package com.app.airmaster.car

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.GuideAdapter
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.bean.CheckBean
import com.app.airmaster.car.check.CheckSuccessActivity
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.dialog.LogDialogView
import com.app.airmaster.viewmodel.CarCheckViewModel
import com.app.airmaster.viewmodel.CarErrorNotifyViewModel
import com.blala.blalable.Utils
import com.hjq.shape.view.ShapeTextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator3
import timber.log.Timber


/**
 * 自动自检
 */
class CarAutoCheckActivity : AppActivity() {

    private var errorNotifyViewModel : CarErrorNotifyViewModel?= null
    private var viewModel : CarCheckViewModel ?= null


    private var checkPager : ViewPager2 ?= null
    private var checkIndicator : CircleIndicator3 ?= null
    private var adapter : GuideAdapter ?= null

    //退出
    private var autoCheckExitLayout : LinearLayout ?= null
    private var autoCheckConfirmBtn : ShapeTextView ?= null

    //是否正在检测
    private var isCheckIng = false

    private var checkHashMap = HashMap<Int,Int>()

    private var logList = mutableListOf<CheckBean>()

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
        autoCheckConfirmBtn = findViewById(R.id.autoCheckConfirmBtn)
        autoCheckExitLayout = findViewById(R.id.autoCheckExitLayout)
        checkPager = findViewById(R.id.checkPager)
        checkIndicator = findViewById(R.id.checkIndicator)


        autoCheckConfirmBtn?.setOnClickListener {
            intoOrExitCheck(false)
            GlobalScope.launch {
                delay(800)
                finish()
            }
        }

        autoCheckExitLayout?.setOnClickListener {
            intoOrExitCheck(false)
            GlobalScope.launch {
                delay(1000)
                finish()
            }

        }

        findViewById<ImageView>(R.id.logImgView).setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                val str = StringBuffer()
                logList.forEach {
                    str.append(it.toString()+"\n\n")
                }
                showLogDialog(str.toString())

