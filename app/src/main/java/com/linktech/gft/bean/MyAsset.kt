package com.linktech.gft.bean

import java.io.Serializable

data class MyAsset(
        var user_id: Int=0,
        var account_balance: Double=0.0,
        var freeze_balance: Double=0.0,
        var create_time: String?=null,
        var update_time: String?=null,
        var currency: String?=null,
        var id: Int=0
) : Serializable