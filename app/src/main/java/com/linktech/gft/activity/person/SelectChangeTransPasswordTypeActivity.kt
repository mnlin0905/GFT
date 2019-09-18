package com.linktech.gft.activity.person


import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.TransPasswordStatus
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.empty
import com.linktech.gft.util.Const

/**
 * function---- SelectChangeTransPasswordTypeActivity
 *
 *
 * 选择修改交易密码的方式
 * 必须是已经设置过交易密码才可以进入
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 12:09:02 (+0000).
 */
@Route(path = ARouterConst.Activity_SelectChangeTransPasswordTypeActivity, extras = ARouterConst.FLAG_TRANSACTION_PASSWORD)
@InjectLayoutRes(layoutResId = R.layout.activity_select_change_trans_password_type)
@InjectActivityTitle(titleRes = R.string.label_select_change_trans_password_type)
class SelectChangeTransPasswordTypeActivity : BaseActivity<BasePresenter<SelectChangeTransPasswordTypeActivity>>(), LineMenuListener {

    /**
     * 人工审核状态
     */
    private var transPasswordStatus: TransPasswordStatus? = null
    private var hasCheckStatus: Boolean = false

    override fun performSelf(v: LineMenuView) {
        when (v.getTag(LMVConfigs.TAG_POSITION) as Int) {
            0 -> //使用旧密码修改
                ARouter.getInstance().build(ARouterConst.Activity_InputOldTransPasswordActivity).navigation()
            1 -> //刷脸
                empty("")
            2 -> {//短信+证件号
                if (!_status_moblie) {
                    showToast(getString(R.string.activity_select_change_trans_password_type_not_bind_mobile))
                    return
                }
                if (!_status_identification) {
                    showToast(getString(R.string.activity_select_change_trans_password_type_not_verify))
                    return
                }
                ARouter.getInstance().build(ARouterConst.Activity_InputCodeActivity)
                        .withString(Const.KEY_PHONE, _phone)
                        .withString(Const.KEY_PHONE_CODE, "")
                        .withInt(Const.KEY_CODE_TYPE, 5).navigation()
            }
        }
    }

    /**
     * 检查状态后,进行处理
     */
    fun getPayPwdArtificialFinish(status: TransPasswordStatus) {
        hasCheckStatus = true
        this.transPasswordStatus = status
    }
}