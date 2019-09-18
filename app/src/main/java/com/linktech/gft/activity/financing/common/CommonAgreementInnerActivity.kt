package com.linktech.gft.activity.financing.common

import android.os.Bundle
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityWebView
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.InformationDetailBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_common_agreement_inner.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

/**
 * Created on 2018/8/13  11:14
 * function : 内置网页界面
 *
 * 通过其他界面传值,共用同一协议界面
 *
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_CommonAgreementInnerActivity, extras = ARouterConst.FLAG_LOGIN)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF])
@InjectLayoutRes(layoutResId = R.layout.activity_common_agreement_inner)
class CommonAgreementInnerActivity : BaseActivityWebView<BasePresenter<CommonAgreementInnerActivity>>() {
    /**
     * 当前网页的地址,如果后退后后还是当前地址,则再次发起后退
     *
     * 此数据有滞后性,再一次反回成功后,下次点击返回之前,该数据未刷新(如果要实时数据请调用 : singleWebView!!.copyBackForwardList().currentIndex)
     */
    private var lastShowUrl: String? = null

    /**
     * 模拟记录重定向的数据
     */
    private var lastBackUrl: String? = null

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
     * 通用接口获取资讯信息
     */
    @JvmField
    @Autowired(name = Const.KEY_COMMON_VALUE_FOUR, required = false)
    var informationId: Int? = null

    /**
     * 资讯信息数据
     */
    lateinit var dataBean: InformationDetailBean

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        /**
         * 动态添加webview
         */
        find<FrameLayout>(R.id.id_fl_webview_container).addView(singleWebView, 0)

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
        singleWebView?.settings?.textZoom = 100
        singleWebView?.run {
            when {
                urls != null -> loadUrl(urls, mapOf("Content-Type" to "text/html;charset=UTF-8")).empty(comment = "file:///android_asset/")
                datas != null -> loadDataWithBaseURL(null, datas, "text/html", Charsets.UTF_8.displayName(), null)
                plains != null -> loadDataWithBaseURL(null, plains, "text/plain", Charsets.UTF_8.displayName(), null)
                informationId != null ->
                    mPresenter.getNewsDetail(informationId!!, _username, _signature) {
                        it?.also {
                            dataBean = it

                            //有外链,则跳转
                            if (it.external == 1) {
                                loadUrl(it.externalLink)
                            } else {
                                //没有外联,则使用content内容
                                loadDataWithBaseURL(null, it.content, "text/html", Charsets.UTF_8.displayName(), null)
                            }

                            //当为资讯信息时,显示收藏功能/点赞效果
                            val refreshInfos = {
                                iv_collect.isActivated = dataBean.hasCollected
                                tv_agree.isActivated = dataBean.hasAgree
                                tv_agree.text = if (dataBean.awesome == 0) getString(R.string.activity_common_agreement_inner_agree) else dataBean.awesome.toString()
                            }

                            refreshInfos()

                            //收藏背景图
                            iv_collect.dOnClick {
                                mPresenter.collectionNews(it.id, _signature, _username) {
                                    toast(if (!dataBean.hasCollected) R.string.activity_common_agreement_inner_collect_success else R.string.activity_common_agreement_inner_not_require).empty(comment = "收藏")

                                    dataBean.hasCollected = !dataBean.hasCollected
                                    refreshInfos()
                                }
                            }

                            //点赞
                            fl_agree.setOnClickListener { tv_agree.performClick() }
                            tv_agree.dOnClick {
                                mPresenter.newsLike(it.id, _signature, _username) {
                                    toast(if (!dataBean.hasAgree) R.string.activity_common_agreement_inner_agree_success else R.string.activity_common_agreement_inner_agree_not).empty(comment = "点赞")
                                    dataBean.awesome = dataBean.awesome + (if (!dataBean.hasAgree) 1 else -1)

                                    dataBean.hasAgree = !dataBean.hasAgree
                                    refreshInfos()
                                }
                            }

                            //分享
                            iv_share.dOnClick {
                                startActivity(IntentUtils.getShareTextIntent(dataBean.externalLink
                                        ?: dataBean.content))
                            }
                        }
                    }
            }
        } ?: toast(R.string.activity_common_agreement_plugin_error)
    }

    override fun onBackPressed() {
        lastShowUrl = singleWebView!!.copyBackForwardList().currentItem.url
        if (singleWebView?.canGoBack() == true) {
            if (lastBackUrl == singleWebView?.copyBackForwardList()?.currentItem?.url) {
                // 如果当前位置处于第0/1位置,则直接退出应用,否则,返回第n-2层
                val currentIndex = singleWebView!!.copyBackForwardList().currentIndex
                if (currentIndex >= 2) {
                    lastBackUrl = null
                    singleWebView!!.loadUrl("javascript:history.go(-2)")
                } else {
                    super.onBackPressed()
                }
            } else {
                lastBackUrl = singleWebView?.copyBackForwardList()?.currentItem?.url
                singleWebView!!.goBack()

                //如果返回一次后,当前界面未改变,再次后退
                singleWebView?.postDelayed({
                    singleWebView?.postDelayed({
                        if (lastShowUrl == singleWebView!!.copyBackForwardList().currentItem.url) {
                            onBackPressed()
                        }
                    }, 300)
                }, 300)
            }
        } else {
            super.onBackPressed()
        }
    }
}