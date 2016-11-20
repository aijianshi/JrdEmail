/*
 * Copyright (C) 2011 Google Inc.
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
 *  /*

 ==========================================================================
 *HISTORY
 *
 *Tag               Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX_1133819 2015/1/26     yanhua.chen   [E2E][Defect-298935][Email_V5.1.0.1.0154.0]There is no way to Save an URL as a bookmark that was sent from an Email
 *BUGFIX_1536054 2016/2/16     jin.dong      [Email]There is no response when click the hyperlink which kind of the public disk.
 *===========================================================================
 */

package com.tct.mail.browse;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Browser;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.webkit.WebView;

import com.tct.email.R;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.providers.Message;
import com.tct.mail.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * <p>Handles display and behavior of the context menu for known actionable content in WebViews.
 * Requires an Activity to bind to for Context resolution and to start other activites.</p>
 * <br>
 * Dependencies:
 * <ul>
 * <li>res/menu/webview_context_menu.xml</li>
 * </ul>
 */
public class WebViewContextMenu implements OnCreateContextMenuListener,
        MenuItem.OnMenuItemClickListener {

    private final Activity mActivity;
    private final InlineAttachmentViewIntentBuilder mIntentBuilder;

    private final boolean mSupportsDial;
    private final boolean mSupportsSms;

    private Callbacks mCallbacks;

    // Strings used for analytics.
    private static final String CATEGORY_WEB_CONTEXT_MENU = "web_context_menu";
    private static final String ACTION_LONG_PRESS = "long_press";
    private static final String ACTION_CLICK = "menu_clicked";
    //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_S
    private static final String BrowserContract_Bookmarks_URL = "url";
    //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_E

    protected static enum MenuType {
        OPEN_MENU,
        COPY_LINK_MENU,
        SHARE_LINK_MENU,
        SAVE_LINK_MENU,//TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD
        DIAL_MENU,
        SMS_MENU,
        ADD_CONTACT_MENU,
        COPY_PHONE_MENU,
        EMAIL_CONTACT_MENU,
        COPY_MAIL_MENU,
        MAP_MENU,
        COPY_GEO_MENU,
    }

    public interface Callbacks {
        /**
         * Given a URL the user clicks/long-presses on, get the {@link Message} whose body contains
         * that URL.
         *
         * @param url URL of a selected link
         * @return Message containing that URL
         */
        ConversationMessage getMessageForClickedUrl(String url);
    }

    public WebViewContextMenu(Activity host, InlineAttachmentViewIntentBuilder builder) {
        mActivity = host;
        mIntentBuilder = builder;

        // Query the package manager to see if the device
        // has an app that supports ACTION_DIAL or ACTION_SENDTO
        // with the appropriate uri schemes.
        final PackageManager pm = mActivity.getPackageManager();
        mSupportsDial = !pm.queryIntentActivities(
                new Intent(Intent.ACTION_DIAL, Uri.parse(WebView.SCHEME_TEL)),
                PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
        mSupportsSms = !pm.queryIntentActivities(
                new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")),
                PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    public void setCallbacks(Callbacks cb) {
        mCallbacks = cb;
    }

    /**
     * Abstract base class that automates sending an analytics event
     * when the menu item is clicked.
     */
    private abstract class AnalyticsClick implements MenuItem.OnMenuItemClickListener {
        private final String mAnalyticsLabel;

        public AnalyticsClick(String analyticsLabel) {
            mAnalyticsLabel = analyticsLabel;
        }

        @Override
        public final boolean onMenuItemClick(MenuItem item) {
            Analytics.getInstance().sendEvent(
                    CATEGORY_WEB_CONTEXT_MENU, ACTION_CLICK, mAnalyticsLabel, 0);
            return onClick();
        }

        public abstract boolean onClick();
    }

    // For our copy menu items.
    private class Copy extends AnalyticsClick {
        private final CharSequence mText;

        public Copy(CharSequence text, String analyticsLabel) {
            super(analyticsLabel);
            mText = text;
        }

        @Override
        public boolean onClick() {
            ClipboardManager clipboard =
                    (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, mText));
            return true;
        }
    }

    /**
     * Sends an intent and reports the analytics event.
     */
    private class SendIntent extends AnalyticsClick {
        private Intent mIntent;
        //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_S
        private String mLabel;
        //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_E
        public SendIntent(String analyticsLabel) {
            super(analyticsLabel);
        }

        public SendIntent(Intent intent, String analyticsLabel) {
            super(analyticsLabel);
            setIntent(intent);
            //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_S
            mLabel = analyticsLabel;
            //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_E
        }

        void setIntent(Intent intent) {
            mIntent = intent;
        }

        @Override
        public final boolean onClick() {
            try {
                mActivity.startActivity(mIntent);
            } catch(android.content.ActivityNotFoundException ex) {
                // if no app handles it, do nothing
                //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_S
                //NOTE: Now we only use chrome to do open_link operations.
                if (TextUtils.equals(mLabel, "open_link")) {
                    createChromeIntent(mIntent);
                    try {
                        mActivity.startActivity(mIntent);
                    }catch(android.content.ActivityNotFoundException e){
                        LogUtils.e("Email","What? still no browser app? Badly, can't do nothing!!!");
                    }
                }
                //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_E
            }
            return true;
        }
    }
    //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_S
    /**
     * Whatever, chrome app is per-installed,so open chrome to open link when browser can't found..
     * @param intent
     * @return
     */
    private void createChromeIntent(Intent intent) {
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, mActivity.getPackageName());
        intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClassName("com.android.chrome", "com.google.android.apps.chrome.Main");
    }
    //TS: jin.dong 2016-2-16 EMAIL BUGFIX_1536054 ADD_E
    // For our share menu items.
    private class Share extends SendIntent {
        public Share(String url, String analyticsLabel) {
            super(analyticsLabel);
            final Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_TEXT, url);
            setIntent(Intent.createChooser(send, mActivity.getText(
                    getChooserTitleStringResIdForMenuType(MenuType.SHARE_LINK_MENU))));
        }
    }

    private boolean showShareLinkMenuItem() {
        PackageManager pm = mActivity.getPackageManager();
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        ResolveInfo ri = pm.resolveActivity(send, PackageManager.MATCH_DEFAULT_ONLY);
        return ri != null;
    }

    //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_S
    // For our save menu items.
    private class Save extends SendIntent {
        public Save(String url, String analyticsLabel){
            super(analyticsLabel);
            final Intent send = new Intent(Intent.ACTION_INSERT);
            send.setType("vnd.android.cursor.dir/bookmark");
            send.putExtra(BrowserContract_Bookmarks_URL, url);
            setIntent(send);
        }
    }

    private boolean showSaveLinkMenuItem() {
        PackageManager pm = mActivity.getPackageManager();
        final Intent send = new Intent(Intent.ACTION_INSERT);
        send.setType("vnd.android.cursor.dir/bookmark");
        ResolveInfo ri = pm.resolveActivity(send, PackageManager.MATCH_DEFAULT_ONLY);
        return ri != null;
    }
    //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_E

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
        // FIXME: This is copied over almost directly from BrowserActivity.
        // Would like to find a way to combine the two (Bug 1251210).

        WebView webview = (WebView) v;
        WebView.HitTestResult result = webview.getHitTestResult();
        if (result == null) {
            return;
        }

        int type = result.getType();
        switch (type) {
            case WebView.HitTestResult.UNKNOWN_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "unknown", 0);
                return;
            case WebView.HitTestResult.EDIT_TEXT_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "edit_text", 0);
                return;
            default:
                break;
        }

        // Note, http://b/issue?id=1106666 is requesting that
        // an inflated menu can be used again. This is not available
        // yet, so inflate each time (yuk!)
        MenuInflater inflater = mActivity.getMenuInflater();
        // Also, we are copying the menu file from browser until
        // 1251210 is fixed.
        inflater.inflate(getMenuResourceId(), menu);

        // Initially make set the menu item handler this WebViewContextMenu, which will default to
        // calling the non-abstract subclass's implementation.
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem menuItem = menu.getItem(i);
            menuItem.setOnMenuItemClickListener(this);
        }


        // Show the correct menu group
        String extra = result.getExtra();
        menu.setGroupVisible(R.id.PHONE_MENU, type == WebView.HitTestResult.PHONE_TYPE);
        menu.setGroupVisible(R.id.EMAIL_MENU, type == WebView.HitTestResult.EMAIL_TYPE);
        menu.setGroupVisible(R.id.GEO_MENU, type == WebView.HitTestResult.GEO_TYPE);
        menu.setGroupVisible(R.id.ANCHOR_MENU, type == WebView.HitTestResult.SRC_ANCHOR_TYPE
                || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE);
        menu.setGroupVisible(R.id.IMAGE_MENU, type == WebView.HitTestResult.IMAGE_TYPE
                || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE);

        // Setup custom handling depending on the type
        switch (type) {
            case WebView.HitTestResult.PHONE_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "phone", 0);
                String decodedPhoneExtra;
                try {
                    decodedPhoneExtra = URLDecoder.decode(extra, Charset.defaultCharset().name());

                    // International numbers start with '+' followed by the country code, etc.
                    // However, during decode, the initial '+' is changed into ' '.
                    // Let's special case that here to avoid losing that information. If the decoded
                    // string starts with one space, let's replace that space with + since it's
                    // impossible for the normal number string to start with a space.
                    // b/10640197
                    if (decodedPhoneExtra.startsWith(" ") && !decodedPhoneExtra.startsWith("  ")) {
                        decodedPhoneExtra = decodedPhoneExtra.replaceFirst(" ", "+");
                    }
                } catch (UnsupportedEncodingException ignore) {
                    // Should never happen; default charset is UTF-8
                    decodedPhoneExtra = extra;
                }

                menu.setHeaderTitle(decodedPhoneExtra);
                // Dial
                final MenuItem dialMenuItem =
                        menu.findItem(getMenuResIdForMenuType(MenuType.DIAL_MENU));

                if (mSupportsDial) {
                    final Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(WebView.SCHEME_TEL + extra));
                    dialMenuItem.setOnMenuItemClickListener(new SendIntent(intent, "dial"));
                } else {
                    dialMenuItem.setVisible(false);
                }

                // Send SMS
                final MenuItem sendSmsMenuItem =
                        menu.findItem(getMenuResIdForMenuType(MenuType.SMS_MENU));
                if (mSupportsSms) {
                    final Intent intent =
                            new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + extra));
                    sendSmsMenuItem.setOnMenuItemClickListener(new SendIntent(intent, "sms"));
                } else {
                    sendSmsMenuItem.setVisible(false);
                }

                // Add to contacts
                final Intent addIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                addIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

                addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, decodedPhoneExtra);
                final MenuItem addToContactsMenuItem =
                        menu.findItem(getMenuResIdForMenuType(MenuType.ADD_CONTACT_MENU));
                addToContactsMenuItem.setOnMenuItemClickListener(
                        new SendIntent(addIntent, "add_contact"));

                // Copy
                menu.findItem(getMenuResIdForMenuType(MenuType.COPY_PHONE_MENU)).
                        setOnMenuItemClickListener(new Copy(extra, "copy_phone"));
                break;
            case WebView.HitTestResult.EMAIL_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "email", 0);
                menu.setHeaderTitle(extra);
                final Intent mailtoIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_MAILTO + extra));
                menu.findItem(getMenuResIdForMenuType(MenuType.EMAIL_CONTACT_MENU))
                        .setOnMenuItemClickListener(new SendIntent(mailtoIntent, "send_email"));
                menu.findItem(getMenuResIdForMenuType(MenuType.COPY_MAIL_MENU)).
                        setOnMenuItemClickListener(new Copy(extra, "copy_email"));
                break;
            case WebView.HitTestResult.GEO_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "geo", 0);
                menu.setHeaderTitle(extra);
                String geoExtra = "";
                try {
                    geoExtra = URLEncoder.encode(extra, Charset.defaultCharset().name());
                } catch (UnsupportedEncodingException ignore) {
                    // Should never happen; default charset is UTF-8
                }
                final MenuItem viewMapMenuItem =
                        menu.findItem(getMenuResIdForMenuType(MenuType.MAP_MENU));

                final Intent viewMap =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_GEO + geoExtra));
                viewMapMenuItem.setOnMenuItemClickListener(new SendIntent(viewMap, "view_map"));
                menu.findItem(getMenuResIdForMenuType(MenuType.COPY_GEO_MENU)).
                        setOnMenuItemClickListener(new Copy(extra, "copy_geo"));
                break;
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "src_anchor", 0);
                setupAnchorMenu(extra, menu);
                break;
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "src_image_anchor", 0);
                setupAnchorMenu(extra, menu);
                setupImageMenu(extra, menu);
                break;
            case WebView.HitTestResult.IMAGE_TYPE:
                Analytics.getInstance().sendEvent(
                        CATEGORY_WEB_CONTEXT_MENU, ACTION_LONG_PRESS, "image", 0);
                setupImageMenu(extra, menu);
                break;
            default:
                break;
        }
    }

    private void setupAnchorMenu(String extra, ContextMenu menu) {
        menu.findItem(getMenuResIdForMenuType(MenuType.SHARE_LINK_MENU)).setVisible(
                showShareLinkMenuItem());
        //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_S
        menu.findItem(getMenuResIdForMenuType(MenuType.SAVE_LINK_MENU)).setVisible(
                showSaveLinkMenuItem());
        //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_E

        // The documentation for WebView indicates that if the HitTestResult is
        // SRC_ANCHOR_TYPE or the url would be specified in the extra.  We don't need to
        // call requestFocusNodeHref().  If we wanted to handle UNKNOWN HitTestResults, we
        // would.  With this knowledge, we can just set the title
        menu.setHeaderTitle(extra);

        menu.findItem(getMenuResIdForMenuType(MenuType.COPY_LINK_MENU)).
                setOnMenuItemClickListener(new Copy(extra, "copy_link"));

        final MenuItem openLinkMenuItem =
                menu.findItem(getMenuResIdForMenuType(MenuType.OPEN_MENU));

        openLinkMenuItem.setOnMenuItemClickListener(
                new SendIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(extra)), "open_link"));

        menu.findItem(getMenuResIdForMenuType(MenuType.SHARE_LINK_MENU)).
                setOnMenuItemClickListener(new Share(extra, "share_link"));

        //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_S
        menu.findItem(getMenuResIdForMenuType(MenuType.SAVE_LINK_MENU)).
                setOnMenuItemClickListener(new Save(extra, "save_link"));
        //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_E
    }

    /**
     * Used to setup the image menu group if the {@link android.webkit.WebView.HitTestResult}
     * is of type {@link android.webkit.WebView.HitTestResult#IMAGE_TYPE} or
     * {@link android.webkit.WebView.HitTestResult#SRC_IMAGE_ANCHOR_TYPE}.
     * @param url Url that was long pressed.
     * @param menu The {@link android.view.ContextMenu} that is about to be shown.
     */
    private void setupImageMenu(String url, ContextMenu menu) {
        final ConversationMessage msg =
                (mCallbacks != null) ? mCallbacks.getMessageForClickedUrl(url) : null;
        if (msg == null) {
            menu.setGroupVisible(R.id.IMAGE_MENU, false);
            return;
        }

        final Intent intent = mIntentBuilder.createInlineAttachmentViewIntent(mActivity, url, msg);
        if (intent == null) {
            menu.setGroupVisible(R.id.IMAGE_MENU, false);
            return;
        }

        final MenuItem menuItem = menu.findItem(R.id.view_image_context_menu_id);
        menuItem.setOnMenuItemClickListener(new SendIntent(intent, "view_image"));

        menu.setGroupVisible(R.id.IMAGE_MENU, true);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onMenuItemSelected(item);
    }

    /**
     * Returns the menu resource id for the specified menu type
     * @param menuType type of the specified menu
     * @return menu resource id
     */
    protected int getMenuResIdForMenuType(MenuType menuType) {
        switch(menuType) {
            case OPEN_MENU:
                return R.id.open_context_menu_id;
            case COPY_LINK_MENU:
                return R.id.copy_link_context_menu_id;
            case SHARE_LINK_MENU:
                return R.id.share_link_context_menu_id;
            //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_S
            case SAVE_LINK_MENU:
                return R.id.save_link_context_menu_id;
            //TS: yanhua.chen 2015-1-26 EMAIL BUGFIX_1133819 ADD_E
            case DIAL_MENU:
                return R.id.dial_context_menu_id;
            case SMS_MENU:
                return R.id.sms_context_menu_id;
            case ADD_CONTACT_MENU:
                return R.id.add_contact_context_menu_id;
            case COPY_PHONE_MENU:
                return R.id.copy_phone_context_menu_id;
            case EMAIL_CONTACT_MENU:
                return R.id.email_context_menu_id;
            case COPY_MAIL_MENU:
                return R.id.copy_mail_context_menu_id;
            case MAP_MENU:
                return R.id.map_context_menu_id;
            case COPY_GEO_MENU:
                return R.id.copy_geo_context_menu_id;
            default:
                throw new IllegalStateException("Unexpected MenuType");
        }
    }

    /**
     * Returns the resource id of the string to be used when showing a chooser for a menu
     * @param menuType type of the specified menu
     * @return string resource id
     */
    protected int getChooserTitleStringResIdForMenuType(MenuType menuType) {
        switch(menuType) {
            case SHARE_LINK_MENU:
                return R.string.choosertitle_sharevia;
            default:
                throw new IllegalStateException("Unexpected MenuType");
        }
    }

    /**
     * Returns the resource id for the web view context menu
     */
    protected int getMenuResourceId() {
        return R.menu.webview_context_menu;
    }


    /**
     * Called when a menu item is not handled by the context menu.
     */
    protected boolean onMenuItemSelected(MenuItem menuItem) {
        return mActivity.onOptionsItemSelected(menuItem);
    }
}
