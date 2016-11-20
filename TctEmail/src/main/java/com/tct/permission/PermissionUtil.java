/*
==========================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ==============================
*TASK-869664    2015/11/27    zheng.zou      [Email]Android M Permission Upgrade
*BUGFIX-1000758    2015/12/02    zheng.zou     [Email][Force close]Email will pop up force close when login a TCL account.
*BUGFIX-1030520    2015/12/02    zheng.zou     [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created
*BUGFIX_1162996    2015/12/18    yanhua.chen   [Android 6.0][Email]TCL account pop up permission needed window continuously if disable contact/calendar permission of exchange]  Edit Notification
*BUGFIX_1162996    2015/1/20     yanhua.chen   [Android 6.0][Email]TCL account pop up permission needed window continuously if disable contact/calendar permission of exchange
*BUGFIX-1909256 2016-04-05   junwei-xu       [HZ-IUT][Email]Email ringtone will not change after select ringtone from SD card.
===========================================================================
*/
package com.tct.permission;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.UIProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PermissionUtil {

    //Default result to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
    public static final String TAG = "PermissionUtil";
    public static final int REQ_CODE_PERMISSION_RESULT = 100;
    public static final int REQ_CODE_PERMISSION_SAVE_ATTACHMENT = 101;
    public static final int REQ_CODE_PERMISSION_SEE_CALENDAR = 102;
    public static final int REQ_CODE_PERMISSION_ADD_ATTACHMENT = 103;    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD
    public static final int REQ_CODE_PERMISSION_READ_CONTACT = 104;   // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 ADD
    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
    public static final int REQ_CODE_PERMISSION_REDOWNLOAD_ATTACHMENT = 105;
    public static final int REQ_CODE_PERMISSION_VIEW_ATTACHMENT = 106;
    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    public static final int REQ_CODE_PERMISSION_ACCESS_RINGTONE = 107;
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
    public static final String EXTRA_INTENT = "extra_intent";
    public static final String EXTRA_PERMISSIONS = "extra_permissions";
    //[BUGFIX]-Mod-BEGIN by SCDTABLET.yingjie.chen@tcl.com,03/07/2016,1746249,
    //Can't login Email prompt permissson issue.
    public static final String[] CRITICAL_PERMISSIONS = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE};  //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 MOD
    //[BUGFIX]-Mod-END by SCDTABLET.yingjie.chen@tcl.com
