package com.linktech.gft.bean


import com.linktech.gft.base.BaseBean

/**
 * function : 版本更新返回值
 *
 * version	Y	int	版本号
 * downlink	Y	String	下载链接
 * forced	Y	String	是否强制更新 true-是 false-否
 * type	N	int	类型 1:IOS 2:安卓
 * updateTime	N	String	更新时间
 * "app_name": "无名",
 * "remark": "暂时没有",
 * "id": 2
 *
 * Created on 2018/8/20  17:13
 * @author mnlin
 */
data class LatestVersionBean(
        var version: Int = 100,
        var downlink: String? = null,
        var forced: Boolean = false,
        var updateTime: String? = null,
        var type: Int = 0,
        val appName: String? = null,
        val remark: String? = null,
        val isDel: Int = 0,
        var id: Int = 0
) : BaseBean()
