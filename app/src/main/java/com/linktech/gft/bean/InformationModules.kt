package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**************************************
 * function : 资讯模块 bean
 *
 * Created on 2019/4/16  16:30
 * @author mnlin
 **************************************/

/**
 * function : 港股资讯列表信息
 *
 * [coverPicture]   Y	String	封面图
 * [releaseTime]   Y	String	发布时间
 * [releaseUser]   Y	String	发布人
 * [source]   Y	String	信息来源
 * [title]   Y	String	标题
 * [type]   Y	int	资讯类型(详见请求参数描述)
 *
 * Created on 2019/4/16  16:30
 * @author mnlin
 */
data class InformationRecordBean(
        var coverPicture: String? = null,
        var releaseTime: String? = null,
        var releaseUser: String? = null,
        var id: Int = 0,
        var newsId: Int = 0,
        var source: String? = null,
        var title: String? = null,
        var type: Int = 0
) : BaseBean()

/**
 * function : 资讯详情信息,包含外网连接地址
 *
 * [releaseTime]   Y	String	发布时间
 * [releaseUser]   Y	String	发布人
 * [source]   Y	String	信息来源
 * [title]   Y	String	标题
 * [type]   Y	int	资讯类型(详见请求参数描述)
 * [externalLink]   Y	String	外联链接
 * [content]   Y	String	详情内容(H5富文本代码)
 * [collected] 0: 为收藏,1:已收藏
 * [external] 0 内部资讯,1 链接
 *
 * Created on 2019/4/16  16:36
 * @author mnlin
 */
data class InformationDetailBean(
        var id: Int = 0,
        var title: String? = null,
        var releaseTime: String? = null,
        var source: String? = null,
        var coverPicture: String? = null,
        var type: Int = 0,
        var releaseUser: String? = null,
        var externalLink: String? = null,
        var external:Int = 0,
        var status: Int = 0,
        var checkInfo: String? = null,
        var createTime: String? = null,
        var updateTime: String? = null,
        var deleted: Int = 0,
        var content: String? = null,
        var collected: Int = 0,
        var awesome: Int = 0,
        var userAwesome: Int = 0
) : BaseBean() {
    /**
     * 是否已经收藏
     */
    var hasCollected: Boolean
        get() = collected == 1
        set(value) {
            collected = if (value) 1 else 0
        }

    /**
     * 是否已经点赞
     */
    var hasAgree:Boolean
        get() = userAwesome == 1
        set(value) {
            userAwesome = if (value) 1 else 0
        }
}