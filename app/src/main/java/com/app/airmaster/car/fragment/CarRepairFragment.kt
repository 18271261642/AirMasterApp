package com.app.airmaster.car.fragment

import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity

/**
 * 维修模式
 * Created by Admin
 *Date 2023/7/14
 */
class CarRepairFragment : TitleBarFragment<CarSysSetActivity>() {


    companion object{
        fun getInstance() : CarRepairFragment{
            return CarRepairFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_car_repair_layout
    }

    override fun initView() {

    }

    override fun initData() {

    }


}