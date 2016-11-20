/*
 * Copyright (C) 2011 The Android Open Source Project
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
 *Tag              Date         Author         Description
 *============== ============ =============== ==============================
 *CONFLICT-50003 2014/10/24   zhaotianyong    Modify the package conflict
 *BUGFIX-868520  2014/12/11   wenggangjin     [Android 5.0][Exchange]Can't display pictures in Exchange mail body
 *BUGFIX-871936  2014/12/15   wenggangjin     [Android5.0][Email][Crash]Email crashs after selecting Save in attachment
 *BUGFIX-888753  2014/01/26   zhaotianyong    [Android5.0][Exchange]The picture order in 163 and 126 account is wrong
 *BUGFIX-899799  2015/03/23   zhaotianyong    [Email]Table is overlpap with area "reply,reply all,forword".
 *BUGFIX-962560  2015/04/01   zhaotianyong    [Email]Can not attach the inner picture when Fwd/Reply/Reply all the HTML email
 *BUGFIX-989483  2015/05/05   zhaotianyong    [Email] ZIP file should be available to download.
 *BUGFIX-1031608 2015/06/30   Gantao          [Email]No prompt pop up after download again the attachment.
 *TASK-1477377   2016/01/20   jian.xu         Save attchment to user select location
 *BUGFIX-1531245 2016/02/04   junwei-xu       [Clock]Alarm ringtone list display not correct after delete ringtone from download list
 *BUGFIX_1719263 2015/3/14    yanhua.chen     [Email]The attachment downloading behavior is incorrect when the attachement is too large
 *BUGFIX-1886442 2016/03/31   junwei-xu       [GAPP][Force Close][Email]Email appear FC when save attachement wihch name's first character is "#".
 ============================================================================
 */

package com.tct.emailcommon.utility;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

//TS: DEL by zhaotianyong for CONFLICT_50003 START
//import com.tct.mail.providers.UIProvider;
//import org.apache.commons.io.IOUtils;
//TS: DEL by zhaotianyong for CONFLICT_50003 END

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.provider.EmailContent.BodyColumns;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
//TS: ADD by zhaotianyong for CONFLICT_50003 START
import com.tct.mail.utils.IOUtils;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.UIProvider;
import com.tct.permission.PermissionUtil;
//TS: ADD by zhaotianyong for CONFLICT_50003 END


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.PatternSyntaxException;

public class AttachmentUtilities {

    public static final String FORMAT_RAW = "RAW";
    public static final String FORMAT_THUMBNAIL = "THUMBNAIL";

    public static class Columns {
        public static final String _ID = "_id";
        public static final String DATA = "_data";
        public static final String DISPLAY_NAME = "_display_name";
        public static final String SIZE = "_size";
    }

    private static final String[] ATTACHMENT_CACHED_FILE_PROJECTION = new String[] {
            AttachmentColumns.CACHED_FILE
    };

