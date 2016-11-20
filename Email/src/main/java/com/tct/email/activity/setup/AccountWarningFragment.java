package com.tct.email.activity.setup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telecom.Call;

import com.tct.email.R;

/**
 * Created by user on 16-3-14.
 */
public  class AccountWarningFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(getString(R.string.dialog_warning))
                .setMessage(getString(R.string.dialog_warning_message))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Callback c =(Callback)getActivity();
                                c.proceedStateCredentials();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel_action), null)
                .create();
    }
    public static interface Callback {
        void proceedStateCredentials();
    }
}
