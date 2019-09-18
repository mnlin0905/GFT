package com.linktech.gft.activity.safety


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_fingerprint_lock.*
import skin.support.content.res.SkinCompatResources

/**
 * function : 指纹锁屏
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_FingerprintLockActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_fingerprint_lock)
@InjectActivityTitle(titleRes = R.string.label_fingerprint_lock)
class FingerprintLockActivity : BaseActivity<BasePresenter<FingerprintLockActivity>>(), LineMenuListener {

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 文本
        tv_label.highlightColor = dispatchGetColor(android.R.color.transparent)
        tv_label.movementMethod = LinkMovementMethod()
        tv_label.text = SpannableStringBuilder(getString(R.string.activity_fingerprint_lock_label)).empty {
            append(SpannableString(getString(R.string.activity_fingerprint_lock_service)).also {
                it.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 跳转到协议界面
                        routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                                .param(getString(R.string.activity_about_inner_service))
                                .firstParam("${BaseApplication.app.baseWeUrl}gft_zh_tw/userProto.html")
                                .navigation()
                                .empty(comment = "協議內容")
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        //不设置下划线,变色
                        ds.color = SkinCompatResources.getColor(this@FingerprintLockActivity, R.color.skin_accent_color)
                    }
                }, 0, it.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            })
        }

        //锁定
        lmv_lock.transition = DefaultPreferenceUtil.getInstance().isScreenFingerprintLocked(_user_id)
    }

    @SuppressLint("CheckResult")
    override fun performSelf(lmv: LineMenuView) {
        DefaultPreferenceUtil.getInstance().setScreenFingerprintLocked(_user_id, !lmv.transition)
        lmv.transition = !lmv.transition

        // 获取指纹库id
        if (lmv.transition) {
            getCurrentFingerprintHash {
                DefaultPreferenceUtil.getInstance().fingerprintMD5 = it
            }
        }
    }
}
