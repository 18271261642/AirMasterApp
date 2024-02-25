package com.app.airmaster.car.fragment


import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.adapter.OnItemCheckedListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.CarFaultNotifyActivity
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.car.ShowWebActivity
import com.app.airmaster.car.view.CarHomeCenterView
import com.app.airmaster.car.view.HomeBottomCheckView
import com.app.airmaster.car.view.HomeBottomNumberView
import com.app.airmaster.car.view.HomeLeftAirPressureView
import com.app.airmaster.car.view.HomeRightTemperatureView
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.blala.blalable.Utils
import com.blala.blalable.car.AutoBackBean


/**
 * Created by Admin
 *Date 2023/7/14
 */
class HomeControlFragment : TitleBarFragment<CarHomeActivity>() {


    private var controlViewModel : ControlViewModel ?= null


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

               if(position == 2){
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

            if(it == -1){
                controlViewModel?.setOneGearReset()
            }else{
                controlViewModel?.getHomeGear(it)
            }

        }


//
//        homeBottomNumberView?.setOnTouchListener(object : OnTouchListener{
//            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
//                attachActivity.showCommAlertDialog("未连接设备","去官网","去连接",object :
//                    OnCommItemClickListener {
//                    override fun onItemClick(position: Int) {
//                        attachActivity.disCommAlertDialog()
//                        if(position == 0x01){
//                            startActivity(SecondScanActivity::class.java)
//                        }
//                    }
//
//                })
//
//                return true
//            }
//
//        })

    }


    override fun initData() {
        //homeLeftAirPressureView?.setAirPressureValue(0)
        carHomeCenterView?.setFrontImage()
//        carHomeCenterView?.setFrontHeightValue(50,50)
//        carHomeCenterView?.setAfterHeightValue(100,80)


        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        val carActivity = attachActivity as CarHomeActivity
        carActivity.setHomeAutoListener(object : CarHomeActivity.OnHomeAutoBackListener{
            override fun backAutoData(autoBean: AutoBackBean) {

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

                carHomeCenterView?.setFrontHeightValue(autoBean.leftFrontHeightRuler,autoBean.rightFrontHeightRuler)
                carHomeCenterView?.setAfterHeightValue(autoBean.leftAfterHeightRuler,autoBean.rightAfterHeightRuler)

                homeLeftAirPressureView?.setAirPressureValue(autoBean.cylinderPressure)
                homeRightView?.setTempValue(autoBean.airBottleTemperature )

                //设备异常
                val deviceErrorCode = autoBean.deviceErrorCode
                val errorArray = Utils.byteToBit(deviceErrorCode)
                val errorMap = getDeviceErrorMsg(errorArray)
                val isEmpty =errorMap.size==0

                val strArray = mutableListOf<String>()
                errorMap.forEach {
                    strArray.add(it.value)
                }

              //  Timber.e("-------设备异常="+errorMap.toString()+" "+isEmpty)

                homeErrorMsgTv?.text = if(isEmpty) "" else strArray[0]
                homeDeviceErrorLayout?.visibility = if(isEmpty) View.INVISIBLE else View.VISIBLE

            }

        })
      /*
        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener {
            BaseApplication.getBaseApplication().autoBackBean = it
            carHomeCenterView?.setLeftRearPressureValue(it.leftRearPressure)
            carHomeCenterView?.setRightTopPressureValue(it.rightPressure)
            carHomeCenterView?.setLeftTopPressureValue(it.leftPressure)
            carHomeCenterView?.setRightRearPressureValue(it.rightRearPressure)

            carHomeCenterView?.setFrontHeightValue(it.leftFrontHeightRuler,it.rightFrontHeightRuler)
            carHomeCenterView?.setAfterHeightValue(it.leftAfterHeightRuler,it.rightAfterHeightRuler)


            homeLeftAirPressureView?.setAirPressureValue(it.cylinderPressure)
            homeRightView?.setTempValue(it.airBottleTemperature -127)


        }
*/
        carHomeCenterView?.setOnPressureListener(object : OnControlPressureCheckedListener{
            override fun onItemChecked(map: MutableMap<Int, Int>?) {

                if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                    showNotConnDialog()
                    return
                }

                controlViewModel?.setManualOperation(map!! as HashMap<Int, Int>)
            }

        })
    }


    val map = HashMap<Int, String>()
    private fun getDeviceErrorMsg(errorStr: String): HashMap<Int, String> {

        map.clear()
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            map[0] = "系统未自检"
        }
        if (chartArray[1].toString() == "1") {
            map[1] = "加速度传感器故障"
        }
        if (chartArray[2].toString() == "1") {
            map[2] = "电池电压过高"
        }
        if (chartArray[3].toString() == "1") {
            map[3] = "电池电压过低"
        }
        if (chartArray[4].toString() == "1") {
            map[4] = "气泵1温度传感器故障"
        }
        if (chartArray[5].toString() == "1") {
            map[5] = "气泵2温度传感器故障"
        }
        if (chartArray[6].toString() == "1") {
            map[6] = "系统温度传感器故障"
        }
        return map
    }


    private fun showNotConnDialog(){
        attachActivity.showCommAlertDialog("未连接设备","去官网","去连接",object :
                    OnCommItemClickListener {
                    override fun onItemClick(position: Int) {
                        attachActivity.disCommAlertDialog()
                        if(position == 0x01){
                            startActivity(SecondScanActivity::class.java)
                        }
                        if(position == 0x00){
                            startActivity(ShowWebActivity::class.java)
                        }
                    }

                })
    }
}