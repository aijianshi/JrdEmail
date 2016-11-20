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
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-898644  2015/1/13    junwei-xu       [HOMO][W36][IOT8][FSR][Email]Add Cyrillic alphabet support for contact pictures
 *BUGFIX-926747  2015/02/11   chenyanhua      [5.0][Email] notification icon is not readable
 *BUGFIX-936675  2015/03/01   zhonghua.tuo    [5.0][Email] [Email]Forceclose when access mailbox
 *BUGFIX-940958  2015/03/12   zhonghua.tuo    [Scenario Test][Email]In email detail UI every contact icon has the same color
 ============================================================================
 */
package com.tct.mail.photomanager;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

import com.tct.email.R;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.ui.ImageCanvas.Dimensions;
import com.tct.mail.utils.BitmapUtil;

/**
 * LetterTileProvider is an implementation of the DefaultImageProvider. When no
 * matching contact photo is found, and there is a supplied displayName or email
 * address whose first letter corresponds to an English alphabet letter (or
 * number), this method creates a bitmap with the letter in the center of a
 * tile. If there is no English alphabet character (or digit), it creates a
 * bitmap with the default contact avatar.
 */
@Deprecated
public class LetterTileProvider {
    private static final String TAG = LogTag.getLogTag();
    private final Bitmap mDefaultBitmap;
    // TS: chenyanhua 2015-02-11 EMAIL BUGFIX-926747 ADD_S
    private final Bitmap mBitmap;
    // TS: chenyanhua 2015-02-11 EMAIL BUGFIX-926747 ADD_E
    private final Bitmap[] mBitmapBackgroundCache;
    private final Bitmap[] mDefaultBitmapCache;
    private final Typeface mSansSerifLight;
    private final Rect mBounds;
    private final int mTileLetterFontSize;
    private final int mTileLetterFontSizeSmall;
    private final int mTileFontColor;
    private final TextPaint mPaint = new TextPaint();
    private final TypedArray mColors;
    private final int mColorCount;
    private final int mDefaultColor;
    private final Canvas mCanvas = new Canvas();
    private final Dimensions mDims = new Dimensions();
    private final char[] mFirstChar = new char[1];

    private static final int POSSIBLE_BITMAP_SIZES = 3;

    public LetterTileProvider(Context context) {
        final Resources res = context.getResources();
        mTileLetterFontSize = res.getDimensionPixelSize(R.dimen.tile_letter_font_size);
        mTileLetterFontSizeSmall = res
                .getDimensionPixelSize(R.dimen.tile_letter_font_size_small);
        mTileFontColor = res.getColor(R.color.letter_tile_font_color);
        mSansSerifLight = Typeface.create("sans-serif-light", Typeface.NORMAL);
        mBounds = new Rect();
        mPaint.setTypeface(mSansSerifLight);
        mPaint.setColor(mTileFontColor);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setAntiAlias(true);
        mBitmapBackgroundCache = new Bitmap[POSSIBLE_BITMAP_SIZES];
        // TS: chenyanhua 2015-02-11 EMAIL BUGFIX-926747 ADD_S
        mBitmap = BitmapFactory.decodeResource(res,
                R.drawable.ic_notification_multiple_mail_24dp);
        // TS: chenyanhua 2015-02-11 EMAIL BUGFIX-926747 ADD_E
        mDefaultBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_generic_man);
        mDefaultBitmapCache = new Bitmap[POSSIBLE_BITMAP_SIZES];

