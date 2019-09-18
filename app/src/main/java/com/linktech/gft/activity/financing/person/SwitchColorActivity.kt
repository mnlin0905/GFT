package com.linktech.gft.activity.financing.person

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_switch_color.*
import skin.support.SkinCompatManager

/**
 * function---- 顏色切換
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 05:50:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SwitchColorActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_switch_color)
@InjectActivityTitle(titleRes = R.string.label_switch_color)
class SwitchColorActivity : BaseActivity<BasePresenter<SwitchColorActivity>>(), LineMenuListener {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        lmv_red_up.rightSelect = is_red_up
        lmv_green_up.rightSelect = !is_red_up
    }

    /**
     * @param v 被點擊到的v;此時應該是該view自身:LineMenuView
     */
    override fun performSelf(lmv: LineMenuView) {
        lmv.friendsWithSelf().map { it.rightSelect = false }
        lmv.rightSelect = true
        val position = lmv.getTag(LMVConfigs.TAG_POSITION) as Int
        if ((is_red_up && position == 1) || (!is_red_up && position == 0)) {
            DefaultPreferenceUtil.getInstance().isRedUpMode = position == 0
            //发广播
            RxBus.instance!!.post(SwitchColor(position == 0))

            // 更换系统颜色(迫使界面进行刷新)
            if(DefaultPreferenceUtil.getInstance().isWhiteMode){
                SkinCompatManager.getInstance().loadSkin("white.skin", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS)
            }else{
                SkinCompatManager.getInstance().restoreDefaultTheme()
            }
        }
    }

}
