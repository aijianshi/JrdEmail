/*
 ==========================================================================
 *HISTORY
 *
 *Tag        Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-935474      2015/02/27   chenyanhua     Error when user receives an email with attachtment bigger than 4Mb.
 ===========================================================================
 */
package com.tct.email.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.tct.email.R;


public class AttachmentInfoDialog extends DialogFragment {
    Intent actionIntent;
    OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int which) {
            // TODO Auto-generated method stub
            switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                startActivity(actionIntent);
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                dismiss();
                break;

            default:
                break;
            }
        }
    };

    public static AttachmentInfoDialog newInstance() {
        AttachmentInfoDialog attachmentInfoDialog = new AttachmentInfoDialog();
        return attachmentInfoDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        actionIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.attachment_info_dialog_wifi_title);
        builder.setMessage(R.string.attachment_info_wifi_only);
        builder.setPositiveButton(R.string.attachment_info_wifi_settings,
                listener);
        builder.setNeutralButton(R.string.attachment_info_ok, listener);
        return builder.show();

    }
}