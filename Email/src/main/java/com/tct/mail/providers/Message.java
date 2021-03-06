/**
 * Copyright (c) 2012, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/******************************************************************************/
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/17/2013|     Chao Zhang       |      FR 631895 	   |[HOMO][HOMO][Ora- */
/*           |                      |porting from FR514398 |nge][Homologatio- */
/*           |                      |                      |n] Exchange Acti- */
/*           |                      |                      |ve Sync Priority  */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag                 Date      Author         Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-852100  2014/12/10   wenggangjin     [Android5.0][Email]Can't display pictures in Email body
 *BUGFIX-898509  2014/01/16   zhaotianyong    [Email]Extra an attachfile in .eml attachfile.
 *BUGFIX-897915  2014/02/02   peng-zhang      [Android5.0][Email][Monitor][ANR] Sometimes Email occurs ANR after open a mail.
 *BUGFIX-931168  2014/02/13   peng-zhang      [REG][Email]Can not open some text link and Email detail screen overlap.
 *BUGFIX-936928  2015/03/06   zheng.zou       [Email]Number and E-mail address can not be displayed as a link
 *BUGFIX-955940  2015/03/23   zhonghua.tuo    [SMC][email] com.tct.email happen anr due to Input dispatching timed out
 *BUGFIX-965608  2015/06/19   junwei-xu       [REG][Android5.0][Email]Priority doesn't saved after edit one draft
 *BUGFIX-957636  2015/07/03   Gantao          [Android5.0][Email]Can't show pictures in eml file
 *BUGFIX-845079  2015/11/03   jian.xu         [Android L][Email]Email will ANR or exit automatically when checking the email with US-ASCII charset.
 ===========================================================================
 */
package com.tct.mail.providers;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;

import com.tct.emailcommon.internet.MimeHeader;
import com.tct.emailcommon.internet.MimeMessage;
import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.mail.Address;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.mail.Part;
import com.tct.emailcommon.utility.ConversionUtilities;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
//import com.google.common.base.Objects;
//import com.google.common.collect.Lists;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.base.Objects;
import com.tct.fw.google.common.collect.Lists;
import com.tct.mail.providers.UIProvider.MessageColumns;
import com.tct.mail.ui.HtmlMessage;
import com.tct.mail.utils.HtmlSanitizer;
import com.tct.mail.utils.Utils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


public class Message implements Parcelable, HtmlMessage {
    /**
     * Regex pattern used to look for any inline images in message bodies, including Gmail-hosted
     * relative-URL images, Gmail emoticons, and any external inline images (although we usually
     * count on the server to detect external images).
     */
    private static Pattern INLINE_IMAGE_PATTERN = Pattern.compile("<img\\s+[^>]*src=",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    // regex that matches content id surrounded by "<>" optionally.
    private static final Pattern REMOVE_OPTIONAL_BRACKETS = Pattern.compile("^<?([^>]+)>?$");

    /**
     * @see BaseColumns#_ID
     */
    public long id;
    /**
     * @see UIProvider.MessageColumns#SERVER_ID
     */
    public String serverId;
    /**
     * @see UIProvider.MessageColumns#URI
     */
    public Uri uri;
    /**
     * @see UIProvider.MessageColumns#CONVERSATION_ID
     */
    public Uri conversationUri;
    /**
     * @see UIProvider.MessageColumns#SUBJECT
     */
    public String subject;
    /**
     * @see UIProvider.MessageColumns#SNIPPET
     */
    public String snippet;
    /**
     * @see UIProvider.MessageColumns#FROM
     */
    private String mFrom;
    /**
     * @see UIProvider.MessageColumns#TO
     */
    private String mTo;
    /**
     * @see UIProvider.MessageColumns#CC
     */
    private String mCc;
    /**
     * @see UIProvider.MessageColumns#BCC
     */
    private String mBcc;
    /**
     * @see UIProvider.MessageColumns#REPLY_TO
     */
    private String mReplyTo;
    /**
     * @see UIProvider.MessageColumns#DATE_RECEIVED_MS
     */
    public long dateReceivedMs;
    /**
     * @see UIProvider.MessageColumns#BODY_HTML
     */
    public String bodyHtml;
    /**
     * @see UIProvider.MessageColumns#BODY_TEXT
     */
    public String bodyText;
    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
    /**
     * @see UIProvider.MessageColumns#BODY_HTML_LINKIFY
     */
    public String bodyHtmlLinkify;
    /**
     * @see UIProvider.MessageColumns#BODY_TEXT_LINKIFY
     */
    public String bodyTextLinkify;
    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
    /**
     * @see UIProvider.MessageColumns#EMBEDS_EXTERNAL_RESOURCES
     */
    public boolean embedsExternalResources;
    /**
     * @see UIProvider.MessageColumns#REF_MESSAGE_ID
     */
    public Uri refMessageUri;
    /**
     * @see UIProvider.MessageColumns#DRAFT_TYPE
     */
    public int draftType;
    /**
     * @see UIProvider.MessageColumns#APPEND_REF_MESSAGE_CONTENT
     */
    public boolean appendRefMessageContent;
    /**
     * @see UIProvider.MessageColumns#HAS_ATTACHMENTS
     */
    public boolean hasAttachments;
    /**
     * @see UIProvider.MessageColumns#ATTACHMENT_LIST_URI
     */
    public Uri attachmentListUri;
    /**
     * @see UIProvider.MessageColumns#ATTACHMENT_BY_CID_URI
     */
    public Uri attachmentByCidUri;
    /**
     * @see UIProvider.MessageColumns#MESSAGE_FLAGS
     */
    public long messageFlags;
    /**
     * @see UIProvider.MessageColumns#ALWAYS_SHOW_IMAGES
     */
    public boolean alwaysShowImages;
    /**
     * @see UIProvider.MessageColumns#READ
     */
    public boolean read;
    /**
     * @see UIProvider.MessageColumns#SEEN
     */
    public boolean seen;
    /**
     * @see UIProvider.MessageColumns#STARRED
     */
    public boolean starred;
    /**
     * @see UIProvider.MessageColumns#QUOTE_START_POS
     */
    public int quotedTextOffset;
    /**
     * @see UIProvider.MessageColumns#ATTACHMENTS
     *<p>
     * N.B. this value is NOT immutable and may change during conversation view render.
     */
    public String attachmentsJson;
    /**
     * @see UIProvider.MessageColumns#MESSAGE_ACCOUNT_URI
     */
    public Uri accountUri;
    /**
     * @see UIProvider.MessageColumns#EVENT_INTENT_URI
     */
    public Uri eventIntentUri;
    /**
     * @see UIProvider.MessageColumns#SPAM_WARNING_STRING
     */
    public String spamWarningString;
    /**
     * @see UIProvider.MessageColumns#SPAM_WARNING_LEVEL
     */
    public int spamWarningLevel;
    /**
     * @see UIProvider.MessageColumns#SPAM_WARNING_LINK_TYPE
     */
    public int spamLinkType;
    /**
     * @see UIProvider.MessageColumns#VIA_DOMAIN
     */
    public String viaDomain;
    /**
     * @see UIProvider.MessageColumns#SENDING_STATE
     */
    public int sendingState;

    /**
     * @see UIProvider.MessageColumns#CLIPPED
     */
    public boolean clipped;
    /**
     * @see UIProvider.MessageColumns#PERMALINK
     */
    public String permalink;

    // TS: Gantao 2015-12-01 EMAIL BUGFIX_1117109 ADD_S
    public int msgFlagLoaded;
    // TS: Gantao 2015-12-01 EMAIL BUGFIX_1117109 ADD_E

    private transient String[] mFromAddresses = null;
    private transient String[] mToAddresses = null;
    private transient String[] mCcAddresses = null;
    private transient String[] mBccAddresses = null;
    private transient String[] mReplyToAddresses = null;

    private transient List<Attachment> mAttachments = null;
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    public int mPriority=1;
    public static final int FLAG_PRIORITY_HIGH = 0;
    public static final int FLAG_PRIORITY_NORMAL = 1;
    public static final int FLAG_PRIORITY_LOW = 2;
    //[FEATURE]-Add-END by TSCD.chao zhang
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && o instanceof Message
                && Objects.equal(uri, ((Message) o).uri));
    }

    @Override
    public int hashCode() {
        return uri == null ? 0 : uri.hashCode();
    }

    /**
     * Helper equality function to check if the two Message objects are equal in terms of
     * the fields that the user can input from ComposeActivity. This is primarily used to
     * ensure draft preview/composition are synced.
     * @param o the Message being compared to
     * @return True if they are equal in fields, false otherwise
     */
    public boolean isEqual(Message o) {
        return TextUtils.equals(this.getFrom(), o.getFrom()) &&
                this.sendingState == o.sendingState &&
                TextUtils.equals(this.getTo(), o.getTo()) &&
                TextUtils.equals(this.getCc(), o.getCc()) &&
                TextUtils.equals(this.getBcc(), o.getBcc()) &&
                TextUtils.equals(this.subject, o.subject) &&
                TextUtils.equals(this.bodyHtml, o.bodyHtml) &&
                TextUtils.equals(this.bodyText, o.bodyText) &&
                // TS: junwei-xu 2015-06-19 EMAIL BUGFIX_965608 ADD_S
                (this.mPriority == o.mPriority) &&
                // TS: junwei-xu 2015-06-19 EMAIL BUGFIX_965608 ADD_E
                this.msgFlagLoaded == o.msgFlagLoaded &&
                Objects.equal(this.attachmentListUri, o.attachmentListUri) &&
                Objects.equal(getAttachments(), o.getAttachments());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(serverId);
        dest.writeParcelable(uri, 0);
        dest.writeParcelable(conversationUri, 0);
        dest.writeString(subject);
        dest.writeString(snippet);
        dest.writeString(mFrom);
        dest.writeString(mTo);
        dest.writeString(mCc);
        dest.writeString(mBcc);
        dest.writeString(mReplyTo);
        dest.writeLong(dateReceivedMs);
        dest.writeString(bodyHtml);
        dest.writeString(bodyText);
        dest.writeInt(embedsExternalResources ? 1 : 0);
        dest.writeParcelable(refMessageUri, 0);
        dest.writeInt(draftType);
        dest.writeInt(appendRefMessageContent ? 1 : 0);
        dest.writeInt(hasAttachments ? 1 : 0);
        dest.writeParcelable(attachmentListUri, 0);
        dest.writeLong(messageFlags);
        dest.writeInt(alwaysShowImages ? 1 : 0);
        dest.writeInt(quotedTextOffset);
        dest.writeString(attachmentsJson);
        dest.writeParcelable(accountUri, 0);
        dest.writeParcelable(eventIntentUri, 0);
        dest.writeString(spamWarningString);
        dest.writeInt(spamWarningLevel);
        dest.writeInt(spamLinkType);
        dest.writeString(viaDomain);
        dest.writeInt(sendingState);
        dest.writeInt(clipped ? 1 : 0);
        dest.writeString(permalink);
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        dest.writeInt(mPriority);
        //[FEATURE]-Add-END by TSCD.chao zhang
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
        dest.writeString(bodyHtmlLinkify);
        dest.writeString(bodyTextLinkify);
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E
        dest.writeInt(msgFlagLoaded);
    }

    private Message(Parcel in) {
        id = in.readLong();
        serverId = in.readString();
        uri = in.readParcelable(null);
        conversationUri = in.readParcelable(null);
        subject = in.readString();
        snippet = in.readString();
        mFrom = in.readString();
        mTo = in.readString();
        mCc = in.readString();
        mBcc = in.readString();
        mReplyTo = in.readString();
        dateReceivedMs = in.readLong();
        bodyHtml = in.readString();
        bodyText = in.readString();
        embedsExternalResources = in.readInt() != 0;
        refMessageUri = in.readParcelable(null);
        draftType = in.readInt();
        appendRefMessageContent = in.readInt() != 0;
        hasAttachments = in.readInt() != 0;
        attachmentListUri = in.readParcelable(null);
        messageFlags = in.readLong();
        alwaysShowImages = in.readInt() != 0;
        quotedTextOffset = in.readInt();
        attachmentsJson = in.readString();
        accountUri = in.readParcelable(null);
        eventIntentUri = in.readParcelable(null);
        spamWarningString = in.readString();
        spamWarningLevel = in.readInt();
        spamLinkType = in.readInt();
        viaDomain = in.readString();
        sendingState = in.readInt();
        clipped = in.readInt() != 0;
        permalink = in.readString();
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        mPriority = in.readInt();
        //[FEATURE]-Add-END by TSCD.chao zhang
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
        bodyHtmlLinkify = in.readString();
        bodyTextLinkify = in.readString();
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E
        msgFlagLoaded = in.readInt();
    }

    public Message() {
       //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
       mPriority = FLAG_PRIORITY_NORMAL;
       //[FEATURE]-Add-END by TSCD.chao zhang
    }

    @Override
    public String toString() {
        return "[message id=" + id + "]";
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

    public Message(Cursor cursor) {
        if (cursor != null) {
            id = cursor.getLong(UIProvider.MESSAGE_ID_COLUMN);
            serverId = cursor.getString(UIProvider.MESSAGE_SERVER_ID_COLUMN);
            final String messageUriStr = cursor.getString(UIProvider.MESSAGE_URI_COLUMN);
            uri = !TextUtils.isEmpty(messageUriStr) ? Uri.parse(messageUriStr) : null;
            final String convUriStr = cursor.getString(UIProvider.MESSAGE_CONVERSATION_URI_COLUMN);
            conversationUri = !TextUtils.isEmpty(convUriStr) ? Uri.parse(convUriStr) : null;
            subject = cursor.getString(UIProvider.MESSAGE_SUBJECT_COLUMN);
            snippet = cursor.getString(UIProvider.MESSAGE_SNIPPET_COLUMN);
            mFrom = cursor.getString(UIProvider.MESSAGE_FROM_COLUMN);
            mTo = cursor.getString(UIProvider.MESSAGE_TO_COLUMN);
            mCc = cursor.getString(UIProvider.MESSAGE_CC_COLUMN);
            mBcc = cursor.getString(UIProvider.MESSAGE_BCC_COLUMN);
            mReplyTo = cursor.getString(UIProvider.MESSAGE_REPLY_TO_COLUMN);
            dateReceivedMs = cursor.getLong(UIProvider.MESSAGE_DATE_RECEIVED_MS_COLUMN);
            bodyHtml = cursor.getString(UIProvider.MESSAGE_BODY_HTML_COLUMN);
            bodyText = cursor.getString(UIProvider.MESSAGE_BODY_TEXT_COLUMN);
            embedsExternalResources = cursor
                    .getInt(UIProvider.MESSAGE_EMBEDS_EXTERNAL_RESOURCES_COLUMN) != 0;
            final String refMessageUriStr =
                    cursor.getString(UIProvider.MESSAGE_REF_MESSAGE_URI_COLUMN);
            refMessageUri = !TextUtils.isEmpty(refMessageUriStr) ?
                    Uri.parse(refMessageUriStr) : null;
            draftType = cursor.getInt(UIProvider.MESSAGE_DRAFT_TYPE_COLUMN);
            appendRefMessageContent = cursor
                    .getInt(UIProvider.MESSAGE_APPEND_REF_MESSAGE_CONTENT_COLUMN) != 0;
            hasAttachments = cursor.getInt(UIProvider.MESSAGE_HAS_ATTACHMENTS_COLUMN) != 0;
            final String attachmentsUri = cursor
                    .getString(UIProvider.MESSAGE_ATTACHMENT_LIST_URI_COLUMN);
            //[Defect]-Del-BEGIN by SCDTABLET.qiao-yang@tcl.com,05/12/2016,2004459,
            attachmentListUri = /*hasAttachments &&*/ !TextUtils.isEmpty(attachmentsUri) ? Uri
                    .parse(attachmentsUri) : null;
            //[Defect]-Del-END by SCDTABLET.qiao-yang@tcl.com
            final String attachmentsByCidUri = cursor
                    .getString(UIProvider.MESSAGE_ATTACHMENT_BY_CID_URI_COLUMN);
            attachmentByCidUri = hasAttachments && !TextUtils.isEmpty(attachmentsByCidUri) ?
                    Uri.parse(attachmentsByCidUri) : null;
            messageFlags = cursor.getLong(UIProvider.MESSAGE_FLAGS_COLUMN);
            alwaysShowImages = cursor.getInt(UIProvider.MESSAGE_ALWAYS_SHOW_IMAGES_COLUMN) != 0;
            read = cursor.getInt(UIProvider.MESSAGE_READ_COLUMN) != 0;
            seen = cursor.getInt(UIProvider.MESSAGE_SEEN_COLUMN) != 0;
            starred = cursor.getInt(UIProvider.MESSAGE_STARRED_COLUMN) != 0;
            quotedTextOffset = cursor.getInt(UIProvider.QUOTED_TEXT_OFFSET_COLUMN);
            attachmentsJson = cursor.getString(UIProvider.MESSAGE_ATTACHMENTS_COLUMN);
            String accountUriString = cursor.getString(UIProvider.MESSAGE_ACCOUNT_URI_COLUMN);
            accountUri = !TextUtils.isEmpty(accountUriString) ? Uri.parse(accountUriString) : null;
            eventIntentUri =
                    Utils.getValidUri(cursor.getString(UIProvider.MESSAGE_EVENT_INTENT_COLUMN));
            spamWarningString =
                    cursor.getString(UIProvider.MESSAGE_SPAM_WARNING_STRING_ID_COLUMN);
            spamWarningLevel = cursor.getInt(UIProvider.MESSAGE_SPAM_WARNING_LEVEL_COLUMN);
            spamLinkType = cursor.getInt(UIProvider.MESSAGE_SPAM_WARNING_LINK_TYPE_COLUMN);
            viaDomain = cursor.getString(UIProvider.MESSAGE_VIA_DOMAIN_COLUMN);
            sendingState = cursor.getInt(UIProvider.MESSAGE_SENDING_STATE_COLUMN);
            clipped = cursor.getInt(UIProvider.MESSAGE_CLIPPED_COLUMN) != 0;
            permalink = cursor.getString(UIProvider.MESSAGE_PERMALINK_COLUMN);
            //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
            mPriority = cursor.getInt(UIProvider.MESSAGE_FLAG_PRIORITY_COLUMN);
            //[FEATURE]-Add-END by TSCD.chao zhang
            //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
            bodyHtmlLinkify = cursor.getString(UIProvider.MESSAGE_BODY_HTML_LINKIFY_COLUMN);
            bodyTextLinkify = cursor.getString(UIProvider.MESSAGE_BODY_TEXT_LINKIFY_COLUMN);
            //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E
            msgFlagLoaded = cursor.getInt(UIProvider.MESSAGE_FLAG_LOADED_COLUMN);
        }
    }

    /**
     * This constructor exists solely to generate Message objects from .eml attachments.
     */
    public Message(Context context, MimeMessage mimeMessage, Uri emlFileUri)
            throws MessagingException {
        // Set message header values.
        setFrom(Address.toHeader(mimeMessage.getFrom()));
        setTo(Address.toHeader(mimeMessage.getRecipients(
                com.tct.emailcommon.mail.Message.RecipientType.TO)));
        setCc(Address.toHeader(mimeMessage.getRecipients(
                com.tct.emailcommon.mail.Message.RecipientType.CC)));
        setBcc(Address.toHeader(mimeMessage.getRecipients(
                com.tct.emailcommon.mail.Message.RecipientType.BCC)));
        setReplyTo(Address.toHeader(mimeMessage.getReplyTo()));
        subject = mimeMessage.getSubject();

        final Date sentDate = mimeMessage.getSentDate();
        final Date internalDate = mimeMessage.getInternalDate();
        if (sentDate != null) {
            dateReceivedMs = sentDate.getTime();
        } else if (internalDate != null) {
            dateReceivedMs = internalDate.getTime();
        } else {
            dateReceivedMs = System.currentTimeMillis();
        }

        // for now, always set defaults
        alwaysShowImages = false;
        viaDomain = null;
        draftType = UIProvider.DraftType.NOT_A_DRAFT;
        sendingState = UIProvider.ConversationSendingState.OTHER;
        starred = false;
        spamWarningString = null;
        messageFlags = 0;
        clipped = false;
        permalink = null;
        hasAttachments = false;
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        mPriority = FLAG_PRIORITY_NORMAL;
        //[FEATURE]-Add-END by TSCD.chao zhang
        // body values (snippet/bodyText/bodyHtml)
        // Now process body parts & attachments
        ArrayList<Part> viewables = new ArrayList<Part>();
        ArrayList<Part> attachments = new ArrayList<Part>();
        MimeUtility.collectParts(mimeMessage, viewables, attachments);

        ConversionUtilities.BodyFieldData data = ConversionUtilities.parseBodyFields(viewables);

        snippet = data.snippet;
        bodyText = data.textContent;

        // sanitize the HTML found within the .eml file before consuming it
        bodyHtml = HtmlSanitizer.sanitizeHtml(data.htmlContent);

        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
        if (!TextUtils.isEmpty(bodyHtml)) {
            bodyHtmlLinkify = com.tct.mail.utils.Linkify.addLinks(bodyHtml);
        }
        if (!TextUtils.isEmpty(bodyText)) {
            final SpannableString spannable = new SpannableString(bodyText);
            Linkify.addLinks(spannable, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS | Linkify.PHONE_NUMBERS);
            bodyTextLinkify = Html.toHtml(spannable);
        }
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E

        // populate mAttachments
        mAttachments = Lists.newArrayList();

        final String messageId = mimeMessage.getMessageId();

        int partId = 0;
        for (final Part attachmentPart : attachments) {
            mAttachments.add(new Attachment(context, attachmentPart,
                    emlFileUri, messageId, Integer.toString(partId++), false /* inline */,subject));
        }

        // instantiating an Attachment for each viewable will cause it to be registered within the
        // EmlAttachmentProvider for later access when displaying inline attachments

        //TS: Gantao 2015-07-03 EMAIL BUGFIX_957636 MOD
        //Now we can add the viewable attachment
        for (final Part viewablePart : viewables) {
            final String[] cids = viewablePart.getHeader(MimeHeader.HEADER_CONTENT_ID);
            if (cids != null && cids.length == 1) {
                final String cid = REMOVE_OPTIONAL_BRACKETS.matcher(cids[0]).replaceAll("$1");
                mAttachments.add(new Attachment(context, viewablePart, emlFileUri, messageId, cid,
                        true /* inline */,subject));
            }
        }

        hasAttachments = !mAttachments.isEmpty();

        attachmentListUri = hasAttachments ?
                EmlAttachmentProvider.getAttachmentsListUri(emlFileUri, messageId) : null;

        attachmentByCidUri = EmlAttachmentProvider.getAttachmentByCidUri(emlFileUri, messageId);
    }

    public boolean isFlaggedReplied() {
        return (messageFlags & UIProvider.MessageFlags.REPLIED) ==
                UIProvider.MessageFlags.REPLIED;
    }

    public boolean isFlaggedForwarded() {
        return (messageFlags & UIProvider.MessageFlags.FORWARDED) ==
                UIProvider.MessageFlags.FORWARDED;
    }

    public boolean isFlaggedCalendarInvite() {
        return (messageFlags & UIProvider.MessageFlags.CALENDAR_INVITE) ==
                UIProvider.MessageFlags.CALENDAR_INVITE;
    }

    public String getFrom() {
        return mFrom;
    }

    public synchronized void setFrom(final String from) {
        mFrom = from;
        mFromAddresses = null;
    }

    public String getTo() {
        return mTo;
    }

    public synchronized void setTo(final String to) {
        mTo = to;
        mToAddresses = null;
    }

    public String getCc() {
        return mCc;
    }

    public synchronized void setCc(final String cc) {
        mCc = cc;
        mCcAddresses = null;
    }

    public String getBcc() {
        return mBcc;
    }

    public synchronized void setBcc(final String bcc) {
        mBcc = bcc;
        mBccAddresses = null;
    }

    @VisibleForTesting
    public String getReplyTo() {
        return mReplyTo;
    }

    public synchronized void setReplyTo(final String replyTo) {
        mReplyTo = replyTo;
        mReplyToAddresses = null;
    }

    public synchronized String[] getFromAddresses() {
        if (mFromAddresses == null) {
            mFromAddresses = tokenizeAddresses(mFrom);
        }
        return mFromAddresses;
    }

    public String[] getFromAddressesUnescaped() {
        return unescapeAddresses(getFromAddresses());
    }

    public synchronized String[] getToAddresses() {
        if (mToAddresses == null) {
            mToAddresses = tokenizeAddresses(mTo);
        }
        return mToAddresses;
    }

    public String[] getToAddressesUnescaped() {
        return unescapeAddresses(getToAddresses());
    }

    public synchronized String[] getCcAddresses() {
        if (mCcAddresses == null) {
            mCcAddresses = tokenizeAddresses(mCc);
        }
        return mCcAddresses;
    }

    public String[] getCcAddressesUnescaped() {
        return unescapeAddresses(getCcAddresses());
    }

    public synchronized String[] getBccAddresses() {
        if (mBccAddresses == null) {
            mBccAddresses = tokenizeAddresses(mBcc);
        }
        return mBccAddresses;
    }

    public String[] getBccAddressesUnescaped() {
        return unescapeAddresses(getBccAddresses());
    }

    public synchronized String[] getReplyToAddresses() {
        if (mReplyToAddresses == null) {
            mReplyToAddresses = tokenizeAddresses(mReplyTo);
        }
        return mReplyToAddresses;
    }

    public String[] getReplyToAddressesUnescaped() {
        return unescapeAddresses(getReplyToAddresses());
    }

    private static String[] unescapeAddresses(String[] escaped) {
        final String[] unescaped = new String[escaped.length];
        for (int i = 0; i < escaped.length; i++) {
            final String escapeMore = escaped[i].replace("<", "&lt;").replace(">", "&gt;");
            unescaped[i] = Html.fromHtml(escapeMore).toString();
        }
        return unescaped;
    }

    public static String[] tokenizeAddresses(String addresses) {
        if (TextUtils.isEmpty(addresses)) {
            return new String[0];
        }

        Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(addresses);
        String[] strings = new String[tokens.length];
        for (int i = 0; i < tokens.length;i++) {
            strings[i] = tokens[i].toString();
        }
        return strings;
    }

    public List<Attachment> getAttachments() {
        if (mAttachments == null) {
            if (attachmentsJson != null) {
                mAttachments = Attachment.fromJSONArray(attachmentsJson);
            } else {
                mAttachments = Collections.emptyList();
            }
        }
        return mAttachments;
    }

    /**
     * Returns the number of attachments in the message.
     * @param includeInline If {@code true}, includes inline attachments in the count.
     *                      {@code false}, otherwise.
     * @return the number of attachments in the message.
     */
    public int getAttachmentCount(boolean includeInline) {
        // If include inline, just return the full list count.
        if (includeInline) {
            return mAttachments.size();
        }

        // Otherwise, iterate through the attachment list,
        // skipping inline attachments.
        int numAttachments = 0;
        final List<Attachment> attachments = getAttachments();
        for (int i = 0, size = attachments.size(); i < size; i++) {
            if (attachments.get(i).isInlineAttachment()) {
                continue;
            }
            numAttachments++;
        }

        return numAttachments;
    }

    /**
     * Returns whether a "Show Pictures" button should initially appear for this message. If the
     * button is shown, the message must also block all non-local images in the body. Inversely, if
     * the button is not shown, the message must show all images within (or else the user would be
     * stuck with no images and no way to reveal them).
     *
     * @return true if a "Show Pictures" button should appear.
     */
    public boolean shouldShowImagePrompt() {
        return !alwaysShowImages && (embedsExternalResources ||
                (!TextUtils.isEmpty(bodyHtml) && INLINE_IMAGE_PATTERN.matcher(bodyHtml).find()));
    }

    @Override
    public boolean embedsExternalResources() {
        return embedsExternalResources;
    }

    /**
     * Helper method to command a provider to mark all messages from this sender with the
     * {@link MessageColumns#ALWAYS_SHOW_IMAGES} flag set.
     *
     * @param handler a caller-provided handler to run the query on
     * @param token (optional) token to identify the command to the handler
     * @param cookie (optional) cookie to pass to the handler
     */
    public void markAlwaysShowImages(AsyncQueryHandler handler, int token, Object cookie) {
        alwaysShowImages = true;

        final ContentValues values = new ContentValues(1);
        values.put(UIProvider.MessageColumns.ALWAYS_SHOW_IMAGES, 1);

        handler.startUpdate(token, cookie, uri, values, null, null);
    }

    @Override
    public String getBodyAsHtml() {
        String body = "";
        if (!TextUtils.isEmpty(bodyHtml)) {
            //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 MOD_S
            body = com.tct.mail.utils.Linkify.addLinks(bodyHtml);
            //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_852100 MOD_S
//            body = bodyHtml;
            //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_852100 MOD_E
            //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 MOD_E
            //[FEATURE]-Mod by TSNJ,li.yu,19/11/2014,PR-845091
        } else if (!TextUtils.isEmpty(bodyText)) {
            final SpannableString spannable = new SpannableString(bodyText);
            //AM: peng-zhang 2015-02-02 EMAIL BUGFIX_897915 MOD_S
            //AM: peng-zhang 2015-02-13 EMAIL BUGFIX_931168 MOD_S
            //TS: zhonghua.tuo 2015-03-23 EMAIL BUGFIX_955940 MOD_S
            Linkify.addLinks(spannable, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS | Linkify.PHONE_NUMBERS);
            //TS: zhonghua.tuo 2015-03-23 EMAIL BUGFIX_955940 MOD_E
            //AM: peng-zhang 2015-02-02 EMAIL BUGFIX_897915 MOD_S
            //AM: peng-zhang 2015-02-13 EMAIL BUGFIX_931168 MOD_E
            body = Html.toHtml(spannable);
        }
        return body;
    }

    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
    @Override
    public String getBodyAsHtmlLinkify() {
        String body = "";
        if (!TextUtils.isEmpty(bodyHtmlLinkify)) {
            body = bodyHtmlLinkify;
        } else if (!TextUtils.isEmpty(bodyTextLinkify)) {
            body = bodyTextLinkify;
        }
        return body;
    }
    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E

    @Override
    public long getId() {
        return id;
    }

    public boolean isDraft() {
        return draftType != UIProvider.DraftType.NOT_A_DRAFT;
    }
}
