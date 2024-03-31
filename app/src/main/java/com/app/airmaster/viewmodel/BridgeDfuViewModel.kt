package com.app.airmaster.viewmodel

import android.content.Context
import android.os.Environment
import com.app.airmaster.BaseApplication
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.bean.SyncTouchpadStatusBean
import com.app.airmaster.utils.CalculateUtils
import com.app.airmaster.utils.ImgUtil
import com.blala.blalable.Utils
import com.blala.blalable.keyboard.KeyBoardConstant
import com.blala.blalable.listener.WriteBackDataListener
import com.hjq.toast.ToastUtils
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException


/**
 * 中转相关的
 */
class BridgeDfuViewModel : CommViewModel(){


    //touchpad的升级的状态
    var touchpadUpgradeStatus = SingleLiveEvent<SyncTouchpadStatusBean>()



    //3.10.7 APP端设擦写设备端指定的FLASH数据块-扩展接口
    fun setEraseDeviceFlash(binFile : File,context: Context){

        //toStartWriteDialFlash(binFile,context)
        val startEndArray = mutableListOf<Byte>(0x00, 0xff.toByte(),
            0xff.toByte(), 0xff.toByte(),0x00, 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
            0x00, 0x80.toByte(),0x00 ,0x01,
            0x03 , 0xFF.toByte(), 0xFF.toByte(), 0xFE.toByte(),
            0x00, 0x00  ,0x03 ,0x04
        )

        val array = ByteArray(2)
        array[0] = 0x08
        array[1] = 0x07

        val keyStr = KeyBoardConstant.keyValue(array,startEndArray.toByteArray())
        val tempArray = Utils.hexStringToByte(keyStr)
        val resultArray = Utils.getFullPackage(tempArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                //88 00 00 00 00 00 03 02 08 08 02
                if(data?.size == 11 && data[9].toInt() == 8 ){

                    val s = data[10].toInt()
                    if(s == 2){
                        val bean = SyncTouchpadStatusBean(true,"")
                        touchpadUpgradeStatus.postValue(bean)
                        toStartWriteDialFlash(binFile,context)
                    }else{
                        val desc = if(s == 1) "不支持擦写FLASH数据" else "设备端不支持该功能"
                        val bean = SyncTouchpadStatusBean(false,desc)
                        touchpadUpgradeStatus.postValue(bean)
                    }


                }
            }

        })
    }


    private fun toStartWriteDialFlash(file : File,context: Context){
        //将bin文件打开
        val binArray = readBinFile(file)
        if(binArray == null || binArray.size == 0){
            Timber.e("-------bin为空了")
            val bean = SyncTouchpadStatusBean(false,"升级固件为空!")
            touchpadUpgradeStatus.postValue(bean)
            return
        }
        val startByte = byteArrayOf(
            0x00, 0xff.toByte(), 0xff.toByte(),
            0xff.toByte())
        Timber.e("-----bindArray-size="+binArray?.size)
        val resultArray = ImgUtil.getDialContent(startByte, startByte, binArray!!, 1000 + 701, -100, 0)

        //计算总的包数
        var allPackSize = resultArray.size
        Timber.e("------总的包数=" + allPackSize)
        //记录发送的包数
        var sendPackSize = 0

        BaseApplication.getBaseApplication().bleOperate.writeDialFlash(
            resultArray
        ) { statusCode ->
            sendPackSize++

            val conn = BaseApplication.getBaseApplication().connStatus

            //计算百分比
            var percentValue =
                CalculateUtils.div(sendPackSize.toDouble(), allPackSize.toDouble(), 2)
            val showPercent = CalculateUtils.mul(percentValue, 100.0).toInt()
            val bean = SyncTouchpadStatusBean(true,(if(showPercent>=100) 100 else showPercent ))
            touchpadUpgradeStatus.postValue(bean)
            Timber.e("---------发送数据包="+percentValue+"  showPercent="+showPercent)
            //gifLogTv?.text = sendPackSize.toString()+"/"+allPackSize+" "+showPercent
          //  showProgressDialog(resources.getString(R.string.string_sync_ing) + (if (showPercent >= 100) 100 else showPercent) + "%")

           // gifStringBuffer.append("----------->>>>写入百分比=$showPercent 状态=$statusCode 连接状态=$conn\n")
            // gifStringBuffer.append("----------->>>>percentValue 状态=$statusCode\n")

            /**
             * 0x01：更新失败
             * 0x02：更新成功
             * 0x03：第 1 个 4K 数据块异常（含 APP 端发擦写和实际写入的数据地址不一致），APP 需要重走流程
             * 0x04：非第 1 个 4K 数据块异常，需要重新发送当前 4K 数据块
             * 0x05：4K 数据块正常，发送下一个 4K 数据
             * 0x06：异常退出（含超时，或若干次 4K 数据错误，设备端处理）
             */

            /**
             * 0x01：更新失败
             * 0x02：更新成功
             * 0x03：第 1 个 4K 数据块异常（含 APP 端发擦写和实际写入的数据地址不一致），APP 需要重走流程
             * 0x04：非第 1 个 4K 数据块异常，需要重新发送当前 4K 数据块
             * 0x05：4K 数据块正常，发送下一个 4K 数据
             * 0x06：异常退出（含超时，或若干次 4K 数据错误，设备端处理）
             */
            if (statusCode == 1) {
                //cancelProgressDialog()
              //  ToastUtils.show(resources.getString(R.string.string_update_failed))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                val bean1 = SyncTouchpadStatusBean(false,"更新失败! 1")
                touchpadUpgradeStatus.postValue(bean1)
            }
            if (statusCode == 2) {
               // cancelProgressDialog()
               // ToastUtils.show(resources.getString(R.string.string_update_success))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                val bean1 = SyncTouchpadStatusBean(true,1000)
                touchpadUpgradeStatus.postValue(bean1)
            }
            if (statusCode == 6) {
              //  cancelProgressDialog()
              //  ToastUtils.show(resources.getString(R.string.string_error_exit))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                val bean6 = SyncTouchpadStatusBean(false,"异常退出! 6")
                touchpadUpgradeStatus.postValue(bean6)
            }
        }
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