package com.app.airmaster.car

import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.viewmodel.McuUpgradeViewModel

class McuOtaActivity : AppActivity() {


    //mcu 的ota升级
    private var mcuViewModel : McuUpgradeViewModel?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_mcu_ota_layout
    }

    override fun initView() {
        findViewById<Button>(R.id.mcuFirstBtn).setOnClickListener {
            mcuViewModel?.setFirstReset()
        }

        findViewById<Button>(R.id.mcuSecondBtn).setOnClickListener {
            mcuViewModel?.setSecondModel()
        }

        findViewById<Button>(R.id.mcuThirdBtn).setOnClickListener {
            val btArray = byteArrayOf(0x58, 0x4C, 0x37, 0x30, 0x30, 0x5F, 0x53, 0x54, 0x5F, 0x4D, 0x5F, 0x30, 0x30, 0x31, 0x00, 0x00)
            mcuViewModel?.setThirdData(btArray)
        }
    }

    override fun initData() {
        mcuViewModel = ViewModelProvider(this)[McuUpgradeViewModel::class.java]


    }
}