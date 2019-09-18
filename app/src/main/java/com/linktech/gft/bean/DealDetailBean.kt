package com.linktech.gft.bean

import android.graphics.drawable.Drawable
import com.linktech.gft.R
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.drawable.ColorTextDrawable
import com.linktech.gft.plugins.dispatchGetDimen
import com.linktech.gft.plugins.dispatchGetSkinColor
import com.linktech.gft.plugins.dispatchGetSkinDrawable

data class DealDetailBean(
        var pages: Int,
        var list: MutableList<DetailBean> = mutableListOf()
) {
    /**
     * [tradeTime] 交易时间
     * [price] 价格（精度向前三位）
     * [aggregateQuantity] 交易数量
     * [type] 交易涨跌色 1-涨色 2-跌色 3-灰色
     * [trdType] 0  自动对盘 菱形
     * 4   开市前成交盘 P
     * 22  非自动对盘 M
     * 100 同一证券商自动对盘 Y
     * 101 同一证券商非自动对盘 X
     * 102 碎股交易 D
     * 103 竞价交易 U
     * 104 暂不显示
     */
    data class DetailBean(
            var tradeTime: String? = null,
            var price: Long = 0,
            var aggregateQuantity: Long = 0,
            var type: Int = 0,
            var trdType: Int = -1
    ) {
        fun getTrdType(): Drawable? {
            return when (trdType) {
                0 -> dispatchGetSkinDrawable(R.drawable.icon_direction_gray)
                4 -> ColorTextDrawable(BaseApplication.app.baseContext)
                        .setText("P")
                        .setColor(dispatchGetSkinColor(R.color.dark_color_ffffff_272a3f))
                        .setTextSize(dispatchGetDimen(R.dimen.text_size_normal_14sp))
                        .setDefaultBounds()
                22 -> ColorTextDrawable(BaseApplication.app.baseContext)
                        .setText("M")
                        .setColor(dispatchGetSkinColor(R.color.dark_color_ffffff_272a3f))
                        .setTextSize(dispatchGetDimen(R.dimen.text_size_normal_14sp))
                        .setDefaultBounds()
                100 -> ColorTextDrawable(BaseApplication.app.baseContext)
                        .setText("Y")
                        .setColor(dispatchGetSkinColor(R.color.dark_color_ffffff_272a3f))
                        .setTextSize(dispatchGetDimen(R.dimen.text_size_normal_14sp))
                        .setDefaultBounds()
                101 -> ColorTextDrawable(BaseApplication.app.baseContext)
                        .setText("X")
                        .setColor(dispatchGetSkinColor(R.color.dark_color_ffffff_272a3f))
                        .setTextSize(dispatchGetDimen(R.dimen.text_size_normal_14sp))
                        .setDefaultBounds()
                102 -> ColorTextDrawable(BaseApplication.app.baseContext)
                        .setText("D")
                        .setColor(dispatchGetSkinColor(R.color.dark_color_ffffff_272a3f))
                        .setTextSize(dispatchGetDimen(R.dimen.text_size_normal_14sp))
                        .setDefaultBounds()
                103 -> ColorTextDrawable(BaseApplication.app.baseContext)
                        .setText("U")
                        .setColor(dispatchGetSkinColor(R.color.dark_color_ffffff_272a3f))
                        .setTextSize(dispatchGetDimen(R.dimen.text_size_normal_14sp))
                        .setDefaultBounds()
                else -> null
            }
        }
    }
}