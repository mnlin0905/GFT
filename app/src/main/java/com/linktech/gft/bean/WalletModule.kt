package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseBean
import com.linktech.gft.plugins.getString
import com.linktech.gft.plugins.getStringArray

/**************************************
 * function : 钱包bean模块
 *
 * Created on 2018/7/6  11:25
 * @author mnlin
 **************************************/

/**
 * function : 转出数据及记录
 *
 * [balance]	Y	double	可用余额
 * [totalTransferOut]	Y	double	转出总量
 * [fee]	Y	String	手续费比例(已做百分比计算直接拼接%展示)
 * [mobile]	Y	String	手机号码
 * [payPwd]	Y	boolean	是否设置交易密码
 * [verified]	Y	int	实名状态  0-未认证，1-已认证 2:认证失败  3:待认证
 * [records]	Y	Array	转出记录
 *
 * Created on 2018/7/6  13:50
 * @author mnlin
 */
data class WalletTakeOutBean(
        var balance: Double = 0.0,
        var totalTransferOut: Double = 0.0,
        var fee: String? = null,
        var mobile: String? = null,
        var payPwd: Boolean = false,
        var verified: Int = 0,
        var records: List<Record> = emptyList()
) : BaseBean() {

    /**
     * function : 转出记录bean
     *
     * [actual]	Y	double	转出数量
     * [toAddress]	Y	String	接受地址
     * [arrive]	Y	double	到账数量
     * [hash]	Y	String	交易ID
     * [status]	Y	int	转出状态: 0 处理中(待审核)  1已审核  2已复核  3已转出  4已成功 5已失败 6已撤销
     * [id]		int	转出申请ID
     * [createTime]	Y	String	创建时间
     *
     * Created on 2018/7/6  13:55
     * @author mnlin
     */
    data class Record(
            var actual: Double = 0.0,
            var createTime: String? = null,
            var arrive: Double = 0.0,
            var toAddress: String? = null,
            var hash: String = null.toString(),
            var status: Int = 0,
            var id: Long = 0) {
        fun getStatusStr(): String {
            return when (status) {

                in 0..6 -> getStringArray(R.array.array_take_out_status)[status]
                else -> getString(R.string.common_unknown_value)
            }
        }

        fun getStatusColor(context: BaseActivity<*>): Int {
            return if (status >= 4) context.dispatchGetColor(R.color.black_text_171a3f) else context.dispatchGetColor(R.color.green_text_09c2a1)
        }

        fun getOperateStr(): String {
            return getString(when (status) {
                0, 1, 2 -> R.string.function_revoke
                3 -> R.string.function_wait
                4, 5, 6 -> R.string.function_completed
                else -> R.string.common_unknown_value
            })
        }

        fun getOperateColor(context: BaseActivity<*>): Int {
            return if (status < 3) context.dispatchGetColor(R.color.toolbar_second_color_primary) else context.dispatchGetColor(R.color.black_text_171a3f)
        }

        fun getOperateAble(): Boolean {
            return status < 3
        }
    }
}