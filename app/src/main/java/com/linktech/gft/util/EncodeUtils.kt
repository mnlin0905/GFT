package com.linktech.gft.util

import sun.misc.BASE64Encoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created on 2018/1/18
 * function : 特殊数据加密
 *
 * @author LinkTech
 */

/**
 * 加密方式:
 */
const val SHA_1 = "SHA-1"
const val MD5 = "MD5"
const val SHA_256 = "SHA-256"
const val SHA_384 = "SHA-384"

fun encode(source: String?, type: String?): String? {
    if (source == null || type == null) {
        return ""
    }

    return try {
        val md = MessageDigest.getInstance(type)
        md.update(source.toByteArray())
        bytes2Hex(md.digest())
    } catch (e: NoSuchAlgorithmException) {
        println("签名失败！")
        null
    }
}

internal fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}


/**
 * AES 加密操作
 * 适用于CBC模式
 *
 * @param content  待加密内容
 * @param password 加密密码
 * @return 返回Base64转码后的加密数据
 */
internal fun encrypt(content: String, tadeKey: String?): String? {
    L.i(content)
    try {
        if (tadeKey == null) {
            print("Key为空null")
            return null
        }
        // 判断Key是否为16位
        if (tadeKey.length != 16) {
            print("Key长度不是16位")
            return null
        }
        val raw = tadeKey.toByteArray(charset("utf-8"))
        val skeySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")//"算法/模式/补码方式"
        val iv = IvParameterSpec(tadeKey.toByteArray())//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(content.toByteArray())
        return BASE64Encoder().encode(encrypted)
        //            return Base64.encodeBase64String(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    } catch (e: Exception) {
        return null
    }

}