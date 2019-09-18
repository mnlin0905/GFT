package com.linktech.gft.skin

import android.content.Context
import android.util.AttributeSet
import com.linktech.gft.view.TitleCenterToolbar
import skin.support.widget.SkinCompatSupportable

/**
 * Created on 2018/3/27
 * function : 标题居中的toolbar
 *
 * @author LinkTech
 */
class MyTitleCenterToolbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.support.v7.appcompat.R.attr.toolbarStyle) : TitleCenterToolbar(context, attrs, defStyleAttr), SkinCompatSupportable {
    private var skinViewHelper: SkinViewHelper? = null

    override fun applySkin() {
        if (skinViewHelper != null) {
            skinViewHelper?.applySkin()
        }
    }

    init {
        if (!isInEditMode) {
            skinViewHelper = SkinViewHelper(this, attrs)
        }
    }
}
