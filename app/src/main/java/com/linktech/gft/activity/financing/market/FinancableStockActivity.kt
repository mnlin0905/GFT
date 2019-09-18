package com.linktech.gft.activity.financing.market


import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_financable_stock.*
import kotlinx.android.synthetic.main.layout_item_financable_stock.view.*

/**
 * function---- FinancableStockActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_FinancableStockActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_financable_stock)
@InjectActivityTitle(title = "可融资股票")
class FinancableStockActivity : BaseActivityRecord<OptionalListBean, BasePresenter<FinancableStockActivity>>(canRefresh = true, canLoadMore = true) {
    private var searchText: String by viewBind(R.id.et_search)

    /**
     * 当点击后跳转的界面
     */
    var onClickItemListener:(View, Int)->Unit = { _, position->
        routeCustom(ARouterConst.Activity_KLineActivity)
                .firstParam(adapter.dataResources[position])
                .navigation()
                .empty(comment = "股票k-line界面")
    }

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_financable_stock,
                listener = onClickItemListener
        ) {
            tv_name.text = it.getShareNameI()
            tv_code.text = it.getShareCodeI()
            tv_dy_rate.text = "--%"
            tv_price.text = it.realTimePrice.stockGetValue()
            tv_percent.text = it.priceLimit.stockGetPercent()

            if (tv_price.dependent == null) {
                tv_price.dependent = tv_percent
            }
        }
    }

    // 1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序 6-涨跌额升序
    private var sort: Int? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //排序
        listOf(tv_sort_rate,tv_sort_price).mapIndexed { index, tv ->
            tv.dOnClick {
                when(index){
                    0 means "漲跌幅排序" -> {
                        tv_sort_rate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 3 || sort == 4 || sort == null) R.drawable.dark_icon_sort_desc
                        else if (sort == 1) R.drawable.dark_icon_sort_asc
                        else R.drawable.dark_icon_sort_default, 0)
                        this@FinancableStockActivity.tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                        when (sort) {
                            null, 3, 4 -> sort = 1
                            1 -> sort = 2
                            2 -> sort = null
                        }
                    }
                    1 means "價格排序" -> {
                        tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 1 || sort == 2 || sort == null) R.drawable.dark_icon_sort_desc
                        else if (sort == 3) R.drawable.dark_icon_sort_asc
                        else R.drawable.dark_icon_sort_default, 0)
                        this@FinancableStockActivity.tv_sort_rate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                        when (sort) {
                            null, 1, 2 -> sort = 3
                            3 -> sort = 4
                            4 -> sort = null
                        }
                    }
                }
                srl_refresh.autoRefresh()
            }

        }
        onRefresh(srl_refresh)
        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE

        //搜索
        et_search.clearFocus()
        et_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                onRefresh(srl_refresh)
            }
            false
        }
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)

        mPresenter.getSecurityList(searchText, 2, currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT,sort) {
            loadDataFinish(it)
        }
    }
}
