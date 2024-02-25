package com.app.airmaster.car

import android.Manifest
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
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.app.airmaster.car.bean.ServerVersionInfoBean
import com.app.airmaster.dialog.DfuDialogView
import com.app.airmaster.dialog.SingleAlertDialog
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.viewmodel.DfuViewModel
import com.app.airmaster.viewmodel.VersionViewModel
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.http.listener.OnDownloadListener
import com.hjq.permissions.XXPermissions
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hjq.shape.view.ShapeEditText
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

/**
 * Created by Admin
 *Date 2023/7/14
 */
class CarAboutActivity :AppActivity() {

    private var viewModel : VersionViewModel ?= null
    private var dfuViewModel : DfuViewModel ?= null

    private var carAboutTitleBar : TitleBar ?= null


    private var aboutDfuShowTv : ShapeTextView ?= null

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

    //芯片序列号
    private var cdKeyCode : String ?= null

    //是否正在升级中
    private var isUpgrading = false

    private var tempDeviceVersionInfo : DeviceBinVersionBean ?= null

    private var tempServerBean : ServerVersionInfoBean.FirmwareListDTO ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_app_about_layout
    }

    override fun initView() {
        carAboutTitleBar = findViewById(R.id.carAboutTitleBar)
        aboutDfuShowTv = findViewById(R.id.aboutDfuShowTv)
        activateContentLayout = findViewById(R.id.activateContentLayout)
        aboutActivateDeviceImageView = findViewById(R.id.aboutActivateDeviceImageView)
        aboutUpgradeImageView = findViewById(R.id.aboutUpgradeImageView)
        touchPadVersionTv = findViewById(R.id.touchPadVersionTv)
        aboutActivateEdit = findViewById(R.id.aboutActivateEdit)
        appVersionTv = findViewById(R.id.appVersionTv)
        aboutUpgradeContentLayout = findViewById(R.id.aboutUpgradeContentLayout)


        findViewById<ShapeConstraintLayout>(R.id.aboutTouchLayout).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            initData()
            if(tempDeviceVersionInfo != null){
                showDfuDialog(tempServerBean!!)
            }

        }

        getPermission()

        isUpgradeFoldState(true)
        isAcFoldState(true)

        //升级
        findViewById<ConstraintLayout>(R.id.aboutUpgradeLayout).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
            isUpgradeFoldState(isUpgradeFold)
        }

        //激活设备
        findViewById<ConstraintLayout>(R.id.aboutActivateLayout).setOnClickListener {
            if(ClickUtils.isFastDoubleClick()){
                return@setOnClickListener
            }
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

        carAboutTitleBar?.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(view: View?) {
                if(isUpgrading){
                    ToastUtils.show("正在升级中，请务退出！")
                    return
                }
                finish()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {

            }

        }
        )
    }



    override fun initData() {
        dfuViewModel = ViewModelProvider(this)[DfuViewModel::class.java]
        viewModel = ViewModelProvider(this)[VersionViewModel::class.java]

        dfuViewModel?.registerDfu(this)
        showAppVersion()

        //ota升级
        dfuViewModel?.dfuProgressData?.observe(this){
            aboutDfuShowTv?.text = "正在升级: $it%"
        }
        dfuViewModel?.dfuUpgradeStatus?.observe(this){
            BaseApplication.getBaseApplication().isOTAModel = false
            dfuDialog?.dismiss()
            isUpgrading = false
            ToastUtils.show(if(it) "升级成功,请重新连接使用!" else "升级失败,请重新升级!")
            if(it){
                startActivity(SecondScanActivity::class.java)
                finish()
            }
        }



        //固件信息
        viewModel?.serverVersionInfo?.observe(this){
            if(it == null){
                return@observe
            }
            if(tempDeviceVersionInfo == null){
                return@observe
            }
           val firmList = it.firmwareList
            var infoBean : ServerVersionInfoBean.FirmwareListDTO ?= null
            firmList.forEachIndexed { index, firmwareListDTO ->
                if(firmwareListDTO.identificationCode == tempDeviceVersionInfo!!.identificationCode){
                    infoBean = firmwareListDTO
                }
            }
            if(infoBean == null){
                return@observe
            }
            this.tempServerBean = infoBean
            if(infoBean!!.versionCode != tempDeviceVersionInfo!!.versionCode){
                aboutDfuShowTv?.visibility = View.VISIBLE
                showDfuStatus(false)
            }else{
                touchPadVersionTv?.text = tempDeviceVersionInfo?.versionStr
                aboutDfuShowTv?.visibility = View.GONE
            }

        }

        //激活状态
        viewModel?.activateState?.observe(this){
            showActivateDialog(it)
            if(it == true){
                val acCode = aboutActivateEdit?.text.toString()
                cdKeyCode?.let { it1 ->
                    viewModel?.saveActivateRecord(this@CarAboutActivity, acCode,
                        it1)
                }
            }
        }


        viewModel?.deviceVersionInfo?.observe(this){ it ->
            touchPadVersionTv?.text =it.versionStr
            aboutDfuShowTv?.visibility = View.GONE
            tempDeviceVersionInfo = it
            viewModel?.getDeviceInfoData(this@CarAboutActivity,it.identificationCode,it.binCode,it.versionCode,it.productCode)
        }


        //芯片序列号
        viewModel?.cdKeyCode?.observe(this){
            cdKeyCode = it
        }

        //获取固件版本信息
        viewModel?.getDeviceVersion()
        GlobalScope.launch {
            delay(1500)
            viewModel?.getDeviceCdKey()
        }

    }



    private fun showClickOta(show : Boolean){

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
            if(position == 0x00){
                startActivity(ShowWebActivity::class.java)
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


    private var dfuDialog : DfuDialogView ?= null
    private fun showDfuDialog(bean : ServerVersionInfoBean.FirmwareListDTO){
        if(dfuDialog == null){
            dfuDialog = DfuDialogView(this@CarAboutActivity, com.bonlala.base.R.style.BaseDialogTheme)
        }
        dfuDialog?.show()
        dfuDialog?.setCancelable(false)
        dfuDialog?.setDfuUpgradeContent("更新内容:\n"+bean.content+"\nfileName: "+bean.fileName+"\nversionCode: "+bean.versionCode)
        val saveUrl = getExternalFilesDir(null)?.path+"/OTA/"+bean.fileName
        dfuDialog?.setOnClick { position ->
            if (position == 0x00) {
                dfuDialog?.unregister()
                dfuDialog?.dismiss()

            }
            if (position == 0x01) {
                dfuDialog?.dismiss()
                //  dfuDialog?.setDfuModel()
                BaseApplication.getBaseApplication().isOTAModel = true
                intoDfuModel()
                Thread.sleep(1000)
                //  BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                showDfuStatus(true)
                downloadOta(bean.ota, saveUrl)
            }
        }
        dfuDialog?.setOnDfuStateListener{
            BaseApplication.getBaseApplication().isOTAModel = false
            dfuDialog?.dismiss()
            ToastUtils.show(if(it == 1) "升级成功,请重新连接使用!" else "升级失败,请重新升级!")
            if(it == 1){
                startActivity(SecondScanActivity::class.java)
                finish()
            }

        }

    }

    //下载
    private fun downloadOta(url : String,saveUrl : String){
        downloadFile(url,saveUrl,object : OnDownloadListener{
            override fun onStart(file: File?) {

            }

            override fun onProgress(file: File?, progress: Int) {

            }

            override fun onComplete(file: File?) {

                GlobalScope.launch {
                    delay(1500)
                    isUpgrading = true
                    file?.path?.let {
                      //  dfuDialog?.startDfuModel(it)
                        dfuViewModel?.startDfuModel(file.path,this@CarAboutActivity)
                    }
                }
            }

            override fun onError(file: File?, e: java.lang.Exception?) {

            }

            override fun onEnd(file: File?) {

            }

        })
    }

    //获取权限
    private fun getPermission(){
        XXPermissions.with(this).permission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)).request { permissions, allGranted ->

        }
    }

    private fun intoDfuModel(){
        val bt = ByteArray(3)
        bt[0] = 0x01
        bt[1] = 0x02
        bt[2] = 0x00
        val otaByte = Utils.getFullPackage(bt)
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(otaByte
        ) { }
    }


    override fun onDestroy() {
        super.onDestroy()
        dfuViewModel?.unregister(this)
    }


    //是否正在升级中
    private fun showDfuStatus(upgrading : Boolean){
        if(upgrading){
            val upArray = IntArray(2)
            upArray[0] = Color.parseColor("#67DFD0")
            upArray[1] = Color.parseColor("#2BA6F7")
            aboutDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray).intoBackground()
            aboutDfuShowTv?.text = "正在升级..."
            return
        }
        val upArray = IntArray(2)
        upArray[0] = Color.parseColor("#F28D27")
        upArray[1] = Color.parseColor("#FD654D")
        aboutDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray).intoBackground()
        aboutDfuShowTv?.text = "有新版本更新"

    }

    override fun onBackPressed() {
        if(isUpgrading){
            ToastUtils.show("正在升级中，请务退出！")
            return
        }

        super.onBackPressed()
    }
}