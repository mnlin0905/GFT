package com.linktech.gft.plugins

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.support.annotation.*
import android.support.v4.app.ActivityOptionsCompat
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.internal.DebouncingOnClickListener
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.knowledge.mnlin.linemenuview.LineMenuListener
import com.knowledge.mnlin.linemenuview.LineMenuView
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.knowledge.mnlin.sdialog.widgets.IncludeDialogViewGroup
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BaseEvent
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.bean.*
import com.linktech.gft.util.*
import com.linktech.gft.util.Quadra
import com.linktech.gft.window.CancelConfirmDialogFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_dialog_title_cancel_confirm.view.*
import org.jetbrains.anko.fingerprintManager
import org.json.JSONObject
import skin.support.content.res.SkinCompatResources
import java.io.Serializable
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**************************************
 * function : 向已有的类添加方法,避免子类的生成
 *
 * Created on 2018/7/2  11:38
 * @author mnlin
 **************************************/

/**
 * 添加onClick防抖动监听
 */
fun <T : View> T.dOnClick(action: T.() -> Unit) {
    this.setOnClickListener(object : DebouncingOnClickListener() {
        override fun doClick(v: View?) {
            action(this@dOnClick)
        }
    })
}

/**
 * 添加onClick防抖动监听
 */
fun <T : List<F>, F : View> T.dOnClick(action: F.() -> Unit) {
    this.map {
        it.dOnClick(action = action)
    }
}

/**
 * 添加onClick防抖动监听,[action]为需要执行的代码
 *
 * 如果[filter]返回true,则执行注册的事件,否则,不执行
 */
fun <T : View> T.dOnClick(filter: () -> Boolean, action: T.() -> Unit) {
    this.setOnClickListener(object : DebouncingOnClickListener() {
        override fun doClick(v: View) {
            if (filter()) action(this@dOnClick)
        }
    })
}

/**
 * 添加onclick and longclick事件
 *
 * click:toast内容
 * longclick:复制内容,且 toast
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : TextView> T.setDefaultClickLongClick(toastClick: String, toastLongClick: String) {
    //点击
    dOnClick {
        if (text.toString().isNotBlank()) {
            toast(toastClick)
        }
    }

    //长按
    setOnLongClickListener {
        if (text.toString().isNotBlank()) {
            ActivityUtil.saveMsgToClipboard(BaseApplication.app, text.toString())
                    .kindAnyReturn({ "未知异常" }) { toastLongClick }.toastSelf()
        }
        true
    }
}

/**
 * 显示bigToast,可以返回自身
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.btoast(msg: String?): T {
    RxBus.instance?.post(BaseEvent(Const.SHOW_BIG_TOAST, msg ?: ""))
    return this
}

/**
 * 显示bigToast,可以返回自身
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.btoast(@StringRes msgRes: Int, vararg formatArgs: Any?): T {
    RxBus.instance?.post(BaseEvent(Const.SHOW_BIG_TOAST, getString(msgRes, *formatArgs)))
    return this
}

/**
 * 显示toast,可以返回自身
 */
@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> T.toast(msg: String?): T {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, msg ?: ""))
    return this
}

/**
 * 显示toast,可以返回自身
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.toast(@StringRes msgRes: Int, vararg formatArgs: Any?): T {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, BaseApplication.app.getString(msgRes, *formatArgs)))
    return this
}

/**
 * toast显示自身,仅限于string类型
 */
@Suppress("NOTHING_TO_INLINE")
inline fun String.toastSelf(): String {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, this))
    return this
}

/**
 * 显示toast,可以返回最后一行
 */
inline fun <T, R> R.let_toast(msg: String?, block: (R) -> T): T {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, msg ?: ""))
    return block(this)
}

/**
 * 显示toast,可以返回自身
 */
inline fun <R> R.also_toast(msg: String?, block: (R) -> Unit): R {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, msg ?: ""))
    block(this)
    return this
}

/**
 * 显示toast,可以返回最后一行
 */
inline fun <T, R> R.run_toast(msg: String?, block: R.() -> T): T {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, msg ?: ""))
    return block(this)
}

