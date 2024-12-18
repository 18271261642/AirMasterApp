package com.app.airmaster.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.app.airmaster.utils.CalculateUtils
import com.app.airmaster.utils.GetJsonDataUtil
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
    var mcuBootTimeOut = SingleLiveEvent<Int>()


    //最后两个byte
    var lastDoubleByte = ByteArray(2)
    //匹配码
    private var matchByteArray = ByteArray(16)
    //原始bin文件包集合
    private var sourceBinList = mutableListOf<ByteArray>()


    private var isReset = false
    private var isIntoBoot = false
    private var isMatchStatus = false




    private var logUrl : String ?= null
    private var currentLogName : String ?= null


    private var logSb = StringBuffer()

    fun setLogUrl(url :String){
        this.logUrl = url
    }


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
        clearMcuLog()
    }


    //第一步，主机复位
    fun setFirstReset(){
        currentLogName = (System.currentTimeMillis()/1000).toString()+"_"+".json"
        operateHandlers.sendEmptyMessageDelayed(0x00,2000)

        val data = byteArrayOf(0x01,0x1E,0x7F, 0xFA.toByte(),
            0xAF.toByte(), 0x00, 0x05, 0x04, 0x01, 0x41, 0x00, 0xB5.toByte()
        )
        val resultArray = Utils.getFullPackage(data)
        logSb.append("第一步复位："+Utils.formatBtArrayToString(resultArray)+"\n\n")
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                val str = Utils.formatBtArrayToString(data)
                Timber.e("----------复位="+str)
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c1 01 34
                logSb.append("复位返回："+str+"\n\n")
                if(data.size == 20 && data[17].toInt().and(0xFF) == 193){   //复位成功
                    Timber.e("--------复位成功")
                    logSb.append("复位成功：$str\n\n")
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
        logSb.append("第二步进入Boot模式："+Utils.formatBtArrayToString(resultArray)+"\n\n")
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                val str = Utils.formatBtArrayToString(data)
                Timber.e("----------进入Boot模式="+Utils.formatBtArrayToString(data))
                logSb.append("进入Boot模式返回："+str+"\n\n")
                //88 00 00 00 00 00 0c d6 03 0f 7f fa af 00 05 01 04 c2 01 33
                if(data.size == 20 && data[17].toInt().and(0xFF) == 194){   //进入Boot成功
                    Timber.e("--------进入Boot模式成功")
                    logSb.append("进入Boot模式成功："+str+"\n\n")
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

        logSb.append("第三步发送匹配码："+contentStr+"\n\n")

        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                val str = Utils.formatBtArrayToString(data)
                Timber.e("----------发送匹配码返回="+str)
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c0 01 35
                logSb.append("发送匹配码返回："+str+"\n\n")
                if(data.size == 20 && data[17].toInt().and(0xFF) == 192 && data[18].toInt().and(0xFF) == 1){
                    logSb.append("发送匹配码成功："+str+"\n\n")
                    isMatchStatus = true
                    operateHandlers.removeMessages(0x02)
                    //匹配码成功
                    Timber.e("-----------匹配码成功")
                    BaseApplication.getBaseApplication().bleOperate.setClearListener()
                    handlers.postAtTime(object : Runnable{
                        override fun run() {
                            startToWriteMcu()
                        }

                    },2000)

                }
            }

        })
    }


    fun dealDfuFile(file: File){
        val fileByteArray = readBinFile(file)
        if(fileByteArray == null){
            return
        }

        Timber.e("-------原始固件包大小="+fileByteArray.size)

        System.arraycopy(fileByteArray,6,matchByteArray,0,matchByteArray.size)
        lastDoubleByte[0] = fileByteArray[fileByteArray.size-2]
        lastDoubleByte[1] = fileByteArray[fileByteArray.size-1]


        val resultFileArray = ByteArray(fileByteArray.size-32-2)

        System.arraycopy(fileByteArray,32,resultFileArray,0,resultFileArray.size)

        val count = resultFileArray.size/512
        val remain = resultFileArray.size % 512

        val t = resultFileArray.size/512F

        Timber.e("------MCU--bin文件大小="+resultFileArray.size+"  "+count+"  remain="+remain+"  "+t)

        val listByteArray = mutableListOf<ByteArray>()

        for(i in 0 until count){
            val btArray = ByteArray(512)
            System.arraycopy(resultFileArray,i * 512,btArray,0,btArray.size)
           // Timber.e("MCU---------itemArray="+btArray[1].toInt())
            listByteArray.add(btArray)
//            if(i == 0){
//                System.arraycopy(btArray,6,matchByteArray,0,matchByteArray.size)
//            }
        }

        val lastBtArray = ByteArray(remain)
        System.arraycopy(resultFileArray,listByteArray.size*512,lastBtArray,0,remain)


        val lt = Utils.formatBtArrayToString(lastBtArray)
        Timber.e("--------最后一包="+lt)
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


    fun getMcuOtaLog() : String{
        return logSb.toString()
    }

    fun clearMcuLog(){
        logSb.delete(0,logSb.length)
    }





    private val hds : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 100){
                val data = msg.obj as String
               // GetJsonDataUtil().writeTxtToFile(data,logUrl,currentLogName)
            }


            if(msg.what == 0x88){
                val array = msg.obj as ByteArray
                writeIndexPack(array)
            }
            if(msg.what == 0x09){
                val str = currentPackIndex.toString()+" "+mergeIndex+" "+sourceBinList.size
                Timber.e("---------分包序号="+str)

                if(mergeIndex<3){
                    val itemArray = mergeList[mergeIndex]
                    write(itemArray)

                }else{
                    if(currentPackIndex == sourceBinList.size-1 && mergeIndex == 3){

                        Timber.e("----------最后一包发送完了11111")
                        //   handlers.removeMessages(0x00)
                        handlers.postAtTime(object : Runnable{
                            override fun run() {
                               // sendCheckMcuData()
                            }

                        },3000)
                    }
                }

            }
        }
    }


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 100){
                Timber.e("---------超时处理="+currentPackIndex)
            }

            if(msg.what == 0x00){
                val str = "当前写入的包序号=$currentPackIndex"+" 总包数"+mcuListData.size
                Timber.e("MCU-----111-------当前写入的包序号=$currentPackIndex"+" 总包数"+mcuListData.size)
                logSb.append("当前写入的包序号="+str+"\n\n")
                if(currentPackIndex<mcuListData.size){

                    var progress = CalculateUtils.div(((currentPackIndex+1).toDouble()),120.0,2).toFloat()
                    if(progress>=1.0){
                        progress = 1.0F
                    }
                    val p = String.format("%.1f",(progress*100F))
                    mcuDfuProgress.postValue("$p%")

                    GlobalScope.launch {
                        //数据内容
                        var currDataArray = mcuListData[currentPackIndex]
                        //长度 2个byte
                        val lengthArray = Utils.toByteArrayLength(currDataArray.size,2)
                        //序号 2个byte
                        val positionArray = Utils.toByteArrayLength(currentPackIndex,2)
                        Timber.e("MCU---序号="+currentPackIndex+"  包大小="+currDataArray.size+" "+Utils.formatBtArrayToString(lengthArray))
                        if(currDataArray.size != 512){
                            val lastMcuDataArray = ByteArray(512)
                            System.arraycopy(mcuListData[currentPackIndex],0,lastMcuDataArray,0,currDataArray.size)
                            currDataArray = lastMcuDataArray
                            Timber.e("-----------最后一个包="+Utils.formatBtArrayToString(currDataArray))
                        }

//                        val positionStr = Utils.formatBtArrayToString(positionArray)
//                        val lengthStr = Utils.formatBtArrayToString(lengthArray)

                        //长度=源地址1+目标地址1+命令1+数据length+校验1
                        val realLength = Utils.getIntFromBytes(lengthArray[0],lengthArray[1])+4
                        val dataLength = 1+1+1 +realLength+1
                        Timber.e("----------总长度="+realLength)
                        val dataLengthStr = Utils.toByteArrayLength(dataLength,2)


                     //   val str512 = positionStr+lengthStr+Utils.formatBtArrayToString(currDataArray)

                        //内容str
                        val contentArray = ByteArray(5+516)
                        contentArray[0] = 0x02 //dataLengthStr[0]
                        contentArray[1] = 0x08//dataLengthStr[1]
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
                        val fullMcuStr = "01207FFAAF$contentStr$crc"

                      //  val resultFullStr = "8800000000020f00"+fullMcuStr

                        //   val resultFullStr = "8800000000020f000120"+fullMcuStr
                        val resultArray = Utils.hexStringToByte(fullMcuStr)
                        val r = Utils.getFullPackage(resultArray)
                        val msg = hds.obtainMessage()
                        msg.what = 0x88
                        msg.obj = r
                        hds.sendMessage(msg)

                        val m2 = hds.obtainMessage()
                        m2.what = 100
                        m2.obj = fullMcuStr
                        hds.sendMessage(m2)
                    }

                }else{
                    Timber.e("MCU---------所有的包都写完了")
                    sendCheckMcuData()
                }
            }

        }
    }


    //所有包都发完了，去校验
    fun sendCheckMcuData(){
        //校验 7FFAAF
        val str = "0006040145"+Utils.getHexString(lastDoubleByte)
        val crc = Utils.crcCarContentArray(str)  //0120
        val allStr = "011E7FFAAF"+str+crc
        val rA = Utils.hexStringToByte(allStr)
        val resultArray = Utils.getFullPackage(rA)
        logSb.append("校验="+allStr+"\n\n")
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                Timber.e("---------校验="+Utils.formatBtArrayToString(data))
                //校验
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c5 01 30
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 c5 00 31
                if(data == null){
                    return
                }
                if(data.size == 20 && data[17].toInt().and(0xFF) == 197){
                    val status = data[18].toInt().and(0xFF)
                    readCheckValue.postValue(status)

                }else if(data.size == 20 && data[17].toInt().and(0xFF) == 198){

                }

                else{
                  //  checkTimeOut(data)
                }

            }

        })
    }


    private fun startToWriteMcuData(list : MutableList<ByteArray>){
        logSb.append("开始写入固件包："+Utils.formatCurrentTime()+"\n\n")
        currentPackIndex = 0
        mcuListData.clear()
        mcuListData.addAll(list)
        handlers.sendEmptyMessageDelayed(0x00,500)
    }


    private fun writeIndexPack(array : ByteArray){

        tempItemArray = array
        Timber.e("MCU---总长度="+array.size+" 序号="+currentPackIndex)
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
        val firstArray = ByteArray(200)
        System.arraycopy(array,0,firstArray,0,firstArray.size)
        val secondArray = ByteArray(200)
        System.arraycopy(array,firstArray.size,secondArray,0,secondArray.size)

        val thirdArray = ByteArray(array.size-firstArray.size*2)
        System.arraycopy(array,secondArray.size*2,thirdArray,0,thirdArray.size)
        list.add(firstArray)
        list.add(secondArray)
        list.add(thirdArray)

        mergeList.addAll(list)
        hds.sendEmptyMessage(0x09)
    }



    private var tempItemArray : ByteArray ?= null
    private var tempItemCount = 0

    private fun write(data : ByteArray){

        tempItemCount++
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
                        tempItemCount = 0
                        currentPackIndex++
                        handlers.removeMessages(100)
                        handlers.sendEmptyMessageDelayed(0x00,50)
                    }else{  //失败
                        handlers.sendEmptyMessageDelayed(100,5000)
                    }
                }else{

                }

                if(data.size == 20 && data[17].toInt().and(0xFF) == 192 && data[18].toInt().and(0xFF) == 1){
                    //匹配码成功
                    Timber.e("---------分包--匹配码成功")
                    currentPackIndex++
                    handlers.removeMessages(100)
                    handlers.sendEmptyMessageDelayed(0x00,50)
                }

            }
        })
        mergeIndex++
        hds.sendEmptyMessageDelayed(0x09,80)
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
            mcuBootTimeOut.postValue(lastValue)

            if(lastValue == 0x01){
              //  setFirstReset()
            }
        }
    }
}