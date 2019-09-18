package com.linktech.gft.bean


import com.linktech.gft.base.BaseHttpBean

/**
 * 登录返回数据
 *
 * [mobile] 手机号
 * [username] 用户名
 * [login_name] 登录名
 */
data class LoginBean(var mobile: String? = null,
                     var username: String? = null,
                     var signature: String? = null,
                     var login_name: String? = null) : BaseHttpBean()

/**
 * function : 微信登录 返回 bean
 *
 * Created on 2019/7/17  11:48
 * @author mnlin
 */
data class WXLoginBean(var data: LoginBean = LoginBean()) : BaseHttpBean()
