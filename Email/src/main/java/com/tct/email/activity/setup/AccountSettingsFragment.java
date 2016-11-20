/*
 * Copyright (C) 2010 The Android Open Source Project
 * Copyright 2013 TCT Communications Technology Holdings Limited
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
 *===================================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== =======================================================================
 *BUGFIX-845093  2014/11/20   zhaotianyong    [Android5.0][Email]The display content will change in account settings
 *BUGFIX-845676  2014/11/21   wenggangjin     [Android5.0][Email]Can't check the "Default account" in Email settings
 *BUGFIX-844548  2014/11/24   wenggangjin     [Android5.0][Email] No response when selecting Help and Send feedback in Email settings
 *BUGFIX-854751  2014/12/01   wenggangjin     [Android5.0][Exchange]It's hard to check "Sync email","Sync contacts","Sync calendar" in account settings
 *BUGFIX-927973  2015/02/12   zheng.zou       [FC][Email]Email appear FC
 *BUGFIX-936696  2015/03/01   chenyanhua      Incorrect cooperate email default ringtone string.
 *BUGFIX-929959  2015/3/1     junwei-xu       [Email]Interface abnormal when rotate screen after locking screen
 *BUGFIX-927828  2015/03/06   gengkexue       [Email]The default account can't be marked
 *BUGFIX-944202  2015/03/11   zheng.zou       [Email]Combined view still sync after remove one email account during syncing
 *BUGFIX-947676  2015/03/13   zhonghua.tuo    [REG][Email]The ringtone name display abnormal when choose default rintone as "None"
 *BUGFIX-979513  2015/04/22   ke.ma           [REG][Email]Email ringtone display wrong when delete the uer-defined ringtone
 *BUGFIX-1008364 2015/04/22   ZhangChao       [Monitor][Android5.0][Email]Some items display in email settings and no action when tap it.
 *BUGFIX-1019271 2015/06/18   ZhangChao       [Monitor][Email]All account settings display gray
 *FEATURE-ID     2015/08/12   Gantao          FEATURE--Always show pictures
 *BUGFIX-1093309 2015/09/29   junwei-xu       <13340Track><26><CDR-EAS-030>Synchronization Scope—Calendar Events
 *BUGFIX-708637  2015/10/19   kaifeng.lu      [PR check][Sprint][Email]"Sync Email from："menu without title in settings
 *BUGFIX-981169  2015/11/25   jian.xu         Remove account dialog style update from ergo5.2.8
 *BUGFIX_1115085 2015/11/23 yanhua.chen      [Monkey][CRASH]CRASH: com.tct.email
 *BUGFIX-956919  2015/12/03   yanhua.chen     [Android L][Email][UE]The font of "Email account" string change to black after rorating screen and the check box style is not consistent after rotating screen..
 *BUGFIX_1058396 2015/12/08   zheng.zou       [Android 6.0][Email][Force close]Load account settings occured FC when set the ringtone as More Ringtones
 *BUGFIX_1175479 2015/12/17   yanhua.chen     [Email]The account ringtone show unkown when select "Default notifiction sound"
 *BUGFIX-1270994  2016/1/5      xing.zhao       [Android 6.0][Email][Ergo]The text 'Settings' is displayed behind back arrows.
 *BUGFIX_1453106 2015/1/22     yanhua.chen      [GAPP][Email]The account ringtone show unkown when select "Default notifiction sound"
 *BUGFIX-1394344 2016/01/11    jian.xu          [Monkey][Email]CRASH: com.tct.email during monkey test
 *BUGFIX-1658378 2016/03/21    junwei-xu        [Email]Email ringtone list display not correct after delete ringtone from download list
 *BUGFIX-1804474 2016/03/21    rong-tang        [Monitor][Email][FOTA]The Email notification settings is grey after Fota
 *BUGFIX-1909256 2016-04-05   junwei-xu       [HZ-IUT][Email]Email ringtone will not change after select ringtone from SD card.
 *BUGFIX-1920899 2016-04-14   junwei-xu       [Android M][Email]Don't display none ringtone in "Choose ringtone"
 ====================================================================================================================
 */

package com.tct.email.activity.setup;

import android.Manifest;
import android.accounts.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tct.email.EmailAddressValidator;
import com.tct.email.R;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.provider.Policy;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.ui.ActionableToastBar;
import com.tct.mail.ui.ToastBarOperation;
import com.tct.mail.utils.LogUtils;
import com.tct.email.SecurityPolicy;
import com.tct.email.provider.EmailProvider;
import com.tct.email.provider.FolderPickerActivity;
import com.tct.email.service.EmailServiceUtils;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.preferences.AccountPreferences;
import com.tct.mail.preferences.FolderPreferences;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.ui.MailAsyncTaskLoader;
import com.tct.mail.ui.settings.MailAccountPrefsFragment;
import com.tct.mail.ui.settings.SettingsUtils;
import com.tct.mail.utils.NotificationUtils;
import com.tct.mail.utils.ContentProviderTask.UpdateTask;
import com.tct.mail.utils.PLFUtils;
import com.tct.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//[FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110

//[FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110

/**
 * Fragment containing the main logic for account settings.  This also calls out to other
 * fragments for server settings.
 *
 * TODO: Can we defer calling addPreferencesFromResource() until after we load the account?  This
 *       could reduce flicker.
 */
