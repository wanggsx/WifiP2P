package com.wanggsx.wifitransfer

import java.io.Serializable

class FileTransfer(//文件路径
        var filePath: String?, //文件大小
        var fileLength: Long) : Serializable {

    //MD5码
    var md5: String? = null

    override fun toString(): String {
        return "FileTransfer{" +
                "filePath='" + filePath + '\''.toString() +
                ", fileLength=" + fileLength +
                ", md5='" + md5 + '\''.toString() +
                '}'.toString()
    }

}