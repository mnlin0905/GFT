package com.linktech.gft.activity.person

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.ActivityUtil
import com.uuzuche.lib_zxing.activity.CodeUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_show_invite_code.*

/**
 * function : 邀请码界面 -> 邀请记录
 *
 * Created on 2018/8/21  20:13
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_ShowInviteCodeActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_VERIFY_HAS_SUCCESS)
@InjectLayoutRes(layoutResId = R.layout.activity_show_invite_code)
@InjectActivityTitle(titleRes = R.string.label_none)
@InjectMenuRes(menuResId = R.menu.menu_text_recommend_record)
@DisableAPTProcess(disables = [APTPlugins.BUTTERKNIF, APTPlugins.AROUTER])
class ShowInviteCodeActivity : BaseActivity<BasePresenter<ShowInviteCodeActivity>>() {
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //透明背景
        setToolbarColor(dispatchGetColor(R.color.transparent))

//        //背景margin
//        ll_content.post {
//            val margin = 210.0 / 375 * ScreenUtils.getScreenWidth()
//            (ll_content.layoutParams as ViewGroup.MarginLayoutParams).topMargin = margin.toInt()
//            ll_content.requestLayout()
//        }

        /**
         * 网络请求
         */
        mPresenter.findInvitedCode(_signature, _username) { nullAble ->
            nullAble?.let { bean ->
                //邀请码
                tv_invite_code.text = bean.invite_code

                //二维码
                iv_qr.post {
                    Observable.just(bean.qrcode_url)
                            .map {
                                return@map CodeUtils.createImage(it, iv_qr.measuredWidth, iv_qr.height, null)
                            }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                iv_qr.setImageBitmap(it)
                            }) {
                                toast("${getString(R.string.common_cannot_create_qr)}:${it.message}")
                            }
                }

                //复制链接
                tv_copy.dOnClick {
                    ActivityUtil.saveMsgToClipboard(this@ShowInviteCodeActivity, bean.qrcode_url).onTrue {
                        toast(R.string.activity_show_invite_code_has_copy)
                    }.onFalse {
                        toast(R.string.activity_show_invite_code_cannot_copy)
                    }
                }

                //上限
                tv_upper_amount.text = getString(R.string.activity_show_invite_code_upper_limit,bean.upperLimit)

                //规则
                tv_introduce.text =getString(R.string.activity_show_invite_code_info,bean.upperLimit)
            }
        }
    }

    /**
     * 推荐记录
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        ARouter.getInstance()
                .build(ARouterConst.Activity_RecommendRecordActivity)
                .navigation()
                .empty(comment = "推荐记录")

        return true
    }
}