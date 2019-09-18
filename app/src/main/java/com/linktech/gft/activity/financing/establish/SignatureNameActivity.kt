package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signature_name.*
import java.io.File
import java.io.FileOutputStream


/**
 * function : 簽字確認
 *
 * 簽字確認 第 14 步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_SignatureNameActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "簽字確認")
@InjectLayoutRes(layoutResId = R.layout.activity_signature_name)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class SignatureNameActivity : BaseActivity<BasePresenter<SignatureNameActivity>>() {
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
     * 初始化數據(可默認不實現)
     */
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 重绘
        bt_rewrite.dOnClick {
            mpv_signature.reset()
        }

        // 确认
        bt_confirm.dOnClick(::verify) {
            Observable.just("signature_${System.currentTimeMillis()}.png")
                    .map {
                        val file = File(cacheDir, it)
                        val out = FileOutputStream(file)
                        loadBitmapFromView(mpv_signature).compress(Bitmap.CompressFormat.PNG, 100, out)
                        out.flush()
                        out.close()
                        file.path
                    }
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        routeCustom(ARouterConst.Activity_SignatureNameConfirmActivity)
                                .param(establishStatusBean)
                                .firstParam(it)
                                .transitionToolbar(baseActivity)
                                .navigation(baseActivity)
                                .empty(comment = "跳转下一页面进行显示")
                    }) {
                        toast("无法保存签名:${it.message}")
                    }
        }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()

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
            Glide.with(this@SignatureNameActivity)
                    .asBitmap()
                    .load("${it.imgServerUrl}${it.signaturePic}")
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            resource?.also {
                                mpv_signature.preDrawBitmap = getRotateBitmap(it, 90F)
                            }
                            return true
                        }
                    })
                    .submit()
        }
    }

    /**
     * 从view 获取到显示的 bitmap
     */
    private fun loadBitmapFromView(view: View): Bitmap {
        return Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).let {
            val c = Canvas(it)

            // 绘制
            c.drawColor(Color.WHITE)
            view.draw(c)

            // 旋转90度
            getRotateBitmap(it, -90F)
        }
    }

    /**
     * 获取
     */
    private fun getRotateBitmap(source: Bitmap, angle: Float): Bitmap {
        // 旋转90度
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            mpv_signature.mPath.isEmpty && mpv_signature.preDrawBitmap == null -> false.toast("请先进行签名")
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
