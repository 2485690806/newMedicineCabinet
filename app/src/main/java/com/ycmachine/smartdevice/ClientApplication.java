package com.ycmachine.smartdevice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.faceunity.core.callback.OperateCallback;
import com.faceunity.core.faceunity.FURenderManager;
import com.faceunity.core.utils.FULogger;
import com.leesche.logger.Logger;
import com.xiaoyezi.networkdetector.NetworkDetector;
import com.ycmachine.smartdevice.network.api.OkHttpProvider;
import com.ycmachine.smartdevice.storage.AuthStorage;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import leesche.smartrecycling.base.BaseApplication;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.utils.DatabaseUtil;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.SharedPreferencesUtils;
import leesche.smartrecycling.base.utils.authpack;
import leesche.smartrecycling.base.websocket.core.Config;
import leesche.smartrecycling.base.websocket.core.RxWebSocket;
import okhttp3.OkHttpClient;

public class ClientApplication extends BaseApplication {

    public static final String TAG = ClientApplication.class.getSimpleName();

    static ClientApplication instance;
    protected final int MSG_INIT_DATABASE = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FileUtil.createDir(Constants.BASE_CACHE_DIR);
        NetworkDetector.getInstance().init(this);
        setTestServer(Constants.IS_TEST);
        initConfig();

        new Thread(new Runnable() {
            @Override
            public void run() {
                registerFURender(ClientApplication.this);  //注册美颜
            }
        }).start();
//        OssUploadFile.getInstance().initOSS(this);
        if (isDebug(this)) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
        initWebSocketConfig();
        initHandlerThread();
        mSubThreadHandler.sendEmptyMessage(MSG_INIT_DATABASE);

        // 恢复认证信息（如存在则设置到网络层）
        restoreAuthFromStorage();

        if(!BuildConfig.isTestSelf){

            RxJavaPlugins.setErrorHandler(e -> {
                if (e instanceof UndeliverableException) {
                    Log.e("RxJava", "Undeliverable exception: " + e.getCause());
                } else {
                    Log.e("RxJava", "exception: " + e.getCause());
//                    Thread.currentThread().getUncaughtExceptionHandler()
//                            .uncaughtException(Thread.currentThread(), e);
                }
            });
        }
    }

    public boolean isDebug(Context context) {
        boolean isDebug = context.getApplicationInfo() != null &&
                (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        return isDebug;
    }

    /**
     * 设置服务器环境
     *
     * @param isTest false为正式环境
     */
    private void setTestServer(boolean isTest) {
        if (isTest) {
            if (!Constants.isSGServer) {
                Constants.BASE_URL = Constants.PROTOCOL_HEADER + Constants.DEV_HOST;
                Constants.WEB_SOCKET_URL = "ws:" + Constants.DEV_HOST + "websocket?";
            } else {
                Constants.BASE_URL = Constants.PROTOCOL_HEADER + "//devcloud.sgrecycle.tech/";
                Constants.WEB_SOCKET_URL = "ws://devcloud.sgrecycle.tech/websocket?";
            }
        } else {
            if (!Constants.isSGServer) {
                Constants.BASE_URL = Constants.PROTOCOL_HEADER + Constants.HOST;
                Constants.WEB_SOCKET_URL = "ws://app.ws.youyiyun.tech?";
            } else {
                Constants.BASE_URL = Constants.PROTOCOL_HEADER + "//cloud.tomrobots.tech/";
                Constants.WEB_SOCKET_URL = "ws://cloud.tomrobots.tech/websocket?";
            }
        }

        Logger.i("setTestServer"+Constants.BASE_URL);
    }

    private void initConfig() {
        if (!SharedPreferencesUtils.contains(getApplicationContext(), SharedPreferencesUtils.WELCOME_AUDIO_NAME)) {
            SharedPreferencesUtils.put(getApplicationContext(), SharedPreferencesUtils.WELCOME_AUDIO_NAME, "");
        }
        if (!SharedPreferencesUtils.contains(getApplicationContext(), SharedPreferencesUtils.REBOOT_COUNT)) {
            SharedPreferencesUtils.put(getApplicationContext(), SharedPreferencesUtils.REBOOT_COUNT, 0);
        }
        if (!SharedPreferencesUtils.contains(getApplicationContext(), SharedPreferencesUtils.INIT_WEIGHT)) {
            SharedPreferencesUtils.put(getApplicationContext(), SharedPreferencesUtils.INIT_WEIGHT, 0);
        }
    }

    /**
     * 从本地存储恢复认证凭证，并设置到OkHttp拦截器
     */
    private void restoreAuthFromStorage() {
        String access = AuthStorage.getAccessToken(getApplicationContext());
        if (access != null && !access.isEmpty()) {
            OkHttpProvider.setAccessToken(access);
            Logger.i("[Auth] restored access token from storage");
        }
    }

    /**
     * 初始化web socket 配置
     */
    private void initWebSocketConfig() {
        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).pingInterval(10, TimeUnit.SECONDS).build();
//        init config
        Config config = new Config.Builder()
                .setClient(client)
                .setShowLog(true, "webSocket")
                .setReconnectInterval(15, TimeUnit.SECONDS)
//                .setSSLSocketFactory(yourSSlSocketFactory, yourX509TrustManager)
                .build();
        RxWebSocket.setConfig(config);
    }

    private HandlerThread mHandlerThread;
    private Handler mSubThreadHandler; //子线程中的Handler实例。

    private void initHandlerThread() {
        mHandlerThread = new HandlerThread("handler_thread");//创建HandlerThread实例
        mHandlerThread.start();//开始运行线程
        Looper loop = mHandlerThread.getLooper();//获取HandlerThread线程中的Looper实例
        mSubThreadHandler = new Handler(loop) {//创建Handler与该线程绑定
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_INIT_DATABASE:
//                        SystemClock.sleep(2000);
                        Logger.i("【INIT DATABASE】 初始化数据库");
                        DatabaseUtil.getInstance().setDatabase(getApplicationContext());//配置数据库操作
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private int registerFURenderNum = 0;


    private void registerFURender(Context context) {
        registerFURenderNum++;
        if (registerFURenderNum > 10){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    new MyToast(context, true).show("美颜服务初始化失败，两分钟后自动重启", 5000);
//                }
//            });
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    zcApi.reboot();
//                }
//            }, 60 * 1000);
            return;
        }
//        FURenderManager.setKitDebug(FULogger.LogLevel.DEBUG);
        FURenderManager.setCoreDebug(FULogger.LogLevel.OFF);
        FURenderManager.registerFURender(context, authpack.A(), new OperateCallback() {
            @Override
            public void onSuccess(int code, String msg) {
//                BEAUTY_STATUS = 1;
                Log.d("registerFURender", "success:" + msg);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
//                BEAUTY_STATUS = 0;
                Log.e("registerFURender", "errCode:" + errCode + "   errMsg:" + errMsg);
                registerFURender(context);
            }
        });
    }


}
