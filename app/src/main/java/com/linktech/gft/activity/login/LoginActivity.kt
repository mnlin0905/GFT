package com.linktech.gft.activity.login


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityToolbar
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.LoginBean
import com.linktech.gft.interfaces.MyTabSelectedListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.*
import com.linktech.gft.view.WatchInputEditText
import com.linktech.gft.view.tablayout.CustomTabLayout
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_login_backup.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- LoginActivity
 *
 *
 * Created(Gradle default create) by ACChain on 2018/01/09 05:59:52 (+0000).
 */
@Route(path = ARouterConst.Activity_LoginActivity, extras = ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectLayoutRes(layoutResId = R.layout.activity_login_backup)
@InjectActivityTitle(titleRes = R.string.label_none)
class LoginActivity : BaseActivityToolbar<BasePresenter<LoginActivity>>(), TextView.OnEditorActionListener {
    /**
     * 是否需要自动回填 帐号/手机号
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var requireAutoFill: Boolean = true

    /**
     * 手机号
     */
    private var phone: String by viewBind(R.id.et_phone)
    /**
     * 动态验证码
     */
    private var randomCode: String by viewBind(R.id.et_random_code)
    /**
     * 手机号或者邮箱
     */
    private var username: String by viewBind(R.id.et_username)
    /**
     * 密码
     */
    private var password: String by viewBind(R.id.et_password)
    /**
     * 是否为手机号码动态登录
     * 默认为true
     */
    private var isLoginByRandom = true

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //tabLayout
        tl_type.apply {
            addTab(newTab().setText(R.string.loginType1))
            addTab(newTab().setText(R.string.loginType2))
            addOnTabSelectedListener(object : MyTabSelectedListener() {
                override fun onTabSelected(tab: CustomTabLayout.Tab?) {
                    //动态登录 或者 密码登录
                    isLoginByRandom = tab?.position == 0
                    fl_forgetPwd.visibility = if (isLoginByRandom) View.GONE else View.VISIBLE
                    linear_loginType1.visibility = if (isLoginByRandom) View.VISIBLE else View.GONE
                    linear_loginType2.visibility = if (isLoginByRandom) View.GONE else View.VISIBLE
                }
            })
        }

        //登录
        bt_common_function.text = dispatchGetString(R.string.function_login)

        //将上次帐号填充进来
        if (requireAutoFill) {
            username = DefaultPreferenceUtil.getInstance().username ?: ""
            phone = _phone ?: ""
        }

        //设置监听处理
        et_phone.setOnEditorActionListener(this)
        et_random_code.setOnEditorActionListener(this)
        et_username.setOnEditorActionListener(this)
        et_password.setOnEditorActionListener(this)

        //监听
        listOf(rctv_random_code, iv_pwd_show_hide, bt_common_function, register, forgetPwd).forEachIndexed { index, view ->
            view.dOnClick {
                when (index) {
                    0 means "获取手机验证码" -> if (verifyAccount()) mPresenter.sendInformation(phone, SmsConst.TAG_LOGIN) { rctv_random_code.startCount() }
                    1 means "密码可见性" -> {
                        view.isSelected = !view.isSelected
                        et_password.transformationMethod = if (view.isSelected) HideReturnsTransformationMethod() else PasswordTransformationMethod()
                    }
                    2 means "登录" -> {
                        when (isLoginByRandom) {
                            true means "动态登录" -> if (verifyDynamicLogin()) mPresenter.dynamicLogin(phone, randomCode) { loginFinish(it) }
                            false means "密码登录" -> if (verifyDoLogin()) mPresenter.doLogin(username, encode(password, MD5)) { loginFinish(it) }
                        }
                    }
                    3 means "注册" -> route(ARouterConst.Activity_RegisterActivity)
                    4 means "忘记密码" -> route(ARouterConst.Activity_ForgetPasswordActivity)
                }
            }
        }

        // 第三方登录 微信
        iv_wx_login.dOnClick {
            wxLogin()
        }

        // 第三方登录 其他
        iv_qq_login.dOnClick {
            toast(R.string.common_not_develop)
        }
        iv_xl_login.dOnClick {
            toast(R.string.common_not_develop)
        }
    }

    /**
     * 微信登录
     */
    fun wxLogin() {
        if (BaseApplication.app.mWxApi?.isWXAppInstalled == true) {
            SendAuth.Req().also {
                it.scope = "snsapi_userinfo"
                it.state = "check_wx_login"
                BaseApplication.app.mWxApi!!.sendReq(it)
                dealLoginSuccess()
            }
        } else {
            toast("您还未安装微信客户端")
        }
    }

    override fun onResume() {
        super.onResume()
        et_phone.post {
            et_phone.setSelection(phone.length)
        }
    }

    private fun loginFinish(result: LoginBean?) {
        //设置登录状态
        result?.let {
            DefaultPreferenceUtil.getInstance().signature = it.signature
            DefaultPreferenceUtil.getInstance().username = it.username
            DefaultPreferenceUtil.getInstance().login = true

            //登录成功,保存帐号信息
            mPresenter.getMemberInfo(_signature, _username) {
                it?.apply {
                    //保存一个用户信息,方便使用userid
                    DefaultPreferenceUtil.getInstance().saveUserInfoBean(this) {
                        setResult(Activity.RESULT_OK)
                        KeyboardUtils.hideSoftInput(this@LoginActivity)
                        dealLoginSuccess()
                    }
                } ?: toast(dispatchGetString(R.string.function_login_cannot_refresh))
            }
        }
    }

    /**
     * 登录成功后,处理逻辑
     *
     * 如果之前有界面已经打开,则返回之前界面
     * 如果之前没有成功打开的界面
     */
    fun dealLoginSuccess() {
        println("当前有多少个activity:? ${ActivityUtils.getActivityList().size}")
        println("当前有多少个activity:? ${ActivityUtils.getActivityList()}")
        if (ActivityUtils.getActivityList().filter {
                    !it.isFinishing && !it.isDestroyed
                }.size > 1) {
            finish()
        } else {
            routeSuccessFinish(ARouterConst.Activity_FinancingActivity)
        }
    }

    /**
     * @return true表示验证通过
     */
    private fun verifyAccount(): Boolean {
        return when {
            phone.isBlank() -> false.toast(dispatchGetString(R.string.function_login_no_mobile))
            phone notMatch RegexConst.REGEX_PHONE -> false.toast(dispatchGetString(R.string.function_login_error_mobile))
            else -> true
        }
    }

    /**
     * @return true表示动态登录的信息验证通过
     */
    private fun verifyDynamicLogin(): Boolean {
        return when {
            !verifyAccount() -> false
            randomCode.isBlank() -> false.toast(dispatchGetString(R.string.function_login_no_code))
            randomCode notMatch RegexConst.REGEX_RANDOM_NUMBER_6 -> false.toast(dispatchGetString(R.string.function_login_error_code))
            else -> true
        }
    }

    /**
     * @return true 表示密码登录的信息验证通过
     */
    private fun verifyDoLogin(): Boolean {
        return when {
            username.isBlank() -> false.toast(dispatchGetString(R.string.function_login_no_account))
            password.isBlank() -> false.toast(dispatchGetString(R.string.function_login_no_pwd))
            password notMatch RegexConst.REGEX_PASSWORD -> false.toast(dispatchGetString(R.string.function_login_error_pwd))
            else -> true
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        //当actionId表示无操作时
        if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
            when (v?.id) {
                R.id.et_phone//输入手机号完成,则默认去触发一次获取验证码按钮
                -> rctv_random_code.performClick()
                R.id.et_random_code//输入完验证码或者密码后,直接调用一次登录操作
                    , R.id.et_password -> bt_common_function.performClick()
            }//帐号输入完成,则不做处理,默认会跳转到下一行
        }
        return false
    }

    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)
        if (isLoginByRandom) {
            bt_common_function.isEnabled = listOf(phone, randomCode).all { it.isNotBlank() }
        } else {
            bt_common_function.isEnabled = listOf(username, password).all { it.isNotBlank() }
        }
    }

    override fun onBackPressed() {
        // 返回键时,直接退出应用
        ActivityUtil.returnHome(this)
    }
}