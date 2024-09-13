package com.app.airmaster.car.fragment

import android.content.Intent
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.ShowWebViewActivity
import com.app.airmaster.action.TitleBarFragment
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.CarAboutActivity
import com.app.airmaster.car.CarAutoCheckActivity
import com.app.airmaster.car.CarHomeActivity
import com.app.airmaster.car.CarSysSetActivity
import com.app.airmaster.car.CarSystemCheckActivity
import com.app.airmaster.car.ShowWebActivity
import com.app.airmaster.car.check.ManualCheckActivity
import com.app.airmaster.dialog.ConfirmDialog
import com.app.airmaster.second.SecondScanActivity
import com.bonlala.widget.layout.SettingBar

/**
 * Created by Admin
 *Date 2023/7/14
 */
class HomeSettingFragment : TitleBarFragment<CarHomeActivity>() {


    companion object{
        fun getInstance() : HomeSettingFragment{
            return HomeSettingFragment()
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_home_setting_layout
    }

    override fun initView() {
        findViewById<SettingBar>(R.id.sysConnDeviceBar).setOnClickListener {
            startActivity(SecondScanActivity::class.java)
        }

        findViewById<SettingBar>(R.id.sysPrivacyBar).setOnClickListener {

            val intent = Intent(context, ShowWebViewActivity::class.java)
            intent.putExtra("url", "file:///android_asset/airmaster_privacy.html")
            intent.putExtra("title", resources.getString(R.string.privacy_agreement_tips))
            startActivity(intent)
        }

        findViewById<SettingBar>(R.id.sysProtocolBar).setOnClickListener {

//                Intent intent = new Intent(getContext(), ShowWebActivity.class);
//                intent.putExtra("url", MmkvUtils.getUserAgreement());
//                intent.putExtra("title",getContext().getResources().getString(R.string.user_agreement_tips));
//                getContext().startActivity(intent);
            //                intent.putExtra("url", "file:///android_asset/keyboard_privacy.html");
            // String url = "http://www.airmaster-performance.com";
            val intent = Intent(context, ShowWebViewActivity::class.java)
            intent.putExtra("url", "file:///android_asset/airmaster_protocol.html")
            intent.putExtra("title", resources.getString(R.string.user_agreement_tips))
            //  intent.putExtra("url", url);
            //  intent.putExtra("url", url);
            startActivity(intent)
        }

        findViewById<SettingBar>(R.id.sysSysBar).setOnClickListener {
            if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                showNotConnDialog()
                return@setOnClickListener
            }
            startActivity(CarSysSetActivity::class.java)
        }
        findViewById<SettingBar>(R.id.sysCheckBar).setOnClickListener {
            if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                showNotConnDialog()
                return@setOnClickListener
            }
           // startActivity(CarSystemCheckActivity::class.java)
            showDialogShow(1)
        }
        findViewById<SettingBar>(R.id.sysAboutBar).setOnClickListener {
//            if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
//                showNotConnDialog()
//                return@setOnClickListener
//            }
            startActivity(CarAboutActivity::class.java)
        }
    }

    override fun initData() {

    }

    private fun showNotConnDialog(){
        attachActivity?.showCommAlertDialog(resources.getString(R.string.string_not_conn_device),resources.getString(R.string.string_go_to_official_website),resources.getString(R.string.string_go_to_conn)
        ) { position ->
            attachActivity.disCommAlertDialog()
            if (position == 0x01) {
                startActivity(SecondScanActivity::class.java)
            }
            if (position == 0x00) {
                startActivity(ShowWebActivity::class.java)
            }
        }
    }



    private fun showDialogShow(code : Int){
        val dialog = ConfirmDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt(if(code == 0) "是否进行手动检测?" else resources.getString(R.string.string_car_auto_check_prompt))
        dialog.setOnCommClickListener(object : OnCommItemClickListener {
            override fun onItemClick(position: Int) {
                dialog.dismiss()

                if(position==1){
                    if(code == 1){
                        val intent = Intent(attachActivity, CarAutoCheckActivity::class.java)
                        intent.putExtra("type",code)
                        startActivity(intent)
                    }else{
                        // viewModel?.intoOrExit(true,false)
                        val intent = Intent(attachActivity, ManualCheckActivity::class.java)
                        intent.putExtra("type",code)
                        startActivity(intent)
                    }

                }
            }

        })
    }
}