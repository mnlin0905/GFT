package com.linktech.gft.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.linktech.gft.R

/**
 * function : 携带一个WebView的Fragment
 *
 * Created on 2018/7/16  16:06
 * @author mnlin
 */
abstract class BaseFragmentToolbarWebView<T : BasePresenter<*>> : BaseFragmentWebView<T>() {

    /**
     * 提前初始化webview
     */
    @CallSuper
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 添加paddingTop
        val toolbar = rootView.findViewById<View>(R.id.toolbar)
        toolbar.post {
            val paddingTop = BarUtils.getStatusBarHeight()
            toolbar.setPadding(toolbar.paddingLeft, toolbar.paddingTop + paddingTop, toolbar.paddingRight, toolbar.paddingBottom)
            val layoutParams = toolbar.layoutParams
            layoutParams.height = toolbar.height + paddingTop
            toolbar.layoutParams = layoutParams
        }
    }
}
