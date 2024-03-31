package com.app.airmaster.car.bean;

/**
 * 用于升级touchpad的状态bean
 */
public class SyncTouchpadStatusBean {

    //是否同步正常，同步不正常提示错误信息
    private boolean isSyncValid;

    private String inValidDesc;


    public SyncTouchpadStatusBean() {
    }

    public SyncTouchpadStatusBean(boolean isSyncValid, String inValidDesc) {
        this.isSyncValid = isSyncValid;
        this.inValidDesc = inValidDesc;
    }

    public SyncTouchpadStatusBean(boolean isSyncValid, int syncProgress) {
        this.isSyncValid = isSyncValid;
        this.syncProgress = syncProgress;
    }

    public SyncTouchpadStatusBean(boolean isSyncValid, String inValidDesc, int syncProgress, String syncResultDesc) {
        this.isSyncValid = isSyncValid;
        this.inValidDesc = inValidDesc;
        this.syncProgress = syncProgress;
        this.syncResultDesc = syncResultDesc;
    }

    //进度
    private int syncProgress;

    //最总同步的结果，成功或不成功的描述
    private String syncResultDesc;

    public boolean isSyncValid() {
        return isSyncValid;
    }

    public void setSyncValid(boolean syncValid) {
        isSyncValid = syncValid;
    }

    public String getInValidDesc() {
        return inValidDesc;
    }

    public void setInValidDesc(String inValidDesc) {
        this.inValidDesc = inValidDesc;
    }

    public int getSyncProgress() {
        return syncProgress;
    }

    public void setSyncProgress(int syncProgress) {
        this.syncProgress = syncProgress;
    }

    public String getSyncResultDesc() {
        return syncResultDesc;
    }

    public void setSyncResultDesc(String syncResultDesc) {
        this.syncResultDesc = syncResultDesc;
    }
}
