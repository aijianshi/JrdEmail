/*
 * Copyright (C) 2009 The Android Open Source Project
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
/* 11/05/2014|TSNJ zhujian.shao     |739308                |[SFR][2.6.2.2.][S */
/*           |                      |                      |ynchronization]Sy */
/*           |                      |                      |nchronization is  */
/*           |                      |                      |inconsistent with */
/*           |                      |                      | the requirement. */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
package com.tct.email.service;

import com.tct.emailcommon.provider.EmailContent;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
//[BUGFIX]-Add-BEGIN by TSCD chuan.ye ,05/15/2014,645211,
//show wraning when syncing the first
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.content.DialogInterface.OnCancelListener;
import android.view.WindowManager;
import android.net.ConnectivityManager;
import android.content.ContentResolver;
import android.util.Log;
import com.tct.email.R;
import com.tct.email.activity.setup.AccountSetupFinal;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.mail.utils.PLFUtils;

import android.content.pm.PackageManager;
//[BUGFIX]-Add-END by TSCD chuan.ye
/**
 * A very basic authenticator service for EAS.  At the moment, it has no UI hooks.  When called
 * with addAccount, it simply adds the account to AccountManager directly with a username and
 * password.
 */
public class AuthenticatorService extends Service {
    public static final String OPTIONS_USERNAME = "username";
    public static final String OPTIONS_PASSWORD = "password";
    public static final String OPTIONS_CONTACTS_SYNC_ENABLED = "contacts";
    public static final String OPTIONS_CALENDAR_SYNC_ENABLED = "calendar";
    public static final String OPTIONS_EMAIL_SYNC_ENABLED = "email";
  //[BUGFIX]-Add-BEGIN by TSNJ.zhujian.shao,11/05/2014,739308,
    //show wraning when syncing the first
    private  final int REQUEST_SYNC_INIT = 2;// add for sync to tip
    private final int REQUEST_SYNC_WIFI=1;//sync on wifi
  //[BUGFIX]-Add-END by TSNJ.zhujian.shao,11/05/2014,739308,

    class Authenticator extends AbstractAccountAuthenticator {
      //[BUGFIX]-Add-BEGIN by TSNJ.zhujian.shao,11/05/2014,739308,
        //show wraning when syncing the first
        private final Context mContext;
        private boolean tempIsAddAccount=false;
        private AThread mAt;
      //[BUGFIX]-Add-END by TSNJ.zhujian.shao,11/05/2014,739308,
        public Authenticator(Context context) {
            super(context);
            mContext = context;
        }

