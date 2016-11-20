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
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/14/2014|     Chao Zhang       |      FR 635028       |[Email]Download   */
/*           |                      |porting from(FR472914)|options to be im- */
/*           |                      |                      |plemented         */
/* ----------|----------------------|----------------------|----------------- */
/* 04/25/2014|     Chao Zhang       |      FR 631895 	   |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/**
 *===================================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== =======================================================================
 *BUGFIX-1004722  2015/05/18   zheng.zou     [REG][Monitor][Android5.0][Email]Sometimes email cannot receive and send.
 *BUGFIX-1009030  2015/06/04   Gantao     [Android5.0][Email]Attachment cannot fetch when download again.
 *BUGFIX-472634   2015/08/07   jin.dong       [Email]When the user has "Delete email from server" turned off on the device, emails on the server will still be sent to the trash folder when deleted on the device.x
 *BUGFIX-472593   2015/08/04   junwei-xu     [Email]The mail can not be deleted when "Delete email from server" is turned off.
 ====================================================================================================================
 */
package com.tct.email.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import android.text.TextUtils;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.TrafficFlags;
import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.mail.AuthenticationFailedException;
import com.tct.emailcommon.mail.Folder.OpenMode;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.mail.Part;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.service.IEmailServiceCallback;
import com.tct.emailcommon.utility.AttachmentUtilities;
import com.tct.mail.utils.LogUtils;
//[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
import com.tct.emailcommon.utility.Utility;
//[FEATURE]-Add-END by TSCD.Chao Zhang
import org.apache.james.mime4j.EOLConvertingInputStream;
//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
import com.tct.email.LegacyConversions;
import com.tct.email.NotificationController;
import com.tct.email.mail.Store;
import com.tct.email.mail.store.Pop3Store;
import com.tct.email.mail.store.Pop3Store.Pop3Folder;
import com.tct.email.mail.store.Pop3Store.Pop3Message;
import com.tct.email.provider.Utilities;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AttachmentState;
//[FEATURE]-Add-END by TSCD.chao zhang
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import android.util.Log;

