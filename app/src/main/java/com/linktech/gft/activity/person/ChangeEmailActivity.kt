package com.linktech.gft.activity.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.MD5
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.encode
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ChangeEmailActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:25:21 (+0000).
 */
@Route(path = ARouterConst.Activity_ChangeEmailActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_VERIFY_HAS_SUCCESS or ARouterConst.FLAG_EMAIL)
@InjectLayoutRes(layoutResId = R.layout.activity_change_email)
@InjectActivityTitle(titleRes = R.string.label_change_email)
class ChangeEmailActivity : BaseActivity<BasePresenter<BindEmailActivity>>() {
    /**
     * 邮箱
     */
    private var email: String by viewBind(R.id.et_email)
    /**
     * 证件号
     */
    private var cardNumber: String by viewBind(R.id.et_card_number)
    /**
     * 登录密码
     */
    private var loginPassword: String by viewBind(R.id.et_login_password)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        bt_common_function.run {
            text = getString(R.string.function_next_step)
            dOnClick(::verifyEmail) {
                mPresenter.verifyEmailAndCardCode(_username, _signature, email, cardNumber, encode(loginPassword, MD5)) {
                    it?.apply {
                        routeCustom(ARouterConst.Activity_InputCodeActivity)
                                .withString(Const.KEY_EMAIL, email)
                                .withString(Const.KEY_VERIFY_CODE, this)
                                .withInt(Const.KEY_CODE_TYPE, 4)
                                .navigation(this@ChangeEmailActivity, Const.REQUEST_CODE_ONE)
                    }
                }
            }
        }
    }

    /**
     * 验证邮箱
     */
    private fun verifyEmail(): Boolean {
        return when {
            email.isBlank() -> false.toast(getString(R.string.activity_change_email_none_email))
            email notMatch RegexConst.REGEX_EMAIL -> false.toast(getString(R.string.activity_change_email_reg_error_email))
            email == _email -> false.toast(getString(R.string.activity_change_email_cannot_equal))
            cardNumber.isBlank() -> false.toast(getString(R.string.activity_change_email_no_card_code))
            cardNumber notMatch RegexConst.REGEX_ID_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_POLICE_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_FOREIGN_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_BACK_CARD_NUMBER -> false.toast(getString(R.string.activity_change_email_card_error))
            loginPassword.isBlank() -> false.toast(getString(R.string.activity_change_email_no_login_pwd))
            loginPassword notMatch RegexConst.REGEX_PASSWORD -> false.toast(getString(R.string.activity_change_email_login_pwd_error))
            else -> true
        }
    }
}