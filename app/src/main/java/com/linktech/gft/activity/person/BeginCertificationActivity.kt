package com.linktech.gft.activity.person

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.UploadCerImgBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.Const.KEY_PICTURE_MULTIPLE
import com.linktech.gft.util.Const.KEY_PICTURE_NEGATIVE
import com.linktech.gft.util.Const.KEY_PICTURE_POSITIVE
import com.linktech.gft.util.Const.REQUEST_CODE_ONE
import com.linktech.gft.util.Const.REQUEST_CODE_TWO
import com.linktech.gft.util.Const.VALUE_POSITION_NULL
import com.linktech.gft.util.RegexConst
import com.linktech.gft.util.SmsConst
import com.linktech.gft.window.CardTypePickerViewDialog
import kotlinx.android.synthetic.main.activity_begin_certification.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*


/**
 * function---- BeginCertificationActivity
 *
 *
 * Created(Gradle default create) by ACChain on 2018/01/17 03:18:45 (+0000).
 */
@Route(path = ARouterConst.Activity_BeginCertificationActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_begin_certification)
@InjectActivityTitle(titleRes = R.string.label_begin_certification)
class BeginCertificationActivity : BaseActivity<BasePresenter<BeginCertificationActivity>>(), LineMenuListener {

    /**
     * 实名认证信息
     */
    private var name: String by viewBind(R.id.et_name)
    private var cardNumber: String by viewBind(R.id.et_card_number)
    private var phone: String by viewBind(R.id.et_phone)
    private var verifyCode: String by viewBind(R.id.et_verify_code)
    /**
     * 存储的图片位置
     * 如果都有值时,表示上传图片成功
     */
    private var picturePositive: String? = null
    private var pictureNegative: String? = null
    private var pictureMultiple: String? = null
    private var resultBean: List<UploadCerImgBean>? = null

    /**
     * 国家/地区
     */
    private var country: String? = null
    /**
     * 身份验证类型
     */
    private var cardType = VALUE_POSITION_NULL

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        country = getString(R.string.activity_bind_mobile_china)
        lmv_location.briefText = country

        //实名认证逻辑
        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick(::verifyTwo) {
                //上传前清空图片
                resultBean = null
                mPresenter.appUploadImg(_username, _signature, picturePositive, pictureNegative, pictureMultiple) {
                    resultBean = it?.also {
                        mPresenter.beginAuth(_username, _signature, country, name, cardNumber, cardType, it.getOrNull(0)?.path, it.getOrNull(1)?.path, it.getOrNull(2)?.path, phone, verifyCode) {
                            mPresenter.getMemberInfo(_signature, _username) {
                                showToast(getString(R.string.activity_begin_certification_success))
                                finish()
                            }
                        }
                    } ?: it.toast(R.string.activity_begin_certification_upload_fail)
                }
            }
        }

        //当当前账号是手机号登陆的状态下，实名认证这里的手机号应该是不可输入直接在后台取出的
        if (_status_login && !_phone.isNullOrBlank()) {
            phone = _phone!!
            et_phone.isEnabled = false
        }

        et_name.addFaceFilter()

        //验证码
        rctv_random_code.dOnClick(this::verifyOne) {
            mPresenter.sendInformation(phone, SmsConst.TAG_VERIFICATION) {
                rctv_random_code.startCount()
            }
        }
    }

    override fun performSelf(v: LineMenuView) {
        when (v.getTag(LMVConfigs.TAG_POSITION) as Int) {
            0 means "选择国家地区" -> {
                hideSoft()
                routeCustom(ARouterConst.Activity_ChooseCountryActivity).navigation(this, Const.REQUEST_CODE_TWO)
            }
            1 means "选择证件类型" -> {
                hideSoft()
                if (!CardTypePickerViewDialog
                                .getInstance(this)
                                .setOnPickerViewListener { position, type ->
                                    cardType = position + 1
                                    lmv_card_type.briefText = type
                                }
                                .show()) {
                    toast(R.string.activity_begin_certification_cannot_select_card)
                }
            }
            2 means "选择证件照片" ->
                routeCustom(ARouterConst.Activity_UploadCertificationPhotoActivity)
                        .withString(KEY_PICTURE_POSITIVE, picturePositive)
                        .withString(KEY_PICTURE_NEGATIVE, pictureNegative)
                        .withString(KEY_PICTURE_MULTIPLE, pictureMultiple)
                        .navigation(this, REQUEST_CODE_ONE)
        }
    }

    /**
     *  隐藏软键盘
     */
    private fun hideSoft() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_card_number.windowToken, 0)
    }

    /**
     * 获取验证码前的验证
     */
    private fun verifyOne(): Boolean {
        return when {
            country.isNullOrBlank() -> false.toast(getString(R.string.activity_begin_certification_toast_country))
            name.isBlank() -> false.toast(getString(R.string.activity_begin_certification_toast_name))
            name notMatch RegexConst.REGEX_VERITY_NAME -> false.toast(getString(R.string.activity_begin_certification_toast_name_error))
            cardType == VALUE_POSITION_NULL -> false.toast(getString(R.string.activity_begin_certification_toast_card_type))
            cardNumber.isBlank() -> false.toast(getString(R.string.activity_begin_certification_toast_card_number))
            // cardType == TYPE_ID_CARD_IDENTIFICATION && cardNumber notMatch RegexConst.REGEX_ID_CARD_NUMBER -> false.toast(getString(R.string.activity_begin_certification_toast_id_error))
            // cardType == TYPE_ID_CARD_FOREIGN && cardNumber notMatch RegexConst.REGEX_FOREIGN_CARD_NUMBER -> false.toast(getString(R.string.activity_begin_certification_toast_hz_error))
            // cardType == TYPE_ID_CARD_OTHER && cardNumber notMatch RegexConst.REGEX_POLICE_CARD_NUMBER -> false.toast(getString(R.string.activity_begin_certification_toast_jg_error))
            pictureNegative.isNullOrBlank() || picturePositive.isNullOrBlank() -> false.toast(getString(R.string.activity_begin_certification_toast_no_card))
            phone.isBlank() -> false.toast(getString(R.string.activity_begin_certification_toast_phone))
            phone notMatch RegexConst.REGEX_PHONE -> false.toast(getString(R.string.activity_begin_certification_toast_phone_error))
            else -> true
        }
    }

    /**
     * 提交认证前的验证
     */
    private fun verifyTwo(): Boolean {
        return when {
            !verifyOne() -> false
            verifyCode.isBlank() -> false.toast(dispatchGetString(R.string.activity_begin_certification_toast_verify_code))
            verifyCode notMatch RegexConst.REGEX_RANDOM_NUMBER_6 -> false.toast(dispatchGetString(R.string.activity_begin_certification_toast_verify_code_error))
            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ONE means "证件照" -> data?.apply {
                    picturePositive = getStringExtra(KEY_PICTURE_POSITIVE)
                    pictureNegative = getStringExtra(KEY_PICTURE_NEGATIVE)
                    pictureMultiple = getStringExtra(KEY_PICTURE_MULTIPLE)
                    lmv_upload_photo.briefText = getString(R.string.activity_begin_certification_toast_has_select_card)
                }
                REQUEST_CODE_TWO means "选择国家" -> data?.apply {
                    country = getStringExtra(Const.KEY_COUNTRY_NAME)
                    lmv_location.briefText = country
                }
            }
        }
    }
}