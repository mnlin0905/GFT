package com.linktech.gft.activity.financing.market


import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.IndustryPartBean
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_industry_part.*
import kotlinx.android.synthetic.main.layout_item_industry_part.view.*

/**
 * function---- IndustrySectorActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_IndustryPartActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_industry_part)
@InjectActivityTitle(title = "行业成分股")
class IndustryPartActivity : BaseActivityRecord<IndustryPartBean, BasePresenter<IndustryPartActivity>>(canRefresh = true, canLoadMore = true) {
    /**
     * 行业id
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var title: String? = null
    /**
     * 行业id
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_TWO, required = true)
    @JvmField
    var induCode: String = ""

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_industry_part,
                listener = { _, i ->
                    adapter.dataResources[i].assetId?.apply {
                        routeCustom(ARouterConst.Activity_KLineActivity)
                                .firstParam(OptionalListBean(securityCode = this.split(".")[0]))
                                .navigation()
                    }
                }
        ) {
            tv_name.text = it.stkName
            tv_code.text = it.assetId?.split(".")?.get(0) ?: ""
            tv_price.text = it.price.toScaleString(3)
            tv_rate.text = (it.stkChgPct?.toDouble() ?: 0.0).stockGetPercent()
        }
    }

    //  1-实时价格降序 2-实时价格升序 3-涨跌幅降序 4-涨跌幅升序
    private var sort: Int? = null
    // 0-按现价排序 1-按涨跌幅排序 2-按市值排序 3-按市盈率排序 4-按市净率排序 （默认为1）
    private var sortField: Int? = null
    // A 表示升序,即ascend D 表示降序,即descend (默认为D)
    private var sortDir: String? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        setTitle(title)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE
        //排序
        //排序
        listOf(tv_sort_price, tv_sort_rate).mapIndexed { index, tv ->
            tv.dOnClick {
                when (index) {
                    0 means "價格排序" -> {
                        tv_sort_rate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                        tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 3 || sort == 4 || sort == null) R.drawable.dark_icon_sort_desc
                        else if (sort == 1) R.drawable.dark_icon_sort_asc
                        else R.drawable.dark_icon_sort_default, 0)
                        when (sort) {
                            null, 3, 4 -> sort = 1
                            1 -> sort = 2
                            2 -> sort = null
                        }
                    }
                    1 means "漲跌幅排序" -> {
                        tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                        tv_sort_rate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 1 || sort == 2 || sort == null) R.drawable.dark_icon_sort_desc
                        else if (sort == 3) R.drawable.dark_icon_sort_asc
                        else R.drawable.dark_icon_sort_default, 0)
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
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)
        sortField = if (sort in 1..2) 0 else if (sort in 3..4) 1 else null
        sortDir = if (sort == 1 || sort == 3) "D" else if (sort == 2 || sort == 4) "A" else null
        mPresenter.industryComponentsList(induCode, Const.CONST_PER_PAGE_SIZE_DEFAULT, if (currentPage == 1) null else datas.lastOrNull()?.assetId, sortField, sortDir) {
            loadDataFinish(it)
        }
    }
}
