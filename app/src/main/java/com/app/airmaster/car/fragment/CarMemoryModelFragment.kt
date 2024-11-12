package com.app.airmaster.car.fragment

import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.bonlala.widget.view.SwitchButton
import timber.log.Timber

/**
 * 记忆模式
 */
class CarMemoryModelFragment : TitleBarFragment<CarSysSetActivity>() {


    companion object{
        fun getInstance() : CarMemoryModelFragment{
            return CarMemoryModelFragment()
        }
    }

    private var viewModel : ControlViewModel?= null

    //高度记忆
    private var memoryHeightSwitchButton : SwitchButton ?= null
    //压力记忆
    private var memoryPressureSwitchButton : SwitchButton ?= null
    private var sysMemoryTitleView : CommTitleView ?= null

    override fun getLayoutId(): Int {
       return R.layout.fragment_car_memory_model_layout
    }

    override fun initView() {
        sysMemoryTitleView = findViewById(R.id.sysMemoryTitleView)
        memoryHeightSwitchButton = findViewById(R.id.memoryHeightSwitchButton)
        memoryPressureSwitchButton = findViewById(R.id.memoryPressureSwitchButton)

        //高度
        memoryHeightSwitchButton?.setOnCheckedChangeListener { button, checked ->
            Timber.e("----------pppppppp="+button.isPressed)
            if(!button.isPressed && checked){
                viewModel?.setHeightMemory(true)
                MmkvUtils.savePressureModel(false)
            }

        }

        //压力
        memoryPressureSwitchButton?.setOnCheckedChangeListener { button, checked ->
            if(!button.isPressed && checked){
                MmkvUtils.savePressureModel(true)
                viewModel?.setHeightMemory(false)
            }

        }

        sysMemoryTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }
        sysMemoryTitleView?.setCommTitleTxt(resources.getString(R.string.string_car_memory_model))
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }

            MmkvUtils.savePressureModel(it.modelType==1)
            //高度记忆或压力记忆，互斥
            val model = it.modelType
            memoryHeightSwitchButton?.isChecked = model == 0
            memoryPressureSwitchButton?.isChecked = model==1
        }

        viewModel?.writeCommonFunction()
    }

}