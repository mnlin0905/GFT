package com.linktech.gft.activity.financing.person

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * function---- SoftSettingActivity
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SettingActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_setting)
@InjectActivityTitle(titleRes = R.string.label_setting)
class SettingActivity : BaseActivity<BasePresenter<SettingActivity>>(), LineMenuListener {
    /**
     * 初始化數據
     *
     * @param savedInstanceState 已存儲對象
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //退出
        bt_exit.dOnClick {
            BaseApplication.isTradeLogin = false
            DefaultPreferenceUtil.getInstance().signature = null
            DefaultPreferenceUtil.getInstance().login = false.empty(comment = "退出登錄")
            routeCustom(ARouterConst.Activity_FinancingActivity)
                    .firstParam(2)
                    .navigation()
                    .toast(getString(R.string.activity_setting_exit_success))
        }
    }

    override fun onResume() {
        super.onResume()
        lmv_switch_mode.briefText = if (DefaultPreferenceUtil.getInstance().isWhiteMode) getString(R.string.activity_setting_day) else getString(R.string.activity_setting_night)
        lmv_switch_color.briefText = if (DefaultPreferenceUtil.getInstance().isRedUpMode) getString(R.string.activity_switch_color_red_green) else getString(R.string.activity_switch_color_green_red)
        lmv_switch_locale.briefText = getStringArray(R.array.array_locale_language)[DefaultPreferenceUtil.getInstance().localeLanguageSwitch]
        bt_exit.visibility = if (_status_login) View.VISIBLE else View.GONE
    }

    /**
     * @param v 被點擊到的v;此時應該是該view自身:LineMenuView
     */
    override fun performSelf(lmv: LineMenuView) {
        when (lmv) {
            lmv_clear_cache means "清除緩存" -> toast(getString(R.string.activity_setting_no_cache))
            else ->
                arrayOf(
                        ARouterConst.Activity_MessageAlertActivity means "提送設置",
                        ARouterConst.Activity_RefreshRateActivity means "刷新頻率",
                        ARouterConst.Activity_SafetyLockActivity means "交易功能安全鎖",
                        null means "清除緩存",
                        ARouterConst.Activity_SwitchModeActivity means "切換模式",
                        ARouterConst.Activity_SwitchLocaleInnerActivity means "切換語言",
                        ARouterConst.Activity_SwitchColorActivity means "漲跌顏色",
                        ARouterConst.Activity_AboutInnerActivity means "關於"
                ).getOrNull(lmv.position)?.let {
                    routeCustom(it).navigation(this@SettingActivity, Const.REQUEST_CODE_ONE)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //重启整个应用
        if (requestCode == Const.REQUEST_CODE_ONE && resultCode == Activity.RESULT_OK) {
            recreate()
        }
    }
}
