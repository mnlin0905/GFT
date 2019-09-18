package com.linktech.gft.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created on 2018/7/2  19:42
 * function : 矩形framelayout
 *
 * @author mnlin
 */
public class RectFrameLayout extends FrameLayout {
    public RectFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //让高度等于宽度
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }
    }
}
