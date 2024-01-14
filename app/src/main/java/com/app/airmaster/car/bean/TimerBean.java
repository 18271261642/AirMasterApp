package com.app.airmaster.car.bean;

/**
 * Created by Admin
 * Date 2023/7/14
 * @author Admin
 */
public class TimerBean {

    private String timeValue;

    private int time;

    private boolean isChecked;

    //是否是高度记忆模式，高度记忆模式更换图片
    private boolean isHeightMemory;

    public TimerBean() {
    }



    public TimerBean(String timeValue, boolean isChecked) {
        this.timeValue = timeValue;
        this.isChecked = isChecked;
    }

    public TimerBean(String timeValue, int time, boolean isChecked) {
        this.timeValue = timeValue;
        this.time = time;
        this.isChecked = isChecked;
    }


    public TimerBean(int time, boolean isHeightMemory,String timeValue) {
        this.time = time;
        this.isHeightMemory = isHeightMemory;
        this.timeValue = timeValue;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public boolean isHeightMemory() {
        return isHeightMemory;
    }

    public void setHeightMemory(boolean heightMemory) {
        isHeightMemory = heightMemory;
    }
}
