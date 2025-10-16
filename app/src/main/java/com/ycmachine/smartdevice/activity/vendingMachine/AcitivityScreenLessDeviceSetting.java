package com.ycmachine.smartdevice.activity.vendingMachine;


import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_CLICK_INTERVER_TIME;
import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_CLICK_NUM;
import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_clickNum;
import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_lastClickTime;
import static com.ycmachine.smartdevice.constent.ClientConstant.REQUEST_CODE_SELECT_MEDIA;
import static tp.xmaihh.serialport.utils.ByteUtil.Byte2Hex;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.enumclass.VideoType;
import com.ycmachine.smartdevice.network.net.TCPAsyncNetUtils;
import com.ycmachine.smartdevice.network.socket.MyWebSocketClient;
import com.ycmachine.smartdevice.utils.DeviceIdUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import android_serialport_api.SerialPortFinder;
import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.handler.LocalConfigManager;
import leesche.smartrecycling.base.utils.QrCodeUtil;
import leesche.smartrecycling.base.utils.RxTimer;
import leesche.smartrecycling.base.utils.UriToPathUtil;
import leesche.smartrecycling.base.view.LocalVImageHolderView;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

public class AcitivityScreenLessDeviceSetting extends BaseActivity implements OnClickListener, RxTimer.OnTimeCounterListener {
    private FlexboxLayout linearLayout;

    String path = "/dev/ttyS4";
    int baudrate = 9600;

    String storeId;
    int Time = 100;
    String firstBox = "1";
    String unDoBox = "1";

    // 1. 队列：存储 FINISH_DO_WATER 的所有数字（用String类型兼容可能的非纯数字场景）// 存储FINISH_DO_WATER的数字队列
    private List<String> finishDoWaterList = new ArrayList<>();
    // 存储START_DO_WATER的数字队列（与FINISH队列对应）
    private List<String> startDoWaterList = new ArrayList<>();
    // 用于状态切换的Handler（确保主线程操作）

    private Handler statusHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("StaticFieldLeak")
    public static MyWebSocketClient webSocketClient;
    protected Button mCameraChange, mCameraChange2;

    protected TextView text_gallery;
    protected TextView text_result;
    protected TextView text_status;
    protected TextView text_temp;

    @BindView(R2.id.cIdleBanner)
    ConvenientBanner cIdleBanner;

    @BindView(R2.id.viewStart)
    View viewStart;

    @BindView(R2.id.bottomContainer)
    LinearLayout bottomContainer;

    @BindView(R2.id.ivQrApplet)
    ImageView ivQrApplet; // 小程序二维码

    @BindView(R2.id.ivQrApp)
    ImageView ivQrApp; // App二维码

    @BindView(R2.id.tvTimer)
    TextView tvTimer;

    @BindView(R2.id.lqTextView)
    TextView lqTextView;

    @BindView(R2.id.imageViewWaterStatus)
    ImageView imageViewWaterStatus;

    @BindView(R2.id.doingTextView)
    TextView doingTextView;

    @BindView(R2.id.ll_doing_undo)
    LinearLayout llDoingUndo;

    @BindView(R2.id.undoTextView)
    TextView undoTextView;


    @BindView(R2.id.text_deviceid)
    TextView textDeviceid;

    @BindView(R2.id.mainlinearLayout)
    LinearLayout mainlinearLayout;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable idleSwitchRunnable; // 用于取消延迟任务

    private List<LocalVImageHolderView> bannerHolders = new ArrayList<>();

    @Override
    protected void onDestroy() {
        cIdleBanner.stopTurning();
        // 清空 Handler 队列，防止后台继续发送请求
        if (handlerGetStatus != null) {
            handlerGetStatus.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    // 生命周期
    @Override
    protected void onResume() {
        super.onResume();
        fullScreenAdvertisement(null);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_screenless_device_setting);
        //利用LinearLayout控件的id获得此控件的对象
        linearLayout = (FlexboxLayout) findViewById(R.id.linearLayout);


        mCameraChange = findViewById(R.id.btn_camera_change);
        mCameraChange.setOnClickListener(this);

        mCameraChange2 = findViewById(R.id.btn_camera_change2);
        text_gallery = findViewById(R.id.text_gallery);
        text_result = findViewById(R.id.text_result);
        text_status = findViewById(R.id.text_status);
        text_temp = findViewById(R.id.text_temp);

        mCameraChange2.setOnClickListener(this);

        /*
         * 利用一个for循环, 向LinearLayout中添加100个按钮
         */
        for (int i = 0; i <= 60; i++) {
            //创建一个新的Button对象
            Button btnLesson = new Button(this);
            //设置对象的id
            btnLesson.setId(i);
            //设置对象显示的值
            btnLesson.setText("货道" + i);
            btnLesson.setWidth(115);
            btnLesson.setHeight(115);
            //给按钮添加监听事件
            btnLesson.setOnClickListener(new btnListener(btnLesson));
            //将Button对象添加到LinearLayout中
            linearLayout.addView(btnLesson);
        }

        initSerial();//串口初始化
//        CoinSerialManage.getInstance().init(this);//串口初始化


        openPath();
        startTotalTimer();

        String deviceIdByGetImei = "";
//        try {
//            deviceIdByGetImei = getDeviceIdByGetImei(this, 0);
//            storeId = deviceIdByGetImei.substring(deviceIdByGetImei.length() - 6);
//
//        } catch (Exception e) {
        deviceIdByGetImei = DeviceIdUtil.getDeviceId(this);
        storeId = deviceIdByGetImei;

//        }
        Logger.d("onCreate: " + storeId);
        textDeviceid.setText("设备编号" + storeId);

        try {
            // 关键：初始化前先清空队列，防止Activity重建导致的重复调度
            handlerGetStatus.removeCallbacksAndMessages(null);
            // 仅调度一次
            handlerGetStatus.postDelayed(runnableGetStatus, 3000);
        } catch (Exception e) {
            Logger.v("----2-----" + e);
        }

        Context ctx = AcitivityScreenLessDeviceSetting.this;
        SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
        String path = sp.getString("path", "none");
        if (!path.equals("none"))
            this.path = path;

//        this.getPath();
        createQRCode();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_screenless_device_setting;
    }


    @Override
    public void initData() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        tvTimer = findViewById(R.id.tvTimer);
        RxTimer.getInstance().start(tvTimer, Time, "ManagerLogin", this);

        LocalConfigManager.getInstance().loadAdDataFromLocal();

        setPages();
    }

