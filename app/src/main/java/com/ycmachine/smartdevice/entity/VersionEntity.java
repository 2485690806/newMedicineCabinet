package com.ycmachine.smartdevice.entity;



public class VersionEntity {


    /** 绑定的设备号 */
    private  String DeviceNum;

    public String getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        DeviceNum = deviceNum;
    }

    public String getVersionNum() {
        return VersionNum;
    }

    public void setVersionNum(String versionNum) {
        VersionNum = versionNum;
    }

    public boolean isReadSoftwareVersion() {
        return readSoftwareVersion;
    }

    public void setReadSoftwareVersion(boolean readSoftwareVersion) {
        this.readSoftwareVersion = readSoftwareVersion;
    }

    /** 版本号 */
    private  String VersionNum;

    /** 是否是软件版本 */
    private  boolean readSoftwareVersion;

}
