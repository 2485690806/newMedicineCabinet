package leesche.smartrecycling.base.entity;

import com.google.gson.JsonArray;

public class UploadDataParamsEntity {

    private JsonArray result;
    private String loginValue;
    private String loginType;
    private String deviceCode;
    private String[] monitorImg;
    private String orderId;
    private long orderTime;

    public JsonArray getResult() {
        return result;
    }

    public void setResult(JsonArray result) {
        this.result = result;
    }

    public String getLoginValue() {
        return loginValue;
    }

    public void setLoginValue(String loginValue) {
        this.loginValue = loginValue;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String[] getMonitorImg() {
        return monitorImg;
    }

    public void setMonitorImg(String[] monitorImg) {
        this.monitorImg = monitorImg;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }
}
