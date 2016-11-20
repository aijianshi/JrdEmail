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
 *Tag		 	 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-50003 2014/10/24   zhaotianyong	  Modify the package conflict
 ============================================================================ 
 */

package com.tct.emailcommon.mail;

import android.util.Base64;
import android.util.Base64OutputStream;
//TS: MOD by zhaotianyong for CONFLICT_50003 START
//import org.apache.commons.io.IOUtils;
import com.tct.mail.utils.IOUtils;
//TS: MOD by zhaotianyong for CONFLICT_50003 END

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Base64Body implements Body {
    private final InputStream mSource;
    // Because we consume the input stream, we can only write out once
    private boolean mAlreadyWritten;

    public Base64Body(InputStream source) {
        mSource = source;
    }

    @Override
    public InputStream getInputStream() throws MessagingException {
        return mSource;
    }

    /**
     * This method consumes the input stream, so can only be called once
     * @param out Stream to write to
     * @throws IllegalStateException If called more than once
     * @throws IOException
     * @throws MessagingException
     */
    @Override
    public void writeTo(OutputStream out)
            throws IllegalStateException, IOException, MessagingException {
        if (mAlreadyWritten) {
            throw new IllegalStateException("Base64Body can only be written once");
        }
        mAlreadyWritten = true;
        try {
            final Base64OutputStream b64out = new Base64OutputStream(out, Base64.DEFAULT);
            IOUtils.copyLarge(mSource, b64out);
        } finally {
            mSource.close();
        }
    }
}
