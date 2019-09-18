package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_signature_name_confirm.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import java.io.File

/**
 * function : 簽字確認
 *
 * 簽字確認 第 14 步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_SignatureNameConfirmActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "簽字確認")
@InjectLayoutRes(layoutResId = R.layout.activity_signature_name_confirm)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class SignatureNameConfirmActivity : BaseActivity<BasePresenter<SignatureNameConfirmActivity>>() {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    /**
     * 验证码验证值
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var drawablePath: String? = null

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
            text = "確認是我本人簽名"
            dOnClick(::verify) {
                val runnable: (String?) -> Unit = {
                    mPresenter.saveSignature(_username, _signature, it) {
                        mPresenter.getByUserIdAccount(_username, _signature) {
                            routeCustom(ARouterConst.Activity_EstablishFinishActivity)
                                    .param(it)
                                    .transitionToolbar(baseActivity)
                                    .navigation(baseActivity)
                                    .empty(comment = "提交签名图片")
                        }
                    }
                }

                // 如果需要,则重新上传图片,
                if (drawablePath != null) {
                    mPresenter.commonUploadPicture(_username, _signature, arrayOf(File(drawablePath))) {
                        if (it != null && it.size == 1) {
                            runnable(it[0].url)
                        }
                    }
                } else {
                    runnable(establishStatusBean!!.signaturePic)
                }
            }
        }

        // 手机号
        tv_phone.dOnClick { startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用")) }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

        // 显示出来(如果是进行设置,则需要使用前界面绘画板中内容)
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
        if (drawablePath != null) {
            iv_signature.setImageBitmap(BitmapFactory.decodeFile(drawablePath))
        } else {
            // 回填
            establishStatusBean?.also {
                Glide.with(this@SignatureNameConfirmActivity)
                        .load("${it.imgServerUrl}${it.signaturePic}")
                        .into(iv_signature)
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
        singleDialog.show(AlphaIDVGAnimatorImpl())
        return true
    }

    override fun onBackPressed() {
        routeCustom(ARouterConst.Activity_UploadPhotoActivity)
                .param(establishStatusBean)
                .transitionToolbar(baseActivity)
                .navigation(baseActivity)
    }
}
