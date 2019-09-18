package com.linktech.gft.activity.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.MD5
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.SmsConst
import com.linktech.gft.util.encode
import kotlinx.android.synthetic.main.activity_set_transaction_password.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- SetTransactionPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 12:07:12 (+0000).
 */
@Route(path = ARouterConst.Activity_SetTransactionPasswordActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_PHONE)
@InjectLayoutRes(layoutResId = R.layout.activity_set_transaction_password)
@InjectActivityTitle(titleRes = R.string.label_set_transaction_password)
class SetTransactionPasswordActivity : BaseActivity<BasePresenter<SetTransactionPasswordActivity>>() {
    /**
     * 成员变量
     */
    private lateinit var passwordTwo: String
    private lateinit var passwordOne: String
    private lateinit var randomCode: String
    var numbers: Array<String> = arrayOf("000000", "111111", "222222", "333333", "444444",
            "555555", "666666", "777777", "888888", "999999",
            "012345", "123456", "234567", "345678", "456789",
            "987654", "876543", "765432", "654321", "543210")


    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //手机号
        tv_phone.text = _phone!!.replace("^(\\d{3}).*(\\d{4})$".toRegex(), "$1****$2")

        //设置交易密码
        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick({ verifySubmit() }) {
                mPresenter.updatePayPwd(_username, _signature, encode(passwordOne, MD5), encode(passwordTwo, MD5), randomCode, 0, null, null) {
                    showBigToast(getString(R.string.activity_set_transaction_password_success))
                    mPresenter.getMemberInfo(_signature, _username) {
                        finish()
                    }
                }
            }
        }

        //验证码
        rctv_random_code.dOnClick(this::verifyPassword) {
            mPresenter.sendInformation(_phone, SmsConst.TAG_RESET_PWD) {
                rctv_random_code.startCount()
            }
        }
    }

    /**
     * @return true 表示信息验证通过
     */
    private fun verifyPassword(): Boolean {
        return when {
            passwordOne.isBlank() -> false.toast(getString(R.string.activity_set_transaction_password_no_pwd))
            passwordTwo.isBlank() -> false.toast(getString(R.string.activity_set_transaction_password_no_confirm))
            passwordOne != passwordTwo -> false.toast(getString(R.string.activity_set_transaction_password_not_equal))
            passwordOne notMatch RegexConst.REGEX_TRANSACTION_PASSWORD -> false.toast(getString(R.string.activity_set_transaction_password_error_pwd))
            !verifyNumber() -> false.toast(getString(R.string.activity_set_pay_pwd_can_not_repeat))
            else -> true
        }
    }

    private fun verifyNumber(): Boolean {
        for (num: String in numbers) {
            if (passwordTwo == num) {
                return false
            }
        }
        return true
    }

    /**
     * @return true 表示信息验证通过
     */
    private fun verifySubmit(): Boolean {
        randomCode = et_random_code.text.toString()
        return when {
            !verifyPassword() -> false
            randomCode.isBlank() -> false.toast(getString(R.string.activity_set_transaction_password_no_code))
            randomCode notMatch RegexConst.REGEX_RANDOM_NUMBER_6 -> false.toast(getString(R.string.activity_set_transaction_password_error_code))
            else -> true
        }
    }
}