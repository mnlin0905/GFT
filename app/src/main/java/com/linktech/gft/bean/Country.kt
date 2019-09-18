package com.linktech.gft.bean

import com.linktech.gft.plugins.getApplicationLocale
import java.util.*

/**
 * function : 國家地區
 *
 * Created on 2018/9/4  19:45
 * @author mnlin
 */
/**
 * 国家信息
 *
 *
 * 英文名
 * 中文名
 * 国家/地区代码
 */
data class Country(private var englishName: String,
                   private var chineseName: String,
                   var countryCode: String) {

    override fun toString(): String {
        return if (getApplicationLocale().language == Locale.CHINESE.language) chineseName else englishName
    }
}