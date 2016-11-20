/*
 * Copyright (C) 2012 The Android Open Source Project
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
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-1003318  2015/05/20   zhaotianyong   [Android5.0][Email]Email can reply from notification when set download option as header only.
 ============================================================================ 
 */
package com.tct.mail.preferences;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;

import com.google.android.mail.common.base.Strings;
import com.tct.emailcommon.provider.EmailContent;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.ImmutableSet;
import com.tct.fw.google.common.collect.ImmutableSet;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.FolderCapabilities;
import com.tct.mail.utils.NotificationActionUtils.NotificationActionType;
import com.tct.mail.utils.PLFUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Preferences relevant to one specific folder. In Email, this would only be used for an account's
 * inbox. In Gmail, this is used for every account/label pair.
 */
public class FolderPreferences extends VersionedPrefs {

    private static final String PREFS_NAME_PREFIX = "Folder";

    public static final class PreferenceKeys {
        /** Boolean value indicating whether notifications are enabled */
        public static final String NOTIFICATIONS_ENABLED = "notifications-enabled";
        /** String value of the notification ringtone URI */
        public static final String NOTIFICATION_RINGTONE = "notification-ringtone";
        /** Boolean value indicating whether we should explicitly vibrate */
        public static final String NOTIFICATION_VIBRATE = "notification-vibrate";
        /**
         * Boolean value indicating whether we notify for every message (<code>true</code>), or just
         * once for the folder (<code>false</code>)
         */
        public static final String NOTIFICATION_NOTIFY_EVERY_MESSAGE =
                "notification-notify-every-message";

        public static final ImmutableSet<String> BACKUP_KEYS =
                new ImmutableSet.Builder<String>()
                        .add(NOTIFICATIONS_ENABLED)
                        .add(NOTIFICATION_RINGTONE)
                        .add(NOTIFICATION_VIBRATE)
                        .add(NOTIFICATION_NOTIFY_EVERY_MESSAGE)
                        .build();
    }

    private final Folder mFolder;
    /** An id that is constant across app installations. */
    private final String mPersistentId;
    private final boolean mUseInboxDefaultNotificationSettings;

    /**
     * @param accountEmail The account email. This must never change for the account.
     * @param folder The folder
     */
    public FolderPreferences(final Context context, final String accountEmail, final Folder folder,
            final boolean useInboxDefaultNotificationSettings) {
        this(context, accountEmail, folder, folder.persistentId,
                useInboxDefaultNotificationSettings);
    }

    /**
     * A constructor that can be used when no {@link Folder} object is available (like during a
     * restore). This will function as expected except when calling
     * {@link #getDefaultNotificationActions(Context)}, so
     * {@link #FolderPreferences(Context, String, Folder, boolean)} should be used if at all
     * possible.
     *
     * @param accountEmail The account email. This must never change for the account.
     * @param persistentId An identifier for the folder that does not change across app
     *        installations.
     */
    public FolderPreferences(final Context context, final String accountEmail, final String persistentId,
            final boolean useInboxDefaultNotificationSettings) {
        this(context, accountEmail, null, persistentId, useInboxDefaultNotificationSettings);
    }

    private FolderPreferences(final Context context, final String accountEmail, final Folder folder,
            final String persistentId, final boolean useInboxDefaultNotificationSettings) {
        super(context, buildSharedPrefsName(accountEmail, persistentId));
        mFolder = folder;
        mPersistentId = persistentId;
        mUseInboxDefaultNotificationSettings = useInboxDefaultNotificationSettings;
    }

    private static String buildSharedPrefsName(final String account, final String persistentId) {
        return PREFS_NAME_PREFIX + '-' + account + '-' + persistentId;
    }

    @Override
    protected void performUpgrade(final int oldVersion, final int newVersion) {
        if (oldVersion > newVersion) {
            throw new IllegalStateException(
                    "You appear to have downgraded your app. Please clear app data.");
        }
    }

    @Override
    protected boolean canBackup(final String key) {
        if (mPersistentId == null) {
            return false;
        }

        return PreferenceKeys.BACKUP_KEYS.contains(key);
    }

