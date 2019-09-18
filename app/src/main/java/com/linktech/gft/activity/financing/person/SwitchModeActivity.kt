package com.linktech.gft.activity.financing.person

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_switch_mode.*
import skin.support.SkinCompatManager

/**
 * function---- 模式切換
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SwitchModeActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_switch_mode)
@InjectActivityTitle(titleRes = R.string.label_switch_mode)
class SwitchModeActivity : BaseActivity<BasePresenter<SwitchModeActivity>>(), LineMenuListener {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        lmv_black_skin.rightSelect = !DefaultPreferenceUtil.getInstance().isWhiteMode
        lmv_white_skin.rightSelect = DefaultPreferenceUtil.getInstance().isWhiteMode
    }

    /**
     * @param lmv 被點擊到的v;此時應該是該view自身:LineMenuView
     */
    override fun performSelf(lmv: LineMenuView) {
        lmv.friendsWithSelf().map { it.rightSelect = false }
        lmv.rightSelect = true
        val position = lmv.getTag(LMVConfigs.TAG_POSITION) as Int
        when (position) {
            0 -> SkinCompatManager.getInstance().loadSkin("white.skin", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS)
            1 -> SkinCompatManager.getInstance().restoreDefaultTheme()
        }
        DefaultPreferenceUtil.getInstance().isWhiteMode = position == 0
    }
}
