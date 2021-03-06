/*******************************************************************************
 *      Copyright (C) 2012 Google Inc.
 *      Licensed to The Android Open Source Project.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *******************************************************************************/
 /* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date       |      author      |         Key        |       comment      */
/* --------------|----------------  |--------------------|------------------- */
/* 11/28/2014    |     wei.huang    |      FR846709      | ] Email will       */
/*               |                  |                    | force close and    */
/*               |                  |                    |  back to homescreen*/
/* --------------|------------------|--------------------|--------------------*/
/* 01/12/2014    | Zhenhua.Fan      |      PR-854923     |Move to Dialog      */
/*               |                  |                    |should not show in  */
/*               |                  |                    |Combine View        */
/* ----------    |------------------|--------------------|-----------------   */
/******************************************************************************/
/*
 ===========================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================================================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-844194  2014/11/26   zhaotianyong    [Android5.0][Email][Crash] Email crashs when first launching it
 *BUGFIX-845345  2014/12/19   wenggangjin     [Android5.0][Email][UE] Should show selection info at top of screen
 *BUGFIX-879393  2014/12/20   xiaolin.li      [Android5.0][Email][Monkey][Crash] com.tct.email crash caused by java.lang.NullPointerException
 *BUGFIX-887553  2014/12/30   xiaolin.li      [Email]Quick horizontal sliding flash back in the mail details interface
 *BUGFIX-881447  2014/12/31   wenggangjin     [Email]Can't click "retry" icon
 *BUGFIX-879468  2014/1/5     junwei-xu       [Email]Add unread widget to the desktop after the restart the phone click into the inbox
 *BUGFIX-881437  2015/1/22    junwei-xu       [Email]Click "Drafts" can't pop up "Move to inbox..."
 *BUGFIX-913979  2015-02-05   wenggangjin     [REG][Exchange]Exchange search very slowly and can not search anything
 *BUGFIX-927510  2015-02-09   wenggangjin     [Android5.0][Email][Crash] Email app crash when re-launching it after configured an account
 *BUGFIX-948137  2015-03-18   zhonghua.tuo    [Monitor][Email]All Email account disappear sometimes
 *BUGFIX-942796  2015-03-24   zheng.zou       [Email] Improve Email search function.
 *BUGFIX-957916  2015/4/2     junwei-xu       [Android5.0][Email]No response when we touch the folder name in some folders
 *BUGFIX-968391  2015/4/5     gangjin.weng    [Android5.0][Email][REG]Can't delete words in server search by sougou input method
 *BUGFIX-991085     2015/03/30   jin.dong      [Email]After the web side to change the password, MS without prompting
 *BUGFIX-1022808  2015/6/23   chao zhang      [Email]After change password,TCL exchange can not work,always in "Getting your messages" page
 *BUGFIX-1027389  2015/7/7   yanhua.chen      [Email]It's no response when tap report after prompt internal error Edit Notification
 *BUGFIX-1355979  2016/01/15  chao-zhang     [Android M][Email][Force close]Mutiple press UNDO Email force close
 ===========================================================================================================
 */
package com.tct.mail.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Set;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.Observable;
import android.graphics.Outline;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.tct.email.EmailApplication;
import com.tct.email.R;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.SearchParams;
import com.tct.fw.google.common.base.Objects;
import com.tct.fw.google.common.collect.ImmutableList;
import com.tct.fw.google.common.collect.Lists;
import com.tct.fw.google.common.collect.Sets;
import com.tct.mail.ConversationListContext;
import com.tct.mail.MailLogService;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.analytics.AnalyticsTimer;
import com.tct.mail.analytics.AnalyticsUtils;
import com.tct.mail.browse.ConfirmDialogFragment;
import com.tct.mail.browse.ConversationCursor;
import com.tct.mail.browse.ConversationCursor.ConversationOperation;
import com.tct.mail.browse.ConversationItemViewModel;
import com.tct.mail.browse.ConversationMessage;
import com.tct.mail.browse.ConversationPagerController;
import com.tct.mail.browse.SelectedConversationsActionMenu;
import com.tct.mail.browse.SyncErrorDialogFragment;
import com.tct.mail.browse.UndoCallback;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.content.CursorCreator;
import com.tct.mail.content.ObjectCursor;
import com.tct.mail.content.ObjectCursorLoader;
import com.tct.mail.preferences.AccountPreferences;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.ConversationInfo;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.FolderWatcher;
import com.tct.mail.providers.MailAppProvider;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.SuggestionsProvider;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.AccountCursorExtraKeys;
import com.tct.mail.providers.UIProvider.AutoAdvance;
import com.tct.mail.providers.UIProvider.ConversationColumns;
import com.tct.mail.providers.UIProvider.ConversationOperations;
import com.tct.mail.providers.UIProvider.FolderCapabilities;
import com.tct.mail.providers.UIProvider.FolderType;
import com.tct.mail.ui.ActionableToastBar.ActionClickedListener;
import com.tct.mail.utils.*;
import com.tct.permission.PermissionUtil;
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.base.Objects;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//TS: MOD by wenggangjin for CONFLICT_20001 END
//[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
/*
=================================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ====================================
*BUGFIX-883925  2015/1/7        jian.xu      Click on the leftmost mail, return icon anomaly
*BUGFIX-902637  2015-01-20   wenggangjin     [Email]The menu display wrong after some oprations in mark list screen
 *BUGFIX-941849  2015-03-10   qing.liang     [Android5.0][Email][Force close]Email force close when tapping 'Undo' after rotating screen.
*BUGFIX-947440  2015/03/12    ke.ma          [Email] Need to add Email FAB button shadow effect.
*BUGFIX-944708  2015/03/18    zheng.zou      [Email]Can not stop search email when no network
*BUGFIX-976622  2015/4/24     yanhua.chen    [Monitor][FC][Email]Email flash back when open email from widget
*BUGFIX-976970  2015/5/08     zheng.zou      [Email]Portrait and landscape screen to switch each other occur black screen
*BUGFIX_996919  2015/06/04    zheng.zou       [Email](new) draft auto saving & discard ui change
*BUGFIX_1013807 2015/06/09    jin.dong       [SW][Email][Monitor] Return to login interface after login account
*BUGFIX_1019473 2015/07/01    jin.dong       [Android5.0][Email] Not highlight search word when search on server.
*BUGFIX_552138  2015/09/01    zheng.zou      [Email](new) draft auto saving & discard ui change
*BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
*FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
*FEATURE-559893 2015/09/11   tao.gan         [Email]Auto hiding action bar in mail box list
*BUGFIX_1065353 2015/09/08    kaifeng.lu     [Performance][Email]Email is too slow and always refresh emails after rotating screen when loading email content via data flow.
*BUGFIX_721230  2015/10/22    zheng.zou      [Android L][Email]The compose icon overlap the UNDO notification
*TASK-869664    2015/11/25    zheng.zou      [Email]Android M Permission Upgrade
*BUGFIX_1101083 2015/11/10    kaifeng.lu     [Android L][Email][Monitor] Cannot exit Inbox by pressing 'Back' key after stress test
*BUGFIX_1126514 2015/12/23    chao-zhang     [Monkey][Force Close][Email]Email crashes during monkey test
=================================================================================
*/
//[FEATURE]-Add-END by TSNJ,Zhenhua.Fan

/**
 * This is an abstract implementation of the Activity Controller. This class
 * knows how to respond to menu items, state changes, layout changes, etc. It
 * weaves together the views and listeners, dispatching actions to the
 * respective underlying classes.
 * <p>
 * Even though this class is abstract, it should provide default implementations
 * for most, if not all the methods in the ActivityController interface. This
 * makes the task of the subclasses easier: OnePaneActivityController and
 * TwoPaneActivityController can be concise when the common functionality is in
 * AbstractActivityController.
 * </p>
 * <p>
 * In the Gmail codebase, this was called BaseActivityController
 * </p>
 */
