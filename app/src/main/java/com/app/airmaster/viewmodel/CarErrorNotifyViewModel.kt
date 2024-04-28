package com.app.airmaster.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.blala.blalable.Utils
import timber.log.Timber

class CarErrorNotifyViewModel : ViewModel() {


    var carErrorDescList = SingleLiveEvent<List<String>>()

    private var list = mutableListOf<String>()



    @SuppressLint("TimberArgCount")
    fun getAllErrorDesc(deviceErrorCode : Byte, airErrorCode : Byte, leftFrontCode : Byte, leftRearCode : Byte, rightFront : Byte, rightRearCode : Byte){
        list.clear()

        //设备故障
       // val deviceErrorCode = it.deviceErrorCode
        val errorArray = Utils.byteToBit(deviceErrorCode)
        Timber.e("--------设备故障="+String.format("%02x",deviceErrorCode)+" "+errorArray)
        val resultMap = getDeviceErrorMsg(errorArray)
        resultMap.forEach {
            list?.add(it.value)
        }

        //气罐故障
      //  val airErrorCode = it.airBottleErrorCode
        val airArray = Utils.byteToBit(airErrorCode)
        Timber.e("--------气罐故障="+String.format("%02x",airErrorCode)+" "+airArray)


        val airMap = getAirBottleErrorCode(airArray)
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

        val leftRearMap = getWheelsError(leftArray,2)
        leftRearMap.forEach {
            list?.add(it.value)
        }

        //右前
       // val rightFront = it.rightFrontErrorCode
        val rightFrontArray = Utils.byteToBit(rightFront)
        Timber.e("--------右前故障="+String.format("%02x",rightFront)+" "+rightFrontArray)

        val rightFrontMap = getWheelsError(leftArray,1)
        rightFrontMap.forEach {
            list?.add(it.value)
        }

        //右后
      //  val rightRearCode = it.rightRearErrorCode
        val rightRearArray = Utils.byteToBit(rightRearCode)
        Timber.e("--------右后故障="+String.format("%02x",rightRearCode)+" "+rightRearArray)

        val rightRearMap = getWheelsError(leftArray,3)
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
    private fun getWheelsError(errorStr: String,code :Int) : HashMap<Int,String>{
        wheelMap.clear()
        val wheel = getWheelType(code)
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            wheelMap[0] = wheel+"高度传感器超量程"
        }
        if (chartArray[1].toString() == "1") {
            wheelMap[1] = wheel+"None"
        }
        if (chartArray[2].toString() == "1") {
            wheelMap[2] = wheel+"高度传感器线序错误"
        }
        if (chartArray[3].toString() == "1") {
            wheelMap[3] = wheel+"高度传感器测量范围过小"
        }
        if (chartArray[4].toString() == "1") {
            wheelMap[4] = wheel+"高度传感器装反"
        }
        if(chartArray[5].toString()=="1"){
            wheelMap[5] = wheel+"高度传感器故障"
        }
        if(chartArray[6].toString()=="1"){
            wheelMap[5] = wheel+"气压传感器故障"
        }
        if(chartArray[7].toString()=="1"){
            wheelMap[5] = wheel+"空气弹簧漏气"
        }
        return wheelMap
    }


    private fun getWheelType(code: Int) : String{
        if(code ==0){
            return "左前"
        }
        if(code == 1){
            return "右前"
        }
        if(code == 2){
            return "左后"
        }
        return "右后"
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
}