package com.linktech.gft.fragment.financing

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseEvent
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.fragment_exchange.*

/**
 * 交易模組
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_ExchangeFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_exchange)
open class ExchangeFragment : BaseFragment<BasePresenter<ExchangeFragment>>() {
    /**
     * 保存开户状态
     */
    var establishStatusBean: EstablishStatusBean? = null

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        toolbar.title = getString(R.string.fragment_exchange_title)

        //登錄/註冊
        tv_login.dOnClick {
            route(ARouterConst.Activity_TradeActivity)
        }
        tv_register.dOnClick {
            // 必须登录才可以进行跳转操作
            if (!_status_login) {
                RxBus.instance?.post(BaseEvent(Const.SHOW_LOGIN_DIALOG, null))
                return@dOnClick
            }

            establishStatusBean?.let {
                when (it.status) {
                    0 -> ARouterConst.Activity_OpenPreparationActivity
                    1 means "信息填写中,跳转到不同界面" -> when (it.stepFillOut) {
                        1 means "已完成:身份证明--上传身份证照片" -> ARouterConst.Activity_CommitInfoBasicActivity
                        2 means "已完成:确认身份信息" -> ARouterConst.Activity_CommitInfoMoreActivity
                        3 means "已完成:填写个人信息" -> ARouterConst.Activity_VerifyBankCardActivity
                        4 means "已完成:银行卡验证" -> ARouterConst.Activity_AgreementDeclareActivity
                        5 means "已完成:个人声明" -> ARouterConst.Activity_InvestmentExperienceActivity
                        6 means "已完成:投资经验" -> ARouterConst.Activity_FinancialPositionActivity
                        7 means "已完成:财务状况" -> ARouterConst.Activity_SelectAccountActivity
                        8 means "已完成:选择账户" -> ARouterConst.Activity_VerifyPasswordActivity
                        9 means "已完成:密码设置" -> ARouterConst.Activity_SignatureAgreementActivity
                        10 means "已完成:上传手持证件照" -> ARouterConst.Activity_SignatureNameActivity
                        11 means "已完成,保存人脸照片,暂不支持" -> toast("暂不支持活体检测流程").let { null }
                        12 means "已完成:确认签名照,则establishStatusBean.status应当已经改变,所以此流程也不会执行" -> toast("异常的状态:\"已完成确认签名照,可申请状态仍未改变\"").let { null }
                        else -> toast("错误的申请步骤").let { null }
                    }
                    2, 3, 4, 5, 6 means "已经提交成功" -> ARouterConst.Activity_EstablishFinishActivity
                    else -> toast("错误的状态类型").let { null }
                }
            }?.let {
                routeCustom(it)
                        .param(establishStatusBean)
                        .navigation()
                        .empty(comment = "跳转界面")
            }
        }
    }

    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)

        // 立即开户(只有登录情况才会刷新数据)
        if (_status_login) {
            mPresenter.getByUserIdAccount(_username, _signature) {
                establishStatusBean = it ?: EstablishStatusBean()
            }
        }
    }
}
