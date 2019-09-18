package com.linktech.gft.activity.financing.establish


import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.IntentUtils
import com.knowledge.mnlin.sdialog.animators.AlphaIDVGAnimatorImpl
import com.knowledge.mnlin.sdialog.utils.DefaultSimulateDialogImpl
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.bean.EstablishStatusBean
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.ws.WSManager.Companion.handler
import kotlinx.android.synthetic.main.activity_risk_disclosure.*
import kotlinx.android.synthetic.main.layout_common_dark_function_button.*

/**
 * function : 風險披露
 *
 * 風險披露 第 11 步
 *
 * Created on 2019/7/15  10:35
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_RiskDisclosureActivity, extras = ARouterConst.FLAG_LOGIN or ARouterConst.FLAG_ACTIVITY_CLEAR_TOP)
@InjectActivityTitle(title = "風險披露")
@InjectLayoutRes(layoutResId = R.layout.activity_risk_disclosure)
@InjectMenuRes(menuResId = R.menu.menu_text_cancel)
class RiskDisclosureActivity : BaseActivity<BasePresenter<RiskDisclosureActivity>>() {
    /**
     * 开户状态信息
     */
    @Autowired(name = Const.KEY_COMMON_VALUE, required = false)
    @JvmField
    var establishStatusBean: EstablishStatusBean? = null

    private var phone: String by viewBind(R.id.tv_phone)

    /**
     * singleDialog
     */
    private lateinit var singleDialog: DefaultSimulateDialogImpl<ViewGroup, FrameLayout.LayoutParams>

    /**
     * 媒体播放控制器
     */
    private lateinit var mediaControl: MediaPlayer

    /**
     * runable
     */
    private var runnableProgress = Runnable { setSeekProgress() }

    /**
     * 当前倒计时
     */
    private var currentSecond = 30
    private var runnable = Runnable { countSeconds() }

    /**
     * 初始化數據(可默認不實現)
     */
    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 直接下一步,无验证消息
        bt_common_function.apply {
            dOnClick(::verify) {
                routeCustom(ARouterConst.Activity_UploadPhotoActivity)
                        .param(establishStatusBean)
                        .transitionToolbar(baseActivity)
                        .navigation(baseActivity)
                        .empty(TODO = "手持照片")
            }

            // 开始计时
            post {
                countSeconds()
            }
        }

        // 播放/暂停
        empty {
            mediaControl = MediaPlayer.create(this, R.raw.risk_disclosure)
            mediaControl.setOnCompletionListener {
                // 到最后的话,就暂停播放,然后回归原始默认值
                handler.removeCallbacks(runnableProgress)
                mediaControl.pause()
                mediaControl.seekTo(0)
                sb_jump.progress = 0
                listOf(iv_control_one, iv_control_two).forEach { it.isActivated = mediaControl.isPlaying }
            }
            mediaControl.setOnPreparedListener {
                listOf(iv_control_one, iv_control_two).dOnClick {
                    if (!mediaControl.isPlaying) {
                        mediaControl.start()
                        setSeekProgress()
                    } else {
                        handler.removeCallbacks(runnableProgress)
                        mediaControl.pause()
                    }
                    listOf(iv_control_one, iv_control_two).forEach { it.isActivated = mediaControl.isPlaying }
                }

                // jump
                sb_jump.max = mediaControl.duration
                sb_jump.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // 拖动停止时,或者点击播放位置后
                        mediaControl.seekTo(seekBar!!.progress)
                    }
                })
            }
            try {
                mediaControl.prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 手机号
        tv_phone.dOnClick {
            startActivity(Intent.createChooser(IntentUtils.getDialIntent(phone), "请选择应用"))
        }

        // dialog
        singleDialog = includeDialog!!.createCCTMDForEstablish()
    }

    /**
     * 倒计时,30秒,之前不可进行
     */
    @SuppressLint("SetTextI18n")
    private fun countSeconds() {
        handler.removeCallbacks(runnable)
        bt_common_function.text = "我已清楚所有風險,下一步 ($currentSecond)"
        onExistEditTextChanged()

        // 未到时间,继续减小
        if (currentSecond-- > 0) {
            handler.postDelayed(runnable, 1000)
        }
    }

    /**
     * 设置 进度情况
     */
    private fun setSeekProgress() {
        sb_jump.progress = mediaControl.currentPosition
        handler.postDelayed(runnableProgress, 500)
    }

    /**
     * 是否通过验证信息
     */
    private fun verify(): Boolean {
        return when {
            else -> true.empty(TODO = "验证输入内容")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        setDefaultToolbarMenuText(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        singleDialog.show(AlphaIDVGAnimatorImpl())
        return true
    }

    override fun onExistEditTextChanged() {
        super.onExistEditTextChanged()

        bt_common_function.isEnabled = currentSecond == 0
    }

    override fun onDestroy() {
        super.onDestroy()

        // 如果界面意外退出销毁,需要移除定时器
        handler.removeCallbacks(runnable)

        empty {
            mediaControl.stop()
            mediaControl.release()
        }
    }

    override fun onPause() {
        super.onPause()

        // 暂停播放
        empty {
            try {
                if (mediaControl.isPlaying) {
                    handler.removeCallbacks(runnableProgress)
                    mediaControl.pause()
                    listOf(iv_control_one, iv_control_two).forEach { it.isActivated = mediaControl.isPlaying }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
