package com.linktech.gft.view;

import android.content.Context;
import android.util.AttributeSet;

import com.bigkoo.convenientbanner.ConvenientBanner;

/**
 * 固定宽高比的banner
 * Created by Administrator on 2018/3/13.
 */

public class StaticConvenientBanner extends ConvenientBanner {
    private float rate;

    public StaticConvenientBanner(Context context) {
        super(context);
    }

    public StaticConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StaticConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (rate != 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * rate), MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 请求刷新宽高
     * <p>
     * height  / width
     */
    public void onRequireLayout(float rate) {
        if (rate > this.rate) {
            this.rate = rate;
            requestLayout();
        }
    }
}
