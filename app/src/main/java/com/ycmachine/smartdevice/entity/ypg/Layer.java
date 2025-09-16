package com.ycmachine.smartdevice.entity.ypg;

public class Layer {
    private Integer bushu; // 步数（对应JS中的this.layers[].bushu）

    public Layer(Integer bushu) {
        this.bushu = bushu;
    }

    public Integer getBushu() {
        return bushu;
    }
}
