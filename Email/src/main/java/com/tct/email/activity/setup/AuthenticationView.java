/*
 ==========================================================================
 *HISTORY
 *
 *Tag               Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin       Modify the package conflict
 *FR_981064      2015/11/25    lin-zhou        5.2.8ergo update
 *BUGFIX-1711589 2016/03/07   kaifeng.lu       [GAPP][Email]The "password" of Outgoing settings become"AUTHENTICATION" when rotate Screen
 ===========================================================================
 */
package com.tct.email.activity.setup;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.Device;
import com.tct.emailcommon.VendorPolicyLoader.OAuthProvider;
import com.tct.emailcommon.provider.Credential;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.email.activity.UiUtilities;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.annotations.VisibleForTesting;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.io.IOException;

public class AuthenticationView extends LinearLayout implements OnClickListener {

    private final static String SUPER_STATE = "super_state";
    private final static String SAVE_PASSWORD = "save_password";
    private final static String SAVE_OFFER_OAUTH = "save_offer_oauth";
    private final static String SAVE_USE_OAUTH = "save_use_oauth";
    private final static String SAVE_OAUTH_PROVIDER = "save_oauth_provider";
    //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_S
    private final static String SAVE_ISDISPLAY_PASSWORD = "save_isDisplay_password";
    //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_E

    // Views
    private TextView mAuthenticationHeader;
    private View mPasswordWrapper;
    private View mOAuthWrapper;
    private View mNoAuthWrapper;
    private TextView mPasswordLabel;
    private EditText mPasswordEdit;
    //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_S
    private ImageView mDisplayOrHidePassword;
    //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_E
    private TextView mOAuthLabel;
    private View mClearPasswordView;
    private View mClearOAuthView;
    private View mAddAuthenticationView;

    private TextWatcher mValidationTextWatcher;

    private boolean mOfferOAuth;
    private boolean mUseOAuth;
    private String mOAuthProvider;

    private boolean mAuthenticationValid;
    private AuthenticationCallback mAuthenticationCallback;
    private boolean mIsDisplayPassword = false;

    public interface AuthenticationCallback {
        public void onValidateStateChanged();

        public void onRequestSignIn();
    }

    public AuthenticationView(Context context) {
        this(context, null);
    }

    public AuthenticationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthenticationView(Context context, AttributeSet attrs, int defstyle) {
        super(context, attrs, defstyle);
        LayoutInflater.from(context).inflate(R.layout.authentication_view, this, true);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mPasswordWrapper = UiUtilities.getView(this, R.id.password_wrapper);
        mOAuthWrapper = UiUtilities.getView(this, R.id.oauth_wrapper);
        mNoAuthWrapper = UiUtilities.getView(this, R.id.no_auth_wrapper);
        mPasswordEdit = UiUtilities.getView(this, R.id.password_edit);
        //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_S
        mDisplayOrHidePassword = UiUtilities.getView(this, R.id.displayOrHide_password);
        mDisplayOrHidePassword.setOnClickListener(this);
        //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_E
        mOAuthLabel =  UiUtilities.getView(this, R.id.oauth_label);
        mClearPasswordView = UiUtilities.getView(this, R.id.clear_password);
        mClearOAuthView = UiUtilities.getView(this, R.id.clear_oauth);
        mAddAuthenticationView = UiUtilities.getView(this, R.id.add_authentication);
        // Don't use UiUtilities here, in some configurations, these view doesn't exist and
        // UiUtilities throws an exception in this case.
        mPasswordLabel = (TextView)findViewById(R.id.password_label);
        mAuthenticationHeader = (TextView)findViewById(R.id.authentication_header);

        mClearPasswordView.setOnClickListener(this);
        mClearOAuthView.setOnClickListener(this);
        mAddAuthenticationView.setOnClickListener(this);

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
        mPasswordEdit.addTextChangedListener(mValidationTextWatcher);
    }

    public void setAuthenticationCallback(final AuthenticationCallback host) {
        mAuthenticationCallback = host;
    }

    public boolean getAuthValid() {
        if (mOfferOAuth & mUseOAuth) {
            return mOAuthProvider != null;
        } else {
            return !TextUtils.isEmpty(mPasswordEdit.getText());
        }
    }

    @VisibleForTesting
    public void setPassword(final String password) {
        mPasswordEdit.setText(password);
    }

    public String getPassword() {
        return mPasswordEdit.getText().toString();
    }

    public String getOAuthProvider() {
        return mOAuthProvider;
    }


    public boolean getIsDisplayPassword(){
        return mIsDisplayPassword;
    }

    public void setIsDisplayPassword(boolean isDisplayPassword){
        mIsDisplayPassword = isDisplayPassword;
    }

    private void validateFields() {
        boolean valid = getAuthValid();
        if (valid != mAuthenticationValid) {
            mAuthenticationCallback.onValidateStateChanged();
            mAuthenticationValid = valid;
        }
        // Warn (but don't prevent) if password has leading/trailing spaces
        AccountSettingsUtils.checkPasswordSpaces(getContext(), mPasswordEdit);
    }

    public void setAuthInfo(final boolean offerOAuth, final HostAuth hostAuth) {
        mOfferOAuth = offerOAuth;

        if (mOfferOAuth) {
            final Credential cred = hostAuth.getCredential(getContext());
            if (cred != null) {
                // We're authenticated with OAuth.
                mUseOAuth = true;
                mOAuthProvider = cred.mProviderId;
            } else {
                mUseOAuth = false;
            }
        } else {
            // We're using a POP or Exchange account, which does not offer oAuth.
            mUseOAuth = false;
        }
        mPasswordEdit.setText(hostAuth.mPassword);

        if (mOfferOAuth && mUseOAuth) {
            // We're authenticated with OAuth.
            final OAuthProvider provider = AccountSettingsUtils.findOAuthProvider(
                    getContext(), mOAuthProvider);
            mOAuthLabel.setText(getContext().getString(R.string.signed_in_with_service_label,
                    provider.label));
        }

        updateVisibility();
        validateFields();
    }

