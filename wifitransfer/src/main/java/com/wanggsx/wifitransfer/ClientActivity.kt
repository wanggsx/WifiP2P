package com.wanggsx.wifitransfer

import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wanggsx.library.util.UtilsToast

class ClientActivity : AppCompatActivity(), OnWifiStateChangedListener {

    lateinit var mReceiver: MyWifiBroadcastReceiver

    private var mWifiP2pManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null
    lateinit var mRecyclerView : RecyclerView

    private var mWifiP2pDeviceList: MutableList<WifiP2pDevice> = ArrayList()

    private var mWifiP2pDevice : WifiP2pDevice ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setDimAmount(0f)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        mRecyclerView = findViewById(R.id.rvMenu_client_activity)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mWifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mWifiP2pManager!!.initialize(this, this.mainLooper, this)
        mReceiver = MyWifiBroadcastReceiver(mWifiP2pManager!!, mChannel!!, this)
        registerReceiver(mReceiver, MyWifiBroadcastReceiver.intentFilter)
    }

    //上传文件
    fun uploadFile(view: View) {

    }

    //显示可用的服务端列表
    fun showServerList(v : View) {
        mWifiP2pManager!!.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                UtilsToast.showLong("Success")
            }

            override fun onFailure(reasonCode: Int) {
                UtilsToast.showLong("Failure")
            }
        })
    }

    //显示服务端对应的文件列表
    fun showMenuList(view: View) {

    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    /** OnWifiChangedListener实现类 */
    override fun onDisconnection() {
        Log.d("wanggsx", "onDisconnection")
    }

    override fun onChannelDisconnected() {
        Log.d("wanggsx", "onChannelDisconnected")
    }

    override fun wifiP2pEnabled(enabled: Boolean) {
        Log.d("wanggsx", "wifiP2pEnabled")
    }

    override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo?) {
        Log.d("wanggsx", "onConnectionInfoAvailable")
    }

    override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice?) {
        Log.d("wanggsx", "onSelfDeviceAvailable")
    }

    var mAdapterDevice = object : BaseQuickAdapter<WifiP2pDevice, BaseViewHolder>(R.layout.item_device) {
        override fun convert(helper: BaseViewHolder?, item: WifiP2pDevice?) {
            if (helper != null && item != null) {
                helper.setText(R.id.tvDeviceName_device_item, item.deviceName + "(" + getStatus(item.status) + ")")
                        .setText(R.id.tvDeviceIp_device_item, item.deviceAddress)
            }
        }
    }

    private fun getStatus(code : Int) : String {
        when(code){
            0 -> "已连接"
            1 -> "邀请过"
            2 -> "连接失败"
            3 -> "可用"
            4 -> "不可用"
            else -> ""
        }
        return ""
    }

    override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>?) {
        Log.d("wanggsx", "onPeersAvailable")
        mWifiP2pDeviceList.clear()
        mWifiP2pDeviceList.addAll(wifiP2pDeviceList!!)
        mRecyclerView.adapter = mAdapterDevice
        mAdapterDevice.setNewData(mWifiP2pDeviceList)
        mAdapterDevice.setOnItemClickListener { adapter, view, position ->
            mWifiP2pDevice = adapter.data[position] as WifiP2pDevice?
            connectToServer()
        }
    }

    private fun connectToServer() {
        val config = WifiP2pConfig()
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice!!.deviceAddress
            config.wps.setup = WpsInfo.PBC
            mWifiP2pManager!!.connect(mChannel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    UtilsToast.showLong("已经成功连接到设置：" + mWifiP2pDevice!!.deviceName)
                }

                override fun onFailure(reason: Int) {
                    UtilsToast.showLong("连接失败！" )
                }
            })
        }
    }

}