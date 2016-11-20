/*
 * Copyright (C) 2012 Google Inc.
 * Licensed to The Android Open Source Project.
 * Copyright 2013 TCL Communication Technology Holdings Limited.
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
/* 04/25/2014|     Chao Zhang       |      FR 631895 	   |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag             Date        Author              Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin	Modify the package conflict
 *BUGFIX-845093 2014/11/20 wenggangjin [Android5.0][Email] Back hard key does not work after viewing a mail
 *BUGFIX-859985  2014/12/18   junwei-xu       [Android5.0][Email][UI]The Email UI display abnormal after tapping Star icon
 *BUGFIX-887553  2014/12/30   xiaolin.li      [Email]Quick horizontal sliding flash back in the mail details interface
 *BUGFIX-882241  2015/01/03   wenggangjin     [Android5.0][Email][REG]Embedded picture covers mail content after rotating screen
 *BUGFIX-906070   2015-01-20  wenggangjin     [Email]Do not add or remove star in Email detail screen successfully
 *BUGFIX-917007  2015-01-29   wenggangjin     [SMC]com.tct.email happend wtf due android.util.Log$TerribleFailure
 *BUGFIX_915771  2015-02-02   gengkexue       [Android5.0][Email]The save icon of attachments will move in combined view
 *BUGFIX-921154  2015-02-06   wenggangjin     [Android5.0][Email]The star icon in Trash folder is not reasonable
 *BUGFIX-932165  2015/03/13   zhaotianyong    [5.0][Email] some email body font is too small to recognize
 *BUGFIX-919767  2015/3/25    junwei-xu       [Android5.0][Email] [UI] Status bar does not change when selecting characters in mail content
 *BUGFIX-940964  2015/4/20    gangjin.weng    [Email] Set Dwonload Head Only by default
 *BUGFIX-997081  2015/05/15   junwei-xu       [HOMO][ALWE] Starring emails is not always working
 *BUGFIX-993643  2015/05/19   wenggangjing    [Android5.0][Email]Loading content is so slowly when set download option as header only.
 *BUGFIX-1005432  2015/05/26   zhangchao      [Monitor][Android5.0][Email]All Email account disappear sometimes
 *BUGFIX-998526  2015/06/02   Gantao           [Email]Email attachment will overlap the email body during downloading remaining
 *BUGFIX-1010521 2015/6/6     yanhua.chen      [REG][Email]The star state have changed after rotate the screen
 *BUGFIX-958223  2015/07/07   junwei-xu       [Android5.0][Email] Star icon disappear after lock/unlock screen
 *BUGFIX-1041711  2015/07/20   Gantao           [Android5.0][Email]Screen flash when remaining content load out with POP account
 *BUGFIX-1043844  2015/07/20   Gantao         [Android5.0][Email] Mail content is cut off by attachment icon in draft
 *BUGFIX-1046583  2015/07/21   chaozhang      [jrdlogger]com.tct.email JE
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
 *BUGFIX-546917  2015/9/19    zheng.zou       fix bug : star will be abnormal when clicked right after enter one mail message
 *BUGFIX_667469 2015/09/25    zheng.zou       check multi user for upgrade to M
 *BUGFIX-707702 2015/10/09    jian.xu         [Android L][Email][Monkey][Crash]java.lang.NullPointerException during monkey test
 *BUGFIX_712361 2015/10/10    lin-zhou        [Android L][Email][Monkey][Crash]java.lang.NullPointerException during monkey test
 *BUGFIX-1121860 2015/12/18   chaozhang       [stability]Crash in email.
 *BUGFIX-1275319 2015/01/27   jian.xu         [Android 6.0][Email][Force close][Monitor]java.lang.IllegalStateException: Recursive entry to executePendingTransactions happened
 *BUGFIX-1693948  2015/12/18   jin.dong        [Email][ANR]Email ANR when slide in mail list
 *BUGFIX-1838565  2016/03/17   tianjing.su    [jrdlogger]com.tct.email Java (JE)
 *BUGFIX-1892015  2016/04/1   xing.zhao    [jrdlogger]com.tct.email Java (JE)
 *BUGFIX-1958170  2016/04/18   kaifeng.lu    [Stability][Email][FC]The email force close when do email stability test
===========================================================================
 */
package com.tct.mail.ui;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Picture;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Environment;
import android.support.v4.text.BidiFormatter;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.tct.emailcommon.mail.Address;
import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.email.service.EmailServiceUtils;
import com.tct.email.service.ImapService;
import com.tct.email.service.Pop3Service;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
import com.tct.fw.google.common.collect.ImmutableList;
import com.tct.fw.google.common.collect.Lists;
import com.tct.fw.google.common.collect.Maps;
import com.tct.fw.google.common.collect.Sets;
import com.tct.mail.FormattedDateBuilder;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.analytics.AnalyticsTimer;
import com.tct.mail.browse.ConversationContainer;
import com.tct.mail.browse.ConversationMessage;
import com.tct.mail.browse.ConversationOverlayItem;
import com.tct.mail.browse.ConversationReplyFabView;
import com.tct.mail.browse.ConversationViewAdapter;
import com.tct.mail.browse.ConversationViewHeader;
import com.tct.mail.browse.ConversationWebView;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilderCreator;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilderCreatorHolder;
import com.tct.mail.browse.MessageCursor;
import com.tct.mail.browse.MessageFooterView;
import com.tct.mail.browse.MessageHeaderView;
import com.tct.mail.browse.ScrollIndicatorsView;
import com.tct.mail.browse.SuperCollapsedBlock;
import com.tct.mail.browse.WebViewContextMenu;
import com.tct.mail.browse.ConversationContainer.OverlayPosition;
import com.tct.mail.browse.ConversationFooterView.ConversationFooterCallbacks;
import com.tct.mail.browse.ConversationViewAdapter.ConversationFooterItem;
import com.tct.mail.browse.ConversationViewAdapter.MessageFooterItem;
import com.tct.mail.browse.ConversationViewAdapter.MessageHeaderItem;
import com.tct.mail.browse.ConversationViewAdapter.SuperCollapsedBlockItem;
import com.tct.mail.browse.MailWebView.ContentSizeChangeListener;
import com.tct.mail.content.ObjectCursor;
import com.tct.mail.print.PrintUtils;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.ui.ConversationViewState.ExpansionState;
import com.tct.mail.utils.ConversationViewUtils;
import com.tct.mail.utils.Utils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.Mailbox;
//[FEATURE]-Add-END by TSCD.chao zhang
import com.tct.emailcommon.service.EmailServiceProxy;

/**
 * The conversation view UI component.
 */
