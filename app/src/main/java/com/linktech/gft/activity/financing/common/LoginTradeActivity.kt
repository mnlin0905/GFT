package com.linktech.gft.activity.financing.common

import android.os.Bundle
import android.text.Editable
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.TradeBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.encrypt
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.activity_login_trade.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 理財中心首頁
 *
 *
 *
 *
 * Created on 2019/3/20  16:33
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_LoginTradeActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_login_trade)
@InjectActivityTitle(titleRes = R.string.label_login_trade)
class LoginTradeActivity : BaseActivity<BasePresenter<LoginTradeActivity>>() {
    private var account: String by viewBind(R.id.et_account)
    private var password: String by viewBind(R.id.et_password)

    /**
     * 選擇的券商
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var tradeBean: TradeBean? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //測試數據
        account = ""
        password = ""

//        account = "68850298"
//        password = "a12345678"

//        account = "58995899"
//        password = "Zq129007074"

        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        // 图标
        Glide.with(this)
                .load(tradeBean?.traderLogo)
                .apply(RequestOptions().circleCrop())
                .into(iv_logo)

        bt_common_function.run {
            text = getString(R.string.function_login)
            dOnClick {
                tradeBean?.apply {
                    BaseApplication.isTradeLogin = true
                    mPresenter.stockLogin(id, account, "1.0.0", 1360, "zh_CN", 720, "android",
                            "firefox", "android", "A000008C511E25", 1, encrypt(password, _trade_key)) {
                        it?.apply {
                            when(header.response_code){
                                10061 -> toast("输入密码错误次数过多,请10分钟后重试")
                                10062 -> toast("账号或密码错误")
                                else -> {
                                    //保存auth_code,替換後續請求中header裏面的auth_code
                                    BaseApplication.authCode = header.auth_code
                                    BaseApplication.tradeType = response_data.type
                                    BaseApplication.account = account
                                    BaseApplication.isTradeLogin = true
                                    routeCustom(ARouterConst.Activity_FinancingActivity)
                                            .param(2)
                                            .navigation()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        bt_common_function.isEnabled = listOf(account, password).all { it.isNotBlank() }
    }

}
