package com.ycmachine.smartdevice.handler;

import static com.ycmachine.smartdevice.ClientApplication.TAG;

import android.os.Looper;
import android.util.Log;

import com.ycmachine.smartdevice.constent.ClientConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import lombok.Getter;
import lombok.Setter;
import tp.xmaihh.serialport.utils.ByteUtil;

@Getter
@Setter
public class HeatParser {

//    Context context; // 上下文对象，可能用于UI更新或其他操作
// 用CopyOnWriteArrayList避免并发修改异常（支持多线程安全）
//private final List<HeatStatusObserver> observers = new CopyOnWriteArrayList<>();

    private static final class HeatParserHolder {
        static final HeatParser ToDiLogicHandler = new HeatParser();
    }

    public static HeatParser getInstance() {
        return HeatParser.HeatParserHolder.ToDiLogicHandler;
    }


    // 定义相关的布尔变量
    private boolean xzhouGuanYan;
    private boolean warehouse;
    private boolean antipinchlighteye;
    private boolean scmsxw;
    private boolean scmxxw;
    private boolean xcmsxw;
    private boolean xcmxxw;
    private boolean hsmdkxw;
    private boolean hsmgbxw;
    private boolean qhmsxw;
    private boolean qhmxxw;
    private boolean dmzt;
    private boolean sjjds;
    private boolean guanyan3;
    private boolean guanyan4;
    private boolean guanyan5;
    private boolean guanyan6;

    // 处理字符串的方法
    public void processString(byte[] bytes) {
        String str = ByteUtil.ByteArrToHex(bytes);
        // 检查字符串是否包含"aa030711"
        if (str.toLowerCase().contains("aa030711")) {
            // 保存当前dmzt状态用于判断状态变化
            if (qhmxxw && !(bytes[14] == 0x01)) {
                snapDevice();
            } else if (!qhmxxw && (bytes[14] == 0x01)) {
                stopSnapDevice();
            }

            // 按固定位置提取子串并设置相应的布尔变量
            xzhouGuanYan = bytes[4] == 0x01;
            warehouse = bytes[5] == 0x01;
            antipinchlighteye = bytes[6] == 0x01;
            scmsxw = bytes[7] == 0x01;
            scmxxw = bytes[8] == 0x01;
            scmxxw = bytes[8] == 0x01;
            xcmsxw = bytes[9] == 0x01;
            xcmxxw = bytes[10] == 0x01;
            hsmdkxw = bytes[11] == 0x01;
            hsmgbxw = bytes[12] == 0x01;
            qhmsxw = bytes[13] == 0x01;
            qhmxxw = bytes[14] == 0x01;
            dmzt = bytes[15] == 0x01;
            sjjds = bytes[16] == 0x01;
            guanyan3 = bytes[17] == 0x01;
            guanyan4 = bytes[18] == 0x01;
            guanyan5 = bytes[19] == 0x01;
            guanyan6 = bytes[20] == 0x01;

//            Log.d(TAG, "processString: " + qhmxxw);


            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.HEAT_STATUS));


        }
    }

    // 辅助方法：获取指定位置的子串并判断是否等于目标字符串
    private boolean getSubstringEquals(String str, int start, int end, String target) {
        // 确保字符串长度足够，避免越界异常
        if (str == null || str.length() < end) {
            return false;
        }
        try {
            String substring = str.substring(start, end);
            return target.equals(substring);
        } catch (IndexOutOfBoundsException e) {
            // 处理索引越界异常
            e.printStackTrace();
            return false;
        }
    }


    public void stopSnapDevice() {
        YpgLogicHandler.getInstance().stopSnapDevice();
    }

    // 一键拍柜机照片的指令方法
    public void snapDevice() {
        // 实现拍照逻辑
        // 这里只是示例，具体实现需要根据实际需求编写

        if (YpgLogicHandler.getInstance().isSnapDevice()) return;
        Log.d(TAG, "snapDevice: 拍照指令发送");

        List<Integer> list = new ArrayList<>();
        for (int value : ClientConstant.LayerValues) {
            list.add(value);
        }
        Looper.prepare();
        YpgLogicHandler.getInstance().snapDevice(list);
    }

    // 发送Y轴停止指令的方法
    private void sendJDJTZCommand() {
        // 实现发送停止指令的逻辑
        // 这里只是示例，具体实现需要根据实际需求编写
        ComponenTestHandler.getInstance().YaxisStop();
    }

    // Getter方法，用于获取各个状态值
    public boolean isXzhouGuanYan() {
        return xzhouGuanYan;
    }

    public boolean isWarehouse() {
        return warehouse;
    }

    public boolean isAntipinchlighteye() {
        return antipinchlighteye;
    }

    public boolean isScmsxw() {
        return scmsxw;
    }

    public boolean isScmxxw() {
        return scmxxw;
    }

    public boolean isXcmsxw() {
        return xcmsxw;
    }

    public boolean isXcmxxw() {
        return xcmxxw;
    }

    public boolean isHsmdkxw() {
        return hsmdkxw;
    }

    public boolean isHsmgbxw() {
        return hsmgbxw;
    }

    public boolean isQhmsxw() {
        return qhmsxw;
    }

    public boolean isQhmxxw() {
        return qhmxxw;
    }

    public boolean isDmzt() {
        return dmzt;
    }

    public boolean isSjjds() {
        return sjjds;
    }

    public boolean isGuanyan3() {
        return guanyan3;
    }

    public boolean isGuanyan4() {
        return guanyan4;
    }

    public boolean isGuanyan5() {
        return guanyan5;
    }

    public boolean isGuanyan6() {
        return guanyan6;
    }
}
