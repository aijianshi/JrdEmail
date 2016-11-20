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
 *BUGFIX-917600  2015/02/05   zhaotianyong      [Email]Flash back when click blue operator in Email title
 *BUGFIX-963186  2015/4/16    yanhua.chen       Android5.0][Email] [UI] Status bar does not change when selecting characters when editing a mail
 *BUGFIX-976970  2015/4/30    zheng.zou         [Email]Portrait and landscape screen to switch each other occur black screen
 *BUGFIX_851207  2015/11/4    yanhua.chen      [Android L][Email]Redundant "Move to" function in starred and unread folder
 *BUGFIX_1178365 2015/12/22   yanhua.chen      [Android 6.0][Email]remove the folder name box on mail detail page
 ===========================================================================
 */

package com.tct.mail.browse;

import java.util.NavigableSet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.text.BidiFormatter;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.mail.browse.ConversationViewHeader.ConversationViewHeaderCallbacks;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider.FolderCapabilities;
import com.tct.mail.text.ChangeLabelsSpan;
import com.tct.mail.ui.FolderDisplayer;
import com.tct.mail.utils.ViewUtils;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
import android.app.Activity;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.annotation.TargetApi;
import android.os.Build;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E
/**
 * A TextView that displays the conversation subject and list of folders for the message.
 * The view knows the widest that any of its containing {@link FolderSpan}s can be.
 * They cannot exceed the TextView line width, or else {@link Layout}
 * will split up the spans in strange places.
 */
