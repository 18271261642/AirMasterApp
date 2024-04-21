package com.app.airmaster

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.view.GaugeHeightView
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Admin
 *Date 2023/4/20
 */
class LogActivity : AppActivity() {

    private var logTv : TextView ?= null

    private var clearBtn : Button ?= null

    private var updateLogTv : TextView ?= null


    private var gaugeView : GaugeHeightView ?= null


    private var sb = StringBuffer()

    override fun getLayoutId(): Int {
        return R.layout.activity_log_layout
    }

    override fun initView() {
        updateLogTv = findViewById(R.id.updateLogTv)
        logTv = findViewById(R.id.logTv)
        clearBtn = findViewById(R.id.clearBtn)
        clearBtn?.setOnClickListener{
          //  BaseApplication.getBaseApplication().logStr = "--"
            BaseApplication.getBaseApplication().clearLog()
            sb.delete(0,sb.length)
            logTv?.text = ""
            updateLogTv?.text = ""
        }

        findViewById<Button>(R.id.requestBtn).setOnClickListener {
            request()
        }


    }



    private fun request(){

    }

    override fun initData() {
        sb.delete(0,sb.length)
       // val logStr = BaseApplication.getBaseApplication().bleOperate.log.toString()

        getLog()

    }

    private val gson = Gson()
    private fun getAutoLog(){
        val logStr = BaseApplication.getBaseApplication().autoBackBean
        sb.append(gson.toJson(logStr)+"\n")
        logTv?.text = sb.toString()
    }


    private fun getLog(){
        GlobalScope.launch {
            delay(500)
            handlers.sendEmptyMessage(0x00)
        }
    }


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                getAutoLog()
                getLog()
            }
        }
    }
}