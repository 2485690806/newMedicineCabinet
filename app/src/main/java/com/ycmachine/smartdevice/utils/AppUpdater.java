//package com.ycmachine.smartdevice.utils;
//
//
//
//import android.app.DownloadManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.Uri;
//import android.os.Environment;
//
//import com.zcapi;
//
//public class AppUpdater {
//
//    private Context context;
//    private String apkUrl;
//    private long downloadId;
//
//    public static zcapi zcApi = new zcapi();
//
//
//    public AppUpdater(Context context, String apkUrl) {
//        this.context = context;
//        this.apkUrl = apkUrl;
//    }
//
//    public void startUpdate() {
//        // 注册下载完成的广播接收器
//        context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//
//        // 开始下载
//        downloadId = downloadFile(apkUrl);
//    }
//
//    private long downloadFile(String url) {
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setTitle("App更新");
//        request.setDescription("正在下载新版本");
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app_update.apk");
//
//        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        return downloadManager.enqueue(request);
//    }
//
//    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//            if (id == downloadId) {
//                // 下载完成，安装 APK
//                System.out.println("下载完成！！！！！！！！！！！");
//                installApk();
//            }
//        }
//    };
//
//
//    private void installApk() {
//        zcApi.InstallApk("/storage/emulated/0/Download/app_update.apk", true);
//        // 注销广播接收器
//        context.unregisterReceiver(onDownloadComplete);
//    }
//
//}