    private void createQRCode() {
        String qrCodeUrl = "https://www.dianfengkj.com/dabuon?deviceNum=" + storeId;
        Bitmap qrCode = QrCodeUtil.createQRCode(qrCodeUrl);
        ivQrApplet.setImageBitmap(qrCode);
        ivQrApp.setImageBitmap(qrCode);
    }

    private void setPages() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        cIdleBanner.setLayoutManager(layoutManager);
        cIdleBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                LocalVImageHolderView localImageHolderView = new LocalVImageHolderView(itemView, 0);
                localImageHolderView.setCallback((curId, time) -> {
                    handleIdleAdTurn(curId, time);
                });
                bannerHolders.add(localImageHolderView);
                return localImageHolderView;
            }

            @Override
            public int getLayoutId() {
                return R.layout.banner_item;
            }
        }, LocalConfigManager.getInstance().getIdleBannerList());
    }


    private void handleIdleAdTurn(int curId, long time) {
        if (cIdleBanner != null) {

            cIdleBanner.postDelayed(() -> {
                if (cIdleBanner != null) {
                    if (curId < LocalConfigManager.getInstance().getIdleBannerList().size() - 1) {
                        cIdleBanner.setCurrentItem(curId + 1, false);
                    } else {
                        cIdleBanner.notifyDataSetChanged();
                    }
                }
            }, time);
        }

    }


    public void getPath() {

        String url4 = "/cashier/getCashierPath?deviceNum=" + storeId;

        // 从本地查询该条码是否存在
        TCPAsyncNetUtils.get(url4, response -> {

            Logger.d("run: " + response);
            JSONObject data2 = new JSONObject(response);
            String path = data2.optString("data");

            if (!path.equals("null")) {


                Context ctx = AcitivityScreenLessDeviceSetting.this;
                SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("path", path);
                editor.apply();

                this.path = path;
                System.out.println(this.path);

                initSerial();//串口初始化

            }


        });
    }

    private SerialPortFinder serialPortFinder;
    private SerialHelper serialHelper;

    private void initSerial() {
        serialPortFinder = new SerialPortFinder();
        serialHelper = new SerialHelper(path, baudrate) {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d("run: " + comBean.sRecTime + " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec));
                        CoinReadData(comBean.bRec);
//                            Logger.d(  comBean.sRecTime + " Rx:<==" + new String(comBean.bRec, StandardCharsets.UTF_8));
                    }
                });
            }
        };


        openPath();


    }

    Handler handlerGetStatus = new Handler();
    Runnable runnableGetStatus = new Runnable() {
        @Override
        public void run() {

            handlerGetStatus.removeCallbacksAndMessages(null); // 清空队列中所有消息

            // 弹簧机查看是否有命令下发
            String url4 = "/cashier/getCommand?deviceNum=" + storeId;
            AtomicBoolean flag = new AtomicBoolean(false);
            try {
                // 从本地查询该条码是否存在
                TCPAsyncNetUtils.get(url4, response -> {
                    if (!response.contains("code"))
                        return;

                    Logger.d("run: " + response);
                    JSONObject data2 = new JSONObject(response);
                    String cmd = data2.optString("data");
                    String msg = data2.optString("message");

                    if (!msg.equals("null")) {

                        if (msg.contains(":")) {
                            handleBoxString(msg);
//                            String[] split = msg.split(":");
//                            if (split.length == 2) {
//                                String type = split[0];
//                                setVideoType(VideoType.valueOf(type));
//                            }
                        } else {

//                            fullScreenAdvertisement(null);
                            setVideoType(VideoType.valueOf(msg));
                        }
                    } else {

                        finishDoWaterList = new ArrayList<>();
                        startDoWaterList = new ArrayList<>();

                        if (!finishAndReturnStart)
                            setVideoType(VideoType.IDLE);
                    }


                    if (!cmd.equals("null")) {
                        flag.set(true);

                        try {
//                            CoinSerialManage.getInstance().open(path);//打开串口
//                            CoinSerialManage.getInstance().send(cmd);// 开锁


                            openPath();
                            addCommand(cmd);
                        } catch (Exception e) {

                        }
                        text_gallery.setText("发送：" + cmd);
//                        handlerGetStatus.removeCallbacks(runnableGetStatus);
//                        handlerGetStatus.postDelayed(runnableGetStatus, 5000);

                    }
                    returnIdle();

//                    else {
//                        handlerGetStatus.removeCallbacks(runnableGetStatus);
//                        handlerGetStatus.postDelayed(runnableGetStatus, 3000);

//                    }


                });
            } catch (Exception e) {
            } finally {
                handlerGetStatus.postDelayed(runnableGetStatus, 3000);

            }
        }
    };

    private void loadGifAndKeepLastFrame() {
        // 替换为你的 GIF 资源名（放在 res/drawable 文件夹下）
        int gifResId = R.drawable.water_dispenser_gif;

        Glide.with(this)
                .asGif() // 强制按 GIF 格式加载（必须加，否则可能显示静态图）
                .load(gifResId) // 加载本地 GIF（网络 GIF 替换为 URL 字符串）
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {

                        // GIF 加载失败时，显示默认图
                        imageViewWaterStatus.setImageResource(R.drawable.water_dispenser_full);
                        imageViewWaterStatus.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            GifDrawable gifDrawable, // 加载完成的 GIF 实例
                            Object model,
                            Target<GifDrawable> target,
                            DataSource dataSource,
                            boolean isFirstResource
                    ) {
                        // 关键：在 GIF 资源就绪后，设置仅播放 1 次（替代原 .loopCount(1)）
                        gifDrawable.setLoopCount(1);

                        // 监听 GIF 播放结束，停止动画并保持最后一帧
                        gifDrawable.registerAnimationCallback(new GifDrawable.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                super.onAnimationEnd(drawable);
                                gifDrawable.stop(); // 播放完 1 次后，停止动画
                            }
                        });

                        // 显示 ImageView（初始为 gone）
                        imageViewWaterStatus.setVisibility(View.VISIBLE);
                        return false; // 返回 false，让 Glide 正常完成后续逻辑
                    }

                })
                .into(imageViewWaterStatus); // 加载到目标 ImageView
    }

    public VideoType nowVideoType = VideoType.IDLE;

    public void setVideoType(VideoType type) {
        // 取消之前的延迟任务，避免冲突
        if (idleSwitchRunnable != null) {
            handler.removeCallbacks(idleSwitchRunnable);
        }
        if (lqTextView == null) return;
        nowVideoType = type;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (type) {
                    case CANCEL_DO_WATER:
                    case IDLE:
                        imageViewWaterStatus.setVisibility(View.GONE);
                        llDoingUndo.setVisibility(View.GONE);
                        bottomContainer.setBackground(getResources().getDrawable(R.drawable.water_dispenser_idle));
                        lqTextView.setText("扫描二维码，享受免费领水优惠");
                        break;

                    case START_DO_WATER:
                        imageViewWaterStatus.setVisibility(View.VISIBLE);
                        llDoingUndo.setVisibility(View.VISIBLE);
                        doingTextView.setVisibility(View.VISIBLE);
                        doingTextView.setText(firstBox + "号制水中"); // 使用更新后的firstBox
                        if (StringUtils.isEmpty(unDoBox)) {
                            undoTextView.setVisibility(View.GONE);
                        } else {
                            undoTextView.setVisibility(View.VISIBLE);
                            undoTextView.setText(unDoBox + "号请等待"); // 使用更新后的unDoBox
                        }
                        loadGifAndKeepLastFrame();
                        lqTextView.setText("领水中，请等待");
                        bottomContainer.setBackground(getResources().getDrawable(R.drawable.water_dispenser_doing));
                        // 120秒后自动切换到IDLE
                        idleSwitchRunnable = () -> setVideoType(VideoType.IDLE);
                        handler.postDelayed(idleSwitchRunnable, 30 * 1000);
                        break;

                    case FINISH_DO_WATER:
//                        llDoingUndo.setVisibility(View.VISIBLE);
//                        doingTextView.setVisibility(View.VISIBLE);
//                        doingTextView.setText(firstBox + "号制水中"); // 使用更新后的firstBox
//                        if (StringUtils.isEmpty(unDoBox)) {
//                            undoTextView.setVisibility(View.GONE);
//                        } else {
//                            undoTextView.setVisibility(View.VISIBLE);
//                            undoTextView.setText(unDoBox + "号请等待"); // 使用更新后的unDoBox
//                        }

//                        if (!finishDoWaterList.isEmpty())
//                            firstElement = finishDoWaterList.get(0);
                        lqTextView.setText(firstBox + "号领水成功，感谢您使用漫甜领水");
                        llDoingUndo.setVisibility(View.GONE);
                        doingTextView.setVisibility(View.GONE);
                        imageViewWaterStatus.setVisibility(View.GONE);
                        bottomContainer.setBackground(getResources().getDrawable(R.drawable.water_dispenser_finish));
//                        // 延迟切换到IDLE（依赖队列长度）
//                        idleSwitchRunnable = () -> setVideoType(VideoType.IDLE);
//                        if (!commandQueue.isEmpty()) {
//                            handler.postDelayed(idleSwitchRunnable, (long) commandQueue.size() * 12 * 1000);
//                        } else {
//                            handler.postDelayed(idleSwitchRunnable, 5 * 1000);
//                        }
                        // 2. 5秒后切换到START_DO_WATER或IDLE（同样在主线程执行）
                        statusHandler.postDelayed(() -> {
                            Logger.d("延迟5秒: ");

                            try {
                                boolean isAllEmpty = finishDoWaterList.isEmpty() && startDoWaterList.isEmpty();
                                if (isAllEmpty) {
                                    setVideoType(VideoType.IDLE);
                                } else {
//                                    updateFirstAndUnDoBoxForStart();
                                    setVideoType(VideoType.START_DO_WATER);
                                }
                                finishAndReturnStart = false;
                            } catch (Exception e) {
                                Logger.i("error" + e.getMessage());
                            }
                        }, 5000);
                        break;
                }

            }
        });
    }

    Handler handlerGetStatus2 = new Handler();
    Runnable runnableGetStatus2 = new Runnable() {
        @Override
        public void run() {

            if (startNum > 60) {
                handlerGetStatus2.removeCallbacks(runnableGetStatus2);
                return;
            }


            // 1为 int 类型、0代表前面要补位的字符、2代表字符串的长度、d表示参数为整数类型
//            @SuppressLint("DefaultLocale") String s = String.format("%02d", startNum);

            String s = intTohex(startNum);


            String cmd;
            if (!GuangMu) {
                cmd = "0105" + s + "030000000000000000000000000000";

            } else {
                cmd = "0105" + s + "030200000000000000000000000000";
            }


            cmd = GetCRC_MODBUS(cmd);
//            cmd = String.valueOf(calculateCRC16(cmd));

            String start = cmd.substring(0, 2);
            String end = cmd.substring(cmd.length() - 2);

            if (!GuangMu) {
                cmd = "0105" + s + "030000000000000000000000000000" + end + start;

            } else {
                cmd = "0105" + s + "030200000000000000000000000000" + end + start;
            }

            text_gallery.setText("发送：" + cmd);
            Logger.d("onClick: " + cmd);
//            CoinSerialManage.getInstance().open(path);//打开串口
//            CoinSerialManage.getInstance().send(cmd);// 开锁


            openPath();

            addCommand(cmd);


            startNum++;
            handlerGetStatus2.removeCallbacks(runnableGetStatus2);
            handlerGetStatus2.postDelayed(runnableGetStatus2, 3000);

        }
    };

    public void stopTimer(View view) {

        RxTimer.getInstance().stop();
    }

    public void startTimer(View view) {

        RxTimer.getInstance().start(tvTimer, Time, "ManagerLogin", this);
    }

    public static String intTohex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        if ("".equals(a)) {
            a = "00";
        }
        if (a.length() == 1) {
            a = "0" + a;
        }
        return a;
    }

    public void CoinReadData(byte[] bytes) {
//        Logger.i("硬币串口回调", Arrays.toString(bytes));
        String result = toHexString(bytes);
        text_result.setText("响应：" + result);
        if (result.startsWith("0007")) {

            // 提取 "3A"（从索引4开始，取2位）
            String hex1 = result.substring(4, 6);
            int rh = Integer.parseInt(hex1, 16); // 16表示解析16进制

            // 提取 "17"（从索引6开始，取2位）
            String hex2 = result.substring(6, 8);
            int temp = Integer.parseInt(hex2, 16);

            text_temp.setText("当前温度：" + rh + "湿度：" + temp);

//            String url4 = "/cashier/setRhTemp?deviceNum=" + storeId+"&rh="+rh+"&temp="+temp;
//            // 从本地查询该条码是否存在
//            TCPAsyncNetUtils.get(url4, response -> {
//                Logger.d("run: " + response);
//            });

            String url4 = "/cashier/setRhTempData?deviceNum=" + storeId + "&data=" + result;
            // 从本地查询该条码是否存在
            TCPAsyncNetUtils.get(url4, response -> {
                Logger.d("run: " + response);
            });

        }

//        if (result.startsWith("0005")) {
//            // 出货成功的回调
//
//            Logger.i("收到0005应答，准备发送下一条命令");
//            // 2. 取消当前超时定时器（应答已收到，无需超时处理）
//            cancelSendTimer();
//            // 3. 发送队列下一条命令
//            sendNextCommand();
//        }
    }

    public static String toHexString(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
            strBuilder.append("");
        }
        return strBuilder.toString();
    }

    Integer startNum = 0;

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_camera_change)
            showInputDialog();
        if (v.getId() == R.id.btn_camera_change2) {
            startNum = 0;
            handlerGetStatus2.postDelayed(runnableGetStatus2, 3000);

        }

    }

    @Override
    public void onTimeEnd() {

        displayIdleBanner(true);
    }

    public void fullScreenAdvertisement(View view) {
        RxTimer.getInstance().stop();
        displayIdleBanner(true);
    }

    public void reStartTime(View view) {
        RxTimer.getInstance().resetCurrentTimer(true);
    }

    public void checkStatus(View view) {

        String cmd = "010300000000000000000000000000000000";

        cmd = GetCRC_MODBUS(cmd);
//            cmd = String.valueOf(calculateCRC16(cmd));

        String start = cmd.substring(0, 2);
        String end = cmd.substring(cmd.length() - 2);

        cmd = "010300000000000000000000000000000000" + end + start;

        text_gallery.setText("发送：" + cmd);
        serialHelper.sendHex(cmd);  // 发送Hex
    }

    @OnClick({R2.id.mainlinearLayout, R2.id.viewStart})
    public void onViewClick(View view) {
        if (view.getId() == R.id.mainlinearLayout) {
            RxTimer.getInstance().resetCurrentTimer(true);
        }
        if (view.getId() == R.id.viewStart) {

            //点击的间隔时间不能超过5秒
            long currentClickTime = SystemClock.uptimeMillis();
            if (currentClickTime - DEVICE_lastClickTime <= DEVICE_CLICK_INTERVER_TIME
                    || DEVICE_lastClickTime == 0) {
                DEVICE_lastClickTime = currentClickTime;
                DEVICE_clickNum = DEVICE_clickNum + 1;
            } else {
                //超过5秒的间隔
                //重新计数 从1开始
                DEVICE_clickNum = 1;
                DEVICE_lastClickTime = 0;
                return;
            }
            if (DEVICE_clickNum == DEVICE_CLICK_NUM) {
                //重新计数
                DEVICE_clickNum = 0;
                DEVICE_lastClickTime = 0;
                /*实现点击多次后的事件*/
                displayIdleBanner(false);
            }
        }
    }


    public void selectAD(View view) {
        openMediaSelector("*/*", true);
    }


    private void openMediaSelector(String mimeType, boolean multiSelect) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(mimeType);
        // 如果需要同时支持图片和视频，可以使用下面的代码
        // intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});

        // 设置为多选模式
        if (multiSelect) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        // 允许访问文档
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CODE_SELECT_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_MEDIA && resultCode == RESULT_OK && data != null) {
            List<String> selectedUris = new ArrayList<>();

            // 处理多选情况
            if (data.getClipData() != null) {
                // 假设你通过 ClipData 获取多选的文件 URI（之前的代码）
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int count = clipData.getItemCount();
                    Logger.i("FileSelect", "已选择 " + count + " 个文件：");

                    for (int i = 0; i < count; i++) {
                        Uri contentUri = clipData.getItemAt(i).getUri();
                        // 调用工具类转换为真实路径
                        String realPath = UriToPathUtil.getRealPathFromUri(this, contentUri);

                        if (!TextUtils.isEmpty(realPath)) {
                            Logger.i("FileSelect. 真实路径：" + realPath);
                            selectedUris.add(realPath);
                        } else {
                            Logger.e("FileSelect. 路径转换失败" + realPath + "，Content URI：" + contentUri);
                        }
                    }
                }
//                int count = data.getClipData().getItemCount();
//                for (int i = 0; i < count; i++) {
//                    Uri uri = data.getClipData().getItemAt(i).getUri();
//                    selectedUris.add(uri);
//                }
            }
            // 处理单选情况
            else if (data.getData() != null) {
                Uri contentUri = data.getData();
                // 调用工具类转换为真实路径
                String realPath = UriToPathUtil.getRealPathFromUri(this, contentUri);

                if (!TextUtils.isEmpty(realPath)) {
                    Logger.i("FileSelect", ". 真实路径：" + realPath);
                    selectedUris.add(realPath);
                } else {
                    Logger.e("FileSelect", ". 路径转换失败，Content URI：" + contentUri);
                }
            }


            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < selectedUris.size(); i++) {
                // 创建JSONObject并设置属性
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", i);
                jsonObject.addProperty("module", "app-screen");
                jsonObject.addProperty("localCache", selectedUris.get(i));
                // 将JSONObject添加到JSONArray
                jsonArray.add(jsonObject);
            }
            LocalConfigManager.getInstance().parseAdJsonToDisplay(jsonArray, true);


            Logger.i("111" + JSON.toJSONString(LocalConfigManager.getInstance().getIdleBannerList()));
            setPages();

            // 显示选择结果
            showSelectedResult(selectedUris);
        }
    }

    /**
     * 显示选中的媒体文件信息
     */
    private void showSelectedResult(List<String> uris) {
        if (uris.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("已选择 ").append(uris.size()).append(" 个文件：\n\n");

        for (int i = 0; i < uris.size(); i++) {
            sb.append(i + 1).append(". ").append(uris.get(i)).append("\n");
        }

        Logger.i(sb.toString());
        Toast.makeText(this, "已选择 " + uris.size() + " 个文件", Toast.LENGTH_SHORT).show();
    }

    public void closeAllBannerHolders() {
        for (LocalVImageHolderView holder : bannerHolders) {
            holder.stopVideo();
        }
        displayIdleBanner(false);
    }

    private void displayIdleBanner(boolean isIdle) {
        viewStart.setVisibility(isIdle ? View.VISIBLE : View.GONE);
        if (isIdle) {
            mainlinearLayout.setVisibility(View.GONE);
            cIdleBanner.setVisibility(View.VISIBLE);
            bottomContainer.setVisibility(View.VISIBLE);
            cIdleBanner.notifyDataSetChanged();
        } else {
            cIdleBanner.setVisibility(View.GONE);
            bottomContainer.setVisibility(View.GONE);
            cIdleBanner.notifyDataSetChanged();
            mainlinearLayout.setVisibility(View.VISIBLE);
            for (LocalVImageHolderView holder : bannerHolders) {
                holder.stopVideo();
            }
        }
    }

    public void toSetting(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    /*
     * 创建一个按钮监听器类, 作用是点击按钮后改变按钮的名字
     */
    class btnListener implements OnClickListener {
        //定义一个 Button 类型的变量
        private Button btn;

        /*
         * 一个构造方法, 将Button对象做为参数
         */
        private btnListener(Button btn) {
            this.btn = btn;//将引用变量传递给实体变量
        }

        public void onClick(View v) {

            Logger.d("onClick: " + v.getId());

            try {


                // 1为 int 类型、0代表前面要补位的字符、2代表字符串的长度、d表示参数为整数类型
//                @SuppressLint("DefaultLocale") String s = String.format("%02d", v.getId());

                String s = intTohex(v.getId());

                String cmd;
                if (!GuangMu) {
                    cmd = "0105" + s + "030000000000000000000000000000";

                } else {
                    cmd = "0105" + s + "030200000000000000000000000000";
                }

//                cmd = String.valueOf(calculateCRC16(cmd));
                cmd = GetCRC_MODBUS(cmd);

                String start = cmd.substring(0, 2);
                String end = cmd.substring(cmd.length() - 2);

                if (!GuangMu) {
                    cmd = "0105" + s + "030000000000000000000000000000" + end + start;

                } else {
                    cmd = "0105" + s + "030200000000000000000000000000" + end + start;
                }
                text_gallery.setText("发送：" + cmd);
                Logger.d("onClick: " + cmd);

//                CoinSerialManage.getInstance().open(path);//打开串口
//                CoinSerialManage.getInstance().send(cmd);// 开锁


                openPath();

                serialHelper.sendHex(cmd);  // 发送Hex

            } catch (Exception e) {
                Logger.v(String.valueOf(e));

            }


//            btn.setText("Welcome!");//改变按钮的名字
        }
    }


    /**
     * getImei获取 deviceId
     *
     * @param context
     * @param slotId  slotId为卡槽Id，它的值为 0、1；
     * @return
     */
    public static String getDeviceIdByGetImei(Context context, int slotId) {

        try {

            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                return tm.getImei();
            }
            Method method = tm.getClass().getMethod("getImei", int.class);

            return method.invoke(tm, slotId).toString();

        } catch (Throwable e) {

        }

        return "";

    }

    private Boolean GuangMu = false;

    public void wuGuangMu(View view) {
        GuangMu = false;
        text_status.setText("无光幕模式");
    }

    public void youGuangMu(View view) {

        GuangMu = true;
        text_status.setText("有光幕模式");
    }

    private void showInputDialog() {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        String Text = "修改串口，当前串口为：" + this.path;
        inputDialog.setTitle(Text).setView(editText);
        inputDialog.setPositiveButton("确定",
                (dialog, which) -> {


                    path = editText.getText().toString();


                    Context ctx = AcitivityScreenLessDeviceSetting.this;
                    SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("path", path);
                    editor.apply();


                    dialog.dismiss();
                });

        inputDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        dialog.dismiss();
                    }

                });
        inputDialog.setCancelable(false);
        inputDialog.show();
    }

