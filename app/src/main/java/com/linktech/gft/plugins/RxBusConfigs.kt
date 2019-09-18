package com.linktech.gft.plugins

import com.linktech.gft.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**************************************
 * function : RxBus 消息传递以及Bean类
 *
 * Created on 2018/6/27  11:52
 * @author mnlin
 **************************************/

/**
 * 功能----RxBus处理数据总线
 * Created by LinkTech on 2017/9/23.
 *
 * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
 */
class RxBus
private constructor() {
    private val bus: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    /**
     * 发送一个新的事件
     */
    fun post(o: Any) {
        bus.onNext(o)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservable(event: Class<T>): Observable<T> {
        return bus.ofType(event)
    }

    companion object {
        @Volatile
        private var defaultInstance: RxBus? = null

        private var group: CompositeDisposable? = null

        /**
         * 单例RxBus
         */
        val instance: RxBus?
            get() {
                if (defaultInstance == null) {
                    synchronized(RxBus::class.java) {
                        if (defaultInstance == null) {
                            defaultInstance = RxBus()
                            group = CompositeDisposable()
                        }
                    }
                }
                return defaultInstance
            }
    }
}

/**
 * function : ARouter没有相应权限被拦截
 * Created on 2018/3/27
 *
 * 提示信息
 * 左侧按钮(取消)
 * 右侧按钮(确认)
 * 右侧点击事件
 *
 * @author LinkTech
 */
data class ARouterNoPermission(var title: String?,
                               var leftText: String?,
                               var rightText: String?,
                               var callback: BasePresenter.HttpCallback<*>?)

data class SwitchColor(var isRedUp: Boolean)