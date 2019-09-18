package com.linktech.gft.activity.person

import android.annotation.SuppressLint
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*

/**
 * function : 邀请码界面 -> 邀请记录
 *
 * Created on 2018/8/21  20:13
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_InvitePetGameActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_invite_pet_game)
@InjectActivityTitle(titleRes = R.string.label_none)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF, APTPlugins.DAGGER, APTPlugins.AROUTER])
class InvitePetGameActivity : BaseActivity<BasePresenter<InvitePetGameActivity>>() {
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //透明背景
        setToolbarColor(dispatchGetColor(R.color.transparent))
    }
}