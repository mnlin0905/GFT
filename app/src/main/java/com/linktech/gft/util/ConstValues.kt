package com.linktech.gft.util

import com.linktech.gft.plugins.empty
import com.linktech.gft.plugins.getApplicationLocale
import com.linktech.gft.plugins.too
import java.util.*

/**
 * function : 基础常量类集合
 *
 * Created on 2018/6/27  14:39
 * @author mnlin
 */
object Const {
    /**
     * 设置图片上传的最大文件大小（单个）
     * 设置图片上传的最大文件尺寸
     */
    const val MAX_SIZE_PICTURE_UPLOAD = 1024 * 1024
    const val MAX_PIXEL_PICTURE_UPLOAD = 500

    /**
     * 动画延迟关闭时间,默认500ms
     */
    const val NORMAL_ANIMATOR_DELAY_TIME = 500

    /**
     * RxBus传递信息,BaseEventBean,信息类型
     *
     *
     * 弹出toast
     * 弹出登录框
     * 显示积极的toast窗口
     * 融云连接服务器
     * 融云:与对方开启私聊
     * ...
     */
    const val SHOW_TOAST = 2001
    const val SHOW_LOGIN_DIALOG = SHOW_TOAST + 1
    const val SHOW_BIG_TOAST = SHOW_TOAST + 2

    /**
     * 用于传递字段的key,字段,基本类型/String
     */
    const val KEY_POSITION = "key_position"
    const val KEY_PICTURE_POSITIVE = "key_picture_positive"
    const val KEY_PICTURE_NEGATIVE = "key_picture_negative"
    const val KEY_PICTURE_MULTIPLE = "key_picture_multiple"
    const val KEY_FILTER_SOURCE = "key_filter_source"
    const val KEY_FILTER_INPUT_KEYS = "key_filter_input_keys"
    const val KEY_IS_FROM_SIGN = "key_is_from_sign"

    /**
     * 正常小数保留位数
     */
    const val NORMAL_DECIMAL_FLOAT_SCALE_1 = 1
    const val NORMAL_DECIMAL_FLOAT_SCALE_2 = 2
    const val NORMAL_DECIMAL_FLOAT_SCALE_3 = 3
    const val NORMAL_DECIMAL_FLOAT_SCALE_5 = 5
    const val NORMAL_DECIMAL_FLOAT_SCALE_6 = 6

    /**
     * 聊天消息类型
     *
     * 系统消息
     * 己方消息
     * 对方消息
     */
    const val KEY_TYPE_CHAT_MESSAGE = "key_type_chat_message"
    const val TYPE_CHAT_MESSAGE_SYSTEM = 0
    const val TYPE_CHAT_MESSAGE_MINE = 1
    const val TYPE_CHAT_MESSAGE_YOURS = 2

    /**
     * 自选/市场
     */
    const val KEY_MARKET_SCAN = "key_market_scan"
    const val MARKET_SCAN_CUSTOM = 0
    const val MARKET_SCAN_MARKET = 1

    /**
     * 股票类型区域
     * 全部
     * 港股
     * 美股
     */
    const val KEY_SHARES_BLOCK_LOCALE = "key_market_scan"
    const val SHARES_BLOCK_LOCALE_ALL = 0
    const val SHARES_BLOCK_LOCALE_HK = 1
    const val SHARES_BLOCK_LOCALE_US = 2

    /**
     * 成交 / 委托
     */
    const val KEY_TYPE_ENTRUST_SEARCH = "key_type_entrust_search"
    const val TYPE_ENTRUST_SEARCH_SUCCESS = 0
    const val TYPE_ENTRUST_SEARCH_WAIT = 1

    /**
     * log上报日志
     */
    const val TAG_LOGGER_REPORT_NET = "logger_report_net"

    /**
     * 有明确值的常量
     *
     *
     * 非法的位置,错误的类型,无类型等等
     * 正确的返回值,正常的量
     */
    const val VALUE_POSITION_NULL = -1

