package com.linktech.gft.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.linktech.gft.R;
import com.linktech.gft.base.BasePresenter;

import javax.annotation.Nullable;

import skin.support.widget.SkinCompatEditText;

/**
 * Created on 2018/8/16  13:39
 * function : 自带清除标志的输入框
 * <p>
 * 在使用过程中,不可再更改
 *
 * @author mnlin
 */
public class ClearEditText extends SkinCompatEditText {
    /**
     * 记录 横坐标
     */
    private float touchX;

    /**
     * 左侧点击事件重载
     * 右侧点击事件重载
     */
    private BasePresenter.HttpCallback<Editable> clickLeftCallback;
    private BasePresenter.HttpCallback<Editable> clickRightCallback;

    {
        //设置默认的清除标识

        int dimen24 = getResources().getDimensionPixelSize(R.dimen.smallest_view_height_24dp);

        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable drawable = getContext().getDrawable(R.drawable.selector_clear_text);
        drawable.setBounds(0, 0, Math.min(drawable.getMinimumWidth(), dimen24), Math.min(drawable.getMinimumHeight(), dimen24));
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2] != null ? compoundDrawables[2] : drawable, compoundDrawables[3]);
    }

    public ClearEditText(Context context) {
        super(context);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                break;
            case MotionEvent.ACTION_UP://如果点击的位置在右侧,并且手指抬起时仍在右侧,那么表示点击的clear标志
                //计算右侧距离
                Drawable drawableRight = getCompoundDrawables()[2];
                if (drawableRight != null) {
                    int judgeX = getWidth() - (drawableRight.getBounds().right - drawableRight.getBounds().left + getCompoundDrawablePadding() / 2 + getPaddingEnd());
                    //如果x位置大于判断基准,且down时位置也大于判断基准,则直接删除内容
                    if (event.getX() > judgeX && touchX > judgeX) {
                        if (clickRightCallback == null) {
                            post(() -> setText(null));
                        } else {
                            clickRightCallback.run(getText());
                        }
                    }
                }

                //计算左侧距离
                Drawable drawableLeft = getCompoundDrawables()[0];
                if (drawableLeft != null) {
                    int judgeX = drawableLeft.getBounds().right - drawableLeft.getBounds().left + getCompoundDrawablePadding() / 2 + getPaddingStart();
                    if (event.getX() < judgeX && touchX < judgeX) {
                        if (clickLeftCallback != null) {
                            clickLeftCallback.run(getText());
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 点击左侧时,进行处理
     */
    public void setOnClickLeft(@Nullable BasePresenter.HttpCallback<Editable> callback) {
        this.clickLeftCallback = callback;
    }

    /**
     * 点击左侧时,进行处理
     */
    public void setOnClickRight(@Nullable BasePresenter.HttpCallback<Editable> callback) {
        this.clickRightCallback = callback;
    }
}
