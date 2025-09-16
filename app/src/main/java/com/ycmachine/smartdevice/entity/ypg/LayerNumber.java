package com.ycmachine.smartdevice.entity.ypg;

// 1. 定义数据类：关联层数和货道编号
public class LayerNumber {
    private int layerId; // 层数（如1对应T1，2对应T2）
    private int channelNumber; // 货道编号（如1-8、16-23等）

    public LayerNumber(int layerId, int channelNumber) {
        this.layerId = layerId;
        this.channelNumber = channelNumber;
    }

    public int getLayerId() {
        return layerId;
    }

    public int getChannelNumber() {
        return channelNumber;
    }
}
