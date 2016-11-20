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
 *Tag             Date        Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-892267  2014/01/14   zhaotianyong    [VF8544][2 - Serious][Email] There should be erro message When mail send failed since of SMPT authentication disabled.
 *BUGFIX-1006486  2015/6/23    chaozhang       [Monitor][Email]POP account can not receive new email sometimes
 *BUGFIX-1031697  2015/6/30    zheng.zou       [HOMO][ALGB][VF1857][Email]‘Sent’ folder for POP/ IMAP mail accounts and ‘Inbox’ folder for POP mail account are not getting updated properly on DUT.
 *BUGFIX-718891 2015/11/23     zheng.zou       Email sync frequency for Exchange/imap/pop3 is not correctly
 *BUGFIX-1921250 2016/04/11    yang.mei       [CodeSync]Sync GApp Code from Email_01 to Email_Rel3_02(008-009) 2016-04-08
 ===========================================================================
 */

package com.tct.email.service;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import com.tct.email.R;
import com.tct.emailcommon.TempDirectory;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceProxy;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.providers.UIProvider;

import java.util.ArrayList;

public class PopImapSyncAdapterService extends Service {
    private static final String TAG = "PopImapSyncService";
    private SyncAdapterImpl mSyncAdapter = null;

    public PopImapSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        public SyncAdapterImpl(Context context) {
            super(context, true /* autoInitialize */);
        }

