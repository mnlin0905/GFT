package com.linktech.gft.plugins

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.service.DegradeService
import com.alibaba.android.arouter.facade.service.SerializationService
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BaseEvent
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BasePresenter.singleUserInfo
import com.linktech.gft.plugins.ARouterConst.Activity_AddBankActivity
import com.linktech.gft.plugins.ARouterConst.Activity_BeginCertificationActivity
import com.linktech.gft.plugins.ARouterConst.Activity_BindEmailActivity
import com.linktech.gft.plugins.ARouterConst.Activity_BindMobileActivity
import com.linktech.gft.plugins.ARouterConst.Activity_SetTransactionPasswordActivity
import com.linktech.gft.plugins.ARouterConst.Activity_ShowCertificationResultActivity
import com.linktech.gft.plugins.ARouterConst.Activity_ShowEmailActivity
import com.linktech.gft.plugins.ARouterConst.Activity_ShowMobileActivity
import com.linktech.gft.plugins.ARouterConst.FLAG_ACTIVITY_CLEAR_TASK
import com.linktech.gft.plugins.ARouterConst.FLAG_ACTIVITY_CLEAR_TOP
import com.linktech.gft.plugins.ARouterConst.FLAG_EMAIL
import com.linktech.gft.plugins.ARouterConst.FLAG_FORBID_ACCESS
import com.linktech.gft.plugins.ARouterConst.FLAG_HAS_BANK_CARD
import com.linktech.gft.plugins.ARouterConst.FLAG_LOGIN
import com.linktech.gft.plugins.ARouterConst.FLAG_MOBILE_NET
import com.linktech.gft.plugins.ARouterConst.FLAG_NONE
import com.linktech.gft.plugins.ARouterConst.FLAG_PHONE
import com.linktech.gft.plugins.ARouterConst.FLAG_TRADE_LOGIN
import com.linktech.gft.plugins.ARouterConst.FLAG_TRANSACTION_PASSWORD
import com.linktech.gft.plugins.ARouterConst.FLAG_VERIFY_HAS_BEGIN
import com.linktech.gft.plugins.ARouterConst.FLAG_VERIFY_HAS_SUCCESS
import com.linktech.gft.plugins.ARouterConst.FLAG_WIFI_NET
import com.linktech.gft.util.Const
import com.linktech.gft.util.DefaultPreferenceUtil
import com.orhanobut.logger.Logger
import java.lang.reflect.Type

/**************************************
 * function : ARouter 框架包含的所有配置
 *
 * Created on 2018/6/27  11:52
 * @author mnlin
 **************************************/

/**
 * 功能----路径跳转activity/fragment
 * <p>
 * Created by LinkTech on 2017/11/24.
 */
object ARouterConst {
    /**
     * 无权限
     * 登录
     * 已开始了实名认证流程
     * 已绑定手机
     * 已绑定邮箱
     * 已设置交易密码
     * 红色通道(默认没有,即正常请求都需要进行过滤,如果有目标需要该权限,则表示通往该目标的请求都将被拦截(除非有特殊处理))
     * 有WIFI网络
     * 有流量网络(MOBILE)
     * 强制activity启动方式:清除任务栈
     * 强制activity启动方式:清除activity上方任务
     * 实名认证已成功
     * 有银行卡
     * 券商已登录
     */
    const val FLAG_NONE = 0B0000_0000_0000_0000_0000_0000_0000_0000
    const val FLAG_LOGIN = 0B0000_0000_0000_0000_0000_0000_0000_0011
    const val FLAG_VERIFY_HAS_BEGIN = 0x00000004
    const val FLAG_PHONE = 0x00000008
    const val FLAG_EMAIL = 0x00000010
    const val FLAG_TRANSACTION_PASSWORD = 0x00000020
    const val FLAG_FORBID_ACCESS = 0x00000040
    const val FLAG_WIFI_NET = 0x00000080
    const val FLAG_MOBILE_NET = 0x00000100
    const val FLAG_ACTIVITY_CLEAR_TASK = 0x00000200
    const val FLAG_ACTIVITY_CLEAR_TOP = 0x00000400
    const val FLAG_VERIFY_HAS_SUCCESS = 0x00000800
    const val FLAG_HAS_BANK_CARD = 0x00001000
    const val FLAG_TRADE_LOGIN = 0x00002000

