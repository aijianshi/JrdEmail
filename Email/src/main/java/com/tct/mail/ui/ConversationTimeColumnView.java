/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-539892  2015-09-01   zheng.zou       CR:in email list view, group email with time range and show time range in column
 ============================================================================
 */
package com.tct.mail.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.tct.email.R;

/**
 * Created by user on 15-8-26.
 * time column view as a child view in listview
 */
public class ConversationTimeColumnView extends FrameLayout{
    TextView mTextView;
    private int mPosition;

    public ConversationTimeColumnView(Context context,int position) {
        super(context);
        mTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.conversation_time_column,null);
        addView(mTextView);
        mPosition = position;
    }

    public int getPosition(){
        return mPosition;
    }

    public void setTitle(String title){
       mTextView.setText(title);
    }

}
