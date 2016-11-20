/******************************************************************************/
/*                                                               Date:04/2015 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2015 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  caixia.chen                                                     */
/*  Email  :                                                                  */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :                                                                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/22/2015|     caixia.chen      |      PR 976586       |[Email]The downl- */
/*           |                      |                      |oad options do n- */
/*           |                      |                      |ot work when log  */
/*           |                      |                      |in exchange mail  */
/*           |                      |                      |account           */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-1020050  2015/06/11   jian.xu         [Android5.0][Exchange] Time and Location are lost after selecting Download remaining when reading a meeting
 *BUGFIX-1029375  2015/7/23   Gantao          [Android 5.0][Email]Some mails with exchange account can't display completely after taping download remaining
 *FEATURE-ID      2015/08/12   Gantao         FEATURE--Always show pictures
 ===========================================================================
 */

package com.tct.exchange.eas;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.internet.MimeMessage;
import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.mail.Part;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.provider.ProviderUnavailableException;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.provider.EmailContent.BodyColumns;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.utility.ConversionUtilities;
import com.tct.emailcommon.utility.StringCompressor;
import com.tct.emailcommon.utility.TextUtilities;
import com.tct.exchange.CommandStatusException;
import com.tct.exchange.Eas;
import com.tct.exchange.EasAuthenticationException;
import com.tct.exchange.EasResponse;
import com.tct.exchange.adapter.Parser;
import com.tct.exchange.adapter.Serializer;
import com.tct.exchange.adapter.Tags;
import com.tct.mail.utils.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//TS: jian.xu 2015-06-11 EXCHANGE BUGFIX-1020050 ADD_S
import com.tct.exchange.utility.CalendarUtilities;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.SpannedString;

import com.tct.emailcommon.mail.MeetingInfo;
import com.tct.emailcommon.mail.PackedString;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.Utility;
import java.text.ParseException;
//TS: jian.xu 2015-06-11 EXCHANGE BUGFIX-1020050 ADD_E

public class EasLoadMore extends EasOperation {
    private static final String CMD = "ItemOperations";
    private static final String TAG = "Email_ccx";

    private static final int RESULT_SUCCESS = 0;

    private Message mMessage;

    public EasLoadMore(Context context, long accountId, Message msg) {
        super(context, accountId);
        mMessage = msg;
    }

    @Override
    protected String getCommand() {
        if (mMessage == null) {
            LogUtils.wtf(TAG, "Error, mMessage is null");
            return null;
        }
        return CMD;
    }

    /**
     * The FetchMessageRequest is basically our wrapper for the Fetch service call Request: <?xml
     * version="1.0" encoding="utf-8"?> <ItemOperations> <Fetch> <Store>Mailbox</Store>
     * <airsync:CollectionId>collectionId</airsync:CollectionId>
     * <airsync:ServerId>serverId</airsync:ServerId> <Options> <airsyncbase:BodyPreference>
     * <airsyncbase:Type>1</airsyncbase:Type>
     * <airsyncbase:TruncationSize>size</airsyncbase:TruncationSize>
     * <airsyncbase:AllOrNone>0</airsyncbase:AllOrNone> </airsyncbase:BodyPreference> </Options>
     * </Fetch> </ItemOperations>
     */
    @Override
    protected HttpEntity getRequestEntity() throws IOException, MessageInvalidException {
        if (mMessage == null) {
            LogUtils.wtf(TAG, "Error, mMessage is null");
            return null;
        }

        final ContentResolver cr = mContext.getContentResolver();

        String serverId = "";
        long mailbox = -1;
        Uri qreryUri = ContentUris.withAppendedId(Message.CONTENT_URI, mMessage.mId);
        String[] projection = new String[] {
                SyncColumns.SERVER_ID, MessageColumns.MAILBOX_KEY
        };
        Cursor c = cr.query(qreryUri, projection, null, null, null);
        if (c == null) {
            throw new ProviderUnavailableException();
        } else {
            if (c.moveToFirst()) {
                serverId = c.getString(0);
                mailbox = c.getLong(1);
            }
            c.close();
            c = null;
        }
        if (TextUtils.isEmpty(serverId) || mailbox < 0)
            return null;
        Mailbox box = Mailbox.restoreMailboxWithId(mContext, mailbox);

        Serializer s = new Serializer();

        s.start(Tags.ITEMS_ITEMS).start(Tags.ITEMS_FETCH);
        s.data(Tags.ITEMS_STORE, "Mailbox");
        s.data(Tags.SYNC_COLLECTION_ID, box.mServerId);
        s.data(Tags.SYNC_SERVER_ID, mMessage.mServerId);
        s.start(Tags.ITEMS_OPTIONS);
        if (getProtocolVersion() >= Eas.SUPPORTED_PROTOCOL_EX2007_DOUBLE) {
            s.start(Tags.BASE_BODY_PREFERENCE);
            s.data(Tags.BASE_TYPE, Eas.BODY_PREFERENCE_HTML);
            s.end();
        } else {
            s.data(Tags.SYNC_MIME_SUPPORT, Eas.MIME_BODY_PREFERENCE_MIME);
            s.data(Tags.BASE_TYPE, Eas.EAS2_5_TRUNCATION_SIZE);
            // s.end();
        }
        s.end().end().end().done();

        return new ByteArrayEntity(s.toByteArray());
    }

