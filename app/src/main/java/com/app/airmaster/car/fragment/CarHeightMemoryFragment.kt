package com.app.airmaster.car.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.car.adapter.CarTimerAdapter
import com.app.airmaster.car.bean.TimerBean
import com.app.airmaster.widget.CommTitleView

/**
 * 高度记忆设置
 * Created by Admin
 *Date 2023/7/18
 */
class CarHeightMemoryFragment : TitleBarFragment<CarSysSetActivity>(){

    companion object{
        fun getInstance() : CarHeightMemoryFragment{
            return CarHeightMemoryFragment()
        }
    }

    private var sysHeightMemoryTitleView : CommTitleView ?= null

    private var carHeightMemoryRy : RecyclerView ?= null
    private var list : MutableList<TimerBean> ?= null

    private var adapter : CarTimerAdapter?= null


    override fun getLayoutId(): Int {
       return R.layout.fragment_car_height_memory_layout
    }

    override fun initView() {
        sysHeightMemoryTitleView = findViewById(R.id.sysHeightMemoryTitleView)
        carHeightMemoryRy = findViewById(R.id.carHeightMemoryRy)
        val linearLayoutManager = LinearLayoutManager(attachActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        carHeightMemoryRy?.layoutManager = linearLayoutManager
        list = ArrayList<TimerBean>()
        adapter = CarTimerAdapter(attachActivity, list as ArrayList<TimerBean>)
        carHeightMemoryRy?.adapter = adapter
        sysHeightMemoryTitleView?.setCommTitleTxt("高度记忆设置")
        sysHeightMemoryTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }
    }

    override fun initData() {
        list?.clear()
        val array = arrayListOf<TimerBean>(
            TimerBean("1",false),
            TimerBean("2",false),
            TimerBean("3",false),
            TimerBean("4",false),
        TimerBean("LOW",false))
        list?.addAll(array)
        adapter?.notifyDataSetChanged()
    }
}