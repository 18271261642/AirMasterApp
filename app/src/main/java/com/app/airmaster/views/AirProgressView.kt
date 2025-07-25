package com.app.airmaster.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View

class AirProgressView : View {


    private var mWidth : Float ?= null
    private var mHeight : Float ?= null


    //背景画笔
    private var bgBarPaint : Paint ?= null

    //进度条的画笔
    private var progressBarPaint : Paint ?= null

    //图片刻度画笔
    private var imgPaint : Paint ?= null

    //数值的画笔
    private var txtPaint : Paint ?= null



    constructor(context: Context) : super (context){
        initPaint()
    }

    constructor(context: Context, attributes: AttributeSet) : super (context,attributes){
        initPaint()
    }


    private fun initPaint(){
        bgBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgBarPaint?.style = Paint.Style.FILL_AND_STROKE
        bgBarPaint?.isAntiAlias = true
        bgBarPaint?.strokeWidth = 2F
        bgBarPaint?.color = Color.parseColor("#FF2C2B29")

        progressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressBarPaint?.style = Paint.Style.FILL_AND_STROKE
        progressBarPaint?.isAntiAlias = true
        progressBarPaint?.strokeWidth = 2F
        progressBarPaint?.color = Color.WHITE

        imgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        imgPaint?.style = Paint.Style.FILL_AND_STROKE
        imgPaint?.isAntiAlias = true
        imgPaint?.strokeWidth = 1F
        imgPaint?.color = Color.WHITE

        txtPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        txtPaint?.style = Paint.Style.FILL_AND_STROKE
        txtPaint?.isAntiAlias = true
        txtPaint?.strokeWidth = 2F
        txtPaint?.color = Color.WHITE
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private var bgPath = Path()
    private fun canvasBg(canvas: Canvas?){

    }

}