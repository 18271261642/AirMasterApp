package com.app.airmaster

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.CarHomeActivity

/**
 * Created by Admin
 *Date 2023/7/14
 */
class LaunchActivity : AppActivity() {


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                startActivity(CarHomeActivity::class.java)
                finish()
            }
        }
    }


    override fun getLayoutId(): Int {
       return R.layout.activity_launch_layout
    }

    override fun initView() {

    }

    override fun initData() {
        handlers.sendEmptyMessageDelayed(0x00,3000)
    }
}