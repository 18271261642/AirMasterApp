package com.app.airmaster.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.ViewModel
import com.app.airmaster.BaseApplication
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import kotlinx.coroutines.delay
import okhttp3.internal.and
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException

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
                //88 00 00 00 00 00 0c d6 03 0f 7f fa af 00 05 01 04 c2 01 33
                if(data.size == 20 && data[17].toInt().and(0xFF) == 194){   //进入Boot成功
                    Timber.e("--------进入Boot模式成功")

                }

            }

        })
    }


    //第三步，发送匹配码
    fun setThirdData(binStr : ByteArray){

        val str = "0014"+"0401"+"40"+Utils.getHexString(binStr)
        val crc = Utils.crcCarContentArray(str)

        val contentStr = "011E"+CarConstant.CAR_HEAD_BYTE_STR+str+crc
        val contentArray = Utils.hexStringToByte(contentStr)
        val resultArray = Utils.getFullPackage(contentArray)

        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null){
                    return
                }
                Timber.e("----------发送匹配码返回="+Utils.formatBtArrayToString(data))
                //88 00 00 00 00 00 0c d0 03 0f 7f fa af 00 05 01 04 c0 00 36

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
        }
        val lastBtArray = ByteArray(remain)
        System.arraycopy(fileByteArray,listByteArray.size*512,lastBtArray,0,lastBtArray.size)
        listByteArray.add(lastBtArray)
        Timber.e("MCU----------最后一包="+"所有包的总数:"+listByteArray.size+"  "+lastBtArray.size+"    "+Utils.formatBtArrayToString(lastBtArray))

        startToWriteMcuData(listByteArray)
    }

    //所有的mcu数组
    private var mcuListData = mutableListOf<ByteArray>()
    //当前发送的序号，从0开始
    private var currentPackIndex = 0



    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                Timber.e("MCU-----111-------当前写入的包序号=$currentPackIndex")
                if(currentPackIndex<mcuListData.size){
                    //数据内容
                    var currDataArray = mcuListData[currentPackIndex]
                    Timber.e("MCU-------写入="+currDataArray.size+"  序号="+currentPackIndex)
                    //长度 2个byte
                    val lengthArray = Utils.toByteArrayLength(currDataArray.size,2)
                    //序号 2个byte
                    val positionArray = Utils.toByteArrayLength(currentPackIndex,2)
                    if(currDataArray.size != 512){
                        val lastMcuDataArray = ByteArray(512)
                        System.arraycopy(mcuListData[currentPackIndex],0,lastMcuDataArray,0,currDataArray.size)
                        currDataArray = lastMcuDataArray
                    }

                    val positionStr = Utils.formatBtArrayToString(positionArray)
                    val lengthStr = Utils.formatBtArrayToString(lengthArray)
                    val tempStr = "7FFAAF0208040144$positionStr$lengthStr"

                    val tempArray = Utils.hexStringToByte(tempStr)

                    val allArray = ByteArray(currDataArray.size+tempArray.size)
                    System.arraycopy(tempArray,0,allArray,0,tempArray.size)
                    System.arraycopy(currDataArray,0,allArray,tempArray.size,currDataArray.size)

                    val resultArray = Utils.getFullPackage(allArray)
                    writeIndexPack(resultArray)

                }else{
                    Timber.e("MCU---------所有的包都写完了")
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

        handlers.sendEmptyMessageDelayed(0x00,200)
//        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(array,object : WriteBackDataListener{
//            override fun backWriteData(data: ByteArray?) {
//                Timber.e("---------写入一包返回="+Utils.formatBtArrayToString(data))
//
//
//            }
//
//        })

       // handlers.sendEmptyMessageDelayed(0x00,80)
    }



    private fun mergePackData(array: ByteArray){
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


        while (tempCount<3){
            val itemArray = list[tempCount]
            tempCount++
            write(itemArray)
            Thread.sleep(50)
        }
    }


    private fun write(data : ByteArray){
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(data,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
               Timber.e("-----------分包="+Utils.formatBtArrayToString(data))
            }

        })
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
}