package com.linktech.gft.bean

import com.google.gson.annotations.Expose
import com.linktech.gft.base.BaseWSBean
import com.linktech.gft.plugins.ifNullOrEmpty
import java.lang.reflect.Type

/**************************************
 * function : ws  返回数据处理
 *
 * Created on 2019/6/13  16:08
 * @author mnlin
 **************************************/

/**
 * 数据类型对应的 type 值
 */
var _wsDataMap: Map<Type, String> = mapOf(
        WSExponentChangeBean::class.java to "MarketIndex",
        WSBuySellDeepBean::class.java to "BidOfferOrders",
        WSStockStatisticsBean::class.java to "SecurityStatistics",
        WSSecurityStatusBean::class.java to "SecurityStatus",
        WSStockMinHourBean::class.java to "TimeSharing",
        WSNormalPriceBean::class.java to "NominalPrice",
        WSMarketStatus::class.java to "MarketStatus"
)

/**
 * 指数涨跌
 *
 * [securityCode] : "1",
 * [timestamp] : "2019-06-13 14:13:00",
 * [indexValue] : "27000.41", 当前指数值  四位精度
 * [easValue] : "2699988",	// 2位精度
 * [highValue] : "2733386", // 4位精度
 * [lowValue] : "2680083",	// 4位精度
 * [openingValue] : "2699977",	// 4位精度
 * [closingValue] : "2700188",// 4位精度
 * [previousSesClose] : "2655538",// 4位精度
 * [indexTurnover] : "88888899977",// 4位精度 成交量
 * [indexQuantity] : "1888888888888",
 * [netChgPrevDayPct] : "0.78",	// 涨跌幅
 * [netChgValue] : "8877",	// 涨跌额 4位精度
 * [indexTime] : "2019-06-13 14:13:00" // 指数时间
 *
 * [indexStatus] 指数状态 C  已收盘  o 开盘 T 交易中
 * [amplitude] 振幅  (无精度,直接显示即可,百分比值)
 * [indexAdv] 上涨  (无精度,直接显示即可)
 * [indexDec] 下跌  (无精度,直接显示即可)
 * [indexFlat] 平盘  (无精度,直接显示即可)
 */
data class WSExponentChangeBean(
        var closingValue: Long = 0,
        var easValue: Long = 0,
        var highValue: Long = 0,
        var indexAdv: Long = 0,
        var indexDec: Long = 0,
        var indexFlat: Long = 0,
        @Expose var indexQuantity: String? = null,
        var indexTime: String? = null,
        var indexTurnover: Long = 0,
        var lowValue: Long = 0,
        var netChgPrevDayPct: Double = .0,
        var netChgValue: Long = 0,
        var openingValue: Long = 0,
        var previousSesClose: Long = 0,
        var indexCode: String? = null,
        @Deprecated(message = "please use method:[getShareCodeI]")
        override var securityCode: String? = null,
        @Expose var timestamp: String? = null,
        @Expose var indexStatus: String? = null,
        var amplitude: Double = .0
) : BaseWSBean(), CommonShareExponentI {
    /**
     * 备用 字段:,用于处理一些特殊需求
     *
     * null 表示无变化或不清楚
     * true 表示上涨
     * false 表示下跌
     */
    var isRateUp: Boolean? = null

    var indexValue: Long = 0
        get() = if (field == 0L) previousSesClose else field

    /**
     * 指数名称
     */
    val securityCodeStr: String?
        get() = when (indexCode ifNullOrEmpty securityCode) {
            "0000100" -> "恒生指数"
            "0001400" -> "国企指数"
            "CES100" -> "港股通100"
            else -> "---"
        }

    /**
     * 指数名称(字母简称)
     */
    val securityCodeSimple: String?
        get() = when (indexCode ifNullOrEmpty securityCode) {
            "0000100" -> "HSI"
            "0001400" -> "HSCEI"
            "CES100" -> getShareCodeI()
            else -> "---"
        }

    override fun getShareIdI(): Int? {
        return when (indexCode ifNullOrEmpty securityCode) {
            "0000100" -> 1
            "0001400" -> 2
            "CES100" -> 3
            else -> null
        }
    }

    override fun getShareCodeI(): String? {
        return indexCode ifNullOrEmpty securityCode
    }

    override fun getShareNameI(): String? {
        return securityCodeStr
    }

    override fun isCollectedI(): Boolean? {
        return false
    }

    override fun setCollected(collected: Boolean) {

    }

    override fun getClosingPrice(): Double {
        return closingValue / 10000.0
    }
}

