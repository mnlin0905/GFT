package com.linktech.gft.base

import android.annotation.SuppressLint
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linktech.gft.plugins.InjectLayoutRes
import com.linktech.gft.plugins.getActivityFromView

/**
 * function : 为RecyclerView添加通用的适配基类,减少size的获取,减少view的 获取
 *
 * Created on 2018/9/10  14:01
 * @author mnlin
 */
open class BaseRecyclerViewAdapter<T>(
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
        private var actionOnBindViewHolder: (View.(bean: T) -> Unit)? = null) : RecyclerView.Adapter<BaseRecyclerViewHolder>() {

    /**
     * 第二构造器,用于array类型数据
     */
    constructor(dataResources: Array<T>, @LayoutRes layoutResId: Int = 0, listener: ((View, Int) -> Unit)? = null, onBindViewHolder: View.(bean: T) -> Unit) : this(dataResources = dataResources.toMutableList(), layoutResId = layoutResId, listener = listener, actionOnBindViewHolder = onBindViewHolder)

    init {
        //若有注解,则反射实例获取布局文件
        val annotations = javaClass.annotations
        for (annotation in annotations) {
            if (annotation is InjectLayoutRes) {
                layoutResId = annotation.layoutResId
            }
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        actionOnBindViewHolder?.invoke(holder.itemView, dataResources[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        return BaseRecyclerViewHolder(LayoutInflater.from(parent.getActivityFromView()).inflate(layoutResId, parent, false), listener).also { holder ->
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
}
