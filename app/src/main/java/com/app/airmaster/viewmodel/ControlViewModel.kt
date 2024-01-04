package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant

/**
 * Create by sjh
 * @Date 2023/12/29
 * @Desc
 */
class ControlViewModel : ViewModel() {



    //设置档位
    fun getHomeGear(gear : Int){
        val bArray = ByteArray(2)
        bArray[0] = 0x1D
        bArray[1] = gear.toByte()
        val scrStr = "000504011D"+String.format("%02x",gear)
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+"000504011D"+String.format("%02x",gear)+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }

    }


    //汽水分离模式
    fun setMoistureModel(model : Int){
        val scrStr = "0005040123"+String.format("%02x",model)
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }


    //主动干燥
    fun setActiveDrying(){
        val time = 15000
        val timeArray = Utils.intToSecondByteArrayHeight(time)
        val scrStr = "0006040124"+Utils.getHexString(timeArray)
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }



    //气罐手动打气
    fun setManualAerate(isStart : Boolean){
        val scrStr = "0005040113"+String.format("%02x",if(isStart) 0 else 1)
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }


    //一键低趴
    fun setOneGearReset(){
        val scrStr = "000504012500"
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }



    //手动调整轮子充气或放气
    fun setManualOperation(map : HashMap<Int,Int>){
        var keyCode = 0
        var valueCode = 0
        map.forEach {
            keyCode = it.key
            valueCode = it.value
        }

        val timeArray = Utils.intToSecondByteArrayHeight(1000)
        val timeStr = com.app.airmaster.ble.ota.Utils.bytesToHexString(timeArray)
        val scrStr = "0005040112"+String.format("%02x",keyCode)+String.format("%02d",(valueCode+1))+timeStr
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }

    }


    //设置ACC启停定时进入休眠时间
    fun setAccModel(time : Int){
        val scrStr = "0005040116"+String.format("%02x",time)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }

    //是否进入维修模式维修模式
    fun setRepairModel(into : Boolean){
        val scrStr = "0005040129"+String.format("%02x",if(into) 1 else 0)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }

    //运行中最低保护气压
    fun setRunLowPressure(front : Int,rear : Int){
        val frontStr = String.format("%02x",front)
        val rearStr = String.format("%02x",rear)
        val scrStr = "000804012A$frontStr$frontStr$rearStr$rearStr"
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }


    //点火联动设置
    fun setStartUpSetting(level :Int){
        val scrStr = "0005040119"+String.format("%02x",level)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }

    //熄火设置
    fun setStallSetting(level :Int){
        val scrStr = "000504011A"+String.format("%02x",level)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }
}