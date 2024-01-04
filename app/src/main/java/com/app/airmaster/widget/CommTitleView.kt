package com.app.airmaster.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener

class CommTitleView : LinearLayout {


    private var onClick : OnCommItemClickListener?= null

    fun setOnItemClick(click : OnCommItemClickListener){
        this.onClick = click
    }

    private var commLeftImgView : ImageView ?= null
    private var commTitleCenterTv : TextView ?= null



    constructor(context: Context) : super (context){
        initViews(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super (context,attributeSet){
        initViews(context)
    }


    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr : Int) : super (context,attributeSet,defStyleAttr){
        initViews(context)
    }


    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.comm_title_layout,this,true)
        commLeftImgView = view.findViewById(R.id.commLeftImgView)
        commTitleCenterTv = view.findViewById(R.id.commTitleCenterTv)

        commLeftImgView?.setOnClickListener {
            onClick?.onItemClick(0x00)
        }
    }


    fun setCommTitleTxt(txt : String){
        commTitleCenterTv?.text = txt
    }
}