package com.linktech.gft.activity.financing.common

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.linktech.gft.R
import com.linktech.gft.adapter.ActivityViewPagerAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.StockRulesBean
import com.linktech.gft.fragment.financing.ExchangeFragment
import com.linktech.gft.fragment.financing.TradeDetailFragment
import com.linktech.gft.interfaces.IIpChinaInterface
import com.linktech.gft.plugins.*
import com.linktech.gft.services.IpJudgeService
import com.linktech.gft.util.Const
import com.linktech.gft.view.DisableScrollViewPager
import com.linktech.gft.ws.WSManager
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_financing.*
import org.litepal.crud.DataSupport

/**
 * function : 理財中心首頁
 *
 *
 *
 *
 * Created on 2019/3/20  16:33
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_FinancingActivity, extras = ARouterConst.FLAG_ACTIVITY_CLEAR_TOP or ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_financing)
@InjectActivityTitle(title = "")
class FinancingActivity : BaseActivity<BasePresenter<FinancingActivity>>(), DisableScrollViewPager.ManageScrollInterface {
    /**
     * 服务连接
     */
    var connIpJudge: ServiceConnection? = null

    /**
     * 初始位置
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var firstCurrent = Const.VALUE_POSITION_NULL

    /**
     * 當前第n個fragment處於最前
     * 上個位置
     */
    private var currentPosition: Int = 0
    private var previousPosition = -1

    /**
     * 菜單項
     */
    private val MENUS_IDS = arrayOf(
            R.id.financing_navigation_market,
            R.id.financing_navigation_information,
            R.id.financing_navigation_exchange,
            R.id.financing_navigation_assets,
            R.id.financing_navigation_person
    )

    /**
     * 碎片佈局:
     * 行情,資訊 ,交易,我的
     */
    private var fragments: MutableList<BaseFragment<*>> = mutableListOf()

    /**
     * 適配器
     */
    private var pagerAdapter = ActivityViewPagerAdapter(sfManager, fragments)

    /**
     * 當返回該首頁時,可能需要切換tab位置
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (firstCurrent != Const.VALUE_POSITION_NULL) {
            bnve_navigation.currentItem = firstCurrent
            currentPosition = firstCurrent
        }
        //返回時判斷當前碎片是否佈局正確
        when {
            is_trade_login && fragments[2] !is TradeDetailFragment -> //如果地址為空,並且第一個碎片不是初始化碎片
                fragments.run {
                    removeAt(2)
                    fragments.add(2, ARouter.getInstance().build(ARouterConst.Fragment_TradeDetailFragment).navigation() as BaseFragment<*>)
                    pagerAdapter.notifyDataSetChanged()
                }
            !is_trade_login && (fragments[2] !is ExchangeFragment) -> //如果地址不為空,並且第一個碎片不是錢包碎片
                fragments.run {
                    removeAt(2)
                    fragments.add(2, ARouter.getInstance().build(ARouterConst.Fragment_ExchangeFragment).navigation() as BaseFragment<*>)
                    pagerAdapter.notifyDataSetChanged()
                }
        }
    }

    @SuppressLint("CheckResult")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 绑定服务
        connIpJudge = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Logger.d("远程服务:异常断开")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                BaseApplication.app.ipAddressI = IIpChinaInterface.Stub.asInterface(service)
                Logger.d("远程服务:已连接")
            }
        }
        connIpJudge?.also {
            bindService(Intent(this, IpJudgeService::class.java), it, Context.BIND_AUTO_CREATE)
        }

        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //去除全屏效果
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = Color.BLACK

        //關閉所有的動畫效果,讓一次顯示完全
        bnve_navigation.enableAnimation(true)
        bnve_navigation.enableShiftingMode(false)
        bnve_navigation.enableItemShiftingMode(false)

        //創建碎片實例
        fragments.add(ARouter.getInstance().build(ARouterConst.Fragment_MarketFragment).navigation() as BaseFragment<*>)
        fragments.add(ARouter.getInstance().build(ARouterConst.Fragment_InformationFragment).navigation() as BaseFragment<*>)
        fragments.add(ARouter.getInstance().build(if (is_trade_login) ARouterConst.Fragment_TradeDetailFragment else ARouterConst.Fragment_ExchangeFragment).navigation() as BaseFragment<*>)
        fragments.add(ARouter.getInstance().build(ARouterConst.Fragment_AssetsFragment).navigation() as BaseFragment<*>)
        fragments.add(ARouter.getInstance().build(ARouterConst.Fragment_PersonFragment).navigation() as BaseFragment<*>)
        //設置圖片顏色不默認 tint
        fragments.mapIndexed { index, _ -> bnve_navigation.setIconTintList(index, null) }

        //默認預加載數量
        vp_fragments.offscreenPageLimit = fragments.size - 1

        //設置適配器
        vp_fragments.adapter = pagerAdapter

        //綁定ViewPager和Navigation
        bindNavigationAndViewPager()

        ll_test_container.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)

        //添加规则
        empty {
            mPresenter.rulesList {
                it?.let {
                    DataSupport.deleteAllAsync(StockRulesBean::class.java, "").listen { _ ->
                        DataSupport.saveAllAsync(it).listen {
                            Logger.d("保存成功")
                        }
                    }
                }
            }
        }

        //第一次进入:检查更新
        mPresenter.findLatestVersion {
            if (it != null && AppUtils.getAppVersionCode() < it.version) {
                routeCustom(ARouterConst.Activity_AboutInnerActivity)
                        .withObject(Const.MODEL_VERSION_UPDATE, it)
                        .navigation()
            }
        }
    }

    /**
     * 綁定ViewPager和Navigation
     *
     *
     * 當viewpager滑動時改變navigation
     * 當navigation點擊時改變viewpager
     */
    private fun bindNavigationAndViewPager() {
        //底部控件點擊回調
        bnve_navigation.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var position = if (previousPosition == -1) 0 else previousPosition
            for (i in MENUS_IDS.indices) {
                if (item.itemId == MENUS_IDS[i]) {
                    position = i
                    break
                }
            }

            if (previousPosition != position) {
                previousPosition = position
                currentPosition = position
                vp_fragments.setCurrentItem(position, false)
                fragments[position].onPagerFragmentChange(false)
            }
            true
        }

        //滑動事件回調
        vp_fragments.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (previousPosition != position) {
                    bnve_navigation.selectedItemId = MENUS_IDS[position]
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * @return true表示當前狀態可以觸發滑動
     */
    override fun currentCanScroll(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()

        //判断当前活动的语言是否有切换,如果有,则重启该activity
        if (currentLocale != getApplicationLocale()) {
            ARouter.getInstance()
                    .build(ARouterConst.Activity_FinancingActivity)
                    .greenChannel()
                    .navigationWithArrivalRun { finish() }
                    .empty(comment = "重新启动")
            return
        }

        // 恢复刷新数据
        vp_fragments.post { fragments[currentPosition].onPagerFragmentChange(true) }
    }

    override fun onDestroy() {
        super.onDestroy()

        WSManager.disconnect()
        connIpJudge?.also {
            unbindService(it)
        }
    }
}
