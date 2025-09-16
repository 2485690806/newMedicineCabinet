package leesche.smartrecycling.base.strategy;

/**
 * 设备运行strategy
 * Created by samba on 17/6/29.
 */
public interface DevStrategy {
    int FONT_SIZE_SMALL = 0;
    int FONT_SIZE_NORMAL = 1;
    int FONT_SIZE_BIG = 2;
    int FONT_SIZE_OVERSIZE = 3;

    int CAMERA_ORIENTATION_0 = 0;
    int CAMERA_ORIENTATION_90= 1;
    int CAMERA_ORIENTATION_180 = 2;
    int CAMERA_ORIENTATION_360 = 3;

    int DPI_120  =0;
    int DPI_160  =1;
    int DPI_240  =2;
    int DPI_320  =3;

    int SCREEN_ORIENTATION_0 = 0;
    int SCREEN_ORIENTATION_90= 90;
    int SCREEN_ORIENTATION_180 = 180;
    int SCREEN_ORIENTATION_270 = 270;

    int BAR_STATE_HIDE = 1;
    int BAR_STATE_SHOW = 0;
    enum FontSize {
        FONTSIZE_SMALL, FONTSIZE_NORMAL, FONTSIZE_BIG, FONTSIZE_OVERSIZE
    }
    enum CameraOrientation {
        CAMERA_ORIENTATION_0, CAMERA_ORIENTATION_90, CAMERA_ORIENTATION_180, CAMERA_ORIENTATION_360
    }
    enum Dpi{
        DPI_120,DPI_160, DPI_240, DPI_320
    }
    enum ScreenOrientation {
        SCREEN_ORIENTATION_0, SCREEN_ORIENTATION_90, SCREEN_ORIENTATION_180, SCREEN_ORIENTATION_270
    }
    enum BarState{
        BAR_STATE_HIDE,BAR_STATE_SHOW
    }

    /**
     * 获取设备策略名称
     */
    String getLabel();

    /**
     * 获取设备标识
     */
    String getMode();

    /**
     * 关机
     */
    void showDownSystem();

    /**
     * 重启系统
     */
    void reBootSystem();

    /**
     * 设置字体大小
     * @param fontSize FontSize
     */
    void setFontSize(FontSize fontSize);

    /**
     * 设置摄像头旋转方向
     * @param cameraOrientation CameraOrientation
     */
    void setCameraOrientation(CameraOrientation cameraOrientation);

    /**
     * 设置屏幕密度
     * @param dpi Dpi
     */
    void setDpi(Dpi dpi);

    /**
     * 设置屏幕旋转方向
     * @param screenOrientation ScreenOrientation
     */
    void setScreenOrientation(ScreenOrientation screenOrientation);

    /**
     * 设置状态栏显示状态
     * @param barState BarState
     */
    void setBarState(BarState barState);

    /**
     * 静默执行root 下命令
     * @param cmd
     */
    int exeRootCmdSilent(String cmd);

    /**
     * 静默安装apk
     * @param apkPath apk本地路径
     */
    void silentInstallApk(String apkPath);

    /**
     * 获取当前系统显示方向
     * @return
     */
    int getDisplayOrientation();

}
