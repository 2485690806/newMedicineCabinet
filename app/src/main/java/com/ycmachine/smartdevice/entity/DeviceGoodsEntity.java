package com.ycmachine.smartdevice.entity;

import java.util.List;

public class DeviceGoodsEntity {


    /** 绑定的设备号 */
    private  String DeviceNum;

    public String getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        DeviceNum = deviceNum;
    }



    List<StoreGoodsEntity> leftGoods;  // 左侧商品列表

    public List<StoreGoodsEntity> getLeftGoods() {
        return leftGoods;
    }

    public void setLeftGoods(List<StoreGoodsEntity> leftGoods) {
        this.leftGoods = leftGoods;
    }

    public List<StoreGoodsEntity> getRightGoods() {
        return rightGoods;
    }

    public void setRightGoods(List<StoreGoodsEntity> rightGoods) {
        this.rightGoods = rightGoods;
    }

    List<StoreGoodsEntity> rightGoods;  // 右侧商品列表


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private  String phone;

}
