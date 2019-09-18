package com.github.tifezh.kchartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.beans.MinuteLineEntity;
import com.github.tifezh.kchartlib.chart.entity.IMinuteLine;
import com.github.tifezh.kchartlib.chart.formatter.BigValueFormatter;
import com.github.tifezh.kchartlib.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 分时图
 * 简单的分时图示例 更丰富的需求可能需要在此基础上再作修改
 */
public abstract class MinuteChartView extends View implements GestureDetector.OnGestureListener, CommonChartUpDownModeColor {

    private final static int ONE_MINUTE = 60000;
    private final List<IMinuteLine> mPoints = new ArrayList<>();
    private int mHeight = 0;
    private int mWidth = 0;
    private int mVolumeHeight = 100;
    private int mTopPadding = 15;
    private int mBottomPadding = 15;
    private int mGridRows = 6;
    private int GridColumns = 5;
    private Paint mAvgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPricePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mVolumePaintUp = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mVolumePaintDown = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor;
    private float mValueMin;
    private float mValueMax;
    private float mVolumeMax;
    private float mValueStart;
    private float mScaleY = 1;
    private float mVolumeScaleY = 1;
    private float mTextSize = 10;
    private boolean isLongPress = false;
    private int selectedIndex;
    private GestureDetectorCompat mDetector;
    private Date mFirstStartTime;
    private Date mFirstEndTime;
    private Date mSecondStartTime;
    private Date mSecondEndTime;
    private long mTotalTime;
    private float mPointWidth;
    private LinearGradient shadowShapeLinear = null;

    private IValueFormatter mVolumeFormatter;
    private boolean hideVolume;

    public MinuteChartView(Context context) {
        super(context);
        init();
    }

