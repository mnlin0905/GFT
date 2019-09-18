package com.linktech.gft.skin;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.wangnan.library.GestureLockView;

/**
 * Created on 2019/6/3  17:49
 * function : 控制 是否在绘制时显示轨迹
 *
 * @author mnlin
 */
public class MyGestureLockView extends GestureLockView {
    /**
     * 是否显示轨迹线
     */
    public boolean showGestureTravel = true;

    public MyGestureLockView(Context context) {
        super(context);
    }

    public MyGestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(showGestureTravel) {
            super.onDraw(canvas);
        }
    }
}
