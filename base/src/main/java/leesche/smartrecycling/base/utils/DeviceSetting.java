package leesche.smartrecycling.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.File;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.strategy.DevContext;
import leesche.smartrecycling.base.strategy.DevStrategy;

public class DeviceSetting {

    @SuppressLint("StaticFieldLeak")
    public static Context context = DevContext.getInstance().getContext();

    /**
     * 重启系统
     */
    public static void rebootSys() {
        DevContext.getInstance().getDevStrategy().reBootSystem();
    }

    /**
     * 关闭系统
     */
    public static void showDownSystem() {
        DevContext.getInstance().getDevStrategy().showDownSystem();
    }

    /**
     * 设置系统字体大小
     *
     * @param fontSize 定义在枚举FontSize
     */
    public static void setFontSize(DevStrategy.FontSize fontSize) {
        DevContext.getInstance().getDevStrategy().setFontSize(fontSize);
    }

    /**
     * 设置摄像头旋转方向
     *
     * @param cameraOrientation 定义在枚举CameraOrientation
     */
    public static void setCameraOrientation(DevStrategy.CameraOrientation cameraOrientation) {
        DevContext.getInstance().getDevStrategy().setCameraOrientation(cameraOrientation);
    }

    /**
     * 设置屏幕密度
     *
     * @param dpi 定义在枚举DevStrategy
     */
    public static void setDpi(DevStrategy.Dpi dpi) {
        DevContext.getInstance().getDevStrategy().setDpi(dpi);
    }

    /**
     * 设置屏幕方向
     *
     * @param screenOrientation 定义在枚举ScreenOrientation
     */
    public static void setScreenOrientation(DevStrategy.ScreenOrientation screenOrientation) {
        DevContext.getInstance().getDevStrategy().setScreenOrientation(screenOrientation);
    }

    /**
     * 设置状态栏状态
     *
     * @param barState 定义在枚举BarState
     */
    public static void setBarState(DevStrategy.BarState barState) {
        DevContext.getInstance().getDevStrategy().setBarState(barState);
    }

    /**
     * 进入系统中的wifi网络设置界面,并且显示状态栏用户返回
     */
    public static void openSettingWifiAndShowBar() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        setBarState(DevStrategy.BarState.BAR_STATE_SHOW);
    }

    /**
     * 打开系统自带浏览器，并且显示状态栏
     */
    public static void openBrowserAndShowBar(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        setBarState(DevStrategy.BarState.BAR_STATE_SHOW);
    }

    /**
     * 检测是否可回退版本
     */
    public static boolean hasRollVersion() {
        return FileUtil.judeFileExists(new File(Constants.APK_CACHE_DIR));
    }

    /**
     * 检测是否有外挂设备
     *
     * @return
     */
    public static boolean hasMount() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static void openUSBFlashDisk() {
        AppUtil.openAppToPackgeName("com.estrongs.android.pop");
        setBarState(DevStrategy.BarState.BAR_STATE_SHOW);
    }

    public static void openCamere() {
        AppUtil.openAppToPackgeName("com.android.camera2");
        setBarState(DevStrategy.BarState.BAR_STATE_SHOW);
    }

    public static int getDisplayOrientation() {
        return DevContext.getInstance().getDevStrategy().getDisplayOrientation();
    }
}
