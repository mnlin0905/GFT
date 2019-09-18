package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.activity_agreement_declare.tv_phone
import kotlinx.android.synthetic.main.activity_upload_photo.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import java.io.File

/**
 * function : 上傳照片
 *
 * 上傳照片 第 12 步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_UploadPhotoActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "上傳照片")
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
@InjectLayoutRes(layoutResId = R.layout.activity_upload_photo)
class UploadPhotoActivity : BaseActivityTPhotoWPer<BasePresenter<UploadPhotoActivity>>() {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    private var phone: String by viewBind(R.id.tv_phone)

    /**
     * 是否是重新上传的图片
     */
    private var photoHasChange: Boolean = false
    private var addressPhoto: String? by setBindNullable {
        onExistEditTextChanged()
        Glide.with(this@UploadPhotoActivity)
                .load(if (photoHasChange) addressPhoto else "${establishStatusBean?.imgServerUrl}$addressPhoto")
                .into(iv_photo)
    }

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
            text = "下一步"
            dOnClick(::verify) {
                val runnable: (String?) -> Unit = {
                    mPresenter.saveHoldIdcard(_username, _signature, it) {
                        mPresenter.getByUserIdAccount(_username, _signature) {
                            routeCustom(ARouterConst.Activity_SignatureNameActivity)
                                    .param(it)
                                    .transitionToolbar(baseActivity)
                                    .navigation(baseActivity)
                                    .empty(comment = "签名")
                        }
                    }
                }

                // 如果没有修改过,还用缓存部分
                if (!photoHasChange) {
                    runnable(addressPhoto)
                } else {
                    // 已修改,重新上传图片
                    mPresenter.commonUploadPicture(_username, _signature, arrayOf(File(addressPhoto))) {
                        if (it != null && it.size == 1) {
                            runnable(it[0].url)
                        }
                    }
                }
            }
        }

        //
        iv_photo.dOnClick {
            showPicturePickDialog()
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 回填
        establishBackFill()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        photoHasChange = false
        establishBackFill()
        onExistEditTextChanged()
    }

    /**
     * 开户状态回填
     */
    private fun establishBackFill() {
        establishStatusBean?.also {
            addressPhoto = it.idcardHord
        }
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
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

    override fun takeSuccess(filePath: String, file: File) {
        photoHasChange = true
        addressPhoto = filePath
    }

    override fun onExistEditTextChanged() {
        super.onExistEditTextChanged()

        bt_common_function.isEnabled = !addressPhoto.isNullOrEmpty()
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_VerifyPasswordActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
