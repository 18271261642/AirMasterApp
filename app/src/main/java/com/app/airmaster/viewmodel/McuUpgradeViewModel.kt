package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import okhttp3.internal.and
import timber.log.Timber

class McuUpgradeViewModel : ViewModel(){


    //第一步，主机复位
    fun setFirstReset(){
        val data = byteArrayOf(0x01,0x1E,0x7F, 0xFA.toByte(),
            0xAF.toByte(), 0x00, 0x05, 0x04, 0x01, 0x41, 0x00, 0xB5.toByte()
        )
        val resultArray = Utils.getFullPackage(data)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                Timber.e("----------复位="+Utils.formatBtArrayToString(data))
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c1 01 34
                if(data.size == 20 && data[17].toInt().and(0xFF) == 193){   //复位成功
                    Timber.e("--------复位成功")
                }
            }

        })
    }


    //第二步，进入boot模式
    fun setSecondModel(){
        //7F FA AF 00 05 04 01 42 00 B4
        val data = byteArrayOf(0x01,0x1E,0x7F, 0xFA.toByte(),
            0xAF.toByte(), 0x00, 0x05, 0x04, 0x01, 0x42, 0x00, 0xB4.toByte()
        )
        val resultArray = Utils.getFullPackage(data)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                Timber.e("----------进入Boot模式="+Utils.formatBtArrayToString(data))
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c1 01 34
                //88 00 00 00 00 00 0c d6 03 0f 7f fa af 00 05 01 04 c2 01 33
                if(data.size == 20 && data[17].toInt().and(0xFF) == 194){   //进入Boot成功
                    Timber.e("--------进入Boot模式成功")

                }

            }

        })
    }


    fun setThirdData(binStr : ByteArray){

        val str = CarConstant.CAR_HEAD_BYTE_STR+"0014"+"0401"+"40"+Utils.getHexString(binStr)
        val crc = Utils.crcCarContentArray(str)

        val contentStr = str+crc
        val contentArray = Utils.hexStringToByte(contentStr)
        val resultArray = Utils.getFullPackage(contentArray)

        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                Timber.e("----------发送匹配码返回="+Utils.formatBtArrayToString(data))
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c1 01 34
                //88 00 00 00 00 00 0c d6 03 0f 7f fa af 00 05 01 04 c2 01 33


            }

        })
    }
}