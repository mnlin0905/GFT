package com.linktech.gft.base

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.ViewGroup
import android.widget.LinearLayout
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * function : 携带一个WebView的Fragment
 *
 * Created on 2018/7/16  16:06
 * @author mnlin
 */
abstract class BaseFragmentWebView<T : BasePresenter<*>> : BaseFragment<T>() {
    /**
     * webView显示内容
     */
    protected var singleWebView: WebView? = null

    /**
     * 提前初始化webview
     */
    @CallSuper
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        initWebView()
    }

    /**
     * 添加webview
     */
    private fun initWebView() {
        singleWebView = singleWebView ?: WebView(baseActivity).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            //配置属性
            settings?.apply {
                setAppCacheEnabled(true)
                domStorageEnabled = true
                databaseEnabled = true
                javaScriptEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                javaScriptCanOpenWindowsAutomatically = true
                loadWithOverviewMode = true
                cacheMode = android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                defaultTextEncodingName = "UTF-8"
                setSupportZoom(true)
                textSize = WebSettings.TextSize.NORMAL
            }

            //重写url变化的逻辑
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearWebViewResource()
    }

    /**
     * 防止webView内存泄露
     */
    private fun clearWebViewResource() {
        singleWebView = singleWebView?.run {
            stopLoading()
            removeAllViews()
            (parent as ViewGroup?)?.removeView(singleWebView)
            tag = null
            clearHistory()
            clearCache(true)
            clearFormData()
            clearMatches()
            clearSslPreferences()
            destroy()
            null
        }
    }
}