/**
 * 显示toast,可以返回自身
 */
inline fun <R> R.apply_toast(msg: String?, block: R.() -> Unit): R {
    RxBus.instance?.post(BaseEvent(Const.SHOW_TOAST, msg ?: ""))
    block(this)
    return this
}

/**
 * 专门重写Boolean方法,folse与ture时执行不同的lambda,返回相同的值
 */
inline fun <T> Boolean.kindAnyReturn(onFalse: () -> T, onTrue: () -> T): T {
    return if (this) onTrue() else onFalse()
}

/**
 * 专门重写Boolean方法,folse与ture时执行不同的lambda,返回不同的值
 */
inline fun Boolean.kindUnitReturn(onFalse: () -> Unit, onTrue: () -> Unit) {
    if (this) onTrue() else onFalse()
}

/**
 * 专门重写Boolean方法,true时执行
 */
inline fun Boolean.onTrue(block: () -> Unit): Boolean {
    if (this) {
        block()
    }
    return this
}

/**
 * 专门重写Boolean方法,false时执行
 */
inline fun Boolean.onFalse(block: () -> Unit): Boolean {
    if (!this) {
        block()
    }
    return this
}

/**
 * 空操作:
 *
 * 可不提示,
 * 可提示文字,
 * 可TODO显示将来需要采用的操作,
 * 可编写好不会执行代码
 *
 * 测试阶段toast
 * 正事环境无操作
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.empty(comment: String? = null, TODO: String? = null, unreachable: T.() -> Unit = {}): T {
    TODO?.let {
        if (BaseApplication.app.resources.getBoolean(R.bool.switch_open_todo)) {
            toast("待开发功能: $TODO")
        }
    }
    return this
}

/**
 * 多用于 when 条件控制
 */
inline infix fun <T : Any?> T.means(comment: String): T = empty(comment = comment)

/**
 * 将collection全部元素放某个默认值
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <F, T : MutableList<F>> T.initValue(value: F) {
    forEachIndexed { index, _ ->
        this[index] = value
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <F> Array<F>.initValue(value: F) {
    for (index in 0 until this.size) {
        this[index] = value
    }
}

/**
 * 只有filter通过,才能进行后面操作
 */
inline fun <T, F> T.filterRun(filter: T.() -> Boolean, block: T.() -> F): F? {
    return if (filter()) {
        block()
    } else null
}

/**
 * 只有filter通过,才能进行后面操作
 */
inline fun <T, F> T.filterLet(filter: (it: T) -> Boolean, block: (it: T) -> F): F? {
    return if (filter(this)) {
        block(this)
    } else null
}

/**
 * 只有filter通过,才能进行后面操作
 */
inline fun <T> T.filterAlso(filter: (it: T) -> Boolean, block: (it: T) -> Unit): T {
    if (filter(this)) {
        block(this)
    }

    return this
}

/**
 * 为Triple 添加方式(仿照Pair)
 */
infix fun <A, B, C> Pair<A, B>.too(that: C): Triple<A, B, C> = Triple(this.first, this.second, that)

/**
 * 为Quadra 添加方式(仿照Pair)
 */
infix fun <A, B, C, D> Triple<A, B, C>.tooo(that: D): Quadra<A, B, C, D> = Quadra(this.first, this.second, this.third, that)

/**
 * 生成LocalWalletModule对象
 * currency ->  balance -> gateway
 */
infix fun Triple<String, String, String>.toWalletIntent(img: Serializable?): WalletIntentBean {
    return when (img) {
        null -> WalletIntentBean(currency = this.first, balance = this.second, gateway = if (this.third.isBlank()) null else this.third)
        is Int -> WalletIntentBean(currency = this.first, balance = this.second, gateway = if (this.third.isBlank()) null else this.third, imgResource = img)
        is String -> WalletIntentBean(currency = this.first, balance = this.second, gateway = if (this.third.isBlank()) null else this.third, imgUrl = img)
        else -> WalletIntentBean(currency = this.first, balance = this.second, gateway = if (this.third.isBlank()) null else this.third)
    }
}

/**
 * 重寫空或空串
 */
