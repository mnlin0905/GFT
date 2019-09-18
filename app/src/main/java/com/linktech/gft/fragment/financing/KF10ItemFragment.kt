package com.linktech.gft.fragment.financing

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.View
import android.view.ViewStub
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.*
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.fragment_k_f10_item.*
import kotlinx.android.synthetic.main.item_f10_brief.view.*
import kotlinx.android.synthetic.main.layout_f10_brief_backup.*
import kotlinx.android.synthetic.main.layout_f10_finance.*
import kotlinx.android.synthetic.main.layout_f10_shareholder.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.textColorResource

/**
 * function : 新聞碎片
 *
 * Created on 2019/3/25  15:21
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_KF10ItemFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_k_f10_item)
class KF10ItemFragment : BaseFragment<BasePresenter<KF10ItemFragment>>() {
    @Autowired(name = Const.KEY_COMMON_VALUE, required = true)
    @JvmField
    var stockCode: String? = null

    //维持当前状态
    private var currentPosition = 0
    private var vsOne: View? = null
    private var vsTwo: View? = null
    private var vsThree: View? = null
    private lateinit var listVs: List<ViewStub?>
    private var isExtend: Boolean = false
    //图标类型  1--报告期   2--年度
    private var financePeType: Int = 1
    private var financeCrashType: Int = 1
    //分红列表
    private var profitDatas: MutableList<FHBean.BonusBean> = mutableListOf()
    private var profitAdapter: BaseRecyclerViewAdapter<FHBean.BonusBean>
    //回购列表
    private var backDatas: MutableList<SBBean.BuyBackBean> = mutableListOf()
    private var backAdapter: BaseRecyclerViewAdapter<SBBean.BuyBackBean>
    //股东列表
    private var holderDatas: MutableList<HoldBean> = mutableListOf()
    private var holderAdapter: BaseRecyclerViewAdapter<HoldBean>

    init {
        profitAdapter = BaseRecyclerViewAdapter(
                dataResources = profitDatas,
                layoutResId = R.layout.item_f10_brief
        ) {
            //方案
            tv_one.text = it.cnParticular ?: "--"
            //年度
            tv_two.text = it.yearend ?: "--"
            //除净日
            tv_three.text = it.exDate ?: "--"

        }
        backAdapter = BaseRecyclerViewAdapter(
                dataResources = backDatas,
                layoutResId = R.layout.item_f10_brief
        ) {
            //登记日
            tv_one.text = it.buybackDate ?: "--"
            //回购总额
            tv_two.text = it.quantity.toInt().toString()
            //占总股本比例
            tv_three.text = "${it.repurchaseRatio}%"

        }
        holderAdapter = BaseRecyclerViewAdapter(
                dataResources = holderDatas,
                layoutResId = R.layout.item_f10_brief
        ) {
            //股东
            tv_one.text = it.shareholder ?: "--"
            //持股（股）
            tv_two.text = it.shareHold ?: "--"
            //占比（%）
            tv_three.text = it.ratio.toScaleString(2)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        listVs = listOf(vs_brief, vs_shareholder, vs_finance)
        listOf(tv_brief, tv_shareholder, tv_finance).forEachIndexed { index, tv ->
            tv.dOnClick {
                changeBg(index)
            }
        }
    }

    private fun changeBg(index: Int) {
        if (index != currentPosition) {
            currentPosition = index
            //处理背景
            listOf(tv_brief, tv_shareholder, tv_finance).forEachIndexed { i, tv ->
                tv.backgroundColorResource = if (i == currentPosition) R.color.dark_main_text_8144e5 else R.color.transparent
                tv.textColorResource = if (i == currentPosition) R.color.white else R.color.dark_main_text_666986
            }
            //处理加载和隐藏
            listVs.forEachIndexed { i, vs ->
                when (i) {
                    0 means "概况" -> if (currentPosition == i) {
                        if (vsOne == null) {
                            vsOne = vs?.inflate()
                        }
                        vsOne?.visibility = View.VISIBLE
                    } else {
                        vs?.visibility = View.GONE
                    }
                    1 means "股东" -> if (currentPosition == i) {
                        if (vsTwo == null) {
                            vsTwo = vs?.inflate()
                            rv_hold_detail.apply {
                                layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
                                adapter = holderAdapter
                            }
                            getHolderData()
                        }
                        vsTwo?.visibility = View.VISIBLE
                    } else {
                        vs?.visibility = View.GONE
                    }
                    2 means "财务" -> if (currentPosition == i) {
                        if (vsThree == null) {
                            vsThree = vs?.inflate()
                            listOf(tv_finance_pe_type, tv_finance_crash_type).forEachIndexed { index, tv ->
                                tv.dOnClick {
                                    when (index) {
                                        0 means "切换pe类型" -> {
                                            financePeType = if (financePeType == 1) 2 else 1
                                            tv.text = if (financePeType == 1) "报告期" else "年度"
                                            getPeList()
                                        }
                                        1 means "切换现金流类型" -> {
                                            financeCrashType = if (financeCrashType == 1) 2 else 1
                                            tv.text = if (financeCrashType == 1) "报告期" else "年度"
                                            getCrashList()
                                        }
                                    }
                                }
                            }
                            getFinanceData()
                        }
                        vsThree?.visibility = View.VISIBLE
                    } else {
                        vs?.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onPagerFragmentChange(obj: Any?) {
        super.onPagerFragmentChange(obj)
        if (vs_brief != null) {
            vsOne = vs_brief.inflate()
            rv_brief_profit.apply {
                layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
                adapter = profitAdapter
            }
            rv_brief_back.apply {
                layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
                adapter = backAdapter
            }
            //点击详情
            tv_brief_describe.dOnClick {
                if (isExtend) {
                    isExtend = !isExtend
                    handDescribe()
                }
            }
            //点击展开
            tv_brief_extend.dOnClick {
                isExtend = true
                handDescribe()
            }
            getBriefData()
        }
    }

    /**
     * 处理业务描述
     */
    private fun handDescribe() {
        tv_brief_describe.maxLines = if (isExtend) Int.MAX_VALUE else 3
        tv_brief_extend.visibility = if (isExtend) View.GONE else View.VISIBLE
        tv_brief_describe.visibility = View.VISIBLE
    }

    private var describe: String? = null

    /**
     * 获取简要数据
     */
    private fun getBriefData() {
        //公司概况
        mPresenter.corpSumm(stockCode) {
            it?.apply {
                //公司主席
                tv_brief_holder.text = this.cnChairman
                //上市日期
                tv_brief_on_time.text = this.listingDate
                //发行价格
                tv_brief_issue_price.text = this.mPnav?.toScaleString(2) ?: "--"
                //发行数量
                tv_brief_issue_count.text = fomate(this.shareIssured)
                //主营业务
                cnProfile?.apply {
                    describe = this
                    tv_brief_describe.text = Html.fromHtml(describe)
                    isExtend = length < 60
                    handDescribe()
                }
            }
        }
        //股本信息,股本结构
        mPresenter.equityInfo(stockCode) {
            it?.apply {
                //总股本
                tv_brief_general_capital.text = "${fomate(issueCap)}股"
                //港股
                tv_brief_hk_stock.text = "${fomate(otherIssueCap)}股"
                //非港股
                tv_brief_hk_stock.text = "${fomate(issueCap - otherIssueCap)}股"
                //优先股
            }
        }
        //分红送转(派息记录)
        mPresenter.dh(stockCode) {
            it?.apply {
                profitDatas.addAll(this)
                profitAdapter.notifyDataSetChanged()
            }
        }
        //回购
        mPresenter.sb(stockCode, null, null) {
            it?.apply {
                backDatas.addAll(this)
                backAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 获取股东数据
     */
    private fun getHolderData() {
        //股本信息,股本结构
        mPresenter.equityInfo(stockCode) {
            it?.apply {
                //总股本
                tv_hold_general_capital.text = "${fomate(issueCap)}股"
                //港股
                tv_hold_hk_stock.text = "${fomate(otherIssueCap)}股"
                //优先股
            }
        }

        //股东明细
        mPresenter.shareholderList(stockCode) {
            it?.apply {
                holderDatas.addAll(this)
                holderAdapter.notifyDataSetChanged()
            }
        }
    }


    /**
     * 获取财务数据
     */
    private fun getFinanceData() {
        //市盈率列表数据,既是财务概要
        mPresenter.financeSumm(stockCode) {
            it?.apply {
                //市盈率
                tv_finance_pe.text = "$pe"
                //每股收益
                tv_finance_per.text = "$eps${getCurrency(currency)}"
                //每股净资产
                tv_finance_per_asset.text = "$nav${getCurrency(currency)}"
                //净资产收益率
                tv_finance_roe.text = "$roEquity%"
                //资产负债率
                tv_finance_lev.text = "$tdEquity%"
            }
        }

        //单独处理市盈率列表
        getPeList()

        //利润表
        mPresenter.plInfo(stockCode) {
            it?.apply {
                //营业收入
                tv_finance_revenue.text = "${fomate(turnover)}${getUnit(unit)}${getCurrency(currency)}"
                //营业支出
                tv_finance_opex.text = "${fomate(operExpense)}${getUnit(unit)}${getCurrency(currency)}"
                //营运利润
                tv_finance_operate_profit.text = "${fomate(operProfit)}${getUnit(unit)}${getCurrency(currency)}"
                //除税前盈利
                tv_finance_before_profit.text = "${fomate(pbt)}${getUnit(unit)}${getCurrency(currency)}"
                //股东应占盈利
                tv_finance_mrmb.text = "${fomate(netProf)}${getUnit(unit)}${getCurrency(currency)}"
                //每股基本盈利
                tv_finance_basic_earn.text = "$eps${getCurrency(currency)}"
            }
        }

        //资产负债表
        mPresenter.balanceSheet(stockCode) {
            it?.apply {
                //总资产
                tv_finance_layer_assets.text = "${fomate(totalAss)}${getUnit(unit)}${getCurrency(currency)}"
                //总负债
                tv_finance_total_liability.text = "${fomate(totalDebt)}${getUnit(unit)}${getCurrency(currency)}"
                //股东权益
                tv_finance_equity.text = "${fomate(equity)}${getUnit(unit)}${getCurrency(currency)}"
            }
        }
        //现金流量列表
        mPresenter.cashFlow(stockCode) {
            it?.apply {
                //经营业务现金净额
                tv_net_cash.text = "${fomate(cfOperAct)}${getUnit(unit)}${getCurrency(currency)}"
                //投资业务现金净额
                tv_finance_one_cash.text = "${fomate(cfInv)}${getUnit(unit)}${getCurrency(currency)}"
                //融资业务现金净额
                tv_finance_two_cash.text = "${fomate(cfBeforeFin)}${getUnit(unit)}${getCurrency(currency)}"
            }
        }
        //单独处理现金流量图表
        getCrashList()
    }

    private var peOneList: MutableList<PEBean>? = null
    private var peTwoList: MutableList<PEBean>? = null

    /**
     * 获取市盈率图表数据
     */
    private fun getPeList() {
        when {
            financePeType == 1 && peOneList == null -> mPresenter.peList(stockCode, financePeType) {
                it?.apply {
                    peOneList = this
                    peView.setData(this)
                    tv_report_date.text = this.last().yearend + " " + this.last().pe.toScaleString(2)
                }
            }
            financePeType == 2 && peTwoList == null -> mPresenter.peList(stockCode, financePeType) {
                it?.apply {
                    peTwoList = this
                    peView.setData(this)
                    tv_report_date.text = this.last().yearend + " " + this.last().pe.toScaleString(2)
                }
            }
            else ->
                (if (financePeType == 1) peOneList else peTwoList)?.apply {
                    peView.setData(this)
                    tv_report_date.text = this.last().yearend + " " + this.last().pe.toScaleString(2)
                }
        }
    }

    private var crashOneList: MutableList<CashFlowBean>? = null
    private var crashTwoList: MutableList<CashFlowBean>? = null

    /**
     * 单独处理现金流量图表
     */
    private fun getCrashList() {
        when {
            financeCrashType == 1 && crashOneList == null -> mPresenter.cashFlowList(stockCode, financeCrashType) {
                it?.apply {
                    crashOneList = this
                    crashView.setData(this)
                }
            }
            financeCrashType == 2 && crashTwoList == null -> mPresenter.cashFlowList(stockCode, financeCrashType) {
                it?.apply {
                    crashTwoList = this
                    crashView.setData(this)
                }
            }
            else -> (if (financeCrashType == 1) crashOneList else crashTwoList)?.apply {
                crashView.setData(this)
            }
        }
    }
}
