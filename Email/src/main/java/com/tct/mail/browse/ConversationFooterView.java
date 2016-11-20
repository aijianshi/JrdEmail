/**
*===================================================================================================================
*HISTORY
*
*Tag             Date        Author          Description
*============== ============ =============== =======================================================================
*BUGFIX-868939   2014/12/26  wenggangjin     [Android5.0][Exchange]It's too slow to forward mails successfully
*BUGFIX-957218  2015/4/1     junwei-xu    [Android5.0][Email]Email can forward before download the attachment.
*BUGFIX-971901  2015/4/16    yanhua.chen     [Android5.0][Email][UE]The toast when we forward an email will last for a long time
*BUGFIX-988459  2015/05/08   zhaotianyong    [Email]Mail can not be forwarded if the attachment is not supported
*BUGFIX-998470  2015/05/11   zhaotianyong    [Android5.0][Email]Update the ergo when reply/reply-all/forward email under header only mode.
*BUGFIX-526192  2015-09-06   junwei-xu       From and reply all must be auto display.
*BUGFIX-858353c 2015/11/03  zheng.zou       [Email]Optimize Exchange smart-forward/smart-reply
*FEATURE-854258 2015/11/11   Gantao          [Android L][Email]There are no buttons for reply/reply all/forward at the end of mail in combine account
====================================================================================================================
*/
package com.tct.mail.browse;

import java.util.List;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.email.R;
import com.tct.emailcommon.provider.EmailContent;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_971901 MOD_S
import com.tct.emailcommon.utility.Utility;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_971901 MOD_E
import com.tct.mail.browse.ConfirmDialogFragment.ForwardDialogFragment;
import com.tct.mail.browse.ConversationViewAdapter.ConversationFooterItem;
import com.tct.mail.browse.ConversationViewAdapter.MessageHeaderItem;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;
/**
 * A view placed at the bottom of the conversation view that allows the user to
 * reply/reply all/forward to the last message in the conversation.
 */
public class ConversationFooterView extends LinearLayout implements View.OnClickListener {

    public interface ConversationFooterCallbacks {
        /**
         * Called when the height of the {@link ConversationFooterView} changes.
         *
         * @param newHeight the new height in px
         */
        void onConversationFooterHeightChange(int newHeight);

        // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 ADD_S
        FragmentManager getFragmentManager();
        // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 ADD_E
    }
    private static final String LOG_TAG = LogTag.getLogTag();

    private ConversationFooterItem mFooterItem;
 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
    //Used in combined view
    private MessageHeaderItem mHeaderItem;
    boolean isCombinedView = false;
 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E
    private ConversationAccountController mAccountController;
    private ConversationFooterCallbacks mCallbacks;

    private View mFooterButtons;
    // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_S
    private TextView replyAllButton;
    // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_E

    public ConversationFooterView(Context context) {
        super(context);
    }

