package com.linktech.gft.activity.financing.person


import android.os.Bundle
import android.text.Editable
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.RegexConst
import com.linktech.gft.view.WatchInputEditText
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function---- FeedbackActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/22 05:48:48 (+0000).
 */
@Route(path = ARouterConst.Activity_FeedbackActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_feedback)
@InjectActivityTitle(titleRes = R.string.label_feedback)
class FeedbackActivity : BaseActivity<BasePresenter<FeedbackActivity>>() {
    /**
     * 反馈意见
     * 手机号
     */
    private var remark: String by viewBind(R.id.wiet_remark)
    private var phone: String by viewBind(R.id.wiet_phone)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //反馈
        bt_common_function.run {
            text = getString(R.string.function_submit)
            dOnClick({
                when {
                    phone.isNotBlank() && phone notMatch RegexConst.REGEX_PHONE -> false.toast(getString(R.string.activity_feedback_phone_error))
                    else -> true
                }
            }) {
                mPresenter.addFeedBack(_username, _signature, remark, phone) {
                    finish().toast(getString(R.string.activity_feedback_success))
                }
            }
        }
    }

    override fun onExistEditTextChanged(editText: WatchInputEditText, s: Editable) {
        super.onExistEditTextChanged(editText, s)

        bt_common_function.isEnabled = remark.isNotBlank()

        //已输入文字
        tv_length.text = "${500 - remark.length}"
    }
}