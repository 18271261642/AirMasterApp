package com.app.airmaster.car

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.adapter.CarFaultNotifyAdapter
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.viewmodel.ControlViewModel
import com.blala.blalable.Utils
import timber.log.Timber

class CarFaultNotifyActivity : AppActivity() {

    private var controlViewModel : ControlViewModel ?= null


    private var faultNotifyRy: RecyclerView? = null
    private var adapter: CarFaultNotifyAdapter? = null
    private var list: MutableList<String>? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_car_fault_notify_layout
    }

    override fun initView() {
        faultNotifyRy = findViewById(R.id.faultNotifyRy)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        faultNotifyRy?.layoutManager = linearLayoutManager

        list = ArrayList<String>()
        adapter = CarFaultNotifyAdapter(this, list as ArrayList<String>)
        faultNotifyRy?.adapter = adapter

        findViewById<ImageView>(R.id.notifyBackImg).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.notifyClearTv).setOnClickListener {
            showClearDialog()
        }
    }

    override fun initData() {
        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        //获取状态
        val autoBean = BaseApplication.getBaseApplication().autoBackBean
        if (autoBean != null) {
            //设备故障
            val deviceErrorCode = autoBean.deviceErrorCode
            val errorArray = Utils.byteToBit(deviceErrorCode)
            Timber.e("------通知=" + errorArray)
            val resultMap = getDeviceErrorMsg(errorArray)
            resultMap.forEach {
                list?.add(it.value)
            }

            //气罐故障
            val airErrorCode = autoBean.airBottleErrorCode
            val airArray = Utils.byteToBit(airErrorCode)
            val airMap = getAirBottleErrorCode(airArray)
            airMap.forEach {
                list?.add(it.value)
            }

            //左前
            val leftFrontCode = autoBean.leftFrontErrorCode
            val leftArray = Utils.byteToBit(leftFrontCode)
            val leftMap = getDirectionErrorCode(leftArray)
            leftMap.forEach {
                list?.add(it.value)
            }

            //左后
            val leftRearCode = autoBean.leftRearErrorCode
            val leftRearArray = Utils.byteToBit(leftRearCode)
            val leftRearMap = getDirectionErrorCode(leftRearArray)
            leftRearMap.forEach {
                list?.add(it.value)
            }

            //右前
            val rightFront = autoBean.rightFrontErrorCode
            val rightFrontArray = Utils.byteToBit(rightFront)
            val rightFrontMap = getDirectionErrorCode(rightFrontArray)
            rightFrontMap.forEach {
                list?.add(it.value)
            }

            //右后
            val rightRearCode = autoBean.rightRearErrorCode
            val rightRearArray = Utils.byteToBit(rightRearCode)
            val rightRearMap = getDirectionErrorCode(rightRearArray)
            rightRearMap.forEach {
                list?.add(it.value)
            }

            adapter?.notifyDataSetChanged()
        }
    }

    private fun showClearDialog() {
        val dialog = ConfirmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setOnCommClickListener {
            dialog.dismiss()
            if (it == 0x01) {
                list?.clear()
                adapter?.notifyDataSetChanged()

                controlViewModel?.clearAllWarring()
            }

        }
    }



    //设备故障码
    val map = HashMap<Int, String>()
    private fun getDeviceErrorMsg(errorStr: String): HashMap<Int, String> {
        map.clear()
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            map[0] = "系统未自检"
        }
        if (chartArray[1].toString() == "1") {
            map[1] = "加速度传感器故障"
        }
        if (chartArray[2].toString() == "1") {
            map[2] = "电池电压过高"
        }
        if (chartArray[3].toString() == "1") {
            map[3] = "电池电压过低"
        }
        if (chartArray[4].toString() == "1") {
            map[4] = "气泵1温度传感器故障"
        }
        if (chartArray[5].toString() == "1") {
            map[5] = "气泵2温度传感器故障"
        }
        if (chartArray[6].toString() == "1") {
            map[6] = "系统温度传感器故障"
        }
        return map
    }


    //方向故障，左右前后
    /**
     * bit0:高度传感器超量程
    bit1:None
    bit2:高度传感器线序错误
    bit3:高度传感器测量范围过小
    bit4:高度传感器装反
    bit5:高度传感器故障
    bit6:气压传感器故障
    bit7:空气弹簧漏气
     */

    private fun getDirectionErrorCode(str : String) : HashMap<Int,String>{
        val directionMap = HashMap<Int, String>()
        val chartArray = str.toCharArray()
        if (chartArray[0].toString() == "1") {
            directionMap[0] = "高度传感器超量程"
        }
        if (chartArray[1].toString() == "1") {
            directionMap[1] = ""
        }
        if (chartArray[2].toString() == "1") {
            directionMap[2] = "高度传感器线序错误"
        }
        if (chartArray[3].toString() == "1") {
            directionMap[3] = "高度传感器测量范围过小"
        }
        if (chartArray[4].toString() == "1") {
            directionMap[4] = "高度传感器装反"
        }
        if (chartArray[5].toString() == "1") {
            directionMap[5] = "高度传感器故障"
        }
        if (chartArray[6].toString() == "1") {
            directionMap[6] = "气压传感器故障"
        }
        if(chartArray[7].toString() == "1"){
            directionMap[7] = "空气弹簧漏气"
        }
        return directionMap
    }


    /**
     * 气罐故障
     * bit0:气压传感器故障
    bit1:气罐压力过低
    bit2:气泵温度过高
    bit3:气罐无法充气
    bit4:气泵状态异常
     */
    val airBotMap = HashMap<Int, String>()
    private fun getAirBottleErrorCode(str : String) : HashMap<Int,String>{
        airBotMap.clear()
        val chartArray = str.toCharArray()
        if (chartArray[0].toString() == "1") {
            airBotMap[0] = "气压传感器故障"
        }
        if (chartArray[1].toString() == "1") {
            airBotMap[1] = "气罐压力过低"
        }
        if (chartArray[2].toString() == "1") {
            airBotMap[2] = "气泵温度过高"
        }
        if (chartArray[3].toString() == "1") {
            airBotMap[3] = "气罐无法充气"
        }
        if (chartArray[4].toString() == "1") {
            airBotMap[4] = "气泵状态异常"
        }
        return airBotMap
    }
}