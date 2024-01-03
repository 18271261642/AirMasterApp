package com.app.airmaster.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.math.BigDecimal

class ScaleView : View {


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }


    private var mLinePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mRulerPaint: Paint? = null
    private var progrees = 10f
    private val max = 101
    private val min = 0
    private var isCanMove = false

    private fun init() {
        mLinePaint = Paint()
        mLinePaint?.setColor(Color.CYAN)
        mLinePaint?.setAntiAlias(true) //抗锯齿
        mLinePaint?.setStyle(Paint.Style.STROKE)
        mLinePaint?.setStrokeWidth(4F)
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
        mRulerPaint?.setStrokeWidth(4F)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(setMeasureWidth(widthMeasureSpec), setMeasureHeight(heightMeasureSpec))
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
        for (i in min until max) {
            if (i % 10 == 0) {
                canvas.drawLine(10F, 0F, 10F, 72F, mLinePaint!!)
                val text = (i / 10).toString() + ""
                val rect = Rect()
                val txtWidth: Float = mTextPaint!!.measureText(text)
                mTextPaint?.getTextBounds(text, 0, text.length, rect)
                canvas.drawText(text, 10 - txtWidth / 2, 72F + rect.height() + 10, mTextPaint!!)
            } else if (i % 5 == 0) {
                canvas.drawLine(10F, 0F, 10F, 64F, mLinePaint!!)
            } else {
                canvas.drawLine(10F, 0F, 10F, 48F, mLinePaint!!)
            }
            canvas.translate(18F, 0F)
        }
        canvas.restore()
        canvas.drawLine(progrees, 0F, progrees, 160F, mRulerPaint!!)
        canvas.drawCircle(progrees, 170F, 10F, mRulerPaint!!)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> isCanMove = true
            MotionEvent.ACTION_MOVE -> {
                if (!isCanMove) {
                    return false
                }
                val x: Float = event.getX() - 10
                progrees = x
                invalidate()
            }
        }
        return true
    }

}