    /**
     * activity/fragment
     */
    const val Activity_ScreenLockActivity = "/activity/ScreenLockActivity"
    const val Activity_ShowInviteCodeActivity = "/activity/ShowInviteCodeActivity"
    const val Activity_InviteRewardActivity = "/activity/InviteRewardActivity"
    const val Activity_InvitePetGameActivity = "/activity/InvitePetGameActivity"
    const val Activity_MainActivity = "/activity/MainActivity"
    const val Activity_LoginActivity = "/activity/LoginActivity"
    const val Activity_RegisterActivity = "/activity/RegisterActivity"
    const val Activity_SoftSettingActivity = "/activity/SoftSettingActivity"
    const val Activity_SafetyCenterActivity = "/activity/SafetyCenterActivity"
    const val Activity_SwitchLocaleActivity = "/activity/SwitchLocaleActivity"
    const val Activity_PersonInformationActivity = "/activity/PersonInformationActivity"
    const val Activity_ChangeNickNameActivity = "/activity/ChangeNickNameActivity"
    const val Activity_BeginCertificationActivity = "/activity/BeginCertificationActivity"
    const val Activity_UploadCertificationPhotoActivity = "/activity/UploadCertificationPhotoActivity"
    const val Activity_ShowCertificationResultActivity = "/activity/ShowCertificationResultActivity"
    const val Activity_ForgetPasswordActivity = "/activity/ForgetPasswordActivity"
    const val Activity_RegisterSuccessActivity = "/activity/RegisterSuccessActivity"
    const val Activity_FindPasswordActivity = "/activity/FindPasswordActivity"
    const val Activity_ResetPasswordActivity = "/activity/ResetPasswordActivity"
    const val Activity_BindWXRegisterActivity = "/activity/BindWXRegisterActivity"
    const val Activity_ChangePasswordActivity = "/activity/ChangePasswordActivity"
    const val Activity_SelectChangeTransPasswordTypeActivity = "/activity/SelectChangeTransPasswordTypeActivity"
    const val Activity_SetTransactionPasswordActivity = "/activity/SetTransactionPasswordActivity"
    const val Activity_BindEmailActivity = "/activity/BindEmailActivity"
    const val Activity_BindMobileActivity = "/activity/BindMobileActivity"
    const val Activity_ChangeEmailActivity = "/activity/ChangeEmailActivity"
    const val Activity_ChangeMobileActivity = "/activity/ChangeMobileActivity"
    const val Activity_InputCodeActivity = "/activity/InputCodeActivity"
    const val Activity_VerifyCardNumActivity = "/activity/VerifyCardNumActivity"
    const val Activity_ShowEmailActivity = "/activity/ShowEmailActivity"
    const val Activity_ShowMobileActivity = "/activity/ShowMobileActivity"
    const val Activity_AboutActivity = "/activity/AboutActivity"
    const val Activity_SignActivity = "/activity/SignActivity"
    const val Activity_SignRewardActivity = "/activity/SignRewardActivity"
    const val Activity_InputOldTransPasswordActivity = "/activity/InputOldTransPasswordActivity"
    const val Activity_ChangeTransPasswordActivity = "/activity/ChangeTransPasswordActivity"
    const val Activity_ChangeTransSuccessActivity = "/activity/ChangeTransSuccessActivity"
    const val Activity_SearchFilterActivity = "/activity/SearchFilterActivity"
    const val Activity_RecommendRecordActivity = "/activity/RecommendRecordActivity"
    const val Activity_ShowTransResultActivity = "/activity/ShowTransResultActivity"
    const val Activity_CommonAgreementActivity = "/activity/CommonAgreementActivity"
    const val Activity_ChooseCountryActivity = "/activity/ChooseCountryActivity"
    const val Activity_AddBankActivity = "/activity/AddBankActivity"