public class AccountSettingsFragment extends MailAccountPrefsFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String ARG_ACCOUNT_ID = "account_id";

    public static final String PREFERENCE_DESCRIPTION = "account_description";
    private static final String PREFERENCE_NAME = "account_name";
    private static final String PREFERENCE_SIGNATURE = "account_signature";
    //[FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110
    private static final String PREFERENCE_OOF_SETTINGS = "account_oofsettings";
    //[FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110
    private static final String PREFERENCE_QUICK_RESPONSES = "account_quick_responses";
    private static final String PREFERENCE_FREQUENCY = "account_check_frequency";
    private static final String PREFERENCE_SYNC_WINDOW = "account_sync_window";
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
    private static final String PREFERENCE_SYNC_CALENDAR_WINDOW = "sync_calendar_window";
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
    private static final String PREFERENCE_SYNC_SETTINGS = "account_sync_settings";
    private static final String PREFERENCE_SYNC_EMAIL = "account_sync_email";
    private static final String PREFERENCE_SYNC_CONTACTS = "account_sync_contacts";
    private static final String PREFERENCE_SYNC_CALENDAR = "account_sync_calendar";
    private static final String PREFERENCE_BACKGROUND_ATTACHMENTS =
            "account_background_attachments";
    private static final String PREFERENCE_DEFAULT = "account_default";
    private static final String PREFERENCE_CATEGORY_DATA_USAGE = "data_usage";
    private static final String PREFERENCE_CATEGORY_NOTIFICATIONS = "account_notifications";
    private static final String PREFERENCE_CATEGORY_SERVER = "account_servers";
    private static final String PREFERENCE_CATEGORY_POLICIES = "account_policies";
    private static final String PREFERENCE_CATEGORY_DELETE = "delete_account_category";
    @SuppressWarnings("unused") // temporarily unused pending policy UI
    private static final String PREFERENCE_POLICIES_ENFORCED = "policies_enforced";
    @SuppressWarnings("unused") // temporarily unused pending policy UI
    private static final String PREFERENCE_POLICIES_UNSUPPORTED = "policies_unsupported";
    private static final String PREFERENCE_POLICIES_RETRY_ACCOUNT = "policies_retry_account";
    private static final String PREFERENCE_INCOMING = "incoming";
    private static final String PREFERENCE_OUTGOING = "outgoing";

    private static final String PREFERENCE_SYSTEM_FOLDERS = "system_folders";
    private static final String PREFERENCE_SYSTEM_FOLDERS_TRASH = "system_folders_trash";
    private static final String PREFERENCE_SYSTEM_FOLDERS_SENT = "system_folders_sent";

    private static final String PREFERENCE_MANUAL_SYNC_WHEN_ROAMING = "manual_sync_when_roaming";   //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD
    private static final String PREFERENCE_CATEGORY_SERVER_POLICY = "server_policy";   //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD

    private static final String SAVESTATE_SYNC_INTERVALS = "savestate_sync_intervals";
    private static final String SAVESTATE_SYNC_INTERVAL_STRINGS = "savestate_sync_interval_strings";

    //TS: junwei-xu 2015-3-1 EMAIL BUGFIX_929959 ADD_S
    private static final String SAVESTATE_SYNC_WINDOWS = "savestate_sync_window";
    private static final String SAVESTATE_SYNC_WINDOWS_STRINGS = "savestate_sync_window_strings";
    //TS: junwei-xu 2015-3-1 EMAIL BUGFIX_929959 ADD_E
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
    private static final String SAVESTATE_SYNC_CALENDAR_WINDOWS = "savestate_sync_calendar_window";
    private static final String SAVESTATE_SYNC_CALENDAR_WINDOWS_STRINGS = "savestate_sync_window_calendar_strings";
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
    static final String EXTRA_RINGTONE_NOTSHOW_NONE = "Email.intent.extra.ringtone.NOT_SHOWNONE";
    // Request code to start different activities.
    private static final int RINGTONE_REQUEST_CODE = 0;

    private static final String PREFERENCE_DELETE_ACCOUNT = "delete_account";
    private EditTextPreference mAccountDescription;
    private EditTextPreference mAccountName;
    private EditTextPreference mAccountSignature;
    private ListPreference mCheckFrequency;
    private ListPreference mSyncWindow;
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
    private ListPreference mSyncCalendarWindow;
    //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
    private Preference mSyncSettings;
    private CheckBoxPreference mAccountDefault;
    private CheckBoxPreference mInboxVibrate;
  //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_S
    private CheckBoxPreference syncContacts;
    private CheckBoxPreference syncCalendar;
    private CheckBoxPreference syncEmail;
  //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_E
    private Preference mInboxRingtone;

    private Preference mOofSettings;//[FEATURE]-ADD by TSNJ,wenlu.wu,10/20/2014,FR-719110
    private Context mContext;
    private long mDefaultAccountId = Account.NO_ACCOUNT; // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD
    private Account mAccount;
    private com.tct.mail.providers.Account mUiAccount;
    private EmailServiceInfo mServiceInfo;
    private Folder mInboxFolder;

    private Ringtone mRingtone;
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    private Uri mTempRingtoneUri;
    private ActionableToastBar mToastBar;
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E

    /**
     * This may be null if the account exists but the inbox has not yet been created in the database
     * (waiting for initial sync)
     */
    private FolderPreferences mInboxFolderPreferences;

    // The email of the account being edited
    private String mAccountEmail;

    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    private static final String PREFERENCE_DOWNLOAD_OPTIONS = "account_download_options";
    private ListPreference mDownloadOptions;
    //[FEATURE]-Add-END by TSCD.Chao Zhang

    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
    private static final String PREFERENCE_INLINE_IMAGES = "account_inline_images";
    private ListPreference mInlineImages;
    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_E

    // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S
    private static final String PREFERENCE_CATEGORY_REPLY_TO = "account_reply_to_cate";
    private static final String PREFERENCE_REPLY_TO = "account_reply_to";
    private EditTextPreference mReplyToPreference;
    // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E

    /**
     * If launching with an email address, use this method to build the arguments.
     */
    public static Bundle buildArguments(final String email) {
        final Bundle b = new Bundle(1);
        b.putString(ARG_ACCOUNT_EMAIL, email);
        return b;
    }
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    public CharSequence getDownloadSummary(int id)
    {
        switch(id)
        {
            case 0:
            {
                return getText(R.string.account_setting_download_opt_headonly);
            }
            case 2 * 1024:
            {
                return getText(R.string.account_setting_download_opt_2k);
            }
            case 5 * 1024:
            {
                return getText(R.string.account_setting_download_opt_5k);
            }
            case 10 * 1024:
            {
                return getText(R.string.account_setting_download_opt_10k);
            }
            case 20 * 1024:
            {
                return getText(R.string.account_setting_download_opt_20k);
            }
            case 50 * 1024:
            {
                return getText(R.string.account_setting_download_opt_50k);
            }
            case 100 * 1024:
            {
                return getText(R.string.account_setting_download_opt_100k);
            }
            case Utility.ENTIRE_MAIL:
            {
                return getText(R.string.account_setting_download_opt_all);
            }
            default:
            {
                return getText(R.string.account_setting_download_opt_all);
            }
        }
    }
    //[FEATURE]-Add-END by TSCD.Chao Zhang

    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
    private CharSequence getInlineImageSummary(int value) {
        switch (value) {
            case 0:
                return getText(R.string.account_settings_inline_always_show);
            case 1:
                return getText(R.string.account_settings_inline_ask_before_show);
            default:
                return getText(R.string.account_settings_inline_always_show);
        }
    }
    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_E
    /**
     * If launching with an account ID, use this method to build the arguments.
     */
    public static Bundle buildArguments(final long accountId) {
        final Bundle b = new Bundle(1);
        b.putLong(ARG_ACCOUNT_ID, accountId);
        return b;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before {@link #onActivityCreated(Bundle)}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_844548 MOD_S
        //setHasOptionsMenu(true);
        setHasOptionsMenu(false);
        //TS: wenggangjin 2014-11-21 EMAIL BUGFIX_844548 MOD_E
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.account_settings_preferences);

        if (!getResources().getBoolean(R.bool.quickresponse_supported)) {
            final Preference quickResponsePref = findPreference(PREFERENCE_QUICK_RESPONSES);
            if (quickResponsePref != null) {
                getPreferenceScreen().removePreference(quickResponsePref);
            }
        }

        // TS: zhaotianyong 2014-11-20 EXCHANGE BUGFIX_845093 MOD_S
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //if (!getResources().getBoolean(R.bool.feature_email_oof_on)) {
        boolean oofEnable = PLFUtils.getBoolean(mContext, "feature_email_oof_on");
        if (!oofEnable) {
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
            final Preference oofSettingsPref = findPreference(PREFERENCE_OOF_SETTINGS);
            if (oofSettingsPref != null) {
                getPreferenceScreen().removePreference(oofSettingsPref);
                }
         }
        // TS: zhaotianyong 2014-11-20 EXCHANGE BUGFIX_845093 MOD_E

        // TS: wenggangjin 2014-11-21 EMAIL BUGFIX_845676 ADD_S
        // TS: junwei-xu 2014-12-03 EMAIL READ_PLF MOD_S
        //if (!getResources().getBoolean(R.bool.feature_email_defaultAccount_on)) {
        boolean defaultAccountEnable = PLFUtils.getBoolean(mContext, "feature_email_defaultAccount_on");
        if (!defaultAccountEnable) {
        // TS: junwei-xu 2014-12-03 EMAIL READ_PLF MOD_E
            final Preference defaultAccountPref = findPreference(PREFERENCE_DEFAULT);
            if (defaultAccountPref != null) {
                getPreferenceScreen().removePreference(defaultAccountPref);
            }
         }
        // TS: wenggangjin 2014-11-21 EMAIL BUGFIX_845676 ADD_E

        //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 11/24/2014 FR847616
        // TS: junwei-xu 2014-12-03 EMAIL READ_PLF MOD_S
        //if(!getResources().getBoolean(R.bool.feature_email_removeAccount_on)){
        boolean removeAccountEnable = PLFUtils.getBoolean(mContext, "feature_email_removeAccount_on");
        if (!removeAccountEnable) {
            // TS: junwei-xu 2014-12-03 EMAIL READ_PLF MOD_E
            final Preference deleteAccountPref=findPreference(PREFERENCE_DELETE_ACCOUNT);
            if(deleteAccountPref != null){
                getPreferenceScreen().removePreference(deleteAccountPref);
            }
        }
        //[FEATURE]-ADD-END by TSNJ.wei huang
        // Start loading the account data, if provided in the arguments
        // If not, activity must call startLoadingAccount() directly
        Bundle b = getArguments();
        if (b != null) {
            mAccountEmail = b.getString(ARG_ACCOUNT_EMAIL);
        }
        if (savedInstanceState != null) {
            // We won't know what the correct set of sync interval values and strings are until
            // our loader completes. The problem is, that if the sync frequency chooser is
            // displayed when the screen rotates, it reinitializes it to the defaults, and doesn't
            // correct it after the loader finishes again. See b/13624066
            // To work around this, we'll save the current set of sync interval values and strings,
            // in onSavedInstanceState, and restore them here.
            final CharSequence [] syncIntervalStrings =
                    savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_INTERVAL_STRINGS);
            final CharSequence [] syncIntervals =
                    savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_INTERVALS);
            mCheckFrequency = (ListPreference) findPreference(PREFERENCE_FREQUENCY);
            if (mCheckFrequency != null) {
                mCheckFrequency.setEntries(syncIntervalStrings);
                mCheckFrequency.setEntryValues(syncIntervals);
            }
            //TS: junwei-xu 2015-3-1 EMAIL BUGFIX_929959 ADD_S
            final PreferenceCategory dataUsageCategory =
                    (PreferenceCategory) findPreference(PREFERENCE_CATEGORY_DATA_USAGE);
            if (savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_WINDOWS_STRINGS) != null) {
                mSyncWindow = new ListPreference(mContext);
                mSyncWindow.setKey(PREFERENCE_SYNC_WINDOW);
                dataUsageCategory.addPreference(mSyncWindow);
            }
            if (mSyncWindow != null) {
                mSyncWindow.setEntries(savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_WINDOWS_STRINGS));
                mSyncWindow.setEntryValues(savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_WINDOWS));
                //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
                mSyncWindow.setDialogTitle(R.string.account_setup_options_mail_window_label);
                //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
            }
            //TS: junwei-xu 2015-3-1 EMAIL BUGFIX_929959 ADD_E
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
            if (savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_CALENDAR_WINDOWS_STRINGS) != null) {
                mSyncCalendarWindow = new ListPreference(mContext);
                mSyncCalendarWindow.setKey(PREFERENCE_SYNC_CALENDAR_WINDOW);
                dataUsageCategory.addPreference(mSyncCalendarWindow);
            }
            if (mSyncCalendarWindow != null) {
                mSyncCalendarWindow.setEntries(savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_CALENDAR_WINDOWS_STRINGS));
                mSyncCalendarWindow.setEntryValues(savedInstanceState.getCharSequenceArray(SAVESTATE_SYNC_CALENDAR_WINDOWS));
                //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_S
                mSyncCalendarWindow.setDialogTitle(R.string.account_setup_options_mail_calendar_window_label);
                //TS: yanhua.chen 2015-12-03 EMAIL BUGFIX_956919 ADD_E
            }
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
        }
    }

    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    //Note: override onCreateView(), add floating view to preferences view's bottom.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout settingsView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        View floatingView = inflater.inflate(R.layout.floating_actions, null);
        mToastBar = (ActionableToastBar) floatingView.findViewById(R.id.toast_bar);
        View composeButton = floatingView.findViewById(R.id.compose_button);
        if (composeButton != null) {
            composeButton.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        settingsView.addView(floatingView, params);
        return settingsView;
    }
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E

    @Override
    public void onSaveInstanceState(@NonNull Bundle outstate) {
        super.onSaveInstanceState(outstate);
        if (mCheckFrequency != null) {
            outstate.putCharSequenceArray(SAVESTATE_SYNC_INTERVAL_STRINGS,
                    mCheckFrequency.getEntries());
            outstate.putCharSequenceArray(SAVESTATE_SYNC_INTERVALS,
                    mCheckFrequency.getEntryValues());
        }
        //TS: junwei-xu 2015-3-1 EMAIL BUGFIX_929959 ADD_S
        if (mSyncWindow != null) {
            outstate.putCharSequenceArray(SAVESTATE_SYNC_WINDOWS_STRINGS,
                    mSyncWindow.getEntries());
            outstate.putCharSequenceArray(SAVESTATE_SYNC_WINDOWS,
                    mSyncWindow.getEntryValues());
        }
        //TS: junwei-xu 2015-3-1 EMAIL BUGFIX_929959 ADD_E
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
        if (mSyncCalendarWindow != null) {
            outstate.putCharSequenceArray(SAVESTATE_SYNC_CALENDAR_WINDOWS_STRINGS,
                    mSyncCalendarWindow.getEntries());
            outstate.putCharSequenceArray(SAVESTATE_SYNC_CALENDAR_WINDOWS,
                    mSyncCalendarWindow.getEntryValues());
        }
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Bundle args = new Bundle(1);
        if (!TextUtils.isEmpty(mAccountEmail)) {
            args.putString(AccountLoaderCallbacks.ARG_ACCOUNT_EMAIL, mAccountEmail);
        } else {
            args.putLong(AccountLoaderCallbacks.ARG_ACCOUNT_ID,
                    getArguments().getLong(ARG_ACCOUNT_ID, -1));
        }
        //TS: xing.zhao 2016-1-5 EMAIL BUGFIX_1270994 ADD_S
        String senderName = mAccountEmail;
        if (TextUtils.isEmpty(senderName)) {
            senderName = getResources().getString(R.string.settings_activity_title);
        }
        if (getActivity() != null) {
            getActivity().setTitle(senderName);
        }
        //TS: xing.zhao 2016-1-5 EMAIL BUGFIX_1270994 ADD_E
        //TS: zhangchao 2015-5-26 EMAIL BUGFIX_1008364 ADD_S
        //NOTE: In some case,the account loader do not return data Immediately,but user checked the account settings,
        // and some not supported perferences or screen shows,it normal behavoir because we disable some
        // not supported perference screen after the account info loaded,So For better user effect,just
        // disable the screend during loading.
        getPreferenceScreen().setEnabled(false);
        //TS: zhangchao 2015-5-26 EMAIL BUGFIX_1008364 ADD_E
        getLoaderManager().initLoader(0, args, new AccountLoaderCallbacks(getActivity()));
    }

    //TS: junwei-xu 2016-03-21 EMAIL BUGFIX-1658378 ADD_S
    @Override
    public void onResume() {
        super.onResume();
        //Note: Refresh ringtone summary maybe user has delete it.
        if (mInboxFolderPreferences != null) {
            String ringtoneUri = mInboxFolderPreferences.getNotificationRingtoneUri();
            if (!TextUtils.isEmpty(ringtoneUri)) {
                if (ringtoneUri != Settings.System.DEFAULT_NOTIFICATION_URI.toString()) {
                    if (!isFileExist(Uri.parse(ringtoneUri))) {
                        ringtoneUri = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
                        Log.i("RINGTONE_URI", "when ringtone file is null ,ringtoneUri:" + ringtoneUri);
                        setRingtone(Uri.parse(ringtoneUri));
                    }
                }
            }
            //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 ADD_S
            setRingtoneSummary(ringtoneUri);
            //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 ADD_E
        }
    }
    //TS: junwei-xu 2016-03-21 EMAIL BUGFIX-1658378 ADD_E

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RINGTONE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    setRingtone(uri);
                    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
                    //Note: init temp ringtone uri after user picked.
                    mTempRingtoneUri = uri;
                    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
                }
                break;
        }
    }

    /**
     * Sets the current ringtone.
     */
    private void setRingtone(Uri ringtone) {
        //TS: yanhua.chen 2015-11-23 EMAIL BUGFIX_1115085 ADD_S
        if(mInboxFolderPreferences == null){
            return;
        }
        //TS: yanhua.chen 2015-11-23 EMAIL BUGFIX_1115085 ADD_E
        if (ringtone != null) {
            mInboxFolderPreferences.setNotificationRingtoneUri(ringtone.toString());
            mRingtone = RingtoneManager.getRingtone(getActivity(), ringtone);
        } else {
            // Null means silent was selected.
            mInboxFolderPreferences.setNotificationRingtoneUri("");
            mRingtone = null;
        }
        //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 MOD_S
        setRingtoneSummary(ringtone != null ? ringtone.toString() : null);// TS: chenyanhua 2015-03-01 EMAIL BUGFIX_936696 MOD
        //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 MOD_E
    }

    //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 MOD_S
    //Note: must consider ringtone string is null, when user select ringtone as None.
    private void setRingtoneSummary(String ringtoneString) {// TS: chenyanhua 2015-03-01 EMAIL BUGFIX_936696 MOD
        Uri ringtone = TextUtils.isEmpty(ringtoneString) ? null : Uri.parse(ringtoneString);
        final String summary = mRingtone != null ? mRingtone.getTitle(mContext)
                : mContext.getString(R.string.silent_ringtone);
        // TS: chenyanhua 2015-03-01 EMAIL BUGFIX_936696 MOD_S
        // TS: zhonghua.tuo 2015-03-13 EMAIL BUGFIX_947676 MOD_S
        if (RingtoneManager.isDefault(ringtone) && !summary.contains(mContext.getString(R.string.default_ringtone))) {
        // TS: zhonghua.tuo 2015-03-13 EMAIL BUGFIX_947676 MOD_E
            //TS: yanhua.chen 2015-1-22 EMAIL BUGFIX_1453106 MOD_S
            if (summary.contains(mContext.getString(R.string.default_ringtone_unkonwn))) {
                mInboxRingtone.setSummary(mContext.getString(R.string.account_setup_exchange_no_certificate));
            } else {
                mInboxRingtone.setSummary(mContext.getString(R.string.default_ringtone)+" ("+summary+")");
            }
            //TS: yanhua.chen 2015-1-22 EMAIL BUGFIX_1453106 MOD_E
        } else {
            //TS: yanhua.chen 2015-12-17 EMAIL BUGFIX_1175479 MOD_S
            if (RingtoneManager.isDefault(ringtone) && summary.contains(mContext.getString(R.string.default_ringtone_unkonwn))) {
                //[BUGFIX]-MOD-BEGIN -by SCDTABLET.shujing.jin,04/22/2016,1963018,
                //mInboxRingtone.setSummary(mContext.getString(R.string.account_setup_exchange_no_certificate));
                mInboxRingtone.setSummary(mContext.getString(R.string.default_ringtone)+" ("+mContext.getString(R.string.silent_ringtone)+")");
                ////[BUGFIX]-MOD-END-by SCDTABLET.shujing.jin
            } else {
                mInboxRingtone.setSummary(summary);
            }
            //TS: yanhua.chen 2015-12-17 EMAIL BUGFIX_1175479 MOD_E
        }
//        mInboxRingtone.setSummary(summary);
     // TS: chenyanhua 2015-03-01 EMAIL BUGFIX_936696 MOD_E
    }
    //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 MOD_E

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            @NonNull Preference preference) {
        final String key = preference.getKey();
        if (key.equals(PREFERENCE_SYNC_SETTINGS)) {
            startActivity(MailboxSettings.getIntent(getActivity(), mUiAccount.fullFolderListUri,
                    mInboxFolder));
            return true;
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    /**
     * Listen to all preference changes in this class.
     * @param preference The changed Preference
     * @param newValue The new value of the Preference
     * @return True to update the state of the Preference with the new value
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Can't use a switch here. Falling back to a giant conditional.
        final String key = preference.getKey();
        final ContentValues cv = new ContentValues(1);
        if (key.equals(PREFERENCE_DESCRIPTION)){
            String summary = newValue.toString().trim();
            if (TextUtils.isEmpty(summary)) {
                summary = mUiAccount.getEmailAddress();
            }
            mAccountDescription.setSummary(summary);
            mAccountDescription.setText(summary);
            cv.put(AccountColumns.DISPLAY_NAME, summary);
        } else if (key.equals(PREFERENCE_NAME)) {
            final String summary = newValue.toString().trim();
            if (!TextUtils.isEmpty(summary)) {
                mAccountName.setSummary(summary);
                mAccountName.setText(summary);
                cv.put(AccountColumns.SENDER_NAME, summary);
            }
        } else if (key.equals(PREFERENCE_SIGNATURE)) {
            // Clean up signature if it's only whitespace (which is easy to do on a
            // soft keyboard) but leave whitespace in place otherwise, to give the user
            // maximum flexibility, e.g. the ability to indent
            String signature = newValue.toString();
            if (signature.trim().isEmpty()) {
                signature = "";
            }
            mAccountSignature.setText(signature);
            SettingsUtils.updatePreferenceSummary(mAccountSignature, signature,
                    R.string.preferences_signature_summary_not_set);
            cv.put(AccountColumns.SIGNATURE, signature);
        } else if (key.equals(PREFERENCE_FREQUENCY)) {
            final String summary = newValue.toString();
            final int index = mCheckFrequency.findIndexOfValue(summary);
            mCheckFrequency.setSummary(mCheckFrequency.getEntries()[index]);
            mCheckFrequency.setValue(summary);
            if (mServiceInfo.syncContacts || mServiceInfo.syncCalendar) {
                // This account allows syncing of contacts and/or calendar, so we will always have
                // separate preferences to enable or disable syncing of email, contacts, and
                // calendar.
                // The "sync frequency" preference really just needs to control the frequency value
                // in our database.
                cv.put(AccountColumns.SYNC_INTERVAL, Integer.parseInt(summary));
            } else {
                // This account only syncs email (not contacts or calendar), which means that we
                // will hide the preference to turn syncing on and off. In this case, we want the
                // sync frequency preference to also control whether or not syncing is enabled at
                // all. If sync is turned off, we will display "sync never" regardless of what the
                // numeric value we have stored says.
                final android.accounts.Account androidAcct = new android.accounts.Account(
                        mAccount.mEmailAddress, mServiceInfo.accountType);
                if (Integer.parseInt(summary) == Account.CHECK_INTERVAL_NEVER) {
                    // Disable syncing from the account manager. Leave the current sync frequency
                    // in the database.
                    ContentResolver.setSyncAutomatically(androidAcct, EmailContent.AUTHORITY,
                            false);
                    //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 ADD_S
                    //need to save the setting to db to keep consistency
                    cv.put(AccountColumns.SYNC_INTERVAL, Integer.parseInt(summary));
                    //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 ADD_E
                } else {
                    // Enable syncing from the account manager.
                    ContentResolver.setSyncAutomatically(androidAcct, EmailContent.AUTHORITY,
                            true);
                    cv.put(AccountColumns.SYNC_INTERVAL, Integer.parseInt(summary));
                }
            }
        } else if (key.equals(PREFERENCE_SYNC_WINDOW)) {
            final String summary = newValue.toString();
            int index = mSyncWindow.findIndexOfValue(summary);
            mSyncWindow.setSummary(mSyncWindow.getEntries()[index]);
            mSyncWindow.setValue(summary);
            cv.put(AccountColumns.SYNC_LOOKBACK, Integer.parseInt(summary));
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
        } else if (key.equals(PREFERENCE_SYNC_CALENDAR_WINDOW)) {
            final String summary = newValue.toString();
            int index = mSyncCalendarWindow.findIndexOfValue(summary);
            mSyncCalendarWindow.setSummary(mSyncCalendarWindow.getEntries()[index]);
            mSyncCalendarWindow.setValue(summary);
            cv.put(AccountColumns.SYNC_CALENDAR_LOOKBACK, Integer.parseInt(summary));
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
        } else if (key.equals(PREFERENCE_SYNC_EMAIL)) {
            final android.accounts.Account androidAcct = new android.accounts.Account(
                    mAccount.mEmailAddress, mServiceInfo.accountType);
            ContentResolver.setSyncAutomatically(androidAcct, EmailContent.AUTHORITY,
                    (Boolean) newValue);
            //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_S
            syncEmail.setChecked((Boolean) newValue);
            //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_E
            loadSettings();
        } else if (key.equals(PREFERENCE_SYNC_CONTACTS)) {
            final android.accounts.Account androidAcct = new android.accounts.Account(
                    mAccount.mEmailAddress, mServiceInfo.accountType);
            ContentResolver.setSyncAutomatically(androidAcct, ContactsContract.AUTHORITY,
                    (Boolean) newValue);
            //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_S
            syncContacts.setChecked((Boolean) newValue);
            //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_E
            loadSettings();
        } else if (key.equals(PREFERENCE_SYNC_CALENDAR)) {
            final android.accounts.Account androidAcct = new android.accounts.Account(
                    mAccount.mEmailAddress, mServiceInfo.accountType);
            ContentResolver.setSyncAutomatically(androidAcct, CalendarContract.AUTHORITY,
                    (Boolean) newValue);
            //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_S
            syncCalendar.setChecked((Boolean) newValue);
            //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_E
            loadSettings();
        } else if (key.equals(PREFERENCE_BACKGROUND_ATTACHMENTS)) {
            //TS: jian.xu 2016-01-11 EMAIL BUGFIX-1394344 ADD_S
            //Note: sometimes mAccount maybe null, just return.
            if (mAccount == null) return false;
            //TS: jian.xu 2016-01-11 EMAIL BUGFIX-1394344 ADD_E
            int newFlags = mAccount.getFlags() & ~(Account.FLAGS_BACKGROUND_ATTACHMENTS);

            newFlags |= (Boolean) newValue ?
                    Account.FLAGS_BACKGROUND_ATTACHMENTS : 0;

            cv.put(AccountColumns.FLAGS, newFlags);
        } else if (FolderPreferences.PreferenceKeys.NOTIFICATIONS_ENABLED.equals(key)) {
            mInboxFolderPreferences.setNotificationsEnabled((Boolean) newValue);
            return true;
        } else if (FolderPreferences.PreferenceKeys.NOTIFICATION_VIBRATE.equals(key)) {
            final boolean vibrateSetting = (Boolean) newValue;
            mInboxVibrate.setChecked(vibrateSetting);
            mInboxFolderPreferences.setNotificationVibrateEnabled(vibrateSetting);
            return true;
        } else if (FolderPreferences.PreferenceKeys.NOTIFICATION_RINGTONE.equals(key)) {
            return true;
         // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
        }else if(key.equals(PREFERENCE_INLINE_IMAGES)){
            final String summary = newValue.toString();
            mInlineImages.setValue(summary);
            mInlineImages.setSummary(getInlineImageSummary(Integer.parseInt(summary)));
            cv.put(AccountColumns.INLINE_IMAGES, Integer.parseInt(summary));
         // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_E
        } else if(key.equals(PREFERENCE_DOWNLOAD_OPTIONS)){
            final String summary = newValue.toString();
            mDownloadOptions.setValue(summary);
            mDownloadOptions.setSummary(getDownloadSummary(Integer.parseInt(summary)));
            cv.put(AccountColumns.DOWNLOAD_OPTIONS, Integer.parseInt(summary));
        // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD_S
        } else if(key.equals(PREFERENCE_DEFAULT)) {
            if(mDefaultAccountId != -1 && mAccount.mId != mDefaultAccountId) {
                Account account = Account.restoreAccountWithId(getActivity(), mDefaultAccountId);
                if(account != null) {
                    cv.put(AccountColumns.IS_DEFAULT, 0);
                    account.update(getActivity(), cv);
                }
            }
            cv.put(AccountColumns.IS_DEFAULT, !mAccountDefault.isChecked() ? 1 : 0);
            mAccount.update(getActivity(), cv);
            return true;
        // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD_E
            // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S
        } else if(key.equals(PREFERENCE_REPLY_TO)) {
            String summary = newValue.toString().trim();
            EmailAddressValidator validator = new EmailAddressValidator();
            boolean isValidAddress = validator.isValid(summary);
            if(!isValidAddress) {
                //Invalid address is not allowed.
                Utility.showToast(mContext, R.string.account_settings_reply_to_invalid);
                summary = "";
            }
            mReplyToPreference.setSummary(summary);
            mReplyToPreference.setText(summary);
            cv.put(AccountColumns.REPLY_TO, summary);
        }
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E
        else {
            // Default behavior, just indicate that the preferences were written
            LogUtils.d(LogUtils.TAG, "Unknown preference key %s", key);
            return true;
        }
        if (cv.size() > 0) {
            new UpdateTask().run(mContext.getContentResolver(), mAccount.getUri(), cv, null, null);
            MailActivityEmail.setServicesEnabledAsync(mContext);
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	//TS: wenggangjin 2014-11-21 EMAIL BUGFIX_844548 MOD_S
//        menu.clear();
//        inflater.inflate(R.menu.settings_fragment_menu, menu);
    	//TS: wenggangjin 2014-11-21 EMAIL BUGFIX_844548 MOD_E
    }

    /**
     * Async task loader to load account in order to view/edit it
     */
    private static class AccountLoader extends MailAsyncTaskLoader<Map<String, Object>> {
        public static final String RESULT_KEY_ACCOUNT = "account";
        private static final String RESULT_KEY_UIACCOUNT_CURSOR = "uiAccountCursor";
        public static final String RESULT_KEY_UIACCOUNT = "uiAccount";
        public static final String RESULT_KEY_INBOX = "inbox";
        public static final String DEFAULT_ACCOUNT = "default_account";

        private final ForceLoadContentObserver mObserver;
        private final String mAccountEmail;
        private final long mAccountId;

        private AccountLoader(Context context, String accountEmail, long accountId) {
            super(context);
            mObserver = new ForceLoadContentObserver();
            mAccountEmail = accountEmail;
            mAccountId = accountId;
        }

        @Override
        public Map<String, Object> loadInBackground() {
            LogUtils.d(Logging.LOG_TAG, "begin to load AccountInfo useing loader,mAccountEmail-->",
                    mAccountEmail);
            final Map<String, Object> map = new HashMap<>();

            final Account account;
            if (!TextUtils.isEmpty(mAccountEmail)) {
                account = Account.restoreAccountWithAddress(getContext(), mAccountEmail, mObserver);
            } else {
                account = Account.restoreAccountWithId(getContext(), mAccountId, mObserver);
            }
            if (account == null) {
                LogUtils.i(LogUtils.TAG, "AccountLoader.loadInBackground()...   account is null");
                return map;
            }

            map.put(RESULT_KEY_ACCOUNT, account);

            // We don't monitor these for changes, but they probably won't change in any meaningful
            // way
            account.getOrCreateHostAuthRecv(getContext());
            account.getOrCreateHostAuthSend(getContext());

            if (account.mHostAuthRecv == null) {
                LogUtils.i(LogUtils.TAG, "AccountLoader.loadInBackground()...   account.mHostAuthRecv is null");
                return map;
            }

            account.mPolicy =
                    Policy.restorePolicyWithId(getContext(), account.mPolicyKey, mObserver);

            final Cursor uiAccountCursor = getContext().getContentResolver().query(
                    EmailProvider.uiUri("uiaccount", account.getId()),
                    UIProvider.ACCOUNTS_PROJECTION,
                    null, null, null);

            if (uiAccountCursor != null) {
                map.put(RESULT_KEY_UIACCOUNT_CURSOR, uiAccountCursor);
                uiAccountCursor.registerContentObserver(mObserver);
            } else {
                LogUtils.i(LogUtils.TAG, "AccountLoader.loadInBackground()...   uiAccountCursor is null.");
                return map;
            }

            if (!uiAccountCursor.moveToFirst()) {
                LogUtils.i(LogUtils.TAG, "AccountLoader.loadInBackground()...   uiAccountCursor.moveToFirst() return false.");
                return map;
            }

            final com.tct.mail.providers.Account uiAccount =
                    com.tct.mail.providers.Account.builder().buildFrom(uiAccountCursor);

            map.put(RESULT_KEY_UIACCOUNT, uiAccount);

            final Cursor folderCursor = getContext().getContentResolver().query(
                    uiAccount.settings.defaultInbox, UIProvider.FOLDERS_PROJECTION, null, null,
                    null);

            final Folder inbox;
            try {
                if (folderCursor != null && folderCursor.moveToFirst()) {
                    inbox = new Folder(folderCursor);
                } else {
                    LogUtils.i(LogUtils.TAG, "AccountLoader.loadInBackground()...   folderCursor is null or folderCursor.moveToFirst() return false.");
                    return map;
                }
            } finally {
                if (folderCursor != null) {
                    folderCursor.close();
                }
            }
            long defaultAccountId = Account.getDefaultAccountId(getContext());

            map.put(RESULT_KEY_INBOX, inbox);
            map.put(DEFAULT_ACCOUNT, Long.valueOf(defaultAccountId));
            return map;
        }

        @Override
        protected void onDiscardResult(Map<String, Object> result) {
            final Account account = (Account) result.get(RESULT_KEY_ACCOUNT);
            if (account != null) {
                if (account.mPolicy != null) {
                    account.mPolicy.close(getContext());
                }
                account.close(getContext());
            }
            final Cursor uiAccountCursor = (Cursor) result.get(RESULT_KEY_UIACCOUNT_CURSOR);
            if (uiAccountCursor != null) {
                uiAccountCursor.close();
            }
        }
    }

    private class AccountLoaderCallbacks
            implements LoaderManager.LoaderCallbacks<Map<String, Object>> {
        public static final String ARG_ACCOUNT_EMAIL = "accountEmail";
        public static final String ARG_ACCOUNT_ID = "accountId";
        private final Context mContext;

        private AccountLoaderCallbacks(Context context) {
            mContext = context;
        }

        @Override
        public void onLoadFinished(Loader<Map<String, Object>> loader, Map<String, Object> data) {
            final Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            if (data == null) {
                activity.finish();
                return;
            }
            //TS: zhangchao 2015-5-26 EMAIL BUGFIX_1008364 ADD_S
            //NOTE: after loaded,enable the screen.
            getPreferenceScreen().setEnabled(true);
            LogUtils.d(Logging.LOG_TAG, "AccountInfo loaded,we enable the perferenceScreen");
            //TS: zhangchao 2015-5-26 EMAIL BUGFIX_1008364 ADD_E
            mUiAccount = (com.tct.mail.providers.Account)
                    data.get(AccountLoader.RESULT_KEY_UIACCOUNT);
            mAccount = (Account) data.get(AccountLoader.RESULT_KEY_ACCOUNT);

            // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD_S
            if(mAccount != null && data.containsKey(AccountLoader.DEFAULT_ACCOUNT)) { // AM: Kexue.Geng 2015-03-10 EMAIL BUGFIX_927828
                mDefaultAccountId = (Long) data.get(AccountLoader.DEFAULT_ACCOUNT);
            }
            // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD_E
            if (mAccount != null && (mAccount.mFlags & Account.FLAGS_SECURITY_HOLD) != 0) {
                final Intent i = AccountSecurity.actionUpdateSecurityIntent(mContext,
                        mAccount.getId(), true);
                mContext.startActivity(i);
                activity.finish();
                return;
            }

            mInboxFolder = (Folder) data.get(AccountLoader.RESULT_KEY_INBOX);

            if (mUiAccount == null || mAccount == null) {
                activity.finish();
                return;
            }

            mServiceInfo =
                    EmailServiceUtils.getServiceInfo(mContext, mAccount.getProtocol(mContext));

            if (mInboxFolder == null) {
                mInboxFolderPreferences = null;
                //TS: rong-tang 2016-03-21 EMAIL BUGFIX-1804474 ADD_S
                //Note: if inbox folder is null, restart loader to load it.
                final Bundle args = new Bundle(1);
                if (!TextUtils.isEmpty(mAccountEmail)) {
                    args.putString(AccountLoaderCallbacks.ARG_ACCOUNT_EMAIL, mAccountEmail);
                } else {
                    args.putLong(AccountLoaderCallbacks.ARG_ACCOUNT_ID,
                            getArguments().getLong(ARG_ACCOUNT_ID, -1));
                }
                LogUtils.w(LogUtils.TAG, "AccountLoaderCallback.onLoadFinish()...   inbox folder is null, restart load it.");
                getLoaderManager().restartLoader(0, args, new AccountLoaderCallbacks(activity));
                return;
                //TS: rong-tang 2016-03-21 EMAIL BUGFIX-1804474 ADD_E
            } else {
                mInboxFolderPreferences = new FolderPreferences(mContext,
                        mUiAccount.getEmailAddress(), mInboxFolder, true);
            }
            loadSettings();
        }

        @Override
        public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
            return new AccountLoader(mContext, args.getString(ARG_ACCOUNT_EMAIL),
                    args.getLong(ARG_ACCOUNT_ID));
        }

        @Override
        public void onLoaderReset(Loader<Map<String, Object>> loader) {
            if(!getPreferenceScreen().isEnabled()){
                LogUtils.d(Logging.LOG_TAG, "The accountLoader reset unexcepted,reset screen to enabled");
                getPreferenceScreen().setEnabled(true);
            }
        }
    }

    /**
     * From a Policy, create and return an ArrayList of Strings that describe (simply) those
     * policies that are supported by the OS.  At the moment, the strings are simple (e.g.
     * "password required"); we should probably add more information (# characters, etc.), though
     */
    @SuppressWarnings("unused") // temporarily unused pending policy UI
    private ArrayList<String> getSystemPoliciesList(Policy policy) {
        Resources res = mContext.getResources();
        ArrayList<String> policies = new ArrayList<>();
        if (policy.mPasswordMode != Policy.PASSWORD_MODE_NONE) {
            policies.add(res.getString(R.string.policy_require_password));
        }
        if (policy.mPasswordHistory > 0) {
            policies.add(res.getString(R.string.policy_password_history));
        }
        if (policy.mPasswordExpirationDays > 0) {
            policies.add(res.getString(R.string.policy_password_expiration));
        }
        if (policy.mMaxScreenLockTime > 0) {
            policies.add(res.getString(R.string.policy_screen_timeout));
        }
        if (policy.mDontAllowCamera) {
            policies.add(res.getString(R.string.policy_dont_allow_camera));
        }
        if (policy.mMaxEmailLookback != 0) {
            policies.add(res.getString(R.string.policy_email_age));
        }
        if (policy.mMaxCalendarLookback != 0) {
            policies.add(res.getString(R.string.policy_calendar_age));
        }
        return policies;
    }

    @SuppressWarnings("unused") // temporarily unused pending policy UI
    private void setPolicyListSummary(ArrayList<String> policies, String policiesToAdd,
            String preferenceName) {
        Policy.addPolicyStringToList(policiesToAdd, policies);
        if (policies.size() > 0) {
            Preference p = findPreference(preferenceName);
            StringBuilder sb = new StringBuilder();
            for (String desc: policies) {
                sb.append(desc);
                sb.append('\n');
            }
            p.setSummary(sb.toString());
        }
    }

    /**
     * Load account data into preference UI. This must be called on the main thread.
     */
    private void loadSettings() {
        final AccountPreferences accountPreferences =
                new AccountPreferences(mContext, mUiAccount.getEmailAddress());
        if (mInboxFolderPreferences != null) {
            NotificationUtils.moveNotificationSetting(
                    accountPreferences, mInboxFolderPreferences);
        }

        final String protocol = mAccount.getProtocol(mContext);
        if (mServiceInfo == null) {
            LogUtils.e(LogUtils.TAG,
                    "Could not find service info for account %d with protocol %s", mAccount.mId,
                    protocol);
            getActivity().onBackPressed();
            // TODO: put up some sort of dialog/toast here to tell the user something went wrong
            return;
        }
        final android.accounts.Account androidAcct = mUiAccount.getAccountManagerAccount();

        mAccountDescription = (EditTextPreference) findPreference(PREFERENCE_DESCRIPTION);
        mAccountDescription.setSummary(mAccount.getDisplayName());
        mAccountDescription.setText(mAccount.getDisplayName());
        mAccountDescription.setOnPreferenceChangeListener(this);

        mAccountName = (EditTextPreference) findPreference(PREFERENCE_NAME);
        String senderName = mUiAccount.getSenderName();
        // In rare cases, sendername will be null;  Change this to empty string to avoid NPE's
        if (senderName == null) {
            senderName = "";
        }
        mAccountName.setSummary(senderName);
        mAccountName.setText(senderName);
        mAccountName.setOnPreferenceChangeListener(this);

        final String accountSignature = mAccount.getSignature();
        mAccountSignature = (EditTextPreference) findPreference(PREFERENCE_SIGNATURE);
        mAccountSignature.setText(accountSignature);
        mAccountSignature.setOnPreferenceChangeListener(this);
        SettingsUtils.updatePreferenceSummary(mAccountSignature, accountSignature,
                R.string.preferences_signature_summary_not_set);

        //[FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //boolean oofEnable = getResources().getBoolean(R.bool.feature_email_oof_on);
        boolean oofEnable = PLFUtils.getBoolean(mContext, "feature_email_oof_on");
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        mOofSettings = (Preference)findPreference(PREFERENCE_OOF_SETTINGS);
        //[BUGFIX]-ADD-BEGIN by TSNJ,shaodong.wang,11/03/2014,PR-822435.
        if (mOofSettings != null) {
            if (oofEnable&&"eas".equals(protocol)) {
                mOofSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        Intent intent = new Intent(mContext,OofSettings.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("account_id", mAccount.mId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return true;
                    }
                });
            } else {
                getPreferenceScreen().removePreference(mOofSettings);
            }
        }
        //[BUGFIX]-ADD-END by TSNJ,shaodong.wang.
        //[FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110

        mCheckFrequency = (ListPreference) findPreference(PREFERENCE_FREQUENCY);
        mCheckFrequency.setEntries(mServiceInfo.syncIntervalStrings);
        mCheckFrequency.setEntryValues(mServiceInfo.syncIntervals);
        if (mServiceInfo.syncContacts || mServiceInfo.syncCalendar) {
            // This account allows syncing of contacts and/or calendar, so we will always have
            // separate preferences to enable or disable syncing of email, contacts, and calendar.
            // The "sync frequency" preference really just needs to control the frequency value
            // in our database.
            mCheckFrequency.setValue(String.valueOf(mAccount.getSyncInterval()));
        } else {
            // This account only syncs email (not contacts or calendar), which means that we will
            // hide the preference to turn syncing on and off. In this case, we want the sync
            // frequency preference to also control whether or not syncing is enabled at all. If
            // sync is turned off, we will display "sync never" regardless of what the numeric
            // value we have stored says.
            boolean synced = ContentResolver.getSyncAutomatically(androidAcct,
                    EmailContent.AUTHORITY);
            if (synced) {
                mCheckFrequency.setValue(String.valueOf(mAccount.getSyncInterval()));
            } else {
                mCheckFrequency.setValue(String.valueOf(Account.CHECK_INTERVAL_NEVER));
            }
        }
        mCheckFrequency.setSummary(mCheckFrequency.getEntry());
        mCheckFrequency.setOnPreferenceChangeListener(this);

        final Preference quickResponsePref = findPreference(PREFERENCE_QUICK_RESPONSES);
        if (quickResponsePref != null) {
            quickResponsePref.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            onEditQuickResponses(mUiAccount);
                            return true;
                        }
                    });
        }

        // Add check window preference
        final PreferenceCategory dataUsageCategory =
                (PreferenceCategory) findPreference(PREFERENCE_CATEGORY_DATA_USAGE);

        if (mServiceInfo.offerLookback) {
            if (mSyncWindow == null) {
                mSyncWindow = new ListPreference(mContext);
                mSyncWindow.setKey(PREFERENCE_SYNC_WINDOW);
                dataUsageCategory.addPreference(mSyncWindow);
            }
            mSyncWindow.setTitle(R.string.account_setup_options_mail_window_label);
            //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_708637 ADD_S
            mSyncWindow.setDialogTitle(R.string.account_setup_options_mail_window_label);
            //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_708637 ADD_E
            mSyncWindow.setValue(String.valueOf(mAccount.getSyncLookback()));
            final int maxLookback;
            if (mAccount.mPolicy != null) {
                maxLookback = mAccount.mPolicy.mMaxEmailLookback;
            } else {
                maxLookback = 0;
            }

            MailboxSettings.setupLookbackPreferenceOptions(mContext, mSyncWindow, maxLookback,
                    false);

            // Must correspond to the hole in the XML file that's reserved.
            mSyncWindow.setOrder(2);
            mSyncWindow.setOnPreferenceChangeListener(this);

            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
            boolean isEnable = PLFUtils.getBoolean(mContext, "feature_email_syncCalendarScope_on");
            if (HostAuth.SCHEME_EAS.equals(protocol) && isEnable) {
                if (mSyncCalendarWindow == null) {
                    mSyncCalendarWindow = new ListPreference(mContext);
                    mSyncCalendarWindow.setKey(PREFERENCE_SYNC_CALENDAR_WINDOW);
                    dataUsageCategory.addPreference(mSyncCalendarWindow);
                }
                mSyncCalendarWindow.setTitle(R.string.account_setup_options_mail_calendar_window_label);
                //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_708637 ADD_S
                mSyncCalendarWindow.setDialogTitle(R.string.account_setup_options_mail_calendar_window_label);
                //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_708637 ADD_E
                mSyncCalendarWindow.setValue(String.valueOf(mAccount.getSyncCalendarLookback()));
                mSyncCalendarWindow.setSummary(mSyncCalendarWindow.getEntry());
                MailboxSettings.setupCalendarLookbackPreferenceOptions(mContext, mSyncCalendarWindow, mAccount);

                // Must correspond to the hole in the XML file that's reserved.
                mSyncCalendarWindow.setOrder(3);
                mSyncCalendarWindow.setOnPreferenceChangeListener(this);
            }
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E

            if (mSyncSettings == null) {
                mSyncSettings = new Preference(mContext);
                mSyncSettings.setKey(PREFERENCE_SYNC_SETTINGS);
                dataUsageCategory.addPreference(mSyncSettings);
            }

            mSyncSettings.setTitle(R.string.folder_sync_settings_pref_title);
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 MOD_S
            //NOTE: Modify order for folder sync settings
            mSyncSettings.setOrder(4);
            //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 MOD_E
        }

        final PreferenceCategory folderPrefs =
                (PreferenceCategory) findPreference(PREFERENCE_SYSTEM_FOLDERS);
        if (folderPrefs != null) {
            if (mServiceInfo.requiresSetup) {
                Preference trashPreference = findPreference(PREFERENCE_SYSTEM_FOLDERS_TRASH);
                Intent i = new Intent(mContext, FolderPickerActivity.class);
                Uri uri = EmailContent.CONTENT_URI.buildUpon().appendQueryParameter(
                        "account", Long.toString(mAccount.getId())).build();
                i.setData(uri);
                i.putExtra(FolderPickerActivity.MAILBOX_TYPE_EXTRA, Mailbox.TYPE_TRASH);
                trashPreference.setIntent(i);

                Preference sentPreference = findPreference(PREFERENCE_SYSTEM_FOLDERS_SENT);
                i = new Intent(mContext, FolderPickerActivity.class);
                i.setData(uri);
                i.putExtra(FolderPickerActivity.MAILBOX_TYPE_EXTRA, Mailbox.TYPE_SENT);
                sentPreference.setIntent(i);
            } else {
                getPreferenceScreen().removePreference(folderPrefs);
            }
        }

        final CheckBoxPreference backgroundAttachments = (CheckBoxPreference)
                findPreference(PREFERENCE_BACKGROUND_ATTACHMENTS);
        if (backgroundAttachments != null) {
            if (!mServiceInfo.offerAttachmentPreload) {
                dataUsageCategory.removePreference(backgroundAttachments);
            } else {
                backgroundAttachments.setChecked(
                        0 != (mAccount.getFlags() & Account.FLAGS_BACKGROUND_ATTACHMENTS));
                backgroundAttachments.setOnPreferenceChangeListener(this);
            }
        }

        final PreferenceCategory notificationsCategory =
                (PreferenceCategory) findPreference(PREFERENCE_CATEGORY_NOTIFICATIONS);

      //[FEATURE]-Add-BEGIN by TSNJ Zhenhua.Fan,09/09/2014,PR 765508
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //boolean defaultAccountEnable = getResources().getBoolean(R.bool.feature_email_defaultAccount_on);
        boolean defaultAccountEnable = PLFUtils.getBoolean(mContext, "feature_email_defaultAccount_on");
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        mAccountDefault = (CheckBoxPreference) findPreference(PREFERENCE_DEFAULT);
        if(mAccountDefault != null){
            if(defaultAccountEnable){
            mAccountDefault.setChecked(mAccount.mId == mDefaultAccountId);
            mAccountDefault.setOnPreferenceChangeListener(this);
            }else{
                  getPreferenceScreen().removePreference(mAccountDefault);
            }
        }
      //[FEATURE]-Add-END by TSNJ Zhenhua.Fan,09/09/2014,PR 765508

        if (mInboxFolderPreferences != null) {
            final CheckBoxPreference inboxNotify = (CheckBoxPreference) findPreference(
                FolderPreferences.PreferenceKeys.NOTIFICATIONS_ENABLED);
            inboxNotify.setChecked(mInboxFolderPreferences.areNotificationsEnabled());
            inboxNotify.setOnPreferenceChangeListener(this);

            mInboxRingtone = findPreference(FolderPreferences.PreferenceKeys.NOTIFICATION_RINGTONE);
            //TS: ke.ma 2015-4-22 EMAIL BUGFIX-979513 MOD_S
            String ringtoneUri = mInboxFolderPreferences.getNotificationRingtoneUri();
            if (!TextUtils.isEmpty(ringtoneUri)) {
                //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
                //Note: init temp ringtone uri from account settings.
                mTempRingtoneUri = Uri.parse(ringtoneUri);
                //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
                mRingtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(ringtoneUri));
                if (ringtoneUri != Settings.System.DEFAULT_NOTIFICATION_URI.toString()) {
                    if (!isFileExist(Uri.parse(ringtoneUri))) {
                        ringtoneUri = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
                        Log.i("RINGTONE_URI", "when ringtone file is null ,ringtoneUri:" + ringtoneUri);
                        setRingtone(Uri.parse(ringtoneUri));
                    }
                }
            }
            //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 ADD_S
            setRingtoneSummary(ringtoneUri);
            //TS: junwei-xu 2016-04-14 EMAIL BUGFIX-1920899 ADD_E
            //TS: ke.ma 2015-4-22 EMAIL BUGFIX-979513 MOD_E
            mInboxRingtone.setOnPreferenceChangeListener(this);
            mInboxRingtone.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    showRingtonePicker();

                    return true;
                }
            });

            notificationsCategory.setEnabled(true);

            // Set the vibrator value, or hide it on devices w/o a vibrator
            mInboxVibrate = (CheckBoxPreference) findPreference(
                    FolderPreferences.PreferenceKeys.NOTIFICATION_VIBRATE);
            if (mInboxVibrate != null) {
                mInboxVibrate.setChecked(
                        mInboxFolderPreferences.isNotificationVibrateEnabled());
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    // When the value is changed, update the setting.
                    mInboxVibrate.setOnPreferenceChangeListener(this);
                } else {
                    // No vibrator present. Remove the preference altogether.
                    notificationsCategory.removePreference(mInboxVibrate);
                    mInboxVibrate = null;
                }
            }
        } else {
            notificationsCategory.setEnabled(false);
        }

        final Preference retryAccount = findPreference(PREFERENCE_POLICIES_RETRY_ACCOUNT);
        final PreferenceCategory policiesCategory = (PreferenceCategory) findPreference(
                PREFERENCE_CATEGORY_POLICIES);
        if (policiesCategory != null) {
            // TODO: This code for showing policies isn't working. For KLP, just don't even bother
            // showing this data; we'll fix this later.
    /*
            if (policy != null) {
                if (policy.mProtocolPoliciesEnforced != null) {
                    ArrayList<String> policies = getSystemPoliciesList(policy);
                    setPolicyListSummary(policies, policy.mProtocolPoliciesEnforced,
                            PREFERENCE_POLICIES_ENFORCED);
                }
                if (policy.mProtocolPoliciesUnsupported != null) {
                    ArrayList<String> policies = new ArrayList<String>();
                    setPolicyListSummary(policies, policy.mProtocolPoliciesUnsupported,
                            PREFERENCE_POLICIES_UNSUPPORTED);
                } else {
                    // Don't show "retry" unless we have unsupported policies
                    policiesCategory.removePreference(retryAccount);
                }
            } else {
    */
            // Remove the category completely if there are no policies
            getPreferenceScreen().removePreference(policiesCategory);

            //}
        }

        if (retryAccount != null) {
            retryAccount.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            // Release the account
                            SecurityPolicy.setAccountHoldFlag(mContext, mAccount, false);
                            // Remove the preference
                            if (policiesCategory != null) {
                                policiesCategory.removePreference(retryAccount);
                            }
                            return true;
                        }
                    });
        }
        findPreference(PREFERENCE_INCOMING).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        onIncomingSettings(mAccount);
                        return true;
                    }
                });

        // Hide the outgoing account setup link if it's not activated
        final Preference prefOutgoing = findPreference(PREFERENCE_OUTGOING);
        if (prefOutgoing != null) {
            if (mServiceInfo.usesSmtp && mAccount.mHostAuthSend != null) {
                prefOutgoing.setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                onOutgoingSettings(mAccount);
                                return true;
                            }
                        });
            } else {
                if (mServiceInfo.usesSmtp) {
                    // We really ought to have an outgoing host auth but we don't.
                    // There's nothing we can do at this point, so just log the error.
                    LogUtils.e(LogUtils.TAG, "Account %d has a bad outbound hostauth",
                            mAccount.getId());
                }
                PreferenceCategory serverCategory = (PreferenceCategory) findPreference(
                        PREFERENCE_CATEGORY_SERVER);
                serverCategory.removePreference(prefOutgoing);
            }
        }
        //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_S
