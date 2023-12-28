package com.app.airmaster.car.fragment

import android.view.View
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.bonlala.widget.layout.SettingBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils

/**
 * 系统设置
 * Created by Admin
 *Date 2023/7/14
 */
class CarSysFragment : TitleBarFragment<CarSysSetActivity>() {


    private var sysTitleBar : TitleBar ?= null

    companion object{
        fun getInstance() : CarSysFragment{
            return CarSysFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_sys_setting_layout
    }

    override fun initView() {
        sysTitleBar = findViewById(R.id.sysTitleBar)

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()

        //气罐气压
        findViewById<SettingBar>(R.id.sysGasBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarGassPressureFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        //工作模式
        findViewById<SettingBar>(R.id.sysWorkModelBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarWorkModelFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //电瓶保护
        findViewById<SettingBar>(R.id.sysPowerProtectBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarPowerProtectFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        //点火熄火
        findViewById<SettingBar>(R.id.sysIgnitionBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarIgnitionFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //维修模式
        findViewById<SettingBar>(R.id.sysRepairBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarRepairFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //最细行驶保护气压
        findViewById<SettingBar>(R.id.sysLowestBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarLowestAirFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        //高度记忆设置
        findViewById<SettingBar>(R.id.carSysHeightMemoryBar).setOnClickListener {
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarHeightMemoryFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        sysTitleBar?.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(view: View?) {
                ToastUtils.show("ddddddd")
                (attachActivity as CarSysSetActivity).finish()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {

            }

        })
    }

    override fun initData() {

    }
}