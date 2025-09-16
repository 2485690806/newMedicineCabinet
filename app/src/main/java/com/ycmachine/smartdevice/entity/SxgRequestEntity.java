package com.ycmachine.smartdevice.entity;



import java.util.List;

public class SxgRequestEntity {

    private Long deviceId; // 指定设备的id

    private String deviceNum; // 指定设备的编号

    private Integer type; // 要下发的指令类型有：0：更新app，1：指定柜门开锁，2：获取锁的状态，3：获取所有价格牌的状态，4：设置价格牌，5：获取某个柜子的托盘重量,6：托盘清零（去皮）7：校准托盘

    private Integer isDeviceNum; // 1:1号柜 2:2号柜

    private Integer freeId;    // 支付id

    private String msg; // 消息

    private List<YzsTray> trayList; // 托盘列表

    private String cmd; // 命令

    private String voice; // 语音播报的内容

    public Integer getIsVip() {
        return isVip;
    }

    public void setIsVip(Integer isVip) {
        this.isVip = isVip;
    }

    private Integer isVip; // 是否是vip 0:不是 1:是


    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }


    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsDeviceNum() {
        return isDeviceNum;
    }

    public void setIsDeviceNum(Integer isDeviceNum) {
        this.isDeviceNum = isDeviceNum;
    }

    public Integer getFreeId() {
        return freeId;
    }

    public void setFreeId(Integer freeId) {
        this.freeId = freeId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<YzsTray> getTrayList() {
        return trayList;
    }

    public void setTrayList(List<YzsTray> trayList) {
        this.trayList = trayList;
    }
}
