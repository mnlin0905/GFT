package com.linktech.gft.window

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.linktech.gft.R
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.base.TradeTypeResponse
import com.linktech.gft.plugins.clearAddAll
import kotlinx.android.synthetic.main.dialog_fragment_price_type.*
import kotlinx.android.synthetic.main.item_price_type.view.*

/**
 * Created on 2018/1/15
 * function : 设置性别
 *
 * @author LinkTech
 */

class PriceTypeDialogFragment : DialogFragment() {

    private var listener: OnChangePriceListener? = null
    /**
     * 交易类型
     */
    private var tradeTypes: MutableList<TradeTypeResponse.Type> = mutableListOf()

    private var priceAdapter = BaseRecyclerViewAdapter(
            dataResources = tradeTypes,
            layoutResId = R.layout.item_price_type,
            listener = { _, position ->
                if (listener?.onClickPrice(position) == false) {
                    dialog.dismiss()
                }
            }
    ) {
        tv_price_str.text = it.value
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var a = { a: Int, b: Boolean ->

        }
        //去除标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //添加view
        return inflater.inflate(R.layout.dialog_fragment_price_type, null, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(activity, R.style.ActivityDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val dialog = dialog
            val window = dialog.window
            val attributes = window!!.attributes
            window.attributes = attributes
            window.setGravity(Gravity.BOTTOM)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                    .FLAG_FULLSCREEN)
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setWindowAnimations(R.style.ActivityBottomViewAnimation)
            dialog.setCanceledOnTouchOutside(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = priceAdapter
            priceAdapter.notifyDataSetChanged()
        }
    }

    fun setTradeType(tradeTypes: MutableList<TradeTypeResponse.Type>): PriceTypeDialogFragment {
        this.tradeTypes.clearAddAll(tradeTypes)
        return this
    }

    fun setOnChangePriceListener(listener: OnChangePriceListener): PriceTypeDialogFragment {
        this.listener = listener
        return this
    }

    /**
     * 当点击弹出框中按钮时进行回调
     */
    interface OnChangePriceListener {
        /**
         * 返回true表示不需要默认操作
         */
        fun onClickPrice(position: Int): Boolean
    }
}
