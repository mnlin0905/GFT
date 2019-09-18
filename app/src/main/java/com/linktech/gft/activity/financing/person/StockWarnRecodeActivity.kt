package com.linktech.gft.activity.financing.person

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.StockWarn
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.window.CommonChooseDialogFragment
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.item_stock_warn_recode.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.textColorResource

/**
 * function : 理財中心首頁
 *
 *
 *
 *
 * Created on 2019/3/20  16:33
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_StockWarnRecodeActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_stock_warn_recode)
@InjectActivityTitle(titleRes = R.string.label_stock_warn_record)
class StockWarnRecodeActivity : BaseActivityRecord<StockWarn, BasePresenter<StockWarnRecodeActivity>>() {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
    }
    init {
        //必須進行適配器的初始化
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_stock_warn_recode,
                childListeners = listOf(
                        R.id.tv_delete means "刪除" to { holder ->
                            this.dOnClick {
                                val stock = datas[holder.currentPosition]
                                CommonChooseDialogFragment()
                                        .setDescribe((if (stock.stockType == 0) "Hk" else "US") + stock.stockName + "(" + stock.stockNum + ")")
                                        .setOnChooseListener(object : CommonChooseDialogFragment.OnChooseListener {
                                            override fun onRightClick() {
                                                datas.removeAt(holder.currentPosition)
                                                adapter.notifyItemRemoved(holder.currentPosition)
                                            }
                                        })
                                        .show(supportFragmentManager, "delete")
                            }
                        },
                        R.id.tv_edit means "編輯" to { holder ->
                            this.dOnClick {
                                routeCustom(ARouterConst.Activity_StockWarnActivity)
                                        .setParam(Const.KEY_COMMON_VALUE, datas[holder.currentPosition])
                                        .navigation()
                            }
                        }
                )
        ) {
            // 港股，美股
            iv_type.image = dispatchGetDrawable(if (it.stockType == 0) R.drawable.dark_icon_market_hk else R.drawable.dark_icon_market_us)
            // 名稱
            tv_stock_name.text = it.stockName
            //代碼
            tv_stock_num.text = it.stockNum
            //當前價
            tv_current_price.text = it.currentPrice.toString()
            tv_current_price.textColorResource=getStockColor(it.currentRate)
            //當前漲跌幅度
            tv_current_rate.text = it.currentRate
            tv_current_rate.textColorResource=getStockColor(it.currentRate)
            //高價
            tv_top_price.text = it.topPrice.toString()
            tv_top_rate.text = it.topRate
            //低價
            tv_lower_price.text = it.lowerPrice.toString()
            tv_lower_rate.text = it.lowerRate
        }
    }

    override fun onResume() {
        super.onResume()
        srl_refresh.autoRefresh(Const.NORMAL_ANIMATOR_DELAY_TIME)
    }

    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)
        val testData = mutableListOf<StockWarn>()
        testData.add(StockWarn(0, getString(R.string.activity_stock_warn_recode_test_zgyh), "601988", 3.80, "-0.84%", 4.01, "5%", 3.50, "3%"))
        testData.add(StockWarn(1, getString(R.string.activity_stock_warn_recode_test_zgrb), "601319", 9.99, "+5.20%", 11.50, "10%", 9.6, "4%"))
        loadDataFinish(testData)
    }
}
