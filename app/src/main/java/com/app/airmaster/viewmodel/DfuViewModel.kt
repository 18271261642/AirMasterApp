package com.app.airmaster.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.ble.DfuService
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.MmkvUtils
import com.blala.blalable.listener.ConnStatusListener
import com.hjq.toast.ToastUtils
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuServiceController
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import timber.log.Timber

/**
 * 用于Nordic的OTA升级
 */
class DfuViewModel : ViewModel(){

    //升级状态
    var dfuUpgradeStatus = SingleLiveEvent<Boolean>()
    //进度
    var dfuProgressData = SingleLiveEvent<Int>()

    private var isDfuConn = false


    private var dfuServiceController : DfuServiceController ?= null

    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
            }

        }
    }

    private var otaFileUrl : String ?= null

    fun registerDfu(context: Context){
        DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener)
    }

    fun unregister(context: Context){
        DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener)
    }



    //扫描
    fun startDfuModel(url : String,context: Context){
        this.otaFileUrl = url
        BaseApplication.getBaseApplication().bleOperate.scanBleDevice(object : SearchResponse {
            override fun onSearchStarted() {

            }

            @SuppressLint("MissingPermission")
            override fun onDeviceFounded(p0: SearchResult?) {
                val name = p0?.name
                if(BikeUtils.isEmpty(name)){
                    return
                }
                if(name!!.lowercase() == "sl_ota" || name!!.lowercase().contains("ota")){
                    handlers.sendEmptyMessageDelayed(0x00,100)
                    MmkvUtils.setSaveObjParams("ota_mac",p0?.device?.address)

//                    if(isDfuConn){
//                        return
//                    }
                    connOta(p0?.device!!.address, context)
                }

            }

            override fun onSearchStopped() {

            }

            override fun onSearchCanceled() {

            }

        },30 * 1000, 1)
    }



    //连接
    private fun connOta(mac : String,context: Context){
        Timber.e("---------dfu连接中====")
        isDfuConn = true
        BaseApplication.getBaseApplication().bleOperate.connYakDevice("ota",mac,object :
            ConnStatusListener {
            override fun connStatus(status: Int) {

            }

            override fun setNoticeStatus(code: Int) {
                startToDfu(otaFileUrl!!,context)
            }

        })
    }




    fun startToDfu(url : String,context: Context){
        val mac = MmkvUtils.getSaveParams("ota_mac","") as String
        val dfuServiceInitiator = DfuServiceInitiator(mac)
            .setDeviceName("SL_OTA")
            .setKeepBond(false)
            .setForceDfu(false)
            .setPacketsReceiptNotificationsEnabled(false)
            .setPacketsReceiptNotificationsValue(12) //6
            .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        dfuServiceInitiator.disableResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context)
        }
        dfuServiceInitiator.setZip(url)
        dfuServiceController = dfuServiceInitiator.start(context, DfuService::class.java)
    }




    private val mDfuProgressListener : DfuProgressListener = object : DfuProgressListener {



        override fun onDeviceConnecting(deviceAddress: String) {

        }

        override fun onDeviceConnected(deviceAddress: String) {

        }

        override fun onDfuProcessStarting(deviceAddress: String) {

        }

        override fun onDfuProcessStarted(deviceAddress: String) {

        }

        override fun onEnablingDfuMode(deviceAddress: String) {

        }

        override fun onProgressChanged(
            deviceAddress: String,
            percent: Int,
            speed: Float,
            avgSpeed: Float,
            currentPart: Int,
            partsTotal: Int
        ) {
            Timber.e("------onProgressChanged--------="+percent+" "+currentPart)
            dfuProgressData.postValue(percent)
        }

        override fun onFirmwareValidating(deviceAddress: String) {
            Timber.e("-----onFirmwareValidating---------")
        }

        override fun onDeviceDisconnecting(deviceAddress: String?) {
            Timber.e("-----onDeviceDisconnecting---------")
        }

        override fun onDeviceDisconnected(deviceAddress: String) {
            TODO("Not yet implemented")
        }

        override fun onDfuCompleted(deviceAddress: String) {
            BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()


            dfuServiceController?.abort()
            isDfuConn = false
            viewModelScope.launch {
                delay(3000)
                dfuUpgradeStatus.postValue(true)
            }
        }

        override fun onDfuAborted(deviceAddress: String) {

        }

        override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String?) {
            isDfuConn = false
            dfuUpgradeStatus.postValue(false)
        }

    }

}