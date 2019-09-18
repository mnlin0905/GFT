package com.linktech.gft.fragment.financing

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.dOnClick
import com.linktech.gft.plugins.toast
import kotlinx.android.synthetic.main.fragment_k_news_item.*

/**
 * function : 新聞碎片
 *
 * Created on 2019/3/25  15:21
 * @author mnlin
 */
@Route(path = ARouterConst.Fragment_KNewsItemFragment)
@InjectLayoutRes(layoutResId = R.layout.fragment_k_news_item)
class KNewsItemFragment : BaseFragment<BasePresenter<KNewsItemFragment>>() {

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        iv_test.dOnClick {
            toast(R.string.common_not_develop)
        }
    }
}