    /**
     * 通用的值传递,Any?/Object
     */
    const val KEY_COMMON_VALUE = "key_common_value"
    const val KEY_COMMON_VALUE_ONE = "key_common_value_one"
    const val KEY_COMMON_VALUE_TWO = "key_common_value_two"
    const val KEY_COMMON_VALUE_THREE = "key_common_value_three"
    const val KEY_COMMON_VALUE_FOUR = "key_common_value_four"
    const val KEY_COMMON_VALUE_FIVE = "key_common_value_five"

    /**
     * 更新版本
     */
    const val MODEL_VERSION_UPDATE = "model_version_update"

    /**
     * 请求码,用于请求其他activity返回bundle信息
     *
     *
     * 最后为请求刷新数据的情况(登录/未登录状态切换)
     */
    const val REQUEST_CODE_ONE = 1
    const val REQUEST_CODE_TWO = REQUEST_CODE_ONE + 1
    const val REQUEST_CODE_THREE = REQUEST_CODE_ONE + 2
    const val REQUEST_CODE_LOGIN_REFRESH = REQUEST_CODE_ONE + 10

    /**
     * 资讯类型
     *
     * 1:热点推送 2:港新股 3:港股 4:7*24小时 5:美股  6:自选
     */
    const val KEY_TYPE_FINANCING_INFORMATION = "key_type_financing_information"
    const val TYPE_FINANCING_INFORMATION_ALL = 0
    const val TYPE_FINANCING_INFORMATION_HOT = 1
    const val TYPE_FINANCING_INFORMATION_NEW_HK = 2
    const val TYPE_FINANCING_INFORMATION_HK = 3
    const val TYPE_FINANCING_INFORMATION_7_24 = 4
    const val TYPE_FINANCING_INFORMATION_US = 5
    const val TYPE_FINANCING_INFORMATION_CUSTOM = 6

    /**
     * 找回登录密码的方式
     *
     *
     * 通过手机
     * 通过邮箱
     */
    const val KEY_TYPE_FIND_LOGIN_PASSWORD = "key_type_find_login_password"
    const val TYPE_FIND_LOGIN_PASSWORD_BY_PHONE = 0
    const val TYPE_FIND_LOGIN_PASSWORD_BY_EMAIL = 1

    /**
     * 身份认证方式:
     * 身份证1,
     * 护照2,
     * 其他3,
     */
    const val KEY_TYPE_ID_CARD_TYPE = "key_type_id_card_type"
    const val TYPE_ID_CARD_IDENTIFICATION = 1
    const val TYPE_ID_CARD_FOREIGN = 2
    const val TYPE_ID_CARD_OTHER = 3

    /**
     * 单页数据量
     *
     *
     * 默认:20
     * 最大:100
     */
    const val CONST_PER_PAGE_SIZE_DEFAULT = 20
    const val CONST_PER_PAGE_SIZE_BIGGEST = 100

    /**
     * 修改交易密码的方式
     */
    const val KEY_TYPE_CHANGE_TRANS_PWD = "key_type_change_trans_pwd"
    const val TYPE_CHANGE_TRANS_PWD_OLDPWD = 1 //原交易密码校验
    const val TYPE_CHANGE_TRANS_FACE = 3//刷脸
    const val TYPE_CHANGE_TRANS_MESSAGE_AND_CARD = 2//短信+证件号码

    /**
     * 消息类型
     *
     * 提醒/通知
     * 活动
     * 互动消息
     * 客服
     */
    const val KEY_TYPE_NEWS_MESSAGE = "key_type_news_message"
    const val TYPE_NEWS_MESSAGE_NOTICE = 0
    const val TYPE_NEWS_MESSAGE_PLAY = 1
    const val TYPE_NEWS_MESSAGE_CHAT = 2
    const val TYPE_NEWS_MESSAGE_SERVICE = 3

