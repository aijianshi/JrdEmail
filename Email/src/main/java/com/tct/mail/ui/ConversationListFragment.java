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
/******************************************************************************/
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 05/28/2014|     zhonghua.tuo     |      FR 670064       |email search fun- */
/*           |                      |                      |ction             */
/* ----------|----------------------|----------------------|----------------- */
/* 04/12/2014|     Zhenhua.Fan      |      PR 861468       |email search can't*/
/*           |                      |                      |show empty view   */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin    Modify the package conflict
 *BUGFIX-864636  2014/12/16   wenggangjin   [Android5.0][Email] Always freshing content in Airplane mode
 *BUGFIX-881447  2014/12/31   wenggangjin   [Email]Can't click "retry" icon
 *BUGFIX-884395  2014/1/5     junwei-xu     [Email]When drop down to refresh mails,screen continuous refresh does not stop
 *BUGFIX-901969  2015/01/20   xiaolin.li    [Android5.0][Email][Monkey][Crash]com.tct.email crashs at com.tct.mail.browse.ConversationCursor$UnderlyingCursorWrapper.getInnerUri
 *BUGFIX-898277  2015/01/21   chenyanhua    [Email]During the search interface display abnormal
 *BUGFIX-908180  2015-01-26   wenggangjin     [Monitor][Email]Flash back when double tap Email notification bar
 *BUGFIX-898211  2015/1/26    junwei-xu       [Email]Refresh all time in the Trash box even no network connect
 *BUGFIX-889859  2015/1/26    junwei-xu      [Email]Can't show 3th level folder when no single mail in 2nd level folder
 *BUGFIX-930431  2015-02-13   wenggangjin     [Monkey][Crash]com.tct.email java.lang.NullPointerException
 *BUGFIX-941160  2015-03-13   wenggangjin     [Scenario Test][Email][FC][monitor]Email force close happen after switch email account
 *BUGFIX-943354  2015-03-17   zhonghua.tuo    [Android5.0][Email] It show always freshing mail list in combined view when no network.
 *BUGFIX-942413  2015-03-17   wenggangjin     [Android5.0][Email]Can't refresh after moving email to other folder
 *BUGFIX_952327  2015-03-18   gengkexue     Corporate Email keep loading mails non stop
 *BUGFIX_958910  2015-03-26   zheng.zou     [Android5.0][Email]No "Search in Server" when the local search result is "0"
 *BUGFIX_959144  2015-03-31   gengkexue     [Android5.0][Email][Monitor] Cannot load mail in mail list.
 *BUGFIX-963257   2015/04/01  zhonghua-tuo     [Android5.0][Email]Two 'no connection" reminder.
 *BUGFIX-963963   2015/04/01  zhonghua-tuo     [Android5.0][Email]'No connection' flash twice in Sent when no network.
 *BUGFIX-971924   2015/04/01  peng-zhang     [Android5.0][Email][Monitor]Email inbox is empty when we refresh the folder.
 *BUGFIX-996904   2015/05/30  zheng.zou      [Email]multi-selection tap on title
 *BUGFIX-1013190   2015/06/04  chaozhang    [Monitor][Android5.0][Email]"Couldn't sign in" always display when enter email.
 *BUGFIX-1019276  2014/06/09  junwei-xu    [Email][SW]Email FC when search email on server
 *BUGFIX-413164   2014/07/07  xujian       [Email]Will pop up prompt 'result 0' during search a mail from server.
 *BUGFIX-425517   2014/07/07  chaozhang      [Email]Email has stopped when slide screen in email(once).
 *BUGFIX-1047784   2014/07/20  chaozhang   [Android5.0][Email] [Monkey][Crash][Monitor]com.tct.email crashed by Short Msg: java.lang.ClassCastException
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *BUGFIX_1111531 2015/11/12   lin-zhou     [Monkey][Crash][Email] 'com.tct.email' crashes during system monkey test
 *BUGFIX-1193902 2015/12/25   junwei-xu     [Android 6.0][Email]search no result page flashed
 *FR_1098700     2015/10/28   yanhua.chen   <18433><EM1>EMAIL
 *FEATURE-1804126 2016/03/14  tianjing.su     [Email]Show warning to user if the version of Exchange is not same with Email
 *BUGFIX-1815601 2016/03/23   rong-tang     [Email]The keyboard doesn't pop up after slide in the search screen
 *BUGFIX-1862349 2016/03/28   tao.gan     [GAPP][Monitor][Forse Close][Email]Email appears FC when open the email in outbox.
 ===========================================================================
 */
package com.tct.mail.ui;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.ImmutableList;
import com.tct.fw.google.common.collect.ImmutableList;
import com.tct.mail.ConversationListContext;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.analytics.AnalyticsTimer;
import com.tct.mail.browse.ConversationCursor;
import com.tct.mail.browse.ConversationItemView;
import com.tct.mail.browse.ConversationItemViewModel;
import com.tct.mail.browse.ConversationListFooterView;
import com.tct.mail.browse.ToggleableItem;
import com.tct.mail.content.ObjectCursorLoader;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.AccountObserver;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.FolderObserver;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.ConversationListIcon;
import com.tct.mail.providers.UIProvider.FolderCapabilities;
import com.tct.mail.providers.UIProvider.Swipe;
import com.tct.mail.ui.SwipeableListView.ListItemSwipedListener;
import com.tct.mail.ui.SwipeableListView.ListItemsRemovedListener;
import com.tct.mail.ui.SwipeableListView.SwipeListener;
import com.tct.mail.ui.ViewMode.ModeChangeListener;
import com.tct.mail.utils.SortHelper;
import com.tct.mail.utils.Utils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static android.view.View.OnKeyListener;


//[FEATURE]-Add-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;

import com.tct.emailcommon.utility.TextUtilities;

import android.database.Cursor;

import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;

import android.content.Loader;

import com.tct.emailcommon.utility.Utility;

import android.net.Uri;
//[FEATURE]-Add-END by TSCD.zhonghua.tuo

//TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD_S
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD_E
/**
 * The conversation list UI component.
 */
