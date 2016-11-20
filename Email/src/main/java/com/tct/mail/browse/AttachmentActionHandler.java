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
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-916938  2015/01/29   zhaotianyong    [Android5.0][Exchange]All attachments can't be opened after being saved
 *BUGFIX-935474  2015/02/27   chenyanhua      Error when user receives an email with attachtment bigger than 4Mb
 *BUGFIX-936728  2015/3/3     zhonghua.tuo    [Email]Can not open vcs and apk file in draft box and outbox
 *BUGFIX-1009013 2015/05/29   Tao.Gan        [Android5.0][Email]Attachment cannot fetch when download again.
 *BUGFIX-996126  2015/06/02   Tao.Gan        [Android5.0][Email] Every touching an apk in attachment, it show a download in Status bar
 *BUGFIX-1029228  2015/06/24  Gantao          [Android L][Email][Download]Can not Download the Email attchment
 *BUGFIX-1031608 2015/06/30  Gantao     [Email]No prompt pop up after download again the attachment.
 *BUGFIX-957636  2015/07/03   Gantao          [Android5.0][Email]Can't show pictures in eml file
 *FEATURE-ID     2015/08/27   tao.gan         Horizontal attachment
 *BUGFIX-1079571 2015/09/09   lin-zhou        [GAPP][Email]Vcf file will be imported automatically when tap download again from email
 *BUGFIX-1103647  2015/11/13  ZhangChao      [Android5.0][Email] Should show no network warning when fetching a file without network connection.
 *TASK-869664  2015/11/25   zheng.zou     [Email]Android M Permission Upgrade
 *TASK-1477377   2016/01/20   jian.xu         Save attchment to user select location
 ===========================================================================
 */

package com.tct.mail.browse;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;

import com.tct.mail.utils.AttachmentUtils;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.MimeType;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AttachmentColumns;
import com.tct.mail.providers.UIProvider.AttachmentContentValueKeys;
import com.tct.mail.providers.UIProvider.AttachmentDestination;
import com.tct.mail.providers.UIProvider.AttachmentState;
import com.tct.permission.PermissionUtil;
import com.tct.mail.utils.UiUtilities;
import com.tct.mail.utils.Utils;
//TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
import android.os.Environment;
import android.util.Log;
import com.tct.mail.utils.IOUtils;
import android.media.MediaScannerConnection;
import android.app.DownloadManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
//TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 AdD_E
import com.tct.mail.providers.UIProvider.AttachmentDestination;// MODIFIED by zheng.zou, 2016-03-22, BUG-1652810

import com.tct.email.activity.AttachmentInfoDialog;
//[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 844039
import com.tct.emailcommon.utility.Utility;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.AttachmentUtilities;
//[BUGFIX]-Add-END by TSNJ Zhenhua.Fan

//TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
//TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E
public class AttachmentActionHandler {
    private static final String PROGRESS_FRAGMENT_TAG = "attachment-progress";
    // TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
    private static final String ATTACHMENT_WIFI_ONLY="attachment_wifi_only";
    // TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E
    private String mAccount;
    private Message mMessage;
    private Attachment mAttachment;

    private final AttachmentCommandHandler mCommandHandler;
    private final AttachmentViewInterface mView;
    private final Context mContext;
    private final Handler mHandler;
    private FragmentManager mFragmentManager;
    private boolean mViewOnFinish;
    // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_S
    private boolean mHaveSaveToExteral = false;
    // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_S
    // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_S
    private boolean mInstallAttachmentViewd = false;
    // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_E

    private static final String LOG_TAG = LogTag.getLogTag();

    private static OptionHandler sOptionHandler = new OptionHandler();

    public AttachmentActionHandler(Context context, AttachmentViewInterface view) {
        mCommandHandler = new AttachmentCommandHandler(context);
        mView = view;
        mContext = context;
        mHandler = new Handler();
        mViewOnFinish = true;
    }

