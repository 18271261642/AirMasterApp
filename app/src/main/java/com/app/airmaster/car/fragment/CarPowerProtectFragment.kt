package com.app.airmaster.car.fragment

import android.os.Build
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.ble.ota.BluetoothLeClass.OnWriteDataListener
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.utils.CalculateUtils
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.widget.view.SwitchButton
import timber.log.Timber

/**
 * 电瓶保护
 * Created by Admin
 *Date 2023/7/14
 */
class CarPowerProtectFragment : TitleBarFragment<CarSysSetActivity>(){

    private var viewModel : ControlViewModel ?= null

    companion object{
        fun getInstance() : CarPowerProtectFragment{
            return CarPowerProtectFragment()
        }
    }

    private var sysPowerProtectTitleView : CommTitleView ?= null

    private var powerProtectValueTv : TextView ?= null
    private var powerSeekBar : SeekBar ?= null

    //高压保护
    private var powerHeightBtn : SwitchButton ?= null


    override fun getLayoutId(): Int {
        return R.layout.fragment_car_power_protect_layout
    }

    override fun initView() {
        sysPowerProtectTitleView = findViewById(R.id.sysPowerProtectTitleView)
        powerHeightBtn = findViewById(R.id.powerHeightBtn)
        powerProtectValueTv = findViewById(R.id.powerProtectValueTv)
        powerSeekBar = findViewById(R.id.powerSeekBar)

        sysPowerProtectTitleView?.setCommTitleTxt(resources.getString(R.string.string_set_battery_protect))
        sysPowerProtectTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        powerHeightBtn?.setOnCheckedChangeListener { button, checked ->
            if(button.isPressed){
                return@setOnCheckedChangeListener
            }
            sendHeightProtect(checked)

        }


    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        powerSeekBar?.max = 120
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            powerSeekBar?.min = 98
        }

        powerSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val bt = p0?.progress
                var bv = CalculateUtils.div(bt!!.toDouble(),10.0,1)
                Timber.e("------电压="+p0?.progress+" "+bv)
                if(bv<=10){
                    bv = 10.0
                }
                powerProtectValueTv?.text = "$bv V"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sendPower()
            }

        })

        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
            powerHeightBtn?.isChecked = it.isHeightVoltage
            powerSeekBar?.progress = it.lowVoltage
            val v = it.lowVoltage/10F
            Timber.e("-----电压--v="+v)
            powerProtectValueTv?.text =  String.format("%.1f",v)+" V"
        }

        viewModel?.writeCommonFunction()

    }


    private fun sendPower(){
        val powerValue = powerSeekBar?.progress
        val crcStr = "0005040117"+String.format("%02x",powerValue)
        val crc = Utils.crcCarContentArray(crcStr)

        val str =  "011E"+CarConstant.CAR_HEAD_BYTE_STR+crcStr+crc

        val arry = Utils.stringToByte(str)
        val result = Utils.getFullPackage(arry)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {
                //8800000000000cee030f7ffaaf0005010497015e
                if(data != null && data.size>15){
                    //8800000000000cc6 030f 7f fa af 00 05 01 04 aa 01 4b
                    //Timber.e("-------气罐压力="+(data[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                    if((data[8].toInt().and(0xFF)) == 3 && (data[9].toInt().and(0xFF)) == 15 &&(data[17].toInt().and(0xFF) == 151
                                )){
                        if(data[18].toInt().and(0xFF) == 1){
                            BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                        }
                    }
                }
            }

        })
    }


    private fun sendHeightProtect(open : Boolean){
        val crcStr = "0005040118"+(if(open) "01" else "00")
        val crc = Utils.crcCarContentArray(crcStr)
        val str = "011E"+CarConstant.CAR_HEAD_BYTE_STR+crcStr+crc
        val arry = Utils.stringToByte(str)
        val result = Utils.getFullPackage(arry)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {
                if(data != null && data.size>15){
                    //8800000000000cc6 030f 7f fa af 00 05 01 04 aa 01 4b
                    //Timber.e("-------气罐压力="+(data[8].toInt().and(0xFF))+" "+(it[9].toInt().and(0xFF))+" "+(it[18].toInt().and(0xFF) == 1))
                    if((data[8].toInt().and(0xFF)) == 3 && (data[9].toInt().and(0xFF)) == 15 &&(data[17].toInt().and(0xFF) == 152
                                )){
                        if(data[18].toInt().and(0xFF) == 1){
                            BaseApplication.getBaseApplication().bleOperate.setCommAllParams()
                        }
                    }
                }
            }

        })
    }
}