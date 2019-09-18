package com.linktech.gft.bean

import java.io.Serializable

data class SignLevel(
        var level0: Double = 0.0,
        var level3: Double = 0.0,
        var level7: Double = 0.0,
        var level15: Double = 0.0,
        var level28: Double = 0.0
) : Serializable