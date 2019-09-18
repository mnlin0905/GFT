package com.linktech.gft.bean

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linktech.gft.plugins.empty
import java.io.Serializable
import kotlin.reflect.KProperty

/**************************************
 * function : Plugins 需要的一些 bean 类
 *
 * Created on 2018/12/14  10:30
 * @author mnlin
 **************************************/

/**
 * function : 四个元素的对象
 *
 * Created on 2018/9/12  20:12
 * @author mnlin
 */
data class Quadra<A, B, C, D>(
        var first: A,
        var second: B,
        var third: C,
        var fourth: D
) {

    /**
     * Returns string representation of the [Triple] including its [first], [second] and [third] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

/**
 * function : kotlin 用于变量委托的类,现指定委托规则:
 *
 * | view | regular(返回值类型) |
 * |  --  |    --   |
 * |   TextView     |   String    |
 * |   CheckBox     |   Boolean    |
 * |        |       |
 * |        |       |
 *
 *
 * 使用前需确保View类有值,否则调用可能失败
 *
 * Created on 2018/12/14  11:34
 * @author mnlin
 */
@Suppress("UNCHECKED_CAST")
class ViewDelegate<T : Serializable>(private var target: Any, private var id: Int, private var default: Serializable? = null) {
    private var view: View? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        preInit()

        return when (view) {
            is TextView -> (view as TextView).text.toString() as T
            null -> when (default) {
                null -> TODO("请在view加载后再进行操作,或添加默认的值")
                else -> default as T
            }
            else -> TODO("请定义规则")
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        preInit()

        when (view) {
            is TextView -> (view as TextView).text = value as CharSequence
            null -> when (default) {
                null -> TODO("请在view加载后再进行操作")
                else -> default = value
            }
            else -> TODO("请定义规则")
        }
    }

    /**
     * 未初始化时进行初始化操作
     */
    private fun preInit() {
        if (view == null) {
            view = when (target) {
                is Fragment -> (target as Fragment).view?.findViewById(id)
                is Activity -> (target as Activity).findViewById(id)
                is ViewGroup -> (target as ViewGroup).findViewById(id)
                else -> null
            }
        }
    }
}

/**
 * function : kotlin 用于变量委托的类,现指定委托规则:(可以为null)
 *
 * | view | regular(返回值类型) |
 * |  --  |    --   |
 * |   TextView     |   String    |
 * |   CheckBox     |   Boolean    |
 * |        |       |
 * |        |       |
 *
 *
 * 使用前需确保View类有值,否则调用可能失败
 *
 * Created on 2018/12/14  11:34
 * @author mnlin
 */
@Suppress("UNCHECKED_CAST")
class ViewDelegateWithNull<T : Serializable?>(private var target: Any, private var id: Int) {
    private var view: View? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        preInit()

        return when (view) {
            is TextView -> (view as TextView).text.toString() as T?
            null -> null
            else -> TODO("请定义规则")
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        preInit()

        when (view) {
            is TextView -> (view as TextView).text = value as CharSequence?
            null -> empty(TODO = "请在view加载后再进行操作")
            else -> TODO("请定义规则")
        }
    }

    /**
     * 未初始化时进行初始化操作
     */
    private fun preInit() {
        if (view == null) {
            view = when (target) {
                is Fragment -> (target as Fragment).view?.findViewById(id)
                is Activity -> (target as Activity).findViewById(id)
                is ViewGroup -> (target as ViewGroup).findViewById(id)
                else -> null
            }
        }
    }
}

/**
 * function : 绑定set方法能使用的操作,除了赋值还可以处理其他内容
 *
 * Created on 2019/7/15  13:52
 * @author mnlin
 */
class SetMethodBindNullable<T : Any?>(var callback: SetMethodBindNullable<T>.(T?) -> Unit) {
    private var proxy: T? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return proxy
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        proxy = value
        callback(this, value)
    }

    /**
     * 直接将值回归 null ,表示设置的值不符合需要的区间
     */
    fun <F> resetNullDirection(returnValue: F? = null): F? {
        proxy = null
        return returnValue
    }
}

/**
 * function : 绑定set方法能使用的操作,除了赋值还可以处理其他内容(非null)
 *
 * Created on 2019/7/15  13:52
 * @author mnlin
 */
class SetMethodBindNotNull<T : Any>(var callback: (T) -> Unit) {
    private var proxy: T? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return if (proxy == null) {
            try {
                0 as T
            } catch (err: TypeCastException) {
                throw err
            }
        } else {
            proxy!!
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        proxy = value
        callback(value)
    }
}