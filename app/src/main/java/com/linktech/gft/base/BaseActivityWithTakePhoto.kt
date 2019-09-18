package com.linktech.gft.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.app.TakePhotoImpl
import com.jph.takephoto.compress.CompressConfig
import com.jph.takephoto.model.InvokeParam
import com.jph.takephoto.model.TContextWrap
import com.jph.takephoto.model.TResult
import com.jph.takephoto.model.TakePhotoOptions
import com.jph.takephoto.permission.InvokeListener
import com.jph.takephoto.permission.PermissionManager
import com.jph.takephoto.permission.TakePhotoInvocationHandler
import com.linktech.gft.R
import com.linktech.gft.plugins.toast
import com.linktech.gft.util.Const
import java.io.File

/**
 * Created on 2018/7/12  16:58
 * function : 携带了TakePhoto的activity
 *
 * @author mnlin
 */
abstract class BaseActivityWithTakePhoto<T : BasePresenter<*>> : BaseActivity<T>(), TakePhoto.TakeResultListener, InvokeListener {
    /**
     * takePhoto框架
     */
    private var invokeParam: InvokeParam? = null
    private var takePhoto: TakePhoto? = null

    /**
     * 初始化数据(可默认不实现)
     *
     * @param savedInstanceState 已存储对象
     */
    @CallSuper
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //takePhoto框架
        getTakePhoto()
        takePhoto?.onCreate(savedInstanceState)
    }

    final override fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.method)
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    protected fun getTakePhoto(): TakePhoto {
        if (takePhoto == null) {
            takePhoto = TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto
        }
        val config = CompressConfig.Builder()
                .setMaxSize(Const.MAX_SIZE_PICTURE_UPLOAD)
                .setMaxPixel(Const.MAX_PIXEL_PICTURE_UPLOAD)
                .create()
        // 启用图片压缩(显示压缩进度框)
        takePhoto?.onEnableCompress(config, true)

        val takePhotoOptions = TakePhotoOptions.Builder()
                .setWithOwnGallery(false)//使用takePhoto自带相册
                .setCorrectImage(true) //纠正图片旋转角度
                .create()
        takePhoto?.setTakePhotoOptions(takePhotoOptions)
        return takePhoto as TakePhoto
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhoto?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        takePhoto?.onSaveInstanceState(outState)
    }

    final override fun takeSuccess(result: TResult) {
        //获取uri
        val filePath = if (result.image.compressPath.isNullOrBlank()) result.image.originalPath else result.image.compressPath

        // 更换头像,获取到了操作后请求网络
        val file = File(filePath)
        if (!file.exists()) {
            showToast(R.string.base_total_picture_no_selected)
            return
        }

        takeSuccess(filePath, file)
    }

    /**
     * 取值成功,进行下一步操作
     */
    abstract fun takeSuccess(filePath: String, file: File)

    override fun takeFail(result: TResult, msg: String) {
        showToast("${getString(R.string.base_total_select_fail)}:$msg")
        takeFailOrCancel()
    }

    override fun takeCancel() {
        toast(R.string.common_operate_cancel)
        takeFailOrCancel()
    }

    open fun takeFailOrCancel() {}
}
