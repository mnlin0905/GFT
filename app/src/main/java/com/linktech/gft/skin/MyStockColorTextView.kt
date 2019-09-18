package com.linktech.gft.skin

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.linktech.gft.base.BaseView
import com.linktech.gft.plugins.valueWatcher
import skin.support.widget.SkinCompatTextView

/**
 * function : 针对股票涨跌的text-view,可以根据值 设置 文字颜色
 *
 * Created on 2019/6/13  17:08
 * @author mnlin
 */
class MyStockColorTextView : SkinCompatTextView, BaseView {
    /**
     * dependent view : change colors
     *
     * 赋值依赖,只能添加一次(在不为null的情况下)
     */
    var dependent: MyStockColorTextView? = null
        set(value) {
            // 相同则直接返回,避免过度操作
            if (field === value) {
                return
            }

            field = value

            // 添加监听
            value?.valueWatcher(checkFocus = false) {
                this@MyStockColorTextView.applySkin()
            }

            // 先更改一次显示效果(可能之前已被赋值)
            text = text
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.valueWatcher(checkFocus = false) {
            applySkin()
        }
    }

    override fun applySkin() {
        super.applySkin()
        setTextColor(when {
            text == "--" || text.isNullOrBlank() || isActivated -> dispatchGetStockColor(null)
            dependent == null -> dispatchGetStockColor(text.toString())
            dependent != null -> dispatchGetStockColor(dependent!!.text.toString())
            else -> TODO()
        })
    }

    /**
     * 如果this不依赖任何其他view,表示自己处理颜色以及+/-,因此必要时动态显示
     */
    @SuppressLint("SetTextI18n")
    override fun setText(text: CharSequence?, type: BufferType?) {
        when {
            text == "--" -> super.setText("--", type)
            TextUtils.isEmpty(text) -> super.setText("--", type)
            else -> super.setText("${getRequireOperate(text)}${text!!.replace("[+]".toRegex(), "")}", type)
        }
    }

    /**
     * 是否需要在前缀加上 + 号
     */
    private fun getRequireOperate(textC: CharSequence?): String {
        return if (textC is CharSequence && !textC.toString().contains("[-]".toRegex()) && dependent == null && !isActivated) "+" else ""
    }
}