package com.app.airmaster

import android.widget.Button
import android.widget.TextView
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.view.GaugeHeightView
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
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



    override fun getLayoutId(): Int {
        return R.layout.activity_log_layout
    }

    override fun initView() {
        gaugeView = findViewById(R.id.gaugeView)
        updateLogTv = findViewById(R.id.updateLogTv)
        logTv = findViewById(R.id.logTv)
        clearBtn = findViewById(R.id.clearBtn)
        clearBtn?.setOnClickListener{
            BaseApplication.getBaseApplication().logStr = "--"
            BaseApplication.getBaseApplication().clearLog()
            logTv?.text = ""
            updateLogTv?.text = ""
        }

        findViewById<Button>(R.id.requestBtn).setOnClickListener {
            request()
        }


        gaugeView?.setValues(80,50)
    }



    private fun request(){
        EasyHttp.get(this).api("checkUpdate?firmwareVersionCode=320&productNumber=c003").request(object :
            OnHttpListener<String> {
            override fun onSucceed(result: String?) {
                logTv?.text = result
            }

            override fun onFail(e: Exception?) {
                e?.printStackTrace()
                Timber.e("----e="+e?.printStackTrace()+"\n"+e?.fillInStackTrace()+"\n"+e?.localizedMessage)
                logTv?.text = e?.message
            }

        })
    }

    override fun initData() {
       // val logStr = BaseApplication.getBaseApplication().bleOperate.log.toString()

        val logStr = BaseApplication.getBaseApplication().logStr

        logTv?.text = logStr

        val updateLog = BaseApplication.getBaseApplication().getAppLog()
        updateLogTv?.text = updateLog
    }
}