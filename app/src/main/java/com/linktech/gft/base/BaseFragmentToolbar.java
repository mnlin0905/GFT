package com.linktech.gft.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.BarUtils;
import com.linktech.gft.R;

/**
 * Created on 2018/11/27  16:42
 * function : 携带toolbar 的 fragment 的 padding 处理
 *
 * @author mnlin
 */
public class BaseFragmentToolbar<T extends BasePresenter> extends BaseFragment<T> {
    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        // 添加paddingTop
        View toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.post(() -> {
            int paddingTop = BarUtils.getStatusBarHeight();
            toolbar.setPadding(toolbar.getPaddingLeft(), paddingTop, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
            ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
            layoutParams.height = toolbar.getHeight() + paddingTop;
            toolbar.setLayoutParams(layoutParams);
        });
    }
}