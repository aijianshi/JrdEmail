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
/**
*===================================================================================================================
*HISTORY
*
*Tag             Date        Author          Description
*============== ============ =============== =======================================================================
*BUGFIX-881447   2014/12/31  wenggangjin     [Email]Can't click "retry" icon
====================================================================================================================
*BUGFIX-904284   2015/01/30  peng-zhang     [Android5.0][Email]Always trying to load mails when there is no connection.
====================================================================================================================
*BUGFIX-963257   2015/04/01  zhonghua-tuo     [Android5.0][Email]Two 'no connection" reminder.
====================================================================================================================
*BUGFIX-963963   2015/04/01  zhonghua-tuo     [Android5.0][Email]'No connection' flash twice in Sent when no network.
====================================================================================================================
====================================================================================================================
*BUGFIX-1013190   2015/06/04  chaozhang    [Monitor][Android5.0][Email]"Couldn't sign in" always display when enter email.
====================================================================================================================
====================================================================================================================
*BUGFIX-1027389   2015/07/07  yanhua.chen    [Email]It's no response when tap report after prompt internal error Edit Notification
====================================================================================================================
*/
package com.tct.mail.browse;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.ui.ErrorListener;
import com.tct.mail.ui.ViewMode;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;

public  class ConversationListFooterView extends LinearLayout implements View.OnClickListener,
        ViewMode.ModeChangeListener {

    public interface FooterViewClickListener {
        void onFooterViewErrorActionClick(Folder folder, int errorStatus);
        void onFooterViewLoadMoreClick(Folder folder);
        /// TCT: add for local search(start remote search in footer view not by IME key).
        void onFooterViewRemoteSearchClick(Folder folder);
    }

    protected static final int STATUS_NONE = 0;
    private static final int STATUS_LOADING = 1;
    private static final int STATUS_LOADMORE = 2;
    private static final int STATUS_NETWORK = 3;
    protected View mLoading;
    protected View mNetworkError;
    protected View mLoadMore;
    protected Button mErrorActionButton;
    protected TextView mErrorText;
    protected Folder mFolder;
    protected Uri mLoadMoreUri;
    protected int mErrorStatus;
    protected FooterViewClickListener mClickListener;
    protected final boolean mTabletDevice;
    // Backgrounds for different states.
    //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304
    private Drawable sWideBackground;
    private Drawable sNormalBackground;
    //[BUGFIX]-Mod-END by TCTNB.caixia.chen
    /// TCT:To recorder the last cursor state
    private int mCursorState;

    //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/05/2016,2013128,
    //[Email]Outbox has two refresh icon when manual refres
    protected ProgressBar mLoadingProgress;
    //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

    public ConversationListFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTabletDevice = Utils.useTabletUI(context.getResources());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mLoading = findViewById(R.id.loading);
        mNetworkError = findViewById(R.id.network_error);
        mLoadMore = findViewById(R.id.load_more);
        mLoadMore.setOnClickListener(this);
        //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/05/2016,2013128,
        //[Email]Outbox has two refresh icon when manual refres
        mLoadingProgress = (ProgressBar)findViewById(R.id.loading_progress);
        //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

        mErrorActionButton = (Button) findViewById(R.id.error_action_button);
        mErrorActionButton.setOnClickListener(this);
        mErrorText = (TextView)findViewById(R.id.error_text);
    }

    public void setClickListener(FooterViewClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        final Folder f = (Folder) v.getTag();
        if (id == R.id.error_action_button) {
            mClickListener.onFooterViewErrorActionClick(f, mErrorStatus);
        } else if (id == R.id.load_more) {
            LogUtils.d(LogTag.getLogTag(), "LoadMore triggered folder [%s]",
                    f != null ? f.loadMoreUri : "null");
            mClickListener.onFooterViewLoadMoreClick(f);
          //[BUGFIX]-Add-BEGIN by TSNJ.Zhenhua.Fan,19/11/2014,PR-828279 not bug I don't want to change this but the customer always push me ,
            mLoadMore.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            //[BUGFIX]-Add-END by TSNJ.Zhenhua.Fan
            //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/05/2016,2013128,
            //[Email]Outbox has two refresh icon when manual refres
            mLoadingProgress.setVisibility(View.VISIBLE);
            //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

        }
    }

    /// TCT: update UI status, for Loading/LoadMore/NetworkError.
    public void updateLoadingStatus(boolean start) {
        LogUtils.d(LogTag.getLogTag(), "updateLoadingStatus show loading progress dialog ? [%s]", start);
        mNetworkError.setVisibility(View.GONE);
        if (start) {
            mLoading.setVisibility(View.VISIBLE);
            mLoadMore.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * TCT: update footer view status with loading, loadmore, network and none
     *
     * @param status status to set
     */
    public void updateFooterStatus(final int status) {
        switch (status) {
            case STATUS_LOADING:
                mLoading.setVisibility(View.VISIBLE);
                mNetworkError.setVisibility(View.GONE);
                mLoadMore.setVisibility(View.GONE);
                break;
            case STATUS_LOADMORE:
                mLoading.setVisibility(View.GONE);
                mNetworkError.setVisibility(View.GONE);
                mLoadMore.setVisibility(View.VISIBLE);
                //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/05/2016,2013128,
                //[Email]Outbox has two refresh icon when manual refres
                mLoadingProgress.setVisibility(View.GONE);
                //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

                break;
            case STATUS_NETWORK:
                mLoading.setVisibility(View.GONE);
                //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257 MOD_S
                //NOTE:DO not show the networkError in footview,only just show the toast.
                //revert the codes.
                mNetworkError.setVisibility(View.GONE);
                //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257 MOD_E
                mLoadMore.setVisibility(View.GONE);
                //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/05/2016,2013128,
                //[Email]Outbox has two refresh icon when manual refres
                mLoadingProgress.setVisibility(View.GONE);
                //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

                break;
            case STATUS_NONE:
            default:
                LogUtils.d(LogTag.getLogTag(), "updateFooterStatus with unknown status: %d", status)
                ;
                mLoading.setVisibility(View.GONE);
                mNetworkError.setVisibility(View.GONE);
                mLoadMore.setVisibility(View.GONE);
                //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/05/2016,2013128,
                //[Email]Outbox has two refresh icon when manual refres
                mLoadingProgress.setVisibility(View.GONE);
                //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang
                break;
        }
    }

    public void setFolder(Folder folder) {
        mFolder = folder;
        mErrorActionButton.setTag(mFolder);
        mLoadMore.setTag(mFolder);
        mLoadMoreUri = folder.loadMoreUri;
    }

    /**
     * Update the view to reflect the new folder status.
     * NOTE: Notused codes.
     */
    public boolean updateStatus(final ConversationCursor cursor) {
        if (cursor == null) {
            mLoading.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.GONE);
            return false;
        }
        boolean showFooter = true;
        final Bundle extras = cursor.getExtras();
        final int cursorStatus = extras.getInt(UIProvider.CursorExtraKeys.EXTRA_STATUS);
        mErrorStatus = extras.containsKey(UIProvider.CursorExtraKeys.EXTRA_ERROR) ?
                extras.getInt(UIProvider.CursorExtraKeys.EXTRA_ERROR)
                : UIProvider.LastSyncResult.SUCCESS;
        final int totalCount = extras.getInt(UIProvider.CursorExtraKeys.EXTRA_TOTAL_COUNT);

        if (UIProvider.CursorStatus.isWaitingForResults(cursorStatus)) {
            if (cursor.getCount() != 0) {
                // When loading more, show the spinner in the footer.
                mLoading.setVisibility(View.VISIBLE);
                mNetworkError.setVisibility(View.GONE);
                mLoadMore.setVisibility(View.GONE);
                //AS: peng-zhang 5-01-30 EMAIL BUGFIX_904284 MOD_S
                if(mErrorStatus != UIProvider.LastSyncResult.SUCCESS){
                      mLoading.setVisibility(View.GONE);
                      mLoadMore.setVisibility(View.VISIBLE);
                }
                //AS: peng-zhang 2015-01-30 EMAIL BUGFIX_904284 MOD_E
            } else {
                // We're currently loading, but we have no messages at all. We don't need to show
                // the footer, because we should be displaying the loading state on the
                // conversation list itself.
                showFooter = false;
            }
        } else if (mErrorStatus != UIProvider.LastSyncResult.SUCCESS) {
            // We are in some error state, show the footer with an error message.
          //TS: wenggangjin 2014-12-31 EMAIL BUGFIX_881447 MOD_S
//            mNetworkError.setVisibility(View.VISIBLE);
//            mErrorText.setText(Utils.getSyncStatusText(getContext(), mErrorStatus));
            mLoading.setVisibility(View.GONE);
//            mLoadMore.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.VISIBLE);
            // Only show the "Retry" button for I/O errors; it won't help for
            // internal errors.
//            mErrorActionButton.setVisibility(
//                    mErrorStatus != UIProvider.LastSyncResult.SECURITY_ERROR ?
//                    View.VISIBLE : View.GONE);

//            final int actionTextResourceId;
//            switch (mErrorStatus) {
//                case UIProvider.LastSyncResult.CONNECTION_ERROR:
//                    actionTextResourceId = R.string.retry;
//                    break;
//                case UIProvider.LastSyncResult.SERVER_ERROR:
//                    actionTextResourceId = R.string.retry;
//                    break;
//                case UIProvider.LastSyncResult.AUTH_ERROR:
//                    actionTextResourceId = R.string.signin;
//                    break;
//                case UIProvider.LastSyncResult.SECURITY_ERROR:
//                    actionTextResourceId = R.string.retry;
//                    mNetworkError.setVisibility(View.GONE);
//                    break; // Currently we do nothing for security errors.
//                case UIProvider.LastSyncResult.STORAGE_ERROR:
//                    actionTextResourceId = R.string.info;
//                    break;
//                case UIProvider.LastSyncResult.INTERNAL_ERROR:
//                    actionTextResourceId = R.string.report;
//                    break;
//                default:
//                    actionTextResourceId = R.string.retry;
//                    mNetworkError.setVisibility(View.GONE);
//                    break;
//            }
//            mErrorActionButton.setText(actionTextResourceId);
          //TS: wenggangjin 2014-12-31 EMAIL BUGFIX_881447 MOD_E
        } else if (mLoadMoreUri != null && cursor.getCount() < totalCount) {
            // We know that there are more messages on the server than we have locally, so we
            // need to show the footer with the "load more" button.
            mLoading.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.VISIBLE);
        } else {
            showFooter = false;
        }
        return showFooter;
    }

    /**
     * TCT: Update the view to reflect the new folder status.
     * @param ConversationCursor the cursor
     * @param currentFooterShown the current footer view shown state
     * @return boolean the new footer view shown state
     */
    public boolean updateStatus(final ConversationCursor cursor, final boolean currentFooterShown) {
        if (cursor == null) {
            // reset status of footer view
            updateFooterStatus(STATUS_NONE);
            return false;
        }
        boolean showFooter = true;
        final Bundle extras = cursor.getExtras();
        final int cursorStatus = extras.getInt(UIProvider.CursorExtraKeys.EXTRA_STATUS);
        mErrorStatus = extras.containsKey(UIProvider.CursorExtraKeys.EXTRA_ERROR) ?
        extras.getInt(UIProvider.CursorExtraKeys.EXTRA_ERROR)
        : UIProvider.LastSyncResult.SUCCESS;
        final int totalCount = extras.getInt(UIProvider.CursorExtraKeys.EXTRA_TOTAL_COUNT);
        /// TCT: Get the value from extra
			/*final boolean allMessagesLoadFinish = extras.getBoolean(
			UIProvider.CursorExtraKeys.EXTRA_MESSAGES_LOAD_FINISH, false);*/

        if (UIProvider.CursorStatus.isWaitingForResults(cursorStatus)) {
            if (cursor.getCount() != 0) {
                // When loading more, show the spinner in the footer.
                updateFooterStatus(STATUS_LOADING);
            } else {
                // We're currently loading, but we have no messages at all. We don't need to show
                // the footer, because we should be displaying the loading state on the
                // conversation list itself.
                showFooter = false;
            }
        } else if (mErrorStatus != UIProvider.LastSyncResult.SUCCESS) {
            // We are in some error state, show the footer with an error message.
            //TS: zhangchao 2015-06-04 EMAIL BUGFIX_1013190 DEL_S
            //NOTE just GONE the errorView.
            //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257 DEL_S
            mNetworkError.setVisibility(View.GONE);
            //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257 DEL_E
            //mErrorText.setText(Utils.getSyncStatusText(getContext(), mErrorStatus));
            //TS: zhangchao 2015-06-04 EMAIL BUGFIX_1013190 DEL_E
            mLoading.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.GONE);
            // Only show the "Retry" button for I/O errors; it won't help for
            // internal errors.
            mErrorActionButton.setVisibility(
                    mErrorStatus != UIProvider.LastSyncResult.SECURITY_ERROR ?
                    View.VISIBLE : View.GONE);

            final int actionTextResourceId;
            switch (mErrorStatus) {
                case UIProvider.LastSyncResult.CONNECTION_ERROR:
                    actionTextResourceId = R.string.retry;
                    break;
                case UIProvider.LastSyncResult.SERVER_ERROR:
                    actionTextResourceId = R.string.retry;
                    break;
                case UIProvider.LastSyncResult.AUTH_ERROR:
                    actionTextResourceId = R.string.signin;
                    break;
                case UIProvider.LastSyncResult.SECURITY_ERROR:
                    actionTextResourceId = R.string.retry;
                    mNetworkError.setVisibility(View.GONE);
                    break; // Currently we do nothing for security errors.
                case UIProvider.LastSyncResult.STORAGE_ERROR:
                    actionTextResourceId = R.string.info;
                    break;
                case UIProvider.LastSyncResult.INTERNAL_ERROR:
                    //TS: yanhua.chen 2015-7-7 EMAIL BUGFIX_1027389 MOD_S
                    //Note remove report button when internal error
                    //actionTextResourceId = R.string.report;
                    actionTextResourceId = R.string.report_empty;
                    //TS: yanhua.chen 2015-7-7 EMAIL BUGFIX_1027389 MOD_E
                    break;
                default:
                    actionTextResourceId = R.string.retry;
                    mNetworkError.setVisibility(View.GONE);
                    break;
            }
            mErrorActionButton.setText(actionTextResourceId);

        } else if (mLoadMoreUri != null && cursor.getCount() < totalCount
			/*&& !allMessagesLoadFinish*/) {
            // We know that there are more messages on the server than we have locally, so we
            // need to show the footer with the "load more" button.
            updateFooterStatus(STATUS_LOADMORE);
        } else {
            // TODO: what happens to here ?
            // Maybe showFooter as what it was is better.
            showFooter = false;
            updateFooterStatus(STATUS_NONE);
            LogUtils.d(LogTag.getLogTag(), "Enter the case and footer view's shown state was : %s",
                    currentFooterShown);
        }
        return showFooter;
    }

    /**
     * Update to the appropriate background when the view mode changes.
     */
    @Override
    public void onViewModeChanged(int newMode) {
        final Drawable drawable;
        if (mTabletDevice && newMode == ViewMode.CONVERSATION_LIST) {
            drawable = getWideBackground();
        } else {
            drawable = getNormalBackground();
        }
        setBackgroundDrawable(drawable);
    }

    private Drawable getWideBackground() {
        if (sWideBackground == null) {
            sWideBackground = getBackground(R.drawable.conversation_wide_unread_selector);
        }
        return sWideBackground;
    }

    private Drawable getNormalBackground() {
        if (sNormalBackground == null) {
            sNormalBackground = getBackground(R.drawable.conversation_item_background_selector);
        }
        return sNormalBackground;
    }

    private Drawable getBackground(int resId) {
        return getContext().getResources().getDrawable(resId);
    }
}
