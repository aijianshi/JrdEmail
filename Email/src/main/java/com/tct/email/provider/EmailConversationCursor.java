/*
 * Copyright (C) 2014 The Android Open Source Project
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
 *Tag		 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin Modify the package conflict
 *BUGFIX-844813  2014/11/27   wenggangjin [Android5.0][Email][UE]There are two loading icons on the screen
 *BUGFIX-958925  2014/03/26   junwei-xu       [Stability][Mail]Reason: Executing service com.tct.email/.service.AttachmentService
 *CR_585337      2015-09-16  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-718891  2015/11/23   zheng.zou       Email sync frequency for Exchange/imap/pop3 is not correctly
 ===========================================================================
 */
package com.tct.email.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.mail.Address;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.mail.utils.LogUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.Lists;
import com.tct.fw.google.common.collect.Lists;
import com.tct.mail.browse.ConversationCursorOperationListener;
import com.tct.mail.providers.ConversationInfo;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.FolderList;
import com.tct.mail.providers.ParticipantInfo;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.ConversationColumns;
//TS: MOD by wenggangjin for CONFLICT_20001 END
//[BugFix]-Add-Begin by TSNJ,Zhenhua.Fan,24/11/2014,Bug-847240
import com.tct.emailcommon.provider.Account;
//[BugFix]-Add-END by TSNJ,Zhenhua.Fan
/**
 * Wrapper that handles the visibility feature (i.e. the conversation list is visible, so
 * any pending notifications for the corresponding mailbox should be canceled). We also handle
 * getExtras() to provide a snapshot of the mailbox's status
 */
