package com.app.airmaster.car.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.car.adapter.CarTimerAdapter
import com.app.airmaster.car.bean.TimerBean
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.bonlala.widget.view.SwitchButton

/**
 * 点火熄火设置
 * Created by Admin
 *Date 2023/7/14
 */
class CarIgnitionFragment : TitleBarFragment<CarSysSetActivity>(){


    private var viewModel : ControlViewModel ?= null



    //熄火开关
    private var ingitionOffSwitch : SwitchButton ?= null
    //点火开关
    private var ingitionOnSwitch : SwitchButton ?= null


    private var sysIngitionTitleView : CommTitleView ?= null

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
        ingitionOnSwitch = findViewById(R.id.ingitionOnSwitch)
        ingitionOffSwitch = findViewById(R.id.ingitionOffSwitch)
        sysIngitionTitleView = findViewById(R.id.sysIngitionTitleView)
        carIgnitionRy = findViewById(R.id.carIgnitionRy)
        val linearLayoutManager = LinearLayoutManager(attachActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        carIgnitionRy?.layoutManager = linearLayoutManager
        list = ArrayList<TimerBean>()
        adapter = CarTimerAdapter(attachActivity, list as ArrayList<TimerBean>)
        carIgnitionRy?.adapter = adapter

        sysIngitionTitleView?.setCommTitleTxt("点火熄火设置")
        sysIngitionTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }


        ingitionOffSwitch?.setOnCheckedChangeListener { button, checked ->
            var code = if(checked) 1 else 0
            if(checked){
                list?.forEach {
                    if(it.isChecked){
                        code = it.time
                    }
                }
            }
            viewModel?.setStallSetting(code)
        }

        ingitionOnSwitch?.setOnCheckedChangeListener { button, checked ->
            var code = if(checked) 1 else 0
            if(checked){
                list?.forEach {
                    if(it.isChecked){
                        code = it.time
                    }
                }
            }

            viewModel?.setStartUpSetting(code)
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        list?.clear()
        val array = arrayListOf<TimerBean>(TimerBean("Low",0,false),
            TimerBean("1",1,false),
            TimerBean("2",2,false),
            TimerBean("3",3,false),
            TimerBean("4",4,false))
        list?.addAll(array)
        adapter?.notifyDataSetChanged()


        adapter?.setOnCommClickListener{
            list?.forEachIndexed { index, timerBean ->
                timerBean.isChecked = index == it
            }
            adapter?.notifyDataSetChanged()
        }
    }
}