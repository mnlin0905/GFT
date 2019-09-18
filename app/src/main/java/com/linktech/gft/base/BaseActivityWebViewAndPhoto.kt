package com.linktech.gft.base

import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.ViewGroup
import android.widget.LinearLayout
import com.linktech.gft.R
import com.linktech.gft.util.L
import com.tencent.smtt.sdk.*
import java.io.File


/**
 * function : 携带一个WebView的activity
 *
 * Created on 2018/7/16  16:06
 * @author mnlin
 */
abstract class BaseActivityWebViewAndPhoto<T : BasePresenter<*>> : BaseActivityTPhotoWPer<T>() {
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

                override fun onReceivedTitle(p0: WebView?, p1: String?) {
                    super.onReceivedTitle(p0, p1)
                    changeTitle(p0?.title)
                }

                override fun onShowFileChooser(webview: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                    L.i("调相册")
                    mUploadCallbackAboveL = filePathCallback
                    //上传图片
                    showPicturePickDialog()
                    return true
                }
            }
        }
    }

    /**
     * 当title改变时
     */
    open fun changeTitle(title: String?) {}


    override fun takeSuccess(filePath: String, file: File) {
        mUploadCallbackAboveL?.apply {
            onReceiveValue(arrayOf(Uri.parse(filePath)))
            mUploadCallbackAboveL = null
        }
    }

    override fun onClickCancel(dialog: Dialog?): Boolean {
        takeFailOrCancel()
        return super.onClickCancel(dialog)
    }

    override fun takeFailOrCancel() {
        mUploadCallbackAboveL?.apply {
            onReceiveValue(null)
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
