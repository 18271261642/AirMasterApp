package com.app.airmaster.car.fragment

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.viewmodel.ControlViewModel
import com.app.airmaster.widget.CommTitleView
import com.bonlala.widget.view.SwitchButton
import com.hjq.shape.layout.ShapeConstraintLayout
import timber.log.Timber


/**
 * 气压平衡
 */
class CarAirPressureBalanceFragment : TitleBarFragment<CarSysSetActivity>(){


    private var viewModel : ControlViewModel?= null

    companion object{
        fun getInstance() : CarAirPressureBalanceFragment{
            return CarAirPressureBalanceFragment()
        }
    }

    private var sysAirBalanceTitleView : CommTitleView ?= null

    //高度优先
    private var airBalanceHeightFirstSwitch : ImageView ?= null
    //气压优先
    private var airBalanceFirstSwitch : ImageView?= null
    //气压平衡
    private var airBalanceSwitch : SwitchButton ?= null

    private var airBalanceContent : LinearLayout ?= null


    var clickNumberAir = 0
    var clickNumberHeight = 0

    override fun getLayoutId(): Int {
        return R.layout.fragment_air_pressure_balance_layout
    }

    override fun initView() {
        airBalanceContent = findViewById(R.id.airBalanceContent)
        airBalanceHeightFirstSwitch = findViewById(R.id.airBalanceHeightFirstSwitch)
        airBalanceFirstSwitch = findViewById(R.id.airBalanceFirstSwitch)
        airBalanceSwitch = findViewById(R.id.airBalanceSwitch)
        sysAirBalanceTitleView = findViewById(R.id.sysAirBalanceTitleView)

        sysAirBalanceTitleView?.setCommTitleTxt("气压平衡")
        sysAirBalanceTitleView?.setOnItemClick{
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }


        airBalanceSwitch?.setOnCheckedChangeListener { button, checked ->
            Timber.e("------pressed="+button.isPressed)
            viewModel?.setAirBalanceSwitch(checked)
            isShowVisibility(checked)
        }

        //气压优先
        findViewById<ShapeConstraintLayout>(R.id.ariFirstLayout).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            clickNumberAir = 0
            viewModel?.setAirBalanceLevel(0)
            checkAirOrHeight(true)
        }


        //高度优先
        findViewById<ShapeConstraintLayout>(R.id.heightFirstLayout).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            clickNumberAir = 0
            viewModel?.setAirBalanceLevel(1)
            checkAirOrHeight(false)
        }

    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        viewModel?.autoSetBeanData?.observe(this){
            //是否气压平衡
            val isBalance = it.pressureBalance ==1
            airBalanceSwitch?.isChecked = isBalance
            if(isBalance ){
                clickNumberAir++
                //等级
                val balanceLevel = it.autoPressureBalanceLevel
                checkAirOrHeight(balanceLevel == 0)
            }

            isShowVisibility(isBalance )
        }

        viewModel?.writeCommonFunction()
    }


    //是否打开
    private fun isShowVisibility(show : Boolean){
        airBalanceContent?.visibility = if(show) View.VISIBLE else View.GONE
    }

    //选择气压或高度优先
    private fun checkAirOrHeight(air : Boolean){
        airBalanceFirstSwitch?.setImageResource(if(air)R.mipmap.ic_scan_conn_checked else R.mipmap.ic_scan_conn_no_checked)
        airBalanceHeightFirstSwitch?.setImageResource(if(!air)R.mipmap.ic_scan_conn_checked else R.mipmap.ic_scan_conn_no_checked)
    }
}