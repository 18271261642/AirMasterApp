package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener


/**
 * 中转相关的
 */
class BridgeDfuViewModel : CommViewModel(){



    //3.10.7 APP端设擦写设备端指定的FLASH数据块-扩展接口
    fun setEraseDeviceFlash(){
        val array = ByteArray(2)
        array[0] = 0x08
        array[1] = 0x07
        val resultArray = Utils.getFullPackage(array)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {

            }

        })
    }
}