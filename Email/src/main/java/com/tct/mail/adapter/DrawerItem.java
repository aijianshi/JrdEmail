/*
 * Copyright (C) 2013 The Android Open Source Project
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
 *Tag		 	 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-50002 2014/10/24   zhaotianyong	  Modify the package conflict
 *FEATURE-834751 2015/10/28   jian.xu         Use different color to distinguish each account when in combined view mode
 ============================================================================ 
 */

package com.tct.mail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//TS: MOD by zhaotianyong for CONFLICT_50002 START
//import com.android.bitmap.BitmapCache;
import com.tct.fw.bitmap.BitmapCache;
import com.tct.mail.bitmap.ContactResolver;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Folder;
import com.tct.mail.ui.AccountItemView;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.ui.FolderItemView;
import com.tct.mail.utils.FolderUri;
//TS: MOD by zhaotianyong for CONFLICT_50002 END

import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;


/**
 * An element that is shown in the {@link com.tct.mail.ui.FolderListFragment}. This class is
 * only used for elements that are shown in the {@link com.tct.mail.ui.DrawerFragment}.
 * This class is an enumeration of a few element types: Account, a folder, a recent folder,
 * or a header (a resource string). A {@link DrawerItem} can only be one type and can never
 * switch types. Items are created using methods like
 * {@link DrawerItem#ofAccount(ControllableActivity, Account, int, boolean, boolean, BitmapCache,
 * ContactResolver)},
 * {@link DrawerItem#ofWaitView(ControllableActivity)}, etc.
 *
 * Once created, the item can create a view using
 * {@link #getView(android.view.View, android.view.ViewGroup)}.
 */
public class DrawerItem {
    private static final String LOG_TAG = LogTag.getLogTag();
    public final Folder mFolder;
    public final Account mAccount;
    private final int mResource;
    /** True if the drawer item represents the current account, false otherwise */
    private final boolean mIsSelected;
    //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-S
    /** True if the drawer item need to show account color block, false otherwise */
    private final boolean mIsAccountColorBlockVisible;
    //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-E
    /** Either {@link #VIEW_ACCOUNT}, {@link #VIEW_FOLDER} or {@link #VIEW_HEADER} */
    public final int mType;
    /** A normal folder, also a child, if a parent is specified. */
    public static final int VIEW_FOLDER = 0;
    /** A text-label which serves as a header in sectioned lists. */
    public static final int VIEW_HEADER = 1;
    /** A text-label which serves as a header in sectioned lists. */
    public static final int VIEW_BLANK_HEADER = 2;
    /** An account object, which allows switching accounts rather than folders. */
    public static final int VIEW_ACCOUNT = 3;
    /** An expandable object for expanding/collapsing more of the list */
    public static final int VIEW_WAITING_FOR_SYNC = 4;
    /** The value (1-indexed) of the last View type.  Useful when returning the number of types. */
    private static final int LAST_FIELD = VIEW_WAITING_FOR_SYNC + 1;

    /** TODO: On adding another type, be sure to change getViewTypes() */

    /** The parent activity */
    private final ControllableActivity mActivity;
    private final LayoutInflater mInflater;

    // TODO(viki): Put all these constants in an interface.
    /**
     * Either {@link #FOLDER_INBOX}, {@link #FOLDER_RECENT} or {@link #FOLDER_OTHER} when
     * {@link #mType} is {@link #VIEW_FOLDER}, or an {@link #ACCOUNT} in the case of
     * accounts, and {@link #INERT_HEADER} otherwise.
     */
    public final int mFolderType;
    /** Non existent item or folder type not yet set */
    public static final int UNSET = 0;
    /** An unclickable text-header visually separating the different types. */
    public static final int INERT_HEADER = 0;
    /** An inbox folder: Inbox, ...*/
    public static final int FOLDER_INBOX = 1;
    /** A folder from whom a conversation was recently viewed */
    public static final int FOLDER_RECENT = 2;
    /** A non-inbox folder that is shown in the "everything else" group. */
    public static final int FOLDER_OTHER = 3;
    /** An entry for the accounts the user has on the device. */
    public static final int ACCOUNT = 4;

