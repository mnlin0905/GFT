package com.linktech.gft.activity.financing.market


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.skin.MyStockColorTextView
import com.linktech.gft.util.Const
import com.linktech.gft.util.ScreenUtils
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_turbine.*
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent
import skin.support.content.res.SkinCompatResources

/**
 * function : 涡轮
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_TurbineActivity)
@InjectActivityTitle(titleRes = R.string.label_turbine)
@InjectLayoutRes(layoutResId = R.layout.activity_turbine)
class TurbineActivity : BaseActivityRecord<OptionalListBean, BasePresenter<TurbineActivity>>(canRefresh = true, canLoadMore = true) {

    /**
     * 兩種表頭數據,成交/委託
     * key:請求類型 -> 表頭views
     *
     * item對應的格式
     */
    var headersChildren: List<TextView> = listOf()

    /**
     * 用于过滤搜索的code
     */
    private var searchText: String by viewBind(R.id.tv_search)

    /**
     * 当点击后跳转的界面
     */
    var onClickItemListener: (View, Int) -> Unit = { _, position ->
        routeCustom(ARouterConst.Activity_KLineActivity)
                .firstParam(adapter.dataResources[position])
                .navigation()
                .empty(comment = "股票k-line界面")
    }

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_entrust_search,
                listener = onClickItemListener
        ) {
            if ((this as ViewGroup).tag != localClassName) {
                //如果類型不符,則需要重新填充佈局
                removeAllViews()

                // 添加第一个 item(linear-layout)
                addView(LinearLayout(this@TurbineActivity).apply {
                    gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    setPadding(dispatchGetDimen(R.dimen.view_padding_margin_8dp), dispatchGetDimen(R.dimen.view_padding_margin_12dp), dispatchGetDimen(R.dimen.view_padding_margin_8dp), dispatchGetDimen(R.dimen.view_padding_margin_8dp))
                    orientation = LinearLayout.VERTICAL
                    addView(createTextView(null, R.dimen.text_size_normal_14sp, R.color.skin_body1_color, wrapSize = true).apply {
                        setPadding(0, 0, 0, dispatchGetDimen(R.dimen.divider_line_width_2dp))
                    }, ViewGroup.MarginLayoutParams(wrapContent, wrapContent))
                    addView(createTextView(null, R.dimen.text_size_small_12sp, R.color.skin_display1_color, wrapSize = true).apply {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.dark_icon_market_hk, 0, 0, 0)
                        compoundDrawablePadding = dispatchGetDimen(R.dimen.divider_line_width_2dp)
                    }, ViewGroup.MarginLayoutParams(wrapContent, wrapContent))
                }, ViewGroup.MarginLayoutParams((ScreenUtils.screenWidth / 2.5).toInt(), wrapContent))

                // 添加其他item
                for (index in 1 until headersChildren.size) {
                    addView(createTextView(null, R.dimen.text_size_normal_14sp, R.color.skin_body1_color, isStockTextView = index == 1 || index == 2).also {
                        if (index == 2) {
                            (getChildAt(1) as MyStockColorTextView).dependent = it as MyStockColorTextView
                        }
                    })
                }
                setTag(localClassName)
            }

            //填充數據
            for (index in 1 until this.childCount) {
                (getChildAt(index) as TextView).text = when (index) {
                    0 means "涡轮名称" -> it.getShareNameI()
                    1 means "最新价" -> it.realTimePrice.stockGetValue()
                    2 means "涨跌幅" -> it.priceLimit.stockGetPercent()
                    3 means "成交量" -> it.totalQuantity.stockGetBigNumberNullable() ?: "--"
                    4 means "到期日" -> it.maturityDate.longToTimeString()
                    5 means "街货比" -> "--"
                    6 means "溢价" -> "--"
                    7 means "引伸波幅" -> "--"
                    8 means "换股比率" -> "--"
                    9 means "有效杠杆" -> "--"
                    else -> TODO()
                }
            }

            // 填充名字 (first child)
            (getChildAt(0) as ViewGroup).also { parent ->
                (parent.getChildAt(0) as TextView).text = it.getShareNameI()
                (parent.getChildAt(1) as TextView).text = it.getShareCodeI()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))

        //動態生成佈局檔
        headersChildren = getStringArray(R.array.turbine_header)
                .mapIndexed { index, text ->
                    createTextView(text, R.dimen.text_size_normal_14sp, R.color.skin_display1_color, isSort = index in 1..2, fixHeight = dispatchGetDimen(R.dimen.prefer_view_height_48dp))
                }

        //可滾動部分
        headersChildren.mapIndexed { index, it ->
            if (index == 0) {
                ll_header.addView(it, ViewGroup.MarginLayoutParams((ScreenUtils.screenWidth / 2.5).toInt(), wrapContent))
            } else {
                ll_header.addView(it)
            }
        }
        //排序
        headersChildren.mapIndexed { index, tv ->
            tv.dOnClick {
                when (index) {
                    1 means "最新价" -> tv.dOnClick {
                        headersChildren[1].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 1 || sort == 2 || sort == null) R.drawable.dark_icon_sort_desc
                        else if (sort == 3) R.drawable.dark_icon_sort_asc
                        else R.drawable.dark_icon_sort_default, 0)
                        headersChildren[2].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                        when (sort) {
                            1, 2, null -> sort = 3
                            3 -> sort = 4
                            4 -> sort = null
                        }
                        srl_refresh.autoRefresh()
                    }
                    2 means "涨跌幅" -> tv.dOnClick {
                        headersChildren[1].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
                        headersChildren[2].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (sort == 3 || sort == 4 || sort == null) R.drawable.dark_icon_sort_desc
                        else if (sort == 1) R.drawable.dark_icon_sort_asc
                        else R.drawable.dark_icon_sort_default, 0)
                        when (sort) {
                            3, 4, null -> sort = 1
                            1 -> sort = 2
                            2 -> sort = null
                        }
                        srl_refresh.autoRefresh()
                    }
                }
            }
        }

        //close
        iv_close.dOnClick {
            ll_alert.visibility = View.GONE
        }

        // 查询提示
        tv_jump.dOnClick {
        }

        // 顶部
        tv_message_at_once.visibility = if (BaseApplication.app.ipAddressI?.isChinaIp == true) View.GONE else View.VISIBLE

        // search
        cv_search.dOnClick {
            routeCustom(ARouterConst.Activity_AddSharesActivity)
                    .firstParam(1)
                    .navigation(baseActivity, Const.REQUEST_CODE_ONE)
                    .empty(comment = "搜索正股,等返回结果再刷新列表")
        }

        // 切换显示
        rg_type.setOnCheckedChangeListener { group, checkedId ->
            srl_refresh.autoRefresh()
        }

        onRefresh(srl_refresh)
    }


    // 1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序 6-涨跌额升序
    private var sort: Int? = null

    /**
     * 創建text-view
     */
    private fun createTextView(txt: String?, @DimenRes dimenRes: Int, @ColorRes colorRes: Int, isSort: Boolean = false, wrapSize: Boolean = false, fixHeight: Int = 0, isStockTextView: Boolean = false): TextView {
        return (if (isStockTextView) MyStockColorTextView(this@TurbineActivity) else TextView(this@TurbineActivity)).apply {
            textColor = SkinCompatResources.getColor(this@TurbineActivity, colorRes)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, dispatchGetDimen(dimenRes).toFloat())
            text = txt
            gravity = Gravity.CENTER
            singleLine = true
            if (isSort) setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dark_icon_sort_default, 0)
            if (isSort) setPadding(dispatchGetDimen(R.dimen.view_padding_margin_16dp), 0, dispatchGetDimen(R.dimen.view_padding_margin_16dp), 0)
            if (!wrapSize) {
                maxWidth = ScreenUtils.screenWidth / 4
                minWidth = ScreenUtils.screenWidth / 4
            }
            // 固定高度
            if (fixHeight != 0) {
                minHeight = fixHeight
                maxHeight = fixHeight
            }
        }
    }

    /**
     * 如果 loadMore 和 onRefresh 使用 同一個方法和參數，則實現該方法即可
     */
    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)
        mPresenter.underlyingStockList(currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, searchText, 1, if (rg_type.checkedRadioButtonId == R.id.rb_one) 1 else 2, sort, _username) {
            loadDataFinish(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 再次搜索
        if (requestCode == Const.REQUEST_CODE_ONE && resultCode == Activity.RESULT_OK) {
            searchText = (data?.getSerializableExtra(Const.KEY_COMMON_VALUE) as OptionalListBean?)?.getShareCodeI().ifNullReturn()
            srl_refresh.autoRefresh()
        }
    }
}
