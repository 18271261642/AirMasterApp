package com.app.airmaster.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.app.airmaster.R

class HomeTxtStyleView : LinearLayout {

    private var homeTxtStyleTv : TextView ?= null

    constructor(context: Context) : super (context){

    }

    constructor(context: Context, attributeSet: AttributeSet) : super (context,attributeSet){
        initViews(context)
    }

    constructor(context: Context, attributeSet: AttributeSet, defaultValue : Int) : super (context,attributeSet,defaultValue){
        initViews(context)
    }

    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.view_txt_style_layout,this,true)
        homeTxtStyleTv = view.findViewById(R.id.homeTxtStyleTv)


    }


    fun setTxtValue(txt : String){
        homeTxtStyleTv?.text = txt
    }
}