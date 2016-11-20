package com.tct.email.activity.setup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.tct.email.R;
import com.tct.mail.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 16-2-27.
 */
public class MessageSaveGroupDialog extends DialogFragment implements View.OnClickListener {
    private static final String FROM_MAILS_ARRAY_KEY = "from_mails_array_key";
    private static final String TO_MAILS_ARRAY_KEY = "to_mails_array_key";
    private static final String CC_MAILS_ARRAY_KEY = "cc_mails_array_key";
    private static final String BCC_MAILS_ARRAY_KEY = "bcc_mails_array_key";
    private CheckBox save_from;
    private CheckBox save_to;
    private RelativeLayout from;
    private RelativeLayout to;
    private RelativeLayout cc;
    private RelativeLayout bcc;
    private CheckBox save_cc;
    private CheckBox save_bcc;
    private static final String ACTION_SAVE_EMAIL_TO_GROUP =
            "com.android.email.MessageCompose.peopleActivity";
    private static final String GROUP_MAILS_EXTRA_KEY = "membersnumber";
    private String FROM_CHECKBOX_STATE_KEY = "from_checkbox_state_key";
    private String TO_CHECKBOX_STATE_KEY = "to_checkbox_state_key";
    private String CC_CHECKBOX_STATE_KEY = "cc_checkbox_state_key";
    private String BCC_CHECKBOX_STATE_KEY = "bcc_checkbox_state_key";
    private String[] mGroupMails;
    private String[] mFromoMails;
    private String[] mToMails;
    private String[] mCcMails;
    private String[] mBccMails;
    public static MessageSaveGroupDialog newInstance(final String[] fromMails,final String[] toMails,
                                              final String[] ccMails, final String[] BccMails) {
        final MessageSaveGroupDialog dialogFrag = new MessageSaveGroupDialog();

        final Bundle args = new Bundle(4);
        args.putStringArray(FROM_MAILS_ARRAY_KEY,fromMails);
        args.putStringArray(TO_MAILS_ARRAY_KEY, toMails);
        args.putStringArray(CC_MAILS_ARRAY_KEY, ccMails);
        args.putStringArray(BCC_MAILS_ARRAY_KEY, BccMails);
        dialogFrag.setArguments(args);

        return dialogFrag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mFromoMails=args.getStringArray(FROM_MAILS_ARRAY_KEY);
        mToMails = args.getStringArray(TO_MAILS_ARRAY_KEY);
        mCcMails = args.getStringArray(CC_MAILS_ARRAY_KEY);
        mBccMails = args.getStringArray(BCC_MAILS_ARRAY_KEY);

        View v= getActivity().getLayoutInflater().inflate(R.layout.message_save_group_dialog_view, null);
        from = (RelativeLayout) v.findViewById(R.id.from_field);
        to = (RelativeLayout) v.findViewById(R.id.to_field);
        cc = (RelativeLayout) v.findViewById(R.id.cc_field);
        bcc = (RelativeLayout) v.findViewById(R.id.bcc_field);

        save_from = (CheckBox) v.findViewById(R.id.from_box);
        save_to = (CheckBox) v.findViewById(R.id.to_box);
        save_cc = (CheckBox) v.findViewById(R.id.cc_box);
        save_bcc = (CheckBox) v.findViewById(R.id.bcc_box);


        from.setOnClickListener(this);
        to.setOnClickListener(this);
        cc.setOnClickListener(this);
        bcc.setOnClickListener(this);

        if(savedInstanceState!=null) {
            save_from.setChecked(savedInstanceState.getBoolean(FROM_CHECKBOX_STATE_KEY, false));
            save_to.setChecked(savedInstanceState.getBoolean(TO_CHECKBOX_STATE_KEY, false));
            save_cc.setChecked(savedInstanceState.getBoolean(CC_CHECKBOX_STATE_KEY, false));
            save_bcc.setChecked(savedInstanceState.getBoolean(BCC_CHECKBOX_STATE_KEY, false));
        }

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.select_contacts_field)
                .setView(v)
                .setPositiveButton(R.string.confirm_select,mOnClickListener )
                .setNegativeButton(getString(R.string.cancel_select).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing, just dimiss the dialog
                    }
                })
                .create();
        return dialog;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.from_field:
                save_from.setChecked(save_from.isChecked() ? false : true);
                break;
            case R.id.to_field:
                save_to.setChecked(save_to.isChecked() ? false : true);
                break;
            case R.id.cc_field:
                save_cc.setChecked(save_cc.isChecked()?false:true);
                break;
            case R.id.bcc_field:
                save_bcc.setChecked(save_bcc.isChecked()?false:true);
                break;
            default:
                break;
        }
    }


    DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            List<String> groupList = new ArrayList<String>();
            if (save_from.isChecked()) {
                // From field is checked, add it to groupList
                List<String> toList = Arrays.asList(mToMails);
                groupList.addAll(toList);
            }
            if (save_to.isChecked()) {
                // to field is checked, add it to groupList
                List<String> ccList = Arrays.asList(mCcMails);
                groupList.addAll(ccList);
            }
            if (save_cc.isChecked()) {
                // cc field is checked, add it to groupList
                List<String> bccList = Arrays.asList(mBccMails);
                groupList.addAll(bccList);
            }
            if (save_bcc.isChecked()) {
                // Bcc field is checked, add it to groupList
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FROM_CHECKBOX_STATE_KEY, save_from.isChecked());
        outState.putBoolean(TO_CHECKBOX_STATE_KEY, save_to.isChecked());
        outState.putBoolean(CC_CHECKBOX_STATE_KEY, save_cc.isChecked());
        outState.putBoolean(BCC_CHECKBOX_STATE_KEY, save_bcc.isChecked());
        super.onSaveInstanceState(outState);
    }

}