    public void initialize(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public void setMessage(Message message) {
        mMessage = message;
    }

    public void setAttachment(Attachment attachment) {
        mAttachment = attachment;
    }

    public void setViewOnFinish(boolean viewOnFinish) {
        mViewOnFinish = viewOnFinish;
    }

    public void showAttachment(int destination) {
        if (mView == null) {
            return;
        }

        // If the caller requested that this attachments be saved to the
        // external storage, we should
        // verify that the it was saved there.
        if (mAttachment.isPresentLocally()
                && (destination == AttachmentDestination.CACHE || mAttachment.destination == destination)) {
            mView.viewAttachment();
        } else {
          //TS: tao.gan 2015-8-27 EMAIL FEATURE_ID MOD_S
            //Don't show the dialog for the feature <Horizontal attachment>.
//            showDownloadingDialog();
            //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_S
            if (!AttachmentUtils.canDownloadAttachment(mContext, mAttachment)){
                UiUtilities.showConnectionAlertDialog(mFragmentManager);
                return ;
            }
            //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_E
            startDownloadingAttachment(destination);
          //TS: tao.gan 2015-8-27 EMAIL FEATURE_ID MOD_E
        }
    }

    /**
     * Start downloading the full size attachment set with
     * {@link #setAttachment(Attachment)} immediately.
     */
    public void startDownloadingAttachment(int destination) {
        startDownloadingAttachment(destination, UIProvider.AttachmentRendition.BEST, 0, false);
    }
    //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 MOD_S
    public void startDownloadingAttachment(
            int destination, int rendition, int additionalPriority, boolean delayDownload) {
        startDownloadingAttachment(
                mAttachment, destination, rendition, additionalPriority, delayDownload,null);
    }
    //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 MOD_E

    //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
    public void startDownloadingAttachment(int destination,final String Path)
    {
        startDownloadingAttachment(mAttachment,destination, UIProvider.AttachmentRendition.BEST, 0, false,Path);
    }
    //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_E
    private void startDownloadingAttachment(
            Attachment attachment, int destination, int rendition, int additionalPriority,
            boolean delayDownload,String path) {
        // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_S
        mInstallAttachmentViewd = false;
        // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_E
        //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 844039
        //TS: Gantao 2015-07-03 EMAIL BUGFIX_957636 MOD_S
        long attachmentId;
        try {
            attachmentId  = Long.parseLong(attachment.uri.getLastPathSegment());
        } catch (NumberFormatException ex) {
            //Sometimes the attachmentId can't be parsed to long ,just like a string,
            //It usually happened on .eml's inline attachment
            attachmentId = -1;
            LogUtils.e(LOG_TAG, "ivalid attachmentId occur!");
        }
        //TS: Gantao 2015-07-03 EMAIL BUGFIX_957636 MOD_E
        EmailContent.Attachment att = EmailContent.Attachment.restoreAttachmentWithId(mContext, attachmentId);
        if (!MimeType.isInstallable(mAttachment.getContentType())&&Utility.attachmentExists(mContext, att)
                && att.mUiState == UIProvider.AttachmentState.SAVED) {// TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 MOD
            //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 MOD_S
            if (PermissionUtil.checkAndRequestStoragePermission(mContext)) {
                //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_S
                //Note: add a path parameter for attachment save location.
                AttachmentUtilities.saveAttachmentToExternal(mContext, att, path);
                //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_E
                // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_S
                setHaveSaveToExternal(true);
                // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_E
            }
            //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 MOD_E
            return;
        }
        //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
        if (attachment.state == AttachmentState.SAVED
                && destination == AttachmentDestination.EXTERNAL
                &&MimeType.isInstallable(mAttachment.getContentType())) {// TS: Gantao 2015-06-24 EMAIL BUGFIX-1029228 MOD_S
            File savedFile = performAttachmentSave(attachment,path);
            if (savedFile != null) {
                // The attachment is saved successfully from cache.
                if(attachment.isInstallable())
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                        Utils.setIntentDataAndTypeAndNormalize(
                                intent, Uri.parse("file://"+savedFile.getAbsolutePath()), attachment.getContentType());

                    try {
                        mContext.startActivity(intent);
                        // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_S
                        mInstallAttachmentViewd = true;
                        // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_E
                    } catch (ActivityNotFoundException e) {
                        // couldn't find activity for View intent
                        LogUtils.e(LOG_TAG, e, "Couldn't find Activity for intent");
                    }
                }
                return;
            }
        }
        /* MODIFIED-BEGIN by yang.mei, 2016-03-22,BUG-1786309 */
        // TS: mei.yang 2016-03-22 EMAIL BUGFIX_1779492 ADD_S
        //always download installable att in cache
        if (mAttachment.isInstallable()){
            destination =  AttachmentDestination.CACHE;
        }
        // TS: mei.yang 2016-03-22 EMAIL BUGFIX_1779492 ADD_E
        /* MODIFIED-END by yang.mei,BUG-1786309 */
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_E
        final ContentValues params = new ContentValues(5);
        params.put(AttachmentColumns.STATE, AttachmentState.READYDOWNLOADING);
        params.put(AttachmentColumns.DESTINATION, destination);
        params.put(AttachmentContentValueKeys.RENDITION, rendition);
        params.put(AttachmentContentValueKeys.ADDITIONAL_PRIORITY, additionalPriority);
        params.put(AttachmentContentValueKeys.DELAY_DOWNLOAD, delayDownload);
        mCommandHandler.sendCommand(attachment.uri, params);
    }

    // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_S
    public void setHaveSaveToExternal(boolean haveSaveToExternal) {
        mHaveSaveToExteral = haveSaveToExternal;
    }

    public boolean getHaveSaveToExteranl(){
        return mHaveSaveToExteral;
    }
    // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_E

    public void cancelAttachment() {
        final ContentValues params = new ContentValues(1);
        params.put(AttachmentColumns.STATE, AttachmentState.NOT_SAVED);

        mCommandHandler.sendCommand(mAttachment.uri, params);
    }

    public void startRedownloadingAttachment(Attachment attachment) {
        final ContentValues params = new ContentValues(2);
        // TS: tao.gan 2015-05-29 EMAIL BUGFIX_1009013 MOD_S
        params.put(AttachmentColumns.STATE, AttachmentState.READYDOWNLOADING);
        // TS: zheng.zou 2016-03-22 EMAIL BUGFIX_1652810 MOD_S
        /* MODIFIED-BEGIN by yang.mei, 2016-03-22,BUG-1786309 */
        // TS: mei.yang 2016-03-22 EMAIL BUGFIX_1779492 MOD_S
        params.put(AttachmentColumns.DESTINATION, AttachmentDestination.CACHE);
        // TS: mei.yang 2016-03-22 EMAIL BUGFIX_1779492 MOD_E
        /* MODIFIED-END by yang.mei,BUG-1786309 */
        // TS: zheng.zou 2016-03-22 EMAIL BUGFIX_1652810 MOD_E
        // TS: tao.gan 2015-05-29 EMAIL BUGFIX_1009013 MOD_E

        mCommandHandler.sendCommand(attachment.uri, params);
    }

