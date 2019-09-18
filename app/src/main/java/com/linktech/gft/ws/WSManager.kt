package com.linktech.gft.ws

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.google.gson.GsonBuilder
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BaseWSBean
import com.linktech.gft.bean.WSControlPush
import com.linktech.gft.bean._wsDataMap
import com.linktech.gft.plugins.ApplicationModule
import com.linktech.gft.util.ListTypeAdapterFactory
import com.neovisionaries.ws.client.*
import com.orhanobut.logger.Logger
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Type


/**
 * ws 管理
 */
class WSManager {
    companion object {
        //设置帧队列最大值为5
        private const val FRAME_QUEUE_SIZE = 5
        //超时时间
        private const val CONNECT_TIMEOUT = 5000
        //重连最小时间间隔
        private const val minInterval: Long = 3000
        //重连最大时间间隔
        private const val maxInterval: Long = 60000

        //监听回调
        private var listeners: MutableList<BaseWSListener<BaseWSBean>> = mutableListOf()

        //对象
        private var ws: WebSocket? = null

        //地址
        private val url = BaseApplication.app.webSocketUrl

        /**
         * handler
         */
        val handler = Handler(Looper.getMainLooper())

        //gson
        var gson = GsonBuilder().serializeNulls()
                .registerTypeAdapterFactory(ListTypeAdapterFactory())//对空列表处理
                .setDateFormat("yyyy:MM:dd HH:mm:ss")
                .registerTypeAdapter(String::class.java, ApplicationModule.ZeroDeleteAdapter())//0.00值处理
                .create()

        /**
         * 初始化
         *
         * 禁止在连接成功情况下多次调用
         */
        @Synchronized
        fun init() {
            try {
                if (ws == null) {
                    ws = WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                            .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                            .setMaxPayloadSize(FRAME_QUEUE_SIZE)
                            .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                            .addListener(WsListener())//添加回调监听
                            .connectAsynchronously()//异步连接
                } else if (!ws!!.isOpen) {
                    ws = ws!!.recreate().connectAsynchronously()
                } else {
                    // nothing
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 断开连接
         */
        fun disconnect() {
            ws?.disconnect()
            ws = null
        }

        /**
         * 添加/移除依赖
         */
        fun <T : BaseWSBean> addListener(listener: BaseWSListener<T>) {
            listeners.add(listener as BaseWSListener<BaseWSBean>)
        }

        /**
         * 添加/移除依赖
         */
        fun removeListener(listener: BaseWSListener<*>) {
            listeners.remove(listener)
        }

        /**
         * 添加/移除依赖
         */
        fun removeListener(ls: List<BaseWSListener<*>>) {
            ls.forEach {
                listeners.remove(it)
            }
        }

        /**
         * 添加/移除依赖
         */
        fun removeListener() {
            listeners.clear()
        }

        /**
         * 添加/移除依赖
         */
        fun removeListener(classType: Type) {
            for (index in listeners.size - 1 downTo 0) {
                if (listeners[index].classType == classType) {
                    listeners.removeAt(index)
                }
            }
        }

        /**
         * 发送数据
         */
        fun sendText(msg: String?) {
            if (!msg.isNullOrEmpty() && ws != null && ws!!.isOpen) {
                ws!!.sendText(msg)
            }
        }

        /**
         * 发送数据
         */
        fun sendText(msg: WSControlPush) {
            Logger.v("WSManager:订阅消息 ：${msg.toJson()}")
            sendText(msg.toJson())
        }

        @SuppressLint("CheckResult")
        private fun parseData(text: String) {
            val type = JSONObject(text).getString("type")

            //缓存一次解析数据,在多个监听器监听同一组数据时,保证不会出现乱流
            var cacheParseData: BaseWSBean? = null

            listeners.forEach {
                if (_wsDataMap[it.classType] == type) {
                    try {
                        cacheParseData = cacheParseData ?: gson.fromJson(text, it.classType)
                        if (cacheParseData != null && it.judgeI?.ifFitTypeData(cacheParseData!!) != false) {
                            handler.post { it.callback.run(cacheParseData!!) }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    class WsListener : WebSocketAdapter() {
        @Throws(Exception::class)
        override fun onTextMessage(websocket: WebSocket?, text: String?) {
            super.onTextMessage(websocket, text)
            Logger.v("WSManager:onTextMessage ： ${text?.length} \n $text")
            text?.let {
                parseData(it)
            }
        }

        @Throws(Exception::class)
        override fun onConnected(websocket: WebSocket?, headers: Map<String, List<String>>?) {
            Logger.e("ws 已连接.")

            super.onConnected(websocket, headers)
        }

        @Throws(Exception::class)
        override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
            Logger.v("WSManager:onConnectError")
            super.onConnectError(websocket, exception)

            tryAgain()
        }

        @Throws(Exception::class)
        override fun onDisconnected(websocket: WebSocket?, serverCloseFrame: WebSocketFrame?, clientCloseFrame: WebSocketFrame?, closedByServer: Boolean) {
            Logger.e("ws 已断开.")
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)

            tryAgain()
        }

        /**
         * 连接失败,会尝试重新连接(5s后)
         */
        private fun tryAgain() {
            handler.postDelayed({
                Logger.e("ws 连接失败,开始重新连接...")
                init()
            }, 5000)
        }
    }
}
