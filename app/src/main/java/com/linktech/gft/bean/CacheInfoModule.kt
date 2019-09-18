package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.base.BaseBean
import com.linktech.gft.plugins.getString

/**
 * function : 1.9 获取会员账户信息
 *
 * [userId] : 用户id
 * [userImg] : "/forum_otc/img/headImg.png", 用户头像
 * [nickname] : "1007109", 用户昵称
 * [mobile] : "159****0003", 手机
 * [email] : "58336***@qq.com" 邮箱
 * [gender] : 3, 性别   1：男  2：女 3：保密
 * [certification_status]: -1未认证,0审核中,1通过,2失败
 * [name] 真实姓名
 * [cardCode] 证件号码
 * [is_signIn] 今天是否签到过  true：是  false：否
 * [has_email] 设置邮箱
 * [has_mobile] 设置手机号
 * [has_payPwd] 交易密码
 * [verified] 0-未认证，1-已认证 2:认证失败  3:待审核
 */
data class UserBaseInfoBean(
        var userId: Int = 0,
        var userImg: String? = null,
        var nickname: String? = null,
        var has_payPwd: Boolean = false,
        var has_mobile: Boolean = false,
        var has_email: Boolean = false,
        var gender: Int = 0,
        var mobile: String? = null,
        var email: String? = null,
        private var verified: Int = 0,
        var not_passing: String? = null,
        var name: String? = null,
        var cardCode: String? = null,
        var is_signIn: Boolean = false,
        var hasBankCard: Boolean = false
) : BaseBean() {

    /**
     * 性别
     */
    val sexStr: String
        get() = if (gender == 1) getString(R.string.common_sex_man) else if (gender == 2) getString(R.string.common_sex_woman) else getString(R.string.common_sex_unknown)

    /**
     * 实名认证状态转变
     *
     *  -1未认证,0审核中,1通过,2失败
     */
    val certification_status: Int
        get() = when (verified) {
            0 -> -1
            1 -> 1
            2 -> 2
            3 -> 0
            else -> -1
        }
}

/**
 * 实名认真结果信息
 *
 *  [idcard]  Y	String	证件号码
 *  [cardType]  Y	String	证件类型 1 身份证 2护照 3 军官证
 *  [name]  Y	String	真实姓名
 *  [mobile]  Y	String	手机号码
 *  [notPassing]  Y	String	审核未通过原因
 *  [enNotPassing]  Y	String	审核未通过原因(英文)
 *  [status]  Y	int	0审核中1通过，2：未通過
 */
data class CertificationResultBean(
        var adminUserId: Int = 0,
        var adminUserName: String? = null,
        var cardType: Int = 0,
        var country: String? = null,
        var createTime: String? = null,
        var enNotPassing: String? = null,
        var examineTime: String? = null,
        var id: Int = 0,
        var idcard: String? = null,
        var idcardContrary: String? = null,
        var idcardHord: String? = null,
        var idcardPositive: String? = null,
        var mobile: String? = null,
        var name: String? = null,
        var notPassing: String? = null,
        var status: Int = 0,
        var userId: Int = 0
)