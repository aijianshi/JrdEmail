/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright 2014 TCT Communications Technology Holdings Limited
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
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/14/2014|     Chao Zhang       |      FR 635028       |[Email]Download   */
/*           |                      |porting from(FR472914)|options to be im- */
/*           |                      |                      |plemented         */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*

 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-923792  2015/2/6      junwei-xu      [HOMO][TMO DT varaint 61562]The download attachments option was selected by default without user interaction.
 *BUGFIX-946308  2015/3/13     zhichuan.wei   [SDM][Email]def_email_checkFrequencyPush_default can not effect
 *BUGFIX-940964  2015/4/20    gangjin.weng    [Email] Set Dwonload Head Only by default
 *BUGFIX-1010063 2015/5/25    junwei-xu       [HOMO][H3G IE][Jira IDOL3-14][Email] Acount of email to sync is set to "Last week"
 *BUGFIX-1071056 2015/8/19    jian.xu         [HOMO]Default Email settings should be changed
 *BUGFIX-1093309 2015/9/29    junwei-xu       <13340Track><26><CDR-EAS-030>Synchronization Scope—Calendar Events
 *===========================================================================
 */
package com.tct.email.activity.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.tct.email.R;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.Policy;
import com.tct.emailcommon.service.SyncCalendarWindow;
import com.tct.emailcommon.service.SyncWindow;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.utility.Utility;
import com.tct.email.activity.UiUtilities;
import com.tct.email.service.EmailServiceUtils;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;

public class AccountSetupOptionsFragment extends AccountSetupFragment {
    private Spinner mCheckFrequencyView;
    private Spinner mSyncWindowView;
    private CheckBox mNotifyView;
    private CheckBox mSyncContactsView;
    private CheckBox mSyncCalendarView;
    private CheckBox mSyncEmailView;
    private CheckBox mBackgroundAttachmentsView;
    private View mAccountSyncWindowRow;

    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
    private Spinner mSyncCalendarWindowView;
    private View mAccountSyncCalendarWindowRow;
    /** Default sync calendar for new EAS accounts */
    private static int SYNC_CALENDAR_WINDOW_DEFAULT = SyncCalendarWindow.SYNC_CALENDAR_WINDOW_2_WEEKS;
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E

    /** Default sync window for new EAS accounts */
    private static int SYNC_WINDOW_EAS_DEFAULT = SyncWindow.SYNC_WINDOW_1_WEEK;
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    private View mAccountDownloadOptionsRow;
    private Spinner mDownloadOptionsView;
    /**
     * Enable an additional spinner using the arrays normally handled by preferences
     */
    private void enableDownloadOptionsSpinner(Account account) {
        // Show everything
        mAccountDownloadOptionsRow.setVisibility(View.VISIBLE);
        CharSequence[] downlaodValues;
        CharSequence[] downlaodEntries;
        // Generate spinner entries using XML arrays used by the preferences
        HostAuth host = account.getOrCreateHostAuthRecv(getActivity());
        String protocol = host != null ? host.mProtocol : "";
        if(HostAuth.SCHEME_POP3.equals(protocol)){
            downlaodValues = getResources().getTextArray(
                    R.array.Email_POP3_Download_Options_values);
            downlaodEntries = getResources().getTextArray(
                    R.array.Email_POP3_Download_Options);
        } else {
            downlaodValues = getResources().getTextArray(
                    R.array.Email_Download_Options_values);
            downlaodEntries = getResources().getTextArray(
                    R.array.Email_Download_Options);
        }

        // Find a proper maximum for email lookback, based on policy (if we have one)
        int maxEntry = downlaodEntries.length;

        // Now create the array used by the Spinner
        SpinnerOption[] downlaodOptions = new SpinnerOption[maxEntry];
        for (int i = 0; i < maxEntry; i++) {
            final int value = Integer.valueOf(downlaodValues[i].toString());
            downlaodOptions[i] = new SpinnerOption(value, downlaodEntries[i].toString());
        }

        ArrayAdapter<SpinnerOption> downlaodOptionsAdapter = new ArrayAdapter<SpinnerOption>(getActivity(),
                android.R.layout.simple_spinner_item, downlaodOptions);
        downlaodOptionsAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDownloadOptionsView.setAdapter(downlaodOptionsAdapter);

        //TS: jian.xu 2015-08-19 Email BUGFIX_1071056 MOD_S
        boolean defaultDownloadAll = PLFUtils.getBoolean(getActivity(), "def_Email_download_option");
        int downloadOption = defaultDownloadAll ? Utility.ENTIRE_MAIL : Utility.HEAD_ONLY;
        //the default vale is 20k,but the POP3 scheme has head-only and all
        if(HostAuth.SCHEME_POP3.equals(protocol)){
            account.setDownloadOptions(downloadOption);
        } else if(HostAuth.SCHEME_IMAP.equals(protocol) ||
                HostAuth.SCHEME_EAS.equals(protocol)){
            account.setDownloadOptions(downloadOption);
        } else {
            account.setDownloadOptions(Utility.ENTIRE_MAIL);//all
        }
        //TS: jian.xu 2015-08-19 Email BUGFIX_1071056 MOD_E

        SpinnerOption.setSpinnerOptionValue(mDownloadOptionsView,
                account.getDownloadOptions());
    }
    //[FEATURE]-Add-END by TSCD.Chao Zhang