        @Override
        public void onPerformSync(android.accounts.Account account, Bundle extras,
                String authority, ContentProviderClient provider, SyncResult syncResult) {
            PopImapSyncAdapterService.performSync(getContext(), account, extras, provider,
                    syncResult);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSyncAdapter = new SyncAdapterImpl(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
    //TS: yang.mei 2016-04-11 EMAIL BUGFIX_1921250 ADD_S
    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
        LogUtils.e(TAG, "What? low memory? kill self is good way...");
        stopSelf();
    }
    //TS: yang.mei 2016-04-11 EMAIL BUGFIX_1921250 ADD_E
    /**
     * @return whether or not this mailbox retrieves its data from the server (as opposed to just
     *     a local mailbox that is never synced).
     */
    private static boolean loadsFromServer(Context context, Mailbox m, String protocol) {
        String legacyImapProtocol = context.getString(R.string.protocol_legacy_imap);
        String pop3Protocol = context.getString(R.string.protocol_pop3);
        if (legacyImapProtocol.equals(protocol)) {
            // TODO: actually use a sync flag when creating the mailboxes. Right now we use an
            // approximation for IMAP.
            return m.mType != Mailbox.TYPE_DRAFTS
                    && m.mType != Mailbox.TYPE_OUTBOX
                    && m.mType != Mailbox.TYPE_SEARCH;

        } else if (pop3Protocol.equals(protocol)) {
            return Mailbox.TYPE_INBOX == m.mType;
        }

        return false;
    }

    private static void sync(final Context context, final long mailboxId,
            final Bundle extras, final SyncResult syncResult, final boolean uiRefresh,
            final int deltaMessageCount) {
        TempDirectory.setTempDirectory(context);
        Mailbox mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
        if (mailbox == null) return;
        Account account = Account.restoreAccountWithId(context, mailbox.mAccountKey);
        if (account == null) return;
        //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 ADD_S
        //do not do none-manual sync when sync frequency is set to never
        final boolean isManual = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        if(account.getSyncInterval() == Account.CHECK_INTERVAL_NEVER && !isManual){
            LogUtils.i(TAG,"cancel none manual sync when sync frequency is set to never");
            return;
        }
        //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 ADD_E
        ContentResolver resolver = context.getContentResolver();
        String protocol = account.getProtocol(context);
        if ((mailbox.mType != Mailbox.TYPE_OUTBOX) &&
                !loadsFromServer(context, mailbox, protocol)) {
            // This is an update to a message in a non-syncing mailbox; delete this from the
            // updates table and return
            resolver.delete(Message.UPDATED_CONTENT_URI, MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {Long.toString(mailbox.mId)});
            return;
        }
        LogUtils.i(TAG, "About to sync mailbox: " + mailbox.mDisplayName);   // TS: zheng.zou 2015-06-30 EMAIL BUGFIX-1031697 MOD_S

        Uri mailboxUri = ContentUris.withAppendedId(Mailbox.CONTENT_URI, mailboxId);
        ContentValues values = new ContentValues();
        // Set mailbox sync state
        values.put(Mailbox.UI_SYNC_STATUS,
                uiRefresh ? EmailContent.SYNC_STATUS_USER : EmailContent.SYNC_STATUS_BACKGROUND);
        resolver.update(mailboxUri, values, null, null);
        try {
            try {
                String legacyImapProtocol = context.getString(R.string.protocol_legacy_imap);
                // TS: chaozhang 2015-06-23 EMAIL BUGFIX-1006486 MOD_S
                EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId,
                        EmailServiceStatus.IN_PROGRESS, 0, UIProvider.LastSyncResult.SUCCESS);
                int status = UIProvider.LastSyncResult.SUCCESS;
                if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
                    LogUtils.i(TAG, "Going to sync outbox");
                    EmailServiceStub.sendMailImpl(context, account.mId);
                } else {
                    /* Updates the status code "IN_PROGRESS" not only for normal mailbox, but also for outbox.
                     * Otherwise, the RefreshStatusMonitor will be triggered, that should not be happen.
                     * Before this, it only update status code for non-outbox type.
                     * */
                    /*EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId,
                            EmailServiceStatus.IN_PROGRESS, 0, UIProvider.LastSyncResult.SUCCESS);
                    final int status;*/
                    if (protocol.equals(legacyImapProtocol)) {
                        status = ImapService.synchronizeMailboxSynchronous(context, account,
                                mailbox, deltaMessageCount != 0, uiRefresh);
                    } else {
                        status = Pop3Service.synchronizeMailboxSynchronous(context, account,
                                mailbox, deltaMessageCount);
                    }
                    /*EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId, status, 0,
                            UIProvider.LastSyncResult.SUCCESS);*/
                }
                EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId, status, 0,
                        UIProvider.LastSyncResult.SUCCESS);
                // TS: chaozhang 2015-06-23 EMAIL BUGFIX-1006486 MOD_E
            } catch (MessagingException e) {
                LogUtils.e(TAG, e, "sync error");
                final int type = e.getExceptionType();
                // type must be translated into the domain of values used by EmailServiceStatus
                switch(type) {
                    case MessagingException.IOERROR:
                        EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId, type, 0,
                                UIProvider.LastSyncResult.CONNECTION_ERROR);
                        syncResult.stats.numIoExceptions++;
                        break;
                    case MessagingException.AUTHENTICATION_FAILED:
                        EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId, type, 0,
                                UIProvider.LastSyncResult.AUTH_ERROR);
                        syncResult.stats.numAuthExceptions++;
                        break;
                    case MessagingException.SERVER_ERROR:
                        EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId, type, 0,
                                UIProvider.LastSyncResult.SERVER_ERROR);
                        break;

                    default:
                        EmailServiceStatus.syncMailboxStatus(resolver, extras, mailboxId, type, 0,
                                UIProvider.LastSyncResult.INTERNAL_ERROR);
                }
            }
        } finally {
            // Always clear our sync state and update sync time.
            values.put(Mailbox.UI_SYNC_STATUS, EmailContent.SYNC_STATUS_NONE);
            values.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
            resolver.update(mailboxUri, values, null, null);
        }
    }

    /**
     * Partial integration with system SyncManager; we initiate manual syncs upon request
     */
    private static void performSync(Context context, android.accounts.Account account,
            Bundle extras, ContentProviderClient provider, SyncResult syncResult) {
        // Find an EmailProvider account with the Account's email address
        Cursor c = null;
        try {
            c = provider.query(com.tct.emailcommon.provider.Account.CONTENT_URI,
                    Account.CONTENT_PROJECTION, AccountColumns.EMAIL_ADDRESS + "=?",
                    new String[] {account.name}, null);
            if (c != null && c.moveToNext()) {
                Account acct = new Account();
                acct.restore(c);
                if (extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD)) {
                    LogUtils.d(TAG, "Upload sync request for " + acct.mDisplayName);
                    // See if any boxes have mail...
                    ArrayList<Long> mailboxesToUpdate;
                    Cursor updatesCursor = provider.query(Message.UPDATED_CONTENT_URI,
                            new String[] {MessageColumns.MAILBOX_KEY},
                            MessageColumns.ACCOUNT_KEY + "=?",
                            new String[] {Long.toString(acct.mId)},
                            null);
                    try {
                        if ((updatesCursor == null) || (updatesCursor.getCount() == 0)) return;
                        mailboxesToUpdate = new ArrayList<Long>();
                        while (updatesCursor.moveToNext()) {
                            Long mailboxId = updatesCursor.getLong(0);
                            if (!mailboxesToUpdate.contains(mailboxId)) {
                                mailboxesToUpdate.add(mailboxId);
                            }
                        }
                    } finally {
                        if (updatesCursor != null) {
                            updatesCursor.close();
                        }
                    }
                    for (long mailboxId: mailboxesToUpdate) {
                        sync(context, mailboxId, extras, syncResult, false, 0);
                    }
                } else {
                    LogUtils.d(TAG, "Sync request for " + acct.mDisplayName);
                    LogUtils.d(TAG, extras.toString());

                    // We update our folder structure on every sync.
                    final EmailServiceProxy service =
                            EmailServiceUtils.getServiceForAccount(context, acct.mId);
                    service.updateFolderList(acct.mId);

                    // Get the id for the mailbox we want to sync.
                    long [] mailboxIds = Mailbox.getMailboxIdsFromBundle(extras);
                    if (mailboxIds == null || mailboxIds.length == 0) {
                        // No mailbox specified, just sync the inbox.
                        // TODO: IMAP may eventually want to allow multiple auto-sync mailboxes.
                        final long inboxId = Mailbox.findMailboxOfType(context, acct.mId,
                                Mailbox.TYPE_INBOX);
                        if (inboxId != Mailbox.NO_MAILBOX) {
                            mailboxIds = new long[1];
                            mailboxIds[0] = inboxId;
                        }
                        // TS: zheng.zou 2015-06-30 EMAIL BUGFIX-1031697 ADD_S
                        //note: sync sent box when auto sync time is come for imap account
                        String protocol = acct.getProtocol(context);
                        String legacyImapProtocol = context.getString(R.string.protocol_legacy_imap);
                        if (protocol.equals(legacyImapProtocol)) {
                            //sync the sent box
                            final long sentId = Mailbox.findMailboxOfType(context, acct.mId,
                                    Mailbox.TYPE_SENT);
                            if (mailboxIds != null && sentId != Mailbox.NO_MAILBOX) {
                                long tempMailboxIds[] = new long[mailboxIds.length + 1];
                                System.arraycopy(mailboxIds, 0, tempMailboxIds, 0, mailboxIds.length);
                                tempMailboxIds[tempMailboxIds.length - 1] = sentId;
                                mailboxIds = tempMailboxIds;
                            }
                        }
                        // TS: zheng.zou 2015-06-30 EMAIL BUGFIX-1031697 ADD_E
                    }

                    if (mailboxIds != null) {
                        boolean uiRefresh =
                            extras.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
                        int deltaMessageCount =
                                extras.getInt(Mailbox.SYNC_EXTRA_DELTA_MESSAGE_COUNT, 0);
                        for (long mailboxId : mailboxIds) {
                            sync(context, mailboxId, extras, syncResult, uiRefresh,
                                    deltaMessageCount);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
           // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_S
            if (EmailServiceStub.isSendMailFailed()){
                Utility.showToast(context, R.string.send_failed);
            }
            EmailServiceStub.setSendMailFailed();
           // TS: zhaotianyong 2014-01-14 EMAIL BUGFIX-892267 ADD_E
        }
    }
}
