package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 * 如果是新股APP不需要再请求股票信息了，直接使用new_stock_info中的数据。
 *
 * stock_id	1	Integer	股票id
 * code	1	String	股票编码
 * exchange	1	String	股票交易市场SESZ(深圳) SESH（上海）
 * 【用于服务端统计，终端可不显示】
 * name	1	String	股票名称、
 * type	1	String	指数类型：A A股F基金I指数
 * is_delist	1	String	是否退市1 退市 0 不退市
 * flag	1	Integer	该用户是否已选择该股票，1已选择0未选择
 * is_new_stock	?	String	是否是可申购的新股1是0不是。
 * new_stock_info	?	Object	新股申购信息
 */
data class SearchStockResponse(
        var stock_id: Int = 0,
        var code: String? = null,
        var exchange: String? = null,
        var name: String? = null,
        var short_name: String? = null,
        var english_name: String? = null,
        var tradiontal_name: String? = null,
        var is_delist: String? = null,
        var flag: Int = 0,
        var type: String? = null,
        var is_new_stock: String? = null,
        var stk_connect: Int = 0
):BaseBean()


