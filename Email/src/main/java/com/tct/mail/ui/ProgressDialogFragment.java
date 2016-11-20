package com.tct.mail.ui;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by zheng.zou on 15-3-21.
 *  ProgressDialogFragment
 */
/*
 ========================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ===========================================================
 *BUGFIX-949589  2015/03/21   zheng.zou       [Email]Share video over 5M by email has some iaaue
 */
public class ProgressDialogFragment extends DialogFragment{
    private static final String KEY_MESSAGE = "key_message";
    private CharSequence mMessage;
    private DialogInterface.OnCancelListener mOnCancelListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessage = getArguments().getString(KEY_MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(mMessage);
        return dialog;
    }


    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(dialog);
        }
    }

    public static ProgressDialogFragment showDialog(FragmentManager fragmentManager, String message) {
        dismissDialog(fragmentManager, message);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MESSAGE, message);
        fragment.setArguments(bundle);
        fragment.show(fragmentManager, message);
        return fragment;
    }

    public static void dismissDialog(FragmentManager fragmentManager, String message) {
        Fragment fragment = fragmentManager.findFragmentByTag(message);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commitAllowingStateLoss();
        }

    }
}
