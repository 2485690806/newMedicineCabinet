package leesche.smartrecycling.base.websocket;

import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leesche.logger.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import leesche.smartrecycling.base.RemoteDataManager;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.http.OssUploadFile;
import leesche.smartrecycling.base.strategy.DevContext;
import leesche.smartrecycling.base.utils.AutoInstaller;
import leesche.smartrecycling.base.utils.DateUtil;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.websocket.core.RxWebSocket;
import leesche.smartrecycling.base.websocket.core.WebSocketSubscriber;
import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketHelper implements IWebSocketHelper {

    public static final String TAG = WebSocketHelper.class.getSimpleName();

    WebSocket mWebSocket;
    FragmentActivity activity;
    Handler handler;
    WebSocketSubscriber webSocketSubscriber;

    private static WebSocketHelper instance;

    private boolean webSocketHaveOpen = false;

    public static WebSocketHelper getInstance() {
        if (instance == null) {
            instance = new WebSocketHelper();
        }
        return instance;
    }

    public boolean isWebSocketHaveOpen() {
        return webSocketHaveOpen;
    }

    public void setWebSocketHaveOpen(boolean webSocketHaveOpen) {
        this.webSocketHaveOpen = webSocketHaveOpen;
        if (!webSocketHaveOpen) {
            disConnect();
        }
    }

    @Override
    public void initServerWebSocket(final FragmentActivity _activity, final String url) {
        activity = _activity;
        handler = null;
        if (webSocketSubscriber != null) {
            webSocketSubscriber.dispose();
            webSocketSubscriber = null;
        }
        webSocketSubscriber = produceWebSocketSubscriber();
        RxWebSocket.get(url).subscribe(webSocketSubscriber);
    }

    private WebSocketSubscriber produceWebSocketSubscriber() {
        WebSocketSubscriber webSocketSubscriber = new WebSocketSubscriber() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket) {
                Constants.NET_LEVEL = Constants.NetStatus.NORMAL_NET;
                mWebSocket = webSocket;
                webSocketHaveOpen = true;
//                ControlManager2Impl.getInstance(activity).setUpIotPassage(webSocket);

//                EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.WEB_SOCKET_HAVE_OPEN));
            }

            @Override
            public void onMessage(@NonNull String text) {
                if ("pong".equalsIgnoreCase(text) || "ping".equalsIgnoreCase(text)) return;
                Logger.i("【Web Socket】received " + text);
                if (!text.startsWith("{")) {
                    return;
                }
                JsonObject resultJson = new JsonParser().parse(text).getAsJsonObject();
                String type = resultJson.get("msg_type").getAsString();
                resultJson.addProperty("state", "ok");
                send(resultJson.toString());
                JsonObject dataJson = null;
                switch (type) {
                    case Constants.aLiPushMsgType.REFRESH_PRODUCT_LIST:
                        //后台刷新
                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.GET_DEVICE_INFO));
                        break;
                    case Constants.aLiPushMsgType.APP_UPGRADE:
                        //APP 更新
                        String updateUrl = resultJson.get("data").getAsString();
                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.UPDATE_APP, updateUrl));
                        break;
                    case Constants.aLiPushMsgType.USER_LOGIN:
                        //用户登录
                        dataJson = resultJson.get("data").getAsJsonObject();
//                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_SCAN_LOGIN, dataJson));
                        break;
                    case Constants.aLiPushMsgType.FIRMWARE_UPDATE:
                        //固件更新
                        String firmwareUrl = resultJson.get("data").getAsString();
//                        EventBus.getDefault().post(new SerialMsgEvent(EventType.SerialPortEvent.FIRMWARE_UPDATE, firmwareUrl));
                        break;
                    case Constants.aLiPushMsgType.UPDATE_DEVICESTATUS:
                        // 更新设备状态
                        Logger.i("【Web Socket】update device status: " + resultJson.get("data").getAsString());
                        //固件更新
                        String update_deviceStatus = resultJson.get("data").getAsString();

//                        EventBus.getDefault().post(new SerialMsgEvent(EventType.RVMEvent.LOCK_MACHINE, update_deviceStatus));

                        break;
                    case Constants.aLiPushMsgType.REBOOT_APP:
                        //重启APP
                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.REBOOT_APP));
                        break;
                    case Constants.aLiPushMsgType.REBOOT_SYS:
                        //重启安卓系统
                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.REBOOT_SYS));
                        break;
                    case Constants.aLiPushMsgType.REMOTELY_MODIFY_CONFIG:
                        //远程修改APP配置文件
                        String data = resultJson.get("data").getAsString();
//                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.REMOTE_MODIFY_CONFIG, data));
                        break;
