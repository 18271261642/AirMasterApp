package com.app.airmaster.car.fragment

import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.widget.CommTitleView


/**
 * 高度尺
 */
class CarSysScaleFragment : TitleBarFragment<CarSysSetActivity>() {


    private var sysScaleTitleView : CommTitleView ?= null


    companion object{
        fun getInstance() : CarSysScaleFragment{
            return CarSysScaleFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_car_scale_layout
    }

    override fun initView() {
        sysScaleTitleView = findViewById(R.id.sysScaleTitleView)
    }

    override fun initData() {
        sysScaleTitleView?.setCommTitleTxt("高度尺工具")
        sysScaleTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

    }
}