infix fun <T : String?> T.ifNullOrEmpty(str: String?): String? {
    return if (this.isNullOrEmpty()) str else this
}

/**
 * 为Quadra 添加方式(仿照Pair)
 */
fun <A, B, C, D> Quadra<A, B, C, D>.subEnd(): Triple<A, B, C> = Triple(this.first, this.second, this.third)

/**
 * 判断String是否为json格式
 */
@Suppress("NOTHING_TO_INLINE")
inline fun String.isJsonFormat(): Boolean {
    return try {
        JSONObject(this)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * 获取string
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.getString(@StringRes res: Int, vararg formatArgs: Any?): String {
    return BaseApplication.app.getString(res, *formatArgs)
}

/**
 * 获取string
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.getStringArray(@ArrayRes res: Int): Array<String> {
    return BaseApplication.app.resources.getStringArray(res)
}

/**
 * 获取 网络请求时需要传入的 locale信息
 */
inline fun <T> T.getLocaleForRequest(config: Configuration? = null): String {
    return getApplicationLocale().let { it ->
        when {
            it.language == Locale.ENGLISH.language -> "en"
            it.language == Locale.CHINA.language && it.country == Locale.SIMPLIFIED_CHINESE.country -> "zh_CN"
            it.language == Locale.CHINA.language && it.country == Locale.TRADITIONAL_CHINESE.country -> "zh_HK"
            it.language == Locale.CHINA.language -> "zh_HK"
            else -> "zh_CN"
        }
    }

}

/**
 * 获取 网络请求时需要传入的 locale信息
 */
inline fun <T> T.getLocaleInt(config: Configuration? = null): Int {
    return getApplicationLocale().let { it ->
        when {
            it.language == Locale.ENGLISH.language -> 3
            it.language == Locale.CHINA.language && it.country == Locale.SIMPLIFIED_CHINESE.country -> 1
            it.language == Locale.CHINA.language -> 2
            else -> 1
        }
    }

}

/**
 * 获取颜色值
 */
fun dispatchGetSkinColor(@ColorRes resId: Int): Int {
    return SkinCompatResources.getColor(BaseApplication.app, resId)
}

/**
 * 获取颜色值
 */
fun dispatchGetSkinDrawable(@DrawableRes resId: Int): Drawable {
    return SkinCompatResources.getDrawable(BaseApplication.app, resId)
}

/**
 * 获取颜色值
 */
fun dispatchGetColor(@ColorRes resId: Int): Int {
    return if (Build.VERSION.SDK_INT < 23) {
        BaseApplication.app.resources.getColor(resId)
    } else {
        SkinCompatResources.getColor(BaseApplication.app, resId)
        BaseApplication.app.resources.getColor(resId, null)
    }
}

/**
 * 获取尺寸
 */
inline fun dispatchGetDimen(@DimenRes resId: Int): Int {
    return BaseApplication.app.resources.getDimensionPixelSize(resId)
}

/**
 * get Application Locale
 *
 * 如果传入config则获取当前config的locale值,如果未传入,则默认返回当前应用的locale(非系统)
 */
inline fun <T> T.getApplicationLocale(config: Configuration? = null): Locale {
    return (config ?: BaseApplication.app.resources.configuration).run {
        if (Build.VERSION.SDK_INT < 24) locale else locales.get(0)
    }
}

/**
 * 可以在跳转成功后执行一个方法体
 */
inline fun Postcard.navigationWithArrivalRun(context: Context? = null, always: Boolean = false, crossinline onSuccess: () -> Unit) {
    this.navigation(context, object : NavigationCallback {
        /**
         * Callback after lose your way.
         *
         * @param postcard meta
         */
        override fun onLost(postcard: Postcard?) {
            if (always) onSuccess()
        }

        /**
         * Callback when find the destination.
         *
         * @param postcard meta
         */
        override fun onFound(postcard: Postcard?) {
        }

        /**
         * Callback on interrupt.
         *
         * @param postcard meta
         */
        override fun onInterrupt(postcard: Postcard?) {
            if (always) onSuccess()
        }

        /**
         * Callback after navigationWithArrivalRun.
         *
         * @param postcard meta
         */
        override fun onArrival(postcard: Postcard?) {
            onSuccess()
        }
    })
}

/**
 * 简单的路由跳转
 */
inline infix fun <T> T.route(path: String): Any? {
    return routeCustom(path).navigation()
}

/**
 * 简单的路由跳转,成功后结束自身
 */
inline infix fun <T : Activity> T.routeSuccessFinish(path: String) {
    return ARouter.getInstance().build(path).navigationWithArrivalRun {
        finish()
    }
}

/**
 * 重写navigation方法,来规范运算符优先级
 */
inline infix fun <T : Postcard> T.nav(act: Context?): Any? = this.navigation(act)

/**
 * 自定义路由跳转,只提供Arouter.getInstance.build()的简写,不进行具体逻辑
 */
inline infix fun <T> T.routeCustom(path: String): Postcard = ARouter.getInstance().build(path)

/**
 * 简单的路由跳转,默认参数
 */
inline infix fun <T : Postcard> T.param(any: Any?): Postcard = setParam(Const.KEY_COMMON_VALUE, any)

/**
 * 简单的路由跳转:参数1
 */
inline infix fun <T : Postcard> T.firstParam(any: Any?): Postcard = setParam(Const.KEY_COMMON_VALUE_ONE, any)

/**
 * 简单的路由跳转:参数2
 */
inline infix fun <T : Postcard> T.secondParam(any: Any?): Postcard = setParam(Const.KEY_COMMON_VALUE_TWO, any)

/**
 * 简单的路由跳转:参数3
 */
inline infix fun <T : Postcard> T.thirdParam(any: Any?): Postcard = setParam(Const.KEY_COMMON_VALUE_THREE, any)

/**
 * 简单的路由跳转:参数4
 */
inline infix fun <T : Postcard> T.fourthParam(any: Any?): Postcard = setParam(Const.KEY_COMMON_VALUE_FOUR, any)

/**
 * 简单的路由跳转:参数5
 */
inline infix fun <T : Postcard> T.fifthParam(any: Any?): Postcard = setParam(Const.KEY_COMMON_VALUE_FIVE, any)

/**
 * 简单的路由跳转:设置参数
 */
inline fun <T : Postcard> T.setParam(key: String, any: Any?): Postcard =
        when (any) {
            is Int, is Short, is Long, is Float, is Double, is Boolean, is Byte, is Char, is String -> withSerializable(key, any as Serializable)
            else -> withObject(key, any)
        }

/**
 * 携带toolbar的跳转
 */
inline infix fun <T : Postcard> T.transitionToolbar(context: BaseActivity<*>): Postcard =
        this.withOptionsCompat(ActivityOptionsCompat.makeSceneTransitionAnimation(context, android.support.v4.util.Pair(context.toolbar, getString(R.string.transition_name_toolbar))
        ))

/**
 * String是否匹配某个字符串(reg)
 */
inline infix fun <T : String> T.matches(regStr: String) = matches(regStr.toRegex())

/**
 * String是否不匹配某个字符串(reg)
 */
inline infix fun <T : String> T.notMatch(regStr: String) = !matches(regStr.toRegex())

/**
 * md5
 */
inline fun <T : String?> T.toMD5() = encode(this, MD5)

/**
 * double - > string
 */
inline infix fun String?.toScaleString(scale: Int) = this?.toBigDecimalOrNull()?.toScaleString(scale)

/**
 * double - > string
 */
inline infix fun <T : Double> T.toScaleString(scale: Int) = toBigDecimal().setScale(scale, BigDecimal.ROUND_HALF_DOWN).toPlainString()

/**
 * decimal - > string
 */
inline infix fun <T : BigDecimal> T.toScaleString(scale: Int) = setScale(scale, BigDecimal.ROUND_HALF_DOWN).toPlainString()

/**
 * stockInfo -> clear ->add-all
 */
inline infix fun <F, T : MutableCollection<F>> T.clearAddAll(datas: Collection<F>) = this.also {
    it.clear()
    it.addAll(datas)
}

/**
 * 设置 TextView 字符串
 */
inline infix fun <T : EditText> T.textStr(value: String?) {
    setText(value)
}

/**
 * 委托模式
 */
inline fun <F : Serializable, T : LifecycleOwner> T.viewBind(@IdRes id: Int, default: Serializable? = null): ViewDelegate<F> = ViewDelegate(this, id, default)

/**
 * 委托模式
 */
inline fun <F : Serializable?, T : LifecycleOwner> T.viewBindNull(@IdRes id: Int): ViewDelegateWithNull<F> = ViewDelegateWithNull(this, id)

/**
 * 委托模式
 */
inline fun <F : Any?, T : LifecycleOwner> T.setBindNullable(noinline callback: SetMethodBindNullable<F>.(F?) -> Unit): SetMethodBindNullable<F> = SetMethodBindNullable(callback)

/**
 * 委托模式
 */
inline fun <F : Any, T : LifecycleOwner> T.setBindNotNull(noinline callback: (F) -> Unit): SetMethodBindNotNull<F> = SetMethodBindNotNull(callback)

/**
 * double -> string 不使用科学计数法
 */
inline fun <T : Double?> T.toStringUnGrouping(): String? {
    if (this == null) {
        return null
    } else {
        return this.toBigDecimal().stripTrailingZeros().toPlainString()
    }
}

/**
 * edit-text 设置文本输入监听
 */
inline fun <T : TextView> T.valueWatcher(checkFocus: Boolean = true, crossinline block: T.() -> Unit) {
    addTextChangedListener(object : TextWatcherWithView<T>(this@valueWatcher) {
        override fun afterTextChanged(s: Editable?) {
            if (checkFocus && target.isFocused) {
                block(this@valueWatcher)
            } else if (!checkFocus) {
                block(this@valueWatcher)
            }
        }
    })
}

/**
 * 输入小数位限制
 */
inline fun <T : EditText> T.addInputFilterDigits(digits: Int) {
    this.filters = this.filters.plus(Utils.createDoubleInputFilter(digits))
}

/**
 * 过滤表情符号
 */
inline fun <T : EditText> T.addFaceFilter() {
    this.filters = this.filters.plus(Utils.setFilter())
}

/**
 * 添加或替换元素值
 *
 * 如果不能添加或者设置到指定的元素位置,则抛出异常
 */
@Throws(IndexOutOfBoundsException::class)
@Suppress("NOTHING_TO_INLINE")
inline fun <F, T : MutableList<F>> T.setOrAdd(index: Int, element: F) {
    when {
        size == index -> add(index, element)
        size > index -> set(index, element)
        else -> throw IndexOutOfBoundsException("超过最大长度至少2")
    }
}

/**
 * 确定/取消 弹出窗口(自动关闭弹窗)
 */
inline fun <T : LifecycleOwner> T.cancelConfirmDialog(@StringRes titleRes: Int, @StringRes cancelRes: Int = R.string.function_cancel, @StringRes confirmRes: Int = R.string.function_confirm, noinline listener: () -> Unit) {
    cancelConfirmDialog(getString(titleRes), getString(cancelRes), getString(confirmRes)) {
        listener()
        false
    }
}

/**
 * 确定/取消 弹出窗口
 */
inline fun <T : LifecycleOwner> T.cancelConfirmDialog(title: String, cancel: String, confirm: String, noinline listener: (Dialog) -> Boolean) {
    CancelConfirmDialogFragment()
            .setTitle(title)
            .setCancelText(cancel)
            .setConfirmText(confirm)
            .setOnOperateListener(listener)
            .show(when (this) {
                is BaseActivity<*> -> this.sfManager
                is BaseFragment<*> -> this.sfManager
                is View -> (this as BaseActivity<*>).sfManager
                else -> TODO()
            }, "$this")
}

/**
 * glide添加事件,当自己处理返回的drawable时使用
 */
inline fun <T> RequestBuilder<T>.glideListener(crossinline onFinish: (T?) -> Unit): RequestBuilder<T> {
    return this.listener(object : RequestListener<T> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
            return false
        }

        override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onFinish(resource)
            return false
        }
    })
}

