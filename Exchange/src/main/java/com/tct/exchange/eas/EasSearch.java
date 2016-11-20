/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date       |      author      |         Key        |       comment      */
/* --------------|----------------  |--------------------|------------------- */
/* 01/12/2014    | Zhenhua.Fan      |      PR-854923     |Exchange can't get  */
/*               |                  |                    |right search result */
/* ----------    |------------------|--------------------|-----------------   */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-50003 2014/11/04   zhaotianyong    Modify the package conflict
 *BUGFIX-933858  2015/03/03   zheng.zou     [Email]Can not stop search email when no network
 *BUGFIX-944708  2015/03/18   zheng.zou      [Email]Can not stop search email when no network
 ============================================================================ 
 */
package com.tct.exchange.eas;

import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.SearchParams;
//TS: MOD by zhaotianyong for CONFLICT_50003 START
//import com.tct.mail.provider.UIProvider;
import com.tct.mail.utils.UIProvider;
//TS: MOD by zhaotianyong for CONFLICT_50003 END
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.CommandStatusException;
import com.tct.exchange.Eas;
import com.tct.exchange.EasResponse;
import com.tct.exchange.adapter.SearchParser;
import com.tct.exchange.adapter.Serializer;
import com.tct.exchange.adapter.Tags;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EasSearch extends EasOperation {

    public final static int RESULT_NO_MESSAGES = 0;
    public final static int RESULT_OK = 1;
    public final static int RESULT_EMPTY_RESPONSE = 2;

    // The shortest search query we'll accept
    // TODO Check with UX whether this is correct
    private static final int MIN_QUERY_LENGTH = 3;
    // The largest number of results we'll ask for per server request
    private static final int MAX_SEARCH_RESULTS = 100;

    final SearchParams mSearchParams;
    final long mDestMailboxId;
    int mTotalResults;

    public EasSearch(final Context context, final long accountId, final SearchParams searchParams,
        final long destMailboxId) {
        super(context, accountId);
        mSearchParams = searchParams;
        mDestMailboxId = destMailboxId;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    @Override
    protected String getCommand() {
        return "Search";
    }

    @Override
    protected HttpEntity getRequestEntity() throws IOException {
        // Sanity check for arguments
        final int offset = mSearchParams.mOffset;
        final int limit = mSearchParams.mLimit;
        final String filter = mSearchParams.mFilter;
        if (limit < 0 || limit > MAX_SEARCH_RESULTS || offset < 0) {
            return null;
        }
        // TODO Should this be checked in UI?  Are there guidelines for minimums?
        if (filter == null || filter.length() < MIN_QUERY_LENGTH) {
            LogUtils.w(LOG_TAG, "filter too short");
            return null;
        }

        int res = 0;
        final Mailbox searchMailbox = Mailbox.restoreMailboxWithId(mContext, mDestMailboxId);
        // Sanity check; account might have been deleted?
        if (searchMailbox == null) {
            LogUtils.i(LOG_TAG, "search mailbox ceased to exist");
            return null;
        }
        final ContentValues statusValues = new ContentValues(2);
        try {
            // Set the status of this mailbox to indicate query
            //[BUGFIX]-Del-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
            statusValues.put(Mailbox.UI_SYNC_STATUS, UIProvider.SyncStatus.LIVE_QUERY);
            //statusValues.put(Mailbox.UI_SYNC_STATUS, UIProvider.UIPROVIDER_SYNCSTATUS_LIVE_QUERY);
            //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385
            searchMailbox.update(mContext, statusValues);

            final Serializer s = new Serializer();
            s.start(Tags.SEARCH_SEARCH).start(Tags.SEARCH_STORE);
            s.data(Tags.SEARCH_NAME, "Mailbox");
            s.start(Tags.SEARCH_QUERY).start(Tags.SEARCH_AND);
            s.data(Tags.SYNC_CLASS, "Email");

            // If this isn't an inbox search, then include the collection id
            final Mailbox inbox =
                    Mailbox.restoreMailboxOfType(mContext, mAccount.mId, Mailbox.TYPE_INBOX);
            if (inbox == null) {
                LogUtils.i(LOG_TAG, "Inbox ceased to exist");
                return null;
            }
            if (mSearchParams.mMailboxId != inbox.mId) {
                s.data(Tags.SYNC_COLLECTION_ID, inbox.mServerId);
            }
            s.data(Tags.SEARCH_FREE_TEXT, filter);

            // Add the date window if appropriate
            if (mSearchParams.mStartDate != null) {
                s.start(Tags.SEARCH_GREATER_THAN);
                s.tag(Tags.EMAIL_DATE_RECEIVED);
                s.data(Tags.SEARCH_VALUE, Eas.DATE_FORMAT.format(mSearchParams.mStartDate));
                s.end(); // SEARCH_GREATER_THAN
            }
            if (mSearchParams.mEndDate != null) {
                s.start(Tags.SEARCH_LESS_THAN);
                s.tag(Tags.EMAIL_DATE_RECEIVED);
                s.data(Tags.SEARCH_VALUE, Eas.DATE_FORMAT.format(mSearchParams.mEndDate));
                s.end(); // SEARCH_LESS_THAN
            }
            s.end().end(); // SEARCH_AND, SEARCH_QUERY
            s.start(Tags.SEARCH_OPTIONS);
            if (offset == 0) {
                s.tag(Tags.SEARCH_REBUILD_RESULTS);
            }
            if (mSearchParams.mIncludeChildren) {
                s.tag(Tags.SEARCH_DEEP_TRAVERSAL);
            }
            // Range is sent in the form first-last (e.g. 0-9)
            s.data(Tags.SEARCH_RANGE, offset + "-" + (offset + limit - 1));
            s.start(Tags.BASE_BODY_PREFERENCE);
            s.data(Tags.BASE_TYPE, Eas.BODY_PREFERENCE_HTML);
            s.data(Tags.BASE_TRUNCATION_SIZE, "20000");
            s.end();                    // BASE_BODY_PREFERENCE
            s.end().end().end().done(); // SEARCH_OPTIONS, SEARCH_STORE, SEARCH_SEARCH
            return makeEntity(s);
        } catch (IOException e) {
            LogUtils.d(LOG_TAG, e, "Search exception");
            //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 DEL_S
            //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
//            statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
//            statusValues.put(Mailbox.UI_SYNC_STATUS, UIProvider.SyncStatus.NO_SYNC);
//            searchMailbox.update(mContext, statusValues);
            //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
            //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 DEL_E
        } finally {
            // TODO: Handle error states
            // Set the status of this mailbox to indicate query over
            //[BUGFIX]-Del-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
//            statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
//            statusValues.put(Mailbox.UI_SYNC_STATUS, UIProvider.SyncStatus.NO_SYNC);
//            searchMailbox.update(mContext, statusValues);
            //[BUGFIX]-Del-END by TSNJ Zhenhua.Fan
        }
        LogUtils.i(LOG_TAG, "end returning null");
        return null;
    }

    @Override
    protected int handleResponse(final EasResponse response)
        throws IOException, CommandStatusException {
        if (response.isEmpty()) {
            return RESULT_EMPTY_RESPONSE;
        }
        final InputStream is = response.getInputStream();
        //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
        final ContentValues statusValues = new ContentValues(3);
        final Mailbox searchMailbox = Mailbox.restoreMailboxWithId(mContext, mDestMailboxId);
        //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385
        try {
            //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
            //final Mailbox searchMailbox = Mailbox.restoreMailboxWithId(mContext, mDestMailboxId);
            //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385
            final SearchParser sp = new SearchParser(mContext, mContext.getContentResolver(),
                    is, searchMailbox, mAccount, mSearchParams.mFilter);
            sp.parse();
            mTotalResults = sp.getTotalResults();
        } finally {
            is.close();
            //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 DEL_S
            //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
//            statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
//            statusValues.put(Mailbox.UI_SYNC_STATUS, UIProvider.SyncStatus.NO_SYNC);
//            statusValues.put(Mailbox.TOTAL_COUNT, mTotalResults);
//            searchMailbox.update(mContext, statusValues);
            //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
            //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 DEL_E
        }
        return RESULT_OK;
    }

    //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 ADD_S
    @Override
    public int performOperation() {
        //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 Mod_S
        int result = -1;
        try {
            result = super.performOperation();
            return result;
        }finally {
            final Mailbox searchMailbox = Mailbox.restoreMailboxWithId(mContext, mDestMailboxId);
            if (searchMailbox != null) {
                final ContentValues statusValues = new ContentValues(3);
                statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
                statusValues.put(Mailbox.UI_SYNC_STATUS, UIProvider.SyncStatus.NO_SYNC);
                statusValues.put(Mailbox.UI_LAST_SYNC_RESULT,  EasOperation.translateSyncResultToUiResult(result));
                statusValues.put(Mailbox.TOTAL_COUNT, mTotalResults);
                searchMailbox.update(mContext, statusValues);

            }
        }
        //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 Mod_E
    }
    //TS: zheng.zou 2015-03-3 EMAIL BUGFIX_933858 ADD_E
}
