package com.app.airmaster.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.app.airmaster.R
import com.blala.blalable.Utils
import timber.log.Timber

class CarErrorNotifyViewModel : ViewModel() {

    private var mContext : Context ?= null

    var carErrorDescList = SingleLiveEvent<List<String>>()

    private var list = mutableListOf<String>()



    @SuppressLint("TimberArgCount")
    fun getAllErrorDesc(context : Context, deviceErrorCode : Byte, airErrorCode : Byte, leftFrontCode : Byte, leftRearCode : Byte, rightFront : Byte, rightRearCode : Byte){
        this.mContext = context
        list.clear()

        //设备故障
       // val deviceErrorCode = it.deviceErrorCode
        val errorArray = Utils.byteToBit(deviceErrorCode)
        Timber.e("--------设备故障="+String.format("%02x",deviceErrorCode)+" "+errorArray)
        val resultMap = getDeviceErrorMsg(errorArray,context)
        resultMap.forEach {
            list?.add(it.value)
        }

        //气罐故障
      //  val airErrorCode = it.airBottleErrorCode
        val airArray = Utils.byteToBit(airErrorCode)
        Timber.e("--------气罐故障="+String.format("%02x",airErrorCode)+" "+airArray)


        val airMap = getAirBottleErrorCode(airArray,context)
        airMap.forEach {
            list?.add(it.value)
        }

        //左前
       // val leftFrontCode = it.leftFrontErrorCode
        val leftArray = Utils.byteToBit(leftFrontCode)
        Timber.e("--------左前故障="+String.format("%02x",leftFrontCode)+" "+leftArray)


        val leftMap = getWheelsError(leftArray,0)
        leftMap.forEach {
            list?.add(it.value)
        }

        //左后
       // val leftRearCode = it.leftRearErrorCode
        val leftRearArray = Utils.byteToBit(leftRearCode)
        Timber.e("--------左后故障="+String.format("%02x",leftRearCode)+" "+leftRearArray)

        val leftRearMap = getWheelsError(leftRearArray,2)
        leftRearMap.forEach {
            list?.add(it.value)
        }

        //右前
       // val rightFront = it.rightFrontErrorCode
        val rightFrontArray = Utils.byteToBit(rightFront)
        Timber.e("--------右前故障="+String.format("%02x",rightFront)+" "+rightFrontArray)

        val rightFrontMap = getWheelsError(rightFrontArray,1)
        rightFrontMap.forEach {
            list?.add(it.value)
        }

        //右后
      //  val rightRearCode = it.rightRearErrorCode
        val rightRearArray = Utils.byteToBit(rightRearCode)
        Timber.e("--------右后故障="+String.format("%02x",rightRearCode)+" "+rightRearArray)

        val rightRearMap = getWheelsError(rightRearArray,3)
        rightRearMap.forEach {
            list?.add(it.value)
        }
        carErrorDescList.postValue(list)

    }

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
    private val wheelMap = HashMap<Int,String>()
     fun getWheelsError(errorStr: String,code :Int) : HashMap<Int,String>{
        wheelMap.clear()
        if(mContext == null){
            return wheelMap
        }

        val wheel = getWheelType(code)
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            wheelMap[0] = wheel+ mContext!!.resources.getString(R.string.string_car_h_e_1)//"高度传感器超量程"
        }
        if (chartArray[1].toString() == "1") {
            wheelMap[1] = wheel+"None"
        }
        if (chartArray[2].toString() == "1") {
            wheelMap[2] = wheel+mContext!!.resources.getString(R.string.string_car_h_e_2)//"高度传感器线序错误"
        }
        if (chartArray[3].toString() == "1") {
            wheelMap[3] = wheel+mContext!!.resources.getString(R.string.string_car_h_e_3)//"高度传感器测量范围过小"
        }
        if (chartArray[4].toString() == "1") {
            wheelMap[4] = wheel+mContext!!.resources.getString(R.string.string_car_h_e_4)//"高度传感器装反"
        }
        if(chartArray[5].toString()=="1"){
            wheelMap[5] = wheel+mContext!!.resources.getString(R.string.string_car_h_e_5)//"高度传感器故障"
        }
        if(chartArray[6].toString()=="1"){
            wheelMap[5] = wheel+mContext!!.resources.getString(R.string.string_car_h_e_6)//"气压传感器故障"
        }
        if(chartArray[7].toString()=="1"){
            wheelMap[5] = wheel+mContext!!.resources.getString(R.string.string_car_h_e_7)//"空气弹簧漏气"
        }
        return wheelMap
    }


    private fun getWheelType(code: Int) : String{
        if(mContext == null){
            return ""
        }
        if(code ==0){
            return mContext!!.resources.getString(R.string.string_car_lr)//"左前"
        }
        if(code == 1){
            return mContext!!.resources.getString(R.string.string_car_rr)//"右前"
        }
        if(code == 2){
            return mContext!!.resources.getString(R.string.string_car_ll)//"左后"
        }
        return mContext!!.resources.getString(R.string.string_car_rl)//"右后"
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
     fun getAirBottleErrorCode(str : String,context: Context) : HashMap<Int,String>{
        airBotMap.clear()
        val chartArray = str.toCharArray()
        if (chartArray[0].toString() == "1") {
            airBotMap[0] = context.resources.getString(R.string.string_air_sensor_error)
        }
        if (chartArray[1].toString() == "1") {
            airBotMap[1] = context.resources.getString(R.string.string_air_pressure_low)
        }
        if (chartArray[2].toString() == "1") {
            airBotMap[2] = context.resources.getString(R.string.string_air_pump_temp_height)
        }
        if (chartArray[3].toString() == "1") {
            airBotMap[3] = context.resources.getString(R.string.string_air_no_input_air)
        }
        if (chartArray[4].toString() == "1") {
            airBotMap[4] = context.resources.getString(R.string.string_air_pump_state_error)
        }
        return airBotMap
    }

    //设备故障码
    val map = HashMap<Int, String>()
     fun getDeviceErrorMsg(errorStr: String,context: Context): HashMap<Int, String> {
        map.clear()
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            map[0] = context.resources.getString(R.string.string_sys_no_check)
        }
        if (chartArray[1].toString() == "1") {
            map[1] = context.resources.getString(R.string.string_acceleration_sensor_error)
        }
        if (chartArray[2].toString() == "1") {
            map[2] = context.resources.getString(R.string.string_battery_pressure_height)
        }
        if (chartArray[3].toString() == "1") {
            map[3] = context.resources.getString(R.string.string_battery_pressure_low)
        }
        if (chartArray[4].toString() == "1") {
            map[4] = context.resources.getString(R.string.string_air_pump1_temp_error)
        }
        if (chartArray[5].toString() == "1") {
            map[5] = context.resources.getString(R.string.string_air_pump2_temp_error)
        }
        if (chartArray[6].toString() == "1") {
            map[6] = context.resources.getString(R.string.string_sys_temp_error)
        }
        return map
    }
}