/*
 * Copyright (C) 2012 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 05/29/2014|     zhonghua.tuo     |      FR 670064       |email search fun- */
/*           |                      |                      |ction             */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-883410  2015/01/13   xiaolin.li      [Email]Search dysfunction;
 *BUGFIX-1070880  2015/08/19  chao-zhang      [Android5.0][Email] [Monkey][Crash][Monitor] com.tct.email crashed by java.lang.NullPointerException
 *BUGFIX-976187  2015/09/23   junwei-xu       [Android5.0][Email]Search result screen cannot display on landscape
 *BUGFIX-791734  2015/10/27   junwei-xu       [Android L][Email][REG]Email crashed when switch to landscape mode in email detail screen
 *BUGFIX-1815601 2016/03/23   rong-tang       [Email]The keyboard doesn't pop up after slide in the search screen
 ===========================================================================
 */
package com.tct.mail.ui;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView.OnSuggestionListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Switch;
import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.ConversationListContext;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.AccountObserver;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.FolderObserver;
import com.tct.mail.providers.SearchRecentSuggestionsProvider;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.FolderCapabilities;
import com.tct.mail.providers.UIProvider.FolderType;
import com.tct.mail.utils.Utils;
import com.tct.mail.browse.SelectedConversationsActionMenu;

/**
 * Controller to manage the various states of the {@link android.app.ActionBar}.
 */