    @Override
    protected int handleResponse(EasResponse response) throws IOException, CommandStatusException {
        int status = response.getStatus();
        if (status == HttpStatus.SC_OK) {
            if (!response.isEmpty()) {
                InputStream is = response.getInputStream();
                LoadMoreParser parser = new LoadMoreParser(is, mMessage);
                parser.parse();
                if (parser.getStatusCode() == LoadMoreParser.STATUS_CODE_SUCCESS) {
                    parser.commit(mContext);
                }
            } else {
                return RESULT_NETWORK_PROBLEM;
            }
        } else {
            LogUtils.e(TAG, "Fetch entire mail(messageId:" + mMessage.mId
                    + ") response error: ", status);
            if (response.isAuthError()) {
                throw new EasAuthenticationException();
            } else {
                throw new IOException();
            }
        }

        return RESULT_SUCCESS;
    }

    private class LoadMoreParser extends Parser {
        /**
         * Response: <?xml version="1.0" encoding="utf-8"?> <ItemOperations> <Status>1</Status>
         * <Response> <Fetch> <Status>1</Status>
         * <airsync:CollectionId>collectionId</airsync:CollectionId>
         * <airsync:ServerId>serverId</airsync:ServerId> <airsync:Class>Email</airsync:Class>
         * <Properties> ... </Properties> </Fetch> </Response> </ItemOperations>
         */

        private int mStatusCode = 0;
        private String mBodyType;

        public static final int STATUS_CODE_SUCCESS = 1;

        public LoadMoreParser(InputStream in, Message msg)
                throws IOException {
            super(in);
        }

        public int getStatusCode() {
            return mStatusCode;
        }

