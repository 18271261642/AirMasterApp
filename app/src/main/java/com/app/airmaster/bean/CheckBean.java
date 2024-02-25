package com.app.airmaster.bean;


/**
 * 检测bean
 */
public class CheckBean {

    //下标
    private int position;

    //检测内容
    private String checkContent;

    //检测状态 2 检测中，1成功，0失败
    private int checkStatus;

    //错误内容
    private String failDesc;

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


    @Override
    public String toString() {
        return "CheckBean{" +
                "position=" + position +
                ", checkContent='" + checkContent + '\'' +
                ", checkStatus=" + checkStatus +
                ", failDesc='" + failDesc + '\'' +
                '}';
    }
}
