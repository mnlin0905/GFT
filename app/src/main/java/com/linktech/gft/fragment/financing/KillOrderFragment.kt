package com.linktech.gft.fragment.financing


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.CardView
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.GsonBuilder
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.interfaces.SimulateDialogInterface
import com.linktech.gft.R
import com.linktech.gft.base.*
import com.linktech.gft.bean.DeputeResponse
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.encrypt
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.dark_layout_top_bar.*
import kotlinx.android.synthetic.main.dialog_kill_order_alert.view.*
import kotlinx.android.synthetic.main.layout_item_kill_order.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColorResource


/**
 * function : 撤單
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_KillOrderFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_kill_order)
@SuppressLint("SetTextI18n")
class KillOrderFragment : BaseFragmentRecordToolbar<DeputeResponse.DeputeDetail, BasePresenter<KillOrderFragment>>(true, false), OnRefreshLoadMoreListener {
    /**
     * 哪只股票
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var sharesSingle = Const.VALUE_POSITION_NULL

    /**
     * dialog
     */
    lateinit var singleDialog: SimulateDialogInterface<CardView, FrameLayout.LayoutParams>

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_kill_order,
                childListeners = listOf(
                        R.id.iv_delete means "撤單" to { holder ->
                            this.dOnClick {
                                includeDialog?.let {
                                    singleDialog.generateView().apply {
                                        tv_cancel.dOnClick {
                                            it.closeDialogsOpened(instance = singleDialog).empty(comment = "取消")
                                        }
                                        tv_confirm.dOnClick {
                                            val request = BaseStockBean<Unit, CancleRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()),
                                                    request_data = CancleRequest(seqs = mutableListOf(OrderResponse(datas[holder.currentPosition].order_seq))))
                                            val json = GsonBuilder().disableHtmlEscaping().create().toJson(request)
                                            mPresenter.stockOrderCancel(encrypt(json, _trade_key)) {
                                                datas.removeAt(holder.currentPosition)
                                                adapter.notifyItemRemoved(holder.currentPosition)
                                            }
                                            it.closeDialogsOpened(instance = singleDialog)
                                        }
                                    }
                                    it.showDialogs(instance = singleDialog, animator = AlphaIDVGAnimatorImpl())
                                }

                            }
                        }
                )
        ) {
            iv_type.imageResource = if (it.dir == 1) R.drawable.dark_icon_buy_triangle else R.drawable.dark_icon_sell_triangle
            tv_name.text = it.name + " " + it.code
            tv_time.text = it.orderdatetime
            tv_price.text = it.orderprice.toScaleString(3)
            tv_price.textColorResource = if (it.dir == 1) R.color.dark_color_f85d5a else R.color.dark_color_20bf7c
            tv_count.text = it.orderqty.toString()
            tv_count.textColorResource = if (it.dir == 1) R.color.dark_color_f85d5a else R.color.dark_color_20bf7c
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //title
        toolbar.title = getString(R.string.fragment_kill_order_title)

        //退出
        toolbar.setNavigationOnClickListener {
            baseActivity.onBackPressed()
        }

        //註冊dialog
        includeDialog?.let {
            singleDialog = generateDefaultDialog(it, R.layout.dialog_kill_order_alert)
        }
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
        val deputeRequest = BaseStockBean<Unit, DeputeRequest>(header = StockHeader(auth_code = _auth_code, system_time = System.currentTimeMillis()),
                request_data = DeputeRequest(type = "0", order_type = 2))
        val deputeJson = GsonBuilder().disableHtmlEscaping().create().toJson(deputeRequest)
        mPresenter.getDepute(encrypt(deputeJson, _trade_key)) {
            it?.apply {
                loadDataFinish(it.details)
            }
        }
    }
}
