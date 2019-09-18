package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_investment_experience.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 投資經驗
 *
 * 投資經驗 第6步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_InvestmentExperienceActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "投資經驗")
@InjectLayoutRes(layoutResId = R.layout.activity_investment_experience)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class InvestmentExperienceActivity : BaseActivity<BasePresenter<InvestmentExperienceActivity>>() {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    /**
     * 设定变量(edit-text绑定)
     */
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
                // 获取选中的位置
                val getPosition: (RadioGroup) -> Int = {
                    it.indexOfChild(it.findViewById<RadioButton>(it.checkedRadioButtonId)) + 1
                }

                mPresenter.saveInvestmentExperience(_username, _signature, getPosition(rg_zq), getPosition(rg_jj), getPosition(rg_wh), getPosition(rg_gd), getPosition(rg_other)) {
                    mPresenter.getByUserIdAccount(_username, _signature) {
                        routeCustom(ARouterConst.Activity_FinancialPositionActivity)
                                .param(it)
                                .transitionToolbar(baseActivity)
                                .navigation(baseActivity)
                                .empty(comment = "财务状况")
                    }
                }
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // rg
        listOf(rg_zq, rg_jj, rg_wh, rg_gd, rg_other).forEach {
            it.setOnCheckedChangeListener { group, checkedId ->
                onExistEditTextChanged()
            }
        }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

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
            rg_zq.check(rg_zq.getChildAt(it.securitiesInvestmentExType - 1)?.id ?: View.NO_ID)
            rg_jj.check(rg_jj.getChildAt(it.fundInvestmentExType - 1)?.id ?: View.NO_ID)
            rg_wh.check(rg_wh.getChildAt(it.foreignInvestmentExType - 1)?.id ?: View.NO_ID)
            rg_gd.check(rg_gd.getChildAt(it.fixedInvestmentExType - 1)?.id ?: View.NO_ID)
            rg_other.check(rg_other.getChildAt(it.otherInvestmentExType - 1)?.id ?: View.NO_ID)
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
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

    override fun onExistEditTextChanged() {
        super.onExistEditTextChanged()

        bt_common_function.isEnabled = listOf(rg_zq, rg_jj, rg_wh, rg_gd, rg_other).all { it.checkedRadioButtonId != View.NO_ID }
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_AgreementDeclareActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