        // commit the body data to database.
        public void commit(Context context) {
            LogUtils.i(TAG, "Fetched message body successfully for " + mMessage.mId);
            // update the body data
            String newContent = updateHTMLContentForInlineAtts(context,
                    mMessage.mHtml, mMessage.mId);
            if (newContent != null) {
                mMessage.mHtml = newContent;
            }
            String snippet;
            boolean containsAttachment = false;
            Attachment[] atts = Attachment.restoreAttachmentsWithMessageId(context, mMessage.mId);
          //TS: Gantao 2015-09-28 EMAIL FEATURE_526529 MOD_S
            for(Attachment attachment: atts) {
              //We do not think the inline images is an attachment from now.
                if(attachment.mIsInline != Utility.INLINE_ATTACHMENT) {
                    containsAttachment = true;
                    break;
                }
            }
          //TS: Gantao 2015-09-28 EMAIL FEATURE_526529 MOD_E
            ContentValues cv = new ContentValues();
            cv.put(BodyColumns.MESSAGE_KEY, mMessage.mId);

            //TS: jian.xu 2015-06-11 EXCHANGE BUGFIX-1020050 ADD_S
            if ((mMessage.mFlags & EmailContent.Message.FLAG_INCOMING_MEETING_MASK) != 0) {
                String text = TextUtilities.makeSnippetFromHtmlText(
                        mMessage.mText != null ? mMessage.mText : mMessage.mHtml);
                if (TextUtils.isEmpty(text)) {
                    // Create text for this invitation
                    String meetingInfo = mMessage.mMeetingInfo;
                    if (!TextUtils.isEmpty(meetingInfo)) {
                        PackedString ps = new PackedString(meetingInfo);
                        ContentValues values = new ContentValues();
                        putFromMeeting(ps, MeetingInfo.MEETING_LOCATION, values,
                                CalendarContract.Events.EVENT_LOCATION);
                        String dtstart = ps.get(MeetingInfo.MEETING_DTSTART);
                        if (!TextUtils.isEmpty(dtstart)) {
                            try {
                                final long startTime =
                                    Utility.parseEmailDateTimeToMillis(dtstart);
                                values.put(CalendarContract.Events.DTSTART, startTime);
                            } catch (ParseException e) {
                                LogUtils.w(TAG, "Parse error for MEETING_DTSTART tag.", e);
                            }
                        }
                        putFromMeeting(ps, MeetingInfo.MEETING_ALL_DAY, values,
                                CalendarContract.Events.ALL_DAY);
                        mMessage.mText = CalendarUtilities.buildMessageTextFromEntityValues(
                                mContext, values, null);
                        mMessage.mHtml = Html.toHtml(new SpannedString(mMessage.mText));
                    }
                }
            }
          //TS: jian.xu 2015-06-11 EXCHANGE BUGFIX-1020050 ADD_E

            // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 MOD_S
            // compress the body if need
            boolean isNeedCompress = false;
            int length = 0;
            if (mBodyType.equals(Eas.BODY_PREFERENCE_HTML)) {
                if (mMessage.mHtml != null){
                    length = mMessage.mHtml.length();
                }
                if (length > MimeUtility.NEED_COMPRESS_BODY_SIZE) {
                    isNeedCompress = true;
                    cv.put(BodyColumns.HTML_CONTENT, StringCompressor.compressToBytes(mMessage.mHtml));
                    LogUtils.i(TAG, "easLoadmore fetch body html is large,we compress it");
                } else {
                    cv.put(BodyColumns.HTML_CONTENT, mMessage.mHtml);
                }
                snippet = TextUtilities.makeSnippetFromHtmlText(mMessage.mHtml);
            } else {
                if (mMessage.mText != null){
                    length = mMessage.mText.length();
                }
                if (length > MimeUtility.NEED_COMPRESS_BODY_SIZE) {
                    isNeedCompress = true;
                    LogUtils.i(TAG, "easLoadmore fetch body text is large,we compress it");
                    cv.put(BodyColumns.HTML_CONTENT, StringCompressor.compressToBytes(mMessage.mText));
                } else {
                    cv.put(BodyColumns.TEXT_CONTENT, mMessage.mText);
                }
                snippet = TextUtilities.makeSnippetFromPlainText(mMessage.mText);
            }
            ContentResolver contentResolver = mContext.getContentResolver();
            int res = contentResolver.update(isNeedCompress? Body.CONTENT_LARGE_URI :Body.CONTENT_URI,
                    cv, BodyColumns.MESSAGE_KEY + "=" + mMessage.mId, null);
            // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 MOD_E
            LogUtils.i(TAG, "update the body content, success number : " + res);
            // update the loaded flag to database.
            cv.clear();
            //To tell the DB that the message have attachment,user can check it in MessageFooterView.
            if (containsAttachment) {
                cv.put(MessageColumns.FLAG_ATTACHMENT, 1);
            }
            cv.put(MessageColumns.SNIPPET, snippet);
            cv.put(MessageColumns.FLAG_LOADED, Message.FLAG_LOADED_COMPLETE);
            cv.put(MessageColumns.FLAGS, mMessage.mFlags);
            Uri uri = ContentUris.withAppendedId(Message.CONTENT_URI, mMessage.mId);
            res = contentResolver.update(uri, cv, null, null);
            LogUtils.i(TAG, "update the message content, success number : " + res);
            // NOTE: Why trigger here ? For attachmentService want use the message flag,so we must
            // wait the Message updated completely and then do the attachment download.
            if (atts.length > 0) {
                loadAttachment(context, mMessage.mId, atts, cv);
            }
        }

        public void parseBody() throws IOException {
            mBodyType = Eas.BODY_PREFERENCE_TEXT;
            String body = "";
            while (nextTag(Tags.BASE_BODY) != END) {
                switch (tag) {
                    case Tags.BASE_TYPE:
                        mBodyType = getValue();
                        break;
                    case Tags.BASE_DATA:
                        body = getValue();
                        break;
                    default:
                        skipTag();
                }
            }
            // We always ask for TEXT or HTML; there's no third option
            if (mBodyType.equals(Eas.BODY_PREFERENCE_HTML)) {
                mMessage.mHtml = body;
            } else {
                mMessage.mText = body;
            }
        }

