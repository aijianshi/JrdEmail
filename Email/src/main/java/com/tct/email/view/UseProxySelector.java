/******************************************************************************/
/*                                                               Date:10/2014 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2014 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  qinglian.zhang                                                  */
/*  Email  :  qinglian.zhang@tcl-mobile.com                                   */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     : packages/apps/Email/src/com/android/email/view/UseProxySelect- */
/*             or.java                                                        */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 10/28/2014|     qinglian.zhang   |      FR 736417       |[Orange]Proxy su- */
/*           |                      |                      |pport             */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.email.view;

import com.tct.email.R;
import android.content.res.Resources;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.email.activity.UiUtilities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * A simple view that can be used to set proxy information when account want to
 * use proxy to build an email account.
 *
 * Host activities must register themselves view {@link #setHostActivity} for this selector to work.
 */
public class UseProxySelector extends RelativeLayout {
    private EditText mProxyView;
    private EditText mProxyPortView;
    private EditText mProxyName;
    private EditText mProxyPassWord;

    public UseProxySelector(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public UseProxySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UseProxySelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setProxyPortInformation(String string){
        mProxyPortView.setText(string);
    }

    public String getProxyPortInformation(){
        String values=mProxyView.getText().toString().trim();
        return values;
    }

    public void setProxyInformation(String proxyAddr,String proxyPort,
            String proxyUsername, String proxyUserpass){
            mProxyView.setText(proxyAddr);
            mProxyPortView.setText(proxyPort);
            mProxyName.setText(proxyUsername);
            mProxyPassWord.setText(proxyUserpass);
    }

    public void updateProxyInformation(Context mContext,Account account,boolean useproxy){
        account.mIsUseproxy = useproxy;
        account.mProxyAddr = mProxyView.getText().toString();
        account.mProxyUsername = mProxyName.getText().toString();
        account.mProxyUserpass = mProxyPassWord.getText().toString();
        account.mProxyPort = Integer.valueOf("".equals(mProxyPortView.getText().toString())?"0":mProxyPortView.getText().toString());
    }

    public void saveProxyInformation(Context mContext, Account account,
            boolean useproxy) {
        account.mIsUseproxy = useproxy;
        account.mProxyAddr = mProxyView.getText().toString();
        account.mProxyUsername = mProxyName.getText().toString();
        account.mProxyUserpass = mProxyPassWord.getText().toString();
        account.mProxyPort = Integer.valueOf("".equals(mProxyPortView.getText().toString())?"0":mProxyPortView.getText().toString());
        if (account.isSaved()) {
            EmailContent.AccountInfo.updateProxyInfo(mContext, account.mId,
                    account.mIsUseproxy, account.mProxyAddr,
                    account.mProxyPort, account.mProxyUsername,
                    account.mProxyUserpass);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mProxyView = UiUtilities.getView(this, R.id.account_proxy);
        mProxyPortView = UiUtilities.getView(this, R.id.account_proxy_port);
        mProxyName = UiUtilities.getView(this, R.id.account_proxy_name);
        mProxyPassWord = UiUtilities.getView(this, R.id.account_proxy_password);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        SavedState savedState = (SavedState) parcel;
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState());
    }

    public static class SavedState extends BaseSavedState {

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
        }
    }
}

