package com.linktech.gft.activity.financing.common

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.adapter.TradeAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.TradeBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.TradeComparator
import com.linktech.gft.window.TradeDialogFragment
import kotlinx.android.synthetic.main.activity_trade.*
import java.util.*

/**
 * function : 理財中心首頁
 *
 *
 *
 *
 * Created on 2019/3/20  16:33
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_TradeActivity,extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_VERIFY_HAS_SUCCESS)
@InjectLayoutRes(layoutResId = R.layout.activity_trade)
@InjectActivityTitle(titleRes = R.string.label_trade)
class TradeActivity : BaseActivity<BasePresenter<TradeActivity>>() {

    private lateinit var tradeAdapter: TradeAdapter
    private var tradeBeans: MutableList<TradeBean> = mutableListOf()
    private lateinit var keyComparator: TradeComparator

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        keyComparator = TradeComparator.getInstance()
        tradeAdapter = TradeAdapter(this@TradeActivity) {
            //保存券商對應的key,用於後續介面加密
            BaseApplication.tradeKey = tradeBeans[it].appKey
            if(tradeBeans[it].isSELECTED_STATUS) {
                routeCustom(ARouterConst.Activity_LoginTradeActivity)
                        .param(tradeBeans[it])
                        .navigation()
            }else{
                toast(R.string.common_not_develop)
            }
        }
        listview.adapter = tradeAdapter
        //設置右側觸摸監聽
        sidebar.setTextView(group_dialog)
        sidebar.setOnTouchingLetterChangedListener {
            val position = tradeAdapter.getPositionForSection(it[0].toInt())
            if (position != -1) {
                listview.setSelection(position)
            }
        }

        tv_tip.dOnClick {
            TradeDialogFragment()
                    .setOnChooseListener(object : TradeDialogFragment.OnChooseListener {
                        override fun onRightClick() {
                            toast(getString(R.string.activity_trade_toast_one))
                        }
                    }).show(supportFragmentManager, "trade")
        }
        getData()
    }

    /**
     * 拉取已經開過戶的券商列表
     */
    private fun getData() {
        mPresenter.getTradeList {
            it?.apply {
                tradeBeans = this

                // 默认第一个为zhunjia
                tradeBeans.firstOrNull()?.isSELECTED_STATUS = true

                Collections.sort(tradeBeans, keyComparator)
                tradeAdapter.setData(tradeBeans)
            }
        }
    }
}
