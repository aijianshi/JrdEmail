/*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1122768    2015/12/11    jin.dong     [Email]It will back to home screen when click ALLOW in Email permissions screen.
 *BUGFIX_1311874    2015/1/11     yanhua.chen  [Email]Can't via email to enter inputting email address interface
 *BUGFIX-1193545    2015/1/11     lin.zhou     [Android 6.0][Email]Enter to Email will flash a redundant screen after click never ask again.
 *BUGFIX-1267234    2016/03/16    junwei-xu    [UX][Email]The background screen flicker when first time launch Email.
 *===========================================================================
 */
package com.tct.permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;


import com.tct.fw.R;
import com.tct.mail.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 15-11-19.
 */
public class PermissionBlockActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int REQ_CODE_CHECK_PERMISSION = 0;

    //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_S
    private static final String PERMISSIONBLOCK_SHAREPREFERENCES = "AndroidMail.Permission";
    private static final String PERMISSIONBLOCK_ISFIRST = "isFirst";
    //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_E

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_block_activity);
        List<String> permissions = getIntent().getStringArrayListExtra(PermissionUtil.EXTRA_PERMISSIONS);
        if (permissions == null || permissions.isEmpty()){
            this.finish();
            return;
        }

        //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_S
        boolean isSkip = false;
        //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_E

        List<String> requestList = new ArrayList<>();
        for (String perm : permissions) {
            if (PermissionChecker.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(perm);
                //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_S
                isSkip = isSkip || ActivityCompat.shouldShowRequestPermissionRationale(this, perm);
                //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_E
            }
        }

        //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_S
        SharedPreferences mSharedPreferences = getSharedPreferences(PERMISSIONBLOCK_SHAREPREFERENCES, Context.MODE_PRIVATE);
        boolean isFirst = mSharedPreferences.getBoolean(PERMISSIONBLOCK_ISFIRST,true);
        if(!isFirst){
            if(!isSkip){
                Intent intent = new Intent(this,PermissionExplainActivity.class);
                Intent fromIntent = getIntent().getParcelableExtra(PermissionUtil.EXTRA_INTENT);
                if (fromIntent != null) {
                    intent.putExtra(PermissionUtil.EXTRA_INTENT, fromIntent);
                }
                startActivity(intent);
                this.finish();
            }
        }
        if(isFirst){
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(PERMISSIONBLOCK_ISFIRST,false);
            editor.commit();
        }
        //TS: lin.zhou 2015-1-11 EMAIL BUGFIX_1193545 MOD_E

        if (!requestList.isEmpty()) {
            //not entrance activity
            if (savedInstanceState == null) {
                //TS: junwei-xu 2016-03-16 EMAIL BUGFIX-1267234 MOD_S
                final String[] permissons = requestList.toArray(new String[requestList.size()]);
                //first time to open the activity, to avoid asking multiple requests when rotate screen
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ActivityCompat.requestPermissions(PermissionBlockActivity.this, permissons, REQ_CODE_CHECK_PERMISSION);
                    }
                }).start();
                //TS: junwei-xu 2016-03-16 EMAIL BUGFIX-1267234 MOD_E
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(REQ_CODE_CHECK_PERMISSION == requestCode){
            for(String permission : permissions){
                if(PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(this,PermissionExplainActivity.class);
                    Intent fromIntent = getIntent().getParcelableExtra(PermissionUtil.EXTRA_INTENT);
                    if (fromIntent != null) {
                        intent.putExtra(PermissionUtil.EXTRA_INTENT, fromIntent);
                    }
                    startActivity(intent);
                    this.finish();
                    return;
                }
            }
            Intent fromIntent = getIntent().getParcelableExtra(PermissionUtil.EXTRA_INTENT);
            if (fromIntent!=null){
                //TS: jin.dong 2015-12-11 EMAIL BUGFIX_1122768 MOD_S
                if (Intent.ACTION_MAIN.equals(fromIntent.getAction())){
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(getPackageName());
                    startActivity(intent);
                } else {
                    //TS: zheng.zou 2015-12-31 EMAIL BUGFIX_1209679 MOD_S
                    //if the storage permission is not granted, and the from intent have the media uri, we should reset
                    //the FLAG_GRANT_READ_URI_PERMISSION flag to avoid permission crash
                    try {
                        //TS: yanhua.chen 2015-1-11 EMAIL BUGFIX_1311874 MOD_S
//                        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                            fromIntent.setFlags(fromIntent.getFlags() & ~Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        }
                        fromIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET & ~Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //TS: yanhua.chen 2015-1-11 EMAIL BUGFIX_1311874 MOD_E
                        startActivity(fromIntent);

                    } catch (SecurityException e) {
                        LogUtils.w(LogUtils.TAG, "can not start activity using fromIntent from block activity");
                    }
                    //TS: zheng.zou 2015-12-31 EMAIL BUGFIX_1209679 MOD_E
                }
                //TS: jin.dong 2015-12-11 EMAIL BUGFIX_1122768 MOD_E
                this.finish();
            }
        }

    }
}
