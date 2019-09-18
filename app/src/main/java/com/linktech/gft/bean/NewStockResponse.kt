package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean
import com.linktech.gft.plugins.toScaleString

/**
 * 2.14 获取新股申购列表（包含今日可申购、即将上市和已上市）
 * [to_buy_list] 今日可申购的
 * [to_be_listed] 即将上市和已上市的
 */
data class NewStockResponse(
        var to_buy_list: MutableList<NewStock> = mutableListOf(),
        var buy_list_size: Int = 0,
        var to_be_listed: MutableList<BeingStock> = mutableListOf(),
        var grey_market_size: Int = 0
) {
    /**
     * 今日可申购的，目前只封装了需要的字段
     *
     * [code] 股票编码
     * [name] 股票名称
     * [price_ceiling] 最低价
     * [price_floor] 最高价
     * [mini_purchase_quota] 最低申购额度
     * [issue_end_date_desc] 认购截止日期
     */
    data class NewStock(
            var name: String? = null,
            var code: String? = null,
            var price_ceiling: Double = 0.0,
            var price_floor: Double = 0.0,
            var mini_purchase_quota: Double = 0.0,
            var issue_end_date_desc: String? = null
    ) : BaseBean(), CommonBean {
        override fun getStockName(): String? {
            return name
        }

        override fun getStockCode(): String? {
            return code
        }

        override fun getValueOne(): String? {
            return "${price_ceiling.toScaleString(3)}-${price_floor.toScaleString(3)}"

        }

        override fun getValueTwo(): String? {
            return mini_purchase_quota.toString()
        }

        override fun getValueThree(): String? {
            return issue_end_date_desc
        }

        override fun getType(): Int {
            return 0
        }

    }

    /**
     *  即将上市和已上市的
     *
     *
     * [code] 股票编码
     * [name] 股票名称
     * [issue_price] 发行价
     * [issue_end_date] 认购截止日期
     * [list_date] 上市日期
     * [dark_day] 暗盘交易日提示信息
     * [request_the_url] 详情
     */
    data class BeingStock(
            var name: String? = null,
            var code: String? = null,
            var issue_price: String? = null,
            var issue_end_date: String? = null,
            var list_date: String? = null,
            var dark_day: String? = null,
            var request_the_url: String? = null
    ) : BaseBean(), CommonBean {
        override fun getStockName(): String? {
            return name
        }

        override fun getStockCode(): String? {
            return code
        }

        override fun getValueOne(): String? {
            return issue_price
        }

        override fun getValueTwo(): String? {
            return list_date
        }

        override fun getValueThree(): String? {
            return dark_day
        }

        override fun getType(): Int {
            return 1
        }
    }
}

interface CommonBean {
    fun getStockName(): String?
    fun getStockCode(): String?
    /**
     * 招股价或者发行价
     */
    fun getValueOne(): String?

    /**
     * 最低认购数量或者上市日期
     */
    fun getValueTwo(): String?

    /**
     * 认购截至日期或者暗盘交易日
     */
    fun getValueThree(): String?

    /**
     * 类型 0 --今日可申购的  1--即将上市
     */
    fun getType(): Int
}


