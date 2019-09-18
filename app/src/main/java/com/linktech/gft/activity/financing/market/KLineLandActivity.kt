package com.linktech.gft.activity.financing.market


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.tifezh.kchartlib.chart.beans.MinuteLineEntity
import com.github.tifezh.kchartlib.chart.entity.IMinuteLine
import com.github.tifezh.kchartlib.chart.formatter.DateFormatter
import com.linktech.gft.R
import com.linktech.gft.adapter.KChartAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.*
import com.linktech.gft.interfaces.MyTabSelectedListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DataHelper
import com.linktech.gft.util.ScreenUtils
import com.linktech.gft.view.tablayout.CustomTabLayout
import com.linktech.gft.ws.BaseWSListener
import com.linktech.gft.ws.WSManager
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_k_line_land.*
import kotlinx.android.synthetic.main.layout_item_k_line.view.*
import org.jetbrains.anko.matchParent
import java.util.*

/**
 * function---- KLineLandActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_KLineLandActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_k_line_land)
@SuppressLint("SetTextI18n")
class KLineLandActivity : BaseActivity<BasePresenter<KLineLandActivity>>() {
    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var currentStock: OptionalListBean = OptionalListBean()

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
    private  lateinit  var buyAdapter: BaseRecyclerViewAdapter<WSBuySellDeepBean.BuyBean>
    private  lateinit  var sellAdapter: BaseRecyclerViewAdapter<WSBuySellDeepBean.SellBean>

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
        buyAdapter.dataResources.addAll(listOf(WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean(),WSBuySellDeepBean.BuyBean()))

        sellAdapter = BaseRecyclerViewAdapter(
                dataResources = mutableListOf(),
                layoutResId = R.layout.layout_item_k_line
        ) {
            tv_minute_name.text = "卖${sellAdapter.dataResources.indexOfOnlyRefrence(it) + 1}"
            tv_minute_price.text = if (it.price != 0L) it.price.stockGetValue() else "--"
            tv_minute_amount.text =  if (it.numberOfOrders != 0L) it.aggregateQuantity.buySellGetBigNumber() else "--"

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
        sellAdapter.dataResources.addAll(listOf(WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean(),WSBuySellDeepBean.SellBean()))
    }

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //設置window效果,鍵盤效果,螢幕方向,輸入法顯示等
        try {
            //判斷,如果是在版本超過8.0的手機上,該方法可能會出現異常:Only fullscreen activities can request orientation?
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } catch (e: Exception) {
            Logger.t(Const.TAG_LOGGER_REPORT_NET).e(e, "BaseActivity:onCreate:設置window效果")
        }

        //設置一些關鍵佈局的寬高 / 介面變化時,調整顯示效果
        kcv_chart_view.setGridRows(3)
        kcv_chart_view.setGridColumns(8)
        fl_chart_multiply.layoutParams.height = ScreenUtils.screenWidth

        //明細
        tv_buy_sell_detail.dOnClick {
            routeCustom(ARouterConst.Activity_BuySellActivity)
                    .firstParam(currentStock)
                    .navigation()
                    .empty(comment = "買賣盤詳情")
        }

        //分時間隔
        getStringArray(R.array.array_k_line_tab).map {
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

        //加載K線數據
        kcv_chart_view.also {
            it.adapter = singleChartAdapter
            it.dateTimeFormatter = DateFormatter()

            // 重写切屏逻辑
            it.mKChartTabView.mTvFullScreen.dOnClick {
                onBackPressed()
            }
        }

        //深度圖/买卖盘
        rv_buy.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_sell.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
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
        }

        // 添加买卖盘监听
        WSManager.addListener(BaseWSListener<WSBuySellDeepBean>(WSBuySellDeepBean::class.java, {
            refreshBuySell(it)
        }) {
            it.securityCode == currentStock.getShareCodeI()
        }.also {
            wsListeners.add(it)
            WSManager.sendText(WSControlPush(subscribe = true, msgType = WSBuySellDeepBean::class.java, code = currentStock.getShareCodeI()))
        })


        //分時圖

        //更新数据
        val refreshMinHour = {
            yesClosePrice?.let {
                mcv_minute_view.initData(minHourDatas,it ,false)
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
                minHourDatas.addAll(it.history.list.map { filterTranslate(it) })

                // 昨收价格
                yesClosePrice = currentStock.previousClosingPrice/1000.0

                refreshMinHour()
            }
        }

        // 添加股票信息监听(分时数据)
        WSManager.addListener(BaseWSListener<WSStockMinHourBean>(WSStockMinHourBean::class.java, {
            //处理分时图部分
            if (it.tradeTime?.stockToDate() equalsIgnoreSecond minHourDatas.lastOrNull()?.date) {
                minHourDatas.removeAt(minHourDatas.size - 1)
            }
            minHourDatas.add(filterTranslate(it))
            refreshMinHour()
        }) {
            it.securityCode == currentStock.getShareCodeI()
        }.also {
            wsListeners.add(it)
            WSManager.sendText(WSControlPush(subscribe = true, msgType = WSStockMinHourBean::class.java, code = currentStock.getShareCodeI()))
        })
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

    override fun onDestroy() {
        super.onDestroy()

        WSManager.removeListener(wsListeners)
    }
}
