/*
 * Copyright (C) 2012 Google Inc.
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
 *Tag             Date          Author               Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *CR_540046      2015/9/2     yanhua.chen     Subject&Sender
 ============================================================================
 */
package com.tct.mail.browse;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.CharacterStyle;
import android.util.LruCache;
import android.util.Pair;

import com.tct.email.R;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
//import com.google.common.base.Objects;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.base.Objects;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.ParticipantInfo;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.utils.FolderUri;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.ArrayList;
import java.util.List;

/**
 * This is the view model for the conversation header. It includes all the
 * information needed to layout a conversation header view. Each view model is
 * associated with a conversation and is cached to improve the relayout time.
 */
public class ConversationItemViewModel {
    private static final int MAX_CACHE_SIZE = 100;

    @VisibleForTesting
    static LruCache<Pair<String, Long>, ConversationItemViewModel> sConversationHeaderMap
        = new LruCache<Pair<String, Long>, ConversationItemViewModel>(MAX_CACHE_SIZE);

    /**
     * The Folder associated with the cache of models.
     */
    private static Folder sCachedModelsFolder;

    // The hashcode used to detect if the conversation has changed.
    private int mDataHashCode;
    private int mLayoutHashCode;

    // Unread
    public boolean unread;

    // Date
    CharSequence dateText;
    public boolean showDateText = true;

    // Personal level
    Bitmap personalLevelBitmap;

    public Bitmap infoIcon;

    public String badgeText;

    public int insetPadding = 0;

    // Paperclip
    Bitmap paperclip;

    /** If <code>true</code>, we will not apply any formatting to {@link #sendersText}. */
    public boolean preserveSendersText = false;

    // Senders
    public String sendersText;

    SpannableStringBuilder sendersDisplayText;
    StaticLayout sendersDisplayLayout;

    //TS: yanhua.chen 2015-9-2 EMAIL CD_ID ADD_S
    //Subject
    public String subjectText;
    public boolean preserveSubjectText = false;
    SpannableStringBuilder subjectDisplayText;
    StaticLayout subjectDisplayLayout;
    //TS: yanhua.chen 2015-9-2 EMAIL CD_ID ADD_E

    boolean hasDraftMessage;

    // View Width
    public int viewWidth;

    // Standard scaled dimen used to detect if the scale of text has changed.
    @Deprecated
    public int standardScaledDimen;

    public long maxMessageId;

    public int gadgetMode;

    public Conversation conversation;

    public ConversationItemView.ConversationItemFolderDisplayer folderDisplayer;

    public boolean hasBeenForwarded;

    public boolean hasBeenRepliedTo;

    public boolean isInvite;

    public SpannableStringBuilder messageInfoString;

    public int styledMessageInfoStringOffset;

    private String mContentDescription;

    /**
     * Email addresses corresponding to the senders/recipients that will be displayed on the top
     * line; used to generate the conversation icon.
     */
    public ArrayList<String> displayableEmails;

    /**
     * Display names corresponding to the email address for the senders/recipients that will be
     * displayed on the top line.
     */
    public ArrayList<String> displayableNames;

    /**
     * A styled version of the {@link #displayableNames} to be displayed on the top line.
     */
    public ArrayList<SpannableString> styledNames;

    /**
     * Returns the view model for a conversation. If the model doesn't exist for this conversation
     * null is returned. Note: this should only be called from the UI thread.
     *
     * @param account the account contains this conversation
     * @param conversationId the Id of this conversation
     * @return the view model for this conversation, or null
     */
    @VisibleForTesting
    static ConversationItemViewModel forConversationIdOrNull(String account, long conversationId) {
        final Pair<String, Long> key = new Pair<String, Long>(account, conversationId);
        synchronized(sConversationHeaderMap) {
            return sConversationHeaderMap.get(key);
        }
    }

    static ConversationItemViewModel forConversation(String account, Conversation conv) {
        ConversationItemViewModel header = ConversationItemViewModel.forConversationId(account,
                conv.id);
        header.conversation = conv;
        header.unread = !conv.read;
        header.hasBeenForwarded =
                (conv.convFlags & UIProvider.ConversationFlags.FORWARDED)
                == UIProvider.ConversationFlags.FORWARDED;
        header.hasBeenRepliedTo =
                (conv.convFlags & UIProvider.ConversationFlags.REPLIED)
                == UIProvider.ConversationFlags.REPLIED;
        header.isInvite =
                (conv.convFlags & UIProvider.ConversationFlags.CALENDAR_INVITE)
                == UIProvider.ConversationFlags.CALENDAR_INVITE;
        return header;
    }

    /**
     * Returns the view model for a conversation. If this is the first time
     * call, a new view model will be returned. Note: this should only be called
     * from the UI thread.
     *
     * @param account the account contains this conversation
     * @param conversationId the Id of this conversation
     * @return the view model for this conversation
     */
    static ConversationItemViewModel forConversationId(String account, long conversationId) {
        synchronized(sConversationHeaderMap) {
            ConversationItemViewModel header =
                    forConversationIdOrNull(account, conversationId);
            if (header == null) {
                final Pair<String, Long> key = new Pair<String, Long>(account, conversationId);
                header = new ConversationItemViewModel();
                sConversationHeaderMap.put(key, header);
            }
            return header;
        }
    }

