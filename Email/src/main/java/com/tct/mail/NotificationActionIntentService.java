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
*CR_585337      2015-09-21  chao.zhang       Exchange Email resend mechanism
====================================================================================================================
*/
package com.tct.mail;

import java.util.ArrayList;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.tct.email.NotificationController;
import com.tct.email.R;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.mail.preferences.MailPrefs;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.utils.NotificationActionUtils;
import com.tct.mail.utils.NotificationActionUtils.NotificationAction;
import com.tct.mail.utils.NotificationUtils;

/**
 * Processes notification action {@link Intent}s that need to run off the main thread.
 */
public class NotificationActionIntentService extends IntentService {
    private static final String LOG_TAG = "NotifActionIS";

    // Compose actions
    public static final String ACTION_REPLY = "com.tct.mail.action.notification.REPLY";
    public static final String ACTION_REPLY_ALL = "com.tct.mail.action.notification.REPLY_ALL";
    public static final String ACTION_FORWARD = "com.tct.mail.action.notification.FORWARD";
    // Toggle actions
    public static final String ACTION_MARK_READ = "com.tct.mail.action.notification.MARK_READ";

    // Destructive actions - These just display the undo bar
    public static final String ACTION_ARCHIVE_REMOVE_LABEL =
            "com.tct.mail.action.notification.ARCHIVE";
    public static final String ACTION_DELETE = "com.tct.mail.action.notification.DELETE";

    /**
     * This action cancels the undo notification, and does not commit any changes.
     */
    public static final String ACTION_UNDO = "com.tct.mail.action.notification.UNDO";

    /**
     * This action performs the actual destructive action.
     */
    public static final String ACTION_DESTRUCT = "com.tct.mail.action.notification.DESTRUCT";
    /**
     * This action performs the actual refresh action.
     */
    public static final String ACTION_REFRESH = "com.tct.mail.action.notification.REFRESH";

    public static final String EXTRA_NOTIFICATION_ACTION =
            "com.tct.mail.extra.EXTRA_NOTIFICATION_ACTION";
    public static final String ACTION_UNDO_TIMEOUT =
            "com.tct.mail.action.notification.UNDO_TIMEOUT";

    public static final String ACTION_CALENDAR_NEVER_ASK_AGAIN = "com.tct.mail.action.notification.calendar.NEVER_ASK_AGAIN";
    public static final String ACTION_CONTACTS_NEVER_ASK_AGAIN = "com.tct.mail.action.notification.contacts.NEVER_ASK_AGAIN";
    public static final String ACTION_STORAGE_NEVER_ASK_AGAIN = "com.tct.mail.action.notification.storage.NEVER_ASK_AGAIN";   //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD

    public NotificationActionIntentService() {
        super("NotificationActionIntentService");
    }

    private static void logNotificationAction(String intentAction, NotificationAction action) {
        final String eventAction;
        final String eventLabel;

        if (ACTION_ARCHIVE_REMOVE_LABEL.equals(intentAction)) {
            eventAction = "archive_remove_label";
            eventLabel = action.getFolder().getTypeDescription();
        } else if (ACTION_DELETE.equals(intentAction)) {
            eventAction = "delete";
            eventLabel = null;
        } else {
            eventAction = intentAction;
            eventLabel = null;
        }

        Analytics.getInstance().sendEvent("notification_action", eventAction, eventLabel, 0);
    }

