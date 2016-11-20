/**
 * Copyright (c) 2011, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-936728  2015/3/3     zhonghua.tuo    [Email]Can not open vcs and apk file in draft box and outbox
 *BUGFIX-935496  2015/03/06   zhaotianyong    [Email] Receive mail have different type audio but can't normal play audio
 *BUGFIX-945771  2015/03/13   zhaotianyong    [Email]Can not open vcs and apk file in draft box and outbox
 *BUGFIX-954622  2015/03/20   peng-zhang      [Email]Can attach file in downloads which has already been deleted.
 *BUGFIX-952615  2015/03/25   zhaotianyong    [Android5.0][Force close][Email][Gallery]Gallery and Google+ FC when open the photo from the Drafts and Outbox.
 *BUGFIX-959707  2015/03/30   peng-zhang      [REG][Android5.0][Email]Cannot share contacts from Contact..
 *BUGFIX-963397  2015/03/30   zhaotianyong    [Android5.0][Email][REG][Force close]Email force close when add file attachment from Drive
 *BUGFIX_968688  2015/04/04   zhangpeng       [Android5.0][Email][FC]Email will force close when we add widget for combined view
 *BUGFIX_971875  2015/04/09   zhonghua.tuo    [SMC][Email]com.tct.email happen crash due to java.lang.SecurityException
 *CR-996908      2015/6/8     yanhua.chen     [Email]attachment size limit unblock with toast
 *BUGFIX_1015669 2015/6/15    jian.xu         [Monitor][Force Close][Email]Happen FC when add picture attachment.
 *BUGFIX-1053132 2015/7/29    yanhua.chen     [Android5.0][Email]Toast display abnormally when forward an email more than 20M
 *FEATURE-ID     2015/08/27   Gantao         Horizontal attachment
 *BUGFIX-1488621 2016/01/22   junwei-xu       [Email]Not show the file size when share contact via Email.
 *BUGFIX-1712549 2016/03/02   rong-tang       [Email][Force Close]Email happens stopped when opening one folder from image
 *BUGFIX-1778322 2016/03/28   rong-tang       [Idol3 5.5 M upgrade][Monitor][Email]Can't input characters in recipients box when compose a new mail.
 *BUGFIX-1886442 2016/04/01   junwei-xu       [GAPP][Force Close][Email]Email appear FC when save attachement wihch name's first character is "#".
 ============================================================================ 
 */
package com.tct.mail.compose;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tct.email.R;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.AttachmentUtils;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.MimeType;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
//import com.google.common.collect.Lists;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.collect.Lists;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.UIProvider.AttachmentDestination;
import com.tct.mail.providers.UIProvider.AttachmentState;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import android.provider.DocumentsContract;
/*
 * View for displaying attachments in the compose screen.
 */
class AttachmentsView extends HorizontalScrollView {
    private static final String LOG_TAG = LogTag.getLogTag();

    private final ArrayList<Attachment> mAttachments;
    private AttachmentAddedOrDeletedListener mChangeListener;
    private LinearLayout mAttachmentLayout;

    //AM: peng-zhang 2015-03-20 EMAIL BUGFIX_954622 MOD_S
    private static boolean IsFileExit = true;
    //AM: peng-zhang 2015-03-20 EMAIL BUGFIX_954622 MOD_E

    public AttachmentsView(Context context) {
        this(context, null);
    }

