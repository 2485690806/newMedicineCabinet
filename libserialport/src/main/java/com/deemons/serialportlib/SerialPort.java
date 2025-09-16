package com.deemons.serialportlib;

import android.util.Log;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";

    public static final String DEFAULT_SU_PATH = "/system/bin/su";
    public static final String POWER_SU_PATH = "/system/xbin/su";

    private static String sSuPath = DEFAULT_SU_PATH;
//    static Context mContext;
    /**
     * Set the su binary path, the default su binary path is {@link #DEFAULT_SU_PATH}
     *
     * @param suPath su binary path
     */
    public static void setSuPath(String suPath) {
        if (suPath == null) {
            return;
        }
        sSuPath = suPath;
//        mContext = context;
    }

    public static String getSuPath() {
        return sSuPath;
    }

    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(String filePath, int baudRate) throws SecurityException, IOException {
        this(new File(filePath), baudRate);
    }

    public SerialPort(File device, int baudRate) throws SecurityException, IOException {
        this(device, baudRate, 0);
    }

    public SerialPort(File file, int baudRate, int flags) throws SecurityException, IOException {
        this(file, baudRate, 0, 8, 1, flags);
    }

    /**
     * 打开串口
     *
     * @param device 串口设备文件
     * @param baudRate 波特率
     * @param parity 奇偶校验，0 None（默认）； 1 Odd； 2 Even
     * @param dataBits 数据位，5 ~ 8  （默认8）
     * @param stopBit 停止位，1 或 2  （默认 1）
     * @param flags 标记 0（默认）
     * @throws SecurityException
     * @throws IOException
     */
    public SerialPort(File device, int baudRate, int parity, int dataBits, int stopBit, int flags)
        throws SecurityException, IOException {

        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            Log.d(TAG, "Missing read permission : " + device.canRead());
            Log.d(TAG, "Missing write permission : " + device.canWrite());
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec(sSuPath);
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                Log.d(TAG, "cmd: " + cmd);
                su.getOutputStream().write(cmd.getBytes());
                Log.d(TAG, "Missing read permission : " + device.canRead());
                Log.d(TAG, "Missing write permission : " + device.canWrite());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        mFd = open(device.getAbsolutePath(), baudRate, parity, dataBits, stopBit, flags);
        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public void closeSerial(){
        close();
    }

    // JNI
    private native static FileDescriptor open(String path, int baudRate, int parity, int dataBits,
                                              int stopBit, int flags);

    public native static void close();

    static {
        System.loadLibrary("serial-port");
    }
}
