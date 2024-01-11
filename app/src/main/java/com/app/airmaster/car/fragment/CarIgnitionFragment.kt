package com.app.airmaster.car.fragment

import android.view.View
import android.widget.LinearLayout
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

    private var accTurnOnLayout : LinearLayout ?= null
    private var accTurnOFFLayout : LinearLayout ?= null


    private var sysIngitionTitleView : CommTitleView ?= null


    private var carIgnitionRy : RecyclerView ?= null
    private var list : MutableList<TimerBean> ?= null
    private var adapter : CarTimerAdapter?= null

    private var carIgnitionOnRy : RecyclerView ?= null
    private var onList : MutableList<TimerBean> ?= null
    private var onAdapter : CarTimerAdapter?= null

    companion object{
        fun getInstance() : CarIgnitionFragment{
            return CarIgnitionFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_car_ingition_layout
    }

    override fun initView() {
        accTurnOFFLayout = findViewById(R.id.accTurnOFFLayout)
        accTurnOnLayout = findViewById(R.id.accTurnOnLayout)
        ingitionOnSwitch = findViewById(R.id.ingitionOnSwitch)
        ingitionOffSwitch = findViewById(R.id.ingitionOffSwitch)
        sysIngitionTitleView = findViewById(R.id.sysIngitionTitleView)
        carIgnitionRy = findViewById(R.id.carIgnitionRy)
        carIgnitionOnRy = findViewById(R.id.carIgnitionOnRy)


        val linearLayoutManager = LinearLayoutManager(attachActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        carIgnitionRy?.layoutManager = linearLayoutManager
        list = ArrayList<TimerBean>()
        adapter = CarTimerAdapter(attachActivity, list as ArrayList<TimerBean>)
        carIgnitionRy?.adapter = adapter

        val linearLayoutManager2 = LinearLayoutManager(attachActivity)
        linearLayoutManager2.orientation = LinearLayoutManager.VERTICAL
        carIgnitionOnRy?.layoutManager = linearLayoutManager2
        onList = ArrayList<TimerBean>()
        onAdapter = CarTimerAdapter(attachActivity, onList as ArrayList<TimerBean>)
        carIgnitionOnRy?.adapter = onAdapter


        sysIngitionTitleView?.setCommTitleTxt("点火熄火设置")
        sysIngitionTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }


        //熄火
        ingitionOffSwitch?.setOnCheckedChangeListener { button, checked ->
            setRyVisibility()
            var code = if(checked) 1 else 0
            if(checked){
                list?.forEach {
                    if(it.isChecked){
                        code = it.time
                    }
                }
            }else{
                list?.forEach {
                   it.isChecked = false
                }
            }
            adapter?.notifyDataSetChanged()
            viewModel?.setStallSetting(code)
        }

        //点火
        ingitionOnSwitch?.setOnCheckedChangeListener { button, checked ->
            setRyVisibility()
            var code = if(checked) 1 else 0
            if(checked){
                onList?.forEach {
                    if(it.isChecked){
                        code = it.time
                    }
                }
            }else{
                onList?.forEach {
                   it.isChecked = false
                }
            }
            onAdapter?.notifyDataSetChanged()
            viewModel?.setStartUpSetting(code)
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        list?.clear()
        onList?.clear()

        val array = arrayListOf<TimerBean>(TimerBean("Low",0,false),
            TimerBean("1",1,false),
            TimerBean("2",2,false),
            TimerBean("3",3,false),
            TimerBean("4",4,false))
        list?.addAll(array)
        adapter?.notifyDataSetChanged()
        val tempArray = array.removeAt(0)
        onList?.addAll(array)
        onAdapter?.notifyDataSetChanged()

        //熄火
        adapter?.setOnCommClickListener{
            var code = 0
            list?.forEachIndexed { index, timerBean ->
                timerBean.isChecked = index == it
                if(it == index){
                    code = timerBean.time
                }
            }
            adapter?.notifyDataSetChanged()
            viewModel?.setStallSetting(code)
        }

        //点火
        onAdapter?.setOnCommClickListener{
            var code = 0
            onList?.forEachIndexed { index, timerBean ->
                timerBean.isChecked = index == it
                if(index == it){
                    code = timerBean.time
                }
            }
            onAdapter?.notifyDataSetChanged()
            viewModel?.setStartUpSetting(code)
        }

        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
           val isOn = it.accTurnOnValue !=0
            val isOff = it.accTurnOffValue != 0

            ingitionOffSwitch?.isChecked = isOff
            ingitionOnSwitch?.isChecked = isOn

            setRyVisibility()

            if(isOn){
                onList?.forEachIndexed { index, timerBean ->
                    timerBean.isChecked = timerBean.time == it.accTurnOnValue
                }
                onAdapter?.notifyDataSetChanged()
            }


            if(isOff){
                list?.forEachIndexed { index, timerBean ->
                    timerBean.isChecked = timerBean.time == it.accTurnOffValue
                }
                adapter?.notifyDataSetChanged()
            }

        }

        viewModel?.writeCommonFunction()

    }


    //显示和隐藏
    private fun setRyVisibility(){
        val onState = ingitionOnSwitch?.isChecked
        val offState = ingitionOffSwitch?.isChecked

        accTurnOnLayout?.visibility = if(onState==true) View.VISIBLE else View.GONE
        accTurnOFFLayout?.visibility = if(offState == true) View.VISIBLE else View.GONE
    }
}