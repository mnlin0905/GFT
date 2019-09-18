package com.linktech.gft.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.CallSuper
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.linktech.gft.R
import com.linktech.gft.plugins.kindAnyReturn
import com.linktech.gft.plugins.toast
import com.linktech.gft.util.Const
import com.linktech.gft.window.ChangePictureDialogFragment
import java.io.File

/**
 * function : 扫描二维码基类,可以通过相机或者通过文件
 *
 * 直接调用 showQRTypePick 方法即可
 *
 * Created on 2018/8/15  16:59
 * @author mnlin
 */
abstract class BaseActivityQrScan<T : BasePresenter<*>> : BaseActivityTPhotoWPer<T>() {
    /**
     * 当获取到二维码时进行回调
     */
    private var onQrGetSuccess: ((qr: String) -> Unit)? = null

    /**
     * 初始化二维码插件
     */
    @CallSuper
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
    }

    @Deprecated(message = "不支持", replaceWith = ReplaceWith(expression = "method: showQRTypePick"))
    override fun showPicturePickDialog() {
        throw RuntimeException(getString(R.string.base_total_cannot_support_method))
    }

    @Deprecated(message = "不支持", replaceWith = ReplaceWith(expression = "method: showQRTypePick"))
    override fun showPicturePickDialogWithDefault() {
        throw RuntimeException(getString(R.string.base_total_cannot_support_method))
    }

    /**
     * 选择qr的方式 : 相机/图片
     */
    protected fun showQRTypePick(onQrGetSuccess: ((qr: String) -> Unit)?) {
        this.onQrGetSuccess = onQrGetSuccess

        ChangePictureDialogFragment()
                .setShowFunction(true, true, false, false)
                .setFromAlbumText(getString(R.string.base_total_album_select))
                .setTakePhotoText(getString(R.string.base_total_take_photo))
                .setOnChangeHeadListener(this)
                .show(supportFragmentManager, "pick_picture")
    }

    /**
     * 使用z-xing自带活动来扫描
     */
    override fun onClickTakePhone(dialog: Dialog): Boolean {
        callWithPermissionWithPermissionCheck(BasePresenter.HttpCallback {
            startActivityForResult(Intent(this@BaseActivityQrScan, CaptureActivity::class.java), Const.REQUEST_CODE_THREE)
        })
        return false
    }

    /**
     * 获取到了图片
     */
    override fun takeSuccess(filePath: String, file: File) {
        CodeUtils.analyzeBitmap(filePath, object : CodeUtils.AnalyzeCallback {
            override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
                result?.let {
                    onQrGetSuccess?.invoke(it)
                } ?: toast(R.string.base_total_qr_analyze_not_require)
            }

            override fun onAnalyzeFailed() {
                toast(R.string.base_total_qr_analyze_error)
            }
        })
    }

    /**
     * 扫码的回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.REQUEST_CODE_THREE && resultCode == Activity.RESULT_OK) {
            data?.let {
                data.extras?.let {
                    (it.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS).kindAnyReturn({ null }) {
                        it.getString(CodeUtils.RESULT_STRING)?.let {
                            onQrGetSuccess?.invoke(it)
                        }
                    }
                }
            } ?: toast(R.string.base_total_qr_analyze_error)
        }
    }
}