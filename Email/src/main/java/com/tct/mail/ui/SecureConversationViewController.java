/*
 * Copyright (C) 2013 Google Inc.
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
 ===========================================================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== =============================================================
 *BUGFIX-869494  2014/12/31   zhaotianyong    [Android5.0][Email][UE] Show attachments on top screen.
 *BUGFIX-936062  2015/03/02   zhaotianyong    [Android5.0][Email] Cannot play video file in the second mail in combined view.
 *BUGFIX-939448  2015/03/06   zhonghua.tuo    [Android5.0][Email]It will show pictures automatically before we click "show pictures" in combined view
 *BUGFIX-970434  2015/04/10   zhaotianyong    crash when message is not loaded
 *BUGFIX-940964  2015/4/20    gangjin.weng    [Email] Set Dwonload Head Only by default
 *BUGFIX-990076  2015/05/21   zhonghua.tuo    [Android5.0][Email]Show 'Download remaining' when open a unread mail in combined view.
 *BUGFIX-1005432  2015/05/26   zhangchao      [Monitor][Android5.0][Email]All Email account disappear sometimes
 *BUGFIX-1046583  2015/07/21   chaozhang      [jrdlogger]com.tct.email JE
 *FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
 *BUGFIX-845079  2015/11/03   jian.xu         [Android L][Email]Email will ANR or exit automatically when checking the email with US-ASCII charset.
 *BUGFIX-571504  2015/11/11   zheng.zou       [Android][Email][Force close]Email force close after continually rotating screen when reading a mail  in singal account and combined view.
 *FEATURE-854258 2015/11/11   Gantao          [Android L][Email]There are no buttons for reply/reply all/forward at the end of mail in combine account
 ==========================================================================================================
 */

package com.tct.mail.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.tct.email.R;
//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.email.service.EmailServiceUtils;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.email.service.ImapService;
import com.tct.email.service.Pop3Service;
import com.tct.mail.FormattedDateBuilder;
import com.tct.mail.browse.ConversationFooterView;
import com.tct.mail.browse.ConversationMessage;
import com.tct.mail.browse.ConversationReplyFabView;
import com.tct.mail.browse.ConversationViewAdapter;
import com.tct.mail.browse.ConversationViewHeader;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilderCreator;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilderCreatorHolder;
import com.tct.mail.browse.MessageFooterView;
import com.tct.mail.browse.MessageHeaderView;
import com.tct.mail.browse.MessageScrollView;
import com.tct.mail.browse.MessageWebView;
import com.tct.mail.browse.WebViewContextMenu;
import com.tct.mail.browse.ConversationViewAdapter.MessageHeaderItem;
import com.tct.mail.browse.ScrollNotifier.ScrollListener;
import com.tct.mail.print.PrintUtils;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Message;
import com.tct.mail.utils.ConversationViewUtils;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Loader;
import com.tct.emailcommon.service.EmailServiceProxy;
//[FEATURE]-Add-END by TSCD.chao zhang
import com.tct.emailcommon.utility.TextUtilities;

/**
 * Controller to do most of the heavy lifting for
 * {@link SecureConversationViewFragment} and
 * {@link com.tct.mail.browse.EmlMessageViewFragment}. Currently that work
 * is pretty much the rendering logic.
 */
