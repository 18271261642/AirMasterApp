package com.blala.blalable;

import android.util.Log;
import java.util.Calendar;
import java.util.UUID;


/**
 * Created by Admin
 * Date 2021/9/3
 * @author Admin
 */
public class BleConstant {


    /**连接成的广播action**/
    public static final String BLE_CONNECTED_ACTION = "com.blala.blalable.ble_connected";
    /**断开连接的action**/
    public static final String BLE_DIS_CONNECT_ACTION = "com.blala.blalable.ble_dis_connected";

    /**发送固件版本的action**/
    public static final String BLE_SEND_DUF_VERSION_ACTION = "com.blala.blalable.dfu_version";

    public static final String BLE_SOURCE_DIS_CONNECTION_ACTION = "com.blala.blalable.source_dis_conn";

    /**扫描完了的广播，一个扫描过程最多20秒**/
    public static final String BLE_SCAN_COMPLETE_ACTION = "com.blala.blalable.scan_complete";
    public static final String BLE_START_SCAN_ACTION = "com.blala.blalable.scan_start";


    /**设置数据同步完成的广播**/
    public static final String BLE_SYNC_COMPLETE_SET_ACTION = "com.blala.blalable.set_complete";
    /**24小时数据同步完成**/
    public static final String BLE_24HOUR_SYNC_COMPLETE_ACTION = "com.blala.blalable.data_complete";
    /**手表结束锻炼，手表返回一个锻炼数据**/
    public static final String BLE_COMPLETE_EXERCISE_ACTION = "com.blala.blalable.exercise_finish";

    /**通用广播的action**/
    public static final String COMM_BROADCAST_ACTION = "comm_action";
    public static final String COMM_BROADCAST_KEY = "comm_action_key";

    /**测量完心率、血压、血氧的value**/
    public static final int MEASURE_COMPLETE_VALUE = -1;



    /**服务UUID**/
    public UUID SERVICE_UUID = UUID.fromString("1f40eaf8-aab4-14a3-f1ba-f61f35cddbaa");

    /**命令发送特征uuid**/
    public UUID WRITE_UUID = UUID.fromString("1f400001-aab4-14a3-f1ba-f61f35cddbaa");
    /**写入数据返回指令的UUID**/
    public UUID READ_UUID = UUID.fromString("1f400002-aab4-14a3-f1ba-f61f35cddbaa");

    /**表盘写入的UUID**/
    public final UUID KEYBOARD_DIAL_WRITE_UUID = UUID.fromString("1f400003-aab4-14a3-f1ba-f61f35cddbaa");


    /**旋钮屏手表的服务_UUID**/
    public UUID CAR_WATCH_SERVICE_UUID = UUID.fromString("1f40eaf7-aab4-14a3-f1ba-f61f35cddbaa");
    public UUID CAR_WATCH_WRITE_UUID = UUID.fromString("1f400001-aab4-14a3-f1ba-f61f35cddbaa");
    public UUID CAR_WATCH_READ_UUID = UUID.fromString("1f400002-aab4-14a3-f1ba-f61f35cddbaa");



    /**电量的serviceUUID，主动读取**/
    public UUID BATTERY_SERVER_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public UUID BATTERY_READ_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");


    /**获取当前时间**/
    public byte[] getCurrTime(){
        return new byte[]{0x00,0x02};
    }

    /**设置时间**/
    public byte[] syncTime(int year,int month,int day,int hour ,int minute,int second){
        byte[] timeByte = new byte[9];
        timeByte[0] = 0x07;
        timeByte[1] = 0x30;
        timeByte[2] = (byte) (year & 0x00ff);
        timeByte[3] = (byte) ((year>>8) & 0xff);
        timeByte[4] = (byte) (month & 0xff);
        timeByte[5] = (byte) (day & 0xff);
        timeByte[6] = (byte) (hour & 0xff);
        timeByte[7] = (byte) (minute & 0xff);
        timeByte[8] = (byte) (second & 0xff);
        return timeByte;
    }

