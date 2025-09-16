package leesche.smartrecycling.base.strategy;

import android.content.Context;
import android.view.Gravity;

/**
 * 默认设备策略
 * Created by samba on
 */
public class DefaultStrategy implements DevStrategy {
    private Context mContext;

    public DefaultStrategy(Context context) {
        this.mContext = context;
    }

    @Override
    public String getLabel() {
        return DevContext.DEFAULT_LABEL;
    }

    @Override
    public String getMode() {
        return DevContext.DEFAULT_MODE;
    }

    @Override
    public void showDownSystem() {
//        ToastUtil.showErrorMsg("当前设备无法支持该功能", Gravity.CENTER);
    }

    @Override
    public void reBootSystem() {
//        ToastUtil.showErrorMsg("当前设备无法支持该功能", Gravity.CENTER);
    }

    @Override
    public void setFontSize(FontSize fontSize) {
//        ToastUtil.showErrorMsg("当前设备无法支持设置字体大小，请到系统设置页面操作", Gravity.CENTER);
    }

    @Override
    public void setCameraOrientation(CameraOrientation cameraOrientation) {
//        ToastUtil.showErrorMsg("当前设备无法支持设置摄像头旋转，请到系统设置页面操作", Gravity.CENTER);

    }

    @Override
    public void setDpi(Dpi dpi) {
//        ToastUtil.showErrorMsg("当前设备无法支持设置屏幕密度，请到系统设置页面操作", Gravity.CENTER);

    }

    @Override
    public void setScreenOrientation(ScreenOrientation screenOrientation) {
//        ToastUtil.showErrorMsg("当前设备无法支持设置屏幕旋转，请到系统设置页面操作", Gravity.CENTER);

    }

    @Override
    public void setBarState(BarState barState) {
//        Logger.e("当前设备无法支持设置状态栏状态，请到系统设置页面操作");
    }

    @Override
    public int exeRootCmdSilent(String cmd) {
        return -1;
    }

    @Override
    public void silentInstallApk(String apkPath) {

    }

    @Override
    public int getDisplayOrientation() {
        return 90;
    }

}
