package com.ycmachine.smartdevice.network.net;


import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TCPAsyncNetUtils {

//    final static String BASE_URL = "https://admin3.51sioc.com/api"; // 河南
//    final static String BASE_URL = "https://cloudtestadmin.wteam.club/api"; // 测试
//    final static String BASE_URL = "https://cw.tmmbuy.com/api"; // 淘喵喵
//    final static String BASE_URL = "http://42.194.183.243:8097/api"; // 测试
    final static String BASE_URL = "https://thj.wteam.club/api"; // 我们的
//    final static String BASE_URL = "https://userbg.czsgaf.com/api"; // 三根


    public interface Callback {
        void onResponse(String response) throws JSONException, MalformedURLException, InterruptedException;
    }
    /*
     * GET方法y
     *
     * @param url
     * @param callback
     */
    public static void get(String url, final Callback callback) {
        try {
            Looper.prepare();
        }catch (Exception ignored){

        }

        url = BASE_URL + url;
        final Handler handler = new Handler();
        String finalUrl = url;
        new Thread(new Runnable() {

            @Override
            public void run() {
                OkHttpClient   mOkHttpClient = new OkHttpClient();
                Response response = null;
                Request request = new Request.Builder()
                        .url(finalUrl)   // 设置请求地址
                        .get()                          // 使用 Get 方法
                        .build();

                try {
                    response = mOkHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String result = null;
                try {
                    if (response != null) {
                        result = response.body().string();
                    }else{
                        try {
                            callback.onResponse(null);
                        } catch (Exception ignored) {

                        }
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }


//                final String response = NetUtils.get(finalUrl);
                String finalResponse = result;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onResponse(finalResponse);
                        } catch (Exception e) {

////                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * POST方法
     *
     * @param url
     * @param content
     * @param callback
     */
    public static void post(String url, final String content, final Callback callback) {
        url = BASE_URL + url;
        final Handler handler = new Handler();
        String finalUrl = url;






        new Thread(new Runnable() {
            @Override
            public void run() {

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");//"类型,字节码"
                //字符串
                String value = content;
                //1.创建OkHttpClient对象
                OkHttpClient  okHttpClient = new OkHttpClient();
                //2.通过RequestBody.create 创建requestBody对象
                RequestBody requestBody =RequestBody.create(mediaType, value);
                //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
                Request request = new Request.Builder().url(finalUrl).post(requestBody).build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                final String response = NetUtils.post(finalUrl, content);
                String finalResponse = result;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onResponse(finalResponse);
                        } catch (Exception e) {
//                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();

    }

}