    /** True if this view is enabled, false otherwise. */
    private final boolean mIsEnabled;

    private BitmapCache mImagesCache;
    private ContactResolver mContactResolver;

    @Override
    public String toString() {
        switch(mType) {
            case VIEW_FOLDER:
                return folderToString();
            case VIEW_HEADER:
                return headerToString();
            case VIEW_BLANK_HEADER:
                return blankHeaderToString();
            case VIEW_ACCOUNT:
                return accountToString();
            case VIEW_WAITING_FOR_SYNC:
                return waitToString();
        }
        // Should never come here.
        return null;
    }

    /**
     * Creates a drawer item with every instance variable specified.
     *
     * @param type the type of the item. This must be a VIEW_* element
     * @param activity the underlying activity
     * @param folder a non-null folder, if this is a folder type
     * @param folderType the type of the folder. For folders this is:
     *            {@link #FOLDER_INBOX}, {@link #FOLDER_RECENT},
     *            {@link #FOLDER_OTHER}, or for non-folders this is
     *            {@link #ACCOUNT}, or {@link #INERT_HEADER}
     * @param account the account object, for an account drawer element
     * @param resource either the string resource for a header, or the unread
     *            count for an account.
     * @param isCurrentAccount true if this item is the current account
     */
    private DrawerItem(int type, ControllableActivity activity, Folder folder, int folderType,
            Account account, int resource, boolean isCurrentAccount, boolean isAccountColorBlockVisible, BitmapCache cache,
            ContactResolver contactResolver) {
        mActivity = activity;
        mFolder = folder;
        mFolderType = folderType;
        mAccount = account;
        mResource = resource;
        mIsSelected = isCurrentAccount;
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-S
        mIsAccountColorBlockVisible = isAccountColorBlockVisible;
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-E
        mInflater = LayoutInflater.from(activity.getActivityContext());
        mType = type;
        mIsEnabled = calculateEnabled();
        mImagesCache = cache;
        mContactResolver = contactResolver;
    }

    /**
     * Create a folder item with the given type.
     *
     * @param activity the underlying activity
     * @param folder a folder that this item represents
     * @param folderType one of {@link #FOLDER_INBOX}, {@link #FOLDER_RECENT} or
     * {@link #FOLDER_OTHER}
     * @return a drawer item for the folder.
     */
    public static DrawerItem ofFolder(ControllableActivity activity, Folder folder,
            int folderType) {
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-S
        //NOTE: Add a parameter which indicate whether to show account color block.
        return new DrawerItem(VIEW_FOLDER, activity, folder,  folderType, null, -1, false, false, null, null);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-E
    }

    private String folderToString() {
        final StringBuilder sb = new StringBuilder("[DrawerItem ");
        sb.append(" VIEW_FOLDER ");
        sb.append(", mFolder=");
        sb.append(mFolder);
        sb.append(", mFolderType=");
        sb.append(mFolderType);
        sb.append("]");
        return sb.toString();
    }

    //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-S
    //NOTE: Add a parameter which indicate whether to show account color block.
    /**
     * Creates an item from an account.
     * @param activity the underlying activity
     * @param account the account to create a drawer item for
     * @param unreadCount the unread count of the account, pass zero if
     * @param isCurrentAccount true if the account is the current account, false otherwise
     * @return a drawer item for the account.
     */
    public static DrawerItem ofAccount(ControllableActivity activity, Account account,
            int unreadCount, boolean isCurrentAccount, boolean isAccountColorBlockVisible, BitmapCache cache,
            ContactResolver contactResolver) {
        return new DrawerItem(VIEW_ACCOUNT, activity, null, ACCOUNT, account, unreadCount,
                isCurrentAccount, isAccountColorBlockVisible, cache, contactResolver);
    }
    //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-E