    /**设置时间**/
    public byte[] syncTime(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        byte[] timeByte = new byte[9];
        timeByte[0] = 0x04;
        timeByte[1] = 0x01;
        timeByte[2] = (byte) ((year>>8) & 0xff);
        timeByte[3] = (byte) (year & 0xff);
        timeByte[4] = (byte) (month & 0xff);
        timeByte[5] = (byte) (day & 0xff);
        timeByte[6] = (byte) (hour & 0xff);
        timeByte[7] = (byte) (minute & 0xff);
        timeByte[8] = (byte) (second & 0xff);
        return timeByte;
    }

    /**关机，复位**/
    public byte[] powerOff(int off){
        return new byte[]{0x05,0x60, (byte) off, (byte) 0xa1, (byte) 0xfe,0x74,0x69};
    }

    /**查找手表**/
    public byte[] findDevice(){
        return new byte[]{0x00,0x019};
    }

    //进入拍照
    public byte[] setIntoPhoto(){
        return new byte[]{0x00,0x0d};
    }

    //获取版本信息
    public byte[] getDeviceVersion(){
        return new byte[]{0x00,0x01};
    }

    //获取电量
    public byte[] getDeviceBattery(){
        return new byte[]{0x00,0x3f};
    }


    //同步用户信息
    public byte[] syncUserInfo(int year,int month,int day,int weight,int height,int sex,int maxHeart,int minHeart){
        byte[] userByte = new byte[12];
        userByte[0] = 0x0a;
        userByte[1] = 0x33;
        userByte[2] = (byte) ((year>>8)&0xff);
        userByte[3] = (byte) (year & 0xff);
        userByte[4] = (byte) (month & 0xff);
        userByte[5] = (byte) (day & 0xff);
        userByte[6] = (byte) sex;
        userByte[7] = (byte) (((weight * 100) >> 8) & 0xff);
        userByte[8] = (byte) (weight & 0xff);
        userByte[9] = (byte) (height & 0xff);
        userByte[10] = (byte) (maxHeart & 0xff);
        userByte[11] = (byte) (minHeart & 0xff);
        return userByte;
    }

    //获取用户信息
    public byte[] getUserInfo(){
        return new byte[]{0x00,0x05};
    }

    //设置目标步数
    public byte[] stepGoal(int step){
        byte[] at = Utils.intToByteArray(step);
        Log.e("SSS","--转换="+Utils.formatBtArrayToString(at));
        return new byte[]{0x03,0x38,at[0],at[1],at[2]};
    }
    //读取计步目标
    public byte[] readStepGoal(){
        return new byte[]{0x00,0x0C,0x00};
    }




    //获取久坐提醒
    public byte[] getLongSitData(){
        return new byte[]{0x00,0x03};
    }


    //设置久坐提醒
    public byte[] setLongSitData(int startHour,int startMinute,int interval ,int endHour,int endMinute){
        byte[] longSitByte = new byte[7];
        longSitByte[0] = 0x05;
        longSitByte[1] = 0x31;
        longSitByte[2] = (byte) (interval & 0xff);
        longSitByte[3] = (byte) (startHour & 0xff);
        longSitByte[4] = (byte) (startMinute & 0xff);
        longSitByte[5] = (byte) (endHour & 0xff);
        longSitByte[6] = (byte) (endMinute & 0xff);
        return longSitByte;

    }

    //获取抬腕亮屏
    public byte[] getWristData(){
        return new byte[]{0x00,0x08};
    }


    //设置抬腕亮屏
    public byte[] setWristData(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        byte[] wristByte = new byte[7];
        wristByte[0] =0x05;
        wristByte[1] = 0x37;
        wristByte[2] = (byte) (isOpen ? 1 : 0);
        wristByte[3] = (byte) (startHour & 0xff);
        wristByte[4] = (byte) (startMinute & 0xff);
        wristByte[5] = (byte) (endHour & 0xff);
        wristByte[6] = (byte) (endMinute & 0xff);
        return wristByte;
    }

    //获取心率开关
    public byte[] getHeartStatus(){
        return new byte[]{0x00,0x09};
    }

    //设置心率开关 1 : 开 ；0 ：关闭
    public byte[] setHeartStatus(boolean isOpen){
        return new byte[]{0x01,0x1a, (byte) (isOpen ? 1 : 0)};
    }

    //读取勿扰模式
    public byte[] getDNTStatus(){
        return new byte[]{0x00,0x06};
    }

