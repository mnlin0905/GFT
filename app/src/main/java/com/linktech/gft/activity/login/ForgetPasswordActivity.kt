package com.linktech.gft.activity.login


import android.app.Activity
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.routeCustom
import com.linktech.gft.util.Const

/**
 * function---- ForgetPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_ForgetPasswordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_forget_password)
@InjectActivityTitle(titleRes = R.string.label_forget_password)
class ForgetPasswordActivity : BaseActivity<BasePresenter<ForgetPasswordActivity>>(), LineMenuListener {
    override fun performSelf(v: LineMenuView) {
        routeCustom(ARouterConst.Activity_FindPasswordActivity)
                .withInt(Const.KEY_TYPE_FIND_LOGIN_PASSWORD, v.getTag(LMVConfigs.TAG_POSITION) as Int)
                .navigation(this, Const.REQUEST_CODE_ONE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.REQUEST_CODE_ONE && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}