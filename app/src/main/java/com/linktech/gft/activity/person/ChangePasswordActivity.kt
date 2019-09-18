package com.linktech.gft.activity.person


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.util.MD5
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.encode
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ChangePasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 11:57:04 (+0000).
 */
@Route(path = ARouterConst.Activity_ChangePasswordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_change_password)
@InjectActivityTitle(titleRes = R.string.label_change_password)
class ChangePasswordActivity : BaseActivity<BasePresenter<ChangePasswordActivity>>() {
    /**
     *
     */
    private var oldPassword: String by viewBind(R.id.et_old_password)
    private var passwordOne: String by viewBind(R.id.et_password_one)
    private var passwordTwo: String by viewBind(R.id.et_password_two)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick(::verifyPassword) {
                mPresenter.changepwd(_username, _signature, encode(oldPassword, MD5), encode(passwordOne, MD5), encode(passwordTwo, MD5)) {
                    showToast(getString(R.string.activity_change_password_success))
                    DefaultPreferenceUtil.getInstance().signature = null
                    DefaultPreferenceUtil.getInstance().login = false
                    ARouter.getInstance().build(ARouterConst.Activity_LoginActivity).navigation()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    /**
     * @return true if ok
     */
    private fun verifyPassword(): Boolean {
        return when {
            oldPassword.isBlank() -> false.toast(getString(R.string.activity_change_password_no_old))
            oldPassword notMatch RegexConst.REGEX_PASSWORD -> false.toast(getString(R.string.activity_change_password_old_error))
            passwordOne.isBlank() -> false.toast(getString(R.string.activity_change_password_no_new))
            passwordTwo.isBlank() -> false.toast(getString(R.string.activity_change_password_no_confirm))
            passwordOne != passwordTwo -> false.toast(getString(R.string.activity_change_password_no_equal))
            passwordOne notMatch RegexConst.REGEX_PASSWORD -> false.toast(getString(R.string.activity_change_password_pwd_reg_error))
            oldPassword == passwordOne -> false.toast(getString(R.string.activity_change_password_no_equal_new_old))
            else -> true
        }
    }

    /**
     * 当文本发生变化
     */
    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        bt_common_function.isEnabled = listOf(oldPassword, passwordOne, passwordTwo).all { it.isNotBlank() }
    }
}