/**
 * LineMenuView设置点击事件
 */
inline fun <T : LineMenuView> T.onPerformSelf(crossinline callback: (LineMenuView) -> Unit) {
    this.setOnClickListener(object : LineMenuListener {
        override fun performSelf(lmv: LineMenuView) {
            callback(lmv)
        }
    })
}

/**
 * LineMenuView设置点击事件
 */
inline fun <T : LineMenuView> T.onPerformLeft(crossinline callback: (TextView) -> Unit) {
    this.setOnClickListener(object : LineMenuListener {
        override fun performClickLeft(tv: TextView): Boolean {
            callback(tv)
            return true
        }
    })
}

/**
 * LineMenuView设置点击事件
 */
inline fun <T : LineMenuView> T.onPerformRight(crossinline callback: (View) -> Unit) {
    this.setOnClickListener(object : LineMenuListener {
        override fun performClickRight(v: View): Boolean {
            callback(v)
            return true
        }
    })
}

inline fun <T : View> T.postV(crossinline callback: T.() -> Unit) {
    this.post {
        callback()
    }
}

/**
 * 创建自定义布局
 */
inline fun <T> T.customShapeDrawable(radius: Float, @ColorInt color: Int): ShapeDrawable {
    return object : ShapeDrawable(RoundRectShape(floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null)) {
        init {
            paint.color = color
        }
    }
}

