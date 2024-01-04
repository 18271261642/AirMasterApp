package com.app.airmaster.car.fragment

import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.adapter.OnItemCheckedListener
import com.app.airmaster.car.CarFaultNotifyActivity
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.car.view.CarHomeCenterView
import com.app.airmaster.car.view.HomeBottomCheckView
import com.app.airmaster.car.view.HomeBottomNumberView
import com.app.airmaster.car.view.HomeLeftAirPressureView
import com.app.airmaster.car.view.HomeRightTemperatureView
import com.app.airmaster.listeners.OnControlPressureCheckedListener
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CusVerticalScheduleView
import com.app.airmaster.widget.CusVerticalTextScheduleView
import com.app.airmaster.widget.VerticalSeekBar
import com.app.airmaster.widget.VerticalSeekBar.OnSeekBarChangeListener
import timber.log.Timber

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




    companion object{
        fun getInstance() : HomeControlFragment{
            return HomeControlFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_home_control_layut
    }

    override fun initView() {
        homeRightView = findViewById(R.id.homeRightView)
        homeLeftAirPressureView = findViewById(R.id.homeLeftAirPressureView)
        carHomeCenterView = findViewById(R.id.carHomeCenterView)
        homeBottomCheckView = findViewById(R.id.homeBottomCheckView)
        homeBottomNumberView = findViewById(R.id.homeBottomNumberView)

        homeBottomCheckView?.setOnItemCheck(object : OnItemCheckedListener{
            override fun onItemCheck(position: Int, isChecked: Boolean) {
                if(position == 0x00){   //打气
                    controlViewModel?.setManualAerate(isChecked)
                }
                if(position == 0x01){   //预设高度

                }

               if(position == 2){
                   startActivity(CarFaultNotifyActivity::class.java)
               }

                if(position == 0x03){   //排水
                    controlViewModel?.setMoistureModel(if(isChecked) 0 else 1)
                }
            }

        })

        homeBottomNumberView?.setOnItemClick{
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

        carHomeCenterView?.setFrontImage()
//        carHomeCenterView?.setFrontHeightValue(50,50)
//        carHomeCenterView?.setAfterHeightValue(100,80)


        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener {
            Timber.e("---------自动回复="+it.toString())
            carHomeCenterView?.setLeftRearPressureValue(it.leftRearPressure)
            carHomeCenterView?.setRightTopPressureValue(it.rightPressure)
            carHomeCenterView?.setLeftTopPressureValue(it.leftPressure)
            carHomeCenterView?.setRightRearPressureValue(it.rightRearPressure)

            carHomeCenterView?.setFrontHeightValue(it.leftFrontHeightRuler,it.rightFrontHeightRuler)
            carHomeCenterView?.setAfterHeightValue(it.leftAfterHeightRuler,it.rightAfterHeightRuler)


            homeLeftAirPressureView?.setAirPressureValue(it.cylinderPressure)
            homeRightView?.setTempValue(it.airBottleTemperature -127)


        }

        carHomeCenterView?.setOnPressureListener(object : OnControlPressureCheckedListener{
            override fun onItemChecked(map: MutableMap<Int, Int>?) {
                controlViewModel?.setManualOperation(map!! as HashMap<Int, Int>)
            }

        })
    }




}