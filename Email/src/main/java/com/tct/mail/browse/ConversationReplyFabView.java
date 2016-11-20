package com.tct.mail.browse;

/*
==========================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ==============================
*FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
*BUGFIX-677920  2015/11/11   jian.xu         [Android L][Email]FAB behavior changed with reply all check on or off
===========================================================================
*/
import com.tct.email.R;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.preferences.MailPrefs;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Message;
import com.tct.mail.utils.LogUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
/*
 * FAB button for the feature <Auto hide Action bar>
 */
public class ConversationReplyFabView extends ImageButton implements OnClickListener{

    private Message mMessage;
    private boolean isReply = false;
    private ConversationAccountController mAccountController;

    public ConversationReplyFabView(Context context) {
        this(context, null);
    }

    public ConversationReplyFabView(Context context, AttributeSet atts) {
        super(context, atts);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //TS: tao.gan 2016-01-19 EMAIL BUGFIX-1441212 ADD_S
        if(mMessage == null) {
            LogUtils.i(LogUtils.TAG, "Null message while click the fab.");
            return;
        }
        if(mMessage.msgFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL) {
			//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
            Utility.showShortToast(getContext(), getResources().getString(R.string.mail_not_download_completely));
            return;
        }
        //TS: tao.gan 2016-01-19 EMAIL BUGFIX-1441212 ADD_E
        if (isReply) {
            ComposeActivity.reply(getContext(), getAccount(), mMessage);
        } else {
            ComposeActivity.replyAll(getContext(), getAccount(), mMessage);
        }
    }

    //TS: jian.xu 2015-11-11 EMAIL BUGFIX-677920 MOD_S
    /**
     * Set the current message,and the fab button's background.
     * Only when default reply all is checked in general settings
     * and current message support reply all, and then we use reply all icon
     */
    public void setMessageAndBGR(Message message) {
        mMessage = message;
        boolean defaultReplyAll = MailPrefs.get(getContext()).getDefaultReplyAll();
        boolean supportReplyAll = isSupportReplyAll(message);
        if (supportReplyAll && defaultReplyAll) {
            isReply = false;
            setBackgroundResource(R.drawable.ic_reply_all);
        } else {
            isReply = true;
            setBackgroundResource(R.drawable.ic_reply);
        }
    }
    //TS: jian.xu 2015-11-11 EMAIL BUGFIX-677920 MOD_E

    public void setAccountController (ConversationAccountController accountController) {
        mAccountController = accountController;
    }
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

    private Account getAccount() {
        return mAccountController != null ? mAccountController.getAccount() : null;
    }
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
}