//
//    public static String GetCRC_MODBUS(String str) {
//        byte[] bytes = toBytes(str);
//        int CRC = 0x0000ffff;
//        int POLYNOMIAL = 0x0000a001;
//        int i, j;
//        for (i = 0; i < bytes.length; i++) {
//            CRC ^= ((int) bytes[i] & 0x000000ff);
//            for (j = 0; j < 8; j++) {
//                if ((CRC & 0x00000001) != 0) {
//                    CRC >>= 1;
//                    CRC ^= POLYNOMIAL;
//                } else {
//                    CRC >>= 1;
//                }
//            }
//        }
//        String crc = Integer.toHexString(CRC);
//        return crc.toUpperCase();
//    }
//    //TODO 将16进制字符串转换为byte[]
//    public static byte[] toBytes(String str) {
//        byte[] bytes = new BigInteger(str, 16).toByteArray();
//        return bytes;
//    }

    private boolean openPath() {
        boolean haveUsb = false;
// 在Activity或Service中调用
        List<String> serialPorts = leesche.smartrecycling.base.serial.SerialHelper.getAllSerialPortPath();
        if (serialPorts.isEmpty()) {
            Logger.d("SerialTest", "未检测到可用串口");
        } else {
            Logger.d("SerialTest", "检测到" + serialPorts.size() + "个串口：");

            for (String port : serialPorts) {
                if (path.contains(port)) {
                    haveUsb = true;
                }
            }
        }
        if (haveUsb) {

            serialHelper.setPort(path);
            try {
                serialHelper.open();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return haveUsb;
    }

    public void readTemp(View view) {
        String cmd = "0107000000000000000000000000000000009229";
        text_gallery.setText("发送：" + cmd);
//        CoinSerialManage.getInstance().open(path);//打开串口
//        CoinSerialManage.getInstance().send(cmd);// 开锁


        openPath();

        serialHelper.sendHex(cmd);  // 发送Hex

    }


    public static String GetCRC_MODBUS(String str) {
        byte[] bytes = toBytes(str);
        int crc = 0xFFFF; // MODBUS标准初始值
        int polynomial = 0xA001; // MODBUS标准多项式

        for (byte b : bytes) {
            crc ^= (b & 0xFF); // 确保无符号处理
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ polynomial; // 无符号右移并异或
                } else {
                    crc = crc >>> 1;
                }
            }
        }

        // 将CRC的高字节和低字节合并，并确保顺序为高字节在前
        String result = String.format("%02X%02X", (crc >> 8) & 0xFF, crc & 0xFF);
        return result;
    }

    public static byte[] toBytes(String hexStr) {
        if (hexStr.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must be even-length");
        }
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int val = Integer.parseInt(hexStr.substring(index, index + 2), 16);
            bytes[i] = (byte) val;
        }
        return bytes;
    }

    public void handleBoxString(String targetStr) {
        // 初始化变量
        firstBox = "";
        unDoBox = "";
        finishDoWaterList.clear(); // 清空FINISH队列
        startDoWaterList.clear();  // 清空START队列

        if (TextUtils.isEmpty(targetStr)) {
            return;
        }

        // 按分号分割不同类型（FINISH和START）
        String[] parts = targetStr.split(";");
        String finishNumberStr = null; // FINISH对应的数字串
        String startNumberStr = null;  // START对应的数字串

        // 解析各部分，提取FINISH和START的数字串
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("FINISH_DO_WATER:")) {
                finishNumberStr = part.replace("FINISH_DO_WATER:", "").trim();
            } else if (part.startsWith("START_DO_WATER:")) {
                startNumberStr = part.replace("START_DO_WATER:", "").trim();
            }
        }

        // 填充FINISH队列（如"2,1" -> ["2","1"]）
        if (!TextUtils.isEmpty(finishNumberStr)) {
            finishDoWaterList.addAll(Arrays.asList(finishNumberStr.split(",")));
        }

        // 填充START队列（如"3" -> ["3"]）
        if (!TextUtils.isEmpty(startNumberStr)) {
            startDoWaterList.addAll(Arrays.asList(startNumberStr.split(",")));
        }

        // 初始化firstBox和unDoBox（优先用FINISH，若无则用START）
        String numberStr = TextUtils.isEmpty(finishNumberStr) ? startNumberStr : TextUtils.isEmpty(startNumberStr) ? finishNumberStr : finishNumberStr + "," + startNumberStr;
        Logger.i("numberStr" + numberStr);
        if (!TextUtils.isEmpty(numberStr)) {
            String[] numbers = numberStr.split(",");
            if (numbers.length > 0) {
                firstBox = numbers[0];
                unDoBox = (numbers.length > 1) ? TextUtils.join(",", Arrays.copyOfRange(numbers, 1, numbers.length)) : "";
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                doingTextView.setText(firstBox + "号制水中"); // 使用更新后的firstBox
                if (StringUtils.isEmpty(unDoBox) || "null".equals(unDoBox)) {
                    undoTextView.setVisibility(View.GONE);
                } else {
                    undoTextView.setVisibility(View.VISIBLE);
                    undoTextView.setText(unDoBox + "号请等待"); // 使用更新后的unDoBox
                }
            }
        });

