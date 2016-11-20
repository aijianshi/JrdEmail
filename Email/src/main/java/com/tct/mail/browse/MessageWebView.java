/*
 * Copyright (C) 2016 Google Inc.
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
 *BUGFIX-963186  2015/4/16    yanhua.chen     Android5.0][Email] [UI] Status bar does not change when selecting characters when editing a mail
 ===========================================================================
 */
package com.tct.mail.browse;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.Clock;
import com.tct.mail.utils.Throttle;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
import com.tct.email.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E
/**
 * A WebView designed to live within a {@link MessageScrollView}.
 */
public class MessageWebView extends WebView implements MessageScrollView.Touchable {

    private static final String LOG_TAG = LogTag.getLogTag();

    private static Handler sMainThreadHandler;

    private boolean mTouched;

    private static final int MIN_RESIZE_INTERVAL = 200;
    private static final int MAX_RESIZE_INTERVAL = 300;
    private final Clock mClock = Clock.INSTANCE;

    private final Throttle mThrottle = new Throttle("MessageWebView",
            new Runnable() {
                @Override public void run() {
                    performSizeChangeDelayed();
                }
            }, getMainThreadHandler(),
            MIN_RESIZE_INTERVAL, MAX_RESIZE_INTERVAL);

    private int mRealWidth;
    private int mRealHeight;
    private boolean mIgnoreNext;
    private long mLastSizeChangeTime = -1;

    //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
    private Activity activity;
    //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E
    public MessageWebView(Context c) {
        this(c, null);
    }

    public MessageWebView(Context c, AttributeSet attrs) {
        super(c, attrs);
        //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
        activity = (Activity)c;
        //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E
    }

    @Override
    public boolean wasTouched() {
        return mTouched;
    }

    @Override
    public void clearTouched() {
        mTouched = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouched = true;
        final boolean handled = super.onTouchEvent(event);
        LogUtils.d(MessageScrollView.LOG_TAG,"OUT WebView.onTouch, returning handled=%s ev=%s",
                handled, event);
        return handled;
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        mRealWidth = w;
        mRealHeight = h;
        final long now = mClock.getTime();
        boolean recentlySized = (now - mLastSizeChangeTime < MIN_RESIZE_INTERVAL);

        // It's known that the previous resize event may cause a resize event immediately. If
        // this happens sufficiently close to the last resize event, drop it on the floor.
        if (mIgnoreNext) {
            mIgnoreNext = false;
            if (recentlySized) {
                    LogUtils.w(LOG_TAG, "Suppressing size change in MessageWebView");
                return;
            }
        }

        if (recentlySized) {
            mThrottle.onEvent();
        } else {
            // It's been a sufficiently long time - just perform the resize as normal. This should
            // be the normal code path.
            performSizeChange(ow, oh);
        }
    }

    private void performSizeChange(int ow, int oh) {
        super.onSizeChanged(mRealWidth, mRealHeight, ow, oh);
        mLastSizeChangeTime = mClock.getTime();
    }

    private void performSizeChangeDelayed() {
        mIgnoreNext = true;
        performSizeChange(getWidth(), getHeight());
    }

    /**
     * @return a {@link Handler} tied to the main thread.
     */
    public static Handler getMainThreadHandler() {
        if (sMainThreadHandler == null) {
            // No need to synchronize -- it's okay to create an extra Handler, which will be used
            // only once and then thrown away.
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return sMainThreadHandler;
    }

  //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
    @Override
    public ActionMode startActionMode(Callback callback) {
        changeStatusBarColor();
        return super.startActionMode(callback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor() {
        if(activity != null && activity.getWindow() != null){
             activity.getWindow().setStatusBarColor(getResources().getColor(R.color.change_status_bar));
        }
    }
  //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E
}
