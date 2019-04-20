package com.wanggsx.wifitransfer

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class ClientActivity :  AppCompatActivity(), OnWifiStateChangedListener{

    lateinit  var mReceiver : MyWifiBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mWifiP2pManager : WifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        var mChannel = mWifiP2pManager.initialize(this, this.mainLooper,this)
        mReceiver = MyWifiBroadcastReceiver(mWifiP2pManager,mChannel,this)
        registerReceiver(mReceiver,MyWifiBroadcastReceiver.intentFilter)
    }

    override fun onDestroy() {
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