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
import kotlinx.android.synthetic.main.activity_investment_experience.tv_phone
import kotlinx.android.synthetic.main.activity_select_account.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 選擇賬戶
 *
 * 選擇賬戶 第 8 步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_SelectAccountActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "選擇賬戶")
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
@InjectLayoutRes(layoutResId = R.layout.activity_select_account)
class SelectAccountActivity : BaseActivity<BasePresenter<SelectAccountActivity>>() {
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

                mPresenter.saveSelectAccount(_username, _signature, getPosition(rg_account_type),
                        listOf(cb_market_type_one, cb_market_type_two, cb_market_type_three).mapIndexed { index, b ->
                            if (b.isChecked) "${index + 1}" else null
                        }.filter { !it.isNullOrBlank() }
                                .joinToString()
                                .replace("\\s".toRegex(), "")
                                .replace(",,".toRegex(), ","),
                        getPosition(rg_other_product) - 1,
                        listOf(cb_one, cb_two, cb_three).mapIndexed { index, b ->
                            if (b.isChecked) "${index + 1}" else null
                        }.filter { !it.isNullOrBlank() }
                                .joinToString()
                                .replace("\\s".toRegex(), "")
                                .replace(",,".toRegex(), ",")
                ) {
                    mPresenter.getByUserIdAccount(_username, _signature) {
                        routeCustom(ARouterConst.Activity_VerifyPasswordActivity)
                                .param(it)
                                .transitionToolbar(baseActivity)
                                .navigation(baseActivity)
                                .empty(comment = "验证密码")
                    }
                }
            }
        }

        // 消失/隐藏
        rg_other_product.setOnCheckedChangeListener { group, checkedId ->
            ll_hide.visibility = if (checkedId == R.id.rb_other_product_yes) View.VISIBLE else View.GONE
        }

        // 状态切换
        listOf(rg_account_type, rg_other_product).forEach {
            it.setOnCheckedChangeListener { group, checkedId ->
                onExistEditTextChanged()
            }
        }
        listOf(cb_one, cb_two, cb_three, cb_market_type_one, cb_market_type_two, cb_market_type_three).forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                onExistEditTextChanged()
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

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
            rg_account_type.check(rg_account_type.getChildAt(it.openAccountType - 1)?.id
                    ?: View.NO_ID)
            listOf(cb_market_type_one, cb_market_type_two, cb_market_type_three).forEachIndexed { index, checkBox ->
                checkBox.isChecked = it.openMarketType?.contains("${index + 1}") ?: false
            }
            rg_other_product.check(rg_other_product.getChildAt(it.ifPromotionOfDerivatives)?.id
                    ?: View.NO_ID)
            listOf(cb_one, cb_two, cb_three).forEachIndexed { index, checkBox ->
                checkBox.isChecked = it.derivativesInvestmentExperience?.contains("${index + 1}")
                        ?: false
            }
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

        bt_common_function.isEnabled = listOf(rg_account_type, rg_other_product).all { it.checkedRadioButtonId != View.NO_ID }
                && listOf(cb_market_type_one, cb_market_type_two, cb_market_type_three).any { it.isChecked }
                && (
                rg_other_product.checkedRadioButtonId == R.id.rb_other_product_no
                        || (
                        rg_other_product.checkedRadioButtonId == R.id.rb_other_product_yes
                                && listOf(cb_one, cb_two, cb_three).any { it.isChecked }))
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_FinancialPositionActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
