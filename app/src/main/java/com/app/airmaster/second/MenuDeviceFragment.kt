package com.app.airmaster.second

import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.hjq.shape.view.ShapeTextView

/**
 * 设备页面
 */
class MenuDeviceFragment : TitleBarFragment<SecondHomeActivity>(){


    companion object{

        fun getInstance() : MenuDeviceFragment{
            return MenuDeviceFragment()
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_menu_device_layout
    }

    override fun initView() {

        findViewById<ShapeTextView>(R.id.deviceNotifyTv).setOnClickListener {
            startActivity(NotifyOpenActivity::class.java)
        }
    }

    override fun initData() {

    }
}