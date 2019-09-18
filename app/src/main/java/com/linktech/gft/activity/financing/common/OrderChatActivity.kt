package com.linktech.gft.activity.financing.common


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.base.BaseRecyclerViewHolder
import com.linktech.gft.bean.ChatMessageBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import kotlinx.android.synthetic.main.activity_order_chat.*
import kotlinx.android.synthetic.main.item_order_chat_system.view.*
import org.jetbrains.anko.backgroundDrawable

/**
 * function : 客服聊天
 *
 * Created on 2018/12/5  17:50
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_OrderChatActivity)
@InjectLayoutRes(layoutResId = R.layout.activity_order_chat)
@InjectActivityTitle(titleRes = R.string.label_order_chat)
class OrderChatActivity : BaseActivity<BasePresenter<OrderChatActivity>>() {
    /**
     * 聊天記錄,數據源
     * 適配器
     */
    private var datas: MutableList<ChatMessageBean> = mutableListOf(
            ChatMessageBean(chat_status = Const.TYPE_CHAT_MESSAGE_SYSTEM, chat_message = "Henry為您服務", chat_time = "03-06 16:04"),
            ChatMessageBean(chat_status = Const.TYPE_CHAT_MESSAGE_YOURS, chat_message = "您好,歡迎訪問港付通!", chat_time = "03-06 16:04"),
            ChatMessageBean(chat_status = Const.TYPE_CHAT_MESSAGE_MINE, chat_message = "在哪里切換語言?", chat_time = "03-06 16:04")
    )
    private lateinit var adapter: ChatAdapter<ChatMessageBean>

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        //添加遮罩效果
        setStatusBarColor(dispatchGetColor(R.color.transparent_mask))
        //背景色
        et_message.postV {
            backgroundDrawable = customShapeDrawable(height.toFloat(), dispatchGetSkinColor(R.color.dark_color_1d2037_f0f0f0))
        }

        //初始化
        adapter = ChatAdapter(datas)
        rv_messages.run {
            layoutManager = LinearLayoutManager(this@OrderChatActivity, LinearLayout.VERTICAL, false)
            adapter = this@OrderChatActivity.adapter
        }

        //文本框(禁止輸入表情)
        et_message.addFaceFilter()
        et_message.setOnEditorActionListener { v, actionId, event ->
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                val msg = et_message.text.toString()
                if (msg.isNotBlank()) {
                    datas.add(ChatMessageBean(chat_status = Const.TYPE_CHAT_MESSAGE_MINE, chat_message = msg, chat_time = "03-06 16:04"))
                    datas.add(ChatMessageBean(chat_status = Const.TYPE_CHAT_MESSAGE_YOURS, chat_message = "請諮詢百度!", chat_time = "03-06 16:04"))
                    adapter.notifyDataSetChanged()
                    rv_messages.post {
                        rv_messages.scrollToPosition(datas.size - 1)
                    }

                    et_message.text = null
                    empty(comment = "發送消息,清除記錄")
                }
            }
            false
        }
    }
}

/**
 * function : 聊天適配器
 *
 * Created on 2018/12/26  11:25
 * @author mnlin
 */
class ChatAdapter<T : ChatMessageBean>(
        private var datas: MutableList<T>
) : RecyclerView.Adapter<BaseRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        val layout = when (viewType) {
            Const.TYPE_CHAT_MESSAGE_SYSTEM -> R.layout.item_order_chat_system
            Const.TYPE_CHAT_MESSAGE_MINE -> R.layout.item_order_chat_mine
            Const.TYPE_CHAT_MESSAGE_YOURS -> R.layout.item_order_chat_yours
            else -> TODO()
        }

        //創建holder
        return BaseRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false), null)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    /**
     * 目前支持三種類型數據: KEY_TYPE_CHAT_MESSAGE
     * 系統消息 TYPE_CHAT_MESSAGE_SYSTEM
     * 對方 TYPE_CHAT_MESSAGE_MINE
     * 自己 TYPE_CHAT_MESSAGE_YOURS
     */
    override fun getItemViewType(position: Int): Int {
        return datas[position].chat_status
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.itemView.tv_message.text = datas[position].chat_message
        holder.itemView.tv_time?.text = datas[position].chat_time
    }
}
