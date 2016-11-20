package com.tct.exchange.eas;
/*
==========================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ==============================
*BUGFIX-969850  2015/4/17    gengkexue       [HOMO][ALWE] Corporate email synchronized again all folders instead of updating only new emails
*BUGFIX-1022349  2015/6/12    zheng.zou      [Email] Hignly abnormal power consumption during exchange contact sync.
*TASK-869664     2015/11/25   zheng.zou      [Email]Android M Permission Upgrade
===========================================================================
*/

import android.Manifest;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.tct.emailcommon.TrafficFlags;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.CommandStatusException;
import com.tct.exchange.Eas;
import com.tct.exchange.EasResponse;
import com.tct.exchange.Exchange;
import com.tct.exchange.ExchangePreferences;
import com.tct.exchange.adapter.AbstractSyncParser;
import com.tct.exchange.adapter.Parser;
import com.tct.exchange.adapter.Serializer;
import com.tct.exchange.adapter.Tags;
import com.tct.permission.PermissionUtil;

import org.apache.http.HttpEntity;

import java.io.IOException;

/**
 * Performs an EAS sync operation for one folder (excluding mail upsync).
 * TODO: Merge with {@link EasSync}, which currently handles mail upsync.
 */
public class EasSyncBase extends EasOperation {

    private static final String TAG = Eas.LOG_TAG;

    public static final int RESULT_DONE = 0;
    public static final int RESULT_MORE_AVAILABLE = 1;

    private boolean mInitialSync;
    private final Mailbox mMailbox;
    private EasSyncCollectionTypeBase mCollectionTypeHandler;

    private int mNumWindows;
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    // Prompt"Internal error".
    private boolean mDownloadFlag = false;
    private long mMsgId;
    private String mMsgServerId = "";

