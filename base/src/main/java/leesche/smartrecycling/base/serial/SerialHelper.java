package leesche.smartrecycling.base.serial;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.stick.AbsStickPackageHelper;
import tp.xmaihh.serialport.stick.BaseStickPackageHelper;
import tp.xmaihh.serialport.utils.ByteUtil;

public class SerialHelper {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private String sPort = "/dev/ttyS1";
    private int iBaudRate = 9600;
    private int stopBits = 1;
    private int dataBits = 8;
    private int parity = 0;
    private int flowCon = 0;
    private int flags = 0;
    private boolean _isOpen = false;
    private byte[] _bLoopData = {48};
    private int iDelay = 500;

    private ScheduledFuture sendStrTask;//循环发送任务
    private Queue<String> queueMsg = new ConcurrentLinkedQueue<String>();//线程安全到队列
    private ScheduledExecutorService scheduledExecutor;//线程池 同一管理保证只有一个

    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;

        scheduledExecutor = Executors.newScheduledThreadPool(8);//初始化8个线程
    }

    public void open() {
        try {
            this.mSerialPort = new SerialPort(new File(this.sPort), this.iBaudRate, this.stopBits, this.dataBits, this.parity, this.flowCon, this.flags);

            this.mOutputStream = this.mSerialPort.getOutputStream();
            this.mInputStream = this.mSerialPort.getInputStream();
            this.mReadThread = new ReadThread();
            this.mReadThread.start();
            this.mSendThread = new SendThread();
            this.mSendThread.setSuspendFlag();
            this.mSendThread.start();
            this._isOpen = true;

            if (mOnSerialListener != null) {
                mOnSerialListener.onSerialOpenSuccess();
            }
            startSendTask();

        } catch (Exception e) {

            Logger.getGlobal().severe("打开串口异常: " + e.getMessage());
            if (mOnSerialListener != null) {
                mOnSerialListener.onSerialOpenException(e);
            }
        }
    }

    public void close() {
        if (this.mReadThread != null) {
            this.mReadThread.interrupt();
        }
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
        this._isOpen = false;
    }

    public void send(byte[] bOutArray) {
        try {
            this.mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有串口路径
     *
     * @return 串口路径集合
     */
    public static List<String> getAllSerialPortPath() {
        SerialPortFinder mSerialPortFinder = new SerialPortFinder();
        String[] deviceArr = mSerialPortFinder.getAllDevicesPath();
        return new ArrayList<>(Arrays.asList(deviceArr));
    }

    //启动发送发送任务
    private void startSendTask() {
        cancelSendTask();//先检查是否已经启动了任务 ？ 若有则取消
        //每隔100毫秒检查一次 队列中是否有新的指令需要执行
        Log.e("startSendTask", "startSendTask");
        sendStrTask = scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {//串口未连接 退出
                if (mSerialPort == null) {
                    Log.e("startSendTask", "串口未初始化 退出");
                    return;
                }//串口未初始化 退出
                if (!_isOpen) {
                    open();
                    Log.e("startSendTask", "串口未连接 退出");
                    return;
                }
                String msg = queueMsg.poll();//取出指令
                if (msg == null || "".equals(msg)) {
//                    Log.e("startSendTask","无效指令 :"+msg+" 退出");
                    return;
                }//无效指令 退出

                byte[] bOutArray = ByteUtil.HexToByteArr(msg);
                send(bOutArray);

            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    //取消发送任务
    private void cancelSendTask() {
        if (sendStrTask == null) return;
        sendStrTask.cancel(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendStrTask = null;
    }

    public void sendHex(String sHex) {

        queueMsg.offer(sHex);//向队列添加指令
    }
//    public void sendHex(String sHex) {
//        byte[] bOutArray = ByteUtil.HexToByteArr(sHex);
//        send(bOutArray);
//    }

    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }

    private class ReadThread
            extends Thread {
        private ReadThread() {
        }

        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (SerialHelper.this.mInputStream == null) {
                        return;
                    }

                    byte[] buffer = getStickPackageHelper().execute(SerialHelper.this.mInputStream);
                    if (buffer != null && buffer.length > 0) {
                        ComBean ComRecData = new ComBean(SerialHelper.this.sPort, buffer, buffer.length);
//                        SerialHelper.this.onDataReceived(ComRecData);

                        if (mOnSerialListener != null) {
                            mOnSerialListener.onReceivedData(ComRecData, buffer.length);
                        }
                    }
//                    int available = SerialHelper.this.mInputStream.available();
//
//                    if (available > 0) {
//                        byte[] buffer = new byte['?'];
//                        int size = SerialHelper.this.mInputStream.read(buffer);
//                        if (size > 0) {
//                            ComBean ComRecData = new ComBean(SerialHelper.this.sPort, buffer, size);
//                            SerialHelper.this.onDataReceived(ComRecData);
//                        }
//                    } else {
//                        SystemClock.sleep(50);
//                    }

                } catch (Throwable e) {
                    if (e.getMessage() != null) {
                        Log.e("error", e.getMessage());
                    }
                    return;
                }
            }
        }
    }

    private class SendThread
            extends Thread {
        public boolean suspendFlag = true;

        private SendThread() {
        }

        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (this.suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                SerialHelper.this.send(SerialHelper.this.getbLoopData());
                try {
                    Thread.sleep(SerialHelper.this.iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    public int getBaudRate() {
        return this.iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (this._isOpen) {
            return false;
        }
        this.iBaudRate = iBaud;
        return true;
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    public int getStopBits() {
        return this.stopBits;
    }

    public boolean setStopBits(int stopBits) {
        if (this._isOpen) {
            return false;
        }
        this.stopBits = stopBits;
        return true;
    }

    public int getDataBits() {
        return this.dataBits;
    }

    public boolean setDataBits(int dataBits) {
        if (this._isOpen) {
            return false;
        }
        this.dataBits = dataBits;
        return true;
    }

    public int getParity() {
        return this.parity;
    }

    public boolean setParity(int parity) {
        if (this._isOpen) {
            return false;
        }
        this.parity = parity;
        return true;
    }

    public int getFlowCon() {
        return this.flowCon;
    }

    public boolean setFlowCon(int flowCon) {
        if (this._isOpen) {
            return false;
        }
        this.flowCon = flowCon;
        return true;
    }

    public String getPort() {
        return this.sPort;
    }

    public boolean setPort(String sPort) {
        if (this._isOpen) {
            return false;
        }
        this.sPort = sPort;
        return true;
    }

    public boolean isOpen() {
        return this._isOpen;
    }

    public byte[] getbLoopData() {
        return this._bLoopData;
    }

    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    public void setHexLoopData(String sHex) {
        this._bLoopData = ByteUtil.HexToByteArr(sHex);
    }

    public int getiDelay() {
        return this.iDelay;
    }

    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    public void startSend() {
        if (this.mSendThread != null) {
            this.mSendThread.setResume();
        }
    }

    public void stopSend() {
        if (this.mSendThread != null) {
            this.mSendThread.setSuspendFlag();
        }
    }

//    protected abstract void onDataReceived(ComBean paramComBean);

    private AbsStickPackageHelper mStickPackageHelper = new BaseStickPackageHelper();

    public AbsStickPackageHelper getStickPackageHelper() {
        return mStickPackageHelper;
    }

    public void setStickPackageHelper(AbsStickPackageHelper mStickPackageHelper) {
        this.mStickPackageHelper = mStickPackageHelper;
    }


    private OnSerialListener mOnSerialListener;

    /**
     * 设置串口监听
     *
     * @param onSerialListener 串口监听
     */
    public void setOnSerialListener(OnSerialListener onSerialListener) {
        this.mOnSerialListener = onSerialListener;
    }

    /**
     * 串口监听
     */
    public interface OnSerialListener {

        /**
         * 串口数据返回
         */
        void onReceivedData(ComBean data, int size);

        /**
         * 串口打开成功
         */
        void onSerialOpenSuccess();

        /**
         * 串口打开异常
         */
        void onSerialOpenException(Exception e);
    }


}
