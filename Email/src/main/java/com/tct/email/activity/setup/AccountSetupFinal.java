/*
 * Copyright (C) 2014 The Android Open Source Project
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
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-922404  2015/02/11   zhaotianyong    [TMO-EU][SSV]The configuration of email is not effective
 *BUGFIX-934322  2015/02/17   gengkexue       [REG][Force Close][Email]Email force close after tap next icon when login email account
 *BUGFIX-921199  2015/03/04   zheng.zou       [Android5.0][Exchange][Compatibility]Incoming server settings screen and account options screen are displayed incompletely on Alto 5 LATAM.
 *BUGFIX-936272  2015/03/05   zhaotianyong    [America Movil][SSV]Add Email configuration in general setting.
 *BUGFIX-943810  2015/03/09   zhaotianyong    [TMO][SSV]Add AT and HR for SSV
 *BUGFIX-936393  2015/03/12    zhichuan.wei    [SDM][Email] “def_email_account2ReloginValue” SDM ID is not working
 *BUGFIX-952593  2015/03/23   zheng.zou       [Scenario Test][Email]Can login repeated email account in landscape mode
 *BUGFIX-951327  2015/03/24   zheng.zou       [Android5.0][Email]One email accounts can log in many times.
 *BUGFIX-955397  2015/03/25   ke.ma           [America][SSV]When enter into email,can't find "iClaro"display.
 *BUGFIX-963376  2015/04/17   gangjin.weng    [SSV] Please change default email signature.
 *BUGFIX-985156  2015/04/30   zheng.zou        [HOMO][Orange][17] Orange and Wanadoo email_incoming settings
 *BUGFIX-996057  2015/05/12   junwei-xu        [Email]Duplicate preset hotmail account in Email
 *BUGFIX-998426  2015/05/14   junwei-xu       [HOMO][HOMO][TMO ] T-Mobile FOC requirement on ranged product but our open marketing version.
 *BUGFIX-969854  2015/05/26   junwei-xu       [HOMO][ALWE][E-Mail][Settings][Country-Profile][UI] Update needed for German e-mail provider Web.de and Gmx.de.
 *BUGFIX-1016597 2015/06/08   junwei-xu       [Force close][Android5.0][Email]Email FC when login with no network connection.
 *BUGFIX-1016597 2015/07/01   xujian          [GAPP][Calendar]Appear exchange server when login pop server from Calendar
 *BUGFIX-1026896 2015/07/01   junwei-xu       [Monitor][Email]Configure account interface shows information blank.
 *BUGFIX-1036900 2015/07/01   xujian          [SW][Email][FC][Monitor]Happen email force close when input password.
 *BUGFIX-1042126 2015/07/13   junwei-xu       [Mail] New version cannot configure/connect IMAP hotmail account
 *BUGFIX-1046659 2015/07/20   junwei-xu       [GAPP][Email]An account with the same type can be created twice
 *BUGFIX-1062845 2015/08/11   junwei-xu       [America][SSV] "def_Email_account_provider1_domain" is wrong when insert Puerto Rico card.
 *BUGFIX-1067884 2015/8/14    yanhua.chen     [SW][Monitor][FC][Email]The interface of account list display no account and can't add account
 *BUGFIX-1046840 2015/08/06   jian.xu         [America][SSV]Email signature is wrong when insert Colombia card
 *FEATURE-664213 2015/09/23   tao.gan         [Email]Idol4 ssv new plan
 *BUGFIX-1093309 2015/09/29   junwei-xu       <13340Track><26><CDR-EAS-030>Synchronization Scope—Calendar Events
 *BUGFIX-897367  2015/11/10   junwei-xu       [TestTrigger]Abnormal behavior when I try to create a exchange account
 *BUGFIX-1030520    2015/12/02    zheng.zou     [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created
 *BUGFIX-1496266 2016/01/26   jian.xu         [Email]Can exist two same accounts in Email.
 *BUGFIX-1652820 2016/02/23   junwei-xu       [Email]Tap back key invaild when log in the same account again.
 *FEATURE-1804126 2016/03/14  tianjing.su     [Email]Show warning to user if the version of Exchange is not same with Email
 *BUGFIX-1820548 2016/03/14  jin.dong         [Monitor][Force Close][Email]FC happens when create exchange account with tcl account
 ===========================================================================
 */

package com.tct.email.activity.setup;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.os.SystemProperties;

import com.tct.email.R;
import com.tct.emailcommon.VendorPolicyLoader;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.service.SyncWindow;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.email.provider.Utilities;
import com.tct.email.service.EmailServiceUtils;

import java.net.URISyntaxException;
import java.util.*;
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

