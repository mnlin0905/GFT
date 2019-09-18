package com.linktech.gft.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.linktech.gft.R;

/**
 * Created on 2018/1/5
 * function : 圆角矩形,左右为圆形,内容背景透明,边框绿色,
 * <p>
 * 默认该控件不能设置background
 *
 * @author LinkTech
 */

public class RoundRectBorderTextView extends FormatDoubleTextView {
    private ColorStateList borderColor;

    //设置字体默认的效果
    {
        //字体大小默认11sp
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_smallest_11sp));

        //上下边框边距默认为4dp,左右默认间距为8dp
        int dp_horizontal = getResources().getDimensionPixelSize(R.dimen.view_padding_margin_8dp);
        int dp_vertical = getResources().getDimensionPixelSize(R.dimen.view_padding_margin_4dp);
        setPadding(dp_horizontal, dp_vertical, dp_horizontal, dp_vertical);
    }

    public RoundRectBorderTextView(Context context) {
        this(context, null);
    }

    public RoundRectBorderTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public RoundRectBorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundRectBorderTextView);
        borderColor = ta.getColorStateList(R.styleable.RoundRectBorderTextView_RoundRectBorderTextView_border_color);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawOutlineUseShape(canvas);
    }

    /**
     * 使用Drawable形式画线
     */
    private void drawOutlineUseDrawable() {
        int height = getHeight() / 2;
        float[] outRect = new float[]{height, height, height, height, height, height, height, height};
        float[] innerRect = new float[]{height, height, height, height, height, height, height, height};
        RectF insert = new RectF(getLeft() - 1, getTop() - 1, getRight() - 1, getBottom() - 1);
        @SuppressLint("DrawAllocation")
        RoundRectShape shape = new RoundRectShape(outRect, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(shape);
        shapeDrawable.getPaint().setColor(borderColor.getDefaultColor());
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);//描边
        shapeDrawable.getPaint().setStrokeWidth(1);
        shapeDrawable.setIntrinsicWidth(getWidth());
        shapeDrawable.setIntrinsicHeight(getHeight());

        setBackground(shapeDrawable);
    }

    /**
     * 使用shape形式画线
     */
    private void drawOutlineUseShape(Canvas canvas) {
        // 在所有内容绘制之后,绘制圆角边框
        int height = getHeight() / 2;

        //生成圆角矩形
        float[] outRect = new float[]{height, height, height, height, height, height, height, height};
        @SuppressLint("DrawAllocation")
        RoundRectShape shape = new RoundRectShape(outRect, null, null);
        shape.resize(getWidth(), getHeight());

        //更换画笔颜色,粗细
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(borderColor.getDefaultColor());
        paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.background_border_width_1dp));

        //将shape画出
        shape.draw(canvas, paint);
    }
}
