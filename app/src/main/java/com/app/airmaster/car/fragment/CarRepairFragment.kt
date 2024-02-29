package com.app.airmaster.car.fragment

import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.bonlala.widget.view.SwitchButton
import timber.log.Timber

/**
 * 维修模式
 * Created by Admin
 *Date 2023/7/14
 */
class CarRepairFragment : TitleBarFragment<CarSysSetActivity>() {


    private var viewModel : ControlViewModel ?= null


    companion object{
        fun getInstance() : CarRepairFragment{
            return CarRepairFragment()
        }
    }


    private var repairModelSwitch : SwitchButton ?= null

    private var sysRepairTitleView : CommTitleView ?= null

    private var clickNumber = 0

    override fun getLayoutId(): Int {
        return R.layout.fragment_car_repair_layout
    }

    override fun initView() {
        repairModelSwitch = findViewById(R.id.repairModelSwitch)
        sysRepairTitleView = findViewById(R.id.sysRepairTitleView)
        sysRepairTitleView?.setCommTitleTxt(resources.getString(R.string.string_set_service_model))
        sysRepairTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        //是否是维修模式
        repairModelSwitch?.isChecked = BaseApplication.getBaseApplication().autoBackBean != null &&  BaseApplication.getBaseApplication().autoBackBean.workModel == 3

        repairModelSwitch?.setOnCheckedChangeListener { button, checked ->
            Timber.e("-------button="+ button.isPressed )
            if(!button.isPressed){
                clickNumber = 0
                viewModel?.setRepairModel(checked)
            }

        }


    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        viewModel?.autoSetBeanData?.observe(this){
            clickNumber++
            if(clickNumber>3){

                //是否是维修模式
                repairModelSwitch?.isChecked = BaseApplication.getBaseApplication().autoBackBean != null &&  BaseApplication.getBaseApplication().autoBackBean.workModel == 3
            }

        }

        viewModel?.writeCommonFunction()
    }


}