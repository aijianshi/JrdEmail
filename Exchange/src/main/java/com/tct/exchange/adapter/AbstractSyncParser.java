/*
 * Copyright (C) 2008-2009 Marc Blank
 * Licensed to The Android Open Source Project.
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
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-969850  2015/4/17    gengkexue       [HOMO][ALWE] Corporate email synchronized again all folders instead of updating only new emails
 *BUGFIX-1005857 2015/06/30   chao-zhang      [Android5.0][Calendar_v5.1.0.1.0020.0][Monitor]The events was missing after sync finished by using exchange account
 ===========================================================================
*/

package com.tct.exchange.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.MailboxColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.CommandStatusException;
import com.tct.exchange.Eas;
import com.tct.exchange.CommandStatusException.CommandStatus;
import com.tct.exchange.Exchange;
import com.tct.exchange.ExchangePreferences;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for the Email and PIM sync parsers
 * Handles the basic flow of syncKeys, looping to get more data, handling errors, etc.
 * Each subclass must implement a handful of methods that relate specifically to the data type
 *
 */
public abstract class AbstractSyncParser extends Parser {
    private static final String TAG = Eas.LOG_TAG;

    protected Mailbox mMailbox;
    protected Account mAccount;
    protected Context mContext;
    protected ContentResolver mContentResolver;

    private boolean mLooping;
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    protected boolean mDownloadFlag;
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E

    public AbstractSyncParser(final Context context, final ContentResolver resolver,
            final InputStream in, final Mailbox mailbox, final Account account) throws IOException {
        super(in);
        init(context, resolver, mailbox, account);
    }

    public AbstractSyncParser(InputStream in, AbstractSyncAdapter adapter) throws IOException {
        super(in);
        init(adapter);
    }

    public AbstractSyncParser(Parser p, AbstractSyncAdapter adapter) throws IOException {
        super(p);
        init(adapter);
    }

    public AbstractSyncParser(final Parser p, final Context context, final ContentResolver resolver,
        final Mailbox mailbox, final Account account) throws IOException {
        super(p);
        init(context, resolver, mailbox, account);
    }

    private void init(final AbstractSyncAdapter adapter) {
        init(adapter.mContext, adapter.mContext.getContentResolver(), adapter.mMailbox,
                adapter.mAccount);
    }

    private void init(final Context context, final ContentResolver resolver, final Mailbox mailbox,
            final Account account) {
        mContext = context;
        mContentResolver = resolver;
        mMailbox = mailbox;
        mAccount = account;
    }

    /**
     * Read, parse, and act on incoming commands from the Exchange server
     * @throws IOException if the connection is broken
     * @throws CommandStatusException
     */
    public abstract void commandsParser() throws IOException, CommandStatusException;

    /**
     * Read, parse, and act on server responses
     * @throws IOException
     */
    public abstract void responsesParser() throws IOException;

    /**
     * Commit any changes found during parsing
     * @throws IOException
     */
    public abstract void commit() throws IOException, RemoteException,
            OperationApplicationException;

    public boolean isLooping() {
        return mLooping;
    }

