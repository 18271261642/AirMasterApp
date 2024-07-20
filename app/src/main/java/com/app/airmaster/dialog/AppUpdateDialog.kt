package com.app.airmaster.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.app.airmaster.R
import com.app.airmaster.adapter.OnCommItemClickListener
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

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

    private var appUpdateDialogTmpView : View ?= null


    private var appUpdateStateTv : TextView ?= null


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
        appUpdateStateTv = findViewById(R.id.appUpdateStateTv)
        appUpdateDialogTmpView = findViewById(R.id.appUpdateDialogTmpView)
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

    fun setIsFocusUpdate(focus : Boolean){
        appUpdateDialogTmpView?.visibility = if(focus) View.GONE else View.VISIBLE
        appUpdateDialogCancelTv?.visibility = if(focus) View.GONE else View.VISIBLE
    }

    fun setTitleTxt(txt : String){
        appUpdateDialogTitleTv?.text = txt
    }

    fun setContent(content : String){
        appUpdateContentTv?.text = "更新内容:\n"+content
    }

    //开始下载
    fun startDownload(lifecycleOwner: LifecycleOwner,url : String,fileName : String){
        showDowning()
        val saveUrl = context.getExternalFilesDir(null)?.path+"/App/"+fileName

       // val u = "http://imtt2.dd.qq.com/sjy.00009/sjy.00004/16891/apk/6C8C3C8CCB60BD085D41159877CCEE33.apk?fsname=com.tencent.mm_8.0.49.apk"

        EasyHttp.download(lifecycleOwner).url(url)
            .file(saveUrl)
            .listener(object : OnDownloadListener{
                override fun onStart(file: File?) {

                }

                override fun onProgress(file: File?, progress: Int) {
                    appUpdateStateTv?.text = context.resources.getString(R.string.string_downloading)
                }

                override fun onComplete(file: File?) {
                    dismiss()
                    //下载完成
                    GlobalScope.launch {
                        delay(1000)
                        installApk(file!!.path)
                    }
                }

                override fun onError(file: File?, e: Exception?) {
                    dismiss()
                    ToastUtils.show(e?.message)
                }

                override fun onEnd(file: File?) {

                }

            }).start()
    }


    //安装
    private fun installApk(file : String){
        val file: File = File(file)
        if (!file.isFile) return
        context.startActivity(getInstallIntent(file))
    }

    /**
     * 获取安装意图
     */
    private fun getInstallIntent(file : File): Intent {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(file)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}