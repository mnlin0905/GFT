package com.linktech.gft.activity.person

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.linktech.gft.R
import com.linktech.gft.adapter.SignRewardAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.SignInVo
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.dOnClick
import com.linktech.gft.util.Const
import com.linktech.gft.util.SpanStringUtil
import kotlinx.android.synthetic.main.activity_sign_reward.*

/**
 * function---- AboutActivity
 *
 * Created(Gradle default create) by LinkTech on 2018/01/22 05:45:55 (+0000).
 */
@Route(path = ARouterConst.Activity_SignRewardActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_sign_reward)
@InjectActivityTitle(titleRes = R.string.label_sign_reward)
class SignRewardActivity : BaseActivity<BasePresenter<SignRewardActivity>>(), XRecyclerView.LoadingListener {
    /**
     * 是否是从签到页面跳过来的
     */
    @Autowired(name = Const.KEY_IS_FROM_SIGN, required = false)
    @JvmField
    var isFromSign: Boolean = false

    private lateinit var signRewardAdapter: SignRewardAdapter
    private var datas: MutableList<SignInVo>? = null
    private var pageIndex: Int = 1

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        signRewardAdapter = SignRewardAdapter()
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.emptyView = empty_view
        recycler.setLoadingMoreEnabled(true)
        recycler.setPullRefreshEnabled(true)
        recycler.adapter = signRewardAdapter
        recycler.setLoadingListener(this)

        tv_to_sign.visibility = if (is_signIn) View.GONE else View.VISIBLE

        //去签到
        tv_to_sign.dOnClick {
            if (!isFromSign) {
                ARouter.getInstance().build(ARouterConst.Activity_SignActivity).navigation()
            }
            finish()
        }
        getData()
    }

    fun getData() {
        //签到信息，包括记录
        mPresenter.getUserSignInInfo(_username, _signature, pageIndex) {
            recycler.loadMoreComplete()
            recycler.refreshComplete()
            it?.run {
                tv_total_sign.text = getString(R.string.activity_sign_total_day, allSignInDay)
                tv_total_power.text = SpanStringUtil.formateDouble(allReward)
                tv_day_power.text = SpanStringUtil.formateDouble(todayReward)
                tv_mouth_power.text = SpanStringUtil.formateDouble(monthReward)
                tv_mouth_sign.text = getString(R.string.activity_sign_reward_total_day_month, monthSignInDay)
                datas?.apply {
                    if (pageIndex == 1) {
                        clear()
                    }
                    addAll(signInVos)
                    signRewardAdapter.setData(this)
                } ?: let { _ ->
                    datas = it.signInVos
                    signRewardAdapter.setData(signInVos)
                }
            }
        }
    }

    override fun onRefresh() {
        pageIndex = 1
        getData()
    }

    override fun onLoadMore() {
        pageIndex++
        getData()
    }

}
