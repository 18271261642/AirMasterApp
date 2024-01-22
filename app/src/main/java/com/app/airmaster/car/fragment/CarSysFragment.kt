package com.app.airmaster.car.fragment

import android.view.View
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.widget.CommTitleView
import com.bonlala.widget.layout.SettingBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import timber.log.Timber

/**
 * 系统设置
 * Created by Admin
 *Date 2023/7/14
 */
class CarSysFragment : TitleBarFragment<CarSysSetActivity>() {


    private var sysSettingTitleView : CommTitleView ?= null

    companion object{
        fun getInstance() : CarSysFragment{
            return CarSysFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_sys_setting_layout
    }

    override fun initView() {
        sysSettingTitleView = findViewById(R.id.sysSettingTitleView)

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()

        //气罐气压
        findViewById<SettingBar>(R.id.sysGasBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarGassPressureFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        //工作模式
        findViewById<SettingBar>(R.id.sysWorkModelBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarWorkModelFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //高度尺
        findViewById<SettingBar>(R.id.sysScaleSettingBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarSysScaleFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //电瓶保护
        findViewById<SettingBar>(R.id.sysPowerProtectBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarPowerProtectFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        //点火熄火
        findViewById<SettingBar>(R.id.sysIgnitionBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarIgnitionFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //维修模式
        findViewById<SettingBar>(R.id.sysRepairBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarRepairFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //最细行驶保护气压
        findViewById<SettingBar>(R.id.sysLowestBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarLowestAirFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        //高度记忆设置
        findViewById<SettingBar>(R.id.carSysHeightMemoryBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarHeightMemoryFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //气压平衡
        findViewById<SettingBar>(R.id.sysAirBalanceBar).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarAirPressureBalanceFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        sysSettingTitleView?.setCommTitleTxt("系统设置")
        sysSettingTitleView?.setOnItemClick{
            finish()
        }
    }

    override fun initData() {

    }



}