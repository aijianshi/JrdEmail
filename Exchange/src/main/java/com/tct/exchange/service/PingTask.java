/*
 * Copyright (C) 2013 The Android Open Source Project
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
=========================================================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ============================================================
*BUGFIX-1049125  2015/7/23    chaozhang      [Monitor][Email][Data usage]Sometimes exchange data usage is very large on 4G mode.
*BUGFIX-1100762   2015-10-19   jin.dong      [Email]The data usage will increase quickly if the password is invalid.
*BUGFIX-1106176  2015/11/04   zheng.zou      Battery performance is much worse after FOTA to 7SRA software
*=========================================================================================================
*/
package com.tct.exchange.service;

import android.content.Context;
import android.os.AsyncTask;

import com.tct.emailcommon.provider.Account;
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.Eas;
import com.tct.exchange.adapter.PingParser;
import com.tct.exchange.eas.EasOperation;
import com.tct.exchange.eas.EasPing;

/**
 * Thread management class for Ping operations.
 */
public class PingTask extends AsyncTask<Void, Void, Void> {
    private final EasPing mOperation;
    private final PingSyncSynchronizer mPingSyncSynchronizer;

    private static final String TAG = Eas.LOG_TAG;

    public PingTask(final Context context, final Account account,
            final android.accounts.Account amAccount,
            final PingSyncSynchronizer pingSyncSynchronizer) {
        assert pingSyncSynchronizer != null;
        mOperation = new EasPing(context, account, amAccount);
        mPingSyncSynchronizer = pingSyncSynchronizer;
    }

    /** Start the ping loop. */
    public void start() {
        executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    /** Abort the ping loop (used when another operation interrupts the ping). */
    public void stop() {
        mOperation.abort();
    }

    /** Restart the ping loop (used when a ping request happens during a ping). */
    public void restart() {
        mOperation.restart();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LogUtils.i(TAG, "Ping task starting for %d", mOperation.getAccountId());
        int pingStatus;
        int tryTimes = 0;
        try {
            do {
                pingStatus = mOperation.doPing();
                tryTimes++;
            } while (PingParser.shouldPingAgain(pingStatus,tryTimes));
        } catch (final Exception e) {
            // TODO: This is hacky, try to be cleaner.
            // If we get any sort of exception here, treat it like the ping returned a connection
            // failure.
            LogUtils.e(TAG, e, "Ping exception for account %d", mOperation.getAccountId());
            pingStatus = EasOperation.RESULT_NETWORK_PROBLEM;
        }
        LogUtils.i(TAG, "Ping task ending with status: %d", pingStatus);
        //TS: chaozhang 2015-07-23 EMAIL BUGFIX_1049125 MOD_S
        //NOTE:Use tryTimes to control the loop,after exceed 10 times,end the loop,and end Ping.
        //TS: jin.dong 2015-10-15 EMAIL BUGFIX_1099694 MOD_S
        //NOTE: should not ping again when encounter AUTHENTICATION_ERROR
        if (isBadPing(pingStatus)|| (tryTimes > PingParser.MAX_TIMES_RETRY_WITH_BAD_PING  //TS: zheng.zou 2015-11-04 EMAIL BUGFIX_1106176 MOD
                && (pingStatus == PingParser.STATUS_REQUEST_INCOMPLETE || pingStatus == PingParser.STATUS_REQUEST_MALFORMED
        ))) {  //TS: jin.dong 2015-10-15 EMAIL BUGFIX_1099694 MOD_E
            pingStatus = PingParser.STATUS_BAD_PING;
            LogUtils.e(TAG,"Bad Ping happen,and retry time exceed MAX 10 times,end the loop");
        }
        mPingSyncSynchronizer.pingEnd(mOperation.getAccountId(), mOperation.getAmAccount(),pingStatus);
        //TS: chaozhang 2015-07-23 EMAIL BUGFIX_1049125 MOD_E
        return null;
    }

    //TS: zheng.zou 2015-11-04 EMAIL BUGFIX_1106176 ADD_S
    private boolean isBadPing(int pingStatus) {
        return pingStatus == EasPing.RESULT_AUTHENTICATION_ERROR
                || pingStatus == EasPing.RESULT_OTHER_FAILURE;
    }
    //TS: zheng.zou 2015-11-04 EMAIL BUGFIX_1106176 ADD_E

    @Override
    protected void onCancelled (Void result) {
        // TODO: This is also hacky, should have a separate result code at minimum.
        // If the ping is cancelled, make sure it reports something to the sync adapter.
        LogUtils.w(TAG, "Ping cancelled for %d", mOperation.getAccountId());
        mPingSyncSynchronizer.pingEnd(mOperation.getAccountId(), mOperation.getAmAccount(),EasOperation.RESULT_ABORT);
    }
}