    /**
     * 嵌入部分
     */
    const val Activity_FinancingActivity = "/activity/FinancingActivity"
    const val Fragment_MarketFragment = "/fragment/MarketFragment"
    const val Fragment_MarketItemFragment = "/fragment/MarketItemFragment"
    const val Fragment_InformationFragment = "/fragment/InformationFragment"
    const val Fragment_InformationItemFragment = "/fragment/InformationItemFragment"
    const val Fragment_PersonFragment = "/fragment/PersonFragment"
    const val Fragment_EntrustSearchFragment = "/fragment/EntrustSearchFragment"
    const val Fragment_ExchangeFragment = "/fragment/ExchangeFragment"
    const val Fragment_AssetsFragment = "/fragment/AssetsFragment"
    const val Fragment_TradeDetailFragment = "/fragment/TradeDetailFragment"
    const val Activity_DealActivity = "/activity/DealActivity"
    const val Activity_TradeActivity = "/activity/TradeActivity"
    const val Activity_FeedbackActivity = "/activity/FeedbackActivity"
    const val Activity_ChooseTradeActivity = "/activity/ChooseTradeActivity"
    const val Activity_FlutterContainerActivity = "/activity/FlutterContainerActivity"
    const val Activity_LoginTradeActivity = "/activity/LoginTradeActivity"
    const val Fragment_DealFragment = "/fragment/DealFragment"
    const val Fragment_KillOrderFragment = "/fragment/KillOrderFragment"
    const val Activity_KLineActivity = "/activity/KLineActivity"
    const val Activity_KLineExponentActivity = "/activity/KLineExponentActivity"
    const val Activity_KLineComponentActivity = "/activity/KLineComponentActivity"

