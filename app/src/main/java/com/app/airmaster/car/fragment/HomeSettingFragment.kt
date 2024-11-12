package com.app.airmaster.car.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
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
import com.app.airmaster.viewmodel.ControlViewModel
import com.bonlala.widget.layout.SettingBar

/**
 * Created by Admin
 *Date 2023/7/14
 */
class HomeSettingFragment : TitleBarFragment<CarHomeActivity>() {

    private var viewModel : ControlViewModel?= null


    //是否是气压记忆
    private var isAirPressureModel = false

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
            val autoBean = BaseApplication.getBaseApplication().autoBackBean
            if(autoBean ==null){
                startActivity(CarSystemCheckActivity::class.java)
                return@setOnClickListener
            }
            if(autoBean.deviceMode == 1){   //气压模式
                showDialogShow(1)
            }else{
                if(isAirPressureModel){
                    showDialogShow(1)
                    return@setOnClickListener
                }
                startActivity(CarSystemCheckActivity::class.java)
            }

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
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }

            //高度记忆或压力记忆 0高度记忆；1压力记忆
            val model = it.modelType

            if(model == 0){ //高度记忆
                isAirPressureModel = false
            }

            if(model == 1){  //压力记忆
                isAirPressureModel = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel?.writeCommonFunction()
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
        dialog.setContentTxt(if(code == 0) resources.getString(R.string.string_manual_check_prompt) else resources.getString(R.string.string_car_auto_check_prompt))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()

            if (position == 1) {
                if (code == 1) {
                    val intent = Intent(attachActivity, CarAutoCheckActivity::class.java)
                    intent.putExtra("type", code)
                    startActivity(intent)
                } else {
                    // viewModel?.intoOrExit(true,false)
                    val intent = Intent(attachActivity, ManualCheckActivity::class.java)
                    intent.putExtra("type", code)
                    startActivity(intent)
                }

            }
        }
    }
}