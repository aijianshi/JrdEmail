/******************************************************************************/
/*                                                               Date:06/2013 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  Dandan.Fang                                                     */
/*  Email  :                                                                  */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :                                                                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 06/03/2013|     Dandan.Fang      |       PR460400       |[wap push&CP]The- */
/*           |                      |                      |re is no email a- */
/*           |                      |                      |ccount after rec- */
/*           |                      |                      |eive a CP that c- */
/*           |                      |                      |ontain POP3 prof- */
/*           |                      |                      |ile               */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-924451  2015/02/05   zhaotianyong    [TMO][HOMO TMO ID 51569][OMA CP]Username created when installing an Email account via OMA-CP is incorrect
 *BUGFIX-886607  2015-02-16   zheng.zou       [OMA Provison]Can't install POP3 provision content successfully
 *BUGFIX_960193  2015-03-28   gengkexue       [Android5.0][Email]"From" field is wrong when compose with an email account which created by NOW SMS.
 *BUGFIX-985156  2015/04/30   zheng.zou        [HOMO][Orange][17] Orange and Wanadoo email_incoming settings
 *BUGFIX-1032790 2015/07/02   junwei-xu       [SW][Email][provisioning]Show 2 same account,and can't load mail list for the 2nd one
 *FEATURE-1778597   2016/03/10   tianjing.su    [Email]Configuration message can't install
 ===========================================================================
 */
package com.tct.email;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.os.RemoteException;

import android.text.TextUtils;
import com.tct.email.activity.setup.AccountSettingsUtils;
import com.tct.email.activity.setup.AccountSetupBasicsFragment;
import com.tct.email.activity.setup.SetupDataFragment;
//import com.android.email.service.MailService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.os.Handler;
import android.os.Message;
import com.tct.email.mail.Sender;
import com.tct.email.mail.Store;
import com.tct.email.provider.EmailProvider;
import com.tct.mail.utils.LogUtils;
import com.tct.email.provider.Utilities;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.EmailContent.HostAuthColumns;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.service.EmailServiceProxy;
import com.tct.emailcommon.service.SyncWindow;
import com.tct.fw.google.common.annotations.VisibleForTesting;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;
import com.tct.email.service.EmailServiceUtils;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.utils.PLFUtils;
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

public class AccountSetupBroadcastReceiver extends BroadcastReceiver{
    //The class is about the otaprovison,just add the code,when need the function,
    //I will program again.
    private static final String TAG = "AccountSetupReceiver";
    private static final String EMAIL_COUNT_NAME = "EMAIL_COUNT_NAME";
    private static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    private static final String EMAIL_PASSWORD = "EMAIL_PASSWORD";
    private static final String EMAIL_ACCOUNT_TYPE_INBOUND = "EMAIL_ACCOUNT_TYPE_INBOUND";
    private static final String EMAIL_ACCOUNT_TYPE_OUTBOUND = "EMAIL_ACCOUNT_TYPE_OUTBOUND";
    private static final String INBOUNDADDRESS = "INBOUNDADDRESS";
    private static final String INBOUNDPORT = "INBOUNDPORT";
    private static final String OUTBOUNDADDRESS = "OUTBOUNDADDRESS";
    private static final String OUTBOUNDPORT = "OUTBOUNDPORT";
    private static final String EMAIL_ACCOUNT_FOR_AUTHENTICATION = "EMAIL_ACCOUNT_FOR_AUTHENTICATION";
    private static final String EMAIL_PASSWORD_FOR_AUTHENTICATION = "EMAIL_PASSWORD_FOR_AUTHENTICATION";
    private static final String SECURITY_TYPE_FORIN = "SECURITY_TYPE_FORIN";
    private static final String SECURITY_TYPE_FOROUT = "SECURITY_TYPE_FOROUT";
    private static final String TYPE_POP3 = "pop3";
    private static final String TYPE_IMAP = "imap";
    private static final String SECURITY = "STARTTLS";
    private static final int DEFAULT_ACCOUNT_CHECK_INTERVAL = 15;
    public static final String OMACP_MTK_SETTING_ACTION = "com.mediatek.omacp.settings";
    public static final String OMACP_CAPABILITY_ACTION = "com.mediatek.omacp.capability";
    public static final String OMACP_QUALCOMM_SETTING_ACTION = "email.intent.android.emailsetting" ;
    public static final String OMACP_SETTING_RESULT_ACTION = "com.mediatek.omacp.settings.result";
    public static final String OMAPCP_CAPABILITY_RESULT_ACTION = "com.mediatek.omacp.capability.result";
    private AccountSetupBasicsFragment mAcountsetupbasics;

////PR 320064 add by yongtao.wang@jrdcom.com  begin
    private static final String TYPE_SMTP = "smtp";
    private static final String SMTP_APPID = String.valueOf(25);
    private static final String SMTP_DEAULT_PORT_NUM = String
            .valueOf(25);
    private static final String POP_DEFAULT_PORT_NUM = String
            .valueOf(110);
    private static final String IMAP_DEFAULT_PORT_NUM = String
            .valueOf(143);
    private static final String POP_APPID = String.valueOf(110);
    private static final String IMAP_APPID = String.valueOf(143);
    private static final int CONNECT_SUCCESS = 1;
    private static final int CONNECT_FAIL = -1;
    private static final String APPID_KEY = "25";
    private static final int SMTP_SERVER_TYPE = 1;
    private static final int POP_SERVER_TYPE = 2;
    private static final int IMAP_SERVER_TYPE = 3;
    private static final String SMTP_DEFAULT_SERVICE = String.valueOf(25);
    private static final String SMTP_SSL_SERVICE = String.valueOf(465);
    private static final String STR_SSL = "ssl";
    private static final String STR_TLS = "tls";
    private static final String POP_DEFAULT_SERVICE = String.valueOf(110);
    private static final String POP_SSL_SERVICE = String.valueOf(995);
    private static final String IMAP_DEFAULT_SERVICE = String.valueOf(143);
    private static final String IMAP_SSL_SERVICE = String.valueOf(993);
    private static final int SYNC_INTERVAL = 15;
    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 ADD_S
    private final static String ACCOUNT_WHERE_EMAILADDRESS = AccountColumns.EMAIL_ADDRESS
            + "=?";
    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 ADD_E
    private final static String ACCOUNT_WHERE_HOSTAUTH = AccountColumns.HOST_AUTH_KEY_RECV
            + "=?";
    private final static String HOSTAUTH_WHERE_CREDENTIALS = HostAuthColumns.ADDRESS
            + " like ?"
            + " and "
            + HostAuthColumns.LOGIN
            + " like ?"
            + " and "
            + HostAuthColumns.PROTOCOL + " not like \"smtp\"";
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 ADD_S
    private EmailAddressValidator mEmailValidator = new EmailAddressValidator();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CONNECT_SUCCESS) {
                Account account = (Account) msg.obj;
                addAccount(account);
                sendResultToOmacp(true);
            } else if (msg.what == CONNECT_FAIL) {
                sendResultToOmacp(false);
            }
        }
    };
    private String mFrom;
    private String mProviderId;
    private String mRtAddr;
    private Context mContext ;
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 ADD_E
    private URI smtpUri;
    private URI inUri;
    String email_account_name = null;
    String email_address = null;
    String email_password = null;
    String email_account_type_in = null;
    String email_account_type_out = null;
    String email_in_server_addr = null;
    int email_in_port;
    String email_out_server_addr = null;
    int email_out_port;
    String email_security_type_forin = null;
    String email_security_type_forout = null;
    String email_account_authentication = null;
    String email_password_authentication = null;

    String userName = null;
    String connectType = null;
    String sendType = null;
    Account account;
