/*
 * Copyright (C) 2014 The Android Open Source Project
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
 ===========================================================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== ===============================================================
 *BUGFIX-993643  2015/05/19   wenggangjing    [Android5.0][Email]Loading content is so slowly when set download option as header only.
 *BUGFIX-1027519 2015/06/23   junwei-xu       [pixi3-5.5 3G][Force close][Android5.1][Email]Email FC when login an TCL account.
 *BUGFIX-1010924  2015-09-28   chaozhang      [HOMO][HOMO][HOMO][ALWE] Corporate email synchronized again all folders instead of updating only new emails
 *BUGFIX-677793   2015-09-29   zheng.zou      [Email]Print test log to sd card[Email]Print test log to sd card
 *BUGFIX-1111768 2015-11-18    jian.xu       [Monkey][Crash][Email]com.tct.email force close during monkey system test
 *==========================================================================================================
 */
package com.tct.exchange.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.tct.emailcommon.TempDirectory;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceProxy;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.service.EmailServiceVersion;
import com.tct.emailcommon.service.HostAuthCompat;
import com.tct.emailcommon.service.IEmailService;
import com.tct.emailcommon.service.IEmailServiceCallback;
import com.tct.emailcommon.service.SearchParams;
import com.tct.emailcommon.service.ServiceProxy;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.FileUtils;
import com.tct.mail.utils.LogUtils;
//[FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110
import com.tct.exchange.Eas;
import com.tct.exchange.eas.EasAutoDiscover;
import com.tct.exchange.eas.EasFolderSync;
import com.tct.exchange.eas.EasFullSyncOperation;
import com.tct.exchange.eas.EasLoadAttachment;
import com.tct.exchange.eas.EasLoadMore;
import com.tct.exchange.eas.EasOperation;
import com.tct.exchange.eas.EasPing;
import com.tct.exchange.eas.EasSearch;
import com.tct.exchange.eas.EasSearchGal;
import com.tct.exchange.eas.EasSendMeetingResponse;
import com.tct.exchange.eas.EasSyncCalendar;
import com.tct.exchange.eas.EasSyncContacts;
import com.tct.exchange.eas.OofOperation;
import com.tct.exchange.provider.GalResult;

import android.util.Log;
//[FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110

import java.util.HashSet;
import java.util.Set;

/**
 * Service to handle all communication with the EAS server. Note that this is completely decoupled
 * from the sync adapters; sync adapters should make blocking calls on this service to actually
 * perform any operations.
 */
public class EasService extends Service {

    private static final String TAG = Eas.LOG_TAG;

    public static final String EXTRA_START_PING = "START_PING";
    public static final String EXTRA_PING_ACCOUNT = "PING_ACCOUNT";

    /**
     * The content authorities that can be synced for EAS accounts. Initialization must wait until
     * after we have a chance to call {@link EmailContent#init} (and, for future content types,
     * possibly other initializations) because that's how we can know what the email authority is.
     */
    private static String[] AUTHORITIES_TO_SYNC;

    /** Bookkeeping for ping tasks & sync threads management. */
    private final PingSyncSynchronizer mSynchronizer;

