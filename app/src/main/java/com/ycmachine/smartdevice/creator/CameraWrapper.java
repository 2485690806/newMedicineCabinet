package com.ycmachine.smartdevice.creator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.utils.FileUtil;

/**
 * 单个摄像头的封装类，管理单个摄像头的所有操作（新增点击全屏功能）
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

    // 原有成员变量...
    private final int cameraNum;
    private final Context context;
    private final CameraCallback callback;
    private final Handler backgroundHandler;
    private final Semaphore cameraOpenCloseLock;
    private TextureView textureView;
    private RadioButton toggleButton;
    private TextView captureButton;
    private TextView recordButton;
    private TextView stopButton;
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
    private volatile boolean isCameraClosed = false;
    private final Object cameraStateLock = new Object();

    // ---------------------- 新增：全屏相关变量 ----------------------
    private TextView cameraNumTextView; // 摄像头编号文本（点击触发全屏）
    private boolean isFullScreen = false; // 是否处于全屏状态
    private ViewGroup.LayoutParams originalTextureParams; // 原有TextureView布局参数（用于恢复）
    private AlertDialog fullScreenDialog; // 全屏预览弹窗
    private TextureView fullScreenTextureView; // 弹窗中的全屏TextureView
    // --------------------------------------------------------------

    // 预览帧超时检测（10秒无帧更新则判定为卡住）
    private static final long PREVIEW_TIMEOUT = 10000;
    private Handler timeoutHandler; // 用于执行超时检测和延迟重启
    private Runnable previewTimeoutRunnable; // 超时任务
    private boolean isRestarting = false; // 避免重复触发重启

    // 回调接口
    public interface CameraCallback {
        void onToast(String message);

        void onImageSaved(int cameraNum, String filePath, int currentFloor);

        void onVideoSaved(int cameraNum, String filePath);

        int getDisplayRotation();

        File getExternalFilesDir();

        void onCameraStuck(int cameraNum); // 新增：摄像头卡住回调
    }

    public CameraWrapper(int cameraNum, Context context, Handler backgroundHandler,
                         Semaphore cameraOpenCloseLock, CameraCallback callback) {
        this.cameraNum = cameraNum;
        this.context = context;
        this.backgroundHandler = backgroundHandler;
        this.cameraOpenCloseLock = cameraOpenCloseLock;
        this.callback = callback;
        this.timeoutHandler = new Handler(Looper.getMainLooper()); // 主线程执行UI和延迟任务
        initPreviewTimeoutRunnable(); // 初始化超时任务
    }
    // 初始化预览超时任务（核心：检测卡住逻辑）
    private void initPreviewTimeoutRunnable() {
        previewTimeoutRunnable = () -> {
            synchronized (cameraStateLock) {
                // 仅在相机激活且未重启时，判定为卡住
                if (isActive && !isRestarting && !isCameraClosed) {
                    Log.e(TAG, "摄像头" + cameraNum + "预览卡住（10秒无帧更新）");
                    callback.onToast("摄像头" + cameraNum + "卡住，正在重启...");
                    callback.onCameraStuck(cameraNum); // 新增卡住回调（需在接口中定义）
                    restartCamera(); // 执行重启逻辑
                }
            }
        };
    }

    // 初始化UI组件（新增：获取cameraNumTextView并保存原布局参数）
    @SuppressLint("SetTextI18n")
    public void initComponents(View cameraArea) {
        textureView = cameraArea.findViewById(R.id.texture_view);
        toggleButton = cameraArea.findViewById(R.id.toggle_btn);
        captureButton = cameraArea.findViewById(R.id.capture_btn);
        recordButton = cameraArea.findViewById(R.id.record_btn);
        stopButton = cameraArea.findViewById(R.id.stop_btn);
        // ---------------------- 新增：初始化摄像头编号文本 ----------------------
        cameraNumTextView = cameraArea.findViewById(R.id.camera_num);
        cameraNumTextView.setText(String.valueOf(cameraNum));
        // 保存原有TextureView布局参数（宽高、margin等）
        originalTextureParams = textureView.getLayoutParams();
        // ----------------------------------------------------------------------
        toggleButton.setText(context.getString(R.string.close_camera) + cameraNum);
        setupListeners();
    }

    // 设置组件监听器（新增：cameraNumTextView点击事件）
    private void setupListeners() {
        // 原有监听器：切换摄像头、拍照、录像...
        toggleButton.setOnClickListener(v -> toggleCamera());
        captureButton.setOnClickListener(v -> takePicture());
        recordButton.setOnClickListener(v -> startRecording());
        stopButton.setOnClickListener(v -> stopRecording());
        // ---------------------- 新增：摄像头编号点击触发全屏切换 ----------------------
        textureView.setOnClickListener(v -> {
            if (!isActive) {
                callback.onToast("请先打开摄像头");
                return;
            }
            toggleFullScreen(); // 切换全屏/正常状态
        });
        // --------------------------------------------------------------------------

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
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                resetPreviewTimeout(); // 有帧更新，重置超时计时
            }
        });


    }

    // ---------------------- 新增：全屏切换核心方法 ----------------------

    /**
     * 切换全屏/正常状态
     */
    private void toggleFullScreen() {
        if (!isFullScreen) {
            enterFullScreen(); // 进入全屏
        } else {
            exitFullScreen(); // 退出全屏
        }
    }


    /**
     * 退出全屏：恢复原布局+原预览会话
     */
    private void exitFullScreen() {
        isFullScreen = false;
        // 1. 关闭弹窗
        if (fullScreenDialog != null && fullScreenDialog.isShowing()) {
            fullScreenDialog.dismiss();
            fullScreenDialog = null;
        }

        // 2. 恢复原有TextureView布局和UI显示
        if (textureView != null && originalTextureParams != null) {
            textureView.setLayoutParams(originalTextureParams);
        }
        toggleButton.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);
        recordButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);

        // 3. 重建原预览会话
        if (isActive) {
            configureTransform(textureView.getWidth(), textureView.getHeight());
            recreatePreviewSession(false); // 重建预览（false=正常模式）
        }

        // 4. 释放全屏TextureView资源
        fullScreenTextureView = null;
    }

    // ---------------------- 新增：Surface 可用状态标记 ----------------------
    private boolean isFullScreenSurfaceReady = false; // 全屏TextureView的Surface是否可用
