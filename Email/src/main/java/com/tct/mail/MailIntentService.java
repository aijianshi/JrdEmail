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
/**
*===================================================================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== =======================================================================
*BUGFIX-904760  2015-01-21   wenggangjin     [Email][UI] Change the color of Email status bar
*CR_585337      2015-09-21  chao.zhang       Exchange Email resend mechanism
*BUGFIX_1162996 2015/1/20   yanhua.chen      [Android 6.0][Email]TCL account pop up permission needed window continuously if disable contact/calendar permission of exchange
====================================================================================================================
*/
package com.tct.mail;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tct.email.NotificationController;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.photo.ContactPhotoFetcher;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Folder;
import com.tct.mail.utils.FolderUri;
import com.tct.mail.utils.NotificationUtils;
import com.tct.mail.utils.StorageLowState;
import com.tct.mail.utils.Utils;
import com.tct.permission.PermissionUtil;

/**
 * A service to handle various intents asynchronously.
 */
public class MailIntentService extends IntentService {
    private static final String LOG_TAG = LogTag.getLogTag();

    public static final String ACTION_RESEND_NOTIFICATIONS =
            "com.tct.mail.action.RESEND_NOTIFICATIONS";
    public static final String ACTION_CLEAR_NEW_MAIL_NOTIFICATIONS =
            "com.tct.mail.action.CLEAR_NEW_MAIL_NOTIFICATIONS";

    /**
     * After user replies an email from Wear, it marks the conversation as read and resend
     * notifications.
     */
    public static final String ACTION_RESEND_NOTIFICATIONS_WEAR =
            "com.tct.mail.action.RESEND_NOTIFICATIONS_WEAR";

    public static final String ACTION_BACKUP_DATA_CHANGED =
            "com.tct.mail.action.BACKUP_DATA_CHANGED";
    public static final String ACTION_SEND_SET_NEW_EMAIL_INDICATOR =
            "com.tct.mail.action.SEND_SET_NEW_EMAIL_INDICATOR";

    public static final String CONVERSATION_EXTRA = "conversation";
    // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
    public static final String ACTION_SEND__MAIL_FAILED_NOTIFICATIONS =
            "com.tct.mail.action.SEND_MAIL_FAILED_NOTIFICATIONS";
    // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
    public static final String ACTION_SEND_CALENDAR_NOTIFICATIONS = "com.tct.mail.action.SEND_CALENDAR_NOTIFICATIONS";
    public static final String ACTION_SEND_CONTACTS_NOTIFICATIONS = "com.tct.mail.action.SEND_CONTACTS_NOTIFICATIONS";
    public static final String ACTION_SEND_STORAGE_NOTIFICATIONS = "com.tct.mail.action.SEND_STORAGE_NOTIFICATIONS";   //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E

    public MailIntentService() {
        super("MailIntentService");
    }

