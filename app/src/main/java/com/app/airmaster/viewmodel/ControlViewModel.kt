package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.car.bean.AutoSetBean
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import timber.log.Timber

/**
 * Create by sjh
 * @Date 2023/12/29
 * @Desc
 */
class ControlViewModel : ViewModel() {


    //获取自动返回的状态
    var autoSetBeanData = SingleLiveEvent<AutoSetBean>()






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
        Timber.e("---map="+map.toString())
        var keyCode = 0
        var valueCode = 0
        map.forEach {
            keyCode = it.key
            valueCode = it.value
        }

        val timeArray = Utils.intToSecondByteArrayHeight(1000)
        val timeStr = com.app.airmaster.ble.ota.Utils.bytesToHexString(timeArray)
        val scrStr = "0008040112"+String.format("%02x",keyCode)+String.format("%02d",(valueCode))+timeStr
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000cd6030f7ffaaf00050104920163
            if(it != null && it.size>15){
                Timber.e("-------气罐压力="+(it[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                if((it[8].toInt().and(0xFF)) == 3 && (it[9].toInt().and(0xFF)) == 15 &&(it[17].toInt().and(0xFF) == 146)){
                    if(it[18].toInt().and(0xFF) == 1){
                        BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                    }
                }
            }
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
            //8800000000000cc2030f7ffaaf00050104a9014c
            if(it != null && it.size>15){
                //8800000000000cc6 030f 7f fa af 00 05 01 04 aa 01 4b
                Timber.e("-------气罐压力="+(it[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                if((it[8].toInt().and(0xFF)) == 3 && (it[9].toInt().and(0xFF)) == 15 &&(it[17].toInt().and(0xFF) == 169
                            )){
                    if(it[18].toInt().and(0xFF) == 1){
                        BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                    }
                }
            }
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
            if(it != null && it.size>15){
                //8800000000000cc6 030f 7f fa af 00 05 01 04 aa 01 4b
                Timber.e("-------气罐压力="+(it[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                if((it[8].toInt().and(0xFF)) == 3 && (it[9].toInt().and(0xFF)) == 15 &&(it[17].toInt().and(0xFF) == 170
                            )){
                    if(it[18].toInt().and(0xFF) == 1){
                        BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                    }
                }
            }
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


    //设置高度尺
    fun setHeightRuler( leftFront : Int,leftRear : Int,rightFront : Int,rightRear : Int ){
        val scrStr = "000804012E"+String.format("%02x",leftFront)+String.format("%02x",leftRear)+String.format("%02x",rightFront)+String.format("%02x",rightRear)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){

        }
    }

    fun writeCommonFunction(){
        BaseApplication.getBaseApplication().bleOperate.setCommAllParams(object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data != null && data.size>15){

                    if((data[8].toInt().and(0xFF)) == 3 && (data[9].toInt().and(0xFF)) == 15 &&(data[17].toInt().and(0xFF) ==145 )){

                        val autoBean = AutoSetBean()

                        //高度记忆模式 0g高度记忆；1压力记忆
                        val modelType = data[18].toInt()
                        autoBean.modelType = modelType
                        //ACC启停定时进入休眠时间(单位分钟)
                        val sleepTime = data[19].toInt().and(0xFF)
                        autoBean.sleepTime = sleepTime
                        //电池高压保护开关
                        val isHeightVoltage = data[20].toInt()==1
                        autoBean.isHeightVoltage = isHeightVoltage
                        //电池低压保护门限(单位0.1V)
                        val lowVoltage = data[21].toInt().and(0xFF)
                        autoBean.lowVoltage =lowVoltage
                        //点火联动预置位 0:关闭
                        //1~4:关联预置位
                        val accTurnOnValue = data[22].toInt().and(0xFF)
                        autoBean.accTurnOnValue = accTurnOnValue
                        val accTurnOffValue = data[23].toInt().and(0xFF)
                        autoBean.accTurnOffValue = accTurnOffValue
                        //水平静止状态下超过5S,自动预置位调节开关 0不能 1能
                        val presetPosition = data[104].toInt().and(0xFF)
                        autoBean.presetPosition = presetPosition
                        //气罐高压值(单位PSI)
                        val gasTankHeightPressure = data[105].toInt().and(0xFF)
                        autoBean.gasTankHeightPressure = gasTankHeightPressure
                        //气罐低压值
                        val gasTankLowPressure = data[106].toInt().and(0xFF)
                        autoBean.gasTankLowPressure = gasTankLowPressure
                        //气罐水分离模式
                        val gasTankWaterModel = data[107].toInt().and(0xFF)
                        autoBean.gasTankWaterModel = gasTankWaterModel
                        //最低预置位
                        val lowestPosition = data[108].toInt().and(0xFF)
                        autoBean.lowestPosition = lowestPosition
                        //ACC模式
                        val accModel = data[109].toInt().and(0xFF)
                        autoBean.accModel = accModel
                        //运动中-左前最低保护气压
                        val leftFrontProtectPressure = data[110].toInt().and(0xFF)
                        autoBean.leftFrontProtectPressure = leftFrontProtectPressure
                        //运动中-右前最低保护气压
                        val rightFrontProtectPressure = data[111].toInt().and(0xFF)
                        autoBean.rightFrontProtectPressure = rightFrontProtectPressure
                        //运动中-左后最低保护气压
                        val leftRearProtectPressure = data[112].toInt().and(0xFF)
                        autoBean.leftRearProtectPressure = leftRearProtectPressure
                        //运动中-右后最低保护气压
                        val rightRearProtectPressure = data[113].toInt().and(0xFF)
                        autoBean.rightRearProtectPressure = rightRearProtectPressure
                        //自动气压调平功能
                        val pressureBalance = data[114].toInt().and(0xFF)
                        autoBean.pressureBalance = pressureBalance
                        //自动气压调平功能-级别
                        val autoPressureBalanceLevel = data[115].toInt().and(0xFF)
                        autoBean.autoPressureBalanceLevel = autoPressureBalanceLevel
                        Timber.e("----------autoBean=$autoBean")
                        autoSetBeanData.postValue(autoBean)
                    }
                }
            }

        })
        //                     10                    17             22                             10                             20                            30                             40                             50                             60                             70                             80
        //880000000000772e 030f7f fa af 00 70 01 04  91 00 0a 01 6e 00  00 00 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 00 00  00 00 00 00 00 0064 00 44 00  11 00 11 00 00 00 00 00 00 00  00 00 1e 00 1e 00 1e 00 1e 00  32 00 32 00 32 00 32 00 46 00  46 00 46 00 46 00 7d 00 4d 00  46 00 46 00 14 00 14 00 14 00  14 00a08c00010047472424010000000000000000000000b5
        //880000000000772e 030f7f fa af 00 70 01 04  91 00 0a 01 6e 00  00 00 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 00 00  00 00 00 00 00 00 64 00 44 00  11 00 11 00 00 00 00 00 00 00  00 00 1e 00 1e 00 1e 00 1e 00  32 00 32 00 32 00 32 00 46 00  46 00 46 00 46 00 7d 00 4d 00  46 00 46 00 14 00 14 00 14 00  14 00 a0 8c 00 01 00 47 47 24 24 010000000000000000000000b5
        //880000000000772E 030F7F FA AF 00 70 01 04 91 000A016E000000000000000000000000000000000000000000000000000000640044001100110000000000000000001E001E001E001E00320032003200320046004600460046007D004D00460046001400140014001400A08C00010047472424010000000000000000000000B5
        //手动打气  自动调平衡  3通知 4 主动干燥
    }
}