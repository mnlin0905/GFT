package com.linktech.gft.bean

/**
 * F10  3.0.1 公司概要
 *
 * [engChairman] 集团主席名称（英）
 * [chiChairman] 集团主席名称（繁）
 * [cnChairman] 集团主席名称（简）
 * [engProfile] 公司业务（英）
 * [chiProfile] 公司业务（繁）
 * [cnProfile] 公司业务（简）
 * [engShareholder] 主要股东（英）
 * [chiShareholder] 主要股东（繁）
 * [cnShareholder] 主要股东（简）
 * [engDirector] 公司董事（英）
 * [chiDirector] 公司董事（繁）
 * [cnDirector] 公司董事（简）
 * [shareIssured] 发行数量
 * [listingDate] 上市日期
 * [mPnav] 发行价格
 */
data class CompanyProfile(
        var engChairman: String? = null,
        var chiChairman: String? = null,
        var cnChairman: String? = null,
        var engProfile: String? = null,
        var chiProfile: String? = null,
        var cnProfile: String? = null,
        var engShareholder: String? = null,
        var chiShareholder: String? = null,
        var cnShareholder: String? = null,
        var engDirector: String? = null,
        var chiDirector: String? = null,
        var cnDirector: String? = null,
        var shareIssured: Double = 0.0,
        var listingDate: String? = null,
        var mPnav: Double? = 0.0
)


/**
 * F10  3.0.2 主要指标-市盈率列表（图表数据）
 *
 * [pe] 市盈率
 * [yearend] 截止日期
 */
data class PEBean(
        var pe: Double = 0.0,
        var yearend: String? = null
)

/**
 * F10  3.0.3 主要指标-市净率最新数据
 *
 * [nav] 每股净资产
 * [pe] 市盈率
 * [eps] 每股收益
 */
data class PBBean(
        var nav: Double = 0.0,
        var pe: Double = 0.0,
        var eps: Double = 0.0,
        var currency: String? = null
)

/**
 * F10  3.0.4 利润表
 *
 * [operProfit] 营运利润
 * [pbt] 除税前盈利
 * [operExpense] 营业支出
 * [netProf] 股东应占盈利
 * [eps] 每股基本盈利
 * [turnover] 营业收入
 */
data class ProfitBean(
        var operProfit: Double = 0.0,
        var pbt: Double = 0.0,
        var operExpense: Double = 0.0,
        var netProf: Double = 0.0,
        var eps: Double = 0.0,
        var turnover: Double = 0.0,
        var unit: String? = null,
        var currency: String? = null
)

/**
 * F10  3.0.5 资产负债表
 *
 * [totalDebt] 总负债
 * [totalAss] 总资产
 * [equity] 股东权益
 */
data class SheetBean(
        var totalDebt: Double = 0.0,
        var totalAss: Double = 0.0,
        var equity: Double = 0.0,
        var unit: String? = null,
        var currency: String? = null
)

/**
 * F10  3.0.6 现金流量表最新数据
 *
 * [cfBeforeFin] 融资业务流量净额
 * [cfOperAct] 经营流量现金净额
 * [cfInv] 投资业务流量净额
 */
data class CashFlowBean(
        var cfBeforeFin: Double = 0.0,
        var cfOperAct: Double = 0.0,
        var cfInv: Double = 0.0,
        var unit: String? = null,
        var currency: String? = null,
        var rate: Double = 0.0,
        var yearend: String? = null
)

/**
 * F10  3.0.8 现金流量表最新数据
 *
 * [tdEquity] 资产负债率
 * [pb] 市净率
 * [nav] 每股净资产
 * [pe] 市盈率
 * [roEquity] 股本收益率
 * [eps] 每股收益
 * [rota] 总资产收益率
 */
data class FinanceSummaryBean(
        var tdEquity: Double = 0.0,
        var pb: Double = 0.0,
        var nav: Double = 0.0,
        var pe: Double = 0.0,
        var roEquity: Double = 0.0,
        var eps: Double = 0.0,
        var rota: Double = 0.0,
        var currency: String? = null
)

/**
 * F10  3.0.9 分红送转(派息记录)
 */
data class FHBean(
        var dfList: MutableList<BonusBean> = mutableListOf()
) {
    /**
     * [announceDate] 公布日期
     * [yearend] 年度/截至 (或财政年度)
     * [engEvent] 派息事项（英）
     * [chiEvent] 派息事项（繁）
     * [cnEvent] 派息事项（简）
     * [engParticular] 派息内容（英）
     * [chiParticular] 派息内容（繁）
     * [cnParticular] 派息内容（简）
     * [currency] 交易币种
     * [ordinaryDividend] 普通派息
     * [specialDividend] 特别派息
     * [option] 派息方式
     * [exDate] 除淨日	(或除息日)
     * [bookCloseStart] 股份过户起始日期
     * [bookCloseEnd] 股份过户截至日期
     * [payDate] 派息日
     */
    data class BonusBean(
            var eventId: String? = null,
            var announceDate: String? = null,
            var yearend: String? = null,
            var engEvent: String? = null,
            var chiEvent: String? = null,
            var cnEvent: String? = null,
            var engParticular: String? = null,
            var chiParticular: String? = null,
            var cnParticular: String? = null,
            var currency: String? = null,
            var ordinaryDividend: String? = null,
            var specialDividend: String? = null,
            var option1: String? = null,
            var exDate: String? = null,
            var bookCloseStart: String? = null,
            var bookCloseEnd: String? = null,
            var payDate: String? = null
    )
}


/**
 * 3.1.0 回购
 */
data class SBBean(
        var sbList: MutableList<BuyBackBean> = mutableListOf()
) {

    /**
     * [announceDate] 公布日期
     * [buybackdate] 回购日期
     * [highprice] 最高价
     * [lowprice] 最低价
     * [currency] 交易币种
     * [quantity] 回购数量
     */
    data class BuyBackBean(
            var eventId: String? = null,
            var stockCode: String? = null,
            var announceDate: String? = null,
            var buybackDate: String? = null,
            var highPrice: Double = 0.0,
            var lowPrice: Double = 0.0,
            var currency: String? = null,
            var quantity: Double = 0.0,
            var lastUpdate: String? = null,
            var operationdatetime: String? = null,
            var repurchaseRatio:Double=0.0
    )
}


/**
 * F10  3.1.1 股本信息
 *
 * [par] 股票面值
 * [otherIssueCap] 已发行股本（H股），港股
 * [reportCurrency] 报告币种
 * [listingDate] 上市日期
 * [parCurrency] 面值币种
 * [epsEnd] 财政年度截至日期
 * [authCap] 法定股本
 * [epsEnd] 投资业务流量净额
 * [issueCap] 全部已发行股本 ，总股本
 * [tradeCurrency] 交易币种
 * [epsStart] 财政年度起始日期
 */
data class EquityBean(
        var par: Double = 0.0,
        var otherIssueCap: Double = 0.0,
        var reportCurrency: String? = null,
        var listingDate: String? = null,
        var parCurrency: String? = null,
        var epsEnd: String? = null,
        var authCap: Double = 0.0,
        var issueCap: Double = 0.0,
        var tradeCurrency: String? = null,
        var epsStart: String? = null
)

/**
 * F10  3.1.2股东明细
 *
 * [shareHold] 持股
 * [shareholder] 股东
 * [ratio] 占比
 */
data class HoldBean(
        var shareHold: String? = null,
        var shareholder: String? = null,
        var ratio: Double = 0.0
)