    protected MailIntentService(final String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        // UnifiedEmail does not handle all Intents

        LogUtils.v(LOG_TAG, "Handling intent %s", intent);

        final String action = intent.getAction();

        if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            NotificationUtils.cancelAndResendNotificationsOnLocaleChange(
                    this, getContactPhotoFetcher());
        } else if (ACTION_CLEAR_NEW_MAIL_NOTIFICATIONS.equals(action)) {
            final Account account = intent.getParcelableExtra(Utils.EXTRA_ACCOUNT);
            final Folder folder = intent.getParcelableExtra(Utils.EXTRA_FOLDER);

            NotificationUtils.clearFolderNotification(this, account, folder, true /* markSeen */);
            Analytics.getInstance().sendEvent("notification_dismiss", folder.getTypeDescription(),
                    null, 0);
        } else if (ACTION_RESEND_NOTIFICATIONS.equals(action)) {
            //Fix NPE when folderUri is null.
            //NotificationUtils expects null, not FolderUri that is holder for null uri.
            //b/17996549 Fatal null pointer exception when removing an account from the device
            final Uri accountUri = intent.getParcelableExtra(Utils.EXTRA_ACCOUNT_URI);
            final Uri extraFolderUri = intent.getParcelableExtra(Utils.EXTRA_FOLDER_URI);
            final FolderUri folderUri =
                    extraFolderUri == null ? null : new FolderUri(extraFolderUri);

            NotificationUtils.resendNotifications(
                    this, false, accountUri, folderUri, getContactPhotoFetcher());

        } else if (ACTION_RESEND_NOTIFICATIONS_WEAR.equals(action)) {
            final Account account = intent.getParcelableExtra(Utils.EXTRA_ACCOUNT);
            final Folder folder = intent.getParcelableExtra(Utils.EXTRA_FOLDER);
            final Uri conversationUri = intent.getParcelableExtra(Utils.EXTRA_CONVERSATION);

            // Mark the conversation as read and refresh the notifications.  This happens
            // when user replies to a conversation remotely from a Wear device.
            NotificationUtils.markConversationAsReadAndSeen(this, conversationUri);
            NotificationUtils.resendNotifications(this, false, account.uri,
                    folder.folderUri, getContactPhotoFetcher());
        } else if (ACTION_SEND_SET_NEW_EMAIL_INDICATOR.equals(action)) {
            final int unreadCount = intent.getIntExtra(NotificationUtils.EXTRA_UNREAD_COUNT, 0);
            final int unseenCount = intent.getIntExtra(NotificationUtils.EXTRA_UNSEEN_COUNT, 0);
            final Account account = intent.getParcelableExtra(Utils.EXTRA_ACCOUNT);
            final Folder folder = intent.getParcelableExtra(Utils.EXTRA_FOLDER);
            final boolean getAttention =
                    intent.getBooleanExtra(NotificationUtils.EXTRA_GET_ATTENTION, false);

            NotificationUtils.setNewEmailIndicator(this, unreadCount, unseenCount,
                    account, folder, getAttention, getContactPhotoFetcher());
        } else if (Intent.ACTION_DEVICE_STORAGE_LOW.equals(action)) {
            // The storage_low state is recorded centrally even though
            // no handler might be present to change application state
            // based on state changes.
            StorageLowState.setIsStorageLow(true);
        } else if (Intent.ACTION_DEVICE_STORAGE_OK.equals(action)) {
            StorageLowState.setIsStorageLow(false);
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        //NOTE: handle the intent and call showMailSendFaildNotification to show notification
        }else if(ACTION_SEND__MAIL_FAILED_NOTIFICATIONS.equals(action)){
            NotificationController nc = NotificationController.getInstance(this);
            final int failedNumbers = intent.getIntExtra(NotificationUtils.EXTRA_FAILED_COUNT, 0);
            final long accountId = intent.getLongExtra(NotificationUtils.EXTRA_ACCOUNT_ID, -1);
            nc.showMailSendFaildNotification(accountId, failedNumbers);
        }
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
        else if(ACTION_SEND_CALENDAR_NOTIFICATIONS.equals(action)){
            NotificationController nc = NotificationController.getInstance(this);
            final String packageName = intent.getStringExtra(PermissionUtil.EXTRA_PACKAGENAME);
            nc.showCalendarNotification(packageName);
        } else if(ACTION_SEND_CONTACTS_NOTIFICATIONS.equals(action)){
            NotificationController nc = NotificationController.getInstance(this);
            final String packageName = intent.getStringExtra(PermissionUtil.EXTRA_PACKAGENAME);
            nc.showContactsNotification(packageName);
        } else if (ACTION_SEND_STORAGE_NOTIFICATIONS.equals(action)){     //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
            NotificationController nc = NotificationController.getInstance(this);
            final String packageName = intent.getStringExtra(PermissionUtil.EXTRA_PACKAGENAME);
            nc.showStoragePermissionNotification(packageName);
            //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
        }
        //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E
    }

    public static void broadcastBackupDataChanged(final Context context) {
      //TS: wenggangjin 2015-01-21 EMAIL BUGFIX_904760 MOD_S
//        final Intent intent = new Intent(ACTION_BACKUP_DATA_CHANGED);
//        context.startService(intent);
      //TS: wenggangjin 2015-01-21 EMAIL BUGFIX_904760 MOD_E
    }

    /**
     * Derived classes should override this method if they wish to provide their
     * own photo loading behavior separate from the ContactProvider-based default.
     * The default behavior of this method returns null.
     */
    public ContactPhotoFetcher getContactPhotoFetcher() {
        return null;
    }
}
