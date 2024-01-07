package com.app.airmaster.car.fragment

import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.app.airmaster.widget.ScaleView



/**
 * 高度尺
 */
class CarSysScaleFragment : TitleBarFragment<CarSysSetActivity>() {

    private var viewModel : ControlViewModel ?= null


    private var sysScaleTitleView : CommTitleView ?= null
    private var leftFrontScaleView : ScaleView ?= null
    private var rightFrontScaleView : ScaleView ?= null
    private var leftRearScaleView : ScaleView ?= null
    private var rightRearScaleView : ScaleView ?= null



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
        leftFrontScaleView = findViewById(R.id.leftFrontScaleView)
        rightFrontScaleView = findViewById(R.id.rightFrontScaleView)
        leftRearScaleView = findViewById(R.id.leftRearScaleView)
        rightRearScaleView = findViewById(R.id.rightRearScaleView)

    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        sysScaleTitleView?.setCommTitleTxt("高度尺工具")
        sysScaleTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }


        initScale()
    }


    private fun initScale(){
        //左前
        leftFrontScaleView?.setOnProgressListener(progressSelectListener)

        //右前
        rightFrontScaleView?.setOnProgressListener(progressSelectListener)

        //左后
        leftRearScaleView?.setOnProgressListener(progressSelectListener)

        //右后
        rightRearScaleView?.setOnProgressListener(progressSelectListener)

    }



    private val progressSelectListener : ScaleView.OnProgressSelectListener = object : ScaleView.OnProgressSelectListener{
        override fun onProgress(progress: Int) {
            getHeightRuler()
        }
    }

    //处理高度尺
    private fun getHeightRuler(){
        val leftFront  = leftFrontScaleView?.getCurrentProgress()
        val rightFront = rightFrontScaleView?.getCurrentProgress()
        val leftRear = leftRearScaleView?.getCurrentProgress()
        val rightRear = rightRearScaleView?.getCurrentProgress()

        if (leftFront != null) {
            viewModel?.setHeightRuler(leftFront,leftRear!!,rightFront!!,rightRear!!)
        }
    }
}