    public AttachmentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAttachments = Lists.newArrayList();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mAttachmentLayout = (LinearLayout) findViewById(R.id.attachment_bar_list);
    }

    public void expandView() {
        mAttachmentLayout.setVisibility(VISIBLE);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /**
     * Set a listener for changes to the attachments.
     */
    public void setAttachmentChangesListener(AttachmentAddedOrDeletedListener listener) {
        mChangeListener = listener;
    }

    /**
     * Adds an attachment and updates the ui accordingly.
     */
    private void addAttachment(final Attachment attachment) {
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_S
        synchronized (mAttachments) {
            mAttachments.add(attachment);
        }
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_E

        //TS: Gantao 2016-02-24 EMAIL BUGFIX_1662403 MOD_S
        // If the attachment is inline do not display this attachment.
//        if (attachment.isInlineAttachment()) {
//            return;
//        }

        //Judge the attachment in my way...
        boolean isInlineAttachment = !(TextUtils.isEmpty(attachment.contentId) || attachment.isStandardAttachment());
        if(isInlineAttachment) {
            return;
        }
        //TS: Gantao 2016-02-24 EMAIL BUGFIX_1662403 MOD_E

        if (!isShown()) {
            setVisibility(View.VISIBLE);
        }

        expandView();

      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
        // only one type attachment view now.
//        // If we have an attachment that should be shown in a tiled look,
//        // set up the tile and add it to the tile grid.
//        if (AttachmentTile.isTiledAttachment(attachment)) {
//            final ComposeAttachmentTile attachmentTile =
//                    mTileGrid.addComposeTileFromAttachment(attachment);
//            attachmentTile.addDeleteListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    deleteAttachment(attachmentTile, attachment);
//                }
//            });
//        // Otherwise, use the old bar look and add it to the new
//        // inner LinearLayout.
//        } else {
//            final AttachmentComposeView attachmentView =
//                new AttachmentComposeView(getContext(), attachment);
//
//            attachmentView.addDeleteListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    deleteAttachment(attachmentView, attachment);
//                }
//            });
//
//
//            mAttachmentLayout.addView(attachmentView, new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT));
//        }
        final AttachmentComposeView attachmentView = new AttachmentComposeView(
                getContext(), attachment);

        attachmentView.addDeleteListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAttachment(attachmentView, attachment);
            }
        });

        mAttachmentLayout.addView(attachmentView);
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
        if (mChangeListener != null) {
            mChangeListener.onAttachmentAdded();
        }
    }

    @VisibleForTesting
    protected void deleteAttachment(final View attachmentView,
            final Attachment attachment) {
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_S
        synchronized (mAttachments) {
            mAttachments.remove(attachment);
        }
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_E
        ((ViewGroup) attachmentView.getParent()).removeView(attachmentView);
        if (mChangeListener != null) {
            mChangeListener.onAttachmentDeleted();
        }
    }

    /**
     * Get all attachments being managed by this view.
     * @return attachments.
     */
    public ArrayList<Attachment> getAttachments() {
        return mAttachments;
    }

    /**
     * Get all attachments previews that have been loaded
     * @return attachments previews.
     */
//    public ArrayList<AttachmentPreview> getAttachmentPreviews() {
//        return mTileGrid.getAttachmentPreviews();
//    }

    /**
     * Call this on restore instance state so previews persist across configuration changes
     */
