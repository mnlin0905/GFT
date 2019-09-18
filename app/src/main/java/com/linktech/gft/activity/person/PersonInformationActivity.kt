package com.linktech.gft.activity.person

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityTPhotoWPer
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BasePresenter.singleUserInfo
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.toast
import com.linktech.gft.util.Const
import com.linktech.gft.window.ChangeSexDialogFragment
import kotlinx.android.synthetic.main.activity_person_information.*
import java.io.File

/**
 * function---- PersonInformationActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/15 07:20:03 (+0000).
 */
@Route(path = ARouterConst.Activity_PersonInformationActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_person_information)
@InjectActivityTitle(titleRes = R.string.label_person_information)
class PersonInformationActivity : BaseActivityTPhotoWPer<BasePresenter<PersonInformationActivity>>(), LineMenuListener, ChangeSexDialogFragment.OnChangeSexListener {
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        refreshData()
        refreshHeadImg()
    }

    override fun onRestart() {
        super.onRestart()
        refreshData()
    }

    /**
     * 头像刷新
     */
    private fun refreshHeadImg() {
        Glide.with(this)
                .asDrawable()
                .apply(RequestOptions().placeholder(R.drawable.default_head_img).circleCrop())
                .load(singleUserInfo.userImg)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resource?.let { lmv_head.setBadge(resource) }
                        return false
                    }
                })
                .submit()
    }

    /**
     * 刷新用户信息
     */
    private fun refreshData() {
        lmv_nick_name.briefText = singleUserInfo.nickname
        lmv_sex.briefText = singleUserInfo.sexStr
    }

    /**
     * @param v 被点击到的v;此时应该是该view自身:LineMenuView
     */
    override fun performSelf(v: LineMenuView) {
        val position = v.getTag(LMVConfigs.TAG_POSITION) as Int

        //position是指在布局中出现的顺序
        performDeal(position)
    }

    /**
     * 如果不需要跳转揭秘那,则由本类类处理数据
     */
    private fun performDeal(position: Int) {
        if (position < NAVIGATION_ACTIVITY.size && position > Const.VALUE_POSITION_NULL && NAVIGATION_ACTIVITY[position] != null) {
            ARouter.getInstance().build(NAVIGATION_ACTIVITY[position]).navigation()
        } else {
            //在当前界面处理的逻辑
            when (position) {
                0 -> //选择设置头像
                    showPicturePickDialogWithDefault()
                2 -> //性别
                    ChangeSexDialogFragment()
                            .setOnChangeSexListener(this)
                            .show(supportFragmentManager, "change_sex")
            }
        }
    }

    /**
     * 还原默认设置
     */
    override fun onClickRestoreDefault(dialog: Dialog): Boolean {
        ImageUtils.drawable2Bytes(dispatchGetDrawable(R.drawable.default_head_img), Bitmap.CompressFormat.PNG).let {
            mPresenter.uploadImg(_username, _signature, it) {
                mPresenter.updateUserLogo(_username, _signature, it) {
                    mPresenter.getMemberInfo(_signature, _username) {
                        //头像设置成功,需要设置到原位置上
                        showToast(getString(R.string.activity_person_information_restore))
                        refreshHeadImg()
                    }
                }
            }
        }
        return false
    }

    /**
     * 取值成功,进行下一步操作
     */
    override fun takeSuccess(filePath: String, file: File) {
        //上传图片
        mPresenter.uploadImg(_username, _signature, file) {
            mPresenter.updateUserLogo(_username, _signature, it) {
                mPresenter.getMemberInfo(_signature, _username) {
                    //头像设置成功,需要设置到原位置上
                    toast(R.string.activity_person_information_head_success)
                    refreshHeadImg()
                }
            }
        }
    }

    /**
     * 设置性别
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun setUserSex(sex: Int) {
        mPresenter.updateUserGender(_username, _signature, sex) {
            mPresenter.getMemberInfo(_signature, _username) {
                showToast(getString(R.string.activity_person_information_sex_success))
                refreshData()
            }
        }
    }

    /**
     * 返回true表示不需要默认操作
     */
    override fun onClickMan(dialog: Dialog?): Boolean {
        setUserSex(1)
        return false
    }

    override fun onClickWoman(dialog: Dialog?): Boolean {
        setUserSex(2)
        return false
    }

    override fun onClickSecret(dialog: Dialog?): Boolean {
        setUserSex(3)
        return false
    }

    override fun onSexCancel(dialog: Dialog?): Boolean {
        return false
    }
}

/**
 * 需要跳转到的活动或界面
 */
private val NAVIGATION_ACTIVITY = arrayOf(
        null, //头像
        ARouterConst.Activity_ChangeNickNameActivity, //昵称
        null //性别
)