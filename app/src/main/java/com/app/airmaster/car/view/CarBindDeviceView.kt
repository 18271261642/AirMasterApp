package com.app.airmaster.car.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.app.airmaster.R

class CarBindDeviceView : LinearLayout {


    private   var itemScanName : TextView ?= null
    private var itemDeviceMacTv : TextView ?= null
    private var itemScanCheckImageView : ImageView ?= null

    constructor(context: Context) : super (context){
        initViews(context)
    }


    constructor(context: Context, attribute : AttributeSet) : super (context,attribute){
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super (context, attrs, defStyleAttr){
        initViews(context)
    }

    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_second_scan_layout,this,true)

       itemScanName  = view.findViewById(R.id.itemSecondNameTv)
       itemDeviceMacTv = view.findViewById(R.id.itemDeviceMacTv)
        itemScanCheckImageView = view.findViewById(R.id.itemScanCheckImageView)
    }


    fun setNameAndMac(name : String,mac : String){
        itemScanName?.text = name
        itemDeviceMacTv?.text = mac
    }


    fun setImgStatus(conn : Boolean){
        itemScanCheckImageView?.setImageResource(if(conn)R.mipmap.ic_scan_conn_checked else R.mipmap.ic_scan_conn_no_checked)
    }

    fun getMac() : String{
        return itemDeviceMacTv?.text.toString()
    }
}