public class ActionBarController implements ViewMode.ModeChangeListener,
        OnQueryTextListener, OnSuggestionListener, MenuItemCompat.OnActionExpandListener, SelectedConversationsActionMenu.SelectedMenuCallback{

    protected final Context mContext;

    protected ActionBar mActionBar;
    protected ControllableActivity mActivity;
    protected ActivityController mController;
    /**
     * The current mode of the ActionBar and Activity
     */
    protected ViewMode mViewModeController;

    /**
     * The account currently being shown
     */
    private Account mAccount;
    /**
     * TCT: Set protected to be used by extend
     * The folder currently being shown
     */
    protected Folder mFolder;

    protected SearchView mSearchWidget;
    protected MenuItem mSearch;
    protected MenuItem mEmptyTrashItem;
    protected MenuItem mEmptySpamItem;
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
    protected MenuItem mStarSwitch;
    protected Switch mSwitch;
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

    /** True if the current device is a tablet, false otherwise. */
    protected final boolean mIsOnTablet;
    private Conversation mCurrentConversation;

    public static final String LOG_TAG = LogTag.getLogTag();

    private FolderObserver mFolderObserver;
    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/29/2014,FR 670064
    private MenuItem mLocalSearchItem;
    private MenuItem mServiceSearchItem;

    //false is local search,true is service search;
    // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410 MOD_S
    //public static boolean SERVICE_SEARCH_MODE = false;
    public static boolean SERVICE_SEARCH_MODE = true;
    // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410 MOD_E
    private String mOldQueryText = "";
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo
    /** Updates the resolver and tells it the most recent account. */
    private final class UpdateProvider extends AsyncTask<Bundle, Void, Void> {
        final Uri mAccount;
        final ContentResolver mResolver;
        public UpdateProvider(Uri account, ContentResolver resolver) {
            mAccount = account;
            mResolver = resolver;
        }

        @Override
        protected Void doInBackground(Bundle... params) {
            mResolver.call(mAccount, UIProvider.AccountCallMethods.SET_CURRENT_ACCOUNT,
                    mAccount.toString(), params[0]);
            return null;
        }
    }

    private final AccountObserver mAccountObserver = new AccountObserver() {
        @Override
        public void onChanged(Account newAccount) {
            updateAccount(newAccount);
        }
    };

    public ActionBarController(Context context) {
        mContext = context;
        mIsOnTablet = Utils.useTabletUI(context.getResources());
    }

    //TS: rong-tang 2016-03-23 EMAIL BUGFIX-1815601 ADD_S
    /** clear focus which on searchview, it will override by subclass */
    public void clearSearchFocus() {}
    //TS: rong-tang 2016-03-23 EMAIL BUGFIX-1815601 ADD_E

    public void expandSearch() {
        if (mSearch != null) {
            MenuItemCompat.expandActionView(mSearch);
        }
    }

    /**
     * TCT: EmailActionBarView will implement it
     */
    protected void expandSearch(String query, String field) {
    }

    /**

    /**
     * Close the search view if it is expanded.
     */
    public void collapseSearch() {
        if (mSearch != null) {
            MenuItemCompat.collapseActionView(mSearch);
        }
    }


    /**
     * TCT: Check if the Search View is expanded @{
     */
    public boolean isExpandedSearch() {
        if (mSearch != null) {
            return MenuItemCompat.isActionViewExpanded(mSearch);
        }
        return false;
    }
    /** @} */

    /**
     * Get the search menu item.
     */
    protected MenuItem getSearch() {
        return mSearch;
    }

    /**
     * Update star toggle's visibility
     */
    protected void updateStarToggleVisible(boolean isSearchExpanded) {
        boolean supportStarToggle = true;
        if (isSearchExpanded || (mFolder != null && (mFolder.isType(FolderType.STARRED)
                || mFolder.isType(FolderType.TRASH) || mFolder.isType(FolderType.OUTBOX)))) {
            supportStarToggle = false;
        }
        if (mStarSwitch != null) {
            mStarSwitch.setVisible(supportStarToggle);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mEmptyTrashItem = menu.findItem(R.id.empty_trash);
        mEmptySpamItem = menu.findItem(R.id.empty_spam);
        mSearch = menu.findItem(R.id.search);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        mStarSwitch = menu.findItem(R.id.menu_star_toggle);
        if (mStarSwitch != null) {
            mSwitch = (Switch) mStarSwitch.getActionView().findViewById(R.id.star_toggle);
            mSwitch.setOnCheckedChangeListener(mController);
        }
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

        if (mSearch != null) {
            mSearchWidget = (SearchView) MenuItemCompat.getActionView(mSearch);
            MenuItemCompat.setOnActionExpandListener(mSearch, this);
            SearchManager searchManager = (SearchManager) mActivity.getActivityContext()
                    .getSystemService(Context.SEARCH_SERVICE);
            if (searchManager != null && mSearchWidget != null) {
                SearchableInfo info = searchManager.getSearchableInfo(mActivity.getComponentName());
                /// TCT: The SearchableInfo may be customized by Email, and we need checking its validation.
                final String authority = getContext().getString(R.string.suggestions_authority);
                if (info != null && authority.equals(info.getSuggestAuthority())) {
                    mSearchWidget.setSearchableInfo(info);
                    mSearchWidget.setOnSuggestionListener(this);
                }
                //TS: Gantao 2016-01-25 EMAIL BUGFIX-1489887 ADD-S
                //Set focusable false in case always show the search suggestion
                mSearchWidget.setFocusable(false);
                //TS: Gantao 2016-01-25 EMAIL BUGFIX-1489887 ADD-E
                mSearchWidget.setOnQueryTextListener(this);
//                mSearchWidget.setOnSuggestionListener(this);
                mSearchWidget.setIconifiedByDefault(true);
            }
        }
        //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
        mLocalSearchItem = menu.findItem(R.id.local_search);
        mServiceSearchItem = menu.findItem(R.id.service_search);
        //[FEATURE]-Add-END by CDTS.zhonghua.tuo

        // the menu should be displayed if the mode is known
        return getMode() != ViewMode.UNKNOWN;
    }
    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
    public String getSearchWidgetText() {
        if (mSearchWidget != null) {
            return mSearchWidget.getQuery().toString();
        }
        return "";
    }
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo
    public int getOptionsMenuId() {
        switch (getMode()) {
            case ViewMode.UNKNOWN:
                return R.menu.conversation_list_menu;
            case ViewMode.CONVERSATION:
                return R.menu.conversation_actions;
            case ViewMode.CONVERSATION_LIST:
                return R.menu.conversation_list_menu;
            case ViewMode.SEARCH_RESULTS_LIST:
                return R.menu.conversation_list_search_results_actions;
            case ViewMode.SEARCH_RESULTS_CONVERSATION:
                return R.menu.conversation_actions;
            case ViewMode.WAITING_FOR_ACCOUNT_INITIALIZATION:
                return R.menu.wait_mode_actions;
        }
        LogUtils.wtf(LOG_TAG, "Menu requested for unknown view mode");
        return R.menu.conversation_list_menu;
    }

    public void initialize(ControllableActivity activity, ActivityController callback,
            ActionBar actionBar) {
        mActionBar = actionBar;
        mController = callback;
        mActivity = activity;

        mFolderObserver = new FolderObserver() {
            @Override
            public void onChanged(Folder newFolder) {
                onFolderUpdated(newFolder);
            }
        };
        // Return values are purposely discarded. Initialization happens quite early, and we don't
        // have a valid folder, or a valid list of accounts.
        mFolderObserver.initialize(mController);
        updateAccount(mAccountObserver.initialize(activity.getAccountController()));
    }

    private void updateAccount(Account account) {
        final boolean accountChanged = mAccount == null || !mAccount.uri.equals(account.uri);
        mAccount = account;
        if (mAccount != null && accountChanged) {
            final ContentResolver resolver = mActivity.getActivityContext().getContentResolver();
            final Bundle bundle = new Bundle(1);
            bundle.putParcelable(UIProvider.SetCurrentAccountColumns.ACCOUNT, account);
            final UpdateProvider updater = new UpdateProvider(mAccount.uri, resolver);
            updater.execute(bundle);
            setFolderAndAccount();
        }
    }

    /**
     * Called by the owner of the ActionBar to change the current folder.
     */
    public void setFolder(Folder folder) {
        mFolder = folder;
        setFolderAndAccount();
    }

    public void onDestroy() {
        //TS: junwei-xu 2015-09-23 EMAIL BUGFIX-976187 ADD_S
        //Note: release controller to ensure local search will not be executed after rotate screen
        mController = null;
        //TS: junwei-xu 2015-09-23 EMAIL BUGFIX-976187 ADD_E
        if (mFolderObserver != null) {
            mFolderObserver.unregisterAndDestroy();
            mFolderObserver = null;
        }
        mAccountObserver.unregisterAndDestroy();
    }

    @Override
    public void onViewModeChanged(int newMode) {
        mActivity.supportInvalidateOptionsMenu();
        // Check if we are either on a phone, or in Conversation mode on tablet. For these, the
        // recent folders is enabled.
        switch (getMode()) {
            case ViewMode.UNKNOWN:
                break;
            case ViewMode.CONVERSATION_LIST:
                showNavList();
                break;
            case ViewMode.SEARCH_RESULTS_CONVERSATION:
                mActionBar.setDisplayHomeAsUpEnabled(true);
                setEmptyMode();
                break;
            case ViewMode.CONVERSATION:
            case ViewMode.AD:
                closeSearchField();
                mActionBar.setDisplayHomeAsUpEnabled(true);
                setEmptyMode();
                break;
            case ViewMode.WAITING_FOR_ACCOUNT_INITIALIZATION:
                // We want the user to be able to switch accounts while waiting for an account
                // to sync.
                showNavList();
                break;
        }
    }

    /**
     * Close the search query entry field to avoid keyboard events, and to restore the actionbar
     * to non-search mode.
     */
    private void closeSearchField() {
        if (mSearch == null) {
            return;
        }
        mSearch.collapseActionView();
    }

    protected int getMode() {
        if (mViewModeController != null) {
            return mViewModeController.getMode();
        } else {
            return ViewMode.UNKNOWN;
        }
    }

    /**
     * Helper function to ensure that the menu items that are prone to variable changes and race
     * conditions are properly set to the correct visibility
     */
    public void validateVolatileMenuOptionVisibility() {
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mEmptyTrashItem != null) {
            mEmptyTrashItem.setVisible(mAccount != null && mFolder != null
                    && mAccount.supportsCapability(AccountCapabilities.EMPTY_TRASH)
                    && mFolder.isTrash() && mFolder.totalCount > 0
                    && (mController != null && (mController.getConversationListCursor() == null
                    || mController.getConversationListCursor().getCount() > 0)));
        }
        if (mEmptySpamItem != null) {
            mEmptySpamItem.setVisible(mAccount != null && mFolder != null
                    && mAccount.supportsCapability(AccountCapabilities.EMPTY_SPAM)
                    && mFolder.isType(FolderType.SPAM) && mFolder.totalCount > 0
                    && (mController != null && (mController.getConversationListCursor() == null
                    || mController.getConversationListCursor().getCount() > 0)));
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // We start out with every option enabled. Based on the current view, we disable actions
        // that are possible.
        LogUtils.d(LOG_TAG, "ActionBarView.onPrepareOptionsMenu().");

        //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //boolean searchEnhance = mContext.getResources().getBoolean(R.bool.feature_email_search_enhance_on);
        boolean searchEnhance = PLFUtils.getBoolean(mContext, "feature_email_search_enhance_on");
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        if (mLocalSearchItem != null) {
            mLocalSearchItem.setVisible(mAccount != null && mFolder != null
                    && SERVICE_SEARCH_MODE && searchEnhance);
        }
        if (mServiceSearchItem != null) {
            mServiceSearchItem.setVisible(mAccount != null && mFolder != null
                    && mAccount.supportsCapability(AccountCapabilities.SERVER_SEARCH)
                    && !SERVICE_SEARCH_MODE && searchEnhance);
        }
        //[FEATURE]-Add-END by CDTS.zhonghua.tuo
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD
        //NOTE: Check if mController is null
        if (mController != null && mController.shouldHideMenuItems()) {
            // Shortcut: hide all menu items if the drawer is shown
            final int size = menu.size();

            for (int i = 0; i < size; i++) {
                final MenuItem item = menu.getItem(i);
                item.setVisible(false);
            }
            return false;
        }
        validateVolatileMenuOptionVisibility();

        switch (getMode()) {
            case ViewMode.CONVERSATION:
            case ViewMode.SEARCH_RESULTS_CONVERSATION:
                // We update the ActionBar options when we are entering conversation view because
                // waiting for the AbstractConversationViewFragment to do it causes duplicate icons
                // to show up during the time between the conversation is selected and the fragment
                // is added.
                setConversationModeOptions(menu);
                break;
            case ViewMode.CONVERSATION_LIST:
                // Show search if the account supports it
                Utils.setMenuItemVisibility(menu, R.id.search, mAccount.supportsSearch());//[FEATURE]-Add-BEGIN by TSNJ Zhenhua.Fan,10/23/2014,PR 728378
                //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
                // Hide star toggle if not support it
                updateStarToggleVisible(isExpandedSearch());
                //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
                break;
            case ViewMode.SEARCH_RESULTS_LIST:
                // Hide compose and search
                Utils.setMenuItemVisibility(menu, R.id.compose, false);
                Utils.setMenuItemVisibility(menu, R.id.search, false);
                LogUtils.logFeature(LogTag.SEARCH_TAG, "EmailActionBarView onPrepareOptionsMenu SEARCH_RESULTS_LIST");
                break;
        }

        return false;
    }

    /**
     * Put the ActionBar in List navigation mode.
     */
    private void showNavList() {
        setTitleModeFlags(ActionBar.DISPLAY_SHOW_TITLE);
        setFolderAndAccount();
    }

    private void setTitle(String title) {
        if (!TextUtils.equals(title, mActionBar.getTitle())) {
            mActionBar.setTitle(title);
        }
    }

    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
    public void setCheckStatus(boolean checked) {
        if (mStarSwitch != null && mSwitch != null) {
            mSwitch.setChecked(checked);
        }
    }
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

    /**
     * Set the actionbar mode to empty: no title, no subtitle, no custom view.
     */
    protected void setEmptyMode() {
        // Disable title/subtitle and the custom view by setting the bitmask to all off.
        setTitleModeFlags(0);
    }

    /**
     * Removes the back button from being shown
     */
    public void removeBackButton() {
        if (mActionBar == null) {
            return;
        }
        // Remove the back button but continue showing an icon.
        final int mask = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME;
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, mask);
        mActionBar.setHomeButtonEnabled(false);
    }

    public void setBackButton() {
        if (mActionBar == null) {
            return;
        }
        // Show home as up, and show an icon.
        final int mask = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME;
        mActionBar.setDisplayOptions(mask, mask);
        mActionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mSearch != null) {
            MenuItemCompat.collapseActionView(mSearch);
            mSearchWidget.setQuery("", false);
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null) {
            mController.executeSearch(query.trim());
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //[FEATURE]-Mod-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
        //if (!SERVICE_SEARCH_MODE /*&& mMode == ViewMode.SEARCH_RESULTS_LIST */ && !(newText.trim().equals(mOldQueryText.trim()))) {
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null && !TextUtils.isEmpty(newText) && !SERVICE_SEARCH_MODE &&
                getMode() == ViewMode.SEARCH_RESULTS_LIST && !(newText.trim().equalsIgnoreCase(mOldQueryText.trim()))) {
            mController.executeLocalSearch(newText.trim());
            mOldQueryText = newText;
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        return true;
        //[FEATURE]-Mod-END by CDTS.zhonghua.tuo
    }

    // Next two methods are called when search suggestions are clicked.
    @Override
    public boolean onSuggestionSelect(int position) {
        return onSuggestionClick(position);
    }

    @Override
    public boolean onSuggestionClick(int position) {
        final Cursor c = mSearchWidget.getSuggestionsAdapter().getCursor();
        final boolean haveValidQuery = (c != null) && c.moveToPosition(position);
        if (!haveValidQuery) {
            LogUtils.d(LOG_TAG, "onSuggestionClick: Couldn't get a search query");
            // We haven't handled this query, but the default behavior will
            // leave EXTRA_ACCOUNT un-populated, leading to a crash. So claim
            // that we have handled the event.
            return true;
        }
        collapseSearch();
        // what is in the text field
        String queryText = mSearchWidget.getQuery().toString();
        // What the suggested query is
        String query = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        // If the text the user typed in is a prefix of what is in the search
        // widget suggestion query, just take the search widget suggestion
        // query. Otherwise, it is a suffix and we want to remove matching
        // prefix portions.
        if (!TextUtils.isEmpty(queryText) && query.indexOf(queryText) != 0) {
            final int queryTokenIndex = queryText
                    .lastIndexOf(SearchRecentSuggestionsProvider.QUERY_TOKEN_SEPARATOR);
            if (queryTokenIndex > -1) {
                queryText = queryText.substring(0, queryTokenIndex);
            }
            // Since we auto-complete on each token in a query, if the query the
            // user typed up until the last token is a substring of the
            // suggestion they click, make sure we don't double include the
            // query text. For example:
            // user types john, that matches john palo alto
            // User types john p, that matches john john palo alto
            // Remove the first john
            // Only do this if we have multiple query tokens.
            if (queryTokenIndex > -1 && !TextUtils.isEmpty(query) && query.contains(queryText)
                    && queryText.length() < query.length()) {
                int start = query.indexOf(queryText);
                query = query.substring(0, start) + query.substring(start + queryText.length());
            }
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        if (mController != null) {
            mController.executeSearch(query.trim());
        }
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        return true;
    }

    /**
     * Uses the current state to update the current folder {@link #mFolder} and the current
     * account {@link #mAccount} shown in the actionbar. Also updates the actionbar subtitle to
     * momentarily display the unread count if it has changed.
     */
    private void setFolderAndAccount() {
        // Very little can be done if the actionbar or activity is null.
        if (mActionBar == null || mActivity == null) {
            return;
        }
        if (ViewMode.isWaitingForSync(getMode())) {
            // Account is not synced: clear title and update the subtitle.
            setTitle("");
            return;
        }
        // Check if we should be changing the actionbar at all, and back off if not.
        final boolean isShowingFolder = mIsOnTablet || ViewMode.isListMode(getMode());
        if (!isShowingFolder) {
            // It isn't necessary to set the title in this case, as the title view will
            // be hidden
            return;
        }
        if (mFolder == null) {
            // Clear the action bar title.  We don't want the app name to be shown while
            // waiting for the folder query to finish
            setTitle("");
            return;
        }
        setTitle(mFolder.name);
    }


    /**
     * Notify that the folder has changed.
     */
    public void onFolderUpdated(Folder folder) {
        if (folder == null) {
            return;
        }
        /** True if we are changing folders. */
        final boolean changingFolders = (mFolder == null || !mFolder.equals(folder));
        mFolder = folder;
        setFolderAndAccount();
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_S
        //NOTE: Check if mController is null
        final ConversationListContext listContext = mController != null ? mController.getCurrentListContext() : null;
        //TS: junwei-xu 2015-10-27 EMAIL BUGFIX-791734 MOD_E
        if (changingFolders && !ConversationListContext.isSearchResult(listContext)) {
            closeSearchField();
        }
        // make sure that we re-validate the optional menu items
        validateVolatileMenuOptionVisibility();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        // Do nothing. Required as part of the interface, we ar only interested in
        // onMenuItemActionCollapse(MenuItem).
        // Have to return true here. Unlike other callbacks, the return value here is whether
        // we want to suppress the action (rather than consume the action). We don't want to
        // suppress the action.
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        // Have to return true here. Unlike other callbacks, the return value
        // here is whether we want to suppress the action (rather than consume the action). We
        // don't want to suppress the action.
        return true;
    }

    /**
     * Sets the actionbar mode: Pass it an integer which contains each of these values, perhaps
     * OR'd together: {@link ActionBar#DISPLAY_SHOW_CUSTOM} and
     * {@link ActionBar#DISPLAY_SHOW_TITLE}. To disable all, pass a zero.
     * @param enabledFlags
     */
    protected void setTitleModeFlags(int enabledFlags) {
        final int mask = ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM;
        mActionBar.setDisplayOptions(enabledFlags, mask);
    }

    public void setCurrentConversation(Conversation conversation) {
        mCurrentConversation = conversation;
    }

    //We need to do this here instead of in the fragment
    /// TCT: replace all setMenuItemVisibility with setMenuItemVisibilityAndAvailability, cause onlyset the visibility
    /// not enough in some case
    public void setConversationModeOptions(Menu menu) {
        if (mCurrentConversation == null) {
            return;
        }
        final boolean showMarkImportant = !mCurrentConversation.isImportant();
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.mark_important, showMarkImportant
                && mAccount.supportsCapability(UIProvider.AccountCapabilities.MARK_IMPORTANT));
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.mark_not_important, !showMarkImportant
                && mAccount.supportsCapability(UIProvider.AccountCapabilities.MARK_IMPORTANT));
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        // Trashbox need not show mark star and mark unstar
        final boolean isTrashbox = mFolder!= null && mFolder.isType(FolderType.TRASH);
        final boolean showMarkStar = !mCurrentConversation.starred;
        LogUtils.i(LOG_TAG, "in ABC.setConversationModeOptions() conversation [ %s, %s ]", mCurrentConversation.id, mCurrentConversation.starred);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.mark_star, isTrashbox ? false : showMarkStar);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.mark_unstar, isTrashbox ? false : !showMarkStar);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
        //[BUGFIX]-Add-BEGIN by CDTS.chao-zhang,08/19/2015,PR 1070880
        //NOTE: avoid NPE happen during monkey test.
        final boolean isOutbox = mFolder != null && mFolder.isType(FolderType.OUTBOX);
        //[BUGFIX]-Add-BEGIN by CDTS.chaozhang,08/19/2015,PR 1070880
        final boolean showDiscardOutbox = mFolder != null && isOutbox &&
                mCurrentConversation.sendingState == UIProvider.ConversationSendingState.SEND_ERROR;
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.discard_outbox, showDiscardOutbox);
        final boolean showDelete = !isOutbox && mFolder != null &&
                mFolder.supportsCapability(UIProvider.FolderCapabilities.DELETE);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.delete, showDelete);
        // We only want to show the discard drafts menu item if we are not showing the delete menu
        // item, and the current folder is a draft folder and the account supports discarding
        // drafts for a conversation
        final boolean showDiscardDrafts = !showDelete && mFolder != null && mFolder.isDraft() &&
                mAccount.supportsCapability(AccountCapabilities.DISCARD_CONVERSATION_DRAFTS);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.discard_drafts, showDiscardDrafts);
        final boolean archiveVisible = mAccount.supportsCapability(AccountCapabilities.ARCHIVE)
                && mFolder != null && mFolder.supportsCapability(FolderCapabilities.ARCHIVE)
                && !mFolder.isTrash();
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.archive, archiveVisible);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.remove_folder, !archiveVisible && mFolder != null
                && mFolder.supportsCapability(FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES)
                && !mFolder.isProviderFolder()
                && mAccount.supportsCapability(AccountCapabilities.ARCHIVE));
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.move_to, mFolder != null
                && mFolder.supportsCapability(FolderCapabilities.ALLOWS_REMOVE_CONVERSATION));
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.move_to_inbox, mFolder != null
                && mFolder.supportsCapability(FolderCapabilities.ALLOWS_MOVE_TO_INBOX));
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.change_folders, mAccount.supportsCapability(
                UIProvider.AccountCapabilities.MULTIPLE_FOLDERS_PER_CONV));

        final MenuItem removeFolder = menu.findItem(R.id.remove_folder);
        if (mFolder != null && removeFolder != null) {
            removeFolder.setTitle(mActivity.getApplicationContext().getString(
                    R.string.remove_folder, mFolder.name));
        }
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.report_spam,
                mAccount.supportsCapability(AccountCapabilities.REPORT_SPAM) && mFolder != null
                        && mFolder.supportsCapability(FolderCapabilities.REPORT_SPAM)
                        && !mCurrentConversation.spam);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.mark_not_spam,
                mAccount.supportsCapability(AccountCapabilities.REPORT_SPAM) && mFolder != null
                        && mFolder.supportsCapability(FolderCapabilities.MARK_NOT_SPAM)
                        && mCurrentConversation.spam);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.report_phishing,
                mAccount.supportsCapability(AccountCapabilities.REPORT_PHISHING) && mFolder != null
                        && mFolder.supportsCapability(FolderCapabilities.REPORT_PHISHING)
                        && !mCurrentConversation.phishing);
        Utils.setMenuItemVisibilityAndAvailability(menu, R.id.mute,
                mAccount.supportsCapability(AccountCapabilities.MUTE) && mFolder != null
                        && mFolder.supportsCapability(FolderCapabilities.DESTRUCTIVE_MUTE)
                        && !mCurrentConversation.muted);
    }

    public void setViewModeController(ViewMode viewModeController) {
        mViewModeController = viewModeController;
        mViewModeController.addListener(this);
    }

    public Context getContext() {
        return mContext;
    }

    /// TCT: show local/remote search result, EmailActionBarView will override this method.
    protected void updateSearchCount(int count) {}

  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
    @Override
    public void setStarSwitchClickable(boolean clickable) {
        // TODO Auto-generated method stub
    }
  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E

}
