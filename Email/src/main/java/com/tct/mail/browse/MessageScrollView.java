/*
 * Copyright (C) 2013 Google Inc.
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
 ===========================================================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== =============================================================
 *BUGFIX-1052793  2015/07/28   zheng.zou    [Android5.0][Email] mail content disappears when sliding up screen in combined view
 *BUGFIX-1054161  2015/07/31  jin.dong        [Android5.0][Email] [Monkey][Crash][Monitor]com.tct email carshs at java.lang.IndexOutOfBoundsException
 *BUGFIX-1052793  2015/08/07   zheng.zou    [Android5.0][Email] mail content disappears when sliding up screen in combined view
 *FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
 *FEATURE-854258 2015/11/11   Gantao          [Android L][Email]There are no buttons for reply/reply all/forward at the end of mail in combine account
 ==========================================================================================================
 */

package com.tct.mail.browse;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.widget.ScrollView;
import android.view.View;

import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A container that tries to play nice with an internally scrollable {@link Touchable} child view.
 * The assumption is that the child view can scroll horizontally, but not vertically, so any
 * touch events on that child view should ALSO be sent here so it can simultaneously vertically
 * scroll (not the standard either/or behavior).
 * <p>
 * Touch events on any other child of this ScrollView are intercepted in the standard fashion.
 */
