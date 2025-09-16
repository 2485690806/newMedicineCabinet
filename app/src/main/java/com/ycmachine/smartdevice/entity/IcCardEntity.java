package com.ycmachine.smartdevice.entity;

public class IcCardEntity {


    /** 绑定的设备号 */
    private  String DeviceNum;

    public String getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        DeviceNum = deviceNum;
    }

    public String getIcCardNum1() {
        return icCardNum1;
    }

    public void setIcCardNum1(String icCardNum1) {
        this.icCardNum1 = icCardNum1;
    }

    public String getIcCardNum2() {
        return icCardNum2;
    }

    public void setIcCardNum2(String icCardNum2) {
        this.icCardNum2 = icCardNum2;
    }

    private String icCardNum1;

    private String icCardNum2;
}
