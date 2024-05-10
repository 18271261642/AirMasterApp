package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.bean.CheckBean
import com.app.airmaster.car.bean.AutoSetBean
import com.app.airmaster.utils.MmkvUtils
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.OnCarWriteBackListener
import com.blala.blalable.listener.WriteBackDataListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Create by sjh
 * @Date 2023/12/29
 * @Desc
 */
open class ControlViewModel : CommViewModel() {


    //获取自动返回的状态
    var autoSetBeanData = SingleLiveEvent<AutoSetBean>()


    var commControlStatus = SingleLiveEvent<Int>()


     var airLog = SingleLiveEvent<String>()

    //自检返回
    var checkBackDataMap = SingleLiveEvent<CheckBean>()


    var manualCheckData = SingleLiveEvent<CheckBean>()

    //清除所有警告
    fun clearAllWarring(){
        val scrStr = "000404012B"
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            if(it != null){
                setCommRefreshDevice(it,0xAB.toByte())
            }
        }
    }




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
            if(it != null){
                //setCommRefreshDevice(it,0x9A.toByte())
            }
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
            if(it != null){
                setCommRefreshDevice(it,0xA3.toByte())
            }
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
            if(it != null){
                setCommRefreshDevice(it,0xA4.toByte())
            }
        }
    }



    //设置预置位主动校准
    fun setPreAutoIsEnable(isOpen : Boolean){
        val scrStr = "000504011B"+String.format("%02x",if(isOpen) 1 else 0)
        val crc = Utils.crcCarContentArray(scrStr)

        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            if(it != null){
                setCommRefreshDevice(it,0x9B.toByte())
            }
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
            if(it != null){
                setCommRefreshDevice(it,0x93.toByte())
            }
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
            if(it != null){
                setCommRefreshDevice(it,0xA5.toByte())
            }
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

        val timeArray = Utils.intToSecondByteArrayHeight(500)
        val timeStr = com.app.airmaster.ble.ota.Utils.bytesToHexString(timeArray)
       // val scrStr = "0008040112"+String.format("%02x",keyCode)+String.format("%02d",(valueCode))+timeStr
        var scrStr = ""
//        if(keyCode == -1 || keyCode == -2){
//            scrStr = "0008040112"+String.format("%02x",if(keyCode==-1) 4 else 5)+String.format("%02d",(valueCode))+timeStr
//        }else{
//            scrStr = "000804012F"+String.format("%02x",keyCode)+String.format("%02d",(valueCode))+timeStr
//
//        }
        scrStr = "000804012F"+String.format("%02x",keyCode)+String.format("%02d",(valueCode))+timeStr
        //val scrStr = "000804012F"+String.format("%02x",keyCode)+String.format("%02d",(valueCode))+timeStr


        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)

        airLog.postValue("发送指令="+map.toString()+" "+str+"\n")

        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000cd6030f7ffaaf00050104920163
            if(it != null && it.size>15){
                Timber.e("-------气罐压力="+(it[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[17].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                if((it[8].toInt().and(0xFF)) == 3 && (it[9].toInt().and(0xFF)) == 15 &&(it[17].toInt().and(0xFF) == 175)){
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
            if(it != null){
                setCommRefreshDevice(it,0x96.toByte())
            }
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
            if(it != null){
                setCommRefreshDevice(it,0x99.toByte())
            }
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
            //8800000000000ce6030f7ffaaf000501049a015b
            if(it != null){
                setCommRefreshDevice(it,0x9A.toByte())
            }
        }
    }


    //设置高度记忆模式
    fun setHeightMemory(isHeightMemory : Boolean){
        val scrStr = "0005040115"+String.format("%02x",if(isHeightMemory) 0 else 1)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000ce6030f7ffaaf000501049a015b
            if(it != null){
                setCommRefreshDevice(it,0x95.toByte())
                GlobalScope.launch {
                    delay(1000)
                    commControlStatus.postValue(1)
                }
            }
        }
    }

    //设置高度记忆的档位1~5
    fun setLogMemoryData(gear: Int){
        val scrStr = "000504011C"+String.format("%02x",gear)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000ce6030f7ffaaf000501049a015b
            if(it != null){
                setCommRefreshDevice(it,0x9C.toByte())
                GlobalScope.launch {
                    delay(1000)
                    commControlStatus.postValue(1)
                }
            }
        }
    }



    fun setAirOutData(isOpen : Boolean){
        val scrStr = "0005040126"+String.format("%02x",if(isOpen) 0 else 1)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000ce6030f7ffaaf000501049a015b
            if(it != null){
                setCommRefreshDevice(it,0xA6.toByte())
                GlobalScope.launch {
                    delay(1000)
                    commControlStatus.postValue(1)
                }
            }
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


    //自动平衡开关
    fun setAirBalanceSwitch(open : Boolean){
        val scrStr = "000504012C"+String.format("%02x",if(open) 1 else 0)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            if(it != null){
                setCommRefreshDevice(it,0xAC.toByte())
            }
        }
    }


    //自动平衡等级
    fun setAirBalanceLevel(level : Int){
        val scrStr = "000504012D"+String.format("%02x",level)
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            if(it != null){
                setCommRefreshDevice(it,0xAD.toByte())
            }
        }
    }

    private var hashMap = HashMap<Int,AutoSetBean.GearBean>()

    fun writeCommonFunction(){
        BaseApplication.getBaseApplication().bleOperate.setCommAllParams(object : OnCarWriteBackListener{

            override fun backWriteBytes(data: ByteArray?) {
                if(data != null && data.size>=115){

                    if((data[8].toInt().and(0xFF)) == 3 && (data[9].toInt().and(0xFF)) == 15 &&(data[17].toInt().and(0xFF) ==145 )){

                        hashMap.clear()

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

                        //处理档位预置位
                        //一档
                        val gear1LeftFront = Utils.getIntFromBytes(data[24],data[25])
                        val gear1RightFront = Utils.getIntFromBytes(data[26],data[27])
                        val gear1LeftRear = Utils.getIntFromBytes(data[28],data[29])
                        val gear1RightRear = Utils.getIntFromBytes(data[30],data[31])
                        val gear1 = AutoSetBean.GearBean(gear1LeftFront,gear1RightFront,gear1LeftRear,gear1RightRear)
                        hashMap[1] = gear1


                        //二档
                        val gear2LeftFront = Utils.getIntFromBytes(data[32],data[33])
                        val gear2RightFront = Utils.getIntFromBytes(data[34],data[35])
                        val gear2LeftRear = Utils.getIntFromBytes(data[36],data[37])
                        val gear2RightRear = Utils.getIntFromBytes(data[38],data[39])
                        val gear2 = AutoSetBean.GearBean(gear2LeftFront,gear2RightFront,gear2LeftRear,gear2RightRear)
                        hashMap[2] = gear2

                        //三挡
                        val gear3LeftFront = Utils.getIntFromBytes(data[40],data[41])
                        val gear3RightFront = Utils.getIntFromBytes(data[42],data[43])
                        val gear3LeftRear = Utils.getIntFromBytes(data[44],data[45])
                        val gear3RightRear = Utils.getIntFromBytes(data[46],data[47])
                        val gear3 = AutoSetBean.GearBean(gear3LeftFront,gear3RightFront,gear3LeftRear,gear3RightRear)
                        hashMap[3] = gear3

                        //四挡
                        val gear4LeftFront = Utils.getIntFromBytes(data[48],data[49])
                        val gear4RightFront = Utils.getIntFromBytes(data[50],data[51])
                        val gear4LeftRear = Utils.getIntFromBytes(data[52],data[53])
                        val gear4RightRear = Utils.getIntFromBytes(data[54],data[55])
                        val gear4 = AutoSetBean.GearBean(gear4LeftFront,gear4RightFront,gear4LeftRear,gear4RightRear)
                        hashMap[4] = gear4

                        //低趴
                        val gearLowLeftFront = Utils.getIntFromBytes(data[56],data[57])
                        val gearLowRightFront = Utils.getIntFromBytes(data[58],data[59])
                        val gearLowLeftRear = Utils.getIntFromBytes(data[60],data[61])
                        val gearLowRightRear = Utils.getIntFromBytes(data[62],data[63])
                        val gear0 = AutoSetBean.GearBean(gearLowLeftFront,gearLowRightFront,gearLowLeftRear,gearLowRightRear)
                        hashMap[0] = gear0

                        autoBean.gearHashMap = hashMap

                        //水平静止状态下超过5S,自动预置位调节开关 0不能 1能
                        val presetPosition = data[104].toInt().and(0xFF)
                        autoBean.presetPosition = presetPosition
                        //气罐高压值(单位PSI)
                        val gasTankHeightPressure = data[105].toInt().and(0xFF)
                        autoBean.gasTankHeightPressure = gasTankHeightPressure

                        MmkvUtils.saveMaxPressureValue(gasTankHeightPressure)

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


    /**
     * 用于判断设置成功后刷新设备状态
     */
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


    fun clearManualCheck(){
        BaseApplication.getBaseApplication().bleOperate.setClearCheck()
    }
    fun setManualCheckBack(){
        BaseApplication.getBaseApplication().bleOperate.setCarCheckDataListener {
            if(it != null && it.size>18){
                //88000000000016c8030f7ffaaf000f0104 a7 01 01 02 000000000000000041
                    if(it[17].toInt().and(0xFF) == 167){
                        val bean = CheckBean()
                        //步骤 1，2,3步骤
                        val step = it[19].toInt()
                        //检测状态 0失败；1成功，2进行中
                        val state = it[20].toInt()
                        //异常编码
                        val errorCode = it[21].toInt()
                        bean.checkStep = step
                        bean.checkStatus = state
                        bean.errorCode = errorCode
                        bean.backHex = Utils.getHexString(it)+"\n"
                        Timber.e("-------自检="+bean.toString())
                        //checkBackDataMap.postValue(bean)
                        manualCheckData?.postValue(bean)
                    }
                //  setCommRefreshDevice(it,0x9A.toByte())
            }
        }
    }


    /**
     * 进入或退出自检
     * @param into 是否进入自检模式
     * @param auto 00自动；01手动
     */
    fun intoOrExit(into : Boolean,auto : Boolean){
        var scrStr : String ?= null
        if(auto){
            scrStr = "00050401"+(if(into) "1E" else "1F")+"00"
        }else{
            scrStr = "00050401"+(if(into) "1E01" else "1F00")
        }

        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000ce6030f7ffaaf000501049a015b
            if(it != null && it.size>18){
                //88000000000016c8030f7ffaaf000f0104 a7 01 01 02 000000000000000041
                if(into){
                    if(it[17].toInt().and(0xFF) == 167){
                        val bean = CheckBean()
                        //步骤 1，2,3步骤
                        val step = it[19].toInt()
                        //检测状态 0失败；1成功，2进行中
                        val state = it[20].toInt()
                        //异常编码
                        val errorCode = it[21].toInt()
                        bean.checkStep = step
                        bean.checkStatus = state
                        bean.errorCode = errorCode
                        bean.backHex = Utils.getHexString(it)+"\n"
                        Timber.e("-------自检="+bean.toString())
                        checkBackDataMap.postValue(bean)

                    }
                }
              //  setCommRefreshDevice(it,0x9A.toByte())
            }
        }
    }


    /**
     * //无
    1   //电池电压异常
    2   //ACC未启动
    3   //气罐气压传感器故障
    4   //自检超时
    5   //左前高度传感器超量程
    6   //右前高度传感器超量程
    7   //左后高度传感器超量程
    8   //右后高度传感器超量程
    9    //左前气压传感器故障
    10   //右前气压传感器故障
    11   //左后气压传感器故障
    12   //右后气压传感器故障
    13   //左前高度传感器故障
    14   //右前高度传感器故障
    15   //左后高度传感器故障
    16   //右后高度传感器故障
    17   //左前高度传感器测量范围过小
    18   //右前高度传感器测量范围过小
    19   //左后高度传感器测量范围过小
    20   //右后高度传感器测量范围过小
    21   //左前高度传感器线序错误
    22   //右前高度传感器线序错误
    23   //左后高度传感器线序错误
    24   //右后高度传感器线序错误
     */

    private fun getDesc(code : Int) : String? {
        val map = HashMap<Int,String>()
        map[0] = ""
        map[1] = "电池电压异常"
        map[2] = "ACC未启动"
        map[3] = "ACC未启动"
        map[4] = "自检超时"
        map[5] = "左前高度传感器超量程"
        map[6] = "右前高度传感器超量程"
        map[7] = "左后高度传感器超量程"
        map[8] = "右后高度传感器超量程"
        map[9] = "左前气压传感器故障"
        map[10] = "右前气压传感器故障"
        map[11] = "左后气压传感器故障"
        map[12] = "右后气压传感器故障"
        map[13] = "左前高度传感器故障"
        map[14] = "右前高度传感器故障"
        map[15] = "左后高度传感器故障"
        map[16] = "右后高度传感器故障"
        map[17] = "左前高度传感器测量范围过小"
        map[18] = "右前高度传感器测量范围过小"
        map[19] = "左后高度传感器测量范围过小"
        map[20] = "右后高度传感器测量范围过小"
        map[21] = "左前高度传感器线序错误"
        map[22] = "右前高度传感器线序错误"
        map[23] = "左后高度传感器线序错误"
        map[24] = "右后高度传感器线序错误"

        return map.get(code)

    }
}