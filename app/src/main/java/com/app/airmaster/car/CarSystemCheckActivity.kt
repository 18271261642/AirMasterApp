package com.app.airmaster.car

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.viewmodel.CarCheckViewModel
import com.bonlala.widget.layout.SettingBar

/**
 * 系统自检
 * Created by Admin
 *Date 2023/7/14
 */
class CarSystemCheckActivity : AppActivity() {


    private var viewModel : CarCheckViewModel?= null

    override fun getLayoutId(): Int {
      return R.layout.activity_car_system_check_layout
    }

    override fun initView() {

        //自动检测
        findViewById<SettingBar>(R.id.sysAutoCheckBar).setOnClickListener {
            showDialogShow(1)
        }

        //手动检测
        findViewById<SettingBar>(R.id.sysManualCheckBar).setOnClickListener {
            showDialogShow(0)
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[CarCheckViewModel::class.java]
    }


    private fun showDialogShow(code : Int){
        val dialog = ConfirmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt(if(code == 0) "是否进行手动检测?" else "是否进行自动检测?")
        dialog.setOnCommClickListener(object : OnCommItemClickListener{
            override fun onItemClick(position: Int) {
                dialog.dismiss()

                if(position==1){
                    if(code == 1){
                        val intent = Intent(this@CarSystemCheckActivity,CarAutoCheckActivity::class.java)
                        intent.putExtra("type",code)
                        startActivity(intent)
                    }else{
                        viewModel?.intoOrExit(true,false)
                    }

                }
            }

        })
    }
}