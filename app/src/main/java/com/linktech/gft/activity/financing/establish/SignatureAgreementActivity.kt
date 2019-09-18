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
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_signature_agreement.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 協議簽署(不经由接口,只是用于)
 *
 * 協議簽署 第10步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_SignatureAgreementActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "協議簽署")
@InjectLayoutRes(layoutResId = R.layout.activity_signature_agreement)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class SignatureAgreementActivity : BaseActivity<BasePresenter<SignatureAgreementActivity>>(), LineMenuListener {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

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
                routeCustom(ARouterConst.Activity_RiskDisclosureActivity)
                        .param(establishStatusBean)
                        .transitionToolbar(baseActivity)
                        .navigation(baseActivity)
                        .empty(comment = "风险披露")
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // cb
        cb_agree.setOnCheckedChangeListener { buttonView, isChecked ->
            onExistEditTextChanged()
        }

        // 回填
        establishBackFill()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        establishBackFill()
        onExistEditTextChanged()
    }

    /**
     * 开户状态回填
     */
    private fun establishBackFill() {
        establishStatusBean?.also {
            // 如果经过了协议界面的下一个界面(风险披露),则这里进来时设置协议已选中
            cb_agree.isChecked = it.stepFillOut >= 10
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            else -> true.empty(comment = "验证输入内容")
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
        arrayOf("open.html" to "開戶說明",
                "condition.html" to "一般性條款及條件",
                "cash.html" to "證券現金買賣協議",
                "margin.html" to "證券保證金買賣協議",
                "trade.html" to "電子交易協議",
                "risk.html" to "風險披露說明及風險聲明",
                "person.html" to "個人資料（隱私）條例",
                "custom.html" to "客戶聲明",
                "table.html" to "W-8BEN表格",
                "collperson.html" to "中華通北向交易個人資料收集聲明")[lmv.position means "不同协议内容"].let {
            "${BaseApplication.app.establishProtocolUrl}${it.first}" to it.second
        }.also {
            routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                    .param(it.second)
                    .firstParam(it.first)
                    .navigation()
                    .empty(comment = "协议界面")
        }
    }

    override fun onExistEditTextChanged() {
        super.onExistEditTextChanged()

        bt_common_function.isEnabled = cb_agree.isChecked
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_VerifyPasswordActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
