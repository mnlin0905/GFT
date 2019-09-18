package com.linktech.gft.bean

import com.linktech.gft.R
import java.io.Serializable

data class BankCard(
        val currency_id: Int = 0,
        val name: String? = null,
        val country: String? = null,
        val deposit_bank: String? = null,
        val branch_name: String? = null,
        val card_number: String? = null,
        var id: Int = 0

) : Serializable {
    fun getBankLogo(): Int {
        return when (deposit_bank) {
            "中国银行" -> R.drawable.bank_zhong_logo
            "招商银行" -> R.drawable.bank_zhao_logo
            "农业银行" -> R.drawable.bank_nong_logo
            else -> R.drawable.bank_nong_logo
        }
    }

    fun getBankBg(): Int {
        return when (deposit_bank) {
            "中国银行" -> R.drawable.bank_zhong_bg
            "招商银行" -> R.drawable.bank_zhao_bg
            "农业银行" -> R.drawable.bank_nong_bg
            else -> R.drawable.bank_nong_bg
        }
    }

    fun getUserName(): String {
        return name?.run {
            substring(0, 1) + "*"
        } ?: ""
    }

    fun getBankNumber(): String {
        return card_number!!
    }

}