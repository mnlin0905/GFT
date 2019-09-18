package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.RegexConst
import kotlinx.android.synthetic.main.activity_verify_password.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 验证 密碼設置 是否正确
 *
 * 密碼設置 第9步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_VerifyPasswordActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "密碼設置")
@InjectLayoutRes(layoutResId = R.layout.activity_verify_password)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class VerifyPasswordActivity : BaseActivity<BasePresenter<VerifyPasswordActivity>>(), LineMenuListener {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    /**
     * 设定变量(edit-text绑定)
     */
    private var passwordOne: String by viewBind(R.id.wiet_password_one)
    private var passwordTwo: String by viewBind(R.id.wiet_password_two)
    private var phone: String by viewBind(R.id.tv_phone)

    /**
     * singleDialog
     */
    private lateinit var singleDialog: DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams>

    /**
     * 初始化數據(可默認不實現)
     */
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 直接下一步,无验证消息
        bt_common_function.apply {
            text = getString(R.string.function_next_step)
            dOnClick(::verify) {
                mPresenter.savePassword(_username, _signature, passwordOne.toMD5(), passwordTwo.toMD5()) {
                    mPresenter.getByUserIdAccount(_username, _signature) {
                        routeCustom(ARouterConst.Activity_SignatureAgreementActivity)
                                .param(it)
                                .transitionToolbar(baseActivity)
                                .navigation(baseActivity)
                                .empty(comment = "验证密码")
                    }
                }
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            passwordOne != passwordTwo -> false.toast("两次输入的密码不同")
            passwordOne notMatch RegexConst.REGEX_PASSWORD -> false.toast("密码格式不符")
            else -> true.empty(TODO = "验证输入内容")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        setDefaultToolbarMenuText(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        singleDialog.show(AlphaIDVGAnimatorImpl())
        return true
    }

    override fun performSelf(lmv: LineMenuView) {
        empty(TODO = "")
        when (lmv.position) {

        }
    }

    override fun onExistEditTextChanged() {
        bt_common_function.isEnabled = listOf(passwordOne, passwordTwo).all { it.isNotBlank() }
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_SelectAccountActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
