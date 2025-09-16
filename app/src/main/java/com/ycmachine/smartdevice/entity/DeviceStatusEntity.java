package com.ycmachine.smartdevice.entity;


import java.util.List;

// 设备所有元器件状态
public class DeviceStatusEntity {


    /**
     * 1号柜锁状态，0关闭，1打开
     * */

    private Integer  deviceLockA;

    /**
     * 2号柜锁状态，0关闭，1打开
     * */
    private Integer  deviceLockB;

    /**
     * 1 号门 RFID 卡号
     * */
    private Integer  deviceRfidA;

    /**
     * 2 号门 RFID 卡号
     * */
    private Integer  deviceRfidB;

    /** 1号柜状态，0关闭，1打开 */
    private Integer deviceStatusA;

    /** 2号柜状态，0关闭，1打开 */
    private Integer deviceStatusB;

    public Integer getDeviceLockA() {
        return deviceLockA;
    }

    public void setDeviceLockA(Integer deviceLockA) {
        this.deviceLockA = deviceLockA;
    }

    public Integer getDeviceLockB() {
        return deviceLockB;
    }

    public void setDeviceLockB(Integer deviceLockB) {
        this.deviceLockB = deviceLockB;
    }

    public Integer getDeviceRfidA() {
        return deviceRfidA;
    }

    public void setDeviceRfidA(Integer deviceRfidA) {
        this.deviceRfidA = deviceRfidA;
    }

    public Integer getDeviceRfidB() {
        return deviceRfidB;
    }

    public void setDeviceRfidB(Integer deviceRfidB) {
        this.deviceRfidB = deviceRfidB;
    }

    public Integer getDeviceStatusA() {
        return deviceStatusA;
    }

    public void setDeviceStatusA(Integer deviceStatusA) {
        this.deviceStatusA = deviceStatusA;
    }

    public Integer getDeviceStatusB() {
        return deviceStatusB;
    }

    public void setDeviceStatusB(Integer deviceStatusB) {
        this.deviceStatusB = deviceStatusB;
    }

    public List<Integer> getWeight() {
        return Weight;
    }

    public void setWeight(List<Integer> weight) {
        Weight = weight;
    }

    public String getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        DeviceNum = deviceNum;
    }

    // 所有托盘重量
    List<Integer> Weight;

    /** 绑定的设备号 */
    private  String DeviceNum;


}
