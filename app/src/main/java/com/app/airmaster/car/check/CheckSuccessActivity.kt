package com.app.airmaster.car.check

import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.viewmodel.CarCheckViewModel
import com.hjq.shape.view.ShapeTextView

class CheckSuccessActivity : AppActivity() {
    private var viewModel : CarCheckViewModel?= null

    private var isAuto = false

    override fun getLayoutId(): Int {
       return R.layout.activity_check_success_layout
    }

    override fun initView() {
        findViewById<ShapeTextView>(R.id.checkSuccessBtn).setOnClickListener {
            //开始自检
            viewModel?.intoOrExit(false,isAuto)
            finish()
        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[CarCheckViewModel::class.java]
        isAuto = intent.getBooleanExtra("isAuto",false)
    }


}