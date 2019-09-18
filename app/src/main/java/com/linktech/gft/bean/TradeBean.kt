package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 *
 */
data class TradeBean(
        var id: Int = 0,
        var traderCode: String? = null,
        var traderName: String? = null,
        var traderLogo: String? = null,
        var traderDesp: String? = null,
        var traderApiUrl: String? = null,
        var appKey: String? = null,
        var appSecret: String? = null,
        var appCode: String? = null,
        var deleted: Int = 0,
        var createTime: String? = null,
        var updateTime: String? = null,
        var iconRes: Int = 0
):BaseBean() {
    fun getLetters(): String {
        return traderCode ?: "#"
    }
}