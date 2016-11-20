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

/******************************************************************************/
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* ----------|----------------------|----------------------|----------------- */
/* 05/29/2014|     zhonghua.tuo     |      FR 670064       |email search fun- */
/*           |                      |                      |ction             */
/* ----------|----------------------|----------------------|----------------- */
/**| 10/24/2014|     zhujian.shao     |      FR 736411       |[HOMO][HOMO][Orang-|
|           |                      |porting from FR622697 |e][Homologation] E- |
|           |                      |                      |xchange Active Sync |
|           |                      |                      | Priority           |
| **********|**********************|**********************|*******************/
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag             Date        Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *BUGFIX-859814  2014/12/15   zhaotianyong    [Android5.0][Email] Priority icon disappears in Sent box.
 *BUGFIX-886241   2015-01-15  wenggangjin     [Email]Don't directly show attachfile icon when receive mail
 *BUGFIX-902637   2015-01-20  wenggangjin     [Email]The menu display wrong after some oprations in mark list screen
 *BUGFIX-900708  2015/1/22    junwei-xu       [Android5.0][Email][UI]No sender icon when only recipient in bcc
 *BUGFIX-888881  2015-01-27   wenggangjin     [Email]The attachment icon disappear when in search list
 *BUGFIX-932279  2015-02-16   peng-zhang      [Arabic][Email]Email contents cannot display on mail list view
 *BUGFIX_900927  2015-02-28   gengkexue       [Android5.0][Email]The divide line is too short when there is no sender image
 *BUGFIX_989906  2015-06-01   junwei-xu       [Android5.0][Email] [UI] Priority icon is overlap with sender in Mail list
 *BUGFIX-305581  2015/6/19    yanhua.chen     [Email]The attachment icon would not disappear after delete the attachment in the draft Edit Notification
 *BUGFIX_1015610 2015-06-23   jin.dong        [Monitor][Force Close][Email]Happen FC when lock unlock screen on inbox list
 *BUGFIX-964544  2015-07-08   junwei-xu       [Android5.0][Email]Undo cannot select when delete email on search result screen
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *BUGFIX-539892  2015-09-01   zheng.zou       CR:in email list view, group email with time range and show time range in column
 *CR_540046      2015/9/2    yanhua.chen     Subject&Sender
 *CR_585337      2015-09-16  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-568778  2015-10-08  zheng.zou        [Android L][Email]Can't display counting time in mail time label
 *BUGFIX-980186  2015/11/27   jian.xu         [Android L][Email]There is a garbage icon for sending mail
 *BUGFIX-1123835 2015-12-16 junwei-xu         [Monkey][Crash] com.tct.email
 ============================================================================
 */
package com.tct.mail.browse;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.format.DateUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.service.SearchParams;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.bitmap.CheckableContactFlipDrawable;
import com.tct.mail.bitmap.ContactDrawable;
import com.tct.mail.perf.Timer;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.ConversationColumns;
import com.tct.mail.providers.UIProvider.ConversationListIcon;
import com.tct.mail.providers.UIProvider.FolderType;
import com.tct.mail.ui.AnimatedAdapter;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.ui.ConversationSelectionSet;
import com.tct.mail.ui.ConversationSetObserver;
import com.tct.mail.ui.FolderDisplayer;
import com.tct.mail.ui.SwipeableItemView;
import com.tct.mail.ui.SwipeableListView;
import com.tct.mail.ui.ViewMode;
import com.tct.mail.ui.DividedImageCanvas.InvalidateCallback;
import com.tct.mail.utils.FolderUri;
import com.tct.mail.utils.HardwareLayerEnabler;
import com.tct.mail.utils.Utils;
import com.tct.mail.utils.ViewUtils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
import com.tct.emailcommon.utility.TextUtilities;
//[FEATURE]-Add-END by CDTS.zhonghua.tuo
import com.tct.mail.ui.AbstractActivityController;

