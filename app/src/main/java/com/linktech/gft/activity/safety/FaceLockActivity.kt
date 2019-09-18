package com.linktech.gft.activity.safety


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_face_lock.*

/**
 * function : 脸部锁屏
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_FaceLockActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_face_lock)
@InjectActivityTitle(titleRes = R.string.label_face_lock)
class FaceLockActivity : BaseActivity<BasePresenter<FaceLockActivity>>() ,LineMenuListener{

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //锁定
        lmv_lock.transition = DefaultPreferenceUtil.getInstance().isScreenFaceLocked(_user_id)
    }

    override fun performSelf(lmv: LineMenuView) {
        DefaultPreferenceUtil.getInstance().setScreenFaceLocked(_user_id, !lmv.transition)
        lmv.transition = !lmv.transition
    }
}
