package com.tct.exchange.eas;

import android.content.Context;
import android.database.Cursor;

import com.tct.emailcommon.TrafficFlags;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.SyncWindow;
import com.tct.emailcommon.utility.Utility;
import com.tct.exchange.Eas;
import com.tct.exchange.adapter.AbstractSyncParser;
import com.tct.exchange.adapter.EmailSyncParser;
import com.tct.exchange.adapter.Serializer;
import com.tct.exchange.adapter.Tags;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Subclass to handle sync details for mail collections.
 */
public class EasSyncMail extends EasSyncCollectionTypeBase {

    /**
     * The projection used for building the fetch request list.
     */
    private static final String[] FETCH_REQUEST_PROJECTION = { SyncColumns.SERVER_ID };
    private static final int FETCH_REQUEST_SERVER_ID = 0;

    private static final int EMAIL_WINDOW_SIZE = 10;
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    private boolean mDownloadFlag = false;
    private long  mMsgId;

    public EasSyncMail(boolean flag, long id) {
        super();
        mDownloadFlag = flag;
        mMsgId = id;

    }

    /*
     * 0 Truncate all body text.
     * 1 Truncate text over 4,096 characters.
     * 2 Truncate text over 5,120 characters.
     * 3 Truncate text over 7,168 characters.
     * 4 Truncate text over 10,240 characters.
     * 5 Truncate text over 20,480 characters.
     * 6 Truncate text over 51,200 characters.
     * 7 Truncate text over 102,400 characters.
     * 8 Do not truncate; send complete MIME data.
     */
    private String getEAS2_5_TruncationSize(int downloadOptions){
        String truncationSize;
        switch(downloadOptions){
            case 0:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_0;
                break;
            case 512:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_1;
                break;
            case 1 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_1;
                break;
            case 2 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_1;
                break;
            case 5 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_2;
                break;
            case 10 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_4;
                break;
            case 20 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_5;
                break;
            case 25 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_5;
                break;
            case 50 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_6;
                break;
            case 100 * 1024:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_7;
                break;
            default:
                truncationSize = Eas.EAS2_5_TRUNCATION_SIZE_8;
                break;
        }
        return truncationSize;
    }

