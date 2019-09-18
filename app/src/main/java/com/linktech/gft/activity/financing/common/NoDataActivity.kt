package com.linktech.gft.activity.financing.common


import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.util.Const

/**
 * function : 暫無數據介面(未完成功能)
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_NoDataActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_no_data)
class NoDataActivity : BaseActivity<BasePresenter<NoDataActivity>>() {
    /**
     * title
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = false)
    @JvmField
    var titleStr: String? = null

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        //標題
        title = titleStr
    }
}
