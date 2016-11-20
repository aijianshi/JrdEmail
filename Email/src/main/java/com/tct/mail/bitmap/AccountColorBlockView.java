/*
 ==========================================================================
 *HISTORY
 *
 *Tag		 	 Date	      Author		  Description
 *============== ============ =============== ==============================
 *FEATURE-834751 2015/10/28   jian.xu         Use different color to distinguish each account when in combined view mode
 ============================================================================
 */
package com.tct.mail.bitmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.tct.email.R;

public class AccountColorBlockView extends View {

    private Paint mPaint = new Paint();
    private int mColor = 0x00000000;
    private float dimension = getResources().getDimension(R.dimen.account_color_block_dimension);
    private float radius = getResources().getDimension(R.dimen.account_color_block_radius_dimension);

    public AccountColorBlockView(Context context) {
        this(context, null);
    }
    public AccountColorBlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public AccountColorBlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
        canvas.drawCircle(dimension / 2, dimension / 2, radius, mPaint);
    }
}
