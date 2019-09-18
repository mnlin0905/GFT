package com.linktech.gft.fragment.financing

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.google.gson.GsonBuilder
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.interfaces.SimulateDialogInterface
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.*
import com.linktech.gft.bean.DeputeResponse
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.bean.StockAccountInfo
import com.linktech.gft.interfaces.MyTabLayoutListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.encrypt
import kotlinx.android.synthetic.main.dialog_kill_order_alert.view.*
import kotlinx.android.synthetic.main.dialog_trade_detail.view.*
import kotlinx.android.synthetic.main.fragment_trade_detail.*
import kotlinx.android.synthetic.main.item_account_day_order.view.*
import kotlinx.android.synthetic.main.item_account_hold.view.*
import org.jetbrains.anko.textColorResource

/**
 * 交易模組
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_TradeDetailFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_trade_detail)
open class TradeDetailFragment : BaseFragment<BasePresenter<TradeDetailFragment>>() {
    /**
     * 當前狀態 0 --持倉  1--當日委託
     */
    private var currentType = 0

    /**
     * 切换/退出
     */
    lateinit var singleDialog: DefaultSimulateDialogImpl<LinearLayout, FrameLayout.LayoutParams>

    /**
     * 撤单
     */
    lateinit var cancelDialog: SimulateDialogInterface<CardView, FrameLayout.LayoutParams>

    private lateinit var dayOrderAdapter: BaseRecyclerViewAdapter<DeputeResponse.DeputeDetail>
    private lateinit var holdAdapter: BaseRecyclerViewAdapter<StockAccountInfo.Hold>

    init {
        //持仓适配器
        holdAdapter = BaseRecyclerViewAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.item_account_hold,
                listener = { _, position ->
                    holdAdapter.dataResources.forEachIndexed { index, deputeDetail ->
                        if (index == position) {
                            deputeDetail.isSELECTED_STATUS = !deputeDetail.isSELECTED_STATUS
                        } else {
                            deputeDetail.isSELECTED_STATUS = false
                        }
                    }
                    holdAdapter.notifyDataSetChanged()
                },
                childListeners = listOf(
                        R.id.tv_deal means "买卖" to { holder ->
                            dOnClick {
                                routeCustom(ARouterConst.Activity_DealActivity)
                                        .secondParam(holdAdapter.dataResources[holder.currentPosition].code)
                                        .navigation()
                            }
                        },
                        R.id.tv_hold_quote means "报价" to { holder ->
                            dOnClick {
                                routeCustom(ARouterConst.Activity_KLineActivity)
                                        .firstParam(OptionalListBean(securityCode = holdAdapter.dataResources[holder.currentPosition].code))
                                        .navigation()
                            }
                        }
                )
        ) {
            //股票名称
            tv_hold_stock_name.text = it.stock_info?.name
            //股票代码
            tv_hold_stock_code.text = "${it.code}.HK"
            //数量
            tv_hold_num.text = it.stkqty.toString()
            //可用
            tv_hold_usable.text = it.stkavl.toString()
            //现价
            tv_new_price.text = it.last_price.toScaleString(3)
            //成本
            tv_base_price.text = it.cost_price.toScaleString(3)
            //持仓盈亏
            tv_profit.text = it.rise_and_fall_value.toScaleString(2)
            //盈亏比例
            tv_profit_rate.text = it.rise_and_fall_rate.toString()
            //操作
            cl_hold.visibility = if (it.isSELECTED_STATUS) View.VISIBLE else View.GONE
        }

        //当日委托适配器
        dayOrderAdapter = BaseRecyclerViewAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.item_account_day_order,
                listener = { _, position ->
                    if (dayOrderAdapter.dataResources[position].isCanOperate() != 2) {
                        dayOrderAdapter.dataResources.forEachIndexed { index, deputeDetail ->
                            if (index == position) {
                                deputeDetail.isSELECTED_STATUS = !deputeDetail.isSELECTED_STATUS
                            } else {
                                deputeDetail.isSELECTED_STATUS = false
                            }
                        }
                        dayOrderAdapter.notifyDataSetChanged()
                    }
                },
                childListeners = listOf(
                        R.id.tv_operate_quote means "报价" to { holder ->
                            dOnClick {
                                routeCustom(ARouterConst.Activity_KLineActivity)
                                        .firstParam(OptionalListBean(securityCode = dayOrderAdapter.dataResources[holder.currentPosition].code))
                                        .navigation()
                            }
                        },
                        R.id.tv_operate_cancel means "撤单" to { holder ->
                            dOnClick {
                                includeDialog?.let {
                                    cancelDialog.generateView().apply {
                                        tv_cancel.dOnClick {
                                            it.closeDialogsOpened(instance = singleDialog).empty(comment = "取消")
                                        }
                                        tv_confirm.dOnClick {
                                            val request = BaseStockBean<Unit, CancleRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()),
                                                    request_data = CancleRequest(seqs = mutableListOf(OrderResponse(dayOrderAdapter.dataResources[holder.currentPosition].order_seq))))
                                            val json = GsonBuilder().disableHtmlEscaping().create().toJson(request)
                                            mPresenter.stockOrderCancel(encrypt(json, _trade_key)) {
                                                dayOrderAdapter.dataResources.removeAt(holder.currentPosition)
                                                dayOrderAdapter.notifyItemRemoved(holder.currentPosition)
                                            }
                                            it.closeDialogsOpened(instance = singleDialog)
                                        }
                                    }
                                    it.showDialogs(instance = cancelDialog, animator = AlphaIDVGAnimatorImpl())
                                }
                            }
                        }
                )
        ) {
            it.apply {
                //类型
                tv_type.text = if (dir == 1) "买入" else "卖出"
                tv_type.textColorResource = if (dir == 1) R.color.dark_color_197cce else R.color.dark_color_e9b529
                //状态
                tv_status.text = getStatusStr()
                tv_status.setCompoundDrawablesRelativeWithIntrinsicBounds(getStatusIcon(), 0, 0, 0)
                //股票名称
                tv_stock_name.text = name
                //代码
                tv_stock_code.text = "$code.HK"
                //数量
                tv_num.text = orderqty.toString()
                //订单价格
                tv_price.text = orderprice.toScaleString(3)
                //下单日期
                tv_date.text = getMonthDate(orderdatetime)
                //下单时间
                tv_time.text = ordertime
                //操作
                cl_order.visibility = if (isSELECTED_STATUS) View.VISIBLE else View.GONE
                tv_operate_cancel.visibility = if (isCanOperate() == 0) View.VISIBLE else View.GONE
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //修改toolbar高度
        toolbar.apply {
            title = getString(R.string.fragment_trade_detail_title)
            //添加切换与退出功能
            mTvTitle?.compoundDrawablePadding = dispatchGetDimen(R.dimen.view_padding_margin_4dp)
            mTvTitle?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_down, 0)
            singleDialog = generateDefaultDialog(includeDialog!!, R.layout.dialog_trade_detail)
            singleDialog.generateView().apply {
                tv_dialog_switch.dOnClick {
                    singleDialog.close()
                    route(ARouterConst.Activity_TradeActivity)
                }
                tv_dialog_exit.dOnClick {
                    singleDialog.close()
                    BaseApplication.isTradeLogin = false.empty(comment = "退出登錄")
                    routeCustom(ARouterConst.Activity_FinancingActivity)
                            .firstParam(2)
                            .navigation()
                            .toast(getString(R.string.activity_setting_exit_success))
                }
            }
            mTvTitle!!.dOnClick {
                singleDialog.generateView().setPadding(0, toolbar.height + BarUtils.getStatusBarHeight(), 0, 0)
                singleDialog.show()
            }
        }

        //tabLayout
        tl_tab.apply {
            addTab(newTab().setText(getString(R.string.fragment_trade_detail_hold_stock_, 0)))
            addTab(newTab().setText(getString(R.string.fragment_trade_detail_day_entrust_, 0)))
            addOnTabSelectedListener(object : MyTabLayoutListener() {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.run {
                        currentType = this.position
                        when (currentType) {
                            0 means "持倉" -> {
                                cl_hold_head.visibility = View.VISIBLE
                                rv_hold.visibility = View.VISIBLE
                                cl_entrust_head.visibility = View.GONE
                                rv_day_order.visibility = View.GONE
                            }
                            1 means "當日委託" -> {
                                cl_hold_head.visibility = View.GONE
                                rv_hold.visibility = View.GONE
                                cl_entrust_head.visibility = View.VISIBLE
                                rv_day_order.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            })
        }

        //券商类型
        tv_trade.text = tradeTypeStr
        tv_trade_account.text = _account

        includeDialog?.apply {
            cancelDialog = generateDefaultDialog(this, R.layout.dialog_kill_order_alert)
        }

        //跳转
        listOf(tv_deal, tv_new_stock, tv_into, tv_withdraw, tv_query).forEachIndexed { index, tv ->
            tv.dOnClick {
                when (index) {
                    0 means "交易" -> route(ARouterConst.Activity_DealActivity)
                    1 means "新股认购" -> route(ARouterConst.Activity_NewStockActivity)
                    2 means "入金" -> route(ARouterConst.Activity_GoldenActivity)
                    3 means "提现" -> route(ARouterConst.Activity_WithdrawActivity)
                    4 means "查询" -> routeCustom(ARouterConst.Activity_DealActivity).param(3).navigation()
                }
            }
        }
        //持倉列表
        rv_hold.run {
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = holdAdapter
        }
        //委托列表
        rv_day_order.run {
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = dayOrderAdapter
        }
    }

    private fun getData() {
        //獲取帳戶資訊和持仓
        val accountRequest = BaseStockBean<Unit, AccountRequest>(header = StockHeader(auth_code = _auth_code, system_time = 1562641943408), request_data = AccountRequest("1"))
        val accountJson = GsonBuilder().disableHtmlEscaping().create().toJson(accountRequest)
        mPresenter.getAccountInfo(encrypt(accountJson, _trade_key)) {
            it?.apply {
                assets.forEach {
                    if (it.currency == "T_HKD") {
                        updateAsset(it)
                        return@forEach
                    }
                }
                //持仓
                holdAdapter.dataResources.clear()
                holdAdapter.dataResources.addAll(positions)
                holdAdapter.notifyDataSetChanged()
                tl_tab.getTabAt(0)?.text = getString(R.string.fragment_trade_detail_hold_stock_, positions.size)
            }
        }

        //查當日委託
        val deputeRequest = BaseStockBean<Unit, DeputeRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()),
                request_data = DeputeRequest(type = "0"))
        val deputeJson = GsonBuilder().disableHtmlEscaping().create().toJson(deputeRequest)
        mPresenter.getDepute(encrypt(deputeJson, _trade_key)) {
            it?.apply {
                dayOrderAdapter.dataResources.clear()
                dayOrderAdapter.dataResources.addAll(details)
                dayOrderAdapter.notifyDataSetChanged()
                tl_tab.getTabAt(1)?.text = getString(R.string.fragment_trade_detail_day_entrust_, details.size)
            }
        }
    }

    /**
     * 更新账户信息
     */
    private fun updateAsset(asset: StockAccountInfo.Asset?) {
        asset?.apply {
            //總資產
            fmtMicrometer(total_assets.toString())
            tv_asset.text = SpannableString(fmtMicrometer(total_assets.toString())).also {
                it.setSpan(AbsoluteSizeSpan(dispatchGetDimen(R.dimen.text_size_small_12sp)), it.length - 2, it.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            //今日盈虧
            tv_today_profit.text = fmtMicrometer(daily_value.toString())
            //盈虧比例
            tv_today_rate.text = daily_rate
            //證券市值
            tv_stock_money.text = fmtMicrometer(stock_assets.toString())
            //最大購買力
            tv_top_power.text = fmtMicrometer(usable.toString())
            //可用資金
            tv_usable_money.text = fmtMicrometer(withdraw.toString())
            //ipo凍結
            tv_ipo_freeze.text = fmtMicrometer(ipo_frozen.toString())
            //委託凍結
            tv_entrust_freeze.text = fmtMicrometer(depute_frozen.toString())
            //可提現資金
            tv_withdraw_money.text = fmtMicrometer(advisable_funds.toString())
        }
    }

    /**
     * 網路數據提取
     */
    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)
        getData()
    }
}
