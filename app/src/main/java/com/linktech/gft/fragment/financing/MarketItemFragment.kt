package com.linktech.gft.fragment.financing


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.ColumnLayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.base.BaseVDelegateAdapter
import com.linktech.gft.bean.*
import com.linktech.gft.plugins.*
import com.linktech.gft.util.ActivityUtil
import com.linktech.gft.util.Const
import com.linktech.gft.view.SortUpDownView
import com.linktech.gft.ws.BaseWSListener
import com.linktech.gft.ws.WSManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_market_item.*
import kotlinx.android.synthetic.main.layout_item_market_first_adapter.view.*
import kotlinx.android.synthetic.main.layout_item_market_fourth_adapter.view.*
import kotlinx.android.synthetic.main.layout_item_market_item_custom.view.*
import kotlinx.android.synthetic.main.layout_item_market_second_adapter.view.*
import kotlinx.android.synthetic.main.layout_market_item_custom.*
import kotlinx.android.synthetic.main.layout_market_item_custom.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColorResource
import skin.support.content.res.SkinCompatResources
import java.util.*
import kotlin.Comparator
import kotlin.concurrent.timer


/**
 * function : 行情/詳情
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_MarketItemFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_market_item)
class MarketItemFragment : BaseFragment<BasePresenter<MarketItemFragment>>() {
    /**
     * 設置默認的選中位置
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var fragmentPosition: Int = Const.VALUE_POSITION_NULL

    /**
     * 適配器,延遲加載
     */
    private lateinit var viewPagerAdapter: ViewAdapter
    private lateinit var firstView: View
    private lateinit var secondView: RecyclerView

    /**
     * 當前哪個佈局介面在前 [Const.KEY_MARKET_SCAN]
     */
    private var currentPosition: Int = Const.VALUE_POSITION_NULL

    /**
     * 當前排序的控件
     */
    private var sortView: SortUpDownView? = null

    /**
     * 行业板块定时器任务
     */
    private var industryTask: Timer? = null

    /**
     * ws监听器回调:
     *
     * 1. 自选列表信息
     */
    private var customWSListener: BaseWSListener<*>? = null

    /**
     * 适配器列表
     */
    private lateinit var delegateAdapter: DelegateAdapter
    private lateinit var firstAdapter: BaseVDelegateAdapter<WSExponentChangeBean>
    private lateinit var fourthAdapter: BaseVDelegateAdapter<IndustryListBean>
    private lateinit var sixthAdapter: BaseVDelegateAdapter<OptionalListBean>
    private lateinit var eighthAdapter: BaseVDelegateAdapter<OptionalListBean>
    private lateinit var tenthAdapter: BaseVDelegateAdapter<OptionalListBean>
    private lateinit var twelfthAdapter: BaseVDelegateAdapter<OptionalListBean>

    /**1
     * 通用依赖注入
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    val commonItemInject = { view: View, bean: OptionalListBean ->
        empty(comment = "數據加載")

        //加載基礎數據
        view.apply {
            tv_name.text = bean.getShareNameI()
            tv_code.text = bean.getShareCodeI()
            tv_code.setCompoundDrawablesWithIntrinsicBounds(dispatchGetDrawable(R.drawable.dark_icon_market_hk), null, null, null)

            //加載網路數據
            tv_rate.text = bean.priceLimit.stockGetPercent().let { if (!it.contains("-")) "+$it" else it }
            tv_amount.text = bean.realTimePrice.stockGetValue()
            tv_amount.textColorResource = getStockColor(bean.priceLimit.toString())
            cv_rate.setCardBackgroundColor(dispatchGetStockColor(bean.priceLimit.toString()))

            // 如果有涨跌变化,则需要进行一次变化
            bean.isRateUp?.also {
                foreground = dispatchGetStockColorDrawable(it)
                bean.isRateUp = null
                postDelayed({ foreground = null }, 300)
            }
        }
    }

    /**
     * 列表數據源
     * 適配器
     */
    private var sharesData: MutableList<OptionalListBean> = mutableListOf()
    @SuppressLint("SetTextI18n")
    private var sharesAdapter = BaseRecyclerViewAdapter(
            dataResources = sharesData,
            layoutResId = R.layout.layout_item_market_item_custom,
            listener = { _: View, index: Int ->
                sharesData[index].also {
                    routeCustom(ARouterConst.Activity_KLineActivity)
                            .firstParam(it)
                            .navigation(baseActivity, Const.REQUEST_CODE_ONE)
                            .empty(comment = "顯示K線等數據")
                }
            }
    ) {
        commonItemInject(this, it)
    }

    //1--价格降序 2--价格升序  3--涨跌幅降序  4--涨跌幅升序  null--不排序
    private var currentSort: Int? = null

    @SuppressLint("all")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //手動加載佈局,禁止手指滑動
        firstView = layoutInflater.inflate(R.layout.layout_market_item_custom, rootView, false).apply {
            //立即開戶
            lmv_register.onPerformSelf {
                if (!is_trade_login) {
                    route(ARouterConst.Activity_ChooseTradeActivity)
                } else {
                    toast(getString(R.string.fragment_market_item_has_login))
                }
            }

            /**
             * 排序方式
             */
            val comparable = Comparator<OptionalListBean> { o1, o2 ->
                //如果有為空的部分,則當做最小值去處理
                if (o1 == null || o2 == null) {
                    return@Comparator 0
                }
                when (currentSort) {
                    1 means "价格降序" -> o1.realTimePrice.compareTo(o2.realTimePrice)
                    2 means "价格升序" -> o2.realTimePrice.compareTo(o1.realTimePrice)
                    3 means "涨跌幅降序" -> o1.priceLimit.compareTo(o2.priceLimit)
                    4 means "涨跌幅升序" -> o2.realTimePrice.compareTo(o1.realTimePrice)
                    else -> 0
                }
            }

            //價格,漲跌幅,排序
            listOf(tv_sort_price, tv_sort_rate).mapIndexed { index, tv ->
                tv.dOnClick {
                    when (index) {
                        0 means "價格排序" -> {
                            tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (currentSort == 3 || currentSort == 4 || currentSort == null) R.drawable.dark_icon_sort_desc
                            else if (currentSort == 1) R.drawable.dark_icon_sort_asc
                            else R.drawable.dark_icon_sort_default, 0)
                            this@MarketItemFragment.tv_sort_rate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                            when (currentSort) {
                                null, 3, 4 -> currentSort = 1
                                1 -> currentSort = 2
                                2 -> currentSort = null
                            }
                        }
                        1 means "漲跌幅排序" -> {
                            tv_sort_rate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (currentSort == 1 || currentSort == 2 || currentSort == null) R.drawable.dark_icon_sort_desc
                            else if (currentSort == 3) R.drawable.dark_icon_sort_asc
                            else R.drawable.dark_icon_sort_default, 0)
                            this@MarketItemFragment.tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                            when (currentSort) {
                                null, 1, 2 -> currentSort = 3
                                3 -> currentSort = 4
                                4 -> currentSort = null
                            }
                        }
                    }
                    if (currentSort == null) {
                        refreshCustomShares()
                    } else {
                        sharesData.sortWith(comparable)
                    }
                    sharesAdapter.notifyDataSetChanged()
                }
            }

            //添加股票
            mutableListOf(tv_add_share, fl_add_share,baseActivity.findViewById(R.id.tv_search)).dOnClick {
                routeCustom(ARouterConst.Activity_AddSharesActivity)
                        .secondParam(sharesData.map { it.getShareCodeI() })
                        .navigation(baseActivity, Const.REQUEST_CODE_ONE)
                        .empty(comment = "添加股票,通過")
            }

            //列表資料項目,適配器等
            val emptyV = empty_view
            xrv_record.run {
                layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
                adapter = sharesAdapter
                emptyView = emptyV
            }

            //註冊監聽器,當數據有變化時,控制添加按鈕的消失/顯示
            sharesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    fl_add_share_control.visibility = if (sharesData.isEmpty()) View.GONE else View.VISIBLE
                }
            })
        }

        //第二個佈局,即市場:
        secondView = RecyclerView(baseActivity).also {
            it.isNestedScrollingEnabled = false
            it.overScrollMode = View.OVER_SCROLL_NEVER

            //添加到父視圖中
            it.layoutParams = ViewGroup.MarginLayoutParams(matchParent, matchParent)

            // 創建VirtualLayoutManager對象,同時內部會創建一個LayoutHelperFinder對象，用來後續的LayoutHelper查找
            val layoutManager = VirtualLayoutManager(baseActivity)
            it.layoutManager = layoutManager

            // 設置組件複用回收池
            empty {
                val viewPool = RecyclerView.RecycledViewPool()
                it.recycledViewPool = viewPool
                viewPool.setMaxRecycledViews(0, 10)
            }

            //適配器管理器
            delegateAdapter = DelegateAdapter(layoutManager, false)
            val adapters = mutableListOf<DelegateAdapter.Adapter<*>>()

            //佈局管理器:指數
            firstAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(WSExponentChangeBean(indexCode = "0000100"), WSExponentChangeBean(indexCode = "0001400"), WSExponentChangeBean(indexCode = "CES100")),
                    layoutResId = R.layout.layout_item_market_first_adapter,
                    layoutHelper = ColumnLayoutHelper(),
                    listener = { _, i ->
                        routeCustom(ARouterConst.Activity_KLineExponentActivity)
                                .firstParam(firstAdapter.dataResources[i])
                                .navigation()
                                .empty(comment = "指数的k-line数据")
                    }
            ) { bean, _, _ ->
                empty(comment = "指数涨跌")
                tv_exponent_name.text = bean.securityCodeStr
                tv_exponent_change_amount.text = bean.netChgValue.stockGetValue(isExponent = true)
                tv_exponent_change_percent.text = bean.netChgPrevDayPct.stockGetPercent()
                tv_exponent_amount.dependent = tv_exponent_change_amount
                tv_exponent_amount.text = bean.indexValue.stockGetValue(isExponent = true)

                // 如果有涨跌变化,则需要进行一次变化
                bean.isRateUp?.also {
                    foreground = dispatchGetStockColorDrawable(it)
                    bean.isRateUp = null
                    postDelayed({ foreground = null }, 300)
                }
            }

            //佈局管理器:菜單
            val secondAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(
                            R.drawable.dark_icon_market_item_second_one to getString(R.string.label_turbine),
                            R.drawable.dark_icon_market_item_second_two to getString(R.string.label_bull_bear_syndrome),
                            R.drawable.dark_icon_market_item_second_three to getString(R.string.fragment_market_item_second_three),
                            R.drawable.dark_icon_market_item_second_four to getString(R.string.fragment_market_item_second_four)),
                    layoutResId = R.layout.layout_item_market_second_adapter,
                    layoutHelper = ColumnLayoutHelper().also { it.marginTop = dispatchGetDimen(R.dimen.view_padding_margin_10dp);it.marginBottom = dispatchGetDimen(R.dimen.view_padding_margin_10dp) },
                    listener = { _: View, i ->
                        when (i) {
                            0 means "涡轮" -> route(ARouterConst.Activity_TurbineActivity)
                            1 means "牛熊" -> route(ARouterConst.Activity_BullBearSyndromeActivity)
                            2 means "融资" -> route(ARouterConst.Activity_FinancableStockActivity)
                            3 means "区间" -> route(ARouterConst.Activity_SectionChangeRateActivity)
                        }
                    }
            ) { bean, _, _ ->
                iv_second_icon.imageResource = bean.first
                iv_second_function.text = bean.second
            }

            //佈局管理器:行業板塊/熱門行業
            val thirdAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(OptionalListBean()),
                    layoutResId = R.layout.layout_item_market_third_adapter,
                    layoutHelper = LinearLayoutHelper(),
                    listener = { _: View, _ ->
                        route(ARouterConst.Activity_IndustrySectorActivity)
                    }
            ) { _, _, _ ->
                (this as LineMenuView).menuText = getString(R.string.fragment_market_item_third_title_one)
            }

            //佈局管理器:行業板塊 - 簡要
            fourthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(),
                    layoutResId = R.layout.layout_item_market_fourth_adapter,
                    layoutHelper = ColumnLayoutHelper().also { it.marginBottom = dispatchGetDimen(R.dimen.view_padding_margin_10dp) },
                    listener = { _: View, i ->
                        routeCustom(ARouterConst.Activity_IndustryPartActivity)
                                .firstParam(fourthAdapter.dataResources[i].induName)
                                .secondParam(fourthAdapter.dataResources[i].induCode)
                                .navigation()
                    }
            ) { bean, position, offset ->
                tv_industry_name.text = bean.induName
                tv_industry_rate.text = (bean.induChgPct * 100).stockGetPercent()
                tv_industry_name_back.text = bean.stk.stkName
                tv_industry_change_amount.dependent = tv_industry_change_percent
                tv_industry_change_amount.text = bean.price.stockGetValue()
                tv_industry_change_percent.text = (bean.priceLimit?.toDouble()
                        ?: 0.0).stockGetPercent()

                // 如果有涨跌变化,则需要进行一次变化
                bean.isRateUp?.also {
                    foreground = dispatchGetStockColorDrawable(it)
                    bean.isRateUp = null
                    postDelayed({ foreground = null }, 300)
                }
            }

            //佈局管理器:概念板塊
            val fifthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(OptionalListBean()),
                    layoutResId = R.layout.layout_item_market_third_adapter,
                    layoutHelper = LinearLayoutHelper(),
                    listener = { _: View, _ ->
                        route(ARouterConst.Activity_ConceptualPlateActivity)
                    }
            ) { _, _, _ ->
                (this as LineMenuView).menuText = getString(R.string.fragment_market_item_third_title_two)
            }

            //佈局管理器:概念板塊 - 簡要
            sixthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(OptionalListBean(), OptionalListBean(), OptionalListBean()),
                    layoutResId = R.layout.layout_item_market_fourth_adapter,
                    layoutHelper = ColumnLayoutHelper().also { it.marginBottom = dispatchGetDimen(R.dimen.view_padding_margin_10dp) },
                    listener = { _: View, i ->
                        routeCustom(ARouterConst.Activity_KLineComponentActivity)
                                .firstParam(sixthAdapter.dataResources[i])
                                .navigation()
                    }
            ) { bean, position, offset ->
                empty(comment = "")
            }

            //佈局管理器:全部港股
            val seventhAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(OptionalListBean()),
                    layoutResId = R.layout.layout_item_market_third_adapter,
                    layoutHelper = LinearLayoutHelper(),
                    listener = { _: View, _ ->
                        route(ARouterConst.Activity_AllMarketStocksActivity)
                    }
            ) { _, _, _ ->
                (this as LineMenuView).menuText = getString(R.string.fragment_market_item_third_title_three)
            }

            //佈局管理器:全部港股-簡要
            eighthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(),
                    layoutResId = R.layout.layout_item_market_fifth_adapter,
                    layoutHelper = LinearLayoutHelper().also { it.marginBottom = dispatchGetDimen(R.dimen.view_padding_margin_10dp) },
                    listener = { _: View, i ->
                        routeCustom(ARouterConst.Activity_KLineActivity)
                                .firstParam(eighthAdapter.dataResources[i])
                                .navigation()
                    }
            ) { bean, _, _ ->
                commonItemInject(this, bean)
            }

            //佈局管理器:主板
            val ninthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(OptionalListBean()),
                    layoutResId = R.layout.layout_item_market_third_adapter,
                    layoutHelper = LinearLayoutHelper(),
                    listener = { _: View, _ ->
                        routeCustom(ARouterConst.Activity_AllMarketStocksActivity)
                                .firstParam(3)
                                .navigation()
                    }
            ) { _, _, _ ->
                (this as LineMenuView).menuText = getString(R.string.fragment_market_item_third_title_four)
            }

            //佈局管理器:主板-簡要
            tenthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(),
                    layoutResId = R.layout.layout_item_market_fifth_adapter,
                    layoutHelper = LinearLayoutHelper().also { it.marginBottom = dispatchGetDimen(R.dimen.view_padding_margin_10dp) },
                    listener = { view: View, i ->
                        routeCustom(ARouterConst.Activity_KLineActivity)
                                .firstParam(tenthAdapter.dataResources[i])
                                .navigation()
                    }
            ) { bean, _, _ ->
                commonItemInject(this, bean)
            }

            //佈局管理器:創業板
            val eleventhAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(OptionalListBean()),
                    layoutResId = R.layout.layout_item_market_third_adapter,
                    layoutHelper = LinearLayoutHelper(),
                    listener = { _: View, _ ->
                        routeCustom(ARouterConst.Activity_AllMarketStocksActivity)
                                .firstParam(4)
                                .navigation()
                    }
            ) { _, _, _ ->
                (this as LineMenuView).menuText = getString(R.string.fragment_market_item_third_title_five)
            }

            //佈局管理器:創業板-簡要
            twelfthAdapter = BaseVDelegateAdapter(
                    dataResources = mutableListOf(),
                    layoutResId = R.layout.layout_item_market_fifth_adapter,
                    layoutHelper = LinearLayoutHelper(),
                    listener = { _: View, i ->
                        routeCustom(ARouterConst.Activity_KLineActivity)
                                .firstParam(twelfthAdapter.dataResources[i])
                                .navigation()
                    }
            ) { bean, _, _ ->
                commonItemInject(this, bean)
            }

            //組合
            adapters.addAll(mutableListOf(firstAdapter, secondAdapter, thirdAdapter, fourthAdapter, /*fifthAdapter, sixthAdapter,TODO 移除概念板块*/ seventhAdapter, eighthAdapter, ninthAdapter, tenthAdapter, eleventhAdapter, twelfthAdapter))

            delegateAdapter.addAdapters(adapters)
            it.adapter = delegateAdapter
        }

        //佈局加載到適配器
        viewPagerAdapter = ViewAdapter(mutableListOf(firstView, secondView))
        vp_content.adapter = viewPagerAdapter
        vp_content.setManageScrollInterface { false }

        //切换红涨绿跌模式
        RxBus.instance!!.toObservable(SwitchColor::class.java).observeOn(AndroidSchedulers.mainThread()).subscribe {
            //刷新自选
            sharesAdapter.notifyDataSetChanged()
        }
    }

    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)

        // 新位置
        currentPosition = obj as Int

        // 动态修改背景,保证皮肤的切换
        secondView.setBackgroundColor(SkinCompatResources.getColor(baseActivity, R.color.skin_scaffold_deep_background_color))

        // 每次都刷新自选列表 / 全部板块等
        when (currentPosition) {
            Const.MARKET_SCAN_CUSTOM means "自定義" -> refreshCustomShares()
            Const.MARKET_SCAN_MARKET means "市場" -> refreshAllStock()
            else -> TODO()
        }

        //切換 viewpager 介面
        vp_content.setCurrentItem(currentPosition, true)

        //根據當前 位置,來判斷如何刷新數據,只刷新一次
        if (isRequireInit) {
            when (currentPosition) {
                Const.MARKET_SCAN_CUSTOM means "自定義" -> empty { refreshCustomShares() }
                Const.MARKET_SCAN_MARKET means "市場" -> refreshMarketList()
                else -> TODO()
            }
        }

        //判断是否登录
        firstView.lmv_register?.visibility = if (is_trade_login) View.GONE else View.VISIBLE
    }

    /**
     * 刷新市场数据 / 指数数据 等
     *
     * 该部分只会执行一次
     */
    private fun refreshMarketList() {
        // 禁止下次执行
        isRequireInit = false

        if (fragmentPosition == 2) {
            return
        }

        // 行业板块定时器 per 5 second TODO 如果已经 停shi 行业板块，则不再刷新
        val taskRun = {
            mPresenter.industryStockList(null, null, 3) {
                it?.let {
                    fourthAdapter.dataResources.clearAddAll(it)
                    fourthAdapter.notifyDataSetChanged()
                    delegateAdapter.notifyDataSetChanged()
                }
            }
        }
        mPresenter.marketStatus {
            it?.firstOrNull { it.marketCode == "MAIN" }?.let {
                when (it.status to it.tradingSessionSubID) {
                    5 to 4, 100 to 0 -> empty(comment = "无操作，已停盘")
                    else -> industryTask = timer(null, true, period = 5000L) {
                        if (this@MarketItemFragment.userVisibleHint && this@MarketItemFragment.isVisible && currentPosition == Const.MARKET_SCAN_MARKET && ActivityUtil.isViewVisible(secondView)) {
                            taskRun()
                        }
                    }
                }
            }
        }
        taskRun()

        // 指数数据,初始化
        mPresenter.indexDataList {
            it?.let {
                firstAdapter.dataResources.set(0, it.firstOrNull { it.getShareCodeI() == "0000100" }
                        ?: firstAdapter.dataResources[0])
                firstAdapter.dataResources.set(1, it.firstOrNull { it.getShareCodeI() == "0001400" }
                        ?: firstAdapter.dataResources[1])
                firstAdapter.dataResources.set(2, it.firstOrNull { it.getShareCodeI() == "CES100" }
                        ?: firstAdapter.dataResources[2])
                firstAdapter.notifyDataSetChanged()
                delegateAdapter.notifyDataSetChanged()

                //监听ws数据(指数涨跌幅)
                if (fragmentPosition != 2) {
                    WSManager.addListener(BaseWSListener<WSExponentChangeBean>(WSExponentChangeBean::class.java, {
                        for (index in 0 until firstAdapter.dataResources.size) {
                            if (firstAdapter.dataResources[index].getShareCodeI() == it.getShareCodeI()) {
                                val removeAt = firstAdapter.dataResources.removeAt(index)
                                it.isRateUp = removeAt.netChgPrevDayPct < it.netChgPrevDayPct
                                firstAdapter.dataResources.add(index, it)

                                break
                            }
                        }
                        firstAdapter.notifyDataSetChanged()
                        delegateAdapter.notifyDataSetChanged()
                    }) {
                        firstAdapter.dataResources.map { it.getShareCodeI() }.contains(it.getShareCodeI())
                    }.also {
                        WSManager.sendText(WSControlPush(subscribe = true, msgType = WSExponentChangeBean::class.java, code = firstAdapter.dataResources.map { it.getShareCodeI() }.joinToString(separator = ",")))
                    })
                }
            }
        }
    }

    /**
     * 刷新全部/港股/新版/创业板块等部分
     */
    private fun refreshAllStock() {
        // 全部
        mPresenter.getSecurityList(null, null, 1, 6, null) {
            it?.let {
                eighthAdapter.dataResources.clearAddAll(it)
                eighthAdapter.notifyDataSetChanged()
                delegateAdapter.notifyDataSetChanged()
            }
        }

        // 主板
        mPresenter.getSecurityList(null, 3, 1, 6, null) {
            it?.let {
                tenthAdapter.dataResources.clearAddAll(it)
                tenthAdapter.notifyDataSetChanged()
                delegateAdapter.notifyDataSetChanged()
            }
        }

        // 创业板
        mPresenter.getSecurityList(null, 4, 1, 6, null) {
            it?.let {
                twelfthAdapter.dataResources.clearAddAll(it)
                twelfthAdapter.notifyDataSetChanged()
                delegateAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 刷新自選的股票列表
     */
    @Suppress("UNCHECKED_CAST")
    private fun refreshCustomShares() {
        if (fragmentPosition == 2) {
            return
        }

        if (_status_login) {
            mPresenter.optionalList(_username, _signature, 1, Const.CONST_PER_PAGE_SIZE_BIGGEST) { it ->
                it?.let {
                    it.map { it.setCollected(true) }
                    if (sharesData.isNotEmpty()) {
                        WSManager.sendText(WSControlPush(subscribe = false, msgType = WSStockMinHourBean::class.java, code = sharesData.map { it.getShareCodeI() }.joinToString(separator = ",")))
                    }
                    sharesData.clearAddAll(it)
                    sharesAdapter.notifyDataSetChanged()

                    //监听股票实时数据信息
                    if (customWSListener != null) {
                        WSManager.removeListener(listener = customWSListener!!)
                    }
                    WSManager.addListener(BaseWSListener<WSStockMinHourBean>(WSStockMinHourBean::class.java, { result ->
                        sharesData.firstOrNull { it.getShareCodeI() == result.securityCode }?.also {
                            it.isRateUp = it.realTimePrice < result.price
                            it.priceLimit = result.netChgPrevDayPct
                            it.realTimePrice = result.price
                        }
                        sharesAdapter.notifyDataSetChanged()
                    }) {
                        sharesData.map { it.getShareCodeI() }.contains(it.securityCode)
                    }.also {
                        customWSListener = it
                        WSManager.sendText(WSControlPush(subscribe = true, msgType = WSStockMinHourBean::class.java, code = sharesData.map { it.getShareCodeI() }.joinToString(separator = ",")))
                    })
                }
            }
        } else {
            sharesData.clear()
            sharesAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        // 移除定时器(行情数据)
        industryTask?.cancel()
        super.onDestroy()
    }
}


/**
 * function : viewPager適配器,針對View佈局
 *
 * Created on 2019/3/20  21:48
 * @author mnlin
 */
internal class ViewAdapter(private val datas: List<View>) : PagerAdapter() {

    override fun getCount(): Int {
        return datas.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = datas[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(datas[position])
    }
}
