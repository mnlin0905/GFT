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
import kotlinx.android.synthetic.main.activity_financial_position.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 財務狀況
 *
 * 財務狀況 第7步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_FinancialPositionActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "財務狀況")
@InjectLayoutRes(layoutResId = R.layout.activity_financial_position)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class FinancialPositionActivity : BaseActivity<BasePresenter<FinancialPositionActivity>>() {
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
                mPresenter.saveFinancialSituation(_username, _signature, getPosition(rg_year_income), getPosition(rg_net_assets),
                        listOf(cb_risk_bearing_one, cb_risk_bearing_two, cb_risk_bearing_three, cb_risk_bearing_four).let {
                            it.indexOf(it.first { it.isChecked }) + 1
                        }, getPosition(rg_investment_objectives)) {
                    mPresenter.getByUserIdAccount(_username, _signature) {
                        routeCustom(ARouterConst.Activity_SelectAccountActivity)
                                .param(it)
                                .transitionToolbar(baseActivity)
                                .navigation(baseActivity)
                                .empty(comment = "選擇賬戶")
                    }
                }
            }
        }

        // rg
        listOf(rg_year_income, rg_net_assets, rg_investment_objectives).forEach { it.setOnCheckedChangeListener { group, checkedId ->
            onExistEditTextChanged()
        } }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 防止 多选冲突
        listOf(cb_risk_bearing_one, cb_risk_bearing_two, cb_risk_bearing_three, cb_risk_bearing_four).forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                // 切换为选中状态,才会让其他按钮恢复未选中(防止循环调用)
                if (isChecked) {
                    listOf(cb_risk_bearing_one, cb_risk_bearing_two, cb_risk_bearing_three, cb_risk_bearing_four).filter { it != buttonView }.forEach {
                        it.isChecked = false
                    }
                }
                onExistEditTextChanged()
            }
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
            rg_year_income.check(rg_year_income.getChildAt(it.householdIncomeType - 1)?.id
                    ?: View.NO_ID)
            rg_net_assets.check(rg_net_assets.getChildAt(it.householdNetWorthType - 1)?.id
                    ?: View.NO_ID)
            rg_investment_objectives.check(rg_investment_objectives.getChildAt(it.investmentObjectiveType - 1)?.id
                    ?: View.NO_ID)
            listOf(cb_risk_bearing_one, cb_risk_bearing_two, cb_risk_bearing_three, cb_risk_bearing_four).getOrNull(it.riskToleranceType - 1)?.isChecked = true
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

        bt_common_function.isEnabled = listOf(rg_year_income, rg_net_assets, rg_investment_objectives).all { it.checkedRadioButtonId != View.NO_ID }
                && listOf(cb_risk_bearing_one, cb_risk_bearing_two, cb_risk_bearing_three, cb_risk_bearing_four).any { it.isChecked }
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_InvestmentExperienceActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