    /**
     * The MIME type(s) of attachments we're willing to send via attachments.
     *
     * Any attachments may be added via Intents with Intent.ACTION_SEND or ACTION_SEND_MULTIPLE.
     */
    public static final String[] ACCEPTABLE_ATTACHMENT_SEND_INTENT_TYPES = new String[] {
        "*/*",
    };
    /**
     * The MIME type(s) of attachments we're willing to send from the internal UI.
     *
     * NOTE:  At the moment it is not possible to open a chooser with a list of filter types, so
     * the chooser is only opened with the first item in the list.
     */
    public static final String[] ACCEPTABLE_ATTACHMENT_SEND_UI_TYPES = new String[] {
        "image/*",
        "video/*",
    };
    /**
     * The MIME type(s) of attachments we're willing to view.
     */
    public static final String[] ACCEPTABLE_ATTACHMENT_VIEW_TYPES = new String[] {
        "*/*",
    };
    /**
     * The MIME type(s) of attachments we're not willing to view.
     */
    public static final String[] UNACCEPTABLE_ATTACHMENT_VIEW_TYPES = new String[] {
    };
    /**
     * The MIME type(s) of attachments we're willing to download to SD.
     */
    public static final String[] ACCEPTABLE_ATTACHMENT_DOWNLOAD_TYPES = new String[] {
        "*/*",
    };
    /**
     * The MIME type(s) of attachments we're not willing to download to SD.
     */
    public static final String[] UNACCEPTABLE_ATTACHMENT_DOWNLOAD_TYPES = new String[] {
    };
    // TS: zhaotianyong 2015-05-05 EMAIL BUGFIX-989483 MOD_S
    /**
     * Filename extensions of attachments we're never willing to download (potential malware).
     * Entries in this list are compared to the end of the lower-cased filename, so they must
     * be lower case, and should not include a "."
     */
    public static final String[] UNACCEPTABLE_ATTACHMENT_EXTENSIONS = new String[] {
        // File types that contain malware
        "ade", "adp", "bat", "chm", "cmd", "com", "cpl", "dll", "exe",
        "hta", "ins", "isp", "jse", "lib", "mde", "msc", "msp",
        "mst", "pif", "scr", "sct", "shb", "sys", "vb", "vbe",
        "vbs", "vxd", "wsc", "wsf", "wsh",
        // File types of common compression/container formats (again, to avoid malware)
        "gz", "z", "tar", "tgz", "bz2",
    };
    // TS: zhaotianyong 2015-05-05 EMAIL BUGFIX-989483 MOD_E
    /**
     * Filename extensions of attachments that can be installed.
     * Entries in this list are compared to the end of the lower-cased filename, so they must
     * be lower case, and should not include a "."
     */
    public static final String[] INSTALLABLE_ATTACHMENT_EXTENSIONS = new String[] {
        "apk",
    };
    // TS: zhaotianyong 2015-05-05 EMAIL BUGFIX-989483 ADD_S
    /**
     * Filename extensions of attachments we can download without installer.
     * Entries in this list are compared to the end of the lower-cased filename, so they must
     * be lower case, and should not include a "."
     */
    public static final String[] DOWNLOAD_WITHOUT_INSTALLER_ATTACHMENT_EXTENSIONS = new String [] {
        "zip",
    };
    // TS: zhaotianyong 2015-05-05 EMAIL BUGFIX-989483 MOD_E
    /**
     * The maximum size of an attachment we're willing to download (either View or Save)
     * Attachments that are base64 encoded (most) will be about 1.375x their actual size
     * so we should probably factor that in. A 5MB attachment will generally be around
     * 6.8MB downloaded but only 5MB saved.
     */
    public static final int MAX_ATTACHMENT_DOWNLOAD_SIZE = (5 * 1024 * 1024);
    /**
     * The maximum size of an attachment we're willing to upload (measured as stored on disk).
     * Attachments that are base64 encoded (most) will be about 1.375x their actual size
     * so we should probably factor that in. A 5MB attachment will generally be around
     * 6.8MB uploaded.
     */
    public static final int MAX_ATTACHMENT_UPLOAD_SIZE = (5 * 1024 * 1024);

    private static Uri sUri;
    public static Uri getAttachmentUri(long accountId, long id) {
        if (sUri == null) {
            sUri = Uri.parse(Attachment.ATTACHMENT_PROVIDER_URI_PREFIX);
        }
        return sUri.buildUpon()
                .appendPath(Long.toString(accountId))
                .appendPath(Long.toString(id))
                .appendPath(FORMAT_RAW)
                .build();
    }

    // exposed for testing
    public static Uri getAttachmentThumbnailUri(long accountId, long id, long width, long height) {
        if (sUri == null) {
            sUri = Uri.parse(Attachment.ATTACHMENT_PROVIDER_URI_PREFIX);
        }
        return sUri.buildUpon()
                .appendPath(Long.toString(accountId))
                .appendPath(Long.toString(id))
                .appendPath(FORMAT_THUMBNAIL)
                .appendPath(Long.toString(width))
                .appendPath(Long.toString(height))
                .build();
    }

    /**
     * Return the filename for a given attachment.  This should be used by any code that is
     * going to *write* attachments.
     *
     * This does not create or write the file, or even the directories.  It simply builds
     * the filename that should be used.
     */
    public static File getAttachmentFilename(Context context, long accountId, long attachmentId) {
        return new File(getAttachmentDirectory(context, accountId), Long.toString(attachmentId));
    }

