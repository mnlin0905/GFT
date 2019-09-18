package com.linktech.gft.bean

import com.linktech.gft.R
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.plugins.empty
import com.linktech.gft.plugins.getString
import org.litepal.annotation.Column
import org.litepal.crud.DataSupport
import java.math.BigDecimal

/**************************************
 * function : 钱包module
 *
 * Created on 2018/8/7  10:28
 * @author mnlin
 **************************************/

/**
 * function : 钱包bean
 *
 * [id] 数据库key
 *
 * [address] 钱包地址 String?
 * [name] 用户名 String?
 * [crypto] 加密bean CryptoBean?,外键关联
 * [deviceId]  String? 设备id(设备唯一标识,用于控制单个设备的帐号数目)
 * [version] Int 软件版本(不同版本可能导出文件格式不同)
 * [hasActivated] 是否已经激活
 * [alertHint] 密码提示信息
 * [hasBackup] 是否已经备份
 *
 * Created on 2018/8/7  10:45
 * @author mnlin
 */
data class WalletBean(
        var id: Int = 0,

        @Column(unique = true, nullable = false)
        var address: String,
        var name: String? = null,
        var deviceId: String? = null,
        var version: Int = 0,
        var hasActivated: Boolean = true,
        var alertHint: String? = null,
        var hasBackup: Boolean = false,

        /**
         * 外键关联,加密对象
         */
        var crypto: CryptoBean,

        /**
         * 外键关联,币种信息,一对多
         */
        var currencys: MutableList<CurrencyBean> = mutableListOf()
) : DataSupport() {
    /**
     * 为LitePal数据库添加默认的构造方法(空参数)
     */
    private constructor () : this(address = "error address", crypto = CryptoBean())

    /**
     * 重写save方法,在save自己之前,先将内部类进行保存
     */
    override fun save(): Boolean {
        DataSupport.saveAll(currencys)
        crypto.save()
        return super.save()
    }

    /**
     * 重写saveOrUpdate方法,在save自己之前,先将内部类进行保存
     */
    override fun saveOrUpdate(vararg conditions: String?): Boolean {
        DataSupport.deleteAll(CurrencyBean::class.java, "WalletBean_id = ?", "$id")
        DataSupport.saveAll(currencys)
        crypto.saveOrUpdate("ciphertext = ?", crypto.ciphertext)
        return super.saveOrUpdate(*conditions)
    }

    /**
     * function : 加密bean
     *
     * [id] 数据库key
     *
     * [cipher] String? 加密方式,一般为 'aes-256'
     * [ciphertext] String? 加密结果
     * [salt] 随机码
     *
     * Created on 2018/8/7  10:48
     * @author mnlin
     */
    data class CryptoBean(
            var id: Int = 0,

            @Column(nullable = false)
            var cipher: String = "aes-256",
            @Column(nullable = false, unique = true)
            var ciphertext: String,
            var salt: String
    ) : DataSupport() {
        /**
         * 为LitePal数据库添加默认的构造方法(空参数)
         */
        constructor() : this(ciphertext = "error ciphertext", salt = "")
    }

    /**
     * function : 钱包对应的多币种列表(用于控制某些需要展示的币种)(由currency和gateway组合来唯一确定记录line)
     *
     * [currency] 币种名称
     * [gateway] 币种信任的网关地址,如"jfskojfslfjljflsfjsfjskdlfjslfs456445fskfslF"
     * [createTime] 创建时间
     * [trustAmount] 信任数量
     * [balance] 账户余额(存在缓存值)
     * [isDefault] 是否默认需要信任(为true则在创建钱包和导入钱包时,需要循环信任)
     * [isDelete] 是否已经被移除,被移除则不再进行显示
     * [visible] 是否默认在资产中可见,初始时跟随 [isDefault] ,显示时先判断 [balance] 是否为null, 为null则表示币种/网关 未信任,需要先进行信任操作,[balance] 非null表示已经信任完成,只需要让显示出值即可
     * [description] 币种描述
     * [platform] 项目方
     *
     * Created on 2018/8/14  10:27
     * @author mnlin
     */
    data class CurrencyBean(
            var id: Int = 0,

            @Column(nullable = false)
            var currency: String,
            @Column(nullable = false)
            var gateway: String,
            var createTime: String? = null,
            var trustAmount: Long = 0,
            var balance: String? = null,
            var isDefault: Boolean = false,
            var isDelete: Boolean = false,
            var visible: Boolean = false,
            var logo: String? = null,
            var description: String? = null,
            var platform: String? = null
    ) : DataSupport() {
        private constructor() : this(currency = "null", gateway = "null")

        /**
         * 是否为系统币 [R.string.system_currency]
         */
        val isSysBalance
            get() = currency == Unit.getString(R.string.system_currency)

        /**
         * 是否为应用主币 [R.string.application_main_currency]
         */
        val isMainBalance
            get() = currency == Unit.getString(R.string.application_main_currency)
    }
}

/**
 * function : 转出资产的bean
 *
 * 从钱包[fromAddress]转出到钱包[toAddress]共[amount]资产,包含注释[comments],注释可为空列表
 *
 * Created on 2018/8/7  16:43
 * @author mnlin
 */
