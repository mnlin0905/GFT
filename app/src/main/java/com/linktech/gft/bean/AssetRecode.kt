package com.linktech.gft.bean

import java.io.Serializable

data class AssetRecode(
        var start_time: String? = null,
        var create_time: String? = null,
        var project_name: String? = null,
        var currency: String? = null,
        var sweet_source: Int=0,
        var amount: Double = 0.0,
        var number: Double = 0.0
) : Serializable