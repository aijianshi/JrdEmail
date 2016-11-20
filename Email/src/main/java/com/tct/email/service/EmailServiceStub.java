/* Copyright (C) 2012 The Android Open Source Project
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
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-892267  2014/01/14   zhaotianyong    [VF8544][2 - Serious][Email] There should be error message When mail send failed since of SMPT authentication disabled.
 *BUGFIX-935438  2014/02/27   gengkexue       [TCT Nanterre] Email account yahoo, exchange.... have several folder duplicated
 *BUGFIX-993643  2015/05/19   wenggangjing    [Android5.0][Email]Loading content is so slowly when set download option as header only.
 *BUGFIX-1018676  2015/6/8    chaozhang       [Android5.0][Email] [FC] Email FC after correcting password in outgoing setting for Sina account
 *CR_585337      2015-09-16  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-944797  2015-11-26   jian.xu         [Android L][Email]Retry notification not disappear after reconnect wifi
 *BUGFIX-1739501 2016-3-8     xiangnan.zhou   [FT][Portugal][VOLTE][EMAIL]E-mail long time cannot send out
 ===========================================================================
 */

package com.tct.email.service;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.TrafficFlags;
import com.tct.emailcommon.internet.MimeBodyPart;
import com.tct.emailcommon.internet.MimeHeader;
import com.tct.emailcommon.internet.MimeMultipart;
import com.tct.emailcommon.mail.AuthenticationFailedException;
import com.tct.emailcommon.mail.FetchProfile;
import com.tct.emailcommon.mail.Folder;
import com.tct.emailcommon.mail.Folder.MessageRetrievalListener;
import com.tct.emailcommon.mail.Folder.OpenMode;
import com.tct.emailcommon.mail.Message;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.provider.EmailContent.BodyColumns;
import com.tct.emailcommon.provider.EmailContent.MailboxColumns;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.service.EmailServiceVersion;
import com.tct.emailcommon.service.HostAuthCompat;
import com.tct.emailcommon.service.IEmailService;
import com.tct.emailcommon.service.IEmailServiceCallback;
import com.tct.emailcommon.service.SearchParams;
import com.tct.emailcommon.utility.AttachmentUtilities;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.NotificationUtils;
import com.tct.mail.utils.Utils;
import com.tct.email.EmailApplication;
import com.tct.email.NotificationController;
import com.tct.email.mail.Sender;
import com.tct.email.mail.Store;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.providers.UIProvider;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * EmailServiceStub is an abstract class representing an EmailService
 *
 * This class provides legacy support for a few methods that are common to both
 * IMAP and POP3, including startSync, loadMore, loadAttachment, and sendMail
 */
public abstract class EmailServiceStub extends IEmailService.Stub implements IEmailService {

    private static final int MAILBOX_COLUMN_ID = 0;
    private static final int MAILBOX_COLUMN_SERVER_ID = 1;
    private static final int MAILBOX_COLUMN_TYPE = 2;

    // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_S
    private static boolean sendMailFailed = false;
    // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_E

    /** Small projection for just the columns required for a sync. */
    private static final String[] MAILBOX_PROJECTION = {
        MailboxColumns._ID,
        MailboxColumns.SERVER_ID,
        MailboxColumns.TYPE,
    };

    protected Context mContext;

    protected void init(Context context) {
        mContext = context;
    }

