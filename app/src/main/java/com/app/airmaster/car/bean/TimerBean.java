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
}
