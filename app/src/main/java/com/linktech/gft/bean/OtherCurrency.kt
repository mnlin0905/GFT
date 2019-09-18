package com.linktech.gft.bean

import java.io.Serializable

data class OtherCurrency(
        val currency_short_name: String,
        val currency_short_name_zh: String,
        val id: Int,
        val currency_img: String
) : Serializable