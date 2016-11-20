/*
 * Copyright (C) 2012 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1035430  2015/7/3    yanhua.chen      [Android5.0][Email] [Monkey][Crash][Monitor] com.tct.email crashs by java.lang.ClassCastException
 *===========================================================================
 */
package com.tct.mail.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;

import java.util.Arrays;
import java.util.Collection;

public abstract class FolderSelectionDialog extends DialogFragment implements OnClickListener {
    protected static final String LOG_TAG = LogTag.getLogTag();

    private static final String ARG_FOLDER_TAG = "folder";
    private static final String ARG_ACCOUNT_TAG = "account";
    private static final String ARG_BATCH_TAG = "batch";
    private static final String ARG_TARGET_TAG = "target";

    protected SeparatedFolderListAdapter mAdapter;
    protected Collection<Conversation> mTarget;
    // True for CAB mode
    protected boolean mBatch;
    protected Account mAccount;
    protected Folder mCurrentFolder;
    protected int mTitleId;

    public static FolderSelectionDialog getInstance(final Account account,
            final Collection<Conversation> target, final boolean isBatch,
            final Folder currentFolder, final boolean isMoveTo) {
        /*
         * TODO: This method should only be called with isMoveTo=true if this capability is not
         * present on the account, so we should be able to remove the check here.
         */
        final FolderSelectionDialog f;
        if (isMoveTo || !account.supportsCapability(
                UIProvider.AccountCapabilities.MULTIPLE_FOLDERS_PER_CONV)) {
            f = new SingleFolderSelectionDialog();
        } else {
            f = new MultiFoldersSelectionDialog();
        }
        final Bundle args = new Bundle(4);
        args.putParcelable(ARG_FOLDER_TAG, currentFolder);
        args.putParcelable(ARG_ACCOUNT_TAG, account);
        args.putBoolean(ARG_BATCH_TAG, isBatch);
        args.putParcelableArray(ARG_TARGET_TAG, target.toArray(new Conversation[target.size()]));
        f.setArguments(args);
        return f;
    }

    protected abstract void onListItemClick(int position);

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SeparatedFolderListAdapter();

        final Bundle args = getArguments();

        mCurrentFolder = args.getParcelable(ARG_FOLDER_TAG);
        mAccount = args.getParcelable(ARG_ACCOUNT_TAG);
        mBatch = args.getBoolean(ARG_BATCH_TAG);
        //TS: yanhua.chen 2015-7-3 EMAIL BUGFIX_1035430 MOD_S
        try{
            mTarget = Arrays.asList((Conversation[])args.getParcelableArray(ARG_TARGET_TAG));
        }catch(ClassCastException e){
            dismiss();//dismiss the dialog when classCastException
            LogUtils.e(LOG_TAG, "FolderSelectionDialog onCreate ClassCasetExcetpion");
        }
        //TS: yanhua.chen 2015-7-3 EMAIL BUGFIX_1035430 MOD_E
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.cancel, this)
                .setPositiveButton(R.string.ok, this)
                .setAdapter(mAdapter, this)
                .setTitle(mTitleId)
                .create();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
        return dialog;
    }

    protected ConversationUpdater getConversationUpdater() {
        if (!isResumed()) {
            throw new IllegalStateException(
                    "Tried to update conversations while fragment is not running");
        }
        final ControllableActivity activity = (ControllableActivity)getActivity();
        return activity.getConversationUpdater();
    }
}
