package com.ycmachine.smartdevice.handler;

import static android.content.Context.MODE_PRIVATE;
import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;
import static com.ycmachine.smartdevice.constent.ClientConstant.medicineCabinetLayer;
import static leesche.smartrecycling.base.utils.HexDump.toHexString;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.entity.ypg.LayerNumber;
import com.ycmachine.smartdevice.entity.ypg.LayerParam;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.serial.SerialHelper;
import leesche.smartrecycling.base.utils.DataSourceOperator;
import leesche.smartrecycling.base.utils.HexUtil;
import leesche.smartrecycling.base.utils.StringUtil;
import leesche.smartrecycling.base.utils.TTSUtils;
import lombok.Getter;
import tp.xmaihh.serialport.bean.ComBean;

@Getter
public class YpgLogicHandler implements SerialHelper.OnSerialListener {

    String path = "/dev/ttyZC0";
//    String path = "/dev/ttyZC2";
    int baudrate = 115200;

    SharedPreferences sp;
    SerialHelper serialController;
    private TTSUtils ttsUtils;
    TextView sendCmd;
    TextView recCmd;
    private Activity context;

    private List<OnStatusListener> statusListeners = new ArrayList<>(); // 状态监听器列表

    private static final long TIMEOUT = 30000; // 超时时间：10秒
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，处理延迟和超时
    private Runnable timeoutRunnable = () -> {
    };
    private int retryCount = 0; // 重试次数

// 在 HuodaoController 类中添加以下代码

    // 最大重试次数（可根据需求调整）
    private static final int MAX_RETRIES = 1;
    // 重试间隔时间（毫秒）
    private static final long RETRY_DELAY = 1000;
    // 单次执行超时时间（毫秒）
    private static final long EXECUTE_TIMEOUT = 30000;

    /**
     * 串口数据返回
     */
    @Override
    public void onReceivedData(final ComBean comBean, int size) {

//        Log.d(TAG, "run: " + comBean.sRecTime + " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec));
        String result = toHexString(comBean.bRec);
        StringUtil.sendTextCmd(result, context, sendCmd);

        try {

            HeatParser.getInstance().processString(comBean.bRec);
//            Logger.i(JSON.toJSONString(HeatParser.getInstance()));
        } catch (Exception e) {
            Logger.e("处理串口数据异常: " + e.getMessage());
        }
//        if (ClientConstant.IS_DOING)
//        recdLayerOperation(comBean.bRec);

        notifyStatusListeners(HexUtil.bytesToHex(comBean.bRec));
    }

    /**
     * 串口打开成功
     */
    @Override
    public void onSerialOpenSuccess() {
        Logger.i("串口打开成功");
        if (context != null && !context.isFinishing() && !context.isDestroyed()) {
            runOnUiThread(() -> {
                Toast.makeText(
                        context,  // 获取按钮所在的上下文
                        "串口打开成功",  // 提示消息（替换为你的message）
                        Toast.LENGTH_SHORT
                ).show();  // 显示弹窗
            });
        }
        ComponenTestHandler.getInstance().YaxisReset(); // 复位Y轴
    }

    /**
     * 串口打开异常
     */
    @Override
    public void onSerialOpenException(Exception e) {
        if (context != null && !context.isFinishing() && !context.isDestroyed()) {
            runOnUiThread(() -> {
                Toast.makeText(
                        context,  // 获取按钮所在的上下文
                        "串口打开异常",  // 提示消息（替换为你的message）
                        Toast.LENGTH_SHORT
                ).show();  // 显示弹窗
            });
        }

    }


    private static final class YpgLogicHandlerHolder {
        static final YpgLogicHandler ToDiLogicHandler = new YpgLogicHandler();
    }

    public static YpgLogicHandler getInstance() {
        return YpgLogicHandlerHolder.ToDiLogicHandler;
    }


