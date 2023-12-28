package com.app.airmaster.car.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.app.airmaster.R

/**
 * 首页右侧温度
 */
class HomeRightTemperatureView : LinearLayout{


    private var temperatureView : CusCarTemperView ?= null


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
        val view = LayoutInflater.from(context).inflate(R.layout.view_home_temperature_layout,this,true)

        temperatureView = view.findViewById(R.id.temperatureView)
        setTempValue(0)
    }


    fun setTempValue(value : Int){
        temperatureView?.setProgressValue(value)
    }
}