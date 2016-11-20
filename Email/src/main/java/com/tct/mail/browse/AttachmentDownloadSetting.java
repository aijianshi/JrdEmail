/******************************************************************************/
/*                                                               Date:04/2014 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2014 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  Zhenhua.Fan                                                        */
/*  Email  :  Zhenhua.Fan@tcl-mobile.com                                             */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :                                                                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 03/11/2014|     Zhenhua.Fan      |      FR 801319       |<CDR-EMA-734>     */
/*           |                      |                      |Saving Attachment */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.mail.browse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.email.R;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.UIProvider;
import com.tct.permission.BaseActivity;
import com.tct.permission.PermissionUtil;
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

public class AttachmentDownloadSetting extends BaseActivity implements OnClickListener {
    private static final int DOWNLOAD_PATH = 0;
    private static final int DOWNLOAD_RATE = 6144000;
    private static final int FILE_NAME_MAX_LENGTH = 32;
    private static final String TAG = "Email";
    public static final String ATTACHMENT_KEY = "attachment";
    private static AttachmentActionHandler mActionHandler = null;
    private static MessageAttachmentBar mMessageAttachmentBar = null;
    private long mContentSize;
    private TextView mDownloadEstimateSizeView;
    private TextView mDownloadEstimateTimeView;
    private TextView mDownloadFilenameView;
    private String mDownloadPath;
    private String mDownloadPathForUser;
    private Button mDownloadStart;
    private Button mDownloadCancle;

    private EditText mDownloadPathView;
    private String mFileName;
    private String mFileNameExtension;
    private String mFilenameBase;
    private boolean mIsSelectPath = false;

    public Attachment mAttachment;

    private OnClickListener mDownloadPathListener = new OnClickListener() {
        public void onClick(View paramView) {
            AttachmentDownloadSetting.this.mIsSelectPath = true;
            try {
                //[FEATURE]-Mod-BEGIN by TSNJ,Zhenhua.Fan,05/05/2014,PR-695071,
                Intent intent = new Intent("com.android.fileexplorer.action.DIR_SEL");
                //intent.putExtra("can_create_dir", true);
                intent.putExtra("ok_text", AttachmentDownloadSetting.this.getResources()
                        .getString(android.R.string.ok));
                AttachmentDownloadSetting.this.startActivityForResult(intent, 0);
                //[FEATURE]-Mod-END by TSNJ,Zhenhua.Fan
                return;
            } catch (Exception localException) {
                String str = AttachmentDownloadSetting.this.getString(R.string.open_file_manager_failed);
				//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
				Utility.showToast(AttachmentDownloadSetting.this, str);
                //Toast.makeText(AttachmentDownloadSetting.this, str, Toast.LENGTH_LONG).show();
            }
        }
    };

    public static void setAttachmentActionHandler(AttachmentActionHandler mActionHandler) {
        AttachmentDownloadSetting.mActionHandler = mActionHandler;
    }

    public static void setMessageAttachmentBar(MessageAttachmentBar messageAttachmentBar) {
        AttachmentDownloadSetting.mMessageAttachmentBar = messageAttachmentBar;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachment_download_setting);
        final Bundle args = this.getIntent().getExtras();
        mAttachment = args.getParcelable(ATTACHMENT_KEY);

        this.mDownloadFilenameView = ((TextView) findViewById(R.id.download_filename_edit));
        this.mDownloadPathView = ((EditText)findViewById(R.id.download_filepath_selected));
        this.mDownloadEstimateSizeView = ((TextView)findViewById(R.id.download_estimate_size_content));
        this.mDownloadEstimateTimeView = ((TextView) findViewById(R.id.download_estimate_time_content));
        this.mDownloadPathView.setOnClickListener(this.mDownloadPathListener);
        this.mDownloadStart = (Button) findViewById(R.id.download_start);
        this.mDownloadCancle = (Button) findViewById(R.id.download_cancel);
        this.mDownloadStart.setOnClickListener(this);
        this.mDownloadCancle.setOnClickListener(this);