    public EasSyncBase(final Context context, final Account account, final Mailbox mailbox,
            final Bundle extras) {
        super(context, account);
        mMailbox = mailbox;
        mDownloadFlag = extras.getBoolean("DOWNLOAD_FLAG", false);
        mMsgId = extras.getLong("MESSAGE_ID", -2);
        // msgId=-2,means no mesage,we quit querying from DB.
        if (mMsgId == -2) {
            return;
        }
        Message msg = Message.restoreMessageWithId(context, mMsgId);
        if (msg != null) {
            mMsgServerId = msg.mServerId;
        }
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
    // TODO: Convert to accountId when ready to convert to EasService.
    public EasSyncBase(final Context context, final Account account, final Mailbox mailbox) {
        super(context, account);
        mMailbox = mailbox;
    }

    /**
     * Get the sync key for this mailbox.
     * @return The sync key for the object being synced. "0" means this is the first sync. If
     *      there is an error in getting the sync key, this function returns null.
     */
    protected String getSyncKey() {
        if (mMailbox == null) {
            return null;
        }
        if (mMailbox.mSyncKey == null) {
            mMailbox.mSyncKey = "0";
        }
        return mMailbox.mSyncKey;
    }

    /**
     * As Sync commend can be used to sync single Message,so discard the ItemOperation Commend.
     * @see com.tct.exchange.eas.EasOperation#getCommand()
     */
    @Override
    protected String getCommand() {
        return "Sync";
    }

    @Override
    public boolean init(final boolean allowReload) {
        final boolean result = super.init(allowReload);
        if (result) {
            mCollectionTypeHandler = getCollectionTypeHandler(mMailbox.mType);
            if (mCollectionTypeHandler == null) {
                return false;
            }
            // Set up traffic stats bookkeeping.
            final int trafficFlags = TrafficFlags.getSyncFlags(mContext, mAccount);
            TrafficStats.setThreadStatsTag(trafficFlags | mCollectionTypeHandler.getTrafficFlag());
        }
        return result;
    }

    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    /*
     * In kk4.4,exchange use SYNC commend to get mails. but not support
     * single message sync if the procol>2.5(exchange2003), So we use
     * ITEM_OPTIONS commend to remedy this neglect. NOTE that we only use the
     * method in DownloadOptions function.
     * After L.sync commend can also sync single Message,so discard the ItemOperation commend.
     */
    @Override
    protected HttpEntity getRequestEntity() throws IOException {
        final String className = Eas.getFolderClass(mMailbox.mType);
        final String syncKey = getSyncKey();
        LogUtils.d(TAG, "Syncing account %d mailbox %d (class %s) with syncKey %s", mAccount.mId,
                mMailbox.mId, className, syncKey);
        mInitialSync = EmailContent.isInitialSyncKey(syncKey);
        final Serializer s = new Serializer();
        s.start(Tags.SYNC_SYNC);
        s.start(Tags.SYNC_COLLECTIONS);
        s.start(Tags.SYNC_COLLECTION);
        // The "Class" element is removed in EAS 12.1 and later versions
        if (getProtocolVersion() < Eas.SUPPORTED_PROTOCOL_EX2007_SP1_DOUBLE) {
            s.data(Tags.SYNC_CLASS, className);
        }
        s.data(Tags.SYNC_SYNC_KEY, syncKey);
        s.data(Tags.SYNC_COLLECTION_ID, mMailbox.mServerId);
        mCollectionTypeHandler.setSyncOptions(mContext, s, getProtocolVersion(), mAccount, mMailbox,
                mInitialSync, mNumWindows);
        s.end().end().end().done();

        return makeEntity(s);
    }

    /*
     * In order to use ITEM_OPETIONS commends to sync single message, we should
     * add different parser(FetchMessageParser) to parse it.
     * In L,use EmailSyncParser can do the jobs.
     */
    @Override
    protected int handleResponse(final EasResponse response)
            throws IOException, CommandStatusException {
        try {
            final AbstractSyncParser parser = mCollectionTypeHandler.getParser(mContext, mAccount,
                    mMailbox, response.getInputStream());
            parser.setEasCall(mDownloadFlag);
            final boolean moreAvailable = parser.parse();
            if (moreAvailable) {
                return RESULT_MORE_AVAILABLE;
            }
        } catch (final Parser.EmptyStreamException e) {
            // This indicates a compressed response which was empty, which is OK.
        }
        return RESULT_DONE;
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E

    @Override
    public int performOperation() {
        int result = RESULT_MORE_AVAILABLE;
        mNumWindows = 1;
        final String key = getSyncKey();
        while (result == RESULT_MORE_AVAILABLE) {
            result = super.performOperation();
            if (result == RESULT_MORE_AVAILABLE || result == RESULT_DONE) {
                mCollectionTypeHandler.cleanup(mContext, mAccount);
            }
            // TODO: Clear pending request queue.
            final String newKey = getSyncKey();
            if (result == RESULT_MORE_AVAILABLE && key.equals(newKey)) {
                LogUtils.e(TAG,
                        "Server has more data but we have the same key: %s numWindows: %d",
                        key, mNumWindows);
                mNumWindows++;
            } else {
                mNumWindows = 1;
            }
        }

        // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_S
        /** M: "Bad Sync Key" recovery course is about to finish,
        here just removed the stale mails from local DB @{ */
        if (mMailbox.mId == Exchange.sBadSyncKeyMailboxId) {
            // Delete the stale-flagged local mails
            final String selection = MessageColumns.DIRTY + " = 1 ";
            int rowDeleted = mContext.getContentResolver().delete(Message.CONTENT_URI, selection , null);
            LogUtils.i(Eas.BSK_TAG, rowDeleted + " local dirty mails were deleted");

            Exchange.sBadSyncKeyMailboxId = Exchange.NO_BSK_MAILBOX;
            ExchangePreferences pref = ExchangePreferences.getPreferences(mContext);
            pref.setBadSyncKeyMailboxId(Exchange.NO_BSK_MAILBOX);
            pref.setRemovedStaleMails(false);
            LogUtils.i(Eas.BSK_TAG, "Bad sync key recovery process is finished");
        }
        /** @} */
        // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_E
        // TS: zheng.zou 2015-06-12 EXCHANGE BUGFIX-1022349 ADD_S
        // note : upload local change to server in small sections and in several sync operation,
        // because the change maybe too large to upload to server in on sync operation.
        if (mMailbox.mType == Mailbox.TYPE_CONTACTS) {
            final int maxSyncCount = 10;    //set max sync count to avoid dead loop
            int count = 0;
            while (mCollectionTypeHandler.hasLocalChange(mContext, mAccount) && count < maxSyncCount) {
                LogUtils.i(TAG,"contact has more change in local side, try another upsync operation");
                result = super.performOperation();
                if (result == RESULT_MORE_AVAILABLE || result == RESULT_DONE) {
                    mCollectionTypeHandler.cleanup(mContext, mAccount);
                }
                count++;
            }
        }
        // TS: zheng.zou 2015-06-12 EXCHANGE BUGFIX-1022349 ADD_E
        return result;
    }

    @Override
    protected long getTimeout() {
        if (mInitialSync) {
            return 120 * DateUtils.SECOND_IN_MILLIS;
        }
        return super.getTimeout();
    }

    /**
     * Get an instance of the correct {@link EasSyncCollectionTypeBase} for a specific collection
     * type.
     * @param type The type of the {@link Mailbox} that we're trying to sync.
     * @return An {@link EasSyncCollectionTypeBase} appropriate for this type.
     */
    private EasSyncCollectionTypeBase getCollectionTypeHandler(final int type) {
        switch (type) {
            case Mailbox.TYPE_MAIL:
            case Mailbox.TYPE_INBOX:
            case Mailbox.TYPE_DRAFTS:
            case Mailbox.TYPE_SENT:
            case Mailbox.TYPE_TRASH:
            case Mailbox.TYPE_JUNK:
                return new EasSyncMail(mDownloadFlag, mMsgId);  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
            case Mailbox.TYPE_CALENDAR: {
                //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_S
                if (!PermissionUtil.checkPermissionAndLaunchExplain(mContext, Manifest.permission.READ_CALENDAR)){
                    LogUtils.i(LOG_TAG, "permission %s needed in getCollectionTypeHandler", Manifest.permission.READ_CALENDAR);
                    return null;
                }
                //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_E
                return new EasSyncCalendar(mContext, mAccount, mMailbox);
            }
            case Mailbox.TYPE_CONTACTS:
                return new EasSyncContacts(mAccount.mEmailAddress);
            default:
                LogUtils.e(LOG_TAG, "unexpected collectiontype %d", type);
                return null;
        }
    }
}
