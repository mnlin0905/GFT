package com.linktech.gft.skin;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.tifezh.kchartlib.chart.MinuteChartView;
import com.linktech.gft.R;
import com.linktech.gft.util.DefaultPreferenceUtil;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatSupportable;

/**
 * Created on 2019/6/27  10:35
 * function : 分时图
 *
 * @author mnlin
 */
public class MyMinuteChartView extends MinuteChartView implements SkinCompatSupportable {
    public MyMinuteChartView(Context context) {
        super(context);
    }

    public MyMinuteChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMinuteChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getBackgroundColor() {
        return SkinCompatResources.getColor(getContext(), R.color.skin_scaffold_background_color);
    }

    @Override
    protected int getGridLineColor() {
        return SkinCompatResources.getColor(getContext(), R.color.skin_divider_color);
    }

    @Override
    protected int getNormalTextColor() {
        return SkinCompatResources.getColor(getContext(), R.color.skin_body1_color);
    }

    @Override
    protected int getTurnoverPriceColor() {
        return Color.parseColor("#FF6600");
    }

    @Override
    protected int getAvePriceColor() {
        return Color.parseColor("#90A901");
    }

    @Override
    public int getUpModeColor() {
        return DefaultPreferenceUtil.getInstance().isRedUpMode() ? SkinCompatResources.getColor(getContext(), R.color.skin_display4_color) : SkinCompatResources.getColor(getContext(), R.color.skin_display2_color);
    }

    @Override
    public int getDownModeColor() {
        return DefaultPreferenceUtil.getInstance().isRedUpMode() ? SkinCompatResources.getColor(getContext(), R.color.skin_display2_color) : SkinCompatResources.getColor(getContext(), R.color.skin_display4_color);
    }

    @Override
    public void applySkin() {
        super.refreshPaintColor();
    }
}
