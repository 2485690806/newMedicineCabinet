package leesche.smartrecycling.base.utils;

import android.annotation.SuppressLint;

import com.jcraft.jsch.SftpProgressMonitor;
import com.leesche.logger.Logger;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import leesche.smartrecycling.base.websocket.WebSocketHelper;

public class SftpMonitor implements SftpProgressMonitor, Runnable {
    private long maxCount = 0;// 文件的总大小
    private String transFileName;
    private int transType = 0;
    public long startTime = 0L;
    private long uploaded = 0;
    private boolean isScheduled = false;
    ScheduledExecutorService executorService;

    public SftpMonitor(long maxCount, String transFileName) {
        this.maxCount = maxCount;
        this.transFileName = transFileName;
    }

    @SuppressLint("SdCardPath")
    @Override
    public void run() {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        String value = format.format((uploaded / (double) maxCount));
        Logger.i("【SFTP】 file trans: " + "已传输：" + uploaded / 1024 + "KB,传输进度：" + value);
        if (uploaded == maxCount) {
            stop();
            long endTime = System.currentTimeMillis();
            Logger.i("【SFTP】 file trans: " + "传输完成！用时：" + (endTime - startTime) / 1000 + "s");
            String remark;
            if (transType == 0) {
                remark = "true|Already uploaded:" + transFileName;
//                WebSocketHelper.getInstance().uploadOctEvent(WebSocketHelper.OctEventType.upload, remark);
            }
            if (transType == 1) {
                List<String> existFiles = FileUtil.getFilePaths("/sdcard/com.ocl.arwl/download");
                for (String path : existFiles) {
                    if (transFileName.contains("IBKL") && path.contains("IBKL") && !path.contains(transFileName)) {
                        FileUtil.deleteSingleFile(path);
                    }
                    if (transFileName.contains("OTP") && path.contains("OTP") && !path.contains(transFileName)) {
                        FileUtil.deleteSingleFile(path);
                    }
                }
                remark = "true|Already downloaded:" + transFileName;
//                WebSocketHelper.getInstance().uploadOctEvent(WebSocketHelper.OctEventType.download, remark);
            }
        }

    }

    /**
     * 输出每个时间段的上传大小
     */
    @Override
    public boolean count(long count) {
        if (!isScheduled) {
            createTread();
        }
        uploaded += count;
//        Logger.i("【SFTP】 file trans: " + "本次上传/下载大小：" + count / 1024 + "KB,");
        return count > 0;

    }

    /**
     * 文件上传结束时调用
     */
    @Override
    public void end() {
        // System.out.println("文件传输结束");
    }

    /**
     * 文件上传时开始调用
     */
    @Override
    public void init(int op, String src, String dest, long max) {
        transType = op;
        if (op == 0) {
            System.out.println();
            Logger.i("【SFTP】 file upload: " + src + "至远程：" + dest + "文件总大小:" + maxCount / 1024 + "KB");
        }
        if (op == 1) {
            Logger.i("【SFTP】 file download: " + src + "至本地：" + dest + "文件总大小:" + maxCount / 1024 + "KB");
        }
        startTime = System.currentTimeMillis();
    }

    /**
     * 创建一个线程每隔一定时间，输出一下上传进度
     */
    public void createTread() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        // 1秒钟后开始执行，每2杪钟执行一次
        executorService.scheduleWithFixedDelay(this, 1, 2, TimeUnit.SECONDS);
        isScheduled = true;
    }

    /**
     * 停止方法
     */
    public void stop() {
        boolean isShutdown = executorService.isShutdown();
        if (!isShutdown) {
            executorService.shutdown();
        }
    }
}
