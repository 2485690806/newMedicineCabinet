//package com.ycmachine.smartdevice.activity;
//
//
//import static android.content.ContentValues.TAG;
//import static com.example.myapplication1.utils.AppUpdater.zcApi;
//import static com.example.myapplication1.utils.FileUtils.getDeviceIdByGetImei;
//import static com.example.myapplication1.utils.TrayUtils.compareWeights;
//import static com.example.myapplication1.utils.TrayUtils.getCmd;
//import static com.example.myapplication1.utils.TrayUtils.getLeftWeight;
//import static com.example.myapplication1.utils.TrayUtils.getRightWeight;
//import static com.example.myapplication1.utils.TrayUtils.toHexString;
//import static com.example.myapplication1.utils.TrayUtils.toVoiceString;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.SystemClock;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.GridLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.cardview.widget.CardView;
//import androidx.core.content.ContextCompat;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.example.myapplication1.R;
//import com.example.myapplication1.entity.DeviceGoodsEntity;
//import com.example.myapplication1.entity.DeviceStatusEntity;
//import com.example.myapplication1.entity.FinishOrderEntity;
//import com.example.myapplication1.entity.GetDeviceGoodsEntity;
//import com.example.myapplication1.entity.GoodsChangeEntity;
//import com.example.myapplication1.entity.IcCardEntity;
//import com.example.myapplication1.entity.Software;
//import com.example.myapplication1.entity.StoreGoodsEntity;
//import com.example.myapplication1.entity.SxgRequestEntity;
//import com.example.myapplication1.entity.TemHumEntity;
//import com.example.myapplication1.entity.TrayWeightEntity;
//import com.example.myapplication1.entity.VersionEntity;
//import com.example.myapplication1.utils.AppUpdater;
//import com.example.myapplication1.utils.CreateQRCode;
//import com.example.myapplication1.utils.DeviceIdUtil;
//import com.example.myapplication1.utils.dialog.LoadingDialog;
//import com.example.myapplication1.utils.dialog.OpenDoorSuccessDialog;
//import com.example.myapplication1.utils.dialog.PaySuccessDialog;
//import com.example.myapplication1.utils.serial.SerialHelper;
//import com.zhangke.websocket.SimpleListener;
//import com.zhangke.websocket.SocketListener;
//import com.zhangke.websocket.WebSocketHandler;
//import com.zhangke.websocket.WebSocketManager;
//import com.zhangke.websocket.WebSocketSetting;
//import com.zhangke.websocket.response.ErrorResponse;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import android_serialport_api.SerialPortFinder;
//import tp.xmaihh.serialport.bean.ComBean;
//import tp.xmaihh.serialport.utils.ByteUtil;
//
////,CoinSerialInter
//public class ActivityBuy extends Activity implements View.OnClickListener {
//
//    String path = "/dev/ttyS4";
//    int baudrate = 9600;
//
//    String storeId;
//
//    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//
//    private SerialPortFinder serialPortFinder;
//    private SerialHelper serialHelper;
//
//    private Integer freeId0, freeId1;    // 支付的订单id
//    private Integer isVip0 = 0, isVip1 = 0;    // 当前左、右边柜是否是会员，0:不是 1:是
//
//    private List<StoreGoodsEntity> leftGoods = new ArrayList<>(), rightGoods = new ArrayList<>();    // 当前左、右边柜的商品列表
//
//
//    private boolean getWeight0 = false, getWeight1 = false; // 是否获取重量,0:1号柜 1:2号柜
//
//    private boolean BeForeGetWeight0 = false, BeForeGetWeight1 = false; // 开柜前获取重量,0:1号柜 1:2号柜
//
//    private List<Integer> LeftWeightList = new ArrayList<>(5); // 左边柜子原重量
//    private List<Integer> RightWeightList = new ArrayList<>(5); // 右边柜子原重量
//
//    private List<Integer> LeftNowWeightList = new ArrayList<>(5); // 左边柜子现重量
//    private List<Integer> RightNowWeightList = new ArrayList<>(5); // 右边柜子现重量
//
//    private boolean readSoftwareVersion = false;
//
//    private boolean isReadTemHum = false; // 读取温湿度
//
//    private boolean isDeviceNum0 = false, isDeviceNum1 = false; // 0:1号柜 1:2号柜
//
//    private boolean isCheckDeviceNum0 = false, isCheckDeviceNum1 = false; // 0:1号柜 1:2号柜
//    private int doorNum = 0;
//
//    private Timer TimeHandler2;
//
//    private Timer GetWeightTime;
//
//    PaySuccessDialog paySuccessDialog;
//    OpenDoorSuccessDialog openDoorSuccessDialog;
//    LoadingDialog loadingDialog;
//
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_buy);
//
//
//        zcApi.getContext(getApplicationContext());//卓策主板操作
//
////        CoinSerialManage.getInstance().init(this);//串口初始化
//
//
//        String deviceIdByGetImei = "";
//        try {
//            deviceIdByGetImei = getDeviceIdByGetImei(this, 0);
//            storeId = deviceIdByGetImei.substring(deviceIdByGetImei.length() - 6);
//
//        } catch (Exception e) {
//            deviceIdByGetImei = DeviceIdUtil.getDeviceId(this);
//            storeId = deviceIdByGetImei;
//
//        }
//
//
//        // 初始化websocket
//        initWebSocket();
//        Log.d(TAG, "onCreate: " + storeId);
//
//
//        Context ctx = ActivityBuy.this;
//        SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
//        String path1 = sp.getString("path", "none");
//        if (!path1.equals("none") && !path1.equals("")) {
//            this.path = path1;
//            Log.d(TAG, "onCreate: " + path);
//
//        }
//
////        CoinSerialManage.getInstance().open(path, baudrate);//打开串口
//        initSerial();//串口初始化
//
//        initActivity(); // 初始化界面
//
//        setAdminClick(); // 设置管理员点击事件
//
//        try {
//            serialHelper.open();
//        } catch (IOException e) {
//
//        }
//
////        this.getPath();
//
//        sendCmd(toVoiceString("欢迎使用智能货柜"));
//
//        PollingTask(); // 循环获取锁的状态
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        // 延迟 3 秒后执行任务
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                sendCmd("010300030014B5C5"); // 获取重量
//
//                getWeightTask();
//            }
//        }, 4000); // 3000 毫秒 = 3 秒
//
//
//    }
//
//    private void initSerial() {
//        serialPortFinder = new SerialPortFinder();
//        serialHelper = new SerialHelper(path, baudrate) {
//            @Override
//            protected void onDataReceived(final ComBean comBean) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "run: " + comBean.sRecTime + " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec));
//                        CoinReadData(comBean.bRec);
////                            Log.d(TAG,  comBean.sRecTime + " Rx:<==" + new String(comBean.bRec, StandardCharsets.UTF_8));
//                    }
//                });
//            }
//        };
//    }
//
//
//    @Override
//    public void onClick(View v) {
//
//
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        WebSocketHandler.getDefault().removeListener(socketListener);
//    }
//
//    private WebSocketManager manager;
//
//    private void initActivity() {
//        String url1 = "https://sxgadmin.wteam.club/static/img/?deviceId=" + storeId + "&isDeviceNum=0";
//        String url2 = "https://sxgadmin.wteam.club/static/img/?deviceId=" + storeId + "&isDeviceNum=2";
//        Bitmap QRCode = CreateQRCode.createQRCodeBitmap(url1, 370, 370, "UTF-8", "L", "1", Color.BLACK, 0);
//        ((ImageView) findViewById(R.id.code1)).setImageBitmap(QRCode);
//        Bitmap QRCode2 = CreateQRCode.createQRCodeBitmap(url2, 370, 370, "UTF-8", "L", "1", Color.BLACK, 0);
//        ((ImageView) findViewById(R.id.code2)).setImageBitmap(QRCode2);
//
//        ((TextView) findViewById(R.id.deviceId)).setText("货柜编号：" + storeId);
//
//        paySuccessDialog = new PaySuccessDialog(this);
//        openDoorSuccessDialog = new OpenDoorSuccessDialog(this);
//        loadingDialog = new LoadingDialog(this);
//
//
//    }
//
//    private void initWebSocket() {
//        WebSocketSetting setting = new WebSocketSetting();
//        //连接地址，必填，例如 wss://echo.websocket.org
////        Log.d(TAG, "initWebSocket: "+"wss://sxgindex.wteam.club/SxgWebSocket?deviceId=" + storeId);
//        setting.setConnectUrl("wss://sxg-admin-api.linjukeji.com/SxgWebSocket?deviceId=" + storeId);//必填
////        setting.setConnectUrl("wss://sxgindex.wteam.club/SxgWebSocketServer/" + storeId);//必填
//
//        //设置连接超时时间
//        setting.setConnectTimeout(15 * 1000);
//
//        //设置心跳间隔时间
////        setting.setConnectionLostTimeout(0);
//        setting.setConnectionLostTimeout(60);
//
//        //设置断开后的重连次数，可以设置的很大，不会有什么性能上的影响
//        setting.setReconnectFrequency(99999999);
//
//        //网络状态发生变化后是否重连，
//        //需要调用 WebSocketHandler.registerNetworkChangedReceiver(context) 方法注册网络监听广播
//        setting.setReconnectWithNetworkChanged(true);
//
//        //通过 init 方法初始化默认的 WebSocketManager 对象
//        manager = WebSocketHandler.init(setting);
//        //启动连接
//        manager.start();
//
////        manager.sendPing();
//
//        //注意，需要在 AndroidManifest 中配置网络状态获取权限
//        //注册网路连接状态变化广播
//        WebSocketHandler.registerNetworkChangedReceiver(this);
//
//
//        WebSocketHandler.getDefault().addListener(socketListener);
//    }
//
//    private void startHeartbeat() {
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleWithFixedDelay(() -> {
//            if (manager.isConnect()) {
//                manager.sendPing(); // 发送 Ping 消息
//                System.out.println("Sent Ping to server");
//            }
//        }, 0, 20, TimeUnit.SECONDS); // 每 20 秒发送一次 Ping
//    }
//
//    private void showPaySuccessDialog() {
//        paySuccessDialog.show();
//        sendCmd(toVoiceString("关柜成功，欢迎下次光临"));
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        // 延迟 3 秒后执行任务
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                paySuccessDialog.cancel();
//
//            }
//        }, 3000); // 3000 毫秒 = 3 秒
//    }
//
//    private void showOpenDoorSuccessDialog() {
//        loadingDialog.cancel();
//        openDoorSuccessDialog.show();
//        sendCmd(toVoiceString("门已打开，请拉开柜门拿取商品"));
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        // 延迟 3 秒后执行任务
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openDoorSuccessDialog.cancel();
//
//            }
//        }, 3000); // 3000 毫秒 = 3 秒
//    }
//
//
//    private void showLoadingDialog() {
//        loadingDialog.show();
//        sendCmd(toVoiceString("开柜中，请稍等"));
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        // 延迟 3 秒后执行任务
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadingDialog.cancel();
//
//            }
//        }, 15000); // 3000 毫秒 = 3 秒
//    }
//
//    public void CoinReadData(byte[] bytes) {//串口数据回调
//
//        String result = toHexString(bytes);
////        Log.d(TAG, "CoinReadData: "+result);
//
//
//        if (result.contains("0103042")) { // 软硬件版本
//            String version = result.substring(6, 14); // 版本信息
//            sendVersionEntity(version, readSoftwareVersion);
//            return;
//        }
//
//        if (result.contains("010310")) { // 读取ic卡
//            String icCard1 = result.substring(5, 19);
//            String icCard2 = result.substring(19, 35);
//
//            String decimalValue1 = String.valueOf(Integer.parseInt(icCard1, 16));
//            String decimalValue2 = String.valueOf(Integer.parseInt(icCard2, 16));
//
//            sendIcCardEntity(decimalValue1, decimalValue2);
//
//            return;
//        }
//
//        // 读取温湿度和读取门状态也是这个指令
//        if (result.contains("010304")) {
//            if (isReadTemHum) { // 读取温湿度
//                isReadTemHum = false;
//
//                String temperature = result.substring(6, 10);
//                String humidity = result.substring(10, 14);
//
//                sendTemHumEntity(temperature, humidity);
//
//                return;
//            }
//
//            byte[] statusData = new byte[4];
//            try {
//
//                // 提取状态数据（从索引 3 开始，提取 4 个字节）
//                System.arraycopy(bytes, 3, statusData, 0, 4);
//            } catch (Exception e) {
//                return;
//            }
//
//
//            // 解析每个字节的状态
//            int lock1Status = statusData[0] & 0xFF; // 1 号门锁状态 00—锁关闭状态 01—锁打开状态
//            int door1Status = statusData[1] & 0xFF; // 1 号柜门状态 00—门打开状态 01—门关闭状态
//            int lock2Status = statusData[2] & 0xFF; // 2 号门锁状态
//            int door2Status = statusData[3] & 0xFF; // 2 号柜门状态
//
//
////            Log.d(TAG, "CoinReadData: " + lock1Status + " " + door1Status + " " + lock2Status + " " + door2Status);
//
//            if (!isCheckDeviceNum0) {
//                // 左边柜子不处在检测状态
//
//                if (isDeviceNum0) { // 左边柜子租借状态，查左边柜子门锁情况
//
//                    if (lock1Status == 0 && door1Status == 1) { // 锁关闭状态，门关闭状态，
//
//                        isDeviceNum0 = false;
//
////                        Handler handler = new Handler(Looper.getMainLooper());
////                        // 延迟 3 秒后执行任务
////                        handler.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
//                        getWeight0 = true;
////                            }
////                        }, 5000); // 3000 毫秒 = 3 秒
//                        getWeightTask();
//
//
//                    }
//                }
//
//                if (door1Status == 1 && lock1Status == 1) { // 1号柜门关闭，锁打开，发指令把他关上
//                    sendCmd(getCmd("010600040100"));
//                }
//
//            }
//
//            if (!isCheckDeviceNum1) {
//                if (isDeviceNum1) { // 右边柜子租借状态，查右边柜子门锁情况
//
//                    if (lock2Status == 0 && door2Status == 1) { // 锁关闭状态，门关闭状态，
//
//                        isDeviceNum1 = false;
//
////                        Handler handler = new Handler(Looper.getMainLooper());
////                        // 延迟 3 秒后执行任务
////                        handler.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
//                        getWeight1 = true;
////                            }
////                        }, 5000); // 3000 毫秒 = 3 秒
//
//                        getWeightTask();
//                    }
////                }
//
//                }
//
//                if (door2Status == 1 && lock2Status == 1) { // 1号柜门关闭，锁打开，发指令把他关上
//                    sendCmd(getCmd("010600040200"));
//                }
//            }
//
//            sendDoorStatus(lock1Status, door1Status, lock2Status, door2Status);
//
//        }
//
//        // 柜子重量
//        if (result.contains("010328")) { // 获取重量返回的数据
//            List<Integer> leftWeight = getLeftWeight(bytes);
//            List<Integer> rightWeight = getRightWeight(bytes);
//            if (leftWeight == null || rightWeight == null)
//                return; // 接着获取
//
//            if (rightWeight.size() < 5 || leftWeight.size() < 5)
//                return; // 接着获取
//
//
//            Log.d(TAG, "leftWeight: " + leftWeight);
//            Log.d(TAG, "rightWeight: " + rightWeight);
//
//            if (getWeight0) { // 1到10字节是1号柜的重量
//                getWeight0 = false;
//                isDeviceNum0 = false;
//                LeftNowWeightList = leftWeight;
//                SendFinishOrder(LeftNowWeightList, LeftWeightList, 0);
//
//
//                showPaySuccessDialog();
//                closeLeftView();
//                isVip0 = 0;
//
//
//                if (isVip1 == 0) {
//                    findViewById(R.id.richang).setVisibility(View.GONE);
//                    findViewById(R.id.vipTip).setVisibility(View.GONE);
//                }
//
//            }
//
//            if (getWeight1) { // 11到20字节是2号柜的重量
//                getWeight1 = false;
//                isDeviceNum1 = false;
//                RightNowWeightList = rightWeight;
//                SendFinishOrder(RightNowWeightList, RightWeightList, 1);
//
//
//                showPaySuccessDialog();
//                closeRightView();
//
//                isVip1 = 0;
//
//
//                if (isVip0 == 0) {
//                    findViewById(R.id.richang).setVisibility(View.GONE);
//                    findViewById(R.id.vipTip).setVisibility(View.GONE);
//                }
//            }
//
//            if (BeForeGetWeight0) { // 开柜前获取一次1号柜的重量
//                Log.d(TAG, "开柜前获取一次1号柜的重量: ");
//                BeForeGetWeight0 = false;
//                LeftWeightList = leftWeight;
//                isDeviceNum0 = true;
//
//                // 开1号柜
//                sendCmd("010600040101085B");
//
//                showOpenDoorSuccessDialog();
//                showLeftView();
//
//
//            }
//
//            if (BeForeGetWeight1) { // 11到20字节是2号柜的重量
//                BeForeGetWeight1 = false;
//                RightWeightList = rightWeight;
//                isDeviceNum1 = true;
//
//                // 开2号柜
//                sendCmd("01060004020108AB");
//
//                showOpenDoorSuccessDialog();
//
//                showRightView();
//            }
//
//            SendTrayWeight(leftWeight, rightWeight);
//
//
////            if (!BeForeGetWeight0 && !BeForeGetWeight1 && !getWeight0 && !getWeight1 && !isDeviceNum0 && !isDeviceNum1) {
////                stopPolling();
////            }
//
//        }
//
//        // ic卡
//
//
//    }
//
//    private void SendTrayWeight(List<Integer> LeftWeightList, List<Integer> RightWeightList) {
//
//        List<Integer> weightList = new ArrayList<>();
//        weightList.addAll(LeftWeightList);
//        weightList.addAll(RightWeightList);
//
//        TrayWeightEntity trayWeightEntity = new TrayWeightEntity();
//        trayWeightEntity.setWeight(weightList);
//        trayWeightEntity.setDeviceNum(storeId);
//
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "TrayWeightEntity");
//        jsonMessage.put("content", trayWeightEntity);
//
//
//        manager.send(JSON.toJSONString(jsonMessage));
//
//
//        rightWeightChange(RightWeightList);
//        letWeightChange(LeftWeightList);
//
//
//    }
//
//    private void letWeightChange(List<Integer> LeftWeightList){
//
//        if (isDeviceNum0) {
//            // 左边柜处在租借状态，判断左边柜的重量哪个格子发生变化（减少）了，然后加到grid中，且发送后台
//
//            Map<Integer, Integer> integerIntegerMap = compareWeights(LeftWeightList, this.LeftWeightList); // 1：当前重量，2：租借前重量
//
//            sendGoodsTray(integerIntegerMap, 0);
//            if (integerIntegerMap == null) { // 异常情况，提示他
//                findViewById(R.id.errortip1).setVisibility(View.VISIBLE);
//                return;
//            }
//            findViewById(R.id.errortip1).setVisibility(View.GONE);
//
//            // 获取GridLayout
//            GridLayout gridLayout = findViewById(R.id.grid1);
//
//// 设置GridLayout可见
//            gridLayout.setVisibility(View.VISIBLE);
//
//// 清空原有视图（可选）
//            gridLayout.removeAllViews();
//
//            Double totalPrice = 0.0;
//            for (Map.Entry<Integer, Integer> entry : integerIntegerMap.entrySet()) {
//                Integer key = entry.getKey();
//                Integer value = entry.getValue(); // 减少的重量
//                if (leftGoods == null || leftGoods.size() == 0) {
//                    return;
//                }
//                if (key >= leftGoods.size()) {
//                    return;
//                }
//                if (value == null) {
//                    return;
//                }
//                StoreGoodsEntity storeGoodsEntity = leftGoods.get(key);
//
//                if (storeGoodsEntity == null) {
//                    return;
//                }
//                if (storeGoodsEntity.getPieceWork() == null) {
//                    return;
//                }
//                if (storeGoodsEntity.getSpecifications() == null) {
//                    return;
//                }
//                if (storeGoodsEntity.getSalePrice() == null) {
//                    return;
//                }
////                if(storeGoodsEntity.getVipPrice() == null){
////                    return;
////                }
//
//
//                Double salePrice = 0.0; // 总价
//                String weight = ""; // 重量
//                Double goodsSalePrice = 0.0; // 单价
//                if (isVip0 == 1) {
//                    goodsSalePrice = storeGoodsEntity.getVipPrice() != null ? storeGoodsEntity.getVipPrice() : storeGoodsEntity.getSalePrice();
//
////                    goodsSalePrice = storeGoodsEntity.getVipPrice();
//                } else {
//                    goodsSalePrice = storeGoodsEntity.getSalePrice();
//                }
//
//                if (storeGoodsEntity.getPieceWork() == 0) {
//                    // 称重
//                    salePrice = value * (goodsSalePrice / storeGoodsEntity.getSpecifications()); // 减少的重量 * （售价/规格）
//
//                    weight = value + "克";
//                } else {
//                    // 计件
//                    int goodsNum = value / storeGoodsEntity.getSpecifications();
//                    salePrice = goodsSalePrice * goodsNum;
//
//                    weight = goodsNum + "个";
//                }
//
//                CardView card = createProductCard(this, storeGoodsEntity.getImageUrl(), storeGoodsEntity.getName(), weight, "￥" + decimalFormat.format(salePrice), isVip0, storeGoodsEntity.getIsSpecial());
//
//                gridLayout.addView(card);
//                totalPrice += salePrice;
//            }
//            ((TextView) findViewById(R.id.heji1)).setText("合计: ￥" + decimalFormat.format(totalPrice));
//
//
//        }
//
//
//    }
//    private void rightWeightChange(List<Integer> RightWeightList) {
//        if (isDeviceNum1) {
//            // 右边柜处在租借状态，判断右边柜的重量哪个格子发生变化（减少）了，然后加到grid中，且发送后台
//            Map<Integer, Integer> integerIntegerMap = compareWeights(RightWeightList, this.RightWeightList); // 1：当前重量，2：租借前重量
//
//            sendGoodsTray(integerIntegerMap, 1);
//            if (integerIntegerMap == null) { // 异常情况，提示他
//
//                findViewById(R.id.errortip2).setVisibility(View.VISIBLE);
//                return;
//            }
//            findViewById(R.id.errortip2).setVisibility(View.GONE);
//
//            // 获取GridLayout
//            GridLayout gridLayout = findViewById(R.id.grid2);
//
//// 设置GridLayout可见
//            gridLayout.setVisibility(View.VISIBLE);
//
//// 清空原有视图（可选）
//            gridLayout.removeAllViews();
//
//            Double totalPrice = 0.0;
//            for (Map.Entry<Integer, Integer> entry : integerIntegerMap.entrySet()) {
//                Integer key = entry.getKey();
//                Integer value = entry.getValue(); // 减少的重量
//                if (rightGoods == null || rightGoods.size() == 0) {
//                    return;
//                }
//                if (key >= rightGoods.size()) {
//                    return;
//                }
//                if (value == null) {
//                    return;
//                }
//
//                StoreGoodsEntity storeGoodsEntity = rightGoods.get(key);
//
//                if (storeGoodsEntity == null) {
//                    return;
//                }
//                if (storeGoodsEntity.getPieceWork() == null) {
//                    return;
//                }
//                if (storeGoodsEntity.getSpecifications() == null) {
//                    return;
//                }
//                if (storeGoodsEntity.getSalePrice() == null) {
//                    return;
//                }
////                if(storeGoodsEntity.getVipPrice() == null){
////                    return;
////                }
//
//
//                Double salePrice = 0.0; // 总价
//                String weight = ""; // 重量
//                Double goodsSalePrice = 0.0; // 单价
//                if (isVip1 == 1) {
//                    goodsSalePrice = storeGoodsEntity.getVipPrice() != null ? storeGoodsEntity.getVipPrice() : storeGoodsEntity.getSalePrice();
//                } else {
//                    goodsSalePrice = storeGoodsEntity.getSalePrice();
//                }
//
//                if (storeGoodsEntity.getPieceWork() == 0) {
//                    // 称重
//                    salePrice = value * (goodsSalePrice / storeGoodsEntity.getSpecifications()); // 减少的重量 * （售价/规格）
//
//                    weight = value + "克";
//                } else {
//                    // 计件
//                    int goodsNum = value / storeGoodsEntity.getSpecifications();
//                    salePrice = goodsSalePrice * goodsNum;
//
//                    weight = goodsNum + "个";
//                }
//
//                CardView card = createProductCard(this, storeGoodsEntity.getImageUrl(), storeGoodsEntity.getName(), weight, "￥" + decimalFormat.format(salePrice), isVip1, storeGoodsEntity.getIsSpecial());
//
//                gridLayout.addView(card);
//                totalPrice += salePrice;
//            }
//            ((TextView) findViewById(R.id.heji2)).setText("合计: ￥" + decimalFormat.format(totalPrice));
//        }
//    }
//
//
////    public void test(View view){
////
////        isDeviceNum0 = true;
////        showLeftView();
////
////        List<Integer> NowWeightList = new ArrayList<>();
////        NowWeightList.add(90);
////        NowWeightList.add(90);
////        NowWeightList.add(90);
////        NowWeightList.add(80);
////        NowWeightList.add(90);
////
////        List<Integer> WeightList = new ArrayList<>();
////        WeightList.add(100);
////        WeightList.add(100);
////        WeightList.add(100);
////        WeightList.add(100);
////        WeightList.add(100);
////        this.LeftWeightList = WeightList;
////
////        SendTrayWeight(NowWeightList, WeightList);
////
////    }
//
//    private SocketListener socketListener = new SimpleListener() {
//        @Override
//        public void onConnected() {
//            Log.d(TAG, "onConnected");
//
//            sendGetDeviceGoodsEntity();
//
//            startHeartbeat();
//
//        }
//
//        @Override
//        public void onConnectFailed(Throwable e) {
//            if (e != null) {
//                Log.d(TAG, "onConnectFailed:" + e.toString());
//            } else {
//                Log.d(TAG, "onConnectFailed:null");
//            }
//        }
//
//        @Override
//        public void onDisconnect() {
//            Log.d(TAG, "onDisconnect");
//        }
//
//        @Override
//        public void onSendDataError(ErrorResponse errorResponse) {
//            Log.d(TAG, "onSendDataError:" + errorResponse.toString());
//            errorResponse.release();
//        }
//
//        @Override
//        public <T> void onMessage(String message, T data) {
//            Log.d(TAG, "onMessage(String, T):---" + message + "----");
//
//            if (message.equals("pong")) {
//
//                manager.sendPong();
//                return;
//            }
//
//
//            JSONObject jsonMessage = JSON.parseObject(message);
//            sendRusultMessage(jsonMessage);
//
//
//            String message1 = jsonMessage.getString("message");
//
//            SxgRequestEntity sxgRequest = JSON.parseObject(message1, SxgRequestEntity.class);
//
//            if (sxgRequest.getType() == 0) { // 更新app
//
//                updateApp(sxgRequest.getMsg());
//                return;
//            }
//            if (sxgRequest.getType() == 15) { // 重启app
//
//                zcApi.reboot();
//                return;
//            }
//            if (sxgRequest.getType() == 17) { // 设置商品信息
//
//                setGoodsDevice(sxgRequest.getMsg());
//                return;
//            }
//
//
//            if (sxgRequest.getType() == 1) { // 开始租借，等到获取到重量后，再发开柜的指令
//
//                if (sxgRequest.getIsVip() == 1) {
//                    findViewById(R.id.richang).setVisibility(View.VISIBLE);
//                    findViewById(R.id.vipTip).setVisibility(View.VISIBLE);
//                }
//
//                if (sxgRequest.getIsDeviceNum() == 0) {
//
//                    BeForeGetWeight0 = true; //  开柜前获取重量
//                    freeId0 = sxgRequest.getFreeId();
//                    isVip0 = sxgRequest.getIsVip();
//                    showLoadingDialog();
//
//                    sendCmd("010300030014B5C5"); // 获取重量
//
//                } else {
//
//                    BeForeGetWeight1 = true;//  开柜前获取重量
//                    freeId1 = sxgRequest.getFreeId();
//                    isVip1 = sxgRequest.getIsVip();
//                    showLoadingDialog();
//
//                    sendCmd("010300030014B5C5"); // 获取重量
//
//                }
//                getWeightTask(); // 获取重量
//
//            } else
//                sendCmd(sxgRequest.getCmd());
//
//
//            if (sxgRequest.getVoice() != null && !sxgRequest.getVoice().equals("")) {
//
//
//                Handler handler = new Handler(Looper.getMainLooper());
//                // 延迟 3 秒后执行任务
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        sendCmd(sxgRequest.getVoice()); // 播放音频
//
//                    }
//                }, 1200); // 1秒再播放，防止串口指令冲突
//            }
//
//        }
//
//        @Override
//        public <T> void onMessage(ByteBuffer bytes, T data) {
//
//            Log.d(TAG, "onMessage(ByteBuffer, T):" + bytes);
//        }
//    };
//
//    private void setGoodsDevice(String data) {
//
//        DeviceGoodsEntity deviceGoods = JSON.parseObject(data, DeviceGoodsEntity.class);
//        if (deviceGoods == null) {
//
//            return;
//        }
//        leftGoods = deviceGoods.getLeftGoods();
//        rightGoods = deviceGoods.getRightGoods();
//
//        if (deviceGoods.getPhone() != null && !deviceGoods.getPhone().equals(""))
//            ((TextView) findViewById(R.id.textView)).setText("客服 " + deviceGoods.getPhone());
//
//    }
//
//    private void sendRusultMessage(JSONObject jsonMessage) { // 响应消息
//        String messageId = jsonMessage.getString("messageId");
//        JSONObject jsonMessage2 = new JSONObject();
//        jsonMessage2.put("type", "result");
//        jsonMessage2.put("messageId", messageId);
//
//        Log.d(TAG, "sendRusultMessage: " + JSON.toJSONString(jsonMessage2));
//        manager.send(JSON.toJSONString(jsonMessage2));
//    }
//
//
//    private void sendCmd(String cmd) {
//
//        if (cmd.contains("010600040101")) {
//            isCheckDeviceNum0 = true;
//            // 刚发了开1号柜，5秒再检测门的状态，防止没拉开就关了
//            // 创建一个 Handler，绑定到主线程的 Looper
//            Handler handler = new Handler(Looper.getMainLooper());
//
//            // 延迟 3 秒后执行任务
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isCheckDeviceNum0 = false;
//                }
//            }, 10000); // 3000 毫秒 = 3 秒
//        }
//        if (cmd.equals("01030004000285CA")) {  // 读取温湿度
//            isReadTemHum = true;
//        }
//
//        if (cmd.equals("01030001000295CB")) {  // 查询硬件版本
//            readSoftwareVersion = false;
//        }
//
//        if (cmd.equals("01030002000265CB")) {  // 查询软件版本
//            readSoftwareVersion = true;
//        }
//
//
//        if (cmd.contains("010600040201")) {
//            isCheckDeviceNum1 = true;
//            // 刚发了开1号柜，5秒再检测门的状态，防止没拉开就关了
//            // 创建一个 Handler，绑定到主线程的 Looper
//            Handler handler = new Handler(Looper.getMainLooper());
//
//            // 延迟 3 秒后执行任务
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isCheckDeviceNum1 = false;
//                }
//            }, 10000); // 3000 毫秒 = 3 秒
//        }
//
//        serialHelper.sendHex(cmd);  // 发送Hex
//
//    }
//
//
//    public void PollingTask() {
//
//
//        TimeHandler2 = new Timer();
//        TimeHandler2.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//                // 执行轮询任务
//                sendCmd("010300050002D40A");
//
//            }
//        }, 0, 3000); // 开柜后，每隔3秒获取一次门锁状态
//
//    }
//
//
//    public void stopPolling() { // 停止计时器
//
//        Log.d(TAG, "stopPolling: ");
////            TimeHandler.removeCallbacks(runnable); // 移除所有的Runnable
//        if (GetWeightTime != null)
//            GetWeightTime.cancel();
//
//    }
//
//
//    public void getWeightTask() {  // 担心发一次获取重量指令，没有返回数据，导致无法开柜卡死，所以每隔3秒获取一次重量
//
//        if (GetWeightTime != null)
//            GetWeightTime.cancel();
//
//        GetWeightTime = new Timer();
//        GetWeightTime.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // 执行轮询任务
//                sendCmd("010300030014B5C5"); // 获取重量
//            }
//        }, 0, 2000); // 5 秒
//
//    }
//
//
//    public void updateApp(String data) {
//        //获取当前版本号
//
//        Software software = JSON.parseObject(data, Software.class);
//        if (software == null) {
//
//            return;
//        }
//        Integer VERSION_CODE = 0;
//        try {
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            VERSION_CODE = packageInfo.versionCode;//获得版本号
//        } catch (Exception e) {
//            Log.e("获取系统信息", e.getMessage());
//        }
//
//        if (software.getPostCode() == VERSION_CODE) {
//            return;
//        }
//
//        System.out.println(software);
//        System.out.println("版本号不匹配，尝试更新");
//
//        sendCmd(toVoiceString("开始更新，请稍等"));
//
//
//        AppUpdater appUpdater = new AppUpdater(this, software.getPostUrl());
//        appUpdater.startUpdate();
//
//
//    }
//
//
//    /**
//     * 动态添加一个商品卡片到 GridLayout
//     *
//     * @param context   上下文
//     * @param imageUrl  图片资源ID
//     * @param name      商品名称
//     * @param weight    商品重量
//     * @param price     商品价格
//     * @param isSpecial 是否是特价商品
//     * @return 返回创建的CardView
//     */
//    private CardView createProductCard(Context context, String imageUrl, String name, String weight, String price, Integer isVip, Integer isSpecial) { // 当前租借的用户是否是会员用户
//        // 创建CardView
//        CardView cardView = new CardView(context);
//
//        // 设置CardView布局参数
//        GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
//        cardParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
//        cardParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
//        cardParams.setMargins(3, 3, 3, 3); // 设置margin
//        cardView.setLayoutParams(cardParams);
//
//        // 设置CardView属性
//        cardView.setCardBackgroundColor(Color.WHITE);
//        cardView.setRadius(dpToPx(context, 5)); // 圆角
//        cardView.setCardElevation(dpToPx(context, 10)); // 阴影
//        cardView.setPreventCornerOverlap(true);
//        cardView.setUseCompatPadding(false);
//
//        // 创建内部LinearLayout
//        LinearLayout linearLayout = new LinearLayout(context);
//        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                dpToPx(context, 250),
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        linearLayout.setPadding(0, dpToPx(context, 8), 0, 0);
//
//        // 添加ImageView
//        ImageView imageView = new ImageView(context);
//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
//                dpToPx(context, 168),
//                dpToPx(context, 80));
//        imageParams.bottomMargin = dpToPx(context, 4);
//        imageView.setLayoutParams(imageParams);
//
//
//        if (imageUrl == null || imageUrl.equals("null"))
//            imageUrl = "https://jkht.wteam.club/file/xiaochenxu/wutupian.png";
//
//
//        Glide.with(cardView).load(imageUrl).placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//
//
////        imageView.setImageResource(imageResId);
//
//        linearLayout.addView(imageView);
//
//        // 创建右侧内容LinearLayout
//        LinearLayout rightLayout = new LinearLayout(context);
//        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1)); // 设置weight
//        rightLayout.setOrientation(LinearLayout.VERTICAL);
//
//        // 添加商品名称TextView
//        TextView nameTextView = new TextView(context);
//        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                0,
//                1); // 设置weight
//        nameTextView.setLayoutParams(nameParams);
//        nameTextView.setGravity(Gravity.CENTER);
//        nameTextView.setText(name);
//        // 获取当前字体并添加BOLD样式
//        nameTextView.setTypeface(nameTextView.getTypeface(), Typeface.BOLD);
//        nameTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
//        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//        rightLayout.addView(nameTextView);
//
//        // 添加分割线ImageView
//        ImageView divider = new ImageView(context);
//        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
//                dpToPx(context, 200),
//                dpToPx(context, 1));
//        divider.setLayoutParams(dividerParams);
//        divider.setBackgroundColor(ContextCompat.getColor(context, R.color.dialog_gray));
//        rightLayout.addView(divider);
//
//        // 创建底部价格和重量的LinearLayout
//        LinearLayout bottomLayout = new LinearLayout(context);
//        bottomLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                0,
//                1)); // 设置weight
//        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//        // 添加重量TextView
//        TextView weightTextView = new TextView(context);
//        LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1); // 设置weight
//        weightTextView.setLayoutParams(weightParams);
//        weightTextView.setGravity(Gravity.CENTER_VERTICAL);
//        weightTextView.setText(weight);
//        weightTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
//        weightTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//        bottomLayout.addView(weightTextView);
//
//        // 添加价格TextView
//        TextView priceTextView = new TextView(context);
//        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                2); // 设置weight
//        priceTextView.setLayoutParams(priceParams);
//        priceTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//        priceTextView.setPadding(0, 0, dpToPx(context, 5), 0);
//
//        if (isVip == 1) {
//            priceTextView.setText("会员价: " + price);
//            priceTextView.setTextColor(ContextCompat.getColor(context, R.color.vip_color));
//        } else {
//
//            priceTextView.setText("市场价: " + price);
//            priceTextView.setTextColor(ContextCompat.getColor(context, R.color.actionsheet_red));
//        }
//
//        if (isSpecial == 1) {
//
//            priceTextView.setText("特价: " + price);
//            priceTextView.setTextColor(ContextCompat.getColor(context, R.color.vip_color));
//        }
//
//
//        priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//        bottomLayout.addView(priceTextView);
//
//        // 将底部布局添加到右侧布局
//        rightLayout.addView(bottomLayout);
//
//        // 将右侧布局添加到主LinearLayout
//        linearLayout.addView(rightLayout);
//
//        // 将LinearLayout添加到CardView
//        cardView.addView(linearLayout);
//
//        return cardView;
//    }
//
//    // dp转px的工具方法
//    private int dpToPx(Context context, float dp) {
//        return (int) (dp * context.getResources().getDisplayMetrics().density);
//    }
//
//
//    //连续点击5次进入设备设置
//    private final int DEVICE_CLICK_NUM = 5;
//    //点击时间间隔3秒
//    private final int DEVICE_CLICK_INTERVER_TIME = 1500;
//    //上一次的点击时间
//    private long DEVICE_lastClickTime = 0;
//    //记录点击次数
//    private int DEVICE_clickNum = 0;
//
//
//    private void setAdminClick() {
//        View toDeviceSetting = findViewById(R.id.deviceId);
//
//        toDeviceSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //点击的间隔时间不能超过5秒
//
//                long currentClickTime = SystemClock.uptimeMillis();
//                if (currentClickTime - DEVICE_lastClickTime <= DEVICE_CLICK_INTERVER_TIME
//                        || DEVICE_lastClickTime == 0) {
//                    DEVICE_lastClickTime = currentClickTime;
//                    DEVICE_clickNum = DEVICE_clickNum + 1;
//                } else {
//                    //超过5秒的间隔
//                    //重新计数 从1开始
//                    DEVICE_clickNum = 1;
//                    DEVICE_lastClickTime = 0;
//                    return;
//                }
//                if (DEVICE_clickNum == DEVICE_CLICK_NUM) {
//                    //重新计数
//                    DEVICE_clickNum = 0;
//                    DEVICE_lastClickTime = 0;
//                    /*实现点击多次后的事件*/
//                    showPasswordDialog();
//                }
//            }
//        });
//
//    }
//
//    /**
//     * 弹窗密码框
//     */
//    private void showPasswordDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("请输入密码");
//
//        final EditText input = new EditText(this);
//        builder.setView(input);
//
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String enteredPassword = input.getText().toString();
//
//                if (enteredPassword.equals("yc123")) {
//
//                    Intent intent = new Intent(ActivityBuy.this, MainActivity2.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        // 设置点击外部和返回键不关闭弹窗
//        builder.setCancelable(false);
//
//        builder.setNegativeButton("取消", null);
//
//        builder.show();
//    }
//
//
//    private void showLeftView() {
//        findViewById(R.id.heji1).setVisibility(View.VISIBLE);
//        findViewById(R.id.grid1).setVisibility(View.VISIBLE);
//        findViewById(R.id.code1).setVisibility(View.GONE);
//
//        GridLayout gridLayout = findViewById(R.id.grid1);
//// 设置GridLayout可见
//        gridLayout.setVisibility(View.VISIBLE);
//// 清空原有视图（可选）
//        gridLayout.removeAllViews();
//    }
//
//    private void closeLeftView() {
//        findViewById(R.id.heji1).setVisibility(View.GONE);
//        findViewById(R.id.grid1).setVisibility(View.GONE);
//        findViewById(R.id.code1).setVisibility(View.VISIBLE);
//
//        findViewById(R.id.errortip1).setVisibility(View.GONE);
//    }
//
//    private void showRightView() {
//        findViewById(R.id.heji2).setVisibility(View.VISIBLE);
//        findViewById(R.id.grid2).setVisibility(View.VISIBLE);
//        findViewById(R.id.code2).setVisibility(View.GONE);
//
//        GridLayout gridLayout = findViewById(R.id.grid2);
//// 设置GridLayout可见
//        gridLayout.setVisibility(View.VISIBLE);
//// 清空原有视图（可选）
//        gridLayout.removeAllViews();
//    }
//
//    private void closeRightView() {
//        findViewById(R.id.heji2).setVisibility(View.GONE);
//        findViewById(R.id.grid2).setVisibility(View.GONE);
//        findViewById(R.id.code2).setVisibility(View.VISIBLE);
//        findViewById(R.id.errortip2).setVisibility(View.GONE);
//    }
//
//    private void sendTemHumEntity(String temperature, String humidity) {
//        TemHumEntity temHumEntity = new TemHumEntity();
//        temHumEntity.setDeviceNum(storeId);
//        temHumEntity.setTemperature(temperature);
//        temHumEntity.setHumidity(humidity);
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "TemHumEntity");
//        jsonMessage.put("content", temHumEntity);
//
//        manager.send(JSON.toJSONString(jsonMessage));
//    }
//
//    private void sendGetDeviceGoodsEntity() { // 获取设备商品
//        GetDeviceGoodsEntity getDeviceGoodsEntity = new GetDeviceGoodsEntity();
//        getDeviceGoodsEntity.setDeviceNum(storeId);
//
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "GetDeviceGoodsEntity");
//        jsonMessage.put("content", getDeviceGoodsEntity);
//
//        manager.send(JSON.toJSONString(jsonMessage));
//    }
//
//    private void sendIcCardEntity(String icCard1, String icCard2) {
//        IcCardEntity icCardEntity = new IcCardEntity();
//        icCardEntity.setDeviceNum(storeId);
//        icCardEntity.setIcCardNum1(icCard1);
//        icCardEntity.setIcCardNum2(icCard2);
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "IcCardEntity");
//        jsonMessage.put("content", icCardEntity);
//
//        manager.send(JSON.toJSONString(jsonMessage));
//    }
//
//    private void sendVersionEntity(String versionNum, boolean readSoftwareVersion) {
//        VersionEntity versionEntity = new VersionEntity();
//        versionEntity.setDeviceNum(storeId);
//        versionEntity.setVersionNum(versionNum);
//        versionEntity.setReadSoftwareVersion(readSoftwareVersion);
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "VersionEntity");
//        jsonMessage.put("content", versionEntity);
//
//        manager.send(JSON.toJSONString(jsonMessage));
//    }
//
//
//    // 发送后台设置门锁状态
//    private void sendDoorStatus(int lock1Status, int door1Status, int lock2Status, int door2Status) {
//
//
//        doorNum++;
//        if (doorNum < 10)
//            return;
//        doorNum = 0;
//
//        DeviceStatusEntity deviceStatusEntity = new DeviceStatusEntity();
//        deviceStatusEntity.setDeviceLockA(lock1Status);
//        deviceStatusEntity.setDeviceLockB(lock2Status);
//        deviceStatusEntity.setDeviceStatusA(door1Status);
//        deviceStatusEntity.setDeviceStatusB(door2Status);
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "DeviceStatusEntity");
//        jsonMessage.put("content", deviceStatusEntity);
//
//        manager.send(JSON.toJSONString(jsonMessage));
//
//    }
//
//    private void SendFinishOrder(List<Integer> NowWeightList, List<Integer> WeightList, Integer isDeviceNum) {
//        Map<Integer, Integer> integerIntegerMap = compareWeights(NowWeightList, WeightList);
//
//        FinishOrderEntity finishOrderEntity = new FinishOrderEntity();
//
//        if (integerIntegerMap == null) {
//            finishOrderEntity.setIsNorMalOrder(0);
//        } else {
//            finishOrderEntity.setIntegerIntegerMap(integerIntegerMap);
//            finishOrderEntity.setIsNorMalOrder(1);
//        }
//        finishOrderEntity.setDeviceNum(storeId);
//        finishOrderEntity.setFreeId(isDeviceNum == 0 ? freeId0 : freeId1);
//        finishOrderEntity.setIsDeviceNum(isDeviceNum);
//
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "FinishOrderEntity");
//        jsonMessage.put("content", finishOrderEntity);
//
//
//        manager.send(JSON.toJSONString(jsonMessage));
//    }
//
//
//    private void sendGoodsTray(Map<Integer, Integer> integerIntegerMap, Integer isDeviceNum) { // 租借时，发送变化的商品信息
//
//
//        GoodsChangeEntity goodsChangeEntity = new GoodsChangeEntity();
//
//        if (integerIntegerMap == null) {
//            goodsChangeEntity.setIsNorMalOrder(0);
//        } else {
//            goodsChangeEntity.setIntegerIntegerMap(integerIntegerMap);
//            goodsChangeEntity.setIsNorMalOrder(1);
//        }
//        goodsChangeEntity.setDeviceNum(storeId);
//        goodsChangeEntity.setFreeId(isDeviceNum == 0 ? freeId0 : freeId1);
//        goodsChangeEntity.setIsDeviceNum(isDeviceNum);
//
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "GoodsChangeEntity");
//        jsonMessage.put("content", goodsChangeEntity);
//
//
//        manager.send(JSON.toJSONString(jsonMessage));
//
//    }
//
//
//}