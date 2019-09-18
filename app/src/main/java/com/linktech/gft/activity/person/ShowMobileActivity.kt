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
import kotlinx.android.synthetic.main.activity_show_mobile.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ShowMobileActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:23:31 (+0000).
 */
@Route(path = ARouterConst.Activity_ShowMobileActivity, extras = ARouterConst.FLAG_PHONE or ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_PHONE)
@InjectLayoutRes(layoutResId = R.layout.activity_show_mobile)
@InjectActivityTitle(titleRes = R.string.label_show_mobile)
class ShowMobileActivity : BaseActivity<BasePresenter<ShowMobileActivity>>() {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        bt_common_function.run {
            text = getString(R.string.function_bind_new_mobile)
            dOnClick {
                ARouter.getInstance().build(ARouterConst.Activity_ChangeMobileActivity).navigation()
            }
        }
    }

    /**
     * 若是修改了手机号后返回该界面,需要主动去刷新显示内容
     */
    override fun onStart() {
        super.onStart()
        tv_phone.text = _phone?.replace("^(\\d{3}).*(\\d{4})$".toRegex(), "$1****$2")?:getString(R.string.common_unknown_value)
    }
}