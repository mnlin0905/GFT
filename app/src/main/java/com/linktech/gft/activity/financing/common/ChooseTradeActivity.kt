package com.linktech.gft.activity.financing.common

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.TradeBean
import com.linktech.gft.plugins.*
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.item_choose_trade.view.*

/**
 * function : 理財中心首頁
 *
 *
 *
 *
 * Created on 2019/3/20  16:33
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_ChooseTradeActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_VERIFY_HAS_SUCCESS)
@InjectLayoutRes(layoutResId = R.layout.activity_choose_trade)
@InjectActivityTitle(titleRes = R.string.label_choose_trade)
class ChooseTradeActivity : BaseActivityRecord<TradeBean, BasePresenter<ChooseTradeActivity>>(false, false) {

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_choose_trade,
                listener = { _, i ->
                    if (datas[i].isSELECTED_STATUS) {
                        routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                                .param(getString(R.string.activity_choose_trade_register))
                                .firstParam("https://accountapi.investassistant.com/miningaccount/accounth5/regist?channel_open=09yEsCKHFXCL2HHxPZ7OAw==&hmsr=weixin&hmpl=&hmcu=&hmkw=&hmci=")
                                .navigation()
                                .empty(comment = "應用內註冊")
                    } else {
                        toast(R.string.common_not_develop)
                    }
                }
        ) {
            Glide.with(context)
                    .load(it.traderLogo)
                    .apply(RequestOptions().circleCrop())
                    .into(iv_image)
            tv_stock_name.text = it.traderName

            //tv_tip.text = it.traderDesp
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        onRefresh(srl_refresh)
    }

    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)

        mPresenter.getTradeList{
            it?.also {
                datas.clearAddAll(it)

                // 默认第一个为zhunjia
                datas.firstOrNull()?.isSELECTED_STATUS = true

               adapter.notifyDataSetChanged()
            }
        }
    }
}
