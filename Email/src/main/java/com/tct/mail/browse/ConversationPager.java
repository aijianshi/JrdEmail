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
 *Tag		 Date	      Author		  Description
 *============== ============ =============== ==============================
 *BUGFIX-956659  2015/12/07   zheng.zou   [Android L][Email][Monkey][Crash]java.lang.IllegalStateException happened during monkey test
 ===========================================================================
 */

package com.tct.mail.browse;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.tct.mail.utils.LogUtils;

public class ConversationPager extends ViewPager {

    public ConversationPager(Context context) {
        this(context, null);
    }

    public ConversationPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // this space intentionally blank (reserved for various debugging hooks)

    //TS: zheng.zou 2015-12-07 EMAIL BUGFIX_956659 ADD_S
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //we can not fully avoid IllegalStateException when ViewPager execute populate() from here, catch it.
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalStateException e) {
            LogUtils.e(LogUtils.TAG, "IllegalStateException in ConversationPager");
            return false;
        }
    }
    //TS: zheng.zou 2015-12-07 EMAIL BUGFIX_956659 ADD_E

    //TS: jin.dong 2016-3-2 EMAIL BUGFIX_1716848 ADD_S
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            //note: catch the exception throwed from framework
            return super.dispatchKeyEvent(event);
        } catch (IndexOutOfBoundsException ex) {
            LogUtils.e(LogUtils.TAG, "IndexOutOfBoundsException in ConversationPager");
            return false;
        }
    }
    //TS: jin.dong 2016-3-2 EMAIL BUGFIX_1716848 ADD_E


    //TS: tao.gan 2016-4-8 EMAIL BUGFIX_1912699  ADD_S
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        }catch (IllegalStateException ex){
            LogUtils.e(LogUtils.TAG, "IllegalStateException(onMeasure) in ConversationPager");
        }

    //TS: tao.gan 2016-4-8 EMAIL BUGFIX_1912699  ADD_E

    }
}
