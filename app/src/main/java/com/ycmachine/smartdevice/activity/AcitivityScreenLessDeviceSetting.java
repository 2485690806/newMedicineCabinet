package com.ycmachine.smartdevice.activity;


import static tp.xmaihh.serialport.utils.ByteUtil.Byte2Hex;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.network.net.TCPAsyncNetUtils;
import com.ycmachine.smartdevice.network.socket.MyWebSocketClient;
import com.ycmachine.smartdevice.utils.DeviceIdUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android_serialport_api.SerialPortFinder;
import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.handler.LocalConfigManager;
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

    @BindView(R2.id.tvTimer)
    TextView tvTimer;

    @BindView(R2.id.mainlinearLayout)
    LinearLayout mainlinearLayout;

    //连续点击5次进入设备设置
    private final int DEVICE_CLICK_NUM = 5;
    //点击时间间隔3秒
    private final int DEVICE_CLICK_INTERVER_TIME = 1500;
    //上一次的点击时间
    private long DEVICE_lastClickTime = 0;
    //记录点击次数
    private int DEVICE_clickNum = 0;

    private static final int REQUEST_CODE_SELECT_MEDIA = 100;
    private List<LocalVImageHolderView> bannerHolders = new ArrayList<>();

    @Override
    protected void onDestroy() {
        cIdleBanner.stopTurning();
        super.onDestroy();
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

        try {
            serialHelper.open();
        } catch (IOException e) {

        }

        String deviceIdByGetImei = "";
        try {
            deviceIdByGetImei = getDeviceIdByGetImei(this, 0);
            storeId = deviceIdByGetImei.substring(deviceIdByGetImei.length() - 6);

        } catch (Exception e) {
            deviceIdByGetImei = DeviceIdUtil.getDeviceId(this);
            storeId = deviceIdByGetImei;

        }
        Logger.d("onCreate: " + storeId);


        try {
            handlerGetStatus.postDelayed(runnableGetStatus, 3000);//3秒查看是否有命令下发
        } catch (Exception e) {
            Logger.v("----2-----" + e);
        }

        Context ctx = AcitivityScreenLessDeviceSetting.this;
        SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
        String path = sp.getString("path", "none");
        if (!path.equals("none"))
            this.path = path;

//        this.getPath();
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
//                            Logger.d(TAG,  comBean.sRecTime + " Rx:<==" + new String(comBean.bRec, StandardCharsets.UTF_8));
                    }
                });
            }
        };

        try {
            serialHelper.open();
        } catch (IOException e) {

        }

    }

    Handler handlerGetStatus = new Handler();
    Runnable runnableGetStatus = new Runnable() {
        @Override
        public void run() {

            handlerGetStatus.removeCallbacks(runnableGetStatus);
            handlerGetStatus.postDelayed(runnableGetStatus, 3000);

            // 弹簧机查看是否有命令下发
            String url4 = "/cashier/getCommand?deviceNum=" + storeId;
            AtomicBoolean flag = new AtomicBoolean(false);
            try {
                // 从本地查询该条码是否存在
                TCPAsyncNetUtils.get(url4, response -> {

                    Logger.d("run: " + response);
                    JSONObject data2 = new JSONObject(response);
                    String cmd = data2.optString("data");

                    if (!cmd.equals("null")) {
                        flag.set(true);

                        try {
//                            CoinSerialManage.getInstance().open(path);//打开串口
//                            CoinSerialManage.getInstance().send(cmd);// 开锁

                            try {
                                serialHelper.open();
                            } catch (IOException e) {

                            }
                            serialHelper.sendHex(cmd);  // 发送Hex
                        } catch (Exception e) {

                        }
                        text_gallery.setText("发送：" + cmd);
//                        handlerGetStatus.removeCallbacks(runnableGetStatus);
//                        handlerGetStatus.postDelayed(runnableGetStatus, 5000);

                    } else {
//                        handlerGetStatus.removeCallbacks(runnableGetStatus);
//                        handlerGetStatus.postDelayed(runnableGetStatus, 3000);

                    }


                });
            } catch (Exception e) {
            }
        }
    };


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

            try {
                serialHelper.open();
            } catch (IOException e) {

            }
            serialHelper.sendHex(cmd);  // 发送Hex


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
            cIdleBanner.notifyDataSetChanged();
        } else {
            cIdleBanner.setVisibility(View.GONE);
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

                try {
                    serialHelper.open();
                } catch (IOException e) {

                }
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


    public void readTemp(View view) {
        String cmd = "0107000000000000000000000000000000009229";
        text_gallery.setText("发送：" + cmd);
//        CoinSerialManage.getInstance().open(path);//打开串口
//        CoinSerialManage.getInstance().send(cmd);// 开锁

        try {
            serialHelper.open();
        } catch (IOException e) {

        }
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
}