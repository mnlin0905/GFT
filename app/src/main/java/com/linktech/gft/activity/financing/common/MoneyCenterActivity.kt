package com.linktech.gft.activity.financing.common

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivityRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.bean.ServiceBean
import com.linktech.gft.plugins.*
import kotlinx.android.synthetic.main.item_fragment_ecology.view.*

/**
 * function : 理財中心(中間層)
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_MoneyCenterActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_money_center)
@InjectActivityTitle(titleRes = R.string.label_money_center)
class MoneyCenterActivity : BaseActivityRecord<ServiceBean, BasePresenter<MoneyCenterActivity>>(false,false) {

    init {
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_fragment_ecology,
                listener = { view: View, i: Int ->
                    when (i) {
                        in 0..1 -> toast(R.string.common_not_develop)
                        2 -> route(ARouterConst.Activity_FinancingActivity).empty(comment = "理財中心內部")
                    }
                }
        ) {
            iv_image.setImageResource(it.imgRes ?: R.drawable.default_head_img)
            tv_title.text = it.name
            tv_subtitle.text = it.description
        }
    }

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        //填充數據
        datas.add(ServiceBean(name = getString(R.string.activity_money_center_first), description = getString(R.string.activity_money_center_first_desp), imgRes = R.drawable.icon_money_center_one))
        datas.add(ServiceBean(name = getString(R.string.activity_money_center_second), description = getString(R.string.activity_money_center_second_desp), imgRes = R.drawable.icon_money_center_two))
        datas.add(ServiceBean(name = getString(R.string.activity_money_center_third), description = getString(R.string.activity_money_center_third_desp), imgRes = R.drawable.icon_money_center_three))
        adapter.notifyDataSetChanged()
    }
}
