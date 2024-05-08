package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.view.CarHomeCenterView
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import timber.log.Timber

class ManualSetHeightView : AppCompatDialog {

    private var onClick : OnCommItemClickListener?= null

    fun setOnDialogClickListener(onCommItemClickListener: OnCommItemClickListener){
        this.onClick = onCommItemClickListener
    }

    private var manualHeightView :CarHomeCenterView ?= null
    private var manualDialogCancelTv : ShapeTextView ?= null
    private var manualDialogConfirmTv : ShapeTextView ?= null

    private var manualHeightOrLowDescTv : TextView ?= null

    private var manualTitleTv : TextView ?= null


    //是否是最高
    private var isHeight = false


    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_manual_set_height_layout)

        initViews()
        initData()
    }



    private fun initData(){
        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener{
            manualHeightView?.setLeftRearPressureValue(it.leftRearPressure)
            manualHeightView?.setRightTopPressureValue(it.rightPressure)
            manualHeightView?.setLeftTopPressureValue(it.leftPressure)
            manualHeightView?.setRightRearPressureValue(it.rightRearPressure)

            manualHeightView?.setFrontHeightValue(it.leftFrontRulerFL,it.rightFrontRulerFL)
            manualHeightView?.setAfterHeightValue(it.leftRearRulerFL,it.rightRearRulerFL)

            manualHeightView?.setFrontGoalValue(it.leftFrontGoalFL,it.rightFrontGoalFL)
            manualHeightView?.setRearGoalValue(it.leftRearGoalFL,it.rightRearGoalFL)

        }


        manualHeightView?.setOnPressureListener(object : OnControlPressureCheckedListener {
            override fun onItemChecked(map: MutableMap<Int, Int>?) {

                if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                   ToastUtils.show("未连接!")
                    return
                }
                setManualOperation(map as HashMap<Int, Int>)

            }

        })
    }


    private fun initViews() {
        manualTitleTv = findViewById(R.id.manualTitleTv)
        manualHeightView = findViewById(R.id.manualHeightView)
        manualDialogCancelTv = findViewById(R.id.manualDialogCancelTv)
        manualDialogConfirmTv = findViewById(R.id.manualDialogConfirmTv)
        manualHeightOrLowDescTv = findViewById(R.id.manualHeightOrLowDescTv)
        manualHeightView?.setFrontImage()
        manualDialogCancelTv?.setOnClickListener {
            onClick?.onItemClick(0x00)
        }

        manualDialogConfirmTv?.setOnClickListener {
            setHeightOrLow(isHeight)
            onClick?.onItemClick(0x01)
        }
    }

    //手动调整轮子充气或放气
    private fun setManualOperation(map : HashMap<Int,Int>){
        Timber.e("---map="+map.toString())
        var keyCode = 0
        var valueCode = 0
        map.forEach {
            keyCode = it.key
            valueCode = it.value
        }

        val timeArray = Utils.intToSecondByteArrayHeight(150)
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


        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)


        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result){
            //8800000000000cd6030f7ffaaf00050104920163
            if(it != null && it.size>15){
                Timber.e("-------气罐压力="+(it[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[17].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                if((it[8].toInt().and(0xFF)) == 3 && (it[9].toInt().and(0xFF)) == 15 &&((it[17].toInt().and(0xFF) == 175) ||(it[17].toInt().and(0xFF) == 146) )){
                    if(it[18].toInt().and(0xFF) == 1){
                        BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                    }
                }
            }
        }

    }


    fun setModel(height: Boolean){
        this.isHeight = height
        manualHeightOrLowDescTv?.text = if(isHeight) "请设定最高高度,\n设置完成后点击保存" else "请设定最低高度,\n设置完成后点击保存"
        manualTitleTv?.text = if(isHeight)"4/6检测最高高度" else "1/6检测最低高度"
    }



    //设定当前高度为最高或最低值
    private fun setHeightOrLow(height : Boolean){
        val scrStr = "00040401"+(if(height)"20" else "21")
        val crc = Utils.crcCarContentArray(scrStr)
        val str = "011E"+ CarConstant.CAR_HEAD_BYTE_STR+scrStr+crc
        val resultArray = Utils.hexStringToByte(str)
        val result = Utils.getFullPackage(resultArray)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByteNoBack(result)
    }

}