    private void updateVisibility() {
        if (mOfferOAuth) {
            if (mAuthenticationHeader != null) {
                mAuthenticationHeader.setVisibility(View.VISIBLE);
                mAuthenticationHeader.setText(R.string.authentication_label);
            }
            if (mUseOAuth) {
                // We're authenticated with OAuth.
                mOAuthWrapper.setVisibility(View.VISIBLE);
                mPasswordWrapper.setVisibility(View.GONE);
                mNoAuthWrapper.setVisibility(View.GONE);
                if (mPasswordLabel != null) {
                    mPasswordLabel.setVisibility(View.VISIBLE);
                }
            } else if (!TextUtils.isEmpty(getPassword())) {
                // We're authenticated with a password.
                mOAuthWrapper.setVisibility(View.GONE);
                mPasswordWrapper.setVisibility(View.VISIBLE);
                //TS: kaifeng.lu 2016-03-07 EMAIL BUGFIX_1711589 ADD_S
                if(mPasswordLabel != null) {
                    mPasswordLabel.setVisibility(View.VISIBLE);
                }
                //TS: kaifeng.lu 2016-03-07 EMAIL BUGFIX_1711589 ADD_E
                mNoAuthWrapper.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mPasswordEdit.getText())) {
                    mPasswordEdit.requestFocus();
                }
                mClearPasswordView.setVisibility(View.VISIBLE);
            } else {
                // We have no authentication, we need to allow either password or oauth.
                mOAuthWrapper.setVisibility(View.GONE);
                mPasswordWrapper.setVisibility(View.GONE);
                mNoAuthWrapper.setVisibility(View.VISIBLE);
            }
        } else {
            // We're using a POP or Exchange account, which does not offer oAuth.
            if (mAuthenticationHeader != null) {
                mAuthenticationHeader.setVisibility(View.VISIBLE);
                mAuthenticationHeader.setText(R.string.account_setup_incoming_password_label);
            }
            mOAuthWrapper.setVisibility(View.GONE);
            mPasswordWrapper.setVisibility(View.VISIBLE);
            mNoAuthWrapper.setVisibility(View.GONE);
            mClearPasswordView.setVisibility(View.GONE);
            if (TextUtils.isEmpty(mPasswordEdit.getText())) {
                mPasswordEdit.requestFocus();
            }
            if (mPasswordLabel != null) {
                mPasswordLabel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState());
        bundle.putBoolean(SAVE_OFFER_OAUTH, mOfferOAuth);
        bundle.putBoolean(SAVE_USE_OAUTH, mUseOAuth);
        bundle.putString(SAVE_PASSWORD, getPassword());
        bundle.putString(SAVE_OAUTH_PROVIDER, mOAuthProvider);
        bundle.putBoolean(SAVE_ISDISPLAY_PASSWORD, mIsDisplayPassword);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle)parcelable;
            super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE));
            mOfferOAuth = bundle.getBoolean(SAVE_OFFER_OAUTH);
            mUseOAuth = bundle.getBoolean(SAVE_USE_OAUTH);
            mOAuthProvider = bundle.getString(SAVE_OAUTH_PROVIDER);

            final String password = bundle.getString(SAVE_PASSWORD);
            mPasswordEdit.setText(password);
            mIsDisplayPassword = bundle.getBoolean(SAVE_ISDISPLAY_PASSWORD);
            if (!TextUtils.isEmpty(mOAuthProvider)) {
                final OAuthProvider provider = AccountSettingsUtils.findOAuthProvider(
                        getContext(), mOAuthProvider);
                if (provider != null) {
                    mOAuthLabel.setText(getContext().getString(R.string.signed_in_with_service_label,
                            provider.label));
                }
            }
            updateVisibility();
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (view == mClearPasswordView) {
            mPasswordEdit.setText(null);
            updateVisibility();
            validateFields();
        } else if (view == mClearOAuthView) {
            mUseOAuth = false;
            mOAuthProvider = null;
            updateVisibility();
            validateFields();
        } else if (view == mAddAuthenticationView) {
            mAuthenticationCallback.onRequestSignIn();
        } else if (viewId == R.id.displayOrHide_password) {
            if (!mIsDisplayPassword) {
                //Show the password
                mIsDisplayPassword = true;
                mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_dark);
                mPasswordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                Selection.setSelection(mPasswordEdit.getText(), mPasswordEdit.getText().length());
            } else {
                //Hide the password
                mIsDisplayPassword = false;
                mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_grey);
                mPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                Selection.setSelection(mPasswordEdit.getText(), mPasswordEdit.getText().length());
            }
        }
    }
    //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_S
    public void setDisplayPassword(boolean mSettingsMode){
        if(mSettingsMode){
            //set icon gone
            mDisplayOrHidePassword.setVisibility(View.GONE);
        } else {
            //set icon dark
            mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_dark);
            //display password
            mPasswordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        Selection.setSelection(mPasswordEdit.getText(), mPasswordEdit.getText().length());
    }

    public void setHiddenPassword(boolean mSettingsMode){
        if(mSettingsMode){
            //set icon gone
            mDisplayOrHidePassword.setVisibility(View.GONE);
        } else {
            //set icon gray
            mDisplayOrHidePassword.setImageResource(R.drawable.ic_visibility_grey);
        }
        Selection.setSelection(mPasswordEdit.getText(), mPasswordEdit.getText().length());
    }
    //TS: lin-zhou 2015-11-25 EMAIL FR_981064 ADD_E
}
