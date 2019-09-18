package com.linktech.gft.fragment.financing

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseBean
import com.linktech.gft.base.BaseFragmentRecord
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.dOnClick
import com.linktech.gft.plugins.toast
import kotlinx.android.synthetic.main.fragment_assets.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.view.*
import kotlinx.android.synthetic.main.layout_item_assets_fragment.view.*
import skin.support.content.res.SkinCompatResources

/**
 * 理财
 *
 * Created on 2018/8/13  15:15
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_AssetsFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_assets)
open class AssetsFragment : BaseFragmentRecord<BaseBean, BasePresenter<AssetsFragment>>(false, false) {

    init {
        datas.addAll(listOf(BaseBean(),BaseBean()))
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.layout_item_assets_fragment,
                listener = { view: View, i: Int ->
                    toast(R.string.common_not_develop)
                }
        ) {
            tv_one_day.text = SpannableStringBuilder(getString(R.string.fragment_assets_one_day))
                    .append(SpannableString("0.3454").apply {
                        setSpan(ForegroundColorSpan(SkinCompatResources.getColor(baseActivity, R.color.skin_accent_color)), 0, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    }).append(getString(R.string.fragment_assets_one_day_money))
            bt_common_function.text =if(datas.indexOf(it) == 0) getString(R.string.fragment_assets_function) else getString(R.string.fragment_assets_function_two)
            bt_common_function.dOnClick {
                toast(R.string.common_not_develop)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        toolbar.title = getString(R.string.action_financing_assets)
    }

}