    /**
     * Displays a loading dialog to be used for downloading attachments.
     * Must be called on the UI thread.
     */
    public void showDownloadingDialog() {
        final FragmentTransaction ft = mFragmentManager.beginTransaction();
        final Fragment prev = mFragmentManager.findFragmentByTag(PROGRESS_FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

         // Create and show the dialog.
        final DialogFragment newFragment = AttachmentProgressDialogFragment.newInstance(
                mAttachment);
        newFragment.show(ft, PROGRESS_FRAGMENT_TAG);
    }
 // TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
    public void showAttachmentInfoDialog() {
        final FragmentTransaction ft = mFragmentManager.beginTransaction();
        final Fragment prev = mFragmentManager
                .findFragmentByTag(ATTACHMENT_WIFI_ONLY);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        final DialogFragment attachmentDialog = AttachmentInfoDialog
                .newInstance();
        attachmentDialog.show(ft, ATTACHMENT_WIFI_ONLY);
    }
 // TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E
    /**
     * Update progress-related views. Will also trigger a view intent if a progress dialog was
     * previously brought up (by tapping 'View') and the download has now finished.
     */
    public void updateStatus(boolean loaderResult) {
        if (mView == null) {
            return;
        }

        final boolean showProgress = mAttachment.shouldShowProgress();

        final AttachmentProgressDialogFragment dialog = (AttachmentProgressDialogFragment)
                mFragmentManager.findFragmentByTag(PROGRESS_FRAGMENT_TAG);
        if (dialog != null && dialog.isShowingDialogForAttachment(mAttachment)) {
            dialog.setProgress(mAttachment.downloadedSize);

            // We don't want the progress bar to switch back to indeterminate mode after
            // have been in determinate progress mode.
            final boolean indeterminate = !showProgress && dialog.isIndeterminate();
            dialog.setIndeterminate(indeterminate);

            if (loaderResult && mAttachment.isDownloadFinishedOrFailed()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }

            // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 MOD_S
            if (mAttachment.state == AttachmentState.SAVED && mViewOnFinish
                    && !installAttachmentViewed(mAttachment)) {
                // TS: lin-zhou 2015-09-09 EMAIL BUGFIX-1079571 MOD_S
                //Note: not need to view attachment if mime type is x-vcard.
                if (!MimeType.isXvcardType(mAttachment.getContentType())) {
                    mView.viewAttachment();
                }
                // TS: lin-zhou 2015-09-09 EMAIL BUGFIX-1079571 MOD_E
            }
            // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 MOD_E
        } else {
            mView.updateProgress(showProgress);
        }

        // Call on update status for the view so that it can do some specific things.
        mView.onUpdateStatus();
    }

    public boolean isProgressDialogVisible() {
        final Fragment dialog = mFragmentManager.findFragmentByTag(PROGRESS_FRAGMENT_TAG);
        return dialog != null && dialog.isVisible();
    }

    public void shareAttachment() {
        if (mAttachment.contentUri == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 MOD_S
        Uri uri = Utils.normalizeUri(mAttachment.contentUri);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if(mAttachment.realUri!=null){
            uri=Utils.normalizeUri(mAttachment.realUri);
        }else{
            uri=Utils.normalizeUri(mAttachment.contentUri);
        }
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 MODD_E
        intent.setType(Utils.normalizeMimeType(mAttachment.getContentType()));

        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // couldn't find activity for SEND intent
            LogUtils.e(LOG_TAG, "Couldn't find Activity for intent", e);
        }
    }

    public void shareAttachments(ArrayList<Parcelable> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        intent.setType("image/*");
        intent.putParcelableArrayListExtra(
                Intent.EXTRA_STREAM, uris);

        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // couldn't find activity for SEND_MULTIPLE intent
            LogUtils.e(LOG_TAG, "Couldn't find Activity for intent", e);
        }
    }

    public static void setOptionHandler(OptionHandler handler) {
        sOptionHandler = handler;
    }

    public boolean shouldShowExtraOption1(String mimeType) {
        return (sOptionHandler != null) && sOptionHandler.shouldShowExtraOption1(mimeType);
    }

    public void handleOption1() {
        if (sOptionHandler == null) {
            return;
        }
        sOptionHandler.handleOption1(mContext, mAccount, mMessage, mAttachment, mFragmentManager);
    }

    /**
     * A default, no-op option class. Override this and set it globally with
     * {@link AttachmentActionHandler#setOptionHandler(OptionHandler)}.<br>
     * <br>
     * Subclasses of this type will live pretty much forever, so really, really try to avoid
     * keeping any state as member variables in them.
     */
    public static class OptionHandler {

        public boolean shouldShowExtraOption1(String mimeType) {
            return false;
        }

