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
 *BUGFIX-932165  2015/03/13   zhaotianyong    [5.0][Email] some email body font is too small to recognize
 *BUGFIX-919767  2015/3/25  junwei-xu         [Android5.0][Email] [UI] Status bar does not change when selecting characters in mail content
 *FEATURE-559891 2015/09/10   tao.gan         [Email] Auto hiding aciont bar in mail content UI
 ===========================================================================
 */

package com.tct.mail.browse;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.tct.email.R;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Utils;
//TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_S
//TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_E
public class ConversationWebView extends MailWebView implements ScrollNotifier {
    /** The initial delay when rendering in hardware layer. */
    private final int mWebviewInitialDelay;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private boolean mUseSoftwareLayer;
    /**
     * Whether this view is user-visible; we don't bother doing supplemental software drawing
     * if the view is off-screen.
     */
    private boolean mVisible;
    // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
    private long lastClick;
    private long currentClick;
    private boolean mZoomedIn = false;
    // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E
  //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_S
    private Activity activity;
  //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_E

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    private int mActionDownY = 0;
    private int mToolbarHeight;
    private ImageButton mFabButton;
    private ConversationViewAdapter mAdapter;
    //animator when show view
    private AnimatorSet backAnimatorSet;
    //animator when hide view
    private AnimatorSet hideAnimatorSet;
    //indicate that whether the footer is hidden
    private boolean mFooterHidden;
    //indicate that whether the fab is hidden
    private boolean mFabHidden;
    //indicate that last position is bottom of webview
    private boolean mIsBottom;
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E

    /** {@link Runnable} to be run when the page is rendered in hardware layer. */
    private final Runnable mNotifyPageRenderedInHardwareLayer = new Runnable() {
        @Override
        public void run() {
            // Switch to hardware layer.
            mUseSoftwareLayer = false;
            destroyBitmap();
            invalidate();
        }
    };

    @Override
    public void onDraw(Canvas canvas) {
        // Always render in hardware layer to avoid flicker when switch.
        super.onDraw(canvas);

        // Render in software layer on top if needed, and we're visible (i.e. it's worthwhile to
        // do all this)
        if (mUseSoftwareLayer && mVisible && getWidth() > 0 && getHeight() > 0) {
            if (mBitmap == null) {
                try {
                    // Create an offscreen bitmap.
                    mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
                    mCanvas = new Canvas(mBitmap);
                } catch (OutOfMemoryError e) {
                    // just give up
                    mBitmap = null;
                    mCanvas = null;
                    mUseSoftwareLayer = false;
                }
            }

            if (mBitmap != null) {
                final int x = getScrollX();
                final int y = getScrollY();

                mCanvas.save();
                mCanvas.translate(-x, -y);
                super.onDraw(mCanvas);
                mCanvas.restore();

                canvas.drawBitmap(mBitmap, x, y, null /* paint */);
            }
        }
    }

    @Override
    public void destroy() {
        destroyBitmap();
        removeCallbacks(mNotifyPageRenderedInHardwareLayer);

        super.destroy();
    }

    /**
     * Destroys the {@link Bitmap} used for software layer.
     */
    private void destroyBitmap() {
        if (mBitmap != null) {
            mBitmap = null;
            mCanvas = null;
        }
    }

    /**
     * Enable this WebView to also draw to an internal software canvas until
     * {@link #onRenderComplete()} is called. The software draw will happen every time
     * a normal {@link #onDraw(Canvas)} happens, and will overwrite whatever is normally drawn
     * (i.e. drawn in hardware) with the results of software rendering.
     * <p>
     * This is useful when you know that the WebView draws sooner to a software layer than it does
     * to its normal hardware layer.
     */
    public void setUseSoftwareLayer(boolean useSoftware) {
        mUseSoftwareLayer = useSoftware;
    }

    /**
     * Notifies the {@link ConversationWebView} that it has become visible. It can use this signal
     * to switch between software and hardware layer.
     */
    public void onRenderComplete() {
        if (mUseSoftwareLayer) {
            // Schedule to switch from software layer to hardware layer in 1s.
            postDelayed(mNotifyPageRenderedInHardwareLayer, mWebviewInitialDelay);
        }
    }

    public void onUserVisibilityChanged(boolean visible) {
        mVisible = visible;
    }

