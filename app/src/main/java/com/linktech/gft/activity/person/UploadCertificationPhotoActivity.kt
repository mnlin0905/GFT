package com.linktech.gft.activity.person

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import butterknife.OnClick
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityTPhotoWPer
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.toast
import com.linktech.gft.util.Const.KEY_PICTURE_MULTIPLE
import com.linktech.gft.util.Const.KEY_PICTURE_NEGATIVE
import com.linktech.gft.util.Const.KEY_PICTURE_POSITIVE
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*
import kotlinx.android.synthetic.main.layout_upload_picture.*
import java.io.File

/**
 * function---- UploadCertificationPhotoActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/17 06:43:02 (+0000).
 */
@Route(path = ARouterConst.Activity_UploadCertificationPhotoActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_upload_certification_photo)
@InjectActivityTitle(titleRes = R.string.label_upload_certification_photo)
class UploadCertificationPhotoActivity : BaseActivityTPhotoWPer<BasePresenter<UploadCertificationPhotoActivity>>() {
    /**
     * 存储的图片位置
     *
     * 默认为null
     *
     * 如果都有值时,表示上传图片成功
     */
    @Autowired(required = false, name = KEY_PICTURE_POSITIVE)
    @JvmField
    var picture_positive: String? = null
    @Autowired(required = false, name = KEY_PICTURE_NEGATIVE)
    @JvmField
    var picture_negative: String? = null
    @Autowired(required = false, name = KEY_PICTURE_MULTIPLE)
    @JvmField
    var picture_multiple: String? = null

    /**
     * 当前上传到文件的位置
     * 0:正
     * 1:反
     * 2:手持
     */
    private var currentFile: Int = 0

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //如果有图片传进来,则进行展示
        Glide.with(this).load(picture_positive).into(iv_positive)
        Glide.with(this).load(picture_negative).into(iv_negative)
        Glide.with(this).load(picture_multiple).into(iv_multi)

        bt_common_function.text = getString(R.string.function_submit)
    }

    @OnClick(R.id.fl_positive, R.id.fl_negative, R.id.fl_multi, R.id.bt_common_function)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.fl_positive -> currentFile = 0
            R.id.fl_negative -> currentFile = 1
            R.id.fl_multi -> currentFile = 2
            R.id.bt_common_function -> //选择图片成功
                if (TextUtils.isEmpty(picture_positive) ||
                        TextUtils.isEmpty(picture_negative) ||
                        TextUtils.isEmpty(picture_multiple)) {
                    toast(R.string.activity_upload_certification_photo_must_upload)
                } else {
                    Intent().run {
                        putExtra(KEY_PICTURE_POSITIVE, picture_positive)
                        putExtra(KEY_PICTURE_NEGATIVE, picture_negative)
                        putExtra(KEY_PICTURE_MULTIPLE, picture_multiple)
                        setResult(Activity.RESULT_OK, this)
                        finish()
                        return
                    }
                }
        }

        //上传图片
        showPicturePickDialog()
    }


    /**
     * 取值成功,进行下一步操作
     */
    override fun takeSuccess(filePath: String, file: File) {
        //文件上传
        var des = iv_positive
        when (currentFile) {
            0 -> picture_positive = filePath
            1 -> {
                des = iv_negative
                picture_negative = filePath
            }
            2 -> {
                des = iv_multi
                picture_multiple = filePath
            }
        }
        Glide.with(this)
                .load(filePath)
                .into(des)
    }
}