      //[BUGFIX]-Add-BEGIN by TSNJ.zhujian.shao,11/05/2014,739308,
        private boolean checkPermission() {
             return PackageManager.PERMISSION_GRANTED == mContext.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE");
        }
      //[BUGFIX]-Add-END by TSNJ zhujian,shao

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {

            final String protocol = EmailServiceUtils.getProtocolFromAccountType(
                    AuthenticatorService.this, accountType);
            final EmailServiceInfo info = EmailServiceUtils.getServiceInfo(
                    AuthenticatorService.this, protocol);
            //[BUGFIX]-Add-BEGIN by TSNJ.zhujian.shao,11/05/2014,739308,
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //if(mContext.getResources().getBoolean(R.bool.feature_tctfw_SyncTip_on)&& this.checkPermission()){
            if(PLFUtils.getBoolean(mContext, "feature_tctfw_SyncTip_on")&& this.checkPermission()){
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
                    ConnectivityManager connect=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(null!=connect.getActiveNetworkInfo()&&ConnectivityManager.TYPE_WIFI != connect.getActiveNetworkInfo().getType()){
                        mAt=new AThread(mContext);
                        mAt.start();
                       tempIsAddAccount=true;
                    }
                }
           //[BUGFIX]-Add-END by TSNJ zhujian,shao
                    // There are two cases here:
            // 1) We are called with a username/password; this comes from the traditional email
            //    app UI; we simply create the account and return the proper bundle
            if (options != null && options.containsKey(OPTIONS_PASSWORD)
                    && options.containsKey(OPTIONS_USERNAME)) {
                final Account account = new Account(options.getString(OPTIONS_USERNAME),
                        accountType);
                AccountManager.get(AuthenticatorService.this).addAccountExplicitly(
                            account, options.getString(OPTIONS_PASSWORD), null);
              //[BUGFIX]-Add-BEGIN by TSNJ.zhujian.shao,11/05/2014,739308,
                // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
                //if(mContext.getResources().getBoolean(R.bool.feature_tctfw_SyncTip_on)&& this.checkPermission()){
                if(PLFUtils.getBoolean(mContext, "feature_tctfw_SyncTip_on")&& this.checkPermission()){
                // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
                    if(tempIsAddAccount){
                        ConnectivityManager connect=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        if(ConnectivityManager.TYPE_WIFI != connect.getActiveNetworkInfo().getType()){
                        try{
                            Message message = mAt.getHandler().obtainMessage();
                            message.what = REQUEST_SYNC_INIT;
                            mAt.getHandler().sendMessage(message);
                        }catch(Exception ex){

                        }
                        tempIsAddAccount=false;
                        }else{
                            Message message = mAt.getHandler().obtainMessage();
                            message.what = REQUEST_SYNC_WIFI;
                            mAt.getHandler().sendMessage(message);
                        }
                     }
                    }
                //[BUGFIX]-Add-END by TSNJ zhujian,shao
                // Set up contacts syncing, if appropriate
                if (info != null && info.syncContacts) {
                    boolean syncContacts = options.getBoolean(OPTIONS_CONTACTS_SYNC_ENABLED, false);
                    ContentResolver.setIsSyncable(account, ContactsContract.AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY,
                            syncContacts);
                }

                // Set up calendar syncing, if appropriate
                if (info != null && info.syncCalendar) {
                    boolean syncCalendar = options.getBoolean(OPTIONS_CALENDAR_SYNC_ENABLED, false);
                    ContentResolver.setIsSyncable(account, CalendarContract.AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(account, CalendarContract.AUTHORITY,
                            syncCalendar);
                }

                // Set up email syncing (it's always syncable, but we respect the user's choice
                // for whether to enable it now)
                boolean syncEmail = false;
                if (options.containsKey(OPTIONS_EMAIL_SYNC_ENABLED) &&
                        options.getBoolean(OPTIONS_EMAIL_SYNC_ENABLED)) {
                    syncEmail = true;
                }
                ContentResolver.setIsSyncable(account, EmailContent.AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, EmailContent.AUTHORITY,
                        syncEmail);

                Bundle b = new Bundle();
                b.putString(AccountManager.KEY_ACCOUNT_NAME, options.getString(OPTIONS_USERNAME));
                b.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                return b;
            // 2) The other case is that we're creating a new account from an Account manager
            //    activity.  In this case, we add an intent that will be used to gather the
            //    account information...
            } else {
                Bundle b = new Bundle();
                Intent intent =
                    AccountSetupFinal.actionGetCreateAccountIntent(AuthenticatorService.this,
                            accountType);
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                b.putParcelable(AccountManager.KEY_INTENT, intent);
                return b;
            }
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                Bundle options) {
            return null;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                String authTokenType, Bundle loginOptions) throws NetworkErrorException {
            return null;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            // null means we don't have compartmentalized authtoken types
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                String[] features) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                String authTokenType, Bundle loginOptions) {
            return null;
        }

    }
    //[BUGFIX]-Add-BEGIN by TSNJ.zhujian.shao,11/05/2014,739308,
    //show wraning when syncing the first
    private  class AThread extends Thread {
    private Context context;
    private Handler mh;
    private  boolean tempUserOperation=false;
    public AThread(Context c) {
        context = c;
    }

    public Handler getHandler() {
        return mh;
    }

    public void run() {
        Looper.prepare();

        android.os.Process
                .setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        //android.os.Process.setCanSelfBackground(false);
        mh = new Handler() {
            public void handleMessage(Message msg) {
                // add for sync to tip
                if (msg.what == REQUEST_SYNC_INIT) {
                    handleTip(msg);
                }else if(msg.what==REQUEST_SYNC_WIFI){
                   ContentResolver.setMasterSyncAutomatically(true);
                }
            }
        };
        Looper.loop();
    }

    private void handleTip(Message msg) {

    AlertDialog.Builder mDialogBuilder    = new AlertDialog.Builder(context);
        mDialogBuilder.setTitle(R.string.tips_sync_alter_title);
        mDialogBuilder.setMessage(R.string.tips_sync_alter_content);
        mDialogBuilder.setCancelable(true);
        mDialogBuilder.setPositiveButton(R.string.tips_sync_alter_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                       tempUserOperation=true;
                    }
                }).setNegativeButton(R.string.tips_sync_alter_no,new DialogInterface.OnClickListener(){

                 @Override
                 public void onClick(DialogInterface dialog, int whichButton) {
                  // TODO Auto-generated method stub
                   tempUserOperation=false;

                 }

              });
        Dialog mDialog = mDialogBuilder.create();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
               if(tempUserOperation){
                    ContentResolver.setMasterSyncAutomatically(true);
               }else{
                    ContentResolver.setMasterSyncAutomatically(false);
               }
            }
        });
        mDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if(!mDialog.isShowing()){
            mDialog.show();
            }
        }
    }
  //[BUGFIX]-Add-END by TSNJ zhujian,shao
    @Override
    public IBinder onBind(Intent intent) {
        if (AccountManager.ACTION_AUTHENTICATOR_INTENT.equals(intent.getAction())) {
            return new Authenticator(this).getIBinder();
        } else {
            return null;
        }
    }
}
