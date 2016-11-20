/**
 * Copyright (c) 2011, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
/* ----------|----------------------|----------------------|----------------- */
/* 04/22/2014|     Chao Zhang       |      FR 631895 	   |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/* 04/17/2013|     Chao Zhang       |      FR 631895 	   |[HOMO][HOMO][Ora- */
/*           |                      |porting from FR514398 |nge][Homologatio- */
/*           |                      |                      |n] Exchange Acti- */
/*           |                      |                      |ve Sync Priority  */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-869494  2014/12/31   zhaotianyong    [Android5.0][Email][UE] Show attachments on top screen.
 *BUGFIX-884329  2014/01/19   zhaotianyong    [Clone][Email]Download vcs-attachments display "Load iCalendar error"
 *BUGFIX-891429  2014/01/19   zhaotianyong    [Android5.0][Email][UI] The arrow direction is wrong after expanding attachments.
 *BUGFIX-901690  2014/01/19   zhaotianyong    [Android5.0][Email]Can't download attachment again successfully
 *BUGFIX-881446  2015-01-15   wenggangjin     [Email]All attachment disappear after delete one attachment of Email in outbox
 *BUGFIX-913854  2015-01-28   zhaotianyong    [Android5.0][Email] Show a large area after expanding attachments.
 *BUGFIX-920071  2015-02-02   gengkexue       [Monkey][Crash]com.tct.email java.lang.NullPointerException.
 *BUGFIX-936358  2015-03-02   chenyanhua      [Email]The mail attachment doesn't update after edit
 *BUGFIX-907154  2015-03-04   wenggangjin     [Android5.0][Exchange]There is no prompt when we forward an email
 *BUGFIX_938279  2015-03-04   gengkexue       [Android5.0][Email][Arabic] No 'receive time' and 'View details' when open a mail.
 *BUGFIX_954064  2015-03-25   ZhangChao       [Android5.0][Email] Attachment not hide when tap the email title.
 *BUGFIX-962222  2015-03-31   junwei-xu       [Android5.0][Email]There will always be a "!" in eml file
 *BUGFIX-957218  2015/4/1     junwei-xu    [Android5.0][Email]Email can forward before download the attachment.
 *BUGFIX-971901  2015/4/16    junwei-xu       [Android5.0][Email][UE]The toast when we forward an email will last for a long time
 *BUGFIX-940964  2015/4/20    gangjin.weng    [Email] Set Dwonload Head Only by default
 *BUGFIX-988459  2015/05/08   zhaotianyong    [Email]Mail can not be forwarded if the attachment is not supported
 *BUGFIX-998470  2015/05/11   zhaotianyong    [Android5.0][Email]Update the ergo when reply/reply-all/forward email under header only mode.
 *BUGFIX-998526  2015/05/22   zhaotianyong    [Email]Email attachment will overlap the email body during downloading remaining
 *BUGFIX-998526  2015/06/02   Gantao           [Email]Email attachment will overlap the email body during downloading remaining
 *BUGFIX-1001086  2015/06/15   Gantao         [Android5.0][Email] Cannot extend attachments after touch down-arrow icon.
 *BUGFIX-965608  2015/06/19   junwei-xu       [REG][Android5.0][Email]Priority doesn't saved after edit one draft
 *BUGFIX-1030014  2015/06/19   chao-zhang      [HOMO][ALWE] [Email] Email app closed suddenly when reading email (but apparently no crash)
 *BUGFIX-1031646  2015/07/01   xujian         [Android5.0][Email] Still display expand/hide arrow after remove all attachments in Draft.
 *BUGFIX-964831  2015/07/16   xujian          [Android5.0][Email][REG] Hidden content show out after rorate device
 *BUGFIX-964831  2015/08/28   kaifeng.lu      [Android L][Email][Monkey][ANR]ANR in com.tct.email (com.tct.email/.activity.Welcome)
 *BUGFIX-526192  2015-09-01   junwei-xu       From and reply all must be auto display.
 *FEATURE-ID      2015/08/12   Gantao         FEATURE--Always show pictures
 *FEATURE-ID     2015/08/27   Gantao         Horizontal attachment
 *CR_585337      2015-09-29  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-858353c 2015/11/03  zheng.zou       [Email]Optimize Exchange smart-forward/smart-reply
 *BUGFIX-1000731 2015/12/01  jin.dong        [Email][Force close][Monitor]It will pop up email force close when hung up a call when received mails.
 *BUGFIX-1595495 2016/02/29  tianjing.su     [Ergo][Email]Have no the save group icon when enter mail details
 ============================================================================
 */
