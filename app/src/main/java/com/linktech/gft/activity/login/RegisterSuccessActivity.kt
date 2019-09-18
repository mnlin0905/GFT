package com.linktech.gft.activity.login


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import kotlinx.android.synthetic.main.activity_register_success.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- RegisterSuccessActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_RegisterSuccessActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_register_success)
@InjectActivityTitle(titleRes = R.string.label_register_success)
class RegisterSuccessActivity : BaseActivity<BasePresenter<RegisterSuccessActivity>>(), LineMenuListener {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 实名
        bt_common_function.apply {
            text = getString(R.string.activity_register_success_function)
            dOnClick {
               routeSuccessFinish(ARouterConst.Activity_BeginCertificationActivity)
            }
        }

        // 登录
        tv_login.dOnClick {
            finish()
        }
    }
}