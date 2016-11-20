/*
 * Copyright (C) 2010 The Android Open Source Project
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
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-906634  2015/1/27    peng-zhang      [Email]Can't send email when input wrong address in the first setup UI.
 *BUGFIX-926293  2015/2/09    peng-zhang      [FC][Monitor][Email]Email FC when login account.
 *BUGFIX-926863  2015/2/13    peng-zhang      [REG][Email]The Bcc adress is wrong when send email from TCL account
 *CR-932701      2015/02/13   ke.ma           [Android5.0][Email][GD] Achieve 'Account setup' according to GD_v5.1.5.4
 *BUGFIX_910257  2015/02/28   gengkexue       [Android5.0][Email]The value of port will change to the original value after rotate screen
 *BUGFIX_955250  2015/03/21   qing.liang      [SMC][crashed]com.tct.email crashed
 *BUGFIX-958218  2015/3/25    junwei-xu       [Android5.0][Exchange]"Add domain" is no use when we create TCL account
 *BUGFIX-966647  2015/4/03    zheng.zou       [REG][Email]Add certificate menu disappear when rotate device
 *BUGFIX-982087  2015/4/21    peng-zhang      [HOMO][ALWE] Send e-mail from a POP3 account with different username/email address.
 *BUGFIX-986357  2015/4/23    zheng.zou       [REG][Email]Add certificate menu disappear when rotate device
 *BUGFIX-986357  2015/5/04    zheng.zou       [REG][Email]Add certificate menu disappear when rotate device
 *BUGFIX-1003323 2015/5/28   yanhua.chen      [5.0][Email] account setup: input area should get focus when user tap "add domain"
 *BUGFIX-1046659 2015/07/20   junwei-xu       [GAPP][Email]An account with the same type can be created twice
 *BUGFIX-489428  2015/8/21    lin-zhou        [Android L][Email]Value of domain will jump to username location Edit Notification
 *BUGFIX-1082125 2015/9/14   tao.gan          [HOMO][HOMO](SSV) The operator request the email exchange security type, SSL/TLS (Accept all certificates) as default
 *BUGFIX_710083  2015/10/14   lin-zhou        [Android L][Email][Monitor]Domain disappeared after pop up can not connect server
 *FR_981064      2015/11/25   lin-zhou        5.2.8ergo update
 *BUGFIX-956919  2015-12-03  yanhua.chen     [Android L][Email][UE]The font of "Email account" string change to black after rorating screen and the check box style is not consistent after rotating screen..
 *BUGFIX-1117195 2015/12/17    zheng.zou      [Android 6.0][Email][Monkey][ANR]ANR happened during monkey test
 *BUGFIX_1545643 2015/2/2     yanhua.chen     [Email]Some status bar icons are displayed in the Incoming settings and Outgoing settings of Email
 ===========================================================================
 */

package com.tct.email.activity.setup;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.Device;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.VendorPolicyLoader;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.Credential;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.utility.CertificateRequestor;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.tct.emailcommon.provider.EmailContent;
//[FEATURE]-Add-END by TSNJ.qinglian.zhang
import com.tct.email.activity.UiUtilities;
import com.tct.email.activity.setup.AuthenticationView.AuthenticationCallback;
import com.tct.email.provider.AccountBackupRestore;
import com.tct.email.provider.Utilities;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.email.view.CertificateSelector;
import com.tct.email.view.UseProxySelector;
import com.tct.email.view.CertificateSelector.HostCallback;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.ui.MailAsyncTaskLoader;

/**
 * Provides UI for IMAP/POP account settings.
 *
 * This fragment is used by AccountSetupIncoming (for creating accounts) and by AccountSettingsXL
 * (for editing existing accounts).
 */
