package com.app.airmaster.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import timber.log.Timber

class ScaleView : View {


    private var tempProgress : Float =0F


    private var listener : OnProgressSelectListener ?= null

    fun setOnProgressListener(c : OnProgressSelectListener){
        this.listener = c
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    private var mHeight : Float ?= null
    private var mWidth : Float ?= null

    private var mLinePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mRulerPaint: Paint? = null
    private var progrees = 10f
    private val max = 100
    private val min = 0
    private var isCanMove = false

    private fun init() {
        mLinePaint = Paint()
        mLinePaint?.setColor(Color.WHITE)
        mLinePaint?.setAntiAlias(true) //抗锯齿
        mLinePaint?.setStyle(Paint.Style.STROKE)
        mLinePaint?.setStrokeWidth(8F)
        mTextPaint = Paint()
        mTextPaint?.setColor(Color.CYAN)
        mTextPaint?.setAntiAlias(true)
        mTextPaint?.setStyle(Paint.Style.FILL)
        mTextPaint?.setStrokeWidth(2F)
        mTextPaint?.setTextSize(48F)
        //
        mRulerPaint = Paint()
        mRulerPaint?.setAntiAlias(true)
        mRulerPaint?.setStyle(Paint.Style.FILL_AND_STROKE)
        mRulerPaint?.setColor(Color.RED)
        mRulerPaint?.strokeWidth = 8F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(setMeasureWidth(widthMeasureSpec), setMeasureHeight(heightMeasureSpec))
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()
    }

    private fun setMeasureHeight(spec: Int): Int {
        val mode = MeasureSpec.getMode(spec)
        var size = MeasureSpec.getSize(spec)
        val result = Int.MAX_VALUE
        when (mode) {
            MeasureSpec.AT_MOST -> size = Math.min(result, size)
            MeasureSpec.EXACTLY -> {}
            else -> size = result
        }
        return size
    }

    private fun setMeasureWidth(spec: Int): Int {
        val mode = MeasureSpec.getMode(spec)
        var size = MeasureSpec.getSize(spec)
        val result = Int.MAX_VALUE
        when (mode) {
            MeasureSpec.AT_MOST -> size = Math.min(result, size)
            MeasureSpec.EXACTLY -> {}
            else -> size = result
        }
        return size
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()


       // canvas?.drawLine(10F,10F,10F,mHeight!!,mLinePaint!!)
        canvas.drawLine(0F,mHeight!!,mWidth!!,mHeight!!,mLinePaint!!)

        val model = mWidth!! / 10
        var countX = 0F
        for(i in 0 ..10){
            val tmp = i * model
            countX += tmp
            canvas.drawLine(tmp,mHeight!!/2,tmp,mHeight!!,mLinePaint!!)
        }
//
//        for (i in min until max) {
//            if (i % 10 == 0) {
//                canvas.drawLine(10F, 0F, 10F, mHeight!!, mLinePaint!!)
//            }
////            else if (i % 5 == 0) {
////                canvas.drawLine(10F, 0F, 10F, mHeight!!, mLinePaint!!)
////            } else {
////                canvas.drawLine(10F, 0F, 10F, mHeight!!, mLinePaint!!)
////            }
//          //  canvas.translate(18F, 0F)
//        }
//        canvas.restore()


        val m1 = 100 / mWidth!!
        val v = (progrees*m1).toInt().toFloat()
        Timber.e("-------vvv="+v)

        progrees = tempProgress/m1
        canvas.drawLine(progrees, 0F, progrees, mHeight!!, mRulerPaint!!)

    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.getAction()) {
//            MotionEvent.ACTION_DOWN -> isCanMove = true
//            MotionEvent.ACTION_MOVE -> {
//                if (!isCanMove) {
//                    return false
//                }
//                val x: Float = event.getX() - 10
//                progrees = x
//                val m1 = 100 / mWidth!!
//                val v = (progrees*m1).toInt()
//
//                listener?.onProgress(v)
//                Timber.e("----progress=$progrees")
//                invalidate()
//            }
//        }
//        return true
//    }


    fun getCurrentProgress(): Int {

        val m1 = 100 / mWidth!!
        return (progrees * m1).toInt()
    }



    fun setProgress(pro: Int){
        Timber.e("-----pro="+pro+" "+measuredWidth)
        this.tempProgress = pro.toFloat()
        invalidate()
//        progrees = (pro/(100/measuredWidth)).toFloat()
//        invalidate()
    }

    interface OnProgressSelectListener{
        fun onProgress(progress : Int)
    }
}