package com.app.airmaster.car

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.hjq.shape.view.ShapeEditText
import com.hjq.shape.view.ShapeTextView

/**
 * Created by Admin
 *Date 2023/7/14
 */
class CarAboutActivity :AppActivity() {

    private var appVersionTv : TextView ?= null
    private var aboutUpgradeContentLayout : LinearLayout ?= null
    private var aboutActivateEdit : ShapeEditText ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_app_about_layout
    }

    override fun initView() {
        aboutActivateEdit = findViewById(R.id.aboutActivateEdit)
        appVersionTv = findViewById(R.id.appVersionTv)
        aboutUpgradeContentLayout = findViewById(R.id.aboutUpgradeContentLayout)
        //升级
        findViewById<ConstraintLayout>(R.id.aboutUpgradeLayout).setOnClickListener {

        }

        //激活设备
        findViewById<ConstraintLayout>(R.id.aboutActivateLayout).setOnClickListener {

        }

        //提交激活码
        findViewById<ShapeTextView>(R.id.aboutActivateSubmitTv).setOnClickListener {

        }

        val buildS = SpannableStringBuilder("请联系经销商获取激活码")
        buildS.setSpan(ForegroundColorSpan(Color.parseColor("#4A4A4B")), 0, buildS.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        aboutActivateEdit?.hint = buildS
    }

    override fun initData() {
        showAppVersion()
    }


    private fun showAppVersion() {
        try {
            val packInfo = packageManager.getPackageInfo(packageName,0)
            val versionName = packInfo.versionName
            appVersionTv?.text = versionName
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}