public class Pop3Service extends Service {
    private static final String TAG = "Pop3Service";
    private static final int DEFAULT_SYNC_COUNT = 20;
    private static final String ACTION_CHECK_MAIL =
         "com.tct.email.intent.action.MAIL_SERVICE_WAKEUP";
    private static final String EXTRA_ACCOUNT = "com.tct.email.intent.extra.ACCOUNT";
    private static final String EXTRA_MSGID = "com.tct.email.intent.extra.MSGID";
    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 DEL_S
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
//    private static boolean  downloadflag = false;
//    private static String  downloadMsgServerId = null;
    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 DEL_E
    //[FEATURE]-Add-END by TSCD.chao zhang

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG,"Inside onStartCommand");
        final String action = intent.getAction();
        LogUtils.d(TAG,"action is " + action);
        Context context = getApplicationContext();
        if (ACTION_CHECK_MAIL.equals(action)) {
            final long accountId = intent.getLongExtra(EXTRA_ACCOUNT, -1);
            LogUtils.d(TAG,"accountId is " + accountId);
            final long inboxId = Mailbox.findMailboxOfType(context, accountId,
                Mailbox.TYPE_INBOX);
            LogUtils.d(TAG,"inboxId is " + inboxId);
            mBinder.init(context);
            mBinder.requestSync(inboxId,true,0);
        }
        return Service.START_STICKY;
    }

    /**
     * Create our EmailService implementation here.
     */
    private final EmailServiceStub mBinder = new EmailServiceStub() {
        @Override
        public void loadAttachment(final IEmailServiceCallback callback, final long accountId,
                final long attachmentId, final boolean background) throws RemoteException {
            Attachment att = Attachment.restoreAttachmentWithId(mContext, attachmentId);
            if (att == null || att.mUiState != AttachmentState.DOWNLOADING) return;
            long inboxId = Mailbox.findMailboxOfType(mContext, att.mAccountKey, Mailbox.TYPE_INBOX);
            if (inboxId == Mailbox.NO_MAILBOX) return;
            // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD_S
            // Note:for pop3,we start a network attachment downloading instead of startSync.
//            // We load attachments during a sync
//            requestSync(inboxId, true, 0);

            final long messageId = att.mMessageKey;
            final EmailContent.Message message =
                    EmailContent.Message.restoreMessageWithId(mContext, att.mMessageKey);
            if (message == null) {
                callback.loadAttachmentStatus(messageId, attachmentId,
                        EmailServiceStatus.MESSAGE_NOT_FOUND, 0);
                return;
            }

            /// M: start a network attachment downloading instead of startSync. @{
            Account account = Account.restoreAccountWithId(mContext, att.mAccountKey);
            Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, inboxId);
            if (account == null || mailbox == null) {
                // If the account/mailbox are gone, just report success; the UI handles this
                callback.loadAttachmentStatus(messageId, attachmentId,
                        EmailServiceStatus.SUCCESS, 0);
                return;
            }

            // If the message is loaded, just report that we're finished
            if (Utility.attachmentExists(mContext, att)
                    && att.mUiState == UIProvider.AttachmentState.SAVED) {
                callback.loadAttachmentStatus(messageId, attachmentId, EmailServiceStatus.SUCCESS,
                        0);
                return;
            }

            /// M: Say we're starting... @{
            callback.loadAttachmentStatus(messageId, attachmentId, EmailServiceStatus.IN_PROGRESS, 0);
            /// @}

            Pop3Folder remoteFolder = null;
            try {
                Pop3Store remoteStore = (Pop3Store) Store.getInstance(account, mContext);
                // The account might have been deleted
                if (remoteStore == null) {
                    return;
                }
                // Open the remote folder and create the remote folder if necessary
                remoteFolder = (Pop3Folder) remoteStore.getFolder(mailbox.mServerId);
                // Open the remote folder. This pre-loads certain metadata like message
                // count.
                remoteFolder.open(OpenMode.READ_WRITE);
                // Get the remote message count.
                final int remoteMessageCount = remoteFolder.getMessageCount();
                /*
                 * Get all messageIds in the mailbox.
                 * We don't necessarily need to sync all of them.
                 */
                Pop3Message[] remoteMessages = remoteFolder.getMessages(remoteMessageCount, remoteMessageCount);
                LogUtils.d(TAG, "remoteMessageCount " + remoteMessageCount);

                HashMap<String, Pop3Message> remoteUidMap = new HashMap<String, Pop3Message>();
                for (final Pop3Message remoteMessage : remoteMessages) {
                    final String uid = remoteMessage.getUid();
                    remoteUidMap.put(uid, remoteMessage);
                }

                /// M: pass in callback to report progress
                fetchAttachment(mContext, att, remoteFolder, remoteUidMap, callback);
                callback.loadAttachmentStatus(att.mMessageKey, attachmentId, EmailServiceStatus.SUCCESS, 0);
            } catch (MessagingException me) {
                LogUtils.i(TAG, me, "Error loading attachment");

                final ContentValues cv = new ContentValues(1);
                cv.put(AttachmentColumns.UI_STATE, UIProvider.AttachmentState.FAILED);
                final Uri uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, attachmentId);
                mContext.getContentResolver().update(uri, cv, null, null);
                callback.loadAttachmentStatus(0, attachmentId, EmailServiceStatus.CONNECTION_ERROR, 0);
            } finally {
                if (remoteFolder != null) {
                    remoteFolder.close(false);
                }
            }
            /// @}
         // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD_E
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        mBinder.init(this);
        return mBinder;
    }

    /**
     * Start foreground synchronization of the specified folder. This is called
     * by synchronizeMailbox or checkMail. TODO this should use ID's instead of
     * fully-restored objects
     *
     * @param account
     * @param folder
     * @param deltaMessageCount the requested change in number of messages to sync.
     * @return The status code for whether this operation succeeded.
     * @throws MessagingException
     */
    public static int synchronizeMailboxSynchronous(Context context, final Account account,
            final Mailbox folder, final int deltaMessageCount) throws MessagingException {
        TrafficStats.setThreadStatsTag(TrafficFlags.getSyncFlags(context, account));
        NotificationController nc = NotificationController.getInstance(context);
        try {
            synchronizePop3Mailbox(context, account, folder, deltaMessageCount,null);    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD
            // Clear authentication notification for this account
            nc.cancelLoginFailedNotification(account.mId);
        } catch (MessagingException e) {
            if (Logging.LOGD) {
                LogUtils.v(Logging.LOG_TAG, "synchronizeMailbox", e);
            }
            if (e instanceof AuthenticationFailedException) {
                // Generate authentication notification
                nc.showLoginFailedNotificationSynchronous(account.mId, true /* incoming */);
            }
            throw e;
        }
        // TODO: Rather than use exceptions as logic aobve, return the status and handle it
        // correctly in caller.
        return EmailServiceStatus.SUCCESS;
    }

    /**
     * Lightweight record for the first pass of message sync, where I'm just
     * seeing if the local message requires sync. Later (for messages that need
     * syncing) we'll do a full readout from the DB.
     */
    private static class LocalMessageInfo {
        private static final int COLUMN_ID = 0;
        private static final int COLUMN_FLAG_LOADED = 1;
        private static final int COLUMN_SERVER_ID = 2;
        private static final String[] PROJECTION = new String[] {
                EmailContent.RECORD_ID, MessageColumns.FLAG_LOADED, SyncColumns.SERVER_ID
        };

        final long mId;
        final int mFlagLoaded;
        final String mServerId;

        public LocalMessageInfo(Cursor c) {
            mId = c.getLong(COLUMN_ID);
            mFlagLoaded = c.getInt(COLUMN_FLAG_LOADED);
            mServerId = c.getString(COLUMN_SERVER_ID);
            // Note: mailbox key and account key not needed - they are projected
            // for the SELECT
        }
    }

    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_S
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    public static ArrayList<Pop3Message>  getDownloadReminMessage(ArrayList<Pop3Message> unsyncedMessages, String downloadMsgServerId){
        ArrayList<Pop3Message> messages =new ArrayList<Pop3Message> ();
        for (Pop3Message msg : unsyncedMessages) {
            if (downloadMsgServerId != null && downloadMsgServerId.equals(msg.getUid())) {
               messages.add(msg);
               break;
            }
        }
        return messages;
    }
    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_E

    public static int loadUnsyncedMessage(final Context context,long msgId) {
        try {
             EmailContent.Message localMessage = EmailContent.Message.restoreMessageWithId(context,msgId);
             com.tct.emailcommon.mail.Message unsyncedMessage = LegacyConversions.makeMessage(context,localMessage);
             Account account = Account.getAccountForMessageId(context,msgId);
             Mailbox  toMailbox = Mailbox.getMailboxForMessageId(context, msgId);
             if (account == null || toMailbox == null) {
                return 0;
             }
             Pop3Store remoteStore = (Pop3Store)Store.getInstance(account,context);
             // The account might have been deleted
             if (remoteStore == null)
             return  0;
            //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_S
             String downloadMsgServerId = unsyncedMessage.getUid();
             synchronizePop3Mailbox(context,account,toMailbox,0,downloadMsgServerId);
            //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_E
        } catch (MessagingException e){
             return  0;
        }
        return 1;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    /**
     * Load the structure and body of messages not yet synced
     *
     * @param account the account we're syncing
     * @param remoteFolder the (open) Folder we're working on
     * @param unsyncedMessages an array of Message's we've got headers for
     * @param toMailbox the destination mailbox we're syncing
     * @throws MessagingException
     */
    static void loadUnsyncedMessages(final Context context, final Account account,
            Pop3Folder remoteFolder, ArrayList<Pop3Message> unsyncedMessages,
            final Mailbox toMailbox, String downloadMsgServerId) throws MessagingException {     //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD

        if (MailActivityEmail.DEBUG) {
            LogUtils.d(TAG, "Loading " + unsyncedMessages.size() + " unsynced messages");
        }
       //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
       boolean mDownloadReminFlag = remoteFolder.getDownloadReminFlag();
       if (mDownloadReminFlag){
          unsyncedMessages = getDownloadReminMessage(unsyncedMessages, downloadMsgServerId);   //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD
       }
        try {
            int cnt = unsyncedMessages.size();
            //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
            if (unsyncedMessages == null || cnt == 0) {
               return;
            }
            int limitedSize = account.getDownloadOptions();
            //[FEATURE]-Add-END by TSCD.Chao Zhang
            if (mDownloadReminFlag) {
               limitedSize = Utility.ENTIRE_MAIL;
            }
            //[FEATURE]-Mod-END by TSCD.Chao Zhang
            // They are in most recent to least recent order, process them that way.
            for (int i = 0; i < cnt; i++) {
                final Pop3Message message = unsyncedMessages.get(i);
                //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
                if(limitedSize == Utility.ENTIRE_MAIL) {
                    //[BUGFIX]-Mod-BEGIN by TSCD.chao zhang,07/23/2014,Fix issues with pop3 head only function(such as impact attachment,mail receive,load more mails).
                    //because we have downloadRemain function,but the system have default load more function,
                    //so here  fetch entire messages to avoid it.
                    //remoteFolder.fetchBody(message, Pop3Store.FETCH_BODY_SANE_SUGGESTED_SIZE / 76,
                    //   null);
                    remoteFolder.fetchBody(message, -1,null);
                    //[BUGFIX]-Mod-END by TSCD.Chao Zhang
                } else {
                remoteFolder.fetchBody(message, 0,null);
                }
                //[FEATURE]-Add-END by TSCD.Chao Zhang
                int flag = EmailContent.Message.FLAG_LOADED_COMPLETE;
                //[FEATURE]-MOD-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                if (!message.isComplete() || (limitedSize != Utility.ENTIRE_MAIL)) {
                //[FEATURE]-MOD-END by TSCD.Chao Zhang
                    // TODO: when the message is not complete, this should mark the message as
                    // partial.  When that change is made, we need to make sure that:
                    // 1) Partial messages are shown in the conversation list
                    // 2) We are able to download the rest of the message/attachment when the
                    //    user requests it.
                     flag = EmailContent.Message.FLAG_LOADED_PARTIAL;
                }
                if (MailActivityEmail.DEBUG) {
                    LogUtils.d(TAG, "Message is " + (message.isComplete() ? "" : "NOT ")
                            + "complete");
                }
                // If message is incomplete, create a "fake" attachment
                //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                Utilities.setPop3Call(true);
                //[FEATURE]-Add-END by TSCD.chao zhang
                Utilities.copyOneMessageToProvider(context, message, account, toMailbox, flag);
            }
        } catch (IOException e) {
            throw new MessagingException(MessagingException.IOERROR);
        }
    }

    private static class FetchCallback implements EOLConvertingInputStream.Callback {
        private final ContentResolver mResolver;
        private final Uri mAttachmentUri;
        private final ContentValues mContentValues = new ContentValues();

        FetchCallback(ContentResolver resolver, Uri attachmentUri) {
            mResolver = resolver;
            mAttachmentUri = attachmentUri;
        }

        @Override
        public void report(int bytesRead) {
            mContentValues.put(AttachmentColumns.UI_DOWNLOADED_SIZE, bytesRead);
            mResolver.update(mAttachmentUri, mContentValues, null, null);
        }
    }

    /**
     * Synchronizer
     *
     * @param account the account to sync
     * @param mailbox the mailbox to sync
     * @param deltaMessageCount the requested change to number of messages to sync
     * @throws MessagingException
     */
    private synchronized static void synchronizePop3Mailbox(final Context context, final Account account,
            final Mailbox mailbox, final int deltaMessageCount, String downloadMsgServerId) throws MessagingException {    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD
        // TODO Break this into smaller pieces
        ContentResolver resolver = context.getContentResolver();

        // We only sync Inbox
        if (mailbox.mType != Mailbox.TYPE_INBOX) {
            return;
        }

        // Get the message list from EmailProvider and create an index of the uids

        Cursor localUidCursor = null;
        HashMap<String, LocalMessageInfo> localMessageMap = new HashMap<String, LocalMessageInfo>();

        try {
            localUidCursor = resolver.query(
                    EmailContent.Message.CONTENT_URI,
                    LocalMessageInfo.PROJECTION,
                    MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            String.valueOf(mailbox.mId)
                    },
                    null);
            while (localUidCursor.moveToNext()) {
                LocalMessageInfo info = new LocalMessageInfo(localUidCursor);
                localMessageMap.put(info.mServerId, info);
            }
        } finally {
            if (localUidCursor != null) {
                localUidCursor.close();
            }
        }

        // Open the remote folder and create the remote folder if necessary

        Pop3Store remoteStore = (Pop3Store)Store.getInstance(account, context);
        // The account might have been deleted
        if (remoteStore == null)
            return;
        Pop3Folder remoteFolder = (Pop3Folder)remoteStore.getFolder(mailbox.mServerId);

        // Open the remote folder. This pre-loads certain metadata like message
        // count.
        remoteFolder.open(OpenMode.READ_WRITE);

        String[] accountIdArgs = new String[] { Long.toString(account.mId) };
        long trashMailboxId = Mailbox.findMailboxOfType(context, account.mId, Mailbox.TYPE_TRASH);
        //TS: jin.dong 2015-08-07 EMAIL BUGFIX-472634(ALM), MOD_S
        // Note:just when user set "Delete from server" option, we operate remove message on server
        // otherwise, don't operate this action
        if (account.getDeletePolicy() == Account.DELETE_POLICY_ON_DELETE) {
            Cursor updates = resolver.query(
                    EmailContent.Message.UPDATED_CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_PROJECTION,
                    EmailContent.MessageColumns.ACCOUNT_KEY + "=?", accountIdArgs,
                    null);
            try {
                // loop through messages marked as deleted
                while (updates.moveToNext()) {
                    long id = updates.getLong(Message.ID_COLUMNS_ID_COLUMN);
                    EmailContent.Message currentMsg =
                            EmailContent.Message.restoreMessageWithId(context, id);
                    if (currentMsg.mMailboxKey == trashMailboxId) {
                        // Delete this on the server
                        Pop3Message popMessage =
                                (Pop3Message) remoteFolder.getMessage(currentMsg.mServerId);
                        if (popMessage != null) {
                            remoteFolder.deleteMessage(popMessage);
                        }
                    }
                    // Finally, delete the update
                    Uri uri = ContentUris.withAppendedId(EmailContent.Message.UPDATED_CONTENT_URI, id);
                    context.getContentResolver().delete(uri, null, null);
                }
            } finally {
                updates.close();
            }
        }
        //TS: jin.dong 2015-08-07 EMAIL BUGFIX-472634(ALM), MOD_E

        // Get the remote message count.
        final int remoteMessageCount = remoteFolder.getMessageCount();

        // Save the folder message count.
        mailbox.updateMessageCount(context, remoteMessageCount);

        // Create a list of messages to download
        Pop3Message[] remoteMessages = new Pop3Message[0];
        final ArrayList<Pop3Message> unsyncedMessages = new ArrayList<Pop3Message>();
        HashMap<String, Pop3Message> remoteUidMap = new HashMap<String, Pop3Message>();     //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 ADD
        boolean  downloadflag = !TextUtils.isEmpty(downloadMsgServerId);

        if (remoteMessageCount > 0) {
            /*
             * Get all messageIds in the mailbox.
             * We don't necessarily need to sync all of them.
             */
            remoteMessages = remoteFolder.getMessages(remoteMessageCount, remoteMessageCount);
            LogUtils.d(Logging.LOG_TAG, "remoteMessageCount " + remoteMessageCount);

            /*
             * TODO: It would be nicer if the default sync window were time based rather than
             * count based, but POP3 does not support time based queries, and the UIDL command
             * does not report timestamps. To handle this, we would need to load a block of
             * Ids, sync those messages to get the timestamps, and then load more Ids until we
             * have filled out our window.
             */
            int count = 0;
            int countNeeded = DEFAULT_SYNC_COUNT;
            for (final Pop3Message message : remoteMessages) {
                final String uid = message.getUid();
                remoteUidMap.put(uid, message);
            }

            /*
             * Figure out which messages we need to sync. Start at the most recent ones, and keep
             * going until we hit one of four end conditions:
             * 1. We currently have zero local messages. In this case, we will sync the most recent
             * DEFAULT_SYNC_COUNT, then stop.
             * 2. We have some local messages, and after encountering them, we find some older
             * messages that do not yet exist locally. In this case, we will load whichever came
             * before the ones we already had locally, and also deltaMessageCount additional
             * older messages.
             * 3. We have some local messages, but after examining the most recent
             * DEFAULT_SYNC_COUNT remote messages, we still have not encountered any that exist
             * locally. In this case, we'll stop adding new messages to sync, leaving a gap between
             * the ones we've just loaded and the ones we already had.
             * 4. We examine all of the remote messages before running into any of our count
             * limitations.
             */
            for (final Pop3Message message : remoteMessages) {
                final String uid = message.getUid();
                final LocalMessageInfo localMessage = localMessageMap.get(uid);
                //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                if (localMessage == null ||
                   (localMessage.mFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL) ||
                   (localMessage.mFlagLoaded != EmailContent.Message.FLAG_LOADED_COMPLETE)) {
                //[FEATURE]-Add-END by TSCD.chao zhang
                    count++;
                } else {
                    // We have found a message that already exists locally. We may or may not
                    // need to keep looking, depending on what deltaMessageCount is.
                    LogUtils.d(Logging.LOG_TAG, "found a local message, need " +
                            deltaMessageCount + " more remote messages");
                    countNeeded = deltaMessageCount;
                    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                    //here we should make shure that the msg we want to sync is in unsynclist.
                    if (downloadflag) {
                       countNeeded = remoteMessageCount > DEFAULT_SYNC_COUNT? remoteMessageCount:DEFAULT_SYNC_COUNT;
                    }
                    //[FEATURE]-Add-END by TSCD.chao zhang
                    count = 0;
                }

                // localMessage == null -> message has never been created (not even headers)
                // mFlagLoaded != FLAG_LOADED_COMPLETE -> message failed to sync completely
                //[FEATURE]-Add-MOD by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                if (localMessage == null ||
                           (localMessage.mFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL)
                           || (localMessage.mFlagLoaded != EmailContent.Message.FLAG_LOADED_COMPLETE)) {
                 //[FEATURE]-Add-MO by TSCD.chao zhang
                    LogUtils.d(Logging.LOG_TAG, "need to sync " + uid);
                    unsyncedMessages.add(message);
                } else {
                    LogUtils.d(Logging.LOG_TAG, "don't need to sync " + uid);
                }

                if (count >= countNeeded) {
                    LogUtils.d(Logging.LOG_TAG, "loaded " + count + " messages, stopping");
                    break;
                }
            }
        } else {
            if (MailActivityEmail.DEBUG) {
                LogUtils.d(TAG, "*** Message count is zero??");
            }
            remoteFolder.close(false);
            return;
        }
        //TS: jin.dong 2015-08-07 EMAIL BUGFIX-472634(ALM), ADD_S
        //Note: if user set never delete email on server when delete email on device.
        //we must not sync these mails which in trash box.
        //TS: junwei-xu 2015-08-04 EMAIL BUGFIX-472593, ADD_S
        //Note: if user set never delete email on server when delete email on device.
        //we must not sync these mails which in trash box.
        if ((account.getDeletePolicy() == Account.DELETE_POLICY_NEVER) && unsyncedMessages.size() > 0) {
            Cursor localTrashUidCursor = null;
            ArrayList<Pop3Message> localTrashMessageList = new ArrayList<Pop3Message>();
            try {
                localTrashUidCursor = resolver.query(
                        EmailContent.Message.CONTENT_URI,
                        new String[]{SyncColumns.SERVER_ID},
                        MessageColumns.MAILBOX_KEY + "=?",
                        new String[]{String.valueOf(trashMailboxId)},
                        null);
                while (localTrashUidCursor.moveToNext()) {
                    for (int i = 0; i < unsyncedMessages.size(); i++) {
                        String serverId = localTrashUidCursor.getString(0);
                        if (!TextUtils.isEmpty(serverId) && serverId.equals(unsyncedMessages.get(i).getUid())) {
                            localTrashMessageList.add(unsyncedMessages.get(i));
                        }
                    }
                }
            } finally {
                if (localTrashUidCursor != null) {
                    localTrashUidCursor.close();
                }
            }

            unsyncedMessages.removeAll(localTrashMessageList);
            localTrashMessageList = null;
        }
        //TS: jin.dong 2015-08-07 EMAIL BUGFIX-472634(ALM), ADD_E
        //TS: junwei-xu 2015-08-04 EMAIL BUGFIX-472593, ADD_E
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        remoteFolder.setDownloadReminFlag(downloadflag);    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD
        int limitedSize = downloadflag ? Utility.ENTIRE_MAIL: account.getDownloadOptions();
        //[FEATURE]-Add-END by TSCD.chao zhang
        //[FEATURE]-MOD-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        if (limitedSize == Utility.ENTIRE_MAIL) {
             // Get "attachments" to be loaded
             Cursor c = resolver.query(Attachment.CONTENT_URI, Attachment.CONTENT_PROJECTION,
                     AttachmentColumns.ACCOUNT_KEY + "=? AND " +
                             AttachmentColumns.UI_STATE + "=" + AttachmentState.DOWNLOADING,
                     new String[] {Long.toString(account.mId)}, null);
             try {
                 final ContentValues values = new ContentValues();
                 while (c.moveToNext()) {
                     values.put(AttachmentColumns.UI_STATE, UIProvider.AttachmentState.SAVED);
                     Attachment att = new Attachment();
                     att.restore(c);
                     Message msg = Message.restoreMessageWithId(context, att.mMessageKey);
                     if (msg == null || (msg.mFlagLoaded == Message.FLAG_LOADED_COMPLETE)) {
                     values.put(AttachmentColumns.UI_DOWNLOADED_SIZE, att.mSize);
                     resolver.update(ContentUris.withAppendedId(Attachment.CONTENT_URI, att.mId),
                             values, null, null);
                     continue;
                     } else {
                        String uid = msg.mServerId;
                        if (downloadflag && downloadMsgServerId != null && downloadMsgServerId.equals(uid)) {
                           downloadMsgServerId=null;
                           Pop3Message popMessage = remoteUidMap.get(uid);
                           if (popMessage != null) {
                              Uri attUri = ContentUris.withAppendedId(Attachment.CONTENT_URI, att.mId);
                              try {
                                  remoteFolder.fetchBody(popMessage, -1,
                                      new FetchCallback(resolver, attUri));
                              } catch (IOException e) {
                                 throw new MessagingException(MessagingException.IOERROR);
                        }

                        // Say we've downloaded the attachment
                        values.put(AttachmentColumns.UI_STATE, AttachmentState.SAVED);
                          resolver.update(attUri, values, null, null);

                        int flag = EmailContent.Message.FLAG_LOADED_COMPLETE;
                        if (!popMessage.isComplete()) {
                           LogUtils.e(TAG, "How is this possible?");
                        }
                           Utilities.setPop3Call(true);
                           Utilities.copyOneMessageToProvider(
                                 context, popMessage, account, mailbox, flag);
                          // Get rid of the temporary attachment
                          resolver.delete(attUri, null, null);

                          } else {
                        // TODO: Should we mark this attachment as failed so we don't
                          // keep trying to download?
                          LogUtils.e(TAG, "Could not find message for attachment " + uid);
                        }
                          break;
                   }
                      Pop3Message popMessage = remoteUidMap.get(uid);
                      if (popMessage != null) {
                          Uri attUri = ContentUris.withAppendedId(Attachment.CONTENT_URI, att.mId);
                          try {
                              remoteFolder.fetchBody(popMessage, -1,
                                      new FetchCallback(resolver, attUri));
                          } catch (IOException e) {
                              throw new MessagingException(MessagingException.IOERROR);
                          }

                         // Say we've downloaded the attachment
                          values.put(AttachmentColumns.UI_STATE, AttachmentState.SAVED);
                          resolver.update(attUri, values, null, null);

                          int flag = EmailContent.Message.FLAG_LOADED_COMPLETE;
                          if (!popMessage.isComplete()) {
                              LogUtils.e(TAG, "How is this possible?");
                          }
                          Utilities.setPop3Call(true);
                          Utilities.copyOneMessageToProvider(
                                  context, popMessage, account, mailbox, flag);
                          // Get rid of the temporary attachment
                          resolver.delete(attUri, null, null);

                      } else {
                        // TODO: Should we mark this attachment as failed so we don't
                          // keep trying to download?
                          LogUtils.e(TAG, "Could not find message for attachment " + uid);
                      }
                  }
                }
          } finally {
              c.close();
           }
        }
        //[FEATURE]-MOD-END by TSCD.chao zhang
        // Remove any messages that are in the local store but no longer on the remote store.
        HashSet<String> localUidsToDelete = new HashSet<String>(localMessageMap.keySet());
        localUidsToDelete.removeAll(remoteUidMap.keySet());
        for (String uidToDelete : localUidsToDelete) {
            LogUtils.d(Logging.LOG_TAG, "need to delete " + uidToDelete);
            LocalMessageInfo infoToDelete = localMessageMap.get(uidToDelete);

            // Delete associated data (attachment files)
            // Attachment & Body records are auto-deleted when we delete the
            // Message record
            AttachmentUtilities.deleteAllAttachmentFiles(context, account.mId,
                    infoToDelete.mId);

            // Delete the message itself
            Uri uriToDelete = ContentUris.withAppendedId(
                    EmailContent.Message.CONTENT_URI, infoToDelete.mId);
            resolver.delete(uriToDelete, null, null);

            // Delete extra rows (e.g. synced or deleted)
            Uri updateRowToDelete = ContentUris.withAppendedId(
                    EmailContent.Message.UPDATED_CONTENT_URI, infoToDelete.mId);
            resolver.delete(updateRowToDelete, null, null);
            Uri deleteRowToDelete = ContentUris.withAppendedId(
                    EmailContent.Message.DELETED_CONTENT_URI, infoToDelete.mId);
            resolver.delete(deleteRowToDelete, null, null);
        }

        LogUtils.d(TAG, "loadUnsynchedMessages " + unsyncedMessages.size());
        // Load messages we need to sync
        loadUnsyncedMessages(context, account, remoteFolder, unsyncedMessages, mailbox, downloadMsgServerId); //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD

        // Clean up and report results
        remoteFolder.close(false);
    }

    // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 ADD_S
    /**
     * M: This is a code refactor of attachment fetching, we move these code from synchronizePop3Mailbox
     * to a stand alone function. Now, we do this in loadAttachment called by attachment download service.
     *
     * @param context
     * @param att
     * @param account
     * @param mailbox
     * @param remoteFolder
     * @param remoteUidMap
     * @param callback
     * @throws MessagingException
     */
    private static void fetchAttachment(Context context, Attachment att, Pop3Folder remoteFolder,
            HashMap<String, Pop3Message> remoteUidMap, final IEmailServiceCallback callback)
                    throws MessagingException, RemoteException {
        Message msg = Message.restoreMessageWithId(context, att.mMessageKey);

        String uid = msg.mServerId;
        Pop3Message popMessage = remoteUidMap.get(uid);
        if (popMessage != null) {
            LogUtils.d(TAG, " Pop3Service : synchronizePop3Mailbox : fetchAttachment : %d : popMessage : %d",
                    att.mId, att.mMessageKey);
            Uri attUri = ContentUris.withAppendedId(Attachment.CONTENT_URI, att.mId);
            try {
                remoteFolder.fetchBody(popMessage, -1, new FetchCallback(context.getContentResolver(), attUri));
            } catch (IOException e) {
                throw new MessagingException(MessagingException.IOERROR);
            }

            /// M: Until now, we have already fetch attachment from server, then we need update db @{
            callback.loadAttachmentStatus(msg.mId, att.mId, EmailServiceStatus.IN_PROGRESS, 100);
            /// @}

            if (!popMessage.isComplete()) {
                LogUtils.e(TAG, "How is this possible?");
            }

            // For pop message the location was the attachment's index in all attachments,
            // so get current attachment's location.
            int location;
            try {
                location = Integer.valueOf(att.mLocation);
            } catch (NumberFormatException e) {
                location = -1;
                LogUtils.e(TAG, "invalid location!");
            }
            // Cause we just create it by partId in LegacyConversions.updateAttachments.
            int partIndex = location;

            // Now process attachments
            ArrayList<Part> viewables = new ArrayList<Part>();
            ArrayList<Part> attachments = new ArrayList<Part>();
            MimeUtility.collectParts(popMessage, viewables, attachments);
            // Now, only save the user specified attachment.
            // TODO : need update all attachments to save data downloading resource...?
            // LegacyConversions.updateAttachments(context, msg, attachments);
            ///  Make sure partIndex is valid @{
            if ((attachments.size()+viewables.size()) > partIndex && partIndex >= 0) {
                // Save the attachment to wherever it's going

                // If it's usual attachment
                if (partIndex < attachments.size()) {
                    AttachmentUtilities.saveAttachment(context, attachments.get(partIndex).getBody().getInputStream(),
                            att);
                }
                // it's inline attachment
                else {
                    AttachmentUtilities.saveAttachment(context, viewables.get(partIndex-attachments.size()).
                            getBody().getInputStream(), att);
                }

                // Say we've downloaded the attachment
                final ContentValues values = new ContentValues(1);
                values.put(AttachmentColumns.UI_STATE, AttachmentState.SAVED);
                context.getContentResolver().update(attUri, values, null, null);
            } else {
                LogUtils.e(TAG, "fetchAttachment : could not save attachment[id=%s],  due to invalid location"
                        + "(partId %d with attachment size %d)", att.mId, partIndex, attachments.size());
            }
            /// @}
        } else {
            // TODO: Should we mark this attachment as failed so we don't
            // keep trying to download?
            LogUtils.e(TAG, "Could not find message for attachment " + uid);
        }
    }
    // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 ADD_E
}
