package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

class SingleAlertDialog : AppCompatDialog {


    private var onClick : OnCommItemClickListener?= null

    fun setOnDialogClickListener(onCommItemClickListener: OnCommItemClickListener){
        this.onClick = onCommItemClickListener
    }


    private var singleContentTv : TextView ?= null

    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_single_alert_layout)

        singleContentTv = findViewById(R.id.singleContentTv)

        findViewById<ShapeTextView>(R.id.singleSubmitTv)?.setOnClickListener {
            dismiss()
            onClick?.onItemClick(0x00)

        }
    }

    fun setContentTxt(txt : String){
        singleContentTv?.text = txt
    }
}