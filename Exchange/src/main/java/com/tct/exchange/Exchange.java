/*
 * Copyright (C) 2011 The Android Open Source Project
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
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-969850  2015/4/17    gengkexue       [HOMO][ALWE] Corporate email synchronized again all folders instead of updating only new emails
 ===========================================================================
*/

package com.tct.exchange;

import android.app.Application;

import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.MailboxUtilities;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;

public class Exchange extends Application {
    // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_S
    public static final int NO_BSK_MAILBOX = -1;
    /// M: The bad sync key mailbox id. At present just suppose at
     // most only 1 mailbox may occurs bad sync key at the same time
    public static long sBadSyncKeyMailboxId = NO_BSK_MAILBOX;
    // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_E

    static {
        LogTag.setLogTag(Eas.LOG_TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EmailContent.init(this);
        try {
            getContentResolver().call(EmailContent.CONTENT_URI,
                    MailboxUtilities.FIX_PARENT_KEYS_METHOD, "", null);
        } catch (IllegalArgumentException e) {
            // If there is no Email provider (which happens if eg the
            // Email app is disabled), ignore.
        }

        // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_S
        /** M: This is to check if the bad sync key had ever happened and its recovery process was
            halted by Exchange process crash or device rebooting etc. @{ */
        ExchangePreferences pref = ExchangePreferences.getPreferences(this);
        sBadSyncKeyMailboxId = pref.getBadSyncKeyMailboxId();
        if (sBadSyncKeyMailboxId != NO_BSK_MAILBOX) {
            LogUtils.i(Eas.BSK_TAG, "Unfinished Bad sync key recovery detected," +
                    " mailbox id: " + sBadSyncKeyMailboxId);
        }
        /** @} */
        // AM: Kexue.Geng 2015-04-17 EMAIL BUGFIX_969850 MOD_E
    }
}