public class EmailConversationCursor extends CursorWrapper implements
        ConversationCursorOperationListener {
    private final long mMailboxId;
    private final int mMailboxTypeId;
    private final Context mContext;
    private final FolderList mFolderList;
    private final Bundle mExtras = new Bundle();

    /**
     * When showing a folder, if it's been at least this long since the last sync,
     * force a folder refresh.
     */
    private static final long AUTO_REFRESH_INTERVAL_MS = 5 * DateUtils.MINUTE_IN_MILLIS;

    public EmailConversationCursor(final Context context, final Cursor cursor,
            final Folder folder, final long mailboxId) {
        super(cursor);
        mMailboxId = mailboxId;
        mContext = context;
        mFolderList = FolderList.copyOf(Lists.newArrayList(folder));
        Mailbox mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);

        if (mailbox != null) {
            mMailboxTypeId = mailbox.mType;

            mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_ERROR,
                    mailbox.mUiLastSyncResult);
            mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_TOTAL_COUNT, mailbox.mTotalCount);
            if (mailbox.mUiSyncStatus == EmailContent.SYNC_STATUS_BACKGROUND
                    || mailbox.mUiSyncStatus == EmailContent.SYNC_STATUS_USER
                    || mailbox.mUiSyncStatus == EmailContent.SYNC_STATUS_LIVE
                    || mailbox.mUiSyncStatus == EmailContent.SYNC_STATUS_INITIAL_SYNC_NEEDED) {
                mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,
                        UIProvider.CursorStatus.LOADING);
            } else if (mailbox.mUiSyncStatus == EmailContent.SYNC_STATUS_NONE) {
                if (mailbox.mSyncInterval == 0
                        && (Mailbox.isSyncableType(mailbox.mType)
                        || mailbox.mType == Mailbox.TYPE_SEARCH)
                        && !TextUtils.isEmpty(mailbox.mServerId) &&
                        // TODO: There's potentially a race condition here.
                        // Consider merging this check with the auto-sync code in respond.
                        System.currentTimeMillis() - mailbox.mSyncTime
                                > AUTO_REFRESH_INTERVAL_MS) {
                    // This will be syncing momentarily
                    mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,
                            UIProvider.CursorStatus.LOADING);
                  //[BugFix]-Add-Begin by TSNJ,Zhenhua.Fan,24/11/2014,Bug-847240
                    if(mailbox.mType==Mailbox.TYPE_SENT&&"POP3".equalsIgnoreCase(Account.
                    getProtocol(context,mailbox.mAccountKey)))//for now pop sent folder is not syncable type folder
                    {
                        mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,UIProvider.CursorStatus.COMPLETE);
                    }
                   //[BugFix]-Add-END by TSNJ,Zhenhua.Fan
                   //TS: wenggangjin 2014-11-27 EMAIL BUGFIX-844813 ADD_S
                    if(mailbox.mType==Mailbox.TYPE_TRASH&&"POP3".equalsIgnoreCase(Account.
                    getProtocol(context,mailbox.mAccountKey)))//for now pop sent folder is not syncable type folder
                    {
                        mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,UIProvider.CursorStatus.COMPLETE);
                    }
                    //TS: wenggangjin 2014-11-27 EMAIL BUGFIX-844813 ADD_E
                } else {
                    mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,
                            UIProvider.CursorStatus.COMPLETE);
                }
            } else {
                LogUtils.d(Logging.LOG_TAG,
                        "Unknown mailbox sync status" + mailbox.mUiSyncStatus);
                mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,
                        UIProvider.CursorStatus.COMPLETE);
            }
        } else {
            mMailboxTypeId = -1;
            // TODO for virtual mailboxes, we may want to do something besides just fake it
            mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_ERROR,
                    UIProvider.LastSyncResult.SUCCESS);
            mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_TOTAL_COUNT,
                    cursor != null ? cursor.getCount() : 0);
            mExtras.putInt(UIProvider.CursorExtraKeys.EXTRA_STATUS,
                    UIProvider.CursorStatus.COMPLETE);
        }
    }

    @Override
    public Bundle getExtras() {
        return mExtras;
    }

    @Override
    public Bundle respond(Bundle params) {
        final String setVisibilityKey =
                UIProvider.ConversationCursorCommand.COMMAND_KEY_SET_VISIBILITY;
        if (params.containsKey(setVisibilityKey)) {
            final boolean visible = params.getBoolean(setVisibilityKey);
            if (visible) {
                // Mark all messages as seen
                markContentsSeen();
                if (params.containsKey(
                        UIProvider.ConversationCursorCommand.COMMAND_KEY_ENTERED_FOLDER)) {
                    Mailbox mailbox = Mailbox.restoreMailboxWithId(mContext, mMailboxId);
                    //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 MOD_S
                    //should also check the account sync frequency
                    Account account = null;
                    if (mailbox != null) {
                        account = Account.restoreAccountWithId(mContext, mailbox.mAccountKey);
                    }
                    if (mailbox != null && account != null) {
                        // For non-push mailboxes, if it's stale (i.e. last sync was a while
                        // ago), force a sync.
                        // TODO: Fix the check for whether we're non-push? Right now it checks
                        // whether we are participating in account sync rules.
                        // NOTE: should also check the account sync frequency, do not force sync when account sync frequency is set to never
                        if (mailbox.mSyncInterval == 0 && account.getSyncInterval()!= Account.CHECK_INTERVAL_NEVER) {
                            //TS: zheng.zou 2015-11-23 EMAIL BUGFIX_718891 MOD_E
                            final long timeSinceLastSync =
                                    System.currentTimeMillis() - mailbox.mSyncTime;
                            if (timeSinceLastSync > AUTO_REFRESH_INTERVAL_MS) {
                                final ContentResolver resolver = mContext.getContentResolver();
                                final Uri refreshUri = Uri.parse(EmailContent.CONTENT_URI +
                                        "/" + EmailProvider.QUERY_UIREFRESH + "/" + mailbox.mId);
                                //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304,close cursor
                                Cursor cursor = resolver.query(refreshUri, null, null, null, null);
                                if (cursor != null) {
                                    cursor.close();
                                }
                                //[BUGFIX]-Mod-END by TCTNB.caixia.chen
                            }
                        }
                    }
                }
            }
        }
        // Return success
        final Bundle response = new Bundle(2);

        response.putString(setVisibilityKey,
                UIProvider.ConversationCursorCommand.COMMAND_RESPONSE_OK);

        final String rawFoldersKey =
                UIProvider.ConversationCursorCommand.COMMAND_GET_RAW_FOLDERS;
        if (params.containsKey(rawFoldersKey)) {
            response.putParcelable(rawFoldersKey, mFolderList);
        }

        final String convInfoKey =
                UIProvider.ConversationCursorCommand.COMMAND_GET_CONVERSATION_INFO;
        if (params.containsKey(convInfoKey)) {
            response.putParcelable(convInfoKey, generateConversationInfo());
        }

        return response;
    }

    private ConversationInfo generateConversationInfo() {
        final int numMessages = getInt(getColumnIndex(ConversationColumns.NUM_MESSAGES));
        final ConversationInfo conversationInfo = new ConversationInfo(numMessages);

        conversationInfo.firstSnippet = getString(getColumnIndex(ConversationColumns.SNIPPET));
        conversationInfo.lastSnippet = conversationInfo.firstSnippet;
        conversationInfo.firstUnreadSnippet = conversationInfo.firstSnippet;

        final boolean isRead = getInt(getColumnIndex(ConversationColumns.READ)) != 0;
        final String senderString = getString(getColumnIndex(EmailContent.MessageColumns.DISPLAY_NAME));

        final String fromString = getString(getColumnIndex(EmailContent.MessageColumns.FROM_LIST));
        final String senderEmail;

        if (fromString != null) {
            final Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(fromString);
            if (tokens.length > 0) {
                senderEmail = tokens[0].getAddress();
            } else {
                LogUtils.d(LogUtils.TAG, "Couldn't parse sender email address");
                senderEmail = fromString;
            }
        } else {
            senderEmail = null;
        }

        // we *intentionally* report no participants for Draft emails so that the UI always
        // displays the single word "Draft" as per b/13304929
        if (mMailboxTypeId == Mailbox.TYPE_DRAFTS) {
            // the UI displays "Draft" in the conversation list based on this count
            conversationInfo.draftCount = 1;
        } else if (mMailboxTypeId == Mailbox.TYPE_SENT ||
                mMailboxTypeId == Mailbox.TYPE_OUTBOX) {
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
            //NOTE: We only display the mail status in OUTBOX.
            if (mMailboxTypeId == Mailbox.TYPE_OUTBOX) {
                conversationInfo.status = getInt(getColumnIndex(EmailContent.MessageColumns.SENDING_STATUS));
            }
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
            // for conversations in outgoing mail mailboxes return a list of recipients
            final String recipientsString = getString(getColumnIndex(
                    EmailContent.MessageColumns.TO_LIST));
            final Address[] recipientAddresses = Address.parse(recipientsString);
            for (Address recipientAddress : recipientAddresses) {
                final String name = recipientAddress.getSimplifiedName();
                final String email = recipientAddress.getAddress();

                // all recipients are said to have read all messages in the conversation
                conversationInfo.addParticipant(new ParticipantInfo(name, email, 0, isRead));
            }
        } else {
            // for conversations in incoming mail mailboxes return the sender
            conversationInfo.addParticipant(new ParticipantInfo(senderString, senderEmail, 0,
                    isRead));
        }

        return conversationInfo;
    }

    @Override
    public void markContentsSeen() {
        final ContentResolver resolver = mContext.getContentResolver();
        final ContentValues contentValues = new ContentValues(1);
        contentValues.put(EmailContent.MessageColumns.FLAG_SEEN, true);
        final Uri uri = EmailContent.Message.CONTENT_URI;
        final String where = EmailContent.MessageColumns.MAILBOX_KEY + " = ? AND " +
                EmailContent.MessageColumns.FLAG_SEEN + " != ?";
        final String[] selectionArgs = {String.valueOf(mMailboxId), "1"};
        resolver.update(uri, contentValues, where, selectionArgs);
    }

    @Override
    public void emptyFolder() {
        final ContentResolver resolver = mContext.getContentResolver();
        final Uri purgeUri = EmailProvider.uiUri("uipurgefolder", mMailboxId);
        // TS: junwei-xu 2015-03-26 EMAIL BUGFIX-958925 MOD_S
//        resolver.delete(purgeUri, null, null);
        HandlerThread handlerThread = new HandlerThread("emptyFolder");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                resolver.delete(purgeUri, null, null);
            }
        });
        // TS: junwei-xu 2015-03-26 EMAIL BUGFIX-958925 MOD_E
    }
}