    public void init(Activity context, TextView sendCmd, TextView recCmd) {
        this.sendCmd = sendCmd;
        this.recCmd = recCmd;
        this.context = context;
        sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        path = sp.getString("path", "/dev/ttyZC2");

        boolean haveUsb = false;
// 在Activity或Service中调用
        List<String> serialPorts = SerialHelper.getAllSerialPortPath();
        if (serialPorts.isEmpty()) {
            Log.d("SerialTest", "未检测到可用串口");
        } else {
            Log.d("SerialTest", "检测到" + serialPorts.size() + "个串口：");

            for (String port : serialPorts) {
                if (path.contains(port)) {
                    haveUsb = true;
                }
            }
//            if (!haveUsb) {
//                for (String port : serialPorts) {
//                    if (port.contains("USB")) {
//                        path = port;
//                        haveUsb = true;
//                    }
//                }
//            }
        }

        ComponenTestHandler.getInstance().setContext(context, sendCmd, recCmd);
        if (haveUsb) {
//            serialController.openSerialPort(path, baudrate, 0);
            serialController = new SerialHelper(path, baudrate);
            ComponenTestHandler.getInstance().init(serialController);
            serialController.setOnSerialListener(this);
            serialController.open();

        }
        try {
            ttsUtils = TTSUtils.getInstance(context);
        } catch (IllegalAccessException | InstantiationException e) {
        }

    }

    /**
     * @param layerNumber:  层数
     * @param buttonNumber: 第几个货道
     */
    public void handleLayerOperation(int layerNumber, int buttonNumber) {
        Logger.i(ClientConstant.currentWorkFlow+"layerNumber "+layerNumber+" buttonNumber"+buttonNumber);
        switch (ClientConstant.currentWorkFlow) {
            case Forward:
                ComponenTestHandler.getInstance().turnGoodsChannel(buttonNumber);
                ClientConstant.IS_DOING = false;
                break;
            case Backward:
                ComponenTestHandler.getInstance().rollbackGoodsChannel(buttonNumber);
                ClientConstant.IS_DOING = false;
                break;
            case Stop:
                ComponenTestHandler.getInstance().stopGoodsChannel(buttonNumber);
                ClientConstant.IS_DOING = false;
                break;
            case Standard:
                isBackward = false;
                handleBackward(layerNumber, buttonNumber);
                break;
            case Recycle:
                isBackward = true;
                handleBackward(layerNumber, buttonNumber);
                break;
            case LiftHandler:
                ClientConstant.IS_DOING = false;

//                handleBackward(layerNumber, buttonNumber);
                break;
            case XAxis:
                ClientConstant.IS_DOING = false;
                if (layerNumber == 1) {
                    ComponenTestHandler.getInstance().XAxisForward();
                } else if (layerNumber == 2) {
                    ComponenTestHandler.getInstance().XAxisReversed();
                } else {
                    ComponenTestHandler.getInstance().XAxisStop();
                }

                break;
            case UpperSideDoor:
                ClientConstant.IS_DOING = false;
                if (layerNumber == 1) {
                    ComponenTestHandler.getInstance().upperSideForward();
                } else if (layerNumber == 2) {
                    ComponenTestHandler.getInstance().upperSideReversed();
                } else {
                    ComponenTestHandler.getInstance().upperSideStop();
                }

                break;
            case LowerSideDoor:
                ClientConstant.IS_DOING = false;
                if (layerNumber == 1) {
                    ComponenTestHandler.getInstance().lowerSideForward();
                } else if (layerNumber == 2) {
                    ComponenTestHandler.getInstance().lowerSideReversed();
                } else {
                    ComponenTestHandler.getInstance().lowerSideStop();
                }


                break;
            case RecyclingDoor:
                ClientConstant.IS_DOING = false;
                if (layerNumber == 1) {
                    ComponenTestHandler.getInstance().recycleSideForward();
                } else if (layerNumber == 2) {
                    ComponenTestHandler.getInstance().recycleSideReversed();
                } else {
                    ComponenTestHandler.getInstance().recycleSideStop();
                }

                break;
            case PickUpDoor:
                ClientConstant.IS_DOING = false;
                if (layerNumber == 1) {
                    ComponenTestHandler.getInstance().pickUpSideForward();
                } else if (layerNumber == 2) {
                    ComponenTestHandler.getInstance().pickUpSideReversed();
                } else {
                    ComponenTestHandler.getInstance().pickUpSideStop();
                }

                break;
            case SingleLayerTesting:
                ClientConstant.IS_DOING = false;

                ComponenTestHandler.getInstance().YaxisGotoLevel(layerNumber);
                break;
            case LayerSetting:

                break;
            default:
                break;

        }
    }

    public boolean isBackward = false;

