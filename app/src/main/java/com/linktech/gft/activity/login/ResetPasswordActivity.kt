package com.linktech.gft.activity.login


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const.KEY_COMMON_VALUE_ONE
import com.linktech.gft.util.Const.KEY_COMMON_VALUE_TWO
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.util.MD5
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.encode
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- Activity_ResetPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 02:31:05 (+0000).
 */
@Route(path = ARouterConst.Activity_ResetPasswordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_reset_password)
@InjectActivityTitle(titleRes = R.string.label_reset_password)
class ResetPasswordActivity : BaseActivity<BasePresenter<ResetPasswordActivity>>() {
    /**
     * 验证码验证值
     */
    @Autowired(name = KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var verifyCode: String?= null

    /**
     * 帐号
     */
    @Autowired(name = KEY_COMMON_VALUE_TWO, required = true)
    @JvmField
    var account: String?= null

    /**
     *
     */
    private var passwordOne: String by viewBind(R.id.et_password_one)
    private var passwordTwo: String by viewBind(R.id.et_password_two)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //验证传递的值
        if (verifyCode.isNullOrEmpty() ||  account.isNullOrEmpty()) {
            showToast(getString(R.string.activity_find_password_no_verify_type))
            finish()
            return
        }

        //功能码
        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick(::verifyReset) {
                mPresenter.resetPwd(account, verifyCode, encode(passwordOne, MD5), encode(passwordTwo, MD5)) {
                    DefaultPreferenceUtil.getInstance().signature = null
                    DefaultPreferenceUtil.getInstance().login = false
                    routeCustom(ARouterConst.Activity_LoginActivity)
                            .navigationWithArrivalRun { setResult(Activity.RESULT_OK);finish() }
                            .toast(R.string.activity_find_password_login_again)
                }
            }
        }
    }

    /**
     * @return true 表示信息验证通过
     */
    private fun verifyReset(): Boolean {
        return when {
            passwordOne.isBlank() -> false.toast(getString(R.string.activity_find_password_no_login_pwd))
            passwordTwo.isBlank() -> false.toast(getString(R.string.activity_find_password_no_confirm_pwd))
            passwordOne != passwordTwo -> false.toast(getString(R.string.activity_find_password_pwd_not_equal))
            passwordOne notMatch RegexConst.REGEX_PASSWORD -> false.toast(getString(R.string.activity_find_password_pws_reg_error))
            else -> true
        }
    }

    /**
     * 当文本发生变化
     */
    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        bt_common_function.isEnabled = listOf( passwordOne, passwordTwo).all { it.isNotBlank() }
    }
}