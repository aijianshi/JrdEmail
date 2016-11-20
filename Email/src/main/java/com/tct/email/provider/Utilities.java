/*
 * Copyright (C) 2012 The Android Open Source Project
 * Copyright 2013 TCL Communication Technology Holdings Limited.
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
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/25/2014|     Chao Zhang       |      FR 631895 	   |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/**
 *===================================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== =======================================================================
 *BUGFIX-1001398 2015/05/21   zheng.zou       [Monitor][Email]The mail which have read display unread email after refresh list
 *BUGFIX-1009030 2015/06/04   Gantao          [Android5.0][Email]Attachment cannot fetch when download again.
 *BUGFIX-1013206 2015/06/05   Gantao          [Monitor][Email]Work abnormal when download some HTML email
 *BUGFIX-571177  2015/09/14   kaifeng.lu      [Android L][Email][Force close][Monitor]Email receive a new mail when tcl account syncing.
 *BUGFIX-1082125 2015/9/14    tao.gan         [HOMO][HOMO](SSV) The operator request the email exchange security type, SSL/TLS (Accept all certificates) as default
 *BUGFIX-1821184 2016/03/16   xiangnan.zhou   [Email]The unread point on Email icon still display after clear Email data in Settings
 ====================================================================================================================
 */

package com.tct.email.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.telephony.TelephonyManager;

import com.tct.email.LegacyConversions;
import com.tct.email.R;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.mail.Message;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.emailcommon.mail.Part;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.utility.ConversionUtilities;
import com.tct.emailcommon.utility.HtmlConverter;
import com.tct.emailcommon.utility.Utility;
import com.tct.fw.google.common.collect.ImmutableSet;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.utils.Utils;
import android.content.SharedPreferences;
//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
//[FEATURE]-Add-END by TSCD.chao zhang

public class Utilities {
    /**
     * Copy one downloaded message (which may have partially-loaded sections)
     * into a newly created EmailProvider Message, given the account and mailbox
     *
     * @param message the remote message we've just downloaded
     * @param account the account it will be stored into
     * @param folder the mailbox it will be stored into
     * @param loadStatus when complete, the message will be marked with this status (e.g.
     *        EmailContent.Message.LOADED)
     */

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    private static boolean isPop3Call=false;
    private static int mDownloadOptions = Utility.ENTIRE_MAIL;
    /**cause MimeUitility don't know which protocol call it,
     **so here for pop3,we should special handle it
     */
    public static void setPop3Call(boolean is) {
      isPop3Call =is;
    }

