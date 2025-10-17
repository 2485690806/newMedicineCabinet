package com.ycmachine.smartdevice.activity.medicineCabinet;

import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_CLICK_INTERVER_TIME;
import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_CLICK_NUM;
import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_clickNum;
import static com.ycmachine.smartdevice.constent.ClientConstant.DEVICE_lastClickTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.creator.UnVisityCameraWrapper;
import com.ycmachine.smartdevice.handler.InitMachineHandler;
import com.ycmachine.smartdevice.handler.MedHttpHandler;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;
import com.ycmachine.smartdevice.manager.CabinetQrManager;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.entity.QrCodeBinding;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.handler.LocalConfigManager;
import leesche.smartrecycling.base.qrcode.GridRegionManager;
import leesche.smartrecycling.base.utils.DataSourceOperator;
import leesche.smartrecycling.base.utils.RxTimer;
import leesche.smartrecycling.base.view.LocalVImageHolderView;

public class UserMedPointActivity extends BaseActivity implements RxTimer.OnTimeCounterListener, UnVisityCameraWrapper.CameraCallback {
    private static final String TAG = "UserMedPointActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2;

    // 摄像头封装类列表（管理3个摄像头）
    private List<UnVisityCameraWrapper> cameraWrappers = new ArrayList<>();
    // 共享资源
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Semaphore cameraOpenCloseLock = new Semaphore(1);
    private CameraManager cameraManager;

    // CheckBox相关
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private Map<CheckBox, Integer> checkBoxIndexMap = new HashMap<>();
    // 新增一个列表用于存储所有选中的层级值
    private List<Integer> selectedLayerValues = new ArrayList<>();
    // 布局组件
    @BindView(R2.id.camera_area_1)
    View area1;
    @BindView(R2.id.camera_area_2)
    View area2;
    @BindView(R2.id.camera_area_3)
    View area3;

    @BindView(R2.id.sendCmd)
    TextView sendCmd;

    @BindView(R2.id.recCmd)
    TextView recCmd;

    @BindView(R2.id.tv_error)
    TextView tvError;

    @BindView(R2.id.tv_dispensing)
    TextView tvDispensing;

    @BindView(R2.id.et_code)
    EditText etCode;


    @BindView(R2.id.mainlinearLayout)
    LinearLayout mainlinearLayout;

    @BindView(R2.id.cIdleBanner)
    ConvenientBanner cIdleBanner;

    @BindView(R2.id.tvTimer)
    TextView tvTimer;
    int Time = 100;

    @BindView(R2.id.viewStart)
    View viewStart;
    @BindView(R2.id.backToSetting)
    View backToSetting;

    private List<LocalVImageHolderView> bannerHolders = new ArrayList<>();

    @Override
    public int initLayout() {
        return R.layout.activity_user_med_point;

    }

