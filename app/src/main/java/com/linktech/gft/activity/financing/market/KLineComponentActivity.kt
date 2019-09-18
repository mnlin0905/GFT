package com.linktech.gft.activity.financing.market


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.View
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.tifezh.kchartlib.chart.formatter.DateFormatter
import com.github.tifezh.kchartlib.utils.DateUtil
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.interfaces.SimulateDialogInterface
import com.linktech.gft.R
import com.linktech.gft.adapter.KChartAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.KLineEntity
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.interfaces.MyTabSelectedListener
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.DataRequest
import com.linktech.gft.util.ScreenUtils
import com.linktech.gft.view.TabPagerCombineLayout
import com.linktech.gft.view.tablayout.CustomTabLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_k_line_component.*
import kotlinx.android.synthetic.main.dialog_k_line_detail.view.*
import java.text.ParseException
import java.util.*

/**
 * function---- 成分股
 *
 * Created(Gradle default create) by LinkTech on 2018/01/19 01:55:53 (+0000).
 */
@Route(path = ARouterConst.Activity_KLineComponentActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_k_line_component)
@InjectActivityTitle(title = "")
class KLineComponentActivity : BaseActivity<BasePresenter<KLineComponentActivity>>(), TabPagerCombineLayout.onTabPagerListener {
    /**
     * 那只股票被选中
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var currentStock: OptionalListBean = OptionalListBean()

    /**
     * dialog
     */
    private lateinit var firstDialog: SimulateDialogInterface<CardView, FrameLayout.LayoutParams>

    /**
     * 新聞碎片
     */
    private var fragments: MutableList<BaseFragment<*>> = ArrayList()

    /**
     * k線庫適配器
     */
    private var singleChartAdapter: KChartAdapter = KChartAdapter()

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //設置一些關鍵佈局的寬高 / 介面變化時,調整顯示效果
        kcv_chart_view.setGridRows(3)
        kcv_chart_view.setGridColumns(8)
        fl_chart_multiply.layoutParams.height = ScreenUtils.screenWidth

        //初始化
        currentStock.also {
            //股票名字
            tv_shares_name.text = it.getShareNameI()
            //股票交易时间
            tv_share_time.text = "HK 交易中 ${it.createTime.longToTimeString("MM-dd HH-mm")}"
            //当前价格
            tv_current_price.text = it.realTimePrice.stockGetValue()
            //涨跌价格
            tv_change_price.text = it.priceLimitAmount.stockGetValue()
            //涨跌幅度
            tv_change_rate.text = it.priceLimit.stockGetPercent()
            //最高
            tv_max_price.text = "--"
            //今开
            tv_open_price.text = "--"
            //最低
            tv_min_price.text = "--"
            //收盘
            tv_close_price.text = "--"
            //成交额
            tv_success_amount.text = "--"

            // 颜色依赖项
            listOf(tv_current_price,tv_max_price,tv_open_price,tv_close_price).map {
                it.dependent = tv_change_price
            }
        }

        //創建dialog
        firstDialog = generateDefaultDialog(includeDialog!!, R.layout.dialog_k_line_detail)

        //股票詳細資訊
        iv_more_information.dOnClick {
            //顯示dialog,提前賦值
            firstDialog.generateView().run {
                empty(TODO = "自動填充數據")
                iv_close.dOnClick {
                    includeDialog!!.closeDialogsOpened(instance = firstDialog)
                }
            }
            includeDialog!!.showDialogs(instance = firstDialog, animator = AlphaIDVGAnimatorImpl()).empty(comment = "顯示dialog:股票詳細數據")
        }

        //分時間隔,新聞資訊
        getStringArray(R.array.array_k_line_tab).map {
            tl_tab.addTab(tl_tab.newTab().setText(it))
        }
        tl_tab.addOnTabSelectedListener(object : MyTabSelectedListener() {
            override fun onTabSelected(tab: CustomTabLayout.Tab?) {
                when (tab?.position) {
                    0 means "分時" -> {
                        kcv_chart_view.visibility = View.GONE
                        ll_minute_view.visibility = View.VISIBLE

                        //刷新分時數據
                        empty(TODO = "")
                    }
                    4 means "週期" -> {
                        //重新選中/選中 最後週期時,提示暫不支持
                        toast(R.string.common_not_develop)
                    }
                    else -> {
                        kcv_chart_view.visibility = View.VISIBLE
                        ll_minute_view.visibility = View.GONE

                        //刷新K線
                        empty(TODO = "")
                    }
                }
                empty(TODO = "點擊時候切換數據:${tab?.position}")
            }

            override fun onTabReselected(tab: CustomTabLayout.Tab?) {
                super.onTabReselected(tab)

                //重新選中/選中 最後週期時,提示暫不支持
                toast(R.string.common_not_develop)
            }
        })

        //新聞
        val stringArray = getStringArray(R.array.array_k_line_component_news).toList()
        fragments.add(route(ARouterConst.Fragment_KLineComponentFragment) as BaseFragment<*>)
        fragments.add(route(ARouterConst.Fragment_KNewsItemFragment) as BaseFragment<*>)
        tpcl_news.provideFragmentManager(sfManager)
                .provideFragments(fragments)
                .provideTitles(stringArray)
                .provideListener(this)
                .provideOffscreenPageLimit(fragments.size - 1)
                .combine()

        //加載K線數據
        kcv_chart_view.also {
            it.adapter = singleChartAdapter
            it.dateTimeFormatter = DateFormatter()
            it.setOnSelectedChangedListener { view, point, index ->
                val data = point as KLineEntity
                com.orhanobut.logger.Logger.d("onSelectedChanged:" + "index:" + index + " closePrice:" + data.closePrice)
            }

            //切屏,(跳转界面)
            it.mKChartTabView.mTvFullScreen.dOnClick {
                routeCustom(ARouterConst.Activity_KLineLandActivity)
                        .firstParam(currentStock)
                        .fourthParam(false)
                        .navigation(this@KLineComponentActivity)
                        .empty(comment = "横竖屏时,通过跳转界面实现")
            }
        }
        kcv_chart_view.postV {
            //加載數據
            showLoading()
            Observable.just(this@KLineComponentActivity)
                    .map { DataRequest.getALL(it) }
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        singleChartAdapter.addFooterData(it)
                        startAnimation()
                        refreshEnd()
                    }) {
                        toast(R.string.common_unknown_error)
                    }
        }

        //分時圖
        try {
            //整體開始時間
            val startTime = DateUtil.shortTimeFormat.parse("09:30")
            //整體的結束時間
            val endTime = DateUtil.shortTimeFormat.parse("15:00")
            //休息開始時間
            val firstEndTime = DateUtil.shortTimeFormat.parse("11:30")
            //休息結束時間
            val secondStartTime = DateUtil.shortTimeFormat.parse("13:00")
            //獲取隨機生成的數據
            val minuteData = DataRequest.getMinuteData(startTime,
                    endTime,
                    firstEndTime,
                    secondStartTime)

            /*mcv_minute_view.initData(minuteData,
                    startTime,
                    endTime,
                    firstEndTime,
                    secondStartTime,
                    (minuteData[0].price - 0.5 + Math.random()).toFloat())*/
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    /**
     * @param position 第position位置的頁面顯示
     */
    override fun onPagerAppear(position: Int) {
        //調用當前fragment的方法
        tpcl_news.post { (fragments[position]).onPagerFragmentChange(true) }
    }
}