    /**
     * 手机号
     * +86
     * 邮箱
     */
    const val KEY_PHONE = "key_phone"
    const val KEY_PHONE_CODE = "key_phone_code"
    const val KEY_EMAIL = "key_email"
    const val KEY_CODE_TYPE = "key_code_type"
    const val KEY_VERIFY_CODE = "key_verify_code"

    /**
     * 系统可设置的语言类型
     *
     * 默认
     * 中文
     * 台湾
     * 香港
     * 英语
     */
    val LOCALE_LANGUAGE_TYPES = arrayOf(
            null,
            Locale.SIMPLIFIED_CHINESE,
            Locale.TRADITIONAL_CHINESE,
            Locale("zh", "HK"),
            Locale.ENGLISH
    )

    /**
     * 国家
     * code
     */
    const val KEY_COUNTRY_NAME = "key_country_name"
    const val KEY_NATION_CODE = "key_nation_code"
}

/**
 * function : http返回错误码
 *
 * Created on 2018/6/27  14:41
 * @author mnlin
 */
val ERROR_CODES = { error_code: Int ->
    when (error_code) {
        0 -> "请求成功" to "請求成功" too "請求成功"
        1001 -> "参数异常" to "參數異常" too "參數異常"
        1002 -> "服务器未知异常" to "伺服器未知異常" too "伺服器未知異常"
        1003 -> "券商ID不存在" to "券商ID不存在" too "券商ID不存在"
        1004 -> "加密方式有误" to "加密方式有誤" too "加密方式有誤"
        1005 -> "参数内容过长" to "參數內容過長" too "參數內容過長"
        1006 -> "用户名不存在" to "用戶名不存在" too "用戶名不存在"
        1007 -> "文件服务器异常" to "檔伺服器異常" too "檔伺服器異常"
        1008 -> "文件类型错误,无后缀名" to "檔類型錯誤,無後綴名" too "檔類型錯誤,無後綴名"
        1009 -> "上传文件过大" to "上傳檔過大" too "上傳檔過大"
        1010 -> "两次密码输入不一致" to "兩次密碼輸入不一致" too "兩次密碼輸入不一致"
        1011 -> "邮箱格式错误" to "郵箱格式錯誤" too "郵箱格式錯誤"
        1012 -> "验证码发送失败" to "驗證碼發送失敗" too "驗證碼發送失敗"
        1013 -> "验证码错误" to "驗證碼錯誤" too "驗證碼錯誤"
        1014 -> "用户名已存在" to "用戶名已存在" too "用戶名已存在"
        1015 -> "密码错误" to "密碼錯誤" too "密碼錯誤"
        1016 -> "登录超时" to "登錄超時" too "登錄超時"
        1017 -> "昵称已存在" to "昵稱已存在" too "昵稱已存在"
        1018 -> "手机号码与注册账号不一致" to "手機號碼與註冊帳號不一致" too "手機號碼與註冊帳號不一致"
        1019 -> "手机号码已被绑定" to "手機號碼已被綁定" too "手機號碼已被綁定"
        1020 -> "实名认证信息已存在" to "實名認證資訊已存在" too "實名認證資訊已存在"
        1021 -> "证件号码已存在" to "證件號碼已存在" too "證件號碼已存在"
        1022 -> "旧密码错误" to "舊密碼錯誤" too "舊密碼錯誤"
        1030 -> "身份证正面无法识别" to "身份证正面无法识别" too "身份证正面无法识别"
        1031 -> "身份证反面无法识别" to "身份证反面无法识别" too "身份证反面无法识别"
        1032 -> "外部服务器异常" to "外部服务器异常" too "外部服务器异常"
        1033 -> "上一步验证已过期" to "上一步验证已过期" too "上一步验证已过期"
        1034 -> "开户记录不存在,请重新填写" to "开户记录不存在,请重新填写" too "开户记录不存在,请重新填写"
        1035 -> "银行卡号无效" to "银行卡号无效" too "银行卡号无效"
        1036 -> "银行卡实名验证未通过" to "银行卡实名验证未通过" too "银行卡实名验证未通过"
        1037 -> "银行卡无法验证" to "银行卡无法验证" too "银行卡无法验证"
        1038 -> "同一身份银行卡号重复调用次数达到上限" to "同一身份银行卡号重复调用次数达到上限" too "同一身份银行卡号重复调用次数达到上限"
        1039 -> "银行卡验证姓名错误" to "银行卡验证姓名错误" too "银行卡验证姓名错误"
        1040 -> "银行卡验证身份证号错误" to "银行卡验证身份证号错误" too "银行卡验证身份证号错误"
        1041 -> "银行卡验证卡号错误" to "银行卡验证卡号错误" too "银行卡验证卡号错误"
        1042 -> "开户信息已提交,无法更改" to "开户信息已提交,无法更改" too "开户信息已提交,无法更改"
        1043 -> "生日格式错误" to "生日格式错误" too "生日格式错误"
        1044 -> "参数类型错误" to "参数类型错误" too "参数类型错误"
        else -> null
    }?.let { languages ->
        when {
            Unit.getApplicationLocale().let { it.language == Locale.CHINESE.language && it.country.toUpperCase().contains("CN") } -> languages.first
            Unit.getApplicationLocale().let { it.language == Locale.CHINESE.language && !it.country.toUpperCase().contains("CN") } -> languages.second.empty(comment = "台湾/香港 返回繁体")
            Unit.getApplicationLocale().let { it.language == Locale.ENGLISH.language } -> languages.third
            else -> languages.third.empty(comment = "如果为不可识别类型,则默认返回英文")
        }
    }
}

