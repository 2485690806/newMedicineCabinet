package leesche.smartrecycling.base.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UpdateDownLoadUtil {
    private String mTempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DownloadFile";

    public interface DownLoadFileStateLister {
        void onComplete(File file);

        void onError();
    }

    public void downLoadFile(final String httpUrl, final DownLoadFileStateLister onDownloadFileStateLister) {
        new Thread(new Runnable() {
            public void run() {
                if (TextUtils.isEmpty(httpUrl)) {
//                    Logger.e("DownloadFile 异常:httpUrl为空");
                    onDownloadFileStateLister.onError();
                    throw new IllegalArgumentException();
                }
//                Logger.e("清空下载目录的文件");
                FileUtil.deleteDirectory(mTempPath, false);
                File file = new File(mTempPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (httpUrl.endsWith("apk")) {
                    file = new File(mTempPath + File.separator + "update.apk");
                } else if (httpUrl.endsWith("zip")) {
                    file = new File(mTempPath + File.separator + "update.zip");
                }
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
                    onDownloadFileStateLister.onError();
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
//                        Logger.e("DownloadFile 异常");
                        onDownloadFileStateLister.onError();
                    }
                }
                onDownloadFileStateLister.onComplete(file);
            }
        }).start();

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


}
