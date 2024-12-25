package com.app.airmaster.car

import android.widget.Button
import android.widget.TextView
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity

class CrcLogActivity : AppActivity() {

    private var crcLogTv : TextView ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_crc_log_layout
    }

    override fun initView() {
        crcLogTv = findViewById(R.id.crcLogTv)
        findViewById<Button>(R.id.crcClearLogBtn)?.setOnClickListener {
            BaseApplication.getInstance().bleManager.clearCrcLog()
            crcLogTv?.text = ""
        }
    }

    override fun initData() {
        val log = BaseApplication.getInstance().bleManager.crcBuffer

        crcLogTv?.text = log
    }


}