/*
 * Copyright (C) 2010 Google Inc.
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
 *BUGFIX-845345  2014/12/19   wenggangjin     [Android5.0][Email][UE] Should show selection info at top of screen
 *BUGFIX-902637  2015-01-20   wenggangjin     [Email]The menu display wrong after some oprations in mark list screen
 *BUGFIX-921154  2015-02-06   wenggangjin     [Android5.0][Email]The star icon in Trash folder is not reasonable
 *BUGFIX-914010  2015-02-28   peng-zhang      [Android5.0][Email] Select a mail in mailist, status bar color does not change.
 *FEATURE-664766 2015/09/25   junwei-xu       [Android L][Email]The icon not change when multiselect read or unread mails
 *CR_585337      2015-09-29  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-712525  2015-10-22   zheng.zou       [Android L][Email]There are no UNDO notification after select sender image to star the mail
 *BUGFIX-980186  2015/11/27   jian.xu         [Android L][Email]There is a garbage icon for sending mail
 *BUGFIX-1355979  2016/01/15  chao-zhang     [Android M][Email][Force close]Mutiple press UNDO Email force close
 *BUGFIX-1463241 2016/01/19   jian.xu         [Force close][Email][Monkey]Com.tct.email happened force close when do monkey System Test
 ============================================================================
 */

package com.tct.mail.browse;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.tct.email.R;
import com.tct.mail.ui.ConversationListFragment;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
//import com.google.common.collect.Lists;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.collect.Lists;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.AccountObserver;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.MailAppProvider;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.ConversationColumns;
import com.tct.mail.providers.UIProvider.FolderCapabilities;
import com.tct.mail.providers.UIProvider.FolderType;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.ui.ConversationListCallbacks;
import com.tct.mail.ui.ConversationSelectionSet;
import com.tct.mail.ui.ConversationSetObserver;
import com.tct.mail.ui.ConversationUpdater;
import com.tct.mail.ui.DestructiveAction;
import com.tct.mail.ui.FolderOperation;
import com.tct.mail.ui.FolderSelectionDialog;
import com.tct.mail.ui.ToastBarOperation;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.utils.Utils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
//AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_S
import android.os.Build;
import android.annotation.TargetApi;
import android.graphics.Color;
//AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_E
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

/**
 * A component that displays a custom view for an {@code ActionBar}'s {@code
 * ContextMode} specific to operating on a set of conversations.
 */
