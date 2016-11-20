/******************************************************************************/
/*                                                               Date:09/2013 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  wei.huang                                                       */
/*  Email  :  wei.huang@tcl-mobile.com                                        */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     : Email/tct-src/PreDefineAccountProvider.java                    */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 10/18/2014|     wei.huang        |      FR 473181       |[SDM]E-mail Port- */
/*           |                      |                      |ing               */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct;

import java.util.Map;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;

import com.tct.email.R;
import com.tct.email.activity.setup.AccountSetupFinal;

public class PreDefineAccountProvider extends Activity implements
        RadioGroup.OnCheckedChangeListener {

    private static final int ID = 0;
    private static final int LABEL = 1;
    private static final int DOMAIN = 2;
    private static final int INCOMING_SERVER = 3;
    private static final int OUTGOING_SERVER = 4;
    private static final int USERNAME = 5;
    private static final int WHETHER_DISPLAY = 6;
    private static final int CHAR_SEQU_LEN = 7;
    private static final String TAG = "PreDefineAccountProvider";
    public static final String PreDefineAccountProvider_INFO = "PreDefineAccountProvider_Info";
    public static final String KEY_CHECKED_ID = "checked_id";
    public static final int ADD_ACCOUNT_LAUNCH=1;
    public static final int ADD_ACCOUNT_SETTING=2;
    private static int addAccontType=ADD_ACCOUNT_LAUNCH;

    private RadioButton rdo1;
    private RadioButton rdo2;
    private RadioButton rdo3;
    private RadioButton rdo4;
    private RadioButton rdo5;
    private RadioButton rdo6;
    private RadioButton rdo7;
    private RadioButton rdo8;
    private RadioButton rdo9;
    private RadioButton rdo10;
    private RadioButton rdo11;
    private RadioGroup rdogroup;
    //[BUGFIX]-Del-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
    //[Email][Perso]Cann't customized email account
    //private Button btn_cancel;
    //private ImageButton btn_next;
    //[BUGFIX]-Del-END by SCDTABLET.yingjie.chen@tcl.com
    private CharSequence[] account_provider1;
    private CharSequence[] account_provider2;
    private CharSequence[] account_provider3;
    private CharSequence[] account_provider4;
    private CharSequence[] account_provider5;
    private CharSequence[] account_provider6;
    private CharSequence[] account_provider7;
    private CharSequence[] account_provider8;
    private CharSequence[] account_provider9;
    private CharSequence[] account_provider10;
    private CharSequence[] account_provider11;

    private HashMap<Integer, Object> rdo_hsmap;

    private int checked_id;

    private void initResourceRefs() {
        rdo1 = (RadioButton) findViewById(R.id.account_provider1);
        rdo2 = (RadioButton) findViewById(R.id.account_provider2);
        rdo3 = (RadioButton) findViewById(R.id.account_provider3);
        rdo4 = (RadioButton) findViewById(R.id.account_provider4);
        rdo5 = (RadioButton) findViewById(R.id.account_provider5);
        rdo6 = (RadioButton) findViewById(R.id.account_provider6);
        rdo7 = (RadioButton) findViewById(R.id.account_provider7);
        rdo8 = (RadioButton) findViewById(R.id.account_provider8);
        rdo9 = (RadioButton) findViewById(R.id.account_provider9);
        rdo10 = (RadioButton) findViewById(R.id.account_provider10);
        rdo11 = (RadioButton) findViewById(R.id.account_provider11);
        rdogroup = (RadioGroup) findViewById(R.id.account_select_radiogroup);
        //[BUGFIX]-Del-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
        // [Email][Perso]Cann't customized email accou
        //btn_cancel = (Button) findViewById(R.id.cancel_button);
        //btn_next = (ImageButton) findViewById(R.id.next_button);
        //[BUGFIX]-Del-END by SCDTABLET.yingjie.chen@tcl.com
    }

    private void updateUi() {
        rdo_hsmap = new HashMap<Integer, Object>();
        account_provider1 = getResources().getTextArray(R.array.account_provider1);
        account_provider2 = getResources().getTextArray(R.array.account_provider2);
        account_provider3 = getResources().getTextArray(R.array.account_provider3);
        account_provider4 = getResources().getTextArray(R.array.account_provider4);
        account_provider5 = getResources().getTextArray(R.array.account_provider5);
        account_provider6 = getResources().getTextArray(R.array.account_provider6);
        account_provider7 = getResources().getTextArray(R.array.account_provider7);
        account_provider8 = getResources().getTextArray(R.array.account_provider8);
        account_provider9 = getResources().getTextArray(R.array.account_provider9);
        account_provider10 = getResources().getTextArray(R.array.account_provider10);
        account_provider11 = getResources().getTextArray(R.array.account_provider11);

        if (account_provider1[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo1.setText(account_provider1[LABEL]);
            rdo1.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo1.getId(), account_provider1);
        } else {
            rdo1.setVisibility(View.GONE);
        }

        if (account_provider2[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo2.setText(account_provider2[LABEL]);
            rdo2.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo2.getId(), account_provider2);
        } else {
            rdo2.setVisibility(View.GONE);
        }

        if (account_provider3[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo3.setText(account_provider3[LABEL]);
            rdo3.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo3.getId(), account_provider3);
        } else {
            rdo3.setVisibility(View.GONE);
        }

        if (account_provider4[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo4.setText(account_provider4[LABEL]);
            rdo4.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo4.getId(), account_provider4);
        } else {
            rdo4.setVisibility(View.GONE);
        }

        if (account_provider5[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo5.setText(account_provider5[LABEL]);
            rdo5.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo5.getId(), account_provider5);
        } else {
            rdo5.setVisibility(View.GONE);
        }

        if (account_provider6[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo6.setText(account_provider6[LABEL]);
            rdo6.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo6.getId(), account_provider6);
        } else {
            rdo6.setVisibility(View.GONE);
        }

        if (account_provider7[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo7.setText(account_provider7[LABEL]);
            rdo7.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo7.getId(), account_provider7);
        } else {
            rdo7.setVisibility(View.GONE);
        }

        if (account_provider8[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo8.setText(account_provider8[LABEL]);
            rdo8.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo8.getId(), account_provider8);
        } else {
            rdo8.setVisibility(View.GONE);
        }

        if (account_provider9[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo9.setText(account_provider9[LABEL]);
            rdo9.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo9.getId(), account_provider9);
        } else {
            rdo9.setVisibility(View.GONE);
        }

        if (account_provider10[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo10.setText(account_provider10[LABEL]);
            rdo10.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo10.getId(), account_provider10);
        } else {
            rdo10.setVisibility(View.GONE);
        }

        if (account_provider11[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo11.setText(account_provider11[LABEL]);
            rdo11.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo11.getId(), account_provider11);
        } else {
            rdo11.setVisibility(View.GONE);
        }
    }

    public static Intent actionEnterPreDefineAccountProvider(final Context context,int addAccountType) {
        Intent i = new Intent(context, PreDefineAccountProvider.class);
        addAccontType=addAccountType;
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_define_account_provider);
        initResourceRefs();
        //[BUGFIX]-Del-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
        //[Email][Perso]Cann't customized email account
        //btn_cancel.setOnClickListener(this);
        //btn_next.setOnClickListener(this);
        //btn_next.setEnabled(false);
        //[BUGFIX]-Del-END by SCDTABLET.yingjie.chen@tcl.com
        updateUi();
        rdogroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        checked_id = rdogroup.getCheckedRadioButtonId();
        SharedPreferences sp = getSharedPreferences(PreDefineAccountProvider_INFO,
                Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_CHECKED_ID, checked_id).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*public void onClick(View v) {
        Integer wid_id;
        String domain_name;
        String label_name;
        if (v.getId() == R.id.next_button) {
            wid_id = (Integer) rdogroup.getCheckedRadioButtonId();
            if (wid_id == R.id.account_provider_other) {
                if(addAccontType==ADD_ACCOUNT_LAUNCH){
                    Intent intent = AccountSetupFinal.actionNewAccountWithResultIntent(this);
                    startActivity(intent);
                }else{
                    Intent intent = AccountSetupFinal.actionNewAccountIntent(this);
                    startActivity(intent);
                }
            } else {
                domain_name = "@" + ((CharSequence[]) rdo_hsmap.get(wid_id))[DOMAIN].toString();
                label_name = ((CharSequence[]) rdo_hsmap.get(wid_id))[LABEL].toString();
                if(addAccontType==ADD_ACCOUNT_LAUNCH){
                    Intent intent = AccountSetupFinal.actionNewAccountWithResultIntent(this, domain_name, label_name);
                    startActivity(intent);
                }else{
                    Intent intent = AccountSetupFinal.actionNewAccountIntent(this, domain_name, label_name);
                    startActivity(intent);
                }
            }
          finish();
        }
        if (v.getId() == R.id.cancel_button) {
            finish();
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        //[BUGFIX]-Del-BEGIN by SCDTABLET.yingjie.chen@tcl.com,02/18/2016,1604305,
        //[Email][Perso]Cann't customized email account
        //btn_next.setEnabled(true);
        //[BUGFIX]-Del-END by SCDTABLET.yingjie.chen@tcl.com
    }

}

