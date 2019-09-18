package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 *  1.0.2 股票基本信息查询
 */
data class SearchStockBean(
        var stockInfo: MutableList<StockInfo> = mutableListOf()
)

/**
 *
 *
 * [id] 股票id
 * [currencyCode] 币种编码,交易区
 * [securityCode] 证券编码
 * [securityNameGccs] 证券名称 繁体中文
 * [securityNameGb] 证券名称 简体中文
 * [realTimePrice] 当前价格
 * [spreadTableCode] 档位间隔( 最小价格变动单位 )
 * [lotSize] 最小变动单位股数
 */
data class StockInfo(
        var id: Int = 0,
        var currencyCode: String? = null,
        var securityCode: String? = null,
        var securityNameGccs: String? = null,
        var securityNameGb: String? = null,
        var realTimePrice: Double = 0.0,
        var spreadTableCode: String? = null,
        var lotSize: Int = 0
) : BaseBean()