                return true
            }

        })
    }


    private var errorSb = StringBuffer()

    private var autoErrorSb = StringBuffer()
    override fun initData() {
        errorNotifyViewModel = ViewModelProvider(this)[CarErrorNotifyViewModel::class.java]
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

        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener { it ->
            autoErrorSb.delete(0,autoErrorSb.length)
            if (it != null) {
                //左前
                val leftFrontCode = it.leftFrontErrorCode
                val leftArray = Utils.byteToBit(leftFrontCode)
                Timber.e("--------左前故障="+String.format("%02x",leftFrontCode)+" "+leftArray)

               // sb.append("左前故障状态码:"+leftFrontCode+" 转换:"+leftArray).append("\n")

                val leftMap = getWheelsError(leftArray,0)
                leftMap.forEach {
                    autoErrorSb.append(it)
                }

                //左后
                val leftRearCode = it.leftRearErrorCode
                val leftRearArray = Utils.byteToBit(leftRearCode)
                Timber.e("--------左后故障="+String.format("%02x",leftRearCode)+" "+leftRearArray)

                //sb.append("左后故障状态码:"+leftRearCode+" 转换:"+leftRearArray).append("\n")

                val leftRearMap = getWheelsError(leftRearArray,2)
                leftRearMap.forEach {

                    autoErrorSb.append(it)
                }

                //右前
                val rightFront = it.rightFrontErrorCode
                val rightFrontArray = Utils.byteToBit(rightFront)
                Timber.e("--------右前故障="+String.format("%02x",rightFront)+" "+rightFrontArray)

              //  sb.append("右前故障状态码:"+rightFront+" 转换:"+rightFrontArray).append("\n")
                val rightFrontMap = getWheelsError(rightFrontArray,1)
                rightFrontMap.forEach {
                    autoErrorSb.append(it)
                }

                //右后
                val rightRearCode = it.rightRearErrorCode
                val rightRearArray = Utils.byteToBit(rightRearCode)
                Timber.e("--------右后故障="+String.format("%02x",rightRearCode)+" "+rightRearArray)
                //sb.append("右后故障状态码:"+rightRearCode+" 转换:"+rightRearArray).append("\n\n\n")
                val rightRearMap = getWheelsError(rightRearArray,3)
                rightRearMap.forEach {
                    autoErrorSb.append(it)
                }
            }
        }

        viewModel?.checkBackDataMap?.observe(this){
            val bean = it
            logList.add(bean)
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
              //  autoCheckExitLayout?.visibility = View.VISIBLE
                checkIndicator?.visibility = View.GONE
                autoCheckConfirmBtn?.visibility = View.VISIBLE
            }

            Timber.e("-----自检--itemBean=$itemBean")
            val p = if(position>=adapter?.itemCount!!-1) adapter?.itemCount!!-1 else position
            checkPager?.currentItem = p
            adapter?.setItem(p,itemBean)

            if(checkStep== 7 && bean.checkStatus==1){
                checkPager?.currentItem = p
                autoCheckConfirmBtn?.visibility = View.VISIBLE
//                val intent = Intent(this@CarAutoCheckActivity,CheckSuccessActivity::class.java)
//                intent.putExtra("isAuto",true)
//                startActivity(intent)
//                finish()
//                adapter?.setItem(p,itemBean)
                GlobalScope.launch {
                    delay(5000)
                    intoOrExitCheck(false)
                    finish()
                }
                return@observe
            }
        }


        intoOrExitCheck(true)

    }

    //进入或退出自检
    private fun intoOrExitCheck(into : Boolean){
        if(into){
            checkHashMap.clear()
            logList.clear()
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
        map[1] = resources.getString(R.string.string_battery_voltage_error)
        map[2] = resources.getString(R.string.string_acc_not_start)
        map[3] = resources.getString(R.string.string_acc_not_start)
        map[4] = resources.getString(R.string.string_auto_check_time_out)
        map[5] = resources.getString(R.string.string_car_lr)+" "+resources.getString(R.string.string_car_h_e_1)
        map[6] = resources.getString(R.string.string_car_rr)+" "+resources.getString(R.string.string_car_h_e_1)
        map[7] = resources.getString(R.string.string_car_ll)+" "+resources.getString(R.string.string_car_h_e_1)
        map[8] = resources.getString(R.string.string_car_rl)+" "+resources.getString(R.string.string_car_h_e_1)
        map[9] = resources.getString(R.string.string_car_lr)+" "+resources.getString(R.string.string_car_h_e_6)
        map[10] = resources.getString(R.string.string_car_rr)+" "+resources.getString(R.string.string_car_h_e_6)
        map[11] = resources.getString(R.string.string_car_ll)+" "+resources.getString(R.string.string_car_h_e_6)
        map[12] = resources.getString(R.string.string_car_rl)+" "+resources.getString(R.string.string_car_h_e_6)
        map[13] =  resources.getString(R.string.string_car_lr)+" "+resources.getString(R.string.string_car_h_e_5) // "左前高度传感器故障"
        map[14] = resources.getString(R.string.string_car_rr)+" "+resources.getString(R.string.string_car_h_e_5)//"右前高度传感器故障"
        map[15] = resources.getString(R.string.string_car_ll)+" "+resources.getString(R.string.string_car_h_e_5)//"左后高度传感器故障"
        map[16] = resources.getString(R.string.string_car_rl)+" "+resources.getString(R.string.string_car_h_e_5)//"右后高度传感器故障"
        map[17] =  resources.getString(R.string.string_car_lr)+" "+resources.getString(R.string.string_car_h_e_3) //"左前高度传感器测量范围过小"
        map[18] = resources.getString(R.string.string_car_rr)+" "+resources.getString(R.string.string_car_h_e_3)//"右前高度传感器测量范围过小"
        map[19] = resources.getString(R.string.string_car_ll)+" "+resources.getString(R.string.string_car_h_e_3)//"左后高度传感器测量范围过小"
        map[20] = resources.getString(R.string.string_car_rl)+" "+resources.getString(R.string.string_car_h_e_3)//"右后高度传感器测量范围过小"
        map[21] =   resources.getString(R.string.string_car_lr)+" "+resources.getString(R.string.string_car_h_e_2) //"左前高度传感器线序错误"
        map[22] = resources.getString(R.string.string_car_rr)+" "+resources.getString(R.string.string_car_h_e_2)//"右前高度传感器线序错误"
        map[23] = resources.getString(R.string.string_car_ll)+" "+resources.getString(R.string.string_car_h_e_2)//"左后高度传感器线序错误"
        map[24] = resources.getString(R.string.string_car_rl)+" "+resources.getString(R.string.string_car_h_e_2)//"右后高度传感器线序错误"

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
        dialog.setContentTxt(resources.getString(R.string.string_exit_auto_check_prompt))
        dialog.setOnCommClickListener(object : OnCommItemClickListener {
            override fun onItemClick(position: Int) {
                dialog.dismiss()

                if(position==1){
                    intoOrExitCheck(false)
                    GlobalScope.launch {
                        delay(1000)
                        finish()
                    }

                }
            }

        })
    }

    private fun showLogDialog(txt : String){
        val dialog = LogDialogView(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setLogTxt(txt)

        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = (metrics2.widthPixels*0.7F).toInt()
        val height : Int = (metrics2.heightPixels*0.7F).toInt()

        windowLayout?.height= height
        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.CENTER_VERTICAL
        window?.attributes = windowLayout
    }

    /**
     * bit0:高度传感器超量程
    bit1:None
    bit2:高度传感器线序错误
    bit3:高度传感器测量范围过小
    bit4:高度传感器装反
    bit5:高度传感器故障
    bit6:气压传感器故障
    bit7:空气弹簧漏气
     */
    private val wheelMap = HashMap<Int,String>()
    private fun getWheelsError(errorStr: String,code :Int) : HashMap<Int,String>{
        wheelMap.clear()
        val wheel = getWheelType(code)
        val chartArray = errorStr.toCharArray()
        if (chartArray[0].toString() == "1") {
            wheelMap[0] = wheel+resources.getString(R.string.string_car_h_e_1)
        }
        if (chartArray[1].toString() == "1") {
            wheelMap[1] = wheel+"None"
        }
        if (chartArray[2].toString() == "1") {
            wheelMap[2] = wheel+resources.getString(R.string.string_car_h_e_2)
        }
        if (chartArray[3].toString() == "1") {
            wheelMap[3] = wheel+resources.getString(R.string.string_car_h_e_3)
        }
        if (chartArray[4].toString() == "1") {
            wheelMap[4] = wheel+resources.getString(R.string.string_car_h_e_4)
        }
        if(chartArray[5].toString()=="1"){
            wheelMap[5] = wheel+resources.getString(R.string.string_car_h_e_5)
        }
        if(chartArray[6].toString()=="1"){
            wheelMap[5] = wheel+resources.getString(R.string.string_car_h_e_6)
        }
        if(chartArray[7].toString()=="1"){
            wheelMap[5] = wheel+resources.getString(R.string.string_car_h_e_7)
        }
        return wheelMap
    }

    private fun getWheelType(code: Int) : String{
        if(code ==0){
            return resources.getString(R.string.string_car_lr)
        }
        if(code == 1){
            return resources.getString(R.string.string_car_rr)
        }
        if(code == 2){
            return resources.getString(R.string.string_car_ll)
        }
        return resources.getString(R.string.string_car_rl)
    }
}