/**
 * 创建GradientDrawable,即xml最后会生成的Drawable
 */
inline fun <T> T.customGradientDrawable(outRadius: Int, borderWidth: Int, @ColorInt fillColor: Int, @ColorInt borderColor: Int): GradientDrawable {
    return GradientDrawable().apply {
        setColor(fillColor)
        cornerRadius = outRadius.toFloat()
        setStroke(borderWidth, borderColor)
    }
}

/**
 * 生成 SimulateDialogInterface
 */
inline fun <T, V : View, L : FrameLayout.LayoutParams> T.generateDefaultDialog(parent: IncludeDialogViewGroup, @LayoutRes layoutRes: Int): DefaultSimulateDialogImpl<V, L> {
    return DefaultSimulateDialogImpl(parent, layoutRes)
}

/**
 * 生成 SimulateDialogInterface
 */
inline fun <T> T.generateSimpleDefaultDialog(parent: IncludeDialogViewGroup, @LayoutRes layoutRes: Int): DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams> {
    return DefaultSimulateDialogImpl(parent, layoutRes)
}

/**
 * 从view中获取Activity对象
 */
inline fun <T : View> T.getActivityFromView(): BaseActivity<*> {
    var activity = this.context
    while (activity is ContextWrapper) {
        if (activity is BaseActivity<*>) {
            return activity
        }
        activity = activity.baseContext
    }
    TODO("只能使用Activity来创建View")
}

