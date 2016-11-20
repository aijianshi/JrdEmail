/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-962861  2015/03/30   zheng.zou      [REG][Android5.0][Email]Characters in search box will lost.
 *BUGFIX-961694  2015/04/21   peng-zhang     [Android5.0][Email] [UI] The 'All, Sender, Receiver, Subject, Body' bar is orange in mail list.
 *FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
 *FR_571122      2015/9/23    yanhua.chen    [Email]Accessibility-Talkback support
 *BUGFIX-791734  2015/10/27   junwei-xu       [Android L][Email][REG]Email crashed when switch to landscape mode in email detail screen
 *BUGFIX-956919  2016/01/13   tianjing.su    [Android L][Email][UE]The font of "Email account" string change to black after rorating screen and the check box style is not consistent after rotating screen..
 *BUGFIX-1554438  2016/02/16  kaifeng.lu     [Email][Ergo]It display 0 when search and not input charaters.
 *BUGFIX-1812029 2015/10/27   rong-tang       [Email]The prompt is incomplete when search in sub-floder
 *BUGFIX-1962384 2016/04/18   jin.dong       [Monkey][Force Close][Monitor][Email]com.tct.email occur FC when do Monkey test
 ===========================================================================
 */
package com.tct.mail.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.tct.email.R;
import com.tct.emailcommon.service.SearchParams;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.ConversationListContext;
import com.tct.mail.browse.SelectedConversationsActionMenu;
import com.tct.mail.ui.ActionBarController;
import com.tct.mail.ui.ViewMode;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.SortHelper;
import com.tct.mail.utils.Throttle;
import com.tct.mail.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * TCT: Base on MailActionBarView, add local search related feature.
 */
public class EmailActionBarController extends ActionBarController implements View.OnClickListener,OnePaneController.Callback{
    //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_ADD
    //private static final String BUNDLE_KEY_ACTION_BAR_SELECTED_FIELD = "ActionBarController.ACTION_BAR_SELECTED_TAB";
    private TabListener mTabListener = new TabListener();
    private String mSearchField;
    private static final String[] SEARCH_FIELD_LIST = { SearchParams.SEARCH_FIELD_ALL, SearchParams.SEARCH_FIELD_FROM,
            SearchParams.SEARCH_FIELD_TO, SearchParams.SEARCH_FIELD_SUBJECT, SearchParams.SEARCH_FIELD_BODY, SearchParams.SEARCH_FIELD_ATTACHMENT };   //TS: zheng.zou 2016-01-26 EMAIL BUGFIX-1247256 MOD
    private static final int INITIAL_FIELD_INDEX = 1;

    private int mLocalSearchResult = 0;
    private TextView mSearchResultCountView;
    private TextView mSearchFiledSpinner;
    private HorizontalScrollView mSearchFieldTabs;
    private MailSearchTabView mSelectedTab;
    private SearchFieldDropdownPopup mSearchFiledDropDown;
    // Indicated user was opening searched conversation, we don't need exit local search mode.
    private boolean mOpeningLocalSearchConversation = false;
    // Indicated user was back from searched conversation, we need restore query text.
    private boolean mBackingLocalSearchConversation = false;

    /// TCT: expandSearch might be called eariler than onCreateOptionsMenu(),
     // in this case, execute this expanding request after creating the search UI
    private String mPendingQuery;
    ///TCT: the flag is set to true if the localsearch execute but the search bar has not expand.
    private boolean mHasPendingQuery = false;

    //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_S
    private boolean mTabChanged = false;
    //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_E

  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
    private boolean mStarSwitchClickable = true;
  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E
    private static final String SEARCHVIEW_QUERYTEXT_FIELD_NAME = "mQueryTextView";
    private MenuItem mSortItem;       //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E