//        final CheckBoxPreference syncContacts = (CheckBoxPreference) findPreference(PREFERENCE_SYNC_CONTACTS);
//        final CheckBoxPreference syncCalendar = (CheckBoxPreference) findPreference(PREFERENCE_SYNC_CALENDAR);
//        final CheckBoxPreference syncEmail = (CheckBoxPreference) findPreference(PREFERENCE_SYNC_EMAIL);
        syncContacts = (CheckBoxPreference) findPreference(PREFERENCE_SYNC_CONTACTS);
        syncCalendar = (CheckBoxPreference) findPreference(PREFERENCE_SYNC_CALENDAR);
        syncEmail = (CheckBoxPreference) findPreference(PREFERENCE_SYNC_EMAIL);
        //TS: wenggangjin 2014-12-01 EMAIL BUGFIX_854751 MOD_S
        if (syncContacts != null && syncCalendar != null && syncEmail != null) {
            if (mServiceInfo.syncContacts || mServiceInfo.syncCalendar) {
                if (mServiceInfo.syncContacts) {
                    syncContacts.setChecked(ContentResolver
                            .getSyncAutomatically(androidAcct, ContactsContract.AUTHORITY));
                    syncContacts.setOnPreferenceChangeListener(this);
                } else {
                    syncContacts.setChecked(false);
                    syncContacts.setEnabled(false);
                }
                if (mServiceInfo.syncCalendar) {
                    syncCalendar.setChecked(ContentResolver
                            .getSyncAutomatically(androidAcct, CalendarContract.AUTHORITY));
                    syncCalendar.setOnPreferenceChangeListener(this);
                } else {
                    syncCalendar.setChecked(false);
                    syncCalendar.setEnabled(false);
                }
                syncEmail.setChecked(ContentResolver
                        .getSyncAutomatically(androidAcct, EmailContent.AUTHORITY));
                syncEmail.setOnPreferenceChangeListener(this);
            } else {
                dataUsageCategory.removePreference(syncContacts);
                dataUsageCategory.removePreference(syncCalendar);
                dataUsageCategory.removePreference(syncEmail);
            }
        }

        //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_S
        Preference manualSyncRoaming = findPreference(PREFERENCE_MANUAL_SYNC_WHEN_ROAMING);
        if (manualSyncRoaming != null) {
            boolean showManualSyncRoaming = false;
            if (mAccount != null && mAccount.mPolicy != null) {
                Policy policy = mAccount.mPolicy;
                if (HostAuth.SCHEME_EAS.equals(protocol) && policy != null && policy.mRequireManualSyncWhenRoaming) {
                    boolean isRoaming = Utility.isRoaming(mContext);
                    showManualSyncRoaming = true;
                    mCheckFrequency.setEnabled(!isRoaming);
                    if (syncEmail != null) {
                        syncEmail.setEnabled(!isRoaming);
                    }
                    if (syncCalendar != null) {
                        syncCalendar.setEnabled(!isRoaming);
                    }
                    if (syncContacts != null) {
                        syncContacts.setEnabled(!isRoaming);
                    }
                } else {
                    mCheckFrequency.setEnabled(true);
                    if (syncEmail != null) {
                        syncEmail.setEnabled(true);
                    }
                    if (syncCalendar != null) {
                        syncCalendar.setEnabled(true);
                    }
                    if (syncContacts != null) {
                        syncContacts.setEnabled(true);
                    }
                }
            }
            if (!showManualSyncRoaming) {
                PreferenceCategory serverPolicyCal = (PreferenceCategory) findPreference(PREFERENCE_CATEGORY_SERVER_POLICY);
                if (serverPolicyCal != null) {
                    serverPolicyCal.removePreference(manualSyncRoaming);
                    if (serverPolicyCal.getPreferenceCount() == 0 && getPreferenceScreen() != null) {
                        getPreferenceScreen().removePreference(serverPolicyCal);
                    }
                }
            } else {
                manualSyncRoaming.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                        Utility.showShortToast(getActivity(), R.string.server_rule_can_not_change);
                        //Toast.makeText(getActivity(), R.string.server_rule_can_not_change, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        }
        //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_E

      //[FEATURE]-Add-BEGIN by TSNJ Zhenhua.Fan,09/09/2014,PR 765508
        // Temporary home for delete account
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //boolean removeAccountEnable = getResources().getBoolean(R.bool.feature_email_removeAccount_on);
        boolean removeAccountEnable = PLFUtils.getBoolean(mContext, "feature_email_removeAccount_on");
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        Preference prefDeleteAccount = findPreference(PREFERENCE_DELETE_ACCOUNT);
        if(prefDeleteAccount != null){
            if(removeAccountEnable){
                        prefDeleteAccount.setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                DeleteAccountFragment dialogFragment = DeleteAccountFragment.newInstance(
                                        mAccount, AccountSettingsFragment.this);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.addToBackStack(null);
                                dialogFragment.show(ft, DeleteAccountFragment.TAG);
                                return true;
                            }
                        });
            }else{
                final PreferenceCategory removeAccountCategory = (PreferenceCategory) findPreference(
                        PREFERENCE_CATEGORY_DELETE);
                getPreferenceScreen().removePreference(removeAccountCategory);
            }
        }
      //[FEATURE]-Add-END by TSNJ Zhenhua.Fan,09/09/2014,PR 765508

        //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
        mDownloadOptions = (ListPreference) findPreference(PREFERENCE_DOWNLOAD_OPTIONS);
        //[FEATURE]-Mod-BEGIN by TSCD.Chao Zhang,04/16/2014,FR 631895(porting fromFR473181)
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //boolean downloadOpitons = getResources().getBoolean(R.bool.feature_email_downloadOptions_on);
        boolean downloadOpitons = PLFUtils.getBoolean(mContext, "feature_email_downloadOptions_on");
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        if(mDownloadOptions!=null){
            if(downloadOpitons) {
             //[FEATURE]-Mod-END by TSCD.Chao Zhang
                if (mAccount.mHostAuthRecv.mProtocol.equals(HostAuth.SCHEME_POP3)){
                    mDownloadOptions.setEntries(R.array.Email_POP3_Download_Options);
                    mDownloadOptions.setEntryValues(R.array.Email_POP3_Download_Options_values);
                } else {
                    mDownloadOptions.setEntries(R.array.Email_Download_Options);
                    mDownloadOptions.setEntryValues(R.array.Email_Download_Options_values);
                }

                if(mAccount.getDownloadOptions() == Utility.ENTIRE_MAIL)
                {
                    if(mAccount.mHostAuthRecv.mProtocol.equals(HostAuth.SCHEME_POP3)){
                        mDownloadOptions.setValueIndex(Utility.POP3_DOWNLOAD_ALL);
                    } else {
                        mDownloadOptions.setValueIndex(Utility.DOWNLOAD_ALL);
                    }
                    mDownloadOptions.setSummary(R.string.account_setting_download_opt_all);
                } else{
                    int index = mDownloadOptions.findIndexOfValue(String.valueOf(mAccount.getDownloadOptions()));
                    if(mAccount.mHostAuthRecv.mProtocol.equals(HostAuth.SCHEME_POP3)){
                        if((index > Utility.POP3_DOWNLOAD_ALL) || (index < 0)) {
                            index = Utility.HEAD_ONLY;
                        }
                    } else {
                        if((index > Utility.DOWNLOAD_ALL) || (index < 0)) {
                            index = Utility.HEAD_ONLY;
                        }
                    }

                    mDownloadOptions.setValueIndex(index);
                    mDownloadOptions.setSummary(getDownloadSummary(
                            Integer.parseInt(mDownloadOptions.getValue())));
                }

                mDownloadOptions.setOnPreferenceChangeListener(this);
            } else {
                dataUsageCategory.removePreference(mDownloadOptions);
            }
        }
        //[FEATURE]-Add-END by TSCD.Chao Zhang
        // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
        mInlineImages = (ListPreference) findPreference(PREFERENCE_INLINE_IMAGES);
        if (mInlineImages != null) {
            if (mAccount.mInlineImages == Utility.ALWAYS_SHOW) {
                mInlineImages.setValueIndex(0);
            } else if (mAccount.mInlineImages == Utility.ASK_BEFORE_SHOWING) {
                mInlineImages.setValueIndex(1);
            }
            mInlineImages.setSummary(getInlineImageSummary(Integer.parseInt(mInlineImages.getValue())));
            mInlineImages.setOnPreferenceChangeListener(this);
        }
        // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID

        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S
        boolean replyToEnable = PLFUtils.getBoolean(mContext, "feature_email_reply_to");
        if(replyToEnable) {
            mReplyToPreference = (EditTextPreference) findPreference(PREFERENCE_REPLY_TO);
            String replyTo = mAccount.getAccountReplyTo();
            if(replyTo == null) {
                replyTo = "";
            }
            mReplyToPreference.setSummary(replyTo);
            mReplyToPreference.setText(replyTo);
            mReplyToPreference.setOnPreferenceChangeListener(this);
        } else {
            final PreferenceCategory replyToPreCategory = (PreferenceCategory) findPreference(PREFERENCE_CATEGORY_REPLY_TO);
            if(replyToPreCategory != null) {
                getPreferenceScreen().removePreference(replyToPreCategory);
            }
        }
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E
    }

    /**
     * Shows the system ringtone picker.
     */
    private void showRingtonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        final String ringtoneUri = mInboxFolderPreferences.getNotificationRingtoneUri();
        if (!TextUtils.isEmpty(ringtoneUri)) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtoneUri));
        }
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                Settings.System.DEFAULT_NOTIFICATION_URI);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(EXTRA_RINGTONE_NOTSHOW_NONE, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        startActivityForResult(intent, RINGTONE_REQUEST_CODE);
    }

    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    public void showNeedPermissionToast(int descId) {
        final ActionableToastBar.ActionClickedListener listener = new ActionableToastBar.ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoNum) {
                PermissionUtil.gotoSettings(context);
            }
        };
        mToastBar.show(listener, getString(descId), R.string.permission_grant_go_setting, true,
                new ToastBarOperation(1, 0, ToastBarOperation.INFO, false, null));

    }
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E

    /**
     * Dispatch to edit quick responses.
     */
    public void onEditQuickResponses(com.tct.mail.providers.Account account) {
        final Bundle args = AccountSettingsEditQuickResponsesFragment.createArgs(account);
        final PreferenceActivity activity = (PreferenceActivity) getActivity();
        activity.startPreferencePanel(AccountSettingsEditQuickResponsesFragment.class.getName(),
                args, R.string.account_settings_edit_quick_responses_label, null, null, 0);
    }
  //[FEATURE]-Add-BEGIN by TSNJ Zhenhua.Fan,09/09/2014,PR 765508
    /**
     * Dialog fragment to show "remove account?" dialog
     */
    public static class DeleteAccountFragment extends DialogFragment {
        private final static String TAG = "DeleteAccountFragment";

        // Argument bundle keys
        private final static String BUNDLE_KEY_ACCOUNT_NAME = "DeleteAccountFragment.Name";

        /**
         * Create the dialog with parameters
         */
        public static DeleteAccountFragment newInstance(Account account, Fragment parentFragment) {
            DeleteAccountFragment f = new DeleteAccountFragment();
            Bundle b = new Bundle();
            b.putString(BUNDLE_KEY_ACCOUNT_NAME, account == null ? "" : account.getDisplayName());  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
            f.setArguments(b);
            f.setTargetFragment(parentFragment, 0);
            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = getActivity();
            final String name = getArguments().getString(BUNDLE_KEY_ACCOUNT_NAME);

            //TS: jian.xu 2015-11-25 EMAIL BUGFIX-981169 MOD_S
            final AlertDialog removeDialog = new AlertDialog.Builder(context)
                //.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.account_delete_dlg_title)
                .setMessage(R.string.account_delete_dlg_instructions_fmt_new)
                .setPositiveButton(
                        getString(R.string.account_delete_dlg_action_remove_label).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Fragment f = getTargetFragment();
                                if (f instanceof AccountSettingsFragment) {
                                    ((AccountSettingsFragment) f).finishDeleteAccount();
                                }
                                dismiss();
                            }
                        })
                .setNegativeButton(
                        getString(R.string.account_delete_dlg_action_cancel_label).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        })
                .create();
            //Note： custom the style for positive button
            removeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    removeDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                            context.getResources().getColor(R.color.account_delete_dlg_action_remove_label_color));
                    removeDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
            });
            return removeDialog;
            //TS: jian.xu 2015-11-25 EMAIL BUGFIX-981169 MOD_E
        }
    }
    /**
     * Callback from delete account dialog - passes the delete command up to the activity
     */
    private void finishDeleteAccount() {
       // mSaveOnExit = false;
       deleteAccount(mAccount);
    }
    //[FEATURE]-Add-BEGIN by TSNJ Zhenhua.Fan,10/23/2014,PR 728378
    /**
     * Delete the selected account
     */
    public void deleteAccount(final Account account) {
        // Kick off the work to actually delete the account
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Uri uri = EmailProvider.uiUri("uiaccount", account.mId);
                //TS: zheng.zou 2015-3-11 EMAIL BUGFIX_944202 Mod_S
                Activity activity = getActivity();
                if (activity!=null){
                    activity.getContentResolver().delete(uri, null, null);

                    android.accounts.Account[] accounts = AccountManager.get(activity).getAccountsByType("com.tct.email");
                    for(android.accounts.Account sysAccount  :accounts){
                        if(sysAccount.name.equalsIgnoreCase(account.getEmailAddress())){
                            AccountManager.get(activity).removeAccount(sysAccount, null, null);
                        }
                    }

                    // Remove the eas account
                    android.accounts.Account[] easAccounts = AccountManager.get(activity).getAccountsByType("com.tct.exchange");
                    for(android.accounts.Account easAccount:easAccounts){
                        if(easAccount.name.equalsIgnoreCase(account.getEmailAddress())){
                            AccountManager.get(activity).removeAccount(easAccount, null, null);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }

                //TS: zheng.zou 2015-02-13 EMAIL BUGFIX-927973 ADD_S
                Activity activity2 = getActivity();
                if (activity2 != null ) {
                    activity2.finish();//need implement below for MultiPane in later.onIsMultiPane()
                }
                //TS: zheng.zou 2015-02-13 EMAIL BUGFIX-927973 ADD_E
                //TS: zheng.zou 2015-3-11 EMAIL BUGFIX_944202 Mod_E
            }}).start();
    }
    //[FEATURE]-Add-END by TSNJ Zhenhua.Fan
    //[FEATURE]-Add-END by TSNJ Zhenhua.Fan,09/09/2014,PR 765508
    /**
     * Dispatch to edit incoming settings.
     */
    public void onIncomingSettings(Account account) {
        final Intent intent =
                AccountServerSettingsActivity.getIntentForIncoming(getActivity(), account);
        getActivity().startActivity(intent);
    }

    /**
     * Dispatch to edit outgoing settings.
     */
    public void onOutgoingSettings(Account account) {
        final Intent intent =
                AccountServerSettingsActivity.getIntentForOutgoing(getActivity(), account);
        getActivity().startActivity(intent);
    }

    //TS: ke.ma 2015-4-22 EMAIL BUGFIX-979513 ADD_S
    private boolean isFileExist(Uri uri) {
        boolean isFileExist = false;
        final ContentResolver contentResolver = mContext.getContentResolver();
        ParcelFileDescriptor file = null;
        try {
            file = contentResolver.openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //TS: zheng.zou 2015-12-08 EMAIL BUGFIX_1058396 ADD_S
        } catch (SecurityException e) {
            LogUtils.w(LogUtils.TAG, "SecurityException account setting "+e);
            isFileExist = true;
            //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
            PermissionUtil.checkAndRequestPermissionForResult(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE, PermissionUtil.REQ_CODE_PERMISSION_ACCESS_RINGTONE);
            //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
            //TS: zheng.zou 2015-12-08 EMAIL BUGFIX_1058396 ADD_E
        } finally {
            try {
                if (null != file) {
                    file.close();
                    isFileExist = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isFileExist;
    }
    //TS: ke.ma 2015-4-22 EMAIL BUGFIX-979513 ADD_E

    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_ACCESS_RINGTONE) {
            if (!PermissionUtil.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                setRingtone(Settings.System.DEFAULT_NOTIFICATION_URI);
                showNeedPermissionToast(R.string.permission_needed_to_check_ringtone);
            } else {
                if (mInboxFolderPreferences != null && mTempRingtoneUri != null) {
                    setRingtone(mTempRingtoneUri);
                }
            }
        }
    }
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
}
