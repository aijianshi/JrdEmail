/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-723337   2015-10-14   zheng.zou      [Email]Print test log to sd card
 *BUGFIX-1005115   2015-12-02  chao-zhang     [Android 6.0][Email][Monkey][ANR][Monitor]ANR NOT RESPONDING happened during monkey test
 ============================================================================
 */
package com.tct.mail.utils;

import android.os.Environment;
import android.os.Process;
import android.text.format.DateUtils;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by user on 15-9-29
 * File utils class
 */
public class FileUtils {
    private static final String PATH = Environment.getExternalStorageDirectory() + "/Android/data/com.tct.email/files/";
    private static final String FILE_TIME_FORMAT = "yyyy-MM-dd";
    private static final String LOG_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
    //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD_S
    private static final String SUFIX_TXT = ".txt";
    private static final String SUFIX_ZIP = ".zip";
    private static final int BUFFER_SIZE = 1024;
    private static final int LOG_EXPIRE_DAY = 3;  //log expire day, the log will be deleted if time exceed this day count
    //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD_E
    //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_S
    //NOTE:after M, have permission check, if no storage permission,lots of error printed,and it may cause FD hang
    private static final boolean DEBUG = false;
    //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_E

    /**
     * append log to the end of an exist file.
     * the logs in one day will be recorded in one file, with the name of current day.
     *
     * @param tag tag
     * @param format  format
     * @param args  args
     */
    public static void appendLog(String tag, String format, Object... args) {
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_S
        if (!DEBUG) {
            return;
        }
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_E
        //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD_S
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtils.e(LogUtils.TAG, "External storage is not mounted!");
            return;
        }
        //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD_E
        SimpleDateFormat df = new SimpleDateFormat(FILE_TIME_FORMAT);
        Date date = new Date(System.currentTimeMillis());
        String fileName = df.format(date);
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File logFile = new File(PATH + fileName + SUFIX_TXT);     //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 MOD
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        SimpleDateFormat df2 = new SimpleDateFormat(LOG_TIME_FORMAT);
        String time = df2.format(date);

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(time)
                    .append(" ")
                    .append(tag)
                    .append("(")
                    .append(String.valueOf(Process.myPid()))
                    .append("): ")
                    .append(String.format(format, args));
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * append log to the end of an exist file, with the stack trace of the Throwable
     * @param tag
     * @param throwable
     * @param format
     * @param args
     */
    public static void appendLogWithThrow(String tag, Throwable throwable, String format, Object... args) {
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_S
        if (!DEBUG) {
            return;
        }
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_E
        String stack = android.util.Log.getStackTraceString(throwable);
        appendLog(tag, format + stack, args);
    }

    /**
     * delete log files LOG_EXPIRE_DAY days ago
     */
    public static void cleanOldFile() {
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_S
        if (!DEBUG) {
            return;
        }
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_E
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            String time = name.substring(0, name.lastIndexOf("."));
            SimpleDateFormat sf = new SimpleDateFormat(FILE_TIME_FORMAT);
            //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 MOD_S
            try {
                Date date = sf.parse(time);
                long past = System.currentTimeMillis() - date.getTime();
                if (past > LOG_EXPIRE_DAY * DateUtils.DAY_IN_MILLIS) {
                    file.delete();
                } else if (past > DateUtils.DAY_IN_MILLIS && file.getName().endsWith(SUFIX_TXT)) {
                    zip(new String[]{file.getAbsolutePath()}, getZipPath(file.getAbsolutePath()));
                    file.delete();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 MOD_E
        }
    }

    //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD_S
    private static String getZipPath(String path) {
        return path.substring(0, path.lastIndexOf(".")) + SUFIX_ZIP;
    }

    /**
     * compact file to zip file
     * @param files  source files path
     * @param zipFile  zip file path
     * @throws IOException
     */
    public static void zip(String[] files, String zipFile) throws IOException {
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_S
        if (!DEBUG) {
            return;
        }
        //TS: chao-zhang 2015-12-02 EMAIL BUGFIX_1005115 ADD_E
        BufferedInputStream origin;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                }
                finally {
                    origin.close();
                }
            }
        }
        finally {
            out.close();
        }
    }
    //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD_E

}