        mColors = res.obtainTypedArray(R.array.letter_tile_colors);
        mColorCount = mColors.length();
        mDefaultColor = res.getColor(R.color.letter_tile_default_color);
    }

    // TS: chenyanhua 2015-02-11 EMAIL BUGFIX-926747 ADD_S
    public Bitmap getMultiTile(final Dimensions dimensions) {
        final Bitmap bitmap = getBitmap(dimensions, false /* getDefault */);

        final Canvas c = mCanvas;
        c.setBitmap(bitmap);
        c.drawColor(R.color.notification_icon_gmail_red);
        c.drawBitmap(mBitmap, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2,
                mPaint);

        return bitmap;
    }

    // TS: chenyanhua 2015-02-11 EMAIL BUGFIX-926747 ADD_E
    public Bitmap getLetterTile(final Dimensions dimensions, final String displayName,
            final String address) {
        final String display = !TextUtils.isEmpty(displayName) ? displayName : address;
        // TS: zhonghua.tuo 2015-03-01 EMAIL BUGFIX-936675 MOD_S
        char firstChar = 0;
        if (display != null && display.length() > 0) {
            firstChar = display.charAt(0);
        }
        // TS: zhonghua.tuo 2015-03-01 EMAIL BUGFIX-936675 MOD_E
        // get an empty bitmap
        final Bitmap bitmap = getBitmap(dimensions, false /* getDefault */);
        if (bitmap == null) {
            LogUtils.w(TAG, "LetterTileProvider width(%d) or height(%d) is 0 for name %s and "
                    + "address %s.", dimensions.width, dimensions.height, displayName, address);
            return null;
        }

        final Canvas c = mCanvas;
        c.setBitmap(bitmap);
        // TS: zhonghua.tuo 2015-03-12 EMAIL BUGFIX-940958 MOD_S
        c.drawColor(pickColor(address));
        // TS: zhonghua.tuo 2015-03-12 EMAIL BUGFIX-940958 MOD_E

        // If its a valid English alphabet letter,
        // draw the letter on top of the color
        if (isEnglishLetterOrDigit(firstChar) || isChineseLetter(firstChar) || isCyrillicLetter(firstChar)) {// TS: junwei-xu 2015-1-13 EMAIL BUGFIX-898644 ADD_S
            mFirstChar[0] = Character.toUpperCase(firstChar);
            mPaint.setTextSize(getFontSize(dimensions.scale));
            mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);
            c.drawText(mFirstChar, 0, 1, 0 + dimensions.width / 2,
                    0 + dimensions.height / 2 + (mBounds.bottom - mBounds.top) / 2, mPaint);
        } else { // draw the generic icon on top
            c.drawBitmap(getBitmap(dimensions, true /* getDefault */), 0, 0, null);
        }

        return bitmap;
    }

    private static boolean isEnglishLetterOrDigit(char c) {
        return ('A' <= c && c <= 'Z')
                || ('a' <= c && c <= 'z')
                || ('0' <= c && c <= '9');
    }

    // TS: junwei-xu 2015-1-13 EMAIL BUGFIX-898644 ADD_S
    private static boolean isCyrillicLetter(char c){
        return (0x0400 <= c && c <= 0x052F);
    }

    private static boolean isChineseLetter(char c){
        return (0x0400 <= c && c <= 0x9FBF);
    }
    // TS: junwei-xu 2015-1-13 EMAIL BUGFIX-898644 ADD_E

    private Bitmap getBitmap(final Dimensions d, boolean getDefault) {
        if (d.width <= 0 || d.height <= 0) {
            LogUtils.w(TAG,
                    "LetterTileProvider width(%d) or height(%d) is 0.", d.width, d.height);
            return null;
        }
        final int pos;
        float scale = d.scale;
        if (scale == Dimensions.SCALE_ONE) {
            pos = 0;
        } else if (scale == Dimensions.SCALE_HALF) {
            pos = 1;
        } else {
            pos = 2;
        }

        final Bitmap[] cache = (getDefault) ? mDefaultBitmapCache : mBitmapBackgroundCache;

        Bitmap bitmap = cache[pos];
        // ensure bitmap is suitable for the desired w/h
        // (two-pane uses two different sets of dimensions depending on pane width)
        if (bitmap == null || bitmap.getWidth() != d.width || bitmap.getHeight() != d.height) {
            // create and place the bitmap
            if (getDefault) {
                bitmap = BitmapUtil.centerCrop(mDefaultBitmap, d.width, d.height);
            } else {
                bitmap = Bitmap.createBitmap(d.width, d.height, Bitmap.Config.ARGB_8888);
            }
            cache[pos] = bitmap;
        }
        return bitmap;
    }

    private int getFontSize(float scale)  {
        if (scale == Dimensions.SCALE_ONE) {
            return mTileLetterFontSize;
        } else {
            return mTileLetterFontSizeSmall;
        }
    }

    private int pickColor(String emailAddress) {
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        int color = Math.abs(emailAddress.hashCode()) % mColorCount;
        return mColors.getColor(color, mDefaultColor);
    }
}
