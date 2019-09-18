package com.linktech.gft.bean

/**
 * function : 股票 分时数据信息
 *
 * Created on 2019/6/19  16:45
 * @author mnlin
 */
data class StockMinHour(
        var history: History = History(),
        var realTime: WSStockMinHourBean = WSStockMinHourBean()
) {
    /**
     * function : 历史数据记录
     *
     * [list] 历史记录列表数据,不包含实时当前的数据项
     *
     * Created on 2019/6/19  16:47
     * @author mnlin
     */
    data class History(
            var list: MutableList<WSStockMinHourBean> = mutableListOf(),
            var securityCode: String? = null,
            var timestamp: String? = null,
            var type: String? = null
    )
}

/**
 * function : 指数 分时图数据
 *
 * Created on 2019/6/20  15:21
 * @author mnlin
 */
data class ExponentMinHour(
        var history: History = History(),
        var realTime: WSExponentChangeBean = WSExponentChangeBean()
) {
    /**
     * function : 历史数据记录
     *
     * [list] 历史记录列表数据,不包含实时当前的数据项
     *
     * Created on 2019/6/19  16:47
     * @author mnlin
     */
    data class History(
            var list: MutableList<WSExponentChangeBean> = mutableListOf(),
            var securityCode: String? = null,
            var timestamp: String? = null,
            var type: String? = null
    )
}