    //设置勿扰模式
    public byte[] setDNTStatus(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        byte[] dntByte = new byte[7];
        dntByte[0] = 0x05;
        dntByte[1] = 0x34;
        dntByte[2] = (byte) (isOpen ? 1 : 0);
        dntByte[3] = (byte) (startHour & 0xff);
        dntByte[4] = (byte) (startMinute & 0xff);
        dntByte[5] = (byte) (endHour & 0xff);
        dntByte[6] = (byte) (endMinute & 0xff);
        return dntByte;
    }


    //进入拍照模式
    public byte[] intoPhoto(){
        return new byte[]{0x00,0x0d};
    }


    //测量血氧 1开始，2结束
    public byte[] measureSpo2(boolean isOpen){
        return new byte[]{0x01,0x3d, (byte) (isOpen ? 1 : 2)};
    }

    //测量血压 1 开始，2停止
    public byte[] measureBloodStatus(boolean isOpen){
        return new byte[]{0x01,0x3b, (byte) (isOpen ? 1 : 2)};
    }

    //测量心率
    public byte[] measureHeartStatus(boolean isOpen){
        return new byte[]{0x01,0x50 , (byte) (isOpen ? 1 : 2)};
    }

    //设置通用
    public byte[] setCommonData(boolean isKm,boolean is24Hour,boolean is24HourHeart,boolean isTmp){
        byte[] connByte = new byte[12];
        connByte[0] = 0x0a;
        connByte[1] = 0x32 & 0xff;


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(0);
        stringBuilder.append(0);
        stringBuilder.append(isTmp?1:0);
        stringBuilder.append(is24HourHeart?0:1);
        stringBuilder.append(0);
        stringBuilder.append(is24Hour?1:0);
        stringBuilder.append(0);
        stringBuilder.append(isKm?0:1);
        Log.e("TAG","---sb="+stringBuilder.toString());

        byte b1 = Utils.bitToByte(stringBuilder.toString());
        connByte[3] =b1;

        StringBuilder stringBuilder2 = new StringBuilder();
        for(int i = 0;i<6;i++){
            stringBuilder2.append(0);
        }
        stringBuilder2.append(1);
        stringBuilder2.append(1);
        byte b2 = 0x03;
        connByte[4] = b2;

        return connByte;


    }


    //获取通用设置
    public byte[] getCommonSetData(){
        return new byte[]{0x00,0x04};
    }

    //获取内置表盘
    public byte[] getLocalDial(){
        return new byte[]{0x00,0x0a};
    }

    //设置内置表盘 1开始
    public byte[] setLocalDial(int index){
        return new byte[]{0x01,0x1b, (byte) index};
    }

    //获取背光等级
    public byte[] getBackLight(){
        return new byte[]{0x00,0x0b};
    }

    /**
     * 设置背光等级
     * @param light 亮度范围等级
     * @param timeInterval 亮度时长
     * @return
     */
    public byte[] setBackLight(int light,int timeInterval){
        return new byte[]{0x02,0x36, (byte) light, (byte) timeInterval};
    }

    //读取当天数据 0为当前，1是昨天
    public byte[] getDaySport(int day){
        return new byte[]{0x01,0x40, (byte) day};
    }

    //获取汇总数据，返回距离，卡路里等信息
    public byte[] getCountData(int day){
        return new byte[]{0x01,0x45, (byte) day};
    }

    /**读取闹钟从0开始第一个，1第二个，2第三个，总共3个**/
    public byte[] readAlarm(int alarmId){
        return new byte[]{0x00,0x07, (byte) alarmId};
    }

    /**设置闹钟**/
    public byte[] setAlarm(int index,boolean isOpen,int repeat,int hour,int minute){
        byte[] alarmByte = new byte[20];
        alarmByte[0] = 0x12;
        alarmByte[1] = 0x35;
        alarmByte[2] = (byte) index;
        alarmByte[3] = (byte) (isOpen ? 1 : 0);
        alarmByte[4] = (byte) repeat;
        alarmByte[5] = (byte) (hour & 0xff);
        alarmByte[6] = (byte) (minute &0xff);
        return alarmByte;
    }

}