    public EmailActionBarController(Context context) {
        super(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mEmptyTrashItem = menu.findItem(R.id.empty_trash);
        mEmptySpamItem = menu.findItem(R.id.empty_spam);

        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        mStarSwitch = menu.findItem(R.id.menu_star_toggle);
        if (mStarSwitch != null) {
            mSwitch = (Switch) mStarSwitch.getActionView().findViewById(R.id.star_toggle);
            mSwitch.setOnCheckedChangeListener(mController);
          //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
            //If we are selected mode,set star switch is not clickable in case
            //of touch star switch which is not expected
            if (mStarSwitchClickable) {
                mSwitch.setClickable(true);
            } else {
                mSwitch.setClickable(false);
            }
          //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E
        }
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
        mSortItem = menu.findItem(R.id.sort_mail);     //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD

        // If the mode is valid, then set the initial menu
        if (getMode() == ViewMode.UNKNOWN) {
            return false;
        }
        mSearch = menu.findItem(R.id.search);
        if (mSearch != null) {
            mSearch.setActionView(R.layout.local_search_actionbar_view);
            mSearchResultCountView = (TextView) MenuItemCompat.getActionView(mSearch)
                    .findViewById(R.id.result_count);
            mSearchResultCountView.setText(String.valueOf(0));
            mSearchFiledSpinner = (TextView) MenuItemCompat.getActionView(mSearch)
                    .findViewById(R.id.search_field);
            mSearchWidget = (SearchView) MenuItemCompat.getActionView(mSearch)
                    .findViewById(R.id.email_search_view);
            MenuItemCompat.setOnActionExpandListener(mSearch, this);
            mSearch.setOnMenuItemClickListener(new OnSearchItemClickListener());
            if (mSearchWidget != null) {
                mSearchWidget.setOnQueryTextListener(this);
                mSearchWidget.setOnSuggestionListener(this);
                mSearchWidget.setIconifiedByDefault(true);
                mSearchWidget.setQueryHint(getContext().getResources()
                        .getString(R.string.search_hint));
                disableSearchViewActionMode(mSearchWidget);
            }
        }
        return true;
    }

    /**
     * This is a workaround of support.v7 SearchView's action mode issue.
     * Now there is no better way to disable SearchView's action mode. We
     * have to hack in to this object, get the mQueryTextView filed and turn it off.
     * @param searchView SearchView to disable the action mode.
     */
    private void disableSearchViewActionMode(SearchView searchView) {
        Field queryTextViewfield = null;
        try{
            queryTextViewfield = searchView.getClass()
                    .getDeclaredField(SEARCHVIEW_QUERYTEXT_FIELD_NAME);
        } catch (NoSuchFieldException e) {
            LogUtils.e(LOG_TAG, " get SearchView mQueryTextView field failded " +
                        "due to NoSuchFieldException");
        }
        if (queryTextViewfield != null) {
            queryTextViewfield.setAccessible(true);
            TextView queryTextView = null;
            try {
                queryTextView = (TextView) queryTextViewfield.get(searchView);
            } catch (IllegalAccessException e) {
                LogUtils.e(LOG_TAG, "SearchView mQueryTextView field return" +
                        " IllegalAccessException");
            } catch (IllegalArgumentException e) {
                LogUtils.e(LOG_TAG, "SearchView mQueryTextView field return" +
                        " IllegalArgumentException");
            }
            if (queryTextView != null) {
                // replace the default action mode with new one.
                queryTextView.setCustomSelectionActionModeCallback(new Callback() {
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // return false to disable default action mode.
                        return false;
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        // always enable search menu for local searching.
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null && !mController.shouldHideMenuItems()) {
            Utils.setMenuItemVisibility(menu, R.id.search, true);
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        // TCT: if has been local searching(resume activity/pull out drawer), init view content.
        // add mSearch null check, sometimes(in localsearch conversationview mode) the mSearch
        // will be null if we change to landscape. @{
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        ConversationListContext currentListContext = mController != null ? mController.getCurrentListContext() : null;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        if (mSearch != null && currentListContext != null
                && currentListContext.isLocalSearch()) {
            //TS: kaifeng.lu 2016-02-16 EMAIL BUGFIX-1554438 ADD_S
            if( getSearchWidgetText().isEmpty()){
                mSearchResultCountView.setVisibility(View.INVISIBLE);
            }else{
                mSearchResultCountView.setVisibility(View.VISIBLE);
            }
            //TS: kaifeng.lu 2016-02-16 EMAIL BUGFIX-1554438 ADD_E
            mSearchResultCountView.setText(String.valueOf(mLocalSearchResult));
            // TS: yanhua.chen 2015-9-23 EMAIL FR_571122 ADD_S
            mSearchResultCountView.setContentDescription(String.valueOf(mLocalSearchResult));
            // TS: yanhua.chen 2015-9-23 EMAIL FR_571122 ADD_E
            expandSearch(currentListContext.getSearchQuery(), currentListContext.getSearchField());
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
            //NOTE: Check if mController is null
            if (mController != null && !useTabMode(getContext())) {
                initSpinner();
            }
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        }
        /// @}

        /// TCT: restore local search state, it's a little strange, however, the menu item don't expand
        /// when back form conversation view. @{
        if (mBackingLocalSearchConversation) {
            if (currentListContext != null) {
                //Reset local search status.
                LogUtils.logFeature(LogTag.SEARCH_TAG,
                        "onPrepareOptionsMenu reset localsearch, currentListContext [%s]", currentListContext);
                expandSearch(currentListContext.getSearchQuery(), currentListContext.getSearchField());
            }
            mBackingLocalSearchConversation = false;
        }
        /// @}

        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        //Note: Restore star toggle status
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        boolean isChecked = mController != null ? mController.getCurrentStarToggleStatus() : false;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        setCheckStatus(isChecked);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S

        /// TCT: There's a pending expanding search request
        if (mHasPendingQuery) {
            expandSearch(mPendingQuery, mSearchField);
            mPendingQuery = null;
            mHasPendingQuery = false;
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
        if (mSortItem != null) {
            mSortItem.setVisible(mController!=null && !mController.shouldHideMenuItems()
                    && SortHelper.isSortEnabled());
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E
        return result;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        LogUtils.logFeature(LogTag.SEARCH_TAG, "onQueryTextSubmit [%s]", query);
        if (!TextUtils.isEmpty(query)) {
            mLocalSearchThrottle.onEvent();
            // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 ADD_S
            //note: update search context in time, not in delayed search execute
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
            //NOTE: Check if mController is null
            ConversationListContext listContext = mController != null ? mController.getCurrentListContext() : null;
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
            if (listContext != null && listContext.isLocalSearch()
                    && mSearchWidget != null && mSearchWidget.getQuery() != null) {
                listContext.setSearchQueryText(mSearchWidget.getQuery().toString());
            }
            // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 ADD_E
            // Follow JB, hide the input method, when press on the submit/search keyboard.
            if (mActivity != null) {
                final InputMethodManager imm = (InputMethodManager) mActivity
                        .getActivityContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mSearchWidget.getWindowToken(), 0);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // if back from conversation view, don't re-query empty term in conversations list.
        if (mBackingLocalSearchConversation
                && TextUtils.isEmpty(newText)) {
            return true;
        }
        /**
         * Not start local search immediately, use Throttle control the query event.
         */
        LogUtils.logFeature(LogTag.SEARCH_TAG, "onQueryTextChange [%s]", newText);
        mLocalSearchThrottle.onEvent();
        // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 ADD_S
        //note: update search context in time, not in delayed search execute
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        ConversationListContext listContext = mController != null ? mController.getCurrentListContext() : null;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        if (listContext != null && listContext.isLocalSearch()
                && mSearchWidget != null && mSearchWidget.getQuery() != null) {
            listContext.setSearchQueryText(mSearchWidget.getQuery().toString());
        }
        // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 ADD_E
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        boolean result = super.onMenuItemActionExpand(item);
        String listContextQuery = null;
        // backup the list context query, cause onActionViewExpanded would clear query text.
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        ConversationListContext listContext = mController != null ? mController.getCurrentListContext() : null;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        if (listContext != null && listContext.isLocalSearch()) {
            listContextQuery = listContext.getSearchQuery();
        }
        mSearchWidget.onActionViewExpanded();
        mSearchWidget.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_ACTION_SEARCH);
        // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 MOD_S
        if (listContextQuery != null) {
            mSearchWidget.setQuery(listContextQuery, false);
        }
        // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 MOD_E
        // Hide star toggle
        updateStarToggleVisible(true);
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
        if (mSortItem != null) {
            mSortItem.setVisible(false);
            if (SortHelper.getCurrentSort() != SortHelper.getDefaultOrder()) {
                SortHelper.resetCurrentOrder();
                ((AbstractActivityController) mController).sort(SortHelper.getCurrentSort());
            }
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E
        return result;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        ConversationListContext listContext = mController != null ? mController.getCurrentListContext() : null;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        if (mOpeningLocalSearchConversation) {
            removeLocalSearchView();
            return super.onMenuItemActionCollapse(item);
        } else if (listContext != null && listContext.isLocalSearch()) {
            mController.exitLocalSearch();
            // call SearchView's collapsed api to clear focus and query text.
            mSearchWidget.onActionViewCollapsed();
            // TCT: Manual clear the query text, make sure the query text cleared.
            mSearchWidget.setQuery(null, false);
            removeLocalSearchView();
        }
        // Show star toggle
        updateStarToggleVisible(false);
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
        if (mSortItem != null) {
            mSortItem.setVisible(SortHelper.isSortEnabled());
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E
        return super.onMenuItemActionCollapse(item);
    }

    private int mLastViewMode = ViewMode.UNKNOWN;
    /*
     * TCT: Don't exit local search mode, if open message in local search results list.
     * @see com.android.mail.ui.MailActionBarView#onViewModeChanged(int)
     */
    @Override
    public void onViewModeChanged(int newMode) {
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        ConversationListContext listContext = mController != null ? mController.getCurrentListContext() : null;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        if (listContext != null && listContext.isLocalSearch()
                && mLastViewMode == ViewMode.CONVERSATION_LIST && newMode == ViewMode.CONVERSATION) {
            mOpeningLocalSearchConversation = true;
            mBackingLocalSearchConversation = false;
            clearSearchFocus();
        }
        if (listContext != null && listContext.isLocalSearch()
                && mLastViewMode == ViewMode.CONVERSATION && newMode == ViewMode.CONVERSATION_LIST) {
            mOpeningLocalSearchConversation = false;
            mBackingLocalSearchConversation = true;
        }
        mLastViewMode = newMode;
        super.onViewModeChanged(newMode);
    }

    /**
     * Remove focus from the search field to avoid 1. The keyboard popping in and out. 2. The search suggestions shown
     * up.
     */
    @Override
    public void clearSearchFocus() {
        // Remove focus from the search action menu in search results mode so
        // the IME and the suggestions don't get in the way.
        //TS: jin.dong 2016-04-18 EMAIL BUGFIX-1962384 MOD_S
        if (mSearchWidget != null){
            mSearchWidget.clearFocus();
        }
        //TS: jin.dong 2016-04-18 EMAIL BUGFIX-1962384 MOD_E
    }

    /**
     * Expand the local search UI and query the text
     */
    @Override
    public void expandSearch(String query, String field) {
        // TCT: support expand search for specific field
        if (field != null) {
            mSearchField = field;
        } else {
            mSearchField = SEARCH_FIELD_LIST[INITIAL_FIELD_INDEX];
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD
        //NOTE: Check if mController is null
        if (mController != null && mSearch != null) {
            mController.enterLocalSearch(mSearchField);
            initLocalSearchView();
            super.expandSearch();
            // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 MOD_S
            mSearchWidget.setQuery(query, false);
            // TS:zheng.zou 2015-03-30 EMAIL BUGFIX-962861 MOD_E
        } else {
            mPendingQuery = query;
            ///TCT: after search actionbar expand, the pending search will be execute
            // and then this flag will reset.
            mHasPendingQuery = true;
        }
    }

    /**
     * TCT: initialize the tab-styled local search UI
     */
    private void initTabs() {
        if (mSearchFieldTabs != null) {
            //TS: rong-tang 2016-03-16 EMAIL BUGFIX-1812029 ADD_S
            //Note: when enter sub folder in search mode, should reset header height.
            if (mController != null) {
                ConversationListFragment cListFragment = mController.getConversationListFragment();
                if (cListFragment != null) {
                    int headerHeight = (int) mActivity.getApplicationContext().getResources().
                            getDimension(R.dimen.abc_action_bar_default_height_material) * 5 / 3;
                    cListFragment.getListHeader().setVisibleHeight(headerHeight);
                    //ProgressEndTarget should twice as header height.
                    cListFragment.setProgressEndTarget(headerHeight * 2);
                }
            }
            //TS: rong-tang 2016-03-16 EMAIL BUGFIX-1812029 ADD_E
            return;
        }
        Context context = mActivity.getApplicationContext();
        mSearchFieldTabs = new HorizontalScrollView(mContext);
        mSearchFieldTabs.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_S
        if(!mTabChanged){
            mSearchFieldTabs.setBackgroundColor(context.getResources().getColor(R.color.actionbar_color));
        }else{
            mSearchFieldTabs.setBackgroundColor(context.getResources().getColor(R.color.action_mode_background));
        }
        //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_E
        mSearchFieldTabs.setVisibility(View.VISIBLE);
        // backup current field, cause addTab would change mSearchField;
        String currentField = mSearchField;

        // Clear old tabs before we add new tab.
        LogUtils.logFeature(LogTag.SEARCH_TAG,
                "Before initTabs remove old Tabs, current status: search field [%s]", mSearchField);
        mSearchFieldTabs.removeAllViews();

     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_S
        //As for the list layout have been modifie to framelayou for the feature<Auto hide action bar>
        //So for the local search function,we need add some more view in case of overlap phenomenon

        //all layout contains the all view in the SearchFieldTabs
        LinearLayout allLayout = new LinearLayout(context, null, android.R.attr.actionBarTabBarStyle);
        allLayout.setOrientation(LinearLayout.VERTICAL);

        //barHeightLayout is occupy position view,its height is same as toolbar's height
        //it should not been seen by user
        LinearLayout barHeightLayout = new LinearLayout(context, null, android.R.attr.actionBarTabBarStyle);
        barHeightLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                (int)context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material)));
        allLayout.addView(barHeightLayout);

        //layout holds the tab button, which can be seen for user
        LinearLayout layout = new LinearLayout(context, null, android.R.attr.actionBarTabBarStyle);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        String[] searchFieldList = context.getResources().getStringArray(R.array.search_field_list);
        for (int i = 0; i < searchFieldList.length; i++) {
            MailSearchTabView tab = new MailSearchTabView(context, null, 0);
            tab.setText(searchFieldList[i].toUpperCase()); //TS: jin.dong 2016-01-29 EMAIL BUGFIX-1535049 ADD
            tab.setBackgroundResource(R.drawable.tab_indicator_material); //TS: tianjing.su 2016-01-13 EMAIL BUGFIX-956919 ADD
            tab.setTabListener(mTabListener);
            tab.setTag(SEARCH_FIELD_LIST[i]);
            tab.setPosition(i);
            tab.setHeight(mActivity.getSupportActionBar().getHeight() * 2 / 3);
            tab.setGravity(Gravity.CENTER);
            tab.setOnClickListener(this);
            layout.addView(tab, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (currentField.equals(SEARCH_FIELD_LIST[i])) {
                tab.setSelected(true);
                mSelectedTab = tab;
            }
        }
        allLayout.addView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mSearchFieldTabs.addView(allLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mSearchFieldTabs.setHorizontalScrollBarEnabled(false);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S
        //The listLayout have been modified to FrameLayout from now
        final FrameLayout listLayout = (FrameLayout) mActivity
                .findViewById(R.id.list_content_view);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_E
        // adding tabs layout dynamic, we place it nearby toolbar which index was 1.
        //Mod by tao.gan. note : list layout have been modified to framelayout, so the parameter index is no user,remove it
        listLayout.addView(mSearchFieldTabs,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        mController.setSearchHeader(mSearchFieldTabs);
        //Set the height of list header view as search bars's height(5/3 of toolbar's height)
        ConversationListFragment cListFragment = mController.getConversationListFragment();
        if (cListFragment != null) {
            int headerHeight = (int) context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material) * 5/3;
            cListFragment.getListHeader().setVisibleHeight(headerHeight);
            //ProgressEndTarget should twice as header height.
            cListFragment.setProgressEndTarget(headerHeight * 2);
        }
     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 MOD_E
        mSearchField = currentField;
    }

    /**
     * TCT: initialize the dropdownlist-style local search UI(for tablet)
     */
    private void initSpinner() {
        ArrayList<String> items = new ArrayList<String>();
        Context context = mActivity.getApplicationContext();
        String[] searchFieldList = context.getResources().getStringArray(R.array.search_field_list);
        for (String field : searchFieldList) {
            field = field.toUpperCase(); //TS: jin.dong 2016-01-29 EMAIL BUGFIX-1535049 ADD
            items.add(field);
        }

        ListAdapter adapter = new ArrayAdapter<String>(context, R.layout.search_fields_spinner, items);

        // field dropdown
        mSearchFiledSpinner.setVisibility(View.VISIBLE);
        //TS: xiannan.zhou 2016-03-02 EMAIL FEATURE-559891 MOD_S
        if(mSearchFiledDropDown == null){
            mSearchFiledDropDown = new SearchFieldDropdownPopup(getContext(), mSearchFiledSpinner);
        }

        if(mSearchFiledDropDown.isShowing()){
            mSearchFiledDropDown.dismiss();
        }
        mSearchFiledDropDown = new SearchFieldDropdownPopup(getContext(), mSearchFiledSpinner);
        mSearchFiledDropDown.setAdapter(adapter);
        //TS: xiannan.zhou 2016-03-02 EMAIL FEATURE-559891 MOD_E

        mSearchFiledSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchFiledDropDown.show();
            }
        });
        for (int i = 0; i < SEARCH_FIELD_LIST.length; i++) {
            if (SEARCH_FIELD_LIST[i].equals(mSearchField)) {
                mSearchFiledSpinner.setText(items.get(i));
                break;
            }
        }
    }

    /**
     * TCT: record current selected tab.
     */
    public class TabListener {
        /* The following are each of the ActionBar.TabListener callbacks */
        public void onTabSelected(MailSearchTabView tab) {
            mSearchField = (String) tab.getTag();
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
            //NOTE: Check if mController is null
            if (mController != null) {
                mController.enterLocalSearch(mSearchField);
            }
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
            String query = mSearchWidget.getQuery().toString();
            if (!TextUtils.isEmpty(query)) {
                onQueryTextChange(query);
            }
        }
    }

    private boolean onPopupFieldsItemSelected(int itemPosition, View v) {
        String searchField = SEARCH_FIELD_LIST[itemPosition];
        mSearchFiledSpinner.setText(((TextView) v).getText());
        mSearchField = searchField;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null) {
            mController.enterLocalSearch(mSearchField);
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        onQueryTextChange(mSearchWidget.getQuery().toString());
        return true;
    }

    // Based on Spinner.DropdownPopup
    private class SearchFieldDropdownPopup extends ListPopupWindow {
        public SearchFieldDropdownPopup(Context context, View anchor) {
            super(context);
            setAnchorView(anchor);
            setModal(true);
            setPromptPosition(POSITION_PROMPT_ABOVE);
            setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    onPopupFieldsItemSelected(position, v);
                    dismiss();
                }
            });
        }

        @Override
        public void show() {
            setWidth(getContext().getResources().getDimensionPixelSize(R.dimen.search_fields_popup_width));
            setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
            super.show();
            // List view is instantiated in super.show(), so we need to do this after...
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    /**
     * TCT: call to update search result count.
     */
    public void updateSearchCount(int count) {
        //TS: kaifeng.lu 2016-02-16 EMAIL BUGFIX-1554438 MOD_S
        if( getSearchWidgetText().isEmpty()){
            mSearchResultCountView.setVisibility(View.INVISIBLE);
        }else{
            mSearchResultCountView.setVisibility(View.VISIBLE);
            mLocalSearchResult = count;
            mSearchResultCountView.setText(String.valueOf(mLocalSearchResult));
        }
        //TS: kaifeng.lu 2016-02-16 EMAIL BUGFIX-1554438 MOD_E
    }

    /**
     * TCT: get the query term if current search field were "body" or "all", otherwise returns null
     */
    public String getQueryTermIfSearchBody() {
        String selectedTab;
        if (!TextUtils.isEmpty(mSearchField)) {
            selectedTab = mSearchField;
        } else {
            return null;
        }

        return (selectedTab.equalsIgnoreCase(SearchParams.SEARCH_FIELD_BODY) || selectedTab
                .equalsIgnoreCase(SearchParams.SEARCH_FIELD_ALL)) ? mSearchWidget.getQuery().toString() : null;
    }

    private boolean useTabMode(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        boolean isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT;
        boolean isOnePane = mController.isDrawerEnabled();
        return isPortrait && isOnePane;
    }

    private void initLocalSearchView() {
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null && useTabMode(mActivity.getApplicationContext())) {
            initTabs();
        } else {
            initSpinner();
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
    }

    private void removeLocalSearchView() {
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null && useTabMode(mActivity.getApplicationContext())) {
          //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S
            //The listLayout have been modfied to FrameLayout from now
            final FrameLayout listLayout = (FrameLayout) mActivity
                    .findViewById(R.id.list_content_view);
          //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_E
            listLayout.removeView(mSearchFieldTabs);
          //TS: tao.gan 2015-10-10 EMAIL FEATURE-559891 ADD_S
            mController.setSearchHeader(null);
            //Set the height of list header view as toolbar 's height
            int headerHeight = (int) mActivity.getApplicationContext().getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
            mController.getConversationListFragment().getListHeader().setVisibleHeight(headerHeight);
            //Set the progress bar's height to the top(twice as header view's height);
            mController.getConversationListFragment().setProgressEndTarget(2*headerHeight);
          //TS: tao.gan 2015-10-10 EMAIL FEATURE-559891 ADD_E
            mSelectedTab = null;
            mSearchFieldTabs = null;
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        mSearchFiledSpinner.setVisibility(View.GONE);
    }

    private class OnSearchItemClickListener implements OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mSearchField = SEARCH_FIELD_LIST[INITIAL_FIELD_INDEX];
            updateSearchCount(0);
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
            //NOTE: Check if mController is null
            if (mController != null ) {
                mController.enterLocalSearch(mSearchField);
                initLocalSearchView();
            }
            //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
            return true;
        }
    }

    public void selectTab(MailSearchTabView tab) {
        if (mSelectedTab != tab) {
            if (mSelectedTab != null) {
                mSelectedTab.setSelected(false);
            }
            mSelectedTab = tab;
            if (mSelectedTab != null) {
                mSelectedTab.getCallback().onTabSelected(mSelectedTab);
            }
        }
    }

    /* TCT: just listening search fields tab view clicking, and do some reactions.
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        MailSearchTabView tab = (MailSearchTabView)v;
        tab.setSelected(true);
        selectTab(tab);
    }

    /**
     * TCT: An search field tab text view, which inherit system's actionbar tab style
     * and could be selected
     *
     */
    public class MailSearchTabView extends TextView {

        private TabListener mCallback;
        private Object mTag;
        private Drawable mIcon;
        private CharSequence mText;
        private CharSequence mContentDesc;
        private int mPosition = -1;
        private View mCustomView;

        public MailSearchTabView(Context context) {
            super(context);
        }

        public MailSearchTabView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, android.R.attr.actionBarTabStyle);
        }

        public TabListener getCallback() {
            return mCallback;
        }

        public void setTabListener(TabListener callback) {
            mCallback = callback;
        }

        public int getPosition() {
            return mPosition;
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        public void select() {
            selectTab(this);
        }
    }

    /**
     * Use throttle to avoid throw too many query, when user keep input or delete query.
     * same to a delay when query changed.
     */
    private static final int MIN_QUERY_INTERVAL = 200;
    private static final int MAX_QUERY_INTERVAL = 500;

    private final Throttle mLocalSearchThrottle = new Throttle("EmailActionBarView",
            new Runnable() {
                @Override public void run() {
                    if (null != mController && null != mSearchWidget
                            && null != mSearchWidget.getQuery()) {
                        mController.executeLocalSearch(mSearchWidget.getQuery().toString());
                    }
                }
            }, Utility.getMainThreadHandler(),
            MIN_QUERY_INTERVAL, MAX_QUERY_INTERVAL);

    //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_S
    @Override
    public void change(int count) {
        if(null != mController){
             ConversationListContext listContext = mController.getCurrentListContext();
             if (null != listContext && listContext.isLocalSearch()) {
                  if(null != mSearchFieldTabs) {
                       if(count == 0) {
                           mSearchFieldTabs.setBackgroundColor(mActivity.getApplicationContext().getResources().getColor(R.color.actionbar_color));
                           mTabChanged = false;
                       } else {
                           mSearchFieldTabs.setBackgroundColor(mActivity.getApplicationContext().getResources().getColor(R.color.action_mode_background));
                           mTabChanged = true;
                       }
                 }else{
                       if(count > 0){
                           mTabChanged = true;
                       }
                }
             }else{
                 mTabChanged = false;
             }
        }
    }
    //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_961694 MOD_E
  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
    /*
     *If we are selected mode,set star switch is not clickable in case
     *of touch star switch which is not expected
     */
    @Override
    public void setStarSwitchClickable(boolean clickable) {
        // TODO Auto-generated method stub
        mStarSwitchClickable = clickable;
        if(mSwitch == null) {
            return;
        }
        LogUtils.i(LogUtils.TAG,"Set star switch clickable : " + clickable);
        mSwitch.setClickable(clickable);
    }
  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E
}
