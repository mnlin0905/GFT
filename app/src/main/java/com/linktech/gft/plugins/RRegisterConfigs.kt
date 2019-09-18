package com.linktech.gft.plugins

import android.content.Intent
import android.text.TextUtils
import android.util.Pair
import com.knowledge.mnlin.rregister.receivers.SMSReceiver
import com.knowledge.mnlin.rregister.util.HttpCallback
import com.linktech.gft.util.RegexConst

/**************************************
 * function : 广播监听框架--rregister 配置与自定义监听类
 *
 * Created on 2018/6/27  13:59
 * @author mnlin
 **************************************/

/**
 * function : 重写短信监听,直接获取到6位短信验证码
 *
 * Created on 2018/6/27  14:30
 * @author mnlin
 */
class MSmsReceiver(httpCallback: HttpCallback<Pair<String, String>>) : SMSReceiver(httpCallback) {
    override fun apply(intentPairFunction: Intent): Pair<String, String> {
        val apply = super.apply(intentPairFunction)
        return if (apply != null && !TextUtils.isEmpty(apply.second)) {
            Pair(apply.first, apply.second.replace(RegexConst.REGEX_VERIFY_CODE_SMS_EMAIL.toRegex(), "$1").let {
                if (it.matches("[\\d]{6}".toRegex())) it else throw RuntimeException("not require verify code")
            })
        } else throw RuntimeException("error verify code")
    }
}