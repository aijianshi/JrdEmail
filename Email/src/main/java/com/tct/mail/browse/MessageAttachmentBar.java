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
 ===========================================================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== =============================================================
 *BUGFIX-847926  2014/11/25   zhaotianyong    [Android5.0][Email]Audio file name is wrong when playing it
 *BUGFIX-869494  2014/12/31   zhaotianyong    [Android5.0][Email][UE] Show attachments on top screen.
 *BUGFIX-912160  2015/01/27   zhaotianyong    [REG][Email]Flash back when tap attachment in mail from Postmaster@163.com
 *BUGFIX-916938  2015/01/29   zhaotianyong    [Android5.0][Exchange]All attachments can't be opened after being saved
 *BUGFIX-928905  2015/02/14   zhaotianyong    [Email]Flash back when open .eml file from Microsoft Outlook
 *BUGFIX-935474  2015/02/27   chenyanhua      Error when user receives an email with attachtment bigger than 4Mb.
 *BUGFIX-936728  2015/3/3     zhonghua.tuo    [Email]Can not open vcs and apk file in draft box and outbox
 *BUGFIX-945771  2015/03/13   zhaotianyong    [Email]Can not open vcs and apk file in draft box and outbox
 *BUGFIX-989483  2015/05/05   zhaotianyong    [Email] ZIP file should be available to download.
 *BUGFIX-996023  2015/5/11    yanhua.chen     [Email]Cannot download attchment more than 5MB when not connect to WIFI
 *BUGFIX-1006010  2015/5/19   zhaotianyong    [Email](new) non-supported attachment handling
 *BUGFIX-998526  2015/05/22   zhaotianyong    [Email]Email attachment will overlap the email body during downloading remaining
 *BUGFIX-1009030  2015/06/03  Gantao          [Android5.0][Email]Attachment cannot fetch when download again.(may be related to 1013191)
 *BUGFIX-1029228  2015/06/24  Gantao          [Android L][Email][Download]Can not Download the Email attchment
 *FEATURE-ID     2015/08/27   tao.gan         Horizontal attachment
 *BUGFIX-1103647  2015/11/13  ZhangChao      [Android5.0][Email] Should show no network warning when fetching a file without network connection.
 *TASK-869664    2015/11/25   zheng.zou      [Email]Android M Permission Upgrade
 *TASK-1477377   2016/01/20   jian.xu         Save attchment to user select location
 *BUGFIX-1541781 2016/02/20   jian.xu         [Email]Can not open the .txt files in Sent folder with POP/IMAP email accout.
 *BUGFIX-1740140 2016/03/16   junwei-xu       [Email]Can't import the vcf  file when tap vcf shared from Contacts in draft.
 *BUGFIX-1541781 2016/03/17   xiangnan.zhou   [Email]Can not open the .txt files in Sent folder with POP/IMAP email accout.
 *BUGFIX-1863570 2016/04/07   zheng.zou       [Email]It can't select save place after clicking save to in attachment menu.
 *BUGFIX-1779964 2016/04/14   zheng.zou       [Email][v5.2.10.3.0215.0_0307]it will flash or black screen when view picture from Draft
 ==========================================================================================================
 */

package com.tct.mail.browse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.text.BidiFormatter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.AsyncTask;
import com.tct.emailcommon.utility.AttachmentUtilities;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.UIProvider.AttachmentDestination;
import com.tct.mail.providers.UIProvider.AttachmentState;
import com.tct.mail.ui.AccountFeedbackActivity;
import com.tct.mail.ui.AttachmentTile;
import com.tct.mail.ui.ThumbnailLoadTask;
import com.tct.mail.utils.AttachmentUtils;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.MimeType;
import com.tct.mail.utils.PLFUtils;
import com.tct.permission.BaseActivity;
import com.tct.permission.PermissionUtil;
import com.tct.mail.utils.UiUtilities;
import com.tct.mail.utils.Utils;
/**
 * View for a single attachment in conversation view. Shows download status and allows launching
 * intents to act on an attachment.
 *
 */
