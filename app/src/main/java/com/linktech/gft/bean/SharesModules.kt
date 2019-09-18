package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**************************************
 * function :  股票 中 bean
 *
 * Created on 2019/3/27  21:38
 * @author mnlin
 **************************************/

/**
 * function : 股票列表
 *
 *  [symbol]: "00066", /*代码*/
 *  [name]: "港铁公司",/*名称*/
 *  [engname]: "MTR CORPORATION",/*英文名*/
 *  [lasttrade]: "35.900",/*最新价*/
 *  [prevclose]: "36.100",/*昨收*/
 *  [open]: "35.900",/*今开*/
 *  [high]: "35.900",/*最高*/
 *  [low]: "35.900",/*最低*/
 *  [volume]: "178000",/*成交量*/
 *  [amount]: "6389923",/*成交额*/
 *  [ticktime]: "2015-07-02 09:20:00",/*时间*/
 *  [buy]: "35.800",/*买入*/
 *  [sell]: "35.900",/*卖出*/
 *  [high_52week]: "40.000",/*52周最高*/
 *  [low_52week]: "29.250",/*52周最低*/
 *  [stocks_sum]: "5839612547",/*总股本*/
 *  [pricechange]: "-0.200",/*涨跌额*/
 *  [changepercent]: "-0.5540166"/*涨跌幅*/
 *
 *  [sharesLocale]  股票 去 :默认 香港港股
 *
 * Created on 2019/3/27  21:39
 * @author mnlin
 */
data class SharesListItem(
        var symbol: String? = null,
        var name: String? = null,
        var engname: String? = null,
        var lasttrade: String? = null,
        var prevclose: String? = null,
        var open: String? = null,
        var high: String? = null,
        var low: String? = null,
        var volume: String? = null,
        var amount: String? = null,
        var ticktime: String? = null,
        var buy: String? = null,
        var sell: String? = null,
        var high_52week: String? = null,
        var low_52week: String? = null,
        var stocks_sum: String? = null,
        var pricechange: String? = null,
        var changepercent: String? = null,
        var sharesLocale: String = "HKEX"
) : BaseBean()

/**
 * function : 股票详情数据
 *
 *
 * [gid]  :"hk00001",		/*股票编号*/
 * [ename]  :"CHEUNG KONG",		/*股票英文简称*/
 * [name]  :"长江实业",		/*股票名称*/
 * [openpri]  :"119.600",		/*今日开盘价*/
 * [formpri]  :"119.200",		/*前收盘价*/
 * [maxpri]  :"120.600",		/*最高价*/
 * [minpri]  :"119.600",		/*最低价*/
 * [lastestpri]  :"119.800",		/*最新价*/
 * [uppic]  :"0.600",		/*涨跌*/
 * [limit]  :"0.503",		/*涨跌幅%*/
 * [inpic]  :"119.700",		/*买入价*/
 * [outpic]  :"119.900",		/*卖出价*/
 * [traAmount]  :"121700502",	/*成交额（元）*/
 * [traNumber]  :"1014242",		/*成交量（股）*/
 * [priearn]  :"6.025",		/*市盈率*/
 * [max52]  :"132.700",		/*52周最高*/
 * [min52]  :"86.000",		/*52周最低*/
 * [date]  :"2013/03/12",		/*日期*/
 * [time]  :"11:19:12"		/*更新时间*/
 *
 * Created on 2019/3/28  0:30
 * @author mnlin
 */
data class SharesDetailBean(
        var gid: String?,
        var ename: String?,
        var name: String?,
        var openpri: String?,
        var formpri: String?,
        var maxpri: String?,
        var minpri: String?,
        var lastestpri: String?,
        var uppic: String?,
        var limit: String?,
        var inpic: String?,
        var outpic: String?,
        var traAmount: String?,
        var traNumber: String?,
        var priearn: String?,
        var max52: String?,
        var min52: String?,
        var date: String?,
        var time: String?
)

/**
 * function : 所有的股票信息
 *
 * 包括HSI以及股票信息
 *
 * Created on 2019/4/2  17:58
 * @author mnlin
 */
data class SharesDetailTotal(
        var data: SharesDetailBean? = null,
        var gopicture: Any? = null,
        var hengsheng_data: HengshengData? = null
) {
    /**
     * function : HSI数据
     *
     * [date]       2014/05/12",//日期
     * [formpri]        21862.99",//前收盘价
     * [lastestpri]     22220.65",//最新价
     * [limit]      1.64",//涨跌幅%
     * [max52]      24111.55",//52周最高
     * [maxpri]     22250.28",//最高价
     * [min52]      0.00",//52周最低
     * [minpri]     21796.90",//最低价
     * [openpri]        21921.59",//开盘价
     * [time]       11:44:00",//时间
     * [traAmount]      31524616000",//成交额
     * [uppic]      357.66"//涨跌额
     *
     * Created on 2019/4/2  18:02
     * @author mnlin
     */
    data class HengshengData(
            var date: String? = null,
            var formpri: String? = null,
            var lastestpri: String? = null,
            var limit: String? = null,
            var max52: String? = null,
            var maxpri: String? = null,
            var min52: String? = null,
            var minpri: String? = null,
            var openpri: String? = null,
            var time: String? = null,
            var traAmount: String? = null,
            var uppic: String? = null
    )
}