public class MessageScrollView extends ScrollView implements ScrollNotifier,
        ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnDoubleTapListener {

    /**
     * A View that reports whether onTouchEvent() was recently called.
     */
    public interface Touchable {
        boolean wasTouched();
        void clearTouched();
        boolean zoomIn();
        boolean zoomOut();
    }

    /**
     * True when performing "special" interception.
     */
    private boolean mWantToIntercept;
    /**
     * Whether to perform the standard touch interception procedure. This is set to true when we
     * want to intercept a touch stream from any child OTHER than {@link #mTouchableChild}.
     */
    private boolean mInterceptNormally;
    /**
     * The special child that we want to NOT intercept from in the normal way. Instead, this child
     * will continue to receive the touch event stream (so it can handle the horizontal component)
     * while this parent will additionally handle the events to perform vertical scrolling.
     */
    private Touchable mTouchableChild;

    /**
     * We want to detect the scale gesture so that we don't try to scroll instead, but we don't
     * care about actually interpreting it because the webview does that by itself when it handles
     * the touch events.
     *
     * This might lead to really weird interactions if the two gesture detectors' implementations
     * drift...
     */
    private ScaleGestureDetector mScaleDetector;
    private boolean mInScaleGesture;

    /**
     * We also want to detect double-tap gestures, but in a way that doesn't conflict with
     * tap-tap-drag gestures
     */
    private GestureDetector mGestureDetector;
    private boolean mDoubleTapOccurred;
    private boolean mZoomedIn;

    /**
     * Touch slop used to determine if this double tap is valid for starting a scale or should be
     * ignored.
     */
    private int mTouchSlopSquared;

    /**
     * X and Y coordinates for the current down event. Since mDoubleTapOccurred only contains the
     * information that there was a double tap event, use these to get the secondary tap
     * information to determine if a user has moved beyond touch slop.
     */
    private float mDownFocusX;
    private float mDownFocusY;
    // TS: zheng.zou 2015-07-28 EMAIL BUGFIX_1052793 ADD_S
    private static final float MIN_SNAP_VEL = 400;
    private static final int MIN_SNAP_SCOPE = 10;   // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 ADD
    private boolean mIsVerticalDownFling;
    // TS: zheng.zou 2015-07-28 EMAIL BUGFIX_1052793 ADD_E
    // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 ADD_S
    private boolean mIsVerticalDownMove;
    private boolean mIsTouchedOutside;
    private float mLastY;
    private float mLastX;
    private int mMultiTouchedCount;
    private int[] mLocation = new int[2];
    // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 ADD_E

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    private ControllableActivity mActivity;
    private ConversationReplyFabView mFabButton;
    private int mActionDownY;
    private int mToolbarHeight;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
    //animator when hide view
    private AnimatorSet hideAnimatorSet;
    //animator when show view
    private AnimatorSet backAnimatorSet;
    private boolean mFooterHidden =false;
    private ConversationFooterView mConversationFooterView;
 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E
    //Whether scroll view is scroll to bottom, if so,don't show toolbar and fab.
    private boolean isScrollToBottom = false;

    private final Set<ScrollListener> mScrollListeners =
            new CopyOnWriteArraySet<ScrollListener>();

    public static final String LOG_TAG = "MsgScroller";

    public MessageScrollView(Context c) {
        this(c, null);
    }

    public MessageScrollView(Context c, AttributeSet attrs) {
        super(c, attrs);
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        //Set the activity and the toolbar height
        if(c instanceof ControllableActivity) {
            mActivity = (ControllableActivity)c;
            mToolbarHeight = mActivity.getToolbar().getHeight();
        }
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        final int touchSlop = ViewConfiguration.get(c).getScaledTouchSlop();
        mTouchSlopSquared = touchSlop * touchSlop;
        mScaleDetector = new ScaleGestureDetector(c, this);
        mGestureDetector = new GestureDetector(c, new GestureDetector.SimpleOnGestureListener(){
            // TS: zheng.zou 2015-07-28 EMAIL BUGFIX_1052793 ADD_S
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //only intercept the vertical downward fast scroll
                if (velocityY < - MIN_SNAP_VEL) {
                    mIsVerticalDownFling = true;
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
              //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
                mActionDownY = getScrollY();
              //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
                mIsVerticalDownFling = false;
                return super.onDown(e);
            }
        });
        // TS: zheng.zou 2015-07-28 EMAIL BUGFIX_1052793 ADD_E
        mGestureDetector.setOnDoubleTapListener(this);
    }

    public void setInnerScrollableView(Touchable child) {
        mTouchableChild = child;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

         calculateIntercept(ev);  // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 ADD

        // TS: zheng.zou 2015-07-28 EMAIL BUGFIX_1052793 ADD_S
        //intercept the vertical downward fast scroll, to let the ScrollView to handle the fling solely,
        //to forbid the child WebView to handle the scroll at the same time.
        //three conditions should be intercept: 1.fast fling down 2. slow move down 3.multi touch and one finger is outside the webview
        if (mIsVerticalDownFling || mIsVerticalDownMove || (mIsTouchedOutside && mMultiTouchedCount > 1)) {   // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 MOD
            return true;
        }
        // TS: zheng.zou 2015-07-28 EMAIL BUGFIX_1052793 ADD_E
        if (mInterceptNormally) {
            LogUtils.d(LOG_TAG, "IN ScrollView.onIntercept, NOW stealing. ev=%s", ev);
            return true;
        } else if (mWantToIntercept) {
            LogUtils.d(LOG_TAG, "IN ScrollView.onIntercept, already stealing. ev=%s", ev);
            return false;
        }

        mWantToIntercept = super.onInterceptTouchEvent(ev);
        LogUtils.d(LOG_TAG, "OUT ScrollView.onIntercept, steal=%s ev=%s", mWantToIntercept, ev);
        return false;
    }

    // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 ADD_S
    private void calculateIntercept(MotionEvent ev) {

        final int action = ev.getActionMasked();
        float x = ev.getX();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsVerticalDownMove = false;
                mLastY = y;
                mLastX = x;
                mMultiTouchedCount = 1;
                if (mTouchableChild != null && mTouchableChild instanceof View) {
                    mIsTouchedOutside = !isPointInsideView(ev.getX(), ev.getY(), (View) mTouchableChild);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (mLastY - y);
                int deltaX = (int) (mLastX - x);
                //1. single touch 2.downward
                mIsVerticalDownMove = mMultiTouchedCount < 2 && deltaY > MIN_SNAP_SCOPE && Math.abs(deltaX) < Math.abs(deltaY);
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mTouchableChild != null && mTouchableChild instanceof View) {
                    mIsTouchedOutside = mIsTouchedOutside || !isPointInsideView(ev.getX(mMultiTouchedCount), ev.getY(mMultiTouchedCount), (View) mTouchableChild);
                }
                mMultiTouchedCount++;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mMultiTouchedCount--;
                break;
            case MotionEvent.ACTION_UP:
                mMultiTouchedCount = 0;
                mIsTouchedOutside = false;
                break;
        }
    }


    public boolean isPointInsideView(float x, float y, View view) {
        view.getLocationOnScreen(mLocation);
        int viewX = mLocation[0];
        int viewY = mLocation[1];
        getLocationOnScreen(mLocation);
        y += mLocation[1];   //translate the relative position to abstract location on screen

        //point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }
    // TS: zheng.zou 2015-08-07 EMAIL BUGFIX_1052793 ADD_E

    // TS: jin.dong 2015-07-31 EMAIL BUGFIX_1054161 ADD_S
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (IndexOutOfBoundsException e) {
            LogUtils.d(LOG_TAG, e, "IndexOutOfBoundsException occured");
        }
        return false;
    }
    // TS: jin.dong 2015-07-31 EMAIL BUGFIX_1054161 ADD_E

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d(LOG_TAG, "IN ScrollView.dispatchTouch, clearing flags");
                mWantToIntercept = false;
                mInterceptNormally = false;
                break;
        }
        if (mTouchableChild != null) {
            mTouchableChild.clearTouched();
        }

        mScaleDetector.onTouchEvent(ev);
        mGestureDetector.onTouchEvent(ev);
        //[BUGFIX]-Add-BEGIN by SCDTABLET.qiao-yang@tcl.com,04/29/2016,2004969,
        boolean handled = false;
        try {
            handled = super.dispatchTouchEvent(ev);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        //[BUGFIX]-Add-END by SCDTABLET.qiao-yang@tcl.com
        LogUtils.d(LOG_TAG, "OUT ScrollView.dispatchTouch, handled=%s ev=%s", handled, ev);

        if (mWantToIntercept && !mInScaleGesture) {
            final boolean touchedChild = (mTouchableChild != null && mTouchableChild.wasTouched());
            if (touchedChild) {
                // also give the event to this scroll view if the WebView got the event
                // and didn't stop any parent interception
                LogUtils.d(LOG_TAG, "IN extra ScrollView.onTouch, ev=%s", ev);
                onTouchEvent(ev);
            } else {
                mInterceptNormally = true;
                mWantToIntercept = false;
            }
        }

        return handled;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        LogUtils.d(LOG_TAG, "Begin scale gesture");
        mInScaleGesture = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        LogUtils.d(LOG_TAG, "End scale gesture");
        mInScaleGesture = false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mDoubleTapOccurred = true;
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        final int action = e.getAction();
        boolean handled = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownFocusX = e.getX();
                mDownFocusY = e.getY();
                break;
            case MotionEvent.ACTION_UP:
                handled = triggerZoom();
                break;
            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (e.getX() - mDownFocusX);
                final int deltaY = (int) (e.getY() - mDownFocusY);
                int distance = (deltaX * deltaX) + (deltaY * deltaY);
                if (distance > mTouchSlopSquared) {
                    mDoubleTapOccurred = false;
                }
                break;

        }
        return handled;
    }

    private boolean triggerZoom() {
        boolean handled = false;
        if (mDoubleTapOccurred) {
            if (mZoomedIn) {
                mTouchableChild.zoomOut();
            } else {
                mTouchableChild.zoomIn();
            }
            mZoomedIn = !mZoomedIn;
            LogUtils.d(LogUtils.TAG, "Trigger Zoom!");
            handled = true;
        }
        mDoubleTapOccurred = false;
        return handled;
    }

    @Override
    public void addScrollListener(ScrollListener l) {
        mScrollListeners.add(l);
    }

    @Override
    public void removeScrollListener(ScrollListener l) {
        mScrollListeners.remove(l);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
        if(t + getHeight() >= computeVerticalScrollRange()) {
            isScrollToBottom = true;
            animateShowFooter(false);
        } else {
            isScrollToBottom = false;
            animateHideFooter(false);
        }

        if ((t-mActionDownY) > Utils.OFFSET_DO_ACTION && t > mToolbarHeight) {//Only we scroll to hide the toolbar height then we hide the bar
            if (mActivity != null) {
                mActivity.animateHide(mFabButton);
            }
        } else if ((mActionDownY - t) > Utils.OFFSET_DO_ACTION && !isScrollToBottom) {
            if (mActivity != null) {
                mActivity.animateShow(mFabButton);
            }
        }
        mActionDownY = t;
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        super.onScrollChanged(l, t, oldl, oldt);
        for (ScrollListener listener : mScrollListeners) {
            listener.onNotifierScroll(t);
        }
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    public void setFabButton (ConversationReplyFabView fabButton) {
        mFabButton = fabButton;
    }
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_S
    public void setConversationFooterView(ConversationFooterView footerView) {
        mConversationFooterView = footerView;
    }

    /*
     * Hide the footer view,show the fab
     */
    public void animateHideFooter(boolean initalize) {
      //Remove other animations first
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
          backAnimatorSet.cancel();
        }
      if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
        //If the animation is running, do nothing.
      } else {
          if(mFooterHidden && !initalize) {
              //the footer is hidden, no need to do the animation
              return;
          }
        hideAnimatorSet = new AnimatorSet();
        //Hide the footer view
        ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(mConversationFooterView, "translationY", mConversationFooterView.getTranslationY(), mConversationFooterView.getBottom()+mConversationFooterView.getHeight());
          ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(mFabButton, "translationY", mFabButton.getTranslationY(), 0f);
        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(footerAnimator);
        animators.add(fabAnimator);
        hideAnimatorSet.setDuration(200);
        hideAnimatorSet.playTogether(animators);
        hideAnimatorSet.start();
        mFooterHidden = true;
      }
    }

    /*
     * Show the footer view, hide the fab
     */
    public void animateShowFooter(boolean initalize) {
      //Remove other animation.
       if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
         hideAnimatorSet.cancel();
       }
       if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
           //If the animation is running,do nothing.
         } else {
             if(!mFooterHidden && !initalize) {
                 //the footer view is showing, no need to do the animation.
                 return;
             }
           backAnimatorSet = new AnimatorSet();
           //Show the footer view
           ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(mConversationFooterView, "translationY", mConversationFooterView.getTranslationY(), 0f);
           ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(mFabButton, "translationY",mFabButton.getTranslationY(), ((View)mFabButton.getParent()).getHeight());
           ArrayList<Animator> animators = new ArrayList<>();
           animators.add(footerAnimator);
           animators.add(fabAnimator);
           backAnimatorSet.setDuration(200);
           backAnimatorSet.playTogether(animators);
           backAnimatorSet.start();
           mFooterHidden = false;
         }
    }

    /*
     * Judge if the scroll view is bottom.
     */
    public boolean isBottom() {
        boolean isBottom = ((getScrollY() + getHeight()) > computeVerticalScrollRange());
        return isBottom;
    }
 // TS: tao.gan 2015-11-11 EMAIL FEATURE-854285 ADD_E
}
