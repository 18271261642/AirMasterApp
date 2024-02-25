package com.app.airmaster.car

import android.os.Build
import android.view.View
import android.view.View.OnLongClickListener
import android.webkit.WebSettings
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.github.lzyzsd.jsbridge.BridgeWebView

class ShowWebActivity : AppActivity() {


    private var commWebView : BridgeWebView ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_show_web_layout
    }

    override fun initView() {
        commWebView = findViewById(R.id.commWebView)

        initWebSetting()

        commWebView?.setOnLongClickListener { true }
    }

    override fun initData() {
        val url = "http://www.airmaster-performance.com"
        commWebView?.loadUrl(url)
    }


    private fun initWebSetting() {
        val settings: WebSettings = commWebView!!.getSettings()
        // 允许文件访问
        settings.allowFileAccess = true
        // 允许网页定位
        settings.setGeolocationEnabled(true)
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        settings.javaScriptEnabled = true
        // 允许网页弹对话框
        settings.javaScriptCanOpenWindowsAutomatically = true
        // 加快网页加载完成的速度，等页面完成再加载图片
        settings.loadsImagesAutomatically = true
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        commWebView?.setOnLongClickListener(View.OnLongClickListener {

            true
        })
    }


    override fun onBackPressed() {

        if (commWebView?.canGoBack() == true) {
            commWebView?.goBack()
            return
        }
        super.onBackPressed()
    }
}