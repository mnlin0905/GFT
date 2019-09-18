package com.linktech.gft.activity.financing.person

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes

/**
 * function---- SoftSettingActivity
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_MessageAlertActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_message_alert)
@InjectActivityTitle(titleRes = R.string.label_message_alert)
class MessageAlertActivity : BaseActivity<BasePresenter<MessageAlertActivity>>(), LineMenuListener {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
    }

    /**
     * @param v 被點擊到的v;此時應該是該view自身:LineMenuView
     */
    override fun performSelf(lmv: LineMenuView) {
        lmv.transition = !lmv.transition
    }
}
