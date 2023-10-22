package com.app.airmaster.car.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.LinearLayout
import com.app.airmaster.R
import com.app.airmaster.adapter.OnItemCheckedListener

/**
 * 首页，底部的选项
 */
class HomeBottomCheckView : LinearLayout,OnCheckedChangeListener{


    private var onCheckListener : OnItemCheckedListener?= null

    fun setOnItemCheck(c : OnItemCheckedListener){
        this.onCheckListener = c
    }

    //打气
    private var homeBottomEncourageCheckBox : CheckBox ?= null
    //预设高度
    private var homeBottomPrepareHeightCheckBox : CheckBox ?= null
    //报警
    private var itemHomeBottomWarringCheckBox : CheckBox ?= null
    //排水
    private var itemHomeBottomDrainageCheckBox : CheckBox ?= null



    constructor(context: Context) : super (context){
        initViews(context)
    }


    constructor(context: Context, attribute : AttributeSet) : super (context,attribute){
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super (context, attrs, defStyleAttr){
        initViews(context)
    }



    @SuppressLint("MissingInflatedId")
    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.view_home_bottom_layout,this,true)
        homeBottomEncourageCheckBox = view.findViewById(R.id.homeBottomEncourageCheckBox)
        homeBottomPrepareHeightCheckBox = view.findViewById(R.id.homeBottomPrepareHeightCheckBox)
        itemHomeBottomWarringCheckBox = view.findViewById(R.id.itemHomeBottomWarringCheckBox)
        itemHomeBottomDrainageCheckBox = view.findViewById(R.id.itemHomeBottomDrainageCheckBox)

        homeBottomEncourageCheckBox?.setOnCheckedChangeListener(this)
        homeBottomPrepareHeightCheckBox?.setOnCheckedChangeListener(this)
        itemHomeBottomWarringCheckBox?.setOnCheckedChangeListener(this)
        itemHomeBottomDrainageCheckBox?.setOnCheckedChangeListener(this)


    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        setAllNoCheck()
        if(!buttonView.isPressed)
            return

        if(buttonView.id == R.id.homeBottomEncourageCheckBox){
            homeBottomEncourageCheckBox?.isChecked = isChecked
            onCheckListener?.onItemCheck(0,isChecked)
        }
        if(buttonView.id == R.id.homeBottomPrepareHeightCheckBox){
            homeBottomPrepareHeightCheckBox?.isChecked = isChecked
            onCheckListener?.onItemCheck(1,isChecked)
        }

        if(buttonView.id == R.id.itemHomeBottomWarringCheckBox){
            itemHomeBottomWarringCheckBox?.isChecked = isChecked
            onCheckListener?.onItemCheck(2,isChecked)
        }
        if(buttonView.id == R.id.itemHomeBottomDrainageCheckBox){
            itemHomeBottomDrainageCheckBox?.isChecked = isChecked
            onCheckListener?.onItemCheck(3,isChecked)
        }
    }


    //设置选中的
    private fun showCheckView(index : Int){
        setAllNoCheck()
        homeBottomEncourageCheckBox?.isChecked = index == 0
        homeBottomPrepareHeightCheckBox?.isChecked = index == 1
        itemHomeBottomWarringCheckBox?.isChecked = index == 2
        itemHomeBottomDrainageCheckBox?.isChecked = index == 3
    }

    //全部未选中
    private fun setAllNoCheck(){
        homeBottomEncourageCheckBox?.isChecked = false
        homeBottomPrepareHeightCheckBox?.isChecked = false
        itemHomeBottomWarringCheckBox?.isChecked = false
        itemHomeBottomDrainageCheckBox?.isChecked = false
    }
}