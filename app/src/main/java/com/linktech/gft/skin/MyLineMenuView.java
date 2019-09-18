package com.linktech.gft.skin;

import android.content.Context;
import android.util.AttributeSet;

import com.knowledge.mnlin.linemenuview.LineMenuView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import skin.support.widget.SkinCompatSupportable;

public class MyLineMenuView extends LineMenuView implements SkinCompatSupportable {
    private SkinViewHelper skinViewHelper;

    public MyLineMenuView(@NotNull Context context) {
        this(context, null);
    }

    public MyLineMenuView(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLineMenuView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
