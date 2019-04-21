package com.wanggsx.wifitransfer

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest

/**
 * 作者：leavesC
 * 时间：2019/2/27 23:57
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
object Md5Util {

    fun getMd5(file: File): String? {
        var inputStream: InputStream? = null
        val buffer = ByteArray(2048)
        var numRead: Int
        val md5: MessageDigest
        try {
            inputStream = FileInputStream(file)
            md5 = MessageDigest.getInstance("MD5")
            do {
                numRead = inputStream.read(buffer)
                if(numRead!=-1)
                    md5.update(buffer, 0, numRead)
            }while (numRead!=-1)
            inputStream.close()
            inputStream = null
            return md5ToString(md5.digest())
        } catch (e: Exception) {
            return null
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun md5ToString(md5Bytes: ByteArray): String {
        val hexValue = StringBuilder()
        for (b in md5Bytes) {
            val `val` = b.toInt() and 0xff
            if (`val` < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(`val`))
        }
        return hexValue.toString()
    }

}
