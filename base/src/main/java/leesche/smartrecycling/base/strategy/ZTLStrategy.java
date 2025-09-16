package leesche.smartrecycling.base.strategy;

import android.content.Context;
import android.util.Log;

public class ZTLStrategy implements DevStrategy {
    private String Tag = ZTLStrategy.class.getSimpleName();
    private Context mContext;

    public ZTLStrategy(Context context) {
        this.mContext = context;
    }

    @Override
    public String getLabel() {
        return DevContext.ZTL_LABEL;
    }

    @Override
    public String getMode() {
        return DevContext.ZTL_MODE;
    }

    @Override
    public void showDownSystem() {
        DevManagerUtil.getZtlManager().shutDownSystem();
    }

    @Override
    public void reBootSystem() {
        DevManagerUtil.getZtlManager().rebootSystem();
    }

    @Override
    public void setFontSize(FontSize fontSize) {
        int fontSizeValue = -1;
        switch (fontSize) {
            case FONTSIZE_SMALL:
                fontSizeValue = FONT_SIZE_SMALL;
                break;
            case FONTSIZE_NORMAL:
                fontSizeValue = FONT_SIZE_NORMAL;
                break;
            case FONTSIZE_BIG:
                fontSizeValue = FONT_SIZE_BIG;
                break;
            case FONTSIZE_OVERSIZE:
                fontSizeValue = FONT_SIZE_OVERSIZE;
                break;
        }
        //todo
        Log.i(Tag, "修改字体大小为：" + fontSizeValue);
        DevManagerUtil.getZtlManager().setFontSize(fontSizeValue);
    }

    @Override
    public void setCameraOrientation(CameraOrientation cameraOrientation) {
        int cameraOrientationValue = -1;
        switch (cameraOrientation) {
            case CAMERA_ORIENTATION_0:
                cameraOrientationValue = CAMERA_ORIENTATION_0;
                break;
            case CAMERA_ORIENTATION_90:
                cameraOrientationValue = CAMERA_ORIENTATION_90;
                break;
            case CAMERA_ORIENTATION_180:
                cameraOrientationValue = CAMERA_ORIENTATION_180;
                break;
            case CAMERA_ORIENTATION_360:
                cameraOrientationValue = CAMERA_ORIENTATION_360;
                break;
        }
        //TODO
        Log.i(Tag, "摄像头旋转方向：" + cameraOrientationValue);
        DevManagerUtil.getZtlManager().setCameraOrientation(cameraOrientationValue);


    }

    @Override
    public void setDpi(Dpi dpi) {
        int dpiValue = -1;
        switch (dpi) {
            case DPI_120:
                dpiValue = DPI_120;
                break;
            case DPI_160:
                dpiValue = DPI_160;
                break;
            case DPI_240:
                dpiValue = DPI_240;
                break;
            case DPI_320:
                dpiValue = DPI_320;
                break;
        }
        //todo
        Log.i(Tag, "屏幕密度设置为：" + dpiValue);
        DevManagerUtil.getZtlManager().setDisplayDensity(dpiValue);

    }

    @Override
    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        int screenOrientationValue = -1;
        switch (screenOrientation) {
            case SCREEN_ORIENTATION_0:
                screenOrientationValue = SCREEN_ORIENTATION_0;
                break;
            case SCREEN_ORIENTATION_90:
                screenOrientationValue = SCREEN_ORIENTATION_90;
                break;
            case SCREEN_ORIENTATION_180:
                screenOrientationValue = SCREEN_ORIENTATION_180;
                break;
            case SCREEN_ORIENTATION_270:
                screenOrientationValue = SCREEN_ORIENTATION_270;
                break;
        }
        Log.i(Tag, "屏幕旋转方向为：" + screenOrientationValue);

        DevManagerUtil.getZtlManager().setDisplayOrientation(screenOrientationValue);
    }

    @Override
    public void setBarState(BarState barState) {
        int barStateValue = -1;
        switch (barState) {
            case BAR_STATE_HIDE:
                barStateValue = BAR_STATE_HIDE;
                break;
            case BAR_STATE_SHOW:
                barStateValue = BAR_STATE_SHOW;
                break;
        }
//        String simIMSI = DevManagerUtil.getZtlManager().getSimIccid();
//        Logger.i(Tag + ": " + simIMSI);
        Log.i(Tag, "导航栏状态修改为：" + barStateValue + "--->" + DevManagerUtil.getZtlManager().getSystemBarState());
        if (barStateValue == BAR_STATE_SHOW) {
            DevManagerUtil.getZtlManager().setOpenSystemBar();
        } else {
            DevManagerUtil.getZtlManager().setCloseSystemBar();
        }
    }

    @Override
    public int exeRootCmdSilent(String cmd) {
        return DevManagerUtil.getZtlManager().execRootCmdSilent(cmd);
    }

    @Override
    public void silentInstallApk(String apkPath) {
        DevManagerUtil.getZtlManager().installAppSilent(apkPath);
    }

    @Override
    public int getDisplayOrientation() {
        return DevManagerUtil.getZtlManager().getDisplayOrientation();
     }
}