public class SelectedConversationsActionMenu implements ActionMode.Callback,
        ConversationSetObserver {

    private static final String LOG_TAG = LogTag.getLogTag();

    /**
     * The set of conversations to display the menu for.
     */
    protected final ConversationSelectionSet mSelectionSet;

    private final ControllableActivity mActivity;
    private final ConversationListCallbacks mListController;
    /**
     * Context of the activity. A dialog requires the context of an activity rather than the global
     * root context of the process. So mContext = mActivity.getApplicationContext() will fail.
     */
    private final Context mContext;

    @VisibleForTesting
    private ActionMode mActionMode;

    private boolean mActivated = false;

    /** Object that can update conversation state on our behalf. */
    private final ConversationUpdater mUpdater;

    private Account mAccount;

    private final Folder mFolder;

    private AccountObserver mAccountObserver;

    private MenuItem mDiscardOutboxMenuItem;

  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
    private SelectedMenuCallback mCallback;

    public interface SelectedMenuCallback {
        void setStarSwitchClickable(boolean clickable);
    }
  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E

    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
    private boolean mEnableSelectAll;
    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E

    public SelectedConversationsActionMenu(
            ControllableActivity activity, ConversationSelectionSet selectionSet, Folder folder) {
        mActivity = activity;
        mListController = activity.getListHandler();
        mSelectionSet = selectionSet;
        mAccountObserver = new AccountObserver() {
            @Override
            public void onChanged(Account newAccount) {
                mAccount = newAccount;
            }
        };
        mAccount = mAccountObserver.initialize(activity.getAccountController());
        mFolder = folder;
        mContext = mActivity.getActivityContext();
        mUpdater = activity.getConversationUpdater();

        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
        mEnableSelectAll = PLFUtils.getBoolean(mContext, "feature_email_select_all");
        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        boolean handled = true;
        // If the user taps a new menu item, commit any existing destructive actions.
        mListController.commitDestructiveActions(true);
        final int itemId = item.getItemId();

        Analytics.getInstance().sendMenuItemEvent(Analytics.EVENT_CATEGORY_MENU_ITEM, itemId,
                "cab_mode", 0);

        UndoCallback undoCallback = null;   // not applicable here (yet)
        if (itemId == R.id.delete) {
            LogUtils.i(LOG_TAG, "Delete selected from CAB menu");
            performDestructiveAction(R.id.delete, undoCallback);
        } else if (itemId == R.id.discard_drafts) {
            LogUtils.i(LOG_TAG, "Discard drafts selected from CAB menu");
            performDestructiveAction(R.id.discard_drafts, undoCallback);
        } else if (itemId == R.id.discard_outbox) {
            LogUtils.i(LOG_TAG, "Discard outbox selected from CAB menu");
            performDestructiveAction(R.id.discard_outbox, undoCallback);
        } else if (itemId == R.id.archive) {
            LogUtils.i(LOG_TAG, "Archive selected from CAB menu");
            performDestructiveAction(R.id.archive, undoCallback);
        } else if (itemId == R.id.remove_folder) {
            destroy(R.id.remove_folder, mSelectionSet.values(),
                    mUpdater.getDeferredRemoveFolder(mSelectionSet.values(), mFolder, true,
                            true, true, undoCallback));
        } else if (itemId == R.id.mute) {
            destroy(R.id.mute, mSelectionSet.values(), mUpdater.getBatchAction(R.id.mute,
                    undoCallback));
        } else if (itemId == R.id.report_spam) {
            destroy(R.id.report_spam, mSelectionSet.values(),
                    mUpdater.getBatchAction(R.id.report_spam, undoCallback));
        } else if (itemId == R.id.mark_not_spam) {
            // Currently, since spam messages are only shown in list with other spam messages,
            // marking a message not as spam is a destructive action
            destroy (R.id.mark_not_spam,
                    mSelectionSet.values(), mUpdater.getBatchAction(R.id.mark_not_spam,
                            undoCallback)) ;
        } else if (itemId == R.id.report_phishing) {
            destroy(R.id.report_phishing,
                    mSelectionSet.values(), mUpdater.getBatchAction(R.id.report_phishing,
                            undoCallback));
        } else if (itemId == R.id.read) {
            markConversationsRead(true);
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_S
            //After mark read, exit the mode and return to mail list. Avoid the dull UI issue.
            clearSelection();
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_E
        } else if (itemId == R.id.unread) {
            markConversationsRead(false);
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_S
            //After mark unRead, exit the mode and return to mail list. Avoid the dull UI issue.
            clearSelection();
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_E
        } else if (itemId == R.id.star) {
            starConversations(true);
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_S
            //After mark star, exit the mode and return to mail list. Avoid the dull UI issue.
            clearSelection();
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_E
        } else if (itemId == R.id.remove_star) {
            if (mFolder.isType(UIProvider.FolderType.STARRED)) {
                LogUtils.d(LOG_TAG, "We are in a starred folder, removing the star");
                performDestructiveAction(R.id.remove_star, undoCallback);
            } else {
                LogUtils.d(LOG_TAG, "Not in a starred folder.");
                starConversations(false);
            }
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_S
            //After mark unStared, exit the mode and return to mail list. Avoid the dull UI issue.
            clearSelection();
            //TS: Gantao 2016-01-08 EMAIL BUGFIX_1275801 ADD_E
        } else if (itemId == R.id.move_to || itemId == R.id.change_folders) {
            boolean cantMove = false;
            Account acct = mAccount;
            // Special handling for virtual folders
            if (mFolder.supportsCapability(FolderCapabilities.IS_VIRTUAL)) {
                Uri accountUri = null;
                for (Conversation conv: mSelectionSet.values()) {
                    if (accountUri == null) {
                        accountUri = conv.accountUri;
                    } else if (!accountUri.equals(conv.accountUri)) {
                        // Tell the user why we can't do this
                        //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                        Utility.showToast(mContext, R.string.cant_move_or_change_labels);
                        //Toast.makeText(mContext, R.string.cant_move_or_change_labels,
                        //        Toast.LENGTH_LONG).show();
                        cantMove = true;
                        return handled;
                    }
                }
                if (!cantMove) {
                    // Get the actual account here, so that we display its folders in the dialog
                    acct = MailAppProvider.getAccountFromAccountUri(accountUri);
                }
            }
            if (!cantMove) {
                final FolderSelectionDialog dialog = FolderSelectionDialog.getInstance(
                        acct, mSelectionSet.values(), true, mFolder,
                        item.getItemId() == R.id.move_to);
                if (dialog != null) {
                    dialog.show(mActivity.getFragmentManager(), null);
                }
            }
        } else if (itemId == R.id.move_to_inbox) {
            new AsyncTask<Void, Void, Folder>() {
                @Override
                protected Folder doInBackground(final Void... params) {
                    // Get the "move to" inbox
                    return Utils.getFolder(mContext, mAccount.settings.moveToInbox,
                            true /* allowHidden */);
                }

                @Override
                protected void onPostExecute(final Folder moveToInbox) {
                    final List<FolderOperation> ops = Lists.newArrayListWithCapacity(1);
                    // Add inbox
                    ops.add(new FolderOperation(moveToInbox, true));
                    mUpdater.assignFolder(ops, mSelectionSet.values(), true,
                            true /* showUndo */, false /* isMoveTo */);
                }
            }.execute((Void[]) null);
        } else if (itemId == R.id.mark_important) {
            markConversationsImportant(true);
        } else if (itemId == R.id.mark_not_important) {
            if (mFolder.supportsCapability(UIProvider.FolderCapabilities.ONLY_IMPORTANT)) {
                performDestructiveAction(R.id.mark_not_important, undoCallback);
            } else {
                markConversationsImportant(false);
            }
            //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
        } else if (itemId == R.id.select_all) {
            ConversationCursor cursor = mListController.getConversationListCursor();
            if(cursor == null) {
                LogUtils.e(LogUtils.TAG, "Null list cursor while click select all");
                return handled;
            }
            mSelectionSet.addAll(cursor);
        } else if (itemId == R.id.unselect_all) {
            mSelectionSet.removeAll();
            //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E
        } else {
            handled = false;
        }
        return handled;
    }

    /**
     * Clear the selection and perform related UI changes to keep the state consistent.
     */
    private void clearSelection() {
        mSelectionSet.clear();
    }

    /**
     * Update the underlying list adapter and redraw the menus if necessary.
     */
    private void updateSelection() {
        mUpdater.refreshConversationList();
        if (mActionMode != null) {
            // Calling mActivity.invalidateOptionsMenu doesn't have the correct behavior, since
            // the action mode is not refreshed when activity's options menu is invalidated.
            // Since we need to refresh our own menu, it is easy to call onPrepareActionMode
            // directly.
            onPrepareActionMode(mActionMode, mActionMode.getMenu());
        }
    }

    private void performDestructiveAction(final int action, UndoCallback undoCallback) {
        final Collection<Conversation> conversations = mSelectionSet.values();
        final Settings settings = mAccount.settings;
        final boolean showDialog;
        // no confirmation dialog by default unless user preference or common sense dictates one
        if (action == R.id.discard_drafts) {
            // drafts are lost forever, so always confirm
            showDialog = true;
        } else if (settings != null && (action == R.id.archive || action == R.id.delete)) {
            showDialog = (action == R.id.delete) ? settings.confirmDelete : settings.confirmArchive;
        } else {
            showDialog = false;
        }
        if (showDialog) {
            mUpdater.makeDialogListener(action, true /* fromSelectedSet */, null /* undoCallback */);
            final int resId;
            if (action == R.id.delete) {
                resId = R.plurals.confirm_delete_conversation;
            } else if (action == R.id.discard_drafts) {
                resId = R.plurals.confirm_discard_drafts_conversation;
            } else {
                resId = R.plurals.confirm_archive_conversation;
            }
            final CharSequence message = Utils.formatPlural(mContext, resId, conversations.size());
            final ConfirmDialogFragment c = ConfirmDialogFragment.newInstance(message);
            c.displayDialog(mActivity.getFragmentManager());
        } else {
            // No need to show the dialog, just make a destructive action and destroy the
            // selected set immediately.
            // TODO(viki): Stop using the deferred action here. Use the registered action.
            destroy(action, conversations, mUpdater.getDeferredBatchAction(action, undoCallback));
        }
    }

    /**
     * Destroy these conversations through the conversation updater
     * @param actionId the ID of the action: R.id.archive, R.id.delete, ...
     * @param target conversations to destroy
     * @param action the action that performs the destruction
     */
    private void destroy(int actionId, final Collection<Conversation> target,
            final DestructiveAction action) {
        LogUtils.i(LOG_TAG, "About to remove %d converations", target.size());
        mUpdater.delete(actionId, target, action, true);
    }

    /**
     * Marks the read state of currently selected conversations (<b>and</b> the backing storage)
     * to the value provided here.
     * @param read is true if the conversations are to be marked as read, false if they are to be
     * marked unread.
     */
    private void markConversationsRead(boolean read) {
        final Collection<Conversation> targets = mSelectionSet.values();
        // The conversations are marked read but not viewed.
        mUpdater.markConversationsRead(targets, read, false);
        updateSelection();
    }

    /**
     * Marks the important state of currently selected conversations (<b>and</b> the backing
     * storage) to the value provided here.
     * @param important is true if the conversations are to be marked as important, false if they
     * are to be marked not important.
     */
    private void markConversationsImportant(boolean important) {
        final Collection<Conversation> target = mSelectionSet.values();
        final int priority = important ? UIProvider.ConversationPriority.HIGH
                : UIProvider.ConversationPriority.LOW;
        mUpdater.updateConversation(target, ConversationColumns.PRIORITY, priority);
        // Update the conversations in the selection too.
        for (final Conversation c : target) {
            c.priority = priority;
        }
        updateSelection();
    }

    /**
     * Marks the selected conversations with the star setting provided here.
     * @param star true if you want all the conversations to have stars, false if you want to remove
     * stars from all conversations
     */
    private void starConversations(boolean star) {
        final Collection<Conversation> target = mSelectionSet.values();
        //TS: zheng.zou 2015-10-22 EMAIL BUGFIX-712525 MOD_S
//        mUpdater.updateConversation(target, ConversationColumns.STARRED, star);
        ContentValues values = new ContentValues(1);
        ConversationCursor cc = mListController.getConversationListCursor();
        values.put(UIProvider.ConversationColumns.STARRED, star);
        if (star) {
            cc.updateStar(target, values, new UndoCallback() {
                @Override
                public void performUndoCallback() {
                    for (final Conversation c : target) {
                        c.starred = false;
                    }
                }
            });
        } else {
            cc.updateUnstar(target, values, new UndoCallback() {
                @Override
                public void performUndoCallback() {
                    for (final Conversation c : target) {
                        c.starred = true;
                    }
                }
            });

        }
        //TS: zheng.zou 2015-10-22 EMAIL BUGFIX-712525 MOD_S
        // Update the conversations in the selection too.
        for (final Conversation c : target) {
            c.starred = star;
        }
        updateSelection();
        //TS: chaozhang 2016-01-15 EMAIL BUGFIX_1355979  MOD_S
        //NOTE:pass the operations number to toastBarOperation. that we will use it to judge if notify or not ontify.
        //here just avoid ANR during operations batchs Mark star or unMart star.
        mActivity.onUndoAvailable(new ToastBarOperation(target.size(), star ? R.id.swipe_star : R.id.swipe_unstar, ToastBarOperation.UNDO, false, null));  //TS: zheng.zou 2015-10-22 EMAIL BUGFIX-712525 ADD
        //TS: chaozhang 2016-01-15 EMAIL BUGFIX_1355979  MOD_E
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mSelectionSet.addObserver(this);
        final MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.conversation_list_selection_actions_menu, menu);
        mActionMode = mode;
      //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
        //We are selected mode ,set star switch is unclickable
        mCallback.setStarSwitchClickable(false);
        //AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_S
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ChangeStatusBarColor();
        }
        //AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_E
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Update the actionbar to select operations available on the current conversation.
        final Collection<Conversation> conversations = mSelectionSet.values();

        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
        final MenuItem selectAll = menu.findItem(R.id.select_all);
        final MenuItem unSelectAll = menu.findItem(R.id.unselect_all);
        int selectedCount = conversations.size();
        ConversationListFragment convList = mListController.getConversationListFragment();
        int totalCount = 1;
        //TS: jian.xu 2016-01-19 EMAIL BUGFIX-1463241 MOD_S
        if(convList != null && convList.getAnimatedAdapter() != null
                && convList.getAnimatedAdapter().getCursor() != null) {
            totalCount = convList.getAnimatedAdapter().getCursor().getCount();
        }
        //TS: jian.xu 2016-01-19 EMAIL BUGFIX-1463241 MOD_E
        //Show select all option if not all conversation is selected
        boolean showSelectAll = (selectedCount < totalCount);
        if(showSelectAll) {
            mSelectionSet.setSelectAll(false);
        } else {
            mSelectionSet.setSelectAll(true);
        }
        if(mEnableSelectAll) {
            selectAll.setVisible(showSelectAll);
            unSelectAll.setVisible(!showSelectAll);
        } else {
            selectAll.setVisible(false);
            unSelectAll.setVisible(false);
        }
        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E

        boolean showStar = false;
        boolean showMarkUnread = false;
        boolean showMarkImportant = false;
        boolean showMarkNotSpam = false;
        boolean showMarkAsPhishing = false;

        // TODO(shahrk): Clean up these dirty calls using Utils.setMenuItemVisibility(...) or
        // in another way

        //Note: For star status, we only need to care the first mail in collection
        int index = 0;
        for (Conversation conversation : conversations) {
            if (index == 0) {
                showStar = !conversation.starred;
                showMarkUnread = conversation.read;
            }
            if (!conversation.isImportant()) {
                showMarkImportant = true;
            }
            if (conversation.spam) {
                showMarkNotSpam = true;
            }
            if (!conversation.phishing) {
                showMarkAsPhishing = true;
            }
            index++;
            if (showStar && showMarkUnread && showMarkImportant && showMarkNotSpam &&
                    showMarkAsPhishing) {
                break;
            }
        }
        //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-921154 MOD_S
        final MenuItem star = menu.findItem(R.id.star);
        final MenuItem unstar = menu.findItem(R.id.remove_star);
        if(mFolder.isTrash()){
            star.setVisible(false);
            unstar.setVisible(false);
        } else{
            star.setVisible(showStar);
            unstar.setVisible(!showStar);
        }
        //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-921154 MOD_E
        //TS: junwei-xu 2015-09-25 EMAIL BUGFIX-664766 ADD_S
        //Note: update the size of selected conversations.
        mode.setTitle(String.valueOf(mSelectionSet.size()));
        //TS: junwei-xu 2015-09-25 EMAIL BUGFIX-664766 ADD_E
        final MenuItem read = menu.findItem(R.id.read);
        read.setVisible(!showMarkUnread);
        final MenuItem unread = menu.findItem(R.id.unread);
        unread.setVisible(showMarkUnread);
        // We only ever show one of:
        // 1) remove folder
        // 2) archive

        final MenuItem removeFolder = menu.findItem(R.id.remove_folder);
        final MenuItem moveTo = menu.findItem(R.id.move_to);
        final MenuItem moveToInbox = menu.findItem(R.id.move_to_inbox);
        final boolean showRemoveFolder = mFolder != null && mFolder.isType(FolderType.DEFAULT)
                && mFolder.supportsCapability(FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES)
                && !mFolder.isProviderFolder()
                && mAccount.supportsCapability(AccountCapabilities.ARCHIVE);
        final boolean showMoveTo = mFolder != null
                && mFolder.supportsCapability(FolderCapabilities.ALLOWS_REMOVE_CONVERSATION);
        final boolean showMoveToInbox = mFolder != null
                && mFolder.supportsCapability(FolderCapabilities.ALLOWS_MOVE_TO_INBOX);
        removeFolder.setVisible(showRemoveFolder);
        moveTo.setVisible(showMoveTo);
        moveToInbox.setVisible(showMoveToInbox);

        final MenuItem changeFolders = menu.findItem(R.id.change_folders);
        changeFolders.setVisible(mAccount.supportsCapability(
                UIProvider.AccountCapabilities.MULTIPLE_FOLDERS_PER_CONV));

        if (mFolder != null && showRemoveFolder) {
            removeFolder.setTitle(mActivity.getActivityContext().getString(R.string.remove_folder,
                    mFolder.name));
        }
        final MenuItem archive = menu.findItem(R.id.archive);
        if (archive != null) {
            archive.setVisible(
                    mAccount.supportsCapability(UIProvider.AccountCapabilities.ARCHIVE) &&
                    mFolder.supportsCapability(FolderCapabilities.ARCHIVE));
        }
        final MenuItem spam = menu.findItem(R.id.report_spam);
        spam.setVisible(!showMarkNotSpam
                && mAccount.supportsCapability(UIProvider.AccountCapabilities.REPORT_SPAM)
                && mFolder.supportsCapability(FolderCapabilities.REPORT_SPAM));
        final MenuItem notSpam = menu.findItem(R.id.mark_not_spam);
        notSpam.setVisible(showMarkNotSpam &&
                mAccount.supportsCapability(UIProvider.AccountCapabilities.REPORT_SPAM) &&
                mFolder.supportsCapability(FolderCapabilities.MARK_NOT_SPAM));
        final MenuItem phishing = menu.findItem(R.id.report_phishing);
        phishing.setVisible(showMarkAsPhishing &&
                mAccount.supportsCapability(UIProvider.AccountCapabilities.REPORT_PHISHING) &&
                mFolder.supportsCapability(FolderCapabilities.REPORT_PHISHING));

        final MenuItem mute = menu.findItem(R.id.mute);
        if (mute != null) {
            mute.setVisible(mAccount.supportsCapability(UIProvider.AccountCapabilities.MUTE)
                    && (mFolder != null && mFolder.isInbox()));
        }
        final MenuItem markImportant = menu.findItem(R.id.mark_important);
        markImportant.setVisible(showMarkImportant
                && mAccount.supportsCapability(UIProvider.AccountCapabilities.MARK_IMPORTANT));
        final MenuItem markNotImportant = menu.findItem(R.id.mark_not_important);
        markNotImportant.setVisible(!showMarkImportant
                && mAccount.supportsCapability(UIProvider.AccountCapabilities.MARK_IMPORTANT));

        boolean shouldShowDiscardOutbox = mFolder != null && mFolder.isType(FolderType.OUTBOX);
        mDiscardOutboxMenuItem = menu.findItem(R.id.discard_outbox);
        if (mDiscardOutboxMenuItem != null) {
            //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 MOD_S
            //Note: When discard outbox can not use, we hide it
            //mDiscardOutboxMenuItem.setVisible(shouldShowDiscardOutbox);
            //mDiscardOutboxMenuItem.setEnabled(shouldEnableDiscardOutbox(conversations));
            mDiscardOutboxMenuItem.setVisible(shouldShowDiscardOutbox(conversations, shouldShowDiscardOutbox));
            //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 MOD_E
        }
        final boolean showDelete = mFolder != null && !mFolder.isType(FolderType.OUTBOX)
                && mFolder.supportsCapability(UIProvider.FolderCapabilities.DELETE);
        final MenuItem trash = menu.findItem(R.id.delete);
        trash.setVisible(showDelete);
        // We only want to show the discard drafts menu item if we are not showing the delete menu
        // item, and the current folder is a draft folder and the account supports discarding
        // drafts for a conversation
        final boolean showDiscardDrafts = !showDelete && mFolder != null && mFolder.isDraft() &&
                mAccount.supportsCapability(AccountCapabilities.DISCARD_CONVERSATION_DRAFTS);
        final MenuItem discardDrafts = menu.findItem(R.id.discard_drafts);
        if (discardDrafts != null) {
            discardDrafts.setVisible(showDiscardDrafts);
        }

        return true;
    }

    //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 ADD_S
    private boolean shouldShowDiscardOutbox(Collection<Conversation> conversations, boolean shouldShowDiscardOutbox) {
        for (Conversation conv : conversations) {
            shouldShowDiscardOutbox &=
                    conv.sendingState != UIProvider.ConversationSendingState.SENDING;
            // &&conv.sendingState != UIProvider.ConversationSendingState.RETRYING;
        }
        return shouldShowDiscardOutbox;
    }
    //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 ADD_E

    private boolean shouldEnableDiscardOutbox(Collection<Conversation> conversations) {
        boolean shouldEnableDiscardOutbox = true;
        // Java should be smart enough to realize that once showDiscardOutbox becomes false it can
        // just skip everything remaining in the for-loop..
        for (Conversation conv : conversations) {
            shouldEnableDiscardOutbox &=
                    conv.sendingState != UIProvider.ConversationSendingState.SENDING;
                   // &&conv.sendingState != UIProvider.ConversationSendingState.RETRYING;
        }
        return shouldEnableDiscardOutbox;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_S
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
             RestoreStatusBarColor();
        }
        //AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_E

        mActionMode = null;
      //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
      //We are not selected mode ,set star switch is clickable
        mCallback.setStarSwitchClickable(true);
      //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E
        // The action mode may have been destroyed due to this menu being deactivated, in which
        // case resources need not be cleaned up. However, if it was destroyed while this menu is
        // active, that implies the user hit "Done" in the top right, and resources need cleaning.
        if (mActivated) {
            destroy();
            // Only commit destructive actions if the user actually pressed
            // done; otherwise, this was handled when we toggled conversation
            // selection state.
            mActivity.getListHandler().commitDestructiveActions(true);
        }
    }

    @Override
    public void onSetPopulated(ConversationSelectionSet set) {
        // Noop. This object can only exist while the set is non-empty.
    }

    @Override
    public void onSetEmpty() {
        LogUtils.d(LOG_TAG, "onSetEmpty called.");
        destroy();
    }

    @Override
    public void onSetChanged(ConversationSelectionSet set) {
        // If the set is empty, the menu buttons are invalid and most like the menu will be cleaned
        // up. Avoid making any changes to stop flickering ("Add Star" -> "Remove Star") just
        // before hiding the menu.
        if (set.isEmpty()) {
            return;
        }
        //TS: junwei-xu 2015-09-25 EMAIL BUGFIX-664766 ADD_S
        if (mActionMode != null) {
            onPrepareActionMode(mActionMode, mActionMode.getMenu());
            updateSelection();
        }
        //TS: junwei-xu 2015-09-25 EMAIL BUGFIX-664766 ADD_E
        //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 DEL_S
        //Note: When discard outbox can not use, we hide it
        /*
        if (mFolder.isType(FolderType.OUTBOX) && mDiscardOutboxMenuItem != null) {
            mDiscardOutboxMenuItem.setEnabled(shouldEnableDiscardOutbox(set.values()));
        }
        */
        //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 DEL_E
    }

    /**
     * Activates and shows this menu (essentially starting an {@link ActionMode}) if the selected
     * set is non-empty.
     */
    public void activate() {
        if (mSelectionSet.isEmpty()) {
            return;
        }
        mListController.onCabModeEntered();
        mActivated = true;
        if (mActionMode == null) {
            mActivity.startSupportActionMode(this);
        }
    }

    /**
     * De-activates and hides the menu (essentially disabling the {@link ActionMode}), but maintains
     * the selection conversation set, and internally updates state as necessary.
     */
    public void deactivate() {
        mListController.onCabModeExited();

        if (mActionMode != null) {
            mActivated = false;
            mActionMode.finish();
        }
    }
  //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 11/28/2014 FR846709
    public boolean isActionModeNull(){
        if(mActionMode==null){
            return true;
        }else{
            return false;
        }
    }
  //[FEATURE]-ADD-END by TSNJ.wei huang

    @VisibleForTesting
    public boolean isActivated() {
        return mActivated;
    }

    /**
     * Destroys and cleans up the resources associated with this menu.
     */
    private void destroy() {
        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
        mSelectionSet.setSelectAll(false);
        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E
        deactivate();
        mSelectionSet.removeObserver(this);
        clearSelection();
        mUpdater.refreshConversationList();
        if (mAccountObserver != null) {
            mAccountObserver.unregisterAndDestroy();
            mAccountObserver = null;
        }
    }

    //AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_S
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void RestoreStatusBarColor() {
        // TODO Auto-generated method stub
        mActivity.getWindow().setStatusBarColor(Color.parseColor("#f57c00"));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ChangeStatusBarColor() {
        // TODO Auto-generated method stub
        mActivity.getWindow().setStatusBarColor(Color.parseColor("#757575"));
    }
    //AM: peng-zhang 2015-02-28 EMAIL BUGFIX_914010 MOD_S

  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_S
    public void setCallback(SelectedMenuCallback callback) {
        mCallback = callback;
    }
  //TS: Gantao 2015-10-16 EMAIL BUGFIX-ID ADD_E

}
