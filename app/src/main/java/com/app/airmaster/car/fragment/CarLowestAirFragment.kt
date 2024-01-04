package com.app.airmaster.car.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView

/**
 * 最低行驶气压保护
 * Created by Admin
 *Date 2023/7/14
 */
class CarLowestAirFragment : TitleBarFragment<CarSysSetActivity>() {


    private var viewModel : ControlViewModel ?= null


    companion object{
        fun getInstance() : CarLowestAirFragment{
            return CarLowestAirFragment()
        }
    }

    private var sysLowestAirTitleView : CommTitleView ?= null

    private var carAddImgView : ImageView ?= null
    private var carAirAddTv : TextView ?= null
    private var carRemoveImgView : ImageView ?= null

    private var carRearAddImgView : ImageView ?= null
    private var carRearAirAddTv : TextView ?= null
    private var carRearRemoveImgView : ImageView ?= null

    override fun getLayoutId(): Int {
        return R.layout.fragment_car_lowest_air
    }

    override fun initView() {
        sysLowestAirTitleView = findViewById(R.id.sysLowestAirTitleView)
        carAddImgView = findViewById(R.id.carAddImgView)
        carAirAddTv = findViewById(R.id.carAirAddTv)
        carRemoveImgView = findViewById(R.id.carRemoveImgView)
        carRearAddImgView = findViewById(R.id.carRearAddImgView)
        carRearAirAddTv = findViewById(R.id.carRearAirAddTv)
        carRearRemoveImgView = findViewById(R.id.carRearRemoveImgView)

        carAddImgView?.setOnClickListener(this)
        carRemoveImgView?.setOnClickListener(this)
        carRearAddImgView?.setOnClickListener(this)
        carRearRemoveImgView?.setOnClickListener(this)

        sysLowestAirTitleView?.setCommTitleTxt("最低行驶气压保护")
        sysLowestAirTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        carAirAddTv?.text = frontCountNumber.toString()
        carRearAirAddTv?.text = rearCountNumber.toString()
    }

    override fun onClick(view: View?) {
        super.onClick(view)

        val id = view?.id

        when (id){
            R.id.carAddImgView->{
                frontAddOrRemove(true)
            }
            R.id.carRemoveImgView->{
                frontAddOrRemove(false)
            }
            R.id.carRearAddImgView->{
                rearAddOrRemove(true)
            }

            R.id.carRearRemoveImgView->{
                rearAddOrRemove(false)
            }
        }
    }

    private var frontCountNumber = 30

    private fun frontAddOrRemove(isAdd : Boolean){
        carAirAddTv?.text = if(isAdd) frontCountNumber--.toString() else frontCountNumber++.toString()
        setLowProtectPressure()
    }

    var rearCountNumber = 30
    private fun rearAddOrRemove(isAdd : Boolean){
        carRearAirAddTv?.text = if(isAdd) rearCountNumber--.toString() else rearCountNumber++.toString()
        setLowProtectPressure()
    }


    //设置保护气压
    private fun setLowProtectPressure(){
        viewModel?.setRunLowPressure(frontCountNumber,rearCountNumber)
    }
}