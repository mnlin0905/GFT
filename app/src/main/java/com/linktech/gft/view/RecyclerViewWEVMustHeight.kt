package com.linktech.gft.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Created on 2018/7/30  14:46
 * function : 可以携带一个EmptyView  的 RecyclerView
 *
 * @author mnlin
 */
open class RecyclerViewWEVMustHeight @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    /**
     * 空视图
     */
    var emptyView: View? = null
        set(value) {
            if (field === value) return

            field = value
            adapter?.notifyDataSetChanged()
        }

    /**
     * 设置数据监听
     */
    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()

                // 如果数据不存在,则设置空视图
                emptyView?.let {
                    it.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                    visibility = if (adapter.itemCount == 0) View.GONE else View.VISIBLE
                }
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2,View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}
