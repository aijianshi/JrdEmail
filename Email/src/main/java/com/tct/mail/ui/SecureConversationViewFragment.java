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
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-899762  2015-01-21   wenggangjin     [Email]In the combined view open an e-mail with a star / cancel starred invalid
 *BUGFIX_915771  2015-02-02   gengkexue       [Android5.0][Email]The save icon of attachments will move in combined view
 *BUGFIX_935495  2015-03-03   zheng.zou       [Monitor][Email]Work abnormal when delete and undo in email detail screen
 *BUGFIX-940964  2015/4/20    gangjin.weng    [Email] Set Dwonload Head Only by default
 *BUGFIX-975099  2015/7/16   chaozhang       [Android5.0][Email][Monitor][Force close]Continuous rotate the screen when reading a email result occur Email Force close
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *FEATURE-854258 2015/11/11   Gantao          [Android L][Email]There are no buttons for reply/reply all/forward at the end of mail in combine account
 ===========================================================================
 */
package com.tct.mail.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.tct.email.R;
import com.tct.emailcommon.mail.Address;
import com.tct.mail.browse.MessageScrollView;
import com.tct.mail.utils.Log;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Sets;
import com.tct.fw.google.common.collect.ImmutableList;
import com.tct.fw.google.common.collect.Sets;
import com.tct.mail.browse.ConversationAccountController;
import com.tct.mail.browse.ConversationMessage;
import com.tct.mail.browse.ConversationViewHeader;
import com.tct.mail.browse.MessageCursor;
import com.tct.mail.browse.MessageHeaderView;
import com.tct.mail.browse.MessageWebView;
import com.tct.mail.content.ObjectCursor;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.UIProvider;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.tct.emailcommon.service.SearchParams;
import com.tct.mail.ConversationListContext;

