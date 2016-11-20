/*
 * Copyright (C) 2012 The Android Open Source Project
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
 *Tag              Date          Author          Description
 *============== ============ =============== ==============================
 *BUGFIX- 931547 2015-02-13  tianyong.zhao    [Email]Click Email flash back
 ============================================================================
 */
package com.tct.mail.providers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tct.email.R;
//[FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368,
import com.tct.PreDefineAccountProvider;
//[FEATURE]-Add-END by TSNJ,wei.huang,
import com.tct.email.activity.setup.AccountSetupFinal;
import com.tct.mail.providers.MailAppProvider;
import com.tct.mail.utils.PLFUtils;


public class EmailAccountCacheProvider extends MailAppProvider {
    // Content provider for Email
    private static String sAuthority;
    /**
     * Authority for the suggestions provider. This is specified in AndroidManifest.xml and
     * res/xml/searchable.xml.
     */
    private static String sSuggestionsAuthority;

    @Override
    protected String getAuthority() {
        if (sAuthority == null) {
            sAuthority = getContext().getString(R.string.authority_account_cache_provider);
        }
        return sAuthority;
    }

    @Override
    protected Intent getNoAccountsIntent(Context context) {
        //[FEATURE]-Add-BEGIN by TSNJ,wei.Huang,10/18/2014,PR-515368,
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //if(context.getResources().getBoolean(R.bool.feature_email_account_list_on))
//        if(PLFUtils.getBoolean(context, "feature_email_account_list_on"))
        //PreDefineAccountProvider this feature is not ok,so set it to false
        // TS: tianyong.zhao 2015-02-13 EMAIL BUGFIX_- 931547 MOD_S
        if (false)
        // TS: tianyong.zhao 2015-02-13 EMAIL BUGFIX_- 931547 MOD_E
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
            return PreDefineAccountProvider.actionEnterPreDefineAccountProvider(context,PreDefineAccountProvider.ADD_ACCOUNT_LAUNCH);
        else
            return AccountSetupFinal.actionNewAccountWithResultIntent(context);
        //[FEATURE]-Add-END by TSNJ,wei.huang,
    }

    @Override
    public String getSuggestionAuthority() {
        if (sSuggestionsAuthority == null) {
            sSuggestionsAuthority = getContext().getString(R.string.authority_suggestions_provider);
        }
        return sSuggestionsAuthority;
    }
}