    private final int mViewportWidth;
    private final float mDensity;

    private final Set<ScrollListener> mScrollListeners =
            new CopyOnWriteArraySet<ScrollListener>();

    /**
     * True when WebView is handling a touch-- in between POINTER_DOWN and
     * POINTER_UP/POINTER_CANCEL.
     */
    private boolean mHandlingTouch;
    private boolean mIgnoringTouch;

    private static final String LOG_TAG = LogTag.getLogTag();

    public ConversationWebView(Context c) {
        this(c, null);
    }

    public ConversationWebView(Context c, AttributeSet attrs) {
        super(c, attrs);

        final Resources r = getResources();
        mViewportWidth = r.getInteger(R.integer.conversation_webview_viewport_px);
        mWebviewInitialDelay = r.getInteger(R.integer.webview_initial_delay);
        mDensity = r.getDisplayMetrics().density;
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
        if ((t-mActionDownY) > Utils.OFFSET_DO_ACTION && t > mToolbarHeight) {//Only we scroll up to the toolbar's height then we hide the bar
            if (activity != null && activity instanceof ControllableActivity) {
                ControllableActivity cActivity = (ControllableActivity) activity;
                mActionDownY = t;
                cActivity.animateHide(mFabButton);
                mFabHidden = true;
            }
        } else if ((mActionDownY - t) > Utils.OFFSET_DO_ACTION) {
            if (activity != null && activity instanceof ControllableActivity) {
                ControllableActivity cActivity = (ControllableActivity) activity;
                mActionDownY = t;
                cActivity.animateShow(mFabButton);
                mFabHidden = false;
            }
        }
        if (isScrollToBottom()) {
            //When the webview scroll to the bottom,should show the footer view and hide the fab button
            animateBottom(false);
            mIsBottom = true;
        } else if(mIsBottom) {
            //When webview scorll up from bottom,should hide the footer view right now
            animateHideFooter();
            mIsBottom = false;
        }
      //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
        super.onScrollChanged(l, t, oldl, oldt);

        for (ScrollListener listener : mScrollListeners) {
            listener.onNotifierScroll(t);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mHandlingTouch = true;
          //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
            mActionDownY = getScrollY();
          //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
            // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
            lastClick = currentClick;
            currentClick = System.currentTimeMillis();
            if ((currentClick - lastClick) < 300) {
                return mIgnoringTouch || triggerZoom();
            }
            // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            LogUtils.d(LOG_TAG, "WebView disabling intercepts: POINTER_DOWN");
            requestDisallowInterceptTouchEvent(true);
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mHandlingTouch = false;
            mIgnoringTouch = false;
            break;
        }

        final boolean handled = mIgnoringTouch || super.onTouchEvent(ev);

        return handled;
    }

    // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_S
    private boolean triggerZoom() {
        if (mZoomedIn) {
            zoomOut();
        } else {
            zoomIn();
        }
        mZoomedIn = !mZoomedIn;
        return true;
    }
    // TS: zhaotianyong 2015-03-13 EMAIL BUGFIX-932165 ADD_E

    public boolean isHandlingTouch() {
        return mHandlingTouch;
    }

    public int getViewportWidth() {
        return mViewportWidth;
    }

    /**
     * Returns the effective width available for HTML content in DP units. This width takes into
     * account the given margin (in screen px) by excluding it. This is not the same as DOM width,
     * since the document is rendered at CSS px={@link #mViewportWidth}.
     *
     * @param sideMarginPx HTML body margin, if any (in screen px)
     * @return width available for HTML content (in dp)
     */
    public int getWidthInDp(int sideMarginPx) {
        return (int) ((getWidth() - sideMarginPx * 2) / mDensity);
    }

    /**
     * Similar to {@link #getScale()}, except that it returns the initially expected scale, as
     * determined by the ratio of actual screen pixels to logical HTML pixels.
     * <p>This assumes that we are able to control the logical HTML viewport with a meta-viewport
     * tag.
     */
    public float getInitialScale() {
        final float scale;
        if (getSettings().getLoadWithOverviewMode()) {
            // in overview mode (aka auto-fit mode), the base ratio is screen px : viewport px
            scale = (float) getWidth() / getViewportWidth();
        } else {
            // in no-zoom mode, the base ratio is just screen px : mdpi css px (i.e. density)
            scale = mDensity;
        }
        return scale;
    }

