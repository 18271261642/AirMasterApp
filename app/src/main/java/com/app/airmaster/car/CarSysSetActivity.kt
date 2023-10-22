package com.app.airmaster.car

import androidx.fragment.app.FragmentManager
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.fragment.CarSysFragment

/**
 * 系统设置
 * Created by Admin
 *Date 2023/7/14
 */
class CarSysSetActivity : AppActivity() {

    var fragmentManager : FragmentManager ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_sys_set_layout
    }

    override fun initView() {

    }

    override fun initData() {
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.add(R.id.stsSetFrameLayout,CarSysFragment.getInstance())
        fragmentTransaction.commit()
    }
}