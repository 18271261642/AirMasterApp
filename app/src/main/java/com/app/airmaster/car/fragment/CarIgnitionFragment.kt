package com.app.airmaster.car.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.car.adapter.CarTimerAdapter
import com.app.airmaster.car.bean.TimerBean

/**
 * 点火熄火设置
 * Created by Admin
 *Date 2023/7/14
 */
class CarIgnitionFragment : TitleBarFragment<CarSysSetActivity>(){


    private var carIgnitionRy : RecyclerView ?= null

    private var list : MutableList<TimerBean> ?= null

    private var adapter : CarTimerAdapter?= null

    companion object{
        fun getInstance() : CarIgnitionFragment{
            return CarIgnitionFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_car_ingition_layout
    }

    override fun initView() {
        carIgnitionRy = findViewById(R.id.carIgnitionRy)
        val linearLayoutManager = LinearLayoutManager(attachActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        carIgnitionRy?.layoutManager = linearLayoutManager
        list = ArrayList<TimerBean>()
        adapter = CarTimerAdapter(attachActivity, list as ArrayList<TimerBean>)
        carIgnitionRy?.adapter = adapter
    }

    override fun initData() {
        list?.clear()
        val array = arrayListOf<TimerBean>(TimerBean("Low",false),
            TimerBean("1",false),
            TimerBean("2",false),
            TimerBean("3",false),
            TimerBean("4",false))
        list?.addAll(array)
        adapter?.notifyDataSetChanged()
    }
}