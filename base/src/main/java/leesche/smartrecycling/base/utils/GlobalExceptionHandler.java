package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.http.HttpMethods;
import leesche.smartrecycling.base.strategy.DevContext;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler{

    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    private static GlobalExceptionHandler myCrashHandler;

    private Context mContext;

    private GlobalExceptionHandler(Context context) {
        mContext = context;
    }

    public static synchronized GlobalExceptionHandler getInstance(Context context) {
        if (null == myCrashHandler) {
            myCrashHandler = new GlobalExceptionHandler(context);
        }
        return myCrashHandler;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        //收集设备信息
        collectDeviceInfo(mContext);
        //保存日志文件
        String exceptionInfo = saveCrashInfo2File(throwable);
        //上报异常到后台
        HttpMethods.getInstance().logUpload("app_exception-" + Constants.MAC_ADDRESS,
                "异常信息：" + exceptionInfo);
        // 重启应用
        SystemClock.sleep(500);
        restartApp(mContext);
    }

    public void restartApp(Context mContext) {
        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx 上下文
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
//                Logger.e(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex 异常
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        return sb.toString();
    }
}
