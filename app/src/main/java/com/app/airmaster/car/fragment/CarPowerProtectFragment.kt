package com.app.airmaster.car.fragment

import android.os.Build
import android.widget.SeekBar
import android.widget.TextView
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.ble.ota.BluetoothLeClass.OnWriteDataListener
import com.app.airmaster.car.CarSysSetActivity
import com.blala.blalable.Utils
import com.blala.blalable.car.CarConstant
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.widget.view.SwitchButton

/**
 * 电瓶保护
 * Created by Admin
 *Date 2023/7/14
 */
class CarPowerProtectFragment : TitleBarFragment<CarSysSetActivity>(){


    companion object{
        fun getInstance() : CarPowerProtectFragment{
            return CarPowerProtectFragment()
        }
    }

    private var powerProtectValueTv : TextView ?= null
    private var powerSeekBar : SeekBar ?= null

    //高压保护
    private var powerHeightBtn : SwitchButton ?= null


    override fun getLayoutId(): Int {
        return R.layout.fragment_car_power_protect_layout
    }

    override fun initView() {
        powerHeightBtn = findViewById(R.id.powerHeightBtn)
        powerProtectValueTv = findViewById(R.id.powerProtectValueTv)
        powerSeekBar = findViewById(R.id.powerSeekBar)

        powerHeightBtn?.setOnCheckedChangeListener { button, checked ->
//            if(!button.isPressed){
//                return@setOnCheckedChangeListener
//            }
            sendHeightProtect(checked)

        }
    }

    override fun initData() {
        powerSeekBar?.max = 130
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            powerSeekBar?.min = 100
        }

        powerSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                powerProtectValueTv?.text = (p0?.progress?.div(10))?.toInt().toString()+" V"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sendPower()
            }

        })

    }


    private fun sendPower(){
        val powerValue = powerSeekBar?.progress

        val crcStr = "000517"+String.format("%02x",powerValue)
        val crc = Utils.crcCarContentArray(crcStr)

        val str =  "011E"+CarConstant.CAR_HEAD_BYTE_STR+crcStr+crc

        val arry = Utils.stringToByte(str)
        val result = Utils.getFullPackage(arry)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {

            }

        })
    }


    private fun sendHeightProtect(open : Boolean){
        val crcStr = "000518"+(if(open) "01" else "00")
        val crc = Utils.crcCarContentArray(crcStr)
        val str = "011E"+CarConstant.CAR_HEAD_BYTE_STR+crcStr+crc
        val arry = Utils.stringToByte(str)
        val result = Utils.getFullPackage(arry)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(result,object :
            WriteBackDataListener {
            override fun backWriteData(data: ByteArray?) {

            }

        })
    }
}