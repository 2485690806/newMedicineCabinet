package com.ycmachine.smartdevice.network.socket;


import android.content.Context;
import android.util.Log;

import com.ycmachine.smartdevice.activity.AcitivityScreenLessDeviceSetting;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class MyWebSocketClient extends WebSocketClient {

    private String id; // 用于标识设备的自定义 id
    private Context context;

    public Context getPayContext() {
        return payContext;
    }

    public void setPayContext(Context payContext) {
        this.payContext = payContext;
    }

    private Context payContext;

    public Context getPayForPrintContext() {
        return payForPrintContext;
    }

    public void setPayForPrintContext(Context payForPrintContext) {
        this.payForPrintContext = payForPrintContext;
    }

    private Context payForPrintContext;


    private static final String TAG = "MyWebSocketClient";

    public MyWebSocketClient(String serverUri, String id, Context context) throws URISyntaxException {
        super(new URI(serverUri));
        this.id = id;
        this.context = context;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        // 连接成功，可以发送心跳包
        sendHeartbeat();
    }

    @Override
    public void onMessage(String message) {
        System.out.println("长连接message：" + message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // 连接关闭，可进行自动重连
        tryReconnect();
    }

    @Override
    public void onError(Exception ex) {
        // 连接发生错误
        ex.printStackTrace();
    }

    // 发送心跳包
    private void sendHeartbeat() {
        // 这里使用定时器定期发送心跳包
        // 示例：每隔 30 秒发送一次心跳包
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                if (isOpen()) {
//                    if(webSocketClient!=null){
//                       try {
//                           send("heartbeat");
//                       }catch (Exception e){
//                           e.printStackTrace();
//                       }
//                    }
//                }
                if (AcitivityScreenLessDeviceSetting.webSocketClient != null) {
                    if (AcitivityScreenLessDeviceSetting.webSocketClient.isClosed()) {
                        tryReconnect();
                    }else{
                        try {
                            send("heartbeat");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    //如果client已为空，重新初始化websocket
                    reconnect();
                }

            }
        }, 0, 30000); // 30 秒
    }

    // 自动重连
    public void tryReconnect() {
        // 这里可以实现自动重连逻辑
        // 示例：延时 5 秒后自动重连
        Log.i(TAG, "长连接断开，尝试自定重连");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isOpen()) {
                    try {
                        reconnectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, 5000); // 5 秒
    }



}


