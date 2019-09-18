package com.linktech.gft.fragment.financing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.tifezh.kchartlib.chart.beans.MinuteLineEntity
import com.github.tifezh.kchartlib.chart.entity.IMinuteLine
import com.knowledge.mnlin.linemenuview.setClickRightListener
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.bean.WSControlPush
import com.linktech.gft.bean.WSExponentChangeBean
import com.linktech.gft.bean.WSStockMinHourBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.view.TabPagerCombineLayout
import com.linktech.gft.ws.BaseWSListener
import com.linktech.gft.ws.WSManager
import kotlinx.android.synthetic.main.dialog_market_item_minute.view.*
import kotlinx.android.synthetic.main.fragment_market.*
import java.util.*

/**
 * 行情碎片
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_MarketFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_market)
open class MarketFragment : BaseFragment<BasePresenter<MarketFragment>>(), TabPagerCombineLayout.onTabPagerListener {
    /**
     * 設置默認的選中位置
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var defaultPosition: Int = 0

    /**
     * 碎片列表
     */
    var fragments: MutableList<BaseFragment<*>> = mutableListOf()

    /**
     * 請求類型,自選,市場  [Const.KEY_MARKET_SCAN]
     */
    var switchType: Int = Const.MARKET_SCAN_CUSTOM

    /**
     * dialog:显示分时图
     */
    private lateinit var firstDialog: DefaultSimulateDialogImpl<CardView, FrameLayout.LayoutParams>

    /**
     * 分时图/数据源
     */
    private var minHourDatas: MutableList<IMinuteLine> = mutableListOf()

    /**
     * 那只股票被选中
     */
    var currentExponent: WSExponentChangeBean = WSExponentChangeBean()

    /**
     * 昨收价格
     */
    var yesClosePrice: Double? = .0

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        //自選/市場
        rg_custom_market.setOnCheckedChangeListener { group, checkedId ->
            switchType = when (checkedId) {
                R.id.rb_custom -> Const.MARKET_SCAN_CUSTOM
                R.id.rb_market -> Const.MARKET_SCAN_MARKET
                else -> TODO()
            }

            //控制指数数据是否显示
            ll_hs_index.visibility = if (switchType == Const.MARKET_SCAN_CUSTOM) View.VISIBLE else View.GONE

            //修改tab-layout文字 TODO
            //tpcl_record.tabLayout.getTabAt(0)?.text = arrayOf(getString(R.string.fragment_market_all), getString(R.string.fragment_market_all_global))[switchType]

            //刷新數據
            notifyFragment()
        }

        //展开dialog(HSI)
        firstDialog = generateDefaultDialog(includeDialog!!, R.layout.dialog_market_item_minute)
        mPresenter.indexDataList {
            it?.firstOrNull { it.getShareIdI() == 1 }?.let {
                currentExponent = it

                // 显示弹出框口
                iv_exponent_expand.dOnClick {
                    firstDialog.generateView().layoutParams = (firstDialog.generateView().layoutParams as ViewGroup.MarginLayoutParams).apply {
                        bottomMargin = includeDialog?.findViewById<View>(R.id.bnve_navigation)?.height
                                ?: 0
                    }
                    firstDialog.show()
                }

                //HSI
                ll_hs_index.dOnClick {
                    routeCustom(ARouterConst.Activity_KLineExponentActivity)
                            .firstParam(OptionalListBean(securityCode = "0000100", securityShortName = getString(R.string.fragment_market_item_first_title)))
                            .navigation()
                            .empty(comment = "指数的k-line数据")
                }

                // 初次刷新
                val refreshBottom: (WSExponentChangeBean) -> Unit = {
                    tv_hs_amount.text = it.indexValue.stockGetValue(isExponent = true)
                    tv_hs_change_amount.text = it.netChgValue.stockGetValue(isExponent = true)
                    tv_hs_rate.text = it.netChgPrevDayPct.stockGetPercent()
                }
                tv_hs_amount.dependent = tv_hs_change_amount
                refreshBottom(currentExponent)

                //首页底部数据刷新(ws监听动态修改)
                WSManager.addListener(BaseWSListener<WSExponentChangeBean>(WSExponentChangeBean::class.java, {
                    refreshBottom(it)
                }) {
                    it.getShareIdI() == currentExponent.getShareIdI()
                }.also {
                    WSManager.sendText(WSControlPush(subscribe = true, msgType = WSExponentChangeBean::class.java, code = currentExponent.getShareCodeI()))
                })

                // dialog中数据
                firstDialog.generateView().apply {
                    // 固定不变的部分
                    tv_dialog_name.text = currentExponent.securityCodeStr

                    // 刷新 dialog
                    val refreshDialog: (WSExponentChangeBean) -> Unit = {
                        tv_dialog_change_amount.text = it.netChgValue.stockGetValue(isExponent = true)
                        tv_dialog_change_percent.text = it.netChgPrevDayPct.stockGetPercent()
                        tv_dialog_turnover.text = it.indexTurnover.stockGetBigNumber(isExponent = true)
                        tv_dialog_amount.dependent = tv_dialog_change_amount
                        tv_dialog_amount.text = it.indexValue.stockGetValue(isExponent = true)
                        tv_dialog_up.text = it.indexAdv.toString()
                        tv_dialog_equal.text = it.indexFlat.toString()
                        tv_dialog_down.text = it.indexDec.toString()
                    }
                    refreshDialog(currentExponent)

                    //转换数据格式
                    val filterTranslate: (WSExponentChangeBean) -> IMinuteLine = {
                        MinuteLineEntity(it.indexTime?.stockToDate()
                                ?: Calendar.getInstance().time, it.indexValue / 10000.0, it.easValue / 100.0, it.indexTurnover / 10000.0)
                    }
                    // 分时 数据加载(分时图)http
                    mPresenter.exponentTimeShareList(currentExponent.getShareIdI()!!) {
                        if (it != null) {
                            //更新数据
                            val refreshMinHour = {
                                yesClosePrice?.let {
                                    mcv_minute_view.initData(minHourDatas, it, true)
                                }
                            }

                            // 历史数据
                            minHourDatas.addAll(it.history.list.map { filterTranslate(it) })

                            // 昨收价格
                            yesClosePrice = currentExponent.previousSesClose / 10000.0

                            refreshMinHour()

                            // 所需数据
                            WSManager.addListener(BaseWSListener<WSExponentChangeBean>(WSExponentChangeBean::class.java, {
                                // 更新视图
                                refreshDialog(it)

                                //处理分时图部分
                                if (it.indexTime?.stockToDate() equalsIgnoreSecond minHourDatas.lastOrNull()?.date) {
                                    minHourDatas.removeAt(minHourDatas.size - 1)
                                }
                                minHourDatas.add(filterTranslate(it))
                                refreshMinHour()
                            }) {
                                it.getShareIdI() == 1
                            }.also {
                                WSManager.sendText(WSControlPush(subscribe = true, msgType = WSExponentChangeBean::class.java, code = currentExponent.getShareCodeI()))
                            })
                        }
                    }

                    //跳转/关闭弹窗
                    cl_dialog_jump.dOnClick {
                        // 跳转 k-line
                        routeCustom(ARouterConst.Activity_KLineExponentActivity)
                                .firstParam(currentExponent)
                                .navigation()
                                .empty(comment = "指数的k-line数据")

                        // 关闭自身
                        firstDialog.close(true)
                    }

                    // 关闭
                    lmv_dialog_close.setClickRightListener {
                        // 关闭自身
                        firstDialog.close(true)
                        true
                    }
                }
            }
        }

        //填充碎片
        initFragment(listOf(/*getString(R.string.fragment_market_all),*/ getString(R.string.fragment_market_hk)/*, getString(R.string.fragment_market_us)*/))
    }

    /**
     * 先獲知有多少個 item - fragment
     *
     * 然後初始化碎片數據
     */
    protected open fun initFragment(titles: List<String>) {
        fragments.addAll(titles.mapIndexed { index, title ->
            routeCustom(ARouterConst.Fragment_MarketItemFragment)
                    .firstParam(index)
                    .secondParam(title)
                    .navigation()
                    .empty(comment = "添加碎片") as BaseFragment<*>
        })

        tpcl_record.provideFragmentManager(childFragmentManager)
                .provideFragments(fragments)
                .provideTitles(titles)
                .provideListener(this)
                .provideDefaultPosition(defaultPosition)
                .provideOffscreenPageLimit(fragments.size - 1)
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
                    fragments[tpcl_record.currentPosition].onPagerFragmentChange(switchType).empty(comment = "通知子 fragment 更新數據")
                } else {
                    notifyFragment()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //移除监听(子类无需重复处理)
        WSManager.removeListener(WSExponentChangeBean::class.java)
        WSManager.removeListener(WSStockMinHourBean::class.java)
    }
}
