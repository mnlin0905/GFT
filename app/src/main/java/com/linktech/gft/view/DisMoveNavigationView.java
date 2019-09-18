package com.linktech.gft.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created on 2018/8/28  11:48
 * function : 在点击时,禁止内容布局自动移动
 *
 * @author mnlin
 */
public class DisMoveNavigationView extends NavigationView {
    public DisMoveNavigationView(Context context) {
        super(context);
    }

    public DisMoveNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisMoveNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
