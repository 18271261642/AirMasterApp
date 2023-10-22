package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

class ConfirmDialog : AppCompatDialog{


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


        findViewById<ShapeTextView>(R.id.confirmCancelTv)?.setOnClickListener {
            onItemClickListener?.onItemClick(0x00)
        }

        findViewById<ShapeTextView>(R.id.confirmConfirmTv)?.setOnClickListener {
            onItemClickListener?.onItemClick(0x01)
        }
    }

}