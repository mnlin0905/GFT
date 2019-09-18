package com.linktech.gft.fragment.financing

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BaseFragmentWebView
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.view.TabPagerCombineLayout
import kotlinx.android.synthetic.main.fragment_information.*

/**
 * 資訊列表
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_InformationFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_information)
open class InformationFragment : BaseFragmentWebView<BasePresenter<InformationFragment>>(), TabPagerCombineLayout.onTabPagerListener {
    /**
     * 設置默認的選中位置
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var defaultPosition: Int = 0

    /**
     * 碎片列表
     */
    private var fragments: MutableList<BaseFragment<*>> = mutableListOf()

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //资讯
        toolbar.title = getString(R.string.action_financing_information)

        //填充碎片
        initFragment(getStringArray(R.array.information_type).toList())
    }

    /**
     * 先獲知有多少個 item - fragment
     *
     * 然後初始化碎片數據
     */
    protected open fun initFragment(titles: List<String>) {
        fragments.addAll(listOf(
                routeCustom(ARouterConst.Fragment_InformationItemFragment)
                        .withInt(Const.KEY_TYPE_FINANCING_INFORMATION,Const.TYPE_FINANCING_INFORMATION_HOT)
                        .navigation()
                        .empty(comment = "添加碎片") as BaseFragment<*>,
                routeCustom(ARouterConst.Fragment_InformationItemFragment)
                        .withInt(Const.KEY_TYPE_FINANCING_INFORMATION,Const.TYPE_FINANCING_INFORMATION_HK)
                        .navigation()
                        .empty(comment = "添加碎片") as BaseFragment<*>,
                routeCustom(ARouterConst.Fragment_InformationItemFragment)
                        .withInt(Const.KEY_TYPE_FINANCING_INFORMATION,Const.TYPE_FINANCING_INFORMATION_US)
                        .navigation()
                        .empty(comment = "添加碎片") as BaseFragment<*>,
                routeCustom(ARouterConst.Fragment_InformationItemFragment)
                        .withInt(Const.KEY_TYPE_FINANCING_INFORMATION,Const.TYPE_FINANCING_INFORMATION_7_24)
                        .navigation()
                        .empty(comment = "添加碎片") as BaseFragment<*>
        ))

        tpcl_record.provideFragmentManager(childFragmentManager)
                .provideFragments(fragments)
                .provideTitles(titles)
                .provideListener(this)
                .provideDefaultPosition(defaultPosition)
                .provideOffscreenPageLimit(fragments.size -1)
                .combine()
    }

    /**
     * 網路數據提取
     */
    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)
        notifyFragment()
    }

    /**
     * @param position 第position位置的頁面顯示
     */
    override fun onPagerAppear(position: Int) {
        notifyFragment()
    }

    /**
     * 通知fragment 刷新
     */
    private fun notifyFragment() {
        //調用當前fragment的方法
        if (fragments.size > tpcl_record.currentPosition) {

            tpcl_record.post {
                if (fragments[tpcl_record.currentPosition].rootView != null) {
                    fragments[tpcl_record.currentPosition].onPagerFragmentChange(null).empty(comment = "通知子 fragment 更新數據")
                } else {
                    notifyFragment()
                }
            }
        }
    }
}
