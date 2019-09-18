package com.linktech.gft.activity.financing.common

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.gavin.com.library.PowerfulStickyDecoration
import com.gavin.com.library.listener.PowerGroupListener
import com.google.gson.GsonBuilder
import com.linktech.gft.R
import com.linktech.gft.base.*
import com.linktech.gft.bean.CommonBean
import com.linktech.gft.bean.NewStockResponse
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.ViewUtil
import com.linktech.gft.util.encrypt
import kotlinx.android.synthetic.main.activity_new_stock.*
import kotlinx.android.synthetic.main.item_ipo.view.*
import kotlinx.android.synthetic.main.item_new_stock.view.*
import kotlinx.android.synthetic.main.layout_iop.*
import kotlinx.android.synthetic.main.layout_new_stock.*

@Route(path = ARouterConst.Activity_NewStockActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_new_stock)
@InjectActivityTitle(title = "")
class NewStockActivity : BaseActivity<BasePresenter<NewStockActivity>>() {

    /**
     * 當前第n個fragment處於最前
     */
    private var currentPosition: Int = 0
    private var vsNew: View? = null
    private var vsIpo: View? = null
    private var newsDatas = mutableListOf<CommonBean>()
    private var ipoDatas = mutableListOf<NewStockResponse.BeingStock>()
    private var newAdapter: BaseRecyclerViewAdapter<CommonBean>
    private lateinit var ipoAdapter: BaseRecyclerViewAdapter<NewStockResponse.BeingStock>

    init {
        newAdapter = BaseRecyclerViewAdapter(
                dataResources = newsDatas,
                layoutResId = R.layout.item_new_stock
        ) {
            it.apply {
                tv_name.text = getStockName()
                tv_code.text = "${getStockCode()}.HK"
                tv_one_value.text = getValueOne()
                tv_two_value.text = getValueTwo()
                tv_three_value.text = getValueThree()
                when (getType()) {
                    0 -> {
                        tv_one_text.text = "招股价"
                        tv_two_text.text = "最低认购额度"
                        tv_three_text.text = "认购截止時間"
                    }
                    1 -> {
                        tv_one_text.text = "发行价"
                        tv_two_text.text = "上市日期"
                        tv_three_text.text = "暗盘交易日"
                    }
                }
            }
        }

        ipoAdapter = BaseRecyclerViewAdapter(
                dataResources = ipoDatas,
                layoutResId = R.layout.item_ipo,
                listener = { view, position ->
                    ipoDatas.forEachIndexed { index, deputeDetail ->
                        if (index == position) {
                            deputeDetail.isSELECTED_STATUS = !deputeDetail.isSELECTED_STATUS
                        } else {
                            deputeDetail.isSELECTED_STATUS = false
                        }
                    }
                    ipoAdapter.notifyDataSetChanged()
                },
                childListeners = listOf(
                        R.id.tv_publish_data means "发行资料" to { hold ->
                            this.dOnClick {
                                routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                                        .param(ipoDatas[hold.currentPosition].name)
                                        .firstParam(ipoDatas[hold.currentPosition].request_the_url)
                                        .navigation()
                            }
                        },
                        R.id.tv_show_detail means "查看行情" to { hold ->
                            this.dOnClick {
                                routeCustom(ARouterConst.Activity_KLineActivity)
                                        .firstParam(OptionalListBean(securityCode = ipoDatas[hold.currentPosition].code))
                                        .navigation(baseActivity)
                            }
                        }
                )
        ) {
            tv_stock_name.text = it.name
            tv_stock_code.text = it.code
            tv_date.text = it.list_date
            tv_price.text = it.issue_price.toScaleString(3)
            cl.visibility = if (it.isSELECTED_STATUS) View.VISIBLE else View.GONE
        }
    }


    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        vsNew = vs_new_share.inflate()
        //自選/市場
        rg.setOnCheckedChangeListener { _, checkedId ->
            when {
                checkedId == R.id.rb_new_stock && currentPosition != 0 -> {
                    currentPosition = 0
                    vsNew!!.visibility = View.VISIBLE
                    vsIpo?.visibility = View.GONE
                    empty_view_new.visibility = if (newsDatas.size == 0) View.VISIBLE else View.GONE
                }
                checkedId == R.id.rb_ipo && currentPosition != 1 -> {
                    currentPosition = 1
                    vsNew!!.visibility = View.GONE
                    if (vsIpo == null) {
                        vsIpo = vs_ipo.inflate()
                        initIpo()
                        getIpo()
                    } else vsIpo!!.visibility = View.VISIBLE
                }
            }
        }

        //recycler
        srl_refresh_new.setEnableLoadMore(false)
        srl_refresh_new.setEnableRefresh(true)
        xrv_record_new.apply {
            layoutManager = LinearLayoutManager(this@NewStockActivity, LinearLayoutManager.VERTICAL, false)
            adapter = newAdapter
        }

        //加悬浮头
        val groupListener = object : PowerGroupListener {
            override fun getGroupName(position: Int): String {
                return when (newsDatas[position].getType()) {
                    0 -> "今日可申购"
                    else -> "待上市股份"
                }
            }

            override fun getGroupView(position: Int): View {
                val v = layoutInflater.inflate(R.layout.item_news_decoration, null, false)
                v.findViewById<TextView>(R.id.tv_title).text = when (newsDatas[position].getType()) {
                    0 -> "今日可申购"
                    else -> "待上市股份"
                }
                return v
            }
        }
        val decoration = PowerfulStickyDecoration.Builder
                .init(groupListener)
                .setGroupHeight(ViewUtil.Dp2Px(baseActivity, 44f))
                .build()
        xrv_record_new.addItemDecoration(decoration)
        getNews()
    }

    private fun initIpo() {
        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE
        srl_refresh_ipo.setEnableLoadMore(false)
        srl_refresh_ipo.setEnableRefresh(true)
        xrv_record_ipo.apply {
            layoutManager = LinearLayoutManager(this@NewStockActivity, LinearLayoutManager.VERTICAL, false)
            adapter = ipoAdapter
        }
    }

    /**
     * 拿可申购新股列表和即将上市的
     */
    private fun getNews() {
        //拿今日可申购和即将上市的
        val requestOne = BaseStockBean<Unit, NewStockRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()), request_data = NewStockRequest(0))
        val jsonOne = GsonBuilder().disableHtmlEscaping().create().toJson(requestOne)
        mPresenter.getOnLineStockList(encrypt(jsonOne, _trade_key)) {
            it?.apply {
                if (to_buy_list.size == 0 && to_be_listed.size == 0) {
                    xrv_record_new.emptyView = empty_view_new
                } else {
                    empty_view_new.visibility = View.GONE
                }
                newsDatas.clear()
                newsDatas.addAll(to_buy_list)
                newsDatas.addAll(to_be_listed)
                newAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 拿已上市和即将上市
     */
    private fun getIpo() {
        //拿已上市的
        val requestTwo = BaseStockBean<Unit, NewStockRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()), request_data = NewStockRequest(2))
        val jsonTwo = GsonBuilder().disableHtmlEscaping().create().toJson(requestTwo)
        mPresenter.getOnLineStockList(encrypt(jsonTwo, _trade_key)) {
            it?.apply {
                if (to_be_listed.size == 0) {
                    xrv_record_ipo.emptyView = empty_view_new
                } else {
                    empty_view_new.visibility = View.GONE
                }
                ipoDatas.clear()
                ipoDatas.addAll(to_be_listed)
                ipoAdapter.notifyDataSetChanged()
            }
        }
    }
}