/**
 * function :  正则常量
 *
 * Created on 2018/6/27  14:41
 * @author mnlin
 */
object RegexConst {
    /**
     * 匹配手机号
     */
    const val REGEX_PHONE = "^[\\d]{6,11}$"
    const val REGEX_TELEPHONE = "^[0-9]{6,}$"

    const val a = "^[\\d]{6,11}$"
    /**
     * 匹配邮箱
     */
    const val REGEX_EMAIL = "^([_0-9a-zA-Z]+)@([_0-9a-zA-Z]+)\\.([_0-9a-zA-Z]+)$"

    /**
     * 匹配手机验证码
     */
    const val REGEX_RANDOM_NUMBER_6 = "^[0-9]{6}$"
    const val REGEX_RANDOM_TEXT_4 = "^[0-9a-zA-Z]{4}$"

    /**
     * 匹配账户密码
     */
    const val REGEX_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$"
    const val REGEX_TRANSACTION_PASSWORD = "^[0-9]{6}$"

    /**
     * 昵称
     */
    const val REGEX_NICKNAME = "^[\u4e00-\u9fa5a-zA-Z0-9_.]{1,16}\$"

    /**
     * 钱包名称
     */
    const val REGEX_WALLET_NAME = "^[\\S]{2,16}$"

    /**
     * 钱包私钥
     */
    const val REGEX_WALLET_SECRET = "^[a-zA-Z0-9_]{10,}$"

    /**
     * 实名认证姓名
     */
    const val REGEX_VERITY_NAME = "^[\u4e00-\u9fa5a-zA-Z0-9.]{1,16}\$"

    /**
     * 手势密码格式
     */
    const val REGEX_LOCK_GESTURE = "^[0-9]{4,9}$"

    /**
     * 身份证号
     * 军官证
     * 护照
     * 回乡证
     */
    const val REGEX_ID_CARD_NUMBER = "(^\\d{15}$)|(^\\d{17}[0-9Xx]$)"
    const val REGEX_POLICE_CARD_NUMBER = "^\\d{7,8}$"
    const val REGEX_FOREIGN_CARD_NUMBER = "^\\w{6,15}$"
    const val REGEX_BACK_CARD_NUMBER = "(^H\\d{8}$)|(^M\\d{8}$)"

    /**
     * 匹配当前应用验证码短信
     */
    const val REGEX_VERIFY_CODE_SMS_EMAIL = "^.*?[^\\d]([\\d]{6})[^\\d].*?\$"