//        updateFirstAndUnDoBoxForStart();
        if (!finishAndReturnStart)
            setVideoType(VideoType.START_DO_WATER);
//
//        if (!finishDoWaterList.isEmpty()) {
////            if (!finishAndReturnStart)
////                setVideoType(VideoType.FINISH_DO_WATER);
//        } else
//            setVideoType(VideoType.START_DO_WATER);
    }

    String firstElement = "";
    boolean finishAndReturnStart = false;

    /**
     * 取出FINISH_DO_WATER队列的第一个元素，并触发状态切换
     *
     * @return 第一个元素（若队列为空则返回null）
     */
    public String getFirstFinishDoWaterElement() {
        if (finishDoWaterList.isEmpty()) {
            boolean isAllEmpty = finishDoWaterList.isEmpty() && startDoWaterList.isEmpty();
            if (isAllEmpty) {
                setVideoType(VideoType.IDLE);
            } else {
                setVideoType(VideoType.START_DO_WATER);
            }
            return null;
        }
        finishAndReturnStart = true;


//        try {
//            updateFirstAndUnDoBoxForFinish();
//        } catch (Exception e) {
//            Logger.i("error" + e.getMessage());
//        }

        setVideoType(VideoType.FINISH_DO_WATER); // 确保在主线程调用

        String url4 = "/cashier/deleteFinishBox?deviceNum=" + storeId + "&box=" + firstBox;
        // 从本地查询该条码是否存在
        TCPAsyncNetUtils.get(url4, response -> {
            Logger.d("deleteFinishBox: " + response);
            if (!finishDoWaterList.isEmpty()) {
                try {
                    finishDoWaterList.remove(0);
                } catch (Exception e) {

                }
            }

        });


        return firstElement;
    }

