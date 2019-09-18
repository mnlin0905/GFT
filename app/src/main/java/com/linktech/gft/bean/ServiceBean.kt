package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 * function : 获取服务列表bean
 *
 * [service] 图片服务器
 *
 * [name] : "商城",
 * [name_en] : 英文名
 * [link] : "https://mall.linktech.io",
 * [sort] : 0, 排序优先级
 * [is_open] : 1, 是否展示 0 否,1 是
 * [icon] : null,
 * [is_del] : 0,
 * [create_time] : "2018-08-08 14:28:16.0",
 * [update_time] : "2018-08-08 14:28:16.0",
 * [id] : 2
 * [type] 1表示h5数据,应用内显示;2表示网页,用浏览器可打开
 * [need_login] 0,否,1,是,是否必须登录后才能跳转
 *
 * Created on 2018/8/21  9:54
 * @author mnlin
 */
data class ServiceBean(
        var service: String? = null,
        var name: String? = null,
        var name_en: String? = null,
        var link: String? = null,
        var sort: Int = 0,
        var is_open: Int = 0,
        var icon: String? = null,
        var is_del: Int = 0,
        var create_time: String? = null,
        var update_time: String? = null,
        var id: Int = 0,
        var type: Int = 1,
        var need_login: Int = 0,
        var description: String? = null,

        //额外部分
        var imgRes: Int? = null
) : BaseBean()