    // [FEATURE]-Add-BEGIN by TSCD.Chao Zhang,05/10/2014,FR 644789.
    // we query from db to get the unsynced msg's serverid and add it
    // to mMessagesToFetch.
    public void getDownloadRemainMessage(Context context, long msgId,
            ArrayList<String> mMessagesToFetch) {
        String serverId = null;
        boolean exist = false;
        Message msg = Message.restoreMessageWithId(context, msgId);
        if (msg == null) {
            return;
        }
        serverId = msg.mServerId;
        if (mMessagesToFetch.size() > 0) {
            for (String id : mMessagesToFetch) {
                if (id.equals(serverId)) {
                    exist = true;
                    break;
                }
            }
        }
        mMessagesToFetch.clear();
        if (exist) {
            exist = false;
            mMessagesToFetch.add(serverId);
        }
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E

    @Override
    public int getTrafficFlag() {
        return TrafficFlags.DATA_EMAIL;
    }

    @Override
    public void setSyncOptions(final Context context, final Serializer s,
            final double protocolVersion, final Account account, final Mailbox mailbox,
            final boolean isInitialSync, final int numWindows) throws IOException {
        if (isInitialSync) {
            // No special options to set for initial mailbox sync.
            return;
        }
        // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_S
        // NOTE: Do not use messagesToFetch to judge the partually message here,We only use EasLoadMore
        // to assemble the fetching single message commend. ItemOperaions is more effective than Sync Commend,
        // So we still use ItemOPerations commend to do the downloadRemain jobs. sorry!!!

        // Check for messages that aren't fully loaded.
        // final ArrayList<String> messagesToFetch = addToFetchRequestList(context, mailbox);
        // The "empty" case is typical; we send a request for changes, and also specify a sync
        // window, body preference type (HTML for EAS 12.0 and later; MIME for EAS 2.5), and
        // truncation
        // If there are fetch requests, we only want the fetches (i.e. no changes from the server)
        // so we turn MIME support off.  Note that we are always using EAS 2.5 if there are fetch
        // requests
        if (true) {
            // Permanently delete if in trash mailbox
            // In Exchange 2003, deletes-as-moves tag = true; no tag = false
            // In Exchange 2007 and up, deletes-as-moves tag is "0" (false) or "1" (true)
            final boolean isTrashMailbox = mailbox.mType == Mailbox.TYPE_TRASH;
            if (protocolVersion < Eas.SUPPORTED_PROTOCOL_EX2007_DOUBLE) {
                if (!isTrashMailbox) {
                    s.tag(Tags.SYNC_DELETES_AS_MOVES);
                }
            } else {
                s.data(Tags.SYNC_DELETES_AS_MOVES, isTrashMailbox ? "0" : "1");
            }
            s.tag(Tags.SYNC_GET_CHANGES);

            final int windowSize = numWindows * EMAIL_WINDOW_SIZE;
            if (windowSize > MAX_WINDOW_SIZE  + EMAIL_WINDOW_SIZE) {
                throw new IOException("Max window size reached and still no data");
            }
            s.data(Tags.SYNC_WINDOW_SIZE,
                    String.valueOf(windowSize < MAX_WINDOW_SIZE ? windowSize : MAX_WINDOW_SIZE));
            s.start(Tags.SYNC_OPTIONS);
            // Set the lookback appropriately (EAS calls this a "filter")
            s.data(Tags.SYNC_FILTER_TYPE, getEmailFilter(account, mailbox));
            // Set the truncation amount for all classes
            if (protocolVersion >= Eas.SUPPORTED_PROTOCOL_EX2007_DOUBLE) {
                s.start(Tags.BASE_BODY_PREFERENCE);
                // HTML for email
                s.data(Tags.BASE_TYPE, Eas.BODY_PREFERENCE_HTML);
                // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
                // s.data(Tags.BASE_TRUNCATION_SIZE, Eas.EAS12_TRUNCATION_SIZE);
                if (account.getDownloadOptions() == Utility.ENTIRE_MAIL) {
                     s.data(Tags.BASE_TRUNCATION_SIZE, Eas.EAS12_TRUNCATION_SIZE);
                } else {
                    s.data(Tags.BASE_TRUNCATION_SIZE, String.valueOf(account.getDownloadOptions()));
                }
                // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
                s.end();
            } else {
                // Use MIME data for EAS 2.5
                s.data(Tags.SYNC_MIME_SUPPORT, Eas.MIME_BODY_PREFERENCE_MIME);
                // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
                if (account.getDownloadOptions() != Utility.ENTIRE_MAIL) {
                    s.data(Tags.SYNC_MIME_TRUNCATION,
                            getEAS2_5_TruncationSize(account.getDownloadOptions()));
                } else {
                    s.data(Tags.SYNC_MIME_TRUNCATION, Eas.EAS2_5_TRUNCATION_SIZE_8);
                }
                // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
            }
            s.end();
            // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_E
        } else {
            /* NOTE: If the message not fully loaded,The user only can use DownloadRemain button to reload the message.Discard the follow codes.
            // If we have any messages that are not fully loaded, ask for plain text rather than
            // MIME, to guarantee we'll get usable text body. This also means we should NOT ask for
            // new messages -- we only want data for the message explicitly fetched.

            // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
            //NOTE:when user only want to donwload one message,here it is.
            //warning:after 2007,the message body is html style.and it's use SYNC
            //commends,it can't use FETCH commend to fetch one message body,here
            //we use ITEM_OPTIONS commends instand.but it not a good way because
            //it use TEXT style to get the body.
            //After L,use Sync commend,do not use ItemOperation. and use Sync commend can get the Html content.
            if (mDownloadFlag) {
                getDownloadRemainMessage(context, mMsgId, messagesToFetch);
                s.start(Tags.SYNC_OPTIONS);
                s.start(Tags.BASE_BODY_PREFERENCE);
                // HTML for email
                s.data(Tags.BASE_TYPE, Eas.BODY_PREFERENCE_HTML);
                s.data(Tags.BASE_TRUNCATION_SIZE, Eas.EAS12_TRUNCATION_SIZE);
                 s.end();
                 s.end();
                // Add FETCH commands for messages that need a body (i.e. we
                // didn't find it during our
                // earlier sync; this happens only in EAS 2.5 where the body
                // couldn't be found after
                // parsing the message's MIME data).
                s.start(Tags.SYNC_COMMANDS);
                for (final String serverId : messagesToFetch) {
                    s.start(Tags.SYNC_FETCH).data(Tags.SYNC_SERVER_ID, serverId).end();
                }
                s.end();
                mDownloadFlag = false;
                return;
            }
            //NOTE:When downloadOption function active,there are some messages downloaded partially.
            //if we only use messagesToFetch.isEmpty() to juge how to sync.it's not a good way.
            //we must give user the chance to sync the new mail or manly refresh mailbox.
            //here it is.
            final boolean isTrashMailbox = mailbox.mType == Mailbox.TYPE_TRASH;
            if (protocolVersion < Eas.SUPPORTED_PROTOCOL_EX2007_DOUBLE) {
                if (!isTrashMailbox) {
                    s.tag(Tags.SYNC_DELETES_AS_MOVES);
                }
            } else {
                s.data(Tags.SYNC_DELETES_AS_MOVES, isTrashMailbox ? "0" : "1");
            }
            s.tag(Tags.SYNC_GET_CHANGES);

            final int windowSize = numWindows * EMAIL_WINDOW_SIZE;
            if (windowSize > MAX_WINDOW_SIZE  + EMAIL_WINDOW_SIZE) {
                throw new IOException("Max window size reached and still no data");
            }
            s.data(Tags.SYNC_WINDOW_SIZE,
                    String.valueOf(windowSize < MAX_WINDOW_SIZE ? windowSize : MAX_WINDOW_SIZE));
            s.start(Tags.SYNC_OPTIONS);
            // Set the lookback appropriately (EAS calls this a "filter")
            s.data(Tags.SYNC_FILTER_TYPE, getEmailFilter(account, mailbox));
            // Set the truncation amount for all classes
            if (protocolVersion >= Eas.SUPPORTED_PROTOCOL_EX2007_DOUBLE) {
                s.start(Tags.BASE_BODY_PREFERENCE);
                // HTML for email
                s.data(Tags.BASE_TYPE, Eas.BODY_PREFERENCE_HTML);
                if (account.getDownloadOptions() == Utility.ENTIRE_MAIL) {
                     s.data(Tags.BASE_TRUNCATION_SIZE, Eas.EAS12_TRUNCATION_SIZE);
                } else {
                    s.data(Tags.BASE_TRUNCATION_SIZE, String.valueOf(account.getDownloadOptions()));
                }
                s.end();
            } else {
                // Use MIME data for EAS 2.5
                s.data(Tags.SYNC_MIME_SUPPORT, Eas.MIME_BODY_PREFERENCE_MIME);
                if (account.getDownloadOptions() != Utility.ENTIRE_MAIL) {
                    s.data(Tags.SYNC_MIME_TRUNCATION,
                            getEAS2_5_TruncationSize(account.getDownloadOptions()));
                } else {
                    s.data(Tags.SYNC_MIME_TRUNCATION, Eas.EAS2_5_TRUNCATION_SIZE_8);
                }
            }
            s.end();
            // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
        */}
    }

    @Override
    public AbstractSyncParser getParser(final Context context, final Account account,
            final Mailbox mailbox, final InputStream is) throws IOException {
        return new EmailSyncParser(context, is, mailbox, account);
    }

    /**
     * Query the provider for partially loaded messages.
     * @return Server ids for partially loaded messages.
     */
    private ArrayList<String> addToFetchRequestList(final Context context, final Mailbox mailbox) {
        final ArrayList<String> messagesToFetch = new ArrayList<String>();
        final Cursor c = context.getContentResolver().query(Message.CONTENT_URI,
                FETCH_REQUEST_PROJECTION,  MessageColumns.FLAG_LOADED + "=" +
                Message.FLAG_LOADED_PARTIAL + " AND " +  MessageColumns.MAILBOX_KEY + "=?",
                new String[] {Long.toString(mailbox.mId)}, null);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    messagesToFetch.add(c.getString(FETCH_REQUEST_SERVER_ID));
                }
            } finally {
                c.close();
            }
        }
        return messagesToFetch;
    }

    /**
     * Get the sync window for this collection and translate it to EAS's value for that (EAS refers
     * to this as the "filter").
     * @param account The {@link Account} for this sync; its sync window is used if the mailbox
     *                doesn't specify an override.
     * @param mailbox The {@link Mailbox} for this sync.
     * @return The EAS string value for the sync window specified for this mailbox.
     */
    private String getEmailFilter(final Account account, final Mailbox mailbox) {
        final int syncLookback = mailbox.mSyncLookback == SyncWindow.SYNC_WINDOW_ACCOUNT
                ? account.mSyncLookback : mailbox.mSyncLookback;
        switch (syncLookback) {
            case SyncWindow.SYNC_WINDOW_1_DAY:
                return Eas.FILTER_1_DAY;
            case SyncWindow.SYNC_WINDOW_3_DAYS:
                return Eas.FILTER_3_DAYS;
            case SyncWindow.SYNC_WINDOW_1_WEEK:
                return Eas.FILTER_1_WEEK;
            case SyncWindow.SYNC_WINDOW_2_WEEKS:
                return Eas.FILTER_2_WEEKS;
            case SyncWindow.SYNC_WINDOW_1_MONTH:
                return Eas.FILTER_1_MONTH;
            case SyncWindow.SYNC_WINDOW_ALL:
                return Eas.FILTER_ALL;
            default:
                // Auto window is deprecated and will also use the default.
                return Eas.FILTER_1_WEEK;
        }
    }
}
