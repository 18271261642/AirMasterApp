package com.app.airmaster.car.fragment

import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.adapter.OnItemCheckedListener
import com.app.airmaster.car.CarFaultNotifyActivity
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.car.view.HomeBottomCheckView
import com.app.airmaster.car.view.HomeBottomNumberView
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.widget.CusVerticalScheduleView
import com.app.airmaster.widget.CusVerticalTextScheduleView
import com.app.airmaster.widget.VerticalSeekBar
import com.app.airmaster.widget.VerticalSeekBar.OnSeekBarChangeListener

/**
 * Created by Admin
 *Date 2023/7/14
 */
class HomeControlFragment : TitleBarFragment<CarHomeActivity>() {


    private var cusVerticalView : CusVerticalScheduleView ?= null
    private var cusVerticalTxtView : CusVerticalTextScheduleView ?= null
    private var homeLeftAirSeekBar : VerticalSeekBar?= null

    private var homeBottomCheckView : HomeBottomCheckView ?= null
    private var homeBottomNumberView : HomeBottomNumberView ?= null



    companion object{
        fun getInstance() : HomeControlFragment{
            return HomeControlFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_home_control_layut
    }

    override fun initView() {
        homeLeftAirSeekBar = findViewById(R.id.homeLeftAirSeekBar)
        cusVerticalTxtView= findViewById(R.id.cusVerticalTxtView)
        cusVerticalView = findViewById(R.id.cusVerticalView)
        homeBottomCheckView = findViewById(R.id.homeBottomCheckView)
        homeBottomNumberView = findViewById(R.id.homeBottomNumberView)


        homeBottomCheckView?.setOnItemCheck(object : OnItemCheckedListener{
            override fun onItemCheck(position: Int, isChecked: Boolean) {
               if(position == 2){
                   startActivity(CarFaultNotifyActivity::class.java)
               }
            }

        })



        homeBottomNumberView?.setOnTouchListener(object : OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                attachActivity.showCommAlertDialog("未连接设备","去官网","去连接",object :
                    OnCommItemClickListener {
                    override fun onItemClick(position: Int) {
                        attachActivity.disCommAlertDialog()
                        if(position == 0x01){
                            startActivity(SecondScanActivity::class.java)
                        }
                    }

                })

                return true
            }

        })


        cusVerticalView?.allScheduleValue = 150F
        cusVerticalTxtView?.allScheduleValue = 150F


        homeLeftAirSeekBar?.max = 150
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            homeLeftAirSeekBar?.min = 0
        }
        homeLeftAirSeekBar?.progress = 130
        showSchedule(130)
        homeLeftAirSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(VerticalSeekBar: VerticalSeekBar?, progress: Int) {
                showSchedule(progress)
            }

            override fun onStartTrackingTouch(VerticalSeekBar: VerticalSeekBar?) {

            }

            override fun onStopTrackingTouch(VerticalSeekBar: VerticalSeekBar?) {

            }

        })
    }

    private fun showSchedule(value : Int){
        cusVerticalView?.setCurrScheduleValue(value.toFloat())
        cusVerticalTxtView?.setCurrScheduleValue(value)
    }

    override fun initData() {

    }


}