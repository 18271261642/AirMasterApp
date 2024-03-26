package com.app.airmaster.car.bean;


/**
 * 设备的固件信息
 */
public class DeviceBinVersionBean {

    /**名称**/
    private String deviceName;

    /**mac**/
    private String deviceMac;


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



    /******MCU********/
    private String mcuBroadcastId;
    private String mcuIdentificationCode;
    private String mcuVersionCode;
    private int mcuVersionCodeInt;


    /**显示屏mcu**/
    private String screenMcuBroadcastId;
    private String screenMcuIdentificationCode;
    private String screenVersionCode;
    private int screenMcuVersionCodeInt;


    /**其它mcu**/
    private String otherScreenMcuBroadcastId;
    private String otherMcuIdentificationCode;
    private String otherScreenVersionCode;
    private int otherMcuVersionCodeInt;




    private String sourceStr;



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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getMcuBroadcastId() {
        return mcuBroadcastId;
    }

    public void setMcuBroadcastId(String mcuBroadcastId) {
        this.mcuBroadcastId = mcuBroadcastId;
    }

    public String getMcuIdentificationCode() {
        return mcuIdentificationCode;
    }

    public void setMcuIdentificationCode(String mcuIdentificationCode) {
        this.mcuIdentificationCode = mcuIdentificationCode;
    }

    public String getMcuVersionCode() {
        return mcuVersionCode;
    }

    public void setMcuVersionCode(String mcuVersionCode) {
        this.mcuVersionCode = mcuVersionCode;
    }

    public int getMcuVersionCodeInt() {
        return mcuVersionCodeInt;
    }

    public void setMcuVersionCodeInt(int mcuVersionCodeInt) {
        this.mcuVersionCodeInt = mcuVersionCodeInt;
    }

    public String getScreenMcuBroadcastId() {
        return screenMcuBroadcastId;
    }

    public void setScreenMcuBroadcastId(String screenMcuBroadcastId) {
        this.screenMcuBroadcastId = screenMcuBroadcastId;
    }

    public String getScreenMcuIdentificationCode() {
        return screenMcuIdentificationCode;
    }

    public void setScreenMcuIdentificationCode(String screenMcuIdentificationCode) {
        this.screenMcuIdentificationCode = screenMcuIdentificationCode;
    }

    public String getScreenVersionCode() {
        return screenVersionCode;
    }

    public void setScreenVersionCode(String screenVersionCode) {
        this.screenVersionCode = screenVersionCode;
    }

    public int getScreenMcuVersionCodeInt() {
        return screenMcuVersionCodeInt;
    }

    public void setScreenMcuVersionCodeInt(int screenMcuVersionCodeInt) {
        this.screenMcuVersionCodeInt = screenMcuVersionCodeInt;
    }

    public String getOtherScreenMcuBroadcastId() {
        return otherScreenMcuBroadcastId;
    }

    public void setOtherScreenMcuBroadcastId(String otherScreenMcuBroadcastId) {
        this.otherScreenMcuBroadcastId = otherScreenMcuBroadcastId;
    }

    public String getOtherMcuIdentificationCode() {
        return otherMcuIdentificationCode;
    }

    public void setOtherMcuIdentificationCode(String otherMcuIdentificationCode) {
        this.otherMcuIdentificationCode = otherMcuIdentificationCode;
    }

    public String getOtherScreenVersionCode() {
        return otherScreenVersionCode;
    }

    public void setOtherScreenVersionCode(String otherScreenVersionCode) {
        this.otherScreenVersionCode = otherScreenVersionCode;
    }

    public int getOtherMcuVersionCodeInt() {
        return otherMcuVersionCodeInt;
    }

    public void setOtherMcuVersionCodeInt(int otherMcuVersionCodeInt) {
        this.otherMcuVersionCodeInt = otherMcuVersionCodeInt;
    }

    public String getSourceStr() {
        return sourceStr;
    }

    public void setSourceStr(String sourceStr) {
        this.sourceStr = sourceStr;
    }
}
