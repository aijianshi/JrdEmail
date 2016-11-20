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
 *Tag               Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-991085     2015/03/30   jin.dong      [Email]After the web side to change the password, MS without prompting
 *CR_585337      2015-09-21  chao.zhang       Exchange Email resend mechanism
 *BUGFIX_709873     2015/11/20   kaifeng.lu   [Android L][Email]One account sent more than one mails failed,the notification display "Multiple accounts"
 *BUGFIX-944797  2015-11-26   jian.xu         [Android L][Email]Retry notification not disappear after reconnect wifi
 *BUGFIX-861247  2015-12-17   zheng.zou       Receive a mail with many times bell ring
 *BUGFIX-1190892  2015-12-18   kaifeng.lu       [Android 6.0][Email][Monitor]Email crash by FATAL EXCEPTION: EmailNotification
 *BUGFIX-1118361 2015/12/08    jian.xu        [Google CTS][GTS] Many application of FC Problem during test
 *BUGFIX-1272060 2016/01/04    kaifeng.lu     [Android 6.0][Email][Ergo]The sent failed emails number and current email account name should be removed.
 *BUGFIX_1162996 2015/1/20     yanhua.chen    [Android 6.0][Email]TCL account pop up permission needed window continuously if disable contact/calendar permission of exchange
 *BUGFIX-1128322 2015/12/30    junwei-xu      [GTS-2.1_R2]Unfortunately, Google Play services,Weather, Google Playservices, Google APP,Email has stopped.
 ===========================================================================
 */
package com.tct.email;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.tct.email.R;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.utility.EmailAsyncTask;
import com.tct.mail.NotificationActionIntentService;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.SparseLongArray;
import com.tct.mail.utils.Utils;
import com.tct.email.activity.setup.AccountSecurity;
import com.tct.email.activity.setup.HeadlessAccountSettingsLoader;
import com.tct.email.provider.EmailProvider;
import com.tct.email.service.EmailServiceUtils;
import com.tct.mail.preferences.FolderPreferences;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.utils.Clock;
import com.tct.mail.utils.NotificationUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that manages notifications.
 */
public class NotificationController {
    private static final String LOG_TAG = LogTag.getLogTag();

    private static final int NOTIFICATION_ID_ATTACHMENT_WARNING = 3;
    private static final int NOTIFICATION_ID_PASSWORD_EXPIRING = 4;
    private static final int NOTIFICATION_ID_PASSWORD_EXPIRED = 5;
    private static final int NOTIFICATION_ID_SEND_FAIL_WARNING = 6;
    private static final int NOTIFICATION_ID_BASE_MASK = 0xF0000000;
    private static final int NOTIFICATION_ID_BASE_LOGIN_WARNING = 0x20000000;
    private static final int NOTIFICATION_ID_BASE_SECURITY_NEEDED = 0x30000000;
    private static final int NOTIFICATION_ID_BASE_SECURITY_CHANGED = 0x40000000;
    private static final SparseLongArray sLastUnreadIds  = new SparseLongArray();     //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
    public static final String TCT_ACTION_MANAGE_APP = "android.intent.action.tct.MANAGE_PERMISSIONS";
    public static final String TCT_EXTRA_PACKAGE_NAME = "android.intent.extra.tct.PACKAGE_NAME";
    public static final int EXCHANGE_NEWCALENDAR_NOTIFICATION_ID = 11111111;
    public static final int EXCHANGE_NEWCONTACTS_NOTIFICATION_ID = 11111112;
    public static final int EXCHANGE_NEWSTORAGE_NOTIFICATION_ID = 11111113;     //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E

    private static NotificationThread sNotificationThread;
    private static Handler sNotificationHandler;
    private static NotificationController sInstance;
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final Clock mClock;
    /** Maps account id to its observer */
    private final Map<Long, ContentObserver> mNotificationMap =
            new HashMap<Long, ContentObserver>();
    private ContentObserver mAccountObserver;

    /** Constructor */
    protected NotificationController(Context context, Clock clock) {
        mContext = context.getApplicationContext();
        EmailContent.init(context);
        mNotificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        mClock = clock;
    }

