package com.ycmachine.smartdevice.entity;

import java.util.Map;

public class FinishOrderEntity {

    public Integer getFreeId() {
        return freeId;
    }

    public void setFreeId(Integer freeId) {
        this.freeId = freeId;
    }

    private Integer freeId;    // 支付id
    private Map<Integer, Integer> integerIntegerMap;

    public Map<Integer, Integer> getIntegerIntegerMap() {
        return integerIntegerMap;
    }

    public void setIntegerIntegerMap(Map<Integer, Integer> integerIntegerMap) {
        this.integerIntegerMap = integerIntegerMap;
    }

    public String getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        DeviceNum = deviceNum;
    }


    /** 绑定的设备号 */
    private  String DeviceNum;

    public Integer getIsDeviceNum() {
        return isDeviceNum;
    }

    public void setIsDeviceNum(Integer isDeviceNum) {
        this.isDeviceNum = isDeviceNum;
    }

    // 所属柜机，0：左边柜机，1：右边柜机，每个柜机最大5个托盘
    private Integer isDeviceNum;

    public Integer getIsNorMalOrder() {
        return isNorMalOrder;
    }

    public void setIsNorMalOrder(Integer isNorMalOrder) {
        this.isNorMalOrder = isNorMalOrder;
    }

    private Integer isNorMalOrder;



}