//        public static final String TCT_ACTION_MANAGE_APP_PERMISSIONS = "android.intent.action.tct.MANAGE_APP_PERMISSIONS";
    public static final String TCT_ACTION_MANAGE_APP = "android.intent.action.tct.MANAGE_PERMISSIONS";
    public static final String TCT_EXTRA_PACKAGE_NAME = "android.intent.extra.tct.PACKAGE_NAME";
    //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_S
    public static HashMap<String, String[]> permissionMap = new HashMap<String, String[]>();
    public static HashSet<String> entranceActivitySet = new HashSet<>();
    //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_E
    //TS: yanhua.chen 2015-12-18 EMAIL BUGFIX_1162996 ADD_S
    private static long lastShowPermissonExplainTime;
    private static long DISTURBTIME = 5 * 60 * 1000;
    //TS: yanhua.chen 2015-12-18 EMAIL BUGFIX_1162996 ADD_E
    public static final String EXTRA_PACKAGENAME = "extra_packageName";

    /*
     * @param activity The target activity.
     * @param permissions The requested permissions.
     * @param requestCode Application specific request code to match with a result
     *    reported to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(
     *    int, String[], int[])}.
     */
    public static void checkAndRequestPermissions(final @NonNull Activity activity,
                                                  final @NonNull String[] permissions, final int requestCode) {
        List<String> requestList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PermissionChecker.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(perm);
            }
        }

        if (requestList.size() > 0) {
            ActivityCompat.requestPermissions(activity, requestList.toArray(new String[requestList.size()]), requestCode);
        }
    }


    public static boolean checkPermission(Context context, String permission) {
        if (context != null && PackageManager.PERMISSION_GRANTED !=
                PermissionChecker.checkSelfPermission(context, permission)) {   //TS: zheng.zou 2015-11-27 EMAIL TASK_869664 MOD
            return false;
        }
        return true;
    }

    public static boolean checkPermissionAndLaunchExplain(Context context, String permission) {
        //TS: zheng.zou 2015-12-02 EMAIL BUGFIX_1000758 MOD_S
        //NOTE: the checkSelfPermission may throw SecurityException when permission is granted and go through
        //noteProxyOp() in AppOpsManager, maybe a framework bug. Check with framework later.
        int grant;
        try {
            grant = PermissionChecker.checkSelfPermission(context, permission);
        } catch (SecurityException e) {
            LogUtils.e(TAG, "SecurityException in PermissionChecker.checkSelfPermission() " + e);
            //NOTE: this situation will happen only when permission is PERMISSION_GRANTED
            grant = PackageManager.PERMISSION_GRANTED;
        }
        if (context != null && PackageManager.PERMISSION_GRANTED != grant) {  //TS: zheng.zou 2015-11-27 EMAIL TASK_869664 MOD
        //TS: zheng.zou 2015-12-02 EMAIL BUGFIX_1000758 MOD_E
            //TS: yanhua.chen 2015-12-18 EMAIL BUGFIX_1162996 MOD_S
            try {
                //set show permission time interval
                if(System.currentTimeMillis() - lastShowPermissonExplainTime > DISTURBTIME
                        || Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 MOD
//                    Intent intent = new Intent(context, PermissionExplainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
                    lastShowPermissonExplainTime = System.currentTimeMillis();
                    final ContentResolver resolver = context.getContentResolver();
                    String method = UIProvider.SHOW_EXCHANGE_CALENDARORCONTACTS_NOTIFICATION;
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_PACKAGENAME, context.getPackageName());
                    bundle.putString(EXTRA_PERMISSIONS, permission);
                    resolver.call(Uri.parse("content://com.tct.email.provider/uiaccount/-1"), method, Uri.parse("content://com.tct.email.provider/uiaccount/-1").toString(), bundle);
                }
            } catch (Exception e){
                LogUtils.i(TAG,"checkPermissionAndLaunchExplain catch exception when call provider");
            }
            //TS: yanhua.chen 2015-12-18 EMAIL BUGFIX_1162996 MOD_E
            return false;
        }
        return true;
    }

    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1163092 ADD_S
    /**
     * check Permission And Do Noting, we need this when we don't want to disturb user
     * @param context context
     * @param permission  permission
     * @return  permission check result
     */
    public static boolean checkPermissionAndDoNoting(Context context, String permission){
        int grant;
        try {
            grant = PermissionChecker.checkSelfPermission(context, permission);
        } catch (SecurityException e) {
            LogUtils.e(TAG, "SecurityException in PermissionChecker.checkSelfPermission() e = " + e);
            //NOTE: this situation will happen only when permission is PERMISSION_GRANTED
            grant = PackageManager.PERMISSION_GRANTED;
        }
        return context != null && PackageManager.PERMISSION_GRANTED == grant;
    }
    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1163092 ADD_E

    public static boolean checkAndRequestStoragePermission(final @NonNull Context context) {
        boolean isGranted = true;
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!checkPermission(context, permission)) {
            isGranted = false;
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, PermissionUtil.REQ_CODE_PERMISSION_SAVE_ATTACHMENT);
            }
        }
        return isGranted;
    }

    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD_S
    /**
     *   check and request permission and return check result
     *   {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}
     *   callback will be invoke after user choose to allow or deny permission
     * @param context  context
     * @param permission  permission
     * @param requestCode requestCode
     * @return  the check result of the permission
     */
    public static boolean checkAndRequestPermissionForResult(final @NonNull Context context, String permission, int requestCode){
        boolean isGranted = true;
        if (!checkPermission(context, permission)) {
            isGranted = false;
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission},requestCode);
            }
        }
        return isGranted;
    }
    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD_E

    public static void gotoSettings(Context context) {
        Intent intent;
        if (isIntentExisting(context, TCT_ACTION_MANAGE_APP)) {
            //Goto setting application permission
            intent = new Intent(TCT_ACTION_MANAGE_APP);
            intent.putExtra(TCT_EXTRA_PACKAGE_NAME, context.getPackageName());
        } else {
            //Goto settings details
            final Uri packageURI = Uri.parse("package:" + context.getPackageName());
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtils.e(LogUtils.TAG, "gotoSettings failed " + e);
        }
    }



    public static boolean isIntentExisting(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_ALL);
        return resolveInfo.size() > 0;
    }

    public static boolean checkAndRequestCalendarPermission(final @NonNull Context context) {
        boolean isGranted = true;
        String permission = Manifest.permission.READ_CALENDAR;
        if (!checkPermission(context, permission)) {
            isGranted = false;
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, PermissionUtil.REQ_CODE_PERMISSION_SEE_CALENDAR);
            }
        }
        return isGranted;
    }

    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
    public static interface OnPermissionResult {

        public void onPermissionResult(int requestCode, String permission, int result);  //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 MOD

    }
    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E

}
