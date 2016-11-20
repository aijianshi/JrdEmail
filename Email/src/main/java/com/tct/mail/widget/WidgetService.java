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
 *
 ==========================================================================
 *HISTORY
 *
 *Tag            Date          Author         Description
 *============== ============ =============== ==============================
 *BUGFIX-850975  2014/12/23   junwei-xu       [Android5.0][Email][UE]There is no account name in Email widget
 *BUGFIX-940428  2015/03/06   zheng.zou       [Scenario Test][Email]Click Email starred invalid
 *BUGFIX-959920  2015/03/27   jian.xu         [Monitor][FC][Email]Email FC during using
 *BUGFIX-958947  2015/04/01   gangjin.weng    [Moniotr][Email]FC when add email widget to homescree
 *BUGFIX-974972  2015/4/16    yanhua.chen     [Email]Email unread number could not sync when the widget folder is empty
 *BUGFIX-984619  2015/4/24    jian.xu         [FC][Email][Gmail]Gmail FC when compose email from conbined view email widget
 *BUGFIX-1044483  2015/7/22   zheng.zou       [SW][Email]Add the email widget on idle, it display loading always
 *BUGFIX-1044483  2015/8/11   zheng.zou       [SW][Email]Add the email widget on idle, it display loading always
 *BUGFIX-1059178  2015/12/09  zheng.zou       [Email]"Save draft" is not gray in menu when compose email from widget.
 *===========================================================================
 */
