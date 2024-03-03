package com.app.airmaster.dialog


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.adapter.ScanDeviceAdapter
import com.app.airmaster.bean.BleBean
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.OnCarVersionListener
import com.blala.blalable.Utils
import com.blala.blalable.listener.BleConnStatusListener
import com.blala.blalable.listener.ConnStatusListener
import com.blala.blalable.listener.OnCarWatchBackListener
import com.blala.blalable.listener.OnCommBackDataListener
import com.blala.blalable.listener.WriteBackDataListener
import com.google.gson.Gson
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import timber.log.Timber
import java.util.*

/**
 * Created by Admin
 *Date 2023/1/12
 */
class DialogScanDeviceView : AppCompatDialog {

    private var list : MutableList<BleBean> ?= null
    private var scanDeviceAdapter : ScanDeviceAdapter ?= null
    private var recyclerView : RecyclerView ?= null

    //用于去重的list
    private var repeatList : MutableList<String> ?= null



    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
            }
        }
    }


    private var backStrListener : OnCarVersionListener?= null

    fun setOnBackDataListener(l : OnCarVersionListener){
        this.backStrListener = l
    }


    private var onClick : OnCommItemClickListener ?= null

    fun setOnDialogClickListener(onCommItemClickListener: OnCommItemClickListener){
        this.onClick = onCommItemClickListener
    }

    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context,theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_scan_device_layout)

        initViews()
    }

    private fun initViews(){
        recyclerView = findViewById(R.id.scanRecyclerView)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        recyclerView?.layoutManager = linearLayoutManager
        list = mutableListOf()
        scanDeviceAdapter = ScanDeviceAdapter(context, list!!)
        recyclerView?.adapter = scanDeviceAdapter
        repeatList = mutableListOf()
        scanDeviceAdapter!!.setOnItemClick(onItemClick)
    }


    private val onItemClick : OnCommItemClickListener =
        OnCommItemClickListener { position ->

            onClick?.onItemClick(0x00)
          //  val service = BaseApplication.getBaseApplication().connStatusService
            val bean = list?.get(position)
            if (bean != null) {
                if(bean?.isConnStatus == 1){
                    return@OnCommItemClickListener
                }
                bean?.isConnStatus = 1
                scanDeviceAdapter?.notifyItemChanged(position)
                val name = bean.bluetoothDevice.name
                val mac = bean.bluetoothDevice.address
                BaseApplication.getBaseApplication().bleOperate.connYakDevice(name,bean.bluetoothDevice.address,object : ConnStatusListener{
                    override fun connStatus(status: Int) {

                    }

                    override fun setNoticeStatus(code: Int) {

                        getDeviceVersion(name,mac)

                    }

                })

                handlers.sendEmptyMessageDelayed(0x00,500)

            }
        }

    //开始扫描
     fun startScan(){

        BaseApplication.getBaseApplication().bleOperate.scanBleDevice(object : SearchResponse{

            override fun onSearchStarted() {

            }

            override fun onDeviceFounded(p0: SearchResult) {
                if(p0.getScanRecord() == null || p0.getScanRecord().isEmpty())
                    return
               // Timber.e("--------扫描="+p0.name+" "+Utils.formatBtArrayToString(p0.getScanRecord()))

                    val recordStr = Utils.formatBtArrayToString(p0.getScanRecord())
                    val bleName = p0.name

                if(BikeUtils.isEmpty(bleName) || bleName.equals("NULL") || BikeUtils.isEmpty(p0.address))
                    return
                if(repeatList?.contains(p0.address) == true)
                    return
                //030543
                if(!BikeUtils.isEmpty(recordStr) && (recordStr.lowercase(Locale.ROOT).contains("c003") || recordStr.contains("c01b") || recordStr.contains("1bc0"))){
                    //判断少于40个设备就不添加了
                    if(repeatList?.size!! >40){
                        return
                    }
                    p0.address?.let { repeatList?.add(it) }
                    list?.add(BleBean(p0.device,p0.rssi))
                    list?.sortBy {
                        Math.abs(it.rssi)
                    }

                    scanDeviceAdapter?.notifyDataSetChanged()
                }

            }

            override fun onSearchStopped() {

            }

            override fun onSearchCanceled() {

            }

        },15 * 1000,1)
    }

    override fun dismiss() {
        super.dismiss()
        Timber.e("------d关闭=====")
        BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
    }



    //获取固件版本
    fun getDeviceVersion(name : String,mac : String){
        val str = "8800000000000300001900"
        val byteArray = Utils.hexStringToByte(str)
        BaseApplication.getBaseApplication().bleOperate.writeCarWatchData(byteArray,object :
            OnCarWatchBackListener {
            override fun backWriteBytes(data: ByteArray?) {
                //880000000000102200 02 c019 000107 d91df96706a3 000101
                //88000000000022a100 1a 010010 c019 000107 d91df96706a3 00800001 0202000a02040301020304010001
                //88000000000022a100 1a 01 0010c019 00 01 07 d91df96706a3 00800001  020200 0a 0204 03 01020304010001
                //880000000000365400 1a 01 0007 00800001  03 06 03   02 0009 c019 02 fffff9 00010a  03000f fffe03fffffc112233fffffa445566  040009 fffe04fffffc778899
                if(data == null)
                    return
                if(data.size >14){
                    if(data[9].toInt().and(0xFF) == 26){
                        val bean = DeviceBinVersionBean()

                        //bin文件的匹配码 00800001
                        val bindCode = String.format("%02x",data[13])+String.format("%02x",data[14])+String.format("%02x",data[15])+String.format("%02x",data[16])
                        //识别码
                        val identificationCode = String.format("%02x",data[25])+String.format("%02x",data[26])+String.format("%02x",data[27])+String.format("%02x",data[28])
                        //版本
                        val version = "V"+data[29].toInt()+"."+data[30].toInt()+"."+data[31]
                        val versionInt = Utils.getIntFromBytes(0x00,data[29],data[30],data[31])
                        //型号
                        val model = String.format("%02x",data[23])+String.format("%02x",data[24])


                        bean.deviceName = name
                        bean.deviceMac = mac

                        bean.identificationCode = identificationCode
                        bean.productCode = model
                        bean.versionStr = version
                        bean.versionCode =versionInt
                        bean.binCode = bindCode

                        Timber.e("---------手表版本:"+Gson().toJson(bean))

                        backStrListener?.backVersion(bean)
                    }
                }
            }

        })
    }
}