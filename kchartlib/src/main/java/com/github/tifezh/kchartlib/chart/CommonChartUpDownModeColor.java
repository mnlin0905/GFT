package com.github.tifezh.kchartlib.chart;

import android.support.annotation.ColorInt;

/**
 * Created on 2019/6/27  13:58
 * function : 涨跌时取值的颜色
 *
 * @author mnlin
 */
public interface CommonChartUpDownModeColor {
    /**
     * 返回(￣︶￣)↗ 涨的颜色
     */
    @ColorInt
    int getUpModeColor();

    /**
     * 返回(┬＿┬)↘ 跌的颜色
     */
    @ColorInt
    int getDownModeColor();
}
