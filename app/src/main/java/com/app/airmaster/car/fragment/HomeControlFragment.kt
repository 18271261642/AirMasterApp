package com.app.airmaster.car.fragment


import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.LogActivity
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnItemCheckedListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.CarFaultNotifyActivity
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.car.view.CarHomeCenterView
import com.app.airmaster.car.view.HomeBottomCheckView
import com.app.airmaster.car.view.HomeBottomNumberView
import com.app.airmaster.car.view.HomeLeftAirPressureView
import com.app.airmaster.car.view.HomeRightTemperatureView
import com.app.airmaster.dialog.ManualSetHeightView
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.CarErrorNotifyViewModel
import com.app.airmaster.viewmodel.ControlViewModel
import com.blala.blalable.Utils
import com.blala.blalable.car.AutoBackBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Created by Admin
 *Date 2023/7/14
 */
class HomeControlFragment : TitleBarFragment<CarHomeActivity>() {


    private var controlViewModel : ControlViewModel ?= null
    private var errorNotifyViewModel : CarErrorNotifyViewModel ?= null


    private var homeBottomCheckView : HomeBottomCheckView ?= null
    private var homeBottomNumberView : HomeBottomNumberView ?= null

    //中间的view
    private var carHomeCenterView :  CarHomeCenterView ?= null
    //左侧的view
    private var homeLeftAirPressureView : HomeLeftAirPressureView ?= null
    private var homeRightView : HomeRightTemperatureView ?= null
    //设备异常，展示第一个
    private var homeErrorMsgTv : TextView ?= null
    private var homeDeviceErrorLayout : LinearLayout ?= null

    private var tempErrorCount = 0

