package com.wanggsx.wifitransfer

import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.wanggsx.library.util.UtilsToast
import java.io.File

class ServerActivity :  AppCompatActivity(), OnWifiStateChangedListener{

    companion object {
        const val TAG : String = "ServerActivity"
    }

    lateinit  var mReceiver : MyWifiBroadcastReceiver
    var mIsServing = false

    private var mWifiP2pManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null

    private var mServerService : ServerService ? = null
    lateinit var mProgressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setDimAmount(0f)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        mWifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mWifiP2pManager!!.initialize(this, this.mainLooper,this)
        mReceiver = MyWifiBroadcastReceiver(mWifiP2pManager!!,mChannel!!,this)
        registerReceiver(mReceiver,MyWifiBroadcastReceiver.intentFilter)
        //对话框
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setTitle("正在接收文件")
        mProgressDialog.max = 100
        bindService()
    }

    /** 注册服务模式 */
    fun toService(v: View){
        mIsServing = true
        startService()
    }

    /** 取消注册服务模式 */
    fun stopService(v : View){
        mIsServing = false
        stopService()
    }

    private fun startService(){
        mWifiP2pManager!!.createGroup(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.e(TAG, "createGroup onSuccess")
                UtilsToast.showLong("onSuccess")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "createGroup onFailure: $reason")
                UtilsToast.showLong("onFailure")
            }
        })
    }

    private fun stopService(){
        mWifiP2pManager!!.removeGroup(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.e(TAG, "removeGroup onSuccess")
                UtilsToast.showLong("onSuccess")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "removeGroup onFailure: $reason")
                UtilsToast.showLong("onFailure")
            }
        })
    }

    override fun onDestroy() {
        if (mIsServing)stopService()
        unregisterReceiver(mReceiver)
        if (mServerService != null) {
            unbindService(mServiceConnection)
            mServerService = null
        }
        stopService()
        stopService(Intent(this, ServerService::class.java))
        super.onDestroy()
    }

    /** OnWifiChangedListener实现类 */
    override fun onDisconnection() {
        Log.d("wanggsx","onDisconnection")
    }

    override fun onChannelDisconnected() {
        Log.d("wanggsx","onChannelDisconnected")
    }

    override fun wifiP2pEnabled(enabled: Boolean) {
        Log.d("wanggsx","wifiP2pEnabled")
    }

    override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo?) {
        Log.d("wanggsx","onConnectionInfoAvailable")
        if (wifiP2pInfo!!.groupFormed && wifiP2pInfo.isGroupOwner) {
            if (mServerService != null) {
                startService(Intent(this, mServerService!!::class.java))
            }
        }

    }

    override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice?) {
        Log.d("wanggsx","onSelfDeviceAvailable")
    }

    override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>?) {
        Log.d("wanggsx","onPeersAvailable")
    }

    /** 后台接收文件服务 */


    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ServerService.MyBinder
            mServerService = binder.service
            mServerService!!.setProgressChangListener(mProgressChangListener)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServerService = null
            bindService()
        }
    }

    private fun bindService() {
        val intent = Intent(this@ServerActivity, ServerService::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }


    private val mProgressChangListener = object : ServerService.OnProgressChangListener {
        override fun onProgressChanged(fileTransfer: FileTransfer, progress: Int) {
            runOnUiThread {
                mProgressDialog.setMessage("文件名： " + File(fileTransfer.filePath).getName())
                mProgressDialog.progress = progress
                mProgressDialog.show()
            }
        }

        override fun onTransferFinished(file: File?) {
            runOnUiThread {
                mProgressDialog.cancel()
//                if (file != null && file.exists()) {
//                    openFile(file.path)
//                }
            }
        }
    }

}