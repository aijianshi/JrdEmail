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
*Tag              Date           Author        Description
*============== ============ =============== ==============================
*BUGFIX-849813  2014/11/24    wenggangjin    [Android5.0][Email] The port number is wrong in SMTP server when the security type is None
*BUGFIX-884384  2015/01/20    chenyanhua     [Email]Wrong SMTP port for security type STARTTLS
*BUGFIX-1067983 2015/08/19    lin-zhou       [Email]The port of the outgoing server will change when switch security type in hotmail.
*FR_981064      2015/11/25    lin-zhou       5.2.8ergo update
*BUGFIX-1117195 2015/12/17    zheng.zou      [Android 6.0][Email][Monkey][ANR]ANR happened during monkey test
*BUGFIX_1545643 2015/2/2     yanhua.chen     [Email]Some status bar icons are displayed in the Incoming settings and Outgoing settings of Email
*BUGFIX-1711589 2016/03/07   kaifeng.lu       [GAPP][Email]The "password" of Outgoing settings become"AUTHENTICATION" when rotate Screen
============================================================================
*/
package com.tct.email.activity.setup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.VendorPolicyLoader;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.Credential;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.email.activity.UiUtilities;
import com.tct.email.activity.setup.AuthenticationView.AuthenticationCallback;
import com.tct.email.provider.AccountBackupRestore;
import com.tct.mail.ui.MailAsyncTaskLoader;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Provides UI for SMTP account settings (for IMAP/POP accounts).
 *
 * This fragment is used by AccountSetupOutgoing (for creating accounts) and by AccountSettingsXL
 * (for editing existing accounts).
 */
