package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.plugins.dispatchGetColor
import com.linktech.gft.plugins.getString

/**************************************
 * function : 转入/转出 (及记录) bean
 *
 * Created on 2018/12/1  11:34
 * @author mnlin
 **************************************/

/**
 * function : 币种简略信息
 *
 * [cumulativeTransfer] Y	double	总转入量
 * [address] Y	String	钱包地址(当返回0时需要提示用户地址正在生成中)
 * [currencyName] Y	String	币种名称
 * [balance] Y	double	可用余额
 * [currencyLogo] Y	String	币种logo
 * [currencyId] Y	int	币种ID
 *
 * Created on 2018/9/26  15:01
 * @author mnlin
 */
data class TakeInBean(
        var currencyId: Int = 0,
        var cumulativeTransfer: Double = 0.0,
        var balance: Double = 0.0,
        var address: String? = null,
        var currencyName: String = Unit.getString(R.string.application_main_currency),
        var currencyLogo: String? = null
)

/**
 * function : 资产转入信息显示
 *
 * [affirm] Y	int	确认数
 * [actual] Y	double	实付款
 * [arrive] Y	double	到账
 * [arrivalTime] Y	String	到账时间
 * [success] Y	int	交易状态 0:待确认 1:已成功
 * [hash] Y	String	交易ID(hash)
 * [fromAddress] 对方地址
 *
 * Created on 2018/9/26  16:05
 * @author mnlin
 */
data class TakeInRecordBean(
        var affirm: Double = 0.0,
        var actual: Double = 0.0,
        var arrive: Double = 0.0,
        var success: Int = 0,
        var arrivalTime: String? = null,
        var hash: String? = null,
        var fromAddress: String? = null
) {
    /**
     * 状态
     */
    val statusStr: String
        get() = when (success) {
            0 -> "处理中"
            1 -> "已成功"
            else -> getString(R.string.common_unknown_value)
        }

    /**
     * 状态颜色
     */
    val statusColor: Int
        get() = dispatchGetColor(when (success) {
            0 -> R.color.toolbar_second_color_primary
            else -> R.color.black_text_171a3f
        })
}
