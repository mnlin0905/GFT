package com.linktech.gft.activity.financing.market


import android.annotation.SuppressLint
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- BuySellActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_WithdrawActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_withdraw)
@InjectActivityTitle(title = "提取資金")
@SuppressLint("SetTextI18n")
class WithdrawActivity : BaseActivity<BasePresenter<WithdrawActivity>>() {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        //反馈
        bt_common_function.run {
            text = "去入金綁定收款賬戶"
            dOnClick {
                route(ARouterConst.Activity_GoldenActivity)
            }
        }
    }
}
