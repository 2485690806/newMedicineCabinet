package leesche.smartrecycling.base.utils;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.text.TextUtils;

import com.amazonaws.util.IOUtils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.leesche.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import leesche.smartrecycling.base.http.HttpUrls;

public class SFTPUtil {

    private ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String host;
    /**
     * SFTP 端口
     */
    private int port;
    SftpMonitor monitor;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SFTPUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SFTPUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtil() {
    }


    /**
     * 连接sftp服务器
     */
    public void login() {
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);// 设置私钥
            }

            session = jsch.getSession(username, host, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param basePath     服务器的基础路径
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     */
    public void upload(String basePath, String sftpFileName, InputStream input) throws SftpException {
        try {
            monitor = new SftpMonitor(input.available(), sftpFileName);
            sftp.cd(basePath);
//            if (!TextUtils.isEmpty(directory)) {
//            sftp.cd(directory);
//            }
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String[] dirs = basePath.split("/");
            String tempPath = basePath;
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) continue;
                tempPath += "/" + dir;
                try {
                    sftp.cd(tempPath);
                } catch (SftpException ex) {
                    sftp.mkdir(tempPath);
                    sftp.cd(tempPath);
                }
            }
        } catch (IOException e) {
            monitor.stop();
        }
        sftp.put(input, sftpFileName, monitor);  //上传文件
    }


    /**
     * 下载文件。
     *
     * @param directory 下载目录
     */
    public void downloadOctopus(String directory) throws SftpException {
        try {
            if (directory != null && !"".equals(directory)) {
                sftp.cd(directory);
            }
            Vector filesObject = sftp.ls(directory);
            File file = null;
            String downloadRoot = "/sdcard/com.ocl.arwl/download/";
            for (Object object : filesObject) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) object;
                if (lsEntry.getFilename().startsWith("OTP") || lsEntry.getFilename().startsWith("IBKL")) {
                    monitor = new SftpMonitor(getFileSize(directory + File.separator + lsEntry.getFilename()), lsEntry.getFilename());
                    if (lsEntry.getFilename().startsWith("OTP")) {
                        file = new File(downloadRoot + lsEntry.getFilename());
                    }
                    if (lsEntry.getFilename().startsWith("IBKL")) {
                        file = new File(downloadRoot + lsEntry.getFilename());
                    }
                    sftp.get(lsEntry.getFilename(), new FileOutputStream(file), monitor);
                    Logger.i("【SFTP】 file download: " + lsEntry.getFilename());
                    SystemClock.sleep(6000);
                }
            }
        } catch (IOException e) {
            if (monitor != null) monitor.stop();
            Logger.i("【SFTP】 file download: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);
        return IOUtils.toByteArray(is);
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }


    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @param
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    @SuppressLint("SdCardPath")
    public static void uploadExchangeFile(String fileName) {
        ThreadManager.getThreadPollProxy().execute(() -> {
            SFTPUtil sftp = new SFTPUtil(HttpUrls.loginName, HttpUrls.sftURL, HttpUrls.port, "/sdcard/RVM/" + HttpUrls.accessKeyFileName);
            sftp.login();
            File file = new File("/sdcard/com.ocl.arwl/upload/" + fileName);
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                sftp.upload(HttpUrls.downloadPath,  fileName, is);
                sftp.logout();
            } catch (FileNotFoundException | SftpException ignored) {

            }
        });
    }

    public static void downloadFile() {
        ThreadManager.getThreadPollProxy().execute(() -> {
            SFTPUtil sftp = new SFTPUtil(HttpUrls.loginName, HttpUrls.sftURL, HttpUrls.port, "/sdcard/RVM/" + HttpUrls.accessKeyFileName);
            sftp.login();
            try {
                sftp.downloadOctopus(HttpUrls.downloadPath);
                sftp.logout();
            } catch (SftpException ignored) {

            }
        });
    }

    public long getFileSize(String srcSftpFilePath) {
        long fileSize;//文件大于等于0则存在
        try {
            SftpATTRS sftpATTRS = sftp.lstat(srcSftpFilePath);
            fileSize = sftpATTRS.getSize();
        } catch (Exception e) {
            fileSize = -1;//获取文件大小异常
            if (e.getMessage().toLowerCase().equals("no such file")) {
                fileSize = -2;//文件不存在
            }
        }
        return fileSize;
    }

    public String getLastModifiedTime(String srcSftpFilePath) {
        try {
            SftpATTRS sftpATTRS = sftp.lstat(srcSftpFilePath);
            int mTime = sftpATTRS.getMTime();
            Date lastModified = new Date(mTime * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(lastModified);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.ofYearDay(2000, 1).plus(12 * 24 * 60 * 60, ChronoUnit.SECONDS);
        }
        System.out.println(date.toString());
    }
}
