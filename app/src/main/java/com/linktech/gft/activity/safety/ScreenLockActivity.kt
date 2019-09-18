package com.linktech.gft.activity.safety


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.DialogInterface
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.linktech.gft.R
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BasePresenter
import com.linktech.gft.plugins.*
import com.linktech.gft.util.Const
import com.linktech.gft.util.CustomAliPayPainter
import com.linktech.gft.util.DefaultPreferenceUtil
import com.linktech.gft.window.AlertCloseDialogFragment
import com.wangnan.library.listener.OnGestureLockListener
import kotlinx.android.synthetic.main.activity_screen_lock.*
import org.jetbrains.anko.fingerprintManager
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * function : 软件后台 -> 前台 锁屏界面
 *
 * Created on 2018/7/16  16:51
 * @author mnlin
 */
@Route(path = ARouterConst.Activity_ScreenLockActivity, extras = ARouterConst.FLAG_LOGIN)
@InjectLayoutRes(layoutResId = R.layout.activity_screen_lock)
@InjectActivityTitle(titleRes = R.string.label_none)
class ScreenLockActivity : BaseActivity<BasePresenter<ScreenLockActivity>>(), OnGestureLockListener {
    /**
     * 是否开启了 指纹 ,是否开启了手势
     */
    @Autowired(name = Const.KEY_COMMON_VALUE_ONE, required = true)
    @JvmField
    var gestureEnable: Boolean = false
    @Autowired(name = Const.KEY_COMMON_VALUE_TWO, required = true)
    @JvmField
    var fingerprintSupport: Boolean = false
    @Autowired(name = Const.KEY_COMMON_VALUE_THREE, required = true)
    @JvmField
    var fingerprintEnable: Boolean = false

    /**
     * 生物识别,用于取消识别
     */
    private var cancellationSignal: CancellationSignal? = null

    /**
     * 指纹解锁 模块
     */
    private var mCipher: Cipher? = null
    private val DEFAULT_KEY_NAME = "default_key"
    lateinit var keyStore: KeyStore

    /**
     * 当前解锁模式:手势0 ; 指纹1
     */
    private var currentMode = 1

    /**
     * 初始化數據(可默認不實現)
     */
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        // 账户退出
        val exist = {
            DefaultPreferenceUtil.getInstance().login = false
            DefaultPreferenceUtil.getInstance().signature = null
        }

        //標題
        //setToolbarNavText(toolbar, getString(R.string.action_close), R.color.skin_accent_color)
        toolbar.setNavigationOnClickListener {
            // 设置账户退出登录
            exist()
            route(ARouterConst.Activity_FinancingActivity)
        }

        // 重新登录
        tv_switch.dOnClick {
            // 设置账户退出登录
            exist()
            routeCustom(ARouterConst.Activity_LoginActivity)
                    .firstParam(false)
                    .navigation()
                    .empty(comment = "这里跳转进入登录界面,不需要回填帐号信息,相当于其他用户来登录")
        }

        // 另一个 登录方式
        tv_another.dOnClick {
            toast(R.string.activity_screen_lock_hardware_not_support)
        }

        // 手势 (切换显示)
        tv_gesture.dOnClick {
            if (currentMode == 0) {
                if (fingerprintEnable) {
                    tv_message.text = getString(R.string.activity_screen_lock_hint)
                    currentMode = 1
                } else {
                    if(fingerprintSupport){
                        //硬件支持,则提示
                        showAlert(false)
                    }else{
                        toast(R.string.activity_screen_lock_hardware_not_support)
                    }
                    return@dOnClick
                }
            } else if (currentMode == 1) {
                if (gestureEnable) {
                    currentMode = 0
                } else {
                    showAlert(true)
                    return@dOnClick
                }
            }

            ll_fingerprint.visibility = if (currentMode == 0) View.GONE else View.VISIBLE
            ll_gesture.visibility = if (currentMode == 0) View.VISIBLE else View.GONE
            tv_gesture.text = getString(if (currentMode == 0) R.string.label_fingerprint_lock else R.string.activity_screen_lock_gesture)

            if (currentMode == 0) {
                cancellationSignal?.cancel()
                cancellationSignal = null
            } else {
                startFingerPrint()
            }
        }

        // 图形部分
        glv_gesture.setPainter(CustomAliPayPainter().also { it.showGestureTravel = DefaultPreferenceUtil.getInstance().isScreenGestureLockedTravel(_user_id) })
        glv_gesture.setGestureLockListener(this)

