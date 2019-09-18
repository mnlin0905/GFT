package com.linktech.gft.base

import android.annotation.SuppressLint
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.linktech.gft.plugins.empty

/**
 * function : 为RecyclerView添加通用的适配基类,减少size的获取,减少view的 获取
 *
 * Created on 2018/9/10  14:01
 * @author mnlin
 */
open class BaseVDelegateAdapter<T>(
        /**
         * 数据源
         */
        var dataResources: MutableList<T>,

        /**
         * 必须保证layoutResId有值
         */
        @LayoutRes
        private var layoutResId: Int = 0,

        /**
         * layoutHelper
         */
        private var layoutHelper: LayoutHelper,

        /**
         * 监听器
         */
        var listener: ((View, Int) -> Unit)? = null,

        /**
         * 子类监听器
         */
        private var childListeners: List<Pair<Int, View.(BaseRecyclerViewHolder) -> Unit>> = listOf(),

        /**
         * onBindViewHolder回调的方法
         */
        @SuppressLint("SetTextI18n")
        private var actionOnBindViewHolder: (View.(bean: T, position: Int, offsetTotal: Int) -> Unit)? = null) : DelegateAdapter.Adapter<BaseRecyclerViewHolder>() {

    /**
     * 第二构造器,用于array类型数据
     */
    constructor(dataResources: Array<T>, @LayoutRes layoutResId: Int = 0, layoutHelper: LayoutHelper, listener: ((View, Int) -> Unit)? = null, onBindViewHolder: View.(bean: T, position: Int, offsetTotal: Int) -> Unit) : this(dataResources = dataResources.toMutableList(), layoutResId = layoutResId, layoutHelper = layoutHelper, listener = listener, actionOnBindViewHolder = onBindViewHolder)

    /**
     * 该方法不再使用
     */
    @Deprecated(message = "请调用 onBindViewHolderWithOffset(holder: BaseRecyclerViewHolder, position: Int, offsetTotal: Int) 方法")
    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        empty(comment = "暂无操作")
    }

    /**
     * 绑定holder,多提供了当前偏移量参数
     */
    override fun onBindViewHolderWithOffset(holder: BaseRecyclerViewHolder, position: Int, offsetTotal: Int) {
        super.onBindViewHolderWithOffset(holder, position, offsetTotal)
        holder.setTag(position)
        actionOnBindViewHolder?.invoke(holder.itemView, dataResources[position], position, offsetTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        return BaseRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(layoutResId, parent, false), listener).also { holder ->
            holder.apply {
                // 循环添加点击事件
                childListeners.map {
                    itemView.findViewById<View>(it.first).run {
                        it.second(this, this@apply)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataResources.size
    }

    /**
     * 创建布局管理器
     */
    override fun onCreateLayoutHelper(): LayoutHelper {
        return layoutHelper
    }


}
