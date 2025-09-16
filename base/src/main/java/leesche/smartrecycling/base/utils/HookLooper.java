package leesche.smartrecycling.base.utils;

import android.os.Looper;
import android.util.Log;

import com.leesche.logger.Logger;


public class HookLooper {
    private static final String TAG = "Suyf";
    private static HookLooper instance;

    private HookLooper() {}

    public static synchronized HookLooper getInstance() {
        if (instance == null) {
            instance = new HookLooper();
        }
        return instance;
    }

    public static void hook() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                // 收集崩溃信息
                Logger.e(TAG, "Thread t-id: " + t.getId()
                        + ", t-name: " + t.getName()
                        + ", t-looper: " + Looper.myLooper()
                        + ", isMainLooper: " + (Looper.myLooper() == Looper.getMainLooper()));
                Logger.e(TAG, "uncaughtException: " + e.getMessage());

                // 如果不是主线程，直接返回
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    return;
                }

                // 循环处理主线程异常，避免应用退出
                while (true) {
                    try {
                        Log.d(TAG, "Looper.loop===before");
                        // 确保当前是主线程的Looper
                        if (Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
                            Looper.loop();
                        } else {
                            break;
                        }
                        Log.d(TAG, "Looper.loop===after");
                    } catch (Exception exception) {
                        Log.d(TAG, "Looper.loop===error: " + exception.getMessage());
                    }
                }
            }
        });
    }
}
