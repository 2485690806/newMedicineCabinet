package com.ycmachine.smartdevice.handler;

import static com.ycmachine.smartdevice.constent.ClientConstant.medicineCabinetLayer;
import static com.ycmachine.smartdevice.constent.ClientConstant.nowFloor;
import static leesche.smartrecycling.base.utils.HexDump.convertToHexString;
import static leesche.smartrecycling.base.utils.HexDump.hexStringToByteArray;

import android.app.Activity;
import android.widget.TextView;

import com.leesche.logger.Logger;

import leesche.smartrecycling.base.serial.SerialHelper;
import leesche.smartrecycling.base.utils.HexUtil;
import leesche.smartrecycling.base.utils.StringUtil;

public class ComponenTestHandler {

    SerialHelper serialController;
    TextView sendCmd;
    TextView recCmd;
    private Activity context;


    private static final class ComponenTestHandlerHolder {
        static final ComponenTestHandler ToDiLogicHandler = new ComponenTestHandler();
    }

    public static ComponenTestHandler getInstance() {
        return ComponenTestHandler.ComponenTestHandlerHolder.ToDiLogicHandler;
    }

    public void setContext(Activity context, TextView sendCmd, TextView recCmd) {
        this.context = context;
        this.sendCmd = sendCmd;
        this.recCmd = recCmd;
    }

    public void init(SerialHelper serialController) {
        this.serialController = serialController;


    }

    /**
     * 正转指令
     *
     * @param item 货道编号
     */
    public void turnGoodsChannel(int item) {
        // 生成指令字符串：AA000102 + 货道编号的十六进制(2位) + 02BB
        String str = "AA000102" + convertToHexString(item) + "01BB";
        sendSerialPort(hexStringToByteArray(str));
    }

    /**
     * 反转指令
     *
     * @param item 货道编号
     */
    public void rollbackGoodsChannel(int item) {
        // 生成指令字符串：AA000102 + 货道编号的十六进制(2位) + 02BB
        String str = "AA000102" + convertToHexString(item) + "02BB";
        sendSerialPort(hexStringToByteArray(str));
    }

    /**
     * 停止指令
     *
     * @param item 货道编号
     */
    public void stopGoodsChannel(int item) {
        // 生成指令字符串：AA000102 + 货道编号的十六进制(2位) + 02BB
        String str = "AA000102" + convertToHexString(item) + "03BB";
        sendSerialPort(hexStringToByteArray(str));
    }

    /**
     * 去往目标层数
     */
    public void YaxisGotoLevel(int floor) {
        String calculate = calculate(floor);
        Logger.d("目标楼层（第" + floor + "层）指令: " + calculate);
        sendSerialPort(hexStringToByteArray(calculate));
    }
    /**
     * Y轴复位
     */
    public void YaxisReset() {
        nowFloor = 7; // 重置当前楼层为7（第8层）
        String str = "AA000B0101BB";

        sendSerialPort(hexStringToByteArray(str));
    }
    public void YaxisRises() {
        String command = "AA000C0301AAAABB";
        sendSerialPort(hexStringToByteArray(command));
    }

    public void YaxisDecline() {
        String command = "AA000C0302AAAABB";
        sendSerialPort(hexStringToByteArray(command));
    }
    public void YaxisStop() {
        String command = "AA000C03000000BB";
        sendSerialPort(hexStringToByteArray(command));
    }

    public void XAxisForward() {
        String str = "AA00020101BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void XAxisReversed() {
        String str = "AA00020102BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void XAxisStop() {
        String str = "AA00020103BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void upperSideForward() {
        String str = "AA00030101BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void upperSideReversed() {
        String str = "AA00030102BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void upperSideStop() {
        String str = "AA00030103BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void lowerSideForward() {
        String str = "AA00040101BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void lowerSideReversed() {
        String str = "AA00040102BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void lowerSideStop() {
        String str = "AA00040103BB";
        sendSerialPort(hexStringToByteArray(str));
    }

    public void recycleSideForward() {
        String str = "AA00050101BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void recycleSideReversed() {
        String str = "AA00050102BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void recycleSideStop() {
        String str = "AA00050103BB";
        sendSerialPort(hexStringToByteArray(str));
    }

    public void pickUpSideForward() {
        String str = "AA00060101BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void pickUpSideReversed() {
        String str = "AA00060102BB";
        sendSerialPort(hexStringToByteArray(str));
    }
    public void pickUpSideStop() {
        String str = "AA00060103BB";
        sendSerialPort(hexStringToByteArray(str));
    }

    public void oneClickDelivery() {
        String command = "AA00090101BB";
        sendSerialPort(hexStringToByteArray(command));
    }
    public void oneClickRecycling() {
        String command = "AA00090102BB";
        sendSerialPort(hexStringToByteArray(command));
    }

    public void selfCheck() {
        String command = "AA000F0101BB";
        sendSerialPort(hexStringToByteArray(command));
    }



    public void sendSerialPort(byte[] bytes) {

        StringUtil.receveTextCmd(HexUtil.bytesToHex(bytes), context, recCmd);
        if (serialController != null)
            serialController.send(bytes);
    }


    /**
     * 计算楼层移动指令
     *
     * @param endFloor 目标楼层（原始楼层号，未减1）
     * @return 生成的指令字符串
     */
    public String calculate(int endFloor) {
        // 目标楼层减1（与JS逻辑一致）
        int targetFloor = endFloor - 1;

        // 相同楼层无需移动，返回固定指令
        if (nowFloor == targetFloor) {
            return "AA000C03000000BB";
        }

        // 打印日志（对应JS中的console.log）
        Logger.d("目标楼层（处理后）: " + targetFloor);
        Logger.d("当前楼层步数: " + medicineCabinetLayer[nowFloor].getBushu());
        Logger.d("目标楼层步数: " + medicineCabinetLayer[targetFloor].getBushu());

        // 解析当前楼层和目标楼层的步数
        int currentBushu = medicineCabinetLayer[nowFloor].getBushu();
        int targetBushu = medicineCabinetLayer[targetFloor].getBushu();

        // 判断方向：目标步数 > 当前步数 → 向上（01），否则向下（02）
        boolean isUp = targetBushu > currentBushu;
        String direction = isUp ? "01" : "02";

        // 计算总步数
        int totalSteps;
        if (isUp) {
            // 向上：目标步数 - 当前步数
            totalSteps = targetBushu - currentBushu;
        } else {
            // 向下：当前步数 - 目标步数
            totalSteps = currentBushu - targetBushu;
        }

        // 将总步数转换为4位十六进制字符串（不足补0）
        String hexSteps = String.format("%04X", totalSteps);

        // 生成指令
        String cmd = "AA000C03" + direction + hexSteps + "BB";

        // 更新当前楼层
        nowFloor = targetFloor;

        return cmd;
    }

}
