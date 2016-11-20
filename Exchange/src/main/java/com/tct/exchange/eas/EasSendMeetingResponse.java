/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-50003 2014/11/04   zhaotianyong    Modify the package conflict
 *BUGFIX-923513  2015-02-04   zhaotianyong    [HOMO][TMO EU 61564]It is not possible to put a comment in a response to a calendar invite.
 *BUGFIX-996128  2015-05-12   zhaotianyong    [Android5.0][Email]The content display twice which response from meeting.
 *BUGFIX-1054379  2015/7/29    Gantao          [TMO][38283][Email]Can not edit respond before sending when accept Meeting reques
 ============================================================================ 
 */
package com.tct.exchange.eas;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import com.tct.emailcommon.mail.Address;
import com.tct.emailcommon.mail.MeetingInfo;
import com.tct.emailcommon.mail.PackedString;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.service.EmailServiceConstants;
import com.tct.emailcommon.utility.Utility;
//TS: MOD by zhaotianyong for CONFLICT_50003 START
//import com.tct.mail.provider.UIProvider;
import com.tct.mail.utils.UIProvider;
//TS: MOD by zhaotianyong for CONFLICT_50003 END
import com.tct.mail.utils.LogUtils;
import com.tct.exchange.CommandStatusException;
import com.tct.exchange.EasResponse;
import com.tct.exchange.adapter.MeetingResponseParser;
import com.tct.exchange.adapter.Serializer;
import com.tct.exchange.adapter.Tags;
import com.tct.exchange.utility.CalendarUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;

public class EasSendMeetingResponse extends EasOperation {
    public final static int RESULT_OK = 1;

    private final static String TAG = LogUtils.TAG;

    /** Projection for getting the server id for a mailbox. */
    private static final String[] MAILBOX_SERVER_ID_PROJECTION = {
            EmailContent.MailboxColumns.SERVER_ID };
    private static final int MAILBOX_SERVER_ID_COLUMN = 0;

    /** EAS protocol values for UserResponse. */
    private static final int EAS_RESPOND_ACCEPT = 1;
    private static final int EAS_RESPOND_TENTATIVE = 2;
    private static final int EAS_RESPOND_DECLINE = 3;
    /** Value to use if we get a UI response value that we can't handle. */
    private static final int EAS_RESPOND_UNKNOWN = -1;

    private final EmailContent.Message mMessage;
    private final int mMeetingResponse;
    private int mEasResponse;
 // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S
    private String bodyContent = null;
 // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S

    public EasSendMeetingResponse(final Context context, final long accountId,
                                  final EmailContent.Message message, final int meetingResponse) {
        super(context, accountId);
        mMessage = message;
        mMeetingResponse = meetingResponse;
    }

    /**
     * Translate from {@link com.tct.mail.providers.UIProvider.MessageOperations} constants to
     * EAS values. They're currently identical but this is for future-proofing.
     * @param messageOperationResponse The response value that came from the UI.
     * @return The EAS protocol value to use.
     */
    private static int messageOperationResponseToUserResponse(final int messageOperationResponse) {
        switch (messageOperationResponse) {
            case UIProvider.UIPROVIDER_MESSAGEOPERATION_RESPOND_ACCEPT:
                return EAS_RESPOND_ACCEPT;
            case UIProvider.UIPROVIDER_MESSAGEOPERATION_RESPOND_TENTATIVE:
                return EAS_RESPOND_TENTATIVE;
            case UIProvider.UIPROVIDER_MESSAGEOPERATION_RESPOND_DECLINE:
                return EAS_RESPOND_DECLINE;
        }
        return EAS_RESPOND_UNKNOWN;
    }

    @Override
    protected String getCommand() {
        return "MeetingResponse";
    }