    private val handlers : Handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                showRulerGoalVisibility(false)
            }

            if(msg.what == 0x08){   //显示异常
                homeBottomCheckView?.setErrorChecked(true)
                homeDeviceErrorLayout?.visibility = View.INVISIBLE
            }
        }
    }


    companion object{
        fun getInstance() : HomeControlFragment{
            return HomeControlFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_home_control_layut
    }

    override fun initView() {
        homeDeviceErrorLayout = findViewById(R.id.homeDeviceErrorLayout)
        homeErrorMsgTv = findViewById(R.id.homeErrorMsgTv)
        homeRightView = findViewById(R.id.homeRightView)
        homeLeftAirPressureView = findViewById(R.id.homeLeftAirPressureView)
        carHomeCenterView = findViewById(R.id.carHomeCenterView)
        homeBottomCheckView = findViewById(R.id.homeBottomCheckView)
        homeBottomNumberView = findViewById(R.id.homeBottomNumberView)

        findViewById<ImageView>(R.id.homeLogoImgView).setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
              //  showManualDialog(0)
                startActivity(LogActivity::class.java)
                return true
            }

        })

        homeBottomCheckView?.setOnItemCheck(object : OnItemCheckedListener{
            override fun onItemCheck(position: Int, isChecked: Boolean) {

                if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                    homeBottomCheckView?.setAllNoCheck()
                    showNotConnDialog()
                    return
                }


                if(position == 0x00){   //打气
                    controlViewModel?.setManualAerate(isChecked)
                }
                if(position == 0x01){   //设置预置位保持自动校准
                    controlViewModel?.setPreAutoIsEnable(isChecked)
                }

               if(position == 2){  //异常信息
                   startActivity(CarFaultNotifyActivity::class.java)
               }

                if(position == 0x03){   //主动干燥
                    controlViewModel?.setActiveDrying()
                }
            }

        })

        homeBottomNumberView?.setOnItemClick{
            if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                homeBottomNumberView?.clearAllClick()
                homeBottomNumberView?.setIsLowGear(false)
                showNotConnDialog()
                return@setOnItemClick
            }
            handlers.removeMessages(0x00)
            handlers.sendEmptyMessageDelayed(0x00,5000)
            if(BaseApplication.getBaseApplication().autoBackBean?.deviceMode==0){  //高度模式
                showRulerGoalVisibility(true)
            }else{
                showRulerGoalVisibility(false)
            }

            if(it == -1){
                controlViewModel?.setOneGearReset()
            }else{
                controlViewModel?.getHomeGear(it)
            }

        }
    }


    private var tempGear =-1

    override fun initData() {
        errorNotifyViewModel = ViewModelProvider(this)[CarErrorNotifyViewModel::class.java]
        showRulerGoalVisibility(false)
        //homeLeftAirPressureView?.setAirPressureValue(0)
        carHomeCenterView?.setFrontImage()
//        carHomeCenterView?.setFrontHeightValue(50,50)
       // carHomeCenterView?.setAfterHeightValue(100,80)
        homeRightView?.setTempValue(100)

        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        errorNotifyViewModel?.carErrorDescList?.observe(this){
          if(it.isNotEmpty()){
              homeErrorMsgTv?.text = it[0]
          }
        }
        //carHomeCenterView?.setShowHeightIndicator(false)



        controlViewModel?.autoSetBeanData?.observe(this){
            if(it != null){
                val autoBackBean =  BaseApplication.getBaseApplication().autoBackBean
                if(autoBackBean != null){
                    val isHeightModel = it.modelType==0
                    Timber.e("------onResume-----backHorPMode="+autoBackBean.deviceMode+" isHeightModel="+isHeightModel)

                    if(autoBackBean.deviceMode == 0){  //气压+高度
                        carHomeCenterView?.setShowHeightIndicator(isHeightModel)
                    }else{  //气压
                        carHomeCenterView?.setShowHeightIndicator(false)
                    }
                }
            }
        }

        carHomeCenterView?.setOnPressureListener(object : OnControlPressureCheckedListener{
            override fun onItemChecked(map: MutableMap<Int, Int>?) {

                if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                    showNotConnDialog()
                    return
                }
                controlViewModel?.setManualOperation(map!! as HashMap<Int, Int>)
            }
        })
        val carActivity = attachActivity as CarHomeActivity
        carActivity.setHomeAutoListener(object : CarHomeActivity.OnHomeAutoBackListener{
            override fun backAutoData(autoBean: AutoBackBean) {
                if(carActivity.isFinishing){
                    return
                }
                  Timber.e("----监听------自动返回数据="+autoBean.toString())

                if(tempGear == -1){
                    showRulerGoalVisibility(false)
                    tempGear = autoBean.curPos
                }

                if(tempGear != autoBean.curPos){
                    tempGear = autoBean.curPos
                    handlers.removeMessages(0x00)
                    handlers.sendEmptyMessageDelayed(0x00,5000)
                    showRulerGoalVisibility(true)
                }

                //档位
                if(autoBean.curPos == 5){
                    homeBottomNumberView?.setIsLowGear(true)
                }else{
                    homeBottomNumberView?.setClickIndexNoResponse(autoBean.curPos-1)
                }

                carHomeCenterView?.setLeftRearPressureValue(autoBean.leftRearPressure)
                carHomeCenterView?.setRightTopPressureValue(autoBean.rightPressure)
                carHomeCenterView?.setLeftTopPressureValue(autoBean.leftPressure)
                carHomeCenterView?.setRightRearPressureValue(autoBean.rightRearPressure)

                carHomeCenterView?.setFrontHeightValue(autoBean.leftFrontRulerFL,autoBean.rightFrontRulerFL)
                carHomeCenterView?.setAfterHeightValue(autoBean.leftRearRulerFL,autoBean.rightRearRulerFL)

                carHomeCenterView?.setFrontGoalValue(autoBean.leftFrontGoalFL,autoBean.rightFrontGoalFL)
                carHomeCenterView?.setRearGoalValue(autoBean.leftRearGoalFL,autoBean.rightRearGoalFL)

                homeLeftAirPressureView?.setAirPressureValue(autoBean.cylinderPressure)
                homeRightView?.setTempValue(autoBean.airBottleTemperature )


                val max = MmkvUtils.getMaxPressureValue()
                if(autoBean.cylinderPressure>=max){
                    homeBottomCheckView?.setDefaultPushAir()
                }

                //设备异常
                val deviceErrorCode = autoBean.deviceErrorCode
                //气罐故障
                val airErrorCode = autoBean.airBottleErrorCode
                //左前
                val leftFrontCode = autoBean.leftFrontErrorCode
                //左后
                val leftRearCode = autoBean.leftRearErrorCode
                //右前
                val rightFront = autoBean.rightFrontErrorCode
                //右后
                val rightRearCode = autoBean.rightRearErrorCode

                val errorArray = Utils.byteToBit(deviceErrorCode)
                val errorMap = getDeviceErrorMsg(errorArray)
                val isEmpty =errorMap.size==0


                //  Timber.e("-------设备异常="+errorMap.toString()+" "+isEmpty)

                val errorCount = deviceErrorCode+airErrorCode+leftFrontCode+leftRearCode+rightFront+rightRearCode
                if(errorCount ==0){
                    tempErrorCount = 0
                    homeDeviceErrorLayout?.visibility = View.INVISIBLE
                    homeBottomCheckView?.setErrorChecked(false)
                }else{
                    if(tempErrorCount != errorCount){
                        homeDeviceErrorLayout?.visibility = View.VISIBLE
                        handlers.sendEmptyMessageDelayed(0x08,15000)
                        tempErrorCount = errorCount
                        errorNotifyViewModel?.getAllErrorDesc(attachActivity,deviceErrorCode,airErrorCode,
                            leftFrontCode,leftRearCode,rightFront,rightRearCode)
                    }
                }
//                homeErrorMsgTv?.text = if(isEmpty) "" else strArray[0]
//                homeDeviceErrorLayout?.visibility = if(isEmpty) View.INVISIBLE else View.VISIBLE

                //carHomeCenterView?.setShowHeightIndicator(autoBean.deviceMode==0)
            }
        })


        val homeActivity = attachActivity
        homeActivity?.setHomeConnListener(object : CarHomeActivity.OnHomeConnStatusListener{
            override fun onConn(isConn: Boolean) {
               if(!isConn){
                   showDefaultViews()
               }
                controlViewModel?.writeCommonFunction()
                handlers.sendEmptyMessage(0x00)
            }
        })
    }


    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        val isConn = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED
        if(!isConn){
            showDefaultViews()
        }else{
            controlViewModel?.writeCommonFunction()
        }
    }


    private fun showDefaultViews(){
        carHomeCenterView?.setHomeCenterDefault()
        homeErrorMsgTv?.text = ""
        homeDeviceErrorLayout?.visibility = View.INVISIBLE
        homeLeftAirPressureView?.setAirPressureValue(0)
        homeRightView?.setTempValue(0)
        homeBottomNumberView?.clearAllClick()
        showRulerGoalVisibility(false)
    }


    val map = HashMap<Int, String>()
    private fun getDeviceErrorMsg(errorStr: String): HashMap<Int, String> {

        map.clear()
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            map[0] = resources.getString(R.string.string_e_1)
        }
        if (chartArray[1].toString() == "1") {
            map[1] = resources.getString(R.string.string_e_2)
        }
        if (chartArray[2].toString() == "1") {
            map[2] = resources.getString(R.string.string_e_3)
        }
        if (chartArray[3].toString() == "1") {
            map[3] = resources.getString(R.string.string_e_4)
        }
        if (chartArray[4].toString() == "1") {
            map[4] = resources.getString(R.string.string_e_5)
        }
        if (chartArray[5].toString() == "1") {
            map[5] = resources.getString(R.string.string_e_6)
        }
        if (chartArray[6].toString() == "1") {
            map[6] = resources.getString(R.string.string_e_7)
        }
        return map
    }


    private fun showNotConnDialog(){
        attachActivity?.showCommAlertDialog(resources.getString(R.string.string_not_conn_device),resources.getString(R.string.string_go_to_official_website),resources.getString(R.string.string_go_to_conn)
        ) { position ->
            attachActivity?.disCommAlertDialog()
            if (position == 0x01) {
                startActivity(SecondScanActivity::class.java)
            }
            if (position == 0x00) {
               // startActivity(ShowWebActivity::class.java)
                attachActivity?.switchFragment(0)
            }
        }
    }

    //显示或隐藏
    private fun showRulerGoalVisibility(visibility: Boolean){
        carHomeCenterView?.setGoalVisibility(visibility)
    }

    private fun showManualDialog(position : Int){
        val dialog = ManualSetHeightView(attachActivity, com.bonlala.base.R.style.CusDialogTheme)
        dialog.show()
        dialog.setModel(position==3)
        dialog.setOnDialogClickListener{
            if(it == 0x01){ //保存了
                dialog.dismiss()
                //  manualCheckPager?.setCurrentItem(position+1,false)
            }
            if(it == 0x00){
                dialog.dismiss()

            }
        }
        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels
        val height : Int = metrics2.heightPixels

        windowLayout?.height= height
        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.CENTER_VERTICAL
        window?.attributes = windowLayout
    }

}