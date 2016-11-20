/*******************************************************************************
 *      Copyright (C) 2012 Google Inc.
 *      Licensed to The Android Open Source Project.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-988459  2015/05/08   zhaotianyong    [Email]Mail can not be forwarded if the attachment is not supported
 *BUGFIX-988459  2015/05/19   zhaotianyong    [Email]Mail can not be forwarded if the attachment is not supported
 ============================================================================
 */

package com.tct.mail.browse;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tct.email.R;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Message;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.ui.ConversationUpdater;

/**
 * A dialog which is also a fragment. This dialog shows a message, two buttons (ok/cancel), and runs
 * a listener only for the positive action (ok). Since this is a fragment, it is created
 * automatically over orientation changes. To make a listener that works with this dialog, create a
 * listener with {@link ConversationUpdater#makeDialogListener(int, boolean)}, which correctly
 * handles activity life-cycle events.
 */
public class ConfirmDialogFragment extends DialogFragment {
    /** Tag for saving the message shown in the dialog. */
    private static final String MESSAGE_KEY = "message";
    /** Tag for the dialog in the fragment manager. */
    private static final String DIALOG_TAG = "confirm-dialog";

    /**
     * Since the fragment can be easily destroyed, get the listener from the central activity
     * immediately after the positive button ("OK" in English) is clicked.  We cannot get the
     * listener in {@link #onActivityCreated(Bundle)} because the controller has not had a chance
     * to create the listener yet.
     */
    private final AlertDialog.OnClickListener POSITIVE_ACTION = new AlertDialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // Casting to ControllableActivity will crash if it fails. This is the expected
            // behavior, since the cast should always succeed. Cast failures only occur during
            // development.
            final AlertDialog.OnClickListener listener =
                    ((ControllableActivity) getActivity()).getConversationUpdater().getListener();
            if (listener != null) {
                listener.onClick(arg0, arg1);
            }
        }
    };

    /**
     * Needs a public empty constructor for instantiation on orientation changes.
     */
    public ConfirmDialogFragment() {}

    /**
     * Create a new {@link ConfirmDialogFragment}.
     * @param message
     * @return new {@link ConfirmDialogFragment} object.
     */
    public static ConfirmDialogFragment newInstance(CharSequence message) {
        final ConfirmDialogFragment f = new ConfirmDialogFragment();
        final Bundle args = new Bundle();
        args.putCharSequence(MESSAGE_KEY, message);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence message = (getArguments()).getCharSequence(MESSAGE_KEY);
        builder.setMessage(message)
               .setPositiveButton(R.string.ok, POSITIVE_ACTION)
               .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    /**
     * Display this dialog with the provided fragment manager
     * @param manager
     */
    public final void displayDialog (FragmentManager manager) {
        show(manager, DIALOG_TAG);
    }

    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX-988459 ADD_S
    public static class ForwardDialogFragment extends ConfirmDialogFragment{

        private static String ACCOUNT = "account";
        private static String MESSAGE = "source-message";

        public static ForwardDialogFragment newInstance(Account account, Message message){
            final ForwardDialogFragment f= new ForwardDialogFragment();
            final Bundle args = new Bundle();
            args.putParcelable(ACCOUNT, account);
            args.putParcelable(MESSAGE, message);
            f.setArguments(args);
            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedState) {
            final Account account = getArguments().getParcelable(ACCOUNT);
            final Message message = getArguments().getParcelable(MESSAGE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity());
            builder.setMessage(R.string.confirm_forward_message)
                    .setPositiveButton(
                            R.string.confirm_forward_drop_unload_attachment,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    ComposeActivity.forward(getActivity(), account, message);
                                }
                            })
                    .setNegativeButton(R.string.confirm_forward_cancel,null);
            return builder.create();
        }
    }
    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX-988459 ADD_E

    //TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 ADD_S
    public static class ChangeForwardDialogFragment extends ConfirmDialogFragment {

        public interface ChangeForwardCallback {
            void setComposeMode (int composeMode);
            void setNavigationItem (int selectedItem);
        }

        private static String INITIALCOMPOSEMODE = "initialComposeMode";

        public static ChangeForwardDialogFragment newInstance (int initialComposeMode) {
            final ChangeForwardDialogFragment changeToForward = new ChangeForwardDialogFragment();
            final Bundle args = new Bundle();
            args.putInt(INITIALCOMPOSEMODE, initialComposeMode);
            changeToForward.setArguments(args);
            return changeToForward;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final int initialComposeMode = getArguments().getInt(INITIALCOMPOSEMODE);
            builder.setMessage(R.string.confirm_forward_message)
            .setPositiveButton(R.string.confirm_forward_drop_unload_attachment, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() instanceof ComposeActivity) {
                        ChangeForwardCallback mChangeForwardCallback = (ChangeForwardCallback) getActivity();
                        if (mChangeForwardCallback != null) {
                            mChangeForwardCallback.setComposeMode(ComposeActivity.FORWARD);
                        }
                    }
                }
            }).setNegativeButton(R.string.confirm_forward_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() instanceof ComposeActivity) {
                        ChangeForwardCallback mChangeForwardCallback = (ChangeForwardCallback) getActivity();
                        if (mChangeForwardCallback != null) {
                            mChangeForwardCallback.setNavigationItem(initialComposeMode);
                        }
                    }
                }
            });
            return builder.create();
        }
    }
    //TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 ADD_E
}