    @Override
    protected HttpEntity getRequestEntity() throws IOException {
        mEasResponse = messageOperationResponseToUserResponse(mMeetingResponse);
        if (mEasResponse == EAS_RESPOND_UNKNOWN) {
            LogUtils.e(TAG, "Bad response value: %d", mMeetingResponse);
            return null;
        }
        final Account account = Account.restoreAccountWithId(mContext, mMessage.mAccountKey);
        if (account == null) {
            LogUtils.e(TAG, "Could not load account %d for message %d", mMessage.mAccountKey,
                    mMessage.mId);
            return null;
        }
        final String mailboxServerId = Utility.getFirstRowString(mContext,
                ContentUris.withAppendedId(Mailbox.CONTENT_URI, mMessage.mMailboxKey),
                MAILBOX_SERVER_ID_PROJECTION, null, null, null, MAILBOX_SERVER_ID_COLUMN);
        if (mailboxServerId == null) {
            LogUtils.e(TAG, "Could not load mailbox %d for message %d", mMessage.mMailboxKey,
                    mMessage.mId);
            return null;
        }
     // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S
        Body body = Body.restoreBodyWithMessageId(mContext, mMessage.mId);
        if (body != null) {
            if (!TextUtils.isEmpty(body.mTextContent)) {
                mMessage.mText = body.mTextContent;
            }
        }
     // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_E
        final HttpEntity response;
        try {
             response = makeResponse(mMessage, mailboxServerId, mEasResponse);
        } catch (CertificateException e) {
            LogUtils.e(TAG, e, "CertficateException");
            return null;
        }
        return response;
    }

    private HttpEntity makeResponse(final EmailContent.Message msg, final String mailboxServerId,
                                    final int easResponse)
            throws IOException, CertificateException {
        final Serializer s = new Serializer();
        s.start(Tags.MREQ_MEETING_RESPONSE).start(Tags.MREQ_REQUEST);
        s.data(Tags.MREQ_USER_RESPONSE, Integer.toString(easResponse));
        s.data(Tags.MREQ_COLLECTION_ID, mailboxServerId);
        s.data(Tags.MREQ_REQ_ID, msg.mServerId);
        s.end().end().done();
        return makeEntity(s);
    }

    @Override
    protected int handleResponse(final EasResponse response)
            throws IOException, CommandStatusException {
        final int status = response.getStatus();
        if (status == HttpStatus.SC_OK) {
            if (!response.isEmpty()) {
                // TODO: Improve the parsing to actually handle error statuses.
                new MeetingResponseParser(response.getInputStream()).parse();

                if (mMessage.mMeetingInfo != null) {
                    final PackedString meetingInfo = new PackedString(mMessage.mMeetingInfo);
                    final String responseRequested =
                            meetingInfo.get(MeetingInfo.MEETING_RESPONSE_REQUESTED);
                    // If there's no tag, or a non-zero tag, we send the response mail
                    if (!"0".equals(responseRequested)) {
                     // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S
                      //NOTE:Before send response mail,we get the comment value.
                        bodyContent = mMessage.mText;
                     // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_E
                        sendMeetingResponseMail(meetingInfo, mEasResponse);
                    }
                }
            }
        } else if (response.isAuthError()) {
            // TODO: Handle this gracefully.
            //throw new EasAuthenticationException();
        } else {
            LogUtils.e(TAG, "Meeting response request failed, code: %d", status);
            throw new IOException();
        }
        return RESULT_OK;
    }

    // TS: zheng.zou 2015-12-22 EXCHANGE BUGFIX-1123842 ADD_S
    @Override
    public int performOperation() {
        //delete invite message first
        deleteInviteMessage();
        return super.performOperation();
    }

