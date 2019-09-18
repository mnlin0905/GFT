package com.linktech.gft.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.linktech.gft.R;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;

/**
 * 可以显示无数据的球脉冲加载
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NoDataBallPulseFooter extends BallPulseFooter {
    private boolean noMoreData;

    public NoDataBallPulseFooter(@NonNull Context context) {
        super(context);
    }

    public NoDataBallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDataBallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 支持"已加载全部数据的操作"
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (noMoreData != this.noMoreData) {
            this.noMoreData = noMoreData;
            invalidate();
        }
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (noMoreData) {
            //绘制结束时的文字信息
            mPaint.setColor(mNormalColor);
            mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_normal_14sp));
            mPaint.setTextAlign(Paint.Align.CENTER);

            //底部的宽高
            final int width = this.getWidth();
            final int height = this.getHeight();

            //文字宽高
            int textWidth = (int) mPaint.measureText(PlusFunsPluginsKt.getString(null, R.string.common_list_no_data));
            int textHeight = (int) (mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top);

            //绘制文字(y轴高度为:(控件高度-文字高度)/2 + 文字baseLine高度)
            canvas.drawText(PlusFunsPluginsKt.getString(null, R.string.common_list_no_data), width / 2, (height - textHeight) / 2 + Math.abs(mPaint.getFontMetrics().top), mPaint);
        } else {
            mPaint.setColor(mAnimatingColor);
            super.dispatchDraw(canvas);
        }
    }

}
