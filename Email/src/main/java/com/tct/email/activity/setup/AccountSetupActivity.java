/*
 * Copyright (C) 2010 The Android Open Source Project
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
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1030520    2015/12/02    zheng.zou     [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created
 *===========================================================================
 */

package com.tct.email.activity.setup;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.tct.permission.BaseNativeActivity;

/**
 * Superclass of all of the account setup activities; ensures that SetupData state is saved/restored
 * automatically as required
 */
public class AccountSetupActivity extends BaseNativeActivity implements SetupDataFragment.SetupDataContainer {      //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 MOD
    protected SetupDataFragment mSetupData;

    private static final String SETUP_DATA_FRAGMENT_TAG = "setupData";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Bundle b = getIntent().getExtras();
            if (b != null) {
                mSetupData = b.getParcelable(SetupDataFragment.EXTRA_SETUP_DATA);
                if (mSetupData != null) {
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(mSetupData, SETUP_DATA_FRAGMENT_TAG);
                    ft.commit();
                }
            }
        } else {
            mSetupData = (SetupDataFragment)getFragmentManager()
                    .findFragmentByTag(SETUP_DATA_FRAGMENT_TAG);
        }

        if (mSetupData == null) {
            mSetupData = new SetupDataFragment();
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(mSetupData, SETUP_DATA_FRAGMENT_TAG);
            ft.commit();
        }
    }

    @Override
    public SetupDataFragment getSetupData() {
        return mSetupData;
    }
}
