/*
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
 *Tag                Date         Author         Description
 *============== ============ =============== ==============================
 *TASK-869664    2015/11/25   zheng.zou       [Email]Android M Permission Upgrade
 ============================================================================
 */

package com.tct.emailcommon;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Device {
    private static String sDeviceId = null;
    //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 ADD_S
    private static final String TAG = "Device";
    private static final String MIDDLE_MAN_SCHEME = "content://com.tct.gapp.middleman";
    private static final String MIDDLE_MAN_GET_DEVICE_ID = "getDeviceID";
    private static final String MIDDLE_MAN_GET_LINE1_NUMBER = "getLine1Number";
    private static final String MIDDLE_MAN_GET_NET_OP_NAME = "getNetworkOperatorName";
    private static final String MIDDLE_MAN_GET_NET_OP = "getNetworkOperator";
    private static final String EXTRA_RESULT_CODE = "result_code";
    private static final String EXTRA_RESULT = "result";
    private static final int MIDDLE_MAN_RESULT_OK = 1;
    private static final int MIDDLE_MAN_RESULT_NO_METHOD = 2;
    private static final int MIDDLE_MAN_RESULT_NULL_METHOD = 3;
    private static final int MIDDLE_MAN_RESULT_NULL_PACKAGE = 4;
    private static final int MIDDLE_MAN_RESULT_NO_REGISTER = 5;
    //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 ADD_E

    /**
     * EAS requires a unique device id, so that sync is possible from a variety of different
     * devices (e.g. the syncKey is specific to a device)  If we're on an emulator or some other
     * device that doesn't provide one, we can create it as android<n> where <n> is system time.
     * This would work on a real device as well, but it would be better to use the "real" id if
     * it's available
     */
    static public synchronized String getDeviceId(Context context) throws IOException {
        if (sDeviceId == null) {
            sDeviceId = getDeviceIdInternal(context);
        }
        return sDeviceId;
    }

    static private String getDeviceIdInternal(Context context) throws IOException {
        if (context == null) {
            throw new IllegalStateException("getDeviceId requires a Context");
        }
        File f = context.getFileStreamPath("deviceName");
        BufferedReader rdr = null;
        String id;
        if (f.exists()) {
            if (f.canRead()) {
                rdr = new BufferedReader(new FileReader(f), 128);
                id = rdr.readLine();
                rdr.close();
                if (id == null) {
                    // It's very bad if we read a null device id; let's delete that file
                    if (!f.delete()) {
                        LogUtils.e(Logging.LOG_TAG,
                                "Can't delete null deviceName file; try overwrite.");
                    }
                } else {
                    return id;
                }
            } else {
                LogUtils.w(Logging.LOG_TAG, f.getAbsolutePath() + ": File exists, but can't read?" +
                    "  Trying to remove.");
                if (!f.delete()) {
                    LogUtils.w(Logging.LOG_TAG, "Remove failed. Tring to overwrite.");
                }
            }
        }
        BufferedWriter w = new BufferedWriter(new FileWriter(f), 128);
        final String consistentDeviceId = getConsistentDeviceId(context);
        if (consistentDeviceId != null) {
            // Use different prefix from random IDs.
            id = "androidc" + consistentDeviceId;
        } else {
            id = "android" + System.currentTimeMillis();
        }
        w.write(id);
        w.close();
        return id;
    }

    /**
     * @return Device's unique ID if available.  null if the device has no unique ID.
     */
    public static String getConsistentDeviceId(Context context) {
        String deviceId = null;
        try {
            //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 MOD_S
//            if (PermissionUtil.checkPermissionAndDoNoting(context, Manifest.permission.READ_PHONE_STATE)){  //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1163092 MOD
//                TelephonyManager tm =
//                        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                if (tm == null) {
//                    return null;
//                }
//                deviceId = tm.getDeviceId();
//            } else {
//                LogUtils.i(PermissionUtil.TAG,"permission %s needed in getConsistentDeviceId()",Manifest.permission.READ_PHONE_STATE);
//            }
            deviceId = getMiddleManDeviceId(context);
            //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 MOD_E
            if (deviceId == null) {
                return null;
            }
        } catch (Exception e) {
            LogUtils.d(Logging.LOG_TAG, "Error in TelephonyManager.getDeviceId(): "
                    + e.getMessage());
            return null;
        }
        return Utility.getSmallHash(deviceId);
    }

    //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 ADD_S
    private static String getMiddleManResult(Context context,String method){
        String result = null;
        if (context!=null){
            Uri uri = Uri.parse(MIDDLE_MAN_SCHEME + "/" + context.getPackageName());
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, method, null, null);
                if (cursor != null && cursor.getExtras() != null) {
                    Bundle bundle = cursor.getExtras();
                    int resultCode = bundle.getInt(EXTRA_RESULT_CODE);
                    if (resultCode == MIDDLE_MAN_RESULT_OK) {
                        result = bundle.getString(EXTRA_RESULT);
                    } else {
                        LogUtils.w(TAG, "can not "+method+", result code = " + resultCode);
                    }
                }
            } catch (Exception e){
                LogUtils.d(Logging.LOG_TAG, "Error in "+ method+"(): "
                        + e.getMessage());
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return result;
    }

    public static String getMiddleManDeviceId(Context context){
       return getMiddleManResult(context,MIDDLE_MAN_GET_DEVICE_ID);
    }

    public static String getLine1Number(Context context) {
        return getMiddleManResult(context, MIDDLE_MAN_GET_LINE1_NUMBER);
    }

    public static String getNetworkOperatorName(Context context){
        return getMiddleManResult(context, MIDDLE_MAN_GET_NET_OP_NAME);
    }

    public static String getNetworkOperator(Context context){
        return getMiddleManResult(context, MIDDLE_MAN_GET_NET_OP);
    }
    //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 ADD_E
}
