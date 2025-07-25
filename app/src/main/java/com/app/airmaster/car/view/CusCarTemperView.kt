package com.app.airmaster.car.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.bonlala.widget.utils.MiscUtil
import timber.log.Timber


/**
 * 自定义首页右侧温度进度条
 */
class CusCarTemperView : View {

    //背景
    private var bgPaint : Paint ?= null
    //进度
    private var progressPaint : Paint ?= null
    //数值
    private var txtPaint : Paint ?= null

    private var tempTxtPaint : Paint ?= null


    private var mWidth : Float ?= null
    private var mHeight : Float ?= null


    //最大值
    private val maxValue = 120
    private var progressValue : Int = 0


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
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint?.style = Paint.Style.FILL_AND_STROKE
        bgPaint?.strokeWidth = 5F
        bgPaint?.color = Color.parseColor("#2C2B29")
        bgPaint?.isAntiAlias = true

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint?.style = Paint.Style.FILL_AND_STROKE
        progressPaint?.isAntiAlias = true
        progressPaint?.strokeWidth = 5F
        progressPaint?.color = Color.parseColor("#2C2B29")

        txtPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        txtPaint?.style = Paint.Style.FILL_AND_STROKE
        txtPaint?.isAntiAlias = true
        txtPaint?.strokeWidth = 1F
        txtPaint?.color = Color.WHITE
        txtPaint?.textSize = MiscUtil.dipToPxFloat(context,10F)


        tempTxtPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        tempTxtPaint?.style = Paint.Style.FILL_AND_STROKE
        tempTxtPaint?.isAntiAlias = true
        tempTxtPaint?.strokeWidth = 1F
        tempTxtPaint?.color = Color.TRANSPARENT
        tempTxtPaint?.textSize = MiscUtil.dipToPxFloat(context,10F)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasView(canvas)
    }


    private fun canvasView(canvas: Canvas?){
        val model = mHeight!! / maxValue

        val progressHeight = model * progressValue
        val progressTxt = String.format("%02d",progressValue)
        val tW = MiscUtil.getTextWidth(tempTxtPaint!!,"100")
        val tH = MiscUtil.measureTextHeight(txtPaint)


        canvas?.drawText("100",12F,mHeight!!/2,tempTxtPaint!!)

        //Timber.e("------tw="+tW+"  "+MiscUtil.getTextWidth(txtPaint!!,"100"))
        val mH = if(progressHeight==0F) mHeight!!-2F else  mHeight!!-progressHeight+tH+5F

        canvas?.drawText(progressTxt,if(progressValue<=99) 12F else 0F, mH,txtPaint!!)

        val bgWidth = tW+15F+ MiscUtil.dipToPx(context,4F)

        val bgRectF = RectF()
        bgRectF.left = tW+15F
        bgRectF.top = 0F
        bgRectF.right = bgWidth.toFloat()
        bgRectF.bottom = mHeight!!
        val bgArray = IntArray(2)
        bgArray[0] = Color.parseColor("#2C2B29")
        bgArray[1] = Color.parseColor("#4A4C4D")
        val bgShader = LinearGradient(0F,0F,bgWidth,mHeight!!,bgArray,null,Shader.TileMode.CLAMP)
        bgPaint?.shader = bgShader
        canvas?.drawRect(bgRectF,bgPaint!!)


        val progressRectF = RectF()
        progressRectF.left = tW+15F
        progressRectF.top = mHeight!!-progressHeight
        progressRectF.right = bgWidth
        progressRectF.bottom = mHeight!!
        val pArray = IntArray(2)
        if(progressValue<=80){
            pArray[0] = Color.parseColor("#2E7BFD")
            pArray[1] = Color.parseColor("#BED6FF")
        }else{
            pArray[0] = Color.parseColor("#FF0000")
            pArray[1] = Color.parseColor("#FF5100")
        }

        val pShader = LinearGradient(progressRectF.left,progressRectF.top,bgWidth,mHeight!!,pArray,null,Shader.TileMode.CLAMP)
        progressPaint?.shader = pShader
        canvas?.drawRect(progressRectF,progressPaint!!)
    }


    fun setProgressValue(value : Int){
        this.progressValue = if(value>=120) 120 else value
        invalidate()
    }

}