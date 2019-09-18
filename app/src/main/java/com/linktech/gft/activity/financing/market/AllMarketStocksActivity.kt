package com.linktech.gft.activity.financing.market


import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.skin.MyStockColorTextView
import com.linktech.gft.util.Const
import com.linktech.gft.util.ScreenUtils
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_all_market_stocks.*
import kotlinx.android.synthetic.main.item_all_market_stocks.view.*
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor
import skin.support.content.res.SkinCompatResources

/**
 * function : 全部港股
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_AllMarketStocksActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_all_market_stocks)
class AllMarketStocksActivity : BaseActivityRecord<OptionalListBean, BasePresenter<AllMarketStocksActivity>>(canRefresh = true, canLoadMore = true) {
    private var searchText: String by viewBind(R.id.et_search)

    /**
     * 全部港股不传 / 主板 3 / 创业板 4
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var stockType: Int = Const.VALUE_POSITION_NULL

    /**
     * 兩種表頭數據,成交/委託
     * key:請求類型 -> 表頭views
     *
     * item對應的格式
     */
    var headersChildren: List<TextView> = listOf()

    /**
     * 左侧 lv 的adapter
     */
    private var leftAdapter: BaseRecyclerViewAdapter<OptionalListBean>

    /**
     * 当点击后跳转的界面
     */
    var onClickItemListener: (View, Int) -> Unit = { _, position ->
        routeCustom(ARouterConst.Activity_KLineActivity)
                .firstParam(adapter.dataResources[position])
                .navigation()
                .empty(comment = "股票k-line界面")
    }

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_entrust_search,
                listener = onClickItemListener
        ) {
            if ((this as ViewGroup).tag != localClassName) {
                //如果類型不符,則需要重新填充佈局
                removeAllViews()

                // 添加其他item
                for (index in 0 until headersChildren.size) {
                    addView(createTextView(null, R.dimen.text_size_normal_14sp, R.color.skin_body1_color, isStockTextView = index == 0 || index == 1 || index == 2, fixHeight = dispatchGetDimen(R.dimen.view_height_64dp)).also {
                        if (index == 1) {
                            (getChildAt(0) as MyStockColorTextView).dependent = it as MyStockColorTextView
                        }
                    })
                }
                setTag(localClassName)
            }

            //填充數據
            for (index in 0 until this.childCount) {
                (getChildAt(index) as TextView).text = when (index) {
                    0 means "价格" -> it.realTimePrice.stockGetValue()
                    1 means "涨跌幅" -> it.priceLimit.stockGetPercent()
                    2 means "涨跌额" -> it.priceLimitAmount.stockGetValue()
                    3 means "成交量" -> it.totalQuantity.stockGetBigNumberNullable() ?: "--"
                    4 means "成交额" -> "--"
                    5 means "换手率" -> it.conversionRatio.stockGetPercent()
                    6 means "市盈率" -> it.pe ?: "--"
                    7 means "振幅" -> if (it.amplitude == null) "--" else it.amplitude + "%"
                    8 means "市值" -> it.marketCapital.stockGetBigNumberNullable() ?: "--"
                    9 means "量比" -> "--"
                    10 means "委比" -> "--"
                    else -> TODO()
                }
            }
        }

        leftAdapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_all_market_stocks,
                listener = onClickItemListener
        ) {
            tv_item_name.text = it.getShareNameI()
            tv_item_code.text = it.getShareCodeI()
            tv_item_code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dark_icon_market_hk, 0, 0, 0)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        // title
        title = when (stockType) {
            Const.VALUE_POSITION_NULL -> "全部港股"
            3 -> "主板"
            4 -> "创业板"
            else -> TODO()
        }

        //搜索
        et_search.clearFocus()
        et_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                onRefresh(srl_refresh)
            }
            false
        }

        //動態生成佈局檔
        headersChildren = getStringArray(R.array.all_market_stocks_header)
                .mapIndexed { index, text -> createTextView(text, R.dimen.text_size_normal_14sp, R.color.skin_display1_color, isSort = index in 0..2, fixHeight = dispatchGetDimen(R.dimen.prefer_view_height_48dp)) }

        //可滾動部分
        headersChildren.map { ll_header.addView(it) }

        //排序 // 1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序 6-涨跌额升序
        headersChildren.mapIndexed { index, tv ->
            when (index) {
                0 means "最新价" -> tv.dOnClick {
                    headersChildren[0].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 1 || sort == 2 || sort == 5 || sort == 6 || sort == null) R.drawable.dark_icon_sort_desc
                    else if (sort == 3) R.drawable.dark_icon_sort_asc
                    else R.drawable.dark_icon_sort_default, 0)
                    headersChildren[1].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                    headersChildren[2].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                    when (sort) {
                        1, 2, 5, 6, null -> sort = 3
                        3 -> sort = 4
                        4 -> sort = null
                    }
                    srl_refresh.autoRefresh()
                }
                1 means "涨跌幅" -> tv.dOnClick {
                    headersChildren[0].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                    headersChildren[1].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 3 || sort == 4 || sort == 5 || sort == 6 || sort == null) R.drawable.dark_icon_sort_desc
                    else if (sort == 1) R.drawable.dark_icon_sort_asc
                    else R.drawable.dark_icon_sort_default, 0)
                    headersChildren[2].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                    when (sort) {
                        3, 4, 5, 6, null -> sort = 1
                        1 -> sort = 2
                        2 -> sort = null
                    }
                    srl_refresh.autoRefresh()
                }
                2 means "涨跌额" -> tv.dOnClick {
                    headersChildren[0].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                    headersChildren[1].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                    headersChildren[2].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 1 || sort == 2 || sort == 3 || sort == 4 || sort == null) R.drawable.dark_icon_sort_desc
                    else if (sort == 5) R.drawable.dark_icon_sort_asc
                    else R.drawable.dark_icon_sort_default, 0)
                    when (sort) {
                        1, 2, 3, 4, null -> sort = 5
                        5 -> sort = 6
                        6 -> sort = null
                    }
                    srl_refresh.autoRefresh()
                }
            }
        }

        //close
        iv_close.dOnClick {
            ll_alert.visibility = View.GONE
        }

        // 查询提示
        tv_jump.dOnClick {

        }

        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE

        //左侧列表
        rv_left.apply {
            layoutManager = LinearLayoutManager(this@AllMarketStocksActivity, LinearLayoutManager.VERTICAL, false)
            adapter = leftAdapter
        }

        // 两者联动
        rv_left.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (xrv_record.computeVerticalScrollOffset() != rv_left.computeVerticalScrollOffset()) {
                    xrv_record.scrollBy(0, dy)
                }
            }
        })
        xrv_record.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (xrv_record.computeVerticalScrollOffset() != rv_left.computeVerticalScrollOffset()) {
                    rv_left.scrollBy(0, dy)
                }
            }
        })

        onRefresh(srl_refresh)
    }

    // 1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序
    private var sort: Int? = null

    /**
     * 創建text-view
     */
    private fun createTextView(txt: String?, @DimenRes dimenRes: Int, @ColorRes colorRes: Int, isSort: Boolean = false, wrapWidth: Boolean = false, fixHeight: Int = 0, isStockTextView: Boolean = false): TextView {
        return (if (isStockTextView) MyStockColorTextView(this@AllMarketStocksActivity) else TextView(this@AllMarketStocksActivity)).apply {
            textColor = SkinCompatResources.getColor(this@AllMarketStocksActivity, colorRes)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, dispatchGetDimen(dimenRes).toFloat())
            text = txt
            gravity = Gravity.CENTER
            singleLine = true
            if (isSort) setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
            if (isSort) setPadding(dispatchGetDimen(R.dimen.view_padding_margin_16dp), 0, dispatchGetDimen(R.dimen.view_padding_margin_16dp), 0)
            if (!wrapWidth) {
                maxWidth = ScreenUtils.screenWidth / 4
                minWidth = ScreenUtils.screenWidth / 4
            }

            // 固定高度
            if (fixHeight != 0) {
                minHeight = fixHeight
                maxHeight = fixHeight
            }
        }
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)
        mPresenter.getSecurityList(searchText, if (stockType == Const.VALUE_POSITION_NULL) null else stockType, currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, sort) {
            loadDataFinish(it)
            leftAdapter.notifyDataSetChanged()
        }
    }
}