    public interface Callback extends AccountSetupFragment.Callback {

    }

    public static AccountSetupOptionsFragment newInstance() {
        return new AccountSetupOptionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflateTemplatedView(inflater, container,
                R.layout.account_setup_options_fragment, R.string.account_setup_options_headline);

        mCheckFrequencyView = UiUtilities.getView(view, R.id.account_check_frequency);
        mSyncWindowView = UiUtilities.getView(view, R.id.account_sync_window);
        mNotifyView = UiUtilities.getView(view, R.id.account_notify);
        mNotifyView.setChecked(true);
        mSyncContactsView = UiUtilities.getView(view, R.id.account_sync_contacts);
        mSyncCalendarView = UiUtilities.getView(view, R.id.account_sync_calendar);
        mSyncEmailView = UiUtilities.getView(view, R.id.account_sync_email);
        mSyncEmailView.setChecked(true);
        mBackgroundAttachmentsView = UiUtilities.getView(view, R.id.account_background_attachments);
        //TS: junwei-xu 2015-2-6 EXCHANGE BUGFIX_923792 MOD_S
        if(PLFUtils.getBoolean(getActivity().getApplicationContext(), "feature_exchange_download_attachment_wifi_on")){
            mBackgroundAttachmentsView.setChecked(true);
        }else{
            mBackgroundAttachmentsView.setChecked(false);
        }
        //TS: junwei-xu 2015-2-6 EXCHANGE BUGFIX_923792 MOD_E
        mAccountSyncWindowRow = UiUtilities.getView(view, R.id.account_sync_window_row);
        //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
        mAccountDownloadOptionsRow = UiUtilities.getView(view, R.id.account_download_options_row);
        mDownloadOptionsView = (Spinner) UiUtilities.getView(view, R.id.account_download_options);
        //[FEATURE]-Add-END by TSCD.Chao Zhang
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
        mAccountSyncCalendarWindowRow = UiUtilities.getView(view, R.id.account_sync_calendar_window_row);
        mSyncCalendarWindowView = UiUtilities.getView(view, R.id.account_sync_calendar_window);
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();

        final SetupDataFragment setupData =
                ((SetupDataFragment.SetupDataContainer) getActivity()).getSetupData();
        final Account account = setupData.getAccount();

        final EmailServiceUtils.EmailServiceInfo serviceInfo =
                setupData.getIncomingServiceInfo(getActivity());

        final CharSequence[] frequencyValues = serviceInfo.syncIntervals;
        final CharSequence[] frequencyEntries = serviceInfo.syncIntervalStrings;

        // Now create the array used by the sync interval Spinner
        final SpinnerOption[] checkFrequencies = new SpinnerOption[frequencyEntries.length];
        for (int i = 0; i < frequencyEntries.length; i++) {
            checkFrequencies[i] = new SpinnerOption(
                    Integer.valueOf(frequencyValues[i].toString()), frequencyEntries[i].toString());
        }
        final ArrayAdapter<SpinnerOption> checkFrequenciesAdapter =
                new ArrayAdapter<SpinnerOption>(getActivity(), android.R.layout.simple_spinner_item,
                        checkFrequencies);
        checkFrequenciesAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCheckFrequencyView.setAdapter(checkFrequenciesAdapter);
        //[BUGFIX]-Add-BEGIN by TCTNJ.(zhichuan.wei),03/13/2015 for PR946308
        String protocol = null;
        protocol = account.mHostAuthRecv.mProtocol;
        boolean eas = HostAuth.SCHEME_EAS.equals(protocol);
        int defaultIntervalValues;
        if (eas) {
            // Notes: custom default for Def_Email_check_Frequency_Push for EAS Account
            defaultIntervalValues = Integer.parseInt(PLFUtils.getString(getActivity().getApplicationContext(), "def_email_checkFrequencyPush_default"));
        } else {
            // Notes: This is the root case. Default for POP3 and IMAP Account
            //        Here we change the interval according to SDMID:Def_Email_check_Frequency,
            //        for EAS account, we handle above
            defaultIntervalValues = Integer.parseInt(PLFUtils.getString(getActivity().getApplicationContext(), "def_email_checkFrequency_default"));
        }
        account.setSyncInterval(defaultIntervalValues);
        //[BUGFIX]-Add-END by TCTNJ.(zhichuan.wei)
        SpinnerOption.setSpinnerOptionValue(mCheckFrequencyView, account.getSyncInterval());

        if (serviceInfo.offerLookback) {
            enableLookbackSpinner(account);
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
            boolean isEnable = PLFUtils.getBoolean(getActivity().getApplicationContext(), "feature_email_syncCalendarScope_on");
            if (eas && isEnable) {
                enableCalendarLookbackSpinner(account);
            }
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
        }
        //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //if(getResources().getBoolean(R.bool.feature_email_downloadOptions_on)) {
        if(PLFUtils.getBoolean(getActivity().getApplicationContext(), "feature_email_downloadOptions_on")) {
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            enableDownloadOptionsSpinner(account);
        }
        //[FEATURE]-Add-END by TSCD.Chao Zhang
        if (serviceInfo.syncContacts) {
            mSyncContactsView.setVisibility(View.VISIBLE);
            mSyncContactsView.setChecked(true);
            UiUtilities.setVisibilitySafe(view, R.id.account_sync_contacts_divider, View.VISIBLE);
        }
        if (serviceInfo.syncCalendar) {
            mSyncCalendarView.setVisibility(View.VISIBLE);
            mSyncCalendarView.setChecked(true);
            UiUtilities.setVisibilitySafe(view, R.id.account_sync_calendar_divider, View.VISIBLE);
        }

        if (!serviceInfo.offerAttachmentPreload) {
            mBackgroundAttachmentsView.setVisibility(View.GONE);
            UiUtilities.setVisibilitySafe(view, R.id.account_background_attachments_divider,
                    View.GONE);
        }
    }

    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
    private void enableCalendarLookbackSpinner(Account account) {
        mAccountSyncCalendarWindowRow.setVisibility(View.VISIBLE);

        // Generate spinner entries using XML arrays used by the preferences
        CharSequence[] windowValues = getResources().getTextArray(
                R.array.account_settings_mail_calendar_window_values);
        CharSequence[] windowEntries = getResources().getTextArray(
                R.array.account_settings_mail_calendar_window_entries);
        // If we have a maximum lookback policy, enforce it
        if (account != null && account.mPolicyKey > 0) {
            Policy policy = Policy.restorePolicyWithId(getActivity().getApplicationContext(), account.mPolicyKey);
            if (policy != null && (policy.mMaxEmailLookback != 0)) {
                int maxEntry  = policy.mMaxEmailLookback + 1;
                // Copy the proper number of values into new entries/values array
                CharSequence[] policyEntries = new CharSequence[maxEntry];
                CharSequence[] policyValues = new CharSequence[maxEntry];
                for (int i = 0; i < maxEntry; i++) {
                    policyEntries[i] = windowEntries[i];
                    policyValues[i] = windowValues[i];
                }
                // Point entries/values to the new arrays
                windowEntries = policyEntries;
                windowValues = policyValues;
            }
        }


        // Now create the array used by the Spinner
        final SpinnerOption[] windowOptions = new SpinnerOption[windowValues.length];
        int defaultIndex = -1;
        for (int i = 0; i < windowValues.length; i++) {
            final int value = Integer.valueOf(windowValues[i].toString());
            windowOptions[i] = new SpinnerOption(value, windowEntries[i].toString());
            if (value == SYNC_CALENDAR_WINDOW_DEFAULT) {
                defaultIndex = i;
            }
        }
        final ArrayAdapter<SpinnerOption> windowOptionsAdapter =
                new ArrayAdapter<SpinnerOption>(getActivity(), android.R.layout.simple_spinner_item,
                        windowOptions);
        windowOptionsAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSyncCalendarWindowView.setAdapter(windowOptionsAdapter);

        SpinnerOption.setSpinnerOptionValue(mSyncCalendarWindowView, account.getSyncCalendarLookback());
        if (defaultIndex >= 0) {
            mSyncCalendarWindowView.setSelection(defaultIndex);
        }
    }
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E

