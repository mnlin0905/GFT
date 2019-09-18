package com.linktech.gft.base

/**
 * Created on 2019/6/13  15:53
 * function : ws 数据类型基类
 *
 * @author mnlin
 */
abstract class BaseWSBean : BaseHttpBean() {
    var type: String? = null
    abstract val securityCode: String?

    override fun equals(other: Any?): Boolean {
        return other is BaseWSBean && securityCode != null && securityCode.equals(other.securityCode)
    }

    override fun hashCode(): Int {
        return securityCode?.hashCode()?:0
    }
}
