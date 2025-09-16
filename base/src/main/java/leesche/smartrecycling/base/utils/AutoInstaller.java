package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ZtlApi.ZtlManager;


public class AutoInstaller extends Handler {

    private static final String TAG = "AutoInstaller";
    private static volatile AutoInstaller mAutoInstaller;
    private Context mContext;
    private String mTempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AutoInstaller";

    public enum MODE {
        ROOT_ONLY,
        AUTO_ONLY,
        BOTH
    }

    private MODE mMode = MODE.BOTH;

    private AutoInstaller(Context context) {
        mContext = context;
    }

    public static AutoInstaller getDefault(Context context) {
        if (mAutoInstaller == null) {
            synchronized (AutoInstaller.class) {
                if (mAutoInstaller == null) {
                    mAutoInstaller = new AutoInstaller(context);
                }
            }
        }
        return mAutoInstaller;
    }


    public interface OnStateChangedListener {
        void onStart();

        void onFileDownloadProgress(double progress);

        void onComplete();

        void onNeed2OpenService();
    }

    private OnStateChangedListener mOnStateChangedListener;

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    public boolean installUseRoot(String filePath) {
        if (TextUtils.isEmpty(filePath))
            throw new IllegalArgumentException("Please check apk file path!");
        boolean result = false;
        Process process = null;
        OutputStream outputStream = null;
        BufferedReader errorStream = null;
        try {
            process = Runtime.getRuntime().exec("su");
            outputStream = process.getOutputStream();

            String command = "pm install -r -d " + filePath + "\n";
            outputStream.write(command.getBytes());
            outputStream.flush();
            outputStream.write("exit\n".getBytes());
            outputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder msg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                msg.append(line);
            }
            Log.d(TAG, "install msg is " + msg);
            if (!msg.toString().contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                outputStream = null;
                errorStream = null;
                process.destroy();
            }
        }
        return result;
    }

    public boolean installUseRootZTL(File file) {
        if (TextUtils.isEmpty(file.getAbsolutePath()))
            throw new IllegalArgumentException("Please check apk file path!");
        String filePath = "/data/local/tmp/Foodpanda-1.0.1.apk";
        String command = "pm install -r -d " + filePath + "\n";
//        String command = "cat " + file.getAbsolutePath() + " | pm install -S " + file.length();
        ZtlManager.GetInstance().execRootCmdSilent(command);
        return false;
    }

    private void installUseAS(String filePath) {
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
//        if (!isAccessibilitySettingsOn(mContext)) {
//            toAccessibilityService();
//            sendEmptyMessage(3);
//        }
    }

    private void toAccessibilityService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        mContext.startActivity(intent);
    }


    public void install(final String filePath) {
        if (TextUtils.isEmpty(filePath) || !filePath.endsWith(".apk"))
            throw new IllegalArgumentException("not a correct apk file path");
        new Thread(new Runnable() {
            @Override
            public void run() {

                sendEmptyMessage(1);

                switch (mMode) {
                    case BOTH:
                        if (!DeviceUtil.checkRooted() || !installUseRoot(filePath))
                            installUseAS(filePath);
                        break;
                    case ROOT_ONLY:
                        installUseRoot(filePath);
                        break;
                    case AUTO_ONLY:
                        installUseAS(filePath);
                }
                sendEmptyMessage(0);

            }
        }).start();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                if (mOnStateChangedListener != null)
                    mOnStateChangedListener.onComplete();
                break;
            case 1:
                if (mOnStateChangedListener != null)
                    mOnStateChangedListener.onStart();
                break;
            case 2:
                if (mOnStateChangedListener != null)
                    mOnStateChangedListener.onFileDownloadProgress((double) msg.obj);
                break;
            case 3:
                if (mOnStateChangedListener != null)
                    mOnStateChangedListener.onNeed2OpenService();
                break;

        }
    }

    public void install(File file) {
        if (file == null)
            throw new IllegalArgumentException("file is null");
        install(file.getAbsolutePath());
    }


    public void installFromUrl(final String httpUrl) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                sendEmptyMessage(1);
//                File file = downLoadFile(httpUrl);
//                install(file);
//            }
//        }).start();
        downLoadNewAppToInstall(httpUrl);
    }

    private void downLoadNewAppToInstall(String httpUrl) {
        sendEmptyMessage(1);
        if (TextUtils.isEmpty(httpUrl)) throw new IllegalArgumentException();
        File file = new File(mTempPath);
        if (!file.exists()) file.mkdirs();
//        file = new File(mTempPath + File.separator + "update.apk");
        String fileName = httpUrl.substring(httpUrl.lastIndexOf("/") + 1);
        String localPath = mTempPath + File.separator + fileName;
        if (new File(localPath).exists()) {
//            ToastUtil.showSuccessMsg("正在卸载重装，请稍后...", Gravity.TOP);
            install(new File(localPath));
            return;
        }
        FileDownloader.getImpl()
                .create(httpUrl)
                .setPath(localPath)
                .setAutoRetryTimes(5)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        Message message = new Message();
                        message.what = 2;
                        double progress = CalcUtil.divide((double) soFarBytes, (double) totalBytes) * 100;
                        message.obj = progress;
                        sendMessage(message);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        sendEmptyMessage(0);
//                        Logger.i("文件下载被终止...");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        install(new File(task.getPath()));
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        sendEmptyMessage(0);
//                        Logger.i("下载文件出错："+ e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
//                        Logger.i("文件重复被下载...");
                    }
                }).start();
    }

    private File downLoadFile(String httpUrl) {
        if (TextUtils.isEmpty(httpUrl)) throw new IllegalArgumentException();
        File file = new File(mTempPath);
        if (!file.exists()) file.mkdirs();
        file = new File(mTempPath + File.separator + "update.apk");
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                SSLContext sslContext = getSLLContext();
                if (sslContext != null) {
                    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                }
            }
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);
            connection.connect();
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
                if (connection != null)
                    connection.disconnect();
            } catch (IOException e) {
                inputStream = null;
                outputStream = null;
            }
        }
        return file;
    }

    private SSLContext getSLLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public static class Builder {

        private MODE mode = MODE.BOTH;
        private Context context;
        private OnStateChangedListener onStateChangedListener;
        private String directory = Environment.getExternalStorageDirectory().getAbsolutePath();

        public Builder(Context c) {
            context = c;
        }

        public Builder setMode(MODE m) {
            mode = m;
            return this;
        }

        public Builder setOnStateChangedListener(OnStateChangedListener o) {
            onStateChangedListener = o;
            return this;
        }

        public Builder setCacheDirectory(String path) {
            directory = path;
            return this;
        }

        public AutoInstaller build() {
            AutoInstaller autoInstaller = new AutoInstaller(context);
            autoInstaller.mMode = mode;
            autoInstaller.mOnStateChangedListener = onStateChangedListener;
            autoInstaller.mTempPath = directory;
            return autoInstaller;
        }

    }


}
