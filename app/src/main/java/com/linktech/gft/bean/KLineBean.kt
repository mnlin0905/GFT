package com.linktech.gft.bean

/**
 * function : 股票K-line 数据
 *
 * [dateType] : 1,//时间类型 1-日K 2-周K 3-月K
 * [securityCode] : "00001",//证券代码
 * [conversionRatio]  : 0,//换手率 三位精度
 * [highPrice]    : 2,//最高价 三位精度
 * [lowPrice] : 3,//最低价 三位精度
 * [openingPrice] : 4,//开盘价 三位精度
 * [closingPrice] : 5,//收盘价 三位精度
 * [sharesTraded] : 6,//证券交易数量
 * [turnover] : 5,//当前交易额 三位精度
 * [createTime]   : "2019-06-19 00:00:00",//时间
 * [updateTime]   : null
 *
 * Created on 2019/6/20  11:09
 * @author mnlin
 */
data class StockKLineBean(
        var closingPrice: Long = 0,
        var conversionRatio: Double = .0,
        var createTime: String? = null,
        var dateType: Int = 0,
        var highPrice: Long = 0,
        var id: Int = 0,
        var lowPrice: Long = 0,
        var openingPrice: Long = 0,
        var securityCode: Long = 0,
        var sharesTraded: Long = 0,
        var turnover: Long = 0,
        var updateTime: String? = null
)

/**
 * function : 指数 k-line 数据
 *
 * [indexType]     : 3,//指数类型 1-HSI 2-HSCEI 3-港通精选100指数
 * [dateType]     : 1,//时间类型 1-日K 2-周K 3-月K
 * [indexValue]     : 1,//当前指数值  四位精度
 * [highValue]     : 3,//最高值 四位精度
 * [lowValue]     : 2,//最低值 四位精度
 * [indexTurnover]     : 11,//当前成交额 四位精度
 * [openingValue]     : 22,//开盘值 四位精度
 * [closingValue]     : 2,/收盘值 四位精度
 * [netChgPrevDayPct]     : 0,//涨跌幅
 * [netChgPrevDay]     : 0,//涨跌额
 * [createTime]     : "2019-06-19 00:00:00",//时间
 *
 * Created on 2019/6/20  15:31
 * @author mnlin
 */
data class ExponentKLineBean(
        var closingValue: Long = 0,
        var createTime: String? = null,
        var dateType: Int = 0,
        var highValue: Long = 0,
        var id: Int = 0,
        var indexTurnover: Long = 0,
        var indexType: Int = 0,
        var indexValue: Long = 0,
        var lowValue: Long = 0,
        var netChgPrevDay: Long = 0,
        var netChgPrevDayPct: Double = .0,
        var openingValue: Long = 0,
        var updateTime: String? = null
)