/*
 * Copyright (C) 2008 The Android Open Source Project
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
/* 04/14/2014|     Chao Zhang       |      FR 635028       |[Email]Download   */
/*           |                      |porting from(FR472914)|options to be im- */
/*           |                      |                      |plemented         */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
package com.tct.emailcommon.mail;

//[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
import com.tct.emailcommon.utility.Utility;
//[FEATURE]-Add-END by TSCD.Chao Zhang

import java.util.Date;
import java.util.HashSet;

public abstract class Message implements Part, Body {
    public static final Message[] EMPTY_ARRAY = new Message[0];

    public enum RecipientType {
        TO, CC, BCC,
    }

    protected String mUid;

    private HashSet<Flag> mFlags = null;

    protected Date mInternalDate;

    protected Folder mFolder;

    //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
    protected int mDownloadOptions = Utility.ENTIRE_MAIL;

    public int getDownloadOptions() {
        return mDownloadOptions;
    }

    public void setDownloadOptions(int downloadOptions) {
        this.mDownloadOptions = downloadOptions;
    }
    //[FEATURE]-Add-END by TSCD.Chao Zhang

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    public Folder getFolder() {
        return mFolder;
    }

    public abstract String getSubject() throws MessagingException;

    public abstract void setSubject(String subject) throws MessagingException;

    public Date getInternalDate() {
        return mInternalDate;
    }

    public void setInternalDate(Date internalDate) {
        this.mInternalDate = internalDate;
    }

    public abstract Date getReceivedDate() throws MessagingException;

    public abstract Date getSentDate() throws MessagingException;

    public abstract void setSentDate(Date sentDate) throws MessagingException;

    public abstract Address[] getRecipients(RecipientType type) throws MessagingException;

    public abstract void setRecipients(RecipientType type, Address[] addresses)
            throws MessagingException;

    public void setRecipient(RecipientType type, Address address) throws MessagingException {
        setRecipients(type, new Address[] {
            address
        });
    }

    public abstract Address[] getFrom() throws MessagingException;

    public abstract void setFrom(Address from) throws MessagingException;

    public abstract Address[] getReplyTo() throws MessagingException;

    public abstract void setReplyTo(Address[] from) throws MessagingException;

    // Always use these instead of getHeader("Message-ID") or setHeader("Message-ID");
    public abstract void setMessageId(String messageId) throws MessagingException;
    public abstract String getMessageId() throws MessagingException;

    @Override
    public boolean isMimeType(String mimeType) throws MessagingException {
        return getContentType().startsWith(mimeType);
    }

    private HashSet<Flag> getFlagSet() {
        if (mFlags == null) {
            mFlags = new HashSet<Flag>();
        }
        return mFlags;
    }

    /*
     * TODO Refactor Flags at some point to be able to store user defined flags.
     */
    public Flag[] getFlags() {
        return getFlagSet().toArray(new Flag[] {});
    }

    /**
     * Set/clear a flag directly, without involving overrides of {@link #setFlag} in subclasses.
     * Only used for testing.
     */
    public final void setFlagDirectlyForTest(Flag flag, boolean set) throws MessagingException {
        if (set) {
            getFlagSet().add(flag);
        } else {
            getFlagSet().remove(flag);
        }
    }

    public void setFlag(Flag flag, boolean set) throws MessagingException {
        setFlagDirectlyForTest(flag, set);
    }

    /**
     * This method calls setFlag(Flag, boolean)
     * @param flags
     * @param set
     */
    public void setFlags(Flag[] flags, boolean set) throws MessagingException {
        for (Flag flag : flags) {
            setFlag(flag, set);
        }
    }

    public boolean isSet(Flag flag) {
        return getFlagSet().contains(flag);
    }

    public abstract void saveChanges() throws MessagingException;

    @Override
    public String toString() {
        return getClass().getSimpleName() + ':' + mUid;
    }
}