    /** Singleton access */
    public static synchronized NotificationController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NotificationController(context, Clock.INSTANCE);
        }
        return sInstance;
    }

    /**
     * Return whether or not a notification, based on the passed-in id, needs to be "ongoing"
     * @param notificationId the notification id to check
     * @return whether or not the notification must be "ongoing"
     */
    private static boolean needsOngoingNotification(int notificationId) {
        // "Security needed" must be ongoing so that the user doesn't close it; otherwise, sync will
        // be prevented until a reboot.  Consider also doing this for password expired.
        return (notificationId & NOTIFICATION_ID_BASE_MASK) == NOTIFICATION_ID_BASE_SECURITY_NEEDED;
    }

    /**
     * Returns a {@link android.support.v4.app.NotificationCompat.Builder} for an event with the
     * given account. The account contains specific rules on ring tone usage and these will be used
     * to modify the notification behaviour.
     *
     * @param accountId The id of the account this notification is being built for.
     * @param ticker Text displayed when the notification is first shown. May be {@code null}.
     * @param title The first line of text. May NOT be {@code null}.
     * @param contentText The second line of text. May NOT be {@code null}.
     * @param intent The intent to start if the user clicks on the notification.
     * @param largeIcon A large icon. May be {@code null}
     * @param number A number to display using {@link Builder#setNumber(int)}. May be {@code null}.
     * @param enableAudio If {@code false}, do not play any sound. Otherwise, play sound according
     *        to the settings for the given account.
     * @return A {@link Notification} that can be sent to the notification service.
     */
    private NotificationCompat.Builder createBaseAccountNotificationBuilder(long accountId,
            String ticker, CharSequence title, String contentText, Intent intent, Bitmap largeIcon,
            Integer number, boolean enableAudio, boolean ongoing) {
        // Pending Intent
        PendingIntent pending = null;
        if (intent != null) {
            pending = PendingIntent.getActivity(
                    mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // NOTE: the ticker is not shown for notifications in the Holo UX
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pending)
                .setLargeIcon(largeIcon)
                .setNumber(number == null ? 0 : number)
                .setSmallIcon(R.drawable.ic_notification_mail_24dp)
                .setWhen(mClock.getTime())
                .setTicker(ticker)
                .setOngoing(ongoing);

        if (enableAudio) {
            Account account = Account.restoreAccountWithId(mContext, accountId);
            setupSoundAndVibration(builder, account);
        }

        return builder;
    }

    /**
     * Generic notifier for any account.  Uses notification rules from account.
     *
     * @param accountId The account id this notification is being built for.
     * @param ticker Text displayed when the notification is first shown. May be {@code null}.
     * @param title The first line of text. May NOT be {@code null}.
     * @param contentText The second line of text. May NOT be {@code null}.
     * @param intent The intent to start if the user clicks on the notification.
     * @param notificationId The ID of the notification to register with the service.
     */
    private void showNotification(long accountId, String ticker, String title,
            String contentText, Intent intent, int notificationId) {
        final NotificationCompat.Builder builder = createBaseAccountNotificationBuilder(accountId,
                ticker, title, contentText, intent, null, null, true,
                needsOngoingNotification(notificationId));
        mNotificationManager.notify(notificationId, builder.build());
    }

    /**
     * Tells the notification controller if it should be watching for changes to the message table.
     * This is the main life cycle method for message notifications. When we stop observing
     * database changes, we save the state [e.g. message ID and count] of the most recent
     * notification shown to the user. And, when we start observing database changes, we restore
     * the saved state.
     */
    public void watchForMessages() {
        ensureHandlerExists();
        // Run this on the message notification handler
        sNotificationHandler.post(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = mContext.getContentResolver();

                // otherwise, start new observers for all notified accounts
                registerMessageNotification(Account.ACCOUNT_ID_COMBINED_VIEW);
                // If we're already observing account changes, don't do anything else
                if (mAccountObserver == null) {
                    LogUtils.i(LOG_TAG, "Observing account changes for notifications");
                    mAccountObserver = new AccountContentObserver(sNotificationHandler, mContext);
                    resolver.registerContentObserver(Account.NOTIFIER_URI, true, mAccountObserver);
                }
            }
        });
    }

    /**
     * Ensures the notification handler exists and is ready to handle requests.
     */

    /**
     * TODO: Notifications jump around too much because we get too many content updates.
     * We should try to make the provider generate fewer updates instead.
     */

    private static final int NOTIFICATION_DELAYED_MESSAGE = 0;
    private static final long NOTIFICATION_DELAY = 15 * DateUtils.SECOND_IN_MILLIS;
    // True if we're coalescing notification updates
    private static boolean sNotificationDelayedMessagePending;
    // True if accounts have changed and we need to refresh everything
    private static boolean sRefreshAllNeeded;
    // Set of accounts we need to regenerate notifications for
    private static final HashSet<Long> sRefreshAccountSet = new HashSet<Long>();
    // These should all be accessed on-thread, but just in case...
    private static final Object sNotificationDelayedMessageLock = new Object();

    private static synchronized void ensureHandlerExists() {
        if (sNotificationThread == null) {
            sNotificationThread = new NotificationThread();
            sNotificationHandler = new Handler(sNotificationThread.getLooper(),
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(final android.os.Message message) {
                            /**
                             * To reduce spamming the notifications, we quiesce updates for a few
                             * seconds to batch them up, then handle them here.
                             */
                            LogUtils.d(LOG_TAG, "Delayed notification processing");
                            synchronized (sNotificationDelayedMessageLock) {
                                sNotificationDelayedMessagePending = false;
                                final Context context = (Context)message.obj;
                                if (sRefreshAllNeeded) {
                                    sRefreshAllNeeded = false;
                                    refreshAllNotificationsInternal(context);
                                }
                                for (final Long accountId : sRefreshAccountSet) {
                                    refreshNotificationsForAccountInternal(context, accountId);
                                }
                                sRefreshAccountSet.clear();
                            }
                            return true;
                        }
                    });
        }
    }

    /**
     * Registers an observer for changes to mailboxes in the given account.
     * NOTE: This must be called on the notification handler thread.
     * @param accountId The ID of the account to register the observer for. May be
     *                  {@link Account#ACCOUNT_ID_COMBINED_VIEW} to register observers for all
     *                  accounts that allow for user notification.
     */
    private void registerMessageNotification(final long accountId) {
        ContentResolver resolver = mContext.getContentResolver();
        if (accountId == Account.ACCOUNT_ID_COMBINED_VIEW) {
            Cursor c = resolver.query(
                    Account.CONTENT_URI, EmailContent.ID_PROJECTION,
                    null, null, null);
            try {
                //TS: junwei-xu 2015-12-30 EMAIL BUGFIX-1128322 MOD_S
                while (c != null && c.moveToNext()) {
                    long id = c.getLong(EmailContent.ID_PROJECTION_COLUMN);
                    registerMessageNotification(id);
                }
                //TS: junwei-xu 2015-12-30 EMAIL BUGFIX-1128322 MOD_E
            } finally {
                //TS: jian.xu 2015-12-08 EMAIL BUGFIX-1118361 MOD_S
                if(c!=null) {
                    c.close();
                }
                //TS: jian.xu 2015-12-08 EMAIL BUGFIX-1118361 MOD_E
            }
        } else {
            ContentObserver obs = mNotificationMap.get(accountId);
            if (obs != null) return;  // we're already observing; nothing to do
            LogUtils.i(LOG_TAG, "Registering for notifications for account " + accountId);
            ContentObserver observer = new MessageContentObserver(
                    sNotificationHandler, mContext, accountId);
            resolver.registerContentObserver(Message.NOTIFIER_URI, true, observer);
            mNotificationMap.put(accountId, observer);
            // Now, ping the observer for any initial notifications
            observer.onChange(true);
        }
    }

    /**
     * Unregisters the observer for the given account. If the specified account does not have
     * a registered observer, no action is performed. This will not clear any existing notification
     * for the specified account. Use {@link NotificationManager#cancel(int)}.
     * NOTE: This must be called on the notification handler thread.
     * @param accountId The ID of the account to unregister from. To unregister all accounts that
     *                  have observers, specify an ID of {@link Account#ACCOUNT_ID_COMBINED_VIEW}.
     */
    private void unregisterMessageNotification(final long accountId) {
        ContentResolver resolver = mContext.getContentResolver();
        if (accountId == Account.ACCOUNT_ID_COMBINED_VIEW) {
            LogUtils.i(LOG_TAG, "Unregistering notifications for all accounts");
            // cancel all existing message observers
            for (ContentObserver observer : mNotificationMap.values()) {
                resolver.unregisterContentObserver(observer);
            }
            mNotificationMap.clear();
        } else {
            LogUtils.i(LOG_TAG, "Unregistering notifications for account " + accountId);
            ContentObserver observer = mNotificationMap.remove(accountId);
            if (observer != null) {
                resolver.unregisterContentObserver(observer);
            }
        }
    }

    public static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_CONVERSATION = "conversationUri";
    public static final String EXTRA_FOLDER = "folder";

    /** Sets up the notification's sound and vibration based upon account details. */
    private void setupSoundAndVibration(
            NotificationCompat.Builder builder, Account account) {
        String ringtoneUri = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
        boolean vibrate = false;

        // Use the Inbox notification preferences
        final Cursor accountCursor = mContext.getContentResolver().query(EmailProvider.uiUri(
                "uiaccount", account.mId), UIProvider.ACCOUNTS_PROJECTION, null, null, null);

        com.tct.mail.providers.Account uiAccount = null;
        try {
            if (accountCursor.moveToFirst()) {
                uiAccount = com.tct.mail.providers.Account.builder().buildFrom(accountCursor);
            }
        } finally {
            accountCursor.close();
        }

        if (uiAccount != null) {
            final Cursor folderCursor =
                    mContext.getContentResolver().query(uiAccount.settings.defaultInbox,
                            UIProvider.FOLDERS_PROJECTION, null, null, null);

            if (folderCursor == null) {
                // This can happen when the notification is for the security policy notification
                // that happens before the account is setup
                LogUtils.w(LOG_TAG, "Null folder cursor for mailbox %s",
                        uiAccount.settings.defaultInbox);
            } else {
                Folder folder = null;
                try {
                    if (folderCursor.moveToFirst()) {
                        folder = new Folder(folderCursor);
                    }
                } finally {
                    folderCursor.close();
                }

                if (folder != null) {
                    final FolderPreferences folderPreferences = new FolderPreferences(
                            mContext, uiAccount.getEmailAddress(), folder, true /* inbox */);

                    ringtoneUri = folderPreferences.getNotificationRingtoneUri();
                    vibrate = folderPreferences.isNotificationVibrateEnabled();
                } else {
                    LogUtils.e(LOG_TAG,
                            "Null folder for mailbox %s", uiAccount.settings.defaultInbox);
                }
            }
        } else {
            LogUtils.e(LOG_TAG, "Null uiAccount for account id %d", account.mId);
        }

        int defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }

        builder.setSound(TextUtils.isEmpty(ringtoneUri) ? null : Uri.parse(ringtoneUri))
            .setDefaults(defaults);
    }

    /**
     * Show (or update) a notification that the given attachment could not be forwarded. This
     * is a very unusual case, and perhaps we shouldn't even send a notification. For now,
     * it's helpful for debugging.
     *
     * NOTE: DO NOT CALL THIS METHOD FROM THE UI THREAD (DATABASE ACCESS)
     */
    public void showDownloadForwardFailedNotificationSynchronous(Attachment attachment) {
        final Message message = Message.restoreMessageWithId(mContext, attachment.mMessageKey);
        if (message == null) return;
        final Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, message.mMailboxKey);
        showNotification(mailbox.mAccountKey,
                mContext.getString(R.string.forward_download_failed_ticker),
                mContext.getString(R.string.forward_download_failed_title),
                attachment.mFileName,
                null,
                NOTIFICATION_ID_ATTACHMENT_WARNING);
    }

    /**
     * Returns a notification ID for login failed notifications for the given account account.
     */
    private static int getLoginFailedNotificationId(long accountId) {
        return NOTIFICATION_ID_BASE_LOGIN_WARNING + (int)accountId;
    }
    //TS:kaifeng.lu 2015-11-20 EMAIL BUGFIX_709873  ADD_S
    private static int getSendFailedNotificationId(long accountId){
        return  NOTIFICATION_ID_SEND_FAIL_WARNING +(int)accountId;
    }
    //TS:kaifeng.lu 2015-11-20 EMAIL BUGFIX_709873  ADD_E

    /**
     * Show (or update) a notification that there was a login failure for the given account.
     *
     * NOTE: DO NOT CALL THIS METHOD FROM THE UI THREAD (DATABASE ACCESS)
     */
    public void showLoginFailedNotificationSynchronous(long accountId, boolean incoming) {
        final Account account = Account.restoreAccountWithId(mContext, accountId);
        if (account == null) return;
        final Mailbox mailbox = Mailbox.restoreMailboxOfType(mContext, accountId,
                Mailbox.TYPE_INBOX);
        if (mailbox == null) return;

        final Intent settingsIntent;
        if (incoming) {
            settingsIntent = new Intent(Intent.ACTION_VIEW,
                    HeadlessAccountSettingsLoader.getIncomingSettingsUri(accountId));
            //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_S
            // NOTE: Add the flag that HeadlessAccountSettingsLoader may use it and send it to
            // AccountServerSettingsActivity.
            // where we want show incoming view,after user do the password updated,Auto go to
            // outgoing view.
            settingsIntent.putExtra("AUTHENTICATIONFAILED", true);
            // TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_E
        } else {
            settingsIntent = new Intent(Intent.ACTION_VIEW,
                    HeadlessAccountSettingsLoader.getOutgoingSettingsUri(accountId));
        }
        showNotification(mailbox.mAccountKey,
                mContext.getString(R.string.login_failed_ticker, account.mDisplayName),
                mContext.getString(R.string.login_failed_title),
                account.getDisplayName(),
                settingsIntent,
                getLoginFailedNotificationId(accountId));
    }

    /**
     * Cancels the login failed notification for the given account.
     */
    public void cancelLoginFailedNotification(long accountId) {
        mNotificationManager.cancel(getLoginFailedNotificationId(accountId));
    }

    /**
     * Show (or update) a notification that the user's password is expiring. The given account
     * is used to update the display text, but, all accounts share the same notification ID.
     *
     * NOTE: DO NOT CALL THIS METHOD FROM THE UI THREAD (DATABASE ACCESS)
     */
    public void showPasswordExpiringNotificationSynchronous(long accountId) {
        final Account account = Account.restoreAccountWithId(mContext, accountId);
        if (account == null) return;

        final Intent intent = AccountSecurity.actionDevicePasswordExpirationIntent(mContext,
                accountId, false);
        final String accountName = account.getDisplayName();
        final String ticker =
            mContext.getString(R.string.password_expire_warning_ticker_fmt, accountName);
        final String title = mContext.getString(R.string.password_expire_warning_content_title);
        showNotification(accountId, ticker, title, accountName, intent,
                NOTIFICATION_ID_PASSWORD_EXPIRING);
    }

    /**
     * Show (or update) a notification that the user's password has expired. The given account
     * is used to update the display text, but, all accounts share the same notification ID.
     *
     * NOTE: DO NOT CALL THIS METHOD FROM THE UI THREAD (DATABASE ACCESS)
     */
    public void showPasswordExpiredNotificationSynchronous(long accountId) {
        final Account account = Account.restoreAccountWithId(mContext, accountId);
        if (account == null) return;

        final Intent intent = AccountSecurity.actionDevicePasswordExpirationIntent(mContext,
                accountId, true);
        final String accountName = account.getDisplayName();
        final String ticker = mContext.getString(R.string.password_expired_ticker);
        final String title = mContext.getString(R.string.password_expired_content_title);
        showNotification(accountId, ticker, title, accountName, intent,
                NOTIFICATION_ID_PASSWORD_EXPIRED);
    }

    /**
     * Cancels any password expire notifications [both expired & expiring].
     */
    public void cancelPasswordExpirationNotifications() {
        mNotificationManager.cancel(NOTIFICATION_ID_PASSWORD_EXPIRING);
        mNotificationManager.cancel(NOTIFICATION_ID_PASSWORD_EXPIRED);
    }

    /**
     * Show (or update) a security needed notification. If tapped, the user is taken to a
     * dialog asking whether he wants to update his settings.
     */
    public void showSecurityNeededNotification(Account account) {
        Intent intent = AccountSecurity.actionUpdateSecurityIntent(mContext, account.mId, true);
        String accountName = account.getDisplayName();
        String ticker =
            mContext.getString(R.string.security_needed_ticker_fmt, accountName);
        String title = mContext.getString(R.string.security_notification_content_update_title);
        showNotification(account.mId, ticker, title, accountName, intent,
                (int)(NOTIFICATION_ID_BASE_SECURITY_NEEDED + account.mId));
    }

    /**
     * Show (or update) a security changed notification. If tapped, the user is taken to the
     * account settings screen where he can view the list of enforced policies
     */
    public void showSecurityChangedNotification(Account account) {
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                HeadlessAccountSettingsLoader.getIncomingSettingsUri(account.getId()));
        final String accountName = account.getDisplayName();
        final String ticker =
            mContext.getString(R.string.security_changed_ticker_fmt, accountName);
        final String title =
                mContext.getString(R.string.security_notification_content_change_title);
        showNotification(account.mId, ticker, title, accountName, intent,
                (int)(NOTIFICATION_ID_BASE_SECURITY_CHANGED + account.mId));
    }

    /**
     * Show (or update) a security unsupported notification. If tapped, the user is taken to the
     * account settings screen where he can view the list of unsupported policies
     */
    public void showSecurityUnsupportedNotification(Account account) {
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                HeadlessAccountSettingsLoader.getIncomingSettingsUri(account.getId()));
        final String accountName = account.getDisplayName();
        final String ticker =
            mContext.getString(R.string.security_unsupported_ticker_fmt, accountName);
        final String title =
                mContext.getString(R.string.security_notification_content_unsupported_title);
        showNotification(account.mId, ticker, title, accountName, intent,
                (int)(NOTIFICATION_ID_BASE_SECURITY_NEEDED + account.mId));
   }

    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    /**
     * Show (or update) a send failed notification. If tapped, the user is taken to the OUTBOX view
     * where he can view the list of failed mails. if tap retry,mail will retry to send.
     */
    public void showMailSendFaildNotification(long accountId, int number) {
        Account account = Account.restoreAccountWithId(mContext, accountId);
        if (account == null) {
            LogUtils.e(LOG_TAG, "Null account during notification SEND_FAILED ");
            return;
        }
         NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext);
        ContentResolver conentResolver = mContext.getContentResolver();
        Folder UIFolder = queryUIFolder(conentResolver, accountId);
        Folder UIInboxFolder = queryUIInboxFolder(conentResolver, accountId);
        com.tct.mail.providers.Account UIAccount = queryUIaccount(conentResolver,accountId);
        if (UIFolder == null || UIAccount == null || UIInboxFolder == null) {
            LogUtils.e(LOG_TAG,
                    "Null UIFolder or null UIAccount during showMailSendFaildNotification,do nothing,just return ");
            return;
        }
        //get account address
        String accountAddress = account.getEmailAddress();
        //get account senderName,if null,use address instand
        String senderName = account.getSenderName();
        //get the notification's title(2 emails not send or Email not send)
        String title = createTitle(number);
        //get the warning icon
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_warning_grey);

        //get the conversation
        final Uri.Builder uriBuilder = UIFolder.conversationListUri.buildUpon();
        Cursor conversationsCursor = mContext.getContentResolver().query(uriBuilder.build(),
                UIProvider.CONVERSATION_PROJECTION, null, null, null);

        //The Martro style only supported after android L.
             //TS:kaifeng.lu 2016-01-04 EMAIL BUGFIX_1272060   MOD_S
        if (com.tct.mail.utils.Utils.isRunningLOrLater()) {
            notification.setColor(
                    mContext.getResources().getColor(R.color.notification_icon_mail_orange));
            //TS:kaifeng.lu 2016-01-04 EMAIL BUGFIX_1272060   MOD_E
               NotificationCompat.InboxStyle digest =
                    new NotificationCompat.InboxStyle(notification);

            // query current account's outbox mails.and get the subject.
            // Subject1;
            // Subject2;
            // Subject3;
            if (conversationsCursor != null) {
                try {
                    while (conversationsCursor.moveToNext()) {
                        final Conversation conversation = new Conversation(conversationsCursor);
                        String sub = createSubject(conversation);
                        digest.addLine(sub);
                    }
                } catch (Exception e) {
                    LogUtils.e(LOG_TAG, "exception happen during get notification subject",
                            e.getMessage());
                } finally {
                    if (conversationsCursor != null) {
                        conversationsCursor.close();
                    }
                }
            }

               //only 1 mails failed, show the subject and its senderName and emailAddress.
               //Line1: show the mail's subject ,same with METHOD: notification.setContentText
            //TS:kaifeng.lu 2015-11-20 EMAIL BUGFIX_709873  MOD_S
            if (number > 0) {
                if (TextUtils.isEmpty(senderName)) {
                    //TS:kaifeng.lu 2016-01-04 EMAIL BUGFIX_1272060   DEL
//                    digest.addLine(accountAddress);
                } else {
                    digest.addLine(senderName); // Line2: show the senderName,same with METHOD:Notification#setSubText()
                }
                digest.setSummaryText(accountAddress);
            }
            //TS:kaifeng.lu 2015-11-20 EMAIL BUGFIX_709873  MOD_E
            notification.setContentTitle(title);
            notification.setTicker(accountAddress);
            notification.setLargeIcon(icon);
            notification.setSmallIcon(R.drawable.ic_notification_mail_24dp);
            //set the content click/tap intent
            //it will trigger going to OUBOX
            Intent toOutboxAction = Utils.createViewFolderIntent(mContext, UIFolder.folderUri.fullUri, UIAccount);
            PendingIntent contentIntent =PendingIntent.getActivity(
                    mContext, 0, toOutboxAction, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(contentIntent);
            //set the action click intent.
            //it will trigger the refresh.
            PendingIntent actionIntent = createRefreshIntent(UIFolder, true, account.mId);
            notification.addAction(R.drawable.ic_refresh_grey_24dp, mContext.getResources()
                    .getString(R.string.retry), actionIntent);

            //set the failed mails.
            //TS:kaifeng.lu 2016-01-04 EMAIL BUGFIX_1272060   DEL
//            notification.setNumber(number);

            //set the light and sound
             FolderPreferences folderPreferences =
                    new FolderPreferences(mContext, UIAccount.getAccountId(), UIInboxFolder, true);
             boolean vibrate = folderPreferences.isNotificationVibrateEnabled();
             //not give sound for retry notification
             //String ringtoneUri = folderPreferences.getNotificationRingtoneUri();
             int defaults = 0;
             if (vibrate) {
                 defaults |= Notification.DEFAULT_VIBRATE;
             }
             // TS: zheng.zou 2015-09-10 EMAIL BUGFIX-557052 MOD_S
           //defaults |= Notification.FLAG_SHOW_LIGHTS;
            defaults |= Notification.DEFAULT_LIGHTS;
           notification.setDefaults(defaults);
           notification.setLights(0xff00ff00, 280, 2080);
           // TS: zheng.zou 2015-09-10 EMAIL BUGFIX-557052 MOD_E
           // notification.setSound(TextUtils.isEmpty(ringtoneUri) ? null
           //          : Uri.parse(ringtoneUri));
           //  LogUtils.i(LOG_TAG, "failed email in %s vibrateWhen: %s, playing notification: %s",
           //          LogUtils.sanitizeName(LOG_TAG, account.getEmailAddress()), vibrate);
             // TS: chao.zhang 2015-09-24 EMAIL FEATURE-585337 MOD_S
             //NOTE: UE:the notification only can be cancel by click.
             notification.setOngoing(true);
        }
        Notification warnningNotification= notification.build();
        warnningNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        //TS:kaifeng.lu 2015-11-20 EMAIL BUGFIX_709873  MOD_S
        mNotificationManager.notify(getSendFailedNotificationId(accountId), warnningNotification);
        //TS:kaifeng.lu 2015-11-20 EMAIL BUGFIX_709873  MOD_E
        // TS: chao.zhang 2015-09-24 EMAIL FEATURE-585337 MOD_S
    }

    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
    /**
     * Show (or update) a exchange sync new calendar notification. If tapped, the user is taken to the Exchange permission view
     * where he can view the exchange permission. if tap never ask again,notification will not show.
     */
    public void showCalendarNotification(String packageName) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext);
        //get the warning icon
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_warning_grey);
        //The Martro style only supported after android L.
        if (com.tct.mail.utils.Utils.isRunningLOrLater()) {
            notification.setColor(mContext.getResources().getColor(R.color.notification_icon_mail_orange));
        }
        notification.setContentTitle(mContext.getResources().getString(R.string.sync_calendar_title));
        notification.setContentText(mContext.getResources().getString(R.string.sync_calendar_content));
        notification.setLargeIcon(icon);
        notification.setSmallIcon(R.drawable.ic_notification_mail_24dp);
        notification.setAutoCancel(true);//TS: zheng.zou 2016-3-19 EMAIL BUGFIX-1841389 ADD_S
        //set the content click/tap intent
        //it will trigger going to app permissions settings
        Intent gotoSettingsIntent = gotoSettingsIntent(mContext,packageName);
        PendingIntent contentIntent = PendingIntent.getActivity(
                mContext, 0, gotoSettingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(contentIntent);

        //set the action click intent.
        //it will never receive notification.
        //never ask again
        Intent clickIntent = new Intent(NotificationActionIntentService.ACTION_CALENDAR_NEVER_ASK_AGAIN);
        clickIntent.setPackage(mContext.getPackageName());
        PendingIntent pendButtonIntent = PendingIntent.getService(mContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.addAction(R.drawable.notification_button,
                mContext.getResources().getString(R.string.notification_never_ask_again).toUpperCase(),pendButtonIntent);

        Notification warningNotification = notification.build();
//        warningNotification.flags |= Notification.FLAG_AUTO_CANCEL;
//        warningNotification.flags |= Notification.DEFAULT_VIBRATE;
        NotificationManager nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        nm.notify(EXCHANGE_NEWCALENDAR_NOTIFICATION_ID,warningNotification);
    }

    /**
     * Show (or update) a exchange sync new contacts notification. If tapped, the user is taken to the Exchange permission view
     * where he can view the exchange permission. if tap never ask again,notification will not show.
     */
    public void showContactsNotification(String packageName) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext);
        //get the warning icon
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_warning_grey);
        //The Martro style only supported after android L.
        if (com.tct.mail.utils.Utils.isRunningLOrLater()) {
            notification.setColor(mContext.getResources().getColor(R.color.notification_icon_mail_orange));
        }
        notification.setContentTitle(mContext.getResources().getString(R.string.sync_contacts_title));
        notification.setContentText(mContext.getResources().getString(R.string.sync_contacts_content));
        notification.setLargeIcon(icon);
        notification.setSmallIcon(R.drawable.ic_notification_mail_24dp);
        notification.setAutoCancel(true);//TS: zheng.zou 2016-3-19 EMAIL BUGFIX-1841389 ADD_S
        //set the content click/tap intent
        //it will trigger going to app permissions settings
        Intent gotoSettingsIntent = gotoSettingsIntent(mContext,packageName);
        PendingIntent contentIntent = PendingIntent.getActivity(
                mContext, 0, gotoSettingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(contentIntent);

        //set the action click intent.
        //it will never receive notification.
        //never ask again
        Intent clickIntent = new Intent(NotificationActionIntentService.ACTION_CONTACTS_NEVER_ASK_AGAIN);
        clickIntent.setPackage(mContext.getPackageName());
        PendingIntent pendButtonIntent = PendingIntent.getService(mContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.addAction(R.drawable.notification_button,
                mContext.getResources().getString(R.string.notification_never_ask_again).toUpperCase(),pendButtonIntent);

        Notification warningNotification = notification.build();
//        warningNotification.flags |= Notification.FLAG_AUTO_CANCEL;
//        warningNotification.flags |= Notification.DEFAULT_VIBRATE;
        NotificationManager nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        nm.notify(EXCHANGE_NEWCONTACTS_NOTIFICATION_ID,warningNotification);
    }

    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
    public void showStoragePermissionNotification(String pgkName){
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext);
        //get the warning icon
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_warning_grey);
        //The Martro style only supported after android L.
        if (com.tct.mail.utils.Utils.isRunningLOrLater()) {
            notification.setColor(mContext.getResources().getColor(R.color.notification_icon_mail_orange));
        }
        notification.setContentTitle(mContext.getResources().getString(R.string.sync_storage_title));
        notification.setContentText(mContext.getResources().getString(R.string.sync_storage_content));
        notification.setLargeIcon(icon);
        notification.setSmallIcon(R.drawable.ic_notification_mail_24dp);
        //set the content click/tap intent
        //it will trigger going to app permissions settings
        Intent gotoSettingsIntent = gotoSettingsIntent(mContext,pgkName);
        PendingIntent contentIntent = PendingIntent.getActivity(
                mContext, 0, gotoSettingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true); //TS: zheng.zou 2016-3-19 EMAIL BUGFIX-1841389 ADD_S

        //set the action click intent.
        //it will never receive notification.
        //never ask again
        Intent clickIntent = new Intent(NotificationActionIntentService.ACTION_STORAGE_NEVER_ASK_AGAIN);
        clickIntent.setPackage(mContext.getPackageName());
        PendingIntent pendButtonIntent = PendingIntent.getService(mContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.addAction(R.drawable.notification_button,
                mContext.getResources().getString(R.string.notification_never_ask_again).toUpperCase(),pendButtonIntent);

        Notification warningNotification = notification.build();
        NotificationManager nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        nm.notify(EXCHANGE_NEWSTORAGE_NOTIFICATION_ID,warningNotification);
    }
    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E

    /**
     * return intent,the user is taken to the Exchange permission view
     */
    public  Intent gotoSettingsIntent(Context context,String packageName) {
        Intent intent;
        if (isIntentExisting(context, TCT_ACTION_MANAGE_APP)) {
            //Goto setting application permission
            intent = new Intent(TCT_ACTION_MANAGE_APP);
            intent.putExtra(TCT_EXTRA_PACKAGE_NAME, packageName);
        } else {
            //Goto settings details
            final Uri packageURI = Uri.parse("package:" + packageName);
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        }
        return intent;
    }

    public  boolean isIntentExisting(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_ALL);
        return resolveInfo.size() > 0;
    }
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E

    /**
     * query the oubox's folder.
     * @param conentResolver
     * @param accountId
     * @return Folder
     */
    private Folder queryUIFolder(ContentResolver conentResolver, long accountId) {
        Mailbox outbox = Mailbox.restoreMailboxOfType(mContext, accountId, Mailbox.TYPE_OUTBOX);
        if (outbox == null) {
            LogUtils.e(LOG_TAG, "null oubox in queryUIFolder,may account deleted,return" );
            return null;
        }
        Uri folderUri = EmailProvider.uiUri("uifolder", outbox.mId);
        Cursor folderCursor = conentResolver.query(folderUri, UIProvider.FOLDERS_PROJECTION, null,
                null, null);
        Folder folder = null;
        if(folderCursor != null){
            try {
                if (folderCursor.moveToFirst()) {
                    folder = new Folder(folderCursor);
                } else {
                    LogUtils.e(LOG_TAG, "Empty folder cursor for account ", "mail uri is",
                            folderUri);
                    return folder;
                }
            } finally {
                folderCursor.close();
            }
        }
        return folder;
    }

    /**
     * query the inbox's folder.
     * @param conentResolver
     * @param accountId
     * @return Folder
     */
    private Folder queryUIInboxFolder(ContentResolver conentResolver, long accountId) {
        Mailbox inbox = Mailbox.restoreMailboxOfType(mContext, accountId, Mailbox.TYPE_INBOX);
        if (inbox == null) {
            LogUtils.e(LOG_TAG, "null inbox in queryUIFolder,may account deleted,return" );
            return null;
        }
        Uri folderUri = EmailProvider.uiUri("uifolder", inbox.mId);
        Cursor folderCursor = conentResolver.query(folderUri, UIProvider.FOLDERS_PROJECTION, null,
                null, null);
        Folder folder = null;
        if(folderCursor != null){
            try {
                if (folderCursor.moveToFirst()) {
                    folder = new Folder(folderCursor);
                } else {
                    LogUtils.e(LOG_TAG, "Empty folder cursor for account ", "mail uri is",
                            folderUri);
                    return folder;
                }
            } finally {
                folderCursor.close();
            }
        }
        return folder;
    }
    /**
     * query the account's UIaccount.
     * @param conentResolver
     * @param accountId
     * @return com.tct.mail.providers.Account
     */
    private com.tct.mail.providers.Account queryUIaccount(ContentResolver conentResolver,
            long accountId) {
        com.tct.mail.providers.Account account = null;
        Cursor accountCursor = null;
        Uri accountUri = EmailProvider.uiUri("uiaccount", accountId);
        try {
            accountCursor = conentResolver.query(accountUri,
                    UIProvider.ACCOUNTS_PROJECTION, null, null, null);
            if (accountCursor == null) {
                LogUtils.e(LOG_TAG, "Null account cursor for account " + accountUri);
                return null;
            }
            if (accountCursor.moveToFirst()) {
                account = com.tct.mail.providers.Account.builder().buildFrom(accountCursor);
            }
        } catch (Exception e) {
            LogUtils.e(LOG_TAG, "Exception happen in queryUIaccount."
                    + e);
        } finally {
            accountCursor.close();
        }
        if (account == null) {
            LogUtils.d(LOG_TAG, "Tried to create a notification for a missing account "
                    + accountUri);
        }
        return account;
    }
    /**
     * @param context a context used to construct the title
     * @param failedCount the number of failed messages
     * @return e.g. "1 Email not send" or "2 emails not send"
     */
    private  String createTitle( int failedCount) {
        final Resources resources = mContext.getResources();
        return resources.getQuantityString(R.plurals.send_failed_messages, failedCount, failedCount);
    }

    /**
     * create the subjects display in notification. 1 mails:just show the subject 2 mails:
     * subject1;subject2;subject3...
     * @param Conversation
     * @return Strings.
     */
    private String createSubject(Conversation conversation) {
        StringBuilder subject = new StringBuilder();
        if (TextUtils.isEmpty(conversation.subject)) {
            //subject empty,what ?show nothing? "No Subject" is good
            subject.append("No Subject");
        } else {
            subject.append(conversation.subject);
        }
        subject.append(";");
        return subject.toString();
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-ID ADD_E
    private PendingIntent createRefreshIntent(Folder folder, boolean cleanStatus, long accountId) {
        String packageName = mContext.getPackageName();
        long boxId = -1;
        Mailbox outbox = Mailbox.restoreMailboxOfType(mContext, accountId, Mailbox.TYPE_OUTBOX);
        if (outbox != null) {
            boxId = outbox.mId;
        }
        Intent clickIntent = new Intent(NotificationActionIntentService.ACTION_REFRESH);
        clickIntent.setPackage(packageName);
        clickIntent.putExtra(NotificationUtils.EXTRA_NEED_CLEAN_STATUS, cleanStatus);
        clickIntent.putExtra(NotificationUtils.EXTRA_OUTBOX_ID, boxId);
        // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 MOD_S
        //Note: Get truth notification id.
        clickIntent.putExtra(NotificationUtils.EXTRA_FAIL_NOTIFICATION_ID, getSendFailedNotificationId(accountId));
        // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 MOD_E
        clickIntent.setData(folder.refreshUri);
        // clickIntent.setData(notificationAction.mConversation.uri);
        PendingIntent actionIntent = PendingIntent.getService(
                mContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return actionIntent;
    }

    // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 MOD_S
    public void cancelFailStatusNotification(long accountId) {
        NotificationManager notificationManager = getInstance(mContext).mNotificationManager;
        notificationManager.cancel(getSendFailedNotificationId(accountId));
    }
    // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 MOD_E

    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
    public void cancelCalendarOrContactsNotification(){
        NotificationManager notificationManager = getInstance(mContext).mNotificationManager;
        notificationManager.cancel(NotificationController.EXCHANGE_NEWCALENDAR_NOTIFICATION_ID);
        notificationManager.cancel(NotificationController.EXCHANGE_NEWCONTACTS_NOTIFICATION_ID);
        notificationManager.cancel(NotificationController.EXCHANGE_NEWSTORAGE_NOTIFICATION_ID);   //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
    }
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E
    /**
     * Cancels all security needed notifications.
     */
    public void cancelSecurityNeededNotification() {
        EmailAsyncTask.runAsyncParallel(new Runnable() {
            @Override
            public void run() {
                Cursor c = mContext.getContentResolver().query(Account.CONTENT_URI,
                        Account.ID_PROJECTION, null, null, null);
                try {
                    while (c.moveToNext()) {
                        long id = c.getLong(Account.ID_PROJECTION_COLUMN);
                        mNotificationManager.cancel(
                               (int)(NOTIFICATION_ID_BASE_SECURITY_NEEDED + id));
                    }
                }
                finally {
                    c.close();
                }
            }});
    }

    /**
     * Cancels all notifications for the specified account id. This includes new mail notifications,
     * as well as special login/security notifications.
     */
    public static void cancelNotifications(final Context context, final Account account) {
        final EmailServiceUtils.EmailServiceInfo serviceInfo
                = EmailServiceUtils.getServiceInfoForAccount(context, account.mId);
        if (serviceInfo == null) {
            LogUtils.d(LOG_TAG, "Can't cancel notification for missing account %d", account.mId);
            return;
        }
        final android.accounts.Account notifAccount
                = account.getAccountManagerAccount(serviceInfo.accountType);

        NotificationUtils.clearAccountNotifications(context, notifAccount);

        final NotificationManager notificationManager = getInstance(context).mNotificationManager;

        notificationManager.cancel((int) (NOTIFICATION_ID_BASE_LOGIN_WARNING + account.mId));
        notificationManager.cancel((int) (NOTIFICATION_ID_BASE_SECURITY_NEEDED + account.mId));
        notificationManager.cancel((int) (NOTIFICATION_ID_BASE_SECURITY_CHANGED + account.mId));
    }

    private static void refreshNotificationsForAccount(final Context context,
            final long accountId) {
        synchronized (sNotificationDelayedMessageLock) {
            if (sNotificationDelayedMessagePending) {
                sRefreshAccountSet.add(accountId);
            } else {
                ensureHandlerExists();
                sNotificationHandler.sendMessageDelayed(
                        android.os.Message.obtain(sNotificationHandler,
                                NOTIFICATION_DELAYED_MESSAGE, context), NOTIFICATION_DELAY);
                sNotificationDelayedMessagePending = true;
                refreshNotificationsForAccountInternal(context, accountId);
            }
        }
    }

    private static void refreshNotificationsForAccountInternal(final Context context,
            final long accountId) {
        final Uri accountUri = EmailProvider.uiUri("uiaccount", accountId);

        final ContentResolver contentResolver = context.getContentResolver();

        final Cursor mailboxCursor = contentResolver.query(
                ContentUris.withAppendedId(EmailContent.MAILBOX_NOTIFICATION_URI, accountId),
                null, null, null, null);
        try {
            while (mailboxCursor.moveToNext()) {
                final long mailboxId =
                        mailboxCursor.getLong(EmailContent.NOTIFICATION_MAILBOX_ID_COLUMN);
                if (mailboxId == 0) continue;

                final int unseenCount = mailboxCursor.getInt(
                        EmailContent.NOTIFICATION_MAILBOX_UNSEEN_COUNT_COLUMN);

                final int unreadCount;
                // If nothing is unseen, clear the notification
                if (unseenCount == 0) {
                    unreadCount = 0;
                } else {
                    unreadCount = mailboxCursor.getInt(
                            EmailContent.NOTIFICATION_MAILBOX_UNREAD_COUNT_COLUMN);
                }
                //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_S
                Mailbox mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
                //TS:kaifeng.lu 2015-12-18 EMAIL BUGFIX_1190892  MOD_S
                if (mailbox != null && mailbox.mType == Mailbox.TYPE_INBOX){
                //TS:kaifeng.lu 2015-12-18 EMAIL BUGFIX_1190892  MOD_E
                    final Cursor unreadCursor = contentResolver.query(
                            ContentUris.withAppendedId(EmailContent.MAILBOX_MOST_RECENT_UNREAD_MESSAGE_URI, mailboxId),
                            null, null, null, null);
                    long mostRecentUnreadMsgId = 0;
                    if (unreadCursor != null && unreadCursor.moveToFirst()) {
                        try {
                            mostRecentUnreadMsgId = unreadCursor.getLong(EmailContent.MAILBOX_MOST_RECENT_UNREAD_ID_COULUM);
                        } finally {
                            unreadCursor.close();
                        }
                    }
                    final int key = getUnreadKey(accountId, mailboxId);
                    long lastMostRecentUnreadMsgId = sLastUnreadIds.get(key);
                    LogUtils.i(LOG_TAG,"key="+key+" unseenCount="+unseenCount+" unreadCount="+unreadCount+" lastMostRecentUnreadMsgId = "+lastMostRecentUnreadMsgId+ " mostRecentUnreadMsgId="+mostRecentUnreadMsgId);
                    //no need to send notification if latest Unread id not change
                    if (lastMostRecentUnreadMsgId!=0 && unseenCount!=0 && lastMostRecentUnreadMsgId == mostRecentUnreadMsgId) {
                        LogUtils.i(LOG_TAG,"no need to send notification broadcast, continue");
                        continue;
                    }
                    sLastUnreadIds.put(key, mostRecentUnreadMsgId);
                }
                //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_E

                final Uri folderUri = EmailProvider.uiUri("uifolder", mailboxId);


                LogUtils.d(LOG_TAG, "Changes to account " + accountId + ", folder: "
                        + mailboxId + ", unreadCount: " + unreadCount + ", unseenCount: "
                        + unseenCount);

                final Intent intent = new Intent(UIProvider.ACTION_UPDATE_NOTIFICATION);
                intent.setPackage(context.getPackageName());
                intent.setType(EmailProvider.EMAIL_APP_MIME_TYPE);

                intent.putExtra(UIProvider.UpdateNotificationExtras.EXTRA_ACCOUNT, accountUri);
                intent.putExtra(UIProvider.UpdateNotificationExtras.EXTRA_FOLDER, folderUri);
                intent.putExtra(UIProvider.UpdateNotificationExtras.EXTRA_UPDATED_UNREAD_COUNT,
                        unreadCount);
                intent.putExtra(UIProvider.UpdateNotificationExtras.EXTRA_UPDATED_UNSEEN_COUNT,
                        unseenCount);

                context.sendOrderedBroadcast(intent, null);
            }
        } finally {
            mailboxCursor.close();
        }
    }

    //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_S
    private static int getUnreadKey(long accountId, long mailboxId) {
        return (int) ((accountId << 16) + mailboxId);
    }
    //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_E

    public static void handleUpdateNotificationIntent(Context context, Intent intent) {
        final Uri accountUri =
                intent.getParcelableExtra(UIProvider.UpdateNotificationExtras.EXTRA_ACCOUNT);
        final Uri folderUri =
                intent.getParcelableExtra(UIProvider.UpdateNotificationExtras.EXTRA_FOLDER);
        final int unreadCount = intent.getIntExtra(
                UIProvider.UpdateNotificationExtras.EXTRA_UPDATED_UNREAD_COUNT, 0);
        final int unseenCount = intent.getIntExtra(
                UIProvider.UpdateNotificationExtras.EXTRA_UPDATED_UNSEEN_COUNT, 0);

        final ContentResolver contentResolver = context.getContentResolver();

        final Cursor accountCursor = contentResolver.query(accountUri,
                UIProvider.ACCOUNTS_PROJECTION,  null, null, null);

        if (accountCursor == null) {
            LogUtils.e(LOG_TAG, "Null account cursor for account " + accountUri);
            return;
        }

        com.tct.mail.providers.Account account = null;
        try {
            if (accountCursor.moveToFirst()) {
                account = com.tct.mail.providers.Account.builder().buildFrom(accountCursor);
            }
        } finally {
            accountCursor.close();
        }

        if (account == null) {
            LogUtils.d(LOG_TAG, "Tried to create a notification for a missing account "
                    + accountUri);
            return;
        }

        final Cursor folderCursor = contentResolver.query(folderUri, UIProvider.FOLDERS_PROJECTION,
                null, null, null);

        if (folderCursor == null) {
            LogUtils.e(LOG_TAG, "Null folder cursor for account " + accountUri + ", mailbox "
                    + folderUri);
            return;
        }

        Folder folder = null;
        try {
            if (folderCursor.moveToFirst()) {
                folder = new Folder(folderCursor);
            } else {
                LogUtils.e(LOG_TAG, "Empty folder cursor for account " + accountUri + ", mailbox "
                        + folderUri);
                return;
            }
        } finally {
            folderCursor.close();
        }

        // TODO: we don't always want getAttention to be true, but we don't necessarily have a
        // good heuristic for when it should or shouldn't be.
        NotificationUtils.sendSetNewEmailIndicatorIntent(context, unreadCount, unseenCount,
                account, folder, true /* getAttention */);
    }

    private static void refreshAllNotifications(final Context context) {
        synchronized (sNotificationDelayedMessageLock) {
            if (sNotificationDelayedMessagePending) {
                sRefreshAllNeeded = true;
            } else {
                ensureHandlerExists();
                sNotificationHandler.sendMessageDelayed(
                        android.os.Message.obtain(sNotificationHandler,
                                NOTIFICATION_DELAYED_MESSAGE, context), NOTIFICATION_DELAY);
                sNotificationDelayedMessagePending = true;
                refreshAllNotificationsInternal(context);
            }
        }
    }

    private static void refreshAllNotificationsInternal(final Context context) {
        NotificationUtils.resendNotifications(
                context, false, null, null, null /* ContactPhotoFetcher */);
    }

    /**
     * Observer invoked whenever a message we're notifying the user about changes.
     */
    private static class MessageContentObserver extends ContentObserver {
        private final Context mContext;
        private final long mAccountId;

        public MessageContentObserver(
                final Handler handler, final Context context, final long accountId) {
            super(handler);
            mContext = context;
            mAccountId = accountId;
        }

        @Override
        public void onChange(final boolean selfChange) {
            refreshNotificationsForAccount(mContext, mAccountId);
        }
    }

    /**
     * Observer invoked whenever an account is modified. This could mean the user changed the
     * notification settings.
     */
    private static class AccountContentObserver extends ContentObserver {
        private final Context mContext;
        public AccountContentObserver(final Handler handler, final Context context) {
            super(handler);
            mContext = context;
        }

        @Override
        public void onChange(final boolean selfChange) {
            final ContentResolver resolver = mContext.getContentResolver();
            final Cursor c = resolver.query(Account.CONTENT_URI, EmailContent.ID_PROJECTION,
                null, null, null);
            final Set<Long> newAccountList = new HashSet<Long>();
            final Set<Long> removedAccountList = new HashSet<Long>();
            if (c == null) {
                // Suspender time ... theoretically, this will never happen
                LogUtils.wtf(LOG_TAG, "#onChange(); NULL response for account id query");
                return;
            }
            try {
                while (c.moveToNext()) {
                    long accountId = c.getLong(EmailContent.ID_PROJECTION_COLUMN);
                    newAccountList.add(accountId);
                }
            } finally {
                c.close();
            }
            // NOTE: Looping over three lists is not necessarily the most efficient. However, the
            // account lists are going to be very small, so, this will not be necessarily bad.
            // Cycle through existing notification list and adjust as necessary
            for (final long accountId : sInstance.mNotificationMap.keySet()) {
                if (!newAccountList.remove(accountId)) {
                    // account id not in the current set of notifiable accounts
                    removedAccountList.add(accountId);
                }
            }
            // A new account was added to the notification list
            for (final long accountId : newAccountList) {
                sInstance.registerMessageNotification(accountId);
            }
            // An account was removed from the notification list
            for (final long accountId : removedAccountList) {
                sInstance.unregisterMessageNotification(accountId);
            }

            refreshAllNotifications(mContext);
        }
    }

    /**
     * Thread to handle all notification actions through its own {@link Looper}.
     */
    private static class NotificationThread implements Runnable {
        /** Lock to ensure proper initialization */
        private final Object mLock = new Object();
        /** The {@link Looper} that handles messages for this thread */
        private Looper mLooper;

        public NotificationThread() {
            new Thread(null, this, "EmailNotification").start();
            synchronized (mLock) {
                while (mLooper == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException ex) {
                        // Loop around and wait again
                    }
                }
            }
        }

        @Override
        public void run() {
            synchronized (mLock) {
                Looper.prepare();
                mLooper = Looper.myLooper();
                mLock.notifyAll();
            }
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Looper.loop();
        }

        public Looper getLooper() {
            return mLooper;
        }
    }
}