    /**
     * Return the directory for a given attachment.  This should be used by any code that is
     * going to *write* attachments.
     *
     * This does not create or write the directory.  It simply builds the pathname that should be
     * used.
     */
    public static File getAttachmentDirectory(Context context, long accountId) {
        return context.getDatabasePath(accountId + ".db_att");
    }

    /**
     * Helper to convert unknown or unmapped attachments to something useful based on filename
     * extensions. The mime type is inferred based upon the table below. It's not perfect, but
     * it helps.
     *
     * <pre>
     *                   |---------------------------------------------------------|
     *                   |                  E X T E N S I O N                      |
     *                   |---------------------------------------------------------|
     *                   | .eml        | known(.png) | unknown(.abc) | none        |
     * | M |-----------------------------------------------------------------------|
     * | I | none        | msg/rfc822  | image/png   | app/abc       | app/oct-str |
     * | M |-------------| (always     |             |               |             |
     * | E | app/oct-str |  overrides  |             |               |             |
     * | T |-------------|             |             |-----------------------------|
     * | Y | text/plain  |             |             | text/plain                  |
     * | P |-------------|             |-------------------------------------------|
     * | E | any/type    |             | any/type                                  |
     * |---|-----------------------------------------------------------------------|
     * </pre>
     *
     * NOTE: Since mime types on Android are case-*sensitive*, return values are always in
     * lower case.
     *
     * @param fileName The given filename
     * @param mimeType The given mime type
     * @return A likely mime type for the attachment
     */
    public static String inferMimeType(final String fileName, final String mimeType) {
        String resultType = null;
        String fileExtension = getFilenameExtension(fileName);
        boolean isTextPlain = "text/plain".equalsIgnoreCase(mimeType);

        if ("eml".equals(fileExtension)) {
            resultType = "message/rfc822";
        } else {
            boolean isGenericType =
                    isTextPlain || "application/octet-stream".equalsIgnoreCase(mimeType);
            // If the given mime type is non-empty and non-generic, return it
            if (isGenericType || TextUtils.isEmpty(mimeType)) {
                if (!TextUtils.isEmpty(fileExtension)) {
                    // Otherwise, try to find a mime type based upon the file extension
                    resultType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                    if (TextUtils.isEmpty(resultType)) {
                        // Finally, if original mimetype is text/plain, use it; otherwise synthesize
                        resultType = isTextPlain ? mimeType : "application/" + fileExtension;
                    }
                }
            } else {
                resultType = mimeType;
            }
        }

        // No good guess could be made; use an appropriate generic type
        if (TextUtils.isEmpty(resultType)) {
            resultType = isTextPlain ? "text/plain" : "application/octet-stream";
        }
        return resultType.toLowerCase();
    }

    /**
     * Extract and return filename's extension, converted to lower case, and not including the "."
     *
     * @return extension, or null if not found (or null/empty filename)
     */
    public static String getFilenameExtension(String fileName) {
        String extension = null;
        if (!TextUtils.isEmpty(fileName)) {
            int lastDot = fileName.lastIndexOf('.');
            if ((lastDot > 0) && (lastDot < fileName.length() - 1)) {
                extension = fileName.substring(lastDot + 1).toLowerCase();
            }
        }
        return extension;
    }

