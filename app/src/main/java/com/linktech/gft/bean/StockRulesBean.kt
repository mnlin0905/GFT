package com.linktech.gft.bean

import org.litepal.crud.DataSupport

/**
 * function : 股票交易规则
 *
 * [minValue] Y	double	最小值
 * [maxValue] Y	double	最大值
 * [range] Y	double	区间
 *
 * Created on 2019/6/18  14:15
 * @author mnlin
 */
class StockRulesBean(
        var minValue: Double = .0,
        var maxValue: Double = .0,
        var range: Double = .0
):DataSupport()