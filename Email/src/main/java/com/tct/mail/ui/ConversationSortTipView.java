package com.tct.mail.ui;

import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.browse.ConversationCursor;
import com.tct.mail.providers.Folder;
import com.tct.mail.utils.SortHelper;
import com.tct.mail.utils.Utils;

/**
 * Created by user on 16-1-5.
 */
public class ConversationSortTipView extends FrameLayout implements ConversationSpecialItemView {
    private static int sScrollSlop = 0;
    private static int sShrinkAnimationDuration;
    private boolean mShown = true;
    private int mAnimatedHeight = -1;
    private ControllableActivity mActivity;

    private int mSortOrder = SortHelper.getDefaultOrder();
    private TextView mSortTipView;
    public ConversationSortTipView(Context context) {
        super(context);
    }

    public ConversationSortTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConversationSortTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources resources = context.getResources();
        synchronized (ConversationPhotoTeaserView.class) {
            if (sScrollSlop == 0) {
                sScrollSlop = resources.getInteger(R.integer.swipeScrollSlop);
                sShrinkAnimationDuration = resources.getInteger(
                        R.integer.shrink_animation_duration);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSortTipView = (TextView) findViewById(R.id.text);
        findViewById(R.id.dismiss_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void updateSortOrder(ControllableActivity activity, int sort){
        mActivity = activity;
        mSortOrder = sort;
        if (mSortTipView!=null){
            String[] sortTypes = getResources().getStringArray(R.array.mail_sort_types);
            String sortType = "";
            if (sortTypes!=null && sort>=0 && sort<sortTypes.length){
                sortType = sortTypes[sort];
            }
            mSortTipView.setText(getResources().getString(R.string.sort_by_tip,sortType));
        }
    }

    public void dismiss() {
        setDismissed();
//        startDestroyAnimation();
        onDismissed();
    }

    private void setDismissed() {
        if (mShown) {
            mShown = false;
        }
    }

    private void startDestroyAnimation() {
        final int start = getHeight();
        final int end = 0;
        mAnimatedHeight = start;
        final ObjectAnimator heightAnimator =
                ObjectAnimator.ofInt(this, "animatedHeight", start, end);
        heightAnimator.setInterpolator(new DecelerateInterpolator(2.0f));
        heightAnimator.setDuration(sShrinkAnimationDuration);
        heightAnimator.start();

        /*
         * Ideally, we would like to call mAdapter.notifyDataSetChanged() in a listener's
         * onAnimationEnd(), but we are in the middle of a touch event, and this will cause all the
         * views to get recycled, which will cause problems.
         *
         * Instead, we'll just leave the item in the list with a height of 0, and the next
         * notifyDatasetChanged() will remove it from the adapter.
         */
    }

    private void onDismissed(){
        if (mActivity!=null && mActivity.getAccountController()!=null &&  mActivity.getAccountController() instanceof AbstractActivityController){
            mSortOrder = SortHelper.getDefaultOrder();
            SortHelper.setCurrentSort(mSortOrder);
            ((AbstractActivityController) mActivity.getAccountController()).sort(mSortOrder);
        }
    }

    /**
     * This method is used by the animator.  It is explicitly kept in proguard.flags to prevent it
     * from being removed, inlined, or obfuscated.
     * Edit ./packages/apps/UnifiedEmail/proguard.flags
     * In the future, we want to use @Keep
     */
    public void setAnimatedHeight(final int height) {
        mAnimatedHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

        if (mAnimatedHeight == -1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mAnimatedHeight);
        }
    }

    @Override
    public void onUpdate(Folder folder, ConversationCursor cursor) {

    }

    @Override
    public void onGetView() {

    }

    @Override
    public boolean getShouldDisplayInList() {
        return !SortHelper.isTimeOrder(mSortOrder);
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public void setAdapter(AnimatedAdapter adapter) {

    }

    @Override
    public void bindFragment(LoaderManager loaderManager, Bundle savedInstanceState) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void onConversationSelected() {

    }

    @Override
    public void onCabModeEntered() {

    }

    @Override
    public void onCabModeExited() {

    }

    @Override
    public boolean acceptsUserTaps() {
        return false;
    }

    @Override
    public void onConversationListVisibilityChanged(boolean visible) {

    }

    @Override
    public void saveInstanceState(Bundle outState) {

    }

    @Override
    public boolean commitLeaveBehindItem() {
        return false;
    }
}
