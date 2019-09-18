package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean
import com.linktech.gft.plugins.ifNullOrEmpty
import java.text.DecimalFormat
import javax.annotation.Nullable

/**************************************
 * function : 股票查询列表类返回的数据
 *
 * Created on 2019/6/17  11:54
 * @author mnlin
 **************************************/

/**
 * function : 通用股票信息类,获取一下编码值,名称等信息,是否已经被收藏
 *
 * Created on 2019/6/17  11:55
 * @author mnlin
 */
interface CommonShareExponentI {
    /**
     * 获取股票在服务端存储的id值
     */
    @Nullable
    fun getShareIdI(): Int?

    /**
     * 获取股票编码
     */
    @Nullable
    fun getShareCodeI(): String?

    /**
     * 获取股票名称
     */
    @Nullable
    fun getShareNameI(): String?

    /**
     * 是否已经被收藏
     */
    @Nullable
    fun isCollectedI(): Boolean?

    /**
     * 设置收藏状态
     */
    fun setCollected(collected: Boolean)

    /**
     * 获取收盘价()
     */
    fun getClosingPrice(): Double
}

/**
 * function : 收藏列表
 *
 * [securityCode]   : 1111,  //证券编码
 * [msgType]   : 0,        //消息类型
 * [marketCode]   : "1111",//市场编码
 * [isinCode]   : null,//证券ISIN 编码
 * [instrumentType]   : null, //证券类型 BOND(债券) EQTY(普通股) TRST(信托) WRNT(认股证)
 * [productType]   : 0,//证券产品类型
 * [filler]   : null,
 * [spreadTableCode]   : null,//档位间隔(最小价格变动单位)
 * [securityShortName]   : null,//证券简称
 * [currencyCode]   : null,//币种编码
 * [securityNameGccs]   : "1112",//证券名称繁体中文
 * [securityNameGb]   : "1113",//证券名称简体中文
 * [lotSize]   : 0,//最小变动单位股数
 * [priceLimit]   : 0, //涨跌幅度（%）
 * [priceLimitAmount]   : 0, //涨跌金额 精度3位
 * [realTimePrice]   : 0, //实时价格 精度3位
 * [type]   : 0, //是否自选 0-未自选 1-已自选
 * [previousClosingPrice]   : 0,//收盘价
 * [vcmFlag]   : null,//市场波动调节机制状态
 * [shortSellFlag]   : null,//沽空
 * [casFlag]   : null,//收市竞价交易时段
 * [ccassFlag]   : null,//是否中央结算
 * [dummySecurityFlag]   : null,//正常股票标识
 * [stampDutyFlag]   : null,//印花税指标
 * [listingDate]   : 0,//上市日期
 * [delistingDate]   : 0,//退市日期
 * [freeText]   : null,//关联文本
 * [efnFlag]   : null,//外汇标志 Y EFN N Non-EFN
 * [accruedInterest]   : 0,//证券利息
 * [couponRate]   : 0,//票面利率
 * [conversionRatio]   : 0,//换手率
 * [strikPrice1]   : 0,//执行价格
 * [strikPrice2]   : 0,//执行价格2
 * [maturityDate]   : 0,//到期时间
 * [callPutFlag]   : null,
 * [style]   : null,
 * [warrantType]   : null,//权证种类
 * [callPrice]   : 0,
 * [decimalsinCallPrice]   : 0,//赎回价格
 * [entitlement]   : 0,//股权认证
 * [decimalsInEntitlement]   : 0,
 * [noWarrantsPerEntitlement]   : 0,//每份认股权证数目?
 * [noUnderlyingSecurities]   : 0,
 * [underlyingSecurityCode]   : 0,//5位数字的代码
 * [createTime]   : 1111,//时间戳
 * [createDate]   : "2019-06-10 00:00:00"//记录日期
 *
 * [id] 股票id
 * [securityCode] 证券编码
 * [type] 是否自选 0-未自选 1-已自选
 * [securityShortName] 证券简称
 * [securityNameGccs] 证券名称繁体中文
 * [securityNameGb] 证券名称简体中文
 * [realTimePrice] 实时价格
 * [priceLimit], //涨跌幅度（%）
 * [priceLimitAmount]: //涨跌金额 精度3位
 * [marketCode] 市场编码
 *
 * [turnoverRate] 换手率
 * [totalTurnOver] 成交率
 * [totalQuantity] 成交量
 * [amplitude] 振幅
 * [marketCapital] 市值
 * [pe] 市盈率
 *
 * Created on 2019/6/17  10:06
 * @author mnlin
 */
