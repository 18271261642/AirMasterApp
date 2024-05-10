package com.app.airmaster.car

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.bean.AutoSetBean
import com.app.airmaster.car.view.CarHomeCenterView
import com.app.airmaster.car.view.HomeLeftAirPressureView
import com.app.airmaster.car.view.HomeRightTemperatureView
import com.app.airmaster.dialog.LogDialogView
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.app.airmaster.viewmodel.ControlViewModel
import com.blala.blalable.car.AutoBackBean
import com.google.gson.Gson
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.shape.view.ShapeTextView

/**
 * 设置高度记忆
 */
class HeightMemorySetActivity : AppActivity(){

    private var heightMemoryTitleBar : TitleBar ?= null

    private var controlViewModel : ControlViewModel?= null

    private var heightMemoryView : CarHomeCenterView ?= null
    private var memoryLeftAirPressureView : HomeLeftAirPressureView ?= null
    private var memoryRightView : HomeRightTemperatureView ?= null

    private var modelIndex  = 0

    private var autoBean : AutoSetBean ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_height_memory_set_layout
    }



    override fun initView() {
        heightMemoryTitleBar = findViewById(R.id.heightMemoryTitleBar)
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


        heightMemoryTitleBar?.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(view: View?) {
               finish()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {
                val log = autoBean?.gearHashMap
                showLogDialog(Gson().toJson(log))
            }

        })
    }

    override fun initData() {
        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        val index = intent.getIntExtra("index",0)
        title = if(index==5) "LOW" else index.toString()
        modelIndex = index
        heightMemoryView?.setFrontImage()

        controlViewModel?.autoSetBeanData?.observe(this){
            autoBean = it
            val hashMap = it.gearHashMap

            val bean = hashMap[if(modelIndex == 5) 0 else modelIndex]
            if(bean != null){
                heightMemoryView?.setFrontGoalValue(bean.leftFront,bean.rightFront)
                heightMemoryView?.setRearGoalValue(bean.leftRear,bean.rightRear)
            }

        }


        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener{
            val autoBean = it
            heightMemoryView?.setLeftRearPressureValue(autoBean.leftRearPressure)
            heightMemoryView?.setRightTopPressureValue(autoBean.rightPressure)
            heightMemoryView?.setLeftTopPressureValue(autoBean.leftPressure)
            heightMemoryView?.setRightRearPressureValue(autoBean.rightRearPressure)

            heightMemoryView?.setFrontHeightValue(autoBean.leftFrontRulerFL,autoBean.rightFrontRulerFL)
            heightMemoryView?.setAfterHeightValue(autoBean.leftRearRulerFL,autoBean.rightRearRulerFL)


//
//            heightMemoryView?.setFrontGoalValue(autoBean.leftFrontGoalFL,autoBean.rightFrontGoalFL)
//            heightMemoryView?.setRearGoalValue(autoBean.leftRearGoalFL,autoBean.rightRearGoalFL)



            memoryLeftAirPressureView?.setAirPressureValue(autoBean.cylinderPressure)
            memoryRightView?.setTempValue(autoBean.airBottleTemperature -86)
        }
        heightMemoryView?.setOnPressureListener { map -> controlViewModel?.setManualOperation(map!! as HashMap<Int, Int>) }


        controlViewModel?.commControlStatus?.observe(this){
            finish()
        }

        controlViewModel?.writeCommonFunction()

    }


    private fun showLogDialog(txt : String){
        val dialog = LogDialogView(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setLogTxt(txt)

        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = (metrics2.widthPixels*0.7F).toInt()
        val height : Int = (metrics2.heightPixels*0.7F).toInt()

        windowLayout?.height= height
        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.CENTER_VERTICAL
        window?.attributes = windowLayout
    }
}