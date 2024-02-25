package com.app.airmaster.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.app.airmaster.R
import com.app.airmaster.bean.CheckBean

class CarCheckViewModel : ControlViewModel() {


    //组装检测的数据
    var carCheckData = SingleLiveEvent<MutableList<CheckBean>>()


    fun dealCheckData(context: Context){
        val list = mutableListOf<CheckBean>()
        val bean1 = CheckBean(1,context.resources.getString(R.string.string_check1_desc),2)
        val bean2 = CheckBean(2,context.resources.getString(R.string.string_check2_desc),2)
        val bean3 = CheckBean(3,"",2)
        val bean4 = CheckBean(4,context.resources.getString(R.string.string_check4_desc),2)
        val bean5 = CheckBean(5,context.resources.getString(R.string.string_check5_desc),2)
        val bean6 = CheckBean(5,context.resources.getString(R.string.string_check6_desc),2)
        list.add(bean1)
        list.add(bean2)
        list.add(bean3)
        list.add(bean4)
        list.add(bean5)
        list.add(bean6)
        carCheckData.postValue(list)

    }



}