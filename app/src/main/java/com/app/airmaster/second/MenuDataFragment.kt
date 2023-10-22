package com.app.airmaster.second

import com.app.airmaster.R
import com.app.airmaster.action.TitleBarFragment
import com.hjq.shape.layout.ShapeLinearLayout

/**
 * 数据页面
 */
class MenuDataFragment : TitleBarFragment<SecondHomeActivity>()
{

    private var dataAddLayout : ShapeLinearLayout ?= null

    companion object{

        fun getInstance() : MenuDataFragment{
            return MenuDataFragment()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_menu_data_layout
    }

    override fun initView() {
        dataAddLayout = findViewById(R.id.dataAddLayout)

        dataAddLayout?.setOnClickListener { startActivity(SecondScanActivity::class.java) }
    }

    override fun initData() {

    }

}