    /**
     * Give the chance to restore the mail's status,here we restore to queue status
     * @param context
     * @param boxId is the outbox's id
     */
    private void cleanFaildMailStatus(Context context, long boxId) {
        Cursor cursor = null;
        ContentResolver resolver = context.getContentResolver();
        ArrayList<Long> pendingSendMails = new ArrayList<Long>();
        ContentValues value = new ContentValues();
        //query the failed mails
        try {
            cursor = resolver.query(EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_WITH_STATUS_PROJECTION,
                    MessageColumns.SENDING_STATUS
                            + "=?" + " AND " + MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            Long.toString(EmailContent.Message.MAIL_IN_FAILED_STATUS),
                            Long.toString(boxId)
                    }, null);
            while (cursor.moveToNext()) {
                pendingSendMails.add(cursor.getLong(0));
            }
        } catch (Exception e) {
            LogUtils.e(LogUtils.TAG,e, "Exception happen during queue the failed mails in cleanFaildMailStatus ");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        //update the mails status
        if (pendingSendMails.size() > 0) {
            for (long id : pendingSendMails) {
                value.clear();
                Uri uri = ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI, id);
                value.put(MessageColumns.SENDING_STATUS, EmailContent.Message.MAIL_IN_QUEUE_STATUS);
                resolver.update(uri, value, null, null);
                LogUtils.d(LogUtils.TAG,"update the mail's status from FAIL to QUEUE,current message id is %d",id);
            }
        }
        pendingSendMails.clear();
    }

    /**if OUTBOX is empty,but user click retry action,avoid this.
     * @param context
     * @param boxId
     * @return
     */
    private boolean isOutboxNotEmpty(Context context, long boxId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_PROJECTION, MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                        Long.toString(boxId)
                    }, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            LogUtils.e(LogUtils.TAG,e, "Exception happen during queue the outbox mails in IsOutboxEmpty ");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
    @Override
    protected void onHandleIntent(final Intent intent) {
        final Context context = this;
        final String action = intent.getAction();
        // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
        //NOTE: handle the refresh intent.
        if (ACTION_REFRESH.equals(action)) {
            boolean cleanStatus = intent.getBooleanExtra(NotificationUtils.EXTRA_NEED_CLEAN_STATUS, false);
            long boxId = intent.getLongExtra(NotificationUtils.EXTRA_OUTBOX_ID, -1);
            //after click the action,cancel the notification.
            int notificationId = intent.getIntExtra(NotificationUtils.EXTRA_FAIL_NOTIFICATION_ID, 0);
            NotificationManager  mNotificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationId);
            if (boxId == -1) {
                LogUtils.e(
                        LOG_TAG,
                        "can't find the oubox during handle Intent ACTION_REFRESH in NotificationActionIntentService#onHandleIntent");
                return;
            }
            Uri refreshUri = intent.getData();
            if (cleanStatus && isOutboxNotEmpty(context, boxId)) {
                // 1.clean failed status
                cleanFaildMailStatus(context, boxId);
                // 2.start refresh(sync) the outbox
                context.getContentResolver().query(refreshUri, null, null, null, null);
                // 3. show the sending toast
                // Why add toast to Handler? cause the notificationActionIntentService is
                // asynchronous,so want show toast,
                // only must add toast to Main thread.
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), R.string.sending,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return;
        }
        // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
        else if(ACTION_CALENDAR_NEVER_ASK_AGAIN.equals(action)){
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.cancel(NotificationController.EXCHANGE_NEWCALENDAR_NOTIFICATION_ID);
            }
            MailPrefs.get(context).setIgnoreExchangeCalendarPermission(true);   //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
            return;
        } else if(ACTION_CONTACTS_NEVER_ASK_AGAIN.equals(action)){
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if(mNotificationManager != null) {
                mNotificationManager.cancel(NotificationController.EXCHANGE_NEWCONTACTS_NOTIFICATION_ID);
            }
            MailPrefs.get(context).setIgnoreExchangeContactPermission(true);    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
            return;
        } else if (ACTION_STORAGE_NEVER_ASK_AGAIN.equals(action)){    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if(mNotificationManager != null) {
                mNotificationManager.cancel(NotificationController.EXCHANGE_NEWSTORAGE_NOTIFICATION_ID);
            }
            MailPrefs.get(context).setIgnoreExchangeStoragePermission(true);
            //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
        }
        /*
         * Grab the alarm from the intent. Since the remote AlarmManagerService fills in the Intent
         * to add some extra data, it must unparcel the NotificationAction object. It throws a
         * ClassNotFoundException when unparcelling.
         * To avoid this, do the marshalling ourselves.
         */
        final NotificationAction notificationAction;
        final byte[] data = intent.getByteArrayExtra(EXTRA_NOTIFICATION_ACTION);
        if (data != null) {
            final Parcel in = Parcel.obtain();
            in.unmarshall(data, 0, data.length);
            in.setDataPosition(0);
            notificationAction = NotificationAction.CREATOR.createFromParcel(in,
                    NotificationAction.class.getClassLoader());
        } else {
            LogUtils.wtf(LOG_TAG, "data was null trying to unparcel the NotificationAction");
            return;
        }

        final Message message = notificationAction.getMessage();

        final ContentResolver contentResolver = getContentResolver();

        LogUtils.i(LOG_TAG, "Handling %s", action);

        logNotificationAction(action, notificationAction);

        if (notificationAction.getSource() == NotificationAction.SOURCE_REMOTE) {
            // Skip undo if the action is bridged from remote node.  This should be similar to the
            // logic after the Undo notification expires in a regular flow.
            LogUtils.d(LOG_TAG, "Canceling %s", notificationAction.getNotificationId());
            NotificationManagerCompat.from(context).cancel(notificationAction.getNotificationId());
            NotificationActionUtils.processDestructiveAction(this, notificationAction);
            NotificationActionUtils.resendNotifications(context, notificationAction.getAccount(),
                    notificationAction.getFolder());
            return;
        }

        if (ACTION_UNDO.equals(action)) {
            NotificationActionUtils.cancelUndoTimeout(context, notificationAction);
            NotificationActionUtils.cancelUndoNotification(context, notificationAction);
        } else if (ACTION_ARCHIVE_REMOVE_LABEL.equals(action) || ACTION_DELETE.equals(action)) {
            // All we need to do is switch to an Undo notification
            NotificationActionUtils.createUndoNotification(context, notificationAction);

            NotificationActionUtils.registerUndoTimeout(context, notificationAction);
        } else {
            if (ACTION_UNDO_TIMEOUT.equals(action) || ACTION_DESTRUCT.equals(action)) {
                // Process the action
                NotificationActionUtils.cancelUndoTimeout(this, notificationAction);
                NotificationActionUtils.processUndoNotification(this, notificationAction);
            } else if (ACTION_MARK_READ.equals(action)) {
                final Uri uri = message.uri;

                final ContentValues values = new ContentValues(1);
                values.put(UIProvider.MessageColumns.READ, 1);

                contentResolver.update(uri, values, null, null);
            }

            NotificationActionUtils.resendNotifications(context, notificationAction.getAccount(),
                    notificationAction.getFolder());
        }
    }
}
