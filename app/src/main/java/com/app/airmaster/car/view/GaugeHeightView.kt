package com.app.airmaster.car.view

import android.content.Context
import android.opengl.Visibility
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.app.airmaster.R
import com.app.airmaster.widget.VerticalSeekBar
import timber.log.Timber

class GaugeHeightView : LinearLayout{


    private var gaugeLeftSeekBar : VerticalSeekBar ?= null
    private var gaugeRightSeekBar : VerticalSeekBar ?= null

    //目标
    private var gaugeLeftGoalSeekBar : VerticalSeekBar ?= null
    private var gaugeRightGoalSeekBar : VerticalSeekBar ?= null

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
        val view = LayoutInflater.from(context).inflate(R.layout.gauge_layout_view,this,true)
        gaugeLeftSeekBar = view.findViewById(R.id.gaugeLeftSeekBar)
        gaugeRightSeekBar = view.findViewById(R.id.gaugeRightSeekBar)

        gaugeLeftGoalSeekBar = view.findViewById(R.id.gaugeLeftGoalSeekBar)
        gaugeRightGoalSeekBar = view.findViewById(R.id.gaugeRightGoalSeekBar)

        gaugeLeftSeekBar?.max = 100
        gaugeRightSeekBar?.max = 100

        gaugeLeftGoalSeekBar?.max = 100
        gaugeRightGoalSeekBar?.max = 100

        setValues(0,0)

        setGoalValue(0,0)
    }


    fun setValues(left : Int,right : Int){
       // Timber.e("-----setValue高度尺="+left+" right="+right)
        gaugeLeftSeekBar?.progress = if(left>=100) 100 else left
        gaugeRightSeekBar?.progress = if(right>=100) 100 else right
    }


    //设置目标
    fun setGoalValue(left : Int,right: Int){
        gaugeLeftGoalSeekBar?.progress = if(left>=100) 100 else left
        gaugeRightGoalSeekBar?.progress = if(right>=100) 100 else right
    }


    //设置目标是否显示或隐藏
    fun setGoalVisibility(visibility: Boolean){
        gaugeLeftGoalSeekBar?.visibility = if(visibility) View.VISIBLE else View.GONE
        gaugeRightGoalSeekBar?.visibility = if(visibility) View.VISIBLE else View.GONE

    }



}