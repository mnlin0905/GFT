package com.linktech.gft.activity.safety


import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_gesture_lock.*

/**
 * function : 手势锁屏
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_GestureLockActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_gesture_lock)
@InjectActivityTitle(titleRes = R.string.label_gesture_lock)
class GestureLockActivity : BaseActivity<BasePresenter<GestureLockActivity>>(), LineMenuListener {

    override fun onResume() {
        super.onResume()

        // 是否打开了手势解锁
        val lockOpen = DefaultPreferenceUtil.getInstance().getScreenGestureLocked(_user_id) != null

        //开关
        lmv_lock.transition = lockOpen

        //轨迹
        lmv_appear.visibility = if (lockOpen) View.VISIBLE else View.GONE
        lmv_appear.transition = DefaultPreferenceUtil.getInstance().isScreenGestureLockedTravel(_user_id)
    }

    override fun performSelf(lmv: LineMenuView) {
        when (lmv.position) {
            0 means "开关" -> {
                if (!lmv.transition) {
                    routeCustom(ARouterConst.Activity_GestureSettingActivity).navigation(this, Const.REQUEST_CODE_ONE)
                } else {
                    DefaultPreferenceUtil.getInstance().setScreenGestureLocked(_user_id, null)
                    lmv.transition = false
                    lmv_appear.visibility = View.GONE
                }
            }
            1 means "轨迹显示" -> {
                DefaultPreferenceUtil.getInstance().setScreenGestureLockedTravel(_user_id, !lmv.transition)
                lmv.transition = !lmv.transition
            }
            2 means "修改" -> {
                empty(TODO = "修改手势密码")
            }
        }
    }
}