package com.tct.mail.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.TaskStackBuilder;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.browse.ConversationItemView;
import com.tct.mail.browse.SendersView;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.preferences.MailPrefs;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.ConversationListQueryParameters;
import com.tct.mail.providers.UIProvider.FolderType;
import com.tct.mail.utils.AccountUtils;
import com.tct.mail.utils.DelayedTaskHandler;
import com.tct.mail.utils.FolderUri;
import com.tct.mail.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WidgetService extends RemoteViewsService {
    /**
     * Lock to avoid race condition between widgets.
     */
    private static final Object sWidgetLock = new Object();

    private static final String LOG_TAG = LogTag.getLogTag();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MailFactory(getApplicationContext(), intent, this);
    }

    protected void configureValidAccountWidget(Context context, RemoteViews remoteViews,
            int appWidgetId, Account account, final int folderType, final int folderCapabilities,
            final Uri folderUri, final Uri folderConversationListUri, String folderName) {
        configureValidAccountWidget(context, remoteViews, appWidgetId, account, folderType,
                folderCapabilities, folderUri, folderConversationListUri, folderName,
                WidgetService.class);
    }

    /**
     * Modifies the remoteView for the given account and folder.
     */
    public static void configureValidAccountWidget(Context context, RemoteViews remoteViews,
            int appWidgetId, Account account, final int folderType, final int folderCapabilities,
            final Uri folderUri, final Uri folderConversationListUri, String folderDisplayName,
            Class<?> widgetService) {
        remoteViews.setViewVisibility(R.id.widget_folder, View.VISIBLE);

        // If the folder or account name are empty, we don't want to overwrite the valid data that
        // had been saved previously.  Since the launcher will save the state of the remote views
        // we should rely on the fact that valid data has been saved.  But we should still log this,
        // as it shouldn't happen
        if (TextUtils.isEmpty(folderDisplayName) || TextUtils.isEmpty(account.getDisplayName())) {
            LogUtils.e(LOG_TAG, new Error(),
                    "Empty folder or account name.  account: %s, folder: %s",
                    account.getEmailAddress(), folderDisplayName);
        }
        if (!TextUtils.isEmpty(folderDisplayName)) {
            remoteViews.setTextViewText(R.id.widget_folder, folderDisplayName);
        }

        //TS: junwei-xu 2014-12-23 EMAIL BUGFIX_850975 ADD_S
        remoteViews.setViewVisibility(R.id.widget_account_noflip, View.VISIBLE);

        if (!TextUtils.isEmpty(account.getEmailAddress())) {
            remoteViews.setTextViewText(R.id.widget_account_noflip, account.getEmailAddress());
            remoteViews.setTextViewText(R.id.widget_account, account.getEmailAddress());
        }
        remoteViews.setViewVisibility(R.id.widget_account_unread_flipper, View.GONE);
        //TS: junwei-xu 2014-12-23 EMAIL BUGFIX_850975 ADD_E

        remoteViews.setViewVisibility(R.id.widget_compose, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.conversation_list, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.empty_conversation_list, View.GONE);  //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 MOD
        remoteViews.setViewVisibility(R.id.widget_folder_not_synced, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_configuration, View.GONE);
//        remoteViews.setEmptyView(R.id.conversation_list, R.id.empty_conversation_list);    //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 DEL

        WidgetService.configureValidWidgetIntents(context, remoteViews, appWidgetId, account,
                folderType, folderCapabilities, folderUri, folderConversationListUri,
                folderDisplayName, widgetService);
    }

    public static void configureValidWidgetIntents(Context context, RemoteViews remoteViews,
            int appWidgetId, Account account, final int folderType, final int folderCapabilities,
            final Uri folderUri, final Uri folderConversationListUri,
            final String folderDisplayName, Class<?> serviceClass) {
        remoteViews.setViewVisibility(R.id.widget_configuration, View.GONE);


        // Launch an intent to avoid ANRs
        final Intent intent = new Intent(context, serviceClass);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(Utils.EXTRA_ACCOUNT, account.serialize());
        intent.putExtra(BaseWidgetProvider.EXTRA_FOLDER_TYPE, folderType);
        intent.putExtra(BaseWidgetProvider.EXTRA_FOLDER_CAPABILITIES, folderCapabilities);
        intent.putExtra(BaseWidgetProvider.EXTRA_FOLDER_URI, folderUri);
        intent.putExtra(BaseWidgetProvider.EXTRA_FOLDER_CONVERSATION_LIST_URI,
                folderConversationListUri);
        intent.putExtra(BaseWidgetProvider.EXTRA_FOLDER_DISPLAY_NAME, folderDisplayName);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.conversation_list, intent);
        remoteViews.setEmptyView(R.id.conversation_list, R.id.empty_conversation_list);  //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 ADD
        // Open mail app when click on header
        final Intent mailIntent = Utils.createViewFolderIntent(context, folderUri, account);
        PendingIntent clickIntent = PendingIntent.getActivity(context, 0, mailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_header, clickIntent);

        // On click intent for Compose
        final Intent composeIntent = new Intent();
        composeIntent.setAction(Intent.ACTION_SEND);
        //TS: jian.xu 2015-4-24 EMAIL BUGFIX_984619 ADD_S
        composeIntent.setPackage(context.getPackageName());
        //TS: jian.xu 2015-4-24 EMAIL BUGFIX_984619 ADD_E
        composeIntent.putExtra(Utils.EXTRA_ACCOUNT, account.serialize());
        composeIntent.setData(account.composeIntentUri);
        composeIntent.putExtra(ComposeActivity.EXTRA_FROM_EMAIL_TASK, true);
        composeIntent.putExtra(ComposeActivity.EXTRA_FROM_EMAIL_WIDGET, true);  //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1059178 ADD
        if (account.composeIntentUri != null) {
            composeIntent.putExtra(Utils.EXTRA_COMPOSE_URI, account.composeIntentUri);
        }

        // Build a task stack that forces the conversation list on the stack before the compose
        // activity.
        final TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        clickIntent = taskStackBuilder.addNextIntent(mailIntent)
                .addNextIntent(composeIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_compose, clickIntent);

        // On click intent for Conversation
        final Intent conversationIntent = new Intent();
        conversationIntent.setAction(Intent.ACTION_VIEW);
        clickIntent = PendingIntent.getActivity(context, 0, conversationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.conversation_list, clickIntent);
    }

    /**
     * Persists the information about the specified widget.
     */
    public static void saveWidgetInformation(Context context, int appWidgetId, Account account,
                final String folderUri) {
        MailPrefs.get(context).configureWidget(appWidgetId, account, folderUri);
    }

    /**
     * Returns true if this widget id has been configured and saved.
     */
    public boolean isWidgetConfigured(Context context, int appWidgetId, Account account) {
        return isAccountValid(context, account) &&
                MailPrefs.get(context).isWidgetConfigured(appWidgetId);
    }

    protected boolean isAccountValid(Context context, Account account) {
        if (account != null) {
            Account[] accounts = AccountUtils.getSyncingAccounts(context);
            for (Account existing : accounts) {
                if (existing != null && account.uri.equals(existing.uri)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remote Views Factory for Mail Widget.
     */
    protected static class MailFactory
            implements RemoteViewsService.RemoteViewsFactory, OnLoadCompleteListener<Cursor> {
        private static final int MAX_CONVERSATIONS_COUNT = 25;
        private static final int MAX_SENDERS_LENGTH = 25;

        private static final int FOLDER_LOADER_ID = 0;
        private static final int CONVERSATION_CURSOR_LOADER_ID = 1;
        private static final int ACCOUNT_LOADER_ID = 2;

        private final Context mContext;
        private final int mAppWidgetId;
        private final Account mAccount;
        private final int mFolderType;
        private final int mFolderCapabilities;
        private final Uri mFolderUri;
        private final Uri mFolderConversationListUri;
        private final String mFolderDisplayName;
        private final WidgetConversationListItemViewBuilder mWidgetConversationListItemViewBuilder;
        private CursorLoader mConversationCursorLoader;
        private Cursor mConversationCursor;
        private CursorLoader mFolderLoader;
        private CursorLoader mAccountLoader;
        private FolderUpdateHandler mFolderUpdateHandler;
        private int mFolderCount;
        private boolean mShouldShowViewMore;
        private boolean mFolderInformationShown = false;
        private final WidgetService mService;
        private String mSendersSplitToken;
        private String mElidedPaddingToken;
        private int mUnreadCount;  // gangjin.weng 2015-04-01 EMAIL BUGFIX_958947 ADD

        public MailFactory(Context context, Intent intent, WidgetService service) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            mAccount = Account.newInstance(intent.getStringExtra(Utils.EXTRA_ACCOUNT));
            mFolderType = intent.getIntExtra(WidgetProvider.EXTRA_FOLDER_TYPE, FolderType.DEFAULT);
            mFolderCapabilities = intent.getIntExtra(WidgetProvider.EXTRA_FOLDER_CAPABILITIES, 0);
            mFolderDisplayName = intent.getStringExtra(WidgetProvider.EXTRA_FOLDER_DISPLAY_NAME);

            final Uri folderUri = intent.getParcelableExtra(WidgetProvider.EXTRA_FOLDER_URI);
            final Uri folderConversationListUri =
                    intent.getParcelableExtra(WidgetProvider.EXTRA_FOLDER_CONVERSATION_LIST_URI);
            if (folderUri != null && folderConversationListUri != null) {
                mFolderUri = folderUri;
                mFolderConversationListUri = folderConversationListUri;
            } else {
                // This is a old intent created in version UR8 (or earlier).
                String folderString = intent.getStringExtra(Utils.EXTRA_FOLDER);
                //noinspection deprecation
                Folder folder = Folder.fromString(folderString);
                if (folder != null) {
                    mFolderUri = folder.folderUri.fullUri;
                    mFolderConversationListUri = folder.conversationListUri;
                } else {
                    mFolderUri = Uri.EMPTY;
                    mFolderConversationListUri = Uri.EMPTY;
                    // this will mark the widget as unconfigured
                    BaseWidgetProvider.updateWidget(mContext, mAppWidgetId, mAccount, mFolderType,
                            mFolderCapabilities, mFolderUri, mFolderConversationListUri,
                            mFolderDisplayName);
                }
            }

            mWidgetConversationListItemViewBuilder = new WidgetConversationListItemViewBuilder(
                    context);
            mService = service;
        }

        @Override
        public void onCreate() {
            // Save the map between widgetId and account to preference
            saveWidgetInformation(mContext, mAppWidgetId, mAccount, mFolderUri.toString());

            // If the account of this widget has been removed, we want to update the widget to
            // "Tap to configure" mode.
            if (!mService.isWidgetConfigured(mContext, mAppWidgetId, mAccount)) {
                BaseWidgetProvider.updateWidget(mContext, mAppWidgetId, mAccount, mFolderType,
                        mFolderCapabilities, mFolderUri, mFolderConversationListUri,
                        mFolderDisplayName);
            }
            LogUtils.i(LOG_TAG, "widgetservice MailFactory onCreate()");     //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 ADD
            mFolderInformationShown = false;

            // We want to limit the query result to 25 and don't want these queries to cause network
            // traffic
            // We also want this cursor to receive notifications on all changes.  Any change that
            // the user made locally, the default policy of the UI provider is to not send
            // notifications for.  But in this case, since the widget is not using the
            // ConversationCursor instance that the UI is using, the widget would not be updated.
            final Uri.Builder builder = mFolderConversationListUri.buildUpon();
            final String maxConversations = Integer.toString(MAX_CONVERSATIONS_COUNT);
            final Uri widgetConversationQueryUri = builder
                    .appendQueryParameter(ConversationListQueryParameters.LIMIT, maxConversations)
                    .appendQueryParameter(ConversationListQueryParameters.USE_NETWORK,
                            Boolean.FALSE.toString())
                    .appendQueryParameter(ConversationListQueryParameters.ALL_NOTIFICATIONS,
                            Boolean.TRUE.toString()).build();

            final Resources res = mContext.getResources();
            mConversationCursorLoader = new CursorLoader(mContext, widgetConversationQueryUri,
                    UIProvider.CONVERSATION_PROJECTION, null, null, null);
            LogUtils.w(LOG_TAG, "WidgetService                     onCreate           mConversationCursorLoader.registerListener");
            mConversationCursorLoader.registerListener(CONVERSATION_CURSOR_LOADER_ID, this);
            mConversationCursorLoader.setUpdateThrottle(
                    res.getInteger(R.integer.widget_refresh_delay_ms));
            mConversationCursorLoader.startLoading();
            mSendersSplitToken = res.getString(R.string.senders_split_token);
            mElidedPaddingToken = res.getString(R.string.elided_padding_token);
            mFolderLoader = new CursorLoader(mContext, mFolderUri, UIProvider.FOLDERS_PROJECTION,
                    null, null, null);
            mFolderLoader.registerListener(FOLDER_LOADER_ID, this);
            mFolderUpdateHandler = new FolderUpdateHandler(
                    res.getInteger(R.integer.widget_folder_refresh_delay_ms));
            mFolderUpdateHandler.scheduleTask();

            mAccountLoader = new CursorLoader(mContext, mAccount.uri,
                    UIProvider.ACCOUNTS_PROJECTION_NO_CAPABILITIES, null, null, null);
            mAccountLoader.registerListener(ACCOUNT_LOADER_ID, this);
            mAccountLoader.startLoading();
        }

        @Override
        public void onDestroy() {
            LogUtils.w(LOG_TAG, "WidgetService                     onDestroy           start");
            synchronized (sWidgetLock) {
                if (mConversationCursorLoader != null) {
                    mConversationCursorLoader.reset();
                    LogUtils.w(LOG_TAG, "WidgetService                     onDestroy           mConversationCursorLoader.unregisterListener(this);");
                    mConversationCursorLoader.unregisterListener(this);
                    mConversationCursorLoader = null;
                }

                // The Loader should close the cursor, so just unset the reference
                // to it here.
                mConversationCursor = null;
            }

            if (mFolderLoader != null) {
                mFolderLoader.reset();
                mFolderLoader.unregisterListener(this);
                mFolderLoader = null;
            }

            if (mAccountLoader != null) {
                mAccountLoader.reset();
                mAccountLoader.unregisterListener(this);
                mAccountLoader = null;
            }
        }

        @Override
        public void onDataSetChanged() {
            LogUtils.w(LOG_TAG, "widgetservice             onDataSetChanged           start");
            // We are not using this as signal to requery the cursor.  The query will be started
            // in the following ways:
            // 1) The Service is started and the loader is started in onCreate()
            //       This will happen when the service is not running, and
            //       AppWidgetManager#notifyAppWidgetViewDataChanged() is called
            // 2) The service is running, with a previously created loader.  The loader is watching
            //    for updates from the existing cursor.  If one is seen, the loader will load a new
            //    cursor in the background.
            mFolderUpdateHandler.scheduleTask();
            LogUtils.w(LOG_TAG, "widgetservice             onDataSetChanged           end");
        }

        /**
         * Returns the number of items should be shown in the widget list.  This method also updates
         * the boolean that indicates whether the "show more" item should be shown.
         * @return the number of items to be displayed in the list.
         */
        @Override
        public int getCount() {
            synchronized (sWidgetLock) {
                final int count = getConversationCount();
                final int cursorCount = mConversationCursor != null ?
                        mConversationCursor.getCount() : 0;
                mShouldShowViewMore = count < cursorCount || count < mFolderCount;
                return count + (mShouldShowViewMore ? 1 : 0);
            }
        }

        /**
         * Returns the number of conversations that should be shown in the widget.  This method
         * doesn't update the boolean that indicates that the "show more" item should be included
         * in the list.
         * @return count
         */
        private int getConversationCount() {
            synchronized (sWidgetLock) {
                final int cursorCount = mConversationCursor != null ?
                        mConversationCursor.getCount() : 0;
                return Math.min(cursorCount, MAX_CONVERSATIONS_COUNT);
            }
        }

        /**
         * @return the {@link RemoteViews} for a specific position in the list.
         */
        @Override
        public RemoteViews getViewAt(int position) {
            synchronized (sWidgetLock) {
                // "View more conversations" view.
                if (mConversationCursor == null || mConversationCursor.isClosed()
                        || (mShouldShowViewMore && position >= getConversationCount())) {
                    return getViewMoreConversationsView();
                }

                if (!mConversationCursor.moveToPosition(position)) {
                    // If we ever fail to move to a position, return the
                    // "View More conversations"
                    // view.
                    LogUtils.e(LOG_TAG, "Failed to move to position %d in the cursor.", position);
                    return getViewMoreConversationsView();
                }

                Conversation conversation = new Conversation(mConversationCursor);
                // Split the senders and status from the instructions.

                ArrayList<SpannableString> senders = new ArrayList<SpannableString>();
                SendersView.format(mContext, conversation.conversationInfo, "",
                        MAX_SENDERS_LENGTH, senders, null, null, mAccount.getEmailAddress(),
                        Folder.shouldShowRecipients(mFolderCapabilities), true);
                final SpannableStringBuilder senderBuilder = elideParticipants(senders);

                // Get styled date.
                CharSequence date = DateUtils.getRelativeTimeSpanString(mContext,
                        conversation.dateMs);

                final int ignoreFolderType;
                if ((mFolderType & FolderType.INBOX) != 0) {
                    ignoreFolderType = FolderType.INBOX;
                } else {
                    ignoreFolderType = -1;
                }

                // Load up our remote view.
                RemoteViews remoteViews = mWidgetConversationListItemViewBuilder.getStyledView(
                        mContext, date, conversation, new FolderUri(mFolderUri), ignoreFolderType,
                        senderBuilder,
                        ConversationItemView.filterTag(mContext, conversation.subject));

                // On click intent.
                remoteViews.setOnClickFillInIntent(R.id.widget_conversation_list_item,
                        Utils.createViewConversationIntent(mContext, conversation, mFolderUri,
                                mAccount));

                return remoteViews;
            }
        }

        private SpannableStringBuilder elideParticipants(List<SpannableString> parts) {
            final SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString prevSender = null;

            boolean skipToHeader = false;

            // start with "To: " if we're showing recipients
            if (Folder.shouldShowRecipients(mFolderCapabilities)) {
                builder.append(SendersView.getFormattedToHeader());
                skipToHeader = true;
            }

            for (SpannableString sender : parts) {
                if (sender == null) {
                    LogUtils.e(LOG_TAG, "null sender while iterating over styledSenders");
                    continue;
                }
                CharacterStyle[] spans = sender.getSpans(0, sender.length(), CharacterStyle.class);
                if (SendersView.sElidedString.equals(sender.toString())) {
                    prevSender = sender;
                    sender = copyStyles(spans, mElidedPaddingToken + sender + mElidedPaddingToken);
                } else if (!skipToHeader && builder.length() > 0
                        && (prevSender == null || !SendersView.sElidedString.equals(prevSender
                                .toString()))) {
                    prevSender = sender;
                    sender = copyStyles(spans, mSendersSplitToken + sender);
                } else {
                    prevSender = sender;
                    skipToHeader = false;
                }
                builder.append(sender);
            }
            return builder;
        }

        private static SpannableString copyStyles(CharacterStyle[] spans, CharSequence newText) {
            SpannableString s = new SpannableString(newText);
            if (spans != null && spans.length > 0) {
                s.setSpan(spans[0], 0, s.length(), 0);
            }
            return s;
        }

        /**
         * @return the "View more conversations" view.
         */
        private RemoteViews getViewMoreConversationsView() {
            RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_loading);
            view.setTextViewText(
                    R.id.loading_text, mContext.getText(R.string.view_more_conversations));
            view.setOnClickFillInIntent(R.id.widget_loading,
                    Utils.createViewFolderIntent(mContext, mFolderUri, mAccount));
            return view;
        }

        @Override
        public RemoteViews getLoadingView() {
            RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_loading);
            view.setTextViewText(
                    R.id.loading_text, mContext.getText(R.string.loading_conversation));
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            final RemoteViews remoteViews =
                    new RemoteViews(mContext.getPackageName(), R.layout.widget);

            if (!mService.isWidgetConfigured(mContext, mAppWidgetId, mAccount)) {
                BaseWidgetProvider.updateWidget(mContext, mAppWidgetId, mAccount, mFolderType,
                        mFolderCapabilities, mFolderUri, mFolderConversationListUri,
                        mFolderDisplayName);
            }

            if (loader == mFolderLoader) {
                if (!isDataValid(data)) {
                    // Our folder may have disappeared on us
                    BaseWidgetProvider.updateWidget(mContext, mAppWidgetId, mAccount, mFolderType,
                            mFolderCapabilities, mFolderUri, mFolderConversationListUri,
                            mFolderDisplayName);

                    return;
                }

                final int unreadCount = data.getInt(UIProvider.FOLDER_UNREAD_COUNT_COLUMN);
                final String folderName = data.getString(UIProvider.FOLDER_NAME_COLUMN);
                mFolderCount = data.getInt(UIProvider.FOLDER_TOTAL_COUNT_COLUMN);

                if (!mFolderInformationShown && !TextUtils.isEmpty(folderName) &&
                        !TextUtils.isEmpty(mAccount.getDisplayName())) {
                    // We want to do a full update to the widget at least once, as the widget
                    // manager doesn't cache the state of the remote views when doing a partial
                    // widget update. This causes the folder name to be shown as blank if the state
                    // of the widget is restored.
                    LogUtils.i(LOG_TAG,"widgetservice configureValidAccountWidget in folder load complete");   //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 ADD
                    mService.configureValidAccountWidget(mContext, remoteViews, mAppWidgetId,
                            mAccount, mFolderType, mFolderCapabilities, mFolderUri,
                            mFolderConversationListUri, folderName);
                    appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
                    mFolderInformationShown = true;
                }

                // There is no reason to overwrite a valid non-null folder name with an empty string
                if (!TextUtils.isEmpty(folderName)) {
                    remoteViews.setViewVisibility(R.id.widget_folder, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widget_compose, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.widget_folder, folderName);
                } else {
                    LogUtils.e(LOG_TAG, "Empty folder name");
                }

                //TS: junwei-xu 2014-12-23 EMAIL BUGFIX_850975 ADD_S
                if (!TextUtils.isEmpty(mAccount.getEmailAddress())) {
                    remoteViews.setTextViewText(R.id.widget_account_noflip, mAccount.getEmailAddress());
                    remoteViews.setTextViewText(R.id.widget_account, mAccount.getEmailAddress());
                }

                final CharSequence unreadCountString = Utils
                        .getUnreadMessageString(mContext.getApplicationContext(), unreadCount);

                // If there are 0 unread messages, hide the unread count text view.
                // Otherwise, show the unread count.
                //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_974972 MOD_S
                //do not show unread in starred folder, already checked with UE
                if (unreadCount == 0 || folderName.equalsIgnoreCase(mContext.getString(R.string.mailbox_name_display_starred))) {
                //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_974972 MOD_E
                    remoteViews.setViewVisibility(R.id.widget_account_noflip, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widget_account_unread_flipper, View.GONE);
                } else {
                    remoteViews.setViewVisibility(R.id.widget_account_noflip, View.GONE);
                    remoteViews.setViewVisibility(R.id.widget_account_unread_flipper, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.widget_unread_count, unreadCountString);
                }
                //TS: junwei-xu 2014-12-23 EMAIL BUGFIX_850975 ADD_E
                appWidgetManager.partiallyUpdateAppWidget(mAppWidgetId, remoteViews);
            } else if (loader == mConversationCursorLoader) {
                // We want to cache the new cursor
                synchronized (sWidgetLock) {
                    LogUtils.w(LOG_TAG, "widgetservice             loader == mConversationCursorLoader          data:"+data +"        isDataValid(data):"+isDataValid(data));
                    LogUtils.i(LOG_TAG, "widgetservice data.count = " + (data == null ? "null" : data.getCount()));   //TS: zheng.zou 2015-07-22 EMAIL BUGFIX_1044483 ADD
                    if (!isDataValid(data)) {
                        mConversationCursor = null;
                    } else {
                        mConversationCursor = data;
                    }
                }

                LogUtils.w(LOG_TAG, "widgetservice             loader == mConversationCursorLoader          start");

                appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId,
                        R.id.conversation_list);
               LogUtils.w(LOG_TAG, "widgetservice             loader == mConversationCursorLoader           end");
                //TS: zheng.zou 2015-07-22 EMAIL BUGFIX_1044483 ADD_S
                //note: use this function to test if AppWidgetManager works normal.
                // If the count of providers is zero, the AppWidgetManager works abnormal.
                try {
                    List<AppWidgetProviderInfo> providers = appWidgetManager.getInstalledProviders();
                    LogUtils.w(LOG_TAG, "widgetservice  test appWidgetManager providers is " + (providers != null && !providers.isEmpty() ? "not empty" : "empty"));
                } catch (Exception e) {
                    LogUtils.e(LOG_TAG, "getInstalledProviders Exception");
                }
                //TS: zheng.zou 2015-07-22 EMAIL BUGFIX_1044483 ADD_E

                if (mConversationCursor == null || mConversationCursor.getCount() == 0) {
                    remoteViews.setTextViewText(R.id.empty_conversation_list,
                            mContext.getString(R.string.empty_folder));
                    appWidgetManager.partiallyUpdateAppWidget(mAppWidgetId, remoteViews);
                } else {     //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 ADD_S
                    remoteViews.setViewVisibility(R.id.conversation_list, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.empty_conversation_list, View.GONE);
                    appWidgetManager.partiallyUpdateAppWidget(mAppWidgetId, remoteViews);
                }   //TS: zheng.zou 2015-08-11 EMAIL BUGFIX_1044483 ADD_E
            } else if (loader == mAccountLoader) {
                BaseWidgetProvider.updateWidget(mContext, mAppWidgetId, mAccount, mFolderType,
                        mFolderCapabilities, mFolderUri, mFolderConversationListUri,
                        mFolderDisplayName);
            }
        }

        /**
         * Returns a boolean indicating whether this cursor has valid data.
         * Note: This seeks to the first position in the cursor
         */
        private static boolean isDataValid(Cursor cursor) {
            return cursor != null && !cursor.isClosed() && cursor.moveToFirst();
        }

        /**
         * A {@link DelayedTaskHandler} to throttle folder update to a reasonable rate.
         */
        private class FolderUpdateHandler extends DelayedTaskHandler {
            public FolderUpdateHandler(int refreshDelay) {
                super(Looper.myLooper(), refreshDelay);
            }

            @Override
            protected void performTask() {
                // Start the loader. The cached data will be returned if present.
                if (mFolderLoader != null) {
                    mFolderLoader.startLoading();
                }
            }
        }
    }
}
