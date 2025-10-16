package com.ycmachine.smartdevice.activity.cameraTest;

import android.Manifest;
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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ycmachine.smartdevice.R;

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

public class TripleCameraActivityDemo extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

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
    private Button mirrorButton1;
    private CameraDevice cameraDevice1;
    private CameraCaptureSession cameraCaptureSession1;
    private CaptureRequest.Builder previewRequestBuilder1;
    private Size previewSize1;
    private Size captureSize1;
    private ImageReader imageReader1;
    private String cameraId1;
    private boolean isCamera1Active = false;
    private boolean isMirror1 = false;

    // 摄像头2组件
    private TextureView textureView2;
    private Button toggleButton2;
    private Button captureButton2;
    private Button mirrorButton2;
    private CameraDevice cameraDevice2;
    private CameraCaptureSession cameraCaptureSession2;
    private CaptureRequest.Builder previewRequestBuilder2;
    private Size previewSize2;
    private Size captureSize2;
    private ImageReader imageReader2;
    private String cameraId2;
    private boolean isCamera2Active = false;
    private boolean isMirror2 = false;

    // 摄像头3(录像)组件
    private TextureView textureView3;
    private Button toggleButton3;
    private Button recordButton;
    private Button stopButton;
    private Button mirrorButton3;
    private CameraDevice cameraDevice3;
    private CameraCaptureSession cameraCaptureSession3;
    private CaptureRequest.Builder previewRequestBuilder3;
    private Size previewSize3;
    private String cameraId3;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private boolean isCamera3Active = false;
    private boolean isMirror3 = false;
    private Surface recorderSurface;

    // 共享资源
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Semaphore cameraOpenCloseLock = new Semaphore(1);
    private CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triple_camera_demo);

        // 初始化UI组件
        textureView1 = findViewById(R.id.texture_view1);
        textureView2 = findViewById(R.id.texture_view2);
        textureView3 = findViewById(R.id.texture_view3);

        toggleButton1 = findViewById(R.id.toggle_button1);
        toggleButton2 = findViewById(R.id.toggle_button2);
        toggleButton3 = findViewById(R.id.toggle_button3);

        captureButton1 = findViewById(R.id.capture_button1);
        captureButton2 = findViewById(R.id.capture_button2);
        recordButton = findViewById(R.id.record_button);
        stopButton = findViewById(R.id.stop_button);

        mirrorButton1 = findViewById(R.id.mirror_button1);
        mirrorButton2 = findViewById(R.id.mirror_button2);
        mirrorButton3 = findViewById(R.id.mirror_button3);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // 设置按钮监听器
        toggleButton1.setOnClickListener(v -> toggleCamera(1));
        toggleButton2.setOnClickListener(v -> toggleCamera(2));
        toggleButton3.setOnClickListener(v -> toggleCamera(3));

        captureButton1.setOnClickListener(v -> takePicture(1));
        captureButton2.setOnClickListener(v -> takePicture(2));
        recordButton.setOnClickListener(v -> startRecording());
        stopButton.setOnClickListener(v -> stopRecording());

        mirrorButton1.setOnClickListener(v -> toggleMirror(1));
        mirrorButton2.setOnClickListener(v -> toggleMirror(2));
        mirrorButton3.setOnClickListener(v -> toggleMirror(3));

        // 初始状态
        captureButton1.setEnabled(false);
        captureButton2.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();

        // 设置TextureView监听器
        textureView1.setSurfaceTextureListener(textureListener1);
        textureView2.setSurfaceTextureListener(textureListener2);
        textureView3.setSurfaceTextureListener(textureListener3);
    }

    @Override
    protected void onPause() {
        closeAllCameras();
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleMirror(int cameraNum) {
        switch (cameraNum) {
            case 1:
                isMirror1 = !isMirror1;
                mirrorButton1.setText(isMirror1 ? "取消镜像1" : "镜像1");
                break;
            case 2:
                isMirror2 = !isMirror2;
                mirrorButton2.setText(isMirror2 ? "取消镜像2" : "镜像2");
                break;
            case 3:
                isMirror3 = !isMirror3;
                mirrorButton3.setText(isMirror3 ? "取消镜像3" : "镜像3");
                break;
        }
        updateTextureViewTransform(cameraNum);
    }

    private void updateTextureViewTransform(int cameraNum) {
        TextureView textureView = null;
        Size previewSize = null;
        boolean isMirror = false;

        switch (cameraNum) {
            case 1:
                textureView = textureView1;
                previewSize = previewSize1;
                isMirror = isMirror1;
                break;
            case 2:
                textureView = textureView2;
                previewSize = previewSize2;
                isMirror = isMirror2;
                break;
            case 3:
                textureView = textureView3;
                previewSize = previewSize3;
                isMirror = isMirror3;
                break;
        }

        if (textureView != null && previewSize != null) {
            configureTransform(textureView, textureView.getWidth(), textureView.getHeight(), previewSize, isMirror);
        }
    }

    private void configureTransform(TextureView textureView, int viewWidth, int viewHeight, Size previewSize, boolean mirror) {
        if (textureView == null || previewSize == null) {
            return;
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }

        if (mirror) {
            matrix.postScale(-1, 1, centerX, centerY);
        }

        textureView.setTransform(matrix);
    }

    private void toggleCamera(int cameraNum) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            if (cameraIdList.length < 3) {
                Toast.makeText(this, "未检测到足够的USB摄像头(需要3个)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cameraNum == 1) {
                cameraId1 = cameraIdList[0];
            } else if (cameraNum == 2) {
                cameraId2 = cameraIdList[1];
            } else {
                cameraId3 = cameraIdList[2];
            }

            if (cameraNum == 1) {
                if (isCamera1Active) {
                    closeCamera(1);
                    toggleButton1.setText("开启摄像头1");
                    captureButton1.setEnabled(false);
                    mirrorButton1.setEnabled(false);
                } else {
                    openCamera(1);
                    toggleButton1.setText("关闭摄像头1");
                    captureButton1.setEnabled(true);
                    mirrorButton1.setEnabled(true);
                }
                isCamera1Active = !isCamera1Active;
            } else if (cameraNum == 2) {
                if (isCamera2Active) {
                    closeCamera(2);
                    toggleButton2.setText("开启摄像头2");
                    captureButton2.setEnabled(false);
                    mirrorButton2.setEnabled(false);
                } else {
                    openCamera(2);
                    toggleButton2.setText("关闭摄像头2");
                    captureButton2.setEnabled(true);
                    mirrorButton2.setEnabled(true);
                }
                isCamera2Active = !isCamera2Active;
            } else {
                if (isCamera3Active) {
                    closeCamera(3);
                    toggleButton3.setText("开启摄像头3");
                    recordButton.setEnabled(false);
                    stopButton.setEnabled(false);
                    mirrorButton3.setEnabled(false);
                } else {
                    openCamera(3);
                    toggleButton3.setText("关闭摄像头3");
                    recordButton.setEnabled(true);
                    mirrorButton3.setEnabled(true);
                }
                isCamera3Active = !isCamera3Active;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "访问摄像头失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera(int cameraNum) {
        try {
            String cameraId = cameraNum == 1 ? cameraId1 : (cameraNum == 2 ? cameraId2 : cameraId3);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

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
            }

            if (cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraManager.openCamera(cameraId, getStateCallback(cameraNum), backgroundHandler);
            } else {
                throw new RuntimeException("等待打开摄像头超时");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "打开摄像头" + cameraNum + "失败", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            throw new RuntimeException("打开摄像头时被中断", e);
        }
    }

    private void closeCamera(int cameraNum) {
        try {
            cameraOpenCloseLock.acquire();

            if (cameraNum == 1) {
                if (cameraCaptureSession1 != null) {
                    cameraCaptureSession1.close();
                    cameraCaptureSession1 = null;
                }
                if (cameraDevice1 != null) {
                    cameraDevice1.close();
                    cameraDevice1 = null;
                }
                if (imageReader1 != null) {
                    imageReader1.close();
                    imageReader1 = null;
                }
            } else if (cameraNum == 2) {
                if (cameraCaptureSession2 != null) {
                    cameraCaptureSession2.close();
                    cameraCaptureSession2 = null;
                }
                if (cameraDevice2 != null) {
                    cameraDevice2.close();
                    cameraDevice2 = null;
                }
                if (imageReader2 != null) {
                    imageReader2.close();
                    imageReader2 = null;
                }
            } else {
                if (isRecording) {
                    stopRecording();
                }

                if (cameraCaptureSession3 != null) {
                    cameraCaptureSession3.close();
                    cameraCaptureSession3 = null;
                }
                if (cameraDevice3 != null) {
                    cameraDevice3.close();
                    cameraDevice3 = null;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("关闭摄像头时被中断", e);
        } catch (Exception e) {
            Log.e("Camera", "关闭摄像头错误", e);
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    private void closeAllCameras() {
        if (isCamera1Active) closeCamera(1);
        if (isCamera2Active) closeCamera(2);
        if (isCamera3Active) closeCamera(3);
    }

    private final TextureView.SurfaceTextureListener textureListener1 = createTextureListener(1);
    private final TextureView.SurfaceTextureListener textureListener2 = createTextureListener(2);
    private final TextureView.SurfaceTextureListener textureListener3 = createTextureListener(3);

    private TextureView.SurfaceTextureListener createTextureListener(final int cameraNum) {
        return new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if ((cameraNum == 1 && isCamera1Active) ||
                        (cameraNum == 2 && isCamera2Active) ||
                        (cameraNum == 3 && isCamera3Active)) {

                    configureTransform(
                            cameraNum == 1 ? textureView1 : (cameraNum == 2 ? textureView2 : textureView3),
                            width, height,
                            cameraNum == 1 ? previewSize1 : (cameraNum == 2 ? previewSize2 : previewSize3),
                            cameraNum == 1 ? isMirror1 : (cameraNum == 2 ? isMirror2 : isMirror3)
                    );
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Size previewSize = cameraNum == 1 ? previewSize1 : (cameraNum == 2 ? previewSize2 : previewSize3);
                configureTransform(
                        cameraNum == 1 ? textureView1 : (cameraNum == 2 ? textureView2 : textureView3),
                        width, height, previewSize,
                        cameraNum == 1 ? isMirror1 : (cameraNum == 2 ? isMirror2 : isMirror3)
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

    private CameraDevice.StateCallback getStateCallback(final int cameraNum) {
        return new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraOpenCloseLock.release();
                if (cameraNum == 1) {
                    cameraDevice1 = camera;
                    createCameraPreviewSession(cameraDevice1, cameraNum);
                } else if (cameraNum == 2) {
                    cameraDevice2 = camera;
                    createCameraPreviewSession(cameraDevice2, cameraNum);
                } else {
                    cameraDevice3 = camera;
                    createCameraPreviewSession(cameraDevice3, cameraNum);
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                cameraOpenCloseLock.release();
                camera.close();
                if (cameraNum == 1) {
                    cameraDevice1 = null;
                    isCamera1Active = false;
                    runOnUiThread(() -> {
                        toggleButton1.setText("开启摄像头1");
                        captureButton1.setEnabled(false);
                        mirrorButton1.setEnabled(false);
                    });
                } else if (cameraNum == 2) {
                    cameraDevice2 = null;
                    isCamera2Active = false;
                    runOnUiThread(() -> {
                        toggleButton2.setText("开启摄像头2");
                        captureButton2.setEnabled(false);
                        mirrorButton2.setEnabled(false);
                    });
                } else {
                    cameraDevice3 = null;
                    isCamera3Active = false;
                    runOnUiThread(() -> {
                        toggleButton3.setText("开启摄像头3");
                        recordButton.setEnabled(false);
                        stopButton.setEnabled(false);
                        mirrorButton3.setEnabled(false);
                    });
                }
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                cameraOpenCloseLock.release();
                camera.close();
                if (cameraNum == 1) {
                    cameraDevice1 = null;
                    isCamera1Active = false;
                    runOnUiThread(() -> {
                        toggleButton1.setText("开启摄像头1");
                        captureButton1.setEnabled(false);
                        mirrorButton1.setEnabled(false);
                        Toast.makeText(TripleCameraActivityDemo.this, "摄像头1错误: " + error, Toast.LENGTH_SHORT).show();
                    });
                } else if (cameraNum == 2) {
                    cameraDevice2 = null;
                    isCamera2Active = false;
                    runOnUiThread(() -> {
                        toggleButton2.setText("开启摄像头2");
                        captureButton2.setEnabled(false);
                        mirrorButton2.setEnabled(false);
                        Toast.makeText(TripleCameraActivityDemo.this, "摄像头2错误: " + error, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    cameraDevice3 = null;
                    isCamera3Active = false;
                    runOnUiThread(() -> {
                        toggleButton3.setText("开启摄像头3");
                        recordButton.setEnabled(false);
                        stopButton.setEnabled(false);
                        mirrorButton3.setEnabled(false);
                        Toast.makeText(TripleCameraActivityDemo.this, "摄像头3错误: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        };
    }

    private void createCameraPreviewSession(CameraDevice cameraDevice, int cameraNum) {
        try {
            TextureView textureView = cameraNum == 1 ? textureView1 : (cameraNum == 2 ? textureView2 : textureView3);
            Size previewSize = cameraNum == 1 ? previewSize1 : (cameraNum == 2 ? previewSize2 : previewSize3);
            ImageReader imageReader = cameraNum == 1 ? imageReader1 : (cameraNum == 2 ? imageReader2 : null);

            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) {
                return;
            }

            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);

            final CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            if (cameraNum == 1) {
                previewRequestBuilder1 = builder;
            } else if (cameraNum == 2) {
                previewRequestBuilder2 = builder;
            } else {
                previewRequestBuilder3 = builder;
            }

            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(surface);

            if (cameraNum == 1 || cameraNum == 2) {
                surfaces.add(imageReader.getSurface());
            }

            cameraDevice.createCaptureSession(surfaces,
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (cameraDevice == null) {
                                return;
                            }

                            if (cameraNum == 1) {
                                cameraCaptureSession1 = session;
                            } else if (cameraNum == 2) {
                                cameraCaptureSession2 = session;
                            } else {
                                cameraCaptureSession3 = session;
                            }

                            try {
                                builder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                builder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                CaptureRequest previewRequest = builder.build();
                                if (cameraNum == 1) {
                                    cameraCaptureSession1.setRepeatingRequest(previewRequest, null, backgroundHandler);
                                } else if (cameraNum == 2) {
                                    cameraCaptureSession2.setRepeatingRequest(previewRequest, null, backgroundHandler);
                                } else {
                                    cameraCaptureSession3.setRepeatingRequest(previewRequest, null, backgroundHandler);
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(TripleCameraActivityDemo.this, "摄像头" + cameraNum + "配置失败", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void takePicture(int cameraNum) {
        if ((cameraNum == 1 && !isCamera1Active) || (cameraNum == 2 && !isCamera2Active)) {
            return;
        }

        CameraDevice cameraDevice = cameraNum == 1 ? cameraDevice1 : cameraDevice2;
        CameraCaptureSession cameraCaptureSession = cameraNum == 1 ? cameraCaptureSession1 : cameraCaptureSession2;
        CaptureRequest.Builder previewRequestBuilder = cameraNum == 1 ? previewRequestBuilder1 : previewRequestBuilder2;
        ImageReader imageReader = cameraNum == 1 ? imageReader1 : imageReader2;

        if (cameraDevice == null) {
            return;
        }

        try {
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation);

            if ((cameraNum == 1 && isMirror1) || (cameraNum == 2 && isMirror2)) {
                orientation = (orientation + 180) % 360;
            }

            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation);

            cameraCaptureSession.stopRepeating();
            cameraCaptureSession.capture(captureBuilder.build(), getCaptureCallback(cameraNum, previewRequestBuilder), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback getCaptureCallback(final int cameraNum, final CaptureRequest.Builder previewRequestBuilder) {
        return new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                           @NonNull CaptureRequest request,
                                           @NonNull TotalCaptureResult result) {
                try {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                    CaptureRequest previewRequest = previewRequestBuilder.build();
                    if (cameraNum == 1) {
                        cameraCaptureSession1.setRepeatingRequest(previewRequest, null, backgroundHandler);
                    } else {
                        cameraCaptureSession2.setRepeatingRequest(previewRequest, null, backgroundHandler);
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

        // 如果需要镜像，处理图片
        if ((cameraNum == 1 && isMirror1) || (cameraNum == 2 && isMirror2)) {
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1); // 水平镜像
            Bitmap mirroredBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            bitmap = mirroredBitmap;
        }

        savePhoto(bitmap, cameraNum);
    }

    private void savePhoto(Bitmap photo, int cameraNum) {
        try {
            File file = new File(getExternalFilesDir(null), "photo_cam" + cameraNum + "_" + System.currentTimeMillis() + ".jpg");
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                if (!created) {
                    Log.e("savePhoto", "创建目录失败: " + parent.getAbsolutePath());
                    return;
                }
            }

            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "摄像头" + cameraNum + "照片已保存: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "摄像头" + cameraNum + "保存照片失败", Toast.LENGTH_SHORT).show();
            Log.e("savePhoto", e.getMessage());
        }
    }

    private void startRecording() {
        if (isRecording || !isCamera3Active || cameraDevice3 == null) {
            return;
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            File videoFile = new File(getExternalFilesDir(null), "video_" + System.currentTimeMillis() + ".mp4");
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());

            mediaRecorder.setVideoEncodingBitRate(10000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(previewSize3.getWidth(), previewSize3.getHeight());
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = 90;
                    break;
                case Surface.ROTATION_90:
                    orientation = 0;
                    break;
                case Surface.ROTATION_180:
                    orientation = 270;
                    break;
                case Surface.ROTATION_270:
                    orientation = 180;
                    break;
            }

            if (isMirror3) {
                orientation = (orientation + 180) % 360;
            }

            mediaRecorder.setOrientationHint(orientation);

            mediaRecorder.prepare();

            recorderSurface = mediaRecorder.getSurface();

            SurfaceTexture texture = textureView3.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize3.getWidth(), previewSize3.getHeight());
            Surface previewSurface = new Surface(texture);

            final CaptureRequest.Builder recorderBuilder = cameraDevice3.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            recorderBuilder.addTarget(previewSurface);
            recorderBuilder.addTarget(recorderSurface);

            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(previewSurface);
            surfaces.add(recorderSurface);

            cameraDevice3.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession3 = session;
                    try {
                        mediaRecorder.start();
                        isRecording = true;

                        runOnUiThread(() -> {
                            recordButton.setEnabled(false);
                            stopButton.setEnabled(true);
                        });

                        cameraCaptureSession3.setRepeatingRequest(recorderBuilder.build(), null, backgroundHandler);
                    } catch (Exception e) {
                        Log.e("Recording", "开始录制失败", e);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(TripleCameraActivityDemo.this, "配置录制会话失败", Toast.LENGTH_SHORT).show();
                }
            }, backgroundHandler);

        } catch (Exception e) {
            Log.e("Recording", "开始录制失败", e);
            if (mediaRecorder != null) {
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }

    private void stopRecording() {
        if (!isRecording) {
            return;
        }

        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;

            isRecording = false;

            runOnUiThread(() -> {
                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
            });

            if (cameraDevice3 != null && isCamera3Active) {
                SurfaceTexture texture = textureView3.getSurfaceTexture();
                texture.setDefaultBufferSize(previewSize3.getWidth(), previewSize3.getHeight());
                Surface surface = new Surface(texture);

                previewRequestBuilder3 = cameraDevice3.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder3.addTarget(surface);

                cameraDevice3.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        cameraCaptureSession3 = session;
                        try {
                            previewRequestBuilder3.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            previewRequestBuilder3.set(CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                            CaptureRequest previewRequest = previewRequestBuilder3.build();
                            cameraCaptureSession3.setRepeatingRequest(previewRequest, null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Toast.makeText(TripleCameraActivityDemo.this, "恢复预览失败", Toast.LENGTH_SHORT).show();
                    }
                }, backgroundHandler);
            }

        } catch (Exception e) {
            Log.e("Recording", "停止录制失败", e);
        }
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

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "相机权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要相机权限才能使用此功能", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}