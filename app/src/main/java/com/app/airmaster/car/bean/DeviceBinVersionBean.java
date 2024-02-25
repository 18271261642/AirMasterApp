package com.app.airmaster.car.bean;


/**
 * 设备的固件信息
 */
public class DeviceBinVersionBean {

    //产品编码
    private String productCode;

    //固件版本
    private String versionStr;
    //版本int cdoe
    private int versionCode;



    //BIN文件的匹配码
    private String binCode;

    //识别码
    private String identificationCode;

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getVersionStr() {
        return versionStr;
    }

    public void setVersionStr(String versionStr) {
        this.versionStr = versionStr;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }
}
