package com.linktech.gft.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.linktech.gft.R;

import skin.support.content.res.SkinCompatResources;

/**
 * 自定义实现轨迹
 */
public class MovePathView extends View {

    public Path mPath;
    private Paint mPaint;

    /**
     * 已经存在的图片
     */
    @Nullable
    private Bitmap preDrawBitmap;

    /**
     * 手指按下的位置
     */
    private float startX, startY;

    public MovePathView(Context context) {
        super(context);
        init();
    }

    public MovePathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovePathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setColor(SkinCompatResources.getColor(getContext(), R.color.skin_accent_color));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                //设置原点
                mPath.moveTo(startX, startY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float currX = event.getX();
                float currY = event.getY();
                //连线
                mPath.lineTo(currX, currY);
                //刷新view
                invalidate();
                break;

        }

        //返回true，消费事件
        return true;
    }

    /**
     * 更新绘画板
     */
    public MovePathView setPreDrawBitmap(@Nullable Bitmap preDrawBitmap) {
        this.preDrawBitmap = preDrawBitmap;
        postInvalidate();
        return this;
    }

    @Nullable
    public Bitmap getPreDrawBitmap() {
        return preDrawBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(preDrawBitmap!=null){
            canvas.drawBitmap(preDrawBitmap,0,0,mPaint);
        }
        canvas.drawPath(mPath, mPaint);
    }

    //对外提供的方法，重新绘制
    public void reset() {
        preDrawBitmap = null;
        mPath.reset();
        invalidate();
    }
}