package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 * function : 公共协议类接口
 *
 * [create_time] "2018-08-16 14:47:09.0",
 * [name] "挖矿算力规则", 标题
 * [link] "https://www.abc.com", 网址
 * [remark] "",
 * [is_del] 0,
 * [version] 100,
 * [category] 4, 备注: category: 协议类别 1:钱包服务协议 2:生态服务协议 3:生态版权信息 4:锁仓释放规则 5:挖矿奖励规则
 * [id] 3
 *
 * Created on 2018/8/16  19:30
 * @author mnlin
 */
data class CommonAgreementBean(
        var create_time: String? = null,
        var name: String? = null,
        var link: String? = null,
        var remark: String? = null,
        var is_del: Int = 0,
        var version: Int = 0,
        var category: Int = 0,
        var id: Int = 0
) : BaseBean()