//                    case Constants.aLiPushMsgType.WATCH_POC_VIDEO:
//                        String data_video = resultJson.get("data").getAsString();
//                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.UPLOAD_POC_VIDEO, resultJson));
//                        break;
                    case Constants.aLiPushMsgType.UPLOAD_LOGGER_REQUEST:
                        RemoteDataManager.getInstance().uploadSystemLogger(resultJson.get("data").getAsString(), false);
                        break;
                    case Constants.aLiPushMsgType.UPLOAD_LOGGER_CRASH:
                        RemoteDataManager.getInstance().uploadCrashLogger2(resultJson.get("data").getAsString());
                        break;
                    case Constants.aLiPushMsgType.SYSC_FILE:
                        //同步文件
                        String path = resultJson.get("data").getAsString();
                        OssUploadFile.getInstance().checkFileToDownload2(activity, path + "/");
                        break;
                    case Constants.aLiPushMsgType.POC_UPLOAD:
                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.PROCESS_WEBSOCKET_MSG, resultJson));
                        break;
                    case Constants.aLiPushMsgType.APP_CONFIG:
                        List<String> list = new ArrayList<>();
                        list.add("https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/app_config/" + Constants.AGENT_ID + "/yyy_config.zip");
                        OssUploadFile.getInstance().downloadFaceFile(activity, list, Constants.BASE_CACHE_DIR);
                        break;
                    case Constants.aLiPushMsgType.REMOVE_LOCAL_FILE:
                        FileUtil.deleteDirectory(resultJson.get("data").getAsString(), true);//删除本地文件
                        break;
                    case Constants.aLiPushMsgType.EXC_INTERNAL_CMD:
                        uploadAnrInfo();
                        break;
                    case Constants.aLiPushMsgType.MONITOR_CHECK:
//                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.WEBSOCKET_CHECK_MONITOR, resultJson));
                        break;
                    case Constants.aLiPushMsgType.NOTIFY_FILE_UPLOAD:
                        RemoteDataManager.getInstance().uploadSystemFile(resultJson.get("data").getAsString());
                        break;
                    case Constants.aLiPushMsgType.INSTALL_APP:
                        String batteryServiceAppUrl = resultJson.get("data").getAsString();
                        FileDownloader.getImpl().create(batteryServiceAppUrl).setPath(Constants.BASE_CACHE_DIR, true).setListener(fileDownloadListener).start();
                    default:
                        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.PROCESS_WEBSOCKET_MSG, resultJson));
                        break;
                }
            }

            @Override
            public void onMessage(@NonNull ByteString byteString) {

            }

            @Override
            protected void onReconnect() {
                Constants.NET_LEVEL = Constants.NetStatus.NO_NET;
                Logger.w("【Web Socket】" + "link again");
            }

            @Override
            protected void onClose() {
                webSocketHaveOpen = false;
                Logger.w("【Web Socket】" + "have closed");
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketHelper.getInstance().initServerWebSocket(activity,
                                Constants.WEB_SOCKET_URL + "groupId=" + Constants.MAC_ADDRESS
                                        + "&userId=" + Constants.MAC_ADDRESS + "&module=recycling");
                    }
                }, 15 * 1000);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                webSocketHaveOpen = false;
                Logger.e("【Web Socket】" + "error: " + e.getMessage());
            }
        };
        return webSocketSubscriber;
    }

    private void uploadAnrInfo() {
        String curDate = DateUtil.getCurTime(DateUtil.DATE_FORMAT3);
        String filePath = "/sdcard/SmartRecycling/logger/traces" + curDate + ".txt";
        String cmdStr = "cat /data/anr/traces.txt > " + filePath;
        DevContext.getInstance().getDevStrategy().exeRootCmdSilent(cmdStr);
        RemoteDataManager.getInstance().uploadCrashLogger(filePath);
    }

    private final int MIN_DELAY_TIME = 15 * 1000;  // 两次点击间隔不能少于1000ms
    private long lastClickTime = 0;

    public boolean isFastRequestUpload() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
            lastClickTime = currentClickTime;
        }
        return flag;
    }

//    @Override
//    public void connect(String url) {
//        if (mSubscription != null) return;
//        mSubscription = (Subscription) RxWebSocket.get(url).subscribe(new Consumer<WebSocketInfo>() {
//            @Override
//            public void accept(WebSocketInfo webSocketInfo) throws Exception {
//                mWebSocket = webSocketInfo.getWebSocket();
//                if (webSocketInfo.isOnOpen()) {
//                    if (Constants.IS_TEST) Log.d("MainActivity", " on WebSocket open");
//                } else {
//                    String string = webSocketInfo.getString();
//                    if (string != null) {
//                        if (Constants.IS_TEST) Log.d("MainActivity", string);
//                    }
//
//                    ByteString byteString = webSocketInfo.getByteString();
//                    if (byteString != null) {
//                        if (Constants.IS_TEST) Log.d("MainActivity",
//                                "webSocketInfo.getByteString():" + byteString);
//                    }
//                }
//            }
//        });
//    }

    @Override
    public boolean send(String msg) {
        if (mWebSocket != null) {
            return mWebSocket.send(msg);
        }
        return false;
    }

    @Override
    public void disConnect() {
        if (mWebSocket != null) {
            mWebSocket.close(1000, "The client application is stopping");
        }
        webSocketHaveOpen = false;
    }

    FileDownloadListener fileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask baseDownloadTask, int i, int i1) {

        }

        @Override
        protected void progress(BaseDownloadTask baseDownloadTask, int i, int i1) {
//            Logger.i("【SmartPrints】battery service download progress: " + i);
        }

        @Override
        protected void completed(BaseDownloadTask baseDownloadTask) {
            String filePath = baseDownloadTask.getPath() + File.separator + baseDownloadTask.getFilename();
            Logger.i("【FFMpeg】 fill downloaded path: " + filePath);
            if (filePath.contains("apk")) {
                AutoInstaller.getDefault(activity).installUseRoot(filePath);
            }
        }

        @Override
        protected void paused(BaseDownloadTask baseDownloadTask, int i, int i1) {

        }

        @Override
        protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {

        }

        @Override
        protected void warn(BaseDownloadTask baseDownloadTask) {

        }
    };
}
