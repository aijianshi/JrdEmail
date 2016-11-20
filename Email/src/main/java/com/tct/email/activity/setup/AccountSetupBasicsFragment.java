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
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *CR-932701      2015/02/13   ke.ma           [Android5.0][Email][GD] Achieve 'Account setup' according to GD_v5.1.5.4
 *BUGFIX-932385  2015/03/09   ke.ma           Phone does not give option to select predefined email account
 *BUGFIX-953437  2015/03/19   ke.ma           [REG][Email]No response when tap "Done" when input email address in landscape mode
 *BUGFIX-953437  2015/03/23   ke.ma           [REG][Email]No response when tap "Done" when input email address in landscape mode
 *BUGFIX_1102520  2015/10/22  lin-zhou        [Monkey][Email]Crash]CRASH: com.tct.email
 *BUGFIX-981546  2015/11/25   junwei-xu       update Account setup style from ergo5.2.8
 ===========================================================================
 */

package com.tct.email.activity.setup;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.mail.Address;
import com.tct.email.activity.UiUtilities;
import com.tct.mail.utils.PLFUtils;

public class AccountSetupBasicsFragment extends AccountSetupFragment {
    //TS: ke.ma 2015-03-19 EMAIL BUGFIX-953437 MOD_S
    //TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 MOD_S
    private EditText mEmailView;
    //TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 MOD_E
    //TS: ke.ma 2015-03-19 EMAIL BUGFIX-953437 MOD_E
    private View mManualSetupView;
    //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
    private View mNextView;
    //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_E
    private boolean mManualSetup;
    //[FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368,
    private String preDomain;
    private String preLabel;
    //[FEATURE]-Add-END by TSNJ,wei.huang,
    //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_S
    private TextView mFlyingLabel;
    //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_E
    //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_S
    private Activity mActivity;
    //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_E

    public interface Callback extends AccountSetupFragment.Callback {
    }

    public static AccountSetupBasicsFragment newInstance() {
        return new AccountSetupBasicsFragment();
    }

    public AccountSetupBasicsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflateTemplatedView(inflater, container,
                R.layout.account_setup_basics_fragment, -1);

        //TS: ke.ma 2015-03-23 EMAIL BUGFIX-953437 DEL_S
        //TS: ke.ma 2015-03-19 EMAIL BUGFIX-953437 ADD_S
//        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
//            mEmailView=(EmailAutoCompleteTextView)mEmailView;
//        }
        //TS: ke.ma 2015-03-19 EMAIL BUGFIX-953437 ADD_E
        //TS: ke.ma 2015-03-23 EMAIL BUGFIX-953437 DEL_E