    public MinuteChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MinuteChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDetector = new GestureDetectorCompat(getContext(), this);
        mTopPadding = dp2px(mTopPadding);
        mBottomPadding = dp2px(mBottomPadding);
        mTextSize = sp2px(mTextSize);
        mVolumeHeight = dp2px(mVolumeHeight);
        mGridPaint.setStrokeWidth(dp2px(1));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(dp2px(0.5f));
        mAvgPaint.setStrokeWidth(dp2px(0.5f));
        mAvgPaint.setTextSize(mTextSize);
        mPricePaint.setStrokeWidth(dp2px(0.5f));
        mPricePaint.setTextSize(mTextSize);
        mVolumeFormatter = new BigValueFormatter();
        refreshPaintColor();
    }

    /**
     * 刷新数据显示的颜色等
     */
    protected void refreshPaintColor() {
        mGridPaint.setColor(getGridLineColor());
        mTextPaint.setColor(getNormalTextColor());
        mAvgPaint.setColor(getAvePriceColor());
        mPricePaint.setColor(getTurnoverPriceColor());
        mVolumePaintDown.setColor(getDownModeColor());
        mVolumePaintUp.setColor(getUpModeColor());
        mBackgroundColor = getBackgroundColor();
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    /**
     * 返回背景颜色值
     */
    @ColorInt
    protected abstract int getBackgroundColor();

    /**
     * 返回网格线颜色
     */
    @ColorInt
    protected abstract int getGridLineColor();

    /**
     * 返回文字颜色(主要文本部分)
     */
    @ColorInt
    protected abstract int getNormalTextColor();

    /**
     * 返回成交价格颜色显示
     */
    @ColorInt
    protected abstract int getTurnoverPriceColor();

    /**
     * 返回平均价格颜色
     */
    @ColorInt
    protected abstract int getAvePriceColor();


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //一个点的时候滑动
                if (event.getPointerCount() == 1) {
                    //长按之后移动
                    if (isLongPress) {
                        calculateSelectedX(event.getX());
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isLongPress = false;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                isLongPress = false;
                invalidate();
                break;
        }
        return true;
    }

    private void calculateSelectedX(float x) {
        selectedIndex = (int) (x * 1f / getX(mPoints.size() - 1) * (mPoints.size() - 1) + 0.5f);
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }
        if (selectedIndex > mPoints.size() - 1) {
            selectedIndex = mPoints.size() - 1;
        }
    }

    /**
     * 根据索引获取x的值
     */
    private float getX(int position) {
        Date date = mPoints.get(position).getDate();
        if (mSecondStartTime != null && date.getTime() >= mSecondStartTime.getTime()) {
            return 1f * (date.getTime() - mSecondStartTime.getTime() + 60000 +
                    mFirstEndTime.getTime() - mFirstStartTime.getTime()) / mTotalTime * (mWidth - mPointWidth) + mPointWidth / 2f;
        } else {
            return 1f * (date.getTime() - mFirstStartTime.getTime()) / mTotalTime * (mWidth - mPointWidth) + mPointWidth / 2f;
        }
    }

    /**
     * 获取最大能有多少个点
     */
    private long getMaxPointCount() {
        return mTotalTime / ONE_MINUTE;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int height = h - mTopPadding - mBottomPadding;
        this.mHeight = height - mVolumeHeight;
        this.mWidth = w;
        notifyChanged();
    }

    /**
     * 对外暴露接口(仅仅传回昨收价格)
     *
     * @param todayOpenPrice 今开价格
     * @param yesClosePrice  昨天收取
     */
    public <T extends IMinuteLine> void initData(List<T> data, double yesClosePrice, boolean hideVolume) {
        if (data != null && data.size() != 0) {
            initData(data, data.get(0).getPrice(), yesClosePrice, hideVolume);
        }
    }

    /**
     * @param data          数据源
     * @param startTime     显示的开始时间
     * @param endTime       显示的结束时间
     * @param yesClosePrice 昨日开盘价
     */
    private void initData(Collection<? extends IMinuteLine> data,
                          Date startTime,
                          Date endTime,
                          float yesClosePrice) {
        initData(data, startTime, endTime, null, null, yesClosePrice);
    }

    private void initData(Collection<? extends IMinuteLine> data,
                          @NonNull Date startTime,
                          @NonNull Date endTime,
                          @Nullable Date firstEndTime,
                          @Nullable Date secondStartTime,
                          double yesClosePrice) {
        initData(data, startTime, endTime, firstEndTime, secondStartTime, ((float) yesClosePrice));
    }

    /**
     * @param todayOpenPrice 今开价格
     * @param yesClosePrice  昨天收取
     */
    public <T extends IMinuteLine> void initData(List<T> data, double todayOpenPrice, double yesClosePrice, boolean hideVolume) {
        this.hideVolume = hideVolume;

        if (data != null) {
            Calendar c = Calendar.getInstance();

            // 处理起始或者默认时间,方便之后插入或者修改数据
            if (data.size() == 0) {
                c.setTimeInMillis(System.currentTimeMillis());
            } else {
                c.setTimeInMillis(data.get(0).getDate().getTime());
            }
            c.set(Calendar.SECOND, 0);

            // 起始时间
            c.set(Calendar.HOUR_OF_DAY, 9);
            c.set(Calendar.MINUTE, 30);
            Date startTime = c.getTime();

            // 终止时间
            c.set(Calendar.HOUR_OF_DAY, 16);
            c.set(Calendar.MINUTE, 0);
            Date endTime = c.getTime();

            // 数据量为空时,制动构造出两个起始的模拟数据
            if (data.size() == 0) {
                // 添加起始点
                data.add((T) new MinuteLineEntity(startTime, yesClosePrice, yesClosePrice, 0.0));
                data.add((T) new MinuteLineEntity(endTime, yesClosePrice, yesClosePrice, 0.0));
            }

            // 2019.08.06 添加中间缺失数据

            // 补开始
            Calendar first = Calendar.getInstance();
            first.setTimeInMillis(data.get(0).getDate().getTime());
            first.set(Calendar.SECOND, 0);
            if (first.getTime().getTime() < startTime.getTime()) {
                // 如果 第一个时间有误(早于 9: 30) 不处理
            } else if (first.getTime().getTime() > startTime.getTime()) {
                // 如果 9: 30 没有数据,则需要手动添加一条
                data.add(0, (T) new MinuteLineEntity(startTime, yesClosePrice, yesClosePrice, 0.0));
            } else {
                // 如果第一个时间正好是9:30,表示不需要在最前做任何处理
            }

            // 补中间
            Calendar old = Calendar.getInstance();
            Calendar current = Calendar.getInstance();
            for (int i = data.size() - 1; i >= 1; i--) {
                // 前一数据 分钟加 1
                old.setTimeInMillis(data.get(i - 1).getDate().getTime());
                old.add(Calendar.MINUTE, 1);
                old.set(Calendar.SECOND, 0);

                // 后一数据
                current.setTimeInMillis(data.get(i).getDate().getTime());
                current.set(Calendar.SECOND, 0);

                //  如果相隔正好一分钟,则不处理
                if (!current.equals(old) &&
                        (data.get(i).getPrice() != data.get(i - 1).getPrice()
                                || data.get(i).getAvgPrice() != data.get(i - 1).getAvgPrice())) {
                    current.add(Calendar.MINUTE, -1);
                    data.add(i, (T) new MinuteLineEntity(current.getTime(), data.get(i - 1).getPrice(), data.get(i - 1).getAvgPrice(), 0.0));
                }
            }

            // 中间停止和开始时间
            c.set(Calendar.HOUR_OF_DAY, 12);
            c.set(Calendar.MINUTE, 0);
            Date betweenStartTime = c.getTime();
            c.set(Calendar.HOUR_OF_DAY, 13);
            c.set(Calendar.MINUTE, 0);
            Date betweenEndTime = c.getTime();

            // 剔除中间数据(12:00 - 13:00 之间部分)
            Date forEach;
            for (int i = data.size() - 1; i >= 1; i--) {
                forEach = data.get(i).getDate();
                if (forEach.after(betweenStartTime) && forEach.before(betweenEndTime)) {
                    data.remove(i);
                }
            }

            initData(data, startTime, endTime, betweenStartTime, betweenEndTime, ((float) todayOpenPrice));
        }
    }

    /**
     * @param data            数据源
     * @param startTime       显示的开始时间
     * @param endTime         显示的结束时间
     * @param firstEndTime    休息开始时间 可空
     * @param secondStartTime 休息结束时间 可空
     * @param yesClosePrice   昨收价
     */
    private void initData(Collection<? extends IMinuteLine> data,
                          @NonNull Date startTime,
                          @NonNull Date endTime,
                          @Nullable Date firstEndTime,
                          @Nullable Date secondStartTime,
                          float yesClosePrice) {
        this.mFirstStartTime = startTime;
        this.mSecondEndTime = endTime;
        if (mFirstStartTime.getTime() >= mSecondEndTime.getTime()) {
            // TODO 以下情况无操作
            // 如果只有一条数据
            // 如果数据源不存在
            return;
        }
        mTotalTime = mSecondEndTime.getTime() - mFirstStartTime.getTime();
        if (firstEndTime != null && secondStartTime != null) {
            this.mFirstEndTime = firstEndTime;
            this.mSecondStartTime = secondStartTime;
            if (!(mFirstStartTime.getTime() < mFirstEndTime.getTime() &&
                    mFirstEndTime.getTime() < mSecondStartTime.getTime() &&
                    mSecondStartTime.getTime() < mSecondEndTime.getTime())) {
                return;
                //throw new IllegalStateException("时间区间有误");
            }
            mTotalTime -= mSecondStartTime.getTime() - mFirstEndTime.getTime() - 60000;
        }
        setValueStart(yesClosePrice);
        if (data != null) {
            mPoints.clear();
            this.mPoints.addAll(data);
        }
        notifyChanged();
    }

    /**
     * 当数据发生变化时调用
     */
    public void notifyChanged() {
        mValueMax = Float.MIN_VALUE;
        mValueMin = Float.MAX_VALUE;
        for (int i = 0; i < mPoints.size(); i++) {
            IMinuteLine point = mPoints.get(i);
            mValueMax = Math.max(mValueMax, point.getPrice());
            mValueMin = Math.min(mValueMin, point.getPrice());
            mVolumeMax = Math.max(mVolumeMax, point.getVolume());
        }
        //最大值和开始值的差值
        float offsetValueMax = mValueMax - mValueStart;
        float offsetValueMin = mValueStart - mValueMin;
        //以开始的点为中点值   上下间隙多出20%
        float offset = (offsetValueMax > offsetValueMin ? offsetValueMax : offsetValueMin) * 1.2f;
        //坐标轴高度以开始的点对称
        mValueMax = mValueStart + (offset == 0 ? mValueStart / 2 : offset);
        mValueMin = mValueStart - (offset == 0 ? mValueStart / 2 : offset);

        //y轴的缩放值
        mScaleY = mHeight / (mValueMax - mValueMin);
        //判断最大值和最小值是否一致
        if (mValueMax == mValueMin) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mValueMax += Math.abs(mValueMax * 0.05f);
            mValueMin -= Math.abs(mValueMax * 0.05f);
            if (mValueMax == 0) {
                mValueMax = 1;
            }
        }

        if (mVolumeMax == 0) {
            mVolumeMax = 1;
        }

        //成交量的缩放值
        mVolumeScaleY = mVolumeHeight / mVolumeMax;
        mPointWidth = (float) mWidth / getMaxPointCount();
        mVolumePaintUp.setStrokeWidth(mPointWidth * 0.8f);
        mVolumePaintDown.setStrokeWidth(mPointWidth * 0.8f);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackgroundColor);
        if (mWidth == 0 || mHeight == 0 || mPoints == null || mPoints.size() == 0) {
            return;
        }
        drawGird(canvas);

        if (mPoints.size() > 0) {
            IMinuteLine lastPoint = mPoints.get(0);
            float lastX = getX(0);

            // 线形轨迹
            Path shadowPath = new Path();
            Paint shadowPaint = new Paint();
            shadowPaint.setAntiAlias(true);
            shadowPaint.setStyle(Paint.Style.FILL);
            shadowPaint.setColor(0x558144E5);
            if (shadowShapeLinear == null) {
                shadowShapeLinear = new LinearGradient(lastX, mHeight, lastX, 0,
                        Color.parseColor("#338144E5"),
                        Color.parseColor("#EE8144E5"),
                        Shader.TileMode.CLAMP
                );
            }
            shadowPaint.setShader(shadowShapeLinear);

            // 起始
            shadowPath.moveTo(lastX, mHeight);
            shadowPath.lineTo(lastX, getY(lastPoint.getPrice()));
            for (int i = 0; i < mPoints.size(); i++) {
                IMinuteLine curPoint = mPoints.get(i);
                float curX = getX(i);
                canvas.drawLine(lastX, getY(lastPoint.getPrice()), curX, getY(curPoint.getPrice()), mPricePaint);
                canvas.drawLine(lastX, getY(lastPoint.getAvgPrice()), curX, getY(curPoint.getAvgPrice()), mAvgPaint);

                // 计算阴影区域(画线)
                shadowPath.lineTo(curX, getY(curPoint.getPrice()));

                //成交量
                if (!hideVolume) {
                    Paint volumePaint = ((i == 0 && curPoint.getPrice() <= mValueStart) || curPoint.getPrice() <= lastPoint.getPrice()) ? mVolumePaintDown : mVolumePaintUp;
                    canvas.drawLine(curX, getVolumeY(0), curX, getVolumeY((float) (curPoint.getVolume() * 0.9)), volumePaint);
                }
                lastPoint = curPoint;
                lastX = curX;
            }

            // 计算成闭合图形
            shadowPath.lineTo(lastX, mHeight);
            shadowPath.lineTo(getX(0), mHeight);

            // 画阴影
            canvas.drawPath(shadowPath, shadowPaint);
        }
        drawText(canvas);
        //画指示线
        if (isLongPress) {
            IMinuteLine point = mPoints.get(selectedIndex);
            float x = getX(selectedIndex);
            canvas.drawLine(x, 0, x, mHeight + mVolumeHeight, mTextPaint);
            canvas.drawLine(0, getY(point.getPrice()), mWidth, getY(point.getPrice()), mTextPaint);
            //画指示线的时间
            String text = DateUtil.shortTimeFormat.format(point.getDate());
            x = x - mTextPaint.measureText(text) / 2;
            if (x < 0) {
                x = 0;
            }
            if (x > mWidth - mTextPaint.measureText(text)) {
                x = mWidth - mTextPaint.measureText(text);
            }
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            float textHeight = fm.descent - fm.ascent;
            float baseLine = (textHeight - fm.bottom - fm.top) / 2;
            //下方时间
            canvas.drawRect(x, mHeight + mVolumeHeight - baseLine + textHeight, x + mTextPaint.measureText(text), mVolumeHeight + mHeight + baseLine, mBackgroundPaint);
            canvas.drawText(text, x, mHeight + mVolumeHeight + baseLine, mTextPaint);

            float r = textHeight / 2;
            float y = getY(point.getPrice());
            //左方值
            text = floatToString(point.getPrice());
            canvas.drawRect(0, y - r, mTextPaint.measureText(text), y + r, mBackgroundPaint);
            canvas.drawText(text, 0, fixTextY(y), mTextPaint);
            //右方值
            text = floatToString((point.getPrice() - mValueStart) * 100f / mValueStart) + "%";
            canvas.drawRect(mWidth - mTextPaint.measureText(text), y - r, mWidth, y + r, mBackgroundPaint);
            canvas.drawText(text, mWidth - mTextPaint.measureText(text), fixTextY(y), mTextPaint);
        }
        drawValue(canvas, isLongPress ? selectedIndex : mPoints.size() - 1);
    }

    /**
     * 画值
     */
    private void drawValue(Canvas canvas, int index) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        if (index >= 0 && index < mPoints.size()) {
            float y = baseLine - textHeight;
            IMinuteLine point = mPoints.get(index);
            String text = "成交价:" + floatToString(point.getPrice()) + " ";
            float x = 0;
            canvas.drawText(text, x, y, mPricePaint);
            x += mPricePaint.measureText(text);
            text = "均价:" + floatToString(point.getAvgPrice()) + " ";
            canvas.drawText(text, x, y, mAvgPaint);
            //成交量
            text = "VOL:" + mVolumeFormatter.format(point.getVolume());
            canvas.drawText(text, mWidth - mTextPaint.measureText(text), mHeight + baseLine, mTextPaint);
        }
    }

    /**
     * 修正y值
     */
    private float getY(float value) {
        return (mValueMax - value) * mScaleY;
    }

    private float getVolumeY(float value) {
        return (mVolumeMax - value) * mVolumeScaleY + mHeight;
    }

    private void drawGird(Canvas canvas) {
        //先画出坐标轴
        canvas.translate(0, mTopPadding);
        canvas.scale(1, 1);
        //横向的grid
        float rowSpace = mHeight / mGridRows;

        for (int i = 0; i <= mGridRows; i++) {
            canvas.drawLine(0, rowSpace * i, mWidth, rowSpace * i, mGridPaint);
        }
        canvas.drawLine(0, rowSpace * mGridRows / 2, mWidth, rowSpace * mGridRows / 2, mGridPaint);

        canvas.drawLine(0, mHeight + mVolumeHeight, mWidth, mHeight + mVolumeHeight, mGridPaint);
        //纵向的grid
        float columnSpace = mWidth / GridColumns;
        for (int i = 0; i <= GridColumns; i++) {
            canvas.drawLine(columnSpace * i, 0, columnSpace * i, mHeight + mVolumeHeight, mGridPaint);
        }

    }

    /**
     * 解决text居中的问题
     */
    public float fixTextY(float y) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return (y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }

    private void drawText(Canvas canvas) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        //画左边的值
        canvas.drawText(floatToString(mValueMax), 0, baseLine, mTextPaint);
        canvas.drawText(floatToString(mValueMin), 0, mHeight, mTextPaint);
        float rowValue = (mValueMax - mValueMin) / mGridRows;
        float rowSpace = mHeight / mGridRows;
        for (int i = 0; i <= mGridRows; i++) {
            String text = floatToString(rowValue * (mGridRows - i) + mValueMin);
            if (i >= 1 && i < mGridRows) {
                canvas.drawText(text, 0, fixTextY(rowSpace * i), mTextPaint);
            }
        }
        String text = floatToString((mValueMax - mValueStart) * 100f / mValueStart) + "%";
        canvas.drawText(text, mWidth - mTextPaint.measureText(text), baseLine, mTextPaint);
        text = floatToString((mValueMin - mValueStart) * 100f / mValueStart) + "%";
        canvas.drawText(text, mWidth - mTextPaint.measureText(text), mHeight, mTextPaint);
        for (int i = 0; i <= mGridRows; i++) {
            text = floatToString((rowValue * (mGridRows - i) + mValueMin - mValueStart) * 100f / mValueStart) + "%";
            if (i >= 1 && i < mGridRows) {
                canvas.drawText(text, mWidth - mTextPaint.measureText(text), fixTextY(rowSpace * i), mTextPaint);
            }
        }
        //画时间
        float y = mHeight + mVolumeHeight + baseLine;
        canvas.drawText(DateUtil.shortTimeFormat.format(mFirstStartTime), 0, y, mTextPaint);
        canvas.drawText(DateUtil.shortTimeFormat.format(mSecondEndTime),
                mWidth - mTextPaint.measureText(DateUtil.shortTimeFormat.format(mSecondEndTime)), y, mTextPaint);
        //成交量
        canvas.drawText(mVolumeFormatter.format(mVolumeMax), 0, mHeight + baseLine, mTextPaint);
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 保留2位小数
     */
    public String floatToString(float value) {
        String s = String.format("%.3f", value);
        return s;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        isLongPress = true;
        calculateSelectedX(e.getX());
        invalidate();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * 设置开始的值 对称轴线
     */
    public void setValueStart(float valueStart) {
        this.mValueStart = valueStart;
    }

    /**
     * 修改某个点的值
     *
     * @param position 索引值
     */
    public void changePoint(int position, IMinuteLine point) {
        mPoints.set(position, point);
        notifyChanged();
    }

    /**
     * 获取点的个数
     */
    private int getItemSize() {
        return mPoints.size();
    }

    /**
     * 刷新最后一个点
     */
    public void refreshLastPoint(IMinuteLine point) {
        changePoint(getItemSize() - 1, point);
    }

    /**
     * 添加一个点
     */
    public void addPoint(IMinuteLine point) {
        mPoints.add(point);
        notifyChanged();
    }

    /**
     * 根据索引获取点
     */
    public IMinuteLine getItem(int position) {
        return mPoints.get(position);
    }

    /**
     * 设置成交量格式化器
     *
     * @param volumeFormatter {@link IValueFormatter} 成交量格式化器
     */
    public void setVolumeFormatter(IValueFormatter volumeFormatter) {
        mVolumeFormatter = volumeFormatter;
    }
}
