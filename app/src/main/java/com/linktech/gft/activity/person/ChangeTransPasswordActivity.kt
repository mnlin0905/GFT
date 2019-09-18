package com.linktech.gft.activity.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const.KEY_COMMON_VALUE
import com.linktech.gft.util.Const.KEY_TYPE_CHANGE_TRANS_PWD
import com.linktech.gft.util.Const.TYPE_CHANGE_TRANS_MESSAGE_AND_CARD
import com.linktech.gft.util.Const.TYPE_CHANGE_TRANS_PWD_OLDPWD
import com.linktech.gft.util.MD5
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.encode
import kotlinx.android.synthetic.main.activity_change_trans_password.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ChangeTransPasswordActivity
 * 修改交易密码界面
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/31 07:28:27 (+0000).
 */
@Route(path = ARouterConst.Activity_ChangeTransPasswordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_change_trans_password)
@InjectActivityTitle(titleRes = R.string.label_change_trans_password)
class ChangeTransPasswordActivity : BaseActivity<BasePresenter<ChangeTransPasswordActivity>>() {
    /**
     * 跳转状态位:验证识别码,验证类型
     */
    @Autowired(required = false, name = KEY_COMMON_VALUE)
    @JvmField
    var verityCode: String? = null
    @Autowired(required = true, name = KEY_TYPE_CHANGE_TRANS_PWD)
    @JvmField
    var verityType: Int = -1

    var numbers: Array<String> = arrayOf("000000", "111111", "222222", "333333", "444444",
            "555555", "666666", "777777", "888888", "999999",
            "012345", "123456", "234567", "345678", "456789",
            "987654", "876543", "765432", "654321", "543210")

    /**
     * 成员变量
     */
    private lateinit var password: String

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //校验方式
        if (verityType !in TYPE_CHANGE_TRANS_PWD_OLDPWD..TYPE_CHANGE_TRANS_MESSAGE_AND_CARD) throw RuntimeException(getString(R.string.activity_change_sex_error_type))

        setDefaultToolbarNavText()

        tv_user_name.text = getString(R.string.activity_change_trans_password_tip,_username)

        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick(this@ChangeTransPasswordActivity::verifyPassword) {
                mPresenter.updatePayPwd(_username, _signature, encode(password, MD5), encode(password, MD5), null, 1, verityCode, "$verityType") {
                    ARouter.getInstance().build(ARouterConst.Activity_ChangeTransSuccessActivity).navigation()
                    finish()
                }
            }
        }
    }

    /**
     * @return true 表示信息验证通过
     */
    private fun verifyPassword(): Boolean {
        password = gpv_password.passWord
        return when {
            password.isBlank() -> showToast(getString(R.string.activity_change_trans_password_no_pwd))
            password notMatch  RegexConst.REGEX_TRANSACTION_PASSWORD -> false.toast(getString(R.string.activity_change_trans_password_error_pwd))
            !verifyNumber() -> showToast(getString(R.string.activity_change_trans_password_cannot_equal))
            else -> true
        }
    }

    private fun verifyNumber(): Boolean {
        for (num: String in numbers) {
            if (password == num) {
                return false
            }
        }
        return true
    }

}