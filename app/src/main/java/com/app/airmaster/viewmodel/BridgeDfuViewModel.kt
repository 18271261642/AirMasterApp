package com.app.airmaster.viewmodel

import com.app.airmaster.BaseApplication
import com.blala.blalable.Utils
import com.blala.blalable.keyboard.KeyBoardConstant
import com.blala.blalable.listener.WriteBackDataListener


/**
 * 中转相关的
 */
class BridgeDfuViewModel : CommViewModel(){



    //3.10.7 APP端设擦写设备端指定的FLASH数据块-扩展接口
    fun setEraseDeviceFlash(){
        val startEndArray = mutableListOf<Byte>(0x00, 0xff.toByte(),
            0xff.toByte(), 0xff.toByte(),0x00, 0xff.toByte(), 0xff.toByte(), 0xff.toByte()
        )

        val array = ByteArray(2)
        array[0] = 0x08
        array[1] = 0x07

        val keyStr = KeyBoardConstant.keyValue(array,startEndArray.toByteArray())
        val tempArray = Utils.hexStringToByte(keyStr)
        val resultArray = Utils.getFullPackage(tempArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                //88 00 00 00 00 00 03 02 08 08 02
                if(data?.size == 11 && data[9].toInt() == 8 && data[10].toInt() == 2){

                }
            }

        })
    }
}