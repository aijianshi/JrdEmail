/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.tct.email.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils; //MODIFIED by Yanhua.chen, 2016-04-06,BUG-1913233

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.utility.AttachmentUtilities;
import com.tct.emailcommon.utility.AttachmentUtilities.Columns;
import com.tct.mail.utils.IOUtils;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.MatrixCursorWithCachedColumns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/*
 * A simple ContentProvider that allows file access to Email's attachments.
 *
 * The URI scheme is as follows.  For raw file access:
 *   content://com.tct.mail.attachmentprovider/acct#/attach#/RAW
 *
 * And for access to thumbnails:
 *   content://com.tct.mail.attachmentprovider/acct#/attach#/THUMBNAIL/width#/height#
 *
 * The on-disk (storage) schema is as follows.
 *
 * Attachments are stored at:  <database-path>/account#.db_att/item#
 * Thumbnails are stored at:   <cache-path>/thmb_account#_item#
 *
 * Using the standard application context, account #10 and attachment # 20, this would be:
 *      /data/data/com.tct.email/databases/10.db_att/20
 *      /data/data/com.tct.email/cache/thmb_10_20
 */
public class AttachmentProvider extends ContentProvider {

    private static final String[] MIME_TYPE_PROJECTION = new String[] {
            AttachmentColumns.MIME_TYPE, AttachmentColumns.FILENAME };
    private static final int MIME_TYPE_COLUMN_MIME_TYPE = 0;
    private static final int MIME_TYPE_COLUMN_FILENAME = 1;

    private static final String[] PROJECTION_QUERY = new String[] { AttachmentColumns.FILENAME,
            AttachmentColumns.SIZE, AttachmentColumns.CONTENT_URI };

    @Override
    public boolean onCreate() {
        /*
         * We use the cache dir as a temporary directory (since Android doesn't give us one) so
         * on startup we'll clean up any .tmp files from the last run.
         */

        final File[] files = getContext().getCacheDir().listFiles();
        if (files != null) {
            for (File file : files) {
                final String filename = file.getName();
                if (filename.endsWith(".tmp") || filename.startsWith("thmb_")) {
                    file.delete();
                }
            }
        }
        return true;
    }

