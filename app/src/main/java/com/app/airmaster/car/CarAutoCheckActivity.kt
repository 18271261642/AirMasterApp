package com.app.airmaster.car

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.GuideAdapter
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.bean.CheckBean
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.viewmodel.CarCheckViewModel
import me.relex.circleindicator.CircleIndicator3
import timber.log.Timber


/**
 * 自动自检
 */
class CarAutoCheckActivity : AppActivity() {


    private var viewModel : CarCheckViewModel ?= null


    private var checkPager : ViewPager2 ?= null
    private var checkIndicator : CircleIndicator3 ?= null
    private var adapter : GuideAdapter ?= null

    //退出
    private var autoCheckExitLayout : LinearLayout ?= null

    //是否正在检测
    private var isCheckIng = false

    private var checkHashMap = HashMap<Int,Int>()

    private var handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_car_auto_check_layout
    }

    override fun initView() {
        autoCheckExitLayout = findViewById(R.id.autoCheckExitLayout)
        checkPager = findViewById(R.id.checkPager)
        checkIndicator = findViewById(R.id.checkIndicator)


        autoCheckExitLayout?.setOnClickListener {
            intoOrExitCheck(false)
            finish()
        }
    }


    private var errorSb = StringBuffer()

    override fun initData() {
        viewModel = ViewModelProvider(this)[CarCheckViewModel::class.java]
        adapter = GuideAdapter(this)
        checkPager?.adapter = adapter
        checkPager?.isUserInputEnabled = false
        checkPager?.registerOnPageChangeCallback(mCallback)
        checkIndicator?.setViewPager(checkPager)

        viewModel?.carCheckData?.observe(this){
            adapter?.data = it
            checkIndicator?.setViewPager(checkPager)
        }
        viewModel?.dealCheckData(this)



        viewModel?.checkBackDataMap?.observe(this){
            val bean = it
            checkHashMap[bean.checkStep] = bean.errorCode
            //步骤
            val checkStep = bean?.checkStep
            val itemBean = CheckBean()
            itemBean.checkContent = getTitleDesc(checkStep!!)
            itemBean.checkStatus = bean!!.checkStatus
            itemBean.errorCode = bean!!.errorCode
            itemBean.checkStep = checkStep!!

           val  position = checkStep!!-1
            isCheckIng = true
            if(bean.checkStatus == 0){  //失败
                isCheckIng = false
                errorSb.append(getErrorDesc(bean.errorCode)+"\n")
                itemBean.failDesc = errorSb.toString()
                autoCheckExitLayout?.visibility = View.VISIBLE
                checkIndicator?.visibility = View.GONE
            }

            Timber.e("-----自检--itemBean=$itemBean")

            checkPager?.currentItem = position
            adapter?.setItem(position,itemBean)
        }


        intoOrExitCheck(true)

    }

    //进入或退出自检
    private fun intoOrExitCheck(into : Boolean){
        if(into){
            checkHashMap.clear()
        }
        //开始自检
        viewModel?.intoOrExit(into,true)
    }



    override fun onDestroy() {
        super.onDestroy()
        checkPager!!.unregisterOnPageChangeCallback(mCallback)
    }


    private val mCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (checkPager!!.currentItem != adapter!!.itemCount - 1 || positionOffsetPixels <= 0) {
                return
            }

        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return
            }

        }
    }


    private var titleMap = HashMap<Int,String>()
    private fun getTitleDesc(position : Int) : String?{
        titleMap[1] = context.resources.getString(R.string.string_check1_desc)
        titleMap[2] = context.resources.getString(R.string.string_check2_desc)
        titleMap[3] = context.resources.getString(R.string.string_check3_desc)
        titleMap[4] = context.resources.getString(R.string.string_check4_desc)
        titleMap[5] = context.resources.getString(R.string.string_check5_desc)
        titleMap[6] = context.resources.getString(R.string.string_check6_desc)
        return titleMap[position]
    }

    private fun getErrorDesc(code : Int) : String? {
        val map = HashMap<Int,String>()
        map[0] = ""
        map[1] = "电池电压异常"
        map[2] = "ACC未启动"
        map[3] = "ACC未启动"
        map[4] = "自检超时"
        map[5] = "左前高度传感器超量程"
        map[6] = "右前高度传感器超量程"
        map[7] = "左后高度传感器超量程"
        map[8] = "右后高度传感器超量程"
        map[9] = "左前气压传感器故障"
        map[10] = "右前气压传感器故障"
        map[11] = "左后气压传感器故障"
        map[12] = "右后气压传感器故障"
        map[13] = "左前高度传感器故障"
        map[14] = "右前高度传感器故障"
        map[15] = "左后高度传感器故障"
        map[16] = "右后高度传感器故障"
        map[17] = "左前高度传感器测量范围过小"
        map[18] = "右前高度传感器测量范围过小"
        map[19] = "左后高度传感器测量范围过小"
        map[20] = "右后高度传感器测量范围过小"
        map[21] = "左前高度传感器线序错误"
        map[22] = "右前高度传感器线序错误"
        map[23] = "左后高度传感器线序错误"
        map[24] = "右后高度传感器线序错误"

        return map.get(code)

    }

    override fun onBackPressed() {
        if(isCheckIng){
            showDialogShow()
            return
        }
        super.onBackPressed()
    }


    private fun showDialogShow(){
        val dialog = ConfirmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt("是否退出自动检测?")
        dialog.setOnCommClickListener(object : OnCommItemClickListener {
            override fun onItemClick(position: Int) {
                dialog.dismiss()

                if(position==1){
                    intoOrExitCheck(false)
                    finish()
                }
            }

        })
    }
}