    /**
     * Enable an additional spinner using the arrays normally handled by preferences
     */
    private void enableLookbackSpinner(Account account) {
        // Show everything
        mAccountSyncWindowRow.setVisibility(View.VISIBLE);

        // Generate spinner entries using XML arrays used by the preferences
        final CharSequence[] windowValues = getResources().getTextArray(
                R.array.account_settings_mail_window_values);
        final CharSequence[] windowEntries = getResources().getTextArray(
                R.array.account_settings_mail_window_entries);

        // Find a proper maximum for email lookback, based on policy (if we have one)
        int maxEntry = windowEntries.length;
        final Policy policy = account.mPolicy;
        if (policy != null) {
            final int maxLookback = policy.mMaxEmailLookback;
            if (maxLookback != 0) {
                // Offset/Code   0      1      2      3      4        5
                // Entries      auto, 1 day, 3 day, 1 week, 2 week, 1 month
                // Lookback     N/A   1 day, 3 day, 1 week, 2 week, 1 month
                // Since our test below is i < maxEntry, we must set maxEntry to maxLookback + 1
                maxEntry = maxLookback + 1;
            }
        }

        //TS: junwei-xu 2015-05-25 BUGFIX_1010063 ADD_S
        String defaultSyncWindowValue = PLFUtils.getString(getActivity().getApplicationContext(), "def_email_syncWindow_default");
        try {
            SYNC_WINDOW_EAS_DEFAULT = Integer.valueOf(defaultSyncWindowValue);
        } catch (NumberFormatException e) {
            LogUtils.e(LogUtils.TAG, "parse default sync window value occur exception, throws NumberFormatException");
        }
        //TS: junwei-xu 2015-05-25 BUGFIX_1010063 ADD_E
        // Now create the array used by the Spinner
        final SpinnerOption[] windowOptions = new SpinnerOption[maxEntry];
        int defaultIndex = -1;
        for (int i = 0; i < maxEntry; i++) {
            final int value = Integer.valueOf(windowValues[i].toString());
            windowOptions[i] = new SpinnerOption(value, windowEntries[i].toString());
            if (value == SYNC_WINDOW_EAS_DEFAULT) {
                defaultIndex = i;
            }
        }

        final ArrayAdapter<SpinnerOption> windowOptionsAdapter =
                new ArrayAdapter<SpinnerOption>(getActivity(), android.R.layout.simple_spinner_item,
                        windowOptions);
        windowOptionsAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSyncWindowView.setAdapter(windowOptionsAdapter);

        SpinnerOption.setSpinnerOptionValue(mSyncWindowView, account.getSyncLookback());
        if (defaultIndex >= 0) {
            mSyncWindowView.setSelection(defaultIndex);
        }
    }

