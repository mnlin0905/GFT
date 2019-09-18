package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 * 账户信息
 */
data class StockAccountInfo(
        var assets: MutableList<Asset> = mutableListOf(),
        var positions: MutableList<Hold> = mutableListOf()
) {
    /**
     * 资产
     *
     * [total_assets]  总资产
     * [net_assets]  净资产（资产净值）
     * [usable]  最大购买力
     * [withdraw]  可用资金
     * [stock_assets]  总市值
     * [usable_rate]  可用占比，分母为总资产
     * [stock_rate]  股票市值占比，分母为总资产
     * [currency_stock_rate]  某币种市场占总市值的比例
     * [daily_value]  日收益
     * [daily_value_str]  字符串格式的日收益，解决ios不能识别null的问题
     * [daily_rate]  分母为交易前总资产
     * [daily_stock_rate]  分母为交易前股票总市值
     * [total_float_value]  持仓盈亏
     * [progress_status]  进度0未加载完成1加载完成
     * [transfer]  银证转账总额正为转入资金账户负为转出资金账户，内部使用。
     * [repayment]  需还款金额
     * [frozen]  冻结
     * [frozen_rate]  冻结资产占比
     * [ex_rate]  汇率，均是对港币的汇率
     * [currency]  币种（枚举），默认人民币CNH 港币HKD 美金 USD，汇总账户为T_CNH或者T_HKD或者T_USD
     * [ex_rate_string]  汇率（字符串），均是对港币的汇率
     * [total_float_value]  持仓盈亏（分币种下是各个币种对应的持仓盈亏，总览下是所有持仓换算汇率之后的持仓盈亏）
     * [margin_limit]  最大信用额
     * [margin_value]  可按揭金额
     * [margin_percent]  风险值（返回带百分号的字符串）
     * [debt_assets]  欠款金额
     */
    data class Asset(
            var total_assets: Double = 0.0,
            var net_assets: Double = 0.0,
            var net_cash: Double = 0.0,
            var usable: Double = 0.0,
            var use_buy_power: Double = 0.0,
            var withdraw: Double = 0.0,
            var stock_assets: Double = 0.0,
            var stock_rate: String? = null,
            var usable_rate: String? = null,
            var daily_value: Double = 0.0,
            var daily_value_str: String? = null,
            var daily_rate: String? = null,
            var daily_stock_rate: String? = null,
            var progress_status: String? = null,
            var debt_assets: Double = 0.0,
            var pure_assets: Double = 0.0,
            var financing_assets: Double = 0.0,
            var security_assets: Double = 0.0,
            var financing_interest: Double = 0.0,
            var security_interest: Double = 0.0,
            var debt_rate: String? = null,
            var pure_rate: String? = null,
            var margin_percent: String? = null,
            var margin_value: Double = 0.0,
            var margin_limit: Double = 0.0,
            var financing_assets_rate: String? = null,
            var security_assets_rate: String? = null,
            var transfer: Double = 0.0,
            var repayment: Double = 0.0,
            var frozen: Double = 0.0,
            var ipo_frozen: Double = 0.0,
            var depute_frozen: Double = 0.0,
            var ex_rate: Double = 0.0,
            var ex_rate_string: String? = null,
            var currency: String? = null,
            var total_float_value: String? = null,
            var advisable_funds: Double = 0.0
    )

    /**
     * 持仓
     *
     *
     */
    data class Hold(
            var name: String? = null,
            var code: String? = null,
            var exchange: String? = null,
            var last_price: Double = 0.0,
            var cost_price: Double = 0.0,
            var stkavl: Int = 0,
            var stkbuy: Int = 0,
            var stksale: Int = 0,
            var tasset: Double = 0.0,
            var rise_and_fall_rate: String? = null,
            var rise_and_fall_value: Double = 0.0,
            var positions_rate: String? = null,
            var stkqty: Int = 0,
            var daily_value: Double = 0.0,
            var daily_rate: String? = null,
            var stock_info: StockInfo? = null,
            var progress_status: String? = null,
            var positions_type: Int = 0,
            var is_warning: String? = null,
            var currency: String? = null
    ):BaseBean()

    /**
     * 股票信息
     */
    data class StockInfo(
            var stock_id: Int = 0,
            var exchange: String? = null,
            var code: String? = null,
            var name: String? = null,
            var is_suspend: String? = null,
            var type: String? = null,
            var is_warning: String? = null,
            var financ: Int = 0
    )
}

