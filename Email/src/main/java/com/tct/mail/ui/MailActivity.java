/*******************************************************************************
 *      Copyright (C) 2012 Google Inc.
 *      Licensed to The Android Open Source Project.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-50002 2014/10/24   zhaotianyong    Modify the package conflict
 *BUGFIX-925761  2015-03-06   wenggangjin      [Android5.0][Email]java.lang.IllegalArgumentException: pointerIndex out of range from daniel
 *FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
 *FEATURE-559893 2015/09/11   tao.gan         [Email]Auto hiding action bar in mail box list
 *TASK-869664    2015/11/25   zheng.zou       [Email]Android M Permission Upgrade
 *BUGFIX-1030520    2015/12/02    zheng.zou     [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created

 ============================================================================
 */

package com.tct.mail.ui;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageButton;

//TS:MOD by zhaotianyong for CONFLICT_50002 START
//import com.android.bitmap.BitmapCache;
//import com.android.bitmap.UnrefedBitmapCache;
import com.tct.fw.bitmap.BitmapCache;
import com.tct.fw.bitmap.UnrefedBitmapCache;
import com.tct.mail.analytics.AnalyticsTimer;
import com.tct.mail.bitmap.ContactResolver;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.utils.StorageLowState;
import com.tct.mail.utils.Utils;
//TS:MOD by zhaotianyong for CONFLICT_50002 END

import com.tct.email.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This is the root activity container that holds the left navigation fragment
 * (usually a list of folders), and the main content fragment (either a
 * conversation list or a conversation view).
 */
public class MailActivity extends AbstractMailActivity implements ControllableActivity {

    /** 339KB cache fits 10 bitmaps at 33856 bytes each. */
    private static final int SENDERS_IMAGES_CACHE_TARGET_SIZE_BYTES = 1024 * 339;
    private static final float SENDERS_IMAGES_PREVIEWS_CACHE_NON_POOLED_FRACTION = 0f;
    /** Each string has upper estimate of 50 bytes, so this cache would be 5KB. */
    private static final int SENDERS_IMAGES_PREVIEWS_CACHE_NULL_CAPACITY = 100;

    /**
     * The activity controller to which we delegate most Activity lifecycle events.
     */
    private ActivityController mController;

    private ViewMode mViewMode;

    private ToastBarOperation mPendingToastOp;
    private boolean mAccessibilityEnabled;
    private AccessibilityManager mAccessibilityManager;

    protected ConversationListHelper mConversationListHelper;

    /**
     * The account name currently in use. Used to construct the NFC mailto: message. This needs
     * to be static since the {@link ComposeActivity} needs to statically change the account name
     * and have the NFC message changed accordingly.
     */
    protected static String sAccountName = null;

    private BitmapCache mSendersImageCache;
    private Toolbar mToolbar;

    /**
     * Create an NFC message (in the NDEF: Nfc Data Exchange Format) to instruct the recepient to
     * send an email to the current account.
     */
    private static class NdefMessageMaker implements NfcAdapter.CreateNdefMessageCallback {
        @Override
        public NdefMessage createNdefMessage(NfcEvent event) {
            if (sAccountName == null) {
                return null;
            }
            return getMailtoNdef(sAccountName);
        }