    @Override
    protected Object getBackupValue(final String key, final Object value) {
        if (PreferenceKeys.NOTIFICATION_RINGTONE.equals(key)) {
            return getRingtoneTitle((String) value);
        }

        return super.getBackupValue(key, value);
    }

    @Override
    protected Object getRestoreValue(final String key, final Object value) {
        if (PreferenceKeys.NOTIFICATION_RINGTONE.equals(key)) {
            return getRingtoneUri((String) value);
        }

        return super.getBackupValue(key, value);
    }

    private String getRingtoneTitle(final String ringtoneUriString) {
        if (ringtoneUriString.length() == 0) {
            return ringtoneUriString;
        }
        final Uri uri = Uri.parse(ringtoneUriString);
        if (RingtoneManager.isDefault(uri)) {
            return ringtoneUriString;
        }
        final RingtoneManager ringtoneManager = new RingtoneManager(getContext());
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);
        final Cursor cursor = ringtoneManager.getCursor();
        try {
            while (cursor.moveToNext()) {
                final Uri cursorUri = ContentUris.withAppendedId(
                        Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX)),
                        cursor.getLong(RingtoneManager.ID_COLUMN_INDEX));
                if (cursorUri.toString().equals(ringtoneUriString)) {
                    final String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                    if (!Strings.isNullOrEmpty(title)) {
                        return title;
                    }
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private String getRingtoneUri(final String name) {
        if (name.length() == 0 || RingtoneManager.isDefault(Uri.parse(name))) {
            return name;
        }

        final RingtoneManager ringtoneManager = new RingtoneManager(getContext());
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);
        final Cursor cursor = ringtoneManager.getCursor();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                if (name.equals(title)) {
                    Uri uri = ContentUris.withAppendedId(
                            Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX)),
                            cursor.getLong(RingtoneManager.ID_COLUMN_INDEX));
                    return uri.toString();
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * If <code>true</code>, we use inbox-defaults for notification settings. If <code>false</code>,
     * we use standard defaults.
     */
    private boolean getUseInboxDefaultNotificationSettings() {
        return mUseInboxDefaultNotificationSettings;
    }

    public boolean isNotificationsEnabledSet() {
        return getSharedPreferences().contains(PreferenceKeys.NOTIFICATIONS_ENABLED);
    }

    public boolean areNotificationsEnabled() {
        return getSharedPreferences().getBoolean(
                PreferenceKeys.NOTIFICATIONS_ENABLED, getUseInboxDefaultNotificationSettings());
    }

    public void setNotificationsEnabled(final boolean enabled) {
        getEditor().putBoolean(PreferenceKeys.NOTIFICATIONS_ENABLED, enabled).apply();
        notifyBackupPreferenceChanged();
    }

    public String getNotificationRingtoneUri() {
        //[BUGFIX]-Mod-BEGIN by SCDTABLET.yingjie.chen@tcl.com,05/11/2016, 2105389 ,
        //Email]Adjust Email default ringtone for CKT requriement
        return getSharedPreferences().getString(PreferenceKeys.NOTIFICATION_RINGTONE,
                getDefaultRingToneUri());
        //[BUGFIX]-Mod-END by SCDTABLET.yingjie.chen@tcl.com
    }

    public void setNotificationRingtoneUri(final String uri) {
        getEditor().putString(PreferenceKeys.NOTIFICATION_RINGTONE, uri).apply();
        notifyBackupPreferenceChanged();
    }

    public boolean isNotificationVibrateEnabled() {
        return getSharedPreferences().getBoolean(PreferenceKeys.NOTIFICATION_VIBRATE, false);
    }

    public void setNotificationVibrateEnabled(final boolean enabled) {
        getEditor().putBoolean(PreferenceKeys.NOTIFICATION_VIBRATE, enabled).apply();
        notifyBackupPreferenceChanged();
    }

    public boolean isEveryMessageNotificationEnabled() {
        return getSharedPreferences()
                .getBoolean(PreferenceKeys.NOTIFICATION_NOTIFY_EVERY_MESSAGE, false);
    }

    public void setEveryMessageNotificationEnabled(final boolean enabled) {
        getEditor().putBoolean(PreferenceKeys.NOTIFICATION_NOTIFY_EVERY_MESSAGE, enabled).apply();
        notifyBackupPreferenceChanged();
    }

