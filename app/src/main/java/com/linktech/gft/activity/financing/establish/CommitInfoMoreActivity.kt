package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityTPhotoWPer
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.RegexConst
import com.linktech.gft.window.PickListOptionsDialog
import kotlinx.android.synthetic.main.activity_commit_info_more.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import java.io.File

/**
 * function : 更多的个人资料填写,邮箱等
 *
 * 开户流程 第3步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_CommitInfoMoreActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "個人資料")
@InjectLayoutRes(layoutResId = R.layout.activity_commit_info_more)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class CommitInfoMoreActivity : BaseActivityTPhotoWPer<BasePresenter<CommitInfoMoreActivity>>(), LineMenuListener {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    /**
     * 设定变量(edit-text绑定)
     */
    private var email: String by viewBind(R.id.wiet_email)
    private var company: String by viewBind(R.id.wiet_company)
    private var tax: String by viewBind(R.id.wiet_tax)
    private var phone: String by viewBind(R.id.tv_phone)

    /**
     * lmv set-inject
     */
    private var education: Int? by setBindNullable {
        onExistEditTextChanged()
        lmv_education.briefText = getStringArray(R.array.all_education_options).getOrNull(it
                ?: Const.VALUE_POSITION_NULL) ?: resetNullDirection()
    }
    private var employment: Int? by setBindNullable {
        onExistEditTextChanged()
        lmv_employment.briefText = getStringArray(R.array.all_employment_options).getOrNull(it
                ?: Const.VALUE_POSITION_NULL) ?: resetNullDirection()
    }
    private var industry: Int? by setBindNullable {
        onExistEditTextChanged()
        lmv_industry.briefText = getStringArray(R.array.all_industry_options).getOrNull(it
                ?: Const.VALUE_POSITION_NULL) ?: resetNullDirection()
    }
    private var duties: Int? by setBindNullable {
        onExistEditTextChanged()
        lmv_duties.briefText = getStringArray(R.array.all_duties_options).getOrNull(it
                ?: Const.VALUE_POSITION_NULL) ?: resetNullDirection()
    }

    /**
     * 国家地区
     */
    private var country: String? by setBindNullable {
        onExistEditTextChanged()
        lmv_country.briefText = it
    }

    /**
     * 住址证明
     */
    private var addressProofHasChange: Boolean = false
    private var addressProof: String? by setBindNullable {
        onExistEditTextChanged()
        tv_upload_address.text = if (it.isNullOrEmpty()) "點擊上傳" else "已选择"
    }

    /**
     * singleDialog
     */
    private lateinit var singleDialog: DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams>

    /**
     * options选择器
     */
    private lateinit var optionsPickDialog: PickListOptionsDialog

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
                // 如果必要,则上传凭证,否则,直接提交内容
                val runnable: (String?) -> Unit = {
                    mPresenter.savePersonalData(_username, _signature, rg_address.checkedRadioButtonId == R.id.rb_equal, email, it, education!! + 1, employment!! + 1, industry!! + 1, company, duties!! + 1, country, tax) {
                        mPresenter.getByUserIdAccount(_username, _signature) {
                            routeCustom(ARouterConst.Activity_VerifyBankCardActivity)
                                    .param(it)
                                    .transitionToolbar(baseActivity)
                                    .navigation(baseActivity)
                                    .empty(comment = "验证银行卡")
                        }
                    }
                }

                if (rg_address.checkedRadioButtonId == R.id.rb_different) {
                    // 如果没有修改过,还用缓存部分
                    if (!addressProofHasChange) {
                        runnable(addressProof)
                    } else {
                        // 已修改,重新上传图片
                        mPresenter.commonUploadPicture(_username, _signature, arrayOf(File(addressProof))) {
                            if (it != null && it.size == 1) {
                                runnable(it[0].url)
                            }
                        }
                    }
                } else {
                    runnable(null)
                }
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // 选择
        tv_upload_address.dOnClick {
            showPicturePickDialog()
        }

        // 通讯地址(切换)
        rg_address.setOnCheckedChangeListener { _, checkedId ->
            onExistEditTextChanged()
            ll_hide.visibility = if (checkedId == R.id.rb_different) View.VISIBLE else View.GONE
        }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 条件选择窗口
        optionsPickDialog = PickListOptionsDialog(container = includeDialog!!,
                options = mutableListOf(
                        getStringArray(R.array.all_education_options).toList(),
                        getStringArray(R.array.all_employment_options).toList(),
                        getStringArray(R.array.all_industry_options).toList(),
                        getStringArray(R.array.all_duties_options).toList()
                ),
                onSelectOptions = { type, position ->
                    when (type) {
                        0 means "学历" -> education = position
                        1 means "就业" -> employment = position
                        2 means "行业" -> industry = position
                        3 means "职务" -> duties = position
                    }
                    false
                }
        )

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
            email = it.email.ifNullReturn()
            company = it.companyName.ifNullReturn()
            tax = it.taxNumber.ifNullReturn()
            rg_address.check(if (it.contactAddress == 2) R.id.rb_different else R.id.rb_equal)
            addressProof = it.proofOfAddress
            education = it.educationType - 1
            employment = it.jobType - 1
            industry = it.industryCategory - 1
            duties = it.dutyType - 1
            country = it.taxState ?: "中国大陆"
            tax = it.idcard.ifNullReturn()
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            email notMatch RegexConst.REGEX_EMAIL -> false.toast("邮箱地址格式错误")
            else -> true.empty(TODO = "验证输入内容")
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
            0 means "学历", 1 means "就业", 2 means "行业", 3 means "职务" -> optionsPickDialog.show(lmv.position).empty(comment = "条件选择")
            4 means "国家" ->
                routeCustom(ARouterConst.Activity_ChooseCountryActivity)
                    .navigation(this, Const.REQUEST_CODE_ONE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ONE) {
            data?.apply {
                country = getStringExtra(Const.KEY_COUNTRY_NAME)
            }
        }
    }

    override fun takeSuccess(filePath: String, file: File) {
        addressProofHasChange = true
        addressProof = filePath
    }

    override fun onExistEditTextChanged() {
        bt_common_function.isEnabled = listOf(email, company, tax).all { it.isNotBlank() }
                && listOf(education, employment, industry, duties, country).all { it != null }
                && (rg_address.checkedRadioButtonId == R.id.rb_equal || !addressProof.isNullOrBlank())
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_CommitInfoBasicActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
