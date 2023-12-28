package com.app.airmaster.car.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.app.airmaster.R
import com.app.airmaster.widget.CusVerticalScheduleView
import com.app.airmaster.widget.CusVerticalTextScheduleView
import com.app.airmaster.widget.VerticalSeekBar

/**
 * 左侧的气压view
 */
class HomeLeftAirPressureView : LinearLayout{

    //seekbar
    private var homeLeftAirSeekBar : VerticalSeekBar ?= null
    //数值
    private var cusHomeLeftVerticalTxtView : CusVerticalTextScheduleView ?= null
    private var cusVerticalView : CusVerticalScheduleView ?= null



    constructor(context: Context) : super (context){
        initViews(context)
    }


    constructor(context: Context, attribute : AttributeSet) : super (context,attribute){
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super (context, attrs, defStyleAttr){
        initViews(context)
    }


    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.view_home_air_layout,this,true)
        homeLeftAirSeekBar = view.findViewById(R.id.homeLeftAirSeekBar)
        cusHomeLeftVerticalTxtView = view.findViewById(R.id.cusHomeLeftVerticalTxtView)
        cusVerticalView = view.findViewById(R.id.cusVerticalView)


        homeLeftAirSeekBar?.isEnabled = false
        cusVerticalView?.allScheduleValue = 150F
        homeLeftAirSeekBar?.isClickable = false
        homeLeftAirSeekBar?.max = 150
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            homeLeftAirSeekBar?.min = 0
        }
        cusHomeLeftVerticalTxtView?.allScheduleValue = 150F

        setDefaultValue()
    }

    //设置气压数值
    fun setAirPressureValue(value : Int){
        homeLeftAirSeekBar?.visibility = View.VISIBLE
        cusVerticalView?.currScheduleValue = value.toFloat()
        homeLeftAirSeekBar?.progress = value
        cusHomeLeftVerticalTxtView?.currScheduleValue = value
        cusHomeLeftVerticalTxtView?.showTxt = value.toString()
    }


    //设置默认值
    fun setDefaultValue(){
        homeLeftAirSeekBar?.visibility = View.GONE
        cusVerticalView?.currScheduleValue = 0F
        cusHomeLeftVerticalTxtView?.currScheduleValue = 0
        cusHomeLeftVerticalTxtView?.showTxt = "00"
    }
}