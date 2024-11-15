package com.app.airmaster.bean;

import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestApi;

public class CdkBean implements IRequestApi {


    @NonNull
    @Override
    public String getApi() {
        return "cdkey/record/save";
    }

    private String cdkey;

    private String productSn;


    public CdkBean setParams(String cdkey, String productSn) {
        this.cdkey = cdkey;
        this.productSn = productSn;
        return this;
    }
}
