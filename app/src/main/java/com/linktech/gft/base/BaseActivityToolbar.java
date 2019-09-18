package com.linktech.gft.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.blankj.utilcode.util.BarUtils;

/**
 * Created on 2019/5/8  17:59
 * function :处理 toolbar
 *
 * @author mnlin
 */
public abstract class BaseActivityToolbar<T extends BasePresenter> extends BaseActivity<T> {
    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        // 添加paddingTop
        if(toolbar!=null){
            toolbar.post(() -> {
                int paddingTop = BarUtils.getStatusBarHeight();
                toolbar.setPadding(toolbar.getPaddingLeft(), paddingTop, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
                ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
                layoutParams.height = toolbar.getHeight() + paddingTop;
                toolbar.setLayoutParams(layoutParams);
            });
        }
    }
}
