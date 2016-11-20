package com.tct.mail.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tct.email.R;
import com.tct.mail.utils.SortHelper;

/**
 * Created by user on 16-1-5.
 */
public class SortChooseDialog extends DialogFragment {
    private static final String EXTRA_SELECTION_ID = "selection_id";
    public static final String TAG = "SortChooseDialog";

    public static DialogFragment newInstance(int selectionId){
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SELECTION_ID, selectionId);
        DialogFragment fragment = new SortChooseDialog();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] sortTypes = getResources().getStringArray(R.array.mail_sort_types);
        int selection = getArguments().getInt(EXTRA_SELECTION_ID, SortHelper.getDefaultOrder());

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sort_title)
                .setSingleChoiceItems(sortTypes, selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = getActivity();
                        if (activity != null && activity instanceof ControllableActivity) {
                            AbstractActivityController controller = (AbstractActivityController) ((ControllableActivity) activity).getAccountController();
                            SortHelper.setCurrentSort(getSortOrder(which));
                            controller.sort(SortHelper.getCurrentSort());
                        }
                       dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private int getSortOrder(int which){
        int[] sortOrder = getResources().getIntArray(R.array.mail_sort_order);
        if (sortOrder ==null || which >= sortOrder.length){
            return SortHelper.getDefaultOrder();
        }
        return sortOrder[which];
    }

}
