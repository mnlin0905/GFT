package com.linktech.gft.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import skin.support.widget.SkinCompatImageView;

/**
 * Created on 2018/7/27  16:23
 * function : fit src宽高
 *
 * @author mnlin
 */
public class FitSrcImageView extends SkinCompatImageView {
    public FitSrcImageView(Context context) {
        this(context, null);
    }

    public FitSrcImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitSrcImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //保证填充布局
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Drawable drawable = getDrawable();
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicHeight != 0 && intrinsicWidth != 0) {
                int width = getMeasuredWidth();
                int height = ((int) (1.00 * width / intrinsicWidth * intrinsicHeight));
                super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }
        }
    }
}