    /**
     * 区块链浏览器-跳转url
     *
     * 区块高度 http://browser.pawstech.io/block-index/2326456
     * 交易哈希 http://browser.pawstech.io/tx/68DECE0AA0C6410A67DB1AA4561A8F2D0D88052FEE97A9771C3609FEA4D761BD
     * 地址26-34 http://browser.pawstech.io/address/zhpZi6ABpeV9gjh9N77pErdLJKjsQ9thBK
     */
    const val REGEX_WALLET_BLOCK_INDEX = "^[\\d]+$"
    const val REGEX_WALLET_HASH_CODE = "^[0-9A-Z]{35,}"
    const val REGEX_WALLET_ADDRESS = "^[a-zA-Z0-9]{26,34}$"


    /**
     * ob**变成普通类
     * 修改if变成when
     * bean生成
     * 移除@Field标志
     */
    const val REGEX_COMMON_1 = "Observable<\\S+?>([\\s\\S]+?\\{)[\\s\\S]*?\\}"
    const val REGEX_COMMON_2 = "if \\((.*?)\\) \\{\\s*(.*)[\\s\\S]*?\\}"
    const val REGEX_COMMON_3 = "\\s*?(\\S+?)\\s+?[YN]\\s+?(\\S+).+?\$"
    const val REGEX_REMOVE_AT_FIELD = "\\s*?@Field[\\S]+"

    /**
     * 匹配相同字符串
     */
    const val REGEX_EQUALS_STRING = "(>.+?<)[\\s\\S]+(\\1)"
    const val REGEX_REPLACE_ONE = "name=\"(.+)\"(>.+?</string)([\\s\\S^%]+)(\\2)"
    const val REGEX_REPLACE_TWO = "name=\"\$1\"\$2\$3>@string/\$1</string"
}

/**
 * function : 发送短信到邮箱,reason
 *
 * 1:注册 2:绑定邮箱 3:修改邮箱 4:实名认证 5:重置密码
 *
 * Created on 2018/6/27  14:41
 * @author mnlin
 */
object EmailConst {
    /**
     * 邮箱进行注册
     */
    const val TAG_REGISTER = "1"

    /**
     * 绑定邮箱
     */
    const val TAG_BOUND_EMAIL = "2"

    /**
     * 修改邮箱
     */
    const val TAG_CHANGE_EMAIL = "3"

    /**
     * 实名认证
     */
    const val TAG_VERIFICATION = "4"

    /**
     * 重置密码
     */
    const val TAG_RESET_PWD = "5"

    /**
     * 提币
     */
    const val TAG_TRANSFER_ASSETS = "transferOutToken"
}

/**
 * function : 发送短信验证码时，用于区分不同的用途
 *
 *  1:注册 2:绑定邮箱 3:修改邮箱 4:实名认证 5:重置密码  6:动态登录
 *
 * Created on 2018/6/27  14:41
 * @author mnlin
 */
object SmsConst {
    /**
     * 手机号进行登录
     */
    const val TAG_LOGIN = "6"

    /**
     * 手机号进行注册
     */
    const val TAG_REGISTER = "1"

    /**
     * 绑定手机号
     */
    const val TAG_BOUND_MOBILE = "bindmobile"

    /**
     * 修改手机号
     */
    const val TAG_CHANGE_MOBILE = "modifyusmobile"

    /**
     * 实名认证
     */
    const val TAG_VERIFICATION = "4"

    /**
     * 重置密码
     */
    const val TAG_RESET_PWD = "5"

    /**
     * 提币
     */
    const val TAG_TRANSFER_ASSETS = "transferOutToken"

    /**
     * 平台内转账
     */
    const val TAG_TRANSFER_PLATFORM = "transfer"

    /**
     * 绑定银行卡
     */
    const val TAG_BIND_BANK = "bindbank"

    /**
     * 修改交易密码
     */
    const val TAG_CHANGE_TRANS_PWD = "setPayPwd"

}