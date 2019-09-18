package com.linktech.gft.activity.financing.establish


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
import com.bumptech.glide.Glide
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityTPhotoWPer
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_upload_id_card.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import java.io.File

/**
 * function : 上传身份证照片(正反面)
 *
 * 开户流程 第1步，第2步信息需要识别第1步信息内容
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_UploadIdCardActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "上傳身份證照片")
@InjectLayoutRes(layoutResId = R.layout.activity_upload_id_card)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class UploadIdCardActivity : BaseActivityTPhotoWPer<BasePresenter<UploadIdCardActivity>>() {
    private var phone: String by viewBind(R.id.tv_phone)

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
     * 正反图片
     */
    private var currentIsFace: Boolean = true

    /**
     * 上传后 img 路径
     */
    private var faceChanged: Boolean = false
    private var fileFaceResult: String? by setBindNullable {
        onExistEditTextChanged()
        Glide.with(this@UploadIdCardActivity)
                .load(if (faceChanged) fileFaceResult else "${establishStatusBean?.imgServerUrl}$fileFaceResult")
                .into(iv_face)
    }
    private var backChanged: Boolean = false
    private var fileBackResult: String?  by setBindNullable {
        onExistEditTextChanged()
        Glide.with(this@UploadIdCardActivity)
                .load(if (backChanged) fileBackResult else "${establishStatusBean?.imgServerUrl}$fileBackResult")
                .into(iv_back)
    }

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 直接下一步,无验证消息
        bt_common_function.apply {
            text = getString(R.string.function_next_step)
            dOnClick(::verify) {
                val runnable: (String?, String?) -> Unit = { face, back ->
                    mPresenter.saveIdcard(_username, _signature, face, back) {
                        if (it != null && it.success) {
                            mPresenter.getByUserIdAccount(_username, _signature) {
                                routeCustom(ARouterConst.Activity_CommitInfoBasicActivity)
                                        .param(it)
                                        .transitionToolbar(baseActivity)
                                        .navigation(baseActivity)
                                        .empty(comment = "使用整体status来赋值,而非本界面提交结果")
                            }
                        } else {
                            toast("身份证上传照片有误,请重新上传")
                        }
                    }
                }

                // 上传图片
                when {
                    faceChanged && backChanged -> arrayOf(File(fileFaceResult), File(fileBackResult))
                    faceChanged -> arrayOf(File(fileFaceResult))
                    backChanged -> arrayOf(File(fileBackResult))
                    else -> {
                        runnable(fileFaceResult!!, fileBackResult!!)
                        null
                    }
                }?.let {
                    mPresenter.commonUploadPicture(_username, _signature, it) {
                        runnable(if (faceChanged) it?.getOrNull(0)?.url else fileFaceResult!!, if (backChanged) it?.getOrNull(1)?.url else fileBackResult!!)
                    }
                }
            }
        }

        // 手机号
        tv_phone.dOnClick {
            startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用"))
        }

        // 正面,背面
        iv_select_face.dOnClick {
            currentIsFace = true
            showPicturePickDialog()
        }
        iv_select_back.dOnClick {
            currentIsFace = false
            showPicturePickDialog()
        }

        // select
        listOf(tv_select_one, tv_select_two).forEach {
            it.highlightColor = dispatchGetColor(android.R.color.transparent)
            it.movementMethod = LinkMovementMethod()
            it.text = SpannableStringBuilder("掃描失敗?")
                    .append(SpannableString("拍攝").apply {
                        setSpan(object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                onClickTakePhone()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                //不设置下划线,变色
                                ds.color = dispatchGetColor(R.color.toolbar_second_color_primary)
                            }
                        }, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    })
                    .append("或")
                    .append(SpannableString("從相冊上傳").apply {
                        setSpan(object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                onClickFromAlbum()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                //不设置下划线,变色
                                ds.color = dispatchGetColor(R.color.toolbar_second_color_primary)
                            }
                        }, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    })
        }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 回填
        establishBackFill()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        backChanged = false
        faceChanged = false
        establishBackFill()
        onExistEditTextChanged()
    }

    /**
     * 开户状态回填
     */
    private fun establishBackFill() {
        establishStatusBean?.also {
            fileFaceResult = it.idcardPositive
            fileBackResult = it.idcardContrary
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            else -> true
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

    override fun onExistEditTextChanged() {
        super.onExistEditTextChanged()

        bt_common_function.isEnabled = !fileFaceResult.isNullOrBlank() && !fileBackResult.isNullOrEmpty()
    }

    override fun takeSuccess(filePath: String, file: File) {
        if (currentIsFace) {
            faceChanged = true
            fileFaceResult = filePath
        } else {
            backChanged = true
            fileBackResult = filePath
        }
    }

    override fun onBackPressed() {
        route(ARouterConst.Activity_FinancingActivity)
    }
}
