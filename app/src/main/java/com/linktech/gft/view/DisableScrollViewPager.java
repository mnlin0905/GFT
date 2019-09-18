package com.linktech.gft.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.linktech.gft.skin.SkinViewHelper;

import skin.support.widget.SkinCompatSupportable;

/**
 * Created on 2018/1/3
 * function : 不可以左右滑动的viewpager;同时关闭预加载
 *
 * @author LinkTech
 */
public class DisableScrollViewPager extends ViewPager  implements SkinCompatSupportable {
    private ManageScrollInterface manageScrollInterface;
    private SkinViewHelper skinViewHelper;

    {
        //如果context实现了接口,则默认设置为监听器
        if (getContext() != null && getContext() instanceof ManageScrollInterface) {
            setManageScrollInterface((ManageScrollInterface) getContext());
        }
    }

    public DisableScrollViewPager(Context context) {
        this(context,null);
    }

    public DisableScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            skinViewHelper = new SkinViewHelper(this, attrs);
        }
    }

    @Override
    public void applySkin() {
        if (skinViewHelper != null) {
            skinViewHelper.applySkin();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (manageScrollInterface != null && !manageScrollInterface.currentCanScroll()) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (manageScrollInterface != null && !manageScrollInterface.currentCanScroll()) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * @param manageScrollInterface 设置管理类
     */
    public void setManageScrollInterface(ManageScrollInterface manageScrollInterface) {
        this.manageScrollInterface = manageScrollInterface;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (manageScrollInterface != null && !manageScrollInterface.currentCanScroll()) {
            return false;
        }
        return super.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (manageScrollInterface != null && !manageScrollInterface.currentCanScroll()) {
            return false;
        }
        return super.canScrollVertically(direction);
    }

    /**
     * 控制滑动的接口
     */
    public interface ManageScrollInterface {
        /**
         * @return true表示当前状态可以触发滑动
         */
        boolean currentCanScroll();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //如果当前测量方式不是确定性的,则需要根据子布局来管理自身高度
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED){
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height)
                    height = h;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }
}

