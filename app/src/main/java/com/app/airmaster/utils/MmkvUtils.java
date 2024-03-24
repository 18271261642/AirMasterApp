package com.app.airmaster.utils;

import com.tencent.mmkv.MMKV;

import timber.log.Timber;


/**
 * Created by Admin
 * Date 2022/3/25
 */
public class MmkvUtils {

    //地址，test环境或preview环境
    private static final String NET_SERVER_KEY = "net_server_key";


    //保存设置的gif的速度，整数型
    private static final String SET_GIF_SPEED = "set_gif_speed";

    private final static String SAVE_FILE_KEY = "bonlala_key";
    public static final String TOKEN_KEY = "token_key";

    /**连接的设备mac**/
    private static final String CONN_DEVICE_MAC = "b_conn_device_mac";
    private static final String CONN_DEVICE_NAME = "b_conn_device_name";
    //是否已经同意用户协议
    private static final String IS_AGREE_PRIVACY = "is_privacy";
   /**气压最大值**/
   private static final String MAX_PRESSURE_VALUE = "max_pressure";


    /**是否是旋钮屏**/
    private static final String IS_SCREEN_DEVICE = "screen_device";



    private static MMKV mmkv;

    public static void initMkv(){
        mmkv = MMKV.mmkvWithID(SAVE_FILE_KEY);
    }

    public static void setSaveParams(String k,String v){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }
        mmkv.putString(k,v);
    }

    public static void setSaveParams(String k,Boolean v){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }
        mmkv.putBoolean(k,v);
    }



    public static void setSaveObjParams(String k,Object v){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }

        if(v instanceof Integer){
            mmkv.putInt(k, (Integer) v);
        }

        if(v instanceof String){
            mmkv.putString(k, (String) v);
        }

        if(v instanceof Boolean){
            mmkv.putBoolean(k, (Boolean) v);
        }

        if(v instanceof  Long){
            mmkv.putLong(k, (Long) v);
        }

        if(v instanceof  Float){
            mmkv.putFloat(k, (Float) v);
        }


    }


    public static Object getSaveParams(String k,Object oj){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }
        if(oj instanceof String){
            return mmkv.getString(k, (String) oj);
        }

        if(oj instanceof Integer){
            return mmkv.getInt(k,(int)oj);
        }

        if(oj instanceof Long){
            return mmkv.getLong(k, (Long) oj);
        }

        if(oj instanceof Float){
            return mmkv.getFloat(k, (Float) oj);
        }

        if(oj instanceof Boolean){
            return mmkv.getBoolean(k, (Boolean) oj);
        }


        return mmkv.getString(k, (String) oj);

    }


    //设置app的环境
    public static void setNetServer(int code){
        setSaveObjParams(NET_SERVER_KEY,code);
    }

    //获取app的环境 0test；1dev;2preview
    public static int getNetServer(){
        return (int) getSaveParams(NET_SERVER_KEY,0);
    }



    //保存用户连接的Mac
    public static void saveConnDeviceMac(String mac){
        setSaveParams(CONN_DEVICE_MAC,mac);
    }

    //获取已经连接的Mac
    public static String getConnDeviceMac(){
        return (String) getSaveParams(CONN_DEVICE_MAC,"");
    }

    //保存用户连接的设备名称
    public static void saveConnDeviceName(String name){
        setSaveParams(CONN_DEVICE_NAME,name);
    }

    public static String getConnDeviceName(){
        return (String) getSaveParams(CONN_DEVICE_NAME,"");
    }


    public static void setIsAgreePrivacy(boolean isAgreePrivacy){
        setSaveParams(IS_AGREE_PRIVACY,isAgreePrivacy);
    }

    public static boolean getPrivacy(){
        return (boolean) getSaveParams(IS_AGREE_PRIVACY,false);
    }



    public static void saveGifSpeed(int speed){
        setSaveObjParams(SET_GIF_SPEED,speed);
    }

    public static int getGifSpeed(){
        return (int) getSaveParams(SET_GIF_SPEED,5);
    }


    public static void saveScreenDeviceStatus(boolean isScreen){
        setSaveObjParams(IS_SCREEN_DEVICE,isScreen);
    }

    public static boolean getScreenDeviceStatus(){
        return (boolean) getSaveParams(IS_SCREEN_DEVICE,false);
    }

    public static void saveMaxPressureValue(int max){
       // Timber.e("-----保存最大值="+max);
        setSaveObjParams(MAX_PRESSURE_VALUE,max);
    }

    public static int getMaxPressureValue(){
        return (int) getSaveParams(MAX_PRESSURE_VALUE,150);
    }
}
