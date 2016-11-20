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
 */

package com.tct.mail.text;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.tct.mail.ContactInfoSource;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.providers.Account;
import com.tct.mail.utils.PLFUtils;

public class EmailAddressSpan extends URLSpan {

    private final Account mAccount;
    private final String mEmailAddress;
    //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 ADD_S
    private ContactInfoSource mContactInfoSource;
    private FragmentManager mFragmentManager;
    private Context mContext;
    //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 ADD_E

    //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 MOD_S
    public EmailAddressSpan(Context context, Account account, String emailAddress, ContactInfoSource contactInfoSource) {
        super("mailto:" + emailAddress);
        mAccount = account;
        mEmailAddress = emailAddress;
        mContactInfoSource = contactInfoSource;
        mContext = context;
    }
    //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 MOD_E

    //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 ADD_S
    public void setFragmentManager(FragmentManager manager) {
        this.mFragmentManager = manager;
    }
    //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 ADD_E

    @Override
    public void onClick(View view) {
        //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 MOD_S
        boolean enable = PLFUtils.getBoolean(mContext, "feature_email_provideActionOptionsForMailAddress_on");
        if (enable) {
            if (mFragmentManager != null) {
                ChooseActionFragment fragment = ChooseActionFragment.newInstance(mAccount, mEmailAddress, mContactInfoSource);
                fragment.show(mFragmentManager, ChooseActionFragment.FRAGMENT_TAG);
            }
        } else {
            ComposeActivity.composeToAddress(view.getContext(), mAccount, mEmailAddress);
        }
        //TS: rong-tang 2016-04-19 EMAIL BUGFIX-1951808 MOD_E
    }

    /**
     * Makes the text in the link color and not underlined.
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false);
    }
}