    /**
     * Implementation of the IEmailService interface.
     * For the most part these calls should consist of creating the correct {@link EasOperation}
     * class and calling {@link #doOperation} with it.
     */
    private final IEmailService.Stub mBinder = new IEmailService.Stub() {
        @Override
        public void loadAttachment(final IEmailServiceCallback callback, final long accountId,
                final long attachmentId, final boolean background) {
            LogUtils.d(TAG, "IEmailService.loadAttachment: %d", attachmentId);
            final EasLoadAttachment operation = new EasLoadAttachment(EasService.this, accountId,
                    attachmentId, callback);
            doOperation(operation, "IEmailService.loadAttachment");
        }

        @Override
        public void updateFolderList(final long accountId) {
            final EasFolderSync operation = new EasFolderSync(EasService.this, accountId);
            doOperation(operation, "IEmailService.updateFolderList");
        }

        public void sendMail(final long accountId) {
            // TODO: We should get rid of sendMail, and this is done in sync.
            LogUtils.wtf(TAG, "unexpected call to EasService.sendMail");
        }

        public int sync(final long accountId, Bundle syncExtras) {
            EasFullSyncOperation op = new EasFullSyncOperation(EasService.this, accountId,
                    syncExtras);
            return convertToEmailServiceStatus(doOperation(op, "IEmailService.sync"));
        }

        @Override
        public void pushModify(final long accountId) {
            LogUtils.d(TAG, "IEmailService.pushModify: %d", accountId);
            final Account account = Account.restoreAccountWithId(EasService.this, accountId);
            if (pingNeededForAccount(EasService.this, account)) {
                mSynchronizer.pushModify(account);
            } else {
                mSynchronizer.pushStop(accountId);
            }
        }

        @Override
        public Bundle validate(final HostAuthCompat hostAuthCom) {
            final HostAuth hostAuth = hostAuthCom.toHostAuth();
            final EasFolderSync operation = new EasFolderSync(EasService.this, hostAuth);
            doOperation(operation, "IEmailService.validate");
            return operation.getValidationResult();
        }

        @Override
        public int searchMessages(final long accountId, final SearchParams searchParams,
                final long destMailboxId) {
            final EasSearch operation = new EasSearch(EasService.this, accountId, searchParams,
                    destMailboxId);
            doOperation(operation, "IEmailService.searchMessages");
            return operation.getTotalResults();
        }

        @Override
        public void sendMeetingResponse(final long messageId, final int response) {
            EmailContent.Message msg = EmailContent.Message.restoreMessageWithId(EasService.this,
                    messageId);
            if (msg == null) {
                LogUtils.e(TAG, "Could not load message %d in sendMeetingResponse", messageId);
                return;
            }

            final EasSendMeetingResponse operation = new EasSendMeetingResponse(EasService.this,
                    msg.mAccountKey, msg, response);
            doOperation(operation, "IEmailService.sendMeetingResponse");
        }

        // [FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110
        public Bundle syncOof(long accountId, String command, Bundle content) {
            final OofOperation operation = new OofOperation(EasService.this, accountId, command,
                    content);
            doOperation(operation, "IEmailService.syncOof");
            return operation.getResultBundle();
        }

        // [FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110

        @Override
        public Bundle autoDiscover(final String username, final String password) {
            final String domain = EasAutoDiscover.getDomain(username);
            for (int attempt = 0; attempt <= EasAutoDiscover.ATTEMPT_MAX; attempt++) {
                LogUtils.d(TAG, "autodiscover attempt %d", attempt);
                final String uri = EasAutoDiscover.genUri(domain, attempt);
                //TS: jian.xu 2015-11-18 EMAIL BUGFIX-1111768 MOD_S
                //Note： maybe happen exception when execute auto discover, catch it.
                try {
                    Bundle result = autoDiscoverInternal(uri, attempt, username, password, true);
                    int resultCode = result.getInt(EmailServiceProxy.AUTO_DISCOVER_BUNDLE_ERROR_CODE);
                    if (resultCode != EasAutoDiscover.RESULT_BAD_RESPONSE) {
                        return result;
                    } else {
                        LogUtils.d(TAG, "got BAD_RESPONSE");
                    }
                }catch (IllegalArgumentException e){
                    LogUtils.w(TAG, e, "exception happen when execute auto discover");
                }
                //TS: jian.xu 2015-11-18 EMAIL BUGFIX-1111768 MOD_E
            }
            return null;
        }

        private Bundle autoDiscoverInternal(final String uri, final int attempt,
                final String username, final String password,
                final boolean canRetry) {
            final EasAutoDiscover op = new EasAutoDiscover(EasService.this, uri, attempt,
                    username, password);
            final int result = op.performOperation();
            if (result == EasAutoDiscover.RESULT_REDIRECT) {
                // Try again recursively with the new uri. TODO we should limit the number of
                // redirects.
                final String redirectUri = op.getRedirectUri();
                //TS: junwei-xu 2015-06-23 EMAIL BUGFIX-1027519 MOD_S
                // Notes: check redirectUri, if it is null, it will throw IllegalArgumentException
                if (!TextUtils.isEmpty(redirectUri)) {
                    return autoDiscoverInternal(redirectUri, attempt, username, password, canRetry);
                } else {
                    final Bundle bundle = new Bundle(1);
                    bundle.putInt(EmailServiceProxy.AUTO_DISCOVER_BUNDLE_ERROR_CODE,
                            EasAutoDiscover.RESULT_BAD_RESPONSE);
                    return bundle;
                }
                //TS: junwei-xu 2015-06-23 EMAIL BUGFIX-1027519 MOD_S
            } else if (result == EasAutoDiscover.RESULT_SC_UNAUTHORIZED) {
                if (canRetry && username.contains("@")) {
                    // Try again using the bare user name
                    final int atSignIndex = username.indexOf('@');
                    final String bareUsername = username.substring(0, atSignIndex);
                    LogUtils.d(TAG, "%d received; trying username: %s", result, atSignIndex);
                    // Try again recursively, but this time don't allow retries for username.
                    return autoDiscoverInternal(uri, attempt, bareUsername, password, false);
                } else {
                    // Either we're already on our second try or the username didn't have an "@"
                    // to begin with. Either way, failure.
                    final Bundle bundle = new Bundle(1);
                    bundle.putInt(EmailServiceProxy.AUTO_DISCOVER_BUNDLE_ERROR_CODE,
                            EasAutoDiscover.RESULT_OTHER_FAILURE);
                    return bundle;
                }
            } else if (result != EasAutoDiscover.RESULT_OK) {
                // Return failure, we'll try again with an alternate address
                final Bundle bundle = new Bundle(1);
                bundle.putInt(EmailServiceProxy.AUTO_DISCOVER_BUNDLE_ERROR_CODE,
                        EasAutoDiscover.RESULT_BAD_RESPONSE);
                return bundle;
            }
            // Success.
            return op.getResultBundle();
        }

        @Override
        public void setLogging(final int flags) {
            LogUtils.d(TAG, "IEmailService.setLogging");
        }

        @Override
        public void deleteExternalAccountPIMData(final String emailAddress) {
            LogUtils.d(TAG, "IEmailService.deleteAccountPIMData");
            if (emailAddress != null) {
                // TODO: stop pings
                final Context context = EasService.this;
                EasSyncContacts.wipeAccountFromContentProvider(context, emailAddress);
                //TS: chao-zhang 2015-06-30 EMAIL BUGFIX-1005857 ADD_S
                EasSyncCalendar.wipeAccountFromContentProvider(context, emailAddress,-1);
                //TS: chao-zhang 2015-06-30 EMAIL BUGFIX-1005857 END_S
            }
        }

        public int getApiVersion() {
            return EmailServiceVersion.CURRENT;
        }

        // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_S
        /**
         * M: Added for Exchange Partial download request
         *Use ItemOperaions commend to instead Sync commend.resore to default kk4.4
         * @param messageId the Id of email to be completely fetched
         */
        @Override
        public int fetchMessage(long messageId) {
            LogUtils.i("Email_ccx", "IEmailService.fetchMessage: %d", messageId);
            int status = -1;
            Message msg = Message.restoreMessageWithId(EasService.this, messageId);
            if (msg == null) {
                LogUtils.e("Email_ccx", "Retrive msg faild, messageId:" + messageId);
                return 0;
            }
            try {
                final EasLoadMore operation = new EasLoadMore(EasService.this, msg.mAccountKey, msg);
                status = convertToEmailServiceStatus(doOperation(operation,
                        "IEmailService.loadMore"));
            } finally {
                if (status != EmailServiceStatus.SUCCESS) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }
        // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_E
    };

    /**
     * Content selection string for getting all accounts that are configured for push.
     * TODO: Add protocol check so that we don't get e.g. IMAP accounts here.
     * (Not currently necessary but eventually will be.)
     */
    private static final String PUSH_ACCOUNTS_SELECTION =
            EmailContent.AccountColumns.SYNC_INTERVAL +
                    "=" + Integer.toString(Account.CHECK_INTERVAL_PUSH);

    /** {@link AsyncTask} to restart pings for all accounts that need it. */
    private class RestartPingsTask extends AsyncTask<Void, Void, Void> {
        private boolean mHasRestartedPing = false;

        @Override
        protected Void doInBackground(Void... params) {
            final Cursor c = EasService.this.getContentResolver().query(Account.CONTENT_URI,
                    Account.CONTENT_PROJECTION, PUSH_ACCOUNTS_SELECTION, null, null);
            if (c != null) {
                try {
                    while (c.moveToNext()) {
                        final Account account = new Account();
                        LogUtils.d(TAG, "RestartPingsTask starting ping for %s", account);
                        account.restore(c);
                        if (EasService.this.pingNeededForAccount(EasService.this, account)) {
                            mHasRestartedPing = true;
                            EasService.this.mSynchronizer.pushModify(account);
                        }
                    }
                } finally {
                    c.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!mHasRestartedPing) {
                LogUtils.d(TAG, "RestartPingsTask did not start any pings.");
                EasService.this.mSynchronizer.stopServiceIfIdle();
            }
        }
    }

    public EasService() {
        super();
        mSynchronizer = new PingSyncSynchronizer(this);
    }

    @Override
    public void onCreate() {
        LogUtils.d(TAG, "EasService.onCreate");
        super.onCreate();
        TempDirectory.setTempDirectory(this);
        EmailContent.init(this);
        AUTHORITIES_TO_SYNC = new String[] {
                EmailContent.AUTHORITY,
                CalendarContract.AUTHORITY,
                ContactsContract.AUTHORITY
        };

        // Restart push for all accounts that need it. Because this requires DB loads, we do it in
        // an AsyncTask, and we startService to ensure that we stick around long enough for the
        // task to complete. The task will stop the service if necessary after it's done.
        startService(new Intent(this, EasService.class));
        new RestartPingsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        mSynchronizer.stopAllPings();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null &&
                TextUtils.equals(Eas.EXCHANGE_SERVICE_INTENT_ACTION, intent.getAction())) {
            if (intent.getBooleanExtra(ServiceProxy.EXTRA_FORCE_SHUTDOWN, false)) {
                // We've been asked to forcibly shutdown. This happens if email accounts are
                // deleted, otherwise we can get errors if services are still running for
                // accounts that are now gone.
                // TODO: This is kind of a hack, it would be nicer if we could handle it correctly
                // if accounts disappear out from under us.
                LogUtils.d(TAG, "Forced shutdown, killing process");
                System.exit(-1);
            } else if (intent.getBooleanExtra(EXTRA_START_PING, false)) {
                LogUtils.d(LogUtils.TAG, "Restarting ping");
                final Account account = intent.getParcelableExtra(EXTRA_PING_ACCOUNT);
                final android.accounts.Account amAccount =
                                new android.accounts.Account(account.mEmailAddress,
                                    Eas.EXCHANGE_ACCOUNT_MANAGER_TYPE);
                EasPing.requestPing(amAccount);
            }
        }
        return START_STICKY;
    }

    public int doOperation(final EasOperation operation, final String loggingName) {
        LogUtils.i(TAG, "doOperation %s: %d", loggingName, operation.getAccountId());
        FileUtils.appendLog( TAG,"doOperation %s: %d", loggingName, operation.getAccountId());
        //TS: chao-zhang 2015-9-28 EMAIL BUGFIX_1010924 MOD_S
        //NOTE: why move catch from PPS.syncStart to here ? think that some task are waiting the lock,but if InterruptedException happened,
        //and the task will do next operation Immediately,that's bad behavior and broke the thread dispatch
        //But we can't avoid the InterruptedException happen during mCondition.await(),so discard the task can be better that do nothing.
        try {
            mSynchronizer.syncStart(operation.getAccountId());
        } catch (InterruptedException e) {
            LogUtils.e(TAG, "PSS InterruptedException acct:%d", operation.getAccountId());
          //TS: Gantao 2015-11-6 EMAIL BUGFIX_1109271 MOD_S
            //NOTE: call syncEnd to release the lock
            mSynchronizer.syncEnd(true, operation.getAccount(), operation.getAccountId());
            return EasOperation.RESULT_OTHER_FAILURE;
          //TS: Gantao 2015-11-6 EMAIL BUGFIX_1109271 MOD_E
        }
        //TS: chao-zhang 2015-9-28 EMAIL BUGFIX_1010924 MOD_E
        int result = EasOperation.RESULT_MIN_OK_RESULT;
        // TODO: Do we need a wakelock here? For RPC coming from sync adapters, no -- the SA
        // already has one. But for others, maybe? Not sure what's guaranteed for AIDL calls.
        // If we add a wakelock (or anything else for that matter) here, must remember to undo
        // it in the finally block below.
        // On the other hand, even for SAs, it doesn't hurt to get a wakelock here.
        try {
            LogUtils.i(TAG, "start performOperation %s: %d", loggingName, operation.getAccountId());
            FileUtils.appendLog(TAG, "start performOperation %s: %d", loggingName, operation.getAccountId());  //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
            result = operation.performOperation();
            LogUtils.d(TAG, "Operation result %d", result);
            FileUtils.appendLog(TAG, "Operation result %d", result);  //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD
            return result;
        } finally {
          //ccx: pass accountId, sometimes the account is null
            //mSynchronizer.syncEnd(result >= EasOperation.RESULT_MIN_OK_RESULT,
            mSynchronizer.syncEnd(result < EasOperation.RESULT_MIN_OK_RESULT,//[BUGFIX]-Mod by TSNJ.congyi.gu,26/11/2014,PR-853985
                    operation.getAccount(), operation.getAccountId());
        }
    }

    /**
     * Determine whether this account is configured with folders that are ready for push
     * notifications.
     * @param account The {@link Account} that we're interested in.
     * @param context The context
     * @return Whether this account needs to ping.
     */
    public static boolean pingNeededForAccount(final Context context, final Account account) {
        // Check account existence.
        if (account == null || account.mId == Account.NO_ACCOUNT) {
            LogUtils.d(TAG, "Do not ping: Account not found or not valid");
            return false;
        }

        // Check if account is configured for a push sync interval.
        if (account.mSyncInterval != Account.CHECK_INTERVAL_PUSH) {
            LogUtils.d(TAG, "Do not ping: Account %d not configured for push", account.mId);
            return false;
        }

        // Check security hold status of the account.
        if ((account.mFlags & Account.FLAGS_SECURITY_HOLD) != 0) {
            LogUtils.d(TAG, "Do not ping: Account %d is on security hold", account.mId);
            return false;
        }

        // Check if the account has performed at least one sync so far (accounts must perform
        // the initial sync before push is possible).
        if (EmailContent.isInitialSyncKey(account.mSyncKey)) {
            LogUtils.d(TAG, "Do not ping: Account %d has not done initial sync", account.mId);
            return false;
        }

        //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_S
        if (!Utility.canAutoSync(context, account)){
            LogUtils.d(TAG, "Do not ping: Account %d is on roaming mode and the mRequireManualSyncWhenRoaming policy is set", account.mId);
            return false;
        }
        //TS: jin.dong 2015-12-29 EMAIL FEATURE_1125784 ADD_E

        // Check that there's at least one mailbox that is both configured for push notifications,
        // and whose content type is enabled for sync in the account manager.
        final android.accounts.Account amAccount = new android.accounts.Account(
                        account.mEmailAddress, Eas.EXCHANGE_ACCOUNT_MANAGER_TYPE);

        final Set<String> authsToSync = getAuthoritiesToSync(amAccount, AUTHORITIES_TO_SYNC);
        // If we have at least one sync-enabled content type, check for syncing mailboxes.
        if (!authsToSync.isEmpty()) {
            final Cursor c = Mailbox.getMailboxesForPush(context.getContentResolver(), account.mId);
            if (c != null) {
                try {
                    while (c.moveToNext()) {
                        final int mailboxType = c.getInt(Mailbox.CONTENT_TYPE_COLUMN);
                        if (authsToSync.contains(Mailbox.getAuthority(mailboxType))) {
                            return true;
                        }
                    }
                } finally {
                    c.close();
                }
            }
        }
        LogUtils.d(TAG, "Do not ping: Account %d has no folders configured for push", account.mId);
        return false;
    }

    static public GalResult searchGal(final Context context, final long accountId,
                                      final String filter, final int limit) {
        final EasSearchGal operation = new EasSearchGal(context, accountId, filter, limit);
        // We don't use doOperation() here for two reasons:
        // 1. This is a static function, doOperation is not, and we don't have an instance of
        // EasService.
        // 2. All doOperation() does besides this is stop the ping and then restart it. This is
        // required during syncs, but not for GalSearches.
        final int result = operation.performOperation();
        if (result == EasSearchGal.RESULT_OK) {
            return operation.getResult();
        } else {
            return null;
        }
    }

    /**
     * Converts from an EasOperation status to a status code defined in EmailServiceStatus.
     * This is used to communicate the status of a sync operation to the caller.
     * @param easStatus result returned from an EasOperation
     * @return EmailServiceStatus
     */
    private int convertToEmailServiceStatus(int easStatus) {
        if (easStatus >= EasOperation.RESULT_MIN_OK_RESULT) {
            return EmailServiceStatus.SUCCESS;
        }
        switch (easStatus) {
            case EasOperation.RESULT_ABORT:
            case EasOperation.RESULT_RESTART:
                // This should only happen if a ping is interruped for some reason. We would not
                // expect see that here, since this should only be called for a sync.
                LogUtils.e(TAG, "Abort or Restart easStatus");
                return EmailServiceStatus.SUCCESS;

            case EasOperation.RESULT_TOO_MANY_REDIRECTS:
                return EmailServiceStatus.INTERNAL_ERROR;

            case EasOperation.RESULT_NETWORK_PROBLEM:
                // This is due to an IO error, we need the caller to know about this so that it
                // can let the syncManager know.
                return EmailServiceStatus.IO_ERROR;

            case EasOperation.RESULT_FORBIDDEN:
            case EasOperation.RESULT_AUTHENTICATION_ERROR:
                return EmailServiceStatus.LOGIN_FAILED;

            case EasOperation.RESULT_PROVISIONING_ERROR:
                return EmailServiceStatus.PROVISIONING_ERROR;

            case EasOperation.RESULT_CLIENT_CERTIFICATE_REQUIRED:
                return EmailServiceStatus.CLIENT_CERTIFICATE_ERROR;

            case EasOperation.RESULT_PROTOCOL_VERSION_UNSUPPORTED:
                return EmailServiceStatus.PROTOCOL_ERROR;

            case EasOperation.RESULT_INITIALIZATION_FAILURE:
            case EasOperation.RESULT_HARD_DATA_FAILURE:
            case EasOperation.RESULT_OTHER_FAILURE:
                return EmailServiceStatus.INTERNAL_ERROR;

            case EasOperation.RESULT_NON_FATAL_ERROR:
                // We do not expect to see this error here: This should be consumed in
                // EasFullSyncOperation. The only case this occurs in is when we try to send
                // a message in the outbox, and there's some problem with the message locally
                // that prevents it from being sent. We return a
                LogUtils.e(TAG, "Other non-fatal error easStatus %d", easStatus);
                return EmailServiceStatus.SUCCESS;
        }
        LogUtils.e(TAG, "Unexpected easStatus %d", easStatus);
        return EmailServiceStatus.INTERNAL_ERROR;
    }


    /**
     * Determine which content types are set to sync for an account.
     * @param account The account whose sync settings we're looking for.
     * @param authorities All possible authorities we could care about.
     * @return The authorities for the content types we want to sync for account.
     */
    public static Set<String> getAuthoritiesToSync(final android.accounts.Account account,
                                                    final String[] authorities) {
        final HashSet<String> authsToSync = new HashSet();
        for (final String authority : authorities) {
            if (ContentResolver.getSyncAutomatically(account, authority)) {
                authsToSync.add(authority);
            }
        }
        return authsToSync;
    }
}
