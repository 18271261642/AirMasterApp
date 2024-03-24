package com.app.airmaster.bean;


/**
 * 检测bean
 */
public class CheckBean {

    //下标
    private int position;

    //检测内容
    private String checkContent;

    //检测步骤 1,2，3,4
    private int checkStep;

    //检测状态 2 检测中，1成功，0失败
    private int checkStatus;

    //错误编码
    private int errorCode;

    //错误内容
    private String failDesc;


    //返回指令
    private String backHex;

    public CheckBean() {
    }

    public CheckBean(int position, String checkContent) {
        this.position = position;
        this.checkContent = checkContent;
    }


    public CheckBean(int position, String checkContent, int checkStatus) {
        this.position = position;
        this.checkContent = checkContent;
        this.checkStatus = checkStatus;
    }

    public CheckBean(int position, String checkContent, int checkStatus, String failDesc) {
        this.position = position;
        this.checkContent = checkContent;
        this.checkStatus = checkStatus;
        this.failDesc = failDesc;
    }


    public int getCheckStep() {
        return checkStep;
    }

    public void setCheckStep(int checkStep) {
        this.checkStep = checkStep;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCheckContent() {
        return checkContent;
    }

    public void setCheckContent(String checkContent) {
        this.checkContent = checkContent;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getFailDesc() {
        return failDesc;
    }

    public void setFailDesc(String failDesc) {
        this.failDesc = failDesc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getBackHex() {
        return backHex;
    }

    public void setBackHex(String backHex) {
        this.backHex = backHex;
    }

    @Override
    public String toString() {
        return "CheckBean{" +
                "position=" + position +
                ", checkContent='" + checkContent + '\'' +
                ", checkStep=" + checkStep +
                ", checkStatus=" + checkStatus +
                ", errorCode=" + errorCode +
                ", failDesc='" + failDesc + '\'' +
                '}';
    }
}