public class SecureConversationViewController implements
        MessageHeaderView.MessageHeaderViewCallbacks, ScrollListener,
        MessageFooterView.MessageFooterCallbacks, ConversationFooterView.ConversationFooterCallbacks{
    private static final String BEGIN_HTML =
            "<body style=\"margin: 0 %spx;\"><div style=\"margin: 16px 0; font-size: 80%%\">";
    private static final String END_HTML = "</div></body>";

    private final SecureConversationViewControllerCallbacks mCallbacks;

    private MessageWebView mWebView;
    private ConversationViewHeader mConversationHeaderView;
    private MessageHeaderView mMessageHeaderView;
    private MessageHeaderView mSnapHeaderView;
    private MessageFooterView mMessageFooterView;
    private ConversationFooterView mConversationFooterView;
    private ConversationMessage mMessage;
    private MessageScrollView mScrollView;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    private ConversationReplyFabView mFabButton;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

    private ConversationViewProgressController mProgressController;
    private FormattedDateBuilder mDateBuilder;

    private int mSideMarginInWebPx;
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,05/10/2014,FR 644789.
    private static final String EXCHANGE_ACCOUNT_MANAGER_TYPE = "com.android.exchange";
    //[FEATURE]-Add-END by TSCD.chao zhang
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,05/10/2014,FR 644789.
    private boolean isEasCall =false;
    private String msgBody =null;
    //[BUGFIX]-Add-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
    private static final int IMAP = 1;
    private static final int POP3 = 2;
    private static final int EAS = 3;
    //[BUGFIX]-Add-END by TSCD.chaozhang
    //[FEATURE]-Add-END by TSCD.chao zhang
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    private static final String IS_DOWNLOADING_REMAINING = "is_downloading_remaining";
    private static final int LOADER_DOWNLOAD_REMAINING = 101;
    private static final String EXTRA_MESSAGE = "message";
    private static final long MSG_NONE_ID = -1;
    private boolean mIsDownloadingRemaining;
    private DownloadRemainCallback mDownloadRemainCallback;
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
    private static final String LOG_TAG = LogTag.getLogTag();

    // TS: yang.mei 2016-03-09 EMAIL BUGFIX_1718656 ADD_S
    private boolean hasRenderContent = false;
    // TS: yang.mei 2016-03-09 EMAIL BUGFIX_1718656 ADD_E

    public SecureConversationViewController(SecureConversationViewControllerCallbacks callbacks) {
        mCallbacks = callbacks;
        mDownloadRemainCallback = new DownloadRemainCallback();  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
    }

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD
    /*
     * Add param isEml to judge if current fragment is EmlMessageViewFragment
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState, boolean isEml) {
        View rootView = inflater.inflate(R.layout.secure_conversation_view, container, false);
        mScrollView = (MessageScrollView) rootView.findViewById(R.id.scroll_view);
        mConversationHeaderView = (ConversationViewHeader) rootView.findViewById(R.id.conv_header);
        mMessageHeaderView = (MessageHeaderView) rootView.findViewById(R.id.message_header);
        mSnapHeaderView = (MessageHeaderView) rootView.findViewById(R.id.snap_header);
        mMessageFooterView = (MessageFooterView) rootView.findViewById(R.id.message_footer);
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
        mConversationFooterView = (ConversationFooterView) rootView.findViewById(R.id.conv_footer);
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        mFabButton = (ConversationReplyFabView) rootView.findViewById(R.id.secure_conversation_view_fab);
        mScrollView.setFabButton(mFabButton);
        mScrollView.setConversationFooterView(mConversationFooterView);
        //If current fragment is Eml fragment,should not show the fab button
        //Also hide the tool blank view ,because it's different activity
        //Also hide the footer buttons
        if (isEml) {
            rootView.findViewById(R.id.tool_blank_view).setVisibility(View.GONE);
            mFabButton.setVisibility(View.GONE);
            mConversationFooterView.setVisibility(View.GONE);
        }
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

        mScrollView.addScrollListener(this);

        // Add color backgrounds to the header and footer.
        // Otherwise the backgrounds are grey. They can't
        // be set in xml because that would add more overdraw
        // in ConversationViewFragment.
        final int color = rootView.getResources().getColor(
                R.color.message_header_background_color);
        mMessageHeaderView.setBackgroundColor(color);
        mSnapHeaderView.setBackgroundColor(color);
        mMessageFooterView.setBackgroundColor(color);
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
        mConversationFooterView.setBackgroundColor(color);
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E

        mProgressController = new ConversationViewProgressController(
                mCallbacks.getFragment(), mCallbacks.getHandler());
        mProgressController.instantiateProgressIndicators(rootView);
        mWebView = (MessageWebView) rootView.findViewById(R.id.webview);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebView.setWebViewClient(mCallbacks.getWebViewClient());
        final InlineAttachmentViewIntentBuilderCreator creator =
                InlineAttachmentViewIntentBuilderCreatorHolder.
                        getInlineAttachmentViewIntentCreator();
        mWebView.setOnCreateContextMenuListener(new WebViewContextMenu(
                mCallbacks.getFragment().getActivity(),
                creator.createInlineAttachmentViewIntentBuilder(null, -1)));
        mWebView.setFocusable(false);
        final WebSettings settings = mWebView.getSettings();

        settings.setJavaScriptEnabled(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        ConversationViewUtils.setTextZoom(mCallbacks.getFragment().getResources(), settings);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        mScrollView.setInnerScrollableView(mWebView);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        mCallbacks.setupConversationHeaderView(mConversationHeaderView);

        final Fragment fragment = mCallbacks.getFragment();

        mDateBuilder = new FormattedDateBuilder(fragment.getActivity());

        //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_S
        mMessageHeaderView.initialize(fragment.getLoaderManager(), fragment.getFragmentManager(),
                mCallbacks.getConversationAccountController(), mCallbacks.getAddressCache());
        //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_E
        mMessageHeaderView.setContactInfoSource(mCallbacks.getContactInfoSource());
        mMessageHeaderView.setCallbacks(this);
        mMessageHeaderView.setExpandable(false);
        mMessageHeaderView.setViewOnlyMode(mCallbacks.isViewOnlyMode());

        mSnapHeaderView.setSnappy();
        //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_S
        mSnapHeaderView.initialize(fragment.getLoaderManager(), fragment.getFragmentManager(),
                mCallbacks.getConversationAccountController(), mCallbacks.getAddressCache());
        //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_E
        mSnapHeaderView.setContactInfoSource(mCallbacks.getContactInfoSource());
        mSnapHeaderView.setCallbacks(this);
        mSnapHeaderView.setExpandable(false);
        mSnapHeaderView.setViewOnlyMode(mCallbacks.isViewOnlyMode());

        mCallbacks.setupMessageHeaderVeiledMatcher(mMessageHeaderView);
        mCallbacks.setupMessageHeaderVeiledMatcher(mSnapHeaderView);

        mMessageFooterView.initialize(fragment.getLoaderManager(), fragment.getFragmentManager(),
                mCallbacks.getConversationAccountController(), this);

     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
        mConversationFooterView.setAccountController(mCallbacks.getConversationAccountController());
        mConversationFooterView.setConversationFooterCallbacks(this);
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E

        mCallbacks.startMessageLoader();

        mProgressController.showLoadingStatus(mCallbacks.isViewVisibleToUser());

        final Resources r = mCallbacks.getFragment().getResources();
        mSideMarginInWebPx = (int) (r.getDimensionPixelOffset(
                R.dimen.conversation_message_content_margin_side) / r.getDisplayMetrics().density);
        // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
        //note:use initLoader to reconnect with the previous loader.
        if (savedInstanceState != null) {
            mIsDownloadingRemaining = savedInstanceState.getBoolean(IS_DOWNLOADING_REMAINING);
        }
        LoaderManager lm = mCallbacks.getFragment().getLoaderManager();
        if (lm.getLoader(LOADER_DOWNLOAD_REMAINING)!=null){
            lm.initLoader(LOADER_DOWNLOAD_REMAINING,null,mDownloadRemainCallback);
        }
        // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
    }

    /**
     * TCT: Add for highlight search keywords.
     */
    public void renderMessage(ConversationMessage message) {
        // TS: yang.mei 2016-03-09 EMAIL BUGFIX_1718656 ADD_S
        if(!hasRenderContent){
            renderMessage(message, null);
            hasRenderContent = true;
        }
        // TS: yang.mei 2016-03-09 EMAIL BUGFIX_1718656 ADD_E
    }

    public void onDestroyView() {
        // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1046583 ADD_S
        //NOTE:It's good idea that after fragment destoried,we release/destory all loaders,it can reduce the asyncTasks,cause the system
        //only supply MAX 128. here means user do not want check this mail,just destory it.
        mCallbacks.getFragment().getLoaderManager().destroyLoader(LOADER_DOWNLOAD_REMAINING);
        // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1046583 ADD_E
        /// M: @{
        mMessageHeaderView.unbind();
        // Release Webview to avoid leak
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
        /// @}
    }

    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_DOWNLOADING_REMAINING,mIsDownloadingRemaining);
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E

    @Override
    public void onNotifierScroll(final int y) {
        // We need to decide whether or not to display the snap header.
        // Get the location of the moveable message header inside the scroll view.
        Rect rect = new Rect();
        mScrollView.offsetDescendantRectToMyCoords(mMessageHeaderView, rect);

        // If we have scrolled further than the distance from the top of the scrollView to the top
        // of the message header, then the message header is at least partially ofscreen. As soon
        // as the message header goes partially offscreen we need to display the snap header.
        // TODO - re-enable when dogfooders howl
//        if (y > rect.top) {
//            mSnapHeaderView.setVisibility(View.VISIBLE);
//        } else {
            mSnapHeaderView.setVisibility(View.GONE);
//        }
    }

    /**
     * Populate the adapter with overlay views (message headers, super-collapsed
     * blocks, a conversation header), and return an HTML document with spacer
     * divs inserted for all overlays.
     */
    public void renderMessage(ConversationMessage message, String query) {
        mMessage = message;
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        mFabButton.setMessageAndBGR(message);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
        if (isEasCall && msgBody != null) {
            mMessage.bodyHtml = msgBody;
            isEasCall = false;
        }
        // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
        ///TCT: update the subject, in case it is change when back from edit draft
        setSubject(mMessage.subject);
        // TS: zhonghua.tuo 2015-03-06 EMAIL BUGFIX_939448 ADD_S
        mWebView.clearCache(true);
        // TS: zhonghua.tuo 2015-03-06 EMAIL BUGFIX_939448 ADD_E
        final boolean alwaysShowImages = mCallbacks.shouldAlwaysShowImages();
        mWebView.getSettings().setBlockNetworkImage(
                !alwaysShowImages && !mMessage.alwaysShowImages);

        // Add formatting to message body
        // At this point, only adds margins.
        StringBuilder dataBuilder = new StringBuilder(
                String.format(BEGIN_HTML, mSideMarginInWebPx));
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 MOD_S
        //NOTE: Use the linked body html, because add link operate maybe block ui thread.
        // the linked body html has been linked when load message for database.
        //dataBuilder.append(mMessage.getBodyAsHtml());
        dataBuilder.append(mMessage.getBodyAsHtmlLinkify());
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 MOD_E
        dataBuilder.append(END_HTML);
        /// TCT: Highlight the query terms, if we are opening an searched result message.
        String htmlToLoad = null;
        if (query != null) {
            htmlToLoad = TextUtilities.highlightTermsInHtml(dataBuilder.toString(), query);
        } else {
            htmlToLoad = dataBuilder.toString();
        }
        mWebView.loadDataWithBaseURL(mCallbacks.getBaseUri(), dataBuilder.toString(),
                "text/html", "utf-8", null);
        if (mMessage!=null){
            LogUtils.d("Email", String.format("test---renderMessage--- messageId=%d conversationUri=%s uri=%s subject=%s",
                    mMessage.id, mMessage.conversationUri, mMessage.uri, mMessage.subject));
        }
        final MessageHeaderItem item = ConversationViewAdapter.newMessageHeaderItem(
                null, mDateBuilder, mMessage, true, mMessage.alwaysShowImages);
        // Clear out the old info from the header before (re)binding
        mMessageHeaderView.unbind();
        mMessageHeaderView.bind(item, false);

     // TS: zhaotianyong 2015-03-02 EMAIL BUGFIX_936062 DEL_S
        //Modify like this because mSnapHeaderView is not use for now.
//        mSnapHeaderView.unbind();
//        mSnapHeaderView.bind(item, false);
     // TS: zhaotianyong 2015-03-02 EMAIL BUGFIX_936062 DEL_E

        if (mMessage.hasAttachments) {
            mMessageFooterView.setVisibility(View.VISIBLE);
            mMessageFooterView.bind(item, false);
        }

     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
        mConversationFooterView.bindToSecureView(item);
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E
    }

    public ConversationMessage getMessage() {
        return mMessage;
    }

    public ConversationViewHeader getConversationHeaderView() {
        return mConversationHeaderView;
    }

    public void dismissLoadingStatus() {
        mProgressController.dismissLoadingStatus();
    }

    public void setSubject(String subject) {
        mConversationHeaderView.setSubject(subject);
    }

    public void printMessage() {
        //TS: zhaotianyong 2015-04-10 EMAIL BUGFIX_970434 MOD_S
        try {
            final Conversation conversation = mMessage.getConversation();
            PrintUtils.printMessage(mCallbacks.getFragment().getActivity(), mMessage,
                    conversation != null ? conversation.subject : mMessage.subject,
                    mCallbacks.getAddressCache(), mCallbacks.getBaseUri(), false /* useJavascript */);
        } catch (NullPointerException e){
            LogUtils.e(LOG_TAG,"Can not print the message now");
        }
        //TS: zhaotianyong 2015-04-10 EMAIL BUGFIX_970434 MOD_E
    }

    // Start MessageHeaderViewCallbacks implementations

    @Override
    public void setMessageSpacerHeight(MessageHeaderItem item, int newSpacerHeight) {
        // Do nothing.
    }

    @Override
    public void setMessageExpanded(MessageHeaderItem item, int newSpacerHeight) {
        // Do nothing.
    }

    @Override
    public void setMessageDetailsExpanded(MessageHeaderItem i, boolean expanded, int heightBefore) {
        // Do nothing.
    }

    @Override
    public void showExternalResources(final Message msg) {
        mWebView.getSettings().setBlockNetworkImage(false);
    }

    @Override
    public void showExternalResources(final String rawSenderAddress) {
        mWebView.getSettings().setBlockNetworkImage(false);
    }

    @Override
    public boolean supportsMessageTransforms() {
        return false;
    }

    @Override
    public String getMessageTransforms(final Message msg) {
        return null;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public FragmentManager getFragmentManager() {
        return mCallbacks.getFragment().getFragmentManager();
    }
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    //we actually let system SyncManager to handle the sync when in exchange .we only
    //care the result. so here we use loop to query the result. when msg flag change
    //to FLAG_LOADED_COMPLETE it means sync complete,and we should set an timeout to
    //make sure can break out the loop.i think 60 seconds is ok.
    //when in pop3 and imap.we try use SyncManager to handle the sync but we can't control
    //the sync reslut.so discard it.
    @Override
    public int  loadSingleMessageBackground(Message msg){
        int status = 0;
        final Context context =  mCallbacks.getFragment().getActivity();
        Account account = Account.getAccountForMessageId(context,msg.getId());
        if (account == null) {
            return  0;
        }
        mMessageHeaderView.showRemainProgress(true);
        mIsDownloadingRemaining = true;
        LoaderManager lm = mCallbacks.getFragment().getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_MESSAGE, msg);
        lm.initLoader(LOADER_DOWNLOAD_REMAINING, bundle, mDownloadRemainCallback);
        /**
        String protocol = account.getProtocol(context);
        EmailServiceInfo info = EmailServiceUtils.getServiceInfo(context, protocol);
        String accountType =info.accountType;
        Log.e("Email.zc","account type:"+accountType);
        //[BUGFIX]-Mod-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
        if(protocol.equals(HostAuth.SCHEME_IMAP)) {
        //status = ImapService.loadUnsyncedMessage(context, msg.getId());
        //requestSyncForOneMessage(context,accountType,account,msg);
        Log.e("Email.zc","imap call LoadMessageAsyncTask");
        LoadMessageAsyncTask syncTask=new LoadMessageAsyncTask(context,IMAP,msg.getId());
        syncTask.execute(msg.getId());
        } else if (protocol.equals(HostAuth.SCHEME_POP3)){
        //status = Pop3Service.loadUnsyncedMessage(context,msg.getId());
        Log.e("Email.zc","imap call LoadMessageAsyncTask");
        LoadMessageAsyncTask syncTask=new LoadMessageAsyncTask(context,POP3,msg.getId());
        syncTask.execute(msg.getId());
        //requestSyncForOneMessage(context,POP3_ACCOUNT_MANAGER_TYPE,account,msg);
        } else if (protocol.equals(HostAuth.SCHEME_EAS)) {
        Log.e("Email.zc","eas call LoadMessageAsyncTask");
        LoadMessageAsyncTask syncTask=new LoadMessageAsyncTask(context,EAS,msg.getId(),account,msg);
        syncTask.execute(msg.getId());**/
        //[BUGFIX]-Mod-END by TSCD.chaozhang
        //To better user experience,we use AyncTask to do sync work.
        //[BUGFIX]-Del-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
      /**  isEasCall = true;
        requestSyncForOneMessage(context,EXCHANGE_ACCOUNT_MANAGER_TYPE,account,msg);
        boolean unSyncComleted = true;
        long currentTime = SystemClock.elapsedRealtime();
        long queryTime = 0;
        boolean notTimeout = true;
        //use a loop to get the sync reslut.can break out by sync complete or timeout.
        while (unSyncComleted && notTimeout) {
            EmailContent.Message localMessage = EmailContent.Message.restoreMessageWithId(context,msg.getId());
            unSyncComleted = localMessage.mFlagLoaded == EmailContent.Message.FLAG_LOADED_COMPLETE? false:true;
            queryTime = SystemClock.elapsedRealtime();
            notTimeout = (queryTime-currentTime) >5000 ? false:true;
        };
        if (!unSyncComleted) {
             Body body=Body.restoreBodyWithMessageId(context,msg.getId());
             msgBody =body.mHtmlContent==null? body.mTextContent : body.mHtmlContent;
             mCallbacks.setEasCallFlag(true);
             mCallbacks.startMessageLoader();
             status =1;
        } else {
            if (!notTimeout) {
                status =0;
            }
            else status = 0;
        }**/
        //[BUGFIX]-Del-END by TSCD.chaozhang
        return status;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,05/10/2014,FR 644789.
    //The method call systemSyncManager to sync one message.by the way,
    //we add DOWNLOAD_FLAG and MESSAGE_ID to bundle in which easSyncHandler
    //need it.
    public static void requestSyncForOneMessage(Context context,String accountType,Account account,Message msg) {
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
    private int loadEasTypeMessage(Context context, Account account, Message msg) {
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
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 MOD_E
    // [BUGFIX]-Add-END by TSCD.chao zhang
    private class DownloadRemainCallback implements LoaderManager.LoaderCallbacks<Long> {
        @Override
        public Loader<Long> onCreateLoader(int id, Bundle args) {
            if (args != null) {
                final Context context = mCallbacks.getFragment().getActivity();
                final Message message = args.getParcelable(EXTRA_MESSAGE);
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
            mCallbacks.getFragment().getLoaderManager().destroyLoader(loader.getId());
            if (data == MSG_NONE_ID) {
                if (mMessageHeaderView != null) {
                    mMessageHeaderView.showRemainProgress(false);
                }
            } else {
                // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1005432 DEL_S
                //NOTE: Performance Optimization. for combindView, here it is, same with conversationFragment,discard the codes. 
                /*long msgId = data;
                if (isEasCall && mCallbacks.getFragment().isAdded()) {
                    // After sync completely,we reinit the loader to load the conversation.
                    Body body = Body.restoreBodyWithMessageId(loader.getContext(), msgId);
                    msgBody = body.mHtmlContent == null ? body.mTextContent
                            : body.mHtmlContent;
                    mCallbacks.setEasCallFlag(true);
                    mCallbacks.startMessageLoader();
                }*/
                if (mMessageHeaderView != null) {
                    mMessageHeaderView.hideRemainView();
                }
                // TS: zhangchao 2015-05-26 EMAIL BUGFIX_1005432 DEL_E
            }
        }

        @Override
        public void onLoaderReset(Loader<Long> loader) {

        }
    }
    //[BUGFIX]-Add-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
    //we use asyncTask to manage the different account type sync.here only eas
    //need to startMessageLoader manually.
    private class LoadMessageAsyncTask extends AsyncTask<Long, Long, Integer> {
        private final Context context;
        private final int accountType;
        private final long msgId;
        private Account account;
        private Message message;

        public LoadMessageAsyncTask(Context cx, int type, long id) {
            context = cx;
            accountType = type;
            msgId = id;
        }

        public LoadMessageAsyncTask(Context cx, int type, long id,
                final Account ac, final Message msg) {
            this(cx, type, id);
            account = ac;
            message = msg;
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
            case IMAP:
                return ImapService.loadUnsyncedMessage(context, msgId);
            case POP3:
                return Pop3Service.loadUnsyncedMessage(context, msgId);
            case EAS:
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
                    Body body = Body.restoreBodyWithMessageId(context, msgId);
                    msgBody = body.mHtmlContent == null ? body.mTextContent
                            : body.mHtmlContent;
                    mCallbacks.setEasCallFlag(true);
                    mCallbacks.startMessageLoader();
                }
                mMessageHeaderView.showRemainProgress(false);
                mMessageHeaderView.hideOrShowRemainMsgInfo(false);
                break;
            }
        }
    }
    //[FEATURE]-Add-END by TSCD.chao zhang
    // End MessageHeaderViewCallbacks implementations

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    public ConversationReplyFabView getFAButton() {
        return mFabButton;
    }
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
    public MessageScrollView getScrollView() {
        return mScrollView;
    }
    @Override
    public void onConversationFooterHeightChange(int newHeight) {
        //do nothing
    }
 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E

    public boolean isDownloadRemaining() {
        return mIsDownloadingRemaining;
    }
}
