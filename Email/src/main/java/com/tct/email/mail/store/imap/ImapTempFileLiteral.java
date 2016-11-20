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
import com.tct.emailcommon.TempDirectory;
import com.tct.emailcommon.mail.Folder;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.email.FixedLengthInputStream;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Subclass of {@link ImapString} used for literals backed by a temp file.
 */
public class ImapTempFileLiteral extends ImapString {
    /* package for test */ final File mFile;

    /** Size is purely for toString() */
    private final int mSize;

    /* package */  ImapTempFileLiteral(FixedLengthInputStream stream) throws IOException {
        mSize = stream.getLength();
        mFile = File.createTempFile("imap", ".tmp", TempDirectory.getTempDirectory());

        // Unfortunately, we can't really use deleteOnExit(), because temp filenames are random
        // so it'd simply cause a memory leak.
        // deleteOnExit() simply adds filenames to a static list and the list will never shrink.
        // mFile.deleteOnExit();
        OutputStream out = new FileOutputStream(mFile);
        IOUtils.copy(stream, out);
        out.close();
    }

    // TS: Gantao 2015-12-07 EMAIL BUGFIX_1020377 ADD_S
    /** copy stream from server to local
     * added UI update processing.
     * @param in stream : stream from the server
     * @param listener : update UI
     * @throws IOException
     */
    ImapTempFileLiteral(FixedLengthInputStream stream,
                        Folder.MessageRetrievalListener listener) throws IOException {
        mSize = stream.getLength();
        mFile = File.createTempFile("imap", ".tmp", TempDirectory.getTempDirectory());

        // Unfortunately, we can't really use deleteOnExit(), because temp filenames are random
        // so it'd simply cause a memory leak.
        // deleteOnExit() simply adds filenames to a static list and the list will never shrink.
        // mFile.deleteOnExit();
        OutputStream out = new FileOutputStream(mFile);
        try {
            // IOUtils.copy(stream, out);
            byte[] buffer = new byte[4 * 1024];
            int size = stream.getLength();
            int count = 0;
            int n = 0;
            long lastCallbackPct = -1;
            while (-1 != (n = stream.read(buffer))) {
                out.write(buffer, 0, n);

                count += n;
                final int pct = (int)((count * 100) / size);
                //callback to update ui progress. Loading data from server finished, but not send finished callback.
                //Because we need wait finished decoding the file to local.
                if (listener != null && size != 0 && count < size && lastCallbackPct < pct) {
                    listener.loadAttachmentProgress(pct);
                    lastCallbackPct = pct;
                }
            }
        } finally {
            out.close();
        }
    }
    // TS: Gantao 2015-12-07 EMAIL BUGFIX_1020377 MOD_E

    /**
     * Make sure we delete the temp file.
     *
     * We should always be calling {@link ImapResponse#destroy()}, but it's here as a last resort.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    @Override
    public InputStream getAsStream() {
        checkNotDestroyed();
        try {
            return new FileInputStream(mFile);
        } catch (FileNotFoundException e) {
            // It's probably possible if we're low on storage and the system clears the cache dir.
            LogUtils.w(Logging.LOG_TAG, "ImapTempFileLiteral: Temp file not found");

            // Return 0 byte stream as a dummy...
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public String getString() {
        checkNotDestroyed();
        try {
            byte[] bytes = IOUtils.toByteArray(getAsStream());
            // Prevent crash from OOM; we've seen this, but only rarely and not reproducibly
            if (bytes.length > ImapResponseParser.LITERAL_KEEP_IN_MEMORY_THRESHOLD) {
                throw new IOException();
            }
            return Utility.fromAscii(bytes);
        } catch (IOException e) {
            LogUtils.w(Logging.LOG_TAG, "ImapTempFileLiteral: Error while reading temp file", e);
            return "";
        }
    }

    @Override
    public void destroy() {
        try {
            if (!isDestroyed() && mFile.exists()) {
                mFile.delete();
            }
        } catch (RuntimeException re) {
            // Just log and ignore.
            LogUtils.w(Logging.LOG_TAG, "Failed to remove temp file: " + re.getMessage());
        }
        super.destroy();
    }

    @Override
    public String toString() {
        return String.format("{%d byte literal(file)}", mSize);
    }

    public boolean tempFileExistsForTest() {
        return mFile.exists();
    }
}
