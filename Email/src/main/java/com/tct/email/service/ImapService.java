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
/* 06/09/2014|     Zhenhua Fan      |      FR 622609       |[Orange][Android  */
/*           |                      |                      |guidelines]IMAP   */
/*           |                      |                      |support           */
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
*BUGFIX-877623  2014/12/24   wenggangjin     [Email]Email display internal error
*BUGFIX-883410  2015/01/13   xiaolin.li    [Email]Search dysfunction;
*BUGFIX-933858  2015/03/03   zheng.zou     [Email]Can not stop search email when no network
*BUGFIX-933858  2015/03/05   zheng.zou     [Email]Can not stop search email when no network
*BUGFIX-944708  2015/03/18   zheng.zou     [Email]Can not stop search email when no network
*BUGFIX-1004722  2015/05/18   zheng.zou     [REG][Monitor][Android5.0][Email]Sometimes email cannot receive and send.
*BUGFIX-1001398  2015/05/21   zheng.zou     [Monitor][Email]The mail which have read display unread email after refresh list
 *BUGFIX-1087027  2015/10/19   kaifeng.lu    [Email]Download .ics file appear error but the notification display download complete
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
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.tct.email.R;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.TrafficFlags;
import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.mail.AuthenticationFailedException;
import com.tct.emailcommon.mail.FetchProfile;
import com.tct.emailcommon.mail.Flag;
import com.tct.emailcommon.mail.Folder;
import com.tct.emailcommon.mail.Folder.FolderType;
import com.tct.emailcommon.mail.Folder.MessageRetrievalListener;
import com.tct.emailcommon.mail.Folder.MessageUpdateCallbacks;
import com.tct.emailcommon.mail.Folder.OpenMode;
import com.tct.emailcommon.mail.Message;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.mail.Part;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.MailboxColumns;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.service.SearchParams;
import com.tct.emailcommon.service.SyncWindow;
import com.tct.emailcommon.utility.AttachmentUtilities;
import com.tct.mail.utils.LogUtils;

//[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.utility.Utility;
//[FEATURE]-Add-END by TSCD.Chao Zhang
//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
//[FEATURE]-Add-END by TSCD.chao zhang
//[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
import com.tct.emailcommon.mail.Address;
import com.tct.email.EmailApplication;
import com.tct.email.LegacyConversions;
import com.tct.email.NotificationController;
import com.tct.email.activity.UiUtilities;
import com.tct.email.mail.Store;
import com.tct.email.mail.store.ImapFolder;
import com.tct.email.mail.store.ImapStore;
import com.tct.email.mail.store.imap.ImapConstants;
import com.tct.email.provider.Utilities;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.providers.UIProvider;
//[FEATURE]-ADD-END by TSNJ.wei huang
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.os.Bundle;

public class ImapService extends Service {
    // TODO get these from configurations or settings.
    private static final long QUICK_SYNC_WINDOW_MILLIS = DateUtils.DAY_IN_MILLIS;
    private static final long FULL_SYNC_WINDOW_MILLIS = 7 * DateUtils.DAY_IN_MILLIS;
    private static final long FULL_SYNC_INTERVAL_MILLIS = 4 * DateUtils.HOUR_IN_MILLIS;
    private static final String TAG = "ImapService";

    // The maximum number of messages to fetch in a single command.
    private static final int MAX_MESSAGES_TO_FETCH = 500;
    private static final int MINIMUM_MESSAGES_TO_SYNC = 10;
    private static final int LOAD_MORE_MIN_INCREMENT = 10;
    private static final int LOAD_MORE_MAX_INCREMENT = 20;
    private static final long INITIAL_WINDOW_SIZE_INCREASE = 24 * 60 * 60 * 1000;

    private static final int INIT_MESSAGE_TO_FETCH = 100; //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
    private static final int NUM_FETCH_MESSAGE_INCREMENT = 25; //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807

    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 DEL_S
//    private static final int MAX_SMALL_MESSAGE_SIZE = (25 * 1024);
    //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 DEL_E
    //[FEATURE]-Add-END by TSCD.Chao Zhang

    private static final Flag[] FLAG_LIST_SEEN = new Flag[] { Flag.SEEN };
    private static final Flag[] FLAG_LIST_FLAGGED = new Flag[] { Flag.FLAGGED };
    private static final Flag[] FLAG_LIST_ANSWERED = new Flag[] { Flag.ANSWERED };

    /**
     * Simple cache for last search result mailbox by account and serverId, since the most common
     * case will be repeated use of the same mailbox
     */
    private static long mLastSearchAccountKey = Account.NO_ACCOUNT;
    private static String mLastSearchServerId = null;
    private static Mailbox mLastSearchRemoteMailbox = null;

    /**
     * Cache search results by account; this allows for "load more" support without having to
     * redo the search (which can be quite slow).  SortableMessage is a smallish class, so memory
     * shouldn't be an issue
     */
    private static final HashMap<Long, SortableMessage[]> sSearchResults =
            new HashMap<Long, SortableMessage[]>();

    /**
     * We write this into the serverId field of messages that will never be upsynced.
     */
    private static final String LOCAL_SERVERID_PREFIX = "Local-";
    private static final String ACTION_CHECK_MAIL =
        "com.tct.email.intent.action.MAIL_SERVICE_WAKEUP";
    private static final String EXTRA_ACCOUNT = "com.tct.email.intent.extra.ACCOUNT";
    private static final String ACTION_DELETE_MESSAGE =
        "com.tct.email.intent.action.MAIL_SERVICE_DELETE_MESSAGE";
    private static final String ACTION_MOVE_MESSAGE =
        "com.tct.email.intent.action.MAIL_SERVICE_MOVE_MESSAGE";
    private static final String ACTION_MESSAGE_READ =
        "com.tct.email.intent.action.MAIL_SERVICE_MESSAGE_READ";
    private static final String ACTION_SEND_PENDING_MAIL =
        "com.tct.email.intent.action.MAIL_SERVICE_SEND_PENDING";
    private static final String EXTRA_MESSAGE_ID = "com.tct.email.intent.extra.MESSAGE_ID";
    private static final String EXTRA_MESSAGE_INFO = "com.tct.email.intent.extra.MESSAGE_INFO";

    private static String sMessageDecodeErrorString;

    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 DEL_S
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
//    private static boolean  downloadflag = false;
//    private static String  downloadMsgUid = null;
    //[FEATURE]-Add-END by TSCD.chao zhang
    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 DEL_E

    /**
     * Used in ImapFolder for base64 errors. Cached here because ImapFolder does not have access
     * to a Context object.
     * @return Error string or empty string
     */
    public static String getMessageDecodeErrorString() {
        return sMessageDecodeErrorString == null ? "" : sMessageDecodeErrorString;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sMessageDecodeErrorString = getString(R.string.message_decode_error);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String action = intent.getAction();
        if (Logging.LOGD) {
            LogUtils.d(Logging.LOG_TAG, "Action: ", action);
        }
        final long accountId = intent.getLongExtra(EXTRA_ACCOUNT, -1);
        Context context = getApplicationContext();
        if (ACTION_CHECK_MAIL.equals(action)) {
            final long inboxId = Mailbox.findMailboxOfType(context, accountId,
                Mailbox.TYPE_INBOX);
            if (Logging.LOGD) {
               LogUtils.d(Logging.LOG_TAG,"accountId is " + accountId);
               LogUtils.d(Logging.LOG_TAG,"inboxId is " + inboxId);
            }
            if (accountId <= -1 || inboxId <= -1 ){
               return START_NOT_STICKY;
            }
            mBinder.init(context);
            mBinder.requestSync(inboxId,true,0);
        } else if (ACTION_DELETE_MESSAGE.equals(action)) {
            final long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
            if (Logging.LOGD) {
                LogUtils.d(Logging.LOG_TAG, "action: Delete Message mail");
                LogUtils.d(Logging.LOG_TAG, "action: delmsg "+messageId);
            }
            if (accountId <= -1 || messageId <= -1 ){
               return START_NOT_STICKY;
            }
            Store remoteStore = null;
            try {
                remoteStore = Store.getInstance(Account.getAccountForMessageId(context, messageId),
                    context);
                mBinder.init(context);
                mBinder.deleteMessage(messageId);
                processPendingActionsSynchronous(context,
                   Account.getAccountForMessageId(context, messageId),remoteStore,true);
            } catch (Exception e){
                LogUtils.d(Logging.LOG_TAG,"RemoteException " +e);
            }
        } else if (ACTION_MESSAGE_READ.equals(action)) {
            final long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
            final int flagRead = intent.getIntExtra(EXTRA_MESSAGE_INFO, 0);
            if (Logging.LOGD) {
                LogUtils.d(Logging.LOG_TAG, "action: Message Mark Read or UnRead ");
                LogUtils.d(Logging.LOG_TAG, "action: delmsg "+messageId);
            }
            if (accountId <= -1 || messageId <= -1 ) {
                return START_NOT_STICKY;
            }
            Store remoteStore = null;
            try {
               mBinder.init(context);
               mBinder.setMessageRead(messageId, (flagRead == 1)? true:false);
               remoteStore = Store.getInstance(Account.getAccountForMessageId(context, messageId),
                                           context);
               processPendingActionsSynchronous(context,
                  Account.getAccountForMessageId(context, messageId),remoteStore,true);
            } catch (Exception e){
               LogUtils.d(Logging.LOG_TAG,"RemoteException " +e);
            }
        } else if (ACTION_MOVE_MESSAGE.equals(action)) {
            final long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
            final int  mailboxType = intent.getIntExtra(EXTRA_MESSAGE_INFO, Mailbox.TYPE_INBOX);
            final long mailboxId = Mailbox.findMailboxOfType(context, accountId, mailboxType);
            if (Logging.LOGD) {
                LogUtils.d(Logging.LOG_TAG, "action:  Move Message mail");
                LogUtils.d(Logging.LOG_TAG, "action: movemsg "+ messageId +
                "mailbox: " +mailboxType + "accountId: "+accountId + "mailboxId: " + mailboxId);
            }
            if (accountId <= -1 || messageId <= -1 || mailboxId <= -1){
                return START_NOT_STICKY;
            }
            Store remoteStore = null;
            try {
                mBinder.init(context);
                mBinder.MoveMessages(messageId, mailboxId);
                remoteStore = Store.getInstance(Account.getAccountForMessageId(context, messageId),
                   context);
                processPendingActionsSynchronous(context,
                    Account.getAccountForMessageId(context, messageId),remoteStore, true);
            } catch (Exception e){
               LogUtils.d(Logging.LOG_TAG,"RemoteException " +e);
            }
        } else if (ACTION_SEND_PENDING_MAIL.equals(action)) {
            if (Logging.LOGD) {
                LogUtils.d(Logging.LOG_TAG, "action: Send Pending Mail "+accountId);
            }
            if (accountId <= -1 ) {
                 return START_NOT_STICKY;
            }
            try {
                mBinder.init(context);
                mBinder.sendMail(accountId);
            } catch (Exception e) {
               LogUtils.e(Logging.LOG_TAG,"RemoteException " +e);
            }
        }

        return Service.START_STICKY;
    }

