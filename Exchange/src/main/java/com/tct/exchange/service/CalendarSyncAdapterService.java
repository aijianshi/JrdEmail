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
 *Tag        Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-1081026  2015/9/9    dongjing        [Vodafone][Monkey][Crash]Package <com.tct.exchange> crash during system test
 *BUGFIX-1106711     2015/12/10   jin.dong    [Android 6.0][Email]Can't sync contact if disable exchange permission of calendar
 ===========================================================================  */
/******************************************************************************/
package com.tct.exchange.service;

import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract.Events;
import android.util.Log;

import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent.MailboxColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.provider.ProviderUnavailableException;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.Eas;

public class CalendarSyncAdapterService extends AbstractSyncAdapterService {
    private static final String TAG = LogUtils.TAG;
    private static final String ACCOUNT_AND_TYPE_CALENDAR =
        MailboxColumns.ACCOUNT_KEY + "=? AND " + MailboxColumns.TYPE + '=' + Mailbox.TYPE_CALENDAR;
    private static final String DIRTY_IN_ACCOUNT =
        Events.DIRTY + "=1 AND " + Events.ACCOUNT_NAME + "=?";

    private static final Object sSyncAdapterLock = new Object();
    private static AbstractThreadedSyncAdapter sSyncAdapter = null;

    public CalendarSyncAdapterService() {
        super();
    }

    @Override
    protected AbstractThreadedSyncAdapter getSyncAdapter() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapterImpl(this);
            }
            return sSyncAdapter;
        }
    }

    private class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        public SyncAdapterImpl(Context context) {
            super(context, true /* autoInitialize */);
        }

        @Override
        public void onPerformSync(android.accounts.Account acct, Bundle extras,
                String authority, ContentProviderClient provider, SyncResult syncResult) {
            if (LogUtils.isLoggable(TAG, Log.DEBUG)) {
                LogUtils.d(TAG, "onPerformSync calendar: %s, %s",
                        acct.toString(), extras.toString());
            } else {
                LogUtils.i(TAG, "onPerformSync calendar: %s", extras.toString());
            }

            if (!waitForService()) {
                // The service didn't connect, nothing we can do.
                return;
            }
            final Account emailAccount = Account.restoreAccountWithAddress(
                    CalendarSyncAdapterService.this, acct.name);
            if (emailAccount == null) {
                // There could be a timing issue with onPerformSync() being called and
                // the account being removed from our database.
                LogUtils.w(TAG,
                        "onPerformSync() - Could not find an Account, skipping calendar sync.");
                return;
            }

            // TODO: is this still needed?
            if (extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD)) {
                final Cursor c = getContentResolver().query(Events.CONTENT_URI,
                        new String[] {Events._ID}, DIRTY_IN_ACCOUNT,
                        new String[] {acct.name}, null);
                if (c == null) {
                    LogUtils.e(TAG, "Null changes cursor in CalendarSyncAdapterService");
                    return;
                }
                try {
                    if (!c.moveToFirst()) {
                        if (Eas.USER_LOG) {
                            LogUtils.d(TAG, "No changes for " + acct.name);
                        }
                        return;
                    }
                } finally {
                    c.close();
                }
            }

            // TODO: move this logic to some common place.
            // Push only means this sync request should only refresh the ping (either because
            // settings changed, or we need to restart it for some reason).
            final boolean pushOnly = Mailbox.isPushOnlyExtras(extras);
            final int mailboxType = extras.getInt(Mailbox.SYNC_EXTRA_MAILBOX_TYPE,
                    Mailbox.TYPE_NONE);        // TS: jin.dong 2015-12-10 EXCHANGE BUGFIX-1106711 ADD

            if (pushOnly) {
                LogUtils.d(TAG, "onPerformSync calendar: mailbox push only");
                if (mEasService != null) {
                    try {
                        mEasService.pushModify(emailAccount.mId);
                        return;
                    } catch (final RemoteException re) {
                        LogUtils.e(TAG, re, "While trying to pushModify within onPerformSync");
                        // TODO: how to handle this?
                    }
                }
                return;
            } else {
                // TS: jin.dong 2015-12-10 EXCHANGE BUGFIX-1106711 ADD_S
                //should only sync calendar type mailbox
                if (mailboxType == Mailbox.TYPE_NONE) {
                    extras.putInt(Mailbox.SYNC_EXTRA_MAILBOX_TYPE, Mailbox.TYPE_CALENDAR);
                }
                // TS: jin.dong 2015-12-10 EXCHANGE BUGFIX-1106711 ADD_E
                try {
                    final int result = mEasService.sync(emailAccount.mId, extras);
                    writeResultToSyncResult(result, syncResult);
                    if (syncResult.stats.numAuthExceptions > 0 &&
                            result != EmailServiceStatus.PROVISIONING_ERROR) {
                        showAuthNotification(emailAccount.mId, emailAccount.mEmailAddress);
                    }
                } catch (RemoteException e) {
                    LogUtils.e(TAG, e, "While trying to pushModify within onPerformSync");
                } catch (ProviderUnavailableException e) {//TS: jin.dong 2015-09-09 EMAIL BUGFIX_1079491 ADD_S
                    LogUtils.e(TAG, e,
                            "While do calendar onPerformSync,may email account has be deleted or uninstalled");
                }//TS: jin.dong 2015-09-09 EMAIL BUGFIX_1079491 ADD_E
            }

            LogUtils.d(TAG, "onPerformSync calendar: finished");
        }
    }
}
