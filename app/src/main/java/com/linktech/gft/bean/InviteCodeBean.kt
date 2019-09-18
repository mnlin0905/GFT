package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.base.BaseBean
import com.linktech.gft.plugins.getString

/**
 * function : 邀请码
 *
 * [invite_code] 邀请码
 * [qrcode_url] 邀请码链接
 * [upperLimit] 邀请上限
 * [useCount] 邀请奖励
 *
 * Created on 2018/8/21  20:22
 * @author mnlin
 */
data class InviteCodeBean(
        var invite_code: String? = null,
        var qrcode_url: String? = null,
        var upperLimit: Int = 0,
        var useCount: Int = 0
) : BaseBean()

/**
 * function : 邀请奖励列表
 *
 * [login_name] :Y	String	用户名
 * [nickname] :Y	String	用户昵称
 * [mobile] :Y	String	手机号
 * [email] :Y	String	邮箱
 * [bounty] :Y	double	奖励数量
 * [asset] :Y	String	奖励币种
 * [is_gain] :Y	int	是否领取 0-否 1-是
 * [create_time] :Y	String	注册时间
 * [id] :Y	int	记录id
 * [verified] 是否认证 1-是 0-否
 *
 * Created on 2018/8/21  20:22
 * @author mnlin
 */
data class InviteListBean(
        var login_name: String? = null,
        var nickname: String? = null,
        var mobile: String? = null,
        var email: String? = null,
        var invited_type: Int = 0,
        var bounty: Double = 0.0,
        var asset: String = Unit.getString(R.string.system_currency),
        var is_gain: Int = 0,
        var create_time: String? = null,
        var id: Int = 0,
        var verified: Int = 0
)