    @Override
    protected void onDestroy() {
        cIdleBanner.stopTurning();
        super.onDestroy();

        MedHttpHandler.getInstance().onDestroy();
        // 关闭所有摄像头
        for (UnVisityCameraWrapper wrapper : cameraWrappers) {
            wrapper.closeCamera();
        }
        MedHttpHandler.getInstance().onDestroy();
        stopBackgroundThread();
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


    @OnClick({R2.id.mainlinearLayout, R2.id.viewStart, R2.id.backToSetting})
    public void onViewClick(View view) {
        if (view.getId() == R.id.mainlinearLayout) {
            RxTimer.getInstance().resetCurrentTimer(true);
        }
        if (view.getId() == R.id.backToSetting) {

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
                LocalConfigManager.getInstance().parseAdJsonToDisplay(null, true);
                LocalConfigManager.getInstance().setIdleBannerList(new ArrayList<>());

                startActivity(new Intent(this, YPGActivity.class));
            }
        }
        if (view.getId() == R.id.viewStart) {
            displayIdleBanner(false);

        }
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

    @SuppressLint({"SetTextI18n", "StringFormatMatches"})
    @Override
    public void onMessageEvent(BasicMessageEvent event) {
        switch (event.getMessage_id()) {
            case EventType.BasicEvent.SNAP_CAMERA_NUM:
                Logger.e("收到拍照事件，摄像头编号：" + event.getMsg_flag());
                int numCamera = event.getMsg_flag();
                UnVisityCameraWrapper cameraWrapper = cameraWrappers.get(numCamera);
                cameraWrapper.takePictureFromExternal();
                break;
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        RxTimer.getInstance().start(tvTimer, Time, "ManagerLogin", this);

        LocalConfigManager.getInstance().loadAdDataFromLocal();
        setPages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YpgLogicHandler.getInstance().init(this, sendCmd, recCmd);
        InitMachineHandler.init();

        // 初始化摄像头管理器
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // 启动后台线程
        startBackgroundThread();
        // 初始化3个摄像头封装类
        initCameraWrappers();

//        handoffCamera(3,true);
    }

    @Override
    public void initData() {

//        handoffCamera(3,true);
//        findViewById(R.id.dummy_focus_view).requestFocus();
        MedHttpHandler.getInstance().initialize(this);
    }

    public void handoffCamera(int cameraNum, boolean status) {
        for (UnVisityCameraWrapper wrapper : cameraWrappers) {
            if (wrapper.getCameraNum() == cameraNum) {
                if (wrapper.isActive() != status) {
                    wrapper.toggleCamera();
                }
                break;
            }
        }
    }

    // 生命周期
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        // 恢复预览
        for (UnVisityCameraWrapper wrapper : cameraWrappers) {
            if (wrapper.isActive()) {
                // 触发纹理重新配置
                View view = wrapper.getCameraNum() == 1 ? area1 :
                        (wrapper.getCameraNum() == 2 ? area2 : area3);
                TextureView textureView = view.findViewById(R.id.texture_view);
                if (textureView != null && textureView.isAvailable()) {
                    wrapper.configureTransform(textureView.getWidth(), textureView.getHeight());
                }
            }
        }

        mainHandler.postDelayed(() -> {


            handoffCamera(1, true);
            handoffCamera(2, true);
        }, 2000); // 1分钟无操作进入广告页
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onToast("相机权限已授予");
            } else {
                onToast("需要相机权限才能使用此功能");
            }
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onToast("录音权限已授予");
                // 重新尝试打开摄像头3
                for (UnVisityCameraWrapper wrapper : cameraWrappers) {
                    if (wrapper.getCameraNum() == 3 && !wrapper.isActive()) {
                        wrapper.toggleCamera();
                        break;
                    }
                }
            } else {
                onToast("需要录音权限才能录像");
            }
        }
    }

    // 初始化摄像头封装类
    private void initCameraWrappers() {
        // 摄像头1
        UnVisityCameraWrapper camera1 = new UnVisityCameraWrapper(1, this, backgroundHandler,
                cameraOpenCloseLock, this);
        camera1.initComponents(area1);
        cameraWrappers.add(camera1);

        // 摄像头2
        UnVisityCameraWrapper camera2 = new UnVisityCameraWrapper(2, this, backgroundHandler,
                cameraOpenCloseLock, this);
        camera2.initComponents(area2);
        cameraWrappers.add(camera2);

        // 摄像头3
        UnVisityCameraWrapper camera3 = new UnVisityCameraWrapper(3, this, backgroundHandler,
                cameraOpenCloseLock, this);
        camera3.initComponents(area3);
        cameraWrappers.add(camera3);
    }

    // 启动后台线程
    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，处理延迟和超时

    // 停止后台线程
    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "停止后台线程失败", e);
            }
        }
    }

    public void Accept(View view) {
        RxTimer.getInstance().resetCurrentTimer(true);

        inputToFind(etCode.getText().toString().trim());

    }

    private void inputToFind(String code) {
        if (code.isEmpty()) {
            onToast("请输入二维码");
            return;
        }
        // 查询这个码对应的是第几层第几个货道
        QrCodeBinding qrCodeBinding = CabinetQrManager.getInstance().findByItemQr(code);

        if (qrCodeBinding == null) {
            onToast("未找到对应二维码");
            return;
        }

        if(ClientConstant.IS_DOING){
            onToast("操作正在执行中");

            ClientConstant.IS_DOING = false;
            return;
        }

        // 可能被取走了
        DataSourceOperator.getInstance().deleteByItemQrCode(code);
        // 发送取走
        if (!StringUtils.isBlank(qrCodeBinding.getBagId()))
            MedHttpHandler.getInstance().BagRemove(qrCodeBinding.getBagId());

        // 打印日志（示例）
        Logger.d("选中的层数: " + qrCodeBinding.getLevel() + ", 数字: " + qrCodeBinding.getGridNumber());
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.Standard;
        // 一键出货
        YpgLogicHandler.getInstance().handleLayerOperation(qrCodeBinding.getLevel(), Integer.parseInt(qrCodeBinding.getGridNumber()));
    }

    public void snapTwoCamera() {
        // 第一个任务：发送第一个EventBus事件（立即执行，之后延迟500ms执行第二个任务）
        mainHandler.post(() -> {
            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.SNAP_CAMERA_NUM, 0));

            // 第二个任务：延迟500ms发送第二个EventBus事件
            mainHandler.postDelayed(() -> {
                EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.SNAP_CAMERA_NUM, 1));

            }, 500); // 第一个到第二个间隔500ms
        });
    }

    @Override
    public void onTimeEnd() {

        displayIdleBanner(true);
    }


    // CameraCallback接口实现（与CameraWrapper交互）
    @Override
    public void onToast(String message) {
        Logger.i(message);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onImageSaved(int cameraNum, String filePath) {
        Logger.d("摄像头" + cameraNum + "保存图片：" + filePath);
        // 可在这里处理图片保存后的逻辑（如上传）
        int nowLevel = ClientConstant.nowFloor;
        List<GridRegion> gridRegions = GridRegionManager.getInstance().getGridRegions(nowLevel, cameraNum);
        if(gridRegions==null || gridRegions.size()==0){
            Logger.e("没有配置层级"+nowLevel+"摄像头"+cameraNum+"的识别区域");
            return;
        }

        CabinetQrManager.getInstance().processPhoto(String.valueOf(cameraNum), nowLevel, filePath, gridRegions, listener);

    }

    @Override
    public void onVideoSaved(int cameraNum, String filePath) {
        Logger.d("摄像头" + cameraNum + "保存视频：" + filePath);
        // 可在这里处理视频保存后的逻辑
    }

    @Override
    public int getDisplayRotation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public File getExternalFilesDir() {
        return getExternalFilesDir(null);
    }

    CabinetQrManager.OnProcessListener listener = new CabinetQrManager.OnProcessListener() {
        @Override
        public void onSuccess() {
            // 处理成功的回调
            System.out.println("Binding success: ");
        }

        @Override
        public void onError(Exception e) {
            // 处理失败的回调
            System.out.println("Binding failed: " + e);
        }
    };

//    private String mScanData = "";  // 条码

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {  // 扫条形码
//
//
//        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
//
//
//            char pressedKey = (char) event.getUnicodeChar();
//            mScanData += pressedKey;
//            return true;
//
//        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//
//
//            inputToFind(mScanData.trim());
//            mScanData = "";
//
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }

}
