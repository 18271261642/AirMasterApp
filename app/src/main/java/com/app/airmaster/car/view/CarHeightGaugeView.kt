package com.app.airmaster.car.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.app.airmaster.R
import com.app.airmaster.utils.DisplayUtils
import com.bonlala.widget.utils.MiscUtil
import timber.log.Timber

/**
 * 高度尺
 */
class CarHeightGaugeView : View{


    private var mWidth : Float ?= null
    private var mHeight : Float ?= null

    //中间竖线尺
    private var middlePaint : Paint ?= null
    //颜色
    private var middleBgColor : Int ?= null

    //图片
    private var imgPaint : Paint ?= null
    private var rightImgPaint : Paint ?= null


    private var leftDrawable : Bitmap ?= null
    private var rightDrawable : Bitmap ?= null


    private var leftValue : Float =0F
    private var rightValue : Float =0F

    private val maxValue = 100F

    constructor(context: Context) : super (context){
        initAttrs(context,null)
    }


    constructor(context: Context, attribute : AttributeSet) : super (context,attribute){
        initAttrs(context,attribute)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super (context, attrs, defStyleAttr){
        initAttrs(context,attrs)
    }



    private fun initAttrs(context: Context,attrs: AttributeSet?){
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CarHeightGaugeView)
        middleBgColor = typeArray.getColor(R.styleable.CarHeightGaugeView_car_height_gauge_mid_bg_color,0)
        typeArray.recycle()


        initPaint()
    }


    private fun initPaint(){
        middlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        middlePaint?.style = Paint.Style.FILL_AND_STROKE
        middlePaint?.strokeWidth = MiscUtil.dipToPxFloat(context,1.5F)
        middlePaint?.color = middleBgColor!!

        imgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        imgPaint?.style = Paint.Style.FILL_AND_STROKE
        imgPaint?.isAntiAlias = true

        rightImgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rightImgPaint?.style = Paint.Style.FILL_AND_STROKE
        rightImgPaint?.isAntiAlias = true

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec)

        val m = measuredWidth
        val h = measuredHeight
        if(m != 0){
            mWidth = measuredWidth.toFloat()
        }
        if(h != 0){
            mHeight = measuredHeight.toFloat()
        }

        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            mWidth?.toInt()?.let { mHeight?.toInt()?.let { it1 -> setMeasuredDimension(it, it1) } }

        }



       // Timber.e("-------onMeasure=$mWidth mHeight=$mHeight")
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Timber.e("-------leftDrawable="+(leftDrawable == null))
        if(leftDrawable == null || rightDrawable == null){
            return
        }

        canvasViews(canvas)
    }


    private fun canvasViews(canvas: Canvas?){
        //绘制中间竖线
        canvas?.drawLine(mWidth!!/2,0F,mWidth!!/2,mHeight!!,middlePaint!!)

        val leftWidth = leftDrawable!!.width
        val leftHeight = leftDrawable!!.height

        val rightWidth = rightDrawable!!.width
        val rightHeight = rightDrawable!!.height

        val model = mHeight!! / maxValue

        if(leftValue>maxValue){
            leftValue = maxValue
        }
        if(rightValue >maxValue){
            rightValue = maxValue
        }

        val lefProgressValue = model * leftValue!!
        val rightProgressValue = model * rightValue!!

        val leftRectF = RectF()
        leftRectF.left = (mWidth!!/2-leftWidth-5F)
        leftRectF.top = if((lefProgressValue) > leftHeight/2 ) (mHeight!!-leftHeight.toFloat()) else ( mHeight!! - lefProgressValue)
        leftRectF.right = mWidth!!/2-5F
        leftRectF.bottom = leftRectF.top+leftHeight

        canvas?.drawBitmap(leftDrawable!!,null,leftRectF,imgPaint)


        val rightRectF = RectF()
        rightRectF.left = mWidth!!/2+5F
        rightRectF.top = if(rightProgressValue >rightHeight/2) (mHeight!!-rightHeight.toFloat()) else mHeight!!-rightProgressValue
        rightRectF.right = rightRectF.left+rightWidth
        rightRectF.bottom = rightRectF.top+rightHeight
        canvas?.drawBitmap(rightDrawable!!,null,rightRectF,rightImgPaint)

    }


    fun setBitmap(leftBitmap : Bitmap ,rightBitmap : Bitmap){
        this.leftDrawable = leftBitmap
        this.rightDrawable = rightBitmap
    }


    fun setValues(leftV : Int,rightV : Int){
        this.leftValue = leftV.toFloat()
        this.rightValue = rightV.toFloat()
        Timber.e("--------left="+leftValue+" right="+rightValue+" max"+maxValue)
        invalidate()
    }
}