/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 11/11/2014|     Chongqing.zhong  |      FR 809805       |[PVS-VVS][Email]  */
/*           |                      |                      |ESP related       */
/*           |                      |                      |customization     */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.email.activity.setup;



import android.app.Activity;
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

import java.util.HashMap;
import java.util.Map;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tct.email.R;
import com.tct.email.activity.UiUtilities;

public class PreDefineAccountProviderFragment extends AccountSetupFragment implements
        OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "PreDefineAccountProvider";

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
    private RadioButton rdo12;
    private RadioButton rdo13;
    private RadioButton rdo14;
    private RadioButton rdo15;
    private RadioButton rdo16;
    private RadioButton rdo17;
    private RadioButton rdo18;
    private RadioButton rdo19;
    private RadioButton rdo20;
    private RadioButton rdo21;
    private RadioButton rdo22;
    private RadioButton rdo23;
    private RadioButton rdo24;
    private RadioButton rdo25;

    private RadioGroup rdogroup;

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
    private CharSequence[] account_provider12;
    private CharSequence[] account_provider13;
    private CharSequence[] account_provider14;
    private CharSequence[] account_provider15;
    private CharSequence[] account_provider16;
    private CharSequence[] account_provider17;
    private CharSequence[] account_provider18;
    private CharSequence[] account_provider19;
    private CharSequence[] account_provider20;
    private CharSequence[] account_provider21;
    private CharSequence[] account_provider22;
    private CharSequence[] account_provider23;
    private CharSequence[] account_provider24;
    private CharSequence[] account_provider25;
    private HashMap<Integer, Object> rdo_hsmap;

    private static final int ID = 0;
    private static final int LABEL = 1;
    private static final int DOMAIN = 2;
    private static final int INCOMING_SERVER = 3;
    private static final int OUTGOING_SERVER = 4;
    private static final int USERNAME = 5;
    private static final int WHETHER_DISPLAY = 6;
    private static final int CHAR_SEQU_LEN = 7;

    private int checked_id;
    public static final String PreDefineAccountProvider_INFO = "PreDefineAccountProvider_Info";
    public static final String KEY_CHECKED_ID = "checked_id";
    public static String account_label = "";
    private static final String KEY_DOMAIN_NAME = "domain_name";
    private static final String KEY_LABEL_NAME = "label_name";
    private static final String EXTRA_FLOW_MODE = "FLOW_MODE";
    private String mDomain;
    private String mLabel;
    private View mExitButton;// PR991067-lijuan.li@tcl.com

    public interface Callback extends AccountSetupFragment.Callback {
        void setPreValues(String domain, String label);
    }

    public static PreDefineAccountProviderFragment newInstance() {
        return new PreDefineAccountProviderFragment();
    }

    public PreDefineAccountProviderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflateTemplatedView(inflater, container,
                R.layout.pre_define_account_provider, -1);
        initResourceRefs(view);
        updateUi();
        rdogroup.setOnCheckedChangeListener(this);
        setNextButtonEnabled(false);
        // PR991067-lijuan.li@tcl.com begin
        setPreviousButtonVisibility(View.GONE);
        mExitButton = UiUtilities.getView(view, R.id.exit);
        mExitButton.setVisibility(View.VISIBLE);
        mExitButton.setOnClickListener(this);
        // PR991067-lijuan.li@tcl.com end
        return view;
    }

    private void initResourceRefs(View view) {
        rdo1 = UiUtilities.getView(view, R.id.account_provider1);
        rdo2 = UiUtilities.getView(view, R.id.account_provider2);
        rdo3 = UiUtilities.getView(view, R.id.account_provider3);
        rdo4 = UiUtilities.getView(view, R.id.account_provider4);
        rdo5 = UiUtilities.getView(view, R.id.account_provider5);
        rdo6 = UiUtilities.getView(view, R.id.account_provider6);
        rdo7 = UiUtilities.getView(view, R.id.account_provider7);
        rdo8 = UiUtilities.getView(view, R.id.account_provider8);
        rdo9 = UiUtilities.getView(view, R.id.account_provider9);
        rdo10 = UiUtilities.getView(view, R.id.account_provider10);
        rdo11 = UiUtilities.getView(view, R.id.account_provider11);
        rdo12 = UiUtilities.getView(view, R.id.account_provider12);
        rdo13 = UiUtilities.getView(view, R.id.account_provider13);
        rdo14 = UiUtilities.getView(view, R.id.account_provider14);
        rdo15 = UiUtilities.getView(view, R.id.account_provider15);
        rdo16 = UiUtilities.getView(view, R.id.account_provider16);
        rdo17 = UiUtilities.getView(view, R.id.account_provider17);
        rdo18 = UiUtilities.getView(view, R.id.account_provider18);
        rdo19 = UiUtilities.getView(view, R.id.account_provider19);
        rdo20 = UiUtilities.getView(view, R.id.account_provider20);
        rdo21 = UiUtilities.getView(view, R.id.account_provider21);
        rdo22 = UiUtilities.getView(view, R.id.account_provider22);
        rdo23 = UiUtilities.getView(view, R.id.account_provider23);
        rdo24 = UiUtilities.getView(view, R.id.account_provider24);
        rdo25 = UiUtilities.getView(view, R.id.account_provider25);

        rdogroup = UiUtilities.getView(view, R.id.account_select_radiogroup);
    }

    private void updateUi()
    {
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
        account_provider12 = getResources().getTextArray(R.array.account_provider12);
        account_provider13 = getResources().getTextArray(R.array.account_provider13);
        account_provider14 = getResources().getTextArray(R.array.account_provider14);
        account_provider15 = getResources().getTextArray(R.array.account_provider15);
        account_provider16 = getResources().getTextArray(R.array.account_provider16);
        account_provider17 = getResources().getTextArray(R.array.account_provider17);
        account_provider18 = getResources().getTextArray(R.array.account_provider18);
        account_provider19 = getResources().getTextArray(R.array.account_provider19);
        account_provider20 = getResources().getTextArray(R.array.account_provider20);
        account_provider21 = getResources().getTextArray(R.array.account_provider21);
        account_provider22 = getResources().getTextArray(R.array.account_provider22);
        account_provider23 = getResources().getTextArray(R.array.account_provider23);
        account_provider24 = getResources().getTextArray(R.array.account_provider24);
        account_provider25 = getResources().getTextArray(R.array.account_provider25);

        if (account_provider1[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo1.setText(account_provider1[LABEL]);
            rdo1.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo1.getId(), account_provider1);

        }
        else {
            rdo1.setVisibility(View.GONE);
        }

        if (account_provider2[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo2.setText(account_provider2[LABEL]);
            rdo2.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo2.getId(), account_provider2);
        }
        else {
            rdo2.setVisibility(View.GONE);
        }

        if (account_provider3[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo3.setText(account_provider3[LABEL]);
            rdo3.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo3.getId(), account_provider3);
        }
        else {
            rdo3.setVisibility(View.GONE);
        }

        if (account_provider4[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo4.setText(account_provider4[LABEL]);
            rdo4.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo4.getId(), account_provider4);
        }
        else {
            rdo4.setVisibility(View.GONE);
        }

        if (account_provider5[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo5.setText(account_provider5[LABEL]);
            rdo5.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo5.getId(), account_provider5);
        }
        else {
            rdo5.setVisibility(View.GONE);
        }

        if (account_provider6[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo6.setText(account_provider6[LABEL]);
            rdo6.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo6.getId(), account_provider6);
        }
        else {
            rdo6.setVisibility(View.GONE);
        }

        if (account_provider7[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo7.setText(account_provider7[LABEL]);
            rdo7.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo7.getId(), account_provider7);
        }
        else {
            rdo7.setVisibility(View.GONE);
        }

        if (account_provider8[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo8.setText(account_provider8[LABEL]);
            rdo8.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo8.getId(), account_provider8);
        }
        else {
            rdo8.setVisibility(View.GONE);
        }

        if (account_provider9[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo9.setText(account_provider9[LABEL]);
            rdo9.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo9.getId(), account_provider9);
        }
        else {
            rdo9.setVisibility(View.GONE);
        }

        if (account_provider10[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo10.setText(account_provider10[LABEL]);
            rdo10.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo10.getId(), account_provider10);
        }
        else {
            rdo10.setVisibility(View.GONE);
        }

        if (account_provider11[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo11.setText(account_provider11[LABEL]);
            rdo11.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo11.getId(), account_provider11);
        }
        else {
            rdo11.setVisibility(View.GONE);
        }

        if (account_provider12[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo12.setText(account_provider12[LABEL]);
            rdo12.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo12.getId(), account_provider12);
        }
        else {
            rdo12.setVisibility(View.GONE);
        }

        if (account_provider13[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo13.setText(account_provider13[LABEL]);
            rdo13.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo13.getId(), account_provider13);
        }
        else {
            rdo13.setVisibility(View.GONE);
        }

        if (account_provider14[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo14.setText(account_provider14[LABEL]);
            rdo14.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo14.getId(), account_provider14);
        }
        else {
            rdo14.setVisibility(View.GONE);
        }

        if (account_provider15[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo15.setText(account_provider15[LABEL]);
            rdo15.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo15.getId(), account_provider15);
        }
        else {
            rdo15.setVisibility(View.GONE);
        }

        if (account_provider16[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo16.setText(account_provider16[LABEL]);
            rdo16.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo16.getId(), account_provider16);
        }
        else {
            rdo16.setVisibility(View.GONE);
        }

        if (account_provider17[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo17.setText(account_provider17[LABEL]);
            rdo17.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo17.getId(), account_provider17);
        }
        else {
            rdo17.setVisibility(View.GONE);
        }

        if (account_provider18[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo18.setText(account_provider18[LABEL]);
            rdo18.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo18.getId(), account_provider18);
        }
        else {
            rdo18.setVisibility(View.GONE);
        }

        if (account_provider19[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo19.setText(account_provider19[LABEL]);
            rdo19.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo19.getId(), account_provider19);
        }
        else {
            rdo19.setVisibility(View.GONE);
        }

        if (account_provider20[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo20.setText(account_provider20[LABEL]);
            rdo20.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo20.getId(), account_provider20);
        }
        else {
            rdo20.setVisibility(View.GONE);
        }

        if (account_provider21[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo21.setText(account_provider21[LABEL]);
            rdo21.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo21.getId(), account_provider21);
        }
        else {
            rdo21.setVisibility(View.GONE);
        }

        if (account_provider22[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo22.setText(account_provider22[LABEL]);
            rdo22.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo22.getId(), account_provider22);
        }
        else {
            rdo22.setVisibility(View.GONE);
        }

        if (account_provider23[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo23.setText(account_provider23[LABEL]);
            rdo23.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo23.getId(), account_provider23);
        }
        else {
            rdo23.setVisibility(View.GONE);
        }

        if (account_provider24[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo24.setText(account_provider24[LABEL]);
            rdo24.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo24.getId(), account_provider24);
        }
        else {
            rdo24.setVisibility(View.GONE);
        }

        if (account_provider25[WHETHER_DISPLAY].toString().trim().equals("1")) {
            rdo25.setText(account_provider25[LABEL]);
            rdo25.setVisibility(View.VISIBLE);
            rdo_hsmap.put(rdo25.getId(), account_provider25);
        }
        else {
            rdo25.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        final Callback callback = (Callback) getActivity();
        Integer wid_id;
        String domain_name;
        String label_name;
        if (viewId == R.id.next) {
            // Handle "Next" button here so we can reset the manual setup diversion
            wid_id = (Integer) rdogroup.getCheckedRadioButtonId();
            if (wid_id == R.id.account_provider_other) {
                callback.setPreValues(null, null);
            }
            else {
                domain_name = "@" + ((CharSequence[]) rdo_hsmap.get(wid_id))[DOMAIN].toString();
                label_name = ((CharSequence[]) rdo_hsmap.get(wid_id))[LABEL].toString();
                callback.setPreValues(domain_name, label_name);
            }
            callback.onNextButton();
        } else if (viewId == R.id.exit) {// PR991067-lijuan.li@tcl.com
            getActivity().onBackPressed();
        } else {
            super.onClick(v);
        }
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        Integer wid_id = (Integer) rdogroup.getCheckedRadioButtonId();
        if (-1 != wid_id) {
            setNextButtonEnabled(true);
        }
    }
}
