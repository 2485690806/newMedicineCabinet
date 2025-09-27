package com.ycmachine.smartdevice.activity.cameraTest;

import static com.ycmachine.smartdevice.constent.ClientConstant.medicineCabinetLayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.creator.CameraWrapper;
import com.ycmachine.smartdevice.entity.ypg.Layer;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.HomeFragmentListener;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.RxTimer;

public class CameraActivity extends BaseActivity implements RxTimer.OnTimeCounterListener,
        HomeFragmentListener, CameraWrapper.CameraCallback {
    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2;

    // 摄像头封装类列表（管理3个摄像头）
    private List<CameraWrapper> cameraWrappers = new ArrayList<>();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化摄像头管理器
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // 启动后台线程
        startBackgroundThread();
        // 初始化3个摄像头封装类
        initCameraWrappers();
    }

    // 初始化摄像头封装类
    private void initCameraWrappers() {
        // 摄像头1
        CameraWrapper camera1 = new CameraWrapper(1, this, backgroundHandler,
                cameraOpenCloseLock, this);
        camera1.initComponents(area1);
        cameraWrappers.add(camera1);

        // 摄像头2
        CameraWrapper camera2 = new CameraWrapper(2, this, backgroundHandler,
                cameraOpenCloseLock, this);
        camera2.initComponents(area2);
        cameraWrappers.add(camera2);

        // 摄像头3
        CameraWrapper camera3 = new CameraWrapper(3, this, backgroundHandler,
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

    // 返回按钮
    public void backToActivity(View view) {
        finish();
    }

    // 批量拍照逻辑
    public void snapDevice(View view) {

        YpgLogicHandler.getInstance().snapDevice(selectedLayerValues);
    }

    // 初始化CheckBox
    private void initCheckboxesWithIndex() {
        checkBoxList.add(findViewById(R.id.cb_1));
        checkBoxIndexMap.put(checkBoxList.get(0), 0);

        checkBoxList.add(findViewById(R.id.cb_2));
        checkBoxIndexMap.put(checkBoxList.get(1), 1);

        checkBoxList.add(findViewById(R.id.cb_3));
        checkBoxIndexMap.put(checkBoxList.get(2), 2);

        checkBoxList.add(findViewById(R.id.cb_4));
        checkBoxIndexMap.put(checkBoxList.get(3), 3);

        checkBoxList.add(findViewById(R.id.cb_5));
        checkBoxIndexMap.put(checkBoxList.get(4), 4);

        checkBoxList.add(findViewById(R.id.cb_6));
        checkBoxIndexMap.put(checkBoxList.get(5), 5);

        checkBoxList.add(findViewById(R.id.cb_7));
        checkBoxIndexMap.put(checkBoxList.get(6), 6);

        checkBoxList.add(findViewById(R.id.cb_8));
        checkBoxIndexMap.put(checkBoxList.get(7), 7);

        checkBoxList.add(findViewById(R.id.cb_pick));
        checkBoxIndexMap.put(checkBoxList.get(8), 8);

        checkBoxList.add(findViewById(R.id.cb_recycle));
        checkBoxIndexMap.put(checkBoxList.get(9), 9);

        // 设置CheckBox监听
        setCheckboxListeners();

        // 设置默认选中的CheckBox（示例：默认选中1号、3号、取货层）
        setDefaultCheckedBoxes(ClientConstant.LayerValues); // 传入需要默认选中的索引
    }

    @SuppressLint({"SetTextI18n", "StringFormatMatches"})
    @Override
    public void onMessageEvent(BasicMessageEvent event) {
        switch (event.getMessage_id()) {
            case EventType.BasicEvent.SNAP_CAMERA_NUM:
                int numCamera = event.getMsg_flag();
                CameraWrapper cameraWrapper = cameraWrappers.get(numCamera);
                cameraWrapper.takePictureFromExternal();
                break;
        }
    }

    /**
     * 设置默认选中的CheckBox，并记录对应的值
     *
     * @param defaultIndices 需要默认选中的索引数组
     */
    private void setDefaultCheckedBoxes(int... defaultIndices) {
        for (int index : defaultIndices) {
            // 遍历CheckBox列表，找到对应索引的CheckBox
            for (Map.Entry<CheckBox, Integer> entry : checkBoxIndexMap.entrySet()) {
                if (entry.getValue() == index) {
                    CheckBox checkBox = entry.getKey();
                    checkBox.setChecked(true); // 设置为选中状态

                    // 主动触发选中事件，获取对应的值（与setCheckboxListeners逻辑一致）
                    Layer selectedLayer = medicineCabinetLayer[index];
                    Log.d("LayerValue", "默认选中：" + checkBox.getText() + "，对应值=" + selectedLayer.getBushu());

                    int layerValue = selectedLayer.getBushu();
                    selectedLayerValues.add(index);

                    break;
                }
            }
        }
    }

    Gson gson = new Gson();

    // CheckBox监听
    private void setCheckboxListeners() {
        removeDuplicates();
        for (CheckBox checkBox : checkBoxList) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int index = checkBoxIndexMap.get(buttonView);

                if (isChecked) {

                    Layer selectedLayer = medicineCabinetLayer[index];
                    Log.d("LayerValue", "选中：" + buttonView.getText() + "，对应值=" + selectedLayer.getBushu());

                    // 选中时添加值（避免重复添加）
                    if (!selectedLayerValues.contains(index)) {
                        selectedLayerValues.add(index);

                        Log.d("LayerValue", "选中：" + buttonView.getText() + "，值=" + index);
                    }


                } else {
                    // 取消选中时移除值
                    selectedLayerValues.remove(Integer.valueOf(index));
                    Log.d("LayerValue", "取消选中：" + buttonView.getText() + "，值=" + index);
                }

                removeDuplicates();
                ClientConstant.LayerValues = new int[selectedLayerValues.size()];
                for (int i = 0; i < selectedLayerValues.size(); i++) {
                    ClientConstant.LayerValues[i] = selectedLayerValues.get(i); // 直接取索引值
                }
                removeDuplicates();
                FileUtil.writeFileSdcardFile(Constants.LAYER_VALUE, gson.toJson(ClientConstant.LayerValues));
                Log.d("LayerValue", "当前选中的层级值：" + Arrays.toString(ClientConstant.LayerValues));

            });
        }
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
    // 权限请求
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
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
                for (CameraWrapper wrapper : cameraWrappers) {
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

    // 外部切换摄像头状态
    public void handoffCamera(int cameraNum, boolean status) {
        for (CameraWrapper wrapper : cameraWrappers) {
            if (wrapper.getCameraNum() == cameraNum) {
                if (wrapper.isActive() != status) {
                    wrapper.toggleCamera();
                }
                break;
            }
        }
    }

    // CameraCallback接口实现（与CameraWrapper交互）
    @Override
    public void onToast(String message) {
        Logger.i("Toast消息：" + message);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onImageSaved(int cameraNum, String filePath) {
         Logger.i( "摄像头" + cameraNum + "保存图片：" + filePath);
        // 可在这里处理图片保存后的逻辑（如上传）
    }

    @Override
    public void onVideoSaved(int cameraNum, String filePath) {
         Logger.i( "摄像头" + cameraNum + "保存视频：" + filePath);
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

    // 生命周期
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        // 恢复预览
        for (CameraWrapper wrapper : cameraWrappers) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭所有摄像头
        for (CameraWrapper wrapper : cameraWrappers) {
            wrapper.closeCamera();
        }
        stopBackgroundThread();
    }

    // BaseActivity抽象方法实现
    @Override
    public int initLayout() {
        return R.layout.activity_camera;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initCheckboxesWithIndex();
    }

    @Override
    public void initData() {
    }

    @Override
    public void skipToFragment(int position) {
    }

    @Override
    public void onTimeEnd() {
    }
}