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
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/14/2014|     Chao Zhang       |      FR 635028       |[Email]Download   */
/*           |                      |porting from(FR472914)|options to be im- */
/*           |                      |                      |plemented         */
/* ----------|----------------------|----------------------|----------------- */
/* 04/22/2014|     Chao Zhang       |      FR 631895 	   |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

/*
==========================================================================
*HISTORY
*
*Tag              Date         Author          Description
*============== ============ =============== ==============================
*CONFLICT-50016  2014/10/24   zhaotianyong     Modify the package conflict
*BUGFIX-871936  2014/12/15   wenggangjin     [Android5.0][Email][Crash]Email crashs after selecting Save in attachment
*BUGFIX-971901  2015/4/16    yanhua.chen     [Android5.0][Email][UE]The toast when we forward an email will last for a long time
*CR_585337      2015-09-16  chao.zhang       Exchange Email resend mechanism
*BUGFIX-971901  2016/03/10   junwei-xu       [Idol3 5.5 M upgrade][Monitor][Force close][Email]Exchange services force close when send a mail with attachments and priority is High.
============================================================================
*/
package com.tct.emailcommon.utility;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.Policy;
import com.tct.emailcommon.provider.ProviderUnavailableException;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.provider.EmailContent.HostAuthColumns;
import com.tct.emailcommon.provider.EmailContent.Message;
//TS: MOD by zhaotianyong for CONFLICT_50016 START
import com.tct.emailcommon.service.PolicyServiceProxy;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.mail.utils.LogUtils;
//import com.google.common.annotations.VisibleForTesting;
//TS: MOD by zhaotianyong for CONFLICT_50016 END

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ThreadLocal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/22/2014,FR 631895(porting from  FR487417)
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
//[FEATURE]-Add-END by TSCD.chao zhang


public class Utility {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ASCII = Charset.forName("US-ASCII");

    public static final String[] EMPTY_STRINGS = new String[0];

    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
    public static final int ALWAYS_SHOW = 0;
    public static final int ASK_BEFORE_SHOWING = 1;
    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_E
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    public static final int ENTIRE_MAIL = -1;
    public static final int HEAD_ONLY = 0;
    /*
     * according arrays.xml Email_IMAP_Download_Options_values and
     * Email_POP3_Download_Options_values,
     */
    public static final int POP3_DOWNLOAD_ALL = 1;
    public static final int DOWNLOAD_ALL = 1;  // If it support size option, this value should be 7
    public static final int DOWNLOAD_DEFAULT_SIZE = 20480;
    //[FEATURE]-Add-END by TSCD.Chao Zhang
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    public static final int MAX_RETRY_SENDING_TIMES = 5;
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    // "GMT" + "+" or "-" + 4 digits
    private static final Pattern DATE_CLEANUP_PATTERN_WRONG_TIMEZONE =
            Pattern.compile("GMT([-+]\\d{4})$");

    private static Handler sMainThreadHandler;

  //TS: Gantao 2015-09-28 EMAIL FEATURE_526529 ADD_S
    // 1 indicates an inline attachment, 0 indicates a standard attachment
    public static final int STANDARD_ATTACHMENT = 0;
    public static final int INLINE_ATTACHMENT = 1;
  //TS: Gantao 2015-09-28 EMAIL FEATURE_526529 ADD_E

  //[BUGFIX]-MOD BEGIN by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
    //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_971901 ADD_S
 /*   private static Toast toast = null;
    public static void showTextToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }*/
    //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_971901 ADD_E

