//package com.ycmachine.smartdevice.activity;
//
//
//import static android.content.ContentValues.TAG;
//import static com.example.myapplication1.utils.AppUpdater.zcApi;
//import static com.example.myapplication1.utils.FileUtils.getDeviceIdByGetImei;
//import static com.example.myapplication1.utils.TrayUtils.compareWeights;
//import static com.example.myapplication1.utils.TrayUtils.convert;
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
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AlertDialog;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.example.myapplication1.R;
//import com.example.myapplication1.entity.DeviceStatusEntity;
//import com.example.myapplication1.entity.FinishOrderEntity;
//import com.example.myapplication1.entity.IcCardEntity;
//import com.example.myapplication1.entity.Software;
//import com.example.myapplication1.entity.SxgRequestEntity;
//import com.example.myapplication1.entity.TemHumEntity;
//import com.example.myapplication1.entity.TrayWeightEntity;
//import com.example.myapplication1.entity.VersionEntity;
//import com.example.myapplication1.utils.AppUpdater;
//import com.example.myapplication1.utils.DeviceIdUtil;
//import com.example.myapplication1.utils.net.TCPAsyncNetUtils;
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
//public class MainActivity2 extends Activity implements View.OnClickListener {
//
//    String path = "/dev/ttyS4";
//    int baudrate = 9600;
//
//    String storeId;
//    private TextView weighresult, weighsend;
//    private Button weigh_path_change, btn_left_lock, btn_right_lock,
//            get_weight, read_temperature_and_humidity, get_weight_model_status,
//            close_left_lock, close_right_lock;
//
//    protected Button mCameraChange, mCameraChange2;
//
//    protected TextView text_gallery;
//
//    private SerialPortFinder serialPortFinder;
//    private SerialHelper serialHelper;
//
//    private Integer freeId0, freeId1;    // 支付的订单id
//
//
//    private boolean getWeight0 = false, getWeight1 = false; // 是否获取重量,0:1号柜 1:2号柜
//
//    private boolean BeForeGetWeight0 = false, BeForeGetWeight1 = false; // 开柜前获取重量,0:1号柜 1:2号柜
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//
//        initActivity();
//
//        zcApi.getContext(getApplicationContext());//卓策主板操作
//
////        CoinSerialManage.getInstance().init(this);//串口初始化
//
//
////        BeatPollingTask(); // 心跳轮询
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
//        Context ctx = MainActivity2.this;
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
//            }
//        }, 4000); // 3000 毫秒 = 3 秒
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
//    private void initActivity() {
//        weighresult = findViewById(R.id.weighresult);
//        weighsend = findViewById(R.id.weighsend);
//        weigh_path_change = findViewById(R.id.weigh_path_change);
//        btn_left_lock = findViewById(R.id.btn_left_lock);
//        btn_right_lock = findViewById(R.id.btn_right_lock);
//        get_weight = findViewById(R.id.get_weight);
//        read_temperature_and_humidity = findViewById(R.id.read_temperature_and_humidity);
//        get_weight_model_status = findViewById(R.id.get_weight_model_status);
//        close_left_lock = findViewById(R.id.close_left_lock);
//        close_right_lock = findViewById(R.id.close_right_lock);
//
//        weigh_path_change.setOnClickListener(this);
//        btn_left_lock.setOnClickListener(this);
//        btn_right_lock.setOnClickListener(this);
//        get_weight.setOnClickListener(this);
//        read_temperature_and_humidity.setOnClickListener(this);
//        get_weight_model_status.setOnClickListener(this);
//        close_left_lock.setOnClickListener(this);
//        close_right_lock.setOnClickListener(this);
//
//
//    }
//
//    public void getPath() {
//
//        String url4 = "/cashier/getCashierPath?deviceNum=" + storeId;
//
//        // 从本地查询该条码是否存在
//        TCPAsyncNetUtils.get(url4, response -> {
//
//            Log.d(TAG, "run: " + response);
//            JSONObject data2 = JSON.parseObject(response);
//            String path = data2.getString("data");
//
//            if (!path.equals("null")) {
//
//
//                Context ctx = MainActivity2.this;
//                SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//
//                editor.putString("path", path);
//                editor.apply();
//
//                this.path = path;
//
//                try {
//                    serialHelper.close();
//                    serialHelper.open();
//                } catch (IOException e) {
//
//                }
////                CoinSerialManage.getInstance().open(path, baudrate);//打开串口
//                System.out.println(this.path);
//
//            }
//
//
//        });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//
//
//        if (v.getId() == R.id.weigh_path_change)
//            showInputDialog();
////       else if (v.getId() == R.id.btn_camera_change2) {
////            startNum = 0;
////            handlerGetStatus2.postDelayed(runnableGetStatus2, 5000);
////        }
//        else if (v.getId() == R.id.btn_left_lock) {// 开锁
//            sendCmd("010600040101085B");
//        } else if (v.getId() == R.id.btn_right_lock) {// 开锁
//            sendCmd("01060004020108AB");
//        } else if (v.getId() == R.id.close_left_lock) {// 关锁
//            sendCmd(getCmd("010600040100"));
//        } else if (v.getId() == R.id.close_right_lock) {// 关锁
//            sendCmd(getCmd("010600040200"));
//        } else if (v.getId() == R.id.get_weight) {
//
//            sendCmd("010300030014B5C5");
//
//        } else if (v.getId() == R.id.read_temperature_and_humidity) {
//
//            sendCmd("01030004000285CA");
//
//        } else if (v.getId() == R.id.get_weight_model_status) {
//
//            sendCmd("010300090013D405");
//
//        }
//    }
//
//
//    public void readSoftwareVersion(View view) {
//        sendCmd("01030002000265CB");
//    }
//
//    public void readHardwareVersion(View view) {
//        sendCmd("01030001000295CB");
//    }
//
//    public void readIcCard(View view) {
//        sendCmd("010300060008A40D");
//    }
//
//
//    public void ql(View view) {
//        String cmd1 = "";
//        String cmd = "";
//        if (view.getId() == R.id.layer_1_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(1);
//        }
//        if (view.getId() == R.id.layer_2_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(2);
//        }
//        if (view.getId() == R.id.layer_3_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(3);
//        }
//        if (view.getId() == R.id.layer_4_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(4);
//        }
//        if (view.getId() == R.id.layer_5_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(5);
//        }
//        if (view.getId() == R.id.layer_6_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(6);
//        }
//        if (view.getId() == R.id.layer_7_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(7);
//        }
//        if (view.getId() == R.id.layer_8_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(8);
//        }
//        if (view.getId() == R.id.layer_9_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(9);
//        }
//        if (view.getId() == R.id.layer_10_ql) { // 1号柜清零
//            cmd = "01060005000" + Integer.toHexString(10);
//        }
//        cmd1 = getCmd(cmd);
//        sendCmd(cmd1);
//    }
//
//
//    public void xz(View view) {
//        String cmd1 = "";
//        String cmd = "";
//        if (view.getId() == R.id.layer_1_xz) {
//            cmd = "01060007000" + Integer.toHexString(1);
//        }
//        if (view.getId() == R.id.layer_2_xz) {
//            cmd = "01060007000" + Integer.toHexString(2);
//        }
//        if (view.getId() == R.id.layer_3_xz) {
//            cmd = "01060007000" + Integer.toHexString(3);
//        }
//        if (view.getId() == R.id.layer_4_xz) {
//            cmd = "01060007000" + Integer.toHexString(4);
//        }
//        if (view.getId() == R.id.layer_5_xz) {
//            cmd = "01060007000" + Integer.toHexString(5);
//        }
//        if (view.getId() == R.id.layer_6_xz) {
//            cmd = "01060007000" + Integer.toHexString(6);
//        }
//        if (view.getId() == R.id.layer_7_xz) {
//            cmd = "01060007000" + Integer.toHexString(7);
//        }
//        if (view.getId() == R.id.layer_8_xz) {
//            cmd = "01060007000" + Integer.toHexString(8);
//        }
//        if (view.getId() == R.id.layer_9_xz) {
//            cmd = "01060007000" + Integer.toHexString(9);
//        }
//        if (view.getId() == R.id.layer_10_xz) {
//            cmd = "01060007000" + Integer.toHexString(10);
//        }
//        cmd1 = getCmd(cmd);
//        sendCmd(cmd1);
//
//    }
//
//    public void bqqp(View view) {
//        String cmd1 = "";
//        String cmd = "";
//        if (view.getId() == R.id.layer_1_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(1);
//        }
//        if (view.getId() == R.id.layer_2_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(2);
//        }
//        if (view.getId() == R.id.layer_3_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(3);
//        }
//        if (view.getId() == R.id.layer_4_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(4);
//        }
//        if (view.getId() == R.id.layer_5_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(5);
//        }
//        if (view.getId() == R.id.layer_6_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(6);
//        }
//        if (view.getId() == R.id.layer_7_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(7);
//        }
//        if (view.getId() == R.id.layer_8_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(8);
//        }
//        if (view.getId() == R.id.layer_9_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(9);
//        }
//        if (view.getId() == R.id.layer_10_bqqp) {
//            cmd = "01060008000" + Integer.toHexString(10);
//        }
//        cmd1 = getCmd(cmd);
//        sendCmd(cmd1);
//
//    }
//
//    public void bqsz(View view) {
//
////        String cmd1 = "";
////        String cmd = "";
//
//        String cmd = "0110000100050A0001" + convert("1.23");
//        Log.d(TAG, "bqsz: " + cmd);
//        if(view.getId()==R.id.layer_1_bqsz){
////            cmd = "0110000100050A0001312E322E332E342E15F6";
////            cmd = "0110000100050A000100312E320030006852";
//
////            cmd = "0110000100050A0001312E322E332E342E";
//            cmd = "0110000" + Integer.toHexString(1) + "00050A0001" + "00312E3233000000";
//        }
//        if(view.getId()==R.id.layer_2_bqsz){
//            cmd = "0110000" + Integer.toHexString(2) + "00050A0001" + convert("2.23");
//        }
//        if(view.getId()==R.id.layer_3_bqsz){
//            cmd = "0110000" + Integer.toHexString(3) + "00050A0001" + convert("3.23");
//        }
//        if(view.getId()==R.id.layer_4_bqsz){
//            cmd = "0110000" + Integer.toHexString(4) + "00050A0001" + convert("4.23");
//        }
//        if(view.getId()==R.id.layer_5_bqsz){
//            cmd = "0110000" + Integer.toHexString(5) + "00050A0001" + convert("5.23");
//        }
//        if(view.getId()==R.id.layer_6_bqsz){
//            cmd = "0110000" + Integer.toHexString(6) + "00050A0001" + convert("6.23");
//        }
//        if(view.getId()==R.id.layer_7_bqsz){
//            cmd = "0110000" + Integer.toHexString(7) + "00050A0001" + convert("7.23");
//        }
//        if(view.getId()==R.id.layer_8_bqsz){
//            cmd = "0110000" + Integer.toHexString(8) + "00050A0001" + convert("8.23");
//        }
//        if(view.getId()==R.id.layer_9_bqsz){
//            cmd = "0110000" + Integer.toHexString(9) + "00050A0001" + convert("9.23");
//        }
//        if(view.getId()==R.id.layer_10_bqsz){
//            cmd = "0110000" + Integer.toHexString(10) + "00050A0001" + convert("10.23");
//        }
//        String cmd1 = getCmd(cmd);
//        sendCmd(cmd1);
//
//    }
//
//
//
//    private void showInputDialog() {
//        /*@setView 装入一个EditView
//         */
//        final EditText editText = new EditText(this);
//        AlertDialog.Builder inputDialog =
//                new AlertDialog.Builder(this);
//        String Text = "修改串口，当前串口为：" + this.path;
//        editText.setText(this.path);
//        inputDialog.setTitle(Text).setView(editText);
//        inputDialog.setPositiveButton("确定",
//                (dialog, which) -> {
//
//
//                    path = editText.getText().toString();
//
//
//                    Context ctx = MainActivity2.this;
//                    SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sp.edit();
//
//                    editor.putString("path", path);
//                    editor.apply();
//
//
//                    serialHelper.close();
//                    try {
//                        serialHelper.open();
//                    } catch (IOException e) {
//
//                    }
////                    CoinSerialManage.getInstance().open(path, baudrate);//打开串口.
//
//
//                    dialog.dismiss();
//                });
//
//        inputDialog.setNegativeButton("关闭",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //...To-do
//                        dialog.dismiss();
//                    }
//
//                });
//        inputDialog.setCancelable(false);
//        inputDialog.show();
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
//    public void CoinReadData(byte[] bytes) {//串口数据回调
//
//        String result = toHexString(bytes);
////        Log.d(TAG, "CoinReadData: "+result);
//
//        receveCmd(result);
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
//
//            // 提取状态数据（从索引 3 开始，提取 4 个字节）
//            byte[] statusData = new byte[4];
//            System.arraycopy(bytes, 3, statusData, 0, 4);
//
//            // 解析每个字节的状态
//            int lock1Status = statusData[0] & 0xFF; // 1 号门锁状态 00—锁关闭状态 01—锁打开状态
//            int door1Status = statusData[1] & 0xFF; // 1 号柜门状态 00—门打开状态 01—门关闭状态
//            int lock2Status = statusData[2] & 0xFF; // 2 号门锁状态
//            int door2Status = statusData[3] & 0xFF; // 2 号柜门状态
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
//                        Handler handler = new Handler(Looper.getMainLooper());
//
//                        // 延迟 3 秒后执行任务
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                getWeight0 = true;
//
//                                getWeightTask();
//
//                            }
//                        }, 5000); // 3000 毫秒 = 3 秒
//
//                    }
//                }
//
//                if (door1Status == 1 && lock1Status == 1) { // 1号柜门关闭，锁打开，发指令把他关上
////                    sendCmd(getCmd("010600040100"));
//                }
//
//            }
//
//            if (!isCheckDeviceNum0) {
//                if (isDeviceNum1) { // 右边柜子租借状态，查右边柜子门锁情况
//
//                    if (lock2Status == 0 && door2Status == 1) { // 锁关闭状态，门关闭状态，
//
//                        isDeviceNum1 = false;
//                        Handler handler = new Handler(Looper.getMainLooper());
//
//                        // 延迟 3 秒后执行任务
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                getWeight1 = true;
//
//                                getWeightTask();
//
//                            }
//                        }, 5000); // 3000 毫秒 = 3 秒
//
//                    }
////                }
//
//                }
//
//                if (door2Status == 1 && lock2Status == 1) { // 1号柜门关闭，锁打开，发指令把他关上
////                    sendCmd(getCmd("010600040200"));
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
//                sendCmd(toVoiceString("关柜成功，欢迎下次光临"));
//
//            }
//
//            if (getWeight1) { // 11到20字节是2号柜的重量
//                getWeight1 = false;
//                isDeviceNum1 = false;
//                RightNowWeightList = rightWeight;
//                SendFinishOrder(RightNowWeightList, RightWeightList, 1);
//
//                sendCmd(toVoiceString("关柜成功，欢迎下次光临"));
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
//                sendCmd(toVoiceString("门已打开，请拉开柜门拿取商品"));
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
//                sendCmd(toVoiceString("门已打开，请拉开柜门拿取商品"));
//            }
//
//            SendTrayWeight(leftWeight, rightWeight);
//
//
//            if (!BeForeGetWeight0 && !BeForeGetWeight1 && !getWeight0 && !getWeight1) {
//                stopPolling();
//            }
//
//        }
//
//        // ic卡
//
//
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
//    private int doorNum = 0;
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
//        if(integerIntegerMap==null){
//            finishOrderEntity.setIsNorMalOrder(0);
//        }else{
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
//    public void readWeightWdx(View view){
//
//        sendCmd(getCmd("0103000A0014"));
//    }
//
//    public void testFinish(View view) {
//        List<Integer> NowWeightList = new ArrayList<>();
//        NowWeightList.add(100);
//        NowWeightList.add(100);
//        NowWeightList.add(100);
//        NowWeightList.add(100);
//        NowWeightList.add(100);
//
//        List<Integer> WeightList = new ArrayList<>();
//        WeightList.add(100);
//        WeightList.add(100);
//        WeightList.add(100);
//        WeightList.add(100);
//        WeightList.add(100);
//
//        Map<Integer, Integer> integerIntegerMap = compareWeights(WeightList, NowWeightList);
//
//
//        FinishOrderEntity finishOrderEntity = new FinishOrderEntity();
//        if(integerIntegerMap==null){
//            finishOrderEntity.setIsNorMalOrder(0);
//        }else{
//            finishOrderEntity.setIntegerIntegerMap(integerIntegerMap);
//            finishOrderEntity.setIsNorMalOrder(1);
//        }
//
//        finishOrderEntity.setDeviceNum(storeId);
//        finishOrderEntity.setFreeId(3350);
//        finishOrderEntity.setIsDeviceNum(0);
//
//
//        JSONObject jsonMessage = new JSONObject();
//        jsonMessage.put("type", "FinishOrderEntity");
//        jsonMessage.put("content", finishOrderEntity);
//
//
//        manager.send(JSON.toJSONString(jsonMessage));
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
//        List<String> list = new ArrayList<>();
//
//        for (int i = 0; i < LeftWeightList.size(); i++) {
//            Integer nowWeight = LeftWeightList.get(i);
//            String str = "";
//
//            if (nowWeight == 32767) {
//                str = "error";
//            } else if (nowWeight < 0) {
//                str = "0g";
//            } else
//                str = nowWeight.toString() + "g";
//            list.add(str);
//        }
//
//        for (int i = 0; i < RightWeightList.size(); i++) {
//            Integer nowWeight = RightWeightList.get(i);
//            String str = "";
//
//            if (nowWeight == 32767) {
//                str = "error";
//            } else if (nowWeight < 0) {
//                str = "0g";
//            } else
//                str = nowWeight.toString() + "g";
//            list.add(str);
//        }
//        ((TextView) findViewById(R.id.layer_1_weight)).setText(list.get(0));
//        ((TextView) findViewById(R.id.layer_2_weight)).setText(list.get(1));
//        ((TextView) findViewById(R.id.layer_3_weight)).setText(list.get(2));
//        ((TextView) findViewById(R.id.layer_4_weight)).setText(list.get(3));
//        ((TextView) findViewById(R.id.layer_5_weight)).setText(list.get(4));
//        ((TextView) findViewById(R.id.layer_6_weight)).setText(list.get(5));
//        ((TextView) findViewById(R.id.layer_7_weight)).setText(list.get(6));
//        ((TextView) findViewById(R.id.layer_8_weight)).setText(list.get(7));
//        ((TextView) findViewById(R.id.layer_9_weight)).setText(list.get(8));
//        ((TextView) findViewById(R.id.layer_10_weight)).setText(list.get(9));
//
//    }
//
//    private SocketListener socketListener = new SimpleListener() {
//        @Override
//        public void onConnected() {
//            Log.d(TAG, "onConnected");
//            startHeartbeat();
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
//            if(message.equals("pong")){
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
//
//
//            if (sxgRequest.getType() == 1) { // 开始租借，等到获取到重量后，再发开柜的指令
//
//                if (sxgRequest.getIsDeviceNum() == 0) {
//
//                    BeForeGetWeight0 = true; //  开柜前获取重量
//                    freeId0 = sxgRequest.getFreeId();
//
//                } else {
//
//                    BeForeGetWeight1 = true;//  开柜前获取重量
//                    freeId1 = sxgRequest.getFreeId();
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
//    private boolean isDeviceNum0 = false, isDeviceNum1 = false; // 0:1号柜 1:2号柜
//
//    private boolean isCheckDeviceNum0 = false, isCheckDeviceNum1 = false; // 0:1号柜 1:2号柜
//
//    public void openWHM(View view){
//
//        sendCmd("55aa0011020100bb");
//
//    }
//    public void backHome(View view){
//
//        Intent intent = new Intent(this, ActivityBuy.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent);
//
//    }
//
//    private void sendCmd(String cmd) {
//
//        if (cmd.equals("010600040101085B")) {
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
//            }, 4000); // 3000 毫秒 = 3 秒
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
//        if (cmd.equals("01060004020108AB")) {
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
//            }, 4000); // 3000 毫秒 = 3 秒
//        }
//
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                weighsend.setText(addString(cmd));
//            }
//        });
//
////        CoinSerialManage.getInstance().setQueueMsg(cmd);// 开锁
////        CoinSerialManage.getInstance().send(cmd);// 开锁
//        serialHelper.sendHex(cmd);  // 发送Hex
//
//    }
//
//    private void receveCmd(String cmd) {
//
//        weighresult.setText(addResultString(cmd));
//    }
//
//    private static final int MAX_SIZE = 10;
//    private ArrayList<String> stringList = new ArrayList<>(MAX_SIZE);
//    private ArrayList<String> resultstringList = new ArrayList<>(MAX_SIZE);
//
//    public String addString(String str) {
//        if (stringList.size() >= MAX_SIZE) {
//            stringList.remove(0); // 移除最早添加的字符串
//        }
//        stringList.add(str); // 添加新字符串
//
//        return String.join("\n", stringList);
//    }
//
//    public String addResultString(String str) {
//        if (resultstringList.size() >= MAX_SIZE) {
//            resultstringList.remove(0); // 移除最早添加的字符串
//        }
//        resultstringList.add(str); // 添加新字符串
//
//        return String.join("\n", resultstringList);
//    }
//
//
//    private Timer TimeHandler2;
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
//    private Timer GetWeightTime;
//
//    public void getWeightTask() {  // 担心发一次获取重量指令，没有返回数据，导致无法开柜卡死，所以每隔3秒获取一次重量
//
//
//        GetWeightTime = new Timer();
//        GetWeightTime.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // 执行轮询任务
//                sendCmd("010300030014B5C5"); // 获取重量
//            }
//        }, 0, 3000); // 5 秒
//
//    }
//
//
//    public void updateApp(String data) {
//        //获取当前版本号
//
//        Software software = JSON.parseObject(JSON.toJSONString(data), Software.class);
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
//
//
//
//
//    }
//
//
//
//
////    @Override
////    public void CoinReadData(String path, byte[] bytes, int size) {
////        Log.d(TAG, " Rx:<==" + ByteUtil.ByteArrToHex(bytes));
////        CoinReadData(bytes);
////
////    }
//}