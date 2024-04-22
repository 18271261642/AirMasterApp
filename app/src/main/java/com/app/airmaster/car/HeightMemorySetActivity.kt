package com.app.airmaster.car

import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.view.CarHomeCenterView
import com.app.airmaster.car.view.HomeLeftAirPressureView
import com.app.airmaster.car.view.HomeRightTemperatureView
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.app.airmaster.viewmodel.ControlViewModel
import com.hjq.shape.view.ShapeTextView

/**
 * 设置高度记忆
 */
class HeightMemorySetActivity : AppActivity(){

    private var controlViewModel : ControlViewModel?= null

    private var heightMemoryView : CarHomeCenterView ?= null
    private var memoryLeftAirPressureView : HomeLeftAirPressureView ?= null
    private var memoryRightView : HomeRightTemperatureView ?= null

    private var modelIndex  = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_height_memory_set_layout
    }

    override fun initView() {
        heightMemoryView = findViewById(R.id.heightMemoryView)
        memoryLeftAirPressureView = findViewById(R.id.memoryLeftAirPressureView)
        memoryRightView = findViewById(R.id.memoryRightView)


        //恢复
        findViewById<ShapeTextView>(R.id.heightMemoryRestoreTv).setOnClickListener {
            finish()
        }

        //保存
        findViewById<ShapeTextView>(R.id.heightMemorySaveTv).setOnClickListener {
            val isH = modelIndex !=0 && modelIndex != 5

          //  controlViewModel?.setHeightMemory(isH)
            controlViewModel?.setLogMemoryData(modelIndex)
        }
    }

    override fun initData() {
        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        val index = intent.getIntExtra("index",0)
        title = if(index==5) "LOW" else index.toString()
        modelIndex = index
        heightMemoryView?.setFrontImage()


        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener{
            val autoBean = it
            heightMemoryView?.setLeftRearPressureValue(autoBean.leftRearPressure)
            heightMemoryView?.setRightTopPressureValue(autoBean.rightPressure)
            heightMemoryView?.setLeftTopPressureValue(autoBean.leftPressure)
            heightMemoryView?.setRightRearPressureValue(autoBean.rightRearPressure)

            heightMemoryView?.setFrontHeightValue(autoBean.leftFrontRulerFL,autoBean.rightFrontRulerFL)
            heightMemoryView?.setAfterHeightValue(autoBean.leftRearRulerFL,autoBean.rightRearRulerFL)

            memoryLeftAirPressureView?.setAirPressureValue(autoBean.cylinderPressure)
            memoryRightView?.setTempValue(autoBean.airBottleTemperature -86)
        }
        heightMemoryView?.setOnPressureListener { map -> controlViewModel?.setManualOperation(map!! as HashMap<Int, Int>) }


        controlViewModel?.commControlStatus?.observe(this){
            finish()
        }

    }
}