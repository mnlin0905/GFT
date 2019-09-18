package com.linktech.gft.activity.other

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import com.linktech.gft.R
import com.linktech.gft.base.BaseView
import com.linktech.gft.plugins.*
import com.linktech.gft.util.ActivityUtil
import com.linktech.gft.util.DefaultPreferenceUtil
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

/**
 * function : 引导界面
 *
 * Created on 2018/8/17  17:01
 * @author mnlin
 */
class SplashActivity : AppCompatActivity(), BaseView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //如果为桌面点击进入,且此时有其他的Activity,则将启动过程终止
        if (!this.isTaskRoot && intent != null && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == intent.action) {
            finish()
            return
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ActivityUtil.setDecorTransparent(this)
        setContentView(R.layout.activity_splash)

        /**
         * 跳转
         */
        val jump = {
            routeSuccessFinish(ARouterConst.Activity_FinancingActivity)
        }
        //直接进去
        jump()
        return

        /**
         * 之后进入,跳过该 activity
         */
        if (!DefaultPreferenceUtil.getInstance().isFirstInstall) {
            jump()
            return
        }

        //引导页初始化
        vp_splash.adapter = ViewAdapter(listOf(
                //第1界面
                FrameLayout(this).also {
                    it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                    //ImageView
                    it.addView(
                            ImageView(this).also {
                                it.setImageResource(R.drawable.layer_splash_bg)
                                it.scaleType = ImageView.ScaleType.CENTER_CROP
                            }, 0, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

                    //button
                    it.addView(
                            Button(this, null, 0).apply {
                                text = getString(R.string.activity_splash_go)
                                textColor = dispatchGetColor(R.color.main_color_white)
                                minHeight = 0
                                minWidth = 0
                                setTextSize(TypedValue.COMPLEX_UNIT_PX, dispatchGetDimen(R.dimen.text_size_16sp).toFloat())
                                setPadding(dispatchGetDimen(R.dimen.view_padding_margin_12dp),
                                        dispatchGetDimen(R.dimen.view_padding_margin_6dp),
                                        dispatchGetDimen(R.dimen.view_padding_margin_12dp),
                                        dispatchGetDimen(R.dimen.view_padding_margin_6dp))

                                //背景
                                post {
                                    backgroundResource = R.drawable.shape_border_5f4b8b_radius_6dp
                                }

                                //点击事件
                                dOnClick {
                                    DefaultPreferenceUtil.getInstance().isFirstInstall = false
                                    jump()
                                }
                            }, 1,
                            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                                bottomMargin = dispatchGetDimen(R.dimen.view_padding_margin_200dp)
                            })
                }

        ))
    }

    override fun onResume() {
        super.onResume()

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN).empty(comment = "去除全屏效果(华为有navigation栏,这里需要取消,来保证不会出现splashTheme的背景)")
        window.navigationBarColor = dispatchGetColor(R.color.main_color_white)
    }
}

/**
 * function : 适配器
 *
 * Created on 2018/8/17  17:02
 * @author mnlin
 */
class ViewAdapter(private val datas: List<View>) : PagerAdapter() {
    /**
     * 页面数量
     */
    private var count: Int = datas.size

    override fun getCount(): Int {
        return count
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = datas[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(datas[position])
    }
}