public class SubjectAndFolderView extends TextView
        implements FolderSpan.FolderSpanDimensions {

    private final int mFolderPadding;
    private final int mFolderPaddingExtraWidth;
    private final int mFolderPaddingAfter;
    private final int mRoundedCornerRadius;
    private final float mFolderSpanTextSize;
    private final int mFolderMarginTop;

    private int mMaxSpanWidth;

    private ConversationFolderDisplayer mFolderDisplayer;

    private String mSubject;

    private boolean mVisibleFolders;

    private ConversationViewAdapter.ConversationHeaderItem mHeaderItem;

    private BidiFormatter mBidiFormatter;
 // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-917600 ADD_S
    private long lastClick = 0;
    private long currentClick = 0;
 // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-917600 ADD_E

  //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
    private Activity activity;
  //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E

    public SubjectAndFolderView(Context context) {
        this(context, null);
    }

    public SubjectAndFolderView(Context context, AttributeSet attrs) {
        super(context, attrs);

      //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
        activity = (Activity)context;
      //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E
        mVisibleFolders = false;
        mFolderDisplayer = new ConversationFolderDisplayer(getContext(), this);

        final Resources r = getResources();
        mFolderPadding = r.getDimensionPixelOffset(R.dimen.conversation_folder_padding);
        mFolderPaddingExtraWidth = r.getDimensionPixelOffset(
                R.dimen.conversation_folder_padding_extra_width);
        mFolderPaddingAfter = r.getDimensionPixelOffset(
                R.dimen.conversation_folder_padding_after);
        mRoundedCornerRadius = r.getDimensionPixelOffset(
                R.dimen.folder_rounded_corner_radius);
        mFolderSpanTextSize = r.getDimension(R.dimen.conversation_folder_font_size);
        mFolderMarginTop = r.getDimensionPixelOffset(R.dimen.conversation_folder_margin_top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxSpanWidth = MeasureSpec.getSize(widthMeasureSpec) - getTotalPaddingLeft()
                - getTotalPaddingRight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getPadding() {
        return mFolderPadding;
    }

    @Override
    public int getPaddingExtraWidth() {
        return mFolderPaddingExtraWidth;
    }

    @Override
    public int getPaddingAfter() {
        return mFolderPaddingAfter;
    }

    @Override
    public int getMaxWidth() {
        return mMaxSpanWidth;
    }

    @Override
    public float getRoundedCornerRadius() {
        return mRoundedCornerRadius;
    }

    @Override
    public float getFolderSpanTextSize() {
        return mFolderSpanTextSize;
    }

    @Override
    public int getMarginTop() {
        return mFolderMarginTop;
    }

    @Override
    public boolean isRtl() {
        return ViewUtils.isViewRtl(this);
    }

    // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-917600 ADD_S
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            lastClick = currentClick;
            currentClick = System.currentTimeMillis();
            if (currentClick - lastClick < 1000) {// double click
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
    // TS: zhaotianyong 2015-02-05 EMAIL BUGFIX-917600 ADD_E

    public void setSubject(String subject) {
        mSubject = Conversation.getSubjectForDisplay(getContext(), null /* badgeText */, subject);

        if (!mVisibleFolders) {
            setText(mSubject);
        }
    }

    public void setFolders(
            ConversationViewHeaderCallbacks callbacks, Account account, Conversation conv) {
        mVisibleFolders = true;
        final BidiFormatter bidiFormatter = getBidiFormatter();
        if(TextUtils.isEmpty(mSubject )){
                    mSubject = conv.subject;
                }
        final SpannableStringBuilder sb =
                new SpannableStringBuilder(bidiFormatter.unicodeWrap(mSubject));
        sb.append('\u0020');
        final Settings settings = account.settings;
        final int start = sb.length();
        if (settings.importanceMarkersEnabled && conv.isImportant()) {
            sb.append(".\u0020");
            sb.setSpan(new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BASELINE) {
                           @Override
                           public Drawable getDrawable() {
                               Drawable d = getContext().getResources().getDrawable(
                                       R.drawable.ic_email_caret_none_important_unread);
                               d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                               return d;
                           }
                       },
                    start, start + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        //TS: yanhua.chen 2015-12-22 EMAIL BUGFIX_1178365 MOD_S
//        mFolderDisplayer.loadConversationFolders(conv, null /* ignoreFolder */, -1 /* ignoreFolderType */);
//        mFolderDisplayer.appendFolderSpans(sb, bidiFormatter);

//        final int end = sb.length();
//        sb.setSpan(new ChangeLabelsSpan(callbacks), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(sb);
        //TS: yanhua.chen 2015-11-4 EMAIL BUGFIX_851207 MOD_S
//        if(isNeedMove()){
//            setMovementMethod(LinkMovementMethod.getInstance());
//        }
        //TS: yanhua.chen 2015-11-4 EMAIL BUGFIX_851207 MOD_E
        //TS: yanhua.chen 2015-12-22 EMAIL BUGFIX_1178365 MOD_E
    }

    //TS: yanhua.chen 2015-11-4 EMAIL BUGFIX_851207 ADD_S
    /**
     *judge whether subjectAndFolderView need to move function
     */
    private boolean isNeedMove(){
        boolean isNeedNove = false;
        Folder folder = null;
        if(!mFolderDisplayer.getNavigableSet().isEmpty() && activity != null){
            for(Folder f : mFolderDisplayer.getNavigableSet()){
                folder = f;
            }
        }
        if(folder != null && folder.supportsCapability(FolderCapabilities.ALLOWS_REMOVE_CONVERSATION)){
            isNeedNove = true;
        }
        return isNeedNove;
   }
   //TS: yanhua.chen 2015-11-4 EMAIL BUGFIX_851207 ADD_E

    public void bind(ConversationViewAdapter.ConversationHeaderItem headerItem) {
        mHeaderItem = headerItem;
    }

    private BidiFormatter getBidiFormatter() {
        if (mBidiFormatter == null) {
            final ConversationViewAdapter adapter = mHeaderItem != null
                    ? mHeaderItem.getAdapter() : null;
            if (adapter == null) {
                mBidiFormatter = BidiFormatter.getInstance();
            } else {
                mBidiFormatter = adapter.getBidiFormatter();
            }
        }
        return mBidiFormatter;
    }

    private static class ConversationFolderDisplayer extends FolderDisplayer {

        private final FolderSpan.FolderSpanDimensions mDims;

        public ConversationFolderDisplayer(Context context, FolderSpan.FolderSpanDimensions dims) {
            super(context);
            mDims = dims;
        }

        public void appendFolderSpans(SpannableStringBuilder sb, BidiFormatter bidiFormatter) {
            for (final Folder f : mFoldersSortedSet) {
                final int bgColor = f.getBackgroundColor(mDefaultBgColor);
                final int fgColor = f.getForegroundColor(mDefaultFgColor);
                addSpan(sb, f.name, bgColor, fgColor, bidiFormatter);
            }

            if (mFoldersSortedSet.isEmpty()) {
                final Resources r = mContext.getResources();
                final String name = r.getString(R.string.add_label);
                final int bgColor = r.getColor(R.color.conv_header_add_label_background);
                final int fgColor = r.getColor(R.color.conv_header_add_label_text);
                addSpan(sb, name, bgColor, fgColor, bidiFormatter);
            }
        }

        private void addSpan(SpannableStringBuilder sb, String name,
                int bgColor, int fgColor, BidiFormatter bidiFormatter) {
            final int start = sb.length();
            sb.append(bidiFormatter.unicodeWrap(name));
            final int end = sb.length();

            sb.setSpan(new BackgroundColorSpan(bgColor), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(fgColor), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new FolderSpan(sb, mDims), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //TS: yanhua.chen 2015-11-4 EMAIL BUGFIX_851207 ADD_S
        public NavigableSet<Folder> getNavigableSet(){
            return mFoldersSortedSet;
        }
        //TS: yanhua.chen 2015-11-4 EMAIL BUGFIX_851207 ADD_E
    }

    //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
    @Override
    public ActionMode startActionMode(Callback callback) {
        changeStatusBarColor();
        return super.startActionMode(callback);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        if(activity != null && activity.getWindow() != null){
            activity.getWindow().setStatusBarColor(getResources().getColor(R.color.change_status_bar));
        }
    }
    //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E

    // TS: zheng.zou 2015-04-30 EMAIL BUGFIX-976970 ADD_S
    //note:the ChangeLabelsSpan and its onclick listener is cached, which has a reference to the old activity context
    //that will cause memory leak.remove selection span before super.onSaveInstanceState(),
    //so that the TextView will NOT save all the spans and restore them when
    //back from activity of fragment recreation.
    @Override
    public Parcelable onSaveInstanceState() {
        if (getText() instanceof Spannable){
            Selection.removeSelection((Spannable) getText());
        }
        return super.onSaveInstanceState();
    }
    // TS: zheng.zou 2015-04-30 EMAIL BUGFIX-976970 ADD_E
}
