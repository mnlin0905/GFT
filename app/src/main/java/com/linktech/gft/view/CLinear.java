package com.linktech.gft.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.logging.Logger;

/**
 * Created on 2019/5/10  18:42
 * function :
 *
 * @author mnlin
 */
public class CLinear extends LinearLayout {
    public CLinear(Context context) {
        super(context);
    }

    public CLinear(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CLinear(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CLinear(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Logger.getAnonymousLogger();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Logger.getAnonymousLogger();
    }
}
