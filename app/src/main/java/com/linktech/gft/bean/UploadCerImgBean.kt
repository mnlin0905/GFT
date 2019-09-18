package com.linktech.gft.bean

import com.linktech.gft.base.BaseBean

/**
 * function : 实名认证上传图片返回值
 *
 * [path]	Y	String	文件下载全路径
 * [url]	Y	String	文件下载半路径
 *
 * Created on 2018/7/2  16:38
 * @author mnlin
 */
data class UploadCerImgBean(
        val path: String? = null,
        val url: String? = null
) : BaseBean()
