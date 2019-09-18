package com.linktech.gft.activity.financing.market


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.tifezh.kchartlib.chart.beans.MinuteLineEntity
import com.github.tifezh.kchartlib.chart.entity.IMinuteLine
import com.github.tifezh.kchartlib.chart.formatter.DateFormatter
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.interfaces.SimulateDialogInterface
import com.linktech.gft.R
import com.linktech.gft.adapter.KChartAdapter
import com.linktech.gft.base.*
import com.linktech.gft.bean.*
import com.linktech.gft.interfaces.MyTabSelectedListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DataHelper
import com.linktech.gft.util.ScreenUtils
import com.linktech.gft.view.TabPagerCombineLayout
import com.linktech.gft.view.tablayout.CustomTabLayout
import com.linktech.gft.ws.BaseWSListener
import com.linktech.gft.ws.WSManager
import kotlinx.android.synthetic.main.activity_k_line.*
import kotlinx.android.synthetic.main.dialog_k_line_detail.view.*
import kotlinx.android.synthetic.main.layout_item_k_line.view.*
import org.jetbrains.anko.matchParent
import java.text.SimpleDateFormat
import java.util.*

/**
 * function---- ForgetPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_KLineActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_k_line)
@InjectActivityTitle(title = "")
@SuppressLint("SetTextI18n")
class KLineActivity : BaseActivity<BasePresenter<KLineActivity>>(), TabPagerCombineLayout.onTabPagerListener {
    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var currentStock: OptionalListBean = OptionalListBean()

    /**
     * 最新的分时数据信息
     */
    var lastMinHourBean: WSStockMinHourBean? = null
        set(value) {
            field = value
            refreshMarketStatus()
        }

    /**
     * dialog
     */
    private lateinit var firstDialog: SimulateDialogInterface<CardView, FrameLayout.LayoutParams>

    /**
     * 新聞碎片
     */
    private var fragments: MutableList<BaseFragment<*>> = ArrayList()

    /**
     * K-line数据源
     */
    private var klineDatas: MutableList<List<KLineEntity>?> = mutableListOf(null, null, null, null)

    /**
     * k線庫適配器
     */
    private var singleChartAdapter: KChartAdapter = KChartAdapter()

    /**
     * 分时图/数据源
     */
    private var minHourDatas: MutableList<IMinuteLine> = mutableListOf()

    /**
     * 买卖盘适配器
     */
    private lateinit var buyAdapter: BaseRecyclerViewAdapter<WSBuySellDeepBean.BuyBean>
    private lateinit var sellAdapter: BaseRecyclerViewAdapter<WSBuySellDeepBean.SellBean>
    /**
     * WS监听器:
     *
     * 1.买卖盘监听
     * 2.分时数据监听
     */
    private var wsListeners: MutableList<BaseWSListener<*>> = mutableListOf()

    /**
     * 昨收价格
     */
    var yesClosePrice: Double? = .0

    /**
     * 市场状态 更改时需要顶部信息发生变化
     */
    var marketStatus: WSMarketStatus? = null
        set(value) {
            field = value
            refreshMarketStatus()
        }

    /**
     * 证券状态 : 更改时需要顶部信息发生变化
     */
    var stockStatus: Int? = null
        set(value) {
            field = value
            refreshMarketStatus()
        }

    init {
        buyAdapter = BaseRecyclerViewAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.layout_item_k_line
        ) {
            tv_minute_name.text = "买${buyAdapter.dataResources.indexOfOnlyRefrence(it) + 1}"
            tv_minute_price.text = if (it.price != 0L) it.price.stockGetValue() else "--"
            tv_minute_amount.text = if (it.numberOfOrders != 0L) it.aggregateQuantity.buySellGetBigNumber() else "--"

            //控制高度
            ((rv_buy.height) / 5.0).let { preferHeight ->
                if (Math.abs(this.height - preferHeight) > 1) {
                    this.layoutParams = ViewGroup.LayoutParams(matchParent, View.MeasureSpec.makeMeasureSpec(preferHeight.toInt(), View.MeasureSpec.EXACTLY))
                }
            }

            // 如果有涨跌变化,则需要进行一次变化
            it.isRateUp?.also { rate ->
                foreground = dispatchGetStockColorDrawable(rate)
                it.isRateUp = null
                postDelayed({ foreground = null }, 300)
            }
        }
        buyAdapter.dataResources.addAll(listOf(WSBuySellDeepBean.BuyBean(), WSBuySellDeepBean.BuyBean(), WSBuySellDeepBean.BuyBean(), WSBuySellDeepBean.BuyBean(), WSBuySellDeepBean.BuyBean()))

        sellAdapter = BaseRecyclerViewAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.layout_item_k_line
        ) {
            tv_minute_name.text = "卖${sellAdapter.dataResources.indexOfOnlyRefrence(it) + 1}"
            tv_minute_price.text = if (it.price != 0L) it.price.stockGetValue() else "--"
            tv_minute_amount.text = if (it.numberOfOrders != 0L) it.aggregateQuantity.buySellGetBigNumber() else "--"

            //控制高度
            ((rv_sell.height) / 5.0).let { preferHeight ->
                if (Math.abs(this.height - preferHeight) > 1) {
                    this.layoutParams = ViewGroup.LayoutParams(matchParent, View.MeasureSpec.makeMeasureSpec(preferHeight.toInt(), View.MeasureSpec.EXACTLY))
                }
            }

            // 如果有涨跌变化,则需要进行一次变化
            it.isRateUp?.also { rate ->
                foreground = dispatchGetStockColorDrawable(rate)
                it.isRateUp = null
                postDelayed({ foreground = null }, 300)
            }
        }
        sellAdapter.dataResources.addAll(listOf(WSBuySellDeepBean.SellBean(), WSBuySellDeepBean.SellBean(), WSBuySellDeepBean.SellBean(), WSBuySellDeepBean.SellBean(), WSBuySellDeepBean.SellBean()))
    }

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //設置一些關鍵佈局的寬高 / 介面變化時,調整顯示效果
        kcv_chart_view.setGridRows(4)
        kcv_chart_view.setGridColumns(4)
        fl_chart_multiply.layoutParams.height = ScreenUtils.screenWidth

        // 刷新数据信息,保证该股票当前信息最新
        mPresenter.getByIdOptional(_username, _signature, if (currentStock.getShareIdI() == 0) null else currentStock.getShareIdI(), currentStock.getShareCodeI()) {
            it?.let {
                currentStock = it

                //自選添加/移除
                val refreshAddCustom = {
                    tv_add_custom.text = getString(if (currentStock.isCollectedI()!!) R.string.activity_k_line_label_custom_remove else R.string.activity_k_line_label_custom_add)
                    tv_add_custom.setCompoundDrawablesWithIntrinsicBounds(null, dispatchGetDrawable(if (currentStock.isCollectedI()!!) R.drawable.dark_icon_remove_custom else R.drawable.dark_icon_add_custom), null, null)
                }
                refreshAddCustom()
                tv_add_custom.dOnClick {
                    mPresenter.addOptional(_username, _signature, currentStock.getShareCodeI()) {
                        toast(if (currentStock.isCollectedI()!!) R.string.activity_k_line_label_custom_remove_success else R.string.activity_k_line_label_custom_add_success)
                        currentStock.setCollected(currentStock.isCollectedI() == false)
                        refreshAddCustom()
                    }
                }

                //初步初始化
                currentStock.also {
                    //股票名字
                    tv_shares_name.text = "${it.getShareNameI()}(${it.getShareCodeI()})"
                    //当前价格
                    tv_current_price.text = it.realTimePrice.stockGetValue()
                    //涨跌价格
                    tv_change_price.text = it.priceLimitAmount.stockGetValue()
                    //涨跌幅度
                    tv_change_rate.text = it.priceLimit.stockGetPercent()
                    // 昨收价格
                    tv_close_price.text = it.previousClosingPrice.stockGetValue()

                    // 颜色依赖项
                    listOf(tv_current_price, tv_max_price, tv_open_price, tv_min_price, tv_close_price).map {
                        it.dependent = tv_change_price
                    }
                }

                //明細
                tv_buy_sell_detail.dOnClick {
                    routeCustom(ARouterConst.Activity_BuySellActivity)
                            .firstParam(currentStock)
                            .navigation()
                            .empty(comment = "買賣盤詳情")
                }

                //創建dialog
                firstDialog = generateDefaultDialog(includeDialog!!, R.layout.dialog_k_line_detail)

                //股票詳細資訊
                iv_more_information.dOnClick {
                    //顯示dialog,提前賦值
                    firstDialog.generateView().run {
                        lastMinHourBean?.also {
                            tv_dialog_open_price.text = tv_open_price.text
                            tv_dialog_max_price.text = it.highPrice.stockGetValueNullable()
                            tv_dialog_close_price.text = currentStock.previousClosingPrice.stockGetValue()
                            tv_dialog_min_price.text = it.lowPrice.stockGetValueNullable()
                            tv_dialog_52_max_price.text = if (tv_dialog_52_max_price.text.matches("(--)|()".toRegex())) it.hPrice1y.stockGetValue() else tv_dialog_52_max_price.text
                            tv_dialog_52_min_price.text = if (tv_dialog_52_min_price.text.matches("(--)|()".toRegex())) it.lPrice1y.stockGetValue() else tv_dialog_52_min_price.text
                            tv_dialog_52_circulate.text = it.turnoverRate.stockGetPercent()
                            tv_dialog_success_count.text = "${it.totalQuantity.stockGetBigNumber(hasScale = false)}股"
                            tv_dialog_total_count.text = if (tv_dialog_total_count.text.matches("(--)|()".toRegex())) it.marketCapital.stockGetBigNumber() else tv_dialog_total_count.text
                            tv_dialog_per_amount.text = currentStock.lotSize.toString()
                            tv_dialog_success_amount.text = it.totalTurnOver.stockGetBigNumber()
                            tv_dialog_swing.text = it.amplitude.stockGetPercent()
                            tv_dialog_pe_ratio.text = it.pe.stockGetValue()
                            tv_dialog_pb_ratio.text = it.pb.stockGetValue()
                            tv_dialog_floating_stock.text = if (tv_dialog_floating_stock.text.matches("(--)|()".toRegex())) "${it.hkIssueCap.stockGetBigNumber()}股" else tv_dialog_floating_stock.text
                            tv_dialog_circulation_value.text = if (tv_dialog_circulation_value.text.matches("(--)|()".toRegex())) it.hkMarketCapital.stockGetBigNumber() else tv_dialog_circulation_value.text
                        }
                        iv_close.dOnClick {
                            includeDialog!!.closeDialogsOpened(instance = firstDialog)
                        }
                    }
                    includeDialog!!.showDialogs(instance = firstDialog, animator = AlphaIDVGAnimatorImpl()).empty(comment = "顯示dialog:股票詳細數據")
                }

                //未開放部分
                listOf(tv_alert, tv_share).dOnClick { toast(R.string.common_not_develop) }

                //交易
                tv_to_exchange.dOnClick {
                    routeCustom(ARouterConst.Activity_DealActivity)
                            .firstParam(currentStock)
                            .navigation()
                            .empty(comment = "交易區")
                }

                // 股票状态
                stockStatus = currentStock.status
                WSManager.addListener(BaseWSListener<WSStockStatus>(WSStockStatus::class.java, {
                    stockStatus = it.status
                }) {
                    it.securityCode == currentStock.getShareCodeI()
                })

                // 获取市场状态
                mPresenter.marketStatus {
                    marketStatus = it?.firstOrNull { it.marketCode == currentStock.marketCode }

                    // ws 监听
                    WSManager.addListener(BaseWSListener<WSMarketStatus>(WSMarketStatus::class.java, {
                        marketStatus = it
                    }) {
                        it.marketCode == currentStock.marketCode
                    })
                }

                //分時間隔
                getStringArray(R.array.array_k_line_tab).forEach {
                    tl_tab.addTab(tl_tab.newTab().setText(it))
                }
                tl_tab.addOnTabSelectedListener(object : MyTabSelectedListener() {
                    override fun onTabSelected(tab: CustomTabLayout.Tab?) {
                        when (tab?.position) {
                            0 means "分時" -> {
                                kcv_chart_view.visibility = View.GONE
                                ll_minute_view.visibility = View.VISIBLE
                            }
                            4 means "週期" -> {
                                //重新選中/選中 最後週期時,提示暫不支持
                                toast(R.string.common_not_develop)
                            }
                            else -> {
                                kcv_chart_view.visibility = View.VISIBLE
                                ll_minute_view.visibility = View.GONE

                                //刷新K線
                                refreshKLine(tab!!.position)
                            }
                        }
                    }
                })

                //新聞,研报，公告，F10
                val stringArray = getStringArray(R.array.array_k_line_news).toList()
                fragments.add((routeCustom(ARouterConst.Fragment_InformationItemFragment)
                        .withInt(Const.KEY_TYPE_FINANCING_INFORMATION, Const.TYPE_FINANCING_INFORMATION_HK)
                        .firstParam(currentStock)
                        .navigation() as BaseFragment<*>).also {
                    // 不能下拉刷新,只能加载更多
                    (it as BaseFragmentRecord<*, *>).canRefresh = false
                    it.canLoadMore = false
                })
                fragments.add(routeCustom(ARouterConst.Fragment_KF10ItemFragment).param(currentStock.getShareCodeI()).navigation() as BaseFragment<*>)
                tpcl_news.provideFragmentManager(sfManager)
                        .provideFragments(fragments)
                        .provideTitles(stringArray)
                        .provideListener(this)
                        .provideOffscreenPageLimit(fragments.size - 1)
                        .combine()

                //加載K線數據
                kcv_chart_view.also {
                    it.adapter = singleChartAdapter
                    it.dateTimeFormatter = DateFormatter()

                    //切屏,(跳转界面)
                    it.mKChartTabView.mTvFullScreen.dOnClick {
                        routeCustom(ARouterConst.Activity_KLineLandActivity)
                                .firstParam(currentStock)
                                .withOptionsCompat(ActivityOptionsCompat.makeSceneTransitionAnimation(this@KLineActivity,
                                        Pair(fl_chart_multiply, getString(R.string.transition_name_kline))
                                ))
                                .navigation(this@KLineActivity)
                                .empty(comment = "横竖屏时,通过跳转界面实现")
                    }
                }

                //深度圖/买卖盘
                rv_buy.layoutManager = LinearLayoutManager(this@KLineActivity, LinearLayoutManager.VERTICAL, false)
                rv_sell.layoutManager = LinearLayoutManager(this@KLineActivity, LinearLayoutManager.VERTICAL, true)
                rv_buy.adapter = buyAdapter
                rv_sell.adapter = sellAdapter

                //买卖盘数据加载(http初始化)
                val refreshBuySell: (WSBuySellDeepBean) -> Unit = {
                    for (index in 0..4) {
                        buyAdapter.dataResources.set(index,
                                (it.bidList.getOrNull(index)
                                        ?: WSBuySellDeepBean.BuyBean()
                                        ).also {
                                    if (buyAdapter.dataResources.size > index) {
                                        it.isRateUp = it.aggregateQuantity > buyAdapter.dataResources.get(index).aggregateQuantity
                                    }
                                })
                        sellAdapter.dataResources.set(index,
                                (it.offerList.getOrNull(index)
                                        ?: WSBuySellDeepBean.SellBean()).also {
                                    if (sellAdapter.dataResources.size > index) {
                                        it.isRateUp = it.aggregateQuantity > sellAdapter.dataResources.get(index).aggregateQuantity
                                    }
                                })
                    }
                    buyAdapter.notifyDataSetChanged()
                    sellAdapter.notifyDataSetChanged()
                }
                mPresenter.buySellOrderList(currentStock.getShareCodeI()) {
                    refreshBuySell((it ?: WSBuySellDeepBean()))

                    // 添加买卖盘监听
                    WSManager.addListener(BaseWSListener<WSBuySellDeepBean>(WSBuySellDeepBean::class.java, {
                        refreshBuySell(it)
                    }) {
                        it.securityCode == currentStock.getShareCodeI()
                    }.also {
                        wsListeners.add(it)
                        WSManager.sendText(WSControlPush(subscribe = true, msgType = WSBuySellDeepBean::class.java, code = currentStock.getShareCodeI()))
                    })
                }

                // 分时 数据加载(分时图)http

                //更新数据
                val refreshMinHour = {
                    yesClosePrice?.let {
                        mcv_minute_view.initData(minHourDatas, it, false)
                    }
                }
                //转换数据格式
                val filterTranslate: (WSStockMinHourBean) -> IMinuteLine = {
                    MinuteLineEntity(it.tradeTime?.stockToDate()
                            ?: Calendar.getInstance().time, it.price / 1000.0, it.averagePrice / 1000.0, it.quantity.toDouble())
                }
                mPresenter.stockTimeShareList(currentStock.getShareCodeI()) {
                    if (it != null) {
                        // 历史数据
                        minHourDatas.clearAddAll(it.history.list.map { filterTranslate(it) })

                        // 昨收价格
                        yesClosePrice = currentStock.previousClosingPrice / 1000.0

                        refreshMinHour()

                        //刷新界面中顶部未知字段信息:
                        lastMinHourBean = it.realTime
                        it.realTime.also {
                            tv_max_price.text = it.highPrice.stockGetValueNullable()
                            tv_open_price.text = it.openingPrice.stockGetValueNullable()
                            tv_min_price.text = it.lowPrice.stockGetValueNullable()
                            tv_success_amount.text = it.totalTurnOver.stockGetBigNumber()
                            tv_circulate_rate.text = it.turnoverRate.stockGetPercent()
                        }

                        // 添加股票信息监听(分时数据)
                        WSManager.addListener(BaseWSListener<WSStockMinHourBean>(WSStockMinHourBean::class.java, {
                            tv_current_price.text = it.price.stockGetValue()
                            tv_change_price.text = it.netChgValue.stockGetValue()
                            tv_change_rate.text = it.netChgPrevDayPct.stockGetPercent()
                            tv_max_price.text = it.highPrice.stockGetValueNullable()
                            tv_min_price.text = it.lowPrice.stockGetValueNullable()
                            tv_success_amount.text = it.totalTurnOver.stockGetBigNumber()
                            tv_circulate_rate.text = it.turnoverRate.stockGetPercent()

                            //处理分时图部分
                            if (it.tradeTime?.stockToDate() equalsIgnoreSecond minHourDatas.lastOrNull()?.date) {
                                minHourDatas.removeAt(minHourDatas.size - 1)
                            }
                            minHourDatas.add(filterTranslate(it))
                            lastMinHourBean = it
                            refreshMinHour()
                        }) {
                            it.securityCode == currentStock.getShareCodeI()
                        }.also {
                            wsListeners.add(it)
                            WSManager.sendText(WSControlPush(subscribe = true, msgType = WSStockMinHourBean::class.java, code = currentStock.getShareCodeI()))
                        })
                    }
                }
            }
        }
    }

    /**
     * 刷新顶部市场状态标志
     *
     * 在以下情况发生变化时,
     */
    private fun refreshMarketStatus() {
        when {
            marketStatus == null || stockStatus == null || lastMinHourBean == null -> null
            stockStatus == 2 -> "停牌" to ""
            stockStatus == 3 || stockStatus == 0 -> when (marketStatus?.status) {
                1 -> when (marketStatus?.tradingSessionSubID) {
                    7 means "9:28 对盘结束" -> "盘前竞价"
                    102 means "12:00 停止" -> "午间休市"
                    else -> null
                }
                2 -> when (marketStatus?.tradingSessionSubID) {
                    1 means "9:00 盘前", 101 means "9:15 不可再改撤单", 2 means "9:20 对盘开始", 7 -> "盘前竞价"
                    3 means "9:30 持续交易 / 13:00 持续交易" -> "交易中"
                    104 means "12:30 可以撤单" -> "午间休市"
                    else -> null
                }
                3 -> when (marketStatus?.tradingSessionSubID) {
                    103 means "12:05 关闭" -> "午间休市"
                    else -> null
                }
                5 -> when (marketStatus?.tradingSessionSubID) {
                    105 means "16:00 持续交易结束", 5 means "16:01 盘后交易", 106 means "16:06 不可撤单", 107 means "16:08 随机收市" -> "收盘竞价"
                    4 means "16:08以后 收市" -> "已收盘" to marketStatus?.timestamp?.stockToDate()?.time?.longToTimeString("MM-dd HH:mm")
                    else -> null
                }
                100 -> when (marketStatus?.tradingSessionSubID) {
                    0 means "最后 关闭 显示已收盘，状态为已收盘， 时间为收盘状态" -> "已收盘" to marketStatus?.timestamp?.stockToDate()?.time?.longToTimeString("MM-dd HH:mm")
                    else -> null
                }
                else -> null
            }
            else -> null
        }.let {
            when (it) {
                is String -> it to (lastMinHourBean?.tradeTime?.stockToDate()?.time?.longToTimeString("MM-dd HH:mm")
                        ?: "")
                is kotlin.Pair<*, *> -> it
                null -> null to null
                else -> TODO()
            }
        }.also {
            //股票交易时间
            tv_share_time.text = "HK ${it.first ?: "--"} ${it.second ?: "--"}"
        }
    }

    /**
     * 刷新k-line数据
     */
    private fun refreshKLine(dateType: Int) {
        val evalResult = {
            singleChartAdapter.datas.clearAddAll(klineDatas[dateType]!!)
            singleChartAdapter.notifyDataSetChanged()
            kcv_chart_view.startAnimation()
            kcv_chart_view.refreshEnd()
        }

        if (klineDatas.getOrNull(dateType) == null) {
            //初次加载k-line
            kcv_chart_view.showLoading()
            mPresenter.klineList(currentStock.getShareCodeI(), dateType) {
                it?.also {
                    empty {
                        var test = it
                        if (it.size != 0) {
                            var c = Calendar.getInstance()
                            c.timeInMillis = it.first().createTime?.stockToDate()?.time
                                    ?: System.currentTimeMillis()
                            for (i in 0 until 60) {
                                c.add(Calendar.DAY_OF_MONTH, 1)
                                test.add(StockKLineBean(closingPrice = it.first().closingPrice,
                                        conversionRatio = it.first().conversionRatio,
                                        createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.time),
                                        highPrice = it.first().highPrice,
                                        lowPrice = it.first().lowPrice,
                                        openingPrice = it.first().openingPrice,
                                        securityCode = it.first().securityCode,
                                        turnover = it.first().turnover))
                            }

                            test.add(StockKLineBean(closingPrice = it.first().closingPrice,
                                    conversionRatio = it.first().conversionRatio,
                                    createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.time),
                                    highPrice = 70,
                                    lowPrice = 50,
                                    openingPrice = 60,
                                    securityCode = it.first().securityCode,
                                    turnover = it.first().turnover))
                        }
                    }
                    klineDatas[dateType] = it.map {
                        KLineEntity(it.createTime, it.openingPrice / 1000.0, it.highPrice / 1000.0, it.lowPrice / 1000.0, it.closingPrice / 1000.0, it.turnover / 1000.0)
                    }.also {
                        DataHelper.calculate(it)
                    }
                    evalResult()
                }
            }
        } else {
            evalResult()
        }
    }

    /**
     * @param position 第position位置的頁面顯示
     */
    override fun onPagerAppear(position: Int) {
        //調用當前fragment的方法
        tpcl_news.post { (fragments[position]).onPagerFragmentChange(true) }
    }

    override fun onDestroy() {
        WSManager.removeListener(wsListeners)

        super.onDestroy()
    }
}
