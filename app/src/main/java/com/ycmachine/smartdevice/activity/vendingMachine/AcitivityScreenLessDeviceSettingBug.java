//package com.ycmachine.smartdevice.activity.vendingMachine;
//
//import static tp.xmaihh.serialport.utils.ByteUtil.Byte2Hex;
//
//import android.annotation.SuppressLint;
//import android.content.ClipData;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.SystemClock;
//import android.provider.Settings;
//import android.telephony.TelephonyManager;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.bigkoo.convenientbanner.ConvenientBanner;
//import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
//import com.bigkoo.convenientbanner.holder.Holder;
//import com.google.android.flexbox.FlexboxLayout;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.leesche.logger.Logger;
//import com.ycmachine.smartdevice.R;
//import com.ycmachine.smartdevice.R2;
//import com.ycmachine.smartdevice.enumclass.VideoType;
//import com.ycmachine.smartdevice.network.net.TCPAsyncNetUtils;
//import com.ycmachine.smartdevice.network.socket.MyWebSocketClient;
//import com.ycmachine.smartdevice.utils.DeviceIdUtil;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import android_serialport_api.SerialPortFinder;
//import butterknife.BindView;
//import butterknife.OnClick;
//import leesche.smartrecycling.base.BaseActivity;
//import leesche.smartrecycling.base.handler.LocalConfigManager;
//import leesche.smartrecycling.base.utils.RxTimer;
//import leesche.smartrecycling.base.utils.UriToPathUtil;
//import leesche.smartrecycling.base.view.LocalVImageHolderView;
//import tp.xmaihh.serialport.SerialHelper;
//import tp.xmaihh.serialport.bean.ComBean;
//import tp.xmaihh.serialport.utils.ByteUtil;
//
//public class AcitivityScreenLessDeviceSettingBug extends BaseActivity implements OnClickListener, RxTimer.OnTimeCounterListener {
//    // ======================== 基础变量 ========================
//    private FlexboxLayout linearLayout;
//    String path = "/dev/ttyS4";
//    int baudrate = 9600;
//    String storeId;
//    int Time = 100; // 闲置倒计时时间（秒）
//    public static MyWebSocketClient webSocketClient;
//    protected Button mCameraChange, mCameraChange2;
//    protected TextView text_gallery, text_result, text_status, text_temp;
//
//    // ======================== Banner相关变量 ========================
//    @BindView(R2.id.cIdleBanner)
//    ConvenientBanner commonBanner; // 共用Banner（原cIdleBanner，改名避免歧义）
//
//    @BindView(R2.id.viewStart)
//    View viewStart; // 闲置广告触发View
//    @BindView(R2.id.tvTimer)
//    TextView tvTimer; // 闲置倒计时
//    @BindView(R2.id.text_deviceid)
//    TextView textDeviceid;
//    @BindView(R2.id.mainlinearLayout)
//    LinearLayout mainlinearLayout; // 货道主布局
//
//    // 广告类型枚举（与VideoType对应，统一管理）
//    public enum AdType {
//        IDLE(VideoType.IDLE),    // 闲置广告（默认）
//        START(VideoType.START_DO_WATER),   // 开始出水广告
//        FINISH(VideoType.FINISH_DO_WATER), // 完成出水广告
//        CANCEL(VideoType.CANCEL_DO_WATER); // 取消出水广告
//
//        private final VideoType videoType;
//
//        AdType(VideoType videoType) {
//            this.videoType = videoType;
//        }
//
//        public VideoType getVideoType() {
//            return videoType;
//        }
//
//        // 根据VideoType获取对应的AdType
//        public static AdType getByVideoType(VideoType videoType) {
//            for (AdType type : AdType.values()) {
//                if (type.videoType == videoType) {
//                    return type;
//                }
//            }
//            return IDLE; // 默认返回闲置广告
//        }
//    }
//
//    private AdType currentAdType = AdType.IDLE; // 当前显示的广告类型
//    private List<LocalVImageHolderView> bannerHolders = new ArrayList<>(); // 共用Holder列表
//    private Handler adTimeoutHandler = new Handler(); // 广告超时处理器（用于自动恢复闲置广告）
//    private static final long START_AD_TIMEOUT = 120 * 1000; // Start广告超时时间（1分钟）
//
//    // ======================== 设备设置点击变量 ========================
//    private final int DEVICE_CLICK_NUM = 5;
//    private final int DEVICE_CLICK_INTERVER_TIME = 1500;
//    private long DEVICE_lastClickTime = 0;
//    private int DEVICE_clickNum = 0;
//
//    // ======================== 其他常量 ========================
//    private static final int REQUEST_CODE_SELECT_MEDIA = 100;
//    private Integer selectADType = 0; // 0:idle 1:start 2:finish 3:cancel
//    private Integer startNum = 0; // 货道测试计数
//    private SerialPortFinder serialPortFinder;
//    private SerialHelper serialHelper;
//    private Handler handlerGetStatus = new Handler();
//    private Handler handlerGetStatus2 = new Handler();
//
//
//    // ======================== 生命周期方法 ========================
//    @Override
//    protected void onDestroy() {
//        // 1. 停止Banner轮播和视频
//        if (commonBanner != null) {
//            commonBanner.stopTurning();
//        }
//        closeAllBannerHolders();
//
//        // 2. 清空所有Handler任务
//        if (handlerGetStatus != null) {
//            handlerGetStatus.removeCallbacksAndMessages(null);
//        }
//        if (handlerGetStatus2 != null) {
//            handlerGetStatus2.removeCallbacksAndMessages(null);
//        }
//        if (adTimeoutHandler != null) {
//            adTimeoutHandler.removeCallbacksAndMessages(null);
//        }
//
//        // 3. 停止计时器和串口
//        RxTimer.getInstance().stop();
//        if (serialHelper != null && serialHelper.isOpen()) {
//            serialHelper.close();
//        }
//        super.onDestroy();
//    }
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        linearLayout = (FlexboxLayout) findViewById(R.id.linearLayout);
//
//        // 初始化控件
//        initViews();
//        // 初始化货道按钮
//        initChannelButtons();
//        // 初始化串口
//        initSerial();
//        // 初始化设备ID
//        initDeviceId();
//        // 初始化服务器指令轮询
//        initServerCommandPoll();
//        // 从SP读取串口路径
//        readSerialPathFromSP();
//    }
//
//    @Override
//    public int initLayout() {
//        return R.layout.activity_screenless_device_setting; // 保持原布局
//    }
//
//    @Override
//    public void initData() {
//    }
//
//    @Override
//    public void initView(Bundle savedInstanceState) {
//        // 1. 初始化计时器
//        tvTimer = findViewById(R.id.tvTimer);
//        RxTimer.getInstance().start(tvTimer, Time, "ManagerLogin", this);
//
//        // 2. 加载所有广告数据（idle/start/finish/cancel）
//        LocalConfigManager.getInstance().loadAdDataFromLocal();
//        LocalConfigManager.getInstance().loadStartAdDataFromLocal();
//        LocalConfigManager.getInstance().loadFinishAdDataFromLocal();
//        LocalConfigManager.getInstance().loadCancelAdDataFromLocal();
//
//        // 3. 初始化共用Banner（默认显示闲置广告）
//        setBannerPages(AdType.IDLE);
//    }
//
//
//    // ======================== 初始化辅助方法 ========================
//
//    /**
//     * 初始化控件引用
//     */
//    private void initViews() {
//        mCameraChange = findViewById(R.id.btn_camera_change);
//        mCameraChange.setOnClickListener(this);
//        mCameraChange2 = findViewById(R.id.btn_camera_change2);
//        mCameraChange2.setOnClickListener(this);
//        text_gallery = findViewById(R.id.text_gallery);
//        text_result = findViewById(R.id.text_result);
//        text_status = findViewById(R.id.text_status);
//        text_temp = findViewById(R.id.text_temp);
//    }
//
//    /**
//     * 初始化货道按钮（1-60号）
//     */
//    private void initChannelButtons() {
//        for (int i = 0; i <= 60; i++) {
//            Button btnLesson = new Button(this);
//            btnLesson.setId(i);
//            btnLesson.setText("货道" + i);
//            btnLesson.setWidth(115);
//            btnLesson.setHeight(115);
//            btnLesson.setOnClickListener(new btnListener(btnLesson));
//            linearLayout.addView(btnLesson);
//        }
//    }
//
//    /**
//     * 初始化串口
//     */
//    private void initSerial() {
//        serialPortFinder = new SerialPortFinder();
//        serialHelper = new SerialHelper(path, baudrate) {
//            @Override
//            protected void onDataReceived(final ComBean comBean) {
//                runOnUiThread(() -> {
//                    Logger.d("串口接收: " + comBean.sRecTime + " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec));
//                    CoinReadData(comBean.bRec);
//                });
//            }
//        };
//        // 尝试打开串口
//        try {
//            serialHelper.open();
//        } catch (IOException e) {
//            Logger.e("串口打开失败: " + e.getMessage());
//            text_status.setText("串口异常: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 初始化设备ID
//     */
//    private void initDeviceId() {
//        String deviceIdByGetImei = "";
//        try {
//            deviceIdByGetImei = getDeviceIdByGetImei(this, 0);
//            storeId = deviceIdByGetImei.substring(deviceIdByGetImei.length() - 6);
//        } catch (Exception e) {
//            deviceIdByGetImei = DeviceIdUtil.getDeviceId(this);
//            storeId = deviceIdByGetImei;
//        }
//        Logger.d("设备编号: " + storeId);
//        textDeviceid.setText("设备编号" + storeId);
//    }
//
//    /**
//     * 初始化服务器指令轮询（3秒一次）
//     */
//    private void initServerCommandPoll() {
//        try {
//            handlerGetStatus.removeCallbacksAndMessages(null);
//            handlerGetStatus.postDelayed(runnableGetStatus, 3000);
//        } catch (Exception e) {
//            Logger.v("指令轮询初始化失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 从SP读取串口路径
//     */
//    private void readSerialPathFromSP() {
//        Context ctx = this;
//        SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
//        String savedPath = sp.getString("path", "none");
//        if (!"none".equals(savedPath)) {
//            this.path = savedPath;
//            Logger.d("从SP读取串口路径: " + path);
//            // 重新初始化串口
//            initSerial();
//        }
//    }
//
//    public void stopTimer(View view) {
//
//        RxTimer.getInstance().stop();
//    }
//
//    public void startTimer(View view) {
//
//        RxTimer.getInstance().start(tvTimer, Time, "ManagerLogin", this);
//    }
//
//    // ======================== 共用Banner核心逻辑 ========================
//
//    /**
//     * 设置Banner页面（根据广告类型加载对应数据源）
//     *
//     * @param adType 广告类型（IDLE/START/FINISH/CANCEL）
//     */
//    private void setBannerPages(AdType adType) {
//        if (commonBanner == null) return;
//
//        // 1. 清空旧状态（停止轮播、清空Holder、移除超时任务）
//        commonBanner.stopTurning();
//        bannerHolders.clear();
//        adTimeoutHandler.removeCallbacksAndMessages(null);
//        currentAdType = adType;
//
//        // 2. 根据广告类型获取数据源
//        List<?> adList = getAdListByType(adType);
//        if (adList == null || adList.isEmpty()) {
//            adList = getAdListByType(AdType.IDLE); // 无数据时默认显示闲置广告
//            currentAdType = AdType.IDLE;
//        }
//
//        // 3. 配置Banner布局（水平不可滚动）
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
//                LinearLayoutManager.HORIZONTAL, false) {
//            @Override
//            public boolean canScrollHorizontally() {
//                return false;
//            }
//        };
//        commonBanner.setLayoutManager(layoutManager);
//
//        // 4. 设置Banner页面（共用Holder）
//        commonBanner.setPages(new CBViewHolderCreator() {
//            @Override
//            public Holder createHolder(View itemView) {
//                LocalVImageHolderView holder = new LocalVImageHolderView(itemView, 0);
//                // 设置轮播回调（自动切换下一页）
//                holder.setCallback((curId, time) -> handleBannerTurn(curId, time));
//                bannerHolders.add(holder);
//                return holder;
//            }
//
//            @Override
//            public int getLayoutId() {
//                return R.layout.banner_item; // 共用广告布局
//            }
//        }, adList);
//
//        // 5. 启动轮播（5秒切换一次）
//        commonBanner.notifyDataSetChanged();
//        commonBanner.startTurning(5000);
//
//        // 6. Start广告特殊处理：设置1分钟超时，自动恢复闲置广告
//        if (adType == AdType.START) {
//            adTimeoutHandler.postDelayed(() -> {
//                runOnUiThread(() -> showAd(AdType.IDLE));
//            }, START_AD_TIMEOUT);
//        }
//    }
//
//    /**
//     * 根据广告类型获取数据源
//     */
//    private List<?> getAdListByType(AdType adType) {
//        switch (adType) {
//            case START:
//                return LocalConfigManager.getInstance().getStartBannerList();
//            case FINISH:
//                return LocalConfigManager.getInstance().getFinishBannerList();
//            case CANCEL:
//                return LocalConfigManager.getInstance().getCancelBannerList();
//            case IDLE:
//            default:
//                return LocalConfigManager.getInstance().getIdleBannerList();
//        }
//    }
//
//    /**
//     * Banner轮播切换逻辑（共用）
//     */
//    private void handleBannerTurn(int curId, long time) {
//        if (commonBanner == null) return;
//
//        commonBanner.postDelayed(() -> {
//            List<?> adList = getAdListByType(currentAdType);
//            if (curId < adList.size() - 1) {
//                commonBanner.setCurrentItem(curId + 1, false); // 下一页
//            } else {
//                commonBanner.setCurrentItem(0, false); // 最后一页切回第一页
//            }
//        }, time);
//    }
//
//    /**
//     * 显示指定类型的广告
//     */
//    private void showAd(AdType adType) {
//        if (commonBanner == null) return;
//
//        // 1. 停止闲置计时器
//        RxTimer.getInstance().stop();
//
//        // 2. 隐藏主布局（货道），显示Banner
//        mainlinearLayout.setVisibility(View.GONE);
//        commonBanner.setVisibility(View.VISIBLE);
////        viewStart.setVisibility(View.GONE); // 隐藏闲置触发View
//
//        // 3. 加载并显示目标广告
//        setBannerPages(adType);
//
//        // 4. 日志记录
//        Logger.d("显示广告类型: " + adType.name());
//    }
//
//    /**
//     * 隐藏广告，恢复主布局（货道）
//     */
//    private void hideAd() {
//        if (commonBanner == null) return;
//
//        // 1. 停止Banner轮播和视频
//        commonBanner.stopTurning();
//        closeAllBannerHolders();
//
//        // 2. 显示主布局，隐藏Banner
//        commonBanner.setVisibility(View.GONE);
//        mainlinearLayout.setVisibility(View.VISIBLE);
//        viewStart.setVisibility(View.VISIBLE); // 显示闲置触发View
//
//        // 3. 恢复闲置计时器
//        RxTimer.getInstance().start(tvTimer, Time, "ManagerLogin", this);
//
//        // 4. 日志记录
//        Logger.d("隐藏广告，恢复货道布局");
//    }
//
//    /**
//     * 关闭所有Banner的视频播放（防止内存泄漏）
//     */
//    public void closeAllBannerHolders() {
//        for (LocalVImageHolderView holder : bannerHolders) {
//            holder.stopVideo();
//        }
//        bannerHolders.clear();
//    }
//
//
//    // ======================== 服务器指令处理逻辑 ========================
//    /**
//     * 服务器指令轮询Runnable
//     */
//    Runnable runnableGetStatus = new Runnable() {
//        @Override
//        public void run() {
//            handlerGetStatus.removeCallbacksAndMessages(null);
//
//            // 1. 请求服务器指令
//            String url4 = "/cashier/getCommand?deviceNum=" + storeId;
//            try {
//                TCPAsyncNetUtils.get(url4, response -> {
//                    Logger.d("服务器响应: " + response);
//                    try {
//                        JSONObject data2 = new JSONObject(response);
//                        String cmd = data2.optString("data");
//                        String msg = data2.optString("message");
//
//                        // 2. 处理广告指令（msg对应VideoType）
//                        if (!"null".equals(msg) && !TextUtils.isEmpty(msg)) {
//                            handleAdCommand(msg);
//                        }
//
//                        // 3. 处理串口指令（货道控制等）
//                        if (!"null".equals(cmd) && !TextUtils.isEmpty(cmd) && serialHelper != null) {
//                            handleSerialCommand(cmd);
//                        }
//                    } catch (Exception e) {
//                        Logger.e("解析服务器响应失败: " + e.getMessage());
//                    }
//                });
//            } catch (Exception e) {
//                Logger.e("请求服务器指令失败: " + e.getMessage());
//            } finally {
//                // 4. 3秒后再次轮询
//                handlerGetStatus.postDelayed(this, 3000);
//            }
//        }
//    };
//
//    public void StartDoWaterAd(View view) {
//        handleAdCommand(VideoType.START_DO_WATER.name());
//    }
//
//    public void FinishDoWaterAd(View view) {
//        handleAdCommand(VideoType.FINISH_DO_WATER.name());
//    }
//
//    public void CancelDoWaterAd(View view) {
//        handleAdCommand(VideoType.CANCEL_DO_WATER.name());
//    }
//
//    /**
//     * 处理广告指令（根据msg切换广告）
//     */
//    private void handleAdCommand(String msg) {
//        runOnUiThread(() -> {
//            try {
//                VideoType videoType = VideoType.valueOf(msg);
//                AdType targetAdType = AdType.getByVideoType(videoType);
//                showAd(targetAdType);
//
//
////                if (targetAdType == AdType.FINISH || targetAdType == AdType.CANCEL) {
////                    adTimeoutHandler.postDelayed(() -> {
////                        runOnUiThread(() -> showAd(AdType.IDLE));
////                    }, 5000); // 假设视频5秒内播放完成，可根据实际调整
////                }
//            } catch (IllegalArgumentException e) {
//                Logger.e("无效的广告指令: " + msg);
//            }
//        });
//    }
//
//    /**
//     * 处理串口指令（发送Hex指令）
//     */
//    private void handleSerialCommand(String cmd) {
//        runOnUiThread(() -> {
//            try {
//                if (!serialHelper.isOpen()) {
//                    serialHelper.open();
//                }
//                serialHelper.sendHex(cmd);
//                text_gallery.setText("发送串口指令: " + cmd);
//            } catch (IOException e) {
//                Logger.e("发送串口指令失败: " + e.getMessage());
//                text_gallery.setText("发送失败: " + e.getMessage());
//            }
//        });
//    }
//
//
//    // ======================== 串口数据接收处理 ========================
//    public void CoinReadData(byte[] bytes) {
//        String result = toHexString(bytes);
//        text_result.setText("串口响应: " + result);
//
//        // 处理温湿度数据（原逻辑保留）
//        if (result.startsWith("0007")) {
//            try {
//                String hex1 = result.substring(4, 6);
//                int rh = Integer.parseInt(hex1, 16);
//                String hex2 = result.substring(6, 8);
//                int temp = Integer.parseInt(hex2, 16);
//
//                text_temp.setText("当前温度：" + rh + "℃ 湿度：" + temp + "%");
//                // 上报温湿度数据到服务器
//                String url4 = "/cashier/setRhTempData?deviceNum=" + storeId + "&data=" + result;
//                TCPAsyncNetUtils.get(url4, response -> Logger.d("温湿度上报响应: " + response));
//            } catch (Exception e) {
//                Logger.e("解析温湿度数据失败: " + e.getMessage());
//            }
//        }
//    }
//
//    // 16进制字节数组转字符串
//    public static String toHexString(byte[] inBytArr) {
//        StringBuilder strBuilder = new StringBuilder();
//        int j = inBytArr.length;
//        for (int i = 0; i < j; i++) {
//            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
//        }
//        return strBuilder.toString();
//    }
//
//
//    // ======================== 货道测试逻辑（原逻辑保留） ========================
//    Runnable runnableGetStatus2 = new Runnable() {
//        @Override
//        public void run() {
//            if (startNum > 60) {
//                handlerGetStatus2.removeCallbacksAndMessages(null);
//                return;
//            }
//
//            String s = intTohex(startNum);
//            String cmd = buildSerialCommand(s); // 构建货道指令
//            text_gallery.setText("测试发送: " + cmd);
//            Logger.d("货道测试指令: " + cmd);
//
//            // 发送测试指令
//            if (serialHelper != null) {
//                try {
//                    if (!serialHelper.isOpen()) {
//                        serialHelper.open();
//                    }
//                    serialHelper.sendHex(cmd);
//                } catch (IOException e) {
//                    Logger.e("货道测试指令发送失败: " + e.getMessage());
//                }
//            }
//
//            startNum++;
//            handlerGetStatus2.removeCallbacksAndMessages(null);
//            handlerGetStatus2.postDelayed(this, 3000);
//        }
//    };
//
//    // 构建货道控制指令（含CRC校验）
//    private String buildSerialCommand(String channelHex) {
//        String cmdPrefix = GuangMu ? "0105" + channelHex + "0302" : "0105" + channelHex + "0300";
//        String cmdWithoutCrc = cmdPrefix + "000000000000000000000000";
//        String crc = GetCRC_MODBUS(cmdWithoutCrc);
//        // CRC高低位交换（原逻辑保留）
//        String crcLow = crc.substring(2, 4);
//        String crcHigh = crc.substring(0, 2);
//        return cmdWithoutCrc + crcLow + crcHigh;
//    }
//
//    // 10进制转2位16进制
//    public static String intTohex(int n) {
//        StringBuffer s = new StringBuffer();
//        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//        while (n != 0) {
//            s.append(b[n % 16]);
//            n = n / 16;
//        }
//        String a = s.reverse().toString();
//        return a.isEmpty() ? "00" : (a.length() == 1 ? "0" + a : a);
//    }
//
//    // MODBUS CRC校验（原逻辑保留）
//    public static String GetCRC_MODBUS(String str) {
//        byte[] bytes = toBytes(str);
//        int crc = 0xFFFF;
//        int polynomial = 0xA001;
//        for (byte b : bytes) {
//            crc ^= (b & 0xFF);
//            for (int i = 0; i < 8; i++) {
//                crc = (crc & 0x0001) != 0 ? (crc >>> 1) ^ polynomial : crc >>> 1;
//            }
//        }
//        return String.format("%02X%02X", (crc >> 8) & 0xFF, crc & 0xFF);
//    }
//
//    // 16进制字符串转字节数组
//    public static byte[] toBytes(String hexStr) {
//        if (hexStr.length() % 2 != 0) {
//            throw new IllegalArgumentException("Hex字符串长度必须为偶数");
//        }
//        byte[] bytes = new byte[hexStr.length() / 2];
//        for (int i = 0; i < bytes.length; i++) {
//            int index = i * 2;
//            bytes[i] = (byte) Integer.parseInt(hexStr.substring(index, index + 2), 16);
//        }
//        return bytes;
//    }
//
//
//    // ======================== 点击事件处理 ========================
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn_camera_change) {
//            showSerialInputDialog(); // 修改串口路径弹窗
//        } else if (id == R.id.btn_camera_change2) {
//            startChannelTest(); // 开始货道测试
//        }
//    }
//
//    // 闲置倒计时结束（显示闲置广告）
//    @Override
//    public void onTimeEnd() {
//        runOnUiThread(() -> showAd(AdType.IDLE));
//    }
//
//    // 全屏广告按钮（强制显示闲置广告）
//    public void fullScreenAdvertisement(View view) {
//        RxTimer.getInstance().stop();
//        showAd(AdType.IDLE);
//    }
//
//    // 重新开始倒计时
//    public void reStartTime(View view) {
//        RxTimer.getInstance().resetCurrentTimer(true);
//        hideAd(); // 隐藏广告，恢复货道
//    }
//
//    // 连续点击进入设备设置
//    @OnClick({R2.id.mainlinearLayout, R2.id.viewStart})
//    public void onViewClick(View view) {
//        int id = view.getId();
//        if (id == R.id.mainlinearLayout) {
//            RxTimer.getInstance().resetCurrentTimer(true);
//        } else if (id == R.id.viewStart) {
////            Logger.d("点击了闲置触发View");
//            handleDeviceSettingClick();
//        }
//    }
//
//    // 处理设备设置连续点击
//    private void handleDeviceSettingClick() {
//        long currentTime = SystemClock.uptimeMillis();
//        if (currentTime - DEVICE_lastClickTime <= DEVICE_CLICK_INTERVER_TIME || DEVICE_lastClickTime == 0) {
//            DEVICE_lastClickTime = currentTime;
//            DEVICE_clickNum++;
//        } else {
//            DEVICE_clickNum = 1;
//            DEVICE_lastClickTime = currentTime;
//            return;
//        }
//
//        if (DEVICE_clickNum == DEVICE_CLICK_NUM) {
//            DEVICE_clickNum = 0;
//            DEVICE_lastClickTime = 0;
//            hideAd(); // 进入设置前隐藏广告
//            Toast.makeText(this, "进入设备设置模式", Toast.LENGTH_SHORT).show();
//            // 可添加设备设置页面跳转逻辑
//        }
//    }
//
//    // 选择广告资源（idle/start/finish/cancel）
//    public void selectAD(View view) {
//        selectADType = 0;
//        openMediaSelector("*/*", true);
//    }
//
//    public void selectDoWaterAd(View view) {
//        selectADType = 1;
//        openMediaSelector("*/*", false);
//    }
//
//    public void selectFinishDoWaterAd(View view) {
//        selectADType = 2;
//        openMediaSelector("*/*", false);
//    }
//
//    public void selectCancelDoWaterAd(View view) {
//        selectADType = 3;
//        openMediaSelector("*/*", false);
//    }
//
//    // 打开系统文件选择器
//    private void openMediaSelector(String mimeType, boolean multiSelect) {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType(mimeType);
//        if (multiSelect) {
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        }
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, REQUEST_CODE_SELECT_MEDIA);
//    }
//
//    // 读取温度按钮
//    public void readTemp(View view) {
//        String cmd = "0107000000000000000000000000000000009229";
//        text_gallery.setText("发送读温指令: " + cmd);
//        if (serialHelper != null) {
//            try {
//                if (!serialHelper.isOpen()) {
//                    serialHelper.open();
//                }
//                serialHelper.sendHex(cmd);
//            } catch (IOException e) {
//                Logger.e("读温指令发送失败: " + e.getMessage());
//                text_gallery.setText("读温失败: " + e.getMessage());
//            }
//        }
//    }
//
//    // 光幕模式切换
//    private Boolean GuangMu = false;
//
//    public void wuGuangMu(View view) {
//        GuangMu = false;
//        text_status.setText("当前模式：无光幕");
//    }
//
//    public void youGuangMu(View view) {
//        GuangMu = true;
//        text_status.setText("当前模式：有光幕");
//    }
//
//
//    // ======================== 弹窗与测试方法 ========================
//    // 修改串口路径弹窗
//    private void showSerialInputDialog() {
//        EditText editText = new EditText(this);
//        editText.setText(this.path); // 显示当前路径
//        new AlertDialog.Builder(this)
//                .setTitle("修改串口路径（当前：" + this.path + "）")
//                .setView(editText)
//                .setPositiveButton("确定", (dialog, which) -> {
//                    String newPath = editText.getText().toString().trim();
//                    if (!TextUtils.isEmpty(newPath) && !newPath.equals(this.path)) {
//                        this.path = newPath;
//                        // 保存到SP
//                        SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
//                        sp.edit().putString("path", newPath).apply();
//                        // 重新初始化串口
//                        initSerial();
//                        text_status.setText("串口路径已更新：" + newPath);
//                    }
//                    dialog.dismiss();
//                })
//                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
//                .setCancelable(false)
//                .show();
//    }
//
//    // 开始货道测试（从0号货道开始）
//    private void startChannelTest() {
//        startNum = 0;
//        handlerGetStatus2.removeCallbacksAndMessages(null);
//        handlerGetStatus2.postDelayed(runnableGetStatus2, 3000);
//        text_status.setText("开始货道测试（3秒后启动）");
//    }
//
//
//    // ======================== 广告资源选择回调 ========================
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_SELECT_MEDIA && resultCode == RESULT_OK && data != null) {
//            List<String> selectedPaths = getSelectedMediaPaths(data);
//            if (!selectedPaths.isEmpty()) {
//                saveAdResources(selectedPaths);
//                // 刷新Banner（显示最新选择的广告）
//                AdType targetType = selectADType == 0 ? AdType.IDLE :
//                        (selectADType == 1 ? AdType.START :
//                                (selectADType == 2 ? AdType.FINISH : AdType.CANCEL));
//                setBannerPages(targetType);
//                showSelectedResult(selectedPaths);
//            }
//        }
//    }
//
//    // 获取选择的媒体文件路径
//    private List<String> getSelectedMediaPaths(Intent data) {
//        List<String> paths = new ArrayList<>();
//        if (data.getClipData() != null) {
//            ClipData clipData = data.getClipData();
//            for (int i = 0; i < clipData.getItemCount(); i++) {
//                Uri uri = clipData.getItemAt(i).getUri();
//                String path = UriToPathUtil.getRealPathFromUri(this, uri);
//                if (!TextUtils.isEmpty(path)) {
//                    paths.add(path);
//                }
//            }
//        } else if (data.getData() != null) {
//            Uri uri = data.getData();
//            String path = UriToPathUtil.getRealPathFromUri(this, uri);
//            if (!TextUtils.isEmpty(path)) {
//                paths.add(path);
//            }
//        }
//        return paths;
//    }
//
//    // 保存广告资源到LocalConfigManager
//    private void saveAdResources(List<String> paths) {
//        JsonArray jsonArray = new JsonArray();
//        for (int i = 0; i < paths.size(); i++) {
//            JsonObject json = new JsonObject();
//            json.addProperty("id", i);
//            json.addProperty("module", "app-screen");
//            json.addProperty("localCache", paths.get(i));
//            jsonArray.add(json);
//        }
//        // 根据选择的广告类型保存
//        if (selectADType == 0) {
//            LocalConfigManager.getInstance().parseAdJsonToDisplay(jsonArray, true);
//        } else if (selectADType == 1) {
//            LocalConfigManager.getInstance().parseStartAdJsonToDisplay(jsonArray, true);
//        } else if (selectADType == 2) {
//            LocalConfigManager.getInstance().parseFinishAdJsonToDisplay(jsonArray, true);
//        } else if (selectADType == 3) {
//            LocalConfigManager.getInstance().parseCancelAdJsonToDisplay(jsonArray, true);
//        }
//    }
//
//    public void toSetting(View view) {
//        startActivity(new Intent(Settings.ACTION_SETTINGS));
//    }
//
//    // 显示选择结果提示
//    private void showSelectedResult(List<String> paths) {
//        Toast.makeText(this, "已选择 " + paths.size() + " 个广告资源", Toast.LENGTH_SHORT).show();
//        for (int i = 0; i < paths.size(); i++) {
//            Logger.i("选择的资源路径 " + (i + 1) + ": " + paths.get(i));
//        }
//    }
//
//
//    // ======================== 设备ID获取工具 ========================
//    public static String getDeviceIdByGetImei(Context context, int slotId) {
//        try {
//            TelephonyManager tm = (TelephonyManager) context.getApplicationContext()
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                return tm.getImei(slotId);
//            }
//            Method method = tm.getClass().getMethod("getImei", int.class);
//            return method.invoke(tm, slotId).toString();
//        } catch (Throwable e) {
//            Logger.e("获取IMEI失败: " + e.getMessage());
//            return "";
//        }
//    }
//
//
//    // ======================== 货道按钮监听器（原逻辑保留） ========================
//    class btnListener implements OnClickListener {
//        private Button btn;
//
//        private btnListener(Button btn) {
//            this.btn = btn;
//        }
//
//        @Override
//        public void onClick(View v) {
//            int channelId = v.getId();
//            Logger.d("点击货道: " + channelId);
//            try {
//                String cmd = buildSerialCommand(intTohex(channelId));
//                text_gallery.setText("货道" + channelId + "指令: " + cmd);
//                if (serialHelper != null) {
//                    if (!serialHelper.isOpen()) {
//                        serialHelper.open();
//                    }
//                    serialHelper.sendHex(cmd);
//                }
//            } catch (Exception e) {
//                Logger.e("货道" + channelId + "控制失败: " + e.getMessage());
//            }
//        }
//    }
//}