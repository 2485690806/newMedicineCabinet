package leesche.smartrecycling.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.leesche.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import leesche.smartrecycling.base.strategy.DevContext;

public class DeviceUtil {

    /**
     * 计算物体从静止开始匀加速运动的时间
     * @param speed 加速度（单位：m/s）
     * @param distance 位移距离（单位：m）
     * @return 时间（单位：s），若参数非法返回-1
     */
    public static double calculateTime(double speed, double distance) {
        if (speed <= 0 || distance <= 0) {
            System.out.println("错误：加速度和距离必须为正数");
            return -1;
        }

        //speed is mm/sec
        double time = 1000 / speed * distance;
        return time;
    }
    //获取手机的唯一标识
    public static String getAndroidSign(Context context) {
        StringBuilder deviceId = new StringBuilder();
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
//            String uuid = getUUID();
//            if(!TextUtils.isEmpty(uuid)){
//                deviceId.append("id");
//                deviceId.append(uuid);
//                return deviceId.toString();
//            }
        } catch (Exception e) {
            e.printStackTrace();
//            deviceId.append("id").append(getUUID());
        }
        return deviceId.toString();
    }

    //获取设备序列号
    @SuppressLint({"NewApi", "MissingPermission"})
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
//            Logger.i("设备序列号：" + serial);
        } catch (Exception e) {
            e.printStackTrace();
//            Logger.e("读取设备序列号异常：" + e.toString());
        }
        return serial;
    }

    /**
     * 得到全局唯一UUID
     */
    private String uuid;

    public String getUUID() {
//        SharedPreferences mShare = getSharedPreferences("uuid",MODE_PRIVATE);
//        if(mShare != null){
//            uuid = mShare.getString("uuid", "");
//        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
//            mShare.edit().putString("uuid",uuid).commit();
        }
        return uuid;
    }

    /**
     * 返回当前程序版本名
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
//            Logger.e("Exception", e);
        }
        return versionName;
    }

    /**
     * 返回当前程序版本编码
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int versionName = -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionCode;
        } catch (Exception e) {
//            Logger.e("Exception", e);
        }
        return versionName;
    }


    public static void recoverySys() {
        String cmd = "su -c reboot";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
//            ToastUtil.showErrorMsg("不支持重启设备", Gravity.CENTER);
        }
    }

    /**
     * 重启APP
     */
    public static void restartApp(Activity context, String cause) {
//        Logger.i(cause + "， 正在重启APP");
        Intent intent = new Intent(context, context.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public static void rebootSys(Context context) {
//        Logger.i("正在重启系统");
        Intent intentSetOff = new Intent();
        intentSetOff.setAction("wits.com.simahuan.reboot");
        context.sendBroadcast(intentSetOff);
    }

    /**
     * 重启APP
     */
    public static void restartLauncherApp() {
        AppUtil.openAppToPackgeName("com.ycmachine.smartdevice");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 检查设备是否有摄像头
     *
     * @return
     */
    public static boolean hasCamera() {
        return hasBackFacingCamera() || hasFrontFacingCamera();
    }

    /**
     * 检查设备是否有后置摄像头
     *
     * @return
     */
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    /**
     * 检查设备是否有前置摄像头
     *
     * @return
     */
    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static boolean checkRooted() {
        boolean result = false;
        try {
            result = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean checkCameraFacing(final int facing) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    //android.os.Build 类中。包括了这样的一些信息。我们可以直接调用 而不需要添加任何的权限和方法。
//    android.os.Build.BOARD //：获取设备基板名称
//    android.os.Build.BOOTLOADER:获取设备引导程序版本号
//    android.os.Build.BRAND：获取设备品牌
//    android.os.Build.CPU_ABI：获取设备指令集名称（CPU的类型）
//    android.os.Build.CPU_ABI2：获取第二个指令集名称
//    android.os.Build.DEVICE：获取设备驱动名称
//    android.os.Build.DISPLAY：获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
//    android.os.Build.FINGERPRINT：设备的唯一标识。由设备的多个信息拼接合成。
//    android.os.Build.HARDWARE：设备硬件名称,一般和基板名称一样（BOARD）
//    android.os.Build.HOST：设备主机地址
//    android.os.Build.ID:设备版本号。
    public static String printDeviceInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Dev Model：" + Build.MODEL.substring(0, 2));
        stringBuilder.append("  Android Sys Ver：" + android.os.Build.VERSION.RELEASE);
        stringBuilder.append("  Sys Desk Package Name：" + getLauncherPackageName(DevContext.getInstance().getContext()));
//        Logger.i(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            return "";
        }
        //如果是不同桌面主题，可能会出现某些问题，这部分暂未处理
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }

    public static boolean isOldRom() {
        String packageName = getLauncherPackageName(DevContext.getInstance().getContext());
        if ("com.ycmachine.smartdevice".equals(packageName)) {
            return false;
        }
        if ("ZC-328".equals(Build.MODEL)) {
            if ("MXC89K".equals(android.os.Build.ID)) {
                return true;
            }
            if (android.os.Build.VERSION.RELEASE.startsWith("6")) {
                return true;
            }
        }
        return true;
    }

    public static String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                // 跳过回环接口和未启用的接口
                if (intf.isLoopback() || !intf.isUp()) continue;
                List<InetAddress> addresses = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addresses) {
                    // 过滤 IPv6 地址和非本地链接地址（169.254.x.x）
                    if (addr.isLinkLocalAddress() || addr.getHostAddress().contains(":")) continue;
                    String ip = addr.getHostAddress();
                    Logger.d("[系统]IP地址 ==> " + "Interface: " + intf.getName() + ", IP: " + ip);
                    return ip;
                }
            }
        } catch (Exception ex) {
            Logger.e("[系统]IP地址 ==> " + "Error getting IP address: " + ex.getMessage());
        }
        return null;
    }
}
