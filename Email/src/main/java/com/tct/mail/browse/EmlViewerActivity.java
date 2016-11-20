/*
 * Copyright (C) 2013 Google Inc.
 * Licensed to The Android Open Source Project.
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
 * /*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX_1059204  2015/12/10     lin-zhou       [Email]Can't open eml file via Email
 *===========================================================================
 */

package com.tct.mail.browse;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.widget.Toast;

import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.ui.AccountFeedbackActivity;
import com.tct.mail.utils.MimeType;
import com.tct.permission.PermissionUtil;
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

public class EmlViewerActivity extends AccountFeedbackActivity {
    private static final String LOG_TAG = LogTag.getLogTag();

    private static final String FRAGMENT_TAG = "eml_message_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 MOD_S
        if (needNonCriticalPermission()) {
            return;
        }
        doCreate(savedInstanceState);
    }

    private void doCreate(Bundle savedInstanceState){
        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String type = intent.getType();
        if (savedInstanceState == null) {
            if (Intent.ACTION_VIEW.equals(action) &&
                    MimeType.isEmlMimeType(type)) {
                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.root, EmlMessageViewFragment.newInstance(
                        intent.getData(), mAccountUri), FRAGMENT_TAG);
                transaction.commit();
                Analytics.getInstance().sendEvent("eml_viewer", null, null, 0);
            } else {
                LogUtils.wtf(LOG_TAG,
                        "Entered EmlViewerActivity with wrong intent action or type: %s, %s",
                        action, type);
                finish(); // we should not be here. bail out. bail out.
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionUtil.REQ_CODE_PERMISSION_RESULT == requestCode) {
            boolean granted = true;
            for (String permission : permissions) {
                if (PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    this.finish();
                    break;
                }
            }
            if (granted) {
                doCreate(null);
            } else {
                //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                Utility.showToast(this, R.string.no_permission_to_open_file);
                //Toast.makeText(this, R.string.no_permission_to_open_file, Toast.LENGTH_LONG).show();
            }
        }

    }
    // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 MOD_E
}