        public void parseMIMEBody(String mimeData) throws IOException {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(mimeData.getBytes());
                // The constructor parses the message
                MimeMessage mimeMessage = new MimeMessage(in);
                // Now process body parts & attachments
                ArrayList<Part> viewables = new ArrayList<Part>();
                // We'll ignore the attachments, as we'll get them directly from EAS
                ArrayList<Part> attachments = new ArrayList<Part>();
                MimeUtility.collectParts(mimeMessage, viewables, attachments);
                // parseBodyFields fills in the content fields of the Body
                ConversionUtilities.BodyFieldData data =
                        ConversionUtilities.parseBodyFields(viewables);
                // But we need them in the message itself for handling during commit()
                mMessage.setFlags(data.isQuotedReply, data.isQuotedForward);
                mMessage.mSnippet = data.snippet;
                mMessage.mHtml = data.htmlContent;
                mMessage.mText = data.textContent;
            } catch (MessagingException e) {
                // This would most likely indicate a broken stream
                throw new IOException(e);
            }
        }

        public void parseProperties() throws IOException {
            while (nextTag(Tags.ITEMS_PROPERTIES) != END) {
                switch (tag) {
                    case Tags.BASE_BODY:
                        parseBody();
                        break;
                    case Tags.EMAIL_MIME_DATA:
                        parseMIMEBody(getValue());
                        break;
                    case Tags.EMAIL_BODY:
                        String text = getValue();
                        mMessage.mText = text;
                        break;
                    default:
                        skipTag();
                }
            }
        }

      //TS: jian.xu 2015-06-11 EXCHANGE BUGFIX-1020050 ADD_S
        private void putFromMeeting(PackedString ps, String field, ContentValues values,
                String column) {
            String val = ps.get(field);
            if (!TextUtils.isEmpty(val)) {
                values.put(column, val);
            }
        }
      //TS: jian.xu 2015-06-11 EXCHANGE BUGFIX-1020050 ADD_E

        public void parseFetch() throws IOException {
            while (nextTag(Tags.ITEMS_FETCH) != END) {
                if (tag == Tags.ITEMS_PROPERTIES) {
                    parseProperties();
                } else {
                    skipTag();
                }
            }
        }

        public void parseResponse() throws IOException {
            while (nextTag(Tags.ITEMS_RESPONSE) != END) {
                if (tag == Tags.ITEMS_FETCH) {
                    parseFetch();
                } else {
                    skipTag();
                }
            }
        }

        @Override
        public boolean parse() throws IOException {
            boolean res = false;
            if (nextTag(START_DOCUMENT) != Tags.ITEMS_ITEMS) {
                throw new IOException();
            }
            while (nextTag(START_DOCUMENT) != END_DOCUMENT) {
                if (tag == Tags.ITEMS_STATUS) {
                    // save the status code.
                    mStatusCode = getValueInt();
                } else if (tag == Tags.ITEMS_RESPONSE) {
                    parseResponse();
                } else {
                    skipTag();
                }
            }
            return res;
        }
    }

    private String updateHTMLContentForInlineAtts(Context context,
            String htmlContent, long msgId) {
        if (TextUtils.isEmpty(htmlContent) || msgId < 1)
            return null;

        boolean update = false;
        Attachment[] attachments = Attachment.restoreAttachmentsWithMessageId(context, msgId);
        for (Attachment att : attachments) {
            if (TextUtils.isEmpty(att.mContentId))
                continue;

            // This attachment is viewable part, need update the body content.
            if (TextUtils.isEmpty(att.getContentUri())) {
                LogUtils.e(Logging.LOG_TAG, "Found one inline att, but contentUri is null.");
                continue;
            }

            // update the contents.
            String contentIdRe = "\\s+(?i)src=\"cid(?-i):\\Q" + att.mContentId + "\\E\"";
            String srcContentUri = " src=\"" + att.getContentUri() + "\"";
            htmlContent = htmlContent.replaceAll(contentIdRe, srcContentUri);
            update = true;
        }
        return update ? htmlContent : null;
    }

    /**
     * NOTE: After message downloaded,trigger the AttachmentService to do real download attachment.
     * EmailProider handle it by insert or update.So here we update the attachment.
     * 
     * @param context
     * @param msgId
     * @param cv
     */
    private void loadAttachment(Context context, long msgId, Attachment[] atts, ContentValues cv) {
        ContentResolver contentResolver = mContext.getContentResolver();
        cv.clear();
        // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID MOD_S
        Attachment att = atts[0];
        //Just trigger AttachmentService auto download attachment under WIFI,
        //not user request.
        cv.put(AttachmentColumns.FLAGS, att.mFlags &= ~Attachment.FLAG_DOWNLOAD_USER_REQUEST);
        cv.put(AttachmentColumns.MESSAGE_KEY, mMessage.mId);
        Uri uri = ContentUris.withAppendedId(Attachment.CONTENT_URI, att.mId);
        contentResolver.update(uri, cv, null, null);
     // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID MOD_E
    }
}