        // 其他
        tv_other.dOnClick {
            // 设置账户退出登录
            exist()
            route(ARouterConst.Activity_LoginActivity)
        }
    }

    /**
     * 当硬件支持但未开启时候,提示信息
     */
    private fun showAlert(isGesture:Boolean){
        AlertCloseDialogFragment()
                .setConfirmText(getString(R.string.function_sure))
                .setTitle(getString(R.string.activity_screen_lock_not_enable,getString(if(isGesture) R.string.label_gesture_lock else R.string.label_fingerprint_lock)))
                .show(sfManager, "alert")
    }

    override fun onStart() {
        super.onStart()

        //开始时,默认是指纹识别(若不行,则让界面显示手势)
        if (fingerprintEnable) {
            startFingerPrint()
        } else {
            tv_gesture.performClick()
        }
    }

    /**
     * 开始监听手势验证
     */
    private fun startFingerPrint() {
        // 是否支持生物识别
        if (Build.VERSION.SDK_INT >= 23) {
            getCurrentFingerprintHash {
                if (it == DefaultPreferenceUtil.getInstance().fingerprintMD5) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        createBiometric()
                    } else {
                        createFingerprint()
                    }
                } else {
                    toast(R.string.activity_screen_lock_login_again)
                    DefaultPreferenceUtil.getInstance().setScreenFingerprintLocked(_user_id, false)
                    tv_switch.performClick()
                }
            }
        } else {
            toast(getString(R.string.activity_screen_lock_not_support))
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    fun createBiometric() {
        cancellationSignal = CancellationSignal().also {
            it.setOnCancelListener {
                tv_message.text = getString(R.string.activity_screen_lock_has_cancel)
            }

            val build = BiometricPrompt.Builder(this)
                    .setTitle(getString(R.string.activity_screen_lock_title))
                    .setDescription(getString(R.string.activity_screen_lock_des))
                    .setNegativeButton(getString(R.string.action_cancel), mainExecutor,
                            DialogInterface.OnClickListener { dialog, which ->
                                it.cancel()
                                goToHome()
                            })
                    .build()

            build.authenticate(
                    it,
                    mainExecutor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                            super.onAuthenticationError(errorCode, errString)
                            tv_message.text = errString?.toString() ?: "error: $errorCode"
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                            super.onAuthenticationSucceeded(result)
                            finish()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            tv_message.text = getString(R.string.activity_screen_lock_fail_once)
                        }
                    })
        }
    }


    /**
     * 使用旧api
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun createFingerprint() {
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        val fingerprintManager = getSystemService(FingerprintManager::class.java)
        if (!fingerprintManager.isHardwareDetected) {
            toast(getString(R.string.activity_screen_lock_hard_not))
        } else if (!keyguardManager.isKeyguardSecure) {
            toast(getString(R.string.activity_screen_lock_no_screen))
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            toast(getString(R.string.activity_screen_lock_require_has))
        } else {
            initKey()
            initCipher()
            startListening()
        }
    }

    @TargetApi(23)
    private fun initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val builder = KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        } catch (e: Exception) {
            toast(e.toString())
            throw RuntimeException(e)
        }
    }

    @TargetApi(23)
    private fun initCipher() {
        try {
            val key = keyStore.getKey(DEFAULT_KEY_NAME, null) as SecretKey
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            mCipher?.init(Cipher.ENCRYPT_MODE, key)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startListening() {
        cancellationSignal = CancellationSignal()
        fingerprintManager.authenticate(FingerprintManager.CryptoObject(mCipher!!), cancellationSignal, 0, object : FingerprintManager.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                tv_message.text = errString?.toString() ?: "error: $errorCode"
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                finish()
            }

            override fun onAuthenticationFailed() {
                tv_message.text = getString(R.string.activity_screen_lock_fail_once)
            }
        }, null)
    }

    override fun onStop() {
        super.onStop()
        cancellationSignal?.cancel()
        cancellationSignal = null
    }

    /**
     * 回到  home 桌面
     */
    private fun goToHome() {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    /**
     * 锁屏时,返回表示退出应用(暂无操作)
     */
    override fun onBackPressed() {

    }

    override fun onComplete(result: String?) {
        if (result.toMD5().equals(DefaultPreferenceUtil.getInstance().getScreenGestureLocked(_user_id))) {
            //相同则 解锁完成
            glv_gesture.clearView()
            finish()
        } else {
            toast(R.string.activity_screen_lock_gesture_error)
            glv_gesture.showErrorStatus(500)
        }
    }

    override fun onStarted() {
    }

    override fun onProgress(progress: String?) {
    }
}
