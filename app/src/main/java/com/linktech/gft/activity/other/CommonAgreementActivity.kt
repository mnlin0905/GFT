package com.linktech.gft.activity.other

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityWebView
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.view.TitleCenterToolbar
import org.jetbrains.anko.*

/**
 * Created on 2018/8/13  11:14
 * function : 各种钱包服务协议
 *
 * 通过其他界面传值,共用同一协议界面
 *
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_CommonAgreementActivity)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF])
//@InjectMenuRes(menuResId = R.menu.menu_more_refresh_browser)
class CommonAgreementActivity : BaseActivityWebView<BasePresenter<CommonAgreementActivity>>() {
    /**
     * 标题
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE, required = true)
    var title: String? = null

    /**
     * 网址net/本地asset-uri
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    var urls: String? = null

    /**
     * h5源码
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE_TWO, required = false)
    var datas: String? = null

    /**
     * 纯文本
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE_THREE, required = false)
    var plains: String? = null

    /**
     * 通用接口获取网址数据
     *
     * 1:钱包服务协议 2:生态服务协议 3:生态版权信息 4:锁仓释放规则 5:挖矿奖励规则
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE_FOUR, required = false)
    var type: Int? = null

    /**
     * 系统主题(toolbar)
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE_FIVE, required = false)
    var transTheme: Boolean = false

    override fun getContentOrViewId(): Int {
        verticalLayout {
            include<TitleCenterToolbar>(R.layout.dark_layout_top_bar)

            //装载webview
            frameLayout {
                backgroundColorResource = R.color.main_color_white
                id = R.id.id_fl_webview_container
            }.lparams(width = matchParent, height = matchParent)
        }

        return 0
    }

    override fun initData(savedInstanceState: Bundle?) {

        super.initData(savedInstanceState)
        /**
         * 动态添加webview
         */
        find<FrameLayout>(R.id.id_fl_webview_container).addView(singleWebView)

        toolbar.navigationIcon = dispatchGetSkinDrawable(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * 标题
         */
        setTitle(title)

        /**
         * 根据不同数据源,加载到web中
         */
        singleWebView?.run {
            when {
                urls != null -> loadUrl(urls, mapOf("Content-Type" to "text/html;charset=UTF-8")).empty(comment = "file:///android_asset/")
                datas != null -> loadDataWithBaseURL(null, datas, "text/html", Charsets.UTF_8.displayName(), null)
                plains != null -> loadDataWithBaseURL(null, plains, "text/plain", Charsets.UTF_8.displayName(), null)
                type != null ->
                    mPresenter.getCommonAgreementList(type!!) {
                        it?.filterRun({ !link.isNullOrBlank() }) {
                            loadUrl(link, mapOf("Content-Type" to "text/html;charset=UTF-8"))
                        } ?: toast(R.string.activity_common_agreement_error)
                    }
            }
        } ?: toast(R.string.activity_common_agreement_plugin_error)

        /**
         * 主题颜色
         */
        if(transTheme){
            (toolbar as TitleCenterToolbar).switchTheme(transTheme)
            setStatusTextBlack(transTheme)
        }
    }

    /**
     * 当网址形式为url(http/https)时,"在浏览器中打开"功能可用
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return false.empty(TODO = "不显示 menu")

        //如果是http或者https类型,则显示"在浏览器中打开按钮"
        menu!!.findItem(R.id.action_browser).isVisible = URLUtil.isNetworkUrl(urls)

        return (singleWebView != null).onFalse { toast(R.string.activity_common_agreement_wait) }
    }

    /**
     * 刷新 ->  刷新界面
     * 跳转 ->  使用浏览器打开
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_refresh -> singleWebView?.reload()
            R.id.action_browser -> browse(urls!!, newTask = true)
        }

        return true
    }

    override fun onBackPressed() {
        if (singleWebView?.canGoBack() == true) {
            singleWebView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}