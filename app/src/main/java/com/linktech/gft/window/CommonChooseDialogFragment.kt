package com.linktech.gft.window


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.*
import com.linktech.gft.R
import com.linktech.gft.plugins.dOnClick
import com.linktech.gft.plugins.dispatchGetColor
import com.linktech.gft.plugins.means
import com.linktech.gft.util.ScreenUtils
import kotlinx.android.synthetic.main.dialog_common_choose.*

/**
 * function 選擇dialog
 */
class CommonChooseDialogFragment : DialogFragment() {
    /**
     * 監聽
     */
    private var mListener: OnChooseListener? = null

    private var stock: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(activity, R.style.ActivityDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //去除標題
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //添加view
        return inflater.inflate(R.layout.dialog_common_choose, null, false)
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

        tv_describe.text = SpannableStringBuilder("確定刪除").let {
            val sbStock = SpannableStringBuilder(stock).apply {
                setSpan(ForegroundColorSpan(dispatchGetColor(R.color.dark_color_f85d5a)), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            it.append(sbStock).append("的股價提醒資訊嗎？刪除後系統將不會給您發送相關 提醒資訊")
            it.toString()
        }
    }

    fun setDescribe(stock: String): CommonChooseDialogFragment {
        this.stock = stock
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

    fun setOnChooseListener(listener: OnChooseListener): CommonChooseDialogFragment {
        this.mListener = listener
        return this
    }

    interface OnChooseListener {
        fun onLeftClick() {}
        fun onRightClick() {}
    }
}