    /**
     * Create our EmailService implementation here.
     */
    private final EmailServiceStub mBinder = new EmailServiceStub() {
        @Override
        public int searchMessages(long accountId, SearchParams searchParams, long destMailboxId) {
            try {
                return searchMailboxImpl(getApplicationContext(), accountId, searchParams,
                        destMailboxId);
            } catch (MessagingException e) {
                //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 Mod_S
                final int type = e.getExceptionType();
                int lastSyncResult;
                switch(type) {
                    case MessagingException.IOERROR:
                        lastSyncResult =  UIProvider.LastSyncResult.CONNECTION_ERROR;
                        break;
                    case MessagingException.AUTHENTICATION_FAILED:
                        lastSyncResult =  UIProvider.LastSyncResult.AUTH_ERROR;
                        break;
                    case MessagingException.SERVER_ERROR:
                        lastSyncResult =  UIProvider.LastSyncResult.SERVER_ERROR;
                        break;

                    default:
                        lastSyncResult =  UIProvider.LastSyncResult.INTERNAL_ERROR;
                }
                //TS: zheng.zou 2015-03-05 EMAIL BUGFIX_933858 ADD_S
                // Tell UI that we're done loading messages
                e.printStackTrace();
                final Mailbox destMailbox = Mailbox.restoreMailboxWithId(getApplicationContext(), destMailboxId);
                if (destMailbox != null) {
                    final ContentValues statusValues = new ContentValues(2);
                    statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
                    statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
                    statusValues.put(Mailbox.UI_LAST_SYNC_RESULT, lastSyncResult);
                    destMailbox.update(getApplicationContext(), statusValues);
                }
                //TS: zheng.zou 2015-03-05 EMAIL BUGFIX_933858 ADD_E
                //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 Mod_E
            }
            return 0;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        mBinder.init(this);
        return mBinder;
    }

    /**
     * Start foreground synchronization of the specified folder. This is called by
     * synchronizeMailbox or checkMail.
     * TODO this should use ID's instead of fully-restored objects
     * @return The status code for whether this operation succeeded.
     * @throws MessagingException
     */
    public static synchronized int synchronizeMailboxSynchronous(Context context,
            final Account account, final Mailbox folder, final boolean loadMore,
            final boolean uiRefresh) throws MessagingException {
        TrafficStats.setThreadStatsTag(TrafficFlags.getSyncFlags(context, account));
        NotificationController nc = NotificationController.getInstance(context);
        Store remoteStore = null;
        try {
            remoteStore = Store.getInstance(account, context);
            processPendingActionsSynchronous(context, account, remoteStore, uiRefresh);
            synchronizeMailboxGeneric(context, account, remoteStore, folder, loadMore, uiRefresh,null);    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD
            // Clear authentication notification for this account
            nc.cancelLoginFailedNotification(account.mId);
        } catch (MessagingException e) {
            if (Logging.LOGD) {
                LogUtils.d(Logging.LOG_TAG, "synchronizeMailboxSynchronous", e);
            }
            if (e instanceof AuthenticationFailedException) {
                // Generate authentication notification
                nc.showLoginFailedNotificationSynchronous(account.mId, true /* incoming */);
            }
            throw e;
        } finally {
            if (remoteStore != null) {
                remoteStore.closeConnections();
            }
        }
        // TODO: Rather than use exceptions as logic above, return the status and handle it
        // correctly in caller.
        return EmailServiceStatus.SUCCESS;
    }

    /**
     * Lightweight record for the first pass of message sync, where I'm just seeing if
     * the local message requires sync.  Later (for messages that need syncing) we'll do a full
     * readout from the DB.
     */
    private static class LocalMessageInfo {
        private static final int COLUMN_ID = 0;
        private static final int COLUMN_FLAG_READ = 1;
        private static final int COLUMN_FLAG_FAVORITE = 2;
        private static final int COLUMN_FLAG_LOADED = 3;
        private static final int COLUMN_SERVER_ID = 4;
        private static final int COLUMN_FLAGS =  5;
        private static final int COLUMN_TIMESTAMP =  6;
        private static final String[] PROJECTION = {
                MessageColumns._ID,
                MessageColumns.FLAG_READ,
                MessageColumns.FLAG_FAVORITE,
                MessageColumns.FLAG_LOADED,
                SyncColumns.SERVER_ID,
                MessageColumns.FLAGS,
                MessageColumns.TIMESTAMP
        };

        final long mId;
        final boolean mFlagRead;
        final boolean mFlagFavorite;
        final int mFlagLoaded;
        final String mServerId;
        final int mFlags;
        final long mTimestamp;

        public LocalMessageInfo(Cursor c) {
            mId = c.getLong(COLUMN_ID);
            mFlagRead = c.getInt(COLUMN_FLAG_READ) != 0;
            mFlagFavorite = c.getInt(COLUMN_FLAG_FAVORITE) != 0;
            mFlagLoaded = c.getInt(COLUMN_FLAG_LOADED);
            mServerId = c.getString(COLUMN_SERVER_ID);
            mFlags = c.getInt(COLUMN_FLAGS);
            mTimestamp = c.getLong(COLUMN_TIMESTAMP);
            // Note: mailbox key and account key not needed - they are projected for the SELECT
        }
    }

    private static class OldestTimestampInfo {
        private static final int COLUMN_OLDEST_TIMESTAMP = 0;
        private static final String[] PROJECTION = new String[] {
            "MIN(" + MessageColumns.TIMESTAMP + ")"
        };
    }

    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
   private static boolean messageTruncated(Account account,int messageSize){
        boolean truncated = false;
        if(account.mHostAuthRecv.mProtocol.equals(HostAuth.SCHEME_POP3)){
            truncated = !(account.getDownloadOptions() == Utility.ENTIRE_MAIL ||
                    (messageSize <= account.getDownloadOptions()));
        } else if(account.mHostAuthRecv.mProtocol.equals(HostAuth.SCHEME_IMAP)){
            truncated = !(account.getDownloadOptions() == Utility.ENTIRE_MAIL ||
                    (messageSize <= account.getDownloadOptions()));
        }
        return truncated;
    }
    //[FEATURE]-Add-END by TSCD.Chao Zhang
    /**
     * Load the structure and body of messages not yet synced
     * @param account the account we're syncing
     * @param remoteFolder the (open) Folder we're working on
     * @param messages an array of Messages we've got headers for
     * @param toMailbox the destination mailbox we're syncing
     * @throws MessagingException
     */
    //[FEATURE]-Del-BEGIN byTSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    /*static void loadUnsyncedMessages(final Context context, final Account account,
            Folder remoteFolder, ArrayList<Message> messages, final Mailbox toMailbox)
            throws MessagingException {

        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.STRUCTURE);
        remoteFolder.fetch(messages.toArray(new Message[messages.size()]), fp, null);
        Message [] oneMessageArray = new Message[1];
        for (Message message : messages) {
            // Build a list of parts we are interested in. Text parts will be downloaded
            // right now, attachments will be left for later.
            ArrayList<Part> viewables = new ArrayList<Part>();
            ArrayList<Part> attachments = new ArrayList<Part>();
            MimeUtility.collectParts(message, viewables, attachments);
            // Download the viewables immediately
            oneMessageArray[0] = message;
            for (Part part : viewables) {
                fp.clear();
                fp.add(part);
                remoteFolder.fetch(oneMessageArray, fp, null);
            }
            // Store the updated message locally and mark it fully loaded
            Utilities.copyOneMessageToProvider(context, message, account, toMailbox,
                    EmailContent.Message.FLAG_LOADED_COMPLETE);
        }
    }*/
    //[FEATURE]-Del-END byTSCD.Chao Zhang

    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_S
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    public static ArrayList<Message> getDownloadReminMessage(ArrayList<Message> unsyncedMessages, String downloadMsgUid){
      ArrayList<Message> downloadReminMessage =new ArrayList<Message>();
      for (Message msg : unsyncedMessages) {
        if (msg.getUid().equals(downloadMsgUid)) {
            downloadReminMessage.add(msg);
            break;
        }
      }
      return downloadReminMessage;
    }
    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_E

    public static int loadUnsyncedMessage (final Context context,long msgId) {
        Store remoteStore = null;
        try {
             EmailContent.Message localMessage = EmailContent.Message.restoreMessageWithId(context,msgId);
             Message unsyncedMessage = LegacyConversions.makeMessage(context,localMessage);
             Account account = Account.getAccountForMessageId(context,msgId);
             Mailbox  toMailbox =Mailbox.getMailboxForMessageId(context, msgId);
                if(account == null || toMailbox == null) {
                 return 0;
                }
            //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_S
             String downloadMsgUid = unsyncedMessage.getUid();
             remoteStore = Store.getInstance(account, context);
            //TS: zheng.zou 2015-05-21 EMAIL BUGFIX_1001398 ADD_S
            //note:sync the pending status(flags) before another sync.
             processPendingActionsSynchronous(context, account, remoteStore, true);
            //TS: zheng.zou 2015-05-21 EMAIL BUGFIX_1001398 ADD_E
             synchronizeMailboxGeneric(context,account,remoteStore,toMailbox,false,true,downloadMsgUid);
            //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_E
             }
         catch (MessagingException e){
               return 0;
          }finally {
              if (remoteStore != null) {
                  remoteStore.closeConnections();
              }
          }
          return 1;
     }
    //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_S
     //[FEATURE]-Add-END byTSCD.Chao Zhang
    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    static void loadUnsyncedMessages(final Context context,final Account account, Folder remoteFolder,
            ArrayList<Message> unsyncedMessages, final Mailbox toMailbox, String downloadMsgUid)
            throws MessagingException {

        // 1. Divide the unsynced messages into small & large (by size)

        // TODO doing this work here (synchronously) is problematic because it prevents the UI
        // from affecting the order (e.g. download a message because the user requested it.)  Much
        // of this logic should move out to a different sync loop that attempts to update small
        // groups of messages at a time, as a background task.  However, we can't just return
        // (yet) because POP messages don't have an envelope yet....

        //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        remoteFolder.setDownloadReminFlag(!TextUtils.isEmpty(downloadMsgUid));
        final boolean mDownloadReminFlag = remoteFolder.getDownloadReminFlag();
        if(account.mHostAuthRecv.mProtocol.equals(HostAuth.SCHEME_IMAP) &&
            account.getDownloadOptions() == Utility.HEAD_ONLY && !mDownloadReminFlag) {
            //do nothing,return,
            return;
        }
        //[FEATURE]-Mod-END by TSCD.chao zhang
        //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
        //here we get which msg we actually want to sync.
        if(mDownloadReminFlag){
            unsyncedMessages = getDownloadReminMessage(unsyncedMessages,downloadMsgUid);
        }
        //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD_E
        //[FEATURE]-Mod-END by TSCD.chao zhang
        //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 DEL_S
//        ArrayList<Message> largeMessages = new ArrayList<Message>();
//        ArrayList<Message> smallMessages = new ArrayList<Message>();
//        for (Message message : unsyncedMessages) {
//            message.setDownloadOptions(account.getDownloadOptions());
//            if (message.getSize() > (MAX_SMALL_MESSAGE_SIZE)) {
//                largeMessages.add(message);
//            } else {
//                smallMessages.add(message);
//            }
//        }
        //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 DEL_E

        // 2. Download small messages

        // TODO Problems with this implementation.  1. For IMAP, where we get a real envelope,
        // this is going to be inefficient and duplicate work we've already done.  2.  It's going
        // back to the DB for a local message that we already had (and discarded).

        // For small messages, we specify "body", which returns everything (incl. attachments)
        FetchProfile fp = new FetchProfile();
        //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 DEL_S
//        fp.add(FetchProfile.Item.BODY);
//        if (Account.restoreAccountWithId(context, account.mId) == null) {
//            return;
//        }
//        remoteFolder.fetch(smallMessages.toArray(new Message[smallMessages.size()]), fp,
//                new MessageRetrievalListener() {
//                    public void messageRetrieved(Message message) {
//                        // Store the updated message locally and mark it fully loaded
//                        try{
//                            Utilities.copyOneMessageToProvider(context,message, account, toMailbox,
//                                   mDownloadReminFlag? EmailContent.Message.FLAG_LOADED_COMPLETE :
//                                         messageTruncated(account, message.getSize())? EmailContent.Message.FLAG_LOADED_PARTIAL : EmailContent.Message.FLAG_LOADED_COMPLETE);
//                        } catch (MessagingException e){
//                            Utilities.copyOneMessageToProvider(context,message, account, toMailbox,
//                                    EmailContent.Message.FLAG_LOADED_PARTIAL);
//                        }
//                    }
//
//                    @Override
//                    public void loadAttachmentProgress(int progress) {
//                    }
//        });
//
//        // 3. Download large messages.  We ask the server to give us the message structure,
//        // but not all of the attachments.
//        fp.clear();
        //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 DEL_E
        fp.add(FetchProfile.Item.STRUCTURE);
        if (Account.restoreAccountWithId(context, account.mId) == null) {
            return;
        }
        //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 MOD_S
        remoteFolder.fetch(unsyncedMessages.toArray(new Message[unsyncedMessages.size()]), fp, null);
        for (Message message : unsyncedMessages) {
        //TS:kaifeng.lu 2015-10-19 EMAIL BUGFIX_1087027 MOD_E
            if (message.getBody() == null) {
                // POP doesn't support STRUCTURE mode, so we'll just do a partial download
                // (hopefully enough to see some/all of the body) and mark the message for
                // further download.
                fp.clear();
                fp.add(FetchProfile.Item.BODY_SANE);
                //  TODO a good optimization here would be to make sure that all Stores set
                //  the proper size after this fetch and compare the before and after size. If
                //  they equal we can mark this SYNCHRONIZED instead of PARTIALLY_SYNCHRONIZED
                if (Account.restoreAccountWithId(context, account.mId) == null) {
                    return;
                }
                remoteFolder.fetch(new Message[] { message }, fp, null);

                // Store the partially-loaded message and mark it partially loaded
                try{
                    //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                    Utilities.copyOneMessageToProvider(context,message, account, toMailbox,
                           mDownloadReminFlag? EmailContent.Message.FLAG_LOADED_COMPLETE
                           : (messageTruncated(account, message.getSize())?EmailContent.Message.FLAG_LOADED_PARTIAL
                           : EmailContent.Message.FLAG_LOADED_COMPLETE));
                    //[FEATURE]-Mod-END by TSCD.chao zhang
                } catch (MessagingException e){
                    Utilities.copyOneMessageToProvider(context,message, account, toMailbox,
                            EmailContent.Message.FLAG_LOADED_PARTIAL);
                }
            } else {
                // We have a structure to deal with, from which
                // we can pull down the parts we want to actually store.
                // Build a list of parts we are interested in. Text parts will be downloaded
                // right now, attachments will be left for later.
                ArrayList<Part> viewables = new ArrayList<Part>();
                ArrayList<Part> attachments = new ArrayList<Part>();
                MimeUtility.collectParts(message, viewables, attachments);
                // Download the viewables immediately
                for (Part part : viewables) {
                    fp.clear();
                    fp.add(part);
                    // TODO what happens if the network connection dies? We've got partial
                    // messages with incorrect status stored.
                    //[BUGFIX]-Add-BEGIN by TCTNB.chen caixia,01/15/2014,PR 588786
                    if (Account.restoreAccountWithId(context, account.mId) == null) {
                        return;
                    }
                    remoteFolder.fetch(new Message[] { message }, fp, null);
                }
                // Store the updated message locally and mark it fully loaded
                try{
                    //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                    Utilities.copyOneMessageToProvider(context,message, account, toMailbox,
                           mDownloadReminFlag? EmailContent.Message.FLAG_LOADED_COMPLETE
                           : (messageTruncated(account, message.getSize())? EmailContent.Message.FLAG_LOADED_PARTIAL
                           : EmailContent.Message.FLAG_LOADED_COMPLETE));
                    //[FEATURE]-Add-END by TSCD.chao zhang
                } catch (MessagingException e){
                    Utilities.copyOneMessageToProvider(context,message, account, toMailbox,
                            EmailContent.Message.FLAG_LOADED_PARTIAL);
                }
            }
        }

    }
    //[FEATURE]-Add-END by TSCD.Chao Zhang


    public static void downloadFlagAndEnvelope(final Context context, final Account account,
            final Mailbox mailbox, Folder remoteFolder, ArrayList<Message> unsyncedMessages,
            HashMap<String, LocalMessageInfo> localMessageMap, final ArrayList<Long> unseenMessages)
            throws MessagingException {
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.ENVELOPE);

        final HashMap<String, LocalMessageInfo> localMapCopy;
        if (localMessageMap != null)
            localMapCopy = new HashMap<String, LocalMessageInfo>(localMessageMap);
        else {
            localMapCopy = new HashMap<String, LocalMessageInfo>();
        }

        remoteFolder.fetch(unsyncedMessages.toArray(new Message[unsyncedMessages.size()]), fp,
                new MessageRetrievalListener() {
                    @Override
                    public void messageRetrieved(Message message) {
                        try {
                            // Determine if the new message was already known (e.g. partial)
                            // And create or reload the full message info
                            final LocalMessageInfo localMessageInfo =
                                    localMapCopy.get(message.getUid());
                            final boolean localExists = localMessageInfo != null;

                            if (!localExists && message.isSet(Flag.DELETED)) {
                                // This is a deleted message that we don't have locally, so don't
                                // create it
                                return;
                            }

                            final EmailContent.Message localMessage;
                            if (!localExists) {
                                localMessage = new EmailContent.Message();
                            } else {
                                localMessage = EmailContent.Message.restoreMessageWithId(
                                        context, localMessageInfo.mId);
                            }

                            if (localMessage != null) {
                                try {
                                    // Copy the fields that are available into the message
                                    LegacyConversions.updateMessageFields(localMessage,
                                            message, account.mId, mailbox.mId);
                                    //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
                                    long mailboxType=Mailbox.getMailboxType(context,localMessage.mMailboxKey);
                                    if (mailboxType == Mailbox.TYPE_DRAFTS
                                            || mailboxType == Mailbox.TYPE_OUTBOX
                                            || mailboxType == Mailbox.TYPE_SENT) {
                                        localMessage.mDisplayName = UiUtilities.makeDisplayName(context, localMessage.mTo, localMessage.mCc, localMessage.mBcc);
                                    } else  {
                                        Address[] from = message.getFrom();
                                        if (from != null && from.length > 0) {
                                            localMessage.mDisplayName = from[0].toFriendly();
                                        }
                                    }
                                    //[FEATURE]-ADD-END by TSNJ.wei huang
                                    // Commit the message to the local store
                                    Utilities.saveOrUpdate(localMessage, context);
                                    // Track the "new" ness of the downloaded message
                                    if (!message.isSet(Flag.SEEN) && unseenMessages != null) {
                                        unseenMessages.add(localMessage.mId);
                                    }
                                } catch (MessagingException me) {
                                    LogUtils.e(Logging.LOG_TAG,
                                            "Error while copying downloaded message." + me);
                                }
                            }
                        }
                        catch (Exception e) {
                            LogUtils.e(Logging.LOG_TAG,
                                    "Error while storing downloaded message." + e.toString());
                        }
                    }

                    @Override
                    public void loadAttachmentProgress(int progress) {
                    }
                });

    }

    /**
     * Synchronizer for IMAP.
     *
     * TODO Break this method up into smaller chunks.
     *
     * @param account the account to sync
     * @param mailbox the mailbox to sync
     * @param loadMore whether we should be loading more older messages
     * @param uiRefresh whether this request is in response to a user action
     * @throws MessagingException
     */
    private synchronized static void synchronizeMailboxGeneric(final Context context,
            final Account account, Store remoteStore, final Mailbox mailbox, final boolean loadMore,
            final boolean uiRefresh, String downloadMsgUid)   //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD
            throws MessagingException {

        LogUtils.d(Logging.LOG_TAG, "synchronizeMailboxGeneric " + account + " " + mailbox + " "
                + loadMore + " " + uiRefresh);

        final ArrayList<Long> unseenMessages = new ArrayList<Long>();

        ContentResolver resolver = context.getContentResolver();

        // 0. We do not ever sync DRAFTS or OUTBOX (down or up)
        if (mailbox.mType == Mailbox.TYPE_DRAFTS || mailbox.mType == Mailbox.TYPE_OUTBOX) {
            return;
        }

        // 1. Figure out what our sync window should be.
        long endDate;
        int localMessageCount = 0; //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807

        // We will do a full sync if the user has actively requested a sync, or if it has been
        // too long since the last full sync.
        // If we have rebooted since the last full sync, then we may get a negative
        // timeSinceLastFullSync. In this case, we don't know how long it's been since the last
        // full sync so we should perform the full sync.
        final long timeSinceLastFullSync = SystemClock.elapsedRealtime() -
                mailbox.mLastFullSyncTime;
        final boolean fullSync = (uiRefresh || loadMore ||
                timeSinceLastFullSync >= FULL_SYNC_INTERVAL_MILLIS || timeSinceLastFullSync < 0);

        //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
        if(!isSyncByTime(account.mEmailAddress)){
            if (fullSync) {
                Cursor localOldestCursor = null;
                try {
                    localOldestCursor = resolver.query(EmailContent.Message.CONTENT_URI,
                            new String[]{"_id"},
                            EmailContent.MessageColumns.ACCOUNT_KEY + "=?" + " AND " +
                                    MessageColumns.MAILBOX_KEY + "=? ",
                            new String[] {String.valueOf(account.mId), String.valueOf(mailbox.mId)},
                            null);
                    if (localOldestCursor != null && localOldestCursor.moveToFirst()) {
                        localMessageCount = localOldestCursor.getCount();
                    }
                }catch(Exception e){
                    Log.d("ImapSync", e.getMessage());
                }
                finally {
                    if (localOldestCursor != null) {
                        localOldestCursor.close();
                    }
                }
            }
        }
        //[BUGFIX]-ADD-END by TSNJ.wei huang 08/21/2015 PR1070807

        if (account.mSyncLookback == SyncWindow.SYNC_WINDOW_ALL) {
            // This is really for testing. There is no UI that allows setting the sync window for
            // IMAP, but it can be set by sending a special intent to AccountSetupFinal activity.
            endDate = 0;
        } else if (fullSync) {
            // Find the oldest message in the local store. We need our time window to include
            // all messages that are currently present locally.
            endDate = System.currentTimeMillis() - FULL_SYNC_WINDOW_MILLIS;
            Cursor localOldestCursor = null;
            try {
                // b/11520812 Ignore message with timestamp = 0 (which includes NULL)
                localOldestCursor = resolver.query(EmailContent.Message.CONTENT_URI,
                        OldestTimestampInfo.PROJECTION,
                        EmailContent.MessageColumns.ACCOUNT_KEY + "=?" + " AND " +
                                MessageColumns.MAILBOX_KEY + "=? AND " +
                                MessageColumns.TIMESTAMP + "!=0",
                        new String[] {String.valueOf(account.mId), String.valueOf(mailbox.mId)},
                        null);
                if (localOldestCursor != null && localOldestCursor.moveToFirst()) {
                    long oldestLocalMessageDate = localOldestCursor.getLong(
                            OldestTimestampInfo.COLUMN_OLDEST_TIMESTAMP);
                    if (oldestLocalMessageDate > 0) {
                        endDate = Math.min(endDate, oldestLocalMessageDate);
                        LogUtils.d(
                                Logging.LOG_TAG, "oldest local message " + oldestLocalMessageDate);
                    }
                }
            } finally {
                if (localOldestCursor != null) {
                    localOldestCursor.close();
                }
            }
            LogUtils.d(Logging.LOG_TAG, "full sync: original window: now - " + endDate);
        } else {
            // We are doing a frequent, quick sync. This only syncs a small time window, so that
            // we wil get any new messages, but not spend a lot of bandwidth downloading
            // messageIds that we most likely already have.
            endDate = System.currentTimeMillis() - QUICK_SYNC_WINDOW_MILLIS;
            LogUtils.d(Logging.LOG_TAG, "quick sync: original window: now - " + endDate);
        }

        // 2. Open the remote folder and create the remote folder if necessary
        // The account might have been deleted
        if (remoteStore == null) {
            LogUtils.d(Logging.LOG_TAG, "account is apparently deleted");
            return;
        }
        final Folder remoteFolder = remoteStore.getFolder(mailbox.mServerId);

        // If the folder is a "special" folder we need to see if it exists
        // on the remote server. It if does not exist we'll try to create it. If we
        // can't create we'll abort. This will happen on every single Pop3 folder as
        // designed and on Imap folders during error conditions. This allows us
        // to treat Pop3 and Imap the same in this code.
        if (mailbox.mType == Mailbox.TYPE_TRASH || mailbox.mType == Mailbox.TYPE_SENT) {
            if (!remoteFolder.exists()) {
                if (!remoteFolder.create(FolderType.HOLDS_MESSAGES)) {
                    LogUtils.w(Logging.LOG_TAG, "could not create remote folder type %d",
                        mailbox.mType);
                    return;
                }
            }
        }
      //TS: wenggangjin 2014-12-24 EMAIL BUGFIX_877623 MOD_S
        if (!remoteFolder.exists()) {
            android.util.Log.d("ccxccx", "folder do not exists on the server mailbox serverId = " + mailbox.mServerId);
            return;
        }
      //TS: wenggangjin 2014-12-24 EMAIL BUGFIX_877623 MOD_E
        remoteFolder.open(OpenMode.READ_WRITE);

        // 3. Trash any remote messages that are marked as trashed locally.
        // TODO - this comment was here, but no code was here.

        // 4. Get the number of messages on the server.
        // TODO: this value includes deleted but unpurged messages, and so slightly mismatches
        // the contents of our DB since we drop deleted messages. Figure out what to do about this.
        final int remoteMessageCount = remoteFolder.getMessageCount();

        // 5. Save folder message count locally.
        mailbox.updateMessageCount(context, remoteMessageCount);

        // 6. Get all message Ids in our sync window:
        Message[] remoteMessages;
        //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
        if(isSyncByTime(account.mEmailAddress)){
            remoteMessages = remoteFolder.getMessages(0, endDate, null);
            LogUtils.d("ImapSync", "Sync by time");
        }else{
            int startFetch = remoteMessageCount - INIT_MESSAGE_TO_FETCH >= 1 ? remoteMessageCount - INIT_MESSAGE_TO_FETCH : 1;
            remoteMessages = remoteFolder.getMessages(startFetch, remoteMessageCount, null);
            LogUtils.d("ImapSync", "Sync by number");
        }
        //[BUGFIX]-ADD-END by TSNJ.wei huang 08/21/2015 PR1070807
        LogUtils.d(Logging.LOG_TAG, "received " + remoteMessages.length + " messages");

        // 7. See if we need any additional messages beyond our date query range results.
        // If we do, keep increasing the size of our query window until we have
        // enough, or until we have all messages in the mailbox.
        int totalCountNeeded;
        int num_loadMoreMessage = localMessageCount; //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
        if (loadMore) {
            totalCountNeeded = remoteMessages.length + LOAD_MORE_MIN_INCREMENT;
            num_loadMoreMessage = localMessageCount + NUM_FETCH_MESSAGE_INCREMENT; //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
        } else {
            totalCountNeeded = remoteMessages.length;
            if (fullSync && totalCountNeeded < MINIMUM_MESSAGES_TO_SYNC) {
                totalCountNeeded = MINIMUM_MESSAGES_TO_SYNC;
            }
        }
        //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
        if(loadMore && !isSyncByTime(account.mEmailAddress)){
            int startFetch = remoteMessageCount - num_loadMoreMessage >= 1 ? remoteMessageCount - num_loadMoreMessage : 1;
            remoteMessages = remoteFolder.getMessages(startFetch, remoteMessageCount, null);
            LogUtils.d("ImapSync", "Sync by Number additional + local :"+localMessageCount + "loadMore : " + num_loadMoreMessage);
        }
        //[BUGFIX]-ADD-END by TSNJ.wei huang 08/21/2015 PR1070807
        LogUtils.d(Logging.LOG_TAG, "need " + totalCountNeeded + " total");

        final int additionalMessagesNeeded = totalCountNeeded - remoteMessages.length;
        if (additionalMessagesNeeded > 0) {
            LogUtils.d(Logging.LOG_TAG, "trying to get " + additionalMessagesNeeded + " more");
            long startDate = endDate - 1;
            Message[] additionalMessages = new Message[0];
            long windowIncreaseSize = INITIAL_WINDOW_SIZE_INCREASE;
            while (additionalMessages.length < additionalMessagesNeeded && endDate > 0) {
                endDate = endDate - windowIncreaseSize;
                if (endDate < 0) {
                    LogUtils.d(Logging.LOG_TAG, "window size too large, this is the last attempt");
                    endDate = 0;
                }
                LogUtils.d(Logging.LOG_TAG,
                        "requesting additional messages from range " + startDate + " - " + endDate);
                //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
                if(isSyncByTime(account.mEmailAddress)){
                    additionalMessages = remoteFolder.getMessages(startDate, endDate, null);
                    LogUtils.d("ImapSync", "Sync by time additional");
                }
                //[BUGFIX]-ADD-END by TSNJ.wei huang 08/21/2015 PR1070807

                // If don't get enough messages with the first window size expansion,
                // we need to accelerate rate at which the window expands. Otherwise,
                // if there were no messages for several weeks, we'd always end up
                // performing dozens of queries.
                windowIncreaseSize *= 2;
            }

            LogUtils.d(Logging.LOG_TAG, "additionalMessages " + additionalMessages.length);
            if (additionalMessages.length < additionalMessagesNeeded) {
                // We have attempted to load a window that goes all the way back to time zero,
                // but we still don't have as many messages as the server says are in the inbox.
                // This is not expected to happen.
                LogUtils.e(Logging.LOG_TAG, "expected to find " + additionalMessagesNeeded
                        + " more messages, only got " + additionalMessages.length);
            }
            int additionalToKeep = additionalMessages.length;
            if (additionalMessages.length > LOAD_MORE_MAX_INCREMENT) {
                // We have way more additional messages than intended, drop some of them.
                // The last messages are the most recent, so those are the ones we need to keep.
                additionalToKeep = LOAD_MORE_MAX_INCREMENT;
            }

            // Copy the messages into one array.
            Message[] allMessages = new Message[remoteMessages.length + additionalToKeep];
            System.arraycopy(remoteMessages, 0, allMessages, 0, remoteMessages.length);
            // additionalMessages may have more than we need, only copy the last
            // several. These are the most recent messages in that set because
            // of the way IMAP server returns messages.
            System.arraycopy(additionalMessages, additionalMessages.length - additionalToKeep,
                    allMessages, remoteMessages.length, additionalToKeep);
            remoteMessages = allMessages;
        }

        // 8. Get the all of the local messages within the sync window, and create
        // an index of the uids.
        // The IMAP query for messages ignores time, and only looks at the date part of the endDate.
        // So if we query for messages since Aug 11 at 3:00 PM, we can get messages from any time
        // on Aug 11. Our IMAP query results can include messages up to 24 hours older than endDate,
        // or up to 25 hours older at a daylight savings transition.
        // It is important that we have the Id of any local message that could potentially be
        // returned by the IMAP query, or we will create duplicate copies of the same messages.
        // So we will increase our local query range by this much.
        // Note that this complicates deletion: It's not okay to delete anything that is in the
        // localMessageMap but not in the remote result, because we know that we may be getting
        // Ids of local messages that are outside the IMAP query window.
        Cursor localUidCursor = null;
        HashMap<String, LocalMessageInfo> localMessageMap = new HashMap<String, LocalMessageInfo>();
        try {
            // FLAG: There is a problem that causes us to store the wrong date on some messages,
            // so messages get a date of zero. If we filter these messages out and don't put them
            // in our localMessageMap, then we'll end up loading the same message again.
            // See b/10508861
//            final long queryEndDate = endDate - DateUtils.DAY_IN_MILLIS - DateUtils.HOUR_IN_MILLIS;
            final long queryEndDate = 0;
            localUidCursor = resolver.query(
                    EmailContent.Message.CONTENT_URI,
                    LocalMessageInfo.PROJECTION,
                    EmailContent.MessageColumns.ACCOUNT_KEY + "=?"
                            + " AND " + MessageColumns.MAILBOX_KEY + "=?"
                            + " AND " + MessageColumns.TIMESTAMP + ">=?",
                    new String[] {
                            String.valueOf(account.mId),
                            String.valueOf(mailbox.mId),
                            String.valueOf(queryEndDate) },
                    null);
            while (localUidCursor.moveToNext()) {
                LocalMessageInfo info = new LocalMessageInfo(localUidCursor);
                // If the message has no server id, it's local only. This should only happen for
                // mail created on the client that has failed to upsync. We want to ignore such
                // mail during synchronization (i.e. leave it as-is and let the next sync try again
                // to upsync).
                if (!TextUtils.isEmpty(info.mServerId)) {
                    localMessageMap.put(info.mServerId, info);
                }
            }
        } finally {
            if (localUidCursor != null) {
                localUidCursor.close();
            }
        }

        // 9. Get a list of the messages that are in the remote list but not on the
        // local store, or messages that are in the local store but failed to download
        // on the last sync. These are the new messages that we will download.
        // Note, we also skip syncing messages which are flagged as "deleted message" sentinels,
        // because they are locally deleted and we don't need or want the old message from
        // the server.
        final ArrayList<Message> unsyncedMessages = new ArrayList<Message>();
        final HashMap<String, Message> remoteUidMap = new HashMap<String, Message>();
        // Process the messages in the reverse order we received them in. This means that
        // we load the most recent one first, which gives a better user experience.
        for (int i = remoteMessages.length - 1; i >= 0; i--) {
            Message message = remoteMessages[i];
            LogUtils.d(Logging.LOG_TAG, "remote message " + message.getUid());
            remoteUidMap.put(message.getUid(), message);

            LocalMessageInfo localMessage = localMessageMap.get(message.getUid());

            // localMessage == null -> message has never been created (not even headers)
            // mFlagLoaded = UNLOADED -> message created, but none of body loaded
            // mFlagLoaded = PARTIAL -> message created, a "sane" amt of body has been loaded
            // mFlagLoaded = COMPLETE -> message body has been completely loaded
            // mFlagLoaded = DELETED -> message has been deleted
            // Only the first two of these are "unsynced", so let's retrieve them
            if (localMessage == null ||
                    (localMessage.mFlagLoaded == EmailContent.Message.FLAG_LOADED_UNLOADED) ||
                    (localMessage.mFlagLoaded == EmailContent.Message.FLAG_LOADED_PARTIAL)) {
                unsyncedMessages.add(message);
            }
        }

        // 10. Download basic info about the new/unloaded messages (if any)
        /*
         * Fetch the flags and envelope only of the new messages. This is intended to get us
         * critical data as fast as possible, and then we'll fill in the details.
         */
        if (unsyncedMessages.size() > 0) {
            downloadFlagAndEnvelope(context, account, mailbox, remoteFolder, unsyncedMessages,
                    localMessageMap, unseenMessages);
        }

        // 11. Refresh the flags for any messages in the local store that we didn't just download.
        // TODO This is a bit wasteful because we're also updating any messages we already did get
        // the flags and envelope for previously.
        // TODO: the fetch() function, and others, should take List<>s of messages, not
        // arrays of messages.
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.FLAGS);
        if (remoteMessages.length > MAX_MESSAGES_TO_FETCH) {
            List<Message> remoteMessageList = Arrays.asList(remoteMessages);
            for (int start = 0; start < remoteMessageList.size(); start += MAX_MESSAGES_TO_FETCH) {
                int end = start + MAX_MESSAGES_TO_FETCH;
                if (end >= remoteMessageList.size()) {
                    end = remoteMessageList.size() - 1;
                }
                List<Message> chunk = remoteMessageList.subList(start, end);
                final Message[] partialArray = chunk.toArray(new Message[chunk.size()]);
                // Fetch this one chunk of messages
                remoteFolder.fetch(partialArray, fp, null);
            }
        } else {
            remoteFolder.fetch(remoteMessages, fp, null);
        }
        boolean remoteSupportsSeen = false;
        boolean remoteSupportsFlagged = false;
        boolean remoteSupportsAnswered = false;
        for (Flag flag : remoteFolder.getPermanentFlags()) {
            if (flag == Flag.SEEN) {
                remoteSupportsSeen = true;
            }
            if (flag == Flag.FLAGGED) {
                remoteSupportsFlagged = true;
            }
            if (flag == Flag.ANSWERED) {
                remoteSupportsAnswered = true;
            }
        }

        // 12. Update SEEN/FLAGGED/ANSWERED (star) flags (if supported remotely - e.g. not for POP3)
        if (remoteSupportsSeen || remoteSupportsFlagged || remoteSupportsAnswered) {
            for (Message remoteMessage : remoteMessages) {
                LocalMessageInfo localMessageInfo = localMessageMap.get(remoteMessage.getUid());
                if (localMessageInfo == null) {
                    continue;
                }
                boolean localSeen = localMessageInfo.mFlagRead;
                boolean remoteSeen = remoteMessage.isSet(Flag.SEEN);
                boolean newSeen = (remoteSupportsSeen && (remoteSeen != localSeen));
                boolean localFlagged = localMessageInfo.mFlagFavorite;
                boolean remoteFlagged = remoteMessage.isSet(Flag.FLAGGED);
                boolean newFlagged = (remoteSupportsFlagged && (localFlagged != remoteFlagged));
                int localFlags = localMessageInfo.mFlags;
                boolean localAnswered = (localFlags & EmailContent.Message.FLAG_REPLIED_TO) != 0;
                boolean remoteAnswered = remoteMessage.isSet(Flag.ANSWERED);
                boolean newAnswered = (remoteSupportsAnswered && (localAnswered != remoteAnswered));
                if (newSeen || newFlagged || newAnswered) {
                    Uri uri = ContentUris.withAppendedId(
                            EmailContent.Message.CONTENT_URI, localMessageInfo.mId);
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(MessageColumns.FLAG_READ, remoteSeen);
                    updateValues.put(MessageColumns.FLAG_FAVORITE, remoteFlagged);
                    if (remoteAnswered) {
                        localFlags |= EmailContent.Message.FLAG_REPLIED_TO;
                    } else {
                        localFlags &= ~EmailContent.Message.FLAG_REPLIED_TO;
                    }
                    updateValues.put(MessageColumns.FLAGS, localFlags);
                    resolver.update(uri, updateValues, null, null);
                }
            }
        }

        // 12.5 Remove messages that are marked as deleted so that we drop them from the DB in the
        // next step
        for (final Message remoteMessage : remoteMessages) {
            if (remoteMessage.isSet(Flag.DELETED)) {
                remoteUidMap.remove(remoteMessage.getUid());
                unsyncedMessages.remove(remoteMessage);
            }
        }

        // 13. Remove messages that are in the local store and in the current sync window,
        // but no longer on the remote store. Note that localMessageMap can contain messages
        // that are not actually in our sync window. We need to check the timestamp to ensure
        // that it is before deleting.
        for (final LocalMessageInfo info : localMessageMap.values()) {
            // If this message is inside our sync window, and we cannot find it in our list
            // of remote messages, then we know it's been deleted from the server.
            if (info.mTimestamp >= endDate && !remoteUidMap.containsKey(info.mServerId)) {
                // Delete associated data (attachment files)
                // Attachment & Body records are auto-deleted when we delete the Message record
                AttachmentUtilities.deleteAllAttachmentFiles(context, account.mId, info.mId);

                // Delete the message itself
                final Uri uriToDelete = ContentUris.withAppendedId(
                        EmailContent.Message.CONTENT_URI, info.mId);
                resolver.delete(uriToDelete, null, null);

                // Delete extra rows (e.g. updated or deleted)
                final Uri updateRowToDelete = ContentUris.withAppendedId(
                        EmailContent.Message.UPDATED_CONTENT_URI, info.mId);
                resolver.delete(updateRowToDelete, null, null);
                final Uri deleteRowToDelete = ContentUris.withAppendedId(
                        EmailContent.Message.DELETED_CONTENT_URI, info.mId);
                resolver.delete(deleteRowToDelete, null, null);
            }
        }

        loadUnsyncedMessages(context, account, remoteFolder, unsyncedMessages, mailbox, downloadMsgUid);  //TS: zheng.zou 2015-05-18 EMAIL BUGFIX_1004722 MOD

        if (fullSync) {
            mailbox.updateLastFullSyncTime(context, SystemClock.elapsedRealtime());
        }

        // 14. Clean up and report results
        remoteFolder.close(false);
    }

    /**
     * Find messages in the updated table that need to be written back to server.
     *
     * Handles:
     *   Read/Unread
     *   Flagged
     *   Append (upload)
     *   Move To Trash
     *   Empty trash
     * TODO:
     *   Move
     *
     * @param account the account to scan for pending actions
     * @throws MessagingException
     */
    private static void processPendingActionsSynchronous(Context context, Account account,
            Store remoteStore, boolean manualSync)
            throws MessagingException {
        TrafficStats.setThreadStatsTag(TrafficFlags.getSyncFlags(context, account));
        String[] accountIdArgs = new String[] { Long.toString(account.mId) };

        // Handle deletes first, it's always better to get rid of things first
        processPendingDeletesSynchronous(context, account, remoteStore, accountIdArgs);

        // Handle uploads (currently, only to sent messages)
        processPendingUploadsSynchronous(context, account, remoteStore, accountIdArgs, manualSync);

        //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        if (EmailApplication.isOrangeImapFeatureOn() && Account.isOrangeImapAccount(context, account.mId)) {
            processPendingUploadsDraftSynchronous(context, account, accountIdArgs, manualSync);
        }
        //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan

        // Now handle updates / upsyncs
        processPendingUpdatesSynchronous(context, account, remoteStore, accountIdArgs);
    }

    /**
     * Get the mailbox corresponding to the remote location of a message; this will normally be
     * the mailbox whose _id is mailboxKey, except for search results, where we must look it up
     * by serverId.
     *
     * @param message the message in question
     * @return the mailbox in which the message resides on the server
     */
    private static Mailbox getRemoteMailboxForMessage(
            Context context, EmailContent.Message message) {
        // If this is a search result, use the protocolSearchInfo field to get the server info
        if (!TextUtils.isEmpty(message.mProtocolSearchInfo)) {
            long accountKey = message.mAccountKey;
            String protocolSearchInfo = message.mProtocolSearchInfo;
            if (accountKey == mLastSearchAccountKey &&
                    protocolSearchInfo.equals(mLastSearchServerId)) {
                return mLastSearchRemoteMailbox;
            }
            Cursor c = context.getContentResolver().query(Mailbox.CONTENT_URI,
                    Mailbox.CONTENT_PROJECTION, Mailbox.PATH_AND_ACCOUNT_SELECTION,
                    new String[] {protocolSearchInfo, Long.toString(accountKey) },
                    null);
            try {
                if (c.moveToNext()) {
                    Mailbox mailbox = new Mailbox();
                    mailbox.restore(c);
                    mLastSearchAccountKey = accountKey;
                    mLastSearchServerId = protocolSearchInfo;
                    mLastSearchRemoteMailbox = mailbox;
                    return mailbox;
                } else {
                    return null;
                }
            } finally {
                c.close();
            }
        } else {
            return Mailbox.restoreMailboxWithId(context, message.mMailboxKey);
        }
    }
    //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
    public static void processPendingDeleteDraftActions(final Context context,final long accountId, final EmailContent.Message message) {
        try {
            Account account = Account.restoreAccountWithId(context, accountId);
            if (account == null) {
                return;
            }
            processPendingDeleteRemoteDraft(context,account, message);
        }
        catch (MessagingException me) {
            if (Logging.LOGD) {
                Log.w(Logging.LOG_TAG, "processPendingDeleteDraftActions", me);
            }
            /*
             * Ignore any exceptions from the commands. Commands will be processed
             * on the next round.
             */
        }
    }

    private static void processPendingDeleteRemoteDraft(Context context,Account account, EmailContent.Message message)
            throws MessagingException {
        try {
            Store remoteStore = Store.getInstance(account, context);
            Mailbox mailbox = getRemoteMailboxForMessage(context,message);
            Folder remoteDraftFolder = remoteStore.getFolder(mailbox.mServerId);
            if (!remoteDraftFolder.exists()) {
                return;
            }
            remoteDraftFolder.open(OpenMode.READ_WRITE);
            if (remoteDraftFolder.getMode() != OpenMode.READ_WRITE) {
                return;
            }
            Message remoteMessage = remoteDraftFolder.getMessage(message.mServerId);
            if (remoteMessage == null) {
                return;
            }
            remoteMessage.setFlag(Flag.DELETED, true);
            remoteDraftFolder.expunge();
            remoteDraftFolder.close(false);
        } catch (MessagingException e) {
            Log.e(Logging.LOG_TAG,"delete draft error = " + e);
        }
    }
    //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
    /**
     * Scan for messages that are in the Message_Deletes table, look for differences that
     * we can deal with, and do the work.
     */
    private static void processPendingDeletesSynchronous(Context context, Account account,
            Store remoteStore, String[] accountIdArgs) {
        Cursor deletes = context.getContentResolver().query(
                EmailContent.Message.DELETED_CONTENT_URI,
                EmailContent.Message.CONTENT_PROJECTION,
                EmailContent.MessageColumns.ACCOUNT_KEY + "=?", accountIdArgs,
                EmailContent.MessageColumns.MAILBOX_KEY);
        long lastMessageId = -1;
        try {
            // loop through messages marked as deleted
            while (deletes.moveToNext()) {
                EmailContent.Message oldMessage =
                        EmailContent.getContent(context, deletes, EmailContent.Message.class);

                if (oldMessage != null) {
                    lastMessageId = oldMessage.mId;

                    Mailbox mailbox = getRemoteMailboxForMessage(context, oldMessage);
                    if (mailbox == null) {
                        continue; // Mailbox removed. Move to the next message.
                    }
                    boolean deleteFromTrash = mailbox.mType == Mailbox.TYPE_TRASH;
                    //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
                    if (EmailApplication.isOrangeImapFeatureOn() && (mailbox.mServerId != null)
                        && mailbox.mServerId.equals(Mailbox.ORANGE_IMAP_TRASHBOX_SERVER_ID)) {
                        deleteFromTrash = true;
                    }
                    //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan

                    // Dispatch here for specific change types
                    if (deleteFromTrash) {
                        // Move message to trash
                        processPendingDeleteFromTrash(remoteStore, mailbox, oldMessage);
                    }

                    // Finally, delete the update
                    Uri uri = ContentUris.withAppendedId(EmailContent.Message.DELETED_CONTENT_URI,
                            oldMessage.mId);
                    context.getContentResolver().delete(uri, null, null);
                }
            }
        } catch (MessagingException me) {
            // Presumably an error here is an account connection failure, so there is
            // no point in continuing through the rest of the pending updates.
            if (MailActivityEmail.DEBUG) {
                LogUtils.d(Logging.LOG_TAG, "Unable to process pending delete for id="
                        + lastMessageId + ": " + me);
            }
        } finally {
            deletes.close();
        }
    }

    /**
     * Scan for messages that are in Sent, and are in need of upload,
     * and send them to the server. "In need of upload" is defined as:
     *  serverId == null (no UID has been assigned)
     * or
     *  message is in the updated list
     *
     * Note we also look for messages that are moving from drafts->outbox->sent. They never
     * go through "drafts" or "outbox" on the server, so we hang onto these until they can be
     * uploaded directly to the Sent folder.
     */
    private static void processPendingUploadsSynchronous(Context context, Account account,
            Store remoteStore, String[] accountIdArgs, boolean manualSync) {
        ContentResolver resolver = context.getContentResolver();
        // Find the Sent folder (since that's all we're uploading for now
        // TODO: Upsync for all folders? (In case a user moves mail from Sent before it is
        // handled. Also, this would generically solve allowing drafts to upload.)

        long lastMessageId = -1;
        //[FEATURE]-Mod-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        Cursor mailboxes;
        // Find the Sent folder (since that's all we're uploading for now
        if (EmailApplication.isOrangeImapFeatureOn() && Account.isOrangeImapAccount(context, account.mId)) {
            mailboxes = resolver.query(Mailbox.CONTENT_URI, Mailbox.ID_PROJECTION,
                    MailboxColumns.ACCOUNT_KEY + "=?"
                    + " and " + MailboxColumns.SERVER_ID + "=?",
                    new String[]{Long.toString(account.mId), Mailbox.ORANGE_IMAP_SENTBOX_SERVER_ID}, null);
        } else {
            mailboxes = resolver.query(Mailbox.CONTENT_URI, Mailbox.ID_PROJECTION,
                    MailboxColumns.ACCOUNT_KEY + "=?"
                    + " and " + MailboxColumns.TYPE + "=" + Mailbox.TYPE_SENT,
                    accountIdArgs, null);
        }
        //[FEATURE]-Mod-END by TSNJ,Zhenhua.Fan
        try {
            while (mailboxes.moveToNext()) {
                long mailboxId = mailboxes.getLong(Mailbox.ID_PROJECTION_COLUMN);
                String[] mailboxKeyArgs = new String[] { Long.toString(mailboxId) };
                // Demand load mailbox
                Mailbox mailbox = null;

                // First handle the "new" messages (serverId == null)
                Cursor upsyncs1 = resolver.query(EmailContent.Message.CONTENT_URI,
                        EmailContent.Message.ID_PROJECTION,
                        MessageColumns.MAILBOX_KEY + "=?"
                        + " and (" + MessageColumns.SERVER_ID + " is null"
                        + " or " + MessageColumns.SERVER_ID + "=''" + ")",
                        mailboxKeyArgs,
                        null);
                try {
                    while (upsyncs1.moveToNext()) {
                        // Load the remote store if it will be needed
                        if (remoteStore == null) {
                            remoteStore = Store.getInstance(account, context);
                        }
                        // Load the mailbox if it will be needed
                        if (mailbox == null) {
                            mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
                            if (mailbox == null) {
                                continue; // Mailbox removed. Move to the next message.
                            }
                        }
                        // upsync the message
                        long id = upsyncs1.getLong(EmailContent.Message.ID_PROJECTION_COLUMN);
                        lastMessageId = id;
                        processUploadMessage(context, remoteStore, mailbox, id, manualSync);
                    }
                } finally {
                    if (upsyncs1 != null) {
                        upsyncs1.close();
                    }
                    if (remoteStore != null) {
                        remoteStore.closeConnections();
                    }
                }
            }
        } catch (MessagingException me) {
            // Presumably an error here is an account connection failure, so there is
            // no point in continuing through the rest of the pending updates.
            if (MailActivityEmail.DEBUG) {
                LogUtils.d(Logging.LOG_TAG, "Unable to process pending upsync for id="
                        + lastMessageId + ": " + me);
            }
        } finally {
            if (mailboxes != null) {
                mailboxes.close();
            }
        }
    }

    //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
    /**
     * Scan for messages that are in Draft, and are in need of upload,
     * and send them to the server.  "In need of upload" is defined as:
     *  serverId == null (no UID has been assigned)
     * or
     *  message is in the updated list
     *
     * @param account
     * @param resolver
     * @param accountIdArgs
     */
    private static void processPendingUploadsDraftSynchronous(Context context,Account account,
             String[] accountIdArgs, boolean manualSync) {
        ContentResolver resolver = context.getContentResolver();
        Long draftboxId = Mailbox.getOrangeImapDraftboxId(context, account.mId);
        Cursor mailboxes;
        if (draftboxId != -1) {
            // Find the Draft folder (since that's all we're uploading for now
            mailboxes = resolver.query(Mailbox.CONTENT_URI, Mailbox.ID_PROJECTION,
                    MailboxColumns.ID + "="
                    + draftboxId,
                    null, null);
        } else {
            return;
        }
        long lastMessageId = -1;
        try {
            // Defer setting up the store until we know we need to access it
            Store remoteStore = null;
            while (mailboxes.moveToNext()) {
                long mailboxId = mailboxes.getLong(Mailbox.ID_PROJECTION_COLUMN);
                String[] mailboxKeyArgs = new String[] { Long.toString(mailboxId) };
                // Demand load mailbox
                Mailbox mailbox = null;

                // First handle the "new" messages (serverId == null)
                Cursor upsyncs1 = resolver.query(EmailContent.Message.CONTENT_URI,
                        EmailContent.Message.ID_PROJECTION,
                        MessageColumns.MAILBOX_KEY + "=?"
                        + " and (" + MessageColumns.SERVER_ID + " is null"
                        + " or " + MessageColumns.SERVER_ID + "=''" + ")",
                        mailboxKeyArgs,
                        null);
                try {
                    while (upsyncs1.moveToNext()) {
                        // Load the remote store if it will be needed
                        if (remoteStore == null) {
                            remoteStore = Store.getInstance(account, context);
                        }
                        // Load the mailbox if it will be needed
                        if (mailbox == null) {
                            mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
                            if (mailbox == null) {
                                continue; // Mailbox removed. Move to the next message.
                            }
                        }
                        // upsync the message
                        long id = upsyncs1.getLong(EmailContent.Message.ID_PROJECTION_COLUMN);
                        lastMessageId = id;
                        processUploadMessage(context, remoteStore, mailbox, id, manualSync);
                    }
                } finally {
                    if (upsyncs1 != null) {
                        upsyncs1.close();
                    }
                }

                // Next, handle any updates (e.g. edited in place, although this shouldn't happen)
                Cursor upsyncs2 = resolver.query(EmailContent.Message.UPDATED_CONTENT_URI,
                        EmailContent.Message.ID_PROJECTION,
                        EmailContent.MessageColumns.MAILBOX_KEY + "=?", mailboxKeyArgs,
                        null);
                try {
                    while (upsyncs2.moveToNext()) {
                        // Load the remote store if it will be needed
                        if (remoteStore == null) {
                            remoteStore = Store.getInstance(account, context);
                        }
                        // Load the mailbox if it will be needed
                        if (mailbox == null) {
                            mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
                            if (mailbox == null) {
                                continue; // Mailbox removed. Move to the next message.
                            }
                        }
                        // upsync the message
                        long id = upsyncs2.getLong(EmailContent.Message.ID_PROJECTION_COLUMN);
                        lastMessageId = id;
                        processUploadMessage(context, remoteStore, mailbox, id, manualSync);
                    }
                } finally {
                    if (upsyncs2 != null) {
                        upsyncs2.close();
                    }
                }
            }
        } catch (MessagingException me) {
            // Presumably an error here is an account connection failure, so there is
            // no point in continuing through the rest of the pending updates.
            if (MailActivityEmail.DEBUG) {
                Log.d(Logging.LOG_TAG, "Unable to process pending upsync for id="
                        + lastMessageId + ": " + me);
            }
        } finally {
            if (mailboxes != null) {
                mailboxes.close();
            }
        }
    }
    //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
    /**
     * Scan for messages that are in the Message_Updates table, look for differences that
     * we can deal with, and do the work.
     */
    private static void processPendingUpdatesSynchronous(Context context, Account account,
            Store remoteStore, String[] accountIdArgs) {
        ContentResolver resolver = context.getContentResolver();
        Cursor updates = resolver.query(EmailContent.Message.UPDATED_CONTENT_URI,
                EmailContent.Message.CONTENT_PROJECTION,
                EmailContent.MessageColumns.ACCOUNT_KEY + "=?", accountIdArgs,
                EmailContent.MessageColumns.MAILBOX_KEY);
        long lastMessageId = -1;
        try {
            // Demand load mailbox (note order-by to reduce thrashing here)
            Mailbox mailbox = null;
            // loop through messages marked as needing updates
            while (updates.moveToNext()) {
                boolean changeMoveToTrash = false;
                boolean changeRead = false;
                boolean changeFlagged = false;
                boolean changeMailbox = false;
                boolean changeAnswered = false;

                EmailContent.Message oldMessage =
                        EmailContent.getContent(context, updates, EmailContent.Message.class);
                lastMessageId = oldMessage.mId;
                EmailContent.Message newMessage =
                        EmailContent.Message.restoreMessageWithId(context, oldMessage.mId);
                if (newMessage != null) {
                    LogUtils.d("zzz","----id : " + oldMessage.mId + "--"+oldMessage.mFlagRead+"--"+newMessage.mFlagRead);
                    LogUtils.d("zzz","----id : " + oldMessage.mId + "--"+oldMessage.mFlagFavorite+"--"+newMessage.mFlagFavorite);
                    mailbox = Mailbox.restoreMailboxWithId(context, newMessage.mMailboxKey);
                    if (mailbox == null) {
                        continue; // Mailbox removed. Move to the next message.
                    }
                    if (oldMessage.mMailboxKey != newMessage.mMailboxKey) {
                        if (mailbox.mType == Mailbox.TYPE_TRASH) {
                            changeMoveToTrash = true;
                        } else {
                            changeMailbox = true;
                        }
                    }
                    changeRead = oldMessage.mFlagRead != newMessage.mFlagRead;
                    changeFlagged = oldMessage.mFlagFavorite != newMessage.mFlagFavorite;
                    changeAnswered = (oldMessage.mFlags & EmailContent.Message.FLAG_REPLIED_TO) !=
                            (newMessage.mFlags & EmailContent.Message.FLAG_REPLIED_TO);
                }

                // Load the remote store if it will be needed
                if (remoteStore == null &&
                        (changeMoveToTrash || changeRead || changeFlagged || changeMailbox ||
                                changeAnswered)) {
                    remoteStore = Store.getInstance(account, context);
                }

                // Dispatch here for specific change types
                if (changeMoveToTrash) {
                    // Move message to trash
                    processPendingMoveToTrash(context, remoteStore, mailbox, oldMessage,
                            newMessage);
                } else if (changeRead || changeFlagged || changeMailbox || changeAnswered) {
                    processPendingDataChange(context, remoteStore, mailbox, changeRead,
                            changeFlagged, changeMailbox, changeAnswered, oldMessage, newMessage);
                }

                // Finally, delete the update
                Uri uri = ContentUris.withAppendedId(EmailContent.Message.UPDATED_CONTENT_URI,
                        oldMessage.mId);
                resolver.delete(uri, null, null);
            }

        } catch (MessagingException me) {
            // Presumably an error here is an account connection failure, so there is
            // no point in continuing through the rest of the pending updates.
            if (MailActivityEmail.DEBUG) {
                LogUtils.d(Logging.LOG_TAG, "Unable to process pending update for id="
                        + lastMessageId + ": " + me);
            }
        } finally {
            updates.close();
        }
    }

    /**
     * Upsync an entire message. This must also unwind whatever triggered it (either by
     * updating the serverId, or by deleting the update record, or it's going to keep happening
     * over and over again.
     *
     * Note: If the message is being uploaded into an unexpected mailbox, we *do not* upload.
     * This is to avoid unnecessary uploads into the trash. Although the caller attempts to select
     * only the Drafts and Sent folders, this can happen when the update record and the current
     * record mismatch. In this case, we let the update record remain, because the filters
     * in processPendingUpdatesSynchronous() will pick it up as a move and handle it (or drop it)
     * appropriately.
     *
     * @param mailbox the actual mailbox
     */
    private static void processUploadMessage(Context context, Store remoteStore, Mailbox mailbox,
            long messageId, boolean manualSync)
            throws MessagingException {
        EmailContent.Message newMessage =
                EmailContent.Message.restoreMessageWithId(context, messageId);
        final boolean deleteUpdate;
        if (newMessage == null) {
            deleteUpdate = true;
            LogUtils.d(Logging.LOG_TAG, "Upsync failed for null message, id=" + messageId);
        } else if (mailbox.mType == Mailbox.TYPE_DRAFTS) {
            deleteUpdate = false;
            LogUtils.d(Logging.LOG_TAG, "Upsync skipped for mailbox=drafts, id=" + messageId);
        } else if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
            deleteUpdate = false;
            LogUtils.d(Logging.LOG_TAG, "Upsync skipped for mailbox=outbox, id=" + messageId);
        } else if (mailbox.mType == Mailbox.TYPE_TRASH) {
            deleteUpdate = false;
            LogUtils.d(Logging.LOG_TAG, "Upsync skipped for mailbox=trash, id=" + messageId);
        } else if (newMessage.mMailboxKey != mailbox.mId) {
            deleteUpdate = false;
            LogUtils.d(Logging.LOG_TAG, "Upsync skipped; mailbox changed, id=" + messageId);
        } else {
            LogUtils.d(Logging.LOG_TAG, "Upsync triggered for message id=" + messageId);
            deleteUpdate =
                    processPendingAppend(context, remoteStore, mailbox, newMessage, manualSync);
        }
        if (deleteUpdate) {
            // Finally, delete the update (if any)
            Uri uri = ContentUris.withAppendedId(
                    EmailContent.Message.UPDATED_CONTENT_URI, messageId);
            context.getContentResolver().delete(uri, null, null);
        }
    }

    /**
     * Upsync changes to read, flagged, or mailbox
     *
     * @param remoteStore the remote store for this mailbox
     * @param mailbox the mailbox the message is stored in
     * @param changeRead whether the message's read state has changed
     * @param changeFlagged whether the message's flagged state has changed
     * @param changeMailbox whether the message's mailbox has changed
     * @param oldMessage the message in it's pre-change state
     * @param newMessage the current version of the message
     */
    private static void processPendingDataChange(final Context context, Store remoteStore,
            Mailbox mailbox, boolean changeRead, boolean changeFlagged, boolean changeMailbox,
            boolean changeAnswered, EmailContent.Message oldMessage,
            final EmailContent.Message newMessage) throws MessagingException {
        // New mailbox is the mailbox this message WILL be in (same as the one it WAS in if it isn't
        // being moved
        Mailbox newMailbox = mailbox;
        // Mailbox is the original remote mailbox (the one we're acting on)
        mailbox = getRemoteMailboxForMessage(context, oldMessage);

        // 0. No remote update if the message is local-only
        if (newMessage.mServerId == null || newMessage.mServerId.equals("")
                || newMessage.mServerId.startsWith(LOCAL_SERVERID_PREFIX) || (mailbox == null)) {
            return;
        }

        // 1. No remote update for DRAFTS or OUTBOX
        if (mailbox.mType == Mailbox.TYPE_DRAFTS || mailbox.mType == Mailbox.TYPE_OUTBOX) {
            return;
        }

        // 2. Open the remote store & folder
        Folder remoteFolder = remoteStore.getFolder(mailbox.mServerId);
        if (!remoteFolder.exists()) {
            return;
        }
        remoteFolder.open(OpenMode.READ_WRITE);
        if (remoteFolder.getMode() != OpenMode.READ_WRITE) {
            return;
        }

        // 3. Finally, apply the changes to the message
        Message remoteMessage = remoteFolder.getMessage(newMessage.mServerId);
        if (remoteMessage == null) {
            return;
        }
        if (MailActivityEmail.DEBUG) {
            LogUtils.d(Logging.LOG_TAG,
                    "Update for msg id=" + newMessage.mId
                    + " read=" + newMessage.mFlagRead
                    + " flagged=" + newMessage.mFlagFavorite
                    + " answered="
                    + ((newMessage.mFlags & EmailContent.Message.FLAG_REPLIED_TO) != 0)
                    + " new mailbox=" + newMessage.mMailboxKey);
        }
        Message[] messages = new Message[] { remoteMessage };
        if (changeRead) {
            remoteFolder.setFlags(messages, FLAG_LIST_SEEN, newMessage.mFlagRead);
        }
        if (changeFlagged) {
            remoteFolder.setFlags(messages, FLAG_LIST_FLAGGED, newMessage.mFlagFavorite);
        }
        if (changeAnswered) {
            remoteFolder.setFlags(messages, FLAG_LIST_ANSWERED,
                    (newMessage.mFlags & EmailContent.Message.FLAG_REPLIED_TO) != 0);
        }
        if (changeMailbox) {
            Folder toFolder = remoteStore.getFolder(newMailbox.mServerId);
            if (!remoteFolder.exists()) {
                return;
            }
            // We may need the message id to search for the message in the destination folder
            remoteMessage.setMessageId(newMessage.mMessageId);
            // Copy the message to its new folder
            remoteFolder.copyMessages(messages, toFolder, new MessageUpdateCallbacks() {
                @Override
                public void onMessageUidChange(Message message, String newUid) {
                    ContentValues cv = new ContentValues();
                    cv.put(MessageColumns.SERVER_ID, newUid);
                    // We only have one message, so, any updates _must_ be for it. Otherwise,
                    // we'd have to cycle through to find the one with the same server ID.
                    context.getContentResolver().update(ContentUris.withAppendedId(
                            EmailContent.Message.CONTENT_URI, newMessage.mId), cv, null, null);
                }

                @Override
                public void onMessageNotFound(Message message) {
                }
            });
            // Delete the message from the remote source folder
            remoteMessage.setFlag(Flag.DELETED, true);
            remoteFolder.expunge();
        }
        remoteFolder.close(false);
    }

    /**
     * Process a pending trash message command.
     *
     * @param remoteStore the remote store we're working in
     * @param newMailbox The local trash mailbox
     * @param oldMessage The message copy that was saved in the updates shadow table
     * @param newMessage The message that was moved to the mailbox
     */
    private static void processPendingMoveToTrash(final Context context, Store remoteStore,
            Mailbox newMailbox, EmailContent.Message oldMessage,
            final EmailContent.Message newMessage) throws MessagingException {

        // 0. No remote move if the message is local-only
        if (newMessage.mServerId == null || newMessage.mServerId.equals("")
                || newMessage.mServerId.startsWith(LOCAL_SERVERID_PREFIX)) {
            return;
        }

        // 1. Escape early if we can't find the local mailbox
        // TODO smaller projection here
        Mailbox oldMailbox = getRemoteMailboxForMessage(context, oldMessage);
        if (oldMailbox == null) {
            // can't find old mailbox, it may have been deleted.  just return.
            return;
        }
        // 2. We don't support delete-from-trash here
        if (oldMailbox.mType == Mailbox.TYPE_TRASH) {
            return;
        }

        // The rest of this method handles server-side deletion

        // 4.  Find the remote mailbox (that we deleted from), and open it
        Folder remoteFolder = remoteStore.getFolder(oldMailbox.mServerId);
        if (!remoteFolder.exists()) {
            return;
        }

        remoteFolder.open(OpenMode.READ_WRITE);
        if (remoteFolder.getMode() != OpenMode.READ_WRITE) {
            remoteFolder.close(false);
            return;
        }

        // 5. Find the remote original message
        Message remoteMessage = remoteFolder.getMessage(oldMessage.mServerId);
        if (remoteMessage == null) {
            remoteFolder.close(false);
            return;
        }

        // 6. Find the remote trash folder, and create it if not found
        Folder remoteTrashFolder = remoteStore.getFolder(newMailbox.mServerId);
        if (!remoteTrashFolder.exists()) {
            /*
             * If the remote trash folder doesn't exist we try to create it.
             */
            remoteTrashFolder.create(FolderType.HOLDS_MESSAGES);
        }

        // 7. Try to copy the message into the remote trash folder
        // Note, this entire section will be skipped for POP3 because there's no remote trash
        if (remoteTrashFolder.exists()) {
            /*
             * Because remoteTrashFolder may be new, we need to explicitly open it
             */
            remoteTrashFolder.open(OpenMode.READ_WRITE);
            if (remoteTrashFolder.getMode() != OpenMode.READ_WRITE) {
                remoteFolder.close(false);
                remoteTrashFolder.close(false);
                return;
            }

            remoteFolder.copyMessages(new Message[] { remoteMessage }, remoteTrashFolder,
                    new Folder.MessageUpdateCallbacks() {
                @Override
                public void onMessageUidChange(Message message, String newUid) {
                    // update the UID in the local trash folder, because some stores will
                    // have to change it when copying to remoteTrashFolder
                    ContentValues cv = new ContentValues();
                    cv.put(MessageColumns.SERVER_ID, newUid);
                    context.getContentResolver().update(newMessage.getUri(), cv, null, null);
                }

                /**
                 * This will be called if the deleted message doesn't exist and can't be
                 * deleted (e.g. it was already deleted from the server.)  In this case,
                 * attempt to delete the local copy as well.
                 */
                @Override
                public void onMessageNotFound(Message message) {
                    context.getContentResolver().delete(newMessage.getUri(), null, null);
                }
            });
            remoteTrashFolder.close(false);
        }

        // 8. Delete the message from the remote source folder
        remoteMessage.setFlag(Flag.DELETED, true);
        remoteFolder.expunge();
        remoteFolder.close(false);
    }

    /**
     * Process a pending trash message command.
     *
     * @param remoteStore the remote store we're working in
     * @param oldMailbox The local trash mailbox
     * @param oldMessage The message that was deleted from the trash
     */
    private static void processPendingDeleteFromTrash(Store remoteStore,
            Mailbox oldMailbox, EmailContent.Message oldMessage)
            throws MessagingException {
        //[FEATURE]-Mod-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        boolean isOrangeImapTrash = false;
        if (EmailApplication.isOrangeImapFeatureOn()) {
            isOrangeImapTrash = oldMailbox.mServerId.equals(Mailbox.ORANGE_IMAP_TRASHBOX_SERVER_ID);
        }
        // 1. We only support delete-from-trash here
        if (oldMailbox.mType != Mailbox.TYPE_TRASH&& !isOrangeImapTrash) {
            return;
        }
        //[FEATURE]-Mod-END by TSNJ,Zhenhua.Fan
        // 2.  Find the remote trash folder (that we are deleting from), and open it
        Folder remoteTrashFolder = remoteStore.getFolder(oldMailbox.mServerId);
        if (!remoteTrashFolder.exists()) {
            return;
        }

        remoteTrashFolder.open(OpenMode.READ_WRITE);
        if (remoteTrashFolder.getMode() != OpenMode.READ_WRITE) {
            remoteTrashFolder.close(false);
            return;
        }

        // 3. Find the remote original message
        Message remoteMessage = remoteTrashFolder.getMessage(oldMessage.mServerId);
        if (remoteMessage == null) {
            remoteTrashFolder.close(false);
            return;
        }

        // 4. Delete the message from the remote trash folder
        remoteMessage.setFlag(Flag.DELETED, true);
        remoteTrashFolder.expunge();
        remoteTrashFolder.close(false);
    }

    /**
     * Process a pending append message command. This command uploads a local message to the
     * server, first checking to be sure that the server message is not newer than
     * the local message.
     *
     * @param remoteStore the remote store we're working in
     * @param mailbox The mailbox we're appending to
     * @param message The message we're appending
     * @param manualSync True if this is a manual sync (changes upsync behavior)
     * @return true if successfully uploaded
     */
    private static boolean processPendingAppend(Context context, Store remoteStore, Mailbox mailbox,
            EmailContent.Message message, boolean manualSync)
            throws MessagingException {
        boolean updateInternalDate = false;
        boolean updateMessage = false;
        boolean deleteMessage = false;

        // 1. Find the remote folder that we're appending to and create and/or open it
        Folder remoteFolder = remoteStore.getFolder(mailbox.mServerId);
        if (!remoteFolder.exists()) {
            if (!remoteFolder.create(FolderType.HOLDS_MESSAGES)) {
                // This is a (hopefully) transient error and we return false to try again later
                return false;
            }
        }
        remoteFolder.open(OpenMode.READ_WRITE);
        if (remoteFolder.getMode() != OpenMode.READ_WRITE) {
            return false;
        }

        // 2. If possible, load a remote message with the matching UID
        Message remoteMessage = null;
        if (message.mServerId != null && message.mServerId.length() > 0) {
            remoteMessage = remoteFolder.getMessage(message.mServerId);
            /**
             * TCT: Get remote message from server according to messageId. To avoid
             * duplicate mail in sent box. @{
             */
        } else {
            // 2b. Load a remote message with the matching MessageId.
            remoteMessage = ((ImapFolder) remoteFolder).getRemoteMessage(message.mMessageId);
            if (remoteMessage != null) {
                message.mServerId = remoteMessage.getUid();
            }
        }
        /** @} */

        // 3. If a remote message could not be found, upload our local message
        if (remoteMessage == null) {
            // TODO:
            // if we have a serverId and remoteMessage is still null, then probably the message
            // has been deleted and we should delete locally.
            // 3a. Create a legacy message to upload
            Message localMessage = LegacyConversions.makeMessage(context, message);
            // 3b. Upload it
            //FetchProfile fp = new FetchProfile();
            //fp.add(FetchProfile.Item.BODY);
            // Note that this operation will assign the Uid to localMessage
            remoteFolder.appendMessage(context, localMessage, manualSync /* no timeout */);

            // 3b. And record the UID from the server
            message.mServerId = localMessage.getUid();
            updateInternalDate = true;
            updateMessage = true;
        } else {
            // 4. If the remote message exists we need to determine which copy to keep.
            // TODO:
            // I don't see a good reason we should be here. If the message already has a serverId,
            // then we should be handling it in processPendingUpdates(),
            // not processPendingUploads()
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            remoteFolder.fetch(new Message[] { remoteMessage }, fp, null);
            Date localDate = new Date(message.mServerTimeStamp);
            Date remoteDate = remoteMessage.getInternalDate();
            if (remoteDate != null && remoteDate.compareTo(localDate) > 0) {
                // 4a. If the remote message is newer than ours we'll just
                // delete ours and move on. A sync will get the server message
                // if we need to be able to see it.
                deleteMessage = true;
            } else {
                // 4b. Otherwise we'll upload our message and then delete the remote message.
                //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
                // reset server id before upload
                message.mServerId = null;
                //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
                // Create a legacy message to upload
                // TODO: This strategy has a problem: This will create a second message,
                // so that at least temporarily, we will have two messages for what the
                // user would think of as one.
                Message localMessage = LegacyConversions.makeMessage(context, message);

                // 4c. Upload it
                fp.clear();
                fp = new FetchProfile();
                fp.add(FetchProfile.Item.BODY);
                remoteFolder.appendMessage(context, localMessage, manualSync /* no timeout */);

                // 4d. Record the UID and new internalDate from the server
                message.mServerId = localMessage.getUid();
                updateInternalDate = true;
                updateMessage = true;

                // 4e. And delete the old copy of the message from the server.
                remoteMessage.setFlag(Flag.DELETED, true);
            }
        }

        // 5. If requested, Best-effort to capture new "internaldate" from the server
        if (updateInternalDate && message.mServerId != null) {
            try {
                Message remoteMessage2 = remoteFolder.getMessage(message.mServerId);
                if (remoteMessage2 != null) {
                    FetchProfile fp2 = new FetchProfile();
                    fp2.add(FetchProfile.Item.ENVELOPE);
                    remoteFolder.fetch(new Message[] { remoteMessage2 }, fp2, null);
                    final Date remoteDate = remoteMessage2.getInternalDate();
                    if (remoteDate != null) {
                        message.mServerTimeStamp = remoteMessage2.getInternalDate().getTime();
                        updateMessage = true;
                    }
                }
            } catch (MessagingException me) {
                // skip it - we can live without this
            }
        }

        //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        remoteFolder.expunge();
        remoteFolder.close(false);
        //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan

        // 6. Perform required edits to local copy of message
        if (deleteMessage || updateMessage) {
            Uri uri = ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI, message.mId);
            ContentResolver resolver = context.getContentResolver();
            if (deleteMessage) {
                resolver.delete(uri, null, null);
            } else if (updateMessage) {
                ContentValues cv = new ContentValues();
                cv.put(MessageColumns.SERVER_ID, message.mServerId);
                cv.put(MessageColumns.SERVER_TIMESTAMP, message.mServerTimeStamp);
                resolver.update(uri, cv, null, null);
            }
        }

        return true;
    }

    /**
     * A message and numeric uid that's easily sortable
     */
    private static class SortableMessage {
        private final Message mMessage;
        private final long mUid;

        SortableMessage(Message message, long uid) {
            mMessage = message;
            mUid = uid;
        }
    }

    private static int searchMailboxImpl(final Context context, final long accountId,
            final SearchParams searchParams, final long destMailboxId) throws MessagingException {
        final Account account = Account.restoreAccountWithId(context, accountId);
        final Mailbox mailbox = Mailbox.restoreMailboxWithId(context, searchParams.mMailboxId);
        final Mailbox destMailbox = Mailbox.restoreMailboxWithId(context, destMailboxId);
        if (account == null || mailbox == null || destMailbox == null) {
            LogUtils.d(Logging.LOG_TAG, "Attempted search for " + searchParams
                    + " but account or mailbox information was missing");
            return 0;
        }

        // Tell UI that we're loading messages
        final ContentValues statusValues = new ContentValues(2);
        // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
        statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.LIVE_QUERY);
        destMailbox.update(context, statusValues);
        Store remoteStore = null;
        try {
            remoteStore = Store.getInstance(account, context);
            final Folder remoteFolder = remoteStore.getFolder(mailbox.mServerId);
            remoteFolder.open(OpenMode.READ_WRITE);

            SortableMessage[] sortableMessages = new SortableMessage[0];
            if (searchParams.mOffset == 0) {
                // Get the "bare" messages (basically uid)
                final Message[] remoteMessages = remoteFolder.getMessages(searchParams, null);
                final int remoteCount = remoteMessages.length;
                if (remoteCount > 0) {
                    sortableMessages = new SortableMessage[remoteCount];
                    int i = 0;
                    for (Message msg : remoteMessages) {
                        sortableMessages[i++] = new SortableMessage(msg, Long.parseLong(msg.getUid()));
                    }
                    // Sort the uid's, most recent first
                    // Note: Not all servers will be nice and return results in the order of request;
                    // those that do will see messages arrive from newest to oldest
                    Arrays.sort(sortableMessages, new Comparator<SortableMessage>() {
                        @Override
                        public int compare(SortableMessage lhs, SortableMessage rhs) {
                            return lhs.mUid > rhs.mUid ? -1 : lhs.mUid < rhs.mUid ? 1 : 0;
                        }
                    });
                    sSearchResults.put(accountId, sortableMessages);
                }
            } else {
                // It seems odd for this to happen, but if the previous query returned zero results,
                // but the UI somehow still attempted to load more, then sSearchResults will have
                // a null value for this account. We need to handle this below.
                sortableMessages = sSearchResults.get(accountId);
            }

            final int numSearchResults = (sortableMessages != null ? sortableMessages.length : 0);
            final int numToLoad =
                    Math.min(numSearchResults - searchParams.mOffset, searchParams.mLimit);
            destMailbox.updateMessageCount(context, numSearchResults);
            if (numToLoad <= 0) {
                //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 ADD_S
                // Tell UI that we're done loading messages
                statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
                statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
                //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_S
                statusValues.put(Mailbox.UI_LAST_SYNC_RESULT, com.tct.mail.utils.UIProvider.UIPROVIDER_LASTSYNCRESULT_SUCCESS);
                //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_E
                destMailbox.update(context, statusValues);
                //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 ADD_E
                return 0;
            } else {
                // It seems odd for this to happen, but if the previous query returned zero results,
                // but the UI somehow still attempted to load more, then sSearchResults will have
                // a null value for this account. We need to handle this below.
                sortableMessages = sSearchResults.get(accountId);
            }

            final ArrayList<Message> messageList = new ArrayList<Message>();
            for (int i = searchParams.mOffset; i < numToLoad + searchParams.mOffset; i++) {
                messageList.add(sortableMessages[i].mMessage);
            }
            // First fetch FLAGS and ENVELOPE. In a second pass, we'll fetch STRUCTURE and
            // the first body part.
            final FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.FLAGS);
            fp.add(FetchProfile.Item.ENVELOPE);

            Message[] messageArray = messageList.toArray(new Message[messageList.size()]);

            // TODO: We are purposely processing messages with a MessageRetrievalListener here, rather
            // than just walking the messageArray after the operation completes. This is so that we can
            // immediately update the database so the user can see something useful happening, even
            // if the message body has not yet been fetched.
            // There are some issues with this approach:
            // 1. It means that we have a single thread doing both network and database operations, and
            // either can block the other. The database updates could slow down the network reads,
            // keeping our network connection open longer than is really necessary.
            // 2. We still load all of this data into messageArray, even though it's not used.
            // It would be nicer if we had one thread doing the network operation, and a separate
            // thread consuming that data and performing the appropriate database work, then discarding
            // the data as soon as it is no longer needed. This would reduce our memory footprint and
            // potentially allow our network operation to complete faster.
            remoteFolder.fetch(messageArray, fp, new MessageRetrievalListener() {
                @Override
                public void messageRetrieved(Message message) {
                    try {
                        EmailContent.Message localMessage = new EmailContent.Message();

                        // Copy the fields that are available into the message
                        LegacyConversions.updateMessageFields(localMessage,
                                message, account.mId, mailbox.mId);
                        //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
                        long mailboxType = Mailbox.getMailboxType(context, localMessage.mMailboxKey);
                        if (mailboxType == Mailbox.TYPE_DRAFTS || mailboxType == Mailbox.TYPE_OUTBOX || mailboxType == Mailbox.TYPE_SENT) {
                            localMessage.mDisplayName = UiUtilities.makeDisplayName(context, localMessage.mTo, localMessage.mCc, localMessage.mBcc);
                        } else {
                            Address[] from = message.getFrom();
                            if (from != null && from.length > 0) {
                                localMessage.mDisplayName = from[0].toFriendly();
                            }
                        }
                        //[FEATURE]-ADD-END by TSNJ.wei huang
                        // Save off the mailbox that this message *really* belongs in.
                        // We need this information if we need to do more lookups
                        // (like loading attachments) for this message. See b/11294681
                        localMessage.mMainMailboxKey = localMessage.mMailboxKey;
                        localMessage.mMailboxKey = destMailboxId;
                        // We load 50k or so; maybe it's complete, maybe not...
                        int flag = EmailContent.Message.FLAG_LOADED_COMPLETE;
                        // We store the serverId of the source mailbox into protocolSearchInfo
                        // This will be used by loadMessageForView, etc. to use the proper remote
                        // folder
                        localMessage.mProtocolSearchInfo = mailbox.mServerId;
                        // Commit the message to the local store
                        Utilities.saveOrUpdate(localMessage, context);
                    } catch (MessagingException me) {
                        LogUtils.e(Logging.LOG_TAG, me,
                                "Error while copying downloaded message.");
                    } catch (Exception e) {
                        LogUtils.e(Logging.LOG_TAG, e,
                                "Error while storing downloaded message.");
                    }
                }

                @Override
                public void loadAttachmentProgress(int progress) {
                }
            });

            // Now load the structure for all of the messages:
            fp.clear();
            fp.add(FetchProfile.Item.STRUCTURE);
            remoteFolder.fetch(messageArray, fp, null);

            // Finally, load the first body part (i.e. message text).
            // This means attachment contents are not yet loaded, but that's okay,
            // we'll load them as needed, same as in synced messages.
            Message[] oneMessageArray = new Message[1];
            for (Message message : messageArray) {
                // Build a list of parts we are interested in. Text parts will be downloaded
                // right now, attachments will be left for later.
                ArrayList<Part> viewables = new ArrayList<Part>();
                ArrayList<Part> attachments = new ArrayList<Part>();
                MimeUtility.collectParts(message, viewables, attachments);
                // Download the viewables immediately
                oneMessageArray[0] = message;
                for (Part part : viewables) {
                    fp.clear();
                    fp.add(part);
                    remoteFolder.fetch(oneMessageArray, fp, null);
                }
                // Store the updated message locally and mark it fully loaded
                Utilities.copyOneMessageToProvider(context, message, account, destMailbox,
                        EmailContent.Message.FLAG_LOADED_COMPLETE);
            }

            /** TCT: Set and update mailbox flag. @{ */
            int currentCount = searchParams.mOffset + messageArray.length;
            boolean allMessagesLoaded = false;
            if (currentCount >= numSearchResults) {
                allMessagesLoaded = true;
            }
            destMailbox.updateAllMessageDownloadFlag(context, allMessagesLoaded);
            /** @} */
            return numSearchResults;
        } finally { /// TCT: move the UI update code to finally block to avoid this operation lost.
            // Tell UI that we're done loading messages 	1843
            statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
            statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
            //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_S
            statusValues.put(Mailbox.UI_LAST_SYNC_RESULT, com.tct.mail.utils.UIProvider.UIPROVIDER_LASTSYNCRESULT_SUCCESS);
            //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_E
            destMailbox.update(context, statusValues);
            if (remoteStore != null) {
                remoteStore.closeConnections();
            }
        }
    }

    //[BUGFIX]-ADD-BEGIN by TSNJ.wei huang 08/21/2015 PR1070807
    public static boolean isSyncByTime(String address){
        String[] domainString = address.split("@");
        if(domainString[1].trim().equals("claro.net.do")){
            return false;
        }
        return true;
    }
    //[BUGFIX]-ADD-END by TSNJ.wei huang 08/21/2015 PR1070807
}
