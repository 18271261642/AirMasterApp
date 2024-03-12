package com.app.airmaster.car.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.app.airmaster.R
import com.app.airmaster.widget.VerticalSeekBar
import timber.log.Timber

class GaugeHeightView : LinearLayout{


    private var gaugeLeftSeekBar : VerticalSeekBar ?= null
    private var gaugeRightSeekBar : VerticalSeekBar ?= null



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

        gaugeLeftSeekBar?.max = 100
        gaugeRightSeekBar?.max = 100
    }


    fun setValues(left : Int,right : Int){
       // Timber.e("-----setValue高度尺="+left+" right="+right)
        gaugeLeftSeekBar?.progress = left
        gaugeRightSeekBar?.progress = right
    }
}