//    public void setAttachmentPreviews(ArrayList<AttachmentPreview> previews) {
//        mTileGrid.setAttachmentPreviews(previews);
//    }

    /**
     * Delete all attachments being managed by this view.
     */
    public void deleteAllAttachments() {
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_S
        synchronized (mAttachments) {
            mAttachments.clear();
        }
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_E
//        mTileGrid.removeAllViews();
        mAttachmentLayout.removeAllViews();
        // TS: Gantao 2015-11-25 EMAIL BUGFIX_958805 MOD_S
        //Note: Just remove all views and don't setVisibility(GONE), in case of
        //UI issue "to/cc/bcc field display abnormal"
//        setVisibility(GONE);
        // TS: Gantao 2015-11-25 EMAIL BUGFIX_958805 MOD_E
    }

    /**
     * Get the total size of all attachments currently in this view.
     */
    private long getTotalAttachmentsSize() {
        long totalSize = 0;
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_S
        synchronized (mAttachments) {
            for (Attachment attachment : mAttachments) {
                totalSize += attachment.size;
            }
        }
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_E
        return totalSize;
    }

    /**
     * Interface to implement to be notified about changes to the attachments
     * explicitly made by the user.
     */
    public interface AttachmentAddedOrDeletedListener {
        public void onAttachmentDeleted();

        public void onAttachmentAdded();
    }

    /**
     * Generate an {@link Attachment} object for a given local content URI. Attempts to populate
     * the {@link Attachment#name}, {@link Attachment#size}, and {@link Attachment#contentType}
     * fields using a {@link ContentResolver}.
     *
     * @param contentUri
     * @return an Attachment object
     * @throws AttachmentFailureException
     */
    public Attachment generateLocalAttachment(Uri contentUri) throws AttachmentFailureException {
        if (contentUri == null || TextUtils.isEmpty(contentUri.getPath())) {
            throw new AttachmentFailureException("Failed to create local attachment");
        }

        // FIXME: do not query resolver for type on the UI thread
        final ContentResolver contentResolver = getContext().getContentResolver();
        String contentType = contentResolver.getType(contentUri);

        if (contentType == null) contentType = "";

        final Attachment attachment = new Attachment();
        attachment.uri = null; // URI will be assigned by the provider upon send/save
        attachment.setName(null);
        attachment.size = 0;
        attachment.contentUri = contentUri;
        attachment.thumbnailUri = contentUri;
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
        attachment.realUri= contentUri;
        //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
        //Note: check if uri is a folder type.
        if (isDocumentFolderType(attachment.contentUri)) {
            Utility.showToast(this.getContext(), R.string.add_folder_as_attachment_fail_tips);
            return null;
        }
        //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E
        if (DocumentsContract.isDocumentUri(this.getContext(), attachment.contentUri)
                || Attachment.isDownloadsUri(attachment.contentUri)) {
            String filePath = null;
            filePath = Attachment.getPath(this.getContext(),attachment.contentUri);
            //TS: junwei-xu 2016-04-01 EMAIL BUGFIX-1886442 MOD_S
            //Note: sometimes attachment name has special character, must encode it before parse to uri.
            if (!TextUtils.isEmpty(filePath)) {
                int index = filePath.lastIndexOf("/");
                if (index > 0) {
                    String directory = filePath.substring(0, index);
                    String name = filePath.substring(index + 1);
                    name = Uri.encode(name);
                    attachment.realUri = Uri.parse("file://" + directory + "/" + name);
                }
            }
            //TS: junwei-xu 2016-04-01 EMAIL BUGFIX-1886442 MOD_E
        }
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_E
        Cursor metadataCursor = null;
        try {
            // TS: zhaotianyong 2015-03-25 EMAIL BUGFIX_952615 MOD_S
            boolean isAudio = contentUri
                    .toString()
                    .startsWith(
                            "content://com.android.providers.media.documents/document/audio");
            // TS: zhaotianyong 2015-03-30 EMAIL BUGFIX_963397 MOD_S
            try {
                metadataCursor = contentResolver.query(
                        contentUri, new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Audio.Media.DATA},
                        null, null, null);
            } catch (Exception e) {
                metadataCursor = contentResolver.query(
                        contentUri, new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE},
                        null, null, null);
            }
            // TS: zhaotianyong 2015-03-30 EMAIL BUGFIX_963397 MOD_E
            if (metadataCursor != null) {
                try {
                    if (metadataCursor.moveToNext()) {
                        if (isAudio && attachment.realUri!=null) {
                            int index = attachment.realUri.toString().lastIndexOf("/");
                            if (index >0) {
                                String name = attachment.realUri.toString().substring(index+1);
                                attachment.setName(name);
                            }
                        } else {
                            attachment.setName(metadataCursor.getString(0));
                        }
                        attachment.size = metadataCursor.getInt(1);
                        String filePath = null;
                        if (metadataCursor.getColumnCount() > 2) {// TS: zhaotianyong 2015-03-30 EMAIL BUGFIX_963397 MOD
                            filePath = metadataCursor.getString(2);
                        }
                        //TS: junwei-xu 2016-04-01 EMAIL BUGFIX-1886442 MOD_S
                        //Note: sometimes attachment name has special character, must encode it before parse to uri.
                        if (!TextUtils.isEmpty(filePath)) {
                            int index = filePath.lastIndexOf("/");
                            if (index > 0) {
                                String directory = filePath.substring(0, index);
                                String name = filePath.substring(index + 1);
                                name = Uri.encode(name);
                                attachment.realUri = Uri.parse("file://" + directory + "/" + name);
                            }
                        }
                        //TS: junwei-xu 2016-04-01 EMAIL BUGFIX-1886442 MOD_E
                        //[BUGFIX]-ADD begin by SCDTABLET.shujing.jin@tcl.com,09/14/2016,2889271
                        if (!TextUtils.isEmpty(filePath)) {
                            int index = filePath.lastIndexOf("/");
                            if (index > 0) {
                                String directory = filePath.substring(0, index);
                                String name = filePath.substring(index + 1);
                                attachment.setName(name);
                            }
                        }
                        //[BUGFIX]-ADD end by SCDTABLET.shujing.jin@tcl.com
             // TS: zhaotianyong 2015-03-25 EMAIL BUGFIX_952615 MOD_E
                    }
                } finally {
                    metadataCursor.close();
                }
            }
        } catch (SQLiteException ex) {
            // One of the two columns is probably missing, let's make one more attempt to get at
            // least one.
            // Note that the documentations in Intent#ACTION_OPENABLE and
            // OpenableColumns seem to contradict each other about whether these columns are
            // required, but it doesn't hurt to fail properly.

            // Let's try to get DISPLAY_NAME
            try {
                metadataCursor = getOptionalColumn(contentResolver, contentUri,
                        OpenableColumns.DISPLAY_NAME);
                if (metadataCursor != null && metadataCursor.moveToNext()) {
                    attachment.setName(metadataCursor.getString(0));
                }
            } finally {
                if (metadataCursor != null) metadataCursor.close();
            }

            // Let's try to get SIZE
            try {
                metadataCursor =
                        getOptionalColumn(contentResolver, contentUri, OpenableColumns.SIZE);
                if (metadataCursor != null && metadataCursor.moveToNext()) {
                    attachment.size = metadataCursor.getInt(0);
                } else {
                    // Unable to get the size from the metadata cursor. Open the file and seek.
                    attachment.size = getSizeFromFile(contentUri, contentResolver);
                }
            } finally {
                if (metadataCursor != null) metadataCursor.close();
            }
        } catch (SecurityException e) {
            throw new AttachmentFailureException("Security Exception from attachment uri", e);
        }

        if (attachment.getName() == null) {
            attachment.setName(contentUri.getLastPathSegment());
        }
        if (attachment.size == 0) {
            // if the attachment is not a content:// for example, a file:// URI
            attachment.size = getSizeFromFile(contentUri, contentResolver);
        }
        //TS: junwei-xu 2016-01-22 EMAIL BUGFIX-1488621 ADD_S
        //Note: For vcf file, use AssetFileDescriptor to get it size.
        if (attachment.size == 0 && "text/x-vcard".equalsIgnoreCase(contentType)) {
            attachment.size = getSizeFromAssetFile(contentUri, contentResolver);
        }
        //TS: junwei-xu 2016-01-22 EMAIL BUGFIX-1488621 ADD_E

        attachment.setContentType(contentType);
        //TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 ADD_S
        attachment.setState(AttachmentState.SAVED);
        if (MimeType.isInstallable(attachment.getContentType())) {
            attachment.setDestination(AttachmentDestination.EXTERNAL);
        }
        //TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 ADD_E
        return attachment;
    }

    //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
    private boolean isDocumentFolderType(Uri attachmentUri) {
        String type = Attachment.getMediaDocumentType(this.getContext(), attachmentUri);
        if ((!TextUtils.isEmpty(type) && type.endsWith("_bucket"))
                || "album".equals(type) || "artist".equals(type)) {
            return true;
        }
        return false;
    }
    //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E

    /**
     * Adds an attachment of either local or remote origin, checking to see if the attachment
     * exceeds file size limits.
     * @param account
     * @param attachment the attachment to be added.
     *
     * @return size of the attachment added.
     * @throws AttachmentFailureException if an error occurs adding the attachment.
     */
    public long addAttachment(Account account, Attachment attachment, boolean isInit)
            throws AttachmentFailureException {//TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD
        final int maxSize = account.settings.getMaxAttachmentSize();
        //AM: peng-zhang 2015-03-30 EMAIL BUGFIX_959707 MOD_S
        if(attachment != null && attachment.contentUri != null) { // AM: peng-zhang 2015-04-04 EMAIL BUGFIX_968688 MOD
            final ContentResolver contentResolver = getContext().getContentResolver();
            boolean isDownload = attachment.contentUri.toString().startsWith("content://com.android.providers.downloads.documents/document");
            boolean isDownload2 = attachment.contentUri.toString().startsWith("content://media/external");
            if(isDownload || isDownload2){
                ParcelFileDescriptor file = null;
                try{
                    file = contentResolver.openFileDescriptor(attachment.contentUri, "r");
                }catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }finally{
                    try{
                        if(null == file){
                            IsFileExit = false;
                        }else{
                            file.close();
                        }
                    }catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        //AM: peng-zhang 2015-03-30 EMAIL BUGFIX_959707 MOD_E
        //TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD_S
        // Error getting the size or the size was too big.
        final int midSize= account.settings.DEFAULT_MID_ATTACHMENT_SIZE;
        final String defaultMidSize = AttachmentUtils.convertToHumanReadableSize(getContext(), midSize);
        if (attachment.size == -1 || attachment.size > maxSize) {
            throw new AttachmentFailureException(
                    "Attachment too large to attach", R.string.too_large_to_attach_single);
        } else if ((getTotalAttachmentsSize()
                + attachment.size) > maxSize) {
            throw new AttachmentFailureException(
                    "Attachment too large to attach", R.string.too_large_to_attach_additional);
        }
        //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 DEL_S
        /*else if ((getTotalAttachmentsSize() + attachment.size) > midSize) {
            addAttachment(attachment);
            if(!isInit){
                showErrorToast(getResources().getString(R.string.too_large_to_mid_attach_additional, defaultMidSize));
            }
        }*/
        //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 DEL_E
        //TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD_E
        //AM: peng-zhang 2015-03-20 EMAIL BUGFIX_954622 MOD_S
        else if(!IsFileExit){
           IsFileExit = true;
           throw new AttachmentFailureException("Attachment is deleted");
        }
        //AM: peng-zhang 2015-03-20 EMAIL BUGFIX_954622 MOD_E
        else {
            addAttachment(attachment);
        }

        return attachment.size;
    }

    //TS: junwei-xu 2016-01-22 EMAIL BUGFIX-1488621 ADD_S
    private int getSizeFromAssetFile(Uri uri, ContentResolver contentResolver) {
        int size = -1;
        AssetFileDescriptor file = null;
        try {
            file = contentResolver.openAssetFileDescriptor(uri, "r");
            size = (int) file.getLength();
        } catch (FileNotFoundException e) {
            LogUtils.w(LOG_TAG, e, "Error opening file to obtain size.");
        } catch (SecurityException e) {
            LogUtils.w(LOG_TAG, e, "no permission to access the uri.");
        } catch (IllegalArgumentException e) {
            LogUtils.w(LOG_TAG, e, "IllegalArgumentException while opening file to obtain size.");
        } finally {
            try {
                if (file != null) file.close();
            } catch (IOException e) {
                LogUtils.w(LOG_TAG, "Error closing file opened to obtain size.");
            }
        }
        // We only want to return a non-negative value. (ParcelFileDescriptor#getStatSize() will
        // return -1 if the fd is not a file
        return Math.max(size, 0);
    }
    //TS: junwei-xu 2016-01-22 EMAIL BUGFIX-1488621 ADD_E

    private static int getSizeFromFile(Uri uri, ContentResolver contentResolver) {
        int size = -1;
        ParcelFileDescriptor file = null;
        try {
            file = contentResolver.openFileDescriptor(uri, "r");
            size = (int) file.getStatSize();
        } catch (FileNotFoundException e) {
            LogUtils.w(LOG_TAG, e, "Error opening file to obtain size.");
        //TS: zhonghua.tuo 2015-04-09 EMAIL BUGFIX_971875 ADD_S
        } catch (SecurityException e) {
            LogUtils.w(LOG_TAG, e, "no permission to access the uri.");
        //TS: zhonghua.tuo 2015-04-09 EMAIL BUGFIX_971875 ADD_E
        //TS: tianjing.su 2016-01-11 EMAIL BUGFIX_1397533 ADD_S
        } catch (IllegalArgumentException e) {
            LogUtils.w(LOG_TAG, e, "IllegalArgumentException while opening file to obtain size.");
        //TS: tianjing.su 2016-01-11 EMAIL BUGFIX_1397533 ADD_E
        //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
        } catch (UnsupportedOperationException e) {
            LogUtils.w(LOG_TAG, e, "UnsupportedOperationException while opening file to obtain size.");
        //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                LogUtils.w(LOG_TAG, "Error closing file opened to obtain size.");
            }
        }
        // We only want to return a non-negative value. (ParcelFileDescriptor#getStatSize() will
        // return -1 if the fd is not a file
        return Math.max(size, 0);
    }

    /**
     * @return a cursor to the requested column or null if an exception occurs while trying
     * to query it.
     */
    private static Cursor getOptionalColumn(ContentResolver contentResolver, Uri uri,
            String columnName) {
        Cursor result = null;
        try {
            result = contentResolver.query(uri, new String[]{columnName}, null, null, null);
        } catch (SQLiteException ex) {
            // ignore, leave result null
        }
        return result;
    }

    public void focusLastAttachment() {
        Attachment lastAttachment;
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_S
        synchronized (mAttachments) {
            lastAttachment = mAttachments.get(mAttachments.size() - 1);
        }
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_E
        View lastView = null;
        //Only one type of attachment now
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
//        int last = 0;
//        if (AttachmentTile.isTiledAttachment(lastAttachment)) {
//            last = mTileGrid.getChildCount() - 1;
//            if (last > 0) {
//                lastView = mTileGrid.getChildAt(last);
//            }
//        } else {
//            last = mAttachmentLayout.getChildCount() - 1;
//            if (last > 0) {
//                lastView = mAttachmentLayout.getChildAt(last);
//            }
//        }
        int last = mAttachmentLayout.getChildCount() - 1;
        //TS: rong-tang 2016-03-28 EMAIL BUGFIX-1863457 MOD_S
        //Note: the last index maybe is zero.
        if (last >= 0) {
            lastView = mAttachmentLayout.getChildAt(last);
        }
        //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
        if (lastView != null) {
            //Note: requestFocus() is invalid, use requestFocusFromTouch().
            lastView.requestFocusFromTouch();
        }
        //TS: rong-tang 2016-03-28 EMAIL BUGFIX-1863457 MOD_E
    }

    /**
     * Class containing information about failures when adding attachments.
     */
    static class AttachmentFailureException extends Exception {
        private static final long serialVersionUID = 1L;
        private final int errorRes;

        public AttachmentFailureException(String detailMessage) {
            super(detailMessage);
            this.errorRes = R.string.generic_attachment_problem;
        }

        public AttachmentFailureException(String error, int errorRes) {
            super(error);
            this.errorRes = errorRes;
        }

        public AttachmentFailureException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
            this.errorRes = R.string.generic_attachment_problem;
        }

        /**
         * Get the error string resource that corresponds to this attachment failure. Always a valid
         * string resource.
         */
        public int getErrorRes() {
            return errorRes;
        }
    }

    private void showErrorToast(String message) {
        Toast t = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        t.setText(message);
        t.setGravity(Gravity.CENTER_HORIZONTAL, 0,
                getResources().getDimensionPixelSize(R.dimen.attachment_toast_yoffset));
        t.show();
    }

}
