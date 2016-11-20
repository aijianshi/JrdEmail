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
/*  Author :  Chao Zhang                                                      */
/*  Email  :  chaozhang@jrdcom.com                                            */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     : Email/src/com/tct/email/activity/ConnectionAlertDialog.ja- */
/*             va                                                             */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/25/2014|     Chao Zhang       |      FR 631895 	   |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.email.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.tct.email.R;

public class ConnectionAlertDialog extends DialogFragment {
    public static ConnectionAlertDialog newInstance() {
        ConnectionAlertDialog frag = new ConnectionAlertDialog();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.unable_to_connect)
                .setMessage(R.string.need_connection_prompt)
                .setPositiveButton(getString(R.string.connection_settings),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                startActivity(new Intent(
                                        Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.cancel_action), null)
                .create();
    }
}