public class MessageAttachmentBar extends FrameLayout implements OnClickListener,
        OnMenuItemClickListener, AttachmentViewInterface ,PermissionUtil.OnPermissionResult{  //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 MOD

    private Attachment mAttachment;
    private TextView mTitle;
    private TextView mSize;
    private String mAttachmentSizeText;
    private String mDisplayType;
    private ProgressBar mProgress;
    private ImageButton mCancelButton;
    private PopupMenu mPopup;
    private ImageView mOverflowButton;
    //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
    private ImageView mThumbnail;
    private TextView mType;
    private ImageView mDownloadIcon;
    private String mAttachmentDownloadedSize;
    //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E
    //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
    Context mContext;
    //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E
    private final AttachmentActionHandler mActionHandler;
    private boolean mSaveClicked;
    //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 ADD_S
    //Note: the attachment save location, it defined by user.
    private String mSavePath;
    //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 ADD_E
    private Account mAccount;
    //TS: Gantao 2015-06-03 EMAIL BUGFIX_1009030(1013191) ADD_S
    private boolean mThunmbnailDefault;
    //TS: Gantao 2015-06-03 EMAIL BUGFIX_1009030(1013191) ADD_E
    //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_S
    private FragmentManager mFragmentManager;
    //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_E

    private final Runnable mUpdateRunnable = new Runnable() {
            @Override
        public void run() {
            updateActionsInternal();
        }
    };

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
    private final Runnable mActionRunnable = new Runnable() {
        public void run() {
            updateOverflow();
        }
    };
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E

    private static final String LOG_TAG = LogTag.getLogTag();

    /**
     * Boolean used to tell whether extra option 1 should always be hidden.
     * Currently makes sure that there is no conversation because that state
     * means that we're in the EML viewer.
     */
    private boolean mHideExtraOptionOne;


    public MessageAttachmentBar(Context context) {
        this(context, null);
        //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
        mContext=context;
        //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E
    }

    public MessageAttachmentBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
        mContext=context;
        //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E
        mActionHandler = new AttachmentActionHandler(context, this);
    }

    public void initialize(FragmentManager fragmentManager) {
        //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_S
        mFragmentManager=fragmentManager;
        //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_E
        mActionHandler.initialize(fragmentManager);
    }

    public static MessageAttachmentBar inflate(LayoutInflater inflater, ViewGroup parent) {
        MessageAttachmentBar view = (MessageAttachmentBar) inflater.inflate(
                R.layout.conversation_message_attachment_bar, parent, false);
        return view;
    }

    /**
     * Render or update an attachment's view. This happens immediately upon instantiation, and
     * repeatedly as status updates stream in, so only properties with new or changed values will
     * cause sub-views to update.
     */
    public void render(Attachment attachment, Account account, ConversationMessage message,
            boolean loaderResult, BidiFormatter bidiFormatter) {
        // get account uri for potential eml viewer usage
        mAccount = account;
        final Attachment prevAttachment = mAttachment;
        mAttachment = attachment;
        if (mAccount != null) {
            mActionHandler.setAccount(mAccount.getEmailAddress());
        }
        mActionHandler.setMessage(message);
        mActionHandler.setAttachment(mAttachment);
        mHideExtraOptionOne = message.getConversation() == null;

        // reset mSaveClicked if we are not currently downloading
        // So if the download fails or the download completes, we stop
        // showing progress, etc
        // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 DEL_S
     //    mSaveClicked = !attachment.isDownloading() ? false : mSaveClicked;
        // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 DEL_S

        LogUtils.d(LOG_TAG, "got attachment list row: name=%s state/dest=%d/%d dled=%d" +
                " contentUri=%s MIME=%s flags=%d", attachment.getName(), attachment.state,
                attachment.destination, attachment.downloadedSize, attachment.contentUri,
                attachment.getContentType(), attachment.flags);

        final String attachmentName = attachment.getName();
        if ((attachment.flags & Attachment.FLAG_DUMMY_ATTACHMENT) != 0) {
            mTitle.setText(R.string.load_attachment);
        } else if (prevAttachment == null
                || !TextUtils.equals(attachmentName, prevAttachment.getName())) {
            mTitle.setText(attachmentName);
        }

        if (prevAttachment == null || attachment.size != prevAttachment.size) {
            mAttachmentSizeText = bidiFormatter.unicodeWrap(
                    AttachmentUtils.convertToHumanReadableSize(getContext(), attachment.size));
            mDisplayType = bidiFormatter.unicodeWrap(
                    AttachmentUtils.getDisplayType(getContext(), attachment));
            updateSubtitleText();
        }

        //Unicode the downloaded size.
        if (mAttachment.isDownloading()) {
            mAttachmentDownloadedSize = bidiFormatter.unicodeWrap(
                    AttachmentUtils.convertToHumanReadableSize(getContext(), mAttachment.downloadedSize));
        }

        updateActions(loaderResult, mAttachment.isDownloading());
        mActionHandler.updateStatus(loaderResult);

      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
        //Load the image thunmbnail and show it.
        if (AttachmentTile.isTiledAttachment(mAttachment)) {
            if (loaderResult && mAttachment.isPresentLocally()) {
                ThumbnailLoadTask.setupThumbnailPreview(mThumbnail, attachment, this);
            } else {
                mThumbnail.setImageResource(R.drawable.ic_array);
                setThumbnailDefault(true);
                setTitleVisibility(VISIBLE);
            }
        }
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
        mThumbnail = (ImageView) findViewById(R.id.attachment_thunmbnail);
        mType = (TextView) findViewById(R.id.attachment_type);
        mDownloadIcon = (ImageView) findViewById(R.id.attachment_download_icon);
        mTitle = (TextView) findViewById(R.id.attachment_title);
        mSize = (TextView) findViewById(R.id.attachment_size);
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
        mProgress = (ProgressBar) findViewById(R.id.attachment_progress);
        mOverflowButton = (ImageView) findViewById(R.id.overflow);
        mCancelButton = (ImageButton) findViewById(R.id.cancel_attachment);

        setOnClickListener(this);
        mOverflowButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onClick(v.getId(), v);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mPopup.dismiss();
        return onClick(item.getItemId(), null);
    }

    private boolean onClick(final int res, final View v) {
        if (res == R.id.preview_attachment) {
            previewAttachment();
        } else if (res == R.id.save_attachment) {
            if (mAttachment.canSave()) {
                //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_S
                //Note: provide an activity for user to select the location to save attachment.
                /*
                // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 MOD_S
                // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029228 MOD_S
                String emlAttAuth = mContext.getResources().getString(R.string.eml_attachment_provider);
                if (mAttachment.contentUri!=null && mAttachment.contentUri.getAuthority().equals(emlAttAuth)) {
                    if (PermissionUtil.checkAndRequestStoragePermission(mContext)) {
                        mActionHandler.startDownloadingAttachment(AttachmentDestination.EXTERNAL);
                        mSaveClicked = true;
                    } else {    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
                        if (mContext instanceof BaseActivity){
                            ((BaseActivity) mContext).registerPermissionResultListener(this);
                        }
                    }  //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E

                } else {
                    if (PermissionUtil.checkAndRequestStoragePermission(mContext)) {
                        mActionHandler.startDownloadingAttachment(AttachmentDestination.CACHE);
                        mSaveClicked = true;
                    } else {   //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
                        if (mContext instanceof BaseActivity){
                            ((BaseActivity) mContext).registerPermissionResultListener(this);
                        }
                    }        //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E
                }
                // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029228 MOD_E
                // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 MOD_E
                */
                boolean saveWay = PLFUtils.getBoolean(getContext(), "feature_email_saveAttachmentLocationSelectable_on");
                if (saveWay) {
                    saveAttachmentWithLocationSelectable();
                } else {
                    saveAttachmentAutomatic();
                }
                //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_E


                Analytics.getInstance().sendEvent(
                        "save_attachment", Utils.normalizeMimeType(mAttachment.getContentType()),
                        "attachment_bar", mAttachment.size);
            }
        } else if (res == R.id.download_again) {
            if (mAttachment.isPresentLocally()) {
              //Don't show the dialog for the feature <Horizontal attachment>.
//                mActionHandler.showDownloadingDialog();
                //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_S
                if (!AttachmentUtils.canDownloadAttachment(getContext(),mAttachment)){
                    UiUtilities.showConnectionAlertDialog(mFragmentManager);
                    return true;
                }
                //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
                if (mAttachment != null && mAttachment.destination == AttachmentDestination.EXTERNAL) {
                    if (!PermissionUtil.checkAndRequestPermissionForResult(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            PermissionUtil.REQ_CODE_PERMISSION_REDOWNLOAD_ATTACHMENT)) {
                        if (mContext instanceof BaseActivity) {
                            ((BaseActivity) mContext).registerPermissionResultListener(this);
                        }
                        return true;
                    }
                }
                //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
                //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_E
                mActionHandler.startRedownloadingAttachment(mAttachment);

                Analytics.getInstance().sendEvent("redownload_attachment",
                        Utils.normalizeMimeType(mAttachment.getContentType()), "attachment_bar",
                        mAttachment.size);
            }
        } else if (res == R.id.cancel_attachment) {
            mActionHandler.cancelAttachment();
            mSaveClicked = false;

            Analytics.getInstance().sendEvent(
                    "cancel_attachment", Utils.normalizeMimeType(mAttachment.getContentType()),
                    "attachment_bar", mAttachment.size);
        } else if (res == R.id.attachment_extra_option1) {
            mActionHandler.handleOption1();
        } else if (res == R.id.overflow) {
            // If no overflow items are visible, just bail out.
            // We shouldn't be able to get here anyhow since the overflow
            // button should be hidden.
            if (shouldShowOverflow()) {
                if (mPopup == null) {
                    mPopup = new PopupMenu(getContext(), v);
                    mPopup.getMenuInflater().inflate(R.menu.message_footer_overflow_menu,
                            mPopup.getMenu());
                    mPopup.setOnMenuItemClickListener(this);
                }

                final Menu menu = mPopup.getMenu();
                //TS: junwei-xu 2016-04-07 EMAIL BUGFIX-1863570 ADD_S
                //Note: should change the title for save attachment menu dynamically.
                boolean saveTo = PLFUtils.getBoolean(getContext(),
                        "feature_email_saveAttachmentLocationSelectable_on");
                if (saveTo) {
                    menu.findItem(R.id.save_attachment).setTitle(R.string.save_to_attachment);
                } else {
                    menu.findItem(R.id.save_attachment).setTitle(R.string.save_attachment);
                }
                //TS: junwei-xu 2016-04-07 EMAIL BUGFIX-1863570 ADD_E
                menu.findItem(R.id.preview_attachment).setVisible(shouldShowPreview());
                menu.findItem(R.id.save_attachment).setVisible(shouldShowSave());
                menu.findItem(R.id.download_again).setVisible(shouldShowDownloadAgain());
                menu.findItem(R.id.attachment_extra_option1).setVisible(shouldShowExtraOption1());

                mPopup.show();
            }
        } else {
            // Handles clicking the attachment
            // in any area that is not the overflow
            // button or cancel button or one of the
            // overflow items.
            final String mime = Utils.normalizeMimeType(mAttachment.getContentType());
            final String action;

            if ((mAttachment.flags & Attachment.FLAG_DUMMY_ATTACHMENT) != 0) {
                // This is a dummy. We need to download it, but not attempt to open or preview.
              //Don't show the dialog for the feature <Horizontal attachment>.
//                mActionHandler.showDownloadingDialog();
                mActionHandler.setViewOnFinish(false);
                mActionHandler.startDownloadingAttachment(AttachmentDestination.CACHE);

                action = null;
            }
            // If we can install, install.
            else if (MimeType.isInstallable(mAttachment.getContentType())) {
                // Save to external because the package manager only handles
                // file:// uris not content:// uris. We do the same
                // workaround in
                // UiProvider#getUiAttachmentsCursorForUIAttachments()
                //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
                if (!(mAttachment.isPresentLocally() && mAttachment.destination == AttachmentDestination.EXTERNAL)
                        && !PermissionUtil.checkAndRequestPermissionForResult(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionUtil.REQ_CODE_PERMISSION_VIEW_ATTACHMENT)){
                    if (mContext instanceof BaseActivity) {
                        ((BaseActivity) mContext).registerPermissionResultListener(this);
                    }
                   return true;
                }
                //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E

                mActionHandler.showAttachment(AttachmentDestination.EXTERNAL);

                action = "attachment_bar_install";
            }
            // If we can view or play with an on-device app,
            // view or play.
            else if (MimeType.isViewable(
                    getContext(), mAttachment.contentUri, mAttachment.getContentType())) {
                mActionHandler.showAttachment(AttachmentDestination.CACHE);

                action = "attachment_bar";
            }
            // TS: zhaotianyong 2015-05-19 EMAIL BUGFIX-1006010 DEL
            // If we can only preview the attachment, preview.
            else if (mAttachment.canPreview()) {
                previewAttachment();

                action = null;
            }
            // TS: zhaotianyong 2015-05-19 EMAIL BUGFIX-1006010 ADD_S
            // New requirement:even if there is no app can open the attachement,
            // we can also download it.
            else if (!mAttachment.isPresentLocally()) {
              //Don't show the dialog for the feature <Horizontal attachment>.
//                mActionHandler.showDownloadingDialog();
                //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_S
                if (!AttachmentUtils.canDownloadAttachment(getContext(),mAttachment)){
                    UiUtilities.showConnectionAlertDialog(mFragmentManager);
                    return true;
                }
                //TS:zhangchao 2015-11-13 EMAIL BUGFIX_1103647 ADD_E
                mActionHandler.startDownloadingAttachment(AttachmentDestination.CACHE);

                action = "attachment_bar";
            }
            // TS: zhaotianyong 2015-05-19 EMAIL BUGFIX-1006010 DEL_E
            // Otherwise, if we cannot do anything, show the info dialog.
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                int dialogMessage = R.string.no_application_found;
                builder.setTitle(R.string.more_info_attachment)
                       .setMessage(dialogMessage)
                       .show();

                action = "attachment_bar_no_viewer";
            }

            if (action != null) {
                Analytics.getInstance()
                        .sendEvent("view_attachment", mime, action, mAttachment.size);
            }
        }

        return true;
    }

    private boolean shouldShowPreview() {
        // state could be anything
        return mAttachment.canPreview();
    }

    private boolean shouldShowSave() {
        return mAttachment.canSave() && !mSaveClicked;
    }

    private boolean shouldShowDownloadAgain() {
        // implies state == SAVED || state == FAILED
        // and the attachment supports re-download
        return mAttachment.supportsDownloadAgain() && mAttachment.isDownloadFinishedOrFailed();
    }

    private boolean shouldShowExtraOption1() {
        return !mHideExtraOptionOne &&
                mActionHandler.shouldShowExtraOption1(mAttachment.getContentType());
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    private boolean shouldShowOverflow() {
        return (shouldShowPreview() || shouldShowSave() || shouldShowDownloadAgain() ||
                shouldShowExtraOption1()) && mAttachment.isPresentLocally() && !shouldShowCancel();
    }

    private boolean shouldShowCancel() {
        // TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD_S
        //we don't show cancel while has started download.
//        return mAttachment.isDownloading();
        return mAttachment.isReadyDownloading();
        // TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD_E
    }

    private boolean shouldShowDownloadIcon() {
        // TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD_S
        return !mAttachment.isPresentLocally() && !shouldShowCancel() && !mAttachment.isDownloading();
        // TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD_E
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

    //TS: junwei-xu 2016-03-16 EMAIL BUGFIX-1740140 ADD_S
    private void importXvcard(Attachment attachment, final String type) {
        new AsyncTask<Attachment, Void, Uri>() {

            @Override
            protected Uri doInBackground(Attachment... params) {
                final Attachment attach = params[0];
                final String[] filenames = mContext.fileList();
                for (String file : filenames) {
                    if (file.endsWith(".vcf")) {
                            mContext.deleteFile(file);
                        }
                }
                try {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = mContext.getContentResolver().openInputStream(attach.contentUri);
                        out = mContext.openFileOutput(attach.getName(), Context.MODE_WORLD_READABLE);
                        byte[] buf = new byte[8096];
                        int seg = 0;
                        while ((seg = in.read(buf)) != -1) {
                                out.write(buf, 0, seg);
                            }
                    } finally {
                        if (in != null) {
                                in.close();
                            }
                        if (out != null) {
                                out.close();
                            }
                    }
                } catch (FileNotFoundException e) {
                    LogUtils.e(LOG_TAG, "importVCard, file not found " + attach + ", exception ", e);
                } catch (IOException e) {
                    LogUtils.e(LOG_TAG, "importVCard, ioexception " + attach + ", exception ", e);
                } catch (Exception e) {
                    LogUtils.e(LOG_TAG, "importVCard, unknown errror ", e);
                }
                final File tempVCard = mContext.getFileStreamPath(attach.getName());
                if (!tempVCard.exists() || tempVCard.length() <= 0) {
                        LogUtils.e(LOG_TAG, "importVCard, file is not exists or empty " + tempVCard);
                        return null;
                }
                return Uri.fromFile(tempVCard);
            }

            @Override
            protected void onPostExecute(Uri uri) {
                super.onPostExecute(uri);
                if (uri == null) return;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                Utils.setIntentDataAndTypeAndNormalize(intent, uri, type);
                try {
                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // couldn't find activity for View intent
                    LogUtils.e(LOG_TAG, e, "Couldn't find Activity for intent");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    int dialogMessage = R.string.no_application_found;
                    builder.setTitle(R.string.more_info_attachment)
                            .setMessage(dialogMessage).show();
                }
            }
        }.execute(attachment);
    }
    //TS: junwei-xu 2016-03-16 EMAIL BUGFIX-1740140 ADD_E

    @Override
    public void viewAttachment() {
        if (mAttachment.contentUri == null) {
            LogUtils.e(LOG_TAG, "viewAttachment with null content uri");
            return;
        }

        //TS: junwei-xu 2016-03-16 EMAIL BUGFIX-1740140 ADD_S
        if (MimeType.isXvcardType(mAttachment.getContentType())) {
            importXvcard(mAttachment, mAttachment.getContentType());
            return;
        }
        //TS: junwei-xu 2016-03-16 EMAIL BUGFIX-1740140 ADD_E
        //TS: zheng.zou 2016-04-14 EMAIL BUGFIX-1779964 MOD_S
        //NOTE:Here for no screen flash when view image with Gallery,gallery call us must remove
        //any Intent TASK TAG...(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //TS: zheng.zou 2016-04-14 EMAIL BUGFIX-1779964 MOD_E
        final String contentType = mAttachment.getContentType();
        // TS: zhaotianyong 2014-11-25 EMAIL BUGFIX-847926 ADD_S
        String str = mAttachment.contentUri.toString();
        // TS: zhaotianyong 2015-01-27 EMAIL BUGFIX-912160 MOD_S
        if( str.endsWith("RAW") && mAttachment.getName()!=null)
        // TS: zhaotianyong 2015-01-27 EMAIL BUGFIX-912160 MOD_E
        {
         // TS: zhaotianyong 2015-02-14 EMAIL BUGFIX-928905 MOD_S
            str = str.replace("RAW", "RAW/" + mAttachment.getName());
         // TS: zhaotianyong 2015-02-14 EMAIL BUGFIX-928905 MOD_E
        }
        // TS: zhaotianyong 2014-11-25 EMAIL BUGFIX-847926 ADD_E
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 MOD_S
        //TS: xiangnan.zhou 2016-03-17 EMAIL BUGFIX_1785573 MOD_S
        //NOTE:remove NONE-Text/HTML judge,here if file exist in local,just use file:///,it means
        //use real uri
        if (fileExist()) {//TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 MOD
            Utils.setIntentDataAndTypeAndNormalize(
                    intent, mAttachment.realUri, contentType);
        } else {
            Utils.setIntentDataAndTypeAndNormalize(
                intent, Uri.parse(str )/*mAttachment.contentUri*/, contentType);
        }
        //TS: xiangnan.zhou 2016-03-17 EMAIL BUGFIX_1785573 MOD_S
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 MOD_E
        // For EML files, we want to open our dedicated
        // viewer rather than let any activity open it.
        if (MimeType.isEmlMimeType(contentType)) {
            intent.setPackage(getContext().getPackageName());
            intent.putExtra(AccountFeedbackActivity.EXTRA_ACCOUNT_URI,
                    mAccount != null ? mAccount.uri : null);
        }

        try {
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // couldn't find activity for View intent
            LogUtils.e(LOG_TAG, e, "Couldn't find Activity for intent");
            // TS: zhaotianyong 2015-05-19 EMAIL BUGFIX-1006010 MOD_S
            // TS: zhaotianyong 2015-05-05 EMAIL BUGFIX-989483 ADD_S
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            int dialogMessage = R.string.no_application_found;
            builder.setTitle(R.string.more_info_attachment)
                    .setMessage(dialogMessage).show();
            // TS: zhaotianyong 2015-05-05 EMAIL BUGFIX-989483 ADD_E
            // TS: zhaotianyong 2015-05-19 EMAIL BUGFIX-1006010 MOD_E
        }
    }

    //TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 ADD_S
    //TS: xiangnan.zhou 2016-03-17 EMAIL BUGFIX_1785573 MOD_S
    private boolean fileExist() {
        if (mAttachment.realUri == null) {
            return false;
        }
        String patch = mAttachment.realUri.getPath();
        if (TextUtils.isEmpty(patch)) {
            return false;
        }
        try {
            File file = new File(patch);
            if (!file.exists()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            LogUtils.e(LOG_TAG, e, "file not exits");
            return false;
        }
    }
    //TS: xiangnan.zhou 2016-03-17 EMAIL BUGFIX_1785573 MOD_S
    //TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 ADD_E

    private void previewAttachment() {
        if (mAttachment.canPreview()) {
            final Intent previewIntent =
                    new Intent(Intent.ACTION_VIEW, mAttachment.previewIntentUri);
            getContext().startActivity(previewIntent);

            Analytics.getInstance().sendEvent(
                    "preview_attachment", Utils.normalizeMimeType(mAttachment.getContentType()),
                    null, mAttachment.size);
        }
    }

    private static void setButtonVisible(View button, boolean visible) {
        button.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Update all actions based on current downloading state.
     */
    private void updateActions(boolean loaderResult, boolean isDownloading) {
        removeCallbacks(mUpdateRunnable);
        post(mUpdateRunnable);
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
        //Only loaderResult that we update the over flow button's visibility.
        if (loaderResult || isDownloading) {
            removeCallbacks(mActionRunnable);
            post(mActionRunnable);
        }
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E
    }

    private void updateActionsInternal() {
        // If the progress dialog is visible, skip any of the updating
        if (mActionHandler.isProgressDialogVisible()) {
            return;
        }

        // To avoid visibility state transition bugs, every button's visibility should be touched
        // once by this routine.
        setButtonVisible(mCancelButton, shouldShowCancel());
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
        setButtonVisible(mDownloadIcon, shouldShowDownloadIcon());
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
    //set the visibility of the onverflow button.
    private void updateOverflow() {
        setButtonVisible(mOverflowButton, shouldShowOverflow());
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E

    @Override
    public void onUpdateStatus() {
        updateSubtitleText();
    }

    @Override
    public void updateProgress(boolean showProgress) {
        if (mAttachment.isDownloading()) {
            mProgress.setMax(mAttachment.size);
            mProgress.setProgress(mAttachment.downloadedSize);
            mProgress.setIndeterminate(!showProgress);
            mProgress.setVisibility(VISIBLE);
        } else {
         // TS: zhaotianyong 2015-01-29 EMAIL oBUGFIX-916938 ADD_S
            if (mAttachment.isPresentLocally() && !mActionHandler.getHaveSaveToExteranl()&&mSaveClicked
                    && mAttachment.destination == AttachmentDestination.CACHE){// TS: Gantao 2015-06-24 EMAIL BUGFIX-1029228 MOD
                long attachmentId = Long.parseLong(mAttachment.uri.getLastPathSegment());
                EmailContent.Attachment att = EmailContent.Attachment.restoreAttachmentWithId(getContext(), attachmentId);
                //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 MOD_S
                if (PermissionUtil.checkAndRequestStoragePermission(mContext)) {
                    //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_S
                    //Note: add a path parameter for attachment save location.
                    AttachmentUtilities.saveAttachmentToExternal(getContext(), att, mSavePath);
                    //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_E
                    mActionHandler.setHaveSaveToExternal(true);
                }
                //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 MOD_E
            }
         // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 ADD_E
            mProgress.setVisibility(INVISIBLE);
        }
    }
    //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_S
    @Override
    public boolean attachmentCanDwonload() {
        //TS: yanhua.chen 2015-5-11 EMAIL BUGFIX_996023 DEL_S
        //if (mAttachment.size > AttachmentUtilities.MAX_ATTACHMENT_DOWNLOAD_SIZE) {
        //    int networkType = EmailConnectivityManager
        //            .getActiveNetworkType(mContext);
        //    if (networkType != ConnectivityManager.TYPE_WIFI) {
        //        return false;
        //    }
        //}
        //TS: yanhua.chen 2015-5-11 EMAIL BUGFIX_996023 DEL_E
        return true;
    }
    //TS: chenyanhua 2015-02-27 EMAIL BUGFIX-935474 ADD_E

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    private void updateSubtitleText() {
        // TODO: make this a formatted resource when we have a UX design.
        // not worth translation right now.
        if (mDisplayType != null) {
            mType.setText(mDisplayType);
        }
        final StringBuilder sb = new StringBuilder();
        if (mAttachment.state == AttachmentState.FAILED) {
            sb.append(getResources().getString(R.string.download_failed));
        } else if (mAttachment.isDownloading()) {
            if (!TextUtils.isEmpty(mAttachmentDownloadedSize) && mAttachment.shouldShowProgress()) {
                sb.append(mAttachmentDownloadedSize + "/");
            } else {
                sb.append(0 + "B/");
            }
            sb.append(mAttachmentSizeText);
            LogUtils.d("kkk"," well , update the size text : " + sb.toString());
        } else {
            if (mSaveClicked) {// TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 MOD
                sb.append(getResources().getString(R.string.saved)).append(mAttachmentSizeText);
            } else {
                sb.append(mAttachmentSizeText);
            }
        }
        mSize.setText(sb.toString());
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

    public void setThumbnailDefault (boolean isDefault) {
        mThunmbnailDefault = isDefault;
    }

    public boolean isThumbnailDefault () {
        return mThunmbnailDefault;
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
    public ImageView getThunmbnailView () {
        return mThumbnail;
    }

    public void setTitleVisibility(int visibility) {
        mTitle.setVisibility(visibility);
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E

    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 MOD_S
    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
    @Override
    public void onPermissionResult(int requestCode, String permission, int result) {
        if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_SAVE_ATTACHMENT) {
            if (PermissionUtil.checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                String emlAttAuth = mContext.getResources().getString(R.string.eml_attachment_provider);
                if (mAttachment.contentUri != null && mAttachment.contentUri.getAuthority().equals(emlAttAuth)) {
                    mActionHandler.startDownloadingAttachment(AttachmentDestination.EXTERNAL);
                    mSaveClicked = true;
                } else {
                    mActionHandler.startDownloadingAttachment(AttachmentDestination.CACHE);
                    mSaveClicked = true;
                }
            }
        } else if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_REDOWNLOAD_ATTACHMENT) {
            if (PermissionUtil.checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mActionHandler.startRedownloadingAttachment(mAttachment);
            }
        } else if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_VIEW_ATTACHMENT) {
            if (PermissionUtil.checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                mActionHandler.showAttachment(AttachmentDestination.EXTERNAL);
            }
        }
        //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 MOD_E
        if (mContext instanceof BaseActivity){
            ((BaseActivity) mContext).unRegisterPermissionResultListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mContext instanceof BaseActivity){
            ((BaseActivity) mContext).unRegisterPermissionResultListener(this);
        }
    }
    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E
    //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 ADD_S
    private void saveAttachmentWithLocationSelectable() {
        if (mAttachment == null) {
            Utility.showToast(this.getContext(), R.string.attachment_not_saved);
            return;
        }
        Activity activity = (Activity) getContext();
        Intent intent = new Intent(activity, com.tct.mail.browse.AttachmentDownloadSetting.class);
        intent.putExtra(AttachmentDownloadSetting.ATTACHMENT_KEY, mAttachment);
        activity.startActivityForResult(intent, 0);
        AttachmentDownloadSetting.setAttachmentActionHandler(mActionHandler);
        AttachmentDownloadSetting.setMessageAttachmentBar(this);
    }

    private void saveAttachmentAutomatic() {
        // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 MOD_S
        // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029228 MOD_S
        String emlAttAuth = mContext.getResources().getString(R.string.eml_attachment_provider);
        if (mAttachment.contentUri != null && mAttachment.contentUri.getAuthority().equals(emlAttAuth)) {
            if (PermissionUtil.checkAndRequestStoragePermission(mContext)) {
                mActionHandler.startDownloadingAttachment(AttachmentDestination.EXTERNAL);
                mSaveClicked = true;
            } else {
                //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
                if (mContext instanceof BaseActivity){
                    ((BaseActivity) mContext).registerPermissionResultListener(this);
                }
                //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E
            }
        } else {
            if (PermissionUtil.checkAndRequestStoragePermission(mContext)) {
                mActionHandler.startDownloadingAttachment(AttachmentDestination.CACHE);
                mSaveClicked = true;
            } else {
                //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
                if (mContext instanceof BaseActivity){
                    ((BaseActivity) mContext).registerPermissionResultListener(this);
                }
                //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E
            }
        }
        // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029228 MOD_E
        // TS: zhaotianyong 2015-01-29 EMAIL BUGFIX-916938 MOD_E
    }

    public void setSaveClicked(boolean clicked) {
        mSaveClicked = clicked;
    }

    public void setSavePath(String path) {
        mSavePath = path;
    }
    //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 ADD_E
}