/**
 * 获取指纹库摘要
 */
@SuppressLint("CheckResult")
inline fun getCurrentFingerprintHash(crossinline onSuccess: (String?) -> Unit) {
    Observable.just(true)
            .map {
                val clazz = FingerprintManager::class.java
                val method = clazz.getDeclaredMethod("getEnrolledFingerprints")
                val result = method.invoke(BaseApplication.app.fingerprintManager) // List<Fingerprint>
                val clazz_fingerprint = Class.forName("android.hardware.fingerprint.Fingerprint")
                //val getName = clazz_fingerprint.getDeclaredMethod("getName")
                val getFingerId = clazz_fingerprint.getDeclaredMethod("getFingerId")
                val builder = StringBuilder()
                if (result is List<*>) {
                    result.forEach {
                        builder.append(getFingerId.invoke(it))
                    }
                }
                builder.toString().toMD5()
            }
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // 指纹库摘要尝试保存
                onSuccess(it)
            }) {
                // 无法验证指纹库
            }
}

/**
 * 股票精度(3为有效小数)-> 可使用的字符串
 *
 * [isExponent] 是否为指数数据(指数数据四位精度)
 * [scale] 保留小数位
 */
inline fun Long.stockGetValue(scale: Int? = null, isExponent: Boolean = false): String {
    return (this / (if (isExponent) 10000.0 else 1000.0)).toBigDecimal().toScaleString(when {
        scale != null -> scale
        isExponent -> Const.NORMAL_DECIMAL_FLOAT_SCALE_2
        !isExponent -> Const.NORMAL_DECIMAL_FLOAT_SCALE_3
        else -> TODO()
    })
}

