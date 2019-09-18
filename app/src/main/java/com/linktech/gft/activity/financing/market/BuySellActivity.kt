package com.linktech.gft.activity.financing.market


import android.annotation.SuppressLint
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.DealDetailBean
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.TimeTool
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_buy_sell.*
import kotlinx.android.synthetic.main.layout_item_buy_sell.view.*
import org.jetbrains.anko.imageResource

/**
 * function---- BuySellActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_BuySellActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_buy_sell)
@InjectActivityTitle(title = "逐笔明细")
@SuppressLint("SetTextI18n")
class BuySellActivity : BaseActivityRecord<DealDetailBean.DetailBean, BasePresenter<BuySellActivity>>(canRefresh = true, canLoadMore = true) {
    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var currentStock: OptionalListBean = OptionalListBean()

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_buy_sell
        ) {
            tv_time.text = TimeTool.dateToOnlyTime(it.tradeTime)
            iv_deal.setImageDrawable(it.getTrdType())
            tv_price.text = it.price.stockGetValue()
            tv_price.dependent = tv_change_price
            tv_amount.text = it.aggregateQuantity.toString()
            iv_type.imageResource = when {
                it.type == 1 && is_red_up -> R.drawable.icon_direction_up_red
                it.type == 1 && !is_red_up -> R.drawable.icon_direction_up_green
                it.type == 2 && is_red_up -> R.drawable.icon_direction_down_green
                it.type == 2 && !is_red_up -> R.drawable.icon_direction_down_red
                else -> R.drawable.icon_direction_gray
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        currentStock?.apply {
            //股票编号
            tv_stock_code.text = getShareCodeI()
            //股票名字
            tv_stock_name.text = getShareNameI()
            //当前价格
            tv_current_price.text = realTimePrice.stockGetValue()
            //涨跌价格
            tv_change_price.text = priceLimitAmount.stockGetValue()
            //涨跌幅度
            tv_change_rate.text = priceLimit.stockGetPercent()
            // 颜色依赖项
            listOf(tv_current_price, tv_change_price, tv_change_rate).map {
                it.dependent = tv_change_price
            }
            srl_refresh.autoRefresh()
        }
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)
        mPresenter.transactionDetailList(currentStock.getShareCodeI(), currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT) {
            loadDataFinish(it?.list)
        }
    }
}
