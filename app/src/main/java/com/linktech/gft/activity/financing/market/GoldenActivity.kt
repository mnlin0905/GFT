package com.linktech.gft.activity.financing.market


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityWebViewAndPhoto
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.L
import kotlinx.android.synthetic.main.activity_golden.*

/**
 * function---- TakeOutRecordDetailActivity
 * Created(Gradle default create) by LinkTech on 2018/01/12 08:01:44 (+0000).
 */
@Route(path = ARouterConst.Activity_GoldenActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_golden)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF])
class GoldenActivity : BaseActivityWebViewAndPhoto<BasePresenter<GoldenActivity>>() {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        setStatusTextBlack(true)

        // 装载webView
        fl_content.addView(singleWebView)

        singleWebView?.run {
            addJavascriptInterface(this@GoldenActivity, "app")
        }

        //返回键
        tv_back.dOnClick {
            onBackPressed()
        }

        //关闭键
        tv_return.dOnClick { finish() }

        //入金记录
        tv_recode.dOnClick {
            singleWebView?.post {
                singleWebView!!.loadUrl("javascript:recode()")
            }
        }
        loadData()
    }

    override fun onBackPressed() {
        singleWebView?.also {
            if (it.canGoBack()) {
                it.goBack()
            } else super.onBackPressed()
        }
    }

    override fun changeTitle(title: String?) {
        tv_title.text = title
    }

    private fun loadData() {
        L.i("url=" + getPath())
        singleWebView?.run {
            loadUrl(getPath())
        }
    }

    private fun getPath(): String {
        return BaseApplication.app.goldenUrl + "?lang=$_h5_language_str&username=$_username&signature=$_signature&fundAccount=$_account"
    }
}