package com.wanggsx.wifitransfer

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log

import com.wanggsx.library.util.UtilsFile

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class ServerService : IntentService("WifiServerService") {

    private var serverSocket: ServerSocket? = null

    private var inputStream: InputStream? = null

    private var objectInputStream: ObjectInputStream? = null

    private var fileOutputStream: FileOutputStream? = null

    private var progressChangListener: OnProgressChangListener? = null

    interface OnProgressChangListener {

        //当传输进度发生变化时
        fun onProgressChanged(fileTransfer: FileTransfer, progress: Int)

        //当传输结束时
        fun onTransferFinished(file: File?)

    }

    inner class MyBinder : Binder() {
        val service: ServerService
            get() = this@ServerService
    }

    override fun onBind(intent: Intent): IBinder? {
        return MyBinder()
    }

    override fun onHandleIntent(intent: Intent?) {
        clean()
        var file: File? = null
        try {
            serverSocket = ServerSocket()
            serverSocket!!.reuseAddress = true
            serverSocket!!.bind(InetSocketAddress(PORT))
            val client = serverSocket!!.accept()
            Log.e(TAG, "客户端IP地址 : " + client.inetAddress.hostAddress)
            inputStream = client.getInputStream()
            objectInputStream = ObjectInputStream(inputStream)
            val fileTransfer = objectInputStream!!.readObject() as FileTransfer
            Log.e(TAG, "待接收的文件: $fileTransfer")
            val name = File(fileTransfer.filePath).name
            //将文件存储至指定位置
            file = File(Environment.getExternalStorageDirectory().toString() + "/wifip2p/" + name)
            UtilsFile.mkdirs(file.path)
            if (file.exists()) file = File(file.path + "_1")
            file.createNewFile()
            fileOutputStream = FileOutputStream(file)
            val buf = ByteArray(512)
            var len: Int
            var total: Long = 0
            var progress: Int
            do {
                len = inputStream!!.read(buf)
                if(len!=-1){
                    fileOutputStream!!.write(buf, 0, len)
                    total += len.toLong()
                    progress = (total * 100 / fileTransfer.fileLength).toInt()
                    Log.e(TAG, "文件接收进度: $progress")
                    if (progressChangListener != null) {
                        progressChangListener!!.onProgressChanged(fileTransfer, progress)
                    }
                }
            }while (len!=-1)
            serverSocket!!.close()
            inputStream!!.close()
            objectInputStream!!.close()
            fileOutputStream!!.close()
            serverSocket = null
            inputStream = null
            objectInputStream = null
            fileOutputStream = null
            Log.e(TAG, "文件接收成功，文件的MD5码是：" + Md5Util.getMd5(file))
        } catch (e: Exception) {
            Log.e(TAG, "文件接收 Exception: " + e.message)
        } finally {
            clean()
            if (progressChangListener != null) {
                progressChangListener!!.onTransferFinished(file)
            }
            //再次启动服务，等待客户端下次连接
            startService(Intent(this, ServerService::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clean()
    }

    fun setProgressChangListener(progressChangListener: OnProgressChangListener) {
        this.progressChangListener = progressChangListener
    }

    private fun clean() {
        if (serverSocket != null) {
            try {
                serverSocket!!.close()
                serverSocket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (inputStream != null) {
            try {
                inputStream!!.close()
                inputStream = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (objectInputStream != null) {
            try {
                objectInputStream!!.close()
                objectInputStream = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream!!.close()
                fileOutputStream = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        private val TAG = "WifiServerService"

        private val PORT = 4786
    }

}
