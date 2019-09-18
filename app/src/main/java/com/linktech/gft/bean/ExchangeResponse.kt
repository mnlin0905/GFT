package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 * 2.8 当日成交: exchange/getExchangeDetail
 */
data class ExchangeResponse(
        var details: MutableList<ExchangeDetail> = mutableListOf()
) {
    /**
     * [exchange] 股票交易市场 SESZ(深圳) SESH（上海）
     * [code] 股票编码
     * [name] 股票名称
     * [ordertime] 订单时间
     * [matchqty] 成交数量
     * [orderqty] 委托数量
     * [cancelqty] 取消数量
     * [orderprice] 委托价格
     * [exchange] 成交均价
     * [matchamt] 成交金额
     * [orderstatus] 订单状态，其中状态为0 1 2 7 A B的可以撤单，在客户端请求撤单接口时只返回以上6个状态的数据。
     * [orderstatusval] 订单状态value值，对应上边orderstatus的前部的数字，如0代表未报，1代表正报等
     * [errormsg] orderstatusval==“9”时，会返回错误信息，即废单原因
     * [type] 该委托下单时的订单类型的key
     * [type_text] 该委托下单时的订单类型的value
     * [dir] 买卖1买 -1卖
     */
    data class ExchangeDetail(
            var exchange_date: String? = null,
            var tdate: Long = 0,
            var match_time: Long = 0,
            var order_seq: String? = null,
            var match_qty: Int = 0,
            var match_price: Double = 0.0,
            var dir: Int = 0,
            var exchange: String? = null,
            var code: String? = null,
            var name: String? = null,
            var matchamt: Double = 0.0,
            var exchange_time: String? = null,
            var matchtype: String? = null,
            var stock_info: StockAccountInfo.StockInfo? = null
    ):BaseBean()

}




