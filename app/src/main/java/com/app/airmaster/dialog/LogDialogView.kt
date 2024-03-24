package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R

class LogDialogView : AppCompatDialog {


    private var logTv : TextView ?= null


    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_log_layout)

        logTv = findViewById(R.id.logDialogTv)

    }


    fun setLogTxt(txt : String){
        logTv?.text = txt
    }
}