public class SecureConversationViewFragment extends AbstractConversationViewFragment
        implements SecureConversationViewControllerCallbacks {
    private static final String LOG_TAG = LogTag.getLogTag();

    private SecureConversationViewController mViewController;

    private class SecureConversationWebViewClient extends AbstractConversationWebViewClient {
        public SecureConversationWebViewClient(Account account) {
            super(account);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            // try to load the url assuming it is a cid url
            final Uri uri = Uri.parse(url);
            final WebResourceResponse response = loadCIDUri(uri, mViewController.getMessage());
            if (response != null) {
                return response;
            }

            // otherwise, attempt the default handling
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Ignore unsafe calls made after a fragment is detached from an activity.
            // This method needs to, for example, get at the loader manager, which needs
            // the fragment to be added.
            if (!isAdded()) {
                LogUtils.d(LOG_TAG, "ignoring SCVF.onPageFinished, url=%s fragment=%s", url,
                        SecureConversationViewFragment.this);
                return;
            }

            if (isUserVisible()) {
                onConversationSeen();
            }

          //[BUGFIX]-Add-BEGIN by TSCD.zheng.zou,02/03/2015,917703,
            //[Email]The characters display too small in IMAP mail which body has 200 characters
            //note: it's a hack to resolve the abnormal display when the Webview's content is a single line
            //without break character. set MATCH_PARENT in xml and change back to WRAP_CONTENT here.
            if (view!=null && view instanceof MessageWebView){
                int height =  view.getLayoutParams().height;
                if (height == LinearLayout.LayoutParams.MATCH_PARENT){
                    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }
            //[BUGFIX]-Add-END by TSCD.zheng.zou
            mViewController.dismissLoadingStatus();

            final Set<String> emailAddresses = Sets.newHashSet();
            final List<Address> cacheCopy;
            synchronized (mAddressCache) {
                cacheCopy = ImmutableList.copyOf(mAddressCache.values());
            }
            for (Address addr : cacheCopy) {
                emailAddresses.add(addr.getAddress());
            }
            final ContactLoaderCallbacks callbacks = getContactInfoSource();
            callbacks.setSenders(emailAddresses);
            getLoaderManager().restartLoader(CONTACT_LOADER, Bundle.EMPTY, callbacks);

         // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
            //If message scroll view is bottom, hide the fab button, if not show fab button.
            MessageScrollView scrollView = mViewController.getScrollView();
            boolean b = scrollView.isBottom();
            if (scrollView.isBottom()) {
                scrollView.animateShowFooter(true);
            } else {
                scrollView.animateHideFooter(true);
            }
         // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E
        }
    }

    /**
     * Creates a new instance of {@link ConversationViewFragment}, initialized
     * to display a conversation with other parameters inherited/copied from an
     * existing bundle, typically one created using {@link #makeBasicArgs}.
     */
    public static SecureConversationViewFragment newInstance(Bundle existingArgs,
            Conversation conversation) {
        SecureConversationViewFragment f = new SecureConversationViewFragment();
        Bundle args = new Bundle(existingArgs);
        args.putParcelable(ARG_CONVERSATION, conversation);
        f.setArguments(args);
        return f;
    }

    /**
     * Constructor needs to be public to handle orientation changes and activity
     * lifecycle events.
     */
    public SecureConversationViewFragment() {}

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mWebViewClient = new SecureConversationWebViewClient(mAccount);
        mViewController = new SecureConversationViewController(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_S
        return mViewController.onCreateView(inflater, container, savedInstanceState, false);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 MOD_E
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewController.onActivityCreated(savedInstanceState);
    }

    // Start implementations of SecureConversationViewControllerCallbacks
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewController.onSaveInstanceState(outState);
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public AbstractConversationWebViewClient getWebViewClient() {
        return mWebViewClient;
    }

    @Override
    public void setupConversationHeaderView(ConversationViewHeader headerView) {
        headerView.setCallbacks(this, this, getListController());
        headerView.setFolders(mConversation);
        headerView.setSubject(mConversation.subject);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
        headerView.setConversationPriority(mConversation.flagPriority);
        //headerView.setStarred(mConversation.starred);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-E
    }

    @Override
    public boolean isViewVisibleToUser() {
        return isUserVisible();
    }

    @Override
    public ConversationAccountController getConversationAccountController() {
        return this;
    }

    @Override
    public Map<String, Address> getAddressCache() {
        return mAddressCache;
    }

    @Override
    public void setupMessageHeaderVeiledMatcher(MessageHeaderView messageHeaderView) {
        messageHeaderView.setVeiledMatcher(
                ((ControllableActivity) getActivity()).getAccountController()
                        .getVeiledAddressMatcher());
    }

    @Override
    public void startMessageLoader() {
        getLoaderManager().initLoader(MESSAGE_LOADER, null, getMessageLoaderCallbacks());
    }

    @Override
    public String getBaseUri() {
        return mBaseUri;
    }

    @Override
    public boolean isViewOnlyMode() {
        return false;
    }

    // End implementations of SecureConversationViewControllerCallbacks

    @Override
    protected void markUnread() {
        super.markUnread();
        // Ignore unsafe calls made after a fragment is detached from an activity
        final ControllableActivity activity = (ControllableActivity) getActivity();
        final ConversationMessage message = mViewController.getMessage();
        if (activity == null || mConversation == null || message == null) {
            LogUtils.w(LOG_TAG, "ignoring markUnread for conv=%s",
                    mConversation != null ? mConversation.id : 0);
            return;
        }
        final HashSet<Uri> uris = new HashSet<Uri>();
        uris.add(message.uri);
        activity.getConversationUpdater().markConversationMessagesUnread(mConversation, uris,
                mViewState.getConversationInfo());
    }

    @Override
    public void onAccountChanged(Account newAccount, Account oldAccount) {
        renderMessage(getMessageCursor());
    }

    @Override
    public void onConversationViewHeaderHeightChange(int newHeight) {
        // Do nothing.
    }

    @Override
    public void onUserVisibleHintChanged() {
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
      //Read mail one by one,when we come to a new view fragment ,let's try to show toolbar for user.
        final ControllableActivity activity = (ControllableActivity) getActivity();
        if(activity != null) {
            activity.animateShow(mViewController.getFAButton());
        }
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        if (mActivity == null) {
            return;
        }
        if (isUserVisible()) {
            onConversationSeen();
        }
    }

    @Override
    protected void onMessageCursorLoadFinished(Loader<ObjectCursor<ConversationMessage>> loader,
            MessageCursor newCursor, MessageCursor oldCursor) {
        renderMessage(newCursor);
    }

    private void renderMessage(MessageCursor newCursor) {
        // ignore cursors that are still loading results
        if (newCursor == null || !newCursor.isLoaded()) {
            LogUtils.i(LOG_TAG, "CONV RENDER: existing cursor is null, rendering from scratch");
            return;
        }
        if (mActivity == null || mActivity.isFinishing()) {
            // Activity is finishing, just bail.
            return;
        }
        if (!newCursor.moveToFirst()) {
            LogUtils.e(LOG_TAG, "unable to open message cursor");
            return;
        }

        /// TCT: add for highlight search key words.
        ActivityController activityController = (ActivityController) mActivity.getAccountController(
        );
        ConversationListContext listContext = activityController.getCurrentListContext();
        if (listContext != null
                && (SearchParams.SEARCH_FIELD_BODY.equals(listContext.getSearchField())
                || SearchParams.SEARCH_FIELD_ALL.equals(listContext.getSearchField()))) {
            // Modified the format, avoid JE happen
            LogUtils.i(LogUtils.TAG, "renderMessage : isLocalSearch : %s, query: %s, field: %s",
                    listContext.isLocalSearch(), listContext.getSearchQuery(), listContext
                            .getSearchField());
            mViewController.renderMessage(newCursor.getMessage(), listContext.getSearchQuery());
        } else {
            mViewController.renderMessage(newCursor.getMessage());
        }
    }

    @Override
    public void onConversationUpdated(Conversation conv) {
        final ConversationViewHeader headerView = mViewController.getConversationHeaderView();
      //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_899762 MOD_S
        // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_S
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 MOD_S
        if (mConversation.isViewed()) {
            conv.markViewed();
        }
        mConversation = conv;
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 MOD_E
        // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_E
      //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_899762 MOD_E
        if (headerView != null) {
            headerView.onConversationUpdated(conv);
        }
    }

    // Need this stub here
    @Override
    public boolean supportsMessageTransforms() {
        return false;
    }

    /**
     * Users are expected to use the Print item in the Message overflow menu to print the single
     * message.
     *
     * @return {@code false} because Print and Print All menu items are never shown in EMail.
     */
    @Override
    protected boolean shouldShowPrintInOverflow() {
        return false;
    }

    @Override
    protected void printConversation() {
        mViewController.printMessage();
    }
  //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_899762 ADD_S
    @Override
    public void onPause() {
        final ControllableActivity activity = (ControllableActivity) getActivity();
        if (activity != null) {
            //TS: zheng.zou 2015-03-03 EMAIL BUGFIX_935495 MOD_S
            ContentValues values = new ContentValues();
            values.put(UIProvider.ConversationColumns.STARRED, mConversation.starred);
            values.put(UIProvider.ConversationOperations.Parameters.SUPPRESS_UNDO, true);
            activity.getConversationUpdater().updateConversation(Conversation.listOf(mConversation),values);
//            activity.getConversationUpdater().updateConversation(Conversation.listOf(mConversation),
//                           UIProvider.ConversationColumns.STARRED, mConversation.starred);
            //TS: zheng.zou 2015-03-03 EMAIL BUGFIX_935495 MOD_E
        }
        super.onPause();
    }
  //TS: wenggangjin 2015-01-20 EMAIL BUGFIX_899762 ADD_E

    // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_S
    @Override
    public void setStarred(boolean star) {
        // TODO Auto-generated method stub
        mConversation.starred = star;
    }
    // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_E

    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_S
    @Override
    public void setEasCallFlag(boolean flag) {
        setCallFlag(flag);
    }
    // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD_E
    // TS: chaozhang 2015-07-16 EMAIL BUGFIX_975099 ADD_S
    //NOTE: We must manually destory the fragment,if continuously rotate screen,the webView
    // will lead memory and cause OOM.
    @Override
    public void onDestroyView() {
        mViewController.onDestroyView();
        super.onDestroyView();
    }
    // TS: chaozhang 2015-07-16 EMAIL BUGFIX_975099 ADD_E
}
