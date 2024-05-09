package com.app.airmaster.car.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Vibrator
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import com.app.airmaster.R
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.app.airmaster.widget.HomeTxtStyleView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class CarHomeCenterView : LinearLayout ,OnClickListener{

    private var onPressureListener : OnControlPressureCheckedListener ?= null

    private var map = HashMap<Int,Int>()
    fun setOnPressureListener(c : OnControlPressureCheckedListener){
        this.onPressureListener = c
    }

    //前轮+
    private var carHomeCenterTopTopImg : ImageView ?= null
    private var carCenterTopBomImg : ImageView ?= null //-
    //后轮+
    private var carCenterBotTopImg : ImageView ?= null
    //后轮-
    private var carCenterBotBomImg : ImageView ?= null


    private var carFrontGaugeView : GaugeHeightView ?= null
    private var carRearGaugeView : GaugeHeightView ?= null

    //左前轮气压值
    private var homeCenterLeftTopTv : HomeTxtStyleView?= null
    //右前轮
    private var homeCenterRightTopPressureTv : HomeTxtStyleView ?= null
    //左后轮气压值
    private var homeCenterLeftRearPressureTv : HomeTxtStyleView ?= null
    //右后方
    private var homeCenterRightRearPressureTv : HomeTxtStyleView ?= null

    //左前轮+
    private var homeCenterLeftTopImg : ImageView ?= null
    //左前轮-
    private var homeCenterLeftBotImg : ImageView ?= null

    //右前轮+
    private var rightTopAddImageView : ImageView ?= null
    //右前轮-
    private var rightTopReduceImageView : ImageView ?= null
    //左后轮+
    private var leftRearAddImageView : ImageView ?= null
    //左后轮-
    private var homeCenterLeftBotImg2 : ImageView ?= null
    //右后轮+
    private var rightRearAddImageView :ImageView ?= null
    //右后轮-
    private var rightRearReduceImageView : ImageView ?= null


    private var carFrontHeightGaugeView : CarHeightGaugeView ?= null
    private var carAfterHeightGaugeView : CarHeightGaugeView ?= null


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                onPressureListener?.onItemChecked(map)
                vibrate()
            }
           if(msg.what == 0x01){
               onPressureListener?.onItemChecked(map)
           }

        }
    }

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
        val view = LayoutInflater.from(context).inflate(R.layout.view_car_home_cente_layout,this,true)
        homeCenterLeftTopTv = view.findViewById(R.id.homeCenterLeftTopTv)
        homeCenterRightTopPressureTv = view.findViewById(R.id.homeCenterRightTopPressureTv)
        homeCenterLeftRearPressureTv = view.findViewById(R.id.homeCenterLeftRearPressureTv)
        homeCenterRightRearPressureTv = view.findViewById(R.id.homeCenterRightRearPressureTv)

        carFrontGaugeView = view.findViewById(R.id.carFrontGaugeView)
        carRearGaugeView= view.findViewById(R.id.carRearGaugeView)

        carHomeCenterTopTopImg = view.findViewById(R.id.carHomeCenterTopTopImg)
        carCenterTopBomImg = view.findViewById(R.id.carCenterTopBomImg)
        carCenterBotTopImg = view.findViewById(R.id.carCenterBotTopImg)
        carCenterBotBomImg = view.findViewById(R.id.carCenterBotBomImg)

        homeCenterLeftTopImg = view.findViewById(R.id.homeCenterLeftTopImg)
        homeCenterLeftBotImg = view.findViewById(R.id.homeCenterLeftBotImg)
        rightTopAddImageView = view.findViewById(R.id.rightTopAddImageView)
        rightTopReduceImageView = view.findViewById(R.id.rightTopReduceImageView)
        leftRearAddImageView  = view.findViewById(R.id.leftRearAddImageView)
        homeCenterLeftBotImg2 = view.findViewById(R.id.homeCenterLeftBotImg2)
        rightRearAddImageView = view.findViewById(R.id.rightRearAddImageView)
        rightRearReduceImageView = view.findViewById(R.id.rightRearReduceImageView)


        carFrontHeightGaugeView = view.findViewById(R.id.carFrontHeightGaugeView)
        carAfterHeightGaugeView = view.findViewById(R.id.carAfterHeightGaugeView)

        setHomeCenterDefault()
      //  setFrontImage()


        homeCenterLeftTopImg?.setOnClickListener(this)
        homeCenterLeftBotImg?.setOnClickListener(this)
        rightTopAddImageView?.setOnClickListener(this)
        rightTopReduceImageView?.setOnClickListener(this)

        homeCenterLeftTopImg?.setOnTouchListener(onTouchListener)
        homeCenterLeftBotImg?.setOnTouchListener(onTouchListener)
        rightTopAddImageView?.setOnTouchListener(onTouchListener)
        rightTopReduceImageView?.setOnTouchListener(onTouchListener)

        leftRearAddImageView?.setOnTouchListener(onTouchListener)
        homeCenterLeftBotImg2?.setOnTouchListener(onTouchListener)
        rightRearReduceImageView?.setOnTouchListener(onTouchListener)
        rightRearAddImageView?.setOnTouchListener(onTouchListener)

        carHomeCenterTopTopImg?.setOnTouchListener(onTouchListener)
        carCenterTopBomImg?.setOnTouchListener(onTouchListener)
        carCenterBotTopImg?.setOnTouchListener(onTouchListener)
        carCenterBotBomImg?.setOnTouchListener(onTouchListener)


        leftRearAddImageView?.setOnClickListener(this)
        homeCenterLeftBotImg2?.setOnClickListener(this)
        rightRearReduceImageView?.setOnClickListener(this)
        rightRearAddImageView?.setOnClickListener(this)

        carHomeCenterTopTopImg?.setOnClickListener(this)
        carCenterTopBomImg?.setOnClickListener(this)
        carCenterBotTopImg?.setOnClickListener(this)
        carCenterBotBomImg?.setOnClickListener(this)

    }



    private val onTouchListener : OnTouchListener = object : OnTouchListener{
        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            Timber.e("---------onTouch="+p1?.action)
           val id = p0?.id
            if(p1?.action == MotionEvent.ACTION_DOWN){
                if (id != null) {
                    isEnd = false
                    startCountDown(id)
                    return true
                }
            }

            if(p1?.action == MotionEvent.ACTION_UP){
                isEnd = true
                var keyV = -1
                map.forEach {
                    keyV = it.key
                }
                Timber.e("---------onTouchkey="+keyV)
                if(keyV != -1){
                    map[keyV] = 0
                    handlers.sendEmptyMessageDelayed(0x01,20)
                }

                return true
            }
            return false
        }

    }


    var isEnd = false
    var count = 0
    private  fun startCountDown(id : Int){
        GlobalScope.launch {
            while (!isEnd){
                map.clear()
                when (id){
                    R.id.homeCenterLeftTopImg->{    //左前+
                        map[1] = 1
                    }
                    R.id.homeCenterLeftBotImg->{    //左前-
                        map[1] = 2
                    }

                    R.id.rightTopAddImageView->{    //右前+
                        map[2] = 1
                    }
                    R.id.rightTopReduceImageView->{ //右前-
                        map[2] = 2
                    }
                    R.id.leftRearAddImageView->{    //左后+
                        map[4] = 1
                    }
                    R.id.homeCenterLeftBotImg2->{   //左后-
                        map[4] = 2
                    }
                    R.id.rightRearAddImageView->{   //右后+
                        map[8] = 1
                    }
                    R.id.rightRearReduceImageView->{    //右后-
                        map[8] = 2
                    }

                    R.id.carHomeCenterTopTopImg->{  //前轮+
                        map[3] = 1
                    }
                    R.id.carCenterTopBomImg->{  //前轮-
                        map[3] = 2
                    }
                    R.id.carCenterBotTopImg->{  //后轮+
                        map[12] = 1
                    }
                    R.id.carCenterBotBomImg->{  //后轮-
                        map[12] = 2
                    }
                }
              handlers.sendEmptyMessage(0x00)
                delay(200)
            }
        }
    }



    //调用手机震动
    @SuppressLint("ServiceCast")
    private fun vibrate(){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }



    //设置左前轮的气压值
    fun setLeftTopPressureValue(leftValue : Int){
        homeCenterLeftTopTv?.setTxtValue(String.format("%02d",leftValue))
    }

    //设置右前方气压值
    fun setRightTopPressureValue(rightValue : Int){
        homeCenterRightTopPressureTv?.setTxtValue(String.format("%02d",rightValue))
    }

    //设置左后方气压值
    fun setLeftRearPressureValue(leftRearValue : Int){
        homeCenterLeftRearPressureTv?.setTxtValue(String.format("%02d",leftRearValue))
    }

    //设置右后方气压值
    fun setRightRearPressureValue(rightRearValue : Int){
        homeCenterRightRearPressureTv?.setTxtValue(String.format("%02d",rightRearValue))
    }

    //设置默认值
    fun setHomeCenterDefault(){
        setLeftTopPressureValue(0)
        setRightTopPressureValue(0)
        setLeftRearPressureValue(0)
        setRightRearPressureValue(0)
    }


    //设置前轮的图片
     fun setFrontImage(){
        val leftBit = BitmapFactory.decodeResource(context.resources,R.mipmap.ic_car_mid_left_img)
        val rightBit = BitmapFactory.decodeResource(context.resources,R.mipmap.ic_car_mid_right_img)



        val afterLeft = BitmapFactory.decodeResource(context.resources,R.mipmap.ic_car_mid_after_left_img)
        val afterRight = BitmapFactory.decodeResource(context.resources,R.mipmap.ic_car_mid_after_right_img)
//        carFrontHeightGaugeView?.setBitmap(afterLeft,afterRight)
//        carAfterHeightGaugeView?.setBitmap(afterLeft,afterRight)

//        setFrontHeightValue(10,10)
//        setAfterHeightValue(10,10)
    }

    //设置前轮高度
    fun setFrontHeightValue(leftValue: Int,rightValue: Int){
       // carFrontHeightGaugeView?.setValues(leftValue,rightValue)

        carFrontGaugeView?.setValues(leftValue,rightValue)
    }

    //设置后轮高度
    fun setAfterHeightValue(afterLeft : Int,afterRight : Int){
       // carAfterHeightGaugeView?.setValues(afterLeft,afterRight)
        carRearGaugeView?.setValues(afterLeft,afterRight)
    }


    //是否显示目标高度
    fun setGoalHeightVisibility(show : Boolean){

    }


    //设置前轮目标高度
    fun setFrontGoalValue(leftValue: Int,rightValue: Int){
        carFrontGaugeView?.setGoalValue(leftValue,rightValue)
    }

    //设置后轮目标高度
    fun setRearGoalValue(afterLeft : Int,afterRight : Int){
        carRearGaugeView?.setGoalValue(afterLeft,afterRight)
    }

    //设置目标显示或隐藏
    fun setGoalVisibility(visibility: Boolean){
        carFrontGaugeView?.setGoalVisibility(visibility)
        carRearGaugeView?.setGoalVisibility(visibility)
    }


    override fun onClick(p0: View?) {
//       val id = p0?.id
//        map.clear()
//        when (id){
//            R.id.homeCenterLeftTopImg->{    //左前+
//                map[0] = 1
//            }
//            R.id.homeCenterLeftBotImg->{    //左前-
//                map[0] = 2
//            }
//
//            R.id.rightTopAddImageView->{    //右前+
//                map[1] = 1
//            }
//            R.id.rightTopReduceImageView->{ //右前-
//                map[1] = 2
//            }
//            R.id.leftRearAddImageView->{    //左后+
//                map[2] = 1
//            }
//            R.id.homeCenterLeftBotImg2->{   //左后-
//                map[2] = 2
//            }
//            R.id.rightRearAddImageView->{   //右后+
//                map[3] = 1
//            }
//            R.id.rightRearReduceImageView->{    //右后-
//                map[3] = 2
//            }
//
//            R.id.carHomeCenterTopTopImg->{  //前轮+
//                map[4] = 1
//            }
//            R.id.carCenterTopBomImg->{  //前轮-
//                map[4] = 2
//            }
//            R.id.carCenterBotTopImg->{  //后轮+
//                map[5] = 1
//            }
//            R.id.carCenterBotBomImg->{  //后轮-
//                map[5] = 2
//            }
//        }
    //    onPressureListener?.onItemChecked(map)
    }


}