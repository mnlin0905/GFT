package com.linktech.gft.window

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.knowledge.mnlin.sdialog.widgets.IncludeDialogViewGroup
import com.linktech.gft.R
import com.linktech.gft.base.BaseRecyclerViewAdapter
import com.linktech.gft.plugins.clearAddAll
import com.linktech.gft.plugins.dOnClick
import com.linktech.gft.plugins.getString
import kotlinx.android.synthetic.main.layout_dialog_common_list_select.view.*

/**
 * function : 生成通用样式:title + list + cancel 的 dialog(模拟),可以在activity中显示出来
 *
 * 支持一个dialog显示多种多样的选项(一个界面可以只使用一个该布局)
 *
 * Created on 2019/7/22  11:39
 * @author mnlin
 */
class PickListOptionsDialog : DefaultSimulateDialogImpl<View, FrameLayout.LayoutParams> {
    /**
     * 点击时触发
     *
     * type ,  数据源类型
     * position , 点击位置
     */
    private var onSelectOptions: (Int, Int) -> Boolean

    /**
     * 数据源类型
     */
    var type: Int = -1

    /**
     * 数据源
     */
    var options: MutableList<List<String>> = mutableListOf()

    val datas: MutableList<String> = mutableListOf()
    val adapter: BaseRecyclerViewAdapter<String>

    constructor(container: IncludeDialogViewGroup, title: String? = null, cancel: String? = Unit.getString(R.string.function_cancel), options: MutableList<List<String>> = mutableListOf(), onSelectOptions: (Int, Int) -> Boolean) : super(container, R.layout.layout_dialog_common_list_select) {
        this.options = options
        this.onSelectOptions = onSelectOptions
        adapter = BaseRecyclerViewAdapter(
                dataResources = datas,
                layoutResId = R.layout.item_layout_dialog_common_list_select,
                listener = { _, position ->
                    if(!onSelectOptions(type, position)){
                        close(true)
                    }
                }
        ) {
            (this as TextView).text = it
        }

        generateView().also {
            it.tv_dialog_title.text = title ?: "请选择"
            it.tv_dialog_cancel.text = cancel

            // list
            it.rv_dialog_options.also {
                it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
                it.adapter = adapter
            }

            // cancel
            it.tv_dialog_cancel.dOnClick {
                close(true)
            }
        }
    }

    /**
     * 显示指定类型的数据源
     */
    fun show(type: Int) {
        if (type < 0 || type >= options.size) {
            return
        }

        this.type = type
        datas.clearAddAll(options[this.type])
        adapter.notifyDataSetChanged()
        show(AlphaIDVGAnimatorImpl())
    }
}