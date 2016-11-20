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
 *Tag		 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin	Modify the package conflict
 ============================================================================ 
 */
package com.tct.mail.browse;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.Maps;
import com.tct.fw.google.common.collect.Maps;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.UIProvider;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;


import java.util.Map;

public class AttachmentLoader extends CursorLoader {

    public AttachmentLoader(Context c, Uri uri) {
        super(c, uri, UIProvider.ATTACHMENT_PROJECTION, null, null, null);
    }

    @Override
    public Cursor loadInBackground() {
        return new AttachmentCursor(super.loadInBackground());
    }

    public static class AttachmentCursor extends CursorWrapper {

        private Map<String, Attachment> mCache = Maps.newHashMap();

        private AttachmentCursor(Cursor inner) {
            super(inner);
        }

        public Attachment get() {
            final String uri = getWrappedCursor().getString(UIProvider.ATTACHMENT_URI_COLUMN);
            Attachment m = mCache.get(uri);
            if (m == null) {
                m = new Attachment(this);
                mCache.put(uri, m);
            }
            return m;
        }
    }
}
