package com.app.airmaster.car


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.ActivityManager
import com.app.airmaster.action.AppActivity
import com.app.airmaster.action.AppFragment
import com.app.airmaster.adapter.NavigationAdapter
import com.app.airmaster.ble.ConnStatus
import com.app.airmaster.car.fragment.HomeAirFragment
import com.app.airmaster.car.fragment.HomeControlFragment
import com.app.airmaster.car.fragment.HomeSettingFragment
import com.app.airmaster.viewmodel.AutoConnViewModel
import com.app.airmaster.viewmodel.ControlViewModel
import com.blala.blalable.BleConstant
import com.blala.blalable.car.AutoBackBean
import com.bonlala.base.FragmentPagerAdapter
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.system.exitProcess


/**
 *
 * Created by Admin
 *Date 2023/7/14
 */
class CarHomeActivity : AppActivity() ,NavigationAdapter.OnNavigationListener{

    private var autoConnViewModel : AutoConnViewModel ?= null
    private var controlViewModel : ControlViewModel ?= null


    private val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
    private val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"

    private var mViewPager: ViewPager? = null
    private var mNavigationView: RecyclerView? = null

    private var mNavigationAdapter: NavigationAdapter? = null
    private var mPagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null


    private var onHomeConnListener : OnHomeConnStatusListener ?= null

    private var autoListener : OnHomeAutoBackListener ?= null

    fun setHomeAutoListener(c : OnHomeAutoBackListener){
        this.autoListener = c
    }

    fun setHomeConnListener(a : OnHomeConnStatusListener){
        this.onHomeConnListener = a
    }

    private var selectorIndex = 0


    override fun getLayoutId(): Int {
        return R.layout.activity_car_home_layout
    }

    override fun initView() {
        mViewPager = findViewById(R.id.vp_home_pager)
        mNavigationView = findViewById(R.id.rv_home_navigation)
    }

    override fun initData() {

        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        intentFilter.addAction(BleConstant.BLE_START_SCAN_ACTION)
        registerReceiver(broadcastReceiver,intentFilter)
        controlViewModel = ViewModelProvider(this)[ControlViewModel::class.java]
        autoConnViewModel = ViewModelProvider(this)[AutoConnViewModel::class.java]
        mNavigationAdapter = NavigationAdapter(this).apply {
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.string_home_air),
                    ContextCompat.getDrawable(this@CarHomeActivity, R.drawable.home_air_selector)
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.string_home_control),
                    ContextCompat.getDrawable(
                        this@CarHomeActivity,
                        R.drawable.home_control_selector
                    )
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    getString(R.string.string_home_setting),
                    ContextCompat.getDrawable(this@CarHomeActivity, R.drawable.home_set_selector)
                )
            )
            setOnNavigationListener(this@CarHomeActivity)
            mNavigationView?.adapter = this
        }
        mPagerAdapter = FragmentPagerAdapter<AppFragment<*>>(this).apply {
            addFragment(HomeAirFragment.getInstance())
            addFragment(HomeControlFragment.getInstance())
            addFragment(HomeSettingFragment.getInstance())
            mViewPager?.adapter = this
        }
        onNewIntent(intent)

        GlobalScope.launch {
            delay(1500)
            autoConnViewModel?.retryConnDevice(this@CarHomeActivity)
        }
        switchFragment(1)
    }



    override fun onResume() {
        super.onResume()
        BaseApplication.getBaseApplication().bleOperate.setClearAutoBack()
        Timber.e("------onResumt----")
        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener {
            Timber.e("---------自动返回数据=$it")
            BaseApplication.getBaseApplication().autoBackBean = it
            autoListener?.backAutoData(it)
        }
        controlViewModel?.writeCommonFunction()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mPagerAdapter?.let {
            switchFragment(it.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
        }
    }

     fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            0, 1, 2 -> {
                Timber.e("----switchFragment=" + fragmentIndex)
                mViewPager?.currentItem = fragmentIndex
                mNavigationAdapter?.setSelectedPosition(fragmentIndex)

            }
        }
    }

    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2 -> {
                Timber.e("----onNavigationItemSelected=" + position)
                mViewPager?.currentItem = position

                true
            }

            else -> false
        }
    }

    private var mExitTime: Long = 0

//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        // 过滤按键动作
//        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
//            if (System.currentTimeMillis() - mExitTime > 2000) {
//                mExitTime = System.currentTimeMillis()
//                ToastUtils.show(resources.getString(R.string.string_double_click_exit))
//                return true
//            } else {
//                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
//                ActivityManager.getInstance().finishAllActivities()
//                finish()
//            }
//        }
//        return super.onKeyDown(keyCode, event)
//    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            mExitTime = System.currentTimeMillis()
            ToastUtils.show(resources.getString(R.string.string_double_click_exit))
            return
        }

        // 移动到上一个任务栈，避免侧滑引起的不良反应
       // moveTaskToBack(false)
        postDelayed({
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities()
            exitProcess(0)
            finish()
        }, 300)
    }


    interface OnHomeAutoBackListener{
        fun backAutoData(autoBean : AutoBackBean)
    }

    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            Timber.e("---------acdtion="+action)
            if(action == BleConstant.BLE_CONNECTED_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_success))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
                onHomeConnListener?.onConn(true)
                controlViewModel?.writeCommonFunction()
            }
            if(action == BleConstant.BLE_DIS_CONNECT_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_disconn))
                onHomeConnListener?.onConn(false)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }


    interface OnHomeConnStatusListener{
        fun onConn(isConn : Boolean)
    }
}