//    // 辅助方法：更新FINISH状态下的firstBox和unDoBox（剩余队列）
//    private void updateFirstAndUnDoBoxForFinish() {
//        if (finishDoWaterList.isEmpty()) {
//            firstBox = "";
//            unDoBox = "";
//        } else {
//            firstBox = finishDoWaterList.get(0);
//            unDoBox = (finishDoWaterList.size() > 1)
//                    ? TextUtils.join(",", finishDoWaterList.subList(1, finishDoWaterList.size()))
//                    : "";
//        }
//    }

    // 辅助方法：更新START状态下的firstBox和unDoBox（START队列）
    private void updateFirstAndUnDoBoxForStart() {
        if (startDoWaterList.isEmpty()) {
            // 若START队列为空，用FINISH剩余队列（确保有内容显示）
            firstBox = finishDoWaterList.isEmpty() ? "" : finishDoWaterList.get(0);
            unDoBox = (finishDoWaterList.size() > 1)
                    ? TextUtils.join(",", finishDoWaterList.subList(1, finishDoWaterList.size()))
                    : "";
        } else {
            firstBox = startDoWaterList.get(0);
            unDoBox = (startDoWaterList.size() > 1)
                    ? TextUtils.join(",", startDoWaterList.subList(1, startDoWaterList.size()))
                    : "";
        }
    }

    // 目标方法：处理字符串并赋值给firstBox和unDoBox
