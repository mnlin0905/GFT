package com.linktech.gft.activity.financing.establish


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_open_preparation.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 未开户时进行开户
 *
 * 开户流程 预备
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_OpenPreparationActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "開戶準備")
@InjectLayoutRes(layoutResId = R.layout.activity_open_preparation)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class OpenPreparationActivity : BaseActivity<BasePresenter<OpenPreparationActivity>>() {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    private var phone: String by viewBind(R.id.tv_phone)

    /**
     * singleDialog
     */
    private lateinit var singleDialog: DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams>

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)


        // 直接下一步,无验证消息
        bt_common_function.apply {
            text = getString(R.string.function_next_step)
            dOnClick {
                routeCustom(ARouterConst.Activity_UploadIdCardActivity)
                        .param(establishStatusBean)
                        .transitionToolbar(baseActivity)
                        .navigationWithArrivalRun(baseActivity) { finish() }
                        .empty(comment = "不留存界面")
            }
        }

        // 手机号
        tv_phone.dOnClick {
            startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用"))
        }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        setDefaultToolbarMenuText(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        singleDialog.show(AlphaIDVGAnimatorImpl())
        return true
    }
}
