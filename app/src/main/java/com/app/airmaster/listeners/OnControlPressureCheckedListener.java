package com.app.airmaster.listeners;

import java.util.Map;

/**
 * 用于处理控制页面气压的点击
 */
public interface OnControlPressureCheckedListener {

    /**
     * key=0左前 key=1右前 key=2左后 key=3右后
     * value = 1+ value =0 -
     * @param map map
     */
    void onItemChecked(Map<Integer,Integer> map);
}
