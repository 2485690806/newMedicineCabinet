////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package leesche.smartrecycling.base.utils;
//
//import android.app.Activity;
//
//import com.serenegiant.usbcameracommon.UvcCameraDataCallBack;
//import com.serenegiant.widget.CameraViewInterface;
//
//public class UVCCameraHandler extends AbstractUVCCameraHandler {
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int width, int height) {
//        return createHandler(parent, cameraView, 1, width, height, 1, 1.0F);
//    }
//
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int width, int height, float bandwidthFactor, UvcCameraDataCallBack uvcCameraDataCallBack) {
//        return createHandler(parent, cameraView, 1, width, height, 1, bandwidthFactor, uvcCameraDataCallBack);
//    }
//
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int width, int height, float bandwidthFactor) {
//        return createHandler(parent, cameraView, 1, width, height, 1, bandwidthFactor);
//    }
//
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int encoderType, int width, int height) {
//        return createHandler(parent, cameraView, encoderType, width, height, 1, 1.0F);
//    }
//
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int encoderType, int width, int height, int format) {
//        return createHandler(parent, cameraView, encoderType, width, height, format, 1.0F);
//    }
//
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int encoderType, int width, int height, int format, float bandwidthFactor) {
//        AbstractUVCCameraHandler.CameraThread thread = new AbstractUVCCameraHandler.CameraThread(UVCCameraHandler.class, parent, cameraView, encoderType, width, height, format, bandwidthFactor);
//        thread.start();
//        return (UVCCameraHandler)thread.getHandler();
//    }
//
//    public static final UVCCameraHandler createHandler(Activity parent, CameraViewInterface cameraView, int encoderType, int width, int height, int format, float bandwidthFactor, UvcCameraDataCallBack uvcCameraDataCallBack) {
//        AbstractUVCCameraHandler.CameraThread thread = new AbstractUVCCameraHandler.CameraThread(UVCCameraHandler.class, parent, cameraView, encoderType, width, height, format, bandwidthFactor);
//        thread.start();
//        thread.setCameraDataCallBack(uvcCameraDataCallBack);
//        return (UVCCameraHandler)thread.getHandler();
//    }
//
//    protected UVCCameraHandler(AbstractUVCCameraHandler.CameraThread thread) {
//        super(thread);
//    }
//
//    public void startPreview(Object surface) {
//        super.startPreview(surface);
//    }
//
//    public void captureStill() {
//        super.captureStill();
//    }
//
//    public void captureStill(String path) {
//        super.captureStill(path);
//    }
//}
