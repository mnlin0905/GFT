package com.linktech.gft.activity.login


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const.KEY_TYPE_FIND_LOGIN_PASSWORD
import com.linktech.gft.util.Const.TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL
import com.linktech.gft.util.Const.TYPE_FIND_LOGIN_PASSWORD_BY_PHONE
import com.linktech.gft.util.EmailConst
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.SmsConst
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.activity_find_password.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- FindPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 02:31:05 (+0000).
 */
@Route(path = ARouterConst.Activity_FindPasswordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_find_password)
@InjectActivityTitle(titleRes = R.string.label_find_password)
class FindPasswordActivity : BaseActivity<BasePresenter<FindPasswordActivity>>() {
    /**
     * 成员变量
     */
    @Autowired(name = KEY_TYPE_FIND_LOGIN_PASSWORD, required = true)
    @JvmField
    var type: Int = -1

    /**
     *
     */
    private var account: String by viewBind(R.id.et_account)
    private var randomCode: String by viewBind(R.id.et_random_code)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //验证传递的值
        if (type !in TYPE_FIND_LOGIN_PASSWORD_BY_PHONE..TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL) {
            showToast(getString(R.string.activity_find_password_no_verify_type))
            finish()
            return
        }

        //初始化界面信息
        tv_label_account.text = if (type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE) getString(R.string.activity_login_phone_num) else getString(R.string.activity_register_email_el)
        et_account.run {
            hint = if (type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE) getString(R.string.activity_find_password_label_mobile) else getString(R.string.activity_find_password_label_email)
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(if (type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE) 11 else 50))
            inputType = if (type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE)
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        et_random_code.hint = if (type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE) getString(R.string.activity_find_password_hint_mobile) else getString(R.string.activity_find_password_hint_email)
        tv_declare.text = if (type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE) getString(R.string.activity_find_password_declare_mobile) else getString(R.string.activity_find_password_declare_email)

        //验证码
        rctv_random_code.dOnClick(::verifyAccount) {
            when (type) {
                TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL -> mPresenter.sendEmail(account, EmailConst.TAG_RESET_PWD) { rctv_random_code.startCount() }
                TYPE_FIND_LOGIN_PASSWORD_BY_PHONE -> mPresenter.sendInformation(account, SmsConst.TAG_RESET_PWD) { rctv_random_code.startCount() }
            }
        }

        //功能码
        bt_common_function.run {
            text = getString(R.string.label_reset_password)
            dOnClick(::verifyReset) {
                //先获取校验码
                mPresenter.verificationRandomCode(account, randomCode, if (type == TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL) EmailConst.TAG_RESET_PWD else SmsConst.TAG_RESET_PWD) {
                    setResult(Activity.RESULT_OK)
                    routeCustom(ARouterConst.Activity_ResetPasswordActivity)
                            .firstParam(randomCode)
                            .secondParam(account)
                            .navigationWithArrivalRun { finish() }
                            .empty(comment = "前往重置界面")
                }
            }
        }
    }


    /**
     * @return true表示验证通过
     */
    private fun verifyAccount(): Boolean {
        return when {
            type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE && account.isBlank() -> false.toast(getString(R.string.activity_find_password_no_mobile))
            type == TYPE_FIND_LOGIN_PASSWORD_BY_PHONE && account notMatch RegexConst.REGEX_PHONE -> false.toast(getString(R.string.activity_find_password_mobile_error))
            type == TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL && account.isBlank() -> false.toast(getString(R.string.activity_find_password_no_email))
            type == TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL && account notMatch RegexConst.REGEX_EMAIL -> false.toast(getString(R.string.activity_find_password_email_error))
            else -> true
        }
    }


    /**
     * @return true 表示信息验证通过
     */
    private fun verifyReset(): Boolean {
        return when {
            !verifyAccount() -> false
            randomCode.isBlank() -> false.toast(getString(R.string.activity_find_password_no_code))
            randomCode notMatch RegexConst.REGEX_RANDOM_NUMBER_6 -> false.toast(getString(R.string.activity_find_password_code_error))
            else -> true
        }
    }

    /**
     * 当文本发生变化
     */
    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        bt_common_function.isEnabled = listOf(account, randomCode).all { it.isNotBlank() }
    }
}