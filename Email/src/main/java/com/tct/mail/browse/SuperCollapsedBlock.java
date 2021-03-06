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

package com.tct.mail.browse;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.mail.browse.ConversationViewAdapter.SuperCollapsedBlockItem;

import java.text.NumberFormat;

/**
 * A header block that expands to a list of collapsed message headers. Will notify a listener on tap
 * so the listener can hide the block and reveal the corresponding collapsed message headers.
 *
 */
public class SuperCollapsedBlock extends FrameLayout implements View.OnClickListener {

    public interface OnClickListener {
        /**
         * Handle a click on a super-collapsed block.
         *
         */
        void onSuperCollapsedClick(SuperCollapsedBlockItem item);
    }

    private SuperCollapsedBlockItem mSuperCollapsedItem;
    private OnClickListener mClick;
    private TextView mSuperCollapsedText;

    public SuperCollapsedBlock(Context context) {
        this(context, null);
    }

    public SuperCollapsedBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        setActivated(false);
        setOnClickListener(this);
    }

    public void initialize(OnClickListener onClick) {
        mClick = onClick;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSuperCollapsedText = (TextView) findViewById(R.id.super_collapsed_text);
    }

    public void bind(SuperCollapsedBlockItem item) {
        mSuperCollapsedItem = item;
        setCount(item.getEnd() - item.getStart() + 1);
    }

    public void setCount(int count) {
        final String strCount = NumberFormat.getIntegerInstance().format(count);
        mSuperCollapsedText.setText(strCount);
        final Resources res = getResources();
        final int colorId = mSuperCollapsedItem.hasDraft() ?
                R.color.text_color_draft_red : R.color.conversation_view_text_color_light;
        mSuperCollapsedText.setTextColor(res.getColor(colorId));
        setContentDescription(
                res.getQuantityString(R.plurals.show_messages_read, count, count));
    }

    @Override
    public void onClick(final View v) {
        ((TextView) findViewById(R.id.super_collapsed_text)).setText(
                R.string.loading_conversation);

        if (mClick != null) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mClick.onSuperCollapsedClick(mSuperCollapsedItem);
                }
            });
        }
    }
}
