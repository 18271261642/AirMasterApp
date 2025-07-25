package com.app.airmaster.car.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by Admin
 *Date 2023/7/18
 */
class HalfCircleView : View {

    //绘制半圆的画笔
    private var halfCirclePaint : Paint ?= null


    //颜色
    private var color : Int = Color.BLUE


    private var mWidth : Int ?= null
    private var mHeight : Int ?= null


    //半径
    private var mRadius : Int ?= null

    constructor(context: Context) : super (context){
        initPaint()
    }

    constructor(context: Context,attributes: AttributeSet) : super (context,attributes){
        initPaint()
    }



    private fun initPaint(){
        halfCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        halfCirclePaint?.style = Paint.Style.FILL
        halfCirclePaint?.isAntiAlias = true
        halfCirclePaint?.color = Color.parseColor("#47281B")
        halfCirclePaint?.strokeWidth = 1F

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        halfCirclePaint?.color = color
        //绘制半圆
        //圆形点
        val cX = mWidth!! /2
        val cY = mRadius
        val rect = RectF(0F,0F, mWidth!!.toFloat(), mHeight!!.toFloat())
      //  canvas?.drawCircle(cX.toFloat(),cY!!.toFloat(),mRadius!!.toFloat(),halfCirclePaint!!)
        canvas?.drawArc(rect,180F,180F,true,halfCirclePaint!!)
    }


    fun setColor(c: Int){
        this.color = c
        invalidate()
    }


    fun setRadius(radius : Int){
        this.mRadius = radius
        invalidate()
    }
}