package com.wanggsx.wifitransfer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import java.util.ArrayList

class MyWifiBroadcastReceiver(private val mWifiP2pManager: WifiP2pManager,
                              private val mChannel: WifiP2pManager.Channel,
                              private val mDirectActionListener: OnWifiStateChangedListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!TextUtils.isEmpty(intent.action)) {
            when (intent.action) {
                // wifip2p状态变更通知
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        //wifip2p可以使用
                        mDirectActionListener.wifiP2pEnabled(true)
                    } else {
                        //wifip2p不可用
                        mDirectActionListener.wifiP2pEnabled(false)
                        val wifiP2pDeviceList = ArrayList<WifiP2pDevice>()
                        mDirectActionListener.onPeersAvailable(wifiP2pDeviceList)
                    }
                }
                // 对等节点列表发生了变化
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    mWifiP2pManager.requestPeers(mChannel) {
                        peers -> mDirectActionListener.onPeersAvailable(peers.deviceList)
                    }
                }
                // Wifi P2P 的连接状态发生了改变
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo.isConnected) {
                        mWifiP2pManager.requestConnectionInfo(mChannel) {
                            info -> mDirectActionListener.onConnectionInfoAvailable(info)
                        }
                        Log.e(TAG, "已连接p2p设备")
                    } else {
                        mDirectActionListener.onDisconnection()
                        Log.e(TAG, "与p2p设备已断开连接")
                    }
                }
                //本设备的设备信息发生了变化
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    mDirectActionListener.onSelfDeviceAvailable(
                            intent.getParcelableExtra<Parcelable>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice)
                }
            }
        }
    }

    companion object {

        private const val TAG = "MyWifiBroadcastReceiver"

        public val intentFilter: IntentFilter
            get() {
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                return intentFilter
            }
    }

}