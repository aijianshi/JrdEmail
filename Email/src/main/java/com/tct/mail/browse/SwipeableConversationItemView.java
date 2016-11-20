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
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 05/29/2014|     zhonghua.tuo     |      FR 670064       |email search fun- */
/*           |                      |                      |ction             */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *CR_585337      2015-09-29  chao.zhang       Exchange Email resend mechanism
 ============================================================================
 */

package com.tct.mail.browse;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.tct.email.R;
import com.tct.emailcommon.service.SearchParams;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.ui.AnimatedAdapter;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.ui.ConversationSelectionSet;

public class SwipeableConversationItemView extends FrameLayout implements ToggleableItem {

    private final ConversationItemView mConversationItemView;
    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/29/2014,FR 670064
    private String mQueryText;
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    private final SwipeSideView mLeftSideView;
    private final SwipeSideView mRightSideView;
    private static  int sShrinkAnimationDuration =-1;
    private static  int sFadeInAnimationDuration = -1;
    private boolean mAnimating;
    private int mSwipeAction;
    private int mAnimatedHeight = -1;
    private Animator mCurrentAnimator;
    private AnimatorListener mAnimListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            setCurrentAnimator(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimating = false;
            mConversationItemView.showUndoToastBar(mSwipeAction);
            setCurrentAnimator(null);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    } ;
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    public void setQueryText(String query) {
        mQueryText = query;
    }
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo

    public SwipeableConversationItemView(Context context, String account) {
        super(context);
        mConversationItemView = new ConversationItemView(context, account);
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
        mLeftSideView = SwipeSideView.newLeftView(context);
        mRightSideView = SwipeSideView.newRightView(context);
        mLeftSideView.setVisibility(GONE);
        mRightSideView.setVisibility(GONE);
        addView(mLeftSideView);
        addView(mRightSideView);
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
        addView(mConversationItemView);
        loadStatics(context);  //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD
    }

    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    private static void loadStatics(final Context context) {
        if (sShrinkAnimationDuration == -1) {
            Resources res = context.getResources();
            sShrinkAnimationDuration = res.getInteger(R.integer.shrink_animation_duration);
            sFadeInAnimationDuration = res.getInteger(R.integer.fade_in_animation_duration);
        }
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    public ListView getListView() {
        return (ListView) getParent();
    }

    public void reset() {
        mAnimatedHeight = -1;   //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD
        mConversationItemView.reset();
    }

    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    public void setSwipeAction(int action){
        mSwipeAction = action;
    }

    public int getSwipeAction(){
        return mSwipeAction;
    }

    public Conversation getConversation() {
        return mConversationItemView.getConversation();
    }

    /**
     * update star img before the star status is changed
     * @param star star status
     */
    public void updateStarViewAfterStatusChange(boolean star){
        mRightSideView.updateStarAfterStatusChange(star);
    }


    /**
     * Animate shrinking the height of this view.
     * @param listener the method to call when the animation is done
     */
    public void startShrinkAnimation(AnimatorListener listener) {
        if (!mAnimating){
             mAnimating = true;
            ObjectAnimator height = ObjectAnimator.ofInt(this, "animatedHeight", getHeight(), 0);
            setMinimumHeight(getHeight());
            height.setInterpolator(new DecelerateInterpolator(2.0f));
            height.setDuration(sShrinkAnimationDuration);
            height.addListener(listener);
            height.addListener(mAnimListener);
            height.start();
        }
    }

    /**
     * Animate alpha of this view.
     * @param listener the method to call when the animation is done
     */
    public void startFadeInAnimation(AnimatorListener listener){
        if (!mAnimating){
             mAnimating = true;
            Animator fade = ObjectAnimator.ofFloat(mConversationItemView, "alpha", 0, 1.0f);
            fade.setDuration(sFadeInAnimationDuration);
            fade.addListener(listener);
            fade.addListener(mAnimListener);
            fade.start();
        }
    }
    private void setCurrentAnimator(Animator animator){
        mCurrentAnimator = animator;
    }

    public boolean isAnimating(){
        return mAnimating;
    }


    public void stopAnimation(){
        if (mCurrentAnimator!=null){
            mCurrentAnimator.cancel();
        }
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    public ConversationItemView getSwipeableItemView() {
        return mConversationItemView;
    }

    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    public SwipeSideView getLeftSideView(){
        return mLeftSideView;
    }

    public SwipeSideView getRightSideView(){
        return mRightSideView;
    }

    // Used by animator
    @SuppressWarnings("unused")
    public void setAnimatedHeight(int height) {
        mAnimatedHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAnimatedHeight != -1) {
            setMeasuredDimension(getWidth(), mAnimatedHeight);
        } else {
            // override the height MeasureSpec to ensure this is sized up at the desired height
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    public void bind(final Conversation conversation, final ControllableActivity activity,
            final ConversationSelectionSet set, final Folder folder,
            final int checkboxOrSenderImage, boolean swipeEnabled,
            final boolean importanceMarkersEnabled, final boolean showChevronsEnabled,
            final AnimatedAdapter animatedAdapter,/**TCT:*/ SearchParams searchParams) {
        // Only enable delete for failed items in the Outbox.
        // Necessary to do it here because Outbox is the only place where we selectively enable
        // swipe on a item-by-item basis.
        // TS: chao.zhang 2015-09-29 EMAIL FEATURE-585337 MOD_S
        if (folder != null && folder.isType(UIProvider.FolderType.OUTBOX)) {
            swipeEnabled &=
                    conversation.sendingState != UIProvider.ConversationSendingState.SENDING;
                   // &&conversation.sendingState != UIProvider.ConversationSendingState.RETRYING;
        }
        // TS: chao.zhang 2015-09-29 EMAIL FEATURE-585337 MOD_E
        mConversationItemView.bind(conversation, activity, set, folder, checkboxOrSenderImage,
                swipeEnabled, importanceMarkersEnabled, showChevronsEnabled, animatedAdapter,/**TCT:*/searchParams);
        mRightSideView.updateStarBeforeStatusChange(conversation.starred);   //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD
    }

    public void startUndoAnimation(AnimatorListener listener, boolean swipe) {
        final Animator a = (swipe) ? mConversationItemView.createSwipeUndoAnimation()
                : mConversationItemView.createUndoAnimation();
        a.addListener(listener);
        a.start();
    }

    public void startDeleteAnimation(AnimatorListener listener, boolean swipe) {
        final Animator a = (swipe) ? mConversationItemView.createDestroyWithSwipeAnimation()
                : mConversationItemView.createDestroyAnimation();
        a.addListener(listener);
        a.start();
    }

    @Override
    public boolean toggleSelectedStateOrBeginDrag() {
        return mConversationItemView.toggleSelectedStateOrBeginDrag();
    }

    @Override
    public boolean toggleSelectedState() {
        return mConversationItemView.toggleSelectedState();
    }
}
