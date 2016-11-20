package com.tct.mail.compose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tct.email.R;
import com.tct.mail.utils.LogUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

//TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_S
//Show this dialog fragment when user click <Save group> option
public class SaveGroupDialog extends DialogFragment implements android.view.View.OnClickListener{

    private static final String TO_MAILS_ARRAY_KEY = "to_mails_array_key";
    private static final String CC_MAILS_ARRAY_KEY = "cc_mails_array_key";
    private static final String BCC_MAILS_ARRAY_KEY = "bcc_mails_array_key";
    private static final String ACTION_SAVE_EMAIL_TO_GROUP =
            "com.android.email.MessageCompose.peopleActivity";
    private static final String GROUP_MAILS_EXTRA_KEY = "membersnumber";

    private static final String DIALOG_FRAGMENT_TAG = "save_group_dialog_tag";

    private static final String TO_CHECKBOX_STATE_KEY = "to_checkbox_state_key";
    private static final String CC_CHECKBOX_STATE_KEY = "cc_checkbox_state_key";
    private static final String BCC_CHECKBOX_STATE_KEY = "bcc_checkbox_state_key";

    private String[] mToMails;
    private String[] mCcMails;
    private String[] mBccMails;
    private String[] mGroupMails;

    private RelativeLayout mToSelectField;
    private RelativeLayout mCcSelectField;
    private RelativeLayout mBccSelectField;
    private CheckBox mToCheckBox;
    private CheckBox mCcCheckBox;
    private CheckBox mBccCheckBox;
    //Default is not checked
    private boolean[] mChoiceState = new boolean[]{false, false, false};
    /*
     * creates a instance of SendGroupDialog
     * @param toMails The string array of to mail address
     * @param ccMails The string array of cc mail address
     * @param bccMails The string array of bcc mail address
     */
    public static SaveGroupDialog newInstance(final String[] toMails,
                                              final String[] ccMails, final String[] BccMails) {
        final SaveGroupDialog dialogFrag = new SaveGroupDialog();

        final Bundle args = new Bundle(3);
        args.putStringArray(TO_MAILS_ARRAY_KEY, toMails);
        args.putStringArray(CC_MAILS_ARRAY_KEY, ccMails);
        args.putStringArray(BCC_MAILS_ARRAY_KEY, BccMails);
        dialogFrag.setArguments(args);

        return dialogFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mToMails = args.getStringArray(TO_MAILS_ARRAY_KEY);
        mCcMails = args.getStringArray(CC_MAILS_ARRAY_KEY);
        mBccMails = args.getStringArray(BCC_MAILS_ARRAY_KEY);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.save_group_dialog_view, null);
        mToSelectField = (RelativeLayout)v.findViewById(R.id.to_field_select);
        mToSelectField.setOnClickListener(this);
        mCcSelectField = (RelativeLayout)v.findViewById(R.id.cc_field_select);
        mCcSelectField.setOnClickListener(this);
        mBccSelectField = (RelativeLayout)v.findViewById(R.id.bcc_field_select);
        mBccSelectField.setOnClickListener(this);
        mToCheckBox = (CheckBox)v.findViewById(R.id.to_box);
        mCcCheckBox = (CheckBox)v.findViewById(R.id.cc_box);
        mBccCheckBox = (CheckBox)v.findViewById(R.id.bcc_box);
        if(savedInstanceState!=null) {
            mToCheckBox.setChecked(savedInstanceState.getBoolean(TO_CHECKBOX_STATE_KEY, false));
            mCcCheckBox.setChecked(savedInstanceState.getBoolean(CC_CHECKBOX_STATE_KEY, false));
            mBccCheckBox.setChecked(savedInstanceState.getBoolean(BCC_CHECKBOX_STATE_KEY, false));
        }

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.select_contacts_field)
                .setView(v)
                .setPositiveButton(R.string.confirm_select, mOnClickListener)
                .setNegativeButton(getString(R.string.cancel_select).toUpperCase(), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing, just dimiss the dialog
                    }
                })
                .create();
        return dialog;
    }

    /*
     * When user click "OK" button
     */
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            List<String> groupList = new ArrayList<String>();
            if (mToCheckBox.isChecked()) {
                // To field is checkd, add it to groupList
                List<String> toList = Arrays.asList(mToMails);
                groupList.addAll(toList);
            }
            if (mCcCheckBox.isChecked()) {
                // Cc field is checkd, add it to groupList
                List<String> ccList = Arrays.asList(mCcMails);
                groupList.addAll(ccList);
            }
            if (mBccCheckBox.isChecked()) {
                // Bcc field is checkd, add it to groupList
                List<String> bccList = Arrays.asList(mBccMails);
                groupList.addAll(bccList);
            }

            mGroupMails = groupList.toArray(new String[groupList.size()]);
            Intent intent = new Intent(ACTION_SAVE_EMAIL_TO_GROUP);
            intent.putExtra(GROUP_MAILS_EXTRA_KEY, mGroupMails);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                LogUtils.e(LogUtils.TAG, "Not found activity when send contacts group");
            }
        }
    };

    /*
     * Show the dialog fragment
     */
    public void displayDialog(FragmentManager manager) {
        show(manager, DIALOG_FRAGMENT_TAG);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.to_field_select:
                mToCheckBox.setChecked(mToCheckBox.isChecked() ? false : true);
                break;
            case R.id.cc_field_select:
                mCcCheckBox.setChecked(mCcCheckBox.isChecked() ? false : true);
                break;
            case R.id.bcc_field_select:
                mBccCheckBox.setChecked(mBccCheckBox.isChecked() ? false : true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TO_CHECKBOX_STATE_KEY, mToCheckBox.isChecked());
        outState.putBoolean(CC_CHECKBOX_STATE_KEY, mCcCheckBox.isChecked());
        outState.putBoolean(BCC_CHECKBOX_STATE_KEY, mBccCheckBox.isChecked());
        super.onSaveInstanceState(outState);
    }

}
//TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_E