    public static void setDownloadOptions(int op) {
      mDownloadOptions = op;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang
    private static final int SEARCH_BODY_INSERT_LIMIT = 1024 * 1024 / 4;

  //TS: tao.gan 2015-9-14 EMAIL BUGFIX_1082128 ADD_S
    private static Set<String> SECURITY_TYPE_DEFAULT_TLS_CODE = ImmutableSet
            .of("70401", "70601", "708001", "71073", "71021", "71403", "71203");
  //TS: tao.gan 2015-9-14 EMAIL BUGFIX_1082128 ADD_E

    //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,04/30/2016, 1986683 ,
    //[Email]Unread messages, the message does not display the number
    private static final String SHARED_FILE_NAME = "unReadNumSavedFile";
    //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.com
    public static void copyOneMessageToProvider(Context context, Message message, Account account,
            Mailbox folder, int loadStatus) {
        EmailContent.Message localMessage = null;
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.CONTENT_PROJECTION,
                    EmailContent.MessageColumns.ACCOUNT_KEY + "=?" +
                            " AND " + MessageColumns.MAILBOX_KEY + "=?" +
                            " AND " + SyncColumns.SERVER_ID + "=?",
                            new String[] {
                            String.valueOf(account.mId),
                            String.valueOf(folder.mId),
                            String.valueOf(message.getUid())
                    },
                    null);
            if (c == null) {
                return;
            } else if (c.moveToNext()) {
                localMessage = EmailContent.getContent(context, c, EmailContent.Message.class);
            } else {
                localMessage = new EmailContent.Message();
            }
            localMessage.mMailboxKey = folder.mId;
            localMessage.mAccountKey = account.mId;
            //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
            mDownloadOptions=account.getDownloadOptions();
            //[FEATURE]-Add-END by TSCD.chao zhang
            // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD_S
            String protocol = account.getProtocol(context);
            copyOneMessageToProvider(context, message, localMessage, loadStatus, protocol);
            // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD_E
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD
    /**
     * Copy one downloaded message (which may have partially-loaded sections)
     * into an already-created EmailProvider Message
     *
     * @param message the remote message we've just downloaded
     * @param localMessage the EmailProvider Message, already created
     * @param loadStatus when complete, the message will be marked with this status (e.g.
     *        EmailContent.Message.LOADED)
     * @param context the context to be used for EmailProvider
     * @param protocol add the protocol to judge if it is pop3 when save attachment
     */
    public static void copyOneMessageToProvider(Context context, Message message,
            EmailContent.Message localMessage, int loadStatus, String protocol) {
        try {
            EmailContent.Body body = null;
            if (localMessage.mId != EmailContent.Message.NO_MESSAGE) {
                body = EmailContent.Body.restoreBodyWithMessageId(context, localMessage.mId);
            }
            if (body == null) {
                body = new EmailContent.Body();
            }
            // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_S
            boolean updateFlagLoadedAnyway = false;
            boolean flagLoadedUpdated = false;
            // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_E
            try {
                //TS: zheng.zou 2015-05-21 EMAIL BUGFIX_1001398 MOD_S
                boolean flagRead = localMessage.mFlagRead;
                // Copy the fields that are available into the message object
                LegacyConversions.updateMessageFields(localMessage, message,
                        localMessage.mAccountKey, localMessage.mMailboxKey);

                //restore the mFlagRead to local flag
                //for pop3, only the local flag is important, remote flag is always false
                //for imap, the local flag and remote flag is the same
                if (localMessage.isSaved()) {
                    localMessage.mFlagRead = flagRead;
                }
                //TS: zheng.zou 2015-05-21 EMAIL BUGFIX_1001398 MOD_E

                // Now process body parts & attachments
                ArrayList<Part> viewables = new ArrayList<Part>();
                ArrayList<Part> attachments = new ArrayList<Part>();
                //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                boolean msgPartDownloadFlag=false;

                if (isPop3Call && mDownloadOptions == Utility.HEAD_ONLY) {
                    MimeUtility.setMsgPartDownloadFlag(true);
                    msgPartDownloadFlag = true;
                    isPop3Call = false;
                    mDownloadOptions= Utility.ENTIRE_MAIL;
                }
                //[FEATURE]-Add-END by TSCD.chao zhang
                MimeUtility.collectParts(message, viewables, attachments);

                final ConversionUtilities.BodyFieldData data =
                        ConversionUtilities.parseBodyFields(viewables);

                // set body and local message values
                localMessage.setFlags(data.isQuotedReply, data.isQuotedForward);
                localMessage.mSnippet = data.snippet;
                body.mTextContent = data.textContent;
                body.mHtmlContent = data.htmlContent;
                /// TCT: create text body for Local Search. @{
                if (TextUtils.isEmpty(data.textContent)
                        && !TextUtils.isEmpty(data.htmlContent)) {
                    //TS: kaifeng.lu 2015-09-14 EMAIL BUGFIX_571177 MOD_S
                    try {
                        if (data.htmlContent.length() > SEARCH_BODY_INSERT_LIMIT) {
                            body.mTextContent = HtmlConverter
                                    .htmlToText(data.htmlContent.substring(0, SEARCH_BODY_INSERT_LIMIT));
                        } else {
                            body.mTextContent = HtmlConverter
                                    .htmlToText(data.htmlContent);
                        }
                    } catch (Exception e) {
                        LogUtils.w(Logging.LOG_TAG, e, "create text body for Local Search");
                    }
                    //TS: kaifeng.lu 2015-09-14 EMAIL BUGFIX_571177 MOD_E
                }
                /// @}

                // Commit the message & body to the local store immediately
                saveOrUpdate(localMessage, context);
                body.mMessageKey = localMessage.mId;
                saveOrUpdate(body, context);

                // process (and save) attachments
                if (loadStatus != EmailContent.Message.FLAG_LOADED_PARTIAL
                        && loadStatus != EmailContent.Message.FLAG_LOADED_UNKNOWN) {
                    // TODO(pwestbro): What should happen with unknown status?
                 // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_S
                    // Note:If we come to here,it says we can update flag to db even some exception
                    // occured(it may happened all times even we try to tap "download remain" many times),
                    // and for attachments,we can download again to make it normal if it's possible.
                    // Is't ok?
                    updateFlagLoadedAnyway = true;
                 // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_E
                 // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD_S
                    LegacyConversions.updateAttachments(context, localMessage, attachments, protocol);
                    LegacyConversions.updateInlineAttachments(context, localMessage, viewables, attachments.size(),protocol);
                    // TS: Gantao 2015-06-04 EMAIL BUGFIX_1009030 MOD_S
                } else {
                    EmailContent.Attachment att = new EmailContent.Attachment();
                    // Since we haven't actually loaded the attachment, we're just putting
                    // a dummy placeholder here. When the user taps on it, we'll load the attachment
                    // for real.
                    // TODO: This is not a great way to model this. What we're saying is, we don't
                    // have the complete message, without paying any attention to what we do have.
                    // Did the main body exceed the maximum initial size? If so, we really might
                    // not have any attachments at all, and we just need a button somewhere that
                    // says "load the rest of the message".
                    // Or, what if we were able to load some, but not all of the attachments?
                    // Then we should ideally not be dropping the data we have on the floor.
                    // Also, what behavior we have here really should be based on what protocol
                    // we're dealing with. If it's POP, then we don't actually know how many
                    // attachments we have until we've loaded the complete message.
                    // If it's IMAP, we should know that, and we should load all attachment
                    // metadata we can get, regardless of whether or not we have the complete
                    // message body.
                    att.mFileName = "";
                    att.mSize = message.getSize();
                    att.mMimeType = "text/plain";
                    att.mMessageKey = localMessage.mId;
                    att.mAccountKey = localMessage.mAccountKey;
                    att.mFlags = Attachment.FLAG_DUMMY_ATTACHMENT;
                    att.save(context);
                    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                    //here to avaid only the headonly message but with attachment icon show.
                    if (!msgPartDownloadFlag) {
                       localMessage.mFlagAttachment = true;
                    }
                   //[FEATURE]-Add-END by TSCD.chao zhang
                }

                // One last update of message with two updated flags
                localMessage.mFlagLoaded = loadStatus;

                ContentValues cv = new ContentValues();
                cv.put(EmailContent.MessageColumns.FLAG_ATTACHMENT, localMessage.mFlagAttachment);
                cv.put(EmailContent.MessageColumns.FLAG_LOADED, localMessage.mFlagLoaded);
                Uri uri = ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI,
                        localMessage.mId);
                context.getContentResolver().update(uri, cv, null, null);
                // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_S
                flagLoadedUpdated = true;
                // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_E

            } catch (MessagingException me) {
                LogUtils.e(Logging.LOG_TAG, "Error while copying downloaded message." + me);
            }
            // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_S
            // update flagLoaded anyway if needed.
            finally {
                if (updateFlagLoadedAnyway && !flagLoadedUpdated) {
                 // One last update of message with two updated flags
                    localMessage.mFlagLoaded = loadStatus;

                    ContentValues cv = new ContentValues();
                    cv.put(EmailContent.MessageColumns.FLAG_LOADED, localMessage.mFlagLoaded);
                    Uri uri = ContentUris.withAppendedId(EmailContent.Message.CONTENT_URI,
                            localMessage.mId);
                    context.getContentResolver().update(uri, cv, null, null);
                }
            }
            // TS: Gantao 2015-06-05 EMAIL BUGFIX_1013206 ADD_E

        } catch (RuntimeException rte) {
            LogUtils.e(Logging.LOG_TAG, "Error while storing downloaded message." + rte.toString());
        } catch (IOException ioe) {
            LogUtils.e(Logging.LOG_TAG, "Error while storing attachment." + ioe.toString());
        }
    }

