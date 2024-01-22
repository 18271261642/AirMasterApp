package com.app.airmaster.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.ble.ota.BluetoothLeClass.OnWriteDataListener
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.app.airmaster.car.bean.ServerVersionInfoBean
import com.app.airmaster.listeners.OnCarVersionsListener
import com.app.airmaster.utils.GsonUtils
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

/**
 * 版本相关的
 */
class VersionViewModel : ViewModel(){

    //设备的固件版本
    var deviceVersionInfo = SingleLiveEvent<DeviceBinVersionBean>()


    //后台返回的信息
    var serverVersionInfo = SingleLiveEvent<ServerVersionInfoBean?>()


    //激活状态
    var activateState = SingleLiveEvent<Boolean>()

    //获取固件版本
    fun getDeviceVersion(){
        val str = "8800000000000300001900"
        val byteArray = Utils.hexStringToByte(str)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(byteArray,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {
                //880000000000102200 02 c019 000107 d91df96706a3 000101
                //88000000000022a100 1a 010010 c019 000107 d91df96706a3 00800001 0202000a02040301020304010001
                if(data == null)
                    return
                if(data.size >14){
                    if(data[9].toInt().and(0xFF) == 26){
                        val bean = DeviceBinVersionBean()
                        //型号
                        val model = String.format("%02x",data[13])+String.format("%02x",data[14])
                        //版本
                        val version = "V"+data[15].toInt()+"."+data[16].toInt()+"."+data[17]

                        //Bin文件的匹配码
                        val binStr = String.format("%02x",data[24])+String.format("%02x",data[25])+String.format("%02x",data[26])+String.format("%02x",data[27])

                        bean.productCode = model
                        bean.versionStr = version
                        bean.binCode = binStr
                        deviceVersionInfo.postValue(bean)
                    }
                }

            }

        })
    }


    //检查设备的固件版本
    fun getDeviceInfoData(life : LifecycleOwner, binCode : String,versionStr : String){
        EasyHttp.get(life).api("checkUpdate?broadcastId=0xfffe&matchCode=$binCode&firmwareVersionCode=$versionStr").request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getInt("code") == 200){
                    val data = jsonObject.getString("data")
                    val bean = GsonUtils.getGsonObject<ServerVersionInfoBean>(data)
                    serverVersionInfo.postValue(bean)

                }
            }

            override fun onFail(e: Exception?) {

            }

        })
    }


    //识别码
    fun setDeviceIdentificationCode(code : String){
        Timber.e("-------输入的字符串="+code)
        val array = Utils.changeStrToAscii(code)
        val st = Utils.formatBtArrayToString(array)
        Timber.e("--------转换后的="+st)

        val scrStr = "0014040130$st"
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data != null){
                    setCommRefreshDevice(data,0xB0.toByte())
                }
                //880000000000051b070201011e

                //8800000000000cd0030f7ffaaf00050104 b0 00 46
                if(data != null && data.size >17){
                    if(data[17].toInt().and(0xFF) == 176){
                        val state = data[18].toInt()
                        activateState.postValue(state==0)
                    }
                }

            }

        })
    }

    private fun setCommRefreshDevice(array : ByteArray,crc : Byte){
        if(array != null && array.size>15){
            val crcValue = crc.toInt().and(0xFF)
            //8800000000000cc6 030f 7f fa af 00 05 01 04 aa 01 4b
            //  Timber.e("-------气罐压力="+(array[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
            if((array[8].toInt().and(0xFF)) == 3 && (array[9].toInt().and(0xFF)) == 15 &&(array[17].toInt().and(0xFF) == crcValue
                        )){
                if(array[18].toInt().and(0xFF) == 1){
                    BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                }
            }
        }
    }

    //保存激活信息
    fun saveActivateRecord(life: LifecycleOwner,activateCode : String,productSn : String,userName : String,phone : String){
        EasyHttp.get(life).api("cdkey/record/save?cdkey=$activateCode&productSn=$productSn&username=$userName&phone=$phone").request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {

            }

            override fun onFail(e: Exception?) {

            }

        })
    }
}