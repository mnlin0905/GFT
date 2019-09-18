package com.linktech.gft.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewManager
import com.linktech.gft.R
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.plugins.dispatchGetSkinColor
import org.jetbrains.anko.custom.ankoView
import skin.support.widget.SkinCompatImageView
import skin.support.widget.SkinCompatSupportable


/**
 * @author 小任
 * @date 2018/1/4
 * version 1.0
 * 描述:
 */
class DividerView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SkinCompatImageView(context, attrs, defStyleAttr), SkinCompatSupportable {
    var colorDrawable: ColorDrawable? = null

    init {
        getColorDrawable()
    }

    private fun getColorDrawable() {
        colorDrawable = ColorDrawable(dispatchGetSkinColor(R.color.skin_divider_color))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (mode == View.MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(resources.getDimensionPixelSize(R.dimen.divider_line_width_1dp), View.MeasureSpec.EXACTLY))
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()

        //只有当内容不存在时,才会去设置自定义的颜色
        if (drawable == null) {
            setImageDrawable(colorDrawable)
        }
    }

    override fun applySkin() {
        getColorDrawable()
        //只有当内容不存在时,才会去设置自定义的颜色
        if (drawable == null) {
            setImageDrawable(colorDrawable)
        }
    }
}

/**
 * anko添加
 */
inline fun ViewManager.dv_line(@ColorRes colorRes: Int? = null, @ColorInt color: Int? = null, init: (DividerView.() -> Unit) = {}): DividerView {
    return ankoView({ ctx ->
        DividerView(ctx, null, 0).apply {
            colorRes?.let {
                colorDrawable?.color = BaseApplication.app.dispatchGetColor(it)
            }
            color?.let {
                colorDrawable?.color = it
            }
        }
    }, theme = 0, init = init)
}

