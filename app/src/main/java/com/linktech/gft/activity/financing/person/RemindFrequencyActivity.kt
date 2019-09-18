package com.linktech.gft.activity.financing.person


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_remind_frequency.*

/**
 * function---- ForgetPasswordActivity
 *
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_RemindFrequencyActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_remind_frequency)
@InjectActivityTitle(titleRes = R.string.label_remind_frequency)
class RemindFrequencyActivity : BaseActivity<BasePresenter<RemindFrequencyActivity>>(), LineMenuListener {
    /**
     * 當前選擇的
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = true)
    @JvmField
    var type: Int = 0

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        selectType(type)
    }

    override fun performSelf(lmv: LineMenuView) {
        val position = lmv.getTag(LMVConfigs.TAG_POSITION) as Int
        if (position != type) {
            selectType(position)
        }
    }

    private fun selectType(type: Int) {
        this.type = type
        listOf(lmv_one, lmv_day, lmv_always).forEachIndexed { index, view ->
            view.rightSelect = index == type
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Const.KEY_COMMON_VALUE, type)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}
