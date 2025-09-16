package leesche.smartrecycling.base.strategy;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import java.io.DataOutputStream;
import java.lang.reflect.Method;

import leesche.smartrecycling.base.utils.DeviceUtil;

public class ZCStrategy implements DevStrategy {
    private Context mContext;
    private Method getLongMethod = null;
    private Method setMethod = null;
    private Method getBooleanMethod = null;

    public ZCStrategy(Context context) {
        this.mContext = context;
    }

    @Override
    public String getLabel() {
        return DevContext.ZC_LABEL;
    }

    @Override
    public String getMode() {
        return DevContext.ZC_MODE;
    }

    @Override
    public void showDownSystem() {
//        Logger.e("卓策->发送关机广播");
        mContext.sendBroadcast(new Intent("com.android.yf_shutdown"));
    }

    @Override
    public void reBootSystem() {
//        Logger.e("卓策->重启");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec("reboot");
                } catch (Exception e) {
//                    Logger.e("卓策重启异常");
                }
            }
        }, 1000);
        DeviceUtil.rebootSys(mContext);
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
//        Logger.e("设置字体大小为：" + (fontSizeValue + 1));
        Intent intent = new Intent("com.zc.SetFontSize");
        intent.putExtra("FontIndex", (fontSizeValue + 1));
        mContext.sendBroadcast(intent);
        reBootSystem();

    }

    @Override
    public void setCameraOrientation(CameraOrientation cameraOrientation) {
        int cameraOrientationValue = -1;
        switch (cameraOrientation) {
            case CAMERA_ORIENTATION_0:
                cameraOrientationValue = 0;
                break;
            case CAMERA_ORIENTATION_90:
                cameraOrientationValue = 90;
                break;
            case CAMERA_ORIENTATION_180:
                cameraOrientationValue = 180;
                break;
            case CAMERA_ORIENTATION_360:
                cameraOrientationValue = 270;
                break;
        }
        //TODO
//        Logger.e("摄像头旋转方向：" + cameraOrientationValue);
        setProperties("persist.sys.camerarotation", cameraOrientationValue + "");
    }

    @Override
    public void setDpi(Dpi dpi) {
        int dpiValue = -1;
        switch (dpi) {
            case DPI_120:
                dpiValue = 120;
                break;
            case DPI_160:
                dpiValue = 160;
                break;
            case DPI_240:
                dpiValue = 240;
                break;
            case DPI_320:
                dpiValue = 320;
                break;
        }
        //todo
//        Logger.e("屏幕密度设置为：" + dpiValue);
        setProperties("persist.sys.zcdpi", dpiValue + "");
        reBootSystem();
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
//        Logger.e("屏幕旋转方向为：" + screenOrientationValue);
        setProperties("persist.sys.rotation", screenOrientationValue + "");
    }

    @Override
    public void setBarState(BarState barState) {
        switch (barState) {
            case BAR_STATE_HIDE:
//                Logger.i("【Navigation Bar】Hide");
                mContext.sendBroadcast(new Intent("hide.systemui"));
                break;
            case BAR_STATE_SHOW:
//                Logger.i("【Navigation Bar】Show");
                mContext.sendBroadcast(new Intent("show.systemui"));
                break;
        }
    }

    @Override
    public int exeRootCmdSilent(String cmd) {
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            return p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void silentInstallApk(String apkPath) {
        exeRootCmdSilent("pm install -r " + apkPath);
    }

    @Override
    public int getDisplayOrientation() {
        int rotation = Integer.parseInt(String.valueOf(getLong("persist.sys.rotation", -1)));
//        Logger.i("当前屏幕方向：" + rotation);
        return rotation;
    }


    public long getLong(final String key, final long def) {
        try {
            if (getLongMethod == null) {
                getLongMethod = Class.forName("android.os.SystemProperties")
                        .getMethod("getLong", String.class, long.class);
            }

            return ((Long) getLongMethod.invoke(null, key, def)).longValue();
        } catch (Exception e) {
//            Logger.e("Platform error: " + e.toString());
            return def;
        }
    }

    public long setProperties(final String key, final String value) {
        try {
            if (setMethod == null) {
                setMethod = Class.forName("android.os.SystemProperties")
                        .getMethod("set", String.class, String.class);
            }

            setMethod.invoke(null, key, value);
        } catch (Exception e) {
//            Logger.e("Platform error: " + e.toString());
        }
        return 0;
    }

    public boolean getBoolean(final String key, final boolean def) {
        try {
            if (getBooleanMethod == null) {
                getBooleanMethod = Class.forName("android.os.SystemProperties")
                        .getMethod("getBoolean", String.class, boolean.class);
            }
            return (Boolean) getBooleanMethod.invoke(null, key, def);
        } catch (Exception e) {
//            Logger.e("Platform error: " + e.toString());
            return def;
        }
    }

}