public class ConversationItemView extends View
        implements SwipeableItemView, ToggleableItem, InvalidateCallback, ConversationSetObserver,
        BadgeSpan.BadgeSpanDimensions {

    // Timer.
    private static int sLayoutCount = 0;
    private static Timer sTimer; // Create the sTimer here if you need to do
                                 // perf analysis.
    private static final int PERF_LAYOUT_ITERATIONS = 50;
    private static final String PERF_TAG_LAYOUT = "CCHV.layout";
    private static final String PERF_TAG_CALCULATE_TEXTS_BITMAPS = "CCHV.txtsbmps";
    private static final String PERF_TAG_CALCULATE_SENDER_SUBJECT = "CCHV.sendersubj";
    private static final String PERF_TAG_CALCULATE_FOLDERS = "CCHV.folders";
    private static final String PERF_TAG_CALCULATE_COORDINATES = "CCHV.coordinates";
    private static final String LOG_TAG = LogTag.getLogTag();

    private static final Typeface SANS_SERIF_LIGHT = Typeface.create("sans-serif-light",
            Typeface.NORMAL);

    // Static bitmaps.
    private static Bitmap STAR_OFF;
    private static Bitmap STAR_ON;
    private static Bitmap ATTACHMENT;
    private static Bitmap ONLY_TO_ME;
    private static Bitmap TO_ME_AND_OTHERS;
    private static Bitmap IMPORTANT_ONLY_TO_ME;
    private static Bitmap IMPORTANT_TO_ME_AND_OTHERS;
    private static Bitmap IMPORTANT;
    private static Bitmap STATE_REPLIED;
    private static Bitmap STATE_FORWARDED;
    private static Bitmap STATE_REPLIED_AND_FORWARDED;
    private static Bitmap STATE_CALENDAR_INVITE;
    private static Drawable VISIBLE_CONVERSATION_HIGHLIGHT;
    private static Drawable RIGHT_EDGE_TABLET;

    private static String sSendersSplitToken;
    private static String sElidedPaddingToken;

    // Static colors.
    private static int sSendersTextColor;
    private static int sDateTextColorRead;
    private static int sDateTextColorUnread;
    private static int sStarTouchSlop;
    private static int sSenderImageTouchSlop;
    private static int sShrinkAnimationDuration;
    private static int sSlideAnimationDuration;
    private static int sCabAnimationDuration;
    private static int sBadgePaddingExtraWidth;
    private static int sBadgeRoundedCornerRadius;
    private static int sFolderRoundedCornerRadius;
    private static int sDividerColor;

    // Static paints.
    private static final TextPaint sPaint = new TextPaint();
    private static final TextPaint sFoldersPaint = new TextPaint();
    private static final Paint sCheckBackgroundPaint = new Paint();
    private static final Paint sDividerPaint = new Paint();

    private static int sDividerInset;
    private static int sDividerHeight;

    private static BroadcastReceiver sConfigurationChangedReceiver;

    // Backgrounds for different states.
    private final SparseArray<Drawable> mBackgrounds = new SparseArray<Drawable>();

    // Dimensions and coordinates.
    private int mViewWidth = -1;
    /** The view mode at which we calculated mViewWidth previously. */
    private int mPreviousMode;

    private int mInfoIconX;
    private int mDateX;
    private int mDateWidth;
    private int mPaperclipX;
    //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_S
    //private int mSendersX;
    //private int mSendersWidth;
    private int mSubjectX;
    private int mSubjectWidth;
    //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_E
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    private int mPriorityX;
    //[FEATURE]-Add-END by TSCD.chao zhang
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    private int mStatusX;
    private int mStatusWidth;
    private int dateAttachmentStart;
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    /** Whether we are on a tablet device or not */
    private final boolean mTabletDevice;
    /** When in conversation mode, true if the list is hidden */
    private final boolean mListCollapsible;

    @VisibleForTesting
    ConversationItemViewCoordinates mCoordinates;

    private ConversationItemViewCoordinates.Config mConfig;

    private final Context mContext;

    public ConversationItemViewModel mHeader;
    private boolean mDownEvent;
    private boolean mSelected = false;
    private ConversationSelectionSet mSelectedConversationSet;
    private Folder mDisplayedFolder;
    private boolean mStarEnabled;
    private boolean mSwipeEnabled;
    private int mLastTouchX;
    private int mLastTouchY;
    private AnimatedAdapter mAdapter;
    private float mAnimatedHeightFraction = 1.0f;
    private final String mAccount;
    private ControllableActivity mActivity;
    private final TextView mSendersTextView;
    private final TextView mSubjectTextView;
    private final TextView mSnippetTextView;
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    private final TextView mStatusTextView;
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    private int mGadgetMode;
    /// TCT: add search params for highlight.
    private SearchParams mSearchParams;

    private static int sFoldersStartPadding;
    private static int sFoldersInnerPadding;
    private static int sFoldersMaxCount;
    private static int sFoldersOverflowGradientPadding;
    private static TextAppearanceSpan sSubjectTextUnreadSpan;
    private static TextAppearanceSpan sSubjectTextReadSpan;
    private static TextAppearanceSpan sBadgeTextSpan;
    private static BackgroundColorSpan sBadgeBackgroundSpan;
    private static int sScrollSlop;
    private static CharacterStyle sActivatedTextSpan;

    private final CheckableContactFlipDrawable mSendersImageView;

    /** The resource id of the color to use to override the background. */
    private int mBackgroundOverrideResId = -1;
    /** The bitmap to use, or <code>null</code> for the default */
    private Bitmap mPhotoBitmap = null;
    private Rect mPhotoRect = null;
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    public  int mPriority=1;
    private int HIGH_PRIORITY = 0;
    private int NORMAL_PRIORITY = 1;
    private int LOW_PRIORITY = 2;
    private static Bitmap sLowPriorityIcon;
    private static Bitmap sHighPriorityIcon;
    private static Bitmap sNormalPriorityIcon;
    //[FEATURE]-Add-END by TSCD.chao zhang

    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
    private String mQueryText;
    private int mField;
    public void setQueryInfo(String query,int field) {
        mQueryText = query;
        mField = field;
    }
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo
    private boolean mWillDrawDivider = true;  //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_539892 ADD
    /**
     * A listener for clicks on the various areas of a conversation item.
     */
    public interface ConversationItemAreaClickListener {
        /** Called when the info icon is clicked. */
        void onInfoIconClicked();

        /** Called when the star is clicked. */
        void onStarClicked();
    }

    /** If set, it will steal all clicks for which the interface has a click method. */
    private ConversationItemAreaClickListener mConversationItemAreaClickListener = null;

    static {
        sPaint.setAntiAlias(true);
        sFoldersPaint.setAntiAlias(true);

        sCheckBackgroundPaint.setColor(Color.GRAY);
    }

    /**
     * Handles displaying folders in a conversation header view.
     */
    static class ConversationItemFolderDisplayer extends FolderDisplayer {

        private int mFoldersCount;

        public ConversationItemFolderDisplayer(Context context) {
            super(context);
        }

        @Override
        public void loadConversationFolders(Conversation conv, final FolderUri ignoreFolderUri,
                final int ignoreFolderType) {
            super.loadConversationFolders(conv, ignoreFolderUri, ignoreFolderType);
            mFoldersCount = mFoldersSortedSet.size();
        }

        @Override
        public void reset() {
            super.reset();
            mFoldersCount = 0;
        }

        public boolean hasVisibleFolders() {
            return mFoldersCount > 0;
        }

        /**
         * Helper function to calculate exactly how much space the displayed folders should take.
         * @return an array of integers that signifies the length in dp.
         */
        private MeasurementWrapper measureFolderDimen(ConversationItemViewCoordinates coordinates) {
            // This signifies the absolute max for each folder cell, no exceptions.
            final int maxCellWidth = coordinates.folderCellWidth;

            final int numDisplayedFolders = Math.min(sFoldersMaxCount, mFoldersSortedSet.size());
            if (numDisplayedFolders == 0) {
                return new MeasurementWrapper(new int[0], new boolean[0]);
            }

            // This variable is calculated based on the number of folders we are displaying
            final int maxAllowedCellSize = Math.min(maxCellWidth, (coordinates.folderLayoutWidth -
                    (numDisplayedFolders - 1) * sFoldersStartPadding) / numDisplayedFolders);
            final int[] measurements = new int[numDisplayedFolders];
            final boolean[] overflow = new boolean[numDisplayedFolders];
            final MeasurementWrapper result = new MeasurementWrapper(measurements, overflow);

            int count = 0;
            int missingWidth = 0;
            int extraWidth = 0;
            for (Folder f : mFoldersSortedSet) {
                if (count > numDisplayedFolders - 1) {
                    break;
                }

                final String folderString = f.name;
                final int neededWidth = (int) sFoldersPaint.measureText(folderString) +
                        2 * sFoldersInnerPadding;

                if (neededWidth > maxAllowedCellSize) {
                    // What we can take from others is the minimum of the width we need to borrow
                    // and the width we are allowed to borrow.
                    final int borrowedWidth = Math.min(neededWidth - maxAllowedCellSize,
                            maxCellWidth - maxAllowedCellSize);
                    final int extraWidthLeftover = extraWidth - borrowedWidth;
                    if (extraWidthLeftover >= 0) {
                        measurements[count] = Math.min(neededWidth, maxCellWidth);
                        extraWidth = extraWidthLeftover;
                    } else {
                        measurements[count] = maxAllowedCellSize + extraWidth;
                        extraWidth = 0;
                    }
                    missingWidth = -extraWidthLeftover;
                    overflow[count] = neededWidth > measurements[count];
                } else {
                    extraWidth = maxAllowedCellSize - neededWidth;
                    measurements[count] = neededWidth;
                    if (missingWidth > 0) {
                        if (extraWidth >= missingWidth) {
                            measurements[count - 1] += missingWidth;
                            extraWidth -= missingWidth;
                            overflow[count - 1] = false;
                        } else {
                            measurements[count - 1] += extraWidth;
                            extraWidth = 0;
                        }
                    }
                    missingWidth = 0;
                }

                count++;
            }

            return result;
        }

        /**
         * @return how much total space the folders list requires.
         */
        private int measureFolders(ConversationItemViewCoordinates coordinates) {
            int[] sizes = measureFolderDimen(coordinates).measurements;
            return sumWidth(sizes);
        }

        private int sumWidth(int[] arr) {
            int sum = 0;
            for (int i = 0; i < arr.length; i++) {
                sum += arr[i];
            }
            return sum + (arr.length - 1) * sFoldersStartPadding;
        }

        public void drawFolders(
                Canvas canvas, ConversationItemViewCoordinates coordinates, boolean isRtl) {
            if (mFoldersCount == 0) {
                return;
            }

            final MeasurementWrapper wrapper = measureFolderDimen(coordinates);
            final int[] measurements = wrapper.measurements;
            final boolean[] overflow = wrapper.overflow;

            final int right = coordinates.foldersRight;
            final int y = coordinates.foldersY;
            final int height = coordinates.foldersHeight;
            final int textBottomPadding = coordinates.foldersTextBottomPadding;

            sFoldersPaint.setTextSize(coordinates.foldersFontSize);
            sFoldersPaint.setTypeface(coordinates.foldersTypeface);

            // Initialize space and cell size based on the current mode.
            final int foldersCount = measurements.length;
            final int width = sumWidth(measurements);
            int xLeft = (isRtl) ?  right - coordinates.folderLayoutWidth : right - width;

            int index = 0;
            for (Folder f : mFoldersSortedSet) {
                if (index > foldersCount - 1) {
                    break;
                }

                final String folderString = f.name;
                final int fgColor = f.getForegroundColor(mDefaultFgColor);
                final int bgColor = f.getBackgroundColor(mDefaultBgColor);

                // Draw the box.
                sFoldersPaint.setColor(bgColor);
                sFoldersPaint.setStyle(Paint.Style.FILL);
                final RectF rect =
                        new RectF(xLeft, y, xLeft + measurements[index], y + height);
                canvas.drawRoundRect(rect, sFolderRoundedCornerRadius, sFolderRoundedCornerRadius,
                        sFoldersPaint);

                // Draw the text.
                sFoldersPaint.setColor(fgColor);
                sFoldersPaint.setStyle(Paint.Style.FILL);
                if (overflow[index]) {
                    final int rightBorder = xLeft + measurements[index];
                    final int x0 = (isRtl) ? xLeft + sFoldersOverflowGradientPadding :
                            rightBorder - sFoldersOverflowGradientPadding;
                    final int x1 = (isRtl) ?  xLeft + sFoldersInnerPadding :
                            rightBorder - sFoldersInnerPadding;
                    final Shader shader = new LinearGradient(x0, y, x1, y, fgColor,
                            Utils.getTransparentColor(fgColor), Shader.TileMode.CLAMP);
                    sFoldersPaint.setShader(shader);
                }
                canvas.drawText(folderString, xLeft + sFoldersInnerPadding,
                        y + height - textBottomPadding, sFoldersPaint);
                if (overflow[index]) {
                    sFoldersPaint.setShader(null);
                }

                xLeft += measurements[index++] + sFoldersStartPadding;
            }
        }

        private static class MeasurementWrapper {
            final int[] measurements;
            final boolean[] overflow;

            public MeasurementWrapper(int[] m, boolean[] o) {
                measurements = m;
                overflow = o;
            }
        }
    }

    public ConversationItemView(Context context, String account) {
        super(context);
        Utils.traceBeginSection("CIVC constructor");
        setClickable(true);
        setLongClickable(true);
        mContext = context.getApplicationContext();
        final Resources res = mContext.getResources();
        mTabletDevice = Utils.useTabletUI(res);
        mListCollapsible = res.getBoolean(R.bool.list_collapsible);
        mAccount = account;

        getItemViewResources(mContext);

        final int layoutDir = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());

        mSendersTextView = new TextView(mContext);
        mSendersTextView.setIncludeFontPadding(false);

        mSubjectTextView = new TextView(mContext);
        mSubjectTextView.setEllipsize(TextUtils.TruncateAt.END);
        mSubjectTextView.setSingleLine(); // allow partial words to be elided
        mSubjectTextView.setIncludeFontPadding(false);
        ViewCompat.setLayoutDirection(mSubjectTextView, layoutDir);
        //AM: peng-zhang 2015-02-15 EMAIL BUGFIX_932279 MOD_S
        //ViewUtils.setTextAlignment(mSubjectTextView, View.TEXT_ALIGNMENT_VIEW_START);
        if(0 == layoutDir){
            ViewUtils.setTextAlignment(mSubjectTextView, View.TEXT_ALIGNMENT_VIEW_START);
        }else if(1 == layoutDir){
            ViewUtils.setTextAlignment(mSubjectTextView, View.TEXT_ALIGNMENT_VIEW_END);
        }
        //AM: peng-zhang 2015-02-15 EMAIL BUGFIX_932279 MOD_E
        mSnippetTextView = new TextView(mContext);
        mSnippetTextView.setEllipsize(TextUtils.TruncateAt.END);
        mSnippetTextView.setSingleLine(); // allow partial words to be elided
        mSnippetTextView.setIncludeFontPadding(false);
        mSnippetTextView.setTypeface(SANS_SERIF_LIGHT);
        mSnippetTextView.setTextColor(getResources().getColor(R.color.snippet_text_color));
        ViewCompat.setLayoutDirection(mSnippetTextView, layoutDir);
        //AM: peng-zhang 2015-02-16 EMAIL BUGFIX_932279 MOD_S
        //ViewUtils.setTextAlignment(mSnippetTextView, View.TEXT_ALIGNMENT_VIEW_START);
        if(0 == layoutDir){
            ViewUtils.setTextAlignment(mSnippetTextView, View.TEXT_ALIGNMENT_VIEW_START);
        }else if(1 == layoutDir){
            ViewUtils.setTextAlignment(mSnippetTextView, View.TEXT_ALIGNMENT_VIEW_END);
        }
        //AM: peng-zhang 2015-02-16 EMAIL BUGFIX_932279 MOD_E
        mSendersImageView = new CheckableContactFlipDrawable(res, sCabAnimationDuration);
        mSendersImageView.setCallback(this);
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        mStatusTextView = new TextView(mContext);
        mStatusTextView.setIncludeFontPadding(false);
        mStatusTextView.setTextColor(getResources().getColor(R.color.status_text_color));
        ViewCompat.setLayoutDirection(mStatusTextView, layoutDir);
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
        Utils.traceEndSection();
    }

    private static synchronized void getItemViewResources(Context context) {
        if (sConfigurationChangedReceiver == null) {
            sConfigurationChangedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    STAR_OFF = null;
                    //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304
                    getItemViewResources(context.getApplicationContext());
                    //[BUGFIX]-Mod-END by TCTNB.caixia.chen
                }
            };
            context.registerReceiver(sConfigurationChangedReceiver, new IntentFilter(
                    Intent.ACTION_CONFIGURATION_CHANGED));
        }
        if (STAR_OFF == null) {
            final Resources res = context.getResources();
            // Initialize static bitmaps.
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
            // star off will not show in list item.
            //STAR_OFF = BitmapFactory.decodeResource(res, R.drawable.ic_star_outline_20dp);
            STAR_OFF = BitmapFactory.decodeResource(res, R.drawable.ic_importance_normal);
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
            STAR_ON = BitmapFactory.decodeResource(res, R.drawable.ic_star_20dp);
            ATTACHMENT = BitmapFactory.decodeResource(res, R.drawable.ic_attach_file_20dp);
            ONLY_TO_ME = BitmapFactory.decodeResource(res, R.drawable.ic_email_caret_double);
            TO_ME_AND_OTHERS = BitmapFactory.decodeResource(res, R.drawable.ic_email_caret_single);
            IMPORTANT_ONLY_TO_ME = BitmapFactory.decodeResource(res,
                    R.drawable.ic_email_caret_double_important_unread);
            IMPORTANT_TO_ME_AND_OTHERS = BitmapFactory.decodeResource(res,
                    R.drawable.ic_email_caret_single_important_unread);
            IMPORTANT = BitmapFactory.decodeResource(res,
                    R.drawable.ic_email_caret_none_important_unread);
            STATE_REPLIED =
                    BitmapFactory.decodeResource(res, R.drawable.ic_badge_reply_holo_light);
            STATE_FORWARDED =
                    BitmapFactory.decodeResource(res, R.drawable.ic_badge_forward_holo_light);
            STATE_REPLIED_AND_FORWARDED =
                    BitmapFactory.decodeResource(res, R.drawable.ic_badge_reply_forward_holo_light);
            STATE_CALENDAR_INVITE =
                    BitmapFactory.decodeResource(res, R.drawable.ic_badge_invite_holo_light);
            VISIBLE_CONVERSATION_HIGHLIGHT = res.getDrawable(
                    R.drawable.visible_conversation_highlight);
            RIGHT_EDGE_TABLET = res.getDrawable(R.drawable.list_edge_tablet);
            //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
            sHighPriorityIcon = BitmapFactory.decodeResource(res, R.drawable.ic_high_priority);
            sLowPriorityIcon = BitmapFactory.decodeResource(res, R.drawable.ic_low_priority);
            sNormalPriorityIcon = BitmapFactory.decodeResource(res, R.drawable.ic_importance_normal);
            //[FEATURE]-Add-END by TSCD.chao zhang

            // Initialize colors.
            sActivatedTextSpan = CharacterStyle.wrap(new ForegroundColorSpan(
                    res.getColor(R.color.senders_text_color)));
            sSendersTextColor = res.getColor(R.color.senders_text_color);
            sSubjectTextUnreadSpan = new TextAppearanceSpan(context,
                    R.style.SubjectAppearanceUnreadStyle);
            sSubjectTextReadSpan = new TextAppearanceSpan(
                    context, R.style.SubjectAppearanceReadStyle);

            sBadgeTextSpan = new TextAppearanceSpan(context, R.style.BadgeTextStyle);
            sBadgeBackgroundSpan = new BackgroundColorSpan(
                    res.getColor(R.color.badge_background_color));
            sDateTextColorRead = res.getColor(R.color.date_text_color_read);
            sDateTextColorUnread = res.getColor(R.color.date_text_color_unread);
            sStarTouchSlop = res.getDimensionPixelSize(R.dimen.star_touch_slop);
            sSenderImageTouchSlop = res.getDimensionPixelSize(R.dimen.sender_image_touch_slop);
            sShrinkAnimationDuration = res.getInteger(R.integer.shrink_animation_duration);
            sSlideAnimationDuration = res.getInteger(R.integer.slide_animation_duration);
            // Initialize static color.
            sSendersSplitToken = res.getString(R.string.senders_split_token);
            sElidedPaddingToken = res.getString(R.string.elided_padding_token);
            sScrollSlop = res.getInteger(R.integer.swipeScrollSlop);
            sFoldersStartPadding = res.getDimensionPixelOffset(R.dimen.folders_start_padding);
            sFoldersInnerPadding = res.getDimensionPixelOffset(R.dimen.folder_cell_content_padding);
            sFoldersMaxCount = res.getInteger(R.integer.conversation_list_max_folder_count);
            sFoldersOverflowGradientPadding =
                    res.getDimensionPixelOffset(R.dimen.folders_gradient_padding);
            sCabAnimationDuration = res.getInteger(R.integer.conv_item_view_cab_anim_duration);
            sBadgePaddingExtraWidth = res.getDimensionPixelSize(R.dimen.badge_padding_extra_width);
            sBadgeRoundedCornerRadius =
                    res.getDimensionPixelSize(R.dimen.badge_rounded_corner_radius);
            sFolderRoundedCornerRadius =
                    res.getDimensionPixelOffset(R.dimen.folder_rounded_corner_radius);
            sDividerColor = res.getColor(R.color.conversation_list_divider_color);
            sDividerInset = res.getDimensionPixelSize(R.dimen.conv_list_divider_inset);
            sDividerHeight = res.getDimensionPixelSize(R.dimen.divider_height);
        }
    }

    public void bind(final Conversation conversation, final ControllableActivity activity,
            final ConversationSelectionSet set, final Folder folder,
            final int checkboxOrSenderImage,
            final boolean swipeEnabled, final boolean importanceMarkersEnabled,
            final boolean showChevronsEnabled, final AnimatedAdapter adapter,/**TCT:*/ SearchParams
            searchParams) {
        Utils.traceBeginSection("CIVC.bind");
        bind(ConversationItemViewModel.forConversation(mAccount, conversation), activity,
                null /* conversationItemAreaClickListener */,
                set, folder, checkboxOrSenderImage, swipeEnabled, importanceMarkersEnabled,
                showChevronsEnabled, adapter, -1 /* backgroundOverrideResId */,
                null /* photoBitmap */, false /* useFullMargins */,/**TCT:*/
                searchParams);
        Utils.traceEndSection();
    }

    public void bindAd(final ConversationItemViewModel conversationItemViewModel,
            final ControllableActivity activity,
            final ConversationItemAreaClickListener conversationItemAreaClickListener,
            final Folder folder, final int checkboxOrSenderImage, final AnimatedAdapter adapter,
            final int backgroundOverrideResId, final Bitmap photoBitmap,/**TCT:*/ SearchParams
            searchParams) {
        Utils.traceBeginSection("CIVC.bindAd");
        bind(conversationItemViewModel, activity, conversationItemAreaClickListener, null /* set */,
                folder, checkboxOrSenderImage, true /* swipeEnabled */,
                false /* importanceMarkersEnabled */, false /* showChevronsEnabled */,
                adapter, backgroundOverrideResId, photoBitmap, true /* useFullMargins */,/**TCT:*/  searchParams);
        Utils.traceEndSection();
    }

    private void bind(final ConversationItemViewModel header, final ControllableActivity activity,
            final ConversationItemAreaClickListener conversationItemAreaClickListener,
            final ConversationSelectionSet set, final Folder folder,
            final int checkboxOrSenderImage,
            boolean swipeEnabled, final boolean importanceMarkersEnabled,
            final boolean showChevronsEnabled, final AnimatedAdapter adapter,
            final int backgroundOverrideResId, final Bitmap photoBitmap,
            final boolean useFullMargins,/**TCT:*/ SearchParams
            searchParams) {
        mBackgroundOverrideResId = backgroundOverrideResId;
        mPhotoBitmap = photoBitmap;
        mConversationItemAreaClickListener = conversationItemAreaClickListener;

        if (mHeader != null) {
            Utils.traceBeginSection("unbind");
            final boolean newlyBound = header.conversation.id != mHeader.conversation.id;
            // If this was previously bound to a different conversation, remove any contact photo
            // manager requests.
            if (newlyBound || (mHeader.displayableNames != null && !mHeader
                    .displayableNames.equals(header.displayableNames))) {
                mSendersImageView.getContactDrawable().unbind();
            }

            if (newlyBound) {
                // Stop the photo flip animation
                final boolean showSenders = !isSelected();
                mSendersImageView.reset(showSenders);
            }
            Utils.traceEndSection();
        }
        /// TCT: add for search highlight
        mSearchParams = searchParams;
        mCoordinates = null;
        mHeader = header;
        mActivity = activity;
        mSelectedConversationSet = set;
        //TS: zheng.zou 2016-01-12 EMAIL BUGFIX_1127720 DEL_S
//        if (mSelectedConversationSet != null) {
//            mSelectedConversationSet.addObserver(this);
//        }
        //TS: zheng.zou 2016-01-12 EMAIL BUGFIX_1127720 DEL_E
        mDisplayedFolder = folder;
        mStarEnabled = folder != null && !folder.isTrash();
        mSwipeEnabled = swipeEnabled;
        mAdapter = adapter;

        Utils.traceBeginSection("drawables");
        mSendersImageView.getContactDrawable().setBitmapCache(mAdapter.getSendersImagesCache());
        mSendersImageView.getContactDrawable().setContactResolver(mAdapter.getContactResolver());
        Utils.traceEndSection();

        if (checkboxOrSenderImage == ConversationListIcon.SENDER_IMAGE) {
            mGadgetMode = ConversationItemViewCoordinates.GADGET_CONTACT_PHOTO;
        } else {
            mGadgetMode = ConversationItemViewCoordinates.GADGET_NONE;
        }

        Utils.traceBeginSection("folder displayer");
        // Initialize folder displayer.
        if (mHeader.folderDisplayer == null) {
            mHeader.folderDisplayer = new ConversationItemFolderDisplayer(mContext);
        } else {
            mHeader.folderDisplayer.reset();
        }
        Utils.traceEndSection();

        final int ignoreFolderType;
        //TS: junwei-xu 2015-12-16 EMAIL BUGFIX-1123835 MOD_S
        //Note: check if mDisplayedFolder is null
        if (mDisplayedFolder != null && mDisplayedFolder.isInbox()) {
            ignoreFolderType = FolderType.INBOX;
        } else {
            ignoreFolderType = -1;
        }

        Utils.traceBeginSection("load folders");
        mHeader.folderDisplayer.loadConversationFolders(mHeader.conversation,
                mDisplayedFolder != null ? mDisplayedFolder.folderUri : null, ignoreFolderType);
        //TS: junwei-xu 2015-12-16 EMAIL BUGFIX-1123835 MOD_E
        Utils.traceEndSection();

        if (mHeader.showDateText) {
            Utils.traceBeginSection("relative time");
            //TS: zheng.zou 2015-10-08 EMAIL BUGFIX-568778 MOD_S
//            mHeader.dateText = DateUtils.getRelativeTimeSpanString(mContext,
//                    mHeader.conversation.dateMs);
            mHeader.dateText = getElapseTime();
            //TS: zheng.zou 2015-10-08 EMAIL BUGFIX-568778 MOD_S
            Utils.traceEndSection();
        } else {
            mHeader.dateText = "";
        }

        Utils.traceBeginSection("config setup");
        mConfig = new ConversationItemViewCoordinates.Config()
            .withGadget(mGadgetMode)
            .setUseFullMargins(useFullMargins);
        if (header.folderDisplayer.hasVisibleFolders()) {
            mConfig.showFolders();
        }
        if (header.hasBeenForwarded || header.hasBeenRepliedTo || header.isInvite) {
            mConfig.showReplyState();
        }
        if (mHeader.conversation.color != 0) {
            mConfig.showColorBlock();
        }

        // Importance markers and chevrons (personal level indicators).
        mHeader.personalLevelBitmap = null;
        final int personalLevel = mHeader.conversation.personalLevel;
        final boolean isImportant =
                mHeader.conversation.priority == UIProvider.ConversationPriority.IMPORTANT;
        final boolean useImportantMarkers = isImportant && importanceMarkersEnabled;
        if (showChevronsEnabled &&
                personalLevel == UIProvider.ConversationPersonalLevel.ONLY_TO_ME) {
            mHeader.personalLevelBitmap = useImportantMarkers ? IMPORTANT_ONLY_TO_ME
                    : ONLY_TO_ME;
        } else if (showChevronsEnabled &&
                personalLevel == UIProvider.ConversationPersonalLevel.TO_ME_AND_OTHERS) {
            mHeader.personalLevelBitmap = useImportantMarkers ? IMPORTANT_TO_ME_AND_OTHERS
                    : TO_ME_AND_OTHERS;
        } else if (useImportantMarkers) {
            mHeader.personalLevelBitmap = IMPORTANT;
        }
        if (mHeader.personalLevelBitmap != null) {
            mConfig.showPersonalIndicator();
        }
        Utils.traceEndSection();

        Utils.traceBeginSection("content description");
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        //email's priority
        mPriority=mHeader.conversation.flagPriority;
        //[FEATURE]-Add-END by TSCD.chao zhang
        setContentDescription();
        Utils.traceEndSection();
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mSelectedConversationSet != null) {
            mSelectedConversationSet.removeObserver(this);
        }
    }

    @Override
    public void invalidateDrawable(final Drawable who) {
        boolean handled = false;
        if (mCoordinates != null) {
            if (mSendersImageView.equals(who)) {
                final Rect r = new Rect(who.getBounds());
                r.offset(mCoordinates.contactImagesX, mCoordinates.contactImagesY);
                ConversationItemView.this.invalidate(r.left, r.top, r.right, r.bottom);
                handled = true;
            }
        }
        if (!handled) {
            super.invalidateDrawable(who);
        }
    }

    /**
     * Get the Conversation object associated with this view.
     */
    public Conversation getConversation() {
        return mHeader.conversation;
    }

    private static void startTimer(String tag) {
        if (sTimer != null) {
            sTimer.start(tag);
        }
    }

    private static void pauseTimer(String tag) {
        if (sTimer != null) {
            sTimer.pause(tag);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      //[BUGFIX]-Add-BEGIN by TSNJ,Zhenhua.Fan,04/01/2014,PR-891052,
        if(mActivity==null||mActivity.getViewMode()==null)
            return;
        //[BUGFIX]-Add-END by TSNJ,Zhenhua.Fan
        Utils.traceBeginSection("CIVC.measure");
        final int wSize = MeasureSpec.getSize(widthMeasureSpec);

        final int currentMode = mActivity.getViewMode().getMode();
        if (wSize != mViewWidth || mPreviousMode != currentMode) {
            mViewWidth = wSize;
            mPreviousMode = currentMode;
        }
        mHeader.viewWidth = mViewWidth;

        mConfig.updateWidth(wSize).setViewMode(currentMode)
                .setLayoutDirection(ViewCompat.getLayoutDirection(this));

        Resources res = getResources();
        mHeader.standardScaledDimen = res.getDimensionPixelOffset(R.dimen.standard_scaled_dimen);

        mCoordinates = ConversationItemViewCoordinates.forConfig(mContext, mConfig,
                mAdapter.getCoordinatesCache());

        if (mPhotoBitmap != null) {
            mPhotoRect = new Rect(0, 0, mCoordinates.contactImagesWidth,
                    mCoordinates.contactImagesHeight);
        }

        final int h = (mAnimatedHeightFraction != 1.0f) ?
                Math.round(mAnimatedHeightFraction * mCoordinates.height) : mCoordinates.height;
        setMeasuredDimension(mConfig.getWidth(), h);
        Utils.traceEndSection();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        startTimer(PERF_TAG_LAYOUT);
        Utils.traceBeginSection("CIVC.layout");

        super.onLayout(changed, left, top, right, bottom);

        Utils.traceBeginSection("text and bitmaps");
        calculateTextsAndBitmaps();
        Utils.traceEndSection();

        Utils.traceBeginSection("coordinates");
        calculateCoordinates();
        Utils.traceEndSection();

        // Subject.
        Utils.traceBeginSection("subject");
        createSubject(mHeader.unread);

        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        //NOTE: we only want draw it in OUTBOX
        if (mDisplayedFolder != null && mDisplayedFolder.isOutbox()) {
            calculateStatus(false);
            createStatus();
        }
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E

        createSnippet();

        if (!mHeader.isLayoutValid()) {
            setContentDescription();
        }
        mHeader.validate();
        Utils.traceEndSection();

        pauseTimer(PERF_TAG_LAYOUT);
        if (sTimer != null && ++sLayoutCount >= PERF_LAYOUT_ITERATIONS) {
            sTimer.dumpResults();
            sTimer = new Timer();
            sLayoutCount = 0;
        }
        Utils.traceEndSection();
    }

    private void setContentDescription() {
        if (mActivity.isAccessibilityEnabled()) {
            mHeader.resetContentDescription();
            setContentDescription(
                    mHeader.getContentDescription(mContext, mDisplayedFolder.shouldShowRecipients()));
        }
    }

    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_539892 ADD_S
    public void setWillDrawDivider(boolean drawDivider){
        mWillDrawDivider = drawDivider;
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_539892 ADD_E

    @Override
    public void setBackgroundResource(int resourceId) {
        Utils.traceBeginSection("set background resource");
        Drawable drawable = mBackgrounds.get(resourceId);
        if (drawable == null) {
            drawable = getResources().getDrawable(resourceId);
            final int insetPadding = mHeader.insetPadding;
            if (insetPadding > 0) {
                drawable = new InsetDrawable(drawable, insetPadding);
            }
            mBackgrounds.put(resourceId, drawable);
        }
        if (getBackground() != drawable) {
            super.setBackgroundDrawable(drawable);
        }
        Utils.traceEndSection();
    }

    private void calculateTextsAndBitmaps() {
        startTimer(PERF_TAG_CALCULATE_TEXTS_BITMAPS);

        if (mSelectedConversationSet != null) {
            mSelected = mSelectedConversationSet.contains(mHeader.conversation);
        }
        setSelected(mSelected);
        mHeader.gadgetMode = mGadgetMode;

        updateBackground();

        mHeader.sendersDisplayText = new SpannableStringBuilder();

        mHeader.hasDraftMessage = mHeader.conversation.numDrafts() > 0;

        // Parse senders fragments.
        if (mHeader.preserveSendersText) {
            // This is a special view that doesn't need special sender formatting
            mHeader.sendersDisplayText = new SpannableStringBuilder(mHeader.sendersText);
            loadImages();
        } else if (mHeader.conversation.conversationInfo != null) {
            Context context = getContext();
            mHeader.messageInfoString = SendersView
                    .createMessageInfo(context, mHeader.conversation, true);
            int maxChars = ConversationItemViewCoordinates.getSendersLength(context,
                    mCoordinates.getMode(), mHeader.conversation.hasAttachments);
            mHeader.displayableEmails = new ArrayList<String>();
            mHeader.displayableNames = new ArrayList<String>();
            mHeader.styledNames = new ArrayList<SpannableString>();

            SendersView.format(context, mHeader.conversation.conversationInfo,
                    mHeader.messageInfoString.toString(), maxChars, mHeader.styledNames,
                    mHeader.displayableNames, mHeader.displayableEmails, mAccount,
                    mDisplayedFolder.shouldShowRecipients(), true);

            if (mHeader.displayableEmails.isEmpty() || mHeader.hasDraftMessage) {//TS: junwei-xu 2015-1-22 EMAIL BUGFIX_900708 MOD
                mHeader.displayableEmails.add(mAccount);
                mHeader.displayableNames.add(mAccount);
            }

            // If we have displayable senders, load their thumbnails
            loadImages();
        } else {
            LogUtils.wtf(LOG_TAG, "Null conversationInfo");
        }

        if (mHeader.isLayoutValid()) {
            pauseTimer(PERF_TAG_CALCULATE_TEXTS_BITMAPS);
            return;
        }
        startTimer(PERF_TAG_CALCULATE_FOLDERS);


        pauseTimer(PERF_TAG_CALCULATE_FOLDERS);

        // Paper clip icon.
        mHeader.paperclip = null;
        if (mHeader.conversation.hasAttachments) {
            mHeader.paperclip = ATTACHMENT;
        }

        startTimer(PERF_TAG_CALCULATE_SENDER_SUBJECT);

        pauseTimer(PERF_TAG_CALCULATE_SENDER_SUBJECT);
        pauseTimer(PERF_TAG_CALCULATE_TEXTS_BITMAPS);
    }

    // FIXME(ath): maybe move this to bind(). the only dependency on layout is on tile W/H, which
    // is immutable.
    private void loadImages() {
        if (mGadgetMode != ConversationItemViewCoordinates.GADGET_CONTACT_PHOTO
                || mHeader.displayableEmails == null
                || mHeader.displayableEmails.isEmpty()) {
            return;
        }
        if (mCoordinates.contactImagesWidth <= 0 || mCoordinates.contactImagesHeight <= 0) {
            LogUtils.w(LOG_TAG,
                    "Contact image width(%d) or height(%d) is 0 for mode: (%d).",
                    mCoordinates.contactImagesWidth, mCoordinates.contactImagesHeight,
                    mCoordinates.getMode());
            return;
        }

        mSendersImageView
                .setBounds(0, 0, mCoordinates.contactImagesWidth, mCoordinates.contactImagesHeight);

        Utils.traceBeginSection("load sender image");
        final ContactDrawable drawable = mSendersImageView.getContactDrawable();
        drawable.setDecodeDimensions(mCoordinates.contactImagesWidth,
                mCoordinates.contactImagesHeight);
        drawable.bind(mHeader.displayableNames.get(0), mHeader.displayableEmails.get(0));
        Utils.traceEndSection();
    }

    private static int makeExactSpecForSize(int size) {
        return MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
    }

    private static void layoutViewExactly(View v, int w, int h) {
        v.measure(makeExactSpecForSize(w), makeExactSpecForSize(h));
        v.layout(0, 0, w, h);
    }

    private void layoutParticipantText(SpannableStringBuilder participantText) {
        if (participantText != null) {
            if (isActivated() && showActivatedText()) {
                participantText.setSpan(sActivatedTextSpan, 0,
                        mHeader.styledMessageInfoStringOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                participantText.removeSpan(sActivatedTextSpan);
            }

            //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_S
            //Note sender width should use define in xml
            final int w = mCoordinates.sendersWidth;
            //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_E
            final int h = mCoordinates.sendersHeight;
            mSendersTextView.setLayoutParams(new ViewGroup.LayoutParams(w, h));
            mSendersTextView.setMaxLines(mCoordinates.sendersLineCount);
            mSendersTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCoordinates.sendersFontSize);
            layoutViewExactly(mSendersTextView, w, h);

            /// TCT: add for search term highlight @{
            boolean hasFilter = (mSearchParams != null && !TextUtils.isEmpty(mSearchParams.mFilter))
                    ;
            boolean fieldMatched = (mSearchParams != null && (SearchParams.SEARCH_FIELD_FROM.equals(
                    mSearchParams.mField)
                    || SearchParams.SEARCH_FIELD_ALL.equals(mSearchParams.mField)
            || SearchParams.SEARCH_FIELD_TO.equals(mSearchParams.mField))); //porting from PR937141
            if (hasFilter && fieldMatched) {
                CharacterStyle[] spans = participantText.getSpans(0, participantText.length(), CharacterStyle.class);
                String senderToHightlight = participantText.toString();
                CharSequence highlightedSender = TextUtilities.highlightTermsInText(senderToHightlight, mSearchParams.mFilter);
                highlightedSender = copyStyles(spans, highlightedSender);
                mSendersTextView.setText(highlightedSender);
            } else {
                mSendersTextView.setText(participantText);
            }
            /// @}
        }
    }

    private void createSubject(final boolean isUnread) {
        final String badgeText = mHeader.badgeText == null ? "" : mHeader.badgeText;
        String subject = filterTag(getContext(), mHeader.conversation.subject);
        subject = Conversation.getSubjectForDisplay(mContext, badgeText, subject);

        /// TCT: add for search term highlight
        // process subject and snippet respectively @{
        SpannableStringBuilder subjectToHighlight = new SpannableStringBuilder(
                subject);
        boolean hasFilter = (mSearchParams != null && !TextUtils.isEmpty(mSearchParams.mFilter));
        if (hasFilter) {
            boolean fieldMatchedSubject = (mSearchParams != null && (SearchParams.SEARCH_FIELD_SUBJECT.equals(mSearchParams.mField)
                    || SearchParams.SEARCH_FIELD_ALL.equals(mSearchParams.mField)));
            /// TCT: Only highlight un-empty subject
            if (fieldMatchedSubject && !TextUtils.isEmpty(subject)) {
                CharSequence subjectChars = TextUtilities.highlightTermsInText(subject, mSearchParams.mFilter);
                subjectToHighlight.replace(0, subject.length(), subjectChars);
            }
        }
        /// @}
        final Spannable displayedStringBuilder = new SpannableString(subjectToHighlight);

        // since spans affect text metrics, add spans to the string before measure/layout or fancy
        // ellipsizing

        final int badgeTextLength = formatBadgeText(displayedStringBuilder, badgeText);

        if (!TextUtils.isEmpty(subject)) {
            displayedStringBuilder.setSpan(TextAppearanceSpan.wrap(
                    isUnread ? sSubjectTextUnreadSpan : sSubjectTextReadSpan),
                    badgeTextLength, subject.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isActivated() && showActivatedText()) {
            displayedStringBuilder.setSpan(sActivatedTextSpan, badgeTextLength,
                    displayedStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        final int subjectWidth = mSubjectWidth;//TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD
        final int subjectHeight = mCoordinates.subjectHeight;
        mSubjectTextView.setLayoutParams(new ViewGroup.LayoutParams(subjectWidth, subjectHeight));
        mSubjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCoordinates.subjectFontSize);
        layoutViewExactly(mSubjectTextView, subjectWidth, subjectHeight);

        //[FEATURE]-Mod-BEGIN by CDTS.zhonghua.tuo,05/29/2014,FR 670064
        SpannableStringBuilder builder = new SpannableStringBuilder();
        boolean filterSubject = false;
        if(mField == UIProvider.LOCAL_SEARCH_ALL || mField == UIProvider.LOCAL_SEARCH_SUBJECT) {
            filterSubject = true;
        }
        if(mQueryText != null && filterSubject) {
            CharSequence formatSubject = displayedStringBuilder;
            formatSubject = TextUtilities.highlightTermsInText(subject, mQueryText);
            builder.append(formatSubject);
            mSubjectTextView.setText(builder);
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
            //store the displayed subject for calculate the statusView's X and width
            mHeader.subjectText = builder.toString();
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
        }else {
            mSubjectTextView.setText(displayedStringBuilder);
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
            mHeader.subjectText = displayedStringBuilder.toString();
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
        }
        //[FEATURE]-Mod-END by CDTS.zhonghua.tuo
    }

 // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    private void createStatus() {
        final String display = mHeader.conversation.getStatusForDisplay(mContext);
        final int statusWidth = mStatusWidth;
        final int statusHeight = mCoordinates.statusHeight;
        final int status = mHeader.conversation.getStatus();
        if (status == EmailContent.Message.MAIL_IN_FAILED_STATUS) {
            mStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            mStatusTextView.setTextColor(getResources().getColor(R.color.status_text_color));
        }
        mStatusTextView.setText(display);
        mStatusTextView.setLayoutParams(new ViewGroup.LayoutParams(statusWidth, statusHeight));
        mStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCoordinates.subjectFontSize);
        layoutViewExactly(mStatusTextView, statusWidth, statusHeight);
        //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 ADD_S
        //Note: Update conversation sending status in conversation selected set.
        if (mSelectedConversationSet != null) {
            mSelectedConversationSet.updateSendingStatus(mHeader.conversation);
        }
        //TS: jian.xu 2015-11-27 EMAIL BUGFIX-980186 ADD_E
    }
 // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S

    private void createSnippet() {
        final String snippet = mHeader.conversation.getSnippet();
        /// TCT: add for search term highlight
        // process subject and snippet respectively @{
        SpannableStringBuilder snippetToHighlight = new SpannableStringBuilder(snippet);
        boolean hasFilter = (mSearchParams != null && !TextUtils.isEmpty(mSearchParams.mFilter));
        if (hasFilter) {
            boolean fieldMatchedSnippet = (mSearchParams != null && (SearchParams.SEARCH_FIELD_BODY.
                    equals(mSearchParams.mField)
                    || SearchParams.SEARCH_FIELD_ALL.equals(mSearchParams.mField)));

            /// TCT: Only highlight un-empty snippet
            if (fieldMatchedSnippet && !TextUtils.isEmpty(snippet)) {
                CharSequence snippetChars = TextUtilities.highlightTermsInText(snippet, mSearchParams.mFilter);
                snippetToHighlight.replace(0, snippet.length(), snippetChars);
            }
        }
        /// @}
        final Spannable displayedStringBuilder = new SpannableString(snippetToHighlight);

        // measure the width of the folders which overlap the snippet view
        final int folderWidth = mHeader.folderDisplayer.measureFolders(mCoordinates);

        // size the snippet view by subtracting the folder width from the maximum snippet width
        final int snippetWidth = mCoordinates.maxSnippetWidth - folderWidth;
        final int snippetHeight = mCoordinates.snippetHeight;
        mSnippetTextView.setLayoutParams(new ViewGroup.LayoutParams(snippetWidth, snippetHeight));
        mSnippetTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCoordinates.snippetFontSize);
        layoutViewExactly(mSnippetTextView, snippetWidth, snippetHeight);

        mSnippetTextView.setText(displayedStringBuilder);
    }

    private int formatBadgeText(Spannable displayedStringBuilder, String badgeText) {
        final int badgeTextLength = (badgeText != null) ? badgeText.length() : 0;
        if (!TextUtils.isEmpty(badgeText)) {
            displayedStringBuilder.setSpan(TextAppearanceSpan.wrap(sBadgeTextSpan),
                    0, badgeTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            displayedStringBuilder.setSpan(TextAppearanceSpan.wrap(sBadgeBackgroundSpan),
                    0, badgeTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            displayedStringBuilder.setSpan(new BadgeSpan(displayedStringBuilder, this),
                    0, badgeTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return badgeTextLength;
    }

    // START BadgeSpan.BadgeSpanDimensions override

    @Override
    public int getHorizontalPadding() {
        return sBadgePaddingExtraWidth;
    }

    @Override
    public float getRoundedCornerRadius() {
        return sBadgeRoundedCornerRadius;
    }

    // END BadgeSpan.BadgeSpanDimensions override

    private boolean showActivatedText() {
        // For activated elements in tablet in conversation mode, we show an activated color, since
        // the background is dark blue for activated versus gray for non-activated.
        return mTabletDevice && !mListCollapsible;
    }

    private void calculateCoordinates() {
        startTimer(PERF_TAG_CALCULATE_COORDINATES);

        sPaint.setTextSize(mCoordinates.dateFontSize);
        sPaint.setTypeface(Typeface.DEFAULT);

        final boolean isRtl = ViewUtils.isViewRtl(this);

        mDateWidth = (int) sPaint.measureText(
                mHeader.dateText != null ? mHeader.dateText.toString() : "");
        if (mHeader.infoIcon != null) {
            mInfoIconX = (isRtl) ? mCoordinates.infoIconX :
                    mCoordinates.infoIconXRight - mHeader.infoIcon.getWidth();

            // If we have an info icon, we start drawing the date text:
            // At the end of the date TextView minus the width of the date text
            // In RTL mode, we just use dateX
            mDateX = (isRtl) ? mCoordinates.dateX : mCoordinates.dateXRight - mDateWidth;
        } else {
            // If there is no info icon, we start drawing the date text:
            // At the end of the info icon ImageView minus the width of the date text
            // We use the info icon ImageView for positioning, since we want the date text to be
            // at the right, since there is no info icon
            // In RTL, we just use infoIconX
            mDateX = (isRtl) ? mCoordinates.infoIconX : mCoordinates.infoIconXRight - mDateWidth;
        }

        // TS: zhaotianyong 2014-12-15 EMAIL BUGFIX_859814 MOD_S
        // The paperclip is drawn starting at the start of the date text minus
        // the width of the paperclip and the date padding.
        // In RTL mode, it is at the end of the date (mDateX + mDateWidth) plus the
        // start date padding.
        mPaperclipX = (isRtl) ? mDateX + mDateWidth + mCoordinates.datePaddingStart :
                mDateX - ATTACHMENT.getWidth() - mCoordinates.datePaddingStart;

        //TS: junwei-xu 2015-06-01 EMAIL BUGFIX_989906 MOD_S
        Bitmap priorityBitmap = sNormalPriorityIcon;
        mPriority = mHeader.conversation.flagPriority;
        if (mPriority == HIGH_PRIORITY) {
            priorityBitmap = sHighPriorityIcon;
        } else if (mPriority == LOW_PRIORITY) {
            priorityBitmap = sLowPriorityIcon;
        }
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        /*
        if (mHeader.paperclip != null) {
            mPriorityX = mPaperclipX - priorityBitmap.getWidth();
        } else {
            mPriorityX = mDateX - priorityBitmap.getWidth();
        }
        */
        if (mHeader.paperclip != null) {
            mPriorityX = (isRtl) ? mPaperclipX + ATTACHMENT.getWidth() + mCoordinates.paperclipPaddingStart :
                    mPaperclipX - priorityBitmap.getWidth();
        } else {
            mPriorityX = (isRtl) ? mDateX + mDateWidth + mCoordinates.datePaddingStart :
                    mDateX - priorityBitmap.getWidth();
        }
        //[FEATURE]-Add-END by TSCD.chao zhang
        //TS: junwei-xu 2015-06-01 EMAIL BUGFIX_989906 MOD_E
        // TS: zhaotianyong 2014-12-15 EMAIL BUGFIX_859814 MOD_E

        // In normal mode, the senders x and width is based
        // on where the date/attachment icon start.

        // Have this end near the paperclip or date, not the folders.

        //TS: junwei-xu 2015-06-01 EMAIL BUGFIX_989906 MOD_S
        if (mHeader.paperclip != null) {
            // If there is a paperclip, the date/attachment start is at the start
            // of the paperclip minus the paperclip padding.
            // In RTL, it is at the end of the paperclip plus the paperclip padding.
            dateAttachmentStart = (isRtl) ?
                    mPaperclipX + ATTACHMENT.getWidth() + mCoordinates.paperclipPaddingStart + priorityBitmap.getWidth()
                    : mPaperclipX - mCoordinates.paperclipPaddingStart;
        } else {
            // If no paperclip, just use the start of the date minus the date padding start.
            // In RTL mode, this is just the paperclipX.
            dateAttachmentStart = (isRtl) ?
                    mPaperclipX + priorityBitmap.getWidth() : mDateX - mCoordinates.datePaddingStart;
        }
        //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_S
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        // NOTE:use measureText to get the status's length. and we only want draw the status textView when in OUTBOX
        // that in other box,user can see more subject.
        int statusWdithWithPanding = 0;
        if (mDisplayedFolder != null && mDisplayedFolder.isOutbox()) {
            String status = mHeader.conversation.getMaxLengthStatusDisplay(mContext);
            mStatusWidth = (int) mStatusTextView.getPaint().measureText(status);
            statusWdithWithPanding = mStatusWidth + mCoordinates.statusPaddingStart
                    + mCoordinates.statusPanddingEnd;
        }
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
        // Subject width is the dateAttachmentStart - subjectX.
        // In RTL, it is subjectWidth + subjectX - dateAttachmentStart.
        mSubjectWidth = (isRtl) ?
                mCoordinates.subjectWidth + mCoordinates.subjectX - dateAttachmentStart
                : mPriorityX - mCoordinates.subjectX - statusWdithWithPanding;
        mSubjectX = (isRtl) ? dateAttachmentStart : mCoordinates.subjectX;
        //TS: junwei-xu 2015-06-01 EMAIL BUGFIX_989906 MOD_E

        // Second pass to layout each fragment.
        sPaint.setTextSize(mCoordinates.subjectFontSize);
        sPaint.setTypeface(Typeface.DEFAULT);

        if (mHeader.styledNames != null) {
            final SpannableStringBuilder participantText = elideParticipants(mHeader.styledNames);
            layoutParticipantText(participantText);
        } else {
            // First pass to calculate width of each fragment.
            if (mSubjectWidth < 0) {
                mSubjectWidth = 0;
            }

            mHeader.subjectDisplayLayout = new StaticLayout(mHeader.subjectDisplayText, sPaint,
                    mSubjectWidth, Alignment.ALIGN_NORMAL, 1, 0, true);
        }

        if (mSubjectWidth < 0) {
            mSubjectWidth = 0;
        }
        //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_E
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        //NOTE: currently,use calculateStatus after subject created.
        //calculateStatus(isRtl);
        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
        pauseTimer(PERF_TAG_CALCULATE_COORDINATES);
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    /**
     * Calculate the statusView's X and width
     * @param isRtl
     */
    private void calculateStatus(boolean isRtl) {
        String subject = mHeader.subjectText;
        int filledSubjectCharsLength = (int) sPaint.measureText(subject);
        if (filledSubjectCharsLength + mStatusWidth + mSubjectX
                + mCoordinates.statusPaddingStart <= mPriorityX) {
            mStatusX = (isRtl) ? dateAttachmentStart : mSubjectX + filledSubjectCharsLength
                    + mCoordinates.statusPaddingStart;
        } else {
            mStatusX = (isRtl) ? dateAttachmentStart : mSubjectX + mSubjectWidth
                    + mCoordinates.statusPaddingStart;
        }
        sPaint.setTextSize(mCoordinates.statusFontSize);
        sPaint.setTypeface(Typeface.DEFAULT);
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    // The rules for displaying elided participants are as follows:
    // 1) If there is message info (either a COUNT or DRAFT info to display), it MUST be shown
    // 2) If senders do not fit, ellipsize the last one that does fit, and stop
    // appending new senders
    SpannableStringBuilder elideParticipants(List<SpannableString> parts) {
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        float totalWidth = 0;
        boolean ellipsize = false;
        float width;
        boolean skipToHeader = false;

        // start with "To: " if we're showing recipients
        if (mDisplayedFolder.shouldShowRecipients() && !parts.isEmpty()) {
            final SpannableString toHeader = SendersView.getFormattedToHeader();
            CharacterStyle[] spans = toHeader.getSpans(0, toHeader.length(),
                    CharacterStyle.class);
            // There is only 1 character style span; make sure we apply all the
            // styles to the paint object before measuring.
            if (spans.length > 0) {
                spans[0].updateDrawState(sPaint);
            }
            totalWidth += sPaint.measureText(toHeader.toString());
            builder.append(toHeader);
            skipToHeader = true;
        }

        final SpannableStringBuilder messageInfoString = mHeader.messageInfoString;
        if (messageInfoString.length() > 0) {
            CharacterStyle[] spans = messageInfoString.getSpans(0, messageInfoString.length(),
                    CharacterStyle.class);
            // There is only 1 character style span; make sure we apply all the
            // styles to the paint object before measuring.
            if (spans.length > 0) {
                spans[0].updateDrawState(sPaint);
            }
            // Paint the message info string to see if we lose space.
            float messageInfoWidth = sPaint.measureText(messageInfoString.toString());
            totalWidth += messageInfoWidth;
        }
       SpannableString prevSender = null;
       SpannableString ellipsizedText;
        for (SpannableString sender : parts) {
            // There may be null sender strings if there were dupes we had to remove.
            if (sender == null) {
                continue;
            }
            // No more width available, we'll only show fixed fragments.
            if (ellipsize) {
                break;
            }
            CharacterStyle[] spans = sender.getSpans(0, sender.length(), CharacterStyle.class);
            // There is only 1 character style span.
            if (spans.length > 0) {
                spans[0].updateDrawState(sPaint);
            }
            // If there are already senders present in this string, we need to
            // make sure we prepend the dividing token
            if (SendersView.sElidedString.equals(sender.toString())) {
                prevSender = sender;
                sender = copyStyles(spans, sElidedPaddingToken + sender + sElidedPaddingToken);
            } else if (!skipToHeader && builder.length() > 0
                    && (prevSender == null || !SendersView.sElidedString.equals(prevSender
                            .toString()))) {
                prevSender = sender;
                sender = copyStyles(spans, sSendersSplitToken + sender);
            } else {
                prevSender = sender;
                skipToHeader = false;
            }
            if (spans.length > 0) {
                spans[0].updateDrawState(sPaint);
            }
            //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_S
            // Measure the width of the current sender and make sure we have space
            width = (int) sPaint.measureText(sender.toString());
            if (width + totalWidth > mCoordinates.sendersWidth) {
                // The text is too long, new line won't help. We have to
                // ellipsize text.
                ellipsize = true;
                width = mCoordinates.sendersWidth - totalWidth; // ellipsis width?
                ellipsizedText = copyStyles(spans,
                        TextUtils.ellipsize(sender, sPaint, width, TruncateAt.END));
                width = (int) sPaint.measureText(ellipsizedText.toString());
            } else {
                ellipsizedText = null;
            }
            totalWidth += width;
            //TS: yanhua.chen 2015-9-2 EMAIL CR_540046 MOD_E

            //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/29/2014,FR 670064
            CharSequence fragmentDisplayText;
            if (ellipsizedText != null) {
                fragmentDisplayText = ellipsizedText;
            } else {
                fragmentDisplayText = sender;
            }
            boolean filterSender = false;
            if (mField == UIProvider.LOCAL_SEARCH_ALL || mField == UIProvider.LOCAL_SEARCH_FROM) {
                filterSender = true;
            }
            if (mQueryText != null && filterSender) {
                fragmentDisplayText = TextUtilities.highlightTermsInText(fragmentDisplayText.toString(),mQueryText);
            }
            //[FEATURE]-Add-END by CDTS.zhonghua.tuo
            builder.append(fragmentDisplayText);
        }
        mHeader.styledMessageInfoStringOffset = builder.length();
        builder.append(messageInfoString);
        return builder;
    }

    private static SpannableString copyStyles(CharacterStyle[] spans, CharSequence newText) {
        SpannableString s = new SpannableString(newText);
        if (spans != null && spans.length > 0) {
            s.setSpan(spans[0], 0, s.length(), 0);
        }
        return s;
    }

    /**
     * If the subject contains the tag of a mailing-list (text surrounded with
     * []), return the subject with that tag ellipsized, e.g.
     * "[android-gmail-team] Hello" -> "[andr...] Hello"
     */
    public static String filterTag(Context context, String subject) {
        String result = subject;
        String formatString = context.getResources().getString(R.string.filtered_tag);
        if (!TextUtils.isEmpty(subject) && subject.charAt(0) == '[') {
            int end = subject.indexOf(']');
            if (end > 0) {
                String tag = subject.substring(1, end);
                result = String.format(formatString, Utils.ellipsize(tag, 7),
                        subject.substring(end + 1));
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TS: jin.dongjin 2015-06-23 EMAIL BUGFIX_1015610 MOD_S
        if (mCoordinates == null) {
            LogUtils.e(LOG_TAG, "null coordinates in ConversationItemView#onDraw");
            return;
        }
        // TS: jin.dongjin 2015-06-23 EMAIL BUGFIX_1015610 MOD_E

        Utils.traceBeginSection("CIVC.draw");

        // Contact photo
        if (mGadgetMode == ConversationItemViewCoordinates.GADGET_CONTACT_PHOTO) {
            canvas.save();
            Utils.traceBeginSection("draw senders image");
            drawSendersImage(canvas);
            Utils.traceEndSection();
            canvas.restore();
        }

        //TS: yanhua.chen 2015-9-2 EMAIL CR_ID MOD_S
        // Subject.
        boolean isUnread = mHeader.unread;
        // Old style subject; apply text colors/ sizes/ styling.
        canvas.save();
        if (mHeader.subjectDisplayLayout != null) {
            sPaint.setTextSize(mCoordinates.subjectFontSize);
            sPaint.setTypeface(Typeface.DEFAULT);
            // [FEATURE]-ADD-BEGIN by TSNJ,zhujian.shao,10/23/2014,FR-736411(porting from FR622697)
            canvas.translate(mSubjectWidth-mPriorityX, mCoordinates.subjectY
                    + mHeader.subjectDisplayLayout.getTopPadding());
            // [FEATURE]-ADD-END by TSNJ,zhujian.shao,10/23/2014,FR-736411
            mHeader.subjectDisplayLayout.draw(canvas);
        } else {
            drawSubject(canvas);
        }
        canvas.restore();

        // Sender.
        sPaint.setTypeface(Typeface.DEFAULT);
        canvas.save();
        drawSenders(canvas);
        canvas.restore();
        //TS: yanhua.chen 2015-9-2 EMAIL CR_ID MOD_E


        canvas.save();
        drawSnippet(canvas);
        canvas.restore();

        // Folders.
        if (mConfig.areFoldersVisible()) {
            mHeader.folderDisplayer.drawFolders(canvas, mCoordinates, ViewUtils.isViewRtl(this));
        }

        // If this folder has a color (combined view/Email), show it here
        if (mConfig.isColorBlockVisible()) {
            sFoldersPaint.setColor(mHeader.conversation.color);
            sFoldersPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mCoordinates.colorBlockX, mCoordinates.colorBlockY,
                    mCoordinates.colorBlockX + mCoordinates.colorBlockWidth,
                    mCoordinates.colorBlockY + mCoordinates.colorBlockHeight, sFoldersPaint);
        }

        // Draw the reply state. Draw nothing if neither replied nor forwarded.
        if (mConfig.isReplyStateVisible()) {
            if (mHeader.hasBeenRepliedTo && mHeader.hasBeenForwarded) {
                canvas.drawBitmap(STATE_REPLIED_AND_FORWARDED, mCoordinates.replyStateX,
                        mCoordinates.replyStateY, null);
            } else if (mHeader.hasBeenRepliedTo) {
                canvas.drawBitmap(STATE_REPLIED, mCoordinates.replyStateX,
                        mCoordinates.replyStateY, null);
            } else if (mHeader.hasBeenForwarded) {
                canvas.drawBitmap(STATE_FORWARDED, mCoordinates.replyStateX,
                        mCoordinates.replyStateY, null);
            } else if (mHeader.isInvite) {
                canvas.drawBitmap(STATE_CALENDAR_INVITE, mCoordinates.replyStateX,
                        mCoordinates.replyStateY, null);
            }
        }

        if (mConfig.isPersonalIndicatorVisible()) {
            canvas.drawBitmap(mHeader.personalLevelBitmap, mCoordinates.personalIndicatorX,
                    mCoordinates.personalIndicatorY, null);
        }

        // Info icon
        if (mHeader.infoIcon != null) {
            canvas.drawBitmap(mHeader.infoIcon, mInfoIconX, mCoordinates.infoIconY, sPaint);
        }

        // Date.
        sPaint.setTextSize(mCoordinates.dateFontSize);
        sPaint.setTypeface(isUnread ? Typeface.SANS_SERIF : SANS_SERIF_LIGHT);
        sPaint.setColor(isUnread ? sDateTextColorUnread : sDateTextColorRead);
        drawText(canvas, mHeader.dateText, mDateX, mCoordinates.dateYBaseline, sPaint);
      //TS: yanhua.chen 2015-6-19 EMAIL BUGFIX_305581 MOD_S
      //TS: wenggangjin 2015-01-27 EMAIL BUGFIX_-888881 MOD_S
      //TS: wenggangjin 2015-01-15 EMAIL BUGFIX_886241 MOD_S
//        if (mHeader.conversation.hasAttachments && !mDisplayedFolder.isSearch()) {
        if (mHeader.conversation.hasAttachments) {
            mHeader.paperclip = ATTACHMENT;
        }else{
            mHeader.paperclip = null;
        }
      //TS: wenggangjin 2015-01-15 EMAIL BUGFIX_886241 MOD_E
      //TS: wenggangjin 2015-01-27 EMAIL BUGFIX_-888881 MOD_S
      //TS: yanhua.chen 2015-6-19 EMAIL BUGFIX_305581 MOD_E
        // Paper clip icon.
        if (mHeader.paperclip != null) {
            canvas.drawBitmap(mHeader.paperclip, mPaperclipX, mCoordinates.paperclipY, sPaint);
        }

        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        //priority icon
        Bitmap sPrio = sNormalPriorityIcon;
        mPriority=mHeader.conversation.flagPriority;
        if(mPriority == HIGH_PRIORITY) {
            sPrio=sHighPriorityIcon;
        } else if(mPriority == LOW_PRIORITY){
             sPrio=sLowPriorityIcon;
        }
        // TS: zhaotianyong 2014-12-15 EMAIL BUGFIX_859814 ADD_S
        sPrio=resizeBitMap(sPrio,0.6f,0.6f);
        // TS: zhaotianyong 2014-12-15 EMAIL BUGFIX_859814 ADD_E
        canvas.drawBitmap(sPrio, mPriorityX, mCoordinates.paperclipY, null);

        //[FEATURE]-Add-END by TSCD.chao zhang

        if (mStarEnabled) {
            // Star.
            canvas.drawBitmap(getStarBitmap(), mCoordinates.starX, mCoordinates.starY, sPaint);
        }

        // right-side edge effect when in tablet conversation mode and the list is not collapsed
        if (Utils.getDisplayListRightEdgeEffect(mTabletDevice, mListCollapsible,
                mConfig.getViewMode())) {
            final boolean isRtl = ViewUtils.isViewRtl(this);
            RIGHT_EDGE_TABLET.setBounds(
                    (isRtl) ? 0 : getWidth() - RIGHT_EDGE_TABLET.getIntrinsicWidth(), 0,
                    (isRtl) ? RIGHT_EDGE_TABLET.getIntrinsicWidth() : getWidth(), getHeight());
            RIGHT_EDGE_TABLET.draw(canvas);

            if (isActivated()) {
                final int w = VISIBLE_CONVERSATION_HIGHLIGHT.getIntrinsicWidth();
                VISIBLE_CONVERSATION_HIGHLIGHT.setBounds(
                        (isRtl) ? getWidth() - w : 0, 0,
                        (isRtl) ? getWidth() : w, getHeight());
                VISIBLE_CONVERSATION_HIGHLIGHT.draw(canvas);
            }
        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_539892 MOD_S
        if (mWillDrawDivider){
            // draw the inset divider
            sDividerPaint.setColor(sDividerColor);
            final int dividerBottomY = getHeight();
            final int dividerTopY = dividerBottomY - sDividerHeight;
            // AM: Kexue.Geng 2015-02-28 EMAIL BUGFIX_900927 MOD_S
            // canvas.drawRect(sDividerInset, dividerTopY, getWidth(), dividerBottomY, sDividerPaint);
            canvas.drawRect(mGadgetMode == ConversationItemViewCoordinates.GADGET_NONE ? 0 : sDividerInset, dividerTopY, getWidth(), dividerBottomY, sDividerPaint);
            // AM: Kexue.Geng 2015-02-28 EMAIL BUGFIX_900927 MOD_E
        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_539892 MOD_E
        Utils.traceEndSection();
     // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
        //draw the status
        canvas.save();
        drawStatus(canvas);
        canvas.restore();
     // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    }

    // TS: zhaotianyong 2014-12-15 EMAIL BUGFIX_859814 ADD_S
    private Bitmap resizeBitMap(Bitmap bitmap,float width_scale, float height_scal){
        Matrix matrix = new Matrix();
        matrix.postScale(width_scale, height_scal);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBitmap;
    }
    // TS: zhaotianyong 2014-12-15 EMAIL BUGFIX_859814 ADD_E

    private void drawSendersImage(final Canvas canvas) {
        if (!mSendersImageView.isFlipping()) {
            final boolean showSenders = !isSelected();
            mSendersImageView.reset(showSenders);
        }
        canvas.translate(mCoordinates.contactImagesX, mCoordinates.contactImagesY);
        if (mPhotoBitmap == null) {
            mSendersImageView.draw(canvas);
        } else {
            canvas.drawBitmap(mPhotoBitmap, null, mPhotoRect, sPaint);
        }
    }

    private void drawSubject(Canvas canvas) {
        canvas.translate(mSubjectX, mCoordinates.subjectY);
        mSubjectTextView.draw(canvas);
    }

    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-ID ADD_S
    private void drawStatus(Canvas canvas) {
        canvas.translate(mStatusX, mCoordinates.statusY);
        mStatusTextView.draw(canvas);
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-ID ADD_E

    private void drawSnippet(Canvas canvas) {
        // if folders exist, their width will be the max width - actual width
        final int folderWidth = mCoordinates.maxSnippetWidth - mSnippetTextView.getWidth();

        // in RTL layouts we move the snippet to the right so it doesn't overlap the folders
        final int x = mCoordinates.snippetX + (ViewUtils.isViewRtl(this) ? folderWidth : 0);
        canvas.translate(x, mCoordinates.snippetY);
        mSnippetTextView.draw(canvas);
    }

    private void drawSenders(Canvas canvas) {
        canvas.translate(mCoordinates.sendersX, mCoordinates.sendersY);
        mSendersTextView.draw(canvas);
    }

    private Bitmap getStarBitmap() {
        return mHeader.conversation.starred ? STAR_ON : STAR_OFF;
    }

    private static void drawText(Canvas canvas, CharSequence s, int x, int y, TextPaint paint) {
        canvas.drawText(s, 0, s.length(), x, y, paint);
    }

    /**
     * Set the background for this item based on:
     * 1. Read / Unread (unread messages have a lighter background)
     * 2. Tablet / Phone
     * 3. Checkbox checked / Unchecked (controls CAB color for item)
     * 4. Activated / Not activated (controls the blue highlight on tablet)
     */
    private void updateBackground() {
        final int background;
        if (mBackgroundOverrideResId > 0) {
            background = mBackgroundOverrideResId;
        } else {
            background = R.drawable.conversation_item_background_selector;
        }
        setBackgroundResource(background);
    }

    /**
     * Toggle the check mark on this view and update the conversation or begin
     * drag, if drag is enabled.
     */
    @Override
    public boolean toggleSelectedStateOrBeginDrag() {
        ViewMode mode = mActivity.getViewMode();
        if (mTabletDevice && mode.isListMode()) {
            return beginDragMode();
        } else {
            return toggleSelectedState("long_press");
        }
    }

    @Override
    public boolean toggleSelectedState() {
        return toggleSelectedState(null);
    }

    private boolean toggleSelectedState(final String sourceOpt) {
        if (mHeader != null && mHeader.conversation != null && mSelectedConversationSet != null) {
            //TS: junwei-xu 2015-07-08 EMAIL BUGFIX_964544 ADD_S
            //Note: maybe the system soft keyboard will coverd the toast for the operate's result,
            //such as delete,mark unread... in actionbar, so, hide the system soft keyboard.
            hideSoftKeyboard();
            //TS: junwei-xu 2015-07-08 EMAIL BUGFIX_964544 ADD_E
            mSelected = !mSelected;
            setSelected(mSelected);
            final Conversation conv = mHeader.conversation;
            // Set the list position of this item in the conversation
            final SwipeableListView listView = getListView();

            try {
                conv.position = mSelected && listView != null ? listView.getPositionForView(this)
                        : Conversation.NO_POSITION;
            } catch (final NullPointerException e) {
                // TODO(skennedy) Remove this if we find the root cause b/9527863
            }

            if (mSelectedConversationSet.isEmpty()) {
                final String source = (sourceOpt != null) ? sourceOpt : "checkbox";
                Analytics.getInstance().sendEvent("enter_cab_mode", source, null, 0);
            }

            mSelectedConversationSet.toggle(conv);
            //TS: zheng.zou 2016-01-12 EMAIL BUGFIX_1127720 ADD_S
            if (mSelectedConversationSet.contains(conv)) {
                mSelectedConversationSet.addObserver(this);
            } else {
                mSelectedConversationSet.removeObserver(this);
            }
            //TS: zheng.zou 2016-01-12 EMAIL BUGFIX_1127720 ADD_E
            if (mSelectedConversationSet.isEmpty()) {
                listView.commitDestructiveActions(true);
            }

            final boolean front = !mSelected;
            mSendersImageView.flipTo(front);

            // We update the background after the checked state has changed
            // now that we have a selected background asset. Setting the background
            // usually waits for a layout pass, but we don't need a full layout,
            // just an update to the background.
            requestLayout();

            return true;
        }

        return false;
    }

    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
    /**
     * When user click select all option, should change the state of conversation item view, such as
     * its background and sender image view
     */
    private void changeStateForSelectAll() {
        if(mHeader != null && mHeader.conversation != null && mSelectedConversationSet != null) {
            if(mSelected) {
                //if the conversation has been selected, do nothing
                return;
            }
            mSelected = true;
            setSelected(true);

            final Conversation conv = mHeader.conversation;
            // Set the list position of this item in the conversation
            final SwipeableListView listView = getListView();

            try {
                conv.position = listView != null ? listView.getPositionForView(this)
                        : Conversation.NO_POSITION;
            } catch (final NullPointerException e) {
                // TODO(skennedy) Remove this if we find the root cause b/9527863
            }

            if (mSelectedConversationSet.isEmpty()) {
                LogUtils.e(LogUtils.TAG, "What? selected set is empty ?");
                final String source = "checkbox";
                Analytics.getInstance().sendEvent("enter_cab_mode", source, null, 0);
                listView.commitDestructiveActions(true);
            }

            mSendersImageView.flipTo(false);

            // We update the background after the checked state has changed
            // now that we have a selected background asset. Setting the background
            // usually waits for a layout pass, but we don't need a full layout,
            // just an update to the background.
            requestLayout();
        }
    }
    //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E

    //TS: junwei-xu 2015-07-08 EMAIL BUGFIX_964544 ADD_S
    /**
     * hide the system's soft keyboard if it is active
     */
    private void hideSoftKeyboard() {
        if (mContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    //TS: junwei-xu 2015-07-08 EMAIL BUGFIX_964544 ADD_E

    @Override
    public void onSetEmpty() {
        mSendersImageView.flipTo(true);
        //TS: zheng.zou 2016-01-12 EMAIL BUGFIX_1127720 ADD_S
        if (mSelectedConversationSet != null) {
            mSelectedConversationSet.removeObserver(this);
        }
        //TS: zheng.zou 2016-01-12 EMAIL BUGFIX_1127720 ADD_E
    }

    @Override
    public void onSetPopulated(final ConversationSelectionSet set) { }

    @Override
    public void onSetChanged(final ConversationSelectionSet set) {
        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_S
        //If user select all cause onSetChanged, let's change the view state for all item view
        if(mSelectedConversationSet.isSelectAll()) {
            LogUtils.i(LogUtils.TAG, "Change the state for select all");
            changeStateForSelectAll();
        }
        //TS: Gantao 2015-12-16 EMAIL BUGFIX_1171140 ADD_E
    }

    /**
     * Toggle the star on this view and update the conversation.
     */
    public void toggleStar() {
        mHeader.conversation.starred = !mHeader.conversation.starred;
      //TS: wenggangjin 2015-01-15 EMAIL BUGFIX_902637 MOD_S
        for (Conversation conv : mSelectedConversationSet.values()) {
            if(conv.id == mHeader.conversation.id){
                conv.starred = mHeader.conversation.starred;
            }
        }
      //TS: wenggangjin 2015-01-15 EMAIL BUGFIX_902637 MOD_E
        Bitmap starBitmap = getStarBitmap();
        postInvalidate(mCoordinates.starX, mCoordinates.starY, mCoordinates.starX
                + starBitmap.getWidth(),
                mCoordinates.starY + starBitmap.getHeight());
        ConversationCursor cursor = (ConversationCursor) mAdapter.getCursor();
        if (cursor != null) {
            // TODO(skennedy) What about ads?
            cursor.updateBoolean(mHeader.conversation, ConversationColumns.STARRED,
                    mHeader.conversation.starred);
        }
    }

    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    /**
     * update the star status of selectedConversationSet
     * @param starrd
     */
    public void updateStarOfSelectedSet(boolean starrd) {
        if (mSelectedConversationSet == null || mHeader == null) {
            return;
        }
        for (Conversation conv : mSelectedConversationSet.values()) {
            if (conv.id == mHeader.conversation.id) {
                conv.starred = starrd;
                break;
            }
        }
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    private boolean isTouchInContactPhoto(float x, float y) {
        // Everything before the end edge of contact photo

        final boolean isRtl = ViewUtils.isViewRtl(this);
        final int threshold = (isRtl) ? mCoordinates.contactImagesX - sSenderImageTouchSlop :
                mCoordinates.contactImagesX + mCoordinates.contactImagesWidth
                + sSenderImageTouchSlop;

        // Allow touching a little right of the contact photo when we're already in selection mode
        final float extra;
        if (mSelectedConversationSet == null || mSelectedConversationSet.isEmpty()) {
            extra = 0;
        } else {
            extra = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
        }

        return mHeader.gadgetMode == ConversationItemViewCoordinates.GADGET_CONTACT_PHOTO
                && ((isRtl) ? x > (threshold - extra) : x < (threshold + extra));
    }

    private boolean isTouchInInfoIcon(final float x, final float y) {
        if (mHeader.infoIcon == null) {
            // We have no info icon
            return false;
        }

        final boolean isRtl = ViewUtils.isViewRtl(this);
        // Regardless of device, we always want to be end of the date's start touch slop
        if (((isRtl) ? x > mDateX + mDateWidth + sStarTouchSlop : x < mDateX - sStarTouchSlop)) {
            return false;
        }

        if (mStarEnabled) {
            // We allow touches all the way to the right edge, so no x check is necessary

            // We need to be above the star's touch area, which ends at the top of the subject
            // text
            return y < mCoordinates.subjectY;
        }

        // With no star below the info icon, we allow touches anywhere from the top edge to the
        // bottom edge
        return true;
    }

    private boolean isTouchInStar(float x, float y) {
        if (mHeader.infoIcon != null) {
            // We have an info icon, and it's above the star
            // We allow touches everywhere below the top of the subject text
            if (y < mCoordinates.subjectY) {
                return false;
            }
        }

        // Everything after the star and include a touch slop.
        return mStarEnabled && isTouchInStarTargetX(ViewUtils.isViewRtl(this), x);
    }

    private boolean isTouchInStarTargetX(boolean isRtl, float x) {
        return (isRtl) ? x < mCoordinates.starX + mCoordinates.starWidth + sStarTouchSlop
                : x >= mCoordinates.starX - sStarTouchSlop;
    }

    @Override
    public boolean canChildBeDismissed() {
        return mSwipeEnabled;
    }

    @Override
    public void dismiss() {
        SwipeableListView listView = getListView();
        if (listView != null) {
            listView.dismissChild(this);
        }
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_S
    public void showUndoToastBar(int action) {
        if (mActivity != null) {
            ((AbstractActivityController) mActivity.getAccountController()).showUndoToastBar(action);
        }
    }
    //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 ADD_E

    private boolean onTouchEventNoSwipe(MotionEvent event) {
        Utils.traceBeginSection("on touch event no swipe");
        boolean handled = false;

        int x = (int) event.getX();
        int y = (int) event.getY();
        mLastTouchX = x;
        mLastTouchY = y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchInContactPhoto(x, y) || isTouchInInfoIcon(x, y) || isTouchInStar(x, y)) {
                    mDownEvent = true;
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mDownEvent = false;
                break;

            case MotionEvent.ACTION_UP:
                if (mDownEvent) {
                    if (isTouchInContactPhoto(x, y)) {
                        // Touch on the check mark
                        toggleSelectedState();
                    } else if (isTouchInInfoIcon(x, y)) {
                        if (mConversationItemAreaClickListener != null) {
                            mConversationItemAreaClickListener.onInfoIconClicked();
                        }
                    } else if (isTouchInStar(x, y)) {
                        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-S
                        // star icon dose not has click response.
                        /*
                        // Touch on the star
                        if (mConversationItemAreaClickListener == null) {
                            toggleStar();
                        } else {
                            mConversationItemAreaClickListener.onStarClicked();
                        }
                        */
                        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-E
                    }
                    handled = true;
                }
                break;
        }

        if (!handled) {
            handled = super.onTouchEvent(event);
        }

        Utils.traceEndSection();
        return handled;
    }

    /**
     * ConversationItemView is given the first chance to handle touch events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.traceBeginSection("on touch event");
        int x = (int) event.getX();
        int y = (int) event.getY();
        mLastTouchX = x;
        mLastTouchY = y;
        if (!mSwipeEnabled) {
            Utils.traceEndSection();
            return onTouchEventNoSwipe(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchInContactPhoto(x, y) || isTouchInInfoIcon(x, y) || isTouchInStar(x, y)) {
                    mDownEvent = true;
                    Utils.traceEndSection();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mDownEvent) {
                    if (isTouchInContactPhoto(x, y)) {
                        // Touch on the check mark
                        Utils.traceEndSection();
                        mDownEvent = false;
                        toggleSelectedState();
                        Utils.traceEndSection();
                        return true;
                    } else if (isTouchInInfoIcon(x, y)) {
                        // Touch on the info icon
                        mDownEvent = false;
                        if (mConversationItemAreaClickListener != null) {
                            mConversationItemAreaClickListener.onInfoIconClicked();
                        }
                        Utils.traceEndSection();
                        return true;
                    } else if (isTouchInStar(x, y)) {
                        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-S
                        // star icon dose not has click response.
                        /*
                        // Touch on the star
                        mDownEvent = false;
                        if (mConversationItemAreaClickListener == null) {
                            toggleStar();
                        } else {
                            mConversationItemAreaClickListener.onStarClicked();
                        }
                        Utils.traceEndSection();
                        */
                        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-E
                        return true;
                    }
                }
                break;
        }
        // Let View try to handle it as well.
        boolean handled = super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Utils.traceEndSection();
            return true;
        }
        Utils.traceEndSection();
        return handled;
    }

    @Override
    public boolean performClick() {
        final boolean handled = super.performClick();
        final SwipeableListView list = getListView();
        if (!handled && list != null && list.getAdapter() != null) {
            final int pos = list.findConversation(this, mHeader.conversation);
            list.performItemClick(this, pos, mHeader.conversation.id);
        }
        return handled;
    }

    private View unwrap() {
        final ViewParent vp = getParent();
        if (vp == null || !(vp instanceof View)) {
            return null;
        }
        return (View) vp;
    }

    private SwipeableListView getListView() {
        SwipeableListView v = null;
        final View wrapper = unwrap();
        if (wrapper != null && wrapper instanceof SwipeableConversationItemView) {
            v = (SwipeableListView) ((SwipeableConversationItemView) wrapper).getListView();
        }
        if (v == null) {
            v = mAdapter.getListView();
        }
        return v;
    }

    /**
     * Reset any state associated with this conversation item view so that it
     * can be reused.
     */
    public void reset() {
        Utils.traceBeginSection("reset");
        setAlpha(1f);
        setTranslationX(0f);
        mAnimatedHeightFraction = 1.0f;
        Utils.traceEndSection();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setTranslationX(float translationX) {
        super.setTranslationX(translationX);

        // When a list item is being swiped or animated, ensure that the hosting view has a
        // background color set. We only enable the background during the X-translation effect to
        // reduce overdraw during normal list scrolling.
        final View parent = (View) getParent();
        if (parent == null) {
            LogUtils.w(LOG_TAG, "CIV.setTranslationX null ConversationItemView parent x=%s",
                    translationX);
        }

        if (parent instanceof SwipeableConversationItemView) {
            if (translationX != 0f) {
                parent.setBackgroundResource(R.color.swiped_bg_color);
            } else {
                parent.setBackgroundDrawable(null);
            }
        }
    }

    /**
     * Grow the height of the item and fade it in when bringing a conversation
     * back from a destructive action.
     */
    public Animator createSwipeUndoAnimation() {
        ObjectAnimator undoAnimator = createTranslateXAnimation(true);
        return undoAnimator;
    }

    /**
     * Grow the height of the item and fade it in when bringing a conversation
     * back from a destructive action.
     */
    public Animator createUndoAnimation() {
        ObjectAnimator height = createHeightAnimation(true);
        Animator fade = ObjectAnimator.ofFloat(this, "alpha", 0, 1.0f);
        fade.setDuration(sShrinkAnimationDuration);
        fade.setInterpolator(new DecelerateInterpolator(2.0f));
        AnimatorSet transitionSet = new AnimatorSet();
        transitionSet.playTogether(height, fade);
        transitionSet.addListener(new HardwareLayerEnabler(this));
        return transitionSet;
    }

    /**
     * Grow the height of the item and fade it in when bringing a conversation
     * back from a destructive action.
     */
    public Animator createDestroyWithSwipeAnimation() {
        ObjectAnimator slide = createTranslateXAnimation(false);
        ObjectAnimator height = createHeightAnimation(false);
        AnimatorSet transitionSet = new AnimatorSet();
        transitionSet.playSequentially(slide, height);
        return transitionSet;
    }

    private ObjectAnimator createTranslateXAnimation(boolean show) {
        SwipeableListView parent = getListView();
        // If we can't get the parent...we have bigger problems.
        int width = parent != null ? parent.getMeasuredWidth() : 0;
        final float start = show ? width : 0f;
        final float end = show ? 0f : width;
        ObjectAnimator slide = ObjectAnimator.ofFloat(this, "translationX", start, end);
        slide.setInterpolator(new DecelerateInterpolator(2.0f));
        slide.setDuration(sSlideAnimationDuration);
        return slide;
    }

    public Animator createDestroyAnimation() {
        return createHeightAnimation(false);
    }

    private ObjectAnimator createHeightAnimation(boolean show) {
        final float start = show ? 0f : 1.0f;
        final float end = show ? 1.0f : 0f;
        ObjectAnimator height = ObjectAnimator.ofFloat(this, "animatedHeightFraction", start, end);
        height.setInterpolator(new DecelerateInterpolator(2.0f));
        height.setDuration(sShrinkAnimationDuration);
        return height;
    }

    // Used by animator
    public void setAnimatedHeightFraction(float height) {
        mAnimatedHeightFraction = height;
        requestLayout();
    }

    @Override
    public SwipeableView getSwipeableView() {
        return SwipeableView.from(this);
    }

    /**
     * Begin drag mode. Keep the conversation selected (NOT toggle selection) and start drag.
     */
    private boolean beginDragMode() {
        if (mLastTouchX < 0 || mLastTouchY < 0 ||  mSelectedConversationSet == null) {
            return false;
        }
        // If this is already checked, don't bother unchecking it!
        if (!mSelected) {
            toggleSelectedState();
        }

        // Clip data has form: [conversations_uri, conversationId1,
        // maxMessageId1, label1, conversationId2, maxMessageId2, label2, ...]
        final int count = mSelectedConversationSet.size();
        String description = Utils.formatPlural(mContext, R.plurals.move_conversation, count);

        final ClipData data = ClipData.newUri(mContext.getContentResolver(), description,
                Conversation.MOVE_CONVERSATIONS_URI);
        for (Conversation conversation : mSelectedConversationSet.values()) {
            data.addItem(new Item(String.valueOf(conversation.position)));
        }
        // Protect against non-existent views: only happens for monkeys
        final int width = this.getWidth();
        final int height = this.getHeight();
        final boolean isDimensionNegative = (width < 0) || (height < 0);
        if (isDimensionNegative) {
            LogUtils.e(LOG_TAG, "ConversationItemView: dimension is negative: "
                        + "width=%d, height=%d", width, height);
            return false;
        }
        mActivity.startDragMode();
        // Start drag mode
        startDrag(data, new ShadowBuilder(this, count, mLastTouchX, mLastTouchY), null, 0);

        return true;
    }

    /**
     * Handles the drag event.
     *
     * @param event the drag event to be handled
     */
    @Override
    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENDED:
                mActivity.stopDragMode();
                return true;
        }
        return false;
    }

    private class ShadowBuilder extends DragShadowBuilder {
        private final Drawable mBackground;

        private final View mView;
        private final String mDragDesc;
        private final int mTouchX;
        private final int mTouchY;
        private int mDragDescX;
        private int mDragDescY;

        public ShadowBuilder(View view, int count, int touchX, int touchY) {
            super(view);
            mView = view;
            mBackground = mView.getResources().getDrawable(R.drawable.list_pressed_holo);
            mDragDesc = Utils.formatPlural(mView.getContext(), R.plurals.move_conversation, count);
            mTouchX = touchX;
            mTouchY = touchY;
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            final int width = mView.getWidth();
            final int height = mView.getHeight();

            sPaint.setTextSize(mCoordinates.subjectFontSize);
            mDragDescX = mCoordinates.sendersX;
            mDragDescY = (height - (int) mCoordinates.subjectFontSize) / 2 ;
            shadowSize.set(width, height);
            shadowTouchPoint.set(mTouchX, mTouchY);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            mBackground.setBounds(0, 0, mView.getWidth(), mView.getHeight());
            mBackground.draw(canvas);
            sPaint.setTextSize(mCoordinates.subjectFontSize);
            canvas.drawText(mDragDesc, mDragDescX, mDragDescY - sPaint.ascent(), sPaint);
        }
    }

    @Override
    public float getMinAllowScrollDistance() {
        return sScrollSlop;
    }

    public String getAccount() {
        return mAccount;
    }

    //TS: zheng.zou 2015-10-08 EMAIL BUGFIX-568778 ADD_S
    private String getElapseTime() {
        long time = mHeader.conversation.dateMs;
        long now = System.currentTimeMillis();
        long elapseTime = now - time;
        String displayTime;
        if (elapseTime < 0) {
            // abnormal time, this may occur when user change system time to a wrong time
            displayTime = (String) DateUtils.getRelativeTimeSpanString(mContext, time);
        } else if (elapseTime < DateUtils.MINUTE_IN_MILLIS) {
            // within one minute
            displayTime = mContext.getString(R.string.conversation_time_elapse_just_now);
        } else if (elapseTime < DateUtils.HOUR_IN_MILLIS) {
            //with in one hour
            int min = (int) (elapseTime / DateUtils.MINUTE_IN_MILLIS);
            displayTime = String.format(mContext.getString(R.string.conversation_time_elapse_minute), min);
        } else if (elapseTime < DateUtils.DAY_IN_MILLIS) {
            //within one day
            displayTime = (String) DateUtils.getRelativeTimeSpanString(mContext, time);
        } else {
            //beyond one day
            displayTime = (String) DateUtils.getRelativeTimeSpanString(mContext, time);
        }

        return displayTime;
    }
    //TS: zheng.zou 2015-10-08 EMAIL BUGFIX-568778 ADD_E
}