        mEmailView = UiUtilities.getView(view, R.id.account_email);
        mManualSetupView = UiUtilities.getView(view, R.id.manual_setup);
        //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_S
        mFlyingLabel = UiUtilities.getView(view, R.id.email_flying_label);
        //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_E
        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
        mNextView=UiUtilities.getView(view, R.id.next);
        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_E
        mManualSetupView.setOnClickListener(this);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
                //[BUGFIX]-Del-BEGIN by SCDTABLET.yingjie.chen@tcl.com,05/05/2016, 2013452 ,
                //[Email]When the mailbox edit box is entered, the original character is not covered.
                //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_S
//                updateFlyingLabelVisable();
                //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_E
                //[BUGFIX]-Del-END by SCDTABLET.yingjie.chen@tcl.com
            }
        };

        mEmailView.addTextChangedListener(textWatcher);

        setPreviousButtonVisibility(View.GONE);

        setManualSetupButtonVisibility(View.VISIBLE);
        //[FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368,
        Bundle data=getArguments();
        preDomain=data.getString(AccountSetupFinal.KEY_DOMAIN_NAME);
        preLabel=data.getString(AccountSetupFinal.KEY_DOMAIN_NAME);
        if(!TextUtils.isEmpty(preDomain)){
            mEmailView.requestFocus();
            mEmailView.setText(preDomain);
            mEmailView.setSelection(0);
        }
        //[FEATURE]-Add-END by TSNJ,wei.huang,

        //TS: Gantao 2015-09-08 BUGFIX_1072293 ADD_S
        String presetDomain = PLFUtils.getString(getActivity().getApplicationContext(), "def_email_accountDomainValuePreset");
        if (!TextUtils.isEmpty(presetDomain) && presetDomain.startsWith("@")) {
            mEmailView.setText(presetDomain);
        }
      //TS: Gantao 2015-09-08 BUGFIX_1072293 ADD_S
        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        validateFields();
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        final Callback callback = (Callback) getActivity();

        if (viewId == R.id.next) {
            // Handle "Next" button here so we can reset the manual setup diversion
            mManualSetup = false;
            //[BUGFIX]-Mod-BEGIN by TCTNB.jun.xie,10/31/2014,FR 736432 105
            /*
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //if (getResources().getBoolean(R.bool.feature_email_exchangeAutoSetup_on)) {
            if (PLFUtils.getBoolean(getActivity().getApplicationContext(), "feature_email_exchangeAutoSetup_on")) {
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
                mManualSetup = true;
            } else {
                mManualSetup = false;
            }
            */
            //[BUGFIX]-Mod-BEGIN by TCTNB.jun.xie,10/31/2014,FR 736432
            callback.onNextButton();
        } else if (viewId == R.id.manual_setup) {
            mManualSetup = true;
            callback.onNextButton();
        } else {
            super.onClick(v);
        }
    }

    private void validateFields() {
        final String emailField = getEmail();
        final Address[] addresses = Address.parse(emailField);

        final boolean emailValid = !TextUtils.isEmpty(emailField)
                && addresses.length == 1
                && !TextUtils.isEmpty(addresses[0].getAddress());

        setNextButtonEnabled(emailValid);
    }

    //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_S
    private void updateFlyingLabelVisable() {
        final String emailField = getEmail();
        if (TextUtils.isEmpty(emailField)) {
            mFlyingLabel.setVisibility(View.GONE);
        } else {
            mFlyingLabel.setVisibility(View.VISIBLE);
        }
    }
    //TS: junwei-xu 2015-11-25 EMAIL BUGFIX-981546 ADD_E

    /**
     * Set visibitlity of the "manual setup" button
     * @param visibility {@link View#INVISIBLE}, {@link View#VISIBLE}, {@link View#GONE}
     */
    public void setManualSetupButtonVisibility(int visibility) {
        mManualSetupView.setVisibility(visibility);
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        super.setNextButtonEnabled(enabled);
        mManualSetupView.setEnabled(enabled);
        //TS: lin-zhou 2015-10-22 EMAIL BUGFIX_1102520 ADD_S
        if(getActivity() == null){
            return;
        }
        //TS: lin-zhou 2015-10-22 EMAIL BUGFIX_1102520 ADD_E
        final float manualButtonAlpha;
        if (enabled) {
            manualButtonAlpha =
                    getResources().getFraction(R.fraction.manual_setup_enabled_alpha, 1, 1);
        } else {
            manualButtonAlpha =
                    getResources().getFraction(R.fraction.manual_setup_disabled_alpha, 1, 1);
        }
        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
        mNextView.setAlpha(manualButtonAlpha);
        //TS: ke.ma 2015-02-13 EMAIL CR932701 ADD_S
        mManualSetupView.setAlpha(manualButtonAlpha);
    }

    public void setEmail(final String email) {
        mEmailView.setText(email);
    }

    public String getEmail() {
        return mEmailView.getText().toString().trim();
    }

    public boolean isManualSetup() {
        return mManualSetup;
    }

    @Override
    public void onStart() {
        //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_S
        //Display home as up in the fragment, Ergo required that
        mActivity = getActivity();
        if(mActivity != null) {
            mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_E
        super.onResume();
    }

    //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_S
    @Override
    public void onStop() {
        //Only display home as up in the fragment,so when jump to other fragment,hide it.
        if(mActivity != null) {
            mActivity.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        super.onStop();
    }
    //TS: Gantao 2015-12-31 EMAIL BUGFIX-1246307 ADD_E
}