data class OptionalListBean(
        private var accruedInterest: Int = 0,
        private var callPrice: Int = 0,
        private var callPutFlag: String? = null,
        private var casFlag: String? = null,
        private var ccassFlag: String? = null,
        var conversionRatio: Double = .0,
        private var couponRate: Int = 0,
        private var createDate: String? = null,
        var createTime: Long = 0,
        var currencyCode: String? = null,
        private var decimalsInEntitlement: Int = 0,
        private var decimalsinCallPrice: Int = 0,
        private var delistingDate: Int = 0,
        private var dummySecurityFlag: String? = null,
        private var efnFlag: String? = null,
        private var entitlement: Int = 0,
        private var filler: String? = null,
        private var freeText: String? = null,
        private var id: Int = 0,
        private var instrumentType: String? = null,
        private var isinCode: String? = null,
        private var listingDate: Int = 0,
        var lotSize: Int = 0,
        var marketCode: String? = null,
        var maturityDate: Long = 0,
        private var msgType: Int = 0,
        private var noUnderlyingSecurities: Int = 0,
        private var noWarrantsPerEntitlement: Int = 0,
        var previousClosingPrice: Long = 0,
        var priceLimit: Double = .0,
        var priceLimitAmount: Long = 0,
        private var productType: Int = 0,
        private var securityCode: String? = null,
        private var securityNameGb: String? = null,
        private var securityNameGccs: String? = null,
        private var securityShortName: String? = null,
        private var shortSellFlag: String? = null,
        var spreadTableCode: String? = null,
        private var stampDutyFlag: String? = null,
        private var strikPrice1: Int = 0,
        private var strikPrice2: Int = 0,
        private var style: String? = null,
        private var type: Int = 0,
        private var underlyingSecurityCode: Int = 0,
        private var vcmFlag: String? = null,
        private var warrantType: String? = null,
        var status: Int = 0,

        var turnoverRate: String? = null,
        var totalTurnOver: String? = null,
        var totalQuantity: Long = 0,
        var amplitude: String? = null,
        var marketCapital: Long = 0,
        var pe: String? = null
) : BaseBean(), CommonShareExponentI {
    /**
     * 备用 字段:,用于处理一些特殊需求
     *
     * null 表示无变化或不清楚
     * true 表示上涨
     * false 表示下跌
     */
    var isRateUp: Boolean? = null

    /**
     * 如果实时价格为0，则返回昨天收盘价
     */
    var realTimePrice: Long = 0
        get() = if (field == 0L) previousClosingPrice else field

    override fun getShareIdI(): Int? {
        return id
    }

    override fun getShareCodeI(): String? {
        return securityCode
    }

    override fun getShareNameI(): String? {
        return securityNameGb ifNullOrEmpty securityNameGccs
    }

    override fun isCollectedI(): Boolean? {
        return type == 1
    }

    override fun setCollected(collected: Boolean) {
        type = if (collected) 1 else 0
    }

    fun getPrice(): Double {
        return realTimePrice / 1000.0
    }

    fun getCurrentPrice(): String {
        return DecimalFormat("0.000").format(realTimePrice / 1000.0)
    }

    override fun getClosingPrice(): Double {
        return previousClosingPrice / 1000.0
    }
}

/**
 * function : 热门行业列表
 *
 * [induCode]   Y	String	行业Id
 * [induName]   Y	String	行业名称
 * [induChgPct] Y	String	行业涨跌幅
 * [priceLimit] Y	String	领涨个股涨跌幅（直接加%,不用处理）
 *
 * Created on 2019/6/19  18:27
 * @author mnlin
 */
data class IndustryListBean(
        var induChgPct: Double = .0,
        var price: Long = 0,
        var induCode: String? = null,
        var induName: String? = null,
        var priceLimit: String? = null,
        var stk: Stk = Stk()
) {
    /**
     * 备用 字段:,用于处理一些特殊需求
     *
     * null 表示无变化或不清楚
     * true 表示上涨
     * false 表示下跌
     */
    var isRateUp: Boolean? = null

    /**
     * function : 股票信息
     *
     * [stkName]    Y	String	领涨股票简称
     * [assetId]    Y	String	领涨股票资产ID
     * [stype]  Y	String	领涨个股涨跌幅
     *
     * Created on 2019/6/19  18:36
     * @author mnlin
     */
    data class Stk(
            var assetId: String? = null,
            var stkName: String? = null,
            var stype: Double = .0
    )
}

/**
 * 1.0.14 行业成分股列表
 *
 * [mktVal] 市值
 * [pb] 市净率
 * [stkName] 股票名称
 * [pe] 市盈率
 * [assetId] 资产id
 * [price] 现价
 * [stkChgPct] 涨跌幅
 *
 */
data class IndustryPartBean(
        var mktVal: String? = null,
        var pb: String? = null,
        var stkName: String? = null,
        var pe: String? = null,
        var assetId: String? = null,
        var price: String? = null,
        var stkChgPct: String? = null,
        var stype: String? = null,
        var status: String? = null
)