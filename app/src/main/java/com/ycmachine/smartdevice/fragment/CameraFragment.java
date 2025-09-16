package com.ycmachine.smartdevice.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.HomeFragmentListener;
import leesche.smartrecycling.base.utils.RxTimer;


public class CameraFragment extends BaseFragment implements RxTimer.OnTimeCounterListener, HomeFragmentListener {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "CameraModel";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    // 摄像头1组件
    private TextureView textureView1;
    private Button toggleButton1;
    private Button captureButton1;
    private Button recordButton1;
    private Button stopButton1;
    private CameraDevice cameraDevice1;
    private CameraCaptureSession cameraCaptureSession1;
    private CaptureRequest.Builder previewRequestBuilder1;
    private Size previewSize1;
    private Size captureSize1;
    private ImageReader imageReader1;
    private String cameraId1;
    private MediaRecorder mediaRecorder1;
    private boolean isRecording1 = false;
    private boolean isCamera1Active = false;
    private Surface recorderSurface1;

    // 摄像头2组件
    private TextureView textureView2;
    private Button toggleButton2;
    private Button captureButton2;
    private Button recordButton2;
    private Button stopButton2;
    private CameraDevice cameraDevice2;
    private CameraCaptureSession cameraCaptureSession2;
    private CaptureRequest.Builder previewRequestBuilder2;
    private Size previewSize2;
    private Size captureSize2;
    private ImageReader imageReader2;
    private String cameraId2;
    private MediaRecorder mediaRecorder2;
    private boolean isRecording2 = false;
    private boolean isCamera2Active = false;
    private Surface recorderSurface2;

    // 摄像头3组件
    private TextureView textureView3;
    private Button toggleButton3;
    private Button captureButton3;
    private Button recordButton3;
    private Button stopButton3;
    private CameraDevice cameraDevice3;
    private CameraCaptureSession cameraCaptureSession3;
    private CaptureRequest.Builder previewRequestBuilder3;
    private Size previewSize3;
    private Size captureSize3;
    private ImageReader imageReader3;
    private String cameraId3;
    private MediaRecorder mediaRecorder3;
    private boolean isRecording3 = false;
    private boolean isCamera3Active = false;
    private Surface recorderSurface3;

    // 共享资源
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Semaphore cameraOpenCloseLock = new Semaphore(1);
    private CameraManager cameraManager;

    private String fileUrl1;
    private String fileUrl2;
    private String fileUrl3;





    @BindView(R2.id.camera_area_1)
    View area1;
    @BindView(R2.id.camera_area_2)
    View area2;
    @BindView(R2.id.camera_area_3)
    View area3;

    // 初始化3个摄像头的组件（从XML中获取）
    private void initCameraComponents() {
        // 摄像头1
        textureView1 = area1.findViewById(R.id.texture_view);
        toggleButton1 = area1.findViewById(R.id.toggle_btn);
        captureButton1 = area1.findViewById(R.id.capture_btn);
        recordButton1 = area1.findViewById(R.id.record_btn);
        stopButton1 = area1.findViewById(R.id.stop_btn);
        toggleButton1.setText("开启摄像头1");
        setupComponentListeners(1);

        // 摄像头2
        textureView2 = area2.findViewById(R.id.texture_view);
        toggleButton2 = area2.findViewById(R.id.toggle_btn);
        captureButton2 = area2.findViewById(R.id.capture_btn);
        recordButton2 = area2.findViewById(R.id.record_btn);
        stopButton2 = area2.findViewById(R.id.stop_btn);
        toggleButton2.setText("开启摄像头2");
        setupComponentListeners(2);

        // 摄像头3
        textureView3 = area3.findViewById(R.id.texture_view);
        toggleButton3 = area3.findViewById(R.id.toggle_btn);
        captureButton3 = area3.findViewById(R.id.capture_btn);
        recordButton3 = area3.findViewById(R.id.record_btn);
        stopButton3 = area3.findViewById(R.id.stop_btn);
        toggleButton3.setText("开启摄像头3");
        setupComponentListeners(3);
    }

