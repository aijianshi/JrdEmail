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
 *Tag		 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin	Modify the package conflict
 *BUGFIX-1045535  2015/07/16  zheng.zou     [Email]The download progress will change to 0% after rotate the screen.
 ============================================================================
 */
package com.tct.mail.browse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tct.email.R;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.base.Objects;
import com.tct.fw.google.common.base.Objects;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.UIProvider.AttachmentColumns;
import com.tct.mail.providers.UIProvider.AttachmentState;
//TS: MOD by wenggangjin for CONFLICT_20001 END
public class AttachmentProgressDialogFragment extends DialogFragment {
    public static final String ATTACHMENT_KEY = "attachment";
    //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_S
    private static final String SAVED_PROGRESS_KEY = "SAVED_PROGRESS_KEY";
    private static final String SAVED_INDETERMINATE_KEY = "SAVED_INDETERMINATE_KEY";
    private int mSavedProgress;
    //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_E
    private AttachmentCommandHandler mCommandHandler;

    private Attachment mAttachment;

    private ProgressDialog mDialog;

    static AttachmentProgressDialogFragment newInstance(Attachment attachment) {
        final AttachmentProgressDialogFragment f = new AttachmentProgressDialogFragment();

        // Supply the attachment as an argument.
        final Bundle args = new Bundle(1);
        args.putParcelable(ATTACHMENT_KEY, attachment);
        f.setArguments(args);

        return f;
    }

    // Public no-args constructor needed for fragment re-instantiation
    public AttachmentProgressDialogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        mAttachment = args.getParcelable(ATTACHMENT_KEY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCommandHandler = new AttachmentCommandHandler(getActivity());
        //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_S
        if (savedInstanceState != null && mDialog != null) {
            mSavedProgress = savedInstanceState.getInt(SAVED_PROGRESS_KEY);
            boolean isIndeterminate = savedInstanceState.getBoolean(SAVED_INDETERMINATE_KEY);
            mDialog.setIndeterminate(isIndeterminate);
            mDialog.setProgress(mSavedProgress);
        }
        //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_E
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle(R.string.fetching_attachment);
        mDialog.setMessage(mAttachment.getName());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setIndeterminate(true);
        mDialog.setMax(mAttachment.size);
        mDialog.setProgressNumberFormat(null);

        return mDialog;
    }

    @Override
    public  void onDismiss(DialogInterface dialog) {
        mDialog = null;
        super.onDismiss(dialog);
    }

    @Override
    public  void onCancel(DialogInterface dialog) {
        mDialog = null;

        // This needs to cancel the attachment
        cancelAttachment();
        super.onCancel(dialog);
    }

    public void cancelAttachment() {
        final ContentValues params = new ContentValues(1);
        params.put(AttachmentColumns.STATE, AttachmentState.NOT_SAVED);

        mCommandHandler.sendCommand(mAttachment.uri, params);
    }

    public void setProgress(int progress) {
        //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_S
        //note: when rotate the fisrt progress set from attachment 0,
        //which is not right, ignore it.
        if (progress == 0 && mSavedProgress != 0) {
            return;
        }
        //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_E
        if (mDialog != null) {
            mDialog.setProgress(progress);
        }
    }

    public boolean isIndeterminate() {
        return mDialog != null && mDialog.isIndeterminate();
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mDialog != null) {
            mDialog.setIndeterminate(indeterminate);
        }
    }

    public boolean isShowingDialogForAttachment(Attachment attachment) {
        return getDialog() != null
                && Objects.equal(attachment.getIdentifierUri(), mAttachment.getIdentifierUri());
    }

    //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_S
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDialog != null) {
            mSavedProgress = mDialog.getProgress();
            outState.putInt(SAVED_PROGRESS_KEY, mSavedProgress);
            outState.putBoolean(SAVED_INDETERMINATE_KEY, mDialog.isIndeterminate());
        }
    }
    //TS: zheng.zou 2015-7-16 EMAIL BUGFIX_1045535 ADD_E
}
