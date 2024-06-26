package com.app.airmaster.car

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.viewmodel.ControlViewModel
import com.bonlala.widget.view.SwitchButton
import com.hjq.shape.layout.ShapeConstraintLayout
import timber.log.Timber

/**
 * 高度记忆LOW设置
 */
class HeightMemoryLowSetActivity : AppActivity(){

    private var viewModel : ControlViewModel?= null

    private var memoryLowOnSwitch : CheckBox ?= null
    private var memoryLowCusLayout : ShapeConstraintLayout ?= null
    private var memoryLowRightImg : ImageView ?= null

    private var isEnable : Boolean = false


    override fun getLayoutId(): Int {
        return R.layout.activity_height_memory_low_layout
    }

    override fun initView() {
        memoryLowOnSwitch = findViewById(R.id.memoryLowOnSwitch)
        memoryLowCusLayout = findViewById(R.id.memoryLowCusLayout)
        memoryLowRightImg = findViewById(R.id.memoryLowRightImg)

        memoryLowCusLayout?.setOnClickListener {

            if(isEnable){
                return@setOnClickListener
            }
            val intent = Intent(this@HeightMemoryLowSetActivity,HeightMemorySetActivity::class.java)
            intent.putExtra("index",5)
            startActivity(intent)
        }

        memoryLowOnSwitch?.setOnCheckedChangeListener { button, checked ->
            Timber.e("---------状态="+button.isPressed)
            if(!button.isPressed){
                return@setOnCheckedChangeListener
            }
            setCusStatus(checked)
            viewModel?.setAirOutData(checked)

        }
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[ControlViewModel::class.java]

        viewModel?.autoSetBeanData?.observe(this){
            if(it == null){
                return@observe
            }
            val model = it.lowestPosition
            Timber.e("----------AIROUT="+it.lowestPosition)
            memoryLowOnSwitch?.isChecked = model ==0
            setCusStatus(model == 0)
        }

        viewModel?.writeCommonFunction()
        //memoryLowOnSwitch?.isChecked = true
    }


    //是否可以进入自定义
    private fun setCusStatus(enable : Boolean){
        isEnable = enable
        memoryLowRightImg?.visibility = if(enable) View.GONE else View.VISIBLE
    }

}