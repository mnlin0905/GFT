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
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ShowTransResultActivity
 * Created(Gradle default create) by MNLIN on 2018/05/26 07:41:26 (+0000).
 */
@Route(path = ARouterConst.Activity_ShowTransResultActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_TRANSACTION_PASSWORD )
@InjectLayoutRes(layoutResId = R.layout.activity_show_trans_result)
@InjectActivityTitle(titleRes = R.string.label_show_trans_result)
class ShowTransResultActivity : BaseActivity<BasePresenter<ShowTransResultActivity>>() {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        bt_common_function.run {
            text = getString(R.string.function_reset)
            dOnClick {
                ARouter.getInstance().build(ARouterConst.Activity_SelectChangeTransPasswordTypeActivity).navigation()
                finish()
            }
        }
    }
}