public class AccountSetupIncomingFragment extends AccountServerBaseFragment
        implements HostCallback, AuthenticationCallback, OnCheckedChangeListener{

    private static final int CERTIFICATE_REQUEST = 0;
    private static final int SIGN_IN_REQUEST = 1;

    private final static String STATE_KEY_CREDENTIAL = "AccountSetupIncomingFragment.credential";
    private final static String STATE_KEY_LOADED = "AccountSetupIncomingFragment.loaded";
    private final static String STATE_KEY_PORT = "AccountSetupIncomingFragment.port";    //TS: zheng.zou 2015-04-03 EMAIL PR966647 ADD
    private final static String STATE_KEY_CERTALIAS = "AccountSetupIncomingFragment.certAlias";    //TS: zheng.zou 2015-04-27 EMAIL PR986357 ADD
    //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 ADD_S
    private final static String STATE_KEY_HASDOMAIN = "AccountSetupIncomingFragment.hasDomain";
    //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 ADD_E

    private EditText mUsernameView;
    private AuthenticationView mAuthenticationView;
    private TextView mAuthenticationLabel;
    private TextView mServerLabelView;
    private EditText mServerView;
    private EditText mPortView;
    private Spinner mSecurityTypeView;
    private TextView mDeletePolicyLabelView;
    private Spinner mDeletePolicyView;
    private CertificateSelector mClientCertificateSelector;
    private View mDeviceIdSection;
    private View mImapPathPrefixSectionView;
    private EditText mImapPathPrefixView;
    //TS: ke.ma 2015-02-13 CR932701 EMAIL ADD_S
    private View mDomainWrapper;
    private TextView mAddDomain;
    private EditText mDomainEdit;
    //TS: ke.ma 2015-02-13 CR932701 EMAIL ADD_E
    private TextView mClientView;
    private EditText mPasswordText;
    private boolean mOAuthProviderPresent;
    // Delete policy as loaded from the device
    private int mLoadedDeletePolicy;

    //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
    private CheckBox mUseProxyView;
    private UseProxySelector mUseProxyViewSelector;
    private boolean isProxyOn;
    private static boolean isExchange;
    private Context mContext; //[BUGFIX]-Mod by TCTNB.caixia.chen,01/07/2015,PR 893304
    //[FEATURE]-Add-END by TSNJ.qinglian.zhang

    private TextWatcher mValidationTextWatcher;

    // Support for lifecycle
    private boolean mStarted;
    private boolean mLoaded;
    private String mCacheLoginCredential;
    private EmailServiceInfo mServiceInfo;
    private int mSecuritySelectIndex = -1; // AM: Kexue.Geng 2015-02-28 EMAIL BUGFIX_910257 MOD
    private String mPort;     //TS: zheng.zou 2015-04-03 EMAIL PR966647 ADD
    private String mCertAlias;  //TS: zheng.zou 2015-04-23 EMAIL PR986357 ADD
    //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_S
    private boolean mHasDomain = false;
    //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_E

    public static AccountSetupIncomingFragment newInstance(boolean settingsMode) {
        final AccountSetupIncomingFragment f = new AccountSetupIncomingFragment();
        f.setArguments(getArgs(settingsMode));
        return f;
    }

    // Public no-args constructor needed for fragment re-instantiation
    public AccountSetupIncomingFragment() {}

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before {@link #onActivityCreated(Bundle)}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCacheLoginCredential = savedInstanceState.getString(STATE_KEY_CREDENTIAL);
            mLoaded = savedInstanceState.getBoolean(STATE_KEY_LOADED, false);
            mPort = savedInstanceState.getString(STATE_KEY_PORT);   //TS: zheng.zou 2015-04-03 EMAIL PR966647 ADD
            mCertAlias = savedInstanceState.getString(STATE_KEY_CERTALIAS);    //TS: zheng.zou 2015-04-23 EMAIL PR986357 ADD
            //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_S
            mHasDomain = savedInstanceState.getBoolean(STATE_KEY_HASDOMAIN);
            //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_E
        }

        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //isProxyOn = getResources().getBoolean(R.bool.feature_email_proxy_on);
        mContext = getActivity() ;
        isProxyOn = PLFUtils.getBoolean(mContext, "feature_email_proxy_on");
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view;
        if (mSettingsMode) {
            view = inflater.inflate(R.layout.account_settings_incoming_fragment, container, false);
            //TS: yanhua.chen 2015-2-2 EMAIL BUGFIX_1545643 ADD_S
            Activity activity = getActivity();
            if(activity!=null){
                Window window = activity.getWindow();
                if(window!=null && window.getAttributes() != null){
                    window.getAttributes().gravity = -1;
                }
            }
            //TS: yanhua.chen 2015-2-2 EMAIL BUGFIX_1545643 ADD_E
        } else {
            view = inflateTemplatedView(inflater, container,
                    R.layout.account_setup_incoming_fragment,
                    R.string.account_setup_incoming_headline);
        }

        mUsernameView = UiUtilities.getView(view, R.id.account_username);
        mServerLabelView = UiUtilities.getView(view, R.id.account_server_label);
        mServerView = UiUtilities.getView(view, R.id.account_server);
        mPortView = UiUtilities.getView(view, R.id.account_port);
        mSecurityTypeView = UiUtilities.getView(view, R.id.account_security_type);
        mDeletePolicyLabelView = UiUtilities.getView(view, R.id.account_delete_policy_label);
        mDeletePolicyView = UiUtilities.getView(view, R.id.account_delete_policy);
        mImapPathPrefixSectionView = UiUtilities.getView(view, R.id.imap_path_prefix_section);
        mImapPathPrefixView = UiUtilities.getView(view, R.id.imap_path_prefix);
        mAuthenticationView = UiUtilities.getView(view, R.id.authentication_view);
        mClientCertificateSelector = UiUtilities.getView(view, R.id.client_certificate_selector);
        mDeviceIdSection = UiUtilities.getView(view, R.id.device_id_section);
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
        mClientView=UiUtilities.getView(view,R.id.title);
        mPasswordText=UiUtilities.getView(view,R.id.password_edit);
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
        // Don't use UiUtilities here. In some configurations this view does not exist, and
        // UiUtilities throws an exception in this case.
        mAuthenticationLabel = (TextView)view.findViewById(R.id.authentication_label);
        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        mUseProxyView = UiUtilities.getView(view, R.id.account_use_proxy);
        mUseProxyView.setOnCheckedChangeListener(this);
        mUseProxyViewSelector = UiUtilities.getView(view, R.id.use_proxy_selector);
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang

        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
        mAddDomain=UiUtilities.getView(view, R.id.add_domain_label);
        mAddDomain.setText(getResources().getText(
                R.string.account_setup_incoming_add_domain_label).toString().toUpperCase());
        mDomainWrapper=UiUtilities.getView(view, R.id.domain_wrapper);
        mDomainEdit=UiUtilities.getView(view, R.id.domain_edit);

        mAddDomain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mAddDomain.setVisibility(View.GONE);
                mDomainWrapper.setVisibility(View.VISIBLE);
                //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_S
                mHasDomain = true;
                //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_E
                //TS: yanhua.chen 2015-5-28 EXCHANGE BUGFIX_1003323 ADD_S
                mDomainEdit.setFocusable(true);
                mDomainEdit.requestFocus();
                final Context context = getActivity().getApplicationContext();
                if(context != null){
                    final InputMethodManager imm =
                            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null){
                        imm.showSoftInput(mDomainEdit, InputMethodManager.SHOW_FORCED);
                    }
                }
               //TS: yanhua.chen 2015-5-28 EXCHANGE BUGFIX_1003323 ADD_E
            }
        });
        //TS: ke.ma 2015-02-11 EMAIL CR932701 ADD_E
        // Updates the port when the user changes the security type. This allows
        // us to show a reasonable default which the user can change.
        mSecurityTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // AM: Kexue.Geng 2015-02-28 EMAIL BUGFIX_910257 MOD_S
                // updatePortFromSecurityType();
                //TS: zheng.zou 2015-04-03 EMAIL PR966647 MOD_S