    public ConversationFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConversationFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFooterButtons = findViewById(R.id.footer_buttons);

        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_S
        replyAllButton = (TextView) findViewById(R.id.reply_all_button);
        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_E
        findViewById(R.id.reply_button).setOnClickListener(this);
        findViewById(R.id.reply_all_button).setOnClickListener(this);
        findViewById(R.id.forward_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 MOD_S
        Message message;
        if (!isCombinedView) {
            //Single account view tap on
            if (mFooterItem == null) {
                LogUtils.i(LOG_TAG, "ignoring conversation footer tap on unbound view");
                return;
            }
            final MessageHeaderItem headerItem = mFooterItem.getLastMessageHeaderItem();
            if (headerItem == null) {
                LogUtils.i(LOG_TAG, "ignoring conversation footer tap on null header item");
                return;
            }
            message = headerItem.getMessage();
        } else {
            // Combined view tap on
            if (mHeaderItem == null) {
                LogUtils.i(LOG_TAG, "ignoring conversation footer tap on null header item");
                return;
            }
            message = mHeaderItem.getMessage();
        }
     // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 MOD_E

        if (message == null) {
            LogUtils.i(LOG_TAG, "ignoring conversation footer tap on null message");
            return;
        }
        final int id = v.getId();
        // TS: zhaotianyong 2015-05-11 EMAIL BUGFIX_998470 MOD_S
        EmailContent.Message msg = EmailContent.Message.restoreMessageWithId(getContext(), message.id);
        boolean mailDownloadPartial = (msg != null && msg.mFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL);
        if (id == R.id.reply_button) {
            if (mailDownloadPartial) {
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showShortToast(getContext(),getResources().getString(R.string.mail_not_download_completely));
                return;
            }
            ComposeActivity.reply(getContext(), getAccount(), message);
        } else if (id == R.id.reply_all_button) {
            if (mailDownloadPartial) {
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showShortToast(getContext(),getResources().getString(R.string.mail_not_download_completely));
                return;
            }
            ComposeActivity.replyAll(getContext(), getAccount(), message);
        } else if (id == R.id.forward_button) {
            if (mailDownloadPartial) {
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showShortToast(getContext(),getResources().getString(R.string.mail_not_download_completely));
                return;
            }
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 ADD_S
            EmailContent.Attachment[] att = EmailContent.Attachment.restoreAttachmentsWithMessageId(getContext(), message.id);
            boolean allAttachmentsDownloaded = ComposeActivity.allAttachmentIsDownload(att);
            //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 MOD_S
            boolean supportSmartForward = ComposeActivity.supportSmartForward(getAccount());
            if (!allAttachmentsDownloaded && !supportSmartForward) {
                //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 MOD_E
                ForwardDialogFragment f = ForwardDialogFragment.newInstance(getAccount(), message);
                f.displayDialog(mCallbacks.getFragmentManager());
            } else {
                ComposeActivity.forward(getContext(), getAccount(), message);
            }
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 ADD_E
        }
        // TS: zhaotianyong 2015-05-11 EMAIL BUGFIX_998470 MOD_E
    }

    public void bind(ConversationFooterItem footerItem) {
        mFooterItem = footerItem;

        if (mFooterItem == null) {
            LogUtils.i(LOG_TAG, "ignoring conversation footer tap on unbound view");
            return;
        }
        final MessageHeaderItem headerItem = mFooterItem.getLastMessageHeaderItem();
        if (headerItem == null) {
            LogUtils.i(LOG_TAG, "ignoring conversation footer tap on null header item");
            return;
        }
        final Message message = headerItem.getMessage();
        if (message == null) {
            LogUtils.i(LOG_TAG, "ignoring conversation footer tap on null message");
            return;
        }

        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_S
        // hide Reply all if current message does not support it
        boolean isSupportReplyAll = isSupportReplyAll(message);
        replyAllButton.setVisibility(isSupportReplyAll ? View.VISIBLE : View.GONE);
        // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_E
        // hide the footer icons
        mFooterButtons.setVisibility(message.isDraft() ? GONE : VISIBLE);
    }

    public void rebind(ConversationFooterItem footerItem) {
        bind(footerItem);

        if (mFooterItem != null) {
            final int h = measureHeight();
            if (mFooterItem.setHeight(h)) {
                mCallbacks.onConversationFooterHeightChange(h);
            }
        }
    }

 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
    /*
     * Bind when we are secure view, because we can't get footer item in secure view,
     * the parameter change to message header item.
     */
    public void bindToSecureView(MessageHeaderItem headerItem) {
        isCombinedView = true;
        mHeaderItem = headerItem;

        if (mHeaderItem == null) {
            LogUtils.i(LOG_TAG, "ignoring conversation footer tap on unbound view");
            return;
        }
        final Message message = headerItem.getMessage();
        if (message == null) {
            LogUtils.i(LOG_TAG, "ignoring conversation footer tap on null message");
            return;
        }

        // hide Reply all if current message does not support it
        boolean isSupportReplyAll = isSupportReplyAll(message);
        replyAllButton.setVisibility(isSupportReplyAll ? View.VISIBLE : View.GONE);
        // hide the footer icons
        mFooterButtons.setVisibility(message.isDraft() ? GONE : VISIBLE);
    }
 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E

    // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_S
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
    // TS: junwei-xu 2015-09-06 EMAIL BUGFIX-526192 ADD_E

    private int measureHeight() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) {
            LogUtils.e(LOG_TAG, "Unable to measure height of conversation header");
            return getHeight();
        }
        final int h = Utils.measureViewHeight(this, parent);
        return h;
    }

    public void setAccountController(ConversationAccountController accountController) {
        mAccountController = accountController;
    }

    public void setConversationFooterCallbacks(ConversationFooterCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    private Account getAccount() {
        return mAccountController != null ? mAccountController.getAccount() : null;
    }
}
