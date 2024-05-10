package com.app.airmaster.car.bean;

import java.util.HashMap;

/**
 * 发送指令返回状态
 */
public class AutoSetBean {

    //高度记忆模式 0g高度记忆；1压力记忆
    private int  modelType ;
    //ACC启停定时进入休眠时间(单位分钟)
    private int  sleepTime ;
    //电池高压保护开关
    private boolean isHeightVoltage;
    //电池低压保护门限(单位0.1V)
    private int  lowVoltage ;
    //点火联动预置位 0:关闭
    //1~4:关联预置位
    private int  accTurnOnValue ;
    //熄火联动设置
    private int accTurnOffValue;

    //水平静止状态下超过5S,自动预置位调节开关 0不能 1能
    private int  presetPosition;
    //气罐高压值(单位PSI)
    private int  gasTankHeightPressure ;
    //气罐低压值
    private int  gasTankLowPressure ;
    //气罐水分离模式
    private int  gasTankWaterModel ;
    //最低预置位
    private int  lowestPosition ;
    //ACC模式
    private int  accModel ;
    //运动中-左前最低保护气压
    private int  leftFrontProtectPressure ;
    //运动中-右前最低保护气压
    private int  rightFrontProtectPressure;
    //运动中-左后最低保护气压
    private int  leftRearProtectPressure ;
    //运动中-右后最低保护气压
    private int  rightRearProtectPressure ;
    //自动气压调平功能
    private int  pressureBalance ;
    //自动气压调平功能-级别
    private int  autoPressureBalanceLevel ;


    //档位的遇设位
    private HashMap<Integer,GearBean> gearHashMap;



    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isHeightVoltage() {
        return isHeightVoltage;
    }

    public void setHeightVoltage(boolean heightVoltage) {
        isHeightVoltage = heightVoltage;
    }

    public int getLowVoltage() {
        return lowVoltage;
    }

    public void setLowVoltage(int lowVoltage) {
        this.lowVoltage = lowVoltage;
    }

    public int getAccTurnOnValue() {
        return accTurnOnValue;
    }

    public void setAccTurnOnValue(int accTurnOnValue) {
        this.accTurnOnValue = accTurnOnValue;
    }

    public int getPresetPosition() {
        return presetPosition;
    }

    public void setPresetPosition(int presetPosition) {
        this.presetPosition = presetPosition;
    }

    public int getGasTankHeightPressure() {
        return gasTankHeightPressure;
    }

    public void setGasTankHeightPressure(int gasTankHeightPressure) {
        this.gasTankHeightPressure = gasTankHeightPressure;
    }

    public int getGasTankLowPressure() {
        return gasTankLowPressure;
    }

    public void setGasTankLowPressure(int gasTankLowPressure) {
        this.gasTankLowPressure = gasTankLowPressure;
    }

    public int getGasTankWaterModel() {
        return gasTankWaterModel;
    }

    public void setGasTankWaterModel(int gasTankWaterModel) {
        this.gasTankWaterModel = gasTankWaterModel;
    }

    public int getLowestPosition() {
        return lowestPosition;
    }

    public void setLowestPosition(int lowestPosition) {
        this.lowestPosition = lowestPosition;
    }

    public int getAccModel() {
        return accModel;
    }

    public void setAccModel(int accModel) {
        this.accModel = accModel;
    }

    public int getLeftFrontProtectPressure() {
        return leftFrontProtectPressure;
    }

    public void setLeftFrontProtectPressure(int leftFrontProtectPressure) {
        this.leftFrontProtectPressure = leftFrontProtectPressure;
    }

    public int getRightFrontProtectPressure() {
        return rightFrontProtectPressure;
    }

    public void setRightFrontProtectPressure(int rightFrontProtectPressure) {
        this.rightFrontProtectPressure = rightFrontProtectPressure;
    }

    public int getLeftRearProtectPressure() {
        return leftRearProtectPressure;
    }

    public void setLeftRearProtectPressure(int leftRearProtectPressure) {
        this.leftRearProtectPressure = leftRearProtectPressure;
    }

    public int getRightRearProtectPressure() {
        return rightRearProtectPressure;
    }

    public void setRightRearProtectPressure(int rightRearProtectPressure) {
        this.rightRearProtectPressure = rightRearProtectPressure;
    }

    public int getPressureBalance() {
        return pressureBalance;
    }

    public void setPressureBalance(int pressureBalance) {
        this.pressureBalance = pressureBalance;
    }

    public int getAutoPressureBalanceLevel() {
        return autoPressureBalanceLevel;
    }

    public void setAutoPressureBalanceLevel(int autoPressureBalanceLevel) {
        this.autoPressureBalanceLevel = autoPressureBalanceLevel;
    }

    public int getAccTurnOffValue() {
        return accTurnOffValue;
    }

    public void setAccTurnOffValue(int accTurnOffValue) {
        this.accTurnOffValue = accTurnOffValue;
    }

    public HashMap<Integer, GearBean> getGearHashMap() {
        return gearHashMap;
    }

    public void setGearHashMap(HashMap<Integer, GearBean> gearHashMap) {
        this.gearHashMap = gearHashMap;
    }

    @Override
    public String toString() {
        return "AutoSetBean{" +
                "modelType=" + modelType +
                ", sleepTime=" + sleepTime +
                ", isHeightVoltage=" + isHeightVoltage +
                ", lowVoltage=" + lowVoltage +
                ", accTurnOnValue=" + accTurnOnValue +
                ", accTurnOffValue=" + accTurnOffValue +
                ", presetPosition=" + presetPosition +
                ", gasTankHeightPressure=" + gasTankHeightPressure +
                ", gasTankLowPressure=" + gasTankLowPressure +
                ", gasTankWaterModel=" + gasTankWaterModel +
                ", lowestPosition=" + lowestPosition +
                ", accModel=" + accModel +
                ", leftFrontProtectPressure=" + leftFrontProtectPressure +
                ", rightFrontProtectPressure=" + rightFrontProtectPressure +
                ", leftRearProtectPressure=" + leftRearProtectPressure +
                ", rightRearProtectPressure=" + rightRearProtectPressure +
                ", pressureBalance=" + pressureBalance +
                ", autoPressureBalanceLevel=" + autoPressureBalanceLevel +
                '}';
    }


    public static class GearBean{
        //左前
        private int leftFront;
        private int rightFront;
        private int leftRear;
        private int rightRear;

        public GearBean() {
        }

        public GearBean(int leftFront, int rightFront, int leftRear, int rightRear) {
            this.leftFront = leftFront;
            this.rightFront = rightFront;
            this.leftRear = leftRear;
            this.rightRear = rightRear;
        }

        public int getLeftFront() {
            return leftFront;
        }

        public void setLeftFront(int leftFront) {
            this.leftFront = leftFront;
        }

        public int getRightFront() {
            return rightFront;
        }

        public void setRightFront(int rightFront) {
            this.rightFront = rightFront;
        }

        public int getLeftRear() {
            return leftRear;
        }

        public void setLeftRear(int leftRear) {
            this.leftRear = leftRear;
        }

        public int getRightRear() {
            return rightRear;
        }

        public void setRightRear(int rightRear) {
            this.rightRear = rightRear;
        }
    }
}
