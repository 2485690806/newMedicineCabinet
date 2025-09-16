package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.view.Gravity;


import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.strategy.DevContext;

public class AppUtil {
    public static final Context context = DevContext.getInstance().getContext();

    public static void openAppToPackgeName(String packname, String[][] str) {
        PackageManager packageManager = context.getPackageManager();
        if (checkPackInfo(packname)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packname);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int i = 0; i < str.length; i++) {
//                Logger.e(str[i][0] + "-->" + str[i][1]);
                intent.putExtra(str[i][0], str[i][1]);
            }
            context.startActivity(intent);
        } else {
//            ToastUtil.showErrorMsg("没有安装该功能", Gravity.CENTER);
        }
    }

    public static void openAppToPackgeName(String packname, String className, String[][] str, String action) {
        if (checkPackInfo(packname)) {
            Intent intent = new Intent(action);
            intent.setClassName(packname, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int i = 0; i < str.length; i++) {
//                Logger.e(str[i][0] + "-->" + str[i][1]);
                intent.putExtra(str[i][0], str[i][1]);
            }
            context.startActivity(intent);
        } else {
//            ToastUtil.showErrorMsg("没有安装该功能", Gravity.CENTER);
        }
    }

    public static void openAppToPackgeName(String packname) {
        PackageManager packageManager = context.getPackageManager();
        if (checkPackInfo(packname)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packname);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } else {
//            ToastUtil.showErrorMsg("没有安装该功能", Gravity.CENTER);
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    public static boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static boolean checkUpdate(Context context, String url, String deviceType) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        if (!(url.endsWith("apk") || url.endsWith("zip"))) {
//            Logger.e("不合法升级链接");
            return false;
        }
        int apkVersion;
        try {
            apkVersion = getUrlVersionCode(url, deviceType);
            int initVersion = 3000;
//            if ("SmartRecycling".equals(deviceType)) {
//                initVersion = 2000;
//            }
            int curVersion = DeviceUtil.getAppVersionCode(context)  - initVersion;
//            Logger.e("进入更新：" + apkVersion);
            if (curVersion >= apkVersion) {
//                Logger.e("更新版本小于或者等于当前版本");
                return false;
            }
            return true;
        } catch (Exception e) {
//            Logger.e("检测版本异常");
        }
        return false;
    }

    public static boolean checkUpdate3(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        if (!(url.endsWith("apk"))) {
//            Logger.e("不合法升级链接");
            return false;
        }
//        Logger.e("进入更新：" + url);
        return true;
    }

    public static int checkUpdate2(Context context, String url, String deviceType) {
        if (url == null || url.isEmpty()) {
            return 0;
        }
        int apkVersion;
        if (!(url.endsWith("apk") || url.endsWith("zip"))) {
//            Logger.e("不合法升级链接");
            return 0;
        }
        try {
            apkVersion = getUrlVersionCode(url, deviceType);
            int initVersion = 3000;
            if ("SmartRecycling".equals(deviceType) || "AdScreen".equals(deviceType)) {
                initVersion = 2000;
            }
            int curVersion = DeviceUtil.getAppVersionCode(context) - initVersion;
//            Logger.e("进入更新：" + apkVersion);
            if (curVersion > apkVersion) {
                return 2;
            }
            if (curVersion == apkVersion) {
                return 0;
            }
            return 1;
        } catch (Exception e) {
//            Logger.e("检测版本异常");
        }
        return 0;
    }

    public static int getUrlVersionCode(String url, String deviceType) {
        int length = deviceType.length() + 6;
        return Integer.parseInt(url.substring(url.lastIndexOf("/") + length, url.length() - 4));
    }

    final static int COUNTS = 4;// 点击次数
    final static long DURATION = 1000;// 规定有效时间
    static long[] mHits = new long[COUNTS];

    public static boolean continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            return true;
        }
        return false;
    }

    /**
     * 回滚到上一版本
     */
    public static void rollBackVersion() {
        new Thread() {
            public void run() {
                if (DevContext.getInstance().getDevStrategy().exeRootCmdSilent("pm uninstall -k com.ycmachine.smartdevice") == 0) {
                    DevContext.getInstance().getDevStrategy().exeRootCmdSilent("pm install -r " + Constants.APK_CACHE_DIR);
                    DeviceUtil.restartLauncherApp();
                }
            }
        }.start();
    }
}
