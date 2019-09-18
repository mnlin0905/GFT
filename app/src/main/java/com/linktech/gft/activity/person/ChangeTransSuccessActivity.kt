package com.linktech.gft.activity.person

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.dOnClick
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

@Route(path = ARouterConst.Activity_ChangeTransSuccessActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_change_trans_success)
@InjectActivityTitle(titleRes = R.string.label_change_trans_success)
class ChangeTransSuccessActivity : BaseActivity<BasePresenter<ChangeTransSuccessActivity>>() {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        setDefaultToolbarNavText()

        bt_common_function.run {
            text = getString(R.string.function_finish)
            dOnClick {
                //                ARouter.getInstance().build(ARouterConst.Activity_SafetyProtectionActivity).navigation()
                finish()
            }
        }
    }
}
