package com.tct.mail.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.tct.email.R;

//TS: tao.gan 2015-10-12 EMAIL FEATURE-559891 ADD_S
/*
 * Add the header view to the top of conversationList, the height is same as action bar.
 * Fill the space area in case of overlap phenomenon
 */
public class ConversationListHeader extends LinearLayout{

    private View view;
    ConversationListHeader(Context context) {
        super(context);
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(param);

        //The height initialized is toolbar height
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(
                        R.dimen.abc_action_bar_default_height_material));
        view = LayoutInflater.from(context).inflate(R.layout.toolbar_fill_blank_view, null);
        addView(view, params1);
    }

    /*
     * Change the header view's visible height,because when user is searching messages,
     * the action bar's height is changed,the header view 's height needs change also.
     */
    public void setVisibleHeight(int height) {
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) view.getLayoutParams();
        params2.height = height;
        view.setLayoutParams(params2);
    }
  //TS: tao.gan 2015-10-12 EMAIL FEATURE-559891 ADD_E
}