public class AccountSetupFinal extends AccountSetupActivity
        implements AccountFinalizeFragment.Callback,
        AccountSetupNoteDialogFragment.Callback, AccountCreationFragment.Callback,
        AccountCheckSettingsFragment.Callback, SecurityRequiredDialogFragment.Callback,
        CheckSettingsErrorDialogFragment.Callback, CheckSettingsProgressDialogFragment.Callback,
        AccountSetupTypeFragment.Callback, AccountSetupNamesFragment.Callback,
        AccountSetupOptionsFragment.Callback, AccountSetupBasicsFragment.Callback,
        AccountServerBaseFragment.Callback, AccountSetupCredentialsFragment.Callback,
        DuplicateAccountDialogFragment.Callback, AccountSetupABFragment.Callback,AccountWarningFragment.Callback,
		PreDefineAccountProviderFragment.Callback{

    /**
     * Direct access for forcing account creation For use by continuous
     * automated test system (e.g. in conjunction with monkey tests) === Support
     * for automated testing == This activity can also be launched directly via
     * INTENT_FORCE_CREATE_ACCOUNT. This is intended only for use by continuous
     * test systems, and is currently only available when
     * {@link ActivityManager#isRunningInTestHarness()} is set. To use this
     * mode, you must construct an intent which contains all necessary
     * information to create the account. No connection checking is done, so the
     * account may or may not actually work. Here is a sample command, for a
     * gmail account "test_account" with a password of "test_password". $ adb
     * shell am start -a com.tct.email.FORCE_CREATE_ACCOUNT \ -e EMAIL
     * test_account@gmail.com \ -e USER "Test Account Name" \ -e INCOMING
     * imap+ssl+://test_account:test_password@imap.gmail.com \ -e OUTGOING
     * smtp+ssl+://test_account:test_password@smtp.gmail.com Note: For accounts
     * that require the full email address in the login, encode the @ as %40.
     * Note: Exchange accounts that require device security policies cannot be
     * created automatically. For accounts that correspond to services in
     * providers.xml you can also use the following form $adb shell am start -a
     * com.tct.email.FORCE_CREATE_ACCOUNT \ -e EMAIL test_account@gmail.com
     * \ -e PASSWORD test_password and the appropriate incoming/outgoing
     * information will be filled in automatically.
     */
    private static String INTENT_FORCE_CREATE_ACCOUNT;
    private static final String EXTRA_FLOW_MODE = "FLOW_MODE";
    private static final String EXTRA_FLOW_ACCOUNT_TYPE = "FLOW_ACCOUNT_TYPE";
    private static final String EXTRA_CREATE_ACCOUNT_EMAIL = "EMAIL";
    private static final String EXTRA_CREATE_ACCOUNT_USER = "USER";
    private static final String EXTRA_CREATE_ACCOUNT_PASSWORD = "PASSWORD";
    private static final String EXTRA_CREATE_ACCOUNT_INCOMING = "INCOMING";
    private static final String EXTRA_CREATE_ACCOUNT_OUTGOING = "OUTGOING";
    private static final String EXTRA_CREATE_ACCOUNT_SYNC_LOOKBACK = "SYNC_LOOKBACK";

    private static final String CREATE_ACCOUNT_SYNC_ALL_VALUE = "ALL";

    private static final Boolean DEBUG_ALLOW_NON_TEST_HARNESS_CREATION = false;

    protected static final String ACTION_JUMP_TO_INCOMING = "jumpToIncoming";
    protected static final String ACTION_JUMP_TO_OUTGOING = "jumpToOutgoing";
    protected static final String ACTION_JUMP_TO_OPTIONS = "jumpToOptions";

    private static final String SAVESTATE_KEY_IS_PROCESSING = "AccountSetupFinal.is_processing";
    private static final String SAVESTATE_KEY_STATE = "AccountSetupFinal.state";
    private static final String SAVESTATE_KEY_PROVIDER = "AccountSetupFinal.provider";
    private static final String SAVESTATE_KEY_AUTHENTICATOR_RESPONSE = "AccountSetupFinal.authResp";
    private static final String SAVESTATE_KEY_REPORT_AUTHENTICATOR_ERROR =
            "AccountSetupFinal.authErr";
    private static final String SAVESTATE_KEY_IS_PRE_CONFIGURED = "AccountSetupFinal.preconfig";
    private static final String SAVESTATE_KEY_SKIP_AUTO_DISCOVER = "AccountSetupFinal.noAuto";
    private static final String SAVESTATE_KEY_PASSWORD_FAILED = "AccountSetupFinal.passwordFailed";
    //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_S
    private static final String SAVESTATE_KEY_ACCOUNT_CREATE_COMPLETE = "AccountSetupFinal.accountCreateComplete";
    //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_E

    private static final String CONTENT_FRAGMENT_TAG = "AccountSetupContentFragment";
    private static final String CREDENTIALS_BACKSTACK_TAG = "AccountSetupCredentialsFragment";

    // Collecting initial email and password
    private static final int STATE_BASICS = 0;
    // Show the user some interstitial message after email entry
    private static final int STATE_BASICS_POST = 1;
    // Account is not pre-configured, query user for account type
    private static final int STATE_TYPE = 2;
    // Account is pre-configured, but the user picked a different protocol
    private static final int STATE_AB = 3;
    // Collect initial password or oauth token
    private static final int STATE_CREDENTIALS = 4;
    // Account is a pre-configured account, run the checker
    private static final int STATE_CHECKING_PRECONFIGURED = 5;
    // Auto-discovering exchange account info, possibly other protocols later
    private static final int STATE_AUTO_DISCOVER = 6;
    // User is entering incoming settings
    private static final int STATE_MANUAL_INCOMING = 7;
    // We're checking incoming settings
    private static final int STATE_CHECKING_INCOMING = 8;
    // User is entering outgoing settings
    private static final int STATE_MANUAL_OUTGOING = 9;
    // We're checking outgoing settings
    private static final int STATE_CHECKING_OUTGOING = 10;
    // User is entering sync options
    private static final int STATE_OPTIONS = 11;
    // We're creating the account
    private static final int STATE_CREATING = 12;
    // User is entering account name and real name
    private static final int STATE_NAMES = 13;
    // we're finalizing the account
    private static final int STATE_FINALIZE = 14;
    //[BUGFIX]-Mod-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
    //[Email][Perso]Cann't customized email accoun
    private static final int STATE_LIST = 15;
    //[BUGFIX]-Mod-END by SCDTABLET.yingjie.chen@tcl.com
    private int mState = STATE_BASICS;

    private boolean mIsProcessing = false;
    private boolean mForceCreate = false;
    private boolean mReportAccountAuthenticatorError;
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse;
    // True if this provider is found in our providers.xml, set after Basics
    private boolean mIsPreConfiguredProvider = false;
    // True if the user selected manual setup
    private boolean mSkipAutoDiscover = false;
    // True if validating the pre-configured provider failed and we want manual
    // setup
    private boolean mPreConfiguredFailed = false;

    private VendorPolicyLoader.Provider mProvider;
    private boolean mPasswordFailed;

    private static final int OWNER_NAME_LOADER_ID = 0;
    private String mOwnerName;

    private static final int EXISTING_ACCOUNTS_LOADER_ID = 1;
    //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_S
    private static final int EXISTING_HOSTAUTH_LOADER_ID = 2;
    private final static String HOSTAUTH_WHERE_PROTOCOL = EmailContent.HostAuthColumns.PROTOCOL + "<>?";
    private List<String> mExistingHostauthLoginList;
    //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_E
    private Map<String, String> mExistingAccountsMap;
    //[FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368,
    public static final String KEY_DOMAIN_NAME = "domain_name";
    public static final String KEY_LABEL_NAME = "label_name";
    private String mDomain;
    private String mLabel;
    //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_S
    private boolean mIsAccountCreateComplete;
    //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_E
    //[FEATURE]-Add-END by TSNJ,wei.huang,
    //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, ADD_S
    private boolean mIsDuplicateAccountDialogShown;
    //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, ADD_E

    public static Intent actionNewAccountIntent(final Context context) {
        final Intent i = new Intent(context, AccountSetupFinal.class);
        i.putExtra(EXTRA_FLOW_MODE, SetupDataFragment.FLOW_MODE_NORMAL);
        return i;
    }

    // FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368
     public static Intent actionNewAccountIntent(final Context context,String domainName,String labelName){
         final Intent i = new Intent(context, AccountSetupFinal.class);
         i.putExtra(EXTRA_FLOW_MODE, SetupDataFragment.FLOW_MODE_NORMAL);
         i.putExtra(KEY_DOMAIN_NAME, domainName);
         i.putExtra(KEY_LABEL_NAME, labelName);
         return i;
     }
    // FEATURE]-Add-END by TSNJ,wei.huang,

    public static Intent actionNewAccountWithResultIntent(final Context context) {
        final Intent i = new Intent(context, AccountSetupFinal.class);
        i.putExtra(EXTRA_FLOW_MODE, SetupDataFragment.FLOW_MODE_NO_ACCOUNTS);
        return i;
    }

    // FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368
    public static Intent actionNewAccountWithResultIntent(final Context context, String domainName,
            String labelName) {
        final Intent i = new Intent(context, AccountSetupFinal.class);
        i.putExtra(KEY_DOMAIN_NAME, domainName);
        i.putExtra(KEY_LABEL_NAME, labelName);
        i.putExtra(EXTRA_FLOW_MODE, SetupDataFragment.FLOW_MODE_NO_ACCOUNTS);
        return i;
    }
    // FEATURE]-Add-END by TSNJ,wei.huang,

    public static Intent actionGetCreateAccountIntent(final Context context,
            final String accountManagerType) {
        final Intent i = new Intent(context, AccountSetupFinal.class);
        i.putExtra(EXTRA_FLOW_MODE, SetupDataFragment.FLOW_MODE_ACCOUNT_MANAGER);
        i.putExtra(EXTRA_FLOW_ACCOUNT_TYPE, accountManagerType);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (INTENT_FORCE_CREATE_ACCOUNT == null) {
            INTENT_FORCE_CREATE_ACCOUNT = getString(R.string.intent_force_create_email_account);
        }

        setContentView(R.layout.account_setup_activity);
        // FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368
        {
            mDomain = intent.getStringExtra(KEY_DOMAIN_NAME);
            mLabel = intent.getStringExtra(KEY_LABEL_NAME);
        }
        // FEATURE]-Add-END by TSNJ,wei.huang,

        if (savedInstanceState != null) {
            mIsProcessing = savedInstanceState.getBoolean(SAVESTATE_KEY_IS_PROCESSING, false);
            mState = savedInstanceState.getInt(SAVESTATE_KEY_STATE, STATE_OPTIONS);
            mProvider = (VendorPolicyLoader.Provider)
                    savedInstanceState.getSerializable(SAVESTATE_KEY_PROVIDER);
            mAccountAuthenticatorResponse =
                    savedInstanceState.getParcelable(SAVESTATE_KEY_AUTHENTICATOR_RESPONSE);
            mReportAccountAuthenticatorError =
                    savedInstanceState.getBoolean(SAVESTATE_KEY_REPORT_AUTHENTICATOR_ERROR);
            mIsPreConfiguredProvider =
                    savedInstanceState.getBoolean(SAVESTATE_KEY_IS_PRE_CONFIGURED);
            mSkipAutoDiscover = savedInstanceState.getBoolean(SAVESTATE_KEY_SKIP_AUTO_DISCOVER);
            mPasswordFailed = savedInstanceState.getBoolean(SAVESTATE_KEY_PASSWORD_FAILED);
            //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_S
            mIsAccountCreateComplete = savedInstanceState.getBoolean(SAVESTATE_KEY_ACCOUNT_CREATE_COMPLETE);
            //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_E
        } else {
            // If we're not restoring from a previous state, we want to
            // configure the initial screen

            // Set aside incoming AccountAuthenticatorResponse, if there was any
            mAccountAuthenticatorResponse = getIntent()
                    .getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
            if (mAccountAuthenticatorResponse != null) {
                // When this Activity is called as part of account
                // authentification flow,
                // we are responsible for eventually reporting the result
                // (success or failure) to
                // the account manager. Most exit paths represent an failed or
                // abandoned setup,
                // so the default is to report the error. Success will be
                // reported by the code in
                // AccountSetupOptions that commits the finally created account.
                mReportAccountAuthenticatorError = true;
            }

            // Initialize the SetupDataFragment
            if (INTENT_FORCE_CREATE_ACCOUNT.equals(action)) {
                mSetupData.setFlowMode(SetupDataFragment.FLOW_MODE_FORCE_CREATE);
            } else {
                final int intentFlowMode = intent.getIntExtra(EXTRA_FLOW_MODE,
                        SetupDataFragment.FLOW_MODE_UNSPECIFIED);
                final String flowAccountType = intent.getStringExtra(EXTRA_FLOW_ACCOUNT_TYPE);
                mSetupData.setAmProtocol(
                        EmailServiceUtils.getProtocolFromAccountType(this, flowAccountType));
                mSetupData.setFlowMode(intentFlowMode);
            }
             //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
             //[Email][Perso]Cann't customized email account
              mState = STATE_LIST;
             //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.com
            //mState = STATE_BASICS;
            // Support unit testing individual screens
            if (TextUtils.equals(ACTION_JUMP_TO_INCOMING, action)) {
                mState = STATE_MANUAL_INCOMING;
            } else if (TextUtils.equals(ACTION_JUMP_TO_OUTGOING, action)) {
                mState = STATE_MANUAL_OUTGOING;
            } else if (TextUtils.equals(ACTION_JUMP_TO_OPTIONS, action)) {
                mState = STATE_OPTIONS;
            }
            updateContentFragment(false /* addToBackstack */);
            mPasswordFailed = false;
        }
        //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_S
        if (mIsAccountCreateComplete){
            //TS: zheng.zou 2016-02-04 EMAIL BUGFIX-1456516 MOD_S
            Intent resultIntent = new Intent();
            resultIntent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mSetupData.getEmail());
            resultIntent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mSetupData.getIncomingServiceInfo(this).accountType);
            setResult(RESULT_OK,resultIntent);
//            setResult(RESULT_OK);
            //TS: zheng.zou 2016-02-04 EMAIL BUGFIX-1456516 MOD_E
        }
        //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_E

        if (!mIsProcessing
                && mSetupData.getFlowMode() == SetupDataFragment.FLOW_MODE_FORCE_CREATE) {
            /**
             * To support continuous testing, we allow the forced creation of
             * accounts. This works in a manner fairly similar to automatic
             * setup, in which the complete server Uri's are available, except
             * that we will also skip checking (as if both checks were true) and
             * all other UI. email: The email address for the new account user:
             * The user name for the new account incoming: The URI-style string
             * defining the incoming account outgoing: The URI-style string
             * defining the outgoing account
             */
            final String email = intent.getStringExtra(EXTRA_CREATE_ACCOUNT_EMAIL);
            final String user = intent.getStringExtra(EXTRA_CREATE_ACCOUNT_USER);
            final String password = intent.getStringExtra(EXTRA_CREATE_ACCOUNT_PASSWORD);
            final String incoming = intent.getStringExtra(EXTRA_CREATE_ACCOUNT_INCOMING);
            final String outgoing = intent.getStringExtra(EXTRA_CREATE_ACCOUNT_OUTGOING);
            final String syncLookbackText = intent
                    .getStringExtra(EXTRA_CREATE_ACCOUNT_SYNC_LOOKBACK);
            final int syncLookback;
            if (TextUtils.equals(syncLookbackText, CREATE_ACCOUNT_SYNC_ALL_VALUE)) {
                syncLookback = SyncWindow.SYNC_WINDOW_ALL;
            } else {
                syncLookback = -1;
            }
            // If we've been explicitly provided with all the details to fill in
            // the account, we
            // can use them
            final boolean explicitForm = !(TextUtils.isEmpty(user) ||
                    TextUtils.isEmpty(incoming) || TextUtils.isEmpty(outgoing));
            // If we haven't been provided the details, but we have the
            // password, we can look up
            // the info from providers.xml
            final boolean implicitForm = !TextUtils.isEmpty(password) && !explicitForm;
            if (TextUtils.isEmpty(email) || !(explicitForm || implicitForm)) {
                LogUtils.e(LogUtils.TAG, "Force account create requires extras EMAIL, " +
                        "USER, INCOMING, OUTGOING, or EMAIL and PASSWORD");
                finish();
                return;
            }

            if (implicitForm) {
                final String[] emailParts = email.split("@");
                final String domain = emailParts[1].trim();
                mProvider = AccountSettingsUtils.findProviderForDomain(this, domain);
                if (mProvider == null) {
                    LogUtils.e(LogUtils.TAG, "findProviderForDomain couldn't find provider");
                    finish();
                    return;
                }
                mIsPreConfiguredProvider = true;
                mSetupData.setEmail(email);
                boolean autoSetupCompleted = finishAutoSetup();
                if (!autoSetupCompleted) {
                    LogUtils.e(LogUtils.TAG, "Force create account failed to create account");
                    finish();
                    return;
                }
                final Account account = mSetupData.getAccount();
                final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
                recvAuth.mPassword = password;
                final HostAuth sendAuth = account.getOrCreateHostAuthSend(this);
                sendAuth.mPassword = password;
            } else {
                final Account account = mSetupData.getAccount();

                try {
                    final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
                    recvAuth.setHostAuthFromString(incoming);

                    final HostAuth sendAuth = account.getOrCreateHostAuthSend(this);
                    sendAuth.setHostAuthFromString(outgoing);
                } catch (URISyntaxException e) {
                    // If we can't set up the URL, don't continue
                    //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                    Utility.showToast(this, R.string.account_setup_username_password_toast);
                    //Toast.makeText(this, R.string.account_setup_username_password_toast,
                    //        Toast.LENGTH_LONG)
                    //        .show();
                    finish();
                    return;
                }

                populateSetupData(user, email);
                // We need to do this after calling populateSetupData(), because
                // that will
                // overwrite it with the default values.
                if (syncLookback >= SyncWindow.SYNC_WINDOW_ACCOUNT &&
                        syncLookback <= SyncWindow.SYNC_WINDOW_ALL) {
                    account.mSyncLookback = syncLookback;
                }
            }

            mState = STATE_OPTIONS;
            updateContentFragment(false /* addToBackstack */);
            getFragmentManager().executePendingTransactions();

            if (!DEBUG_ALLOW_NON_TEST_HARNESS_CREATION &&
                    !ActivityManager.isRunningInTestHarness()) {
                LogUtils.e(LogUtils.TAG,
                        "ERROR: Force account create only allowed while in test harness");
                finish();
                return;
            }

            mForceCreate = true;
        }

        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_S
        if (mHasNoPermission){
            return;
        }
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_E

        // Launch a loader to look up the owner name. It should be ready well in
        // advance of
        // the time the user clicks next or manual.
        getLoaderManager().initLoader(OWNER_NAME_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
                        return new CursorLoader(AccountSetupFinal.this,
                                ContactsContract.Profile.CONTENT_URI,
                                new String[] {
                                    ContactsContract.Profile.DISPLAY_NAME_PRIMARY
                                },
                                null, null, null);
                    }

                    @Override
                    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
                        if (data != null && data.moveToFirst()) {
                            mOwnerName = data.getString(data.getColumnIndex(
                                    ContactsContract.Profile.DISPLAY_NAME_PRIMARY));
                        }
                    }

                    @Override
                    public void onLoaderReset(final Loader<Cursor> loader) {
                    }
                });

        // Launch a loader to cache some info about existing accounts so we can
        // dupe-check against
        // them.
        getLoaderManager().initLoader(EXISTING_ACCOUNTS_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        return new CursorLoader(AccountSetupFinal.this, Account.CONTENT_URI,
                                new String[] {
                                        AccountColumns.EMAIL_ADDRESS,
                                        AccountColumns.DISPLAY_NAME
                                },
                                null, null, null);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        if (data == null) {
                            mExistingAccountsMap = null;
                            return;
                        }

                        mExistingAccountsMap = new HashMap<String, String>();

                        final int emailColumnIndex = data.getColumnIndex(
                                AccountColumns.EMAIL_ADDRESS);
                        final int displayNameColumnIndex =
                                data.getColumnIndex(AccountColumns.DISPLAY_NAME);
                        //TS: zheng.zou 2015-03-23 EMAIL BUGFIX-936272 MOD_S
                        if (data.moveToFirst()) {
                            do {
                                final String email = data.getString(emailColumnIndex);
                                final String displayName = data.getString(displayNameColumnIndex);
                                //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_S
                                mExistingAccountsMap.put(convertDomainToLowerCase(email),
                                        TextUtils.isEmpty(displayName) ? email : displayName);
                                //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_E
                            } while (data.moveToNext());
                        }
//                        while (data.moveToNext()) {
//                            final String email = data.getString(emailColumnIndex);
//                            final String displayName = data.getString(displayNameColumnIndex);
//                            mExistingAccountsMap.put(email,
//                                    TextUtils.isEmpty(displayName) ? email : displayName);
//                        }
                        //TS: zheng.zou 2015-03-23 EMAIL BUGFIX-936272 MOD_E
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        mExistingAccountsMap = null;
                    }
                });

        //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_S
        // Launch a loader to cache some info about existing accounts of eas protocol which use add-domain
        // so we can dupe-check against them.
        getLoaderManager().initLoader(EXISTING_HOSTAUTH_LOADER_ID, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        return new CursorLoader(AccountSetupFinal.this, HostAuth.CONTENT_URI,
                                new String[] {EmailContent.HostAuthColumns.LOGIN},
                                HOSTAUTH_WHERE_PROTOCOL, new String[] {HostAuth.SCHEME_SMTP}, null);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        if (data == null) {
                            mExistingHostauthLoginList = null;
                            return;
                        }

                        mExistingHostauthLoginList = new ArrayList<String>();

                        final int loginColumnIndex = data.getColumnIndex(EmailContent.HostAuthColumns.LOGIN);
                        if (data.moveToFirst()) {
                            do {
                                final String hostAuthLogin = data.getString(loginColumnIndex);
                                mExistingHostauthLoginList.add(convertDomainToLowerCase(hostAuthLogin));
                            } while (data.moveToNext());
                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        mExistingHostauthLoginList = null;
                    }
                });
        //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_E
    }

    //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_S
    /** it used to convert the email address's domain to lower case */
    private String convertDomainToLowerCase(String email) {
        if (TextUtils.isEmpty(email)) {
            return email;
        }
        if (!email.contains("@")) {
            return email;
        }
        final String[] emailParts = email.split("@");
        //TS: jin.dong 2016-03-16 EMAIL BUGFIX-1820548 ADD_S
        if (emailParts.length == 1) {
            return emailParts[0];
        }
        //TS: jin.dong 2016-03-16 EMAIL BUGFIX-1820548 ADD_E
        return emailParts[0] + "@" + emailParts[1].toLowerCase();
    }
    //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_E

    @Override
    protected void onResume() {
        super.onResume();
        if (mForceCreate) {
            mForceCreate = false;

            // We need to do this after onCreate so that we can ensure that the
            // fragment is
            // fully created before querying it.
            // This will call initiateAccountCreation() for us
            proceed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVESTATE_KEY_IS_PROCESSING, mIsProcessing);
        outState.putInt(SAVESTATE_KEY_STATE, mState);
        outState.putSerializable(SAVESTATE_KEY_PROVIDER, mProvider);
        outState.putParcelable(SAVESTATE_KEY_AUTHENTICATOR_RESPONSE, mAccountAuthenticatorResponse);
        outState.putBoolean(SAVESTATE_KEY_REPORT_AUTHENTICATOR_ERROR,
                mReportAccountAuthenticatorError);
        outState.putBoolean(SAVESTATE_KEY_IS_PRE_CONFIGURED, mIsPreConfiguredProvider);
        outState.putBoolean(SAVESTATE_KEY_PASSWORD_FAILED, mPasswordFailed);
        //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_S
        outState.putBoolean(SAVESTATE_KEY_ACCOUNT_CREATE_COMPLETE, mIsAccountCreateComplete);
        //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_E
    }

    /**
     * Swap in the new fragment according to mState. This pushes the current
     * fragment onto the back stack, so only call it once per transition.
     */
    private void updateContentFragment(boolean addToBackstack) {
        final AccountSetupFragment f;
        String backstackTag = null;

        switch (mState) {
            //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
            //[Email][Perso]Cann't customized email account
            case STATE_LIST:
                f = PreDefineAccountProviderFragment.newInstance();
                break;
            //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.
            case STATE_BASICS:
                f = AccountSetupBasicsFragment.newInstance();
                //[FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368,
                Bundle data=new Bundle();
                data.putString(KEY_DOMAIN_NAME,mDomain);
                data.putString(KEY_LABEL_NAME,mLabel);
                f.setArguments(data);
                // FEATURE]-Add-END by TSNJ,wei.huang,
                break;
            case STATE_TYPE:
                f = AccountSetupTypeFragment.newInstance();
                break;
            case STATE_AB:
                f = AccountSetupABFragment.newInstance(mSetupData.getEmail(),
                        mSetupData.getAmProtocol(), mSetupData.getIncomingProtocol(this));
                break;
            case STATE_CREDENTIALS:
                f = AccountSetupCredentialsFragment.newInstance(mSetupData.getEmail(),
                        mSetupData.getIncomingProtocol(this), mSetupData.getClientCert(this),
                        mPasswordFailed, false /* standalone */);
                backstackTag = CREDENTIALS_BACKSTACK_TAG;
                break;
            case STATE_MANUAL_INCOMING:
                //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen,04/29/2016,2012077
                // [Email]Landing imap mailboxes, Server appears as 163.com
                final Account account = mSetupData.getAccount();
                final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
                if (TextUtils.isEmpty(mSetupData.getIncomingProtocol())) {
                    mSetupData.setIncomingProtocol(this, recvAuth.mProtocol);
                }
                if (!(HostAuth.SCHEME_EAS.equals(mSetupData.getIncomingProtocol()))) {
                    final EmailServiceUtils.EmailServiceInfo info = mSetupData
                            .getIncomingServiceInfo(this);
                    //[BUGFIX]-Add-BEGIN by SCDTABLET.shujing.jin,05/11/2016,2116445,
                    if (recvAuth.mAddress != null && (recvAuth.mAddress.contains("imap")||recvAuth.mAddress.contains("pop3"))) {

                    } else {
                    //[BUGFIX]-Add-END by SCDTABLET.shujing.jin
                    recvAuth.mAddress = AccountSettingsUtils.inferServerName(this,
                    recvAuth.mAddress, info.inferPrefix, null);
                    }//[BUGFIX]-Add-by SCDTABLET.shujing.jin,05/11/2016,2116445,
                }
                //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen
                f = AccountSetupIncomingFragment.newInstance(false);
                break;
            case STATE_MANUAL_OUTGOING:
                f = AccountSetupOutgoingFragment.newInstance(false);
                break;
            case STATE_OPTIONS:
                f = AccountSetupOptionsFragment.newInstance();
                break;
            case STATE_NAMES:
                f = AccountSetupNamesFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("Incorrect state " + mState);
        }
        f.setState(mState);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.setup_fragment_container, f, CONTENT_FRAGMENT_TAG);
        if (addToBackstack) {
            ft.addToBackStack(backstackTag);
        }
        ft.commit();

        final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final View fragment_container = findViewById(R.id.setup_fragment_container);
        imm.hideSoftInputFromWindow(fragment_container.getWindowToken(),
                0 /* flags: always hide */);
    }

    /**
     * Retrieve the current content fragment
     *
     * @return The content fragment or null if it wasn't found for some reason
     */
    private AccountSetupFragment getContentFragment() {
        return (AccountSetupFragment) getFragmentManager().findFragmentByTag(CONTENT_FRAGMENT_TAG);
    }

    /**
     * Reads the flow state saved into the current fragment and restores mState
     * to it, also resetting the headline at the same time.
     */
    private void resetStateFromCurrentFragment() {
        AccountSetupFragment f = getContentFragment();
        mState = f.getState();
    }

    /**
     * Main choreography function to handle moving forward through scenes.
     * Moving back should be generally handled for us by the back stack
     */
    protected void proceed() {
        mIsProcessing = false;
        final AccountSetupFragment oldContentFragment = getContentFragment();
        if (oldContentFragment != null) {
            oldContentFragment.setNextButtonEnabled(true);
        }

        getFragmentManager().executePendingTransactions();

        switch (mState) {
            //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
            //[Email][Perso]Cann't customized email account
            case STATE_LIST:
                 mState = STATE_BASICS;
                 updateContentFragment(true /* addToBackstack */);
                 break;
            //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.com
            case STATE_BASICS:
                final boolean advance = onBasicsComplete();
                //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, ADD_S
                final AccountSetupBasicsFragment basicsFragment = (AccountSetupBasicsFragment) getContentFragment();
                final String email = basicsFragment.getEmail();
                //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_S
                final String duplicateAccountName =
                        mExistingAccountsMap != null ? mExistingAccountsMap.get(convertDomainToLowerCase(email)) : null;
                //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_E
                if (!TextUtils.isEmpty(duplicateAccountName)) {
                    break;
                }
                //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, ADD_E
                if (!advance) {
                    mState = STATE_BASICS_POST;
                    break;
                } // else fall through
            case STATE_BASICS_POST:
                if (shouldDivertToManual()) {
                    mSkipAutoDiscover = true;
                    mIsPreConfiguredProvider = false;
                    mState = STATE_TYPE;
                } else {
                    mSkipAutoDiscover = false;
                    if (mIsPreConfiguredProvider) {
                        if (!TextUtils.isEmpty(mSetupData.getAmProtocol()) &&
                                !TextUtils.equals(mSetupData.getAmProtocol(),
                                        mSetupData.getIncomingProtocol(this))) {
                            mState = STATE_AB;
                        } else {
                            mState = STATE_CREDENTIALS;
                            if (possiblyDivertToGmail()) {
                                return;
                            }
                        }
                    } else {
                        final String amProtocol = mSetupData.getAmProtocol();
                        if (!TextUtils.isEmpty(amProtocol)) {
                            mSetupData.setIncomingProtocol(this, amProtocol);
                            //TS: junwei-xu 2015-07-01 EMAIL BUGFIX-1026896 ADD_S
                            populateHostAuthsFromSetupData();
                            //TS: junwei-xu 2015-07-01 EMAIL BUGFIX-1026896 ADD_E
                            final Account account = mSetupData.getAccount();
                            setDefaultsForProtocol(account);
                            mState = STATE_CREDENTIALS;
                        } else {
                            mState = STATE_TYPE;
                        }
                    }
                }
                updateContentFragment(true /* addToBackstack */);
                break;
            case STATE_TYPE:
                // We either got here through "Manual Setup" or because we
                // didn't find the provider
                mState = STATE_CREDENTIALS;
                updateContentFragment(true /* addToBackstack */);
                break;
            case STATE_AB:
                if (possiblyDivertToGmail()) {
                    return;
                }
                mState = STATE_CREDENTIALS;
                updateContentFragment(true /* addToBackstack */);
                break;
            case STATE_CREDENTIALS:
                //TS: tianjing.su 2016-03-14 EMAIL FEATURE_1804126 MOD_S
                if(mSetupData != null) {
                    final Account account = mSetupData.getAccount();
                    final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
                    String mProtocol = recvAuth.mProtocol;
                    if(mProtocol.equals(getString(R.string.protocol_eas))){
                        try {
                            int emailVersionCode = getPackageManager().getPackageInfo(getString(R.string.email_package_name), 0).versionCode;
                            int exchangeVersionCode = getPackageManager().getPackageInfo(getString(R.string.intent_exchange_package), 1).versionCode;
                            if(emailVersionCode != exchangeVersionCode){
                               new AccountWarningFragment().show(getFragmentManager(),"dialog warning");
                                break;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                proceedStateCredentials();
                //TS: tianjing.su 2016-03-14 EMAIL FEATURE_1804126 MOD_E
                break;
            case STATE_CHECKING_PRECONFIGURED:
                if (mPreConfiguredFailed) {
                    if (mPasswordFailed) {
                        //TS: junwei-xu 2015-11-10 EMAIL BUGFIX-897367 MOD_S
                        //NOTE: For password failed condition, we use sdm to control whether allow goto manual incoming screen.
                        boolean enableManualEdit = PLFUtils.getBoolean(this, "def_email_manualEditAfterPasswordFailed_on");
                        if (enableManualEdit) {
                            mState = STATE_MANUAL_INCOMING;
                            updateContentFragment(true /* addToBackstack */);
                        } else {
                            // Get rid of the previous instance of the
                            // AccountSetupCredentialsFragment.
                            FragmentManager fm = getFragmentManager();
                            fm.popBackStackImmediate(CREDENTIALS_BACKSTACK_TAG, 0);
                            final AccountSetupCredentialsFragment f = (AccountSetupCredentialsFragment)
                                    getContentFragment();
                            f.setPasswordFailed(mPasswordFailed);
                            resetStateFromCurrentFragment();
                        }
                        //TS: junwei-xu 2015-11-10 EMAIL BUGFIX-897367 MOD_E
                    } else {
                        mState = STATE_MANUAL_INCOMING;
                        updateContentFragment(true /* addToBackstack */);
                    }
                } else {
                    mState = STATE_OPTIONS;
                    updateContentFragment(true /* addToBackstack */);
                }
                break;
            case STATE_AUTO_DISCOVER:
                // TODO: figure out if we can skip past manual setup
                mState = STATE_MANUAL_INCOMING;
                updateContentFragment(true);
                break;
            case STATE_MANUAL_INCOMING:
                //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_S
                //Note: check if this is a duplicate account again
                if ((getContentFragment() != null) && (getContentFragment() instanceof AccountSetupIncomingFragment)) {
                    AccountSetupIncomingFragment f = (AccountSetupIncomingFragment) getContentFragment();
                    String hostAuthLogin = f.getAccountHostAuthLogin();
                    // check email address duplicate
                    final String existingAccountName =
                            mExistingAccountsMap != null ? mExistingAccountsMap.get(convertDomainToLowerCase(hostAuthLogin)) : null;
                    if (!TextUtils.isEmpty(existingAccountName) && !mIsDuplicateAccountDialogShown) {
                        showDuplicateAccountDialog(existingAccountName);
                        mIsDuplicateAccountDialogShown = true;
                        break;
                    }
                    //TS: yanhua.chen 2015-8-14 EMAIL BUGFIX_1067884 MOD_S
                    // check hostauth login duplicate
                    if (mExistingHostauthLoginList != null && !mExistingHostauthLoginList.isEmpty() && !mIsDuplicateAccountDialogShown &&
                            mExistingHostauthLoginList.contains(convertDomainToLowerCase(hostAuthLogin))) {
                    //TS: yanhua.chen 2015-8-14 EMAIL BUGFIX_1067884 MOD_E
                        showDuplicateAccountDialog(hostAuthLogin);
                        mIsDuplicateAccountDialogShown = true;
                        break;
                    }
                }
                //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_E
                onIncomingComplete();
                mState = STATE_CHECKING_INCOMING;
                initiateCheckSettingsFragment(SetupDataFragment.CHECK_INCOMING);
                break;
            case STATE_CHECKING_INCOMING:
                final EmailServiceUtils.EmailServiceInfo serviceInfo =
                        mSetupData.getIncomingServiceInfo(this);
                if (serviceInfo.usesSmtp) {
                    mState = STATE_MANUAL_OUTGOING;
                } else {
                    mState = STATE_OPTIONS;
                }
                updateContentFragment(true /* addToBackstack */);
                break;
            case STATE_MANUAL_OUTGOING:
                onOutgoingComplete();
                mState = STATE_CHECKING_OUTGOING;
                initiateCheckSettingsFragment(SetupDataFragment.CHECK_OUTGOING);
                break;
            case STATE_CHECKING_OUTGOING:
                mState = STATE_OPTIONS;
                updateContentFragment(true /* addToBackstack */);
                break;
            case STATE_OPTIONS:
                mState = STATE_CREATING;
                initiateAccountCreation();
                break;
            case STATE_CREATING:
                mState = STATE_NAMES;
                updateContentFragment(true /* addToBackstack */);
                if (mSetupData.getFlowMode() == SetupDataFragment.FLOW_MODE_FORCE_CREATE) {
                    getFragmentManager().executePendingTransactions();
                    initiateAccountFinalize();
                }
                break;
            case STATE_NAMES:
                initiateAccountFinalize();
                break;
            case STATE_FINALIZE:
                finish();
                break;
            default:
                LogUtils.wtf(LogUtils.TAG, "Unknown state %d", mState);
                break;
        }
    }

    /**
     * Check if we should divert to creating a Gmail account instead
     *
     * @return true if we diverted
     */
    private boolean possiblyDivertToGmail() {
        // TODO: actually divert here
        final EmailServiceUtils.EmailServiceInfo info =
                mSetupData.getIncomingServiceInfo(this);
        if (TextUtils.equals(info.protocol, "gmail")) {
            final Bundle options = new Bundle(1);
            options.putBoolean("allowSkip", false);
            AccountManager.get(this).addAccount("com.google",
                    "mail" /* authTokenType */,
                    null,
                    options,
                    this, null, null);

            finish();
            return true;
        }
        return false;
    }

    /**
     * Block the back key if we are currently processing the "next" key"
     */
    @Override
    public void onBackPressed() {
        if (mIsProcessing) {
            return;
        }
        if (mState == STATE_NAMES) {
            finish();
        } else {
            super.onBackPressed();
        }
        // After super.onBackPressed() our fragment should be in place, so query
        // the state we
        // installed it for
        resetStateFromCurrentFragment();
    }

    @Override
    public void setAccount(Account account) {
        mSetupData.setAccount(account);
    }

    @Override
    public void finish() {
        // If the account manager initiated the creation, and success was not
        // reported,
        // then we assume that we're giving up (for any reason) - report
        // failure.
        if (mReportAccountAuthenticatorError) {
            if (mAccountAuthenticatorResponse != null) {
                mAccountAuthenticatorResponse
                        .onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
                mAccountAuthenticatorResponse = null;
            }
        }
        super.finish();
    }

    @Override
    public void onNextButton() {
        // Some states are handled without UI, block double-presses here
        if (!mIsProcessing) {
            proceed();
        }
    }

    //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_S
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_E
    /**
     * @return true to proceed, false to remain on the current screen
     */
    private boolean onBasicsComplete() {
        final AccountSetupBasicsFragment f = (AccountSetupBasicsFragment) getContentFragment();
        final String email = f.getEmail();

        // Reset the protocol choice in case the user has back-navigated here
        mSetupData.setIncomingProtocol(this, null);

        if (!TextUtils.equals(email, mSetupData.getEmail())) {
            // If the user changes their email address, clear the password
            // failed state
            mPasswordFailed = false;
        }
        mSetupData.setEmail(email);

        final String[] emailParts = email.split("@");
        final String domain = emailParts[1].trim();

        // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_S
        //Whether load the ssv configuration or normal isp preset account
        boolean ssvEnabled = Utilities.ssvEnabled();
        LogUtils.i(LogUtils.TAG, "ssvEnabled : " + ssvEnabled);
        try {
            if(ssvEnabled) {
                mProvider = PLFUtils.findSSVProviderForDomain(this, domain, null);
            }else {
                // [FEATURE]-Add-BEGIN by TSNJ,wei.huang,10/22/2014
                // mProvider = AccountSettingsUtils.findProviderForDomain(this, domain);
                mProvider = AccountSettingsUtils.findProviderForDomain(this,
                        domain, mLabel);
                // [FEATURE]-Add-END by TSNJ,wei.huang
            }

            //No ssv account find,indicate that we should do normal isp account configuration,
            //current sim card is not required ssv sim card
            if(mProvider != null && PLFUtils.NO_SSV_ACOUNT_FIND.equals(mProvider.domain)) {
                mProvider = AccountSettingsUtils.findProviderForDomain(this,
                        domain, mLabel);
            }
        } catch (URISyntaxException e) {
            LogUtils.e(LogUtils.TAG,"URISyntaxException while do the uri initialization");
        }
     // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_E
      //TS: zhaotianyong 2015-02-11 EMAIL BUGFIX-922404 MOD_E
        if (mProvider != null) {
            mIsPreConfiguredProvider = true;
            if (mProvider.note != null) {
                final AccountSetupNoteDialogFragment dialogFragment =
                        AccountSetupNoteDialogFragment.newInstance(mProvider.note);
                dialogFragment.show(getFragmentManager(), AccountSetupNoteDialogFragment.TAG);
                return false;
            } else {
                return finishAutoSetup();
            }
        } else {
            mIsPreConfiguredProvider = false;
            //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_S
            final String existingAccountName =
                    mExistingAccountsMap != null ? mExistingAccountsMap.get(convertDomainToLowerCase(email)) : null;
            //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_E
            //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, MOD_S
            if (!TextUtils.isEmpty(existingAccountName) && !mIsDuplicateAccountDialogShown) {
                showDuplicateAccountDialog(existingAccountName);
                mIsDuplicateAccountDialogShown = true;
                //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, MOD_E
                return false;
            } else {
                populateSetupData(mOwnerName, email);
                mSkipAutoDiscover = false;
                return true;
            }
        }
    }

    private void showDuplicateAccountDialog(final String existingAccountName) {
        final DuplicateAccountDialogFragment dialogFragment =
                DuplicateAccountDialogFragment.newInstance(existingAccountName);
        dialogFragment.show(getFragmentManager(), DuplicateAccountDialogFragment.TAG);
    }

    @Override
    public void onDuplicateAccountDialogDismiss() {
        //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, ADD_S
        mIsDuplicateAccountDialogShown = false;
        //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, ADD_E
        resetStateFromCurrentFragment();
    }

    private boolean shouldDivertToManual() {
        final AccountSetupBasicsFragment f = (AccountSetupBasicsFragment) getContentFragment();
        return f.isManualSetup();
    }

    @Override
    public void onCredentialsComplete(Bundle results) {
        proceed();
    }

    private void collectCredentials() {
        final AccountSetupCredentialsFragment f = (AccountSetupCredentialsFragment)
                getContentFragment();
        final Bundle results = f.getCredentialResults();
        mSetupData.setCredentialResults(results);
        final Account account = mSetupData.getAccount();
        final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
        AccountSetupCredentialsFragment.populateHostAuthWithResults(this, recvAuth,
                mSetupData.getCredentialResults());
        mSetupData.setIncomingCredLoaded(true);
        final EmailServiceUtils.EmailServiceInfo info = mSetupData.getIncomingServiceInfo(this);
      //TS: xujian 2015-07-06 EMAIL BUGFIX-1036900 MOD_S
        if (info != null && info.usesSmtp) {
      //TS: xujian 2015-07-06 EMAIL BUGFIX-1036900 MOD_E
            final HostAuth sendAuth = account.getOrCreateHostAuthSend(this);
            AccountSetupCredentialsFragment.populateHostAuthWithResults(this, sendAuth,
                    mSetupData.getCredentialResults());
            mSetupData.setOutgoingCredLoaded(true);
        }
    }

    @Override
    public void onNoteDialogComplete() {
        finishAutoSetup();
        proceed();
    }

    @Override
    public void onNoteDialogCancel() {
        resetStateFromCurrentFragment();
    }

    /**
     * Finish the auto setup process, in some cases after showing a warning
     * dialog. Happens after onBasicsComplete
     *
     * @return true to proceed, false to remain on the current screen
     */
    private boolean finishAutoSetup() {
        final String email = mSetupData.getEmail();

        try {
            mProvider.expandTemplates(email);

            final String primaryProtocol = HostAuth.getProtocolFromString(mProvider.incomingUri);
            EmailServiceUtils.EmailServiceInfo info =
                    EmailServiceUtils.getServiceInfo(this, primaryProtocol);
            // If the protocol isn't one we can use, and we're not diverting to
            // gmail, try the alt
            if (!info.isGmailStub && !EmailServiceUtils.isServiceAvailable(this, info.protocol)) {
                LogUtils.d(LogUtils.TAG, "Protocol %s not available, using alternate",
                        info.protocol);
                mProvider.expandAlternateTemplates(email);
                final String alternateProtocol = HostAuth.getProtocolFromString(
                        mProvider.incomingUri);
                info = EmailServiceUtils.getServiceInfo(this, alternateProtocol);
            }
            final Account account = mSetupData.getAccount();
            final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
            recvAuth.setHostAuthFromString(mProvider.incomingUri);

            recvAuth.setUserName(mProvider.incomingUsername);
            recvAuth.mPort =
                    ((recvAuth.mFlags & HostAuth.FLAG_SSL) != 0) ? info.portSsl : info.port;

            if (info.usesSmtp) {
                final HostAuth sendAuth = account.getOrCreateHostAuthSend(this);
                sendAuth.setHostAuthFromString(mProvider.outgoingUri);
                sendAuth.setUserName(mProvider.outgoingUsername);
                //[BUGFIX]-Add-BEGIN by TCTNJ.(zhichuan.wei),03/12/2015 for PR936393
                //[FEATURE]-Add-BEGIN by TSNJ,wei.huang,10/22/2014
                if (mProvider.relogin != null && mProvider.relogin.equalsIgnoreCase("0")) {
                sendAuth.mFlags &= ~HostAuth.FLAG_AUTHENTICATE;
                } else {
                sendAuth.mFlags |= HostAuth.FLAG_AUTHENTICATE;
                }
                //[FEATURE]-Add-END by TSNJ,wei.huang
                //[BUGFIX]-Add-END by TCTNJ.(zhichuan.wei)
            }

            // Populate the setup data, assuming that the duplicate account
            // check will succeed
            populateSetupData(mOwnerName, email);

            //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_S
            final String duplicateAccountName =
                    mExistingAccountsMap != null ? mExistingAccountsMap.get(convertDomainToLowerCase(email)) : null;
            //TS: junwei-xu 2015-05-12 EMAIL BUGFIX-996057 MOD_E
            //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, MOD_S
            if (duplicateAccountName != null && !mIsDuplicateAccountDialogShown) {
                showDuplicateAccountDialog(duplicateAccountName);
                mIsDuplicateAccountDialogShown = true;
                //TS: zheng.zou 2015-03-24 EMAIL BUGFIX-951327, MOD_E
                return false;
            }
        } catch (URISyntaxException e) {
            mSkipAutoDiscover = false;
            mPreConfiguredFailed = true;
        }
        return true;
    }


    /**
     * Helper method to fill in some per-protocol defaults
     *
     * @param account Account object to fill in
     */
    public void setDefaultsForProtocol(Account account) {
        final EmailServiceUtils.EmailServiceInfo info = mSetupData.getIncomingServiceInfo(this);
        if (info == null)
            return;
        account.mSyncInterval = Integer.parseInt(info.defaultSyncInterval);
        account.mSyncLookback = info.defaultLookback;
        if (info.offerLocalDeletes) {
            //TS: zheng.zou 2015-4-30 EMAIL BUGFIX-985156, MOD_S
            String protocol = account.getProtocol(this);
            if (!TextUtils.isEmpty(protocol) && HostAuth.SCHEME_POP3.equalsIgnoreCase(protocol)) {
                int customPopDeletePolicy = PLFUtils.getCustomPopDeletePolicy(this, info.defaultLocalDeletes);
                account.setDeletePolicy(customPopDeletePolicy);
            } else {
                account.setDeletePolicy(info.defaultLocalDeletes);
            }
            //TS: zheng.zou 2015-4-30 EMAIL BUGFIX-985156, MOD_E
        }
    }

    /**
     * Populate SetupData's account with complete setup info, assumes that the
     * receive auth is created and its protocol is set
     */
    private void populateSetupData(String senderName, String senderEmail) {
        final Account account = mSetupData.getAccount();
        account.setSenderName(senderName);
        account.setEmailAddress(senderEmail);
        account.setDisplayName(senderEmail);
        setDefaultsForProtocol(account);
    }

    private void onIncomingComplete() {
        //TS: junwei-xu 2015-06-08 EMAIL BUGFIX-1016597 MOD_S
        if ((getContentFragment() != null) && (getContentFragment() instanceof AccountSetupIncomingFragment)) {
            AccountSetupIncomingFragment f = (AccountSetupIncomingFragment) getContentFragment();
            f.collectUserInput();
        }
        //TS: junwei-xu 2015-06-08 EMAIL BUGFIX-1016597 MOD_E
    }

    private void onOutgoingComplete() {
        //TS: junwei-xu 2015-06-08 EMAIL BUGFIX-1016597 MOD_S
        if ((getContentFragment() != null) && (getContentFragment() instanceof AccountSetupOutgoingFragment)) {
            AccountSetupOutgoingFragment f = (AccountSetupOutgoingFragment) getContentFragment();
            f.collectUserInput();
        }
        //TS: junwei-xu 2015-06-08 EMAIL BUGFIX-1016597 MOD_E
    }

    // This callback method is only applicable to using Incoming/Outgoing
    // fragments in settings mode
    @Override
    public void onAccountServerUIComplete(int checkMode) {
    }

    // This callback method is only applicable to using Incoming/Outgoing
    // fragments in settings mode
    @Override
    public void onAccountServerSaveComplete() {
    }

    private void populateHostAuthsFromSetupData() {
        final String email = mSetupData.getEmail();
        final String[] emailParts = email.split("@");
        final String domain = emailParts[1];

        final Account account = mSetupData.getAccount();

        final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
        recvAuth.setUserName(email);
        recvAuth.setConnection(mSetupData.getIncomingProtocol(), domain,
                HostAuth.PORT_UNKNOWN, HostAuth.FLAG_NONE);
        AccountSetupCredentialsFragment.populateHostAuthWithResults(this, recvAuth,
                mSetupData.getCredentialResults());
        mSetupData.setIncomingCredLoaded(true);

        final EmailServiceUtils.EmailServiceInfo info =
                mSetupData.getIncomingServiceInfo(this);
        if (info.usesSmtp) {
            final HostAuth sendAuth = account.getOrCreateHostAuthSend(this);
            sendAuth.setUserName(email);
            sendAuth.setConnection(HostAuth.LEGACY_SCHEME_SMTP, domain,
                    HostAuth.PORT_UNKNOWN, HostAuth.FLAG_NONE);
            AccountSetupCredentialsFragment.populateHostAuthWithResults(this, sendAuth,
                    mSetupData.getCredentialResults());
            mSetupData.setOutgoingCredLoaded(true);
        }
    }

    private void initiateAutoDiscover() {
        // Populate the setup data, assuming that the duplicate account check
        // will succeed
        initiateCheckSettingsFragment(SetupDataFragment.CHECK_AUTODISCOVER);
    }

    private void initiateCheckSettingsFragment(int checkMode) {
        final Fragment f = AccountCheckSettingsFragment.newInstance(checkMode);
        final Fragment d = CheckSettingsProgressDialogFragment.newInstance(checkMode);
        getFragmentManager().beginTransaction()
                .add(f, AccountCheckSettingsFragment.TAG)
                .add(d, CheckSettingsProgressDialogFragment.TAG)
                .commit();
    }

    @Override
    public void onCheckSettingsProgressDialogCancel() {
        dismissCheckSettingsFragment();
        resetStateFromCurrentFragment();
    }

    private void dismissCheckSettingsFragment() {
        final Fragment f = getFragmentManager().findFragmentByTag(AccountCheckSettingsFragment.TAG);
        final Fragment d =
                getFragmentManager().findFragmentByTag(CheckSettingsProgressDialogFragment.TAG);
        getFragmentManager().beginTransaction()
                .remove(f)
                .remove(d)
                .commit();
    }

    @Override
    public void onCheckSettingsError(int reason, String message) {
        if (reason == CheckSettingsErrorDialogFragment.REASON_AUTHENTICATION_FAILED ||
                reason == CheckSettingsErrorDialogFragment.REASON_CERTIFICATE_REQUIRED) {
            // TODO: possibly split password and cert error conditions
            mPasswordFailed = true;
        }
        dismissCheckSettingsFragment();
        final DialogFragment f =
                CheckSettingsErrorDialogFragment.newInstance(reason, message);
        f.show(getFragmentManager(), CheckSettingsErrorDialogFragment.TAG);
    }

    @Override
    public void onCheckSettingsErrorDialogEditCertificate() {
        if (mState == STATE_CHECKING_PRECONFIGURED) {
            mPreConfiguredFailed = true;
            proceed();
        } else {
            resetStateFromCurrentFragment();
        }
        final AccountSetupIncomingFragment f = (AccountSetupIncomingFragment) getContentFragment();
        f.onCertificateRequested();
    }

    @Override
    public void onCheckSettingsErrorDialogEditSettings() {
        // If we're checking pre-configured, set a flag that we failed and
        // navigate forwards to
        // incoming settings
        if (mState == STATE_CHECKING_PRECONFIGURED || mState == STATE_AUTO_DISCOVER) {
            mPreConfiguredFailed = true;
            proceed();
        } else {
            resetStateFromCurrentFragment();
        }
    }

    @Override
    public void onCheckSettingsComplete() {
        mPreConfiguredFailed = false;
        mPasswordFailed = false;
        //TS: Gantao 2015-12-15 EMAIL BUGFIX-1158892 ADD_S
        try {
            dismissCheckSettingsFragment();
            proceed();
            //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen,04/29/2016,2012077
            // [Email]Landing imap mailboxes, Server appears as 163.co
            AccountSetupIncomingFragment.saveSettingsAfterSetup(this, mSetupData);
            //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen
        } catch(IllegalStateException E) {
            LogUtils.e(LogUtils.TAG, "IllegalStateException while check setting complete");
        }
        //TS: Gantao 2015-12-15 EMAIL BUGFIX-1158892 ADD_E
    }

    @Override
    public void onCheckSettingsAutoDiscoverComplete(int result) {
        dismissCheckSettingsFragment();
        proceed();
        //[BUGFIX]-Mod-BEGIN by TSNJ Zhenhua.Fan,10/27/2014,PR 719562
        if(result ==AccountCheckSettingsFragment.AUTODISCOVER_OK)
        {
            proceed();
        }
        //[BUGFIX]-Mod-END by TSNJ Zhenhua.Fan
    }

    @Override
    public void onCheckSettingsSecurityRequired(String hostName) {
        dismissCheckSettingsFragment();
        final DialogFragment f = SecurityRequiredDialogFragment.newInstance(hostName);
        f.show(getFragmentManager(), SecurityRequiredDialogFragment.TAG);
    }

    @Override
    public void onSecurityRequiredDialogResult(boolean ok) {
        if (ok) {
            proceed();
        } else {
            resetStateFromCurrentFragment();
        }
    }

    @Override
    public void onChooseProtocol(String protocol) {
        mSetupData.setIncomingProtocol(this, protocol);
        //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 MOD_S
        //Whether load the ssv configureation or normal isp preset account
        try {
            final String[] emailParts = mSetupData.getEmail().split("@");
            final String domain = emailParts[1].trim();

         // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_S
            boolean ssvEnabled = Utilities.ssvEnabled();
            LogUtils.i(LogUtils.TAG,"ssvEnabled  : " + ssvEnabled);
            if(ssvEnabled) {
                mProvider = PLFUtils.findSSVProviderForDomain(this, domain, protocol);
            }else {
                mProvider = AccountSettingsUtils.findProviderForDomainAndProtocol(this, domain, protocol);
            }

            //No ssv account find,indicate that we should do normal isp account configuration,
            //current sim card is not required ssv sim card
            if(mProvider != null && PLFUtils.NO_SSV_ACOUNT_FIND.equals(mProvider.domain)) {
                mProvider = AccountSettingsUtils.findProviderForDomain(this,
                        domain, mLabel);
            }
         // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 MOD_E
        } catch (URISyntaxException e) {
            //TS: junwei-xu 2015-07-13 EMAIL BUGFIX-1042126 ADD_S
            //Note: reset mProvider if catch exception when read plf file.
            mProvider = null;
            //TS: junwei-xu 2015-07-13 EMAIL BUGFIX-1042126 ADD_E
            LogUtils.e(LogUtils.TAG, "findProviderForDomainAndProtocol couldn't find provider, throws URISyntaxException");
        } catch (NullPointerException e) {
            //TS: junwei-xu 2015-07-13 EMAIL BUGFIX-1042126 ADD_S
            //Note: reset mProvider if catch exception when read plf file.
            mProvider = null;
            //TS: junwei-xu 2015-07-13 EMAIL BUGFIX-1042126 ADD_E
            LogUtils.e(LogUtils.TAG, "get email is null from mSetupData, throws NullPointerException");
        } catch (IndexOutOfBoundsException e) {
            //TS: junwei-xu 2015-07-13 EMAIL BUGFIX-1042126 ADD_S
            //Note: reset mProvider if catch exception when read plf file.
            mProvider = null;
            //TS: junwei-xu 2015-07-13 EMAIL BUGFIX-1042126 ADD_E
            LogUtils.e(LogUtils.TAG, "can not get domain or get substring of mccmnc error, throws IndexOutOfBoundsException");
        }
        if (mProvider != null) {
            populateHostAuthsFromProvider();
        } else {
            populateHostAuthsFromSetupData();
        }
        //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 ADD_E
        final Account account = mSetupData.getAccount();
        setDefaultsForProtocol(account);
        proceed();
    }

    //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 ADD_S
    /**
     * Populate HostAuth(receive auth and send Auth) with the provider from plf file
     */
    private void populateHostAuthsFromProvider() {

        try {
            final String email = mSetupData.getEmail();
            mProvider.expandTemplates(email);

            final String primaryProtocol = HostAuth.getProtocolFromString(mProvider.incomingUri);
            EmailServiceUtils.EmailServiceInfo info =
                    EmailServiceUtils.getServiceInfo(this, primaryProtocol);
            // If the protocol isn't one we can use, and we're not diverting to
            // gmail, try the alt
            if (!info.isGmailStub && !EmailServiceUtils.isServiceAvailable(this, info.protocol)) {
                LogUtils.d(LogUtils.TAG, "Protocol %s not available, using alternate",
                        info.protocol);
                mProvider.expandAlternateTemplates(email);
                final String alternateProtocol = HostAuth.getProtocolFromString(
                        mProvider.incomingUri);
                info = EmailServiceUtils.getServiceInfo(this, alternateProtocol);
            }
            final Account account = mSetupData.getAccount();
            final HostAuth recvAuth = account.getOrCreateHostAuthRecv(this);
            recvAuth.setHostAuthFromString(mProvider.incomingUri);

            recvAuth.setUserName(mProvider.incomingUsername);
            recvAuth.mPort =
                    ((recvAuth.mFlags & HostAuth.FLAG_SSL) != 0) ? info.portSsl : info.port;

            if (info.usesSmtp) {
                final HostAuth sendAuth = account.getOrCreateHostAuthSend(this);
                sendAuth.setHostAuthFromString(mProvider.outgoingUri);
                sendAuth.setUserName(mProvider.outgoingUsername);
                //[BUGFIX]-Add-BEGIN by TCTNJ.(zhichuan.wei),03/12/2015 for PR936393
                //[FEATURE]-Add-BEGIN by TSNJ,wei.huang,10/22/2014
                if (mProvider.relogin != null && mProvider.relogin.equalsIgnoreCase("0")) {
                    sendAuth.mFlags &= ~HostAuth.FLAG_AUTHENTICATE;
                } else {
                    sendAuth.mFlags |= HostAuth.FLAG_AUTHENTICATE;
                }
                //[FEATURE]-Add-END by TSNJ,wei.huang
                //[BUGFIX]-Add-END by TCTNJ.(zhichuan.wei)
            }
        } catch (URISyntaxException e) {
            populateHostAuthsFromSetupData();
        } catch (NullPointerException e) {
            LogUtils.e(LogUtils.TAG, "get email is null from mSetupData, throws NullPointerException");
            populateHostAuthsFromSetupData();
        }
    }
    //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 ADD_E

    @Override
    public void onABProtocolDisambiguated(String chosenProtocol) {
        //TS: junwei-xu 2015-07-01 EMAIL BUGFIX-1026896 MOD_S
        // Note:when select account type, we should get account server info from perso
        if (!TextUtils.equals(mSetupData.getIncomingProtocol(this), chosenProtocol)) {
            mIsPreConfiguredProvider = false;
            onChooseProtocol(chosenProtocol);
            /*
            mSetupData.setIncomingProtocol(this, chosenProtocol);
            final Account account = mSetupData.getAccount();
            setDefaultsForProtocol(account);
            */
        } else {
            proceed();
        }
        //TS: junwei-xu 2015-07-01 EMAIL BUGFIX-1026896 MOD_E
    }

    /**
     * Ths is called when the user clicks the "done" button. It collects the
     * data from the UI, updates the setup account record, and launches a
     * fragment which handles creating the account in the system and database.
     */
    private void initiateAccountCreation() {
        mIsProcessing = true;
        getContentFragment().setNextButtonEnabled(false);

        final Account account = mSetupData.getAccount();
        if (account.mHostAuthRecv == null) {
            throw new IllegalStateException("in AccountSetupOptions with null mHostAuthRecv");
        }

        final AccountSetupOptionsFragment fragment = (AccountSetupOptionsFragment)
                getContentFragment();
        if (fragment == null) {
            throw new IllegalStateException("Fragment missing!");
        }
        //TS: jian.xu 2016-01-26 EMAIL BUGFIX-1496266, ADD_S
        //Note: check duplicate account before create it.
        final String duplicateAccountName = mExistingAccountsMap != null ?
                mExistingAccountsMap.get(convertDomainToLowerCase(account.getEmailAddress())) : null;
        if (duplicateAccountName != null && !mIsDuplicateAccountDialogShown) {
            showDuplicateAccountDialog(duplicateAccountName);
            mIsDuplicateAccountDialogShown = true;
            //TS: junwei-xu 2016-02-23 EMAIL BUGFIX-1652820, ADD_S
            mIsProcessing = false;
            //TS: junwei-xu 2016-02-23 EMAIL BUGFIX-1652820, ADD_E
            return;
        }
        //TS: jian.xu 2016-01-26 EMAIL BUGFIX-1496266, ADD_E
        //TS: gangjin.weng 2015-04-17 EMAIL BUGFIX-963376, MOD_S
        /*
        //[FEATURE]-Add-BEGIN by by TSNJ.(li.yu),18/11/2014 for FR836645
        // TS: xiaolin.li 2014-11-26 EMAIL READ_PLF MOD_S
        //if(getApplicationContext().getResources().getBoolean(R.bool.feature_email_accountGermanSignature_on)&&Locale.getDefault().getLanguage().equals("de"))
        if(PLFUtils.getBoolean(getApplicationContext(), "feature_email_accountGermanSignature_on")&&Locale.getDefault().getLanguage().equals("de"))
        // TS: xiaolin.li 2014-11-26 EMAIL READ_PLF MOD_E
        {
        	// TS: xiaolin.li 2014-11-26 EMAIL READ_PLF MOD_S
        	//account.setSignature(getString(R.string.def_email_accountGermanSignature));
        	String germanSign = PLFUtils.getString(getApplicationContext(), "def_email_accountGermanSignature");
        	if(null != germanSign){
        		account.setSignature(germanSign);
        	}
        	// TS: xiaolin.li 2014-11-26 EMAIL READ_PLF MOD_E
        }else{
            //[FEATURE]-Add-BEGIN by by TSNJ.(li.yu),24/10/2014 for FR622680
        	// TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //account.setSignature(getString(R.string.def_email_accountSignature));
        	String accountSign = PLFUtils.getString(getApplicationContext(), "def_email_accountSignature");
        	if(null != accountSign){
        		account.setSignature(accountSign);
        	}
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
            //[FEATURE]-Add-END by TSNJ.(li.yu)
        }
        //[FEATURE]-Add-END by TSNJ.(li.yu)
        */
        //TS: jian.xu 2015-08-06 EMAIL BUGFIX-1046840, MOD_S
        //Note: For Claro/Telcel sim card, CTS require specific signature
        String accountSign = Utilities.getSingnatureForAccount(this);
        account.setSignature(accountSign);
        //TS: jian.xu 2015-08-06 EMAIL BUGFIX-1046840, MOD_E
        //TS: gangjin.weng 2015-04-17 EMAIL BUGFIX-963376, MOD_E
        account.setDisplayName(account.getEmailAddress());
        int newFlags = account.getFlags() & ~(Account.FLAGS_BACKGROUND_ATTACHMENTS);
        final EmailServiceUtils.EmailServiceInfo serviceInfo =
                mSetupData.getIncomingServiceInfo(this);
        if (serviceInfo.offerAttachmentPreload && fragment.getBackgroundAttachmentsValue()) {
            newFlags |= Account.FLAGS_BACKGROUND_ATTACHMENTS;
        }
        final HostAuth hostAuth = account.getOrCreateHostAuthRecv(this);
        if (hostAuth.mProtocol.equals(getString(R.string.protocol_eas))) {
            try {
                final double protocolVersionDouble = Double.parseDouble(account.mProtocolVersion);
                if (protocolVersionDouble >= 12.0) {
                    // If the the account is EAS and the protocol version is
                    // above 12.0,
                    // we know that SmartForward is enabled and the various
                    // search flags
                    // should be enabled first.
                    // TODO: Move this into protocol specific code in the
                    // future.
                    newFlags |= Account.FLAGS_SUPPORTS_SMART_FORWARD |
                            Account.FLAGS_SUPPORTS_GLOBAL_SEARCH | Account.FLAGS_SUPPORTS_SEARCH;
                }
            } catch (NumberFormatException e) {
                LogUtils.wtf(LogUtils.TAG, e, "Exception thrown parsing the protocol version.");
            //TS: junwei-xu 2015-06-08 EMAIL BUGFIX-1016597 ADD_S
            } catch (NullPointerException e) {
                LogUtils.e(LogUtils.TAG, e, "Exception thrown parsing the protocol version.");
            }
            //TS: junwei-xu 2015-06-08 EMAIL BUGFIX-1016597 ADD_E
        }
        account.setFlags(newFlags);
        account.setSyncInterval(fragment.getCheckFrequencyValue());
        final Integer syncWindowValue = fragment.getAccountSyncWindowValue();
        if (syncWindowValue != null) {
            account.setSyncLookback(syncWindowValue);
        }
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
        final Integer syncCalendarWindowValue = fragment.getAccountSyncCalendarWindowValue();
        if (syncCalendarWindowValue != null) {
            account.setSyncCalendarLookback(syncCalendarWindowValue);
        }
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
        //[FEATURE]-Add-BEGIN by TSNJ.Zhenhua.Fan,10/17/2014,for DownloadOptions
        final Integer downloadOptionsValue = fragment.getDownloadOptionsValue();
        if(downloadOptionsValue!=null){
            account.setDownloadOptions(downloadOptionsValue);
        }
        //[FEATURE]-Add-END by TSNJ.Zhenhua.Fan,10/17/2014
        // Finish setting up the account, and commit it to the database
        if (mSetupData.getPolicy() != null) {
            account.mFlags |= Account.FLAGS_SECURITY_HOLD;
            account.mPolicy = mSetupData.getPolicy();
        }

        // Finally, write the completed account (for the first time) and then
        // install it into the Account manager as well. These are done
        // off-thread.
        // The account manager will report back via the callback, which will
        // take us to
        // the next operations.
        final boolean syncEmail = fragment.getSyncEmailValue();
        final boolean syncCalendar = serviceInfo.syncCalendar && fragment.getSyncCalendarValue();
        final boolean syncContacts = serviceInfo.syncContacts && fragment.getSyncContactsValue();
        final boolean enableNotifications = fragment.getNotifyValue();

        final Fragment f = AccountCreationFragment.newInstance(account, syncEmail, syncCalendar,
                syncContacts, enableNotifications);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(f, AccountCreationFragment.TAG);
        ft.commit();

        showCreateAccountDialog();
    }

    /**
     * Called by the account creation fragment after it has completed. We do a
     * small amount of work here before moving on to the next state.
     */
    @Override
    public void onAccountCreationFragmentComplete() {
        destroyAccountCreationFragment();
        // If the account manager initiated the creation, and success was not
        // reported,
        // then we assume that we're giving up (for any reason) - report
        // failure.
        if (mAccountAuthenticatorResponse != null) {
            final EmailServiceUtils.EmailServiceInfo info = mSetupData.getIncomingServiceInfo(this);
            final Bundle b = new Bundle(2);
            b.putString(AccountManager.KEY_ACCOUNT_NAME, mSetupData.getEmail());
            b.putString(AccountManager.KEY_ACCOUNT_TYPE, info.accountType);
            mAccountAuthenticatorResponse.onResult(b);
            mAccountAuthenticatorResponse = null;
            mReportAccountAuthenticatorError = false;
        }
        //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_S
        mIsAccountCreateComplete = true;
        //TS: zheng.zou 2015-03-05 EMAIL BUGFIX-921199,for 902870 MOD_E
        //TS: zheng.zou 2016-02-04 EMAIL BUGFIX-1456516 MOD_S
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mSetupData.getEmail());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mSetupData.getIncomingServiceInfo(this).accountType);
        setResult(RESULT_OK,intent);
//        setResult(RESULT_OK);
        //TS: zheng.zou 2016-02-04 EMAIL BUGFIX-1456516 MOD_E
        proceed();
    }

    @Override
    public void destroyAccountCreationFragment() {
        dismissCreateAccountDialog();

        final Fragment f = getFragmentManager().findFragmentByTag(AccountCreationFragment.TAG);
        if (f == null) {
            LogUtils.wtf(LogUtils.TAG, "Couldn't find AccountCreationFragment to destroy");
        }
        getFragmentManager().beginTransaction()
                .remove(f)
                .commit();
    }

    @Override
    public void proceedStateCredentials() {
          collectCredentials();
         if (mIsPreConfiguredProvider) {
             mState = STATE_CHECKING_PRECONFIGURED;
             initiateCheckSettingsFragment(SetupDataFragment.CHECK_INCOMING
                     | SetupDataFragment.CHECK_OUTGOING);
         } else {
             //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 MOD_S
             //populateHostAuthsFromSetupData();
             //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 MoD_E
             if (mSkipAutoDiscover) {
                 mState = STATE_MANUAL_INCOMING;
                 updateContentFragment(true /* addToBackstack */);
             } else {
                 mState = STATE_AUTO_DISCOVER;
                 initiateAutoDiscover();
             }
         }
    }


    public static class CreateAccountDialogFragment extends DialogFragment {
        public static final String TAG = "CreateAccountDialogFragment";

        public CreateAccountDialogFragment() {
        }

        public static CreateAccountDialogFragment newInstance() {
            return new CreateAccountDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // / Show "Creating account..." dialog
            setCancelable(false);
            final ProgressDialog d = new ProgressDialog(getActivity());
            d.setIndeterminate(true);
            d.setMessage(getString(R.string.account_setup_creating_account_msg));
            return d;
        }
    }

    protected void showCreateAccountDialog() {
        CreateAccountDialogFragment.newInstance()
                .show(getFragmentManager(), CreateAccountDialogFragment.TAG);
    }

    protected void dismissCreateAccountDialog() {
        final DialogFragment f = (DialogFragment)
                getFragmentManager().findFragmentByTag(CreateAccountDialogFragment.TAG);
        if (f != null) {
            f.dismiss();
        }
    }

    public static class CreateAccountErrorDialogFragment extends DialogFragment
            implements DialogInterface.OnClickListener {
        public CreateAccountErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String message = getString(R.string.account_setup_failed_dlg_auth_message,
                    R.string.system_account_create_failed);

            setCancelable(false);
            return new AlertDialog.Builder(getActivity())
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.account_setup_failed_dlg_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            getActivity().finish();
        }
    }

    /**
     * This is called if MailService.setupAccountManagerAccount() fails for some
     * reason
     */
    @Override
    public void showCreateAccountErrorDialog() {
        new CreateAccountErrorDialogFragment().show(getFragmentManager(), null);
    }

    /**
     * Collect the data from AccountSetupNames and finish up account creation
     */
    private void initiateAccountFinalize() {
        mIsProcessing = true;
        getContentFragment().setNextButtonEnabled(false);

        AccountSetupNamesFragment fragment = (AccountSetupNamesFragment) getContentFragment();
        // Update account object from UI
        final Account account = mSetupData.getAccount();
        final String description = fragment.getDescription();
        if (!TextUtils.isEmpty(description)) {
            account.setDisplayName(description);
        }
        account.setSenderName(fragment.getSenderName());

        final Fragment f = AccountFinalizeFragment.newInstance(account);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(f, AccountFinalizeFragment.TAG);
        ft.commit();
    }

    /**
     * Called when the AccountFinalizeFragment has finished its tasks
     */
    @Override
    public void onAccountFinalizeFragmentComplete() {
        finish();
    }
    //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
    //[Email][Perso]Cann't customized email account
    @Override
    public void setPreValues(String domain, String label) {
        mDomain = domain;
        mLabel = label;
    }
    //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.co

}
