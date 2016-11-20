package com.tct.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class BaseNativeActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "BaseNativeActivity";
    protected boolean mHasNoPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHasNoPermission = false;
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
                    mHasNoPermission = true;
                    return;
                }

                //not entrance activity
                if (savedInstanceState == null) {
//                    first time to open the activity, to avoid asking multiple requests when rotate screen
                    ActivityCompat.requestPermissions(this, requestList.toArray(new String[requestList.size()]), PermissionUtil.REQ_CODE_PERMISSION_RESULT);
                }
            }
        }
    }


    private boolean isEntranceActivity(){
        return PermissionUtil.entranceActivitySet.contains(this.getClass().getName());
    }

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

}
