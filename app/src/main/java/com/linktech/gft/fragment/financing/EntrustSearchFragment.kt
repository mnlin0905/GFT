package com.linktech.gft.fragment.financing

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.GsonBuilder
import com.linktech.gft.R
import com.linktech.gft.base.*
import com.linktech.gft.bean.DeputeResponse
import com.linktech.gft.bean.ExchangeResponse
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.ScreenUtils
import com.linktech.gft.util.encrypt
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.fragment_entrust_search.*
import kotlinx.android.synthetic.main.item_all_market_stocks.view.*
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalPadding

/**
 * function : 委託單查詢
 *
 * Created on 2019/3/25  19:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_EntrustSearchFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_entrust_search)
class EntrustSearchFragment : BaseFragmentRecordToolbar<BaseBean, BasePresenter<EntrustSearchFragment>>(true, false) {

    /**
     * 請求類型:成交/委託  [Const.KEY_TYPE_ENTRUST_SEARCH]
     */
    var switchType: Int = Const.TYPE_ENTRUST_SEARCH_SUCCESS

    /**
     * 兩種表頭數據,成交/委託
     * key:請求類型 -> 表頭views
     *
     * item對應的格式
     */
    var headersChildren: HashMap<Int, List<TextView>> = hashMapOf()

    /**
     * 左侧 lv 的adapter
     */
    private var leftAdapter: BaseRecyclerViewAdapter<BaseBean>

    init {

        //左边固定的头
        leftAdapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_all_market_stocks
        ) {
            when (switchType) {
                0 means "成交" -> (it as ExchangeResponse.ExchangeDetail).apply {
                    tv_item_name.text = it.name
                    tv_item_code.text = it.code
                    tv_item_code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dark_icon_market_hk, 0, 0, 0)
                }
                1 means "委託" -> (it as DeputeResponse.DeputeDetail).apply {
                    tv_item_name.text = it.name
                    tv_item_code.text = it.code
                    tv_item_code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dark_icon_market_hk, 0, 0, 0)
                }
            }
        }

        //数据
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_entrust_search
        ) {
            if ((this as ViewGroup).tag != switchType) {
                //如果類型不符,則需要重新填充佈局
                removeAllViews()
                when (switchType) {
                    Const.TYPE_ENTRUST_SEARCH_SUCCESS ->
                        headersChildren[Const.TYPE_ENTRUST_SEARCH_SUCCESS]!!
                                .map { createTextView(null, dispatchGetDimen(R.dimen.text_size_title_15sp)) }
                    Const.TYPE_ENTRUST_SEARCH_WAIT ->
                        headersChildren[Const.TYPE_ENTRUST_SEARCH_WAIT]!!
                                .map { createTextView(null, dispatchGetDimen(R.dimen.text_size_title_15sp)) }
                    else -> TODO()
                }.map {
                    addView(it)
                }
                setTag(switchType)
            }

            //填充數據
            for (index in 0 until this.childCount) {
                when (switchType) {
                    0 means "成交" -> (it as ExchangeResponse.ExchangeDetail).apply {
                        (getChildAt(index) as TextView).text = when (index) {
                            0 means "买卖标志" -> if (dir == 1) "买入" else "卖出"
                            1 means "成交价格" -> match_price.toScaleString(3)
                            2 means "成交数量" -> match_qty.toString()
                            3 means "成交金额" -> matchamt.toScaleString(2)
                            4 means "成交编号" -> order_seq
                            5 means "成交时间" -> exchange_time
                            else -> "--"
                        }
                    }
                    1 means "委托" -> (it as DeputeResponse.DeputeDetail).apply {
                        (getChildAt(index) as TextView).text = when (index) {
                            0 means "买卖标志" -> if (dir == 1) "买入" else "卖出"
                            1 means "委托价格" -> orderprice.toScaleString(3)
                            2 means "委托数量" -> orderqty.toString()
                            3 means "成交价格" -> avg_trade_price.toScaleString(3)
                            4 means "成交数量" -> matchqty.toString()
                            5 means "状态说明" -> getStatusStr()
                            6 means "委托时间" -> ordertime
                            7 means "委托编号" -> order_seq
                            8 means "报价方式" -> type_text
                            else -> "--"
                        }
                    }
                }
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //退出
        toolbar.setNavigationOnClickListener {
            baseActivity.onBackPressed()
        }

        //切換
        rg_success_wait.setOnCheckedChangeListener { group, checkedId ->
            switchType = when (checkedId) {
                R.id.rb_success -> Const.TYPE_ENTRUST_SEARCH_SUCCESS
                R.id.rb_wait -> Const.TYPE_ENTRUST_SEARCH_WAIT
                else -> TODO()
            }
            //刷新數據
            ll_header.removeAllViews()
            headersChildren[switchType]?.map { ll_header.addView(it) }
            srl_refresh.autoRefresh()
        }

        //動態生成佈局檔
        headersChildren[Const.TYPE_ENTRUST_SEARCH_SUCCESS] =
                getStringArray(R.array.entrust_search_time_success_header)
                        .map { createTextView(it, dispatchGetDimen(R.dimen.text_size_13sp)) }
        headersChildren[Const.TYPE_ENTRUST_SEARCH_WAIT] =
                getStringArray(R.array.entrust_search_time_wait_header)
                        .map { createTextView(it, dispatchGetDimen(R.dimen.text_size_13sp)) }

        //可滾動部分
        headersChildren[switchType]?.map { ll_header.addView(it) }

        //左侧列表
        rv_left.apply {
            layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = leftAdapter
        }

        // 两者联动
        rv_left.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (xrv_record.computeVerticalScrollOffset() != rv_left.computeVerticalScrollOffset()) {
                    xrv_record.scrollBy(0, dy)
                }
            }
        })
        xrv_record.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (xrv_record.computeVerticalScrollOffset() != rv_left.computeVerticalScrollOffset()) {
                    rv_left.scrollBy(0, dy)
                }
            }
        })

        onRefresh(srl_refresh)
    }

    /**
     * 創建text-view
     */
    private fun createTextView(txt: String?, tSize: Int): TextView {
        return TextView(baseActivity).apply {
            textColor = dispatchGetColor(R.color.dark_main_text_7c7fa2)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, tSize.toFloat())
            text = txt
            gravity = Gravity.CENTER
            singleLine = true
            verticalPadding = dispatchGetDimen(R.dimen.view_padding_margin_14dp)
            maxWidth = ScreenUtils.screenWidth / 4
            minWidth = ScreenUtils.screenWidth / 4
        }
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        when (switchType) {
            Const.TYPE_ENTRUST_SEARCH_SUCCESS means "成交" -> {
                val exchangeRequest = BaseStockBean<Unit, ExchangeDetailRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()), request_data = ExchangeDetailRequest("0"))
                val exchangeJson = GsonBuilder().disableHtmlEscaping().create().toJson(exchangeRequest)
                mPresenter.getExchangeDetail(encrypt(exchangeJson, _trade_key)) {
                    it?.apply {
                        loadDataFinish(it.details)
                        leftAdapter.notifyDataSetChanged()
                    }
                }
            }
            Const.TYPE_ENTRUST_SEARCH_WAIT means "委托" -> {
                val deputeRequest = BaseStockBean<Unit, DeputeRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()),
                        request_data = DeputeRequest(type = "0"))
                val deputeJson = GsonBuilder().disableHtmlEscaping().create().toJson(deputeRequest)
                mPresenter.getDepute(encrypt(deputeJson, _trade_key)) {
                    it?.apply {
                        loadDataFinish(it.details)
                        leftAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
