package com.linktech.gft.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import skin.support.widget.SkinCompatFrameLayout;

/**
 * Created on 2018/7/27  16:23
 * function : fit 第一个view 的宽高
 *
 * @author mnlin
 */
public class FitFirstChildFrameLayout extends SkinCompatFrameLayout {
    public FitFirstChildFrameLayout(Context context) {
        this(context, null);
    }

    public FitFirstChildFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitFirstChildFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() != 0) {
            View child = getChildAt(0);
            if (child.getMeasuredWidth() != 0 && child.getMeasuredHeight() != 0) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY));
            }
        }
    }
}
