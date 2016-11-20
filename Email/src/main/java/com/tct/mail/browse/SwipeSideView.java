/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 ============================================================================
 */
package com.tct.mail.browse;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.tct.email.R;

/**
 * Created by user on 15-8-8.
 * side view to exhibit the swipe action under conversationItemView
 */
public class SwipeSideView extends FrameLayout{
    private ImageView mStarIV;
    public SwipeSideView(Context context) {
        super(context);
    }

    /**
     * set imageview
     * @param imageView  will be used latter to update star status
     */
    public void  setStarIV(ImageView imageView){
       mStarIV = imageView;
    }


    /**
     * update star status before star status change
     * @param star  star status
     */
    public void updateStarBeforeStatusChange(boolean star){
        if (mStarIV!=null){
            if (star){
                mStarIV.setImageResource(R.drawable.ic_star_outline);
            } else {
                mStarIV.setImageResource(R.drawable.ic_star);
            }
        }
    }


    /**
     * update star status after star status change
     * @param star  star status
     */
    public void updateStarAfterStatusChange(boolean star){
        if (mStarIV!=null){
            if (star){
                mStarIV.setImageResource(R.drawable.ic_star);
            } else {
                mStarIV.setImageResource(R.drawable.ic_star_outline);
            }
        }
    }


    /**
     * construct left view
     * @param context
     * @return  SwipeSideView
     */
    public static SwipeSideView newLeftView(Context context){
        SwipeSideView swipeSideView =  new SwipeSideView(context);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_delete_white);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.conv_list_leavebehind_icon_padding);
        params.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(params);
        swipeSideView.setBackgroundResource(R.color.swipe_delete_item_background);
        swipeSideView.addView(imageView);

        return swipeSideView;
    }

    /**
     * construct right view
     * @param context
     * @return  SwipeSideView
     */
    public static SwipeSideView newRightView(Context context){
        SwipeSideView swipeSideView =  new SwipeSideView(context);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_star);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.conv_list_leavebehind_icon_padding);;
        params.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        imageView.setLayoutParams(params);
        swipeSideView.setStarIV(imageView);
        swipeSideView.setBackgroundResource(R.color.swipe_star_item_background);
        swipeSideView.addView(imageView);

        return swipeSideView;
    }

}
