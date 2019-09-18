package com.linktech.gft.bean

/**
 *  2.9 获取股票基本面信息
 */
data class StockBasicInfo(
        var stock_info: StockInfo? = null,
        var pre_close_px: Double = 0.0,
        var rise_and_fall_rate: String? = null,
        var rise_and_fall_value: Double = 0.0,
        var flag: Int = 0,
        var is_trade_time: Int = 0,
        var is_delay: Boolean = false,
        var finance_info: FinanceInfo? = null,
        var lombard_rate: String? = null
) {
    /**
     * [stock_id] 股票id
     * [exchange] 股票交易市场 SESZ (深圳) SESH （上海）
     * [code] 股票编码
     * [name] 股票名称
     * [short_name] 股票首字母
     * [tshares] 总股本
     * [mshares] 流通股本
     * [is_suspend] 是否停牌 0-否 1-是
     * [is_st] 0-不是st 1-是
     * [type] A A股F基金I指数
     * [is_delist] 是否退市
     * [listed_time] 上市时间
     * [stock_id] 退市时间
     * [eps] 每股收益
     * [stock_id] 退市时间
     * [time_info] 时间
     * [time_status] 1-未开盘 2-集合竞价 3-交易中 4-午盘休市 5-已收盘 6-停牌 7-退市
     * [is_warning] 是否有退市风险 0 否 1 是
     * [stock_id] 退市时间
     * [stock_id] 退市时间
     * [stock_id] 退市时间
     *
     */
    data class StockInfo(
            var stock_id: Int = 0,
            var exchange: String? = null,
            var code: String? = null,
            var name: String? = null,
            var short_name: String? = null,
            var tshares: Double = 0.0,
            var mshares: Double = 0.0,
            var is_suspend: String? = null,
            var is_st: String? = null,
            var type: String? = null,
            var is_delist: String? = null,
            var listed_time: String? = null,
            var eps: Double = 0.0,
            var time_info: String? = null,
            var time_status: Int = 0,
            var is_warning: String? = null,
            var lclose: Double = 0.0,
            var trading_unit: Double = 0.0,
            var financ: Int = 0,
            var stk_connect: Int = 0,
            var adjust_factor: Double = 0.0,
            var max52_px: Double = 0.0,
            var min52_px: Double = 0.0
    )

    /**
     * [impawn_rate] 质押率，为去掉%号的数组，比如70%时，值为70.0000
     */
    data class FinanceInfo(var impawn_rate: String? = null)

}