    const val Activity_KLineLandExponentActivity = "/activity/KLineLandExponentActivity"
    const val Activity_KLineLandActivity = "/activity/KLineLandActivity"
    const val Activity_BuySellActivity = "/activity/BuySellActivity"
    const val Activity_FinancableStockActivity = "/activity/FinancableStockActivity"
    const val Activity_IndustrySectorActivity = "/activity/IndustrySectorActivity"
    const val Activity_IndustryPartActivity = "/activity/IndustryPartActivity"
    const val Activity_ConceptualPlateActivity = "/activity/ConceptualPlateActivity"
    const val Fragment_KNewsItemFragment = "/fragment/KNewsItemFragment"
    const val Fragment_KLineComponentFragment = "/fragment/KLineComponentFragment"
    const val Fragment_KF10ItemFragment = "/fragment/KF10ItemFragment"
    const val Activity_StockWarnRecodeActivity = "/activity/StockWarnRecodeActivity"
    const val Activity_StockWarnActivity = "/activity/StockWarnActivity"
    const val Activity_RemindFrequencyActivity = "/activity/RemindFrequencyActivity"
    const val Activity_NewsActivity = "/activity/NewsActivity"
    const val Activity_NoDataActivity = "/activity/NoDataActivity"
    const val Activity_OpenPreparationActivity = "/establish/OpenPreparationActivity"
    const val Activity_UploadIdCardActivity = "/establish/UploadIdCardActivity"
    const val Activity_CommitInfoMoreActivity = "/establish/CommitInfoMoreActivity"
    const val Activity_AgreementDeclareActivity = "/establish/AgreementDeclareActivity"
    const val Activity_SelectAccountActivity = "/establish/SelectAccountActivity"
    const val Activity_InvestmentExperienceActivity = "/establish/InvestmentExperienceActivity"
    const val Activity_FinancialPositionActivity = "/establish/FinancialPositionActivity"
    const val Activity_VerifyPasswordActivity = "/establish/VerifyPasswordActivity"
    const val Activity_SignatureAgreementActivity = "/establish/SignatureAgreementActivity"
    const val Activity_RiskDisclosureActivity = "/establish/RiskDisclosureActivity"
    const val Activity_SignatureNameActivity = "/establish/SignatureNameActivity"
    const val Activity_SignatureNameConfirmActivity = "/establish/SignatureNameConfirmActivity"
    const val Activity_UploadPhotoActivity = "/establish/UploadPhotoActivity"
    const val Activity_EstablishFinishActivity = "/establish/EstablishFinishActivity"
    const val Activity_VerifyBankCardActivity = "/establish/VerifyBankCardActivity"
    const val Activity_CommitInfoBasicActivity = "/establish/CommitInfoBasicActivity"
    const val Activity_TurbineActivity = "/activity/TurbineActivity"
    const val Activity_BullBearSyndromeActivity = "/activity/BullBearSyndromeActivity"
    const val Activity_AllMarketStocksActivity = "/activity/AllMarketStocksActivity"
    const val Activity_SectionChangeRateActivity = "/activity/SectionChangeRateActivity"
    const val Activity_FaceLockActivity = "/activity/FaceLockActivity"
    const val Activity_GestureSettingActivity = "/activity/GestureSettingActivity"
    const val Activity_GestureLockActivity = "/activity/GestureLockActivity"
    const val Activity_FingerprintLockActivity = "/activity/FingerprintLockActivity "
    const val Activity_MoneyCenterActivity = "/activity/MoneyCenterActivity"
    const val Activity_SettingActivity = "/activity/SettingActivity"
    const val Activity_MessageAlertActivity = "/activity/MessageAlertActivity"
    const val Activity_SwitchLocaleInnerActivity = "/activity/SwitchLocaleInnerActivity"
    const val Activity_SwitchModeActivity = "/activity/SwitchModeActivity"
    const val Activity_SwitchColorActivity = "/activity/SwitchColorActivity"
    const val Activity_RefreshRateActivity = "/activity/RefreshRateActivity"
    const val Activity_SafetyLockActivity = "/activity/SafetyLockActivity"
    const val Activity_AboutInnerActivity = "/activity/AboutInnerActivity"
    const val Activity_AddSharesActivity = "/activity/AddSharesActivity"
    const val Activity_CommonAgreementInnerActivity = "/activity/CommonAgreementInnerActivity"
    const val Activity_OrderChatActivity = "/activity/OrderChatActivity"
    const val Activity_MyCollectionActivity = "/activity/MyCollectionActivity"
    const val Activity_SafetyProtectActivity = "/activity/SafetyProtectActivity"
    const val Activity_NewStockActivity = "/activity/NewStockActivity"
    const val Activity_GoldenActivity = "/activity/GoldenActivity"
    const val Activity_WithdrawActivity = "/activity/WithdrawActivity"

}

/**
 * function : 跳转拦截器(权限拦截)
 *
 *
 * 比较经典的应用就是在跳转过程中处理登陆事件，这样就不需要在目标页重复做登陆检查
 * 拦截器会在跳转之间执行，多个拦截器会按优先级顺序依次执行
 *
 * @author ACChain
 */
@Interceptor(priority = 2, name = "ARouter跳转拦截器")
class ARouterInterceptor : IInterceptor {
    @Suppress("LocalVariableName")
    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        Logger.v("######界面跳转 ： " + postcard.toString())

        //当前所有的权限
        val permissions = getStringArray(R.array.array_arouter_permissions)

        val FLAGS_ALL = intArrayOf(
                FLAG_LOGIN,
                FLAG_VERIFY_HAS_BEGIN,
                FLAG_PHONE,
                FLAG_EMAIL,
                FLAG_TRANSACTION_PASSWORD,
                FLAG_FORBID_ACCESS,
                FLAG_WIFI_NET,
                FLAG_MOBILE_NET,
                FLAG_ACTIVITY_CLEAR_TASK,
                FLAG_ACTIVITY_CLEAR_TOP,
                FLAG_VERIFY_HAS_SUCCESS,
                FLAG_HAS_BANK_CARD,
                FLAG_TRADE_LOGIN
        )

