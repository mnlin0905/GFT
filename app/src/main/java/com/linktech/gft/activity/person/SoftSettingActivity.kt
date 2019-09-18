package com.linktech.gft.activity.person

import android.app.Activity
import android.content.Intent
import android.support.design.widget.AppBarLayout
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.linemenuview.lmv_text
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.view.DividerView
import org.jetbrains.anko.*

/**
 * function---- SoftSettingActivity
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SoftSettingActivity,extras = ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(titleRes = R.string.label_soft_setting)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF, APTPlugins.AROUTER, APTPlugins.DAGGER])
class SoftSettingActivity : BaseActivity<BasePresenter<SoftSettingActivity>>(), LineMenuListener {
    /**
     * 布局文件控件
     */
    private lateinit var lmv_locale: LineMenuView

    override fun getContentOrViewId(): Int {
        verticalLayout {
            include<AppBarLayout>(R.layout.dark_layout_top_bar)

            scrollView {
                overScrollMode = View.OVER_SCROLL_ALWAYS
                isVerticalScrollBarEnabled = false

                verticalLayout {
                    /**
                     * 生成较为简单的布局
                     */
                    val simple: (Int, action: ViewGroup.MarginLayoutParams.() -> Unit) -> LineMenuView = { str, action ->
                        lmv_text(menuText = getString(str)).lparams(width = matchParent) {
                            action()
                        }
                    }

                    //系统语言
                    lmv_locale = simple(R.string.activity_soft_setting_language) {
                        topMargin = dimen(R.dimen.view_padding_margin_10dp)
                    }.setBriefColor(dispatchGetColor(R.color.black_text_171a3f))
                }.lparams(width = matchParent).applyRecursively {
                    //如果是LineMenuView,DividerView,则添加bg,添加padding,
                    val exec_lmv_dv: View.() -> Unit = {
                        horizontalPadding = dimen(R.dimen.view_padding_margin_16dp)
                        backgroundColorResource = R.color.main_color_white
                    }

                    if (it is LineMenuView) {
                        it.exec_lmv_dv()
                        it.setNavigation(dispatchGetDrawable(R.drawable.icon_arrow_right))
                    }

                    if (it is DividerView) {
                        it.exec_lmv_dv()
                    }
                }
            }.lparams(matchParent, matchParent)
        }

        return 0
    }

    override fun onStart() {
        super.onStart()

        lmv_locale.briefText = DefaultPreferenceUtil.getInstance().getStringArray(R.array.array_locale_language)[DefaultPreferenceUtil.getInstance().localeLanguageSwitch]
    }

    /**
     * @param v 被点击到的v;此时应该是该view自身:LineMenuView
     */
    override fun performSelf(v: LineMenuView) {
        (v.getTag(LMVConfigs.TAG_POSITION) as Int).let { position ->
            NAVIGATION_ACTIVITY[position].also {
                ARouter.getInstance()
                        .build(it)
                        .navigation(this@SoftSettingActivity, Const.REQUEST_CODE_ONE)
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

/**
 * 需要跳转到的活动或界面
 */
private val NAVIGATION_ACTIVITY = arrayOf(
        ARouterConst.Activity_SwitchLocaleActivity//切換中英文環境
)