    // TS: zhaotianyong 2015-05-1003318 EMAIL BUGFIX_1003318 MOD_S
    public Set<String> getNotificationActions(final Account account, Message message) {
        final boolean supportsArchiveRemoveLabel =
                mFolder.supportsCapability(FolderCapabilities.ARCHIVE)
                || mFolder.supportsCapability(FolderCapabilities.ALLOWS_REMOVE_CONVERSATION);
        final boolean preferDelete = MailPrefs.RemovalActions.DELETE.equals(
                MailPrefs.get(getContext()).getRemovalAction(
                        account.supportsCapability(AccountCapabilities.ARCHIVE)));
        final NotificationActionType destructiveActionType =
                supportsArchiveRemoveLabel && !preferDelete ?
                        NotificationActionType.ARCHIVE_REMOVE_LABEL : NotificationActionType.DELETE;
        final String destructiveAction = destructiveActionType.getPersistedValue();

        // TS: yanhua.chen 2015-09-30 EMAIL BUGFIX_653104 MOD_S
        // MailPrefs.get(getContext()).getDefaultReplyAll()
        // ? NotificationActionType.REPLY_ALL.getPersistedValue()
        // : NotificationActionType.REPLY.getPersistedValue();

        // 1.If Reply All is checked in General settings:
        // when the summation for sendToAddresses, ccAddresses and bccAddresses's size over 1.
        // we think it supprot reply all,else show reply.
        // 2.If Reply All is not checked in General settings:
        // just show reply
        String replyAction = NotificationActionType.REPLY.getPersistedValue();
        boolean defaultReplyAll = MailPrefs.get(getContext()).getDefaultReplyAll();
        if (defaultReplyAll) {
            replyAction = isSupportReplyAll(message) ? NotificationActionType.REPLY_ALL
                    .getPersistedValue() : NotificationActionType.REPLY.getPersistedValue();
        }
        // TS: yanhua.chen 2015-09-30 EMAIL BUGFIX_653104 MOD_E

        EmailContent.Message msg = EmailContent.Message.restoreMessageWithId(getContext(), message.id);
        boolean mailDownloadPartial = (msg != null && msg.mFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL);
        Set<String> notificationActions;
        if (mailDownloadPartial) {
            notificationActions = new LinkedHashSet<String>(1);
            notificationActions.add(destructiveAction);
        } else {
            notificationActions = new LinkedHashSet<String>(2);
            notificationActions.add(destructiveAction);
            notificationActions.add(replyAction);
        }
        // TS: zhaotianyong 2015-05-1003318 EMAIL BUGFIX_1003318 MOD_E

        return notificationActions;
    }

    // TS: yanhua.chen 2015-09-30 EMAIL BUGFIX_653104 ADD_S
    /**
     * check whether the message support Reply all
     */
    private boolean isSupportReplyAll(Message message) {
        boolean supprot = false;

        if (message != null) {
            String[] sendToAddresses = message.getToAddressesUnescaped();
            String[] ccAddresses = message.getCcAddressesUnescaped();
            String[] bccAddresses = message.getBccAddressesUnescaped();
            int sizeSendTo = sendToAddresses != null ? sendToAddresses.length : 0;
            int sizeCc = ccAddresses != null ? ccAddresses.length : 0;
            int sizeBcc = bccAddresses != null ? bccAddresses.length : 0;

            // when the summation for sendToAddresses, ccAddresses and
            // bccAddresses's size over 1.
            // we think it supprot reply all.
            if (sizeSendTo + sizeCc + sizeBcc > 1) {
                supprot = true;
            }
        }

        return supprot;
    }
    // TS: yanhua.chen 2015-09-30 EMAIL BUGFIX_653104 ADD_E

    //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,05/11/2016, 2105389 ,
    //Email]Adjust Email default ringtone for CKT requriement
    private String getDefaultRingToneUri(){
        String defaultRingtoneUri = "";
        boolean is464gCKT =PLFUtils.getBoolean(getContext(),"feature_platform_464gCKT_on");
        if (is464gCKT) {
            defaultRingtoneUri = Uri.parse("content://media/internal/audio/media/20").toString();
        } else {
            defaultRingtoneUri = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
        }
        return defaultRingtoneUri;
    }
    //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.com
}