public class AccountSetupOutgoingFragment extends AccountServerBaseFragment
        implements OnCheckedChangeListener, AuthenticationCallback {

    private static final int SIGN_IN_REQUEST = 1;

    private final static String STATE_KEY_LOADED = "AccountSetupOutgoingFragment.loaded";
  //TS: chenyanhua 2015-01-20 EMAIL BUGFIX_884384 MOD_S
  //private static final int SMTP_PORT_NORMAL = 587;
    private static final int SMTP_PORT_NORMAL = 25;
  //TS: chenyanhua 2015-01-20 EMAIL BUGFIX_884384 MOD_E
  //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_849813 MOD_S
    private static final int SMTP_PORT_NONE = 25;
  //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_849813 MOD_E
    private static final int SMTP_PORT_SSL    = 465;

    private EditText mUsernameView;
    private AuthenticationView mAuthenticationView;
    private TextView mAuthenticationLabel;
    private EditText mServerView;
    private EditText mPortView;
    private CheckBox mRequireLoginView;
    private Spinner mSecurityTypeView;

    // Support for lifecycle
    private boolean mLoaded;
    private ImageView clearPassword;
    private int isVisible =View.GONE;
    private String STATE_AUTHENTICATIONLABEL = "visible state";
    public static AccountSetupOutgoingFragment newInstance(boolean settingsMode) {
        final AccountSetupOutgoingFragment f = new AccountSetupOutgoingFragment();
        f.setArguments(getArgs(settingsMode));
        return f;
    }

    // Public no-args constructor needed for fragment re-instantiation
    public AccountSetupOutgoingFragment() {}

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before {@link #onActivityCreated(Bundle)}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLoaded = savedInstanceState.getBoolean(STATE_KEY_LOADED, false);
        }
        mBaseScheme = HostAuth.LEGACY_SCHEME_SMTP;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view;
        if (mSettingsMode) {
            view = inflater.inflate(R.layout.account_settings_outgoing_fragment, container, false);
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
                    R.layout.account_setup_outgoing_fragment,
                    R.string.account_setup_outgoing_headline);
        }

        mUsernameView = UiUtilities.getView(view, R.id.account_username);
        mAuthenticationView = UiUtilities.getView(view, R.id.authentication_view);
        mServerView = UiUtilities.getView(view, R.id.account_server);
        mPortView = UiUtilities.getView(view, R.id.account_port);
        mRequireLoginView = UiUtilities.getView(view, R.id.account_require_login);
        mSecurityTypeView = UiUtilities.getView(view, R.id.account_security_type);
        mRequireLoginView.setOnCheckedChangeListener(this);
        // Don't use UiUtilities here. In some configurations this view does not exist, and
        // UiUtilities throws an exception in this case.
        mAuthenticationLabel = (TextView)view.findViewById(R.id.authentication_label);

        // Updates the port when the user changes the security type. This allows
        // us to show a reasonable default which the user can change.
        mSecurityTypeView.post(new Runnable() {
            @Override
            public void run() {
                mSecurityTypeView.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                                //TS: lin-zhou 2015-08-19 EMAIL BUGFIX_1067983 MOD_S
                                final HostAuth sendAuth = mSetupData.getAccount().getOrCreateHostAuthSend(mAppContext);
                                SpinnerOption so = (SpinnerOption)mSecurityTypeView.getItemAtPosition(arg2);
                                if (so.value.equals(sendAuth.mFlags & HostAuth.FLAG_TRANSPORTSECURITY_MASK) && sendAuth.mPort != -1) {
                                    mPortView.setText(Integer.toString(sendAuth.mPort));
                                } else {
                                  updatePortFromSecurityType();
                                }
                                //TS: lin-zhou 2015-08-19 EMAIL BUGFIX_1067983 MOD_E
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
            }});

        // Calls validateFields() which enables or disables the Next button
        final TextWatcher validationTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        mUsernameView.addTextChangedListener(validationTextWatcher);
        mServerView.addTextChangedListener(validationTextWatcher);
        mPortView.addTextChangedListener(validationTextWatcher);

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

        final Context context = getActivity();
        // Note:  Strings are shared with AccountSetupIncomingFragment
        final SpinnerOption securityTypes[] = {
                new SpinnerOption(HostAuth.FLAG_NONE, context.getString(
                        R.string.account_setup_incoming_security_none_label)),
                new SpinnerOption(HostAuth.FLAG_SSL, context.getString(
                        R.string.account_setup_incoming_security_ssl_label)),
                new SpinnerOption(HostAuth.FLAG_SSL | HostAuth.FLAG_TRUST_ALL, context.getString(
                        R.string.account_setup_incoming_security_ssl_trust_certificates_label)),
                new SpinnerOption(HostAuth.FLAG_TLS, context.getString(
                        R.string.account_setup_incoming_security_tls_label)),
                new SpinnerOption(HostAuth.FLAG_TLS | HostAuth.FLAG_TRUST_ALL, context.getString(
                        R.string.account_setup_incoming_security_tls_trust_certificates_label)),
        };

        final ArrayAdapter<SpinnerOption> securityTypesAdapter =
                new ArrayAdapter<SpinnerOption>(context, android.R.layout.simple_spinner_item,
                        securityTypes);
        securityTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSecurityTypeView.setAdapter(securityTypesAdapter);

        loadSettings();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_S
        if(!mAuthenticationView.getIsDisplayPassword()){
            //Hide password
            mAuthenticationView.setHiddenPassword(mSettingsMode);
        }else {
            //Show password
            mAuthenticationView.setDisplayPassword(mSettingsMode);
        }
        //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_E
        validateFields();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_LOADED, mLoaded);
    }

    /**
     * Load the current settings into the UI
     */
    private void loadSettings() {
        if (mLoaded) return;

        final HostAuth sendAuth = mSetupData.getAccount().getOrCreateHostAuthSend(mAppContext);
        if (!mSetupData.isOutgoingCredLoaded()) {
            sendAuth.setUserName(mSetupData.getEmail());
            AccountSetupCredentialsFragment.populateHostAuthWithResults(mAppContext, sendAuth,
                    mSetupData.getCredentialResults());
            final String[] emailParts = mSetupData.getEmail().split("@");
            final String domain = emailParts[1];
            sendAuth.setConnection(sendAuth.mProtocol, domain, HostAuth.PORT_UNKNOWN,
                    HostAuth.FLAG_NONE);
            mSetupData.setOutgoingCredLoaded(true);
        }
        if ((sendAuth.mFlags & HostAuth.FLAG_AUTHENTICATE) != 0) {
            final String username = sendAuth.mLogin;
            if (username != null) {
                mUsernameView.setText(username);
                mRequireLoginView.setChecked(true);
            }

            final List<VendorPolicyLoader.OAuthProvider> oauthProviders =
                    AccountSettingsUtils.getAllOAuthProviders(getActivity());
            mAuthenticationView.setAuthInfo(true/*oauthProviders.size() > 0*/, sendAuth);//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/09/2016,2013280
        }

        final int flags = sendAuth.mFlags & HostAuth.FLAG_TRANSPORTSECURITY_MASK;
        SpinnerOption.setSpinnerOptionValue(mSecurityTypeView, flags);

        final String hostname = sendAuth.mAddress;
        if (hostname != null) {
            mServerView.setText(hostname);
        }

        final int port = sendAuth.mPort;
        if (port != -1) {
            mPortView.setText(Integer.toString(port));
        } else {
            updatePortFromSecurityType();
        }

        // Make a deep copy of the HostAuth to compare with later
        final Parcel parcel = Parcel.obtain();
        parcel.writeParcelable(sendAuth, sendAuth.describeContents());
        parcel.setDataPosition(0);
        mLoadedSendAuth = parcel.readParcelable(HostAuth.class.getClassLoader());
        parcel.recycle();

        mLoaded = true;
        validateFields();
    }

    /**
     * Preflight the values in the fields and decide if it makes sense to enable the "next" button
     */
    private void validateFields() {
        if (!mLoaded) return;
        boolean enabled =
            Utility.isServerNameValid(mServerView) && Utility.isPortFieldValid(mPortView);

        if (enabled && mRequireLoginView.isChecked()) {
            enabled = !TextUtils.isEmpty(mUsernameView.getText())
                    && mAuthenticationView.getAuthValid();
        }
        enableNextButton(enabled);
   }

    /**
     * implements OnCheckedChangeListener
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final HostAuth sendAuth = mSetupData.getAccount().getOrCreateHostAuthSend(mAppContext);
        mAuthenticationView.setAuthInfo(true, sendAuth);
        final int visibility = isChecked ? View.VISIBLE : View.GONE;
        UiUtilities.setVisibilitySafe(getView(), R.id.account_require_login_settings, visibility);
        UiUtilities.setVisibilitySafe(getView(), R.id.account_require_login_settings_2, visibility);
        validateFields();
    }

    private int getPortFromSecurityType() {
        final int securityType =
                (Integer)((SpinnerOption)mSecurityTypeView.getSelectedItem()).value;
      //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_849813 MOD_S
        if(securityType == HostAuth.FLAG_NONE){
            return SMTP_PORT_NONE;
        }
      //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_849813 MOD_E
        return (securityType & HostAuth.FLAG_SSL) != 0 ? SMTP_PORT_SSL : SMTP_PORT_NORMAL;
    }

    private void updatePortFromSecurityType() {
        final int port = getPortFromSecurityType();
        mPortView.setText(Integer.toString(port));
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
     * Blocking - do not call from UI Thread.
     */
    public static void saveSettingsAfterEdit(Context context, SetupDataFragment setupData) {
        final Account account = setupData.getAccount();
        final Credential cred = account.mHostAuthSend.mCredential;
        if (cred != null) {
            if (cred.isSaved()) {
                cred.update(context, cred.toContentValues());
            } else {
                cred.save(context);
                account.mHostAuthSend.mCredentialKey = cred.mId;
            }
        }
        account.mHostAuthSend.update(context, account.mHostAuthSend.toContentValues());
        // Update the backup (side copy) of the accounts
        AccountBackupRestore.backup(context);
    }

    /**
     * Entry point from Activity after entering new settings and verifying them.  For setup mode.
     */
    @SuppressWarnings("unused")
    public static void saveSettingsAfterSetup(Context context, SetupDataFragment setupData) {
        // No need to do anything here
    }

    /**
     * Entry point from Activity, when "next" button is clicked
     */
    @Override
    public int collectUserInputInternal() {
        final Account account = mSetupData.getAccount();
        final HostAuth sendAuth = account.getOrCreateHostAuthSend(mAppContext);

        if (mRequireLoginView.isChecked()) {
            final String userName = mUsernameView.getText().toString().trim();
            final String userPassword = mAuthenticationView.getPassword();
            sendAuth.setLogin(userName, userPassword);
        } else {
            sendAuth.setLogin(null, null);
        }

        final String serverAddress = mServerView.getText().toString().trim();
        int serverPort;
        try {
            serverPort = Integer.parseInt(mPortView.getText().toString().trim());
        } catch (NumberFormatException e) {
            serverPort = getPortFromSecurityType();
            LogUtils.d(LogUtils.TAG, "Non-integer server port; using '" + serverPort + "'");
        }
        final int securityType =
                (Integer)((SpinnerOption)mSecurityTypeView.getSelectedItem()).value;
        sendAuth.setConnection(mBaseScheme, serverAddress, serverPort, securityType);
        sendAuth.mDomain = null;

        return SetupDataFragment.CHECK_OUTGOING;
    }

    @Override
    public void onValidateStateChanged() {
        validateFields();
    }

    @Override
    public void onRequestSignIn() {
        // Launch the credential activity.
        final String protocol =
                mSetupData.getAccount().getOrCreateHostAuthSend(mAppContext).mProtocol;
        final Intent intent = AccountCredentials.getAccountCredentialsIntent(getActivity(),
                mUsernameView.getText().toString(), protocol);
        startActivityForResult(intent, SIGN_IN_REQUEST);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == SIGN_IN_REQUEST && resultCode == Activity.RESULT_OK) {
            final Account account = mSetupData.getAccount();
            final HostAuth sendAuth = account.getOrCreateHostAuthSend(getActivity());
            AccountSetupCredentialsFragment.populateHostAuthWithResults(mAppContext, sendAuth,
                    data.getExtras());
            mAuthenticationView.setAuthInfo(true, sendAuth);
        }
    }

    //TS: zheng.zou 2015-12-17 EMAIL BUGFIX_1117195 ADD_S
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //NOTE: when run monkey, the dropdown is force remove cause input timeout.
        //we hide normally to avoid this
        if (mSecurityTypeView != null) {
            mSecurityTypeView.clearFocus();
            hideSpinnerDropDown(mSecurityTypeView);
        }
    }

    /**
     * Hides a spinner's drop down.
     */
    private void hideSpinnerDropDown(Spinner spinner) {
        if (spinner == null)
            return;
        LogUtils.i(LogUtils.TAG,"hide spinner dropdown in outgoing fragment");

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