package com.tct.mail.browse;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.text.BidiFormatter;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.email.activity.setup.MessageSaveGroupDialog;
import com.tct.emailcommon.mail.Address;
import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.email.activity.ConnectionAlertDialog;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.base.Objects;
import com.tct.fw.google.common.collect.Lists;
import com.tct.mail.ContactInfo;
import com.tct.mail.ContactInfoSource;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.browse.AttachmentLoader.AttachmentCursor;
import com.tct.mail.browse.ConfirmDialogFragment.ForwardDialogFragment;
import com.tct.mail.browse.ConversationViewAdapter.MessageHeaderItem;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.perf.Timer;
import com.tct.mail.photomanager.LetterTileProvider;
import com.tct.mail.print.PrintUtils;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.text.EmailAddressSpan;
import com.tct.mail.ui.AbstractConversationViewFragment;
import com.tct.mail.ui.ImageCanvas;
import com.tct.mail.utils.MimeType;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.utils.Utils;
import com.tct.mail.utils.VeiledAddressMatcher;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.utility.Utility;
public class MessageHeaderView extends SnapHeader implements OnClickListener,
        OnMenuItemClickListener, ConversationContainer.DetachListener, LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Cap very long recipient lists during summary construction for efficiency.
     */
    private static final int SUMMARY_MAX_RECIPIENTS = 50;

    private static final int MAX_SNIPPET_LENGTH = 100;

    private static final int SHOW_IMAGE_PROMPT_ONCE = 1;
    private static final int SHOW_IMAGE_PROMPT_ALWAYS = 2;

    private static final String HEADER_RENDER_TAG = "message header render";
    private static final String LAYOUT_TAG = "message header layout";
    private static final String MEASURE_TAG = "message header measure";
    private static final String DIALOG_FRAGMENT_TAG = "save_group_dialog_tag";
    private static final String LOG_TAG = LogTag.getLogTag();

    // This is a debug only feature
    public static final boolean ENABLE_REPORT_RENDERING_PROBLEM = false;

    private static final String DETAILS_DIALOG_TAG = "details-dialog";
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    public static final int LOAD_REMAIN_MESSAGE_SUCCESS= 1;
    public static final int LOAD_REMAIN_MESSAGE_FAIL= 0;
    //[FEATURE]-Add-END by TSCD.chao zhang
    private MessageHeaderViewCallbacks mCallbacks;
    //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 ADD_S
    private LoaderManager mLoaderManager;
    private Integer mOldAttachmentLoaderId;
    private AttachmentCursor mAttachmentsCursor;
    private FragmentManager mFragmentManager;
    //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 ADD_E

    private View mBorderView;
    private ViewGroup mUpperHeaderView;
    private View mTitleContainer;
    private View mSnapHeaderBottomBorder;
    private TextView mSenderNameView;
    private TextView mRecipientSummary;
    private TextView mDateView;
    private View mHideDetailsView;
    private TextView mSnippetView;
    private MessageHeaderContactBadge mPhotoView;
    private ViewGroup mExtraContentView;
    private ViewGroup mExpandedDetailsView;
    private SpamWarningView mSpamWarningView;
    private TextView mImagePromptView;
    private MessageInviteView mInviteView;
    private View mForwardButton;
    private View mOverflowButton;
    private View mDraftIcon;
    private View mEditDraftButton;
    private TextView mUpperDateView;
    private View mReplyButton;
    private View mReplyAllButton;
    private View mAttachmentIcon;
    private final EmailCopyContextMenu mEmailCopyMenu;
    // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 DEL_S
    /*
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    private ImageView mPriority;
    //[FEATURE]-Add-END by TSCD.chao zhang
    */
    // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 DEL_E
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)7
    private long  mId;
    private boolean mPartialDownload = false;
    private LinearLayout mRemainBtnView;
    //[FEATURE]-Add-END by TSCD.chao zhang
    //TS: Gantao 2015-06-16 EMAIL BUGFIX_1013206 ADD_S
    private TextView msgRemainBtn;
    private ProgressBar msgProgress;
    private TextView msgLoading;
    //TS: Gantao 2015-06-16 EMAIL BUGFIX_1013206 ADD_E

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    private LinearLayout mAttachmentBarListLayout;
    private HorizontalScrollView mAttachmentBarScorll;
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

    // temporary fields to reference raw data between initial render and details
    // expansion
    private String[] mFrom;
    private String[] mTo;
    private String[] mCc;
    private String[] mBcc;
    private String[] mReplyTo;

    private boolean mIsDraft = false;

    private int mSendingState;

    private String mSnippet;

    private Address mSender;

    private ContactInfoSource mContactInfoSource;

    private boolean mPreMeasuring;

    private ConversationAccountController mAccountController;

    private Map<String, Address> mAddressCache;

    private boolean mShowImagePrompt;

    private PopupMenu mPopup;

    private MessageHeaderItem mMessageHeaderItem;
    private ConversationMessage mMessage;

    private boolean mRecipientSummaryValid;
    private boolean mExpandedDetailsValid;

    private final LayoutInflater mInflater;

    private AsyncQueryHandler mQueryHandler;

    private boolean mObservingContactInfo;

    /**
     * What I call myself? "me" in English, and internationalized correctly.
     */
    private final String mMyName;

    private final DataSetObserver mContactInfoObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            updateContactInfo();
        }
    };

    private boolean mExpandable = true;

    private VeiledAddressMatcher mVeiledMatcher;

    private boolean mIsViewOnlyMode = false;

    private LetterTileProvider mLetterTileProvider;
    private final int mContactPhotoWidth;
    private final int mContactPhotoHeight;
    private final int mTitleContainerMarginEnd;
    private final int mLinkColorBlue; // AM: Kexue.Geng 2015-03-04 EMAIL BUGFIX_938279

    /**
     * The snappy header has special visibility rules (i.e. no details header,
     * even though it has an expanded appearance)
     */
    private boolean mIsSnappy;

    private BidiFormatter mBidiFormatter;

    public interface MessageHeaderViewCallbacks {
        void setMessageSpacerHeight(MessageHeaderItem item, int newSpacerHeight);

        void setMessageExpanded(MessageHeaderItem item, int newSpacerHeight);

        void setMessageDetailsExpanded(MessageHeaderItem messageHeaderItem, boolean expanded,
                int previousMessageHeaderItemHeight);

        void showExternalResources(Message msg);

        void showExternalResources(String senderRawAddress);

        boolean supportsMessageTransforms();

        String getMessageTransforms(Message msg);

        FragmentManager getFragmentManager();

        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
	    int loadSingleMessageBackground(Message msg);
        //[FEATURE]-Add-END by TSCD.chao zhang

        // TS: Gantao 2016-02-17 EMAIL BUGFIX-1554314 ADD_S
        boolean isDownloadRemaining();
        // TS: Gantao 2016-02-17 EMAIL BUGFIX-1554314 ADD_E

        /**
         * @return <tt>true</tt> if this header is contained within a SecureConversationViewFragment
         * and cannot assume the content is <strong>not</strong> malicious
         */
        boolean isSecure();
    }

    public MessageHeaderView(Context context) {
        this(context, null);
    }

    public MessageHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MessageHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        context = context;
        mIsSnappy = false;
        mEmailCopyMenu = new EmailCopyContextMenu(getContext());
        mInflater = LayoutInflater.from(context);
        mMyName = context.getString(R.string.me_object_pronoun);

        final Resources res = getResources();
        mContactPhotoWidth = res.getDimensionPixelSize(R.dimen.contact_image_width);
        mContactPhotoHeight = res.getDimensionPixelSize(R.dimen.contact_image_height);
        mTitleContainerMarginEnd = res.getDimensionPixelSize(R.dimen.conversation_view_margin_side);
        mLinkColorBlue = res.getColor(R.color.conversation_view_text_color_link_blue); // AM: Kexue.Geng 2015-03-04 EMAIL BUGFIX_938279
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBorderView = findViewById(R.id.message_header_border);
        mUpperHeaderView = (ViewGroup) findViewById(R.id.upper_header);
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        mRemainBtnView = (LinearLayout) findViewById(R.id.msg_remain);
        //[FEATURE]-Add-END by TSCD.chao zhang
        //TS: Gantao 2015-06-16 EMAIL BUGFIX_1013206 ADD_S
        msgRemainBtn = (TextView) findViewById(R.id.msg_remain_btn);
        msgRemainBtn.setOnClickListener(this);
        msgProgress = (ProgressBar) findViewById(R.id.msg_remain_loading_progress);
        msgLoading = (TextView) findViewById(R.id.msg_remain_loading_text);
        //TS: Gantao 2015-06-16 EMAIL BUGFIX_1013206 ADD_E
        mTitleContainer = findViewById(R.id.title_container);
        mSnapHeaderBottomBorder = findViewById(R.id.snap_header_bottom_border);
        mSenderNameView = (TextView) findViewById(R.id.sender_name);
        mRecipientSummary = (TextView) findViewById(R.id.recipient_summary);
        mDateView = (TextView) findViewById(R.id.send_date);
        mDateView.setOnLongClickListener(null); // AM: Kexue.Geng 2015-03-04 EMAIL BUGFIX_938279
        mHideDetailsView = findViewById(R.id.hide_details);
        mSnippetView = (TextView) findViewById(R.id.email_snippet);
        mPhotoView = (MessageHeaderContactBadge) findViewById(R.id.photo);
        mPhotoView.setQuickContactBadge(
                (QuickContactBadge) findViewById(R.id.invisible_quick_contact));
        mReplyButton = findViewById(R.id.reply);
        mReplyAllButton = findViewById(R.id.reply_all);
        mForwardButton = findViewById(R.id.forward);
        mOverflowButton = findViewById(R.id.overflow);
        mDraftIcon = findViewById(R.id.draft);
        mEditDraftButton = findViewById(R.id.edit_draft);
        mUpperDateView = (TextView) findViewById(R.id.upper_date);
        mAttachmentIcon = findViewById(R.id.attachment);
        mExtraContentView = (ViewGroup) findViewById(R.id.header_extra_content);
        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 DEL_S
        /*
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        mPriority = (ImageView)findViewById(R.id.priority_icon);
        //[FEATURE]-Add-END by TSCD.chao zhang
        // TS: junwei-xu 2015-03-31 EMAIL BUGFIX-962222 MOD_S
        if (mPriority != null) {
            mPriority.setImageLevel(Message.FLAG_PRIORITY_NORMAL);
        }
        // TS: junwei-xu 2015-03-31 EMAIL BUGFIX-962222 MOD_E
        */
        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 DEL_E
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
        mAttachmentBarListLayout = (LinearLayout) findViewById(R.id.attachment_bar_lists);
        mAttachmentBarScorll = (HorizontalScrollView) findViewById(R.id.attachment_bar_scroll);
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
        setExpanded(true);

        registerMessageClickTargets(mReplyButton, mReplyAllButton, mForwardButton,
                mEditDraftButton, mOverflowButton, mUpperHeaderView, mDateView, mHideDetailsView);

        mUpperHeaderView.setOnCreateContextMenuListener(mEmailCopyMenu);
    }

    private void registerMessageClickTargets(View... views) {
        for (View v : views) {
            if (v != null) {
                v.setOnClickListener(this);
            }
        }
    }

    @Override
    public void initialize(ConversationAccountController accountController,
            Map<String, Address> addressCache, MessageHeaderViewCallbacks callbacks,
            ContactInfoSource contactInfoSource, VeiledAddressMatcher veiledAddressMatcher) {
        //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_S
        initialize(mLoaderManager, mFragmentManager, accountController, addressCache);
        //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_E
        setCallbacks(callbacks);
        setContactInfoSource(contactInfoSource);
        setVeiledMatcher(veiledAddressMatcher);
    }

    /**
     * Associate the header with a contact info source for later contact
     * presence/photo lookup.
     */
    public void setContactInfoSource(ContactInfoSource contactInfoSource) {
        mContactInfoSource = contactInfoSource;
    }

    public void setCallbacks(MessageHeaderViewCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void setVeiledMatcher(VeiledAddressMatcher matcher) {
        mVeiledMatcher = matcher;
    }

    public boolean isExpanded() {
        // (let's just arbitrarily say that unbound views are expanded by default)
        return mMessageHeaderItem == null || mMessageHeaderItem.isExpanded();
    }

    @Override
    public void onDetachedFromParent() {
        unbind();
    }

    /**
     * Headers that are unbound will not match any rendered header (matches()
     * will return false). Unbinding is not guaranteed to *hide* the view's old
     * data, though. To re-bind this header to message data, call render() or
     * renderUpperHeaderFrom().
     */
    @Override
    public void unbind() {
        mMessageHeaderItem = null;
        mMessage = null;

        if (mObservingContactInfo) {
            mContactInfoSource.unregisterObserver(mContactInfoObserver);
            mObservingContactInfo = false;
        }
    }

    //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_S
    public void initialize(LoaderManager loaderManager, FragmentManager fragmentManager, ConversationAccountController accountController,
            Map<String, Address> addressCache) {
        mLoaderManager = loaderManager;
        mFragmentManager = fragmentManager;
        mAccountController = accountController;
        mAddressCache = addressCache;
    }
    //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_E

    private Account getAccount() {
        return mAccountController != null ? mAccountController.getAccount() : null;
    }

    public void bind(MessageHeaderItem headerItem, boolean measureOnly) {
        if (mMessageHeaderItem != null && mMessageHeaderItem == headerItem) {
            return;
        }

        mMessageHeaderItem = headerItem;
        render(measureOnly);
    }

    // TS: Gantao 2015-06-02 EMAIL BUGFIX-998526 ADD_S
    public void updateHeight(){
        ViewGroup parent = (ViewGroup) getParent();
        if(parent!=null){
            updateSpacerHeight();
        }
    }
    // TS: Gantao 2015-06-02 EMAIL BUGFIX-998526 ADD_E

    /**
     * Rebinds the view to its data. This will only update the view
     * if the {@link MessageHeaderItem} sent as a parameter is the
     * same as the view's current {@link MessageHeaderItem} and the
     * view's expanded state differs from the item's expanded state.
     */
    public void rebind(MessageHeaderItem headerItem) {
        if (mMessageHeaderItem == null || mMessageHeaderItem != headerItem ||
                isActivated() == isExpanded()) {
            return;
        }

        render(false /* measureOnly */);
    }

    @Override
    public void refresh() {
        render(false);
    }

    private BidiFormatter getBidiFormatter() {
        if (mBidiFormatter == null) {
            final ConversationViewAdapter adapter = mMessageHeaderItem != null
                    ? mMessageHeaderItem.getAdapter() : null;
            if (adapter == null) {
                mBidiFormatter = BidiFormatter.getInstance();
            } else {
                mBidiFormatter = adapter.getBidiFormatter();
            }
        }
        return mBidiFormatter;
    }

    private void render(boolean measureOnly) {
        if (mMessageHeaderItem == null) {
            return;
        }

        Timer t = new Timer();
        t.start(HEADER_RENDER_TAG);

        mRecipientSummaryValid = false;
        mExpandedDetailsValid = false;

        mMessage = mMessageHeaderItem.getMessage();

        final Account account = getAccount();
        final boolean alwaysShowImagesForAccount = (account != null) &&
                (account.settings.showImages == Settings.ShowImages.ALWAYS);

        final boolean alwaysShowImagesForMessage = mMessage.shouldShowImagePrompt();

        if (!alwaysShowImagesForMessage) {
            // we don't need the "Show picture" prompt if the user allows images for this message
            mShowImagePrompt = false;
        } else if (mCallbacks.isSecure()) {
            // in a secure view we always display the "Show picture" prompt
            mShowImagePrompt = true;
        } else {
            // otherwise honor the account setting for automatically showing pictures
            mShowImagePrompt = !alwaysShowImagesForAccount;
        }

        setExpanded(mMessageHeaderItem.isExpanded());

        mFrom = mMessage.getFromAddresses();
        mTo = mMessage.getToAddresses();
        mCc = mMessage.getCcAddresses();
        mBcc = mMessage.getBccAddresses();
        mReplyTo = mMessage.getReplyToAddresses();
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        mId = mMessage.getId();
        //[FEATURE]-Add-END by TSCD.chao zhang
        /**
         * Turns draft mode on or off. Draft mode hides message operations other
         * than "edit", hides contact photo, hides presence, and changes the
         * sender name to "Draft".
         */
        mIsDraft = mMessage.draftType != UIProvider.DraftType.NOT_A_DRAFT;
        mSendingState = mMessage.sendingState;
        // TS: chao.zhang 2015-09-29 EMAIL FEATURE-585337 ADD_S
        //NOTE: Only display draft mode during message send failed.
        if (!mIsDraft || (mSendingState != UIProvider.ConversationSendingState.OTHER
                && mSendingState != UIProvider.ConversationSendingState.SEND_ERROR)) {
            mIsDraft = false;
        }
        // TS: chao.zhang 2015-09-29 EMAIL FEATURE-585337 ADD_E
        // If this was a sent message AND:
        // 1. the account has a custom from, the cursor will populate the
        // selected custom from as the fromAddress when a message is sent but
        // not yet synced.
        // 2. the account has no custom froms, fromAddress will be empty, and we
        // can safely fall back and show the account name as sender since it's
        // the only possible fromAddress.
        String from = mMessage.getFrom();
        if (TextUtils.isEmpty(from)) {
            from = (account != null) ? account.getEmailAddress() : "";
        }
        mSender = getAddress(from);

        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 DEL_S
        // TS: junwei-xu 2015-06-19 EMAIL BUGFIX_965608 ADD_S
        /*
        if (mPriority != null) {
            mPriority.setImageLevel(getPriorityOptionValue(mMessage.mPriority));
        }
        */
        /*
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        final Conversation conversation = mMessage.getConversation();
        if (conversation != null) {
              mMessage.mPriority=conversation.flagPriority;
            if(mPriority!=null){
                mPriority.setImageLevel(getPriorityOptionValue(conversation.flagPriority));
            }
             //[FEATURE]-Add-END by TSCD.chao zhang
        }
        */
        // TS: junwei-xu 2015-06-19 EMAIL BUGFIX_965608 ADD_E
        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 DEL_E
        //TS: Gantao 2015-06-15 EMAIL BUGFIX_1001086 MOD_S
        updateChildVisibility(false);
        //TS: Gantao 2015-06-15 EMAIL BUGFIX_1001086 MOD_E

        final String snippet;
        if (mIsDraft || mSendingState != UIProvider.ConversationSendingState.OTHER) {
            snippet = makeSnippet(mMessage.snippet);
        } else {
            snippet = mMessage.snippet;
        }
        mSnippet = snippet == null ? null : getBidiFormatter().unicodeWrap(snippet);

        mSenderNameView.setText(getHeaderTitle());
        setRecipientSummary();
        setDateText();
        mSnippetView.setText(mSnippet);
        setAddressOnContextMenu();

        if (mUpperDateView != null) {
            mUpperDateView.setText(mMessageHeaderItem.getTimestampShort());
        }

      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
        final Integer attachmentLoaderId = getAttachmentLoaderId();

        // Destroy the loader if we are attempting to load a different
        // attachment
        // TS: zhaotianyong 2015-02-17 EMAIL BUGFIX_933187 MOD_S
        // TS: wenggangjin 2015-01-15 EMAIL BUGFIX_881446 MOD_S
        if (mOldAttachmentLoaderId != null
                && (!Objects.equal(mOldAttachmentLoaderId, attachmentLoaderId) || mIsDraft)) {// TS:
                                                                                              // chenyanhua
                                                                                              // 2015-03-02
                                                                                              // EMAIL
                                                                                              // BUGFIX_936358
                                                                                              // MOD
            // TS: wenggangjin 2015-01-15 EMAIL BUGFIX_881446 MOD_E
            // TS: zhaotianyong 2015-02-17 EMAIL BUGFIX_933187 MOD_E // reback
            // the BUGFIX_881446
            mLoaderManager.destroyLoader(mOldAttachmentLoaderId);

            // Resets the footer view. This step is only done if the
            // attachmentsListUri changes so that we don't
            // repeat the work of layout and measure when
            // we're only updating the attachments.
            // TS: Gantao 2015-06-15 EMAIL BUGFIX_1001086 MOD_S
            mAttachmentBarListLayout.removeAllViewsInLayout();
            mAttachmentBarScorll.setVisibility(View.GONE);
        }
        mOldAttachmentLoaderId = attachmentLoaderId;

        // kick off load of Attachment objects in background thread
        // but don't do any Loader work if we're only measuring
        if (!measureOnly && attachmentLoaderId != null) {
            LogUtils.i(LOG_TAG,
                    "binding footer view, calling initLoader for message %d",
                    attachmentLoaderId);
            mLoaderManager.initLoader(attachmentLoaderId, Bundle.EMPTY, this);
        }
        //[Defect]-Del-BEGIN by SCDTABLET.qiao-yang@tcl.com,05/12/2016,2004459,
        if (mAttachmentBarListLayout.getChildCount() == 0 /*&& mMessage.hasAttachments*/) { // TS:
                                                                               // Gantao
                                                                               // 2016-02-23
                                                                               // EMAIL
                                                                               // BUGFIX_1649841
                                                                               // MOD
            renderAttachments(false);
        }
        //[Defect]-Del-END by SCDTABLET.qiao-yang@tcl.com
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

        if (measureOnly) {
            // avoid leaving any state around that would interfere with future regular bind() calls
            unbind();
        } else {
            updateContactInfo();
            if (!mObservingContactInfo) {
                mContactInfoSource.registerObserver(mContactInfoObserver);
                mObservingContactInfo = true;
            }
        }

        t.pause(HEADER_RENDER_TAG);
    }

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    private int getPriorityOptionValue(int index) {
        int[] values = getResources().getIntArray(R.array.set_priority_dialog_options_values);
        if ((index >=0) && (index < values.length)) {
            return values[index];
        } else {
            return -1;
        }
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
    /**
     * check whether the message support Reply all
     */
    private boolean isSupportReplyAll(Message message) {
        boolean supprot = false;

        if (message != null) {
            String[] sendToAddresses = message.getToAddressesUnescaped();
            String[] ccAddresses = message.getCcAddressesUnescaped();
            String[] bccAddresses = message.getBccAddressesUnescaped();
            int sizeSendTo = sendToAddresses != null ? sendToAddresses.length : 0;
            int sizeCc = ccAddresses != null ? ccAddresses.length : 0;
            int sizeBcc = bccAddresses != null ? bccAddresses.length : 0;

            //when the summation for sendToAddresses, ccAddresses and bccAddresses's size over 1.
            //we think it supprot reply all.
            if (sizeSendTo + sizeCc + sizeBcc > 1) {
                supprot = true;
            }
        }

        return supprot;
    }
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E

    /**
     * Update context menu's address field for when the user long presses
     * on the message header and attempts to copy/send email.
     */
    private void setAddressOnContextMenu() {
        if (mSender != null) {
            mEmailCopyMenu.setAddress(mSender.getAddress());
        }
    }

    @Override
    public boolean isBoundTo(ConversationOverlayItem item) {
        return item == mMessageHeaderItem;
    }

    public Address getAddress(String emailStr) {
        return Utils.getAddress(mAddressCache, emailStr);
    }

    protected void updateSpacerHeight() {
        final int h = measureHeight();
        //TS: chaozhang 2015-06-24 EMAIL BUGFIX_1030014 ADD_S
        //After ChromeWebView loaded finished,a callBack will notify to update UI.
        //in some special case,during calutate the MessageHeaderView,
        //nullPointerException will thrown,which made libwebviewchromium crashed
        if (mMessageHeaderItem == null) {
            return;
        }
        //TS: chaozhang 2015-06-24 EMAIL BUGFIX_1030014 ADD_E
        mMessageHeaderItem.setHeight(h);
        if (mCallbacks != null) {
            mCallbacks.setMessageSpacerHeight(mMessageHeaderItem, h);
        }
    }

    private int measureHeight() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) {
            LogUtils.e(LOG_TAG, new Error(), "Unable to measure height of detached header");
            return getHeight();
        }
        mPreMeasuring = true;
        final int h = Utils.measureViewHeight(this, parent);
        mPreMeasuring = false;
        return h;
    }

    private CharSequence getHeaderTitle() {
        CharSequence title;
        switch (mSendingState) {
            case UIProvider.ConversationSendingState.QUEUED:
                title = getResources().getString(R.string.message_in_queued_status);
                break;
            case UIProvider.ConversationSendingState.SENDING:
                title = getResources().getString(R.string.sending);
                break;
            case UIProvider.ConversationSendingState.RETRYING:
                title = getResources().getString(R.string.message_retrying);
                break;
            case UIProvider.ConversationSendingState.SEND_ERROR:
                title = getResources().getString(R.string.message_failed);
                break;
            default:
                if (mIsDraft) {
                    title = SendersView.getSingularDraftString(getContext());
                } else {
                    title = getBidiFormatter().unicodeWrap(
                            getSenderName(mSender));
                }
        }

        return title;
    }

    private void setRecipientSummary() {
        if (!mRecipientSummaryValid) {
            if (mMessageHeaderItem.recipientSummaryText == null) {
                final Account account = getAccount();
                final String meEmailAddress = (account != null) ? account.getEmailAddress() : "";
                mMessageHeaderItem.recipientSummaryText = getRecipientSummaryText(getContext(),
                        meEmailAddress, mMyName, mTo, mCc, mBcc, mAddressCache, mVeiledMatcher,
                        getBidiFormatter());
            }
            mRecipientSummary.setText(mMessageHeaderItem.recipientSummaryText);
            mRecipientSummaryValid = true;
        }
    }

    private void setDateText() {
        if (mIsSnappy) {
            mDateView.setText(mMessageHeaderItem.getTimestampLong());
            mDateView.setOnClickListener(null);
        } else {
            // AM: Kexue.Geng 2015-03-04 EMAIL BUGFIX_938279 MOD_S
            /*
            mDateView.setMovementMethod(LinkMovementMethod.getInstance());
            mDateView.setText(Html.fromHtml(getResources().getString(
                    R.string.date_and_view_details, mMessageHeaderItem.getTimestampLong())));
            StyleUtils.stripUnderlinesAndUrl(mDateView);
             */
            mDateView.setText(Html.fromHtml(getResources().getString(
                    R.string.date_and_view_details_another, mMessageHeaderItem.getTimestampLong(), mLinkColorBlue)));
            // AM: Kexue.Geng 2015-03-04 EMAIL BUGFIX_938279 MOD_E
        }
    }

    /**
     * Return the name, if known, or just the address.
     */
    private static String getSenderName(Address sender) {
        if (sender == null) {
            return "";
        }
        final String displayName = sender.getPersonal();
        return TextUtils.isEmpty(displayName) ? sender.getAddress() : displayName;
    }

    private static void setChildVisibility(int visibility, View... children) {
        for (View v : children) {
            if (v != null) {
                v.setVisibility(visibility);
            }
        }
    }

    private void setExpanded(final boolean expanded) {
        // use View's 'activated' flag to store expanded state
        // child view state lists can use this to toggle drawables
        setActivated(expanded);
        if (mMessageHeaderItem != null) {
            mMessageHeaderItem.setExpanded(expanded);
        }
    }

    //TS: Gantao 2015-06-15 EMAIL BUGFIX_1001086 MOD
    /**
     * Update the visibility of the many child views based on expanded/collapsed
     * and draft/normal state.
     * @param TS:add param isToggleExpanded to indicate if is called by toggleExpanded()
     */
    private void updateChildVisibility(boolean isToggleExpanded) {
        // Too bad this can't be done with an XML state list...

        if (mIsViewOnlyMode) {
            setMessageDetailsVisibility(VISIBLE);
            setChildVisibility(GONE, mSnapHeaderBottomBorder);

            setChildVisibility(GONE, mReplyButton, mReplyAllButton, mForwardButton,
                    mOverflowButton, mDraftIcon, mEditDraftButton,
                    mAttachmentIcon, mUpperDateView, mSnippetView);
            setChildVisibility(VISIBLE, mPhotoView, mRecipientSummary);

            setChildMarginEnd(mTitleContainer, 0);
        } else if (isExpanded()) {
            int normalVis, draftVis;

            final boolean isSnappy = isSnappy();
            setMessageDetailsVisibility((isSnappy) ? GONE : VISIBLE);
            setChildVisibility(isSnappy ? VISIBLE : GONE, mSnapHeaderBottomBorder);

            if (mIsDraft) {
                normalVis = GONE;
                draftVis = VISIBLE;
            } else {
                normalVis = VISIBLE;
                draftVis = GONE;
            }

            setReplyOrReplyAllVisible();
            setChildVisibility(normalVis, mPhotoView, mForwardButton, mOverflowButton);
            setChildVisibility(draftVis, mDraftIcon, mEditDraftButton);
            setChildVisibility(VISIBLE, mRecipientSummary);
            setChildVisibility(GONE, mAttachmentIcon, mUpperDateView, mSnippetView);
            setChildMarginEnd(mTitleContainer, 0);
          //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
            //[Defect]-Del-BEGIN by SCDTABLET.qiao-yang@tcl.com,05/12/2016,2004459,
            //if (mMessage.hasAttachments) {
                setChildVisibility(VISIBLE, mAttachmentBarScorll);
            //}
            //[Defect]-Del-END by SCDTABLET.qiao-yang@tcl.com
          //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
        } else {
            setMessageDetailsVisibility(GONE);
            setChildVisibility(GONE, mSnapHeaderBottomBorder);
            setChildVisibility(VISIBLE, mSnippetView, mUpperDateView);

            setChildVisibility(GONE, mEditDraftButton, mReplyButton, mReplyAllButton,
                    mForwardButton, mOverflowButton, mRecipientSummary,
                    mDateView, mHideDetailsView);

            setChildVisibility(mMessage.hasAttachments ? VISIBLE : GONE,
                    mAttachmentIcon);
          //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
            //[Defect]-Del-BEGIN by SCDTABLET.qiao-yang@tcl.com,05/12/2016,2004459,
            //if (mMessage.hasAttachments) {
                setChildVisibility(GONE, mAttachmentBarScorll);
            //}
            //[Defect]-Del-END by SCDTABLET.qiao-yang@tcl.com
          //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

            if (mIsDraft) {
                setChildVisibility(VISIBLE, mDraftIcon);
                setChildVisibility(GONE, mPhotoView);
            } else {
                setChildVisibility(GONE, mDraftIcon);
                setChildVisibility(VISIBLE, mPhotoView);
            }
            setChildMarginEnd(mTitleContainer, mTitleContainerMarginEnd);
        }
        final ConversationViewAdapter adapter = mMessageHeaderItem.getAdapter();
        if (adapter != null) {
            mBorderView.setVisibility(
                    adapter.isPreviousItemSuperCollapsed(mMessageHeaderItem) ? GONE : VISIBLE);
        } else {
            mBorderView.setVisibility(VISIBLE);
        }
    }

    /**
     * If an overflow menu is present in this header's layout, set the
     * visibility of "Reply" and "Reply All" actions based on a user preference.
     * Only one of those actions will be visible when an overflow is present. If
     * no overflow is present (e.g. big phone or tablet), it's assumed we have
     * plenty of screen real estate and can show both.
     */
    private void setReplyOrReplyAllVisible() {
        if (mIsDraft) {
            setChildVisibility(GONE, mReplyButton, mReplyAllButton);
            return;
        } else if (mOverflowButton == null) {
            setChildVisibility(VISIBLE, mReplyButton, mReplyAllButton);
            return;
        }

        final Account account = getAccount();
        final boolean defaultReplyAll = (account != null) ? account.settings.replyBehavior
                == UIProvider.DefaultReplyBehavior.REPLY_ALL : false;
        setChildVisibility(defaultReplyAll ? GONE : VISIBLE, mReplyButton);
        setChildVisibility(defaultReplyAll ? VISIBLE : GONE, mReplyAllButton);
    }

    @SuppressLint("NewApi")
    private static void setChildMarginEnd(View childView, int marginEnd) {
        MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
        if (Utils.isRunningJBMR1OrLater()) {
            mlp.setMarginEnd(marginEnd);
        } else {
            mlp.rightMargin = marginEnd;
        }
        childView.setLayoutParams(mlp);
    }



    @VisibleForTesting
    static CharSequence getRecipientSummaryText(Context context, String meEmailAddress,
            String myName, String[] to, String[] cc, String[] bcc,
            Map<String, Address> addressCache, VeiledAddressMatcher matcher,
            BidiFormatter bidiFormatter) {

        final RecipientListsBuilder builder = new RecipientListsBuilder(
                context, meEmailAddress, myName, addressCache, matcher, bidiFormatter);

        builder.append(to);
        builder.append(cc);
        builder.append(bcc);

        return builder.build();
    }

    /**
     * Utility class to build a list of recipient lists.
     */
    private static class RecipientListsBuilder {
        private final Context mContext;
        private final String mMeEmailAddress;
        private final String mMyName;
        private final StringBuilder mBuilder = new StringBuilder();
        private final CharSequence mComma;
        private final Map<String, Address> mAddressCache;
        private final VeiledAddressMatcher mMatcher;
        private final BidiFormatter mBidiFormatter;

        int mRecipientCount = 0;
        boolean mFirst = true;

        public RecipientListsBuilder(Context context, String meEmailAddress, String myName,
                Map<String, Address> addressCache, VeiledAddressMatcher matcher,
                BidiFormatter bidiFormatter) {
            mContext = context;
            mMeEmailAddress = meEmailAddress;
            mMyName = myName;
            mComma = mContext.getText(R.string.enumeration_comma);
            mAddressCache = addressCache;
            mMatcher = matcher;
            mBidiFormatter = bidiFormatter;
        }

        public void append(String[] recipients) {
            final int addLimit = SUMMARY_MAX_RECIPIENTS - mRecipientCount;
            final boolean hasRecipients = appendRecipients(recipients, addLimit);
            if (hasRecipients) {
                mRecipientCount += Math.min(addLimit, recipients.length);
            }
        }

        /**
         * Appends formatted recipients of the message to the recipient list,
         * as long as there are recipients left to append and the maximum number
         * of addresses limit has not been reached.
         * @param rawAddrs The addresses to append.
         * @param maxToCopy The maximum number of addresses to append.
         * @return {@code true} if a recipient has been appended. {@code false}, otherwise.
         */
        private boolean appendRecipients(String[] rawAddrs,
                int maxToCopy) {
            if (rawAddrs == null || rawAddrs.length == 0 || maxToCopy == 0) {
                return false;
            }

            final int len = Math.min(maxToCopy, rawAddrs.length);
            for (int i = 0; i < len; i++) {
                final Address email = Utils.getAddress(mAddressCache, rawAddrs[i]);
                final String emailAddress = email.getAddress();
                final String name;
                if (mMatcher != null && mMatcher.isVeiledAddress(emailAddress)) {
                    if (TextUtils.isEmpty(email.getPersonal())) {
                        // Let's write something more readable.
                        name = mContext.getString(VeiledAddressMatcher.VEILED_SUMMARY_UNKNOWN);
                    } else {
                        name = email.getSimplifiedName();
                    }
                } else {
                    // Not a veiled address, show first part of email, or "me".
                    name = mMeEmailAddress.equals(emailAddress) ?
                            mMyName : email.getSimplifiedName();
                }

                // duplicate TextUtils.join() logic to minimize temporary allocations
                if (mFirst) {
                    mFirst = false;
                } else {
                    mBuilder.append(mComma);
                }
                mBuilder.append(mBidiFormatter.unicodeWrap(name));
            }

            return true;
        }

        public CharSequence build() {
            return mContext.getString(R.string.to_message_header, mBuilder);
        }
    }

    private void updateContactInfo() {
        if (mContactInfoSource == null || mSender == null) {
            mPhotoView.setImageToDefault();
            mPhotoView.setContentDescription(getResources().getString(
                    R.string.contact_info_string_default));
            return;
        }

        // Set the photo to either a found Bitmap or the default
        // and ensure either the contact URI or email is set so the click
        // handling works
        String contentDesc = getResources().getString(R.string.contact_info_string,
                !TextUtils.isEmpty(mSender.getPersonal())
                        ? mSender.getPersonal()
                        : mSender.getAddress());
        mPhotoView.setContentDescription(contentDesc);
        boolean photoSet = false;
        final String email = mSender.getAddress();
        final ContactInfo info = mContactInfoSource.getContactInfo(email);
        final Resources res = getResources();
        if (info != null) {
            if (info.contactUri != null) {
                mPhotoView.assignContactUri(info.contactUri);
            } else {
                mPhotoView.assignContactFromEmail(email, true /* lazyLookup */);
            }

            if (info.photo != null) {
                mPhotoView.setImageBitmap(frameBitmapInCircle(info.photo));
                photoSet = true;
            }
        } else {
            mPhotoView.assignContactFromEmail(email, true /* lazyLookup */);
        }

        if (!photoSet) {
            mPhotoView.setImageBitmap(
                    frameBitmapInCircle(makeLetterTile(mSender.getPersonal(), email)));
        }
    }

    private Bitmap makeLetterTile(
            String displayName, String senderAddress) {
        if (mLetterTileProvider == null) {
            mLetterTileProvider = new LetterTileProvider(getContext());
        }

        final ImageCanvas.Dimensions dimensions = new ImageCanvas.Dimensions(
                mContactPhotoWidth, mContactPhotoHeight, ImageCanvas.Dimensions.SCALE_ONE);
        return mLetterTileProvider.getLetterTile(dimensions, displayName, senderAddress);
    }

    /**
     * Frames the input bitmap in a circle.
     */
    private static Bitmap frameBitmapInCircle(Bitmap input) {
        if (input == null) {
            return null;
        }

        // Crop the image if not squared.
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int targetX, targetY, targetSize;
        if (inputWidth >= inputHeight) {
            targetX = inputWidth / 2 - inputHeight / 2;
            targetY = 0;
            targetSize = inputHeight;
        } else {
            targetX = 0;
            targetY = inputHeight / 2 - inputWidth / 2;
            targetSize = inputWidth;
        }

        // Create an output bitmap and a canvas to draw on it.
        Bitmap output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // Create a black paint to draw the mask.
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        // Draw a circle.
        canvas.drawCircle(targetSize / 2, targetSize / 2, targetSize / 2, paint);

        // Replace the black parts of the mask with the input image.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, targetX /* left */, targetY /* top */, paint);

        return output;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mPopup.dismiss();
        return onClick(null, item.getItemId());
    }

    @Override
    public void onClick(View v) {
        onClick(v, v.getId());
    }

    /**
     * Handles clicks on either views or menu items. View parameter can be null
     * for menu item clicks.
     */
    public boolean onClick(final View v, final int id) {
        if (mMessage == null) {
            LogUtils.i(LOG_TAG, "ignoring message header tap on unbound view");
            return false;
        }

        boolean handled = true;

        // TS: zhaotianyong 2015-05-11 EMAIL BUGFIX_998470 MOD_S
        boolean mailDownloadPartial = (mMessage != null && mMessage.msgFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL);
        if (id == R.id.reply) {
            if (mailDownloadPartial) {
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showShortToast(getContext(),getResources().getString(R.string.mail_not_download_completely));
                return true;
            }
            ComposeActivity.reply(getContext(), getAccount(), mMessage);
        } else if (id == R.id.reply_all) {
            if (mailDownloadPartial) {
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showShortToast(getContext(),getResources().getString(R.string.mail_not_download_completely));
                return true;
            }
            ComposeActivity.replyAll(getContext(), getAccount(), mMessage);
        } else if (id == R.id.forward) {
            if (mailDownloadPartial) {
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showShortToast(getContext(),getResources().getString(R.string.mail_not_download_completely));
                return true;
            }
            // TS: zhaotianyong 2015-05-11 EMAIL BUGFIX_998470 MOD_E
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_S
            EmailContent.Attachment[] att = EmailContent.Attachment.restoreAttachmentsWithMessageId(getContext(), mMessage.id);
            boolean allAttachmentsDownloaded = ComposeActivity.allAttachmentIsDownload(att);
            //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 MOD_S
            boolean supportSmartForward = ComposeActivity.supportSmartForward(getAccount());
            if ( !allAttachmentsDownloaded && !supportSmartForward) {
                //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 MOD_E
                ForwardDialogFragment f = ForwardDialogFragment.newInstance(getAccount(), mMessage);
                f.displayDialog(mCallbacks.getFragmentManager());
            } else {
                ComposeActivity.forward(getContext(), getAccount(), mMessage);
            }
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_E
            // TS: tianjing.su 2016-02-29 EMAIL BUGFIX-1595495 MOD_S
        } else if(id == R.id.save_group){
            String[] fromMails=Address.addressParse(mFrom);
            String[] toMails=  Address.addressParse(mTo);
            String[] ccMails=Address.addressParse(mCc);
            String[] bccMails=Address.addressParse(mBcc);
            MessageSaveGroupDialog.newInstance(fromMails, toMails, ccMails, bccMails).show(mFragmentManager, DIALOG_FRAGMENT_TAG);
        }
        // TS: tianjing.su 2016-02-29 EMAIL BUGFIX-1595495 MOD_E
        else if (id == R.id.print_message) {
            printMessage();
        } else if (id == R.id.report_rendering_problem) {
            final String text = getContext().getString(R.string.report_rendering_problem_desc);
            ComposeActivity.reportRenderingFeedback(getContext(), getAccount(), mMessage,
                    text + "\n\n" + mCallbacks.getMessageTransforms(mMessage));
        } else if (id == R.id.report_rendering_improvement) {
            final String text = getContext().getString(R.string.report_rendering_improvement_desc);
            ComposeActivity.reportRenderingFeedback(getContext(), getAccount(), mMessage,
                    text + "\n\n" + mCallbacks.getMessageTransforms(mMessage));
        } else if (id == R.id.edit_draft) {
            ComposeActivity.editDraft(getContext(), getAccount(), mMessage);
        } else if (id == R.id.overflow) {
            if (mPopup == null) {
                mPopup = new PopupMenu(getContext(), v);
                mPopup.getMenuInflater().inflate(R.menu.message_header_overflow_menu,
                        mPopup.getMenu());
                // TS: tianjing.su 2016-02-29 EMAIL BUGFIX-1595495 ADD_S
                mPopup.getMenu().findItem(R.id.save_group).setVisible(PLFUtils.getBoolean(getContext(), "feature_email_save_group"));
                // TS: tianjing.su 2016-02-29 EMAIL BUGFIX-1595495 ADD_E
                mPopup.setOnMenuItemClickListener(this);
            }
            final boolean defaultReplyAll = getAccount().settings.replyBehavior
                    == UIProvider.DefaultReplyBehavior.REPLY_ALL;
            final Menu m = mPopup.getMenu();
            // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 MOD_S
            //Note: check whether support Reply all
            //if support, show it, not support, hide it.
            //m.findItem(R.id.reply).setVisible(defaultReplyAll);
            //m.findItem(R.id.reply_all).setVisible(!defaultReplyAll);
            // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 MOD_E
            m.findItem(R.id.reply_all).setVisible(isSupportReplyAll(mMessage));
            m.findItem(R.id.print_message).setVisible(Utils.isRunningKitkatOrLater());

            final boolean reportRendering = ENABLE_REPORT_RENDERING_PROBLEM
                && mCallbacks.supportsMessageTransforms();
            m.findItem(R.id.report_rendering_improvement).setVisible(reportRendering);
            m.findItem(R.id.report_rendering_problem).setVisible(reportRendering);

            mPopup.show();
        } else if (id == R.id.send_date || id == R.id.hide_details ||
                id == R.id.details_expanded_content) {
            toggleMessageDetails();
        } else if (id == R.id.upper_header) {
            toggleExpanded();
        } else if (id == R.id.show_pictures_text) {
            handleShowImagePromptClick(v);
        //[FEATURE]-MOD-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        } else if(id == R.id.msg_remain_btn){
            doLoadMsgBackground(mMessage);
        //[FEATURE]-MOD-END by TSCD.chao zhang
        }else {
            LogUtils.i(LOG_TAG, "unrecognized header tap: %d", id);
            handled = false;
        }

        if (handled && id != R.id.overflow) {
            Analytics.getInstance().sendMenuItemEvent(Analytics.EVENT_CATEGORY_MENU_ITEM, id,
                    "message_header", 0);
        }

        return handled;
    }

    private void printMessage() {
        // Secure conversation view does not use a conversation view adapter
        // so it's safe to test for existence as a signal to use javascript or not.
        final boolean useJavascript = mMessageHeaderItem.getAdapter() != null;
        final Account account = getAccount();
        final Conversation conversation = mMessage.getConversation();
        final String baseUri =
                AbstractConversationViewFragment.buildBaseUri(getContext(), account, conversation);
        PrintUtils.printMessage(getContext(), mMessage, conversation.subject,
                mAddressCache, conversation.getBaseUri(baseUri), useJavascript);
    }

    /**
     * Set to true if the user should not be able to perform message actions
     * on the message such as reply/reply all/forward/star/etc.
     *
     * Default is false.
     */
    public void setViewOnlyMode(boolean isViewOnlyMode) {
        mIsViewOnlyMode = isViewOnlyMode;
    }

    public void setExpandable(boolean expandable) {
        mExpandable = expandable;
    }

    public void toggleExpanded() {
        if (!mExpandable) {
            return;
        }
        setExpanded(!isExpanded());

        // The snappy header will disappear; no reason to update text.
        if (!isSnappy()) {
            mSenderNameView.setText(getHeaderTitle());
            setRecipientSummary();
            setDateText();
            mSnippetView.setText(mSnippet);
        }

        //TS: Gantao 2015-06-15 EMAIL BUGFIX_1001086 MOD_S
        updateChildVisibility(true);
        //TS: Gantao 2015-06-15 EMAIL BUGFIX_1001086 MOD_E

        // Force-measure the new header height so we can set the spacer size and
        // reveal the message div in one pass. Force-measuring makes it unnecessary to set
        // mSizeChanged.
        int h = measureHeight();
        mMessageHeaderItem.setHeight(h);
        if (mCallbacks != null) {
            mCallbacks.setMessageExpanded(mMessageHeaderItem, h);
        }
    }

    private static boolean isValidPosition(int position, int size) {
        return position >= 0 && position < size;
    }

    @Override
    public void setSnappy() {
        mIsSnappy = true;
        hideMessageDetails();
    }

    private boolean isSnappy() {
        return mIsSnappy;
    }

    private void toggleMessageDetails() {
        int heightBefore = measureHeight();
        final boolean expand =
                (mExpandedDetailsView == null || mExpandedDetailsView.getVisibility() == GONE);
        setMessageDetailsExpanded(expand);
        updateSpacerHeight();
        if (mCallbacks != null) {
            mCallbacks.setMessageDetailsExpanded(mMessageHeaderItem, expand, heightBefore);
        }
    }

    private void setMessageDetailsExpanded(boolean expand) {
        if (expand) {
            showExpandedDetails();
        } else {
            hideExpandedDetails();
        }

        if (mMessageHeaderItem != null) {
            mMessageHeaderItem.detailsExpanded = expand;
        }
    }

    public void setMessageDetailsVisibility(int vis) {
        if (vis == GONE) {
            hideExpandedDetails();
            hideSpamWarning();
            hideShowImagePrompt();
            hideInvite();
            //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
            hideOrShowRemainMsgInfo(false);
            //[FEATURE]-Add-END by TSCD.chao zhang
            mUpperHeaderView.setOnCreateContextMenuListener(null);
        } else {
            setMessageDetailsExpanded(mMessageHeaderItem.detailsExpanded);
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //if(getResources().getBoolean(R.bool.feature_email_account_list_on)){
//            if(PLFUtils.getBoolean(this.getContext(), "feature_email_downloadOptions_on")){
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
                // TS: GanTao 2015-12-1 EMAIL BUGFIX_1117109 MOD_S
                //No need to query databases,Just get the data when we query UI message
                //EmailContent.Message msg = EmailContent.Message.restoreMessageWithId(getContext(), mId);
                showRemainMsgInfo();
                // TS: GanTao 2015-12-1 EMAIL BUGFIX_1117109 MOD_E
//            }
            if (mMessage.spamWarningString == null) {
                hideSpamWarning();
            } else {
                showSpamWarning();
            }
          //TS: Gantao 2015-09-18 EMAIL BUGFIX-571112 MOD
          //"Always show picture form the sender" should show for the feature <Always show images>
            if (mShowImagePrompt) {
                if (mMessageHeaderItem.getShowImages()) {
                    showImagePromptAlways(true);
                } else {
                    showImagePromptOnce();
                }
            } else {
                hideShowImagePrompt();
            }
            if (mMessage.isFlaggedCalendarInvite()) {
                showInvite();
            } else {
                hideInvite();
            }
            mUpperHeaderView.setOnCreateContextMenuListener(mEmailCopyMenu);
        }
    }

    private void hideMessageDetails() {
        setMessageDetailsVisibility(GONE);
    }

    private void hideExpandedDetails() {
        if (mExpandedDetailsView != null) {
            mExpandedDetailsView.setVisibility(GONE);
        }
        mDateView.setVisibility(VISIBLE);
        mHideDetailsView.setVisibility(GONE);
    }

    private void hideInvite() {
        if (mInviteView != null) {
            mInviteView.setVisibility(GONE);
        }
    }

    private void showInvite() {
        if (mInviteView == null) {
            mInviteView = (MessageInviteView) mInflater.inflate(
                    R.layout.conversation_message_invite, this, false);
            mExtraContentView.addView(mInviteView);
        }
        mInviteView.bind(mMessage);
        mInviteView.setVisibility(VISIBLE);
    }
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    public void showRemainProgress(boolean flag) {
     // TS: Gantao 2015-06-11 EMAIL BUGFIX-1013206 MOD_S
//        mRemainBtnView.removeAllViews();

//        View v = mInflater.inflate(R.layout.conversation_view_fragment_remain_btn, null);
//        TextView msgRemainBtn = (TextView) v.findViewById(R.id.msg_remain_btn);
//        msgRemainBtn.setOnClickListener(this);  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
//        //msgRemainBtn.setVisibility(View.GONE);
//
//        ProgressBar msgProgress = (ProgressBar) v.findViewById(R.id.msg_remain_loading_progress);
//        TextView msgLoading = (TextView) v.findViewById(R.id.msg_remain_loading_text);

        makeVisible(msgRemainBtn,!flag);
        makeVisible(msgProgress, flag);
        makeVisible(msgLoading, flag);

//        mRemainBtnView.addView(v);
     // TS: Gantao 2015-06-11 EMAIL BUGFIX-1013206 MOD_E
        hideOrShowRemainMsgInfo(true);
    }
    public void hideOrShowRemainMsgInfo(boolean show) {
         if (mRemainBtnView !=null) {
             if (show){
                 mRemainBtnView.setVisibility(VISIBLE);
             } else mRemainBtnView.setVisibility(GONE);
         }
    }
    public void hideRemainView() {
        if (mRemainBtnView != null) {
         // TS: Gantao 2015-06-11 EMAIL BUGFIX-1013206 DEL_S
//            mRemainBtnView.removeAllViews();
         // TS: Gantao 2015-06-11 EMAIL BUGFIX-1013206 DEL_E
            mRemainBtnView.setVisibility(GONE);
        }
    }
    public void showRemainMsgInfo() {
        // TS: Gantao 2015-06-02 EMAIL BUGFIX-998526 DEL_S
        //[BUGFIX]-Add-BEGIN by TSCD.zhangyang,08/22/2014,PR772845,
        //[Email]"Mark starred" button and "Download Remaining" button are conflicted.
//        ProgressBar msgProgress = (ProgressBar) mRemainBtnView.findViewById(R.id.msg_remain_loading_progress);
//        if (msgProgress != null && msgProgress.getVisibility() == View.VISIBLE) {
//            hideOrShowRemainMsgInfo(true);  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
//            return;
//        }
        //[BUGFIX]-Add-END by TSCD.zhangyang
        // TS: Gantao 2015-06-02 EMAIL BUGFIX-998526 DEL_E
     // TS: Gantao 2015-06-11 EMAIL BUGFIX-1013206 MOD_S
//        mRemainBtnView.removeAllViews();
        if (mMessage == null) {
            return;
        }
        //com.tct.emailcommon.provider.Account account = com.tct.emailcommon.provider.Account.getAccountForMessageId(getContext(),message.mId);
        if (getAccount() == null) {
            return;
        }
        if (mMessage.msgFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL) {
//            View v = mInflater.inflate(R.layout.conversation_view_fragment_remain_btn, null);
//            TextView msgRemainBtn = (TextView) v.findViewById(R.id.msg_remain_btn);
//            TextView msgRemainBtn = (TextView) findViewById(R.id.msg_remain_btn);
            msgRemainBtn.setOnClickListener(this);

            // TS: Gantao 2016-02-17 EMAIL BUGFIX-1554314 MOD_S
            boolean isDownloadRemaining = false;
            if(mCallbacks != null) {
                isDownloadRemaining = mCallbacks.isDownloadRemaining();
            }
            makeVisible(msgRemainBtn, !isDownloadRemaining);
            makeVisible(msgProgress, isDownloadRemaining);
            makeVisible(msgLoading, isDownloadRemaining);
            // TS: Gantao 2016-02-17 EMAIL BUGFIX-1554314 MOD_E
            hideOrShowRemainMsgInfo(true);
            mPartialDownload = true;
        } else if (mMessage.msgFlagLoaded == EmailContent.Message.FLAG_LOADED_COMPLETE) {
            showRemainProgress(false);
            hideOrShowRemainMsgInfo(false);
            mPartialDownload = false;
        }
    }
    private static void makeVisible(View v, boolean visible) {
         final int visibility = visible ? View.VISIBLE : View.GONE;
         if ((v != null) && (v.getVisibility() != visibility)) {
            v.setVisibility(visibility);
         }
    }
    //[FEATURE]-Add-BEGIN by TSNJ.(li.yu),09/02/2015 for FR917870
    Handler romainHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
           switch (msg.what){
            case 0:
                   showRemainProgress(false);
                 break;
            case 1:
                   showRemainProgress(false);
                   hideOrShowRemainMsgInfo(false);
                 break;
        }
      }
    };
    //[FEATURE]-END by TSNJ.(li.yu),
    private void doLoadMsgBackground(final Message msg){
        boolean isEas =false;
        Context context =getContext();
         if (!Utility.hasConnectivity(getContext())) {
            mPartialDownload = false;
            ConnectionAlertDialog.newInstance().show(
                    mCallbacks.getFragmentManager(), "connectionalertdialog");
            return;
         }
         mPartialDownload = true;
         //[BUGFIX]-Del-BEGIN by TSCD.chaozhang,06/04/2014,PR689959
         //[Force Close][Email]The Email stop running after download the body.
         //we update the UI in another class.here is not good.
         /**showRemainProgress(true);
         int status = mCallbacks.loadSingleMessageBackground(msg);
         switch (status) {
         case LOAD_REMAIN_MESSAGE_FAIL:
             showRemainProgress(false);
             break;
         case LOAD_REMAIN_MESSAGE_SUCCESS:
             showRemainProgress(false);
             hideOrShowRemainMsgInfo(false);
             break;
         } **/
         //[BUGFIX]-Del-END by TSCD.chaozhang
         //showRemainProgress(true);
         //com.tct.emailcommon.provider.Account account = com.tct.emailcommon.provider.Account
         //      .getAccountForMessageId(context, msg.getId());
        if (getAccount() == null) {
            return;
        }
        mCallbacks.loadSingleMessageBackground(msg);
    }

    private void hideShowImagePrompt() {
        if (mImagePromptView != null) {
            mImagePromptView.setVisibility(GONE);
        }
    }

    private void showImagePromptOnce() {
        if (mImagePromptView == null) {
            mImagePromptView = (TextView) mInflater.inflate(
                    R.layout.conversation_message_show_pics, this, false);
            mExtraContentView.addView(mImagePromptView);
            mImagePromptView.setOnClickListener(this);
        }
        mImagePromptView.setVisibility(VISIBLE);
        mImagePromptView.setText(R.string.show_images);
        mImagePromptView.setTag(SHOW_IMAGE_PROMPT_ONCE);
    }

    /**
     * Shows the "Always show pictures" message
     *
     * @param initialShowing <code>true</code> if this is the first time we are showing the prompt
     *        for "show images", <code>false</code> if we are transitioning from "Show pictures"
     */
    private void showImagePromptAlways(final boolean initialShowing) {
        if (initialShowing) {
            // Initialize the view
            showImagePromptOnce();
        }

        mImagePromptView.setText(R.string.always_show_images);
        mImagePromptView.setTag(SHOW_IMAGE_PROMPT_ALWAYS);

        if (!initialShowing) {
            // the new text's line count may differ, so update the spacer height
            updateSpacerHeight();
        }
    }

    private void hideSpamWarning() {
        if (mSpamWarningView != null) {
            mSpamWarningView.setVisibility(GONE);
        }
    }

    private void showSpamWarning() {
        if (mSpamWarningView == null) {
            mSpamWarningView = (SpamWarningView)
                    mInflater.inflate(R.layout.conversation_message_spam_warning, this, false);
            mExtraContentView.addView(mSpamWarningView);
        }

        mSpamWarningView.showSpamWarning(mMessage, mSender);
    }

    private void handleShowImagePromptClick(View v) {
        Integer state = (Integer) v.getTag();
        if (state == null) {
            return;
        }
        switch (state) {
            case SHOW_IMAGE_PROMPT_ONCE:
                if (mCallbacks != null) {
                    mCallbacks.showExternalResources(mMessage);
                }
                if (mMessageHeaderItem != null) {
                    mMessageHeaderItem.setShowImages(true);
                }
                //TS: Gantao 2015-09-18 EMAIL BUGFIX-571112 MOD
                //"Always show picture form the sender" should show for the feature <Always show images>
                if (mIsViewOnlyMode) {
                    hideShowImagePrompt();
                } else {
                    showImagePromptAlways(false);
                }
                break;
            case SHOW_IMAGE_PROMPT_ALWAYS:
                mMessage.markAlwaysShowImages(getQueryHandler(), 0 /* token */, null /* cookie */);

                if (mCallbacks != null) {
                    mCallbacks.showExternalResources(mMessage.getFrom());
                }

                mShowImagePrompt = false;
                v.setTag(null);
                v.setVisibility(GONE);
                updateSpacerHeight();
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
				Utility.showShortToast(getContext(), R.string.always_show_images_toast);
                //Toast.makeText(getContext(), R.string.always_show_images_toast, Toast.LENGTH_SHORT)
                //        .show();
        }
    }

    private AsyncQueryHandler getQueryHandler() {
        if (mQueryHandler == null) {
            mQueryHandler = new AsyncQueryHandler(getContext().getContentResolver()) {};
        }
        return mQueryHandler;
    }

    /**
     * Makes expanded details visible. If necessary, will inflate expanded
     * details layout and render using saved-off state (senders, timestamp,
     * etc).
     */
    private void showExpandedDetails() {
        // lazily create expanded details view
        final boolean expandedViewCreated = ensureExpandedDetailsView();
        if (expandedViewCreated) {
            mExtraContentView.addView(mExpandedDetailsView, 0);
        }
        mExpandedDetailsView.setVisibility(VISIBLE);
        mDateView.setVisibility(GONE);
        mHideDetailsView.setVisibility(VISIBLE);
    }

    private boolean ensureExpandedDetailsView() {
        boolean viewCreated = false;
        if (mExpandedDetailsView == null) {
            View v = inflateExpandedDetails(mInflater);
            v.setOnClickListener(this);

            mExpandedDetailsView = (ViewGroup) v;
            viewCreated = true;
        }
        if (!mExpandedDetailsValid) {
            renderExpandedDetails(getResources(), mExpandedDetailsView, mMessage.viaDomain,
                    mAddressCache, getAccount(), mVeiledMatcher, mFrom, mReplyTo, mTo, mCc, mBcc,
                    mMessageHeaderItem.getTimestampFull(),
                    getBidiFormatter());

            mExpandedDetailsValid = true;
        }
        return viewCreated;
    }

    public static View inflateExpandedDetails(LayoutInflater inflater) {
        return inflater.inflate(R.layout.conversation_message_header_details, null, false);
    }

    public void renderExpandedDetails(Resources res, View detailsView,
            String viaDomain, Map<String, Address> addressCache, Account account,
            VeiledAddressMatcher veiledMatcher, String[] from, String[] replyTo,
            String[] to, String[] cc, String[] bcc, CharSequence receivedTimestamp,
            BidiFormatter bidiFormatter) {
        renderEmailList(res, R.id.from_heading, R.id.from_details, from, viaDomain,
                detailsView, addressCache, account, veiledMatcher, bidiFormatter);
        renderEmailList(res, R.id.replyto_heading, R.id.replyto_details, replyTo, viaDomain,
                detailsView, addressCache, account, veiledMatcher, bidiFormatter);
        renderEmailList(res, R.id.to_heading, R.id.to_details, to, viaDomain,
                detailsView, addressCache, account, veiledMatcher, bidiFormatter);
        renderEmailList(res, R.id.cc_heading, R.id.cc_details, cc, viaDomain,
                detailsView, addressCache, account, veiledMatcher, bidiFormatter);
        renderEmailList(res, R.id.bcc_heading, R.id.bcc_details, bcc, viaDomain,
                detailsView, addressCache, account, veiledMatcher, bidiFormatter);

        // Render date
        detailsView.findViewById(R.id.date_heading).setVisibility(VISIBLE);
        final TextView date = (TextView) detailsView.findViewById(R.id.date_details);
        date.setText(receivedTimestamp);
        date.setVisibility(VISIBLE);
    }

    /**
     * Render an email list for the expanded message details view.
     */
    private void renderEmailList(Resources res, int headerId, int detailsId,
            String[] emails, String viaDomain, View rootView,
            Map<String, Address> addressCache, Account account,
            VeiledAddressMatcher veiledMatcher, BidiFormatter bidiFormatter) {
        if (emails == null || emails.length == 0) {
            return;
        }
        final String[] formattedEmails = new String[emails.length];
        for (int i = 0; i < emails.length; i++) {
            final Address email = Utils.getAddress(addressCache, emails[i]);
            String name = email.getPersonal();
            final String address = email.getAddress();
            // Check if the address here is a veiled address.  If it is, we need to display an
            // alternate layout
            final boolean isVeiledAddress = veiledMatcher != null &&
                    veiledMatcher.isVeiledAddress(address);
            final String addressShown;
            if (isVeiledAddress) {
                // Add the warning at the end of the name, and remove the address.  The alternate
                // text cannot be put in the address part, because the address is made into a link,
                // and the alternate human-readable text is not a link.
                addressShown = "";
                if (TextUtils.isEmpty(name)) {
                    // Empty name and we will block out the address. Let's write something more
                    // readable.
                    name = res.getString(VeiledAddressMatcher.VEILED_ALTERNATE_TEXT_UNKNOWN_PERSON);
                } else {
                    name = name + res.getString(VeiledAddressMatcher.VEILED_ALTERNATE_TEXT);
                }
            } else {
                addressShown = address;
            }
            if (name == null || name.length() == 0 || name.equalsIgnoreCase(addressShown)) {
                formattedEmails[i] = bidiFormatter.unicodeWrap(addressShown);
            } else {
                // The one downside to having the showViaDomain here is that
                // if the sender does not have a name, it will not show the via info
                if (viaDomain != null) {
                    formattedEmails[i] = res.getString(
                            R.string.address_display_format_with_via_domain,
                            bidiFormatter.unicodeWrap(name),
                            bidiFormatter.unicodeWrap(addressShown),
                            bidiFormatter.unicodeWrap(viaDomain));
                } else {
                    formattedEmails[i] = res.getString(R.string.address_display_format,
                            bidiFormatter.unicodeWrap(name),
                            bidiFormatter.unicodeWrap(addressShown));
                }
            }
        }

        rootView.findViewById(headerId).setVisibility(VISIBLE);
        final TextView detailsText = (TextView) rootView.findViewById(detailsId);
        detailsText.setText(TextUtils.join("\n", formattedEmails));
        stripUnderlines(detailsText, account);
        detailsText.setVisibility(VISIBLE);
    }

    private void stripUnderlines(TextView textView, Account account) {
        final Spannable spannable = (Spannable) textView.getText();
        final URLSpan[] urls = textView.getUrls();

        for (URLSpan span : urls) {
            final int start = spannable.getSpanStart(span);
            final int end = spannable.getSpanEnd(span);
            spannable.removeSpan(span);
            //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 MOD_S
            span = new EmailAddressSpan(getContext(), account, span.getURL().substring(7), mContactInfoSource);
            ((EmailAddressSpan) span).setFragmentManager(mFragmentManager);
            //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 MOD_E
            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * Returns a short plaintext snippet generated from the given HTML message
     * body. Collapses whitespace, ignores '&lt;' and '&gt;' characters and
     * everything in between, and truncates the snippet to no more than 100
     * characters.
     *
     * @return Short plaintext snippet
     */
    @VisibleForTesting
    static String makeSnippet(final String messageBody) {
        if (TextUtils.isEmpty(messageBody)) {
            return null;
        }

        final StringBuilder snippet = new StringBuilder(MAX_SNIPPET_LENGTH);

        final StringReader reader = new StringReader(messageBody);
        try {
            int c;
            while ((c = reader.read()) != -1 && snippet.length() < MAX_SNIPPET_LENGTH) {
                // Collapse whitespace.
                if (Character.isWhitespace(c)) {
                    snippet.append(' ');
                    do {
                        c = reader.read();
                    } while (Character.isWhitespace(c));
                    if (c == -1) {
                        break;
                    }
                }

                if (c == '<') {
                    // Ignore everything up to and including the next '>'
                    // character.
                    while ((c = reader.read()) != -1) {
                        if (c == '>') {
                            break;
                        }
                    }

                    // If we reached the end of the message body, exit.
                    if (c == -1) {
                        break;
                    }
                } else if (c == '&') {
                    // Read HTML entity.
                    StringBuilder sb = new StringBuilder();

                    while ((c = reader.read()) != -1) {
                        if (c == ';') {
                            break;
                        }
                        sb.append((char) c);
                    }

                    String entity = sb.toString();
                    if ("nbsp".equals(entity)) {
                        snippet.append(' ');
                    } else if ("lt".equals(entity)) {
                        snippet.append('<');
                    } else if ("gt".equals(entity)) {
                        snippet.append('>');
                    } else if ("amp".equals(entity)) {
                        snippet.append('&');
                    } else if ("quot".equals(entity)) {
                        snippet.append('"');
                    } else if ("apos".equals(entity) || "#39".equals(entity)) {
                        snippet.append('\'');
                    } else {
                        // Unknown entity; just append the literal string.
                        snippet.append('&').append(entity);
                        if (c == ';') {
                            snippet.append(';');
                        }
                    }

                    // If we reached the end of the message body, exit.
                    if (c == -1) {
                        break;
                    }
                } else {
                    // The current character is a non-whitespace character that
                    // isn't inside some
                    // HTML tag and is not part of an HTML entity.
                    snippet.append((char) c);
                }
            }
        } catch (IOException e) {
            LogUtils.wtf(LOG_TAG, e, "Really? IOException while reading a freaking string?!? ");
        }

        return snippet.toString();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Timer perf = new Timer();
        perf.start(LAYOUT_TAG);
        super.onLayout(changed, l, t, r, b);
        perf.pause(LAYOUT_TAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Timer t = new Timer();
        if (Timer.ENABLE_TIMER && !mPreMeasuring) {
            t.count("header measure id=" + mMessage.id);
            t.start(MEASURE_TAG);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mPreMeasuring) {
            t.pause(MEASURE_TAG);
        }
    }

    //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 ADD_S
    private void renderAttachments(boolean loaderResult) {
        final List<Attachment> attachments;
        if (mAttachmentsCursor != null && !mAttachmentsCursor.isClosed()) {
            int i = -1;
            attachments = Lists.newArrayList();
            while (mAttachmentsCursor.moveToPosition(++i)) {
                attachments.add(mAttachmentsCursor.get());
            }
        } else {
            // before the attachment loader results are in, we can still render immediately using
            // the basic info in the message's attachmentsJSON
            attachments = mMessageHeaderItem.getMessage().getAttachments();
        }
        //TS: Gantao 2015-11-10 EMAIL BUGFIX_871525 ADD_S
        //In case of NullPointerException
        if (mMessageHeaderItem == null) {
            return;
        }
        //TS: Gantao 2015-11-10 EMAIL BUGFIX_871525 ADD_E
        renderAttachments(attachments, loaderResult);
        // if message is no expanded, hide the attachment bar scroll view.
        if(!isExpanded()) {
            setChildVisibility(GONE, mAttachmentBarScorll);
        }
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    private void renderAttachments(List<Attachment> attachments, boolean loaderResult) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        // filter the attachments into tiled and non-tiled
        final int maxSize = attachments.size();
        final List<Attachment> barAttachments = new ArrayList<Attachment>(maxSize);
        for (Attachment attachment : attachments) {
            // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID MOD_S
            //We don't show inline attachments now.
            //[Defect]-Del-BEGIN by SCDTABLET.qiao-yang@tcl.com,05/12/2016,2004459,
            //if (TextUtils.isEmpty(attachment.contentId) || attachment.isStandardAttachment()) {
                barAttachments.add(attachment);
            //}
            //[Defect]-Del-END by SCDTABLET.qiao-yang@tcl.com
            // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID MOD_E
        }

        // AM: Kexue.Geng 2015-01-02 EMAIL BUGFIX_920071 MOD_S
        // mMessageHeaderItem.getMessage().attachmentsJson = Attachment.toJSONArray(attachments);
        ConversationMessage cmg = mMessageHeaderItem.getMessage();
        if (cmg != null) {
            cmg.attachmentsJson = Attachment.toJSONArray(attachments);
        }
        // AM: Kexue.Geng 2015-01-02 EMAIL BUGFIX_920071 MOD_E
        // All attachments are inline, don't display anything.
        if (barAttachments.isEmpty()) {
            return;
        }
        renderBarAttachments(barAttachments, loaderResult);
    }

    private void renderBarAttachments(List<Attachment> barAttachments,
            boolean loaderResult) {
        mAttachmentBarScorll.setVisibility(VISIBLE);

        final Account account = getAccount();
        for (Attachment attachment : barAttachments) {
            renderBarAttachments(attachment, account, loaderResult);
        }
    }

    private void renderBarAttachments(Attachment attachment, Account account, boolean loaderResult){
        final Uri id = attachment.getIdentifierUri();
        MessageAttachmentBar barAttachmentView =
                (MessageAttachmentBar) mAttachmentBarListLayout.findViewWithTag(id);

        if (barAttachmentView == null) {
            barAttachmentView = MessageAttachmentBar.inflate(mInflater, this);
            barAttachmentView.setTag(id);
            barAttachmentView.initialize(mFragmentManager);
            barAttachmentView.setThumbnailDefault(true);
            MimeType.setThumbnailBackground(barAttachmentView.getThunmbnailView(),attachment.getContentType());
            mAttachmentBarListLayout.addView(barAttachmentView);
        }
        //TS: zheng.zou 2015-12-01 EMAIL BUGFIX_1000731 MOD_S
        if (mMessageHeaderItem != null) {
            barAttachmentView.render(attachment, account, mMessageHeaderItem.getMessage(),
                    loaderResult, getBidiFormatter());
        }
        //TS: zheng.zou 2015-12-01 EMAIL BUGFIX_1000731 MOD_E
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AttachmentLoader(getContext(),
                mMessageHeaderItem.getMessage().attachmentListUri);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        mAttachmentsCursor = (AttachmentCursor) data;

        if (mAttachmentsCursor == null || mAttachmentsCursor.isClosed()) {
            return;
        }

        renderAttachments(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAttachmentsCursor = null;
    }
    private Integer getAttachmentLoaderId() {
            Integer id = null;
            final Message msg = mMessageHeaderItem == null ? null : mMessageHeaderItem.getMessage();
            if (msg != null && /*msg.hasAttachments && */msg.attachmentListUri != null) {//[Defect]-Del-BEGIN by SCDTABLET.qiao-yang@tcl.com,05/12/2016,2004459,
                id = msg.attachmentListUri.hashCode();
            }
            return id;
        }
    //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 ADD_E

}
