package com.linktech.gft.skin;

import android.util.AttributeSet;
import android.view.View;

import com.knowledge.mnlin.linemenuview.LineMenuView;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatHelper;

/**
 * Created by ximsfei on 2017/1/10.
 */

public class SkinViewHelper extends SkinCompatHelper {
    private final View mView;
    private int mBackgroundResId = INVALID_ID;
    private int mMenuResId = INVALID_ID;
    private int mBriefResId = INVALID_ID;
    private int mNavIcon = INVALID_ID;
    private int mToolTitleResId = INVALID_ID;
    private int mItemTextColorId = INVALID_ID;


    public SkinViewHelper(View view, AttributeSet attrs) {
        mView = view;
        loadFromAttributes(attrs);
    }

    private void loadFromAttributes(AttributeSet attrs) {
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            int attrValueId = attrs.getAttributeResourceValue(i, 0);
            if(mView instanceof MyBottomNavigationViewEx){
               // L.i("test123", String.format("当前属性索引为：%d,索引名为：%s", i, attributeName) + "   当前属性值为：" + attrs.getAttributeValue(i) + "   attrValueId=" + attrValueId);
            }
            if (attributeName.equals("background")) {
                mBackgroundResId = attrValueId;
            } else if (attributeName.equals("LineMenuView_menu_text_color")) {
                mMenuResId = attrValueId;
            } else if (attributeName.equals("LineMenuView_brief_text_color")) {
                mBriefResId = attrValueId;
            } else if (attributeName.equals("navigationIcon")) {
                mNavIcon = attrValueId;
            } else if (attributeName.equals("titleTextColor")) {
                mToolTitleResId = attrValueId;
            } else if (attributeName.equals("itemTextColor")) {
                mItemTextColorId = attrValueId;
            }
            applySkin();
        }
    }


    @Override
    public void applySkin() {
        //背景
        if (mBackgroundResId != INVALID_ID) {
            mView.setBackground(SkinCompatResources.getDrawable(mView.getContext(), mBackgroundResId));
        }
        //menu
        if (mMenuResId != INVALID_ID) {
            ((LineMenuView) mView).getMenu().setTextColor(SkinCompatResources.getColorStateList(mView.getContext(), mMenuResId));
        }
        //brief
        if (mBriefResId != INVALID_ID) {
            ((LineMenuView) mView).getBrief().setTextColor(SkinCompatResources.getColorStateList(mView.getContext(), mBriefResId));
        }
        //navigationIcon
        if (mNavIcon != INVALID_ID) {
            ((MyTitleCenterToolbar) mView).setNavigationIcon(SkinCompatResources.getDrawableCompat(mView.getContext(), mNavIcon));
        }
        //toolbar title颜色
        if (mToolTitleResId != INVALID_ID) {
            ((MyTitleCenterToolbar) mView).setTitleTextColor(SkinCompatResources.getColor(mView.getContext(), mToolTitleResId));
        }
        //navigation 文字颜色
        if (mItemTextColorId != INVALID_ID) {
            ((MyBottomNavigationViewEx) mView).setItemTextColor(SkinCompatResources.getColorStateList(mView.getContext(), mItemTextColorId));
        }
    }
}