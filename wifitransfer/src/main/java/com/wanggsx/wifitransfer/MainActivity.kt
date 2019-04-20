package com.wanggsx.wifitransfer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //申请文件读写权限
        //UtilsPermission.requestWriteStory(this)
    }

    fun toClient(view: View) {
        startActivity(Intent(this,ClientActivity::class.java))
    }

    fun toServer(view: View) {
        startActivity(Intent(this,ServerActivity::class.java))
    }

}