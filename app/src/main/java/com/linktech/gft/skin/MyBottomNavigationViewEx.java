package com.linktech.gft.skin;

import android.content.Context;
import android.util.AttributeSet;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import skin.support.widget.SkinCompatSupportable;

public class MyBottomNavigationViewEx extends BottomNavigationViewEx implements SkinCompatSupportable {
    private SkinViewHelper skinViewHelper;

    public MyBottomNavigationViewEx(Context context) {
        this(context,null);
    }

    public MyBottomNavigationViewEx(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyBottomNavigationViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
}