        //当前所有权限对应的boolean值;为false则对应权限设为 FLAG_NONE
        val FLAGS_ALL_VALUE = booleanArrayOf(
                DefaultPreferenceUtil.getInstance().login,
                singleUserInfo.certification_status != -1,
                !singleUserInfo.mobile.isNullOrBlank(),
                !singleUserInfo.email.isNullOrBlank(),
                singleUserInfo.has_payPwd,
                false,
                false,
                false,
                false,
                false,
                singleUserInfo.certification_status == 1,
                singleUserInfo.hasBankCard,
                BaseApplication.isTradeLogin
        )

        //目标界面需要的权限
        val requireFlags = postcard.extra or Integer.MIN_VALUE

        Logger.v("###### require permission : " + Integer.toBinaryString(requireFlags))

        //如果目标无需任何权限,跳过权限判断逻辑
        if (requireFlags == Integer.MIN_VALUE) {
            callback.onContinue(postcard)
            return
        }

        //当前所有的权限
        var currentFlags = Integer.MIN_VALUE
        for (position in FLAGS_ALL.indices) {
            currentFlags = currentFlags or
                    if (FLAGS_ALL_VALUE[position]) FLAGS_ALL[position] else FLAG_NONE
        }
        Logger.v("###### current permission : " + Integer.toBinaryString(currentFlags))

        //如果需要的权限都已存在,则直接跳转,不做处理
        if (requireFlags and currentFlags == requireFlags) {
            callback.onContinue(postcard)
            return
        }

        //如果发现不一致,说明某些权限不存在,则需要依次判断哪个权限不存在
        for (position in FLAGS_ALL.indices) {
            if (requireFlags and FLAGS_ALL[position] != 0 && currentFlags and FLAGS_ALL[position] == 0) {
                var consume = false
                when (position) {
                    0 -> consume = dispatchLogin(postcard, callback) // 未登录
                    1 -> consume = dispatchVerityHasBegin(postcard, callback) // 未开始实名认证
                    2 -> consume = dispatchMobile(postcard, callback) // 未绑定手机
                    3 -> consume = dispatchEmail(postcard, callback) // 未绑定邮箱
                    4 -> consume = dispatchTransactionPassword(postcard, callback) // 未设置交易密码
                    5 -> consume = dispatchForbidAccess(postcard, callback) // 红色通道
                    8 -> consume = dispatchSingleTask(postcard, callback) // 请求单例启动
                    9 -> consume = dispatchSingleTop(postcard, callback) // 请求清除栈顶启动
                    10 -> consume = dispatchVerityHasSuccess(postcard, callback) // 实名认证成功
                    11 -> consume = dispatchVerityBankCard(postcard, callback) // 银行卡不存在
                    12 -> consume = dispatchTradeLogin(postcard, callback) // 券商登录
                    else -> callback.onInterrupt(RuntimeException(getString(R.string.plugins_no_any_permission, permissions[position])))
                }

                if (!consume) {
                    callback.onInterrupt(RuntimeException(getString(R.string.plugins_cannot_jump_to_des)))
                }

                return
            }
        }

