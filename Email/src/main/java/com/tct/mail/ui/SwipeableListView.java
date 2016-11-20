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
 *Tag             Date        Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *FEATURE-559893 2015/09/11   tao.gan         [Email]Auto hiding action bar in mail box list
 *BUGFIX_1097364  2015/10/16    jin.dong    [Monkey][Crash] com.tct.email
 *BUGFIX_1134859  2016/1/31     chaozhang   [Monkey][CRASH]CRASH: com.tct.email
 ============================================================================
 */

package com.tct.mail.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tct.email.R;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.browse.ConversationCursor;
import com.tct.mail.browse.ConversationItemView;
import com.tct.mail.browse.SwipeableConversationItemView;
import com.tct.mail.browse.UndoCallback;
import com.tct.mail.providers.*;
import com.tct.mail.ui.SwipeHelper.Callback;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SwipeableListView extends ListView implements Callback, OnScrollListener{
    private final SwipeHelper mSwipeHelper;
    private boolean mEnableSwipe = false;

    public static final String LOG_TAG = LogTag.getLogTag();

    private ConversationSelectionSet mConvSelectionSet;
    private int mSwipeAction;
    private Account mAccount;
    private Folder mFolder;
    private ListItemSwipedListener mSwipedListener;
    private boolean mScrolling;

    private SwipeListener mSwipeListener;
    private int mSwipeDirection;    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD

  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
    private ControllableActivity mActivity;
    private ImageButton mComposeButton;
    private float mLastY = 0;
    private float mCurrentY = 0;
  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S

    // Instantiated through view inflation
    @SuppressWarnings("unused")
    public SwipeableListView(Context context) {
        this(context, null);
    }

    public SwipeableListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwipeableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(this);
        float densityScale = getResources().getDisplayMetrics().density;
        float pagingTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mSwipeHelper = new SwipeHelper(context, SwipeHelper.X, this, densityScale,
                pagingTouchSlop);
      //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
        //Get the activiyt and fab button
        if(context instanceof ControllableActivity) {
            mActivity = (ControllableActivity) context;
            mComposeButton = mActivity.getComposeButton();
        } else {
            LogUtils.w(LogUtils.TAG,"Unexpected context while initialize SwipeableListView");
        }
      //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        float densityScale = getResources().getDisplayMetrics().density;
        mSwipeHelper.setDensityScale(densityScale);
        float pagingTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
        mSwipeHelper.setPagingTouchSlop(pagingTouchSlop);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        LogUtils.d(Utils.VIEW_DEBUGGING_TAG,
                "START CLF-ListView.onFocusChanged layoutRequested=%s root.layoutRequested=%s",
                isLayoutRequested(), getRootView().isLayoutRequested());
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        LogUtils.d(Utils.VIEW_DEBUGGING_TAG, new Error(),
                "FINISH CLF-ListView.onFocusChanged layoutRequested=%s root.layoutRequested=%s",
                isLayoutRequested(), getRootView().isLayoutRequested());
    }

    /**
     * Enable swipe gestures.
     */
    public void enableSwipe(boolean enable) {
        mEnableSwipe = enable;
    }

    public void setSwipeAction(int action) {
        mSwipeAction = action;
    }

    public void setListItemSwipedListener(ListItemSwipedListener listener) {
        mSwipedListener = listener;
    }

    public int getSwipeAction() {
        return mSwipeAction;
    }

    public void setSelectionSet(ConversationSelectionSet set) {
        mConvSelectionSet = set;
    }

    public void setCurrentAccount(Account account) {
        mAccount = account;
    }

    public void setCurrentFolder(Folder folder) {
        mFolder = folder;
    }

    @Override
    public ConversationSelectionSet getSelectionSet() {
        return mConvSelectionSet;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
      //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
        handleAnimation(ev);
      //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_E
        if (mScrolling) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return mSwipeHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
      //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
        handleAnimation(ev);
      //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_E
        return mSwipeHelper.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public View getChildAtPosition(MotionEvent ev) {
        // find the view under the pointer, accounting for GONE views
        final int count = getChildCount();
        final int touchY = (int) ev.getY();
        int childIdx = 0;
        View slidingChild;
        for (; childIdx < count; childIdx++) {
            slidingChild = getChildAt(childIdx);
            if (slidingChild.getVisibility() == GONE) {
                continue;
            }
            if (touchY >= slidingChild.getTop() && touchY <= slidingChild.getBottom()) {
                //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_S
//                if (slidingChild instanceof SwipeableConversationItemView) {
//                    return ((SwipeableConversationItemView) slidingChild).getSwipeableItemView();
//                }
                //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 MOD_E
                return slidingChild;
            }
        }
        return null;
    }

    @Override
    public boolean canChildBeDismissed(SwipeableItemView v) {
        return mEnableSwipe && v.canChildBeDismissed();
    }

    @Override
    public void onChildDismissed(SwipeableItemView v) {
        if (v != null) {
            v.dismiss();
        }
    }

    // Call this whenever a new action is taken; this forces a commit of any
    // existing destructive actions.
    public void commitDestructiveActions(boolean animate) {
        final AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter != null) {
            adapter.commitLeaveBehindItems(animate);
        }
    }

    public void dismissChild(final ConversationItemView target) {
        // Notifies the SwipeListener that a swipe has ended.
        if (mSwipeListener != null) {
            mSwipeListener.onEndSwipe();
        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 MOD_S
//        final ToastBarOperation undoOp;

//        undoOp = new ToastBarOperation(1, mSwipeAction, ToastBarOperation.UNDO, false /* batch */,
//                mFolder);
        final Conversation conv = target.getConversation();
        target.getConversation().position = findConversation(target, conv);
        final AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter == null) {
            return;
        }
//       adapter.setupLeaveBehind(conv, undoOp, conv.position, target.getHeight());
        if (mSwipeDirection == SwipeHelper.SWIPE_DIRECTION_TO_RIGHT) {
            mSwipeAction = R.id.swipe_delete;
        } else {
            if (conv.starred) {
                mSwipeAction = R.id.swipe_unstar;
            } else {
                mSwipeAction = R.id.swipe_star;
            }
        }
        SwipeableConversationItemView conversationItemView = (SwipeableConversationItemView) target.getParent();
        conversationItemView.setSwipeAction(mSwipeAction);
        conversationItemView.updateStarViewAfterStatusChange(conv.starred);
        adapter.setupSwipeItem(conv, conversationItemView);
        final ConversationCursor cc = (ConversationCursor) adapter.getCursor();
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 MOD_E
        Collection<Conversation> convList = Conversation.listOf(conv);
        ArrayList<Uri> folderUris;
        ArrayList<Boolean> adds;

        Analytics.getInstance().sendMenuItemEvent("list_swipe", mSwipeAction, null, 0);

        if (mSwipeAction == R.id.remove_folder) {
            FolderOperation folderOp = new FolderOperation(mFolder, false);
            HashMap<Uri, Folder> targetFolders = Folder
                    .hashMapForFolders(conv.getRawFolders());
            targetFolders.remove(folderOp.mFolder.folderUri.fullUri);
            final FolderList folders = FolderList.copyOf(targetFolders.values());
            conv.setRawFolders(folders);
            final ContentValues values = new ContentValues();
            folderUris = new ArrayList<Uri>();
            folderUris.add(mFolder.folderUri.fullUri);
            adds = new ArrayList<Boolean>();
            adds.add(Boolean.FALSE);
            ConversationCursor.addFolderUpdates(folderUris, adds, values);
            ConversationCursor.addTargetFolders(targetFolders.values(), values);
            cc.mostlyDestructiveUpdate(Conversation.listOf(conv), values);
        } else if (mSwipeAction == R.id.archive) {
            cc.mostlyArchive(convList);
        } else if (mSwipeAction == R.id.delete) {
            cc.mostlyDelete(convList);
        } else if (mSwipeAction == R.id.discard_outbox) {
            cc.moveFailedIntoDrafts(convList);
        } else if (mSwipeAction == R.id.swipe_delete){
            cc.mostlyDelete(convList);
         // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_S
            //If we swipe to delete a mail,then the action bar should be shown
            mActivity.animateShow(null);
         // TS: tao.gan 2015-09-21 EMAIL FEATURE-559893 ADD_E
        } else if (mSwipeAction == R.id.swipe_star) {  //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
            ContentValues values = new ContentValues(1);
            conv.starred = true;
            values.put(UIProvider.ConversationColumns.STARRED, conv.starred);
            cc.updateStar(Conversation.listOf(conv), values,  new UndoCallback() {
                @Override
                public void performUndoCallback() {
                    target.updateStarOfSelectedSet(false);
                }
            });
        } else if (mSwipeAction == R.id.swipe_unstar) {
            ContentValues values = new ContentValues(1);
            conv.starred = false;
            values.put(UIProvider.ConversationColumns.STARRED, conv.starred);
            cc.updateUnstar(Conversation.listOf(conv), values, new UndoCallback() {
                @Override
                public void performUndoCallback() {
                    target.updateStarOfSelectedSet(true);
                }
            });
        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E
        if (mSwipedListener != null) {
            mSwipedListener.onListItemSwiped(convList);
        }
        adapter.notifyDataSetChanged();
        if (mConvSelectionSet != null && !mConvSelectionSet.isEmpty()
                && mConvSelectionSet.contains(conv)) {
            mConvSelectionSet.toggle(conv);
            // Don't commit destructive actions if the item we just removed from
            // the selection set is the item we just destroyed!
            if (!conv.isMostlyDead() && mConvSelectionSet.isEmpty()) {
                commitDestructiveActions(true);
            }
        }
    }

    @Override
    public void onBeginDrag(View v) {
        // We do this so the underlying ScrollView knows that it won't get
        // the chance to intercept events anymore
        requestDisallowInterceptTouchEvent(true);
        cancelDismissCounter();

        // Notifies the SwipeListener that a swipe has begun.
        if (mSwipeListener != null) {
            mSwipeListener.onBeginSwipe();
        }
    }

    @Override
    public void onDragCancelled(SwipeableItemView v) {
        final AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter != null) {
            adapter.startDismissCounter();
            adapter.cancelFadeOutLastLeaveBehindItemText();
        }

        // Notifies the SwipeListener that a swipe has ended.
        if (mSwipeListener != null) {
            mSwipeListener.onEndSwipe();
        }
    }

    /**
     * Archive items using the swipe away animation before shrinking them away.
     */
    public boolean destroyItems(Collection<Conversation> convs,
            final ListItemsRemovedListener listener) {
        if (convs == null) {
            LogUtils.e(LOG_TAG, "SwipeableListView.destroyItems: null conversations.");
            return false;
        }
        final AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter == null) {
            LogUtils.e(LOG_TAG, "SwipeableListView.destroyItems: Cannot destroy: adapter is null.");
            return false;
        }
        adapter.swipeDelete(convs, listener);
        return true;
    }

    public int findConversation(ConversationItemView view, Conversation conv) {
        int position = INVALID_POSITION;
        long convId = conv.id;
        try {
            position = getPositionForView(view);
        } catch (Exception e) {
            position = INVALID_POSITION;
            LogUtils.w(LOG_TAG, e, "Exception finding position; using alternate strategy");
        }
        if (position == INVALID_POSITION) {
            // Try the other way!
            Conversation foundConv;
            long foundId;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof SwipeableConversationItemView) {
                    foundConv = ((SwipeableConversationItemView) child).getSwipeableItemView()
                            .getConversation();
                    foundId = foundConv.id;
                    if (foundId == convId) {
                        position = i + getFirstVisiblePosition();
                        break;
                    }
                }
            }
        }
        return position;
    }

    private AnimatedAdapter getAnimatedAdapter() {
        return (AnimatedAdapter) getAdapter();
    }

    @Override
    public boolean performItemClick(View view, int pos, long id) {
        final int previousPosition = getCheckedItemPosition();
        final boolean selectionSetEmpty = mConvSelectionSet.isEmpty();

        // Superclass method modifies the selection set
        final boolean handled = super.performItemClick(view, pos, id);

        // If we are in CAB mode then a click shouldn't
        // activate the new item, it should only add it to the selection set
        if (!selectionSetEmpty && previousPosition != -1) {
            setItemChecked(previousPosition, true);
        }
        // Commit any existing destructive actions when the user selects a
        // conversation to view.
        commitDestructiveActions(true);
        return handled;
    }

    @Override
    public void onScroll() {
        commitDestructiveActions(true);
    }

    public interface ListItemsRemovedListener {
        public void onListItemsRemoved();
    }

    public interface ListItemSwipedListener {
        public void onListItemSwiped(Collection<Conversation> conversations);
    }

    @Override
    public final void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        mScrolling = scrollState != OnScrollListener.SCROLL_STATE_IDLE;

        if (!mScrolling) {
            final Context c = getContext();
            if (c instanceof ControllableActivity) {
                final ControllableActivity activity = (ControllableActivity) c;
                activity.onAnimationEnd(null /* adapter */);
            } else {
                LogUtils.wtf(LOG_TAG, "unexpected context=%s", c);
            }
        }
        //TS: jin.dong 2015-10-16 EMAIL BUGFIX_1097364 ADD_S
        if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
            View currentFocus = findFocus();
            if (currentFocus != null) {
                //TS: chaozhang 2015-10-16 EMAIL BUGFIX_1134859 ADD_S
                currentFocus.clearFocus();
                //TS: chaozhang 2015-10-16 EMAIL BUGFIX_1134859 ADD_E
                clearChildFocus(currentFocus);
            }
        }
        //TS: jin.dong 2015-10-16 EMAIL BUGFIX_1097364 ADD_E
    }

    public boolean isScrolling() {
        return mScrolling;
    }

    @Override
    public void cancelDismissCounter() {
        AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter != null) {
            adapter.cancelDismissCounter();
        }
    }

    @Override
    public LeaveBehindItem getLastSwipedItem() {
        AnimatedAdapter adapter = getAnimatedAdapter();
        if (adapter != null) {
            return adapter.getLastLeaveBehindItem();
        }
        return null;
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    @Override
    public void setSwipeDirection(int direction) {
        mSwipeDirection = direction;
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    public void setSwipeListener(SwipeListener swipeListener) {
        mSwipeListener = swipeListener;
    }

    //TS: jin.dong 2015-10-16 EMAIL BUGFIX_1097364 ADD_S
    @Override
    public void addChildrenForAccessibility(ArrayList<View> childrenForAccessibility) {
        try {
            super.addChildrenForAccessibility(childrenForAccessibility);
        } catch (Exception e) {
            LogUtils.e(LogUtils.TAG, "addChildrenForAccessibility exception e = " + e);
        }
    }
    //TS: jin.dong 2015-10-16 EMAIL BUGFIX_1097364 ADD_E

    public interface SwipeListener {
        public void onBeginSwipe();
        public void onEndSwipe();
    }

  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_S
    /*
     * Do the animation when onTouch
     */
    public void handleAnimation(MotionEvent ev) {
        int action = ev.getAction();
        switch(action) {
        case MotionEvent.ACTION_DOWN:
            mLastY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            mCurrentY = ev.getY();
            //if move over OFFSET_DO_ACTION and the list's first item is visible ,we hide the tool bar
            if ((mLastY - mCurrentY) > Utils.OFFSET_DO_ACTION && getFirstVisiblePosition() >0) {
                mLastY = mCurrentY;
                mActivity.animateHide(mComposeButton);
            } else if ((mCurrentY - mLastY)> Utils.OFFSET_DO_ACTION) {
                mLastY = mCurrentY;
                mActivity.animateShow(mComposeButton);
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            break;
        }
    }

    public void animateShowBar() {
        mActivity.animateShow(mComposeButton);
    }
  //TS: tao.gan 2015-09-11 EMAIL FEATURE-559893 ADD_E
}
