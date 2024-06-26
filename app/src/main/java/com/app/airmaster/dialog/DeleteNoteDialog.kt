package com.app.airmaster.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

/**
 * 删除笔记的dialog
 */
class DeleteNoteDialog : AppCompatDialog,OnClickListener{



    //item点击
    private var onItemClickListener : OnCommItemClickListener?= null

    fun setOnCommClickListener(onclick : OnCommItemClickListener){
        this.onItemClickListener = onclick
    }


    //取消
    private var dialogDeleteCancelTv : ShapeTextView ?= null
    //确定
    private var dialogDeleteConfirmTv : ShapeTextView ?= null

    private var detelteContentTv : TextView ?= null




    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_note_layout)

        detelteContentTv = findViewById(R.id.detelteContentTv)
        dialogDeleteCancelTv = findViewById(R.id.dialogDeleteCancelTv)
        dialogDeleteConfirmTv = findViewById(R.id.dialogDeleteConfirmTv)


        dialogDeleteCancelTv?.setOnClickListener(this)
        dialogDeleteConfirmTv?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
       val id = v?.id
        //取消
        if(id == R.id.dialogDeleteCancelTv){
            onItemClickListener?.onItemClick(0x00)
        }
        //确定
        if(id == R.id.dialogDeleteConfirmTv){
            onItemClickListener?.onItemClick(0x01)
        }
    }


    fun setContent(content : String){
        detelteContentTv?.text = content
    }
}