/*******************************************************************************
 *      Copyright (C) 2012 Google Inc.
 *      Licensed to The Android Open Source Project.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Ta             Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-980186  2015/11/27   jian.xu         [Android L][Email]There is a garbage icon for sending mail
 ===========================================================================
 */
package com.tct.mail.ui;

import android.os.Parcel;
import android.os.Parcelable;

//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
//import com.google.common.collect.BiMap;
//import com.google.common.collect.HashBiMap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.collect.BiMap;
import com.tct.fw.google.common.collect.HashBiMap;
import com.tct.fw.google.common.collect.Lists;
import com.tct.fw.google.common.collect.Sets;
import com.tct.mail.browse.ConversationCursor;
import com.tct.mail.providers.Conversation;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.*;

/**
 * A simple thread-safe wrapper over a set of conversations representing a
 * selection set (e.g. in a conversation list). This class dispatches changes
 * when the set goes empty, and when it becomes unempty. For simplicity, this
 * class <b>does not allow modifications</b> to the collection in observers when
 * responding to change events.
 */
public class ConversationSelectionSet implements Parcelable {
    public static final ClassLoaderCreator<ConversationSelectionSet> CREATOR =
            new ClassLoaderCreator<ConversationSelectionSet>() {

        @Override
        public ConversationSelectionSet createFromParcel(Parcel source) {
            return new ConversationSelectionSet(source, null);
        }

        @Override
        public ConversationSelectionSet createFromParcel(Parcel source, ClassLoader loader) {
            return new ConversationSelectionSet(source, loader);
        }

        @Override
        public ConversationSelectionSet[] newArray(int size) {
            return new ConversationSelectionSet[size];
        }

    };

    private final Object mLock = new Object();
    /** Map of conversation ID to conversation objects. Every selected conversation is here. */
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
    // Use linkedhashmap to remember order for the items in the map.
    //private final HashMap<Long, Conversation> mInternalMap = new HashMap<Long, Conversation>();
    private final LinkedHashMap<Long, Conversation> mInternalMap = new LinkedHashMap<Long, Conversation>();
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-E
    /** Map of Conversation URI to Conversation ID. */
    private final BiMap<String, Long> mConversationUriToIdMap = HashBiMap.create();
    /** All objects that are interested in changes to the selected set. */
    @VisibleForTesting
    final Set<ConversationSetObserver> mObservers = new HashSet<ConversationSetObserver>();

    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
    /** whether user select all current conversations */
    private boolean mSelectAll = false;
    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E

    /**
     * Create a new object,
     */
    public ConversationSelectionSet() {
        // Do nothing.
    }

    private ConversationSelectionSet(Parcel source, ClassLoader loader) {
        Parcelable[] conversations = source.readParcelableArray(loader);
        for (Parcelable parceled : conversations) {
            Conversation conversation = (Conversation) parceled;
            put(conversation.id, conversation);
        }
    }

    /**
     * Registers an observer to listen for interesting changes on this set.
     *
     * @param observer the observer to register.
     */
    public void addObserver(ConversationSetObserver observer) {
        synchronized (mLock) {
            mObservers.add(observer);
        }
    }

    /**
     * Clear the selected set entirely.
     */
    public void clear() {
        synchronized (mLock) {
            boolean initiallyNotEmpty = !mInternalMap.isEmpty();
            mInternalMap.clear();
            mConversationUriToIdMap.clear();

            if (mInternalMap.isEmpty() && initiallyNotEmpty) {
                ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
                dispatchOnChange(observersCopy);
                dispatchOnEmpty(observersCopy);
            }
        }
    }

    /**
     * Returns true if the given key exists in the conversation selection set. This assumes
     * the internal representation holds conversation.id values.
     * @param key the id of the conversation
     * @return true if the key exists in this selected set.
     */
    private boolean containsKey(Long key) {
        synchronized (mLock) {
            return mInternalMap.containsKey(key);
        }
    }

