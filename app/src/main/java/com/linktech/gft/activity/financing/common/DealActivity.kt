package com.linktech.gft.activity.financing.common

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.linktech.gft.R
import com.linktech.gft.adapter.ActivityViewPagerAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.param
import com.linktech.gft.util.Const
import com.linktech.gft.view.DisableScrollViewPager
import kotlinx.android.synthetic.main.activity_deal.*

/**
 * function : 理財中心首頁
 *
 *
 *
 *
 * Created on 2019/3/20  16:33
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_DealActivity,extras = ARouterConst.FLAG_VERIFY_HAS_SUCCESS or ARouterConst.FLAG_TRADE_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_deal)
@InjectActivityTitle(title = "")
class DealActivity : BaseActivity<BasePresenter<DealActivity>>(), DisableScrollViewPager.ManageScrollInterface {
    /**
     * 初始位置
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var firstCurrent = 0

    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var currentStock: OptionalListBean? = null

    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_TWO, required = false)
    @JvmField
    var stockCode: String? = null

    /**
     * 當前第n個fragment處於最前
     */
    private var currentPosition: Int = 0

    /**
     * 菜單項
     */
    private val MENUS_IDS = arrayOf(
            R.id.deal_navigation_buy,
            R.id.deal_navigation_sale,
            R.id.deal_navigation_revoke,
            R.id.deal_navigation_query
    )

    /**
     * 碎片佈局:
     * 行情,資訊 ,交易,我的
     */
    private var fragments = listOf(
            ARouter.getInstance().build(ARouterConst.Fragment_DealFragment).param(0).navigation() as BaseFragment<*>,
            ARouter.getInstance().build(ARouterConst.Fragment_KillOrderFragment).navigation() as BaseFragment<*>,
            ARouter.getInstance().build(ARouterConst.Fragment_EntrustSearchFragment).navigation() as BaseFragment<*>
    )

    /**
     * 適配器
     */
    private var pagerAdapter = ActivityViewPagerAdapter(supportFragmentManager, fragments)

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //關閉所有的動畫效果,讓一次顯示完全
        bnve_navigation.enableAnimation(true)
        bnve_navigation.enableShiftingMode(false)
        bnve_navigation.enableItemShiftingMode(false)

        //設置圖片顏色不默認 tint
        fragments.mapIndexed { index, _ -> bnve_navigation.setIconTintList(index, null) }

        //默認預加載數量
        vp_fragments.offscreenPageLimit = fragments.size - 1

        //設置適配器
        vp_fragments.adapter = pagerAdapter

        //綁定ViewPager和Navigation
        bindNavigationAndViewPager()

        if (firstCurrent != 0) {
            vp_fragments.setCurrentItem(firstCurrent, false)
            currentPosition = firstCurrent
            bnve_navigation.currentItem = firstCurrent
        }
    }

    /**
     * 綁定ViewPager和Navigation
     *
     * 當viewpager滑動時改變navigation
     * 當navigation點擊時改變viewpager
     */
    private fun bindNavigationAndViewPager() {
        //底部控件點擊回調
        bnve_navigation.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            //点击的位置
            var position = 0
            for (i in MENUS_IDS.indices) {
                if (item.itemId == MENUS_IDS[i]) {
                    position = i
                    break
                }
            }
            if (position != currentPosition) {
                when {
                    position in 0..1 && currentPosition in 2..3 -> {
                        vp_fragments.setCurrentItem(0, false)
                        fragments[0].onPagerFragmentChange(position)
                    }
                    position in 0..1 && currentPosition in 0..1 -> {
                        fragments[0].onPagerFragmentChange(position)
                    }
                    position in 2..3 -> {
                        vp_fragments.setCurrentItem(position - 1, false)
                        fragments[position - 1].onPagerFragmentChange(false)
                    }
                }
                currentPosition = position
            }
            true
        }
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
        return false
    }

    fun getCurrentStock(): OptionalListBean? {
        return currentStock
    }

    fun getStockCode():String?{
        return stockCode
    }
}
