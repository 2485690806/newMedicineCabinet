package leesche.smartrecycling.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.leesche.logger.Logger;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

import java.io.File;
import java.util.List;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.strategy.DevContext;
import leesche.smartrecycling.base.utils.CrashHandler;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals("com.ycmachine.smartdevice")
                    || processName.equals("leesche.smartrecycling.assistant");
            if (!defaultProcess) {
                return;
            }
        } else {
            return;
        }
        DevContext.initDev(this);
        Logger.init(Constants.LOGGER);
        //全局捕获错误日志
        CrashHandler.getInstance().init(this);
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(30_000) // set connection timeout.
                        .readTimeout(30_000) // set read timeout.
                ))
                .commit();
        File file = new File(Constants.UPDATE_CONFIG);
        if (file.exists()) {
            file.delete();
        }
//        FileDownloader.getImpl().create(Constants.VERSION_UPDATE_URL).setPath(Constants.UPDATE_CONFIG).start();
    }

    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