/**
 * function : k-line深度图
 *
 * [securityCode] 股票编码
 *
 * Created on 2019/6/19  10:43
 * @author mnlin
 */
data class WSBuySellDeepBean(
        var bidList: List<BuyBean> = listOf(),
        var offerList: List<SellBean> = listOf(),
        override var securityCode: String? = null,
        var timestamp: String? = null
) : BaseWSBean() {
    /**
     * [price]  挂单价格(需除1000后展示,54表示的是0.054)
     * [numberOfOrders]  当前价格总数量
     * [aggregateQuantity]  子订单总数
     */
    data class BuyBean(
            override var numberOfOrders: Long = 0,
            override var price: Long = 0,
            override var aggregateQuantity: Long = 0
    ) : BuySellI {
        /**
         * 备用 字段:,用于处理一些特殊需求
         *
         * null 表示无变化或不清楚
         * true 表示上涨
         * false 表示下跌
         */
        var isRateUp: Boolean? = null
    }

    /**
     * [price]  挂单价格(需除1000后展示,54表示的是0.054)
     * [numberOfOrders]  当前价格总数量
     * [aggregateQuantity]  子订单总数
     */
    data class SellBean(
            override var numberOfOrders: Long = 0,
            override var price: Long = 0,
            override var aggregateQuantity: Long = 0
    ) : BuySellI {
        /**
         * 备用 字段:,用于处理一些特殊需求
         *
         * null 表示无变化或不清楚
         * true 表示上涨
         * false 表示下跌
         */
        var isRateUp: Boolean? = null
    }

    /**
     * buy/sell
     *
     * [price]  挂单价格(需除1000后展示,54表示的是0.054)
     * [numberOfOrders]  当前价格总数量
     */
    interface BuySellI {
        val numberOfOrders: Long
        val price: Long
        val aggregateQuantity: Long
    }
}

/**
 * function : 股票统计数据
 *
 * [sharesTraded]   Y	String	成交量
 * [turnover]   Y	String	成交额(精度向前三位)
 * [highPrice]  Y	String	最高价(精度向前三位)
 * [lowPrice]   Y	String	最低价(精度向前三位)
 * [lastPrice]  Y	String	上一个价格(精度向前三位)
 *
 * Created on 2019/6/19  11:40
 * @author mnlin
 */
data class WSStockStatisticsBean(
        var highPrice: Long = 0,
        var lastPrice: Long = 0,
        var lowPrice: Long = 0,
        override var securityCode: String? = null,
        var sharesTraded: String? = null,
        var timestamp: String? = null,
        var turnover: Long = 0
) : BaseWSBean()

/**
 * function : 证券状态
 *
 * [status] 2: Trading Halt or Suspend ; 3  : Resume
 *
 * Created on 2019/6/19  13:41
 * @author mnlin
 */
data class WSSecurityStatusBean(
        override var securityCode: String? = null,
        var status: Int = 0,
        var timestamp: String? = null
) : BaseWSBean()

