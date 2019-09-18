package com.linktech.gft.fragment.financing

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseBean
import com.linktech.gft.base.BaseFragmentRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectLayoutRes
import com.scwang.smartrefresh.layout.api.RefreshLayout

/**
 * function : 成分股
 *
 * Created on 2019/3/25  15:21
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_KLineComponentFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_k_line_component)
class KLineComponentFragment : BaseFragmentRecord<BaseBean,BasePresenter<KLineComponentFragment>>(true,true) {

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_k_line_component_fragment
        ) {

        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        datas.add(BaseBean())
        datas.add(BaseBean())
        datas.add(BaseBean())
        datas.add(BaseBean())
        datas.add(BaseBean())
        adapter.notifyDataSetChanged()
    }

    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)
        //第一次或者強制時,會刷新刷新
        if (ifRequireInitAndResetFalse()) {
            srl_refresh.autoRefresh()
        }
    }

    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        //查當日委託
        loadDataFinish(datas)
    }
}
