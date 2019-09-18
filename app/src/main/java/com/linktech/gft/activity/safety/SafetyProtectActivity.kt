package com.linktech.gft.activity.safety


import android.app.Activity
import android.content.Intent
import android.os.Build
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
import kotlinx.android.synthetic.main.activity_safety_protect.*
import org.jetbrains.anko.fingerprintManager
import org.jetbrains.anko.keyguardManager
import skin.support.content.res.SkinCompatResources

/**
 * function---- SafetyProtectActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_SafetyProtectActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_safety_protect)
@InjectActivityTitle(titleRes = R.string.fragment_person_safe)
class SafetyProtectActivity : BaseActivity<BasePresenter<SafetyProtectActivity>>(), LineMenuListener {
    override fun performSelf(v: LineMenuView) {
        when (v.position) {
            0 -> route(ARouterConst.Activity_ShowCertificationResultActivity)
            1 -> routeCustom(ARouterConst.Activity_ChangePasswordActivity).navigation(this, Const.REQUEST_CODE_ONE)
            2 ->
                if (Build.VERSION.SDK_INT >= 23 && keyguardManager.isKeyguardSecure && fingerprintManager.hasEnrolledFingerprints()) {
                    route(ARouterConst.Activity_FingerprintLockActivity)
                } else {
                    toast(R.string.activity_safety_protect_not_open_fingerprint)
                }
            3 -> route(ARouterConst.Activity_FaceLockActivity)
            4 -> route(ARouterConst.Activity_GestureLockActivity)
        }
    }

    override fun onResume() {
        super.onResume()

        when (if (_identification_status == -1) 3 else _identification_status) {
            in 0..3 -> lmv_verify.briefText = resources.getStringArray(R.array.array_certification_label_status_brief)[if (_identification_status == -1) 3 else _identification_status]
            else -> toast(R.string.activity_soft_setting_error_status)
        }

        // 根据状态修改颜色值
        lmv_verify.setBriefColor(if (_identification_status == 1) SkinCompatResources.getColor(this, R.color.skin_body1_color) else SkinCompatResources.getColor(this, R.color.skin_display1_color))

        // 指纹识别,刷脸登录
        lmv_fingerprint.briefText = if (DefaultPreferenceUtil.getInstance().isScreenFingerprintLocked(_user_id)) getString(R.string.activity_safety_protect_has_open) else getString(R.string.activity_safety_protect_not_open)
        lmv_face.briefText = if (DefaultPreferenceUtil.getInstance().isScreenFaceLocked(_user_id)) getString(R.string.activity_safety_protect_has_open) else getString(R.string.activity_safety_protect_not_open)
        lmv_gesture.briefText = if (DefaultPreferenceUtil.getInstance().getScreenGestureLocked(_user_id)!=null) getString(R.string.activity_safety_protect_has_open) else getString(R.string.activity_safety_protect_not_open)

        //判断指纹识别可见性
        (Build.VERSION.SDK_INT >= 23 && fingerprintManager.isHardwareDetected).also {
            lmv_fingerprint.visibility = if (it) View.VISIBLE else View.GONE
            dv_fingerprint.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 修改密码(成功)后,界面关闭
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ONE) {
            finish()
        }
    }
}