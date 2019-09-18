package com.linktech.gft.activity.financing.person


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.StockWarn
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_stock_warn.*
import kotlinx.android.synthetic.main.dialog_set_stock_warn.view.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import org.jetbrains.anko.image

/**
 * function---- ForgetPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_StockWarnActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_stock_warn)
@InjectActivityTitle(titleRes = R.string.label_stock_warn)
class StockWarnActivity : BaseActivity<BasePresenter<StockWarnActivity>>(), LineMenuListener {
    /**
     * 成員變數
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = true)
    @JvmField
    var stockWarn: StockWarn? = null

    /**
     * 設置成功提醒框
     */
    lateinit var editSuccessDialog: DefaultSimulateDialogImpl<CardView, FrameLayout.LayoutParams>

    private var remindType = 0

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        stockWarn?.let {
            // 港股，美股
            iv_type.image = dispatchGetDrawable(if (it.stockType == 0) R.drawable.dark_icon_market_hk else R.drawable.dark_icon_market_us)
            // 名稱
            tv_stock_name.text = it.stockName
            //代碼
            tv_stock_num.text = it.stockNum
            //當前價
            tv_current_price.text = it.currentPrice.toString()
            tv_current_price.setTextColor(dispatchGetColor(if (it.currentRate!!.contains("-")) R.color.dark_color_20bf7c else R.color.dark_color_f85d5a))
            //當前漲跌幅度
            tv_current_rate.text = it.currentRate
            tv_current_rate.setTextColor(dispatchGetColor(if (it.currentRate!!.contains("-")) R.color.dark_color_20bf7c else R.color.dark_color_f85d5a))
            // 港元,美元
            tv_top_money_unit.text = (if (it.stockType == 0) getString(R.string.activity_stock_warn_hk_money) else getString(R.string.activity_stock_warn_us_money))
            tv_lower_money_unit.text = (if (it.stockType == 0) getString(R.string.activity_stock_warn_hk_money) else getString(R.string.activity_stock_warn_us_money))
        }

        setFrequency(remindType)

        //初始化設置提醒框
        includeDialog?.also {
            editSuccessDialog = generateDefaultDialog(it, R.layout.dialog_set_stock_warn)
            editSuccessDialog.generateView().apply {
                //股票資訊
                iv_shares_icon.setImageResource(R.drawable.dark_icon_market_hk)
                tv_shares_name.text = "騰訊控股"
                tv_shares_code.text = "00700"

                //漲跌限制
                tv_price_max.text = "380.00"
                tv_price_min.text = "30.00"
                tv_up_limit.text = "20%"
                tv_down_limit.text = "5%"

                //確定
                tv_confirm.dOnClick {
                    editSuccessDialog.close()
                    toast(getString(R.string.activity_stock_warn_success))
                }
            }

            bt_common_function.apply {
                text = getString(R.string.function_submit)
                dOnClick {
                    editSuccessDialog.show(AlphaIDVGAnimatorImpl())
                    toast(R.string.common_not_develop)
                }
            }
        }
    }

    override fun performSelf(lmv: LineMenuView) {
        routeCustom(ARouterConst.Activity_RemindFrequencyActivity)
                .param(remindType)
                .navigation(this@StockWarnActivity, Const.REQUEST_CODE_ONE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ONE) {
            data?.apply {
                setFrequency(getIntExtra(Const.KEY_COMMON_VALUE, 0))
            }
        }
    }

    private fun setFrequency(type: Int) {
        remindType = type
        lmv_frequency.briefText = when (type) {
            0 -> getString(R.string.activity_remind_frequency_onece)
            1 -> getString(R.string.activity_remind_frequency_day_one)
            2 -> getString(R.string.activity_remind_frequency_always)
            else -> TODO()
        }
    }
}
