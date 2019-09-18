package com.linktech.gft.bean

/**
 *  1.0.2 股票基本信息查询
 *
 *  [id] 股票id
 *  [securityCode] 证券编码
 *  [marketCode] 市场编码
 *  [isinCode] 证券ISIN 编码
 *  [instrumentType] 证券类型 BOND(债券) EQTY(普通股) TRST(信托) WRNT(认股证)
 *  [productType] 证券产品类型
 *  [filler]
 *  [spreadTableCode] 档位间隔(最小价格变动单位)
 */
data class BaseStockInfo(
        var id: Int = 0,
        var securityCode: Int = 0,  //证券编码
        var msgType: Int = 0,        //消息类型
        var marketCode: String? = null,//市场编码
        var isinCode: String? = null,//证券ISIN 编码
        var instrumentType: String? = null, //证券类型 BOND(债券) EQTY(普通股) TRST(信托) WRNT(认股证)
        var productType: Int = 0,//证券产品类型
        var filler: String? = null,
        var spreadTableCode: String? = null,//档位间隔(最小价格变动单位)
        var securityShortName: String? = null,//证券简称
        var currencyCode: String? = null,//币种编码
        var securityNameGccs: String? = null,//证券名称繁体中文
        var securityNameGb: String? = null,//证券名称简体中文
        var lotSize: Int = 0,//最小变动单位股数
        var previousClosingPrice: Double = 0.0,//收盘价
        var priceLimit: String? = null, //涨跌幅度（%）
        var priceLimitAmount: String? = null, //涨跌金额 精度3位
        var vcmFlag: String? = null,//市场波动调节机制状态
        var shortSellFlag: String? = null,//沽空
        var casFlag: String? = null,//收市竞价交易时段
        var ccassFlag: String? = null,//是否中央结算
        var dummySecurityFlag: String? = null,//正常股票标识
        var stampDutyFlag: String? = null,//印花税指标
        var listingDate: String? = null,//上市日期
        var delistingDate: String? = null,//退市日期
        var freeText: String? = null,//关联文本
        var efnFlag: String? = null,//外汇标志 Y EFN N Non-EFN
        var accruedInterest: String? = null,//证券利息
        var couponRate: String? = null,//票面利率
        var conversionRatio: String? = null,//换手率
        var strikPrice1: String? = null,//执行价格
        var strikPrice2: String? = null,//执行价格2
        var maturityDate: String? = null,//到期时间
        var callPutFlag: String? = null,
        var style: String? = null,
        var warrantType: String? = null,//权证种类
        var callPrice: String? = null,
        var decimalsinCallPrice: String? = null,//赎回价格
        var entitlement: String? = null,//股权认证
        var decimalsInEntitlement: String? = null,
        var noWarrantsPerEntitlement: String? = null,//每份认股权证数目?
        var noUnderlyingSecurities: String? = null,
        var underlyingSecurityCode: String? = null,//5位数字的代码
        var createTime: String? = null,//时间戳
        var createDate: String? = null//记录日期
)


