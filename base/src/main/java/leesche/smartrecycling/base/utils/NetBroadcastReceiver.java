package leesche.smartrecycling.base.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.Objects;

import leesche.smartrecycling.base.BaseActivity;

public class NetBroadcastReceiver extends BroadcastReceiver {

    public NetChangeListener listener = BaseActivity.listener;

    private long lastUpdateTime = 0;

    private static final long MIN_UPDATE_INTERVAL = 1000; // 1秒间隔

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > MIN_UPDATE_INTERVAL) {
                lastUpdateTime = currentTime;
                Log.i("NetBroadcastReceiver", "NetBroadcastReceiver changed");
                int netWorkState = NetUtil.getNetWorkState(context);
                if (listener != null) {
                    listener.onChangeListener(netWorkState);
                }
            }
        }
    }

    // 自定义接口
    public interface NetChangeListener {
        void onChangeListener(int status);
    }

}