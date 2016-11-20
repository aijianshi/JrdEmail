/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-50003 2014/11/04   zhaotianyong   Modify the package conflict
 *BUGFIX-930488  2015/02/13   zheng.zou      [Scenario Test][Email][REG]Folder can auto sync when not select sync in setting.
 *BUGFIX-939950  2015/3/5     junwei-xu      [Email]Trash box of exchange can not sync
 *BUGFIX-942249  2015/3/12    junwei-xu      [Android5.0][Exchange]It should load emails for all folders after creating an exchange account
 *CR_585337      2015-09-16  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-677793   2015-09-29   zheng.zou      [Email]Print test log to sd card[Email]Print test log to sd card
 *BUGFIX-723337   2015-10-14   zheng.zou      [Email]Print test log to sd card
 *BUGFIX-944797  2015-11-26   jian.xu         [Android L][Email]Retry notification not disappear after reconnect wifi
 *BUGFIX-718891 2015/11/23      zheng.zou      Email sync frequency for Exchange/imap/pop3 is not correctly
 *BUGFIX-1752988 2016/03/09   xiangnan.zhou   [jrdlogger]com.tct.exchange Java (JE)
 *BUGFIX-1929553 2016/04/18   zheng.zou       [onetouch feedback][com.tct.calendar][Version  v5.1.4.1.0213.0_0324][Other]
 ============================================================================
 */
package com.tct.exchange.eas;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;