    public static void saveOrUpdate(EmailContent content, Context context) {
        if (content.isSaved()) {
            content.update(context, content.toContentValues());
        } else {
            content.save(context);
            //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
            if (content instanceof EmailContent.Message) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                ((EmailContent.Message) content).addInnerRecipinetsOps(context, ops);
                ((EmailContent.Message) content).applyBatchOperations(context, ops);
            }
            //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
        }
    }

    /**
     * Converts a string representing a file mode, such as "rw", into a bitmask suitable for use
     * with {@link android.os.ParcelFileDescriptor#open}.
     * <p>
     * @param mode The string representation of the file mode.
     * @return A bitmask representing the given file mode.
     * @throws IllegalArgumentException if the given string does not match a known file mode.
     */
    @TargetApi(19)
    public static int parseMode(String mode) {
        if (Utils.isRunningKitkatOrLater()) {
            return ParcelFileDescriptor.parseMode(mode);
        }
        final int modeBits;
        if ("r".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_ONLY;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else if ("wa".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_APPEND;
        } else if ("rw".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE;
        } else if ("rwt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else {
            throw new IllegalArgumentException("Bad mode '" + mode + "'");
        }
        return modeBits;
    }

 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_S
    public static boolean ssvEnabled() {
        return SystemProperties.getBoolean("ro.ssv.enabled", false);
    }
 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_E

  //TS: tao.gan 2015-9-14 EMAIL BUGFIX_1082128 ADD_S
    public static boolean isNeedChange(String mccmnc) {
        return SECURITY_TYPE_DEFAULT_TLS_CODE.contains(mccmnc);
    }
  //TS: tao.gan 2015-9-14 EMAIL BUGFIX_1082128 ADD_E

    //TS: tao.gan 2015-12-04 EMAIL BUGFIX_1031705 ADD_S
    public static String getSingnatureForAccount(Context context) {
        String accountSign = null;
        boolean ssvEnabled = SystemProperties.getBoolean("ro.ssv.enabled", false);
        String operator = SystemProperties.get("ro.ssv.operator.choose", "");
        //[BUGFIX]-Mod-BEGIN by SCDTABLET.weiwei.huang,05/17/2016,2156891,
        //[Email]Email will FC when configure Email via OMA CP
        String mccmnc = SystemProperties.get("persist.sys.lang.mccmnc", "");
        //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,03/02/2016,1711311,
        // [SSV][Claro_Chile]Email need customized.
//        TelephonyManager telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        String imsi = telManager.getSubscriberId();
        //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.com
        if (ssvEnabled && operator.equals("TEF")/* && null != imsi*/){
            try {
                String mcc = mccmnc.substring(0, 3);
                //[BUGFIX]-Mod-END by SCDTABLET.weiwei.huang
                if (!TextUtils.isEmpty(mcc)) {
                    int mccCode = Integer.parseInt(mcc);
                    String productName = PLFUtils.getString(context, "def_email_accountSignature_productName");
                    switch (mccCode) {
                        case 722:
                        case 732:
                        case 712:
                        case 370:
                        case 704:
                        case 708:
                        case 710:
                        case 744:
                        case 716:
                        case 706:
                        case 748:
                        case 330:
                        case 730:
                        case 740:
                        case 714: //Claro
                            accountSign = context.getResources().getString(R.string.signature_for_claro_sim_card, productName);
                            break;
                        case 334: //Telcel
                            accountSign = context.getResources().getString(R.string.signature_for_telcel_sim_card, productName);
                            break;
                        default:
                            accountSign = context.getResources().getString(R.string.mail_account_signature_prefix, productName);
                            break;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            String productName = PLFUtils.getString(context, "def_email_accountSignature_productName");
            accountSign = context.getResources().getString(R.string.mail_account_signature_prefix, productName);
        }
        return accountSign;
    }
    //TS: tao.gan 2015-12-04 EMAIL BUGFIX_1031705 ADD_E

    //TS: xiangnan.zhou 2016-03-16 EMAIL BUGFIX-1821184 ADD_S
    public static void sendUnreadCountToLauncher(Context context) {
        int unReadCount = 0;
        //TS: junwei-xu 2015-3-23 EMAIL BUGFIX_930464 ADD_S
        Cursor cursor = context.getContentResolver().
                query(Account.CONTENT_URI,new String[] {EmailContent.AccountColumns._ID}, null, null, null);
        try {
            if (cursor != null && cursor.getCount() != 0) {
                //TS: junwei-xu 2015-08-19 EMAIL BUGFIX_1064838 MOD_S
                //Note:get all trash box id, remove these mail counts when statistics of the total unread numbers
                StringBuffer accountIds = new StringBuffer("(");
                StringBuffer trashBoxIds = new StringBuffer("(");
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long accountId =  cursor.getLong(0);
                    cursor.moveToNext();
                    boolean isAfterLast = cursor.isAfterLast();
                    long trashBoxId = Mailbox.findMailboxOfType(context, accountId, Mailbox.TYPE_TRASH);
                    accountIds.append(accountId);
                    trashBoxIds.append(trashBoxId);
                    if (!isAfterLast) {
                        accountIds.append(",");
                        trashBoxIds.append(",");
                    }
                }
                accountIds.append(")");
                trashBoxIds.append(")");

                //Note: unread count must except unread mails which in Trash box
                unReadCount = EmailContent.count(context, EmailContent.Message.CONTENT_URI, MessageColumns.FLAG_READ +
                        "=0 AND accountKey in " + accountIds.toString() + " AND mailboxKey NOT IN " + trashBoxIds.toString(), null);
                //TS: junwei-xu 2015-08-19 EMAIL BUGFIX_1064838 MOD_E
                //TS: junwei-xu 2015-3-23 EMAIL BUGFIX_930464 ADD_E
            }
            ComponentName component = new ComponentName("com.tct.email", "com.tct.email.activity.Welcome");
            Intent intent = new Intent("com.intent.unread");
            intent.putExtra("componentName", component);
            intent.putExtra("unreadNum", unReadCount);
            context.sendBroadcast(intent);

            //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen@tcl.com,04/26/2016, 1986683 ,
            //[Email]Unread messages, the message does not display the number
            SharedPreferences share = context.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_WORLD_READABLE);
            share.edit().putInt("com_tct_email_unread", unReadCount).commit();
            //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen@tcl.com

            //TS: zheng.zou 2015-9-25 EMAIL BUGFIX_667469 DEL_S
            //note: no need for this project, will cause security exception in multi-user change. remove it.
            //TS: tianjing.su 2015-8-10 EMAIL BUGFIX_1064857 ADD_S
            //After send broadcast,we also update the System properties.
//                android.provider.Settings.System.putInt(getContext().getContentResolver(), "com_tct_email_unread", unReadCount);
//                LogUtils.i(TAG, "send the unread Broadcast and store the value", unReadCount);
            //TS: tianjing.su 2015-8-10 EMAIL BUGFIX_1064857 ADD_E
            //TS: zheng.zou 2015-9-25 EMAIL BUGFIX_667469 DEL_E
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //TS: xiangnan.zhou 2016-03-16 EMAIL BUGFIX-1821184 ADD_E
}
