package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;

import com.alibaba.fastjson.JSON;
import com.leesche.logger.Logger;
import com.leesche.logger.LoggerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.ThrAuditLogEntity;
import leesche.smartrecycling.base.entity.ThrAuditLogItemEntity;
import leesche.smartrecycling.base.entity.ThrFileUploadEntity;
import leesche.smartrecycling.base.entity.ThrFileUploadItemEntity;
import leesche.smartrecycling.base.http.HttpMethods;
import leesche.smartrecycling.base.http.thr.ThrHttpMethods;

/**
 * Desc:崩溃日志处理器
 * <p>
 * Created by yyw on 2017/11/1.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;

    public ThrAuditLogEntity getThrAuditLogEntity() {
        return thrAuditLogEntity;
    }

    public void setThrAuditLogEntity(ThrAuditLogEntity thrAuditLogEntity) {
        this.thrAuditLogEntity = thrAuditLogEntity;
    }
    public void addThrAuditLogEntity(ThrAuditLogItemEntity thrAuditLogEntity) {
        this.thrAuditLogEntity.getLogMessages().add(thrAuditLogEntity);
    }

    public  ThrAuditLogEntity thrAuditLogEntity = new ThrAuditLogEntity();
    private static CrashHandler instance;
    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE);

    //构造方法私有，防止外部构造多个实例，即采用单例模式
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //上报异常到后台
//        HttpMethods.getInstance().logUpload("app_exception-" + Constants.MAC_ADDRESS, "异常信息：" + ex);
        //导出异常信息到SD卡中
        dumpExceptionToSDCard(ex);
        // 重启应用
        SystemClock.sleep(500);
        restartApp(mContext);
    }

    public void restartApp(Context mContext) {
        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        Process.killProcess(Process.myPid());
    }

    /**
     * 保存异常信息到本地
     */
    private void dumpExceptionToSDCard(Throwable ex) {

        if (ex != null) Logger.e(JSON.toJSONString(ex));
//        if (ex != null) Logger.e(ex.getMessage());

;
        if (ex != null) {
            ThrHttpMethods.getInstance().sendAuditLog("error", ex.getMessage(),true,"123456");
            ThrHttpMethods.getInstance().sendAuditLog("error", ex.toString(),true,"123456");
        }else {
            ThrHttpMethods.getInstance().sendAuditLog("error", "发生未知异常",true,"123456");

        }


        File file = new File(LoggerConfig.CRASH_LOG_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        long current = System.currentTimeMillis();
        String fileName = fileNameFormat.format(current) + LoggerConfig.LOG_FILE_TYPE;
        file = new File(LoggerConfig.CRASH_LOG_PATH, fileName);

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file, true), true);
            if (file.exists() && file.length() > 0) {
                pw.println();
                pw.println();
            }
            //导出发生异常的时间
            String time = dateFormat.format(current);
            pw.println(time);

            //导出手机信息
            dumpPhoneInfo(pw);

            pw.println();
            //导出异常的调用栈信息
            ex.printStackTrace(pw);



            pw.close();
            Logger.e("错误日志保存路径：" + file.getAbsolutePath());
        } catch (Exception e) {
            Logger.e(e, "出现异常情况");
        }
    }

    /**
     * 存储设备信息
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        //包名
        pw.print("Package Name：");
        pw.println(mContext.getPackageName());

        //应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);

        pw.print("Device Id：");
        pw.println(Constants.MAC_ADDRESS);
    }

}
