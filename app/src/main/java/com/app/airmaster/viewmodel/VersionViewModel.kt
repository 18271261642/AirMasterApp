package com.app.airmaster.viewmodel

import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.bean.CdkBean
import com.app.airmaster.ble.ota.BluetoothLeClass.OnWriteDataListener
import com.app.airmaster.car.bean.AppVoBean
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.app.airmaster.car.bean.ServerVersionInfoBean
import com.app.airmaster.car.bean.VersionParamsBean
import com.app.airmaster.listeners.OnCarVersionsListener
import com.app.airmaster.utils.GsonUtils
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.OnCarWatchBackListener
import com.blala.blalable.listener.WriteBackDataListener
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * 版本相关的
 */
class VersionViewModel : CommViewModel(){

    //app版本
    var appVersionData = SingleLiveEvent<AppVoBean?>()

    //设备的固件版本
    var deviceVersionInfo = SingleLiveEvent<DeviceBinVersionBean>()


    //后台返回的信息
    var serverVersionInfo = SingleLiveEvent<ServerVersionInfoBean?>()


    //激活状态
    var activateState = SingleLiveEvent<Boolean>()


    //芯片识别码
    var cdKeyCode = SingleLiveEvent<String>()


    //获取固件版本
    fun getDeviceVersion(isScreen : Boolean){
        Timber.e("-------isScreen="+isScreen)
        if(!isScreen){
            getWatchDeviceVersion()
            return
        }


        val str = "8800000000000300001900"
        val byteArray = Utils.hexStringToByte(str)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(byteArray,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {
                //880000000000102200 02 c019 000107 d91df96706a3 000101
                //88000000000022a100 1a 010010 c019 000107 d91df96706a3 00800001 0202000a02040301020304010001
                //88000000000022a100 1a 01 0010c019 00 01 07 d91df96706a3 00800001  020200 0a 0204 03 01020304010001
                //880000000000365400 1a 01 0007 00800001  03 06 03   02 0009 c019 02 fffff9 00010a  03000f fffe03fffffc112233fffffa445566  040009 fffe04fffffc778899
                if(data == null)
                    return
                if(data.size >14){
                    if(data[9].toInt().and(0xFF) == 26){
                        val bean = DeviceBinVersionBean()

                        //bin文件的匹配码 00800001
                        val bindCode = String.format("%02x",data[13])+String.format("%02x",data[14])+String.format("%02x",data[15])+String.format("%02x",data[16])
                        //识别码
                        val identificationCode = String.format("%02x",data[25])+String.format("%02x",data[26])+String.format("%02x",data[27])+String.format("%02x",data[28])
                        //版本
                        val version = "V"+data[29].toInt()+"."+data[30].toInt()+"."+data[31]
                        val versionInt = Utils.getIntFromBytes(0x00,data[29],data[30],data[31])
                        //型号
                        val model = String.format("%02x",data[23])+String.format("%02x",data[24])

                        //bluetooth nordic芯片
                        bean.identificationCode = identificationCode
                        bean.productCode = model
                        bean.versionStr = version
                        bean.versionCode =versionInt
                        bean.binCode = bindCode


                        //旋钮显示屏的mcu
                        bean.mcuBroadcastId = String.format("%02x",data[35])+String.format("%02x",data[36])
                        bean.mcuIdentificationCode = String.format("%02x",data[37])+String.format("%02x",data[38])+String.format("%02x",data[39])+String.format("%02x",data[40])
                        bean.mcuVersionCode = "V"+data[41].toInt()+"."+data[42].toInt()+"."+data[43]
                        bean.mcuVersionCodeInt = Utils.getIntFromBytes(0x00,data[41],data[42],data[43])

                        //客户主板MCU，其它mcu
                        bean.screenMcuBroadcastId =String.format("%02x",data[53])+String.format("%02x",data[54])
                        bean.screenMcuIdentificationCode = String.format("%02x",data[55])+String.format("%02x",data[56])+String.format("%02x",data[57])+String.format("%02x",data[58])
                        bean.screenVersionCode = "V"+data[59].toInt()+"."+data[60].toInt()+"."+data[61]
                        bean.screenMcuVersionCodeInt = Utils.getIntFromBytes(0x00,data[59],data[60],data[61])

                        bean.sourceStr = Utils.formatBtArrayToString(data)
                        deviceVersionInfo.postValue(bean)
                    }
                }

            }

        })
    }


    fun getWatchDeviceVersion(){
        val str = "8800000000000300001900"
        val byteArray = Utils.hexStringToByte(str)
        BaseApplication.getBaseApplication().bleOperate.writeCarWatchData(byteArray,object :
            OnCarWatchBackListener {
            override fun backWriteBytes(data: ByteArray?) {
                //880000000000102200 02 c019 000107 d91df96706a3 000101
                //88000000000022a100 1a 010010 c019 000107 d91df96706a3 00800001 0202000a02040301020304010001
                //88000000000022a100 1a 01 0010c019 00 01 07 d91df96706a3 00800001  020200 0a 0204 03 01020304010001
                //880000000000365400 1a 01 0007 00800001  03 06 03   02 0009 c019 02 fffff9 00010a  03000f fffe03fffffc112233fffffa445566  040009 fffe04fffffc778899
                if(data == null)
                    return
                if(data.size >14){
                    if(data[9].toInt().and(0xFF) == 26){
                        val bean = DeviceBinVersionBean()

                        //bin文件的匹配码 00800001
                        val bindCode = String.format("%02x",data[13])+String.format("%02x",data[14])+String.format("%02x",data[15])+String.format("%02x",data[16])
                        //识别码
                        val identificationCode = String.format("%02x",data[25])+String.format("%02x",data[26])+String.format("%02x",data[27])+String.format("%02x",data[28])
                        //版本
                        val version = "V"+data[29].toInt()+"."+data[30].toInt()+"."+data[31]
                        val versionInt = Utils.getIntFromBytes(0x00,data[29],data[30],data[31])
                        //型号
                        val model = String.format("%02x",data[23])+String.format("%02x",data[24])

                        bean.identificationCode = identificationCode
                        bean.productCode = model
                        bean.versionStr = version
                        bean.versionCode =versionInt
                        bean.binCode = bindCode

                        Timber.e("---------手表版本:"+Gson().toJson(bean))

                        deviceVersionInfo.postValue(bean)
                    }
                }
            }

        })
    }


    //读取芯片序列号
    fun getDeviceCdKey(){
        val scrStr = "000504010700"
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                // 8800000000001b30030f7ffaaf00140104873030303030303030303000000000000080
                if(data == null){
                    return
                }
                if(data.size>26 && data[17].toInt().and(0xFF) == 135){
                    val array = ByteArray(16)
                    System.arraycopy(data,18,array,0,16)
                    Timber.e("------array="+Utils.formatBtArrayToString(array))
                    val sb = StringBuffer()
                    array.forEach {
                        if(it.toInt() != 0){
                            sb.append(String.format("%02x",it.toInt()))
                        }
                    }
                    val resultArray = Utils.hexStringToByte(sb.toString())
                    val cdkStr = String(resultArray)
                    cdKeyCode.postValue(cdkStr)
                }


            }
        })
    }


    //检查设备的固件版本
    fun getDeviceInfoData(isWatch : Boolean,life : LifecycleOwner,matchCode : String,list : MutableList<VersionParamsBean.ParamsListBean>){

        val versionParamsBean = VersionParamsBean()
        versionParamsBean.matchCode = matchCode

//        val list = ArrayList<VersionParamsBean.ParamsListBean>()
//
//        val touchBean = VersionParamsBean.ParamsListBean()
//        touchBean.identificationCode = binCode
//        touchBean.versionCode = versionCode.toString()
//        touchBean.broadcastId = broadcastId
//        list.add(touchBean)

        versionParamsBean.mcuList = list
        val str = Gson().toJson(versionParamsBean)
        Timber.e("----------固件提交参数="+str)
        val requestBody = RequestBody.create(JSON, str)
        EasyHttp.post(life)
            .api("checkUpdate")
            .body(requestBody)
            .request(object : OnHttpListener<String>{
                override fun onSucceed(result: String?) {
                    val jsonObject = JSONObject(result)
                    if(jsonObject.getInt("code") == 200){
                        val data = jsonObject.getString("data")
                        val bean = GsonUtils.getGsonObject<ServerVersionInfoBean>(data)
                        if(bean != null){
                            bean.isCarWatch = isWatch
                        }
                        serverVersionInfo.postValue(bean)

                    }
                }

                override fun onFail(e: Exception?) {

                }

            })

//
//
//        EasyHttp.get(life).api("checkUpdate?identificationCode=$binCode&matchCode=$matchCode").request(object : OnHttpListener<String>{
//            override fun onSucceed(result: String?) {
//                val jsonObject = JSONObject(result)
//                if(jsonObject.getInt("code") == 200){
//                    val data = jsonObject.getString("data")
//                    val bean = GsonUtils.getGsonObject<ServerVersionInfoBean>(data)
//                    serverVersionInfo.postValue(bean)
//
//                }
//            }
//
//            override fun onFail(e: Exception?) {
//
//            }
//
//        })
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
                        activateState.postValue(state==1)
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
    fun saveActivateRecord(life: LifecycleOwner,activateCode : String,productSn : String){



        EasyHttp.post(life).api(CdkBean().setParams(activateCode,productSn)).request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {

            }

            override fun onFail(e: Exception?) {

            }

        })
    }




    //检查App版本
    fun checkAppVersion(life: LifecycleOwner,versionCode: Int) {
        val map = HashMap<String,Any>()
        map["appVersion"] = versionCode
        map["appType"] = 1
        val requestBody = RequestBody.create(JSON, Gson().toJson(map))
        EasyHttp.post(life).api("checkUpdate")
            .body(requestBody)
            .request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getInt("code") == 200){
                    val data = jsonObject.getJSONObject("data")
                    val voStr = data.getString("appVo")
                    val bean = GsonUtils.getGsonObject<AppVoBean>(voStr)
                    appVersionData.postValue(bean)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })


    }

}