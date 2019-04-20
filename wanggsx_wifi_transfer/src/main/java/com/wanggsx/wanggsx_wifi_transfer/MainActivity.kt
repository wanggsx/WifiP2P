package com.wanggsx.wanggsx_wifi_transfer

import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity(){




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun sendFile(view: View) {
        startActivity(Intent(this, SendFileActivity::class.java))
    }

    fun receiveFile(view: View) {
        startActivity(Intent(this, ReceiveFileActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
