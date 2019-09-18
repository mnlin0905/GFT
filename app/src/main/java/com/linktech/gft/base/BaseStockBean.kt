package com.linktech.gft.base

/**
 * 外层的bean ，请求的和返回的共用
 */
class BaseStockBean<P : Any, Q : Any>(var header: StockHeader? = null,
                                      var response_data: P? = null,
                                      var request_data: Q? = null
) : BaseHttpBean() {
    override fun getCode(): Int {
        return header?.response_code ?: -1
    }

    override fun getMsg(): String {
        return header?.error_message ?: "未知错误"
    }
}

/**
 * 请求和返回的共用header
 */
open class StockHeader(
        var version: Int = 1,
        var auth_code: String? = null,
        var system_time: Long = 0,
        var response_code: Int? = null,
        var error_message: String? = null,
        var uuid: String = "cmscc6c6f5b-5310-ba6f-37bb-1ef02415ca91",
        var ua: StockHeaderUa = StockHeaderUa()
)

/**
 *  Header 里面的ua
 */
data class StockHeaderUa(
        var platform: String = "android",
        var model: String = "safari/604.1",
        var os_version: String = "win",
        var width: Int = 720,
        var height: Int = 1360,
        var app_version: String = "1.7.43", //这个字段值改了登录就没数据
        var trader: String = "mining_t",
        var language: String? = "zh_CN"
)

/**************************************************************封装请求体********************************************************************/

/**
 * 2.2 请求账户信息
 *
 * [including_posi]  是否包含持仓，Null或者0 不包含；  1 包含
 */
data class AccountRequest(var including_posi: String? = null)

/**
 * 2.3 请求下单
 *
 * [exchange] 交易市场
 * [code] 股票编码
 * [type] 增强限价单 - EnhancedLimitedPrice；限价单 - LimitedPrice；特别限价单 - SpecialLimitedPrice；市价单 - MarketPrice；新股买入时为限价单 - LimitPrice
 * [dir] 买卖标志：1 --普通交易买入   -1 --普通交易卖出
 * [price] 下单价格
 * [qty] 股数
 */
data class OrderRequest(var exchange: String = "HKEX",
                        var code: String? = null,
                        var type: String? = null,
                        var dir: Int = 0,
                        var price: String? = null,
                        var qty: String? = null
)

/**
 * 2.4 请求搜索股票
 *
 * [code] 股票名称、首字母或者股票编号
 * [include_new_stock] 是否包含新股
 * [market] 默认为全部搜索   A  A股，HK 港股，US美股，三种股票用逗号分割，自由组合，比如A,US则只搜索A股和美股
 * [include_stock_connect] 是否包含沪港通深港通等股票,逗号分隔，不传为都不包含
 */
data class SearchStockRequest(
        var code: String,
        var include_new_stock: Int = 0,
        var market: String = "HK",
        var include_stock_connect: String = "ght,gst"
)

/**
 * 2.5 请求当日/历史委托 ,获取可撤单列表
 *
 * [type] 0: 当天 1: 历史（目前不支持历史查询，所以目前只能传0）
 * [order_type] 1：查询订单状态（所有委托) 2：为查询可撤单列表，为2 时只有type=0 时有效
 * [query_num] 每次取多少条,默认20，为0 时取满足min_order_seq和max_order_seq的所有
 */
data class DeputeRequest(
        var type: String = "0",
        var order_type: Int? = null,
        var query_num: Int = 0
)

/**
 * 2.6 撤单
 */
data class CancleRequest(var seqs: MutableList<OrderResponse> = mutableListOf())

/**
 * 2.7 请求交易类型
 *
 * [trade_type_contury]  HK 港股；  US 美股； A中华通
 */
data class TradeTypeRequest(var trade_type_contury: String? = null)


/**
 * 2.8 当日成交
 *
 * [type]  0: 当天   1: 历史，为1 时起始结束时间有效，查询历史交易记录时不包含当天交易记录（即使结束时间设置成当天也无效),目前不支持历史查询，只能传0）
 */
data class ExchangeDetailRequest(var type: String? = null)

/**
 * 2.9 获取股票基本面信息
 *
 * [code]  股票编码
 * [exchange]  股票交易市场   SESZ(深圳)   SESH（上海）  HKEX(香港）  NASDAQ(美股)  NYSE(美股）
 */
data class StockInfoRequest(
        var code: String? = null,
        var exchange: String? = null
)

/**
 * 2.14 获取新股申购列表（包含即将上市和已上市）
 * [data_type]  0:获取新股认购列表; 2:获取新股已上市列表；	默认0
 */
data class NewStockRequest(
        var data_type: Int = 0
)


/**************************************************************封装返回体(数据量比较大的放在外面)********************************************************************/

/**
 * 1.0.2 尊嘉用户登录
 */
data class LoginTradeResponse(
        var header: StockHeader = StockHeader(),
        var response_data: ResponseData = ResponseData()
) {
    data class ResponseData(
            var type: String? = null
    )
}

/**
 * 2.3 返回下单
 *
 * [order_seq] 订单编号
 */
data class OrderResponse(var order_seq: String? = null)

/**
 * 2.7 获取交易类型
 */
data class TradeTypeResponse(
        var trade_types: MutableList<Type> = mutableListOf()
) {
    data class Type(
            var key: String? = null,
            var value: String? = null
    )
}
