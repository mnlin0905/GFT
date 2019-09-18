package com.linktech.gft.bean

import java.io.Serializable

data class AllAsset(
        var total_assets: Double,
        var currency: String?,
        var freeze_assets: Double,
        var usable_assets: Double,
        var hashrate_amount: Double,
        var hashrate_sign: Double,
        var hashrate_now_day_sign: Double,
        var hashrate_invite: Double,
        var hashrate_now_day_invite: Double,
        var hashrate_real_name: Double
) : Serializable