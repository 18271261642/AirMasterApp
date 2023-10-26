package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

class ConfirmDialog : AppCompatDialog{


    private var confirmCancelTv : ShapeTextView ?= null
    private var confirmConfirmTv : ShapeTextView ?= null
    private var confirmContentTv : TextView ?= null


    //item点击
    private var onItemClickListener : OnCommItemClickListener?= null

    fun setOnCommClickListener(onclick : OnCommItemClickListener){
        this.onItemClickListener = onclick
    }


    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm_layout)


        confirmContentTv = findViewById(R.id.confirmContentTv)
        confirmCancelTv = findViewById(R.id.confirmCancelTv)
        confirmConfirmTv = findViewById(R.id.confirmConfirmTv)


        confirmCancelTv?.setOnClickListener {
            onItemClickListener?.onItemClick(0x00)
        }

        confirmConfirmTv?.setOnClickListener {
            onItemClickListener?.onItemClick(0x01)
        }
    }

    fun setContentTxt(txt : String){
        confirmContentTv?.text = txt
    }


    fun setBtnTxt(confirm : String,cancel : String){
        confirmCancelTv?.text = cancel
        confirmConfirmTv?.text = confirm
    }

}