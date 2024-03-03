package com.app.airmaster.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.app.airmaster.BaseApplication
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.utils.BikeUtils
import com.app.airmaster.utils.BonlalaUtils
import com.app.airmaster.utils.MmkvUtils
import com.hjq.permissions.XXPermissions

class AutoConnViewModel : CommViewModel() {


    fun retryConnDevice(activity : Activity){
        val connBleMac = MmkvUtils.getConnDeviceMac()
        if (!BikeUtils.isEmpty(connBleMac)) {
            //是否已经连接
            val isConn = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED
            if (isConn) {
                return
            }

            verifyScanFun(connBleMac,activity)
        }
    }


    //判断是否有位置权限了，没有请求权限
    private fun verifyScanFun(mac: String,context: Activity) {

        //判断蓝牙是否开启
        if (!BikeUtils.isBleEnable(context)) {
            BikeUtils.openBletooth(context)
            return
        }
        //判断权限
        val isPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermission) {
            XXPermissions.with(context).permission(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ).request { permissions, all ->
                connToDevice(mac)
            }
            // ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),0x00)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            XXPermissions.with(context).permission(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                )
            ).request { permissions, all ->
                //verifyScanFun()
            }
        }

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S_V2){
//            XXPermissions.with(this).permission(arrayOf(POST_NOTIFICATIONS)).request { permissions, allGranted ->  }
//        }


        //判断蓝牙是否打开
        val isOpenBle = BonlalaUtils.isOpenBlue(context)
        if (!isOpenBle) {
            BonlalaUtils.openBluetooth(context)
            return
        }
        connToDevice(mac)
    }

    private fun connToDevice(mac : String){
        Handler(Looper.getMainLooper()).postDelayed({
            val service = BaseApplication.getBaseApplication().connStatusService

            if(service != null){
             //   BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTING
                service.autoConnDevice(mac, false)
            }

        },1000)

    }
}