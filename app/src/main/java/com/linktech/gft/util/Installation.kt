package com.linktech.gft.util

import android.content.Context
import com.blankj.utilcode.util.DeviceUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

/**
 * Created on 2018/2/2
 * function : 生成唯一的ID号
 *
 * @author LinkTech
 */

private var sID: String? = null

private val INSTALLATION = "INSTALLATION"

@Synchronized
fun id(context: Context): String? {
    //首先获取设备AndroidId,如果发现生成了固定的串号,就自动来生成一个唯一id
    val androidID = DeviceUtils.getAndroidID()
    if (!androidID.equals("9774d56d682e549c", ignoreCase = true)) {
        return androidID
    }

    if (sID == null) {
        val installation = File(context.filesDir, INSTALLATION)
        try {
            if (!installation.exists())
                writeInstallationFile(installation)
            sID = readInstallationFile(installation)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
    return sID
}

@Throws(IOException::class)
private fun readInstallationFile(installation: File): String {
    val f = RandomAccessFile(installation, "r")
    val bytes = ByteArray(f.length().toInt())
    f.readFully(bytes)
    f.close()
    return String(bytes)
}

@Throws(IOException::class)
private fun writeInstallationFile(installation: File) {
    val out = FileOutputStream(installation)
    val id = UUID.randomUUID().toString()
    out.write(id.toByteArray())
    out.close()
}