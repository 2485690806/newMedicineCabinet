package com.ycmachine.smartdevice.creator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.activity.CameraActivity;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;

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

/**
 * 单个摄像头的封装类，管理单个摄像头的所有操作
 */
public class CameraWrapper {
    private static final String TAG = "CameraWrapper";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    // 摄像头唯一标识（1/2/3）
    private final int cameraNum;
    // 上下文与外部回调
    private final Context context;
    private final CameraCallback callback;
    // 后台线程（从Activity共享）
    private final Handler backgroundHandler;
    private final Semaphore cameraOpenCloseLock;

    // 摄像头组件
    private TextureView textureView;
    private RadioButton toggleButton;
    private TextView captureButton;
    private TextView recordButton;
    private TextView stopButton;

    // 摄像头状态
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewRequestBuilder;
    private Size previewSize;
    private Size captureSize;
    private ImageReader imageReader;
    private MediaRecorder mediaRecorder;
    private boolean isActive = false;
    private boolean isRecording = false;
    private Surface recorderSurface;
    private String fileUrl;

    // 回调接口：通知Activity状态变化
    public interface CameraCallback {
        void onToast(String message);
        void onImageSaved(int cameraNum, String filePath);
        void onVideoSaved(int cameraNum, String filePath);
        int getDisplayRotation();
        File getExternalFilesDir();
    }

    public CameraWrapper(int cameraNum, Context context, Handler backgroundHandler,
                         Semaphore cameraOpenCloseLock, CameraCallback callback) {
        this.cameraNum = cameraNum;
        this.context = context;
        this.backgroundHandler = backgroundHandler;
        this.cameraOpenCloseLock = cameraOpenCloseLock;
        this.callback = callback;
    }

    // 初始化UI组件
    @SuppressLint("SetTextI18n")
    public void initComponents(View cameraArea) {
        textureView = cameraArea.findViewById(R.id.texture_view);
        toggleButton = cameraArea.findViewById(R.id.toggle_btn);
        captureButton = cameraArea.findViewById(R.id.capture_btn);
        recordButton = cameraArea.findViewById(R.id.record_btn);
        stopButton = cameraArea.findViewById(R.id.stop_btn);
        toggleButton.setText(context.getString(R.string.close_camera) + cameraNum);
        ((TextView) cameraArea.findViewById(R.id.camera_num)).setText(String.valueOf(cameraNum));
        setupListeners();
    }

    // 设置组件监听器
    private void setupListeners() {
        // 切换摄像头开关
        toggleButton.setOnClickListener(v -> toggleCamera());
        // 拍照
        captureButton.setOnClickListener(v -> takePicture());
        // 开始录像
        recordButton.setOnClickListener(v -> startRecording());
        // 停止录像
        stopButton.setOnClickListener(v -> stopRecording());
        // 预览纹理监听
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (isActive) {
                    configureTransform(width, height);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (isActive) {
                    configureTransform(width, height);
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
        });
    }

    // 切换摄像头状态（打开/关闭）
    public void toggleCamera() {
        if (isActive) {
            closeCamera();
        } else {
            openCamera();
        }
    }
    /**
     * 暴露给外部的拍照方法
     */
    public void takePictureFromExternal() {
        // 确保摄像头处于激活状态
        if (!isActive) {
            Log.w(TAG, "摄像头" + cameraNum + "未激活，无法拍照");
            return;
        }
        // 调用内部拍照逻辑
        takePicture();
    }

    // 打开摄像头
    private void openCamera() {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length < cameraNum) {
                callback.onToast("未检测到摄像头" + cameraNum);
                return;
            }
            // 分配摄像头ID（第1个摄像头用0，第2个用1，以此类推）
            cameraId = cameraIds[Math.min(cameraNum - 1, cameraIds.length - 1)];

