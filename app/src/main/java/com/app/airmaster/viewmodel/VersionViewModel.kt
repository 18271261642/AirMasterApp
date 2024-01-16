package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.ble.ota.BluetoothLeClass.OnWriteDataListener
import com.app.airmaster.listeners.OnCarVersionsListener
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener

/**
 * 版本相关的
 */
class VersionViewModel : ViewModel(){

    //设备的固件版本
    var deviceVersionInfo = SingleLiveEvent<HashMap<String,String>>()


    //获取固件版本
    fun getDeviceVersion(){
        val str = "8800000000000300000100"
        val byteArray = Utils.hexStringToByte(str)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(byteArray,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {
                //880000000000102200 02 c019 000107 d91df96706a3 000101
                if(data == null)
                    return
                if(data.size >14){
                    if(data[9].toInt() == 2){
                        //型号
                        val model = String.format("%02x",data[10])+String.format("%02x",data[11])
                        //版本
                        val version = "V"+data[12].toInt()+"."+data[13].toInt()+"."+data[14]
                        val hashMap = HashMap<String,String>()
                        hashMap[model] = version
                        deviceVersionInfo.postValue(hashMap)
                    }
                }

            }

        })
    }
}