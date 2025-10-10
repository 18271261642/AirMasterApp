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
import timber.log.Timber

/**
 * 最低行驶气压保护
 * Created by Admin
 *Date 2023/7/14
 */
class CarLowestAirFragment : TitleBarFragment<CarSysSetActivity>() {


    private var viewModel : ControlViewModel ?= null
    private var rearCountNumber = 30
    private var frontCountNumber = 30
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

        sysLowestAirTitleView?.setCommTitleTxt(resources.getString(R.string.string_set_minimum_dirve))
        sysLowestAirTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }
    }


    private var isFirst = false

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
            if(isFirst){
                return@observe
            }
            isFirst = true
            frontCountNumber = it.leftFrontProtectPressure
            rearCountNumber = it.leftRearProtectPressure
            carAirAddTv?.text = frontCountNumber.toString()
            carRearAirAddTv?.text = rearCountNumber.toString()
        }

        viewModel?.writeCommonFunction()
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



    private fun frontAddOrRemove(isAdd : Boolean){
        if(isAdd){
            frontCountNumber += 1
        }else{
            frontCountNumber -= 1
        }
        var v = frontCountNumber
        if(v>=99){
            v = 99
            carAirAddTv?.text = "99"
            setLowProtectPressure()
            return
        }
        if(v<=0){
            v = 0
            frontCountNumber = 0
            carAirAddTv?.text = "0"
            setLowProtectPressure()
            return
        }
        carAirAddTv?.text = v.toString()
        Timber.e("----------frontCountNumber="+v)

         setLowProtectPressure()
    }


    private fun rearAddOrRemove(isAdd : Boolean){

        if(isAdd){
            rearCountNumber += 1
        }else{
            rearCountNumber -= 1
        }
        var v = rearCountNumber
        if(v<=0){
            v = 0
            rearCountNumber = 0
            carRearAirAddTv?.text = "0"
            setLowProtectPressure()
            return
        }
        if(v>=99){
            v = 99
            carRearAirAddTv?.text = "99"
            setLowProtectPressure()
            return
        }
        carRearAirAddTv?.text = v.toString()
        setLowProtectPressure()
    }


    //设置保护气压
    private fun setLowProtectPressure(){
        Timber.e("-------fffrrr="+frontCountNumber+" rearCountNumber="+rearCountNumber)
        viewModel?.setRunLowPressure(frontCountNumber,rearCountNumber)
    }
}