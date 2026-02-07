package com.app.airmaster.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.ble.DfuService
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.utils.MmkvUtils
import com.blala.blalable.listener.ConnStatusListener
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import timber.log.Timber


/**
 * 升级的dialog
 */
class DfuDialogView : AppCompatDialog {


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
            }

        }
    }


    private var onClickListener : OnCommItemClickListener ?= null

    private var onDfuListener : OnCommItemClickListener ?= null

    fun setOnClick(c : OnCommItemClickListener){
        this.onClickListener = c
    }

    fun setOnDfuStateListener(d : OnCommItemClickListener){
        this.onDfuListener = d
    }




    //是否在升级中
    private var isUpgradeing = false

    private var dfuDialogTitleTv : TextView ?= null
    private var dfuContentTv : TextView ?= null
    private var dfuBtnLayout : LinearLayout ?= null
    private var privacyDialogConfirmTv : ShapeTextView ?= null
    private var privacyDialogCancelTv : ShapeTextView ?= null

    private var dfuIngLayout : LinearLayout ?= null
    private var dfuStateTv : TextView ?= null


    private var otaFileUrl : String ?= null

    constructor(context: Context) : super (context){

    }

    constructor(context: Context, theme : Int) : super (context,theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_device_ota_layout)
        initViews()

        initData()
    }


    private fun initData(){
       // DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener)
    }

    private fun initViews(){
        dfuDialogTitleTv = findViewById(R.id.dfuDialogTitleTv)
        dfuContentTv = findViewById(R.id.dfuContentTv)
        privacyDialogConfirmTv = findViewById(R.id.privacyDialogConfirmTv)
        privacyDialogCancelTv = findViewById(R.id.privacyDialogCancelTv)
        dfuIngLayout = findViewById(R.id.dfuIngLayout)
        dfuStateTv = findViewById(R.id.dfuStateTv)
        dfuBtnLayout = findViewById(R.id.dfuBtnLayout)

        privacyDialogConfirmTv?.setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            onClickListener?.onItemClick(0x01)
        }
        privacyDialogCancelTv?.setOnClickListener {
            onClickListener?.onItemClick(0x00)
        }
    }


    fun setTitleTxt(txt : String){
        dfuDialogTitleTv?.text = txt
    }

    //显示升级的状态
    fun setDfuModel(){
        dfuContentTv?.visibility = View.GONE
        dfuIngLayout?.visibility = View.VISIBLE
        dfuBtnLayout?.visibility = View.GONE
    }

    fun setDfuUpgradeContent(content : String){
        dfuContentTv?.text = content
    }



    //扫描
    fun startDfuModel(url : String){
        this.otaFileUrl = url
        BaseApplication.getBaseApplication().bleOperate.scanBleDevice(object : SearchResponse{
            override fun onSearchStarted() {

            }

            @SuppressLint("MissingPermission")
            override fun onDeviceFounded(p0: SearchResult?) {
               val name = p0?.name
                if(BikeUtils.isEmpty(name)){
                    return
                }
                if(name!!.lowercase() == "sl_ota"){
                    handlers.sendEmptyMessageDelayed(0x00,1000)
                    MmkvUtils.setSaveObjParams("ota_mac",p0?.device?.address)
                    connOta(p0?.device!!.address)
                }

            }

            override fun onSearchStopped() {

            }

            override fun onSearchCanceled() {

            }

        },30 * 1000, 1)
    }



    //连接
    private fun connOta(mac : String){
        BaseApplication.getBaseApplication().bleOperate.connYakDevice("ota",mac,object :
            ConnStatusListener{
            override fun connStatus(status: Int) {

            }

            override fun setNoticeStatus(code: Int) {
                startToDfu(otaFileUrl!!)
            }

        })
    }




     fun startToDfu(url : String){
        val mac = MmkvUtils.getSaveParams("ota_mac","") as String
        val dfuServiceInitiator = DfuServiceInitiator(mac)
            .setDeviceName("SL_OTA")
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


//    private val mDfuProgressListener : DfuProgressListener = object : DfuProgressListener {
//        override fun onDeviceConnecting(deviceAddress: String?) {
//            Timber.e("------onDeviceConnecting--------")
//
//            //   dfuBtnStatusView?.setShowTxt = resources.getString(R.string.string_upgrade_ing)
//        }
//
//        override fun onDeviceConnected(deviceAddress: String?) {
//            Timber.e("-------onDeviceConnected-------")
//        }
//
//        override fun onDfuProcessStarting(deviceAddress: String?) {
//            Timber.e("------onDfuProcessStarting--------")
//            isUpgradeing = true
//
//        }
//
//        override fun onDfuProcessStarted(deviceAddress: String?) {
//            Timber.e("------onDfuProcessStarted--------")
//        }
//
//        override fun onEnablingDfuMode(deviceAddress: String?) {
//            Timber.e("-------onEnablingDfuMode-------")
//        }
//
//        @SuppressLint("SetTextI18n")
//        override fun onProgressChanged(
//            deviceAddress: String?,
//            percent: Int,
//            speed: Float,
//            avgSpeed: Float,
//            currentPart: Int,
//            partsTotal: Int
//        ) {
//            Timber.e("------onProgressChanged--------="+percent+" "+currentPart)
//            dfuStateTv?.text = "升级中: "+percent
//        }
//
//        override fun onFirmwareValidating(deviceAddress: String?) {
//            Timber.e("-----onFirmwareValidating---------")
//
//        }
//
//        override fun onDeviceDisconnecting(deviceAddress: String?) {
//            Timber.e("-----onDeviceDisconnecting---------")
//        }
//
//        override fun onDeviceDisconnected(deviceAddress: String?) {
//            Timber.e("----onDeviceDisconnected----------")
//        }
//
//        override fun onDfuCompleted(deviceAddress: String?) {
//            Timber.e("-------onDfuCompleted-------="+deviceAddress)
//            ToastUtils.show(context.getString(R.string.string_upgrade_success))
//            //  dfuNoUpdateTv.visibility = View.GONE
//            failOrSuccess(true)
////            val saveMac = MmkvUtils.getConnDeviceMac()
////            if(!BikeUtils.isEmpty(saveMac)){
//////                BaseApplication.getInstance().connStatusService.autoConnDevice(saveMac,true)
////            }
//            //BaseApplication.getInstance().connStatusService.autoConnDevice()
//        }
//
//        override fun onDfuAborted(deviceAddress: String?) {
//            Timber.e("------onDfuAborted--------")
//            failOrSuccess(false)
//        }
//
//        override fun onError(deviceAddress: String?, error: Int, errorType: Int, message: String?) {
//            Timber.e("--------onError------="+error+" "+message)
//            failOrSuccess(false)
//        }
//
//    }




     fun unregister(){
       // DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener)
    }


    private fun failOrSuccess(success : Boolean){
      //  dismiss()

        onDfuListener?.onItemClick(if(success) 1 else 0)
    }
}