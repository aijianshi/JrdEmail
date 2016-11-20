/**
 * Copyright (c) 2013, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 *CONFLICT-50002 2014/10/24   zhaotianyong    Modify the package conflict
 *FEATURE-834751 2015/10/28   jian.xu         Use different color to distinguish each account when in combined view mode
 *BUGFIX-956919  2016/01/13   tianjing.su   [Android L][Email][UE]The font of "Email account" string change to black after rorating screen and the check box style is not consistent after rotating screen..
 ============================================================================ 
 */
package com.tct.mail.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//TS:MODE by zhaotianyong for CONFLICT_50002 START
//import com.android.bitmap.BitmapCache;
import com.tct.fw.bitmap.BitmapCache;
import com.tct.mail.bitmap.AccountAvatarDrawable;
import com.tct.mail.bitmap.AccountColorBlockView;
import com.tct.mail.bitmap.ContactResolver;
import com.tct.mail.providers.Account;
//TS:MODE by zhaotianyong for CONFLICT_50002 END

import com.tct.email.R;

/**
 * The view for each account in the folder list/drawer.
 */
public class AccountItemView extends LinearLayout {
    private TextView mAccountDisplayName;
    private TextView mAccountAddress;
    private ImageView mAvatar;
    private ImageView mCheckmark;
    //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-S
    private AccountColorBlockView mAccountColorBlock;
    //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-E

    public AccountItemView(Context context) {
        super(context);
    }

    public AccountItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAccountDisplayName = (TextView)findViewById(R.id.account_display_name);
        mAccountAddress = (TextView)findViewById(R.id.account_address);
        mAvatar = (ImageView)findViewById(R.id.avatar);
        mCheckmark = (ImageView)findViewById(R.id.checkmark);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-S
        mAccountColorBlock = (AccountColorBlockView) findViewById(R.id.account_color_block);
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-E
    }

    /**
     * Sets the account name and draws the unread count. Depending on the account state (current or
     * unused), the colors and drawables will change through the call to setSelected for each
     * necessary element.
     *
     * @param account account whose name will be displayed
     * @param isCurrentAccount true if the account is the one in use, false otherwise
     */
    public void bind(final Context context, final Account account, final boolean isCurrentAccount,
                 final boolean isAccountColorBlockVisible, final BitmapCache imagesCache, final ContactResolver contactResolver) {
        if (!TextUtils.isEmpty(account.getSenderName())) {
            mAccountDisplayName.setText(account.getSenderName());
            mAccountAddress.setText(account.getEmailAddress());
            mAccountAddress.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(account.getDisplayName()) &&
                !TextUtils.equals(account.getDisplayName(), account.getEmailAddress())) {
            mAccountDisplayName.setText(account.getDisplayName());
            mAccountAddress.setText(account.getEmailAddress());
            mAccountAddress.setVisibility(View.VISIBLE);
        } else {
            mAccountDisplayName.setText(account.getEmailAddress());
            mAccountAddress.setVisibility(View.GONE);
        }
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-S
        if (isAccountColorBlockVisible) {
            mAccountColorBlock.setVisibility(View.VISIBLE);
            mAccountColorBlock.setColor(account.color);
        } else {
            mAccountColorBlock.setVisibility(View.GONE);
        }
        //TS: jian.xu 2015-10-28 EMAIL BUGFIX-834751 ADD-E
        if (isCurrentAccount) {
            mCheckmark.setVisibility(View.VISIBLE);
            mAccountDisplayName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            mCheckmark.setVisibility(View.GONE);
            mAccountDisplayName.setTypeface(Typeface.DEFAULT);
        }

        ImageView v = (ImageView) mAvatar.findViewById(R.id.avatar);
        //TS: tianjing.su 2016-01-13 EMAIL BUGFIX-956919 MOD_S
        AccountAvatarDrawable drawable = new AccountAvatarDrawable(
                context.getResources(), imagesCache, contactResolver , account.getAccountId());
        //TS: tianjing.su 2016-01-13 EMAIL BUGFIX-956919 MOD_E
        final int size = context.getResources().getDimensionPixelSize(
                R.dimen.account_avatar_dimension);
        drawable.setDecodeDimensions(size, size);
        drawable.bind(account.getSenderName(), account.getEmailAddress());
        v.setImageDrawable(drawable);
    }
}
