package com.linktech.gft.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.linktech.gft.R;
import com.linktech.gft.bean.PEBean;

import java.util.List;

import skin.support.widget.SkinCompatSupportable;

import static com.linktech.gft.plugins.PlusFunsPluginsKt.dispatchGetSkinColor;

public class PeView extends View {
    private int width, height;
    private int padding = 16;
    private int xLeft = 44;
    private int textPadding = 8;
    private int timeHeight = 40;
    private Paint pePaint;
    private Paint datePaint;
    private Paint xPaint;
    private Paint linePaint;
    private Paint potPaint;
    private List<PEBean> peBeanList;
    private double minPe;
    private double maxPe;


    public PeView(Context context) {
        this(context, null);
    }

    public PeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    private void initAttrs() {
        //pe画笔
        pePaint = new Paint();
        pePaint.setColor(Color.parseColor("#bdcadb"));
        pePaint.setTextSize(sp2px(12));
        pePaint.setAntiAlias(true);
        //时间画笔
        datePaint = new Paint();
        datePaint.setColor(Color.parseColor("#6c7b8a"));
        datePaint.setTextSize(sp2px(12));
        datePaint.setAntiAlias(true);
        //横线画笔
        xPaint = new Paint();
        xPaint.setColor(dispatchGetSkinColor(R.color.dark_color_454860_e5e5e5));
        xPaint.setStrokeWidth(dp2px(1));
        xPaint.setAntiAlias(true);
        //实体线
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#51a5de"));
        linePaint.setStrokeWidth(dp2px(2));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        //实心点
        potPaint = new Paint();
        potPaint.setColor(Color.parseColor("#51a5de"));
        potPaint.setStyle(Paint.Style.FILL);
        potPaint.setAntiAlias(true);
        //初始化宽高
        padding = dp2px(padding);
        xLeft = dp2px(xLeft);
        timeHeight = dp2px(timeHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawX(canvas);
        if (peBeanList != null) {
            drawPe(canvas);
            drawDate(canvas);
            drawLine(canvas);
            drawCircle(canvas);
        }
    }

    //画横线
    private void drawX(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            int y = padding + (height - timeHeight - padding) * i / 4;
            canvas.drawLine(xLeft, y, width - padding, y, xPaint);
        }
    }

    //画pe
    private void drawPe(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            int y = padding + (height - timeHeight - padding) * i / 4;
            String text = doubleToString(maxPe - (maxPe - minPe) * i / 4);
            float textWidth = pePaint.measureText(text);
            canvas.drawText(text, xLeft - textPadding - textWidth, fixTextY(y), pePaint);
        }
    }

    //画日期
    private void drawDate(Canvas canvas) {
        for (int i = 0; i < peBeanList.size(); i++) {
            String date = peBeanList.get(i).getYearend();
            float textCenter = (2 * i + 1) * (width - xLeft - padding) / (float) peBeanList.size() / 2f + xLeft;
            float textWidth = datePaint.measureText(date);
            canvas.drawText(date, textCenter - textWidth / 2, fixTextY(height - timeHeight / 2f), datePaint);
        }
    }

    //画折线
    private void drawLine(Canvas canvas) {
        Path pathLine = new Path();
        for (int i = 0; i < peBeanList.size(); i++) {
            float textCenter = (2 * i + 1) * (width - xLeft - padding) / (float) peBeanList.size() / 2f + xLeft;
            float y = calculateY(peBeanList.get(i).getPe());
            if (i == 0) {
                pathLine.moveTo(textCenter, y);
            } else {
                pathLine.lineTo(textCenter, y);
            }
//            canvas.drawCircle(textCenter, y, 4, potPaint);
        }
        canvas.drawPath(pathLine, linePaint);
    }

    //画圆心
    private void drawCircle(Canvas canvas) {
        for (int i = 0; i < peBeanList.size(); i++) {
            float textCenter = (2 * i + 1) * (width - xLeft - padding) / (float) peBeanList.size() / 2f + xLeft;
            float y = calculateY(peBeanList.get(i).getPe());
            canvas.drawCircle(textCenter, y, 6, potPaint);
        }
    }

    private float calculateY(double pe) {
        double y = height - timeHeight - (pe - minPe) * (height - timeHeight - padding) / (maxPe - minPe);
        return (float) y;
    }

    /**
     * 数据源
     */
    public void setData(List<PEBean> peBeanList) {
        this.peBeanList = peBeanList;
        //找出最大值和最小值
        for (int i = 0; i < peBeanList.size(); i++) {
            double pe = peBeanList.get(i).getPe();
            if (i == 0) {
                minPe = maxPe = pe;
                continue;
            }
            if (pe < minPe) minPe = pe;
            if (pe > maxPe) maxPe = pe;
        }
        minPe = minPe > 0 ? minPe / 1.2 : minPe * 1.2;
        maxPe = maxPe * 1.2;
        invalidate();
    }

    /**
     * 解决text居中的问题
     */
    public float fixTextY(float y) {
        Paint.FontMetrics fontMetrics = pePaint.getFontMetrics();
        return (y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }

    /**
     * 保留2位小数
     */
    public String doubleToString(double value) {
        return String.format("%.2f", value);
    }

    public int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int sp2px(float sp) {
        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

}
