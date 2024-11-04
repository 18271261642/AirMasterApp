package com.app.airmaster.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.app.airmaster.BaseApplication
import com.app.airmaster.utils.GsonUtils
import com.hjq.http.EasyHttp
import com.hjq.http.EasyLog
import com.hjq.http.listener.OnHttpListener
import org.json.JSONObject
import java.lang.Exception

/**
 * 过滤设备的viewModel
 */
class FilterViewModel : CommViewModel() {

    var filterState = SingleLiveEvent<Boolean>()

    fun getFilterDevice(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("getBroadcastId?countryType=0").request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObj = JSONObject(result)
                if(jsonObj.has("code") && jsonObj.get("code")==200){
                    val data = jsonObj.getString("data")
                    val list = GsonUtils.getGsonObject<List<String>>(data)
                    if(list?.isNotEmpty()==true){
                        BaseApplication.getBaseApplication().filterList = list
                    }
                }
                filterState.postValue(true)
            }

            override fun onFail(e: Exception?) {
                filterState.postValue(true)
            }

        })
    }
}