    public int screenPxToWebPx(int screenPx) {
        return (int) (screenPx / getInitialScale());
    }

    public int webPxToScreenPx(int webPx) {
        return (int) (webPx * getInitialScale());
    }

    public float screenPxToWebPxError(int screenPx) {
        return screenPx / getInitialScale() - screenPxToWebPx(screenPx);
    }

    public float webPxToScreenPxError(int webPx) {
        return webPx * getInitialScale() - webPxToScreenPx(webPx);
    }

  //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_S
    @Override
    public ActionMode startActionMode(Callback callback) {
        // TODO Auto-generated method stub
        ChangeStatusBarColor();
        return super.startActionMode(callback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ChangeStatusBarColor() {
        // TODO Auto-generated method stub
        if(activity!=null){
            activity.getWindow().setStatusBarColor(Color.parseColor("#757575"));
        }
    }

    public void setActivity(Activity activity1){
        activity = activity1;
    }

    public Activity getActivity(){
        return activity;
    }
  //TS: junwei-xu 2015-3-25 EMAIL BUGFIX_919767 ADD_E

  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_S
    public void setToolbarHeight(int toolbarHeight) {
        mToolbarHeight = toolbarHeight;
    }

    public void setFabButton (ImageButton fabButton) {
        mFabButton = fabButton;
    }

    /*
     * Check if the webview scroll to the bottom
     */
    public boolean isScrollToBottom() {
        //When the distance is less than 5, we think it scrolls to bottom
        return Math.abs(getContentHeight() * getScale() - (getHeight() + getScrollY())) < 5;
    }

    /*
     * Check if the webview is on the bottom when initialized
     */
    public boolean isInitializedBottom() {
        return (getContentHeight() * getScale() - (getHeight() + getScrollY())) < 5;
    }

    public void setAdapter(ConversationViewAdapter adapter) {
        mAdapter = adapter;
    }

    private ConversationFooterView getFooterView() {
        return mAdapter.getConversationFooterView();
    }

    /*
     * Show the footer view and hide the fab button
     * @parameter initialize, when initialize the mail content view, and need show footer view(hide the FAB)
     * we should do it anyway
     */
    public void animateBottom(boolean initialize) {
      //Remove other animation.
      if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
        hideAnimatorSet.cancel();
      }
      if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
        //If the animation is running,do nothing.
      } else {
          if(!mFooterHidden && mFabHidden && !initialize) {
              //the footer view is showing and fab button is hidden, no need to do the animation.
              return;
          }
        backAnimatorSet = new AnimatorSet();
        //Show the footer view
        ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(getFooterView(), "translationY", getFooterView().getTranslationY(), 0f);
        //hide the fabButton
        ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(mFabButton, "translationY", mFabButton.getTranslationY(), mFabButton.getHeight()+mFabButton.getBottom());
        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(footerAnimator);
        animators.add(fabAnimator);
        backAnimatorSet.setDuration(200);
        backAnimatorSet.playTogether(animators);
        backAnimatorSet.start();
        mFooterHidden = false;
        mFabHidden = true;
      }
    }

    /*
     * Hide the footer view
     */
    public void animateHideFooter() {
      //Remove other animations first
      if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
        backAnimatorSet.cancel();
      }
      if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
        //If the animation is running, do nothing.
      } else {
          if(mFooterHidden && !mFabHidden) {
              //the footer is hidden, no need to do the animation
              return;
          }
        hideAnimatorSet = new AnimatorSet();
        //Hide the footer view
        ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(getFooterView(), "translationY", getFooterView().getTranslationY(), getContentHeight()+getFooterView().getHeight());
        //Show the fabButton
        ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(mFabButton, "translationY", mFabButton.getTranslationY(), 0f);
        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(footerAnimator);
        animators.add(fabAnimator);
        hideAnimatorSet.setDuration(200);
        hideAnimatorSet.playTogether(animators);
        hideAnimatorSet.start();
        mFooterHidden = true;
        mFabHidden = false;
      }
    }
  //TS: tao.gan 2015-09-10 EMAIL FEATURE-559891 ADD_E
}
