package com.linktech.gft.bean

import org.litepal.annotation.Column
import org.litepal.crud.DataSupport

/**************************************
 * function : 内部嵌入的布局
 *
 * Created on 2019/3/26  20:41
 * @author mnlin
 **************************************/

/**
 * function : 聊天记录消息
 *
 * [chat_status] 消息类型 [Const.KEY_TYPE_CHAT_MESSAGE]
 *
 * Created on 2018/12/26  11:31
 * @author mnlin
 */
data class ChatMessageBean(
        var chat_status: Int = 0,
        var chat_message: String? = null,
        var chat_time: String? = null
)

/**
 * function : 添加自选保存到本地处理
 *
 * [id] 股票id
 * [sharesName] 股票名称
 * [sharesCode] 股票编号
 * [sharesLocale] 股票所属区域:港股,美股等
 *
 * Created on 2019/4/1  13:44
 * @author mnlin
 */
data class CustomSharesBean(
        var id: Int = 0,
        var sharesName: String? = null,
        @Column(nullable = false, unique = true)
        var sharesCode: String = "00000",
        var sharesLocale: String? = null
) : DataSupport()

