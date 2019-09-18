package com.linktech.gft.activity.safety


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.util.RegexConst
import com.wangnan.library.listener.OnGestureLockListener
import com.wangnan.library.painter.AliPayPainter
import kotlinx.android.synthetic.main.activity_gesture_setting.*

/**
 * function : 设置手势信息
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_GestureSettingActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_gesture_setting)
@InjectActivityTitle(titleRes = R.string.label_gesture_setting)
class GestureSettingActivity : BaseActivity<BasePresenter<GestureSettingActivity>>(), LineMenuListener, OnGestureLockListener {
    /**
     * 手势值,0-9,4-9位,非重复
     */
    private var gesture: String? = null

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //手势
        glv_gesture.setPainter(AliPayPainter())
        glv_gesture.setGestureLockListener(this)

        //不设置/返回
        tv_return.dOnClick {
            onBackPressed()
        }

        // 初始化
        refreshLabel()
    }

    /**
     * 第一次/再次
     */
    private fun refreshLabel() {
        tv_label.text = if (gesture == null) getString(R.string.activity_gesture_setting_label_one) else getString(R.string.activity_gesture_setting_label_two)
    }

    override fun performSelf(lmv: LineMenuView) {
        DefaultPreferenceUtil.getInstance().setScreenFaceLocked(_user_id, !lmv.transition)
        lmv.transition = !lmv.transition
    }

    /**
     * 与本地手势密码进行验证
     */
    override fun onComplete(result: String?) {
        when {
            gesture == null ->
                gesture = result?.let {
                    if (it matches RegexConst.REGEX_LOCK_GESTURE) {
                        glv_gesture.clearView()
                        it
                    } else {
                        toast(getString(R.string.activity_gesture_setting_regex))
                        glv_gesture.showErrorStatus(500)
                        null
                    }
                }
            gesture.equals(result) means "如果非第一次设置,则判断两者是否相等,相等则表示设置完成" -> {
                DefaultPreferenceUtil.getInstance().setScreenGestureLocked(_user_id, gesture.toMD5())
                finish()
            }
            else -> {
                gesture = null
                toast(getString(R.string.activity_gesture_setting_not_equal))
                glv_gesture.showErrorStatus(500)
            }
        }

        refreshLabel()
    }

    override fun onStarted() {
        empty(TODO = "开始")
    }

    override fun onProgress(progress: String?) {
        empty(TODO = "过程中")
    }
}
