package com.linktech.gft.activity.financing.market


import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.IndustryListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_industry_sector.*
import kotlinx.android.synthetic.main.layout_item_industry_sector.view.*

/**
 * function---- IndustrySectorActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_IndustrySectorActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_industry_sector)
@InjectActivityTitle(title = "热门行业")
class IndustrySectorActivity : BaseActivityRecord<IndustryListBean, BasePresenter<IndustrySectorActivity>>(canRefresh = true, canLoadMore = true) {

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_industry_sector,
                listener = { _, i ->
                    routeCustom(ARouterConst.Activity_IndustryPartActivity)
                            .firstParam(adapter.dataResources[i].induName)
                            .secondParam(adapter.dataResources[i].induCode)
                            .navigation()
                }
        ) {
            tv_name.text = it.induName
            tv_code.text = it.induCode
            tv_change_rate.text = (it.induChgPct * 100).stockGetPercent()
            tv_name_back.text = it.stk.stkName
            tv_change_rate_back.text = (it.priceLimit?.toDouble() ?: 0.0).stockGetPercent()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE
        onRefresh(srl_refresh)
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)

        mPresenter.industryStockList(null, if (currentPage == 1) null else datas.lastOrNull()?.induCode, Const.CONST_PER_PAGE_SIZE_DEFAULT) {
            loadDataFinish(it)
        }
    }
}
