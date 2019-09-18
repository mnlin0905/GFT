package com.linktech.gft.plugins

import android.support.annotation.*
import java.lang.annotation.Inherited

/**************************************
 * function : 注解处理器:方便开发使用,使用 注解 完成文件的注册任务
 *
 * Created on 2018/6/30  18:37
 * @author mnlin
 **************************************/

/**
 * function : 自动注册menu菜单
 *
 * Created on 2018/6/30  18:34
 * @author mnlin
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InjectMenuRes(@MenuRes val menuResId: Int)

/**
 * function : 自动插入layout资源文件名,使用注解,减少代码量
 *
 * Created on 2018/6/30  17:29
 * @author mnlin
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InjectLayoutRes(@LayoutRes val layoutResId: Int)

/**
 * function : 标题内容绑定
 *
 * titleRes指定时,title值无效
 *
 * Created on 2018/8/6  15:22
 * @author mnlin
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InjectActivityTitle(@StringRes val titleRes: Int = 0, val title: String = "")

/**
 * function : 资源文件说明
 *
 * Created on 2018/8/6  15:22
 * @author mnlin
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MyResources(@LayoutRes @MenuRes @IdRes @StringRes @DimenRes val resId: Int)

/**
 * function : 禁止某些初始化生成类操作
 *
 * Created on 2018/7/6  17:54
 * @author mnlin
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DisableAPTProcess(vararg val disables: APTPlugins)

/**
 * function : activity/fragment自带的框架加载器
 *
 * Created on 2018/7/6  17:55
 * @author mnlin
 */
enum class APTPlugins(val value: Int) {
    AROUTER(0x00000001), DAGGER(0x00000002), BUTTERKNIF(0x00000004)
}

