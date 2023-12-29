package com.app.airmaster.car.fragment

import android.os.Build
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.ble.ota.Utils
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.widget.VerticalSeekBar
import com.app.airmaster.widget.VerticalSeekBar.OnSeekBarChangeListener
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import timber.log.Timber

/**
 * 气罐压力
 * Created by Admin
 *Date 2023/7/14
 */
class CarGassPressureFragment : TitleBarFragment<CarSysSetActivity>(){

    companion object{
        fun getInstance() : CarGassPressureFragment{
            return CarGassPressureFragment()
        }
    }

    //高压
    private var gassPressureHeightSeekBar : SeekBar ?= null
    //低压
    private var gassPressureLowSeekBar : SeekBar ?= null

    private var pHTv : TextView ?= null
    private var pLowTv : TextView ?= null






    override fun getLayoutId(): Int {
        return R.layout.fragment_car_gass_pressure_layout
    }

    override fun initView() {
        pHTv = findViewById(R.id.pHTv)
        pLowTv = findViewById(R.id.pLowTv)
        gassPressureHeightSeekBar = findViewById(R.id.gassPressureHeightSeekBar)
        gassPressureLowSeekBar = findViewById(R.id.gassPressureLowSeekBar)
    }

    override fun initData() {
        gassPressureHeightSeekBar?.max = 190
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gassPressureHeightSeekBar?.min = 100
            gassPressureLowSeekBar?.min = 70
        }
        gassPressureLowSeekBar?.max = 170

        gassPressureHeightSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                pHTv?.text = String.format(resources.getString(R.string.string_pressure_h),p0?.progress.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sendPressure()
            }

        })


        gassPressureLowSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                pLowTv?.text = String.format(resources.getString(R.string.string_pressure_l),p0?.progress.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sendPressure()
            }

        })

    }


    //发送指令
    private fun sendPressure(){
        val height = gassPressureHeightSeekBar?.progress
        val low = gassPressureLowSeekBar?.progress

        val h = String.format("%02x",height)
        val l = String.format("%02x",low)

        val contentStr = "040114$h$l"
        val contentArray = com.blala.blalable.Utils.hexStringToByte(contentStr)
        val lengthArray = com.blala.blalable.Utils.intToSecondByteArrayHeight(contentArray.size+1)
        val lenthStr = com.blala.blalable.Utils.getHexString(lengthArray)

        val resultContent = lenthStr+contentStr

        val crcStr = com.blala.blalable.Utils.crcCarContentArray(resultContent)

        val str = "011E"+CarConstant.CAR_HEAD_BYTE_STR+resultContent+crcStr
        val strArray = com.blala.blalable.Utils.hexStringToByte(str)
        val resultArray = com.blala.blalable.Utils.getFullPackage(strArray)
        Log.e("TAG","-----ddddd---------"+str+"  "+com.blala.blalable.Utils.getHexString(resultArray))


        val array = com.blala.blalable.Utils.stringToByte(str)
        val result = com.blala.blalable.Utils.getFullPackage(array)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {

            }

        })
    }
}