        //权限定义错误
        RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, getString(R.string.common_no_permission)))
    }

    /**
     * 红色通道
     */
    private fun dispatchForbidAccess(postcard: Postcard, callback: InterceptorCallback): Boolean {
        toast(R.string.plugins_cannot_jump_to_des)
        return false
    }

    /**
     * 未绑定邮箱
     */
    private fun dispatchEmail(postcard: Postcard, callback: InterceptorCallback): Boolean {
        return if (postcard.path.equals(Activity_ShowEmailActivity, ignoreCase = true)) {
            replaceDes(postcard, Activity_BindEmailActivity)
            process(postcard, callback)
            true
        } else {
            RxBus.instance?.post(ARouterNoPermission(
                    getString(R.string.plugins_no_email_permission),
                    getString(R.string.function_abandon), getString(R.string.function_go),
                    BasePresenter.HttpCallback<Any?> { ARouter.getInstance().build(Activity_BindEmailActivity).navigation() }))
            false
        }
    }

    /**
     * 请求单例启动
     *
     * 清除栈上其他活动
     */
    private fun dispatchSingleTask(postcard: Postcard, callback: InterceptorCallback): Boolean {
        postcard.withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        callback.onContinue(postcard)
        return true
    }

    /**
     * 请求清除栈顶启动
     *
     * 清除栈上其他活动
     */
    private fun dispatchSingleTop(postcard: Postcard, callback: InterceptorCallback): Boolean {
        postcard.withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        callback.onContinue(postcard)
        return true
    }


    /**
     * 实名认证已成功
     */
    private fun dispatchVerityHasSuccess(postcard: Postcard, callback: InterceptorCallback): Boolean {
        RxBus.instance?.post(ARouterNoPermission(
                getString(R.string.plugins_no_verify_success_permission),
                getString(R.string.function_abandon), getString(R.string.function_go),
                BasePresenter.HttpCallback<Any?> { ARouter.getInstance().build(Activity_ShowCertificationResultActivity).navigation() }))
        return false
    }

    /**
     * 没有银行卡
     */
    private fun dispatchVerityBankCard(postcard: Postcard, callback: InterceptorCallback): Boolean {
        RxBus.instance?.post(ARouterNoPermission(
                getString(R.string.plugins_no_bank_card),
                getString(R.string.function_abandon), getString(R.string.function_go),
                BasePresenter.HttpCallback<Any?> { ARouter.getInstance().build(Activity_AddBankActivity).navigation() }))
        return false
    }

    /**
     * 未开始实名认证
     */
    private fun dispatchVerityHasBegin(postcard: Postcard, callback: InterceptorCallback): Boolean {
        replaceDes(postcard, Activity_BeginCertificationActivity)
        process(postcard, callback)
        return true
    }

    /**
     * 未绑定手机
     *
     *
     * 默认toast
     *
     *
     * 在未绑定手机时,必须先去实名认证,然后才能显示手机号绑定结果
     *
     * @return 返回true表示逻辑由方法自身来处理, false则会采取默认操作:抛出异常
     */
    private fun dispatchMobile(postcard: Postcard, callback: InterceptorCallback): Boolean {
        return if (postcard.path.equals(Activity_ShowMobileActivity, ignoreCase = true)) {
            replaceDes(postcard, Activity_BindMobileActivity)
            process(postcard, callback)
            true
        } else {
            RxBus.instance?.post(ARouterNoPermission(
                    getString(R.string.plugins_no_phone_question_permission),
                    getString(R.string.function_abandon), getString(R.string.function_go),
                    BasePresenter.HttpCallback<Any?> { ARouter.getInstance().build(Activity_BindMobileActivity).navigation() }))
            false
        }
    }

    /**
     * 未设置交易密码
     * 则跳转到设置交易密码界面
     *
     *
     * 默认提示toast
     */
    private fun dispatchTransactionPassword(postcard: Postcard, callback: InterceptorCallback): Boolean {
        //如果是"显示交易密码结果"界面,则跳转到设置交易密码界面,其他的话,都弹出提示进行选择
        return if (postcard.path in arrayOf(ARouterConst.Activity_ShowTransResultActivity)) {
            replaceDes(postcard, ARouterConst.Activity_SetTransactionPasswordActivity)
            process(postcard, callback)
            true
        } else {
            RxBus.instance?.post(ARouterNoPermission(
                    getString(R.string.plugins_no_trans_pwd_permission),
                    getString(R.string.function_abandon), getString(R.string.function_go),
                    BasePresenter.HttpCallback<Any?> { ARouter.getInstance().build(Activity_SetTransactionPasswordActivity).navigation() }))
            false
        }
    }

    /**
     * 处理未登录操作
     */
    private fun dispatchLogin(postcard: Postcard, callback: InterceptorCallback): Boolean {
        if (postcard.path in arrayOf(ARouterConst.Activity_SignActivity, ARouterConst.Activity_InviteRewardActivity)) {
            replaceDes(postcard, ARouterConst.Activity_LoginActivity)
            process(postcard, callback)
            return true
        }

        // 2019.08.01 修改无登录权限时直接跳转到登录界面
        empty {
            RxBus.instance?.post(BaseEvent(Const.SHOW_LOGIN_DIALOG, null))
        }
        route(ARouterConst.Activity_LoginActivity)
        return false
    }

    /**
     * 红色通道
     */
    private fun dispatchTradeLogin(postcard: Postcard, callback: InterceptorCallback): Boolean {
        toast("请先登录券商!")
        return false
    }

    override fun init(context: Context) {
        // 拦截器的初始化，会在sdk初始化的时候调用该方法，仅会调用一次
    }

    /**
     * 更换意图的跳转路径
     * 然后进行跳转处理
     *
     * @param postcard 意图
     * @param des      目的 string
     */
    private fun replaceDes(postcard: Postcard, des: String) {
        //动态的修改postcard信息,更换跳转路径
        val newPostcard = ARouter.getInstance().build(des)
        LogisticsCenter.completion(newPostcard)
        postcard.setExtra(newPostcard.extra).setPath(newPostcard.path).setGroup(newPostcard.group).destination = newPostcard.destination
    }
}

