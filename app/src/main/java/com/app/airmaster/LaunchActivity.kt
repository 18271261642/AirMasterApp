package com.app.airmaster

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.action.ActivityManager
import com.app.airmaster.action.AppActivity
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.dialog.ShowPrivacyDialogView
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.FilterViewModel

/**
 * Created by Admin
 *Date 2023/7/14
 */
class LaunchActivity : AppActivity() {

    private var filterViewModel : FilterViewModel ?= null

    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                //判断是否需要显示隐私政策，第一次打开需要隐私政策
                val isFirstOpen = MmkvUtils.getPrivacy()
                if (!isFirstOpen) {
                    showPrivacyDialog()
                } else {
                    BaseApplication.getBaseApplication().setAgree()
                    startActivity(CarHomeActivity::class.java)
                    finish()
                }
            }
        }
    }


    override fun getLayoutId(): Int {
       return R.layout.activity_launch_layout
    }

    override fun initView() {

    }

    override fun initData() {
        filterViewModel = ViewModelProvider(this)[FilterViewModel::class.java]
        handlers.sendEmptyMessageDelayed(0x00,2000)

    }


    //显示隐私弹窗
    private fun showPrivacyDialog() {
        val dialog =
            ShowPrivacyDialogView(this, com.bonlala.base.R.style.BaseDialogTheme, this@LaunchActivity)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setOnPrivacyClickListener(object : ShowPrivacyDialogView.OnPrivacyClickListener {
            override fun onCancelClick() {
                dialog.dismiss()
                MmkvUtils.setIsAgreePrivacy(false)
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                ActivityManager.getInstance().finishAllActivities()
                finish()
            }

            override fun onConfirmClick() {
                dialog.dismiss()
                MmkvUtils.setIsAgreePrivacy(true)
                startActivity(CarHomeActivity::class.java)
                finish()
            }

        })
    }

}