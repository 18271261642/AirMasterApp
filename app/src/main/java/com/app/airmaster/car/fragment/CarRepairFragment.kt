package com.app.airmaster.car.fragment

import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.bonlala.widget.view.SwitchButton

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



    override fun getLayoutId(): Int {
        return R.layout.fragment_car_repair_layout
    }

    override fun initView() {
        repairModelSwitch = findViewById(R.id.repairModelSwitch)
        sysRepairTitleView = findViewById(R.id.sysRepairTitleView)
        sysRepairTitleView?.setCommTitleTxt("维修模式")
        sysRepairTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        repairModelSwitch?.setOnCheckedChangeListener { button, checked ->
            viewModel?.setRepairModel(checked)
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
    }


}