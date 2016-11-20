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
 /*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *FEATURE_658300  2015/9/29         tianjing.su     [Android L][Email]There is no touch to display password function when creating account
 *BUGFIX-956919  2015-12-03  yanhua.chen     [Android L][Email][UE]The font of "Email account" string change to black after rorating screen and the check box style is not consistent after rotating screen..
 *BUGFIX_1214146 2015/12/28  yanhua.chen     [Email]The button "DONE" is display brightness but can not click in Outgoing settings of IMAP account
 *===========================================================================
 */
package com.tct.email.activity.setup;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.Device;
import com.tct.emailcommon.VendorPolicyLoader.OAuthProvider;
import com.tct.emailcommon.provider.Credential;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.utility.CertificateRequestor;
import com.tct.mail.utils.LogUtils;
import com.tct.email.activity.UiUtilities;
import com.tct.email.service.EmailServiceUtils;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.email.view.CertificateSelector;
import com.tct.email.view.CertificateSelector.HostCallback;

import java.io.IOException;
import java.util.List;

public class AccountSetupCredentialsFragment extends AccountSetupFragment
        implements OnClickListener, HostCallback {

    private static final int CERTIFICATE_REQUEST = 1000;

    private static final String EXTRA_EMAIL = "email";
    private static final String EXTRA_PROTOCOL = "protocol";
    private static final String EXTRA_PASSWORD_FAILED = "password_failed";
    private static final String EXTRA_STANDALONE = "standalone";

    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_CLIENT_CERT = "certificate";
    public static final String EXTRA_OAUTH_PROVIDER = "provider";
    public static final String EXTRA_OAUTH_ACCESS_TOKEN = "accessToken";
    public static final String EXTRA_OAUTH_REFRESH_TOKEN = "refreshToken";
    public static final String EXTRA_OAUTH_EXPIRES_IN_SECONDS = "expiresInSeconds";
    public static final String SAVE_STATE_IS_DISPLAY_PASSWORD = "isDisplayPassword";

    private View mOAuthGroup;
    private View mOAuthButton;
    private EditText mImapPasswordText;
    private EditText mRegularPasswordText;
    //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_S
    private ImageView mDisplayOrHidePassword;
    //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_E
    private TextWatcher mValidationTextWatcher;
    private TextView mPasswordWarningLabel;
    private TextView mEmailConfirmationLabel;
    private TextView mEmailConfirmation;
    private CertificateSelector mClientCertificateSelector;
    private View mDeviceIdSection;
    private TextView mDeviceId;

    private String mEmailAddress;
    private boolean mOfferOAuth;
    private boolean mOfferCerts;
    //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
    private  TextView mClientView;
    private boolean isShow;
    //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
    private String mProviderId;
    List<OAuthProvider> mOauthProviders;

    private Context mAppContext;

    private Bundle mResults;
    private boolean mIsDisplayPassword = false;

    public interface Callback extends AccountSetupFragment.Callback {
        void onCredentialsComplete(Bundle results);
    }

    /**
     * Create a new instance of this fragment with the appropriate email and protocol
     * @param email login address for OAuth purposes
     * @param protocol protocol of the service we're gathering credentials for
     * @param clientCert alias of existing client cert
     * @param passwordFailed true if the password attempt previously failed
     * @param standalone true if this is not being inserted in the setup flow
     * @return new fragment instance
     */
    public static AccountSetupCredentialsFragment newInstance(final String email,
            final String protocol, final String clientCert, final boolean passwordFailed,
            final boolean standalone) {
        final AccountSetupCredentialsFragment f = new AccountSetupCredentialsFragment();
        final Bundle b = new Bundle(5);
        b.putString(EXTRA_EMAIL, email);
        b.putString(EXTRA_PROTOCOL, protocol);
        b.putString(EXTRA_CLIENT_CERT, clientCert);
        b.putBoolean(EXTRA_PASSWORD_FAILED, passwordFailed);
        b.putBoolean(EXTRA_STANDALONE, standalone);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final boolean standalone = getArguments().getBoolean(EXTRA_STANDALONE);
        final View view;
        if (standalone) {
            view = inflater.inflate(R.layout.account_credentials_fragment, container, false);
            mNextButton = UiUtilities.getView(view, R.id.done);
            mNextButton.setOnClickListener(this);
            mPreviousButton = UiUtilities.getView(view, R.id.cancel);
            mPreviousButton.setOnClickListener(this);
        } else {
            // TODO: real headline string instead of sign_in_title
            view = inflateTemplatedView(inflater, container,
                    R.layout.account_setup_credentials_fragment, R.string.sign_in_title);
        }

        mImapPasswordText = UiUtilities.getView(view, R.id.imap_password);
        mRegularPasswordText = UiUtilities.getView(view, R.id.regular_password);
        //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_S
        mDisplayOrHidePassword = UiUtilities.getView(view, R.id.displayOrHide_password);
        mDisplayOrHidePassword.setOnClickListener(this);
        //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_E
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
        mClientView=UiUtilities.getView(view,R.id.title);
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
        mOAuthGroup = UiUtilities.getView(view, R.id.oauth_group);
        mOAuthButton = UiUtilities.getView(view, R.id.sign_in_with_oauth);
        mOAuthButton.setOnClickListener(this);
        mClientCertificateSelector = UiUtilities.getView(view, R.id.client_certificate_selector);
        mDeviceIdSection = UiUtilities.getView(view, R.id.device_id_section);
        mDeviceId = UiUtilities.getView(view, R.id.device_id);
        mPasswordWarningLabel  = UiUtilities.getView(view, R.id.wrong_password_warning_label);
        mEmailConfirmationLabel  = UiUtilities.getView(view, R.id.email_confirmation_label);
        mEmailConfirmation  = UiUtilities.getView(view, R.id.email_confirmation);

        mClientCertificateSelector.setHostCallback(this);
        mClientCertificateSelector.setCertificate(getArguments().getString(EXTRA_CLIENT_CERT));

        // After any text edits, call validateFields() which enables or disables the Next button
        mValidationTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        mImapPasswordText.addTextChangedListener(mValidationTextWatcher);
        mRegularPasswordText.addTextChangedListener(mValidationTextWatcher);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAppContext = getActivity().getApplicationContext();
        mEmailAddress = getArguments().getString(EXTRA_EMAIL);
        final String protocol = getArguments().getString(EXTRA_PROTOCOL);
        mOauthProviders = AccountSettingsUtils.getAllOAuthProviders(mAppContext);
        mOfferCerts = true;
        if (protocol != null) {
            final EmailServiceInfo info = EmailServiceUtils.getServiceInfo(mAppContext, protocol);
            if (info != null) {
                if (mOauthProviders.size() > 0) {
                    mOfferOAuth = info.offerOAuth;
                }
                mOfferCerts = info.offerCerts;
            }
        } else {
            // For now, we might not know what protocol we're using, so just default to
            // offering oauth
            if (mOauthProviders.size() > 0) {
                mOfferOAuth = true;
            }
        }
        // We may want to disable OAuth during the new account setup flow, but allow it elsewhere
        final boolean standalone = getArguments().getBoolean(EXTRA_STANDALONE);
        final boolean skipOAuth = !standalone &&
                getActivity().getResources().getBoolean(R.bool.skip_oauth_on_setup);
        mOfferOAuth = mOfferOAuth && !skipOAuth;

        mOAuthGroup.setVisibility(mOfferOAuth ? View.VISIBLE : View.GONE);
        mRegularPasswordText.setVisibility(mOfferOAuth ? View.GONE : View.VISIBLE);
        //[BugFix]-Add-Begin by TSNJ,Yuanxing.zhao,7/11/2014,Bug-828979
        if(mImapPasswordText!=null && mRegularPasswordText!=null){
            mImapPasswordText.requestFocus();
            mRegularPasswordText.requestFocus();
        }
        //[BugFix]-Add-End by TSNJ,Yuanxing.zhao
        if (mOfferCerts) {
            // TODO: Here we always offer certificates for any protocol that allows them (i.e.
            // Exchange). But they will really only be available if we are using SSL security.
            // Trouble is, first time through here, we haven't offered the user the choice of
            // which security type to use.
            mClientCertificateSelector.setVisibility(mOfferCerts ? View.VISIBLE : View.GONE);
            mDeviceIdSection.setVisibility(mOfferCerts ? View.VISIBLE : View.GONE);
            String deviceId = "";
            try {
                deviceId = Device.getDeviceId(getActivity());
            } catch (IOException e) {
                // Not required
            }
            mDeviceId.setText(deviceId);
        }
        final boolean passwordFailed = getArguments().getBoolean(EXTRA_PASSWORD_FAILED, false);
        setPasswordFailed(passwordFailed);
        validatePassword();
        //[BugFix]-Add-Begin by TSNJ,Yuanxing.zhao,7/11/2014,Bug-828979
        final InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
        //[BugFix]-Add-End by TSNJ,Yuanxing.zhao
        //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_S
        if(savedInstanceState != null){
            mIsDisplayPassword = savedInstanceState.getBoolean(SAVE_STATE_IS_DISPLAY_PASSWORD);
        }
        if(mIsDisplayPassword) {
            //Show password
            mRegularPasswordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            //Set image view dark
            mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_dark);
        }
        //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_E
    }

    //[BugFix]-Add-Begin by TSNJ,Yuanxing.zhao,7/11/2014,Bug-828979
    @Override
    public void onPause() {
        // Hide the soft keyboard if we lose focus
        final InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onPause();
        }
    //[BugFix]-Add-End by TSNJ,Yuanxing.zhao

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImapPasswordText != null) {
            mImapPasswordText.removeTextChangedListener(mValidationTextWatcher);
            mImapPasswordText = null;
        }
        if (mRegularPasswordText != null) {
            mRegularPasswordText.removeTextChangedListener(mValidationTextWatcher);
            mRegularPasswordText = null;
        }
    }

    public void setPasswordFailed(final boolean failed) {
        if (failed) {
            mPasswordWarningLabel.setVisibility(View.VISIBLE);
            //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
            mRegularPasswordText.getBackground().setColorFilter(getResources().getColor(R.color.text_warning_color), PorterDuff.Mode.SRC_ATOP);
            isShow=true;
            //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
            mEmailConfirmationLabel.setVisibility(View.VISIBLE);
            mEmailConfirmation.setVisibility(View.VISIBLE);
            mEmailConfirmation.setText(mEmailAddress);
        } else {
            mPasswordWarningLabel.setVisibility(View.GONE);
            mEmailConfirmationLabel.setVisibility(View.GONE);
            mEmailConfirmation.setVisibility(View.GONE);
        }
    }

    public void validatePassword() {
        setNextButtonEnabled(!TextUtils.isEmpty(getPassword()));
        //TS: yanhua.chen 2015-12-28 EMAIL BUGFIX_1214146 ADD_S
        final float nextButtonAlpha;
        if (!TextUtils.isEmpty(getPassword())) {
            nextButtonAlpha = getResources().getFraction(R.fraction.manual_setup_enabled_alpha, 1, 1);
        } else {
            nextButtonAlpha = getResources().getFraction(R.fraction.manual_setup_disabled_alpha, 1, 1);
        }
        if (mNextButton != null) {
            mNextButton.setAlpha(nextButtonAlpha);
        }
        //TS: yanhua.chen 2015-12-28 EMAIL BUGFIX_1214146 ADD_E
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 MOD_S
        if(isShow){
            mRegularPasswordText.getBackground().setColorFilter(getResources().getColor(R.color.restore_status_bar), PorterDuff.Mode.SRC_ATOP);
            mPasswordWarningLabel.setVisibility(View.GONE);
            mEmailConfirmationLabel.setVisibility(View.GONE);
            mEmailConfirmation.setVisibility(View.GONE);
            isShow=false;
        }if (!TextUtils.isEmpty(getPassword())) {
            mClientView.setTextColor(getResources().getColor(R.color.text_front_color_1));
        }else{
            // Warn (but don't prevent) if password has leading/trailing spaces
            mClientView.setTextColor(getResources().getColor(R.color.text_front_color_2));}
        //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 MOD_E
        AccountSettingsUtils.checkPasswordSpaces(mAppContext, mImapPasswordText);
        AccountSettingsUtils.checkPasswordSpaces(mAppContext, mRegularPasswordText);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == CERTIFICATE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                final String certAlias = data.getStringExtra(CertificateRequestor.RESULT_ALIAS);
                if (certAlias != null) {
                    mClientCertificateSelector.setCertificate(certAlias);
                }
            } else {
                LogUtils.e(LogUtils.TAG, "Unknown result from certificate request %d",
                        resultCode);
            }
        } else if (requestCode == OAuthAuthenticationActivity.REQUEST_OAUTH) {
            if (resultCode == OAuthAuthenticationActivity.RESULT_OAUTH_SUCCESS) {
                final String accessToken = data.getStringExtra(
                        OAuthAuthenticationActivity.EXTRA_OAUTH_ACCESS_TOKEN);
                final String refreshToken = data.getStringExtra(
                        OAuthAuthenticationActivity.EXTRA_OAUTH_REFRESH_TOKEN);
                final int expiresInSeconds = data.getIntExtra(
                        OAuthAuthenticationActivity.EXTRA_OAUTH_EXPIRES_IN, 0);
                final Bundle results = new Bundle(4);
                results.putString(EXTRA_OAUTH_PROVIDER, mProviderId);
                results.putString(EXTRA_OAUTH_ACCESS_TOKEN, accessToken);
                results.putString(EXTRA_OAUTH_REFRESH_TOKEN, refreshToken);
                results.putInt(EXTRA_OAUTH_EXPIRES_IN_SECONDS, expiresInSeconds);
                mResults = results;
                final Callback callback = (Callback) getActivity();
                callback.onCredentialsComplete(results);
            } else if (resultCode == OAuthAuthenticationActivity.RESULT_OAUTH_FAILURE
                    || resultCode == OAuthAuthenticationActivity.RESULT_OAUTH_USER_CANCELED) {
                LogUtils.i(LogUtils.TAG, "Result from oauth %d", resultCode);
            } else {
                LogUtils.wtf(LogUtils.TAG, "Unknown result code from OAUTH: %d", resultCode);
            }
        } else {
            LogUtils.e(LogUtils.TAG, "Unknown request code for onActivityResult in"
                    + " AccountSetupBasics: %d", requestCode);
        }
    }

    @Override
    public void onClick(final View view) {
        final int viewId = view.getId();
        if (viewId == R.id.sign_in_with_oauth) {
            // TODO currently the only oauth provider we support is google.
            // If we ever have more than 1 oauth provider, then we need to implement some sort
            // of picker UI. For now, just always take the first oauth provider.
            if (mOauthProviders.size() > 0) {
                mProviderId = mOauthProviders.get(0).id;
                final Intent i = new Intent(getActivity(), OAuthAuthenticationActivity.class);
                i.putExtra(OAuthAuthenticationActivity.EXTRA_EMAIL_ADDRESS, mEmailAddress);
                i.putExtra(OAuthAuthenticationActivity.EXTRA_PROVIDER, mProviderId);
                startActivityForResult(i, OAuthAuthenticationActivity.REQUEST_OAUTH);
            }
        } else if (viewId == R.id.done) {
            final Callback callback = (Callback) getActivity();
            callback.onNextButton();
        } else if (viewId == R.id.cancel) {
            final Callback callback = (Callback) getActivity();
            callback.onBackPressed();
        //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_S
        } else if(viewId == R.id.displayOrHide_password){
            if (!mIsDisplayPassword) {
                //Show the password
                mIsDisplayPassword = true;
                mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_dark);
                mRegularPasswordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                Selection.setSelection(mRegularPasswordText.getText(), mRegularPasswordText.getText().length());
            } else {
                //Hide the password
                mIsDisplayPassword = false;
                mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_grey);
                mRegularPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                Selection.setSelection(mRegularPasswordText.getText(), mRegularPasswordText.getText().length());
            }
        //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_E
        } else {
            super.onClick(view);
        }
    }

    public String getPassword() {
        if (mOfferOAuth) {
            return mImapPasswordText.getText().toString();
        } else {
            return mRegularPasswordText.getText().toString();
        }
    }

    public Bundle getCredentialResults() {
        if (mResults != null) {
            return mResults;
        }

        final Bundle results = new Bundle(2);
        results.putString(EXTRA_PASSWORD, getPassword());
        results.putString(EXTRA_CLIENT_CERT, getClientCertificate());
        return results;
    }

    public static void populateHostAuthWithResults(final Context context, final HostAuth hostAuth,
            final Bundle results) {
        if (results == null) {
            return;
        }
        final String password = results.getString(AccountSetupCredentialsFragment.EXTRA_PASSWORD);
        if (!TextUtils.isEmpty(password)) {
            hostAuth.mPassword = password;
            hostAuth.removeCredential();
        } else {
            Credential cred = hostAuth.getOrCreateCredential(context);
            cred.mProviderId = results.getString(
                    AccountSetupCredentialsFragment.EXTRA_OAUTH_PROVIDER);
            cred.mAccessToken = results.getString(
                    AccountSetupCredentialsFragment.EXTRA_OAUTH_ACCESS_TOKEN);
            cred.mRefreshToken = results.getString(
                    AccountSetupCredentialsFragment.EXTRA_OAUTH_REFRESH_TOKEN);
            cred.mExpiration = System.currentTimeMillis()
                    + results.getInt(
                    AccountSetupCredentialsFragment.EXTRA_OAUTH_EXPIRES_IN_SECONDS, 0)
                    * DateUtils.SECOND_IN_MILLIS;
            hostAuth.mPassword = null;
        }
        hostAuth.mClientCertAlias = results.getString(EXTRA_CLIENT_CERT);
    }

    public String getClientCertificate() {
        return mClientCertificateSelector.getCertificate();
    }

    @Override
    public void onCertificateRequested() {
        final Intent intent = new Intent(getString(R.string.intent_exchange_cert_action));
        intent.setData(CertificateRequestor.CERTIFICATE_REQUEST_URI);
        // We don't set EXTRA_HOST or EXTRA_PORT here because we don't know the final host/port
        // that we're connecting to yet, and autodiscover might point us somewhere else
        //TS: Gantao 2016-2-24 EMAIL BUGFIX_1659471 MOD_S
        try {
            startActivityForResult(intent, CERTIFICATE_REQUEST);
        } catch (ActivityNotFoundException e) {
            LogUtils.e(LogUtils.TAG, "Activity not found for certificate request in credentials setting");
        }
        //TS: Gantao 2016-2-24 EMAIL BUGFIX_1659471 MOD_E
    }

    //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_S
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_STATE_IS_DISPLAY_PASSWORD, mIsDisplayPassword);
    }
    //TS: tianjing.su 2015-9-29 EMAIL FEATURE_658300 ADD_E
}
