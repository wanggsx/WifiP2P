package com.wanggsx.wifitransfer

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

/**
 * 作者：leavesC
 * 时间：2019/2/27 23:56
 * 描述：客户端发送文件
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class ClientTask(context: Context, private val fileTransfer: FileTransfer) : AsyncTask<String, Int, Boolean>() {

    private val progressDialog: ProgressDialog = ProgressDialog(context)

    init {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle("正在发送文件")
        progressDialog.max = 100
    }

    override fun onPreExecute() {
        progressDialog.show()
    }

    override fun doInBackground(vararg strings: String): Boolean? {
        fileTransfer.md5 = Md5Util.getMd5(File(fileTransfer.filePath))
        Log.e(TAG, "文件的MD5码值是：" + fileTransfer.md5!!)
        var socket: Socket? = null
        var outputStream: OutputStream? = null
        var objectOutputStream: ObjectOutputStream? = null
        var inputStream: InputStream? = null
        try {
            socket = Socket()
            socket.bind(null)
            socket.connect(InetSocketAddress(strings[0], PORT), 10000)
            outputStream = socket.getOutputStream()
            objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(fileTransfer)
            inputStream = FileInputStream(File(fileTransfer.filePath))
            val fileSize = fileTransfer.fileLength
            var total: Long = 0
            val buf = ByteArray(512)
            var len: Int
            do {
                len = inputStream.read(buf)
                if(len!=-1){
                    outputStream!!.write(buf, 0, len)
                    total += len.toLong()
                    val progress = (total * 100 / fileSize).toInt()
                    publishProgress(progress)
                    Log.e(TAG, "文件发送进度：$progress")
                }
            }while (len != -1)
            outputStream!!.close()
            objectOutputStream.close()
            inputStream.close()
            socket.close()
            outputStream = null
            objectOutputStream = null
            inputStream = null
            socket = null
            Log.e(TAG, "文件发送成功")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "文件发送异常 Exception: " + e.message)
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (socket != null) {
                try {
                    socket.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return false
    }

    override fun onProgressUpdate(vararg values: Int?) {
        progressDialog.progress = values[0]!!
    }

    override fun onPostExecute(aBoolean: Boolean?) {
        progressDialog.cancel()
        Log.e(TAG, "onPostExecute: " + aBoolean!!)
    }

    companion object {

        private val PORT = 4786

        private val TAG = "WifiClientTask"
    }

}
