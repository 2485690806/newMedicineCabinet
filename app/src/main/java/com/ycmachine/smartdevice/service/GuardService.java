package com.ycmachine.smartdevice.service;

import static com.ycmachine.smartdevice.ClientApplication.TAG;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.activity.medicineCabinet.YPGActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.strategy.DevContext;


// 用于提供AIDL 接口给其它app使用
public class GuardService extends Service {
    // GuardService 是一个Android后台服务，主要功能是监控主进程状态、维护心跳连接、处理数据上传以及确保应用稳定性。。

    public static final int UPLOAD_RECORD_DATA = 0x00000001;
    public static final int UPLOAD_SALE_DATA = 0x00000002;

    private android.os.Binder mApi; // 远程API实现
    private Thread mWatchDog; // 看门狗线程
    BlurHandler blurHandler; // 模糊处理Handler(当前未实际使用)
    private Handler mMainHandler = new Handler(); // 主线程Handler
    private volatile long mLastHeartBeatTime; // 最后心跳时间
    private volatile boolean mMonitorMainProcess = true; // 是否监控主进程

    @Override
    public IBinder onBind(Intent intent) {

        return mApi; // 返回Binder接口
    }

    @SuppressLint("HandlerLeak")
    private class BlurHandler extends Handler {
        private final WeakReference<GuardService> mTarget;

        public BlurHandler(GuardService controller) {
            mTarget = new WeakReference<GuardService>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            GuardService controller = mTarget.get();
            if (controller != null) {

            }
        }
    }

//    private class RemoteApiImpl extends IBinder。Stub {
//        private IBinder.DeathRecipient mRecipient;
//        private IBinder mBinder;
//
//        @Override
//        public void join(IBinder token) throws RemoteException {
//            if (mBinder != null && mRecipient != null) {
//                try {
//                    mBinder.unlinkToDeath(mRecipient, 0);
//                    mRecipient = null;
//                } catch (NoSuchElementException e) {
//                }
//            }
//            mBinder = token;
//
//            updateHeartBeatTime();
//
//            mMonitorMainProcess = true;
//
//            mRecipient = new IBinder.DeathRecipient() {
//                @Override
//                public void binderDied() {
////                    AppLogger.getInstance().writeLog("binderDied, restart main activity later");
////                    startMainActivityDelay(1500, "binder died");
//                }
//            };
//            token.linkToDeath(mRecipient, 0);
//        }
//
//        @Override
//        public void leave(IBinder token) {
//            mMonitorMainProcess = false;
//            try {
//                token.unlinkToDeath(mRecipient, 0);
//                mRecipient = null;
//            } catch (NoSuchElementException e) {
//            }
//        }
//
//        @Override
//        public void sendHeartbeat() {
//            updateHeartBeatTime();
//        }
//
//        @Override
//        public void startUploadDeliveryInfo(String device_token, boolean allow) throws RemoteException {
//            Constants.CAN_UPLOAD = true;
//            Constants.DEVICE_TOKEN = device_token;
//            if (mWatchDog == null) {
//                mWatchDog = new WatchDog();
//                mWatchDog.start();
//            } else {
//                Log.i(TAG, mWatchDog.isAlive() + "");
//            }
//        }
//
//        @Override
//        public void setUpdateFacePath(String facePath) throws RemoteException {
//
//        }
//
//        @Override
//        public void setOssEntity(String ossEntityJson) throws RemoteException {
//
//        }
//
//        public void destroy() {
//            mMonitorMainProcess = false;
//            if (mBinder != null && mRecipient != null) {
//                try {
//                    mBinder.unlinkToDeath(mRecipient, 0);
//                    mRecipient = null;
//                } catch (NoSuchElementException e) {
//                }
//            }
//        }
//    }

    private void updateHeartBeatTime() {
        mLastHeartBeatTime = SystemClock.elapsedRealtime();
        Log.i("mLastHeartBeatTime", mLastHeartBeatTime + "");
    }

    @Override
    public void onCreate() {
        mWatchDog = new WatchDog(); // 创建看门狗线程
        mWatchDog.start(); // 启动看门狗
        blurHandler = new BlurHandler(this); // 初始化Handler
        DevContext.initDev(this); // 初始化设备上下文
        Logger.init(Constants.LOGGER); // 初始化日志系统
//        ConfigManager.getInstance().setClient2Config();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mWatchDog != null) {
            mWatchDog.interrupt();
            mWatchDog = null;
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, GuardService.class)); // 自重启
    }

    private class WatchDog extends Thread {

        public WatchDog() {
            super("WatchDog");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Thread.sleep(60 * 1000); // 每分钟检查一次
                } catch (InterruptedException e) {
                    break;  // 此处应该 break ，否则在没有使用 startService 启动的情况下会导致 GuardService 无法回收
                }
                // 检查主进程状态
                int mainProcessId = getMainProcessId();
                if (mainProcessId < 0) {
                    break;
                }
                boolean mainProcessIsRunning = false;
                if (mainProcessId > 0) {
                    mainProcessIsRunning = true;
                }
                // 异常处理逻辑
                if (mMonitorMainProcess && !isInterrupted()
                        && (!mainProcessIsRunning || (SystemClock.elapsedRealtime() - mLastHeartBeatTime > 90 * 1000))) {

                    // 杀死异常主进程
                    if (mainProcessIsRunning) {
                        if (mApi != null) {
//                            ((RemoteApiImpl) mApi).destroy();
                        }
                        android.os.Process.killProcess(mainProcessId);
                    }

                    // 重启主Activity
//                    startMainActivityDelay(100, "main thread blocked");

                } else {
                    // 正常情况处理离线数据
//                    mMainHandler.postDelayed(() ->  BackendRequestHandler.getInstance().handlerOfflineDataToUpload(), 500);
                }
            }
        }
    }


    private void startMainActivityDelay(int milliSeconds, final String reason) {
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.equals(reason, "upgrade") && getMainProcessId() > 0) {
//                    AppLogger.getInstance().writeWarning("main process is running, ignore start action for reason(%s)", reason);
                    return;
                }
                try {
                    Intent intent = new Intent(getApplicationContext(), YPGActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getApplicationContext().startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (Exception e) {
                }
            }
        }, milliSeconds);

    }

    private int getMainProcessId() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) return -1;

        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            String processName = procInfo.processName;
            if (TextUtils.equals(processName, getPackageName())) {
                Log.e(TAG, "WatchDog:" + processName);
                return procInfo.pid;
            }
        }
        return 0;
    }
}
