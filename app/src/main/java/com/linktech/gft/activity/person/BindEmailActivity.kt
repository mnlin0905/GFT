package com.linktech.gft.activity.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.RegexConst
import kotlinx.android.synthetic.main.activity_bind_email.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- BindEmailActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:25:43 (+0000).
 */
@Route(path = ARouterConst.Activity_BindEmailActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_bind_email)
@InjectActivityTitle(titleRes = R.string.label_bind_email)
class BindEmailActivity : BaseActivity<BasePresenter<BindEmailActivity>>() {
    /**
     * 邮箱
     */
    private var email: String by viewBind(R.id.et_email)
    /**
     * 图形验证码
     */
    private var imageCode: String by viewBind(R.id.et_image_code)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        bt_common_function.run {
            text = getString(R.string.function_next_step)
            dOnClick(::verifyEmail) {
                mPresenter.verifyIMGCodeAndEmail(_username, _signature, email, imageCode) {
                    it?.apply {
                        routeCustom(ARouterConst.Activity_InputCodeActivity)
                                .withString(Const.KEY_EMAIL, email)
                                .withString(Const.KEY_VERIFY_CODE, this)
                                .withInt(Const.KEY_CODE_TYPE, 3)
                                .navigation(this@BindEmailActivity, Const.REQUEST_CODE_ONE)
                    }
                }
            }
        }
        iv_code.run {
            dOnClick {
                val path = BaseApplication.app.baseWeUrl + "account/getVerifyCode?username=" + _username + "&signature=" + _signature + "&client=1" + "&time=" + System.currentTimeMillis()
                Glide.with(this@BindEmailActivity).load(path).into(this)
            }
        }
        iv_code.performClick()
    }

    /**
     * 验证邮箱和图形验证码
     */
    private fun verifyEmail(): Boolean {
        return when {
            email.isBlank() -> false.toast(getString(R.string.activity_bind_email_none_email))
            email notMatch RegexConst.REGEX_EMAIL -> showToast(getString(R.string.activity_bind_email_error))
            imageCode.isBlank() -> false.toast(getString(R.string.activity_bind_email_picture_code))
            imageCode notMatch RegexConst.REGEX_RANDOM_TEXT_4 -> false.toast(getString(R.string.activity_bind_email_picture_code_error))
            else -> true
        }
    }
}