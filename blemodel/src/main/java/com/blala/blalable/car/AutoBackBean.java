package com.blala.blalable.car;


/**
 * 设备自动返回信息
 */
public class AutoBackBean {

    /**工作模式
     * 0-工作模式
     * 1-自动自检模式
     * 2-手动自检模式
     * 3-维修模式
     */
    private int workModel;

    /**
     * 自检模式下自检项目
     * 1:检测最低高度
     * 2.检测车身水平状态
     * 3.检测传感器
     * 4.检测最高高度
     * 5.检测线束状态
     * 6.检测动态调整
     */
    private int selfCheckModel;

    /**
     * 预置位置
     * Bit0~bit3:
     * 0:手动位置
     * 1~5:档位1~5
     * Bit4~bit7:
     * 0:正常
     * 1:正在调整中…
     * 2:档位调整失败
     */
    private int curPos;


    /**
     * 左前气压值
     */
    private int leftPressure;

    /**
     * 右前气压值
     */
    private int rightPressure;

    /**
     * 左后气压值
     */
    private int leftRearPressure;

    /**
     * 右后气压值
     */
    private int rightRearPressure;

    /**
     * 气缸气压值
     */
    private int cylinderPressure;

    /**
     * 左前高度尺工作量
     */
    private int leftFrontHeight;


    /**
     * 电池电压 0.1v
     */
    private int batteryVal;

    /**
     * 气罐温度
     */
    private int airBottleTemperature;


    public int getWorkModel() {
        return workModel;
    }

    public void setWorkModel(int workModel) {
        this.workModel = workModel;
    }

    public int getSelfCheckModel() {
        return selfCheckModel;
    }

    public void setSelfCheckModel(int selfCheckModel) {
        this.selfCheckModel = selfCheckModel;
    }

    public int getCurPos() {
        return curPos;
    }

    public void setCurPos(int curPos) {
        this.curPos = curPos;
    }

    public int getLeftPressure() {
        return leftPressure;
    }

    public void setLeftPressure(int leftPressure) {
        this.leftPressure = leftPressure;
    }

    public int getRightPressure() {
        return rightPressure;
    }

    public void setRightPressure(int rightPressure) {
        this.rightPressure = rightPressure;
    }

    public int getLeftRearPressure() {
        return leftRearPressure;
    }

    public void setLeftRearPressure(int leftRearPressure) {
        this.leftRearPressure = leftRearPressure;
    }

    public int getRightRearPressure() {
        return rightRearPressure;
    }

    public void setRightRearPressure(int rightRearPressure) {
        this.rightRearPressure = rightRearPressure;
    }

    public int getCylinderPressure() {
        return cylinderPressure;
    }

    public void setCylinderPressure(int cylinderPressure) {
        this.cylinderPressure = cylinderPressure;
    }

    public int getLeftFrontHeight() {
        return leftFrontHeight;
    }

    public void setLeftFrontHeight(int leftFrontHeight) {
        this.leftFrontHeight = leftFrontHeight;
    }

    public int getBatteryVal() {
        return batteryVal;
    }

    public void setBatteryVal(int batteryVal) {
        this.batteryVal = batteryVal;
    }

    public int getAirBottleTemperature() {
        return airBottleTemperature;
    }

    public void setAirBottleTemperature(int airBottleTemperature) {
        this.airBottleTemperature = airBottleTemperature;
    }

    @Override
    public String toString() {
        return "AutoBackBean{" +
                "workModel=" + workModel +
                ", selfCheckModel=" + selfCheckModel +
                ", curPos=" + curPos +
                ", leftPressure=" + leftPressure +
                ", rightPressure=" + rightPressure +
                ", leftRearPressure=" + leftRearPressure +
                ", rightRearPressure=" + rightRearPressure +
                ", cylinderPressure=" + cylinderPressure +
                ", leftFrontHeight=" + leftFrontHeight +
                ", batteryVal=" + batteryVal +
                ", airBottleTemperature=" + airBottleTemperature +
                '}';
    }
}