    public static void showShortToast(Context context, int resId) {
        showShortToast(context, context.getResources().getString(resId));
    }

    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param message Message to show.
     */
    public static void showShortToast(final Context context, final String message) {
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //[BUGFIX]-MOD END by SCDTABLET.shujing.jin@tcl.com

    /**
     * @return a {@link Handler} tied to the main thread.
     */
    public static Handler getMainThreadHandler() {
        if (sMainThreadHandler == null) {
            // No need to synchronize -- it's okay to create an extra Handler, which will be used
            // only once and then thrown away.
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return sMainThreadHandler;
    }

    public static boolean arrayContains(Object[] a, Object o) {
        int index = arrayIndex(a, o);
        return (index >= 0);
    }

    public static int arrayIndex(Object[] a, Object o) {
        for (int i = 0, count = a.length; i < count; i++) {
            if (a[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a concatenated string containing the output of every Object's
     * toString() method, each separated by the given separator character.
     */
    public static String combine(Object[] parts, char separator) {
        if (parts == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i].toString());
            if (i < parts.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static boolean isPortFieldValid(TextView view) {
        CharSequence chars = view.getText();
        if (TextUtils.isEmpty(chars)) return false;
        Integer port;
        // In theory, we can't get an illegal value here, since the field is monitored for valid
        // numeric input. But this might be used elsewhere without such a check.
        try {
            port = Integer.parseInt(chars.toString());
        } catch (NumberFormatException e) {
            return false;
        }
        return port > 0 && port < 65536;
    }

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/22/2014,FR 631895(porting from  FR487417)
    public static boolean hasConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            DetailedState state = info.getDetailedState();
            if (state == DetailedState.CONNECTED) {
                return true;
            }
        }
        return false;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_S
    public static boolean canAutoSync(Context context, Account account) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // Enforce manual sync only while roaming here
        long policyKey = account.mPolicyKey;
        // Quick exit from this check
        if ((policyKey != 0) && (networkInfo != null) &&
                isNetworkTypeMobile(networkInfo.getType())) {
            // We'll cache the Policy data here
            Policy policy = account.mPolicy;
//            if (policy == null) {
//                policy = Policy.restorePolicyWithId(context, policyKey);
//                account.mPolicy = policy;
//                if (!PolicyServiceProxy.isActive(context, policy)) {
//                    PolicyServiceProxy.setAccountHoldFlag(context, account, true);
//                    return false;
//                }
//            }
            if (policy != null && policy.mRequireManualSyncWhenRoaming && networkInfo.isRoaming()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isRoaming(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && isNetworkTypeMobile(networkInfo.getType())
                && networkInfo.isRoaming();
    }

    /**
     * Taken from ConnectivityManager using public constants
     */
    public static boolean isNetworkTypeMobile(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
                return true;
            default:
                return false;
        }
    }
    //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_E

    /**
     * Validate a hostname name field.
     *
     * Because we just use the {@link URI} class for validation, it'll accept some invalid
     * host names, but it works well enough...
     */
    public static boolean isServerNameValid(TextView view) {
        return isServerNameValid(view.getText().toString());
    }

    public static boolean isServerNameValid(String serverName) {
        serverName = serverName.trim();
        if (TextUtils.isEmpty(serverName)) {
            return false;
        }
        try {
            new URI(
                    "http",
                    null,
                    serverName,
                    -1,
                    null, // path
                    null, // query
                    null);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private final static String HOSTAUTH_WHERE_CREDENTIALS = HostAuthColumns.ADDRESS + " like ?"
            + " and " + HostAuthColumns.LOGIN + " like ?  ESCAPE '\\'"
            + " and " + HostAuthColumns.PROTOCOL + " not like \"smtp\"";
    private final static String ACCOUNT_WHERE_HOSTAUTH = AccountColumns.HOST_AUTH_KEY_RECV + "=?";

    /**
     * Look for an existing account with the same username & server
     *
     * @param context a system context
     * @param allowAccountId this account Id will not trigger (when editing an existing account)
     * @param hostName the server's address
     * @param userLogin the user's login string
     * @return null = no matching account found.  Account = matching account
     */
    public static Account findExistingAccount(Context context, long allowAccountId,
            String hostName, String userLogin) {
        ContentResolver resolver = context.getContentResolver();
        String userName = userLogin.replace("_", "\\_");
        Cursor c = resolver.query(HostAuth.CONTENT_URI, HostAuth.ID_PROJECTION,
                HOSTAUTH_WHERE_CREDENTIALS, new String[] { hostName, userName }, null);
        if (c == null) throw new ProviderUnavailableException();
        try {
            while (c.moveToNext()) {
                long hostAuthId = c.getLong(HostAuth.ID_PROJECTION_COLUMN);
                // Find account with matching hostauthrecv key, and return it
                Cursor c2 = resolver.query(Account.CONTENT_URI, Account.ID_PROJECTION,
                        ACCOUNT_WHERE_HOSTAUTH, new String[] { Long.toString(hostAuthId) }, null);
                try {
                    while (c2.moveToNext()) {
                        long accountId = c2.getLong(Account.ID_PROJECTION_COLUMN);
                        if (accountId != allowAccountId) {
                            Account account = Account.restoreAccountWithId(context, accountId);
                            if (account != null) {
                                return account;
                            }
                        }
                    }
                } finally {
                    c2.close();
                }
            }
        } finally {
            c.close();
        }

        return null;
    }

    private static class ThreadLocalDateFormat extends ThreadLocal<SimpleDateFormat> {
        private final String mFormatStr;

        public ThreadLocalDateFormat(String formatStr) {
            mFormatStr = formatStr;
        }

        @Override
        protected SimpleDateFormat initialValue() {
            final SimpleDateFormat format = new SimpleDateFormat(mFormatStr);
            final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            format.setCalendar(cal);
            return format;
        }

        public Date parse(String date) throws ParseException {
            return super.get().parse(date);
        }
    }

    /**
     * Generate a time in milliseconds from a date string that represents a date/time in GMT
     * @param date string in format 20090211T180303Z (rfc2445, iCalendar).
     * @return the time in milliseconds (since Jan 1, 1970)
     */
    public static long parseDateTimeToMillis(String date) throws ParseException {
        return parseDateTimeToCalendar(date).getTimeInMillis();
    }

    private static final ThreadLocalDateFormat mFullDateTimeFormat =
        new ThreadLocalDateFormat("yyyyMMdd'T'HHmmss'Z'");

    private static final ThreadLocalDateFormat mAbbrevDateTimeFormat =
        new ThreadLocalDateFormat("yyyyMMdd");

    /**
     * Generate a GregorianCalendar from a date string that represents a date/time in GMT
     * @param date string in format 20090211T180303Z (rfc2445, iCalendar), or
     *             in abbreviated format 20090211.
     * @return the GregorianCalendar
     */
    @VisibleForTesting
    public static GregorianCalendar parseDateTimeToCalendar(String date) throws ParseException {
        final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        if (date.length() <= 8) {
            cal.setTime(mAbbrevDateTimeFormat.parse(date));
        } else {
            cal.setTime(mFullDateTimeFormat.parse(date));
        }
        return cal;
    }

    private static final ThreadLocalDateFormat mAbbrevEmailDateTimeFormat =
        new ThreadLocalDateFormat("yyyy-MM-dd");

    private static final ThreadLocalDateFormat mEmailDateTimeFormat =
        new ThreadLocalDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final ThreadLocalDateFormat mEmailDateTimeFormatWithMillis =
        new ThreadLocalDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Generate a time in milliseconds from an email date string that represents a date/time in GMT
     * @param date string in format 2010-02-23T16:00:00.000Z (ISO 8601, rfc3339)
     * @return the time in milliseconds (since Jan 1, 1970)
     */
    @VisibleForTesting
    public static long parseEmailDateTimeToMillis(String date) throws ParseException {
        final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        if (date.length() <= 10) {
            cal.setTime(mAbbrevEmailDateTimeFormat.parse(date));
        } else if (date.length() <= 20) {
            cal.setTime(mEmailDateTimeFormat.parse(date));
        } else {
            cal.setTime(mEmailDateTimeFormatWithMillis.parse(date));
        }
        return cal.getTimeInMillis();
    }

    private static byte[] encode(Charset charset, String s) {
        if (s == null) {
            return null;
        }
        final ByteBuffer buffer = charset.encode(CharBuffer.wrap(s));
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    private static String decode(Charset charset, byte[] b) {
        if (b == null) {
            return null;
        }
        final CharBuffer cb = charset.decode(ByteBuffer.wrap(b));
        return new String(cb.array(), 0, cb.length());
    }

    /** Converts a String to UTF-8 */
    public static byte[] toUtf8(String s) {
        return encode(UTF_8, s);
    }

    /** Builds a String from UTF-8 bytes */
    public static String fromUtf8(byte[] b) {
        return decode(UTF_8, b);
    }

    /** Converts a String to ASCII bytes */
    public static byte[] toAscii(String s) {
        return encode(ASCII, s);
    }

    /** Builds a String from ASCII bytes */
    public static String fromAscii(byte[] b) {
        return decode(ASCII, b);
    }

    /**
     * @return true if the input is the first (or only) byte in a UTF-8 character
     */
    public static boolean isFirstUtf8Byte(byte b) {
        // If the top 2 bits is '10', it's not a first byte.
        return (b & 0xc0) != 0x80;
    }

    public static String byteToHex(int b) {
        return byteToHex(new StringBuilder(), b).toString();
    }

    public static StringBuilder byteToHex(StringBuilder sb, int b) {
        b &= 0xFF;
        sb.append("0123456789ABCDEF".charAt(b >> 4));
        sb.append("0123456789ABCDEF".charAt(b & 0xF));
        return sb;
    }

    public static String replaceBareLfWithCrlf(String str) {
        return str.replace("\r", "").replace("\n", "\r\n");
    }

    /**
     * Cancel an {@link AsyncTask}.  If it's already running, it'll be interrupted.
     */
    public static void cancelTaskInterrupt(AsyncTask<?, ?, ?> task) {
        cancelTask(task, true);
    }

    /**
     * Cancel an {@link AsyncTask}.
     *
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *        task should be interrupted; otherwise, in-progress tasks are allowed
     *        to complete.
     */
    public static void cancelTask(AsyncTask<?, ?, ?> task, boolean mayInterruptIfRunning) {
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(mayInterruptIfRunning);
        }
    }

    public static String getSmallHash(final String value) {
        final MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException impossible) {
            return null;
        }
        sha.update(Utility.toUtf8(value));
        final int hash = getSmallHashFromSha1(sha.digest());
        return Integer.toString(hash);
    }

    /**
     * @return a non-negative integer generated from 20 byte SHA-1 hash.
     */
    /* package for testing */ static int getSmallHashFromSha1(byte[] sha1) {
        final int offset = sha1[19] & 0xf; // SHA1 is 20 bytes.
        return ((sha1[offset]  & 0x7f) << 24)
                | ((sha1[offset + 1] & 0xff) << 16)
                | ((sha1[offset + 2] & 0xff) << 8)
                | ((sha1[offset + 3] & 0xff));
    }

    /**
     * Try to make a date MIME(RFC 2822/5322)-compliant.
     *
     * It fixes:
     * - "Thu, 10 Dec 09 15:08:08 GMT-0700" to "Thu, 10 Dec 09 15:08:08 -0700"
     *   (4 digit zone value can't be preceded by "GMT")
     *   We got a report saying eBay sends a date in this format
     */
    public static String cleanUpMimeDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return date;
        }
        date = DATE_CLEANUP_PATTERN_WRONG_TIMEZONE.matcher(date).replaceFirst("$1");
        return date;
    }

    public static ByteArrayInputStream streamFromAsciiString(String ascii) {
        return new ByteArrayInputStream(toAscii(ascii));
    }

    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param resId Resource ID of the message string.
     */
    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param message Message to show.
     */
    public static void showToast(final Context context, final String message) {
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Run {@code r} on a worker thread, returning the AsyncTask
     * @return the AsyncTask; this is primarily for use by unit tests, which require the
     * result of the task
     *
     * @deprecated use {@link EmailAsyncTask#runAsyncParallel} or
     *     {@link EmailAsyncTask#runAsyncSerial}
     */
    @Deprecated
    public static AsyncTask<Void, Void, Void> runAsync(final Runnable r) {
        return new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... params) {
                r.run();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Interface used in {@link #createUniqueFile} instead of {@link File#createNewFile()} to make
     * it testable.
     */
    /* package */ interface NewFileCreator {
        public static final NewFileCreator DEFAULT = new NewFileCreator() {
                    @Override public boolean createNewFile(File f) throws IOException {
                        return f.createNewFile();
                    }
        };
        public boolean createNewFile(File f) throws IOException ;
    }

    /**
     * Creates a new empty file with a unique name in the given directory by appending a hyphen and
     * a number to the given filename.
     *
     * @return a new File object, or null if one could not be created
     */
    public static File createUniqueFile(File directory, String filename) throws IOException {
        return createUniqueFileInternal(NewFileCreator.DEFAULT, directory, filename);
    }

    /* package */ static File createUniqueFileInternal(final NewFileCreator nfc,
            final File directory, final String filename) throws IOException {
        final File file = new File(directory, filename);
        if (nfc.createNewFile(file)) {
            return file;
        }
        // Get the extension of the file, if any.
        final int index = filename.lastIndexOf('.');
        final String name;
        final String extension;
        if (index != -1) {
            name = filename.substring(0, index);
            extension = filename.substring(index);
        } else {
            name = filename;
            extension = "";
        }

        for (int i = 2; i < Integer.MAX_VALUE; i++) {
            final File numberedFile =
                    new File(directory, name + "-" + Integer.toString(i) + extension);
            if (nfc.createNewFile(numberedFile)) {
                return numberedFile;
            }
        }
        return null;
    }

    public interface CursorGetter<T> {
        T get(Cursor cursor, int column);
    }

    private static final CursorGetter<Long> LONG_GETTER = new CursorGetter<Long>() {
        @Override
        public Long get(Cursor cursor, int column) {
            return cursor.getLong(column);
        }
    };

    private static final CursorGetter<Integer> INT_GETTER = new CursorGetter<Integer>() {
        @Override
        public Integer get(Cursor cursor, int column) {
            return cursor.getInt(column);
        }
    };

    private static final CursorGetter<String> STRING_GETTER = new CursorGetter<String>() {
        @Override
        public String get(Cursor cursor, int column) {
            return cursor.getString(column);
        }
    };

    private static final CursorGetter<byte[]> BLOB_GETTER = new CursorGetter<byte[]>() {
        @Override
        public byte[] get(Cursor cursor, int column) {
            return cursor.getBlob(column);
        }
    };

    /**
     * @return if {@code original} is to the EmailProvider, add "?limit=1".  Otherwise just returns
     * {@code original}.
     *
     * Other providers don't support the limit param.  Also, changing URI passed from other apps
     * can cause permission errors.
     */
    /* package */ static Uri buildLimitOneUri(Uri original) {
        if ("content".equals(original.getScheme()) &&
                EmailContent.AUTHORITY.equals(original.getAuthority())) {
            return EmailContent.uriWithLimit(original, 1);
        }
        return original;
    }

    /**
     * @return a generic in column {@code column} of the first result row, if the query returns at
     * least 1 row.  Otherwise returns {@code defaultValue}.
     */
    public static <T> T getFirstRowColumn(Context context, Uri uri,
            String[] projection, String selection, String[] selectionArgs, String sortOrder,
            int column, T defaultValue, CursorGetter<T> getter) {
        // Use PARAMETER_LIMIT to restrict the query to the single row we need
        uri = buildLimitOneUri(uri);
        Cursor c = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                sortOrder);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return getter.get(c, column);
                }
            } finally {
                c.close();
            }
        }
        return defaultValue;
    }

    /**
     * {@link #getFirstRowColumn} for a Long with null as a default value.
     */
    public static Long getFirstRowLong(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column) {
        return getFirstRowColumn(context, uri, projection, selection, selectionArgs,
                sortOrder, column, null, LONG_GETTER);
    }

    /**
     * {@link #getFirstRowColumn} for a Long with a provided default value.
     */
    public static Long getFirstRowLong(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column,
            Long defaultValue) {
        return getFirstRowColumn(context, uri, projection, selection, selectionArgs,
                sortOrder, column, defaultValue, LONG_GETTER);
    }

    /**
     * {@link #getFirstRowColumn} for an Integer with null as a default value.
     */
    public static Integer getFirstRowInt(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column) {
        return getFirstRowColumn(context, uri, projection, selection, selectionArgs,
                sortOrder, column, null, INT_GETTER);
    }

    /**
     * {@link #getFirstRowColumn} for an Integer with a provided default value.
     */
    public static Integer getFirstRowInt(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column,
            Integer defaultValue) {
        return getFirstRowColumn(context, uri, projection, selection, selectionArgs,
                sortOrder, column, defaultValue, INT_GETTER);
    }

    /**
     * {@link #getFirstRowColumn} for a String with null as a default value.
     */
    public static String getFirstRowString(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column) {
        return getFirstRowString(context, uri, projection, selection, selectionArgs, sortOrder,
                column, null);
    }

    /**
     * {@link #getFirstRowColumn} for a String with a provided default value.
     */
    public static String getFirstRowString(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column,
            String defaultValue) {
        return getFirstRowColumn(context, uri, projection, selection, selectionArgs,
                sortOrder, column, defaultValue, STRING_GETTER);
    }

    /**
     * {@link #getFirstRowColumn} for a byte array with a provided default value.
     */
    public static byte[] getFirstRowBlob(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int column,
            byte[] defaultValue) {
        return getFirstRowColumn(context, uri, projection, selection, selectionArgs, sortOrder,
                column, defaultValue, BLOB_GETTER);
    }

    public static boolean attachmentExists(Context context, Attachment attachment) {
      //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_871936 MOD_S
//        if (attachment == null) {
        if (attachment == null || attachment.mFileName == null || attachment.mFileName.length() <= 0) {
          //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_871936 MOD_E
            return false;
        } else if (attachment.mContentBytes != null) {
            return true;
        } else {
            final String cachedFile = attachment.getCachedFileUri();
            // Try the cached file first
            if (!TextUtils.isEmpty(cachedFile)) {
                final Uri cachedFileUri = Uri.parse(cachedFile);
                try {
                    final InputStream inStream =
                            context.getContentResolver().openInputStream(cachedFileUri);
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        // Nothing to be done if can't close the stream
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    // We weren't able to open the file, try the content uri below
                    LogUtils.e(LogUtils.TAG, e, "not able to open cached file");
                } catch (IllegalArgumentException e) {
                    //TS: junwei-xu 2016-03-10 EMAIL BUGFIX-1778317 ADD_S
                    //Note: sometimes, the cachedFileUri maybe illegal.
                    LogUtils.e(LogUtils.TAG, e, "not able to open cached file");
                    //TS: junwei-xu 2016-03-10 EMAIL BUGFIX-1778317 ADD_E
                }
            }
            final String contentUri = attachment.getContentUri();
            if (TextUtils.isEmpty(contentUri)) {
                return false;
            }
            try {
                final Uri fileUri = Uri.parse(contentUri);
                try {
                    final InputStream inStream =
                            context.getContentResolver().openInputStream(fileUri);
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        // Nothing to be done if can't close the stream
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    return false;
                }
            } catch (RuntimeException re) {
                LogUtils.w(LogUtils.TAG, re, "attachmentExists RuntimeException");
                return false;
            }
        }
    }

    /**
     * Check whether the message with a given id has unloaded attachments.  If the message is
     * a forwarded message, we look instead at the messages's source for the attachments.  If the
     * message or forward source can't be found, we return false
     * @param context the caller's context
     * @param messageId the id of the message
     * @return whether or not the message has unloaded attachments
     */
    public static boolean hasUnloadedAttachments(Context context, long messageId) {
        Message msg = Message.restoreMessageWithId(context, messageId);
        if (msg == null) return false;
        Attachment[] atts = Attachment.restoreAttachmentsWithMessageId(context, messageId);
        for (Attachment att: atts) {
            if (!attachmentExists(context, att)) {
                // If the attachment doesn't exist and isn't marked for download, we're in trouble
                // since the outbound message will be stuck indefinitely in the Outbox.  Instead,
                // we'll just delete the attachment and continue; this is far better than the
                // alternative.  In theory, this situation shouldn't be possible.
                if ((att.mFlags & (Attachment.FLAG_DOWNLOAD_FORWARD |
                        Attachment.FLAG_DOWNLOAD_USER_REQUEST)) == 0) {
                    LogUtils.d(LogUtils.TAG, "Unloaded attachment isn't marked for download: %s" +
                            ", #%d", att.mFileName, att.mId);
                    Account acct = Account.restoreAccountWithId(context, msg.mAccountKey);
                    if (acct == null) return true;
                    // If smart forward is set and the message is a forward, we'll act as though
                    // the attachment has been loaded
                    // In Email1 this test wasn't necessary, as the UI handled it...
                    if ((msg.mFlags & Message.FLAG_TYPE_FORWARD) != 0) {
                        if ((acct.mFlags & Account.FLAGS_SUPPORTS_SMART_FORWARD) != 0) {
                            continue;
                        }
                    }
                    Attachment.delete(context, Attachment.CONTENT_URI, att.mId);
                } else if (att.getContentUri() != null) {
                    // In this case, the attachment file is gone from the cache; let's clear the
                    // contentUri; this should be a very unusual case
                    ContentValues cv = new ContentValues();
                    cv.putNull(AttachmentColumns.CONTENT_URI);
                    Attachment.update(context, Attachment.CONTENT_URI, att.mId, cv);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method wrapping calls to retrieve columns from a single row, via EmailProvider.
     * The arguments are exactly the same as to contentResolver.query().  Results are returned in
     * an array of Strings corresponding to the columns in the projection.  If the cursor has no
     * rows, null is returned.
     */
    public static String[] getRowColumns(Context context, Uri contentUri, String[] projection,
            String selection, String[] selectionArgs) {
        String[] values = new String[projection.length];
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(contentUri, projection, selection, selectionArgs, null);
        try {
            if (c.moveToFirst()) {
                for (int i = 0; i < projection.length; i++) {
                    values[i] = c.getString(i);
                }
            } else {
                return null;
            }
        } finally {
            c.close();
        }
        return values;
    }

    /**
     * Convenience method for retrieving columns from a particular row in EmailProvider.
     * Passed in here are a base uri (e.g. Message.CONTENT_URI), the unique id of a row, and
     * a projection.  This method calls the previous one with the appropriate URI.
     */
    public static String[] getRowColumns(Context context, Uri baseUri, long id,
            String ... projection) {
        return getRowColumns(context, ContentUris.withAppendedId(baseUri, id), projection, null,
                null);
    }

    public static boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void enableStrictMode(boolean enabled) {
        StrictMode.setThreadPolicy(enabled
                ? new StrictMode.ThreadPolicy.Builder().detectAll().build()
                : StrictMode.ThreadPolicy.LAX);
        StrictMode.setVmPolicy(enabled
                ? new StrictMode.VmPolicy.Builder().detectAll().build()
                : StrictMode.VmPolicy.LAX);
    }
}
