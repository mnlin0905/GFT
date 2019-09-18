package com.linktech.gft.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.github.tifezh.kchartlib.chart.KChartView;
import com.linktech.gft.R;
import com.linktech.gft.util.DefaultPreferenceUtil;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatSupportable;

/**
 * Created on 2019/6/27  11:48
 * function : 适应模式的k-line
 *
 * @author mnlin
 */
public class MyKChartView extends KChartView implements SkinCompatSupportable {
    public MyKChartView(Context context) {
        super(context);
    }

    public MyKChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyKChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getSkinColorResource(TypedArray array, int index, int defaultRes) {
        int colorResId = array.getResourceId(index,defaultRes);
        return SkinCompatResources.getColor(getContext(), colorResId);
    }

    @Override
    protected ColorStateList getSkinColorStateListResource(TypedArray array, int index, int defaultRes) {
        int colorResId = array.getResourceId(index,defaultRes);
        return SkinCompatResources.getColorStateList(getContext(), colorResId);
    }

    @Override
    public int getUpModeColor() {
        return DefaultPreferenceUtil.getInstance().isRedUpMode()? SkinCompatResources.getColor(getContext(), R.color.skin_display4_color) :SkinCompatResources.getColor(getContext(), R.color.skin_display2_color) ;
    }

    @Override
    public int getDownModeColor() {
        return DefaultPreferenceUtil.getInstance().isRedUpMode()? SkinCompatResources.getColor(getContext(), R.color.skin_display2_color) :SkinCompatResources.getColor(getContext(), R.color.skin_display4_color) ;
    }

    @Override
    public void applySkin() {

    }
}
