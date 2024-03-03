package com.app.airmaster.viewmodel

import com.app.airmaster.BaseApplication
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener

class WatchDeviceViewModel : CommViewModel() {

    //读取手表的固件版本信息
    var watchVersionData = SingleLiveEvent<DeviceBinVersionBean>()

    //获取固件版本
    fun getDeviceVersion() {
        val str = "8800000000000300001900"
        val byteArray = Utils.hexStringToByte(str)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(byteArray, object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {
                //880000000000102200 02 c019 000107 d91df96706a3 000101
                //88000000000022a100 1a 010010 c019 000107 d91df96706a3 00800001 0202000a02040301020304010001
                //88000000000022a100 1a 01 0010c019 00 01 07 d91df96706a3 00800001  020200 0a 0204 03 01020304010001
                //880000000000365400 1a 01 0007 00800001  03 06 03   02 0009 c019 02 fffff9 00010a  03000f fffe03fffffc112233fffffa445566  040009 fffe04fffffc778899
                if (data == null)
                    return
                if (data.size > 14) {
                    if (data[9].toInt().and(0xFF) == 26) {
                        val bean = DeviceBinVersionBean()

                        //bin文件的匹配码 00800001
                        val bindCode = String.format("%02x", data[13]) + String.format(
                            "%02x",
                            data[14]
                        ) + String.format("%02x", data[15]) + String.format("%02x", data[16])
                        //识别码
                        val identificationCode = String.format("%02x", data[25]) + String.format(
                            "%02x",
                            data[26]
                        ) + String.format("%02x", data[27]) + String.format("%02x", data[28])
                        //版本
                        val version =
                            "V" + data[29].toInt() + "." + data[30].toInt() + "." + data[31]
                        val versionInt = Utils.getIntFromBytes(0x00, data[29], data[30], data[31])
                        //型号
                        val model =
                            String.format("%02x", data[23]) + String.format("%02x", data[24])

                        bean.identificationCode = identificationCode
                        bean.productCode = model
                        bean.versionStr = version
                        bean.versionCode = versionInt
                        bean.binCode = bindCode
                        watchVersionData.postValue(bean)
                    }
                }

            }

        })
    }
}