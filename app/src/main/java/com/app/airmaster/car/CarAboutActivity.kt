package com.app.airmaster.car

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.StrictMode
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
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
import com.app.airmaster.car.bean.AppVoBean
import com.app.airmaster.car.bean.DeviceBinVersionBean
import com.app.airmaster.car.bean.ServerVersionInfoBean
import com.app.airmaster.car.bean.VersionParamsBean
import com.app.airmaster.dialog.AppUpdateDialog
import com.app.airmaster.dialog.DfuDialogView
import com.app.airmaster.dialog.LogDialogView
import com.app.airmaster.dialog.ShowProgressDialog
import com.app.airmaster.dialog.SingleAlertDialog
import com.app.airmaster.second.SecondScanActivity
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.ClickUtils
import com.app.airmaster.utils.GetJsonDataUtil
import com.app.airmaster.utils.MmkvUtils
import com.app.airmaster.viewmodel.BridgeDfuViewModel
import com.app.airmaster.viewmodel.DfuViewModel
import com.app.airmaster.viewmodel.McuUpgradeViewModel
import com.app.airmaster.viewmodel.VersionViewModel
import com.app.airmaster.viewmodel.WatchDeviceViewModel
import com.app.airmaster.viewmodel.WatchOTAViewModel
import com.blala.blalable.BleConstant
import com.blala.blalable.BleOperateManager
import com.blala.blalable.Utils
import com.google.gson.Gson
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.http.listener.OnDownloadListener
import com.hjq.permissions.XXPermissions
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

    private val BLUETOOTH_IdentificationCode = "02fffff9"
    private var TOUCHPAD_IdentificationCode = "03fffffe"
    private val MCU_IdentificationCode = "04fffffd"

    //mcu 的ota升级
    private var mcuViewModel : McuUpgradeViewModel ?= null
    private var bridgeDfuViewModel: BridgeDfuViewModel? = null

    private var viewModel: VersionViewModel? = null
    private var dfuViewModel: DfuViewModel? = null
    private var watchViewModel: WatchDeviceViewModel? = null
    private var watchOtaViewModel: WatchOTAViewModel? = null

    private var appVoBean : AppVoBean ?= null


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

    //旋钮的布局
    private var screenLayout: LinearLayout? = null


    //Bluetooth版本
    private var bluetoothDfuVersionTv: TextView? = null
    private var bluetoothDfuShowTv: ShapeTextView? = null

    //touchpad
    private var touchpadDfuShowTv: ShapeTextView? = null
    private var touchpadVersionTv: TextView? = null

    //otherMcu
    private var aboutOtherMcuLayout : ConstraintLayout ?= null
    private var otherMcuDfuShowTv: ShapeTextView? = null
    private var aboutOtherMcuVersionTv: TextView? = null

    //app升级
    private var aboutAppVersionShowTv : ShapeTextView ?= null


    private var aboutActivateDeviceImageView: ImageView? = null
    private var aboutUpgradeImageView: ImageView? = null


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
    //正在升级的类型识别码，Bluetooth、touchpad、手表或者mcu
    private var upgradingIdentificationCode : String ?= null

    private var tempDeviceVersionInfo: DeviceBinVersionBean? = null

    private var tempServerBean: ServerVersionInfoBean.FirmwareListDTO? = null

    private var tempServerListBean: MutableList<ServerVersionInfoBean.FirmwareListDTO>? = null

    //是否连接了手表
    private var isConnWatch = false

    private var serverStr = "--"


    private val handlers: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x00) {
                isUpgrading = false
                cancelProgressDialog()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_app_about_layout
    }

    override fun initView() {
        aboutAppVersionShowTv = findViewById(R.id.aboutAppVersionShowTv)
        aboutOtherMcuLayout = findViewById(R.id.aboutOtherMcuLayout)
        screenLayout = findViewById(R.id.screenLayout)
        otherMcuDfuShowTv = findViewById(R.id.otherMcuDfuShowTv)
        touchpadDfuShowTv = findViewById(R.id.touchpadDfuShowTv)
        bluetoothDfuVersionTv = findViewById(R.id.bluetoothDfuVersionTv)
        aboutOtherMcuVersionTv = findViewById(R.id.aboutOtherMcuVersionTv)
        touchpadVersionTv = findViewById(R.id.touchpadVersionTv)
        aboutActivateSubmitTv = findViewById(R.id.aboutActivateSubmitTv)
        carWatchLayout = findViewById(R.id.carWatchLayout)
        aboutActivateLayout = findViewById(R.id.aboutActivateLayout)
        aboutUpgradeLayout = findViewById(R.id.aboutUpgradeLayout)
        aboutCarDfuShowTv = findViewById(R.id.aboutCarDfuShowTv)
        aboutWatchVersionTv = findViewById(R.id.aboutWatchVersionTv)
        carAboutTitleBar = findViewById(R.id.carAboutTitleBar)
        bluetoothDfuShowTv = findViewById(R.id.bluetoothDfuShowTv)
        activateContentLayout = findViewById(R.id.activateContentLayout)
        aboutActivateDeviceImageView = findViewById(R.id.aboutActivateDeviceImageView)
        aboutUpgradeImageView = findViewById(R.id.aboutUpgradeImageView)
        aboutActivateEdit = findViewById(R.id.aboutActivateEdit)
        appVersionTv = findViewById(R.id.appVersionTv)
        aboutUpgradeContentLayout = findViewById(R.id.aboutUpgradeContentLayout)


        aboutOtherMcuLayout?.setOnClickListener {
            mcuViewModel?.sendCheckMcuData()
        }

        aboutOtherMcuLayout?.setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
               // openFilePath()

                return true
            }

        })


        aboutUpgradeLayout?.setOnClickListener(this)
        aboutActivateLayout?.setOnClickListener(this)
        carWatchLayout?.setOnClickListener(this)
        aboutCarDfuShowTv?.setOnClickListener(this)
        aboutActivateSubmitTv?.setOnClickListener(this)


        bluetoothDfuShowTv?.setOnClickListener(this)
        touchpadDfuShowTv?.setOnClickListener(this)
        otherMcuDfuShowTv?.setOnClickListener(this)

        aboutAppVersionShowTv?.setOnClickListener(this)

        touchpadVersionTv?.setOnClickListener {

        }


       // getPermission()

        isUpgradeFoldState(true)
        isAcFoldState(true)


        val buildS = SpannableStringBuilder(resources.getString(R.string.string_activation_code))
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
                    ToastUtils.show(resources.getString(R.string.string_not_exit_dfu))
                    return
                }
                if (isConnWatch) {
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                    BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                }
                finish()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {

                if (tempDeviceVersionInfo != null) {
                    showLogDialog(Gson().toJson(tempDeviceVersionInfo)+"\n"+serverStr)
                  //  tempDeviceVersionInfo?.sourceStr?.let { showLogDialog(it) }

                }
            }

        })
    }


    private fun openFilePath(){
        val path = getExternalFilesDir(null)?.path+"/log/"
        val f = File(path)
        if(!f.exists()){
            f.mkdirs()
        }
        val logContent = mcuViewModel?.getMcuOtaLog()
        val name = System.currentTimeMillis().toString()+".json"
        GlobalScope.launch {
            GetJsonDataUtil().writeTxtToFile(logContent,path,name)

            val sharePath = path+name
            openFileThirdApp(this@CarAboutActivity,sharePath)
        }
    }

    /**
     * 调用系统应用打开文件（系统分享）
     *
     * @param context
     * @param filePath 文件路径
     */
    fun openFileThirdApp(context: Context, filePath: String?) {
        if (TextUtils.isEmpty(filePath)) {
            ToastUtils.show("文件不存在，请重新选择")
            return
        }
        val file = File(filePath)
        checkFileUriExposure()
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        intent.type = "*/*" //分享文件类型
        context.startActivity(Intent.createChooser(intent, "分享"))
    }

    /**
     * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
     * 否则会报android.os.FileUriExposedException: file:///xxx.pdf exposed beyond app through ClipData.Item.getUri()
     */
    private fun checkFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
        }
    }


    private fun checkMcuViewModel(){
        //升级中
        mcuViewModel?.mcuDfuProgress?.observe(this){
            otherMcuDfuShowTv?.text  = resources.getString(R.string.string_upgrading) + " " + it
        }

        //校验码状态值
        mcuViewModel?.readCheckValue?.observe(this){
            ToastUtils.show("校验返回=$it"+checkStatus(it))
            isUpgrading = false
            if(it == 0){
                otherMcuDfuShowTv?.text = resources.getString(R.string.string_upgradge_success)
                ToastUtils.show(resources.getString(R.string.string_upgradge_success))
                GlobalScope.launch {
                    delay(2000)
                    finish()
                }
            }
        }

        mcuViewModel?.mcuBootTimeOut?.observe(this){
            isUpgrading = false
            if(it==1){
                ToastUtils.show(resources.getString(R.string.string_upgradge_time_out))
                GlobalScope.launch {
                    delay(2000)
                    finish()
                }
            }else if(it == 0){
                ToastUtils.show(resources.getString(R.string.string_upgradge_success))
            }
            else{
                ToastUtils.show(String.format(resources.getString(R.string.string_exit_bot_code),it.toString()))

            }
        }
    }


    override fun initData() {

        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_START_SCAN_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver,intentFilter,Context.RECEIVER_EXPORTED)
        }else{
            registerReceiver(broadcastReceiver,intentFilter)
        }

        mcuViewModel = ViewModelProvider(this)[McuUpgradeViewModel::class.java]
        bridgeDfuViewModel = ViewModelProvider(this)[BridgeDfuViewModel::class.java]
        watchViewModel = ViewModelProvider(this)[WatchDeviceViewModel::class.java]
        dfuViewModel = ViewModelProvider(this)[DfuViewModel::class.java]
        viewModel = ViewModelProvider(this)[VersionViewModel::class.java]
        watchOtaViewModel = ViewModelProvider(this)[WatchOTAViewModel::class.java]

        checkMcuViewModel()
