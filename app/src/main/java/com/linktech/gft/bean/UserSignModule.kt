package com.linktech.gft.bean


data class UserSignInfo(
        val signInVos: MutableList<SignInVo> = mutableListOf(),
        val todayReward: Double = 0.0,
        val monthSignInDay: Int = 0,
        val allSignInDay: Int = 0,
        val monthReward: Double = 0.0,
        val allReward: Double = 0.0
)

data class SignInVo(
        val bounty: Double = 0.0,
        val create_time: String? = null
)
