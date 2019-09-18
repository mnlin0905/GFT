package com.linktech.gft.util

import java.nio.charset.StandardCharsets

/**************************************
 * function : java hex,str,bin等格式工具类转换
 *
 * Created on 2018/8/8  15:28
 * @author mnlin
 **************************************/

/**
 * function : hex字符串(长度为2倍速) -> byte[]
 */
fun hexStr2ByteArray(hexs: String): ByteArray {
    val length = hexs.length
    val bytes = ByteArray(length / 2)
    for (i in 0 until length / 2) {
        bytes[i] = Integer.parseInt(hexs.substring(i * 2, i * 2 + 2), 16).toByte()
    }
    return bytes
}

/**
 * function : hex字符串(长度为2倍速) -> byte[]
 */
fun hexStr2String(hexs: String): String? {
    return String(hexStr2ByteArray(hexs), StandardCharsets.UTF_8)
}

/**
 * string -> hexString(length为2的倍数)
 */
fun str2HexStr(content: ByteArray): String {
    val builder = StringBuilder()
    content.forEach { item ->
        builder.append(Integer.toHexString((item.toInt() and 0x000000FF)).let {
            if (it.length == 1) "0$it" else it
        })
    }
    return builder.toString()
}

