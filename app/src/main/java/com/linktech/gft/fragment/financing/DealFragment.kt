package com.linktech.gft.fragment.financing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.GsonBuilder
import com.linktech.gft.R
import com.linktech.gft.activity.financing.common.DealActivity
import com.linktech.gft.base.*
import com.linktech.gft.bean.*
import com.linktech.gft.interfaces.MyTextWatch
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.encrypt
import com.linktech.gft.window.ConfirmOrderDialogFragment
import com.linktech.gft.window.PriceTypeDialogFragment
import com.linktech.gft.ws.BaseWSListener
import com.linktech.gft.ws.WSManager
import kotlinx.android.synthetic.main.dark_layout_top_bar.*
import kotlinx.android.synthetic.main.fragment_deal.*
import kotlinx.android.synthetic.main.item_account_hold.view.*
import kotlinx.android.synthetic.main.item_handicap.view.*
import org.jetbrains.anko.backgroundColor
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * 買入|賣出
 *
 */
@Route(path = ARouterConst.Fragment_DealFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_deal)
open class DealFragment : BaseFragmentToolbar<BasePresenter<DealFragment>>() {
    /**
     * 設置默認的選中位置  0 --買入  1--賣出
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var defaultPosition: Int = 0

    private var currentStock: OptionalListBean? = null

    private lateinit var activity: DealActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as DealActivity
    }

    private var price: String by viewBind(R.id.et_price)
    private var num: String by viewBind(R.id.et_num)
    private var orderMoney: String by viewBind(R.id.tv_order_money)
    private var unitPrice: Double = 0.000
    private var unitCount: Int = 0
    private var usableBalance: Double = 0.00
    private var usableCount: Int = 0

    /**
     * 交易類型
     */
    private var tradeTypes: MutableList<TradeTypeResponse.Type> = mutableListOf()
    private var priceType: String? = null

    //持仓数据
    private var holdAdapter: BaseRecyclerViewAdapter<StockAccountInfo.Hold>
    /**
     * 买卖盘适配器
     */
    private var buyAdapter: BaseRecyclerViewPositionAdapter<WSBuySellDeepBean.BuyBean>
    private var sellAdapter: BaseRecyclerViewPositionAdapter<WSBuySellDeepBean.SellBean>

    init {
        buyAdapter = BaseRecyclerViewPositionAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.item_handicap
        ) { bean, position ->
            tv_sort.text = "买${position + 1}"
            tv_price.text = if (bean.price != 0L) bean.price.stockGetValue() else "--"
            tv_price.setTextColor(dispatchGetColor(R.color.dark_color_f85d5a))
            tv_count.text = if (bean.numberOfOrders != 0L) bean.numberOfOrders.toString() else "--"
        }
        buyAdapter.dataResources.addAll(listOf(WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean()))

        sellAdapter = BaseRecyclerViewPositionAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.item_handicap
        ) { bean, position ->
            tv_sort.text = "卖${position + 1}"
            tv_price.text = if (bean.price != 0L) bean.price.stockGetValue() else "--"
            tv_price.setTextColor(dispatchGetColor(R.color.dark_color_20bf7c))
            tv_count.text = if (bean.numberOfOrders != 0L) bean.numberOfOrders.toString() else "--"
        }
        sellAdapter.dataResources.addAll(listOf(WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean()))

        //持仓适配器
        holdAdapter = BaseRecyclerViewAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.item_account_hold
        ) {
            //股票名称
            tv_hold_stock_name.text = it.name
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
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //退出
        toolbar.setNavigationOnClickListener {
            baseActivity.onBackPressed()
        }

        //初始化買入|賣出介面
        changeType()

        // 盤口數據
        rv_sale.run {
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, true)
            adapter = sellAdapter
        }

        rv_buy.run {
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = buyAdapter
        }
        //持仓
        rv_order.run {
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = holdAdapter
        }

        // 限價 |市價
        tv_price_type.dOnClick {
            if (tradeTypes.size > 0) {
                PriceTypeDialogFragment()
                        .setTradeType(tradeTypes)
                        .setOnChangePriceListener(object : PriceTypeDialogFragment.OnChangePriceListener {
                            override fun onClickPrice(position: Int): Boolean {
                                tv_price_type.text = tradeTypes[position].value
                                priceType = tradeTypes[position].key
                                return false
                            }
                        }).show(sfManager, "price")
            }
        }

        // 加減
        iv_price_add.dOnClick { addOrSubtractPrice(isAdd = true, isPrice = true) }
        iv_price_subtract.dOnClick { addOrSubtractPrice(isAdd = false, isPrice = true) }
        iv_num_add.dOnClick { addOrSubtractPrice(isAdd = true, isPrice = false) }
        iv_num_subtract.dOnClick { addOrSubtractPrice(isAdd = false, isPrice = false) }

        //買入，賣出
        bt_commit.dOnClick(::verify) {
            ConfirmOrderDialogFragment()
                    .setType(defaultPosition)
                    .setStockName(currentStock!!.getShareNameI() ?: "")
                    .setStockCode(currentStock!!.getShareCodeI() ?: "")
                    .setPrice(price)
                    .setNum(num)
                    .setOnChooseListener(object : ConfirmOrderDialogFragment.OnChooseListener {
                        override fun onRightClick() {
                            commitOrder()
                        }
                    }).show(fragmentManager, "confirm")
        }

        //搜索股票
        tv_stock_num.dOnClick {
            routeCustom(ARouterConst.Activity_AddSharesActivity)
                    .firstParam(1)
                    .navigation(baseActivity, Const.REQUEST_CODE_ONE)

        }

        //获取账户资金
        getAccount()

        //监听价格和数量变动
        listOf(et_price, et_num).forEach {
            it.addTextChangedListener(object : MyTextWatch() {
                override fun afterTextChanged(s: Editable?) {
                    val d = (price.toDoubleOrNull() ?: 0.0) * (num.toDoubleOrNull() ?: 0.0)
                    orderMoney = d.toScaleString(2)
                    parseUsable()
                }
            })
        }

        //添加ws 监听价格动态变化
        WSManager.addListener(BaseWSListener<WSNormalPriceBean>(WSNormalPriceBean::class.java, {
            currentStock?.realTimePrice = it.nominalPrice
        }, {
            currentStock?.getShareCodeI() == it.securityCode
        }).also {
            WSManager.sendText(WSControlPush(subscribe = true,msgType = WSNormalPriceBean::class.java,code = currentStock?.getShareCodeI()))
        })

        //初始化界面数据
        currentStock = activity.getCurrentStock()
        val stockCode = activity.getStockCode()
        when {
            currentStock == null && stockCode != null means "用code请求股票信息" ->
                mPresenter.getByIdOptional(_username, _signature, null, stockCode) {
                    currentStock = it
                    refreshData()
                }
            else -> refreshData().empty("不管有没有股票信息，初始化一下界面")
        }
    }

    /**
     * 选择股票回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.REQUEST_CODE_ONE && resultCode == Activity.RESULT_OK) {
            data?.apply {
                currentStock = getSerializableExtra(Const.KEY_COMMON_VALUE) as OptionalListBean?
                refreshData()
            }
        }
    }

    /**
     * 获取账户资金
     */
    private fun getAccount() {
        //獲取帳戶和持仓
        val accountRequest = BaseStockBean<Unit, AccountRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()), request_data = AccountRequest("1"))
        val accountJson = GsonBuilder().disableHtmlEscaping().create().toJson(accountRequest)
        mPresenter.getAccountInfo(encrypt(accountJson, _trade_key)) {
            it?.apply {
                //资产
                assets[0].apply {
                    //可用資金
                    usableBalance = withdraw
                    tv_usable_balance.text = fmtMicrometer(withdraw.toString())
                    //最大購買力
                    tv_max_power.text = fmtMicrometer(usable.toString())
                }
                //持仓
                holdAdapter.dataResources.clear()
                holdAdapter.dataResources.addAll(positions)
                holdAdapter.notifyDataSetChanged()
                //计算可用数量
                parseUsable()
            }
        }
    }

    /**
     * 初始化
     */
    private fun initView() {
        currentStock?.apply {
            //股票代码
            tv_stock_num.text = "${getShareCodeI()}.HK"
            //股票名字
            tv_stock_name.text = getShareNameI()
            //当前价格
            tv_current_price.text = getCurrentPrice()
            //涨跌幅
            tv_current_rate.text = "${priceLimit}%"
            //价格
            et_price.setText(getCurrentPrice())
            //最小变动价格
            unitPrice = getUintPrice(spreadTableCode, getPrice())
            tv_unit_price.text = "${unitPrice.toScaleString(3)}港元"
            //数量
            num = ""
            //最小变动数量
            unitCount = lotSize
            tv_unit_count.text = lotSize.toString()
            //输入过滤
            et_price.addInputFilterDigits(3)
        }
        parseUsable()
    }

    /**
     * 刷新交易类型，买卖盘数据
     */
    private fun refreshData() {
        initView()
        currentStock?.apply {
            //獲取交易類型
            val baseStockBean = BaseStockBean<Unit, TradeTypeRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()), request_data = TradeTypeRequest(currencyCode))
            val jsonStr = GsonBuilder().disableHtmlEscaping().create().toJson(baseStockBean)
            mPresenter.getTradeType(encrypt(jsonStr, _trade_key)) {
                it?.apply {
                    onGetType(this.trade_types)
                }
            }
        }
        val handData = { bean: WSBuySellDeepBean? ->
            (bean ?: WSBuySellDeepBean()).let {
                for (index in 0..4) {
                    buyAdapter.dataResources.set(index, it.bidList.getOrNull(index)
                            ?: WSBuySellDeepBean.BuyBean())
                    sellAdapter.dataResources.set(index, it.offerList.getOrNull(index)
                            ?: WSBuySellDeepBean.SellBean())
                }
                buyAdapter.notifyDataSetChanged()
                sellAdapter.notifyDataSetChanged()
            }
        }
        //获取买卖盘数据
        currentStock?.apply {
            mPresenter.buySellOrderList(getShareCodeI()) {
                handData(it)
            }
        } ?: handData(null)
    }

    /**
     * 验证下单
     */
    private fun verify(): Boolean {
        return when {
            currentStock == null -> false.toast("请选择股票")
            price.isEmpty() -> false.toast("请输入价格")
            !isMultiple(price.toDouble(), unitPrice) -> false.toast("价格不符合")
            num.isEmpty() -> false.toast("请输入数量")
            num.toInt() % unitCount != 0 -> false.toast("数量不符合")
            num.toInt() > usableCount -> false.toast("可用数量不足")
            else -> true
        }
    }

    private fun isMultiple(d1: Double, d2: Double): Boolean {
        val d = (BigDecimal(d1) / BigDecimal(d2)).setScale(3, BigDecimal.ROUND_HALF_DOWN)
        return d.toInt().toDouble() == d.toDouble()
    }

    /**
     * 下單
     */
    private fun commitOrder() {
        val baseStockBean = BaseStockBean<Unit, OrderRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()),
                request_data = OrderRequest(code = currentStock!!.getShareCodeI(), type = priceType, dir = if (defaultPosition == 0) 1 else -1, price = price, qty = num))
        val jsonStr = GsonBuilder().disableHtmlEscaping().create().toJson(baseStockBean)
        mPresenter.stockOrder(encrypt(jsonStr, _trade_key)) {
            toast("下单成功")
            getAccount()
            price = currentStock!!.getCurrentPrice()
            num = ""
        }
    }

    /**
     * 计算可买和可卖数量
     */
    private fun parseUsable() {
        currentStock?.apply {
            when (defaultPosition) {
                0 means ("计算可买") -> {
                    if (price.toDoubleOrNull() ?: 0.0 != 0.0) {
                        usableCount = (usableBalance / (price.toDoubleOrNull() ?: 0.0)).toInt()
                        tv_usable_count.text = usableCount.toString()
                    } else
                        tv_usable_count.text = "--"
                }
                1 means ("计算可卖") -> {
                    usableCount = 0
                    holdAdapter.dataResources.forEach {
                        if (it.code == this.getShareCodeI()) {
                            usableCount = it.stkavl
                            return@forEach
                        }
                    }
                    tv_usable_count.text = usableCount.toString()
                }
            }
        }
    }

    /**
     *  處理價格加減，數量加減
     */
    private fun addOrSubtractPrice(isAdd: Boolean, isPrice: Boolean) {
        val df = DecimalFormat("0.000")
        when {
            isAdd && isPrice means "價格增加" -> price = BigDecimal(df.format(price.toDoubleOrNull()
                    ?: 0.0)).add(BigDecimal(df.format(unitPrice))).toString()
            !isAdd && isPrice means "價格減少" -> {
                val p = BigDecimal(df.format(price.toDoubleOrNull()
                        ?: 0.0)).subtract(BigDecimal(df.format(unitPrice)))
                if (p >= BigDecimal(0))
                    price = p.toString()
            }
            isAdd && !isPrice means "數量增加" -> num = BigDecimal(num.toIntOrNull()
                    ?: 0).add(BigDecimal(unitCount)).toString()
            !isAdd && !isPrice means "數量減少" -> {
                val n = BigDecimal(num.toIntOrNull() ?: 0).subtract(BigDecimal(unitCount))
                if (n >= BigDecimal(0))
                    num = n.toString()
            }
        }
    }

    /**
     * 初始化買入|賣出介面
     */
    private fun changeType() {
        when (defaultPosition) {
            0 means "買入" -> {
                toolbar.title = getString(R.string.fragment_deal_buy_in)
                bt_commit.text = getString(R.string.fragment_deal_buy_in)
                bt_commit.backgroundColor = dispatchGetColor(R.color.dark_color_f85d5a)
                et_price.hint = getString(R.string.fragment_deal_buy_hint)
                tv_deal_type.text = getString(R.string.fragment_deal_can_buy)
                cl_account.visibility = View.VISIBLE
            }
            1 means "卖出" -> {
                toolbar.title = getString(R.string.fragment_deal_sell_out)
                bt_commit.text = getString(R.string.fragment_deal_sell_out)
                bt_commit.backgroundColor = dispatchGetColor(R.color.dark_color_20bf7c)
                et_price.hint = getString(R.string.fragment_deal_sell_hint)
                tv_deal_type.text = getString(R.string.fragment_deal_can_sell)
                cl_account.visibility = View.GONE
            }
        }
    }

    /**
     * 拿到交易類型,給activity調用
     */
    private fun onGetType(tradeTypes: MutableList<TradeTypeResponse.Type>) {
        this.tradeTypes = tradeTypes
        tv_price_type.apply {
            text = tradeTypes.getOrNull(0)?.value
            priceType = tradeTypes.getOrNull(0)?.key
            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (tradeTypes.size > 1) R.drawable.dark_icon_arrow_down else 0, 0)
        }
    }


    /**
     * 網路數據提取
     */
    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)
        defaultPosition = obj as Int
        changeType()
        parseUsable()
    }

    override fun onDestroy() {
        super.onDestroy()
        WSManager.removeListener(WSNormalPriceBean::class.java)
    }
}
