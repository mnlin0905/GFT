package com.linktech.gft.activity.person

import android.annotation.SuppressLint
import android.os.Bundle
import cn.nekocode.badge.BadgeDrawable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import kotlinx.android.synthetic.main.activity_invite_reward.*

/**
 * function : 邀请码界面 -> 邀请记录
 *
 * Created on 2018/8/21  20:13
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_InviteRewardActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_invite_reward)
@InjectActivityTitle(titleRes = R.string.label_none)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF, APTPlugins.AROUTER])
class InviteRewardActivity : BaseActivity<BasePresenter<InviteRewardActivity>>() {
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //透明背景
        setToolbarColor(dispatchGetColor(R.color.transparent))

        //跳转
        tv_invite.dOnClick {
            ARouter.getInstance()
                    .build(ARouterConst.Activity_ShowInviteCodeActivity)
                    .navigation()
                    .empty(comment = "邀请码界面")
        }

        //序号
        iv_1.setImageDrawable(createBadge(1))
        iv_2.setImageDrawable(createBadge(2))
        iv_3.setImageDrawable(createBadge(3))
        iv_4.setImageDrawable(createBadge(4))
        iv_5.setImageDrawable(createBadge(5))
    }

    /**
     * 生成badge
     */
    private fun createBadge(num: Int): BadgeDrawable {
        return BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .number(num)
                .badgeColor(dispatchGetColor(R.color.blue_bg_8ca7fc))
                .textColor(dispatchGetColor(R.color.main_color_white))
                .textSize(dispatchGetDimen(R.dimen.text_size_normal_14sp).toFloat())
                .build()
    }
}