        this.mFileName = mAttachment.getName();
        this.mContentSize = mAttachment.size;
        this.mFilenameBase = getFilenameBase(this.mFileName);
        if (this.mFilenameBase.length() >= 32)
            this.mFilenameBase = this.mFilenameBase.substring(0, 32);
//        EditText localEditText = this.mDownloadFilenameView;
//        InputFilter[] arrayOfInputFilter = new InputFilter[1];
//        arrayOfInputFilter[0] = new LengthFilter(32);
//        localEditText.setFilters(arrayOfInputFilter);
        this.mDownloadFilenameView.setText(this.mFilenameBase);

        this.mDownloadPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        //this.mDownloadPathForUser = getDownloadPathForUser(this, this.mDownloadPath);
        setDownloadPathForUserText(this.mDownloadPath);
        setDownloadFileSizeText();
        setDownloadFileTimeText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_cancel:
                this.finish();
                break;
            case R.id.download_start:
                startDownload();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mIsSelectPath = false;
        if ((requestCode != 0) || (resultCode != -1) || (data == null))
            return;
        Uri uri = data.getData();
        if (uri == null)
            return;
        this.mDownloadPath = uri.getPath();
        //this.mDownloadPathForUser = getDownloadPathForUser(this, this.mDownloadPath);
        setDownloadPathForUserText(this.mDownloadPath);
    }

    private String getDownloadFileSize() {
        String str = "";
        if (this.mContentSize > 0L)
            str = Formatter.formatFileSize(this, this.mContentSize);
        return str;
    }

    private String getFilenameBase(String paramString) {
        if(paramString ==null) return "";//[BUGFIX]-Add by TSNJ,Zhenhua.Fan,09/05/2014,PR-783309
        int i = paramString.lastIndexOf('.');
        if (i != -1)
            return paramString.substring(0, i);
        return "";
    }

    private String getFilenameBaseFromUserEnter() {
        return this.mDownloadFilenameView.getText().toString();
    }

    private String getFilenameExtension(String path) {
        if(path ==null) return "";//[BUGFIX]-Add by TSNJ,Zhenhua.Fan,09/05/2014,PR-783309
        int i = path.lastIndexOf('.');
        if (i != -1)
            return path.substring(i + 1);
        return "";
    }

    private long getNeededTime() {
        long l = this.mContentSize / 6144000L;
        if (l < 1L)
            l = 1L;
        return l;
    }

    /**
     * Whether the storage status is OK for download file.
     *
     * @param downloadPath the download file's path
     * @return boolean true is ok, and false is not
     */
    public boolean isStorageStatusOK(String downloadPath) {
        String primaryPath = Environment.getExternalStorageDirectory().getPath();
        if (downloadPath.startsWith(primaryPath)) {
            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                return false;
            }
        }
        return true;
    }

    private void setDownloadFileSizeText() {
        String sizeStr;
        if (this.mContentSize <= 0L)
            sizeStr = getString(R.string.unknown_length);
        else
            sizeStr = getDownloadFileSize();

        this.mDownloadEstimateSizeView.setText(sizeStr);
    }

    private void setDownloadFileTimeText() {
        String timeText;
        if (this.mContentSize <= 0L)
            timeText = getString(R.string.unknown_length);
        else
            timeText = getNeededTime()
                    + getString(R.string.time_min);

        this.mDownloadEstimateTimeView.setText(timeText);
    }

    private void setDownloadPathForUserText(String path) {
        this.mDownloadPathView.setText(path);
    }

    private void startDownload() {
        this.mFilenameBase = getFilenameBaseFromUserEnter();
        if (this.mFilenameBase.length() <= 0) {
            //Utility.showToast(this, R.string.filename_empty_msg);
            return;
        }
        if (isStorageStatusOK(this.mDownloadPath)) {
            this.mFileNameExtension = getFilenameExtension(this.mFileName);
            this.mFileName = (this.mFilenameBase + "." + this.mFileNameExtension);

            if(mActionHandler != null) {
                String emlAttAuth = getResources().getString(R.string.eml_attachment_provider);
                if (mAttachment.contentUri != null && mAttachment.contentUri.getAuthority().equals(emlAttAuth)) {
                    if (PermissionUtil.checkAndRequestStoragePermission(this)) {
                        //mAttachment.setName(this.mFileName);
                        //mActionHandler.setAttachment(mAttachment);
                        mMessageAttachmentBar.setSavePath(mDownloadPath);
                        mActionHandler.startDownloadingAttachment(UIProvider.AttachmentDestination.EXTERNAL, mDownloadPath);
                        mMessageAttachmentBar.setSaveClicked(true);
                        this.finish();
                    }
                } else {
                    if (PermissionUtil.checkAndRequestStoragePermission(this)) {
                        //mAttachment.setName(this.mFileName);
                        //mActionHandler.setAttachment(mAttachment);
                        mMessageAttachmentBar.setSavePath(mDownloadPath);
                        mActionHandler.startDownloadingAttachment(UIProvider.AttachmentDestination.CACHE, mDownloadPath);
                        mMessageAttachmentBar.setSaveClicked(true);
                        this.finish();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.checkPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String emlAttAuth = getResources().getString(R.string.eml_attachment_provider);
            if (mAttachment.contentUri != null && mAttachment.contentUri.getAuthority().equals(emlAttAuth)) {
                //mAttachment.setName(this.mFileName);
                //mActionHandler.setAttachment(mAttachment);
                mMessageAttachmentBar.setSavePath(mDownloadPath);
                mActionHandler.startDownloadingAttachment(UIProvider.AttachmentDestination.EXTERNAL, mDownloadPath);
                mMessageAttachmentBar.setSaveClicked(true);
                this.finish();
            } else {
                //mAttachment.setName(this.mFileName);
                //mActionHandler.setAttachment(mAttachment);
                mMessageAttachmentBar.setSavePath(mDownloadPath);
                mActionHandler.startDownloadingAttachment(UIProvider.AttachmentDestination.CACHE, mDownloadPath);
                mMessageAttachmentBar.setSaveClicked(true);
                this.finish();
            }
        }
    }

    /**
     * translate the directory name into a name which is easy to know for user
     *
     * @param activity
     * @param downloadPath
     * @return String
     */
    /*
    public static String getDownloadPathForUser(Activity activity, String downloadPath) {
        if (downloadPath == null) {
            return downloadPath;
        }
        final String phoneStorageDir;
        final String sdCardDir = getExternalStorageDirectory();
        if (true) {
            phoneStorageDir = Environment.getExternalStorageDirectory().getPath();
        } else {
            phoneStorageDir = null;
        }

        if (downloadPath.startsWith(sdCardDir)) {
            String sdCardLabel = activity.getResources().getString(
                    R.string.download_path_sd_card_label);
            downloadPath = downloadPath.replace(sdCardDir, sdCardLabel);
        } else if ((phoneStorageDir != null) && downloadPath.startsWith(phoneStorageDir)) {
            String phoneStorageLabel = activity.getResources().getString(
                    R.string.download_path_phone_storage_label);
            downloadPath = downloadPath.replace(phoneStorageDir, phoneStorageLabel);
        }
        return downloadPath;
    }
    */

    /*
    private static String getExternalStorageDirectory(Context context) {
        String sd = null;
        StorageManager mStorageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();
        for (int i = 0; i < volumes.length; i++) {
            if (volumes[i].isRemovable() && volumes[i].allowMassStorage()) {
                sd = volumes[i].getPath();
            }
        }
        return sd;
    }
    */

}
