package com.app.airmaster.car;

public interface OnCarMcuListener {

    /**升级进度
     *
     */
    void mcuDfuProgress(float progress);

    /**
     * 读取校验值
     * 0:成功;
     * 1:数据错误;
     * 2:擦除错误;
     * 3:写入错误;
     * 4:写入校验错误
     */

    void readCheckValue(int value);

    /**
     * 超时
     */
    void mcuBootTimeOut();
}
