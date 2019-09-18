package com.linktech.gft.bean

data class StockWarn(
        var stockType: Int = 0,
        var stockName: String?,
        var stockNum: String?,
        var currentPrice: Double = 0.00,
        var currentRate: String?,
        var topPrice: Double = 0.00,
        var topRate: String?,
        var lowerPrice: Double = 0.00,
        var lowerRate: String?
)