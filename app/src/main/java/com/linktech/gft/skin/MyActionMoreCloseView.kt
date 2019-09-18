package com.linktech.gft.skin

import android.content.Context
import android.util.AttributeSet
import com.linktech.gft.R
import com.linktech.gft.plugins.customGradientDrawable
import com.linktech.gft.plugins.dispatchGetDimen
import com.linktech.gft.plugins.dispatchGetSkinColor
import com.linktech.gft.view.ActionMoreCloseView
import org.jetbrains.anko.backgroundDrawable
import skin.support.widget.SkinCompatSupportable

/**
 * Created on 2019/3/22  14:55
 * function : toolbar 右側快捷功能按鈕
 *
 * @author mnlin
 */
class MyActionMoreCloseView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0) : ActionMoreCloseView(context, attrs, defStyleAttr, defStyleRes), SkinCompatSupportable {

    override fun applySkin() {
        setBackground()
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_action_more_close
    }

    override fun setBackground() {
        post {
            backgroundDrawable = customGradientDrawable(height, dispatchGetDimen(R.dimen.divider_line_width_1dp), dispatchGetSkinColor(R.color.dark_color_1d2037_ffffff), dispatchGetSkinColor(R.color.dark_color_454860_e5e5e5))
        }
    }

}
