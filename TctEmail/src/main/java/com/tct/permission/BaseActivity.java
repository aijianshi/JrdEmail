/*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1030520    2015/12/02    zheng.zou     [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created
 *===========================================================================
 */
package com.tct.permission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "BaseActivity";
    protected boolean mHasNoPermission;
    private boolean mNeedNonCriticalPermission; // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 ADD
    private List<PermissionUtil.OnPermissionResult>  mPermissionResultListeners = new ArrayList<>();      //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHasNoPermission = false;        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD
//        String[] permissions = PermissionUtil.CRITICAL_PERMISSIONS;
        String[] permissions = PermissionUtil.permissionMap.get(this.getClass().getName());
        if (PermissionUtil.permissionMap.get(this.getClass().getName()) != null) {
            List<String> requestList = new ArrayList<>();
            for (String perm : permissions) {
                if (PermissionChecker.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    requestList.add(perm);
                }
            }
            if (!requestList.isEmpty()) {
                if (isEntranceActivity()) {
                    Intent intent = new Intent(this, PermissionBlockActivity.class);
                    intent.putStringArrayListExtra(PermissionUtil.EXTRA_PERMISSIONS, (ArrayList<String>) requestList);
                    Intent fromIntent = getIntent();
                    if (fromIntent != null) {
                        intent.putExtra(PermissionUtil.EXTRA_INTENT, fromIntent);
                    }
                    startActivity(intent);
                    finish();
                    mHasNoPermission = true;      //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD
                    return;
                }

                //not entrance activity
                if (savedInstanceState == null) {
//                    first time to open the activity, to avoid asking multiple requests when rotate screen
                    ActivityCompat.requestPermissions(this, requestList.toArray(new String[requestList.size()]), PermissionUtil.REQ_CODE_PERMISSION_RESULT);
                    mNeedNonCriticalPermission = true;    // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 ADD
                }
            }
        }
    }

    // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 ADD_S
    public final boolean needCriticalPermission(){
        return mHasNoPermission;
    }

    public final boolean needNonCriticalPermission(){
        return mNeedNonCriticalPermission;
    }
    // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 ADD_E


    private boolean isEntranceActivity(){
        return PermissionUtil.entranceActivitySet.contains(this.getClass().getName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if(PermissionUtil.REQ_CODE_PERMISSION_RESULT == requestCode){
            for(String permission : permissions){
                if(PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG, "No permission, " + permission);
                    if(Build.VERSION.SDK_INT >= 23){
                        //TODO:show a toast if you need
                    }
                    this.finish();
                }
            }
        }

    }

    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
    public void registerPermissionResultListener(PermissionUtil.OnPermissionResult listener){
        if (!mPermissionResultListeners.contains(listener)){
            mPermissionResultListeners.add(listener);
        }
    }

    public void unRegisterPermissionResultListener(PermissionUtil.OnPermissionResult listener){
        mPermissionResultListeners.remove(listener);
    }

    public void notifyPermissionResult(int requestCode, String permission, int result){    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
        for (PermissionUtil.OnPermissionResult listener: mPermissionResultListeners){
            listener.onPermissionResult(requestCode, permission,result);
        }
    }
    //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E

}
