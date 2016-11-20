/**
 * Copyright (c) 2011, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 *Tag            Date         Author           Description
 *============== ============ =============== ==============================
 *BUG-840455     2014/11/14  wenggangjin       [Email][Crash] Email crashs when 
 *                                              creating/replaying a mail.
 *BUGFIX-943609  2015/03/24   junwei-xu       [Android5.0][Email][UI]The color when touch a& hold a sender does not follow Email style.
 ============================================================================
 */
package com.tct.mail.compose;

import android.app.Instrumentation;
import android.content.Context;
import android.text.TextUtils;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.mail.providers.ReplyFromAccount;

import java.util.List;

/**
 * FromAddressSpinnerAdapter returns the correct spinner adapter for reply from
 * addresses based on device size.
 *
 * @author mindyp@google.com
 */
public class FromAddressSpinnerAdapter extends ArrayAdapter<ReplyFromAccount> {
    private static final int FROM = 0;
    private static final int CUSTOM_FROM = 1;
    private static String sFormatString;

    public static int REAL_ACCOUNT = 2;

    public static int ACCOUNT_DISPLAY = 0;

    public static int ACCOUNT_ADDRESS = 1;

    private LayoutInflater mInflater;

    // junwei-xu 2015-03-24 EMAIL BUGFIX-943609 ADD_S
    FromAddressSpinner mFromAddressSpinner;

    public void setSpinner(FromAddressSpinner spinner) {
        mFromAddressSpinner = spinner;
    }

    private void setFromAccount(View fromEntry, final int position) {
        if (fromEntry == null) {
            return;
        }

        fromEntry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFromAddressSpinner != null) {
                    mFromAddressSpinner.setSelection(position);
                }

                // close popup
                new Thread() {
                    public void run() {
                        try{
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        }
                        catch (Exception e) {
                            Log.e("Exception when onBack", e.toString());
                        }
                    }
                }.start();
            }
        });
    }
    // junwei-xu 2015-03-24 EMAIL BUGFIX-943609 ADD_E

    public FromAddressSpinnerAdapter(Context context) {
        super(context, R.layout.from_item, R.id.spinner_account_address);
        sFormatString = getContext().getString(R.string.formatted_email_address);
    }

    protected LayoutInflater getInflater() {
        if (mInflater == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    @Override
    public int getViewTypeCount() {
        //[BUGFIX]-MOD-BEGIN by TSCD.gangjin.weng,14/11/2014,PR 840455 
        // FROM and CUSTOM_FROM
        //return 2;
        return 1;
        //[BUGFIX]-MOD-END by TSCD.gangjin.weng,14/11/2014,PR 840455
    }

    @Override
    public int getItemViewType(int pos) {
        return getItem(pos).isCustomFrom ? CUSTOM_FROM : FROM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReplyFromAccount fromItem = getItem(position);
        int res = fromItem.isCustomFrom ? R.layout.custom_from_item : R.layout.from_item;
        View fromEntry = convertView == null ? getInflater().inflate(res, null) : convertView;
        if (fromItem.isCustomFrom) {
            ((TextView) fromEntry.findViewById(R.id.spinner_account_name)).setText(fromItem.name);

            ((TextView) fromEntry.findViewById(R.id.spinner_account_address))
                    .setText(formatAddress(fromItem.address));
        } else {
            ((TextView) fromEntry.findViewById(R.id.spinner_account_address))
                    .setText(fromItem.address);
        }
        return fromEntry;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ReplyFromAccount fromItem = getItem(position);
        int res = fromItem.isCustomFrom ? R.layout.custom_from_dropdown_item
                : R.layout.from_dropdown_item;
        View fromEntry = getInflater().inflate(res, null);
        if (fromItem.isCustomFrom) {
            ((TextView) fromEntry.findViewById(R.id.spinner_account_name)).setText(fromItem.name);
            ((TextView) fromEntry.findViewById(R.id.spinner_account_address))
                    .setText(formatAddress(fromItem.address));
        } else {
            ((TextView) fromEntry.findViewById(R.id.spinner_account_address))
                    .setText(fromItem.address);
        }
        // junwei-xu 2015-03-24 EMAIL BUGFIX-943609 ADD_S
        setFromAccount(fromEntry, position);
        // junwei-xu 2015-03-24 EMAIL BUGFIX-943609 ADD_E
        return fromEntry;
    }

    private CharSequence formatAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        return String.format(sFormatString, Rfc822Tokenizer.tokenize(address)[0].getAddress());
    }

    public void addAccounts(List<ReplyFromAccount> replyFromAccounts) {
        // Get the position of the current account
        for (ReplyFromAccount account : replyFromAccounts) {
            // Add the account to the Adapter
            add(account);
        }
    }
}
