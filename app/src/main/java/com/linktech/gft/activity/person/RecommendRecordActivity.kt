package com.linktech.gft.activity.person

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.InviteListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.item_recommend_record.view.*

/**
 * function : 推荐记录
 *
 * Created on 2018/10/8  18:25
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_RecommendRecordActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_recommend_record)
@InjectActivityTitle(titleRes = R.string.label_recommend_record)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF, APTPlugins.AROUTER])
class RecommendRecordActivity : BaseActivityRecord<InviteListBean, BasePresenter<RecommendRecordActivity>>(), OnRefreshLoadMoreListener {

    init {
        //必须进行适配器的初始化
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_recommend_record
        ) {
            tv_user_name.text = it.mobile ifNullOrEmpty it.email ifNullOrEmpty getString(R.string.common_unknown_user)
            tv_boolean.text = if (it.verified == 1) getString(R.string.common_yes) else getString(R.string.common_no)
            tv_register_time.text = it.create_time
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //刷新数据
        srl_refresh.autoRefresh(Const.NORMAL_ANIMATOR_DELAY_TIME)
    }

    /**
     * 刷新数据
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        mPresenter.findInvitedList(_username, _signature, currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, null) { nullAble: MutableList<InviteListBean>? ->
            loadDataFinish(nullAble)
        }
    }

    /**
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        mPresenter.findInvitedList(_username, _signature, currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, null) { nullAble: MutableList<InviteListBean>? ->
            loadDataFinish(nullAble)
        }
    }
}