        public void handleOption1(Context context, String account, Message message,
                Attachment attachment, FragmentManager fm) {
            // no-op
        }
    }
    //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
    private File createUniqueFile(File directory, String filename) throws IOException {
        File file = new File(directory, filename);
        if (file.createNewFile()) {
            return file;
        }
        // Get the extension of the file, if any.
        int index = filename.lastIndexOf('.');
        String format;
        if (index != -1) {
            String name = filename.substring(0, index);
            String extension = filename.substring(index);
            format = name + "-%d" + extension;
        } else {
            format = filename + "-%d";
        }

        for (int i = 2; i < Integer.MAX_VALUE; i++) {
            file = new File(directory, String.format(format, i));
            if (file.createNewFile()) {
                return file;
            }
        }
        return null;
    }
    private File performAttachmentSave(final Attachment attachment,final String Path) {
        Uri attachmentUri = attachment.uri;
        File file = null;
        int size = attachment.size;
        final ContentValues params = new ContentValues();
        long attachmentId=0;
        try {
            attachmentId = Long.parseLong(attachment.uri.getLastPathSegment());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        com.tct.emailcommon.provider.EmailContent.Attachment contentAttachment =
                com.tct.emailcommon.provider.EmailContent.Attachment.restoreAttachmentWithId(mContext, attachmentId);
        // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 MOD_S
        InputStream in = null;
        OutputStream out = null;
        try {
            File downloads;
            if(Path!=null)
                downloads = new File(Path);
            else
            // TS: zheng.zou 2016-03-30 EMAIL BUGFIX-1872449 MOD_S
//                downloads = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS);
            {
                downloads = mContext.getExternalCacheDir();
            }
            downloads.mkdirs();
            long attId = 0;
            try {
                attId = Long.parseLong(attachment.uri.getLastPathSegment());
            } catch (Exception e) {
                LogUtils.i(LOG_TAG, "get att id error when performAttachmentSave");
            }
            String apkFilePath = downloads.getAbsolutePath()+"/"+ attId + "_" +attachment.getName();
            // TS: zheng.zou 2016-03-30 EMAIL BUGFIX-1872449 MOD_E
            File tempApkFile = new File(apkFilePath);
            if (!tempApkFile.exists()) {
                file = createUniqueFile(downloads, attachment.getName());
                Uri contentUri = attachment.contentUri;
                in = mContext.getContentResolver().openInputStream(contentUri);
                out = new FileOutputStream(file);
                size = IOUtils.copy(in, out);
                out.flush();
                String absolutePath = file.getAbsolutePath();
                MediaScannerConnection.scanFile(mContext, new String[] {absolutePath},
                        null, null);
                // TS: zheng.zou 2016-03-30 EMAIL BUGFIX-1872449 DEL_S
                //note: no need show notification
//                DownloadManager dm =
//                        (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//                long id = dm.addCompletedDownload(attachment.getName(), attachment.getName(),
//                        false /* do not use media scanner */,
//                       // attachment.getContentType(), absolutePath, attachment.downloadedSize,
//                        attachment.getContentType(), absolutePath, size,
//                        true /* show notification */);
                // TS: zheng.zou 2016-03-30 EMAIL BUGFIX-1872449 DEL_S
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.SIZE, size);
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.UI_STATE, UIProvider.AttachmentState.SAVED);
                // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_S
                //Note:we have saved the attachment to sd card,so should update the attachment destination external and real uri
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.UI_DESTINATION, UIProvider.AttachmentDestination.EXTERNAL);
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.REAL_URI, "file://" + absolutePath);// TS: zheng.zou 2016-03-30 EMAIL BUGFIX-1872449 MOD
                // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_E
                if(contentAttachment != null) {
                    contentAttachment.update(mContext,params);
                }
            } else {
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.SIZE, size);
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.UI_STATE, UIProvider.AttachmentState.SAVED);
                // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_S
                //Note:we have saved the attachment to sd card,so should update the attachment destination external and real uri
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.UI_DESTINATION, UIProvider.AttachmentDestination.EXTERNAL);
                params.put(com.tct.emailcommon.provider.EmailContent.AttachmentColumns.REAL_URI, "file://" + apkFilePath);
                // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_E
                if(contentAttachment != null) {
                    contentAttachment.update(mContext,params);
                }
                return tempApkFile;
            }
        } catch (IOException ioe) {
            Log.e("Email", "Attachment download fail:"+ioe);
            // Ignore. Callers will handle it from the return code.
        }
        catch (IllegalArgumentException e) {
            Log.e("Email", "DownloadManager is disable:" + e);
            // Ignore. Callers will handle it from the return code.
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtils.e(LogUtils.TAG, "IOException while close the stream");
            }
         // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 MOD_E
        }
        return file;
    }
    //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_E

    // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_S
    private boolean installAttachmentViewed(Attachment attachment) {
        return attachment.isInstallable() && mInstallAttachmentViewd;
    }
    // TS: Tao.Gan 2015-06-02 EMAIL BUGFIX-996126 ADD_E

}
