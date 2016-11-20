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
/*
 ==========================================================================
 *HISTORY
 *
 *Tag              Date          Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20021 2014/11/4   wenggangjin      Modify the package conflict
 *BUGFIX- 931547 2015-02-13  tianyong.zhao    [Email]Click Email flash back
 *BUGFIX- 989528 2015-08-07   jin.dong        [Android5.0][Email]Two email setting screens display.
 *BUGFIX-1909256 2016-04-05   junwei-xu       [HZ-IUT][Email]Email ringtone will not change after select ringtone from SD card.
 ============================================================================
 */
package com.tct.email.activity.setup;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.tct.email.R;
import com.tct.emailcommon.utility.IntentUtilities;

import java.util.List;
//TS: MOD by wenggangjin for CONFLICT_20021 START
import com.tct.PreDefineAccountProvider;
import com.tct.mail.providers.UIProvider.EditSettingsExtras;
import com.tct.mail.ui.settings.MailPreferenceActivity;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.utils.Utils;
//TS: MOD by wenggangjin for CONFLICT_20021 END
/**
 * Handles account preferences, using multi-pane arrangement when possible.
 *
 * This activity uses the following fragments:
 *   AccountSettingsFragment
 *   GeneralPreferences
 *   DebugFragment
 *
 */
public class EmailPreferenceActivity extends MailPreferenceActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback { //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD
    /*
     * Intent to open account settings for account=1
        adb shell am start -a android.intent.action.EDIT \
            -d '"content://ui.email.android.com/settings?ACCOUNT_ID=1"'
     */

    // Intent extras for our internal activity launch
    private static final String EXTRA_ENABLE_DEBUG = "AccountSettings.enable_debug";
    //TS: yanhua.chen 2015-8-10 EMAIL BUGFIX_863355 MOD_S
    //Note do ship with the debug menu allowed
    // STOPSHIP: Do not ship with the debug menu allowed.
    private static final boolean DEBUG_MENU_ALLOWED = true;
    //TS: yanhua.chen 2015-8-10 EMAIL BUGFIX_863355 MOD_E

    // Intent extras for launch directly from system account manager
    // NOTE: This string must match the one in res/xml/account_preferences.xml
    private static String INTENT_ACCOUNT_MANAGER_ENTRY;

    // Key codes used to open a debug settings fragment.
    private static final int[] SECRET_KEY_CODES = {
            KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_U,
            KeyEvent.KEYCODE_G
            };
    private int mSecretKeyCodeIndex = 0;

    // When the user taps "Email Preferences" 10 times in a row, we'll enable the debug settings.
    private int mNumGeneralHeaderClicked = 0;

    private boolean mShowDebugMenu;
    private Uri mFeedbackUri;
    private MenuItem mFeedbackMenuItem;
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    private Fragment mCurrentFragment;
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E

