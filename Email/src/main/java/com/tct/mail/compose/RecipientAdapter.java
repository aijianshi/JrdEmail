/**
 * Copyright (c) 2007, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 *CONFLICT-20002 2014/10/24   wenggangjin	Modify the package conflict   
 ===========================================================================
 */

package com.tct.mail.compose;
//TS: MOD by wenggangjin for CONFLICT_20002 START
//import com.tct.ex.chips.BaseRecipientAdapter;
import com.tct.fw.ex.chips.BaseRecipientAdapter;
import com.tct.mail.providers.Account;
//TS: MOD by wenggangjin for CONFLICT_20002 START

import android.content.Context;

public class RecipientAdapter extends BaseRecipientAdapter {
    public RecipientAdapter(Context context, Account account) {
        super(context);
        setAccount(account.getAccountManagerAccount());
    }
}