data class TransferBean(
        var fromAddress: String,
        var toAddress: String,
        var amount: String,
        var comments: MutableList<String> = mutableListOf()
)

/**
 * function : 联系人bean
 *
 * [id] 数据库key
 * [name] 姓名(不可为null,)
 * [address] 地址,联系地址(不可为null)
 * [phone] 手机号
 * [email] 邮箱
 * [remark] 备注信息
 *
 * Created on 2018/8/14  10:27
 * @author mnlin
 */
data class ContactBean(
        var id: Int = 0,

        @Column(nullable = false)
        var name: String,
        @Column(unique = true, nullable = false)
        var address: String,
        var phone: String? = null,
        var email: String? = null,
        var remark: String? = null
) : DataSupport() {
    private constructor() : this(name = "null", address = "null")
}

/**
 * function : 转账所需手续费
 *
 * [mType] 费率计算方式:0 规定费率 ,其他:百分比
 * [mRate] 费率百分比
 * [mFixFee] 固定费率
 * [mMinFee] 最小费率(小于此值取此值)
 * [mMaxFee] 最大费率(大于此值取此值)
 *
 * Created on 2018/8/15  14:16
 * @author mnlin
 */
private data class TransferFee(
        var mType: Int = 0,
        var mRate: String? = null,
        var mFixFee: String? = null,
        var mMinFee: String? = null,
        var mMaxFee: String? = null
)

/**
 * function : 信任列表bean(由currency和gateway组合来唯一确定记录line)
 *
 * [currency] 币种值
 * [gateway] 信任网关
 * [trust_amount] 信任值,默认1000亿
 * [create_time] 创建时间
 * [is_del] 是否删除,1表示true
 * [first_trust] 是否为需要默认信任.1表示true
 * [id] id
 * [logo] 图标
 * [description] 币种描述
 * [platform] 项目方
 *
 * Created on 2018/8/15  14:16
 * @author mnlin
 */
data class TrustListBean(
        var currency: String = Unit.getString(R.string.application_main_currency),
        var gateway: String = BaseApplication.app.issueTrustAddress,
        var trust_amount: Long = 1000_0000_0000L,
        var create_time: String?,
        var is_del: Int = 0,
        var first_trust: Int = 0,
        var id: Int = 0,
        var logo: String? = null,
        var description: String? = null,
        var platform: String? = null
) {
    /**
     * 是否初次就需要信任
     */
    fun requireFirstBelieve(): Boolean {
        return first_trust == 1
    }
}

/**
 * 数据传递层,用于activity之间数据传递
 *
 * [currency] 币种类型,默认为 系统币(DAC)
 * [currencyAll] 币种类型全称
 * [balance] 币种余额,bigDecimal操作
 * [gateway] 网关地址,系统币时网关为null
 * [imgResource] 头像ResId,系统币时有值
 * [imgUrl] 头像url,头像为网络数据时路径
 */
data class WalletIntentBean(
        var currency: String = Unit.getString(R.string.system_currency),
        var gateway: String? = null,
        var balance: String? = null,
        var imgResource: Int? = null,
        var imgUrl: String? = null,
        var toTargetCurrencyRate:BigDecimal?=null
) {
    var currencyAll: String = ""
        get() = if (currency == getString(R.string.application_main_currency)) getString(R.string.application_main_currency_full_name) else currency

    /**
     * 系统币和普通币不同,显示时需要扣除2
     */
    val appearBalance: String?
        get() = balance?.let {
            if (isSysBalance.empty(comment = "如果为系统币,则显示余额时扣除2")) {
                it.toBigDecimalOrNull()?.let {
                    if (it > 2.toBigDecimal()) it - 2.toBigDecimal() else BigDecimal.ZERO
                }
            } else {
                it.toBigDecimalOrNull() ?: BigDecimal.ZERO
            }
        }?.toPlainString()

    /**
     * 是否为系统币 [R.string.system_currency]
     */
    val isSysBalance
        get() = currency == Unit.getString(R.string.system_currency)

    /**
     * 是否为应用主币 [R.string.application_main_currency]
     */
    val isMainBalance
        get() = currency == Unit.getString(R.string.application_main_currency)

    /**
     * 获取网络url地址
     */
    val getCurrencyImg
        get() = imgResource ?: imgUrl

    /**
     * 是否有余额(余额不足时,不能进行转账操作)
     */
    val hasEnoughBalance
        get() = balance?.toBigDecimalOrNull() ?: BigDecimal.ZERO > BigDecimal.ZERO

    /**
     * 保存一些經常使用的單例(系統幣,應用幣)
     */
    companion object {
        /** 系统币 */
        var SYS_CURRENCY = WalletIntentBean(currency = getString(R.string.system_currency), imgResource = R.drawable.icon_currency_dac)
        /** 应用币 */
        var MAIN_CURRENCY = WalletIntentBean(currency = getString(R.string.application_main_currency), imgResource = R.drawable.icon_usdp)
    }
}
