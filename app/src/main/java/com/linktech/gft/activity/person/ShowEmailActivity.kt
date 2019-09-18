package com.linktech.gft.activity.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.dOnClick
import kotlinx.android.synthetic.main.activity_show_email.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ShowEmailActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:25:31 (+0000).
 */
@Route(path = ARouterConst.Activity_ShowEmailActivity, extras = ARouterConst.FLAG_EMAIL or ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_show_email)
@InjectActivityTitle(titleRes = R.string.label_show_email)
class ShowEmailActivity : BaseActivity<BasePresenter<ShowEmailActivity>>() {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        bt_common_function.run {
            text = getString(R.string.function_bind_new_email)
            dOnClick {
                ARouter.getInstance().build(ARouterConst.Activity_ChangeEmailActivity).navigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        tv_email.text = _email?.replace("^(\\S{2}).*(@.*)$".toRegex(), "$1****$2") ?: getString(R.string.common_unknown_value)
    }
}