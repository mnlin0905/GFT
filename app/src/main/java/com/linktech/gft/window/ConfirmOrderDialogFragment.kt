package com.linktech.gft.window


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.linktech.gft.R
import com.linktech.gft.plugins.dOnClick
import com.linktech.gft.plugins.means
import com.linktech.gft.util.ScreenUtils
import kotlinx.android.synthetic.main.dialog_confirm_order.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.textColorResource

/**
 * function 確認買入|賣出 下單
 */
class ConfirmOrderDialogFragment : DialogFragment() {
    /**
     * 監聽
     */
    private var mListener: OnChooseListener? = null

    /**
     *  0--買入  1--賣出
     */
    private var type: Int = 0

    private var price: String? = null
    private var num: String? = null
    private var stockName: String? = null
    private var code: String? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(activity, R.style.ActivityDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //去除標題
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //添加view
        return inflater.inflate(R.layout.dialog_confirm_order, null, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //監聽
        arrayOf(tv_left, tv_right).forEachIndexed { index, tv ->
            tv.dOnClick {
                when (index) {
                    0 means "點左邊" -> mListener?.onLeftClick()
                    1 means "點右邊" -> mListener?.onRightClick()
                }
                dialog.dismiss()
            }
        }

        //初始化
        tv_price.text = price
        tv_num.text = num
        tv_stock_name.text = stockName
        tv_stock_num.text = code
        when (type) {
            0 means "買入" -> {
                tv_price_str.text = "買入價格"
                tv_price.textColorResource = R.color.dark_color_f85d5a
                tv_num_str.text = "買入數量"
                tv_num.textColorResource = R.color.dark_color_f85d5a
                tv_right.backgroundColorResource = R.color.dark_color_f85d5a
                tv_right.text = "確定買入"

            }
            1 means "賣出" -> {
                tv_price_str.text = "賣出價格"
                tv_price.textColorResource = R.color.dark_color_20bf7c
                tv_num_str.text = "賣出數量"
                tv_num.textColorResource = R.color.dark_color_20bf7c
                tv_right.backgroundColorResource = R.color.dark_color_20bf7c
                tv_right.text = "確定賣出"
            }
        }


    }

    fun setStockCode(stockCode: String): ConfirmOrderDialogFragment {
        this.code = stockCode
        return this
    }

    fun setStockName(stockName: String): ConfirmOrderDialogFragment {
        this.stockName = stockName
        return this
    }

    fun setType(type: Int): ConfirmOrderDialogFragment {
        this.type = type
        return this
    }

    fun setPrice(price: String): ConfirmOrderDialogFragment {
        this.price = price
        return this
    }

    fun setNum(num: String): ConfirmOrderDialogFragment {
        this.num = num
        return this
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val dialog = dialog
            val window = dialog.window
            window.setGravity(Gravity.CENTER)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.setLayout((ScreenUtils.getScreenWidth(activity) * 0.75).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            window.setWindowAnimations(R.style.ActivityBottomViewAnimation)
            dialog.setCanceledOnTouchOutside(false)
        }
    }

    fun setOnChooseListener(listener: OnChooseListener): ConfirmOrderDialogFragment {
        this.mListener = listener
        return this
    }

    interface OnChooseListener {
        fun onLeftClick() {}
        fun onRightClick() {}
    }
}
