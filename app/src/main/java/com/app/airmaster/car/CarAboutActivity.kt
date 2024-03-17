package com.app.airmaster.car

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Im
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.adapter.OnCommItemClickListener
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.app.airmaster.car.bean.ServerVersionInfoBean
import com.app.airmaster.dialog.DfuDialogView
import com.app.airmaster.dialog.DialogScanDeviceView
import com.app.airmaster.dialog.SingleAlertDialog
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.DfuViewModel
import com.app.airmaster.viewmodel.OnCarVersionListener
import com.app.airmaster.viewmodel.VersionViewModel
import com.app.airmaster.viewmodel.WatchDeviceViewModel
import com.app.airmaster.viewmodel.WatchOTAViewModel
import com.blala.blalable.Utils
import com.blala.blalable.listener.OnCommBackDataListener
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
class CarAboutActivity : AppActivity() {

    private var viewModel: VersionViewModel? = null
    private var dfuViewModel: DfuViewModel? = null
    private var watchViewModel: WatchDeviceViewModel? = null
    private var watchOtaViewModel : WatchOTAViewModel ?= null

    private var aboutTouchLayout : ConstraintLayout ?= null
    private var aboutMcuLayout : ConstraintLayout ?= null

    //mcu
    private var aboutMcuVersionTv : TextView ?= null


    //系统升级菜单
    private var aboutUpgradeLayout: ConstraintLayout? = null

    //系统设备菜单
    private var aboutActivateLayout: ConstraintLayout? = null

    private var carAboutTitleBar: TitleBar? = null

    //手表
    private var carWatchLayout: ConstraintLayout? = null
    private var aboutWatchVersionTv: TextView? = null
    private var aboutCarDfuShowTv: ShapeTextView? = null


    //提交激活码
    private var aboutActivateSubmitTv: ShapeTextView? = null


    private var aboutDfuShowTv: ShapeTextView? = null

    private var aboutActivateDeviceImageView: ImageView? = null

    private var aboutUpgradeImageView: ImageView? = null

    private var touchPadVersionTv: TextView? = null

    private var appVersionTv: TextView? = null
    private var aboutUpgradeContentLayout: LinearLayout? = null
    private var aboutActivateEdit: ShapeEditText? = null

    private var activateContentLayout: LinearLayout? = null

    //是否是折叠状态
    private var isUpgradeFold = false
    private var isActivateFold = false

    //芯片序列号
    private var cdKeyCode: String? = null

    //是否正在升级中
    private var isUpgrading = false

    private var tempDeviceVersionInfo: DeviceBinVersionBean? = null

    private var tempServerBean: ServerVersionInfoBean.FirmwareListDTO? = null


    //是否连接了手表
    private var isConnWatch = false

    override fun getLayoutId(): Int {
        return R.layout.activity_app_about_layout
    }

