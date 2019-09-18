package com.linktech.gft.activity.financing.person

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
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
import com.linktech.gft.window.ProgressDialog
import kotlinx.android.synthetic.main.activity_switch_locale_inner.*

/**
 * function---- 內部語言切換
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SwitchLocaleInnerActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_switch_locale_inner)
@InjectActivityTitle(titleRes = R.string.label_switch_locale_inner)
class SwitchLocaleInnerActivity : BaseActivity<BasePresenter<SwitchLocaleInnerActivity>>(), LineMenuListener {
    /**
     * 保持切换语言的布局视图
     */
    private lateinit var views: List<LineMenuView>

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        views = listOf(lmv_auto, lmv_zh_cn, lmv_zh_tw, lmv_zh_hk, lmv_en)

        //默认一个语言
        views[DefaultPreferenceUtil.getInstance().localeLanguageSwitch].rightSelect = true
    }

    /**
     * @param v 被点击到的v;此时应该是该view自身:LineMenuView
     */
    override fun performSelf(v: LineMenuView) {
        if (!v.rightSelect) {
            v.position.let { position ->
                ProgressDialog.getInstance(this@SwitchLocaleInnerActivity).show()
                DefaultPreferenceUtil.getInstance().localeLanguageSwitch = position
                Const.LOCALE_LANGUAGE_TYPES[position]?.also {
                    BaseApplication.app.setLocale(it)
                } ?: BaseApplication.app.setLocale(BaseApplication.app.systemLocale)
                ProgressDialog.getInstance(this@SwitchLocaleInnerActivity).dismiss()

                //恢复上个状态
                for (i in views.indices) {
                    views[i].rightSelect = i == position
                }

                //刷新界面布局
                onConfigurationChanged(resources.configuration)
            }
        }
    }

    /**
     * 刷新當前界面:主要是標題和默認
     */
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        views[0].menuText = getStringArray(R.array.array_locale_language)[0]
        title = Unit.getString(R.string.label_switch_locale_inner)
        setResult(Activity.RESULT_OK)
    }
}