public final class ConversationListFragment extends Fragment implements
        OnItemLongClickListener, ModeChangeListener, ListItemSwipedListener, OnRefreshListener,
        SwipeListener, OnKeyListener, AdapterView.OnItemClickListener, /*TCT:*/View.OnTouchListener {
    /** Key used to pass data to {@link ConversationListFragment}. */
    private static final String CONVERSATION_LIST_KEY = "conversation-list";
    /** Key used to keep track of the scroll state of the list. */
    private static final String LIST_STATE_KEY = "list-state";

    private static final String LOG_TAG = LogTag.getLogTag();

    //AM: peng-zhang 2015-04-23 EMAIL BUGFIX_971924 MOD_S
    private static final String STAG = "TctEmail";
    //AM: peng-zhang 2015-04-23 EMAIL BUGFIX_971924 MOD_E

    /** Key used to save the ListView choice mode, since ListView doesn't save it automatically! */
    private static final String CHOICE_MODE_KEY = "choice-mode-key";

    // True if we are on a tablet device
    private static boolean mTabletDevice;

    // Delay before displaying the loading view.
    private static int LOADING_DELAY_MS;
    // Minimum amount of time to keep the loading view displayed.
    private static int MINIMUM_LOADING_DURATION;

    /**
     * Frequency of update of timestamps. Initialized in
     * {@link #onCreate(Bundle)} and final afterwards.
     */
    private static int TIMESTAMP_UPDATE_INTERVAL = 0;

    private ControllableActivity mActivity;

    // Control state.
    private ConversationListCallbacks mCallbacks;

    private final Handler mHandler = new Handler();

    // The internal view objects.
    private SwipeableListView mListView;

    private View mSearchHeaderView;
    private TextView mSearchResultCountTextView;

    /**
     * Current Account being viewed
     */
    private Account mAccount;
    /**
     * Current folder being viewed.
     */
    private Folder mFolder;

    /**
     * A simple method to update the timestamps of conversations periodically.
     */
    private Runnable mUpdateTimestampsRunnable = null;

    private ConversationListContext mViewContext;

    private AnimatedAdapter mListAdapter;

    private ConversationListFooterView mFooterView;
    private ConversationListEmptyView mEmptyView;
    private View mLoadingView;
    private View mConversationUpdateTimeView;
    private ErrorListener mErrorListener;
    private FolderObserver mFolderObserver;
    private DataSetObserver mConversationCursorObserver;
    private ConversationListHeader mListHeader;
  //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_S
    private MyReceiver myReceiver = new MyReceiver();
  //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_E
    //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD_S
    private boolean show = true;
    //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD_E
    private ConversationSelectionSet mSelectedSet;
    private final AccountObserver mAccountObserver = new AccountObserver() {
        @Override
        public void onChanged(Account newAccount) {
            mAccount = newAccount;
            setSwipeAction();
        }
    };
    private ConversationUpdater mUpdater;
    /** Hash of the Conversation Cursor we last obtained from the controller. */
    private int mConversationCursorHash;
    // The number of items in the last known ConversationCursor
    private int mConversationCursorLastCount;
    // State variable to keep track if we just loaded a new list, used for analytics only
    // True if NO DATA has returned, false if we either partially or fully loaded the data
    private boolean mInitialCursorLoading;

    private @IdRes int mNextFocusLeftId;
    // Tracks if a onKey event was initiated from the listview (received ACTION_DOWN before
    // ACTION_UP). If not, the listview only receives ACTION_UP.
    private boolean mKeyInitiatedFromList;

    /** Duration, in milliseconds, of the CAB mode (peek icon) animation. */
    private static long sSelectionModeAnimationDuration = -1;
    // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_S
//    private boolean notSetEmptyViewFocus = true;
//    private boolean notSetListViewFocus = true;
    // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_E
    // Let's ensure that we are only showing one out of the three views at once
    private void showListView() {
        mListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void showEmptyView() {
        mEmptyView.setupEmptyView(
                mFolder, mViewContext.searchQuery, mListAdapter.getBidiFormatter());
        //TS: tao.gan 2015-12-09 EMAIL BUGFIX-1039985 MOD_S
        //Don't set listview invisible in case some UI issue,when we are showing search result list
        //TODO: only do this when we are showing search result list.
//        mListView.setVisibility(View.INVISIBLE);
        //TS: tao.gan 2015-12-09 EMAIL BUGFIX-1039985 MOD_E
        mEmptyView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_S
        //If we show the empty view ,then the action bar should be shown
        mActivity.animateShow(null);
     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_E
        // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_S
//        if(notSetEmptyViewFocus){
//            mEmptyView.setFocusable(true);
//            mEmptyView.setFocusableInTouchMode(true);
//            mEmptyView.requestFocus();
//            notSetEmptyViewFocus = false;
//            }
        // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_E
    }

    private void showLoadingView() {
        mListView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_S
//        mLoadingView.setFocusable(true);
//        mLoadingView.setFocusableInTouchMode(true);
//        mLoadingView.requestFocus();
        // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_E
    }

    private final Runnable mLoadingViewRunnable = new FragmentRunnable("LoadingRunnable", this) {
        @Override
        public void go() {
            if (!isCursorReadyToShow()) {
                mCanTakeDownLoadingView = false;
                showLoadingView();
                mHandler.removeCallbacks(mHideLoadingRunnable);
                mHandler.postDelayed(mHideLoadingRunnable, MINIMUM_LOADING_DURATION);
            }
            mLoadingViewPending = false;
        }
    };

    private final Runnable mHideLoadingRunnable = new FragmentRunnable("CancelLoading", this) {
        @Override
        public void go() {
            mCanTakeDownLoadingView = true;
            if (isCursorReadyToShow()) {
                hideLoadingViewAndShowContents();
            }
        }
    };

    // Keep track of if we are waiting for the loading view. This variable is also used to check
    // if the cursor corresponding to the current folder loaded (either partially or completely).
    private boolean mLoadingViewPending;
    private boolean mCanTakeDownLoadingView;

    /**
     * If <code>true</code>, we have restored (or attempted to restore) the list's scroll position
     * from when we were last on this conversation list.
     */
    private boolean mScrollPositionRestored = false;
    private MailSwipeRefreshLayout mSwipeRefreshWidget;

    /**
     * Constructor needs to be public to handle orientation changes and activity
     * lifecycle events.
     */
    public ConversationListFragment() {
        super();
    }

    @Override
    public void onBeginSwipe() {
        mSwipeRefreshWidget.setEnabled(false);
    }

    @Override
    public void onEndSwipe() {
        mSwipeRefreshWidget.setEnabled(true);
    }
    //[FEATURE]-Add-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
    private View mlocalSearchHeader;
    private TextView mLocalSearchHeaderText;
    private TextView mLocalSearchHeaderCount;
    private Button mLocalSearchHeaderAll;
    private Button mLocalSearchHeaderFrom;
    private Button mLocalSearchHeaderTo;
    private Button mLocalSearchHeaderSubject;
    public static int localSearchField = 0;

    private class LocalSearchListener implements OnClickListener {

        @Override
        public void onClick(View btn) {
            // TODO Auto-generated method stub
            switch(btn.getId()) {
                case R.id.local_search_all:
                    localSearchField = UIProvider.LOCAL_SEARCH_ALL;
                    initLocalSearchButton(mLocalSearchHeaderAll);
                    break;
                case R.id.local_search_from:
                    localSearchField = UIProvider.LOCAL_SEARCH_FROM;
                    initLocalSearchButton(mLocalSearchHeaderFrom);
                    break;
                case R.id.local_search_to:
                    localSearchField = UIProvider.LOCAL_SEARCH_TO;
                    initLocalSearchButton(mLocalSearchHeaderTo);
                    break;
                case R.id.local_search_subject:
                    localSearchField = UIProvider.LOCAL_SEARCH_SUBJECT;
                    initLocalSearchButton(mLocalSearchHeaderSubject);
                    break;
                default:
                    localSearchField = UIProvider.LOCAL_SEARCH_ALL;
                    break;
            }
            reFreshLocalSearch(mViewContext.searchQuery);
        }
    }

    public void reFreshLocalSearch(String query){
        if (mAccount.searchUri != null) {
            final Uri.Builder searchBuilder = mAccount.searchUri.buildUpon();
            searchBuilder.appendQueryParameter(UIProvider.SearchQueryParameters.QUERY, query);
            final Uri searchUri = searchBuilder.build();
           //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,101/07/2015,PR 893304,close cursor
            Cursor cursor = mActivity.getActivityContext().getContentResolver().query(searchUri, UIProvider.FOLDERS_PROJECTION,
                    null, null, null);
            if (cursor != null) {
                cursor.close();
            }
            //[BUGFIX]-Mod-END by TCTNB.caixia.chen
            mViewContext = ConversationListContext.forSearchQuery(mAccount,mFolder,query);
            onConversationListStatusUpdated();
            requestListRefresh();
        }
    }

    private void initLocalSearchButton(Button btn) {
        switch(btn.getId()) {
            case R.id.local_search_all:
                mLocalSearchHeaderAll.setTextColor(getResources().getColorStateList(R.color.message_search_match_button));
                mLocalSearchHeaderFrom.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderTo.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderSubject.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                break;
            case R.id.local_search_from:
                mLocalSearchHeaderAll.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderFrom.setTextColor(getResources().getColorStateList(R.color.message_search_match_button));
                mLocalSearchHeaderTo.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderSubject.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                break;
            case R.id.local_search_to:
                mLocalSearchHeaderAll.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderFrom.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderTo.setTextColor(getResources().getColorStateList(R.color.message_search_match_button));
                mLocalSearchHeaderSubject.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                break;
            case R.id.local_search_subject:
                mLocalSearchHeaderAll.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderFrom.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderTo.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderSubject.setTextColor(getResources().getColorStateList(R.color.message_search_match_button));
                break;
            default:
                mLocalSearchHeaderAll.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderFrom.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderTo.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                mLocalSearchHeaderSubject.setTextColor(getResources().getColorStateList(R.color.message_search_button));
                break;
        }
    }

    private void initLocalSearchHeader() {
        mlocalSearchHeader = mActivity.findViewById(R.id.local_search_header);
        mLocalSearchHeaderText = (TextView) mActivity.findViewById(R.id.local_search_header_text);
        mLocalSearchHeaderCount = (TextView) mActivity.findViewById(R.id.local_search_count);
        mLocalSearchHeaderAll = (Button) mActivity.findViewById(R.id.local_search_all);
        mLocalSearchHeaderFrom = (Button) mActivity.findViewById(R.id.local_search_from);
        mLocalSearchHeaderTo = (Button) mActivity.findViewById(R.id.local_search_to);
        mLocalSearchHeaderSubject = (Button) mActivity.findViewById(R.id.local_search_subject);
        mLocalSearchHeaderAll.setOnClickListener(new LocalSearchListener());
        mLocalSearchHeaderFrom.setOnClickListener(new LocalSearchListener());
        mLocalSearchHeaderTo.setOnClickListener(new LocalSearchListener());
        mLocalSearchHeaderSubject.setOnClickListener(new LocalSearchListener());
    }
    //[FEATURE]-Add-END by TSCD.zhonghua.tuo

    private class ConversationCursorObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            onConversationListStatusUpdated();
        }
    }

    /**
     * Creates a new instance of {@link ConversationListFragment}, initialized
     * to display conversation list context.
     */
    public static ConversationListFragment newInstance(ConversationListContext viewContext) {
        final ConversationListFragment fragment = new ConversationListFragment();
        final Bundle args = new Bundle(1);
        args.putBundle(CONVERSATION_LIST_KEY, viewContext.toBundle());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Show the header if the current conversation list is showing search
     * results.
     */
    private void updateSearchResultHeader(int count) {
        if (mActivity == null || mSearchHeaderView == null) {
            return;
        }
        mSearchResultCountTextView.setText(
                getResources().getString(R.string.search_results_loaded, count));
    }
    //[FEATURE]-Add-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
    private void updateLocalSearchResultHeader(int count) {
        if (mActivity == null || mSearchHeaderView == null) {
            return;
        }
        // Only show the header if the context is for a search result
        final Resources res = getResources();
        final boolean showHeader = ConversationListContext.isSearchResult(mViewContext);
        LogUtils.d(LOG_TAG, "ConversationListFragment.updateSearchResultHeader(%d)", count);
        if(showHeader) {
            if (!ActionBarController.SERVICE_SEARCH_MODE) {
                mListAdapter.setQueryInfo(mViewContext.searchQuery,localSearchField);
                mlocalSearchHeader.setVisibility(View.VISIBLE);
                //mSearchStatusView.setVisibility(View.GONE);
                int marginTop = true ? (int) (res.getDimension(R.dimen.notification_view_height)
                        + res.getDimension(R.dimen.local_search_header_height)) : 0;
                MarginLayoutParams layoutParams = (MarginLayoutParams) mListView.getLayoutParams();
                layoutParams.topMargin = marginTop;
                mListView.setLayoutParams(layoutParams);
                mLocalSearchHeaderText.setText(res.getString(R.string.search_results_header));
                mLocalSearchHeaderCount.setText(res.getString(R.string.search_results_loaded, count));
            }
        }
    }
    //[FEATURE]-Add-END by TSCD.zhonghua.tuo
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        mLoadingViewPending = false;
        mCanTakeDownLoadingView = true;
        if (sSelectionModeAnimationDuration < 0) {
            sSelectionModeAnimationDuration = getResources().getInteger(
                    R.integer.conv_item_view_cab_anim_duration);
        }

        // Strictly speaking, we get back an android.app.Activity from
        // getActivity. However, the
        // only activity creating a ConversationListContext is a MailActivity
        // which is of type
        // ControllableActivity, so this cast should be safe. If this cast
        // fails, some other
        // activity is creating ConversationListFragments. This activity must be
        // of type
        // ControllableActivity.
        final Activity activity = getActivity();
        if (!(activity instanceof ControllableActivity)) {
            LogUtils.e(LOG_TAG, "ConversationListFragment expects only a ControllableActivity to"
                    + "create it. Cannot proceed.");
        }
        mActivity = (ControllableActivity) activity;
        // Since we now have a controllable activity, load the account from it,
        // and register for
        // future account changes.
        mAccount = mAccountObserver.initialize(mActivity.getAccountController());
        mCallbacks = mActivity.getListHandler();
        mErrorListener = mActivity.getErrorListener();
        // Start off with the current state of the folder being viewed.
        final LayoutInflater inflater = LayoutInflater.from(mActivity.getActivityContext());
        mFooterView = (ConversationListFooterView) inflater.inflate(
                R.layout.conversation_list_footer_view, null);
        mFooterView.setClickListener(mActivity);
        //TS: zhangchao 2015-06-04 EMAIL BUGFIX_1013190 DEL_S
        //NOTE:Use the orginal code to control the behavior.
        //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257,963963 ADD_S
        //mFooterView.setErrorListener(mErrorListener);
      //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257,963963 ADD_E
        //TS: zhangchao 2015-06-04 EMAIL BUGFIX_1013190 DEL_E
        final ConversationCursor conversationCursor = getConversationListCursor();
        final LoaderManager manager = getLoaderManager();

        // TODO: These special views are always created, doesn't matter whether they will
        // be shown or not, as we add more views this will get more expensive. Given these are
        // tips that are only shown once to the user, we should consider creating these on demand.
        final ConversationListHelper helper = mActivity.getConversationListHelper();
        final List<ConversationSpecialItemView> specialItemViews = helper != null ?
                ImmutableList.copyOf(helper.makeConversationListSpecialViews(
                        activity, mActivity, mAccount))
                : null;
        if (specialItemViews != null) {
            // Attach to the LoaderManager
            for (final ConversationSpecialItemView view : specialItemViews) {
                view.bindFragment(manager, savedState);
            }
        }

        mListAdapter = new AnimatedAdapter(mActivity.getApplicationContext(), conversationCursor,
                mActivity.getSelectedSet(), mActivity, mListView, specialItemViews);
        mListAdapter.addFooter(mFooterView);

     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_S
        //Add the list header in case of overlap phenomenon
        mListHeader = new ConversationListHeader(activity);
        mListAdapter.addHeader(mListHeader);

     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_E
        // Show search result header only if we are in search mode
        final boolean showSearchHeader = ConversationListContext.isSearchResult(mViewContext);
        if (showSearchHeader) {
            mSearchHeaderView = inflater.inflate(R.layout.search_results_view, null);
            mSearchResultCountTextView = (TextView)
                    mSearchHeaderView.findViewById(R.id.search_result_count_view);
          //TS: xujian 2015-07-08 EMAIL BUGFIX_413164 ADD_S
            mSearchHeaderView.setVisibility(View.GONE);
          //TS: xujian 2015-07-08 EMAIL BUGFIX_413164 ADD_E
            mListAdapter.addHeader(mSearchHeaderView);
        } else {
            //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_S
            if(hasUpdateTimeView()){
                mListAdapter.addHeader(mConversationUpdateTimeView);
            }
            //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_E
        }

        mListView.setAdapter(mListAdapter);
        mSelectedSet = mActivity.getSelectedSet();
        mListView.setSelectionSet(mSelectedSet);
        mListAdapter.setFooterVisibility(false);
        mFolderObserver = new FolderObserver(){
            @Override
            public void onChanged(Folder newFolder) {
                onFolderUpdated(newFolder);
            }
        };
        mFolderObserver.initialize(mActivity.getFolderController());
        mConversationCursorObserver = new ConversationCursorObserver();
        mUpdater = mActivity.getConversationUpdater();
        mUpdater.registerConversationListObserver(mConversationCursorObserver);
        mTabletDevice = Utils.useTabletUI(mActivity.getApplicationContext().getResources());
        //[FEATURE]-Add-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //if(mActivity.getActivityContext().getResources().getBoolean(R.bool.feature_email_search_enhance_on))
        if(PLFUtils.getBoolean(getActivity().getApplicationContext(), "feature_email_search_enhance_on"))
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
            initLocalSearchHeader();
        //[FEATURE]-Add-END by TSCD.zhonghua.tuo
        // The onViewModeChanged callback doesn't get called when the mode
        // object is created, so
        // force setting the mode manually this time around.
        onViewModeChanged(mActivity.getViewMode().getMode());
        mActivity.getViewMode().addListener(this);

        if (mActivity.isFinishing()) {
            // Activity is finishing, just bail.
            return;
        }
        mConversationCursorHash = (conversationCursor == null) ? 0 : conversationCursor.hashCode();
        // Belt and suspenders here; make sure we do any necessary sync of the
        // ConversationCursor
        if (conversationCursor != null && conversationCursor.isRefreshReady()) {
            conversationCursor.sync();
        }

        // On a phone we never highlight a conversation, so the default is to select none.
        // On a tablet, we highlight a SINGLE conversation in landscape conversation view.
        int choice = getDefaultChoiceMode(mTabletDevice);
        if (savedState != null) {
            // Restore the choice mode if it was set earlier, or NONE if creating a fresh view.
            // Choice mode here represents the current conversation only. CAB mode does not rely on
            // the platform: checked state is a local variable {@link ConversationItemView#mChecked}
            choice = savedState.getInt(CHOICE_MODE_KEY, choice);
            if (savedState.containsKey(LIST_STATE_KEY)) {
                // TODO: find a better way to unset the selected item when restoring
                mListView.clearChoices();
            }
        }
        setChoiceMode(choice);

        // Show list and start loading list.
        showList();
        ToastBarOperation pendingOp = mActivity.getPendingToastOperation();
        if (pendingOp != null) {
            // Clear the pending operation
            mActivity.setPendingToastOperation(null);
            mActivity.onUndoAvailable(pendingOp);
        }
        /// TCT: register touch listener to hide keybord in search mode. 	396
        mListView.setOnTouchListener(this);
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_S
      //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_S
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        activity.registerReceiver(myReceiver, filter);
      //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_E
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_E
    }

    /**
     * Returns the default choice mode for the list based on whether the list is displayed on tablet
     * or not.
     * @param isTablet
     * @return
     */
    private final static int getDefaultChoiceMode(boolean isTablet) {
        return isTablet ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE;
    }

    public AnimatedAdapter getAnimatedAdapter() {
        return mListAdapter;
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Initialize fragment constants from resources
        final Resources res = getResources();
        TIMESTAMP_UPDATE_INTERVAL = res.getInteger(R.integer.timestamp_update_interval);
        LOADING_DELAY_MS = res.getInteger(R.integer.conversationview_show_loading_delay);
        MINIMUM_LOADING_DURATION = res.getInteger(R.integer.conversationview_min_show_loading);
        mUpdateTimestampsRunnable = new Runnable() {
            @Override
            public void run() {
                mListView.invalidateViews();
                mHandler.postDelayed(mUpdateTimestampsRunnable, TIMESTAMP_UPDATE_INTERVAL);
            }
        };

        // Get the context from the arguments
        final Bundle args = getArguments();
        mViewContext = ConversationListContext.forBundle(args.getBundle(CONVERSATION_LIST_KEY));
        mAccount = mViewContext.account;

        setRetainInstance(false);
    }

    @Override
    public String toString() {
        final String s = super.toString();
        if (mViewContext == null) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(s);
        sb.setLength(sb.length() - 1);
        sb.append(" mListAdapter=");
        sb.append(mListAdapter);
        sb.append(" folder=");
        sb.append(mViewContext.folder);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View rootView = inflater.inflate(R.layout.conversation_list, null);
        mEmptyView = (ConversationListEmptyView) rootView.findViewById(R.id.empty_view);
        mLoadingView = rootView.findViewById(R.id.background_view);
        mLoadingView.setVisibility(View.GONE);
        mLoadingView.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
        mListView = (SwipeableListView) rootView.findViewById(R.id.conversation_list_view);
        mListView.setHeaderDividersEnabled(false);
        mListView.setOnItemLongClickListener(this);
        mListView.enableSwipe(mAccount.supportsCapability(AccountCapabilities.UNDO));
        mListView.setListItemSwipedListener(this);
        mListView.setSwipeListener(this);
        mListView.setOnKeyListener(this);
        mListView.setOnItemClickListener(this);
        //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_S
        mConversationUpdateTimeView = inflater.inflate(R.layout.conversation_update_time_view,null);
        //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_E
        if (mNextFocusLeftId != 0) {
            mListView.setNextFocusLeftId(mNextFocusLeftId);
        }

        // enable animateOnLayout (equivalent of setLayoutTransition) only for >=JB (b/14302062)
        if (Utils.isRunningJellybeanOrLater()) {
            ((ViewGroup) rootView.findViewById(R.id.conversation_list_parent_frame))
                    .setLayoutTransition(new LayoutTransition());
        }

        // By default let's show the list view
        showListView();

        if (savedState != null && savedState.containsKey(LIST_STATE_KEY)) {
            mListView.onRestoreInstanceState(savedState.getParcelable(LIST_STATE_KEY));
        }
        mSwipeRefreshWidget =
                (MailSwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorScheme(R.color.swipe_refresh_color1,
                R.color.swipe_refresh_color2,
                R.color.swipe_refresh_color3, R.color.swipe_refresh_color4);
     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_S
        //ProgressEndTarget should twice as toolbar's height.
        setProgressEndTarget(2 * (int) getResources().getDimension(
                R.dimen.abc_action_bar_default_height_material));
     // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_E
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setScrollableChild(mListView);

        return rootView;
    }

    /**
     * Set the progress bar's distance to the top when refresh the list
     */
    public void setProgressEndTarget(int distance) {
        mSwipeRefreshWidget.setProgressViewEndTarget(false, distance);
    }
    /**
     * Sets the choice mode of the list view
     */
    private final void setChoiceMode(int choiceMode) {
        mListView.setChoiceMode(choiceMode);
    }

    /**
     * Tell the list to select nothing.
     */
    public final void setChoiceNone() {
        // On a phone, the default choice mode is already none, so nothing to do.
        if (!mTabletDevice) {
            return;
        }
        clearChoicesAndActivated();
        setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    /**
     * Tell the list to get out of selecting none.
     */
    public final void revertChoiceMode() {
        // On a phone, the default choice mode is always none, so nothing to do.
        if (!mTabletDevice) {
            return;
        }
        setChoiceMode(getDefaultChoiceMode(mTabletDevice));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_S
      //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_S
//        getActivity().unregisterReceiver(myReceiver);
      //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_E
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_E
        // Clear the list's adapter
        mListAdapter.destroy();
        mListView.setAdapter(null);

        mActivity.getViewMode().removeListener(this);
        if (mFolderObserver != null) {
            mFolderObserver.unregisterAndDestroy();
            mFolderObserver = null;
        }
        if (mConversationCursorObserver != null) {
            mUpdater.unregisterConversationListObserver(mConversationCursorObserver);
            mConversationCursorObserver = null;
        }
        mAccountObserver.unregisterAndDestroy();
        getAnimatedAdapter().cleanup();
        super.onDestroyView();
    }

    /**
     * There are three binary variables, which determine what we do with a
     * message. checkbEnabled: Whether check boxes are enabled or not (forced
     * true on tablet) cabModeOn: Whether CAB mode is currently on or not.
     * pressType: long or short tap (There is a third possibility: phone or
     * tablet, but they have <em>identical</em> behavior) The matrix of
     * possibilities is:
     * <p>
     * Long tap: Always toggle selection of conversation. If CAB mode is not
     * started, then start it.
     * <pre>
     *              | Checkboxes | No Checkboxes
     *    ----------+------------+---------------
     *    CAB mode  |   Select   |     Select
     *    List mode |   Select   |     Select
     *
     * </pre>
     *
     * Reference: http://b/issue?id=6392199
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Ignore anything that is not a conversation item. Could be a footer.
        if (!(view instanceof ConversationItemView)) {
            return false;
        }
        return ((ConversationItemView) view).toggleSelectedStateOrBeginDrag();
    }

    /**
     * See the comment for
     * {@link #onItemLongClick(AdapterView, View, int, long)}.
     * <p>
     * Short tap behavior:
     *
     * <pre>
     *              | Checkboxes | No Checkboxes
     *    ----------+------------+---------------
     *    CAB mode  |    Peek    |     Select
     *    List mode |    Peek    |      Peek
     * </pre>
     *
     * Reference: http://b/issue?id=6392199
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // TS: xiaolin.li 2015-01-20 EMAIL BUGFIX-901969 ADD_S
        if( position < 0 ){
                return;
        }
        // TS: xiaolin.li 2015-01-20 EMAIL BUGFIX-901969 ADD_E
        onListItemSelected(view, position);
    }

    private void onListItemSelected(View view, int position) {
        if (view instanceof ToggleableItem) {
            //TS: zheng.zou 2015-5-30 EMAIL FEATURE_996904 MOD_S
//            final boolean showSenderImage =
//                    (mAccount.settings.convListIcon == ConversationListIcon.SENDER_IMAGE);
            final boolean inCabMode = !mSelectedSet.isEmpty();
            if (inCabMode) {
            //TS: zheng.zou 2015-5-30 EMAIL FEATURE_996904 MOD_E
                ((ToggleableItem) view).toggleSelectedState();
            } else {
                if (inCabMode) {
                    // this is a peek.
                    Analytics.getInstance().sendEvent("peek", null, null, mSelectedSet.size());
                }
                AnalyticsTimer.getInstance().trackStart(AnalyticsTimer.OPEN_CONV_VIEW_FROM_LIST);
                viewConversation(position);
            }
        } else {
            // Ignore anything that is not a conversation item. Could be a footer.
            // If we are using a keyboard, the highlighted item is the parent;
            // otherwise, this is a direct call from the ConverationItemView
            return;
        }
        // When a new list item is clicked, commit any existing leave behind
        // items. Wait until we have opened the desired conversation to cause
        // any position changes.
        commitDestructiveActions(Utils.useTabletUI(mActivity.getActivityContext().getResources()));
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        SwipeableListView list = (SwipeableListView) view;
        // Don't need to handle ENTER because it's auto-handled as a "click".
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                if (mKeyInitiatedFromList) {
                    onListItemSelected(list.getSelectedView(), list.getSelectedItemPosition());
                }
                mKeyInitiatedFromList = false;
            } else if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                mKeyInitiatedFromList = true;
            }
            return true;
        } else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                final int position = list.getSelectedItemPosition();
                final Object item = getAnimatedAdapter().getItem(position);
                if (item != null && item instanceof ConversationCursor) {
                    final Conversation conv = ((ConversationCursor) item).getConversation();
                    mCallbacks.onConversationFocused(conv);
                }
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isCursorReadyToShow()) {
            // If the cursor got reset, let's reset the analytics state variable and show the list
            // view since we are waiting for load again
            mInitialCursorLoading = true;
            showListView();
        }

        final ConversationCursor conversationCursor = getConversationListCursor();
        if (conversationCursor != null) {
            conversationCursor.handleNotificationActions();

            restoreLastScrolledPosition();
        }

        mSelectedSet.addObserver(mConversationSetObserver);
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_S
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD
        getActivity().registerReceiver(myReceiver, filter);
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_E
    }

    @Override
    public void onPause() {
        super.onPause();

        mSelectedSet.removeObserver(mConversationSetObserver);

        saveLastScrolledPosition();
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_S
        getActivity().unregisterReceiver(myReceiver);
      //TS: wenggangjin 2015-01-26 EMAIL BUGFIX_-908180 MOD_E
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            outState.putParcelable(LIST_STATE_KEY, mListView.onSaveInstanceState());
            outState.putInt(CHOICE_MODE_KEY, mListView.getChoiceMode());
        }

        if (mListAdapter != null) {
            mListAdapter.saveSpecialItemInstanceState(outState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.postDelayed(mUpdateTimestampsRunnable, TIMESTAMP_UPDATE_INTERVAL);
        Analytics.getInstance().sendView("ConversationListFragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mUpdateTimestampsRunnable);
    }

    @Override
    public void onViewModeChanged(int newMode) {
        if (mTabletDevice) {
            if (ViewMode.isListMode(newMode)) {
                // There are no selected conversations when in conversation list mode.
                clearChoicesAndActivated();
            }
        }
        if (mFooterView != null) {
            mFooterView.onViewModeChanged(newMode);
        }

        // Set default navigation
        if (ViewMode.isListMode(newMode)) {
            mListView.setNextFocusRightId(R.id.conversation_list_view);
            mListView.requestFocus();
        } else if (ViewMode.isConversationMode(newMode)) {
            // This would only happen in two_pane
            mListView.setNextFocusRightId(R.id.conversation_pager);
        }
    }

    public boolean isAnimating() {
        final AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter != null && adapter.isAnimating()) {
            return true;
        }
        final boolean isScrolling = (mListView != null && mListView.isScrolling());
        if (isScrolling) {
            LogUtils.i(LOG_TAG, "CLF.isAnimating=true due to scrolling");
        }
        return isScrolling;
    }

    private void clearChoicesAndActivated() {
        final int currentSelected = mListView.getCheckedItemPosition();
        if (currentSelected != ListView.INVALID_POSITION) {
            mListView.setItemChecked(mListView.getCheckedItemPosition(), false);
        }
    }

    /**
     * Handles a request to show a new conversation list, either from a search
     * query or for viewing a folder. This will initiate a data load, and hence
     * must be called on the UI thread.
     */
    private void showList() {
        mInitialCursorLoading = true;
        onFolderUpdated(mActivity.getFolderController().getFolder());
        onConversationListStatusUpdated();

        // try to get an order-of-magnitude sense for message count within folders
        // (N.B. this count currently isn't working for search folders, since their counts stream
        // in over time in pieces.)
        final Folder f = mViewContext.folder;
        if (f != null) {
            final long countLog;
            if (f.totalCount > 0) {
                countLog = (long) Math.log10(f.totalCount);
            } else {
                countLog = 0;
            }
            Analytics.getInstance().sendEvent("view_folder", f.getTypeDescription(),
                    Long.toString(countLog), f.totalCount);
        }
    }

    /**
     * View the message at the given position.
     *
     * @param position The position of the conversation in the list (as opposed to its position
     *        in the cursor)
     */
    private void viewConversation(final int position) {
        LogUtils.d(LOG_TAG, "ConversationListFragment.viewConversation(%d)", position);
        // TS: chaozhang 2015-07-20 EMAIL BUGFIX-1047784 MOD_S
        //NOTE: In monkey test,may be the clicked item is footview,so issue happen,here to avoid this.
        ConversationCursor cursor = null;
        try {
            cursor =
                    (ConversationCursor) getAnimatedAdapter().getItem(position);
        } catch (ClassCastException e) {
            LogUtils.e(
                    LOG_TAG,
                    "ClassCastException happen,may be current clicked item is not converionsation,but footerView");
            return;
        }
        // TS: chaozhang 2015-07-20 EMAIL BUGFIX-1047784 MOD_E
        if (cursor == null) {
            LogUtils.e(LOG_TAG,
                    "unable to open conv at cursor pos=%s cursor=%s getPositionOffset=%s",
                    position, cursor, getAnimatedAdapter().getPositionOffset(position));
            return;
        }

        final Conversation conv = cursor.getConversation();
        // TS: tao.gan 2016-03-28 EMAIL BUGFIX-1862349 ADD_S
        if (conv == null) {
            LogUtils.e(LOG_TAG, "conversation null at ConversationListFragment#viewConversation,can't view it ");
            return;
        }
        // TS: tao.gan 2016-03-28 EMAIL BUGFIX-1862349 ADD_E
        /*
         * The cursor position may be different than the position method parameter because of
         * special views in the list.
         */
        conv.position = cursor.getPosition();
        setSelected(conv.position, true);
        mCallbacks.onConversationSelected(conv, false /* inLoaderCallbacks */);
    }

    /**
     * Sets the selected conversation to the position given here.
     * @param cursorPosition The position of the conversation in the cursor (as opposed to
     * in the list)
     * @param different if the currently selected conversation is different from the one provided
     * here.  This is a difference in conversations, not a difference in positions. For example, a
     * conversation at position 2 can move to position 4 as a result of new mail.
     */
    public void setSelected(final int cursorPosition, boolean different) {
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return;
        }

        final int position =
                cursorPosition + getAnimatedAdapter().getPositionOffset(cursorPosition);

        setRawSelected(position, different);
    }

    /**
     * Sets the selected conversation to the position given here.
     * @param position The position of the item in the list
     * @param different if the currently selected conversation is different from the one provided
     * here.  This is a difference in conversations, not a difference in positions. For example, a
     * conversation at position 2 can move to position 4 as a result of new mail.
     */
    public void setRawSelected(final int position, final boolean different) {
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            return;
        }

        if (different) {
            mListView.smoothScrollToPosition(position);
        }
        mListView.setItemChecked(position, true);
    }

    /**
     * Returns the cursor associated with the conversation list.
     * @return
     */
    private ConversationCursor getConversationListCursor() {
        return mCallbacks != null ? mCallbacks.getConversationListCursor() : null;
    }

    /**
     * Request a refresh of the list. No sync is carried out and none is
     * promised.
     */
    public void requestListRefresh() {
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Change the UI to delete the conversations provided and then call the
     * {@link DestructiveAction} provided here <b>after</b> the UI has been
     * updated.
     * @param conversations
     * @param action
     */
    public void requestDelete(int actionId, final Collection<Conversation> conversations,
            final DestructiveAction action) {
        for (Conversation conv : conversations) {
            conv.localDeleteOnUpdate = true;
        }
        final ListItemsRemovedListener listener = new ListItemsRemovedListener() {
            @Override
            public void onListItemsRemoved() {
                action.performAction();
            }
        };
        if (mListView.getSwipeAction() == actionId) {
            if (!mListView.destroyItems(conversations, listener)) {
                // The listView failed to destroy the items, perform the action manually
                LogUtils.e(LOG_TAG, "ConversationListFragment.requestDelete: " +
                        "listView failed to destroy items.");
                action.performAction();
            }
            return;
        }
        // Delete the local delete items (all for now) and when done,
        // update...
        mListAdapter.delete(conversations, listener);
    }

    public void onFolderUpdated(Folder folder) {
        if (!isCursorReadyToShow()) {
            // Wait a bit before showing either the empty or loading view. If the messages are
            // actually local, it's disorienting to see this appear on every folder transition.
            // If they aren't, then it will likely take more than 200 milliseconds to load, and
            // then we'll see the loading view.
            if (!mLoadingViewPending) {
                mHandler.postDelayed(mLoadingViewRunnable, LOADING_DELAY_MS);
                mLoadingViewPending = true;
            }
        }

        mFolder = folder;
        setSwipeAction();
        // Update enabled state of swipe to refresh.
        mSwipeRefreshWidget.setEnabled(!ConversationListContext.isSearchResult(mViewContext));
        //TS: chaozhang, 2015-07-14 EMAIL BUGFIX_425517 MOD_S
        //NOTE: add here to avoid NPE
        if (mFolder == null) {
            checkSyncStatus(); // AM: Kexue.Geng 2015-03-18 EMAIL BUGFIX_952327 MOD
            return;
        }
        //TS: chaozhang, 2015-07-14 EMAIL BUGFIX_425517 MOD_E
        //TS: zhonghua.tuo 2015-03-17 EMAIL BUGFIX_943354 ADD_S
        if (mFolder.refreshUri == null) {
            mSwipeRefreshWidget.setEnabled(false);
        }
        //TS: zhonghua.tuo 2015-03-17 EMAIL BUGFIX_943354 ADD_E
        //TS: junwei-xu 2014-1-5 EMAIL BUGFIX_884395 ADD_S
        //TS: wenggangjin 2015-03-13 EMAIL BUGFIX_-941160 MOD_S
//        if(mActivity.getFolderController().getFolder().isDraft()){
        if(mActivity.getFolderController() != null
                && mActivity.getFolderController().getFolder() != null
                && mActivity.getFolderController().getFolder().isDraft()){
            mSwipeRefreshWidget.setEnabled(false);
        }
      //TS: wenggangjin 2015-03-13 EMAIL BUGFIX_-941160 MOD_E
        //TS: junwei-xu 2014-1-5 EMAIL BUGFIX_884395 ADD_E
        mListAdapter.setFolder(mFolder);
        mFooterView.setFolder(mFolder);
        if (!mFolder.wasSyncSuccessful()&&!mFolder.isSyncInProgress()) {
            //TS: zhangchao 2015-06-04 EMAIL BUGFIX_1013190 MOD_S
            //NOTE: Use orginal code to control the behivor.
            //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257,963963 MOD_S
            mErrorListener.onError(mFolder, false);
            //mFooterView.updateFooterStatus(3);
            //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257,963963 mod_E
            //TS: zhangchao 2015-06-04 EMAIL BUGFIX_1013190 MOD_E
        }

        // Update the sync status bar with sync results if needed
        checkSyncStatus();

        //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_S
        if(hasUpdateTimeView()){
            if(needListHeader()){
                updateHeaderView();
            }else {
                mListAdapter.removeHeader(mConversationUpdateTimeView);
            }
        }
        //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_E

        // Blow away conversation items cache.
        ConversationItemViewModel.onFolderUpdated(mFolder);
    }

    /**
     * Updates the footer visibility and updates the conversation cursor
     */
    public void onConversationListStatusUpdated() {
        final ConversationCursor cursor = getConversationListCursor();
        /// TCT: add params of current footer view shown state
        final boolean showFooter = mFooterView.updateStatus(cursor,
                mListAdapter.getFooterViewShowState());
        mListAdapter.setFooterVisibility(showFooter);
        // Also change the cursor here.
        onCursorUpdated();
        //TS: junwei-xu 2015-12-25 EMAIL BUGFIX-1193902 DEL_S
        //Note: layout xml contains empty view, we don't need set empty view to list view.
        /*
        if(cursor == null || cursor.getCount() == 0) { // AM: Kexue.Geng 2015-03-31 EMAIL BUGFIX_959144 MOD
            mListView.setEmptyView(null); //porting from PR937218
        }
        */
        //TS: junwei-xu 2015-12-25 EMAIL BUGFIX-1193902 DEL_E
        if (isCursorReadyToShow() && mCanTakeDownLoadingView) {
            LogUtils.d(STAG,"onConversationListStatusUpdated is true!"); //AM: peng-zhang 2015-04-23 EMAIL BUGFIX_971924 MOD_ADD
            hideLoadingViewAndShowContents();
        }
        /** TCT:update local search count. */
        updateLocalSearchCount(cursor);
    }

    private void hideLoadingViewAndShowContents() {
        final ConversationCursor cursor = getConversationListCursor();
        //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 MOD_S
//        boolean showFooter = mFooterView.updateStatus(cursor);
        /// TCT: add params of current footer view shown state
        // Update the sync status bar with sync results if needed
        final boolean showFooter = mFooterView.updateStatus(cursor, mListAdapter.getFooterViewShowState());
//        if(!show){
//            showFooter = false;
//        }
        //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 MOD_E
        // Update the sync status bar with sync results if needed
        checkSyncStatus();
        mListAdapter.setFooterVisibility(showFooter);
        mLoadingViewPending = false;
        mHandler.removeCallbacks(mLoadingViewRunnable);

        //AM: peng-zhang 2015-04-23 EMAIL BUGFIX_971924 MOD_S
        LogUtils.d(STAG,"cursor is null   " + (cursor != null));
        if(cursor != null){
            LogUtils.d(STAG,"isCursorReadyToShow   " + (ConversationCursor.isCursorReadyToShow(cursor)));
        }
        LogUtils.d(STAG,"mListAdapter cursor is null   " + (mListAdapter.getCursor() == null));
        if( mListAdapter.getCursor() != null){
            LogUtils.d(STAG,"cursor count number   " + (mListAdapter.getCursor().getCount() == 0));
        }
        LogUtils.d(STAG,"folder has children folder   " + (mFolder.hasChildren));
        LogUtils.d(STAG,"mListAdapter count number   " + (mListAdapter.getCount()));
        //AM: peng-zhang 2015-04-23 EMAIL BUGFIX_971924 MOD_E

        // Even though cursor might be empty, the list adapter might have teasers/footers.
        // So we check the list adapter count if the cursor is fully/partially loaded.
        //TS: junwei-xu 2015-12-31 EMAIL BUGFIX-1201184 MOD_S
        //Note: No need to check if it has child folder.
        //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_889859 ADD_S
        //TS: jin.dong 2016-3-17 EMAIL BUGFIX-1786313 MOD_S
        if (cursor != null && ConversationCursor.isCursorReadyToShow(cursor) &&
                (mListAdapter.getCursor() == null || (mListAdapter.getCursor().getCount() == 0 && !mFolder.hasChildren ))) {//[BUGFIX]-Mod by TSNJ Zhenhua.Fan,04/12/2014,PR 861468
        //TS: jin.dong 2016-3-17 EMAIL BUGFIX-1786313 MOD_E
        //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_889859 ADD_E
        //TS: junwei-xu 2015-12-31 EMAIL BUGFIX-1201184 MOD_E
            LogUtils.d(STAG,"showEmptyView!");
            showEmptyView();
        } else {
            showListView();
          //TS: xujian 2015-07-08 EMAIL BUGFIX_413164 ADD_S
            if(mSearchHeaderView != null){
                mSearchHeaderView.setVisibility(View.VISIBLE);
            }
          //TS: xujian 2015-07-08 EMAIL BUGFIX_413164 ADD_E
            // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_S
//            if(notSetListViewFocus){
//                mListView.setFocusable(true);
//                mListView.setFocusableInTouchMode(true);
//                mListView.requestFocus();
//                notSetListViewFocus = false;
//                }
            // TS: chenyanhua 2015-01-21 EMAIL BUGFIX-898277 ADD_E
        }
    }

    private void setSwipeAction() {
        int swipeSetting = Settings.getSwipeSetting(mAccount.settings);
        if (swipeSetting == Swipe.DISABLED
                || !mAccount.supportsCapability(AccountCapabilities.UNDO)
                || (mFolder != null && (mFolder.isTrash() || mFolder.isDraft()))) {  //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 MOD
            mListView.enableSwipe(false);
        } else {
            final int action;
            mListView.enableSwipe(true);
            if (mFolder == null) {
                action = R.id.remove_folder;
            } else {
                switch (swipeSetting) {
                    // Try to respect user's setting as best as we can and default to doing nothing
                    case Swipe.DELETE:
                        // Delete in Outbox means discard failed message and put it in draft
                        if (mFolder.isType(UIProvider.FolderType.OUTBOX)) {
                            action = R.id.discard_outbox;
                        } else {
                            action = R.id.delete;
                        }
                        break;
                    case Swipe.ARCHIVE:
                        // Special case spam since it shouldn't remove spam folder label on swipe
                        if (mAccount.supportsCapability(AccountCapabilities.ARCHIVE)
                                && !mFolder.isSpam()) {
                            if (mFolder.supportsCapability(FolderCapabilities.ARCHIVE)) {
                                action = R.id.archive;
                                break;
                            } else if (mFolder.supportsCapability
                                    (FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES)) {
                                action = R.id.remove_folder;
                                break;
                            }
                        }

                        /*
                         * If we get here, we don't support archive, on either the account or the
                         * folder, so we want to fall through to swipe doing nothing
                         */
                        //$FALL-THROUGH$
                    default:
                        mListView.enableSwipe(false);
                        action = 0; // Use default value so setSwipeAction essentially has no effect
                        break;
                }
            }
            mListView.setSwipeAction(action);
        }
        mListView.setCurrentAccount(mAccount);
        mListView.setCurrentFolder(mFolder);
    }

    /**
     * Changes the conversation cursor in the list and sets selected position if none is set.
     */
    private void onCursorUpdated() {
        LogUtils.w(LOG_TAG, "ConversationListFragment           onCursorUpdated          ");
        if (mCallbacks == null || mListAdapter == null) {
            return;
        }
        // Check against the previous cursor here and see if they are the same. If they are, then
        // do a notifyDataSetChanged.
        final ConversationCursor newCursor = mCallbacks.getConversationListCursor();
        //TS: junwei-xu 2015-06-09 EMAIL BUGFIX-1019276 MOD_S
        //Notes: if newCursor contains '%', when use LogUtils.w(),it will throws MissingFormatArgumentException,
        //so, we use Log.w() to print log.
        //LogUtils.w(LOG_TAG, "ConversationListFragment           onCursorUpdated          newCursor:" + newCursor);
        LogUtils.d(LOG_TAG, "ConversationListFragment           onCursorUpdated          newCursor:" + newCursor);
        //TS: junwei-xu 2015-06-09 EMAIL BUGFIX-1019276 MOD_E

        if (newCursor == null && mListAdapter.getCursor() != null) {
            // We're losing our cursor, so save our scroll position
            saveLastScrolledPosition();
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
        if(hasUpdateTimeView()){
            if(needListHeader()){
                if (!mListAdapter.containsHeader(mConversationUpdateTimeView)){
                    mListAdapter.addHeader(mConversationUpdateTimeView);
                }
                updateHeaderView();
            } else {
                mListAdapter.removeHeader(mConversationUpdateTimeView);
            }
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E

        mListAdapter.swapCursor(newCursor);
        // When the conversation cursor is *updated*, we get back the same instance. In that
        // situation, CursorAdapter.swapCursor() silently returns, without forcing a
        // notifyDataSetChanged(). So let's force a call to notifyDataSetChanged, since an updated
        // cursor means that the dataset has changed.
        final int newCursorHash = (newCursor == null) ? 0 : newCursor.hashCode();
        LogUtils.w(LOG_TAG, "ConversationListFragment           onCursorUpdated          newCursorHash:"+newCursorHash);
        LogUtils.w(LOG_TAG, "ConversationListFragment           onCursorUpdated          mConversationCursorHash:"+mConversationCursorHash);
        if (mConversationCursorHash == newCursorHash && mConversationCursorHash != 0) {
        LogUtils.w(LOG_TAG, "ConversationListFragment           onCursorUpdated          ");
            mListAdapter.notifyDataSetChanged();
        }
        mConversationCursorHash = newCursorHash;

        updateAnalyticsData(newCursor);
        if (newCursor != null) {
            final int newCursorCount = newCursor.getCount();
            //[FEATURE]-Mod-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //if(mActivity.getActivityContext().getResources().getBoolean(R.bool.feature_email_search_enhance_on)){
//            if(PLFUtils.getBoolean(getActivity().getApplicationContext(), "feature_email_search_enhance_on")){
//            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
//                if(ActionBarController.SERVICE_SEARCH_MODE) {
//                    updateSearchResultHeader(newCursorCount);
//                }else {
//                    updateLocalSearchResultHeader(newCursorCount);
//                }
//            }
//            //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,04/12/2014,PR 861468
//            else
//            {
//                updateSearchResultHeader(newCursorCount);
//            }
            // We want to update the UI with this information if either we are loaded or complete,
            // or we have a folder with a non-0 count.
            /// TCT: Show total count for remote search
            final int folderCount = mFolder != null ? mFolder.totalCount : 0;
            updateSearchResultHeader(folderCount);
          //[BUGFIX]-Add-EDN by TSNJ Zhenhua.Fan
            //[FEATURE]-Mod-END by TSCD.zhonghua.tuo
            if (newCursorCount > 0) {
                //TS: jin.dong 2016-3-7 EMAIL BUGFIX_1748590 MOD_S
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newCursor.markContentsSeen();
                    }
                }).start();
                //TS: jin.dong 2016-3-7 EMAIL BUGFIX_1748590 MOD_E
                restoreLastScrolledPosition();
            }
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 DEL_S
        //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_S
//        if(hasUpdateTimeView()){
//            updateHeaderView();
//        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 DEL_E
        //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_E

        // If a current conversation is available, and none is selected in the list, then ask
        // the list to select the current conversation.
        final Conversation conv = mCallbacks.getCurrentConversation();
        if (conv != null) {
            if (mListView.getChoiceMode() != ListView.CHOICE_MODE_NONE
                    && mListView.getCheckedItemPosition() == -1) {
                setSelected(conv.position, true);
            }
        }
    }

    public void commitDestructiveActions(boolean animate) {
        if (mListView != null) {
            mListView.commitDestructiveActions(animate);

        }
    }

    @Override
    public void onListItemSwiped(Collection<Conversation> conversations) {
        mUpdater.showNextConversation(conversations);
    }

    private void checkSyncStatus() {
        if (mFolder != null && mFolder.isSyncInProgress()) {
            LogUtils.d(LOG_TAG, "CLF.checkSyncStatus still syncing");
            // Still syncing, ignore
        } else {
            // Finished syncing:
            LogUtils.d(LOG_TAG, "CLF.checkSyncStatus done syncing");
            mSwipeRefreshWidget.setRefreshing(false);
        }
    }

    /**
     * Displays the indefinite progress bar indicating a sync is in progress.  This
     * should only be called if user manually requested a sync, and not for background syncs.
     */
    protected void showSyncStatusBar() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    /**
     * Clears all items in the list.
     */
    public void clear() {
        mListView.setAdapter(null);
    }

    private final ConversationSetObserver mConversationSetObserver = new ConversationSetObserver() {
        @Override
        public void onSetPopulated(final ConversationSelectionSet set) {
            // Disable the swipe to refresh widget.
            mSwipeRefreshWidget.setEnabled(false);
        }

        @Override
        public void onSetEmpty() {
          //TS: wenggangjin 2015-03-17 EMAIL BUGFIX_-942413 MOD_S
          //TS: wenggangjin 2015-02-13 EMAIL BUGFIX_-930431 MOD_S
            // AM: Kexue.Geng 2015-03-18 EMAIL BUGFIX_952327 MOD_S
           // if(mActivity.getFolderController() == null || mActivity.getFolderController().getFolder() == null ){
           //     return;
           // }
            // AM: Kexue.Geng 2015-03-18 EMAIL BUGFIX_952327 MOD_E
          //TS: wenggangjin 2015-02-13 EMAIL BUGFIX_-930431 MOD_E
          //TS: wenggangjin 2015-03-17 EMAIL BUGFIX_-942413 MOD_E
            //TS: junwei-xu 2014-1-5 EMAIL BUGFIX_884395 MOD_S
            if(mActivity != null && mActivity.getFolderController() != null && mActivity.getFolderController().getFolder() != null && mActivity.getFolderController().getFolder().isDraft()){ // AM: Kexue.Geng 2015-03-18 EMAIL BUGFIX_952327
                mSwipeRefreshWidget.setEnabled(false);
            }else {
                mSwipeRefreshWidget.setEnabled(true);
            }
            //TS: junwei-xu 2014-1-5 EMAIL BUGFIX_884395 MOD_E
        }

        @Override
        public void onSetChanged(final ConversationSelectionSet set) {
            // Do nothing
        }
    };

    private void saveLastScrolledPosition() {
        //TS: lin-zhou 2015-11-12 EMAIL BUGFIX_1111531 MOD_S
        if (mListAdapter.getCursor() == null || mActivity == null || mFolder == null || mFolder.conversationListUri == null) {
            // If you save your scroll position in an empty list, you're gonna have a bad time
            return;
        }
        //TS: lin-zhou 2015-11-12 EMAIL BUGFIX_1111531 MOD_E

        final Parcelable savedState = mListView.onSaveInstanceState();

        mActivity.getListHandler().setConversationListScrollPosition(
                mFolder.conversationListUri.toString(), savedState);
    }

    private void restoreLastScrolledPosition() {
        // Scroll to our previous position, if necessary
        if (!mScrollPositionRestored && mFolder != null) {
            final String key = mFolder.conversationListUri.toString();
            final Parcelable savedState = mActivity.getListHandler()
                    .getConversationListScrollPosition(key);
            if (savedState != null) {
                mListView.onRestoreInstanceState(savedState);
            }
            mScrollPositionRestored = true;
        }
    }

    /* (non-Javadoc)
     * @see android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener#onRefresh()
     */
    @Override
    public void onRefresh() {
        //TS: tianjing.su 2016-03-14 EMAIL FEATURE_1804126 ADD_S
        try {
            if(mAccount !=null && mAccount.getType() .equals( getString(R.string.intent_exchange_package) )){
                int emailVersionCode = getActivity().getPackageManager().getPackageInfo(getString(R.string.email_package_name), 0).versionCode;
                int exchangeVersionCode = getActivity().getPackageManager().getPackageInfo(getString(R.string.intent_exchange_package), 1).versionCode;
                if(emailVersionCode != exchangeVersionCode) {
					//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
					Utility.showToast(getActivity(), R.string.toast_warning_text);
                    //Toast.makeText(getActivity(), R.string.toast_warning_text, Toast.LENGTH_LONG).show();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //TS: tianjing.su 2016-03-14 EMAIL FEATURE_1804126 ADD_E
        Analytics.getInstance().sendEvent(Analytics.EVENT_CATEGORY_MENU_ITEM, "swipe_refresh", null,
                0);

        // This will call back to showSyncStatusBar():
        mActivity.getFolderController().requestFolderRefresh();

        // Clear list adapter state out of an abundance of caution.
        // There is a class of bugs where an animation that should have finished doesn't (maybe
        // it didn't start, or it didn't finish), and the list gets stuck pretty much forever.
        // Clearing the state here is in line with user expectation for 'refresh'.
        getAnimatedAdapter().clearAnimationState();
        // possibly act on the now-cleared state
        mActivity.onAnimationEnd(mListAdapter);
    }

    /**
     * Extracted function that handles Analytics state and logging updates for each new cursor
     * @param newCursor the new cursor pointer
     */
    private void updateAnalyticsData(ConversationCursor newCursor) {
        if (newCursor != null) {
            // Check if the initial data returned yet
            if (mInitialCursorLoading) {
                // This marks the very first time the cursor with the data the user sees returned.
                // We either have a cursor in LOADING state with cursor's count > 0, OR the cursor
                // completed loading.
                // Use this point to log the appropriate timing information that depends on when
                // the conversation list view finishes loading
                if (isCursorReadyToShow()) {
                    if (newCursor.getCount() == 0) {
                        Analytics.getInstance().sendEvent("empty_state", "post_label_change",
                                mFolder.getTypeDescription(), 0);
                    }
                    AnalyticsTimer.getInstance().logDuration(AnalyticsTimer.COLD_START_LAUNCHER,
                            true /* isDestructive */, "cold_start_to_list", "from_launcher", null);
                    // Don't need null checks because the activity, controller, and folder cannot
                    // be null in this case
                    // TS: Gantao 2016-01-19 EMAIL BUGFIX-1463224 MOD_S
                    //Do null checks
                    Folder folder = mActivity.getFolderController().getFolder();
                    if (folder != null && folder.isSearch()) {
                        AnalyticsTimer.getInstance().logDuration(AnalyticsTimer.SEARCH_TO_LIST,
                                true /* isDestructive */, "search_to_list", null, null);
                    }
                    // TS: Gantao 2016-01-19 EMAIL BUGFIX-1463224 MOD_E

                    mInitialCursorLoading = false;
                }
            } else {
                // Log the appropriate events that happen after the initial cursor is loaded
                if (newCursor.getCount() == 0 && mConversationCursorLastCount > 0) {
                    Analytics.getInstance().sendEvent("empty_state", "post_delete",
                            mFolder.getTypeDescription(), 0);
                }
            }

            // We save the count here because for folders that are empty, multiple successful
            // cursor loads will occur with size of 0. Thus we don't want to emit any false
            // positive post_delete events.
            mConversationCursorLastCount = newCursor.getCount();
        } else {
            mConversationCursorLastCount = 0;
        }
    }

    /**
     * Helper function to determine if the current cursor is ready to populate the UI
     * Since we extracted the functionality into a static function in ConversationCursor,
     * this function remains for the sole purpose of readability.
     * @return
     */
    private boolean isCursorReadyToShow() {
        return ConversationCursor.isCursorReadyToShow(getConversationListCursor());
    }

    public ListView getListView() {
        return mListView;
    }

    public void setNextFocusLeftId(@IdRes int id) {
        mNextFocusLeftId = id;
        if (mListView != null) {
            mListView.setNextFocusLeftId(mNextFocusLeftId);
        }
    }
  //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_S
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)){
                boolean airplaneModeOn = intent.getBooleanExtra("state", false);
                if(airplaneModeOn){
                    mHandler.postDelayed(mHideLoadingRunnable, MINIMUM_LOADING_DURATION);
                  //TS: wenggangjin 2014-12-31 EMAIL BUGFIX_881447 MOD_S
                    mErrorListener.onError(mFolder, false);
                  //TS: wenggangjin 2014-12-31 EMAIL BUGFIX_881447 MOD_E
                }
                else {
                    mHandler.postDelayed(mLoadingViewRunnable, LOADING_DELAY_MS);
                }
            }
           //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD_S
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if(!(info != null && info.isConnected() && info.getState() == NetworkInfo.State.CONNECTED)) {
                    show = false;
                }
            }
          //TS: junwei-xu 2015-1-26 EMAIL BUGFIX_898211 ADD_E
        }
    }
  //TS: wenggangjin 2014-12-16 EMAIL BUGFIX_864636 MOD_E

    /// TCT: observer touch event, and hide keyboard in search mode when read search result.
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE) {
            // hide the soft keyboard while touch search mail list
            ActivityController activityController = (ActivityController) mActivity
                    .getAccountController();
            ConversationListContext listContext = activityController
                    .getCurrentListContext();
            if (listContext != null && listContext.isLocalSearch()) {
                //TS: rong-tang 2016-03-23 EMAIL BUGFIX-1815601 MOD_S
                hideSoftKeyboard((AbstractActivityController) activityController);
                //TS: rong-tang 2016-03-23 EMAIL BUGFIX-1815601 MOD_E
            }
        }
        // don't do anything, let the system process the event
        return false;
    }

    //TS: rong-tang 2016-03-23 EMAIL BUGFIX-1815601 MOD_S
    /// TCT: hide soft keyboard
    private void hideSoftKeyboard(AbstractActivityController controller) {
        InputMethodManager imm = (InputMethodManager)
        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getListView().getWindowToken(), 0);
            //Note: after hide soft input, also need to clear focus which in search view.
            controller.mActionBarController.clearSearchFocus();
        }
    }
    //TS: rong-tang 2016-03-23 EMAIL BUGFIX-1815601 MOD_E

            /**
             * TCT: use to update footer view loading status.
             */
    public void updateFooterStatus(boolean isStarted) {
        mFooterView.updateLoadingStatus(isStarted);
    }

            /**
             * TCT: Update remote search result count.
             * @param count Count of results from remote search
             */
    private void updateRemoteSearchCount(AccountController controller, int count) {
        if (controller == null) {
            LogUtils.logFeature(LogTag.SEARCH_TAG, "updateRemoteSearchCount with controller is NULL"
            );
            return;
        }
        AbstractActivityController aac = (AbstractActivityController) controller;
        aac.updateSearchResult(count);
    }

            /**
             * TCT: update local search result count.
             */
    private void updateLocalSearchCount(ConversationCursor cursor) {
        ActivityController activityController = (ActivityController) mActivity
                .getAccountController();
        ConversationListContext listContext = activityController.getCurrentListContext();
        if (cursor != null && listContext != null
                && listContext.isLocalSearchExecuted()
                && activityController instanceof AbstractActivityController) {
            LogUtils.v(LOG_TAG, "ConversationList changed, update local search result. count="
                    + cursor.getCount());
            AbstractActivityController aac = (AbstractActivityController) activityController;
            aac.updateSearchResult(cursor.getCount());
            //TS: junwei-xu 2015-12-25 EMAIL BUGFIX-1193902 DEL_S
            //Note: layout xml contains empty view, we don't need set empty view to list view.
            /*
            /// M: when search count is zero and not support remote search
            // set the mListView to empty view. @{
            if (cursor.getCount() == 0
                    && (!mAccount.supportsCapability(AccountCapabilities.FOLDER_SERVER_SEARCH)
                    /// M: OUTBOX and DRAFT folder is not show remote footer view, if
                    // search count is 0, just show "No Messages".
                    || mFolder.isType(UIProvider.FolderType.OUTBOX)
                    || mFolder.isType(UIProvider.FolderType.DRAFT))) {
                mListView.setEmptyView(mEmptyView);
            } else {
                //TS: zheng.zou 2015-3-26 EMAIL BUGFIX_958910 ADD_S
                mListView.setEmptyView(null);
                //TS: zheng.zou 2015-3-26 EMAIL BUGFIX_958910 ADD_E
            }
            /// @}
            */
            //TS: junwei-xu 2015-12-25 EMAIL BUGFIX-1193902 DEL_E
        }
    }

  //TS: tao.gan 2015-10-12 EMAIL FEATURE-559891 ADD_S
    public ConversationListHeader getListHeader() {
        return mListHeader;
    }
  //TS: tao.gan 2015-10-12 EMAIL FEATURE-559891 ADD_E

    //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_S
    private boolean hasUpdateTimeView(){
        boolean hasUpdateTimeView = false;
        boolean lastUpdateTimeOn = PLFUtils.getBoolean(mActivity.getActivityContext(), "feature_email_lastUpdateTime_on");
        if(lastUpdateTimeOn && (mAccount != null && !mAccount.getAccountId().equalsIgnoreCase("Account Id"))){
            hasUpdateTimeView = true;
        }
        return hasUpdateTimeView;
    }
    /**
     *update last update time.
     */
    private void updateHeaderView() {
        // update time stamp
        TextView timeStamp = (TextView)mConversationUpdateTimeView.findViewById(R.id.update_time);
        long time = (mViewContext.folder == null) ? 0 : mFolder.mSyncTime;
        String timeStampText;
        if (time == 0 && getActivity() != null) {
            timeStampText = getActivity().getResources().getString(R.string.conversation_update_unknown_time_stamp);
        } else {
            timeStampText = getElapseTime(time);
        }
        timeStamp.setText(" " + timeStampText);
    }

    /**
     *judge the folder whether to need update time view.
     */
    private boolean needListHeader(){
        if(mViewContext == null || mViewContext.folder == null && getActivity() == null){
            return false;
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
        if (!SortHelper.isTimeOrder(SortHelper.getCurrentSort())){
            return false;
        }
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E
        String protocol = com.tct.emailcommon.provider.Account.getProtocol(getActivity(),mViewContext.folder.mAccountKey);
        boolean isPopAccount = false;
        if(protocol != null && HostAuth.SCHEME_POP3.equalsIgnoreCase(protocol)){
            isPopAccount = true;
        }
        if ((mViewContext != null) && (mViewContext.folder != null) && mViewContext.folder.isSyncable()
                && (mViewContext.folder.type != UIProvider.FolderType.OUTBOX)
                && (mViewContext.folder.type != UIProvider.FolderType.SEARCH)
                && !(isPopAccount && mViewContext.folder.type == UIProvider.FolderType.SENT)
                && !(isPopAccount && mViewContext.folder.type == UIProvider.FolderType.TRASH)){
            return true;
        }else {
            return false;
        }
    }

    private String getElapseTime(long time) {
        long now = System.currentTimeMillis();
        long elapseTime = now - time;
        String displayTime;
        if (elapseTime < 0) {
            // abnormal time, this may occur when user change system time to a wrong time
            displayTime = (String) DateUtils.getRelativeTimeSpanString(mActivity.getActivityContext(), time);
        } else if (elapseTime < DateUtils.DAY_IN_MILLIS) {
            //within one day
            displayTime = (String) DateUtils.getRelativeTimeSpanString(mActivity.getActivityContext(), time);
            displayTime = mActivity.getActivityContext().getString(R.string.conversation_time_elapse_today) + ", " + displayTime;
        } else {
            //beyond one day
            java.text.DateFormat timeFormat = DateFormat.getTimeFormat(mActivity.getActivityContext());
            Date date = new Date(time);
            String dateText = DateUtils.formatDateTime(mActivity.getActivityContext(), time, DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_MONTH);
            displayTime = dateText + ", " + timeFormat.format(date);
        }

        return displayTime;
    }

    //TS: yanhua.chen 2015-10-28 EMAIL FR_1098700 ADD_E
}