    public void handleBackward(int layerNumber, int buttonNumber) {

        ClientConstant.IS_DOING = true;
        ComponenTestHandler.getInstance().YaxisReset();

        Logger.d("步骤1：移动Y轴到层数=" + layerNumber);

        // 注册状态监听
        OnStatusListener statusHandler1 = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                Logger.d("收到状态：" + str);
                // 检测目标状态（忽略大小写，原js用indexOf）
                if (str.toLowerCase().contains("aa030c0303")) {
                    // 收到目标状态，移除监听并进入下一步
                    mainHandler.removeCallbacks(timeoutRunnable); // 取消超时
                    removeStatusListener(this);
                    // 进入步骤2：移动Y轴到指定层数
                    step2MoveYToLayer(layerNumber, buttonNumber);
                }
            }
        };
        // 注册监听
        addStatusListener(statusHandler1);

        // 设置超时任务
        timeoutRunnable = () -> {
            Logger.d("步骤1超时（10秒）");
            removeStatusListener(statusHandler1); // 移除监听
            step2MoveYToLayer(layerNumber, buttonNumber); // 超时后仍进入下一步
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT);
    }


    // 步骤2：根据层数移动Y轴到对应位置
    private void step2MoveYToLayer(int layer, int item) {
        // 根据层数调用不同的Y轴移动方法（原js的if-else逻辑）

        ComponenTestHandler.getInstance().YaxisGotoLevel(layer);

        // 等待Y轴移动完成（状态"aa030c0301"）
        OnStatusListener statusHandler2 = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                Logger.d("收到状态：" + str);
                if (str.toLowerCase().contains("aa030c0301")) {
                    mainHandler.removeCallbacks(timeoutRunnable);
                    removeStatusListener(this);
                    // 进入步骤3：电机正转
                    step3HuodaoForward(item);
                }
            }
        };

        addStatusListener(statusHandler2);

        // 超时任务
        timeoutRunnable = () -> {
            Logger.d("步骤2超时（10秒）");
            removeStatusListener(statusHandler2);
            step3HuodaoForward(item); // 超时后进入下一步
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT);
    }


    // 步骤3：电机正转（货道出货）
    private void step3HuodaoForward(int item) {
        Logger.d("步骤3：电机正转（货道号=" + item);
        // 调用电机正转方法（原js的huodao120）
        ComponenTestHandler.getInstance().turnGoodsChannel(item);

        // 等待掉货回调（状态"aa03080101bb"）
        OnStatusListener statusHandler3 = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                Logger.d("收到状态：" + str);
                if (str.toLowerCase().contains("aa03080101bb")) {
                    mainHandler.removeCallbacks(timeoutRunnable);
                    removeStatusListener(this);
                    // 进入步骤4：Y轴移动到取货口
                    step4MoveYToPickup(item);
                }
            }
        };

        addStatusListener(statusHandler3);

        // 超时任务
        timeoutRunnable = () -> {
            Logger.d("步骤3超时（10秒）");
            removeStatusListener(statusHandler3);
            step4MoveYToPickup(item); // 超时后进入下一步
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT * 3); // 延长超时时间，避免因电机转动时间过长导致超时
    }


    // 步骤4：Y轴到达取货口，电机反转
    private void step4MoveYToPickup(int item) {
        Logger.d("步骤4：Y轴移动到取货口");
        // 移动Y轴到取货口（原js的Yto91）
        if (!isBackward)
            ComponenTestHandler.getInstance().YaxisGotoLevel(10);
        else
            ComponenTestHandler.getInstance().YaxisGotoLevel(11);


        // 等待Y轴到达取货口的状态（"aa030c0301"）
        OnStatusListener statusHandler4 = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                Logger.d("收到状态：" + str);
                if (str.toLowerCase().contains("aa030c0301")) {
                    mainHandler.removeCallbacks(timeoutRunnable);
                    removeStatusListener(this);
                    // 进入步骤5：执行带重试的操作
                    startRetryLoop(item);
                }
            }
        };

        addStatusListener(statusHandler4);

        // 超时任务
        timeoutRunnable = () -> {
            Logger.d("步骤4超时（10秒）");
            removeStatusListener(statusHandler4);
            this.startRetryLoop(item);
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT);
    }


    /**
     * 重试循环的核心逻辑
     */
    private void startRetryLoop(int item) {
        if (!isBackward)
            ComponenTestHandler.getInstance().oneClickDelivery(); // 需在 DeviceCommunicator 中添加通用发送方法
        else
            ComponenTestHandler.getInstance().oneClickRecycling(); // 需在 DeviceCommunicator 中添加通用发送方法

        // 3. 定义状态监听器
        OnStatusListener statusHandler = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                // 检测目标状态（"aa030a0101bb"）
                if (str.toLowerCase().contains("aa030a0101bb")) {
                    Logger.d("收到成功状态，执行完成");



                    // 取消超时任务
                    mainHandler.removeCallbacks(timeoutRunnable);
                    // 移除监听器
                    removeStatusListener(this);
                    YReset(item);
                }
            }
        };

        // 4. 注册监听器
        addStatusListener(statusHandler);
        // 超时任务
        timeoutRunnable = () -> {
            Logger.d("步骤6超时（10秒）");
            removeStatusListener(statusHandler);
            YReset(item);
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT);
    }

    private void YReset(int item) {
        // 执行 Y 轴复位
        ComponenTestHandler.getInstance().YaxisReset();
        // 延迟500ms执行电机反转（原js的setTimeout 500）
        mainHandler.postDelayed(() -> {
            Logger.d("延迟500ms：电机反转");
            ComponenTestHandler.getInstance().rollbackGoodsChannel(item); // 电机反转
        }, 500);
        // 重置重试次数
        retryCount = 0;
        ClientConstant.IS_DOING = false;
    }


    /**
     * 处理执行失败的逻辑
     */
    private void handleExecuteFailed(String errorMsg) {
        Logger.e("指令执行失败：" + errorMsg);
        // 可添加失败后的处理，如显示错误提示、记录日志等
    }


    // 内部类：模拟itemIndex（货道号信息）
    public static class ItemIndex {
        public String text; // 货道号文本

        public ItemIndex(String text) {
            this.text = text;
        }
    }

    // 内部类：模拟layerIndex（层数信息）
    public static class LayerIndex {
        public int index; // 层数索引

        public LayerIndex(int index) {
            this.index = index;
        }
    }


    public void recdLayerOperation(byte[] data) {


//        switch (ClientConstant.currentWorkFlow) {
//            case Stop:
//
//                ComponenTestHandler.getInstance().turnGoodsChannel(buttonNumber);
//                break;
//            case Forward:
//
//                ComponenTestHandler.getInstance().rollbackGoodsChannel(buttonNumber);
//                break;
//            case Recycle:
//
//                ComponenTestHandler.getInstance().stopGoodsChannel(buttonNumber);
//                break;
//            case Backward:
//
//                break;
//            case Standard:
//
//                break;
//            default:
//                break;
//
//        }
    }

    // 状态监听接口（用于接收设备返回的状态字符串）
    public interface OnStatusListener {
        void onStatusReceived(String status);
    }


    /**
     * 注册状态监听器
     */
    public void addStatusListener(OnStatusListener listener) {
        if (!statusListeners.contains(listener)) {
            statusListeners.add(listener);
        }
    }

    /**
     * 移除状态监听器
     */
    public void removeStatusListener(OnStatusListener listener) {
        statusListeners.remove(listener);
    }

    // 通知所有状态监听器（切换到主线程）
    private void notifyStatusListeners(String status) {
        // 用主线程Handler回调，避免在子线程处理UI
        for (OnStatusListener listener : statusListeners) {
            listener.onStatusReceived(status);
        }
    }


    // 新增：清除所有状态监听（强制中断旧任务的关键）
    private void clearAllStatusListeners() {
        statusListeners.clear(); // 清空监听列表，避免旧监听干扰新任务
    }
    private boolean isSnapDevice = false;
    public int nowLevel = 0;
    public List<Integer> selectedLayerValues = new ArrayList<>();

    public void snapDevice(List<Integer> layerValues) {

        DataSourceOperator.getInstance().deleteQrCodeBindingDao(); // 清空数据库

        Logger.i("layerValues"+ JSON.toJSONString(layerValues));

        // 1. 清除所有旧的状态监听（避免旧任务的状态回调影响新任务）
        clearAllStatusListeners();

        // 2. 移除所有未执行的超时任务（避免旧任务超时逻辑触发）
        mainHandler.removeCallbacksAndMessages(null); // 清除handler所有回调和消息

        // 3. 立即复位设备（无论旧任务执行到哪一步，强制回到初始状态）
        ComponenTestHandler.getInstance().YaxisReset();

        // 4. 重置任务相关变量（清空旧任务状态）
        isSnapDevice = false; // 强制标记任务未执行
        nowLevel = 0; // 重置当前层级进度
        if (selectedLayerValues != null) {
            selectedLayerValues.clear(); // 清空旧的层级列表
        }

        // ---------------------------------------------------

        // 显示“正在一键拍照”提示（替换原有的重复检查提示）
        Toast.makeText(
                context,
                "正在一键拍照",
                Toast.LENGTH_SHORT
        ).show();
        isSnapDevice = true; // 标记新任务开始

        // 处理新的层级列表（原逻辑保留）
        this.selectedLayerValues = layerValues;
        removeDuplicates(); // 去重
        // 按bushu升序排序（原逻辑保留）
        Collections.sort(selectedLayerValues, (o1, o2) -> {
            int bushu1 = medicineCabinetLayer[o1].getBushu();
            int bushu2 = medicineCabinetLayer[o2].getBushu();
            return bushu1 - bushu2;
        });
        Logger.i("新任务层级列表：" + selectedLayerValues.toString());

        // 启动新任务的第一步：Y轴复位（原逻辑保留，但此时已在开头强制复位过，可省略或保留）
        ComponenTestHandler.getInstance().YaxisReset();

        // 注册新的状态监听（原逻辑保留，但监听已被清空，重新注册新监听）
        OnStatusListener statusHandler1 = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                Logger.d("新任务收到状态：" + str);
                if (str.toLowerCase().contains("aa030c0303")) {
                    mainHandler.removeCallbacks(timeoutRunnable);
                    removeStatusListener(this); // 移除当前监听
                    snapAllLayer(); // 进入下一步
                }
            }
        };
        addStatusListener(statusHandler1); // 添加到新的监听列表

        // 设置新任务的超时逻辑（原逻辑保留）
        timeoutRunnable = () -> {
            Logger.d("新任务步骤1超时（10秒）");
            removeStatusListener(statusHandler1);
            snapAllLayer();
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT);
    }

    public void removeDuplicates() {
        if (selectedLayerValues == null || selectedLayerValues.isEmpty()) {
            return;
        }
        // 将列表转换为HashSet（自动去重）
        HashSet<Integer> uniqueSet = new HashSet<>(selectedLayerValues);
        // 清空原列表并添加去重后的元素
        selectedLayerValues.clear();
        selectedLayerValues.addAll(uniqueSet);
    }

    public void snapAllLayer() {

        if (nowLevel >= selectedLayerValues.size()) {

            ComponenTestHandler.getInstance().YaxisReset();
            isSnapDevice = false;
            return;
        }

        int Level = this.selectedLayerValues.get(nowLevel);
        Level += 1;
        Logger.d("步骤2：移动Y轴到层数=" + Level);
        // 根据层数调用不同的Y轴移动方法（原js的if-else逻辑）

        ComponenTestHandler.getInstance().YaxisGotoLevel(Level);

        // 等待Y轴移动完成（状态"aa030c0301"）
        OnStatusListener statusHandler2 = new OnStatusListener() {
            @Override
            public void onStatusReceived(String str) {
                Logger.d("收到状态：" + str);
                if (str.toLowerCase().contains("aa030c0301")) {
                    mainHandler.removeCallbacks(timeoutRunnable);
                    removeStatusListener(this);
                    nowLevel++;
                    snapTwoCamera();
                }
            }
        };

        addStatusListener(statusHandler2);

        // 超时任务
        timeoutRunnable = () -> {
            Logger.d("步骤2超时（10秒）");
            removeStatusListener(statusHandler2);
            nowLevel++;
            snapTwoCamera();
        };
        mainHandler.postDelayed(timeoutRunnable, TIMEOUT);


    }


    public void snapTwoCamera() {
        // 第一个任务：发送第一个EventBus事件（立即执行，之后延迟500ms执行第二个任务）
        mainHandler.post(() -> {
            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.SNAP_CAMERA_NUM, 0,(ClientConstant.nowFloor +1)));

            // 第二个任务：延迟500ms发送第二个EventBus事件
            mainHandler.postDelayed(() -> {
                EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.SNAP_CAMERA_NUM, 1,(ClientConstant.nowFloor +1)));

                // 第三个任务：再延迟500ms执行snapAllLayer()
                mainHandler.postDelayed(this::snapAllLayer, 500); // 第二个到第三个间隔500ms
            }, 500); // 第一个到第二个间隔500ms
        });
    }



    private boolean isOpenAllGoodsChannel = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    // 存储所有「层数-货道编号」的关联数据
    private List<LayerNumber> allLayerNumbers = new ArrayList<>();
    private int currentIndex = 0;

    // 一键开所有货道
    public void openAllGoodsChannel() {
//        if (isOpenAllGoodsChannel) {
//            Log.d("OpenAll", "所有货道已开启，忽略重复调用");
//            Toast.makeText(context, "所有货道已开启，忽略重复调用", Toast.LENGTH_SHORT).show();
//            return;
//        }
        isOpenAllGoodsChannel = true;
        allLayerNumbers.clear(); // 清空历史数据
        currentIndex = 0; // 重置索引

        // 定义各层货道范围


        // 2. 遍历所有层，存储「层数-货道编号」关联数据
        for (LayerParam param : ClientConstant.layerParams) {
            int layerId = param.getLayerNumber(); // 获取当前层数（1-8）
            // 遍历当前层的所有货道编号，关联层数并存储
            for (int num = param.getStartNum(); num <= param.getEndNum(); num++) {
                allLayerNumbers.add(new LayerNumber(layerId, num));
            }
        }

        // 3. 开始定时操作货道（每隔500ms）
        printNextNumber();
    }
    public void handleCurrentLayerTest(int currentLayer){
        isOpenAllGoodsChannel = true;
        allLayerNumbers.clear(); // 清空历史数据
        currentIndex = 0; // 重置索引

        // 定义各层货道范围
        List<LayerParam> layerParams = new ArrayList<LayerParam>() {{
            add(new LayerParam(1, "T1", 1, 8));    // 层数1（T1）对应货道1-8
            add(new LayerParam(2, "T2", 16, 23));  // 层数2（T2）对应货道16-23
            add(new LayerParam(3, "T3", 31, 40));  // 层数3（T3）对应货道31-40
            add(new LayerParam(4, "T4", 46, 55));  // 层数4（T4）对应货道46-55
            add(new LayerParam(5, "T5", 61, 72));  // 层数5（T5）对应货道61-72
            add(new LayerParam(6, "T6", 76, 87));  // 层数6（T6）对应货道76-87
            add(new LayerParam(7, "T7", 91, 102)); // 层数7（T7）对应货道91-102
            add(new LayerParam(8, "T8", 106, 117));// 层数8（T8）对应货道106-117
            add(new LayerParam(9,"T9", 121, 132));
        }};

        // 2. 遍历所有层，存储「层数-货道编号」关联数据
        for (LayerParam param : ClientConstant.layerParams) {
            int layerId = param.getLayerNumber(); // 获取当前层数（1-8）
            if(currentLayer == layerId){
                // 遍历当前层的所有货道编号，关联层数并存储
                for (int num = param.getStartNum(); num <= param.getEndNum(); num++) {
                    allLayerNumbers.add(new LayerNumber(layerId, num));
                }
            }
        }

        // 3. 开始定时操作货道（每隔500ms）
        printNextNumber();
    }

    // 递归调用：定时操作下一个货道（同时获取层数和货道编号）
    private void printNextNumber() {
        ClientConstant.WorkFlow WorkFlow = ClientConstant.currentWorkFlow;
        if (currentIndex < allLayerNumbers.size()) {
            // 获取当前货道的「层数」和「编号」
            LayerNumber current = allLayerNumbers.get(currentIndex);
            int layerId = current.getLayerId(); // 层数（1-8）
            int channelNumber = current.getChannelNumber(); // 货道编号

            // 打印日志（包含层数和货道编号）
            Log.d("OpenChannel", "层数: " + layerId + "，货道编号: " + channelNumber);


            switch (WorkFlow) {
                case Forward:
                    ComponenTestHandler.getInstance().turnGoodsChannel(channelNumber);
                    ClientConstant.IS_DOING = false;
                    break;
                case Backward:
                    ComponenTestHandler.getInstance().rollbackGoodsChannel(channelNumber);
                    ClientConstant.IS_DOING = false;
                    break;
                default:
                    ComponenTestHandler.getInstance().stopGoodsChannel(channelNumber);
                    ClientConstant.IS_DOING = false;
                    break;
            }

            // 准备下一个
            currentIndex++;
            // 500ms后执行下一个
            mHandler.postDelayed(this::printNextNumber, 1000);
        } else {
            // 所有货道操作完成
            Log.d("OpenChannel", "所有货道操作完成");
            allLayerNumbers.clear();
            isOpenAllGoodsChannel = false; // 重置状态
        }
    }


}
