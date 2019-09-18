package com.linktech.gft.activity.person


import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.EmailConst
import com.linktech.gft.util.SmsConst
import com.linktech.gft.view.PhoneCode
import kotlinx.android.synthetic.main.activity_input_code.*
import java.util.*

/**
 * function---- ChangeMobileActivity
 *  填写验证码 1-更换手机号，2-绑定手机号
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:24:09 (+0000).
 */
@Route(path = ARouterConst.Activity_InputCodeActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_input_code)
class InputCodeActivity : BaseActivity<BasePresenter<BindMobileActivity>>(), LineMenuListener {
    @Autowired(name = Const.KEY_PHONE, required = false)
    @JvmField
    var phone: String = ""     //手机号

    @Autowired(name = Const.KEY_PHONE_CODE, required = false)
    @JvmField
    var phoneCode: String = "" //区号

    @Autowired(name = Const.KEY_EMAIL, required = false)
    @JvmField
    var email: String = ""     //邮箱

    @Autowired(name = Const.KEY_VERIFY_CODE, required = false)
    @JvmField
    var verifyCode: String = "" //验证标识

    /**
     * type 1-绑定手机号，2-更换手机号,3-绑定邮箱,4-更换邮箱,5--重置交易密码时 身份校验
     */
    @Autowired(name = Const.KEY_CODE_TYPE, required = true)
    @JvmField
    var codeType: Int = 0    //手机号

    private lateinit var reason: String

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (codeType == 0) {
            showToast(getString(R.string.common_no_require_params))
            return
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        when (codeType) {
            1 -> title = getString(R.string.label_bind_mobile)
            2 -> title = getString(R.string.label_change_mobile)
            3 -> title = getString(R.string.label_bind_email)
            4 -> title = getString(R.string.label_change_email)
            5 -> {
                title = getString(R.string.label_verify_card_num)
                setDefaultToolbarNavText()
            }
        }

        when (codeType) {
            1, 2, 5 -> {
                //已发送验手机号&邮箱
                SpannableString(getString(R.string.activity_input_code_verify_code)).also {
                    (getApplicationLocale().language == Locale.ENGLISH.language).onFalse {
                        it.setSpan(StyleSpan(Typeface.BOLD), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        it.setSpan(AbsoluteSizeSpan(16, true), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }.let { tv_describe.text = it }
                // 手机号&邮箱
                SpannableStringBuilder(if (codeType == 5) phoneCode else "+$phoneCode").let {
                    //                    it.setSpan(SubscriptSpan(), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    it.setSpan(AbsoluteSizeSpan(12, true), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    it.setSpan(ForegroundColorSpan(Color.parseColor("#3a4254")), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    it.append(phone)
                }.let { tv_phone.text = it }

                //重新获取
                SpannableString(getString(R.string.activity_input_code_no_sms)).also {
                    (getApplicationLocale().language == Locale.ENGLISH.language).onFalse {
                        it.setSpan(object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                sendCode()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                ds.color = dispatchGetColor(R.color.toolbar_second_color_primary)
                            }
                        }, 10, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }.onTrue {
                        it.setSpan(ForegroundColorSpan(dispatchGetColor(R.color.toolbar_second_color_primary)), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }.let {
                    tv_reset.movementMethod = LinkMovementMethod.getInstance()
                    tv_reset.text = it
                    tv_reset.dOnClick {
                        sendCode()
                    }
                }
            }
            3, 4 -> {
                //已发送验手机号&邮箱
                SpannableString(getString(R.string.activity_input_code_send_to_email)).also {
                    (getApplicationLocale().language == Locale.ENGLISH.language).onFalse {
                        it.setSpan(StyleSpan(Typeface.BOLD), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        it.setSpan(AbsoluteSizeSpan(16, true), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }.let { tv_describe.text = it }

                // 手机号&邮箱
                tv_phone.text = email

                //重新获取
                SpannableString(getString(R.string.activity_input_code_no_email)).also {
                    (getApplicationLocale().language == Locale.ENGLISH.language).onFalse {
                        it.setSpan(object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                sendCode()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                ds.color = dispatchGetColor(R.color.toolbar_second_color_primary)
                            }
                        }, 8, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }.onTrue {
                        it.setSpan(ForegroundColorSpan(Color.parseColor("#684bf1")), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }.let {
                    tv_reset.movementMethod = LinkMovementMethod.getInstance()
                    tv_reset.text = it
                }
            }
        }

        phone_code.setOnInputListener(object : PhoneCode.OnInputListener {
            override fun onSucess(code: String?) {
                val lam = {
                    ARouter.getInstance()
                            .build(ARouterConst.Activity_SoftSettingActivity)
                            .withFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            .greenChannel()
                            .navigation()
                }
                when (codeType) {
                    1 -> {
                        mPresenter.updateMobile(_username, _signature, phone, code, verifyCode, phoneCode) {
                            mPresenter.getMemberInfo(_signature, _username) {
                                toast(R.string.activity_input_code_bind_mobile_success)
                                lam()
                            }
                        }
                    }
                    2 -> {
                        mPresenter.updateMobile(_username, _signature, phone, code, verifyCode, phoneCode) {
                            _username = phone
                            mPresenter.getMemberInfo(_signature, _username) {
                                toast(R.string.activity_input_code_change_mobile_success)
                                lam()
                            }
                        }
                    }
                    3 -> {
                        mPresenter.updateEmail(_username, _signature, email, code, verifyCode) {
                            mPresenter.getMemberInfo(_signature, _username) {
                                toast(R.string.activity_input_code_bind_email_success)
                                lam()
                            }
                        }
                    }
                    4 -> {
                        mPresenter.updateEmail(_username, _signature, email, code, verifyCode) {
                            _username = email
                            mPresenter.getMemberInfo(_signature, _username) {
                                toast(R.string.activity_input_code_change_email_success)
                                lam()
                            }
                        }
                    }
                    5 -> {
                        mPresenter.verifyRandomCode(_username, _signature, code, SmsConst.TAG_CHANGE_TRANS_PWD) {
                            ARouter.getInstance().build(ARouterConst.Activity_VerifyCardNumActivity)
                                    .withString(Const.KEY_COMMON_VALUE, it).navigation()
                            finish()
                        }
                    }

                }
            }

            override fun onInput() {
            }
        })
        sendCode()
    }

    // 发送验证码
    private fun sendCode() {
        // type 1-绑定手机号，2-更换手机号,3-绑定邮箱,4-更换邮箱,5-身份校验
        reason = when (codeType) {
            1 -> SmsConst.TAG_BOUND_MOBILE
            2 -> SmsConst.TAG_CHANGE_MOBILE
            3 -> EmailConst.TAG_BOUND_EMAIL
            4 -> EmailConst.TAG_CHANGE_EMAIL
            5 -> SmsConst.TAG_CHANGE_TRANS_PWD
            else -> "d"
        }
        when (codeType) {
            1, 2 -> mPresenter.sendInformation(phone, reason, phoneCode) {}
            3, 4 -> mPresenter.sendEmail(email, reason) {}
            5 -> mPresenter.sendInformation(phone, reason) {}
        }
    }

    /**
     * 上次点击back时间
     */
    private var lastPressBackTime: Long = 0

    override fun onBackPressed() {
        when {
            System.currentTimeMillis() - lastPressBackTime > 1000 -> lastPressBackTime = System.currentTimeMillis().toast(R.string.activity_main_back_press)
            else -> super.onBackPressed()
        }
    }
}