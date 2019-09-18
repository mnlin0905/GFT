package com.linktech.gft.activity.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.*
import kotlinx.android.synthetic.main.activity_input_old_trans_password.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- InputOldTransPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/31 07:05:14 (+0000).
 */
@Route(path = ARouterConst.Activity_InputOldTransPasswordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_input_old_trans_password)
@InjectActivityTitle(titleRes = R.string.label_input_old_trans_password)
class InputOldTransPasswordActivity : BaseActivity<BasePresenter<InputOldTransPasswordActivity>>() {
    private lateinit var password: String
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        setToolbarNavText(toolbar, null, R.color.main_color_white)

        bt_common_function.run {
            text = getString(R.string.function_next_step)
            dOnClick {
                password = gpv_password.passWord
                when {
                    password.isBlank() -> showToast(getString(R.string.activity_input_old_trans_password_no_pwd))
                    password notMatch RegexConst.REGEX_TRANSACTION_PASSWORD -> showToast(context.getString(R.string.activity_input_old_trans_password_pwd_error))
                    else -> mPresenter.verifyPayPwd(_username, _signature, encode(password, MD5), SmsConst.TAG_CHANGE_TRANS_PWD) {
                        // 修改交易密码,成功后结束finish,然后跳转修改密码
                        routeCustom(ARouterConst.Activity_ChangeTransPasswordActivity)
                                .withString(Const.KEY_COMMON_VALUE, it)
                                .withInt(Const.KEY_TYPE_CHANGE_TRANS_PWD, Const.TYPE_CHANGE_TRANS_PWD_OLDPWD)
                                .navigation()
                        finish()
                    }
                }
            }
        }
    }
}