    /**
     * Returns the hashcode to compare if the data in the header is valid.
     */
    private static int getHashCode(CharSequence dateText, Object convInfo,
            List<Folder> rawFolders, boolean starred, boolean read, int priority,
            int sendingState) {
        if (dateText == null) {
            return -1;
        }
        return Objects.hashCode(convInfo, dateText, rawFolders, starred, read, priority,
                sendingState);
    }

    /**
     * Returns the layout hashcode to compare to see if the layout state has changed.
     */
    private int getLayoutHashCode() {
        return Objects.hashCode(mDataHashCode, viewWidth, standardScaledDimen, gadgetMode);
    }

    /**
     * Marks this header as having valid data and layout.
     */
    void validate() {
        mDataHashCode = getHashCode(dateText,
                conversation.conversationInfo, conversation.getRawFolders(), conversation.starred,
                conversation.read, conversation.priority, conversation.sendingState);
        mLayoutHashCode = getLayoutHashCode();
    }

    /**
     * Returns if the data in this model is valid.
     */
    boolean isDataValid() {
        return mDataHashCode == getHashCode(dateText,
                conversation.conversationInfo, conversation.getRawFolders(), conversation.starred,
                conversation.read, conversation.priority, conversation.sendingState);
    }

    /**
     * Returns if the layout in this model is valid.
     */
    boolean isLayoutValid() {
        return isDataValid() && mLayoutHashCode == getLayoutHashCode();
    }

    /**
     * Describes the style of a Senders fragment.
     */
    static class SenderFragment {
        // Indices that determine which substring of mSendersText we are
        // displaying.
        int start;
        int end;

        // The style to apply to the TextPaint object.
        CharacterStyle style;

        // Width of the fragment.
        int width;

        // Ellipsized text.
        String ellipsizedText;

        // Whether the fragment is fixed or not.
        boolean isFixed;

        // Should the fragment be displayed or not.
        boolean shouldDisplay;

        SenderFragment(int start, int end, CharSequence sendersText, CharacterStyle style,
                boolean isFixed) {
            this.start = start;
            this.end = end;
            this.style = style;
            this.isFixed = isFixed;
        }
    }


    /**
     * Reset the content description; enough content has changed that we need to
     * regenerate it.
     */
    public void resetContentDescription() {
        mContentDescription = null;
    }

    /**
     * Get conversation information to use for accessibility.
     */
    public CharSequence getContentDescription(Context context, boolean showToHeader) {
        if (mContentDescription == null) {
            // If any are unread, get the first unread sender.
            // If all are unread, get the first sender.
            // If all are read, get the last sender.
            String participant = "";
            String lastParticipant = "";
            int last = conversation.conversationInfo.participantInfos != null ?
                    conversation.conversationInfo.participantInfos.size() - 1 : -1;
            if (last != -1) {
                lastParticipant = conversation.conversationInfo.participantInfos.get(last).name;
            }
            if (conversation.read) {
                participant = TextUtils.isEmpty(lastParticipant) ?
                        SendersView.getMe(showToHeader /* useObjectMe */) : lastParticipant;
            } else {
                ParticipantInfo firstUnread = null;
                for (ParticipantInfo p : conversation.conversationInfo.participantInfos) {
                    if (!p.readConversation) {
                        firstUnread = p;
                        break;
                    }
                }
                if (firstUnread != null) {
                    participant = TextUtils.isEmpty(firstUnread.name) ?
                            SendersView.getMe(showToHeader /* useObjectMe */) : firstUnread.name;
                }
            }
            if (TextUtils.isEmpty(participant)) {
                // Just take the last sender
                participant = lastParticipant;
            }

            // the toHeader should read "To: " if requested
            String toHeader = "";
            if (showToHeader && !TextUtils.isEmpty(participant)) {
                toHeader = SendersView.getFormattedToHeader().toString();
            }

            boolean isToday = DateUtils.isToday(conversation.dateMs);
            String date = DateUtils.getRelativeTimeSpanString(context, conversation.dateMs)
                    .toString();
            String readString = context.getString(
                    conversation.read ? R.string.read_string : R.string.unread_string);
            int res = isToday ? R.string.content_description_today : R.string.content_description;
            mContentDescription = context.getString(res, toHeader, participant,
                    conversation.subject, conversation.getSnippet(), date, readString);
        }
        return mContentDescription;
    }

    /**
     * Clear cached header model objects when accessibility changes.
     */

    public static void onAccessibilityUpdated() {
        sConversationHeaderMap.evictAll();
    }

    /**
     * Clear cached header model objects when the folder changes.
     */
    public static void onFolderUpdated(Folder folder) {
        final FolderUri old = sCachedModelsFolder != null
                ? sCachedModelsFolder.folderUri : FolderUri.EMPTY;
        final FolderUri newUri = folder != null ? folder.folderUri : FolderUri.EMPTY;
        if (!old.equals(newUri)) {
            sCachedModelsFolder = folder;
            sConversationHeaderMap.evictAll();
        }
    }
}