    /**
     * Returns the mime type for a given attachment.  There are three possible results:
     *  - If thumbnail Uri, always returns "image/png" (even if there's no attachment)
     *  - If the attachment does not exist, returns null
     *  - Returns the mime type of the attachment
     */
    @Override
    public String getType(Uri uri) {
        long callingId = Binder.clearCallingIdentity();
        try {
            List<String> segments = uri.getPathSegments();
            String id = segments.get(1);
            String format = segments.get(2);
            if (AttachmentUtilities.FORMAT_THUMBNAIL.equals(format)) {
                return "image/png";
            } else {
                uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, Long.parseLong(id));
                Cursor c = getContext().getContentResolver().query(uri, MIME_TYPE_PROJECTION, null,
                        null, null);
                try {
                    if (c.moveToFirst()) {
                        String mimeType = c.getString(MIME_TYPE_COLUMN_MIME_TYPE);
                        String fileName = c.getString(MIME_TYPE_COLUMN_FILENAME);
                        mimeType = AttachmentUtilities.inferMimeType(fileName, mimeType);
                        return mimeType;
                    }
                } finally {
                    c.close();
                }
                return null;
            }
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    /**
     * Open an attachment file.  There are two "formats" - "raw", which returns an actual file,
     * and "thumbnail", which attempts to generate a thumbnail image.
     *
     * Thumbnails are cached for easy space recovery and cleanup.
     *
     * TODO:  The thumbnail format returns null for its failure cases, instead of throwing
     * FileNotFoundException, and should be fixed for consistency.
     *
     *  @throws FileNotFoundException
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        // If this is a write, the caller must have the EmailProvider permission, which is
        // based on signature only
        if (mode.equals("w")) {
            Context context = getContext();
            if (context.checkCallingOrSelfPermission(EmailContent.PROVIDER_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED) {
                throw new FileNotFoundException();
            }
            List<String> segments = uri.getPathSegments();
            String accountId = segments.get(0);
            String id = segments.get(1);
            File saveIn =
                AttachmentUtilities.getAttachmentDirectory(context, Long.parseLong(accountId));
            if (!saveIn.exists()) {
                saveIn.mkdirs();
            }
            File newFile = new File(saveIn, id);
            return ParcelFileDescriptor.open(
                    newFile, ParcelFileDescriptor.MODE_READ_WRITE |
                        ParcelFileDescriptor.MODE_CREATE | ParcelFileDescriptor.MODE_TRUNCATE);
        }
        long callingId = Binder.clearCallingIdentity();
        try {
            List<String> segments = uri.getPathSegments();
            String accountId = segments.get(0);
            String id = segments.get(1);
            String format = segments.get(2);
            if (AttachmentUtilities.FORMAT_THUMBNAIL.equals(format)) {
                int width = Integer.parseInt(segments.get(3));
                int height = Integer.parseInt(segments.get(4));
                String filename = "thmb_" + accountId + "_" + id;
                File dir = getContext().getCacheDir();
                File file = new File(dir, filename);
                if (!file.exists()) {
                    Uri attachmentUri = AttachmentUtilities.
                        getAttachmentUri(Long.parseLong(accountId), Long.parseLong(id));
                    Cursor c = query(attachmentUri,
                            new String[] { Columns.DATA }, null, null, null);
                    if (c != null) {
                        try {
                            if (c.moveToFirst()) {
                                attachmentUri = Uri.parse(c.getString(0));
                            } else {
                                return null;
                            }
                        } finally {
                            c.close();
                        }
                    }
                    String type = getContext().getContentResolver().getType(attachmentUri);
                    try {
                        InputStream in =
                            getContext().getContentResolver().openInputStream(attachmentUri);
                        Bitmap thumbnail = createThumbnail(type, in);
                        if (thumbnail == null) {
                            return null;
                        }
                        thumbnail = Bitmap.createScaledBitmap(thumbnail, width, height, true);
                        FileOutputStream out = new FileOutputStream(file);
                        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.close();
                        in.close();
                    } catch (IOException ioe) {
                        LogUtils.d(Logging.LOG_TAG, "openFile/thumbnail failed with " +
                                ioe.getMessage());
                        return null;
                    } catch (OutOfMemoryError oome) {
                        LogUtils.d(Logging.LOG_TAG, "openFile/thumbnail failed with " +
                                oome.getMessage());
                        return null;
                    }
                }
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            }
            else {
                //TS: jin.dong 2016-2-25 EMAIL BUGFIX_1657658 ADD_S
                File file =  new File(getContext().getDatabasePath(accountId + ".db_att"), id);
                if (!file.exists()) {
                    Uri attachmentUri = AttachmentUtilities.
                            getAttachmentUri(Long.parseLong(accountId), Long.parseLong(id));
                    Uri contentUri = null;//TS: zheng.zou 2016-3-2 EMAIL BUGFIX_1715818 ADD
                    Cursor c = query(attachmentUri,
                            new String[]{AttachmentColumns.CONTENT_URI}, null, null, null);
                    if (c != null) {
                        try {
                            if (c.moveToFirst()) {//TS: zheng.zou 2016-3-1 EMAIL BUGFIX_1714392 MOD
                                //TS: yanhua.chen 2016-4-6 EMAIL BUGFIX_1913233 MOD
                                String url = c.getString(0);
                                if (TextUtils.isEmpty(url)) {
                                    LogUtils.e(Logging.LOG_TAG, "openFile failed,the file uri is null ");
                                    return null;
                                }
                                //TS: yanhua.chen 2016-4-6 EMAIL BUGFIX_1913233 MOD
                                contentUri = Uri.parse(url); //TS: zheng.zou 2016-3-2 EMAIL BUGFIX_1715818 MOD
                            } else {
                                return null;
                            }
                        } finally {
                            c.close();
                        }
                    }
                    //TS: zheng.zou 2016-3-2 EMAIL BUGFIX_1715818 ADD_S
                    if (contentUri == null || contentUri.equals(attachmentUri)) {
                        return null;
                    }
                    //TS: zheng.zou 2016-3-2 EMAIL BUGFIX_1715818 ADD_E
                    String filename = "att_" + accountId + "_" + id;
                    File dir = getContext().getCacheDir();
                    File cacheFile = new File(dir, filename);
                    if (!cacheFile.exists()) {
                        FileOutputStream out = null;
                        InputStream in = null;
                        try {
                            in = getContext().getContentResolver().openInputStream(contentUri); //TS: zheng.zou 2016-3-2 EMAIL BUGFIX_1715818 MOD
                            out = new FileOutputStream(file);
                            IOUtils.copy(in, out);
                            out.flush();
                        } catch (IOException ioe) {
                            LogUtils.d(Logging.LOG_TAG, "openFile failed with " +
                                    ioe.getMessage());
                            return null;
                        } catch (OutOfMemoryError oome) {
                            LogUtils.d(Logging.LOG_TAG, "openFile failed with " +
                                    oome.getMessage());
                            return null;
                        } catch (SecurityException e){
                            LogUtils.d(Logging.LOG_TAG, "SecurityException opening file with " +
                                    e.getMessage());
                            return null;
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                                if (in != null) {
                                    in.close();
                                }
                            } catch (IOException e) {
                                LogUtils.e(LogUtils.TAG, "IOException while close the stream");
                            }
                        }

                    }
                    return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                }
                //TS: jin.dong 2016-2-25 EMAIL BUGFIX_1657658 ADD_E
                return ParcelFileDescriptor.open(
                        file,//TS: jin.dong 2016-2-25 EMAIL BUGFIX_1657658 MOD
                        ParcelFileDescriptor.MODE_READ_ONLY);
            }
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    @Override
    public int delete(Uri uri, String arg1, String[] arg2) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    /**
     * Returns a cursor based on the data in the attachments table, or null if the attachment
     * is not recorded in the table.
     *
     * Supports REST Uri only, for a single row - selection, selection args, and sortOrder are
     * ignored (non-null values should probably throw an exception....)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        long callingId = Binder.clearCallingIdentity();
        try {
            if (projection == null) {
                projection =
                    new String[] {
                        Columns._ID,
                        Columns.DATA,
                };
            }

            List<String> segments = uri.getPathSegments();
            String accountId = segments.get(0);
            String id = segments.get(1);
            String format = segments.get(2);
            String name = null;
            int size = -1;
            String contentUri = null;

            uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, Long.parseLong(id));
            Cursor c = getContext().getContentResolver().query(uri, PROJECTION_QUERY,
                    null, null, null);
            try {
                if (c.moveToFirst()) {
                    name = c.getString(0);
                    size = c.getInt(1);
                    contentUri = c.getString(2);
                } else {
                    return null;
                }
            } finally {
                c.close();
            }

            MatrixCursor ret = new MatrixCursorWithCachedColumns(projection);
            Object[] values = new Object[projection.length];
            for (int i = 0, count = projection.length; i < count; i++) {
                String column = projection[i];
                if (Columns._ID.equals(column)) {
                    values[i] = id;
                }
                else if (Columns.DATA.equals(column)) {
                    values[i] = contentUri;
                }
                else if (Columns.DISPLAY_NAME.equals(column)) {
                    values[i] = name;
                }
                else if (Columns.SIZE.equals(column)) {
                    values[i] = size;
                }  //TS: jin.dong 2016-2-25 EMAIL BUGFIX_1657658 ADD_S
                else if (AttachmentColumns.CONTENT_URI.endsWith(column)){
                    values[i] = contentUri;
                }
                //TS: jin.dong 2016-2-25 EMAIL BUGFIX_1657658 ADD_E
            }
            ret.addRow(values);
            return ret;
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static Bitmap createThumbnail(String type, InputStream data) {
        if(MimeUtility.mimeTypeMatches(type, "image/*")) {
            return createImageThumbnail(data);
        }
        return null;
    }

    private static Bitmap createImageThumbnail(InputStream data) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(data);
            return bitmap;
        } catch (OutOfMemoryError oome) {
            LogUtils.d(Logging.LOG_TAG, "createImageThumbnail failed with " + oome.getMessage());
            return null;
        } catch (Exception e) {
            LogUtils.d(Logging.LOG_TAG, "createImageThumbnail failed with " + e.getMessage());
            return null;
        }
    }

    /**
     * Need this to suppress warning in unit tests.
     */
    @Override
    public void shutdown() {
        // Don't call super.shutdown(), which emits a warning...
    }
}
