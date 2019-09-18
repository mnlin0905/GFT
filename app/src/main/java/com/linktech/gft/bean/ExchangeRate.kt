package com.linktech.gft.bean

import java.io.Serializable

data class ExchangeRate(
        var money: String = "0.0",
        var toCode: String?,
        var exchange_fee: Double
) : Serializable