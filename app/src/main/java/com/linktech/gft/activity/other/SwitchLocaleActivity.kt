package com.linktech.gft.activity.other

import android.app.Activity
import android.content.res.Configuration
import android.support.design.widget.AppBarLayout
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.linemenuview.lmv_select
import com.linktech.gft.R
import com.linktech.gft.activity.person.SoftSettingActivity
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const.LOCALE_LANGUAGE_TYPES
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.view.DividerView
import com.linktech.gft.view.dv_line
import com.linktech.gft.window.ProgressDialog
import org.jetbrains.anko.*

/**
 * function---- SoftSettingActivity
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SwitchLocaleActivity)
@InjectActivityTitle(titleRes = R.string.label_switch_locale)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF, APTPlugins.AROUTER, APTPlugins.DAGGER])
class SwitchLocaleActivity : BaseActivity<BasePresenter<SoftSettingActivity>>(), LineMenuListener {
    /**
     * 布局文件控件
     *
     * 默认
     * 中文
     * 台湾
     * 香港
     * 英文
     */
    private var lmvs = arrayOfNulls<LineMenuView>(5)

    override fun getContentOrViewId(): Int {
        verticalLayout {
            include<AppBarLayout>(R.layout.dark_layout_top_bar)

            scrollView {
                overScrollMode = View.OVER_SCROLL_ALWAYS
                isVerticalScrollBarEnabled = false

                verticalLayout {
                    //系统存储的值
                    val locale = DefaultPreferenceUtil.getInstance().localeLanguageSwitch
                    val menus = getStringArray(R.array.array_locale_language)

                    //初始化界面
                    for (i in lmvs.indices) {
                        //lmv
                        lmvs[i] = lmv_select(menuText = menus[i]) {
                            rightSelect = i == locale
                        }.lparams(width = matchParent) {
                            if (i == 0) {
                                topMargin = dimen(R.dimen.view_padding_margin_10dp)
                            }
                        }

                        //分隔符divider
                        if (i < lmvs.size - 1) {
                            dv_line().lparams(width = matchParent)
                        }
                    }
                }.lparams(width = matchParent).applyRecursively {
                    if (it is LineMenuView || it is DividerView) {
                        it.horizontalPadding = dimen(R.dimen.view_padding_margin_16dp)
                        it.backgroundResource=R.drawable.rp_main_white_has_border
                    }
                }
            }.lparams(matchParent, matchParent)
        }

        return 0
    }

    /**
     * @param v 被点击到的v;此时应该是该view自身:LineMenuView
     */
    override fun performSelf(v: LineMenuView) {
        if (!v.rightSelect) {
            (v.getTag(LMVConfigs.TAG_POSITION) as Int).let { position ->
                ProgressDialog.getInstance(this@SwitchLocaleActivity).show()
                DefaultPreferenceUtil.getInstance().localeLanguageSwitch = position
                LOCALE_LANGUAGE_TYPES[position]?.also {
                    BaseApplication.app.setLocale(it)
                } ?: BaseApplication.app.setLocale(BaseApplication.app.systemLocale)
                ProgressDialog.getInstance(this@SwitchLocaleActivity).dismiss()

                //恢复上个状态
                for (i in lmvs.indices) {
                    lmvs[i]?.rightSelect = i == position
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
        lmvs[0]?.menuText = getStringArray(R.array.array_locale_language)[0]
        title = Unit.getString(R.string.label_switch_locale)
        setResult(Activity.RESULT_OK)
    }
}
