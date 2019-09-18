package com.linktech.gft.activity.financing.market


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.OptionalListBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.activity_add_shares.*
import kotlinx.android.synthetic.main.item_add_shares.view.*

/**
 * function : 暫無數據介面(未完成功能)
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_AddSharesActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_add_shares)
@InjectActivityTitle(title = "")
class AddSharesActivity : BaseActivityRecord<OptionalListBean, BasePresenter<AddSharesActivity>>(true, true) {
    /**
     * type=0  来自于自选
     * type=1 来自于搜索股票
     * type=2 牛熊选择正股
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var type: Int = 0

    /**
     * 已加入自选的
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_TWO, required = false)
    @JvmField
    var customShares: MutableList<String?>? = mutableListOf()

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_add_shares,
                listener = { view: View, i: Int ->
                    when (type) {
                        0 means "来自于自选,顯示K線等數據" ->
                            routeCustom(ARouterConst.Activity_KLineActivity)
                                    .firstParam(datas[i])
                                    .navigation()
                                    .empty(comment = "顯示K線等數據")
                        1 means "来自于搜索股票,跳到交易",
                        2 means "涡轮牛熊选择正股" ->
                            Intent().run {
                                putExtra(Const.KEY_COMMON_VALUE, datas[i])
                                setResult(Activity.RESULT_OK, this)
                                finish()
                            }
                    }
                },
                childListeners = listOf(
                        R.id.iv_operate means "添加/移除 自選" to { holder ->
                            dOnClick {
                                datas[holder.currentPosition].also {
                                    mPresenter.addOptional(_username, _signature, it.getShareCodeI()) { _ ->
                                        setResult(Activity.RESULT_OK)

                                        it.isSELECTED_STATUS = !it.isSELECTED_STATUS
                                        if (it.isSELECTED_STATUS) {
                                            customShares!!.add(it.getShareCodeI())
                                            toast(context.getString(R.string.activity_add_shares_add))
                                        } else {
                                            //當前為已添加,則需要進行移除操作
                                            customShares!!.remove(it.getShareCodeI())
                                            toast(context.getString(R.string.activity_add_shares_remove))
                                        }

                                        //刷新介面
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                )
        ) {
            iv_type.setImageResource(R.drawable.dark_icon_market_hk)
            tv_name.text = it.getShareNameI()
            tv_code.text = it.getShareCodeI()

            if (type == 0) {
                iv_operate.setImageResource(if (it.isSELECTED_STATUS) R.drawable.dark_icon_add_shares_less else R.drawable.dark_icon_add_shares_more)
            }
        }
    }

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        customShares = customShares ?: mutableListOf()

        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        //搜索
        et_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                onRefresh(srl_refresh)
            }
            false
        }

        // cancel
        tv_cancel.dOnClick {
            onBackPressed()
        }
    }

    override fun onLoadMoreAndOnRefresh(refreshLayout: RefreshLayout) {
        super.onLoadMoreAndOnRefresh(refreshLayout)
        //獲取搜索結果
        et_search.text.toString().filterAlso({ it.isNotBlank() }) {
            //獲取搜索結果
            mPresenter.getSecurityList(it, null, currentPage, Const.CONST_PER_PAGE_SIZE_DEFAULT, 7) {
                loadDataFinish(it)
            }
        }
    }

    /**
     * 处理返回结果值,判断是否已添加自选
     */
    override fun loadDataFinish(beans: MutableList<OptionalListBean>?) {
        beans?.forEach {
            it.isSELECTED_STATUS = customShares!!.contains(it.getShareCodeI())
        }
        super.loadDataFinish(beans)
    }
}