        /**
         * Returns an NDEF message with a single mailto URI record
         * for the given email address.
         */
        private static NdefMessage getMailtoNdef(String account) {
            byte[] accountBytes;
            try {
                accountBytes = URLEncoder.encode(account, "UTF-8").getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                accountBytes = account.getBytes();
            }
            byte prefix = 0x06; // mailto:
            byte[] recordBytes = new byte[accountBytes.length + 1];
            recordBytes[0] = prefix;
            System.arraycopy(accountBytes, 0, recordBytes, 1, accountBytes.length);
            NdefRecord mailto = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,
                    new byte[0], recordBytes);
            return new NdefMessage(new NdefRecord[] { mailto });
        }
    }

    private final NdefMessageMaker mNdefHandler = new NdefMessageMaker();

    public MailActivity() {
        super();
        mConversationListHelper = new ConversationListHelper();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mController.onTouchEvent(ev);
      //TS: wenggangjin 2015-03-06 EMAIL BUGFIX_-925761 MOD_S
//        return super.dispatchTouchEvent(ev);
        boolean re = false;
        try {
            re = super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
        }
        return re;
      //TS: wenggangjin 2015-03-06 EMAIL BUGFIX_-925761 MOD_E
    }

    /**
     * Default implementation returns a null view mode.
     */
    @Override
    public ViewMode getViewMode() {
        return mViewMode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mController.onActivityResult(requestCode, resultCode, data);
    }

    //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_S
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_S
        if (permissions.length > 0) {
            notifyPermissionResult(requestCode, permissions[0], grantResults[0]);  //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 MOD
        }
        //TS: jian.xu 2015-12-29 EMAIL BUGFIX-1240340 ADD_E
    }
    //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_E

    @Override
    public void onBackPressed() {
        if (!mController.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        // Log the start time if this is launched from the launcher with no saved states
        Intent i = getIntent();
        if (i != null && i.getCategories() != null &&
                i.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
            AnalyticsTimer.getInstance().trackStart(AnalyticsTimer.COLD_START_LAUNCHER);
        }

        resetSenderImageCache();
        mViewMode = new ViewMode();
        final boolean tabletUi = Utils.useTabletUI(this.getResources());
        mController = ControllerFactory.forActivity(this, mViewMode, tabletUi);

        setContentView(mController.getContentViewResource());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mail_toolbar);
        // Toolbar is currently only used on phone layout, so this is expected to be null
        // on tablets
        if (toolbar != null) {
            mToolbar = toolbar;
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(mController.getNavigationViewClickListener());
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hide the app icon.
            actionBar.setIcon(android.R.color.transparent);
            actionBar.setDisplayUseLogoEnabled(false);
        }

        // Must be done after setting up action bar
        mController.onCreate(savedState);

        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_S
        if (mHasNoPermission){
            return;
        }
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_E

        mAccessibilityManager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        mAccessibilityEnabled = mAccessibilityManager.isEnabled();
        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            nfcAdapter.setNdefPushMessageCallback(mNdefHandler, this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mController.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mController.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mController.onRestart();
    }

    /**
     * Constructs and sets the default NFC message. This message instructs the receiver to send
     * email to the account provided as the argument. This message is to be shared with
     * "zero-clicks" using NFC. The message will be available as long as the current activity is in
     * the foreground.
     *
     * @param account The email address to send mail to.
     */
    public static void setNfcMessage(String account) {
        sAccountName = account;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mController.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(int id, Bundle bundle) {
        final Dialog dialog = mController.onCreateDialog(id, bundle);
        return dialog == null ? super.onCreateDialog(id, bundle) : dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mController.onCreateOptionsMenu(menu) || super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mController.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mController.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        mController.onPause();
    }

    @Override
    public void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        super.onPrepareDialog(id, dialog, bundle);
        mController.onPrepareDialog(id, dialog, bundle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mController.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.onResume();
        final boolean enabled = mAccessibilityManager.isEnabled();
        if (enabled != mAccessibilityEnabled) {
            onAccessibilityStateChanged(enabled);
        }
        // App has resumed, re-check the top-level storage situation.
        StorageLowState.checkStorageLowMode(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mController.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.onStart();
    }

    @Override
    public boolean onSearchRequested() {
        mController.startSearch();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 MOD_S
        if (mController!=null){
            mController.onDestroy();
        }
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 MOD_E
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mController.onWindowFocusChanged(hasFocus);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("{ViewMode=");
        sb.append(mViewMode);
        sb.append(" controller=");
        sb.append(mController);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public ConversationListCallbacks getListHandler() {
        return mController;
    }

    @Override
    public FolderChangeListener getFolderChangeListener() {
        return mController;
    }

    @Override
    public FolderSelector getFolderSelector() {
        return mController;
    }

    @Override
    public FolderController getFolderController() {
        return mController;
    }

    @Override
    public ConversationSelectionSet getSelectedSet() {
        return mController.getSelectedSet();
    }

    @Override
    public boolean supportsDrag(DragEvent event, Folder folder) {
        return mController.supportsDrag(event, folder);
    }

    @Override
    public void handleDrop(DragEvent event, Folder folder) {
        mController.handleDrop(event, folder);
    }

    @Override
    public void onUndoAvailable(ToastBarOperation undoOp) {
        mController.onUndoAvailable(undoOp);
    }

    @Override
    public Folder getHierarchyFolder() {
        return mController.getHierarchyFolder();
    }

    @Override
    public ConversationUpdater getConversationUpdater() {
        return mController;
    }

    @Override
    public ErrorListener getErrorListener() {
        return mController;
    }

    @Override
    public void setPendingToastOperation(ToastBarOperation op) {
        mPendingToastOp = op;
    }

    @Override
    public ToastBarOperation getPendingToastOperation() {
        return mPendingToastOp;
    }

    @Override
    public void onAnimationEnd(AnimatedAdapter animatedAdapter) {
        mController.onAnimationEnd(animatedAdapter);
    }

    @Override
    public AccountController getAccountController() {
        return mController;
    }

    @Override
    public RecentFolderController getRecentFolderController() {
        return mController;
    }

    @Override
    public DrawerController getDrawerController() {
        return mController.getDrawerController();
    }

    @Override
    public KeyboardNavigationController getKeyboardNavigationController() {
        return mController;
    }

    @Override
    public void onFooterViewErrorActionClick(Folder folder, int errorStatus) {
        mController.onFooterViewErrorActionClick(folder, errorStatus);
    }

    @Override
    public void onFooterViewLoadMoreClick(Folder folder) {
        mController.onFooterViewLoadMoreClick(folder);
    }

    /** TCT: add for local search*/
    @Override
    public void onFooterViewRemoteSearchClick(Folder folder) {
        mController.onFooterViewRemoteSearchClick(folder);
    }


    @Override
    public void startDragMode() {
        mController.startDragMode();
    }

    @Override
    public void stopDragMode() {
        mController.stopDragMode();
    }

    @Override
    public boolean isAccessibilityEnabled() {
        return mAccessibilityEnabled;
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        mAccessibilityEnabled = enabled;
        mController.onAccessibilityStateChanged();
    }

    @Override
    public final ConversationListHelper getConversationListHelper() {
        return mConversationListHelper;
    }

    @Override
    public FragmentLauncher getFragmentLauncher() {
        return mController;
    }

    @Override
    public ContactLoaderCallbacks getContactLoaderCallbacks() {
        return new ContactLoaderCallbacks(getActivityContext());
    }

    @Override
    public ContactResolver getContactResolver(ContentResolver resolver, BitmapCache bitmapCache) {
        return new ContactResolver(resolver, bitmapCache);
    }

    @Override
    public BitmapCache getSenderImageCache() {
        return mSendersImageCache;
    }

    @Override
    public void resetSenderImageCache() {
        mSendersImageCache = createNewSenderImageCache();
    }

    private BitmapCache createNewSenderImageCache() {
        return new UnrefedBitmapCache(Utils.isLowRamDevice(this) ?
                0 : SENDERS_IMAGES_CACHE_TARGET_SIZE_BYTES,
                SENDERS_IMAGES_PREVIEWS_CACHE_NON_POOLED_FRACTION,
                SENDERS_IMAGES_PREVIEWS_CACHE_NULL_CAPACITY);
    }

    @Override
    public void showHelp(Account account, int viewMode) {
        int helpContext = ViewMode.isConversationMode(viewMode)
                ? R.string.conversation_view_help_context
                : R.string.conversation_list_help_context;
        Utils.showHelp(this, account, getString(helpContext));
    }

    /**
     * Returns the loader callback that can create a
     * {@link AbstractActivityController#LOADER_WELCOME_TOUR_ACCOUNTS} followed by a
     * {@link AbstractActivityController#LOADER_WELCOME_TOUR} which determines whether the welcome
     * tour should be displayed.
     *
     * The base implementation returns {@code null} and subclasses should return an actual
     * implementation if they want to be invoked at appropriate time.
     */
    public LoaderManager.LoaderCallbacks<?> getWelcomeCallbacks() {
        return null;
    }

    /**
     * Returns whether the latest version of the welcome tour was shown on this device.
     * <p>
     * The base implementation returns {@code true} and applications that implement a welcome tour
     * should override this method in order to optimize
     * {@link AbstractActivityController#perhapsStartWelcomeTour()}.
     *
     * @return Whether the latest version of the welcome tour was shown.
     */
    public boolean wasLatestWelcomeTourShownOnDeviceForAllAccounts() {
        return true;
    }

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    @Override
    public void animateShow(ImageButton fabButton) {
        mController.animateShow(fabButton);
    }

    @Override
    public void animateHide(ImageButton fabButton) {
        mController.animateHide(fabButton);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
    @Override
    public ImageButton getComposeButton() {
        return mController.getComposeButton();
    }
  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_E
  //TS: tao.gan 2015-09-12 EMAIL BUGFIX-1080620 ADD_S
    @Override
    public void backToList(Conversation conversation) {
        // TODO Auto-generated method stub
        mController.backToList(conversation);
    }
  //TS: tao.gan 2015-09-12 EMAIL BUGFIX-1080620 ADD_E
}
