package com.linktech.gft.activity.other

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.adapter.ChooseCountryAdapter
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.CountryBean
import com.linktech.gft.plugins.ARouterConst
import com.linktech.gft.plugins.InjectActivityTitle
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.util.Const
import com.linktech.gft.util.KeyComparator
import kotlinx.android.synthetic.main.activity_choose_country.*
import java.util.*

/**
 * function---- AboutActivity
 *
 * Created(Gradle default create) by LinkTech on 2018/01/22 05:45:55 (+0000).
 */
@Route(path = ARouterConst.Activity_ChooseCountryActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_choose_country)
@InjectActivityTitle(titleRes = R.string.label_choose_country)
class ChooseCountryActivity : BaseActivity<BasePresenter<ChooseCountryActivity>>() {

    private lateinit var chooseCountryAdapter: ChooseCountryAdapter
    private var countryBeans: MutableList<CountryBean> = mutableListOf()
    private lateinit var keyComparator: KeyComparator

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        keyComparator = KeyComparator.getInstance()
        chooseCountryAdapter = ChooseCountryAdapter(this@ChooseCountryActivity)
        listview.adapter = chooseCountryAdapter
        //设置右侧触摸监听
        sidebar.setTextView(group_dialog)
        sidebar.setOnTouchingLetterChangedListener {
            val position = chooseCountryAdapter.getPositionForSection(it[0].toInt())
            if (position != -1) {
                listview.setSelection(position)
            }
        }
        listview.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra(Const.KEY_COUNTRY_NAME, countryBeans[position].getCountry())
            intent.putExtra(Const.KEY_NATION_CODE, countryBeans[position].codeArea)
            setResult(RESULT_OK, intent)
            finish()
        }

        mPresenter.findInternationSmsList {
            it?.apply {
                countryBeans.addAll(this as List<CountryBean>)
                Collections.sort(countryBeans, keyComparator)
                chooseCountryAdapter.setData(countryBeans)
            }
        }
    }
}