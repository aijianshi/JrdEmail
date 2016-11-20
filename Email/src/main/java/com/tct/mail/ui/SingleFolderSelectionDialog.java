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
=========================================================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ============================================================
*BUGFIX-1035112  2015/07/02    zheng.zou      [MONKEY][Email][CRASH]CRASH: com.tct.email
*BUGFIX-1084439  2015/09/10    dong.jin       [Monkey][CRASH][Email][42# _Proto_2S]Email occurs CRASH during Monkey test
*BUGFIX-1096263  2015/10/12    jian.xu        [MONKEY][Email][CRASH]CRASH: com.tct.email
*BUGFIX-1061061  2015/12/09    zheng.zou      [Android L][Email][SBS]The move to folder list will go back to the beginning after unlock the screen.
============================================================================================================
*/
package com.tct.mail.ui;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.tct.email.R;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.ui.FolderSelectorAdapter.FolderRow;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;

import java.util.ArrayList;

/**
 * Displays a folder selection dialog for the conversation provided. It allows
 * the user to switch a conversation from one folder to another.
 */
public class SingleFolderSelectionDialog extends FolderSelectionDialog {
    public SingleFolderSelectionDialog() {}

    private static final int FOLDER_LOADER_ID = 0;
    private static final String FOLDER_QUERY_URI_TAG = "folderQueryUri";
    private boolean mItemClicked;    //TS: zheng.zou 2015-07-02 EMAIL BUGFIX_1035112 ADD

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitleId = R.string.move_to_selection_dialog_title;

        final Bundle args = new Bundle(1);
        args.putParcelable(FOLDER_QUERY_URI_TAG, !Utils.isEmpty(mAccount.fullFolderListUri) ?
                mAccount.fullFolderListUri : mAccount.folderListUri);
        final Context loaderContext = getActivity().getApplicationContext();
        getLoaderManager().initLoader(FOLDER_LOADER_ID, args,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        final Uri queryUri = args.getParcelable(FOLDER_QUERY_URI_TAG);
                        return new CursorLoader(loaderContext, queryUri,
                                UIProvider.FOLDERS_PROJECTION, null, null, null);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        final Context context = getActivity();
                        if (data == null || context == null) {
                            return;
                        }

                        final AlertDialog dialog = (AlertDialog) getDialog();
                        if (dialog == null) {
                            // This could happen if the dialog is dismissed just before the
                            // load finishes.
                            return;
                        }
                        // The number of view types changes here, so we have to reset the ListView's
                        // adapter.
//                        dialog.getListView().setAdapter(null);    //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1061061 DEL

                        mAdapter.clearSections();

                        // Create a system folder adapter and an adapter for hierarchical
                        // and user folders. If there are no folders added to either of them,
                        // do not add as a section since a 0-count adapter will result in an
                        // IndexOutOfBoundsException.
                        SystemFolderSelectorAdapter sysFolderAdapter =
                                new SystemFolderSelectorAdapter(context, data,
                                    R.layout.single_folders_view, mCurrentFolder);
                        if (sysFolderAdapter.getCount() > 0) {
                            mAdapter.addSection(sysFolderAdapter);
                        }

                        // TODO(pwestbro): determine if we need to call filterFolders
                        // if filterFolders is not necessary, remove the method decl with one arg.
                        UserFolderHierarchicalFolderSelectorAdapter hierarchicalAdapter =
                                new UserFolderHierarchicalFolderSelectorAdapter(context,
                                    AddableFolderSelectorAdapter.filterFolders(data),
                                    R.layout.single_folders_view, mCurrentFolder);
                        if (hierarchicalAdapter.getCount() > 0) {
                            mAdapter.addSection(hierarchicalAdapter);
                        }
                        //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1061061 MOD_S
                        //NOTE: use notifyDataSetChanged() so that the Listview position will not jump up to first when data changed
//                        dialog.getListView().setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1061061 MOD_E
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        mAdapter.clearSections();
                    }
                });
    }

    @Override
    protected void onListItemClick(int position) {
        //TS: zheng.zou 2015-07-02 EMAIL BUGFIX_1035112 ADD_S
        if (mItemClicked) {
            return;
        }
        mItemClicked = true;
        //TS: zheng.zou 2015-07-02 EMAIL BUGFIX_1035112 ADD_E
        final Object item = mAdapter.getItem(position);
        if (item instanceof FolderRow) {
            //TS: jian.xu 2015-10-12 EMAIL BUGFIX-1096263 MOD_S
            //Note: getConversationUpdater() maybe will throw IllegalStateException, catch it.
            try {
                final Folder folder = ((FolderRow) item).getFolder();
                ArrayList<FolderOperation> ops = new ArrayList<FolderOperation>();
                // Remove the current folder and add the new folder.
                ops.add(new FolderOperation(mCurrentFolder, false));
                ops.add(new FolderOperation(folder, true));
                //TS: jin.dong 2015-09-10 EMAIL BUGFIX_1084439 ADD_S
                //NOTE:monkey test,if fragment not running,exception thrown on FolderSelectionDialog.
                //avoid it!!!
                if (!isResumed()) {
                    LogUtils.e(LOG_TAG,
                            "SingleFolderSelectionDialog  is not running,can't update it,just return");
                    return;
                }
                //TS: jin.dong 2015-09-10 EMAIL BUGFIX_1084439 ADD_E
                getConversationUpdater()
                        .assignFolder(ops, mTarget, mBatch, true /* showUndo */, true /* isMoveTo */);
                dismiss();
            } catch (IllegalStateException e) {
                LogUtils.e(LOG_TAG, "Tried to update conversations while fragment is not running");
            }
            //TS: jian.xu 2015-10-12 EMAIL BUGFIX-1096263 MOD_E
        }
        mItemClicked = false;    //TS: zheng.zou 2015-07-02 EMAIL BUGFIX_1035112 ADD
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Do nothing.
    }
}
