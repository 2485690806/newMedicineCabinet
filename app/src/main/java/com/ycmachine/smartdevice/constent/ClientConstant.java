package com.ycmachine.smartdevice.constent;


import com.ycmachine.smartdevice.entity.ypg.Layer;

public class ClientConstant {

    public static WorkFlow currentWorkFlow = WorkFlow.Forward; // 当前工作流程
    public static boolean IS_DOING = false; // 是否第一次登录调试

    public static int nowFloor = 7;
    public static Layer[] medicineCabinetLayer = new Layer[]{ // 每层对应的步数
            new Layer(450),
            new Layer(365),
            new Layer(295),
            new Layer(215),
            new Layer(160),
            new Layer(105),
            new Layer(50),
            new Layer(0),
            new Layer(450),
            new Layer(80),
    };
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
}