////PR 320064 add by yongtao.wang@jrdcom.com  end
   private static final int popPorts[] = {
        110, 995, 995, 110, 110
    };
   private static final int smtpPorts[] = {
        25, 465, 465, 25, 25
    };
   private static final int imapPorts[] = {
       143, 993, 993, 143, 143
    };
   private static final String popSchemes[] = {
        "pop3", "pop3+ssl+", "pop3+ssl+trustallcerts", "pop3+tls+", "pop3+tls+trustallcerts"
    };
   private static final String imapSchemes[] = {
        "imap", "imap+ssl+", "imap+ssl+trustallcerts", "imap+tls+", "imap+tls+trustallcerts"
    };
   private static final String smtpSchemes[] = {
        "smtp", "smtp+ssl+", "smtp+ssl+trustallcerts", "smtp+tls+", "smtp+tls+trustallcerts"
    };
   private static final String security[] = {"", "ssl", "ssl+trustallcerts", "tls", "tls+trustallcerts"};

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO Auto-generated method stub
        //There is no need to run another thread
        //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 MOD_S
        String action = intent.getAction();
        mContext = context;
        switch (action){
            case OMACP_QUALCOMM_SETTING_ACTION :
                setupAccount(context, intent);
                break;
            case OMACP_MTK_SETTING_ACTION :
                if (intent != null) {
                    Account account = parserOmacpRequest(intent);
                    if (account != null) {
                        checkEmailServerConnect(context, account);
                    } else {
                        sendResultToOmacp(false);
                    }
                }
                break;
            case OMACP_CAPABILITY_ACTION :
                buildCapabilityResultToOmacp(context);
                break;
        }
        //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 MOD_E
    }

    private int getPosition(String securityType){
        if(null == securityType){
            return 0;
        }
        for(int i=0; i<security.length; i++){
            if(securityType.equalsIgnoreCase(security[i])){
                return i;
            }
        }
        return 0;
    }

    private String getConnectType(String accountType, String securityType){
        if(null == accountType){
            return null;
        }
        int position = getPosition(securityType);
        if(accountType.equalsIgnoreCase(TYPE_POP3)){
            return popSchemes[position];
        }
        else if(accountType.equalsIgnoreCase(TYPE_IMAP)){
            return imapSchemes[position];
        }
        return null;
    }

    private String getSendType(String securityType) {
        return smtpSchemes[getPosition(securityType)];
    }

    private void setupAccount(final Context context, Intent intent) {
        //TS: zheng.zou 2015-02-16 EMAIL BUGFIX_886607 MOD_S
        account =new SetupDataFragment().getAccount();
        //TS: zheng.zou 2015-02-16 EMAIL BUGFIX_886607 MOD_E
        email_account_name = intent.getStringExtra(EMAIL_COUNT_NAME);
        email_address = intent.getStringExtra(EMAIL_ADDRESS);
        email_password = intent.getStringExtra(EMAIL_PASSWORD);
        email_account_type_in = intent
                .getStringExtra(EMAIL_ACCOUNT_TYPE_INBOUND);
        email_account_type_out = intent
                .getStringExtra(EMAIL_ACCOUNT_TYPE_OUTBOUND);
        email_in_server_addr = intent.getStringExtra(INBOUNDADDRESS);
        email_in_port = Integer
                .valueOf(intent.getStringExtra(INBOUNDPORT) == null ? "0"
                        : intent.getStringExtra(INBOUNDPORT));
        email_out_server_addr = intent.getStringExtra(OUTBOUNDADDRESS);
        email_out_port = Integer
                .valueOf(intent.getStringExtra(OUTBOUNDPORT) == null ? "0"
                        : intent.getStringExtra(OUTBOUNDPORT));
        email_security_type_forin = intent.getStringExtra(SECURITY_TYPE_FORIN);
        email_security_type_forout = intent
                .getStringExtra(SECURITY_TYPE_FOROUT);
        email_account_authentication = intent
                .getStringExtra(EMAIL_ACCOUNT_FOR_AUTHENTICATION);
        email_password_authentication = intent
                .getStringExtra(EMAIL_PASSWORD_FOR_AUTHENTICATION);

////PR 320064 add by yongtao.wang@jrdcom.com  begin
        if ((null != email_security_type_forin && email_security_type_forin
                .equalsIgnoreCase(SECURITY))
                || (null != email_security_type_forout && email_security_type_forout
                        .equalsIgnoreCase(SECURITY))) {
            email_security_type_forin = "tls+trustallcerts";
            email_security_type_forout = "tls+trustallcerts";
            int position = getPosition(email_security_type_forin);
            if (email_account_type_in.equalsIgnoreCase(TYPE_POP3)) {
                email_in_port = popPorts[position];
            } else if (email_account_type_in.equalsIgnoreCase(TYPE_IMAP)) {
                email_in_port = imapPorts[position];
            }
            email_out_port = smtpPorts[position];
        }

        //[BUGFIX]-Mod-BEGIN by TCTNB.Xian Jiang,02/07/2013,386690,
        //Must give the whole "email_address"
        //userName = email_address.split("@")[0];
        userName = email_address;

        //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 MOD_S
        //Note: use perso to controll tmo provision eamil account, default is false
        /*
     // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-924451 ADD_S
        if(context.getResources().getBoolean(R.bool.feature_email_TmoProvisionEmail_on)){
            if (email_account_authentication != null) {
                userName = email_account_authentication;
            }
        }
     // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-924451 ADD_E
        */
        boolean tmoProvisionEnable = PLFUtils.getBoolean(context, "feature_email_TmoProvisionEmail_on");
        if (tmoProvisionEnable && !TextUtils.isEmpty(email_account_authentication)) {
            userName = email_account_authentication;
        }
        //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 MOD_E
        //[BUGFIX]-Mod-END by TCTNB.Xian Jiang
        connectType = getConnectType(email_account_type_in,
                email_security_type_forin);
        sendType = getSendType(email_security_type_forout);

        //account = SetupData.getAccount();
        //[BUGFIX]-Mod-BEGIN by TCTNB.Xian Jiang,02/07/2013,386690,
        //add the condition for having existed different email count
        //with OTA provising sending
        //if (null == account) {
     // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-924451 MOD_S
        if ((null == account)||
                (!email_address.equals(account.mEmailAddress))||
                ((!userName.equals(account.mDisplayName)))) {
     // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-924451 MOD_E
        //[BUGFIX]-Mod-END by TCTNB.Xian Jiang
            account = new Account();
            account.setDeletePolicy(0);
            account.setDefaultAccount(false);

            try {
                // commit the account to provider
                if (email_account_type_in.equals(TYPE_POP3)) {

                    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 MOD_S
                    //Note:if has same eamil address, we consider it as a duplicate account
                    /*
                    Account tempAccount = findDuplicateAccount(context,
                            account.mId, email_in_server_addr, userName);
                    */
                    Account tempAccount = findDuplicateAccount(context, email_address);
                    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 MOD_E
                    if (null != tempAccount) {
                        // for updating account,obtain account according mid
                        account.mId = tempAccount.mId;
                        account = Account.restoreAccountWithId(context,
                                account.mId);
                    }
                    //[BUGFIX]-Mod-BEGIN by TCTNB.Xian Jiang,02/07/2013,386690,
                    //set "inUri" correctly
//                    String scheme1 = getScheme(email_account_type_in,
//                            POP_SERVER_TYPE);
//                    inUri = new URI(scheme1, userName + ":" + email_password,
//                            email_in_server_addr, email_in_port, null, null,
//                            null);
                    if (null !=connectType) {
                        inUri = new URI(connectType, userName + ":" + email_password,
                                email_in_server_addr, email_in_port, null, null,
                                null);
                    } else {
                        String scheme1 = getScheme(email_account_type_in,
                                POP_SERVER_TYPE);
                        inUri = new URI(scheme1, userName + ":" + email_password,
                                email_in_server_addr, email_in_port, null, null,
                                null);
                    }
                    //[BUGFIX]-Mod-END by TCTNB.Xian Jiang
                    HostAuth recAuth = account.getOrCreateHostAuthRecv(context);
                    //TS: zheng.zou 2015-4-30 EMAIL BUGFIX-985156, MOD_S
                    int customPopDeletePolicy = PLFUtils.getCustomPopDeletePolicy(context,Account.DELETE_POLICY_ON_DELETE);
//                    account.setDeletePolicy(Account.DELETE_POLICY_ON_DELETE);
                    account.setDeletePolicy(customPopDeletePolicy);
                    //TS: zheng.zou 2015-4-30 EMAIL BUGFIX-985156, MOD_E
                    recAuth.setHostAuthFromString(inUri.toString());
                } else if (email_account_type_in.equals(TYPE_IMAP)) {
                    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 MOD_S
                    //Note:if has same eamil address, we consider it as a duplicate account
                    /*
                    Account tempAccount = findDuplicateAccount(context,
                            account.mId, email_in_server_addr, userName);
                    */
                    Account tempAccount = findDuplicateAccount(context, email_address);
                    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 MOD_E
                    if (null != tempAccount) {
                        // for updating account,obtain account according mid
                        account.mId = tempAccount.mId;
                        account = Account.restoreAccountWithId(context,
                                account.mId);
                    }
                    //[BUGFIX]-Mod-BEGIN by TCTNB.Xian Jiang,02/07/2013,386690,
                    //set "inUri" correctly
//                    String scheme2 = getScheme(email_account_type_in,
//                            IMAP_SERVER_TYPE);
//                    inUri = new URI(scheme2, userName + ":" + email_password,
//                            email_in_server_addr, email_in_port, null, null,
//                            null);
                    if (null !=connectType) {
                        inUri = new URI(connectType, userName + ":" + email_password,
                          email_in_server_addr, email_in_port, null, null,
                          null);
                    } else {
                        String scheme2 = getScheme(email_account_type_in,
                                IMAP_SERVER_TYPE);
                        inUri = new URI(scheme2, userName + ":" + email_password,
                                email_in_server_addr, email_in_port, null, null,
                                null);
                    }
                    //[BUGFIX]-Mod-END by TCTNB.Xian Jiang
                    account.setDeletePolicy(Account.DELETE_POLICY_ON_DELETE);
                    HostAuth recAuth = account.getOrCreateHostAuthRecv(context);
                    recAuth.setHostAuthFromString(inUri.toString());
                }

                if (email_account_type_out.equals(TYPE_SMTP)) {
                  //[BUGFIX]-Mod-BEGIN by TCTNB.Xian Jiang,02/07/2013,386690,
                  //set "smtpUri" correctly
//                    String scheme3 = getScheme(email_account_type_out,
//                            SMTP_SERVER_TYPE);
//                    smtpUri = new URI(scheme3, userName + ":" + email_password,
//                            email_out_server_addr, email_out_port, null, null,
//                            null);
                    if (null !=sendType) {
                        smtpUri = new URI(sendType, userName + ":" + email_password,
                                email_out_server_addr, email_out_port, null, null,
                                null);
                    } else {
                        String scheme3 = getScheme(email_account_type_out,
                                SMTP_SERVER_TYPE);
                        smtpUri = new URI(scheme3, userName + ":" + email_password,
                                email_out_server_addr, email_out_port, null, null,
                                null);
                    }
                  //[BUGFIX]-Mod-END by TCTNB.Xian Jiang
                    HostAuth sendAuth = account
                            .getOrCreateHostAuthSend(context);
                    sendAuth.setHostAuthFromString(smtpUri.toString());
                }

                // if the account is already saved, reset smtp sender
                try {
                    if (account.isSaved()) {
                        HostAuth sendHostAuth = account
                                .getOrCreateHostAuthSend(context);
                        sendHostAuth.setHostAuthFromString(smtpUri.toString());
                        // account.setSenderUri(this, smtpUri.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "" + e, e);
                } finally {
                }

                int newFlags = account.getFlags()
                        & ~(Account.FLAGS_NOTIFY_NEW_MAIL);
                newFlags |= Account.FLAGS_NOTIFY_NEW_MAIL;
                account.setFlags(newFlags);
                account.setEmailAddress(email_address);
                account.setSenderName(email_address);
                account.setDisplayName(email_account_name);
                account.setSyncInterval(SYNC_INTERVAL);
                account.setDefaultAccount(true);
                //TS: zheng.zou 2015-02-16 EMAIL BUGFIX_886607 MOD_S
//                account.mFlags &= ~Account.FLAGS_INCOMPLETE;
                // Set the incomplete flag here to avoid reconciliation issues
                account.mFlags |= Account.FLAGS_INCOMPLETE;
                //TS: zheng.zou 2015-02-16 EMAIL BUGFIX_886607 MOD_E

                //TS: Gantao 2015-12-04 EMAIL BUGFIX_1031705 ADD_S
                //Set the signature for account
                String accountSign = Utilities.getSingnatureForAccount(context);
                account.setSignature(accountSign);
                //TS: Gantao 2015-12-04 EMAIL BUGFIX_1031705 ADD_E

                if (account.isSaved()) {
                    Log.i(TAG, "update Account send & receive information");
                    account.mHostAuthSend.update(context,
                            account.mHostAuthSend.toContentValues());
                    account.mHostAuthRecv.update(context,
                            account.mHostAuthRecv.toContentValues());
                }
                // update account flag.
                String protocal = account.mHostAuthRecv == null ? null
                        : account.mHostAuthRecv.mProtocol;
                if (null != protocal) {
                    setFlagsForProtocol(account, protocal);
                }
                Log.i(TAG, "add Account with flag : " + account.mFlags);
                // Save email account.
                AccountSettingsUtils.commitSettings(context, account);
                Log.i(TAG,
                        "AccountSettingsUtils.commitSettings save email Account ");
                //TS: zheng.zou 2015-02-16 EMAIL BUGFIX_886607 MOD_S
                // Save system account.
//                EmailServiceUtils.setupAccountManagerAccount(context, account, true,
//                        false, false, null);
                EmailServiceUtils.setupAccountManagerAccount(context, account, true,
                        false, false, new AccountManagerCallback<Bundle>() {
                            public void run(AccountManagerFuture<Bundle> future) {
                                try {
                                    Bundle bundle = future.getResult();
                                    bundle.keySet();
                                    Log.i(TAG, "******** mAccountManagerCallback addAccount success");
                                    // Now that AccountManager account creation is complete, clear the INCOMPLETE flag
                                    account.mFlags &= ~Account.FLAGS_INCOMPLETE;
                                    AccountSettingsUtils.commitSettings(context, account);
                                    //Email.setServicesEnabledSync(mContext);
                                    MailActivityEmail.setServicesEnabledSync(context);
                                // AM: Kexue.Geng 2015-03-28 EMAIL BUGFIX_960193 MOD_S
                                    final EmailServiceProxy proxy = EmailServiceUtils.getServiceForAccount(context, account.mId);
                                    proxy.updateFolderList(account.mId);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                // AM: Kexue.Geng 2015-03-28 EMAIL BUGFIX_960193 MOD_E
                                } catch (OperationCanceledException e) {
                                    Log.e(TAG, "addAccount was canceled");
                                } catch (IOException e) {
                                    Log.e(TAG, "addAccount failed: " + e);
                                } catch (AuthenticatorException e) {
                                    Log.e(TAG, "addAccount failed: " + e);
                                }
                            }
                        });
                Log.i(TAG,
                        "MailService.setupAccountManagerAccount save system Account ");
//                MailActivityEmail.setServicesEnabledSync(context);
                //TS: zheng.zou 2015-02-16 EMAIL BUGFIX_886607 MOD_E

            } catch (URISyntaxException use) {
                // If we can't set up the URL, don't continue - account setup
                // pages will fail too
                //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showToast(context, R.string.account_setup_username_password_toast);
                //Toast.makeText(context,
                //        R.string.account_setup_username_password_toast,
                //        Toast.LENGTH_LONG).show();
                account = null;
            }
        }
    ////PR 320064 add by yongtao.wang@jrdcom.com  end
    }

    /**
     * Sets the account sync, delete, and other misc flags not captured in {@code HostAuth}
     * information for the specified account based on the protocol type.
     */
    @VisibleForTesting
    static void setFlagsForProtocol(Account account, String protocol) {
        if (HostAuth.SCHEME_IMAP.equals(protocol)) {
            // Delete policy must be set explicitly, because IMAP does not provide a UI selection
            // for it.
            account.setDeletePolicy(Account.DELETE_POLICY_ON_DELETE);
            account.mFlags |= Account.FLAGS_SUPPORTS_SEARCH;
        }

        if (HostAuth.SCHEME_EAS.equals(protocol)) {
            account.setDeletePolicy(Account.DELETE_POLICY_ON_DELETE);
            account.setSyncInterval(Account.CHECK_INTERVAL_PUSH);
            account.setSyncLookback(SyncWindow.SYNC_WINDOW_AUTO);
        } else {
            account.setSyncInterval(DEFAULT_ACCOUNT_CHECK_INTERVAL);
        }
    }

    ////////PR 320064 add by yongtao.wang@jrdcom.com  begin
    /**
     * get scheme of email uri
     *
     * @param service service from omacp
     * @param serverType Email server type
     * @return
     */
    /* This function need improving if more request reach  ---yongtao.wang@jrdcom.com*/
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 MOD_S
    private String getScheme(String service, int serverType) {
        String scheme = null;
        if (SMTP_SERVER_TYPE == serverType) {
            if (SMTP_DEFAULT_SERVICE.equalsIgnoreCase(service)) {
                scheme = "smtp";
            } else if ("STARTTLS".equalsIgnoreCase(service) || STR_TLS.equalsIgnoreCase(service)) {
                scheme = "smtp+tls+";
            } else if (SMTP_SSL_SERVICE.equalsIgnoreCase(service) || STR_SSL.equalsIgnoreCase(service)) {
                scheme = "smtp+ssl+";
            }else {
                scheme = "smtp";
            }
        } else if (POP_SERVER_TYPE == serverType) {
            if (POP_DEFAULT_SERVICE.equalsIgnoreCase(service)) {
                scheme = "pop3";
            } else if ("STARTTLS".equalsIgnoreCase(service) || STR_TLS.equalsIgnoreCase(service)) {
                scheme = "pop3+tls+";
            } else if (POP_SSL_SERVICE.equalsIgnoreCase(service) || STR_SSL.equalsIgnoreCase(service)) {
                scheme = "pop3+ssl+";
            }else {
               scheme = "pop3";
            }
        } else if (IMAP_SERVER_TYPE == serverType) {
            if (IMAP_DEFAULT_SERVICE.equalsIgnoreCase(service)) {
                scheme = "imap";
            } else if ("STARTTLS".equalsIgnoreCase(service) || STR_TLS.equalsIgnoreCase(service)) {
                scheme = "imap+tls+";
            } else if (IMAP_SSL_SERVICE.equalsIgnoreCase(service) || STR_SSL.equalsIgnoreCase(service)) {
                scheme = "imap+ssl+";
            }else {
                scheme = "imap";
            }
        }
        return scheme;
    }
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 MOD_E
    /**
     * Look for an existing account with the same username & server
     *
     * @param context a system context
     * @param allowAccountId this account Id will not trigger (when editing an
     *            existing account)
     * @param hostName the server
     * @param userLogin the user login string
     * @result null = no dupes found. non-null = dupe account's display name
     */
    public Account findDuplicateAccount(Context context, long allowAccountId, String hostName,
            String userLogin) {
        Account account = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(HostAuth.CONTENT_URI, HostAuth.ID_PROJECTION,
                HOSTAUTH_WHERE_CREDENTIALS, new String[] {
                        hostName, userLogin
                }, null);
        try {
            while (c.moveToNext()) {
                long hostAuthId = c.getLong(HostAuth.ID_PROJECTION_COLUMN);

                // Find account with matching hostauthrecv key, and return its
                // display name
                Cursor c2 = resolver.query(Account.CONTENT_URI, Account.ID_PROJECTION,
                        ACCOUNT_WHERE_HOSTAUTH, new String[] {
                            Long.toString(hostAuthId)
                        }, null);
                try {
                    while (c2.moveToNext()) {
                        long accountId = c2.getLong(Account.ID_PROJECTION_COLUMN);
                        if (accountId != allowAccountId) {
                            account = Account.restoreAccountWithId(context, accountId);
                        }
                    }
                } finally {
                    c2.close();
                }
            }
        } finally {
            c.close();
        }

        return account;
    }
    ////////PR 320064 add by yongtao.wang@jrdcom.com  end

    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 ADD_S
    /**
     * Look for an existing account with the same email address
     * @param context a system context
     * @param emailAddress the login email address
     * @return null = no dupes found. non-null = dupe account
     */
    public Account findDuplicateAccount(Context context, String emailAddress) {
        Account account = null;
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(Account.CONTENT_URI, new String[] {
                AccountColumns._ID,
                AccountColumns.EMAIL_ADDRESS
        }, ACCOUNT_WHERE_EMAILADDRESS, new String[] {emailAddress}, null);
        try {
            final int idColumnIndex = cursor.getColumnIndex(AccountColumns._ID);

            if (cursor.moveToFirst()) {
                do {
                    long accountId = cursor.getLong(idColumnIndex);
                    account = Account.restoreAccountWithId(context, accountId);
                    break;
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return account;
    }
    //TS: junwei-xu 2015-07-02 EMAIL BUGFIX-1032790 ADD_E
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 ADD_S
    /**
     * Obtaining Account Object by parsering OMACP request
     * @param intent OMACP intent
     * @return Account
     */
    @SuppressWarnings("unchecked")
    private Account parserOmacpRequest(Intent intent) {
        LogUtils.i(TAG, "parser Omacp Request is begin");
        List<Intent> omacpList = intent.getParcelableArrayListExtra("email_setting_intent");
        if (null == omacpList || omacpList.isEmpty()) {
            LogUtils.e(TAG, "OMACP email_setting_intent is " + omacpList);
            return null;
        }
        Account account = new Account();
        String addr = null;
        String portNbr = null;
        String service = null;
        String scheme = null;
        ArrayList appAddr = null;
        ArrayList appAuth = null;
        URI uri = null;
        URI smtpUri = null;
        String appId = null;
        String authType = null;
        try {
            for (Intent emailIntent : omacpList) {
                appId = emailIntent.getStringExtra("APPID");
                appAddr = emailIntent.getParcelableArrayListExtra("APPADDR");
                if (null != appAddr && !appAddr.isEmpty()) {
                    for (int i = 0; i < appAddr.size(); i++) {
                        if (appAddr.get(i) instanceof Map) {
                            Map<String, String> addrMap = (Map<String, String>) appAddr.get(i);
                            addr = addrMap.get("ADDR");
                            portNbr = addrMap.get("PORTNBR");
                            service = addrMap.get("SERVICE");
                        }
                    }
                } else {
                    addr = emailIntent.getStringExtra("ADDR");
                }
                if (isEmpty(addr)) {
                    LogUtils.e(TAG, "addr is empty");
                    return null;
                }
                appAuth = emailIntent.getParcelableArrayListExtra("APPAUTH");
                String aAuthName = null;
                String aAuthSecret = null;
                boolean needAuth = false;
                if (null != appAuth && !appAuth.isEmpty()) {
                    for (int i = 0; i < appAuth.size(); i++) {
                        if (appAuth.get(i) instanceof Map) {
                            Map<String, String> authMap = (Map<String, String>) appAuth.get(i);
                            authType = authMap.get("AAUTHTYPE");
                            aAuthName = authMap.get("AAUTHNAME");
                            aAuthSecret = authMap.get("AAUTHSECRET");
                            needAuth = true;
                            LogUtils.i(TAG,
                                    "Parse Auth: appId [%s], authType [%s], aAuthName [%s], aAuthSecret [%s] ",
                                    appId, authType, aAuthName, aAuthSecret);
                        }
                    }
                }
                if (SMTP_APPID.equals(appId)) {
                    mProviderId = emailIntent.getStringExtra("PROVIDER-ID");
                    mFrom = emailIntent.getStringExtra("FROM");
                    mRtAddr = emailIntent.getStringExtra("RT-ADDR");

                    LogUtils.i(TAG, "[OMACP] smtp param: PROVIDER-ID=" + mProviderId + ";ADDR=" + addr
                            + ";FROM=" + mFrom + ";RT-ADDR=" + mRtAddr + ";PORT=" + portNbr
                            + ";SERVICE=" + service + ";AAUTHTYPE=" + authType + ";AAUTHNAME="
                            + aAuthName + ";AAUTHSECRET=" + aAuthSecret);
                    if (isInvalidEmailAddress(mFrom)) {
                        return null;
                    }

                    // delete "< >"
                    if (mFrom.contains("<")) {
                        mFrom = mFrom.split("<")[1].replace(">", "");
                    }
                    if (mRtAddr != null) {
                        if (isInvalidEmailAddress(mRtAddr)) {
                            return null;
                        }
                        if (mRtAddr.contains("<")) {
                            mRtAddr = mRtAddr.split("<")[0].trim().replace("\"", "");
                        } else {

                            // if the rtaddr doesn't contain nickname,then set
                            // email address to be reply address
                            mRtAddr = null;
                        }
                    }
                    String userInfo = null;

                    // Just check if contains APPAUTH tag, only failed for APPAUTH tag but without
                    // AAUTHNAME or AAUTHSECRET.
                    if (needAuth) {
                        if (isEmpty(aAuthName) || isEmpty(aAuthSecret)) {
                            LogUtils.i(TAG, "SMTP failed for without aAuthName or aAuthSecret");
                            return null;
                        }
                        userInfo = aAuthName + ":" + aAuthSecret;
                    }
                    portNbr = portNbr == null ? SMTP_DEAULT_PORT_NUM : portNbr;
                    service = service == null ? SMTP_DEFAULT_SERVICE : service;
                    scheme = getScheme(service, SMTP_SERVER_TYPE);
                    if (null == scheme) {
                        return null;
                    }
                    smtpUri = new URI(scheme, userInfo, addr, Integer.parseInt(portNbr), null,
                            null, null);
                    HostAuth sendAuth = account.getOrCreateHostAuthSend(mContext);
                    sendAuth.setHostAuthFromString(smtpUri.toString());
                } else if (POP_APPID.equals(appId)) {
                    LogUtils.i(TAG, "[OMACP] pop param: ADDR=" + addr + ";PORT=" + portNbr + ";SERVICE="
                            + service + ";AAUTHNAME=" + aAuthName + ";AAUTHSECRET=" + aAuthSecret);
                    if (isEmpty(aAuthName) || isEmpty(aAuthSecret)) {
                        return null;
                    }
                    Account tempAccount = findDuplicateAccount(mContext, account.mId, addr, aAuthName);
                    if (null != tempAccount) {

                        // for updating account,obtain account according mid
                        account.mId = tempAccount.mId;
                        account = Account.restoreAccountWithId(mContext, account.mId);
                    }
                    portNbr = portNbr == null ? POP_DEFAULT_PORT_NUM : portNbr;
                    service = service == null ? POP_DEFAULT_SERVICE : service;
                    scheme = getScheme(service, POP_SERVER_TYPE);
                    if (null == scheme) {
                        return null;
                    }
                    uri = new URI(scheme, aAuthName + ":" + aAuthSecret, addr, Integer
                            .parseInt(portNbr), null, null, null);
                    HostAuth recAuth = account.getOrCreateHostAuthRecv(mContext);
                    recAuth.setHostAuthFromString(uri.toString());
                } else if (IMAP_APPID.equals(appId)) {
                    LogUtils.i(TAG, "[OMACP] imap param: ADDR=" + addr + ";PORT=" + portNbr
                            + ";SERVICE=" + service + ";AAUTHNAME=" + aAuthName + ";AAUTHSECRET="
                            + aAuthSecret);
                    if (isEmpty(aAuthName) || isEmpty(aAuthSecret)) {
                        return null;
                    }
                    Account tempAccount = findDuplicateAccount(mContext, account.mId, addr, aAuthName);
                    if (null != tempAccount) {

                        // for updating account,obtain account according mid
                        account.mId = tempAccount.mId;
                        account = Account.restoreAccountWithId(mContext, account.mId);
                    }
                    portNbr = portNbr == null ? IMAP_DEFAULT_PORT_NUM : portNbr;
                    service = service == null ? IMAP_DEFAULT_SERVICE : service;
                    scheme = getScheme(service, IMAP_SERVER_TYPE);
                    if (null == scheme) {
                        return null;
                    }
                    uri = new URI(scheme, aAuthName + ":" + aAuthSecret, addr, Integer
                            .parseInt(portNbr), null, null, null);
                    account.setDeletePolicy(Account.DELETE_POLICY_ON_DELETE);
                    HostAuth recAuth = account.getOrCreateHostAuthRecv(mContext);
                    recAuth.setHostAuthFromString(uri.toString());
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "" + e, e);
            return null;
        }

        // if the account is already saved, reset smtp sender
        try {
            if (account.isSaved()) {
                HostAuth sendHostAuth = account.getOrCreateHostAuthSend(mContext);
                sendHostAuth.setHostAuthFromString(smtpUri.toString());
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "" + e, e);
            return null;
        }

        return account;
    }
    /**
     * check email server is connect or not,including sender server and receiver
     * server
     * @param context
     * @param account
     */
    private void checkEmailServerConnect(final Context context, final Account account) {
        // Start debug log to get more logs, default switch in MailActivityEmail.
        Preferences prefs = Preferences.getPreferences(context);
        //DebugUtils.DEBUG = prefs.getEnableDebugLogging();
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    Store store = Store.getInstance(account, context);
                    if (store == null) {
                        sendMessage(CONNECT_FAIL, null);
                        return;
                    }
                    store.checkSettings();
                    LogUtils.i(TAG, ">>> check incoming pass.");

                    Sender sender = Sender.getInstance(context, account);
                    if (sender == null) {
                        sendMessage(CONNECT_FAIL, null);
                        return;
                    }
                    sender.close();
                    sender.open();
                    sender.close();
                    LogUtils.i(TAG, "email server check finish.");
                    sendMessage(CONNECT_SUCCESS, account);
                } catch (Exception e) {
                    LogUtils.e(TAG, "" + e, e);
                    sendMessage(CONNECT_FAIL, null);
                }
            }
        };
        new Thread(runnable).start();
    }
    private void sendResultToOmacp( boolean isSucceed) {
        Intent intent = new Intent();
        intent.setAction(OMACP_SETTING_RESULT_ACTION);
        intent.putExtra("appId", APPID_KEY);
        intent.putExtra("result", isSucceed);
        mContext.sendBroadcast(intent);
    }
    private static final String APPID_VALUE = "25";
    static void buildCapabilityResultToOmacp(Context context) {
        Intent intent = new Intent(OMAPCP_CAPABILITY_RESULT_ACTION);
        intent.putExtra(EmailCapability.APPID, APPID_VALUE);
        intent.putExtra(EmailCapability.EMAIL, true);
        intent.putExtra(EmailCapability.EMAIL_PROVIDER_ID, true);
        intent.putExtra(EmailCapability.EMAIL_OUTBOUND_ADDR, true);
        intent.putExtra(EmailCapability.EMAIL_OUTBOUND_PORT_NUMBER, true);
        intent.putExtra(EmailCapability.EMAIL_OUTBOUND_SECURE, true);
        intent.putExtra(EmailCapability.EMAIL_OUTBOUND_AUTH_TYPE, true);
        intent.putExtra(EmailCapability.EMAIL_OUTBOUND_USER_NAME, true);
        intent.putExtra(EmailCapability.EMAIL_OUTBOUND_PASSWORD, true);
        intent.putExtra(EmailCapability.EMAIL_FROM, true);
        intent.putExtra(EmailCapability.EMAIL_RT_ADDR, true);
        intent.putExtra(EmailCapability.EMAIL_INBOUND_ADDR, true);
        intent.putExtra(EmailCapability.EMAIL_INBOUND_PORT_NUMBER, true);
        intent.putExtra(EmailCapability.EMAIL_INBOUND_SECURE, true);
        intent.putExtra(EmailCapability.EMAIL_INBOUND_USER_NAME, true);
        intent.putExtra(EmailCapability.EMAIL_INBOUND_PASSWORD, true);
        LogUtils.i(TAG, "return OMACP capability result intent:" + intent);
        context.sendBroadcast(intent);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    /**
     * add account to email
     * @param account
     */
    private void addAccount(Account account) {
        LogUtils.i(TAG, "add Account is beginning");
        int newFlags = account.getFlags() & ~(Account.FLAGS_NOTIFY_NEW_MAIL);
        newFlags |= Account.FLAGS_NOTIFY_NEW_MAIL;
        account.setFlags(newFlags);
        account.setEmailAddress(mFrom);
        account.setSenderName(mRtAddr);
        account.setDisplayName(mProviderId);
        account.setSyncInterval(SYNC_INTERVAL);
        account.mFlags &= ~Account.FLAGS_INCOMPLETE;
        if (account.isSaved()) {
            LogUtils.i(TAG, "update Account send & receive information");
            account.mHostAuthSend.update(mContext, account.mHostAuthSend.toContentValues());
            account.mHostAuthRecv.update(mContext, account.mHostAuthRecv.toContentValues());
        }
        //update account flag.
        String protocal = account.mHostAuthRecv == null ? null : account.mHostAuthRecv.mProtocol;
        if (null != protocal) {
            setFlagsForProtocol(account, protocal);
        }
        LogUtils.i(TAG, "add Account with flag : " + account.mFlags);
        //Save email account.
        AccountSettingsUtils.commitSettings(mContext, account);
        LogUtils.i(TAG, "AccountSettingsUtils.commitSettings save email Account ");
        //Save system account.
        EmailServiceUtils.setupAccountManagerAccount(mContext, account, true, false, false, null);
        LogUtils.i(TAG, "EmailServiceUtils save system Account ");
        EmailProvider.setServicesEnabledSync(mContext);
        EmailServiceUtils.startService(mContext, account.mHostAuthRecv.mProtocol);

        // Start fist sync, Update the folder list (to get our starting folders, e.g. Inbox)
        final EmailServiceProxy proxy = EmailServiceUtils.getServiceForAccount(mContext, account.mId);
        try {
            proxy.updateFolderList(account.mId);
            LogUtils.i(TAG, "*** Setup omcap account [%s] success, start update folder list ",
                    account.getEmailAddress());
        } catch (RemoteException e) {
            // It's ok, it will be started by internal sync or menu sync again.
        }
    }

    /**
     * check the email address is invalid or not
     * @param email email address
     * @return
     */
    private boolean isInvalidEmailAddress(String email) {
        return isEmpty(email) || !mEmailValidator.isValid(email);
    }

    private void sendMessage(int isConnect, Account account) {
        if (CONNECT_FAIL == isConnect) {
            mHandler.sendEmptyMessage(isConnect);
        } else if (CONNECT_SUCCESS == isConnect) {
            Message message = mHandler.obtainMessage();
            message.what = isConnect;
            message.obj = account;
            mHandler.sendMessage(message);
        }
    }

    interface EmailCapability {

        String APPID = "appId";

        String EMAIL = "email";

        String EMAIL_PROVIDER_ID = "email_provider_id";

        String EMAIL_OUTBOUND_ADDR = "email_outbound_addr";

        String EMAIL_OUTBOUND_PORT_NUMBER = "email_outbound_port_number";

        String EMAIL_OUTBOUND_SECURE = "email_outbound_secure";

        String EMAIL_OUTBOUND_AUTH_TYPE = "email_outbound_auth_type";

        String EMAIL_OUTBOUND_USER_NAME = "email_outbound_user_name";

        String EMAIL_OUTBOUND_PASSWORD = "email_outbound_password";

        String EMAIL_FROM = "email_from";

        String EMAIL_RT_ADDR = "email_rt_addr";

        String EMAIL_INBOUND_ADDR = "email_inbound_addr";

        String EMAIL_INBOUND_PORT_NUMBER = "email_inbound_port_number";

        String EMAIL_INBOUND_SECURE = "email_inbound_secure";

        String EMAIL_INBOUND_USER_NAME = "email_inbound_user_name";

        String EMAIL_INBOUND_PASSWORD = "email_inbound_password";
    }
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 ADD_E
}
