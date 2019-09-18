package com.linktech.gft.base

import android.Manifest
import android.app.Dialog
import android.net.Uri
import com.blankj.utilcode.util.PermissionUtils
import com.linktech.gft.R
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.window.ChangePictureDialogFragment
import org.jetbrains.anko.toast
import permissions.dispatcher.*

/**
 * function : 请求图片选取,同时请求权限
 *
 * Created on 2018/7/13  9:49
 * @author mnlin
 */
@RuntimePermissions
abstract class BaseActivityTPhotoWPer<T : BasePresenter<*>> : BaseActivityWithTakePhoto<T>(), ChangePictureDialogFragment.OnChangeHeadListener {
    /**
     * 显示图片选择窗口
     */
    protected open fun showPicturePickDialog() {
        ChangePictureDialogFragment()
                .setShowFunction(true, true, false, true)
                .setOnChangeHeadListener(this)
                .show(supportFragmentManager, "pick_picture")
    }

    /**
     * 显示图片选择窗口(带"还原默认"按钮)
     */
    protected open fun showPicturePickDialogWithDefault() {
        ChangePictureDialogFragment()
                .setShowFunction(true, true, true, true)
                .setOnChangeHeadListener(this)
                .show(supportFragmentManager, "pick_picture")
    }

    /**
     * 直接拍照
     */
    override fun onClickTakePhone(dialog: Dialog): Boolean {
        onClickTakePhone()
        return false
    }

    /**
     * 直接拍照
     */
    fun onClickTakePhone(){
        getPictureWithPermissionCheck(0)
    }

    /**
     * 从相册获取
     */
    override fun onClickFromAlbum(dialog: Dialog): Boolean {
        onClickFromAlbum()
        return false
    }

    /**
     * 从相册获取
     */
    fun onClickFromAlbum() {
        getPictureWithPermissionCheck(1)
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun getPicture(type: Int) {
        when (type) {
            0 -> //拍照
                DefaultPreferenceUtil.getRandomPictureFile(this, getString(R.string.common_picture_directory), true).let {
                    getTakePhoto().onPickFromCapture(Uri.fromFile(it))
                }
            1 -> //相册
                getTakePhoto().onPickFromGallery()
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun callWithPermission(callback: BasePresenter.HttpCallback<Unit>) {
        callback.run(Unit)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onShowRationals(request: PermissionRequest) {
        toast(R.string.base_total_must_permission)
        request.proceed()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onPermissionDenied() {
        toast(R.string.base_total_no_pmn_to_picture)
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onNeverAskAgain() {
        showSnackbar(getString(R.string.base_total_has_forbid_pmn_camera_storage), getString(R.string.function_to_set)) { PermissionUtils.openAppSettings() }
    }
}