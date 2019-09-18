package com.linktech.gft.fragment.financing

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.knowledge.mnlin.linemenuview.LMVConfigs
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import kotlinx.android.synthetic.main.fragment_person.*

@Route(path = ARouterConst.Fragment_PersonFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_person)
class PersonFragment : BaseFragment<BasePresenter<PersonFragment>>(), LineMenuListener {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        tv_login.dOnClick {
            routeCustom(ARouterConst.Activity_LoginActivity).navigation()
        }

        // 个人资料
        iv_image.dOnClick {
            route(ARouterConst.Activity_PersonInformationActivity)
        }
    }

    private fun changeLogin() {
        when (_status_login) {
            true means "登錄" -> {
                Glide.with(baseActivity)
                        .load(BasePresenter.singleUserInfo.userImg)
                        .apply(RequestOptions().circleCrop().placeholder(R.drawable.default_head_img))
                        .into(iv_image)
                iv_image.visibility = View.VISIBLE
                tv_name.visibility = View.VISIBLE
                tv_name.text = BasePresenter.singleUserInfo.nickname
                        ?: BasePresenter.singleUserInfo.mobile ?: BasePresenter.singleUserInfo.email
                iv_verify.visibility = View.VISIBLE
                iv_verify.isActivated = BasePresenter.singleUserInfo.certification_status == 1
                iv_mobile.visibility = View.VISIBLE
                iv_mobile.isActivated = !BasePresenter.singleUserInfo.mobile.isNullOrEmpty()
                iv_email.visibility = View.VISIBLE
                iv_email.isActivated = !BasePresenter.singleUserInfo.email.isNullOrEmpty()
                tv_welcome.visibility = View.GONE
                tv_login.visibility = View.GONE
            }
            false means "未登錄" -> {
                iv_image.visibility = View.GONE
                tv_name.visibility = View.GONE
                iv_verify.visibility = View.GONE
                iv_mobile.visibility = View.GONE
                iv_email.visibility = View.GONE
                tv_welcome.visibility = View.VISIBLE
                tv_login.visibility = View.VISIBLE
            }
        }
    }

    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)
        if (_status_login) {
            mPresenter.getMemberInfo(_signature, _username) {
                changeLogin()
            }
        } else {
            changeLogin()
        }
    }

    override fun performSelf(lmv: LineMenuView) {
        when (lmv.getTag(LMVConfigs.TAG_POSITION) as Int) {
            0 means "积分商城" ->
                routeCustom(ARouterConst.Activity_CommonAgreementActivity)
                        .param("积分商城")
                        .firstParam("http://mall.gangft.com/")
                        .navigation()
                        .empty(comment = "第三方商城")
            1 means "股價提醒" -> route(ARouterConst.Activity_StockWarnRecodeActivity)
            2 means "我的消息" -> route(ARouterConst.Activity_NewsActivity)
            3 means "我的收藏" ->
                routeCustom(ARouterConst.Activity_MyCollectionActivity)
                        .firstParam(getString(R.string.fragment_person_my_collection))
                        .navigation()
            4 means "意见反馈" -> route(ARouterConst.Activity_FeedbackActivity)
            5 means "設置" -> {
                //startActivity(FlutterContainerActivity.makeIntent(baseActivity, "/")).empty(comment = "设置功能:flutter界面")

                route(ARouterConst.Activity_SettingActivity).empty(comment = "原生界面")
            }
            6 means "安全防护" -> route(ARouterConst.Activity_SafetyProtectActivity)
        }
    }
}
