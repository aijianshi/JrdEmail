/*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1122768    2015/12/11    jin.dong     [Email]It will back to home screen when click ALLOW in Email permissions screen.
 *BUGFIX_1311874    2015/1/11     yanhua.chen  [Email]Can't via email to enter inputting email address interface
 *===========================================================================
 */
package com.tct.permission;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;

import com.tct.fw.R;
import com.tct.mail.utils.LogUtils;

/**
 * Created by user on 15-11-19.
 */
public class PermissionExplainActivity extends BaseActivity implements View.OnClickListener{
    private static final int REQ_CODE_GO_SETTING = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_explain_activity);
        findViewById(R.id.permission_explain_exit_btn).setOnClickListener(this);
        findViewById(R.id.permission_explain_go_setting_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.permission_explain_exit_btn){
            this.finish();
        } else if (v.getId() == R.id.permission_explain_go_setting_btn){
            gotoSettingsForResult(this,REQ_CODE_GO_SETTING);
        }
    }

    public static void gotoSettingsForResult(Activity activity,int requestCode) {
        Intent intent;
        if (PermissionUtil.isIntentExisting(activity, PermissionUtil.TCT_ACTION_MANAGE_APP)) {
            //Goto setting application permission
            intent = new Intent(PermissionUtil.TCT_ACTION_MANAGE_APP);
            intent.putExtra(PermissionUtil.TCT_EXTRA_PACKAGE_NAME, activity.getPackageName());
        } else {
            //Goto settings details
            final Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        }
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            LogUtils.e(LogUtils.TAG, "gotoSettings failed " + e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GO_SETTING){
            for (String permission:PermissionUtil.CRITICAL_PERMISSIONS){
               boolean granted = PermissionUtil.checkPermission(this,permission);
                if (!granted){
                    return;
                }
            }
            //TS: jin.dong 2015-12-11 EMAIL BUGFIX_1122768 MOD_S
            Intent fromIntent = getIntent().getParcelableExtra(PermissionUtil.EXTRA_INTENT);
            if (fromIntent != null) {
                if (Intent.ACTION_MAIN.equals(fromIntent.getAction())) {
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
//                            //fromIntent.setFlags(fromIntent.getFlags() & ~Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        }
                        fromIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET & ~Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //TS: yanhua.chen 2015-1-11 EMAIL BUGFIX_1311874 MOD_E
                        startActivity(fromIntent);
                    } catch (SecurityException e) {
                        LogUtils.w(LogUtils.TAG, "can not start activity using fromIntent from explain activity");
                    }
                    //TS: zheng.zou 2015-12-31 EMAIL BUGFIX_1209679 MOD_E
                }
                //TS: jin.dong 2015-12-11 EMAIL BUGFIX_1122768 MOD_E
                this.finish();
            }
        }
    }
}
