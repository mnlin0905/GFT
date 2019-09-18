package com.linktech.gft.activity.person


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.MD5
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.encode
import kotlinx.android.synthetic.main.activity_change_mobile.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- ChangeMobileActivity
 *  更换手机号
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:24:09 (+0000).
 */
@Route(path = ARouterConst.Activity_ChangeMobileActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_VERIFY_HAS_SUCCESS or ARouterConst.FLAG_PHONE)
@InjectLayoutRes(layoutResId = R.layout.activity_change_mobile)
@InjectActivityTitle(titleRes = R.string.label_change_mobile)
class ChangeMobileActivity : BaseActivity<BasePresenter<BindMobileActivity>>(), LineMenuListener {
    /**
     * 区号
     */
    private var addNum: String = "86"
    /**
     * 手机号
     */
    private var phone: String by viewBind(R.id.et_phone)
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
        tv_add_num.text = "+$addNum"
        lmv_country.briefText = getString(R.string.activity_bind_mobile_china)

        bt_common_function.run {
            text = getString(R.string.function_next_step)
            dOnClick(::verifyPhone) {
                mPresenter.verifyMobileAndCardCode(_username, _signature, phone, cardNumber, encode(loginPassword, MD5)) {
                    it?.apply {
                        routeCustom(ARouterConst.Activity_InputCodeActivity)
                                .withString(Const.KEY_PHONE, phone)
                                .withString(Const.KEY_PHONE_CODE, addNum)
                                .withString(Const.KEY_VERIFY_CODE, this)
                                .withInt(Const.KEY_CODE_TYPE, 2)
                                .navigation(this@ChangeMobileActivity, Const.REQUEST_CODE_ONE)
                    }
                }
            }
        }
    }

    /**
     * @param v 被点击到的v;此时应该是该view自身:LineMenuView
     */
    override fun performSelf(v: LineMenuView) {
        ARouter.getInstance().build(ARouterConst.Activity_ChooseCountryActivity).navigation(this, Const.REQUEST_CODE_ONE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ONE) {
            data?.apply {
                lmv_country.briefText = getStringExtra(Const.KEY_COUNTRY_NAME)
                addNum = getStringExtra(Const.KEY_NATION_CODE)
                tv_add_num.text = "+$addNum"
            }
        }
    }

    /**
     * 验证手机号和证件号码后4位
     */
    private fun verifyPhone(): Boolean {
        return when {
            phone.isBlank() -> false.toast(getString(R.string.activity_change_mobile_no_mobile))
            phone notMatch RegexConst.REGEX_PHONE -> false.toast(getString(R.string.activity_change_mobile_error_mobile))
            phone == _phone -> showToast(getString(R.string.activity_change_mobile_cannot_equal))
            cardNumber.isBlank() -> false.toast(getString(R.string.activity_change_mobile_no_card))
            cardNumber notMatch RegexConst.REGEX_ID_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_POLICE_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_FOREIGN_CARD_NUMBER
                    && cardNumber notMatch RegexConst.REGEX_BACK_CARD_NUMBER -> false.toast(getString(R.string.activity_change_mobile_error_card))
            loginPassword.isBlank() -> false.toast(getString(R.string.activity_change_mobile_no_pwd))
            loginPassword notMatch RegexConst.REGEX_PASSWORD -> false.toast(getString(R.string.activity_change_mobile_error_pwd))
            else -> true
        }
    }
}