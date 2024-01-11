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


    /**左前高度尺**/
    private int leftFrontHeightRuler;
    /**右前高度尺**/
    private int rightFrontHeightRuler;
    /**左后高度尺**/
    private int leftAfterHeightRuler;
    /**右后高度尺**/
    private int rightAfterHeightRuler;




    /**
     * 气缸气压值
     */
    private int cylinderPressure;




    /**
     * 电池电压 0.1v
     */
    private int batteryVal;

    /**
     * 气罐温度
     */
    private int airBottleTemperature;


    //ACC状态 0关闭；1开启
    private int accStatus;
    //ACC工作模式 0acc模式;1定时模式
    private int accWorkModel ;
    //是否在移动 0静止；1运动
    private int moveStatus;
    //是否休眠状态 0-非休眠；1-休眠
    private int sleepStatus ;
    //左前气压目标值(单位PSI)
    private int leftFrontGoalPressure ;
    //右前气压目标值(单位PSI)
    private int rightFrontGoalPressure ;
    //左后气压目标值(单位PSI)
    private int leftRearGoalPressure ;
    //右后气压目标值(单位PSI)
    private int rightRearGoalPressure ;
    //是否正在加热 0 false 1 true
    private int heatStatus ;

    /**设备故障状态码
     * bit0:系统未自检
     * bit1:加速度传感器故障
     * bit2:电池电压过高
     * bit3:电池电压过低
     * bit4:气泵1温度传感器故障
     * bit5:气泵2温度传感器故障
     * bit6:系统温度传感器故障
     */
    private byte deviceErrorCode ;
    /**
     * 左前故障状态码
     * bit0:高度传感器超量程
     * bit1:None
     * bit2:高度传感器线序错误
     * bit3:高度传感器测量范围过小
     * bit4:高度传感器装反
     * bit5:高度传感器故障
     * bit6:气压传感器故障
     * bit7:空气弹簧漏气
     */
    private byte leftFrontErrorCode ;
    //右前故障状态码 状态码
    private byte rightFrontErrorCode ;
    //左后故障状态码
    private byte leftRearErrorCode ;
    //右后故障状态码
    private byte rightRearErrorCode ;
    /**
     * 气罐故障状态码
     * bit0:气压传感器故障
     * bit1:气罐压力过低
     * bit2:气泵温度过高
     * bit3:气罐无法充气
     * bit4:气泵状态异常
     */
    private byte airBottleErrorCode ;
    //是否激活
    private int activationStatus ;




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

    public int getLeftFrontHeightRuler() {
        return leftFrontHeightRuler;
    }

    public void setLeftFrontHeightRuler(int leftFrontHeightRuler) {
        this.leftFrontHeightRuler = leftFrontHeightRuler;
    }

    public int getRightFrontHeightRuler() {
        return rightFrontHeightRuler;
    }

    public void setRightFrontHeightRuler(int rightFrontHeightRuler) {
        this.rightFrontHeightRuler = rightFrontHeightRuler;
    }

    public int getLeftAfterHeightRuler() {
        return leftAfterHeightRuler;
    }

    public void setLeftAfterHeightRuler(int leftAfterHeightRuler) {
        this.leftAfterHeightRuler = leftAfterHeightRuler;
    }

    public int getRightAfterHeightRuler() {
        return rightAfterHeightRuler;
    }

    public void setRightAfterHeightRuler(int rightAfterHeightRuler) {
        this.rightAfterHeightRuler = rightAfterHeightRuler;
    }


    public int getAccStatus() {
        return accStatus;
    }

    public void setAccStatus(int accStatus) {
        this.accStatus = accStatus;
    }

    public int getAccWorkModel() {
        return accWorkModel;
    }

    public void setAccWorkModel(int accWorkModel) {
        this.accWorkModel = accWorkModel;
    }

    public int getMoveStatus() {
        return moveStatus;
    }

    public void setMoveStatus(int moveStatus) {
        this.moveStatus = moveStatus;
    }

    public int getSleepStatus() {
        return sleepStatus;
    }

    public void setSleepStatus(int sleepStatus) {
        this.sleepStatus = sleepStatus;
    }

    public int getLeftFrontGoalPressure() {
        return leftFrontGoalPressure;
    }

    public void setLeftFrontGoalPressure(int leftFrontGoalPressure) {
        this.leftFrontGoalPressure = leftFrontGoalPressure;
    }

    public int getRightFrontGoalPressure() {
        return rightFrontGoalPressure;
    }

    public void setRightFrontGoalPressure(int rightFrontGoalPressure) {
        this.rightFrontGoalPressure = rightFrontGoalPressure;
    }

    public int getLeftRearGoalPressure() {
        return leftRearGoalPressure;
    }

    public void setLeftRearGoalPressure(int leftRearGoalPressure) {
        this.leftRearGoalPressure = leftRearGoalPressure;
    }

    public int getRightRearGoalPressure() {
        return rightRearGoalPressure;
    }

    public void setRightRearGoalPressure(int rightRearGoalPressure) {
        this.rightRearGoalPressure = rightRearGoalPressure;
    }

    public int getHeatStatus() {
        return heatStatus;
    }

    public void setHeatStatus(int heatStatus) {
        this.heatStatus = heatStatus;
    }

    public byte getDeviceErrorCode() {
        return deviceErrorCode;
    }

    public void setDeviceErrorCode(byte deviceErrorCode) {
        this.deviceErrorCode = deviceErrorCode;
    }

    public byte getLeftFrontErrorCode() {
        return leftFrontErrorCode;
    }

    public void setLeftFrontErrorCode(byte leftFrontErrorCode) {
        this.leftFrontErrorCode = leftFrontErrorCode;
    }

    public byte getRightFrontErrorCode() {
        return rightFrontErrorCode;
    }

    public void setRightFrontErrorCode(byte rightFrontErrorCode) {
        this.rightFrontErrorCode = rightFrontErrorCode;
    }

    public byte getLeftRearErrorCode() {
        return leftRearErrorCode;
    }

    public void setLeftRearErrorCode(byte leftRearErrorCode) {
        this.leftRearErrorCode = leftRearErrorCode;
    }

    public byte getRightRearErrorCode() {
        return rightRearErrorCode;
    }

    public void setRightRearErrorCode(byte rightRearErrorCode) {
        this.rightRearErrorCode = rightRearErrorCode;
    }

    public byte getAirBottleErrorCode() {
        return airBottleErrorCode;
    }

    public void setAirBottleErrorCode(byte airBottleErrorCode) {
        this.airBottleErrorCode = airBottleErrorCode;
    }

    public int getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(int activationStatus) {
        this.activationStatus = activationStatus;
    }

    @Override
    public String toString() {
        return "AutoBackBean{" +
                "工作模式=" + workModel +
                ", 自检模式下自检项目=" + selfCheckModel +
                ", 当前处于预置位位置=" + curPos +
                ", 左前气压值=" + leftPressure +
                ", 右前气压值=" + rightPressure +
                ", 左后气压值=" + leftRearPressure +
                ", 右后气压值=" + rightRearPressure +
                ", 气缸气压值=" + leftFrontHeightRuler +
                ", 左前高度尺工作量=" + rightFrontHeightRuler +
                ", 右前高度尺工作量=" + leftAfterHeightRuler +
                ", 左后高度尺工作量=" + rightAfterHeightRuler +
                ", 右后高度尺工作量=" + cylinderPressure +
                ", 电池电压(单位0.1V)=" + batteryVal +
                ", 气罐温度(单位摄氏度)=" + airBottleTemperature +
                ", ACC状态=" + accStatus +
                ", ACC工作模式=" + accWorkModel +
                ", 是否正在移动=" + moveStatus +
                ", 是否休眠状态=" + sleepStatus +
                ", 左前气压目标值=" + leftFrontGoalPressure +
                ", 右前气压目标值=" + rightFrontGoalPressure +
                ", 左后气压目标值=" + leftRearGoalPressure +
                ", 右后气压目标值=" + rightRearGoalPressure +
                ", 是否正在加热=" + heatStatus +
                ", 设备故障状态码=" + deviceErrorCode +
                ", 左前故障状态码=" + leftFrontErrorCode +
                ", 右前故障状态码=" + rightFrontErrorCode +
                ", 左后故障状态码=" + leftRearErrorCode +
                ", 右后故障状态码=" + rightRearErrorCode +
                ", 气罐故障状态码=" + airBottleErrorCode +
                ", 是否激活=" + activationStatus +
                '}';
    }
}
