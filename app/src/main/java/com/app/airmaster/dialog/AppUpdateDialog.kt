package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

class AppUpdateDialog : AppCompatDialog {


    private var onClick : OnCommItemClickListener?= null

    fun setOnDialogClickListener(onCommItemClickListener: OnCommItemClickListener){
        this.onClick = onCommItemClickListener
    }


    private var appUpdateDialogTitleTv : TextView ?= null
    private var appUpdateContentTv : TextView ?= null
    private var appUpdateDialogCancelTv : ShapeTextView ?= null
    private var appUpdateDialogConfirmTv : ShapeTextView ?= null
    private var appUpdateIngLayout : LinearLayout ?= null
    private var appUpdateBtnLayout : LinearLayout ?= null



    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_update_dialog_layout)

        initViews()

        appUpdateDialogCancelTv?.setOnClickListener {
            onClick?.onItemClick(0x00)
        }

        appUpdateDialogConfirmTv?.setOnClickListener {
            onClick?.onItemClick(0x01)
        }

    }

    private fun initViews(){
        appUpdateBtnLayout = findViewById(R.id.appUpdateBtnLayout)
        appUpdateIngLayout = findViewById(R.id.appUpdateIngLayout)
        appUpdateDialogTitleTv = findViewById(R.id.appUpdateDialogTitleTv)
        appUpdateContentTv = findViewById(R.id.appUpdateContentTv)
        appUpdateDialogCancelTv = findViewById(R.id.appUpdateDialogCancelTv)
        appUpdateDialogConfirmTv = findViewById(R.id.appUpdateDialogConfirmTv)

    }


    private fun showDowning(){
        appUpdateBtnLayout?.visibility = View.GONE
        appUpdateIngLayout?.visibility = View.VISIBLE
    }


    fun startDowning(url : String){

    }


    fun setTitleTxt(txt : String){
        appUpdateDialogTitleTv?.text = txt
    }

    fun setContent(content : String){
        appUpdateContentTv?.text = content
    }
}