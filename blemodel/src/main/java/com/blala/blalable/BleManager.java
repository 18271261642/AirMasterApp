package com.blala.blalable;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.blala.blalable.car.AutoBackBean;
import com.blala.blalable.car.OnCarAutoBackListener;
import com.blala.blalable.listener.BleConnStatusListener;
import com.blala.blalable.listener.ConnStatusListener;
import com.blala.blalable.listener.InterfaceManager;
import com.blala.blalable.listener.OnBleStatusBackListener;
import com.blala.blalable.listener.OnCarWriteBackListener;
import com.blala.blalable.listener.OnCommBackDataListener;
import com.blala.blalable.listener.OnExerciseDataListener;
import com.blala.blalable.listener.OnMeasureDataListener;
import com.blala.blalable.listener.OnRealTimeDataListener;
import com.blala.blalable.listener.OnSendWriteDataListener;
import com.blala.blalable.listener.WriteBack24HourDataListener;
import com.blala.blalable.listener.WriteBackDataListener;
import com.google.gson.Gson;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.UUID;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2021/9/3
 * @author Admin
 */
public class BleManager {

    private static final String TAG = "BleManager";


    private static final String SAVE_BLE_MAC_KEY = "blala_ble_mac";

    private static BleManager bleManager;
    private static BluetoothClient bluetoothClient;

    private static Context mContext;


    private final StringBuffer stringBuffer = new StringBuffer();

    private final BleConstant bleConstant = new BleConstant();


    private BleConnStatusListener bleConnStatusListener;


    private OnCarAutoBackListener onCarAutoBackListener;


    private final InterfaceManager interfaceManager = new InterfaceManager();

    public void setBleConnStatusListener(BleConnStatusListener bleConnStatusListener) {
        this.bleConnStatusListener = bleConnStatusListener;
    }

    public void setOnBleBackListener (OnBleStatusBackListener onBleBackListener){
        this.interfaceManager.onBleBackListener = onBleBackListener;
    }


    public void setOnCarWriteBackListener(OnCarWriteBackListener onCarWriteBackListener){
        this.interfaceManager.onCarWriteBackListener = onCarWriteBackListener;
    }


    public void setOnCarAutoBackListener(OnCarAutoBackListener onCarAutoBackListener) {
        this.onCarAutoBackListener = onCarAutoBackListener;
    }

    public void setOnSendWriteListener(OnSendWriteDataListener onSendWriteListener){
        this.interfaceManager.onSendWriteDataListener = onSendWriteListener;
    }

    //实时心率数据
    public void setOnRealTimeDataListener(OnRealTimeDataListener onRealTimeDataListener) {
        this.interfaceManager.onRealTimeDataListener = onRealTimeDataListener;
    }


    /**测量数据返回**/
    public void setOnMeasureDataListener(OnMeasureDataListener onMeasureDataListener){
        this.interfaceManager.onMeasureDataListener = onMeasureDataListener;
    }

    /**锻炼数据**/
    public void setOnExerciseDataListener(OnExerciseDataListener onExerciseDataListener){
        this.interfaceManager.onExerciseDataListener = onExerciseDataListener;
    }

