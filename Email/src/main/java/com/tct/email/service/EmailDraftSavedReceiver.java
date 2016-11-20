package com.tct.email.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.tct.email.R;
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

 /*
 ========================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ===========================================================
 *BUGFIX_996919 2015/06/04    zheng.zou       [Email](new) draft auto saving & discard ui change
 */
public class EmailDraftSavedReceiver extends BroadcastReceiver {
    private static final String DRAFT_SAVED_ACTION = "com.tct.mail.action.DRAFT_SAVED_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && DRAFT_SAVED_ACTION.equals(intent.getAction())) {
			//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
			Utility.showToast(context, R.string.message_saved);
            //Toast.makeText(context, R.string.message_saved,
            //        Toast.LENGTH_LONG).show();
        }
    }
}
