package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_establish_finish.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import org.jetbrains.anko.backgroundColor
import skin.support.content.res.SkinCompatResources

/**
 * function : 開戶完成
 *
 * 開戶完成 第 15 步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_EstablishFinishActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "開戶完成")
@InjectLayoutRes(layoutResId = R.layout.activity_establish_finish)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class EstablishFinishActivity : BaseActivity<BasePresenter<EstablishFinishActivity>>() {
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
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 直接下一步,无验证消息
        bt_common_function.apply {
            text = "確認"
            dOnClick(::verify) {
                when (establishStatusBean?.status) {
                    4 means "失败" -> when (establishStatusBean?.stepNotPassed) {
                        1 means "失败于:身份证明--上传身份证照片" -> ARouterConst.Activity_UploadIdCardActivity
                        2 means "失败于:确认身份信息" -> ARouterConst.Activity_CommitInfoBasicActivity
                        3 means "失败于:填写个人信息" -> ARouterConst.Activity_CommitInfoMoreActivity
                        4 means "失败于:银行卡验证" -> ARouterConst.Activity_VerifyBankCardActivity
                        5 means "失败于:个人声明" -> ARouterConst.Activity_AgreementDeclareActivity
                        6 means "失败于:投资经验" -> ARouterConst.Activity_InvestmentExperienceActivity
                        7 means "失败于:财务状况" -> ARouterConst.Activity_FinancialPositionActivity
                        8 means "失败于:选择账户" -> ARouterConst.Activity_SelectAccountActivity
                        9 means "失败于:密码设置" -> ARouterConst.Activity_VerifyPasswordActivity
                        10 means "失败于:上传手持证件照" -> ARouterConst.Activity_UploadPhotoActivity
                        11 means "失败于,保存人脸照片,暂不支持" -> toast("暂不支持活体检测流程").let { null }
                        12 means "失败于:确认签名照" -> ARouterConst.Activity_SignatureNameActivity
                        else -> toast("异常的开户失败步骤").let { null }
                    }?.let {
                        routeCustom(it)
                                .param(establishStatusBean)
                                .transitionToolbar(baseActivity)
                                .navigationWithArrivalRun(baseActivity) { finish() }
                                .empty(comment = "关闭自身,开启某个步骤")
                    }
                    else -> route(ARouterConst.Activity_FinancingActivity)
                }
            }
        }

        // 表格
        tv_alert_click.highlightColor = dispatchGetColor(android.R.color.transparent)
        tv_alert_click.movementMethod = LinkMovementMethod()
        tv_alert_click.text = SpannableStringBuilder("3.如有需要，請查看")
                .append(SpannableString("《開戶申請表》").apply {
                    setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            // 跳转到协议界面
                            routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                                    .param("W-8BEN表格")
                                    .firstParam("${BaseApplication.app.establishProtocolUrl}table.html")
                                    .transitionToolbar(baseActivity)
                                    .navigation(baseActivity)
                                    .empty(comment = "协议界面")
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            //不设置下划线,变色
                            ds.color = dispatchGetColor(R.color.toolbar_second_color_primary)
                        }
                    }, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                })

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 回填
        establishBackFill()

        // 做一次数据刷新操作
        empty {
            mPresenter.getByUserIdAccount(_username, _signature) {
                if (it?.status != establishStatusBean?.status) {
                    routeCustom(ARouterConst.Activity_EstablishFinishActivity)
                            .param(it)
                            .transitionToolbar(baseActivity)
                            .navigation(baseActivity)
                            .empty(comment = "刷新数据")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        establishBackFill()
        onExistEditTextChanged()
    }

    /**
     * 开户状态回填
     */
    @SuppressLint("SetTextI18n")
    private fun establishBackFill() {
        establishStatusBean?.also {
            when (it.status) {
                1 means "申请中（信息填写中）,该中状态默认不应该在此界面出现,出现则直接退出" -> finish().toast("状态不应为审核中")
                2, 3 means "待审核" -> {
                    iv_status.setImageResource(R.drawable.ic_establish_finish_wait)
                    tv_status.text = "開戶資料已提交，請耐心等待審核預計時間1-2天！"
                }
                6 means "通过" -> {
                    iv_status.setImageResource(R.drawable.ic_establish_finish_success)
                    tv_status.text = "賬戶已開通，請檢查您的開戶郵件"
                    listOf(dv_one, dv_two, dv_three).forEach { it.backgroundColor = SkinCompatResources.getColor(this@EstablishFinishActivity, R.color.skin_accent_color) }
                    iv_two.setImageResource(R.drawable.ic_establish_finish_two_actived)
                    iv_three.setImageResource(R.drawable.ic_establish_finish_three_actived)
                    tv_success.visibility = View.VISIBLE
                    tv_success.dOnClick {
                        routeSuccessFinish(ARouterConst.Activity_TradeActivity).empty(comment = "登录券商")
                    }
                }
                4 means "未通過" -> {
                    iv_status.setImageResource(R.drawable.ic_establish_finish_error)
                    tv_status.text = "開戶審核不通過，原因：${it.notPassing}"
                }
                5 means "已关闭" -> finish().toast("未知状态:已关闭")
                else -> finish().toast("未知状态")
            }
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            else -> true.empty(TODO = "验证输入内容")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        setDefaultToolbarMenuText(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        route(ARouterConst.Activity_FinancingActivity)
        return true
    }

    override fun onBackPressed() {
        route(ARouterConst.Activity_FinancingActivity)
    }
}