    /**
     * Skip through tags until we reach the specified end tag
     * @param endTag the tag we end with
     * @throws IOException
     */
    public void skipParser(int endTag) throws IOException {
        while (nextTag(endTag) != END) {
            skipTag();
        }
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    /**
     * The flag used to indicate the user clicked the downloadRemain button.
     */
    public void setEasCall(boolean flag) {
        mDownloadFlag = flag;
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
    /**
     * Loop through the top-level structure coming from the Exchange server
     * Sync keys and the more available flag are handled here, whereas specific data parsing
     * is handled by abstract methods implemented for each data class (e.g. Email, Contacts, etc.)
     * @throws CommandStatusException
     */
    @Override
    public boolean parse() throws IOException, CommandStatusException {
        int status;
        boolean moreAvailable = false;
        boolean newSyncKey = false;
        mLooping = false;
        // If we're not at the top of the xml tree, throw an exception
        if (nextTag(START_DOCUMENT) != Tags.SYNC_SYNC) {
            throw new EasParserException();
        }

        boolean mailboxUpdated = false;
        ContentValues cv = new ContentValues();

        // Loop here through the remaining xml
        while (nextTag(START_DOCUMENT) != END_DOCUMENT) {
            if (tag == Tags.SYNC_COLLECTION || tag == Tags.SYNC_COLLECTIONS) {
                // Ignore these tags, since we've only got one collection syncing in this loop
            } else if (tag == Tags.SYNC_STATUS) {
                // Status = 1 is success; everything else is a failure
                status = getValueInt();
                if (status != 1) {
                    // TS: zheng.zou 2016-04-5 EMAIL BUGFIX_1892303 ADD_S
                    LogUtils.w(TAG, "parse, get response status: " + status);
                    // TS: zheng.zou 2016-04-5 EMAIL BUGFIX_1892303 ADD_E
                    if (status == 3 || CommandStatus.isBadSyncKey(status)) {
                        // Must delete all of the data and start over with syncKey of "0"
                        mMailbox.mSyncKey = "0";
                        newSyncKey = true;

                        // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_S
                        /** M: Store bad sync key mailbox id in Preference in case of user rebooting the device
                            or Exchange process crashes during the recovery, or else we would loss the BSK status.
                            We ought to know there is still an unfinished bad sync key recovery and have to
                            go on finish it. We do not deal with Calendar & Contacts folders with the recovery
                            process at present @{ */
                        if (mMailbox.mType < Mailbox.TYPE_NOT_EMAIL) {
                            LogUtils.i(Eas.BSK_TAG, "Bad sync key occurs");
                            Exchange.sBadSyncKeyMailboxId = mMailbox.mId;
                            ExchangePreferences pref = ExchangePreferences.getPreferences(mContext);
                            pref.setBadSyncKeyMailboxId(mMailbox.mId);
                        }
                        /** @} */
                        // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_E
                        wipe();
                        // Indicate there's more so that we'll start syncing again
                        moreAvailable = true;
                    } else if (status == 16 || status == 5) {
                        // Status 16 indicates a transient server error (indeterminate state)
                        // Status 5 indicates "server error"; this tends to loop for a while so
                        // throwing IOException will at least provide backoff behavior
                        LogUtils.w(TAG,"parse abnormal, response status = "+status);//TS: jin.dong 2016-3-24 EMAIL BUGFIX_1567481 ADD
                        throw new IOException();
                    } else if (status == 8 || status == 12) {
                        // Status 8 is Bad; it means the server doesn't recognize the serverId it
                        // sent us.  12 means that we're being asked to refresh the folder list.
                        // We'll do that with 8 also...
                        // TODO: Improve this -- probably best to do this synchronously and then
                        // immediately retry the current sync.
                        final Bundle extras = new Bundle(1);
                        extras.putBoolean(Mailbox.SYNC_EXTRA_ACCOUNT_ONLY, true);
                        ContentResolver.requestSync(new android.accounts.Account(
                                mAccount.mEmailAddress, Eas.EXCHANGE_ACCOUNT_MANAGER_TYPE),
                                EmailContent.AUTHORITY, extras);
                        LogUtils.w(TAG,"parse abnormal, response status = "+status);//TS: jin.dong 2016-3-24 EMAIL BUGFIX_1567481 ADD
                        // We don't have any provision for telling the user "wait a minute while
                        // we sync folders"...
                        throw new IOException();
                    } else if (status == 7) {
                        // TODO: Fix this. The handling here used to be pretty bogus, and it's not
                        // obvious that simply forcing another resync makes sense here.
                        moreAvailable = true;
                    } else {
                        LogUtils.e(LogUtils.TAG, "Sync: Unknown status: " + status);
                        // Access, provisioning, transient, etc.
                        throw new CommandStatusException(status);
                    }
                }
            } else if (tag == Tags.SYNC_COMMANDS) {
                commandsParser();
            } else if (tag == Tags.SYNC_RESPONSES) {
                responsesParser();
            } else if (tag == Tags.SYNC_MORE_AVAILABLE) {
                moreAvailable = true;
            } else if (tag == Tags.SYNC_SYNC_KEY) {
                if (mMailbox.mSyncKey.equals("0")) {
                    moreAvailable = true;
                }
                String newKey = getValue();
                userLog("Parsed key for ", mMailbox.mDisplayName, ": ", newKey);
                if (!newKey.equals(mMailbox.mSyncKey)) {
                    mMailbox.mSyncKey = newKey;
                    cv.put(MailboxColumns.SYNC_KEY, newKey);
                    mailboxUpdated = true;
                    newSyncKey = true;
                }
           } else {
                skipTag();
           }
        }

        // If we don't have a new sync key, ignore moreAvailable (or we'll loop)
        if (moreAvailable && !newSyncKey) {
            LogUtils.e(TAG, "Looping detected");
            mLooping = true;
        }

        // Commit any changes
        try {
            commit();
            if (mailboxUpdated) {
                mMailbox.update(mContext, cv);
            }
        } catch (RemoteException e) {
            LogUtils.e(TAG, "Failed to commit changes", e);
        } catch (OperationApplicationException e) {
            LogUtils.e(TAG, "Failed to commit changes", e);
        }
        // Let the caller know that there's more to do
        if (moreAvailable) {
            userLog("MoreAvailable");
        }
        return moreAvailable;
    }

    abstract protected void wipe();
    //TS: chao-zhang 2015-06-30 EMAIL BUGFIX-1005857 ADD_S
    //Good For reuse thus codes,for better parser logs.no matter care the perference of the log print.
    //use DEBUG to open or close it.
    //void userLog(String ...strings) {
        // TODO: Convert to other logging types?
        //mService.userLog(strings);
    //}

    //void userLog(String string, int num, String string2) {
        // TODO: Convert to other logging types?
        //mService.userLog(string, num, string2);
    //}
    /**
     * Convenience methods to do user logging (i.e. connection activity). Saves a bunch of
     * repetitive code.
     */
    public static void userLog(String string, int code, String string2) {
        if (LogUtils.isLoggable(TAG, LogUtils.DEBUG)) {
            userLog(string + code + string2);
        }
    }

    public static void userLog(String string, int code) {
        if (LogUtils.isLoggable(TAG, LogUtils.DEBUG)) {
            userLog(string + code);
        }
    }

    public static void userLog(String str, Exception e) {
        if (LogUtils.isLoggable(TAG, LogUtils.DEBUG)) {
            Log.d(TAG, str, e);
        }
    }

    /**
     * Standard logging for EAS. If user logging is active, we concatenate any arguments and log
     * them using Log.d We also check for file logging, and log appropriately
     * @param strings strings to concatenate and log
     */
    public static void userLog(String... strings) {
        if (LogUtils.isLoggable(TAG, LogUtils.DEBUG)) {
            String logText;
            if (strings.length == 1) {
                logText = strings[0];
            } else {
                StringBuilder sb = new StringBuilder(64);
                for (String string : strings) {
                    sb.append(string);
                }
                logText = sb.toString();
            }
            Log.d(TAG, logText);
        }
    }
    //TS: chao-zhang 2015-06-30 EMAIL BUGFIX-1005857 ADD_E
}