    @Override
    public Intent getIntent() {
        final Intent intent = super.getIntent();
        final long accountId = IntentUtilities.getAccountIdFromIntent(intent);
        if (accountId < 0) {
            return intent;
        }
        Intent modIntent = new Intent(intent);
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, AccountSettingsFragment.class.getCanonicalName());
        modIntent.putExtra(
                EXTRA_SHOW_FRAGMENT_ARGUMENTS,
                AccountSettingsFragment.buildArguments(
                        IntentUtilities.getAccountNameFromIntent(intent)));
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        return modIntent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Intent i = getIntent();
        if (savedInstanceState == null) {
            // If we are not restarting from a previous instance, we need to
            // figure out the initial prefs to show.  (Otherwise, we want to
            // continue showing whatever the user last selected.)
            if (INTENT_ACCOUNT_MANAGER_ENTRY == null) {
                INTENT_ACCOUNT_MANAGER_ENTRY = getString(R.string.intent_account_manager_entry);
            }
            if (INTENT_ACCOUNT_MANAGER_ENTRY.equals(i.getAction())) {
                // This case occurs if we're changing account settings from Settings -> Accounts.
                // We get an account object in the intent, but it's not actually useful to us since
                // it's always just the first account of that type. The user can't specify which
                // account they wish to view from within the settings UI, so just dump them at the
                // main screen.
                // android.accounts.Account acct = i.getParcelableExtra("account");
            } else if (i.hasExtra(EditSettingsExtras.EXTRA_FOLDER)) {
                throw new IllegalArgumentException("EXTRA_FOLDER is no longer supported");
            } else {
                // Otherwise, we're called from within the Email app and look for our extras
                final long accountId = IntentUtilities.getAccountIdFromIntent(i);
                if (accountId != -1) {
                    final Bundle args = AccountSettingsFragment.buildArguments(accountId);
                    startPreferencePanel(AccountSettingsFragment.class.getName(), args,
                            0, null, null, 0);
                    // TS: jin.dong 2015-08-07 EMAIL BUGFIX_989528 ADD_S
                    finish();
                    return;
                    // TS: jin.dong 2015-08-07 EMAIL BUGFIX_989528 ADD_E
                }
            }
        }
        mShowDebugMenu = i.getBooleanExtra(EXTRA_ENABLE_DEBUG, false);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        }
        ListView listView = getListView();
        if (listView != null) {
            listView.setDivider(new ColorDrawable(Color.GRAY));
            listView.setDividerHeight(1);
        }
        mFeedbackUri = Utils.getValidUri(getString(R.string.email_feedback_uri));
    }

    /**
     * Listen for secret sequence and, if heard, enable debug menu
     */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getKeyCode() == SECRET_KEY_CODES[mSecretKeyCodeIndex]) {
            mSecretKeyCodeIndex++;
            if (mSecretKeyCodeIndex == SECRET_KEY_CODES.length) {
                mSecretKeyCodeIndex = 0;
                enableDebugMenu();
            }
        } else {
            mSecretKeyCodeIndex = 0;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        mFeedbackMenuItem = menu.findItem(R.id.feedback_menu_item);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mFeedbackMenuItem != null) {
            // We only want to enable the feedback menu item, if there is a valid feedback uri
            mFeedbackMenuItem.setVisible(!Uri.EMPTY.equals(mFeedbackUri));
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // The app icon on the action bar is pressed.  Just emulate a back press.
                // TODO: this should navigate to the main screen, even if a sub-setting is open.
                // But we shouldn't just finish(), as we want to show "discard changes?" dialog
                // when necessary.
                onBackPressed();
                break;
            case R.id.feedback_menu_item:
                Utils.sendFeedback(this, mFeedbackUri, false /* reportingProblem */);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean isValidFragment(String fragmentName) {
        // This activity is not exported, so we can allow any fragment
        return true;
    }

    private void enableDebugMenu() {
        mShowDebugMenu = true;
        invalidateHeaders();
    }

    private void onAddNewAccount() {
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //if(getResources().getBoolean(R.bool.feature_email_account_list_on)){
//        if(PLFUtils.getBoolean(this, "feature_email_account_list_on")){
        //PreDefineAccountProvider this feature is not ok,so set it to false
        // TS: tianyong.zhao 2015-02-13 EMAIL BUGFIX_- 931547 MOD_S
        if (false) {
        // TS: tianyong.zhao 2015-02-13 EMAIL BUGFIX_- 931547 MOD_E
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
            final Intent setupIntent=PreDefineAccountProvider.actionEnterPreDefineAccountProvider(this,PreDefineAccountProvider.ADD_ACCOUNT_SETTING);
            startActivity(setupIntent);
        }
        else
        {
            final Intent setupIntent = AccountSetupFinal.actionNewAccountIntent(this);
            startActivity(setupIntent);
        }
    }

    @Override
    public void onBuildExtraHeaders(List<Header> target) {
        super.onBuildExtraHeaders(target);

        loadHeadersFromResource(R.xml.email_extra_preference_headers, target);

        // if debug header is enabled, show it
        if (DEBUG_MENU_ALLOWED) {
            if (mShowDebugMenu) {
                // setup lightweight header for debugging
                final Header debugHeader = new Header();
                debugHeader.title = getText(R.string.debug_title);
                debugHeader.summary = null;
                debugHeader.iconRes = 0;
                debugHeader.fragment = DebugFragment.class.getCanonicalName();
                debugHeader.fragmentArguments = null;
                target.add(debugHeader);
            }
        }
    }

    /**
     * Called when the user selects an item in the header list.  Handles save-data cases as needed
     *
     * @param header The header that was selected.
     * @param position The header's position in the list.
     */
    @Override
    public void onHeaderClick(@NonNull Header header, int position) {
        // Secret keys:  Click 10x to enable debug settings
        if (position == 0) {
            mNumGeneralHeaderClicked++;
            if (mNumGeneralHeaderClicked == 10) {
                enableDebugMenu();
            }
        } else {
            mNumGeneralHeaderClicked = 0;
        }
        if (header.id == R.id.add_account_header) {
            onAddNewAccount();
            return;
        }

        // Process header click normally
        super.onHeaderClick(header, position);
    }

    @Override
    public void onAttachFragment(Fragment f) {
        super.onAttachFragment(f);
        // When we're changing fragments, enable/disable the add account button
        invalidateOptionsMenu();
        //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
        mCurrentFragment = f;
        //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
    }
    //[FEATURE]-Add-BEGIN by TSNJ Zhenhua.Fan,10/23/2014,PR 728378

    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_S
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCurrentFragment != null && mCurrentFragment instanceof AccountSettingsFragment) {
            ((AccountSettingsFragment) mCurrentFragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //TS: junwei-xu 2016-04-05 EMAIL BUGFIX-1909256 ADD_E
}
