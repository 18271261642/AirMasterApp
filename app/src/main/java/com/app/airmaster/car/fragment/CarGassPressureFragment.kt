package com.app.airmaster.car.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import timber.log.Timber
import kotlin.experimental.and

/**
 * 气罐压力
 * Created by Admin
 *Date 2023/7/14
 */
class CarGassPressureFragment : TitleBarFragment<CarSysSetActivity>(){

    private var viewModel : ControlViewModel?= null

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


    private var sysGassTitleView : CommTitleView ?= null





    override fun getLayoutId(): Int {
        return R.layout.fragment_car_gass_pressure_layout
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initView() {
        sysGassTitleView = findViewById(R.id.sysGassTitleView)
        pHTv = findViewById(R.id.pHTv)
        pLowTv = findViewById(R.id.pLowTv)
        gassPressureHeightSeekBar = findViewById(R.id.gassPressureHeightSeekBar)
        gassPressureLowSeekBar = findViewById(R.id.gassPressureLowSeekBar)

        sysGassTitleView?.setCommTitleTxt("气罐压力")

        sysGassTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
                fragmentManager.popBackStack()
        }

    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
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


        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
            gassPressureHeightSeekBar?.progress = it.gasTankHeightPressure
            gassPressureLowSeekBar?.progress = it.gasTankLowPressure
            pLowTv?.text = String.format(resources.getString(R.string.string_pressure_l),it.gasTankLowPressure)
            pHTv?.text = String.format(resources.getString(R.string.string_pressure_h),it.gasTankHeightPressure)
        }

        viewModel?.writeCommonFunction()

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
                //88 00 00 00 00 00 0c d2 03 0f 7f fa af 00 05 01 04 94 01 61
                // 8800000000000cd2030f7ffaaf00050104940161
                Timber.e("--------设置气罐压力返回="+Utils.formatBtArrayToString(data))
                if(data != null && data.size>15){
                    Timber.e("-------气罐压力="+(data[8].toInt().and(0xFF))+" "+(data[9].toInt().and(0xFF))+" "+(data[18].toInt().and(0xFF) == 1))
                    if((data[8].toInt().and(0xFF)) == 3 && (data[9].toInt().and(0xFF)) == 15 && (data[17].toInt().and(0xFF) == 148)){
                        if(data[18].toInt().and(0xFF) == 1){
                            BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                        }
                    }
                }
            }

        })
    }
}