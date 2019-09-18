package com.linktech.gft.bean

import com.linktech.gft.base.BaseHttpBean

/**
 * function : banner
 *
 * Created on 2018/8/21  10:35
 * @author mnlin
 */
data class BannerBean(
        var img: String? = null,
        var url: String? = null,
        var description: String? = null
) : BaseHttpBean()
