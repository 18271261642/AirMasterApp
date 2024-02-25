package com.app.airmaster.car.fragment

import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.CarAboutActivity
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.car.CarSystemCheckActivity
import com.app.airmaster.car.ShowWebActivity
import com.app.airmaster.second.SecondScanActivity
import com.bonlala.widget.layout.SettingBar

/**
 * Created by Admin
 *Date 2023/7/14
 */
class HomeSettingFragment : TitleBarFragment<CarHomeActivity>() {


    companion object{
        fun getInstance() : HomeSettingFragment{
            return HomeSettingFragment()
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_home_setting_layout
    }

    override fun initView() {
        findViewById<SettingBar>(R.id.sysConnDeviceBar).setOnClickListener {
            startActivity(SecondScanActivity::class.java)
        }
        findViewById<SettingBar>(R.id.sysSysBar).setOnClickListener {
            if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                showNotConnDialog()
                return@setOnClickListener
            }
            startActivity(CarSysSetActivity::class.java)
        }
        findViewById<SettingBar>(R.id.sysCheckBar).setOnClickListener {
            startActivity(CarSystemCheckActivity::class.java)
        }
        findViewById<SettingBar>(R.id.sysAboutBar).setOnClickListener {
            startActivity(CarAboutActivity::class.java)
        }
    }

    override fun initData() {

    }

    private fun showNotConnDialog(){
        attachActivity?.showCommAlertDialog("未连接设备","去官网","去连接",object :
            OnCommItemClickListener {
            override fun onItemClick(position: Int) {
                attachActivity.disCommAlertDialog()
                if(position == 0x01){
                    startActivity(SecondScanActivity::class.java)
                }
                if(position == 0x00){
                    startActivity(ShowWebActivity::class.java)
                }
            }

        })
    }
}