    private void deleteInviteMessage(){
        Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, mMessage.mMailboxKey);
        if (mailbox != null && mailbox.mType != Mailbox.TYPE_TRASH) {
            Uri uri = Uri.parse("content://" + EmailContent.AUTHORITY + "/uimessage/" + mMessage.mId);
            LogUtils.i(TAG, "delete invite msg uri =" + uri);
            mContext.getContentResolver().delete(uri, null, null);
        }
    }
    // TS: zheng.zou 2015-12-22 EXCHANGE BUGFIX-1123842 ADD_E

    private void sendMeetingResponseMail(final PackedString meetingInfo, final int response) {
        // This will come as "First Last" <box@server.blah>, so we use Address to
        // parse it into parts; we only need the email address part for the ics file
        final Address[] addrs = Address.parse(meetingInfo.get(MeetingInfo.MEETING_ORGANIZER_EMAIL));
        // It shouldn't be possible, but handle it anyway
        if (addrs.length != 1) return;
        final String organizerEmail = addrs[0].getAddress();

        final String dtStamp = meetingInfo.get(MeetingInfo.MEETING_DTSTAMP);
        final String dtStart = meetingInfo.get(MeetingInfo.MEETING_DTSTART);
        final String dtEnd = meetingInfo.get(MeetingInfo.MEETING_DTEND);
        if (TextUtils.isEmpty(dtStamp) || TextUtils.isEmpty(dtStart) || TextUtils.isEmpty(dtEnd)) {
            LogUtils.w(TAG, "blank dtStamp %s dtStart %s dtEnd %s", dtStamp, dtStart, dtEnd);
            return;
        }

        // What we're doing here is to create an Entity that looks like an Event as it would be
        // stored by CalendarProvider
        final ContentValues entityValues = new ContentValues(6);
        final Entity entity = new Entity(entityValues);

        // Fill in times, location, title, and organizer
        entityValues.put("DTSTAMP",
                CalendarUtilities.convertEmailDateTimeToCalendarDateTime(dtStamp));
        try {
            entityValues.put(CalendarContract.Events.DTSTART,
                    Utility.parseEmailDateTimeToMillis(dtStart));
            entityValues.put(CalendarContract.Events.DTEND,
                    Utility.parseEmailDateTimeToMillis(dtEnd));
        } catch (ParseException e) {
             LogUtils.w(TAG, "Parse error for DTSTART/DTEND tags.", e);
        }
        entityValues.put(CalendarContract.Events.EVENT_LOCATION,
                meetingInfo.get(MeetingInfo.MEETING_LOCATION));
        entityValues.put(CalendarContract.Events.TITLE, meetingInfo.get(MeetingInfo.MEETING_TITLE));
        entityValues.put(CalendarContract.Events.TITLE, meetingInfo.get(MeetingInfo.MEETING_TITLE));
        entityValues.put(CalendarContract.Events.ORGANIZER, organizerEmail);

        // Add ourselves as an attendee, using our account email address
        final ContentValues attendeeValues = new ContentValues(2);
        attendeeValues.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
                CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
        attendeeValues.put(CalendarContract.Attendees.ATTENDEE_EMAIL, mAccount.mEmailAddress);
        entity.addSubValue(CalendarContract.Attendees.CONTENT_URI, attendeeValues);

        // Add the organizer
        final ContentValues organizerValues = new ContentValues(2);
        organizerValues.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
                CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);
        organizerValues.put(CalendarContract.Attendees.ATTENDEE_EMAIL, organizerEmail);
        entity.addSubValue(CalendarContract.Attendees.CONTENT_URI, organizerValues);

        // Create a message from the Entity we've built.  The message will have fields like
        // to, subject, date, and text filled in.  There will also be an "inline" attachment
        // which is in iCalendar format
        final int flag;
        switch(response) {
            case EmailServiceConstants.MEETING_REQUEST_ACCEPTED:
                flag = EmailContent.Message.FLAG_OUTGOING_MEETING_ACCEPT;
                break;
            case EmailServiceConstants.MEETING_REQUEST_DECLINED:
                flag = EmailContent.Message.FLAG_OUTGOING_MEETING_DECLINE;
                break;
            case EmailServiceConstants.MEETING_REQUEST_TENTATIVE:
            default:
                flag = EmailContent.Message.FLAG_OUTGOING_MEETING_TENTATIVE;
                break;
        }
        final EmailContent.Message outgoingMsg =
                CalendarUtilities.createMessageForEntity(mContext, entity, flag,
                        meetingInfo.get(MeetingInfo.MEETING_UID), mAccount);
        // Assuming we got a message back (we might not if the event has been deleted), send it
        if (outgoingMsg != null) {
         // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S
          //NOTE:the response message is created,but without any comment,we should append the comment
            //to its message body.
            if (bodyContent != null) {
                String text = outgoingMsg.mText;
                StringBuilder builder = new StringBuilder();
                builder.append(bodyContent);
                builder.append("\n\n");
                builder.append(text);
                outgoingMsg.mText = builder.toString();
                bodyContent = null;
            }
         // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_E
            sendMessage(mAccount, outgoingMsg);
        }
    }
}
