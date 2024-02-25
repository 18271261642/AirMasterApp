package com.app.airmaster.car

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.GuideAdapter
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

    //数量
    private var position = 0

    private var type = 0


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
        checkPager = findViewById(R.id.checkPager)
        checkIndicator = findViewById(R.id.checkIndicator)

    }

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
            val bean = adapter?.data?.get(position)
          //  bean?.checkContent = it.checkContent
            bean?.checkStatus = it.checkStatus

            if (bean != null) {
                adapter?.setItem(position,bean)
            }
            checkPager?.currentItem = position
            if(it.checkStatus == 2){
                position++
            }

            Timber.e("-----position="+position+" "+it.checkStatus)

            if(position == 3){
                handlers.sendEmptyMessageDelayed(0x00,16 * 1000)
            }
        }


        intoOrExitCheck(true)

    }

    //进入或退出自检
    private fun intoOrExitCheck(into : Boolean){
        //开始自检
        viewModel?.intoOrExit(into)
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
}