    public boolean getBackgroundAttachmentsValue() {
        return mBackgroundAttachmentsView.isChecked();
    }

    public Integer getCheckFrequencyValue() {
        return (Integer)((SpinnerOption)mCheckFrequencyView.getSelectedItem()).value;
    }

    /**
     * @return Sync window value or null if view is hidden
     */
    public Integer getAccountSyncWindowValue() {
        if (mAccountSyncWindowRow.getVisibility() != View.VISIBLE) {
            return null;
        }
        return (Integer)((SpinnerOption)mSyncWindowView.getSelectedItem()).value;
    }

    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
    /**
     * @return Sync calendar window value or null if view is hidden
     */
    public Integer getAccountSyncCalendarWindowValue() {
        if (mAccountSyncCalendarWindowRow.getVisibility() != View.VISIBLE) {
            return null;
        }
        return (Integer)((SpinnerOption)mSyncCalendarWindowView.getSelectedItem()).value;
    }
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E

    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    public Integer getDownloadOptionsValue() {
        if (mAccountDownloadOptionsRow.getVisibility() != View.VISIBLE) {
            return null;
        }
        return (Integer)((SpinnerOption)mDownloadOptionsView.getSelectedItem()).value;
    }
    //[FEATURE]-Add-END by TSCD.Chao Zhang

    public boolean getSyncEmailValue() {
        return mSyncEmailView.isChecked();
    }

    public boolean getSyncCalendarValue() {
        return mSyncCalendarView.isChecked();
    }

    public boolean getSyncContactsValue() {
        return mSyncContactsView.isChecked();
    }

    public boolean getNotifyValue() {
        return mNotifyView.isChecked();
    }
}
