package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class DeviceInfoUtils {

    // 获取当前时区
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID(); // 返回时区ID（如 "Asia/Shanghai"）
    }

    // 获取设备IP地址（返回第一个有效的IPv4地址）
    public static String getDeviceIpAddress(Context context) {
        // 1. 检查网络连接
        if (!isNetworkConnected(context)) {
            return "No Network Connection";
        }

        // 2. 遍历网络接口获取IP
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                // 跳过非活动接口和虚拟接口
                if (!intf.isUp() || intf.isLoopback() || intf.isVirtual()) continue;

                // 3. 获取接口下的IP地址
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (addr.isLoopbackAddress()) continue;

                    String ip = addr.getHostAddress();
                    // 优先返回IPv4地址
                    if (ip.indexOf(':') < 0) {
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("IP_ERROR", "getDeviceIpAddress: " + e.getMessage());
        }
        return "IP Not Found";
    }

    // 检查网络连接状态
    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }
}