package com.wanggsx.wifitransfer

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

class ServerActivity :  AppCompatActivity(), OnWifiStateChangedListener{

    lateinit  var mReceiver : MyWifiBroadcastReceiver
    var mIsServing = false

    private var mWifiP2pManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setDimAmount(0f)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        mWifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mWifiP2pManager!!.initialize(this, this.mainLooper,this)
        mReceiver = MyWifiBroadcastReceiver(mWifiP2pManager!!,mChannel!!,this)
        registerReceiver(mReceiver,MyWifiBroadcastReceiver.intentFilter)
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

    }

    private fun stopService(){

    }

    override fun onDestroy() {
        if (mIsServing)stopService()
        unregisterReceiver(mReceiver)
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
    }

    override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice?) {
        Log.d("wanggsx","onSelfDeviceAvailable")
    }

    override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>?) {
        Log.d("wanggsx","onPeersAvailable")
    }

}