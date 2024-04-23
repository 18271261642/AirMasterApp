package com.app.airmaster.car.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.app.airmaster.R
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.widget.CusVerticalScheduleView
import com.app.airmaster.widget.CusVerticalTextScheduleView
import com.app.airmaster.widget.VerticalSeekBar
import timber.log.Timber

/**
 * 左侧的气压view
 */
class HomeLeftAirPressureView : LinearLayout{

    //seekbar
    private var homeLeftAirSeekBar : VerticalSeekBar ?= null
    //数值
    private var cusHomeLeftVerticalTxtView : CusVerticalTextScheduleView ?= null
    private var cusVerticalView : CusVerticalScheduleView ?= null

    //最大值
    private var homeAirTopTv : TextView ?= null


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
        homeAirTopTv = view.findViewById(R.id.homeAirTopTv)

        val max = MmkvUtils.getMaxPressureValue()
        homeAirTopTv?.text = max.toString()
        homeLeftAirSeekBar?.isEnabled = false
        cusVerticalView?.allScheduleValue = max.toFloat()
        homeLeftAirSeekBar?.isClickable = false
        homeLeftAirSeekBar?.max = max
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            homeLeftAirSeekBar?.min = 0
        }
        cusHomeLeftVerticalTxtView?.allScheduleValue = max.toFloat()

        setDefaultValue()


    }

    //设置气压数值
    fun setAirPressureValue(value : Int){
        homeLeftAirSeekBar?.visibility = View.VISIBLE
        cusVerticalView?.currScheduleValue = value.toFloat()
        val max = MmkvUtils.getMaxPressureValue()
        homeAirTopTv?.text = max.toString()
        cusHomeLeftVerticalTxtView?.allScheduleValue = max.toFloat()
        if(value<=80){
            homeLeftAirSeekBar?.thumb = resources.getDrawable(R.mipmap.ic_home_left_air_blue)
        }else{
            homeLeftAirSeekBar?.thumb = resources.getDrawable(R.mipmap.ic_home_left_air)
        }
        homeLeftAirSeekBar?.progress = value
        cusHomeLeftVerticalTxtView?. currScheduleValue = if(value>max) max else  value
        cusHomeLeftVerticalTxtView?.showTxt = if(value>max) max.toString() else value.toString()
    }


    //设置默认值
    fun setDefaultValue(){
        homeLeftAirSeekBar?.visibility = View.GONE
        cusVerticalView?.currScheduleValue = 0F
        cusHomeLeftVerticalTxtView?.currScheduleValue = 0
        cusHomeLeftVerticalTxtView?.showTxt = "00"
    }
}