    /**
     * Resolve attachment id to content URI.  Returns the resolved content URI (from the attachment
     * DB) or, if not found, simply returns the incoming value.
     *
     * @param attachmentUri
     * @return resolved content URI
     *
     * TODO:  Throws an SQLite exception on a missing DB file (e.g. unknown URI) instead of just
     * returning the incoming uri, as it should.
     */
    public static Uri resolveAttachmentIdToContentUri(ContentResolver resolver, Uri attachmentUri) {
        Cursor c = resolver.query(attachmentUri,
                new String[] { Columns.DATA },
                null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    final String strUri = c.getString(0);
                    if (strUri != null) {
                        return Uri.parse(strUri);
                    }
                }
            } finally {
                c.close();
            }
        }
        return attachmentUri;
    }

    /**
     * In support of deleting a message, find all attachments and delete associated attachment
     * files.
     * @param context
     * @param accountId the account for the message
     * @param messageId the message
     */
    public static void deleteAllAttachmentFiles(Context context, long accountId, long messageId) {
        Uri uri = ContentUris.withAppendedId(Attachment.MESSAGE_ID_URI, messageId);
        Cursor c = context.getContentResolver().query(uri, Attachment.ID_PROJECTION,
                null, null, null);
        try {
            while (c.moveToNext()) {
                long attachmentId = c.getLong(Attachment.ID_PROJECTION_COLUMN);
                File attachmentFile = getAttachmentFilename(context, accountId, attachmentId);
                // Note, delete() throws no exceptions for basic FS errors (e.g. file not found)
                // it just returns false, which we ignore, and proceed to the next file.
                // This entire loop is best-effort only.
                attachmentFile.delete();
            }
        } finally {
            c.close();
        }
    }

    /**
     * In support of deleting a message, find all attachments and delete associated cached
     * attachment files.
     * @param context
     * @param accountId the account for the message
     * @param messageId the message
     */
    public static void deleteAllCachedAttachmentFiles(Context context, long accountId,
            long messageId) {
        final Uri uri = ContentUris.withAppendedId(Attachment.MESSAGE_ID_URI, messageId);
        final Cursor c = context.getContentResolver().query(uri, ATTACHMENT_CACHED_FILE_PROJECTION,
                null, null, null);
        try {
            while (c.moveToNext()) {
                final String fileName = c.getString(0);
                if (!TextUtils.isEmpty(fileName)) {
                    final File cachedFile = new File(fileName);
                    // Note, delete() throws no exceptions for basic FS errors (e.g. file not found)
                    // it just returns false, which we ignore, and proceed to the next file.
                    // This entire loop is best-effort only.
                    cachedFile.delete();
                }
            }
        } finally {
            c.close();
        }
    }

    /**
     * In support of deleting a mailbox, find all messages and delete their attachments.
     *
     * @param context
     * @param accountId the account for the mailbox
     * @param mailboxId the mailbox for the messages
     */
    public static void deleteAllMailboxAttachmentFiles(Context context, long accountId,
            long mailboxId) {
        Cursor c = context.getContentResolver().query(Message.CONTENT_URI,
                Message.ID_COLUMN_PROJECTION, MessageColumns.MAILBOX_KEY + "=?",
                new String[] { Long.toString(mailboxId) }, null);
        try {
            while (c.moveToNext()) {
                long messageId = c.getLong(Message.ID_PROJECTION_COLUMN);
                deleteAllAttachmentFiles(context, accountId, messageId);
            }
        } finally {
            c.close();
        }
    }

    /**
     * In support of deleting or wiping an account, delete all related attachments.
     *
     * @param context
     * @param accountId the account to scrub
     */
    public static void deleteAllAccountAttachmentFiles(Context context, long accountId) {
        File[] files = getAttachmentDirectory(context, accountId).listFiles();
        if (files == null) return;
        for (File file : files) {
            boolean result = file.delete();
            if (!result) {
                LogUtils.e(Logging.LOG_TAG, "Failed to delete attachment file " + file.getName());
            }
        }
    }

    private static long copyFile(InputStream in, OutputStream out) throws IOException {
        long size = IOUtils.copy(in, out);
        in.close();
        out.flush();
        out.close();
        return size;
    }
    
    /**
     * Save the attachment to its final resting place (cache or sd card)
     */
    public static long saveAttachment(Context context, InputStream in, Attachment attachment) {
        final Uri uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, attachment.mId);
        final ContentValues cv = new ContentValues();
        final long attachmentId = attachment.mId;
        final long accountId = attachment.mAccountKey;
        //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_S
        String contentUri = null;
        //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_E
        String realUri = null;  //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
        long size =0;

        try {
            ContentResolver resolver = context.getContentResolver();
            if (attachment.mUiDestination == UIProvider.UIPROVIDER_ATTACHMENTDESTINATION_CACHE) {
                LogUtils.i(LogUtils.TAG, "AttachmentUtilities saveAttachment when attachment destination is cache", "attachment.size:" + attachment.mSize);
                Uri attUri = getAttachmentUri(accountId, attachmentId);
                size = copyFile(in, resolver.openOutputStream(attUri));
                contentUri = attUri.toString();
            } else if (Utility.isExternalStorageMounted()) {
                LogUtils.i(LogUtils.TAG, "AttachmentUtilities saveAttachment to storage", "attachment.size:" + attachment.mSize);
                if (TextUtils.isEmpty(attachment.mFileName)) {
                    // TODO: This will prevent a crash but does not surface the underlying problem
                    // to the user correctly.
                    LogUtils.w(Logging.LOG_TAG, "Trying to save an attachment with no name: %d",
                            attachmentId);
                    throw new IOException("Can't save an attachment with no name");
                }
                //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
                String exchange = "com.tct.exchange";
                if (exchange.equals(context.getPackageName())
                        && !PermissionUtil.checkPermissionAndLaunchExplain(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    throw new IOException("Can't save an attachment due to no Storage permission");
                }
                //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
                File downloads = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                downloads.mkdirs();
                File file = Utility.createUniqueFile(downloads, attachment.mFileName);
                size = copyFile(in, new FileOutputStream(file));
                String absolutePath = file.getAbsolutePath();
                realUri = "file://" + absolutePath;   //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD

                // Although the download manager can scan media files, scanning only happens
                // after the user clicks on the item in the Downloads app. So, we run the
                // attachment through the media scanner ourselves so it gets added to
                // gallery / music immediately.
                MediaScannerConnection.scanFile(context, new String[] {absolutePath},
                        null, null);

                final String mimeType = TextUtils.isEmpty(attachment.mMimeType) ?
                        "application/octet-stream" :
                        attachment.mMimeType;

                try {
                    DownloadManager dm =
                            (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    //TS: junwei-xu 2016-02-04 EMAIL BUGFIX-1531245 MOD_S
                    //Note: should use media scanner, it will allow update the
                    //media provider uri column in download manager's database.
                    long id = dm.addCompletedDownload(attachment.mFileName, attachment.mFileName,
                            true /* use media scanner */,
                            mimeType, absolutePath, size,
                            true /* show notification */);
                    //TS: junwei-xu 2016-02-04 EMAIL BUGFIX-1531245 MOD_E
                    contentUri = dm.getUriForDownloadedFile(id).toString();
                } catch (final IllegalArgumentException e) {
                    LogUtils.d(LogUtils.TAG, e, "IAE from DownloadManager while saving attachment");
                    throw new IOException(e);
                }
            } else {
                LogUtils.w(Logging.LOG_TAG,
                        "Trying to save an attachment without external storage?");
                throw new IOException();
            }

            // Update the attachment
            cv.put(AttachmentColumns.SIZE, size);
            cv.put(AttachmentColumns.CONTENT_URI, contentUri);
            cv.put(AttachmentColumns.UI_STATE, UIProvider.UIPROVIDER_ATTACHMENTSTATE_SAVED);
            //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
            if (realUri != null) {
                cv.put(AttachmentColumns.REAL_URI, realUri);
            }
            //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
        } catch (IOException e) {
            LogUtils.e(Logging.LOG_TAG,e,"Fail to save attachment to storage!");
            // Handle failures here...
            cv.put(AttachmentColumns.UI_STATE, UIProvider.UIPROVIDER_ATTACHMENTSTATE_FAILED);
        }
       context.getContentResolver().update(uri, cv, null, null);
     //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_S
       // If this is an inline attachment, update the body
       if (contentUri != null && attachment.mContentId != null && attachment.mContentId.length() > 0) {
           Body body = Body.restoreBodyWithMessageId(context, attachment.mMessageKey);
           if (body != null && body.mHtmlContent != null) {
               cv.clear();
               String html = body.mHtmlContent;
               String contentIdRe =
                       "\\s+(?i)src=\"cid(?-i):\\Q" + attachment.mContentId + "\\E\"";
               //TS: zhaotianyong 2015-03-23 EXCHANGE BUGFIX_899799 MOD_S
               //TS: zhaotianyong 2015-04-01 EXCHANGE BUGFIX_962560 MOD_S
               String srcContentUri = " src=\"" + contentUri + "\"";
               //TS: zhaotianyong 2015-04-01 EXCHANGE BUGFIX_962560 MOD_E
               //TS: zhaotianyong 2015-03-23 EXCHANGE BUGFIX_899799 MOD_E
               //TS: zhaotianyong 2015-04-15 EMAIL BUGFIX_976967 MOD_S
               try {
                   html = html.replaceAll(contentIdRe, srcContentUri);
               } catch (PatternSyntaxException e) {
                    LogUtils.w(Logging.LOG_TAG,
                            "Unrecognized backslash escape sequence in pattern");
               }
               //TS: zhaotianyong 2015-04-15 EMAIL BUGFIX_976967 MOD_E
               cv.put(BodyColumns.HTML_CONTENT, html);
               Body.updateBodyWithMessageId(context, attachment.mMessageKey, cv);
               Body.restoreBodyHtmlWithMessageId(context, attachment.mMessageKey);
           }
       }
     //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_E
        return size;
    }
    //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 844039	463
    public static void saveAttachmentToExternal(Context context, Attachment attachment, String path) {
        final Uri uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, attachment.mId);
        final ContentValues cv = new ContentValues();
        final long attachmentId = attachment.mId;
        final long accountId = attachment.mAccountKey;
        //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_S
        String contentUri = null;
        //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_S
        final long size;
        InputStream in = null;
        OutputStream out = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            if (Utility.isExternalStorageMounted()) {
                if (TextUtils.isEmpty(attachment.mFileName)) {
                    // TODO: This will prevent a crash but does not surface the underlying problem
                    // to the user correctly.
                    LogUtils.w(Logging.LOG_TAG, "Trying to save an attachment with no name: %d",
                            attachmentId);
                    throw new IOException("Can't save an attachment with no name");
                }
                // TS: Gantao 2015-07-29 EMAIL BUGFIX-1055568 MOD_S
                try {
                    String cachedFileUri = attachment.getCachedFileUri();
                    if (TextUtils.isEmpty(cachedFileUri)) {
                        throw new IOException();
                    }
                    in = resolver.openInputStream(Uri.parse(cachedFileUri));
                } catch (IOException e) {
                    String contentUriForOpen = attachment.getContentUri();
                    if (TextUtils.isEmpty(contentUriForOpen)) {
                        throw new IOException();
                    }
                    in = resolver.openInputStream(Uri.parse(contentUriForOpen));
                //TS: junwei-xu 2016-03-31 EMAIL BUGFIX-1886442 ADD_S
                } catch (IllegalArgumentException e) {
                    String contentUriForOpen = attachment.getContentUri();
                    if (TextUtils.isEmpty(contentUriForOpen)) {
                        throw new IOException();
                    }
                    in = resolver.openInputStream(Uri.parse(contentUriForOpen));
                }
                //TS: junwei-xu 2016-03-31 EMAIL BUGFIX-1886442 ADD_E
                //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_S
                //Note: we support save attachment at user designated location.
                File downloads;
                if (path != null) {
                    downloads = new File(path);
                } else {
                    downloads = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                }
                //TS: jian.xu 2016-01-20 EMAIL FEATURE-1477377 MOD_E
                downloads.mkdirs();
                File file = Utility.createUniqueFile(downloads, attachment.mFileName);
                out = new FileOutputStream(file);
                size = copyFile(in, out);
                String absolutePath = file.getAbsolutePath();
                // Although the download manager can scan media files, scanning only happens
                // after the user clicks on the item in the Downloads app. So, we run the
                // attachment through the media scanner ourselves so it gets added to
                // gallery / music immediately.
                MediaScannerConnection.scanFile(context, new String[] {absolutePath},
                        null, null);
                final String mimeType = TextUtils.isEmpty(attachment.mMimeType) ?
                        "application/octet-stream" :attachment.mMimeType;
                try {
                    DownloadManager dm =
                            (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    //TS: junwei-xu 2016-02-04 EMAIL BUGFIX-1531245 MOD_S
                    //Note: should use media scanner, it will allow update the
                    //media provider uri column in download manager's database.
                    long id = dm.addCompletedDownload(attachment.mFileName, attachment.mFileName,
                            true /* use media scanner */,
                            mimeType, absolutePath, size,
                            true /* show notification */);
                    //TS: junwei-xu 2016-02-04 EMAIL BUGFIX-1531245 MOD_E
                    contentUri = dm.getUriForDownloadedFile(id).toString();
                } catch (final IllegalArgumentException e) {
                    LogUtils.d(LogUtils.TAG, e, "IAE from DownloadManager while saving attachment");
                    throw new IOException(e);
                }
            } else {
                LogUtils.w(Logging.LOG_TAG,
                        "Trying to save an attachment without external storage?");
                throw new IOException();
            }
            // Update the attachment
            cv.put(AttachmentColumns.SIZE, size);
            cv.put(AttachmentColumns.UI_STATE, UIProvider.UIPROVIDER_ATTACHMENTSTATE_SAVED);
            // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_S
            //Note:we have saved the attachment to sd card,so should update the attachment destination external
            cv.put(AttachmentColumns.UI_DESTINATION,UIProvider.UIPROVIDER_ATTACHMENTDESTINATION_EXTERNAL);
            // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_E
        } catch (IOException e) {
            // Handle failures here...
            LogUtils.e(Logging.LOG_TAG,"IOException while save an attachment to external storage");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtils.e(Logging.LOG_TAG,"ioexception while close the stream");
            }
        }
        // TS: Gantao 2015-07-29 EMAIL BUGFIX-1055568 MOD_E
      //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_871936 MOD_S
