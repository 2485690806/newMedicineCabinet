package leesche.smartrecycling.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.UploadSignal;
import leesche.smartrecycling.base.utils.NetworkUtil;

public class NetMonitor {

    private static NetMonitor netMonitor;
    NetworkConnectChangedReceiver networkConnectChangedReceiver;
    TelephonyManager mTelephonyManager;
    private int netLevel = 0;
    OnNetStatusListener onNetStatusListener;
    String curNetTypeName = "4G";

    public int getNetLevel() {
        return netLevel;
    }

    public void setOnNetStatusListener(OnNetStatusListener onNetStatusListener) {
        this.onNetStatusListener = onNetStatusListener;
    }

    public interface OnNetStatusListener {
        void onSignalLevel(UploadSignal uploadSignal);

        void onNetTypeName(String typeName, boolean isConnected);
    }

    public static NetMonitor getInstance() {
        if (netMonitor == null) {
            netMonitor = new NetMonitor();
        }
        return netMonitor;
    }

    //网络状态监听广播
    public class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    checkActiveNet(context);
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
//                    Logger.i("【WIFI Status】" + state);
                    checkActiveNet(context);
                    break;
                case WifiManager.RSSI_CHANGED_ACTION:
                    int RSSI = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -1000);
                    int level = WifiManager.calculateSignalLevel(RSSI, 5);
                    if (netLevel != level) {
//                        Logger.i("【WIFI Strength】" + level);
                        uploadSignalToServer(RSSI, level);
                    }
                    break;
            }
//            checkNetIsConnect(context);
        }
    }

    private void checkActiveNet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            handlerNetType(networkInfo);
        } else {
            Constants.NET_LEVEL = Constants.NetStatus.NO_NET;
            if (onNetStatusListener != null) {
                onNetStatusListener.onNetTypeName(curNetTypeName, false);
            }
//            Logger.i("without net can use...");
        }
    }

    private void checkNetIsConnect(Context context) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                Logger.i("【网络连接状态】WIFI已连接,移动数据已连接");
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
//                Logger.i("【网络连接状态】WIFI已连接,移动数据已断开");
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                Logger.i("【网络连接状态】WIFI已断开,移动数据已连接");
            } else {
//                Logger.i("【网络连接状态】WIFI已断开,移动数据已断开");
            }
        } else {
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
            }
            if (TextUtils.isEmpty(sb.toString())) {
                sb.append("无网络连接");
            }
//            Logger.i("【网络连接状态】" + sb.toString());
        }
    }

    private class PhoneStatListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int dbm = signalStrength.getGsmSignalStrength();
//            int aus = Math.abs((-113 + (2 * dbm)));
            int level = NetworkUtil.ausToLevel(dbm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                level = signalStrength.getLevel();
            }
            try {
                Method getLteLevelMethod = signalStrength.getClass().getMethod("getLevel");
                level = (int) getLteLevelMethod.invoke(signalStrength);
            } catch (Throwable e) {
//                Logger.i("onSignalStrengthsChanged:" + e.getMessage());
            }
            if (level != netLevel) {
//                Logger.i("（网络）信号强度： " + level);
                netLevel = level;
                uploadSignalToServer(dbm, level);
            }
        }
    }

    private void uploadSignalToServer(int dbm, int level) {
        UploadSignal uploadSignal = new UploadSignal();
//        if ("以太网".equals(curNetTypeName)) {
//            uploadSignal.setSignalType("多重网络");
//        } else {
        uploadSignal.setSignalType(curNetTypeName);
//        }
        uploadSignal.setSignalLattice(dbm);
        uploadSignal.setSignalIntensity(level);
        if (onNetStatusListener != null) onNetStatusListener.onSignalLevel(uploadSignal);
    }

    private void handlerNetType(NetworkInfo networkInfo) {
        int type = getNetType(networkInfo);
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                curNetTypeName = getMobileNewType();
                break;
            case ConnectivityManager.TYPE_WIFI:
                if ("WIFI".equalsIgnoreCase(networkInfo.getTypeName())) {
                    curNetTypeName = "WIFI";

                }
                if ("Ethernet".equalsIgnoreCase(networkInfo.getTypeName())) {
                    curNetTypeName = "Ethernet";
                }
                break;
            case ConnectivityManager.TYPE_ETHERNET:
                curNetTypeName = "Ethernet";
                break;
            default:
                curNetTypeName = type == -1 ? "without Net" : "UnKnown";
                break;
        }
        String connectStatus = networkInfo.isConnected() ? " Connected" : " Disconnect";
//        Logger.i("【Net Type】" + curNetTypeName + connectStatus);
        if (onNetStatusListener != null)
            onNetStatusListener.onNetTypeName(curNetTypeName, networkInfo.isConnected());
    }

    public void addNetListener(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        context.registerReceiver(networkConnectChangedReceiver, filter);
        mTelephonyManager.listen(new PhoneStatListener(), PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private String getMobileNewType() {
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "MONET";
        }
    }

    // 获取当前网络类型.
    public static int getNetType(NetworkInfo info) {
        return info == null ? -1 : info.getType();
    }

    public void displayNetCannotUse(TextView tv_net) {
        if (netLevel > 0 && Constants.NET_LEVEL == Constants.NetStatus.NO_NET) {
            tv_net.setText("X");//网络无法使用
        }
    }

//    public void updateSignalUI(Context context, int level, TextView tv_net_signal, ImageView iv_signal) {
//        netLevel = level;
//        boolean isWifi = "WIFI".equalsIgnoreCase(curNetTypeName);
//        switch (netLevel) {
//            case 0:
//                displaySignal(context, tv_net_signal, iv_signal, R.string.signal_null, isWifi ? R.mipmap.wifi_no : R.mipmap.img_signal_0);
//                break;
//            case 1:
//                displaySignal(context, tv_net_signal, iv_signal, R.string.signal_weak, isWifi ? R.mipmap.wifi_1 : R.mipmap.img_signal_1);
//                break;
//            case 2:
//                displaySignal(context, tv_net_signal, iv_signal, R.string.signal_weak, isWifi ? R.mipmap.wifi_2 : R.mipmap.img_signal_2);
//            case 3:
//                displaySignal(context, tv_net_signal, iv_signal, R.string.signal_weak, isWifi ? R.mipmap.wifi_3 : R.mipmap.img_signal_3);
//                break;
//            case 4:
//                displaySignal(context, tv_net_signal, iv_signal, R.string.signal_strong, isWifi ? R.mipmap.wifi_4 : R.mipmap.img_signal_4);
//                break;
//            case 5:
//                displaySignal(context, tv_net_signal, iv_signal, R.string.signal_strong, isWifi ? R.mipmap.wifi_4 : R.mipmap.img_signal_5);
//                break;
//            default:
//                break;
//        }
//    }

    private void displaySignal(Context context, TextView tv_net_signal, ImageView iv_signal, int hintResId, int imgResId) {
        if (tv_net_signal != null) tv_net_signal.setText(hintResId);
        if (iv_signal != null)
            iv_signal.setImageDrawable(ContextCompat.getDrawable(context, imgResId));
    }

    public void unInit(Context context) {
        if (context != null && networkConnectChangedReceiver != null) {
            context.unregisterReceiver(networkConnectChangedReceiver);
        }
    }
}
