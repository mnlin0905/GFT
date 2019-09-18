package com.linktech.gft.bean

import com.linktech.gft.plugins.getApplicationLocale
import java.util.*


data class CountryBean(
        val nationEn: String,
        val nationCn: String,
        val codeName: String? = null,
        val codeArea: String? = null,
        val chineseInitial: String? = null,
        val price: Double = 0.0,
        val id: Int = 0
) {
    fun getCountry(): String {
        return if (getApplicationLocale().language == Locale.CHINESE.language) nationCn else nationEn
    }

    fun getLetters(): String {
        return if (getApplicationLocale().language == Locale.CHINESE.language) {
            if (chineseInitial.isNullOrEmpty()) "#" else chineseInitial
        } else nationEn
    }

}