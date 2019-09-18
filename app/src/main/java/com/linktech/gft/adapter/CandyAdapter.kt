package com.linktech.gft.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.linktech.gft.R
import com.linktech.gft.base.BaseRecyclerViewHolder
import com.linktech.gft.bean.AssetRecode
import kotlinx.android.synthetic.main.item_crowd_recode.view.*

/**
 * Created on 2018/1/10
 * function : 转入记录查询
 *
 * @author LinkTech
 */
class CandyAdapter : RecyclerView.Adapter<BaseRecyclerViewHolder>() {
    private var datas: List<AssetRecode> = listOf()

    fun setData(datas: List<AssetRecode>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        return BaseRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_crowd_recode, parent, false))
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        var bean = datas[position]
        holder.itemView.run {
            tv_time.text = bean.create_time
            tv_describe.text = bean.sweet_source.toString()
            tv_amount.text = bean.amount.toString()
        }
    }
}