/**
 * 股票精度(3为有效小数)-> 可使用的字符串
 *
 * [isExponent] 是否为指数数据(指数数据四位精度)
 * [scale] 保留小数位
 */
inline fun Long.stockGetValueNullable(scale: Int? = null, isExponent: Boolean = false): String? {
    return if (this == 0L) null else (this / (if (isExponent) 10000.0 else 1000.0)).toBigDecimal().toScaleString(when {
        scale != null -> scale
        isExponent -> Const.NORMAL_DECIMAL_FLOAT_SCALE_2
        !isExponent -> Const.NORMAL_DECIMAL_FLOAT_SCALE_3
        else -> TODO()
    })
}

/**
 * 股票精度(3为有效小数)-> 可使用的字符串(百分比值)
 *
 * [scale] 保留小数位
 */
inline fun Double.stockGetPercent(scale: Int = Const.NORMAL_DECIMAL_FLOAT_SCALE_2): String {
    return "${toBigDecimal().toScaleString(scale)}%"
}

/**
 * 可能超过万/亿的话需要显示特殊内容
 */
inline fun Long.stockGetBigNumber(isExponent: Boolean = false, hasScale: Boolean = true): String {
    return (this / (if (hasScale) (if (isExponent) 10000.0 else 1000.0) else 1.0)).toBigDecimal().let {
        when {
            it > 1_0000_0000.toBigDecimal() -> "${(it.divide(BigDecimal(1_0000_0000), Const.NORMAL_DECIMAL_FLOAT_SCALE_2, BigDecimal.ROUND_HALF_DOWN)).toScaleString(Const.NORMAL_DECIMAL_FLOAT_SCALE_2)}亿"
            it > 1_0000.toBigDecimal() -> "${(it.divide(BigDecimal(1_0000), Const.NORMAL_DECIMAL_FLOAT_SCALE_2, BigDecimal.ROUND_HALF_DOWN)).toScaleString(Const.NORMAL_DECIMAL_FLOAT_SCALE_2)}万"
            else -> if (hasScale) it.toScaleString(Const.NORMAL_DECIMAL_FLOAT_SCALE_2) else it.toScaleString(0)
        }
    }
}

/**
 * 可能超过万/亿的话需要显示特殊内容 (可为null)
 */
inline fun Long.stockGetBigNumberNullable(isExponent: Boolean = false, hasScale: Boolean = true): String? {
    return if (this == 0L) null else this.stockGetBigNumber(isExponent, hasScale)
}

/**
 * 可能超过万/亿的话需要显示特殊内容
 */
inline fun Long.buySellGetBigNumber(): String {
    return toBigDecimal().let {
        when {
            it > 1_000_000.toBigDecimal() -> "${(it.divide(BigDecimal(1_000_000), Const.NORMAL_DECIMAL_FLOAT_SCALE_2, BigDecimal.ROUND_HALF_DOWN)).toScaleString(Const.NORMAL_DECIMAL_FLOAT_SCALE_2)}M"
            it > 1_000.toBigDecimal() -> "${(it.divide(BigDecimal(1_000), Const.NORMAL_DECIMAL_FLOAT_SCALE_2, BigDecimal.ROUND_HALF_DOWN)).toScaleString(Const.NORMAL_DECIMAL_FLOAT_SCALE_2)}K"
            else -> it.toString()
        }
    }
}


/**
 * long -> 日期
 */
inline fun Long.longToTimeString(format: String = "yyyy-MM-dd"): String? {
    return Date().let {
        it.time = this
        try {
            SimpleDateFormat(format, Locale.CHINA).format(it)
        } catch (e: Exception) {
            null
        }
    }
}

@SuppressLint("SimpleDateFormat")
inline fun getMonthDate(str: String?): String {
    val sdf1 = SimpleDateFormat("MM/dd")
    val sdf2 = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    return try {
        sdf1.format(sdf2.parse(str))
    } catch (e: Exception) {
        ""
    }
}

