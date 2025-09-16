package leesche.smartrecycling.base.strategy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;

import com.blankj.utilcode.util.ShellUtils;

import java.util.concurrent.Executors;

/**
 * 默认设备策略
 * Created by samba on
 */
public class MCStrategy implements DevStrategy {
    private Context mContext;
    Intent systemBarIntent = new Intent("com.tchip.changeBarHideStatus");
    Intent statusBarIntent = new Intent("com.tchip.changeStatusBarHideStatus");

    public MCStrategy(Context context) {
        this.mContext = context;
    }

    @Override
    public String getLabel() {
        return DevContext.MC_LABEL;
    }

    @Override
    public String getMode() {
        return DevContext.MC_MODE;
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
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int hide = (barState == BarState.BAR_STATE_HIDE) ? 1 : 0;
                ShellUtils.execCmd("settings put system systembar_hide " + hide, true); // 1: 隐藏，0: 显示
                mContext.sendBroadcast(systemBarIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ShellUtils.execCmd("settings put system systemstatusbar_hide " + hide, true); // 1: 隐藏状态栏，0: 显示状态栏
                    mContext.sendBroadcast(statusBarIntent);
                }
            }
        });
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
