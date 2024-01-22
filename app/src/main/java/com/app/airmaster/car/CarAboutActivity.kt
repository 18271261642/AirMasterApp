package com.app.airmaster.car

import android.graphics.Color
import android.provider.ContactsContract.CommonDataKinds.Im
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.dialog.SingleAlertDialog
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.viewmodel.VersionViewModel
import com.blala.blalable.Utils
import com.hjq.shape.view.ShapeEditText
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import timber.log.Timber

/**
 * Created by Admin
 *Date 2023/7/14
 */
class CarAboutActivity :AppActivity() {

    private var viewModel : VersionViewModel ?= null


    private var aboutActivateDeviceImageView : ImageView ?= null

    private var aboutUpgradeImageView : ImageView ?= null

    private var touchPadVersionTv : TextView ?= null

    private var appVersionTv : TextView ?= null
    private var aboutUpgradeContentLayout : LinearLayout ?= null
    private var aboutActivateEdit : ShapeEditText ?= null

    private var activateContentLayout : LinearLayout ?= null

    //是否是折叠状态
    private var isUpgradeFold = false
    private var isActivateFold = false

    override fun getLayoutId(): Int {
        return R.layout.activity_app_about_layout
    }

    override fun initView() {
        activateContentLayout = findViewById(R.id.activateContentLayout)
        aboutActivateDeviceImageView = findViewById(R.id.aboutActivateDeviceImageView)
        aboutUpgradeImageView = findViewById(R.id.aboutUpgradeImageView)
        touchPadVersionTv = findViewById(R.id.touchPadVersionTv)
        aboutActivateEdit = findViewById(R.id.aboutActivateEdit)
        appVersionTv = findViewById(R.id.appVersionTv)
        aboutUpgradeContentLayout = findViewById(R.id.aboutUpgradeContentLayout)


        isUpgradeFoldState(true)
        isAcFoldState(true)

        //升级
        findViewById<ConstraintLayout>(R.id.aboutUpgradeLayout).setOnClickListener {
            isUpgradeFoldState(isUpgradeFold)
        }

        //激活设备
        findViewById<ConstraintLayout>(R.id.aboutActivateLayout).setOnClickListener {
            isAcFoldState(isActivateFold)
        }

        //提交激活码
        findViewById<ShapeTextView>(R.id.aboutActivateSubmitTv).setOnClickListener {
            val activateCode = aboutActivateEdit?.text.toString()
            if(BikeUtils.isEmpty(activateCode)){
                ToastUtils.show("请输入激活码!")
                return@setOnClickListener
            }
            if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
                showNotConnDialog()
                return@setOnClickListener
            }
            viewModel?.setDeviceIdentificationCode(activateCode)
        }

        val buildS = SpannableStringBuilder("请联系经销商获取激活码")
        buildS.setSpan(ForegroundColorSpan(Color.parseColor("#4A4A4B")), 0, buildS.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        aboutActivateEdit?.hint = buildS
    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[VersionViewModel::class.java]
        showAppVersion()

        val bt = ByteArray(3)
        bt[0] = 0x01
        bt[1] = 0x02
        bt[2] = 0x00
        val otaByte = Utils.getFullPackage(bt)

        Timber.e("--------otaByte="+Utils.formatBtArrayToString(otaByte))


        //固件信息
        viewModel?.serverVersionInfo?.observe(this){

        }

        //激活状态
        viewModel?.activateState?.observe(this){
            showActivateDialog(it)
            if(it == true){
                val acCode = aboutActivateEdit?.text.toString()
                viewModel?.saveActivateRecord(this@CarAboutActivity, acCode,"sn123456","userName","18888888888")
            }
        }


        viewModel?.deviceVersionInfo?.observe(this){ it ->
            touchPadVersionTv?.text =it.versionStr

            viewModel?.getDeviceInfoData(this@CarAboutActivity,it.binCode,it.versionStr)
        }


        //获取固件版本信息
        viewModel?.getDeviceVersion()
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


    private fun showActivateDialog(success : Boolean){
        val dialog = SingleAlertDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt(if(success) "激活成功!" else "激活失败,请输入正确的激活码!")

    }

    private fun showNotConnDialog(){
        showCommAlertDialog("未连接设备","去官网","去连接"
        ) { position ->
            disCommAlertDialog()
            if (position == 0x01) {
                startActivity(SecondScanActivity::class.java)
            }
        }
    }


    //是否是折叠装填
    private fun isUpgradeFoldState(fold : Boolean){
        isUpgradeFold = !fold
        aboutUpgradeContentLayout?.visibility = if(fold) View.GONE else View.VISIBLE
        rotateU(fold)
    }


    private fun isAcFoldState(fold : Boolean){
        isActivateFold = !fold
        activateContentLayout?.visibility = if(fold) View.GONE else View.VISIBLE
        rotateA(fold)
    }

    private fun rotateU(fold : Boolean){
        val rotate = RotateAnimation(0F,if(fold)0F else 90F,Animation.RELATIVE_TO_SELF,0.5F,RotateAnimation.RELATIVE_TO_SELF,0.5F)
        rotate.duration = 10
        rotate.fillAfter = true
        rotate.repeatCount = 0
        aboutUpgradeImageView?.startAnimation(rotate)
    }


    private fun rotateA(fold : Boolean){
        val rotate = RotateAnimation(0F,if(fold)0F else 90F,Animation.RELATIVE_TO_SELF,0.5F,RotateAnimation.RELATIVE_TO_SELF,0.5F)
        rotate.duration = 10
        rotate.fillAfter = true
        rotate.repeatCount = 0
        aboutActivateDeviceImageView?.startAnimation(rotate)
    }
}