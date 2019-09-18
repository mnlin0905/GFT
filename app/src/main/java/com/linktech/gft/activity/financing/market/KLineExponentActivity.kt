package com.linktech.gft.activity.financing.market


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.tifezh.kchartlib.chart.beans.MinuteLineEntity
import com.github.tifezh.kchartlib.chart.entity.IMinuteLine
import com.github.tifezh.kchartlib.chart.formatter.DateFormatter
import com.linktech.gft.R
import com.linktech.gft.adapter.KChartAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BaseFragmentRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.KLineEntity
import com.linktech.gft.bean.WSControlPush
import com.linktech.gft.bean.WSExponentChangeBean
import com.linktech.gft.bean.WSMarketStatus
import com.linktech.gft.interfaces.MyTabSelectedListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DataHelper
import com.linktech.gft.util.ScreenUtils
import com.linktech.gft.view.TabPagerCombineLayout
import com.linktech.gft.view.tablayout.CustomTabLayout
import com.linktech.gft.ws.BaseWSListener
import com.linktech.gft.ws.WSManager
import kotlinx.android.synthetic.main.activity_k_line_exponent.*
import java.util.*

/**
 * function---- kline 界面/无交易
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_KLineExponentActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_k_line_exponent)
@InjectActivityTitle(title = "")
class KLineExponentActivity : BaseActivity<BasePresenter<KLineExponentActivity>>(), TabPagerCombineLayout.onTabPagerListener {
    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var currentExponent: WSExponentChangeBean = WSExponentChangeBean()

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
     * WS监听器:
     *
     * 1.分时数据监听(指数)
     */
    private var wsListener: MutableList<BaseWSListener<*>> = mutableListOf()

    /**
     * 昨收价格
     */
    var yesClosePrice: Double? = .0

    /**
     * 最新的指数数据信息
     */
    var lastExponentBean: WSExponentChangeBean? = null
        set(value) {
            field = value
            refreshMarketStatus()
        }

    /**
     * 市场状态 更改时需要顶部信息发生变化
     */
    var marketStatus: WSMarketStatus? = null
        set(value) {
            field = value
            refreshMarketStatus()
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
        mPresenter.indexDataList {
            it?.firstOrNull {
                it.getShareCodeI() == currentExponent.getShareCodeI()
            }?.also {
                currentExponent = it

                //自選添加/移除
                val refreshAddCustom = {
                    tv_add_custom.text = getString(if (currentExponent.isCollectedI()!!) R.string.activity_k_line_label_custom_remove else R.string.activity_k_line_label_custom_add)
                    tv_add_custom.setCompoundDrawablesWithIntrinsicBounds(null, dispatchGetDrawable(if (currentExponent.isCollectedI()!!) R.drawable.dark_icon_remove_custom else R.drawable.dark_icon_add_custom), null, null)
                }
                refreshAddCustom()
                tv_add_custom.dOnClick {
                    mPresenter.addOptional(_username, _signature, currentExponent.getShareCodeI()) {
                        toast(if (currentExponent.isCollectedI()!!) R.string.activity_k_line_label_custom_remove_success else R.string.activity_k_line_label_custom_add_success)
                        currentExponent.setCollected(currentExponent.isCollectedI() == false)
                        refreshAddCustom()
                    }
                }

                //初步初始化
                currentExponent.also {
                    //股票名字
                    tv_shares_name.text = "${it.getShareNameI()}(${it.securityCodeSimple})"
                    //当前价格
                    tv_current_price.text = it.indexValue.stockGetValue(isExponent = true)
                    //涨跌价格
                    tv_change_price.text = it.netChgValue.stockGetValue(isExponent = true)
                    //涨跌幅度
                    tv_change_rate.text = it.netChgPrevDayPct.stockGetPercent()
                    // 昨收价格
                    tv_close_price.text = it.previousSesClose.stockGetValue(isExponent = true)
                    // 控制颜色
                    listOf(tv_swing, tv_up, tv_down, tv_equal).forEach { it.isActivated = true }

                    // 颜色依赖项
                    listOf(tv_current_price, tv_max_price, tv_open_price, tv_min_price, tv_close_price).map {
                        it.dependent = tv_change_price
                    }
                }

                //未開放部分
                listOf(tv_alert, tv_share).dOnClick { toast(R.string.common_not_develop) }

                //分時間隔,新聞資訊
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

                //新聞
                val stringArray = getStringArray(R.array.array_k_line_news).toList().subList(0, 1)
                fragments.add((routeCustom(ARouterConst.Fragment_InformationItemFragment)
                        .withInt(Const.KEY_TYPE_FINANCING_INFORMATION, Const.TYPE_FINANCING_INFORMATION_HK)
                        .navigation() as BaseFragment<*>).also {
                    // 不能下拉刷新,只能加载更多
                    (it as BaseFragmentRecord<*, *>).canRefresh = false
                    it.canLoadMore = false
                })
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
                        routeCustom(ARouterConst.Activity_KLineLandExponentActivity)
                                .firstParam(currentExponent)
                                .navigation(this@KLineExponentActivity)
                                .empty(comment = "横竖屏时,通过跳转界面实现")
                    }
                }

                // 获取市场状态
                mPresenter.marketStatus {
                    marketStatus = it?.firstOrNull { it.marketCode == "MAIN" }

                    // ws 监听
                    WSManager.addListener(BaseWSListener<WSMarketStatus>(WSMarketStatus::class.java, {
                        marketStatus = it
                    }) {
                        it.marketCode == "MAIN"
                    })
                }

                // 分时 数据加载(分时图)http

                //更新数据
                val refreshMinHour = {
                    yesClosePrice?.let {
                        mcv_minute_view.initData(minHourDatas, it, true)
                    }
                }
                //转换数据格式
                val filterTranslate: (WSExponentChangeBean) -> IMinuteLine = {
                    MinuteLineEntity(it.indexTime?.stockToDate()
                            ?: Calendar.getInstance().time, it.indexValue / 10000.0, it.easValue / 100.0, it.indexTurnover / 10000.0)
                }
                mPresenter.exponentTimeShareList(currentExponent.getShareIdI()!!) {
                    if (it != null) {
                        // 历史数据
                        minHourDatas.addAll(it.history.list.map { filterTranslate(it) })

                        // 昨收价格
                        yesClosePrice = currentExponent.previousSesClose / 10000.0

                        refreshMinHour()

                        //刷新界面中顶部未知字段信息:
                        it.realTime.also {
                            tv_max_price.text = it.highValue.stockGetValueNullable(isExponent = true)
                            tv_open_price.text = it.openingValue.stockGetValueNullable(isExponent = true)
                            tv_min_price.text = it.lowValue.stockGetValueNullable(isExponent = true)
                            tv_success_amount.text = it.indexTurnover.stockGetBigNumber(isExponent = true)
                            tv_swing.text = it.amplitude.stockGetPercent()
                            tv_up.text = it.indexAdv.toString()
                            tv_equal.text = it.indexFlat.toString()
                            tv_down.text = it.indexDec.toString()
                            lastExponentBean = it
                        }

                        // 添加股票信息监听(分时数据)
                        WSManager.addListener(BaseWSListener<WSExponentChangeBean>(WSExponentChangeBean::class.java, {
                            tv_current_price.text = it.indexValue.stockGetValue(isExponent = true)
                            tv_change_price.text = it.netChgValue.stockGetValue(isExponent = true)
                            tv_change_rate.text = it.netChgPrevDayPct.stockGetPercent()
                            tv_max_price.text = it.highValue.stockGetValueNullable(isExponent = true)
                            tv_min_price.text = it.lowValue.stockGetValueNullable(isExponent = true)
                            tv_success_amount.text = it.indexTurnover.stockGetBigNumber(isExponent = true)
                            tv_swing.text = it.amplitude.stockGetPercent()
                            tv_up.text = it.indexAdv.toString()
                            tv_equal.text = it.indexFlat.toString()
                            tv_down.text = it.indexDec.toString()

                            //处理分时图部分
                            if (it.indexTime?.stockToDate() equalsIgnoreSecond minHourDatas.lastOrNull()?.date) {
                                minHourDatas.removeAt(minHourDatas.size - 1)
                            }
                            lastExponentBean = it
                            minHourDatas.add(filterTranslate(it))
                            refreshMinHour()
                        }) {
                            it.getShareCodeI() == currentExponent.getShareCodeI()
                        }.also {
                            wsListener.add(it)
                            WSManager.sendText(WSControlPush(subscribe = true, msgType = WSExponentChangeBean::class.java, code = currentExponent.getShareCodeI()))
                        })
                    }
                }
            }
        }
    }

    /**
     * 设置时间信息
     */
    @SuppressLint("SetTextI18n")
    private fun refreshMarketStatus() {
        when {
            marketStatus == null || lastExponentBean == null -> null
            else -> when (marketStatus?.status) {
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
                    4 means "16:08以后 收市" -> "已收盘" to "16:00"
                    else -> null
                }
                100 -> when (marketStatus?.tradingSessionSubID) {
                    0 means "最后 关闭 显示已收盘， 状态为已收盘， 时间为16:00" -> "已收盘" to "16:00"
                    else -> null
                }
                else -> null
            }
        }.let {
            when (it) {
                is String -> it to (lastExponentBean?.indexTime?.stockToDate()?.time?.longToTimeString("MM-dd HH:mm")
                        ?: "")
                is kotlin.Pair<*, *> -> it
                null -> "--" to "--"
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
            mPresenter.exponentKlineList(currentExponent.getShareIdI()!!, dateType) {
                it?.also {
                    klineDatas[dateType] = it.map {
                        KLineEntity(it.createTime, it.openingValue / 10000.0, it.highValue / 10000.0, it.lowValue / 10000.0, it.closingValue / 10000.0, it.indexTurnover / 10000.0)
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
        super.onDestroy()

        WSManager.removeListener(wsListener)
    }
}
