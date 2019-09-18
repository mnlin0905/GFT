package com.linktech.gft.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created on 2019/7/17  11:32
 * function : 不可以滑动的布局
 *
 * @author mnlin
 */
public class DisableHScrollView extends HorizontalScrollView {
    public DisableHScrollView(Context context) {
        super(context);
    }

    public DisableHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisableHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DisableHScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }
}
