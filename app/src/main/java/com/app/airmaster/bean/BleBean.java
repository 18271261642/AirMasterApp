package com.app.airmaster.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Admin
 * Date 2022/8/4
 * @author Admin
 */
public class BleBean {

    private BluetoothDevice bluetoothDevice;

    private int rssi;

    //0正常，1连接中，2连接成功
    private int connStatus;

    //是否已经连接
    private boolean isConnected;


    //是否是旋钮屏
    private boolean isScreenDevice;


    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public BleBean(BluetoothDevice bluetoothDevice, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;

    }

    public BleBean(BluetoothDevice bluetoothDevice, int rssi, boolean isScreenDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.isScreenDevice = isScreenDevice;
    }

    public int isConnStatus() {
        return connStatus;
    }

    public void setConnStatus(int connStatus) {
        this.connStatus = connStatus;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isScreenDevice() {
        return isScreenDevice;
    }

    public void setScreenDevice(boolean screenDevice) {
        isScreenDevice = screenDevice;
    }
}
