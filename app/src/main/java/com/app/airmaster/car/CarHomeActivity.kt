package com.app.airmaster.car

import android.content.Intent
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.airmaster.BaseApplication
import com.app.airmaster.R
import com.app.airmaster.action.ActivityManager
import com.app.airmaster.action.AppActivity
import com.app.airmaster.action.AppFragment
import com.app.airmaster.adapter.NavigationAdapter
import com.app.airmaster.car.fragment.HomeAirFragment
import com.app.airmaster.car.fragment.HomeControlFragment
import com.app.airmaster.car.fragment.HomeSettingFragment
import com.blala.blalable.car.AutoBackBean
import com.bonlala.base.FragmentPagerAdapter
import com.hjq.toast.ToastUtils
import timber.log.Timber

/**
 *
 * Created by Admin
 *Date 2023/7/14
 */
class CarHomeActivity : AppActivity() ,NavigationAdapter.OnNavigationListener{

    private val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
    private val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"

    private var mViewPager: ViewPager? = null
    private var mNavigationView: RecyclerView? = null

    private var mNavigationAdapter: NavigationAdapter? = null
    private var mPagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null


    private var autoListener : OnHomeAutoBackListener ?= null

    fun setHomeAutoListener(c : OnHomeAutoBackListener){
        this.autoListener = c
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




        BaseApplication.getBaseApplication().bleOperate.setAutoBackDataListener {
            BaseApplication.getBaseApplication().autoBackBean = it
            autoListener?.backAutoData(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mPagerAdapter?.let {
            switchFragment(it.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
        }
    }

    private fun switchFragment(fragmentIndex: Int) {
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 过滤按键动作
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                mExitTime = System.currentTimeMillis()
                ToastUtils.show(resources.getString(R.string.string_double_click_exit))
                return true
            } else {
                ActivityManager.getInstance().finishAllActivities()
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    interface OnHomeAutoBackListener{
        fun backAutoData(autoBean : AutoBackBean)
    }
}