            // 获取摄像头参数
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                callback.onToast("摄像头" + cameraNum + "配置失败");
                return;
            }


            // 配置预览和拍照尺寸
            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), 1920, 1080);
            captureSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), 3840, 2160);
            imageReader = ImageReader.newInstance(captureSize.getWidth(), captureSize.getHeight(),
                    ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(this::onImageAvailable, backgroundHandler);

            // 尝试获取锁，打开摄像头
            if (cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    cameraOpenCloseLock.release();
                    return;
                }
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            } else {
                callback.onToast("打开摄像头" + cameraNum + "超时");
            }
        } catch (CameraAccessException | InterruptedException e) {
            callback.onToast("打开摄像头" + cameraNum + "失败：" + e.getMessage());
            Log.e(TAG, "openCamera error", e);
        }
    }

    // 关闭摄像头
    public void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            // 停止录像（如果正在录制）
            if (isRecording) {
                stopRecording();
            }
            // 释放资源
            if (captureSession != null) {
                captureSession.close();
                captureSession = null;
            }
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }
            isActive = false;
            toggleButton.setChecked(false);
            updateUIState();
        } catch (InterruptedException e) {
            Log.e(TAG, "closeCamera interrupted", e);
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    // 摄像头状态回调
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice = camera;
            isActive = true;
            toggleButton.setChecked(true);
            createPreviewSession();
            updateUIState();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;
            isActive = false;
            toggleButton.setChecked(false);
            updateUIState();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice = null;
            cameraOpenCloseLock.release();
            camera.close();
            isActive = false;
            toggleButton.setChecked(false);
            callback.onToast("摄像头" + cameraNum + "错误：" + error);
            updateUIState();
        }
    };

    // 创建预览会话
    private void createPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) return;

            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);

            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            int rotation = callback.getDisplayRotation();
            int orientation = ORIENTATIONS.get(rotation);

            if ((cameraNum == 1) || (cameraNum == 3)) {
                orientation = (orientation + 180) % 360;
            }
            previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS.get(orientation));

            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(surface);
            surfaces.add(imageReader.getSurface());

            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        // 自动对焦和曝光
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 开始预览
                        session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "createPreviewSession error", e);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    callback.onToast("摄像头" + cameraNum + "预览配置失败");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createPreviewSession error", e);
        }
    }

    // 拍照
    public void takePicture() {
        if (!isActive || cameraDevice == null || captureSession == null) return;

        try {
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());

            // 自动对焦和曝光
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            int rotation = callback.getDisplayRotation();
            int orientation = ORIENTATIONS.get(rotation);

            if ((cameraNum == 1) || (cameraNum == 3)) {
                orientation = (orientation + 180) % 360;
            }


            // 照片方向
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS.get(orientation));

            // 停止预览并拍照
            captureSession.stopRepeating();
            captureSession.capture(captureBuilder.build(), captureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            callback.onToast("摄像头" + cameraNum + "拍照失败");
            Log.e(TAG, "takePicture error", e);
        }
    }

    // 拍照回调（恢复预览）
    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            try {
                // 恢复预览
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
            } catch (CameraAccessException e) {
                Log.e(TAG, "captureCallback error", e);
            }
        }
    };

    // 处理拍照图像
    private void onImageAvailable(ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            if (image != null) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                saveImageToFile(bytes);
            }
        } finally {
            if (image != null) image.close();
        }
    }

    // 保存照片
    private void saveImageToFile(byte[] bytes) {
        Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        try {

            File file = new File(callback.getExternalFilesDir(),
                    "photo_cam" + cameraNum +"_level_"+  YpgLogicHandler.getInstance().getNowLevel() + "_" + System.currentTimeMillis()  + ".jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            callback.onImageSaved(cameraNum, file.getAbsolutePath());
            callback.onToast("摄像头" + cameraNum + "照片已保存");
        } catch (IOException e) {
            callback.onToast("摄像头" + cameraNum + "保存失败");
            Log.e(TAG, "saveImage error", e);
        }
    }

    // 开始录像
    public void startRecording() {
        if (!isActive || isRecording || cameraDevice == null) return;

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 输出文件
            File videoFile = new File(callback.getExternalFilesDir(),
                    "video" + cameraNum + "_" + System.currentTimeMillis() + ".mp4");
            fileUrl = videoFile.getAbsolutePath();
            mediaRecorder.setOutputFile(fileUrl);

            // 视频参数
            mediaRecorder.setVideoEncodingBitRate(10000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(previewSize.getWidth(), previewSize.getHeight());
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            // 视频方向
            mediaRecorder.setOrientationHint(ORIENTATIONS.get(callback.getDisplayRotation()));
            mediaRecorder.prepare();

            // 配置录制会话
            recorderSurface = mediaRecorder.getSurface();
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(texture);

            CaptureRequest.Builder recorderBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            recorderBuilder.addTarget(previewSurface);
            recorderBuilder.addTarget(recorderSurface);

            List<Surface> surfaces = Arrays.asList(previewSurface, recorderSurface);
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        mediaRecorder.start();
                        isRecording = true;
                        updateUIState();
                    } catch (Exception e) {
                        callback.onToast("摄像头" + cameraNum + "录像失败");
                        Log.e(TAG, "startRecording error", e);
                        releaseMediaRecorder();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    callback.onToast("摄像头" + cameraNum + "录像配置失败");
                    releaseMediaRecorder();
                }
            }, backgroundHandler);
        } catch (Exception e) {
            callback.onToast("摄像头" + cameraNum + "录像失败");
            Log.e(TAG, "startRecording error", e);
            releaseMediaRecorder();
        }
    }

    // 停止录像
    public void stopRecording() {
        if (!isRecording || mediaRecorder == null) return;

        try {
            mediaRecorder.stop();
        } catch (RuntimeException e) {
            Log.e(TAG, "stopRecording error (可能录制过短)", e);
        } finally {
            releaseMediaRecorder();
            isRecording = false;
            callback.onVideoSaved(cameraNum, fileUrl);
            callback.onToast("摄像头" + cameraNum + "录像已保存");
            // 恢复预览
            createPreviewSession();
            updateUIState();
        }
    }

    // 释放MediaRecorder
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        recorderSurface = null;
    }

    // 配置预览旋转和缩放
    public void configureTransform(int viewWidth, int viewHeight) {
        if (textureView == null || previewSize == null) return;

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        int rotation = callback.getDisplayRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    // 更新UI状态（按钮可用状态等）
    private void updateUIState() {
        ((CameraActivity) context).runOnUiThread(() -> {
            toggleButton.setText(isActive ?  context.getString(R.string.close_camera) + cameraNum : context.getString(R.string.open_camera) + cameraNum);
            toggleButton.setChecked(isActive);
            captureButton.setEnabled(isActive && !isRecording);
            recordButton.setEnabled(isActive && !isRecording);
            stopButton.setEnabled(isActive && isRecording);
        });
    }

    // 选择合适的预览/拍照尺寸
    private Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();
        for (Size option : choices) {
            if (option.getWidth() == width && option.getHeight() == height) {
                return option;
            }
            if (option.getHeight() == option.getWidth() * height / width
                    && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (!bigEnough.isEmpty()) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    // 尺寸比较器
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    // Getter
    public boolean isActive() {
        return isActive;
    }

    public int getCameraNum() {
        return cameraNum;
    }
}