package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.base.BaseBean
import com.linktech.gft.plugins.means

/**
 * 2.5 当日/历史委托
 */
data class DeputeResponse(
        var details: MutableList<DeputeDetail> = mutableListOf()
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
    data class DeputeDetail(
            var exchange: String? = null,
            var code: String? = null,
            var name: String? = null,
            var matchqty: Int = 0,
            var orderqty: Int = 0,
            var cancelqty: Int = 0,
            var orderprice: Double = 0.0,
            var matchamt: Double = 0.0,
            var orderstatus: String? = null,
            var orderstatusval: String? = null,
            var order_seq: String? = null,
            var dir: Int = 0,
            var errormsg: String? = null,
            var ordertime: String? = null,
            var stock_info: StockAccountInfo.StockInfo? = null,
            var type: String? = null,
            var type_text: String? = null,
            var avg_trade_price: Double = 0.0,
            var orderdatetime: String? = null
    ) : BaseBean() {

        fun isCanOperate(): Int {
            return when (orderstatusval) {
                "0", "1", "2", "A", "B", "7" means "可报价；可撤单" -> 0
                "8" means "可报价" -> 1
                else -> 2  // 不可操作
            }
        }

        fun getStatusStr(): String {
            return when (orderstatusval) {
                "0", "1", "2", "A", "B" means "未报,正报,已报,待报,审批中" -> "等待成交"
                "7" means "部分成交" -> "部分成交"
                "8" means "已成交" -> "已成交"
                "5", "6" means "部分撤销,已撤" -> "已撤销"
                "3", "4" means "已报待撤,部成待撤" -> "待撤销"
                "9" means "废单" -> "废单"
                else -> ""
            }
        }

        fun getStatusIcon(): Int {
            return when (orderstatusval) {
                "0", "1", "2", "3", "4", "A", "B" means "未报,正报,已报,已报待撤,部成待撤,待报,审批中---等待成交" -> R.drawable.icon_status_wait_deal
                "7" means "部分成交" -> R.drawable.icon_status_part_deal
                "8" means "已成交" -> R.drawable.icon_status_deal
                "5", "6", "9" means "部分撤销,已撤, 废单---已撤销" -> R.drawable.icon_status_cancel
                else -> 0
            }
        }
    }
}