public class ConversationViewFragment extends AbstractConversationViewFragment implements
        SuperCollapsedBlock.OnClickListener, OnLayoutChangeListener,
        MessageHeaderView.MessageHeaderViewCallbacks, MessageFooterView.MessageFooterCallbacks,
        WebViewContextMenu.Callbacks, ConversationFooterCallbacks, View.OnKeyListener {

    private static final String LOG_TAG = LogTag.getLogTag();
    public static final String LAYOUT_TAG = "ConvLayout";

    /**
     * Difference in the height of the message header whose details have been expanded/collapsed
     */
    private int mDiff = 0;

    /**
     * Default value for {@link #mLoadWaitReason}. Conversation load will happen immediately.
     */
    private final int LOAD_NOW = 0;
    /**
     * Value for {@link #mLoadWaitReason} that means we are offscreen and waiting for the visible
     * conversation to finish loading before beginning our load.
     * <p>
     * When this value is set, the fragment should register with {@link ConversationListCallbacks}
     * to know when the visible conversation is loaded. When it is unset, it should unregister.
     */
    private final int LOAD_WAIT_FOR_INITIAL_CONVERSATION = 1;
    /**
     * Value for {@link #mLoadWaitReason} used when a conversation is too heavyweight to load at
     * all when not visible (e.g. requires network fetch, or too complex). Conversation load will
     * wait until this fragment is visible.
     */
    private final int LOAD_WAIT_UNTIL_VISIBLE = 2;

    // Keyboard navigation
    private KeyboardNavigationController mNavigationController;
    // Since we manually control navigation for most of the conversation view due to problems
    // with two-pane layout but still rely on the system for SOME navigation, we need to keep track
    // of the view that had focus when KeyEvent.ACTION_DOWN was fired. This is because we only
    // manually change focus on KeyEvent.ACTION_UP (to prevent holding down the DOWN button and
    // lagging the app), however, the view in focus might have changed between ACTION_UP and
    // ACTION_DOWN since the system might have handled the ACTION_DOWN and moved focus.
    private View mOriginalKeyedView;
    private int mMaxScreenHeight;
    private int mTopOfVisibleScreen;

    protected ConversationContainer mConversationContainer;

    protected ConversationWebView mWebView;

    private ViewGroup mTopmostOverlay;

    private ConversationViewProgressController mProgressController;

    private Button mNewMessageBar;

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    private ConversationReplyFabView mFabButton;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

    protected HtmlConversationTemplates mTemplates;

    private final MailJsBridge mJsBridge = new MailJsBridge();

    protected ConversationViewAdapter mAdapter;

    protected boolean mViewsCreated;
    // True if we attempted to render before the views were laid out
    // We will render immediately once layout is done
    private boolean mNeedRender;

    /**
     * Temporary string containing the message bodies of the messages within a super-collapsed
     * block, for one-time use during block expansion. We cannot easily pass the body HTML
     * into JS without problematic escaping, so hold onto it momentarily and signal JS to fetch it
     * using {@link MailJsBridge}.
     */
    private String mTempBodiesHtml;

    private int  mMaxAutoLoadMessages;

    protected int mSideMarginPx;

    /**
     * If this conversation fragment is not visible, and it's inappropriate to load up front,
     * this is the reason we are waiting. This flag should be cleared once it's okay to load
     * the conversation.
     */
    private int mLoadWaitReason = LOAD_NOW;

    private boolean mEnableContentReadySignal;

    private ContentSizeChangeListener mWebViewSizeChangeListener;

    private float mWebViewYPercent;

    /**
     * Has loadData been called on the WebView yet?
     */
    private boolean mWebViewLoadedData;

    private long mWebViewLoadStartMs;
    //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 ADD_S
    //Note: current message's star value in database.
    private boolean mStarInDatabase;
    //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 ADD_E
  //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_S
    private Boolean star = null;
  //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_E
    //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 DEL_S
    /*
  //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_906070 MOD_S
    private Boolean initStar = null;
  //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_906070 MOD_E
    */
    //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 DEL_E
    private boolean isEasCall = false;
    //[BUGFIX]-Add-BEGIN?by?TSCD.zheng.zou,01/14/2015,887972
    //[Email]It?still?display?download?remaining?when?rotate?the?screen?during?loading
    private static final String IS_DOWNLOADING_REMAINING = "is_downloading_remaining";
    private static final int LOADER_DOWNLOAD_REMAINING = 101;
    private static final String EXTRA_MESSAGE = "message";
    private static final long MSG_NONE_ID = -1;
    private boolean mIsDownloadingRemaining;
    private DownloadRemainCallback mDownloadRemainCallback;
    private static MessageHeaderView mMessageHeaderView;
    //[BUGFIX]-Add-END?by?TSCD.zheng.zou
    private final Map<String, String> mMessageTransforms = Maps.newHashMap();

    private final DataSetObserver mLoadedObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            getHandler().post(new FragmentRunnable("delayedConversationLoad",
                    ConversationViewFragment.this) {
                @Override
                public void go() {
                    LogUtils.d(LOG_TAG, "CVF load observer fired, this=%s",
                            ConversationViewFragment.this);
                    handleDelayedConversationLoad();
                }
            });
        }
    };

    private final Runnable mOnProgressDismiss = new FragmentRunnable("onProgressDismiss", this) {
        @Override
        public void go() {
            LogUtils.d(LOG_TAG, "onProgressDismiss go() - isUserVisible() = %b", isUserVisible());
            if (isUserVisible()) {
                onConversationSeen();
            }
            mWebView.onRenderComplete();
        }
    };

    private static final boolean DEBUG_DUMP_CONVERSATION_HTML = false;
    private static final boolean DISABLE_OFFSCREEN_LOADING = false;
    private static final boolean DEBUG_DUMP_CURSOR_CONTENTS = false;

    private static final String BUNDLE_KEY_WEBVIEW_Y_PERCENT =
            ConversationViewFragment.class.getName() + "webview-y-percent";
    // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_S
    private boolean isCheckStar = false;
    // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_E
    private BidiFormatter mBidiFormatter;

    // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
    private boolean webViewScaleHasChanged = false;
    private int mCovHeaderHeight;
    private int mMsgHeaderHeight;
    private int mCovFooterHegiht;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    private int mToolbarHeight;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
    // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E
    // TS: tao.gan 2015-07-20 EMAIL BUGFIX-1041711 ADD_S
    private static String IS_POP_DOWNLOAD_REMAIN = "is_pop_download_remain";
    private boolean mIsPopDownloadRemain = false;
    private boolean hasRenderContent = false;
    // TS: tao.gan 2015-07-20 EMAIL BUGFIX-1041711 ADD_E

    /**
     * Contains a mapping between inline image attachments and their local message id.
     */
    private Map<String, String> mUrlToMessageIdMap;

    /**
     * Constructor needs to be public to handle orientation changes and activity lifecycle events.
     */
    public ConversationViewFragment() {}

    /**
     * Creates a new instance of {@link ConversationViewFragment}, initialized
     * to display a conversation with other parameters inherited/copied from an existing bundle,
     * typically one created using {@link #makeBasicArgs}.
     */
    public static ConversationViewFragment newInstance(Bundle existingArgs,
            Conversation conversation) {
        ConversationViewFragment f = new ConversationViewFragment();
        Bundle args = new Bundle(existingArgs);
        args.putParcelable(ARG_CONVERSATION, conversation);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAccountChanged(Account newAccount, Account oldAccount) {
        // if overview mode has changed, re-render completely (no need to also update headers)
        if (isOverviewMode(newAccount) != isOverviewMode(oldAccount)) {
            setupOverviewMode();
            final MessageCursor c = getMessageCursor();
            if (c != null) {
                renderConversation(c);
            } else {
                // Null cursor means this fragment is either waiting to load or in the middle of
                // loading. Either way, a future render will happen anyway, and the new setting
                // will take effect when that happens.
            }
            return;
        }

        // settings may have been updated; refresh views that are known to
        // depend on settings
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogUtils.d(LOG_TAG, "IN CVF.onActivityCreated, this=%s visible=%s", this, isUserVisible());
        super.onActivityCreated(savedInstanceState);

        if (mActivity == null || mActivity.isFinishing()) {
            // Activity is finishing, just bail.
            return;
        }

        Context context = getContext();
        mTemplates = new HtmlConversationTemplates(context);

        final FormattedDateBuilder dateBuilder = new FormattedDateBuilder(context);

        mNavigationController = mActivity.getKeyboardNavigationController();

        mAdapter = new ConversationViewAdapter(mActivity, this,
                getLoaderManager(), this, this, getContactInfoSource(), this, this,
                getListController(), this, mAddressCache, dateBuilder, mBidiFormatter, this);
        mConversationContainer.setOverlayAdapter(mAdapter);

        // set up snap header (the adapter usually does this with the other ones)
        mConversationContainer.getSnapHeader().initialize(
                this, mAddressCache, this, getContactInfoSource(),
                mActivity.getAccountController().getVeiledAddressMatcher());

        final Resources resources = getResources();
        mMaxAutoLoadMessages = resources.getInteger(R.integer.max_auto_load_messages);

        mSideMarginPx = resources.getDimensionPixelOffset(
                R.dimen.conversation_message_content_margin_side);

        mUrlToMessageIdMap = new ArrayMap<String, String>();
        final InlineAttachmentViewIntentBuilderCreator creator =
                InlineAttachmentViewIntentBuilderCreatorHolder.
                getInlineAttachmentViewIntentCreator();
        final WebViewContextMenu contextMenu = new WebViewContextMenu(getActivity(),
                creator.createInlineAttachmentViewIntentBuilder(mAccount,
                mConversation != null ? mConversation.id : -1));
        contextMenu.setCallbacks(this);
        mWebView.setOnCreateContextMenuListener(contextMenu);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
        mWebView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                final int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_UP && webViewScaleHasChanged) {
                    mWebView.loadUrl(String
                            .format("javascript:setConversationHeaderSpacerHeight(%s);",
                                    mCovHeaderHeight
                                            * mWebView.getInitialScale()
                                            / mWebView.getScale()));
                    if (mAdapter.getMessageHeaderItem() != null) {
                        mWebView.loadUrl(String
                                .format("javascript:setMessageHeaderSpacerHeight('%s', %s);",
                                        mTemplates.getMessageDomId(mAdapter
                                                .getMessageHeaderItem()
                                                .getMessage()),
                                        mMsgHeaderHeight
                                                * mWebView.getInitialScale()
                                                / mWebView.getScale()));
                    }
                    mWebView.loadUrl(String
                            .format("javascript:setConversationFooterSpacerHeight(%s);",
                                    mCovFooterHegiht
                                            * mWebView.getInitialScale()
                                            / mWebView.getScale()));
                    webViewScaleHasChanged = false;
                }
                return mWebView.onTouchEvent(event);
            }
        });
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E

      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        mFabButton.setAccountController(this);
        //Because we can't get the webview's contentHeight when onPageFinished(),so set the PictureListener
        //to get the contentHeight and judge if it's initialized bottom,and then do the animation.
        mWebView.setPictureListener(new PictureListener() {
            int previousHeight;
            @Deprecated
            public void onNewPicture(WebView w, Picture picture) {
                // TODO Auto-generated method stub
                int height = w.getContentHeight();
                if (previousHeight == height) return;
                previousHeight = height;
                if(mWebView.isInitializedBottom()) {
                    mWebView.animateBottom(true);
                } else {
                    mWebView.animateHideFooter();
                }
            }
        });
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        // set this up here instead of onCreateView to ensure the latest Account is loaded
        setupOverviewMode();

        // Defer the call to initLoader with a Handler.
        // We want to wait until we know which fragments are present and their final visibility
        // states before going off and doing work. This prevents extraneous loading from occurring
        // as the ViewPager shifts about before the initial position is set.
        //
        // e.g. click on item #10
        // ViewPager.setAdapter() actually first loads #0 and #1 under the assumption that #0 is
        // the initial primary item
        // Then CPC immediately sets the primary item to #10, which tears down #0/#1 and sets up
        // #9/#10/#11.
        getHandler().post(new FragmentRunnable("showConversation", this) {
            @Override
            public void go() {
                showConversation();
            }
        });

        if (mConversation != null && mConversation.conversationBaseUri != null &&
                !Utils.isEmpty(mAccount.accountCookieQueryUri)) {
            // Set the cookie for this base url
            new SetCookieTask(getContext(), mConversation.conversationBaseUri.toString(),
                    mAccount.accountCookieQueryUri).execute();
        }

        // Find the height of the screen for manually scrolling the webview via keyboard.
        final Rect screen = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(screen);
        mMaxScreenHeight = screen.bottom;
        mTopOfVisibleScreen = screen.top + mActivity.getSupportActionBar().getHeight();
        //[BUGFIX]-Add-BEGIN?by?TSCD.zheng.zou,01/14/2015,887972
        //[Email]It?still?display?download?remaining?when?rotate?the?screen?during?loading
        //note:use initLoader to reconnect with the previous loader.
        if (savedInstanceState != null) {
            mIsDownloadingRemaining = savedInstanceState.getBoolean(IS_DOWNLOADING_REMAINING);
            mIsPopDownloadRemain = savedInstanceState.getBoolean(IS_POP_DOWNLOAD_REMAIN);
        }
        LoaderManager lm = getLoaderManager();
        if (lm.getLoader(LOADER_DOWNLOAD_REMAINING) != null) {
            lm.initLoader(LOADER_DOWNLOAD_REMAINING, null, mDownloadRemainCallback);
        }
        //[BUGFIX]-Add-END?by?TSCD.zheng.zou
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 ADD_S
        //Note: initialize star from mConversation when first create this fragment.
        mStarInDatabase = mConversation.starred;
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 ADD_E
        mWebViewClient = createConversationWebViewClient();

        if (savedState != null) {
            mWebViewYPercent = savedState.getFloat(BUNDLE_KEY_WEBVIEW_Y_PERCENT);
          //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_S
            star = savedState.getBoolean("starred");
          //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_E
            mConversation.starred = star;
        }
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 DEL_S
        /*
      //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_906070 MOD_S
        initStar = mConversation.starred;
      //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_906070 MOD_E
        */
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 DEL_E
        mBidiFormatter = BidiFormatter.getInstance();
        mDownloadRemainCallback = new DownloadRemainCallback();
    }

    protected ConversationWebViewClient createConversationWebViewClient() {
        return new ConversationWebViewClient(mAccount);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        //TS: chao-zhang 2015-12-10 EMAIL BUGFIX_1121860 MOD_S 	544
        //NOTE: when infalte conversationView which is implements from WebView,webview.jar is not exist
        //or not found,NameNotFoundException exception thrown,and InflateException thrown in Email,BAD!!!
        View rootView;
        try {
            rootView = inflater.inflate(R.layout.conversation_view, container, false);
        } catch (InflateException e) {
            LogUtils.e(LOG_TAG,e,"InflateException happen during inflate conversationView");
            //TS: xing.zhao 2016-4-1 EMAIL BUGFIX_1892015 MOD_S
            if (getActivity() == null) {
                return null;
            } else {
                rootView = new View(getActivity().getApplicationContext());
                return rootView;
            }
            //TS: xing.zhao 2016-4-1 EMAIL BUGFIX_1892015 MOD_E
        }
        //TS: chao-zhang 2015-12-10 EMAIL BUGFIX_1121860 MOD_E
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        //Here we romve the imagebutton and then add it ,to make it show on the top level.
        //Because we can't add the fab button at last on the layout xml.
        mFabButton = (ConversationReplyFabView) rootView.findViewById(R.id.conversation_view_fab);
        FrameLayout framelayout = (FrameLayout) rootView.findViewById(R.id.conversation_view_framelayout);
        framelayout.removeView(mFabButton);
        framelayout.addView(mFabButton);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        mConversationContainer = (ConversationContainer) rootView
                .findViewById(R.id.conversation_container);
        mConversationContainer.setAccountController(this);

        mTopmostOverlay =
                (ViewGroup) mConversationContainer.findViewById(R.id.conversation_topmost_overlay);
        mTopmostOverlay.setOnKeyListener(this);
        inflateSnapHeader(mTopmostOverlay, inflater);
        mConversationContainer.setupSnapHeader();

        setupNewMessageBar();

        mProgressController = new ConversationViewProgressController(this, getHandler());
        mProgressController.instantiateProgressIndicators(rootView);

        mWebView = (ConversationWebView)
                mConversationContainer.findViewById(R.id.conversation_webview);
      //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_S
        if(mWebView != null){
            mWebView.setActivity(getActivity());
        }
      //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_E
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        mWebView.setFabButton(mFabButton);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

        mWebView.addJavascriptInterface(mJsBridge, "mail");
        // On JB or newer, we use the 'webkitAnimationStart' DOM event to signal load complete
        // Below JB, try to speed up initial render by having the webview do supplemental draws to
        // custom a software canvas.
        // TODO(mindyp):
        //PAGE READINESS SIGNAL FOR JELLYBEAN AND NEWER
        // Notify the app on 'webkitAnimationStart' of a simple dummy element with a simple no-op
        // animation that immediately runs on page load. The app uses this as a signal that the
        // content is loaded and ready to draw, since WebView delays firing this event until the
        // layers are composited and everything is ready to draw.
        // This signal does not seem to be reliable, so just use the old method for now.
        final boolean isJBOrLater = Utils.isRunningJellybeanOrLater();
        final boolean isUserVisible = isUserVisible();
        mWebView.setUseSoftwareLayer(!isJBOrLater);
        mEnableContentReadySignal = isJBOrLater && isUserVisible;
        mWebView.onUserVisibilityChanged(isUserVisible);
        mWebView.setWebViewClient(mWebViewClient);
        final WebChromeClient wcc = new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                  //TS: wenggangjin 2015-01-29 EMAIL BUGFIX_-917007 MOD_S
//                    LogUtils.wtf(LOG_TAG, "JS: %s (%s:%d) f=%s", consoleMessage.message(),
                      LogUtils.w(LOG_TAG, "JS: %s (%s:%d) f=%s", consoleMessage.message(),
                            consoleMessage.sourceId(), consoleMessage.lineNumber(),
                            ConversationViewFragment.this);
                    //TS: wenggangjin 2015-01-29 EMAIL BUGFIX_-917007 MOD_E
                } else {
                    LogUtils.i(LOG_TAG, "JS: %s (%s:%d) f=%s", consoleMessage.message(),
                            consoleMessage.sourceId(), consoleMessage.lineNumber(),
                            ConversationViewFragment.this);
                }
                return true;
            }
        };
        mWebView.setWebChromeClient(wcc);

        final WebSettings settings = mWebView.getSettings();

        final ScrollIndicatorsView scrollIndicators =
                (ScrollIndicatorsView) rootView.findViewById(R.id.scroll_indicators);
        scrollIndicators.setSourceView(mWebView);

        settings.setJavaScriptEnabled(true);

        ConversationViewUtils.setTextZoom(getResources(), settings);
        //Enable third-party cookies. b/16014255
        if (Utils.isRunningLOrLater()) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true /* accept */);
        }

        mViewsCreated = true;
        mWebViewLoadedData = false;

        return rootView;
    }
    //TS: kaifeng.lu 2016-04-18 EMAIL BUGFIX-1958170 MOD_S
    protected void inflateSnapHeader(ViewGroup topmostOverlay, LayoutInflater inflater) {
        try {
            inflater.inflate(R.layout.conversation_topmost_overlay_items, topmostOverlay, true);
        }catch(OutOfMemoryError e){
           LogUtils.i(LOG_TAG,"inflateSnapHeader  OutOfMemoryError");
            System.gc();
        }
    }
    //TS: kaifeng.lu 2016-04-18 EMAIL BUGFIX-1958170 MOD_E

    protected void setupNewMessageBar() {
        mNewMessageBar = (Button) mConversationContainer.findViewById(
                R.id.new_message_notification_bar);
        mNewMessageBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewMessageBarClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
      //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 871926 + JrdApp PR 859985
      // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 DEL_S
      /*final ControllableActivity activity = (ControllableActivity) getActivity();
      if (activity != null) {
          activity.getConversationUpdater().updateConversation(Conversation.listOf(mConversation),
                  UIProvider.ConversationColumns.STARRED, mConversation.starred);
      }*/
      // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 DEL_E
      //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
        final ControllableActivity activity = (ControllableActivity) getActivity();
        if (activity != null) {
            ContentValues values = new ContentValues();
            values.put(UIProvider.ConversationColumns.STARRED, mConversation.starred);
            values.put(UIProvider.ConversationOperations.Parameters.SUPPRESS_UNDO, true);
            activity.getConversationUpdater().updateConversation(Conversation.listOf(mConversation),values);
        }
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1005432 ADD_S
        //NOTE : performance optimization,we want to reduce the loader work lesser for some unExpected padding happen.
        // So destroy the loader after the fragment destroyed, not care about the data because no fragment to show anymore.
        getLoaderManager().destroyLoader(MESSAGE_LOADER);
        // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1005432 ADD_E
        // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1046583 ADD_S
        //NOTE:It's good idea that after fragment destoried,we release/destory all loaders,it can reduce the asyncTasks,cause the system
        //only supply MAX 128. here means user do not want check this mail,just destory it.
        getLoaderManager().destroyLoader(LOADER_DOWNLOAD_REMAINING);
        // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1046583 ADD_E
        //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_906070 MOD_S
        //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 871926 + JrdApp PR 859985
        // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_S

        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 MOD_S
        //if(ConversationViewHeader.isClickStar){
        //if(initStar != mConversation.starred){
        //Note: save conversation's star to database if if does not equal with it in database.
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_S
//        if (mConversation.starred != mStarInDatabase) {
          //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_906070 MOD_E
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 MOD_E
//                final ControllableActivity activity = (ControllableActivity) getActivity();
//             if (activity != null) {
//                 //TS: zheng.zou 2015-03-03 EMAIL BUGFIX_935495 MOD_S
//                 ContentValues values = new ContentValues();
//                 values.put(UIProvider.ConversationColumns.STARRED, mConversation.starred);
//                 values.put(UIProvider.ConversationOperations.Parameters.SUPPRESS_UNDO, true);
//                 activity.getConversationUpdater().updateConversation(Conversation.listOf(mConversation),values);
////                 activity.getConversationUpdater().updateConversation(Conversation.listOf(mConversation),
////                                UIProvider.ConversationColumns.STARRED, mConversation.starred);
//                 //TS: zheng.zou 2015-03-03 EMAIL BUGFIX_935495 MOD_E
//                 }
//        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_E
        ConversationViewHeader.isClickStar = false;
        // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_E
        //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
        // TS: jin.dong 2016-02-27 EMAIL BUGFIX_1693948  ADD_S
        // avoid memory leak. avoid ANR ,conversationViewFrament may be dattached,destroy webview when fragment destroy.
        if (mWebView != null) {
            // TS: zheng.zou 2015-11-11 EMAIL BUGFIX_571504 ADD_S
            // NOTE: in 5.1 Webview, onDetachedFromWindow() will return early if the Webview is destroyed,
            //the mComponentCallbacks will not be unregistered, this will cause leak.
            // so we remove view before destroy to avoid this situation.
            ViewGroup parent = ((ViewGroup) mWebView.getParent());
            if (parent != null) {
                parent.removeView(mWebView);
            }
            // TS: zheng.zou 2015-11-11 EMAIL BUGFIX_571504 ADD_E
            mWebView.destroy();
            mWebView = null;
        }
        // TS: jin.dong 2016-02-27 EMAIL BUGFIX_1693948  ADD_E

        super.onDestroyView();
        mConversationContainer.setOverlayAdapter(null);
        mAdapter = null;
        resetLoadWaiting(); // be sure to unregister any active load observer
        mViewsCreated = false;
        mMessageHeaderView = null;  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_S
        outState.putBoolean("starred", mConversation.starred);
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_E
        outState.putFloat(BUNDLE_KEY_WEBVIEW_Y_PERCENT, calculateScrollYPercent());
        outState.putBoolean(IS_DOWNLOADING_REMAINING,mIsDownloadingRemaining);  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
        outState.putBoolean(IS_POP_DOWNLOAD_REMAIN, mIsPopDownloadRemain);
    }

    private float calculateScrollYPercent() {
        final float p;
        if (mWebView == null) {
            // onCreateView hasn't been called, return 0 as the user hasn't scrolled the view.
            return 0;
        }

        final int scrollY = mWebView.getScrollY();
        final int viewH = mWebView.getHeight();
        final int webH = (int) (mWebView.getContentHeight() * mWebView.getScale());

        if (webH == 0 || webH <= viewH) {
            p = 0;
        } else if (scrollY + viewH >= webH) {
            // The very bottom is a special case, it acts as a stronger anchor than the scroll top
            // at that point.
            p = 1.0f;
        } else {
            p = (float) scrollY / webH;
        }
        return p;
    }

    private void resetLoadWaiting() {
        if (mLoadWaitReason == LOAD_WAIT_FOR_INITIAL_CONVERSATION) {
            getListController().unregisterConversationLoadedObserver(mLoadedObserver);
        }
        mLoadWaitReason = LOAD_NOW;
    }

    @Override
    protected void markUnread() {
        super.markUnread();
        // Ignore unsafe calls made after a fragment is detached from an activity
        final ControllableActivity activity = (ControllableActivity) getActivity();
        if (activity == null) {
            LogUtils.w(LOG_TAG, "ignoring markUnread for conv=%s", mConversation.id);
            return;
        }

        if (mViewState == null) {
            LogUtils.i(LOG_TAG, "ignoring markUnread for conv with no view state (%d)",
                    mConversation.id);
            return;
        }
        activity.getConversationUpdater().markConversationMessagesUnread(mConversation,
                mViewState.getUnreadMessageUris(), mViewState.getConversationInfo());
    }

    @Override
    public void onUserVisibleHintChanged() {
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        //Read mail one by one,when we come to a new view fragment ,let's try to show toolbar for user.
        final ControllableActivity activity = (ControllableActivity) getActivity();
        if(activity != null) {
            activity.animateShow(null);
        }
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        final boolean userVisible = isUserVisible();
        LogUtils.d(LOG_TAG, "ConversationViewFragment#onUserVisibleHintChanged(), userVisible = %b",
                userVisible);

        if (!userVisible) {
            //TS: lin-zhou 2015-10-10 EMAIL BUGFIX_712361 MOD_S
            if(mProgressController != null){
                mProgressController.dismissLoadingStatus();
            }
            //TS: lin-zhou 2015-10-10 EMAIL BUGFIX_712361 MOD_E
        } else if (mViewsCreated) {
            // TS: Gantao 2015-12-23 EMAIL BUGFIX-1190476 ADD_S
            //If the message is first or last, the download remain button show again!!!
            //Anyway try to hide it.
            if(mMessageHeaderView != null) {
                mMessageHeaderView.showRemainMsgInfo();
                mMessageHeaderView.updateHeight();
            }
            // TS: Gantao 2015-12-23 EMAIL BUGFIX-1190476 ADD_E
            String loadTag = null;
            final boolean isInitialLoading;
            if (mActivity != null) {
                isInitialLoading = mActivity.getConversationUpdater()
                    .isInitialConversationLoading();
            } else {
                isInitialLoading = true;
            }

            if (getMessageCursor() != null) {
                LogUtils.d(LOG_TAG, "Fragment is now user-visible, onConversationSeen: %s", this);
                if (!isInitialLoading) {
                    loadTag = "preloaded";
                }
                onConversationSeen();
            } else if (isLoadWaiting()) {
                LogUtils.d(LOG_TAG, "Fragment is now user-visible, showing conversation: %s", this);
                if (!isInitialLoading) {
                    loadTag = "load_deferred";
                }
                handleDelayedConversationLoad();
            }

            if (loadTag != null) {
                // pager swipes are visibility transitions to 'visible' except during initial
                // pager load on A) enter conversation mode B) rotate C) 2-pane conv-mode list-tap
              Analytics.getInstance().sendEvent("pager_swipe", loadTag,
                      getCurrentFolderTypeDesc(), 0);
            }
        }

        if (mWebView != null) {
            mWebView.onUserVisibilityChanged(userVisible);
        }
    }

    /**
     * Will either call initLoader now to begin loading, or set {@link #mLoadWaitReason} and do
     * nothing (in which case you should later call {@link #handleDelayedConversationLoad()}).
     */
    private void showConversation() {
        final int reason;

        if (isUserVisible()) {
            LogUtils.i(LOG_TAG,
                    "SHOWCONV: CVF is user-visible, immediately loading conversation (%s)", this);
            reason = LOAD_NOW;
            timerMark("CVF.showConversation");
        } else {
            final boolean disableOffscreenLoading = DISABLE_OFFSCREEN_LOADING
                    || Utils.isLowRamDevice(getContext())
                    || (mConversation != null && (mConversation.isRemote
                            || mConversation.getNumMessages() > mMaxAutoLoadMessages));

            // When not visible, we should not immediately load if either this conversation is
            // too heavyweight, or if the main/initial conversation is busy loading.
            if (disableOffscreenLoading) {
                reason = LOAD_WAIT_UNTIL_VISIBLE;
                LogUtils.i(LOG_TAG, "SHOWCONV: CVF waiting until visible to load (%s)", this);
            } else if (getListController().isInitialConversationLoading()) {
                reason = LOAD_WAIT_FOR_INITIAL_CONVERSATION;
                LogUtils.i(LOG_TAG, "SHOWCONV: CVF waiting for initial to finish (%s)", this);
                getListController().registerConversationLoadedObserver(mLoadedObserver);
            } else {
                LogUtils.i(LOG_TAG,
                        "SHOWCONV: CVF is not visible, but no reason to wait. loading now. (%s)",
                        this);
                reason = LOAD_NOW;
            }
        }

        mLoadWaitReason = reason;
        if (mLoadWaitReason == LOAD_NOW) {
            startConversationLoad();
        }
    }

    private void handleDelayedConversationLoad() {
        resetLoadWaiting();
        startConversationLoad();
    }

    private void startConversationLoad() {
        mWebView.setVisibility(View.VISIBLE);
        loadContent();
        // TODO(mindyp): don't show loading status for a previously rendered
        // conversation. Ielieve this is better done by making sure don't show loading status
        // until XX ms have passed without loading completed.
        mProgressController.showLoadingStatus(isUserVisible());
    }

    /**
     * Can be overridden in case a subclass needs to load something other than
     * the messages of a conversation.
     */
    protected void loadContent() {
        getLoaderManager().initLoader(MESSAGE_LOADER, Bundle.EMPTY, getMessageLoaderCallbacks());
    }

    private void revealConversation() {
        timerMark("revealing conversation");
        mProgressController.dismissLoadingStatus(mOnProgressDismiss);
        if (isUserVisible()) {
            AnalyticsTimer.getInstance().logDuration(AnalyticsTimer.OPEN_CONV_VIEW_FROM_LIST,
                    true /* isDestructive */, "open_conversation", "from_list", null);
        }
    }

    private boolean isLoadWaiting() {
        return mLoadWaitReason != LOAD_NOW;
    }

    private void renderConversation(MessageCursor messageCursor) {
        hasRenderContent = true;
        final String convHtml = renderMessageBodies(messageCursor, mEnableContentReadySignal);
        timerMark("rendered conversation");

        if (DEBUG_DUMP_CONVERSATION_HTML) {
            java.io.FileWriter fw = null;
            try {
                fw = new java.io.FileWriter(getSdCardFilePath());
                fw.write(convHtml);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // save off existing scroll position before re-rendering
        if (mWebViewLoadedData) {
            mWebViewYPercent = calculateScrollYPercent();
        }

        //TS: jian.xu 2016-01-27 EMAIL BUGFIX-1275319 MOD_S
        try {
            mWebView.loadDataWithBaseURL(mBaseUri, convHtml, "text/html", "utf-8", null);
        } catch (OutOfMemoryError e) {
            LogUtils.e(LOG_TAG, e, "happen out of memory error while execute loadDataWithBaseURL in render conversation.");
        }
        //TS: jian.xu 2016-01-27 EMAIL BUGFIX-1275319 MOD_E
        mWebViewLoadedData = true;
        mWebViewLoadStartMs = SystemClock.uptimeMillis();
    }

    protected String getSdCardFilePath() {
        //TS: zheng.zou 2015-9-25 EMAIL BUGFIX_667469 MOD_S
        return Environment.getExternalStorageDirectory() + "/conv" + mConversation.id + ".html";
        //TS: zheng.zou 2015-9-25 EMAIL BUGFIX_667469 MOD_E
    }

    /**
     * Populate the adapter with overlay views (message headers, super-collapsed blocks, a
     * conversation header), and return an HTML document with spacer divs inserted for all overlays.
     *
     */
    protected String renderMessageBodies(MessageCursor messageCursor,
            boolean enableContentReadySignal) {
        int pos = -1;

        LogUtils.d(LOG_TAG, "IN renderMessageBodies, fragment=%s", this);
        boolean allowNetworkImages = false;

        // TODO: re-use any existing adapter item state (expanded, details expanded, show pics)

        // Walk through the cursor and build up an overlay adapter as you go.
        // Each overlay has an entry in the adapter for easy scroll handling in the container.
        // Items are not necessarily 1:1 in cursor and adapter because of super-collapsed blocks.
        // When adding adapter items, also add their heights to help the container later determine
        // overlay dimensions.

        // When re-rendering, prevent ConversationContainer from laying out overlays until after
        // the new spacers are positioned by WebView.
        mConversationContainer.invalidateSpacerGeometry();

        mAdapter.clear();

        // re-evaluate the message parts of the view state, since the messages may have changed
        // since the previous render
        final ConversationViewState prevState = mViewState;
        mViewState = new ConversationViewState(prevState);

        // N.B. the units of height for spacers are actually dp and not px because WebView assumes
        // a pixel is an mdpi pixel, unless you set device-dpi.

      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        //add a single toolbar fill blank and measure it
        final int toolbarPos = mAdapter.addToolbarFillBlank();
        final int toolbarPx = measureOverlayHeight(toolbarPos);
        mToolbarHeight = toolbarPx;
        mWebView.setToolbarHeight(toolbarPx);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        // add a single conversation header item
        final int convHeaderPos = mAdapter.addConversationHeader(mConversation);
        final int convHeaderPx = measureOverlayHeight(convHeaderPos);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S
        //The conversation header spacer height should add the toolbar fill blank height now
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
        mCovHeaderHeight = mWebView.screenPxToWebPx(convHeaderPx + toolbarPx);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E

        mTemplates.startConversation(mWebView.getViewportWidth(),
                mWebView.screenPxToWebPx(mSideMarginPx), mWebView.screenPxToWebPx(convHeaderPx + toolbarPx));
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S

        int collapsedStart = -1;
        ConversationMessage prevCollapsedMsg = null;

        final boolean alwaysShowImages = shouldAlwaysShowImages();

        boolean prevSafeForImages = alwaysShowImages;

        boolean hasDraft = false;
        while (messageCursor.moveToPosition(++pos)) {
            final ConversationMessage msg = messageCursor.getMessage();
            if(msg.isDraft()) {//We don't show FAB for draft message
                mFabButton.setVisibility(View.GONE);
            } else {
                mFabButton.setMessageAndBGR(msg);
            }

            final boolean safeForImages = alwaysShowImages ||
                    msg.alwaysShowImages || prevState.getShouldShowImages(msg);
            allowNetworkImages |= safeForImages;

            final Integer savedExpanded = prevState.getExpansionState(msg);
            final int expandedState;
            if (savedExpanded != null) {
                if (ExpansionState.isSuperCollapsed(savedExpanded) && messageCursor.isLast()) {
                    // override saved state when this is now the new last message
                    // this happens to the second-to-last message when you discard a draft
                    expandedState = ExpansionState.EXPANDED;
                } else {
                    expandedState = savedExpanded;
                }
            } else {
                // new messages that are not expanded default to being eligible for super-collapse
                if (!msg.read || messageCursor.isLast()) {
                    expandedState = ExpansionState.EXPANDED;
                } else if (messageCursor.isFirst()) {
                    expandedState = ExpansionState.COLLAPSED;
                } else {
                    expandedState = ExpansionState.SUPER_COLLAPSED;
                    hasDraft |= msg.isDraft();
                }
            }
            mViewState.setShouldShowImages(msg, prevState.getShouldShowImages(msg));
            mViewState.setExpansionState(msg, expandedState);

            // save off "read" state from the cursor
            // later, the view may not match the cursor (e.g. conversation marked read on open)
            // however, if a previous state indicated this message was unread, trust that instead
            // so "mark unread" marks all originally unread messages
            mViewState.setReadState(msg, msg.read && !prevState.isUnread(msg));

            // We only want to consider this for inclusion in the super collapsed block if
            // 1) The we don't have previous state about this message  (The first time that the
            //    user opens a conversation)
            // 2) The previously saved state for this message indicates that this message is
            //    in the super collapsed block.
            if (ExpansionState.isSuperCollapsed(expandedState)) {
                // contribute to a super-collapsed block that will be emitted just before the
                // next expanded header
                if (collapsedStart < 0) {
                    collapsedStart = pos;
                }
                prevCollapsedMsg = msg;
                prevSafeForImages = safeForImages;

                // This line puts the from address in the address cache so that
                // we get the sender image for it if it's in a super-collapsed block.
                getAddress(msg.getFrom());
                continue;
            }

            // resolve any deferred decisions on previous collapsed items
            if (collapsedStart >= 0) {
                if (pos - collapsedStart == 1) {
                    // Special-case for a single collapsed message: no need to super-collapse it.
                    renderMessage(prevCollapsedMsg, false /* expanded */, prevSafeForImages);
                } else {
                    renderSuperCollapsedBlock(collapsedStart, pos - 1, hasDraft);
                }
                hasDraft = false; // reset hasDraft
                prevCollapsedMsg = null;
                collapsedStart = -1;
            }

            renderMessage(msg, ExpansionState.isExpanded(expandedState), safeForImages);
        }

        final MessageHeaderItem lastHeaderItem = getLastMessageHeaderItem();
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S
        final int convFooterPos = mAdapter.addConversationFooter(lastHeaderItem, mAdapter);
        final int convFooterPx = measureOverlayHeight(convFooterPos);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
        mCovFooterHegiht = mWebView.screenPxToWebPx(convFooterPx);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E

        mWebView.setAdapter(mAdapter);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_E
        mWebView.getSettings().setBlockNetworkImage(!allowNetworkImages);

        final boolean applyTransforms = shouldApplyTransforms();

        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 MOD_S
        // If the conversation has specified a base uri, use it here, otherwise use mBaseUri
        return mTemplates.endConversation(mWebView.screenPxToWebPx(convFooterPx), mBaseUri,
                mConversation.getBaseUri(mBaseUri),
                mWebView.getViewportWidth(), mWebView.getWidthInDp(mSideMarginPx),
                enableContentReadySignal, isOverviewMode(mAccount), applyTransforms,
                applyTransforms);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E
    }

    private MessageHeaderItem getLastMessageHeaderItem() {
        final int count = mAdapter.getCount();
        if (count < 3) {
            LogUtils.wtf(LOG_TAG, "not enough items in the adapter. count: %s", count);
            return null;
        }
        return (MessageHeaderItem) mAdapter.getItem(count - 2);
    }

    private void renderSuperCollapsedBlock(int start, int end, boolean hasDraft) {
        final int blockPos = mAdapter.addSuperCollapsedBlock(start, end, hasDraft);
        final int blockPx = measureOverlayHeight(blockPos);
        mTemplates.appendSuperCollapsedHtml(start, mWebView.screenPxToWebPx(blockPx));
    }

    private void renderMessage(ConversationMessage msg, boolean expanded, boolean safeForImages) {

        final int headerPos = mAdapter.addMessageHeader(msg, expanded,
                mViewState.getShouldShowImages(msg));
        final MessageHeaderItem headerItem = (MessageHeaderItem) mAdapter.getItem(headerPos);

        final int footerPos = mAdapter.addMessageFooter(headerItem);

        // Measure item header and footer heights to allocate spacers in HTML
        // But since the views themselves don't exist yet, render each item temporarily into
        // a host view for measurement.
        final int headerPx = measureOverlayHeight(headerPos);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
        mMsgHeaderHeight = mWebView.screenPxToWebPx(headerPx);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E
        final int footerPx = measureOverlayHeight(footerPos);

        mTemplates.appendMessageHtml(msg, expanded, safeForImages,
                mWebView.screenPxToWebPx(headerPx), mWebView.screenPxToWebPx(footerPx));
        timerMark("rendered message");
    }

    private String renderCollapsedHeaders(MessageCursor cursor,
            SuperCollapsedBlockItem blockToReplace) {
        final List<ConversationOverlayItem> replacements = Lists.newArrayList();

        mTemplates.reset();

        final boolean alwaysShowImages = (mAccount != null) &&
                (mAccount.settings.showImages == Settings.ShowImages.ALWAYS);

        // In devices with non-integral density multiplier, screen pixels translate to non-integral
        // web pixels. Keep track of the error that occurs when we cast all heights to int
        float error = 0f;
        boolean first = true;
        for (int i = blockToReplace.getStart(), end = blockToReplace.getEnd(); i <= end; i++) {
            cursor.moveToPosition(i);
            final ConversationMessage msg = cursor.getMessage();
            if (msg != null) {
                LogUtils.d("Email", String.format("test---renderCollapsedHeaders---moveToPosition=%d  messageId=%d conversationUri=%s uri=%s subject=%s",
                        i, msg.id, msg.conversationUri, msg.uri, msg.subject));
            }
            final MessageHeaderItem header = ConversationViewAdapter.newMessageHeaderItem(
                    mAdapter, mAdapter.getDateBuilder(), msg, false /* expanded */,
                    alwaysShowImages || mViewState.getShouldShowImages(msg));
            final MessageFooterItem footer = mAdapter.newMessageFooterItem(mAdapter, header);

            final int headerPx = measureOverlayHeight(header);
            final int footerPx = measureOverlayHeight(footer);
            error += mWebView.screenPxToWebPxError(headerPx)
                    + mWebView.screenPxToWebPxError(footerPx);

            // When the error becomes greater than 1 pixel, make the next header 1 pixel taller
            int correction = 0;
            if (error >= 1) {
                correction = 1;
                error -= 1;
            }

            mTemplates.appendMessageHtml(msg, false /* expanded */,
                    alwaysShowImages || msg.alwaysShowImages,
                    mWebView.screenPxToWebPx(headerPx) + correction,
                    mWebView.screenPxToWebPx(footerPx));
            replacements.add(header);
            replacements.add(footer);

            mViewState.setExpansionState(msg, ExpansionState.COLLAPSED);
        }

        mAdapter.replaceSuperCollapsedBlock(blockToReplace, replacements);
        mAdapter.notifyDataSetChanged();

        return mTemplates.emit();
    }

    protected int measureOverlayHeight(int position) {
        return measureOverlayHeight(mAdapter.getItem(position));
    }

    /**
     * Measure the height of an adapter view by rendering an adapter item into a temporary
     * host view, and asking the view to immediately measure itself. This method will reuse
     * a previous adapter view from {@link ConversationContainer}'s scrap views if one was generated
     * earlier.
     * <p>
     * After measuring the height, this method also saves the height in the
     * {@link ConversationOverlayItem} for later use in overlay positioning.
     *
     * @param convItem adapter item with data to render and measure
     * @return height of the rendered view in screen px
     */
    private int measureOverlayHeight(ConversationOverlayItem convItem) {
        final int type = convItem.getType();

        final View convertView = mConversationContainer.getScrapView(type);
        final View hostView = mAdapter.getView(convItem, convertView, mConversationContainer,
                true /* measureOnly */);
        if (convertView == null) {
            mConversationContainer.addScrapView(type, hostView);
        }

        final int heightPx = mConversationContainer.measureOverlay(hostView);
        convItem.setHeight(heightPx);
        convItem.markMeasurementValid();

        return heightPx;
    }

    @Override
    public void onConversationViewHeaderHeightChange(int newHeight) {
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S
        //Now the conversationViewHeader's should add the toolbar fill blank item's height
        //for benefit treatment.
        final int h = mWebView.screenPxToWebPx(newHeight + mToolbarHeight);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S

        mWebView.loadUrl(String.format("javascript:setConversationHeaderSpacerHeight(%s);", h));
    }

    // END conversation header callbacks

    // START conversation footer callbacks

    @Override
    public void onConversationFooterHeightChange(int newHeight) {
        final int h = mWebView.screenPxToWebPx(newHeight);

        mWebView.loadUrl(String.format("javascript:setConversationFooterSpacerHeight(%s);", h));
    }

    // END conversation footer callbacks

    // START message header callbacks
    @Override
    public void setMessageSpacerHeight(MessageHeaderItem item,
            int newSpacerHeightPx) {
        mConversationContainer.invalidateSpacerGeometry();

        // update message HTML spacer height
        final int h = mWebView.screenPxToWebPx(newSpacerHeightPx);
        LogUtils.i(LAYOUT_TAG, "setting HTML spacer h=%dwebPx (%dscreenPx)", h,
                newSpacerHeightPx);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 MOD_S
        if (mWebView.getScale() < mWebView.getInitialScale()) {
            mWebView.loadUrl(String.format(
                    "javascript:setMessageHeaderSpacerHeight('%s', %s);",
                    mTemplates.getMessageDomId(item.getMessage()),
                    h * mWebView.getInitialScale() / mWebView.getScale()));
        } else {
            mWebView.loadUrl(String.format(
                    "javascript:setMessageHeaderSpacerHeight('%s', %s);",
                    mTemplates.getMessageDomId(item.getMessage()), h));
        }
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 MOD_E
    }

    @Override
    public void setMessageExpanded(MessageHeaderItem item, int newSpacerHeightPx) {
        mConversationContainer.invalidateSpacerGeometry();

        // show/hide the HTML message body and update the spacer height
        final int h = mWebView.screenPxToWebPx(newSpacerHeightPx);
        LogUtils.i(LAYOUT_TAG, "setting HTML spacer expanded=%s h=%dwebPx (%dscreenPx)",
                item.isExpanded(), h, newSpacerHeightPx);
        mWebView.loadUrl(String.format("javascript:setMessageBodyVisible('%s', %s, %s);",
                mTemplates.getMessageDomId(item.getMessage()), item.isExpanded(), h));

        mViewState.setExpansionState(item.getMessage(),
                item.isExpanded() ? ExpansionState.EXPANDED : ExpansionState.COLLAPSED);
    }

    @Override
    public void showExternalResources(final Message msg) {
        mViewState.setShouldShowImages(msg, true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.loadUrl("javascript:unblockImages(['" + mTemplates.getMessageDomId(msg) + "']);");
    }

    @Override
    public void showExternalResources(final String senderRawAddress) {
        mWebView.getSettings().setBlockNetworkImage(false);

        final Address sender = getAddress(senderRawAddress);
        final MessageCursor cursor = getMessageCursor();

        final List<String> messageDomIds = new ArrayList<String>();

        int pos = -1;
        while (cursor.moveToPosition(++pos)) {
            final ConversationMessage message = cursor.getMessage();
            if (sender.equals(getAddress(message.getFrom()))) {
                message.alwaysShowImages = true;

                mViewState.setShouldShowImages(message, true);
                messageDomIds.add(mTemplates.getMessageDomId(message));
            }
        }

        final String url = String.format(
                "javascript:unblockImages(['%s']);", TextUtils.join("','", messageDomIds));
        mWebView.loadUrl(url);
    }

    @Override
    public boolean supportsMessageTransforms() {
        return true;
    }

    @Override
    public String getMessageTransforms(final Message msg) {
        final String domId = mTemplates.getMessageDomId(msg);
        return (domId == null) ? null : mMessageTransforms.get(domId);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    // END message header callbacks

    @Override
    public void showUntransformedConversation() {
        super.showUntransformedConversation();
        renderConversation(getMessageCursor());
    }

    @Override
    public void onSuperCollapsedClick(SuperCollapsedBlockItem item) {
        MessageCursor cursor = getMessageCursor();
        if (cursor == null || !mViewsCreated) {
            return;
        }

        mTempBodiesHtml = renderCollapsedHeaders(cursor, item);
        mWebView.loadUrl("javascript:replaceSuperCollapsedBlock(" + item.getStart() + ")");
        mConversationContainer.focusFirstMessageHeader();
    }

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    @Override
    public int loadSingleMessageBackground(Message msg) {
        Context context = getContext();
        com.tct.emailcommon.provider.Account account = com.tct.emailcommon.provider.Account
                .getAccountForMessageId(context, msg.getId());
        int status = -1;
        if (account == null) {
            return 0;
        }
        /* String protocol = account.getProtocol(getContext());
        if (protocol.equals(HostAuth.SCHEME_IMAP)) {
            status = ImapService.loadUnsyncedMessage(getContext(), msg.getId());
        } else if (protocol.equals(HostAuth.SCHEME_POP3)){
            status = Pop3Service.loadUnsyncedMessage(getContext(),msg.getId()); */
        if (mAdapter != null && mAdapter.getMessageHeaderItem() != null) {
            mMessageHeaderView = mAdapter.getMessageHeaderView();
        }
        if (mMessageHeaderView != null) {
            mMessageHeaderView.showRemainProgress(true);
        }
        mIsDownloadingRemaining = true;
        LoaderManager lm = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_MESSAGE, msg);
        lm.initLoader(LOADER_DOWNLOAD_REMAINING, bundle, mDownloadRemainCallback);
        return status;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,05/10/2014,FR 644789.
    //The method call systemSyncManager to sync one message.by the way,
    //we add DOWNLOAD_FLAG and MESSAGE_ID to bundle in which easSyncHandler
    //need it.
    public static void requestSyncForOneMessage(Context context,String accountType,com.tct.emailcommon.provider.Account account,Message msg) {
        long mailboxId = Mailbox.findMailboxOfType(context, account.mId, Mailbox.TYPE_INBOX);
        final Bundle extras = Mailbox.createSyncBundle(mailboxId);
        extras.putBoolean("DOWNLOAD_FLAG",true);
        extras.putLong("MESSAGE_ID",msg.getId());
        ContentResolver.requestSync(new android.accounts.Account(account.mEmailAddress,
                accountType), EmailContent.AUTHORITY, extras);
    }
    //[FEATURE]-Add-END by TSCD.chao zhang
    // [BUGFIX]-Add-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
    //The method used to get if the eas account mail body sync complete.
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 MOD_S
    // NOTE: Here we use AIDL to do the load jobs,just for exchange
    private int loadEasTypeMessage(Context context, com.tct.emailcommon.provider.Account account,
            Message msg) {
        int status = 0;
        isEasCall = true;
        EmailServiceProxy service =
                EmailServiceUtils.getServiceForAccount(context, account.getId());
        if (service != null) {
            try {
                status = service.fetchMessage(msg.getId());
            } catch (RemoteException e) {
                LogUtils.e("Email_ccx", "fetchMessage RemoteException", e);
            }
        }
        return status;
    }
    // [BUGFIX]-Add-END by TSCD.chao zhang
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 MOD_E

    //[BUGFIX]-Add-BEGIN?by?TSCD.zheng.zou,01/14/2015,887972
    //[Email]It?still?display?download?remaining?when?rotate?the?screen?during?loading
    //note: use AsyncTaskLoader to replace with the original AsyncTask
    //the LoaderManager will take manage of the AsyncTaskLoader when rotate screen,  AsyncTask will not
    private class DownloadRemainCallback implements LoaderManager.LoaderCallbacks<Long> {
        @Override
        public Loader<Long> onCreateLoader(int id, Bundle args) {
            if (args != null) {
                final Message message = args.getParcelable(EXTRA_MESSAGE);
                final Context context = getContext();
                AsyncTaskLoader<Long> loader = new AsyncTaskLoader<Long>(context) {
                    @Override
                    public Long loadInBackground() {
                        long msgId = message.getId();
                        com.tct.emailcommon.provider.Account account = com.tct.emailcommon.provider.Account
                                .getAccountForMessageId(context, message.getId());
                        String protocol = account.getProtocol(context);
                        int status = MessageHeaderView.LOAD_REMAIN_MESSAGE_FAIL;
                        if (protocol.equals(HostAuth.SCHEME_IMAP)) {
                            status = ImapService.loadUnsyncedMessage(context, msgId);
                        } else if (HostAuth.SCHEME_POP3.equals(protocol)) {
                            // TS: tao.gan 2015-07-20 EMAIL BUGFIX-1041711 ADD_S
                            mIsPopDownloadRemain = true;
                            hasRenderContent = false;
                            // TS: tao.gan 2015-07-20 EMAIL BUGFIX-1041711 ADD_E
                            status = Pop3Service.loadUnsyncedMessage(context, msgId);
                        } else if (HostAuth.SCHEME_EAS.equals(protocol)) {
                            status = loadEasTypeMessage(context, account, message);
                        }
                        return status == MessageHeaderView.LOAD_REMAIN_MESSAGE_SUCCESS ?
                                msgId : MSG_NONE_ID;

                    }
                };
                loader.forceLoad();
                return loader;
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Long> loader, Long data) {
            mIsDownloadingRemaining = false;
            getLoaderManager().destroyLoader(loader.getId());
            // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
            if (data == MSG_NONE_ID) {
                if (mMessageHeaderView != null) {
                    mMessageHeaderView.showRemainProgress(false);
                }
            } else {
                // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1005432 DEL_S
                //NOTE: Performance Optimization. After L,no need to reinit the message loader to load the conversation,Observer will help to do it.
                // In some case,It's bad influence that cause the accountList,conversationList,FolderList loader pedding.
                // Discard it !!!!!
                /*long msgId = data;
                if (isEasCall && isAdded()) {
                    // After sync completely,we reinit the loader to load the conversation.
                    getLoaderManager().destroyLoader(MESSAGE_LOADER);
                    if(isVisible()){
                    loadContent();
                    }
                    isEasCall =false;
                }*/
                // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1005432 DEL_E
                if (mMessageHeaderView != null) {
                    mMessageHeaderView.hideRemainView();
                }
            }
            // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
        }

        @Override
        public void onLoaderReset(Loader<Long> loader) {

        }
    }
    //[BUGFIX]-Add-END?by?TSCD.zheng.zou

    //[BUGFIX]-Add-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
    //we use asyncTask to manage the different account type sync.here only eas
    //need to startMessageLoader manually.
    private class LoadMessageAsyncTask extends AsyncTask<Long, Long, Integer> {
        private final Context context;
        private final int accountType;
        private final long msgId;
        private com.tct.emailcommon.provider.Account account;
        private Message message;
        public LoadMessageAsyncTask(Context cx, int type, long id) {
            context = cx;
            accountType = type;
            msgId = id;
            if(mAdapter!=null &&mAdapter.getMessageHeaderItem()!=null){
            mMessageHeaderView = mAdapter.getMessageHeaderView();
            }
        }

        public LoadMessageAsyncTask(Context cx, int type, long id,
                final com.tct.emailcommon.provider.Account ac, final Message msg) {
            this(cx, type, id);
            account = ac;
            message = msg;
            if(mAdapter!=null &&mAdapter.getMessageHeaderItem()!=null){
            mMessageHeaderView = mAdapter.getMessageHeaderView();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mMessageHeaderView.showRemainProgress(true);
        }

        @Override
        protected Integer doInBackground(Long... arg0) {
            // TODO Auto-generated method stub
            switch (accountType) {
            case Utils.TYPE_IMAP:
                return ImapService.loadUnsyncedMessage(context, msgId);
            case Utils.TYPE_POP3:
                return Pop3Service.loadUnsyncedMessage(context, msgId);
            case Utils.TYPE_EAS:
                return loadEasTypeMessage(context, account, message);
            }
            return 0;
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            int syncStatus = result.intValue();
            Log.e("Email.zc", "onPostExecute--->syncStatus=="
                    + syncStatus);
            switch (syncStatus) {
            case MessageHeaderView.LOAD_REMAIN_MESSAGE_FAIL:
                mMessageHeaderView.showRemainProgress(false);
                break;
            case MessageHeaderView.LOAD_REMAIN_MESSAGE_SUCCESS:
                if (isEasCall) {
                    //After sync completely,we reinit the loader to load the conversation.
                    getLoaderManager().destroyLoader(MESSAGE_LOADER);
                    loadContent();
                }
                mMessageHeaderView.showRemainProgress(false);
                mMessageHeaderView.hideOrShowRemainMsgInfo(false);
                break;
            }
        }
    }
    //[BUGFIX]-Add-END by TSCD.chao zhang

    private void showNewMessageNotification(NewMessagesInfo info) {
        mNewMessageBar.setText(info.getNotificationText());
        mNewMessageBar.setVisibility(View.VISIBLE);
    }

    private void onNewMessageBarClick() {
        mNewMessageBar.setVisibility(View.GONE);

        renderConversation(getMessageCursor()); // mCursor is already up-to-date
                                                // per onLoadFinished()
    }

    private static OverlayPosition[] parsePositions(final int[] topArray, final int[] bottomArray) {
        final int len = topArray.length;
        final OverlayPosition[] positions = new OverlayPosition[len];
        for (int i = 0; i < len; i++) {
            positions[i] = new OverlayPosition(topArray[i], bottomArray[i]);
        }
        return positions;
    }

    protected Address getAddress(String rawFrom) {
        return Utils.getAddress(mAddressCache, rawFrom);
    }

    private void ensureContentSizeChangeListener() {
        if (mWebViewSizeChangeListener == null) {
            mWebViewSizeChangeListener = new ContentSizeChangeListener() {
                @Override
                public void onHeightChange(int h) {
                    // When WebKit says the DOM height has changed, re-measure
                    // bodies and re-position their headers.
                    // This is separate from the typical JavaScript DOM change
                    // listeners because cases like NARROW_COLUMNS text reflow do not trigger DOM
                    // events.
                    // TS: tianjing.su 2016-03-17 EMAIL BUGFIX-1838565 MOD_S
                    if (mWebView != null) {
                        mWebView.loadUrl("javascript:measurePositions();");
                    }
                    // TS: tianjing.su 2016-03-17 EMAIL BUGFIX-1838565 MOD_E
                }
            };
        }
        mWebView.setContentSizeChangeListener(mWebViewSizeChangeListener);
    }

    public static boolean isOverviewMode(Account acct) {
        return acct.settings.isOverviewMode();
    }

    private void setupOverviewMode() {
        // for now, overview mode means use the built-in WebView zoom and disable custom scale
        // gesture handling
        final boolean overviewMode = isOverviewMode(mAccount);
        final WebSettings settings = mWebView.getSettings();
        final WebSettings.LayoutAlgorithm layout;
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 DEL_S
//        settings.setUseWideViewPort(overviewMode);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 DEL_E
        settings.setSupportZoom(overviewMode);
        settings.setBuiltInZoomControls(overviewMode);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 DEL_S
//        settings.setLoadWithOverviewMode(overviewMode);
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 DEL_E
        if (overviewMode) {
            settings.setDisplayZoomControls(false);
            layout = WebSettings.LayoutAlgorithm.NORMAL;
        } else {
            layout = WebSettings.LayoutAlgorithm.NARROW_COLUMNS;
        }
        settings.setLayoutAlgorithm(layout);
    }

    @Override
    public ConversationMessage getMessageForClickedUrl(String url) {
        final String domMessageId = mUrlToMessageIdMap.get(url);
        if (domMessageId == null) {
            return null;
        }
        final String messageId = mTemplates.getMessageIdForDomId(domMessageId);
        return getMessageCursor().getMessageForId(Long.parseLong(messageId));
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            mOriginalKeyedView = view;
        }

        if (mOriginalKeyedView != null) {
            final int id = mOriginalKeyedView.getId();
            final boolean isActionUp = keyEvent.getAction() == KeyEvent.ACTION_UP;
            final boolean isLeft = keyCode == KeyEvent.KEYCODE_DPAD_LEFT;
            final boolean isRight = keyCode == KeyEvent.KEYCODE_DPAD_RIGHT;
            final boolean isUp = keyCode == KeyEvent.KEYCODE_DPAD_UP;
            final boolean isDown = keyCode == KeyEvent.KEYCODE_DPAD_DOWN;

            // First we run the event by the controller
            // We manually check if the view+direction combination should shift focus away from the
            // conversation view to the thread list in two-pane landscape mode.
            final boolean isTwoPaneLand = mNavigationController.isTwoPaneLandscape();
            final boolean navigateAway = mConversationContainer.shouldNavigateAway(id, isLeft,
                    isTwoPaneLand);
            if (mNavigationController.onInterceptKeyFromCV(keyCode, keyEvent, navigateAway)) {
                return true;
            }

            // If controller didn't handle the event, check directional interception.
            if ((isLeft || isRight) && mConversationContainer.shouldInterceptLeftRightEvents(
                    id, isLeft, isRight, isTwoPaneLand)) {
                return true;
            } else if (isUp || isDown) {
                // We don't do anything on up/down for overlay
                if (id == R.id.conversation_topmost_overlay) {
                    return true;
                }

                // We manually handle up/down navigation through the overlay items because the
                // system's default isn't optimal for two-pane landscape since it's not a real list.
                final int position = mConversationContainer.getViewPosition(mOriginalKeyedView);
                final View next = mConversationContainer.getNextOverlayView(position, isDown);
                if (next != null) {
                    if (isActionUp) {
                        next.requestFocus();

                        // Make sure that v is in view
                        final int[] coords = new int[2];
                        next.getLocationOnScreen(coords);
                        final int bottom = coords[1] + next.getHeight();
                        if (bottom > mMaxScreenHeight) {
                            mWebView.scrollBy(0, bottom - mMaxScreenHeight);
                        } else if (coords[1] < mTopOfVisibleScreen) {
                            mWebView.scrollBy(0, coords[1] - mTopOfVisibleScreen);
                        }
                    }
                    return true;
                } else {
                    // Special case two end points
                    // Start is marked as index 1 because we are currently not allowing focus on
                    // conversation view header.
                    if ((position == mConversationContainer.getOverlayCount() - 1 && isDown) ||
                            (position == 1 && isUp)) {
                        mTopmostOverlay.requestFocus();
                        // Scroll to the the top if we hit the first item
                        if (isUp) {
                            mWebView.scrollTo(0, 0);
                        }
                        return true;
                    }
                }
            }

            // Finally we handle the special keys
            if (keyCode == KeyEvent.KEYCODE_BACK && id != R.id.conversation_topmost_overlay) {
                if (isActionUp) {
                    mTopmostOverlay.requestFocus();
                }
                //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_845619 MOD_S
//                return true;
                return false;
                //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_845619 MOD_E
            } else if (keyCode == KeyEvent.KEYCODE_ENTER &&
                    id == R.id.conversation_topmost_overlay) {
                if (isActionUp) {
                    mConversationContainer.focusFirstMessageHeader();
                    mWebView.scrollTo(0, 0);
                }
                return true;
            }
        }
        return false;
    }

    public class ConversationWebViewClient extends AbstractConversationWebViewClient {
        public ConversationWebViewClient(Account account) {
            super(account);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Ignore unsafe calls made after a fragment is detached from an activity.
            // This method needs to, for example, get at the loader manager, which needs
            // the fragment to be added.
            if (!isAdded() || !mViewsCreated) {
                LogUtils.d(LOG_TAG, "ignoring CVF.onPageFinished, url=%s fragment=%s", url,
                        ConversationViewFragment.this);
                return;
            }

            LogUtils.d(LOG_TAG, "IN CVF.onPageFinished, url=%s fragment=%s wv=%s t=%sms", url,
                    ConversationViewFragment.this, view,
                    (SystemClock.uptimeMillis() - mWebViewLoadStartMs));

            ensureContentSizeChangeListener();

            if (!mEnableContentReadySignal) {
                revealConversation();
            }

            final Set<String> emailAddresses = Sets.newHashSet();
            final List<Address> cacheCopy;
            synchronized (mAddressCache) {
                cacheCopy = ImmutableList.copyOf(mAddressCache.values());
            }
            for (Address addr : cacheCopy) {
                emailAddresses.add(addr.getAddress());
            }
            final ContactLoaderCallbacks callbacks = getContactInfoSource();
            callbacks.setSenders(emailAddresses);
            getLoaderManager().restartLoader(CONTACT_LOADER, Bundle.EMPTY, callbacks);
            // TS: Gantao 2015-07-20 EMAIL BUGFIX-1043844 ADD_S
            //Note:update message header view's sapcer height here to avoid the UI cut off.
            mAdapter.getMessageHeaderView().updateHeight();
            // TS: Gantao 2015-07-20 EMAIL BUGFIX-1043844 ADD_E
        }

        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            // TODO Auto-generated method stub
            if (newScale < mWebView.getInitialScale()) {
                webViewScaleHasChanged = true;
            } else {
                webViewScaleHasChanged = false;
            }
            super.onScaleChanged(view, oldScale, newScale);
        }
        // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return mViewsCreated && super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * NOTE: all public methods must be listed in the proguard flags so that they can be accessed
     * via reflection and not stripped.
     *
     */
    private class MailJsBridge {
        @JavascriptInterface
        public void onWebContentGeometryChange(final int[] overlayTopStrs,
                final int[] overlayBottomStrs) {
            try {
                getHandler().post(new FragmentRunnable("onWebContentGeometryChange",
                        ConversationViewFragment.this) {
                    @Override
                    public void go() {
                        if (!mViewsCreated) {
                            LogUtils.d(LOG_TAG, "ignoring webContentGeometryChange because views"
                                    + " are gone, %s", ConversationViewFragment.this);
                            return;
                        }
                        mConversationContainer.onGeometryChange(
                                parsePositions(overlayTopStrs, overlayBottomStrs));
                        if (mDiff != 0) {
                            // SCROLL!
                            int scale = (int) (mWebView.getScale() / mWebView.getInitialScale());
                            if (scale > 1) {
                                mWebView.scrollBy(0, (mDiff * (scale - 1)));
                            }
                            mDiff = 0;
                        }
                    }
                });
            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.onWebContentGeometryChange");
            }
        }

        @JavascriptInterface
        public String getTempMessageBodies() {
            try {
                if (!mViewsCreated) {
                    return "";
                }

                final String s = mTempBodiesHtml;
                mTempBodiesHtml = null;
                return s;
            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.getTempMessageBodies");
                return "";
            }
        }

        @JavascriptInterface
        public String getMessageBody(String domId) {
            try {
                final MessageCursor cursor = getMessageCursor();
                if (!mViewsCreated || cursor == null) {
                    return "";
                }

                int pos = -1;
                while (cursor.moveToPosition(++pos)) {
                    final ConversationMessage msg = cursor.getMessage();
                    if (TextUtils.equals(domId, mTemplates.getMessageDomId(msg))) {
                        return HtmlConversationTemplates.wrapMessageBody(msg.getBodyAsHtml());
                    }
                }

                return "";

            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.getMessageBody");
                return "";
            }
        }

        @JavascriptInterface
        public String getMessageSender(String domId) {
            try {
                final MessageCursor cursor = getMessageCursor();
                if (!mViewsCreated || cursor == null) {
                    return "";
                }

                int pos = -1;
                while (cursor.moveToPosition(++pos)) {
                    final ConversationMessage msg = cursor.getMessage();
                    if (TextUtils.equals(domId, mTemplates.getMessageDomId(msg))) {
                        return getAddress(msg.getFrom()).getAddress();
                    }
                }

                return "";

            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.getMessageSender");
                return "";
            }
        }

        @JavascriptInterface
        public void onContentReady() {
            try {
                getHandler().post(new FragmentRunnable("onContentReady",
                        ConversationViewFragment.this) {
                    @Override
                    public void go() {
                        try {
                            if (mWebViewLoadStartMs != 0) {
                                LogUtils.i(LOG_TAG, "IN CVF.onContentReady, f=%s vis=%s t=%sms",
                                        ConversationViewFragment.this,
                                        isUserVisible(),
                                        (SystemClock.uptimeMillis() - mWebViewLoadStartMs));
                            }
                            revealConversation();
                        } catch (Throwable t) {
                            LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.onContentReady");
                            // Still try to show the conversation.
                            revealConversation();
                        }
                    }
                });
            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.onContentReady");
            }
        }

        @JavascriptInterface
        public float getScrollYPercent() {
            try {
                return mWebViewYPercent;
            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.getScrollYPercent");
                return 0f;
            }
        }

        @JavascriptInterface
        public void onMessageTransform(String messageDomId, String transformText) {
            try {
                LogUtils.i(LOG_TAG, "TRANSFORM: (%s) %s", messageDomId, transformText);
                mMessageTransforms.put(messageDomId, transformText);
                onConversationTransformed();
            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.onMessageTransform");
            }
        }

        @JavascriptInterface
        public void onInlineAttachmentsParsed(final String[] urls, final String[] messageIds) {
            try {
                getHandler().post(new FragmentRunnable("onInlineAttachmentsParsed",
                        ConversationViewFragment.this) {
                    @Override
                    public void go() {
                        try {
                            for (int i = 0, size = urls.length; i < size; i++) {
                                mUrlToMessageIdMap.put(urls[i], messageIds[i]);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LogUtils.e(LOG_TAG, e,
                                    "Number of urls does not match number of message ids - %s:%s",
                                    urls.length, messageIds.length);
                        }
                    }
                });
            } catch (Throwable t) {
                LogUtils.e(LOG_TAG, t, "Error in MailJsBridge.onInlineAttachmentsParsed");
            }
        }
    }

    private class NewMessagesInfo {
        int count;
        int countFromSelf;
        String senderAddress;

        /**
         * Return the display text for the new message notification overlay. It will be formatted
         * appropriately for a single new message vs. multiple new messages.
         *
         * @return display text
         */
        public String getNotificationText() {
            Resources res = getResources();
            if (count > 1) {
                return res.getQuantityString(R.plurals.new_incoming_messages_many, count, count);
            } else {
                final Address addr = getAddress(senderAddress);
                return res.getString(R.string.new_incoming_messages_one,
                        mBidiFormatter.unicodeWrap(TextUtils.isEmpty(addr.getPersonal())
                                ? addr.getAddress() : addr.getPersonal()));
            }
        }
    }

    @Override
    public void onMessageCursorLoadFinished(Loader<ObjectCursor<ConversationMessage>> loader,
            MessageCursor newCursor, MessageCursor oldCursor) {
        /*
         * what kind of changes affect the MessageCursor? 1. new message(s) 2.
         * read/unread state change 3. deleted message, either regular or draft
         * 4. updated message, either from self or from others, updated in
         * content or state or sender 5. star/unstar of message (technically
         * similar to #1) 6. other label change Use MessageCursor.hashCode() to
         * sort out interesting vs. no-op cursor updates.
         */

        if (oldCursor != null && !oldCursor.isClosed()) {
            final NewMessagesInfo info = getNewIncomingMessagesInfo(newCursor);

            if (info.count > 0) {
                // don't immediately render new incoming messages from other
                // senders
                // (to avoid a new message from losing the user's focus)
                LogUtils.i(LOG_TAG, "CONV RENDER: conversation updated"
                        + ", holding cursor for new incoming message (%s)", this);
                showNewMessageNotification(info);
                return;
            }

            // TS: tao.gan 2015-07-20 EMAIL BUGFIX-1041711 MOD_S
            // Note :for pop account's download remain,we get the state without checking it's
            // attachmentsStashCode to avoid screen flash during render message content.
//            final int oldState = oldCursor.getStateHashCode();
//            final boolean changed = newCursor.getStateHashCode() != oldState;
            int oldState;
            boolean changed;
            if(mMessageHeaderView != null && mIsPopDownloadRemain && hasRenderContent) {
                oldState = oldCursor.getStateHashCodePop();
                changed = newCursor.getStateHashCodePop() != oldState;
                // TS: zheng.zou 2015-09-19 EMAIL BUGFIX-546917 Add_S
            } else if (newCursor.getConversation() != null && mConversation != null &&
                    mConversation.starred == newCursor.getConversation().starred) {
                //avoid render whole content when the star state is already changed in cache
                //which will cause flash screen
                oldState = oldCursor.getStateHashCodeWithoutStar();
                changed = newCursor.getStateHashCodeWithoutStar() != oldState;
                // TS: zheng.zou 2015-09-19 EMAIL BUGFIX-546917 Add_E
            } else {
                oldState = oldCursor.getStateHashCode();
                changed = newCursor.getStateHashCode() != oldState;
            }
            // TS: tao.gan 2015-07-20 EMAIL BUGFIX-1041711 MOD_E

            if (!changed) {
                final boolean processedInPlace = processInPlaceUpdates(newCursor, oldCursor);
                if (processedInPlace) {
                    LogUtils.i(LOG_TAG, "CONV RENDER: processed update(s) in place (%s)", this);
                } else {
                    LogUtils.i(LOG_TAG, "CONV RENDER: uninteresting update"
                            + ", ignoring this conversation update (%s)", this);
                }
                return;
            } else if (info.countFromSelf == 1) {
                // Special-case the very common case of a new cursor that is the same as the old
                // one, except that there is a new message from yourself. This happens upon send.
                final boolean sameExceptNewLast = newCursor.getStateHashCode(1) == oldState;
                if (sameExceptNewLast) {
                    LogUtils.i(LOG_TAG, "CONV RENDER: update is a single new message from self"
                            + " (%s)", this);
                    newCursor.moveToLast();
                    processNewOutgoingMessage(newCursor.getMessage());
                    return;
                }
            }
            // cursors are different, and not due to an incoming message. fall
            // through and render.
            LogUtils.i(LOG_TAG, "CONV RENDER: conversation updated"
                    + ", but not due to incoming message. rendering. (%s)", this);

            if (DEBUG_DUMP_CURSOR_CONTENTS) {
                LogUtils.i(LOG_TAG, "old cursor: %s", oldCursor.getDebugDump());
                LogUtils.i(LOG_TAG, "new cursor: %s", newCursor.getDebugDump());
            }
        } else {
            LogUtils.i(LOG_TAG, "CONV RENDER: initial render. (%s)", this);
            timerMark("message cursor load finished");
        }

        renderContent(newCursor);
    }

    protected void renderContent(MessageCursor messageCursor) {
        // if layout hasn't happened, delay render
        // This is needed in addition to the showConversation() delay to speed
        // up rotation and restoration.
        if (mConversationContainer.getWidth() == 0) {
            mNeedRender = true;
            mConversationContainer.addOnLayoutChangeListener(this);
        } else {
            renderConversation(messageCursor);
        }
    }

    private NewMessagesInfo getNewIncomingMessagesInfo(MessageCursor newCursor) {
        final NewMessagesInfo info = new NewMessagesInfo();

        int pos = -1;
        while (newCursor.moveToPosition(++pos)) {
            final Message m = newCursor.getMessage();
            if (!mViewState.contains(m)) {
                LogUtils.i(LOG_TAG, "conversation diff: found new msg: %s", m.uri);

                final Address from = getAddress(m.getFrom());
                // distinguish ours from theirs
                // new messages from the account owner should not trigger a
                // notification
                if (from == null || mAccount.ownsFromAddress(from.getAddress())) {
                    LogUtils.i(LOG_TAG, "found message from self: %s", m.uri);
                    info.countFromSelf++;
                    continue;
                }

                info.count++;
                info.senderAddress = m.getFrom();
            }
        }
        return info;
    }

    private boolean processInPlaceUpdates(MessageCursor newCursor, MessageCursor oldCursor) {
        final Set<String> idsOfChangedBodies = Sets.newHashSet();
        final List<Integer> changedOverlayPositions = Lists.newArrayList();

        boolean changed = false;

        int pos = 0;
        while (true) {
            if (!newCursor.moveToPosition(pos) || !oldCursor.moveToPosition(pos)) {
                break;
            }

            final ConversationMessage newMsg = newCursor.getMessage();
            final ConversationMessage oldMsg = oldCursor.getMessage();

            // We are going to update the data in the adapter whenever any input fields change.
            // This ensures that the Message object that ComposeActivity uses will be correctly
            // aligned with the most up-to-date data.
            if (!newMsg.isEqual(oldMsg)) {
                mAdapter.updateItemsForMessage(newMsg, changedOverlayPositions);
                LogUtils.i(LOG_TAG, "msg #%d (%d): detected field(s) change. sendingState=%s",
                        pos, newMsg.id, newMsg.sendingState);
            }

            // update changed message bodies in-place
            if (!TextUtils.equals(newMsg.bodyHtml, oldMsg.bodyHtml) ||
                    !TextUtils.equals(newMsg.bodyText, oldMsg.bodyText)) {
                // maybe just set a flag to notify JS to re-request changed bodies
                idsOfChangedBodies.add('"' + mTemplates.getMessageDomId(newMsg) + '"');
                LogUtils.i(LOG_TAG, "msg #%d (%d): detected body change", pos, newMsg.id);
            }

            pos++;
        }


        if (!changedOverlayPositions.isEmpty()) {
            // notify once after the entire adapter is updated
            mConversationContainer.onOverlayModelUpdate(changedOverlayPositions);
            changed = true;
        }

        final ConversationFooterItem footerItem = mAdapter.getFooterItem();
        if (footerItem != null) {
            footerItem.invalidateMeasurement();
        }
        if (!idsOfChangedBodies.isEmpty()) {
            mWebView.loadUrl(String.format("javascript:replaceMessageBodies([%s]);",
                    TextUtils.join(",", idsOfChangedBodies)));
            changed = true;
        }

        // TS: Gantao 2015-06-02 EMAIL BUGFIX-998526 ADD_S
        if (mMessageHeaderView != null) {
            mMessageHeaderView.updateHeight();
        }
        // TS: Gantao 2015-06-02 EMAIL BUGFIX-998526 ADD_E
        return changed;
    }

    private void processNewOutgoingMessage(ConversationMessage msg) {
        // Temporarily remove the ConversationFooterItem and its view.
        // It will get re-added right after the new message is added.
        final ConversationFooterItem footerItem = mAdapter.removeFooterItem();
        mConversationContainer.removeViewAtAdapterIndex(footerItem.getPosition());
        mTemplates.reset();
        // this method will add some items to mAdapter, but we deliberately want to avoid notifying
        // adapter listeners (i.e. ConversationContainer) until onWebContentGeometryChange is next
        // called, to prevent N+1 headers rendering with N message bodies.
        renderMessage(msg, true /* expanded */, msg.alwaysShowImages);
        mTempBodiesHtml = mTemplates.emit();

        if (footerItem != null) {
            footerItem.setLastMessageHeaderItem(getLastMessageHeaderItem());
            footerItem.invalidateMeasurement();
            mAdapter.addItem(footerItem);
        }

        mViewState.setExpansionState(msg, ExpansionState.EXPANDED);
        // FIXME: should the provider set this as initial state?
        mViewState.setReadState(msg, false /* read */);

        // From now until the updated spacer geometry is returned, the adapter items are mismatched
        // with the existing spacers. Do not let them layout.
        mConversationContainer.invalidateSpacerGeometry();

        mWebView.loadUrl("javascript:appendMessageHtml();");
    }

    private static class SetCookieTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;
        private final String mUri;
        private final Uri mAccountCookieQueryUri;
        private final ContentResolver mResolver;

        /* package */ SetCookieTask(Context context, String baseUri, Uri accountCookieQueryUri) {
            mContext = context;
            mUri = baseUri;
            mAccountCookieQueryUri = accountCookieQueryUri;
            mResolver = context.getContentResolver();
        }

        @Override
        public Void doInBackground(Void... args) {
            // First query for the cookie string from the UI provider
            final Cursor cookieCursor = mResolver.query(mAccountCookieQueryUri,
                    UIProvider.ACCOUNT_COOKIE_PROJECTION, null, null, null);
            if (cookieCursor == null) {
                return null;
            }

            try {
                if (cookieCursor.moveToFirst()) {
                    final String cookie = cookieCursor.getString(
                            cookieCursor.getColumnIndex(UIProvider.AccountCookieColumns.COOKIE));

                    if (cookie != null) {
                        final CookieSyncManager csm =
                                CookieSyncManager.createInstance(mContext);
                        CookieManager.getInstance().setCookie(mUri, cookie);
                        csm.sync();
                    }
                }

            } finally {
                cookieCursor.close();
            }


            return null;
        }
    }

    @Override
    public void onConversationUpdated(Conversation conv) {
        final ConversationViewHeader headerView = (ConversationViewHeader) mConversationContainer
                .findViewById(R.id.conversation_header);

        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 ADD_S
        //Note: initialize starInDatabase from conv, conv is load from database.
        //keep mConversation.starred is changed, we will save it at onDestroyView().
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_S
//        mStarInDatabase = conv.starred;
//        if (mConversation.starred != mStarInDatabase) {
//            conv.starred = mConversation.starred;
//        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_E
        mConversation = conv;
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 ADD_E
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 DEL_S
        /*
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_S
        if(star != null){
            mConversation.starred = star;
        }
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_E
        */
        //TS: junwei-xu 2015-07-07 EMAIL BUGFIX_958223 DEL_E
        if (headerView != null) {
            headerView.onConversationUpdated(conv);
          //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 DEL_S
          //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-921154 MOD_S
            /*String folder = getCurrentFolderTypeDesc();
            if("trash".equals(folder)){
                headerView.setStarTrash();
            }*/
          //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-921154 MOD_E
          //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 DEL_E
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
            int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        boolean sizeChanged = mNeedRender
                && mConversationContainer.getWidth() != 0;
        if (sizeChanged) {
            mNeedRender = false;
            mConversationContainer.removeOnLayoutChangeListener(this);
            renderConversation(getMessageCursor());
        }
    }

    @Override
    public void setMessageDetailsExpanded(MessageHeaderItem i, boolean expanded, int heightBefore) {
        mDiff = (expanded ? 1 : -1) * Math.abs(i.getHeight() - heightBefore);
    }

    /**
     * @return {@code true} because either the Print or Print All menu item is shown in GMail
     */
    @Override
    protected boolean shouldShowPrintInOverflow() {
        return true;
    }

    @Override
    protected void printConversation() {
        //TS: jian.xu 2015-10-09 EMAIL BUGFIX-707702 ADD_S
        if (mActivity == null) {
            return;
        }
        //TS: jian.xu 2015-10-09 EMAIL BUGFIX-707702 ADD_E
        PrintUtils.printConversation(mActivity.getActivityContext(), getMessageCursor(),
                mAddressCache, mConversation.getBaseUri(mBaseUri), true /* useJavascript */);
    }

    // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_S
    @Override
    public void setStarred(boolean star) {
        // TODO Auto-generated method stub
        //TS: junwei-xu 2015-05-15 EMAIL BUGFIX_997081 ADD_S
        mConversation.starred = star;
        //TS: junwei-xu 2015-05-15 EMAIL BUGFIX_997081 ADD_E
    }
    // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_E

    public boolean isDownloadRemaining() {
        return mIsDownloadingRemaining;
    }
    //TS: kaifeng.lu 2016-04-18 EMAIL BUGFIX-1958170 ADD_S
    @Override
    public void onLowMemory() {
        getActivity().finish();
        super.onLowMemory();
    }
    //TS: kaifeng.lu 2016-04-18 EMAIL BUGFIX-1958170 ADD_E
}
