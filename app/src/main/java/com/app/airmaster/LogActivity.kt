package com.app.airmaster

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.view.GaugeHeightView
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.ControlViewModel
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

    private var controlViewModel : ControlViewModel?= null

    private var logTv : TextView ?= null

    private var clearBtn : Button ?= null

    private var updateLogTv : TextView ?= null


    private var gaugeView : GaugeHeightView ?= null

    private var lefAddBtn : Button ?= null
    private var leftRemoveBtn : Button ?= null

    private var lefAddBtn2 : Button ?= null
    private var leftRemoveBtn2 : Button ?= null



    private var sb = StringBuffer()

    override fun getLayoutId(): Int {
        return R.layout.activity_log_layout
    }

    override fun initView() {
        leftRemoveBtn2 = findViewById(R.id.leftRemoveBtn2)
        lefAddBtn2 = findViewById(R.id.lefAddBtn2)
        leftRemoveBtn = findViewById(R.id.leftRemoveBtn)
        lefAddBtn = findViewById(R.id.lefAddBtn)
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

        findViewById<Button>(R.id.intoBtn).setOnClickListener {
            controlViewModel?.setHeightMemory(false)
        }
        findViewById<Button>(R.id.outBtn).setOnClickListener {
            controlViewModel?.setHeightMemory(true)
        }



        findViewById<Button>(R.id.requestBtn).setOnClickListener {
            request()
        }

        lefAddBtn?.setOnClickListener {
            sb.delete(0,sb.length)
            val map = HashMap<Int,Int>()
            map[1] = 1
            controlViewModel?.setManualOperation(map)

//            GlobalScope.launch {
//                delay(150)
//                map[1] = 0
//                controlViewModel?.setManualOperation(map)
//            }
        }

        leftRemoveBtn?.setOnClickListener {
            sb.delete(0,sb.length)
            val map = HashMap<Int,Int>()
            map[1] = 2
            controlViewModel?.setManualOperation(map)
//            GlobalScope.launch {
//                delay(150)
//                map[1] = 0
//                controlViewModel?.setManualOperation(map)
//            }
        }


        lefAddBtn2?.setOnClickListener {
            sb.delete(0,sb.length)
            val map = HashMap<Int,Int>()
            map[1] = 1
            controlViewModel?.setManualOperation(map)

            GlobalScope.launch {
                delay(150)
                map[1] = 0
                controlViewModel?.setManualOperation(map)
            }
        }

        leftRemoveBtn2?.setOnClickListener {
            sb.delete(0,sb.length)
            val map = HashMap<Int,Int>()
            map[1] = 2
            controlViewModel?.setManualOperation(map)
            GlobalScope.launch {
                delay(150)
                map[1] = 0
                controlViewModel?.setManualOperation(map)
            }
        }
    }



    private fun request(){
        //最大气压
        val maxAir = MmkvUtils.getMaxPressureValue()
        sb.append("最大气压="+maxAir+"\n")
        logTv?.text = sb.toString()
        controlViewModel?.writeCommonFunction()
    }

    override fun initData() {
        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        sb.delete(0,sb.length)
       // val logStr = BaseApplication.getBaseApplication().bleOperate.log.toString()

        //getLog()
        getAutoLog()


        controlViewModel?.airLog?.observe(this){
            val st =  BikeUtils.getCurrDate2()+" "+it
            sb.append(st+"\n")
            logTv?.text =sb.toString()
        }

        controlViewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
          sb.append("自动返回:"+Gson().toJson(it)+"\n")
            logTv?.text = sb.toString()
        }
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