//                if (mSecuritySelectIndex != arg2 && mSecuritySelectIndex != -1) {
//                    updatePortFromSecurityType();
//                }
//                mSecuritySelectIndex = arg2;
                // AM: Kexue.Geng 2015-02-28 EMAIL BUGFIX_910257 MOD_E
                if (mSecuritySelectIndex != arg2) {
                    updatePortFromSecurityType();
                    if (mPort != null) {
                        mPortView.setText(mPort);
                        mPort = null;
                    }
                    mSecuritySelectIndex = arg2;
                }
                //TS: zheng.zou 2015-04-03 EMAIL PR966647 MOD_E
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

        // After any text edits, call validateFields() which enables or disables the Next button
        mValidationTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };

        mUsernameView.addTextChangedListener(mValidationTextWatcher);
        mServerView.addTextChangedListener(mValidationTextWatcher);
        mPortView.addTextChangedListener(mValidationTextWatcher);
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
        mPasswordText.addTextChangedListener(mValidationTextWatcher);
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E

        // Only allow digits in the port field.
        mPortView.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        // Additional setup only used while in "settings" mode
        onCreateViewSettingsMode(view);

        mAuthenticationView.setAuthenticationCallback(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mClientCertificateSelector.setHostCallback(this);

        final Context context = getActivity();
        final SetupDataFragment.SetupDataContainer container =
                (SetupDataFragment.SetupDataContainer) context;
        mSetupData = container.getSetupData();
        final Account account = mSetupData.getAccount();
        final HostAuth recvAuth = account.getOrCreateHostAuthRecv(mAppContext);

        // Pre-fill info as appropriate
        if (!mSetupData.isIncomingCredLoaded()) {
            recvAuth.mLogin = mSetupData.getEmail();
            AccountSetupCredentialsFragment.populateHostAuthWithResults(context, recvAuth,
                    mSetupData.getCredentialResults());
            final String[] emailParts = mSetupData.getEmail().split("@");
            //TS: qing.liang 2015-02-13 EMAIL PR955250 MOD_S
            //it may rarely lose symbol '@' after data transfer
//            final String domain = emailParts[1];
            final String domain = emailParts.length == 2 ? emailParts[1] : "";
            //TS: qing.liang 2015-02-13 EMAIL PR955250 MOD_E
            recvAuth.setConnection(recvAuth.mProtocol, domain, HostAuth.PORT_UNKNOWN,
                    HostAuth.FLAG_NONE);
            mSetupData.setIncomingCredLoaded(true);
        }

        mServiceInfo = mSetupData.getIncomingServiceInfo(context);

        if (mServiceInfo.offerLocalDeletes) {
            SpinnerOption deletePolicies[] = {
                    new SpinnerOption(Account.DELETE_POLICY_NEVER,
                            context.getString(
                                    R.string.account_setup_incoming_delete_policy_never_label)),
                    new SpinnerOption(Account.DELETE_POLICY_ON_DELETE,
                            context.getString(
                                    R.string.account_setup_incoming_delete_policy_delete_label)),
            };
            ArrayAdapter<SpinnerOption> deletePoliciesAdapter =
                    new ArrayAdapter<SpinnerOption>(context,
                            android.R.layout.simple_spinner_item, deletePolicies);
            deletePoliciesAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            mDeletePolicyView.setAdapter(deletePoliciesAdapter);
        }

        // Set up security type spinner
        ArrayList<SpinnerOption> securityTypes = new ArrayList<SpinnerOption>();
        securityTypes.add(
                new SpinnerOption(HostAuth.FLAG_NONE, context.getString(
                        R.string.account_setup_incoming_security_none_label)));
        securityTypes.add(
                new SpinnerOption(HostAuth.FLAG_SSL, context.getString(
                        R.string.account_setup_incoming_security_ssl_label)));
        securityTypes.add(
                new SpinnerOption(HostAuth.FLAG_SSL | HostAuth.FLAG_TRUST_ALL, context.getString(
                        R.string.account_setup_incoming_security_ssl_trust_certificates_label)));
        if (mServiceInfo.offerTls) {
            securityTypes.add(
                    new SpinnerOption(HostAuth.FLAG_TLS, context.getString(
                            R.string.account_setup_incoming_security_tls_label)));
            securityTypes.add(new SpinnerOption(HostAuth.FLAG_TLS | HostAuth.FLAG_TRUST_ALL,
                    context.getString(R.string
                            .account_setup_incoming_security_tls_trust_certificates_label)));
        }
        ArrayAdapter<SpinnerOption> securityTypesAdapter = new ArrayAdapter<SpinnerOption>(
                context, android.R.layout.simple_spinner_item, securityTypes);
        securityTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSecurityTypeView.setAdapter(securityTypesAdapter);
    }
    /**
     * Called when the Fragment is visible to the user.
     */
    @Override
    public void onStart() {
        if (Logging.DEBUG_LIFECYCLE && MailActivityEmail.DEBUG) {
            LogUtils.d(Logging.LOG_TAG, "AccountSetupIncomingFragment onStart");
        }
        super.onStart();
        mStarted = true;
        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        if ("eas".equals(mServiceInfo.protocol) || (mServiceInfo.accountType).equals(
            getResources().getString(R.string.account_manager_type_exchange))) {
            isExchange = true ;
        } else {
            isExchange = false ;
        }
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang

        //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_S
        if(!mAuthenticationView.getIsDisplayPassword()){
            //Hide password
            mAuthenticationView.setHiddenPassword(mSettingsMode);
        }else {
            //Show password
            mAuthenticationView.setDisplayPassword(mSettingsMode);
        }
        //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_E

        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
        loadDomainView();
        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_E
        configureEditor();
        loadSettings();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        validateFields();
    }

    @Override
    public void onDestroyView() {
        // Make sure we don't get callbacks after the views are supposed to be destroyed
        // and also don't hold onto them longer than we need
        if (mUsernameView != null) {
            mUsernameView.removeTextChangedListener(mValidationTextWatcher);
        }
        mUsernameView = null;
        mServerLabelView = null;
        if (mServerView != null) {
            mServerView.removeTextChangedListener(mValidationTextWatcher);
        }
        mServerView = null;
        if (mPortView != null) {
            mPortView.removeTextChangedListener(mValidationTextWatcher);
        }
        mPortView = null;
        if (mSecurityTypeView != null) {
            mSecurityTypeView.setOnItemSelectedListener(null);
        }
        mSecuritySelectIndex = -1;   //TS: zheng.zou 2015-05-04 EMAIL PR986357 ADD
        //TS: zheng.zou 2015-12-17 EMAIL BUGFIX_1117195 ADD_S
        //NOTE: when run monkey, the dropdown is force remove cause input timeout.
        //we hide normally to avoid this
        if (mSecurityTypeView!=null){
            mSecurityTypeView.clearFocus();
            hideSpinnerDropDown(mSecurityTypeView);
        }
        if (mDeletePolicyView!=null){
            mDeletePolicyView.clearFocus();
            hideSpinnerDropDown(mDeletePolicyView);
        }
        //TS: zheng.zou 2015-12-17 EMAIL BUGFIX_1117195 ADD_E
        mSecurityTypeView = null;
        mDeletePolicyLabelView = null;
        mDeletePolicyView = null;
        mImapPathPrefixSectionView = null;
        mImapPathPrefixView = null;
        mDeviceIdSection = null;
        mClientCertificateSelector = null;

        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_KEY_CREDENTIAL, mCacheLoginCredential);
        outState.putBoolean(STATE_KEY_LOADED, mLoaded);
        //TS: zheng.zou 2015-04-03 EMAIL PR966647 ADD_S
        if (mPortView != null && mPortView.getText() != null) {
            outState.putString(STATE_KEY_PORT, mPortView.getText().toString());
        }
        //TS: zheng.zou 2015-04-03 EMAIL PR966647 ADD_E
        //TS: zheng.zou 2015-04-23 EMAIL PR986357 ADD_S
        String certAlias = mClientCertificateSelector == null ? "" : mClientCertificateSelector.getCertificate();
        if (!TextUtils.isEmpty(certAlias)) {
            outState.putString(STATE_KEY_CERTALIAS, certAlias);
        }
        //TS: zheng.zou 2015-04-23 EMAIL PR986357 ADD_E
        //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 ADD_S
        outState.putBoolean(STATE_KEY_HASDOMAIN, mHasDomain);
        //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 ADD_E
    }

    //TS: zheng.zou 2015-05-04 EMAIL PR986357 ADD_S
    //note: sometimes onSaveInstanceState() is not called, save value here
    @Override
    public void onPause() {
        super.onPause();
        mCertAlias = mClientCertificateSelector == null ? "" : mClientCertificateSelector.getCertificate();
    }
    //TS: zheng.zou 2015-05-04 EMAIL PR986357 ADD_E

    //TS: zheng.zou 2015-04-23 EMAIL PR986357 ADD_S
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (mClientCertificateSelector != null && !TextUtils.isEmpty(mCertAlias)) {
            mClientCertificateSelector.setCertificate(mCertAlias);
        }
    }
    //TS: zheng.zou 2015-04-23 EMAIL PR986357 ADD_E

    /**
     * Configure the editor for the account type
     */
    private void configureEditor() {
        final Account account = mSetupData.getAccount();
        if (account == null || account.mHostAuthRecv == null) {
            LogUtils.e(LogUtils.TAG,
                    "null account or host auth. account null: %b host auth null: %b",
                    account == null, account == null || account.mHostAuthRecv == null);
            return;
        }

        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        if (isExchange && isProxyOn) {
            mUseProxyView.setVisibility(View.VISIBLE);
        }
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang

        TextView lastView = mImapPathPrefixView;
        mBaseScheme = account.mHostAuthRecv.mProtocol;
        mServerLabelView.setText(R.string.account_setup_incoming_server_label);
        mServerView.setContentDescription(getResources().getText(
                R.string.account_setup_incoming_server_label));
        if (!mServiceInfo.offerPrefix) {
            mImapPathPrefixSectionView.setVisibility(View.GONE);
        }
        if (!mServiceInfo.offerLocalDeletes) {
            mDeletePolicyLabelView.setVisibility(View.GONE);
            mDeletePolicyView.setVisibility(View.GONE);
            mPortView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
    }

    /**
     * Load the current settings into the UI
     */
    private void loadSettings() {
        if (mLoaded) return;

        final Account account = mSetupData.getAccount();
        final HostAuth recvAuth = account.getOrCreateHostAuthRecv(mAppContext);
        mServiceInfo = mSetupData.getIncomingServiceInfo(mContext);
        final List<VendorPolicyLoader.OAuthProvider> oauthProviders =
                AccountSettingsUtils.getAllOAuthProviders(mContext);
        final boolean offerOAuth = (mServiceInfo.offerOAuth && oauthProviders.size() > 0);

        mAuthenticationView.setAuthInfo(offerOAuth, recvAuth);
        if (mAuthenticationLabel != null) {
            if (offerOAuth) {
				//authentication_label);//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/05/2016,2013280
                mAuthenticationLabel.setText(R.string.account_setup_basics_password_label);
            } else {
                mAuthenticationLabel.setText(R.string.account_setup_basics_password_label);
            }
        }

        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        boolean useproxy = false;
        if (isExchange) {
            if (isProxyOn) {
                if (account != null) {
                    if (account.isSaved()){
                        EmailContent.AccountInfo.querryProxyInfo(mContext, account.mId,
                                account);
                    } else {
                        account.mIsUseproxy = false;
                        account.mProxyAddr = "";
                        account.mProxyPort = 0;
                        account.mProxyUsername = "";
                        account.mProxyUserpass = "";
                    }
                    useproxy = account.mIsUseproxy;
                    mUseProxyView.setChecked(account.mIsUseproxy);
                    mUseProxyViewSelector.setProxyInformation(account.mProxyAddr, String.valueOf(account.mProxyPort),
                            account.mProxyUsername, account.mProxyUserpass);
                }
            } else {
                if (account != null) {
                    useproxy = account.mIsUseproxy = false;
                    account.mProxyAddr = "";
                    account.mProxyPort = 0;
                    account.mProxyUsername = "";
                    account.mProxyUserpass = "";
                }
            }
        }
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang

        //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_982087 MOD_S
        //AM: peng-zhang 2015-02-09 EMAIL BUGFIX_926293 MOD_S
        final String username = recvAuth.mLogin;
        //AM: peng-zhang 2015-02-09 EMAIL BUGFIX_926293 MOD_E
        //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_982087 MOD_E
        //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_S
        /*if (username != null) {
            //*** For eas?
            // Add a backslash to the start of the username, but only if the username has no
            // backslash in it.
            //if (userName.indexOf('\\') < 0) {
            //    userName = "\\" + userName;
            //}
            mUsernameView.setText(username);
        }*/
        if (!TextUtils.isEmpty(recvAuth.mDomain) && username.contains("\\")) {
            mDomainWrapper.setVisibility(View.VISIBLE);
            mAddDomain.setVisibility(View.GONE);
            if(username != null && username.length() >= (username.indexOf("\\") + 1) ) {
                String name = username.substring((username.indexOf("\\")+1));
                mUsernameView.setText(name);
            }
            mDomainEdit.setText(recvAuth.mDomain);
        } else {
            mUsernameView.setText(username);
        }
            //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_E

        if (mServiceInfo.offerPrefix) {
            final String prefix = recvAuth.mDomain;
            if (prefix != null && prefix.length() > 0) {
                mImapPathPrefixView.setText(prefix.substring(1));
            }
        }

        // The delete policy is set for all legacy accounts. For POP3 accounts, the user sets
        // the policy explicitly. For IMAP accounts, the policy is set when the Account object
        // is created. @see AccountSetupBasics#populateSetupData
        mLoadedDeletePolicy = account.getDeletePolicy();
        SpinnerOption.setSpinnerOptionValue(mDeletePolicyView, mLoadedDeletePolicy);

        int flags = recvAuth.mFlags;
        if (mServiceInfo.defaultSsl && recvAuth.isDefaultHostAuth()) { //TS: zheng.zou 2016-3-28 EMAIL BUGFIX_1863562 MOD
            flags |= HostAuth.FLAG_SSL;
        }
      //TS: tao.gan 2015-9-14 EMAIL BUGFIX_1082128 ADD_S
        //New ssv requirement:if when insert the specail sim card,
        //the default secuirty type of manual exchange account is "SSL/TLS(Accept all certificate)"
        boolean ssvEnabled = SystemProperties.getBoolean("ro.ssv.enabled", false);
        String mccmnc = SystemProperties.get("persist.sys.lang.mccmnc","");
        String protocol = recvAuth.mProtocol;
        if (HostAuth.SCHEME_EAS.equals(protocol) && ssvEnabled
                && Utilities.isNeedChange(mccmnc)) {
            flags |= HostAuth.FLAG_TRUST_ALL;
        }
        LogUtils.d(LogUtils.TAG, "Incoming setup! protocol :" + protocol
                + "  ssvEnabled : " + ssvEnabled + "   mccmnc : " + mccmnc
                + "   flags:"
                + flags);
      //TS: tao.gan 2015-9-14 EMAIL BUGFIX_1082128 ADD_E
        // Strip out any flags that are not related to security type.
        int securityTypeFlags = (flags & HostAuth.FLAG_TRANSPORTSECURITY_MASK);
        SpinnerOption.setSpinnerOptionValue(mSecurityTypeView, securityTypeFlags);

        final String hostname = recvAuth.mAddress;
        if (hostname != null) {
            mServerView.setText(hostname);
        }

        final int port = recvAuth.mPort;
        if (port != HostAuth.PORT_UNKNOWN) {
            mPortView.setText(Integer.toString(port));
        } else {
            updatePortFromSecurityType();
        }

        if (!TextUtils.isEmpty(recvAuth.mClientCertAlias)) {
            mClientCertificateSelector.setCertificate(recvAuth.mClientCertAlias);
        }

        // Make a deep copy of the HostAuth to compare with later
        final Parcel parcel = Parcel.obtain();
        parcel.writeParcelable(recvAuth, recvAuth.describeContents());
        parcel.setDataPosition(0);
        mLoadedRecvAuth = parcel.readParcelable(HostAuth.class.getClassLoader());
        parcel.recycle();

        mLoaded = true;
        validateFields();
    }

    //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
    private void loadDomainView(){
       LogUtils.i("isExchange", "isExchange is "+isExchange);
       if(isExchange){
           //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_S
           mAddDomain.setVisibility(View.VISIBLE);
           final Account account = mSetupData.getAccount();
           final HostAuth recvAuth = account.getOrCreateHostAuthRecv(mAppContext);
           if (!TextUtils.isEmpty(recvAuth.mDomain)){
               mHasDomain = true;
           }
           //TS: lin-zhou 2015-10-14 EMAIL BUGFIX_710083 MOD_S
           if (!mHasDomain){
               mAddDomain.setVisibility(View.VISIBLE);
               mDomainWrapper.setVisibility(View.GONE);
           } else {
               mDomainWrapper.setVisibility(View.VISIBLE);
               mAddDomain.setVisibility(View.GONE);
               mDomainEdit.setText(mDomainEdit.getText().toString());
           }
           //TS: lin-zhou 2015-10-14 EMAIL BUGFIX_710083 MOD_E
           //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_E
       }else{
           mAddDomain.setVisibility(View.GONE);
       }
    }
    //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_E

    /**
     * Check the values in the fields and decide if it makes sense to enable the "next" button
     */
    private void validateFields() {
        if (!mLoaded) return;
        enableNextButton(!TextUtils.isEmpty(mUsernameView.getText())
                && mAuthenticationView.getAuthValid()
                && Utility.isServerNameValid(mServerView)
                && Utility.isPortFieldValid(mPortView));

        mCacheLoginCredential = mUsernameView.getText().toString().trim();
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
        if(!mPasswordText.getText().toString().isEmpty()){
            mClientView.setTextColor(getResources().getColor(R.color.text_front_color_1));
        }else{
            mClientView.setTextColor(getResources().getColor(R.color.text_front_color_2));
        }
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
    }

    private int getPortFromSecurityType(boolean useSsl) {
        return useSsl ? mServiceInfo.portSsl : mServiceInfo.port;
    }

    private boolean getSslSelected() {
        final int securityType =
                (Integer)((SpinnerOption)mSecurityTypeView.getSelectedItem()).value;
        return ((securityType & HostAuth.FLAG_SSL) != 0);
    }

    public void onUseSslChanged(boolean useSsl) {
        if (mServiceInfo.offerCerts) {
            final int mode = useSsl ? View.VISIBLE : View.GONE;
            mClientCertificateSelector.setVisibility(mode);
            String deviceId = "";
            try {
                deviceId = Device.getDeviceId(mAppContext);
            } catch (IOException e) {
                // Not required
            }
            ((TextView) UiUtilities.getView(getView(), R.id.device_id)).setText(deviceId);

            mDeviceIdSection.setVisibility(mode);
        }
    }

    private void updatePortFromSecurityType() {
        final boolean sslSelected = getSslSelected();
        final int port = getPortFromSecurityType(sslSelected);
        mPortView.setText(Integer.toString(port));
        onUseSslChanged(sslSelected);
    }

    @Override
    public void saveSettings() {
        // Reset this here so we don't get stuck on this screen
        mLoadedDeletePolicy = mSetupData.getAccount().getDeletePolicy();
        super.saveSettings();
    }

    private static class SaveSettingsLoader extends MailAsyncTaskLoader<Boolean> {
        private final SetupDataFragment mSetupData;
        private final boolean mSettingsMode;

        private SaveSettingsLoader(Context context, SetupDataFragment setupData,
                boolean settingsMode) {
            super(context);
            mSetupData = setupData;
            mSettingsMode = settingsMode;
        }

        @Override
        public Boolean loadInBackground() {
            if (mSettingsMode) {
                saveSettingsAfterEdit(getContext(), mSetupData);
            } else {
                saveSettingsAfterSetup(getContext(), mSetupData);
            }
            return true;
        }

        @Override
        protected void onDiscardResult(Boolean result) {}
    }

    @Override
    public Loader<Boolean> getSaveSettingsLoader() {
        return new SaveSettingsLoader(mAppContext, mSetupData, mSettingsMode);
    }

    /**
     * Entry point from Activity after editing settings and verifying them.  Must be FLOW_MODE_EDIT.
     * Note, we update account here (as well as the account.mHostAuthRecv) because we edit
     * account's delete policy here.
     * Blocking - do not call from UI Thread.
     */
    public static void saveSettingsAfterEdit(Context context, SetupDataFragment setupData) {
        final Account account = setupData.getAccount();
        account.update(context, account.toContentValues());
        final Credential cred = account.mHostAuthRecv.mCredential;
        if (cred != null) {
            if (cred.isSaved()) {
                cred.update(context, cred.toContentValues());
            } else {
                cred.save(context);
                account.mHostAuthRecv.mCredentialKey = cred.mId;
            }
        }
        account.mHostAuthRecv.update(context, account.mHostAuthRecv.toContentValues());

        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        if (isExchange) {
            /*if (isProxyOn) {
                boolean useproxy = mUseProxyView.isChecked();
                mUseProxyViewSelector.updateProxyInformation(mContext,account,useproxy);
            } else {
                account.mIsUseproxy = false;
                account.mProxyAddr = "";
                account.mProxyPort = 0;
                account.mProxyUsername = "";
                account.mProxyUserpass = "";
            }*/
            EmailContent.AccountInfo.updateProxyInfo(context, account.mId,
                    account.mIsUseproxy, account.mProxyAddr,
                    account.mProxyPort, account.mProxyUsername,
                    account.mProxyUserpass); //[BUGFIX]-Mod by TCTNB.caixia.chen,01/07/2015,PR 893304
        }
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang
    }

    /**
     * Entry point from Activity after entering new settings and verifying them.  For setup mode.
     */
    public static void saveSettingsAfterSetup(Context context, SetupDataFragment setupData) {
        final Account account = setupData.getAccount();
        final HostAuth recvAuth = account.getOrCreateHostAuthRecv(context);
        final HostAuth sendAuth = account.getOrCreateHostAuthSend(context);

        // Set the username and password for the outgoing settings to the username and
        // password the user just set for incoming.  Use the verified host address to try and
        // pick a smarter outgoing address.
        //[BUGFIX]-MOD-BEGIN by SCDTABLET.shujing.jin,05/11/2016,2116445,
        //can't send email when use yahoo account
        String hostName = null;
        if (sendAuth.mAddress != null && sendAuth.mAddress.contains("smtp")) {
            hostName = sendAuth.mAddress;
        } else {
            hostName = AccountSettingsUtils.inferServerName(context, recvAuth.mAddress, null, "smtp");
        }
        //[BUGFIX]-Add-END by SCDTABLET.shujing.jin
        sendAuth.setLogin(recvAuth.mLogin, recvAuth.mPassword);
        sendAuth.setConnection(sendAuth.mProtocol, hostName, sendAuth.mPort, sendAuth.mFlags);

        //[BUGFIX]-Add by SCDTABLET.yafang.wei,08/01/2016,2637315
        // Modify to fix user name can't sync issue
        if(!("eas".equals(account.getProtocol(context)) && (recvAuth.mLogin!=null && recvAuth.mLogin.contains("\\"))))//[BUGFIX]-ADD begin by SCDTABLET.shujing.jin@tcl.com,09/06/2016,2846017
        account.setEmailAddress(recvAuth.mLogin);

    }

    //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_S
    /**
     * get the hostauth login address for this email account
     */
    public String getAccountHostAuthLogin() {
        String hostAuthLogin = null;
        if (isExchange && (mDomainEdit.getVisibility() == View.VISIBLE) &&
                !TextUtils.isEmpty(mDomainEdit.getText().toString().trim())) {
            String userName = mUsernameView.getText().toString().trim();
            String domain=mDomainEdit.getText().toString().trim();
            hostAuthLogin = domain + "\\" + userName;
        } else {
            hostAuthLogin = mUsernameView.getText().toString().trim();
        }

        return hostAuthLogin;
    }
    //TS: junwei-xu 2015-07-20 EMAIL BUGFIX-1046659 ADD_E

    /**
     * Entry point from Activity, when "next" button is clicked
     */
    @Override
    public int collectUserInputInternal() {
        final Account account = mSetupData.getAccount();

        // Make sure delete policy is an valid option before using it; otherwise, the results are
        // indeterminate, I suspect...
        if (mDeletePolicyView.getVisibility() == View.VISIBLE) {
            account.setDeletePolicy(
                    (Integer) ((SpinnerOption) mDeletePolicyView.getSelectedItem()).value);
        }

        final HostAuth recvAuth = account.getOrCreateHostAuthRecv(mAppContext);
        final String userName = mUsernameView.getText().toString().trim();
        //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_982087 MOD_S
        //AM: peng-zhang 2015-1-27 EMAIL BUGFIX_906634 ADD_S
        //AM: peng-zhang 2015-02-13 EMAIL BUGFIX_926863 MOD_S
        //AM: peng-zhang 2015-02-13 EMAIL BUGFIX_926863 MOD_E
        //AM: peng-zhang 2015-1-27 EMAIL BUGFIX_906634 ADD_E
        //AM: peng-zhang 2015-04-21 EMAIL BUGFIX_982087 MOD_E
        final String userPassword = mAuthenticationView.getPassword();
        //TS: ke.ma 2015-02-13 EMAIL CR932701 DEL_S
        //recvAuth.setLogin(userName, userPassword);
        //TS: ke.ma 2015-02-13 EMAIL CR932701 DEL_E

        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
        final String domain=mDomainEdit.getText().toString().trim();
      //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_958218 ADD_S
        final String domainUserName = domain + "\\" + userName;
      //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_958218 ADD_E
        if(isExchange&&!"Domain".equals(domain)&&!"".equals(domain)){
           recvAuth.setLogin(domainUserName, userPassword);//TS: junwei-xu 2015-3-25 EMAIL BUGFIX_958218 ADD_E
        }else{
           recvAuth.setLogin(userName, userPassword);
        }
        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_E

        if (!TextUtils.isEmpty(mAuthenticationView.getOAuthProvider())) {
            Credential cred = recvAuth.getOrCreateCredential(mContext);
            cred.mProviderId = mAuthenticationView.getOAuthProvider();
        }

        final String serverAddress = mServerView.getText().toString().trim();

        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        String portText = mPortView.getText().toString().trim();
        if (isExchange) {
            if (isProxyOn) {
                boolean useproxy = mUseProxyView.isChecked();
                mUseProxyViewSelector.saveProxyInformation(mContext,account, useproxy);
                mUseProxyViewSelector.updateProxyInformation(mContext,account, useproxy);
                if (useproxy) {
                    portText = mUseProxyViewSelector.getProxyPortInformation();
                } else {
                    portText = mPortView.getText().toString().trim();
                }
            } else {
                portText = mPortView.getText().toString().trim();
                account.mIsUseproxy = false;
                account.mProxyAddr = "";
                account.mProxyPort = 0;
                account.mProxyUsername = "";
                account.mProxyUserpass = "";
            }

            recvAuth.mIsUseproxy = account.mIsUseproxy;
            recvAuth.mProxyAddr = account.mProxyAddr;
            recvAuth.mProxyPort = account.mProxyPort;
            recvAuth.mProxyUsername = account.mProxyUsername;
            recvAuth.mProxyUserpass = account.mProxyUserpass;
        }
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang

        int serverPort;
        try {
            //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
            serverPort = Integer.parseInt(portText);
            //[FEATURE]-Mod-END by TSNJ.qinglian.zhang
        } catch (NumberFormatException e) {
            serverPort = getPortFromSecurityType(getSslSelected());
            LogUtils.d(LogUtils.TAG, "Non-integer server port; using '" + serverPort + "'");
        }
        final int securityType =
                (Integer) ((SpinnerOption) mSecurityTypeView.getSelectedItem()).value;
        recvAuth.setConnection(mBaseScheme, serverAddress, serverPort, securityType);
        if (mServiceInfo.offerPrefix) {
            final String prefix = mImapPathPrefixView.getText().toString().trim();
            recvAuth.mDomain = TextUtils.isEmpty(prefix) ? null : ("/" + prefix);
        //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_S
        } else if(!TextUtils.isEmpty(domain)) {
            recvAuth.mDomain = domain;
        } else {
            recvAuth.mDomain = null;
        }
      //TS: lin-zhou 2015-8-21 EMAIL BUGFIX_489428 MOD_E
        recvAuth.mClientCertAlias = mClientCertificateSelector.getCertificate();

        return SetupDataFragment.CHECK_INCOMING;
    }

    @Override
    public boolean haveSettingsChanged() {
        final boolean deletePolicyChanged;

        // Only verify the delete policy if the control is visible (i.e. is a pop3 account)
        if (mDeletePolicyView != null && mDeletePolicyView.getVisibility() == View.VISIBLE) {
            int newDeletePolicy =
                (Integer)((SpinnerOption)mDeletePolicyView.getSelectedItem()).value;
            deletePolicyChanged = mLoadedDeletePolicy != newDeletePolicy;
        } else {
            deletePolicyChanged = false;
        }

        return deletePolicyChanged || super.haveSettingsChanged();
    }

    @Override
    public void onValidateStateChanged() {
        validateFields();
    }

    @Override
    public void onRequestSignIn() {
        // Launch the credentials activity.
        final String protocol =
                mSetupData.getAccount().getOrCreateHostAuthRecv(mAppContext).mProtocol;
        final Intent intent = AccountCredentials.getAccountCredentialsIntent(mContext,
                mUsernameView.getText().toString(), protocol);
        startActivityForResult(intent, SIGN_IN_REQUEST);
    }

    @Override
    public void onCertificateRequested() {
        final Intent intent = new Intent(getString(R.string.intent_exchange_cert_action));
        intent.setData(CertificateRequestor.CERTIFICATE_REQUEST_URI);
        intent.putExtra(CertificateRequestor.EXTRA_HOST, mServerView.getText().toString().trim());
        try {
            intent.putExtra(CertificateRequestor.EXTRA_PORT,
                    Integer.parseInt(mPortView.getText().toString().trim()));
        } catch (final NumberFormatException e) {
            LogUtils.d(LogUtils.TAG, "Couldn't parse port %s", mPortView.getText());
        }
        //TS: Gantao 2016-2-24 EMAIL BUGFIX_1659471 MOD_S
        try {
            startActivityForResult(intent, CERTIFICATE_REQUEST);
        } catch (ActivityNotFoundException e) {
            LogUtils.e(LogUtils.TAG, "Activity not found for certificate request in incomming setting");
        }
        //TS: Gantao 2016-2-24 EMAIL BUGFIX_1659471 MOD_E
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CERTIFICATE_REQUEST && resultCode == Activity.RESULT_OK) {
            final String certAlias = data.getStringExtra(CertificateRequestor.RESULT_ALIAS);
            if (certAlias != null) {
                mClientCertificateSelector.setCertificate(certAlias);
            }
        } else if (requestCode == SIGN_IN_REQUEST && resultCode == Activity.RESULT_OK) {
            final Account account = mSetupData.getAccount();
            final HostAuth recvAuth = account.getOrCreateHostAuthRecv(mContext);
            AccountSetupCredentialsFragment.populateHostAuthWithResults(mAppContext, recvAuth,
                    data.getExtras());
            mAuthenticationView.setAuthInfo(mServiceInfo.offerOAuth, recvAuth);
        }
    }

    //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isProxyOn && (buttonView.getId() == R.id.account_use_proxy)) {
            int mode = isChecked ? View.VISIBLE : View.GONE;
            mUseProxyViewSelector.setVisibility(mode);
        }
    }
    //[FEATURE]-Add-END by TSNJ.qinglian.zhang

    //TS: zheng.zou 2015-12-17 EMAIL BUGFIX_1117195 ADD_S
    /**
     * Hides a spinner's drop down.
     */
    private void hideSpinnerDropDown(Spinner spinner) {
        if (spinner == null)
            return;
        LogUtils.i(LogUtils.TAG,"hide spinner dropdown in incoming fragment");
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //TS: zheng.zou 2015-12-17 EMAIL BUGFIX_1117195 ADD_E
}
