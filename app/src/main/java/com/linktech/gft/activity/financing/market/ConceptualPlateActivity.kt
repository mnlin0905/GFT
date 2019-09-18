package com.linktech.gft.activity.financing.market


import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.*
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.empty
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_conceptual_plate.*

/**
 * function---- ConceptualPlateActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_ConceptualPlateActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_conceptual_plate)
@InjectActivityTitle(title = "概念行业")
class ConceptualPlateActivity : BaseActivityRecord<BaseBean, BasePresenter<ConceptualPlateActivity>>(canRefresh = true, canLoadMore = true) {

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_conceptual_plate
        ) {

        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)

        empty(TODO = "加載數據")
        loadDataFinish(mutableListOf(BaseBean(), BaseBean(), BaseBean(), BaseBean(), BaseBean(), BaseBean()))
    }
}