    // 为组件设置点击事件（替代原动态设置）
    private void setupComponentListeners(int cameraNum) {
        Button toggleBtn = getToggleButtonByNum(cameraNum);
        Button captureBtn = getCaptureButtonByNum(cameraNum);
        Button recordBtn = getRecordButtonByNum(cameraNum);
        Button stopBtn = getStopButtonByNum(cameraNum);

        if (toggleBtn != null) {
            toggleBtn.setOnClickListener(v -> toggleCamera(cameraNum));
        }
        if (captureBtn != null) {
            captureBtn.setOnClickListener(v -> takePicture(cameraNum));
        }
        if (recordBtn != null) {
            recordBtn.setOnClickListener(v -> startRecording(cameraNum));
        }
        if (stopBtn != null) {
            stopBtn.setOnClickListener(v -> stopRecording(cameraNum));
        }

        // 设置TextureView监听器
        TextureView textureView = getTextureViewByNum(cameraNum);
        if (textureView != null) {
            textureView.setSurfaceTextureListener(createTextureListener(cameraNum));
        }
    }

    // 辅助方法：根据编号获取按钮（新增）
    private Button getToggleButtonByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return toggleButton1;
            case 2: return toggleButton2;
            case 3: return toggleButton3;
            default: return null;
        }
    }

    private Button getCaptureButtonByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return captureButton1;
            case 2: return captureButton2;
            case 3: return captureButton3;
            default: return null;
        }
    }


    private void setupTextureListener(int cameraNum) {
        TextureView textureView = getTextureViewByNum(cameraNum);
        if (textureView != null) {
            textureView.setSurfaceTextureListener(createTextureListener(cameraNum));
            if (textureView.isAvailable()) {
                configureTransform(textureView, textureView.getWidth(), textureView.getHeight(), getPreviewSizeByNum(cameraNum));
            }
        }
    }


    private void startBackgroundThread() {
        if (backgroundThread == null) {
            backgroundThread = new HandlerThread("CameraBackground");
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
        }
    }

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

    // 切换摄像头状态
    private void toggleCamera(int cameraNum) {
        Context context = getContext();
        if (context == null) return;

        // 检查相机权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            Log.d(TAG, "可用摄像头数量: " + cameraIdList.length);

            if (cameraNum > cameraIdList.length) {
                showToast("未检测到足够的摄像头(需要" + cameraNum + "个)");
                return;
            }

            // 分配摄像头ID
            assignCameraId(cameraNum, cameraIdList);

            // 处理摄像头开关状态
            if (cameraNum == 1) {
                handleCameraToggle(1, isCamera1Active, toggleButton1, captureButton1, recordButton1);
                isCamera1Active = !isCamera1Active;
            } else if (cameraNum == 2) {
                handleCameraToggle(2, isCamera2Active, toggleButton2, captureButton2, recordButton2);
                isCamera2Active = !isCamera2Active;
            } else {
                // 录像需要录音权限
                if (activity != null && !isCamera3Active) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestAudioPermission();
                        return;
                    }
                }

                handleCameraToggle(3, isCamera3Active, toggleButton3, captureButton3, recordButton3);
                isCamera3Active = !isCamera3Active;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            showToast("访问摄像头失败");
        }
    }

    private void assignCameraId(int cameraNum, String[] cameraIdList) {
        switch (cameraNum) {
            case 1:
                cameraId1 = cameraIdList[0];
                break;
            case 2:
                cameraId2 = cameraIdList.length > 1 ? cameraIdList[1] : cameraIdList[0];
                break;
            case 3:
                cameraId3 = cameraIdList.length > 2 ? cameraIdList[2] :
                        (cameraIdList.length > 1 ? cameraIdList[1] : cameraIdList[0]);
                break;
        }
    }

    private void requestCameraPermission() {
        if (activity != null ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void requestAudioPermission() {
        if (activity != null ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleCameraToggle(int cameraNum, boolean isActive, Button toggleBtn,
                                    Button captureBtn, Button recordBtn) {
        if (isActive) {
            closeCamera(cameraNum);
            toggleBtn.setText("开启摄像头" + cameraNum);
            captureBtn.setEnabled(false);
            recordBtn.setEnabled(false);
            getStopButtonByNum(cameraNum).setEnabled(false);
        } else {
            openCamera(cameraNum);
            toggleBtn.setText("关闭摄像头" + cameraNum);
            captureBtn.setEnabled(true);
            recordBtn.setEnabled(true);
        }
    }

    private Button getStopButtonByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return stopButton1;
            case 2: return stopButton2;
            case 3: return stopButton3;
            default: return null;
        }
    }

    private void openCamera(int cameraNum) {
        try {
            String cameraId = getCameraIdByNum(cameraNum);
            if (cameraId == null) return;

            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map == null) {
                showToast("无法获取摄像头配置");
                return;
            }

            // 配置摄像头参数
            configureCameraParameters(cameraNum, map);

            if (cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    cameraOpenCloseLock.release();
                    return;
                }
                cameraManager.openCamera(cameraId, getStateCallback(cameraNum), backgroundHandler);
            } else {
                throw new RuntimeException("等待打开摄像头超时");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            showToast("打开摄像头" + cameraNum + "失败");
        } catch (InterruptedException e) {
            throw new RuntimeException("打开摄像头时被中断", e);
        }
    }

    private String getCameraIdByNum(int cameraNum) {
        return cameraNum == 1 ? cameraId1 : (cameraNum == 2 ? cameraId2 : cameraId3);
    }

    private void configureCameraParameters(int cameraNum, StreamConfigurationMap map) {
        if (cameraNum == 1) {
            previewSize1 = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), 1920, 1080);
            captureSize1 = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), 3840, 2160);
            imageReader1 = ImageReader.newInstance(captureSize1.getWidth(), captureSize1.getHeight(), ImageFormat.JPEG, 2);
            imageReader1.setOnImageAvailableListener(image -> onImageAvailable(image, 1), backgroundHandler);
        } else if (cameraNum == 2) {
            previewSize2 = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), 1920, 1080);
            captureSize2 = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), 3840, 2160);
            imageReader2 = ImageReader.newInstance(captureSize2.getWidth(), captureSize2.getHeight(), ImageFormat.JPEG, 2);
            imageReader2.setOnImageAvailableListener(image -> onImageAvailable(image, 2), backgroundHandler);
        } else {
            previewSize3 = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), 1920, 1080);
            captureSize3 = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), 3840, 2160);
            imageReader3 = ImageReader.newInstance(captureSize3.getWidth(), captureSize3.getHeight(), ImageFormat.JPEG, 2);
            imageReader3.setOnImageAvailableListener(image -> onImageAvailable(image, 3), backgroundHandler);
        }
    }

    private void closeCamera(int cameraNum) {
        try {
            cameraOpenCloseLock.acquire();

            if (cameraNum == 1) {
                // 如果正在录制，先停止
                if (isRecording1) {
                    stopRecording(cameraNum);
                }
                releaseCameraResources(cameraCaptureSession1, cameraDevice1, imageReader1);
                cameraCaptureSession1 = null;
                cameraDevice1 = null;
                imageReader1 = null;
            } else if (cameraNum == 2) {
                // 如果正在录制，先停止
                if (isRecording2) {
                    stopRecording(cameraNum);
                }
                releaseCameraResources(cameraCaptureSession2, cameraDevice2, imageReader2);
                cameraCaptureSession2 = null;
                cameraDevice2 = null;
                imageReader2 = null;
            } else {
                // 如果正在录制，先停止
                if (isRecording3) {
                    stopRecording(cameraNum);
                }
                releaseCameraResources(cameraCaptureSession3, cameraDevice3, imageReader3);
                cameraCaptureSession3 = null;
                cameraDevice3 = null;
                imageReader3 = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("关闭摄像头时被中断", e);
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    private void releaseCameraResources(CameraCaptureSession session, CameraDevice device, ImageReader reader) {
        if (session != null) {
            session.close();
        }
        if (device != null) {
            device.close();
        }
        if (reader != null) {
            reader.close();
        }
    }

    private void closeAllCameras() {
        if (isCamera1Active) closeCamera(1);
        if (isCamera2Active) closeCamera(2);
        if (isCamera3Active) closeCamera(3);
    }

    private TextureView.SurfaceTextureListener createTextureListener(final int cameraNum) {
        return new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // 只有摄像头激活时才配置
                if ((cameraNum == 1 && isCamera1Active) ||
                        (cameraNum == 2 && isCamera2Active) ||
                        (cameraNum == 3 && isCamera3Active)) {

                    configureTransform(
                            getTextureViewByNum(cameraNum),
                            width, height,
                            getPreviewSizeByNum(cameraNum)
                    );
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                configureTransform(
                        getTextureViewByNum(cameraNum),
                        width, height,
                        getPreviewSizeByNum(cameraNum)
                );
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };
    }

    private TextureView getTextureViewByNum(int cameraNum) {
        switch (cameraNum) {
            case 1:
                return textureView1;
            case 2:
                return textureView2;
            case 3:
                return textureView3;
            default:
                return null;
        }
    }

    private Size getPreviewSizeByNum(int cameraNum) {
        switch (cameraNum) {
            case 1:
                return previewSize1;
            case 2:
                return previewSize2;
            case 3:
                return previewSize3;
            default:
                return null;
        }
    }

    private CameraDevice.StateCallback getStateCallback(final int cameraNum) {
        return new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraOpenCloseLock.release();
                switch (cameraNum) {
                    case 1:
                        cameraDevice1 = camera;
                        createCameraPreviewSession(cameraDevice1, 1);
                        break;
                    case 2:
                        cameraDevice2 = camera;
                        createCameraPreviewSession(cameraDevice2, 2);
                        break;
                    case 3:
                        cameraDevice3 = camera;
                        createCameraPreviewSession(cameraDevice3, 3);
                        break;
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                cameraOpenCloseLock.release();
                camera.close();
                updateCameraState(cameraNum, false);
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                cameraOpenCloseLock.release();
                camera.close();
                updateCameraState(cameraNum, false);
                showToast("摄像头" + cameraNum + "错误: " + error);
            }
        };
    }

    private void updateCameraState(int cameraNum, boolean active) {
        if (activity != null ) {
            (activity).runOnUiThread(() -> {
                switch (cameraNum) {
                    case 1:
                        cameraDevice1 = null;
                        isCamera1Active = active;
                        toggleButton1.setText("开启摄像头1");
                        captureButton1.setEnabled(false);
                        recordButton1.setEnabled(false);
                        stopButton1.setEnabled(false);
                        break;
                    case 2:
                        cameraDevice2 = null;
                        isCamera2Active = active;
                        toggleButton2.setText("开启摄像头2");
                        captureButton2.setEnabled(false);
                        recordButton2.setEnabled(false);
                        stopButton2.setEnabled(false);
                        break;
                    case 3:
                        cameraDevice3 = null;
                        isCamera3Active = active;
                        toggleButton3.setText("开启摄像头3");
                        captureButton3.setEnabled(false);
                        recordButton3.setEnabled(false);
                        stopButton3.setEnabled(false);
                        break;
                }
            });
        }
    }

    private void createCameraPreviewSession(CameraDevice cameraDevice, int cameraNum) {
        try {
            TextureView textureView = getTextureViewByNum(cameraNum);
            Size previewSize = getPreviewSizeByNum(cameraNum);
            if (textureView == null || previewSize == null) return;

            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) return;

            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);

            final CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            // 保存预览请求构建器
            setPreviewRequestBuilder(cameraNum, builder);

            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(surface);

            // 添加ImageReader
            ImageReader reader = getImageReaderByNum(cameraNum);
            if (reader != null) {
                surfaces.add(reader.getSurface());
            }

            cameraDevice.createCaptureSession(surfaces,
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (cameraDevice == null) return;

                            // 保存会话引用
                            setCaptureSession(cameraNum, session);

                            try {
                                // 配置自动对焦和曝光
                                builder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                builder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                // 开始预览
                                CaptureRequest previewRequest = builder.build();
                                session.setRepeatingRequest(previewRequest, null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            showToast("摄像头" + cameraNum + "配置失败");
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private ImageReader getImageReaderByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return imageReader1;
            case 2: return imageReader2;
            case 3: return imageReader3;
            default: return null;
        }
    }

    private void setPreviewRequestBuilder(int cameraNum, CaptureRequest.Builder builder) {
        switch (cameraNum) {
            case 1:
                previewRequestBuilder1 = builder;
                break;
            case 2:
                previewRequestBuilder2 = builder;
                break;
            case 3:
                previewRequestBuilder3 = builder;
                break;
        }
    }

    private void setCaptureSession(int cameraNum, CameraCaptureSession session) {
        switch (cameraNum) {
            case 1:
                cameraCaptureSession1 = session;
                break;
            case 2:
                cameraCaptureSession2 = session;
                break;
            case 3:
                cameraCaptureSession3 = session;
                break;
        }
    }

    private void configureTransform(TextureView textureView, int viewWidth, int viewHeight, Size previewSize) {
        if (textureView == null || previewSize == null || activity == null) {
            return;
        }

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (activity != null ) {
            int rotation = (activity).getWindowManager().getDefaultDisplay().getRotation();
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                        (float) viewHeight / previewSize.getHeight(),
                        (float) viewWidth / previewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            }
        }

        textureView.setTransform(matrix);
    }

    public void takePicture(int cameraNum) {
        if ((cameraNum == 1 && !isCamera1Active) ||
                (cameraNum == 2 && !isCamera2Active) ||
                (cameraNum == 3 && !isCamera3Active)) {
            return;
        }

        CameraDevice cameraDevice = getCameraDeviceByNum(cameraNum);
        CameraCaptureSession session = getCaptureSessionByNum(cameraNum);
        CaptureRequest.Builder builder = getPreviewRequestBuilderByNum(cameraNum);
        ImageReader reader = getImageReaderByNum(cameraNum);

        if (cameraDevice == null || session == null || builder == null || reader == null) {
            return;
        }

        try {
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());

            // 配置自动对焦和曝光
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            if (activity != null ) {
                // 设置照片方向
                int rotation = (activity).getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            }

            // 停止预览并拍照
            session.stopRepeating();
            session.capture(captureBuilder.build(), getCaptureCallback(cameraNum, builder), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice getCameraDeviceByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return cameraDevice1;
            case 2: return cameraDevice2;
            case 3: return cameraDevice3;
            default: return null;
        }
    }

    private CameraCaptureSession getCaptureSessionByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return cameraCaptureSession1;
            case 2: return cameraCaptureSession2;
            case 3: return cameraCaptureSession3;
            default: return null;
        }
    }

    private CaptureRequest.Builder getPreviewRequestBuilderByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return previewRequestBuilder1;
            case 2: return previewRequestBuilder2;
            case 3: return previewRequestBuilder3;
            default: return null;
        }
    }

    private CameraCaptureSession.CaptureCallback getCaptureCallback(final int cameraNum, final CaptureRequest.Builder previewBuilder) {
        return new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                           @NonNull CaptureRequest request,
                                           @NonNull TotalCaptureResult result) {
                try {
                    // 恢复预览
                    previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    previewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                    CaptureRequest previewRequest = previewBuilder.build();
                    CameraCaptureSession captureSession = getCaptureSessionByNum(cameraNum);
                    if (captureSession != null) {
                        captureSession.setRepeatingRequest(previewRequest, null, backgroundHandler);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void onImageAvailable(ImageReader reader, int cameraNum) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            if (image != null) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                saveImageToFile(bytes, cameraNum);
            }
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }

    private void saveImageToFile(byte[] bytes, int cameraNum) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        savePhoto(bitmap, cameraNum);
    }

    private void savePhoto(Bitmap photo, int cameraNum) {
        if (getContext() == null) return;

        try {
            File file = new File(getContext().getExternalFilesDir(null),
                    "photo_cam" + cameraNum + "_" + System.currentTimeMillis() + ".jpg");
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                Log.e(TAG, "创建目录失败: " + parent.getAbsolutePath());
                return;
            }

            FileOutputStream fos = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            showToast("摄像头" + cameraNum + "照片已保存: " + file.getAbsolutePath());

//            HashMap<String, Object> eventData = new HashMap<>();
//            eventData.put("cameraNum", cameraNum);
//            eventData.put("filePath", file.getAbsolutePath());
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("detail", eventData);
//
//
//            (activity).runOnUiThread(() ->
//                    fireEvent("savePhoto", params));


        } catch (IOException e) {
            showToast("摄像头" + cameraNum + "保存照片失败");
            Log.e(TAG, "保存照片错误", e);
        }
    }

    public void startRecording(Integer cameraNum) {
        if ((cameraNum == 1 && (isRecording1 || !isCamera1Active || cameraDevice1 == null)) ||
                (cameraNum == 2 && (isRecording2 || !isCamera2Active || cameraDevice2 == null)) ||
                (cameraNum == 3 && (isRecording3 || !isCamera3Active || cameraDevice3 == null))) {
            return;
        }

        // 录像需要录音权限
        if (activity != null ) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestAudioPermission();
                return;
            }
        }

        try {
            // 获取当前摄像头的设备和预览尺寸
            CameraDevice cameraDevice = getCameraDeviceByNum(cameraNum);
            Size previewSize = getPreviewSizeByNum(cameraNum);
            TextureView textureView = getTextureViewByNum(cameraNum);

            if (cameraDevice == null || previewSize == null || textureView == null) {
                showToast("摄像头" + cameraNum + "初始化失败");
                return;
            }

            // 初始化MediaRecorder
            MediaRecorder mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 设置输出文件
            File videoFile = new File(getContext().getExternalFilesDir(null),
                    "video" + cameraNum + "_" + System.currentTimeMillis() + ".mp4");

            // 保存文件路径
            setFileUrlByNum(cameraNum, videoFile.getAbsolutePath());
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());

            // 配置视频参数
            mediaRecorder.setVideoEncodingBitRate(10000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(previewSize.getWidth(), previewSize.getHeight());
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            // 设置视频方向
            if (activity != null ) {
                int rotation = (activity).getWindowManager().getDefaultDisplay().getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0:
                        mediaRecorder.setOrientationHint(90);
                        break;
                    case Surface.ROTATION_90:
                        mediaRecorder.setOrientationHint(0);
                        break;
                    case Surface.ROTATION_180:
                        mediaRecorder.setOrientationHint(270);
                        break;
                    case Surface.ROTATION_270:
                        mediaRecorder.setOrientationHint(180);
                        break;
                }
            }

            mediaRecorder.prepare();


            // 获取录制Surface
            Surface recorderSurface = mediaRecorder.getSurface();
            setRecorderSurfaceByNum(cameraNum, recorderSurface);

            // 配置预览和录制会话
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(texture);

            final CaptureRequest.Builder recorderBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            recorderBuilder.addTarget(previewSurface);
            recorderBuilder.addTarget(recorderSurface);

            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(previewSurface);
            surfaces.add(recorderSurface);

            // 创建新的捕获会话
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    // 更新会话引用
                    setCaptureSession(cameraNum, session);

                    try {
                        mediaRecorder.start();
                        setIsRecordingByNum(cameraNum, true);

                        // 保存MediaRecorder引用
                        setMediaRecorderByNum(cameraNum, mediaRecorder);
                        // 更新UI状态
                        updateRecordingUI(cameraNum, true);

                        session.setRepeatingRequest(recorderBuilder.build(), null, backgroundHandler);
                    } catch (Exception e) {
                        Log.e(TAG, "摄像头" + cameraNum + "开始录制失败", e);
                        releaseMediaRecorderByNum(cameraNum);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    showToast("摄像头" + cameraNum + "配置录制会话失败");
                    releaseMediaRecorderByNum(cameraNum);
                }
            }, backgroundHandler);

        } catch (Exception e) {
            Log.e(TAG, "摄像头" + cameraNum + "开始录制失败", e);
            releaseMediaRecorderByNum(cameraNum);
        }
    }

    private void setFileUrlByNum(int cameraNum, String url) {
        switch (cameraNum) {
            case 1: fileUrl1 = url; break;
            case 2: fileUrl2 = url; break;
            case 3: fileUrl3 = url; break;
        }
    }

    private void setMediaRecorderByNum(int cameraNum, MediaRecorder recorder) {
        switch (cameraNum) {
            case 1: mediaRecorder1 = recorder; break;
            case 2: mediaRecorder2 = recorder; break;
            case 3: mediaRecorder3 = recorder; break;
        }
    }

    private void setRecorderSurfaceByNum(int cameraNum, Surface surface) {
        switch (cameraNum) {
            case 1: recorderSurface1 = surface; break;
            case 2: recorderSurface2 = surface; break;
            case 3: recorderSurface3 = surface; break;
        }
    }

    private void setIsRecordingByNum(int cameraNum, boolean isRecording) {
        switch (cameraNum) {
            case 1: isRecording1 = isRecording; break;
            case 2: isRecording2 = isRecording; break;
            case 3: isRecording3 = isRecording; break;
        }
    }

    private void updateRecordingUI(int cameraNum, boolean isRecording) {
        if (activity != null ) {
            (activity).runOnUiThread(() -> {
                Button recordBtn = getRecordButtonByNum(cameraNum);
                Button stopBtn = getStopButtonByNum(cameraNum);

                if (recordBtn != null && stopBtn != null) {
                    recordBtn.setEnabled(!isRecording);
                    stopBtn.setEnabled(isRecording);
                }
            });
        }
    }

    private Button getRecordButtonByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return recordButton1;
            case 2: return recordButton2;
            case 3: return recordButton3;
            default: return null;
        }
    }

    private void releaseMediaRecorderByNum(int cameraNum) {
        MediaRecorder recorder = getMediaRecorderByNum(cameraNum);
        if (recorder != null) {
            try {
                recorder.reset();
                recorder.release();
            } catch (Exception e) {
                Log.e(TAG, "释放MediaRecorder失败", e);
            }
        }

        switch (cameraNum) {
            case 1: mediaRecorder1 = null; break;
            case 2: mediaRecorder2 = null; break;
            case 3: mediaRecorder3 = null; break;
        }
    }

    private MediaRecorder getMediaRecorderByNum(int cameraNum) {
        switch (cameraNum) {
            case 1: return mediaRecorder1;
            case 2: return mediaRecorder2;
            case 3: return mediaRecorder3;
            default: return null;
        }
    }


    public void stopRecording(Integer cameraNum) {
        if ((cameraNum == 1 && (!isRecording1 || mediaRecorder1 == null)) ||
                (cameraNum == 2 && (!isRecording2 || mediaRecorder2 == null)) ||
                (cameraNum == 3 && (!isRecording3 || mediaRecorder3 == null))) {
            return;
        }

        MediaRecorder mediaRecorder = getMediaRecorderByNum(cameraNum);
        CameraDevice cameraDevice = getCameraDeviceByNum(cameraNum);
        TextureView textureView = getTextureViewByNum(cameraNum);
        Size previewSize = getPreviewSizeByNum(cameraNum);

        if (mediaRecorder == null || cameraDevice == null || textureView == null || previewSize == null) {
            return;
        }

        try {
            try {
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                Log.e("Recording", "停止录制时发生错误，可能是录制时间过短", e);
            }
            mediaRecorder.reset();
            mediaRecorder.release();

            // 更新录制状态
            setIsRecordingByNum(cameraNum, false);
            setMediaRecorderByNum(cameraNum, null);

            // 发送事件通知
            sendVideoSavedEvent(cameraNum);

            // 更新UI状态
            updateRecordingUI(cameraNum, false);

            // 恢复预览
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);

            CaptureRequest.Builder previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);
            setPreviewRequestBuilder(cameraNum, previewBuilder);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    setCaptureSession(cameraNum, session);
                    try {
                        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        previewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                        CaptureRequest previewRequest = previewBuilder.build();
                        session.setRepeatingRequest(previewRequest, null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    showToast("摄像头" + cameraNum + "恢复预览失败");
                }
            }, backgroundHandler);

        } catch (Exception e) {
            Log.e(TAG, "摄像头" + cameraNum + "停止录制失败", e);
        }
    }

    private void sendVideoSavedEvent(int cameraNum) {

        showToast("摄像头" + cameraNum + "视频已保存");
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();

        for (Size option : choices) {
            if (option.getWidth() == width && option.getHeight() == height) {
                return option;
            }

            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_camera;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context); // 调用父类的方法
        startBackgroundThread();
        // 恢复预览
        if (isCamera1Active) {
            setupTextureListener(1);
        }
        if (isCamera2Active) {
            setupTextureListener(2);
        }
        if (isCamera3Active) {
            setupTextureListener(3);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeAllCameras();
        stopBackgroundThread();
    }

    @Override
    protected void initView(View mRoot) {

        // 初始化摄像头管理器
        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        // 初始化3个摄像头的组件
        initCameraComponents();
        // 启动后台线程
        startBackgroundThread();
    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void skipToFragment(int position) {

    }

    @Override
    public void onTimeEnd() {

    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private void showToast(String message) {
        if (activity != null ) {
            (activity).runOnUiThread(() ->
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("相机权限已授予");
            } else {
                showToast("需要相机权限才能使用此功能");
            }
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("录音权限已授予");
                // 如果正在尝试开启摄像头3，则继续
                if (!isCamera3Active) {
                    toggleCamera(3);
                }
            } else {
                showToast("需要录音权限才能录像");
            }
        }
    }

    // uniapp切换摄像头状态: cameraNum: 1,2,3; status: true/false （开启/关闭摄像头）
    public void handoffCamera(int cameraNum, boolean status) {
        // 检查是否需要改变状态
        boolean currentStatus = cameraNum == 1 ? isCamera1Active :
                (cameraNum == 2 ? isCamera2Active : isCamera3Active);

        if (currentStatus == status) {
            return; // 状态相同，无需操作
        }

        toggleCamera(cameraNum);
    }
}
