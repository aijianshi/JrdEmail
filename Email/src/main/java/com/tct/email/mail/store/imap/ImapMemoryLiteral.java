/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.tct.email.mail.store.imap;

import com.tct.emailcommon.Logging;
import com.tct.emailcommon.mail.Folder;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.email.FixedLengthInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Subclass of {@link ImapString} used for literals backed by an in-memory byte array.
 */
public class ImapMemoryLiteral extends ImapString {
    private byte[] mData;

    /* package */ ImapMemoryLiteral(FixedLengthInputStream in) throws IOException {
        // We could use ByteArrayOutputStream and IOUtils.copy, but it'd perform an unnecessary
        // copy....
        mData = new byte[in.getLength()];
        int pos = 0;
        while (pos < mData.length) {
            int read = in.read(mData, pos, mData.length - pos);
            if (read < 0) {
                break;
            }
            pos += read;
        }
        if (pos != mData.length) {
            LogUtils.w(Logging.LOG_TAG, "");
        }
    }

    // TS: Gantao 2015-12-07 EMAIL BUGFIX_1020377 ADD_S
    /** copy stream from server to local.
     * added UI update processing.
     * @param in stream : stream from the server
     * @param listener : update UI
     * @throws IOException
     */
    ImapMemoryLiteral(FixedLengthInputStream in, Folder.MessageRetrievalListener listener)
            throws IOException {
        // We could use ByteArrayOutputStream and IOUtils.copy, but it'd perform
        // an unnecessary copy....
        mData = new byte[in.getLength()];
        int size = in.getLength();
        int pos = 0;
        long lastCallbackPct = -1;
        while (pos < mData.length) {
            int read = in.read(mData, pos, mData.length - pos);
            if (read < 0) {
                break;
            }

            pos += read;
            final int pct = (int)((pos * 100) / size);
            //callback to update ui progress. Loading data from server finished, but not send finished callback.
            //Because we need wait finished decoding the file to local.
            if (listener != null && size != 0 && pos < size && lastCallbackPct < pct) {
                listener.loadAttachmentProgress(pos * 100 / size);
                lastCallbackPct = pct;
            }
        }
        if (pos != mData.length) {
            LogUtils.w(Logging.LOG_TAG, "");
        }
    }
    // TS: Gantao 2015-12-07 EMAIL BUGFIX_1020377 MOD_E

    @Override
    public void destroy() {
        mData = null;
        super.destroy();
    }

    @Override
    public String getString() {
        return Utility.fromAscii(mData);
    }

    @Override
    public InputStream getAsStream() {
        return new ByteArrayInputStream(mData);
    }

    @Override
    public String toString() {
        return String.format("{%d byte literal(memory)}", mData.length);
    }
}
