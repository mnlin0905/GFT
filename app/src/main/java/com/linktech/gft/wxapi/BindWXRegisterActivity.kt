package com.linktech.gft.wxapi


import android.os.Bundle
import android.text.Editable
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.LoginBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.util.SmsConst
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.activity_begin_certification.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- Activity_ResetPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 02:31:05 (+0000).
 */
@Route(path = ARouterConst.Activity_BindWXRegisterActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_bind_wx_register)
@InjectActivityTitle(title = "绑定手机号")
class BindWXRegisterActivity : BaseActivity<BasePresenter<BindWXRegisterActivity>>() {
    private var phone: String by viewBind(R.id.et_phone)
    private var verifyCode: String by viewBind(R.id.et_verify_code)

    /**
     * 验证码
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var wxCode: String? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //验证码
        rctv_random_code.dOnClick(this::verifyOne) {
            mPresenter.sendInformation(phone, SmsConst.TAG_REGISTER) {
                rctv_random_code.startCount()
            }
        }

        //功能码
        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick(::verifyTwo) {
                mPresenter.weChatBind(wxCode, phone, verifyCode) {
                    it?.let {
                        loginFinish(it.data)
                    } ?: also {
                        showToast(R.string.common_unknown_error.toString() + ";result == null")
                    }
                }
            }
        }
    }

    /**
     * @return true 表示信息验证通过
     */
    private fun verifyOne(): Boolean {
        return when {
            else -> true
        }
    }

    /**
     * @return true 表示信息验证通过
     */
    private fun verifyTwo(): Boolean {
        return when {
            else -> true
        }
    }

    /**
     * 登录成功
     */
    private fun loginFinish(it: LoginBean) {
        //设置登录状态
        DefaultPreferenceUtil.getInstance().signature = it.signature
        DefaultPreferenceUtil.getInstance().username = it.username
        DefaultPreferenceUtil.getInstance().login = true

        //登录成功,保存帐号信息
        mPresenter.getMemberInfo(_signature, _username) { tag ->
            if (tag != null) {
                DefaultPreferenceUtil.getInstance().saveUserInfoBean(tag) { finish() }
                finish()
            }
        }
    }

    /**
     * 当文本发生变化
     */
    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        bt_common_function.isEnabled = listOf(phone, verifyCode).all { it.isNotBlank() }
    }
}