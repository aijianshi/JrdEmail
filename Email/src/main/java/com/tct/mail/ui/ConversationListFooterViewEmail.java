package com.tct.mail.ui;
/*HISTORY
*
*Tag            Date         Author        Description
*============== ============ =============== ==============================
*BUGFIX-963257   2015/04/01  zhonghua-tuo     [Android5.0][Email]Two 'no connection" reminder.
===========================================================================
*/
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.tct.email.R;
import com.tct.mail.ConversationListContext;
import com.tct.mail.browse.ConversationCursor;
import com.tct.mail.browse.ConversationListFooterView;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.ui.ActivityController;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;
import com.tct.mail.utils.Utility;

/**
 * TCT: Override ConversationListFooterView, add remote search view.
 */
public class ConversationListFooterViewEmail extends ConversationListFooterView {
    /// TCT: add for local search feature.
    private View mRemoteSearch;

    public ConversationListFooterViewEmail(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRemoteSearch = (View) findViewById(R.id.remote_search);
        mRemoteSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        final int id = v.getId();
        final Folder f = (Folder) v.getTag();
        if (id == R.id.remote_search) {
            mClickListener.onFooterViewRemoteSearchClick(f);
        }
    }

    /**
     * Update the view to reflect the new folder status.
     */
    @Override
    public boolean updateStatus(final ConversationCursor cursor, final boolean currentFooterShown) {
        /// TCT: disable update footer view (loadmore/loading/network error),
        ///    since current mailbox is not syncable. @{
        if (mFolder != null && !mFolder.isSyncable()) {
            LogUtils.d(LogTag.getLogTag(),
                    "updateStatus return false, for unSyncable mailbox [%s]", mFolder);
            return false;
        }
        /// @}

        ControllableActivity activity = (ControllableActivity) mClickListener;
        ActivityController activityController = (ActivityController) activity.getAccountController();
        ConversationListContext listContext = activityController.getCurrentListContext();
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        boolean starToggleStatus = activityController.getCurrentStarToggleStatus();
        /** update folder status to NONE when star toggle is checked */
        if (starToggleStatus) {
            updateFooterStatus(STATUS_NONE);
            return false;
        }
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
        /** TCT: Add condition to avoid NPE.@{ */
        if (listContext == null) {
            return false;
        }
        /** @} */
        /// TCT: get the current account.
        Account account = listContext.account;
        /// TCT: check Connectivity. Adjust mFolder is null to avoid NPE. @{
        if (!Utility.hasConnectivity(getContext()) && mFolder != null
                && mFolder.isSyncable()
                /// TCT: if it is local/remote search, not show "no connection and retry". @{
                && !(listContext.isLocalSearchExecuted())
                && !(ConversationListContext.isSearchResult(listContext))) {
            mErrorStatus = UIProvider.LastSyncResult.CONNECTION_ERROR;
          //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257 DEL_S
            //mNetworkError.setVisibility(View.VISIBLE);
          //TS: tuo-zhonghua 2015-04-01 EMAIL BUGFIX_963257 DEL_E
            mErrorText.setText(Utils.getSyncStatusText(getContext(),
                    mErrorStatus));
            mLoading.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.GONE);
            mErrorActionButton.setVisibility(View.VISIBLE);
            mErrorActionButton.setText(R.string.retry);
            mRemoteSearch.setVisibility(View.GONE);
            return true;
        }
        /// @}
        // check if this folder allow remote search.
        /// TCT: show search on server for syncable folder except outbox.
        if (listContext.isLocalSearchExecuted() && mFolder != null
                && mFolder.isSyncable() && !mFolder.isType(UIProvider.FolderType.OUTBOX)) {
            mLoading.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.GONE);
            mLoadMore.setVisibility(View.GONE);
            /// TCT: pop account do not support remote search. @{
            boolean showRemoteSearch = account != null ? account.supportsCapability(
                    AccountCapabilities.FOLDER_SERVER_SEARCH) : false;
            mRemoteSearch.setVisibility(showRemoteSearch ? View.VISIBLE : View.GONE);
            return showRemoteSearch;
            /// @}
        } else {
            mRemoteSearch.setVisibility(View.GONE);
        }
        return super.updateStatus(cursor, currentFooterShown);
    }
}