//        val aB = AppVoBean()
//        aB.versionCode = 1
//        aB.content = "dfsfas"
//        aB.fileName = "123"
//        aB.ota = "http://47.113.195.211:8090/app/download?url=upgrade/app/AIRMASTER_V1.0.6.5.apk"
//        showAppUpdateDialog(aB)

        watchOtaViewModel?.upgradeStatus?.observe(this) {
            BaseApplication.getBaseApplication().isOTAModel = false
            isUpgrading = false
            aboutCarDfuShowTv?.visibility = View.GONE
            ToastUtils.show(if (it) resources.getString(R.string.string_upgradge_success) else resources.getString(R.string.string_upgradge_failed_try_again))
            isConnWatch = true
            BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
        }
        val isScreen = MmkvUtils.getScreenDeviceStatus()

        watchOtaViewModel?.initData(this)


        dfuViewModel?.registerDfu(this)
        showAppVersion()


        val logUrl = getExternalFilesDir(null)?.path+"/log/"
        val f = File(logUrl)
        if(!f.exists()){
            f.mkdirs()
        }
        mcuViewModel?.setLogUrl(logUrl)

        //touchpad的升级状态
        bridgeDfuViewModel?.touchpadUpgradeStatus?.observe(this) {
            if (it.isSyncValid) {
                if (it.syncProgress != 0) {
                    touchpadDfuShowTv?.text = resources.getString(R.string.string_upgrading) + " " + it.syncProgress + "%"
                   // showProgressDialog(resources.getString(R.string.string_upgrading) + " " + it.syncProgress + "%")
                }
                if (it.syncProgress == 1000) {
                    cancelProgressDialog()
                    BaseApplication.getBaseApplication().isOTAModel = false
                    //  ToastUtils.show("升级成功!")
                    isUpgrading = false
                    ToastUtils.show(resources.getString(R.string.string_upgradge_success_re_conn))
                    startActivity(SecondScanActivity::class.java)
                    finish()
                }
            } else {
                BaseApplication.getBaseApplication().isOTAModel = false
                ToastUtils.show(it.inValidDesc)
                cancelProgressDialog()
                isUpgrading = false
                showDfuStatus(false,TOUCHPAD_IdentificationCode)
            }
        }

        //bluetooth ota升级
        dfuViewModel?.dfuProgressData?.observe(this) {
            bluetoothDfuShowTv?.text = String.format(resources.getString(R.string.string_upgradge_progress),it)
        }
        dfuViewModel?.dfuUpgradeStatus?.observe(this) {
            BaseApplication.getBaseApplication().isOTAModel = false
            dfuDialog?.dismiss()
            isUpgrading = false

            ToastUtils.show(if (it) resources.getString(R.string.string_upgradge_success_re_conn) else resources.getString(R.string.string_upgradge_failed_try_again))
            dfuViewModel?.unregister(this@CarAboutActivity)
            if (it) {
                startActivity(SecondScanActivity::class.java)
                finish()
            }
        }


        //固件信息后台返回
        viewModel?.serverVersionInfo?.observe(this) {
            if (it == null) {
                isUpgrading = false
                showVisibilityUpgrade()
                return@observe
            }
            if (tempDeviceVersionInfo == null) {
                isUpgrading = false
                showVisibilityUpgrade()
                return@observe
            }

            val firmList = it.firmwareList

            if (firmList == null || firmList.size == 0) {
                isUpgrading = false
                showVisibilityUpgrade()
                return@observe
            }

            var infoBean: ServerVersionInfoBean.FirmwareListDTO? = null
            if (it.isCarWatch) {  //手表
                infoBean = firmList[0]
                this.tempServerBean = infoBean
                aboutCarDfuShowTv?.visibility = View.VISIBLE
                showWatchDfuStatus(false)
                return@observe
            }

            tempServerListBean = firmList
            firmList.forEachIndexed { index, firmwareListDTO ->
                if (firmwareListDTO.identificationCode == tempDeviceVersionInfo?.identificationCode) { //bluetooth
                    bluetoothDfuShowTv?.visibility = View.VISIBLE
                }

                if (firmwareListDTO.identificationCode == tempDeviceVersionInfo?.mcuIdentificationCode) { //touchpad
                    TOUCHPAD_IdentificationCode = firmwareListDTO.identificationCode
                    touchpadDfuShowTv?.visibility = View.VISIBLE
                }

                if (firmwareListDTO.identificationCode == tempDeviceVersionInfo?.screenMcuIdentificationCode) { //muc
                    otherMcuDfuShowTv?.visibility = View.VISIBLE
                }

                showDfuStatus(false, firmwareListDTO.identificationCode)

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


        //读取设备的版本信息
        viewModel?.deviceVersionInfo?.observe(this) { it ->
            tempDeviceVersionInfo = it
            if (isScreen) { //旋钮
                TOUCHPAD_IdentificationCode = it.mcuIdentificationCode
                //bluetooth
                bluetoothDfuVersionTv?.text = it.versionStr
                //touchpad
                touchpadVersionTv?.text = it.mcuVersionCode
                //other
                aboutOtherMcuVersionTv?.text = it.screenVersionCode

                bluetoothDfuShowTv?.visibility = View.GONE
                touchpadDfuShowTv?.visibility = View.GONE
                otherMcuDfuShowTv?.visibility = View.GONE


                val list = mutableListOf<VersionParamsBean.ParamsListBean>()
                val bluetoothBean = VersionParamsBean.ParamsListBean(
                    it.identificationCode,
                    "0x" + it.productCode,
                    it.versionCode.toString()
                )

                val touchpadBean = VersionParamsBean.ParamsListBean(
                    it.mcuIdentificationCode,
                    "0x" + it.mcuBroadcastId,
                    it.mcuVersionCodeInt.toString()
                )

                //it.screenMcuVersionCodeInt.toString()  //"780"
                val otherMcuBean = VersionParamsBean.ParamsListBean(
                    it.screenMcuIdentificationCode,
                    "0x" + it.screenMcuBroadcastId,
                    it.screenMcuVersionCodeInt.toString()
                )
                list.add(bluetoothBean)
                list.add(touchpadBean)
                list.add(otherMcuBean)

                serverStr = it.binCode+" "+Gson().toJson(list)

                viewModel?.getDeviceInfoData(
                    false,
                    this@CarAboutActivity, it.binCode,
                    list
                )
            } else {
                aboutWatchVersionTv?.text = it.versionStr

                tempDeviceVersionInfo?.deviceMac = MmkvUtils.getConnDeviceMac()
                val list = mutableListOf<VersionParamsBean.ParamsListBean>()
                val watchBean = VersionParamsBean.ParamsListBean(
                    it.identificationCode,
                    "0x" + it.productCode,
                    it.versionCode.toString()
                )
                list.add(watchBean)
                viewModel?.getDeviceInfoData(
                    true,
                    this@CarAboutActivity, it.binCode,
                    list
                )
            }

        }


        //芯片序列号
        viewModel?.cdKeyCode?.observe(this) {
            cdKeyCode = it
        }

        getDeviceVersion(isScreen)

        showWatchOrNot(isScreen)

        if (isScreen && BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED) {
            val activityStatus =
                BaseApplication.getBaseApplication().autoBackBean?.activationStatus == 1
            if (activityStatus) {
                aboutActivateSubmitTv?.text = resources.getString(R.string.string_has_activation)
                aboutActivateSubmitTv!!.shapeDrawableBuilder.setSolidColor(Color.parseColor("#80FD654D"))
                    .intoBackground()
            }
        }



        viewModel?.appVersionData?.observe(this){
            if(it != null){
                if(!it.fileName.contains(".apk")){
                    return@observe
                }

                val info = packageManager.getPackageInfo(packageName,0)
                val versionCode = info.versionCode
                appVoBean = it
                if(versionCode<it.versionCode){ //提示升级

                    aboutAppVersionShowTv?.visibility = View.VISIBLE


                }else{
                    aboutAppVersionShowTv?.visibility = View.GONE
                }
            }
        }

        val versionInfo = packageManager.getPackageInfo(packageName,0)
        viewModel?.checkAppVersion(this,versionInfo.versionCode)

    }


    private fun getDeviceVersion(isScreen: Boolean) {
        //获取固件版本信息
        viewModel?.getDeviceVersion(isScreen)
        GlobalScope.launch {
            delay(1500)
            viewModel?.getDeviceCdKey()
        }
    }

    //显示手表或旋钮屏
    private fun showWatchOrNot(screen: Boolean) {
        carWatchLayout?.visibility = if (screen) View.GONE else View.VISIBLE
        screenLayout?.visibility = if (screen) View.VISIBLE else View.GONE
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
        dialog.setContentTxt(if (success) resources.getString(R.string.string_activation_success) else resources.getString(R.string.string_activation_failed_input_code))
        if (success) {
            //是否已激活
           // MmkvUtils.setSaveObjParams(MmkvUtils.getConnDeviceMac(), success)
            aboutActivateSubmitTv?.text = resources.getString(R.string.string_has_activation)
            aboutActivateSubmitTv!!.shapeDrawableBuilder.setSolidColor(Color.parseColor("#80FD654D"))
                .intoBackground()
        }

    }

    private fun showNotConnDialog() {
        showCommAlertDialog(
            resources.getString(R.string.string_not_conn_device), resources.getString(R.string.string_go_to_official_website), resources.getString(R.string.string_go_to_conn)
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
    private fun showDfuDialog(isCarWatch: Boolean, bean: ServerVersionInfoBean.FirmwareListDTO) {

        if (dfuDialog == null) {
            dfuDialog =
                DfuDialogView(this@CarAboutActivity, com.bonlala.base.R.style.BaseDialogTheme)
        }
        dfuDialog?.show()
        dfuDialog?.setCancelable(false)
        dfuDialog?.setTitleTxt(if (isCarWatch) resources.getString(R.string.string_watch_upgradge) else checkTypeTitle(bean.identificationCode))
        dfuDialog?.setDfuUpgradeContent(resources.getString(R.string.string_upgradge_contents) + bean.content + "\nfileName: " + bean.fileName + "\nversionCode: " + bean.versionCode)
        val saveUrl = getExternalFilesDir(null)?.path + "/OTA/" + bean.fileName
        dfuDialog?.setOnClick { position ->
            if (position == 0x00) {
                dfuDialog?.unregister()
                dfuDialog?.dismiss()

            }
            if (position == 0x01) {
                dfuDialog?.dismiss()
                upgradingIdentificationCode = bean.identificationCode
                //  dfuDialog?.setDfuModel()
                BaseApplication.getBaseApplication().isOTAModel = true
                if (isCarWatch || bean.identificationCode == BLUETOOTH_IdentificationCode) {
                    intoDfuModel(isCarWatch)
                }

                Thread.sleep(1000)
                //  BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                if (isCarWatch) {
                    isConnWatch = true
                    BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                    showWatchDfuStatus(true)
                    watchOtaViewModel?.downloadFile(
                        bean.ota,
                        bean.fileName,
                        tempDeviceVersionInfo?.deviceMac
                    )
                } else {
                    showDfuStatus(true, bean.identificationCode)

                   // val url = "https://otaitem.oss-cn-shenzhen.aliyuncs.com/upgrade/firmware/check_BIN_BA0059CS_0XFFFE_V00015C_0X00800001_0X04FFFFFD_202403222023.xlbin"
                    downloadOta(bean.ota, saveUrl, bean.identificationCode)

//                    downloadOta(url, saveUrl, bean.identificationCode)
                }

            }
        }

    }

    //下载
    private fun downloadOta(url: String, saveUrl: String, identificationCode: String) {
        Timber.e("-------识别码=" + identificationCode)
      //  showProgressDialog("升级中...")
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
                        if (identificationCode == BLUETOOTH_IdentificationCode) {   //bluetooth nordic
                          //  showDfuStatus(true, BLUETOOTH_IdentificationCode)
                            dfuViewModel?.startDfuModel(file.path, this@CarAboutActivity)
                        }
                        if (identificationCode == TOUCHPAD_IdentificationCode) {   //touchpad 旋钮屏的显示
                            bridgeDfuViewModel?.setEraseDeviceFlash(file, this@CarAboutActivity)
                        }
                        if (identificationCode == MCU_IdentificationCode) {   //其它mcu，客户mcu
                           // handlers.sendEmptyMessageDelayed(0x00, 10 * 1000)

                            GlobalScope.launch {
                                mcuViewModel?.setInit()
                                mcuViewModel?.dealDfuFile(file)
                            }

                        }

                    }
                }
            }

            override fun onError(file: File?, e: java.lang.Exception?) {

            }

            override fun onEnd(file: File?) {

            }

        })
    }


    private fun intoDfuModel(isCarWatch: Boolean) {
        val bt = ByteArray(3)
        bt[0] = 0x01
        bt[1] = 0x02
        bt[2] = 0x00
        val otaByte = Utils.getFullPackage(bt)
        if (isCarWatch) {
            BaseApplication.getBaseApplication().bleOperate.writeCarWatchData(
                otaByte
            ) {

            }
            return
        }
        BaseApplication.getBaseApplication().bleOperate.writeCommonByte(
            otaByte
        ) { }
    }


    override fun onDestroy() {
        super.onDestroy()
        dfuViewModel?.unregister(this)
        unregisterReceiver(broadcastReceiver)
    }


    //旋钮是否正在升级中
    private fun showDfuStatus(upgrading: Boolean, identificationCode: String) {
        if (upgrading) {
            val upArray = IntArray(2)
            upArray[0] = Color.parseColor("#67DFD0")
            upArray[1] = Color.parseColor("#2BA6F7")

            when (identificationCode) {
                BLUETOOTH_IdentificationCode -> {   //bluetooth
                    bluetoothDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                        .intoBackground()
                    bluetoothDfuShowTv?.text = resources.getString(R.string.string_upgradge_state_ing)
                }

                TOUCHPAD_IdentificationCode -> {  //touchpad
                    touchpadDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                        .intoBackground()
                    touchpadDfuShowTv?.text = resources.getString(R.string.string_upgradge_state_ing)
                }

                MCU_IdentificationCode -> {  //mcu
                    otherMcuDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                        .intoBackground()
                    otherMcuDfuShowTv?.text = resources.getString(R.string.string_upgradge_state_ing)
                }
            }

            return
        }
        val upArray = IntArray(2)
        upArray[0] = Color.parseColor("#F28D27")
        upArray[1] = Color.parseColor("#FD654D")

        when (identificationCode) {
            BLUETOOTH_IdentificationCode -> {   //bluetooth
                bluetoothDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                    .intoBackground()
                bluetoothDfuShowTv?.text = resources.getString(R.string.string_has_new_version)
            }

            TOUCHPAD_IdentificationCode -> {  //touchpad
                touchpadDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                    .intoBackground()
                touchpadDfuShowTv?.text = resources.getString(R.string.string_has_new_version)
            }

            MCU_IdentificationCode -> {  //mcu
                otherMcuDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                    .intoBackground()
                otherMcuDfuShowTv?.text = resources.getString(R.string.string_has_new_version)
            }
        }


    }

    override fun onBackPressed() {
        if (isUpgrading) {
            ToastUtils.show(resources.getString(R.string.string_not_exit_dfu))
            return
        }
//        if(isConnWatch){
//            BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
//            BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
//        }

        super.onBackPressed()
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

            //app升级
            R.id.aboutAppVersionShowTv->{
                if (isUpgrading) {
                    return
                }
                if(appVoBean != null){
                    showAppUpdateDialog(appVoBean!!)
                }

            }

            //bluetooth 升级
            R.id.bluetoothDfuShowTv -> {
                checkConnStatus()
                if (isUpgrading) {
                    return
                }
                if (tempServerListBean != null && tempServerListBean?.size!! > 0) {
                    val bean = tempServerListBean?.find { it.identificationCode == BLUETOOTH_IdentificationCode }
                    if (bean != null) {
                        showDfuDialog(false, bean)
                    }
                }
            }

            R.id.touchpadDfuShowTv -> {   //touchpad 升级
                checkConnStatus()
                if (isUpgrading) {
                    return
                }
                if (tempServerListBean != null && tempServerListBean?.size!! > 0) {
                    val bean = tempServerListBean?.find { it.identificationCode == TOUCHPAD_IdentificationCode }
                    if (bean != null) {
                        showDfuDialog(false, bean)
                    }
                }
            }


            R.id.otherMcuDfuShowTv -> {   //other mcu
                checkConnStatus()
                if (isUpgrading) {
                    return
                }
                if (tempServerListBean != null && tempServerListBean?.size!! > 0) {
                    val bean = tempServerListBean?.find { it.identificationCode == MCU_IdentificationCode }
                    if (bean != null) {
                        showDfuDialog(false, bean)
                    }
                }
            }

            //无线手环的升级
            R.id.aboutCarDfuShowTv -> {
                if (isUpgrading) {
                    return
                }
                if (tempDeviceVersionInfo != null) {
                    showDfuDialog(true, tempServerBean!!)
                }
            }

            //提交激活码
            R.id.aboutActivateSubmitTv -> {
                val activateCode = aboutActivateEdit?.text.toString()
                if (BikeUtils.isEmpty(activateCode)) {
                    ToastUtils.show(resources.getString(R.string.string_input_activation_code))
                    return
                }
                if (BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED) {
                    showNotConnDialog()
                    return
                }

                val autoBean = BleOperateManager.getInstance().getmAutoBean()

                //val success = MmkvUtils.getSaveParams(MmkvUtils.getConnDeviceMac(), false)
                if (autoBean.activationStatus == 1) {

                    return
                }
                viewModel?.setDeviceIdentificationCode(activateCode)
            }
        }
    }


    private fun showLogDialog(txt: String) {
        val dialog = LogDialogView(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setLogTxt(txt)

        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = (metrics2.widthPixels * 0.8F).toInt()
        val height: Int = (metrics2.heightPixels * 0.7F).toInt()

        windowLayout?.height = height
        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.CENTER_VERTICAL
        window?.attributes = windowLayout
    }


    private fun checkTypeTitle(identificationCode: String): String {
        if (identificationCode == BLUETOOTH_IdentificationCode) {   //bluetooth nordic
            return String.format(resources.getString(R.string.string_air_upgrade), "Bluetooth")
        }
        if (identificationCode == TOUCHPAD_IdentificationCode) {   //bluetooth nordic
            return String.format(resources.getString(R.string.string_air_upgrade), "Touchpad")
        }

        return String.format(resources.getString(R.string.string_air_upgrade), "MCU")
    }


    fun cancelProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    private var progressDialog: ShowProgressDialog? = null

    //显示弹窗
    private fun showProgressDialog(msg: String) {
        Timber.e("--------Dialog=" + (isFinishing))
        if (progressDialog == null) {
            progressDialog = ShowProgressDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        }
        if (progressDialog?.isShowing == false) {
            if (isFinishing) {
                return
            }
            progressDialog?.show()
        }
        progressDialog?.setCancelable(false)
        progressDialog?.setShowMsg(msg)
    }


    //手表是否正在升级中
    private fun showWatchDfuStatus(upgrading: Boolean) {
        if (upgrading) {
            val upArray = IntArray(2)
            upArray[0] = Color.parseColor("#67DFD0")
            upArray[1] = Color.parseColor("#2BA6F7")


            aboutCarDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray)
                .intoBackground()
            aboutCarDfuShowTv?.text = resources.getString(R.string.string_upgradge_state_ing)

            return
        }
        val upArray = IntArray(2)
        upArray[0] = Color.parseColor("#F28D27")
        upArray[1] = Color.parseColor("#FD654D")
        aboutCarDfuShowTv!!.shapeDrawableBuilder.setSolidGradientColors(upArray).intoBackground()
        aboutCarDfuShowTv?.text = resources.getString(R.string.string_has_new_version)

    }


    private fun showVisibilityUpgrade() {
        bluetoothDfuShowTv?.visibility = View.GONE
        touchpadDfuShowTv?.visibility = View.GONE
        otherMcuDfuShowTv?.visibility = View.GONE
    }

    private fun checkConnStatus() {
        val isConn = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED
        if (!isConn) {
            ToastUtils.show(resources.getString(R.string.string_device_not_connect))
            return
        }
    }


    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            Timber.e("---------acdtion="+action)
            if(action == BleConstant.BLE_CONNECTED_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_success))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()

            }
            if(action == BleConstant.BLE_DIS_CONNECT_ACTION){
                //ToastUtils.show(resources.getString(R.string.string_conn_disconn))
                if(upgradingIdentificationCode != null && upgradingIdentificationCode == TOUCHPAD_IdentificationCode){
                    ToastUtils.show(resources.getString(R.string.string_conn_disconn))
                    showInit()
                    if(isUpgrading){
                        showDisConnectDialog()
                    }
                }

            }

        }

    }


    private fun showInit(){
        bluetoothDfuVersionTv?.text = ""
        touchpadVersionTv?.text = ""
        aboutOtherMcuVersionTv?.text = ""
        showVisibilityUpgrade()
    }

    private fun showDisConnectDialog(){
        val dialog = SingleAlertDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setContentTxt(resources.getString(R.string.string_conn_has_dis_please_conn))
       dialog.setOnDialogClickListener {
           dialog.dismiss()
           startActivity(SecondScanActivity::class.java)
           finish()
       }
    }


    private fun checkStatus(code : Int) : String{
        if(code == 0){
            return resources.getString(R.string.string_upgradge_success)
        }
        if(code == 1){
            return resources.getString(R.string.string_mcu_ota_data_error)
        }
        if(code == 2){
            return resources.getString(R.string.string_mcu_ota_erase_error)
        }
        if(code == 3){
            return resources.getString(R.string.string_mcu_write_error)
        }
        return resources.getString(R.string.string_mcu_ota_write_check_error)
    }

    private fun showAppUpdateDialog(bean : AppVoBean){
        val dialog = AppUpdateDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setTitleTxt(resources.getString(R.string.string_has_new_version))
        dialog.setContent(bean.content)
        dialog.setIsFocusUpdate(bean.isForceUpdate)
        dialog.setOnDialogClickListener(object : OnCommItemClickListener{
            override fun onItemClick(position: Int) {
                if(position == 0x00){
                    dialog.dismiss()
                }
                if(position == 0x01){
                    dialog.startDownload(this@CarAboutActivity,bean.ota,"airmaster_"+bean.versionCode.toString()+".apk")
                }
            }

        })
    }

}