    override fun initView() {
        aboutMcuVersionTv = findViewById(R.id.aboutMcuVersionTv)
        aboutMcuLayout = findViewById(R.id.aboutMcuLayout)
        aboutTouchLayout = findViewById(R.id.aboutTouchLayout)
        aboutActivateSubmitTv = findViewById(R.id.aboutActivateSubmitTv)
        carWatchLayout = findViewById(R.id.carWatchLayout)
        aboutActivateLayout = findViewById(R.id.aboutActivateLayout)
        aboutUpgradeLayout = findViewById(R.id.aboutUpgradeLayout)
        aboutCarDfuShowTv = findViewById(R.id.aboutCarDfuShowTv)
        aboutWatchVersionTv = findViewById(R.id.aboutWatchVersionTv)
        carAboutTitleBar = findViewById(R.id.carAboutTitleBar)
        aboutDfuShowTv = findViewById(R.id.aboutDfuShowTv)
        activateContentLayout = findViewById(R.id.activateContentLayout)
        aboutActivateDeviceImageView = findViewById(R.id.aboutActivateDeviceImageView)
        aboutUpgradeImageView = findViewById(R.id.aboutUpgradeImageView)
        touchPadVersionTv = findViewById(R.id.touchPadVersionTv)
        aboutActivateEdit = findViewById(R.id.aboutActivateEdit)
        appVersionTv = findViewById(R.id.appVersionTv)
        aboutUpgradeContentLayout = findViewById(R.id.aboutUpgradeContentLayout)


        aboutUpgradeLayout?.setOnClickListener(this)
        aboutActivateLayout?.setOnClickListener(this)
        carWatchLayout?.setOnClickListener(this)
        aboutCarDfuShowTv?.setOnClickListener(this)
        aboutActivateSubmitTv?.setOnClickListener(this)

        aboutDfuShowTv?.setOnClickListener(this)


        getPermission()

        isUpgradeFoldState(true)
        isAcFoldState(true)


        val buildS = SpannableStringBuilder("请联系经销商获取激活码")
        buildS.setSpan(
            ForegroundColorSpan(Color.parseColor("#4A4A4B")),
            0,
            buildS.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        aboutActivateEdit?.hint = buildS

        carAboutTitleBar?.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(view: View?) {
                if (isUpgrading) {
                    ToastUtils.show("正在升级中，请务退出！")
                    return
                }
                if(isConnWatch){
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                    BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
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
        watchViewModel = ViewModelProvider(this)[WatchDeviceViewModel::class.java]
        dfuViewModel = ViewModelProvider(this)[DfuViewModel::class.java]
        viewModel = ViewModelProvider(this)[VersionViewModel::class.java]
        watchOtaViewModel = ViewModelProvider(this)[WatchOTAViewModel::class.java]


        watchOtaViewModel?.upgradeStatus?.observe(this){
            BaseApplication.getBaseApplication().isOTAModel = false
            isUpgrading = false
            aboutCarDfuShowTv?.visibility = View.GONE
            ToastUtils.show(if(it) "升级成功!" else "升级失败,请重试!")
            isConnWatch = true
            BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
        }
        val isScreen = MmkvUtils.getScreenDeviceStatus()

        watchOtaViewModel?.initData(this)


        dfuViewModel?.registerDfu(this)
        showAppVersion()

        //ota升级
        dfuViewModel?.dfuProgressData?.observe(this) {
            aboutDfuShowTv?.text = "正在升级: $it%"
        }
        dfuViewModel?.dfuUpgradeStatus?.observe(this) {
            BaseApplication.getBaseApplication().isOTAModel = false
            dfuDialog?.dismiss()
            isUpgrading = false
            ToastUtils.show(if (it) "升级成功,请重新连接使用!" else "升级失败,请重新升级!")
            if (it) {
                startActivity(SecondScanActivity::class.java)
                finish()
            }
        }


        //固件信息
        viewModel?.serverVersionInfo?.observe(this) {
            if (it == null) {
                isUpgrading = false
                return@observe
            }
            if (tempDeviceVersionInfo == null) {
                isUpgrading = false
                return@observe
            }

            val firmList = it.firmwareList
            var infoBean: ServerVersionInfoBean.FirmwareListDTO? = null
            firmList.forEachIndexed { index, firmwareListDTO ->
                if (firmwareListDTO.identificationCode == tempDeviceVersionInfo!!.identificationCode) {
                    infoBean = firmwareListDTO
                }
            }
            if (infoBean == null) {
                isUpgrading = false
                return@observe
            }
            this.tempServerBean = infoBean
            if (it.isCarWatch) {
                if(infoBean!!.versionCode != tempDeviceVersionInfo!!.versionCode){
                    aboutCarDfuShowTv?.visibility = View.VISIBLE
                    showWatchDfuStatus(false)
                  return@observe
                }
                isUpgrading = false
                showWatchDfuStatus(false)
                return@observe

            }


            if (infoBean!!.versionCode != tempDeviceVersionInfo!!.versionCode) {
                aboutDfuShowTv?.visibility = View.VISIBLE
                showDfuStatus(false)
            } else {
                touchPadVersionTv?.text = tempDeviceVersionInfo?.versionStr
                aboutDfuShowTv?.visibility = View.GONE
            }

        }

        //激活状态
        viewModel?.activateState?.observe(this) {
            showActivateDialog(it)
            if (it == true) {
                val acCode = aboutActivateEdit?.text.toString()
                cdKeyCode?.let { it1 ->
                    viewModel?.saveActivateRecord(
                        this@CarAboutActivity, acCode,
                        it1
                    )
                }
            }
        }


        viewModel?.deviceVersionInfo?.observe(this) { it ->

            if(isScreen){
                touchPadVersionTv?.text = it.versionStr
                aboutMcuVersionTv?.text = it.mcuVersionCode
                aboutDfuShowTv?.visibility = View.GONE
            }else{
                aboutWatchVersionTv?.text = it.versionStr

            }

            tempDeviceVersionInfo = it
            tempDeviceVersionInfo?.deviceMac = MmkvUtils.getConnDeviceMac()
            viewModel?.getDeviceInfoData(
                !isScreen,
                this@CarAboutActivity,
                it.identificationCode,
                it.binCode,
                it.versionCode,
                it.productCode
            )
        }


        //芯片序列号
        viewModel?.cdKeyCode?.observe(this) {
            cdKeyCode = it
        }


        //获取固件版本信息
        viewModel?.getDeviceVersion(isScreen)
        GlobalScope.launch {
            delay(1500)
            viewModel?.getDeviceCdKey()
        }
        showWatchOrNot(isScreen)

        if(isScreen && BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED){
            val activityStatus = BaseApplication.getBaseApplication().autoBackBean.activationStatus==1
            if(activityStatus){
                aboutActivateSubmitTv?.text = "已激活"
                aboutActivateSubmitTv!!.shapeDrawableBuilder.setSolidColor(Color.parseColor("#80FD654D")).intoBackground()
            }
        }
    }


    private fun showWatchOrNot(screen: Boolean) {
        carWatchLayout?.visibility = if(screen) View.GONE else View.VISIBLE
        aboutMcuLayout?.visibility = if(screen) View.VISIBLE else View.GONE
        aboutTouchLayout?.visibility = if(screen) View.VISIBLE else View.GONE
    }


    private fun showAppVersion() {
        try {
            val packInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packInfo.versionName
            appVersionTv?.text = versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showActivateDialog(success: Boolean) {
        val dialog = SingleAlertDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt(if (success) "激活成功!" else "激活失败,请输入正确的激活码!")
        if(success){
            //是否已激活
            MmkvUtils.setSaveObjParams(MmkvUtils.getConnDeviceMac(),success)
            aboutActivateSubmitTv?.text = "已激活"
            aboutActivateSubmitTv!!.shapeDrawableBuilder.setSolidColor(Color.parseColor("#80FD654D")).intoBackground()
        }

    }

    private fun showNotConnDialog() {
        showCommAlertDialog(
            "未连接设备", "去官网", "去连接"
        ) { position ->
            disCommAlertDialog()
            if (position == 0x01) {
                startActivity(SecondScanActivity::class.java)
            }
            if (position == 0x00) {
                startActivity(ShowWebActivity::class.java)
            }
        }
    }


    //是否是折叠装填
    private fun isUpgradeFoldState(fold: Boolean) {
        isUpgradeFold = !fold
        aboutUpgradeContentLayout?.visibility = if (fold) View.GONE else View.VISIBLE
        rotateU(fold)
    }


    private fun isAcFoldState(fold: Boolean) {
        isActivateFold = !fold
        activateContentLayout?.visibility = if (fold) View.GONE else View.VISIBLE
        rotateA(fold)
    }

    private fun rotateU(fold: Boolean) {
        val rotate = RotateAnimation(
            0F,
            if (fold) 0F else 90F,
            Animation.RELATIVE_TO_SELF,
            0.5F,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5F
        )
        rotate.duration = 10
        rotate.fillAfter = true
        rotate.repeatCount = 0
        aboutUpgradeImageView?.startAnimation(rotate)
    }


    private fun rotateA(fold: Boolean) {
        val rotate = RotateAnimation(
            0F,
            if (fold) 0F else 90F,
            Animation.RELATIVE_TO_SELF,
            0.5F,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5F
        )
        rotate.duration = 10
        rotate.fillAfter = true
        rotate.repeatCount = 0
        aboutActivateDeviceImageView?.startAnimation(rotate)
    }


    private var dfuDialog: DfuDialogView? = null
    private fun showDfuDialog(isCarWatch : Boolean,bean: ServerVersionInfoBean.FirmwareListDTO) {




        if (dfuDialog == null) {
            dfuDialog =
                DfuDialogView(this@CarAboutActivity, com.bonlala.base.R.style.BaseDialogTheme)
        }
        dfuDialog?.show()
        dfuDialog?.setCancelable(false)
        dfuDialog?.setTitleTxt(if(isCarWatch) "无线手环更新" else "Touchpad新版本")
        dfuDialog?.setDfuUpgradeContent("更新内容:\n" + bean.content + "\nfileName: " + bean.fileName + "\nversionCode: " + bean.versionCode)
        val saveUrl = getExternalFilesDir(null)?.path + "/OTA/" + bean.fileName
        dfuDialog?.setOnClick { position ->
            if (position == 0x00) {
                dfuDialog?.unregister()
                dfuDialog?.dismiss()

            }
            if (position == 0x01) {
                dfuDialog?.dismiss()
                //  dfuDialog?.setDfuModel()
                BaseApplication.getBaseApplication().isOTAModel = true
                intoDfuModel(isCarWatch)
                Thread.sleep(1000)
                //  BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                if(isCarWatch){
                    isConnWatch = true
                    BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                    showWatchDfuStatus(true)
                    watchOtaViewModel?.downloadFile(bean.ota,bean.fileName,tempDeviceVersionInfo?.deviceMac)
                }else{
                    showDfuStatus(true)
                    downloadOta(bean.ota, saveUrl)
                }


            }
        }
        dfuDialog?.setOnDfuStateListener {
            BaseApplication.getBaseApplication().isOTAModel = false
            dfuDialog?.dismiss()
            ToastUtils.show(if (it == 1) "升级成功,请重新连接使用!" else "升级失败,请重新升级!")
            if (it == 1) {
                startActivity(SecondScanActivity::class.java)
                finish()
            }

        }

    }

    //下载
    private fun downloadOta(url: String, saveUrl: String) {
        downloadFile(url, saveUrl, object : OnDownloadListener {
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
                        dfuViewModel?.startDfuModel(file.path, this@CarAboutActivity)
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
    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            XXPermissions.with(this).permission(arrayOf(Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.POST_NOTIFICATIONS)).request { permissions, allGranted ->

            }
            return
        }
        XXPermissions.with(this).permission(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ).request { permissions, allGranted ->

        }
    }

    private fun intoDfuModel(isCarWatch : Boolean) {
        val bt = ByteArray(3)
        bt[0] = 0x01
        bt[1] = 0x02
        bt[2] = 0x00
        val otaByte = Utils.getFullPackage(bt)
        if(isCarWatch){
            BaseApplication.getBaseApplication().bleOperate.writeCarWatchData(
                otaByte
            ) { }
            return
        }
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(
            otaByte
        ) { }
    }


    override fun onDestroy() {
        super.onDestroy()
        dfuViewModel?.unregister(this)
    }


    //旋钮是否正在升级中
    private fun showDfuStatus(upgrading: Boolean) {
        if (upgrading) {
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
        if (isUpgrading) {
            ToastUtils.show("正在升级中，请务退出！")
            return
        }
//        if(isConnWatch){
//            BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
//            BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
//        }

        super.onBackPressed()
    }


    //手表是否正在升级中
    //旋钮是否正在升级中
    private fun showWatchDfuStatus(upgrading: Boolean) {
        if (upgrading) {
            val upArray = IntArray(2)
            upArray[0] = Color.parseColor("#67DFD0")
            upArray[1] = Color.parseColor("#2BA6F7")
            aboutCarDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                .intoBackground()
            aboutCarDfuShowTv?.text = "正在升级..."
            return
        }
        val upArray = IntArray(2)
        upArray[0] = Color.parseColor("#F28D27")
        upArray[1] = Color.parseColor("#FD654D")
        aboutCarDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray).intoBackground()
        aboutCarDfuShowTv?.text = "有新版本更新"

    }

    private fun showConnWatchDialog() {
        showCommAlertDialog(
            "连接手环需要断开当前旋钮设备，是否断开?",
            "取消",
            "确定"
        ) { position ->
            disCommAlertDialog()
            if (position == 0x01) {
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                showWatchScanDialog()
            }
        }
    }


    private fun showWatchScanDialog() {
        val scanDialog = DialogScanDeviceView(this, com.bonlala.base.R.style.BaseDialogTheme)
        scanDialog.show()
        scanDialog.startScan()
        scanDialog.setOnBackDataListener(object : OnCarVersionListener {

            override fun backVersion(deviceBinVersionBean: DeviceBinVersionBean?) {
                scanDialog.dismiss()
                if (deviceBinVersionBean == null) {
                    aboutWatchVersionTv?.text = ""
                    isConnWatch = true
                    return
                }
                isConnWatch = true
              //  isUpgrading = true
                tempDeviceVersionInfo = deviceBinVersionBean
                aboutWatchVersionTv?.text =
                    deviceBinVersionBean?.deviceName + " " + deviceBinVersionBean?.versionStr
                viewModel?.getDeviceInfoData(
                    true,
                    this@CarAboutActivity,
                    deviceBinVersionBean.identificationCode,
                    deviceBinVersionBean.binCode,
                    deviceBinVersionBean.versionCode,
                    deviceBinVersionBean.productCode
                )
            }

        })

        val window = scanDialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = (metrics2.widthPixels * 0.9f).toInt()
        val height: Int = (metrics2.heightPixels * 0.6f).toInt()
        windowLayout?.width = widthW
        windowLayout?.height = height
        window?.attributes = windowLayout
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id
        when (id) {
            //系统升级菜单
            R.id.aboutUpgradeLayout -> {
                if (ClickUtils.isFastDoubleClick()) {
                    return
                }
                isUpgradeFoldState(isUpgradeFold)
            }
            //激活设备菜单
            R.id.aboutActivateLayout -> {
                if (ClickUtils.isFastDoubleClick()) {
                    return
                }
                isAcFoldState(isActivateFold)
            }


            //无线手环点击
            R.id.carWatchLayout -> {
//                if(isUpgrading){
//                    return
//                }
//                showConnWatchDialog()
            }

            //touchpad 升级
            R.id.aboutDfuShowTv -> {
                if(isUpgrading){
                    return
                }
                if(ActivityCompat.checkSelfPermission(this@CarAboutActivity,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this@CarAboutActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS),0x00)
                    }
                    return
                }





                if (tempDeviceVersionInfo != null) {
                    showDfuDialog(false,tempServerBean!!)
                }
            }
            //无线手环的升级
            R.id.aboutCarDfuShowTv -> {
                if(isUpgrading){
                    return
                }
                if (tempDeviceVersionInfo != null) {
                    showDfuDialog(true,tempServerBean!!)
                }
            }

            //提交激活码
            R.id.aboutActivateSubmitTv -> {
                val activateCode = aboutActivateEdit?.text.toString()
                if (BikeUtils.isEmpty(activateCode)) {
                    ToastUtils.show("请输入激活码!")
                    return
                }
                if (BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED) {
                    showNotConnDialog()
                    return
                }
                val success = MmkvUtils.getSaveParams(MmkvUtils.getConnDeviceMac(),false)
                if(success == true){

                    return
                }
                viewModel?.setDeviceIdentificationCode(activateCode)
            }
        }
    }
}