    public static BleManager getInstance(Context context){
        mContext = context;
        bluetoothClient = new BluetoothClient(mContext);
        if(bleManager == null){
            synchronized (BleManager.class){
                if(bleManager == null){
                    bleManager = new BleManager();
                }
            }
        }
        return bleManager;
    }


    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                sendCommBroadcast(-1);
            }
        }
    };



    public BluetoothClient getBluetoothClient(){
        return bluetoothClient;
    }


    /**
     * 搜索设备
     * @param searchResponse 回调
     * @param duration 搜索时间
     * @param times 搜索次数
     *   eg:duration=10 * 1000;times=1 表示：10s搜索一次，每次10s
     */
    public void startScanBleDevice(final SearchResponse searchResponse, int duration, int times){

        final SearchRequest searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(duration,times)
                .build();
        bluetoothClient.search(searchRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                searchResponse.onSearchStarted();
            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                searchResponse.onDeviceFounded(searchResult);
            }

            @Override
            public void onSearchStopped() {
                searchResponse.onSearchStopped();
            }

            @Override
            public void onSearchCanceled() {
                searchResponse.onSearchCanceled();
            }
        });

    }


    /**
     * 搜索设备
     * @param searchResponse 回调
     * @param duration 搜索时间
     * @param times 搜索次数
     *   eg:duration=10 * 1000;times=1 表示：10s搜索一次，每次10s
     */
    public void startScanBleDevice(final SearchResponse searchResponse, boolean scanClass,int duration, int times){

//        if(!scanClass){
//            startScanBleDevice(searchResponse,duration,times);
//            return;
//        }
//        bluetoothClient.stopSearch();
        final SearchRequest searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(duration,times)
//                .searchBluetoothClassicDevice(10 * 1000)
                .build();
        bluetoothClient.search(searchRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                searchResponse.onSearchStarted();
            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                searchResponse.onDeviceFounded(searchResult);
            }

            @Override
            public void onSearchStopped() {
                searchResponse.onSearchStopped();
            }

            @Override
            public void onSearchCanceled() {
                searchResponse.onSearchCanceled();
            }
        });

    }


    public void clearLog(){
        stringBuffer.delete(0,stringBuffer.length());
    }

    public String getLog(){
        return stringBuffer.toString();
    }



    /**
     * 停止搜索
     */
    public void stopScan(){
        if(bluetoothClient != null){
            bluetoothClient.stopSearch();
        }
    }

    //根据Mac地址连接蓝牙设备
    public void connBleDeviceByMac(String mac, String bleName, ConnStatusListener connStatusListener){
        connBleDevice(mac,bleName,connStatusListener);
    }

    //断连连接
    public void disConnDevice(){
        String spMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(spMac))
            return;
        bluetoothClient.stopSearch();
        bluetoothClient.disconnect(spMac);
        bluetoothClient.unregisterConnectStatusListener(spMac,connectStatusListener);
        BleSpUtils.remove(mContext,SAVE_BLE_MAC_KEY);

    }

    public void disConnDeviceNotRemoveMac(){
        String spMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(spMac))
            return;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(spMac);
            if (bleDevice != null) {
                unpairDevice(bleDevice);
            }
        }
        bluetoothClient.stopSearch();
        bluetoothClient.disconnect(spMac);
        bluetoothClient.unregisterConnectStatusListener(spMac,connectStatusListener);
        BleSpUtils.remove(mContext,SAVE_BLE_MAC_KEY);
    }
    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * // Constants.REQUEST_READ，所有读请求
     * // Constants.REQUEST_WRITE，所有写请求
     * // Constants.REQUEST_NOTIFY，所有通知相关的请求
     * // Constants.REQUEST_RSSI，所有读信号强度的请求
     */
    public void clearRequest(){
        String mac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(mac))
            return;
        bluetoothClient.clearRequest(mac,Constants.REQUEST_WRITE);
        bluetoothClient.clearRequest(mac,Constants.REQUEST_NOTIFY);

    }


    private synchronized void connBleDevice(final String bleMac, final String bleName, final ConnStatusListener connectResponse){
        BleSpUtils.put(mContext,SAVE_BLE_MAC_KEY,bleMac);
        clearLog();
        stringBuffer.append("连接"+System.currentTimeMillis()/1000+"\n\n");
        int status = bluetoothClient.getConnectStatus(bleMac);
        sendCommBroadcast("ble_action",0);
        Log.e(TAG,"************连接处="+bleMac+"--连接状态="+status);

        bluetoothClient.registerConnectStatusListener(bleMac,connectStatusListener);
        BleConnectOptions options = (new com.inuker.bluetooth.library.connect.options.BleConnectOptions.Builder()).setConnectRetry(2).setConnectTimeout(30000).setServiceDiscoverRetry(1).setServiceDiscoverTimeout(20000).build();
        bluetoothClient.connect(bleMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(final int code, final BleGattProfile data) {
                if(data == null || data.getServices() == null)
                    return;
                List<BleGattService> serviceList = data.getServices();
                Log.e(TAG,"-----onResponse="+code+"\n"+new Gson().toJson(serviceList));

                if(code == 0){  //连接成功了，开始设置通知
                    stringBuffer.append("连接成功"+"\n\n");
                    sendCommBroadcast("ble_action",0);
                    //判断是否是OTA升级状态，是OTA状态不保存地址
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                        public void run() {

                            //实时数据返回，主动通道

                            setNotifyData(bleMac,bleConstant.SERVICE_UUID,bleConstant.READ_UUID,connectResponse);

                        }
                    }, 2000L);
                    connectResponse.connStatus(code);
                }
            }
        });

    }


    public synchronized void clearListener(){
        if(interfaceManager.writeBack24HourDataListener != null)
            interfaceManager.writeBack24HourDataListener = null;
    }



    public void setClearListener(){
        if(interfaceManager.writeBackDataListener != null){
            interfaceManager.writeBackDataListener = null;
        }
        if(interfaceManager.writeBack24HourDataListener != null)
            interfaceManager.writeBack24HourDataListener = null;

    }

    public void setClearExercise(){
        if(interfaceManager.onExerciseDataListener != null){
            interfaceManager.onExerciseDataListener = null;
        }
    }


    private AutoBackBean autoBackBean = new AutoBackBean();

    //数据发送通道返回数据，app端发送数据后设备返回数据
    private synchronized void setNotifyData(String mac,UUID serviceUUid,UUID notifyUUid,ConnStatusListener connStatusListener){
        bluetoothClient.notify(mac, serviceUUid, notifyUUid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                String notifyStr = uuid1.toString()+" "+Utils.formatBtArrayToString(bytes);
                Log.e(TAG,"------写入数据返回="+notifyStr);
                //stringBuffer.append("数据返回:"+notifyStr+"\n\n");
               // sendCommBroadcast("ble_action",0);
                if(interfaceManager.writeBackDataListener != null){
                    interfaceManager.writeBackDataListener.backWriteData(bytes);
                }

                if(interfaceManager.onCarWriteBackListener != null){
                    interfaceManager.onCarWriteBackListener.backWriteBytes(bytes);
                }

                //8800000000003860030f7ffaaf0031040190020100461e46321632df0000dfdf00000000000078ce010000008b8b464600020600000000000000000000000085
                //8800000000003808030f7ffaaf00310401 90 02 01 003c0a16322c1edf0000dfdf00000000000078ce010000008b8b464600040c000000000000000000000000c9

                if(bytes.length>20 && (bytes[9] &0xff) == 15 && (bytes[10] &0xff) == 127 && (bytes[17] & 0xff) == 144){

                    //当前工作模式
                    int workModel = bytes[18] &0xff;
                    //自检模式下项目
                    int selfCheckModel = bytes[19] & 0xff;
                    //当前处于预置位位置
                    int curPos =  bytes[20] & 0xff;
                    //左前气压值
                    int leftPressure = bytes[21] &0xff;
                    //右前气压值
                    int rightPressure = bytes[22] & 0xff;
                    //左后气压值
                    int leftRearPressure = bytes[23] & 0xff;
                    //右后气压值
                    int rightRearPressure = bytes[24] &0xff;


                    //气缸气压值
                    int cylinderPressure = bytes[25] & 0xff;

                    //左前高度尺
                    int leftRuler = bytes[26] & 0xff;
                    //右前高度尺
                    int rightRuler = bytes[27] &0xff;
                    //左后高度尺
                    int leftAfterRuler = bytes[28] &0xff;
                    //右后高度尺
                    int rightAfterRuler = bytes[29] & 0xff;

                    //电池电压
                    int batteryVal = bytes[38] & 0xff;
                    //气罐温度
                    int airBottleTemperature = bytes[39] & 0xff;

                    //ACC状态 0关闭；1开启
                    int accStatus = bytes[40] & 0xff;
                    //ACC工作模式 0acc模式;1定时模式
                    int accWorkModel = bytes[41] & 0xff;
                    //是否在移动 0静止；1运动
                    int moveStatus = bytes[42] & 0xff;
                    //是否休眠状态 0-非休眠；1-休眠
                    int sleepStatus = bytes[43] & 0xff;
                    //左前气压目标值(单位PSI)
                    int leftFrontGoalPressure = bytes[44] & 0xff;
                    //右前气压目标值(单位PSI)
                    int rightFrontGoalPressure = bytes[45] & 0xff;
                    //左后气压目标值(单位PSI)
                    int leftRearGoalPressure = bytes[46] & 0xff;
                    //右后气压目标值(单位PSI)
                    int rightRearGoalPressure = bytes[47] & 0xff;
                    //是否正在加热 0 false 1 true
                    int heatStatus = bytes[48] & 0xff;

                    /**设备故障状态码
                     * bit0:系统未自检
                     * bit1:加速度传感器故障
                     * bit2:电池电压过高
                     * bit3:电池电压过低
                     * bit4:气泵1温度传感器故障
                     * bit5:气泵2温度传感器故障
                     * bit6:系统温度传感器故障
                     */
                    byte deviceErrorCode = bytes[49];
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
                    byte leftFrontErrorCode = bytes[50];
                    //右前故障状态码 状态码
                    byte rightFrontErrorCode = bytes[51];
                    //左后故障状态码
                    byte leftRearErrorCode = bytes[52];
                    //右后故障状态码
                    byte rightRearErrorCode = bytes[53];
                    /**
                     * 气罐故障状态码
                     * bit0:气压传感器故障
                     * bit1:气罐压力过低
                     * bit2:气泵温度过高
                     * bit3:气罐无法充气
                     * bit4:气泵状态异常
                     */
                    byte airBottleErrorCode = bytes[54];
                    //是否激活
                    int activationStatus = bytes[55];


                    autoBackBean.setWorkModel(workModel);
                    autoBackBean.setSelfCheckModel(selfCheckModel);
                    autoBackBean.setCurPos(curPos);
                    autoBackBean.setLeftPressure(leftPressure);
                    autoBackBean.setRightPressure(rightPressure);
                    autoBackBean.setLeftRearPressure(leftRearPressure);
                    autoBackBean.setRightRearPressure(rightRearPressure);
                    autoBackBean.setLeftFrontHeightRuler(leftRuler);
                    autoBackBean.setRightFrontHeightRuler(rightRuler);
                    autoBackBean.setLeftAfterHeightRuler(leftAfterRuler);
                    autoBackBean.setRightAfterHeightRuler(rightAfterRuler);


                    autoBackBean.setCylinderPressure(cylinderPressure);
                    autoBackBean.setBatteryVal(batteryVal);
                    autoBackBean.setAirBottleTemperature(airBottleTemperature);

                    autoBackBean.setAccStatus(accStatus);
                    autoBackBean.setAccWorkModel(accWorkModel);
                    autoBackBean.setMoveStatus(moveStatus);
                    autoBackBean.setSleepStatus(sleepStatus);
                    autoBackBean.setLeftFrontGoalPressure(leftFrontGoalPressure);
                    autoBackBean.setRightFrontGoalPressure(rightFrontGoalPressure);
                    autoBackBean.setLeftRearGoalPressure(leftRearGoalPressure);
                    autoBackBean.setRightRearGoalPressure(rightRearGoalPressure);
                    autoBackBean.setHeatStatus(heatStatus);
                    autoBackBean.setDeviceErrorCode(deviceErrorCode);
                    autoBackBean.setLeftFrontErrorCode(leftFrontErrorCode);
                    autoBackBean.setRightFrontErrorCode(rightFrontErrorCode);
                    autoBackBean.setLeftFrontErrorCode(leftRearErrorCode);
                    autoBackBean.setRightRearErrorCode(rightRearErrorCode);
                    autoBackBean.setAirBottleErrorCode(airBottleErrorCode);
                    autoBackBean.setActivationStatus(activationStatus);





                   // Log.e(TAG,"--------自动返回="+autoBackBean.toString());

                    if(onCarAutoBackListener != null){
                        onCarAutoBackListener.backAutoData(autoBackBean);
                    }

                }

            }

            @Override
            public void onResponse(int i) {
                connStatusListener.setNoticeStatus(i);
            }
        });
    }


    /**读取电量**/
    public synchronized void readDeviceBatteryValue(OnCommBackDataListener onCommBackDataListener){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        bluetoothClient.read(bleMac, bleConstant.BATTERY_SERVER_UUID, bleConstant.BATTERY_READ_UUID, new BleReadResponse() {
            @Override
            public void onResponse(int i, byte[] bytes) {
                if(bytes == null)
                    return;
                Log.e(TAG,"-------电量读取="+Utils.formatBtArrayToString(bytes));
                int batteryValue = bytes[0] & 0xff;
                if(onCommBackDataListener != null)
                    onCommBackDataListener.onIntDataBack(new int[]{batteryValue});
            }
        });
    }


    //写入设备数据
    public synchronized void writeDataToDevice(byte[] data, WriteBackDataListener writeBackDataListener){
        String writeStr = Utils.formatBtArrayToString(data);
        Log.e(TAG,"-----写入数据="+writeStr);
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        stringBuffer.append("写入数据:"+writeStr+"\n\n");
        sendCommBroadcast("ble_action",0);
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac,bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }
    //OnCarWriteBackListener


    public synchronized void writeCarDataToDevice(byte[] data, OnCarWriteBackListener writeBackDataListener){
        String writeStr = Utils.formatBtArrayToString(data);
        Log.e(TAG,"-----写入数据="+writeStr);
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        stringBuffer.append("写入数据:"+writeStr+"\n\n");
        sendCommBroadcast("ble_action",0);
        interfaceManager.setOnCarWriteBackListener(writeBackDataListener);
        bluetoothClient.write(bleMac,bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }




    //写入设备数据
    public synchronized void writeDataToDevice(byte[] data){
        Log.e(TAG,"-----写入数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        bluetoothClient.write(bleMac,bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }



    /**
     * 键盘写入表盘数据
     */
    public synchronized void writeKeyBoardDialData(byte[] data, WriteBackDataListener writeBackDataListener){
      //  this.dayTag = day;
       // Log.e(TAG,"-----写入键盘表盘数据长度="+data.length +"  "+Utils.formatBtArrayToString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac, bleConstant.SERVICE_UUID,bleConstant.KEYBOARD_DIAL_WRITE_UUID,data,bleWriteResponse);
    }



    //监听蓝牙连接状态的监听
    private final BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {

            Log.e(TAG,"---mmmmm-连接状态manager="+mac+" "+status);
            if(mac != null && status == Constants.STATUS_DISCONNECTED){
                sendCommBroadcast(BleConstant.BLE_SOURCE_DIS_CONNECTION_ACTION);
            }
            if(bleConnStatusListener != null){
                bleConnStatusListener.onConnectStatusChanged(mac==null?"mac":mac,status);
            }
        }
    };

    private final BleWriteResponse bleWriteResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    private void sendCommBroadcast(int...value){
        Intent intent = new Intent();
        intent.setAction(BleConstant.COMM_BROADCAST_ACTION);
        intent.putExtra(BleConstant.COMM_BROADCAST_KEY,value);
        mContext.sendBroadcast(intent);
    }

    private void sendCommBroadcast(String action,int...value){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(BleConstant.COMM_BROADCAST_KEY,value);
        mContext.sendBroadcast(intent);
    }
}
