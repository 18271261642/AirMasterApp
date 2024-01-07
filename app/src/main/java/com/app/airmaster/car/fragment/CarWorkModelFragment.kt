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
 * 工作模式
 * Created by Admin
 *Date 2023/7/14
 */
class CarWorkModelFragment : TitleBarFragment<CarSysSetActivity>() {



    companion object{
        fun getInstance() : CarWorkModelFragment{
            return CarWorkModelFragment()
        }
    }


    private var viewModel : ControlViewModel ?= null
    private var accModelSwitch : SwitchButton ?= null
    private var workModelStandbySwitch : SwitchButton ?= null
    private var sysWorkModelTitleView : CommTitleView ?= null

    private var workModelRy : RecyclerView ?= null
    private var list : MutableList<TimerBean> ?= null

    private var adapter : CarTimerAdapter ?= null

    override fun getLayoutId(): Int {
      return R.layout.fragment_car_work_model_layout
    }

    override fun initView() {
        workModelStandbySwitch = findViewById(R.id.workModelStandbySwitch)
        accModelSwitch = findViewById(R.id.accModelSwitch)
        sysWorkModelTitleView = findViewById(R.id.sysWorkModelTitleView)
        workModelRy = findViewById(R.id.workModelRy)
        val linearLayoutManager = LinearLayoutManager(attachActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        workModelRy?.layoutManager = linearLayoutManager
        list = ArrayList<TimerBean>()
        adapter = CarTimerAdapter(attachActivity, list as ArrayList<TimerBean>)
        workModelRy?.adapter = adapter

        sysWorkModelTitleView?.setCommTitleTxt("工作模式")
        sysWorkModelTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        list?.clear()
        val array = arrayListOf<TimerBean>(TimerBean("5分钟",5,false),
            TimerBean("10分钟",10,false),
            TimerBean("15分钟",15,false),
            TimerBean("20分钟",20,false),
            TimerBean("30分钟",30,false),
            TimerBean("60分钟",60,false))
        list?.addAll(array)
        adapter?.notifyDataSetChanged()


        adapter?.setOnCommClickListener{
            list?.forEachIndexed { index, timerBean ->
                timerBean.isChecked = index ==it
            }
            val time = list?.get(it)?.time
            viewModel?.setAccModel(time!!)
            adapter?.notifyDataSetChanged()

        }


        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
            workModelStandbySwitch?.isChecked = it.accModel == 1
            accModelSwitch?.isChecked = it.accModel == 0
            if(it.sleepTime != 0){
                list?.forEachIndexed { index, timerBean ->
                    timerBean.isChecked = it.sleepTime == timerBean.time
                }
                adapter?.notifyDataSetChanged()
            }
        }

        viewModel?.writeCommonFunction()
    }
}