//    public void handleBoxString(String targetStr) {
//        // 1. 初始化变量（默认值可根据需求调整）
//
//        // 2. 空值防御：避免空指针异常（若targetStr为null，直接返回默认空值）
//        if (TextUtils.isEmpty(targetStr)) {
//            // 可根据业务需求决定是否给默认值（如firstBox = "1"）
//            firstBox = "";
//            unDoBox = "";
//            return;
//        }
//
//        // 3. 判断字符串是否包含逗号
//        if (targetStr.contains(",")) {
//            // 3.1 包含逗号：拆分处理
//            // 找到第一个逗号的索引
//            int firstCommaIndex = targetStr.indexOf(",");
//
//            // 3.1.1 第一个逗号前的内容赋值给firstBox（若逗号在开头，firstBox为空）
//            firstBox = targetStr.substring(0, firstCommaIndex);
//
//            // 3.1.2 第一个逗号后的内容（从逗号后一位开始截取）
//            String afterFirstComma = targetStr.substring(firstCommaIndex + 1);
//
//            // 3.1.3 逗号后内容用","分割，再用空格连接赋值给unDoBox
//            if (!TextUtils.isEmpty(afterFirstComma)) {
//                // 分割成数组（若afterFirstComma以逗号结尾，会生成空元素，可加filter过滤）
//                String[] unDoParts = afterFirstComma.split(",");
//                // 用空格连接数组（Android推荐用TextUtils.join，兼容低版本）
//                unDoBox = TextUtils.join(" ", unDoParts);
//            }
//            // 若第一个逗号后无内容，unDoBox保持空值
//        } else {
//            // 3.2 不包含逗号：整个字符串赋值给firstBox，unDoBox为空
//            firstBox = targetStr;
//            unDoBox = ""; // 或根据需求设默认值（如unDoBox = "1"）
//        }
//
//    }


    // 存储0105命令的队列（Hex字符串格式，如"0105"）
    private final Queue<String> commandQueue = new LinkedList<>();
    // 发送状态标记：true=正在发送，false=空闲
    private boolean isSending = false;
    // 重入锁：保证队列操作和状态修改的线程安全
    private final ReentrantLock lock = new ReentrantLock();
    // 超时定时器：发送0105后启动，10秒未收到应答则触发超时
    private Timer sendTimer;
    private Timer totalTimer;
    // 标记当前是否处于超时等待中（避免重复启动定时器）
    private volatile boolean isWaitingAck = false;


    /**
     * 添加0105命令到队列（Hex字符串格式，如"0105"）
     */
    public void addCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            Logger.e("添加的命令为空，忽略");
            return;
        }

        lock.lock();
        try {

            commandQueue.add(command);
            Logger.i("0105命令添加到队列，当前队列大小：" + commandQueue.size());

            // 若当前无发送任务，立即触发第一条命令发送
            if (!isSending && !isWaitingAck) {
                sendNextCommand();
            }
        } finally {
            lock.unlock();
        }
    }

    public void returnIdle() {

//        if (!commandQueue.isEmpty() || !StringUtils.isBlank(firstBox) || !StringUtils.isBlank(unDoBox)){
//
//            if(nowVideoType!= VideoType.FINISH_DO_WATER){
//                setVideoType(VideoType.START_DO_WATER);
//            }
//
//        }
    }

    /**
     * 发送队列中的下一条命令（核心逻辑）
     */
    public void sendNextCommand() {
        lock.lock();
        try {
            // 1. 校验状态：队列空/正在发送/等待应答，直接返回
            if (commandQueue.isEmpty() || isSending || isWaitingAck) {
                Logger.i("发送条件不满足：队列空=" + commandQueue.isEmpty()
                        + "，正在发送=" + isSending + "，等待应答=" + isWaitingAck);
                return;
            }

            // 2. 取出队首命令（先进先出）
            String nextCommand = commandQueue.poll();
            if (nextCommand == null || nextCommand.trim().isEmpty()) {
                Logger.e("取出的命令为空，终止发送");
                resetSendState(); // 重置状态
                return;
            }

            // 3. 标记发送状态（防止并发发送）
            isSending = true;
            isWaitingAck = true;
            Logger.i("准备发送0105命令：" + nextCommand + "，剩余队列大小：" + commandQueue.size());

            // 4. 发送命令到串口（通过SerialHelper的sendHex方法）
            serialHelper.sendHex(nextCommand);
            // 5. 启动10秒超时定时器：未收到应答则强制继续下一条
            startSendTimer();

            try {
                getFirstFinishDoWaterElement();
            } catch (Exception e) {
                Logger.e(JSON.toJSONString(e));
            }


        } finally {
            lock.unlock();
        }
    }


    /**
     * 启动10秒超时定时器
     */
    private void startSendTimer() {
        // 先取消之前的定时器（避免重复计时）
        cancelSendTimer();

        sendTimer = new Timer();
        sendTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 超时回调：10秒未收到0005，强制继续下一条
                Logger.w("0105命令发送后10秒未收到0005应答，触发超时处理");
                resetSendState(); // 重置等待状态
                sendNextCommand(); // 继续发送下一条
                returnIdle();
            }
        }, 10 * 1000); // 延迟10秒执行

        Logger.i("10秒超时定时器已启动");
    }


    /**
     * 取消超时定时器
     */
    private void cancelSendTimer() {
        lock.lock();
        try {
            if (sendTimer != null) {
                sendTimer.cancel();
                sendTimer.purge(); // 清除已取消的任务
                sendTimer = null;
                Logger.i("超时定时器已取消");
            }
        } finally {
            lock.unlock();
        }
    }

    private void cancelTotalTimer() {
        lock.lock();
        try {
            if (totalTimer != null) {
                totalTimer.cancel();
                totalTimer.purge(); // 清除已取消的任务
                totalTimer = null;
                Logger.i("超时定时器已取消");
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * 重置发送状态（isSending、isWaitingAck）
     */
    private void resetSendState() {
        lock.lock();
        try {
            isSending = false;
            isWaitingAck = false;
            Logger.i("发送状态已重置：isSending=false，isWaitingAck=false");
        } finally {
            lock.unlock();
        }
    }


    private void startTotalTimer() {
//        // 先取消之前的定时器（避免重复计时）
//        cancelTotalTimer();
//        totalTimer = new Timer();
//        totalTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // 超时回调：10秒未收到0005，强制继续下一条
//                returnIdle();
//            }
//        }, 1000); // 延迟10秒执行
//
//        Logger.i("10秒超时定时器已启动");
    }
}