/**
 * String -> Date
 */
inline fun String.stockToDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    return try {
        SimpleDateFormat(format, Locale.CHINA).parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 忽略秒情况下,两者时间是否相同
 */
inline infix fun <T : Date?> T.equalsIgnoreSecond(newTime: T): Boolean {
    if (this == null || newTime == null) {
        return false
    }

    val c = Calendar.getInstance()

    // 旧时间
    c.timeInMillis = this.time
    c.set(Calendar.SECOND, 0)
    val old = c.timeInMillis

    // 终止时间
    c.timeInMillis = newTime.time
    c.set(Calendar.SECOND, 0)
    val new = c.timeInMillis

    return old == new
}

/**
 * 格式化数字为千分位显示；
 * @param 要格式化的数字；
 * @return
 */
inline fun fmtMicrometer(text: String): String {
    var df: DecimalFormat? = null
    if (text.indexOf(".") > 0) {
        if (text.length - text.indexOf(".") - 1 == 0) {
            df = DecimalFormat("###,##0.")
        } else if (text.length - text.indexOf(".") - 1 == 1) {
            df = DecimalFormat("###,##0.00")
        } else {
            df = DecimalFormat("###,##0.00")
        }
    } else {
        df = DecimalFormat("###,##0")
    }
    var number = 0.0
    try {
        number = text.toScaleString(2)?.toDouble() ?: 0.00
    } catch (e: Exception) {
        number = 0.0
    }

    return df.format(number)
}

fun getCurrency(currency: String?): String {
    return when (currency) {
        "HKD" -> "港元"
        "USD" -> "美元"
        "RMB" -> "人民币"
        else -> "港币"
    }
}

fun getUnit(unit: String?): String {
    return when (unit) {
        "000" -> "千"
        "Mn" -> "百万"
        "Bn" -> "十亿"
        else -> ""
    }
}

fun fomate(double: Double): String {
    val bigDecimal = BigDecimal(double)
    return bigDecimal.toScaleString(2)
}

/**
 * 生成通用样式:title + cancel + confirm 的 dialog(模拟),可以在activity中显示出来
 */
inline fun IncludeDialogViewGroup.createCancelConfirmTMDialog(title: String? = null, cancel: String? = getString(R.string.function_cancel), confirm: String? = getString(R.string.function_sure), crossinline onConfirm: () -> Boolean): DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams> {
    return generateSimpleDefaultDialog(this, R.layout.layout_dialog_title_cancel_confirm).also { dialog ->
        dialog.generateView().also {
            it.tv_dialog_title.text = title
            it.tv_dialog_cancel.text = cancel
            it.tv_dialog_confirm.text = confirm

            // cancel
            it.tv_dialog_cancel.dOnClick {
                dialog.close(true)
            }

            // confirm
            it.tv_dialog_confirm.dOnClick {
                if (!onConfirm()) {
                    dialog.close(true)
                }
            }
        }
    }
}

/**
 * 生成通用样式:title + cancel + confirm 的 dialog(模拟),可以在activity中显示出来
 *
 * for establish
 */
inline fun IncludeDialogViewGroup.createCCTMDForEstablish(): DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams> {
    return createCancelConfirmTMDialog(title = "確定要退出開戶流程嗎") {
        routeCustom(ARouterConst.Activity_FinancingActivity)
                .param(2)
                .navigation()
        false
    }
}

/**
 * 如果字符串为null,则返回默认的一个字符串
 */
inline fun <T : String?> T.ifNullReturn(defaultValue: String = ""): String {
    return (this  as? String) ?: defaultValue
}

/**
 * 针对有些可能重写 equal 的集合,只通过判断 引用 来获取位置
 */
inline fun <T : List<F?>, F> T.indexOfOnlyRefrence(obj: F?): Int {
    if (this.size == 0) {
        return -1;
    }

    if (obj == null) {
        return this.indexOf(obj)
    }

    this.forEachIndexed { index, f ->
        if (f === obj) {
            return index
        }
    }

    return -1;
}