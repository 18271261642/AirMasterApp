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
        val scrStr = "00051D"+String.format("%02x",gear)
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+"00051D"+String.format("%02x",gear)+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }

    }


    //一键低趴
    fun setOneGearReset(){
        val scrStr = "00052500"
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }
}