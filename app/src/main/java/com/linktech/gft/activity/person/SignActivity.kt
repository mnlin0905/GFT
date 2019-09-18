package com.linktech.gft.activity.person

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.window.SignSuccessDialogFragment
import kotlinx.android.synthetic.main.activity_sign.*

/**
 * function---- AboutActivity
 *
 * Created(Gradle default create) by LinkTech on 2018/01/22 05:45:55 (+0000).
 */
@Route(path = ARouterConst.Activity_SignActivity,extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_sign)
@InjectMenuRes(menuResId = R.menu.menu_sign)
@InjectActivityTitle(titleRes = R.string.label_sign)
class SignActivity : BaseActivity<BasePresenter<SignActivity>>(), LineMenuListener {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        changeDayText(tv_one_day, 3)
        changeDayText(tv_two_day, 7)
        changeDayText(tv_three_day, 15)
        changeDayText(tv_four_day, 28)
        tv_sign.setText(if (is_signIn) R.string.activity_sign_has_sign else R.string.activity_sign_to_sign)

        //奖励规则
        mPresenter.getSignInRewardInfo(_username, _signature) {
            it?.run {
                changePowerText(tv_one_power, level3)
                changePowerText(tv_two_power, level7)
                changePowerText(tv_three_power, level15)
                changePowerText(tv_four_power, level28)
                tv_one_image.text = level3.toString()
                tv_two_image.text = level7.toString()
                tv_three_image.text = level15.toString()
                tv_four_image.text = level28.toString()
                tv_rule.text = resources.getString(R.string.activity_sign_info, level0.toString(), level3.toString(), level7.toString(), level15.toString(), level28.toString())
            }
        }

        //签到信息，包括记录
        getSignInfo()

        //签到
        tv_sign.dOnClick {
            if (is_signIn) {
                return@dOnClick
            }
            mPresenter.doSignIn(_username, _signature) { power ->
                mPresenter.getMemberInfo(_signature, _username) {
                    it?.run {
                        tv_sign.setText(if (is_signIn) R.string.activity_sign_has_sign else R.string.activity_sign_to_sign)
                        getSignInfo()
                        SignSuccessDialogFragment().setPower(power).show(supportFragmentManager, "tag")
                    }
                }
            }
        }
    }

    private fun getSignInfo() {
        //签到信息，包括记录
        mPresenter.getUserSignInInfo(_username, _signature) {
            it?.run {
                SpannableStringBuilder(getString(R.string.activity_sign_has_sign_month, monthSignInDay)).also {
                    it.setSpan(ForegroundColorSpan(Color.parseColor("#82e9ff")), it.indexOf(monthSignInDay.toString()), it.indexOf(monthSignInDay.toString()) + monthSignInDay.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }.let {
                    tv_day.text = it
                }
            }
        }
    }


    private fun changeDayText(tv: TextView, day: Int) {
        SpannableStringBuilder(getString(R.string.activity_sign_signs_in_month, day)).also {
            it.setSpan(ForegroundColorSpan(Color.parseColor("#1b8cfe")), it.indexOf(day.toString()), it.indexOf(day.toString())+day.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }.let {
            tv.text = it
        }
    }

    fun changePowerText(tv: TextView, power: Double) {
        SpannableStringBuilder(getString(R.string.activity_sign_reward_zl, power)).also {
            it.setSpan(ForegroundColorSpan(Color.parseColor("#1b8cfe")), it.indexOf(power.toString()), it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }.let {
            tv.text = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        SpannableString(getString(R.string.activity_sign_record)).let {
            it.setSpan(ForegroundColorSpan(dispatchGetColor(R.color.white)), 0, it.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            menu.getItem(0).setTitle(it)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        ARouter.getInstance().build(ARouterConst.Activity_SignRewardActivity)
                .withBoolean(Const.KEY_IS_FROM_SIGN, true)
                .navigation()
        return true
    }
}