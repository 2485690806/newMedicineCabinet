package com.ycmachine.smartdevice.entity;

import java.util.List;

public class TrayWeightEntity {


    /** 绑定的设备号 */
    private  String DeviceNum;

    public String getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        DeviceNum = deviceNum;
    }

    public List<Integer> getWeight() {
        return Weight;
    }

    public void setWeight(List<Integer> weight) {
        Weight = weight;
    }

    // 所有托盘重量
    List<Integer> Weight;

}
