package com.ycmachine.smartdevice.utils.serial;

/**
 * 串口回调
 */
public interface CoinSerialInter {

    /**
     * 连接结果回调
     * @param path 串口地址(当有多个串口需要统一处理时，可以用地址来区分)
     * @param isSucc 连接是否成功
     */
//    void CoinConnectMsg(String path,boolean isSucc);

    /**
     * 读取到的数据回调
     * @param path 串口地址(当有多个串口需要统一处理时，可以用地址来区分)
     * @param bytes 读取到的数据
     * @param size 数据长度
     */
    void CoinReadData(String path,byte[] bytes,int size);

    //若在串口开启的方法中 传入false 此处不会返回数据

}