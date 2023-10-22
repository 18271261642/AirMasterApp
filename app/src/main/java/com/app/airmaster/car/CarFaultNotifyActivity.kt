package com.app.airmaster.car

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.airmaster.R
import com.app.airmaster.action.AppActivity
import com.app.airmaster.car.adapter.CarFaultNotifyAdapter
import com.app.airmaster.dialog.ConfirmDialog

class CarFaultNotifyActivity :AppActivity() {


    private var faultNotifyRy : RecyclerView ?= null
    private var adapter : CarFaultNotifyAdapter ?= null
    private var list : MutableList<String> ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_car_fault_notify_layout
    }

    override fun initView() {
        faultNotifyRy = findViewById(R.id.faultNotifyRy)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        faultNotifyRy?.layoutManager = linearLayoutManager

        list = ArrayList<String>()
        adapter = CarFaultNotifyAdapter(this, list as ArrayList<String>)
        faultNotifyRy?.adapter = adapter

        findViewById<ImageView>(R.id.notifyBackImg).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.notifyClearTv).setOnClickListener {
            showClearDialog()
        }
    }

    override fun initData() {
        list?.clear()
        list?.add("胎压检查系统故障")
        list?.add("大灯故障")
        list?.add("电瓶电压过低")
        list?.add("天窗无法关闭故障")
        list?.add("后备箱无法关闭")
        list?.add("胎压检查系统故障")
        adapter?.notifyDataSetChanged()
    }

    private fun showClearDialog(){
        val dialog = ConfirmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setOnCommClickListener{
            dialog.dismiss()
            if(it == 0x01){
                list?.clear()
                adapter?.notifyDataSetChanged()
            }

        }
    }
}