    @Override
    public Bundle validate(HostAuthCompat hostAuthCom) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    protected void requestSync(long mailboxId, boolean userRequest, int deltaMessageCount) {
        final Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, mailboxId);
        if (mailbox == null) return;
        final Account account = Account.restoreAccountWithId(mContext, mailbox.mAccountKey);
        if (account == null) return;
        final EmailServiceInfo info =
                EmailServiceUtils.getServiceInfoForAccount(mContext, account.mId);
        final android.accounts.Account acct = new android.accounts.Account(account.mEmailAddress,
                info.accountType);
        final Bundle extras = Mailbox.createSyncBundle(mailboxId);
        if (userRequest) {
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        }
        if (deltaMessageCount != 0) {
            extras.putInt(Mailbox.SYNC_EXTRA_DELTA_MESSAGE_COUNT, deltaMessageCount);
        }
        ContentResolver.requestSync(acct, EmailContent.AUTHORITY, extras);
        LogUtils.i(Logging.LOG_TAG, "requestSync EmailServiceStub requestSync %s, %s",
                account.toString(), extras.toString());
    }

    //[FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110
    public Bundle syncOof(long accountId, String command, Bundle content) throws RemoteException {
        return null;
    }
    //[FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110

    /**
     * Delete a single message by moving it to the trash, or really delete it if it's already in
     * trash or a draft message.
     *
     * This function has no callback, no result reporting, because the desired outcome
     * is reflected entirely by changes to one or more cursors.
     *
     * @param messageId The id of the message to "delete".
     */
     public void deleteMessage(long messageId) {

        final EmailContent.Message message =
            EmailContent.Message.restoreMessageWithId(mContext, messageId);
         if (message == null) {
            if (Logging.LOGD) LogUtils.v(Logging.LOG_TAG, "dletMsg message NULL");
            return;
         }
        // 1. Get the message's account
        final Account account = Account.restoreAccountWithId(mContext, message.mAccountKey);
        // 2. Get the message's original mailbox
        final Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, message.mMailboxKey);
        if (account == null || mailbox == null) {
            if (Logging.LOGD) LogUtils.v(Logging.LOG_TAG, "dletMsg account or mailbox NULL");
            return;
        }
        if(Logging.LOGD)
            LogUtils.d(Logging.LOG_TAG, "AccountKey "+account.mId + "oirigMailbix: "+mailbox.mId);
        // 3. Confirm that there is a trash mailbox available.  If not, create one
        //[FEATURE]-Mod-BEGIN by TSNJ.Zhenhua.Fan,09/06/2014,FR 622609
        Mailbox trashFolder = null;
        if (EmailApplication.isOrangeImapFeatureOn() && Account.isOrangeImapAccount(mContext, account.mId))
        {
            long trashMailboxId = Mailbox.getOrangeImapTrashboxId(mContext, account.mId);
            if (trashMailboxId != Mailbox.NO_MAILBOX) {
                trashFolder = Mailbox.restoreMailboxWithId(mContext, trashMailboxId);
            }
        } else {
            trashFolder = Mailbox.restoreMailboxOfType(mContext, account.mId,
                    Mailbox.TYPE_TRASH);
        }
        //[FEATURE]-Mod-END by TSNJ.Zhenhua.Fan
        if (trashFolder == null) {
            if (Logging.LOGD) LogUtils.v(Logging.LOG_TAG, "dletMsg Trash mailbox NULL");
        }else
            LogUtils.d(Logging.LOG_TAG, "TrasMailbix: "+ trashFolder.mId);

        // 4.  Drop non-essential data for the message (e.g. attachment files)
        AttachmentUtilities.deleteAllAttachmentFiles(mContext, account.mId,
                messageId);

        Uri uri = ContentUris.withAppendedId(EmailContent.Message.SYNCED_CONTENT_URI,
                messageId);

        // 5. Perform "delete" as appropriate
        if ((mailbox.mId == trashFolder.mId) || (mailbox.mType == Mailbox.TYPE_DRAFTS)) {
            // 5a. Really delete it
             mContext.getContentResolver().delete(uri, null, null);
        } else {
            // 5b. Move to trash
            ContentValues cv = new ContentValues();
            cv.put(EmailContent.MessageColumns.MAILBOX_KEY, trashFolder.mId);
            mContext.getContentResolver().update(uri, cv, null, null);
        }
    }

    /**
     * Moves messages to a new mailbox.
     *
     * This function has no callback, no result reporting, because the desired outcome
     * is reflected entirely by changes to one or more cursors.
     *
     * Note this method assumes all of the given message and mailbox IDs belong to the same
     * account.
     *
     * @param messageIds IDs of the messages that are to be moved
     * @param newMailboxId ID of the new mailbox that the messages will be moved to
     * @return an asynchronous task that executes the move (for testing only)
     */
     public void MoveMessages(long messageId, long newMailboxId) {
        Account account = Account.getAccountForMessageId(mContext, messageId);
        if (account != null) {
            if (Logging.LOGD) {
                LogUtils.d(Logging.LOG_TAG, "moveMessage Acct "+account.mId);
                LogUtils.d(Logging.LOG_TAG, "moveMessage messageId:" + messageId);
            }
            ContentValues cv = new ContentValues();
            cv.put(EmailContent.MessageColumns.MAILBOX_KEY, newMailboxId);
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = ContentUris.withAppendedId(
                EmailContent.Message.SYNCED_CONTENT_URI, messageId);
            resolver.update(uri, cv, null, null);
        } else
            LogUtils.d(Logging.LOG_TAG, "moveMessage Cannot find account");
     }
    /**
     * Set/clear boolean columns of a message
     *
     * @param messageId the message to update
     * @param columnName the column to update
     * @param columnValue the new value for the column
     */
    private void setMessageBoolean(long messageId, String columnName, boolean columnValue) {
        ContentValues cv = new ContentValues();
        cv.put(columnName, columnValue);
        Uri uri = ContentUris.withAppendedId(EmailContent.Message.SYNCED_CONTENT_URI, messageId);
        mContext.getContentResolver().update(uri, cv, null, null);
    }

    /**
     * Set/clear the unread status of a message
     *
     * @param messageId the message to update
     * @param isRead the new value for the isRead flag
     */
    public void setMessageRead(long messageId, boolean isRead) {
        setMessageBoolean(messageId, EmailContent.MessageColumns.FLAG_READ, isRead);
    }

    @Override
    public void loadAttachment(final IEmailServiceCallback cb, final long accountId,
            final long attachmentId, final boolean background) throws RemoteException {
        Folder remoteFolder = null;
        try {
            //1. Check if the attachment is already here and return early in that case
            Attachment attachment =
                Attachment.restoreAttachmentWithId(mContext, attachmentId);
            if (attachment == null) {
                cb.loadAttachmentStatus(0, attachmentId,
                        EmailServiceStatus.ATTACHMENT_NOT_FOUND, 0);
                return;
            }
            final long messageId = attachment.mMessageKey;

            final EmailContent.Message message =
                    EmailContent.Message.restoreMessageWithId(mContext, attachment.mMessageKey);
            if (message == null) {
                cb.loadAttachmentStatus(messageId, attachmentId,
                        EmailServiceStatus.MESSAGE_NOT_FOUND, 0);
                return;
            }

            // If the message is loaded, just report that we're finished
            if (Utility.attachmentExists(mContext, attachment)
                    && attachment.mUiState == UIProvider.AttachmentState.SAVED) {
                cb.loadAttachmentStatus(messageId, attachmentId, EmailServiceStatus.SUCCESS,
                        0);
                return;
            }

            // Say we're starting...
            cb.loadAttachmentStatus(messageId, attachmentId, EmailServiceStatus.IN_PROGRESS, 0);

            // 2. Open the remote folder.
            final Account account = Account.restoreAccountWithId(mContext, message.mAccountKey);
            Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, message.mMailboxKey);
            if (mailbox == null) {
                // This could be null if the account is deleted at just the wrong time.
                return;
            }
            if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
                long sourceId = Utility.getFirstRowLong(mContext, Body.CONTENT_URI,
                        new String[] {BodyColumns.SOURCE_MESSAGE_KEY},
                        BodyColumns.MESSAGE_KEY + "=?",
                        new String[] {Long.toString(messageId)}, null, 0, -1L);
                if (sourceId != -1) {
                    EmailContent.Message sourceMsg =
                            EmailContent.Message.restoreMessageWithId(mContext, sourceId);
                    if (sourceMsg != null) {
                        mailbox = Mailbox.restoreMailboxWithId(mContext, sourceMsg.mMailboxKey);
                        message.mServerId = sourceMsg.mServerId;
                    }
                }
            } else if (mailbox.mType == Mailbox.TYPE_SEARCH && message.mMainMailboxKey != 0) {
                mailbox = Mailbox.restoreMailboxWithId(mContext, message.mMainMailboxKey);
            }

            if (account == null || mailbox == null) {
                // If the account/mailbox are gone, just report success; the UI handles this
                cb.loadAttachmentStatus(messageId, attachmentId,
                        EmailServiceStatus.SUCCESS, 0);
                return;
            }
            TrafficStats.setThreadStatsTag(
                    TrafficFlags.getAttachmentFlags(mContext, account));

            final Store remoteStore = Store.getInstance(account, mContext);
            remoteFolder = remoteStore.getFolder(mailbox.mServerId);
            remoteFolder.open(OpenMode.READ_WRITE);

            // 3. Generate a shell message in which to retrieve the attachment,
            // and a shell BodyPart for the attachment.  Then glue them together.
            final Message storeMessage = remoteFolder.createMessage(message.mServerId);
            final MimeBodyPart storePart = new MimeBodyPart();
            storePart.setSize((int)attachment.mSize);
            storePart.setHeader(MimeHeader.HEADER_ANDROID_ATTACHMENT_STORE_DATA,
                    attachment.mLocation);
            storePart.setHeader(MimeHeader.HEADER_CONTENT_TYPE,
                    String.format("%s;\n name=\"%s\"",
                    attachment.mMimeType,
                    attachment.mFileName));

            // TODO is this always true for attachments?  I think we dropped the
            // true encoding along the way
            storePart.setHeader(MimeHeader.HEADER_CONTENT_TRANSFER_ENCODING, "base64");

            final MimeMultipart multipart = new MimeMultipart();
            multipart.setSubType("mixed");
            multipart.addBodyPart(storePart);

            storeMessage.setHeader(MimeHeader.HEADER_CONTENT_TYPE, "multipart/mixed");
            storeMessage.setBody(multipart);

            // 4. Now ask for the attachment to be fetched
            final FetchProfile fp = new FetchProfile();
            fp.add(storePart);
            remoteFolder.fetch(new Message[] { storeMessage }, fp,
                    new MessageRetrievalListenerBridge(messageId, attachmentId, cb));

            // If we failed to load the attachment, throw an Exception here, so that
            // AttachmentService knows that we failed
            if (storePart.getBody() == null) {
                throw new MessagingException("Attachment not loaded.");
            }

            // Save the attachment to wherever it's going
            AttachmentUtilities.saveAttachment(mContext, storePart.getBody().getInputStream(),
                    attachment);

            // 6. Report success
            cb.loadAttachmentStatus(messageId, attachmentId, EmailServiceStatus.SUCCESS, 0);

        } catch (MessagingException me) {
            LogUtils.i(Logging.LOG_TAG, me, "Error loading attachment");

            final ContentValues cv = new ContentValues(1);
            cv.put(AttachmentColumns.UI_STATE, UIProvider.AttachmentState.FAILED);
            final Uri uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, attachmentId);
            mContext.getContentResolver().update(uri, cv, null, null);

            cb.loadAttachmentStatus(0, attachmentId, EmailServiceStatus.CONNECTION_ERROR, 0);
        } finally {
            if (remoteFolder != null) {
                remoteFolder.close(false);
            }
        }

    }

    /**
     * Bridge to intercept {@link MessageRetrievalListener#loadAttachmentProgress} and
     * pass down to {@link IEmailServiceCallback}.
     */
    public class MessageRetrievalListenerBridge implements MessageRetrievalListener {
        private final long mMessageId;
        private final long mAttachmentId;
        private final IEmailServiceCallback mCallback;


        public MessageRetrievalListenerBridge(final long messageId, final long attachmentId,
                final IEmailServiceCallback callback) {
            mMessageId = messageId;
            mAttachmentId = attachmentId;
            mCallback = callback;
        }

        @Override
        public void loadAttachmentProgress(int progress) {
            try {
                mCallback.loadAttachmentStatus(mMessageId, mAttachmentId,
                        EmailServiceStatus.IN_PROGRESS, progress);
            } catch (final RemoteException e) {
                // No danger if the client is no longer around
            }
        }

        @Override
        public void messageRetrieved(com.tct.emailcommon.mail.Message message) {
        }
    }

    @Override
    public synchronized void updateFolderList(final long accountId) throws RemoteException { // AM: Kexue.Geng 2015-02-27 EMAIL BUGFIX_935438 MOD
        final Account account = Account.restoreAccountWithId(mContext, accountId);
        if (account == null) {
            LogUtils.e(LogUtils.TAG, "Account %d not found in updateFolderList", accountId);
            return;
        };
        long inboxId = -1;
        TrafficStats.setThreadStatsTag(TrafficFlags.getSyncFlags(mContext, account));
        Cursor localFolderCursor = null;
        Store store = null;
        try {
            store = Store.getInstance(account, mContext);

            // Step 0: Make sure the default system mailboxes exist.
            for (final int type : Mailbox.REQUIRED_FOLDER_TYPES) {
                if (Mailbox.findMailboxOfType(mContext, accountId, type) == Mailbox.NO_MAILBOX) {
                    final Mailbox mailbox = Mailbox.newSystemMailbox(mContext, accountId, type);
                    if (store.canSyncFolderType(type)) {
                        // If this folder is syncable, then we should set its UISyncStatus.
                        // Otherwise the UI could show the empty state until the sync
                        // actually occurs.
                        mailbox.mUiSyncStatus = Mailbox.SYNC_STATUS_INITIAL_SYNC_NEEDED;
                    }
                    mailbox.save(mContext);
                    if (type == Mailbox.TYPE_INBOX) {
                        inboxId = mailbox.mId;
                    }
                }
            }

            // Step 1: Get remote mailboxes
            final Folder[] remoteFolders = store.updateFolders();
            final HashSet<String> remoteFolderNames = new HashSet<String>();
            for (final Folder remoteFolder : remoteFolders) {
                remoteFolderNames.add(remoteFolder.getName());
            }

            // Step 2: Get local mailboxes
            localFolderCursor = mContext.getContentResolver().query(
                    Mailbox.CONTENT_URI,
                    MAILBOX_PROJECTION,
                    EmailContent.MailboxColumns.ACCOUNT_KEY + "=?",
                    new String[] { String.valueOf(account.mId) },
                    null);

            // Step 3: Remove any local mailbox not on the remote list
            while (localFolderCursor.moveToNext()) {
                final String mailboxPath = localFolderCursor.getString(MAILBOX_COLUMN_SERVER_ID);
                // Short circuit if we have a remote mailbox with the same name
                if (remoteFolderNames.contains(mailboxPath)) {
                    continue;
                }

                final int mailboxType = localFolderCursor.getInt(MAILBOX_COLUMN_TYPE);
                final long mailboxId = localFolderCursor.getLong(MAILBOX_COLUMN_ID);
                switch (mailboxType) {
                    case Mailbox.TYPE_INBOX:
                    case Mailbox.TYPE_DRAFTS:
                    case Mailbox.TYPE_OUTBOX:
                    case Mailbox.TYPE_SENT:
                    case Mailbox.TYPE_TRASH:
                    case Mailbox.TYPE_SEARCH:
                        // Never, ever delete special mailboxes
                        break;
                    default:
                        // Drop all attachment files related to this mailbox
                        AttachmentUtilities.deleteAllMailboxAttachmentFiles(
                                mContext, accountId, mailboxId);
                        // Delete the mailbox; database triggers take care of related
                        // Message, Body and Attachment records
                        Uri uri = ContentUris.withAppendedId(
                                Mailbox.CONTENT_URI, mailboxId);
                        mContext.getContentResolver().delete(uri, null, null);
                        break;
                }
            }
        } catch (MessagingException me) {
            LogUtils.i(Logging.LOG_TAG, me, "Error in updateFolderList");
            // We'll hope this is temporary
            // TODO: Figure out what type of messaging exception it was and return an appropriate
            // result. If we start doing this from sync, it's important to let the sync manager
            // know if the failure was due to IO error or authentication errors.
        } finally {
            if (localFolderCursor != null) {
                localFolderCursor.close();
            }
            if (store != null) {
                store.closeConnections();
            }
            // If we just created the inbox, sync it
            if (inboxId != -1) {
                requestSync(inboxId, true, 0);
            }
        }
    }

    @Override
    public void setLogging(final int flags) throws RemoteException {
        // Not required
    }

    @Override
    public Bundle autoDiscover(final String userName, final String password)
            throws RemoteException {
        // Not required
       return null;
    }

    @Override
    public void sendMeetingResponse(final long messageId, final int response)
            throws RemoteException {
        // Not required
    }

    @Override
    public void deleteExternalAccountPIMData(final String emailAddress) throws RemoteException {
        // No need to do anything here, for IMAP and POP accounts none of our data is external.
    }

    @Override
    public int searchMessages(final long accountId, final SearchParams params,
                              final long destMailboxId)
            throws RemoteException {
        // Not required
        return EmailServiceStatus.SUCCESS;
    }

    @Override
    public void pushModify(final long accountId) throws RemoteException {
        LogUtils.e(Logging.LOG_TAG, "pushModify invalid for account type for %d", accountId);
    }

    @Override
    public int sync(final long accountId, final Bundle syncExtras) {
        return EmailServiceStatus.SUCCESS;

    }

    @Override
    public void sendMail(final long accountId) throws RemoteException {
        sendMailImpl(mContext, accountId);
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
    //NOTE: For every message, have Five chance to resend,after that,quit the loop.
    public static void sendMailImpl(final Context context, final long accountId) {
        final Account account = Account.restoreAccountWithId(context, accountId);
        if (account == null) {
            LogUtils.e(LogUtils.TAG, "account %d not found in sendMailImpl", accountId);
            return;
        }
        TrafficStats.setThreadStatsTag(TrafficFlags.getSmtpFlags(context, account));
        final NotificationController nc = NotificationController.getInstance(context);
        // 1. Loop through all messages in the account's outbox
        final long outboxId = Mailbox.findMailboxOfType(context, account.mId, Mailbox.TYPE_OUTBOX);
        if (outboxId == Mailbox.NO_MAILBOX) {
            LogUtils.e(LogUtils.TAG, "outboxId is NO_MAILBOX");
            return;
        }
        final ContentResolver resolver = context.getContentResolver();
        final ArrayList<Long> pendingSendMails = new ArrayList<Long>();
        final ContentValues updateMessageStatusValues = new ContentValues(1);
        int retryCounts = 0;
        do {
            retryCounts++;
            LogUtils.i(LogUtils.TAG, "current retry sending Count is" + retryCounts);
            final Cursor c = resolver.query(
                    EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_WITH_STATUS_PROJECTION,
                    MessageColumns.SENDING_STATUS
                            + "!=?" + " AND " +
                            MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            Long.toString(EmailContent.Message.MAIL_IN_FAILED_STATUS),
                            Long.toString(outboxId)
                    },
                    EmailContent.MessageColumns.TIMESTAMP +" DESC ");
            try {
                // 2. exit early
                if (c.getCount() <= 0) {
                    LogUtils.i(LogUtils.TAG, "no mails in outbox");
                    return;
                }
                final Sender sender = Sender.getInstance(context, account);
                final Store remoteStore = Store.getInstance(account, context);
                final ContentValues moveToSentValues;
                if (remoteStore.requireCopyMessageToSentFolder()) {
                    // [FEATURE]-Mod-BEGIN by TSNJ.Zhenhua.Fan,06/09/2014,FR 622609
                    Mailbox sentFolder = null;
                    if (EmailApplication.isOrangeImapFeatureOn()
                            && Account.isOrangeImapAccount(context, accountId)) {
                        long sentboxId = Mailbox.getOrangeImapSentboxId(context, accountId);
                        if (sentboxId != Mailbox.NO_MAILBOX) {
                            sentFolder = Mailbox.restoreMailboxWithId(context, sentboxId);
                        }
                    } else if (EmailApplication.isSfrImapFeatureOn()
                            && Account.isSfrImapAccount(context, accountId)) {
                        long sentboxId = Mailbox.getSfrImapSentboxId(context, accountId);
                        if (sentboxId != Mailbox.NO_MAILBOX) {
                            sentFolder = Mailbox.restoreMailboxWithId(context, sentboxId);
                        }
                    } else {
                        sentFolder = Mailbox.restoreMailboxOfType(context, accountId,
                                Mailbox.TYPE_SENT);
                    }
                    // [FEATURE]-Mod-END by TSNJ.Zhenhua.Fan
                    moveToSentValues = new ContentValues();
                    moveToSentValues.put(MessageColumns.MAILBOX_KEY, sentFolder.mId);
                } else {
                    moveToSentValues = null;
                }

                // 3. loop through the available messages and send them
                // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 ADD_S
                // NOTE: Use the flag to judge the authFail,during requesting with server.
                // if true,we pop-up auth notification.
                boolean isAuthFailed = false;
                while (c.moveToNext()) {
                    long messageId = -1;
                    if (moveToSentValues != null) {
                        moveToSentValues.remove(EmailContent.MessageColumns.FLAGS);
                    }
                    try {
                        messageId = c.getLong(0);
                        Uri uri =ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI,messageId);
                        // Don't send messages with unloaded attachments
                        if (Utility.hasUnloadedAttachments(context, messageId)) {
                            if (MailActivityEmail.DEBUG) {
                                LogUtils.d(Logging.LOG_TAG, "Can't send #" + messageId +
                                        "; unloaded attachments");
                            }
                            LogUtils.i(Logging.LOG_TAG, "Can't send #" + messageId +
                                    "; unloaded attachments,update sendingStatus to queued");
                            updateMessageStatus(uri, resolver, updateMessageStatusValues,
                                    EmailContent.Message.MAIL_IN_QUEUE_STATUS);
                            continue;
                        }
                        updateMessageStatus(uri, resolver, updateMessageStatusValues,
                                EmailContent.Message.MAIL_IN_SENDING_STATUS);
                        sender.sendMessage(messageId);
                    } catch (MessagingException me) {
                        LogUtils.e(LogUtils.TAG,me, "sendMessage error" );
                        // report error for this message, but keep trying others
                        if (me instanceof AuthenticationFailedException) {
                            // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 ADD_S
                            // NOTE:Normal behavior is not good,if we go to outgoingSetting
                            // dirrectlly,FC will happen,
                            // Now whatever use done,if happen request with server,we sequentially
                            // go to
                            // incomingSetting,outGoingSettings.
                            // nc.showLoginFailedNotificationSynchronous(account.mId,
                            // false /* incoming */);
                            nc.showLoginFailedNotificationSynchronous(account.mId,
                                    true /* incoming */);
                            isAuthFailed = true;
                            // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 MOD_S
                        }
                            //NOTE: exception happen,means mails send failed,just store the status and continue next sending
                            Uri uri =ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI,messageId);
                            if (retryCounts < Utility.MAX_RETRY_SENDING_TIMES) {
                                LogUtils.d(LogUtils.TAG,
                                        "update the message status to MAIL_IN_QUEUE_STATUS");
                                updateMessageStatus(uri, resolver, updateMessageStatusValues,
                                        EmailContent.Message.MAIL_IN_QUEUE_STATUS);
                                if (!pendingSendMails.contains(messageId)) {
                                    pendingSendMails.add(messageId);
                                    LogUtils.d(LogUtils.TAG,
                                            "add the message to pending queue,now the list size is+"
                                                    + pendingSendMails.size());

                                }
                            } else {
                                updateMessageStatus(uri, resolver, updateMessageStatusValues,
                                        EmailContent.Message.MAIL_IN_FAILED_STATUS);
                                pendingSendMails.remove(messageId);
                                LogUtils.d(LogUtils.TAG,
                                        "update  to MAIL_IN_FAILED_STATUS and delte from pendingQueue,the list size is"
                                                + pendingSendMails.size());
                        }
                        continue;
                    }
                    //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
                    //Note: send message successful, insert recipients which in this message to db
                    //TS: xiangnan.zhou 2016-03-08 EMAIL BUGFIX-1739501 MOD-S
                    //Note: sqlException will stop next code execute,catch it to avoid message always stay in OUTBOX
                    try {
                        EmailContent.Message message =
                                EmailContent.Message.restoreMessageWithId(context, messageId);
                        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                        message.addInnerRecipinetsOps(context, ops);
                        message.applyBatchOperations(context, ops);
                    }catch (SQLiteConstraintException sql){
                        LogUtils.e(LogUtils.TAG,
                                "Badlly,SQLiteConstraintException happen when insert recipient to db");
                        sql.printStackTrace();
                    }
                    //TS: xiangnan.zhou 2016-03-08 EMAIL BUGFIX-1739501 MOD-E
                    //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
                    // 4. move to sent, or delete
                    final Uri syncedUri =
                            ContentUris.withAppendedId(EmailContent.Message.SYNCED_CONTENT_URI,
                                    messageId);
                    // Delete all cached files
                    AttachmentUtilities.deleteAllCachedAttachmentFiles(context, account.mId,
                            messageId);
                    if (moveToSentValues != null) {
                        // If this is a forwarded message and it has attachments, delete them, as
                        // they
                        // duplicate information found elsewhere (on the server). This saves
                        // storage.
                        final EmailContent.Message msg =
                                EmailContent.Message.restoreMessageWithId(context, messageId);
                        if ((msg.mFlags & EmailContent.Message.FLAG_TYPE_FORWARD) != 0) {
                            AttachmentUtilities.deleteAllAttachmentFiles(context, account.mId,
                                    messageId);
                        }
                        final int flags = msg.mFlags & ~(EmailContent.Message.FLAG_TYPE_REPLY |
                                EmailContent.Message.FLAG_TYPE_FORWARD |
                                EmailContent.Message.FLAG_TYPE_REPLY_ALL |
                                EmailContent.Message.FLAG_TYPE_ORIGINAL);
                        // [FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
                        moveToSentValues.put(SyncColumns.SERVER_ID, "");
                        // [FEATURE]-Add-END by TSNJ,Zhenhua.Fan
                        //restore status to default.
                        moveToSentValues.put(MessageColumns.SENDING_STATUS,
                                EmailContent.Message.MAIL_IN_NONE_STATUS);
                        // TS: chao.zhang 2015-10-09 EMAIL FEATURE-585337 ADD_S
                        //NOTE: After message sent,just restore the sendStatus to normal cause MessageHeaderView#getHeaderTitle() need the status.
                        moveToSentValues.put(EmailContent.MessageColumns.FLAGS, flags);
                        // TS: chao.zhang 2015-10-09 EMAIL FEATURE-585337 ADD_E
                        resolver.update(syncedUri, moveToSentValues, null, null);
                        LogUtils.i(Logging.LOG_TAG, "Send success" + messageId +
                                ",move message to sent");
                    } else {
                        AttachmentUtilities.deleteAllAttachmentFiles(context, account.mId,
                                messageId);
                        final Uri uri =
                                ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI,
                                        messageId);
                        resolver.delete(uri, null, null);
                        resolver.delete(syncedUri, null, null);
                    }
                }
                // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 MOD_S
                // NOTE:After auth failed,do not cancel or dismiss the notification,give user chance
                // to change it.
                if (!isAuthFailed) {
                    nc.cancelLoginFailedNotification(account.mId);
                }
                // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 MOD_E
            } catch (MessagingException me) {
                LogUtils.e(LogUtils.TAG, me, "sendMailImpl error in large catch");
                if (me instanceof AuthenticationFailedException) {
                    // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 MOD_S
                    // nc.showLoginFailedNotificationSynchronous(account.mId, false /* incoming */);
                    nc.showLoginFailedNotificationSynchronous(account.mId, true /* incoming */);
                    // TS: chaozhang 2015-6-8 EMAIL BUGFIX_1018676 MOD_E
                }
            } finally {
                c.close();
            }
        } while (!pendingSendMails.isEmpty() && retryCounts < Utility.MAX_RETRY_SENDING_TIMES);
        //get the failed message and notification  user
        int failedMails = 0;
        Cursor cursor = null;
        try {
            cursor = resolver.query(EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_WITH_STATUS_PROJECTION,
                    MessageColumns.SENDING_STATUS
                            + "=?" + " AND " + MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            Long.toString(EmailContent.Message.MAIL_IN_FAILED_STATUS),
                            Long.toString(outboxId)
                    }, null);
            failedMails = cursor.getCount();
            if (failedMails > 0) {
                // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_S
                sendMailFailed = true;
                // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_E
               //Utils.createFailedNotificationsIntent(context,account.mId,failedMails);
                // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
                //NOTE: For pop/imap/eas, use EmailProvier#call to handle the notification init.
                String method = UIProvider.AccountCallMethods.SHOW_FAILED_NOTIFICATION;
                Bundle bundle =new Bundle();
                bundle.putInt("faild_mails", failedMails);
                bundle.putLong("account_id", account.mId);
                Uri uri = account.getUri();
                resolver.call(uri, method, uri.toString(), bundle);
                // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
                LogUtils.i(LogUtils.TAG, "have failed message,and show fail notification");
            } else {
                // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_S
                //Note: we should cancel send fail notification.
                String method = UIProvider.AccountCallMethods.CANCEL_FAILED_NOTIFICATION;
                Bundle bundle = new Bundle();
                bundle.putLong("account_id", account.mId);
                Uri uri = account.getUri();
                resolver.call(uri, method, uri.toString(), bundle);
                // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_E
                LogUtils.i(LogUtils.TAG, "All message send successfully,cancel all fail notification");
            }
        } catch (Exception e) {
            LogUtils.d(LogUtils.TAG,e, "Exception happen during query failed mails in EmailService#sendMailImpl");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    /**
     *Use to update the current message's status
     */
    private static void updateMessageStatus(Uri uri, ContentResolver resolver, ContentValues cv,
            int status) {
        cv.clear();
        cv.put(MessageColumns.SENDING_STATUS, status);
        resolver.update(uri, cv, null, null);
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_E
    public int getApiVersion() {
        return EmailServiceVersion.CURRENT;
    }

    // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_S
    public static boolean isSendMailFailed(){
        return sendMailFailed;
    }

    public static void setSendMailFailed(){
        sendMailFailed = false;
    }
    // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_E
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_S
    @Override
    public int fetchMessage(long messageId) throws RemoteException {
        // / M: TODO Add for partial download
        // Do nothing here but sub classes
        return 0;
    }
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_E
}
