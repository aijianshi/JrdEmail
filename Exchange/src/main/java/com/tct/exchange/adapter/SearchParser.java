/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date       |      author      |         Key        |       comment      */
/* --------------|----------------  |--------------------|------------------- */
/* 01/12/2014    | Zhenhua.Fan      |      PR-854923     |Exchange can't get  */
/*               |                  |                    |right search result */
/* ----------    |------------------|--------------------|-----------------   */
/******************************************************************************/

package com.tct.exchange.adapter;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.utility.TextUtilities;
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.Eas;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Parse the result of a Search command
 */
public class SearchParser extends Parser {
    private static final String LOG_TAG = Logging.LOG_TAG;
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final Mailbox mMailbox;
    private final Account mAccount;
    private final String mQuery;
    private int mTotalResults;

    public SearchParser(final Context context, final ContentResolver resolver,
        final InputStream in, final Mailbox mailbox, final Account account,
        String query)
            throws IOException {
        super(in);
        mContext = context;
        mContentResolver = resolver;
        mMailbox = mailbox;
        mAccount = account;
        mQuery = query;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    @Override
    public boolean parse() throws IOException {
        boolean res = false;
        if (nextTag(START_DOCUMENT) != Tags.SEARCH_SEARCH) {
            throw new IOException();
        }
        while (nextTag(START_DOCUMENT) != END_DOCUMENT) {
            if (tag == Tags.SEARCH_STATUS) {
                String status = getValue();
                if (Eas.USER_LOG) {
                    LogUtils.d(Logging.LOG_TAG, "Search status: " + status);
                }
            } else if (tag == Tags.SEARCH_RESPONSE) {
                parseResponse();
            } else {
                skipTag();
            }
        }
        return res;
    }

    private boolean parseResponse() throws IOException {
        boolean res = false;
      //[BUGFIX]-Mod-BEGIN by TSNJ Zhenhua.Fan,23/12/2014 and BUGFIX-883410
        try{
        while (nextTag(Tags.SEARCH_RESPONSE) != END) {
            if (tag == Tags.SEARCH_STORE) {
                parseStore();
            } else {
                skipTag();
            }
        }
        }catch(Exception e){
            android.util.Log.d("Exchange","parseResponse have encounter exception");
            e.printStackTrace();
        }
      //[BUGFIX]-Mod-END by TSNJ Zhenhua.Fan,23/12/2014
        return res;
    }

    private boolean parseStore() throws IOException {
        EmailSyncParser parser = new EmailSyncParser(this, mContext, mContentResolver,
                mMailbox, mAccount);
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        boolean res = false;
      //[BUGFIX]-Mod-BEGIN by TSNJ Zhenhua.Fan,23/12/2014 and BUGFIX-883410
        try{
        while (nextTag(Tags.SEARCH_STORE) != END) {
            if (tag == Tags.SEARCH_STATUS) {
                getValue();
            } else if (tag == Tags.SEARCH_TOTAL) {
                mTotalResults = getValueInt();
                android.util.Log.e("Exchange","totalcount:"+mTotalResults);
            } else if (tag == Tags.SEARCH_RESULT) {
                parseResult(parser, ops);
            } else {
                skipTag();
            }
        }
        }catch(Exception e){}
      //[BUGFIX]-Mod-END by TSNJ Zhenhua.Fan,23/12/2014
        try {
            // FLAG: In EmailSyncParser.commit(), we have complicated logic to constrain the size
            // of the batch, and fall back to one op at a time if that fails. We don't have any
            // such logic here, but we probably should.
            mContentResolver.applyBatch(EmailContent.AUTHORITY, ops);
            LogUtils.d(Logging.LOG_TAG, "Saved %s search results", ops.size());
        } catch (RemoteException e) {
            LogUtils.d(Logging.LOG_TAG, "RemoteException while saving search results.");
        } catch (OperationApplicationException e) {
        }

        return res;
    }

    private boolean parseResult(EmailSyncParser parser,
            ArrayList<ContentProviderOperation> ops) throws IOException {
        // Get an email sync parser for our incoming message data
        boolean res = false;
        Message msg = new Message();
        while (nextTag(Tags.SEARCH_RESULT) != END) {
            if (tag == Tags.SYNC_CLASS) {
                getValue();
            } else if (tag == Tags.SYNC_COLLECTION_ID) {
                getValue();
            } else if (tag == Tags.SEARCH_LONG_ID) {
                msg.mProtocolSearchInfo = getValue();
            } else if (tag == Tags.SEARCH_PROPERTIES) {
                msg.mAccountKey = mAccount.mId;
                msg.mMailboxKey = mMailbox.mId;
                msg.mFlagLoaded = Message.FLAG_LOADED_COMPLETE;
                parser.pushTag(tag);
                parser.addData(msg, tag);
                if (msg.mHtml != null) {
                    msg.mHtml = TextUtilities.highlightTermsInHtml(msg.mHtml, mQuery);
                }
                msg.addSaveOps(ops);
              //[BUGFIX]-Mod-BEGIN by TSNJ Zhenhua.Fan,23/12/2014 and BUGFIX-883410
            } else if (tag == Tags.SEARCH_RESULT) {
                parseResult(parser, ops);
            } else if (tag == Tags.SEARCH_TOTAL) {
                mTotalResults = getValueInt();
                android.util.Log.e("Exchange","totalcount1:"+mTotalResults);
            } //[BUGFIX]-Mod-END by TSNJ Zhenhua.Fan,23/12/2014
            else {
                skipTag();
            }
        }
        return res;
    }
}
