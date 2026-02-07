package com.app.airmaster.car.check

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.GuideAdapter
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.bean.CheckBean
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.dialog.ManualSetHeightView
import com.app.airmaster.viewmodel.CarCheckViewModel
import com.hjq.shape.view.ShapeTextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator3
import timber.log.Timber

class ManualCheckActivity : AppActivity() {

    private var manualCheckPager : ViewPager2 ?= null
    private var manualCheckIndicator : CircleIndicator3 ?= null
    private var adapter : GuideAdapter?= null
    private var viewModel : CarCheckViewModel?= null

    private var errorSb = StringBuffer()
    private var checkConfirmBtn : ShapeTextView ?= null
    //是否正在检测
    private var isCheckIng = false


    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                finish()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_car_manual_check_layout
    }

    override fun initView() {
        checkConfirmBtn = findViewById(R.id.checkConfirmBtn)
        manualCheckPager = findViewById(R.id.manualCheckPager)
        manualCheckIndicator = findViewById(R.id.manualCheckIndicator)

        checkConfirmBtn?.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[CarCheckViewModel::class.java]
        adapter = GuideAdapter(this)
        manualCheckPager?.adapter = adapter
       // manualCheckPager?.isUserInputEnabled = false
        manualCheckPager?.registerOnPageChangeCallback(mCallback)
        manualCheckIndicator?.setViewPager(manualCheckPager)


        viewModel?.carCheckData?.observe(this){
            adapter?.data = it
            manualCheckIndicator?.setViewPager(manualCheckPager)
        }
        viewModel?.dealCheckData(this)


        viewModel?.manualCheckData?.observe(this){
            val bean = it
            Timber.e("---------自检="+it.toString())
//            logList.add(bean)
//            checkHashMap[bean.checkStep] = bean.errorCode
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
//                autoCheckExitLayout?.visibility = View.VISIBLE
//                checkIndicator?.visibility = View.GONE
                manualCheckPager?.currentItem = position
                adapter?.setItem(position,itemBean)
             //   finish()
                checkConfirmBtn?.visibility = View.VISIBLE
                manualCheckPager?.isUserInputEnabled = false
                return@observe
            }

            Timber.e("-----自检--itemBean=$itemBean")

            if(position<adapter?.itemCount!!){
                manualCheckPager?.currentItem = position
                adapter?.setItem(position,itemBean)
            }


            if(checkStep==6 ){

                checkConfirmBtn?.visibility = View.VISIBLE
                manualCheckPager?.isUserInputEnabled = false

//                val intent = Intent(this@ManualCheckActivity,CheckSuccessActivity::class.java)
//                intent.putExtra("isAuto",false)
//                startActivity(intent)
//                finish()
                handlers.sendEmptyMessageDelayed(0x00,5000)
              //  finish()
            }
        }

        viewModel?.setManualCheckBack()
        intoOrExitCheck(true)
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel?.clearManualCheck()
    }

    private fun intoOrExitCheck(into : Boolean){
        if(into){
//            checkHashMap.clear()
//            logList.clear()
        }
        //开始自检
        viewModel?.intoOrExit(into,false)
    }


    private val mCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

            if (manualCheckPager!!.currentItem != adapter!!.itemCount - 1 || positionOffsetPixels <= 0) {
                return
            }



        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return
            }

        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if(position == 0 || position == 3){
                showManualDialog(position)
            }
        }
    }


    private fun showManualDialog(position : Int){
        val deviceModel = BaseApplication.getBaseApplication().autoBackBean?.deviceMode==0
        val dialog = ManualSetHeightView(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setShowHeightModel(deviceModel)
        dialog.setModel(position==3)
        dialog.setOnDialogClickListener{
            if(it == 0x01){ //保存了
                dialog.dismiss()
              //  manualCheckPager?.setCurrentItem(position+1,false)
            }
            if(it == 0x00){
                dialog.dismiss()
                intoOrExitCheck(false)
                lifecycleScope.launch {
                    delay(500)
                    finish()
                }
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
        map[5] = resources.getString(R.string.string_car_lr)+resources.getString(R.string.string_car_h_e_1)
        map[6] = resources.getString(R.string.string_car_rr)+resources.getString(R.string.string_car_h_e_1)
        map[7] = resources.getString(R.string.string_car_ll)+resources.getString(R.string.string_car_h_e_1)
        map[8] = resources.getString(R.string.string_car_rl)+resources.getString(R.string.string_car_h_e_1)
        map[9] = resources.getString(R.string.string_car_lr)+resources.getString(R.string.string_car_h_e_6)
        map[10] = resources.getString(R.string.string_car_rr)+resources.getString(R.string.string_car_h_e_6)
        map[11] = resources.getString(R.string.string_car_ll)+resources.getString(R.string.string_car_h_e_6)
        map[12] = resources.getString(R.string.string_car_rl)+resources.getString(R.string.string_car_h_e_6)
        map[13] =  resources.getString(R.string.string_car_lr)+resources.getString(R.string.string_car_h_e_5) // "左前高度传感器故障"
        map[14] = resources.getString(R.string.string_car_rr)+resources.getString(R.string.string_car_h_e_5)//"右前高度传感器故障"
        map[15] = resources.getString(R.string.string_car_ll)+resources.getString(R.string.string_car_h_e_5)//"左后高度传感器故障"
        map[16] = resources.getString(R.string.string_car_rl)+resources.getString(R.string.string_car_h_e_5)//"右后高度传感器故障"
        map[17] =  resources.getString(R.string.string_car_lr)+resources.getString(R.string.string_car_h_e_3) //"左前高度传感器测量范围过小"
        map[18] = resources.getString(R.string.string_car_rr)+resources.getString(R.string.string_car_h_e_3)//"右前高度传感器测量范围过小"
        map[19] = resources.getString(R.string.string_car_ll)+resources.getString(R.string.string_car_h_e_3)//"左后高度传感器测量范围过小"
        map[20] = resources.getString(R.string.string_car_rl)+resources.getString(R.string.string_car_h_e_3)//"右后高度传感器测量范围过小"
        map[21] =   resources.getString(R.string.string_car_lr)+resources.getString(R.string.string_car_h_e_2) //"左前高度传感器线序错误"
        map[22] = resources.getString(R.string.string_car_rr)+resources.getString(R.string.string_car_h_e_2)//"右前高度传感器线序错误"
        map[23] = resources.getString(R.string.string_car_ll)+resources.getString(R.string.string_car_h_e_2)//"左后高度传感器线序错误"
        map[24] = resources.getString(R.string.string_car_rl)+resources.getString(R.string.string_car_h_e_2)//"右后高度传感器线序错误"

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
        dialog.setContentTxt(resources.getString(R.string.string_exit_manual_check_prompt))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()

            if (position == 1) {
                intoOrExitCheck(false)
                finish()
            }
        }
    }
}