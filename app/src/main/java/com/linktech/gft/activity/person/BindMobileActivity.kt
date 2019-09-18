package com.linktech.gft.activity.person


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.RegexConst
import kotlinx.android.synthetic.main.activity_bind_mobile.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*


/**
 * function---- BindMobileActivity
 * 绑定手机号,不能主动调用,只能在实名认证时候绑定
 *
 * Created(Gradle default create) by LinkTech on 2018/01/20 07:23:58 (+0000).
 */
@Route(path = ARouterConst.Activity_BindMobileActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_bind_mobile)
@InjectActivityTitle(titleRes = R.string.label_bind_mobile)
class BindMobileActivity : BaseActivity<BasePresenter<BindMobileActivity>>(), LineMenuListener {

    /**
     * 手机号
     */
    private var phone: String by viewBind(R.id.et_phone)
    /**
     * 图形验证码
     */
    private var imageCode: String by viewBind(R.id.et_image_code)
    /**
     * 区号
     */
    private lateinit var nationCode: String

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        nationCode = "86"
        lmv_country.briefText = getString(R.string.activity_bind_mobile_china)
        tv_add_num.text = "+$nationCode"
        bt_common_function.run {
            text = getString(R.string.function_next_step)
            dOnClick(::verifyPhone) {
                mPresenter.verifyIMGCodeAndMobile(_username, _signature, phone, imageCode) {
                    it?.apply {
                        routeCustom(ARouterConst.Activity_InputCodeActivity)
                                .withString(Const.KEY_PHONE, phone)
                                .withString(Const.KEY_PHONE_CODE, nationCode)
                                .withString(Const.KEY_VERIFY_CODE, this)
                                .withInt(Const.KEY_CODE_TYPE, 1).navigation(this@BindMobileActivity, Const.REQUEST_CODE_ONE)
                    }
                }
            }
        }
        iv_code.run {
            dOnClick {
                val path = BaseApplication.app.baseWeUrl + "account/getVerifyCode?username=" + _username + "&signature=" + _signature + "&client=1" + "&time=" + System.currentTimeMillis()
                Glide.with(this@BindMobileActivity).load(path).into(this)
            }
            performClick()
        }
    }

    /**
     * @param v 被点击到的v;此时应该是该view自身:LineMenuView
     */
    override fun performSelf(v: LineMenuView) {
        routeCustom(ARouterConst.Activity_ChooseCountryActivity).navigation(this, Const.REQUEST_CODE_ONE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ONE) {
            data?.apply {
                lmv_country.briefText = getStringExtra(Const.KEY_COUNTRY_NAME)
                nationCode = getStringExtra(Const.KEY_NATION_CODE)
                tv_add_num.text = "+$nationCode"
            }
        }
    }

    /**
     * 验证手机号和图形验证码
     */
    private fun verifyPhone(): Boolean {
        return when {
            phone.isBlank() -> false.toast(getString(R.string.activity_bind_mobile_no_mobile))
            phone notMatch RegexConst.REGEX_PHONE -> false.toast(getString(R.string.activity_bind_mobile_error_mobile))
            imageCode.isBlank() -> false.toast(getString(R.string.activity_bind_mobile_picture_code))
            imageCode notMatch RegexConst.REGEX_RANDOM_TEXT_4 -> false.toast(getString(R.string.activity_bind_mobile_error_picture))
            else -> true
        }
    }
}