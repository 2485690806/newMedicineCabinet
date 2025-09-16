package com.ycmachine.smartdevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.activity.YPGActivity;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.utils.DateUtil;
import leesche.smartrecycling.base.utils.DeviceUtil;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        Logger.i("收到广播: " + action);

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_LOCKED_BOOT_COMPLETED:

            case "android.intent.action.PACKAGE_REPLACED":
                startMainActivity(context);
                break;

            case "android.intent.action.PACKAGE_ADDED":
                handlePackageAdded(context, intent);
                break;

            case "android.intent.action.PACKAGE_REMOVED":
                String removedPackage = intent.getDataString();
                Logger.i(removedPackage + " 终端程序被卸载");
                break;

            case "com.smartrecycling.alarm.clock":
                handleAlarmClock(context, intent);
                break;
        }
    }

    private void startMainActivity(Context context) {
        Intent intent = new Intent(context, YPGActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        // 谨慎使用 killProcess
        // android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void handlePackageAdded(Context context, Intent intent) {
        String packageName = intent.getDataString();
        if (packageName == null) return;

        if (packageName.contains("com.leesche.hkcommonservice")) {
            Intent launchIntent = context.getPackageManager()
                    .getLaunchIntentForPackage("com.leesche.hkcommonservice");
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
                Logger.i(packageName + " 程序已安装并启动");

                // 延迟启动 MainActivity
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startMainActivity(context);
                }, 4000);
            }
        } else {
            Logger.i(packageName + " 终端程序被安装");
        }
    }

    private void handleAlarmClock(Context context, Intent intent) {
        int taskId = intent.getIntExtra("id", 0);
        Logger.i("【定时任务：】当前系统时间：" +
                DateUtil.getCurTime(DateUtil.FORMAT_YYYY_MM_DD_HH_MM));

        if (taskId == 3) {
            DeviceUtil.rebootSys(context);
        } else if (taskId == 4) {
            Constants.SYS_REBOOT_FLAG = true;
        }
    }
}