package com.app.airmaster.dialog

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R
import com.app.airmaster.ble.DfuService
import com.app.airmaster.utils.MmkvUtils
import com.hjq.toast.ToastUtils
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import timber.log.Timber


/**
 * 升级的dialog
 */
class DfuDialogView : AppCompatDialog {

    //是否在升级中
    private var isUpgradeing = false

    constructor(context: Context) : super (context){

    }

    constructor(context: Context, theme : Int) : super (context,theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_device_ota_layout)


    }


    private fun startToDfu(url : String){
        val mac = MmkvUtils.getConnDeviceMac()
        val dfuServiceInitiator = DfuServiceInitiator(mac)
            .setDeviceName(MmkvUtils.getConnDeviceName())
            .setKeepBond(true)
            .setForceDfu(false)
            .setPacketsReceiptNotificationsEnabled(true)
            .setPacketsReceiptNotificationsValue(6)
            .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        dfuServiceInitiator.disableResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context)
        }
        dfuServiceInitiator.setZip(url)
        dfuServiceInitiator.start(context, DfuService::class.java)
    }


    private val mDfuProgressListener : DfuProgressListener = object : DfuProgressListener {
        override fun onDeviceConnecting(deviceAddress: String?) {
            Timber.e("------onDeviceConnecting--------")

            //   dfuBtnStatusView?.setShowTxt = resources.getString(R.string.string_upgrade_ing)
        }

        override fun onDeviceConnected(deviceAddress: String?) {
            Timber.e("-------onDeviceConnected-------")
        }

        override fun onDfuProcessStarting(deviceAddress: String?) {
            Timber.e("------onDfuProcessStarting--------")
            isUpgradeing = true

        }

        override fun onDfuProcessStarted(deviceAddress: String?) {
            Timber.e("------onDfuProcessStarted--------")
        }

        override fun onEnablingDfuMode(deviceAddress: String?) {
            Timber.e("-------onEnablingDfuMode-------")
        }

        override fun onProgressChanged(
            deviceAddress: String?,
            percent: Int,
            speed: Float,
            avgSpeed: Float,
            currentPart: Int,
            partsTotal: Int
        ) {
            Timber.e("------onProgressChanged--------="+percent+" "+currentPart)

        }

        override fun onFirmwareValidating(deviceAddress: String?) {
            Timber.e("-----onFirmwareValidating---------")

        }

        override fun onDeviceDisconnecting(deviceAddress: String?) {
            Timber.e("-----onDeviceDisconnecting---------")
        }

        override fun onDeviceDisconnected(deviceAddress: String?) {
            Timber.e("----onDeviceDisconnected----------")
        }

        override fun onDfuCompleted(deviceAddress: String?) {
            Timber.e("-------onDfuCompleted-------="+deviceAddress)
            ToastUtils.show(context.getString(R.string.string_upgrade_success))
            //  dfuNoUpdateTv.visibility = View.GONE

//            val saveMac = MmkvUtils.getConnDeviceMac()
//            if(!BikeUtils.isEmpty(saveMac)){
////                BaseApplication.getInstance().connStatusService.autoConnDevice(saveMac,true)
//            }
            //BaseApplication.getInstance().connStatusService.autoConnDevice()
        }

        override fun onDfuAborted(deviceAddress: String?) {
            Timber.e("------onDfuAborted--------")
        }

        override fun onError(deviceAddress: String?, error: Int, errorType: Int, message: String?) {
            Timber.e("--------onError------="+error+" "+message)

        }

    }


    private fun unregister(){
        DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener)
    }
}