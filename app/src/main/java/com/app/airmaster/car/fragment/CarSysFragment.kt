package com.app.airmaster.car.fragment

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.ControlViewModel
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

    private var viewModel : ControlViewModel?= null

    private var sysSettingTitleView : CommTitleView ?= null

    //记忆模式
    private var sysMemoryModelBar : SettingBar ?= null
    //高度尺
    private var sysScaleSettingBar : SettingBar ?= null
    //气压平衡
    private var sysAirBalanceBar : SettingBar ?= null

    companion object{
        fun getInstance() : CarSysFragment{
            return CarSysFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_sys_setting_layout
    }

    override fun initView() {
        sysAirBalanceBar = findViewById(R.id.sysAirBalanceBar)
        sysScaleSettingBar = findViewById(R.id.sysScaleSettingBar)
        sysMemoryModelBar = findViewById(R.id.sysMemoryModelBar)
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

        //记忆模式
        sysMemoryModelBar?.setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarMemoryModelFragment.getInstance())
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
        sysScaleSettingBar?.setOnClickListener {
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
        sysAirBalanceBar?.setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            fragmentTransaction.replace(R.id.stsSetFrameLayout,CarAirPressureBalanceFragment.getInstance())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        sysSettingTitleView?.setCommTitleTxt(resources.getString(R.string.string_setting_menu_set))
        sysSettingTitleView?.setOnItemClick{
            finish()
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        checkDeviceModel()
        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }

            //高度记忆或压力记忆 0高度记忆；1压力记忆
            val model = it.modelType

            if(model == 0){ //高度记忆
                //高度尺
                sysScaleSettingBar?.visibility = View.VISIBLE
                //气压平衡
                sysAirBalanceBar?.visibility = View.VISIBLE
            }

            if(model == 1){  //压力记忆
                //高度尺
                sysScaleSettingBar?.visibility = View.GONE
                //气压平衡
                sysAirBalanceBar?.visibility = View.GONE
            }

        }
    }


    override fun onResume() {
        super.onResume()

        Timber.e("-------onResume-------")
        checkDeviceModel()

//        val autoBean = BaseApplication.getBaseApplication().autoBackBean
//        if(autoBean != null){
//            sysMemoryModelBar?.visibility = if(autoBean.deviceMode == 0) View.VISIBLE else View.GONE
//
//            //气压模式下高度尺和气压平衡没有
//            if(autoBean.deviceMode == 1){    //气压版本
//                //记忆模式
//                sysMemoryModelBar?.visibility = View.GONE
//                //高度尺工具
//                sysScaleSettingBar?.visibility = View.GONE
//                //气压平衡
//                sysAirBalanceBar?.visibility = View.GONE
//            }else{  //气压+高度版本
//                sysMemoryModelBar?.visibility = View.VISIBLE
//                sysScaleSettingBar?.visibility = View.VISIBLE
//                sysAirBalanceBar?.visibility = View.VISIBLE
//
//            }
//        }
       // viewModel?.writeCommonFunction()
    }



    private fun checkDeviceModel(){
        val isPressureModel = MmkvUtils.getPressureModel()
        if(isPressureModel){
            //记忆模式
          //  sysMemoryModelBar?.visibility = View.GONE
            //高度尺工具
            sysScaleSettingBar?.visibility = View.GONE
            //气压平衡
            sysAirBalanceBar?.visibility = View.GONE
        }else{
           // sysMemoryModelBar?.visibility = View.VISIBLE
            sysScaleSettingBar?.visibility = View.VISIBLE
            sysAirBalanceBar?.visibility = View.VISIBLE
        }
    }

}