package com.app.airmaster.second

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LabeledIntent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.View.OnLongClickListener
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.action.SingleClick
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.adapter.SecondScanAdapter
import com.app.airmaster.bean.BleBean
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.view.CarBindDeviceView
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.dialog.DeleteNoteDialog
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.BonlalaUtils
import com.app.airmaster.utils.MmkvUtils
import com.blala.blalable.BleConstant
import com.blala.blalable.Utils
import com.blala.blalable.listener.OnCommLongClickListener
import com.bonlala.base.action.ClickAction
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import timber.log.Timber
import java.nio.file.WatchEvent

/**
 * Created by Admin
 *Date 2023/7/12
 */
class SecondScanActivity : AppActivity() {



    private var secondScanRy: RecyclerView? = null


    private var adapter: SecondScanAdapter? = null
    private var list: MutableList<BleBean>? = null


    private var scanBindDeviceView : CarBindDeviceView ?= null


    //用于去重的list
    private var repeatList: MutableList<String>? = null

    private val handlers: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x00) {
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
            }
            if(msg.what == 0x01){
                hideDialog()
                ToastUtils.show("连接失败!")
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                verifyScanFun(false)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_second_scan_layout
    }

    override fun initView() {

        scanBindDeviceView  = findViewById(R.id.scanBindDeviceView)


        secondScanRy = findViewById(R.id.secondScanRy)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        secondScanRy?.layoutManager = linearLayoutManager
        list = mutableListOf()
        adapter = SecondScanAdapter(context, list!!,false)
        secondScanRy?.adapter = adapter
        repeatList = mutableListOf()
        adapter!!.setOnItemClick(onItemClick)

        clickToConn()

    }


    @SingleClick()
    private fun clickToConn(){
        scanBindDeviceView?.setOnClickListener {
            if(BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTING){
                return@setOnClickListener
            }
            if(BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED){
                return@setOnClickListener
            }
            showDialog("Connecting..")
            BaseApplication.getBaseApplication().connStatusService?.autoConnDevice(MmkvUtils.getConnDeviceMac(),false)
        }


        scanBindDeviceView?.setOnLongClickListener {
            unBindDevice()
            true
        }
    }

    override fun initData() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        registerReceiver(broadcastReceiver,intentFilter)


        verifyScanFun(false)

        getHasBindDevice()
    }



    //获取已经连接的设备
    private fun getHasBindDevice(){
        val connMac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(connMac)){
            scanBindDeviceView?.visibility = View.GONE
            return
        }
        scanBindDeviceView?.visibility = View.VISIBLE
        val name = MmkvUtils.getConnDeviceName()
        val isConnected = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED
        scanBindDeviceView?.setNameAndMac(name,connMac)
        scanBindDeviceView?.setImgStatus(isConnected)

        var position = -1
        list?.forEachIndexed { index, bleBean ->
            if(bleBean.bluetoothDevice.address == connMac){

                position = index
            }
        }
        if(list?.size!! >0 && position !=-1){
            list?.removeAt(position)
            adapter?.notifyDataSetChanged()
        }

    }



    //判断是否有位置权限了，没有请求权限
    private fun verifyScanFun(isReconn: Boolean) {

        //判断蓝牙是否开启
        if (!BikeUtils.isBleEnable(this)) {
            BikeUtils.openBletooth(this)
            return
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            XXPermissions.with(this).permission(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                )
            ).request { permissions, all ->
                //verifyScanFun()
            }
        }



        //判断权限
        val isPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermission) {
            XXPermissions.with(this).permission(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ).request { permissions, all ->
                verifyScanFun(isReconn)
            }
            // ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),0x00)
            return
        }


        //判断蓝牙是否打开
        val isOpenBle = BonlalaUtils.isOpenBlue(this@SecondScanActivity)
        if (!isOpenBle) {
            BonlalaUtils.openBluetooth(this)
            return
        }

        if (isReconn) {
            val mac = MmkvUtils.getConnDeviceMac()
            if (BikeUtils.isEmpty(mac))
                return
            BaseApplication.getBaseApplication().connStatusService.autoConnDevice(mac, false)

        } else {
            startScan()
        }

    }


    @SuppressLint("MissingPermission")
    private val onItemClick: OnCommItemClickListener =
        OnCommItemClickListener { position ->
            val service = BaseApplication.getBaseApplication().connStatusService
            val bean = list?.get(position)
            if (bean != null) {

                val mac = MmkvUtils.getConnDeviceMac()
                if(!BikeUtils.isEmpty(mac)){
                    ToastUtils.show(resources.getString(R.string.string_scan_conn_new_device))
                    return@OnCommItemClickListener
                }

                showDialog("Connecting..")
                handlers.sendEmptyMessageDelayed(0x00, 500)
                handlers.sendEmptyMessageDelayed(0x01,20 * 1000)
                service.connDeviceBack(
                    bean.bluetoothDevice.name, bean.bluetoothDevice.address
                ) { mac, status ->
                    handlers.removeMessages(0x01)
                    hideDialog()
                    MmkvUtils.saveScreenDeviceStatus(bean.isScreenDevice)
                    MmkvUtils.saveConnDeviceMac(mac)
                    MmkvUtils.saveConnDeviceName(bean.bluetoothDevice.name)
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED

                    getHasBindDevice()
                }
            }
        }


    //开始扫描
    fun startScan() {
        val connMac = MmkvUtils.getConnDeviceMac()
        BaseApplication.getBaseApplication().bleOperate.scanBleDevice(object : SearchResponse {

            override fun onSearchStarted() {

            }

            override fun onDeviceFounded(p0: SearchResult) {
                if (p0.getScanRecord() == null || p0.getScanRecord().isEmpty())
                    return
                 Timber.e("--------扫描="+p0.name+" "+Utils.formatBtArrayToString(p0.getScanRecord()))

                val recordStr = Utils.formatBtArrayToString(p0.getScanRecord())
                val bleName = p0.name

                if (BikeUtils.isEmpty(bleName) || bleName.equals("NULL") || BikeUtils.isEmpty(p0.address))
                    return
                if (repeatList?.contains(p0.address) == true)
                    return
                //030543
                if (!BikeUtils.isEmpty(recordStr)
                ) {

                    if(!BikeUtils.isEmpty(connMac) && connMac == p0.address){
                        return
                    }

                    if(recordStr.contains("c019") || recordStr.contains("19c0") || recordStr.contains("c01b") || recordStr.contains("1bc0")){


                        val isScreen = recordStr.contains("c019") || recordStr.contains("19c0")

                    //判断少于40个设备就不添加了
                    if (repeatList?.size!! > 40) {
                        return
                    }
                    p0.address?.let { repeatList?.add(it) }
                    list?.add(BleBean(p0.device, p0.rssi,isScreen))
                    list?.sortBy {
                        Math.abs(it.rssi)
                    }

                    adapter?.notifyDataSetChanged()

                    }
                }

            }

            override fun onSearchStopped() {

            }

            override fun onSearchCanceled() {

            }

        }, 15 * 1000, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        BaseApplication.getInstance().bleManager.stopScan()
    }


    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            if(action == BleConstant.BLE_CONNECTED_ACTION){
                hideDialog()
                ToastUtils.show(resources.getString(R.string.string_scan_conn_success))
                getHasBindDevice()
            }
            if(action == BleConstant.BLE_DIS_CONNECT_ACTION){
                hideDialog()
                ToastUtils.show(resources.getString(R.string.string_scan_conn_failed))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                getHasBindDevice()
            }
        }

    }



    private fun unBindDevice(){
        val dialog = ConfirmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt(resources.getString(R.string.string_scan_unbind_prompt))
        dialog.setOnCommClickListener{
            dialog.dismiss()
            if(it == 0x01){
                MmkvUtils.saveConnDeviceMac("")
                MmkvUtils.saveConnDeviceName("")
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                getHasBindDevice()

                verifyScanFun(false)
            }

        }

    }
}