package com.linktech.gft.bean


data class RewardList(
        val login_name: String? = null,
        val nickname: String? = null,
        val mobile: String? = null,
        val email: String? = null,
        val reward_type: Int = 0,
        val bounty: Double = 0.0,
        val asset: String? = null,
        val success: Int = 0,
        val create_time: String? = null,
        val id: Int = 0,
        val is_open: Int = 0
)