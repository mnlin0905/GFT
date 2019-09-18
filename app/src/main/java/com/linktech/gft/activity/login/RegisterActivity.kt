package com.linktech.gft.activity.login


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.KeyboardUtils
import com.knowledge.mnlin.rregister.util.BCRMachine
import com.knowledge.mnlin.rregister.util.ListenerInActivity
import com.knowledge.mnlin.rregister.util.RemoveTime
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.interfaces.MyTabSelectedListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.*
import com.linktech.gft.view.WatchInputEditText
import com.linktech.gft.view.tablayout.CustomTabLayout
import kotlinx.android.synthetic.main.activity_register_backup.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

/**
 * function---- RegisterActivity
 *
 *
 * Created(Gradle default create) by ACChain on 2018/01/09 07:44:40 (+0000).
 */
@RuntimePermissions
@Route(path = ARouterConst.Activity_RegisterActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_register_backup)
@InjectActivityTitle(titleRes = R.string.label_register)
class RegisterActivity : BaseActivity<BasePresenter<RegisterActivity>>(), TextView.OnEditorActionListener, ListenerInActivity {
    /**
     *
     */
    private var randomCode: String by viewBind(R.id.et_random_code)
    private var passwordOne: String by viewBind(R.id.et_password_one)
    private var passwordTwo: String by viewBind(R.id.et_password_two)
    private var nationCode: String = "86"
    private var account: String = ""

    /**
     * 手机号/邮箱注册
     */
    private var registerType: Int = 1

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        //切换注册方式
        tl_type.apply {
            addTab(newTab().setText(R.string.activity_register_phone))
            addTab(newTab().setText(R.string.activity_register_email))
            addOnTabSelectedListener(object : MyTabSelectedListener() {
                override fun onTabSelected(tab: CustomTabLayout.Tab?) {
                    registerType = tab!!.position + 1
                    ll_phone.visibility = if (registerType == 1) View.VISIBLE else View.GONE
                    ll_email.visibility = if (registerType == 2) View.VISIBLE else View.GONE

                    rctv_random_code.stopCount()
                }
            })
        }

        //国家地区
        iv_country.dOnClick {
            routeCustom(ARouterConst.Activity_ChooseCountryActivity).navigation(this@RegisterActivity, Const.REQUEST_CODE_ONE)
        }

        //Ecology服务协议
        cb_protocol.run {
            highlightColor = dispatchGetColor(android.R.color.transparent)
            movementMethod = LinkMovementMethod()
            text = SpannableString(getString(R.string.activity_register_service_agreement)).run {
                setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 跳转到协议界面
                        routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                                .param(getString(R.string.activity_about_inner_service))
                                .firstParam("${BaseApplication.app.baseWeUrl}gft_zh_tw/userProto.html")
                                .navigation()
                                .empty(comment = "協議內容")
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        //不设置下划线,变色
                        ds.color = dispatchGetColor(R.color.toolbar_second_color_primary)
                    }
                }, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                SpannableStringBuilder(getString(R.string.activity_register_to_read)).append(this)
            }
        }

        //获取手机/邮箱验证码
        rctv_random_code.dOnClick(::verifyAccount) {
            when (registerType) {
                1 -> mPresenter.sendInformation(account, SmsConst.TAG_REGISTER, nationCode) { rctv_random_code.startCount() }
                2 -> mPresenter.sendEmail(account, EmailConst.TAG_REGISTER) { rctv_random_code.startCount() }
            }
        }

        ////注册/先验证随机码
        bt_common_function.run {
            text = getString(R.string.label_register)
            dOnClick(this@RegisterActivity::verifyRegister) {
                val lam = {
                    mPresenter.register(account, randomCode, encode(passwordOne, MD5), encode(passwordTwo, MD5), null, nationCode) {
                        //showToast(getString(R.string.activity_register_register_success))
                        mPresenter.doLogin(account, encode(passwordOne, MD5)) {
                            mPresenter.getMemberInfo(_signature, _username) {
                                routeCustom(ARouterConst.Activity_RegisterSuccessActivity)
                                        .navigationWithArrivalRun { finish() }
                                        .empty(comment = "退出自身,回到登录界面")
                            }
                        }
                    }
                }
                //如果没有邀请码,则进行提示
                lam()
            }
        }

        //监听并填入短信
        initSMSReceiverWithPermissionCheck()

        //添加回车键监听
        wiet_email.setOnEditorActionListener(this)
        wiet_phone.setOnEditorActionListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ONE) {
            data?.apply {
                tv_country.text = getStringExtra(Const.KEY_COUNTRY_NAME)
                nationCode = getStringExtra(Const.KEY_NATION_CODE)
            }
        }
    }

    /**
     * @return 验证帐号
     */
    private fun verifyAccount(): Boolean {
        account = if (registerType == 1) wiet_phone.text.toString() else wiet_email.text.toString()
        return when {
            registerType == 1 && account.isNullOrBlank() -> false.toast(R.string.activity_find_password_no_mobile)
            registerType == 1 && account notMatch RegexConst.REGEX_PHONE -> false.toast(R.string.activity_register_phone_error)
            registerType == 2 && account.isNullOrBlank() -> false.toast(R.string.activity_find_password_no_email)
            registerType == 2 && account notMatch RegexConst.REGEX_EMAIL -> false.toast(R.string.activity_register_email_error)
            else -> true
        }
    }

    /**
     * 验证帐号密码验证码等是否可行
     *
     * @return true表示数据格式无误
     */
    private fun verifyRegister(): Boolean {
        return when {
            !verifyAccount() -> false
            randomCode.isBlank() -> false.toast(if (registerType == 1) getString(R.string.activity_register_sms_code) else getString(R.string.activity_register_email_code))
            randomCode notMatch RegexConst.REGEX_RANDOM_NUMBER_6 -> false.toast(if (registerType == 1) getString(R.string.activity_register_error_sms_code) else getString(R.string.activity_register_error_email_code))
            passwordOne.isBlank() -> false.toast(R.string.activity_register_no_login_pwd)
            passwordTwo.isBlank() -> false.toast(R.string.activity_register_no_confirm_pwd)
            passwordOne != passwordTwo -> false.toast(R.string.activity_register_no_equals_pwd)
            passwordOne notMatch RegexConst.REGEX_PASSWORD -> false.toast(R.string.activity_register_error_pwd)
            !cb_protocol.isChecked -> false.toast(R.string.activity_register_no_select_protocol)
            else -> true
        }
    }

    @NeedsPermission(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS)
    internal fun initSMSReceiver() {
        try {
            BCRMachine.registerBroadcastWithoutCheck(this, this, RemoveTime.onDestroy, { tag: Pair<String, String>? ->
                //当短信获取正确时,自动填入
                et_random_code.run {
                    setText(tag!!.second)
                    KeyboardUtils.showSoftInput(et_password_one)
                    setSelection(et_password_one.text.length)
                }
            }, MSmsReceiver::class.java)
        } catch (e: Exception) {
            toast(R.string.activity_register_cannot_auto_sms)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
            return when (v?.id) {
                R.id.et_email, R.id.et_phone ->//帐号输入完毕->主动发送短信验证码
                    rctv_random_code.performClick()
                else -> true
            }
        }
        return false
    }

    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        account = if (registerType == 1) wiet_phone.text.toString() else wiet_email.text.toString()
        bt_common_function.isEnabled = listOf(account, randomCode, passwordOne, passwordTwo).all { it.isNotBlank() }
    }
}