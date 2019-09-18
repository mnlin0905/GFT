package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.plugins.getString

/**
 * function : 资产,转入转出记录,bean
 *
 * Created on 2018/11/30  11:17
 * @author mnlin
 */

/**
 * function : 最近交易信息
 *
 * [affirm] Y	int	确认数
 * [actual] Y	double	实付款
 * [arrive] Y	double	到账
 * [arrivalTime] Y	String	到账时间
 * [success] Y	int	交易状态 0:待确认 1:已成功
 * [hash] Y	String	交易ID(hash)
 * [type] Y	int	0:转入 1:转出
 * [id] Y	int	交易主键ID
 * [userId] Y	int	用户ID
 *
 * Created on 2018/11/30  11:20
 * @author mnlin
 */
data class RecentTransferRecordBean(
        var affirm: String? = null,
        var actual: String? = null,
        var amount: String? = null,
        var createTime: String? = null,
        var arrive: String? = null,
        var arrivalTime: String? = null,
        var success: Int = 0,
        var id: Int = 0,
        var type: Int = 0,
        var userId: Int = 0,
        var hash: String? = null
) {
    /**
     * 是否为抓如
     */
    val isTakeIn: Boolean
        get() = type == 0

    /**
     * 交易类型/字符串
     */
    val typeStr: String
        get() = when(type){
            0 -> "收款"
            1 -> "转账"
            else -> getString(R.string.common_unknown_value)
        }
}