/**
 * function : 个股分时数据
 *
 * 所有价格字段精度需要向前三位
 *
 * [price]  Y	String	交易价格
 * [lastPrice]  Y	String	上一个价格
 * [turnoverRate]   Y	String	换手续
 * [tradeTime]  Y	String	交易时间
 * [securityCode]  : "1",
 * [timestamp]  : "2019-06-13 14:13:00",
 * [averagePrice]  : "2699988",	//
 * [highPrice]  : 最高价格 "2733386", // 4位精度
 * [lowPrice]  : 最低价格"2680083",	// 4位精度
 * [openingPrice] 开盘价格 : "2699977",	// 4位精度
 * [closingPrice] 收盘价格 : "2700188",// 4位精度
 * [turnover] 成交额 : "88888899977",// 4位精度
 * [quantity]  : "1_8888_8888_8888",
 * [netChgPrevDayPct] 涨跌幅度 : "0.78",	// 涨跌幅
 * [netChgValue] 涨跌额度 : "8877",	// 涨跌额 4位精度
 * [amplitude] 振幅
 * [preSesClose] 昨收
 * [totalQuantity] 成交量 整形,无精度
 *
 * [hPrice1y] 52 最高
 * [lPrice1y] 52 最低
 * [marketCapital] 总市值
 * [pe] 市盈率
 * [pb] 市净率
 * [issueCap] 总股本
 * [hkIssueCap] 港股股本
 * [hkMarketCapital] 港股市值
 *
 * Created on 2019/6/19  13:46
 * @author mnlin
 */
data class WSStockMinHourBean(
        var turnoverRate: Double = .0,
        @Expose var lastPrice: Long = 0,
        var averagePrice: Long = 0,
        @Expose var closingPrice: Long = 0,
        var highPrice: Long = 0,
        var lowPrice: Long = 0,
        var netChgPrevDayPct: Double = .0,
        var netChgValue: Long = 0,
        var openingPrice: Long = 0,
        var preSesClose: Long = 0,
        var amplitude: Double = 0.0,
        var quantity: Long = 0,
        var totalQuantity: Long = 0,
        override var securityCode: String? = null,
        @Expose var timestamp: String? = null,
        @Expose var turnover: Long = 0,
        var totalTurnOver: Long = 0,
        var tradeTime: String? = null,

        var hPrice1y: Long = 0,
        var lPrice1y: Long = 0,
        var hkIssueCap: Long = 0,
        var hkMarketCapital: Long = 0,
        var marketCapital: Long = 0,
        var issueCap: Long = 9520969416704,
        var pb: Long = 9325,
        var pe: Long = 38013
) : BaseWSBean() {
    var price: Long = 0
        get() = if (field == 0L) preSesClose else field
}

/**
 * function : 2.0.3 最佳交易价格
 *
 * [nominalPrice] 价格格式
 *
 * Created on 2019/6/19  15:44
 * @author mnlin
 */
data class WSNormalPriceBean(
        var nominalPrice: Long = 0,
        override var securityCode: String? = null
) : BaseWSBean()

/**
 * function : 订阅/取消 事件监听(只用于发送数据(不接收数据))
 *
 * Created on 2019/6/28  10:36
 * @author mnlin
 */
data class WSControlPush(
        var subscribe: Boolean = true,
        var msgType: Type,
        var code: String? = null
) {
    /**
     * 生成 json 格式字符串
     */
    fun toJson(): String? {
        return if (code.isNullOrEmpty()) null else "{\"type\":\"${if (subscribe) "subscribe" else "unsubscribe"}\",\"msgType\":\"${_wsDataMap[msgType]}\",\"code\":\"$code\"}"
    }
}

/**
 * function : 市场状态
 *
 * [marketCode] 市场编码  MAIN GEM NASD ETS
 * [tradingSessionSubID] 交易状态子状态 值定义参考OMD 20号消息
 * [status] 交易状态
 *
 * Created on 2019/7/8  14:06
 * @author mnlin
 */
data class WSMarketStatus(
        var marketCode: String? = null,
        var msgSize: Int = 0,
        var msgType: String? = null,
        var seqNum: Int = 0,
        var status: Int = 0,
        var timestamp: String? = null,
        var tradingSessionSubID: Int = 0
) : BaseWSBean() {
    /**
     * 请求code时返回市场状态
     */
    @Deprecated(message = "field maybe is error", replaceWith = ReplaceWith("marketCode"))
    override val securityCode: String?
        get() = marketCode
}

/**
 * function : 证券状态
 *
 * [status]  交易状态 2 Trading Halt or Suspend（终止） currently halted/ suspended for 3 Resume（恢复）
 *
 * Created on 2019/7/8  15:01
 * @author mnlin
 */
data class WSStockStatus(
        override var securityCode: String? = null,
        var status: Int = 0,
        var timestamp: String? = null
) : BaseWSBean()





