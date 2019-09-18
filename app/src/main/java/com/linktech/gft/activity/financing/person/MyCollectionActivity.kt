package com.linktech.gft.activity.financing.person


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.InformationRecordBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.layout_item_my_collection.view.*

/**
 * function : 我的收藏
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_MyCollectionActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_my_collection)
@InjectActivityTitle(titleRes = R.string.label_my_collection)
class MyCollectionActivity : BaseActivityRecord<InformationRecordBean, BasePresenter<MyCollectionActivity>>(canRefresh = true, canLoadMore = true) {

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_my_collection,
                listener = { _, position ->
                    routeCustom(ARouterConst.Activity_CommonAgreementInnerActivity)
                            .param(getString(R.string.activity_common_agreement_inner_information_detail))
                            .fourthParam(datas[position].newsId)
                            .navigation()
                            .empty(comment = "点击时,通过id 获取 外网url,然后网页显示")
                },
                childListeners = listOf(
                        R.id.iv_delete to { holder ->
                            dOnClick {
                                cancelConfirmDialog(R.string.activity_my_collection_question, R.string.function_cancel, R.string.function_confirm) {
                                    mPresenter.collectionNews(datas[holder.currentPosition].newsId,_signature,_username) {
                                        toast(R.string.activity_my_collection_success)
                                        onRefresh(srl_refresh)
                                    }
                                }
                            }
                        }
                )
        ) {
            tv_title.text = it.title
            tv_time.text = it.releaseTime
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        srl_refresh.autoRefresh()
    }

    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)

        mPresenter.getCollectList(currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, _signature, _username) {
            loadDataFinish(it)
        }
    }
}
