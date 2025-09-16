package com.ycmachine.smartdevice.entity.ypg;

public class LayerParam {
    private int layerNumber; // 层数（如1、2、3...）
    private String layerTitle; // 层标题
    private int startNum; // 起始数字
    private int endNum; // 结束数字

    // 构造方法
    public LayerParam(int layerNumber, String layerTitle, int startNum, int endNum) {
        this.layerNumber = layerNumber;
        this.layerTitle = layerTitle;
        this.startNum = startNum;
        this.endNum = endNum;
    }

    // getter方法（获取层数）
    public int getLayerNumber() {
        return layerNumber;
    }

    // 其他getter（已有则无需修改）
    public String getLayerTitle() { return layerTitle; }
    public int getStartNum() { return startNum; }
    public int getEndNum() { return endNum; }
}