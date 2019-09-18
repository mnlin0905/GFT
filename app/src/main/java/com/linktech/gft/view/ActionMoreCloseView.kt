package com.linktech.gft.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.interfaces.SimulateDialogInterface
import com.knowledge.mnlin.sdialog.widgets.IncludeDialogViewGroup
import com.linktech.gft.R
import com.linktech.gft.plugins.*
import kotlinx.android.synthetic.main.dark_dialog_action_more_close.view.*
import kotlinx.android.synthetic.main.layout_action_more_close.view.*
import org.jetbrains.anko.backgroundDrawable

/**
 * Created on 2019/3/22  14:55
 * function : toolbar 右側快捷功能按鈕
 *
 * @author mnlin
 */
open class ActionMoreCloseView : LinearLayout {
    /**
     * dialog佈局
     */
    lateinit var commonJumpDialog: SimulateDialogInterface<LinearLayout, FrameLayout.LayoutParams>

    /**
     * 獲取佈局系統中的 IncludeDialogViewGroup
     */
    lateinit var dialogContainer: IncludeDialogViewGroup

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        //設置自身參數
        gravity = Gravity.CENTER_VERTICAL

        //加載佈局
        LayoutInflater.from(context).inflate(getLayoutId(), this, true)

        //事件處理,背景處理
        setBackground()

        iv_action_close.dOnClick {
            route(ARouterConst.Activity_MainActivity).empty(comment = "直接前往首頁")
        }
    }

    open fun getLayoutId(): Int {
        return R.layout.layout_action_more_close_black
    }

    open fun setBackground() {
        post {
            backgroundDrawable = customGradientDrawable(height, dispatchGetDimen(R.dimen.divider_line_width_1dp), dispatchGetColor(R.color.dark_color_1d2037_f4f4f4), dispatchGetColor(R.color.dark_divider_454860))
        }
    }

    /**
     * 必須在父佈局都處理完 onFinishInflate 回調後,才能去獲取 IncludeDialogViewGroup
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        //先讓父佈局執行完回調方法
        post {
            dialogContainer = getActivityFromView().includeDialog.let {
                it.empty(comment = "如果發現沒有找到佈局,則停止事件綁定等操作") ?: return@post
            }
            commonJumpDialog = generateDefaultDialog(dialogContainer, R.layout.dark_dialog_action_more_close)

            //當有彈出框時,點擊更多才會彈窗
            iv_action_more.dOnClick {
                dialogContainer.showDialogs(instance = commonJumpDialog, animator = AlphaIDVGAnimatorImpl()).empty(comment = "更多功能")
            }

            //綁定佈局:關閉:其他功能
            commonJumpDialog.generateView().run {
                //無動畫關閉dialog
                val closeDialog = {
                    dialogContainer.closeDialogsOpened(instance = commonJumpDialog, useAnimator = false).empty(comment = "關閉")
                }

                iv_common_lose.dOnClick {
                    dialogContainer.closeDialogsOpened(instance = commonJumpDialog)
                }

                tv_common_news.dOnClick {
                    closeDialog().route(ARouterConst.Activity_NewsActivity).empty(comment = "消息")
                }

                tv_common_search.dOnClick {
                    closeDialog().toast(R.string.common_not_develop).empty(TODO = "搜索")
                }

                tv_common_custom.dOnClick {
                    closeDialog().route(ARouterConst.Activity_FeedbackActivity).empty(comment = "客服")
                }

                tv_common_exchange.dOnClick {
                    closeDialog().route(ARouterConst.Activity_FinancingActivity).empty(comment = "首頁")
                }

                tv_common_asset.dOnClick {
                    closeDialog().routeCustom(ARouterConst.Activity_FinancingActivity)
                            .param(2)
                            .navigation()
                            .empty(TODO = "資產")
                }

                tv_common_ecology.dOnClick {
                    closeDialog().route(ARouterConst.Activity_MainActivity).empty(comment = "生態/益享")
                }
            }
        }
    }
}
