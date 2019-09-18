package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.TimePickerView
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.RegexConst
import com.linktech.gft.window.ChangeSexDialogFragment
import kotlinx.android.synthetic.main.activity_commit_info_basic.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import kotlinx.android.synthetic.main.layout_dialog_commit_info_basic.view.*
import java.util.*

/**
 * function : 基础的个人资料填写
 *
 * 开户流程 第2步，信息有些需要取自身份证
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_CommitInfoBasicActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "個人資料")
@InjectLayoutRes(layoutResId = R.layout.activity_commit_info_basic)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class CommitInfoBasicActivity : BaseActivity<BasePresenter<CommitInfoBasicActivity>>(), LineMenuListener, ChangeSexDialogFragment.OnChangeSexListener, TimePickerView.OnTimeSelectListener {
    private var name: String by viewBind(R.id.wiet_name)
    private var pyFirst: String by viewBind(R.id.wiet_py_first)
    private var pyLast: String by viewBind(R.id.wiet_py_last)
    private var address: String by viewBind(R.id.wiet_address)
    private var idCard: String by viewBind(R.id.wiet_id_card)
    private var phone: String by viewBind(R.id.tv_phone)
    private var sex: Int? by setBindNullable {
        onExistEditTextChanged()
        lmv_sex.briefText = when (it) {
            1 -> "男"
            2 -> "女"
            else -> this.resetNullDirection()
        }
    }
    private var birthday: Calendar? by setBindNullable {
        onExistEditTextChanged()
        lmv_birthday.briefText = "${it?.get(Calendar.YEAR)}-${it?.get(Calendar.MONTH)?.plus(1)}-${it?.get(Calendar.DAY_OF_MONTH)}"
    }

    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    /**
     * singleDialog
     */
    private lateinit var singleDialog: DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams>

    /**
     * 提示窗口
     */
    private lateinit var sencondDialog: DefaultSimulateDialogImpl<*, *>

    /**
     * 初始化數據(可默認不實現)
     */
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        // 直接下一步,无验证消息
        bt_common_function.apply {
            text = getString(R.string.function_next_step)
            dOnClick(::verify) {
                sencondDialog.also {
                    it.generateView().tv_dialog_py.text = "姓名拼音：$pyFirst $pyLast"
                }.show(AlphaIDVGAnimatorImpl())
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 弹出窗口先初始化,方便之后弹出
        sencondDialog = generateSimpleDefaultDialog(includeDialog!!, R.layout.layout_dialog_commit_info_basic).also {
            it.generateView().apply {
                tv_dialog_cancel.dOnClick {
                    sencondDialog.close(true)
                }

                tv_dialog_confirm.dOnClick {
                    sencondDialog.close(true)
                    mPresenter.saveIdentity(_username, _signature, name, pyLast, pyFirst, sex!!, lmv_birthday.briefText, address, idCard) {
                        mPresenter.getByUserIdAccount(_username, _signature) {
                            routeCustom(ARouterConst.Activity_CommitInfoMoreActivity)
                                    .param(it)
                                    .transitionToolbar(baseActivity)
                                    .navigation(baseActivity)
                                    .empty(comment = "请求接口,然后下一步操作")
                        }
                    }
                }
            }
        }

        // 回填
        establishBackFill()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        establishBackFill()
        onExistEditTextChanged()
    }

    /**
     * 开户状态回填
     */
    private fun establishBackFill() {
        establishStatusBean?.also {
            name = it.name.ifNullReturn()
            pyFirst = it.firstName.ifNullReturn()
            pyLast = it.lastName.ifNullReturn()
            address = it.address.ifNullReturn()
            idCard = it.idcard.ifNullReturn()
            sex = it.sex

            // 生日解析
            it.birth?.replace("\\s".toRegex(), "")
                    ?.split("-")
                    ?.filterLet({ it.size >= 3 }) {
                        birthday = Calendar.getInstance().also { c ->
                            c.set(Calendar.YEAR, it[0].toInt())
                            c.set(Calendar.MONTH, it[1].toInt() - 1)
                            c.set(Calendar.DAY_OF_MONTH, it[2].toInt())
                        }
                    }
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            idCard notMatch RegexConst.REGEX_ID_CARD_NUMBER -> false.toast("身份证号格式错误")
            else -> true.empty(comment = "验证输入内容")
        }
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

    override fun performSelf(lmv: LineMenuView) {
        when (lmv.position) {
            0 means "性别" -> ChangeSexDialogFragment()
                    .setOnChangeSexListener(this)
                    .setSecretHide()
                    .show(sfManager, "change_sex")
            1 means "出生日期" -> TimePickerView.Builder(this, this)
                    .setRange(1900, Calendar.getInstance().get(Calendar.YEAR))
                    .setBgColor(dispatchGetSkinColor(R.color.skin_dialog_background_color))
                    .setCancelColor(dispatchGetSkinColor(R.color.skin_body1_color))
                    .setSubmitColor(dispatchGetSkinColor(R.color.skin_accent_color))
                    .setDividerColor(dispatchGetSkinColor(R.color.skin_divider_color))
                    .setTitleBgColor(dispatchGetSkinColor(R.color.skin_scaffold_deep_background_color))
                    .setTitleColor(dispatchGetSkinColor(R.color.skin_title_color))
                    .setTextColorCenter(dispatchGetSkinColor(R.color.skin_accent_color))
                    .setTextColorOut(dispatchGetSkinColor(R.color.skin_display1_color))
                    .setCancelText(dispatchGetString(R.string.function_cancel))
                    .setSubmitText(dispatchGetString(R.string.function_confirm))
                    .setType(booleanArrayOf(true, true, true, false, false, false))
                    .build()
                    .show()
        }
    }

    /**
     * 出生日期选择
     */
    override fun onTimeSelect(date: Date?, v: View?) {
        birthday = Calendar.getInstance().also { it.timeInMillis = date?.time ?: 0 }
    }

    /**
     * 返回true表示不需要默认操作
     */
    override fun onClickMan(dialog: Dialog?): Boolean {
        sex = 1
        return false
    }

    override fun onClickWoman(dialog: Dialog?): Boolean {
        sex = 2
        return false
    }

    override fun onExistEditTextChanged() {
        bt_common_function.isEnabled = listOf(name, pyFirst, pyLast, address, idCard).all { it.isNotEmpty() } && sex != null && birthday != null
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_UploadIdCardActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