/**
 * function : 自定义ARouter跳转全局降级，当跳转失败的话，可以进行处理，保证程序健壮性
 *
 *
 * 必须实现DegradeService接口，并加上一个Path内容任意的注解
 *
 * @author LinkTech
 * @date 2017/11/30
 */
@Route(path = "/degrade/DegradeServiceImpl")
class DegradeServiceImpl : DegradeService {
    /**
     * Router has lost.
     *
     * @param postcard meta
     */
    override fun onLost(context: Context?, postcard: Postcard?) {
        Logger.t(Const.TAG_LOGGER_REPORT_NET).e("ARouter:无法跳转(全局降级策略):\n path: ${postcard?.path} \n destination ${postcard?.destination} ")
        toast(R.string.plugins_cannot_jump_to_des)
    }

    /**
     * Do your init work in this method, it well be call when processor has been load.
     *
     * @param context ctx
     */
    override fun init(context: Context?) {
    }
}

/**
 * 功能----Aouter框架:如果需要传递自定义对象，需要实现 SerializationService,并使用@Route注解标注(方便用户自行选择序列化方式)
 *
 *
 * Created by LinkTech on 2017/11/14.
 */
@Route(path = "/service/json")
class JsonServiceImpl : SerializationService {
    override fun object2Json(instance: Any?): String {
        return Gson().toJson(instance)
    }

    override fun <T> parseObject(input: String?, clazz: Type): T? {
        return Gson().fromJson<T>(input, clazz)
    }

    override fun init(context: Context) {

    }

    override fun <T> json2Object(input: String, clazz: Class<T>): T? {
        return null
    }
}

/**
 * function : ARouter框架回调接口,默认实现所有方法
 *
 * 默认当跳转成功后,结束自身
 * 适用于目标不存在任何权限要求的情况,在添加该callback后系统默认的全局降级策略将失效
 *
 * Created on 2018/6/27  14:35
 * @author mnlin
 */
class ARouterFinishCallback(/*当前所在的activity*/private val activity: BaseActivity<*>?) : NavigationCallback {
    /**
     * Callback when find the destination.
     *
     * @param postcard meta
     */
    override fun onFound(postcard: Postcard?) {
        Logger.d("ARouter:找到路由路径:$postcard")
    }

    /**
     * Callback after lose your way.
     *
     * @param postcard meta
     */
    override fun onLost(postcard: Postcard?) {
        Logger.t(Const.TAG_LOGGER_REPORT_NET).e("ARouter:跳转失败(单独降级策略):$postcard")
    }

    /**
     * Callback after navigationWithArrivalRun.
     *
     * @param postcard meta
     */
    override fun onArrival(postcard: Postcard?) {
        Logger.d("ARouter:跳转成功(将结束自身):$postcard")
        if (activity != null && !activity.isDestroyed) {
            activity.finish()
        }
    }

    /**
     * Callback on interrupt.
     *
     * @param postcard meta
     */
    override fun onInterrupt(postcard: Postcard?) {
        Logger.t(Const.TAG_LOGGER_REPORT_NET).e("ARouter:跳转被拦截:$postcard")
    }
}
