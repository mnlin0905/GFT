package com.linktech.gft.view

import android.content.Context
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.linktech.gft.R
import com.linktech.gft.base.BaseApplication

/**
 * Created on 2018/3/27
 * function : 标题居中的toolbar
 *
 * @author LinkTech
 */
open class TitleCenterToolbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.support.v7.appcompat.R.attr.toolbarStyle) : Toolbar(context, attrs, defStyleAttr) {
    var mTvTitle: TextView? = null

    /**
     * 延迟执行的部分
     */
    var delayRunable :Runnable? = null

    init {
        View.inflate(context, R.layout.layout_title_center_toolbar, this)
        mTvTitle = findViewById(R.id.tv_title)
    }

    /**
     * Returns the title of this toolbar.
     *
     * @return The current title.
     */
    override fun getTitle(): CharSequence? {
        return if (TextUtils.isEmpty(mTvTitle?.text)) null else mTvTitle?.text
    }

    /**
     * Set the title of this toolbar.
     *
     *
     *
     * A title should be used as the anchor for a section of content. It should
     * describe or name the content being viewed.
     *
     * @param resId Resource ID of a string to set as the title
     */
    override fun setTitle(resId: Int) {
        mTvTitle?.setText(resId)

        //如果title为空,则让title部分不可见,(否则由于TextView有padding存在,会占用一定布局)
        mTvTitle?.visibility = if (mTvTitle?.text?.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Set the title of this toolbar.
     *
     *
     *
     * A title should be used as the anchor for a section of content. It should
     * describe or name the content being viewed.
     *
     * @param title Title to set
     */
    override fun setTitle(title: CharSequence?) {
        mTvTitle?.text = title

        //如果title为空,则让title部分不可见,(否则由于TextView有padding存在,会占用一定布局)
        mTvTitle?.visibility = if (mTvTitle?.text?.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color
     * from the specified TextAppearance resource.
     *
     * @param context
     * @param resId
     */
    override fun setTitleTextAppearance(context: Context, resId: Int) {
        mTvTitle?.setTextAppearance(context, resId)
    }

    /**
     * Sets the text color of the title, if present.
     *
     * @param color The new text color in 0xAARRGGBB format
     */
    override fun setTitleTextColor(color: Int) {
        //需要延时则创建并post
        if(mTvTitle==null){
            delayRunable = Runnable { delayRun(color) }
            post(delayRunable)
        }else{
            //移除延时
            delayRunable?.let {
                removeCallbacks(delayRunable)
            }

            delayRun(color)
        }
    }

    /**
     * 延迟执行toolbar 颜色修改
     */
    private fun delayRun(color : Int){
        mTvTitle?.setTextColor(color)
    }

    /**
     * 设置为透明主题
     */
    fun switchTheme(transparent: Boolean) {
        if (transparent) {
            setNavigationIcon(R.drawable.ic_close_white_24dp)
            overflowIcon = BaseApplication.app.dispatchGetDrawable(R.drawable.ic_more_vert_white_24dp)
            mTvTitle?.setTextColor(BaseApplication.app.dispatchGetColor(R.color.main_color_white))
            setBackgroundColor(BaseApplication.app.dispatchGetColor(R.color.transparent))
        }
    }
}
