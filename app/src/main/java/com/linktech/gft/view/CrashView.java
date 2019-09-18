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
import com.linktech.gft.bean.CashFlowBean;

import java.util.List;

import static com.linktech.gft.plugins.PlusFunsPluginsKt.dispatchGetSkinColor;

public class CrashView extends View {
    private int width, height;
    private int padding = 16;
    private int xLeft = 56;
    private int xRight = 48;
    private int textPadding = 4;
    private int timeHeight = 40;
    private int rectWidth = 20;
    private Paint pePaint;
    private Paint datePaint;
    private Paint xPaint;
    private Paint rectPaint;
    private Paint linePaint;
    private Paint potPaint;
    private List<CashFlowBean> datas;
    private double minCrash;
    private double maxCrash;
    private double minRate;
    private double maxRate;

    public CrashView(Context context) {
        this(context, null);
    }

    public CrashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        //柱状图画笔
        rectPaint = new Paint();
        rectPaint.setColor(Color.parseColor("#53a8e2"));
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setAntiAlias(true);
        //实体线
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#fe8555"));
        linePaint.setStrokeWidth(dp2px(2));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        //实心点
        potPaint = new Paint();
        potPaint.setColor(Color.parseColor("#fe8555"));
        potPaint.setStyle(Paint.Style.FILL);
        potPaint.setAntiAlias(true);
        //初始化宽高
        padding = dp2px(padding);
        xLeft = dp2px(xLeft);
        xRight = dp2px(xRight);
        timeHeight = dp2px(timeHeight);
        rectWidth = dp2px(rectWidth);
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
        if (datas != null) {
            drawCrash(canvas);
            drawRate(canvas);
            drawDate(canvas);
            drawRect(canvas);
            if (minCrash >= 0) {
                drawLine(canvas);
                drawCircle(canvas);
            }
        }
    }

    //画横线
    private void drawX(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            int y = padding + (height - timeHeight - padding) * i / 4;
            canvas.drawLine(xLeft, y, width - xRight, y, xPaint);
        }
    }

    //画净额
    private void drawCrash(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            int y = padding + (height - timeHeight - padding) * i / 4;
            String text = doubleToString(maxCrash - (maxCrash - minCrash) * i / 4);
            float textWidth = pePaint.measureText(text);
            canvas.drawText(text, xLeft - textPadding - textWidth, fixTextY(y), pePaint);
        }
    }

    //画增长率
    private void drawRate(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            int y = padding + (height - timeHeight - padding) * i / 4;
            String text = doubleToString(maxRate - (maxRate - minRate) * i / 4) + "%";
            canvas.drawText(text, width - xRight + textPadding, fixTextY(y), pePaint);
        }
    }

    //画日期
    private void drawDate(Canvas canvas) {
        for (int i = 0; i < datas.size(); i++) {
            String date = datas.get(i).getYearend();
            float textCenter = (2 * i + 1) * (width - xLeft - xRight) / (float) datas.size() / 2f + xLeft;
            float textWidth = datePaint.measureText(date);
            canvas.drawText(date, textCenter - textWidth / 2, fixTextY(height - timeHeight / 2f), datePaint);
        }
    }

    //画矩形
    private void drawRect(Canvas canvas) {
        for (int i = 0; i < datas.size(); i++) {
            float textCenter = (2 * i + 1) * (width - xLeft - xRight) / (float) datas.size() / 2f + xLeft;
            double crash = datas.get(i).getCfOperAct();
            canvas.drawRect(textCenter - rectWidth / 2, calculateCrashTop(crash), textCenter + rectWidth / 2, calculateCrashBottom(crash), rectPaint);
        }
    }

    //画折线
    private void drawLine(Canvas canvas) {
        Path pathLine = new Path();
        for (int i = 0; i < datas.size(); i++) {
            float textCenter = (2 * i + 1) * (width - xLeft - xRight) / (float) datas.size() / 2f + xLeft;
            float y = calculateRateY(datas.get(i).getRate());
            if (i == 0) {
                pathLine.moveTo(textCenter, y);
            } else {
                pathLine.lineTo(textCenter, y);
            }
        }
        canvas.drawPath(pathLine, linePaint);
    }

    //画圆心
    private void drawCircle(Canvas canvas) {
        for (int i = 0; i < datas.size(); i++) {
            float textCenter = (2 * i + 1) * (width - xLeft - xRight) / (float) datas.size() / 2f + xLeft;
            float y = calculateRateY(datas.get(i).getRate());
            canvas.drawCircle(textCenter, y, 6, potPaint);
        }
    }

    private float calculateCrashTop(double crash) {
        double top;
        if (maxCrash == 0) {
            top = padding;
        } else if (minCrash == 0) {
            top = height - timeHeight - (crash - minCrash) * (height - timeHeight - padding) / (maxCrash - minCrash);
        } else if (crash > 0) {
            top = height - timeHeight - (crash - minCrash) * (height - timeHeight - padding) / (maxCrash - minCrash);
        } else {
            top = (height - timeHeight - padding) / 2 + padding;
        }
        return (float) top;
    }

    private float calculateCrashBottom(double crash) {
        double bottom;
        if (maxCrash == 0) {
            bottom = height - timeHeight - (crash - minCrash) * (height - timeHeight - padding) / (maxCrash - minCrash);
        } else if (minCrash == 0) {
            bottom = height - timeHeight;
        } else if (crash > 0) {
            bottom = (height - timeHeight - padding) / 2 + padding;
        } else {
            bottom = height - timeHeight - (crash - minCrash) * (height - timeHeight - padding) / (maxCrash - minCrash);
        }
        return (float) bottom;
    }

    private float calculateRateY(double rate) {
        double y = height - timeHeight - (rate - minRate) * (height - timeHeight - padding) / (maxRate - minRate);
        return (float) y;
    }

    /**
     * 数据源
     */
    public void setData(List<CashFlowBean> datas) {
        this.datas = datas;
        //找出最大值和最小值
        for (int i = 0; i < datas.size(); i++) {
            double crash = datas.get(i).getCfOperAct();
            double rate = datas.get(i).getRate();
            if (i == 0) {
                minCrash = maxCrash = crash;
                minRate = maxRate = rate;
                continue;
            }
            if (crash < minCrash) minCrash = crash;
            if (crash > maxCrash) maxCrash = crash;
            if (rate < minRate) minRate = rate;
            if (rate > maxRate) maxRate = rate;
        }

        //处理净额
        if (minCrash > 0) { //都是大于0的值
            minCrash = 0;
            maxCrash = maxCrash * 1.2;
        }
        if (maxCrash < 0) { //都是小于0的值
            maxCrash = 0;
            minCrash = minCrash * 1.2;
        }
        if (minCrash < 0 && maxCrash > 0) { //最小值小于零，最大值大于零
            if (Math.abs(minCrash) < Math.abs(maxCrash)) {
                maxCrash = maxCrash * 1.2;
                minCrash = -maxCrash;
            } else {
                minCrash = minCrash * 1.2;
                maxCrash = -minCrash;
            }
        }
        //处理增长率
        minRate = minRate > 0 ? minRate / 1.2 : minRate * 1.2;
        maxRate = maxRate * 1.2;
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
        return String.format("%.1f", value);
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
