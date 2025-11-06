package com.ycmachine.smartdevice.activity.cameraTest;

import static com.ycmachine.smartdevice.constent.ClientConstant.medicineCabinetLayer;
import static com.ycmachine.smartdevice.manager.GridRegionManager.GetKeyByValue;
import static com.ycmachine.smartdevice.manager.GridRegionManager.LevelMapLayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.creator.CameraWrapper;
import com.ycmachine.smartdevice.entity.ypg.Layer;
import com.ycmachine.smartdevice.handler.ComponenTestHandler;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;
import com.ycmachine.smartdevice.manager.CabinetQrManager;
import com.ycmachine.smartdevice.manager.GridRegionManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.HomeFragmentListener;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.RxTimer;

public class CameraActivity extends BaseActivity implements RxTimer.OnTimeCounterListener,
        HomeFragmentListener, CameraWrapper.CameraCallback {
    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2;
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，处理延迟和超时

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
    public void toImageRecognition(View view) {

        for (CameraWrapper wrapper : cameraWrappers) {
            wrapper.closeCamera();
        }
        startActivity(new Intent(this, ImageRecognitionActivity.class));
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
        for (CameraWrapper wrapper : cameraWrappers) {
            wrapper.closeCamera();
        }
        finish();
    }

    // 批量拍照逻辑
    public void snapDevice(View view) {
        Logger.e(ClientConstant.nowFloor+ "onCameraStuck");

        YpgLogicHandler.getInstance().snapDevice(new ArrayList<>(selectedLayerValues));
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

        checkBoxList.add(findViewById(R.id.cb_9));
        checkBoxIndexMap.put(checkBoxList.get(10), 10);
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
                int msg_nowlevel = event.getMsg_level();
                CameraWrapper cameraWrapper = cameraWrappers.get(numCamera);
                cameraWrapper.takePictureFromExternal(msg_nowlevel);
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
                Log.d("LayerValue", "selectedLayerValues：" + JSON.toJSONString(selectedLayerValues));

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
    public void onImageSaved(int cameraNum, String filePath,int nowLevel) {
        Logger.i(nowLevel+"摄像头" + cameraNum + "保存图片：" + filePath);
        // 可在这里处理图片保存后的逻辑（如上传）

//        int nowLevel = ClientConstant.nowFloor;

        new  Thread(()->{
            List<GridRegion> gridRegions = GridRegionManager.getInstance().getGridRegions(LevelMapLayer.get(nowLevel), cameraNum);
            Logger.i("当前key:"+ JSON.toJSONString(gridRegions));
            if(gridRegions==null || gridRegions.size()==0){
                Logger.e("没有配置层级"+nowLevel+"摄像头"+cameraNum+"的识别区域");
                return;
            }
            CabinetQrManager.getInstance().processPhoto(String.valueOf(cameraNum), nowLevel, filePath, gridRegions, listener);

        }).start();

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

    @Override
    public void onVideoSaved(int cameraNum, String filePath) {
        Logger.i("摄像头" + cameraNum + "保存视频：" + filePath);
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

    @Override
    public void onCameraStuck(int cameraNum) {
        Logger.e(ClientConstant.nowFloor+ "onCameraStuck"+cameraNum);
        YpgLogicHandler.getInstance().snapDevice(new ArrayList<>(selectedLayerValues));

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


        mainHandler.postDelayed(() -> {
            handoffCamera(1, true);
            handoffCamera(2, true);
        }, 2000); // 1分钟无操作进入广告页
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

        initLayerRadioButtons();
        setupInputListener();
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


    // 绑定所有RadioButton（与布局ID对应）
    @BindView(R2.id.rb_layer_1)
    RadioButton rbLayer1;

    @BindView(R2.id.rb_layer_2)
    RadioButton rbLayer2;

    @BindView(R2.id.rb_layer_3)
    RadioButton rbLayer3;

    @BindView(R2.id.rb_layer_4)
    RadioButton rbLayer4;

    @BindView(R2.id.rb_pick_layer)
    RadioButton rbPickLayer;

    @BindView(R2.id.rb_layer_5)
    RadioButton rbLayer5;

    @BindView(R2.id.rb_layer_6)
    RadioButton rbLayer6;

    @BindView(R2.id.rb_layer_7)
    RadioButton rbLayer7;

    @BindView(R2.id.rb_layer_8)
    RadioButton rbLayer8;

    @BindView(R2.id.rb_layer_9)
    RadioButton rbLayer9;

    @BindView(R2.id.et_step_count)
    EditText etStepCount;

    @BindView(R2.id.rb_recycle_layer)
    RadioButton rbRecycleLayer;

    @BindView(R2.id.rb_reposition)
    RadioButton rbReposition;

    // 绑定保存按钮布局
    @BindView(R2.id.ll_save_setting)
    View llSaveSetting;

    // 存储所有层级RadioButton的列表
    private List<RadioButton> layerRadioButtons = new ArrayList<>();

    /**
     * 初始化层级选择RadioButton
     */
    private void initLayerRadioButtons() {
        // 将所有层级RadioButton添加到列表
        layerRadioButtons.add(rbLayer1);
        layerRadioButtons.add(rbLayer2);
        layerRadioButtons.add(rbLayer3);
        layerRadioButtons.add(rbLayer4);
        layerRadioButtons.add(rbPickLayer);
        layerRadioButtons.add(rbLayer5);
        layerRadioButtons.add(rbLayer6);
        layerRadioButtons.add(rbLayer7);
        layerRadioButtons.add(rbLayer8);
        layerRadioButtons.add(rbLayer9);
        layerRadioButtons.add(rbRecycleLayer);

        // 设置全局单选监听器
        View.OnClickListener layerListener = v -> {
            RadioButton clickedRb = (RadioButton) v;

            // 操作执行中禁止切换
            if (ClientConstant.IS_DOING) {
                clickedRb.setChecked(false);
                Toast.makeText(
                        clickedRb.getContext(),
                        "操作正在执行中，请稍后再试",
                        Toast.LENGTH_SHORT
                ).show();

                ClientConstant.IS_DOING = false;
                return;
            }

            // 实现单选效果：仅选中当前点击的按钮
            for (RadioButton rb : layerRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }

            // 处理层级选择逻辑
            handleLayerSelection(clickedRb);
        };

        // 为所有层级RadioButton绑定监听器
        for (RadioButton rb : layerRadioButtons) {
            rb.setOnClickListener(layerListener);
        }
        handleLayerOperation(1); // 1层逻辑

    }

    /**
     * 设置输入监听，实现输入完成后自动保存
     */
    private void setupInputListener() {
        // 监听输入文本变化（实时保存，可选）
        etStepCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isBlank(s.toString()))
                    return;
                // 输入变化后立即保存（适合需要实时保存的场景）
//                saveStepCountToLocal(s.toString());
                ClientConstant.medicineCabinetLayer[nowLayerNumber - 1] = new Layer(Integer.parseInt(s.toString()));
            }
        });

        // 监听失去焦点事件（输入完成后，点击其他地方时保存）
        etStepCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 失去焦点时
//                saveStepCountToLocal(etStepCount.getText().toString());
            }
        });
    }


    /**
     * 处理层级选择逻辑
     */
    private void handleLayerSelection(RadioButton selectedRb) {
        String layerName = selectedRb.getText().toString();
        int layerId = selectedRb.getId();

        // 打印选中的层级信息
        Logger.d("选中层级：" + layerName + "，ID：" + layerId);

        // 根据选中的层级执行对应逻辑
        if (layerId == R.id.rb_layer_1) {
            handleLayerOperation(1); // 1层逻辑
        } else if (layerId == R.id.rb_layer_2) {
            handleLayerOperation(2); // 2层逻辑
        } else if (layerId == R.id.rb_layer_3) {
            handleLayerOperation(3); // 3层逻辑
        } else if (layerId == R.id.rb_layer_4) {
            handleLayerOperation(4); // 4层逻辑
        } else if (layerId == R.id.rb_pick_layer) {
            handleLayerOperation(9); // 取货层逻辑
        } else if (layerId == R.id.rb_layer_5) {
            handleLayerOperation(5); // 5层逻辑
        } else if (layerId == R.id.rb_layer_6) {
            handleLayerOperation(6); // 6层逻辑
        } else if (layerId == R.id.rb_layer_7) {
            handleLayerOperation(7); // 7层逻辑
        } else if (layerId == R.id.rb_layer_8) {
            handleLayerOperation(8); // 8层逻辑
        } else if (layerId == R.id.rb_recycle_layer) {
            handleLayerOperation(10); // 回收层逻辑
        } else if (layerId == R.id.rb_layer_9) {
            handleLayerOperation(11); // 回收层逻辑
        }
    }

    private int nowLayerNumber = 1;

    /**
     * 处理1-8层的通用操作
     */
    private void handleLayerOperation(int layerNumber) {
        nowLayerNumber = layerNumber;
        Logger.d("执行" + layerNumber + "层操作");
        // 1-8层的通用业务逻辑
//        YpgLogicHandler.getInstance().handleLayerOperation(layerNumber, 0);

        Layer layer = ClientConstant.medicineCabinetLayer[layerNumber - 1];
        etStepCount.setText(layer.getBushu() + "");

    }

    /**
     * 保存设置按钮点击事件（通过ButterKnife的@OnClick绑定）
     */
    @OnClick({R2.id.ll_save_setting, R2.id.rb_reposition})
    public void onViewClick(View view) {

        if (view.getId() == R.id.ll_save_setting) {
            if (ClientConstant.IS_DOING) {
                Toast.makeText(this, "操作正在执行中，无法保存", Toast.LENGTH_SHORT).show();
                ClientConstant.IS_DOING = false;
                return;
            }

            // 获取当前选中的层级
            RadioButton selectedRb = getSelectedLayerRadioButton();
            if (selectedRb == null) {
                Toast.makeText(this, "请先选择层级", Toast.LENGTH_SHORT).show();
                return;
            }

            // 执行保存逻辑（示例）
            Logger.d("保存设置：选中层级=" + selectedRb.getText());
            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();

            Gson gson = new Gson();
            FileUtil.writeFileSdcardFile(Constants.LAYER_LIST, gson.toJson(ClientConstant.medicineCabinetLayer));
        }
        if (view.getId() == R.id.rb_reposition) {
            handleReposition();
        }
    }

    /**
     * 获取当前选中的层级RadioButton
     */
    private RadioButton getSelectedLayerRadioButton() {
        for (RadioButton rb : layerRadioButtons) {
            if (rb.isChecked()) {
                return rb;
            }
        }
        return null;
    }

     void handleReposition() {
        Log.d("Control", "执行复位操作");
        ComponenTestHandler.getInstance().YaxisReset();
    }

}