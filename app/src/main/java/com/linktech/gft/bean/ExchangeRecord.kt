package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.plugins.getString
import java.io.Serializable

data class ExchangeRecord(
        val id: Int = 0,
        val exchange_currency_id: Int = 0,
        val payment_currency_id: Int = 0,
        val exchange_currency: String? = null,
        val payment_currency: String? = null,
        val amount: Double = 0.0,
        val exchange_amount: Double = 0.0,
        val exchange_fee: Double = 0.0,
        val exchange_rate: Double = 0.0,
        val status: Int = 0,//审核状态 0:待审核 1:已通过 2:拒绝 3:已撤销
        val create_time: String? = null,
        val update_time: String? = null,
        val check_opinion: String? = null,
        val name: String? = null,
        val country: String? = null,
        val deposit_bank: String? = null,
        val branch_name: String? = null,
        val card_number: String? = null
) : Serializable {
    fun getStatusStr(): String {
        return when (status) {
            0 -> getString(R.string.function_processing) //  绿色  撤销  蓝色
            1 -> getString(R.string.function_approved) //  黑色  完成  黑色
            2 -> getString(R.string.function_rejected)  //  红色 未通过 红色
            3 -> getString(R.string.function_revoked) //  黑色  完成  黑色
            else -> getString(R.string.common_unknown_value)
        }
    }

    fun getOperateStr(): String {
        return when (status) {
            0 -> getString(R.string.function_revoke)
            1, 3 -> getString(R.string.function_completed)
            2 -> getString(R.string.function_failed)
            else -> getString(R.string.common_unknown_value)
        }
    }

    fun getStatusColor(context: BaseActivity<*>): Int {
        return when (status) {
            0 -> context.dispatchGetColor(R.color.green_text_09c2a1)
            1, 2, 3 -> context.dispatchGetColor(R.color.black_text_171a3f)
            else -> context.dispatchGetColor(R.color.black_text_171a3f)
        }
    }

    fun getOperateColor(context: BaseActivity<*>): Int {
        return when (status) {
            0 -> context.dispatchGetColor(R.color.toolbar_second_color_primary)
            1, 2, 3 -> context.dispatchGetColor(R.color.black_text_171a3f)
            else -> context.dispatchGetColor(R.color.black_text_171a3f)
        }
    }

    fun getOperateAble(): Boolean {
        return status == 0
    }

}