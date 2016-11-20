/*
 ==========================================================================
 *HISTORY
 *
 *Tag               Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-991085     2015/03/30   jin.dong      [Email]After the web side to change the password, MS without prompting
 ===========================================================================
 */
package com.tct.email.activity.setup;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.IntentUtilities;
import com.tct.mail.ui.MailAsyncTaskLoader;

/**
 * This activity is headless. It exists to load the Account object from  the given account ID and
 * then starts the {@link AccountServerSettingsActivity} activity with the incoming/outgoing
 * settings fragment
 */
public class HeadlessAccountSettingsLoader extends Activity {

    public static Uri getIncomingSettingsUri(long accountId) {
        final Uri.Builder baseUri = Uri.parse("auth://" + EmailContent.EMAIL_PACKAGE_NAME +
                ".ACCOUNT_SETTINGS/incoming/").buildUpon();
        IntentUtilities.setAccountId(baseUri, accountId);
        return baseUri.build();
    }

    public static Uri getOutgoingSettingsUri(long accountId) {
        final Uri.Builder baseUri = Uri.parse("auth://" + EmailContent.EMAIL_PACKAGE_NAME +
                ".ACCOUNT_SETTINGS/outgoing/").buildUpon();
        IntentUtilities.setAccountId(baseUri, accountId);
        return baseUri.build();
    }
    //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_S
    //NOTE: user can going to incoming settings from sign_in toast /notificaion.
    private boolean isAuthError = false;
    //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_E
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent i = getIntent();
        final long accountID = IntentUtilities.getAccountIdFromIntent(i);

        if (savedInstanceState == null) {
            //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_S
            isAuthError = getIntent().getBooleanExtra("AUTHENTICATIONFAILED", false);
            //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_E
            new LoadAccountIncomingSettingsAsyncTask(getApplicationContext(),
                    "incoming".equals(i.getData().getLastPathSegment()))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, accountID);
        }
    }

    // TS: Gantao 2015-11-28 EMAIL BUGFIX-983923 ADD_S
    //Android M's bug, setvisible(true) in onStart()can avoid the crash issue
    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
    // TS: Gantao 2015-11-28 EMAIL BUGFIX-983923 ADD_E

    /**
     * Asynchronously loads the Account object from its ID and then navigates to the AccountSettings
     * fragment.
     */
    private class LoadAccountIncomingSettingsAsyncTask extends AsyncTask<Long, Void, Account> {
        private final Context mContext;
        private final boolean mIncoming;

        private LoadAccountIncomingSettingsAsyncTask(Context context, boolean incoming) {
            mContext = context;
            mIncoming = incoming;
        }

        protected Account doInBackground(Long... params) {
            return Account.restoreAccountWithId(mContext, params[0]);
        }

        protected void onPostExecute(Account result) {
            // create an Intent to view a new activity
            final Intent intent;
            if (mIncoming) {
                intent = AccountServerSettingsActivity.getIntentForIncoming(mContext, result);
                //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_S
                //NOTE: resend the flag to AccountServerSettingsActivity,that judge if auto switch incoming or outgoing fragment.
                intent.putExtra("AUTHENTICATIONFAILED",isAuthError);
                //TS: jin.dong 2015-05-29 EMAIL BUGFIX_991085 ADD_E
            } else {
                intent = AccountServerSettingsActivity.getIntentForOutgoing(mContext, result);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(intent);

            finish();
         }
    }
}