// ----------------------------------------------------------------------


    @SuppressLint("RtlHardcoded")
    /**
     * 创建全屏弹窗的内容视图（修复按钮遮挡问题）
     */
    private View createFullScreenView() {
        FrameLayout fullScreenContainer = new FrameLayout(context);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        fullScreenContainer.setLayoutParams(containerParams);
        fullScreenContainer.setBackgroundColor(context.getResources().getColor(android.R.color.black));

        // 1. 先添加全屏TextureView（作为底层）
        fullScreenTextureView = new TextureView(context);
        FrameLayout.LayoutParams textureParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        fullScreenTextureView.setLayoutParams(textureParams);

        // SurfaceTexture监听（原有逻辑保留）
        fullScreenTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (previewSize != null) {
                    surface.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                    isFullScreenSurfaceReady = true;
                    if (isRecording) {
                        recreateRecordingSession();
                    } else {
                        recreatePreviewSession(true);
                    }
                }
                configureFullScreenTransform();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                isFullScreenSurfaceReady = true;
//                configureFullScreenTransform();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                isFullScreenSurfaceReady = false;
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                resetPreviewTimeout(); // 全屏模式下同样检测帧更新
            }
        });
        // 先添加TextureView到容器（底层）
        fullScreenContainer.addView(fullScreenTextureView);

        // 2. 再添加关闭按钮（作为上层，覆盖在TextureView之上）
        TextView closeBtn = new TextView(context);
        FrameLayout.LayoutParams closeBtnParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        closeBtnParams.gravity = Gravity.TOP | Gravity.RIGHT;
        closeBtnParams.setMargins(0, 30, 30, 0); // 上右间距
        closeBtn.setText("×");
        closeBtn.setTextSize(24);
        closeBtn.setTextColor(context.getResources().getColor(android.R.color.white));
        closeBtn.setPadding(20, 10, 20, 10);
        closeBtn.setOnClickListener(v -> exitFullScreenWithVideo());

        // 后添加按钮到容器（上层）
        fullScreenContainer.addView(closeBtn);

        return fullScreenContainer;
    }
    // 新增：重置预览超时计时的方法
    private void resetPreviewTimeout() {
        if (!isActive || isRecording) return; // 录制时暂不检测（避免误判）
        timeoutHandler.removeCallbacks(previewTimeoutRunnable); // 移除旧超时任务
        timeoutHandler.postDelayed(previewTimeoutRunnable, PREVIEW_TIMEOUT); // 重新添加超时任务
    }

    /**
     * 重建全屏视频录制会话（专门用于视频播放场景）
     */
    private void recreateRecordingSession() {
        synchronized (cameraStateLock) {
            if (isCameraClosed || cameraDevice == null || !isRecording || fullScreenTextureView == null) {
                Log.w(TAG, "视频会话重建：状态异常（isRecording=" + isRecording + "）");
                return;
            }

            // 1. 停止原有录像会话
            if (captureSession != null) {
                try {
                    captureSession.stopRepeating();
                    captureSession.close();
                } catch (CameraAccessException e) {
                    Log.e(TAG, "停止原有录像会话失败", e);
                }
                captureSession = null;
            }

            try {
                // 2. 绑定全屏TextureView的Surface（视频输出目标）
                SurfaceTexture texture = fullScreenTextureView.getSurfaceTexture();
                if (texture == null) return;
                texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                Surface previewSurface = new Surface(texture);

                // 3. 重建MediaRecorder的Surface（视频录制输出）
                if (mediaRecorder == null) {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setOutputFile(fileUrl); // 复用原有录制文件路径
                    mediaRecorder.setVideoEncodingBitRate(10000000);
                    mediaRecorder.setVideoFrameRate(30);
                    mediaRecorder.setVideoSize(previewSize.getWidth(), previewSize.getHeight());
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setOrientationHint(ORIENTATIONS.get(callback.getDisplayRotation()));
                    mediaRecorder.prepare();
                    recorderSurface = mediaRecorder.getSurface();
                }

                // 4. 配置录像会话的目标Surface（预览+录制）
                CaptureRequest.Builder recorderBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                recorderBuilder.addTarget(previewSurface); // 全屏预览Surface
                recorderBuilder.addTarget(recorderSurface); // 视频录制Surface

                List<Surface> surfaces = Arrays.asList(previewSurface, recorderSurface);
                cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        captureSession = session;
                        try {
                            // 启动视频录制（继续之前的录制，避免中断）
                            session.setRepeatingRequest(recorderBuilder.build(), null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "启动全屏视频录制失败", e);
                            releaseMediaRecorder();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        callback.onToast("全屏视频配置失败");
                        releaseMediaRecorder();
                        if (isActive && !isRestarting) {
                            callback.onCameraStuck(cameraNum);
                            restartCamera();
                        }
                    }
                }, backgroundHandler);
            } catch (Exception e) {
                Log.e(TAG, "重建视频会话异常", e);
                releaseMediaRecorder();
            }
        }
    }

    /**
     * 带视频处理的退出全屏（点击关闭按钮触发）
     */
    private void exitFullScreenWithVideo() {
        synchronized (cameraStateLock) {
            // 1. 优先取消所有相机回调和请求（关键：防止释放后仍有回调）
            cancelAllCameraCallbacks();

            // 2. 停止录制（若正在录制）
            if (isRecording && mediaRecorder != null) {
                try {
                    mediaRecorder.stop();
                    callback.onVideoSaved(cameraNum, fileUrl);
                    callback.onToast("视频已保存");
                } catch (RuntimeException e) {
                    Log.e(TAG, "停止录制异常", e);
                    callback.onToast("视频保存失败");
                } finally {
                    releaseMediaRecorder();
                    isRecording = false;
                }
            }

            // 3. 关闭并置空相机会话（确保不再处理新请求）
            if (captureSession != null) {
                try {
                    // 先停止重复请求，再关闭会话
                    captureSession.stopRepeating();
                    captureSession.abortCaptures(); // 终止所有未完成的捕获请求
                    captureSession.close();
                } catch (CameraAccessException e) {
                    Log.e(TAG, "关闭相机会话异常", e);
                } finally {
                    captureSession = null; // 必须置空，避免后续访问
                }
            }

            // 4. 关闭弹窗
            if (fullScreenDialog != null && fullScreenDialog.isShowing()) {
                fullScreenDialog.dismiss();
                fullScreenDialog = null;
            }

            // 5. 恢复原布局和预览
            if (textureView != null && originalTextureParams != null) {
                textureView.setLayoutParams(originalTextureParams);
            }
            toggleButton.setVisibility(View.VISIBLE);
            captureButton.setVisibility(View.VISIBLE);
            recordButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);

            // 6. 重建原预览会话（仅在相机仍激活时）
            if (isActive && !isCameraClosed && cameraDevice != null) {
                configureTransform(textureView.getWidth(), textureView.getHeight());
                recreatePreviewSession(false);
            }

            // 7. 重置全屏状态
            isFullScreen = false;
            isFullScreenSurfaceReady = false;
            fullScreenTextureView = null;
            recorderSurface = null;
            updateUIState();
        }
    }

    /**
     * 新增：取消所有相机相关回调，避免释放后仍有事件处理
     */

    private void cancelAllCameraCallbacks() {
        // 1. 移除ImageReader的回调（防止拍照回调在资源释放后执行）
        if (imageReader != null) {
            imageReader.setOnImageAvailableListener(null, null);
        }

        // 2. 停止相机会话的重复请求（使用正确的stopRepeating()）
        if (captureSession != null) {
            try {
                // 正确方式：停止重复请求（替代setRepeatingRequest(null, ...)）
                captureSession.stopRepeating();
                // 额外：终止所有未完成的捕获请求（避免残留回调）
                captureSession.abortCaptures();
            } catch (CameraAccessException e) {
                Log.e(TAG, "取消相机请求异常", e);
            }
        }
    }


    // 进入全屏时，弹窗销毁监听修改
    private void enterFullScreen() {
        isFullScreen = true;
        // 隐藏原UI
        toggleButton.setVisibility(View.GONE);
        captureButton.setVisibility(View.GONE);
        recordButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

        // 创建弹窗（关键：禁用默认关闭方式）
        fullScreenDialog = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                .setCancelable(false) // 禁用：点击外部/返回键关闭弹窗
                .setView(createFullScreenView())
                .create();

        // 仅允许通过关闭按钮触发退出（移除原有dismiss监听，避免意外调用）
        fullScreenDialog.setOnDismissListener(null);

        // 显示弹窗
        fullScreenDialog.show();
    }

    private void recreatePreviewSession(boolean isFullScreenMode) {
        synchronized (cameraStateLock) {
            if (isCameraClosed || cameraDevice == null) return;

            // 1. 停止原有会话（原有逻辑保留）
            if (captureSession != null) {
                try {
                    captureSession.stopRepeating();
                    captureSession.close();
                } catch (CameraAccessException e) {
                    Log.e(TAG, "停止预览会话失败", e);
                }
                captureSession = null;
            }

            try {
                // ---------------------- 关键验证：获取当前模式的 TextureView ----------------------
                TextureView currentTextureView = isFullScreenMode ? fullScreenTextureView : textureView;
                if (currentTextureView == null) {
                    Log.e(TAG, "recreateSession：当前TextureView为空（isFullScreenMode=" + isFullScreenMode + "）");
                    return;
                }

                SurfaceTexture texture = currentTextureView.getSurfaceTexture();
                if (texture == null) {
                    Log.e(TAG, "recreateSession：SurfaceTexture未初始化");
                    return;
                }

                // 2. 再次确认缓冲大小（双重保障）
                if (previewSize != null) {
                    texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                } else {
                    Log.e(TAG, "recreateSession：previewSize为空，无法设置缓冲大小");
                    return;
                }

                // 3. 创建当前TextureView对应的Surface（必须是这个Surface！）
                Surface previewSurface = new Surface(texture);
                Log.d(TAG, "recreateSession：绑定Surface（宽度=" + previewSize.getWidth() + "，高度=" + previewSize.getHeight() + "）");

                // 4. 构建预览请求（确保目标是当前Surface）
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(previewSurface); // 关键：添加当前Surface

                // 5. 设置预览方向（原有逻辑保留）
                int rotation = callback.getDisplayRotation();
                int orientation = ORIENTATIONS.get(rotation);
                if (cameraNum == 3) {
                    orientation = 180;
                }
                previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation);

                // 6. 准备Surface列表（预览+拍照）
                List<Surface> surfaces = new ArrayList<>();
                surfaces.add(previewSurface); // 优先添加当前预览Surface
                if (imageReader != null) {
                    surfaces.add(imageReader.getSurface()); // 拍照Surface
                }

                // 7. 创建新会话（添加日志，排查创建失败原因）
                cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        captureSession = session;
                        Log.d(TAG, "recreateSession：会话创建成功（isFullScreenMode=" + isFullScreenMode + "）");
                        try {
                            // 启动预览（原有逻辑保留）
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                            session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "recreateSession：启动预览失败", e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        // 新增日志：排查会话创建失败原因（如Surface尺寸不支持）
                        Log.e(TAG, "recreateSession：会话配置失败（isFullScreenMode=" + isFullScreenMode + "），可能Surface尺寸不支持");
                        callback.onToast("摄像头" + cameraNum + "全屏预览配置失败");
                        if (isActive && !isRestarting) {
                            callback.onCameraStuck(cameraNum);
                            restartCamera();
                        }
                    }
                }, backgroundHandler);
            } catch (CameraAccessException e) {
                Log.e(TAG, "recreateSession：创建会话异常", e);
            }
        }
    }

    private void configureFullScreenTransform() {
        if (fullScreenTextureView == null || previewSize == null) return;


        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, fullScreenTextureView.getWidth(), fullScreenTextureView.getHeight());
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        int rotation = callback.getDisplayRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) fullScreenTextureView.getHeight() / previewSize.getHeight(),
                    (float) fullScreenTextureView.getWidth() / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        Logger.i("cameraNum" + cameraNum);
        if (cameraNum == 1) {
            matrix.postScale(-1, 1, centerX, centerY);
        }

        fullScreenTextureView.setTransform(matrix);
        Log.d(TAG, "configureFullScreen：预览尺寸（" + previewSize.getWidth() + "x" + previewSize.getHeight() + "），屏幕尺寸（" + viewRect.width() + "x" + viewRect.height() + "）");
    }

    // 原有方法：toggleCamera、openCamera、closeCamera等（仅修改closeCamera关闭弹窗）
    public void toggleCamera() {
        if (isActive) {
            closeCamera();
        } else {
            openCamera();
        }
    }

    int Message_NowLevel;
    public void takePictureFromExternal(int Message_NowLevel) {
        synchronized (cameraStateLock) {
            if (!isActive || isCameraClosed) {
                Log.w(TAG, "摄像头" + cameraNum + "未激活或已关闭，无法拍照");
                return;
            }
        }
        this.Message_NowLevel = Message_NowLevel;
        takePicture();
    }

    private void openCamera() {
        synchronized (cameraStateLock) {
            isCameraClosed = false;
        }

        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length < cameraNum) {
                callback.onToast("未检测到摄像头" + cameraNum);
                return;
            }
            cameraId = cameraIds[Math.min(cameraNum - 1, cameraIds.length - 1)];

            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                callback.onToast("摄像头" + cameraNum + "配置失败");
                return;
            }

            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), 1920, 1080);
            captureSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), 3840, 2160);
            imageReader = ImageReader.newInstance(captureSize.getWidth(), captureSize.getHeight(),
                    ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(this::onImageAvailable, backgroundHandler);

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
            if (cameraNum == 1) {
                configureTransform(textureView.getWidth(), textureView.getHeight());
            }

        } catch (CameraAccessException | InterruptedException e) {
            callback.onToast("打开摄像头" + cameraNum + "失败：" + e.getMessage());
            Log.e(TAG, "openCamera error", e);
        }
    }

    // 关闭摄像头（新增：关闭全屏弹窗）
    public void closeCamera() {
        boolean lockAcquired = false;
        try {
            if (cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                lockAcquired = true;
                synchronized (cameraStateLock) {
                    timeoutHandler.removeCallbacks(previewTimeoutRunnable);
                    if (isCameraClosed) {
                        return;
                    }
                    // 新增：取消所有相机请求（防止残留回调）
                    cancelAllCameraCallbacks();


                    // ---------------------- 新增：关闭全屏弹窗 ----------------------
                    if (fullScreenDialog != null && fullScreenDialog.isShowing()) {
                        fullScreenDialog.dismiss();
                    }
                    // ----------------------------------------------------------------

                    if (isRecording) {
                        stopRecording();
                    }

                    if (captureSession != null) {
                        try {
                            captureSession.stopRepeating();
                            captureSession.abortCaptures();
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "关闭时停止请求失败", e);
                        }
                    }

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
                    releaseMediaRecorder();

                    isCameraClosed = true;
                    isActive = false;
                    toggleButton.setChecked(false);
                    updateUIState();
                }
            } else {
                Log.w(TAG, "获取相机锁超时，无法安全关闭摄像头" + cameraNum);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "closeCamera interrupted", e);
        } finally {
            if (lockAcquired) {
                cameraOpenCloseLock.release();
            }
        }
    }

    // 以下为原有方法：stateCallback、createPreviewSession、takePicture等（无修改）
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice = camera;
            isActive = true;
            ((Activity) context).runOnUiThread(() -> toggleButton.setChecked(true));
            createPreviewSession();
            updateUIState();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            synchronized (cameraStateLock) {
                camera.close();
                cameraDevice = null;
                isCameraClosed = true;
                isActive = false;
            }
            ((Activity) context).runOnUiThread(() -> toggleButton.setChecked(false));
            updateUIState();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice = null;
            cameraOpenCloseLock.release();
            synchronized (cameraStateLock) {
                camera.close();
                isCameraClosed = true;
                isActive = false;
            }
            toggleButton.setChecked(false);
            callback.onToast("摄像头" + cameraNum + "错误：" + error);
            Logger.e(TAG, "CameraDevice error: " + error);
            updateUIState();
        }
    };

    private void createPreviewSession() {
        synchronized (cameraStateLock) {
            if (isCameraClosed || cameraDevice == null) {
                Log.w(TAG, "相机已关闭，跳过预览会话创建");
                return;
            }

            try {
                SurfaceTexture texture = textureView.getSurfaceTexture();
                if (texture == null) return;

                texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                Surface surface = new Surface(texture);

                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(surface);

                int rotation = callback.getDisplayRotation();
                int orientation = ORIENTATIONS.get(rotation);
                if (cameraNum == 3) {
                    orientation = 180;
                }
                previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation);

                List<Surface> surfaces = new ArrayList<>();
                surfaces.add(surface);
                surfaces.add(imageReader.getSurface());

                cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        synchronized (cameraStateLock) {
                            if (isCameraClosed || cameraDevice == null) {
                                session.close();
                                return;
                            }
                            captureSession = session;
                        }

                        try {
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                            session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "createPreviewSession error", e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        callback.onToast("摄像头" + cameraNum + "预览配置失败");
                        if (isActive && !isRestarting) {
                            callback.onCameraStuck(cameraNum);
                            restartCamera();
                        }
                    }
                }, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "createPreviewSession error", e);
            }
        }

    }

    public void takePicture() {
        synchronized (cameraStateLock) {
            if (!isActive || isCameraClosed || cameraDevice == null || captureSession == null) {
                Log.w(TAG, "摄像头" + cameraNum + "状态异常，无法拍照");
                return;
            }
        }

        try {
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            int rotation = callback.getDisplayRotation();
            int orientation = ORIENTATIONS.get(rotation);
            if (cameraNum == 3) {
                orientation = 180;
            }
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation);

            captureSession.stopRepeating();
            captureSession.capture(captureBuilder.build(), captureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            callback.onToast("摄像头" + cameraNum + "拍照失败");
            Log.e(TAG, "takePicture error", e);
        }
    }

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            synchronized (cameraStateLock) {
                if (isCameraClosed || cameraDevice == null) {
                    Log.w(TAG, "相机已关闭，跳过预览恢复");
                    return;
                }
                if (captureSession != session) {
                    Log.w(TAG, "预览会话已变更，跳过预览恢复");
                    return;
                }
            }

            try {
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

    private void onImageAvailable(ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            if (image != null) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                saveImageToFile(bytes,Message_NowLevel);
            }
        } finally {
            if (image != null) image.close();
        }
    }

    private void saveImageToFile(byte[] bytes, int currentFloor) {

        new Thread(()->{
            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            String abs = FileUtil.saveBitmapToTempFile(bitmap, "photo_cam" + cameraNum + "_level_" + currentFloor + "_");

            callback.onImageSaved(cameraNum, abs, currentFloor);
            callback.onToast("摄像头" + cameraNum + "照片已保存");
        }).start();
    }

    public void startRecording() {
        synchronized (cameraStateLock) {
            if (!isActive || isRecording || isCameraClosed || cameraDevice == null) {
                Log.w(TAG, "摄像头" + cameraNum + "状态异常，无法录像");
                return;
            }

            try {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                File videoFile = new File(Constants.IMAGE_FILE,
                        "video" + cameraNum + "_" + System.currentTimeMillis() + ".mp4");
                fileUrl = videoFile.getAbsolutePath();
                mediaRecorder.setOutputFile(fileUrl);

                mediaRecorder.setVideoEncodingBitRate(10000000);
                mediaRecorder.setVideoFrameRate(30);
                mediaRecorder.setVideoSize(previewSize.getWidth(), previewSize.getHeight());
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                mediaRecorder.setOrientationHint(ORIENTATIONS.get(callback.getDisplayRotation()));
                mediaRecorder.prepare();

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
                        synchronized (cameraStateLock) {
                            if (isCameraClosed || cameraDevice == null || !isRecording) {
                                session.close();
                                releaseMediaRecorder();
                                return;
                            }
                            captureSession = session;
                        }

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
                        if (isActive && !isRestarting) {
                            callback.onCameraStuck(cameraNum);
                            restartCamera();
                        }
                    }
                }, backgroundHandler);
            } catch (Exception e) {
                callback.onToast("摄像头" + cameraNum + "录像失败");
                Log.e(TAG, "startRecording error", e);
                releaseMediaRecorder();
            }
        }

    }
    // 摄像头重启核心方法
    private void restartCamera() {
        synchronized (cameraStateLock) {
            if (isRestarting || !isActive) return;
            isRestarting = true; // 标记为重启中，避免重复触发
        }

        // 步骤1：立即关闭摄像头（复用现有closeCamera方法）
        closeCamera();

        // 步骤2：延迟3秒后重新打开摄像头
        timeoutHandler.postDelayed(() -> {
            synchronized (cameraStateLock) {
                isRestarting = false; // 重置重启标记
            }
            openCamera(); // 重新打开摄像头
        }, 3000); // 3秒延迟
    }
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
            synchronized (cameraStateLock) {
                if (!isCameraClosed && isActive) {
                    createPreviewSession();
                }
            }
            updateUIState();
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        recorderSurface = null;
    }

    public void configureTransform(int viewWidth, int viewHeight) {
        synchronized (cameraStateLock) {
            if (isCameraClosed || textureView == null || previewSize == null) {
                return;
            }
        }

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
        Logger.i("cameraNum" + cameraNum);
        if (cameraNum == 1) {
            matrix.postScale(-1, 1, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private void updateUIState() {
        ((Activity) context).runOnUiThread(() -> {
            toggleButton.setText(isActive ? context.getString(R.string.close_camera) + cameraNum : context.getString(R.string.open_camera) + cameraNum);
            toggleButton.setChecked(isActive);
            captureButton.setEnabled(isActive && !isRecording && !isCameraClosed);
            recordButton.setEnabled(isActive && !isRecording && !isCameraClosed);
            stopButton.setEnabled(isActive && isRecording && !isCameraClosed);
        });
    }

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

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public int getCameraNum() {
        return cameraNum;
    }
}