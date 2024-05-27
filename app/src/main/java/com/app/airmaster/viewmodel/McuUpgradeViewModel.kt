package com.app.airmaster.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.utils.CalculateUtils
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.and
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class McuUpgradeViewModel : ViewModel(){

    //升级进度
    var mcuDfuProgress = SingleLiveEvent<String>()
    //读取校验值状态
    var readCheckValue = SingleLiveEvent<Int>()
    //超时
    var mcuBootTimeOut = SingleLiveEvent<Boolean>()


    //最后两个byte
    var lastDoubleByte = ByteArray(2)
    //匹配码
    private var matchByteArray = ByteArray(16)
    //原始bin文件包集合
    private var sourceBinList = mutableListOf<ByteArray>()


    private var isReset = false
    private var isIntoBoot = false
    private var isMatchStatus = false

    private val operateHandlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //复位
            if(msg.what == 0x00){
                setFirstReset()
            }

            //进入boot
            if(msg.what == 0x01){
                setSecondModel()
            }
            if(msg.what == 0x02){
                setThirdData()
            }

        }
    }

    fun setInit(){
        sourceBinList.clear()
        isReset =false
        isIntoBoot =false
        isMatchStatus = false

    }


    //第一步，主机复位
    fun setFirstReset(){
        operateHandlers.sendEmptyMessageDelayed(0x00,2000)

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
                    isReset = true
                    operateHandlers.removeMessages(0x00)
                    setSecondModel()
                }
            }

        })
    }


    //第二步，进入boot模式
    fun setSecondModel(){
        operateHandlers.sendEmptyMessageDelayed(0x01,2000)
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
                //88 00 00 00 00 00 0c d6 03 0f 7f fa af 00 05 01 04 c2 01 33
                if(data.size == 20 && data[17].toInt().and(0xFF) == 194){   //进入Boot成功
                    Timber.e("--------进入Boot模式成功")
                    isIntoBoot = true
                    operateHandlers.removeMessages(0x01)
                    setThirdData()
                }

            }

        })
    }


    //第三步，发送匹配码
    fun setThirdData(){
        operateHandlers.sendEmptyMessageDelayed(0x02,2000)
        val binStr = matchByteArray
        val str = "0014"+"0401"+"40"+Utils.getHexString(binStr)
        val crc = Utils.crcCarContentArray(str)

        val contentStr = "011E"+CarConstant.CAR_HEAD_BYTE_STR+str+crc
        val contentArray = Utils.hexStringToByte(contentStr)
        val resultArray = Utils.getFullPackage(contentArray)

        //584C3730315F53545F4D5F3030310000
        val tmpStr = "8800000000001b00011e7ffaaf0014040140584C3730315F53545F4D5F3030310000ca"
        val tmpR = Utils.hexStringToByte(tmpStr)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                Timber.e("----------发送匹配码返回="+Utils.formatBtArrayToString(data))
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c0 01 35
                if(data.size == 20 && data[17].toInt().and(0xFF) == 192 && data[18].toInt().and(0xFF) == 1){
                    isMatchStatus = true
                    operateHandlers.removeMessages(0x02)
                    //匹配码成功
                    Timber.e("-----------匹配码成功")
                    BaseApplication.getBaseApplication().bleOperate.setClearListener()
                    startToWriteMcu()
                }
            }

        })
    }


    fun dealDfuFile(file: File){
        val fileByteArray = readBinFile(file)
        if(fileByteArray == null){
            return
        }
        val count = fileByteArray.size/512
        val remain = fileByteArray.size % 512

        val listByteArray = mutableListOf<ByteArray>()

        for(i in 0 until count){
            val btArray = ByteArray(512)
            System.arraycopy(fileByteArray,i * 512,btArray,0,btArray.size)
            Timber.e("MCU---------itemArray="+btArray[1].toInt())
            listByteArray.add(btArray)
            if(i == 0){
                System.arraycopy(btArray,6,matchByteArray,0,matchByteArray.size)
            }
        }
        val lastBtArray = ByteArray(remain)
        System.arraycopy(fileByteArray,listByteArray.size*512,lastBtArray,0,lastBtArray.size)
        lastDoubleByte[0] = lastBtArray[lastBtArray.size-2]
        lastDoubleByte[1] = lastBtArray[lastBtArray.size-1]
        listByteArray.add(lastBtArray)
        Timber.e("MCU----------最后一包="+"所有包的总数:"+listByteArray.size+"  "+lastBtArray.size+"    "+Utils.formatBtArrayToString(lastBtArray))
        sourceBinList.addAll(listByteArray)

        setFirstReset()
    }



    private fun startToWriteMcu(){
        if(sourceBinList.size == 0){
            return
        }
        startToWriteMcuData(sourceBinList)
    }

    //所有的mcu数组
    private var mcuListData = mutableListOf<ByteArray>()
    //当前发送的序号，从0开始
    private var currentPackIndex = 0

    private val hds : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x88){
                val array = msg.obj as ByteArray
                writeIndexPack(array)
            }
            if(msg.what == 0x09){
                if(mergeIndex<3){
                    val itemArray = mergeList[mergeIndex]
                    write(itemArray)
                    if(currentPackIndex == sourceBinList.size-1 && mergeIndex == 2){
                        handlers.removeMessages(0x00)
                        handlers.sendEmptyMessageDelayed(0x00,50)
                    }
                }

            }
        }
    }


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)


            if(msg.what == 0x00){
                Timber.e("MCU-----111-------当前写入的包序号=$currentPackIndex"+" 总包数"+mcuListData.size)
                if(currentPackIndex<mcuListData.size){

                    val progress = CalculateUtils.div(((currentPackIndex+1).toDouble()),120.0,3)
                    val p = String.format("%.1f",(progress.toFloat()*100F))
                    mcuDfuProgress.postValue("$p%")

                    GlobalScope.launch {
                        //数据内容
                        var currDataArray = mcuListData[currentPackIndex]
                        Timber.e("MCU-------写入="+currDataArray.size+"  序号="+currentPackIndex)
                        //长度 2个byte
                        var lengthArray = Utils.toByteArrayLength(currDataArray.size,2)
                        //序号 2个byte
                        val positionArray = Utils.toByteArrayLength(currentPackIndex,2)
                        if(currDataArray.size != 512){
                            val lastMcuDataArray = ByteArray(512)
                            System.arraycopy(mcuListData[currentPackIndex],0,lastMcuDataArray,0,currDataArray.size)
                            currDataArray = lastMcuDataArray
                        }

                        val positionStr = Utils.formatBtArrayToString(positionArray)
                        val lengthStr = Utils.formatBtArrayToString(lengthArray)

                        //长度=源地址1+目标地址1+命令1+数据length+校验1
                        val realLength = Utils.getIntFromBytes(lengthArray[0],lengthArray[1])+4
                        val dataLength = 1+1+1 +realLength+1
                        Timber.e("----------总长度="+realLength)
                        val dataLengthStr = Utils.toByteArrayLength(dataLength,2)


                     //   val str512 = positionStr+lengthStr+Utils.formatBtArrayToString(currDataArray)

                        //内容str
                        val contentArray = ByteArray(5+516)
                        contentArray[0] = dataLengthStr[0]
                        contentArray[1] = dataLengthStr[1]
                        contentArray[2] = 0x04
                        contentArray[3] = 0x01
                        contentArray[4] = 0x44
                        contentArray[5] = positionArray[0]
                        contentArray[6] = positionArray[1]
                        contentArray[7] = lengthArray[0]
                        contentArray[8] = lengthArray[1]

                        System.arraycopy(currDataArray,0,contentArray,9,currDataArray.size)

                        val contentStr = Utils.formatBtArrayToString(contentArray)

                        val crc = Utils.crcCarContentByteArray(contentArray)
                        val fullMcuStr = "7FFAAF$contentStr$crc"
                        val resultFullStr = "8800000000020f000120"+fullMcuStr
                        val resultArray = Utils.hexStringToByte(resultFullStr)

                        val msg = hds.obtainMessage()
                        msg.what = 0x88
                        msg.obj = resultArray
                        hds.sendMessage(msg)
                    }

                }else{
                    Timber.e("MCU---------所有的包都写完了")
                    //校验 7FFAAF
                    val str = "0006040145"+Utils.getHexString(lastDoubleByte)
                    val crc = Utils.crcCarContentArray(str)
                    val allStr = "011e7FFAAF"+str+crc
                    val rA = Utils.hexStringToByte(allStr)
                    val resultArray = Utils.getFullPackage(rA)
                    BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
                        override fun backWriteData(data: ByteArray?) {
                           Timber.e("---------校验="+Utils.formatBtArrayToString(data))
                            checkTimeOut(data)


                        }

                    })
                }
            }

        }
    }

    private fun startToWriteMcuData(list : MutableList<ByteArray>){
        currentPackIndex = 0
        mcuListData.clear()
        mcuListData.addAll(list)
        handlers.sendEmptyMessageDelayed(0x00,500)
    }


    private fun writeIndexPack(array : ByteArray){
        currentPackIndex++
        Timber.e("MCU---总长度="+array.size)

        mergePackData(array)

//        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(array,object : WriteBackDataListener{
//            override fun backWriteData(data: ByteArray?) {
//                Timber.e("---------写入一包返回="+Utils.formatBtArrayToString(data))
//
//
//            }
//
//        })
//
//        handlers.sendEmptyMessageDelayed(0x00,100)
    }




    val mergeList = mutableListOf<ByteArray>()
    var mergeIndex = 0

    private fun mergePackData(array: ByteArray){
        mergeList.clear()
        mergeIndex =  0
        val list = mutableListOf<ByteArray>()
        var tempCount = 0
        val firstArray = ByteArray(181)
        System.arraycopy(array,0,firstArray,0,firstArray.size)
        val secondArray = ByteArray(181)
        System.arraycopy(array,firstArray.size,secondArray,0,secondArray.size)

        val thirdArray = ByteArray(array.size-firstArray.size*2)
        System.arraycopy(array,secondArray.size*2,thirdArray,0,thirdArray.size)
        list.add(firstArray)
        list.add(secondArray)
        list.add(thirdArray)

        mergeList.addAll(list)
        hds.sendEmptyMessage(0x09)
//        while (tempCount<3){
//            val itemArray = list[tempCount]
//            Timber.e("----------循环写入指令="+Utils.formatBtArrayToString(itemArray))
//            val msg = hds.obtainMessage()
//            msg.what = 0x09
//            msg.obj = itemArray
//            hds.handleMessage(msg)
//
//            Thread.sleep(1000)
//            tempCount++
//        }
    }


    private fun write(data : ByteArray){
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(data,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                checkTimeOut(data)
                if(data == null){
                    return
                }

               Timber.e("-----------分包="+Utils.formatBtArrayToString(data))
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c4 01 31
                if(data.size== 20 && data[17].toInt().and(0xFF) == 196){
                    val status = data[18].toInt().and(0xFF)
                    if(status == 1){    //成功，进入下一个
                        handlers.sendEmptyMessageDelayed(0x00,20)
                    }else{  //失败

                    }
                }

                if(data.size == 20 && data[17].toInt().and(0xFF) == 192 && data[18].toInt().and(0xFF) == 1){
                    //匹配码成功
                    Timber.e("---------分包--匹配码成功")
                    handlers.sendEmptyMessageDelayed(0x00,20)
                }

            }
        })
        mergeIndex++
        hds.sendEmptyMessageDelayed(0x09,50)
    }

    private fun readBinFile(file: File): ByteArray? {
        var fileContent: ByteArray? = null
        val binFile = file
        if (binFile.exists()) {
            try {
                FileInputStream(binFile).use { fis ->
                    fileContent = ByteArray(binFile.length().toInt())
                    fis.read(fileContent)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return fileContent
    }


    private fun checkTimeOut(byteArray: ByteArray?){
        //88 00 00 00 00 00 0c ce 03 0f 7f fa af 00 05 01 04 c6 01 2f
        //88 00 00 00 00 00 0c d6 03 0f 7f fa af 00 05 01 04 c6 01 33
        if(byteArray== null){
            return
        }
        if(byteArray.size == 20 && byteArray[17].toInt().and(0xFF) == 198){
            val lastValue = byteArray[18].toInt().and(0xFF)
            mcuBootTimeOut.postValue(lastValue == 1)
        }
    }
}