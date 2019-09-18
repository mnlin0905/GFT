package com.linktech.gft.skin;

import android.content.Context;
import android.util.AttributeSet;

import com.linktech.gft.view.NewsConstraintLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import skin.support.widget.SkinCompatSupportable;

public class MyNewsConstraintLayout extends NewsConstraintLayout implements SkinCompatSupportable {
    private SkinViewHelper skinViewHelper;

    public MyNewsConstraintLayout(@NotNull Context context) {
        this(context, null);
    }

    public MyNewsConstraintLayout(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyNewsConstraintLayout(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
