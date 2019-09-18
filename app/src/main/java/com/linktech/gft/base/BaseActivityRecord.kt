package com.linktech.gft.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import com.linktech.gft.R
import com.linktech.gft.plugins.toast
import com.linktech.gft.util.Const
import com.linktech.gft.view.RecyclerViewWithEmptyView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener

/**
 * Created on 2018/12/7  11:11
 * function : 当以处理列表数据的activity
 *
 * @param <BEAN> 数据对象
 * @param <T> [BasePresenter] 类型
 * @author mnlin
</T></BEAN> */
abstract class BaseActivityRecord<BEAN, T : BasePresenter<*>>(private var canRefresh: Boolean = true, private var canLoadMore: Boolean = false) : BaseActivity<T>(), OnRefreshLoadMoreListener {
    /**
     * 固定使用的布局
     */
    protected lateinit var xrv_record: RecyclerViewWithEmptyView
    protected lateinit var srl_refresh: SmartRefreshLayout

    /**
     * 当前页数
     */
    protected var currentPage: Int = 1

    /**
     * 数据源
     * 适配器(子类必须实现该适配器,不然会异常)
     */
    protected var datas: MutableList<BEAN> = mutableListOf()
    protected lateinit var adapter: BaseRecyclerViewAdapter<BEAN>

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //数据列表
        xrv_record = findViewById<RecyclerViewWithEmptyView>(R.id.xrv_record).apply {
            layoutManager = LinearLayoutManager(this@BaseActivityRecord, LinearLayoutManager.VERTICAL, false)
            adapter = this@BaseActivityRecord.adapter
        }

        //上啦下拉
        srl_refresh = findViewById<SmartRefreshLayout>(R.id.srl_refresh).setOnRefreshLoadMoreListener(this)
        srl_refresh.setEnableLoadMore(canLoadMore)
        srl_refresh.setEnableRefresh(canRefresh)
    }

    @CallSuper
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        //请求下一跳数据
        currentPage++
        onLoadMoreAndOnRefresh(refreshLayout)
    }

    @CallSuper
    override fun onRefresh(refreshLayout: RefreshLayout) {
        //加载数据,恢复到最开始状态
        currentPage = 1
        onLoadMoreAndOnRefresh(refreshLayout)
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一个方法和参数，则实现该方法即可
     */
    protected open fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {

    }

    /**
     * 加载数据
     */
    protected open fun loadDataFinish(beans: MutableList<BEAN>?) {
        srl_refresh.finishRefresh()
        srl_refresh.finishLoadMore(Const.NORMAL_ANIMATOR_DELAY_TIME)

        beans?.apply {
            if (size == 0) {
                //最后才放置空视图
                xrv_record.emptyView = findViewById(R.id.empty_view)
                srl_refresh.setNoMoreData(true)
            }

            //如果为第一组,则清除再添加
            if (currentPage == 1) datas.clear()

            datas.addAll(this)
            adapter.notifyDataSetChanged()
        } ?: toast(R.string.common_list_error_data)
    }
}