//        context.getContentResolver().update(uri, cv, null, null);
        if(cv.size() > 0){
            context.getContentResolver().update(uri, cv, null, null);
        }
      //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_871936 MOD_E
      //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_S
        if (contentUri != null && attachment.mContentId != null && attachment.mContentId.length() > 0) {
            Body body = Body.restoreBodyWithMessageId(context, attachment.mMessageKey);
            if (body != null && body.mHtmlContent != null) {
                cv.clear();
                String html = body.mHtmlContent;
                String contentIdRe =
                        "\\s+(?i)src=\"cid(?-i):\\Q" + attachment.mContentId + "\\E\"";
                String srcContentUri = " src=\"" + contentUri + "\"";
                //TS: zhaotianyong 2015-04-15 EMAIL BUGFIX_976967 MOD_S
                try {
                    html = html.replaceAll(contentIdRe, srcContentUri);
                } catch (PatternSyntaxException e) {
                    LogUtils.w(Logging.LOG_TAG,
                            "Unrecognized backslash escape sequence in pattern");
                }
                //TS: zhaotianyong 2015-04-15 EMAIL BUGFIX_976967 MOD_E
                cv.put(BodyColumns.HTML_CONTENT, html);
                Body.updateBodyWithMessageId(context, attachment.mMessageKey, cv);
                Body.restoreBodyHtmlWithMessageId(context, attachment.mMessageKey);
            }
        }
      //TS: wenggangjin 2014-12-11 EMAIL BUGFIX_868520 MOD_E
    }
    //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan

    //TS: zhaotianyong 2015-04-14 EMAIL BUGFIX_962560 ADD_S
    public static String refactorHtmlBody(String html, Attachment att) {
        if (!TextUtils.isEmpty(html) && !TextUtils.isEmpty(att.mContentId)
                && !TextUtils.isEmpty(att.getContentUri())) {
            String contentIdRe = " src=\"cid:" + att.mContentId + "\"";
            String srcContentUri = " src=\"" + att.getContentUri() + "\"";
            html = html.replaceAll(srcContentUri, contentIdRe);
        }
        return html;
    }
    //TS: zhaotianyong 2015-04-14 EMAIL BUGFIX_962560 ADD_E
}
