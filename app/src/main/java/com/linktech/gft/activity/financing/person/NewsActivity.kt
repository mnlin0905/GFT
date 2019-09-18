package com.linktech.gft.activity.financing.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_news.*

/**
 * function---- 消息列表
 *
 * Created(Gradle default create) by LinkTech on 2018/01/22 13:43:05 (+0000).
 */
@Route(path = ARouterConst.Activity_NewsActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_news)
@InjectActivityTitle(titleRes = R.string.label_news)
class NewsActivity : BaseActivity<BasePresenter<NewsActivity>>() {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        empty {
            nll_logistics.setBadge(5, dispatchGetColor(R.color.main_color_white), dispatchGetColor(R.color.dark_color_20bf7c))
        }

        // 默认空白界面
        listOf(nll_notification, nll_logistics, nll_online_chat, nll_online_service).dOnClick {
            when (this.id) {
                R.id.nll_notification -> Const.TYPE_NEWS_MESSAGE_NOTICE to getString(R.string.activity_news_alert)
                R.id.nll_logistics -> Const.TYPE_NEWS_MESSAGE_PLAY to getString(R.string.activity_news_play)
                R.id.nll_online_chat -> Const.TYPE_NEWS_MESSAGE_CHAT to getString(R.string.activity_news_system)
                R.id.nll_online_service -> Const.TYPE_NEWS_MESSAGE_SERVICE to getString(R.string.activity_news_online)
                else -> TODO()
            }.let {
                routeCustom(ARouterConst.Activity_NoDataActivity)
                        .firstParam(it.second)
                        .navigation()
                        .empty(comment = "列表")
            }
        }
    }
}
