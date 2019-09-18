package com.linktech.gft.fragment.financing


import android.annotation.SuppressLint
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragmentRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.InformationRecordBean
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.layout_item_information_item_hot.view.*


/**
 * function : otc-item fragment
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_InformationItemFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_information_item)
@SuppressLint("SetTextI18n")
class InformationItemFragment : BaseFragmentRecord<InformationRecordBean, BasePresenter<InformationItemFragment>>(true, true), OnRefreshLoadMoreListener {
    /**
     * 类型id
     *
     * @link{Const.KEY_TYPE_FINANCING_INFORMATION}
     */
    @Autowired(name = Const.KEY_TYPE_FINANCING_INFORMATION, required = true)
    @JvmField
    var itemType: Int = Const.TYPE_FINANCING_INFORMATION_ALL

    /**
     * 股票信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var currentStock: OptionalListBean? = null

    /**
     * 根据参数来确定,到底需要创建什么样子的布局
     */
    override fun beforeInitAdapter() {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = when (itemType) {
                    Const.TYPE_FINANCING_INFORMATION_HOT -> R.layout.layout_item_information_item_hot
                    Const.TYPE_FINANCING_INFORMATION_HK -> R.layout.layout_item_information_item_hk
                    Const.TYPE_FINANCING_INFORMATION_7_24 -> R.layout.layout_item_information_item_7_24
                    Const.TYPE_FINANCING_INFORMATION_US -> R.layout.layout_item_information_item_hk
                    else -> R.layout.layout_item_information_item_hk
                },
                listener = { _, position ->
                    if (itemType != Const.TYPE_FINANCING_INFORMATION_7_24) {
                        routeCustom(ARouterConst.Activity_CommonAgreementInnerActivity)
                                .param(getString(R.string.activity_common_agreement_inner_information_detail))
                                .fourthParam(datas[position].id)
                                .navigation()
                                .empty(comment = "点击时,通过id 获取 外网url,然后网页显示")
                    }
                }
        ) {
            tv_title.text = it.title
            tv_time.text = it.releaseTime
            when (itemType) {
                Const.TYPE_FINANCING_INFORMATION_HOT means "热点/要闻" -> {
                    Glide.with(baseActivity)
                            .load(it.coverPicture)
                            .into(iv_picture)
                }
                Const.TYPE_FINANCING_INFORMATION_HK means "港股" -> {
                }
                Const.TYPE_FINANCING_INFORMATION_7_24 means "7 * 24" -> {
                }
            }
        }
    }

    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)

        //第一次或者強制時,會刷新刷新
        if (ifRequireInitAndResetFalse()) {
            if(canRefresh) {
                srl_refresh.autoRefresh()
            }else {
                onRefresh(srl_refresh)
            }
        }
    }

    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        //加載數據
        mPresenter.getNewsList(currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, itemType,currentStock?.getShareNameI()) {
            loadDataFinish(it)
        }
    }
}
