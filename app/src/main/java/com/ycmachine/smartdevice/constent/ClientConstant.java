package com.ycmachine.smartdevice.constent;


import com.ycmachine.smartdevice.entity.ypg.Layer;
import com.ycmachine.smartdevice.entity.ypg.LayerParam;

import java.util.ArrayList;
import java.util.List;

public class ClientConstant {

    public static WorkFlow currentWorkFlow = WorkFlow.Forward; // 当前工作流程
    public static boolean IS_DOING = false; // 是否第一次登录调试

    public static int ResetFloor = 10; // 复位层数
    public static int nowFloor = ResetFloor;
    public static Layer[] medicineCabinetLayer = new Layer[]{ // 每层对应的步数
            new Layer(467), // 7
            new Layer(390), // 6
            new Layer(318), // 5
            new Layer(250), // 4
            new Layer(199), // 3
            new Layer(150), // 2
            new Layer(101), // 1
            new Layer(51), // 0 第八层
            new Layer(456), // 取货层
            new Layer(80), // 回收层
            new Layer(0),// 10 第九层
    };



    public static List<LayerParam> layerParams = new ArrayList<LayerParam>() {{
        add(new LayerParam(1,"T1", 1, 8));
        add(new LayerParam(2,"T2", 16, 23));
        add(new LayerParam(3,"T3", 31, 40));
        add(new LayerParam(4,"T4", 46, 55));
        add(new LayerParam(5,"T5", 61, 72));
        add(new LayerParam(6,"T6", 76, 87));
        add(new LayerParam(7,"T7", 91, 102));
        add(new LayerParam(8,"T8", 106, 117));
        add(new LayerParam(9,"T9", 121, 132));
    }};

    public static int[] LayerValues = new int[]{1, 3, 5, 7}; // 一键拍照的层数

    public interface PageFlag {
        int componentTestFragment = 0;
        int controlModeFragment = 1;
        int planImageFragment = 2;
        int realImageFragment = 3;
    }

    public enum WorkFlow {
        Forward(0, "正转", "Forward"),
        Backward(1, "反转", "Backward"),
        Stop(2, "停止", "Stop"),
        Standard(3, "标准出货模式", "Standard"),
        Recycle(4, "标准回收模式", "Recycle"),
        LiftHandler(5, "升降机模式", "LiftHandler"),
        XAxis(6, "X-轴", "XAxis"),
        UpperSideDoor(7, "上侧门", "UpperSideDoor"),
        LowerSideDoor(8, "下侧门", "LowerSideDoor"),
        RecyclingDoor(9, "回收门", "RecyclingDoor"),
        PickUpDoor(10, "取货门", "PickUpDoor"),
        SingleLayerTesting(11, "单层测试", "SingleLayerTesting"),
        LayerSetting(12, "层数设置", "LayerSetting"),
                ;

        private int value;
        private String desc;
        private String flag = "reject6";

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

        public String getFlag() {
            return flag;
        }

        WorkFlow(int value, String desc) {
            this.value = value;
            this.desc = desc;
            this.flag = "other";
        }

        WorkFlow(int value, String desc, String flag) {
            this.value = value;
            this.desc = desc;
            this.flag = flag;
        }
    }


    public static final int REQUEST_CODE_SELECT_MEDIA = 100;

    //连续点击5次进入设备设置
    public static final int DEVICE_CLICK_NUM = 5;
    //点击时间间隔3秒
    public static final int DEVICE_CLICK_INTERVER_TIME = 1500;
    //上一次的点击时间
    public static long DEVICE_lastClickTime = 0;
    //记录点击次数
    public static int DEVICE_clickNum = 0;

}