    /**
     * Returns true if the given conversation is stored in the selection set.
     * @param conversation
     * @return true if the conversation exists in the selected set.
     */
    public boolean contains(Conversation conversation) {
        synchronized (mLock) {
            return containsKey(conversation.id);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void dispatchOnBecomeUnempty(ArrayList<ConversationSetObserver> observers) {
        synchronized (mLock) {
            for (ConversationSetObserver observer : observers) {
                observer.onSetPopulated(this);
            }
        }
    }

    private void dispatchOnChange(ArrayList<ConversationSetObserver> observers) {
        synchronized (mLock) {
            // Copy observers so that they may unregister themselves as listeners on
            // event handling.
            for (ConversationSetObserver observer : observers) {
                observer.onSetChanged(this);
            }
        }
    }

    private void dispatchOnEmpty(ArrayList<ConversationSetObserver> observers) {
        synchronized (mLock) {
            for (ConversationSetObserver observer : observers) {
                observer.onSetEmpty();
            }
        }
    }

    /**
     * Is this conversation set empty?
     * @return true if the conversation selection set is empty. False otherwise.
     */
    public boolean isEmpty() {
        synchronized (mLock) {
            return mInternalMap.isEmpty();
        }
    }

    private void put(Long id, Conversation info) {
        synchronized (mLock) {
            final boolean initiallyEmpty = mInternalMap.isEmpty();
            mInternalMap.put(id, info);
            mConversationUriToIdMap.put(info.uri.toString(), id);

            final ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
            dispatchOnChange(observersCopy);
            if (initiallyEmpty) {
                dispatchOnBecomeUnempty(observersCopy);
            }
        }
    }

    /** @see java.util.HashMap#remove */
    private void remove(Long id) {
        synchronized (mLock) {
            removeAll(Collections.singleton(id));
        }
    }

    private void removeAll(Collection<Long> ids) {
        synchronized (mLock) {
            final boolean initiallyNotEmpty = !mInternalMap.isEmpty();

            final BiMap<Long, String> inverseMap = mConversationUriToIdMap.inverse();

            for (Long id : ids) {
                mInternalMap.remove(id);
                inverseMap.remove(id);
            }

            ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
            dispatchOnChange(observersCopy);
            if (mInternalMap.isEmpty() && initiallyNotEmpty) {
                dispatchOnEmpty(observersCopy);
            }
        }
    }

    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
    /**
     * Add all conversations in mail list to the selected set.
     * @param cursor : conversation list cursor which contains all the mails
     */
    public void addAll(ConversationCursor cursor) {
        synchronized (mLock) {
            if(cursor.moveToFirst()) {
                do {
                    Conversation conversation = cursor.getConversation();
                    if(conversation == null) {
                        continue;
                    }
                    final long id = conversation.id;
                    if(!mInternalMap.containsKey(id)) {
                        mInternalMap.put(id, conversation);
                        mConversationUriToIdMap.put(conversation.uri.toString(), id);
                    }
                } while(cursor.moveToNext());
            }

            mSelectAll = true;
            final ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
            dispatchOnChange(observersCopy);
        }
    }
    /**
     * Remove all selected conversations
     */
    public void removeAll(){
        synchronized (mLock) {
            final boolean initiallyNotEmpty = !mInternalMap.isEmpty();

            final BiMap<Long, String> inverseMap = mConversationUriToIdMap.inverse();

            mInternalMap.clear();
            inverseMap.clear();

            ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
            dispatchOnChange((observersCopy));
            if (mInternalMap.isEmpty() && initiallyNotEmpty) {
                dispatchOnEmpty(observersCopy);
            }
            mSelectAll = false;
        }
    }

    public void setSelectAll(boolean isSelectAll) {
        mSelectAll = isSelectAll;
    }

    public boolean isSelectAll() {
        return mSelectAll;
    }
    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E

    /**
     * Unregisters an observer for change events.
     *
     * @param observer the observer to unregister.
     */
    public void removeObserver(ConversationSetObserver observer) {
        synchronized (mLock) {
            mObservers.remove(observer);
        }
    }

    /**
     * Returns the number of conversations that are currently selected
     * @return the number of selected conversations.
     */
    public int size() {
        synchronized (mLock) {
            return mInternalMap.size();
        }
    }

    /**
     * Toggles the existence of the given conversation in the selection set. If the conversation is
     * currently selected, it is deselected. If it doesn't exist in the selection set, then it is
     * selected.
     * @param conversation
     */
    public void toggle(Conversation conversation) {
        final long conversationId = conversation.id;
        if (containsKey(conversationId)) {
            // We must not do anything with view here.
            remove(conversationId);
        } else {
            put(conversationId, conversation);
        }
    }

    /** @see java.util.HashMap#values */
    public Collection<Conversation> values() {
        synchronized (mLock) {
            return mInternalMap.values();
        }
    }

    /** @see java.util.HashMap#keySet() */
    public Set<Long> keySet() {
        synchronized (mLock) {
            return mInternalMap.keySet();
        }
    }

    /**
     * Puts all conversations given in the input argument into the selection set. If there are
     * any listeners they are notified once after adding <em>all</em> conversations to the selection
     * set.
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    public void putAll(ConversationSelectionSet other) {
        if (other == null) {
            return;
        }

        final boolean initiallyEmpty = mInternalMap.isEmpty();
        mInternalMap.putAll(other.mInternalMap);

        final ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
        dispatchOnChange(observersCopy);
        if (initiallyEmpty) {
            dispatchOnBecomeUnempty(observersCopy);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Conversation[] values = values().toArray(new Conversation[size()]);
        dest.writeParcelableArray(values, flags);
    }

    /**
     * @param deletedRows an arraylist of conversation IDs which have been deleted.
     */
    public void delete(ArrayList<Integer> deletedRows) {
        for (long id : deletedRows) {
            remove(id);
        }
    }

    //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 ADD_S
    /**
     * Update conversation in selected set
     */
    public void updateSendingStatus(Conversation conversation) {
        for (Long conversationId : mInternalMap.keySet()) {
            if (conversationId == conversation.id) {
                synchronized (mLock) {
                    Conversation innerConv = mInternalMap.get(conversationId);
                    innerConv.conversationInfo.status = conversation.conversationInfo.status;
                    innerConv.sendingState = conversation.sendingState;
                    final ArrayList<ConversationSetObserver> observersCopy = Lists.newArrayList(mObservers);
                    dispatchOnChange(observersCopy);
                    break;
                }
            }
        }
    }
    //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 ADD_E

    /**
     * Iterates through a cursor of conversations and ensures that the current set is present
     * within the result set denoted by the cursor. Any conversations not foun in the result set
     * is removed from the collection.
     */
    public void validateAgainstCursor(ConversationCursor cursor) {
        synchronized (mLock) {
            if (isEmpty()) {
                return;
            }

            if (cursor == null) {
                clear();
                return;
            }

            // First ask the ConversationCursor for the list of conversations that have been deleted
            final Set<String> deletedConversations = cursor.getDeletedItems();
            // For each of the uris in the deleted set, add the conversation id to the
            // itemsToRemoveFromBatch set.
            final Set<Long> itemsToRemoveFromBatch = Sets.newHashSet();
            for (String conversationUri : deletedConversations) {
                final Long conversationId = mConversationUriToIdMap.get(conversationUri);
                if (conversationId != null) {
                    itemsToRemoveFromBatch.add(conversationId);
                }
            }

            // Get the set of the items that had been in the batch
            final Set<Long> batchConversationToCheck = new HashSet<Long>(keySet());

            // Remove all of the items that we know are missing.  This will leave the items where
            // we need to check for existence in the cursor
            batchConversationToCheck.removeAll(itemsToRemoveFromBatch);
            // At this point batchConversationToCheck contains the conversation ids for the
            // conversations that had been in the batch selection, with the items we know have been
            // deleted removed.

            // This set contains the conversation ids that are in the conversation cursor
            final Set<Long> cursorConversationIds = cursor.getConversationIds();

            // We want to remove all of the valid items that are in the conversation cursor, from
            // the batchConversations to check.  The goal is after this block, anything remaining
            // would be items that don't exist in the conversation cursor anymore.
            if (!batchConversationToCheck.isEmpty() && cursorConversationIds != null) {
                batchConversationToCheck.removeAll(cursorConversationIds);
            }

            // At this point any of the item that are remaining in the batchConversationToCheck set
            // are to be removed from the selected conversation set
            itemsToRemoveFromBatch.addAll(batchConversationToCheck);

            removeAll(itemsToRemoveFromBatch);
        }
    }

    @Override
    public String toString() {
        synchronized (mLock) {
            return String.format("%s:%s", super.toString(), mInternalMap);
        }
    }
}
