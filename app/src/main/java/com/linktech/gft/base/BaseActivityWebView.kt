package com.linktech.gft.base

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.ViewGroup
import android.widget.LinearLayout
import com.linktech.gft.R
import com.linktech.gft.util.Const
import com.linktech.gft.util.L
import com.tencent.smtt.sdk.*

/**
 * function : 携带一个WebView的activity
 *
 * Created on 2018/7/16  16:06
 * @author mnlin
 */
 abstract class BaseActivityWebView<T : BasePresenter<*>> : BaseActivity<T>() {
    /**
     * webView显示内容
     */
    protected var singleWebView: WebView? = null


    private var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null


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
    fun initWebView() {
        singleWebView = singleWebView ?: WebView(this).apply {
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

                /**
                 * 结束时候关闭进度框
                 */
                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    mPresenter.disappearLoading()
                    changeTitle(p0?.title)
                }

                /**
                 * 开启加载窗口
                 */
                override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                    super.onPageStarted(p0, p1, p2)
                    mPresenter.appearLoading("${getString(R.string.common_load_ing)}...")
                }

            }

            //重写调用相册的逻辑
            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(webview: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                    L.i("调相册")
                    chooseImage(filePathCallback)
                    return true
                }
            }
        }
    }

    /**
     * 当title改变时
     */
    open  fun changeTitle(title: String?){}

    /**
     * android 5.0(含) 以上开启图片选择（原生）
     *
     * 可以自己改图片选择框架。
     */
    private fun chooseImage(filePathCallback: ValueCallback<Array<Uri>>?) {
        mUploadCallbackAboveL = filePathCallback
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "image/*"
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        startActivityForResult(chooserIntent, Const.REQUEST_CODE_ONE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val result = when {
            resultCode != Activity.RESULT_OK || intent == null -> null
            else -> intent.data
        }
        if (requestCode == Const.REQUEST_CODE_ONE) {
            if (null == mUploadCallbackAboveL) return
            if (result != null) {
                mUploadCallbackAboveL!!.onReceiveValue(arrayOf(result))
            } else {
                mUploadCallbackAboveL!!.onReceiveValue(arrayOf())
            }
            mUploadCallbackAboveL = null
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