public abstract class AbstractActivityController implements ActivityController,
        EmptyFolderDialogFragment.EmptyFolderDialogFragmentListener, View.OnClickListener {
    // Keys for serialization of various information in Bundles.
    /** TCT: Tag for save global search */
    private static final String SAVED_GLOBAL_SEARCH = "saved-global-search";
    /** TCT: Tag for {@link ConversationListContext#localSearch} */
    private static final String SAVED_LOCAL_SEARCH = "saved-local-search";
    /** Tag for {@link #mAccount} */
    private static final String SAVED_ACCOUNT = "saved-account";
    /** Tag for {@link #mFolder} */
    private static final String SAVED_FOLDER = "saved-folder";
    /** Tag for {@link #mCurrentConversation} */
    private static final String SAVED_CONVERSATION = "saved-conversation";
    /** Tag for {@link #mSelectedSet} */
    private static final String SAVED_SELECTED_SET = "saved-selected-set";
    /** Tag for {@link ActionableToastBar#getOperation()} */
    private static final String SAVED_TOAST_BAR_OP = "saved-toast-844469bar-op";
    private static final String SAVED_UNDO_CONVERSATION = "saved-undo-conversation";
    private static final String SAVED_UNDO_ACTION = "saved-undo-action";
    /** Tag for {@link #mFolderListFolder} */
    private static final String SAVED_HIERARCHICAL_FOLDER = "saved-hierarchical-folder";
    /** Tag for {@link ConversationListContext#searchQuery} */
    private static final String SAVED_QUERY = "saved-query";
    /** Tag for {@link #mDialogAction} */
    private static final String SAVED_ACTION = "saved-action";
    /** Tag for {@link #mDialogFromSelectedSet} */
    private static final String SAVED_ACTION_FROM_SELECTED = "saved-action-from-selected";
    /** Tag for {@link #mDetachedConvUri} */
    private static final String SAVED_DETACHED_CONV_URI = "saved-detached-conv-uri";
    /** Key to store {@link #mInbox}. */
    private static final String SAVED_INBOX_KEY = "m-inbox";
    /** Key to store {@link #mConversationListScrollPositions} */
    private static final String SAVED_CONVERSATION_LIST_SCROLL_POSITIONS =
            "saved-conversation-list-scroll-positions";
    private static final String SAVED_DRAFT_MSG_ID = "saved-discard-msg-id";

    /** Tag used when loading a wait fragment */
    protected static final String TAG_WAIT = "wait-fragment";
    /** Tag used when loading a conversation list fragment. */
    public static final String TAG_CONVERSATION_LIST = "tag-conversation-list";
    /** Tag used when loading a custom fragment. */
    protected static final String TAG_CUSTOM_FRAGMENT = "tag-custom-fragment";

    /** Key to store an account in a bundle */
    private final String BUNDLE_ACCOUNT_KEY = "account";
    /** Key to store a folder in a bundle */
    private final String BUNDLE_FOLDER_KEY = "folder";
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
    /** key to store check status for star toggle */
    private final String BUNDLE_CHECK_STATUS_KEY = "star-check-status";
    /** check status for star toggle in actionbar */
    private boolean mCheckStatus = false;
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
    /**
     * Key to set a flag for the ConversationCursorLoader to ignore any
     * initial load limit that may be set by the Account. Instead,
     * perform a full load instead of the full-stage load.
     */
    private final String BUNDLE_IGNORE_INITIAL_CONVERSATION_LIMIT_KEY =
            "ignore-initial-conversation-limit";
    private final String BUNDLE_CONVERSATION_ORDER_KEY =
            "conversation-order";     //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD

    protected Account mAccount;
    protected Folder mFolder;
    protected Folder mInbox;
    /** True when {@link #mFolder} is first shown to the user. */
    private boolean mFolderChanged = false;
    protected ActionBarController mActionBarController;
    protected final MailActivity mActivity;
    protected final Context mContext;
    private final FragmentManager mFragmentManager;
    protected final RecentFolderList mRecentFolderList;
    protected ConversationListContext mConvListContext;
    protected Conversation mCurrentConversation;
    /**
     * The hash of {@link #mCurrentConversation} in detached mode. 0 if we are not in detached mode.
     */
    private Uri mDetachedConvUri;

    /** A map of {@link Folder} {@link Uri} to scroll position in the conversation list. */
    private final Bundle mConversationListScrollPositions = new Bundle();

    /** A {@link android.content.BroadcastReceiver} that suppresses new e-mail notifications. */
    private SuppressNotificationReceiver mNewEmailReceiver = null;

    /** Handler for all our local runnables. */
    protected Handler mHandler = new Handler();

    /**
     * The current mode of the application. All changes in mode are initiated by
     * the activity controller. View mode changes are propagated to classes that
     * attach themselves as listeners of view mode changes.
     */
    protected final ViewMode mViewMode;
    protected ContentResolver mResolver;
    protected boolean mHaveAccountList = false;
    private AsyncRefreshTask mAsyncRefreshTask;

    private boolean mDestroyed;

    /** True if running on tablet */
    private final boolean mIsTablet;

    /**
     * Are we in a point in the Activity/Fragment lifecycle where it's safe to execute fragment
     * transactions? (including back stack manipulation)
     * <p>
     * Per docs in {@link FragmentManager#beginTransaction()}, this flag starts out true, switches
     * to false after {@link Activity#onSaveInstanceState}, and becomes true again in both onStart
     * and onResume.
     */
    private boolean mSafeToModifyFragments = true;

    private final Set<Uri> mCurrentAccountUris = Sets.newHashSet();
    protected ConversationCursor mConversationListCursor;
    private final DataSetObservable mConversationListObservable = new MailObservable("List");

    /** Runnable that checks the logging level to enable/disable the logging service. */
    private Runnable mLogServiceChecker = null;
    /** List of all accounts currently known to the controller. This is never null. */
    private Account[] mAllAccounts = new Account[0];

    private FolderWatcher mFolderWatcher;

    private boolean mIgnoreInitialConversationLimit;

    /**
     * Interface for actions that are deferred until after a load completes. This is for handling
     * user actions which affect cursors (e.g. marking messages read or unread) that happen before
     * that cursor is loaded.
     */
    private interface LoadFinishedCallback {
        void onLoadFinished();
    }

    /** The deferred actions to execute when mConversationListCursor load completes. */
    private final ArrayList<LoadFinishedCallback> mConversationListLoadFinishedCallbacks =
            new ArrayList<LoadFinishedCallback>();

    private RefreshTimerTask mConversationListRefreshTask;

    /** Listeners that are interested in changes to the current account. */
    private final DataSetObservable mAccountObservers = new MailObservable("Account");
    /** Listeners that are interested in changes to the recent folders. */
    private final DataSetObservable mRecentFolderObservers = new MailObservable("RecentFolder");
    /** Listeners that are interested in changes to the list of all accounts. */
    private final DataSetObservable mAllAccountObservers = new MailObservable("AllAccounts");
    /** Listeners that are interested in changes to the current folder. */
    private final DataSetObservable mFolderObservable = new MailObservable("CurrentFolder");
    /** Listeners that are interested in changes to the Folder or Account selection */
    private final DataSetObservable mFolderOrAccountObservers =
            new MailObservable("FolderOrAccount");

    /**
     * Selected conversations, if any.
     */
    private final ConversationSelectionSet mSelectedSet = new ConversationSelectionSet();

    private final int mFolderItemUpdateDelayMs;

    /** Keeps track of selected and unselected conversations */
    final protected ConversationPositionTracker mTracker;

    /**
     * Action menu associated with the selected set.
     */
    SelectedConversationsActionMenu mCabActionMenu;

    /** The compose button floating over the conversation/search lists */
    protected View mFloatingComposeButton;
    protected ActionableToastBar mToastBar;
    protected ConversationPagerController mPagerController;

    // This is split out from the general loader dispatcher because its loader doesn't return a
    // basic Cursor
    /** Handles loader callbacks to create a convesation cursor. */
    private final ConversationListLoaderCallbacks mListCursorCallbacks =
            new ConversationListLoaderCallbacks();

    /** Object that listens to all LoaderCallbacks that result in {@link Folder} creation. */
    private final FolderLoads mFolderCallbacks = new FolderLoads();
    /** Object that listens to all LoaderCallbacks that result in {@link Account} creation. */
    private final AccountLoads mAccountCallbacks = new AccountLoads();

    /**
     * Matched addresses that must be shielded from users because they are temporary. Even though
     * this is instantiated from settings, this matcher is valid for all accounts, and is expected
     * to live past the life of an account.
     */
    private final VeiledAddressMatcher mVeiledMatcher;

    protected static final String LOG_TAG = LogTag.getLogTag();

    // Loader constants: Accounts
    /**
     * The list of accounts. This loader is started early in the application life-cycle since
     * the list of accounts is central to all other data the application needs: unread counts for
     * folders, critical UI settings like show/hide checkboxes, ...
     * The loader is started when the application is created: both in
     * {@link #onCreate(Bundle)} and in {@link #onActivityResult(int, int, Intent)}. It is never
     * destroyed since the cursor is needed through the life of the application. When the list of
     * accounts changes, we notify {@link #mAllAccountObservers}.
     */
    private static final int LOADER_ACCOUNT_CURSOR = 0;

    /**
     * The current account. This loader is started when we have an account. The mail application
     * <b>needs</b> a valid account to function. As soon as we set {@link #mAccount},
     * we start a loader to observe for changes on the current account.
     * The loader is always restarted when an account is set in {@link #setAccount(Account)}.
     * When the current account object changes, we notify {@link #mAccountObservers}.
     * A possible performance improvement would be to listen purely on
     * {@link #LOADER_ACCOUNT_CURSOR}. The current account is guaranteed to be in the list,
     * and would avoid two updates when a single setting on the current account changes.
     */
    private static final int LOADER_ACCOUNT_UPDATE_CURSOR = 1;

    // Loader constants: Conversations

    /** The conversation cursor over the current conversation list. This loader provides
     * a cursor over conversation entries from a folder to display a conversation
     * list.
     * This loader is started when the user switches folders (in {@link #updateFolder(Folder)},
     * or when the controller is told that a folder/account change is imminent
     * (in {@link #preloadConvList(Account, Folder)}. The loader is maintained for the life of
     * the current folder. When the user switches folders, the old loader is destroyed and a new
     * one is created.
     *
     * When the conversation list changes, we notify {@link #mConversationListObservable}.
     */
    private static final int LOADER_CONVERSATION_LIST = 10;

    // Loader constants: misc
    /**
     * The loader that determines whether the Warm welcome tour should be displayed for the user.
     */
    public static final int LOADER_WELCOME_TOUR = 20;

    /**
     * The load which loads accounts for the welcome tour.
     */
    public static final int LOADER_WELCOME_TOUR_ACCOUNTS = 21;

    // Loader constants: Folders

    /** The current folder. This loader watches for updates to the current folder in a manner
     * analogous to the {@link #LOADER_ACCOUNT_UPDATE_CURSOR}. Updates to the current folder
     * might be due to server-side changes (unread count), or local changes (sync window or sync
     * status change).
     * The change of current folder calls {@link #updateFolder(Folder)}.
     * This is responsible for restarting a loader using the URI of the provided folder. When the
     * loader returns, the current folder is updated and consumers, if any, are notified.
     * When the current folder changes, we notify {@link #mFolderObservable}
     */
    private static final int LOADER_FOLDER_CURSOR = 30;

    /**
     * The list of recent folders. Recent folders are shown in the DrawerFragment. The recent
     * folders are tied to the current account being viewed. When the account is changed,
     * we restart this loader to retrieve the recent accounts. Recents are pre-populated for
     * phones historically, when they were displayed in the spinner. On the tablet,
     * they showed in the {@link FolderListFragment} and were not-populated.  The code to
     * pre-populate the recents is somewhat convoluted: when the loader returns a short list of
     * recent folders, it issues an update on the Recent Folder URI. The underlying provider then
     * does the appropriate thing to populate recent folders, and notify of a change on the cursor.
     * Recent folders are needed for the life of the current account.
     * When the recent folders change, we notify {@link #mRecentFolderObservers}.
     */
    private static final int LOADER_RECENT_FOLDERS = 31;
    /**
     * The primary inbox for the current account. The mechanism to load the default inbox for the
     * current account is (sadly) different from loading other folders. The method
     * {@link #loadAccountInbox()} is called, and it restarts this loader. When the loader returns
     * a valid cursor, we create a folder, call {@link #onFolderChanged{Folder)} eventually
     * calling {@link #updateFolder(Folder)} which starts a loader {@link #LOADER_FOLDER_CURSOR}
     * over the current folder.
     * When we have a valid cursor, we destroy this loader, This convoluted flow is historical.
     */
    private static final int LOADER_ACCOUNT_INBOX = 32;

    /**
     * The fake folder of search results for a term. When we search for a term,
     * a new activity is created with {@link Intent#ACTION_SEARCH}. For this new activity,
     * we start a loader which returns conversations that match the user-provided query.
     * We destroy the loader when we obtain a valid cursor since subsequent searches will create
     * a new activity.
     */
    private static final int LOADER_SEARCH = 33;
    /**
     * The initial folder at app start. When the application is launched from an intent that
     * specifies the initial folder (notifications/widgets/shortcuts),
     * then we extract the folder URI from the intent, but we cannot trust the folder object. Since
     * shortcuts and widgets persist past application update, they might have incorrect
     * information encoded in them. So, to obtain a {@link Folder} object from a {@link Uri},
     * we need to start another loader. Upon obtaining a valid cursor, the loader is destroyed.
     * An additional complication arises if we have to view a specific conversation within this
     * folder. This is the case when launching the app from a single conversation notification
     * or tapping on a specific conversation in the widget. In these cases, the conversation is
     * saved in {@link #mConversationToShow} and is retrieved when the loader returns.
     */
    public static final int LOADER_FIRST_FOLDER = 34;

    /**
     * Guaranteed to be the last loader ID used by the activity. Loaders are owned by Activity or
     * fragments, and within an activity, loader IDs need to be unique. A hack to ensure that the
     * {@link FolderWatcher} can create its folder loaders without clashing with the IDs of those
     * of the {@link AbstractActivityController}. Currently, the {@link FolderWatcher} is the only
     * other class that uses this activity's LoaderManager. If another class needs activity-level
     * loaders, consider consolidating the loaders in a central location: a UI-less fragment
     * perhaps.
     */
    public static final int LAST_LOADER_ID = 35;
    /**
     * T: local search loader.
     */
    private static final int LOADER_LOCALSEARCH_CONVERSATION_LIST = 110;

    /**

    /**
     * Guaranteed to be the last loader ID used by the Fragment. Loaders are owned by Activity or
     * fragments, and within an activity, loader IDs need to be unique. Currently,
     * SectionedInboxTeaserView is the only class that uses the
     * {@link ConversationListFragment}'s LoaderManager.
     */
    public static final int LAST_FRAGMENT_LOADER_ID = 1000;

    /** Code returned after an account has been added. */
    private static final int ADD_ACCOUNT_REQUEST_CODE = 1;
    /** Code returned when the user has to enter the new password on an existing account. */
    private static final int REAUTHENTICATE_REQUEST_CODE = 2;
    /** Code returned when the previous activity needs to navigate to a different folder
     *  or account */
    private static final int CHANGE_NAVIGATION_REQUEST_CODE = 3;
    public static final String EXTRA_FOLDER = "extra-folder";
    public static final String EXTRA_ACCOUNT = "extra-account";
    private static final int DRAFT_SAVE_PRIORITY = 10;   //TS: zheng.zou 2015-07-14 EMAIL FEATURE_996919 ADD

    /** The pending destructive action to be carried out before swapping the conversation cursor.*/
    private DestructiveAction mPendingDestruction;
    protected AsyncRefreshTask mFolderSyncTask;
    private Folder mFolderListFolder;
    private boolean mIsDragHappening;
    private final int mShowUndoBarDelay;
    private boolean mRecentsDataUpdated;
    /** A wait fragment we added, if any. */
    private WaitFragment mWaitFragment;
    /** True if we have results from a search query */
    private boolean mHaveSearchResults = false;
    /** If a confirmation dialog is being show, the listener for the positive action. */
    private OnClickListener mDialogListener;
    /**
     * If a confirmation dialog is being show, the resource of the action: R.id.delete, etc.  This
     * is used to create a new {@link #mDialogListener} on orientation changes.
     */
    private int mDialogAction = -1;
    /**
     * If a confirmation dialog is being shown, this is true if the dialog acts on the selected set
     * and false if it acts on the currently selected conversation
     */
    private boolean mDialogFromSelectedSet;
    //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_S
    private int mUndoAction = -1;
    private Conversation mUndoConversation = null;
    //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_E

    /** Which conversation to show, if started from widget/notification. */
    private Conversation mConversationToShow = null;

    /**
     * A temporary reference to the pending destructive action that was deferred due to an
     * auto-advance transition in progress.
     * <p>
     * In detail: when auto-advance triggers a mode change, we must wait until the transition
     * completes before executing the destructive action to ensure a smooth mode change transition.
     * This member variable houses the pending destructive action work to be run upon completion.
     */
    private Runnable mAutoAdvanceOp = null;

    protected DrawerLayout mDrawerContainer;
    protected View mDrawerPullout;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected ListView mListViewForAnimating;
    protected boolean mHasNewAccountOrFolder;
    private boolean mConversationListLoadFinishedIgnored;
    private final MailDrawerListener mDrawerListener = new MailDrawerListener();
    private boolean mHideMenuItems;

    private final DrawIdler mDrawIdler = new DrawIdler();
    // TS: zhonghua.tuo 2015-03-18 EMAIL BUGFIX-948923 ADD_S
    //when first launch email,this will start twice,this field to avoid it
    private boolean mFirstLoadAccount = true;
    // TS: zhonghua.tuo 2015-03-18 EMAIL BUGFIX-948923 ADD_E

    public static final String SYNC_ERROR_DIALOG_FRAGMENT_TAG = "SyncErrorDialogFragment";
    /// TCT: If this is a global search
    private boolean mGlobalSearch;
    private DraftSaveBroadcastReceiver mDraftReceiver = new DraftSaveBroadcastReceiver();    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD
    private long mSavedDraftMsgId = -1;         // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    //animator when show view
    private AnimatorSet backAnimatorSet;
    //animator when hide view
    private AnimatorSet hideAnimatorSet;
    //indicate that whether toolbar and fab button is hidden
    private boolean mToolbarHidden = false;
    private HorizontalScrollView mSearchHeader;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
    //Count of mails in mail list
    private int mConversationCount;
    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E

    private final DataSetObserver mUndoNotificationObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            if (mConversationListCursor != null) {
                mConversationListCursor.handleNotificationActions();
            }
        }
    };

    private final HomeButtonListener mHomeButtonListener = new HomeButtonListener();

    public AbstractActivityController(MailActivity activity, ViewMode viewMode) {
        mActivity = activity;
        mFragmentManager = mActivity.getFragmentManager();
        mViewMode = viewMode;
        mContext = activity.getApplicationContext();
        mRecentFolderList = new RecentFolderList(mContext);
        mTracker = new ConversationPositionTracker(this);
        // Allow the fragment to observe changes to its own selection set. No other object is
        // aware of the selected set.
        mSelectedSet.addObserver(this);

        final Resources r = mContext.getResources();
        mFolderItemUpdateDelayMs = r.getInteger(R.integer.folder_item_refresh_delay_ms);
        mShowUndoBarDelay = r.getInteger(R.integer.show_undo_bar_delay_ms);
        mVeiledMatcher = VeiledAddressMatcher.newInstance(activity.getResources());
        mIsTablet = Utils.useTabletUI(r);
        mConversationListLoadFinishedIgnored = false;
    }

    @Override
    public Account getCurrentAccount() {
        return mAccount;
    }

    @Override
    public ConversationListContext getCurrentListContext() {
        return mConvListContext;
    }

    @Override
    public final ConversationCursor getConversationListCursor() {
        return mConversationListCursor;
    }

    /**
     * Check if the fragment is attached to an activity and has a root view.
     * @param in fragment to be checked
     * @return true if the fragment is valid, false otherwise
     */
    private static boolean isValidFragment(Fragment in) {
        return !(in == null || in.getActivity() == null || in.getView() == null);
    }

    /**
     * Get the conversation list fragment for this activity. If the conversation list fragment is
     * not attached, this method returns null.
     *
     * Caution! This method returns the {@link ConversationListFragment} after the fragment has been
     * added, <b>and</b> after the {@link FragmentManager} has run through its queue to add the
     * fragment. There is a non-trivial amount of time after the fragment is instantiated and before
     * this call returns a non-null value, depending on the {@link FragmentManager}. If you
     * need the fragment immediately after adding it, consider making the fragment an observer of
     * the controller and perform the task immediately on {@link Fragment#onActivityCreated(Bundle)}
     */
    public ConversationListFragment getConversationListFragment() {
        final Fragment fragment = mFragmentManager.findFragmentByTag(TAG_CONVERSATION_LIST);
        if (isValidFragment(fragment)) {
            return (ConversationListFragment) fragment;
        }
        return null;
    }

    /**
     * Returns the folder list fragment attached with this activity. If no such fragment is attached
     * this method returns null.
     *
     * Caution! This method returns the {@link FolderListFragment} after the fragment has been
     * added, <b>and</b> after the {@link FragmentManager} has run through its queue to add the
     * fragment. There is a non-trivial amount of time after the fragment is instantiated and before
     * this call returns a non-null value, depending on the {@link FragmentManager}. If you
     * need the fragment immediately after adding it, consider making the fragment an observer of
     * the controller and perform the task immediately on {@link Fragment#onActivityCreated(Bundle)}
     */
    protected FolderListFragment getFolderListFragment() {
        final String drawerPulloutTag = mActivity.getString(R.string.drawer_pullout_tag);
        final Fragment fragment = mFragmentManager.findFragmentByTag(drawerPulloutTag);
        if (isValidFragment(fragment)) {
            return (FolderListFragment) fragment;
        }
        return null;
    }

    /**
     * Initialize the action bar. This is not visible to OnePaneController and
     * TwoPaneController so they cannot override this behavior.
     */
    protected void initializeActionBar() {
        final ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        Intent intent = mActivity.getIntent();
        /// TCT: Action is search but no extra account means a global search
        final boolean isSearch = intent != null
                && Intent.ACTION_SEARCH.equals(intent.getAction())
                && intent.hasExtra(Utils.EXTRA_ACCOUNT);
        mActionBarController = isSearch ?
                new SearchActionBarController(mContext) :
                new ActionBarController(mContext);
        mActionBarController.initialize(mActivity, this, actionBar);

        // init the action bar to allow the 'up' affordance.
        // any configurations that disallow 'up' should do that later.
        mActionBarController.setBackButton();
    }

    /**
     * Attach the action bar to the activity.
     */
    private void attachActionBar() {
        final ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            // Show a title
            final int mask = ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME;
            actionBar.setDisplayOptions(mask, mask);
            mActionBarController.setViewModeController(mViewMode);
        }
    }

    /**
     * Returns whether the conversation list fragment is visible or not.
     * Different layouts will have their own notion on the visibility of
     * fragments, so this method needs to be overriden.
     *
     */
    protected abstract boolean isConversationListVisible();

    /**
     * If required, starts wait mode for the current account.
     */
    final void perhapsEnterWaitMode() {
        // If the account is not initialized, then show the wait fragment, since nothing can be
        // shown.
        if (mAccount.isAccountInitializationRequired()) {
            LogUtils.d(LOG_TAG,
                    "AAC.perhapsEnterWaitMode-->showWaitForInitialization,The account neet initialized" );
            showWaitForInitialization();
            return;
        }

        final boolean inWaitingMode = inWaitMode();
        final boolean isSyncRequired = mAccount.isAccountSyncRequired();
        //TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 ADD_S
        // attempts to get the inbox, actually, it would be null until folder sync completed.
        final Folder inbox = mFolderWatcher != null ? mFolderWatcher.getDefaultInbox(mAccount) : null;
        LogUtils.d(LOG_TAG,
                "AAC.perhapsEnterWaitMode inWaitingMode [%s], isSyncRequired [%s], inbox [%s]",
                inWaitingMode, isSyncRequired, inbox);
        //TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 ADD_E
        if (isSyncRequired) {
            if (inWaitingMode) {
                // Update the WaitFragment's account object
                updateWaitMode();
            } else {
                // Transition to waiting mode
                showWaitForInitialization();
                //TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 ADD_S
                LogUtils.d(LOG_TAG,
                        "AAC.perhapsEnterWaitMode-->showWaitForInitialization now!!!" );
                //TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 ADD_E
            }
        } else if (inWaitingMode && inbox != null) {//TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 MOD_S
            // Dismiss waiting mode
            hideWaitForInitialization();
        }
    }

    @Override
    public void switchToDefaultInboxOrChangeAccount(Account account) {
        LogUtils.d(LOG_TAG, "AAC.switchToDefaultAccount(%s)", account);
        if (mViewMode.isSearchMode()) {
            // We are in an activity on top of the main navigation activity.
            // We need to return to it with a result code that indicates it should navigate to
            // a different folder.
            final Intent intent = new Intent();
            intent.putExtra(AbstractActivityController.EXTRA_ACCOUNT, account);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
            return;
        }
        final boolean firstLoad = mAccount == null;
        final boolean switchToDefaultInbox = !firstLoad && account.uri.equals(mAccount.uri);
        // If the active account has been clicked in the drawer, go to default inbox
        if (switchToDefaultInbox) {
            loadAccountInbox();
            return;
        }
        changeAccount(account);
    }

    public void changeAccount(Account account) {
        LogUtils.d(LOG_TAG, "AAC.changeAccount(%s)", account);
        // Is the account or account settings different from the existing account?
        final boolean firstLoad = mAccount == null;
        final boolean accountChanged = firstLoad || !account.uri.equals(mAccount.uri);

        // If nothing has changed, return early without wasting any more time.
        if (!accountChanged && !account.settingsDiffer(mAccount)) {
            return;
        }
        // We also don't want to do anything if the new account is null
        if (account == null) {
            LogUtils.e(LOG_TAG, "AAC.changeAccount(null) called.");
            return;
        }
        final String emailAddress = account.getEmailAddress();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MailActivity.setNfcMessage(emailAddress);
            }
        });
        if (accountChanged) {
            commitDestructiveActions(false);
        }
        Analytics.getInstance().setCustomDimension(Analytics.CD_INDEX_ACCOUNT_TYPE,
                AnalyticsUtils.getAccountTypeForAccount(emailAddress));
        // Change the account here
        setAccount(account);
        // And carry out associated actions.
        cancelRefreshTask();
        if (accountChanged) {
            loadAccountInbox();
        }
        // Check if we need to force setting up an account before proceeding.
        if (mAccount != null && !Uri.EMPTY.equals(mAccount.settings.setupIntentUri)) {
            // Launch the intent!
            final Intent intent = new Intent(Intent.ACTION_EDIT);

            intent.setPackage(mContext.getPackageName());
            intent.setData(mAccount.settings.setupIntentUri);

            mActivity.startActivity(intent);
        }
    }

    /**
     * Adds a listener interested in change in the current account. If a class is storing a
     * reference to the current account, it should listen on changes, so it can receive updates to
     * settings. Must happen in the UI thread.
     */
    @Override
    public void registerAccountObserver(DataSetObserver obs) {
        mAccountObservers.registerObserver(obs);
    }

    /**
     * Removes a listener from receiving current account changes.
     * Must happen in the UI thread.
     */
    @Override
    public void unregisterAccountObserver(DataSetObserver obs) {
        mAccountObservers.unregisterObserver(obs);
    }

    @Override
    public void registerAllAccountObserver(DataSetObserver observer) {
        mAllAccountObservers.registerObserver(observer);
    }

    @Override
    public void unregisterAllAccountObserver(DataSetObserver observer) {
        mAllAccountObservers.unregisterObserver(observer);
    }

    @Override
    public Account[] getAllAccounts() {
        return mAllAccounts;
    }

    @Override
    public Account getAccount() {
        return mAccount;
    }

    @Override
    public void registerFolderOrAccountChangedObserver(final DataSetObserver observer) {
        mFolderOrAccountObservers.registerObserver(observer);
    }

    @Override
    public void unregisterFolderOrAccountChangedObserver(final DataSetObserver observer) {
        mFolderOrAccountObservers.unregisterObserver(observer);
    }

    /**
     * If the drawer is open, the function locks the drawer to the closed, thereby sliding in
     * the drawer to the left edge, disabling events, and refreshing it once it's either closed
     * or put in an idle state.
     */
    @Override
    public void closeDrawer(final boolean hasNewFolderOrAccount, Account nextAccount,
            Folder nextFolder) {
        if (!isDrawerEnabled()) {
            if (hasNewFolderOrAccount) {
                mFolderOrAccountObservers.notifyChanged();
            }
            return;
        }
        // If there are no new folders or accounts to switch to, just close the drawer
        if (!hasNewFolderOrAccount) {
            mDrawerContainer.closeDrawers();
            return;
        }
        // Otherwise, start preloading the conversation list for the new folder.
        if (nextFolder != null) {
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
            // reset star toggle status.
            mActionBarController.setCheckStatus(false);
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
            preloadConvList(nextAccount, nextFolder);
        }
        // Remember if the conversation list view is animating
        final ConversationListFragment conversationList = getConversationListFragment();
        if (conversationList != null) {
            mListViewForAnimating = conversationList.getListView();
        } else {
            // There is no conversation list to animate, so just set it to null
            mListViewForAnimating = null;
        }

        if (mDrawerContainer.isDrawerOpen(mDrawerPullout)) {
            // Lets the drawer listener update the drawer contents and notify the FolderListFragment
            mHasNewAccountOrFolder = true;
            mDrawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            // Drawer is already closed, notify observers that is the case.
            if (hasNewFolderOrAccount) {
                mFolderOrAccountObservers.notifyChanged();
            }
        }
    }

    /**
     * Load the conversation list early for the given folder. This happens when some UI element
     * (usually the drawer) instructs the controller that an account change or folder change is
     * imminent. While the UI element is animating, the controller can preload the conversation
     * list for the default inbox of the account provided here or to the folder provided here.
     *
     * @param nextAccount The account which the app will switch to shortly, possibly null.
     * @param nextFolder The folder which the app will switch to shortly, possibly null.
     */
    protected void preloadConvList(Account nextAccount, Folder nextFolder) {
        // Fire off the conversation list loader for this account already with a fake
        // listener.
        final Bundle args = new Bundle(2);
        if (nextAccount != null) {
            args.putParcelable(BUNDLE_ACCOUNT_KEY, nextAccount);
        } else {
            args.putParcelable(BUNDLE_ACCOUNT_KEY, mAccount);
        }
        if (nextFolder != null) {
            args.putParcelable(BUNDLE_FOLDER_KEY, nextFolder);
        } else {
            LogUtils.e(LOG_TAG, new Error(), "AAC.preloadConvList(): Got an empty folder");
        }
        mFolder = null;
        final LoaderManager lm = mActivity.getLoaderManager();
        lm.destroyLoader(LOADER_CONVERSATION_LIST);
        lm.initLoader(LOADER_CONVERSATION_LIST, args, mListCursorCallbacks);
    }

    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
    /**
     * Load conversation list when click the star toggle in actionbar.
     */
    private void loadConvListByStarToggle(Account nextAccount, Folder nextFolder) {
        final Bundle args = new Bundle(3);   //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD
        if (nextAccount != null) {
            args.putParcelable(BUNDLE_ACCOUNT_KEY, nextAccount);
        } else {
            args.putParcelable(BUNDLE_ACCOUNT_KEY, mAccount);
        }
        if (nextFolder != null) {
            args.putParcelable(BUNDLE_FOLDER_KEY, nextFolder);
        } else {
            LogUtils.e(LOG_TAG, new Error(), "AAC.loadConvListByStarToggle(): Got an empty folder");
        }
        args.putInt(BUNDLE_CONVERSATION_ORDER_KEY, SortHelper.getCurrentSort());  //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD
        final LoaderManager lm = mActivity.getLoaderManager();
        lm.destroyLoader(LOADER_CONVERSATION_LIST);
        lm.initLoader(LOADER_CONVERSATION_LIST, args, mListCursorCallbacks);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //save the check status for star toggle and reload conversation list.
        if (mCheckStatus != isChecked) {
            mCheckStatus = isChecked;
            loadConvListByStarToggle(mAccount, mFolder);
        }
    }

    @Override
    public boolean getCurrentStarToggleStatus() {
        return mCheckStatus;
    }
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

    /**
     * Initiates the async request to create a fake search folder, which returns conversations that
     * match the query term provided by the user. Returns immediately.
     * @param intent Intent that the app was started with. This intent contains the search query.
     */
    private void fetchSearchFolder(Intent intent) {
        final Bundle args = new Bundle(1);
        args.putString(ConversationListContext.EXTRA_SEARCH_QUERY, intent
                .getStringExtra(ConversationListContext.EXTRA_SEARCH_QUERY));
        /// TCT: add search field for remote search. @{
        args.putString(SearchParams.BUNDLE_QUERY_FIELD,
                intent.getStringExtra(SearchParams.BUNDLE_QUERY_FIELD));
        /// @}
        mActivity.getLoaderManager().restartLoader(LOADER_SEARCH, args, mFolderCallbacks);
    }

    @Override
    public void onFolderChanged(Folder folder, final boolean force) {
        if (isDrawerEnabled()) {
            /** If the folder doesn't exist, or its parent URI is empty,
             * this is not a child folder */
            final boolean isTopLevel = Folder.isRoot(folder);
            final int mode = mViewMode.getMode();
            mDrawerToggle.setDrawerIndicatorEnabled(
                    getShouldShowDrawerIndicator(mode, isTopLevel));
            mDrawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            mDrawerContainer.closeDrawers();
        }

        if (mFolder == null || !mFolder.equals(folder)) {
            // We are actually changing the folder, so exit cab mode
            exitCabMode();
        }

        final String query;
        //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-942796 MOD_S
        //keep search word after go back from view conversation
//        if (folder != null && folder.isType(FolderType.SEARCH)) {
//            query = mConvListContext.searchQuery;
//        } else {
//            query = null;
//        }
        if (mConvListContext != null) {
            query = mConvListContext.searchQuery;
        } else {
            query = null;
        }
        //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-942796 MOD_E

        changeFolder(folder, query, force);
        /** TCT: For global search, enter local search mode and search the text @{ */
        if (mGlobalSearch) {
            LogUtils.logFeature(LogTag.SEARCH_TAG, "[Global Search]Enter and execute local search");
            mActionBarController.expandSearch(mActivity.getIntent().getStringExtra(SearchManager.QUERY), null);
        }
        /** @{ */
    }

    /**
     * Sets the folder state without changing view mode and without creating a list fragment, if
     * possible.
     * @param folder the folder whose list of conversations are to be shown
     * @param query the query string for a list of conversations matching a search
     */
    private void setListContext(Folder folder, String query) {
        updateFolder(folder);
        final boolean localSearching = mConvListContext != null && mConvListContext.isLocalSearch();
        final String localSearchField = mConvListContext != null ? mConvListContext.getSearchField()
                : null;
        if (query != null) {
            mConvListContext = ConversationListContext.forSearchQuery(mAccount, mFolder, query);
        } else {
            mConvListContext = ConversationListContext.forFolder(mAccount, mFolder);
        }
        /// TCT: restore last local search params after re-instance list context.@{
        mConvListContext.setLocalSearch(localSearching);
        mConvListContext.setSearchField(localSearchField);
        /// @}
        cancelRefreshTask();
    }

    /**
     * Changes the folder to the value provided here. This causes the view mode to change.
     * @param folder the folder to change to
     * @param query if non-null, this represents the search string that the folder represents.
     * @param force <code>true</code> to force a folder change, <code>false</code> to disallow
     *          changing to the current folder
     */
    private void changeFolder(Folder folder, String query, final boolean force) {
        if (!Objects.equal(mFolder, folder)) {
            commitDestructiveActions(false);
        }
        if (folder != null && (!folder.equals(mFolder) || force)
                || (mViewMode.getMode() != ViewMode.CONVERSATION_LIST)) {
            if (folder != null && !folder.equals(mFolder)) {
                SortHelper.resetCurrentOrder();
            }
            setListContext(folder, query);
            showConversationList(mConvListContext);
            // Touch the current folder: it is different, and it has been accessed.
            // Prevent rare NPE  b/18017065
            if (mFolder != null) {
                mRecentFolderList.touchFolder(mFolder, mAccount);
            }
            //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_S
            if (folder != null && folder.isInbox()) {
                showManualRefreshInRoamingIfNeed();
            }
            //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_E

        }
        resetActionBarIcon();
    }

    @Override
    public void onFolderSelected(Folder folder) {
        onFolderChanged(folder, false /* force */);
    }

    /**
     * Adds a listener interested in change in the recent folders. If a class is storing a
     * reference to the recent folders, it should listen on changes, so it can receive updates.
     * Must happen in the UI thread.
     */
    @Override
    public void registerRecentFolderObserver(DataSetObserver obs) {
        mRecentFolderObservers.registerObserver(obs);
    }

    /**
     * Removes a listener from receiving recent folder changes.
     * Must happen in the UI thread.
     */
    @Override
    public void unregisterRecentFolderObserver(DataSetObserver obs) {
        mRecentFolderObservers.unregisterObserver(obs);
    }

    @Override
    public RecentFolderList getRecentFolders() {
        return mRecentFolderList;
    }

    @Override
    public void loadAccountInbox() {
        boolean handled = false;
        if (mFolderWatcher != null) {
            final Folder inbox = mFolderWatcher.getDefaultInbox(mAccount);
            if (inbox != null) {
                onFolderChanged(inbox, false /* force */);
                handled = true;
            }
        }
        if (!handled) {
            LogUtils.d(LOG_TAG, "Starting a LOADER_ACCOUNT_INBOX for %s", mAccount);
            restartOptionalLoader(LOADER_ACCOUNT_INBOX, mFolderCallbacks, Bundle.EMPTY);
            //TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 ADD_S
            LogUtils.d(LOG_TAG, "Restart a LOADER_ACCOUNT_INBOX");
            // if the inbox has not be prepared when we change to new account, we must revert everything
            /// that depends on a mailbox. such as loader, actionbar @{
            final LoaderManager lm = mActivity.getLoaderManager();
            lm.destroyLoader(LOADER_FOLDER_CURSOR);
            //  Also destroy the loader LOADER_CONVERSATION_LIST.
            lm.destroyLoader(LOADER_CONVERSATION_LIST);
            mInbox = null;
            mFolder = null;
            mActionBarController.setFolder(mFolder);
            perhapsEnterWaitMode();
            return;
            //TS: chaozhang 2015-6-26 EMAIL BUGFIX_1022808 ADD_E
        }
        final int mode = mViewMode.getMode();
        if (mode == ViewMode.UNKNOWN || mode == ViewMode.WAITING_FOR_ACCOUNT_INITIALIZATION) {
            mViewMode.enterConversationListMode();
        }
    }

    @Override
    public void setFolderWatcher(FolderWatcher watcher) {
        mFolderWatcher = watcher;
    }

    /**
     * Marks the {@link #mFolderChanged} value if the newFolder is different from the existing
     * {@link #mFolder}. This should be called immediately <b>before</b> assigning newFolder to
     * mFolder.
     * @param newFolder the new folder we are switching to.
     */
    private void setHasFolderChanged(final Folder newFolder) {
        // We should never try to assign a null folder. But in the rare event that we do, we should
        // only set the bit when we have a valid folder, and null is not valid.
        if (newFolder == null) {
            return;
        }
        // If the previous folder was null, or if the two folders represent different data, then we
        // consider that the folder has changed.
        if (mFolder == null || !newFolder.equals(mFolder)) {
            mFolderChanged = true;
        }
    }

    /**
     * Sets the current folder if it is different from the object provided here. This method does
     * NOT notify the folder observers that a change has happened. Observers are notified when we
     * get an updated folder from the loaders, which will happen as a consequence of this method
     * (since this method starts/restarts the loaders).
     * @param folder The folder to assign
     */
    private void updateFolder(Folder folder) {
        if (folder == null || !folder.isInitialized()) {
            LogUtils.e(LOG_TAG, new Error(), "AAC.setFolder(%s): Bad input", folder);
            return;
        }
        if (folder.equals(mFolder)) {
            LogUtils.d(LOG_TAG, "AAC.setFolder(%s): Input matches mFolder", folder);
            return;
        }
        final boolean wasNull = mFolder == null;
        LogUtils.d(LOG_TAG, "AbstractActivityController.setFolder(%s)", folder.name);
        final LoaderManager lm = mActivity.getLoaderManager();
        // updateFolder is called from AAC.onLoadFinished() on folder changes.  We need to
        // ensure that the folder is different from the previous folder before marking the
        // folder changed.
        setHasFolderChanged(folder);
        mFolder = folder;

        // We do not need to notify folder observers yet. Instead we start the loaders and
        // when the load finishes, we will get an updated folder. Then, we notify the
        // folderObservers in onLoadFinished.
        mActionBarController.setFolder(mFolder);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        mActivity.invalidateOptionsMenu();
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

        // Only when we switch from one folder to another do we want to restart the
        // folder and conversation list loaders (to trigger onCreateLoader).
        // The first time this runs when the activity is [re-]initialized, we want to re-use the
        // previous loader's instance and data upon configuration change (e.g. rotation).
        // If there was not already an instance of the loader, init it.
        if (lm.getLoader(LOADER_FOLDER_CURSOR) == null) {
            lm.initLoader(LOADER_FOLDER_CURSOR, Bundle.EMPTY, mFolderCallbacks);
        } else {
            lm.restartLoader(LOADER_FOLDER_CURSOR, Bundle.EMPTY, mFolderCallbacks);
        }
        if (!wasNull && lm.getLoader(LOADER_CONVERSATION_LIST) != null) {
            // If there was an existing folder AND we have changed
            // folders, we want to restart the loader to get the information
            // for the newly selected folder
            lm.destroyLoader(LOADER_CONVERSATION_LIST);
        }
        /// TCT: Fix the empty view will always flash out here. no need show empty view when folde changing,
        // folders, we want to restart the loader to get the information 	/// cause loadConversationListData would always destroy loader,and the cursor always be empty when do this. @{
        // for the newly selected folder
        final ConversationListFragment conversationList = getConversationListFragment();
        // TS: zheng.zou 2015-05-8 EMAIL BUGFIX-976970 DEL_S
//        lm.destroyLoader(LOADER_CONVERSATION_LIST);
        // TS: zheng.zou 2015-05-8 EMAIL BUGFIX-976970 DEL_E
        if (conversationList != null) {
            conversationList.getListView().setEmptyView(null);
        }
        loadConversationListData(true);
    }

    /**
     * TCT: Move data load code to an independent function, if we just want refresh loader data.
     * call this to refresh cursor data(local search or update folder)
     */
    private void loadConversationListData(boolean folderUpdated) {
        if (mFolder == null || !mFolder.isInitialized()) {
            LogUtils.e(LOG_TAG, new Error(), "AAC.setFolder(%s): Bad input", mFolder);
            return;
        }
        final LoaderManager lm = mActivity.getLoaderManager();

        if (mConvListContext != null && mConvListContext.isLocalSearchExecuted()) {
            final Bundle args = new Bundle(4);
            args.putParcelable(BUNDLE_ACCOUNT_KEY, mAccount);
            args.putParcelable(BUNDLE_FOLDER_KEY, mFolder);
            args.putString(SearchParams.BUNDLE_QUERY_FIELD, mConvListContext.getSearchField());
            args.putString(SearchParams.BUNDLE_QUERY_TERM, mConvListContext.getSearchQuery());
            LogUtils.logFeature(
                    LogTag.SEARCH_TAG,
                    "loadConversationListData for local search query [%s], field [%s]",
                    mConvListContext.getSearchQuery(), mConvListContext.getSearchField());
            ///TCT: Before we start the local search loader, need cancel the normal conversation list
            // loader to avoid no need load and wrong result display by the load finish delay.@{
            if (lm.getLoader(LOADER_CONVERSATION_LIST) != null) {
                lm.destroyLoader(LOADER_CONVERSATION_LIST);
            }
            // @}

            lm.restartLoader(LOADER_LOCALSEARCH_CONVERSATION_LIST, args, mListCursorCallbacks);
        } else {

            ///TCT: Before we start the normal search loader, need cancel the local search conversation list
            // loader to avoid no need load and wrong result display by the load finish delay.@{
            if (lm.getLoader(LOADER_LOCALSEARCH_CONVERSATION_LIST) != null) {

                lm.destroyLoader(LOADER_LOCALSEARCH_CONVERSATION_LIST);

            }
            // @}

            // TS: kaifeng.lu 2015-09-8 EMAIL BUGFIX-1065353 DEL_S
//            final ConversationCursorLoader ccl = (ConversationCursorLoader) ((Object) lm
//                    .getLoader(LOADER_CONVERSATION_LIST));

//            if (ccl != null && !ccl.getUri().equals(mFolder.conversationListUri) && folderUpdated) {

                // If there was an existing folder AND we have changed
                // folders, we want to restart the loader to get the information
                // for the newly selected folder
//                lm.destroyLoader(LOADER_CONVERSATION_LIST);

//            }
            // TS: kaifeng.lu 2015-09-8 EMAIL BUGFIX-1065353 DEL_E

            final Bundle args = new Bundle(2);
            args.putParcelable(BUNDLE_ACCOUNT_KEY, mAccount);
            args.putParcelable(BUNDLE_FOLDER_KEY, mFolder);
            args.putBoolean(BUNDLE_IGNORE_INITIAL_CONVERSATION_LIMIT_KEY,
                    mIgnoreInitialConversationLimit);
            mIgnoreInitialConversationLimit = false;
            lm.initLoader(LOADER_CONVERSATION_LIST, args, mListCursorCallbacks);
        }
    }

    @Override
    public Folder getFolder() {
        return mFolder;
    }

    @Override
    public Folder getHierarchyFolder() {
        return mFolderListFolder;
    }

    @Override
    public void setHierarchyFolder(Folder folder) {
        mFolderListFolder = folder;
    }

    /**
     * The mail activity calls other activities for two specific reasons:
     * <ul>
     *     <li>To add an account. And receives the result {@link #ADD_ACCOUNT_REQUEST_CODE}</li>
     *     <li>To update the password on a current account. The result {@link
     *     #REAUTHENTICATE_REQUEST_CODE} is received.</li>
     * </ul>
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_ACCOUNT_REQUEST_CODE:
                // We were waiting for the user to create an account
                // TS: zhonghua.tuo 2015-03-18 EMAIL BUGFIX-948923 ADD_S
                mFirstLoadAccount = true;
                // TS: zhonghua.tuo 2015-03-18 EMAIL BUGFIX-948923 ADD_E
                if (resultCode == Activity.RESULT_OK) {
                    // restart the loader to get the updated list of accounts
                    mActivity.getLoaderManager().initLoader(LOADER_ACCOUNT_CURSOR, Bundle.EMPTY,
                            mAccountCallbacks);
                } else {
                    // The user failed to create an account, just exit the app
                    mActivity.finish();
                }
                break;
            case REAUTHENTICATE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // The user successfully authenticated, attempt to refresh the list
                    final Uri refreshUri = mFolder != null ? mFolder.refreshUri : null;
                    if (refreshUri != null) {
                        startAsyncRefreshTask(refreshUri);
                    }
                }
                break;
            case CHANGE_NAVIGATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // We have have received a result that indicates we need to navigate to a
                    // different folder or account. This happens if someone navigates using the
                    // drawer on the search results activity.
                    final Folder folder = data.getParcelableExtra(EXTRA_FOLDER);
                    final Account account = data.getParcelableExtra(EXTRA_ACCOUNT);
                    if (folder != null) {
                        onFolderSelected(folder);
                        mViewMode.enterConversationListMode();
                    } else if (account != null) {
                        switchToDefaultInboxOrChangeAccount(account);
                        mViewMode.enterConversationListMode();
                    }
                }
                break;
        }
    }

    //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_S
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_SAVE_ATTACHMENT
                || requestCode == PermissionUtil.REQ_CODE_PERMISSION_REDOWNLOAD_ATTACHMENT
                || requestCode == PermissionUtil.REQ_CODE_PERMISSION_VIEW_ATTACHMENT){    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 MOD
                for(String permission : permissions){
                    if(PermissionChecker.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED){
                         showNeedPermissionToast(R.string.permission_needed_to_save_attachment);
                    }
                }
        } else if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_SEE_CALENDAR) {
            for(String permission : permissions) {
                if (PermissionChecker.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                    showNeedPermissionToast(R.string.permission_needed_to_see_calendar);
                }
            }
        }
    }
    //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_E

    /**
     * Inform the conversation cursor that there has been a visibility change.
     * @param visible true if the conversation list is visible, false otherwise.
     */
    protected synchronized void informCursorVisiblity(boolean visible) {
        if (mConversationListCursor != null) {
            Utils.setConversationCursorVisibility(mConversationListCursor, visible, mFolderChanged);
            // We have informed the cursor. Subsequent visibility changes should not tell it that
            // the folder has changed.
            mFolderChanged = false;
        }
    }

    @Override
    public void onConversationListVisibilityChanged(boolean visible) {
        informCursorVisiblity(visible);
        commitAutoAdvanceOperation();

        // Notify special views
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null && convListFragment.getAnimatedAdapter() != null) {
            convListFragment.getAnimatedAdapter().onConversationListVisibilityChanged(visible);
        }
    }

    /**
     * Called when a conversation is visible. Child classes must call the super class implementation
     * before performing local computation.
     */
    @Override
    public void onConversationVisibilityChanged(boolean visible) {
        commitAutoAdvanceOperation();
    }

    /**
     * Commits any pending destructive action that was earlier deferred by an auto-advance
     * mode-change transition.
     */
    private void commitAutoAdvanceOperation() {
        if (mAutoAdvanceOp != null) {
            mAutoAdvanceOp.run();
            mAutoAdvanceOp = null;
        }
    }

    /**
     * Initialize development time logging. This can potentially log a lot of PII, and we don't want
     * to turn it on for shipped versions.
     */
    private void initializeDevLoggingService() {
        if (!MailLogService.DEBUG_ENABLED) {
            return;
        }
        // Check every 5 minutes.
        final int WAIT_TIME = 5 * 60 * 1000;
        // Start a runnable that periodically checks the log level and starts/stops the service.
        mLogServiceChecker = new Runnable() {
            /** True if currently logging. */
            private boolean mCurrentlyLogging = false;

            /**
             * If the logging level has been changed since the previous run, start or stop the
             * service.
             */
            private void startOrStopService() {
                // If the log level is already high, start the service.
                final Intent i = new Intent(mContext, MailLogService.class);
                final boolean loggingEnabled = MailLogService.isLoggingLevelHighEnough();
                if (mCurrentlyLogging == loggingEnabled) {
                    // No change since previous run, just return;
                    return;
                }
                if (loggingEnabled) {
                    LogUtils.e(LOG_TAG, "Starting MailLogService");
                    mContext.startService(i);
                } else {
                    LogUtils.e(LOG_TAG, "Stopping MailLogService");
                    mContext.stopService(i);
                }
                mCurrentlyLogging = loggingEnabled;
            }

            @Override
            public void run() {
                startOrStopService();
                mHandler.postDelayed(this, WAIT_TIME);
            }
        };
        // Start the runnable right away.
        mHandler.post(mLogServiceChecker);
    }

    /**
     * The application can be started from the following entry points:
     * <ul>
     *     <li>Launcher: you tap on the Gmail icon in the launcher. This is what most users think of
     *         as “Starting the app”.</li>
     *     <li>Shortcut: Users can make a shortcut to take them directly to a label.</li>
     *     <li>Widget: Shows the contents of a synced label, and allows:
     *     <ul>
     *         <li>Viewing the list (tapping on the title)</li>
     *         <li>Composing a new message (tapping on the new message icon in the title. This
     *         launches the {@link ComposeActivity}.
     *         </li>
     *         <li>Viewing a single message (tapping on a list element)</li>
     *     </ul>
     *
     *     </li>
     *     <li>Tapping on a notification:
     *     <ul>
     *         <li>Shows message list if more than one message</li>
     *         <li>Shows the conversation if the notification is for a single message</li>
     *     </ul>
     *     </li>
     *     <li>...and most importantly, the activity life cycle can tear down the application and
     *     restart it:
     *     <ul>
     *         <li>Rotate the application: it is destroyed and recreated.</li>
     *         <li>Navigate away, and return from recent applications.</li>
     *     </ul>
     *     </li>
     *     <li>Add a new account: fires off an intent to add an account,
     *     and returns in {@link #onActivityResult(int, int, android.content.Intent)} .</li>
     *     <li>Re-authenticate your account: again returns in onActivityResult().</li>
     *     <li>Composing can happen from many entry points: third party applications fire off an
     *     intent to compose email, and launch directly into the {@link ComposeActivity}
     *     .</li>
     * </ul>
     * {@inheritDoc}
     */
    @SuppressLint("NewApi")
    @Override
    public boolean onCreate(Bundle savedState) {
        initializeActionBar();
        initializeDevLoggingService();
        // Allow shortcut keys to function for the ActionBar and menus.
        mActivity.setDefaultKeyMode(Activity.DEFAULT_KEYS_SHORTCUT);
        mResolver = mActivity.getContentResolver();
        mNewEmailReceiver = new SuppressNotificationReceiver();
        mRecentFolderList.initialize(mActivity);
        mVeiledMatcher.initialize(this);

        mFloatingComposeButton = mActivity.findViewById(R.id.compose_button);

        //TS: ke.ma 2015-03-12 EMAIL BUGFIX-947440 ADD_S
        mFloatingComposeButton.setElevation(8);
        mFloatingComposeButton.setOutlineProvider(new ViewOutlineProvider() {

            @Override
            public void getOutline(View view, Outline outline) {
                // TODO Auto-generated method stub
                outline.setOval(0, 0, view.getWidth(), view.getWidth());
            }
        });
        //TS: ke.ma 2015-03-12 EMAIL BUGFIX-947440 ADD_E
        mFloatingComposeButton.setOnClickListener(this);

        if (isDrawerEnabled()) {
            mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerContainer,
//                    false,
//                    R.drawable.ic_drawer,
                    R.string.drawer_open, R.string.drawer_close);
            mDrawerContainer.setDrawerListener(mDrawerListener);
            mDrawerContainer.setDrawerShadow(
                    mContext.getResources().getDrawable(R.drawable.drawer_shadow), Gravity.START);

            mDrawerToggle.setDrawerIndicatorEnabled(isDrawerEnabled());
        } else {
            final ActionBar ab = mActivity.getSupportActionBar();
            ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
            ab.setHomeActionContentDescription(R.string.drawer_open);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // All the individual UI components listen for ViewMode changes. This
        // simplifies the amount of logic in the AbstractActivityController, but increases the
        // possibility of timing-related bugs.
        mViewMode.addListener(this);
        mPagerController = new ConversationPagerController(mActivity, this);
        mToastBar = findActionableToastBar(mActivity);
        attachActionBar();

        mDrawIdler.setRootView(mActivity.getWindow().getDecorView());

        final Intent intent = mActivity.getIntent();

        // Immediately handle a clean launch with intent, and any state restoration
        // that does not rely on restored fragments or loader data
        // any state restoration that relies on those can be done later in
        // onRestoreInstanceState, once fragments are up and loader data is re-delivered
        if (savedState != null) {
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
            // restore check status for star toggle
            mCheckStatus = savedState.getBoolean(BUNDLE_CHECK_STATUS_KEY, false);
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
            /// TCT: restore global search tag.
            if (savedState.containsKey(SAVED_GLOBAL_SEARCH)) {
                mGlobalSearch = savedState.getBoolean(SAVED_GLOBAL_SEARCH);
                LogUtils.logFeature(LogTag.SEARCH_TAG,
                        "onCreate restore mGlobalSearch [%s] ", mGlobalSearch);
            }
            if (savedState.containsKey(SAVED_ACCOUNT)) {
                setAccount((Account) savedState.getParcelable(SAVED_ACCOUNT));
            }
            if (savedState.containsKey(SAVED_FOLDER)) {
                final Folder folder = savedState.getParcelable(SAVED_FOLDER);
                /**
                 * TCT: Restore the local search or global search instance:
                 * 1. Restore the ConversationListContext from Bundle.
                 * 2. Restore query if in global search mode.
                 * 3. Update Local Search UI (ActionBarView)
                 * @{
                 */
                final Bundle bundle = savedState.getParcelable(SAVED_LOCAL_SEARCH);
                if (bundle != null) {
                    final ConversationListContext convListContext =
                    ConversationListContext.forBundle(bundle);
                    mConvListContext = convListContext;
                    LogUtils.logFeature(
                            LogTag.SEARCH_TAG,
                            "onCreate restore ConverationListContext from saved instance [%s] ",
                            mConvListContext);
                }
                String query = mConvListContext != null ? mConvListContext.getSearchQuery() : null;
                if (TextUtils.isEmpty(query) && mGlobalSearch) {
                    query = intent.getStringExtra(SearchManager.QUERY);
                    mConvListContext.setLocalSearch(true);
                    mConvListContext.setSearchQueryText(query);
                    LogUtils.logFeature(LogTag.SEARCH_TAG,
                            "onCreate restore global search query [%s]", mConvListContext);
                }
                setListContext(folder, query);
                if (mConvListContext.isLocalSearch()) {
                    LogUtils.logFeature(
                            LogTag.SEARCH_TAG,
                            "[Local Search] Enter and execute local search [%s]", query);
                    mActionBarController.expandSearch(query, mConvListContext.getSearchField());
                }
            /** @} */
            }
            if (savedState.containsKey(SAVED_ACTION)) {
                mDialogAction = savedState.getInt(SAVED_ACTION);
            }
            mDialogFromSelectedSet = savedState.getBoolean(SAVED_ACTION_FROM_SELECTED, false);
            mViewMode.handleRestore(savedState);
        } else if (intent != null) {
            handleIntent(intent);
        }
        // Create the accounts loader; this loads the account switch spinner.
        mActivity.getLoaderManager().initLoader(LOADER_ACCOUNT_CURSOR, Bundle.EMPTY,
                mAccountCallbacks);
        return true;
    }

    /**
     * @param activity the activity that has been inflated
     * @return the Actionable Toast Bar defined within the activity
     */
    protected ActionableToastBar findActionableToastBar(MailActivity activity) {
        return (ActionableToastBar) activity.findViewById(R.id.toast_bar);
    }

    @Override
    public void onPostCreate(Bundle savedState) {
        if (!isDrawerEnabled()) {
            return;
        }
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

        mHideMenuItems = isDrawerEnabled() && mDrawerContainer.isDrawerOpen(mDrawerPullout);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (isDrawerEnabled()) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    /**
     * This controller listens for clicks on items in the floating action bar.
     *
     * @param view the item that was clicked in the floating action bar
     */
    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.compose_button) {
            ComposeActivity.compose(mActivity.getActivityContext(), getAccount());
        } else if (viewId == android.R.id.home) {
            // TODO: b/16627877
            onUpPressed();
        }
    }

    /**
     * If drawer is open/visible (even partially), close it.
     */
    protected void closeDrawerIfOpen() {
        if (!isDrawerEnabled()) {
            return;
        }
        if(mDrawerContainer.isDrawerOpen(mDrawerPullout)) {
            mDrawerContainer.closeDrawers();
        }
    }

    @Override
    public void onStart() {
        mSafeToModifyFragments = true;

        NotificationActionUtils.registerUndoNotificationObserver(mUndoNotificationObserver);

        if (mViewMode.getMode() != ViewMode.UNKNOWN) {
            Analytics.getInstance().sendView("MainActivity" + mViewMode.toString());
        }
        IntentFilter filter = new IntentFilter(ComposeActivity.DRAFT_SAVED_ACTION);
        filter.setPriority(DRAFT_SAVE_PRIORITY);
        mActivity.registerReceiver(mDraftReceiver, filter); //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD
    }

    @Override
    public void onRestart() {
        final DialogFragment fragment = (DialogFragment)
                mFragmentManager.findFragmentByTag(SYNC_ERROR_DIALOG_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
        // When the user places the app in the background by pressing "home",
        // dismiss the toast bar. However, since there is no way to determine if
        // home was pressed, just dismiss any existing toast bar when restarting
        // the app.
        if (mToastBar != null) {
            mToastBar.hide(false, false /* actionClicked */);
        }
    }

    @Override
    public Dialog onCreateDialog(int id, Bundle bundle) {
        return null;
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        if (mViewMode.isAdMode()) {
            return false;
        }
        final MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(mActionBarController.getOptionsMenuId(), menu);
        mActionBarController.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
      //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 11/24/2014 FR848855
        if(keyCode==event.KEYCODE_BACK){
            if(mCabActionMenu!=null){
                if(mCabActionMenu.isActivated()){
                    if(mCabActionMenu.isActionModeNull()){
                        return false;
                    }else{
                        mCabActionMenu.onSetEmpty();
                    }
                    return true;
                }else{
                    return false;
                }
            }
        }
      //[FEATURE]-ADD-END by TSNJ.wei huang

        // TS: gangjin.weng 2015-4-5 EMAIL BUGFIX-968391 ADD_S
        if (keyCode == KeyEvent.KEYCODE_DEL && mCabActionMenu == null &&
                event != null && event.getScanCode() == 0 && event.getMetaState() == 0 && event.getFlags() == (KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE)) {
            return true;
        }
        // TS: gangjin.weng 2015-4-5 EMAIL BUGFIX-968391 ADD_E
        return false;
    }

    public abstract boolean doesActionChangeConversationListVisibility(int action);

    /**
     * Helper function that determines if we should associate an undo callback with
     * the current menu action item
     * @param actionId the id of the action
     * @return the appropriate callback handler, or null if not applicable
     */
    private UndoCallback getUndoCallbackForDestructiveActionsWithAutoAdvance(
            int actionId, final Conversation conv) {
        // We associated the undoCallback if the user is going to perform an action on the current
        // conversation, causing the current conversation to be removed from view and replacing it
        // with another (via Auto Advance). The undoCallback will bring the removed conversation
        // back into the view if the action is undone.
        final Collection<Conversation> convCol = Conversation.listOf(conv);
        final boolean isApplicableForReshow = mAccount != null &&
                mAccount.settings != null &&
                mTracker != null &&
                // ensure that we will show another conversation due to Auto Advance
                mTracker.getNextConversation(
                        mAccount.settings.getAutoAdvanceSetting(), convCol) != null &&
                // ensure that we are performing the action from conversation view
                isCurrentConversationInView(convCol) &&
                // check for the appropriate destructive actions
                doesActionRemoveCurrentConversationFromView(actionId);
        return (isApplicableForReshow) ?
            new UndoCallback() {
                @Override
                public void performUndoCallback() {
                    showConversation(conv);
                }
            } : null;
    }

    /**
     * Check if the provided action will remove the active conversation from view
     * @param actionId the applied action
     * @return true if it will remove the conversation from view, false otherwise
     */
    private boolean doesActionRemoveCurrentConversationFromView(int actionId) {
        return actionId == R.id.archive ||
                actionId == R.id.delete ||
                actionId == R.id.discard_outbox ||
                actionId == R.id.remove_folder ||
                actionId == R.id.report_spam ||
                actionId == R.id.report_phishing ||
                actionId == R.id.move_to;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
         * The action bar home/up action should open or close the drawer.
         * mDrawerToggle will take care of this.
         */
        if (isDrawerEnabled() && mDrawerToggle.onOptionsItemSelected(item)) {
            Analytics.getInstance().sendEvent(Analytics.EVENT_CATEGORY_MENU_ITEM, "drawer_toggle",
                    null, 0);
            return true;
        }

        Analytics.getInstance().sendMenuItemEvent(Analytics.EVENT_CATEGORY_MENU_ITEM,
                item.getItemId(), "action_bar/" + mViewMode.getModeString(), 0);

        final int id = item.getItemId();
        LogUtils.d(LOG_TAG, "AbstractController.onOptionsItemSelected(%d) called.", id);
        boolean handled = true;
        /** This is NOT a batch action. */
        final boolean isBatch = false;
        final Collection<Conversation> target = Conversation.listOf(mCurrentConversation);
        final Settings settings = (mAccount == null) ? null : mAccount.settings;
        // The user is choosing a new action; commit whatever they had been
        // doing before. Don't animate if we are launching a new screen.
        commitDestructiveActions(!doesActionChangeConversationListVisibility(id));
        final UndoCallback undoCallback = getUndoCallbackForDestructiveActionsWithAutoAdvance(
                id, mCurrentConversation);
        //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_S
        mUndoAction = id;
        mUndoConversation = mCurrentConversation;
        //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_E

        if (id == R.id.archive) {
            final boolean showDialog = (settings != null && settings.confirmArchive);
            confirmAndDelete(id, target, showDialog, R.plurals.confirm_archive_conversation, undoCallback);
        } else if (id == R.id.remove_folder) {
            delete(R.id.remove_folder, target,
                    getDeferredRemoveFolder(target, mFolder, true, isBatch, true, undoCallback),
                    isBatch);
        } else if (id == R.id.delete) {
            final boolean showDialog = (settings != null && settings.confirmDelete);
            confirmAndDelete(id, target, showDialog, R.plurals.confirm_delete_conversation, undoCallback);
        } else if (id == R.id.discard_drafts) {
            // drafts are lost forever, so always confirm
            confirmAndDelete(id, target, true /* showDialog */,
                    R.plurals.confirm_discard_drafts_conversation, undoCallback);
        } else if (id == R.id.discard_outbox) {
            // discard in outbox means we discard the failed message and save them in drafts
            delete(id, target, getDeferredAction(id, target, isBatch, undoCallback), isBatch);
        } else if (id == R.id.mark_important) {
            updateConversation(Conversation.listOf(mCurrentConversation),
                    ConversationColumns.PRIORITY, UIProvider.ConversationPriority.HIGH);
        } else if (id == R.id.mark_not_important) {
            if (mFolder != null && mFolder.isImportantOnly()) {
                delete(R.id.mark_not_important, target,
                        getDeferredAction(R.id.mark_not_important, target, isBatch, undoCallback),
                        isBatch);
            } else {
                updateConversation(Conversation.listOf(mCurrentConversation),
                        ConversationColumns.PRIORITY, UIProvider.ConversationPriority.LOW);
            }
        } else if (id == R.id.mute) {
            delete(R.id.mute, target, getDeferredAction(R.id.mute, target, isBatch, undoCallback),
                    isBatch);
        } else if (id == R.id.report_spam) {
            delete(R.id.report_spam, target,
                    getDeferredAction(R.id.report_spam, target, isBatch, undoCallback), isBatch);
        } else if (id == R.id.mark_not_spam) {
            // Currently, since spam messages are only shown in list with
            // other spam messages,
            // marking a message not as spam is a destructive action
            delete(R.id.mark_not_spam, target,
                    getDeferredAction(R.id.mark_not_spam, target, isBatch, undoCallback), isBatch);
        } else if (id == R.id.report_phishing) {
            delete(R.id.report_phishing, target,
                    getDeferredAction(R.id.report_phishing, target, isBatch, undoCallback), isBatch);
        } else if (id == android.R.id.home) {
            onUpPressed();
        } else if (id == R.id.compose) {
            ComposeActivity.compose(mActivity.getActivityContext(), mAccount);
        } else if (id == R.id.local_search) {//[FEATURE]-Add-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
            ActionBarController.SERVICE_SEARCH_MODE = false;
            mActivity.invalidateOptionsMenu();
        } else if (id == R.id.service_search) {
            ActionBarController.SERVICE_SEARCH_MODE = true;
            executeSearch(mActionBarController.getSearchWidgetText());//[FEATURE]-Add-END by TSCD.zhonghua.tuo
        }else if (id == R.id.refresh) {
            requestFolderRefresh();
        } else if (id == R.id.settings) {
            Utils.showSettings(mActivity.getActivityContext(), mAccount);
        } else if (id == R.id.help_info_menu_item) {
            mActivity.showHelp(mAccount, mViewMode.getMode());
        } else if (id == R.id.move_to || id == R.id.change_folders) {
          //TS: junwei-xu 2015-4-2 EMAIL BUGFIX_957916 ADD_S
          //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,01/12/2014,PR 854923
            //if(mFolder != null && (mFolder.supportsCapability(FolderCapabilities.ALLOWS_REMOVE_CONVERSATION) || mFolder.isDraft())){//TS: junwei-xu 2015-1-22 EMAIL BUGFIX_881437 MOD
            final FolderSelectionDialog dialog = FolderSelectionDialog.getInstance(mAccount,
                    Conversation.listOf(mCurrentConversation), isBatch, mFolder,
                    id == R.id.move_to);
            if(!(mAccount.getAccountId().equalsIgnoreCase("Account Id"))){
                if (dialog != null) {
                    //TS: xinlei.sheng 2016-3-3 EMAIL BUGFIX_1723314 MOD_S
                    try {
                        dialog.show(mActivity.getFragmentManager(), null);
                    } catch (IllegalStateException e) {
                        LogUtils.e(LOG_TAG, "onOptionsItemSelected: IllegalStateException when show FolderSelectionDialog");
                    }
                    //TS: xinlei.sheng 2016-3-3 EMAIL BUGFIX_1723314 MOD_E
                }
            }
           // }
          //TS: junwei-xu 2015-4-2 EMAIL BUGFIX_957916 ADD_E
            //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
        } else if (id == R.id.move_to_inbox) {
            new AsyncTask<Void, Void, Folder>() {
                @Override
                protected Folder doInBackground(final Void... params) {
                    // Get the "move to" inbox
                    return Utils.getFolder(mContext, mAccount.settings.moveToInbox,
                            true /* allowHidden */);
                }

                @Override
                protected void onPostExecute(final Folder moveToInbox) {
                    final List<FolderOperation> ops = Lists.newArrayListWithCapacity(1);
                    // Add inbox
                    ops.add(new FolderOperation(moveToInbox, true));
                    assignFolder(ops, Conversation.listOf(mCurrentConversation), true,
                            true /* showUndo */, false /* isMoveTo */);
                }
            }.execute((Void[]) null);
        } else if (id == R.id.empty_trash) {
            showEmptyDialog();
        } else if (id == R.id.empty_spam) {
            showEmptyDialog();
        } else if (id == R.id.sort_mail){ //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
            showSortDialog();
        }  //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E
        else {
            handled = false;
        }
        return handled;
    }

    /**
     * Opens an {@link EmptyFolderDialogFragment} for the current folder.
     */
    private void showEmptyDialog() {
        if (mFolder != null) {
            final EmptyFolderDialogFragment fragment =
                    EmptyFolderDialogFragment.newInstance(mFolder.totalCount, mFolder.type);
            fragment.setListener(this);
            fragment.show(mActivity.getFragmentManager(), EmptyFolderDialogFragment.FRAGMENT_TAG);
        }
    }

    private void showSortDialog(){
        DialogFragment fragment = SortChooseDialog.newInstance(SortHelper.getCurrentSort());
        fragment.show(mActivity.getFragmentManager(),SortChooseDialog.TAG);
    }

    @Override
    public void onFolderEmptied() {
        emptyFolder();
    }

    /**
     * Performs the work of emptying the currently visible folder.
     */
    private void emptyFolder() {
        if (mConversationListCursor != null) {
            mConversationListCursor.emptyFolder();
        }
    }

    private void attachEmptyFolderDialogFragmentListener() {
        final EmptyFolderDialogFragment fragment =
                (EmptyFolderDialogFragment) mActivity.getFragmentManager()
                        .findFragmentByTag(EmptyFolderDialogFragment.FRAGMENT_TAG);

        if (fragment != null) {
            fragment.setListener(this);
        }
    }

    /**
     * Toggles the drawer pullout. If it was open (Fully extended), the
     * drawer will be closed. Otherwise, the drawer will be opened. This should
     * only be called when used with a toggle item. Other cases should be handled
     * explicitly with just closeDrawers() or openDrawer(View drawerView);
     */
    protected void toggleDrawerState() {
        if (!isDrawerEnabled()) {
            return;
        }
        if(mDrawerContainer.isDrawerOpen(mDrawerPullout)) {
            mDrawerContainer.closeDrawers();
        } else {
            mDrawerContainer.openDrawer(mDrawerPullout);
        }
    }

    @Override
    public final boolean onUpPressed() {
        return handleUpPress();
    }

    @Override
    public final boolean onBackPressed() {
        if (isDrawerEnabled() && mDrawerContainer.isDrawerVisible(mDrawerPullout)) {
            LogUtils.i(LOG_TAG," --- mDrawerContainer.isDrawerVisible = "+mDrawerContainer.isDrawerVisible(mDrawerPullout)); // TS: kaifeng.lu 2015-11-10 EMAIL LOG-1101083 ADD
            mDrawerContainer.closeDrawers();
            return true;
        }

        return handleBackPress();
    }

    protected abstract boolean handleBackPress();

    protected abstract boolean handleUpPress();

    @Override
    public void updateConversation(Collection<Conversation> target, ContentValues values) {
        //TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 ADD_S
        if (mConversationListCursor == null) {
                LogUtils.d(LOG_TAG, "updateConversation: mConversationListCursor = null");
                return;
        }
        //TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 ADD_E
        mConversationListCursor.updateValues(target, values);
        refreshConversationList();
    }

    @Override
    public void updateConversation(Collection <Conversation> target, String columnName,
            boolean value) {
        //TS: xiaolin.li 2014-12-20 EMAIL BUGFIX-879393 ADD_S
        if (mConversationListCursor == null) {
                LogUtils.d(LOG_TAG, "updateConversation: mConversationListCursor = null");
                return;
        }
        //TS: xiaolin.li 2014-12-20 EMAIL BUGFIX-879393 ADD_E
        mConversationListCursor.updateBoolean(target, columnName, value);
        refreshConversationList();
    }

    @Override
    public void updateConversation(Collection <Conversation> target, String columnName,
            int value) {
        //TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 ADD_S
        if (mConversationListCursor == null) {
                LogUtils.d(LOG_TAG, "updateConversation: mConversationListCursor = null");
                return;
        }
        //TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 ADD_E
        mConversationListCursor.updateInt(target, columnName, value);
        refreshConversationList();
    }

    @Override
    public void updateConversation(Collection <Conversation> target, String columnName,
            String value) {
        //TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 ADD_S
        if (mConversationListCursor == null) {
                LogUtils.d(LOG_TAG, "updateConversation: mConversationListCursor = null");
                return;
        }
        //TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 ADD_E
        mConversationListCursor.updateString(target, columnName, value);
        refreshConversationList();
    }

    @Override
    public void markConversationMessagesUnread(final Conversation conv,
            final Set<Uri> unreadMessageUris, final byte[] originalConversationInfo) {
        LogUtils.i(LOG_TAG, "markConversationMessagesUnread begin convId=" + conv.id +" read="+conv.read); // TS: zheng.zou 2015-1-18 EMAIL BUGFIX_1130225 ADD
        // The only caller of this method is the conversation view, from where marking unread should
        // *always* take you back to list mode.
        showConversation(null);

        // locally mark conversation unread (the provider is supposed to propagate message unread
        // to conversation unread)
        conv.read = false;
        if (mConversationListCursor == null) {
            LogUtils.d(LOG_TAG, "markConversationMessagesUnread(id=%d), deferring", conv.id);

            mConversationListLoadFinishedCallbacks.add(new LoadFinishedCallback() {
                @Override
                public void onLoadFinished() {
                    doMarkConversationMessagesUnread(conv, unreadMessageUris,
                            originalConversationInfo);
                }
            });
        } else {
            LogUtils.d(LOG_TAG, "markConversationMessagesUnread(id=%d), performing", conv.id);
            doMarkConversationMessagesUnread(conv, unreadMessageUris, originalConversationInfo);
        }
    }

    private void doMarkConversationMessagesUnread(Conversation conv, Set<Uri> unreadMessageUris,
            byte[] originalConversationInfo) {
        // Only do a granular 'mark unread' if a subset of messages are unread
        final int unreadCount = (unreadMessageUris == null) ? 0 : unreadMessageUris.size();
        final int numMessages = conv.getNumMessages();
        final boolean subsetIsUnread = (numMessages > 1 && unreadCount > 0
                && unreadCount < numMessages);

        LogUtils.d(LOG_TAG, "markConversationMessagesUnread(conv=%s)"
                + ", numMessages=%d, unreadCount=%d, subsetIsUnread=%b",
                conv, numMessages, unreadCount, subsetIsUnread);
        if (!subsetIsUnread) {
            // Conversations are neither marked read, nor viewed, and we don't want to show
            // the next conversation.
            LogUtils.d(LOG_TAG, ". . doing full mark unread");
            markConversationsRead(Collections.singletonList(conv), false, false, false);
        } else {
            if (LogUtils.isLoggable(LOG_TAG, LogUtils.DEBUG)) {
                final ConversationInfo info = ConversationInfo.fromBlob(originalConversationInfo);
                LogUtils.d(LOG_TAG, ". . doing subset mark unread, originalConversationInfo = %s",
                        info);
            }
            mConversationListCursor.setConversationColumn(conv.uri, ConversationColumns.READ, 0);

            // Locally update conversation's conversationInfo to revert to original version
            if (originalConversationInfo != null) {
                mConversationListCursor.setConversationColumn(conv.uri,
                        ConversationColumns.CONVERSATION_INFO, originalConversationInfo);
            }

            // applyBatch with each CPO as an UPDATE op on each affected message uri
            final ArrayList<ContentProviderOperation> ops = Lists.newArrayList();
            String authority = null;
            for (Uri messageUri : unreadMessageUris) {
                if (authority == null) {
                    authority = messageUri.getAuthority();
                }
                ops.add(ContentProviderOperation.newUpdate(messageUri)
                        .withValue(UIProvider.MessageColumns.READ, 0)
                        .build());
                LogUtils.d(LOG_TAG, ". . Adding op: read=0, uri=%s", messageUri);
            }
            LogUtils.d(LOG_TAG, ". . operations = %s", ops);
            new ContentProviderTask() {
                @Override
                protected void onPostExecute(Result result) {
                    if (result.exception != null) {
                        LogUtils.e(LOG_TAG, result.exception, "ContentProviderTask() ERROR.");
                    } else {
                        LogUtils.d(LOG_TAG, "ContentProviderTask(): success %s",
                                Arrays.toString(result.results));
                    }
                }
            }.run(mResolver, authority, ops);
        }
    }

    @Override
    public void markConversationsRead(final Collection<Conversation> targets, final boolean read,
            final boolean viewed) {
        //TS: Gantao 2016-01-14 EMAIL BUGFIX-1424288 ADD_S
        try {
            LogUtils.d(LOG_TAG, "markConversationsRead(targets=%s)", targets.toArray());
        } catch (MissingFormatArgumentException e) {
            LogUtils.e(LOG_TAG, "Miss format argument while output the log of markConversationsRead");
        }
        //TS: Gantao 2016-01-14 EMAIL BUGFIX-1424288 ADD_E

        if (mConversationListCursor == null) {
            if (LogUtils.isLoggable(LOG_TAG, LogUtils.DEBUG)) {
                LogUtils.d(LOG_TAG, "markConversationsRead(targets=%s), deferring",
                        targets.toArray());
            }
            mConversationListLoadFinishedCallbacks.add(new LoadFinishedCallback() {
                @Override
                public void onLoadFinished() {
                    markConversationsRead(targets, read, viewed, true);
                }
            });
        } else {
            // We want to show the next conversation if we are marking unread.
            markConversationsRead(targets, read, viewed, true);
        }
    }

    private void markConversationsRead(final Collection<Conversation> targets, final boolean read,
            final boolean markViewed, final boolean showNext) {
        LogUtils.d(LOG_TAG, "performing markConversationsRead");
        // Auto-advance if requested and the current conversation is being marked unread
        if (showNext && !read) {
            final Runnable operation = new Runnable() {
                @Override
                public void run() {
                    markConversationsRead(targets, read, markViewed, showNext);
                }
            };

            if (!showNextConversation(targets, operation)) {
                // This method will be called again if the user selects an autoadvance option
                return;
            }
        }

        final int size = targets.size();
        final List<ConversationOperation> opList = new ArrayList<ConversationOperation>(size);
        for (final Conversation target : targets) {
            final ContentValues value = new ContentValues(4);
            value.put(ConversationColumns.READ, read);

            // We never want to mark unseen here, but we do want to mark it seen
            if (read || markViewed) {
                value.put(ConversationColumns.SEEN, Boolean.TRUE);
            }

            // The mark read/unread/viewed operations do not show an undo bar
            value.put(ConversationOperations.Parameters.SUPPRESS_UNDO, true);
            if (markViewed) {
                value.put(ConversationColumns.VIEWED, true);
            }
            final ConversationInfo info = target.conversationInfo;
            final boolean changed = info.markRead(read);
            if (changed) {
                value.put(ConversationColumns.CONVERSATION_INFO, info.toBlob());
            }
            opList.add(mConversationListCursor.getOperationForConversation(
                    target, ConversationOperation.UPDATE, value));
            // Update the local conversation objects so they immediately change state.
            target.read = read;
            if (markViewed) {
                target.markViewed();
            }
        }
        mConversationListCursor.updateBulkValues(opList);
    }

    /**
     * Auto-advance to a different conversation if the currently visible conversation in
     * conversation mode is affected (deleted, marked unread, etc.).
     *
     * <p>Does nothing if outside of conversation mode.</p>
     *
     * @param target the set of conversations being deleted/marked unread
     */
    @Override
    public void showNextConversation(final Collection<Conversation> target) {
        showNextConversation(target, null);
    }

    /**
     * Helper function to determine if the provided set of conversations is in view
     * @param target set of conversations that we are interested in
     * @return true if they are in view, false otherwise
     */
    private boolean isCurrentConversationInView(final Collection<Conversation> target) {
        final int viewMode = mViewMode.getMode();
        return (viewMode == ViewMode.CONVERSATION
                || viewMode == ViewMode.SEARCH_RESULTS_CONVERSATION)
                && Conversation.contains(target, mCurrentConversation);
    }

    /**
     * Auto-advance to a different conversation if the currently visible conversation in
     * conversation mode is affected (deleted, marked unread, etc.).
     *
     * <p>Does nothing if outside of conversation mode.</p>
     * <p>
     * Clients may pass an operation to execute on the target that this method will run after
     * auto-advance is complete. The operation, if provided, may run immediately, or it may run
     * later, or not at all. Reasons it may run later include:
     * <ul>
     * <li>the auto-advance setting is uninitialized and we need to wait for the user to set it</li>
     * <li>auto-advance in this configuration requires a mode change, and we need to wait for the
     * mode change transition to finish</li>
     * </ul>
     * <p>If the current conversation is not in the target collection, this method will do nothing,
     * and will not execute the operation.
     *
     * @param target the set of conversations being deleted/marked unread
     * @param operation (optional) the operation to execute after advancing
     * @return <code>false</code> if this method handled or will execute the operation,
     * <code>true</code> otherwise.
     */
    private boolean showNextConversation(final Collection<Conversation> target,
            final Runnable operation) {
        if (isCurrentConversationInView(target)) {
            final int autoAdvanceSetting = mAccount.settings.getAutoAdvanceSetting();

            // If we don't have one set, but we're here, just take the default
            final int autoAdvance = (autoAdvanceSetting == AutoAdvance.UNSET) ?
                    AutoAdvance.DEFAULT : autoAdvanceSetting;

            final Conversation next = mTracker.getNextConversation(autoAdvance, target);
            LogUtils.d(LOG_TAG, "showNextConversation: showing %s next.", next);
            // Set mAutoAdvanceOp *before* showConversation() to ensure that it runs when the
            // transition doesn't run (i.e. it "completes" immediately).
            mAutoAdvanceOp = operation;
            showConversation(next);
            return (mAutoAdvanceOp == null);
        }

        return true;
    }

    @Override
    public void starMessage(ConversationMessage msg, boolean starred) {
        if (msg.starred == starred) {
            return;
        }

        msg.starred = starred;

        // locally propagate the change to the owning conversation
        // (figure the provider will properly propagate the change when it commits it)
        //
        // when unstarring, only propagate the change if this was the only message starred
        final boolean conversationStarred = starred || msg.isConversationStarred();
        final Conversation conv = msg.getConversation();
        if (conversationStarred != conv.starred) {
            conv.starred = conversationStarred;
            mConversationListCursor.setConversationColumn(conv.uri,
                    ConversationColumns.STARRED, conversationStarred);
        }

        final ContentValues values = new ContentValues(1);
        values.put(UIProvider.MessageColumns.STARRED, starred ? 1 : 0);

        new ContentProviderTask.UpdateTask() {
            @Override
            protected void onPostExecute(Result result) {
                // TODO: handle errors?
            }
        }.run(mResolver, msg.uri, values, null /* selection*/, null /* selectionArgs */);
    }

    @Override
    public void requestFolderRefresh() {
        if (mFolder == null) {
            return;
        }
        final ConversationListFragment convList = getConversationListFragment();
        if (convList == null) {
            // This could happen if this account is in initial sync (user
            // is seeing the "your mail will appear shortly" message)
            return;
        }
        convList.showSyncStatusBar();

        if (mAsyncRefreshTask != null) {
            mAsyncRefreshTask.cancel(true);
        }
        mAsyncRefreshTask = new AsyncRefreshTask(mContext, mFolder.refreshUri);
        mAsyncRefreshTask.execute();
    }

    /**
     * Confirm (based on user's settings) and delete a conversation from the conversation list and
     * from the database.
     * @param actionId the ID of the menu item that caused the delete: R.id.delete, R.id.archive...
     * @param target the conversations to act upon
     * @param showDialog true if a confirmation dialog is to be shown, false otherwise.
     * @param confirmResource the resource ID of the string that is shown in the confirmation dialog
     */
    private void confirmAndDelete(int actionId, final Collection<Conversation> target,
            boolean showDialog, int confirmResource, UndoCallback undoCallback) {
        final boolean isBatch = false;
        if (showDialog) {
            makeDialogListener(actionId, isBatch, undoCallback);
            final CharSequence message = Utils.formatPlural(mContext, confirmResource,
                    target.size());
            final ConfirmDialogFragment c = ConfirmDialogFragment.newInstance(message);
            c.displayDialog(mActivity.getFragmentManager());
        } else {
            delete(0, target, getDeferredAction(actionId, target, isBatch, undoCallback), isBatch);
        }
    }

    @Override
    public void delete(final int actionId, final Collection<Conversation> target,
                       final DestructiveAction action, final boolean isBatch) {
        // Order of events is critical! The Conversation View Fragment must be
        // notified of the next conversation with showConversation(next) *before* the
        // conversation list
        // fragment has a chance to delete the conversation, animating it away.

        // Update the conversation fragment if the current conversation is
        // deleted.
        final Runnable operation = new Runnable() {
            @Override
            public void run() {
                delete(actionId, target, action, isBatch);
            }
        };

        if (!showNextConversation(target, operation)) {
            // This method will be called again if the user selects an autoadvance option
            return;
        }
        // If the conversation is in the selected set, remove it from the set.
        // Batch selections are cleared in the end of the action, so not done for batch actions.
        if (!isBatch) {
            for (final Conversation conv : target) {
                if (mSelectedSet.contains(conv)) {
                    mSelectedSet.toggle(conv);
                }
            }
        }
        // The conversation list deletes and performs the action if it exists.
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null) {
            LogUtils.i(LOG_TAG, "AAC.requestDelete: ListFragment is handling delete.");
            convListFragment.requestDelete(actionId, target, action);
            return;
        }
        // No visible UI element handled it on our behalf. Perform the action
        // ourself.
        LogUtils.i(LOG_TAG, "ACC.requestDelete: performing remove action ourselves");
        action.performAction();
    }

    /**
     * Requests that the action be performed and the UI state is updated to reflect the new change.
     * @param action the action to be performed, specified as a menu id: R.id.archive, ...
     */
    private void requestUpdate(final DestructiveAction action) {
        action.performAction();
        refreshConversationList();
    }

    @Override
    public void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        // TODO(viki): Auto-generated method stub
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mActionBarController.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        mHaveAccountList = false;
        enableNotifications();
    }

    @Override
    public void onResume() {
        // Register the receiver that will prevent the status receiver from
        // displaying its notification icon as long as we're running.
        // The SupressNotificationReceiver will block the broadcast if we're looking at the folder
        // that the notification was received for.
        disableNotifications();

        mSafeToModifyFragments = true;

        attachEmptyFolderDialogFragmentListener();

        // Invalidating the options menu so that when we make changes in settings,
        // the changes will always be updated in the action bar/options menu/
        mActivity.invalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /// TCT: save global search tag, for it may be lost after rotated devices
        outState.putBoolean(SAVED_GLOBAL_SEARCH, mGlobalSearch);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        // save the check status for star toggle
        outState.putBoolean(BUNDLE_CHECK_STATUS_KEY, mCheckStatus);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
        mViewMode.handleSaveInstanceState(outState);
        if (mAccount != null) {
            outState.putParcelable(SAVED_ACCOUNT, mAccount);
        }
        if (mFolder != null) {
            outState.putParcelable(SAVED_FOLDER, mFolder);
        }
        // TCT: If this is a search activity, let's store the search context
        if (mConvListContext !=null) {
//            outState.putString(SAVED_QUERY, mConvListContext.searchQuery);
            outState.putParcelable(SAVED_LOCAL_SEARCH, mConvListContext.toBundle());
        }
        if (mCurrentConversation != null && mViewMode.isConversationMode()) {
            outState.putParcelable(SAVED_CONVERSATION, mCurrentConversation);
        }
        if (!mSelectedSet.isEmpty()) {
            outState.putParcelable(SAVED_SELECTED_SET, mSelectedSet);
        }
        if (mToastBar.getVisibility() == View.VISIBLE) {
            outState.putParcelable(SAVED_TOAST_BAR_OP, mToastBar.getOperation());
            //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_S
            if (mUndoConversation != null) {
                outState.putParcelable(SAVED_UNDO_CONVERSATION, mUndoConversation);
            }
            if (mUndoAction != -1) {
                outState.putInt(SAVED_UNDO_ACTION, mUndoAction);
            }
            //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_E
            // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD_S
            if (mSavedDraftMsgId !=-1){
                outState.putLong(SAVED_DRAFT_MSG_ID, mSavedDraftMsgId);
            }
            // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD_E
        }
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null) {
            convListFragment.getAnimatedAdapter().onSaveInstanceState(outState);
        }
        // If there is a dialog being shown, save the state so we can create a listener for it.
        if (mDialogAction != -1) {
            outState.putInt(SAVED_ACTION, mDialogAction);
            outState.putBoolean(SAVED_ACTION_FROM_SELECTED, mDialogFromSelectedSet);
        }
        if (mDetachedConvUri != null) {
            outState.putParcelable(SAVED_DETACHED_CONV_URI, mDetachedConvUri);
        }

        outState.putParcelable(SAVED_HIERARCHICAL_FOLDER, mFolderListFolder);
        mSafeToModifyFragments = false;

        outState.putParcelable(SAVED_INBOX_KEY, mInbox);

        outState.putBundle(SAVED_CONVERSATION_LIST_SCROLL_POSITIONS,
                mConversationListScrollPositions);
    }

    /**
     * @see #mSafeToModifyFragments
     */
    protected boolean safeToModifyFragments() {
        return mSafeToModifyFragments;
    }


    /**
     * TCT: implement local search method @{
     */
    @Override
    public void enterLocalSearch(String searchfield) {
        if (mConvListContext == null || TextUtils.isEmpty(searchfield)) {
            LogUtils.logFeature(
                    LogTag.SEARCH_TAG,
                    "enterLocalSearch failed for search field or mConvListContext is null, searchfie ld [%s]",
                    searchfield);
            return;
        }
        LogUtils.logFeature(LogTag.SEARCH_TAG, " >>>> enterLocalSearch searchfield [%s] ", searchfield);
        mConvListContext.setLocalSearch(true);
        mConvListContext.setSearchField(searchfield);
        if (isDrawerEnabled()) {
            mDrawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void exitLocalSearch() {
        if (mConvListContext == null) {
            return;
        }
        LogUtils.logFeature(LogTag.SEARCH_TAG, " <<<< exitLocalSearch");
        mConvListContext.setLocalSearch(false);
        mConvListContext.setSearchQueryText(null);
        mConvListContext.setSearchField(null);
        // refresh conversation list.
        loadConversationListData(false);
        if (isDrawerEnabled()) {
            mDrawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    /**
     * Note: if {@link Query} is null, default refresh current mailbox.
     */
    @Override
    public void executeLocalSearch(String query) {
        if (mConvListContext == null || !mConvListContext.isLocalSearch()) {
            LogUtils.logFeature(
                    LogTag.SEARCH_TAG,
                    "enterLocalSearch failed for mConvListContext is null or current is not local search, mConvListContext: [%s]",
                    mConvListContext);
            return;
        }
        LogUtils.logFeature(LogTag.SEARCH_TAG, "executeLocalSearch query [%s]", query);
        mConvListContext.setSearchQueryText(query);
        // Clear global search tag, since it has worked.
        if (mGlobalSearch) {
            mGlobalSearch = false;
        }
        loadConversationListData(false);
    }
    /** @} */

    /**
     * TCT: This will launch a new activity with a new controller to do remote search.
     */
    @Override
    public void executeSearch(String query) {
        AnalyticsTimer.getInstance().trackStart(AnalyticsTimer.SEARCH_TO_LIST);
        /// TCT: add search field for remote search. @{
        String searchFiled = null;
        if (mConvListContext != null) {
            searchFiled = mConvListContext.getSearchField();
        }
        if (TextUtils.isEmpty(searchFiled)) {
            LogUtils.d(LOG_TAG, "set search field as ALL, if user not set search field");
            searchFiled = SearchParams.SEARCH_FIELD_ALL;
        }
        /// @}
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(ConversationListContext.EXTRA_SEARCH_QUERY, query);
        /// TCT: add search field for remote search. @{
        intent.putExtra(SearchParams.BUNDLE_QUERY_FIELD, searchFiled);
        /// @}
        intent.putExtra(Utils.EXTRA_ACCOUNT, mAccount);
        intent.setComponent(mActivity.getComponentName());
        /// TCT: It's better to stay at local search UI when back from remote search.
//        mActionBarController.collapseSearch();
        // Call startActivityForResult here so we can tell if we have navigated to a different folder
        // or account from search results.
        mActivity.startActivityForResult(intent, CHANGE_NAVIGATION_REQUEST_CODE);
    }

    //[FEATURE]-Add-BEGIN by TSCD.zhonghua.tuo,05/28/2014,FR 670064
//    @Override
//    public void executeLocalSearch(String query) {
//        if(getConversationListFragment() != null) {
//            getConversationListFragment().reFreshLocalSearch(query);
//        }
//    }
    //[FEATURE]-Add-END by TSCD.zhonghua.tuo

    /**
     * TCT: This will retry remote search right in this remote context(activity/controller).
     */
    private void retryRemoteSearch() {
        LogUtils.d(LOG_TAG, "AAC. retry remote search.");
        Intent intent = mActivity.getIntent();
        if (null != intent) {
            fetchSearchFolder(intent);
        } else {
            LogUtils.d(LOG_TAG, "AAC. retry remote search failed!");
        }
    }

    @Override
    public void onStop() {
        NotificationActionUtils.unregisterUndoNotificationObserver(mUndoNotificationObserver);
        mActivity.unregisterReceiver(mDraftReceiver);  //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD
    }

    @Override
    public void onDestroy() {
        // stop listening to the cursor on e.g. configuration changes
        if (mConversationListCursor != null) {
            mConversationListCursor.removeListener(this);
        }
        mDrawIdler.setListener(null);
        mDrawIdler.setRootView(null);
        // unregister the ViewPager's observer on the conversation cursor
        mPagerController.onDestroy();
        mActionBarController.onDestroy();
        mRecentFolderList.destroy();
        mDestroyed = true;
        mHandler.removeCallbacks(mLogServiceChecker);
        mLogServiceChecker = null;
    }

    /**
     * Set the Action Bar icon according to the mode. The Action Bar icon can contain a back button
     * or not. The individual controller is responsible for changing the icon based on the mode.
     */
    protected abstract void resetActionBarIcon();

    /**
     * {@inheritDoc} Subclasses must override this to listen to mode changes
     * from the ViewMode. Subclasses <b>must</b> call the parent's
     * onViewModeChanged since the parent will handle common state changes.
     */
    @Override
    public void onViewModeChanged(int newMode) {
        // The floating action compose button is only visible in the conversation/search lists
        final int composeVisible = ViewMode.isListMode(newMode) ? View.VISIBLE : View.GONE;
        mFloatingComposeButton.setVisibility(composeVisible);
        //TODO for debug, will be deleted later
        LogUtils.i(LOG_TAG, "update float compose button visibility as "+composeVisible);

        // When we step away from the conversation mode, we don't have a current conversation
        // anymore. Let's blank it out so clients calling getCurrentConversation are not misled.
        if (!ViewMode.isConversationMode(newMode)) {
            setCurrentConversation(null);
        }

        // If the viewmode is not set, preserve existing icon.
        if (newMode != ViewMode.UNKNOWN) {
            resetActionBarIcon();
        }

        if (isDrawerEnabled()) {
            /** If the folder doesn't exist, or its parent URI is empty,
             * this is not a child folder */
            final boolean isTopLevel = Folder.isRoot(mFolder);
            mDrawerToggle.setDrawerIndicatorEnabled(
                    getShouldShowDrawerIndicator(newMode, isTopLevel));
            mDrawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            closeDrawerIfOpen();
        }
    }

    /**
     * Returns true if the drawer icon is shown
     * @param viewMode the current view mode
     * @param isTopLevel true if the current folder is not a child
     * @return whether the drawer indicator is shown
     */
    private boolean getShouldShowDrawerIndicator(final int viewMode,
            final boolean isTopLevel) {
        // If search list/conv mode: disable indicator
        // Indicator is enabled either in conversation list or folder list mode.
        return isDrawerEnabled() && !ViewMode.isSearchMode(viewMode)
            && (viewMode == ViewMode.CONVERSATION_LIST  && isTopLevel);
    }

    public void disablePagerUpdates() {
        mPagerController.stopListening();
    }

    public boolean isDestroyed() {
        return mDestroyed;
    }

    @Override
    public void commitDestructiveActions(boolean animate) {
        ConversationListFragment fragment = getConversationListFragment();
        if (fragment != null) {
            fragment.commitDestructiveActions(animate);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        final ConversationListFragment convList = getConversationListFragment();
        // hasFocus already ensures that the window is in focus, so we don't need to call
        // AAC.isFragmentVisible(convList) here.
        if (hasFocus && convList != null && convList.isVisible()) {
            // The conversation list is visible.
            informCursorVisiblity(true);
        }
    }

    /**
     * Set the account, and carry out all the account-related changes that rely on this.
     * @param account new account to set to.
     */
    private void setAccount(Account account) {
        if (account == null) {
            LogUtils.w(LOG_TAG, new Error(),
                    "AAC ignoring null (presumably invalid) account restoration");
            return;
        }
        LogUtils.d(LOG_TAG, "AbstractActivityController.setAccount(): account = %s", account.uri);
        mAccount = account;
        // Only change AAC state here. Do *not* modify any other object's state. The object
        // should listen on account changes.
        restartOptionalLoader(LOADER_RECENT_FOLDERS, mFolderCallbacks, Bundle.EMPTY);
        mActivity.invalidateOptionsMenu();
        disableNotificationsOnAccountChange(mAccount);
        restartOptionalLoader(LOADER_ACCOUNT_UPDATE_CURSOR, mAccountCallbacks, Bundle.EMPTY);
        // The Mail instance can be null during test runs.
        final MailAppProvider instance = MailAppProvider.getInstance();
        if (instance != null) {
            instance.setLastViewedAccount(mAccount.uri.toString());
        }
        if (account.settings == null) {
            LogUtils.w(LOG_TAG, new Error(), "AAC ignoring account with null settings.");
            return;
        }
        mAccountObservers.notifyChanged();
        perhapsEnterWaitMode();
    }

    /**
     * Restore the state from the previous bundle. Subclasses should call this
     * method from the parent class, since it performs important UI
     * initialization.
     *
     * @param savedState previous state
     */
    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        mDetachedConvUri = savedState.getParcelable(SAVED_DETACHED_CONV_URI);
        if (savedState.containsKey(SAVED_CONVERSATION)) {
            // Open the conversation.
            final Conversation conversation = savedState.getParcelable(SAVED_CONVERSATION);
            if (conversation != null && conversation.position < 0) {
                // Set the position to 0 on this conversation, as we don't know where it is
                // in the list
                conversation.position = 0;
            }
            showConversation(conversation);
        }

        if (savedState.containsKey(SAVED_TOAST_BAR_OP)) {
            ToastBarOperation op = savedState.getParcelable(SAVED_TOAST_BAR_OP);
            if (op != null) {
                //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_S
                if (mConversationListCursor != null && mConversationListCursor.getUndoCallback() != null) {
                    mUndoConversation = savedState.getParcelable(SAVED_UNDO_CONVERSATION);
                    mUndoAction = savedState.getInt(SAVED_UNDO_ACTION);

                    final UndoCallback undoCallback = new UndoCallback() {
                        @Override
                        public void performUndoCallback() {
                            showConversation(mUndoConversation);
                        }
                    };
                    mConversationListCursor.updateCallback(undoCallback);
                }
                //TS: qing.liang 2015-03-10 EMAIL BUGFIX_-941849 ADD_E
                if (op.getType() == ToastBarOperation.UNDO) {
                    onUndoAvailable(op);
                } else if (op.getType() == ToastBarOperation.ERROR) {
                    onError(mFolder, true);
                } else if (op.getType() == ToastBarOperation.DISCARD) {     // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD_S
                    mSavedDraftMsgId = savedState.getLong(SAVED_DRAFT_MSG_ID);
                    showDiscardDraftToast(mSavedDraftMsgId);
                }else if (op.getType() == ToastBarOperation.DISCARDED){
                    showDiscardedToast();
                }
                // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD_E
            }
        }
        mFolderListFolder = savedState.getParcelable(SAVED_HIERARCHICAL_FOLDER);
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null) {
            convListFragment.getAnimatedAdapter().onRestoreInstanceState(savedState);
        }
        /*
         * Restore the state of selected conversations. This needs to be done after the correct mode
         * is set and the action bar is fully initialized. If not, several key pieces of state
         * information will be missing, and the split views may not be initialized correctly.
         */
        restoreSelectedConversations(savedState);
        // Order is important!!!
        // The dialog listener needs to happen *after* the selected set is restored.

        // If there has been an orientation change, and we need to recreate the listener for the
        // confirm dialog fragment (delete/archive/...), then do it here.
        if (mDialogAction != -1) {
            makeDialogListener(mDialogAction, mDialogFromSelectedSet,
                    getUndoCallbackForDestructiveActionsWithAutoAdvance(
                            mDialogAction, mCurrentConversation));
        }

        mInbox = savedState.getParcelable(SAVED_INBOX_KEY);

        mConversationListScrollPositions.clear();
        mConversationListScrollPositions.putAll(
                savedState.getBundle(SAVED_CONVERSATION_LIST_SCROLL_POSITIONS));
    }

    /**
     * Handle an intent to open the app. This method is called only when there is no saved state,
     * so we need to set state that wasn't set before. It is correct to change the viewmode here
     * since it has not been previously set.
     *
     * This method is called for a subset of the reasons mentioned in
     * {@link #onCreate(android.os.Bundle)}. Notably, this is called when launching the app from
     * notifications, widgets, and shortcuts.
     * @param intent intent passed to the activity.
     */
    private void handleIntent(Intent intent) {
        LogUtils.d(LOG_TAG, "IN AAC.handleIntent. action=%s", intent.getAction());
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            if (intent.hasExtra(Utils.EXTRA_ACCOUNT)) {
                setAccount(Account.newInstance(intent.getStringExtra(Utils.EXTRA_ACCOUNT)));
            }
            if (mAccount == null) {
                return;
            }
            final boolean isConversationMode = intent.hasExtra(Utils.EXTRA_CONVERSATION);

            if (intent.getBooleanExtra(Utils.EXTRA_FROM_NOTIFICATION, false)) {
                Analytics.getInstance().setCustomDimension(Analytics.CD_INDEX_ACCOUNT_TYPE,
                        AnalyticsUtils.getAccountTypeForAccount(mAccount.getEmailAddress()));
                Analytics.getInstance().sendEvent("notification_click",
                        isConversationMode ? "conversation" : "conversation_list", null, 0);
            }

            if (isConversationMode && mViewMode.getMode() == ViewMode.UNKNOWN) {
                mViewMode.enterConversationMode();
            } else {
                mViewMode.enterConversationListMode();
            }
            // Put the folder and conversation, and ask the loader to create this folder.
            final Bundle args = new Bundle();

            final Uri folderUri;
            if (intent.hasExtra(Utils.EXTRA_FOLDER_URI)) {
                folderUri = intent.getParcelableExtra(Utils.EXTRA_FOLDER_URI);
            } else if (intent.hasExtra(Utils.EXTRA_FOLDER)) {
                final Folder folder =
                        Folder.fromString(intent.getStringExtra(Utils.EXTRA_FOLDER));
                folderUri = folder.folderUri.fullUri;
              //TS: junwei-xu 2014-1-5 EMAIL BUGFIX_879468 ADD_S
            } else if(intent.getData()!=null){
                folderUri = intent.getData();
              //TS: junwei-xu 2014-1-5 EMAIL BUGFIX_879468 ADD_E
            }else {
                final Bundle extras = intent.getExtras();
                LogUtils.d(LOG_TAG, "Couldn't find a folder URI in the extras: %s",
                        extras == null ? "null" : extras.toString());
                folderUri = mAccount.settings.defaultInbox;
            }

            // Check if we should load all conversations instead of using
            // the default behavior which loads an initial subset.
            mIgnoreInitialConversationLimit =
                    intent.getBooleanExtra(Utils.EXTRA_IGNORE_INITIAL_CONVERSATION_LIMIT, false);

            args.putParcelable(Utils.EXTRA_FOLDER_URI, folderUri);
            args.putParcelable(Utils.EXTRA_CONVERSATION,
                    intent.getParcelableExtra(Utils.EXTRA_CONVERSATION));
            restartOptionalLoader(LOADER_FIRST_FOLDER, mFolderCallbacks, args);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (intent.hasExtra(Utils.EXTRA_ACCOUNT)) {
                mHaveSearchResults = false;
                //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_S
                mToastBar.hide(false,false);
                //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_E
                // Save this search query for future suggestions.
                final String query = intent.getStringExtra(SearchManager.QUERY);
                final String authority = mContext.getString(R.string.suggestions_authority);
                final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                        mContext, authority, SuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                setAccount((Account) intent.getParcelableExtra(Utils.EXTRA_ACCOUNT));
                fetchSearchFolder(intent);
                if (shouldEnterSearchConvMode()) {
                    mViewMode.enterSearchResultsConversationMode();
                } else {
                    mViewMode.enterSearchResultsListMode();
                }
                /** TCT: init the list context for remote search, except folder. @{ */
                // use a UNINITIALIZED folder temporarily. need update when finish search load.
                Folder folder = Folder.newUnsafeInstance();
                mConvListContext = ConversationListContext.forSearchQuery(mAccount, folder, query);
                mConvListContext.setSearchField(mActivity.getIntent().getStringExtra(
                        SearchParams.BUNDLE_QUERY_FIELD));
                /** @} */
            } else {
                /** TCT: The action is search but no extra account means it's a global search. @{ */
                mGlobalSearch = true;
                LogUtils.logFeature(
                        LogTag.SEARCH_TAG, "Handle ACTION_SEARCH , is mGlobalSearch [%s]", mGlobalSearch);
                // reload conbined inbox folder if needed.
                if (mAccount != null && (mFolder == null || !mFolder.isInitialized())) {
                    Bundle args = new Bundle();
                    LogUtils.logFeature(LogTag.SEARCH_TAG, " GlobalSearch but without Folder, reload inbox again.");
                    args.putParcelable(Utils.EXTRA_FOLDER_URI, mAccount.settings.defaultInbox);
                    restartOptionalLoader(LOADER_FIRST_FOLDER, mFolderCallbacks, args);
                }
                /** @} */
            }
        }
        if (mAccount != null) {
            restartOptionalLoader(LOADER_ACCOUNT_UPDATE_CURSOR, mAccountCallbacks, Bundle.EMPTY);
        }
    }

    /**
     * Returns true if we should enter conversation mode with search.
     */
    protected final boolean shouldEnterSearchConvMode() {
        return mHaveSearchResults && Utils.showTwoPaneSearchResults(mActivity.getActivityContext());
    }

    /**
     * Copy any selected conversations stored in the saved bundle into our selection set,
     * triggering {@link ConversationSetObserver} callbacks as our selection set changes.
     *
     */
    private void restoreSelectedConversations(Bundle savedState) {
        if (savedState == null) {
            mSelectedSet.clear();
            return;
        }
        final ConversationSelectionSet selectedSet = savedState.getParcelable(SAVED_SELECTED_SET);
        if (selectedSet == null || selectedSet.isEmpty()) {
            mSelectedSet.clear();
            return;
        }

        // putAll will take care of calling our registered onSetPopulated method
        mSelectedSet.putAll(selectedSet);
    }

    /**
     * Show the conversation provided in the arguments. It is safe to pass a null conversation
     * object, which is a signal to back out of conversation view mode.
     * Child classes must call super.showConversation() <b>before</b> their own implementations.
     * @param conversation the conversation to be shown, or null if we want to back out to list
     *                     mode.
     * onLoadFinished(Loader, Cursor) on any callback.
     */
    protected void showConversation(Conversation conversation) {
        showConversation(conversation, true /* markAsRead */);
    }

    protected void showConversation(Conversation conversation, boolean markAsRead) {
        if (conversation != null) {
            Utils.sConvLoadTimer.start();
        }

        MailLogService.log("AbstractActivityController", "showConversation(%s)", conversation);
        // Set the current conversation just in case it wasn't already set.
        setCurrentConversation(conversation);
    }

    /**
     * Children can override this method, but they must call super.showWaitForInitialization().
     * {@inheritDoc}
     */
    @Override
    public void showWaitForInitialization() {
        mViewMode.enterWaitingForInitializationMode();
        mWaitFragment = WaitFragment.newInstance(mAccount, true /* expectingMessages */);
    }

    private void updateWaitMode() {
        final FragmentManager manager = mActivity.getFragmentManager();
        final WaitFragment waitFragment =
                (WaitFragment)manager.findFragmentByTag(TAG_WAIT);
        if (waitFragment != null) {
            waitFragment.updateAccount(mAccount);
        }
    }

    /**
     * Remove the "Waiting for Initialization" fragment. Child classes are free to override this
     * method, though they must call the parent implementation <b>after</b> they do anything.
     */
    protected void hideWaitForInitialization() {
        mWaitFragment = null;
    }

    /**
     * Use the instance variable and the wait fragment's tag to get the wait fragment.  This is
     * far superior to using the value of mWaitFragment, which might be invalid or might refer
     * to a fragment after it has been destroyed.
     * @return a wait fragment that is already attached to the activity, if one exists
     */
    protected final WaitFragment getWaitFragment() {
        final FragmentManager manager = mActivity.getFragmentManager();
        final WaitFragment waitFrag = (WaitFragment) manager.findFragmentByTag(TAG_WAIT);
        if (waitFrag != null) {
            // The Fragment Manager knows better, so use its instance.
            mWaitFragment = waitFrag;
        }
        return mWaitFragment;
    }

    /**
     * Returns true if we are waiting for the account to sync, and cannot show any folders or
     * conversation for the current account yet.
     */
    private boolean inWaitMode() {
        final WaitFragment waitFragment = getWaitFragment();
        if (waitFragment != null) {
            final Account fragmentAccount = waitFragment.getAccount();
            return fragmentAccount != null && fragmentAccount.uri.equals(mAccount.uri) &&
                    mViewMode.getMode() == ViewMode.WAITING_FOR_ACCOUNT_INITIALIZATION;
        }
        return false;
    }

    /**
     * Children can override this method, but they must call super.showConversationList().
     * {@inheritDoc}
     */
    @Override
    public void showConversationList(ConversationListContext listContext) {
    }

    @Override
    public void onConversationSelected(Conversation conversation, boolean inLoaderCallbacks) {
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null && convListFragment.getAnimatedAdapter() != null) {
            convListFragment.getAnimatedAdapter().onConversationSelected();
        }
        // Only animate destructive actions if we are going to be showing the
        // conversation list when we show the next conversation.
        commitDestructiveActions(mIsTablet);
        //TS: yanhua.chen 2015-4-24 EMAIL BUGFIX_976622 MOD_S
        if(conversation != null){
            showConversation(conversation);
        }
        //TS: yanhua.chen 2015-4-24 EMAIL BUGFIX_976622 MOD_E
    }

    @Override
    public final void onCabModeEntered() {
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null && convListFragment.getAnimatedAdapter() != null) {
            convListFragment.getAnimatedAdapter().onCabModeEntered();
        }
    }

    @Override
    public final void onCabModeExited() {
        final ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null && convListFragment.getAnimatedAdapter() != null) {
            convListFragment.getAnimatedAdapter().onCabModeExited();
        }
    }

    @Override
    public Conversation getCurrentConversation() {
        return mCurrentConversation;
    }

    /**
     * Set the current conversation. This is the conversation on which all actions are performed.
     * Do not modify mCurrentConversation except through this method, which makes it easy to
     * perform common actions associated with changing the current conversation.
     * @param conversation new conversation to view. Passing null indicates that we are backing
     *                     out to conversation list mode.
     */
    @Override
    public void setCurrentConversation(Conversation conversation) {
        // The controller should come out of detached mode if a new conversation is viewed, or if
        // we are going back to conversation list mode.
        if (mDetachedConvUri != null && (conversation == null
                || !mDetachedConvUri.equals(conversation.uri))) {
            clearDetachedMode();
        }

        // Must happen *before* setting mCurrentConversation because this sets
        // conversation.position if a cursor is available.
        mTracker.initialize(conversation);
        mCurrentConversation = conversation;

        if (mCurrentConversation != null) {
            LogUtils.i(LOG_TAG, "in AAC.setCurrentConversation() conversation [ %s, %s ]",
                    mCurrentConversation.id, mCurrentConversation.starred);
            mActionBarController.setCurrentConversation(mCurrentConversation);
            mActivity.invalidateOptionsMenu();
        }
    }

    /**
     * {@link LoaderManager} currently has a bug in
     * {@link LoaderManager#restartLoader(int, Bundle, android.app.LoaderManager.LoaderCallbacks)}
     * where, if a previous onCreateLoader returned a null loader, this method will NPE. Work around
     * this bug by destroying any loaders that may have been created as null (essentially because
     * they are optional loads, and may not apply to a particular account).
     * <p>
     * A simple null check before restarting a loader will not work, because that would not
     * give the controller a chance to invalidate UI corresponding the prior loader result.
     *
     * @param id loader ID to safely restart
     * @param handler the LoaderCallback which will handle this loader ID.
     * @param args arguments, if any, to be passed to the loader. Use {@link Bundle#EMPTY} if no
     *             arguments need to be specified.
     */
    private void restartOptionalLoader(int id, LoaderManager.LoaderCallbacks handler, Bundle args) {
        final LoaderManager lm = mActivity.getLoaderManager();
        lm.destroyLoader(id);
        lm.restartLoader(id, args, handler);
    }

    @Override
    public void registerConversationListObserver(DataSetObserver observer) {
        mConversationListObservable.registerObserver(observer);
    }

    @Override
    public void unregisterConversationListObserver(DataSetObserver observer) {
        try {
            mConversationListObservable.unregisterObserver(observer);
        } catch (IllegalStateException e) {
            // Log instead of crash
            LogUtils.e(LOG_TAG, e, "unregisterConversationListObserver called for an observer that "
                    + "hasn't been registered");
        }
    }

    @Override
    public void registerFolderObserver(DataSetObserver observer) {
        mFolderObservable.registerObserver(observer);
    }

    @Override
    public void unregisterFolderObserver(DataSetObserver observer) {
        try {
            mFolderObservable.unregisterObserver(observer);
        } catch (IllegalStateException e) {
            // Log instead of crash
            LogUtils.e(LOG_TAG, e, "unregisterFolderObserver called for an observer that "
                    + "hasn't been registered");
        }
    }

    @Override
    public void registerConversationLoadedObserver(DataSetObserver observer) {
        mPagerController.registerConversationLoadedObserver(observer);
    }

    @Override
    public void unregisterConversationLoadedObserver(DataSetObserver observer) {
        try {
            mPagerController.unregisterConversationLoadedObserver(observer);
        } catch (IllegalStateException e) {
            // Log instead of crash
            LogUtils.e(LOG_TAG, e, "unregisterConversationLoadedObserver called for an observer "
                    + "that hasn't been registered");
        }
    }

    /**
     * Returns true if the number of accounts is different, or if the current account has
     * changed. This method is meant to filter frequent changes to the list of
     * accounts, and only return true if the new list is substantially different from the existing
     * list. Returning true is safe here, it leads to more work in creating the
     * same account list again.
     * @param accountCursor the cursor which points to all the accounts.
     * @return true if the number of accounts is changed or current account missing from the list.
     */
    private boolean accountsUpdated(ObjectCursor<Account> accountCursor) {
        // Check to see if the current account hasn't been set, or the account cursor is empty
        if (mAccount == null || !accountCursor.moveToFirst()) {
            return true;
        }

        // Check to see if the number of accounts are different, from the number we saw on the last
        // updated
        if (mCurrentAccountUris.size() != accountCursor.getCount()) {
            return true;
        }

        // Check to see if the account list is different or if the current account is not found in
        // the cursor.
        boolean foundCurrentAccount = false;
        do {
            final Account account = accountCursor.getModel();
            if (!foundCurrentAccount && mAccount.uri.equals(account.uri)) {
                if (mAccount.settingsDiffer(account)) {
                    // Settings changed, and we don't need to look any further.
                    return true;
                }
                foundCurrentAccount = true;
            }
            // Is there a new account that we do not know about?
            if (!mCurrentAccountUris.contains(account.uri)) {
                return true;
            }
        } while (accountCursor.moveToNext());

        // As long as we found the current account, the list hasn't been updated
        return !foundCurrentAccount;
    }

    /**
     * Updates accounts for the app. If the current account is missing, the first
     * account in the list is set to the current account (we <em>have</em> to choose something).
     *
     * @param accounts cursor into the AccountCache
     * @return true if the update was successful, false otherwise
     */
    private boolean updateAccounts(ObjectCursor<Account> accounts) {
        if (accounts == null || !accounts.moveToFirst()) {
            return false;
        }

        final Account[] allAccounts = Account.getAllAccounts(accounts);
        // A match for the current account's URI in the list of accounts.
        Account currentFromList = null;

        // Save the uris for the accounts and find the current account in the updated cursor.
        mCurrentAccountUris.clear();
        for (final Account account : allAccounts) {
            LogUtils.d(LOG_TAG, "updateAccounts(%s)", account);
            mCurrentAccountUris.add(account.uri);
            if (mAccount != null && account.uri.equals(mAccount.uri)) {
                currentFromList = account;
            }
        }

        // 1. current account is already set and is in allAccounts:
        //    1a. It has changed -> load the updated account.
        //    2b. It is unchanged -> no-op
        // 2. current account is set and is not in allAccounts -> pick first (acct was deleted?)
        // 3. saved preference has an account -> pick that one
        // 4. otherwise just pick first

        boolean accountChanged = false;
        /// Assume case 4, initialize to first account, and see if we can find anything better.
        Account newAccount = allAccounts[0];
        if (currentFromList != null) {
            // Case 1: Current account exists but has changed
            if (!currentFromList.equals(mAccount)) {
                newAccount = currentFromList;
                accountChanged = true;
            }
            // Case 1b: else, current account is unchanged: nothing to do.
        } else {
            // Case 2: Current account is not in allAccounts, the account needs to change.
            accountChanged = true;
            if (mAccount == null) {
                // Case 3: Check for last viewed account, and check if it exists in the list.
                final String lastAccountUri = MailAppProvider.getInstance().getLastViewedAccount();
                if (lastAccountUri != null) {
                    for (final Account account : allAccounts) {
                        if (lastAccountUri.equals(account.uri.toString())) {
                            newAccount = account;
                            break;
                        }
                    }
                }
            }
        }
        if (accountChanged) {
            /// tct: If this is global search, take combined account as the new account.
            // This lead to enter combined inbox.
            if (mGlobalSearch) {
                LogUtils.logFeature(LogTag.SEARCH_TAG, "[Global Search]Change account to combined account");
                        newAccount = allAccounts[allAccounts.length - 1];
            }
            changeAccount(newAccount);
        }
        // Whether we have updated the current account or not, we need to update the list of
        // accounts in the ActionBar.
        mAllAccounts = allAccounts;
        mAllAccountObservers.notifyChanged();
        return (allAccounts.length > 0);
    }

    private void disableNotifications() {
        mNewEmailReceiver.activate(mContext, this);
    }

    private void enableNotifications() {
        mNewEmailReceiver.deactivate();
    }

    private void disableNotificationsOnAccountChange(Account account) {
        // If the new mail suppression receiver is activated for a different account, we want to
        // activate it for the new account.
        if (mNewEmailReceiver.activated() &&
                !mNewEmailReceiver.notificationsDisabledForAccount(account)) {
            // Deactivate the current receiver, otherwise multiple receivers may be registered.
            mNewEmailReceiver.deactivate();
            mNewEmailReceiver.activate(mContext, this);
        }
    }

    /**
     * Destructive actions on Conversations. This class should only be created by controllers, and
     * clients should only require {@link DestructiveAction}s, not specific implementations of the.
     * Only the controllers should know what kind of destructive actions are being created.
     */
    public class ConversationAction implements DestructiveAction {
        /**
         * The action to be performed. This is specified as the resource ID of the menu item
         * corresponding to this action: R.id.delete, R.id.report_spam, etc.
         */
        private final int mAction;
        /** The action will act upon these conversations */
        private final Collection<Conversation> mTarget;
        /** Whether this destructive action has already been performed */
        private boolean mCompleted;
        /** Whether this is an action on the currently selected set. */
        private final boolean mIsSelectedSet;

        private UndoCallback mCallback;

        /**
         * Create a listener object.
         * @param action action is one of four constants: R.id.y_button (archive),
         * R.id.delete , R.id.mute, and R.id.report_spam.
         * @param target Conversation that we want to apply the action to.
         * @param isBatch whether the conversations are in the currently selected batch set.
         */
        public ConversationAction(int action, Collection<Conversation> target, boolean isBatch) {
            mAction = action;
            mTarget = ImmutableList.copyOf(target);
            mIsSelectedSet = isBatch;
        }

        @Override
        public void setUndoCallback(UndoCallback undoCallback) {
            mCallback = undoCallback;
        }

        /**
         * The action common to child classes. This performs the action specified in the constructor
         * on the conversations given here.
         */
        @Override
        public void performAction() {
            if (isPerformed()) {
                return;
            }
            boolean undoEnabled = mAccount.supportsCapability(AccountCapabilities.UNDO);

            // Are we destroying the currently shown conversation? Show the next one.
            if (LogUtils.isLoggable(LOG_TAG, LogUtils.DEBUG)){
                LogUtils.d(LOG_TAG, "ConversationAction.performAction():"
                        + "\nmTarget=%s\nCurrent=%s",
                        Conversation.toString(mTarget), mCurrentConversation);
            }

            if (mConversationListCursor == null) {
                LogUtils.e(LOG_TAG, "null ConversationCursor in ConversationAction.performAction():"
                        + "\nmTarget=%s\nCurrent=%s",
                        Conversation.toString(mTarget), mCurrentConversation);
                return;
            }

            if (mAction == R.id.archive) {
                LogUtils.d(LOG_TAG, "Archiving");
                mConversationListCursor.archive(mTarget, mCallback);
            } else if (mAction == R.id.delete) {
                LogUtils.d(LOG_TAG, "Deleting");
                mConversationListCursor.delete(mTarget, mCallback);
                if (mFolder.supportsCapability(FolderCapabilities.DELETE_ACTION_FINAL)) {
                    undoEnabled = false;
                }
            } else if (mAction == R.id.mute) {
                LogUtils.d(LOG_TAG, "Muting");
                if (mFolder.supportsCapability(FolderCapabilities.DESTRUCTIVE_MUTE)) {
                    for (Conversation c : mTarget) {
                        c.localDeleteOnUpdate = true;
                    }
                }
                mConversationListCursor.mute(mTarget, mCallback);
            } else if (mAction == R.id.report_spam) {
                LogUtils.d(LOG_TAG, "Reporting spam");
                mConversationListCursor.reportSpam(mTarget, mCallback);
            } else if (mAction == R.id.mark_not_spam) {
                LogUtils.d(LOG_TAG, "Marking not spam");
                mConversationListCursor.reportNotSpam(mTarget, mCallback);
            } else if (mAction == R.id.report_phishing) {
                LogUtils.d(LOG_TAG, "Reporting phishing");
                mConversationListCursor.reportPhishing(mTarget, mCallback);
            } else if (mAction == R.id.remove_star) {
                LogUtils.d(LOG_TAG, "Removing star");
                // Star removal is destructive in the Starred folder.
                mConversationListCursor.updateBoolean(mTarget, ConversationColumns.STARRED,
                        false);
            } else if (mAction == R.id.mark_not_important) {
                LogUtils.d(LOG_TAG, "Marking not-important");
                // Marking not important is destructive in a mailbox
                // containing only important messages
                if (mFolder != null && mFolder.isImportantOnly()) {
                    for (Conversation conv : mTarget) {
                        conv.localDeleteOnUpdate = true;
                    }
                }
                mConversationListCursor.updateInt(mTarget, ConversationColumns.PRIORITY,
                        UIProvider.ConversationPriority.LOW);
            } else if (mAction == R.id.discard_drafts) {
                LogUtils.d(LOG_TAG, "Discarding draft messages");
                // Discarding draft messages is destructive in a "draft" mailbox
                if (mFolder != null && mFolder.isDraft()) {
                    for (Conversation conv : mTarget) {
                        conv.localDeleteOnUpdate = true;
                    }
                }
                mConversationListCursor.discardDrafts(mTarget);
                // We don't support undoing discarding drafts
                undoEnabled = false;
            } else if (mAction == R.id.discard_outbox) {
                LogUtils.d(LOG_TAG, "Discarding failed messages in Outbox");
                mConversationListCursor.moveFailedIntoDrafts(mTarget);
                undoEnabled = false;
            }
            if (undoEnabled) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onUndoAvailable(new ToastBarOperation(mTarget.size(), mAction,
                                ToastBarOperation.UNDO, mIsSelectedSet, mFolder));
                    }
                }, mShowUndoBarDelay);
            }
            refreshConversationList();
            if (mIsSelectedSet) {
                mSelectedSet.clear();
            }
        }

        /**
         * Returns true if this action has been performed, false otherwise.
         *
         */
        private synchronized boolean isPerformed() {
            if (mCompleted) {
                return true;
            }
            mCompleted = true;
            return false;
        }
    }

    // Called from the FolderSelectionDialog after a user is done selecting folders to assign the
    // conversations to.
    @Override
    public final void assignFolder(Collection<FolderOperation> folderOps,
            Collection<Conversation> target, boolean batch, boolean showUndo,
            final boolean isMoveTo) {
        // Actions are destructive only when the current folder can be un-assigned from and
        // when the list of folders contains the current folder.
        final boolean isDestructive = mFolder
                .supportsCapability(FolderCapabilities.ALLOWS_REMOVE_CONVERSATION)
                && FolderOperation.isDestructive(folderOps, mFolder);
        LogUtils.d(LOG_TAG, "onFolderChangesCommit: isDestructive = %b", isDestructive);
        if (isDestructive) {
            for (final Conversation c : target) {
                c.localDeleteOnUpdate = true;
            }
        }
        final DestructiveAction folderChange;
        final UndoCallback undoCallback = isMoveTo ?
                getUndoCallbackForDestructiveActionsWithAutoAdvance(R.id.move_to,
                        mCurrentConversation)
                : null;
        // Update the UI elements depending no their visibility and availability
        // TODO(viki): Consolidate this into a single method requestDelete.
        if (isDestructive) {
            /*
             * If this is a MOVE operation, we want the action folder to be the destination folder.
             * Otherwise, we want it to be the current folder.
             *
             * A set of folder operations is a move if there are exactly two operations: an add and
             * a remove.
             */
            final Folder actionFolder;
            if (folderOps.size() != 2) {
                actionFolder = mFolder;
            } else {
                Folder addedFolder = null;
                boolean hasRemove = false;
                for (final FolderOperation folderOperation : folderOps) {
                    if (folderOperation.mAdd) {
                        addedFolder = folderOperation.mFolder;
                    } else {
                        hasRemove = true;
                    }
                }

                if (hasRemove && addedFolder != null) {
                    actionFolder = addedFolder;
                } else {
                    actionFolder = mFolder;
                }
            }

            folderChange = getDeferredFolderChange(target, folderOps, isDestructive,
                    batch, showUndo, isMoveTo, actionFolder, undoCallback);
            delete(0, target, folderChange, batch);
        } else {
            folderChange = getFolderChange(target, folderOps, isDestructive,
                    batch, showUndo, false /* isMoveTo */, mFolder, undoCallback);
            requestUpdate(folderChange);
        }
    }

    @Override
    public final void onRefreshRequired() {
        if (isAnimating() || isDragging()) {
            final ConversationListFragment f = getConversationListFragment();
            LogUtils.w(ConversationCursor.LOG_TAG,
                    "onRefreshRequired: delay until animating done. cursor=%s adapter=%s",
                    mConversationListCursor, (f != null) ? f.getAnimatedAdapter() : null);
            return;
        }
        //TS: chao-zhang 2015-12-23 EMAIL FEATURE_1126514 MOD_S
        // Refresh the query in the background
        if (mConversationListCursor != null && mConversationListCursor.isRefreshRequired()) {
            mConversationListCursor.refresh();
        }
        //TS: chao-zhang 2015-12-23 EMAIL FEATURE_1126514 MOD_E
    }

    @Override
    public void startDragMode() {
        mIsDragHappening = true;
    }

    @Override
    public void stopDragMode() {
        mIsDragHappening = false;
        if (mConversationListCursor.isRefreshReady()) {
            LogUtils.i(ConversationCursor.LOG_TAG, "Stopped dragging: try sync");
            onRefreshReady();
        }

        if (mConversationListCursor.isRefreshRequired()) {
            LogUtils.i(ConversationCursor.LOG_TAG, "Stopped dragging: refresh");
            mConversationListCursor.refresh();
        }
    }

    private boolean isDragging() {
        return mIsDragHappening;
    }

    @Override
    public boolean isAnimating() {
        boolean isAnimating = false;
        ConversationListFragment convListFragment = getConversationListFragment();
        if (convListFragment != null) {
            isAnimating = convListFragment.isAnimating();
        }
        return isAnimating;
    }

    /**
     * Called when the {@link ConversationCursor} is changed or has new data in it.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public final void onRefreshReady() {
        LogUtils.d(LOG_TAG, "Received refresh ready callback for folder %s",
                mFolder != null ? mFolder.id : "-1");

        if (mDestroyed) {
            LogUtils.i(LOG_TAG, "ignoring onRefreshReady on destroyed AAC");
            return;
        }

        if (!isAnimating()) {
            // Swap cursors
            mConversationListCursor.sync();
        } else {
            // (CLF guaranteed to be non-null due to check in isAnimating)
            LogUtils.w(LOG_TAG,
                    "AAC.onRefreshReady suppressing sync() due to animation. cursor=%s aa=%s",
                    mConversationListCursor, getConversationListFragment().getAnimatedAdapter());
        }
        mTracker.onCursorUpdated();
        perhapsShowFirstSearchResult();
    }

    @Override
    public final void onDataSetChanged() {
        updateConversationListFragment();
        mConversationListObservable.notifyChanged();
        mSelectedSet.validateAgainstCursor(mConversationListCursor);
    }

    /**
     * If the Conversation List Fragment is visible, updates the fragment.
     */
    private void updateConversationListFragment() {
        final ConversationListFragment convList = getConversationListFragment();
        if (convList != null) {
            refreshConversationList();
            if (isFragmentVisible(convList)) {
                informCursorVisiblity(true);
            }
        }
    }

    /**
     * This class handles throttled refresh of the conversation list
     */
    static class RefreshTimerTask extends TimerTask {
        final Handler mHandler;
        final AbstractActivityController mController;

        RefreshTimerTask(AbstractActivityController controller, Handler handler) {
            mHandler = handler;
            mController = controller;
        }

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d(LOG_TAG, "Delay done... calling onRefreshRequired");
                    mController.onRefreshRequired();
                }});
        }
    }

    /**
     * Cancel the refresh task, if it's running
     */
    private void cancelRefreshTask () {
        if (mConversationListRefreshTask != null) {
            mConversationListRefreshTask.cancel();
            mConversationListRefreshTask = null;
        }
    }

    @Override
    public void onAnimationEnd(AnimatedAdapter animatedAdapter) {
        if (animatedAdapter != null) {
            LogUtils.i(LOG_TAG, "AAC.onAnimationEnd. cursor=%s adapter=%s", mConversationListCursor,
                    animatedAdapter);
        }
        if (mConversationListCursor == null) {
            LogUtils.e(LOG_TAG, "null ConversationCursor in onAnimationEnd");
            return;
        }
        if (mConversationListCursor.isRefreshReady()) {
            LogUtils.i(ConversationCursor.LOG_TAG, "Stopped animating: try sync");
            onRefreshReady();
        }

        if (mConversationListCursor.isRefreshRequired()) {
            LogUtils.i(ConversationCursor.LOG_TAG, "Stopped animating: refresh");
            mConversationListCursor.refresh();
        }
        if (mRecentsDataUpdated) {
            mRecentsDataUpdated = false;
            mRecentFolderObservers.notifyChanged();
        }
    }

    @Override
    public void onSetEmpty() {
        // There are no selected conversations. Ensure that the listener and its associated actions
        // are blanked out.
        setListener(null, -1);
    }

    @Override
    public void onSetPopulated(ConversationSelectionSet set) {
        mCabActionMenu = new SelectedConversationsActionMenu(mActivity, set, mFolder);
      //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
        mCabActionMenu.setCallback(mActionBarController);
      //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E
        if (mViewMode.isListMode() || (mIsTablet && mViewMode.isConversationMode())) {
            enableCabMode();
        }
    }

    @Override
    public void onSetChanged(ConversationSelectionSet set) {
        // Do nothing. We don't care about changes to the set.
    }

    @Override
    public ConversationSelectionSet getSelectedSet() {
        return mSelectedSet;
    }

    /**
     * Disable the Contextual Action Bar (CAB). The selected set is not changed.
     */
    protected void disableCabMode() {
        // Commit any previous destructive actions when entering/ exiting CAB mode.
        commitDestructiveActions(true);
        if (mCabActionMenu != null) {
            mCabActionMenu.deactivate();
        }
    }

    /**
     * Re-enable the CAB menu if required. The selection set is not changed.
     */
    protected void enableCabMode() {
        if (mCabActionMenu != null &&
                !(isDrawerEnabled() && mDrawerContainer.isDrawerOpen(mDrawerPullout))) {
            mCabActionMenu.activate();
        }
    }

    /**
     * Re-enable CAB mode only if we have an active selection
     */
    protected void maybeEnableCabMode() {
        if (!mSelectedSet.isEmpty()) {
            if (mCabActionMenu != null) {
                mCabActionMenu.activate();
            }
        }
    }

    /**
     * Unselect conversations and exit CAB mode.
     */
    protected final void exitCabMode() {
        mSelectedSet.clear();
    }

    @Override
    public void startSearch() {
        if (mAccount == null) {
            // We cannot search if there is no account. Drop the request to the floor.
            LogUtils.d(LOG_TAG, "AbstractActivityController.startSearch(): null account");
            return;
        }
        if (mAccount.supportsSearch()) {
            mActionBarController.expandSearch();
        } else {
            //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
            Utility.showShortToast(mActivity.getActivityContext(), R.string.search_unsupported);
            //Toast.makeText(mActivity.getActivityContext(), mActivity.getActivityContext()
            //        .getString(R.string.search_unsupported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void exitSearchMode() {
        if (mViewMode.getMode() == ViewMode.SEARCH_RESULTS_LIST) {
            mActivity.finish();
        }
    }

    /**
     * Supports dragging conversations to a folder.
     */
    @Override
    public boolean supportsDrag(DragEvent event, Folder folder) {
        return (folder != null
                && event != null
                && event.getClipDescription() != null
                && folder.supportsCapability
                    (UIProvider.FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES)
                && !mFolder.equals(folder));
    }

    /**
     * Handles dropping conversations to a folder.
     */
    @Override
    public void handleDrop(DragEvent event, final Folder folder) {
        if (!supportsDrag(event, folder)) {
            return;
        }
        if (folder.isType(UIProvider.FolderType.STARRED)) {
            // Moving a conversation to the starred folder adds the star and
            // removes the current label
            handleDropInStarred(folder);
            return;
        }
        if (mFolder.isType(UIProvider.FolderType.STARRED)) {
            handleDragFromStarred(folder);
            return;
        }
        final ArrayList<FolderOperation> dragDropOperations = new ArrayList<FolderOperation>();
        final Collection<Conversation> conversations = mSelectedSet.values();
        // Add the drop target folder.
        dragDropOperations.add(new FolderOperation(folder, true));
        // Remove the current folder unless the user is viewing "all".
        // That operation should just add the new folder.
        boolean isDestructive = !mFolder.isViewAll()
                && mFolder.supportsCapability
                    (UIProvider.FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES);
        if (isDestructive) {
            dragDropOperations.add(new FolderOperation(mFolder, false));
        }
        // Drag and drop is destructive: we remove conversations from the
        // current folder.
        final DestructiveAction action =
                getFolderChange(conversations, dragDropOperations, isDestructive,
                        true /* isBatch */, true /* showUndo */, true /* isMoveTo */, folder,
                        null /* undoCallback */);
        if (isDestructive) {
            delete(0, conversations, action, true);
        } else {
            action.performAction();
        }
    }

    private void handleDragFromStarred(Folder folder) {
        final Collection<Conversation> conversations = mSelectedSet.values();
        // The conversation list deletes and performs the action if it exists.
        final ConversationListFragment convListFragment = getConversationListFragment();
        // There should always be a convlistfragment, or the user could not have
        // dragged/ dropped conversations.
        if (convListFragment != null) {
            LogUtils.d(LOG_TAG, "AAC.requestDelete: ListFragment is handling delete.");
            ArrayList<ConversationOperation> ops = new ArrayList<ConversationOperation>();
            ArrayList<Uri> folderUris;
            ArrayList<Boolean> adds;
            for (Conversation target : conversations) {
                folderUris = new ArrayList<Uri>();
                adds = new ArrayList<Boolean>();
                folderUris.add(folder.folderUri.fullUri);
                adds.add(Boolean.TRUE);
                final HashMap<Uri, Folder> targetFolders =
                        Folder.hashMapForFolders(target.getRawFolders());
                targetFolders.put(folder.folderUri.fullUri, folder);
                ops.add(mConversationListCursor.getConversationFolderOperation(target,
                        folderUris, adds, targetFolders.values()));
            }
            if (mConversationListCursor != null) {
                mConversationListCursor.updateBulkValues(ops);
            }
            refreshConversationList();
            mSelectedSet.clear();
        }
    }

    private void handleDropInStarred(Folder folder) {
        final Collection<Conversation> conversations = mSelectedSet.values();
        // The conversation list deletes and performs the action if it exists.
        final ConversationListFragment convListFragment = getConversationListFragment();
        // There should always be a convlistfragment, or the user could not have
        // dragged/ dropped conversations.
        if (convListFragment != null) {
            LogUtils.d(LOG_TAG, "AAC.requestDelete: ListFragment is handling delete.");
            convListFragment.requestDelete(R.id.change_folders, conversations,
                    new DroppedInStarredAction(conversations, mFolder, folder));
        }
    }

    // When dragging conversations to the starred folder, remove from the
    // original folder and add a star
    private class DroppedInStarredAction implements DestructiveAction {
        private final Collection<Conversation> mConversations;
        private final Folder mInitialFolder;
        private final Folder mStarred;

        public DroppedInStarredAction(Collection<Conversation> conversations, Folder initialFolder,
                Folder starredFolder) {
            mConversations = conversations;
            mInitialFolder = initialFolder;
            mStarred = starredFolder;
        }

        @Override
        public void setUndoCallback(UndoCallback undoCallback) {
            return;     // currently not applicable
        }

        @Override
        public void performAction() {
            ToastBarOperation undoOp = new ToastBarOperation(mConversations.size(),
                    R.id.change_folders, ToastBarOperation.UNDO, true /* batch */, mInitialFolder);
            onUndoAvailable(undoOp);
            ArrayList<ConversationOperation> ops = new ArrayList<ConversationOperation>();
            ContentValues values = new ContentValues();
            ArrayList<Uri> folderUris;
            ArrayList<Boolean> adds;
            ConversationOperation operation;
            for (Conversation target : mConversations) {
                folderUris = new ArrayList<Uri>();
                adds = new ArrayList<Boolean>();
                folderUris.add(mStarred.folderUri.fullUri);
                adds.add(Boolean.TRUE);
                folderUris.add(mInitialFolder.folderUri.fullUri);
                adds.add(Boolean.FALSE);
                final HashMap<Uri, Folder> targetFolders =
                        Folder.hashMapForFolders(target.getRawFolders());
                targetFolders.put(mStarred.folderUri.fullUri, mStarred);
                targetFolders.remove(mInitialFolder.folderUri.fullUri);
                values.put(ConversationColumns.STARRED, true);
                operation = mConversationListCursor.getConversationFolderOperation(target,
                        folderUris, adds, targetFolders.values(), values);
                ops.add(operation);
            }
            if (mConversationListCursor != null) {
                mConversationListCursor.updateBulkValues(ops);
            }
            refreshConversationList();
            mSelectedSet.clear();
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mToastBar != null && !mToastBar.isEventInToastBar(event)) {
                // if the toast bar is still animating, ignore this attempt to hide it
                if (mToastBar.isAnimating()) {
                    return;
                }

                // if the toast bar has not been seen long enough, ignore this attempt to hide it
                if (mToastBar.cannotBeHidden()) {
                    return;
                }

                // hide the toast bar
                mToastBar.hide(true /* animated */, false /* actionClicked */);
            }
        }
    }

    @Override
    public void onConversationSeen() {
        mPagerController.onConversationSeen();
    }

    @Override
    public boolean isInitialConversationLoading() {
        return mPagerController.isInitialConversationLoading();
    }

    /**
     * Check if the fragment given here is visible. Checking {@link Fragment#isVisible()} is
     * insufficient because that doesn't check if the window is currently in focus or not.
     */
    private boolean isFragmentVisible(Fragment in) {
        return in != null && in.isVisible() && mActivity.hasWindowFocus();
    }

    /**
     * This class handles callbacks that create a {@link ConversationCursor}.
     */
    private class ConversationListLoaderCallbacks implements
        LoaderManager.LoaderCallbacks<ConversationCursor> {

        @Override
        public Loader<ConversationCursor> onCreateLoader(int id, Bundle args) {
            final Account account = args.getParcelable(BUNDLE_ACCOUNT_KEY);
            final Folder folder = args.getParcelable(BUNDLE_FOLDER_KEY);
            final boolean ignoreInitialConversationLimit =
                    args.getBoolean(BUNDLE_IGNORE_INITIAL_CONVERSATION_LIMIT_KEY, false);
            if (account == null || folder == null) {
                return null;
            }
            /// TCT: for load search build a local search uri.
            /// default folder.conversationListUri.
            Uri uri = folder.conversationListUri;
            if (id == LOADER_LOCALSEARCH_CONVERSATION_LIST) {
                String filter = args.getString(SearchParams.BUNDLE_QUERY_TERM);
                String field = args.getString(SearchParams.BUNDLE_QUERY_FIELD);
                if (!TextUtils.isEmpty(filter) && !TextUtils.isEmpty(field)
                        && (folder.localSearchUri != null)) {
                    uri = folder.buildLocalSearchUri(filter, field);
                } else {
                    uri = folder.conversationListUri;
                    LogUtils.logFeature(
                            LogTag.SEARCH_TAG,
                            "create local search uri failed for some field is null, use default conversationListUri");
                }
            }
            //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD_S
            int orderBy = args.getInt(BUNDLE_CONVERSATION_ORDER_KEY,SortHelper.SORT_BY_DATE);
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
            //Note: get the star toggle's status, add to uri query parameters.
            uri = uri.buildUpon()
                    .appendQueryParameter(UIProvider.FLAG_FAVORITE_QUERY_PARAMETER, String.valueOf(mCheckStatus))
                    .appendQueryParameter(UIProvider.ORDER_QUERY_PARAMETER, String.valueOf(orderBy))
                    .build();
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
            //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD_E
            LogUtils.d(LOG_TAG,
                    "ConversationListLoaderCallbacks.onCreateLoader uri %s", uri);
            //[FEATURE]-Mod-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,PR-622096,
            Uri tmpfolderUri = folder.conversationListUri;
            String tmpUri= folder.conversationListUri.toString() ;
            String accountId = mAccount.uri.getLastPathSegment();
            int index = folder.conversationListUri.toString().lastIndexOf("/");
            if (EmailApplication.isOrangeImapFeatureOn() &&
                    com.tct.emailcommon.provider.Account.isOrangeImapAccount(mContext, Long.parseLong(accountId))){
                if(folder.isType(FolderType.DRAFT)){
                    tmpUri = tmpUri.substring(0,index)+"/"+Mailbox.getOrangeImapDraftboxId(mContext,Long.valueOf(accountId));
                    tmpfolderUri = Uri.parse(tmpUri);
                }else if(folder.isType(FolderType.SENT)){
                    tmpUri = tmpUri.substring(0,index)+"/"+Mailbox.getOrangeImapSentboxId(mContext,Long.valueOf(accountId));
                    tmpfolderUri = Uri.parse(tmpUri);
                }else if(folder.isType(FolderType.TRASH)){
                    tmpUri = tmpUri.substring(0,index)+"/"+Mailbox.getOrangeImapTrashboxId(mContext,Long.valueOf(accountId));
                    tmpfolderUri = Uri.parse(tmpUri);
                }
            }
            //todo verity here
            return new ConversationCursorLoader(mActivity, account,
                    uri, folder.name,
                    ignoreInitialConversationLimit);
//            return new ConversationCursorLoader(mActivity, account,
//                    tmpfolderUri, folder.getTypeDescription(),
//                    ignoreInitialConversationLimit);
            //[FEATURE]-Mod-BEGIN by TSNJ,Zhenhua.Fan
        }

        @Override
        public void onLoadFinished(Loader<ConversationCursor> loader, ConversationCursor data) {
            LogUtils.d(LOG_TAG,
                    "IN AAC.ConversationCursor.onLoadFinished, data=%s loader=%s this=%s",
                    data, loader, this);
            if (isDrawerEnabled() && mDrawerListener.getDrawerState() != DrawerLayout.STATE_IDLE) {
                LogUtils.d(LOG_TAG, "ConversationListLoaderCallbacks.onLoadFinished: ignoring.");
                mConversationListLoadFinishedIgnored = true;
                return;
            }
            // Clear our all pending destructive actions before swapping the conversation cursor
            destroyPending(null);
            mConversationListCursor = data;
            mConversationListCursor.addListener(AbstractActivityController.this);
            mDrawIdler.setListener(mConversationListCursor);
            mTracker.onCursorUpdated();
            mConversationListObservable.notifyChanged();
            // Handle actions that were deferred until after the conversation list was loaded.
            for (LoadFinishedCallback callback : mConversationListLoadFinishedCallbacks) {
                callback.onLoadFinished();
            }
            mConversationListLoadFinishedCallbacks.clear();

            final ConversationListFragment convList = getConversationListFragment();
            if (isFragmentVisible(convList)) {
                // The conversation list is already listening to list changes and gets notified
                // in the mConversationListObservable.notifyChanged() line above. We only need to
                // check and inform the cursor of the change in visibility here.
                informCursorVisiblity(true);
            }
            perhapsShowFirstSearchResult();

            /// TCT: update search count. @{
            int count = data.getCount();
            //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
            mConversationCount = count;
            //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E
            LogUtils.d(LOG_TAG, "AAC.ConversationCursor.onLoadFinished, count [%s]", count);
            if (loader.getId() == LOADER_LOCALSEARCH_CONVERSATION_LIST) {
                LogUtils.logFeature(LogTag.SEARCH_TAG, "AAC.onLoadFinished, update result as [%s]",
                        count);
                updateSearchResult(count);
            } else if (mConvListContext != null && mConvListContext.isLocalSearch()){
                // Normal conversation loader (LOADER_CONVERSATION_LIST) but in local search mode.
                // This case happened:
                // 1. user click search menu and enter local search mode, input nothing but list refreshing.
                // 2. user delete query in search view, and back to initial search mode.
                // Any way, in initial search mode, set search result as 0.
                LogUtils.logFeature(LogTag.SEARCH_TAG, "AAC.onLoadFinished recovery to inital model,set count 0 ");
                updateSearchResult(0);
            }
            /// @}

            /// TCT: For local search results, we need validate selected set manually, otherwise,
            /// this necessary process would miss and selected number does not update, such as
            /// deleting items in local search and rotating phone at once.
            /// Because CC in local search would not be notified refreshing due to the special observing uri,
            /// and never trigger AAC's onDatasetChanged. @{
            if (loader.getId() == LOADER_LOCALSEARCH_CONVERSATION_LIST) {
                mSelectedSet.validateAgainstCursor(mConversationListCursor);
            }
            /// @}

            /// TCT: ConversationListFragment doesn't existed in some special case, such as ui controller changed
            /// dynamically, re-create and show it. @{
            if (getConversationListFragment() == null) {
                LogUtils.d(LOG_TAG,
                        "ConversationListFragment doesn't existed in some special case, re-create and show it.");
                if (mActivity == null || mActivity.getFragmentManager().isDestroyed()) {
                    LogUtils.e(LOG_TAG, " mActivity is destoryed, can't show conversation list.");
                    return;
                }
                int mode = mViewMode.getMode();
                switch (mode) {
                    case ViewMode.CONVERSATION_LIST:
                    case ViewMode.SEARCH_RESULTS_LIST:
                        showConversationList(mConvListContext);
                        break;
                    case ViewMode.CONVERSATION:
                        if (AbstractActivityController.this instanceof TwoPaneController) {
                            // for two pane controller, we have to show list fragment it even if the view mode was
                                    // in conversation view, cause the fragment also be shown at the left screen
                            ((TwoPaneController)AbstractActivityController.this).renderConversationList();
                        }
                        break;

                    default:
                        break;
                }
            }
            /// @}
          //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-913979 MOD_S
            if(!mHaveSearchResults){
                mHandler.postDelayed(mLoadingViewRunnable, 3000);
            }
          //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-913979 MOD_E
        }

        @Override
        public void onLoaderReset(Loader<ConversationCursor> loader) {
            LogUtils.d(LOG_TAG,
                    "IN AAC.ConversationCursor.onLoaderReset, data=%s loader=%s this=%s",
                    mConversationListCursor, loader, this);

            if (mConversationListCursor != null) {
                // Unregister the listener
                mConversationListCursor.removeListener(AbstractActivityController.this);
                mDrawIdler.setListener(null);
                mConversationListCursor = null;

                // Inform anyone who is interested about the change
                mTracker.onCursorUpdated();
                mConversationListObservable.notifyChanged();
            }
        }
    }
  //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-913979 MOD_S
    private final Runnable mLoadingViewRunnable = new Runnable() {

        @Override
        public void run() {
          //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-927510 MOD_S
//            mConversationListCursor.readyToShowSearchResults();
            if(mConversationListCursor != null){
                mConversationListCursor.readyToShowSearchResults();
            }
          //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-927510 MOD_E
        }
    };
  //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-913979 MOD_E

    /// TCT: update search result. Both local and remote search
    public void updateSearchResult(int count) {
        // Only we are in search update the count.
        if (mActionBarController != null &&
        (mActionBarController.getSearch() != null
                || (mViewMode.isSearchMode(mViewMode.getMode())))) {
            mActionBarController.updateSearchCount(count);
        }
    }

    /**
     * Class to perform {@link LoaderManager.LoaderCallbacks} for creating {@link Folder} objects.
     */
    private class FolderLoads implements LoaderManager.LoaderCallbacks<ObjectCursor<Folder>> {

        @Override
        public Loader<ObjectCursor<Folder>> onCreateLoader(int id, Bundle args) {
            final String[] everything = UIProvider.FOLDERS_PROJECTION;
            switch (id) {
                case LOADER_FOLDER_CURSOR:
                    LogUtils.d(LOG_TAG, "LOADER_FOLDER_CURSOR created");
                    final ObjectCursorLoader<Folder> loader = new
                            ObjectCursorLoader<Folder>(
                            mContext, mFolder.folderUri.fullUri, everything, Folder.FACTORY);
                    loader.setUpdateThrottle(mFolderItemUpdateDelayMs);
                    return loader;
                case LOADER_RECENT_FOLDERS:
                    LogUtils.d(LOG_TAG, "LOADER_RECENT_FOLDERS created");
                    if (mAccount != null && mAccount.recentFolderListUri != null
                            && !mAccount.recentFolderListUri.equals(Uri.EMPTY)) {
                        return new ObjectCursorLoader<Folder>(mContext,
                                mAccount.recentFolderListUri, everything, Folder.FACTORY);
                    }
                    break;
                case LOADER_ACCOUNT_INBOX:
                    LogUtils.d(LOG_TAG, "LOADER_ACCOUNT_INBOX created");
                    final Uri defaultInbox = Settings.getDefaultInboxUri(mAccount.settings);
                    final Uri inboxUri = defaultInbox.equals(Uri.EMPTY) ?
                            mAccount.folderListUri : defaultInbox;
                    LogUtils.d(LOG_TAG, "Loading the default inbox: %s", inboxUri);
                    if (inboxUri != null) {
                        return new ObjectCursorLoader<Folder>(mContext, inboxUri,
                                everything, Folder.FACTORY);
                    }
                    break;
                case LOADER_SEARCH:
                    LogUtils.d(LOG_TAG, "LOADER_SEARCH created");
                    return Folder.forSearchResults(mAccount,
                            args.getString(ConversationListContext.EXTRA_SEARCH_QUERY),
                            /// TCT: remote search, add search filed. @{
                            args.getString(SearchParams.BUNDLE_QUERY_FIELD),
                            /// @}
                            // We can just use current time as a unique identifier for this search
                            Long.toString(SystemClock.uptimeMillis()),
                            mActivity.getActivityContext());
                case LOADER_FIRST_FOLDER:
                    LogUtils.d(LOG_TAG, "LOADER_FIRST_FOLDER created");
                    final Uri folderUri = args.getParcelable(Utils.EXTRA_FOLDER_URI);
                    mConversationToShow = args.getParcelable(Utils.EXTRA_CONVERSATION);
                    if (mConversationToShow != null && mConversationToShow.position < 0){
                        mConversationToShow.position = 0;
                    }
                    return new ObjectCursorLoader<Folder>(mContext, folderUri,
                            everything, Folder.FACTORY);
                default:
                    LogUtils.wtf(LOG_TAG, "FolderLoads.onCreateLoader(%d) for invalid id", id);
                    return null;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<ObjectCursor<Folder>> loader, ObjectCursor<Folder> data) {
            if (data == null) {
                LogUtils.e(LOG_TAG, "Received null cursor from loader id: %d", loader.getId());
            }
            switch (loader.getId()) {
                case LOADER_FOLDER_CURSOR:
                    LogUtils.d(LOG_TAG,"LOADER_FOLDER_CURSOR->onLoadFinished",
                            "LOADER_FOLDER_CURSOR.onLoadFinished");
                    if (data != null && data.moveToFirst()) {
                        final Folder folder = data.getModel();
                        setHasFolderChanged(folder);
                        mFolder = folder;
                        mFolderObservable.notifyChanged();
                    } else {
                        LogUtils.d(LOG_TAG, "Unable to get the folder %s",
                                mFolder != null ? mFolder.name : "");
                    }
                    break;
                case LOADER_RECENT_FOLDERS:
                    // Few recent folders and we are running on a phone? Populate the default
                    // recents. The number of default recent folders is at least 2: every provider
                    // has at least two folders, and the recent folder count never decreases.
                    // Having a single recent folder is an erroneous case, and we can gracefully
                    // recover by populating default recents. The default recents will not stomp on
                    // the existing value: it will be shown in addition to the default folders:
                    // the max number of recent folders is more than 1+num(defaultRecents).
                    LogUtils.d(LOG_TAG,"LOADER_RECENT_FOLDERS->onLoadFinished",
                            "LOADER_RECENT_FOLDERS.onLoadFinished");
                    if (data != null && data.getCount() <= 1 && !mIsTablet) {
                        final class PopulateDefault extends AsyncTask<Uri, Void, Void> {
                            @Override
                            protected Void doInBackground(Uri... uri) {
                                // Asking for an update on the URI and ignore the result.
                                final ContentResolver resolver = mContext.getContentResolver();
                                resolver.update(uri[0], null, null, null);
                                return null;
                            }
                        }
                        final Uri uri = mAccount.defaultRecentFolderListUri;
                        LogUtils.v(LOG_TAG, "Default recents at %s", uri);
                        new PopulateDefault().execute(uri);
                        break;
                    }
                    LogUtils.v(LOG_TAG, "Reading recent folders from the cursor.");
                    mRecentFolderList.loadFromUiProvider(data);
                    if (isAnimating()) {
                        mRecentsDataUpdated = true;
                    } else {
                        mRecentFolderObservers.notifyChanged();
                    }
                    break;
                case LOADER_ACCOUNT_INBOX:
                    LogUtils.d(LOG_TAG,"LOADER_ACCOUNT_INBOX->onLoadFinished",
                            "LOADER_ACCOUNT_INBOX.onLoadFinished");
                    if (data != null && !data.isClosed() && data.moveToFirst()) {
                        final Folder inbox = data.getModel();
                        onFolderChanged(inbox, false /* force */);
                        // Just want to get the inbox, don't care about updates to it
                        // as this will be tracked by the folder change listener.
                        mActivity.getLoaderManager().destroyLoader(LOADER_ACCOUNT_INBOX);
                    } else {
                        LogUtils.d(LOG_TAG, "Unable to get the account inbox for account %s",
                                mAccount != null ? mAccount.getEmailAddress() : "");
                    }
                    break;
                case LOADER_SEARCH:
                    LogUtils.d(LOG_TAG,"LOADER_SEARCH->onLoadFinished",
                            "LOADER_SEARCH.onLoadFinished");
                    if (data != null && data.getCount() > 0) {
                        data.moveToFirst();
                        final Folder search = data.getModel();
                        /** TCT: initialize the listcontext in advance for remote search 's highligh
                         t snippet @ { */
                        mConvListContext = ConversationListContext.forSearchQuery(mAccount, search,
                                mConvListContext.searchQuery);
                        mConvListContext.setSearchField(mActivity.getIntent()
                                .getStringExtra(SearchParams.BUNDLE_QUERY_FIELD));
                        if (!ConversationListContext.isSearchResult(mConvListContext)) {
                            // Save the error status to log for analysis.
                            LogUtils.e(LOG_TAG,
                                    "invalid ConversationListContext=%s", mConvListContext);
                        }
                        /** @} */
                        updateFolder(search);
                        // TS: jin.dong 2015-07-01 EMAIL BUGFIX-1019473 MOD_S
//                        mConvListContext = ConversationListContext.forSearchQuery(mAccount, mFolder,
//                                mActivity.getIntent()
//                                        .getStringExtra(UIProvider.SearchQueryParameters.QUERY));
                        // TS: jin.dong 2015-07-01 EMAIL BUGFIX-1019473 MOD_E
                        showConversationList(mConvListContext);
                        mActivity.invalidateOptionsMenu();
                        mHaveSearchResults = search.totalCount > 0;
                        mActivity.getLoaderManager().destroyLoader(LOADER_SEARCH);
                    } else {
                        LogUtils.e(LOG_TAG, "Null/empty cursor returned by LOADER_SEARCH loader");
                    }
                    break;
                case LOADER_FIRST_FOLDER:
                    LogUtils.d(LOG_TAG,"LOADER_FIRST_FOLDER->onLoadFinished",
                            "LOADER_FIRST_FOLDER.onLoadFinished");
                    if (data == null || data.getCount() <=0 || !data.moveToFirst()) {
                        return;
                    }
                    final Folder folder = data.getModel();
                    boolean handled = false;
                    if (folder != null) {
                        onFolderChanged(folder, false /* force */);
                        handled = true;
                    }
                    if (mConversationToShow != null) {
                        // Open the conversation.
                        showConversation(mConversationToShow);
                        handled = true;
                    }
                    if (!handled) {
                        // We have an account, but nothing else: load the default inbox.
                        loadAccountInbox();
                    }
                    mConversationToShow = null;
                    // And don't run this anymore.
                    mActivity.getLoaderManager().destroyLoader(LOADER_FIRST_FOLDER);
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<ObjectCursor<Folder>> loader) {
        }
    }

    /**
     * Class to perform {@link LoaderManager.LoaderCallbacks} for creating {@link Account} objects.
     */
    private class AccountLoads implements LoaderManager.LoaderCallbacks<ObjectCursor<Account>> {
        final String[] mProjection = UIProvider.ACCOUNTS_PROJECTION;
        final CursorCreator<Account> mFactory = Account.FACTORY;

        @Override
        public Loader<ObjectCursor<Account>> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_ACCOUNT_CURSOR:
                    LogUtils.d(LOG_TAG,  "LOADER_ACCOUNT_CURSOR created");
                    return new ObjectCursorLoader<Account>(mContext,
                            MailAppProvider.getAccountsUri(), mProjection, mFactory);
                case LOADER_ACCOUNT_UPDATE_CURSOR:
                    LogUtils.d(LOG_TAG,  "LOADER_ACCOUNT_UPDATE_CURSOR created");
                    return new ObjectCursorLoader<Account>(mContext, mAccount.uri, mProjection,
                            mFactory);
                default:
                    LogUtils.wtf(LOG_TAG, "Got an id  (%d) that I cannot create!", id);
                    break;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<ObjectCursor<Account>> loader,
                ObjectCursor<Account> data) {
            if (data == null) {
                LogUtils.e(LOG_TAG, "Received null cursor from loader id: %d", loader.getId());
            }
            switch (loader.getId()) {
                case LOADER_ACCOUNT_CURSOR:
                    LogUtils.d(LOG_TAG,"LOADER_ACCOUNT_CURSOR->onLoadFinished",
                            "LOADER_ACCOUNT_CURSOR.onLoadFinished");
                    // We have received an update on the list of accounts.
                    if (data == null) {
                        // Nothing useful to do if we have no valid data.
                        break;
                    }
                    final long count = data.getCount();
                    if (count == 0) {
                        /** TCT: Once the Activity is finishing, we should stop to setup Account @{
                         */
                        if (mActivity.isFinishing()) {
                            LogUtils.d(LOG_TAG, "onLoadFinished finished just skip");
                            break;
                        }
                        /** @} */
                        // If an empty cursor is returned, the MailAppProvider is indicating that
                        // no accounts have been specified.  We want to navigate to the
                        // "add account" activity that will handle the intent returned by the
                        // MailAppProvider

                        // If the MailAppProvider believes that all accounts have been loaded,
                        // and the account list is still empty, we want to prompt the user to add
                        // an account.
                        final Bundle extras = data.getExtras();
                        final boolean accountsLoaded =
                                extras.getInt(AccountCursorExtraKeys.ACCOUNTS_LOADED) != 0;
                        if (accountsLoaded) {
                            final Intent noAccountIntent = MailAppProvider.getNoAccountIntent
                                    (mContext);
                            // TS: zhonghua.tuo 2015-03-18 EMAIL BUGFIX-948923 MOD_S
                            if (noAccountIntent != null && mFirstLoadAccount) {
                                mActivity.startActivityForResult(noAccountIntent,
                                        ADD_ACCOUNT_REQUEST_CODE);
                                //when first launch email,this will start twice,this field to avoid it
                                mFirstLoadAccount = false;
                                // TS: zhaotianyong 2014-11-26 EMAIL BUGFIX-844194 ADD_S
                                // TS: jin.dong 2015-6-9 EMAIL BUGFIX-1013807 MOD_S
                                mActivity.getLoaderManager().destroyLoader(LOADER_ACCOUNT_CURSOR);
                                // TS: jin.dong 2015-6-9 EMAIL BUGFIX-1013807 MOD_e
                                // TS: zhaotianyong 2014-11-26 EMAIL BUGFIX-844194 ADD_E
                                // TS: zhonghua.tuo 2015-03-18 EMAIL BUGFIX-948923 MOD_E
                            }
                        }
                    } else {
                        final boolean accountListUpdated = accountsUpdated(data);
                        if (!mHaveAccountList || accountListUpdated) {
                            mHaveAccountList = updateAccounts(data);
                        }
                        Analytics.getInstance().setCustomDimension(Analytics.CD_INDEX_ACCOUNT_COUNT,
                                Long.toString(count));
                    }
                    break;
                case LOADER_ACCOUNT_UPDATE_CURSOR:
                    LogUtils.d(LOG_TAG,"LOADER_ACCOUNT_UPDATE_CURSOR->onLoadFinished",
                            "LOADER_ACCOUNT_UPDATE_CURSOR.onLoadFinished");
                    // We have received an update for current account.
                    if (data != null && data.moveToFirst()) {
                        final Account updatedAccount = data.getModel();
                        // Make sure that this is an update for the current account
                        if (updatedAccount.uri.equals(mAccount.uri)) {
                            final Settings previousSettings = mAccount.settings;

                            // Update the controller's reference to the current account
                            mAccount = updatedAccount;
                            /**
                             * TCT: Once the folder sync was later than the
                             * loadAccountInbox() first call, the inbox uri maybe
                             * change, in this case if there is no folder, we should
                             * load the account inbox again, otherwise just do
                             * nothing.
                             *
                             * @{
                             */
                            if ((mFolder == null)
                                    && !previousSettings.defaultInbox
                                    .equals(mAccount.settings.defaultInbox)) {
                                loadAccountInbox();
                            }
                            /** @} */
                            LogUtils.d(LOG_TAG, "AbstractActivityController.onLoadFinished(): "
                                    + "mAccount = %s", mAccount.uri);

                            // Only notify about a settings change if something differs
                            if (!Objects.equal(mAccount.settings, previousSettings)) {
                                mAccountObservers.notifyChanged();
                            }
                            perhapsEnterWaitMode();
                            perhapsStartWelcomeTour();
                        } else {
                            LogUtils.e(LOG_TAG, "Got update for account: %s with current account:"
                                    + " %s", updatedAccount.uri, mAccount.uri);
                            // We need to restart the loader, so the correct account information
                            // will be returned.
                            restartOptionalLoader(LOADER_ACCOUNT_UPDATE_CURSOR, this, Bundle.EMPTY);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<ObjectCursor<Account>> loader) {
            // Do nothing. In onLoadFinished() we copy the relevant data from the cursor.
        }
    }

    /**
     * Loads the preference that tells whether the welcome tour should be displayed,
     * and calls the callback with this value.
     * For this to function, the account must have been synced.
     */
    private void perhapsStartWelcomeTour() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (mActivity.wasLatestWelcomeTourShownOnDeviceForAllAccounts()) {
                    // No need to go through the WelcomeStateLoader machinery.
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    if (mAccount != null && mAccount.isAccountReady()) {
                        LoaderManager.LoaderCallbacks<?> welcomeLoaderCallbacks =
                                mActivity.getWelcomeCallbacks();
                        if (welcomeLoaderCallbacks != null) {
                            // The callback is responsible for showing the tour when appropriate.
                            mActivity.getLoaderManager().initLoader(LOADER_WELCOME_TOUR_ACCOUNTS,
                                    Bundle.EMPTY, welcomeLoaderCallbacks);
                        }
                    }
                }
            }
        }.execute();
    }

    /**
     * Updates controller state based on search results and shows first conversation if required.
     */
    private void perhapsShowFirstSearchResult() {
        if (mCurrentConversation == null) {
            // Shown for search results in two-pane mode only.
            mHaveSearchResults = Intent.ACTION_SEARCH.equals(mActivity.getIntent().getAction())
                    && mConversationListCursor.getCount() > 0;
            if (!shouldShowFirstConversation()) {
                return;
            }
            mConversationListCursor.moveToPosition(0);
            final Conversation conv = new Conversation(mConversationListCursor);
            conv.position = 0;
            onConversationSelected(conv, true /* checkSafeToModifyFragments */);
        }
    }

    /**
     * Destroy the pending {@link DestructiveAction} till now and assign the given action as the
     * next destructive action..
     * @param nextAction the next destructive action to be performed. This can be null.
     */
    private void destroyPending(DestructiveAction nextAction) {
        // If there is a pending action, perform that first.
        if (mPendingDestruction != null) {
            mPendingDestruction.performAction();
        }
        mPendingDestruction = nextAction;
    }

    /**
     * Register a destructive action with the controller. This performs the previous destructive
     * action as a side effect. This method is final because we don't want the child classes to
     * embellish this method any more.
     * @param action the action to register.
     */
    private void registerDestructiveAction(DestructiveAction action) {
        // TODO(viki): This is not a good idea. The best solution is for clients to request a
        // destructive action from the controller and for the controller to own the action. This is
        // a half-way solution while refactoring DestructiveAction.
        destroyPending(action);
    }
    //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S

    /**
     * start a query to sort emails by order
     * @param order
     */
    public void sort(int order){
        final Bundle args = new Bundle(4);
        args.putParcelable(BUNDLE_ACCOUNT_KEY, mAccount);
        args.putParcelable(BUNDLE_FOLDER_KEY, mFolder);
        args.putBoolean(BUNDLE_IGNORE_INITIAL_CONVERSATION_LIMIT_KEY,
                mIgnoreInitialConversationLimit);
        args.putInt(BUNDLE_CONVERSATION_ORDER_KEY, order);
        mIgnoreInitialConversationLimit = false;
        mActivity.getLoaderManager().restartLoader(LOADER_CONVERSATION_LIST, args,
                mListCursorCallbacks);
    }
    //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E

    @Override
    public final DestructiveAction getBatchAction(int action, UndoCallback undoCallback) {
        final DestructiveAction da = new ConversationAction(action, mSelectedSet.values(), true);
        da.setUndoCallback(undoCallback);
        registerDestructiveAction(da);
        return da;
    }

    @Override
    public final DestructiveAction getDeferredBatchAction(int action, UndoCallback undoCallback) {
        return getDeferredAction(action, mSelectedSet.values(), true, undoCallback);
    }

    /**
     * Get a destructive action for a menu action. This is a temporary method,
     * to control the profusion of {@link DestructiveAction} classes that are
     * created. Please do not copy this paradigm.
     * @param action the resource ID of the menu action: R.id.delete, for
     *            example
     * @param target the conversations to act upon.
     * @return a {@link DestructiveAction} that performs the specified action.
     */
    private DestructiveAction getDeferredAction(int action, Collection<Conversation> target,
            boolean batch, UndoCallback callback) {
        ConversationAction cAction = new ConversationAction(action, target, batch);
        cAction.setUndoCallback(callback);
        return cAction;
    }

    /**
     * Class to change the folders that are assigned to a set of conversations. This is destructive
     * because the user can remove the current folder from the conversation, in which case it has
     * to be animated away from the current folder.
     */
    private class FolderDestruction implements DestructiveAction {
        private final Collection<Conversation> mTarget;
        private final ArrayList<FolderOperation> mFolderOps = new ArrayList<FolderOperation>();
        private final boolean mIsDestructive;
        /** Whether this destructive action has already been performed */
        private boolean mCompleted;
        private final boolean mIsSelectedSet;
        private final boolean mShowUndo;
        private final int mAction;
        private final Folder mActionFolder;

        private UndoCallback mUndoCallback;

        /**
         * Create a new folder destruction object to act on the given conversations.
         * @param target conversations to act upon.
         * @param actionFolder the {@link Folder} being acted upon, used for displaying the undo bar
         */
        private FolderDestruction(final Collection<Conversation> target,
                final Collection<FolderOperation> folders, boolean isDestructive, boolean isBatch,
                boolean showUndo, int action, final Folder actionFolder) {
            mTarget = ImmutableList.copyOf(target);
            mFolderOps.addAll(folders);
            mIsDestructive = isDestructive;
            mIsSelectedSet = isBatch;
            mShowUndo = showUndo;
            mAction = action;
            mActionFolder = actionFolder;
        }

        @Override
        public void setUndoCallback(UndoCallback undoCallback) {
            mUndoCallback = undoCallback;
        }

        @Override
        public void performAction() {
            if (isPerformed()) {
                return;
            }
            if (mIsDestructive && mShowUndo) {
                ToastBarOperation undoOp = new ToastBarOperation(mTarget.size(), mAction,
                        ToastBarOperation.UNDO, mIsSelectedSet, mActionFolder);
                onUndoAvailable(undoOp);
            }
            // For each conversation, for each operation, add/ remove the
            // appropriate folders.
            ArrayList<ConversationOperation> ops = new ArrayList<ConversationOperation>();
            ArrayList<Uri> folderUris;
            ArrayList<Boolean> adds;
            for (Conversation target : mTarget) {
                HashMap<Uri, Folder> targetFolders = Folder.hashMapForFolders(target
                        .getRawFolders());
                folderUris = new ArrayList<Uri>();
                adds = new ArrayList<Boolean>();
                if (mIsDestructive) {
                    target.localDeleteOnUpdate = true;
                }
                for (FolderOperation op : mFolderOps) {
                    folderUris.add(op.mFolder.folderUri.fullUri);
                    adds.add(op.mAdd ? Boolean.TRUE : Boolean.FALSE);
                    if (op.mAdd) {
                        targetFolders.put(op.mFolder.folderUri.fullUri, op.mFolder);
                    } else {
                        targetFolders.remove(op.mFolder.folderUri.fullUri);
                    }
                }
                ops.add(mConversationListCursor.getConversationFolderOperation(target,
                        folderUris, adds, targetFolders.values(), mUndoCallback));
            }
            if (mConversationListCursor != null) {
                mConversationListCursor.updateBulkValues(ops);
            }
            refreshConversationList();
            if (mIsSelectedSet) {
                mSelectedSet.clear();
            }
        }

        /**
         * Returns true if this action has been performed, false otherwise.
         *
         */
        private synchronized boolean isPerformed() {
            if (mCompleted) {
                return true;
            }
            mCompleted = true;
            return false;
        }
    }

    public final DestructiveAction getFolderChange(Collection<Conversation> target,
            Collection<FolderOperation> folders, boolean isDestructive, boolean isBatch,
            boolean showUndo, final boolean isMoveTo, final Folder actionFolder,
            UndoCallback undoCallback) {
        final DestructiveAction da = getDeferredFolderChange(target, folders, isDestructive,
                isBatch, showUndo, isMoveTo, actionFolder, undoCallback);
        registerDestructiveAction(da);
        return da;
    }

    public final DestructiveAction getDeferredFolderChange(Collection<Conversation> target,
            Collection<FolderOperation> folders, boolean isDestructive, boolean isBatch,
            boolean showUndo, final boolean isMoveTo, final Folder actionFolder,
            UndoCallback undoCallback) {
        final DestructiveAction fd = new FolderDestruction(target, folders, isDestructive, isBatch,
                showUndo, isMoveTo ? R.id.move_folder : R.id.change_folders, actionFolder);
        fd.setUndoCallback(undoCallback);
        return fd;
    }

    @Override
    public final DestructiveAction getDeferredRemoveFolder(Collection<Conversation> target,
            Folder toRemove, boolean isDestructive, boolean isBatch,
            boolean showUndo, UndoCallback undoCallback) {
        Collection<FolderOperation> folderOps = new ArrayList<FolderOperation>();
        folderOps.add(new FolderOperation(toRemove, false));
        final DestructiveAction da = new FolderDestruction(target, folderOps, isDestructive, isBatch,
                showUndo, R.id.remove_folder, mFolder);
        da.setUndoCallback(undoCallback);
        return da;
    }

    @Override
    public final void refreshConversationList() {
        final ConversationListFragment convList = getConversationListFragment();
        if (convList == null) {
            return;
        }
        convList.requestListRefresh();
    }

    protected final ActionClickedListener getUndoClickedListener(
            final AnimatedAdapter listAdapter) {
        return new ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoNum) {
                if (mAccount.undoUri != null) {
                    // NOTE: We might want undo to return the messages affected, in which case
                    // the resulting cursor might be interesting...
                    // TODO: Use UIProvider.SEQUENCE_QUERY_PARAMETER to indicate the set of
                    // commands to undo
                    if (mConversationListCursor != null) {
                        //TS: chaozhang 2016-01-15 EMAIL BUGFIX_1355979  MOD_S
                        //NOTE: use 50 as critical value is good.
                        if (undoNum >= 50) {
                            LogUtils.d(LOG_TAG, "UNDO operation number Exceed 100,and DO NOT notify UI avoid ANR,and the number is %d ", undoNum);
                            mConversationListCursor.undoWithoutNotify(
                                    mActivity.getActivityContext(), mAccount.undoUri);
                        } else {
                            mConversationListCursor.undo(
                                    mActivity.getActivityContext(), mAccount.undoUri);
                        }
                        //TS: chaozhang 2016-01-15 EMAIL BUGFIX_1355979  MOD_E
                    }

                    if (listAdapter != null) {
                        listAdapter.setUndo(true);
                    }
                }
            }
        };
    }

    /**
     * Shows an error toast in the bottom when a folder was not fetched successfully.
     * @param folder the folder which could not be fetched.
     * @param replaceVisibleToast if true, this should replace any currently visible toast.
     */
    protected final void showErrorToast(final Folder folder, boolean replaceVisibleToast) {

        final ActionClickedListener listener;
        final int actionTextResourceId;
        final int lastSyncResult = folder.lastSyncResult;
        switch (lastSyncResult & 0x0f) {
            case UIProvider.LastSyncResult.CONNECTION_ERROR:
                //TS: wenggangjin 2014-12-31 EMAIL BUGFIX_881447 MOD_S
                // The sync request that caused this failure.
//                final int syncRequest = lastSyncResult >> 4;
                // Show: User explicitly pressed the refresh button and there is no connection
                // Show: The first time the user enters the app and there is no connection
                //       TODO(viki): Implement this.
                // Reference: http://b/7202801
//                final boolean showToast = (syncRequest & UIProvider.SyncStatus.USER_REFRESH) != 0;
                // Don't show: Already in the app; user switches to a synced label
                // Don't show: In a live label and a background sync fails
//                final boolean avoidToast = !showToast && (folder.syncWindow > 0
//                        || (syncRequest & UIProvider.SyncStatus.BACKGROUND_SYNC) != 0);
//                if (avoidToast) {
//                    return;
//                }
                //TS: wenggangjin 2014-12-31 EMAIL BUGFIX_881447 MOD_S
                listener = getRetryClickedListener(folder);
                actionTextResourceId = R.string.retry;
                break;
            case UIProvider.LastSyncResult.AUTH_ERROR:
                listener = getSignInClickedListener();
                actionTextResourceId = R.string.signin;
                break;
            case UIProvider.LastSyncResult.SECURITY_ERROR:
                return; // Currently we do nothing for security errors.
            case UIProvider.LastSyncResult.STORAGE_ERROR:
                listener = getStorageErrorClickedListener();
                actionTextResourceId = R.string.info;
                break;
            case UIProvider.LastSyncResult.INTERNAL_ERROR:
                listener = getInternalErrorClickedListener();
                //TS: yanhua.chen 2015-7-7 EMAIL BUGFIX_1027389 MOD_S
                //Note remove report button when internal error
                //actionTextResourceId = R.string.report;
                actionTextResourceId = R.string.report_empty;
                //TS: yanhua.chen 2015-7-7 EMAIL BUGFIX_1027389 MOD_E
                return;
            default:
                return;
        }
        mToastBar.show(listener,
                Utils.getSyncStatusText(mActivity.getActivityContext(), lastSyncResult),
                actionTextResourceId,
                replaceVisibleToast,
                new ToastBarOperation(1, 0, ToastBarOperation.ERROR, false, folder));
    }

    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    public void showUndoToastBar(int action) {
        onUndoAvailable(new ToastBarOperation(1, action, ToastBarOperation.UNDO, false, null));
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E


    //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_S
    public void showManualRefreshInRoamingIfNeed() {
        final AccountPreferences accountPreferences =
                new AccountPreferences(mContext, mAccount.getEmailAddress());
        boolean isManualSyncWhenRoaming = accountPreferences.getManualSyncWhenRoamingEnabled();
        if (isManualSyncWhenRoaming && com.tct.emailcommon.utility.Utility.isRoaming(mContext)) {
            final ActionClickedListener listener = new ActionClickedListener() {
                @Override
                public void onActionClicked(Context context, int num) {
                    requestFolderRefresh();
                }
            };
            mToastBar.show(listener, mContext.getString(R.string.manual_sync_when_roaming_toast), R.string.manual_sync_when_roaming_refresh, true,
                    new ToastBarOperation(1, 0, ToastBarOperation.INFO, false, null));
        }

    }

    //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_E
    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD_S
    public void showDiscardDraftToast(long msgId) {
        // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 MOD_S
        mSavedDraftMsgId = msgId;
        final ActionClickedListener listener = getDiscardClickedListener(msgId);
        mToastBar.show(listener, mContext.getString(R.string.draft_saved), R.string.discard, true,
                new ToastBarOperation(1, 0, ToastBarOperation.DISCARD, false, null));
        // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 MOD_E
    }

    //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_S
    public void showNeedPermissionToast(int descId){
       final ActionClickedListener listener = new ActionClickedListener() {
           @Override
           public void onActionClicked(Context context,int undoUum) {
               PermissionUtil.gotoSettings(context);
           }
       };
        mToastBar.show(listener,mContext.getString(descId),R.string.permission_grant_go_setting,true,
                new ToastBarOperation(1,0,ToastBarOperation.INFO,false,null));

    }
    //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_E

    private ActionClickedListener getDiscardClickedListener(final long msgId) {
        return new ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoUnm) {
                //when we edit a draft and go back,
                //we will go to next conversation or return to conversation list after we delete a draft
                if (mCurrentConversation != null && mCurrentConversation.id == msgId) {
                    final Collection<Conversation> target = Conversation.listOf(mCurrentConversation);
                    // The user is choosing a new action; commit whatever they had been
                    // doing before. Don't animate if we are launching a new screen.
                    commitDestructiveActions(!doesActionChangeConversationListVisibility(R.id.discard_drafts));
                    final UndoCallback undoCallback = getUndoCallbackForDestructiveActionsWithAutoAdvance(
                            R.id.discard_drafts, mCurrentConversation);
                    delete(0, target, getDeferredAction(R.id.discard_drafts, target, false, undoCallback), false);
                } else {
                    // delete draft directly
                    Uri uri = Uri.parse("content://" + EmailContent.AUTHORITY + "/uimessage/" + msgId);
                    mContext.getContentResolver().delete(uri, null, null);
                }
                mToastBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final ActionClickedListener emptyListener = new ActionClickedListener() {

                            @Override
                            public void onActionClicked(Context context,int undoUum) {

                            }
                        };
                        mToastBar.show(emptyListener, mContext.getString(R.string.draft_discard), R.string.empty_string, true,
                                new ToastBarOperation(1, 0, ToastBarOperation.DISCARDED, false, null));  // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 MOD

                    }
                }, 500);

            }
        };
    }
    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD_E

    // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD_S
    private void showDiscardedToast(){
        final ActionClickedListener emptyListener = new ActionClickedListener() {

            @Override
            public void onActionClicked(Context context,int undoUum) {

            }
        };
        mToastBar.show(emptyListener, mContext.getString(R.string.draft_discard), R.string.empty_string, true,
                new ToastBarOperation(1, 0, ToastBarOperation.DISCARDED, false, null));
    }
    // TS: zheng.zou 2015-09-01 EMAIL BUGFIX-552138 ADD_E

    private ActionClickedListener getRetryClickedListener(final Folder folder) {
        return new ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoUum) {
                final Uri uri = folder.refreshUri;

                if (uri != null) {
                    startAsyncRefreshTask(uri);
                }
            }
        };
    }

    private ActionClickedListener getSignInClickedListener() {
        return new ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoUum) {
                promptUserForAuthentication(mAccount);
            }
        };
    }

    private ActionClickedListener getStorageErrorClickedListener() {
        return new ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoUum) {
                showStorageErrorDialog();
            }
        };
    }

    private void showStorageErrorDialog() {
        DialogFragment fragment = (DialogFragment)
                mFragmentManager.findFragmentByTag(SYNC_ERROR_DIALOG_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = SyncErrorDialogFragment.newInstance();
        }
        fragment.show(mFragmentManager, SYNC_ERROR_DIALOG_FRAGMENT_TAG);
    }

    private ActionClickedListener getInternalErrorClickedListener() {
        return new ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoUum) {
                Utils.sendFeedback(mActivity, mAccount, true /* reportingProblem */);
            }

        };
    }

    @Override
    public void onFooterViewErrorActionClick(Folder folder, int errorStatus) {
        Uri uri = null;
        switch (errorStatus) {
            case UIProvider.LastSyncResult.CONNECTION_ERROR:
                if (folder != null && folder.refreshUri != null) {
                    uri = folder.refreshUri;
                }
                break;
            case UIProvider.LastSyncResult.AUTH_ERROR:
                promptUserForAuthentication(mAccount);
                return;
            case UIProvider.LastSyncResult.SECURITY_ERROR:
                return; // Currently we do nothing for security errors.
            case UIProvider.LastSyncResult.STORAGE_ERROR:
                showStorageErrorDialog();
                return;
            case UIProvider.LastSyncResult.INTERNAL_ERROR:
                Utils.sendFeedback(mActivity, mAccount, true /* reportingProblem */);
                return;
            default:
                return;
        }
        /// TCT: We should retry remote search if it's in remote search context. @{
        if (mConvListContext != null && mConvListContext.isRemoteSearch()) {
            retryRemoteSearch();
        } else if (uri != null) {
            startAsyncRefreshTask(uri);
        }
        /// @}
    }

    /** TCT: add for remote search*/
    @Override
    public void onFooterViewRemoteSearchClick(Folder folder) {

        /** TCT: can not do remote search when network disconnection.@{ */
        if (!Utility.hasConnectivity(mContext)) {

            UiUtilities.showConnectionAlertDialog(mActivity.getFragmentManager());

            return;

        }
        /** @} */
        if (mConvListContext != null && !TextUtils.isEmpty(mConvListContext.searchQuery)) {

            //MailActivity.sRecordOpening = false;
            executeSearch(mConvListContext.searchQuery);
        }
    }
    @Override
    public void onFooterViewLoadMoreClick(Folder folder) {
        if (folder != null && folder.loadMoreUri != null) {
            startAsyncRefreshTask(folder.loadMoreUri);
        }
    }

    private void startAsyncRefreshTask(Uri uri) {
        if (mFolderSyncTask != null) {
            mFolderSyncTask.cancel(true);
        }
        mFolderSyncTask = new AsyncRefreshTask(mActivity.getActivityContext(), uri);
        mFolderSyncTask.execute();
    }

    private void promptUserForAuthentication(Account account) {
        if (account != null && !Utils.isEmpty(account.reauthenticationIntentUri)) {
            final Intent authenticationIntent =
                    new Intent(Intent.ACTION_VIEW, account.reauthenticationIntentUri);
            //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_S
            authenticationIntent.putExtra("AUTHENTICATIONFAILED", true);
            //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_E
            mActivity.startActivityForResult(authenticationIntent, REAUTHENTICATE_REQUEST_CODE);
        }
    }

    @Override
    public void onAccessibilityStateChanged() {
        // Clear the cache of objects.
        ConversationItemViewModel.onAccessibilityUpdated();
        // Re-render the list if it exists.
        final ConversationListFragment frag = getConversationListFragment();
        if (frag != null) {
            AnimatedAdapter adapter = frag.getAnimatedAdapter();
            if (adapter != null) {
                adapter.notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public void makeDialogListener (final int action, final boolean isBatch,
            UndoCallback undoCallback) {
        final Collection<Conversation> target;
        if (isBatch) {
            target = mSelectedSet.values();
        } else {
            LogUtils.d(LOG_TAG, "Will act upon %s", mCurrentConversation);
            target = Conversation.listOf(mCurrentConversation);
        }
        final DestructiveAction destructiveAction = getDeferredAction(action, target, isBatch,
                undoCallback);
        mDialogAction = action;
        mDialogFromSelectedSet = isBatch;
        mDialogListener = new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(action, target, destructiveAction, isBatch);
                // Afterwards, let's remove references to the listener and the action.
                setListener(null, -1);
            }
        };
    }

    @Override
    public AlertDialog.OnClickListener getListener() {
        return mDialogListener;
    }

    /**
     * Sets the listener for the positive action on a confirmation dialog.  Since only a single
     * confirmation dialog can be shown, this overwrites the previous listener.  It is safe to
     * unset the listener; in which case action should be set to -1.
     * @param listener the listener that will perform the task for this dialog's positive action.
     * @param action the action that created this dialog.
     */
    private void setListener(AlertDialog.OnClickListener listener, final int action){
        mDialogListener = listener;
        mDialogAction = action;
    }

    @Override
    public VeiledAddressMatcher getVeiledAddressMatcher() {
        return mVeiledMatcher;
    }

    @Override
    public void setDetachedMode() {
        // Tell the conversation list not to select anything.
        final ConversationListFragment frag = getConversationListFragment();
        if (frag != null) {
            frag.setChoiceNone();
        } else if (mIsTablet) {
            // How did we ever land here? Detached mode, and no CLF on tablet???
            LogUtils.e(LOG_TAG, "AAC.setDetachedMode(): CLF = null!");
        }
        mDetachedConvUri = mCurrentConversation.uri;
    }

    private void clearDetachedMode() {
        // Tell the conversation list to go back to its usual selection behavior.
        final ConversationListFragment frag = getConversationListFragment();
        if (frag != null) {
            frag.revertChoiceMode();
        } else if (mIsTablet) {
            // How did we ever land here? Detached mode, and no CLF on tablet???
            LogUtils.e(LOG_TAG, "AAC.clearDetachedMode(): CLF = null on tablet!");
        }
        mDetachedConvUri = null;
    }

    @Override
    public DrawerController getDrawerController() {
        return mDrawerListener;
    }

    private class MailDrawerListener extends Observable<DrawerLayout.DrawerListener>
            implements DrawerLayout.DrawerListener, DrawerController {
        private int mDrawerState;
        private float mOldSlideOffset;

        public MailDrawerListener() {
            mDrawerState = DrawerLayout.STATE_IDLE;
            mOldSlideOffset = 0.f;
        }

        @Override
        public boolean isDrawerEnabled() {
            return AbstractActivityController.this.isDrawerEnabled();
        }

        @Override
        public void registerDrawerListener(DrawerLayout.DrawerListener l) {
            registerObserver(l);
        }

        @Override
        public void unregisterDrawerListener(DrawerLayout.DrawerListener l) {
            unregisterObserver(l);
        }

        @Override
        public boolean isDrawerOpen() {
            return isDrawerEnabled() && mDrawerContainer.isDrawerOpen(mDrawerPullout);
        }

        @Override
        public boolean isDrawerVisible() {
            return isDrawerEnabled() && mDrawerContainer.isDrawerVisible(mDrawerPullout);
        }

        @Override
        public void toggleDrawerState() {
            AbstractActivityController.this.toggleDrawerState();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);

            for (DrawerLayout.DrawerListener l : mObservers) {
                l.onDrawerOpened(drawerView);
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
            if (mHasNewAccountOrFolder) {
                refreshDrawer();
            }

            // When closed, we want to use either the burger, or up, based on where we are
            final int mode = mViewMode.getMode();
            final boolean isTopLevel = Folder.isRoot(mFolder);
            mDrawerToggle.setDrawerIndicatorEnabled(getShouldShowDrawerIndicator(mode, isTopLevel));

            for (DrawerLayout.DrawerListener l : mObservers) {
                l.onDrawerClosed(drawerView);
            }
        }

        /**
         * As part of the overriden function, it will animate the alpha of the conversation list
         * view along with the drawer sliding when we're in the process of switching accounts or
         * folders. Note, this is the same amount of work done as {@link ValueAnimator#ofFloat}.
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
            if (mHasNewAccountOrFolder && mListViewForAnimating != null) {
                mListViewForAnimating.setAlpha(slideOffset);
            }

            // This code handles when to change the visibility of action items
            // based on drawer state. The basic logic is that right when we
            // open the drawer, we hide the action items. We show the action items
            // when the drawer closes. However, due to the animation of the drawer closing,
            // to make the reshowing of the action items feel right, we make the items visible
            // slightly sooner.
            //
            // However, to make the animating behavior work properly, we have to know whether
            // we're animating open or closed. Only if we're animating closed do we want to
            // show the action items early. We save the last slide offset so that we can compare
            // the current slide offset to it to determine if we're opening or closing.
            if (mDrawerState == DrawerLayout.STATE_SETTLING) {
                if (mHideMenuItems && slideOffset < 0.15f && mOldSlideOffset > slideOffset) {
                    mHideMenuItems = false;
                    mActivity.supportInvalidateOptionsMenu();
                    maybeEnableCabMode();
                } else if (!mHideMenuItems && slideOffset > 0.f && mOldSlideOffset < slideOffset) {
                    mHideMenuItems = true;
                    mActivity.supportInvalidateOptionsMenu();
                    disableCabMode();
                }
            } else {
                if (mHideMenuItems && Float.compare(slideOffset, 0.f) == 0) {
                    mHideMenuItems = false;
                    mActivity.supportInvalidateOptionsMenu();
                    maybeEnableCabMode();
                } else if (!mHideMenuItems && slideOffset > 0.f) {
                    mHideMenuItems = true;
                    mActivity.supportInvalidateOptionsMenu();
                    disableCabMode();
                }
            }

            mOldSlideOffset = slideOffset;

            // If we're sliding, we always want to show the burger
            //TS: jian.xu 2015-1-7 EMAIL BUGFIX-883925 DEL_S
            //mDrawerToggle.setDrawerIndicatorEnabled(true /* enable */);
            //TS: jian.xu 2015-1-7 EMAIL BUGFIX-883925 DEL_E

            for (DrawerLayout.DrawerListener l : mObservers) {
                l.onDrawerSlide(drawerView, slideOffset);
            }
        }

        /**
         * This condition here should only be called when the drawer is stuck in a weird state
         * and doesn't register the onDrawerClosed, but shows up as idle. Make sure to refresh
         * and, more importantly, unlock the drawer when this is the case.
         */
        @Override
        public void onDrawerStateChanged(int newState) {
            LogUtils.d(LOG_TAG, "AAC onDrawerStateChanged %d", newState);
            mDrawerState = newState;
            mDrawerToggle.onDrawerStateChanged(mDrawerState);

            for (DrawerLayout.DrawerListener l : mObservers) {
                l.onDrawerStateChanged(newState);
            }

            if (mViewMode.isSearchMode()) {
                return;
            }
            if (mDrawerState == DrawerLayout.STATE_IDLE) {
                if (mHasNewAccountOrFolder) {
                    refreshDrawer();
                }
                if (mConversationListLoadFinishedIgnored) {
                    mConversationListLoadFinishedIgnored = false;
                    final Bundle args = new Bundle();
                    args.putParcelable(BUNDLE_ACCOUNT_KEY, mAccount);
                    args.putParcelable(BUNDLE_FOLDER_KEY, mFolder);
                    mActivity.getLoaderManager().initLoader(
                            LOADER_CONVERSATION_LIST, args, mListCursorCallbacks);
                }
            }
        }

        /**
         * If we've reached a stable drawer state, unlock the drawer for usage, clear the
         * conversation list, and finish end actions. Also, make
         * {@link #mHasNewAccountOrFolder} false to reflect we're done changing.
         */
        public void refreshDrawer() {
            mHasNewAccountOrFolder = false;
            mDrawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ConversationListFragment conversationList = getConversationListFragment();
            if (conversationList != null) {
                conversationList.clear();
            }
            mFolderOrAccountObservers.notifyChanged();
        }

        /**
         * Returns the most recent update of the {@link DrawerLayout}'s state provided
         * by {@link #onDrawerStateChanged(int)}.
         * @return The {@link DrawerLayout}'s current state. One of
         * {@link DrawerLayout#STATE_DRAGGING}, {@link DrawerLayout#STATE_IDLE},
         * or {@link DrawerLayout#STATE_SETTLING}.
         */
        public int getDrawerState() {
            return mDrawerState;
        }
    }

    @Override
    public boolean isDrawerPullEnabled() {
        return true;
    }

    @Override
    public boolean shouldHideMenuItems() {
        return mHideMenuItems;
    }

    protected void navigateUpFolderHierarchy() {
        new AsyncTask<Void, Void, Folder>() {
            @Override
            protected Folder doInBackground(final Void... params) {
                if (mInbox == null) {
                    // We don't have an inbox, but we need it
                    final Cursor cursor = mContext.getContentResolver().query(
                            mAccount.settings.defaultInbox, UIProvider.FOLDERS_PROJECTION, null,
                            null, null);

                    if (cursor != null) {
                        try {
                            if (cursor.moveToFirst()) {
                                mInbox = new Folder(cursor);
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                }

                // Now try to load our parent
                final Folder folder;

                if (mFolder != null) {
                    Cursor cursor = null;
                    try {
                        cursor = mContext.getContentResolver().query(mFolder.parent,
                                UIProvider.FOLDERS_PROJECTION, null, null, null);

                        if (cursor == null || !cursor.moveToFirst()) {
                            // We couldn't load the parent, so use the inbox
                            folder = mInbox;
                        } else {
                            folder = new Folder(cursor);
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                } else {
                    folder = mInbox;
                }

                return folder;
            }

            @Override
            protected void onPostExecute(final Folder result) {
                onFolderSelected(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    @Override
    public Parcelable getConversationListScrollPosition(final String folderUri) {
        return mConversationListScrollPositions.getParcelable(folderUri);
    }

    @Override
    public void setConversationListScrollPosition(final String folderUri,
            final Parcelable savedPosition) {
        mConversationListScrollPositions.putParcelable(folderUri, savedPosition);
    }

    @Override
    public View.OnClickListener getNavigationViewClickListener() {
        return mHomeButtonListener;
    }

    // TODO: Fold this into the outer class when b/16627877 is fixed
    private class HomeButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onUpPressed();
        }
    }

    /**
     * TCT: use to update footer view loading status.
     */
    private void updateFooterStatus(boolean isStarted) {
        ConversationListFragment listFragment = getConversationListFragment();
        if (listFragment != null) {
            listFragment.updateFooterStatus(isStarted);
        }
    }

    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD_S
    private class DraftSaveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && ComposeActivity.DRAFT_SAVED_ACTION.equals(intent.getAction())) {
                long msgId = intent.getLongExtra(BaseColumns._ID, -1);
                if (msgId != -1) {
                    showDiscardDraftToast(msgId);
                    //the event is handled, abort here
                    abortBroadcast();
                }
            }
        }
    }
    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD_E

    /*
     * Show the toolbar and fab button
     */
    @Override
    public void animateShow(ImageButton fabButton) {
        Toolbar toolbar = mActivity.getToolbar();
        if (toolbar == null) {
            return;
        }
      //Remove other animation.
      if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
        hideAnimatorSet.cancel();
      }
      if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
        //If the animation is running,do nothing.
      } else {
          if (!mToolbarHidden) {
              //toolbar is showing,no need to do the show animation.
              return;
          }
        backAnimatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        //Show the toolbar
        ObjectAnimator headerAnimator = ObjectAnimator.ofFloat(toolbar, "translationY", toolbar.getTranslationY(), 0f);
        if(fabButton != null) {
          //Show the fabButton
            ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(fabButton, "translationY", fabButton.getTranslationY(), 0f);
            animators.add(fabAnimator);
        }
        if(mSearchHeader != null) {
          //Show search header view
          //If we are searching the message, we should also show the search header
            ObjectAnimator searchHeaderAnimator = ObjectAnimator.ofFloat(mSearchHeader, "translationY", mSearchHeader.getTranslationY(), 0f);
            animators.add(searchHeaderAnimator);
        }
        animators.add(headerAnimator);
        backAnimatorSet.setDuration(200);
        backAnimatorSet.playTogether(animators);
        backAnimatorSet.start();
        mToolbarHidden = false;
      }
    }

  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
    /*
     * Hide the toolbar and the fab button
     */
    @Override
    public void animateHide(ImageButton fabButton) {
        Toolbar toolbar = mActivity.getToolbar();
        if (toolbar == null) {
            return;
        }
      //Remove other animations first
      if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
        backAnimatorSet.cancel();
      }
      if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
        //If the animation is running, do nothing.
      } else {
          if (mToolbarHidden) {
              //toolbar is hidden,no need to do the hide animation
              return;
          }
        hideAnimatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        //Hide the toolbar
        ObjectAnimator headerAnimator = ObjectAnimator.ofFloat(toolbar, "translationY", toolbar.getTranslationY(), -toolbar.getHeight());
        //Hide the fabButton
        ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(fabButton, "translationY", fabButton.getTranslationY(),((View)fabButton.getParent()).getHeight()); //TS: zheng.zou 2015-10-22 EMAIL BUGFIX-721230 MOD
        if (mSearchHeader != null) {
            //Hide search header view
            //If we are searching the message, we should also hide the search header
            ObjectAnimator searchHeaderAnimator = ObjectAnimator.ofFloat(mSearchHeader, "translationY", toolbar.getTranslationY(), -mSearchHeader.getHeight());
            animators.add(searchHeaderAnimator);
        }
        animators.add(headerAnimator);
        animators.add(fabAnimator);
        hideAnimatorSet.setDuration(200);
        hideAnimatorSet.playTogether(animators);
        hideAnimatorSet.start();
        mToolbarHidden = true;
      }
    }

  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
    @Override
    public ImageButton getComposeButton() {
        return (ImageButton)mFloatingComposeButton;
    }
  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_E

    public void setSearchHeader (HorizontalScrollView header) {
        mSearchHeader = header;
    }
  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_E
  //TS: tao.gan 2015-09-12 EMAIL BUGFIX-1080620 ADD_S
    public void backToList(Conversation conversation) {
    }
  //TS: tao.gan 2015-09-12 EMAIL BUGFIX-1080620 ADD_E

}
