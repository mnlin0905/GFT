package com.linktech.gft.activity.person


import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.RegexConst
import kotlinx.android.synthetic.main.activity_verify_card_num.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import java.util.*

/**
 * function---- ChangeMobileActivity
 *  填写验证码 1-更换手机号，2-绑定手机号
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:24:09 (+0000).
 */
@Route(path = ARouterConst.Activity_VerifyCardNumActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_verify_card_num)
@InjectActivityTitle(titleRes = R.string.label_verify_card_num)
class VerifyCardNumActivity : BaseActivity<BasePresenter<BindMobileActivity>>(), LineMenuListener {

    @Autowired(name = Const.KEY_COMMON_VALUE, required = true)
    @JvmField
    var verifyCode: String = ""  // 校验码

    private var cardNumber: String by viewBind(R.id.et_card_number)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        setDefaultToolbarNavText()

        val tip = getString(R.string.activity_verify_card_num_text_name, _name)
        SpannableString(getString(R.string.activity_verify_card_num_text_name, _name)).also {
            (getApplicationLocale() == Locale.ENGLISH).onFalse {
                it.setSpan(StyleSpan(Typeface.BOLD), 2, tip.indexOf(getString(R.string.function_verify)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                it.setSpan(AbsoluteSizeSpan(16, true), 2, tip.indexOf(getString(R.string.function_verify)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }.let { tv_tip.text = it }

        bt_common_function.run {
            text = getString(R.string.function_next_step)
            dOnClick(this@VerifyCardNumActivity::verifyCardNumber) {
                mPresenter.verifyIdCard(_username, _signature, cardNumber, verifyCode) {
                    routeCustom(ARouterConst.Activity_ChangeTransPasswordActivity)
                            .withString(Const.KEY_COMMON_VALUE, it)
                            .withInt(Const.KEY_TYPE_CHANGE_TRANS_PWD, Const.TYPE_CHANGE_TRANS_MESSAGE_AND_CARD)
                            .navigation()
                    finish()
                }
            }
        }
    }

    /**
     * @return true 表示信息验证通过
     *
     */
    private fun verifyCardNumber(): Boolean {
        return when {
            cardNumber.isBlank() -> false.toast(getString(R.string.activity_verify_card_num_no_card))
            cardNumber notMatch RegexConst.REGEX_ID_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_POLICE_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_FOREIGN_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_BACK_CARD_NUMBER
            -> false.toast(getString(R.string.activity_verify_card_num_error_card))
            else -> true
        }
    }
}