import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.utility.Utility;
//TS: MOD by zhaotianyong for CONFLICT_50003 START
//import com.tct.mail.provider.UIProvider;
import com.tct.mail.utils.FileUtils;
import com.tct.mail.utils.UIProvider;
//TS: MOD by zhaotianyong for CONFLICT_50003 END
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.CommandStatusException;
import com.tct.exchange.Eas;
import com.tct.exchange.EasResponse;
import com.tct.exchange.service.EasService;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class EasFullSyncOperation extends EasOperation {
    private final static String TAG = LogUtils.TAG;

    private final static int RESULT_SUCCESS = 0;
    private final static int RESULT_SECURITY_HOLD = -100;
    public static final int SEND_FAILED = 1;
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
    //NOTE:we just care the status column.
    public static final String MAILBOX_KEY_AND_NOT_SEND_FAILED =
            EmailContent.MessageColumns.MAILBOX_KEY + "=? and (" +
                    //EmailContent.SyncColumns.SERVER_ID + " is null or " +
                    EmailContent.MessageColumns.SENDING_STATUS +"!=" +Message.MAIL_IN_FAILED_STATUS +
                    ')';
    public static final String MAIL_CREATE_TIME_DESC_ORDER = EmailContent.MessageColumns.TIMESTAMP +" DESC ";
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
    /**
     * The content authorities that can be synced for EAS accounts. Initialization must wait until
     * after we have a chance to call {@link EmailContent#init} (and, for future content types,
     * possibly other initializations) because that's how we can know what the email authority is.
     */
    private static String[] AUTHORITIES_TO_SYNC;
    static {
        // Statically initialize the authorities we'll sync.
        AUTHORITIES_TO_SYNC = new String[] {
                EmailContent.AUTHORITY,
                CalendarContract.AUTHORITY,
                ContactsContract.AUTHORITY
        };
    }

    final Bundle mSyncExtras;
    Set<String> mAuthsToSync;

    public EasFullSyncOperation(final Context context, final long accountId,
                                final Bundle syncExtras) {
        super(context, accountId);
        mSyncExtras = syncExtras;
    }

    @Override
    protected String getCommand() {
        // This is really a container operation, its performOperation() actually just creates and
        // performs a bunch of other operations. It doesn't actually do any of its own
        // requests.
        // TODO: This is kind of ugly, maybe we need a simpler base class for EasOperation that
        // does not assume that it will perform a single network operation.
        LogUtils.e(TAG, "unexpected call to EasFullSyncOperation.getCommand");
        return null;
    }

    @Override
    protected HttpEntity getRequestEntity() throws IOException {
        // This is really a container operation, its performOperation() actually just creates and
        // performs a bunch of other operations. It doesn't actually do any of its own
        // requests.
        LogUtils.e(TAG, "unexpected call to EasFullSyncOperation.getRequestEntity");
        return null;
    }

    @Override
    protected int handleResponse(final EasResponse response)
            throws IOException, CommandStatusException {
        // This is really a container operation, its performOperation() actually just creates and
        // performs a bunch of other operations. It doesn't actually do any of its own
        // requests.
        LogUtils.e(TAG, "unexpected call to EasFullSyncOperation.handleResponse");
        return RESULT_SUCCESS;
    }

    @Override
    public int performOperation() {
        // Make sure the account is loaded if it hasn't already been.
        if (!init(false)) {
            LogUtils.i(LOG_TAG, "Failed to initialize %d before operation EasFullSyncOperation",
                    getAccountId());
            FileUtils.appendLog(LOG_TAG, "Failed to initialize %d before operation EasFullSyncOperation",
                    getAccountId());      //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
            return RESULT_INITIALIZATION_FAILURE;
        }
        final android.accounts.Account amAccount = new android.accounts.Account(
                mAccount.mEmailAddress, Eas.EXCHANGE_ACCOUNT_MANAGER_TYPE);
        mAuthsToSync = EasService.getAuthoritiesToSync(amAccount, AUTHORITIES_TO_SYNC);

        // Figure out what we want to sync, based on the extras and our account sync status.
        final boolean isInitialSync = EmailContent.isInitialSyncKey(mAccount.mSyncKey);
        final long[] mailboxIds = Mailbox.getMailboxIdsFromBundle(mSyncExtras);
        final int mailboxType = mSyncExtras.getInt(Mailbox.SYNC_EXTRA_MAILBOX_TYPE,
                Mailbox.TYPE_NONE);

        final boolean isManual = mSyncExtras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        // Push only means this sync request should only refresh the ping (either because
        // settings changed, or we need to restart it for some reason).
        final boolean pushOnly = Mailbox.isPushOnlyExtras(mSyncExtras);
        // Account only means just do a FolderSync.
        final boolean accountOnly = Mailbox.isAccountOnlyExtras(mSyncExtras);
        final boolean hasCallbackMethod =
                mSyncExtras.containsKey(EmailServiceStatus.SYNC_EXTRAS_CALLBACK_METHOD);
        // A "full sync" means that we didn't request a more specific type of sync.
        // In this case we sync the folder list and all syncable folders.
        final boolean isFullSync = (!pushOnly && !accountOnly && mailboxIds == null &&
                mailboxType == Mailbox.TYPE_NONE);
        // A FolderSync is necessary for full sync, initial sync, and account only sync.
        final boolean isFolderSync = (isFullSync || isInitialSync || accountOnly);
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        final boolean haveMails = haveMailInOutBox(mAccount.mId);
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
        int result;
        //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_S
        if (!isManual && !Utility.canAutoSync(mContext, mAccount)) {
            LogUtils.i(TAG, "can not auto sync contact");
            return RESULT_SUCCESS;
        }
        //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_E

        // Now we will use a bunch of other EasOperations to actually do the sync. Note that
        // since we have overridden performOperation, this EasOperation does not have the
        // normal handling of errors and retrying that is built in. The handling of errors and
        // retries is done in each individual operation.

        // Perform a FolderSync if necessary.
        // TODO: We permit FolderSync even during security hold, because it's necessary to
        // resolve some holds. Ideally we would only do it for the holds that require it.
        if (isFolderSync) {
            LogUtils.i(TAG, "Now sync is FolderSync"); //MODIFIED by zheng.zou, 2016-04-18,BUG-1929553
            final EasFolderSync folderSync = new EasFolderSync(mContext, mAccount);
            result = folderSync.performOperation();
            if (isFatal(result)) {
                // This is a failure, abort the sync.
                LogUtils.i(TAG, "Fatal result %d on folderSync", result);
                FileUtils.appendLog(TAG, "Fatal result %d on folderSync", result);     //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
                // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
                //NOTE:give OUTBOX the chance to do the syncOutbox operation.
                if (!haveMails) {
                    return result;
                }
                // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_E
            }
        }


        // Do not permit further syncs if we're on security hold.
        if ((mAccount.mFlags & Account.FLAGS_SECURITY_HOLD) != 0) {
            LogUtils.d(TAG, "Account is on security hold %d", mAccount.getId());
            return RESULT_SECURITY_HOLD;
        }

        if (!isInitialSync) {
            LogUtils.i(TAG, "Now sync is MoveItem,isInitialSync is false"); //MODIFIED by zheng.zou, 2016-04-18,BUG-1929553
            EasMoveItems moveOp = new EasMoveItems(mContext, mAccount);
            result = moveOp.upsyncMovedMessages();
            if (isFatal(result)) {
                // This is a failure, abort the sync.
                LogUtils.i(TAG, "Fatal result %d on MoveItems", result);
                FileUtils.appendLog(TAG, "Fatal result %d on MoveItems", result);  //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
                // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
                //NOTE:give OUTBOX the chance to do the syncOutbox operation.
                if(!haveMails){
                return result;
                }
                // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_E
            }

            final EasSync upsync = new EasSync(mContext, mAccount);
            LogUtils.i(TAG, "Now sync is upSync"); //MODIFIED by zheng.zou, 2016-04-18,BUG-1929553
            result = upsync.upsync();
            if (isFatal(result)) {
                // This is a failure, abort the sync.
                LogUtils.i(TAG, "Fatal result %d on upsync", result);
                FileUtils.appendLog(TAG, "Fatal result %d on upsync", result);   //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
                // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
                //NOTE:give OUTBOX the chance to do the syncOutbox operation.
                if(!haveMails){
                return result;
                }
                // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_E
            }
        }

        if (mailboxIds != null) {
            LogUtils.i(TAG, "We have to sync the mailbox that was explicitly requested"); //MODIFIED by zheng.zou, 2016-04-18,BUG-1929553
            // Sync the mailbox that was explicitly requested.
            for (final long mailboxId : mailboxIds) {
                result = syncMailbox(mailboxId, hasCallbackMethod, isManual);
                if (isFatal(result)) {
                    // This is a failure, abort the sync.
                    LogUtils.i(TAG, "Fatal result %d on syncMailbox", result);
                    FileUtils.appendLog(TAG, "Fatal result %d on syncMailbox", result); //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
                    return result;
                }
            }
        } else if (!accountOnly && !pushOnly) {
            LogUtils.i(TAG, "We have to sync multiple folders,accountOnly is false and pushOnly is false"); //MODIFIED by zheng.zou, 2016-04-18,BUG-1929553
           // We have to sync multiple folders.
            final Cursor c;
            if (isFullSync) {
                // Full account sync includes all mailboxes that participate in system sync.
                c = Mailbox.getMailboxIdsForSync(mContext.getContentResolver(), mAccount.mId);
            } else {
                // Type-filtered sync should only get the mailboxes of a specific type.
                c = Mailbox.getMailboxIdsForSyncByType(mContext.getContentResolver(),
                        mAccount.mId, mailboxType);
            }
            if (c != null) {
                try {
                    while (c.moveToNext()) {
                        result = syncMailbox(c.getLong(0), hasCallbackMethod, isManual); //MODIFIED by zheng.zou, 2016-04-18,BUG-1929553
                        if (isFatal(result)) {
                            // This is a failure, abort the sync.
                            LogUtils.i(TAG, "Fatal result %d on syncMailbox", result);
                            FileUtils.appendLog(TAG, "Fatal result %d on syncMailbox", result);   //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
                            return result;
                        }
                    }
                } finally {
                    c.close();
                }
            }
        }

        return RESULT_SUCCESS;
    }

    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    /**
     * query if have mail in sending status from outbox.
     * @param accountId
     * @return
     */
    private boolean haveMailInOutBox(long accountId) {
        Mailbox outbox = Mailbox.restoreMailboxOfType(mContext, accountId, Mailbox.TYPE_OUTBOX);
        if (outbox == null) {
            return false;
        }
        Cursor c = mContext.getContentResolver().query(EmailContent.Message.CONTENT_URI,
                EmailContent.Message.CONTENT_PROJECTION, MAILBOX_KEY_AND_NOT_SEND_FAILED,
                new String[] {
                    Long.toString(outbox.mId)
                }, null);
        try {
            return c.getCount() > 0;
        } finally {
            c.close();
        }
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E

    private int syncMailbox(final long folderId, final boolean hasCallbackMethod,
                            final boolean isUserSync) {
        final Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, folderId);
        if (mailbox == null) {
            LogUtils.d(TAG, "Could not load folder %d", folderId);
            return EasSyncBase.RESULT_HARD_DATA_FAILURE;
        }

        if (mailbox.mAccountKey != mAccount.mId) {
            LogUtils.e(TAG, "Mailbox does not match account: mailbox %s, %s", mAccount.toString(),
                    mSyncExtras);
            return EasSyncBase.RESULT_HARD_DATA_FAILURE;
        }

        //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 ADD_S
        //do not do none-manual sync when sync frequency is set to never
        if (mAccount.getSyncInterval() == Account.CHECK_INTERVAL_NEVER && !isUserSync) {
            LogUtils.i(TAG, "eas cancel none manual sync when sync frequency is set to never");
            return EasSyncBase.RESULT_DONE;
        }
        //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 ADD_E

        if (mAuthsToSync != null && !mAuthsToSync.contains(Mailbox.getAuthority(mailbox.mType))) {
            // We are asking for an account sync, but this mailbox type is not configured for
            // sync. Do NOT treat this as a sync error for ping backoff purposes.
            return EasSyncBase.RESULT_DONE;
        }

        if (mailbox.mType == Mailbox.TYPE_DRAFTS) {
            // TODO: Because we don't have bidirectional sync working, trying to downsync
            // the drafts folder is confusing. b/11158759
            // For now, just disable all syncing of DRAFTS type folders.
            // Automatic syncing should always be disabled, but we also stop it here to ensure
            // that we won't sync even if the user attempts to force a sync from the UI.
            // Do NOT treat as a sync error for ping backoff purposes.
            LogUtils.d(TAG, "Skipping sync of DRAFTS folder");
            return EmailServiceStatus.SUCCESS;
        }

        int result = 0;
        // Non-mailbox syncs are whole account syncs initiated by the AccountManager and are
        // treated as background syncs.
        if (mailbox.mType == Mailbox.TYPE_OUTBOX || mailbox.isSyncable()) {
            final ContentValues cv = new ContentValues(2);
            updateMailbox(mailbox, cv, isUserSync ?
                    EmailContent.SYNC_STATUS_USER : EmailContent.SYNC_STATUS_BACKGROUND);
            try {
                if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
                    return syncOutbox(mailbox.mId);
                }
              //TS: junwei-xu 2015-3-12 EMAIL BUGFIX_942249 DEL_S
                //TS: zheng.zou 2015-02-13 EMAIL BUGFIX-830488 ADD_S
                //mSyncInterval value 0 means do not sync
                //if (mailbox.mSyncInterval == 0 && mailbox.mType != Mailbox.TYPE_TRASH){//TS: junwei-xu 2015-3-5 EMAIL BUGFIX_939950 MOD
                //    LogUtils.d(TAG, "Skipping sync of folder which is set as unsync");
                //    return EmailServiceStatus.SUCCESS;
                //}
                //TS: zheng.zou 2015-02-13 EMAIL BUGFIX-830488 ADD_E
              //TS: junwei-xu 2015-3-12 EMAIL BUGFIX_942249 DEL_E
                if (hasCallbackMethod) {
                    EmailServiceStatus.syncMailboxStatus(mContext.getContentResolver(), mSyncExtras,
                            mailbox.mId, EmailServiceStatus.IN_PROGRESS, 0,
                            UIProvider.UIPROVIDER_LASTSYNCRESULT_SUCCESS);
                }
                final EasSyncBase operation = new EasSyncBase(mContext, mAccount, mailbox, mSyncExtras);  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
                LogUtils.d(TAG, "IEmailService.syncMailbox account %d", mAccount.mId);
                FileUtils.appendLog(TAG, "IEmailService.syncMailbox account %d", mAccount.mId);   //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 ADD
                result = operation.performOperation();
            } finally {
                updateMailbox(mailbox, cv, EmailContent.SYNC_STATUS_NONE);
                if (hasCallbackMethod) {
                    EmailServiceStatus.syncMailboxStatus(mContext.getContentResolver(), mSyncExtras,
                            mailbox.mId, EmailServiceStatus.SUCCESS, 0,
                            EasOperation.translateSyncResultToUiResult(result));
                }
            }
        } else {
            // This mailbox is not syncable.
            LogUtils.d(TAG, "Skipping sync of non syncable folder");
        }

        return result;
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-ID ADD_S
    private int syncOutbox(final long mailboxId) {
        LogUtils.d(TAG, "syncOutbox %d", mAccount.mId);
        FileUtils.appendLog(TAG, "syncOutbox %d", mAccount.mId);  //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
        // Because syncing the outbox uses a single EasOperation for every message, we don't
        // want to use doOperation(). That would stop and restart the ping between each operation,
        // which is wasteful if we have several messages to send.
        final ContentResolver resolver = mContext.getContentResolver();
        final ContentValues updateMessageStatusValues = new ContentValues(1);
        final ArrayList<Long> pendingSendMails = new ArrayList<Long>();
        int retryCounts = 0;
        do{
            final Cursor c = resolver.query(EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.CONTENT_PROJECTION, MAILBOX_KEY_AND_NOT_SEND_FAILED,
                    new String[] {Long.toString(mailboxId)}, MAIL_CREATE_TIME_DESC_ORDER);
            retryCounts++;
            LogUtils.d(TAG, "current retry numbers is" + retryCounts);
            FileUtils.appendLog(TAG, "current retry numbers is" + retryCounts);    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
        try {
            // Loop through the messages, sending each one
            while (c.moveToNext()) {
                final Message message = new Message();
                message.restore(c);
                // Update the current mail's status to sending...
                Uri uri =
                        ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI,
                                message.mId);
                if (Utility.hasUnloadedAttachments(mContext, message.mId)) {
                    // We'll just have to wait on this...
                    // TODO: We should make sure that this attachment is queued for download here.
                    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD_S
                    LogUtils.d(TAG, "hasUnloadedAttachments not send subject = " + message.mSubject);
                    FileUtils.appendLog(TAG, "hasUnloadedAttachments not send subject = " + message.mSubject);
                    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD_E
                    updateMessageStatus(uri, resolver, updateMessageStatusValues,
                            EmailContent.Message.MAIL_IN_QUEUE_STATUS);
                    continue;
                }
                    updateMessageStatus(uri, resolver, updateMessageStatusValues,
                            EmailContent.Message.MAIL_IN_SENDING_STATUS);
                // TODO: Fix -- how do we want to signal to UI that we started syncing?
                // Note the entire callback mechanism here needs improving.
                //sendMessageStatus(message.mId, null, EmailServiceStatus.IN_PROGRESS, 0);

                EasOperation op = new EasOutboxSync(mContext, mAccount, message, true);

                int result = op.performOperation();
                if (result == EasOutboxSync.RESULT_ITEM_NOT_FOUND) {
                    // This can happen if we are using smartReply, and the message we are referring
                    // to has disappeared from the server. Try again with smartReply disabled.
                    // This should be a legitimate, but unusual case. Log a warning.
                    LogUtils.w(TAG, "WARNING: EasOutboxSync falling back from smartReply");
                    op = new EasOutboxSync(mContext, mAccount, message, false);
                    result = op.performOperation();
                }
                // If we got some connection error or other fatal error, terminate the sync.
                // RESULT_NON_FATAL_ERROR
                //NOTE: may some error not defined (resylt < EasOutboxSync.RESULT_OP_SPECIFIC_ERROR_RESULT)
                if (result != EasOutboxSync.RESULT_OK &&
                        result != EasOutboxSync.RESULT_NON_FATAL_ERROR
                        //&&result > EasOutboxSync.RESULT_OP_SPECIFIC_ERROR_RESULT
                        ) {
                    LogUtils.w(TAG, "Aborting outbox sync for error %d", result);
                    FileUtils.appendLog(TAG, "Aborting outbox sync for error %d", result);    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
                    //NOTE:Do not return,just do the next retry,we must give the other mails the chances.
                    //return result;
                    if (retryCounts < Utility.MAX_RETRY_SENDING_TIMES) {
                        LogUtils.d(TAG,
                                "update the message status to MAIL_IN_QUEUE_STATUS");
                        FileUtils.appendLog(TAG,
                                "update the message status to MAIL_IN_QUEUE_STATUS");     //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
                        updateMessageStatus(uri, resolver, updateMessageStatusValues,
                                EmailContent.Message.MAIL_IN_QUEUE_STATUS);
                        if (!pendingSendMails.contains(message.mId)) {
                            pendingSendMails.add(message.mId);
                            LogUtils.d(TAG,
                                    "add the message to pending queue,now the list size is+"
                                            + pendingSendMails.size());
                            FileUtils.appendLog(TAG,
                                    "add the message to pending queue,now the list size is+"
                                            + pendingSendMails.size());    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
                        }
                    } else {
                        updateMessageStatus(uri, resolver, updateMessageStatusValues,
                                EmailContent.Message.MAIL_IN_FAILED_STATUS);
                        pendingSendMails.remove(message.mId);
                        LogUtils.d(TAG,
                                "update  to MAIL_IN_FAILED_STATUS and delte from pendingQueue,the list size is"+pendingSendMails.size());
                        FileUtils.appendLog(TAG,
                                "update  to MAIL_IN_FAILED_STATUS and delte from pendingQueue,the list size is" + pendingSendMails.size());  //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
                    }
                    continue;
                }
                //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
                //Note: sending mail successful, insert this mail's recipients to db
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                message.addInnerRecipinetsOps(mContext, ops);
                message.applyBatchOperations(mContext, ops);
                //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
            }
        } finally {
            // TODO: Some sort of sendMessageStatus() is needed here.
            c.close();
        }
        }while(!pendingSendMails.isEmpty() && retryCounts < Utility.MAX_RETRY_SENDING_TIMES);
        //show the failed notifications.
        Cursor cursor = null;
        try {
            int failedMails = 0;
            cursor = resolver.query(EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_WITH_STATUS_PROJECTION,
                    MessageColumns.SENDING_STATUS
                            + "=?" + " AND " + MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            Long.toString(EmailContent.Message.MAIL_IN_FAILED_STATUS),
                            Long.toString(mailboxId)
                    }, null);
            failedMails = cursor.getCount();
            if (failedMails > 0) {
                // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
                // NOTE: For pop/imap/eas, use EmailProvier#call to handle the notification init.
                String method = UIProvider.SHOW_FAILED_NOTIFICATION;
                Bundle bundle =new Bundle();
                bundle.putInt("faild_mails", failedMails);
                bundle.putLong("account_id", mAccount.mId);
                Uri uri = mAccount.getUri();
                resolver.call(uri, method, uri.toString(), bundle);
                // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
            } else {
                // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_S
                //Note: we should cancel send fail notification.
                String method = UIProvider.CANCEL_FAILED_NOTIFICATION;
                Bundle bundle = new Bundle();
                bundle.putLong("account_id", mAccount.mId);
                Uri uri = mAccount.getUri();
                resolver.call(uri, method, uri.toString(), bundle);
                // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_E
            }
        } catch (Exception e) {
            // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
            LogUtils.d(TAG,
                    "Exception happen during notificy failed mails in EasFullSyncOperation#syncOutbox");
            FileUtils.appendLog(TAG,
                    "Exception happen during notificy failed mails in EasFullSyncOperation#syncOutbox");  //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
            // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return EasOutboxSync.RESULT_OK;
    }

    private static void updateMessageStatus(Uri uri, ContentResolver resolver, ContentValues cv,
            int status) {
        cv.clear();
        cv.put(MessageColumns.SENDING_STATUS, status);
        resolver.update(uri, cv, null, null);
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    /**
     * Update the mailbox's sync status with the provider and, if we're finished with the sync,
     * write the last sync time as well.
     * @param mailbox The mailbox whose sync status to update.
     * @param cv A {@link ContentValues} object to use for updating the provider.
     * @param syncStatus The status for the current sync.
     */
    private void updateMailbox(final Mailbox mailbox, final ContentValues cv,
                               final int syncStatus) {
        cv.put(Mailbox.UI_SYNC_STATUS, syncStatus);
        if (syncStatus == EmailContent.SYNC_STATUS_NONE) {
            cv.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
        }
        //TS: xiangnan.zhou 2016-03-09 EMAIL BUGFIX_1752988 MOD_S
        try {
            mailbox.update(mContext, cv);
        } catch (IllegalArgumentException e) {
            LogUtils.e(TAG, "updateMailbox：IllegalArgumentException");
        }
        //TS: xiangnan.zhou 2016-03-09 EMAIL BUGFIX_1752988 MOD_E
    }
}
