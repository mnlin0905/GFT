package com.linktech.gft.activity.person


import android.support.annotation.ColorRes
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.CertificationResultBean
import com.linktech.gft.drawable.ColorTextDrawable
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.window.AlertCloseDialogFragment
import kotlinx.android.synthetic.main.activity_show_certification_result.*
import org.jetbrains.anko.imageResource

/**
 * function---- ShowCertificationResultActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/17 08:00:22 (+0000).
 */
@Route(path = ARouterConst.Activity_ShowCertificationResultActivity, extras = ARouterConst.FLAG_VERIFY_HAS_BEGIN or ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_show_certification_result)
@InjectActivityTitle(titleRes = R.string.label_show_certification_result)
class ShowCertificationResultActivity : BaseActivity<BasePresenter<ShowCertificationResultActivity>>(), LineMenuListener {
    /**
     * 实名认证状态
     */
    private var certificationStatus: Int = 0

    /**
     * 实名认证信息
     */
    private var resultBean: CertificationResultBean? = null

    override fun onResume() {
        super.onResume()

        // 重新赋值
        certificationStatus = BasePresenter.singleUserInfo.certification_status

        //图标/背景
        val drawable = intArrayOf(R.drawable.icon_certification_access, R.drawable.icon_certification_success, R.drawable.icon_certification_fail)
        val background = intArrayOf(R.drawable.bg_certification_access, R.drawable.bg_certification_success, R.drawable.bg_certification_fail)

        //提示性信息
        val message = resources.getStringArray(R.array.array_certification_label_status)

        //状态文字
        val navigation = getStringArray(R.array.array_certification_label_status_brief)

        //状态颜色
        val navigationColor = intArrayOf(R.color.skin_display3_color, R.color.skin_display2_color, R.color.skin_display4_color)

        if (certificationStatus !in 0..2) {
            showToast(getString(R.string.activity_show_certification_result_status_error))
            finish()
            return
        }

        mPresenter.getExamineInfo(_username, _signature) {
            resultBean = it
            resultBean?.also {
                if (it.cardType !in Const.TYPE_ID_CARD_IDENTIFICATION..Const.TYPE_ID_CARD_OTHER) {
                    showToast(getString(R.string.activity_show_certification_result_card_error))
                    finish()
                    return@getExamineInfo
                }

                //初始化界面
                iv_icon.imageResource = background[certificationStatus]
                tv_message.setCompoundDrawablesWithIntrinsicBounds(0, drawable[certificationStatus], 0, 0)
                tv_message.text = message[certificationStatus]
                setStatus(navigation[certificationStatus], navigationColor[certificationStatus], certificationStatus == 2)
                lmv_name.briefText = it.name?.replace("^.*(\\S)$".toRegex(), "**$1") ?: getString(R.string.common_unknown_value)
                lmv_type.briefText = resources.getStringArray(R.array.array_person_card_type)[it.cardType - 1]
                lmv_number.briefText = it.idcard?.replace("^(\\S).*(\\S)$".toRegex(), "$1*****$2") ?: "****"
            }
        }
    }

    /**
     * 设置审核状态
     *
     * @param navigation      文字
     * @param navigationColor 颜色
     * @param showBrief       显示brief文字
     */
    private fun setStatus(navigation: String, @ColorRes navigationColor: Int, showBrief: Boolean) {
        val textDrawable = ColorTextDrawable(this).setText(navigation)
                .setColor(dispatchGetColor(navigationColor))
                .setTextSize(resources.getDimensionPixelSize(R.dimen.text_size_normal_14sp))
                .setDefaultBounds()
        lmv_info.setBriefColor(dispatchGetColor(if (showBrief) R.color.black_text_666 else R.color.mc_divider_gray))
        lmv_info.briefText = if (showBrief) getString(R.string.activity_show_certification_result_check_reason) else ""
        lmv_info.findViewById<TextView>(R.id.tv_brief_info_line_menu_view).setCompoundDrawables(null, null, textDrawable, null)
    }

    /**
     * 点击左侧,拦截响应点击事件
     */
    override fun performClickLeft(v: TextView): Boolean {
        return true
    }

    override fun performSelf(v: LineMenuView) {
        if (resultBean != null && v === lmv_info && certificationStatus == 2) {
            AlertCloseDialogFragment()
                    .setConfirmText(getString(R.string.activity_show_certification_result_verify_again))
                    .setTitle(resultBean!!.notPassing)
                    .setOnAlertListener {
                        route(ARouterConst.Activity_BeginCertificationActivity)
                        false
                    }.show(sfManager, "certification")
        }
    }
}