    private String accountToString() {
        final StringBuilder sb = new StringBuilder("[DrawerItem ");
        sb.append(" VIEW_ACCOUNT ");
        sb.append(", mAccount=");
        sb.append(mAccount);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Create a header item with a string resource.
     *
     * @param activity the underlying activity
     * @param resource the string resource: R.string.all_folders_heading
     * @return a drawer item for the header.
     */
    public static DrawerItem ofHeader(ControllableActivity activity, int resource) {
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-S
        //NOTE: Add a parameter which indicate whether to show account color block.
        return new DrawerItem(VIEW_HEADER, activity, null, INERT_HEADER, null, resource, false, false, null, null);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-E
    }

    private String headerToString() {
        final StringBuilder sb = new StringBuilder("[DrawerItem ");
        sb.append(" VIEW_HEADER ");
        sb.append(", mResource=");
        sb.append(mResource);
        sb.append("]");
        return sb.toString();
    }

    public static DrawerItem ofBlankHeader(ControllableActivity activity) {
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-S
        //NOTE: Add a parameter which indicate whether to show account color block.
        return new DrawerItem(VIEW_BLANK_HEADER, activity, null, INERT_HEADER, null, 0, false, false, null, null);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-E
    }

    private String blankHeaderToString() {
        return "[DrawerItem VIEW_BLANK_HEADER]";
    }

    /**
     * Create a "waiting for initialization" item.
     *
     * @param activity the underlying activity
     * @return a drawer item with an indeterminate progress indicator.
     */
    public static DrawerItem ofWaitView(ControllableActivity activity) {
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-S
        //NOTE: Add a parameter which indicate whether to show account color block.
        return new DrawerItem(VIEW_WAITING_FOR_SYNC, activity, null, INERT_HEADER, null, -1, false, false, null, null);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-E
    }

    private static String waitToString() {
        return "[DrawerItem VIEW_WAITING_FOR_SYNC ]";
    }

    /**
     * Returns a view for the given item. The method signature is identical to that required by a
     * {@link android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)}.
     */
    public View getView(View convertView, ViewGroup parent) {
        final View result;
        switch (mType) {
            case VIEW_FOLDER:
                result = getFolderView(convertView, parent);
                break;
            case VIEW_HEADER:
                result = getHeaderView(convertView, parent);
                break;
            case VIEW_BLANK_HEADER:
                result = getBlankHeaderView(convertView, parent);
                break;
            case VIEW_ACCOUNT:
                result = getAccountView(convertView, parent);
                break;
            case VIEW_WAITING_FOR_SYNC:
                result = getEmptyView(convertView, parent);
                break;
            default:
                LogUtils.wtf(LOG_TAG, "DrawerItem.getView(%d) for an invalid type!", mType);
                result = null;
        }
        return result;
    }

    /**
     * Book-keeping for how many different view types there are. Be sure to
     * increment this appropriately once adding more types as drawer items
     * @return number of different types of view items
     */
    public static int getViewTypes() {
        return LAST_FIELD;
    }

    /**
     * Returns whether this view is enabled or not. An enabled view is one that accepts user taps
     * and acts upon them.
     * @return true if this view is enabled, false otherwise.
     */
    public boolean isItemEnabled() {
        return mIsEnabled;
    }

    /** Calculate whether the item is enabled */
    private boolean calculateEnabled() {
        switch (mType) {
            case VIEW_HEADER:
            case VIEW_BLANK_HEADER:
                // Headers are never enabled.
                return false;
            case VIEW_FOLDER:
                // Folders are always enabled.
                return true;
            case VIEW_ACCOUNT:
                // Accounts are always enabled.
                return true;
            case VIEW_WAITING_FOR_SYNC:
                // Waiting for sync cannot be tapped, so never enabled.
                return false;
            default:
                LogUtils.wtf(LOG_TAG, "DrawerItem.isItemEnabled() for invalid type %d", mType);
                return false;
        }
    }

    /**
     * Returns whether this view is highlighted or not.
     *
     *
     * @param currentFolder The current folder, according to the
     *                      {@link com.tct.mail.ui.FolderListFragment}
     * @param currentType The type of the current folder. We want to only highlight a folder once.
     *                    A folder might be in two places at once: in "All Folders", and in
     *                    "Recent Folder". Valid types of selected folders are :
     *                    {@link DrawerItem#FOLDER_INBOX}, {@link DrawerItem#FOLDER_RECENT} or
     *                    {@link DrawerItem#FOLDER_OTHER}, or {@link DrawerItem#UNSET}.

     * @return true if this DrawerItem results in a view that is highlighted (this DrawerItem is
     *              the current folder.
     */
    public boolean isHighlighted(FolderUri currentFolder, int currentType) {
        switch (mType) {
            case VIEW_HEADER:
            case VIEW_BLANK_HEADER:
                // Headers are never highlighted
                return false;
            case VIEW_FOLDER:
                // True if folder types and URIs are the same
                if (currentFolder != null && mFolder != null && mFolder.folderUri != null) {
                    return (mFolderType == currentType) && mFolder.folderUri.equals(currentFolder);
                }
                return false;
            case VIEW_ACCOUNT:
                // Accounts are never highlighted
                return false;
            case VIEW_WAITING_FOR_SYNC:
                // Waiting for sync cannot be tapped, so never highlighted.
                return false;
            default:
                LogUtils.wtf(LOG_TAG, "DrawerItem.isHighlighted() for invalid type %d", mType);
                return false;
        }
    }

    /**
     * Return a view for an account object.
     *
     * @param convertView a view, possibly null, to be recycled.
     * @param parent the parent viewgroup to attach to.
     * @return a view to display at this position.
     */
    private View getAccountView(View convertView, ViewGroup parent) {
        final AccountItemView accountItemView;
        if (convertView != null) {
            accountItemView = (AccountItemView) convertView;
        } else {
            accountItemView =
                    (AccountItemView) mInflater.inflate(R.layout.account_item, parent, false);
        }
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-S
        //NOTE: Add a parameter which indicate whether to show account color block.
        accountItemView.bind(mActivity.getActivityContext(), mAccount, mIsSelected, mIsAccountColorBlockVisible,
                mImagesCache, mContactResolver);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 MOD-E
        return accountItemView;
    }

    /**
     * Returns a text divider between divisions.
     *
     * @param convertView a previous view, perhaps null
     * @param parent the parent of this view
     * @return a text header at the given position.
     */
    private View getHeaderView(View convertView, ViewGroup parent) {
        final View headerView;
        if (convertView != null) {
            headerView = convertView;
        } else {
            headerView = mInflater.inflate(R.layout.folder_list_header, parent, false);
        }
        final TextView textView = (TextView) headerView.findViewById(R.id.header_text);
        textView.setText(mResource);
        return headerView;
    }

    /**
     * Returns a blank divider
     *
     * @param convertView A previous view, perhaps null
     * @param parent the parent of this view
     * @return a blank header
     */
    private View getBlankHeaderView(View convertView, ViewGroup parent) {
        final View blankHeaderView;
        if (convertView != null) {
            blankHeaderView = convertView;
        } else {
            blankHeaderView = mInflater.inflate(R.layout.folder_list_blank_header, parent, false);
        }
        return blankHeaderView;
    }

    /**
     * Return a folder: either a parent folder or a normal (child or flat)
     * folder.
     *
     * @param convertView a view, possibly null, to be recycled.
     * @return a view showing a folder at the given position.
     */
    private View getFolderView(View convertView, ViewGroup parent) {
        final FolderItemView folderItemView;
        if (convertView != null) {
            folderItemView = (FolderItemView) convertView;
        } else {
            folderItemView =
                    (FolderItemView) mInflater.inflate(R.layout.folder_item, parent, false);
        }
        folderItemView.bind(mFolder, mActivity);
        folderItemView.setIcon(mFolder);
        return folderItemView;
    }

    /**
     * Return a view for the 'Waiting for sync' item with the indeterminate progress indicator.
     *
     * @param convertView a view, possibly null, to be recycled.
     * @param parent the parent hosting this view.
     * @return a view for "Waiting for sync..." at given position.
     */
    private View getEmptyView(View convertView, ViewGroup parent) {
        final ViewGroup emptyView;
        if (convertView != null) {
            emptyView = (ViewGroup) convertView;
        } else {
            emptyView = (ViewGroup) mInflater.inflate(R.layout.drawer_empty_view, parent, false);
        }
        return emptyView;
    }

}
